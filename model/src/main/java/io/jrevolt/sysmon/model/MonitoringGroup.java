//package io.jrevolt.sysmon.model;
//
//import org.apache.commons.lang3.builder.ToStringBuilder;
//import org.apache.commons.lang3.builder.ToStringStyle;
//
//import java.util.AbstractList;
//import java.util.AbstractMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
///**
// * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
// */
//public class MonitoringGroup {
//
//	private String name;
//
//	public MonitoringGroup() {
//	}
//
//	public MonitoringGroup(String name) {
//		this.name = name;
//	}
//
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
/////
//
//
//
//	void init() {
//		//stream().forEach(g->g.setParent(this));
//	}
//
//	///
//
//
//	@Override
//	public boolean equals(Object o) {
//		if (this == o) return true;
//		if (o == null || getClass() != o.getClass()) return false;
//
//		MonitoringGroup that = (MonitoringGroup) o;
//
//		return !(name != null ? !name.equals(that.name) : that.name != null);
//
//	}
//
//	@Override
//	public int hashCode() {
//		return name != null ? name.hashCode() : 0;
//	}
//
//	@Override
//	public String toString() {
//		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
//				.append("name", name)
//				.toString();
//	}
//}
