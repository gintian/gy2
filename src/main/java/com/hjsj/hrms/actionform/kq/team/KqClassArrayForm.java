package com.hjsj.hrms.actionform.kq.team;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class KqClassArrayForm extends FrameForm {
	
    private ArrayList classlist=new ArrayList();//基本班次
    private String rest_postpone;//公休日顺延
    private String feast_postpone;//节假日顺延
    private String selected_class;
    private String start_date;
    private String end_date;
    private String a_code;
    private String nbase;
    private ArrayList cyclelist;//周期班次
    private String usedflag;
    private String shift_class_id;
    /**选中的字段名数组*/
    private String left_fields[];
    private ArrayList left_list=new ArrayList();
    private ArrayList right_list=new ArrayList();
    /**选中的字段名数组*/
    private String right_fields[];  
    private String cycle_id;
    private ArrayList class_list_vo=new ArrayList();
    private PaginationForm recordListForm=new PaginationForm();
    private String cycle_name;
    private String cycle_flag;
    private String object_flag;
    private String sql_str;
    private String column;
    private String where_str;
    private String selected_object;
    private String relief_id[];
    private String relief_day[];
    private String cycle_ids[];
    private String cycle_days[];
	private String session_data;
	private ArrayList kq_list=new ArrayList();//人员库   
    private String select_pre;
    private String take_turns;//轮番
    private String group_syn="";
    private String syc_type="1";
    private String grnbase;
    private String syn_uro;
    private String isPost;
	public String getSyn_uro() {
		return syn_uro;
	}
	public void setSyn_uro(String syn_uro) {
		this.syn_uro = syn_uro;
	}
	public String getSyc_type() {
		return syc_type;
	}
	public void setSyc_type(String syc_type) {
		this.syc_type = syc_type;
	}
	public String getTake_turns() {
		return take_turns;
	}
	public void setTake_turns(String take_turns) {
		this.take_turns = take_turns;
	}
	public String getSession_data() {
		return session_data;
	}
	public void setSession_data(String session_data) {
		this.session_data = session_data;
	}
	public String[] getRelief_day() {
		return relief_day;
	}
	public void setRelief_day(String[] relief_day) {
		this.relief_day = relief_day;
	}
	public String getCycle_id() {
		return cycle_id;
	}
	public void setCycle_id(String cycle_id) {
		this.cycle_id = cycle_id;
	}
	@Override
    public void outPutFormHM()
	{
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setClasslist((ArrayList)this.getFormHM().get("classlist"));
		this.setStart_date((String)this.getFormHM().get("start_date"));
		this.setEnd_date((String)this.getFormHM().get("end_date"));
		this.setRest_postpone((String)this.getFormHM().get("rest_postpone"));
		this.setFeast_postpone((String)this.getFormHM().get("feast_postpone"));
		this.setNbase((String)this.getFormHM().get("nbase"));
		this.setCyclelist((ArrayList)this.getFormHM().get("cyclelist"));
		this.setCycle_id((String)this.getFormHM().get("cycle_id"));
		this.getRecordListForm().setList((ArrayList)this.getFormHM().get("class_list_vo"));
		this.setCycle_name((String)this.getFormHM().get("cycle_name"));
		this.setObject_flag((String)this.getFormHM().get("object_flag"));
		this.setSql_str((String)this.getFormHM().get("sql_str"));
		this.setColumn((String)this.getFormHM().get("column"));
		this.setWhere_str((String)this.getFormHM().get("where_str"));
		this.setRelief_id((String[])this.getFormHM().get("relief_id"));
		this.setRelief_day((String[])this.getFormHM().get("relief_day"));
		this.setLeft_list((ArrayList)this.getFormHM().get("left_list"));
		this.setRight_list((ArrayList)this.getFormHM().get("right_list"));
		this.setSession_data((String)this.getFormHM().get("session_data"));
		this.setKq_list((ArrayList)this.getFormHM().get("kq_list"));
	    this.setSelect_pre((String)this.getFormHM().get("select_pre"));
	    this.setTake_turns((String)this.getFormHM().get("take_turns"));
	    this.setGroup_syn((String)this.getFormHM().get("group_syn"));
	    this.setSyc_type((String)this.getFormHM().get("syc_type"));
	    this.setGrnbase((String)this.getFormHM().get("grnbase"));
	    this.setSyn_uro((String)this.getFormHM().get("syn_uro"));
	    this.setIsPost((String)this.getFormHM().get("isPost"));
	}
	@Override
    public void inPutTransHM()
    {
		this.getFormHM().put("cycle_name",this.getCycle_name());
		this.getFormHM().put("cycle_flag",this.getCycle_flag());
		this.getFormHM().put("rest_postpone",this.getRest_postpone());
		this.getFormHM().put("feast_postpone",this.getFeast_postpone());
		this.getFormHM().put("selected_class",this.getSelected_class());
		this.getFormHM().put("start_date",this.getStart_date());
		this.getFormHM().put("end_date",this.getEnd_date());
		this.getFormHM().put("a_code",this.getA_code());
		this.getFormHM().put("nbase",this.getNbase());
		this.getFormHM().put("cycle_id",this.getCycle_id());
		this.getFormHM().put("selectedlist",(ArrayList)this.getRecordListForm().getSelectedList());
		this.getFormHM().put("object_flag",this.getObject_flag());
		this.getFormHM().put("relief_id",this.getRelief_id());
		this.getFormHM().put("relief_day",this.getRelief_day());
		this.getFormHM().put("selected_object",this.getSelected_object());
		this.getFormHM().put("left_fields",this.getLeft_fields());
		this.getFormHM().put("right_fields",this.getRight_fields());
		this.getFormHM().put("left_list",this.getLeft_list());
		this.getFormHM().put("right_list",this.getRight_list());
		this.getFormHM().put("session_data",this.getSession_data());
		this.getFormHM().put("cycle_ids",this.getCycle_ids());
		this.getFormHM().put("cycle_days",this.getCycle_days());
		this.getFormHM().put("cycle_id",this.cycle_id);
		this.getFormHM().put("select_pre",this.getSelect_pre());
		this.getFormHM().put("take_turns", this.getTake_turns());
		this.getFormHM().put("group_syn", this.getGroup_syn());
		this.getFormHM().put("syc_type", this.getSyc_type());
		this.getFormHM().put("grnbase",this.getGrnbase());
    }
	public String getA_code() {
		return a_code;
	}
	public void setA_code(String a_code) {
		this.a_code = a_code;
	}
	public ArrayList getClasslist() {
		return classlist;
	}
	public void setClasslist(ArrayList classlist) {
		this.classlist = classlist;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public String getFeast_postpone() {
		return feast_postpone;
	}
	public void setFeast_postpone(String feast_postpone) {
		this.feast_postpone = feast_postpone;
	}
	public String getRest_postpone() {
		return rest_postpone;
	}
	public void setRest_postpone(String rest_postpone) {
		this.rest_postpone = rest_postpone;
	}
	public String getSelected_class() {
		return selected_class;
	}
	public void setSelected_class(String selected_class) {
		this.selected_class = selected_class;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getNbase() {
		return nbase;
	}
	public void setNbase(String nbase) {
		this.nbase = nbase;
	}
	public ArrayList getCyclelist() {
		return cyclelist;
	}
	public void setCyclelist(ArrayList cyclelist) {
		this.cyclelist = cyclelist;
	}
	public String getUsedflag() {
        return usedflag;
    }
    public void setUsedflag(String usedflag) {
        this.usedflag = usedflag;
    }
	public String[] getLeft_fields() {
		return left_fields;
	}
	public void setLeft_fields(String[] left_fields) {
		this.left_fields = left_fields;
	}
	public String[] getRight_fields() {
		return right_fields;
	}
	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}
	public String getShift_class_id() {
		return shift_class_id;
	}
	public void setShift_class_id(String shift_class_id) {
		this.shift_class_id = shift_class_id;
	}
	public ArrayList getClass_list_vo() {
		return class_list_vo;
	}
	public void setClass_list_vo(ArrayList class_list_vo) {
		this.class_list_vo = class_list_vo;
	}
	public PaginationForm getRecordListForm() {
		return recordListForm;
	}
	public void setRecordListForm(PaginationForm recordListForm) {
		this.recordListForm = recordListForm;
	}
	public String getCycle_flag() {
		return cycle_flag;
	}
	public void setCycle_flag(String cycle_flag) {
		this.cycle_flag = cycle_flag;
	}
	public String getCycle_name() {
		return cycle_name;
	}
	public void setCycle_name(String cycle_name) {
		this.cycle_name = cycle_name;
	}
	public String getObject_flag() {
		return object_flag;
	}
	public void setObject_flag(String object_flag) {
		this.object_flag = object_flag;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getSql_str() {
		return sql_str;
	}
	public void setSql_str(String sql_str) {
		this.sql_str = sql_str;
	}
	public String getWhere_str() {
		return where_str;
	}
	public void setWhere_str(String where_str) {
		this.where_str = where_str;
	}
	public String getSelected_object() {
		return selected_object;
	}
	public void setSelected_object(String selected_object) {
		this.selected_object = selected_object;
	}
	public String[] getRelief_id() {
		return relief_id;
	}
	public void setRelief_id(String[] relief_id) {
		this.relief_id = relief_id;
	}
	public ArrayList getLeft_list() {
		return left_list;
	}
	public void setLeft_list(ArrayList left_list) {
		if(left_list==null)
			left_list=new ArrayList();
		this.left_list = left_list;
	}
	public ArrayList getRight_list() {
		return right_list;
	}
	public void setRight_list(ArrayList right_list) {
		if(right_list==null)
			right_list=new ArrayList();
		this.right_list = right_list;
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
	    if("/kq/team/array/cycle_shift_employee".equals(arg0.getPath())&&arg1.getParameter("b_employee")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();
	    }
	    if("/kq/team/array/cycle_shift_group".equals(arg0.getPath())&&arg1.getParameter("b_group")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();
	    }
	    //个人排班
	    if("/kq/team/array/cycle_array_class".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            if(this.recordListForm.getPagination()!=null)
                this.recordListForm.getPagination().gotoPage(1);
        }
	    
	    arg1.setAttribute("targetWindow", "1");
	    return super.validate(arg0, arg1);
	}
	
	public String[] getCycle_days() {
		return cycle_days;
	}
	public void setCycle_days(String[] cycle_days) {
		this.cycle_days = cycle_days;
	}
	public String[] getCycle_ids() {
		return cycle_ids;
	}
	public void setCycle_ids(String[] cycle_ids) {
		this.cycle_ids = cycle_ids;
	}
	public ArrayList getKq_list() {
		return kq_list;
	}
	public void setKq_list(ArrayList kq_list) {
		this.kq_list = kq_list;
	}
	public String getSelect_pre() {
		return select_pre;
	}
	public void setSelect_pre(String select_pre) {
		this.select_pre = select_pre;
	}
	public String getGroup_syn() {
		return group_syn;
	}
	public void setGroup_syn(String group_syn) {
		this.group_syn = group_syn;
	}
	public String getGrnbase() {
		return grnbase;
	}
	public void setGrnbase(String grnbase) {
		this.grnbase = grnbase;
	}
	public String getIsPost() {
		return isPost;
	}
	public void setIsPost(String isPost) {
		this.isPost = isPost;
	}
}
