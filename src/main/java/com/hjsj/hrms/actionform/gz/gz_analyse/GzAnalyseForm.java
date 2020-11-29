package com.hjsj.hrms.actionform.gz.gz_analyse;

import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:GzAnalyseForm.java</p>
 * <p>Description:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-11-10 下午01:23:09</p>
 * @author LiZhenWei
 * @version 4.0
 */
public class GzAnalyseForm extends FrameForm{
	/**薪资类别id*/
	private String salaryid;
	/**应用库前缀*/
	private String pre;
	/**应用库列表*/
	private ArrayList preList = new ArrayList();
	/**薪资类别列表*/
	private ArrayList salarySetList= new ArrayList();
	/**报表id*/
	private String reportTabId;
	private String rsdtlid;
	private String rsid;
	/**报表列表*/
	private ArrayList reportTabList=new ArrayList();
	/**工资项目列表*/
	private ArrayList itemlist=new ArrayList();
	/**report name*/
	private String rsname;
	private String[] right_fields= new String[0];
	private String[] itemid=new String[0];
	/**按部门分页标志*/
	private String bgroup;
	/** user*/
	private String username;
	/**浏览窗口的宽和高*/
	private String width;
	private String height;
	private String gz_module;
	/**登录用户权限范围库*/
	private String privDb;
	/**登陆用户的角色（是否是超级用户）*/
	private String role;
	/**登录用户的管理机构代码*/
	private String privCode;
	private String privCodeValue;
	/**----------------------工资进度表分析参数------------------------------*/
	/**薪资总额是否控制到部门*/
	private String ctrl_type;
	/**分析项目列表*/
	private ArrayList planitemlist = new ArrayList();
	/**分析项目id*/
	private String planitemid;
	/**分析年份*/
	private String yearf;
	/**单位或部门代码*/
	private String code;
	/**饼状图数据*/
	private ArrayList btlist = new ArrayList();
	/**柱状图数据*/
	private ArrayList ztlist = new ArrayList();
	/**线图数据*/
	private ArrayList xtlist = new ArrayList();
	private HashMap xtmap;
	/**列表数据*/
	private ArrayList ltlist = new ArrayList();
	private String address;
    private ChartParameter chartParameter = null ;
    private String chartkind;
    private ArrayList setList=new ArrayList();
    private ArrayList selectedList=new ArrayList();
    private String selectedids;
    private String archive;
    /**月度总额使用情况=1柱状图=2线图*/
    private String charttype;
    private ArrayList dataList = new ArrayList();
    private ArrayList alist = new ArrayList();
    private String totalValue;
    private String isYd;
    private ArrayList tableHeaderList = new ArrayList();
    private ArrayList adjustDataList = new ArrayList();
    private String isHasAdjustSet;
    private String planItemDesc;
    private String ownerType;
    private String totalAmount="";
    //---------------------------------按部门各月工资构成分析表 高级按钮功能------------------------
	private String[] _left_fields;
	private String[] _right_fields;
	private ArrayList _selectsubclass;
	private ArrayList _subclasslist;
	private ArrayList nameList;
	private ArrayList gzProjectList;
	private String[] faNeme;
	private String[] gzproject;
	private String faNameStr;//方案名称title属性
	private String gzprojectStr;//薪资项目title属性
	//--------------------------end------------------------
    public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	private String info="";
	
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("ownerType", this.getOwnerType());
		this.getFormHM().put("planItemDesc", this.getPlanItemDesc());
		this.getFormHM().put("isHasAdjustSet", this.getIsHasAdjustSet());
		this.getFormHM().put("dataList", this.getDataList());
	    this.getFormHM().put("charttype",this.getCharttype());
		this.getFormHM().put("archive", this.getArchive());
		this.getFormHM().put("selectedids", this.getSelectedids());
		this.getFormHM().put("rsid", this.getRsid());
		this.getFormHM().put("chartkind", this.getChartkind());
		this.getFormHM().put("pre",this.getPre());
		this.getFormHM().put("salaryid",this.getSalaryid());
		this.getFormHM().put("reportTabId",this.getReportTabId());
		this.getFormHM().put("rsname",this.getRsname());
		this.getFormHM().put("rsdtlid",this.getRsdtlid());
		this.getFormHM().put("right_fields",this.getRight_fields());
		this.getFormHM().put("bgroup",this.getBgroup());
		this.getFormHM().put("gz_module",this.getGz_module());
		this.getFormHM().put("ctrl_type", this.getCtrl_type());
		this.getFormHM().put("code",this.getCode());
		this.getFormHM().put("yearf",this.getYearf());
		this.getFormHM().put("planitemid",this.getPlanitemid());
		
		this.getFormHM().put("_left_fields",this.get_left_fields());
		this.getFormHM().put("_right_fileds",this.get_right_fields());
		this.getFormHM().put("faNeme", this.getFaNeme());
		this.getFormHM().put("gzproject", this.getGzproject());
		//this.getFormHM().put("planitemlist",this.getPlanitemlist());
	}

	@Override
    public void outPutFormHM() {
		this.setInfo((String)this.getFormHM().get("info"));
		this.setTotalAmount((String)this.getFormHM().get("totalAmount"));                  
		this.setOwnerType((String)this.getFormHM().get("ownerType"));
		this.setPlanItemDesc((String)this.getFormHM().get("planItemDesc"));
		this.setIsHasAdjustSet((String)this.getFormHM().get("isHasAdjustSet"));
		this.setTableHeaderList((ArrayList)this.getFormHM().get("tableHeaderList"));
		this.setAdjustDataList((ArrayList)this.getFormHM().get("adjustDataList"));
		this.setIsYd((String)this.getFormHM().get("isYd"));
		this.setTotalValue((String)this.getFormHM().get("totalValue"));
		this.setAlist((ArrayList)this.getFormHM().get("alist"));
		this.setDataList((ArrayList)this.getFormHM().get("dataList"));
		this.setCharttype((String)this.getFormHM().get("charttype"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setArchive((String)this.getFormHM().get("archive"));
		this.setRsid((String)this.getFormHM().get("rsid"));
		this.setSelectedList((ArrayList)this.getFormHM().get("selectedList"));
		this.setSetList((ArrayList)this.getFormHM().get("setList"));
		this.setChartkind((String)this.getFormHM().get("chartkind"));
		this.setPrivCodeValue((String)this.getFormHM().get("privCodeValue"));
		this.setRole((String)this.getFormHM().get("role"));
		this.setPrivCode((String)this.getFormHM().get("privCode"));
		this.setPrivDb((String)this.getFormHM().get("privDb"));
		this.setPre((String)this.getFormHM().get("pre"));
		this.setPreList((ArrayList)this.getFormHM().get("preList"));
		this.setSalaryid((String)this.getFormHM().get("salaryid"));
		this.setSalarySetList((ArrayList)this.getFormHM().get("salarySetList"));
		this.setReportTabId((String)this.getFormHM().get("reportTabId"));
		this.setReportTabList((ArrayList)this.getFormHM().get("reportTabList"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setRsname((String)this.getFormHM().get("rsname"));
		this.setRsdtlid((String)this.getFormHM().get("rsdtlid"));
		this.setBgroup((String)this.getFormHM().get("bgroup"));
		this.setUsername((String)this.getFormHM().get("username"));
		this.setWidth((String)this.getFormHM().get("width"));
		this.setHeight((String)this.getFormHM().get("height"));
		this.setGz_module((String)this.getFormHM().get("gz_module"));
		this.setCtrl_type((String)this.getFormHM().get("ctrl_type"));
		this.setCode((String)this.getFormHM().get("code"));
		this.setYearf((String)this.getFormHM().get("yearf"));
		this.setPlanitemlist((ArrayList)this.getFormHM().get("planitemlist"));
		this.setPlanitemid((String)this.getFormHM().get("planitemid"));
		this.setBtlist((ArrayList)this.getFormHM().get("btlist"));
		this.setXtlist((ArrayList)this.getFormHM().get("xtlist"));
		this.setLtlist((ArrayList)this.getFormHM().get("ltlist"));
		this.setZtlist((ArrayList)this.getFormHM().get("ztlist"));
		this.setXtmap((HashMap)this.getFormHM().get("xtmap"));
		this.setAddress((String)this.getFormHM().get("address"));
		this.setChartParameter((ChartParameter)this.getFormHM().get("chartParameter"));
		
		this.set_left_fields((String[]) this.getFormHM().get("_left_fields"));
		this.set_right_fields((String[]) this.getFormHM().get("_right_fields"));
		this.set_selectsubclass((ArrayList) this.getFormHM().get("_selectsubclass"));
		this.set_subclasslist((ArrayList) this.getFormHM().get("_subclasslist"));
		this.setNameList((ArrayList) this.getFormHM().get("nameList"));
		this.setGzProjectList((ArrayList) this.getFormHM().get("gzProjectList"));
		this.setFaNeme((String[]) this.getFormHM().get("faNeme"));
		this.setGzproject((String[]) this.getFormHM().get("gzproject"));
		this.setFaNameStr((String) this.getFormHM().get("faNameStr"));
		this.setGzprojectStr((String) this.getFormHM().get("gzprojectStr"));
	}

	public String getPre() {
		return pre;
	}

	public void setPre(String pre) {
		this.pre = pre;
	}

	public ArrayList getPreList() {
		return preList;
	}

	public void setPreList(ArrayList preList) {
		this.preList = preList;
	}

	public String getReportTabId() {
		return reportTabId;
	}

	public void setReportTabId(String reportTabId) {
		this.reportTabId = reportTabId;
	}

	public ArrayList getReportTabList() {
		return reportTabList;
	}

	public void setReportTabList(ArrayList reportTabList) {
		this.reportTabList = reportTabList;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public ArrayList getSalarySetList() {
		return salarySetList;
	}

	public void setSalarySetList(ArrayList salarySetList) {
		this.salarySetList = salarySetList;
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public String getRsname() {
		return rsname;
	}

	public void setRsname(String rsname) {
		this.rsname = rsname;
	}

	public String getRsdtlid() {
		return rsdtlid;
	}

	public void setRsdtlid(String rsdtlid) {
		this.rsdtlid = rsdtlid;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public String getBgroup() {
		return bgroup;
	}

	public void setBgroup(String bgroup) {
		this.bgroup = bgroup;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getGz_module() {
		return gz_module;
	}

	public void setGz_module(String gz_module) {
		this.gz_module = gz_module;
	}

	public String getCtrl_type() {
		return ctrl_type;
	}

	public void setCtrl_type(String ctrl_type) {
		this.ctrl_type = ctrl_type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPlanitemid() {
		return planitemid;
	}

	public void setPlanitemid(String planitemid) {
		this.planitemid = planitemid;
	}

	public ArrayList getPlanitemlist() {
		return planitemlist;
	}

	public void setPlanitemlist(ArrayList planitemlist) {
		this.planitemlist = planitemlist;
	}

	public String getYearf() {
		return yearf;
	}

	public void setYearf(String yearf) {
		this.yearf = yearf;
	}

	public ArrayList getBtlist() {
		return btlist;
	}

	public void setBtlist(ArrayList btlist) {
		this.btlist = btlist;
	}

	public ArrayList getLtlist() {
		return ltlist;
	}

	public void setLtlist(ArrayList ltlist) {
		this.ltlist = ltlist;
	}

	public ArrayList getXtlist() {
		return xtlist;
	}

	public void setXtlist(ArrayList xtlist) {
		this.xtlist = xtlist;
	}

	public ArrayList getZtlist() {
		return ztlist;
	}

	public void setZtlist(ArrayList ztlist) {
		this.ztlist = ztlist;
	}

	public HashMap getXtmap() {
		return xtmap;
	}

	public void setXtmap(HashMap xtmap) {
		this.xtmap = xtmap;
	}

	public String getPrivCode() {
		return privCode;
	}

	public void setPrivCode(String privCode) {
		this.privCode = privCode;
	}

	public String getPrivDb() {
		return privDb;
	}

	public void setPrivDb(String privDb) {
		this.privDb = privDb;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPrivCodeValue() {
		return privCodeValue;
	}

	public void setPrivCodeValue(String privCodeValue) {
		this.privCodeValue = privCodeValue;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public ChartParameter getChartParameter() {
		return chartParameter;
	}

	public void setChartParameter(ChartParameter chartParameter) {
		this.chartParameter = chartParameter;
	}

	public String getChartkind() {
		return chartkind;
	}

	public void setChartkind(String chartkind) {
		this.chartkind = chartkind;
	}

	public ArrayList getSetList() {
		return setList;
	}

	public void setSetList(ArrayList setList) {
		this.setList = setList;
	}

	public ArrayList getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(ArrayList selectedList) {
		this.selectedList = selectedList;
	}

	public String[] getItemid() {
		return itemid;
	}

	public void setItemid(String[] itemid) {
		this.itemid = itemid;
	}

	public String getRsid() {
		return rsid;
	}

	public void setRsid(String rsid) {
		this.rsid = rsid;
	}

	public String getSelectedids() {
		return selectedids;
	}

	public void setSelectedids(String selectedids) {
		this.selectedids = selectedids;
	}

	public String getArchive() {
		return archive;
	}

	public void setArchive(String archive) {
		this.archive = archive;
	}

	public String getCharttype() {
		return charttype;
	}

	public void setCharttype(String charttype) {
		this.charttype = charttype;
	}

	public ArrayList getDataList() {
		return dataList;
	}

	public void setDataList(ArrayList dataList) {
		this.dataList = dataList;
	}

	public ArrayList getAlist() {
		return alist;
	}

	public void setAlist(ArrayList alist) {
		this.alist = alist;
	}

	public String getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(String totalValue) {
		this.totalValue = totalValue;
	}

	public String getIsYd() {
		return isYd;
	}

	public void setIsYd(String isYd) {
		this.isYd = isYd;
	}

	public ArrayList getTableHeaderList() {
		return tableHeaderList;
	}

	public void setTableHeaderList(ArrayList tableHeaderList) {
		this.tableHeaderList = tableHeaderList;
	}

	public ArrayList getAdjustDataList() {
		return adjustDataList;
	}

	public void setAdjustDataList(ArrayList adjustDataList) {
		this.adjustDataList = adjustDataList;
	}

	public String getIsHasAdjustSet() {
		return isHasAdjustSet;
	}

	public void setIsHasAdjustSet(String isHasAdjustSet) {
		this.isHasAdjustSet = isHasAdjustSet;
	}

	public String getPlanItemDesc() {
		return planItemDesc;
	}

	public void setPlanItemDesc(String planItemDesc) {
		this.planItemDesc = planItemDesc;
	}

	public String getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}
	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String[] get_left_fields() {
		return _left_fields;
	}

	public void set_left_fields(String[] _left_fields) {
		this._left_fields = _left_fields;
	}

	public String[] get_right_fields() {
		return _right_fields;
	}

	public void set_right_fields(String[] _right_fields) {
		this._right_fields = _right_fields;
	}

	public ArrayList get_selectsubclass() {
		return _selectsubclass;
	}

	public void set_selectsubclass(ArrayList _selectsubclass) {
		this._selectsubclass = _selectsubclass;
	}

	public ArrayList get_subclasslist() {
		return _subclasslist;
	}

	public void set_subclasslist(ArrayList _subclasslist) {
		this._subclasslist = _subclasslist;
	}

	public ArrayList getNameList() {
		return nameList;
	}

	public void setNameList(ArrayList nameList) {
		this.nameList = nameList;
	}

	public ArrayList getGzProjectList() {
		return gzProjectList;
	}

	public void setGzProjectList(ArrayList gzProjectList) {
		this.gzProjectList = gzProjectList;
	}

	public String[] getFaNeme() {
		return faNeme;
	}

	public void setFaNeme(String[] faNeme) {
		this.faNeme = faNeme;
	}

	public String[] getGzproject() {
		return gzproject;
	}

	public void setGzproject(String[] gzproject) {
		this.gzproject = gzproject;
	}

	public String getFaNameStr() {
		return faNameStr;
	}

	public void setFaNameStr(String faNameStr) {
		this.faNameStr = faNameStr;
	}

	public String getGzprojectStr() {
		return gzprojectStr;
	}

	public void setGzprojectStr(String gzprojectStr) {
		this.gzprojectStr = gzprojectStr;
	}

}
