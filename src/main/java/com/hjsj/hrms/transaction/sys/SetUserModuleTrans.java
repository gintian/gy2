/**
 * 
 */
package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.Des;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * <p>Title:SetUserModuleTrans</p>
 * <p>Description:交易类</p> 
 * <p>Company:hjsj</p> 
 * create time at:Jul 18, 200611:13:36 AM
 * @author chenmengqing
 * @version 4.0
 */
public class SetUserModuleTrans extends IBusiness {
	private EncryptLockClient lockclient;
	/**薪资、保险、绩效模块CS|BS标识*/ //默认改为bs guodd 2018-08-30
	private String flag="bs";
	/**
	 * 取登录用户库的列表
	 * @return
	 * @throws Exception
	 */
    private ArrayList getDbList()throws Exception
    {
        /**登录参数表*/
        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
        String db_strs = login_vo.getString("str_value").toLowerCase();
		ArrayList dblist=this.userView.getPrivDbList();
		
		DbNameBo dbvo=new DbNameBo(this.getFrameconn());
		dblist=dbvo.getAllDbNameVoList();
		ArrayList list=new ArrayList();
		for(int i=0;i<dblist.size();i++)
		{
			RecordVo dbname=(RecordVo)dblist.get(i);
			if(db_strs.indexOf(dbname.getString("pre").toLowerCase())==-1)
				continue;
			//CommonData vo=new CommonData();			
			//vo.setDataName(dbname.getString("dbname"));
			//vo.setDataValue(dbname.getString("pre"));
			list.add(/*vo*/dbname.getString("pre"));
		}        
        return list;
    }
    /**
     * 求拥有某业务模块的用户查询串
     * @param module
     * @return
     * @throws Exception
     */
	private String getQueryString(int module)throws Exception 
	{
	  StringBuffer strsql=new StringBuffer();	
	  String name=null;	  
	  try
	  {

		strsql.append("(select username name,");	
		name=Sql_switcher.substr("module_ctrl",String.valueOf(module+1),"1");
		strsql.append(name);
		strsql.append(" ");
		strsql.append("Cx");
		strsql.append(" from operuser ");
		strsql.append(" where ");
		strsql.append(name);
		strsql.append("='1' )");
		strsql.append(" union ");

        /**登录参数表*/
        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
        String login_name="username";
        if(login_vo!=null)
        {
        	login_name = login_vo.getString("str_value");
        	int idx=login_name.indexOf(",");
        	if(idx==-1)
        		login_name="username";
        	else
        	{
        		login_name=login_name.substring(0,idx);
        		if("#".equals(login_name)|| "".equalsIgnoreCase(login_name))
        			login_name="username";
        	}
        }
        /**人员基本情况中的用户*/
		ArrayList dblist=getDbList();
		for(int i=0;i<dblist.size();i++)
		{
			String dbpre=(String)dblist.get(i);
			strsql.append("(select ");
			strsql.append(login_name);
			strsql.append(" name,");
			name=Sql_switcher.substr("groups",String.valueOf(module+1),"1");	//6去掉自助服务功能
			strsql.append(name);
			strsql.append(" Cx");
			strsql.append(" from ");
			strsql.append(dbpre);
			strsql.append("a01");
			strsql.append(" where ");
			strsql.append(name);
			strsql.append("='1'");
			strsql.append(" ) UNION ");
		}
		strsql.setLength(strsql.length()-7);	

	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
		  throw GeneralExceptionHandler.Handle(ex);
	  }
	  return strsql.toString();
	}
	
	/**
	 * 取得某模块的用户串列表
	 * @param module
	 * @param privcount
	 * @return
	 */
	private String getUsersByModule(int module,int privcount)throws GeneralException
	{
		StringBuffer strUsers=new StringBuffer();
		int i=0;
		try
		{
			String strsql=getQueryString(module);
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(strsql);
			strUsers.append(",");
			while(this.frowset.next())
			{
				if(i>privcount)
					break;
				strUsers.append(this.frowset.getString("name"));
				strUsers.append(",");
				i++;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return strUsers.toString();
	}
	/**
	 * 求得CS应用的连接字符串
	 * @return
	 */
	
	private String get_CsApp_Parameter()
	{
		StringBuffer strparameter=new StringBuffer();
		try
		{
			Des des=new Des();
		
			String value=getDecryptProperty(SystemConfig.getProperty("dbserver_addr"));
			if(value==null|| "".equals(value))
				value="#";
			else			
				value=des.EncryPwdStr(value);
			strparameter.append("\""+value+"\"");
			strparameter.append(",");
			value=getDecryptProperty(SystemConfig.getProperty("dbserver_port"));
			if(value==null|| "".equals(value))
				value="#";		
			else			
				value=des.EncryPwdStr(value);
			strparameter.append("\""+value+"\"");
			strparameter.append(",");
			value=getDecryptProperty(SystemConfig.getProperty("dbname"));
			if(value==null|| "".equals(value))
				value="#";	
			else			
				value=des.EncryPwdStr(value);
			strparameter.append("\""+value+"\"");
			strparameter.append(",");
			value=SystemConfig.getProperty("dbserver");
			if(value==null|| "".equals(value))
				value="#";
			else			
				value=des.EncryPwdStr(value);
			strparameter.append("\""+value+"\"");
			strparameter.append(",");
			value=getDecryptProperty(SystemConfig.getProperty("db_user"));
			if(value==null|| "".equals(value))
				value="#";		
			value=des.EncryPwdStr(value);

			strparameter.append("\""+value+"\"");
			strparameter.append(",");
			value=getDecryptProperty(SystemConfig.getProperty("db_user_pwd"));
			if(value==null|| "".equals(value))
				value="#";	
			else			
				value=des.EncryPwdStr(value);
			
			strparameter.append("\""+value+"\"");
			strparameter.append(",");

			value=this.userView.getUserName();
			if(value==null|| "".equals(value))
				value="#";
			//des=new Des();			
			value=des.EncryPwdStr(value);
			
			strparameter.append("\""+value+"\"");
			strparameter.append(",");
			value=this.userView.getPassWord();
			if(value==null|| "".equals(value))
				value="#";
			else
				value=des.EncryPwdStr(value);
			strparameter.append("\""+value+"\"");
			strparameter.append(",");
		    String license=lockclient.getLicenseCount();
		    int version=userView.getVersion();
		    if(license==null|| "0".equals(license)|| "".equals(license))
		    {
		    	strparameter.append(version+100);
		    }
		    else
		    {	 strparameter.append(version);	
		    
		    }
		    
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return strparameter.toString();
	}
	
	/**
	 * 检查属性是否加密，加密的话返回解密后内容。
	 * @param proValue： 属性值，第一个字符是@表示为加密串
	 * @return： 返回结果
	 */
	private String getDecryptProperty(String proValue) {
	    if(proValue==null || proValue.length()==1 || proValue.charAt(0)!='@') {
	    	return proValue;
	    }else{
	    	Des des=new Des();
	    	return des.DecryPwdStr(proValue.substring(1));
	    }
    }

	public void execute() throws GeneralException {
		 int module=0;
		 HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		 String str_module="";
		 
		 //在mainpanel.jsp页面判断业务用户和自助用户
		 String unit="";
		 int userType = this.userView.getStatus();//判断是业务用户还是自助用户。如果是4则是自助用户,0是业务用户。
		 if(userType==4){//如果是自助用户
			 unit="4";
		 }else if(userType==0){//如果是业务用户，先看操作单位。如果没有，则看管理范围
			 unit="0";
		 }
		 if(!"".equals(unit)){
			 this.getFormHM().put("unit", unit);
		 }
		 
		 
		 
		 try
		 {	
			 this.getCodeItem();
			 if(hm!=null)
			 {
				 /**HJ-eHR5.0新版本授权*/
				 String sign=(String)hm.get("sign");
				 this.getFormHM().put("sign",sign);
				 str_module=(String)this.getFormHM().get("module");	
				 /*
				  * 默认改为bs，当request中cs_module参数时，flag改为cs guodd 2018-08-30
				 flag=(String)hm.get("moduleflag");
				 if(flag==null||flag.equalsIgnoreCase(""))
				 	 flag="cs";
				 */
				 if(hm.containsKey("cs_module")){
					 flag = "cs";
					 hm.remove("cs_module");
				 }
				 //这两个属性的添加  郭峰   2012-1-26
				 String fromUrl = (String)this.getFormHM().get("fromUrl");
				 String fromModid = (String)this.getFormHM().get("fromModid");
				 this.getFormHM().put("fromUrl", fromUrl);
				 this.getFormHM().put("fromModid", fromModid);
				 
			 }
			 else//HJ-eHR5.0新界面的授权控制
			 {
				 str_module=String.valueOf(this.getFormHM().get("module"));
			 }

			lockclient =(EncryptLockClient)this.getFormHM().get("lock");	
			if(this.userView==null)
			{
				throw new GeneralException("会话超时,请重新登录!");
			}
            if(str_module==null|| "".equals(str_module))
            	return;
            
            //登陆进来默认打开第一个菜单，此次进入不占点。通过autoLoad参数控制 guodd 2018-08-27
	   		if(hm!=null && hm.containsKey("autoLoad")) {
				this.getFormHM().put("cs_app_str",get_CsApp_Parameter());
		        this.getFormHM().put("appDate",ConstantParamter.getAppdate(this.userView.getUserName()).replaceAll("\\.","-"));
				hm.remove("autoLoad");
				return;
			}
	   		 
    		VersionControl ver=new VersionControl();               
            /**分发的bs应用*/
            if("-1".equals(str_module))
            {
            	if(!ver.isBCurrent())
            		cs_module();
            	else
            	{
            		
            	}
            }
            else
            {
            	bs_module(str_module,ver.isBCurrent());
            }
            this.getFormHM().put("cs_app_str",get_CsApp_Parameter());
            this.getFormHM().put("appDate",ConstantParamter.getAppdate(this.userView.getUserName()).replaceAll("\\.","-"));
            
         }
		 catch(Exception ex)
		 {
			 ex.printStackTrace();
			 throw GeneralExceptionHandler.Handle(ex);
		 }	
		 //Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		 //empChangeInfo(sysbo);//我的变动信息
		 //getBrowse_photo(sysbo);//照片显示
	}
	private void cs_module() throws GeneralException {
		String[] module_ctrl=(String[])this.getFormHM().get("module_ctrl");
		//
		String name=this.userView.getUserName();
		String users=getUsersByModule(8,lockclient.getModuleCount(8)).toLowerCase();
		String tmp=","+name+",";
		if(users.indexOf(tmp.toLowerCase())!=-1)
			module_ctrl[8]="1";
		else
			module_ctrl[8]="0";			
		users=getUsersByModule(9,lockclient.getModuleCount(9)).toLowerCase();
		tmp=","+name+",";
		if(users.indexOf(tmp.toLowerCase())!=-1)
			module_ctrl[9]="1";
		else
			module_ctrl[9]="0";			
		users=getUsersByModule(10,lockclient.getModuleCount(10)).toLowerCase();
		tmp=","+name+",";
		if(users.indexOf(tmp.toLowerCase())!=-1)
			module_ctrl[10]="1";
		else
			module_ctrl[10]="0";			
		users=getUsersByModule(11,lockclient.getModuleCount(11)).toLowerCase();
		tmp=","+name+",";
		if(users.indexOf(tmp.toLowerCase())!=-1)
			module_ctrl[11]="1";
		else
			module_ctrl[11]="0";			
		users=getUsersByModule(14,lockclient.getModuleCount(14)).toLowerCase();
		tmp=","+name+",";
		if(users.indexOf(tmp.toLowerCase())!=-1)
			module_ctrl[14]="1";
		else
			module_ctrl[14]="0";			
		users=getUsersByModule(15,lockclient.getModuleCount(15)).toLowerCase();
		tmp=","+name+",";
		if(users.indexOf(tmp.toLowerCase())!=-1)
			module_ctrl[15]="1";
		else
			module_ctrl[15]="0";			
		this.getFormHM().put("module_ctrl",module_ctrl);
	}
	/**
	 * 纯bs业务权限控制
	 * @param str_module
	 * @throws GeneralException
	 */
	private void bs_module(String str_module,boolean bcurrent) throws GeneralException {
		int module;
		module=Integer.parseInt(str_module);
		try
		{
			try{//xiegh 20170704 bug:27714  lockclient.getModuleCount(module)有为空的情况 但是不能用 null==-1做判断   这个地方只能用try抓捕异常
				/**此模块不限用户数*/
				if(lockclient.getModuleCount(module)==-1)
					return;
			}catch(Exception w){
				w.printStackTrace();
				throw GeneralExceptionHandler.Handle(new Exception("请重新启动浏览器！"));
			}
		VersionControl ver=new VersionControl();
		/**用户名*/
		String name=this.userView.getUserName();
		/**业务平台*/	
		if(module>=6&&module!=22)
		{
			if(bcurrent)//并发处理
			{
				/**后台业务模块*/
				if((module>=8&&module<11)||(module>=14&&module<=17))
				{
				   if("cs".equalsIgnoreCase(flag))
				   {
						if(!lockclient.addUser(name,module))
						{
							/**5.0界面版权控制超出时直接抛异常*/
							if("hl".equalsIgnoreCase(this.userView.getBosflag()))
							{
								throw new GeneralException(ResourceFactory.getProperty("error.priv.overcount"));							
							}
							else
								this.getFormHM().put("license", "0");
						}
						else
							this.getFormHM().put("license", "1");
				   }
				   else
				   {
					    /**5.0界面版权控制超出时直接抛异常*/					   
						if(!lockclient.addUser(name,module))
							throw new GeneralException(ResourceFactory.getProperty("error.priv.overcount"));					   
				   }
				}
				else//纯BS业务模块
				{
					if(!lockclient.addUser(name,module))
						throw new GeneralException(ResourceFactory.getProperty("error.priv.overcount"));
				}
			}
			else //按帐号加密控制
			{
				String users=getUsersByModule(module,lockclient.getModuleCount(module)).toLowerCase();
				String temp=","+name+",";
				if(users.indexOf(temp.toLowerCase())==-1)
				{
					this.getFormHM().put("license", "0");					
					//throw new GeneralException(ResourceFactory.getProperty("error.priv.notover"));
				}
				else
					this.getFormHM().put("license", "1");					
			}
		}
		else//自助平台,和并发业务平台（除后台业务之外）加密控制一样
		{
			if(!lockclient.addUser(name,module))
				throw new GeneralException(ResourceFactory.getProperty("error.priv.overcount"));
		}
		}
		catch(Exception ex)
		{
			//ex.printStackTrace();
			/**授权数超过，抛异常, cmq changed at 20100206*/
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 我的变动信息
	 *
	 */
    private void empChangeInfo(Sys_Oth_Parameter sysbo)
    {

        String inputchinfor=sysbo.getValue(Sys_Oth_Parameter.INPUTCHINFOR);
			inputchinfor=inputchinfor!=null&&inputchinfor.trim().length()>0?inputchinfor:"1";
		String approveflag=sysbo.getValue(Sys_Oth_Parameter.APPROVE_FLAG);
			approveflag=approveflag!=null&&approveflag.trim().length()>0?approveflag:"1";
		this.getFormHM().put("inputchinfor", inputchinfor);
		this.getFormHM().put("approveflag", approveflag);		
    }
    private void getBrowse_photo(Sys_Oth_Parameter sysbo)
    {
    	String browse_photo=sysbo.getValue(Sys_Oth_Parameter.BROWSE_PHOTO);
		browse_photo=browse_photo!=null&&browse_photo.length()>0?browse_photo:"0";//0默认为表格信息，1照片显示
		this.getFormHM().put("browse_photo", browse_photo);
    }
    
    /**
     * 取得招聘对象类型列表
     * @throws Exception 
     */
    public void getCodeItem() throws Exception
    {
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
        RowSet rs = null;
        rs = dao.search("select * from codeitem where codesetid='35'");
        if(!rs.next())
        {
        	throw GeneralExceptionHandler.Handle(new Exception("没有设置35号代码类"));						
        }
       
    }
    
}
