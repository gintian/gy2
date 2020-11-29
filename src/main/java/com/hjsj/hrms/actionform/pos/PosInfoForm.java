package com.hjsj.hrms.actionform.pos;

import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class PosInfoForm extends FrameForm {
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
	private String first="";
	
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
	    
	    
	    this.setFirst((String)this.getFormHM().get("first"));
	    this.setChildfielditemlist((ArrayList)this.getFormHM().get("childfielditemlist"));
	    this.setUplevel((String)this.getFormHM().get("uplevel"));	
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
	
}