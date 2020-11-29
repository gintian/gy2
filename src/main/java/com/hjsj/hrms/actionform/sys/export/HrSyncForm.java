package com.hjsj.hrms.actionform.sys.export;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * 
 *<p>Title:HrSyncForm.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 24, 2008</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class HrSyncForm extends FrameForm {
	/**人员视图主页*/
	private ArrayList dbnamelist;

	private String dbname;
	
	private String selectsql;
	
	private String column;
	
	private String wheresql;
	
	private String order;
	private ArrayList fieldslist;
	 /**分页*/
    private int current=1;
    private PaginationForm recordListForm=new PaginationForm();
    private int scurrent=1;
    private PaginationForm setListForm=new PaginationForm(); 
    private ArrayList sysid = new ArrayList();
    private String tacitly;
    private String hrunique_ids;
    private String orgunique_ids;
    private String postunique_ids;
	/**设置人员库*/
	private String[] dbstr = new String[0];
	
	private ArrayList dbprelist;
	
	private String dbnamestr;
	
	/**设置基本指标*/
	private ArrayList setlist;
	
	private ArrayList itemlist;
    /**选中的字段名数组*/
    private String left_fields[];
    /**选中的字段名数组*/
    private String right_fields[]; 
	
    private String fieldstr;
    
    private ArrayList sync_fieldlist;
    
    private String sync_field;
    
    private String code;
    
    private String code_value;
    
    private String codefieldstr;
    
    private String orgfieldstr;
    
    private String orgcodefieldstr;
    
    private String postfieldstr;
    private String postcodefieldstr;
    
	private String type;
	
	private String appfield;
	
	private String editname;
	
	private String emporg;
    private String sync_A01;
    private String sync_B01;
    private String sync_K01;
    private ArrayList sync_typelst=new ArrayList();
    private String onlyfieldstr;
    private String onlyfield;
    private ArrayList onlyfieldlist=new ArrayList();
    private String sync_mode;//同步方式 trigger：触发器；time_job 定时任务
    private String fail_limit;//失败次数上限
    // 姓名
    private String select_name;
    
    private String jz_field;//同步兼职 1=是
    
    private String photo;// 同步照片，1为同步，
    
    private String fieldChange;// 跟踪指标变化前后信息，1为同步
    
    private String fieldAndCode;// 普通代码类型指标翻译指标含代码
    
    private String state;//已选状态
    
    private ArrayList statelist = new ArrayList();//状态
    
    public String getFieldAndCode() {
		return fieldAndCode;
	}

	public void setFieldAndCode(String fieldAndCode) {
		this.fieldAndCode = fieldAndCode;
	}

	public String getFieldAndCodeSeq() {
		return fieldAndCodeSeq;
	}

	public void setFieldAndCodeSeq(String fieldAndCodeSeq) {
		this.fieldAndCodeSeq = fieldAndCodeSeq;
	}

	private String fieldAndCodeSeq; // 分隔符
    
	public String getFieldChange() {
		return fieldChange;
	}

	public void setFieldChange(String fieldChange) {
		this.fieldChange = fieldChange;
	}

	public ArrayList getSysid() {
		return sysid;
	}

	public void setSysid(ArrayList sysid) {
		this.sysid = sysid;
	}

	public String getTacitly() {
		return tacitly;
	}

	public void setTacitly(String tacitly) {
		this.tacitly = tacitly;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public ArrayList getStatelist() {
		return statelist;
	}

	public void setStatelist(ArrayList statelist) {
		this.statelist = statelist;
	}

	public String getHrunique_ids() {
		return hrunique_ids;
	}

	public void setHrunique_ids(String hrunique_ids) {
		this.hrunique_ids = hrunique_ids;
	}

	public String getOrgunique_ids() {
		return orgunique_ids;
	}

	public void setOrgunique_ids(String orgunique_ids) {
		this.orgunique_ids = orgunique_ids;
	}

	public String getPostunique_ids() {
		return postunique_ids;
	}

	public void setPostunique_ids(String postunique_ids) {
		this.postunique_ids = postunique_ids;
	}

	@Override
    public void inPutTransHM() {
		
		this.getFormHM().put("hrunique_ids",this.getHrunique_ids());
		this.getFormHM().put("orgunique_ids",this.getOrgunique_ids());
		this.getFormHM().put("postunique_ids",this.getPostunique_ids());
		this.getFormHM().put("statelist",this.getStatelist());
		this.getFormHM().put("state",this.getState());
		this.getFormHM().put("sysid",this.getSysid());
		this.getFormHM().put("sql",this.getDbname());
		this.getFormHM().put("tacitly",this.getTacitly());
		this.getFormHM().put("selectedlist",(ArrayList)this.getRecordListForm().getSelectedList());
		this.getFormHM().put("right_fields",this.getRight_fields());
		this.getFormHM().put("sync_field",this.getSync_field());
		this.getFormHM().put("code",this.getCode());
		this.getFormHM().put("code_value",this.getCode_value());
		this.getFormHM().put("dbname",this.getDbname());
		this.getFormHM().put("type",this.getType());
		this.getFormHM().put("appfield",this.getAppfield());
		this.getFormHM().put("selectedlist",(ArrayList)this.getSetListForm().getSelectedList());
		this.getFormHM().put("editname",this.getEditname());
		this.getFormHM().put("emporg",this.getEmporg());
		this.getFormHM().put("sync_B01", this.getSync_B01());
		this.getFormHM().put("sync_A01", this.getSync_A01());
		this.getFormHM().put("order", this.getOrder());
		this.getFormHM().put("onlyfield", this.getOnlyfield());
		this.getFormHM().put("sync_K01", this.getSync_K01());
		this.getFormHM().put("select_name", this.getSelect_name());
		this.getFormHM().put("sync_mode", this.getSync_mode());
		this.getFormHM().put("fail_limit", this.getFail_limit());
		this.getFormHM().put("jz_field", this.getJz_field());
		this.getFormHM().put("photo", this.getPhoto());
		this.getFormHM().put("fieldChange", this.getFieldChange());
		this.getFormHM().put("fieldAndCode", this.getFieldAndCode());
		this.getFormHM().put("fieldAndCodeSeq", this.getFieldAndCodeSeq());
	}
	
	@Override
    public void outPutFormHM() {
		this.setOnlyfieldstr((String)this.getFormHM().get("onlyfieldstr"));
		this.setOnlyfieldlist((ArrayList)this.getFormHM().get("onlyfieldlist"));
		this.setOnlyfield((String)this.getFormHM().get("onlyfield"));
		this.setDbnamelist((ArrayList)this.getFormHM().get("dbnamelist"));
		this.setDbname((String)this.getFormHM().get("dbname"));
		this.setSelectsql((String)this.getFormHM().get("selectsql"));
		this.setWheresql((String)this.getFormHM().get("wheresql"));
		this.setColumn((String)this.getFormHM().get("column"));
		this.getRecordListForm().getPagination().gotoPage(current);
		this.getSetListForm().getPagination().gotoPage(scurrent);
		this.setDbprelist((ArrayList)this.getFormHM().get("dbprelist"));
		this.setDbstr((String[])this.getFormHM().get("dbstr"));
		this.setDbnamestr((String)this.getFormHM().get("dbnamestr"));
		this.setTacitly((String)this.getFormHM().get("tacitly"));
		this.setSetlist((ArrayList)this.getFormHM().get("setlist"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setFieldstr((String)this.getFormHM().get("fieldstr"));
		this.setSync_fieldlist((ArrayList)this.getFormHM().get("sync_fieldlist"));
		this.setCode((String)this.getFormHM().get("code"));
		this.setCode_value((String)this.getFormHM().get("code_value"));
		this.setFieldslist((ArrayList)this.getFormHM().get("fieldslist"));
		this.setCodefieldstr((String)this.getFormHM().get("codefieldstr"));
		this.setOrgfieldstr((String)this.getFormHM().get("orgfieldstr"));
		this.setOrgcodefieldstr((String)this.getFormHM().get("orgcodefieldstr"));
		this.getSetListForm().setList((ArrayList)this.getFormHM().get("setList"));
		this.setEmporg((String)this.getFormHM().get("emporg"));
		this.setSync_A01((String)this.getFormHM().get("sync_A01"));
		this.setSync_B01((String)this.getFormHM().get("sync_B01"));
		this.setSync_K01((String)this.getFormHM().get("sync_K01"));
		this.setPostfieldstr((String)this.getFormHM().get("postfieldstr"));
		this.setPostcodefieldstr((String)this.getFormHM().get("postcodefieldstr"));
		this.setSync_typelst((ArrayList)this.getFormHM().get("sync_typelst"));
		this.setSysid((ArrayList)this.getFormHM().get("sysid"));
		this.setSync_mode((String)this.getFormHM().get("sync_mode"));
		this.setStatelist((ArrayList)this.getFormHM().get("statelist"));
		this.setState((String)this.getFormHM().get("state"));
		this.setFail_limit((String)this.getFormHM().get("fail_limit"));
		this.setJz_field((String)this.getFormHM().get("jz_field"));
		this.setPhoto((String) this.getFormHM().get("photo"));
		this.setFieldChange((String) this.getFormHM().get("fieldChange"));
		this.setFieldAndCode((String) this.getFormHM().get("fieldAndCode"));
		this.setFieldAndCodeSeq((String) this.getFormHM().get("fieldAndCodeSeq"));
		this.setHrunique_ids((String) this.getFormHM().get("hrunique_ids"));
		this.setOrgunique_ids((String) this.getFormHM().get("orgunique_ids"));
		this.setPostunique_ids((String) this.getFormHM().get("postunique_ids"));
		this.setSelect_name((String) this.getFormHM().get("select_name"));//点击机构 查询姓名框内容置空 wangb 20170830  31073
	}

	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
	   try
	   {
	    if("/sys/export/SearchEmpSync".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            if(this.getPagination()!=null)
              this.getPagination().firstPage();
        }	
	    if("/sys/export/SearchEmpSync".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
        {
        	if(this.recordListForm.getPagination()!=null)
        	 current=this.recordListForm.getPagination().getCurrent();
        }
	    if("/sys/export/SearchHrSyncSet".equals(arg0.getPath()) && arg1.getParameter("b_set")!=null)
        {
        	if(this.setListForm.getPagination()!=null)
        	 scurrent=this.setListForm.getPagination().getCurrent();
        }
	    
	    /*问题 13571  系统管理-数据交换-数据视图-设置人员指标-点击修改的时候，如果指标多余一页，
	     * 那么 我选选择第二页的指标修改保存时自动返回第一页，如果指标很多，用起来很不方便
	     * 在这里添加保存当前页  guodd 2015-11-19
	     * */
	    if("/sys/export/SearchHrSyncFiled".equals(arg0.getPath()))
        {
	    	   //有b_search说明是刚进入，定位为第一页（因为人员、岗位、单位指标都是走这里，所以每次都要定位到第一页）
	    	   if(arg1.getParameter("b_search")!=null)
	    		 scurrent = 1;
	    	   else{
	    		   //没有b_search说明是翻页或其他操作跳转，保存下当前页数，以便返回时定位页数
	       	if(this.setListForm.getPagination()!=null)
	        	 scurrent=this.setListForm.getPagination().getCurrent();
	    	   }
        }
        /* 问题 13571 ----END---  */
	   }catch(Exception e)
	   {
	   	  e.printStackTrace();
	   }
         return super.validate(arg0, arg1);
	}
	
	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public ArrayList getDbnamelist() {
		return dbnamelist;
	}

	public void setDbnamelist(ArrayList dbnamelist) {
		this.dbnamelist = dbnamelist;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public PaginationForm getRecordListForm() {
		return recordListForm;
	}

	public void setRecordListForm(PaginationForm recordListForm) {
		this.recordListForm = recordListForm;
	}

	public String getSelectsql() {
		return selectsql;
	}

	public void setSelectsql(String selectsql) {
		this.selectsql = selectsql;
	}

	public String getWheresql() {
		return wheresql;
	}

	public void setWheresql(String wheresql) {
		this.wheresql = wheresql;
	}

	public ArrayList getDbprelist() {
		return dbprelist;
	}

	public void setDbprelist(ArrayList dbprelist) {
		this.dbprelist = dbprelist;
	}

	public String[] getDbstr() {
		return dbstr;
	}

	public void setDbstr(String[] dbstr) {
		this.dbstr = dbstr;
	}

	public String getDbnamestr() {
		return dbnamestr;
	}

	public void setDbnamestr(String dbnamestr) {
		this.dbnamestr = dbnamestr;
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

	public ArrayList getSetlist() {
		return setlist;
	}

	public void setSetlist(ArrayList setlist) {
		this.setlist = setlist;
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public String getFieldstr() {
		return fieldstr;
	}

	public void setFieldstr(String fieldstr) {
		this.fieldstr = fieldstr;
	}

	public String getSync_field() {
		return sync_field;
	}

	public void setSync_field(String sync_field) {
		this.sync_field = sync_field;
	}

	public ArrayList getSync_fieldlist() {
		return sync_fieldlist;
	}

	public void setSync_fieldlist(ArrayList sync_fieldlist) {
		this.sync_fieldlist = sync_fieldlist;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode_value() {
		return code_value;
	}

	public void setCode_value(String code_value) {
		this.code_value = code_value;
	}

	public ArrayList getFieldslist() {
		return fieldslist;
	}

	public void setFieldslist(ArrayList fieldslist) {
		this.fieldslist = fieldslist;
	}

	public String getCodefieldstr() {
		return codefieldstr;
	}

	public void setCodefieldstr(String codefieldstr) {
		this.codefieldstr = codefieldstr;
	}

	public String getOrgcodefieldstr() {
		return orgcodefieldstr;
	}

	public void setOrgcodefieldstr(String orgcodefieldstr) {
		this.orgcodefieldstr = orgcodefieldstr;
	}

	public String getOrgfieldstr() {
		return orgfieldstr;
	}

	public void setOrgfieldstr(String orgfieldstr) {
		this.orgfieldstr = orgfieldstr;
	}

	public PaginationForm getSetListForm() {
		return setListForm;
	}

	public void setSetListForm(PaginationForm setListForm) {
		this.setListForm = setListForm;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAppfield() {
		return appfield;
	}

	public void setAppfield(String appfield) {
		this.appfield = appfield;
	}

	public String getEditname() {
		return editname;
	}

	public void setEditname(String editname) {
		this.editname = editname;
	}

	public String getEmporg() {
		return emporg;
	}

	public void setEmporg(String emporg) {
		this.emporg = emporg;
	}

	public String getSync_A01() {
		return sync_A01;
	}

	public void setSync_A01(String sync_A01) {
		this.sync_A01 = sync_A01;
	}

	public String getSync_B01() {
		return sync_B01;
	}

	public void setSync_B01(String sync_B01) {
		this.sync_B01 = sync_B01;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public ArrayList getOnlyfieldlist() {
		return onlyfieldlist;
	}

	public void setOnlyfieldlist(ArrayList onlyfieldlist) {
		this.onlyfieldlist = onlyfieldlist;
	}

	public String getOnlyfieldstr() {
		return onlyfieldstr;
	}

	public void setOnlyfieldstr(String onlyfieldstr) {
		this.onlyfieldstr = onlyfieldstr;
	}

	public String getOnlyfield() {
		return onlyfield;
	}

	public void setOnlyfield(String onlyfield) {
		this.onlyfield = onlyfield;
	}

	public String getPostfieldstr() {
		return postfieldstr;
	}

	public void setPostfieldstr(String postfieldstr) {
		this.postfieldstr = postfieldstr;
	}

	public String getPostcodefieldstr() {
		return postcodefieldstr;
	}

	public void setPostcodefieldstr(String postcodefieldstr) {
		this.postcodefieldstr = postcodefieldstr;
	}

	public String getSync_K01() {
		return sync_K01;
	}

	public void setSync_K01(String sync_K01) {
		this.sync_K01 = sync_K01;
	}

	public ArrayList getSync_typelst() {
		return sync_typelst;
	}

	public void setSync_typelst(ArrayList sync_typelst) {
		this.sync_typelst = sync_typelst;
	}

	public String getSelect_name() {
		return select_name;
	}

	public void setSelect_name(String select_name) {
		this.select_name = select_name;
	}

	public String getSync_mode() {
		return sync_mode;
	}

	public void setSync_mode(String sync_mode) {
		this.sync_mode = sync_mode;
	}

	public String getFail_limit() {
		return fail_limit;
	}

	public void setFail_limit(String fail_limit) {
		this.fail_limit = fail_limit;
	}

	public String getJz_field() {
		return jz_field;
	}

	public void setJz_field(String jz_field) {
		this.jz_field = jz_field;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}



	

	

}
