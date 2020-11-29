/*
 * Created on 2005-6-14
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.browse.history;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PersonHistoryForm extends FrameForm {

    
    private String userbase="Usr";
    private String dbcond;
    private String setname="A01";
	private String a0100;
    ArrayList infodetailfieldlist=new ArrayList();
    ArrayList infofieldlist=new ArrayList();
    ArrayList infosetlist=new ArrayList();
    private String flag;
    private String cond_str;
    private String code;
    private String kind;
    private String treeCode;
    private String strsql;
	private ArrayList browsefields=new ArrayList();
	private String columns;
	private String state;
	private String uplevel="";
	private String check="";
	private String home="";
	private String result="";
	private String orgtype="";
	private String where_n="";
	private String ensql="";
	private String photo_other_view="";
	private String photolength="";
	private String expr="";
	private String factor="";
	/***人员快速浏览****/
	private String isShowCondition="";
	private String querylike="";//模糊查询
	private ArrayList scanfieldlist=new ArrayList();//查询指标 
	private String unit_code_mess="";//岗位编码
	private String codemess="";//岗位名称
	private String query="";//快速查询标识
	private String orgflag="";//组织机构标志
	private String parentid="";
	private String codesetid="";
	private String return_codeid="";
	private String orglike="";
	private String scantype="";	
	private ArrayList queryfieldlist=new ArrayList();
	private String select_name;
	private String backdate;
	private String backname;//时点名称
	private String ifbackup;
	private String uniqueitem;
	
	/*liwc*/
	private ArrayList list1=new ArrayList();
	private ArrayList rightlist=new ArrayList();
	private ArrayList leftlist=new ArrayList();
	private ArrayList setlist=new ArrayList();
	private ArrayList chklist=new ArrayList();
	private HashMap chk_v=new HashMap();
	private String right_fields;
	private String left_fields;
	private String right_value;
	private String left_value;
	private String snap_norm;
	private String sn_left;
	
	//高级查询
	private String strQuery;
	private String fieldid;
	private String fieldSetId="hr_emp_hisdata";
	private String type="1";
	private String a_code="";
	private String tablename="";
	private String fieldSetDesc="人员历史时点信息";
	
	private String orgbackdate;
	
	private String returnvalue;
	private String sumtype;
	
	private String pageShowOnly="";
	
	public String getStrQuery() {
		return strQuery;
	}
	public void setStrQuery(String strQuery) {
		this.strQuery = strQuery;
	}
	public String getA_code() {
		return a_code;
	}
	public void setA_code(String a_code) {
		this.a_code = a_code;
	}
	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	public String getFieldSetDesc() {
		return fieldSetDesc;
	}
	public void setFieldSetDesc(String fieldSetDesc) {
		this.fieldSetDesc = fieldSetDesc;
	}
	public String getFieldSetId() {
		return fieldSetId;
	}
	public void setFieldSetId(String fieldSetId) {
		this.fieldSetId = fieldSetId;
	}
	public String getFieldid() {
		return fieldid;
	}
	public void setFieldid(String fieldid) {
		this.fieldid = fieldid;
	}
	public String getSn_left() {
		return sn_left;
	}
	public void setSn_left(String sn_left) {
		this.sn_left = sn_left;
	}
	public String getSnap_norm() {
		return snap_norm;
	}
	public void setSnap_norm(String snap_norm) {
		this.snap_norm = snap_norm;
	}
	public String getRight_value() {
		return right_value;
	}
	public void setRight_value(String right_value) {
		this.right_value = right_value;
	}
	public String getLeft_value() {
		return left_value;
	}
	public void setLeft_value(String left_value) {
		this.left_value = left_value;
	}
	public ArrayList getLeftlist() {
		return leftlist;
	}
	public void setLeftlist(ArrayList leftlist) {
		this.leftlist = leftlist;
	}
	public String getLeft_fields() {
		return left_fields;
	}
	public void setLeft_fields(String left_fields) {
		this.left_fields = left_fields;
	}
	public String getRight_fields() {
		return right_fields;
	}
	public void setRight_fields(String right_fields) {
		this.right_fields = right_fields;
	}
	public ArrayList getSetlist() {
		return setlist;
	}
	public void setSetlist(ArrayList setlist) {
		this.setlist = setlist;
	}
	public ArrayList getRightlist() {
		return rightlist;
	}
	public void setRightlist(ArrayList rightlist) {
		this.rightlist = rightlist;
	}
	public ArrayList getQueryfieldlist() {
		return queryfieldlist;
	}
	public void setQueryfieldlist(ArrayList queryfieldlist) {
		this.queryfieldlist = queryfieldlist;
	}
	
	public String getScantype() {
		return scantype;
	}
	public void setScantype(String scantype) {
		this.scantype = scantype;
	}
	public String getOrglike() {
		return orglike;
	}
	public void setOrglike(String orglike) {
		this.orglike = orglike;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getUnit_code_mess() {
		return unit_code_mess;
	}
	public void setUnit_code_mess(String unit_code_mess) {
		this.unit_code_mess = unit_code_mess;
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
	public String getQuerylike() {
		return querylike;
	}
	public void setQuerylike(String querylike) {
		this.querylike = querylike;
	}
	public ArrayList getScanfieldlist() {
		return scanfieldlist;
	}
	public void setScanfieldlist(ArrayList scanfieldlist) {
		this.scanfieldlist = scanfieldlist;
	}
	public String getPhoto_other_view() {
		return photo_other_view;
	}
	public void setPhoto_other_view(String photo_other_view) {
		this.photo_other_view = photo_other_view;
	}
	public String getEnsql() {
		return ensql;
	}
	public void setEnsql(String ensql) {
		this.ensql = ensql;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getHome() {
		return home;
	}
	public void setHome(String home) {
		this.home = home;
	}
	public String getCheck() {
		return check;
	}
	public void setCheck(String check) {
		this.check = check;
	}
	public String getUplevel() {
		return uplevel;
	}
	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}
	
	/**
	 * @return Returns the strsql.
	 */
	public String getStrsql() {
		return strsql;
	}
	/**
	 * @param strsql The strsql to set.
	 */
	public void setStrsql(String strsql) {
		this.strsql = strsql;
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
	/**
	 * @return Returns the flag.
	 */
	public String getFlag() {
		return flag;
	}
	/**
	 * @param flag The flag to set.
	 */
	public void setFlag(String flag) {
		this.flag = flag;
	}
    private String photoname;
	/**
	 * @return Returns the infosetlist.
	 */
	public ArrayList getInfosetlist() {
		return infosetlist;
	}
	/**
	 * @param infosetlist The infosetlist to set.
	 */
	public void setInfosetlist(ArrayList infosetlist) {
		this.infosetlist = infosetlist;
	}
	/**
	 * @return Returns the photoname.
	 */
	public String getPhotoname() {
		return photoname;
	}
	/**
	 * @param photoname The photoname to set.
	 */
	public void setPhotoname(String photoname) {
		this.photoname = photoname;
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
    private PaginationForm browseForm=new PaginationForm();    
    private ArrayList list=new ArrayList();

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub

		 this.setUserbase((String)this.getFormHM().get("userbase"));
		 this.setDbcond((String)this.getFormHM().get("dbcond"));
		 this.getBrowseForm().setList((ArrayList)this.getFormHM().get("detailinfolist"));
		 this.setA0100((String)this.getFormHM().get("a0100"));
		 this.setInfodetailfieldlist((ArrayList)this.getFormHM().get("infodetailfieldlist"));
	     this.setInfofieldlist((ArrayList)this.getFormHM().get("infofieldlist"));
	     this.setInfosetlist((ArrayList)this.getFormHM().get("infosetlist"));	   
	     this.setCond_str((String)this.getFormHM().get("cond_str"));
	     this.setCode((String)this.getFormHM().get("code"));
	     this.setKind((String)this.getFormHM().get("kind"));
	 	 this.setStrsql((String)this.getFormHM().get("strsql"));
	     this.setTreeCode((String)this.getFormHM().get("treeCode"));
	     this.setBrowsefields((ArrayList)this.getFormHM().get("browsefields"));
	     this.setColumns((String)this.getFormHM().get("columns"));
	     this.setState((String)this.getFormHM().get("state"));
	     this.setUplevel((String)this.getFormHM().get("uplevel"));
	     this.setSetname((String)this.getFormHM().get("setname"));
	     this.setCheck((String)this.getFormHM().get("check"));
	     this.setHome((String)this.getFormHM().get("home"));
	     this.setOrgtype((String)this.getFormHM().get("orgtype"));
	     this.setWhere_n((String)this.getFormHM().get("where_n"));
	     this.setEnsql((String)this.getFormHM().get("ensql"));
	     this.setPhoto_other_view((String)this.getFormHM().get("photo_other_view"));
	     String photolength = "";
	     if(photo_other_view!=null&&photo_other_view.length()>0){
	    	 photolength=Integer.toString(photo_other_view.split(",").length);
	     }
	     this.setPhotolength(photolength);
	     this.setIsShowCondition((String)this.getFormHM().get("isShowCondition"));
	     this.setQuerylike((String)this.getFormHM().get("querylike"));
	     this.setScanfieldlist((ArrayList)this.getFormHM().get("scanfieldlist"));
	     this.setCodemess((String)this.getFormHM().get("codemess"));
	     this.setUnit_code_mess((String)this.getFormHM().get("unit_code_mess"));
	     this.setQuery((String)this.getFormHM().get("query"));
	     this.setParentid((String)this.getFormHM().get("parentid"));
	     this.setCodesetid((String)this.getFormHM().get("codesetid"));
	     this.setOrglike((String)this.getFormHM().get("orglike"));
	     this.setQueryfieldlist((ArrayList)this.getFormHM().get("queryfieldlist"));

	     this.setBackdate((String)this.getFormHM().get("backdate"));
	     this.setBackname((String)this.getFormHM().get("backname"));
	     this.setUniqueitem((String)this.getFormHM().get("uniqueitem"));
	     this.setSelect_name((String)this.getFormHM().get("select_name"));
	     this.setQuerylike((String)this.getFormHM().get("querylike"));
	     this.setQuery((String)this.getFormHM().get("query"));

	     /*liwc*/
	     this.setRightlist((ArrayList)this.getFormHM().get("rightlist"));
	     this.setLeftlist((ArrayList)this.getFormHM().get("leftlist"));
	     this.setSetlist((ArrayList)this.getFormHM().get("setlist"));
	     this.setLeft_fields((String)this.getFormHM().get("left_fields"));
	     this.setLeft_value((String)this.getFormHM().get("left_value"));
	     this.setRight_fields((String)this.getFormHM().get("right_fields"));
	     this.setRight_value((String)this.getFormHM().get("right_value"));
	     this.setChk_v((HashMap)this.getFormHM().get("chk_v"));
	     this.setList1((ArrayList)this.getFormHM().get("list1"));
	     this.setChklist((ArrayList)this.getFormHM().get("chklist"));
	     this.setIfbackup((String)this.getFormHM().get("ifbackup"));
	     this.setSumtype((String)this.getFormHM().get("sumtype"));
	     
	     this.setPageShowOnly((String) this.getFormHM().get("pageShowOnly"));
  }

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("selectedlist",(ArrayList)this.getBrowseForm().getSelectedList());
		this.getFormHM().put("userbase",userbase);
		this.getFormHM().put("setname",setname);
		this.getFormHM().put("a0100",a0100);
        this.getFormHM().put("code", code);
        this.getFormHM().put("kind", this.getKind());
        this.getFormHM().put("check", this.getCheck());
        this.getFormHM().put("home",this.getHome());
        this.getFormHM().put("expr", this.getExpr());
        this.getFormHM().put("factor", this.getFactor());
        this.getFormHM().put("querylike", this.getQuerylike());
        this.getFormHM().put("scanfieldlist", this.getScanfieldlist());
        this.getFormHM().put("query", this.getQuery());
        this.getFormHM().put("parentid", this.parentid);
		this.getFormHM().put("codesetid", this.codesetid);
		this.getFormHM().put("orglike", this.getOrglike());
		this.getFormHM().put("queryfieldlist", this.getQueryfieldlist());
		this.getFormHM().put("backdate", backdate);
		this.getFormHM().put("backname", backname);
		//liwc
		this.getFormHM().put("snap_norm", this.getSnap_norm());
		this.getFormHM().put("sn_left", this.getSn_left());
		this.getFormHM().put("uniqueitem", uniqueitem);
		this.getFormHM().put("select_name", this.getSelect_name());
		this.getFormHM().put("strQuery", this.getStrQuery());
		this.getFormHM().put("orgbackdate", orgbackdate);
		this.getFormHM().put("pageShowOnly", this.getPageShowOnly());
	}

	/**
	 * @return Returns the list.
	 */
	public ArrayList getList() {
		return list;
	}
	/**
	 * @param list The list to set.
	 */
	public void setList(ArrayList list) {
		this.list = list;
	}
	/**
	 * @return Returns the userbase.
	 */
	public String getUserbase() {
		return userbase;
	}
	/**
	 * @param userbase The userbase to set.
	 */
	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}	
	
	/**
	 * @return Returns the infodetailfieldlist.
	 */
	public ArrayList getInfodetailfieldlist() {
		return infodetailfieldlist;
	}
	/**
	 * @param infodetailfieldlist The infodetailfieldlist to set.
	 */
	public void setInfodetailfieldlist(ArrayList infodetailfieldlist) {
		this.infodetailfieldlist = infodetailfieldlist;
	}
	/**
	 * @return Returns the infofieldlist.
	 */
	public ArrayList getInfofieldlist() {
		return infofieldlist;
	}
	/**
	 * @param infofieldlist The infofieldlist to set.
	 */
	public void setInfofieldlist(ArrayList infofieldlist) {
		this.infofieldlist = infofieldlist;
	}
	/**
	 * @return Returns the selfInfoForm.
	 */
	public PaginationForm getBrowseForm() {
		return browseForm;
	}
	/**
	 * @param selfInfoForm The selfInfoForm to set.
	 */
	public void setBrowseForm(PaginationForm browseForm) {
		this.browseForm = browseForm;
	}
	/**
	 * @return Returns the a0100.
	 */
	public String getA0100() {
		return a0100;
	}
	/**
	 * @param a0100 The a0100 to set.
	 */
	public void setA0100(String a0100) {
		this.a0100 = a0100;
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
	 * @return Returns the cond_str.
	 */
	public String getCond_str() {
		return cond_str;
	}
	/**
	 * @param cond_str The cond_str to set.
	 */
	public void setCond_str(String cond_str) {
		this.cond_str = cond_str;
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
	  @Override
      public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        if("/workbench/browse/history/showinfo".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
        {
            if(this.getPagination()!=null)
              this.getPagination().firstPage();//?
        }
        
        return super.validate(arg0, arg1);
    }
	
	public ArrayList getBrowsefields() {
		return this.browsefields;
	}
	public void setBrowsefields(ArrayList browsefields) {
		this.browsefields = browsefields;
	}
	public String getColumns() {
		return this.columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getOrgtype() {
		return orgtype;
	}
	public void setOrgtype(String orgtype) {
		this.orgtype = orgtype;
	}
	public String getWhere_n() {
		return where_n;
	}
	public void setWhere_n(String where_n) {
		this.where_n = where_n;
	}
	
	public String getExpr() {
		return expr;
	}
	public void setExpr(String expr) {
		this.expr = expr;
	}
	public String getFactor() {
		return factor;
	}
	public void setFactor(String factor) {
		this.factor = factor;
	}
	
	public String getOrgflag() {
		return orgflag;
	}
	public void setOrgflag(String orgflag) {
		this.orgflag = orgflag;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	public String getCodesetid() {
		return codesetid;
	}
	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}
	public String getReturn_codeid() {
		return return_codeid;
	}
	public void setReturn_codeid(String return_codeid) {
		this.return_codeid = return_codeid;
	}
	public String getSelect_name() {
		return select_name;
	}
	public void setSelect_name(String select_name) {
		this.select_name = select_name;
	}
	public String getDbcond() {
		return dbcond;
	}
	public void setDbcond(String dbcond) {
		this.dbcond = dbcond;
	}
	public String getBackdate() {
		return backdate;
	}
	public void setBackdate(String backdate) {
		this.backdate = backdate;
	}
	public String getIfbackup() {
		return ifbackup;
	}
	public void setIfbackup(String ifbackup) {
		this.ifbackup = ifbackup;
	}
	public String getUniqueitem() {
		return uniqueitem;
	}
	public void setUniqueitem(String uniqueitem) {
		this.uniqueitem = uniqueitem;
	}
	public ArrayList getList1() {
		return list1;
	}
	public void setList1(ArrayList list1) {
		this.list1 = list1;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOrgbackdate() {
		return orgbackdate;
	}
	public void setOrgbackdate(String orgbackdate) {
		this.orgbackdate = orgbackdate;
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	public HashMap getChk_v() {
		return chk_v;
	}
	public void setChk_v(HashMap chk_v) {
		this.chk_v = chk_v;
	}
	public ArrayList getChklist() {
		return chklist;
	}
	public void setChklist(ArrayList chklist) {
		this.chklist = chklist;
	}
	public String getSumtype() {
		return sumtype;
	}
	public void setSumtype(String sumtype) {
		this.sumtype = sumtype;
	}
	public String getBackname() {
		return backname;
	}
	public void setBackname(String backname) {
		this.backname = backname;
	}
	public String getPhotolength() {
		return photolength;
	}
	public void setPhotolength(String photolength) {
		this.photolength = photolength;
	}
    public String getPageShowOnly() {
        return pageShowOnly;
    }
    public void setPageShowOnly(String pageShowOnly) {
        this.pageShowOnly = pageShowOnly;
    }
	
	
}
