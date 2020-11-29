/*
 * Created on 2005-12-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.pos;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PosBusinessForm extends FrameForm {

	private String treeCode;
	private String codesetid;
	private String codeitemid;
	private String codeitemdesc;
	private String parentid;
	private String childid;
	private String flag;
	
	private String a_code;
	private String len;
	private String labelmessage;
	private String first;
	private String isrefresh;
	private PaginationForm codeitemForm=new PaginationForm();
	private String code;
	private String codesetdesc;
	private String issetpos;                /*是否设置了职务编码*/
	private ArrayList codelist;
	private String codeitem;
	private String cflag;  //判断模式 0用户 1开发商
	private String valueflag; // codeset 表中的 status 参数
	private String validateflag; //时间标识代码类（0 不是|1是） 2009-10-10xuj
	private String invalid;//代码项状态 =0无效 =1有效 2009-10-10xuj
	private String start_date;
	private String end_date;
	private String islevel;////当属于级别设置时控制在jsp不显示职务代码列 yes 不显示
	private String corcode;//职务代码（转换代码）
	private String param;// 区分是显示 职务编码、职务级别设置、岗/职位编码或岗/职位级别设置
	private String a0000;
	private String returnvalue;
	private String fromflag;//区分入口：=1或无值 从代码维护进入，=2从能力素质模型中进入，=3从培训知识点进入
	private String object_type;//区分素质模型中的不同模块（主要用于返回不同模块）
	private String historyDate;//素质模型中的历史时点
	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setTreeCode((String)this.getFormHM().get("treeCode"));
		this.setLabelmessage((String)this.getFormHM().get("labelmessage"));
	    this.setLen((String)this.getFormHM().get("len"));
	    this.setFirst((String)this.getFormHM().get("first"));
	    this.setCodeitemid((String)this.getFormHM().get("codeitemid"));
	    this.setCodeitemdesc((String)this.getFormHM().get("codeitemdesc"));
	    this.setIsrefresh((String)this.getFormHM().get("isrefresh"));
	    this.setCodesetid((String)this.getFormHM().get("codesetid"));
	    this.getCodeitemForm().setList((ArrayList)this.getFormHM().get("codeitemlist"));
	    this.setCode((String)this.getFormHM().get("code"));
	    this.setCodesetdesc((String)this.getFormHM().get("codesetdesc"));
	    this.setIssetpos((String)this.getFormHM().get("issetpos"));
	    this.setCodelist((ArrayList)this.getFormHM().get("codelist"));
	    this.setCodeitem((String)this.getFormHM().get("codeitem"));
	    this.setCflag((String)this.getFormHM().get("cflag"));
	    this.setValueflag((String)this.getFormHM().get("valueflag"));
	    this.setValidateflag((String)this.getFormHM().get("validateflag"));
	    this.setInvalid((String)this.getFormHM().get("invalid"));
	    this.setIslevel((String)this.getFormHM().get("islevel"));
	    this.setCorcode((String)this.getFormHM().get("corcode"));
	    this.setParam((String)this.getFormHM().get("param"));
	    this.setFromflag((String)this.getFormHM().get("fromflag"));
        this.setObject_type((String)this.getFormHM().get("object_type"));
        this.setHistoryDate((String)this.getFormHM().get("historyDate"));
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("historyDate", this.getHistoryDate());
		this.getFormHM().put("fromflag", this.getFromflag());
		this.getFormHM().put("object_type",this.getObject_type());
		this.getFormHM().put("selectedlist",this.getCodeitemForm().getSelectedList());
        this.getFormHM().put("a_code",a_code);
		this.getFormHM().put("codeitemid",codeitemid);
		this.getFormHM().put("codeitemdesc",codeitemdesc);
		this.getFormHM().put("first",first);
		this.getFormHM().put("codesetid",codesetid);
		this.getFormHM().put("codeitem",codeitem);
		this.getFormHM().put("invalid", this.getInvalid());
		this.getFormHM().put("start_date", this.getStart_date());
		this.getFormHM().put("end_date", this.getEnd_date());
		this.getFormHM().put("validateflag", this.getValidateflag());
		this.getFormHM().put("codesetdesc", this.getCodesetdesc());
		this.getFormHM().put("corcode",this.getCorcode());
		this.getFormHM().put("param", param);
		this.getFormHM().put("a0000",a0000);
	}

	
	
	@Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		// TODO Auto-generated method stub
		if("/pos/posbusiness/searchposbusinesslist".equalsIgnoreCase(mapping.getPath())&&request.getParameter("b_query")!=null&& "link".equals(request.getParameter("b_query"))){
			//系统管理，代码体系，调整代码位置后，默认刷新到首页，不对   jingq upd 2014.12.06
			//codeitemForm.getPagination().firstPage();
			//查看代码项默认显示到第一页  jingq add
			if(request.getParameter("a_code")!=null){
				codeitemForm.getPagination().firstPage();
			}
		}
		super.reset(mapping, request);
	}

	@Override
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
		if("/pos/posbusiness/searchposbusinesslist".equals(mapping.getPath())&&request.getParameter("b_save")!=null){
			//左侧功能菜单进入的页面 提示信息改为不显示按钮 hej upd 2015.10.20
			request.setAttribute("targetWindow", "0");//0不显示按钮 |1关闭|默认为返回
        }
		return super.validate(mapping, request);
	}

	/**
	 * @return Returns the childid.
	 */
	public String getChildid() {
		return childid;
	}
	/**
	 * @param childid The childid to set.
	 */
	public void setChildid(String childid) {
		this.childid = childid;
	}
	/**
	 * @return Returns the codeitemdesc.
	 */
	public String getCodeitemdesc() {
		return codeitemdesc;
	}
	/**
	 * @param codeitemdesc The codeitemdesc to set.
	 */
	public void setCodeitemdesc(String codeitemdesc) {
		this.codeitemdesc = codeitemdesc;
	}
	/**
	 * @return Returns the codeitemid.
	 */
	public String getCodeitemid() {
		return codeitemid;
	}
	/**
	 * @param codeitemid The codeitemid to set.
	 */
	public void setCodeitemid(String codeitemid) {
		this.codeitemid = codeitemid;
	}
	/**
	 * @return Returns the codesetid.
	 */
	public String getCodesetid() {
		return codesetid;
	}
	/**
	 * @param codesetid The codesetid to set.
	 */
	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
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
	/**
	 * @return Returns the parentid.
	 */
	public String getParentid() {
		return parentid;
	}
	/**
	 * @param parentid The parentid to set.
	 */
	public void setParentid(String parentid) {
		this.parentid = parentid;
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
	 * @return Returns the first.
	 */
	public String getFirst() {
		return first;
	}
	/**
	 * @param first The first to set.
	 */
	public void setFirst(String first) {
		this.first = first;
	}
	/**
	 * @return Returns the isrefresh.
	 */
	public String getIsrefresh() {
		return isrefresh;
	}
	/**
	 * @param isrefresh The isrefresh to set.
	 */
	public void setIsrefresh(String isrefresh) {
		this.isrefresh = isrefresh;
	}
	/**
	 * @return Returns the labelmessage.
	 */
	public String getLabelmessage() {
		return labelmessage;
	}
	/**
	 * @param labelmessage The labelmessage to set.
	 */
	public void setLabelmessage(String labelmessage) {
		this.labelmessage = labelmessage;
	}
	/**
	 * @return Returns the len.
	 */
	public String getLen() {
		return len;
	}
	/**
	 * @param len The len to set.
	 */
	public void setLen(String len) {
		this.len = len;
	}
	/**
	 * @return Returns the codeitemForm.
	 */
	public PaginationForm getCodeitemForm() {
		return codeitemForm;
	}
	/**
	 * @param codeitemForm The codeitemForm to set.
	 */
	public void setCodeitemForm(PaginationForm codeitemForm) {
		this.codeitemForm = codeitemForm;
	}
	/**
	 * @return Returns the a_code.
	 */
	public String getA_code() {
		return a_code;
	}
	/**
	 * @param a_code The a_code to set.
	 */
	public void setA_code(String a_code) {
		this.a_code = a_code;
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

	public String getCodesetdesc() {
		return codesetdesc;
	}

	public void setCodesetdesc(String codesetdesc) {
		this.codesetdesc = codesetdesc;
	}

	public String getIssetpos() {
		return issetpos;
	}

	public void setIssetpos(String issetpos) {
		this.issetpos = issetpos;
	}

	public ArrayList getCodelist() {
		return codelist;
	}

	public void setCodelist(ArrayList codelist) {
		this.codelist = codelist;
	}
	
	public String getCodeitem() {
		return codeitem;
	}
	
	public void setCodeitem(String codeitem) {
		this.codeitem = codeitem;
	}
	
	public String getCflag() {
		return cflag;
	}

	public void setCflag(String cflag) {
		this.cflag = cflag;
	}
	
	public String getValueflag() {
		return valueflag;
	}

	public void setValueflag(String valueflag) {
		this.valueflag = valueflag;
	}

	public String getValidateflag() {
		return validateflag;
	}

	public void setValidateflag(String validateflag) {
		this.validateflag = validateflag;
	}

	public String getInvalid() {
		return invalid;
	}

	public void setInvalid(String invalid) {
		this.invalid = invalid;
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

	public String getIslevel() {
		return islevel;
	}

	public void setIslevel(String islevel) {
		this.islevel = islevel;
	}

	public String getCorcode() {
		return corcode;
	}

	public void setCorcode(String corcode) {
		this.corcode = corcode;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

	public String getA0000() {
		return a0000;
	}

	public void setA0000(String a0000) {
		this.a0000 = a0000;
	}

	public String getFromflag() {
		return fromflag;
	}

	public void setFromflag(String fromflag) {
		this.fromflag = fromflag;
	}

	public String getObject_type() {
		return object_type;
	}

	public void setObject_type(String object_type) {
		this.object_type = object_type;
	}

	public String getHistoryDate() {
		return historyDate;
	}

	public void setHistoryDate(String historyDate) {
		this.historyDate = historyDate;
	}
}
