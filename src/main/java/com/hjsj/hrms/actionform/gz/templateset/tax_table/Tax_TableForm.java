package com.hjsj.hrms.actionform.gz.templateset.tax_table;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class Tax_TableForm extends FrameForm{
	/**
	 * 税率表号
	 */
	private String taxid;
	/**
	 * 描述
	 */
	private String description;
	/**
	 * 基数
	 */
	private String k_base;
	/**
	 * 其他参数xml格式
	 */
	private String param;
	/**
	 * 项目序号
	 */
	private String taxitem;
	/**
	 * 应纳税所得额下限
	 */
	private String ynse_down;
	/**
	 * 应纳税所得额上限
	 */
	private String ynse_up;
	/**
	 * 税率
	 */
	private String sl;
	/**
	 * 速算扣除数
	 */
	private String sskcs;
	/**
	 * 上限标志
	 */
	private String flag;
	/**
	 * 扣除基数
	 */
	private String kc_base;
	/**
	 * 税率表信息列表
	 */
	private ArrayList  taxList = new ArrayList();
	private ArrayList taxTypeList= new ArrayList();
	private PaginationForm taxListForm=new PaginationForm();
    private String[] taxidArray=new String[0];
    private ArrayList detailList = new ArrayList();
    private ArrayList flagList = new ArrayList();
    private String[] taxDetailArray = new String[0];
    private String size;
    //------------------初始化导入税率表  zhaoxg add 2014-11-12----------------------
    private ArrayList validateList = new ArrayList();
    private String validateinfo;
    private String validatereturnInfo;
    private String repeats;
    //---------------------end----------------------------------------------------
    /**
     * 应纳税所得额
     * */
    private String income;
    private ArrayList  incomeList = new ArrayList();
    /**
	 * 薪资类别id
	 */
    private String salaryid;
    /**
	 * 计算项目编号
	 */
    private String itemid;
    /**
	 * 计算项目编号
	 */
    private String mode; //0|1 正算|反算
    /**导入文件目录*/
    private String dir;
    private FormFile formfile;
    private String info;
    private String returnInfo;
	@Override
    public void outPutFormHM() {
	   this.setTaxList((ArrayList)this.getFormHM().get("taxList"));
	   this.getTaxListForm().setList((ArrayList)this.getFormHM().get("taxList"));
	   this.setTaxTypeList((ArrayList)this.getFormHM().get("taxTypeList"));
	   this.setTaxid((String)this.getFormHM().get("taxid"));
	   this.setDetailList((ArrayList)this.getFormHM().get("detailList"));
	   this.setFlagList((ArrayList)this.getFormHM().get("flagList"));
	   this.setParam((String)this.getFormHM().get("param"));
	   this.setK_base((String)this.getFormHM().get("k_base"));
	   this.setSize((String)this.getFormHM().get("size"));
	   this.setReturnflag((String)this.getFormHM().get("returnflag"));
	   this.setIncome((String)this.getFormHM().get("income"));
	   this.setIncomeList((ArrayList)this.getFormHM().get("incomeList"));
	   this.setSalaryid((String)this.getFormHM().get("salaryid"));
	   this.setItemid((String)this.getFormHM().get("itemid"));
	   this.setMode((String)this.getFormHM().get("mode"));
	   this.setReturnInfo((String)this.getFormHM().get("returnInfo"));
	   this.setInfo((String)this.getFormHM().get("info"));
	   this.setValidateinfo((String) this.getFormHM().get("validateinfo"));
	   this.setValidateList((ArrayList) this.getFormHM().get("validateList"));
	   this.setValidatereturnInfo((String) this.getFormHM().get("validatereturnInfo"));
	   this.setRepeats((String) this.getFormHM().get("repeats"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("formfile",this.getFormfile());
		this.getFormHM().put("selectedList",this.getTaxListForm().getSelectedList());
		this.getFormHM().put("taxidArray",this.getTaxidArray());
		this.getFormHM().put("param",this.getParam());
		this.getFormHM().put("description",this.getDescription());
		this.getFormHM().put("k_base",this.getK_base());
		this.getFormHM().put("taxidArray",this.getTaxidArray());
		this.getFormHM().put("detailList",this.getDetailList());
		this.getFormHM().put("taxDetailArray",this.getTaxDetailArray());
		this.getFormHM().put("salaryid",this.getSalaryid());
		this.getFormHM().put("itemid",this.getItemid());
		this.getFormHM().put("dir",this.getDir());
		this.getFormHM().put("repeats", this.getRepeats());
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/gz/templateset/tax_table/initTaxTable".equals(arg0.getPath())&&(arg1.getParameter("b_init")!=null))
		{
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		if("/gz/templateset/tax_table/initTaxTable".equals(arg0.getPath())&&(arg1.getParameter("b_init")!=null))
		{
			if(this.getTaxListForm()!=null)
				this.getTaxListForm().getPagination().firstPage();
		}
		/* 安全问题 文件上传 薪资-税率表-导入 xiaoyun 2014-9-16 start */
		if("/gz/templateset/tax_table/initTaxTable".equals(arg0.getPath()) && arg1.getParameter("isclose")!=null){
			arg1.setAttribute("targetWindow", "1");//0不显示按钮 |1关闭|默认为返回
		}
		/* 安全问题 文件上传 薪资-税率表-导入 xiaoyun 2014-9-16 end */
		return super.validate(arg0, arg1);
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getK_base() {
		return k_base;
	}

	public void setK_base(String k_base) {
		this.k_base = k_base;
	}

	public String getKc_base() {
		return kc_base;
	}

	public void setKc_base(String kc_base) {
		this.kc_base = kc_base;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getSl() {
		return sl;
	}

	public void setSl(String sl) {
		this.sl = sl;
	}

	public String getSskcs() {
		return sskcs;
	}

	public void setSskcs(String sskcs) {
		this.sskcs = sskcs;
	}

	public String getTaxid() {
		return taxid;
	}

	public void setTaxid(String taxid) {
		this.taxid = taxid;
	}

	public String getTaxitem() {
		return taxitem;
	}

	public void setTaxitem(String taxitem) {
		this.taxitem = taxitem;
	}

	public String getYnse_down() {
		return ynse_down;
	}

	public void setYnse_down(String ynse_down) {
		this.ynse_down = ynse_down;
	}

	public String getYnse_up() {
		return ynse_up;
	}

	public void setYnse_up(String ynse_up) {
		this.ynse_up = ynse_up;
	}

	public ArrayList getTaxList() {
		return taxList;
	}

	public void setTaxList(ArrayList taxList) {
		this.taxList = taxList;
	}
	/*

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getSql_str() {
		return sql_str;
	}

	public void setSql_str(String sql_str) {
		this.sql_str = sql_str;
	}

	public String getWhere_sql() {
		return where_sql;
	}

	public void setWhere_sql(String where_sql) {
		this.where_sql = where_sql;
	}*/

	public PaginationForm getTaxListForm() {
		return taxListForm;
	}

	public void setTaxListForm(PaginationForm taxListForm) {
		this.taxListForm = taxListForm;
	}

	public String[] getTaxidArray() {
		return taxidArray;
	}

	public void setTaxidArray(String[] taxidArray) {
		this.taxidArray = taxidArray;
	}

	public ArrayList getTaxTypeList() {
		return taxTypeList;
	}

	public void setTaxTypeList(ArrayList taxTypeList) {
		this.taxTypeList = taxTypeList;
	}

	public ArrayList getDetailList() {
		return detailList;
	}

	public void setDetailList(ArrayList detailList) {
		this.detailList = detailList;
	}

	public ArrayList getFlagList() {
		return flagList;
	}

	public void setFlagList(ArrayList flagList) {
		this.flagList = flagList;
	}

	public String[] getTaxDetailArray() {
		return taxDetailArray;
	}

	public void setTaxDetailArray(String[] taxDetailArray) {
		this.taxDetailArray = taxDetailArray;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getIncome() {
		return income;
	}

	public void setIncome(String income) {
		this.income = income;
	}

	public ArrayList getIncomeList() {
		return incomeList;
	}

	public void setIncomeList(ArrayList incomeList) {
		this.incomeList = incomeList;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public FormFile getFormfile() {
		return formfile;
	}

	public void setFormfile(FormFile formfile) {
		this.formfile = formfile;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getReturnInfo() {
		return returnInfo;
	}

	public void setReturnInfo(String returnInfo) {
		this.returnInfo = returnInfo;
	}

	public ArrayList getValidateList() {
		return validateList;
	}

	public void setValidateList(ArrayList validateList) {
		this.validateList = validateList;
	}

	public String getValidateinfo() {
		return validateinfo;
	}

	public void setValidateinfo(String validateinfo) {
		this.validateinfo = validateinfo;
	}

	public String getValidatereturnInfo() {
		return validatereturnInfo;
	}

	public void setValidatereturnInfo(String validatereturnInfo) {
		this.validatereturnInfo = validatereturnInfo;
	}

	public String getRepeats() {
		return repeats;
	}

	public void setRepeats(String repeats) {
		this.repeats = repeats;
	}

}
