package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * 30200710250
 * <p>Title:GetPasswordTrans.java</p>
 * <p>Description>:GetPasswordTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Sep 9, 2009 5:08:25 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class GetPasswordTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=xmlBo.getAttributeValues();
			String isAttach="0";
			if(map.get("attach")!=null&&((String)map.get("attach")).length()>0)
				isAttach=(String)map.get("attach");
			EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn(),isAttach);
			String dbName=employNetPortalBo.getZpkdbName();	
			String userNameCloumn="username";
			String passWordCloumn="userpassword";
			String email=PubFunc.getReplaceStr(SafeCode.decode((String)this.getFormHM().get("email")));
			String msg = "1";
			String password="";
			String a0101="";
			StringBuffer buf = new StringBuffer("");
			buf.append("select a0101,"+passWordCloumn+" from "+dbName+"a01 where UPPER("+userNameCloumn+")='"+email.toUpperCase()+"'");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(buf.toString());
			boolean isExisit=false;
			while(this.frowset.next())
			{
				password=this.frowset.getString(passWordCloumn);
				a0101=this.frowset.getString("a0101");
				isExisit=true;
			}
			String fromAddr=this.getFromAddr();
			if(fromAddr==null|| "".equals(fromAddr.trim()))
			{
				msg="系统未设置邮件服务器!";
				this.getFormHM().put("msg", msg);
				return;
			}
			if(!isExisit)
			{
				msg="系统未找到与你输入匹配的用户，请确认输入是否正确！";
				this.getFormHM().put("msg", msg);
				return;
			}
			String why=SystemConfig.getPropertyValue("sys_name");
			if(why==null|| "".equals(why))
				why="";
			String str=why;
			StringBuffer content = new StringBuffer("");
			//content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+a0101+",您好：<br>");
			content.append(a0101+",您好：<br>");//汉口银行特意要求将前面空格去掉
			//content.append("您的"+str+"注册邮箱为："+email+",密码为:"+password+"<br>");
			content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;您的"+str+"注册邮箱为："+email+",密码为:"+password+"<br>");//汉口银行要求格式
			content.append("该邮件是为您找回密码发出，请勿回复!");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			content.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+format.format(new Date()));
			String title="用户名和密码信息";
			try
			{
				EMailBo bo=new EMailBo(this.getFrameconn(),true,"");
	        	bo.sendEmail(title,content.toString(),"",fromAddr,email);
			}
			catch(Exception e)
			{
				msg="系统邮件服务器配置不正确,请跟系统管理员联系!";
			}
	    	this.getFormHM().put("msg", msg);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
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

}
