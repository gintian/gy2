package com.hjsj.hrms.actionform.performance.options;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:ConfigParameterForm.java</p>
 * <p>Description>:绩效管理 配置参数</p>
 * <p>Company:HJSJ</p>
 * <p>@version: 1.0</p>
 * <p>@author: JinChunhai
 */

public class ConfigParameterForm extends FrameForm
{
	private String busitype = "0";	// 业务分类字段 =0(绩效考核); =1(能力素质)
	private String sub_page;	
    private String redio;
    private String treeCode;
    private ArrayList itemList = new ArrayList();
    private ArrayList fieldList = new ArrayList();
    private ArrayList setlist = new ArrayList();
    private ArrayList codeList = new ArrayList();
    private ArrayList sortList = new ArrayList();
    private ArrayList dblist = new ArrayList();
    private ArrayList timeFieldList = new ArrayList();
    private String timeitemid = "";
    private String toScope = "";
    private String fromScope = "";
    private String dbname = "";
    private String codeid = "";
    private String itemid = "";
    private String fieldid = "";
    private String fromnum = "";
    private String tonum = "";
    private String fromdate = "";
    private String todate = "";
    private String sqlstr = "";
    private String wherestr = "";
    private String column = "";
    private String orderby = "";
    private String setid = "";
    private String searchtext = "";
    private String sortid = "";
    private String tablename = "";
    private String kh_setdesc = "";
    private String kh_set_lookdesc = "";
    private String khtitle = "";
    private String highsearch = "";
    private String ecxelsql = "";// 打印excel sql语句
    private String model = "1"; // 1:综合评定(总分排名) 2：查询使用
    private String sortitem = "";
    private String isTargetCardTemp = "";// 是否需要目标卡制定模板
    private String isTargetAppraisesTemp = "";// 是否需要目标卡评估模板
    private String targetCardTemp = "";// 目标卡制定模板
    private String targetAppraisesTemp = "";// 目标卡评估模板
    ArrayList emailTempList = new ArrayList();// 邮件模板列表
    ArrayList busiTempList = new ArrayList();// 业务模板列表
    private String appealTemplate = "";// 申诉模板
    private String interviewTemplate = "";// 面谈模板
    private String togetherCommit = "";// 统一提交
    private String controlByKHMoudle = ""; // 考核计划按模板权限控制, True,False(默认)
    private String targetPostSet = "";// 岗位职责子集   
    private ArrayList targetPostSetList = new ArrayList();
    private String targetItem = "";// 项 目 指 标
    private ArrayList targetItemList = new ArrayList();
    private ArrayList targetAccordList = new ArrayList();
    private String targetAccordStr = "";
    private String descriptionItem = "";// 指标解释指标
    private String principleItem = "";// 评分说明指标
    private ArrayList targetItemList2 = new ArrayList();// 目标卡指标
    private String returnvalue1="";
    private String feedBackTemplate ="";//考核结果反馈表通知邮件模板
    
    private String allowLeaderTrace="False";   //允许领导制定及批准跟踪指标, True(默认) False  
    private String targetTraceItem = ""; //目标卡跟踪显示指标
    private String targetCollectItem = ""; //目标卡采集指标    
    private String targetDefineItem = ""; //目标卡指标   
    private String targetCalcItem = ""; //目标卡计算指标
    private String tarItem = "";//所有有公式的计算指标
    private String calItemStr="";   //目标表计算指标串
    private ArrayList targetCalcItemList = new ArrayList();//目标计算项目
    private ArrayList targetDefineItemList = new ArrayList();//目标卡显示项目
    private ArrayList targetCollectItemList = new ArrayList();//目标采集项目
    private ArrayList targetTraceItemList = new ArrayList();//目标卡跟踪显示项目 
    private String returnvalue;
    private String rightCtrlByPerObjType="false";//按计划考核对象类型权限控制 默认为False 
	private ArrayList evaluateList = new ArrayList();//360考核结果显示列表
    private ArrayList objectiveList = new ArrayList();//目标考核结果显示列表    
    private String e_str;
    private String o_str;
    
    private String blind_360;    //360的评价盲点值
    private String blind_goal;   //目标考核的评价盲点值
	
    /////////目标卡部门职责参数/////////
    String departDutySet = "";// 部门职责子集
	ArrayList departDutySetList =  new ArrayList();
	String projectField = "";// 项目指标
	ArrayList projectFieldList = new ArrayList();//项目指标列表
	String validDateField = "";//有效时间指标
	ArrayList validDateFieldList = new ArrayList();//有效时间指标列表
	ArrayList allDataList = new ArrayList();//要显示的数据列表
	String departTextValue = "";//字段对应关系
	///////////////////////////////////////////
	//-----------铁血网绩效  zhaoxg 2013-3-20---------------------
	private String istargetTasktracking = "";
	private String targetTasktracking = "";
	private String istargetTaskofadjusting = "";
	private String targetTaskofadjusting = "";
	//----------------------------------------------------------
	private String nameLinkCard="";//考核对象基本信息表（人员）	zhaoxg 2014-4-23
	private ArrayList rnameList=new ArrayList();//人员登记表列表  zhaoxg 2014-4-23
    @Override
    public void inPutTransHM()
    {  
    	this.getFormHM().put("istargetTasktracking", this.getIstargetTasktracking());
    	this.getFormHM().put("targetTasktracking", this.getTargetTasktracking());
    	this.getFormHM().put("istargetTaskofadjusting", this.getIstargetTaskofadjusting());
    	this.getFormHM().put("targetTaskofadjusting", this.getTargetTaskofadjusting());
    	this.getFormHM().put("controlByKHMoudle", this.getControlByKHMoudle());
        this.getFormHM().put("e_str", this.getE_str());
        this.getFormHM().put("o_str", this.getO_str());
	    this.getFormHM().put("rightCtrlByPerObjType",this.getRightCtrlByPerObjType());
	    this.getFormHM().put("sub_page",this.getSub_page());
	    this.getFormHM().put("tarItem",this.getTarItem());
	    this.getFormHM().put("targetCalcItem", this.getTargetCalcItem());
	    this.getFormHM().put("targetCalcItemList", this.getTargetCalcItemList());
	    this.getFormHM().put("feedBackTemplate", this.getFeedBackTemplate());
		this.getFormHM().put("togetherCommit", this.getTogetherCommit());
		this.getFormHM().put("appealTemplate", this.getAppealTemplate());
		this.getFormHM().put("interviewTemplate", this.getInterviewTemplate());	
		this.getFormHM().put("isTargetCardTemp", this.getIsTargetCardTemp());
		this.getFormHM().put("isTargetAppraisesTemp", this.getIsTargetAppraisesTemp());
		this.getFormHM().put("targetCardTemp", this.getTargetCardTemp());
		this.getFormHM().put("targetAppraisesTemp", this.getTargetAppraisesTemp());
		this.getFormHM().put("emailTempList", this.getEmailTempList());
		this.getFormHM().put("busiTempList", this.getBusiTempList());	
		this.getFormHM().put("timeitemid", this.getTimeitemid());
		this.getFormHM().put("fromScope", this.getFromScope());
		this.getFormHM().put("toScope", this.getToScope());	
		this.getFormHM().put("redio", this.getRedio());
		this.getFormHM().put("itemList", this.getItemList());
		this.getFormHM().put("codeid", this.getCodeid());
		this.getFormHM().put("itemid", this.getItemid());
		this.getFormHM().put("fieldid", this.getFieldid());
		this.getFormHM().put("fromnum", this.getFromnum());
		this.getFormHM().put("tonum", this.getTonum());
		this.getFormHM().put("fromdate", this.getFromdate());
		this.getFormHM().put("todate", this.getTodate());
		this.getFormHM().put("sqlstr", this.getSqlstr());
		this.getFormHM().put("setid", this.getSetid());
		this.getFormHM().put("searchtext", this.getSearchtext());
		this.getFormHM().put("sortid", this.getSortid());
		this.getFormHM().put("dbname", this.getDbname());
		this.getFormHM().put("treeCode", this.getTreeCode());
		this.getFormHM().put("highsearch", this.getHighsearch());
		this.getFormHM().put("sortitem", this.getSortitem());	
		this.getFormHM().put("targetPostSet", this.getTargetPostSet());
		this.getFormHM().put("targetPostSetList", this.getTargetPostSetList());
		this.getFormHM().put("targetItem", this.getTargetItem());
		this.getFormHM().put("targetItemList", this.getTargetItemList());
		this.getFormHM().put("targetAccordList", this.getTargetAccordList());
		this.getFormHM().put("targetAccordStr", this.getTargetAccordStr());	
		this.getFormHM().put("descriptionItem", this.getDescriptionItem());
		this.getFormHM().put("principleItem", this.getPrincipleItem());
		this.getFormHM().put("targetItemList2", this.getTargetItemList2());		
		this.getFormHM().put("allowLeaderTrace", this.getAllowLeaderTrace());
		this.getFormHM().put("targetCollectItem", this.getTargetCollectItem());
		this.getFormHM().put("targetTraceItem", this.getTargetTraceItem());
		this.getFormHM().put("targetDefineItem", this.getTargetDefineItem());		
		this.getFormHM().put("targetCollectItemList", this.getTargetCollectItemList());    	
		this.getFormHM().put("targetTraceItemList", this.getTargetTraceItemList());
		this.getFormHM().put("targetDefineItemList", this.getTargetDefineItemList());
		this.getFormHM().put("blind_360", this.getBlind_360());
		this.getFormHM().put("blind_goal", this.getBlind_goal());
		
		this.getFormHM().put("departDutySet", this.getDepartDutySet());
		this.getFormHM().put("departDutySetList", this.getDepartDutySetList());
		this.getFormHM().put("projectField", this.getProjectField());
		this.getFormHM().put("projectFieldList", this.getProjectFieldList());
		this.getFormHM().put("validDateField", this.getValidDateField());
		this.getFormHM().put("validDateFieldList", this.getValidDateFieldList());
		this.getFormHM().put("allDataList", this.getValidDateFieldList());
		this.getFormHM().put("departTextValue", this.getDepartTextValue());
		this.getFormHM().put("busitype", this.getBusitype());
		this.getFormHM().put("nameLinkCard", this.getNameLinkCard());
		this.getFormHM().put("rnameList", this.getRnameList());
    }

    @Override
    public void outPutFormHM()
    {
    	
    	this.setControlByKHMoudle((String)this.getFormHM().get("controlByKHMoudle"));
    	this.setE_str((String)this.getFormHM().get("e_str"));
    	this.setO_str((String)this.getFormHM().get("o_str"));
    	this.setEvaluateList((ArrayList)this.getFormHM().get("evaluateList"));
    	this.setObjectiveList((ArrayList)this.getFormHM().get("objectiveList"));
	    this.setRightCtrlByPerObjType((String)this.getFormHM().get("rightCtrlByPerObjType"));	
	    this.setSub_page((String)this.getFormHM().get("sub_page"));	
	    this.setCalItemStr((String)this.getFormHM().get("calItemStr"));
	    this.setTarItem((String)this.getFormHM().get("tarItem"));	
	    this.setTargetCalcItem((String) this.getFormHM().get("targetCalcItem"));	
	    this.setTargetCalcItemList((ArrayList) this.getFormHM().get("targetCalcItemList"));	
	    this.setReturnflag((String)this.getFormHM().get("returnflag"));  	
	    this.setFeedBackTemplate((String) this.getFormHM().get("feedBackTemplate"));
		this.setTargetAccordStr((String) this.getFormHM().get("targetAccordStr"));
		this.setTogetherCommit((String) this.getFormHM().get("togetherCommit"));
		this.setAppealTemplate((String) this.getFormHM().get("appealTemplate"));
		this.setInterviewTemplate((String) this.getFormHM().get("interviewTemplate"));
		this.setIsTargetAppraisesTemp((String) this.getFormHM().get("isTargetAppraisesTemp"));
		this.setIsTargetCardTemp((String) this.getFormHM().get("isTargetCardTemp"));
		this.setTargetAppraisesTemp((String) this.getFormHM().get("targetAppraisesTemp"));
		this.setTargetCardTemp((String) this.getFormHM().get("targetCardTemp"));
		this.setEmailTempList((ArrayList) this.getFormHM().get("emailTempList"));
		this.setBusiTempList((ArrayList) this.getFormHM().get("busiTempList"));
	
		this.setTimeFieldList((ArrayList) this.getFormHM().get("timeFieldList"));
		this.setModel((String) this.getFormHM().get("model"));
		this.setFromScope((String) this.getFormHM().get("fromScope"));
		this.setToScope((String) this.getFormHM().get("toScope"));
		this.setTimeitemid((String) this.getFormHM().get("timeitemid"));
	
		this.setRedio((String) this.getFormHM().get("redio"));
		this.setItemList((ArrayList) this.getFormHM().get("itemList"));
		this.setTreeCode((String) this.getFormHM().get("treeCode"));
		this.setFieldList((ArrayList) this.getFormHM().get("fieldList"));
		this.setSetlist((ArrayList) this.getFormHM().get("setlist"));
		this.setCodeList((ArrayList) this.getFormHM().get("codeList"));
		this.setCodeid((String) this.getFormHM().get("codeid"));
		this.setItemid((String) this.getFormHM().get("itemid"));
		this.setFieldid((String) this.getFormHM().get("fieldid"));
		this.setFromnum((String) this.getFormHM().get("fromnum"));
		this.setTonum((String) this.getFormHM().get("tonum"));
		this.setFromdate((String) this.getFormHM().get("fromdate"));
		this.setTodate((String) this.getFormHM().get("todate"));
		this.setSqlstr((String) this.getFormHM().get("sqlstr"));
		this.setWherestr((String) this.getFormHM().get("wherestr"));
		this.setColumn((String) this.getFormHM().get("column"));
		this.setOrderby((String) this.getFormHM().get("orderby"));
		this.setSetid((String) this.getFormHM().get("setid"));
		this.setSearchtext((String) this.getFormHM().get("searchtext"));
		this.setSortid((String) this.getFormHM().get("sortid"));
		this.setSortList((ArrayList) this.getFormHM().get("sortList"));
		this.setDblist((ArrayList) this.getFormHM().get("dblist"));
		this.setDbname((String) this.getFormHM().get("dbname"));
		this.setTablename((String) this.getFormHM().get("tablename"));
		this.setKh_setdesc((String) this.getFormHM().get("kh_setdesc"));
		this.setKh_set_lookdesc((String) this.getFormHM().get("kh_set_lookdesc"));
		this.setKhtitle((String) this.getFormHM().get("khtitle"));
		this.setHighsearch((String) this.getFormHM().get("highsearch"));
		this.setSortitem((String) this.getFormHM().get("sortitem"));
		this.setEcxelsql((String) this.getFormHM().get("ecxelsql"));
		this.setTargetPostSet((String) this.getFormHM().get("targetPostSet"));
		this.setTargetPostSetList((ArrayList) this.getFormHM().get("targetPostSetList"));
		this.setTargetItem((String) this.getFormHM().get("targetItem"));
		this.setTargetItemList((ArrayList) this.getFormHM().get("targetItemList"));
		this.setTargetAccordList((ArrayList) this.getFormHM().get("targetAccordList"));
	
		this.setAllowLeaderTrace((String) this.getFormHM().get("allowLeaderTrace"));
		this.setDescriptionItem((String) this.getFormHM().get("descriptionItem"));
		this.setPrincipleItem((String) this.getFormHM().get("principleItem"));
		this.setTargetItemList2((ArrayList) this.getFormHM().get("targetItemList2"));
		
		this.setTargetTraceItem((String) this.getFormHM().get("targetTraceItem"));
		this.setTargetCollectItem((String) this.getFormHM().get("targetCollectItem"));
		this.setTargetDefineItem((String) this.getFormHM().get("targetDefineItem"));
		this.setTargetCollectItemList((ArrayList) this.getFormHM().get("targetCollectItemList"));
		this.setTargetTraceItemList((ArrayList) this.getFormHM().get("targetTraceItemList"));
		this.setTargetDefineItemList((ArrayList) this.getFormHM().get("targetDefineItemList"));
		this.setBlind_360((String)this.getFormHM().get("blind_360"));
		this.setBlind_goal((String)this.getFormHM().get("blind_goal"));
		
		this.setDepartDutySet((String)this.getFormHM().get("departDutySet"));
		this.setDepartDutySetList((ArrayList)this.getFormHM().get("departDutySetList"));
		this.setProjectField((String)this.getFormHM().get("projectField"));
		this.setProjectFieldList((ArrayList)this.getFormHM().get("projectFieldList"));
		this.setValidDateField((String)this.getFormHM().get("validDateField"));
		this.setValidDateFieldList((ArrayList)this.getFormHM().get("validDateFieldList"));
		this.setAllDataList((ArrayList)this.getFormHM().get("allDataList"));
		this.setDepartTextValue((String)this.getFormHM().get("departTextValue"));
		this.setIstargetTasktracking((String) this.getFormHM().get("istargetTasktracking"));
		this.setTargetTasktracking((String) this.getFormHM().get("targetTasktracking"));
		this.setIstargetTaskofadjusting((String) this.getFormHM().get("istargetTaskofadjusting"));
		this.setTargetTaskofadjusting((String) this.getFormHM().get("targetTaskofadjusting"));
		this.setBusitype((String)this.getFormHM().get("busitype"));
		this.setNameLinkCard((String) this.getFormHM().get("nameLinkCard"));
		this.setRnameList((ArrayList) this.getFormHM().get("rnameList"));
    }

    @Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1)
    {

		super.reset(arg0, arg1);
		this.setIsTargetAppraisesTemp("0");
		this.setIsTargetCardTemp("0");
		this.setTogetherCommit("0");
		this.setControlByKHMoudle("0");
		this.setAllowLeaderTrace("0");
		this.setIstargetTasktracking("0");
		this.setIstargetTaskofadjusting("0");
		this.setSub_page("");
		this.setRightCtrlByPerObjType("0");
    }

    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {

		try
		{
		    if ("/performance/totalrank/totalrank".equals(arg0.getPath()) && arg1.getParameter("b_look") != null)
		    {
				if (this.getPagination() != null)
				    this.getPagination().firstPage();
		    }
		    if("/performance/totalrank/totalrank".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
	        {
	            this.setSetid("");
	            this.getFormHM().put("setid", "");
	            this.setDbname("");
	            this.getFormHM().put("dbname", "");
	            this.setItemid("");
	            this.getFormHM().put("itemid", "");
	            this.getFormHM().clear();
	            this.setSearchtext("");
	            this.getFormHM().put("searchtext", "");
	            this.setFromnum("");
	            this.getFormHM().put("fromnum", "");
	            this.setTonum("");
	            this.getFormHM().put("tonum", "");
	            this.setFromdate("");
	            this.getFormHM().put("fromdate", "");
	            this.setTodate("");
	            this.getFormHM().put("todate", "");
	            this.setFromScope("");
	            this.getFormHM().put("fromScope", "");
	            this.setToScope("");
	            this.getFormHM().put("toScope", "");
	            this.getFormHM().put("timeitemid", "");
	            this.setTimeitemid("");
	            if(arg1.getParameter("returnvalue")==null)
		        {
	            	this.getFormHM().put("returnvalue1", "");
	            	this.setReturnvalue1("");
		        }else
		        {
		        	this.setReturnvalue1(arg1.getParameter("returnvalue"));
		        }
	        }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return super.validate(arg0, arg1);
    }

    public ArrayList getItemList()
    {
    	return itemList;
    }

    public void setItemList(ArrayList itemList)
    {
    	this.itemList = itemList;
    }

    public String getTreeCode()
    {

	return treeCode;
    }

    /**
         * @param treeCode
         *                The treeCode to set.
         */
    public void setTreeCode(String treeCode)
    {

	this.treeCode = treeCode;
    }

    public String getCodeid()
    {

	return codeid;
    }

    public void setCodeid(String codeid)
    {

	this.codeid = codeid;
    }

    public ArrayList getCodeList()
    {

	return codeList;
    }

    public void setCodeList(ArrayList codeList)
    {

	this.codeList = codeList;
    }

    public String getFieldid()
    {

	return fieldid;
    }

    public void setFieldid(String fieldid)
    {

	this.fieldid = fieldid;
    }

    public ArrayList getFieldList()
    {

	return fieldList;
    }

    public void setFieldList(ArrayList fieldList)
    {

	this.fieldList = fieldList;
    }

    public String getFromdate()
    {

	return fromdate;
    }

    public void setFromdate(String fromdate)
    {

	this.fromdate = fromdate;
    }

    public String getFromnum()
    {

	return fromnum;
    }

    public void setFromnum(String fromnum)
    {

	this.fromnum = fromnum;
    }

    public String getItemid()
    {

	return itemid;
    }

    public void setItemid(String itemid)
    {

	this.itemid = itemid;
    }

    public ArrayList getSetlist()
    {

	return setlist;
    }

    public void setSetlist(ArrayList setlist)
    {

	this.setlist = setlist;
    }

    public String getSqlstr()
    {

	return sqlstr;
    }

    public void setSqlstr(String sqlstr)
    {

	this.sqlstr = sqlstr;
    }

    public String getTodate()
    {

	return todate;
    }

    public void setTodate(String todate)
    {

	this.todate = todate;
    }

    public String getTonum()
    {

	return tonum;
    }

    public void setTonum(String tonum)
    {

	this.tonum = tonum;
    }

    public String getSetid()
    {

	return setid;
    }

    public void setSetid(String setid)
    {

	this.setid = setid;
    }

    public String getSearchtext()
    {

	return searchtext;
    }

    public void setSearchtext(String searchtext)
    {

	this.searchtext = searchtext;
    }

    public String getSortid()
    {

	return sortid;
    }

    public void setSortid(String sortid)
    {

	this.sortid = sortid;
    }

    public ArrayList getSortList()
    {

	return sortList;
    }

    public void setSortList(ArrayList sortList)
    {

	this.sortList = sortList;
    }

    public String getColumn()
    {

	return column;
    }

    public void setColumn(String column)
    {

	this.column = column;
    }

    public String getOrderby()
    {

	return orderby;
    }

    public void setOrderby(String orderby)
    {

	this.orderby = orderby;
    }

    public String getWherestr()
    {

	return wherestr;
    }

    public void setWherestr(String wherestr)
    {

	this.wherestr = wherestr;
    }

    public ArrayList getDblist()
    {

	return dblist;
    }

    public void setDblist(ArrayList dblist)
    {

	this.dblist = dblist;
    }

    public String getDbname()
    {

	return dbname;
    }

    public void setDbname(String dbname)
    {

	this.dbname = dbname;
    }

    public String getTablename()
    {

	return tablename;
    }

    public void setTablename(String tablename)
    {

	this.tablename = tablename;
    }

    public String getModel()
    {

	return model;
    }

    public void setModel(String model)
    {

	this.model = model;
    }

    public String getFromScope()
    {

	return fromScope;
    }

    public void setFromScope(String fromScope)
    {

	this.fromScope = fromScope;
    }

    public ArrayList getTimeFieldList()
    {

	return timeFieldList;
    }

    public void setTimeFieldList(ArrayList timeFieldList)
    {

	this.timeFieldList = timeFieldList;
    }

    public String getToScope()
    {

	return toScope;
    }

    public void setToScope(String toScope)
    {

	this.toScope = toScope;
    }

    public String getTimeitemid()
    {

	return timeitemid;
    }

    public void setTimeitemid(String timeitemid)
    {

	this.timeitemid = timeitemid;
    }

    public String getKh_setdesc()
    {

	return kh_setdesc;
    }

    public void setKh_setdesc(String kh_setdesc)
    {

	this.kh_setdesc = kh_setdesc;
    }

    public String getKh_set_lookdesc()
    {

	return kh_set_lookdesc;
    }

    public void setKh_set_lookdesc(String kh_set_lookdesc)
    {

	this.kh_set_lookdesc = kh_set_lookdesc;
    }

    public String getKhtitle()
    {

	return khtitle;
    }

    public void setKhtitle(String khtitle)
    {

	this.khtitle = khtitle;
    }

    public String getHighsearch()
    {

	return highsearch;
    }

    public void setHighsearch(String highsearch)
    {

	this.highsearch = highsearch;
    }

    public String getSortitem()
    {

	return sortitem;
    }

    public void setSortitem(String sortitem)
    {

	this.sortitem = sortitem;
    }

    public String getEcxelsql()
    {

	return ecxelsql;
    }

    public void setEcxelsql(String ecxelsql)
    {

	this.ecxelsql = ecxelsql;
    }

    public ArrayList getBusiTempList()
    {

	return busiTempList;
    }

    public void setBusiTempList(ArrayList busiTempList)
    {

	this.busiTempList = busiTempList;
    }

    public ArrayList getEmailTempList()
    {

	return emailTempList;
    }

    public void setEmailTempList(ArrayList emailTempList)
    {

	this.emailTempList = emailTempList;
    }

    public String getIsTargetAppraisesTemp()
    {

	return isTargetAppraisesTemp;
    }

    public void setIsTargetAppraisesTemp(String isTargetAppraisesTemp)
    {

	this.isTargetAppraisesTemp = isTargetAppraisesTemp;
    }

    public String getIsTargetCardTemp()
    {

	return isTargetCardTemp;
    }

    public void setIsTargetCardTemp(String isTargetCardTemp)
    {

	this.isTargetCardTemp = isTargetCardTemp;
    }

    public String getTargetAppraisesTemp()
    {

	return targetAppraisesTemp;
    }

    public void setTargetAppraisesTemp(String targetAppraisesTemp)
    {

	this.targetAppraisesTemp = targetAppraisesTemp;
    }

    public String getTargetCardTemp()
    {

	return targetCardTemp;
    }

    public void setTargetCardTemp(String targetCardTemp)
    {

	this.targetCardTemp = targetCardTemp;
    }

    public String getAppealTemplate()
    {

	return appealTemplate;
    }

    public void setAppealTemplate(String appealTemplate)
    {

	this.appealTemplate = appealTemplate;
    }

    public String getInterviewTemplate()
    {

	return interviewTemplate;
    }

    public void setInterviewTemplate(String interviewTemplate)
    {

	this.interviewTemplate = interviewTemplate;
    }

    public String getTargetTraceItem()
    {

	return targetTraceItem;
    }

    public void setTargetTraceItem(String targetTraceItem)
    {

	this.targetTraceItem = targetTraceItem;
    }

    public String getTogetherCommit()
    {

	return togetherCommit;
    }

    public void setTogetherCommit(String togetherCommit)
    {

	this.togetherCommit = togetherCommit;
    }

    public ArrayList getTargetCollectItemList()
    {

	return targetCollectItemList;
    }

    public void setTargetCollectItemList(ArrayList targetCollectItemList)
    {

	this.targetCollectItemList = targetCollectItemList;
    }

    public String getTargetCollectItem()
    {

	return targetCollectItem;
    }

    public void setTargetCollectItem(String targetCollectItem)
    {

	this.targetCollectItem = targetCollectItem;
    }

    public ArrayList getTargetAccordList()
    {

	return targetAccordList;
    }

    public void setTargetAccordList(ArrayList targetAccordList)
    {

	this.targetAccordList = targetAccordList;
    }

    public String getTargetItem()
    {

	return targetItem;
    }

    public void setTargetItem(String targetItem)
    {

	this.targetItem = targetItem;
    }

    public ArrayList getTargetItemList()
    {

	return targetItemList;
    }

    public void setTargetItemList(ArrayList targetItemList)
    {

	this.targetItemList = targetItemList;
    }

    public String getTargetPostSet()
    {

	return targetPostSet;
    }

    public void setTargetPostSet(String targetPostSet)
    {

	this.targetPostSet = targetPostSet;
    }

    public ArrayList getTargetPostSetList()
    {

	return targetPostSetList;
    }

    public void setTargetPostSetList(ArrayList targetPostSetList)
    {

	this.targetPostSetList = targetPostSetList;
    }

    public String getTargetAccordStr()
    {

	return targetAccordStr;
    }

    public void setTargetAccordStr(String targetAccordStr)
    {

	this.targetAccordStr = targetAccordStr;
    }

    public String getAllowLeaderTrace()
    {

	return allowLeaderTrace;
    }

    public void setAllowLeaderTrace(String allowLeaderTrace)
    {

	this.allowLeaderTrace = allowLeaderTrace;
    }

    public String getDescriptionItem()
    {

	return descriptionItem;
    }

    public void setDescriptionItem(String descriptionItem)
    {

	this.descriptionItem = descriptionItem;
    }

    public String getPrincipleItem()
    {

	return this.principleItem;
    }

    public void setPrincipleItem(String principleItem)
    {

	this.principleItem = principleItem;
    }

    public ArrayList getTargetItemList2()
    {

	return targetItemList2;
    }

    public void setTargetItemList2(ArrayList targetItemList2)
    {

	this.targetItemList2 = targetItemList2;
    }

	public String getFeedBackTemplate() {
		return this.feedBackTemplate;
	}

	public void setFeedBackTemplate(String feedBackTemplate) {
		this.feedBackTemplate = feedBackTemplate;
	}

	public String getTargetDefineItem()
	{
		return targetDefineItem;
	}

	public void setTargetDefineItem(String targetDefineItem)
	{
		this.targetDefineItem = targetDefineItem;
	}

	public ArrayList getTargetDefineItemList()
	{
		return targetDefineItemList;
	}

	public void setTargetDefineItemList(ArrayList targetDefineItemList)
	{
		this.targetDefineItemList = targetDefineItemList;
	}

	public ArrayList getTargetTraceItemList()
	{
		return targetTraceItemList;
	}

	public void setTargetTraceItemList(ArrayList targetTraceItemList)
	{
		this.targetTraceItemList = targetTraceItemList;
	}

	public String getTargetCalcItem() {
		return targetCalcItem;
	}

	public void setTargetCalcItem(String targetCalcItem) {
		this.targetCalcItem = targetCalcItem;
	}

	public ArrayList getTargetCalcItemList() {
		return targetCalcItemList;
	}

	public void setTargetCalcItemList(ArrayList targetCalcItemList) {
		this.targetCalcItemList = targetCalcItemList;
	}

	public String getTarItem() {
		return tarItem;
	}

	public void setTarItem(String tarItem) {
		this.tarItem = tarItem;
	}

	public String getCalItemStr() {
		return calItemStr;
	}

	public void setCalItemStr(String calItemStr) {
		this.calItemStr = calItemStr;
	}

	public String getSub_page() {
		return sub_page;
	}

	public void setSub_page(String sub_page) {
		this.sub_page = sub_page;
	}

	public String getRightCtrlByPerObjType() {
		return rightCtrlByPerObjType;
	}

	public void setRightCtrlByPerObjType(String rightCtrlByPerObjType) {
		this.rightCtrlByPerObjType = rightCtrlByPerObjType;
	}
	   
    public ArrayList getEvaluateList() {
		return evaluateList;
	}

	public void setEvaluateList(ArrayList evaluateList) {
		this.evaluateList = evaluateList;
	}

	public ArrayList getObjectiveList() {
		return objectiveList;
	}

	public void setObjectiveList(ArrayList objectiveList) {
		this.objectiveList = objectiveList;
	}

	public String getE_str() {
		return e_str;
	}

	public void setE_str(String e_str) {
		this.e_str = e_str;
	}

	public String getO_str() {
		return o_str;
	}

	public void setO_str(String o_str) {
		this.o_str = o_str;
	}
	
	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

	public String getReturnvalue1()
	{
		return returnvalue1;
	}

	public void setReturnvalue1(String returnvalue1)
	{
		this.returnvalue1 = returnvalue1;
	}

	public String getRedio()
    {
		return redio;
    }

    public void setRedio(String redio)
    {
    	this.redio = redio;
    }

	public String getControlByKHMoudle() {
		return controlByKHMoudle;
	}

	public void setControlByKHMoudle(String controlByKHMoudle) {
		this.controlByKHMoudle = controlByKHMoudle;
	}

	public String getBlind_360() {
		return blind_360;
	}

	public void setBlind_360(String blind_360) {
		this.blind_360 = blind_360;
	}

	public String getBlind_goal() {
		return blind_goal;
	}

	public void setBlind_goal(String blind_goal) {
		this.blind_goal = blind_goal;
	}

	public String getDepartDutySet() {
		return departDutySet;
	}

	public void setDepartDutySet(String departDutySet) {
		this.departDutySet = departDutySet;
	}

	public ArrayList getDepartDutySetList() {
		return departDutySetList;
	}

	public void setDepartDutySetList(ArrayList departDutySetList) {
		this.departDutySetList = departDutySetList;
	}

	public String getProjectField() {
		return projectField;
	}

	public void setProjectField(String projectField) {
		this.projectField = projectField;
	}

	public ArrayList getProjectFieldList() {
		return projectFieldList;
	}

	public void setProjectFieldList(ArrayList projectFieldList) {
		this.projectFieldList = projectFieldList;
	}

	public String getValidDateField() {
		return validDateField;
	}

	public void setValidDateField(String validDateField) {
		this.validDateField = validDateField;
	}

	public ArrayList getValidDateFieldList() {
		return validDateFieldList;
	}

	public void setValidDateFieldList(ArrayList validDateFieldList) {
		this.validDateFieldList = validDateFieldList;
	}

	public ArrayList getAllDataList() {
		return allDataList;
	}

	public void setAllDataList(ArrayList allDataList) {
		this.allDataList = allDataList;
	}

	public String getDepartTextValue() {
		return departTextValue;
	}

	public void setDepartTextValue(String departTextValue) {
		this.departTextValue = departTextValue;
	}

	public String getIstargetTasktracking() {
		return istargetTasktracking;
	}

	public void setIstargetTasktracking(String istargetTasktracking) {
		this.istargetTasktracking = istargetTasktracking;
	}

	public String getTargetTasktracking() {
		return targetTasktracking;
	}

	public void setTargetTasktracking(String targetTasktracking) {
		this.targetTasktracking = targetTasktracking;
	}

	public String getIstargetTaskofadjusting() {
		return istargetTaskofadjusting;
	}

	public void setIstargetTaskofadjusting(String istargetTaskofadjusting) {
		this.istargetTaskofadjusting = istargetTaskofadjusting;
	}

	public String getTargetTaskofadjusting() {
		return targetTaskofadjusting;
	}

	public void setTargetTaskofadjusting(String targetTaskofadjusting) {
		this.targetTaskofadjusting = targetTaskofadjusting;
	}

	public String getBusitype() {
		return busitype;
	}

	public void setBusitype(String busitype) {
		this.busitype = busitype;
	}

	public String getNameLinkCard() {
		return nameLinkCard;
	}

	public void setNameLinkCard(String nameLinkCard) {
		this.nameLinkCard = nameLinkCard;
	}

	public ArrayList getRnameList() {
		return rnameList;
	}

	public void setRnameList(ArrayList rnameList) {
		this.rnameList = rnameList;
	}

}
