package com.hjsj.hrms.module.certificate.utils;

import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.sql.Connection;
import java.util.List;

public class CertificateUtilsBo {
	
	private Connection conn;
    private UserView userView;
    
    public CertificateUtilsBo(Connection conn, UserView userView) {
    	this.conn = conn;
        this.userView = userView;
    }
    
    /**
     * 发送通知提醒
     * @param nbase
     * @param A0100
     * @param title
     * @param content
     * @return
     * @throws GeneralException
     */
    public String sendMode(String nbase,String A0100,String title,String content) throws GeneralException{
        String msg = "";
        try{
        	/**邮件*/
            if(this.getEmailConfig()){
                LazyDynaBean emailbean = new LazyDynaBean();
                emailbean.set("objectId", nbase+A0100);
                emailbean.set("subject", title);
                emailbean.set("bodyText", content);
                emailbean.set("href", "");
                emailbean.set("hrefDesc", "");  
                AsyncEmailBo emailbo = new AsyncEmailBo(this.conn, this.userView);
                emailbo.send(emailbean);
            }
            /**短信*/
            if(this.getSmsConfig()){
                SmsBo sbo = new SmsBo(this.conn);
    			sbo.sendMessage(userView, nbase+A0100, content);
            }
            /**微信*/
            if(this.getWeixinConfig()){
    			WeiXinBo.sendMsgToPerson(nbase, A0100, "证书借阅审批", content, "", "");
            }
            /**钉钉*/
            if(this.getDDConfig()){
            	DTalkBo.sendMessage(nbase, A0100, "证书借阅审批", content, "", "");
            }
            
        } catch (Exception e) {
            msg = e.getMessage();
            throw GeneralExceptionHandler.Handle(e);
        }
        return msg;
    }
	/**
     * 获取是否配置钉钉参数
     * @return ddflag
     * @throws GeneralException
     */
    private Boolean getDDConfig() throws GeneralException{
    	
    	boolean ddflag = true;
    	RecordVo dd_vo = ConstantParamter.getConstantVo("DINGTALK");
		if(dd_vo == null){
			ddflag = false;
		}else{
			if (dd_vo != null)
			{
				try {
					String ddcropid = "",ddcorpsecret = "",ddagentid = "",dduserid = "";
					if(StringUtils.isBlank(dd_vo.getString("str_value")))
						return false;
					
					Document doc = PubFunc.generateDom(dd_vo.getString("str_value"));
					Element root = doc.getRootElement();
					List list = root.getChildren();
					for (int i = 0; i < list.size(); ++i)
					{
						Element child = (Element) list.get(i);
						String key = child.getAttributeValue("key");
						if("corpid".equals(key))
							ddcropid = child.getAttributeValue("value");
						else if ("corpsecret".equals(key))
							ddcorpsecret = child.getAttributeValue("value");
						else if ("agentid".equals(key))
							ddagentid = child.getAttributeValue("value");
						else if ("userid".equals(key))
							dduserid = child.getAttributeValue("value");
					}
					if(StringUtils.isBlank(ddcropid)
							|| StringUtils.isBlank(ddcorpsecret)
							|| StringUtils.isBlank(ddagentid)
							|| StringUtils.isBlank(dduserid)) {
						ddflag = false;
					}
				} catch (Exception e) {
					throw GeneralExceptionHandler.Handle(e);
				}finally {
				}
			}
		}
    	
    	return ddflag;
    }
	/**
     * 获取是否配置微信参数
     * @return weixinflag
     * @throws GeneralException
     */
    private Boolean getWeixinConfig() throws GeneralException{
    	
    	boolean weixinflag = true;
		try {
			// 微信相关配置，全部为空则不显示微信通知选项  
			String wxcorpid = ConstantParamter.getAttribute("wx", "corpid");
			String wxcorpsecret = ConstantParamter.getAttribute("wx", "corpsecret");
			String wxurl = ConstantParamter.getAttribute("wx", "url");
			String wxtoken = ConstantParamter.getAttribute("wx", "token");
			String wxencodingaeskey = ConstantParamter.getAttribute("wx", "encodingaeskey");
			String wxagentid = ConstantParamter.getAttribute("wx", "agentid");
			
			if(StringUtils.isEmpty(wxcorpid) 
					|| StringUtils.isEmpty(wxcorpsecret)
					|| StringUtils.isEmpty(wxurl)
					|| StringUtils.isEmpty(wxtoken)
					|| StringUtils.isEmpty(wxencodingaeskey)
					|| StringUtils.isEmpty(wxagentid))
				weixinflag = false;
			
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} 
    	
    	return weixinflag;
    }
	
	/**
     * 获取是否配置邮箱参数
     * @return emailflag
     * @throws GeneralException
     */
    private Boolean getEmailConfig() throws GeneralException{
    	
    	boolean emailflag = true;
		RecordVo email_vo = ConstantParamter.getConstantVo("SS_STMP_SERVER");
		if (email_vo == null){
			emailflag = false;
		}else{
			try {
				String email_param = email_vo.getString("str_value");
				if(StringUtils.isBlank(email_param)) 
					return false;
				
				String ehost = "",eusername = "",epassword = "",efromAddr = "", eport= "";//emaxSend="",eauthy="";
				Document doc = PubFunc.generateDom(email_param);
				Element root = doc.getRootElement();
				Element child = (Element) root.getChildren().get(0);
				
				ehost = child.getAttributeValue("host");
				eusername = child.getAttributeValue("username");
				epassword = child.getAttributeValue("password");
				efromAddr = child.getAttributeValue("from_addr");
//				emaxSend = child.getAttributeValue("max_send");
				eport = child.getAttributeValue("port");

				if(StringUtils.isBlank(ehost)
						|| StringUtils.isBlank(eusername)
						|| StringUtils.isBlank(epassword)
						|| StringUtils.isBlank(efromAddr)
						|| StringUtils.isBlank(eport)) {
					emailflag = false;
				}
			} catch (Exception e) {
				throw GeneralExceptionHandler.Handle(e);
			}finally {
			}
		}
    	
    	return emailflag;
    }
	/**
     * 获取是否短信配置
     * @return smsflag
     * @throws GeneralException
     */
    private Boolean getSmsConfig() throws GeneralException{
    	boolean smsflag = true;
    	try{
	        RecordVo sms_vo=ConstantParamter.getConstantVo("SS_SMS_OPTIONS");
	        if(sms_vo == null) {
	        	return false;
	        }
	        String param = sms_vo.getString("str_value");
	        if(StringUtils.isEmpty(param))
	        	return false;
	        
	        Document doc = PubFunc.generateDom(param);
	        String xpath = "//port[@valid=\"true\"]";	        
	        XPath reportPath = XPath.newInstance(xpath);
	        List childlist = reportPath.selectNodes(doc);
	        if(childlist.size()!=0) {
	        	Element ele = null;
	        	ele = (Element)childlist.get(0);
	        	String port = ele.getAttributeValue("name");//端口
	        	String pin = ele.getAttributeValue("pin");//PIN码
	        	//int bit=Integer.parseInt(ele.getAttributeValue("bit"));//比特率
	        	
	        	if(StringUtils.isBlank(port)
						|| StringUtils.isBlank(pin)) 
	        		smsflag = false;
	        }
    	} catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        }    
    	
    	return smsflag;
    }

}
