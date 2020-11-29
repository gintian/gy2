package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.infor.CleanPersonSetting;
import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hjsj.hrms.businessobject.sys.SysParamConstant;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.Des;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.StringTokenizer;
/**
 * <p>Title:SaveUserAccountTrans</p>
 * <p>Description:保存用户分配的账号信息</p>
 * <p>Company:hjsj</p>
 * <p>create time:May 30, 2005:10:07:40 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SaveUserAccountTrans extends IBusiness {
    
    /**
     * 分析录入的用户名在系统中是否存在
     * @param username ,用户指定的登录用户名称
     * @param username_value 　用户值
     * @param conn
     * @return
     */
    private boolean IsExists(String username,String username_value,Connection conn)
    {
        String dbpre=null;
        /**如果未填，则清空以前用户定义的登录帐号*/
        if(username_value==null|| "".equals(username_value.trim()))
        	return false;
        RecordVo vo =ConstantParamter.getConstantVo("SS_LOGIN",conn);
        if(vo==null)
            dbpre="usr,";
        /**
         * 登录主集usrA01/retA01/....
         */
        StringBuffer strsql=new StringBuffer();
        boolean bflag=false;        
        dbpre = vo.getString("str_value");
        /**default usra01*/
        if(dbpre==null|| "".equals(dbpre))
            dbpre="usr,";
        StringTokenizer st = new StringTokenizer(dbpre, ",");
        while (st.hasMoreTokens())
        {
          String pre=st.nextToken().trim();
          strsql.append("select a0100 from ");
          strsql.append(pre);
          strsql.append("A01 ");
          strsql.append(" where ");
          strsql.append(username);
          strsql.append("='");
          strsql.append(username_value);
          strsql.append("' UNION ");
        }        
        strsql.append(" select username from operuser where username='");
        strsql.append(username_value);
        strsql.append("'");
        ContentDAO dao=new ContentDAO(conn);
        try
        {
             this.frowset=dao.search(strsql.toString());
             if(this.frowset.next())
             {
                 bflag=true;
             }
             //return bflag;                
        }
        catch(Exception sqle)
        {
            sqle.printStackTrace();
        }
        return bflag;
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
    
    private String getFirstDbase(ArrayList dblist)
    {
    	CommonData vo=(CommonData)dblist.get(0);
    	return vo.getDataValue();
    } 
    
    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        RecordVo vo=(RecordVo)this.getFormHM().get("user_vo");
        String dbpre=(String)this.getFormHM().get("dbpre");    
        String ip_addr=(String)this.getFormHM().get("ip_addr");
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
            String username_value=vo.getString("username").trim(); 
            username_value = username_value.replaceAll("-", "_");
            vo.setString("username", username_value);
            String pwd_ok=vo.getString("state");
            pwd = PubFunc.keyWord_reback(pwd);
            pwd = pwd==null?"":pwd.trim();
            pwd_ok = PubFunc.keyWord_reback(pwd_ok);
            pwd_ok = pwd_ok==null?"":pwd_ok.trim();
            cat.debug("user_vo="+vo.toString());            
            /**分析用户名和密码是否存在特殊字符*/
            UserObjectBo userbo=new UserObjectBo(this.getFrameconn());
             
            userbo.validateUserNamePwdComma(pwd);
            userbo.validateUserNamePwdComma(pwd_ok);
            String ctrlvalue="`=[];'\\,./·【】；‘’、，。、~!@#$%^&*()+{}:\"|<>?！￥……（）：“”《》？";
            if(userbo.validateUserNamePwd(username_value)/*||userbo.validateUserNamePwd(pwd)*/)
            {
      	      throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("error.user.usernameerror")+ctrlvalue,"",""));
            }
           /* if(!userbo.validatePassword(pwd))
            {
            	throw new GeneralException(ResourceFactory.getProperty("error.password.validate"));
            } */   
          //现对密码复杂度进行0低|1中|2强三种模式划分  xuj update 2013-5-29 有账号才检查密码规则
            if(username_value!=null && username_value.length()>0)
            		userbo.validatePasswordNew(pwd);
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
            String ip_addr_value="";
            if(ip_addr!=null&&ip_addr.length()>0)
            {
            	ip_addr_value=vo.getString(ip_addr);
            	if(IsExistsIP(ip_addr,ip_addr_value,this.getFrameconn()))
            		throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("error.ip.exist"),"",""));;
            }
            	
            if(IsExists(username,username_value,this.getFrameconn()))
    	      throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("error.user.exist"),"",""));
            
            
            //上面的判断为false说明新账号不存在，则需根据a0100来同步用户在t_sys_table_portal中的信息
            if(!CleanPersonSetting.cleanByA0100(vo.getString("a0100"), dbpre,vo.getString("username")))//add by xiegh on 20170915 修改自助用户的登陆账号，同步删除用户面板信息
        		throw GeneralExceptionHandler.Handle(new Exception("同步人员面板信息错误！"));
            
            user_vo.setString("a0100",vo.getString("a0100"));
            String login_history_pwd = SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.LOGIN_HISTORY_PWD);
	        if("1".equals(SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.LOGIN_FIRST_CHANG_PWD))||SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.PASSWORD_LOCK_DAYS).length()>0||!(login_history_pwd==null||login_history_pwd.length()==0||"0".equals(login_history_pwd))){
            	RecordVo old_vo=dao.findByPrimaryKey(user_vo);
            	String oldusername= old_vo.getString(username);
            	userbo.updateUserName(vo.getString("username"), oldusername);
            }
	        String tmpname= PubFunc.keyWord_reback(vo.getString("username"));
	        tmpname = tmpname==null?"":tmpname.trim();
            user_vo.setString(username,tmpname);
            user_vo.setString(password,pwd);
            if(ip_addr!=null&&ip_addr.length()>0)
            {
            	if(ip_addr_value!=null&&ip_addr_value.length()>0)            	
            	  user_vo.setString(ip_addr,ip_addr_value);
            }
            cat.debug("user_vo="+user_vo.toString());
            dao.updateValueObject(user_vo);
            
            this.getFormHM().put("@eventlog", ResourceFactory.getProperty("log.account.set").replace("{0}", vo.getString("a0101")));
        }
        catch(Exception ee)
        {
            ee.printStackTrace();
   	     	throw GeneralExceptionHandler.Handle(ee);                
        }
        /*****用户ip******/
    }
    private boolean IsExistsIP(String ip_addr,String ip_addr_value,Connection conn)
    {
    	boolean isCorrect=false;
    	VersionControl versionControl=new VersionControl();
    	if(!versionControl.searchFunctionId("0B4"))
    		return false;
    	if(ip_addr_value==null||ip_addr_value.length()<=0)
            return false; 
    	RecordVo vo =ConstantParamter.getConstantVo("SS_LOGIN",conn);
    	String dbpre=null;
        if(vo==null)
            dbpre="usr,";
        /**
         * 登录主集usrA01/retA01/....
         */
        StringBuffer strsql=new StringBuffer();               
        dbpre = vo.getString("str_value");
        /**default usra01*/
        if(dbpre==null|| "".equals(dbpre))
            dbpre="usr,";
        StringTokenizer st = new StringTokenizer(dbpre, ",");
        while (st.hasMoreTokens())
        {
          String pre=st.nextToken().trim();
          strsql.append("select a0100 from ");
          strsql.append(pre);
          strsql.append("A01 ");
          strsql.append(" where ");
          strsql.append(ip_addr);
          strsql.append("='");
          strsql.append(ip_addr_value+"'");
          strsql.append(" UNION ");
        }    
        strsql.setLength(strsql.length()-7);
        ContentDAO dao=new ContentDAO(conn);
        try
        {
             this.frowset=dao.search(strsql.toString());
             if(this.frowset.next())
             {
            	 isCorrect=true;
             }
             //return bflag;                
        }
        catch(Exception sqle)
        {
            sqle.printStackTrace();
        }
    	return isCorrect;
    }
}
