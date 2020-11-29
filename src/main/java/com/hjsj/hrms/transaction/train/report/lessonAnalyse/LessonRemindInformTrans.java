package com.hjsj.hrms.transaction.train.report.lessonAnalyse;

import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class LessonRemindInformTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
    	String flag = "ok";
    	String users = (String)this.getFormHM().get("users");
    	
    	ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
    	String mail = constantbo.getNodeAttributeValue("/param/lesson_hint", "mail");
		String sms = constantbo.getNodeAttributeValue("/param/lesson_hint", "sms");
		String weixin = constantbo.getNodeAttributeValue("/param/lesson_hint", "weixin");
		String template = constantbo.getNodeAttributeValue("/param/lesson_hint", "template");
		String dingTalk = constantbo.getNodeAttributeValue("/param/lesson_hint", "dingTalk");
		
		if(users==null||users.length()<3)
			flag = ResourceFactory.getProperty("train.online.message.obj");//请选择通知对象！
		else if(!"1".equals(mail)&&!"1".equals(sms)&&!"1".equals(weixin)&&!"1".equals(dingTalk))
			flag = ResourceFactory.getProperty("train.online.message.style.hint");//未设置通知方式！
		else if(template==null||template.length()<1)
			flag = ResourceFactory.getProperty("train.online.message.template.hint");//未设置通知模版！
		
		if("ok".equals(flag)){
			boolean tmp = true;
			if("1".equals(mail))
				tmp = sendEMail(template, users);
			
			if("1".equals(sms))
				tmp = sendSMS(template, users);

			String picUrl = "http://www.hjsoft.com.cn:8089/UserFiles/Image/announce.png";
			String tipic = ResourceFactory.getProperty("message.weixin.lesson");
			sendLessonToWX(template,users,tipic,picUrl,"");
				
			if(!tmp)
				flag = ResourceFactory.getProperty("train.send.message.failure");//发送失败！
		}
		this.getFormHM().put("flag", flag);
    }
    
    /**
	 * 发送学习情况通知到微信
	 * @param templateId 模板Id
	 * @param users 发送的人员
	 * @param topic 标题
	 * @param picUrl 图片url
	 * @param url 点击图文消息进入页面地址
	 * @return
	 */
	public boolean sendLessonToWX(String templateId,String users,String topic,String picUrl,String url){
		boolean flag = false;
		try{
		    ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
	        String weixin = constantbo.getNodeAttributeValue("/param/lesson_hint", "weixin");
	        String dingTalk = constantbo.getNodeAttributeValue("/param/lesson_hint", "dingTalk");
	        
			if(templateId!=null&&templateId.length()>0){
				EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				RecordVo vo = null;
				
				String username = "";
				RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
				if (login_vo == null) {
					username = "username";
				} else {
					String login_name = login_vo.getString("str_value").toLowerCase();
					int idx = login_name.indexOf(",");
					if (idx == -1) {
						username = "username";
					} else {
						username = login_name.substring(0, idx);
						if ("#".equals(username) || "".equals(username)) {
							username = "username";
						}
					}
				}
				
				ArrayList fieldList=bo.getTemplateFieldInfo(Integer.parseInt(templateId),2);
				String[] arr = users.split(",");
				for (int i = 0; i < arr.length; i++) {
					if(arr[i]==null&&arr[i].length()<3)
						continue;
					String base = arr[i].substring(0,3);
					String user = arr[i].substring(3);
					
					vo = new RecordVo(base+"A01");
					vo.setString("a0100", user);
					if(dao.isExistRecordVo(vo)){
						if(vo!=null){
							String content = bo.getFactContent(bo.getEmailContent(Integer.parseInt(templateId)), arr[i], fieldList, userView);
							/*content=content.replaceAll(" ", "");
						    content=content.replaceAll("\\r","");
						    content=content.replaceAll("\\n","");
						    content=content.replaceAll("\\r\\n","");*/
							if("1".equals(weixin)&&StringUtils.isNotEmpty(ConstantParamter.getAttribute("wx", "corpid")))
							    flag = WeiXinBo.sendMsgToPerson(vo.getString(username), topic, content, picUrl, url);
							
							//推送至钉钉 chenxg 2017-06-01
                            if("1".equals(dingTalk) && StringUtils.isNotEmpty(ConstantParamter.getAttribute("DINGTALK","corpid")))
                                flag = DTalkBo.sendMessage(vo.getString(username), topic, content, "", "");
							    
						}
					}
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
    
    /**
	 * 发送短信
	 * @param templateId 短信模版
	 * @param tomails 发送对象 Usr0000009`Usr0000036
	 */
	private boolean sendSMS(String templateId, String tomails) {
		boolean flag = true;
		try {
			String phone=ConstantParamter.getMobilePhoneField().toLowerCase();
			RecordVo vo = null;
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
			EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
			if(templateId!=null&&templateId.length()>0){
				ArrayList fieldList=bo.getTemplateFieldInfo(Integer.parseInt(templateId),2);
				String[] users = tomails.split(",");
				for(int i=0; i<users.length;i++){
					if(users[i]==null||users[i].length()<3)
						continue;
					String tmpNbase = users[i].substring(0,3);
					String tmpA0100 = users[i].substring(3);
					
					vo = new RecordVo(tmpNbase+"A01");
					vo.setString("a0100", tmpA0100);
					if(dao.isExistRecordVo(vo)){
						if(vo!=null){
							String _phone=vo.getString(phone);
							String phones = tmpNbase+tmpA0100;
							String content = bo.getFactContent(bo.getEmailContent(Integer.parseInt(templateId)), users[i], fieldList, userView);
							content=content.replaceAll(" ", "");
						    content=content.replaceAll("\\r","");
						    content=content.replaceAll("\\n","");
						    content=content.replaceAll("\\r\\n","");
							SmsBo sbo = new SmsBo(this.getFrameconn());
							sbo.sendMessage(userView, phones, content);
						}
					}
				}//end while
			}
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 发送邮件
	 * @param templateId 邮件模版
	 * @param tomails 发送对象 Usr0000009`Usr0000036
	 */
	public boolean sendEMail(String templateId,String tomails)
	{
		boolean flag = true;
		try {
			String email=ConstantParamter.getEmailField().toLowerCase();
			RecordVo vo = null;
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
			StringBuffer buf=new StringBuffer();//邮件内容
			StringBuffer title=new StringBuffer();//邮件标题
			
			EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
			if(templateId!=null&&templateId.length()>0){
				HashMap hashmap = bo.getSubject(templateId);
				ArrayList fieldList=bo.getTemplateFieldInfo(Integer.parseInt(templateId),2);
				
				String[] users = tomails.split(",");
				for(int i=0; i<users.length;i++){
					if(users[i]==null||users[i].length()<3)
						continue;
					String tmpNbase = users[i].substring(0,3);
					String tmpA0100 = users[i].substring(3);
					
					vo = new RecordVo(tmpNbase+"A01");
					vo.setString("a0100", tmpA0100);
					if(dao.isExistRecordVo(vo)){
						if(vo!=null){
							buf.setLength(0);
							title.setLength(0);
							String email_address=vo.getString(email);
							if(!bo.isMail(email_address))
								continue;
							String content = bo.getFactContent(bo.getEmailContent(Integer.parseInt(templateId)), users[i], fieldList, userView);
							content = content.replaceAll("\r\n", "<br/>");
							content = content.replace("\r", "<br/>");
							content = content.replace("\n", "<br/>");
							
							/** 发送邮件 实现附件发送  wangb 20170714 29685 */
							ArrayList attachList = getAttachFileName(templateId);
							AsyncEmailBo emailBo = new AsyncEmailBo(this.frameconn, this.userView);
							LazyDynaBean emailBean = new LazyDynaBean();
							emailBean.set("toAddr", email_address);
							emailBean.set("subject", hashmap.get("subject"));
							emailBean.set("bodyText", content);
							   /*添加附件*/
							emailBean.set("attachList", attachList);
							emailBo.sendSync(emailBean);
//							EMailBo mailbo = new EMailBo(this.getFrameconn(),true, tmpNbase);
//							mailbo.sendEmail((String) hashmap.get("subject"), content,(String) hashmap.get("attach"), email_address);
						}
					}
				}//end while
			}
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	
	 /**
	    * 将邮件模板的附件从数据库中取出，还原成文件，返回的是由文件名组成的列表
	    * @param templateId
	    * @return
	    */
	   private ArrayList getAttachFileName(String templateId)
	   {
		   ArrayList list = new ArrayList();
		   InputStream is=null;
		   FileOutputStream fileOut=null;
		   try
		   {
			   String sql ="select filename,attach from email_attach where id="+templateId;
			   ContentDAO dao = new ContentDAO(this.frameconn);
			   byte buf[] = new byte[1024];
			   RowSet rs=null;
			   rs=dao.search(sql);
			   while(rs.next())
			   {
				   try{
					   is=rs.getBinaryStream("attach");
					   if(is==null)
					   {
						   continue;
					   }
					   String filename=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+rs.getString("filename");
					   File file = new File(filename);
					   if(file.exists())
						   if(file.delete())
							   file.createNewFile();
						   else
						   {
						   }
					   if(!file.exists())
						   file.createNewFile();
					   fileOut = new FileOutputStream(filename);
					   int length;
					   while((length=is.read(buf))!=-1)
					   {
						   fileOut.write(buf,0,length);
						   fileOut.flush();
					   }
					   list.add(filename);
				   }finally{
					   PubFunc.closeResource(fileOut);
					   PubFunc.closeIoResource(is);
				   }
			   }
		   }
		   catch(Exception e)
		   {
			   e.printStackTrace();
		   }
		   return list;
	   }
}
