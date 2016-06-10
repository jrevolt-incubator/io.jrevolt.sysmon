package io.jrevolt.sysmon.cloud;

import io.jrevolt.sysmon.cloud.model.QueryAsyncJobResultResponse;
import io.jrevolt.sysmon.cloud.model.VirtualMachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static io.jrevolt.sysmon.cloud.Utils.interruptible;
import static io.jrevolt.sysmon.common.Utils.with;
import static java.lang.Math.negateExact;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Service
public class CloudService {

	static private final Logger LOG = LoggerFactory.getLogger(CloudService.class);

	@Autowired
	CloudCfg cfg;

	@Autowired
	CloudApi api;

	///

	Predicate<VirtualMachine> nofilter = vm -> true;

	Predicate<VirtualMachine> vmfilter = vm -> {
		Map<String, String> tagfilter = cfg.getTagFilter();
		return tagfilter.keySet().stream()
				.map(tag -> vm.containsTag(tag) && vm.getTag(tag, "").matches(tagfilter.get(tag)))
				.filter(match->!match)
				.distinct().findFirst().orElse(true);
	};

	Comparator<CloudVM> vmsorter = (vm1,vm2) ->
			cfg.isSortByHostName() ? Optional.ofNullable(vm1.getHostname()).orElse(vm1.getName()).toLowerCase().compareTo(
					Optional.ofNullable(vm1.getHostname()).orElse(vm2.getName()).toLowerCase())
			: cfg.isSortByStartLevel() ? Optional.ofNullable(vm1.getStartLevel()).orElse(Integer.MAX_VALUE).compareTo(
					Optional.ofNullable(vm2.getStartLevel()).orElse(Integer.MAX_VALUE))
			: 0;

	// FIXME QDH: reversed sorting due to invalid metadata in cloud tags
	Comparator<Integer> stopSorting = Integer::compareTo;
	Comparator<Integer> startSorting = (l1, l2)-> negateExact(stopSorting.compare(l1, l2));

	///

	interface Action {
		void run(List<CloudVM> vms);
	}

	///

	public void startAll(int level) {
		doAll(
				startSorting,
				(vm) -> vm.getStartLevel() <= level,
				(vms) -> doLevel(vms, vm -> api.startVirtualMachine(vm.getId()).getJobid(), !cfg.isSkipStartWait())
		);
	}

	public void stopAll(int level) {
		doAll(
				stopSorting,
				(vm) -> vm.getStartLevel() >= level,
				(vms) -> doLevel(vms, vm->api.stopVirtualMachine(vm.getId()).getJobid(), false)
		);
	}

	public void rebootVM(String vmid) {
		Set<String> jobs = new CopyOnWriteArraySet<>();
		api.listVirtualMachines(true).getVirtualmachine().stream()
				.map(CloudVM::new)
				.filter(vm-> vmid.equals(vm.getId()) || vmid.equals(vm.getHostname()))
				.forEach(vm-> jobs.add(api.rebootVirtualMachine(vm.getId()).getJobid()));
		interruptible(()->waitForJobs(jobs, null/*FIXME*/));
	}

	public void listVMs() {
		List<VirtualMachine> vms = api.listVirtualMachines(true).getVirtualmachine();
		List<CloudVM> filtered = vms.stream()
				.filter(cfg.isShowFilteredOnly() ? vmfilter : nofilter)
				.map(CloudVM::new)
				.sorted(vmsorter)
				.collect(Collectors.toList());
		filtered.forEach(vm->{
					System.out.printf(
							"id:%-40s name:%-20s hostname:%24s environment:%-8s startLevel:%4d startWait:%4d state:%-8s mac:%s%n",
							vm.getId(), vm.getName(),
							vm.getHostname(), vm.getEnvironment(), vm.getStartLevel(), vm.getStartWait(),
							vm.getVirtualMachine().getState(), vm.getMacAddress()
					);
				});
		LOG.info("Listed {} of {} VMs", filtered.size(), vms.size());
	}

	public void listTags() {
		api.listVirtualMachines(true).getVirtualmachine().forEach(vm->{
			System.out.printf("%-20s %s%n", vm.getDisplayname(), vm.getTags());
		});
	}

	///

	protected void doAll(Comparator<Integer> levelSorter,
							Predicate<CloudVM> levelFilter,
							Action action) {
		List<CloudVM> vms = api.listVirtualMachines(true).getVirtualmachine().stream()
				.filter(vmfilter)
				.map(CloudVM::new)
				.filter(levelFilter::test)
				.collect(Collectors.toList());
		LOG.debug("Processing {} VMs", vms.size());

		// get unique levels
		List<Integer> levels = vms.stream()
				.map(CloudVM::getStartLevel)
				.distinct()
				.sorted(levelSorter)
				.collect(Collectors.toList());

		LOG.debug("Found {} distinct levels: {}", levels.size(), levels);

		levels.stream().forEach(level -> {
			List<CloudVM> filtered = vms.stream()
					.filter(vm -> vm.getStartLevel().equals(level))
					.collect(Collectors.toList());
			action.run(filtered);
		});
	}

	protected void doLevel(List<CloudVM> vms, Function<CloudVM, String> action, boolean applyStartWait) {
		Integer level = vms.stream().findFirst().map(CloudVM::getStartLevel).get();
		Integer startWait = applyStartWait
				? vms.stream().map(CloudVM::getStartWait).max(Integer::compareTo).orElse(30)
				: null;
		Set<String> jobs = new CopyOnWriteArraySet<>();
		vms.stream()
				.parallel()
				.forEach(vm -> jobs.add(action.apply(vm)));
		jobs.remove(null);

		interruptible(()->{
			LOG.info("Level {}: Processing {} VMs: {}",
						level,
						vms.size(),
						vms.stream().map(CloudVM::getHostname).collect(Collectors.toList()));
			waitForJobs(jobs, vms);

			if (applyStartWait) {
				LOG.info("Level {}: All {} jobs completed. Waiting {} seconds to allow level services to fully initialize",
							level, jobs.size(), startWait);
				Thread.sleep(TimeUnit.SECONDS.toMillis(startWait));
			}
		});
	}

	private void waitForJobs(Set<String> jobs, List<CloudVM> vms) throws InterruptedException {
		LOG.debug("Waiting for {} jobs", jobs.size());
		Set<String> inprogress = vms.stream().map(CloudVM::getHostname).collect(Collectors.toSet());
		Set<String> completed = new CopyOnWriteArraySet<>();
		while (!jobs.isEmpty()) {
			jobs.parallelStream().forEach(id->{
				QueryAsyncJobResultResponse result = api.queryAsyncJobResult(id);
				if (result.getJobstatus() == 1) {
					CloudVM vm = new CloudVM(result.getJobresult().getVirtualmachine());
					jobs.remove(id);
					inprogress.remove(vm.getHostname());
					completed.add(vm.getHostname());
					with(System.console(), console -> console.printf("\r\033[K"));
					LOG.info("Completed: {}", vm.getHostname());
				}
			});
			with(System.console(), console -> {
				console.printf("\rWaiting for %s jobs %s. Completed: %s", inprogress.size(), inprogress, completed);
			});
			if (!jobs.isEmpty()) { Thread.sleep(2500); }
		}
		with(System.console(), console -> console.printf("\r\033[K"));
	}



}
