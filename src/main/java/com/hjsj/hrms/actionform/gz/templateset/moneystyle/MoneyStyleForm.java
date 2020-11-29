package com.hjsj.hrms.actionform.gz.templateset.moneystyle;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class MoneyStyleForm extends FrameForm {
	/**货币类别编号*/
	private String nstyleid;
	/**类别名称*/
	private String cname;
	/**货币符号*/
	private String ctoken;
	/**货币单位*/
	private String cunit;
	/**汇率*/
	private String nratio;
	/**状态标识*/
	private String cstate;
	/**面值*/
	private String nitemid;
	/**货币项目名称*/
	private String nname;
	/**有效标识*/
	private String nflag;
	/**币种列表*/
	private ArrayList moneyList = new ArrayList();
	/**币种明细列表*/
	private ArrayList moneyDetailList = new ArrayList();
	/**分页*/
	private PaginationForm moneyListForm=new PaginationForm();
	private String[] nstyleidArray=new String[0];
	/**时时分页标签*/
	private String sql;
	private String where_sql;
	private String columns;
	/**控制保存并继续按钮是否出现，新建时出现，修改时不出现*/
	private String isVisable;// =new 出现; =edit 不出现
    /**判断是新增明细还是修改,有值是修改,没有值是新增*/
	private String beforenitemid;
	@Override
    public void outPutFormHM()
	{
		this.setMoneyList((ArrayList)this.getFormHM().get("moneyList"));
		this.setMoneyDetailList((ArrayList)this.getFormHM().get("moneyDetailList"));
		this.getMoneyListForm().setList((ArrayList)this.getFormHM().get("moneyList"));
		this.setNstyleidArray((String[])this.getFormHM().get("nstyleidArray"));
		this.setCname((String)this.getFormHM().get("cname"));
		this.setCtoken((String)this.getFormHM().get("ctoken"));
		this.setCunit((String)this.getFormHM().get("cunit"));
		this.setNstyleid((String)this.getFormHM().get("nstyleid"));
		this.setNratio((String)this.getFormHM().get("nratio"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setWhere_sql((String)this.getFormHM().get("where_sql"));
		this.setIsVisable((String)this.getFormHM().get("isVisable"));
		this.setNitemid((String)this.getFormHM().get("nitemid"));
		this.setBeforenitemid((String)this.getFormHM().get("beforenitemid"));
		this.setColumns((String)this.getFormHM().get("columns"));
	}

	@Override
    public void inPutTransHM()
	{
		this.getFormHM().put("selectedList",this.getMoneyListForm().getSelectedList());
		this.getFormHM().put("cname",this.getCname());
		this.getFormHM().put("ctoken",this.getCtoken());
		this.getFormHM().put("cunit",this.getCunit());
		this.getFormHM().put("nratio",this.getNratio());
		this.getFormHM().put("nstyleid",this.getNstyleid());
		this.getFormHM().put("nitemid",this.getNitemid());
		this.getFormHM().put("beforenitemid",this.getBeforenitemid());
	}
	/*public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if(arg1.getParameter("operate")!=null&&arg1.getParameter("operate").equals("init"))
		{
			if(this.getPosDemandListform()!=null)
				this.getPosDemandListform().getPagination().firstPage();
		}
		return super.validate(arg0, arg1);
		/gz/templateset/moneystyle/initMoneyStyle.do?b_init=init
	}*/
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/gz/templateset/moneystyle/initMoneyStyleDetail".equals(arg0.getPath())&&arg1.getParameter("b_init")!=null)
		{
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		if(arg1.getParameter("b_init")!=null&& "init".equals(arg1.getParameter("b_init")))
			if(this.getMoneyListForm()!=null)
				this.getMoneyListForm().getPagination().firstPage();
		return super.validate(arg0, arg1);
	}
	

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getCstate() {
		return cstate;
	}

	public void setCstate(String cstate) {
		this.cstate = cstate;
	}

	public String getCtoken() {
		return ctoken;
	}

	public void setCtoken(String ctoken) {
		this.ctoken = ctoken;
	}

	public String getCunit() {
		return cunit;
	}

	public void setCunit(String cunit) {
		this.cunit = cunit;
	}

	public ArrayList getMoneyDetailList() {
		return moneyDetailList;
	}

	public void setMoneyDetailList(ArrayList moneyDetailList) {
		this.moneyDetailList = moneyDetailList;
	}

	public ArrayList getMoneyList() {
		return moneyList;
	}

	public void setMoneyList(ArrayList moneyList) {
		this.moneyList = moneyList;
	}

	public String getNflag() {
		return nflag;
	}

	public void setNflag(String nflag) {
		this.nflag = nflag;
	}

	public String getNitemid() {
		return nitemid;
	}

	public void setNitemid(String nitemid) {
		this.nitemid = nitemid;
	}

	public String getNname() {
		return nname;
	}

	public void setNname(String nname) {
		this.nname = nname;
	}

	public String getNratio() {
		return nratio;
	}

	public void setNratio(String nratio) {
		this.nratio = nratio;
	}

	public String getNstyleid() {
		return nstyleid;
	}

	public void setNstyleid(String nstyleid) {
		this.nstyleid = nstyleid;
	}

	public PaginationForm getMoneyListForm() {
		return moneyListForm;
	}

	public void setMoneyListForm(PaginationForm moneyListForm) {
		this.moneyListForm = moneyListForm;
	}

	public String[] getNstyleidArray() {
		return nstyleidArray;
	}

	public void setNstyleidArray(String[] nstyleidArray) {
		this.nstyleidArray = nstyleidArray;
	}

	public String getIsVisable() {
		return isVisable;
	}

	public void setIsVisable(String isVisable) {
		this.isVisable = isVisable;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getWhere_sql() {
		return where_sql;
	}

	public void setWhere_sql(String where_sql) {
		this.where_sql = where_sql;
	}

	public String getBeforenitemid() {
		return beforenitemid;
	}

	public void setBeforenitemid(String beforenitemid) {
		this.beforenitemid = beforenitemid;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

}
