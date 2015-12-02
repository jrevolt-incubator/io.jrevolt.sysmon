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
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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


	public List<VirtualMachine> listVMs(String env) {
		return api.listVirtualMachines().getVirtualmachine().stream()
				.filter(vm -> env == null || env.equals(vm.getTag("ENV")))
				.collect(Collectors.toList());
	}

	public List<VirtualMachine> listTaggedVMs() {
		return api.listVirtualMachines().getVirtualmachine().stream()
				.filter(vm -> vm.containsTag("fqdn"))
				.collect(Collectors.toList());
	}

	Comparator<Integer> startSorting = Integer::compareTo;
	Comparator<Integer> stopSorting = (l1,l2)-> negateExact(startSorting.compare(l2,l1));

	interface Action {
		void run(List<CloudVM> vms);
	}

	public void startAll(int level) {
		doAll(
				startSorting,
				(vm) -> vm.getStartLevel() <= level,
				(vms) -> doLevel(vms, vm -> api.startVirtualMachine(vm.getId()).getJobid())
		);
	}

	public void stopAll(int level) {
		doAll(
				stopSorting,
				(vm) -> vm.getStartLevel() >= level,
				(vms) -> doLevel(vms, vm->api.stopVirtualMachine(vm.getId()).getJobid())
		);
	}

	public void rebootVM(String vmid) {
		Set<String> jobs = new CopyOnWriteArraySet<>();
		api.listVirtualMachines().getVirtualmachine().stream()
				.map(CloudVM::new)
				.filter(vm-> vmid.equals(vm.getId()) || vmid.equals(vm.getHostname()))
				.forEach(vm-> jobs.add(api.rebootVirtualMachine(vm.getId()).getJobid()));
		waitForJobs(jobs);
	}

	public void listVMs() {
		api.listVirtualMachines().getVirtualmachine().stream()
				.map(CloudVM::new)
				.forEach(vm->LOG.info("{} ip:{} mac:{}", vm.getHostname(), vm.getIpAddress(), vm.getMacAddress()));
	}

	///

	protected void doAll(Comparator<Integer> levelSorter,
							Predicate<CloudVM> levelFilter,
							Action action) {
		Map<String, String> tagfilter = cfg.getTagFilter();
		List<CloudVM> vms = api.listVirtualMachines().getVirtualmachine().stream()
				.filter(vm -> tagfilter.keySet().stream()
						.map(tag -> vm.containsTag(tag) && vm.getTag(tag, "").matches(tagfilter.get(tag)))
						.filter(b -> b.equals(false))
						.distinct().findFirst().orElse(true))
				.map(CloudVM::new)
				.filter(levelFilter::test)
				.collect(Collectors.toList());

		// get unique levels
		List<Integer> levels = vms.stream()
				.map(CloudVM::getStartLevel)
				.distinct()
				.sorted(levelSorter)
				.collect(Collectors.toList());

		LOG.debug("Found {} distinct levels: {}", levels.size(), levels);

		levels.stream().forEach(level -> {
			List<CloudVM> filtered = vms.stream()
					.filter(vm->vm.getStartLevel() == level)
					.collect(Collectors.toList());
			action.run(filtered);
		});
	}

	protected void doLevel(List<CloudVM> vms, Function<CloudVM, String> action) {
		Integer level = vms.stream().map(CloudVM::getStartLevel).findFirst().orElse(null);
		Set<String> jobs = new CopyOnWriteArraySet<>();
		vms.stream()
				.parallel()
				.forEach(vm -> jobs.add(action.apply(vm)));
		LOG.info("Processing {} VMs: {}",
					jobs.size(),
					vms.stream().map(CloudVM::getHostname).collect(Collectors.toList()));
		int total = jobs.size();
		waitForJobs(jobs);
	}

	private void waitForJobs(Set<String> jobs) {
		while (!jobs.isEmpty()) {
			LOG.debug("Waiting for {} jobs", jobs.size());
			jobs.parallelStream().forEach(id->{
				QueryAsyncJobResultResponse result = api.queryAsyncJobResult(id);
				if (result.getJobstatus() == 1) {
					jobs.remove(id);
					CloudVM vm = new CloudVM(result.getJobresult().getVirtualmachine());
					LOG.info("Completed: {} {}", vm.getHostname(), vm.getVirtualMachine().getNic());
				}
			});
			if (!jobs.isEmpty()) try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
	}


}
