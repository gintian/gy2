package com.hjsj.hrms.actionform.general.statics;

import com.hrms.hjsj.sys.Constant;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
public class StaticForm extends FrameForm {
	/**信息群标识*/
	private String infor_Flag="2";
    /**记录集名称*/
    private String setname="A01";
    /**构库标识*/
    private String usedflag=Integer.toString(Constant.USED_FIELD_SET);
    /**信息种类，对人员信息查询则选全部子集*/
    private String domainflag=Integer.toBinaryString(Constant.ALL_FIELD_SET);
    private String dbcond;
    private String strsql;
    private String where_str;
    private String columns;
    private String order_by;
    private String a0100;
    private String formatstr;
    private String find;
    private String returnvalue;
    private String viewuserbases;
    private String userbases;
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
	 * @return Returns the order_by.
	 */
	public String getOrder_by() {
		return order_by;
	}
	/**
	 * @param order_by The order_by to set.
	 */
	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}
	/**
	 * @return Returns the columns.
	 */
	public String getColumns() {
		return columns;
	}
	/**
	 * @param columns The columns to set.
	 */
	public void setColumns(String columns) {
		this.columns = columns;
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
	 * @return Returns the where_str.
	 */
	public String getWhere_str() {
		return where_str;
	}
	/**
	 * @param where_str The where_str to set.
	 */
	public void setWhere_str(String where_str) {
		this.where_str = where_str;
	}
	/**
	 * @return Returns the dbcond.
	 */
	public String getDbcond() {
		return dbcond;
	}
	/**
	 * @param dbcond The dbcond to set.
	 */
	public void setDbcond(String dbcond) {
		this.dbcond = dbcond;
	}
    /**应用库表前缀*/
    private String dbpre;
    /**字段名称*/
    private String itemdesc;
    /**选择条件*/
    private String select;
	/**
	 * @return Returns the select.
	 */
	public String getSelect() {
		return select;
	}
	/**
	 * @param select The select to set.
	 */
	public void setSelect(String select) {
		this.select = select;
	}
	/**
	 * @return Returns the itemdesc.
	 */
	public String getItemdesc() {
		return itemdesc;
	}
	/**
	 * @param itemdesc The itemdesc to set.
	 */
	public void setItemdesc(String itemdesc) {
		this.itemdesc = itemdesc;
	}
    /**使用标志,公用,还是私用*/
    private String used_flag;
	/**人员库列表*/
    private ArrayList dblist=new ArrayList();
    
    
    /**花名册指标列表*/
    private ArrayList fieldlist;
    private PaginationForm staticForm=new PaginationForm();
    
	@Override
    public void outPutFormHM() {
		this.setDbcond((String)this.getFormHM().get("dbcond"));
		this.setDblist((ArrayList)this.getFormHM().get("dblist"));
		this.setInfor_Flag((String)this.getFormHM().get("inforkind"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.getStaticForm().setList((ArrayList)this.getFormHM().get("valuelist"));
		this.setDbpre((String)this.getFormHM().get("dbpre"));
		this.setItemdesc((String)this.getFormHM().get("itemdesc"));
		this.setSelect((String)this.getFormHM().get("select"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setWhere_str((String)this.getFormHM().get("where_str"));
        this.setFormatstr((String)this.getFormHM().get("formatstr")); 
        this.setUserbases((String)this.getFormHM().get("userbases"));
        this.setViewuserbases((String)this.getFormHM().get("viewuserbases"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("dbpre",this.getDbpre());		
		this.getFormHM().put("used_flag",this.getUsed_flag());
		this.getFormHM().put("inforkind",this.getInfor_Flag());
		this.getFormHM().put("valuelist",this.getStaticForm().getList());
		this.getFormHM().put("find",(String)this.getFind());
		this.getFormHM().put("infor_Flag",this.getInfor_Flag());
		this.getFormHM().put("where_str",where_str);
		this.getFormHM().put("userbases", userbases);
	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/general/static/singlestatic/showspecial".equals(arg0.getPath())&&arg1.getParameter("b_show")!=null)
        {
	    	if(this.getPagination()!=null)
	              this.getPagination().firstPage();//?
        }
		return super.validate(arg0, arg1);
	}
    
	public String getDbpre() {
		return dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}



	public String getDomainflag() {
		return domainflag;
	}

	public void setDomainflag(String domainflag) {
		this.domainflag = domainflag;
	}

	public String getUsedflag() {
		return usedflag;
	}

	public void setUsedflag(String usedflag) {
		this.usedflag = usedflag;
	}

	public String getSetname() {
		return setname;
	}

	public void setSetname(String setname) {
		this.setname = setname;
	}

	public String getInfor_Flag() {
		return infor_Flag;
	}

	public void setInfor_Flag(String infor_Flag) {
		this.infor_Flag = infor_Flag;
	}


	public ArrayList getDblist() {
		return dblist;
	}

	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}


	public String getUsed_flag() {
		return used_flag;
	}

	public void setUsed_flag(String used_flag) {
		this.used_flag = used_flag;
	}

	

	

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	/**
	 * @return Returns the staticForm.
	 */
	public PaginationForm getStaticForm() {
		return staticForm;
	}
	/**
	 * @param staticForm The staticForm to set.
	 */
	public void setStaticForm(PaginationForm staticForm) {
		this.staticForm = staticForm;
	}
	public String getFind() {
		return find;
	}
	public void setFind(String find) {
		this.find = find;
	}
	
	public String getFormatstr() {
		return formatstr;
	}
	public void setFormatstr(String formatstr) {
		this.formatstr = formatstr;
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	public String getViewuserbases() {
		return viewuserbases;
	}
	public void setViewuserbases(String viewuserbases) {
		this.viewuserbases = viewuserbases;
	}
	public String getUserbases() {
		return userbases;
	}
	public void setUserbases(String userbases) {
		this.userbases = userbases;
	}	
}
