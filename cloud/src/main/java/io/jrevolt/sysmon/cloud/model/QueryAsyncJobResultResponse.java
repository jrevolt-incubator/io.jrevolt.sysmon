package io.jrevolt.sysmon.cloud.model;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class QueryAsyncJobResultResponse extends ApiObject {

	String jobid;
	String cmd;
	String jobinstanceid;
	String jobprocstatus;
	String jobresultcode;
	String jobresulttype;
	int jobstatus;
	JobResult jobresult;

	public String getJobid() {
		return jobid;
	}

	public void setJobid(String jobid) {
		this.jobid = jobid;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getJobinstanceid() {
		return jobinstanceid;
	}

	public void setJobinstanceid(String jobinstanceid) {
		this.jobinstanceid = jobinstanceid;
	}

	public String getJobprocstatus() {
		return jobprocstatus;
	}

	public void setJobprocstatus(String jobprocstatus) {
		this.jobprocstatus = jobprocstatus;
	}

	public String getJobresultcode() {
		return jobresultcode;
	}

	public void setJobresultcode(String jobresultcode) {
		this.jobresultcode = jobresultcode;
	}

	public String getJobresulttype() {
		return jobresulttype;
	}

	public void setJobresulttype(String jobresulttype) {
		this.jobresulttype = jobresulttype;
	}

	public int getJobstatus() {
		return jobstatus;
	}

	public void setJobstatus(int jobstatus) {
		this.jobstatus = jobstatus;
	}

	public JobResult getJobresult() {
		return jobresult;
	}

	public void setJobresult(JobResult jobresult) {
		this.jobresult = jobresult;
	}
}
