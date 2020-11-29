package com.hjsj.hrms.actionform.gz.gz_accounting.cash;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
/**
 * <p>Title:CashListForm.java</p>
 * <p>Description:分钱清单的form</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time: 2007-09-05 10:43:43 am</p>
 * @author lizhenwei
 * @version 4.0
 *
 */

public class CashListForm extends FrameForm{
	/**单位/部门代码*/
	private String code;
	/**当前薪资类别中的数值行指标id*/
	private String itemid;
	/**薪资类别*/
	private String salaryid;
	private String size;
	/**某单位或某部门下的人员的票额信息列表*/
	private ArrayList cashList= new ArrayList();
	/**有效的货币面值列(前台显示的表头)*/
	private ArrayList columnslist = new ArrayList();
	/**分页显示*/
	private PaginationForm moneyListForm = new PaginationForm();
	/**当前薪资类别中的数值行指标*/
	private ArrayList itemList = new ArrayList();
	/**当前货币下的所有面值*/
	private ArrayList moneyItemList = new ArrayList();
	/**币种类别id*/
	private String nmoneyid;
	/**薪资表名*/
	private String tableName;
	/**人员筛选的sql语句*/
	private String filterSql;
	/**货币面值串*/
	private String moneyitemids="";
	private String beforeSql ;
	private String isCancel;
	private String condid;
	private ArrayList filterList = new ArrayList();
	private String priv;
	private String showUnitCodeTree;//是否按操作单位来显示机构树
	@Override
    public void outPutFormHM() {
		this.setShowUnitCodeTree((String)this.getFormHM().get("showUnitCodeTree"));
		this.setPriv((String)this.getFormHM().get("priv"));
		this.setCondid((String)this.getFormHM().get("condid"));
		this.setFilterList((ArrayList)this.getFormHM().get("filterList"));
		this.setBeforeSql((String)this.getFormHM().get("beforeSql"));
		this.setCode((String)this.getFormHM().get("code"));
		this.setItemid((String)this.getFormHM().get("itemid"));
		this.setSalaryid((String)this.getFormHM().get("salaryid"));
		this.setSize((String)this.getFormHM().get("size"));
		this.setCashList((ArrayList)this.getFormHM().get("cashList"));
		this.setColumnslist((ArrayList)this.getFormHM().get("columnslist"));
		this.getMoneyListForm().setList((ArrayList)this.getFormHM().get("cashList"));
		this.setItemList((ArrayList)this.getFormHM().get("itemList"));
		this.setMoneyItemList((ArrayList)this.getFormHM().get("moneyItemList"));
		this.setNmoneyid((String)this.getFormHM().get("nmoneyid"));
		this.setTableName((String)this.getFormHM().get("tableName"));
		this.setFilterSql((String)this.getFormHM().get("filterSql"));
		this.setIsCancel((String)this.getFormHM().get("isCancel"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("showUnitCodeTree", this.getShowUnitCodeTree());
		this.getFormHM().put("priv",this.getPriv());
		this.getFormHM().put("condid",this.getCondid());
		this.getFormHM().put("beforeSql",this.getBeforeSql());
		this.getFormHM().put("code",this.getCode());
		this.getFormHM().put("itemid",this.getItemid());
		this.getFormHM().put("salaryid",this.getSalaryid());
		this.getFormHM().put("selectedList",this.getMoneyListForm().getSelectedList());
		this.getFormHM().put("columnslist",this.getColumnslist());
		this.getFormHM().put("nmoneyid",this.getNmoneyid());
		this.getFormHM().put("tableName",this.getTableName());
		this.getFormHM().put("filterSql",this.getFilterSql());
		this.getFormHM().put("moneyitemids",this.getMoneyitemids());
		this.getFormHM().put("isCancel",this.getIsCancel());
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/gz/gz_accounting/cash/initCashList".equals(arg0.getPath())&&(arg1.getParameter("b_query")!=null||arg1.getParameter("b_filter")!=null))
		{
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		if((arg1.getParameter("b_query")!=null&& "init".equals(arg1.getParameter("b_query")))||(arg1.getParameter("b_filter")!=null&& "filter".equals(arg1.getParameter("b_filter"))))
		{
			if(this.getMoneyListForm()!=null)
				this.getMoneyListForm().getPagination().firstPage();
		}
		return super.validate(arg0, arg1);
	}
	

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public ArrayList getCashList() {
		return cashList;
	}

	public void setCashList(ArrayList cashList) {
		this.cashList = cashList;
	}

	public ArrayList getColumnslist() {
		return columnslist;
	}

	public void setColumnslist(ArrayList columnslist) {
		this.columnslist = columnslist;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public PaginationForm getMoneyListForm() {
		return moneyListForm;
	}

	public void setMoneyListForm(PaginationForm moneyListForm) {
		this.moneyListForm = moneyListForm;
	}

	public ArrayList getItemList() {
		return itemList;
	}

	public void setItemList(ArrayList itemList) {
		this.itemList = itemList;
	}

	public ArrayList getMoneyItemList() {
		return moneyItemList;
	}

	public void setMoneyItemList(ArrayList moneyItemList) {
		this.moneyItemList = moneyItemList;
	}

	public String getNmoneyid() {
		return nmoneyid;
	}

	public void setNmoneyid(String nmoneyid) {
		this.nmoneyid = nmoneyid;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getFilterSql() {
		return filterSql;
	}

	public void setFilterSql(String filterSql) {
		this.filterSql = filterSql;
	}

	public String getMoneyitemids() {
		return moneyitemids;
	}

	public void setMoneyitemids(String moneyitemids) {
		this.moneyitemids = moneyitemids;
	}

	public String getBeforeSql() {
		return beforeSql;
	}

	public void setBeforeSql(String beforeSql) {
		this.beforeSql = beforeSql;
	}

	public String getIsCancel() {
		return isCancel;
	}

	public void setIsCancel(String isCancel) {
		this.isCancel = isCancel;
	}

	public String getCondid() {
		return condid;
	}

	public void setCondid(String condid) {
		this.condid = condid;
	}

	public ArrayList getFilterList() {
		return filterList;
	}

	public void setFilterList(ArrayList filterList) {
		this.filterList = filterList;
	}

	public String getPriv() {
		return priv;
	}

	public void setPriv(String priv) {
		this.priv = priv;
	}

	public String getShowUnitCodeTree() {
		return showUnitCodeTree;
	}

	public void setShowUnitCodeTree(String showUnitCodeTree) {
		this.showUnitCodeTree = showUnitCodeTree;
	}

}
