/**   
* @Title: HeadhunterLoginNetPortalTrans.java 
* @Package com.hjsj.hrms.transaction.hire.employNetPortal 
* @Description: TODO(猎头招聘登录功能) 
* @author xucs  
* @date 2015年1月26日 上午11:39:32 
* @version V1.0   
*/ 
package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.Md5ForHire;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Calendar;

/** 
 * @ClassName: HeadhunterLoginNetPortalTrans 
 * @Description: TODO(猎头招聘登录功能) 
 * @author xucs 
 * @date 2015年1月26日 上午11:39:32 
 *  
 */
public class HeadhunterLoginNetPortalTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
			String loginName=(String)this.getFormHM().get("loginName");//登录的用户名
			String password=(String)this.getFormHM().get("password");//用户名的密码
			loginName=PubFunc.getReplaceStr(loginName);
			
			if(loginName==null||loginName.trim().length()==0)//无登录名 不准登录
				return;
			
			password=PubFunc.getReplaceStr(password);
			
			HttpSession session=(HttpSession)this.getFormHM().get("session");
			if(session.getAttribute(WebConstant.userView)!=null&&((UserView)session.getAttribute(WebConstant.userView)).getUserEmail().equalsIgnoreCase(loginName))
			 	return;
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer sql=new StringBuffer();
			sql.append("select name from zp_headhunter_login where username=? and password=?");
			ArrayList valueList = new ArrayList();
			valueList.add(loginName);
			valueList.add(password);
			String userName = "";
			this.frowset = dao.search(sql.toString(), valueList);
			while(this.frowset.next()){
				userName = this.frowset.getString(1);
			}
			
			Calendar cd=Calendar.getInstance();
			String yy=""+cd.get(Calendar.YEAR);
			String mm1=cd.get(Calendar.MONTH)+1<=9?"0"+(cd.get(cd.MONTH)+1):(cd.get(cd.MONTH)+1)+"";
			String dd=cd.get(Calendar.DATE)<=9?"0"+cd.get(Calendar.DATE):cd.get(Calendar.DATE)+"";
			String partime=yy+mm1+dd;
			String cer="";
			Md5ForHire md5 =new Md5ForHire();
			String keycode="klskuge9723kgs8772k3";
			this.getFormHM().put("loginName",loginName);//登录用户名
			this.getFormHM().put("cer",cer);
			cer=md5.getMD5((loginName+keycode+partime).getBytes());
			this.getFormHM().put("hdtusername",loginName);
			this.getFormHM().put("cer",cer);
		    this.getFormHM().put("userName",userName);//显示的用户名
			this.getFormHM().put("a0100","headHire");//猎头招聘并没有a0100所以随便加一个
			if(session!=null)//构建猎头招聘的userview
			{
			 UserView userview=new UserView(loginName,password,this.getFrameconn());
	         userview.setUserId("headHire");
	         userview.setUserEmail(loginName);
	         userview.getHm().put("isHeadhunter","1");
	        //session.setAttribute(WebConstant.isLogon, new Boolean(true));
	         session.setAttribute(WebConstant.userView, userview);
			}
			this.getFormHM().remove("password"); //20140812 基于安全考虑，避免返回信息中带有password信息
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
