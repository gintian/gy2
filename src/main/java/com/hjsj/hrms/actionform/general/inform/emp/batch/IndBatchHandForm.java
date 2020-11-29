package com.hjsj.hrms.actionform.general.inform.emp.batch;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class IndBatchHandForm extends FrameForm {
	private String setname; //库前缀
	private String dbname; //子集
	private String a_code; //代码id
	/**指标名称**/
	private String itemid;
	private ArrayList indlist = new ArrayList();
	
	/**参考值**/
	private String refvalue;
	private ArrayList refvaluelist = new ArrayList();
	
	private ArrayList fieldlist = new ArrayList();
	private String tablestr; //生成计算table
	private String sortstr;//显示指标排序
	private String formula;//计算公式
	private String fieldsetid;//子集id
	private ArrayList fieldsetlist = new ArrayList();//子集list
	private String[] itemid_arr; //指标
	private String formulatable; //生成计算公式table
	private String formulastr;
	private String flag;
	private String viewsearch;
	private String flagcheck;
	private String history; //是否修改历史记录
	private String infor;
	private String unit_type;//1,2,3,4 :合同,人员，单位 职位
	private String count;//当前记录数
	private String countmon; //某年某月某次记录数
	private String countall; //所有记录数
	private String prive; //子集的权限值
	private String strid;//选中记录的id
	private String secount;//选中记录的条数
	private ArrayList fieldSetDataList = new ArrayList();//所有人员信息集 xuj 2010-5-28 add
	private String isSetId;//setname是否用作查询公式的条件=1是，=0 不是
	private String entranceFlag;//进入模块标志=1从工资管理的基础数据维护进入，默认为0从其他模块进入
	private String inforflag; //1: 员工管理BS表格录入 2：外部培训
	@Override
    public void outPutFormHM() {
		this.setEntranceFlag((String)this.getFormHM().get("entranceFlag"));
		this.setIsSetId((String)this.getFormHM().get("isSetId"));
		this.setIndlist((ArrayList)this.getFormHM().get("indlist"));
		this.setItemid((String)this.getFormHM().get("itemid"));
		this.setRefvaluelist((ArrayList)this.getFormHM().get("refvaluelist"));
		this.setRefvalue((String)this.getFormHM().get("refvalue"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setSetname((String)this.getFormHM().get("setname"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setDbname((String)this.getFormHM().get("dbname"));
		this.setTablestr((String)this.getFormHM().get("tablestr"));
		this.setSortstr((String)this.getFormHM().get("sortstr"));
		this.setFormula((String)this.getFormHM().get("formula"));
		this.setFieldsetid((String)this.getFormHM().get("fieldsetid"));
		this.setFieldsetlist((ArrayList)this.getFormHM().get("fieldsetlist"));
		this.setItemid_arr((String[])this.getFormHM().get("itemid_arr"));
		this.setFormulatable((String)this.getFormHM().get("formulatable"));
		this.setFormulastr((String)this.getFormHM().get("formulastr"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setViewsearch((String)this.getFormHM().get("viewsearch"));
		this.setFlagcheck((String)this.getFormHM().get("flagcheck"));
		this.setInfor((String)this.getFormHM().get("infor"));
		this.setUnit_type((String)this.getFormHM().get("unit_type"));
		this.setHistory((String)this.getFormHM().get("history"));
		this.setCount((String)this.getFormHM().get("count"));
		this.setCountmon((String)this.getFormHM().get("countmon"));
		this.setCountall((String)this.getFormHM().get("countall"));
		this.setPrive((String)this.getFormHM().get("prive"));
		this.setStrid((String)this.getFormHM().get("strid"));
		this.setSecount((String)this.getFormHM().get("secount"));
		this.setFieldSetDataList((ArrayList)this.getFormHM().get("fieldSetDataList"));
		this.setInforflag((String)this.getFormHM().get("inforflag"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("entranceFlag", this.getEntranceFlag());
		this.getFormHM().put("isSetId", this.getIsSetId());
		this.getFormHM().put("setname", this.getSetname());
		this.getFormHM().put("inforflag", this.getInforflag());
	}
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
        super.reset(arg0, arg1);  
    }
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
        if("/general/inform/emp/batch/alertmoreind".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null/*&&"infoself".equals(arg1.getParameter("flag"))*/)
        {
            arg1.setAttribute("targetWindow", "1");
        }
        return super.validate(arg0, arg1);
    }
	public ArrayList getIndlist() {
		return indlist;
	}

	public void setIndlist(ArrayList indlist) {
		this.indlist = indlist;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getRefvalue() {
		return refvalue;
	}

	public void setRefvalue(String refvalue) {
		this.refvalue = refvalue;
	}

	public ArrayList getRefvaluelist() {
		return refvaluelist;
	}

	public void setRefvaluelist(ArrayList refvaluelist) {
		this.refvaluelist = refvaluelist;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getSetname() {
		return setname;
	}

	public void setSetname(String setname) {
		this.setname = setname;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getTablestr() {
		return tablestr;
	}

	public void setTablestr(String tablestr) {
		this.tablestr = tablestr;
	}

	public String getSortstr() {
		return sortstr;
	}

	public void setSortstr(String sortstr) {
		this.sortstr = sortstr;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getFieldsetid() {
		return fieldsetid;
	}

	public void setFieldsetid(String fieldsetid) {
		this.fieldsetid = fieldsetid;
	}

	public ArrayList getFieldsetlist() {
		return fieldsetlist;
	}

	public void setFieldsetlist(ArrayList fieldsetlist) {
		this.fieldsetlist = fieldsetlist;
	}

	public String[] getItemid_arr() {
		return itemid_arr;
	}

	public void setItemid_arr(String[] itemid_arr) {
		this.itemid_arr = itemid_arr;
	}

	public String getFormulatable() {
		return formulatable;
	}

	public void setFormulatable(String formulatable) {
		this.formulatable = formulatable;
	}

	public String getFormulastr() {
		return formulastr;
	}

	public void setFormulastr(String formulastr) {
		this.formulastr = formulastr;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getViewsearch() {
		return viewsearch;
	}

	public void setViewsearch(String viewsearch) {
		this.viewsearch = viewsearch;
	}

	public String getFlagcheck() {
		return flagcheck;
	}

	public void setFlagcheck(String flagcheck) {
		this.flagcheck = flagcheck;
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

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getCountall() {
		return countall;
	}

	public void setCountall(String countall) {
		this.countall = countall;
	}

	public String getCountmon() {
		return countmon;
	}

	public void setCountmon(String countmon) {
		this.countmon = countmon;
	}

	public String getPrive() {
		return prive;
	}

	public void setPrive(String prive) {
		this.prive = prive;
	}

	public String getSecount() {
		return secount;
	}

	public void setSecount(String secount) {
		this.secount = secount;
	}

	public String getStrid() {
		return strid;
	}

	public void setStrid(String strid) {
		this.strid = strid;
	}

	public ArrayList getFieldSetDataList() {
		return fieldSetDataList;
	}

	public void setFieldSetDataList(ArrayList fieldSetDataList) {
		this.fieldSetDataList = fieldSetDataList;
	}

	public String getIsSetId() {
		return isSetId;
	}

	public void setIsSetId(String isSetId) {
		this.isSetId = isSetId;
	}

	public String getEntranceFlag() {
		return entranceFlag;
	}

	public void setEntranceFlag(String entranceFlag) {
		this.entranceFlag = entranceFlag;
	}

	public void setInforflag(String inforflag) {
		this.inforflag = inforflag;
	}

	public String getInforflag() {
		return inforflag;
	}
}
