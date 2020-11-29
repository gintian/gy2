/*
 * Created on 2005-7-6
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.org;

import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OrgInfoForm extends FrameForm {
	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	private String action;
	private String code;
	private String i9999;
	private String setname;
	private String kind;
	private String edittype;
	private String treeCode;
	private String setprv;
	private String orgtype;
	/**当前选中的单位介绍的指标*/
	private String orgFieldID;
	/**单位介绍形式=1文字,=0网址*/
	private String type="";
	/**单位介绍形式指标*/
	private String contentField;
	private String contentFieldValue;
	private String fieldstr="";//单位主集显示列表设置的设置项
	private String sqlstr="";
	private String wherestr="";
	private String columnstr="";
	private String orderby="";
	private ArrayList fieldList=new ArrayList();
	private ArrayList selectfieldlist=new ArrayList();//快速查询指标
	private String codemess="";//当前组织单元
	private String isShowCondition="none";
	private String querylike="";//模糊查询
	private String orglike="";//显示当前组织单元下所有组织单元
	private String query="0";//查询标识
	private String returnvalue="";//返回标识
	private String returnvalue1="";//返回标识
	private String parentid="";
	private String uplevel="0";
	private String return_codeid="";
	private String backdate="";//还原点时间
	private String start_date="";//有效日期起
	private String end_date="";//有效日期止
	private String root = "";//根节点名称
	
	//撤销结构人的转移到的机构列表
	private String[] right_fields;
	private String dbpre;
	private ArrayList dbprelist=new ArrayList();
	private ArrayList bolishlist=new ArrayList();
	//撤销的机构名
	private String bolishorgname;
	private String tarorgname;
	private String tarorgid;
	private String[] left_fields;
	private String ishavepersonmessage;
	private ArrayList movepersons=new ArrayList();
	private ArrayList fieldlist = new ArrayList();//变动历史子集指标
	private String changemsg;//是否显示变动信息
	private String isnewcombineorg;//标示组织合并时是不是采用的新的机构编码 yes 是 no 不是
	private String isrefresh;
	private ArrayList codelist=new ArrayList();
	private String codesetid;
	private String codeitemid;
	private String codeitemdesc;
	private String issuperuser;
	private String manageprive;
	private ArrayList codesetidlist=new ArrayList();
	private ArrayList transferorglist = new ArrayList();
	private String ishavedept;
    private String transfercodeitemid;
    private ArrayList newidlist = new ArrayList();
    private String transfercodeitemidall;
    private ArrayList codeitemlist = new ArrayList();
    private String combinecodeitemid;
    private String tarcodeitemdesc;
    private String corcode;
    private String selectcodeitemids;
    private String firstNodeCode;
    private String combinetext;
    private String leader;
    private String org_m;
    private ArrayList org_mlist = new ArrayList();
    private String org_c;
    private String emp_e;
    private ArrayList emp_elist = new ArrayList();
    private String link_field;
    private ArrayList link_fieldlist = new ArrayList();
    private String org_c_view;
    private String order_by;
    private String b0110;
    private ArrayList b0110list=new ArrayList();
    private String busi_have;
    private String posfillable;
    private String unitfillable;
    private ArrayList assave_fieldlist=new ArrayList();//批量另存为指标
    private PaginationForm assave_fieldlistForm=new PaginationForm();  
    private String assavefields;
    private String isWrite="";
    
    // 领导班子
    private String leaderType; //班子类型指标
    private ArrayList leaderTypeList = new ArrayList();
    private String sessionitem; //届次指标
    private ArrayList sessionItemList = new ArrayList();
    private String display_mess; // 领导班子显示指标
    private String gcond_mess;  // 统计分析条件
    
    private String leaderTypeValue;//班子类型查询值
    private String sessionValue; //届次查询值
    private String bz_mess;   //班子人员库信息
    
    private String parentCode;   //班子人员库信息
    //add by wangchaoqun on 2014-9-11 安全问题，新增参数
    private String encryptParam;
    
	public String getEncryptParam() {
		return encryptParam;
	}
	public void setEncryptParam(String encryptParam) {
		this.encryptParam = encryptParam;
	}
	public String getOrg_m() {
		return org_m;
	}
	public void setOrg_m(String org_m) {
		this.org_m = org_m;
	}
	public ArrayList getOrg_mlist() {
		return org_mlist;
	}
	public void setOrg_mlist(ArrayList org_mlist) {
		this.org_mlist = org_mlist;
	}
	public String getOrg_c() {
		return org_c;
	}
	public void setOrg_c(String org_c) {
		this.org_c = org_c;
	}

	public String getEmp_e() {
		return emp_e;
	}
	public void setEmp_e(String emp_e) {
		this.emp_e = emp_e;
	}
	public ArrayList getEmp_elist() {
		return emp_elist;
	}
	public void setEmp_elist(ArrayList emp_elist) {
		this.emp_elist = emp_elist;
	}
	public String getLink_field() {
		return link_field;
	}
	public void setLink_field(String link_field) {
		this.link_field = link_field;
	}
	public ArrayList getLink_fieldlist() {
		return link_fieldlist;
	}
	public void setLink_fieldlist(ArrayList link_fieldlist) {
		this.link_fieldlist = link_fieldlist;
	}
	public String getRoot() {
		return root;
	}
	public void setRoot(String root) {
		this.root = root;
	}
	public String getReturn_codeid() {
		return return_codeid;
	}
	public void setReturn_codeid(String return_codeid) {
		this.return_codeid = return_codeid;
	}
	public String getUplevel() {
		return uplevel;
	}
	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getOrglike() {
		return orglike;
	}
	public void setOrglike(String orglike) {
		this.orglike = orglike;
	}
	public String getQuerylike() {
		return querylike;
	}
	public void setQuerylike(String querylike) {
		this.querylike = querylike;
	}
	public String getCodemess() {
		return codemess;
	}
	public void setCodemess(String codemess) {
		this.codemess = codemess;
	}
	public String getIsShowCondition() {
		return isShowCondition;
	}
	public void setIsShowCondition(String isShowCondition) {
		this.isShowCondition = isShowCondition;
	}
	public ArrayList getSelectfieldlist() {
		return selectfieldlist;
	}
	public void setSelectfieldlist(ArrayList selectfieldlist) {
		this.selectfieldlist = selectfieldlist;
	}
	public String getFieldstr() {
		return fieldstr;
	}
	public void setFieldstr(String fieldstr) {
		this.fieldstr = fieldstr;
	}
	public String getSqlstr() {
		return sqlstr;
	}
	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}
	public String getWherestr() {
		return wherestr;
	}
	public void setWherestr(String wherestr) {
		this.wherestr = wherestr;
	}
	public String getOrderby() {
		return orderby;
	}
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	public ArrayList getFieldList() {
		return fieldList;
	}
	public void setFieldList(ArrayList fieldList) {
		this.fieldList = fieldList;
	}
	public String getOrgtype() {
		return orgtype;
	}
	public void setOrgtype(String orgtype) {
		this.orgtype = orgtype;
	}
	/**
	 * @return Returns the setprv.
	 */
	public String getSetprv() {
		return setprv;
	}
	
	public void setSetprv(String setprv) {
		this.setprv = setprv;
	}
    /**当前页*/
    private int current=1;
	private List infoSetList=new ArrayList();
	/**
	 * @return Returns the infoSetList.
	 */
	public List getInfoSetList() {
		return infoSetList;
	}
	/**
	 * @param infoSetList The infoSetList to set.
	 */
	public void setInfoSetList(List infoSetList) {
		this.infoSetList = infoSetList;
	}
	/**
	 * @return Returns the treeCode.
	 */
	public String getTreeCode() {
		return treeCode;
	}
	/**
	 * @param treeCode The treeCode to set.
	 */
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
	private List infofieldlist=new ArrayList();
	private PaginationForm orgInfoForm=new PaginationForm();   
	
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub	
		this.setInfofieldlist((List)this.getFormHM().get("infofieldlist"));
	    this.setInfoSetList((List)this.getFormHM().get("infosetlist"));
	    this.setTreeCode((String)this.getFormHM().get("treeCode"));
	    this.setKind((String)this.getFormHM().get("kind"));
	    this.setCode((String)this.getFormHM().get("code"));
	    this.setSetname((String)this.getFormHM().get("setname"));
	    this.setI9999((String)this.getFormHM().get("i9999"));
	    this.getOrgInfoForm().setList((ArrayList)this.getFormHM().get("detailinfolist"));
	    this.getOrgInfoForm().getPagination().gotoPage(current);
	    this.setSetprv((String)this.getFormHM().get("setprv"));
	    this.setOrgtype((String)this.getFormHM().get("orgtype"));
	    this.setOrgFieldID((String)this.getFormHM().get("orgFieldID"));
	    this.setType((String)this.getFormHM().get("type"));
	    this.setContentField((String)this.getFormHM().get("contentField"));
	    this.setContentFieldValue((String)this.getFormHM().get("contentFieldValue"));
	    this.setSqlstr((String)this.getFormHM().get("sqlstr"));
	    this.setFieldstr((String)this.getFormHM().get("fieldstr"));
	    this.setWherestr((String)this.getFormHM().get("wherestr"));
	    this.setOrderby((String)this.getFormHM().get("orderby"));
	    this.setFieldList((ArrayList)this.getFormHM().get("fieldList"));
	    this.setSelectfieldlist((ArrayList)this.getFormHM().get("selectfieldlist"));
	    this.setCodemess((String)this.getFormHM().get("codemess"));
	    this.setIsShowCondition((String)this.getFormHM().get("isShowCondition"));
	    this.setQuerylike((String)this.getFormHM().get("querylike"));
	    this.setOrglike((String)this.getFormHM().get("orglike"));
	    this.setColumnstr((String)this.getFormHM().get("columnstr"));
	    this.setQuery((String)this.getFormHM().get("query"));
	    this.setReturnvalue((String)this.getFormHM().get("returnvalue"));
	    this.setParentid((String)this.getFormHM().get("parentid"));
	    this.setUplevel((String)this.getFormHM().get("uplevel"));
	    this.setRoot((String) this.getFormHM().get("root")); 
	    this.setBolishlist((ArrayList)this.getFormHM().get("bolishlist"));
	    this.setDbprelist((ArrayList)this.getFormHM().get("dbprelist"));
	    this.setIshavepersonmessage((String)this.getFormHM().get("ishavepersonmessage"));
	    if(this.getFormHM().get("movepersons")!=null)
	         this.setMovepersons((ArrayList)this.getFormHM().get("movepersons"));
	    this.setIsrefresh((String)this.getFormHM().get("isrefresh"));
	    this.setFieldlist((ArrayList)this.getFormHM().get("childfielditemlist"));
	    this.setChangemsg((String)this.getFormHM().get("changemsg"));
	    if(this.getFormHM().get("codelist")!=null)
	    	this.setCodelist((ArrayList)this.getFormHM().get("codelist"));
	    this.setEdittype((String)this.getFormHM().get("edittype"));
	    this.setCodesetid((String)this.getFormHM().get("setid"));
	    this.setCodeitemid((String)this.getFormHM().get("itemid"));
	    this.setCodeitemdesc((String)this.getFormHM().get("codeitemdesc"));
	    this.setIssuperuser((String)this.getFormHM().get("issuperuser"));
	    this.setManageprive((String)this.getFormHM().get("manageprive"));
	    this.setCodesetidlist((ArrayList)this.getFormHM().get("codesetidlist"));
	    this.setIshavedept((String)this.getFormHM().get("ishavedept"));
	    this.setNewidlist((ArrayList)this.getFormHM().get("newidlist"));
	    this.setTransfercodeitemidall((String)this.getFormHM().get("transfercodeitemidall"));
	    this.setTransferorglist((ArrayList)this.getFormHM().get("transferorglist"));
	    this.setCodeitemlist((ArrayList)this.getFormHM().get("codeitemlist"));
	    this.setCodesetid((String)this.getFormHM().get("codesetid"));
	    this.selectcodeitemids=(String)this.getFormHM().get("selectcodeitemids");
	    this.setFirstNodeCode((String)this.getFormHM().get("firstNodeCode"));
	    this.setIsnewcombineorg((String)this.getFormHM().get("isnewcombineorg"));
	    this.setCombinetext((String)this.getFormHM().get("combinetext"));
	    this.setBackdate((String)this.getFormHM().get("backdate"));
	    this.setEmp_e((String)this.getFormHM().get("emp_e"));
	    this.setEmp_elist((ArrayList)this.getFormHM().get("emp_elist"));
	    this.setOrg_m((String)this.getFormHM().get("org_m"));
	    this.setOrg_mlist((ArrayList)this.getFormHM().get("org_mlist"));
	    this.setOrg_c((String)this.getFormHM().get("org_c"));
	    this.setLink_field((String)this.getFormHM().get("link_field"));
	    this.setLink_fieldlist((ArrayList)this.getFormHM().get("link_fieldlist"));
	    this.setOrg_c_view((String)this.getFormHM().get("org_c_view"));
	    this.setB0110((String)this.getFormHM().get("b0110"));
	    this.setB0110list((ArrayList)this.getFormHM().get("b0110list"));
	    this.setOrder_by((String)this.getFormHM().get("order_by"));
	    this.setBusi_have((String)this.getFormHM().get("busi_have"));
	    this.setUnitfillable((String)this.getFormHM().get("unitfillable"));
	    this.setPosfillable((String)this.getFormHM().get("posfillable"));
	    this.getAssave_fieldlistForm().setList((ArrayList)this.getFormHM().get("assave_fieldlist"));
	    this.setAssavefields((String)this.getFormHM().get("assavefields"));
	    this.setIsWrite((String)this.getFormHM().get("isWrite"));

		this.setTarorgname((String)getFormHM().get("tarorgname"));
	    this.setTransfercodeitemid((String)getFormHM().get("transfercodeitemid"));
	    this.setLeaderType((String)getFormHM().get("leaderType"));
	    this.setLeaderTypeList((ArrayList)getFormHM().get("leaderTypeList"));
	    this.setSessionitem((String)getFormHM().get("sessionitem"));
	    this.setSessionItemList((ArrayList)getFormHM().get("sessionitemList"));
	    this.setDisplay_mess((String)getFormHM().get("display_mess"));
	    this.setGcond_mess((String)getFormHM().get("gcond_mess"));
	    
	    this.setLeaderTypeValue((String)getFormHM().get("leaderTypeValue"));
	    this.setSessionValue((String)getFormHM().get("sessionValue"));
	    
	    this.setEncryptParam((String)this.getFormHM().get("encryptParam"));   //add by wangchaoqun on 2014-9-11
	    this.setBz_mess((String)this.getFormHM().get("bz_mess"));
	    this.setParentid((String) this.getFormHM().get("parentid"));
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub	
	 this.getFormHM().put("contentField", this.getContentField());
	 this.getFormHM().put("contentFieldValue", this.getContentFieldValue());
	 this.getFormHM().put("setname",setname);
	 this.getFormHM().put("code",code);
	 this.getFormHM().put("i9999",i9999);
	 this.getFormHM().put("infofieldlist",infofieldlist);
	 this.getFormHM().put("action",action);
	 this.getFormHM().put("kind",kind);
	 this.getFormHM().put("selectedlist",(List)this.getOrgInfoForm().getSelectedList());
	 this.getFormHM().put("edittype",edittype);
	 this.getFormHM().put("orgtype",this.getOrgtype());
	 this.getFormHM().put("selectfieldlist", this.getSelectfieldlist());
	 this.getFormHM().put("fieldstr", this.getFieldstr());
	 this.getFormHM().put("isShowCondition", this.isShowCondition);
	 this.getFormHM().put("querylike", this.getQuerylike());
	 this.getFormHM().put("orglike", this.getOrglike());
	 this.getFormHM().put("query", this.getQuery());
	 this.getFormHM().put("returnvalue", this.getReturnvalue());
	 this.getFormHM().put("parentid", this.getParentid());
	 this.getFormHM().put("uplevel", this.getUplevel());
	 if(this.getPagination()!=null){
		   this.getFormHM().put("selectedinfolist",(ArrayList)this.getPagination().getSelectedList());
		   this.getPagination().unSelectedAll();
	 }
	 this.getFormHM().put("persons",this.left_fields);
	 this.getFormHM().put("persontoorg",this.tarorgid);
	 this.getFormHM().put("dbpre",dbpre);
	 this.getFormHM().put("bolishorgname",bolishorgname);
	 this.getFormHM().put("fieldlist", this.getFieldlist());
	 this.getFormHM().put("transferorglist", transferorglist);
	 this.getFormHM().put("end_date", end_date);
	 this.getFormHM().put("transfercodeitemid",transfercodeitemid);
	 this.getFormHM().put("transferorglist", transferorglist);
	 this.getFormHM().put("isrefresh", this.getIsrefresh());
	 this.getFormHM().put("combinecodeitemid", combinecodeitemid);
	 this.getFormHM().put("tarcodeitemdesc", tarcodeitemdesc);
	 this.getFormHM().put("corcode", corcode);
	 this.getFormHM().put("selectcodeitemids", selectcodeitemids);
	 this.getFormHM().put("movepersons", movepersons);
	 this.getFormHM().put("backdate", backdate);
	 this.getFormHM().put("org_m", org_m);
	 this.getFormHM().put("org_c", org_c);
	 this.getFormHM().put("emp_e", emp_e);
	 this.getFormHM().put("link_field", link_field);
	 this.getFormHM().put("leader", leader);
	 this.getFormHM().put("b0110", b0110);
	 this.getFormHM().put("order_by", order_by);
	 this.getFormHM().put("assavefields", this.getAssavefields());
	 this.getFormHM().put("leaderType", leaderType);
	 this.getFormHM().put("sessionitem", sessionitem);
	 this.getFormHM().put("display_mess", display_mess);
	 this.getFormHM().put("gcond_mess", gcond_mess);
	 
	 this.getFormHM().put("leaderTypeValue", leaderTypeValue);
	 this.getFormHM().put("sessionValue", sessionValue);
	 this.getFormHM().put("parentid", parentid);
	}
	
	@Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		// TODO Auto-generated method stub
		super.reset(mapping, request);
		try{
			if(this.getFieldlist()!=null)
				for(int i=0;i<this.getFieldlist().size();i++){
					FieldItem fielditem = (FieldItem)this.getFieldlist().get(i);
					fielditem.setValue("");
					fielditem.setViewvalue("");
				}
		}catch(Exception e){
			e.printStackTrace();
		}
		this.left_fields=null;
	}
	/**
	 * @return Returns the action.
	 */
	public String getAction() {
		return action;
	}
	/**
	 * @param action The action to set.
	 */
	public void setAction(String action) {
		this.action = action;
	}
	/**
	 * @return Returns the code.
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code The code to set.
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return Returns the i9999.
	 */
	public String getI9999() {
		return i9999;
	}
	/**
	 * @param i9999 The i9999 to set.
	 */
	public void setI9999(String i9999) {
		this.i9999 = i9999;
	}
	
	/**
	 * @return Returns the infofieldlist.
	 */
	public List getInfofieldlist() {
		return infofieldlist;
	}
	/**
	 * @param infofieldlist The infofieldlist to set.
	 */
	public void setInfofieldlist(List infofieldlist) {
		this.infofieldlist = infofieldlist;
	}
	/**
	 * @return Returns the kind.
	 */
	public String getKind() {
		return kind;
	}
	/**
	 * @param kind The kind to set.
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}
	/**
	 * @return Returns the setname.
	 */
	public String getSetname() {
		return setname;
	}
	/**
	 * @param setname The setname to set.
	 */
	public void setSetname(String setname) {
		this.setname = setname;
	}
	/**
	 * @return Returns the orgInfoForm.
	 */
	public PaginationForm getOrgInfoForm() {
		return orgInfoForm;
	}
	/**
	 * @param orgInfoForm The orgInfoForm to set.
	 */
	public void setOrgInfoForm(PaginationForm orgInfoForm) {
		this.orgInfoForm = orgInfoForm;
	}
	/**
	 * @return Returns the edittype.
	 */
	public String getEdittype() {
		return edittype;
	}
	/**
	 * @param edittype The edittype to set.
	 */
	public void setEdittype(String edittype) {
		this.edittype = edittype;
	}
	  @Override
      public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	   {
		    if(this.orgInfoForm.getPagination()!=null)
               current=this.orgInfoForm.getPagination().getCurrent();
	        if("/workbench/orginfo/editorgdetailinfodata".equals(arg0.getPath())&&(arg1.getParameter("b_return")!=null || arg1.getParameter("b_save")!=null))
	        {
	           if(this.edittype!=null&& "update".equalsIgnoreCase(this.edittype))
	           {
	        	   if(this.orgInfoForm.getPagination()!=null)	 	             
	 	            current=this.orgInfoForm.getPagination().getCurrent();
	           }else
	           {
	        	   if(this.orgInfoForm.getPagination()!=null)
	        	   {
	        		    this.orgInfoForm.getPagination().lastPage();
		 	            current=this.orgInfoForm.getPagination().getCurrent();
	        	   }  
	           }
	           
	        }
	        /*新增机构主集没维护切换页签报错提示先维护主集信息，点击返回页面变了，但是顶部页签没切换。这里处理一下，不显示返回按钮了。guodd 2018-01-10*/
	        if("/workbench/orginfo/searchdetailinfolist".equals(arg0.getPath())&& arg1.getParameter("b_search")!=null) {
	        	arg1.setAttribute("targetWindow", "0");
	        }
	        if("/workbench/orginfo/searchdetailinfolist".equals(arg0.getPath())&& arg1.getParameter("b_delete")!=null)
	        {
	            if(this.orgInfoForm.getPagination()!=null)
	            	current=this.orgInfoForm.getPagination().getCurrent();
	        }	
	        if("/workbench/orginfo/searchdetailinfolist".equals(arg0.getPath())&& arg1.getParameter("b_search")!=null)
	        {
	            if(this.orgInfoForm.getPagination()!=null)
	            	this.orgInfoForm.getPagination().firstPage();//?
	            current=this.orgInfoForm.getPagination().getCurrent();
	            arg1.setAttribute("formpath", "/workbench/orginfo/editorginfodata.do?b_query=link&setname=B01&treetype=org");//liuy 2015-4-25 9055
	        }
	        if("/workbench/orginfo/searchorginfo".equals(arg0.getPath())&& arg1.getParameter("b_search")!=null)
	        {
	            if(this.getPagination()!=null)
	            	this.getPagination().firstPage();//?
	           
	            this.setCode("");
	            this.setKind("");
	            this.getFormHM().put("code", "");
	            this.getFormHM().put("kind", "");
	        }
	        if("/workbench/orginfo/searchorginfodata".equals(arg0.getPath())&& arg1.getParameter("b_search")!=null)
	        {
	            if(this.getPagination()!=null)
	            	this.getPagination().firstPage();//?
	            this.setQuery("0");
	          this.getFormHM().put("query", "");
	        }
	        if("/workbench/orginfo/searchorginfodata".equals(arg0.getPath())&& arg1.getParameter("b_query")!=null)
	        {
	            if(this.getPagination()!=null)
	            	//this.getPagination().firstPage();//?	
	            	current=this.orgInfoForm.getPagination().getCurrent();
	        }
	        return super.validate(arg0, arg1);
	   }
	public String getOrgFieldID() {
		return orgFieldID;
	}
	public void setOrgFieldID(String orgFieldID) {
		this.orgFieldID = orgFieldID;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContentField() {
		return contentField;
	}
	public void setContentField(String contentField) {
		this.contentField = contentField;
	}
	public String getContentFieldValue() {
		return contentFieldValue;
	}
	public void setContentFieldValue(String contentFieldValue) {
		this.contentFieldValue = contentFieldValue;
	}
	public String getColumnstr() {
		return columnstr;
	}
	public void setColumnstr(String columnstr) {
		this.columnstr = columnstr;
	}
	public String getBackdate() {
		return backdate;
	}
	public void setBackdate(String backdate) {
		this.backdate = backdate;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public ArrayList getBolishlist() {
		return bolishlist;
	}
	public void setBolishlist(ArrayList bolishlist) {
		this.bolishlist = bolishlist;
	}
	public ArrayList getDbprelist() {
		return dbprelist;
	}
	public void setDbprelist(ArrayList dbprelist) {
		this.dbprelist = dbprelist;
	}
	public String[] getRight_fields() {
		return right_fields;
	}
	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}
	public String getDbpre() {
		return dbpre;
	}
	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}
	public String getBolishorgname() {
		return bolishorgname;
	}
	public void setBolishorgname(String bolishorgname) {
		this.bolishorgname = bolishorgname;
	}
	public String getTarorgname() {
		return tarorgname;
	}
	public void setTarorgname(String tarorgname) {
		this.tarorgname = tarorgname;
	}
	public String getTarorgid() {
		return tarorgid;
	}
	public void setTarorgid(String tarorgid) {
		this.tarorgid = tarorgid;
	}
	public String[] getLeft_fields() {
		return left_fields;
	}
	public void setLeft_fields(String[] left_fields) {
		this.left_fields = left_fields;
	}
	public String getIshavepersonmessage() {
		return ishavepersonmessage;
	}
	public void setIshavepersonmessage(String ishavepersonmessage) {
		this.ishavepersonmessage = ishavepersonmessage;
	}
	public ArrayList getMovepersons() {
		return movepersons;
	}
	public void setMovepersons(ArrayList movepersons) {
		this.movepersons = movepersons;
	}
	public ArrayList getFieldlist() {
		return fieldlist;
	}
	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}
	public String getChangemsg() {
		return changemsg;
	}
	public void setChangemsg(String changemsg) {
		this.changemsg = changemsg;
	}
	public String getIsnewcombineorg() {
		return isnewcombineorg;
	}
	public void setIsnewcombineorg(String isnewcombineorg) {
		this.isnewcombineorg = isnewcombineorg;
	}
	public int getCurrent() {
		return current;
	}
	public void setCurrent(int current) {
		this.current = current;
	}
	public String getIsrefresh() {
		return isrefresh;
	}
	public void setIsrefresh(String isrefresh) {
		this.isrefresh = isrefresh;
	}
	public ArrayList getCodelist() {
		return codelist;
	}
	public void setCodelist(ArrayList codelist) {
		this.codelist = codelist;
	}
	public String getCodesetid() {
		return codesetid;
	}
	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}
	public String getCodeitemid() {
		return codeitemid;
	}
	public void setCodeitemid(String codeitemid) {
		this.codeitemid = codeitemid;
	}
	public String getCodeitemdesc() {
		return codeitemdesc;
	}
	public void setCodeitemdesc(String codeitemdesc) {
		this.codeitemdesc = codeitemdesc;
	}
	public String getIssuperuser() {
		return issuperuser;
	}
	public void setIssuperuser(String issuperuser) {
		this.issuperuser = issuperuser;
	}
	public String getManageprive() {
		return manageprive;
	}
	public void setManageprive(String manageprive) {
		this.manageprive = manageprive;
	}
	public ArrayList getCodesetidlist() {
		return codesetidlist;
	}
	public void setCodesetidlist(ArrayList codesetidlist) {
		this.codesetidlist = codesetidlist;
	}
	public ArrayList getTransferorglist() {
		return transferorglist;
	}
	public void setTransferorglist(ArrayList transferorglist) {
		this.transferorglist = transferorglist;
	}
	public String getIshavedept() {
		return ishavedept;
	}
	public void setIshavedept(String ishavedept) {
		this.ishavedept = ishavedept;
	}
	public String getTransfercodeitemid() {
		return transfercodeitemid;
	}
	public void setTransfercodeitemid(String transfercodeitemid) {
		this.transfercodeitemid = transfercodeitemid;
	}
	public ArrayList getNewidlist() {
		return newidlist;
	}
	public void setNewidlist(ArrayList newidlist) {
		this.newidlist = newidlist;
	}
	public String getTransfercodeitemidall() {
		return transfercodeitemidall;
	}
	public void setTransfercodeitemidall(String transfercodeitemidall) {
		this.transfercodeitemidall = transfercodeitemidall;
	}
	public ArrayList getCodeitemlist() {
		return codeitemlist;
	}
	public void setCodeitemlist(ArrayList codeitemlist) {
		this.codeitemlist = codeitemlist;
	}
	public String getCombinecodeitemid() {
		return combinecodeitemid;
	}
	public void setCombinecodeitemid(String combinecodeitemid) {
		this.combinecodeitemid = combinecodeitemid;
	}

	public String getCorcode() {
		return corcode;
	}
	public void setCorcode(String corcode) {
		this.corcode = corcode;
	}
	public String getSelectcodeitemids() {
		return selectcodeitemids;
	}
	public void setSelectcodeitemids(String selectcodeitemids) {
		this.selectcodeitemids = selectcodeitemids;
	}
	public String getTarcodeitemdesc() {
		return tarcodeitemdesc;
	}
	public void setTarcodeitemdesc(String tarcodeitemdesc) {
		this.tarcodeitemdesc = tarcodeitemdesc;
	}
	public String getFirstNodeCode() {
		return firstNodeCode;
	}
	public void setFirstNodeCode(String firstNodeCode) {
		this.firstNodeCode = firstNodeCode;
	}
	public String getCombinetext() {
		return combinetext;
	}
	public void setCombinetext(String combinetext) {
		this.combinetext = combinetext;
	}
	public String getReturnvalue1() {
		return returnvalue1;
	}
	public void setReturnvalue1(String returnvalue1) {
		this.returnvalue1 = returnvalue1;
	}
	public String getLeader() {
		return leader;
	}
	public void setLeader(String leader) {
		this.leader = leader;
	}
	public String getOrg_c_view() {
		return org_c_view;
	}
	public void setOrg_c_view(String org_c_view) {
		this.org_c_view = org_c_view;
	}
	public String getOrder_by() {
		return order_by;
	}
	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}
	public String getB0110() {
		return b0110;
	}
	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}
	public ArrayList getB0110list() {
		return b0110list;
	}
	public void setB0110list(ArrayList b0110list) {
		this.b0110list = b0110list;
	}
	public String getBusi_have() {
		return busi_have;
	}
	public void setBusi_have(String busi_have) {
		this.busi_have = busi_have;
	}
	public String getPosfillable() {
		return posfillable;
	}
	public void setPosfillable(String posfillable) {
		this.posfillable = posfillable;
	}
	public String getUnitfillable() {
		return unitfillable;
	}
	public void setUnitfillable(String unitfillable) {
		this.unitfillable = unitfillable;
	}
	public ArrayList getAssave_fieldlist() {
		return assave_fieldlist;
	}
	public void setAssave_fieldlist(ArrayList assave_fieldlist) {
		this.assave_fieldlist = assave_fieldlist;
	}
	public PaginationForm getAssave_fieldlistForm() {
		return assave_fieldlistForm;
	}
	public void setAssave_fieldlistForm(PaginationForm assave_fieldlistForm) {
		this.assave_fieldlistForm = assave_fieldlistForm;
	}
	public String getAssavefields() {
		return assavefields;
	}
	public void setAssavefields(String assavefields) {
		this.assavefields = assavefields;
	}
	public String getIsWrite() {
		return isWrite;
	}
	public void setIsWrite(String isWrite) {
		this.isWrite = isWrite;
	}
	public String getLeaderType() {
		return leaderType;
	}
	public void setLeaderType(String leaderType) {
		this.leaderType = leaderType;
	}
	public String getSessionitem() {
		return sessionitem;
	}
	public void setSessionitem(String sessionitem) {
		this.sessionitem = sessionitem;
	}
	public String getDisplay_mess() {
		return display_mess;
	}
	public void setDisplay_mess(String display_mess) {
		this.display_mess = display_mess;
	}
	public String getGcond_mess() {
		return gcond_mess;
	}
	public void setGcond_mess(String gcond_mess) {
		this.gcond_mess = gcond_mess;
	}
	public ArrayList getLeaderTypeList() {
		return leaderTypeList;
	}
	public void setLeaderTypeList(ArrayList leaderTypeList) {
		this.leaderTypeList = leaderTypeList;
	}
	public ArrayList getSessionItemList() {
		return sessionItemList;
	}
	public void setSessionItemList(ArrayList sessionItemList) {
		this.sessionItemList = sessionItemList;
	}
	public String getLeaderTypeValue() {
		return leaderTypeValue;
	}
	public void setLeaderTypeValue(String leaderTypeValue) {
		this.leaderTypeValue = leaderTypeValue;
	}
	public String getSessionValue() {
		return sessionValue;
	}
	public void setSessionValue(String sessionValue) {
		this.sessionValue = sessionValue;
	}
	public String getBz_mess() {
		return bz_mess;
	}
	public void setBz_mess(String bz_mess) {
		this.bz_mess = bz_mess;
	}
    public String getParentCode() {
        return parentCode;
    }
    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }
	
	

	
}
