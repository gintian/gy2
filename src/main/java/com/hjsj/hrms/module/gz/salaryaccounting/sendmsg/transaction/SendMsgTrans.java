package com.hjsj.hrms.module.gz.salaryaccounting.sendmsg.transaction;

import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.businessobject.hire.AutoSendEMailBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salaryaccounting.sendmsg.businessobject.SendMsgBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 项目名称：hcm7.x
 * 类名称：SendMsgTrans 
 * 类描述：发送通知
 * 创建人：sunming
 * 创建时间：2015-7-8
 * @version
 */
public class SendMsgTrans extends IBusiness{

	@Override
    public void execute() throws GeneralException {
		HashMap map =null;
		String templateId="";//模板id
		String e_m_type="";//发送类别 0邮件 1短信 2微信 3钉钉
		String listCount="0";
		boolean flag = false;
		try
		{
			String sendtype=(String)this.getFormHM().get("type");//=1 发送选择的人员,=2群发
			e_m_type=(String)this.getFormHM().get("e_m_type");
			String ids = (String)this.getFormHM().get("ids");//待发送的人员personid
			templateId=(String)this.getFormHM().get("templateId");
			String salaryid = (String)this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			String Send_ok=(String)this.getFormHM().get("Send_ok").toString();//页面所选发送状态 0：未发 1：已发 2：发送失败 3：全部
			String tableName = this.userView.getUserName()+"_salary_"+salaryid;
			String condSql=(String)this.userView.getHm().get("condSql");//页面搜索框中查询条件sql。InitSendMsgTrans中拼装
			String appdate=(String)this.getFormHM().get("appdate");//业务日期
			appdate=PubFunc.decrypt(SafeCode.decode(appdate));

			String isSend=(String)this.getFormHM().get("isSend");//1为发送信息 0为查询发送结果
			SendMsgBo bo = new SendMsgBo(this.getFrameconn(), userView);
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(), Integer.parseInt(salaryid), this.userView);
			String manager = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			if (manager.length() == 0 || this.userView.getUserName().equalsIgnoreCase(manager))
				tableName = this.userView.getUserName() + "_salary_" + salaryid;
			else
				tableName = manager + "_salary_" + salaryid;
			String privSql = gzbo.getWhlByUnits(tableName, true);

			String timeSql = bo.queryRecordByTime(appdate);
			if("1".equals(isSend)){//发送信息


				AutoSendEMailBo ase = new AutoSendEMailBo(this.getFrameconn());
				String mobile_field = ase.getMobileField();
				if ("1".equals(e_m_type) && (mobile_field == null || mobile_field.trim().length() == 0))
					throw GeneralExceptionHandler.Handle(new Exception("电话指标没有设置！"));
				ArrayList contentList = bo.getEmailContentList(sendtype, ids, templateId, this.userView, tableName, privSql, mobile_field, e_m_type, Send_ok, condSql, timeSql, salaryid);
				if (contentList.size() == 0)
					throw GeneralExceptionHandler.Handle(new Exception("没有需要发送的邮件！"));
				listCount = String.valueOf(contentList.size());
				ArrayList attachList = bo.getAttachFileName(templateId);
				String fromAddress = bo.getFromAddr();
				if ("1".equals(e_m_type)) {
					RecordVo sms_vo = ConstantParamter.getConstantVo("SS_SMS_OPTIONS");
					if (sms_vo == null)
						throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("sys.smsparam.nodifine")));//未配置短信服务
					map = bo.sendMessage(0, contentList, templateId, attachList, "", userView, 0, mobile_field);
				} else if ("0".equals(e_m_type)) {
					flag=true;
					map = bo.sendEmail(0, contentList, templateId, attachList, fromAddress, this.getUserView(), 0);
				} else if ("2".equals(e_m_type)) {
					String corpid = (String) ConstantParamter.getAttribute("wx", "corpid");
					if (corpid == null || "".equals(corpid)) {
						throw GeneralExceptionHandler.Handle(new Exception("微信参数没有设置！"));
					}
					if (corpid != null && corpid.length() > 0) {
						for (int i = 0; i < contentList.size(); i++) {
							LazyDynaBean bean = (LazyDynaBean) contentList.get(i);
							flag = WeiXinBo.sendMsgToPerson((String) bean.get("usernameZiZhu"), (String) bean.get("subject"), (String) bean.get("bodyText"), "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png", "");
						}
					}
				} else if ("3".equals(e_m_type)) {
					String corpid = (String) ConstantParamter.getAttribute("DINGTALK", "corpid");
					if (corpid == null || "".equals(corpid)) {
						throw GeneralExceptionHandler.Handle(new Exception("钉钉参数没有设置！"));
					}
					if (corpid != null && corpid.length() > 0) {
						for (int i = 0; i < contentList.size(); i++) {
							LazyDynaBean bean = (LazyDynaBean) contentList.get(i);
							flag = DTalkBo.sendMessage((String) bean.get("usernameZiZhu"), (String) bean.get("subject"), (String) bean.get("bodyText"), "", "");
						}
					}
				}
			}else{//查询发送状态
				boolean isOk=false;
				isOk=bo.getSendStatus(sendtype, ids, templateId, tableName, privSql, condSql, timeSql, salaryid);
				this.getFormHM().put("isOk", isOk?"1":"0");
			}
		
		}
		catch(Exception e)
		{
			flag=false;
			e.printStackTrace();
			if(e.toString().indexOf("电话指标没有设置")!=-1)
				throw GeneralExceptionHandler.Handle(e);
			else if(e.toString().indexOf("微信参数没有设置")!=-1){
				throw GeneralExceptionHandler.Handle(e);
			}else if(e.toString().indexOf(ResourceFactory.getProperty("sys.smsparam.nodifine"))!=-1)
				throw GeneralExceptionHandler.Handle(e);
			else
				throw GeneralExceptionHandler.Handle(e);
		}
		finally
		{
			this.getFormHM().put("e_m_type", e_m_type);
			this.getFormHM().put("flag", flag);
			if(map!=null)
			{
				this.getFormHM().put("n",(String)map.get("flag"));
				this.getFormHM().put("filename",(String)map.get("file"));
				this.getFormHM().put("count", listCount);
			}
			this.getFormHM().put("templateid",templateId);
		}
		
	}
}
