/*
 * Created on 2005-11-25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.servlet.hirelogin;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.common.HireUserView;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
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
public class HireLoginAction extends Action {	
	  Connection conn;
      public ActionForward execute(ActionMapping mapping, ActionForm form,
	                               HttpServletRequest request,
	                               HttpServletResponse response){
      	HireLoginForm contractForm = (HireLoginForm) form;
        HttpSession session;
        ActionMessages actionMsgs = new ActionMessages();
        session = request.getSession();
        HireUserView hireUserView =new HireUserView();
        String username=contractForm.getUserName();
        String userpassword=contractForm.getPassWord();
        hireUserView.setUsername(username);
        String a0100=username;
        StringBuffer sql=new StringBuffer();
        
        RecordVo constandb_vo=(RecordVo)ConstantParamter.getRealConstantVo("ZP_DBNAME");
        RecordVo constantuser_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
        String usernamefield=constantuser_vo.getString("str_value");
        if(usernamefield !=null && usernamefield.indexOf(",")>0  && usernamefield.indexOf("#")==-1)
			usernamefield=usernamefield.substring(0,usernamefield.indexOf(","));
		else
			usernamefield="username";
        String userbase="";
         if(constandb_vo!=null){
        	userbase=constandb_vo.getString("str_value");
        }else{        	
          actionMsgs.add("org.apache.struts.action.GLOBAL_MESSAGE", new ActionMessage("error.hire.notsetdb"));
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
         List userlist=ExecuteSQL.executeMyQuery(sql.toString());
        
         if(!userlist.isEmpty() && userlist.size()>0){
         	 session.setAttribute(WebConstant.isLogon, new Boolean(true));             
             UserView userview=new UserView(username,userpassword,conn);            
             LazyDynaBean rec=(LazyDynaBean)userlist.get(0);
             a0100=rec.get("a0100")!=null?rec.get("a0100").toString():username;
             userview.setUserId(a0100);
             session.setAttribute(WebConstant.userView, userview);
         	return mapping.findForward("hireloginsuccess");
         }else{
            session.setAttribute(WebConstant.isLogon, new Boolean(false));
         	session.setAttribute(WebConstant.userView, null);
            actionMsgs.add("org.apache.struts.action.GLOBAL_MESSAGE",new ActionMessage("error.user.password"));
            contractForm.setMessageReturn(ResourceFactory.getProperty("error.user.password"));
            return mapping.findForward("hireloginerrors");
         }
        }catch(Exception e){
          e.printStackTrace();  
        }
        try{
        	if(conn!=null)
        		conn.close();
            }catch(Exception e){}
	    return null;
	  }	
}
