package io.jrevolt.sysmon.cloud;

import io.jrevolt.sysmon.cloud.model.ListHostsResponse;
import io.jrevolt.sysmon.cloud.model.ListTagsResponse;
import io.jrevolt.sysmon.cloud.model.ListVirtualMachinesResponse;
import io.jrevolt.sysmon.cloud.model.QueryAsyncJobResultResponse;
import io.jrevolt.sysmon.cloud.model.StartVirtualMachineResponse;
import io.jrevolt.sysmon.cloud.model.StopVirtualMachineResponse;
import io.jrevolt.sysmon.cloud.model.Tag;

import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public interface CloudApi {

	ListTagsResponse listTags(String key);

	ListVirtualMachinesResponse listVirtualMachines();

	ListVirtualMachinesResponse listVirtualMachines(String id, String name);

	//ListVirtualMachinesResponse listVirtualMachines(Tag ... tags);

	StartVirtualMachineResponse startVirtualMachine(String id);

	StopVirtualMachineResponse stopVirtualMachine(String id);

	QueryAsyncJobResultResponse queryAsyncJobResult(String jobid);

}
