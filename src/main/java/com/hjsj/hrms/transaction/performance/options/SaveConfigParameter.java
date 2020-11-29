package com.hjsj.hrms.transaction.performance.options;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * <p>Title:SaveConfigParameter.java</p>
 * <p>Description:保存配置参数</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-10-29 11:28:36</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SaveConfigParameter extends IBusiness
{

	public void execute() throws GeneralException
	{
		String itemStr = (String) this.getFormHM().get("targetTraceItem");
		String redio = (String) this.getFormHM().get("redio");
		String appealTemplate = (String) this.getFormHM().get("appealTemplate");
		String interviewTemplate = (String) this.getFormHM().get("interviewTemplate");
		String isTargetCardTemp = (String) this.getFormHM().get("isTargetCardTemp");
		String isTargetAppraisesTemp = (String) this.getFormHM().get("isTargetAppraisesTemp");
		String targetCardTemp = (String) this.getFormHM().get("targetCardTemp");
		String targetAppraisesTemp = (String) this.getFormHM().get("targetAppraisesTemp");
		String isTogetherCommi = (String) this.getFormHM().get("togetherCommit");
		String controlByKHMoudle = (String) this.getFormHM().get("controlByKHMoudle");
		String targetCollectItem = (String) this.getFormHM().get("targetCollectItem");
		String targetCalcItem = (String)this.getFormHM().get("targetCalcItem");
		String targetDefineItem = (String) this.getFormHM().get("targetDefineItem");
		ArrayList targetAccordList = (ArrayList) this.getFormHM().get("targetAccordList");
		String targetPostSet = (String) this.getFormHM().get("targetPostSet");
		String targetItem = (String) this.getFormHM().get("targetItem");
		String targetAccordStr = (String) this.getFormHM().get("targetAccordStr");
		////////////目标卡部门职责参数 郭峰/////////////
		String departDutySet = (String)this.getFormHM().get("departDutySet");
		String projectField = (String)this.getFormHM().get("projectField");
		String validDateField = (String)this.getFormHM().get("validDateField");
		String departTextValue = (String)this.getFormHM().get("departTextValue");
		if(departDutySet==null || "".equals(departDutySet)){
			departTextValue = "";
		}
		////////////////////////
		//--------------------铁血绩效  赵旭光 2013-3-20-----------------------------
		String istargetTasktracking = (String) this.getFormHM().get("istargetTasktracking");
		String targetTasktracking = (String) this.getFormHM().get("targetTasktracking");
		String istargetTaskofadjusting = (String) this.getFormHM().get("istargetTaskofadjusting");
		String targetTaskofadjusting = (String) this.getFormHM().get("targetTaskofadjusting");
		//------------------------------------------------------------------------
		String NameLinkCard = (String) this.getFormHM().get("nameLinkCard");//考核对象基本信息表（人员）	zhaoxg 2014-4-23
		String allowLeaderTrace = (String) this.getFormHM().get("allowLeaderTrace");
		String descriptionItem = (String) this.getFormHM().get("descriptionItem");
		String principleItem = (String) this.getFormHM().get("principleItem");
		String feedBackTemplate = (String) this.getFormHM().get("feedBackTemplate");
		String rightCtrlByPerObjType = (String) this.getFormHM().get("rightCtrlByPerObjType");
		String e_str=(String)this.getFormHM().get("e_str");
		String o_str=(String)this.getFormHM().get("o_str");
		String blind_360 = "0";
		if(e_str.indexOf(",7")!=-1){
			blind_360 = (String)this.getFormHM().get("blind_360");
		}
		String blind_goal ="0";
		if(o_str.indexOf(",7")!=-1){
			blind_goal = (String)this.getFormHM().get("blind_goal");
		}
		// StringBuffer targetAccordStr = new StringBuffer();
		// for(int i=0;i<targetAccordList.size();i++)
		// {
		// LazyDynaBean abean = (LazyDynaBean)targetAccordList.get(i);
		// String itemid = (String)abean.get("itemid");
		// String destFldId = (String)abean.get("destFldId");
		// if(destFldId.trim().length()>0)
		// targetAccordStr.append(itemid.toUpperCase()+"="+destFldId.trim().toUpperCase()+",");
		// System.out.println("itemid:"+itemid+" destFldId:"+destFldId);
		// }
		// if(targetAccordStr.length()>0)
		// targetAccordStr.setLength(targetAccordStr.length()-1);

		ConstantXml xml = new ConstantXml(this.frameconn, "PER_PARAMETERS", "Per_Parameters");
		xml.setAttributeValue("/Per_Parameters/TargetCard", "email", "1".equals(isTargetCardTemp) ? "true" : "false");
		xml.setAttributeValue("/Per_Parameters/TargetCard", "template", "0".equals(isTargetCardTemp) ? "-1" : targetCardTemp);
		xml.setAttributeValue("/Per_Parameters/TargetAppraises", "email", "1".equals(isTargetAppraisesTemp) ? "true" : "false");
		xml.setAttributeValue("/Per_Parameters/TargetAppraises", "template", "0".equals(isTargetAppraisesTemp) ? "-1" : targetAppraisesTemp);
		xml.setAttributeValue("/Per_Parameters/Appeal", "template", appealTemplate);
		xml.setAttributeValue("/Per_Parameters/Interview", "template", interviewTemplate);
		xml.setAttributeValue("/Per_Parameters/FeedBack", "template", feedBackTemplate);

		xml.setAttributeValue("/Per_Parameters/Plan", "MarkingMode", redio);
		xml.setAttributeValue("/Per_Parameters/Plan", "TogetherCommit", "1".equals(isTogetherCommi) ? "True" : "False");
		xml.setAttributeValue("/Per_Parameters/Plan", "ControlByKHMoudle", "1".equals(controlByKHMoudle) ? "True" : "False");
		xml.setAttributeValue("/Per_Parameters/Plan", "NameLinkCard", NameLinkCard);
		xml.setTextValue("/Per_Parameters/TargetTraceItem", itemStr);
		xml.setTextValue("/Per_Parameters/TargetCollectItem", targetCollectItem);
		xml.setTextValue("/Per_Parameters/TargetCalcItem", targetCalcItem);
		xml.setTextValue("/Per_Parameters/TargetDefineItem", targetDefineItem.length() == 0 ? "," : targetDefineItem);// 客户如果没有选择目标卡指标保存个逗号进去
		xml.setAttributeValue("/Per_Parameters/TargetPostDuty", "SubSet", targetPostSet);
		xml.setAttributeValue("/Per_Parameters/TargetPostDuty", "TargetItem", targetItem);
		xml.setTextValue("/Per_Parameters/TargetPostDuty", targetAccordStr.toUpperCase());
		///目标卡部门职责参数  郭峰  ////
		xml.setAttributeValue("/Per_Parameters/TargetDeptDuty", "SubSet", departDutySet);
		xml.setAttributeValue("/Per_Parameters/TargetDeptDuty", "TargetItem", projectField);
		xml.setAttributeValue("/Per_Parameters/TargetDeptDuty", "DataItem", validDateField);
		xml.setTextValue("/Per_Parameters/TargetDeptDuty", departTextValue.toUpperCase());
		////////////////////////////
		xml.setTextValue("/Per_Parameters/AllowLeaderTrace", "1".equals(allowLeaderTrace) ? "True" : "False");
		xml.setTextValue("/Per_Parameters/DescriptionItem", descriptionItem.toUpperCase());
		xml.setTextValue("/Per_Parameters/PrincipleItem", principleItem.toUpperCase());
		xml.setTextValue("/Per_Parameters/RightCtrlByPerObjType", "1".equals(rightCtrlByPerObjType) ? "True" : "False");
		xml.setAttributeValue("/Per_Parameters/ResultVisible", "evaluate", e_str);
		xml.setAttributeValue("/Per_Parameters/ResultVisible", "objective", o_str);
		xml.setAttributeValue("/Per_Parameters/ResultVisible", "blind_360", blind_360);
		xml.setAttributeValue("/Per_Parameters/ResultVisible", "blind_goal", blind_goal);
		//--------------------铁血网绩效  赵旭光 2013-3-21----------------------------------
		xml.setAttributeValue("/Per_Parameters/TraceItem", "email", "1".equals(istargetTasktracking) ? "true" : "false");
		xml.setAttributeValue("/Per_Parameters/TraceItem", "template", "0".equals(istargetTasktracking) ? "-1" : targetTasktracking);
		xml.setAttributeValue("/Per_Parameters/AdjustTask", "email", "1".equals(istargetTaskofadjusting) ? "true" : "false");
		xml.setAttributeValue("/Per_Parameters/AdjustTask", "template", "0".equals(istargetTaskofadjusting) ? "-1" : targetTaskofadjusting);
		//-------------------------------------------------------------------------------
		xml.saveStrValue();
		try
		{//加载动态参数
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			AnalysePlanParameterBo bo = new AnalysePlanParameterBo(this.frameconn);
			frowset = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
			if (frowset.next())
			{
				String values = Sql_switcher.readMemo(frowset, "str_value");
				if (!"".equals(values.trim()))
				{
					Element root;
					Document doc = PubFunc.generateDom(values);
					root = doc.getRootElement();
					Hashtable tempHash = new Hashtable();
					tempHash = bo.getElements(root);
					AnalysePlanParameterBo.setReturnHt(tempHash);
				}

			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
