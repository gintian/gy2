package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
public class SendEmail {

    String host = "";
	String user = "";
	String password = "";
	String senderName="";//发件人昵称

	public SendEmail(String host,String user,String password)
	{
		setInfo();
		this.host = host;
		this.user = user;
		this.password = password;
	}

	public SendEmail()
	{
		
	}
	
	public boolean setInfo()
	{
		boolean flag=true;
		 RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_STMP_SERVER");
		 if(stmp_vo==null) {
             return false ;
         }
	        String param=stmp_vo.getString("str_value");
	        if(param==null|| "".equals(param)) {
                return false;
            }
	        try
	        {
		        Document doc = PubFunc.generateDom(param);
		        Element root = doc.getRootElement();
		        Element stmp=root.getChild("stmp");
		        if(stmp==null) {
                    return false;
                }
		        if(stmp.getAttributeValue("host")==null|| "".equals(stmp.getAttributeValue("host"))) {
                    return false;
                }
		        if(stmp.getAttributeValue("username")==null|| "".equals(stmp.getAttributeValue("username"))) {
                    return false;
                }
		        if(stmp.getAttributeValue("password")==null|| "".equals(stmp.getAttributeValue("password"))) {
                    return false;
                }
		        this.host=stmp.getAttributeValue("host");
		        this.user=stmp.getAttributeValue("username");
		        this.password=stmp.getAttributeValue("password");
		        this.senderName=stmp.getAttributeValue("sendername")==null? "" : stmp.getAttributeValue("sendername");
		               
	        }
	        catch(Exception ex)
	        {
	        	ex.printStackTrace();
	        }
	        return flag;
		
	}
	
	
	public  void send(String to, String subject,
			String content){
		if(this.host==null||this.host.length()<2||this.user==null||this.user.length()<2||this.password==null||this.password.length()<1) {
            return;
        }
		Properties props = new Properties();
		props.put("mail.smtp.host",this.host);
		// 指定SMIP服务器
		props.put("mail.smtp.auth", "true");		
		try {
			Session mailSession = Session.getDefaultInstance(props);
			// 是否在控制台显示debug信息
			mailSession.setDebug(false);
			Message message = new MimeMessage(mailSession);
			InternetAddress internetAddress=new InternetAddress(this.user);
			if(StringUtils.isNotBlank(this.senderName)){
				internetAddress.setPersonal(this.senderName);
			}
			message.setFrom(internetAddress);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			// 收件人
			message.setSubject(subject);
			/**显示html格式的邮件内容，要进行编码*/
			message.setContent(content, "text/html;charset=GB2312");
			//message.setText(content);
			message.saveChanges();
			Transport transport = mailSession.getTransport("smtp");
			transport.connect(this.host, this.user, this.password);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (Exception e) {
			
		}
	}

	public static void main(String args[]) {

		//SendEmail sendEmail=new SendEmail("smtp.126.com","lq7905_0@126.com","lqlqlqlq");
		//sendEmail.send("dengcan802@yahoo.com.cn", "test ddtest ",
				//"testgdfg号码");
		
		//String sdfdsf="(~姓名~)：\r\n 您好！\r\n很高兴您的简历符合我们(~应聘职位~)职位的要求，如果有时间的话，请于(~面试时间~) 来我公司(~面试地点~) 面试";
		
		
		
	}
}
