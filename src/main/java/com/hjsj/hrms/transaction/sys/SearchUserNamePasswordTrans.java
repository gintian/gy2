package com.hjsj.hrms.transaction.sys;

import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:SearchUserNamePasswordTrans</p>
 * <p>Description:从参数中查询登录的用户及口令</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 28, 2005:10:04:54 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SearchUserNamePasswordTrans extends IBusiness {

    /**
     * 
     */
    public SearchUserNamePasswordTrans() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
    	
    	/**判断是否需要口令加密 口令解密 依据(constant表中是否存在 EncryPwd 字段)*/
    	boolean b = ConstantParamter.isEncPwd(this.getFrameconn());
        if(b){
        	this.getFormHM().put("flag","show");
        }else{
        	this.getFormHM().put("flag","hidden");
        }
         
        /**登录参数表*/
        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
        if(login_vo==null)
        	return;
        String login_name = login_vo.getString("str_value");
        int idx=login_name.indexOf(",");
        if(idx==-1)
        {
            this.getFormHM().put("username","");
            this.getFormHM().put("password","");
            return;
        }
        String username=login_name.substring(0,idx);
        String password=login_name.substring(idx+1);
        cat.debug("username="+username);
        cat.debug("password="+password);
        this.getFormHM().put("username",username);
        this.getFormHM().put("password",password);   
        /**********ip地址*************/
        RecordVo ip_vo=ConstantParamter.getConstantVo("SS_BIND_IPADDR");
        String ip_addr="";
        if(ip_vo!=null)
        {
        	ip_addr= ip_vo.getString("str_value");            
        }        
        this.getFormHM().put("ip_addr",ip_addr);
        
        /**系统登录参数  SYS_LOGIN_LOCK 登录用户锁定指标，  2013-5-30 add xuj
         */
        
        RecordVo vo=ConstantParamter.getConstantVo("SS_LOGIN_LOCK_FIELD");
        String lockfield="";
        if(vo!=null)
        {
        	lockfield= vo.getString("str_value");            
        }        
        this.getFormHM().put("lockfield",lockfield);
    }

}
