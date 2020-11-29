package com.hjsj.hrms.actionform.hire.zp_options.positionstat;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class PositionStatForm extends FrameForm{
	/**查询起始时间*/
	private String starttime;
	/**查询结束时间*/
    private String endtime;
    /**记录列表*/
    private ArrayList recordList = new ArrayList();
    private PaginationForm recordListform=new PaginationForm();
    /**职位名称*/
    private String zp_pos_name;
    /**分页sql语句*/
    private String select_sql;
    private String where_sql;
    private String order_sql;
    private String columns;
    private String count;
    /**分类统计条件列表*/
    private ArrayList condlist = new ArrayList();
    private String schoolPosition;
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("returnflag", this.getReturnflag());
		this.getFormHM().put("starttime",this.getStarttime());
		this.getFormHM().put("endtime",this.getEndtime());
		this.getFormHM().put("selectedList",this.getRecordListform().getSelectedList());
	}

	@Override
    public void outPutFormHM() {
		this.setSchoolPosition((String)this.getFormHM().get("schoolPosition"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setEndtime((String)this.getFormHM().get("endtime"));
		this.setStarttime((String)this.getFormHM().get("starttime"));
		//this.setRecordList((ArrayList)this.getFormHM().get("recordList"));
		this.getRecordListform().setList((ArrayList)this.getFormHM().get("recordList"));
		this.setOrder_sql((String)this.getFormHM().get("order_sql"));
		this.setSelect_sql((String)this.getFormHM().get("select_sql"));
		this.setWhere_sql((String)this.getFormHM().get("where_sql"));
		this.setZp_pos_name((String)this.getFormHM().get("zp_pos_name"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setCount((String)this.getFormHM().get("count"));
		this.setCondlist((ArrayList)this.getFormHM().get("condlist"));
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/hire/zp_options/stat/positionstat/person_wish".equals(arg0.getPath())&&arg1.getParameter("b_detail")!=null)
		{
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		if(arg1.getParameter("b_query")!=null&& "link".equals(arg1.getParameter("b_query")))
			if(this.getRecordListform()!=null)
				this.getRecordListform().getPagination().firstPage();
		return super.validate(arg0, arg1);
	}
	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public PaginationForm getRecordListform() {
		return recordListform;
	}

	public void setRecordListform(PaginationForm recordListform) {
		this.recordListform = recordListform;
	}

	public String getOrder_sql() {
		return order_sql;
	}

	public void setOrder_sql(String order_sql) {
		this.order_sql = order_sql;
	}

	public ArrayList getRecordList() {
		return recordList;
	}

	public void setRecordList(ArrayList recordList) {
		this.recordList = recordList;
	}

	public String getSelect_sql() {
		return select_sql;
	}

	public void setSelect_sql(String select_sql) {
		this.select_sql = select_sql;
	}

	public String getWhere_sql() {
		return where_sql;
	}

	public void setWhere_sql(String where_sql) {
		this.where_sql = where_sql;
	}

	public String getZp_pos_name() {
		return zp_pos_name;
	}

	public void setZp_pos_name(String zp_pos_name) {
		this.zp_pos_name = zp_pos_name;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public ArrayList getCondlist() {
		return condlist;
	}

	public void setCondlist(ArrayList condlist) {
		this.condlist = condlist;
	}

	public String getSchoolPosition() {
		return schoolPosition;
	}

	public void setSchoolPosition(String schoolPosition) {
		this.schoolPosition = schoolPosition;
	}

}
