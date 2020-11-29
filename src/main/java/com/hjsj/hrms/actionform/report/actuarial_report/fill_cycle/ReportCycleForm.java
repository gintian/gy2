/**
 * 
 */
package com.hjsj.hrms.actionform.report.actuarial_report.fill_cycle;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import com.hrms.struts.valueobject.UserView;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:报表周期</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:oct 6, 2009:10:46:29 AM</p>
 * @author xieguiquan
 * @version 1.0
 * 
 */
public class ReportCycleForm extends FrameForm {

	/**
	 * 新建及编辑功能控制
	 */
	
	
	private ArrayList spersonlist = new ArrayList();
	private String sperson;
	private String adddate;
	
	
	protected UserView userView = this.getUserView();

	/**
	 * 建议对象
	 */
	private RecordVo reportcyclevo = new RecordVo("tt_cycle");

	/**
	 * 建议对象列表
	 */

	private PaginationForm reportCycleForm = new PaginationForm();
	private PaginationForm  editreportForm = new PaginationForm(); // 显示数据的分页处理
	private String report_id;
	private String flag;
	private String unitcode;
	private String id;
	private String sql;
	private ArrayList fieldlistU02=new ArrayList();
	private ArrayList fieldlsitU01=new ArrayList();
	private ArrayList editlistU02=new ArrayList();
	private FormFile file;
	private String importInfo;
	private ArrayList import_insertList=new ArrayList();
	private ArrayList import_updateList=new ArrayList();
	private String import_insertSql;
	private String import_updateSql;
	private String cycleparm;//来自填报周期的参数
	private ArrayList import_deleteinfo;
	private String unitcodes;
	/** 表3 */
	private ArrayList u03DataList=new ArrayList();
	private ArrayList compareDataList=new ArrayList();  //比较数据
	private String    current_values="";
	private ArrayList dataHeadList=new ArrayList();
	private String opt="1";   //1:可操作  0：只读 
	private String info="";   //提交返回信息
	private String reportStatus="";  //报表状态;
	private String kmethod="";
	private ArrayList u04DataList=new ArrayList();
   /** 表5 */	
	private ArrayList dataHeadList_u05=new ArrayList();
	private ArrayList dataList_u05=new ArrayList();
	
	private String t5_desc="";
	private String t3_desc="";
	private String addother="0";
	

	@Override
    public void outPutFormHM() {
	
		  this.getReportCycleForm().setList((ArrayList)this.getFormHM().get("cyclelist"));
		  this.setReportcyclevo((RecordVo) this.getFormHM().get("reportcyclevo2"));
		  this.getEditreportForm().setList((ArrayList)this.getFormHM().get("repotlist"));
			this.setFieldlsitU01((ArrayList)this.getFormHM().get("fieldlsitU01"));
			this.setFieldlistU02((ArrayList)this.getFormHM().get("fieldlistU02"));
			this.setSql((String)this.getFormHM().get("sql"));
			this.setEditlistU02((ArrayList)this.getFormHM().get("editlistU02"));
			
			this.setFlag((String)this.getFormHM().get("flag"));
			this.setU03DataList((ArrayList)this.getFormHM().get("u03DataList"));
			this.setCompareDataList((ArrayList)this.getFormHM().get("compareDataList"));
			this.setDataHeadList((ArrayList)this.getFormHM().get("dataHeadList"));
			this.setUnitcode((String)this.getFormHM().get("unitcode"));
			this.setOpt((String)this.getFormHM().get("opt"));
			this.setId((String)this.getFormHM().get("id"));
			this.setInfo((String)this.getFormHM().get("info"));
			this.setReportStatus((String)this.getFormHM().get("reportStatus"));
			this.setKmethod((String)this.getFormHM().get("kmethod"));
			this.setImportInfo((String)this.getFormHM().get("importInfo"));
			this.setDataHeadList_u05((ArrayList)this.getFormHM().get("dataHeadList_u05"));
			this.setDataList_u05((ArrayList)this.getFormHM().get("dataList_u05"));
			this.setU04DataList((ArrayList)this.getFormHM().get("u04DataList"));
			this.setImport_insertList((ArrayList)this.getFormHM().get("import_insertList"));
			this.setImport_updateList((ArrayList)this.getFormHM().get("import_updateList"));
			this.setImport_insertSql((String)this.getFormHM().get("import_insertSql"));
			this.setImport_updateSql((String)this.getFormHM().get("import_updateSql"));
			this.setImport_deleteinfo((ArrayList)this.getFormHM().get("import_deleteinfo"));
			this.setUnitcodes((String)this.getFormHM().get("unitcodes"));
			
			
			this.setT3_desc((String)this.getFormHM().get("t3_desc"));
			this.setT5_desc((String)this.getFormHM().get("t5_desc"));
			
			this.setCycleparm((String)this.getFormHM().get("cycleparm"));
	}

	/*
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		
		this.getFormHM().put("reportcyclevo", this.getReportcyclevo());
		this.getFormHM().put("adddate",this.adddate);
		//System.out.println("getreportcyclevo:"+this.getReportcyclevo());
		this.getFormHM().put("selectedreportlist",
				(ArrayList) this.getReportCycleForm().getSelectedList());
		this.getFormHM().put("report_id", this.getReport_id());
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("unitcode", this.getUnitcode());
		this.getFormHM().put("id", this.getId());
		this.getFormHM().put("fieldlsitU01", this.getFieldlsitU01());
        this.getFormHM().put("editlistU02", this.getEditlistU02());
        this.getFormHM().put("file",this.getFile());
		this.getFormHM().put("opt",this.opt);
		this.getFormHM().put("import_insertList", this.getImport_insertList());
		this.getFormHM().put("import_updateList", this.getImport_updateList());
		this.getFormHM().put("import_insertSql", this.getImport_insertSql());
		this.getFormHM().put("import_updateSql", this.getImport_updateSql());
		this.getFormHM().put("import_deleteinfo", this.import_deleteinfo);
		this.getFormHM().put("unitcodes", this.unitcodes);
		this.getFormHM().put("current_values",this.getCurrent_values());
		this.getFormHM().put("kmethod", this.getKmethod());
		this.getFormHM().put("t3_desc", this.getT3_desc());
		this.getFormHM().put("t5_desc",this.getT5_desc());
		this.getFormHM().put("addother", this.addother);
	}

	

	/**
	 * @return Returns the Boardvo.
	
	/*
	 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		super.reset(arg0, arg1);
	}



	public ArrayList getSpersonlist() {
		return spersonlist;
	}

	public void setSpersonlist(ArrayList spersonlist) {
		this.spersonlist = spersonlist;
	}

	public String getSperson() {
		return sperson;
	}

	public void setSperson(String sperson) {
		this.sperson = sperson;
	}

	@Override
    public UserView getUserView() {
		return userView;
	}

	@Override
    public void setUserView(UserView userView) {
		this.userView = userView;
	}

	

	public RecordVo getReportcyclevo() {
		return reportcyclevo;
	}

	public void setReportcyclevo(RecordVo reportcyclevo) {
		this.reportcyclevo = reportcyclevo;
	}

	public PaginationForm getReportCycleForm() {
		return reportCycleForm;
	}

	public void setReportCycleForm(PaginationForm reportCycleForm) {
		this.reportCycleForm = reportCycleForm;
	}

	public String getAdddate() {
		return adddate;
	}

	public void setAdddate(String adddate) {
		this.adddate = adddate;
	}

	public PaginationForm getEditreportForm() {
		return editreportForm;
	}

	public void setEditreportForm(PaginationForm editreportForm) {
		this.editreportForm = editreportForm;
	}


	public String getCycleparm() {
		return cycleparm;
	}

	public void setCycleparm(String cycleparm) {
		this.cycleparm = cycleparm;
	}

	public String getReport_id() {
		return report_id;
	}

	public void setReport_id(String report_id) {
		this.report_id = report_id;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getUnitcode() {
		return unitcode;
	}

	public void setUnitcode(String unitcode) {
		this.unitcode = unitcode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public ArrayList getFieldlistU02() {
		return fieldlistU02;
	}

	public void setFieldlistU02(ArrayList fieldlistU02) {
		this.fieldlistU02 = fieldlistU02;
	}

	public ArrayList getFieldlsitU01() {
		return fieldlsitU01;
	}

	public void setFieldlsitU01(ArrayList fieldlsitU01) {
		this.fieldlsitU01 = fieldlsitU01;
	}

	public ArrayList getEditlistU02() {
		return editlistU02;
	}

	public void setEditlistU02(ArrayList editlistU02) {
		this.editlistU02 = editlistU02;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getImportInfo() {
		return importInfo;
	}

	public void setImportInfo(String importInfo) {
		this.importInfo = importInfo;
	}

	public ArrayList getImport_insertList() {
		return import_insertList;
	}

	public void setImport_insertList(ArrayList import_insertList) {
		this.import_insertList = import_insertList;
	}

	public ArrayList getImport_updateList() {
		return import_updateList;
	}

	public void setImport_updateList(ArrayList import_updateList) {
		this.import_updateList = import_updateList;
	}

	public String getImport_insertSql() {
		return import_insertSql;
	}

	public void setImport_insertSql(String import_insertSql) {
		this.import_insertSql = import_insertSql;
	}

	public String getImport_updateSql() {
		return import_updateSql;
	}

	public void setImport_updateSql(String import_updateSql) {
		this.import_updateSql = import_updateSql;
	}

	public ArrayList getU03DataList() {
		return u03DataList;
	}

	public void setU03DataList(ArrayList dataList) {
		u03DataList = dataList;
	}

	public ArrayList getCompareDataList() {
		return compareDataList;
	}

	public void setCompareDataList(ArrayList compareDataList) {
		this.compareDataList = compareDataList;
	}

	public String getCurrent_values() {
		return current_values;
	}

	public void setCurrent_values(String current_values) {
		this.current_values = current_values;
	}

	public ArrayList getDataHeadList() {
		return dataHeadList;
	}

	public void setDataHeadList(ArrayList dataHeadList) {
		this.dataHeadList = dataHeadList;
	}

	public String getOpt() {
		return opt;
	}

	public void setOpt(String opt) {
		this.opt = opt;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}

	public String getKmethod() {
		return kmethod;
	}

	public void setKmethod(String kmethod) {
		this.kmethod = kmethod;
	}

	public ArrayList getU04DataList() {
		return u04DataList;
	}

	public void setU04DataList(ArrayList dataList) {
		u04DataList = dataList;
	}

	public ArrayList getDataHeadList_u05() {
		return dataHeadList_u05;
	}

	public void setDataHeadList_u05(ArrayList dataHeadList_u05) {
		this.dataHeadList_u05 = dataHeadList_u05;
	}

	public ArrayList getDataList_u05() {
		return dataList_u05;
	}

	public void setDataList_u05(ArrayList dataList_u05) {
		this.dataList_u05 = dataList_u05;
	}

	public String getT5_desc() {
		return t5_desc;
	}

	public void setT5_desc(String t5_desc) {
		this.t5_desc = t5_desc;
	}

	public String getT3_desc() {
		return t3_desc;
	}

	public void setT3_desc(String t3_desc) {
		this.t3_desc = t3_desc;
	}

	
	public String getUnitcodes() {
		return unitcodes;
	}

	public ArrayList getImport_deleteinfo() {
		return import_deleteinfo;
	}

	public void setImport_deleteinfo(ArrayList import_deleteinfo) {
		this.import_deleteinfo = import_deleteinfo;
	}

	public void setUnitcodes(String unitcodes) {
		this.unitcodes = unitcodes;
	}

	public String getAddother() {
		return addother;
	}

	public void setAddother(String addother) {
		this.addother = addother;
	}



	


 
    //end insert 
  
   
 
}
