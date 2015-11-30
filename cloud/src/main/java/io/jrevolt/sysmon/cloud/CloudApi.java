package io.jrevolt.sysmon.cloud;

import io.jrevolt.sysmon.cloud.model.ListHostsResponse;
import io.jrevolt.sysmon.cloud.model.ListTagsResponse;
import io.jrevolt.sysmon.cloud.model.ListVirtualMachinesResponse;
import io.jrevolt.sysmon.cloud.model.Tag;

import java.util.List;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public interface CloudApi {

	ListTagsResponse listTags(String key);

	ListVirtualMachinesResponse listVirtualMachines();

	ListVirtualMachinesResponse listVirtualMachines(String id);

	ListVirtualMachinesResponse listVirtualMachines(Tag ... tags);

}
