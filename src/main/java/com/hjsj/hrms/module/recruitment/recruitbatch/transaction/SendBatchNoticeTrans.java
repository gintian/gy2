package com.hjsj.hrms.module.recruitment.recruitbatch.transaction;

import com.hjsj.hrms.module.recruitment.recruitbatch.businessobject.RecruitBatchBo;
import com.hjsj.hrms.module.recruitment.util.EmailInfoBo;
import com.hjsj.hrms.module.recruitment.util.ZpPendingtaskBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public class SendBatchNoticeTrans  extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			String functionType = (String) this.getFormHM().get("functionType");//模板编号
			/** 得到前台传入邮件模板Id，切换模板发送邮件时使用 ***/
			String templateId = (String) this.getFormHM().get("templateId");
			ArrayList<String> batchIds = (ArrayList) this.getFormHM().get("batchIds");
			String sub_module = "92";//模板编号
			String nModule = "7";// 模块编号
			EmailInfoBo bo = new EmailInfoBo(frameconn, userView);
			ZpPendingtaskBo taskBo = new ZpPendingtaskBo(this.frameconn, this.userView);
			RecruitBatchBo batchBo = new RecruitBatchBo(this.frameconn, this.userView);
			//获取邮件内容
			LazyDynaBean bean = bo.getTemplateInfo(nModule, sub_module, templateId, "");
			/** 得到邮件模板Id ***/
			templateId = ((String) bean.get("id")) == null ? "0" : ((String) bean.get("id"));
			if("search".equals(functionType)) {
				//获取邮件模板列表
				String templateList = bo.getTemplateList(nModule, sub_module, "");
				/** 得模板项目列表 */
				ArrayList list = bo.getTemplateFieldInfo(Integer.parseInt(templateId), 2);
				/** 取邮件模板内容 */
				String content = (String) bean.get("content") == null ? "" : ((String) bean.get("content"));
				/** 替换系统字段 **/
				if(StringUtils.isNotEmpty(content)) {         
					content = bo.getSysContent(content);
				}
				content = content+"\n ";
				ArrayList defPerson = batchBo.getDefPerson(batchIds);
				this.getFormHM().put("content", content);
				this.getFormHM().put("templateList", templateList);
				this.getFormHM().put("selfuser", defPerson.get(0));
				this.getFormHM().put("bususer", defPerson.get(1));
			} else if("send".equals(functionType)) {
				ArrayList<String> persons = (ArrayList)this.getFormHM().get("persons");
				ArrayList<String> bususer = (ArrayList)this.getFormHM().get("bususer");
				Boolean sendEmail = (Boolean) this.getFormHM().get("sendEmail");//是否发送邮件
				String content = (String) this.getFormHM().get("content");//通知内容
				ArrayList<String> titles = taskBo.getTitles(batchIds);
				taskBo.sendPendingTask(persons, titles , true);
				taskBo.sendPendingTask(bususer, titles , false);
				if(sendEmail) {
					taskBo.sendEmailTask(persons, titles, templateId, sub_module, nModule, content, true);
					taskBo.sendEmailTask(bususer, titles, templateId, sub_module, nModule, content, false);
				}
					
			}

		} catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}
