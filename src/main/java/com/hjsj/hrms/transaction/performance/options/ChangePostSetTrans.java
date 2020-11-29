package com.hjsj.hrms.transaction.performance.options;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.businessobject.performance.options.ConfigParamBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:ChangePostSetTrans.java</p>
 * <p>Description:配置参数切换岗位职责子集</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-11-18 11:11:11</p>
 * @author JinChunhai
 * @version 1.0 
 */

public class ChangePostSetTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	
		String targetTraceItem =  (String)this.getFormHM().get("targetTraceItem");
		String targetCollectItem = (String)this.getFormHM().get("targetCollectItem");
		String targetDefineItem = (String)this.getFormHM().get("targetDefineItem");
		
		ExamPlanBo bo = new ExamPlanBo(this.frameconn);
		ArrayList targetCollectItemList = bo.getTargetItemList(targetDefineItem,targetCollectItem,"0");
		ArrayList targetTraceItemList = bo.getTargetItemList(targetDefineItem,targetTraceItem,"0");
		ArrayList targetDefineItemList = bo.getTargetDefineItemList(targetDefineItem,"0");
		
		this.getFormHM().put("targetCollectItemList",targetCollectItemList);    	
		this.getFormHM().put("targetTraceItemList",targetTraceItemList);
		this.getFormHM().put("targetDefineItemList", targetDefineItemList);
		
		String targetPostSet = (String)this.getFormHM().get("targetPostSet");
		ConstantXml xml = new ConstantXml(this.frameconn, "PER_PARAMETERS", "Per_Parameters");
		String accordString = xml.getTextValue("/Per_Parameters/TargetPostDuty");
		String postSetXml = xml.getNodeAttributeValue("/Per_Parameters/TargetPostDuty","SubSet");
		String targetItem = "";
		if(targetPostSet.equalsIgnoreCase(postSetXml))
		    targetItem = xml.getNodeAttributeValue("/Per_Parameters/TargetPostDuty","TargetItem");
		
		ConfigParamBo configParamBo = new ConfigParamBo(this.getFrameconn());
		ArrayList targetAccordList = configParamBo.getTargetAccordList(accordString,targetPostSet);
		ArrayList targetItemList = configParamBo.getPostSubsetItems(targetPostSet,"A",2);//取得子集的非代码类字符型
		this.getFormHM().put("targetAccordList", targetAccordList);
		this.getFormHM().put("targetItem", targetItem);
		this.getFormHM().put("targetItemList", targetItemList);
		
		////////////////目标卡部门职责参数 郭峰///////////////////////////
		String departDutySet = (String)this.getFormHM().get("departDutySet");
		ArrayList[] arrayList = configParamBo.getSetFieldList(departDutySet);
		ArrayList projectFieldList = arrayList[0];
		ArrayList validDateFieldList = arrayList[3];
		xml = new ConstantXml(this.frameconn, "PER_PARAMETERS", "Per_Parameters");
		String accordString2 = "";//对应关系
		String projectXml = "";//项目子集
		String validtimeXml = "";//有效时间子集
		String departSetXml = xml.getNodeAttributeValue("/Per_Parameters/TargetDeptDuty","SubSet");
		if(departSetXml.equalsIgnoreCase(departDutySet)){//如果该子集正好是已经保存下来的
			accordString2 = xml.getTextValue("/Per_Parameters/TargetDeptDuty");
			projectXml = xml.getNodeAttributeValue("/Per_Parameters/TargetDeptDuty","TargetItem");
			validtimeXml = xml.getNodeAttributeValue("/Per_Parameters/TargetDeptDuty","DataItem");
		}
		ArrayList allDataList = configParamBo.getFieldList(accordString2,departDutySet);
		this.getFormHM().put("projectField", projectXml);
		this.getFormHM().put("validDateField", validtimeXml);
		this.getFormHM().put("projectFieldList", projectFieldList);
		this.getFormHM().put("validDateFieldList", validDateFieldList);
		this.getFormHM().put("allDataList", allDataList);
		
	
    }
}
