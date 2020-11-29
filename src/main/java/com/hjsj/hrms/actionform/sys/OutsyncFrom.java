package com.hjsj.hrms.actionform.sys;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class OutsyncFrom extends FrameForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String sql_str;

	private String sql_where;

	private String table;

	private String columns;

	private String order_by;

	private String distinct;
	
	private String max_time;
	
	private RecordVo vo;
	
	/**范围过滤*/
	private ArrayList setlist = new ArrayList();
	
	private ArrayList htmllist = new ArrayList();
	
	private String type;
	
	private String like;
	
	private String other_param;
	
	private String jobId;

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("record", this.getVo());
		this.getFormHM().put("type", this.getType());
		this.getFormHM().put("like", this.getLike());
		this.getFormHM().put("other_param", this.getOther_param());
		this.getFormHM().put("jobId", jobId);
		if(this.getPagination()!= null)
		this.getFormHM().put("selected", this.getPagination().getSelectedList());
		
	}

	@Override
    public void outPutFormHM() {
		this.setSql_str((String) this.getFormHM().get("sql_str"));
		this.setSql_where((String) this.getFormHM().get("sql_where"));
		this.setTable((String) this.getFormHM().get("table"));
		this.setColumns((String) this.getFormHM().get("columns"));
		this.setOrder_by((String) this.getFormHM().get("order_by"));
		this.setDistinct((String) this.getFormHM().get("distinct"));
		this.setVo((RecordVo) this.getFormHM().get("record"));
		this.setMax_time((String)this.getFormHM().get("max_time"));
		this.setType((String)this.getFormHM().get("type"));
		this.setSetlist((ArrayList)this.getFormHM().get("setlist"));
		this.setOther_param((String)this.getFormHM().get("other_param"));
		this.setLike((String)this.getFormHM().get("like"));
		this.setHtmllist((ArrayList)this.getFormHM().get("htmllist"));
		this.setJobId((String) this.getFormHM().get("jobId"));
	}

	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1){
		if(this.getVo()!=null)
			this.getVo().setInt("state", 0);
	}

	public String getSql_str() {
		return sql_str;
	}

	public void setSql_str(String sql_str) {
		this.sql_str = sql_str;
	}

	public String getSql_where() {
		return sql_where;
	}

	public void setSql_where(String sql_where) {
		this.sql_where = sql_where;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getOrder_by() {
		return order_by;
	}

	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}

	public String getDistinct() {
		return distinct;
	}

	public void setDistinct(String distinct) {
		this.distinct = distinct;
	}

	public RecordVo getVo() {
		return vo;
	}

	public void setVo(RecordVo vo) {
		this.vo = vo;
	}

	public String getMax_time() {
		return max_time;
	}

	public void setMax_time(String max_time) {
		this.max_time = max_time;
	}

	public ArrayList getSetlist() {
		return setlist;
	}

	public void setSetlist(ArrayList setlist) {
		this.setlist = setlist;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOther_param() {
		return other_param;
	}

	public void setOther_param(String other_param) {
		this.other_param = other_param;
	}

	public ArrayList getHtmllist() {
		return htmllist;
	}

	public void setHtmllist(ArrayList htmllist) {
		this.htmllist = htmllist;
	}

	public String getLike() {
		return like;
	}

	public void setLike(String like) {
		this.like = like;
	}
	
}
