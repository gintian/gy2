/*
 * Created on 2005-11-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.servlet.hirelogin;

import com.hjsj.hrms.businessobject.structuresql.CommonSqlExec;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.common.HireUserView;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.util.List;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ApplyUserAccount extends Action {	
	  Connection conn;
      public ActionForward execute(ActionMapping mapping, ActionForm form,
	                               HttpServletRequest request,
	                               HttpServletResponse response){
      	HireLoginForm applyForm = (HireLoginForm) form;
        HttpSession session;
        ActionMessages actionMsgs = new ActionMessages();
        session = request.getSession();
        HireUserView hireUserView =new HireUserView();
        String username=applyForm.getUserName();
        String userpassword=applyForm.getPassWord();
        String operate="";
        if(request.getParameter("operate")!=null)
        	operate=request.getParameter("operate");
        hireUserView.setUsername(username);
        String a0100=username;
        StringBuffer sql=new StringBuffer();
        RecordVo constandb_vo=(RecordVo)ConstantParamter.getRealConstantVo("ZP_DBNAME");       
        RecordVo constantuser_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
        String usernamefield=constantuser_vo.getString("str_value");
        if(usernamefield !=null && usernamefield.indexOf(",")!=-1 && usernamefield.indexOf("#")==-1)
			usernamefield=usernamefield.substring(0,usernamefield.indexOf(","));
		else
			usernamefield="username";
         String userbase="";  
         if(constandb_vo!=null){
        	userbase=constandb_vo.getString("str_value");
        	if(userbase==null || userbase!=null && userbase.length()!=3)
        	{
        		 actionMsgs.add("org.apache.struts.action.GLOBAL_MESSAGE", new ActionMessage("error.hire.notsetdb"));
                 applyForm.setMessageReturn(ResourceFactory.getProperty("error.hire.notsetdb"));
                 return mapping.findForward("hireloginerrors");
        	}
        		
        }else{        	
          actionMsgs.add("org.apache.struts.action.GLOBAL_MESSAGE", new ActionMessage("error.hire.notsetdb"));
          applyForm.setMessageReturn(ResourceFactory.getProperty("error.hire.notsetdb"));
          return mapping.findForward("hireloginerrors");
        }
        try{
         conn=AdminDb.getConnection();      	
         sql.append("select * from ");
         sql.append(userbase);
         sql.append("A01 where ");
         sql.append(usernamefield);
         sql.append("='");
         sql.append(username);
         sql.append("' and UserPassword='");
         sql.append(userpassword);
         sql.append("'");
         List userlist=ExecuteSQL.executeMyQuery(sql.toString(),conn);
         //System.out.println("username" + username);
         if(!userlist.isEmpty())
         {
            actionMsgs.add("org.apache.struts.action.GLOBAL_MESSAGE", new ActionMessage("hire.zp_persondb.existusermessage"));
            applyForm.setMessageReturn(ResourceFactory.getProperty("hire.zp_persondb.existusermessage"));
            
            if(!"add".equals(operate))
            	return mapping.findForward("hireloginerrors");
            else
            {
            	request.setAttribute("operate",operate);
            	return mapping.findForward("hireAccountExist");
            }
         }
         else
         {
         	a0100=new CommonSqlExec().getUserId(userbase + "A01",conn);
         	sql.delete(0,sql.length());
         	sql.append("insert into ");
         	sql.append(userbase);
         	sql.append("A01(a0100,");
         	sql.append(usernamefield);
         	sql.append(",UserPassword)values('");
         	sql.append(a0100);
         	sql.append("','");
         	sql.append(username);
         	sql.append("','");
         	sql.append(userpassword);
         	sql.append("')");
         	new ExecuteSQL().execUpdate(sql.toString());
         	
         	if(!"add".equals(operate))
         	{
	         	actionMsgs.add("org.apache.struts.action.GLOBAL_MESSAGE", new ActionMessage("label.zp_person.applyaccountsuccess"));
	         	applyForm.setMessageReturn(ResourceFactory.getProperty("label.zp_person.applyaccountsuccess"));
	         	applyForm.setPassWord("");
	         	 session.setAttribute(WebConstant.isLogon, new Boolean(true));
	             UserView userview=new UserView(username,userpassword,conn);
	            userview.setUserId(a0100);
	             session.setAttribute(WebConstant.userView, userview);
	         	return mapping.findForward("hireloginsuccess");
         	}
         	else
         	{
         		UserView userview=(UserView)session.getAttribute(WebConstant.userView);
         		
         		userview.getHm().put("add_a0100",a0100);
         		userview.getHm().put("add_username",username);
         		return mapping.findForward("hireAccountSuccess");
         	}
         	
         }         
        }catch(Exception e){
          e.printStackTrace();  
        }finally
		{
        	try{
        	if(conn!=null)
        		conn.close();
            }catch(Exception e){}
        }
	    return null;
	  }	
}
