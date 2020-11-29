package com.hjsj.hrms.module.recruitment.recruitprocess.transaction;

import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.ResumeInfoSMSBo;
import com.hjsj.hrms.module.recruitment.util.EmailInfoBo;
import com.hjsj.hrms.module.recruitment.util.FeedBackBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public class SendEmailTrans extends IBusiness  {

	/***
	 * 发送邮件
	 */
	@Override
    public void execute() throws GeneralException {
		String msg="";
		try {			
			String c0102 =SafeCode.decode((String)this.getFormHM().get("c0102"));//应聘者邮箱
			//c0102 = "east_xiaodong@163.com";
			String title = SafeCode.decode((String)this.getFormHM().get("title"));//邮件标题
			String content = SafeCode.decode((String)this.getFormHM().get("content"));//邮件内容
			content = PubFunc.keyWord_reback(content);
			String templateId = SafeCode.decode((String)this.getFormHM().get("templateId"));//邮件模板
			String phoneNum = SafeCode.decode((String)this.getFormHM().get("phoneNum"));//电话
			String a0101 = SafeCode.decode((String)this.getFormHM().get("a0101"));//姓名
			String a0100 = SafeCode.decode((String)this.getFormHM().get("a0100"));//姓名
			String nbase = SafeCode.decode((String)this.getFormHM().get("nbase"));//姓名
			String z0301 = SafeCode.decode((String)this.getFormHM().get("z0301"));//姓名
			//给面试者发送邮件
			boolean candidateMail = (Boolean) this.getFormHM().get("candidateMail");
			//给面试者发送短信
			boolean candidateText = (Boolean) this.getFormHM().get("candidateText");
			//给面试者发送反馈消息
			boolean feedBack = (Boolean) this.getFormHM().get("feedBack");
			ArrayList<LazyDynaBean> beans = new ArrayList<LazyDynaBean>();
			if(candidateMail){
				EmailInfoBo emailBo = new EmailInfoBo(frameconn, userView);
				ArrayList fileList = emailBo.getAttachFileName(templateId);
				LazyDynaBean bean = emailBo.getTemplateInfo("7","20",templateId,"");
				String return_address = (String)bean.get("return_address");
				msg = emailBo.getEmailBean(beans, templateId, c0102, title, content,fileList,return_address);
				String bulkSendEmail = emailBo.bulkSendEmail(beans);
	        	if(!"".equals(bulkSendEmail))
	        		msg = bulkSendEmail;
			}
			
			if(candidateText){
				SmsBo smsbo=new SmsBo(this.getFrameconn());
				ArrayList destlist = new ArrayList();
				LazyDynaBean dyvo = new LazyDynaBean();
				String sender = StringUtils.isEmpty(this.userView.getUserFullName())?this.userView.getUserName():this.userView.getUserFullName();
				if(StringUtils.isNotEmpty(phoneNum)){
					dyvo.set("sender",sender);
					dyvo.set("receiver",a0101);
					dyvo.set("phone_num",phoneNum);
					dyvo.set("msg",ResumeInfoSMSBo.delHTMLTag(content));
					destlist.add(dyvo);
					try {
						smsbo.batchSendMessage(destlist);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					if("".equals(msg))
						msg=a0101+"的电话号码为空！";
					else{
						if("1".equals(msg))
							msg=a0101+"的电话号码为空！";
						else
							msg+="<br>"+a0101+"的电话号码为空！";
					}
				}
			}
        	if(feedBack) {
        		FeedBackBo bo = new FeedBackBo(this.frameconn);
        		ArrayList<String> value = new ArrayList<String>();
        		value.add(content);
    			value.add(PubFunc.decrypt(a0100).trim());
    			value.add(PubFunc.decrypt(nbase).trim());
    			value.add(PubFunc.decrypt(z0301));
    			ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
    			list.add(value);
        		bo.updateFeedBack(list);
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.getFormHM().put("msg", msg);
		}
	}
	
}
