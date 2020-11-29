package com.hjsj.hrms.businessobject.attestation.mobile;

import com.cmpp2.CmppSession;
import com.cmpp2.ShortMessage;
import com.cmpp2.SmsShortMessage;
import com.hjsj.hrms.interfaces.sys.SmsProxy;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.admin.ReVerify;
import com.hrms.struts.constant.SystemConfig;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.util.List;
import java.util.Random;
/**
 * 用移动标准cmpp，短信校验认证
 * @author Owner
 *
 */
public class CmppNoteCheckSend implements ReVerify, SmsProxy{

	/**短信猫=0|短信网关=1*/
	private String flag="0";
	/**短信网关参数*/
	private String username;
	private String password;
	private String up_url;
	private String down_url;
	private String channelid;
	@Override
    public String sendValidateCodeBySms(String phone) {
		// TODO Auto-generated method stub
		//System.out.println("开始调用"+phone);
		//Category.getInstance("com.hrms.frame.dao.ContentDAO").error("开始调用"+phone);
		String issendsme;
		try {
			issendsme = SystemConfig.getPropertyValue("issendsms");
			if(issendsme!=null&& "false".equals(issendsme.trim())) {
				return "12345678";
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        Random random;
    	StringBuffer strSrc=new StringBuffer();
		strSrc.append("123456789");
		random=new Random(System.currentTimeMillis()); 
		int codelen=6;
		StringBuffer checkNum=new StringBuffer();
		int index=0;
		for(int i=0;i<codelen;i++)
		{
			index=random.nextInt(9);
			checkNum.append(strSrc.charAt(index));
		}    	
		
		try
		{
			
			if(phone==null||phone.length()<=0)
			{
				phone=SystemConfig.getProperty("sendsmsphone");
				if(phone==null||phone.length()<=0) {
					return "";
				}
			}			
			//Category.getInstance("com.hrms.frame.dao.ContentDAO").error("建立初始化");
			initparamter();				
			//Category.getInstance("com.hrms.frame.dao.ContentDAO").error("建立CMPPAPI链接");
			/*main_connect();//建立CMPPAPI链接				
			submitMsg("",phone,"您的e-HR登陆临时校验码是："+checkNum.toString());*/
			CmppSession session=null;
			session = new CmppSession(null);
			session.setDebug(true);			
			session.connect(this.up_url, Integer.parseInt(this.down_url), this.username, this.password); 
			String msg="您的e-HR登陆临时校验码是："+checkNum.toString();
			ShortMessage sms = new SmsShortMessage("66", this.channelid, phone, msg,this.username);
			//System.out.println(phone+"----"+msg);
			long msgId = session.submit(sms);
			//Category.getInstance("com.hrms.frame.dao.ContentDAO").error("开始完成"+phone);
			
		}catch(Exception e)
		{
		  e.printStackTrace();	
		}
		//System.out.println(checkNum.toString());
		return checkNum.toString();
	}
	private void initparamter()
	{
        RecordVo sms_vo=ConstantParamter.getConstantVo("SS_SMS_OPTIONS");
        if(sms_vo==null) {
			return ;
		}
        String param=sms_vo.getString("str_value");
        if(param==null|| "".equals(param)) {
			return;
		}
        Element ele=null;
        try
        {
	        Document doc = PubFunc.generateDom(param);
	        Element root=doc.getRootElement();
	        String flagvalue=root.getAttributeValue("flag");
	        if(flagvalue==null||flagvalue.length()==0) {
				flagvalue="0";
			}
	        this.flag=flagvalue;
	        String xpath = "//port[@valid=\"true\"]";	        
            XPath reportPath = XPath.newInstance(xpath);
            List childlist = reportPath.selectNodes(doc);
            if(childlist.size()!=0)
            {
            	ele=(Element)childlist.get(0);
            	
            }
            /**短信网关参数*/
    		xpath="/ports/gateway";
    		reportPath = XPath.newInstance(xpath);
    		childlist = reportPath.selectNodes(doc);
            if(childlist.size()!=0)
            {
	            ele=(Element)childlist.get(0);
	            this.username=ele.getAttributeValue("username");
	            this.password=ele.getAttributeValue("password");	
	            this.channelid=ele.getAttributeValue("channelid");
	            childlist=ele.getChildren("up_url");
	            if(childlist.size()!=0)
	            {
	            	Element c_ele=(Element)childlist.get(0);
	            	this.up_url=c_ele.getText();
	            }
	            childlist=ele.getChildren("down_url");
	            if(childlist.size()!=0)
	            {	
	            	Element c_ele=(Element)childlist.get(0);
	            	this.down_url=c_ele.getText();            
	            }
            }
        }
            
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	//throw GeneralExceptionHandler.Handle(ex);
        }     		
	}
	@Override
    public boolean sendMessage(String phone, String msg) {
		// TODO Auto-generated method stub
		return false;
	}

}
