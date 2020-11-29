package com.hjsj.hrms.transaction.sys.options;

import com.hjsj.hrms.businessobject.sys.options.JWhichUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.*;

public class SaveAdauthenticatesetTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String host=(String)this.getFormHM().get("host");
		//LDAP认证服务器设置，目录服务器地址需还原特殊字符 jingq add 2014.09.22
		host = PubFunc.keyWord_reback(host);
		String port=(String)this.getFormHM().get("port");
		String domain_name=(String)this.getFormHM().get("domain_name");
		String ldaptype=(String)this.getFormHM().get("ldaptype");
		String ldapset=(String)this.getFormHM().get("ldapset");
	
		/*String domain=(String)this.getFormHM().get("domain");
		String username=(String)this.getFormHM().get("username");
		String userpassword=(String)this.getFormHM().get("userpassword");
		if(userpassword==null || userpassword.trim().length()==0 || userpassword.equals("null"))
			userpassword="null";
		try{
			 String ad=host + "|" + port + "|" + domain_name + "|" + domain + "|" + username + "|" + userpassword + "|Users|simple|1";
			 RecordVo  vo=new RecordVo("constant");
		     vo.setString("constant","ADPARAMETER");
		     vo.setString("str_value",ad);
		     vo.setString("describe","AD认证");
		     ContentDAO dao=new ContentDAO(this.getFrameconn());
		     dao.deleteValueObject(vo);
	         dao.addValueObject(vo);
	         ConstantParamter.putConstantVo(vo,"ADPARAMETER");
		 }catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }*/
		
		String rootPath=JWhichUtil.getResourceFilePath("system.properties");	    	
		if(rootPath==null||rootPath.length()<=0)
		    throw GeneralExceptionHandler.Handle(new GeneralException("得到站点路径错误！"));
		/*Configuration configuration=new Configuration();
		configuration.operateConfiguration(rootPath);		
		configuration.setValue("rootorg",domain_name);
		configuration.setValue("ldapport",port);
		configuration.setValue("ldaphost",host);
		configuration.setValue("ldap",ldapset);
		configuration.setValue("ldapserver",ldaptype);		
		configuration.saveFile(rootPath, "");*/
		File file = new File(rootPath); 
        FileInputStream fis = null;
        InputStreamReader isr = null;
		BufferedReader br = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis); 
	        br = new BufferedReader(isr);
	        StringBuffer buf = new StringBuffer(); 
	        String line = "";
	        while ((line = br.readLine()) != null) { 
	        	if(line.indexOf("rootorg")!=-1)
	        		continue;
	        	if(line.indexOf("ldapport")!=-1)
	        		continue;
	        	if(line.indexOf("ldaphost")!=-1)
	        		continue;
	        	if(line.indexOf("ldap")!=-1)
	        		continue;
	        	if(line.indexOf("ldapserver")!=-1)
	        		continue;
                buf = buf.append(line); 
                buf = buf.append("\n\r"); 
            } 
	        buf.append("rootorg="+domain_name);
	        buf = buf.append("\n\r"); 
	        buf.append("ldapport="+port);
	        buf = buf.append("\n\r"); 
	        buf.append("ldaphost="+host);
	        buf = buf.append("\n\r"); 
	        buf.append("ldap="+ldapset);
	        buf = buf.append("\n\r"); 
	        buf.append("ldapserver="+ldaptype);
	        buf = buf.append("\n\r"); 
	        buf.append("initialContextFactory=com.sun.jndi.ldap.LdapCtxFactory");
	        buf = buf.append("\n\r"); 
	        br.close(); 
            fos = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(fos); 
            pw.write(buf.toString().toCharArray()); 
            pw.flush(); 
            pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) { 
            e.printStackTrace(); 
        } finally{
			PubFunc.closeResource(fos);
			PubFunc.closeResource(br);
        	PubFunc.closeIoResource(isr);
        	PubFunc.closeIoResource(fis);
        }
	}
	
}
