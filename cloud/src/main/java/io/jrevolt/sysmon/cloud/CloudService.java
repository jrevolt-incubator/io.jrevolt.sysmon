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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

	public void startAll(int level) {
		Map<String, String> tagfilter = cfg.getTagFilter();
		List<CloudVM> vms = api.listVirtualMachines().getVirtualmachine().stream()
//				.filter(vm -> vm.containsTag("ENV"))
//				.filter(vm -> vm.containsTag("fqdn"))
				.filter(vm -> tagfilter.keySet().stream()
						.map(tag -> vm.containsTag(tag) && vm.getTag(tag, "").matches(tagfilter.get(tag)))
						.filter(b -> b.equals(false))
						.distinct().findFirst().orElse(true))
				.map(CloudVM::new)
				.filter(vm -> vm.getStartLevel() <= level)
				.collect(Collectors.toList());

		// get unique levels
		List<Integer> levels = vms.stream()
				.map(CloudVM::getStartLevel)
				.distinct().sorted()
				.collect(Collectors.toList());

		LOG.debug("Found {} distinct start levels: {}", levels.size(), levels);

		levels.stream().forEach(l -> startLevel(vms, l));

	}

	interface Action {
		void run(List<CloudVM> vms, int level);
	}

	public void doAll(Function<CloudVM, Integer> levelProvider,
							Comparator<Integer> levelSorter,
							Predicate<CloudVM> levelFilter,
							Action action) {
		Map<String, String> tagfilter = cfg.getTagFilter();
		List<CloudVM> vms = api.listVirtualMachines().getVirtualmachine().stream()
//				.filter(vm -> vm.containsTag("ENV"))
//				.filter(vm -> vm.containsTag("fqdn"))
				.filter(vm -> tagfilter.keySet().stream()
						.map(tag -> vm.containsTag(tag) && vm.getTag(tag, "").matches(tagfilter.get(tag)))
						.filter(b -> b.equals(false))
						.distinct().findFirst().orElse(true))
				.map(CloudVM::new)
				.filter(levelFilter::test)
				.collect(Collectors.toList());

		// get unique levels
		List<Integer> levels = vms.stream()
				.map(levelProvider)
				.distinct()
				.sorted(levelSorter)
				.collect(Collectors.toList());

		LOG.debug("Found {} distinct levels: {}", levels.size(), levels);

		levels.stream().forEach(level -> {
			List<CloudVM> filtered = vms.stream()
					.filter(vm->vm.getStartLevel() == level)
					.collect(Collectors.toList());
			action.run(filtered, level);
		});
	}

	private void startLevel(List<CloudVM> vms, Integer level) {

		LOG.info("Starting level {}", level);

		doLevel(vms, level,
				  vm->vm.getStartLevel()==level,
				  vm->api.startVirtualMachine(vm.getId()).getJobid());

	}

	private void doLevel(List<CloudVM> vms, Integer level,
								Predicate<CloudVM> levelFilter,
								Function<CloudVM, String> action) {
		Set<String> jobs = new CopyOnWriteArraySet<>();
		vms.stream()
				.filter(levelFilter::test)
				.parallel()
				.forEach(vm -> jobs.add(action.apply(vm)));
		if (jobs.isEmpty()) {
			LOG.debug("No VMs for level {}", level);
			return;
		} else {
			LOG.info("Processing {} VMs.", jobs.size());
		}
		int total = jobs.size();
		while (!jobs.isEmpty()) {
			LOG.debug("Waiting for level {} ({} of {} VMs).", level, jobs.size(), total);
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
