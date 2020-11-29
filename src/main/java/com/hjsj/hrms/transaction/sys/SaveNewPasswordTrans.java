package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hjsj.hrms.businessobject.sys.SysParamConstant;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.AesEncryptUtil;
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
import com.hrms.struts.valueobject.UserView;

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
public class SaveNewPasswordTrans extends IBusiness {

    /**
     * 
     */
    public SaveNewPasswordTrans() {
        super();
    }

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        String oldpwd=(String)this.getFormHM().get("oldpwd");
        String newpwd=(String)this.getFormHM().get("newpwd");
        String newokpwd=(String)this.getFormHM().get("newokpwd");
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
        String firstpwd =null;
        if(hm!=null){
        	firstpwd = (String)hm.get("b_first_save");
        	if(firstpwd==null)
        		firstpwd = (String)hm.get("b_first_save_b");
        	if(firstpwd==null)
        		firstpwd = (String)hm.get("b_first_save_he");
        	if(firstpwd==null)
        		firstpwd = (String)hm.get("b_first_save_hc");
        	if(firstpwd==null)
        		firstpwd = (String)hm.get("b_first_save_i");
        	hm.remove("b_first_save");
        	hm.remove("b_first_save_b");
        	hm.remove("b_first_save_he");
        	hm.remove("b_first_save_hc");
        	hm.remove("b_first_save_i");
        }
        /*oldpwd = PubFunc.keyWord_reback(oldpwd);
        oldpwd = oldpwd==null?"":oldpwd.trim();
        newpwd = PubFunc.keyWord_reback(newpwd);
        newpwd = newpwd==null?"":newpwd.trim();
        newokpwd = PubFunc.keyWord_reback(newokpwd);
        newokpwd = newokpwd==null?"":newokpwd.trim();
        */
        
        /*为了密码传输安全，前端对密码进行了加密，此处解密。 guodd 2018-09-28*/
        oldpwd = PubFunc.keyWord_reback(oldpwd);
        oldpwd = oldpwd==null?"":AesEncryptUtil.aesDecryptStrict(oldpwd).trim();
        newpwd = PubFunc.keyWord_reback(newpwd);
        newpwd = newpwd==null?"":AesEncryptUtil.aesDecryptStrict(newpwd).trim();
        newokpwd = PubFunc.keyWord_reback(newokpwd);
        newokpwd = newokpwd==null?"":AesEncryptUtil.aesDecryptStrict(newokpwd).trim();
        
        if(oldpwd == null || newpwd==null || newokpwd==null)
        	throw GeneralExceptionHandler.Handle(new Exception("数据不合法，操作终止。"));

		//检查userview
		UserView userView = checkUserView();

        /**分析用户名和密码是否存在特殊字符*/
        UserObjectBo userbo=new UserObjectBo(this.getFrameconn());
        /*if(!userbo.validatePassword(newokpwd))
        {
        	throw new GeneralException(ResourceFactory.getProperty("error.password.validate"));
        }*/
        userbo.validateUserNamePwdComma(newokpwd);
        userbo.validateUserNamePwdComma(newpwd);
        userbo.validateUserNamePwdComma(oldpwd);
      //现对密码复杂度进行0低|1中|2强三种模式划分  xuj update 2013-5-29
		userbo.validatePasswordNew(newokpwd);
        //cmq 20090602  去掉口令特殊字符检查
//        if(userbo.validateUserNamePwd(newokpwd))
//        {
//  	      throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("error.userpwd.validate"),"",""));
//        }    
		 if(firstpwd==null){
	        /**口令加密*/
			Des des=new Des();        
       
	        String userP=userView.getPassWord();
	        
	        if(ConstantParamter.isEncPwd(this.getFrameconn()))
	        {
	        	userP=des.DecryPwdStr(userP.replaceAll("''", "'"));
	        } else{
	        	userP=userP.replaceAll("''", "'");
	        }

			 if(!oldpwd.equals(userP))
			 {
				 throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.sys.password"), "",""));
			 }
			 /* 密码匹配需要区分大小写 guodd 2020-01-16
	        if(Sql_switcher.searchDbServer()==1){
		        if(!oldpwd.equalsIgnoreCase(userP))
		        {
		    	    throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.sys.password"), "",""));              
		        }
	        }else{
	        	if(!oldpwd.equals(userP))
		        {
		    	    throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.sys.password"), "",""));              
		        }
	        }
			  */
        }else{
        	
			String userkey = (String)hm.get("userkey");
        	if(userkey==null)
        		throw GeneralExceptionHandler.Handle(new Exception("数据不合法，操作终止。"));
        	userkey = PubFunc.keyWord_reback(userkey);
        	userkey = AesEncryptUtil.aesDecrypt(userkey).trim();
        	if(!userkey.equals(userView.getUserName()) && !userkey.equals(this.getUserView().getUserName()))
        		throw GeneralExceptionHandler.Handle(new Exception("数据不合法，操作终止。"));
    		
        	/**口令加密*/
			Des des=new Des();  
			String _newpwd = "";
	        
	        String userP=userView.getPassWord();
	        
	        if(ConstantParamter.isEncPwd(this.getFrameconn()))
	        {
	        	_newpwd=des.DecryPwdStr(userP.replaceAll("''", "'"));
	        }else{
	        	_newpwd=userP.replaceAll("''", "'");
	        }
	        if(Sql_switcher.searchDbServer()==1){
	        	if(_newpwd.equalsIgnoreCase(newpwd))
		        {
		    	    throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("error.password.last.same"), "",""));              
		        }
	        }else{
	        	if(_newpwd.equals(newpwd))
		        {
		    	    throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("error.password.last.same"), "",""));              
		        }
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
        if(userbo.checkHistoryPwd(newpwd, userView.getUserName())){
        	 throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("error.password.history.same").replace("{0}", SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.LOGIN_HISTORY_PWD)), "",""));              
        }
        
        /**口令加密*/
        if(ConstantParamter.isEncPwd(this.getFrameconn()))
        {
    		Des des0=new Des(); 
			newpwd=des0.EncryPwdStr(newpwd);        	
        }
        userView.setPassWord(newpwd.replaceAll("'", "''"));
        String tablename=null;
        try
        {
	        /**平台用户*/
	        if(userView.getStatus()==0)
	        {
	            tablename="operuser";
	            RecordVo vo=new RecordVo(tablename);
	            vo.setString("password",newpwd);
	            vo.setString("username",userView.getUserId());
				vo.setDate("modtime",DateStyle.getSystemTime());	            
	            ContentDAO dao=new ContentDAO(this.getFrameconn());
	            dao.updateValueObject(vo);
	        }
	        /**自助用户*/
	        if(userView.getStatus()==4)
	        {
	            /**登录参数表,登录用户指定不是username or userpassword*/
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
	                    if("#".equals(username)|| "".equals(username))
	                    	username="username";
	                    password=login_name.substring(idx+1);  
	                    if("#".equals(password)|| "".equals(password))
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
	        }
	        
	        //处理历史密码、首次密码修改 xuj add 2013-10-9
	        
	        if(ConstantParamter.isEncPwd(this.getFrameconn()))
	        {
	    		Des des0=new Des(); 
				newpwd=des0.DecryPwdStr(newpwd);        	
	        }
	        userbo.doHistoryPwd(newpwd,userView.getUserName());
        }
        catch(Exception eee)
        {
            eee.printStackTrace();
    	    throw GeneralExceptionHandler.Handle(eee);              
        }
    }

	/**
	 * 检查userview
	 * 当设置了登陆验证类时（system.properties中设置了logonclass），自助用户登录时会自动转成关联的业务用户身份登录
	 * 修改密码时，程序需要判断一下用户到底想要修改的是自助的密码，还是业务用户的密码。
	 * 此方法用户获取业务用户关联的自助用户的userview
	 * @return
	 */
	private UserView checkUserView(){
		UserView uv = this.userView;
		try{
			if(this.userView.getHm().containsKey("real_login_username")){
				UserView newuv = new UserView(this.userView.getHm().get("real_login_username").toString(),this.frameconn);
				if(newuv.canLogin()){
					return newuv;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return uv;
	}
}
