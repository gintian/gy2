package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.Des;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:SaveNewPasswordTrans</p>
 * <p>Description:口令重置</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 23, 2005:11:56:59 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class UpdatePasswordTrans extends IBusiness {

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
    	
    	HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
    	if(hm==null){
	    		String msg = "ok";
	    		
	    		//进行身份验证
	    		String userP = (String)this.getFormHM().get("userP");
	    		String user_name = (String)this.getFormHM().get("user_name");
	        String oldpwd=(String)this.getFormHM().get("oldpwd");
	        String newpwd=(String)this.getFormHM().get("newpwd");
	        String newokpwd=(String)this.getFormHM().get("newokpwd");
	        
	        oldpwd = PubFunc.keyWord_reback(oldpwd);
	        oldpwd = oldpwd==null?"":oldpwd.trim();
	        newpwd = PubFunc.keyWord_reback(newpwd);
	        newpwd = newpwd==null?"":newpwd.trim();
	        newokpwd = PubFunc.keyWord_reback(newokpwd);
	        newokpwd = newokpwd==null?"":newokpwd.trim();
	    		ContentDAO dao=new ContentDAO(this.getFrameconn());
	    		try{
		    		
	    			/* 53555 用户可以分级新建其他用户、给其他用户授权，但是不能修改密码。此处取消改密码用户分级管理限制 guodd 2019-09-18
		    		if(!this.userView.isSuper_admin()){
		    			String sql = "select '1' c from operuser where username=? and groupid=? ";
		    			ArrayList values = new ArrayList();
		    			values.add(com.hrms.frame.codec.SafeCode.decode(user_name));
		    			values.add(this.userView.getGroupId());
		    			this.frowset = dao.search(sql, values);
		    			if(!this.frowset.next())
		    				throw new Exception("操作超出权限！");
		    		}
	    			*/
		        
		        /**分析用户名和密码是否存在特殊字符*/
		        UserObjectBo userbo=new UserObjectBo(this.getFrameconn());
		       /* if(!userbo.validatePassword(newokpwd))
		        {
		        	throw new GeneralException(ResourceFactory.getProperty("error.password.validate"));
		        } */   
		        userbo.validateUserNamePwdComma(newokpwd);
		        userbo.validateUserNamePwdComma(newpwd);
		        userbo.validateUserNamePwdComma(oldpwd);
		      //现对密码复杂度进行0低|1中|2强三种模式划分  xuj update 2013-5-29
				userbo.validatePasswordNew(newokpwd);
				if(Sql_switcher.searchDbServer()==1){
			        if(!oldpwd.equalsIgnoreCase(userP.replaceAll("''", "'")))
			        {
			    	    throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.sys.password"), "",""));              
			        }
				}else{
					if(!oldpwd.equals(userP.replaceAll("''", "'")))
			        {
			    	    throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.sys.password"), "",""));              
			        }
				}
				if(Sql_switcher.searchDbServer()==1){
			        if(!newpwd.equalsIgnoreCase(newokpwd))
			        {
			    	    throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.sys.newpassword"), "",""));              
			        }
				}else{
					if(!newpwd.equals(newokpwd))
			        {
			    	    throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.sys.newpassword"), "",""));              
			        }
				}
		        /**口令加密*/
		        if(ConstantParamter.isEncPwd(this.getFrameconn()))
		        {
		    		Des des0=new Des(); 
					newpwd=des0.EncryPwdStr(newpwd);        	
		        }
		        String tablename=null;
	        
		        /**平台用户*/
		        if(userView.getStatus()==0)
		        {
		            tablename="operuser";
		            RecordVo vo=new RecordVo(tablename);
		            vo.setString("password",newpwd);
		            vo.setString("username",com.hrms.frame.codec.SafeCode.decode(user_name));
					vo.setDate("modtime",DateStyle.getSystemTime());	            
		            dao.updateValueObject(vo);
		            
		            userbo.change2firstPwd("'"+com.hrms.frame.codec.SafeCode.decode(user_name)+"'");
		            
		        }
		        /**自助用户*/
		        /*if(userView.getStatus()==4)
		        {
		            //登录参数表,登录用户指定不是username or userpassword
		            String username=null;
		            String password=null;
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
		                    if(username.equals("#")||username.equals(""))
		                    	username="username";
		                    password=login_name.substring(idx+1);  
		                    if(password.equals("#")||password.equals(""))
		                    	password="userpassword";
		                }
		            }
		            String dbpre=userView.getDbname();
		            tablename=dbpre+"A01";
		            RecordVo vo=new RecordVo(tablename);
		            vo.setString(password,newpwd);
		            vo.setString("a0100",userView.getUserId());
		            ContentDAO dao=new ContentDAO(this.getFrameconn());
		            dao.updateValueObject(vo);            
		        } */  
        
        	
	        }catch(Exception eee)
	        {
	        		msg="error";
	            eee.printStackTrace();
	    	    		throw GeneralExceptionHandler.Handle(eee);              
	        }finally{
		        	this.getFormHM().clear();
		        	this.getFormHM().put("msg", msg);
		        	this.getFormHM().put("@eventlog", userView.getUserName()+"修改"+user_name+"密码");
	        }
    	}else{
    		String username=(String)hm.get("user_name");
    		username = com.hrms.frame.codec.SafeCode.decode(username);
    		if(username==null|| "".equals(username))
    			return;
    		ContentDAO dao=new ContentDAO(this.getFrameconn());
    		try
    		{
    			RecordVo vo=new RecordVo("operuser");
    			vo.setString("username",username);
    			vo=dao.findByPrimaryKey(vo);
    			String oldpwd="";
    			if(ConstantParamter.isEncPwd(this.getFrameconn()))
    			{
    				Des des=new Des();
    				oldpwd=des.DecryPwdStr(vo.getString("password"));
    			}
    			
    			this.getFormHM().clear();
    			this.getFormHM().put("oldpwd",oldpwd);
    		}
    		catch(Exception ex)
    		{
    			ex.printStackTrace();
    			throw GeneralExceptionHandler.Handle(ex);
    		}
        }
    }

}
