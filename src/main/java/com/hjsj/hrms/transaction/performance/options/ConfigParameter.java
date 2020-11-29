package com.hjsj.hrms.transaction.performance.options;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.businessobject.performance.options.ConfigParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:ConfigParameter.java</p>
 * <p>Description>:绩效管理 配置参数</p>
 * <p>Company:HJSJ</p>
 * <p>@version: 1.0</p>
 * <p>@author: JinChunhai
 */

public class ConfigParameter extends IBusiness
{

    public void execute() throws GeneralException
    {
    	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String returnflag=(String)hm.get("returnflag");
		this.getFormHM().put("returnflag",returnflag);
		hm.remove("returnvalue");
		String busitype = (String) hm.get("busitype");	
		this.getFormHM().put("busitype",busitype);
	    	
		ConfigParamBo bo = new ConfigParamBo(this.getFrameconn());
	//	ArrayList list = bo.getItemList();
	//	ArrayList targetCollectItemList = bo.getTargetCollectItemList();
	//	this.getFormHM().put("itemList", list);
	//	this.getFormHM().put("targetCollectItemList", targetCollectItemList);
		this.getFormHM().put("emailTempList", bo.getEmailTempList());
		this.getFormHM().put("busiTempList", bo.getBusiTempList());
	
		String appealTemplate = "-1";// 申诉模板
		String interviewTemplate = "-1";// 面谈模板
		String isTargetCardTemp = "0";// 是否需要目标卡制定模板
		String isTargetAppraisesTemp = "0";// 是否需要目标卡评估模板
		String targetCardTemp = "-1";// 目标卡制定模板
		String targetAppraisesTemp = "-1";// 目标卡评估模板
		String isTogetherCommi = "0";// 是否统一提交
		String controlByKHMoudle = "0"; // 考核计划按模板权限控制, True,False(默认)
		String markingMode = "1";
		String targetPostSet = "";// 岗位职责子集
		ArrayList targetPostSetList =  bo.getPostSubset();
		String targetItem = "";// 项 目 指 标
		ArrayList targetItemList = new ArrayList();
		ArrayList targetAccordList = new ArrayList();
		
		///////////目标卡部门职责参数  郭峰增加/////////////
		String departDutySet = "";// 部门职责子集
		ArrayList departDutySetList =  bo.getDepartSetList();
		String projectField = "";// 项目指标
		ArrayList projectFieldList = new ArrayList();//项目指标列表
		String validDateField = "";//有效时间指标
		ArrayList validDateFieldList = new ArrayList();//有效时间指标列表
		ArrayList allDataList = new ArrayList();//要显示的数据列表
		//////////////////////////////////////
		
		String allowLeaderTrace = "False"; // 允许领导制定及批准跟踪指标, True(默认) False
	
		String descriptionItem = "";// 指标解释指标
	
		String principleItem = "";// 评分说明指标
	
		String feedBackTemplate = "";//考核结果反馈表通知邮件模板
		
		ArrayList targetItemList2 = bo.getItemList2();// 目标卡指标
		
		
		String targetTraceItems = "";//目标卡跟踪和显示指标
		String targetCollectItems ="";//目标卡采集指标
		String targetDefineItem = "";// 目标卡指标	
		String targetCalcItems = ""; //目标卡计算指标
		String tarItem = "";//目标卡计算指标串
		String rightCtrlByPerObjType = "true";//按计划考核对象类型权限控制 默认为true
		String e_str="";//360考核结果显示项
		String o_str="";//目标考核结果显示项
		String blind_360="0";    //360的评价盲点值
		String blind_goal="0";   //目标考核的评价盲点值
		//---------------------铁血网绩效----zhaoxg 2013-3-20 ----------------------------
		String istargetTasktracking = "0"; //目标卡任务跟踪需邮件通知
		String targetTasktracking = "-1";
		String istargetTaskofadjusting = "0";  //目标卡任务调整需邮件通知
		String targetTaskofadjusting = "-1";
		//------------------------------------------------------------------------------
		String NameLinkCard="";//考核对象基本信息表（人员）	zhaoxg 2014-4-23
		try
		{
		    ContentDAO dao = new ContentDAO(this.frameconn);
		    this.frowset = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
		    if ( this.frowset.next())
		    {
			String str_value = this.frowset.getString("str_value");
			if (str_value == null || (str_value != null && "".equals(str_value)))
			{
	
			} else
			{
			    Document doc = PubFunc.generateDom(str_value);
			    String xpath = "//Per_Parameters";
			    XPath xpath_ = XPath.newInstance(xpath);
			    Element ele = (Element) xpath_.selectSingleNode(doc);
			    Element child;
			    if (ele != null)
			    {
					child = ele.getChild("Plan");
					if (child != null)
					{
					    markingMode = child.getAttributeValue("MarkingMode");
					    isTogetherCommi = child.getAttributeValue("TogetherCommit");
					    controlByKHMoudle = child.getAttributeValue("ControlByKHMoudle");
					    NameLinkCard = child.getAttributeValue("NameLinkCard");
					}
		
					child = ele.getChild("TargetCard");
					if (child != null)
					{
					    isTargetCardTemp = child.getAttributeValue("email");
					    targetCardTemp = child.getAttributeValue("template");
					}
					//-------------铁血网绩效 --- zhaoxg 2013-3-20---------------------------
					child = ele.getChild("TraceItem");
					if (child != null)
					{
						istargetTasktracking = child.getAttributeValue("email");
						targetTasktracking = child.getAttributeValue("template");
					}
					child = ele.getChild("AdjustTask");
					if (child != null)
					{
						istargetTaskofadjusting = child.getAttributeValue("email");
						targetTaskofadjusting = child.getAttributeValue("template");
					}
					//---------------------------------------------------------------------
					child = ele.getChild("TargetAppraises");
					if (child != null)
					{
					    isTargetAppraisesTemp = child.getAttributeValue("email");
					    targetAppraisesTemp = child.getAttributeValue("template");
					}
					child = ele.getChild("Appeal");
					if (child != null)
					{
					    appealTemplate = child.getAttributeValue("template");
					}
					child = ele.getChild("Interview");
					if (child != null)
					{
					    interviewTemplate = child.getAttributeValue("template");
					}
					child = ele.getChild("TargetPostDuty");
					if (child != null)
					{
					    targetPostSet = child.getAttributeValue("SubSet");
					    targetItem = child.getAttributeValue("TargetItem");
					    String accordString = child.getTextTrim();
					    targetItemList = bo.getPostSubsetItems(targetPostSet,"A",2);//取得子集的非代码类字符型
					    targetAccordList = bo.getTargetAccordList(accordString,targetPostSet);
					}else
					{
						 targetAccordList = bo.getTargetAccordList("","");
					}
					/////目标卡部门职责参数  郭峰添加////////
					child = ele.getChild("TargetDeptDuty");
					if (child != null)
					{
						departDutySet = child.getAttributeValue("SubSet");
						projectField = child.getAttributeValue("TargetItem");
						validDateField = child.getAttributeValue("DataItem");
						ArrayList[] arrayList = bo.getSetFieldList(departDutySet);
						projectFieldList = arrayList[0];
						validDateFieldList = arrayList[3];
					    String accordString = child.getTextTrim();
					    allDataList = bo.getFieldList(accordString,departDutySet);
					}else
					{
						allDataList = bo.getFieldList("","");
					}
					///////////////////////////////////
					child = ele.getChild("AllowLeaderTrace");
					if (child != null)			
					    allowLeaderTrace = child.getTextTrim();
		
					child = ele.getChild("DescriptionItem");
					if (child != null)			
					    descriptionItem = child.getTextTrim();
					
					child = ele.getChild("PrincipleItem");
					if (child != null)			
					    principleItem = child.getTextTrim();
				   
				    
					child = ele.getChild("FeedBack");
					if (child != null)			
						feedBackTemplate = child.getAttributeValue("template");
					
					child = ele.getChild("TargetDefineItem");
					if (child != null)			
						targetDefineItem = child.getTextTrim();			
					
					child = ele.getChild("TargetTraceItem");
					if (child != null)			
						targetTraceItems = child.getTextTrim();
					
					child = ele.getChild("TargetCollectItem");
					if (child != null)			
						targetCollectItems = child.getTextTrim();
					
					child = ele.getChild("TargetCalcItem");
					if (child != null)			
						targetCalcItems = child.getTextTrim();
					
					child = ele.getChild("TarItem");
					if (child != null)			
						tarItem = child.getTextTrim();
					child=ele.getChild("ResultVisible");
					if(child!=null)
					{
						e_str=child.getAttributeValue("evaluate");
						o_str=child.getAttributeValue("objective");
						blind_360=child.getAttributeValue("blind_360");
						blind_goal=child.getAttributeValue("blind_goal");
					}
			    }
				
			  }
		    }
		    else		
				 targetAccordList = bo.getTargetAccordList("","");
			
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		
		ExamPlanBo planbo = new ExamPlanBo(this.frameconn);
		
		ArrayList targetDefineItemList = planbo.getTargetDefineItemList(targetDefineItem,"0");
		ArrayList targetCollectItemList = new ArrayList();
		ArrayList targetTraceItemList = new ArrayList();
		ArrayList targetCalcItemList = new ArrayList();
		if(targetDefineItem.trim().length()==0)//初次进入 目标卡指标默认都选中
		{
			ArrayList tempList = new ArrayList();
			for(int i=0;i<targetDefineItemList.size();i++)
			{
				LazyDynaBean abean =  (LazyDynaBean)targetDefineItemList.get(i);
				abean.set("selected","1");
				tempList.add(abean);
				targetDefineItem+=","+(String)abean.get("itemid");
			}
			targetDefineItemList=tempList;
			targetDefineItem=targetDefineItem.substring(1);
			targetCollectItemList = planbo.getTargetItemList(targetDefineItem,targetCollectItems,"0");
			targetTraceItemList = planbo.getTargetItemList(targetDefineItem,targetTraceItems,"0");
			targetCalcItemList = planbo.getComputeItemList(tarItem,targetDefineItem,targetCalcItems);
		}else if(",".equals(targetDefineItem))//客户将目标卡指标一个也不选点击了保存按钮
		{
	//		targetDefineItem=",";
			
			targetCalcItemList = planbo.getComputeItemList(tarItem,targetDefineItem,targetCalcItems);
		}else
		{
			targetCalcItemList = planbo.getComputeItemList(tarItem,targetDefineItem,targetCalcItems);
			targetCollectItemList = planbo.getTargetItemList(targetDefineItem,targetCollectItems,"0");
			targetTraceItemList = planbo.getTargetItemList(targetDefineItem,targetTraceItems,"0");
		}
		
		String targetComputeItem = "";
		for(int i=0;i<targetDefineItemList.size();i++)
		{
			LazyDynaBean abean =  (LazyDynaBean)targetDefineItemList.get(i);						
			targetComputeItem+=","+(String)abean.get("itemid");
		}
		if(targetComputeItem!=null && targetComputeItem.trim().length()>0)
			targetComputeItem = targetComputeItem.substring(1);
		e_str=e_str==null|| "".equals(e_str.trim())?"":(","+e_str+",");
		o_str=o_str==null|| "".equals(o_str.trim())?"":(","+o_str+",");
		ArrayList evaluateList=ConfigParamBo.getConfigDrawList(1,e_str);
		ArrayList objectiveList = ConfigParamBo.getConfigDrawList(2,o_str);
		this.getFormHM().put("e_str",e_str);
		this.getFormHM().put("o_str", o_str);
		this.getFormHM().put("blind_360", blind_360);
		this.getFormHM().put("blind_goal", blind_goal);
		this.getFormHM().put("evaluateList", evaluateList);
		this.getFormHM().put("objectiveList", objectiveList);
		this.getFormHM().put("calItemStr",planbo.getComputeItemStr(targetComputeItem));
		this.getFormHM().put("targetCalcItem", targetCalcItems);
		this.getFormHM().put("allowLeaderTrace", allowLeaderTrace);
		this.getFormHM().put("targetCollectItem",targetCollectItems);
		this.getFormHM().put("targetTraceItem", targetTraceItems);
		this.getFormHM().put("targetDefineItem",targetDefineItem);
		
		this.getFormHM().put("targetCalcItemList",targetCalcItemList);
		this.getFormHM().put("targetCollectItemList",targetCollectItemList);    	
		this.getFormHM().put("targetTraceItemList",targetTraceItemList);
		this.getFormHM().put("targetDefineItemList", targetDefineItemList);
		
		
		controlByKHMoudle = (controlByKHMoudle != null && "true".equalsIgnoreCase(controlByKHMoudle)) ? "1" : "0";
		isTogetherCommi = (isTogetherCommi != null && "true".equalsIgnoreCase(isTogetherCommi)) ? "1" : "0";
		isTargetAppraisesTemp = (isTargetAppraisesTemp != null && "true".equalsIgnoreCase(isTargetAppraisesTemp)) ? "1" : "0";
		rightCtrlByPerObjType = (rightCtrlByPerObjType != null && "true".equalsIgnoreCase(rightCtrlByPerObjType)) ? "1" : "0";
		
		this.getFormHM().put("rightCtrlByPerObjType", rightCtrlByPerObjType);
		this.getFormHM().put("appealTemplate", appealTemplate);
		this.getFormHM().put("interviewTemplate", interviewTemplate);
		this.getFormHM().put("isTargetCardTemp", isTargetCardTemp);
		this.getFormHM().put("isTargetAppraisesTemp", isTargetAppraisesTemp);
		this.getFormHM().put("targetCardTemp", targetCardTemp);
		this.getFormHM().put("targetAppraisesTemp", targetAppraisesTemp);
		this.getFormHM().put("togetherCommit", isTogetherCommi);
		this.getFormHM().put("controlByKHMoudle", controlByKHMoudle);
		this.getFormHM().put("redio", markingMode);
		
		this.getFormHM().put("targetPostSet", targetPostSet);
		this.getFormHM().put("targetPostSetList",targetPostSetList);
		this.getFormHM().put("targetItem", targetItem);
		this.getFormHM().put("targetItemList", targetItemList);
		this.getFormHM().put("targetAccordList", targetAccordList);
			
		this.getFormHM().put("descriptionItem", descriptionItem);
		this.getFormHM().put("principleItem", principleItem);
		this.getFormHM().put("targetItemList2", targetItemList2);
		this.getFormHM().put("feedBackTemplate", feedBackTemplate);
		
		//目标卡部门职责参数  郭峰增加
		this.getFormHM().put("departDutySet", departDutySet);
		this.getFormHM().put("departDutySetList", departDutySetList);
		this.getFormHM().put("projectField", projectField);
		this.getFormHM().put("projectFieldList", projectFieldList);
		this.getFormHM().put("validDateField", validDateField);
		this.getFormHM().put("validDateFieldList", validDateFieldList);
		this.getFormHM().put("allDataList", allDataList);
		//-------------铁血网绩效  zhaoxg 2013-3-20------------
		this.getFormHM().put("istargetTasktracking", istargetTasktracking);
		this.getFormHM().put("targetTasktracking", targetTasktracking);
		this.getFormHM().put("istargetTaskofadjusting", istargetTaskofadjusting);
		this.getFormHM().put("targetTaskofadjusting", targetTaskofadjusting);
		//---------------------------------------------------
		this.getFormHM().put("nameLinkCard", NameLinkCard==null?"":NameLinkCard);
		this.getFormHM().put("rnameList", bo.getRnameList());
    }
}
