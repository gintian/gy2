/**
 * <p>Title:TemplatePageTrans.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-7-29 下午01:36:00</p>
 * <p>@version: 7.x</p>
 */
package com.hjsj.hrms.module.template.historydata.formcorrelation.templatecard.transaction;

import com.hjsj.hrms.module.template.historydata.formcorrelation.templatecard.businessobject.TemplateCardBo;
import com.hjsj.hrms.module.template.historydata.formcorrelation.templatecard.businessobject.TemplatePageBo;
import com.hjsj.hrms.module.template.utils.TemplateFuncBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 页签数据交易
* @Title: TemplatePageTrans
* @Description:
* @author: hej
* @date 2019年11月19日 下午4:41:59
* @version
 */
public class TemplatePageTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		try {
			HashMap formMap= this.getFormHM();
			TemplateFrontProperty frontProperty =new TemplateFrontProperty(formMap);
            String return_flag = frontProperty.getReturnFlag();
            String tabid = frontProperty.getTabId();
            String task_id = frontProperty.getTaskId();
			String pagid = TemplateFuncBo.getValueFromMap(formMap,"page_id");
			String cur_task_id = TemplateFuncBo.getDecValueFromMap(formMap,"cur_task_id");
			if ("".equals(cur_task_id)){
			    cur_task_id="0";
			}
			String cur_object_id = TemplateFuncBo.getDecValueFromMap(formMap,"object_id");
			String needLoadPageHtml = TemplateFuncBo.getValueFromMap(formMap,"needLoadPageHtml");
			String needLoadFieldValue = TemplateFuncBo.getValueFromMap(formMap,"needLoadFieldValue");
			String firstPageNo = TemplateFuncBo.getValueFromMap(formMap,"firstPageNo");
			String noShowPageNo = frontProperty.getOtherParam("noshow_pageno");//设定的不显示的页签
			String archive_id = frontProperty.getOtherParam("archive_id");
			String record_id = frontProperty.getOtherParam("record_id");
			String archive_year = frontProperty.getOtherParam("archive_year");
			TemplatePageBo pageBo= new TemplatePageBo(this.frameconn,this.userView,
					Integer.parseInt(tabid),Integer.parseInt(pagid),archive_id);
	
		   String selfapply="0";
		   if (frontProperty.isSelfApply()){
		       selfapply="1";
		   }
		   pageBo.setTask_id(task_id);
		   pageBo.setCurTaskId(cur_task_id);
		   pageBo.setSelfApply(selfapply);
		   pageBo.setObjectId(cur_object_id);
		   pageBo.setApproveFlag(frontProperty.getApproveFlag());
		   pageBo.getParamBo().setReturnFlag(return_flag);
		   pageBo.setFirstPageNo(Integer.parseInt(firstPageNo));
		   pageBo.setNoShowPageNo(noShowPageNo);
		   //生成html模板页html 每张模板页只生成一次。
		   if ("true".equals(needLoadPageHtml)){
			   String htmlView = pageBo.getPageHtml();
			   this.getFormHM().put("htmlview", htmlView);
		   }
		   //切换人后，第一次请求模板页数据时 加载当前人的所有页数据
		   if ("true".equals(needLoadFieldValue)){
			   TemplateCardBo cardBo= new TemplateCardBo(this.frameconn,this.userView,
					   pageBo.getParamBo());
			   cardBo.setTask_id(task_id);
			   cardBo.setCurTaskId(cur_task_id);
			   cardBo.setSelfApply(selfapply);
			   cardBo.setObjectId(cur_object_id);
			   cardBo.setApproveFlag(frontProperty.getApproveFlag());
			   cardBo.setRecord_id(record_id);
			   cardBo.setArchive_id(archive_id);
			   cardBo.setArchive_year(archive_year);
			   cardBo.setChangeInfoLMap(new HashMap());
			   ArrayList fieldValueList=new ArrayList();
			   fieldValueList=cardBo.getFieldValueList();
			   this.getFormHM().put("fieldValueList", fieldValueList);
		   }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}


}
