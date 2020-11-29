package com.hjsj.hrms.actionform.kq.options.manager;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class MagCardManagerForm extends FrameForm{
	
	private ArrayList fieldlist=new ArrayList();
	private String treeCode;//树形菜单，在HtmlMenu中
	private String strsql;
    private String strwhere;
    private String orderby;
    private String columns;
    private String code;
    private String kind;
    private String name;
    private String kq_type; 
    private String kq_cardno; 
    private String kq_gno; 
    private String kq_code;
    private String manageWhere;
    private ArrayList typelist=new ArrayList();
    private ArrayList newfieldlist=new ArrayList();
    private ArrayList changefieldlist=new ArrayList();
    
    private ArrayList kq_list=new ArrayList();//人员库
	private String select_name;//筛选名字
	private String select_flag;//筛选表示
	private String select_pre;
	private String magcard_flag;
	private String a_code;
	private String magcard_setid;
	private String cardno_value="";
	private String i9999="";
	private String a0100="";
	private String singmess="";
	private String nbase="";
	private ArrayList machinelist=new ArrayList();
	private String machineid="1";
	private String magcard_com="";
	private String selectWhere="";
    /**权限范围内的人员库*/
    private ArrayList dblist=new ArrayList();
    private ArrayList searchlist=new ArrayList(); //通用查询公式
    private String viewsearch; //是否显示查询结果
    private String magcard_cardid="";
	private String dbType="1";
	@Override
    public void outPutFormHM()	{
    	   this.setKq_type((String)this.getFormHM().get("kq_type"));
		   this.setTreeCode((String)this.getFormHM().get("treeCode"));	 
		   this.setStrsql((String)this.getFormHM().get("strsql"));
		   this.setStrwhere((String)this.getFormHM().get("strwhere"));
		   this.setOrderby((String)this.getFormHM().get("orderby"));
		   this.setColumns((String)this.getFormHM().get("columns"));
		   this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		   this.setCode((String)this.getFormHM().get("code"));
	 	   this.setKind((String)this.getFormHM().get("kind"));
	 	   this.setTypelist((ArrayList)this.getFormHM().get("typelist"));
	 	   this.setNewfieldlist((ArrayList)this.getFormHM().get("newfieldlist"));	 	   
		   this.setDblist((ArrayList)this.getFormHM().get("dblist"));
		   this.setManageWhere((String)this.getFormHM().get("manageWhere"));		   
		   this.setKq_gno((String)this.getFormHM().get("kq_gno"));
		   this.setKq_cardno((String)this.getFormHM().get("kq_cardno"));		  
		   this.setKq_list((ArrayList)this.getFormHM().get("kq_list"));
		   this.setSelect_name((String)this.getFormHM().get("select_name"));
		   this.setSelect_flag((String)this.getFormHM().get("select_flag"));
		   this.setSelect_pre((String)this.getFormHM().get("select_pre"));
		   this.setMagcard_flag((String)this.getFormHM().get("magcard_flag"));
		   this.setA_code((String)this.getFormHM().get("a_code"));
		   this.setMagcard_setid((String)this.getFormHM().get("magcard_setid"));
		   this.setChangefieldlist((ArrayList)this.getFormHM().get("changefieldlist"));
		   this.setCardno_value((String)this.getFormHM().get("cardno_value"));
		   this.setA0100((String)this.getFormHM().get("a0100"));
		   this.setI9999((String)this.getFormHM().get("i9999"));
		   this.setSingmess((String)this.getFormHM().get("singmess"));
		   this.setMagcard_cardid((String)this.getFormHM().get("magcard_cardid"));
		   this.setDbType((String)this.getFormHM().get("dbType"));
		   this.setMachinelist((ArrayList)this.getFormHM().get("machinelist"));
		   this.setMagcard_com((String)this.getFormHM().get("magcard_com"));
		   this.setSearchlist((ArrayList)this.getFormHM().get("searchlist"));
		   this.setViewsearch((String)this.getFormHM().get("viewsearch"));
		   this.setName((String)this.getFormHM().get("nbase"));
	}
	private String left_fields[];
    /**选中的字段名数组*/
    private String right_fields[];  
    /**能用查询的表达式:!(1+2*3),!非，＋或，*且*/
    @Override
    public void inPutTransHM() {
    	this.getFormHM().put("kq_type",this.getKq_type());
 	    if(this.getPagination()!=null)        
 	          this.getFormHM().put("list",this.getPagination().getAllList());
 	    this.getFormHM().put("code",this.getCode());
 	    this.getFormHM().put("kind",this.getKind());
 	    this.getFormHM().put("kq_code",this.getKq_code());
 	    this.getFormHM().put("right_fields",this.getRight_fields());
 	    this.getFormHM().put("kq_gno",this.getKq_gno());
 	    this.getFormHM().put("kq_type",this.getKq_type());
 	    this.getFormHM().put("fieldlist",this.getFieldlist());
 	    this.getFormHM().put("dblist",this.getDblist());
 	    this.getFormHM().put("manageWhere",this.getManageWhere());
 	   
 	    if(this.getPagination()!=null)			
		   this.getFormHM().put("selectedinfolist",(ArrayList)this.getPagination().getSelectedList());
 	  
 	    this.getFormHM().put("select_name",this.getSelect_name());
        this.getFormHM().put("select_flag",this.getSelect_flag());
        this.getFormHM().put("select_pre",this.getSelect_pre());
        this.getFormHM().put("magcard_setid", this.getMagcard_setid());
        this.getFormHM().put("newfieldlist", this.getNewfieldlist());
        this.getFormHM().put("cardno_value", this.getCardno_value());
        this.getFormHM().put("a0100", this.getA0100());
        this.getFormHM().put("i9999", this.getI9999());
        this.getFormHM().put("nbase", nbase);
        this.getFormHM().put("selectWhere", this.getSelectWhere());
    }
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getColumns() {
		return columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	public ArrayList getFieldlist() {
		return fieldlist;
	}
	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getOrderby() {
		return orderby;
	}
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	public String getStrsql() {
		return strsql;
	}
	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}
	public String getStrwhere() {
		return strwhere;
	}
	public void setStrwhere(String strwhere) {
		this.strwhere = strwhere;
	}
	public String getTreeCode() {
		return treeCode;
	}
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKq_type() {
		return kq_type;
	}
	public void setKq_type(String kq_type) {
		this.kq_type = kq_type;
	}
	public ArrayList getTypelist() {
		return typelist;
	}
	public void setTypelist(ArrayList typelist) {
		this.typelist = typelist;
	}
	public String getKq_code() {
		return kq_code;
	}
	public void setKq_code(String kq_code) {
		this.kq_code = kq_code;
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
	
	public ArrayList getDblist() {
		return dblist;
	}
	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}
	public String getKq_cardno() {
		return kq_cardno;
	}
	public void setKq_cardno(String kq_cardno) {
		this.kq_cardno = kq_cardno;
	}
	public String getKq_gno() {
		return kq_gno;
	}
	public void setKq_gno(String kq_gno) {
		this.kq_gno = kq_gno;
	}
	public String getManageWhere() {
		return manageWhere;
	}
	public void setManageWhere(String manageWhere) {
		this.manageWhere = manageWhere;
	}	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		String selectWhere=(String)arg1.getSession().getAttribute("selectWhere");
		if(selectWhere==null||selectWhere.length()<=0)
			selectWhere=""; 
		this.setSelectWhere(selectWhere);
		this.getFormHM().put("selectWhere", selectWhere);
	    if("/kq/options/manager/usermanagerdata".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	        this.getFormHM().put("select_name", "");
	        this.setSelect_name("");
	    }
	    if("/kq/options/manager/usermanager".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	        this.getFormHM().put("select_name", "");
	        this.getFormHM().put("magcard_cardid", "");
	        this.setSelect_name("");
	        this.setMagcard_cardid("");
	        this.getFormHM().clear();;
	    }
	    if("/kq/options/manager/usermanagerdata".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	    }
	    if("/kq/options/manager/magcard".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?	      
	        this.getFormHM().put("select_name", "");
	        this.setSelect_name("");	       
	        this.getFormHM().put("magcard_cardid", "");
	        this.setMagcard_cardid("");
	        this.getFormHM().clear();
	    }
	    if("/kq/options/manager/magcarddata".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	    	 if(this.getPagination()!=null)
		          this.getPagination().firstPage();//?	 
	    	 if(arg1.getParameter("viewsearch")!=null)
	    	 {
	    	     this.getFormHM().put("select_name", "");
		     this.setSelect_name("");
		     this.getFormHM().put("select_pre","all");
		     this.setSelect_pre("all");
	    	 }
	    }
	    return super.validate(arg0, arg1);
	}
	
	public ArrayList getKq_list() {
		return kq_list;
	}
	public void setKq_list(ArrayList kq_list) {
		this.kq_list = kq_list;
	}
	public String getSelect_flag() {
		return select_flag;
	}
	public void setSelect_flag(String select_flag) {
		this.select_flag = select_flag;
	}
	public String getSelect_name() {
		return select_name;
	}
	public void setSelect_name(String select_name) {
		this.select_name = select_name;
	}
	public String getSelect_pre() {
		return select_pre;
	}
	public void setSelect_pre(String select_pre) {
		this.select_pre = select_pre;
	}
	public String getMagcard_flag() {
		return magcard_flag;
	}
	public void setMagcard_flag(String magcard_flag) {
		this.magcard_flag = magcard_flag;
	}
	public String getA_code() {
		return a_code;
	}
	public void setA_code(String a_code) {
		this.a_code = a_code;
	}
	public String getMagcard_setid() {
		return magcard_setid;
	}
	public void setMagcard_setid(String magcard_setid) {
		this.magcard_setid = magcard_setid;
	}
	public ArrayList getNewfieldlist() {
		return newfieldlist;
	}
	public void setNewfieldlist(ArrayList newfieldlist) {
		this.newfieldlist = newfieldlist;
	}
	public ArrayList getChangefieldlist() {
		return changefieldlist;
	}
	public void setChangefieldlist(ArrayList changefieldlist) {
		this.changefieldlist = changefieldlist;
	}
	public String getCardno_value() {
		return cardno_value;
	}
	public void setCardno_value(String cardno_value) {
		this.cardno_value = cardno_value;
	}
	public String getA0100() {
		return a0100;
	}
	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}
	public String getI9999() {
		return i9999;
	}
	public void setI9999(String i9999) {
		this.i9999 = i9999;
	}
	public String getSingmess() {
		return singmess;
	}
	public void setSingmess(String singmess) {
		this.singmess = singmess;
	}
	public String getMagcard_cardid() {
		return magcard_cardid;
	}
	public void setMagcard_cardid(String magcard_cardid) {
		this.magcard_cardid = magcard_cardid;
	}
	public ArrayList getMachinelist() {
		return machinelist;
	}
	public void setMachinelist(ArrayList machinelist) {
		this.machinelist = machinelist;
	}
	public String getMachineid() {
		return machineid;
	}
	public void setMachineid(String machineid) {
		this.machineid = machineid;
	}
	public String getMagcard_com() {
		return magcard_com;
	}
	public void setMagcard_com(String magcard_com) {
		this.magcard_com = magcard_com;
	}
	public ArrayList getSearchlist() {
		return searchlist;
	}
	public void setSearchlist(ArrayList searchlist) {
		this.searchlist = searchlist;
	}
	public String getViewsearch() {
		return viewsearch;
	}
	public void setViewsearch(String viewsearch) {
		this.viewsearch = viewsearch;
	}
	public String getSelectWhere() {
		return selectWhere;
	}
	public void setSelectWhere(String selectWhere) {
		this.selectWhere = selectWhere;
	}
	public String getDbType() {
		return dbType;
	}
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}
	public String getNbase() {
		return nbase;
	}
	public void setNbase(String nbase) {
		this.nbase = nbase;
	}
}
