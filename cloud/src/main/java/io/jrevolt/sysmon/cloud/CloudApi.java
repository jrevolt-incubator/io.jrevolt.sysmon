package io.jrevolt.sysmon.cloud;

import io.jrevolt.sysmon.cloud.model.ListTagsResponse;
import io.jrevolt.sysmon.cloud.model.ListVirtualMachinesResponse;
import io.jrevolt.sysmon.cloud.model.QueryAsyncJobResultResponse;
import io.jrevolt.sysmon.cloud.model.RebootVirtualMachineResponse;
import io.jrevolt.sysmon.cloud.model.StartVirtualMachineResponse;
import io.jrevolt.sysmon.cloud.model.StopVirtualMachineResponse;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public interface CloudApi {

	@Cached
	ListTagsResponse listTags(String key);

	@Cached
	ListVirtualMachinesResponse listVirtualMachines(boolean listall);

	@Cached
	ListVirtualMachinesResponse listVirtualMachines(String id, String name);

	//ListVirtualMachinesResponse listVirtualMachines(Tag ... tags);

	@DryRun
	StartVirtualMachineResponse startVirtualMachine(String id);

	@DryRun
	StopVirtualMachineResponse stopVirtualMachine(String id);

	@DryRun
	RebootVirtualMachineResponse rebootVirtualMachine(String id);

	QueryAsyncJobResultResponse queryAsyncJobResult(String jobid);

}
