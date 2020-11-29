package com.hjsj.hrms.actionform.kq.options.manager;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.taglib.CommonData;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class UserManagerForm extends FrameForm{
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
    private ArrayList codelist=new ArrayList();
    private ArrayList findlist=new ArrayList();   
    private ArrayList operlist=new ArrayList();
    private ArrayList selectedlist = new ArrayList();
    private ArrayList logiclist=new ArrayList(); 
    private ArrayList factorlist=new ArrayList(); 
    private ArrayList nbaselist=new ArrayList(); 
    private ArrayList opinlist=new ArrayList();
    private ArrayList kq_list=new ArrayList();//人员库
	private String select_name;//筛选名字
	private String select_flag;//筛选表示
	private String select_pre;
	private String magcard_flag;
	private String magcard_cardid="";
	private String dbType="1";
	private String selectWhere="";
	private String returnvalue="1";
	private String uplevel;
	private String slflag="0";
    /**权限范围内的人员库*/
    private ArrayList dblist=new ArrayList();
    public UserManagerForm() {
        CommonData vo=new CommonData("=","=");
        operlist.add(vo);
        vo=new CommonData(">",">");
        operlist.add(vo);  
        vo=new CommonData(">=",">=");
        operlist.add(vo); 
        vo=new CommonData("<","<");
        operlist.add(vo);
        vo=new CommonData("<=","<=");
        operlist.add(vo);   
        vo=new CommonData("<>","<>");
        operlist.add(vo);
//        vo=new CommonData("like","包含");
//        operlist.add(vo);        
        vo=new CommonData("*","并且");
        logiclist.add(vo);
        vo=new CommonData("+","或");  
        logiclist.add(vo);
	}
    public ArrayList getCodelist() {
		return codelist;
	}
	public void setCodelist(ArrayList codelist) {
		this.codelist = codelist;
	}
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
		   this.setKq_code((String)this.getFormHM().get("kq_code"));
	 	   this.setKind((String)this.getFormHM().get("kind"));
	 	   this.setTypelist((ArrayList)this.getFormHM().get("typelist"));
	 	   this.setCodelist((ArrayList)this.getFormHM().get("codelist"));
	 	   this.setFindlist((ArrayList)this.getFormHM().get("findlist"));
	 	   this.setSelectedlist((ArrayList)this.getFormHM().get("selectedlist"));
	 	   this.setFactorlist((ArrayList)this.getFormHM().get("factorlist"));
		   this.setDblist((ArrayList)this.getFormHM().get("dblist"));
		   this.setManageWhere((String)this.getFormHM().get("manageWhere"));
		   this.setNbaselist((ArrayList)this.getFormHM().get("nbaselist"));
		   this.setKq_gno((String)this.getFormHM().get("kq_gno"));
		   this.setKq_cardno((String)this.getFormHM().get("kq_cardno"));
		   this.setOpinlist((ArrayList)this.getFormHM().get("opinlist"));
		   this.setKq_list((ArrayList)this.getFormHM().get("kq_list"));
		   this.setSelect_name((String)this.getFormHM().get("select_name"));
		   this.setSelect_flag((String)this.getFormHM().get("select_flag"));
		   this.setSelect_pre((String)this.getFormHM().get("select_pre"));
		   this.setMagcard_flag((String)this.getFormHM().get("magcard_flag"));
		   this.setMagcard_cardid((String)this.getFormHM().get("magcard_cardid"));
		   this.setDbType((String)this.getFormHM().get("dbType"));
		   this.setSelectWhere((String)this.getFormHM().get("selectWhere"));
		   this.setUplevel((String)this.getFormHM().get("uplevel"));
		   this.setSlflag((String)this.getFormHM().get("slflag"));
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
 	   this.getFormHM().put("nbaselist",this.getNbaselist());
 	   if(this.getPagination()!=null)			
		   this.getFormHM().put("selectedinfolist",(ArrayList)this.getPagination().getSelectedList());
 	   this.getFormHM().put("opinlist",this.getOpinlist());
 	   this.getFormHM().put("select_name",this.getSelect_name());
       this.getFormHM().put("select_flag",this.getSelect_flag());
       this.getFormHM().put("select_pre",this.getSelect_pre());
       this.getFormHM().put("magcard_cardid", this.getMagcard_cardid());
       this.getFormHM().put("magcard_flag", this.getMagcard_flag());
       this.getFormHM().put("slflag", this.getSlflag());
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
	public ArrayList getFindlist() {
		return findlist;
	}
	public void setFindlist(ArrayList findlist) {
		this.findlist = findlist;
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
	public ArrayList getLogiclist() {
		return logiclist;
	}
	public void setLogiclist(ArrayList logiclist) {
		this.logiclist = logiclist;
	}
	public ArrayList getOperlist() {
		return operlist;
	}
	public void setOperlist(ArrayList operlist) {
		this.operlist = operlist;
	}
	public ArrayList getSelectedlist() {
		return selectedlist;
	}
	
	public void setSelectedlist(ArrayList selectedlist) {
		if(selectedlist==null)
			selectedlist=new ArrayList();
		this.selectedlist = selectedlist;
	}
	public ArrayList getFactorlist() {
		return factorlist;
	}


	public void setFactorlist(ArrayList factorlist) {
		this.factorlist = factorlist;
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
	public ArrayList getNbaselist() {
		return nbaselist;
	}
	public void setNbaselist(ArrayList nbaselist) {
		this.nbaselist = nbaselist;
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
	    if("/kq/options/manager/usermanagerdata".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();
		
//		    this.getFormHM().put("select_name", "");
//		    this.setSelect_name("");
//		    this.getFormHM().put("selectWhere", "");
//		    this.setSelectWhere("");
//		    this.getFormHM().put("select_pre","");
//		    this.setSelect_pre("");
		
	       
	    }
	    if("/kq/options/manager/usermanager".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	        this.setMagcard_flag("");
	        this.getFormHM().put("magcard_flag", "");
	        this.getFormHM().put("select_name", "");
	        this.setSelect_name("");
	        this.getFormHM().put("selectWhere", "");
	        this.setSelectWhere("");
	        this.getFormHM().put("magcard_cardid", "");
	        this.setMagcard_cardid("");
		this.getFormHM().put("select_pre","all");
		this.setSelect_pre("all");
	        this.getFormHM().clear();
	    }
	    if("/kq/options/manager/usermanagerdata".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	    }
	    if("/kq/options/manager/usermanagerdata".equals(arg0.getPath())&&arg1.getParameter("b_view")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	    }
	    return super.validate(arg0, arg1);
	}
	public ArrayList getOpinlist() {
		return opinlist;
	}
	public void setOpinlist(ArrayList opinlist) {
		this.opinlist = opinlist;
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
	public String getMagcard_cardid() {
		return magcard_cardid;
	}
	public void setMagcard_cardid(String magcard_cardid) {
		this.magcard_cardid = magcard_cardid;
	}
	public String getDbType() {
		return dbType;
	}
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}
	public String getSelectWhere() {
		return selectWhere;
	}
	public void setSelectWhere(String selectWhere) {
		this.selectWhere = selectWhere;
	}
	public String getUplevel() {
		return uplevel;
	}
	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	public String getSlflag() {
		return slflag;
	}
	public void setSlflag(String slflag) {
		this.slflag = slflag;
	}
	@Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		this.setSlflag("0");
		super.reset(mapping, request);
	}
}
