package com.hjsj.hrms.module.hire.businessobject;

import com.hjsj.hrms.businessobject.hire.AutoSendEMailBo;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class SendResetPasswordMailBo {
	private Connection conn;
	/**
	 * 构造方法
	 * @param conn
	 */
	public SendResetPasswordMailBo(Connection conn){
		this.conn=conn;
	}
	
	/**
	 * 发送重置密码链接
	 * @param emailName
	 * @param requesturl1
	 * @param newHireFlag 标识新招聘外网
	 * @return
	 * @throws GeneralException
	 */
	public String sendEmail(String emailName,String requesturl1,String newHireFlag) throws GeneralException{
		RowSet rs = null;
		RowSet rs2 = null;
		ContentDAO dao = new ContentDAO(conn);
		try {
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname="";  //应聘人员库
			if(vo!=null)
				dbname=vo.getString("str_value");
			else
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
			
			StringBuffer url = new StringBuffer(requesturl1);
			StringBuffer requesturl = new StringBuffer(requesturl1);
			String active = UUID.randomUUID().toString();
			if(StringUtils.isNotBlank(newHireFlag)) {
			    url.append("/zp.html#/resetpass?action=resetpassword");
			    requesturl.append("/zp.html#/resetpass?action=resetpassword");
			}else {
			    url.append("/module/hire/resetPassword.html?action=resetpassword");
			    requesturl.append("/module/hire/resetPassword.html?action=resetpassword");
			}
			url.append("&emailName=");
			requesturl.append("&emailName=");
			url.append(PubFunc.encrypt(emailName));
			requesturl.append(PubFunc.encrypt(emailName));
			url.append("&active=");
			requesturl.append("<br>&active=");
			url.append(active);
			requesturl.append(active);
			String sql = "select guidkey,UserName from "+dbname+"A01 where UserName=?";
			ArrayList<String> list = new ArrayList<String>();
			ArrayList<String> serlist = new ArrayList<String>();
			list.add(emailName);
			rs = dao.search(sql,list);
			String guidkey = "";
			Timestamp outDate = new Timestamp(System.currentTimeMillis());
			if(rs.next()){
				guidkey = rs.getString("guidkey");
				if((StringUtils.isEmpty(guidkey)||"".equals(guidkey))&&StringUtils.isNotEmpty(rs.getString("UserName"))){
					guidkey = UUID.randomUUID().toString();
					String updateSql = "update "+dbname+"A01 set guidkey=? where UserName=?";
					ArrayList uplist = new ArrayList();
					uplist.add(guidkey);
					uplist.add(emailName);
					dao.update(updateSql, uplist);
				}
				serlist.add(guidkey);
				String insertSql="insert into t_sys_resetpassword (guidkey,active,Activetime) values (?,?,?)";
				
				if(emailName.equals(rs.getString("UserName"))){
					ArrayList inlist = new ArrayList();
					inlist.add(guidkey);
					inlist.add(active);
					inlist.add(outDate);
					try {
						dao.update(insertSql, inlist);
					} catch (SQLException e) {
						e.printStackTrace();
						//防止库没有升级报错
						return "err";
					}
					this.send(emailName, url.toString(),requesturl.toString());
					return "success";
				}
			}else{
				return "no_account";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception("系统邮件服务器配置或网络连接不正确,请联系系统管理员!"));
		}finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(rs2);
		}
		return "err";
	}
	
	public void send(String emailName,String requesturl,String requesturl2) throws GeneralException{
		EMailBo emb=null;
 	    try
	    {
 		   emb= new EMailBo(this.conn,true,"");
 		   AutoSendEMailBo autoSendEMailBo = new AutoSendEMailBo(this.conn);
 		   String from_addr=autoSendEMailBo.getFromAddr();
 		   String title="忘记密码";
 		   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
 		   StringBuffer context = new StringBuffer();
 		   Calendar calendar = Calendar.getInstance(); //发送激活邮件的时间
 		   SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
 		   String activeDate =  format1.format(calendar.getTime());
 		   activeDate=PubFunc.encryption(activeDate);
 		   context.append("<p><h3><B>安全提示:</B></h3></p>");
 		   context.append("<h5>为保障您的帐户安全，请在2小时内点击该链接，重置您的密码：</h5><br>");
 		   context.append("<a style='color:#1aa3ff' href=\""+requesturl+"\"");
 		   context.append(" target=\"\"_blank\">重置密码</a>");
 		   emb.sendEmail(title,context.toString(),"",from_addr,emailName);
	    }
	    catch(Exception e)
	    {
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(new Exception("系统邮件服务器配置或网络连接不正确,请联系系统管理员!"));
	    }
 	    
	}

}
