package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hjsj.hrms.businessobject.sys.SysParamConstant;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.Des;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 30200710241
 * <p>Title:GetPasswordTrans.java</p>
 * <p>Description>:GetPasswordTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Aug 24, 2009 2:52:15 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class GetPasswordTrans extends IBusiness{

	private String msg="0";
	private boolean flag=false;
	private Pattern emailpattern=Pattern.compile("([a-zA-Z0-9]+[_|\\_|\\-|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,3}");
	private Pattern phonepattern=Pattern.compile("[0-9]{11}");
	String usernamevalue="";
	String passwordvalue="";
	String userfullname="";
	String sql="";
	String email="";
	String phone="";
	public void execute() throws GeneralException {
		try
		{
			
			//Connection con=(Connection)AdminDb.getConnection();
			/**用户登录平台=1业务=2自助*/
			String logintype=(String)this.getFormHM().get("logintype");
			/**找回密码方式=1根据电话=2根据邮箱*/
			String type=(String)this.getFormHM().get("type");
			/**电话或邮箱*/
			String inputusername = SafeCode.decode((String)this.getFormHM().get("ZE"));
			String ZE=inputusername;
			String logonClassFunc = SystemConfig.getPropertyValue("logonclass_func");
			if(logonClassFunc!=null && logonClassFunc.length()>0){
				ZE = getRealUsername(ZE,logonClassFunc);
			}
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			//String email = getEmail(ZE,dao);
			//if(email==null||"".equals(email)){
			//	throw GeneralExceptionHandler.Handle(new Exception("邮箱信息为空，请联系管理员录入邮箱信息！"));
			//}
			//String phone="";
			String fromaddr=this.getFromAddr();
			/**取得密码的长度*/
			int pwdlen=8;//getPwdLen();
			String	pwdlength=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.PASSWORDLENGTH);
			try{
				pwdlen = Integer.parseInt(pwdlength);
			}catch(Exception e){
				pwdlen=8;
			}
			String newpassword=getRandomPwd(pwdlen);
			if("2".equals(type)&&(fromaddr==null|| "".equals(fromaddr)))
			{
				msg="系统未设置邮件服务器，无法给您发送邮件!";
				this.getFormHM().put("msg", SafeCode.encode(msg));
				return;
			}
			if("1".equals(logintype))
			{
				//验证密码通过此发送方式是否可修改 
				validateCanChange(newpassword ,ZE,type);
			}
			else
			{
				//验证密码通过此发送方式是否可修改  
				validateCanChangeSelf(newpassword ,ZE,type);
			}
			if(!flag)
			{
				msg="系统未找到与您输入匹配的用户，请您仔细检查输入是否正确!";
				this.getFormHM().put("msg", SafeCode.encode(msg));
				return;
			}
			this.usernamevalue = inputusername;

			/* 56539 聊城大学 为了安全，找回密码发送的信息中用户名部分用*代替 guodd 2020-01-03*/
			String secretStr = "*****************************************************";
			if(usernamevalue.length()<3){
				usernamevalue = usernamevalue.substring(0,1)+secretStr.substring(0,usernamevalue.length()-1);
			}else if(usernamevalue.length()<6){
				usernamevalue = usernamevalue.substring(0,1)+secretStr.substring(0,usernamevalue.length()-2)+usernamevalue.substring(usernamevalue.length()-1);
			}else{
				usernamevalue = usernamevalue.substring(0,2)+secretStr.substring(0,usernamevalue.length()-4)+usernamevalue.substring(usernamevalue.length()-2);
			}
			String title="用户名和密码信息";
			String why=SystemConfig.getPropertyValue("sys_name");
			if(why==null|| "".equals(why))
				why="";
			String str=why;
			if(str.length()==0)
				str=ResourceFactory.getProperty("frame.logon.title");
			StringBuffer content = new StringBuffer("");
			if("2".equals(type))
			{
				content.append(((userfullname==null|| "".equals(userfullname))?usernamevalue:userfullname)+", 您好:<br>");
				content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;登录帐号："+usernamevalue);
				content.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;登录口令："+(passwordvalue==null?"":passwordvalue));
				content.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;请您妥善保管。");
				content.append("该邮件由"+str+"为您发送。");
				
				try
				{
	    	    	EMailBo bo=new EMailBo(this.getFrameconn(),true,"");
	    	    	bo.sendEmail(title,content.toString(),"",fromaddr,email);
				}catch(Exception e)
				{
					e.printStackTrace();
					msg="邮件服务器配置不成功,未能成功发送邮件!";
					this.getFormHM().put("msg", SafeCode.encode(msg));
					return;
				}
				dao.update(sql);
			}else if("3".equals(type)){
				content.append("尊敬的"+((userfullname==null|| "".equals(userfullname))?usernamevalue:userfullname)+":");
				content.append("\n        您的"+str+"用户名：\""+usernamevalue+"\"");
				content.append("密码：\""+(passwordvalue==null?"":passwordvalue)+"\"。");
				try
				{
					WeiXinBo.sendMsgToPerson(ZE, title, content.toString(), "http://www.hjsoft.com.cn:8089/UserFiles/Image/warn.png", "");
				}catch(Exception e)
				{
					e.printStackTrace();
					msg="微信接口配置不成功,未能成功发送微信消息!";
					this.getFormHM().put("msg", SafeCode.encode(msg));
					return;
				}
				dao.update(sql);
			}else if("4".equals(type)){
				content.append("尊敬的"+((userfullname==null|| "".equals(userfullname))?usernamevalue:userfullname)+":");
				content.append("\n        您的"+str+"用户名：\""+usernamevalue+"\"");
				content.append("密码：\""+(passwordvalue==null?"":passwordvalue)+"\"。");
				try
				{
					DTalkBo.sendMessage(ZE, title, content.toString(), "http://www.hjsoft.com.cn:8089/UserFiles/Image/warn.png", "");
				}catch(Exception e)
				{
					e.printStackTrace();
					msg="钉钉接口配置不成功,未能成功发送钉钉消息!";
					this.getFormHM().put("msg", SafeCode.encode(msg));
					return;
				}
				dao.update(sql);
			}
			else
			{
				content.append(((userfullname==null|| "".equals(userfullname))?usernamevalue:userfullname)+",您好:");
				content.append("您的"+str+",用户名：\""+usernamevalue+"\"");
				content.append(",密码：\""+(passwordvalue==null?"":passwordvalue)+"\"。");
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				content.append(format.format(new Date()));
				try
				{
					if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
					{
		    	    	com.hjsj.hrms.businessobject.attestation.unicom.UnicomSms ucs= new com.hjsj.hrms.businessobject.attestation.unicom.UnicomSms();
                        boolean isSend = ucs.sendSms(phone, content.toString());
					}
					else
					{
						ArrayList destlist = new ArrayList();
						LazyDynaBean dyvo=new LazyDynaBean();
						dyvo.set("sender",str);
						dyvo.set("receiver",phone);
						dyvo.set("phone_num",phone);
						dyvo.set("msg",content.toString());
						destlist.add(dyvo);
						SmsBo smsbo=new SmsBo(this.getFrameconn());
		        		smsbo.batchSendMessage(destlist);
					}
				}catch(Exception e)
				{
					e.printStackTrace();
					msg="短信服务器配置不成功,未能成功发送短信!";
					this.getFormHM().put("msg", SafeCode.encode(msg));
					return;
				}
				dao.update(sql);
			}
		    this.getFormHM().put("type",type);
			this.getFormHM().put("msg",SafeCode.encode(msg));
			if("0".equals(msg)){
				UserObjectBo userBo = new UserObjectBo(this.frameconn);
				userBo.change2firstPwd("'"+ZE+"'");
			}
		}
		catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
	private String getEmail(String username,ContentDAO dao) throws Exception{
		String returnstr = "";
		try {
			String[] pre = getPre(dao);
			String email = "";
			this.frowset = dao.search("select str_value from constant where constant = 'SS_EMAIL'");
			while(this.frowset.next()){
				email = this.frowset.getString("str_value");
			}
			if("#".equals(email)){
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.employResume.noemailfield")));
			}
			StringBuffer sql = new StringBuffer();
			for (int i = 0; i < pre.length-1; i++) {
				sql.append("select "+email+" from "+pre[i]+" where username = '"+username+"' union all ");
			}
			sql.append("select "+email+" from "+pre[pre.length-1]+"A01 where username = '"+username+"'");
		
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next()){
				returnstr = this.frowset.getString(email);
			}
			if(returnstr==null||"".equals(returnstr)){
				this.frowset = dao.search("select email from OperUser where UserName = '"+username+"'");
				while(this.frowset.next()){
					returnstr = this.frowset.getString("email");
				}
			}
		} catch (SQLException e) {
			throw GeneralExceptionHandler.Handle(e);
		}
		return returnstr;
	}
	
	public String getEmailField(){
    	String str="";
    	try{
    	   RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_EMAIL");
           if(stmp_vo==null)
         	  return "";
           String param=stmp_vo.getString("str_value");
           if(param==null|| "#".equals(param))
         	   return "";
           str=param;
    	}catch(Exception ex)
         {
         	ex.printStackTrace();
         }  
    	
    	return str;
    }
	public String getFromAddr() throws GeneralException 
	{
		String str = "";
        RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_STMP_SERVER");
        if(stmp_vo==null)
        	return "";
        String param=stmp_vo.getString("str_value");
        if(param==null|| "".equals(param))
        	return "";
        try
        {
	        Document doc = PubFunc.generateDom(param);
	        Element root = doc.getRootElement();
	        Element stmp=root.getChild("stmp");	
	        str=stmp.getAttributeValue("from_addr");
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ex);
        }  
        return str;
	}
	public String[] getPre(ContentDAO dao)
	{
		String[] pre = new String[0];
		try
		{
			/**登录参数表*/
            RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
            String A01="";
            if(login_vo!=null) 
              A01 = login_vo.getString("str_value").toLowerCase();
            
            if(A01.length()==0)
            	return pre;
			String sql ="select pre from dbname ";
			this.frowset=dao.search(sql);
			String aa="";
			int i=0;
			while(this.frowset.next())
			{
				String dbpre = this.frowset.getString(1);
				if(A01.toLowerCase().indexOf(dbpre.toLowerCase())!=-1){
					if(i!=0)
						aa+=",";
					aa+=dbpre;
					i++;
				}
			}
			pre=aa.split(",");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return pre;
	}
	private StringBuffer strSrc=new StringBuffer();
	private Random random;//=new Random();
	private String username;
	private String password;
	public GetPasswordTrans() {
		strSrc.append("qazwsxedcrfvtgbyhnujmiklp192384756");
		//Date date=new Date();
		random=new Random(System.currentTimeMillis());
	}
	/**
	 * 取得密码的长度以及用户名及口令
	 * 随机生成8位的口令
	 * @return
	 */
	private int getPwdLen()
	{
		int len=8;
        /**登录参数表,登录用户指定不是username or userpassword*/
        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
   
        if(login_vo==null)
        {
            username="username";
            password="userpassword";
        }
        else
        {
            String login_name = login_vo.getString("str_value").toLowerCase();
            int idx=login_name.indexOf(",");
            if(idx==-1)
            {
                username="username";
                password="userpassword";
            }
            else
            {
                username=login_name.substring(0,idx);
                if("#".equals(username)|| "".equals(username))
                	username="username";
                password=login_name.substring(idx+1);  
                if("#".equals(password)|| "".equals(password))
                	password="userpassword";
                else
                {
                	FieldItem item=DataDictionary.getFieldItem(password);
                	len=8;//item.getItemlength();       //当然可以按实际长度生成随机密码         	
                }
            }
        }

		return len;
	}
	/**
	 * 取得随机生成的密码的长度
	 * @param pwdlen
	 * @return
	 */
	private String getRandomPwd(int pwdlen)
	{
		StringBuffer strpwd=new StringBuffer();
		int index=0;
		for(int i=0;i<pwdlen;i++)
		{
			index=random.nextInt(33);
			strpwd.append(this.strSrc.charAt(index));
		}
		return strpwd.toString();
	}
	
	private void validateCanChange(String newpassword,String ZE,String type) throws Exception{

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String email="";
		String phone="";
		StringBuffer buf = new StringBuffer("");
		sql="update operuser set password='"+newpassword+"' where UPPER(username)='"+ZE.toUpperCase()+"'";
		if(ConstantParamter.isEncPwd(this.getFrameconn()))
        {
    		Des des0=new Des(); 
			String newpwd=des0.EncryPwdStr(newpassword);    
			sql="update operuser set password='"+newpwd+"' where UPPER(username)='"+ZE.toUpperCase()+"'";
        }
		buf.append("select email,username,fullname,password,phone from operuser where UPPER(username)='"+ZE.toUpperCase()+"'");
		this.frowset=dao.search(buf.toString());
		while(this.frowset.next())
		{
			if("3".equals(type)){
				msg="业务用户不支持微信消息找回密码！";
				this.getFormHM().put("msg", SafeCode.encode(msg));
				return;
			}
			flag=true;
			usernamevalue=this.frowset.getString("username");
			userfullname=this.frowset.getString("fullname");
			email=this.frowset.getString("email");
			email=email==null?"":email;
			if("2".equals(type)&&email.length()<1){
				msg="无邮件地址，无法找回密码!";
				this.getFormHM().put("msg", SafeCode.encode(msg));
				return;
			}
			if("2".equals(type)){
				Matcher m = emailpattern.matcher(email);
				if(!m.matches()){
					msg="邮件地址不正确，无法找回密码!";
					this.getFormHM().put("msg", SafeCode.encode(msg));
					return;
				}
			}
			passwordvalue=newpassword;
			phone=this.frowset.getString("phone");
			phone=phone==null?"":phone;
			if("1".equals(type)&&phone.length()<1){
				msg="无移动电话号码，无法找回密码!";
				this.getFormHM().put("msg", SafeCode.encode(msg));
				return;
			}
			if("1".equals(type)){
				Matcher m = phonepattern.matcher(phone);
				if(!m.matches()){
					msg="移动电话号码不正确，无法找回密码!";
					this.getFormHM().put("msg", SafeCode.encode(msg));
					return;
				}
			}
		}
		if(!flag)
		{
			String zpFld = "";
			String pwdFld="";
			String emailFld=this.getEmailField();
			String phoneFld ="";
			if("1".equals(type))
			{
				RecordVo avo = ConstantParamter.getConstantVo("SS_MOBILE_PHONE");
				if(avo!=null)
				{
					phoneFld=avo.getString("str_value");
				}
				if(phoneFld==null|| "".equals(phoneFld)|| "#".equals(phoneFld))
				{
					msg="系统未设置移动电话指标，无法找回密码!";
					this.getFormHM().put("msg", SafeCode.encode(msg));
					return;
				}
			}
			if("2".equals(type)&&(emailFld==null|| "".equals(emailFld)|| "#".equals(emailFld)))
			{
				msg="系统未设置邮件指标，无法找回密码!";
				this.getFormHM().put("msg", SafeCode.encode(msg));
				return;
			}
			RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
			if (login_vo != null)
			{
			    String login_name = login_vo.getString("str_value");
				int idx = login_name.indexOf(",");
				if (idx != -1)
				{
				    zpFld = login_name.substring(0, idx);
				    if(login_name.length()>idx)
				       pwdFld=login_name.substring(idx+1);
				}
			 }
			if ("".equals(zpFld)|| "#".equals(zpFld))
			    zpFld = "username";
			if("".equals(pwdFld)|| "#".equals(pwdFld))
			   pwdFld="userpassword";
			String[] pre = this.getPre(dao);
			for(int i=0;i<pre.length;i++)
			{
				buf.setLength(0);
				if("1".equals(type))
				{
					buf.append("select a0101,"+zpFld+","+pwdFld+","+phoneFld+" from "+pre[i]+"a01 where UPPER("+zpFld+")='"+ZE.toUpperCase()+"'");
				}
				else
				{
					buf.append("select a0101,"+zpFld+","+pwdFld+","+emailFld+" from "+pre[i]+"a01 where UPPER("+zpFld+")='"+ZE.toUpperCase()+"'");
				}
				this.frowset=dao.search(buf.toString());
				while(this.frowset.next())
				{
					flag=true;
					usernamevalue=this.frowset.getString(zpFld);
					userfullname=this.frowset.getString("a0101");
					if("2".equals(type)){
				    	email=this.frowset.getString(emailFld);
				    	email=email==null?"":email;
				    	if(email.length()<1){
							msg="无邮件地址，无法找回密码!";
							this.getFormHM().put("msg", SafeCode.encode(msg));
							return;
						}
							Matcher m = emailpattern.matcher(email);
							if(!m.matches()){
								msg="邮件地址不正确，无法找回密码!";
								this.getFormHM().put("msg", SafeCode.encode(msg));
								return;
							}
					}
					passwordvalue=this.frowset.getString(pwdFld);
					if("1".equals(type)){
		    			phone=this.frowset.getString(phoneFld);
		    			phone=phone==null?"":phone;
		    			if(phone.length()<1){
							msg="无移动电话号码，无法找回密码!";
							this.getFormHM().put("msg", SafeCode.encode(msg));
							return;
						}
		    			Matcher m = phonepattern.matcher(phone);
						if(!m.matches()){
							msg="移动电话号码不正确，无法找回密码!";
							this.getFormHM().put("msg", SafeCode.encode(msg));
							return;
						}
					}
				}
				if(flag)
				{
					sql="update "+pre[i]+"a01 set "+pwdFld+"='"+newpassword+"' where UPPER("+zpFld+")='"+ZE.toUpperCase()+"'";
					if(ConstantParamter.isEncPwd(this.getFrameconn()))
			        {
			    		Des des0=new Des(); 
						String newpwd=des0.EncryPwdStr(newpassword);    
						sql="update "+pre[i]+"a01 set "+pwdFld+"='"+newpwd+"' where UPPER("+zpFld+")='"+ZE.toUpperCase()+"'";
					}
					passwordvalue=newpassword;
					break;
				}
			}
		}
		
		this.email = email;
		this.phone = phone;
	
	}
	
	private void validateCanChangeSelf(String newpassword,String ZE,String type) throws Exception{

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String email="";
		String phone="";
		String zpFld = "";
		String pwdFld="";
		String emailFld=this.getEmailField();
		String phoneFld="";
		if("1".equals(type))
		{
     		RecordVo avo = ConstantParamter.getConstantVo("SS_MOBILE_PHONE");
         		if(avo!=null)
    		{
    			phoneFld=avo.getString("str_value");
    		}
    		if(phoneFld==null|| "".equals(phoneFld)|| "#".equals(phoneFld))
    		{
	    		msg="系统未设置移动电话指标，无法找回密码!";
	    		this.getFormHM().put("msg", SafeCode.encode(msg));
	       		return;
	    	}
		}
		if("2".equals(type)&&(emailFld==null|| "".equals(emailFld)|| "#".equals(emailFld)))
		{
			msg="系统未设置邮件指标，无法找回密码!";
			this.getFormHM().put("msg", SafeCode.encode(msg));
			return;
		}
		RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
		if (login_vo != null)
		{
		    String login_name = login_vo.getString("str_value");
			int idx = login_name.indexOf(",");
			if (idx != -1)
			{
			    zpFld = login_name.substring(0, idx);
			    if(login_name.length()>idx)
			       pwdFld=login_name.substring(idx+1);
			}
		 }
		if ("".equals(zpFld)|| "#".equals(zpFld))
		    zpFld = "username";
		if("".equals(pwdFld)|| "#".equals(pwdFld))
		   pwdFld="userpassword";
		String[] pre = this.getPre(dao);
		for(int i=0;i<pre.length;i++)
		{
			StringBuffer buf = new StringBuffer("");
			if("1".equals(type))
			{
				buf.append("select a0101,"+zpFld+","+pwdFld+","+phoneFld+" from "+pre[i]+"a01 where UPPER("+zpFld+")='"+ZE.toUpperCase()+"'");
			}
			else
			{
				buf.append("select a0101,"+zpFld+","+pwdFld+","+emailFld+" from "+pre[i]+"a01 where UPPER("+zpFld+")='"+ZE.toUpperCase()+"'");
			}
			this.frowset=dao.search(buf.toString());
			while(this.frowset.next())
			{
				flag=true;
				usernamevalue=this.frowset.getString(zpFld);
				userfullname=this.frowset.getString("a0101");
				if("2".equals(type)){
	     			email=this.frowset.getString(emailFld);
	     			email=email==null?"":email;
	     			if(email.length()<1){
						msg="无邮件地址，无法找回密码!";
						this.getFormHM().put("msg", SafeCode.encode(msg));
						return;
					}
	     			Matcher m = emailpattern.matcher(email);
					if(!m.matches()){
						msg="邮件地址不正确，无法找回密码!";
						this.getFormHM().put("msg", SafeCode.encode(msg));
						return;
					}
				}
				passwordvalue=newpassword;
				if("1".equals(type)){
		    		phone=this.frowset.getString(phoneFld);
		    		phone=phone==null?"":phone;
		    		if(phone.length()<1){
						msg="无移动电话号码，无法找回密码!";
						this.getFormHM().put("msg", SafeCode.encode(msg));
						return;
					}
		    		Matcher m = phonepattern.matcher(phone);
					if(!m.matches()){
						msg="移动电话号码不正确，无法找回密码!";
						this.getFormHM().put("msg", SafeCode.encode(msg));
						return;
					}
				}
			}
			if(flag)
			{
				sql="update "+pre[i]+"a01 set "+pwdFld+"='"+newpassword+"' where UPPER("+zpFld+")='"+ZE.toUpperCase()+"'";
				if(ConstantParamter.isEncPwd(this.getFrameconn()))
		        {
		    		Des des0=new Des(); 
					String newpwd=des0.EncryPwdStr(newpassword);    
					sql="update "+pre[i]+"a01 set "+pwdFld+"='"+newpwd+"' where UPPER("+zpFld+")='"+ZE.toUpperCase()+"'";
				}
				passwordvalue=newpassword;
				break;

			}
		}
		if(!flag)
		{
			StringBuffer buf = new StringBuffer("");
			sql="update operuser set password='"+newpassword+"' where UPPER(username)='"+ZE.toUpperCase()+"'";
			if(ConstantParamter.isEncPwd(this.getFrameconn()))
	        {
	    		Des des0=new Des(); 
				String newpwd=des0.EncryPwdStr(newpassword);    
				sql="update operuser set password='"+newpwd+"' where UPPER(username)='"+ZE.toUpperCase()+"'";
			}
			buf.append("select email,username,fullname,password,phone from operuser where UPPER(username)='"+ZE.toUpperCase()+"'");
			this.frowset=dao.search(buf.toString());
			while(this.frowset.next())
			{
				flag=true;
				usernamevalue=this.frowset.getString("username");
				userfullname=this.frowset.getString("fullname");
				email=this.frowset.getString("email");
				email=email==null?"":email;
				if("2".equals(type)&&email.length()<1){
					msg="无邮件地址，无法找回密码!";
					this.getFormHM().put("msg", SafeCode.encode(msg));
					return;
				}
				if("2".equals(type)){
					Matcher m = emailpattern.matcher(email);
					if(!m.matches()){
						msg="邮件地址不正确，无法找回密码!";
						this.getFormHM().put("msg", SafeCode.encode(msg));
						return;
					}
				}
				passwordvalue=newpassword;
				phone=this.frowset.getString("phone");
				phone=phone==null?"":phone;
				if("1".equals(type)&&phone.length()<1){
					msg="无移动电话号码，无法找回密码!";
					this.getFormHM().put("msg", SafeCode.encode(msg));
					return;
				}
				if("1".equals(type)){
					Matcher m = phonepattern.matcher(phone);
					if(!m.matches()){
						msg="移动电话号码不正确，无法找回密码!";
						this.getFormHM().put("msg", SafeCode.encode(msg));
						return;
					}
				}
			}
		}
		
		this.email = email;
		this.phone = phone;
	
	}
	
	private String getRealUsername(String username,String classPath){
		try{
			Class funcClass = Class.forName(classPath);
			Object func = funcClass.newInstance();
			Method getRealUsername = funcClass.getMethod("getRealUsername",String.class);
			username = (String)getRealUsername.invoke(func,username);
	    }catch(Exception e){
	    	   e.printStackTrace();
	    }
		return username;
	}
}
