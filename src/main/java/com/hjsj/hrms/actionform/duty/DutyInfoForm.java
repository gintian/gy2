/*
 * Created on 2005-8-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.duty;

import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DutyInfoForm extends FrameForm {
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
    private int current=1;
    private String setprv;
    private String orgtype;
    private String codeitemdesc;
    private String filetitle;
	private FormFile mediafile;
	private String filesort;
	private String sortcond;
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
	private String parentid="";
	private String pos_code_field="";//岗位代码
	private String ps_c_level_code="";//岗位级别
	private String fieldstr="";	
	private String cardID="";
	private String uplevel="";
	//撤销结构人的转移到的机构列表
	private String[] right_fields;
	private String dbpre;
    private String edit_flag="";
	private ArrayList dbprelist=new ArrayList();
	private ArrayList bolishlist=new ArrayList();
	private ArrayList movepersons=new ArrayList();
	private String[] left_fields;
	private String ishavepersonmessage;	
	//撤销的机构名
	private String bolishorgname;
	private String tarorgname;
	private String tarorgid;
	private String end_date;
	private ArrayList msgb0110=new ArrayList();
	//划转
	private ArrayList transferorglist=new ArrayList();
	private String transfercodeitemid;

	
	/****新增****/
	private String codeitemid;
	private String corcode;
	private String len;
    private String grade;
    private String first;
    private String return_codeid="";
    private String root = "";
    private String ps_card_attach;//xuj 2010-4-20 是否显示岗位附件
    
    
    //返回到编制管理界面所需参数 2010-5-1许建
    private String b0110;
    private String setid;
    private String a_code;
    private String infor;
    private String unit_type;
    private String nextlevel;
    
    private String ps_c_sduty; //是否显示引用基准岗位
    
    
    private String returnvalue1;
    
    private String ps_superior;
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
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public String getPos_code_field() {
		return pos_code_field;
	}
	public void setPos_code_field(String pos_code_field) {
		this.pos_code_field = pos_code_field;
	}
	public String getCardID() {
		return cardID;
	}
	public void setCardID(String cardID) {
		this.cardID = cardID;
	}
	public ArrayList getFieldList() {
		return fieldList;
	}
	public void setFieldList(ArrayList fieldList) {
		this.fieldList = fieldList;
	}
	public ArrayList getSelectfieldlist() {
		return selectfieldlist;
	}
	public void setSelectfieldlist(ArrayList selectfieldlist) {
		this.selectfieldlist = selectfieldlist;
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
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	public String getSortcond() {
		return sortcond;
	}
	public void setSortcond(String sortcond) {
		this.sortcond = sortcond;
	}
	public String getFilesort() {
		return filesort;
	}
	public void setFilesort(String filesort) {
		this.filesort = filesort;
	}
	public String getFiletitle() {
		return filetitle;
	}
	public void setFiletitle(String filetitle) {
		this.filetitle = filetitle;
	}
	public FormFile getMediafile() {
		return mediafile;
	}
	public void setMediafile(FormFile mediafile) {
		this.mediafile = mediafile;
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
	/**
	 * @param setprv The setprv to set.
	 */
	public void setSetprv(String setprv) {
		this.setprv = setprv;
	}
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
	private String changemsg="";
	private ArrayList childfielditemlist=new ArrayList();
	private PaginationForm orgInfoForm=new PaginationForm();   
	@Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		// TODO Auto-generated method stub
		super.reset(mapping, request);
		try{
			if(this.getChildfielditemlist()!=null)
				for(int i=0;i<this.getChildfielditemlist().size();i++){
					FieldItem fielditem = (FieldItem)this.getChildfielditemlist().get(i);
					fielditem.setValue("");
					fielditem.setViewvalue("");
				}
		}catch(Exception e){
			e.printStackTrace();
		}
		this.left_fields=null;
	}
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
	    this.setCodeitemdesc((String)this.getFormHM().get("codeitemdesc"));
	    this.setFiletitle((String)this.getFormHM().get("filetitle"));
	    this.setSortcond((String)this.getFormHM().get("sortcond"));
	    this.setSqlstr((String)this.getFormHM().get("sqlstr"));
	    this.setColumnstr((String)this.getFormHM().get("columnstr"));
	    this.setWherestr((String)this.getFormHM().get("wherestr"));
	    this.setOrderby((String)this.getFormHM().get("orderby"));
	    this.setFieldList((ArrayList)this.getFormHM().get("fieldList"));
	    this.setSelectfieldlist((ArrayList)this.getFormHM().get("selectfieldlist"));
	    this.setCodemess((String)this.getFormHM().get("codemess"));
	    this.setIsShowCondition((String)this.getFormHM().get("isShowCondition"));
	    this.setQuerylike((String)this.getFormHM().get("querylike"));
	    this.setOrglike((String)this.getFormHM().get("orglike"));
	    this.setCardID((String)this.getFormHM().get("cardID"));
	    this.setPos_code_field((String)this.getFormHM().get("pos_code_field"));
	    this.setQuery((String)this.getFormHM().get("query"));
	    this.setReturnvalue((String)this.getFormHM().get("returnvalue"));
	    this.setParentid((String)this.getFormHM().get("parentid"));
	    this.setPos_code_field((String)this.getFormHM().get("pos_code_field"));
	    this.setPs_c_level_code((String)this.getFormHM().get("ps_c_level_code"));
	    this.setFieldstr((String)this.getFormHM().get("fieldstr"));	 
	    this.setChangemsg((String)this.getFormHM().get("changemsg"));
	    
	    this.setBolishlist((ArrayList)this.getFormHM().get("bolishlist"));
	    this.setDbprelist((ArrayList)this.getFormHM().get("dbprelist"));
	    this.setIshavepersonmessage((String)this.getFormHM().get("ishavepersonmessage"));	    
	    this.setRight_fields((String[])this.getFormHM().get("right_fields"));
	    if(this.getFormHM().get("movepersons")!=null)
	         this.setMovepersons((ArrayList)this.getFormHM().get("movepersons"));
	    this.setEnd_date((String)this.getFormHM().get("end_date"));
	    this.setEdit_flag((String)this.getFormHM().get("edit_flag"));
	    
	    this.setCodeitemid((String)this.getFormHM().get("codeitemid"));
	    this.setCorcode((String)this.getFormHM().get("corcode"));
	    this.setLen((String)this.getFormHM().get("len"));
	    this.setGrade((String)this.getFormHM().get("grade"));
	    this.setFirst((String)this.getFormHM().get("first"));
	    this.setChildfielditemlist((ArrayList)this.getFormHM().get("childfielditemlist"));
	    this.setUplevel((String)this.getFormHM().get("uplevel"));
	    this.setRoot((String) this.getFormHM().get("root"));
	    this.setReturn_codeid((String) this.getFormHM().get("return_codeid"));
	    this.setPs_card_attach((String)this.getFormHM().get("ps_card_attach"));
	    
	    this.setTransferorglist((ArrayList)this.getFormHM().get("transferorglist"));
	    this.setMsgb0110((ArrayList)this.getFormHM().get("msgb0110"));
	    
	    if(this.getFormHM().get("ps_c_sduty")!=null)
	        this.setPs_c_sduty(this.getFormHM().get("ps_c_sduty").toString());
	    
	    this.setPs_superior((String)this.getFormHM().get("ps_superior"));
	    
	    this.setTarorgname((String)getFormHM().get("tarorgname"));
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub	
	 this.getFormHM().put("setname",setname);
	 this.getFormHM().put("code",code);
	 this.getFormHM().put("i9999",i9999);
	 this.getFormHM().put("infofieldlist",infofieldlist);
	 this.getFormHM().put("action",action);
	 this.getFormHM().put("kind",kind);
	 this.getFormHM().put("selectedlist",(List)this.getOrgInfoForm().getSelectedList());
	 this.getFormHM().put("edittype",edittype);
	 this.getFormHM().put("orgtype",this.getOrgtype());
	 this.getFormHM().put("filetitle",filetitle);
	 this.getFormHM().put("file",mediafile);
	 this.getFormHM().put("filesort",filesort);
	 this.getFormHM().put("orgtype",this.getOrgtype());
	 this.getFormHM().put("selectfieldlist", this.getSelectfieldlist());	
	 this.getFormHM().put("isShowCondition", this.isShowCondition);
	 this.getFormHM().put("querylike", this.getQuerylike());
	 this.getFormHM().put("orglike", this.getOrglike());
	 this.getFormHM().put("query", this.getQuery());
	 this.getFormHM().put("returnvalue", this.getReturnvalue());
	 this.getFormHM().put("parentid", this.getParentid());
	 this.getFormHM().put("fieldstr", this.getFieldstr());
	 this.getFormHM().put("cardID", this.getCardID());
	  if(this.getPagination()!=null)
		   this.getFormHM().put("selectedinfolist",(ArrayList)this.getPagination().getSelectedList());
	  //撤销人的转移到的结构
      this.getFormHM().put("persontoorg",this.tarorgid);
	  this.getFormHM().put("persons",this.left_fields);
      this.getFormHM().put("bolishlist",bolishlist);
      this.getFormHM().put("dbpre",dbpre);
      this.getFormHM().put("bolishorgname",bolishorgname);
      this.getFormHM().put("right_fields",this.getRight_fields());
      this.getFormHM().put("end_date", this.getEnd_date());
      this.getFormHM().put("edit_flag", this.getEdit_flag());
      this.getFormHM().put("codeitemid", this.getCodeitemid());
      this.getFormHM().put("corcode", this.getCorcode());
      this.getFormHM().put("len", this.getLen());
      this.getFormHM().put("grade",this.getGrade());
      this.getFormHM().put("first", this.getFirst());
      this.getFormHM().put("childfielditemlist", this.getChildfielditemlist());
      this.getFormHM().put("codemess", this.getCodemess());
      this.getFormHM().put("return_codeid", this.getReturn_codeid());
      this.getFormHM().put("ps_card_attach", ps_card_attach);
      this.getFormHM().put("movepersons", movepersons);
      
      this.getFormHM().put("transferorglist", transferorglist);
      this.getFormHM().put("transfercodeitemid", transfercodeitemid);
      this.getFormHM().put("msgb0110", msgb0110);
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
	/** c
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
		 if("/workbench/dutyinfo/editorgdetailinfodata".equals(arg0.getPath())&&(arg1.getParameter("b_return")!=null || arg1.getParameter("b_save")!=null))
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
        if("/workbench/dutyinfo/searchdetailinfolist".equals(arg0.getPath())&& arg1.getParameter("b_delete")!=null)
        {
            if(this.orgInfoForm.getPagination()!=null)
            	current=this.orgInfoForm.getPagination().getCurrent();
        }	
        if("/workbench/dutyinfo/searchdetailinfolist".equals(arg0.getPath())&& arg1.getParameter("b_search")!=null)
        {
            if(this.orgInfoForm.getPagination()!=null)
            {
            	this.orgInfoForm.getPagination().firstPage();
            	current=this.orgInfoForm.getPagination().getCurrent();
            }
            	
        }	
        if("/workbench/dutyinfo/searchdutyinfo".equals(arg0.getPath())&& arg1.getParameter("b_search")!=null)
        {
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();//?           
            this.setCode("");
            this.setKind("");
            this.getFormHM().put("code", "");
            this.getFormHM().put("kind", "");
        }
        if("/workbench/dutyinfo/searchdutyinfodata".equals(arg0.getPath())&& arg1.getParameter("b_search")!=null)
        {
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();//? 
            this.setQuery("0");
	          this.getFormHM().put("query", "");
        }
        if("/workbench/dutyinfo/searchdutyinfodata".equals(arg0.getPath())&& arg1.getParameter("b_query")!=null)
        {
            //if(this.getPagination()!=null)
            	//this.getPagination().firstPage();//?             
        }
        return super.validate(arg0, arg1);
   }
	public String getCodeitemdesc() {
		return codeitemdesc;
	}
	public void setCodeitemdesc(String codeitemdesc) {
		this.codeitemdesc = codeitemdesc;
	}
	public int getCurrent() {
		return current;
	}
	public void setCurrent(int current) {
		this.current = current;
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
	public String getColumnstr() {
		return columnstr;
	}
	public void setColumnstr(String columnstr) {
		this.columnstr = columnstr;
	}
	public String getOrderby() {
		return orderby;
	}
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	public String getPs_c_level_code() {
		return ps_c_level_code;
	}
	public void setPs_c_level_code(String ps_c_level_code) {
		this.ps_c_level_code = ps_c_level_code;
	}
	public String getFieldstr() {
		return fieldstr;
	}
	public void setFieldstr(String fieldstr) {
		this.fieldstr = fieldstr;
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
	public ArrayList getDbprelist() {
		return dbprelist;
	}
	public void setDbprelist(ArrayList dbprelist) {
		this.dbprelist = dbprelist;
	}
	public ArrayList getBolishlist() {
		return bolishlist;
	}
	public void setBolishlist(ArrayList bolishlist) {
		this.bolishlist = bolishlist;
	}
	public ArrayList getMovepersons() {
		return movepersons;
	}
	public void setMovepersons(ArrayList movepersons) {
		this.movepersons = movepersons;
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
	public String getEdit_flag() {
		return edit_flag;
	}
	public void setEdit_flag(String edit_flag) {
		this.edit_flag = edit_flag;
	}
	public String getCodeitemid() {
		return codeitemid;
	}
	public void setCodeitemid(String codeitemid) {
		this.codeitemid = codeitemid;
	}
	public String getCorcode() {
		return corcode;
	}
	public void setCorcode(String corcode) {
		this.corcode = corcode;
	}
	public String getLen() {
		return len;
	}
	public void setLen(String len) {
		this.len = len;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getFirst() {
		return first;
	}
	public void setFirst(String first) {
		this.first = first;
	}
	public String getChangemsg() {
		return changemsg;
	}
	public void setChangemsg(String changemsg) {
		this.changemsg = changemsg;
	}
	public ArrayList getChildfielditemlist() {
		return childfielditemlist;
	}
	public void setChildfielditemlist(ArrayList childfielditemlist) {
		this.childfielditemlist = childfielditemlist;
	}
	public String getUplevel() {
		return uplevel;
	}
	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}
	public String getPs_card_attach() {
		return ps_card_attach;
	}
	public void setPs_card_attach(String ps_card_attach) {
		this.ps_card_attach = ps_card_attach;
	}
	public String getB0110() {
		return b0110;
	}
	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}
	public String getSetid() {
		return setid;
	}
	public void setSetid(String setid) {
		this.setid = setid;
	}
	public String getA_code() {
		return a_code;
	}
	public void setA_code(String a_code) {
		this.a_code = a_code;
	}
	public String getInfor() {
		return infor;
	}
	public void setInfor(String infor) {
		this.infor = infor;
	}
	public String getUnit_type() {
		return unit_type;
	}
	public void setUnit_type(String unit_type) {
		this.unit_type = unit_type;
	}
	public String getNextlevel() {
		return nextlevel;
	}
	public void setNextlevel(String nextlevel) {
		this.nextlevel = nextlevel;
	}
	public String getReturnvalue1() {
		return returnvalue1;
	}
	public void setReturnvalue1(String returnvalue1) {
		this.returnvalue1 = returnvalue1;
	}
	public ArrayList getTransferorglist() {
		return transferorglist;
	}
	public void setTransferorglist(ArrayList transferorglist) {
		this.transferorglist = transferorglist;
	}
	public String getTransfercodeitemid() {
		return transfercodeitemid;
	}
	public void setTransfercodeitemid(String transfercodeitemid) {
		this.transfercodeitemid = transfercodeitemid;
	}
	public ArrayList getMsgb0110() {
		return msgb0110;
	}
	public void setMsgb0110(ArrayList msgb0110) {
		this.msgb0110 = msgb0110;
	}
	public String getPs_superior() {
		return ps_superior;
	}
	public void setPs_superior(String ps_superior) {
		this.ps_superior = ps_superior;
	}
	public String getPs_c_sduty() {
		return ps_c_sduty;
	}
	public void setPs_c_sduty(String psCSduty) {
		ps_c_sduty = psCSduty;
	} 
}
