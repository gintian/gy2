/**
 * 
 */
package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.Des;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * <p>Title:ResetLoginPasswordTrans</p>
 * <p>Description:重新设置用户登录密码,仅为有的用户忘记登录用户密码,管理员重新设</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-1-3:11:11:18</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class ResetLoginPasswordTrans extends IBusiness {

    private String getFirstDbase(ArrayList dblist)
    {
    	CommonData vo=(CommonData)dblist.get(0);
    	return vo.getDataValue();
    } 
    
    /**求得登录用户的应用库列表*/
    private ArrayList getLoginBaseList()throws GeneralException
    {
        /**登录参数表*/
        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN",this.getFrameconn());
        String A01 = login_vo.getString("str_value");
        /**系统所有存在的数据库列表usr,oth,trs,ret*/
        StringBuffer strsql=new StringBuffer();
        strsql.append("select pre,dbname from dbname");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList dblist=new ArrayList();
        try
        {
            this.frowset=dao.search(strsql.toString());
            while(this.frowset.next())
            {
                String dbpre=this.frowset.getString("pre");
                /**权限分析*/
	                if((A01.indexOf(dbpre)!=-1)&&(userView.isSuper_admin()||userView.hasTheDbName(dbpre)))
	                {
	                	CommonData vo=new CommonData(this.frowset.getString("pre"),this.frowset.getString("dbname"));
	                	dblist.add(vo);
	                }
            }
            /**认为是在职人员库*/
            if(dblist.size()==0)
            {
                CommonData vo=new CommonData("usr",ResourceFactory.getProperty("label.sys.userbase"));
                dblist.add(vo);                
            }
        }
        catch(Exception sqle)
        {
            sqle.printStackTrace();
  	      	throw GeneralExceptionHandler.Handle(sqle);                
        }
        return dblist;
    }
    
	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
        RecordVo vo=(RecordVo)this.getFormHM().get("user_vo");
        String dbpre=(String)this.getFormHM().get("dbpre"); 
        if(vo==null)
            return ;
        ArrayList dblist=getLoginBaseList();
        if(dbpre==null|| "".equals(dbpre))
        {
        	dbpre=getFirstDbase(dblist);
        }         
        /**登录参数表,登录用户指定不是username or userpassword*/
        String username=null;
        String password=null;
        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD",this.getFrameconn());
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
            }
        }
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        String tablename=dbpre+"a01";
        RecordVo user_vo=new RecordVo(tablename);
        try
        {
            String pwd=vo.getString("userpassword");
            /**分析用户名和密码是否存在特殊字符*/
            UserObjectBo userbo=new UserObjectBo(this.getFrameconn());
            /*if(!userbo.validatePassword(pwd))
            {
            	throw new GeneralException(ResourceFactory.getProperty("error.password.validate"));
            }*/ 
          //现对密码复杂度进行0低|1中|2强三种模式划分  xuj update 2013-5-29
			userbo.validatePasswordNew(pwd);
            //chenmengqing added 20090602
//            if(userbo.validateUserNamePwd(pwd))
//            {
//      	      throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("error.userpwd.validate"),"",""));
//            }    
            
            String pwd_ok=vo.getString("state");
            cat.debug("user_vo="+vo.toString());
            pwd = PubFunc.keyWord_reback(pwd);
            pwd = pwd==null?"":pwd.trim();
            pwd_ok = PubFunc.keyWord_reback(pwd_ok);
            pwd_ok = pwd_ok==null?"":pwd_ok.trim();
            userbo.validateUserNamePwdComma(pwd);
            userbo.validateUserNamePwdComma(pwd_ok);
            if(Sql_switcher.searchDbServer()==1){
	            if(!pwd.equalsIgnoreCase(pwd_ok))
	            {
	   	     	  throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty(GeneralConstant.PASSWORD_ERROR),"",""));   
	            }
            }else{
            	if(!pwd.equals(pwd_ok))
	            {
	   	     	  throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty(GeneralConstant.PASSWORD_ERROR),"",""));   
	            }
            }
			if(ConstantParamter.isEncPwd(this.getFrameconn()))
			{
				Des des=new Des();
				pwd=des.EncryPwdStr(pwd);
			}              
            user_vo.setString("a0100",vo.getString("a0100"));
            user_vo.setString(password,pwd/*vo.getString("userpassword")*/);
            cat.debug("user_vo="+user_vo.toString());
            dao.updateValueObject(user_vo);
            this.getFormHM().put("@eventlog", ResourceFactory.getProperty("log.account.repwd").replace("{0}", vo.getString("a0101")));
        
          //处理为首次密码修改 xuj add 2013-10-9
	        userbo.change2firstPwd("'"+vo.getString(username)+"'");
	        
	        //当重设密码后可自动解锁
	        userbo.unlockUser(dbpre, vo.getString("a0100"), vo.getString(username));
	        /*ArrayList values = new ArrayList();
	        ArrayList users = new ArrayList();
	        users.add(vo.getString(username));
	        values.add(users);
	        userbo.updatePWDModTime(values);*/
        }
        catch(Exception ee)
        {
            ee.printStackTrace();
   	     	throw GeneralExceptionHandler.Handle(ee);                
        }

	}	 
}
