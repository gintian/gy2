package com.hjsj.hrms.transaction.sys.options;

import com.hjsj.hrms.businessobject.sys.Configuration;
import com.hjsj.hrms.businessobject.sys.options.JWhichUtil;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchAdauthenticatesetTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		/*try{
			 RecordVo constant_vo=ConstantParamter.getRealConstantVo("ADPARAMETER",this.getFrameconn());     
			 if(constant_vo!=null)
			 {
				String ad=constant_vo.getString("str_value"); 
				if(ad!=null && ad.length()>0)
				{
					StringTokenizer token=new StringTokenizer(ad,"|");
					if(token.hasMoreTokens())
						this.getFormHM().put("host",token.nextToken());
					if(token.hasMoreTokens())
						this.getFormHM().put("port",token.nextToken());
					if(token.hasMoreTokens())
						this.getFormHM().put("domain_name",token.nextToken());
					if(token.hasMoreTokens())
						this.getFormHM().put("domain",token.nextToken());
					if(token.hasMoreTokens())
						this.getFormHM().put("username",token.nextToken());
					if(token.hasMoreTokens())
					{
						String pw=token.nextToken();
						if(pw==null || (pw!=null && pw.equals("null")))
							pw="";
						this.getFormHM().put("userpassword",pw);
					}
				}
			 }else
			 {
				 String ad="127.0.0.1|389|domain|com|administrator||Users|simple|1";
				 RecordVo  vo=new RecordVo("constant");
			     vo.setString("constant","ADPARAMETER");
			     vo.setString("str_value",ad);
			     vo.setString("describe","AD认证");
			     ContentDAO dao=new ContentDAO(this.getFrameconn());
			     dao.deleteValueObject(vo);
		         dao.addValueObject(vo);
		         ConstantParamter.putConstantVo(vo,"ADPARAMETER");	
		         this.getFormHM().put("domain_name","domain");
		         this.getFormHM().put("domain","com");
		         this.getFormHM().put("host","127.0.0.1");
		         this.getFormHM().put("port","389");
		         this.getFormHM().put("username","administrator");
		         this.getFormHM().put("userpassword","null");
			 }   
		 }catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }*/
		/*String rootorg=SystemConfig.getProperty("rootorg");//DN域
		String ldaphost=SystemConfig.getProperty("ldaphost");//服务器地址
		String ldapport=SystemConfig.getProperty("ldapport");//端口
		//String initialContextFactory=SystemConfig.getProperty("initialContextFactory");
		String ldaptype=SystemConfig.getPropertyValue("ldapserver");//服务器类型
		String ldapset=SystemConfig.getPropertyValue("ldap");//服务器类型
*/		
		
		String rootPath=JWhichUtil.getResourceFilePath("system.properties");	    	
		if(rootPath==null||rootPath.length()<=0)
		    throw GeneralExceptionHandler.Handle(new GeneralException("找不到system.properties资源文件！"));
		Configuration configuration=new Configuration(rootPath);				
		String rootorg=configuration.getValue("rootorg");
		String ldapport=configuration.getValue("ldapport");
		String ldaphost=configuration.getValue("ldaphost");
		String ldapset=configuration.getValue("ldap");
		String ldaptype=configuration.getValue("ldapserver");	
		rootorg=rootorg!=null&&rootorg.length()>0?rootorg:"domain.com";
		ldaphost=ldaphost!=null&&ldaphost.length()>0?ldaphost:"ldap://ldapserver";
		ldapport=ldapport!=null&&ldapport.length()>0?ldapport:"389";
		ldaptype=ldaptype!=null&&ldaptype.length()>0?ldaptype:"AD";
		ldapset=ldapset!=null&&ldapset.length()>0?ldapset:"true";
		this.getFormHM().put("domain_name",rootorg);        
        this.getFormHM().put("host",ldaphost);
        this.getFormHM().put("port",ldapport);
        this.getFormHM().put("ldaptype",ldaptype);     
        this.getFormHM().put("ldapset",ldapset);
        
	}

}
