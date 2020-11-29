package com.hjsj.hrms.transaction.general.email_template;

import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hjsj.hrms.businessobject.gz.CashListBo;
import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.hire.AutoSendEMailBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SendEmailTrans extends IBusiness{

	public void execute() throws GeneralException {
		//int n=0;
		HashMap map =null;
		String templateId="";
		String code="";
		String e_m_type="";
		try
		{
			String sendtype=(String)this.getFormHM().get("type");//=1 发送选择的人员,=2群发
			e_m_type=(String)this.getFormHM().get("e_m_type");
			String ids = (String)this.getFormHM().get("ids");
			templateId=(String)this.getFormHM().get("templateid");
			String queryvalue=(String)this.getFormHM().get("queryvalue");
			String queryYearValue=(String)this.getFormHM().get("queryYearValue");
			String beforeSql=(String)this.getFormHM().get("beforeSql");
			String _beforeSql=PubFunc.decrypt(SafeCode.decode(beforeSql));
			code=(String)this.getFormHM().get("code");
			String salaryid = (String)this.getFormHM().get("salaryid");
			
			String tableName = this.userView.getUserName()+"_salary_"+salaryid;
			//ArrayList emailContentList,String templateId,ArrayList attachList,String from
			 SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			  String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			
			  if(manager.length()==0||this.userView.getUserName().equalsIgnoreCase(manager))
				  tableName=this.userView.getUserName()+"_salary_"+salaryid;
			  else
				  tableName=manager+"_salary_"+salaryid;
			  CashListBo clb = new CashListBo(this.getFrameconn(),"0",salaryid);
			  clb.setUserview(this.userView);
			String privSql=clb.getPrivSql(this.userView, gzbo);
			EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
			AutoSendEMailBo ase=new AutoSendEMailBo(this.getFrameconn());
			String mobile_field=ase.getMobileField();
			if("1".equals(e_m_type)&&(mobile_field==null||mobile_field.trim().length()==0))
				throw GeneralExceptionHandler.Handle(new Exception("电话指标没有设置！"));
			String codeSql=clb.getCodeSql("s", code);
			if(codeSql!=null&&codeSql.length()>0)
			{
				if(privSql!=null&&privSql.length()>0)
					privSql+=" and "+codeSql;
				else
					privSql=codeSql;
			}
			ArrayList contentList=bo.getEmailContentList(sendtype,ids,templateId,this.userView,queryYearValue,queryvalue,tableName,privSql,mobile_field,e_m_type,_beforeSql);
			ArrayList attachList=bo.getAttachFileName(templateId);
			String fromAddress=bo.getFromAddr();
			if("1".equals(e_m_type))
				map=bo.sendMessage(0, contentList, templateId, attachList, "", userView, 0, mobile_field);
			else if("0".equals(e_m_type)){
				map=bo.sendEmail(0,contentList,templateId,attachList,fromAddress,this.getUserView(),0);
			}
			else if("2".equals(e_m_type)){
				String corpid = (String) ConstantParamter.getAttribute("wx","corpid");
				if(corpid == null || "".equals(corpid)){
					throw GeneralExceptionHandler.Handle(new Exception("微信参数没有设置！"));
				}
				if(corpid!=null&&corpid.length()>0){//推送微信公众号  zhaoxg add 2015-5-5
					for(int i=0;i<contentList.size();i++){
						LazyDynaBean bean = (LazyDynaBean) contentList.get(i);
						String username = gzbo.getZizhuUsername((String)bean.get("username"));
						WeiXinBo.sendMsgToPerson(username, (String)bean.get("subject"), (String)bean.get("content"), "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png", "");
					}
				}
			}
			else if("3".equals(e_m_type)){
				String corpid = (String) ConstantParamter.getAttribute("DINGTALK","corpid");
				if(corpid == null || "".equals(corpid)){
					throw GeneralExceptionHandler.Handle(new Exception("钉钉参数没有设置！"));
				}
				if(corpid!=null&&corpid.length()>0){//推送钉钉 add 2017-6-1
					for(int i=0;i<contentList.size();i++){
						LazyDynaBean bean = (LazyDynaBean) contentList.get(i);
						String username = gzbo.getZizhuUsername((String)bean.get("username"));
						DTalkBo.sendMessage(username, (String)bean.get("subject"), (String)bean.get("content"), "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png", "");
					}
				}
			}
		
		}
		catch(Exception e)
		{
			//n=1;
			e.printStackTrace();
			if(e.toString().indexOf("电话指标没有设置")!=-1)
				throw GeneralExceptionHandler.Handle(e);
			else if(e.toString().indexOf("微信参数没有设置")!=-1){
				throw GeneralExceptionHandler.Handle(e);
			}else if(e.toString().indexOf("短信发送失败")!=-1){
				throw GeneralExceptionHandler.Handle(e);
			}
		}
		finally
		{
			
			
			this.getFormHM().put("e_m_type", e_m_type);
			if(map!=null)
			{
				this.getFormHM().put("n",(String)map.get("flag"));
				this.getFormHM().put("filename",(String)map.get("file"));
			}
			this.getFormHM().put("templateid",templateId);
			this.getFormHM().put("code",code);
		}
		
	}
}
