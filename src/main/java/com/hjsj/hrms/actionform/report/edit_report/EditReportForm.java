package com.hjsj.hrms.actionform.report.edit_report;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class EditReportForm extends FrameForm {
	String returnType="";
	String htmlCode="";
	String rows="0";
	String cols="0";
	String tabid="0";
	String userId="0";
	String userName="0";
	String param_str="";						//参数名
	String sortName="";							//表类名
	String sortId="";							//表类id
	String reboundDescription="false";              //打回说明（ false：菜单中没有此项 true：有此项  --针对编辑报表模块）
	String narch="0";                           //报表类型
	//操作权限
	String appeal="true";
	String save="true";
	String freeze="true";						//封存权限
	String pigeonhole="true";                   //归档权限
	String appeal2="true";                      //报表汇总里上报权限
	String isPopedom="1";                       //是否有表权限
	//临时数据

	ArrayList  formulaList=new ArrayList();         //计算公式集合（用于报表计算）
	String     formulaType="a";				        // a:表内计算  b:表间计算
	//liuy 2015-2-13 6807：cs扫描库设置为本报表设置，bs自动取数/反查：对1号表取数后反查，反查不对 start
	String     dbpreStr="";				        //用于保存重新取数时设置的人员库，反查时使用
	String     appdate="";				        //用于保存重新取数时设置的截止日期，反查时使用
	String     start="";				        //用于保存重新取数时设置的开始日期，反查时使用
	//liuy 2015-2-13 end
	//上报列表
	ArrayList  appealInfoList=new ArrayList();
	ArrayList  tsortList=new ArrayList();
	ArrayList  subUnitList=new ArrayList();
	String        subUnitSize="0";
	String     appealUnitCode="";				    //上报单位unitcode
	
	String operateObject="1";					     // 1：编辑没上报表 2：编辑上报后的表
	//报表汇总-编辑报表
	ArrayList tabList=new ArrayList();				//权限下的所有报表列表集合
	String status="";								//=-1，未填	=0,正在编辑	=1,已上报	 =2,打回	=3,封存（基层单位的数据不让修改）,= 4 审批中
	String unitcode="";								//填报单位
	String selfUnitcode="";
	
	String unitName="";                             //组织名称
	String reportName="";                           //报表名称
	
	String desc="";                                 //上报标识信息说明
	String turnDownDesc="";                         //打回描述
	
	
	ArrayList reverseResultList=new ArrayList();    //反查结果集
	String reverseHtml="";                          //反查结果
	String flag="0";								//是否给予反查 0:不反查
	
	String isSubNode="false";                       //是否是叶子节点
	
	String isCollectCheck="0";                      // 0:没有汇总校验  1:有汇总校验
	
	String reverseSql="";							//反查sql
	String setMap_str="";
	String fieldItem_str="";
	String scanMode="";
	String existunicode="";							//0:当前人员没填报单位
	String use_scope_cond="";						//是否使用统计口径，0不使用(默认值), 1使用
	String scopeid ="0";							//统计口径id
	String scopename ="";							//口径名称
	String scopeownerunitid ="";					//口径所属单位id
	ArrayList scopelist = new ArrayList();			//口径列表
	String reportlisthref = "";						//查阅返回的连接
	String dmlflag="";								//duml 判断上级单位和袭击单位是否同时为上报状态 （在报表汇总编辑报表中）
	String dmlunit="";
	String dmlsort="";
	String dmlunitname="";
	ArrayList sortlist=new ArrayList();
	String selectunit="";
	String selfstatus="";
	private String right_fields[];
	ArrayList subunitsInfo=new ArrayList();
	private String auto_archive="";					//报表是否自动归档 存储在 tname表的xmlstyle字段中 节点为 auto_archive 0/1 否/是
	//报表上报是否审批  zhaoxg 
	private String isApproveflag = "";				//报表上报按钮的显示
	private String isUpapprove = "";				//是否是顶级审批人
	private String username = "";					//上报人的信息
	private String unitcode1 = "";					//上报人的单位
	private String username1 = "";					//审批人信息
	private String obj1 = "";						//判断从哪里进入的编辑报表  =1 我的任务  =2从左面菜单
	private String isPrint = "";					//是否支持打印功能
	private RecordVo treport_ctrl=new RecordVo("treport_ctrl");
	
	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getAuto_archive() {
		return auto_archive;
	}

	public void setAuto_archive(String auto_archive) {
		this.auto_archive = auto_archive;
	}

	public ArrayList getSubunitsInfo() {
		return subunitsInfo;
	}

	public void setSubunitsInfo(ArrayList subunitsInfo) {
		this.subunitsInfo = subunitsInfo;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public ArrayList getSortlist() {
		return sortlist;
	}

	public void setSortlist(ArrayList sortlist) {
		this.sortlist = sortlist;
	}

	public String getDmlunit() {
		return dmlunit;
	}

	public void setDmlunit(String dmlunit) {
		this.dmlunit = dmlunit;
	}

	public String getDmlsort() {
		return dmlsort;
	}

	public void setDmlsort(String dmlsort) {
		this.dmlsort = dmlsort;
	}

	@Override
    public void outPutFormHM() {
		this.setIsCollectCheck((String)this.getFormHM().get("isCollectCheck"));
		this.setIsSubNode((String)this.getFormHM().get("isSubNode"));
		
		this.setUnitName((String)this.getFormHM().get("unitName"));
		this.setReportName((String)this.getFormHM().get("reportName"));
		
		this.setHtmlCode((String)this.getFormHM().get("htmlCode"));
		this.setRows((String)this.getFormHM().get("rows"));
		this.setCols((String)this.getFormHM().get("cols"));
		if(this.getFormHM().get("tabid")!=null)
			this.setTabid((String)this.getFormHM().get("tabid"));
		this.setUserId((String)this.getFormHM().get("userId"));
		this.setUserName((String)this.getFormHM().get("userName"));
		this.setParam_str((String)this.getFormHM().get("param_str"));
		this.setAppeal((String)this.getFormHM().get("appeal"));
		this.setSave((String)this.getFormHM().get("save"));
		this.setFormulaList((ArrayList)this.getFormHM().get("formulaList"));
		this.setFormulaType((String)this.getFormHM().get("formulaType"));
		this.setDbpreStr((String)this.getFormHM().get("dbpreStr"));
		this.setAppdate((String)this.getFormHM().get("appdate"));
		this.setStart((String)this.getFormHM().get("start"));
		this.setAppealInfoList((ArrayList)this.getFormHM().get("appealInfoList"));
		this.setSortName((String)this.getFormHM().get("sortName"));
		this.setSortId((String)this.getFormHM().get("sortId"));
		this.setOperateObject((String)this.getFormHM().get("operateObject"));
		this.setStatus((String)this.getFormHM().get("status"));
		this.setTabList((ArrayList)this.getFormHM().get("tabList"));
		this.setUnitcode((String)this.getFormHM().get("unitcode"));
		this.setTsortList((ArrayList)this.getFormHM().get("tsortList"));
		this.setSelfUnitcode((String)this.getFormHM().get("selfUnitcode"));
		this.setDesc((String)this.getFormHM().get("desc"));
		this.setFreeze((String)this.getFormHM().get("freeze"));
		this.setReboundDescription((String)this.getFormHM().get("reboundDescription"));
		this.setReverseResultList((ArrayList)this.getFormHM().get("reverseResultList"));
		this.setReverseHtml((String)this.getFormHM().get("reverseHtml"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setNarch((String)this.getFormHM().get("narch"));
		this.setPigeonhole((String)this.getFormHM().get("pigeonhole"));
		this.setSubUnitList((ArrayList)this.getFormHM().get("subUnitList"));
		if(this.getFormHM().get("subUnitList")!=null)
			this.setSubUnitSize(String.valueOf(((ArrayList)this.getFormHM().get("subUnitList")).size()));	
		this.setAppeal2((String)this.getFormHM().get("appeal2"));
		this.setIsPopedom((String)this.getFormHM().get("isPopedom"));
		
		if(this.unitcode!=null&&this.selfUnitcode!=null&&this.unitcode.equals(this.selfUnitcode))
			this.setTurnDownDesc("查阅驳回原因");
		else
			this.setTurnDownDesc("驳回");
		
		this.setReverseSql((String)this.getFormHM().get("reverseSql"));
		this.setSetMap_str((String)this.getFormHM().get("setMap_str"));
		this.setFieldItem_str((String)this.getFormHM().get("fieldItem_str"));
		this.setScanMode((String)this.getFormHM().get("scanMode"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setExistunicode((String)this.getFormHM().get("existunicode"));
		this.setUse_scope_cond((String)this.getFormHM().get("use_scope_cond"));
		this.setScopeid((String)this.getFormHM().get("scopeid"));
		this.setScopename((String)this.getFormHM().get("scopename"));
		this.setScopeownerunitid((String)this.getFormHM().get("scopeownerunitid"));
		this.setScopelist((ArrayList)this.getFormHM().get("scopelist"));
		this.setReportlisthref((String)this.getFormHM().get("reportlisthref"));
		this.setDmlflag((String)this.getFormHM().get("dmlflag"));
		this.setDmlsort((String)this.getFormHM().get("dmlsort"));
		this.setDmlunit((String)this.getFormHM().get("dmlunit"));
		this.setSortlist((ArrayList)this.getFormHM().get("sortlist"));
		this.setDmlunitname((String)this.getFormHM().get("dmlunitname"));
		this.setRight_fields((String[])this.getFormHM().get("right_fields"));
		this.setSelectunit((String)this.getFormHM().get("selectunit"));
		this.setSelfstatus((String)this.getFormHM().get("selfstatus"));
		this.setSubunitsInfo((ArrayList)this.getFormHM().get("subunitsInfo"));
		this.setAppealUnitCode((String)this.getFormHM().get("appealUnitCode"));
		this.setAuto_archive((String)this.getFormHM().get("auto_archive"));
		this.setIsApproveflag((String) this.getFormHM().get("isApproveflag"));
		this.setIsUpapprove((String) this.getFormHM().get("isUpapprove"));
		this.setUsername((String) this.getFormHM().get("username"));
		this.setUsername1((String) this.getFormHM().get("username1"));
		this.setUnitcode1((String) this.getFormHM().get("unitcode1"));
		this.setObj1((String) this.getFormHM().get("obj1"));
		this.setTreport_ctrl((RecordVo) this.getFormHM().get("treport_ctrl"));
		this.setIsPrint((String) this.getFormHM().get("isPrint"));
		this.setReturnType((String) this.getFormHM().get("returnType"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("tabid",this.getTabid());
		this.getFormHM().put("rows",this.getRows());
		this.getFormHM().put("cols",this.getCols());
		this.getFormHM().put("sortId",this.getSortId());
		this.getFormHM().put("appealUnitCode",this.getAppealUnitCode());
		this.getFormHM().put("desc",this.getDesc());
		this.getFormHM().put("right_fields", this.getRight_fields());
		this.getFormHM().put("isApproveflag", this.getIsApproveflag());
		this.getFormHM().put("isUpapprove", this.getIsUpapprove());
		this.getFormHM().put("username", this.getUsername());
		this.getFormHM().put("username1", this.getUsername1());
		this.getFormHM().put("unitcode1", this.getUnitcode1());
		this.getFormHM().put("obj1", this.getObj1());
		this.getFormHM().put("treport_ctrl", this.getTreport_ctrl());
		this.getFormHM().put("isPrint", this.getIsPrint());
		this.getFormHM().put("dbpreStr", this.getDbpreStr());
		this.getFormHM().put("start", this.getStart());
		this.getFormHM().put("appdate", this.getAppdate());
	}

	@Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		this.setDbpreStr("");
		this.setStart("");
		this.setAppdate("");
	}
		
	public String getHtmlCode() {
		return htmlCode;
	}

	public void setHtmlCode(String htmlCode) {
		this.htmlCode = htmlCode;
	}

	public String getCols() {
		return cols;
	}

	public void setCols(String cols) {
		this.cols = cols;
	}

	public String getRows() {
		return rows;
	}

	public void setRows(String rows) {
		this.rows = rows;
	}

	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getParam_str() {
		return param_str;
	}

	public void setParam_str(String param_str) {
		this.param_str = param_str;
	}

	public String getAppeal() {
		return appeal;
	}

	public void setAppeal(String appeal) {
		this.appeal = appeal;
	}

	public String getSave() {
		return save;
	}

	public void setSave(String save) {
		this.save = save;
	}



	public ArrayList getFormulaList() {
		return formulaList;
	}

	public void setFormulaList(ArrayList formulaList) {
		this.formulaList = formulaList;
	}

	public String getFormulaType() {
		return formulaType;
	}

	public void setFormulaType(String formulaType) {
		this.formulaType = formulaType;
	}
	
	public String getDbpreStr() {
		return dbpreStr;
	}

	public void setDbpreStr(String dbpreStr) {
		this.dbpreStr = dbpreStr;
	}
	
	public String getAppdate() {
		return appdate;
	}

	public void setAppdate(String appdate) {
		this.appdate = appdate;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public ArrayList getAppealInfoList() {
		return appealInfoList;
	}

	public void setAppealInfoList(ArrayList appealInfoList) {
		this.appealInfoList = appealInfoList;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public String getSortId() {
		return sortId;
	}

	public void setSortId(String sortId) {
		this.sortId = sortId;
	}

	

	public ArrayList getTabList() {
		return tabList;
	}

	public void setTabList(ArrayList tabList) {
		this.tabList = tabList;
	}

	public String getOperateObject() {
		return operateObject;
	}

	public void setOperateObject(String operateObject) {
		this.operateObject = operateObject;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUnitcode() {
		return unitcode;
	}

	public void setUnitcode(String unitcode) {
		this.unitcode = unitcode;
	}

	public ArrayList getTsortList() {
		return tsortList;
	}

	public void setTsortList(ArrayList tsortList) {
		this.tsortList = tsortList;
	}

	public String getSelfUnitcode() {
		return selfUnitcode;
	}

	public void setSelfUnitcode(String selfUnitcode) {
		this.selfUnitcode = selfUnitcode;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getFreeze() {
		return freeze;
	}

	public void setFreeze(String freeze) {
		this.freeze = freeze;
	}

	public String getReboundDescription() {
		return reboundDescription;
	}

	public void setReboundDescription(String reboundDescription) {
		this.reboundDescription = reboundDescription;
	}

	public ArrayList getReverseResultList() {
		return reverseResultList;
	}

	public void setReverseResultList(ArrayList reverseResultList) {
		this.reverseResultList = reverseResultList;
	}

	public String getReverseHtml() {
		return reverseHtml;
	}

	public void setReverseHtml(String reverseHtml) {
		this.reverseHtml = reverseHtml;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getNarch() {
		return narch;
	}

	public void setNarch(String narch) {
		this.narch = narch;
	}

	public String getPigeonhole() {
		return pigeonhole;
	}

	public void setPigeonhole(String pigeonhole) {
		this.pigeonhole = pigeonhole;
	}

	public ArrayList getSubUnitList() {
		return subUnitList;
	}

	public void setSubUnitList(ArrayList subUnitList) {
		this.subUnitList = subUnitList;
	}

	public String getAppealUnitCode() {
		return appealUnitCode;
	}

	public void setAppealUnitCode(String appealUnitCode) {
		this.appealUnitCode = appealUnitCode;
	}

	public String getAppeal2() {
		return appeal2;
	}

	public void setAppeal2(String appeal2) {
		this.appeal2 = appeal2;
	}

	public String getIsPopedom() {
		return isPopedom;
	}

	public void setIsPopedom(String isPopedom) {
		this.isPopedom = isPopedom;
	}

	public String getTurnDownDesc() {
		return turnDownDesc;
	}

	public void setTurnDownDesc(String turnDownDesc) {
		this.turnDownDesc = turnDownDesc;
	}

	public String getSubUnitSize() {
		return subUnitSize;
	}

	public void setSubUnitSize(String subUnitSize) {
		this.subUnitSize = subUnitSize;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getIsSubNode() {
		return isSubNode;
	}

	public void setIsSubNode(String isSubNode) {
		this.isSubNode = isSubNode;
	}

	public String getIsCollectCheck() {
		return isCollectCheck;
	}

	public void setIsCollectCheck(String isCollectCheck) {
		this.isCollectCheck = isCollectCheck;
	}

	public String getReverseSql() {
		return reverseSql;
	}

	public void setReverseSql(String reverseSql) {
		this.reverseSql = reverseSql;
	}

	public String getSetMap_str() {
		return setMap_str;
	}

	public void setSetMap_str(String setMap_str) {
		this.setMap_str = setMap_str;
	}

	public String getFieldItem_str() {
		return fieldItem_str;
	}

	public void setFieldItem_str(String fieldItem_str) {
		this.fieldItem_str = fieldItem_str;
	}

	public String getScanMode() {
		return scanMode;
	}

	public void setScanMode(String scanMode) {
		this.scanMode = scanMode;
	}

	public String getExistunicode() {
		return existunicode;
	}

	public void setExistunicode(String existunicode) {
		this.existunicode = existunicode;
	}

	public String getUse_scope_cond() {
		return use_scope_cond;
	}

	public void setUse_scope_cond(String use_scope_cond) {
		this.use_scope_cond = use_scope_cond;
	}

	public String getScopeid() {
		return scopeid;
	}

	public void setScopeid(String scopeid) {
		this.scopeid = scopeid;
	}

	public String getScopename() {
		return scopename;
	}

	public void setScopename(String scopename) {
		this.scopename = scopename;
	}

	public String getReportlisthref() {
		return reportlisthref;
	}

	public void setReportlisthref(String reportlisthref) {
		this.reportlisthref = reportlisthref;
	}

	public String getScopeownerunitid() {
		return scopeownerunitid;
	}

	public void setScopeownerunitid(String scopeownerunitid) {
		this.scopeownerunitid = scopeownerunitid;
	}

	public ArrayList getScopelist() {
		return scopelist;
	}

	public void setScopelist(ArrayList scopelist) {
		this.scopelist = scopelist;
	}

	public String getDmlflag() {
		return dmlflag;
	}

	public void setDmlflag(String dmlflag) {
		this.dmlflag = dmlflag;
	}

	public String getDmlunitname() {
		return dmlunitname;
	}

	public void setDmlunitname(String dmlunitname) {
		this.dmlunitname = dmlunitname;
	}

	public String getSelectunit() {
		return selectunit;
	}

	public void setSelectunit(String selectunit) {
		this.selectunit = selectunit;
	}

	public String getSelfstatus() {
		return selfstatus;
	}

	public void setSelfstatus(String selfstatus) {
		this.selfstatus = selfstatus;
	}

	public String getIsApproveflag() {
		return isApproveflag;
	}

	public void setIsApproveflag(String isApproveflag) {
		this.isApproveflag = isApproveflag;
	}

	public String getIsUpapprove() {
		return isUpapprove;
	}

	public void setIsUpapprove(String isUpapprove) {
		this.isUpapprove = isUpapprove;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUnitcode1() {
		return unitcode1;
	}

	public void setUnitcode1(String unitcode1) {
		this.unitcode1 = unitcode1;
	}

	public RecordVo getTreport_ctrl() {
		return treport_ctrl;
	}

	public void setTreport_ctrl(RecordVo treport_ctrl) {
		this.treport_ctrl = treport_ctrl;
	}

	public String getUsername1() {
		return username1;
	}

	public void setUsername1(String username1) {
		this.username1 = username1;
	}

	public String getObj1() {
		return obj1;
	}

	public void setObj1(String obj1) {
		this.obj1 = obj1;
	}

	public String getIsPrint() {
		return isPrint;
	}

	public void setIsPrint(String isPrint) {
		this.isPrint = isPrint;
	}


	
	
}
