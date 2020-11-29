package com.hjsj.hrms.businessobject.hire;


import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

//import org.apache.commons.beanutils.DynaBean;
//import org.apache.commons.beanutils.LazyDynaBean;

/**
 * <p>title:AutoSendEMailBo.java</p>
 * <p>Description:自动发送email</p>
 * <p>Company:hjsj</p>
 * <p>create time:2007.05.12  15:13:05 pm</P>
 * @author lizhenwei
 * @version 4.0
 *
 */
public class AutoSendEMailBo {
	/**数据库连接*/
	private Connection conn;
	
	public AutoSendEMailBo(Connection con){
		this.conn=con;
		
	}
	
	/**
	 * 发送邮件调用的方法
	 * @param z0301
	 * @param toAddr
	 */
	public void AutoSend(String z0301,String toAddr,String a0100,String dbname,String templateId){
		boolean flag = false;
		String sql1 = "select "+dbname+"a01.A0101 from "+dbname+"a01 where A0100 = '"+a0100+"'";
		String sql = "select o.codeitemdesc from organization o ,z03 where z03.z0301 = '"+z0301+"' and z03.z0311 = o.codeitemid and o.codesetid='@K'";
		String sql2 = "select content,title from t_sys_msgtemplate where template_id = "+templateId;
		String A0101 = "";
		String desc = "";
		String template_content = "";
		String email_title= "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs=dao.search(sql2);
			while(rs.next()){
				flag = true;
				template_content = rs.getString("content");
				email_title=rs.getString("title");
				if(template_content.indexOf("[") != -1){
					template_content=template_content.replaceAll("\\[","");
					template_content=template_content.replaceAll("\\]","");
				}
			}
			if(!flag) {
                return;
            }
			rs=dao.search(sql1);
			while(rs.next()){
				if(rs.getString("A0101")!= null) {
                    A0101 =rs.getString("A0101");
                }
				
			}
			rs=dao.search(sql);
			while(rs.next()){
				if(rs.getString("codeitemdesc") != null) {
                    desc=rs.getString("codeitemdesc");
                }
			}
			
				
			template_content=template_content.replaceAll("人员姓名\\(A0100\\)",A0101);
			template_content=template_content.replaceAll("应聘职位\\(z0311\\)",desc);
		 
			template_content=template_content.replaceAll("\\(~系统时间~\\)",format.format(new Date()));
		    EMailBo bo = new EMailBo(this.conn,true,"");
		    /**对这种情况，按超级用户所有地址作为发送地址*/
		    String fromaddr=this.getFromAddr();
		    String toaddr=bo.getEmailAddrByA0100(dbname+a0100);
		    bo.sendEmail(email_title,template_content,"",fromaddr,toaddr);
			bo.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	/**
	 * 从系统邮件服务器设置中得到发送邮件的地址
	 * @return
	 */
	public String getFromAddr() throws GeneralException 
	{
		String str = "";
        RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_STMP_SERVER");
        if(stmp_vo==null) {
            return "";
        }
        String param=stmp_vo.getString("str_value");
        if(param==null|| "".equals(param)) {
            return "";
        }
        try
        {
	        Document doc = PubFunc.generateDom(param);
	        Element root = doc.getRootElement();
	        Element stmp=root.getChild("stmp");	
	        str=stmp.getAttributeValue("from_addr");
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ex);
        }  
        return str;
	}
	/**
	 * 群发邮件
	 * @param dbpre
	 * @param list
	 * @param template_id
	 */
	public void sendEqualEMail(String dbpre,ArrayList list,String template_id,String type,String status,UserView userView) throws GeneralException {
		try{
			String title="";
			String content="";
			String t_c = this.getTemplateInfo(template_id);
			String[] tc_Arr=t_c.split("#");
			title=tc_Arr[0];
			content=tc_Arr[1];
	      	String email_field ="";
    		if(this.getEmailField()!=null && this.getEmailField().trim().length()>0){
	    		email_field=this.getEmailField();
    		}
	    	if(email_field==null || email_field.trim().length()<=0) {
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.employResume.noemailfield")));
            }
	
    		HashMap personMap = this.getPersonInfo(dbpre,this.getUserA0100(list),type,status,userView);
    		HashMap    emailMap=this.getEmailInfo(email_field,dbpre,this.getUserA0100(list),type,status,userView);
    		HashMap positionMap=this.getPositionInfo();
    		HashMap userNameMap=this.getUserName(dbpre,this.getUserA0100(list),type,status,userView);
    		int maxsend = 20;
    		String max=getMaxSend();
    		if(max != null && max.trim().length()>0) {
                maxsend=Integer.parseInt(max);
            }
    		if(maxsend==0) {
                maxsend=20;
            }
	    	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
            String fromAddr=this.getFromAddr();
            if(fromAddr ==null || fromAddr.trim().length()<=0){
            	throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.employResume.noemailservice")));
            }
            int n=0;
            if(list.size()%maxsend==0) {
                n=list.size()/maxsend;
            } else {
                n=list.size()/maxsend+1;
            }
            EMailBo bo = null;
            StringBuffer buf = new StringBuffer("");
            for(int j=0;j<n;j++){
         	   try
        	   {
        	        bo = new EMailBo(this.conn,true,"");
        	   }
        	   catch(Exception e)
        	   {
        		   throw GeneralExceptionHandler.Handle(new Exception("连接邮件服务器失败，请检查邮件服务器设置和网络连接")); 
        	   }
     		for(int i=j*maxsend ;i<(j+1)*maxsend&&i<list.size();i++){
    			String a0100="";
    			String z0301="";
    			String s=(String)list.get(i);
    			if(s.indexOf("/") != -1){
     			    a0100=s.substring(0,s.indexOf("/"));
    			    z0301=s.substring(s.indexOf("/")+1);
			    
	    		}else{
        			a0100=s;
	    		}
	    		String email_address="";
	    	  if(emailMap.get(a0100)==null || !(this.isMail((String)emailMap.get(a0100)))){
	    		  if(userNameMap.get(a0100)==null||!(this.isMail((String)userNameMap.get(a0100)))){
				 // System.out.println("email是空的人：a0100="+a0100+":a0101="+personMap.get(a0100)+":email="+emailMap.get(a0100)+":username="+userNameMap.get(a0100));
	    			  buf.append((String)personMap.get(a0100)+",email:"+email_address+"发送失败<br>");  
	    			  continue;
	    		  }
	    		  else {
                      email_address=(String)userNameMap.get(a0100);
                  }
	    	  } else {
                  email_address=(String)emailMap.get(a0100);
              }
   			  
    		   if(content.indexOf("[") != -1){
    			     content=content.replaceAll("\\[","");
    			     content=content.replaceAll("\\]","");
    		   }
    		   if(personMap.get(a0100) !=null) {
                   content=content.replaceAll("人员姓名\\(A0100\\)",(String)personMap.get(a0100));
               } else{
     		        content=content.replaceAll("人员姓名\\(A0100\\)","");
    		   }
    				content=content.replaceAll("\\(~系统时间~\\)",format.format(new Date()));
    				if(z0301.trim().length()>0 && positionMap.get(z0301) != null) {
                        content=content.replaceAll("应聘职位\\(z0311\\)",(String)positionMap.get(z0301));
                    } else{
    					content=content.replaceAll("应聘职位\\(z0311\\)","");
    				}
				 /*content=content.replaceAll("\r\n","<br>");
				 content=content.replace("\n","<br>");
				 content=content.replace("\r","<br>");*/
    				 try
    				 {
                            bo.sendEmail(title,content,"",fromAddr,email_address);
    				 }catch(Exception e){
    					 buf.append((String)personMap.get(a0100)+",email:"+email_address+"发送失败<br>"); 
    					 /*e.printStackTrace();
    					 throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.employResume.sendfaild"))); */
    				 }
     			   content=tc_Arr[1];
    		}
		
		      
		
		 
       }
            if(buf.toString().length()>0) {
                throw GeneralExceptionHandler.Handle(new Exception(buf.toString()));
            }
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	public void sendEqualEMailByUserView(String dbpre,ArrayList list,String template_id,String type,String status,UserView userView) throws GeneralException {
		try{
			String title="";
			String content="";
			String t_c = this.getTemplateInfo(template_id);
			String[] tc_Arr=t_c.split("#");
			title=tc_Arr[0];
			content=tc_Arr[1];
	      	String email_field ="";
    		if(this.getEmailField()!=null && this.getEmailField().trim().length()>0){
	    		email_field=this.getEmailField();
    		}
	    	if(email_field==null || email_field.trim().length()<=0) {
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.employResume.noemailfield")));
            }
	
    		HashMap personMap = this.getPersonInfo(dbpre,this.getUserA0100(list),type,status,userView);
    		HashMap    emailMap=this.getEmailInfo(email_field,dbpre,this.getUserA0100(list),type,status,userView);
    		HashMap positionMap=this.getPositionInfo();
    		HashMap userNameMap=this.getUserName(dbpre,this.getUserA0100(list),type,status,userView);
    		int maxsend = 20;
    		String max=getMaxSend();
    		if(max != null && max.trim().length()>0) {
                maxsend=Integer.parseInt(max);
            }
    		if(maxsend==0) {
                maxsend=20;
            }
	    	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
            String fromAddr="";
            int n=0;
            if(list.size()%maxsend==0) {
                n=list.size()/maxsend;
            } else {
                n=list.size()/maxsend+1;
            }
            EMailBo bo = null;
            String username="";
    		String password="";
            StringBuffer buf = new StringBuffer("");
            EmailTemplateBo  etb = new EmailTemplateBo(this.conn);
            LazyDynaBean bean = etb.getFromAddrByUser(userView);
            LazyDynaBean bean2 = etb.getOperuserBean(userView);
            for(int j=0;j<n;j++){
         	   try
        	   {
         		  boolean flagS=false;
         		   
    			   if(bean.get("email")!=null&&!"".equals((String)bean.get("email"))&&bean.get("pw")!=null&&!"".equals((String)bean.get("pw")))
    			   {
    				   username=(String)bean.get("email");
    				   password=(String)bean.get("pw");
    				   fromAddr=username;
    				   bo = new EMailBo(this.conn,true);
    				   bo.setUsername(username);
	 		    	   bo.setPassword(password);
	 		    	  if(bo.configTransfor())
	 		          {
	 		        	  flagS=true;
	 		          }
    			   }
    			   if(!flagS)
	 		    	  {
	 		    		   if(bean2.get("email")!=null&&!"".equals((String)bean2.get("email"))&&bean2.get("pw")!=null&&!"".equals((String)bean2.get("pw")))
	 					   {
	 						   username=(String)bean2.get("email");
	 						   password=(String)bean2.get("pw");
	 						   fromAddr=username;
	 						   bo.setUsername(username);
	 		 		    	   bo.setPassword(password);
	 		 		    	   if(bo.configTransfor())
	 		 		    	   {
	 		 		    		   flagS=true;
	 		 		    	   }
	 					   }
	 		    	  }
    			   if(!flagS)
	 		       {
        	          bo = new EMailBo(this.conn,true,"");
        	          fromAddr=this.getFromAddr();
        	          if(fromAddr ==null || fromAddr.trim().length()<=0){
        	            throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.employResume.noemailservice")));
        	          }
	 		       }
        	   }
        	   catch(Exception e)
        	   {
        		   throw GeneralExceptionHandler.Handle(new Exception("连接邮件服务器失败，请检查邮件服务器设置和网络连接")); 
        	   }
     		for(int i=j*maxsend ;i<(j+1)*maxsend&&i<list.size();i++){
    			String a0100="";
    			String z0301="";
    			String s=(String)list.get(i);
    			if(s.indexOf("/") != -1){
     			    a0100=s.substring(0,s.indexOf("/"));
    			    z0301=s.substring(s.indexOf("/")+1);
			    
	    		}else{
        			a0100=s;
	    		}
	    		String email_address="";
	    	  if(emailMap.get(a0100)==null || !(this.isMail((String)emailMap.get(a0100)))){
	    		  if(userNameMap.get(a0100)==null||!(this.isMail((String)userNameMap.get(a0100)))){
				 // System.out.println("email是空的人：a0100="+a0100+":a0101="+personMap.get(a0100)+":email="+emailMap.get(a0100)+":username="+userNameMap.get(a0100));
	    			  buf.append((String)personMap.get(a0100)+",email:"+email_address+"发送失败<br>");  
	    			  continue;
	    		  }
	    		  else {
                      email_address=(String)userNameMap.get(a0100);
                  }
	    	  } else {
                  email_address=(String)emailMap.get(a0100);
              }
   			  
    		   if(content.indexOf("[") != -1){
    			     content=content.replaceAll("\\[","");
    			     content=content.replaceAll("\\]","");
    		   }
    		   if(personMap.get(a0100) !=null) {
                   content=content.replaceAll("人员姓名\\(A0100\\)",(String)personMap.get(a0100));
               } else{
     		        content=content.replaceAll("人员姓名\\(A0100\\)","");
    		   }
    				content=content.replaceAll("\\(~系统时间~\\)",format.format(new Date()));
    				if(z0301.trim().length()>0 && positionMap.get(z0301) != null) {
                        content=content.replaceAll("应聘职位\\(z0311\\)",(String)positionMap.get(z0301));
                    } else{
    					content=content.replaceAll("应聘职位\\(z0311\\)","");
    				}
				 /*content=content.replaceAll("\r\n","<br>");
				 content=content.replace("\n","<br>");
				 content=content.replace("\r","<br>");*/
    				 try
    				 {
                            bo.sendEmail(title,content,"",fromAddr,email_address);
    				 }catch(Exception e){
    					 buf.append((String)personMap.get(a0100)+",email:"+email_address+"发送失败<br>"); 
    					 /*e.printStackTrace();
    					 throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.employResume.sendfaild"))); */
    				 }
     			   content=tc_Arr[1];
    		}
		
		      
		
		 
       }
            FileOutputStream fileOut = null;
            try {
                if (buf.toString().length() > 0) {
                    String outName = "RevokeFile_" + PubFunc.getStrg() + ".txt";
                    fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
                    fileOut.write(buf.toString().getBytes());
                    outName = outName.replace(".txt", "#");
                    // throw GeneralExceptionHandler.Handle(new
                    // Exception(buf.toString()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileOut != null) {
                    PubFunc.closeResource(fileOut);
                }
            }
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	/**
	 * 群发短信
	 * @param dbpre 招聘库前缀
	 * @param list 封装了发送短信人员的信息
	 * @param template_id 模板
	 * @param type 群发还是单发
	 * @param status 状态
	 * @param userView 登录用户对象
	 * @throws GeneralException
	 */
	public void sendMessage(String dbpre,ArrayList list,String template_id,String type,String status,UserView userView) throws GeneralException {
		try
		{
			//boolean bb= this.isMobileNumber("010-197574");
			String title="";
			String content="";
			String t_c = this.getTemplateInfo(template_id);
			String[] tc_Arr=t_c.split("#");
			title=tc_Arr[0];
			content=tc_Arr[1];
	      	String mobile_field ="";
    		if(this.getMobileField()!=null && this.getMobileField().trim().length()>0){
    			mobile_field=this.getMobileField();
    		}
	    	if(mobile_field==null || mobile_field.trim().length()<=0) {
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.employResume.nomessagefield")));
            }
	    	 RecordVo sms_vo=ConstantParamter.getConstantVo("SS_SMS_OPTIONS");
	         if(sms_vo==null) {
                 throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.employResume.nomessageservice")));
             }
	         String param=sms_vo.getString("str_value");
	         if(param==null|| "".equals(param)) {
                 throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.employResume.nomessageservice")));
             }
    		HashMap personMap = this.getPersonInfo(dbpre,this.getUserA0100(list),type,status,userView);
    		HashMap    mobileMap=this.getEmailInfo(mobile_field,dbpre,this.getUserA0100(list),type,status,userView);
    		HashMap positionMap=this.getPositionInfo();
	    	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
      
	    	ArrayList destlist = new ArrayList();
     		for(int i=0;i<list.size();i++){
    			String a0100="";
    			String z0301="";
    			String s=(String)list.get(i);
    			if(s.indexOf("/") != -1){
     			    a0100=s.substring(0,s.indexOf("/"));
    			    z0301=s.substring(s.indexOf("/")+1);
			    
	    		}else{
        			a0100=s;
	    		}
	    		String mobile_num="";
	    	  if(mobileMap.get(a0100)==null || !(this.isMobileNumber((String)mobileMap.get(a0100)))){
	    			    continue;
	    	  } else {
                  mobile_num=(String)mobileMap.get(a0100);
              }
   			  
    		   if(content.indexOf("[") != -1){
    			     content=content.replaceAll("\\[","");
    			     content=content.replaceAll("\\]","");
    		   }
    		   if(personMap.get(a0100) !=null) {
                   content=content.replaceAll("人员姓名\\(A0100\\)",(String)personMap.get(a0100));
               } else{
     		        content=content.replaceAll("人员姓名\\(A0100\\)","");
    		   }
    				content=content.replaceAll("\\(~系统时间~\\)",format.format(new Date()));
    				if(z0301.trim().length()>0 && positionMap.get(z0301) != null) {
                        content=content.replaceAll("应聘职位\\(z0311\\)",(String)positionMap.get(z0301));
                    } else{
    					content=content.replaceAll("应聘职位\\(z0311\\)","");
    				}
    				LazyDynaBean dyvo=new LazyDynaBean();
					dyvo.set("sender",userView.getUserFullName());
					dyvo.set("receiver",(String)personMap.get(a0100));
					dyvo.set("phone_num",mobile_num);
					content=content.replaceAll(" ", "");
					content=content.replaceAll("\\r","");
					content=content.replaceAll("\\n","");
					content=content.replaceAll("\\r\\n","");
					dyvo.set("msg",content);
					destlist.add(dyvo);
     			   content=tc_Arr[1];
    		   }
     		try
     		{
     			if(destlist!=null&&destlist.size()>0)
     			{
            		SmsBo smsbo=new SmsBo(this.conn);
	        		smsbo.batchSendMessage(destlist);
     			}
     		}
     		catch(Exception e)
     		{
     			 throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.employResume.sendmessagefaild"))); 
     		}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 取系统的电话指标
	 * @return
	 */
	public String getMobileField()
	{
		String str = "";
		try
		{
			  RecordVo vo=ConstantParamter.getConstantVo("SS_MOBILE_PHONE");
		        if(vo==null) {
                    return "";
                }
		        String field_name=vo.getString("str_value");
		        if(field_name==null|| "".equals(field_name)) {
                    return "";
                }
		        FieldItem item=DataDictionary.getFieldItem(field_name);
		        if(item==null) {
                    return "";
                }
		        /**分析是否构库*/
		        if("0".equals(item.getUseflag())) {
                    return "";
                }
		        return field_name; 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
    /**
     * 得到系统邮件指标
     * @return
     */
    public String getEmailField(){
    	String str="";
    	try{
    	   RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_EMAIL");
           if(stmp_vo==null) {
               return "";
           }
           String param=stmp_vo.getString("str_value");
           if(param==null|| "#".equals(param)) {
               return "";
           }
           str=param;
    	}catch(Exception ex)
         {
         	ex.printStackTrace();
         }  
    	
    	return str;
    }
    /**
     * 取得邮件模板的标题和内容
     * @param template_id
     * @return
     */
    public String getTemplateInfo(String template_id){
    	String t_c="";
    	String sql ="select title,content from t_sys_msgtemplate where template_id="+template_id;
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	try{
    		rs=dao.search(sql);
    		while(rs.next()){
    			String title=rs.getString("title");
    			String content=rs.getString("content");
    			t_c=title+"#"+content;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return t_c;
    }
    /**
     * 得到人的信息
     * @param dbpre
     * @return
     */
    public HashMap getPersonInfo(String dbpre,String a0100s,String type,String status,UserView userView){
    	HashMap map=new HashMap();
    	ContentDAO  dao =new ContentDAO(this.conn);
    	RowSet rs= null;
    	StringBuffer str= new StringBuffer();
    	if("0".equals(type)){
    		str.append("select a0100,a0101 from ");
    		str.append(dbpre);
    		str.append("a01 where a0100 in (");
    		str.append(a0100s);
    		str.append(") order by a0100");
    	
    	}else if("1".equals(type)){
    		if("-1".equals(status)){
    			str.append("select "+dbpre+"a01.a0100,"+dbpre+"a01.a0101 from ");
    			str.append(dbpre+"a01");
    			str.append(" where ");
    			str.append(dbpre+"a01.a0100");
    			str.append(" not in (select distinct a0100 from zp_pos_tache)");
    		}else{
    			if("-3".equalsIgnoreCase(status)){
    				str.append(" select zpt.a0100,");

    				str.append("a.a0101  from zp_pos_tache zpt ,z03,");
    				str.append(dbpre);
    				str.append("a01 a where zpt.zp_pos_id=z03.z0301 and zpt.a0100 =a.a0100 and (a.a0100 not in (select a0100 from zp_pos_tache) or a.a0100 in (select a0100 from zp_pos_tache left join Z03 on zp_pos_tache.zp_pos_id=Z03.z0301 where   1=1 ))  and a.a0100 is not null  order by  zp_pos_id asc ");
    			}else{
	    			str.append("select zpt.a0100,");
	    			str.append(dbpre+"a01.a0101 ");
	    			str.append("from zp_pos_tache zpt ,z03,");
	    			str.append(dbpre+"a01");
	    			str.append(" where zpt.zp_pos_id=z03.z0301 and resume_flag='"+status+"'");
	    			str.append(" and zpt.a0100 = ");
	    			str.append(dbpre+"a01.a0100");
	    			if(!(userView.isAdmin()&& "1".equals(userView.getGroupId()))&& userView.getUnit_id().length()>2){
	    			//	str.append(" and z0311 like '");
	    			//	str.append(userView.getUnit_id().substring(2));
	    			//	str.append("%'");
	    				
	    				StringBuffer tempSql=new StringBuffer("");
						String[] temp=userView.getUnit_id().split("`");
						for(int i=0;i<temp.length;i++)
						{
							tempSql.append(" or  z0311 like '"+temp[i].substring(2)+"%'");
						}
						str.append(" and ( "+tempSql.substring(3)+" ) ");
	    				
	    				
	    				
	    			}
    			}
    		}
    		
    	}
    	try{
    		rs=dao.search(str.toString());
    		while(rs.next()){
    			map.put(rs.getString("a0100"),rs.getString("a0101"));
    		}
    			
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return map;
    }
    /**
     * 一次得到所有职位的信息
     */
    public HashMap getPositionInfo(){
    	HashMap map = new HashMap();
    	ContentDAO dao =new ContentDAO(this.conn);
    	RowSet rs =null;
    	try{
    		String sql="select z03.z0301, o.codeitemdesc from organization o ,z03 where z03.z0311 = o.codeitemid and o.codesetid='@K' order by z0311";
            rs=dao.search(sql);
            while(rs.next()){
            	map.put(rs.getString("z0301"),rs.getString("codeitemdesc"));
            }
    }catch(Exception e){
    	e.printStackTrace();
    }
    return map;
    }
    /**
     * 根据系统的邮件指标设置得到目的邮件地址
     * @param email_field
     * @param dbpre
     * @return
     */
	public HashMap getEmailInfo(String email_field,String dbpre,String a0100s,String type,String status,UserView userView){
		HashMap map = new HashMap();
		StringBuffer str = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs =null;
		if("0".equals(type)){
    		str.append("select a0100,"+email_field+" from ");
    		str.append(dbpre);
    		str.append("a01 where a0100 in (");
    		str.append(a0100s);
    		str.append(") order by a0100");
    	
    	}else if("1".equals(type)){
    		if("-1".equals(status)){
    			str.append("select "+dbpre+"a01.a0100,"+dbpre+"a01."+email_field+" from ");
    			str.append(dbpre+"a01");
    			str.append(" where ");
    			str.append(dbpre+"a01.a0100");
    			str.append(" not in (select distinct a0100 from zp_pos_tache)");
    		}else{
    			if("-3".equalsIgnoreCase(status)){
    				str.append(" select zpt.a0100,");
    				str.append("a.");
    				str.append(email_field);
    				str.append(" from zp_pos_tache zpt ,z03,");
    				str.append(dbpre);
    				str.append("a01 a where zpt.zp_pos_id=z03.z0301 and zpt.a0100 =a.a0100 and (a.a0100 not in (select a0100 from zp_pos_tache) or a.a0100 in (select a0100 from zp_pos_tache left join Z03 on zp_pos_tache.zp_pos_id=Z03.z0301 where   1=1 ))  and a.a0100 is not null  order by  zp_pos_id asc ");
    			}else{
	    			str.append("select zpt.a0100,");
	    			str.append(dbpre+"a01."+email_field);
	    			str.append(" from zp_pos_tache zpt ,z03,");
	    			str.append(dbpre+"a01");
	    			str.append(" where zpt.zp_pos_id=z03.z0301 and resume_flag='"+status+"'");
	    			str.append(" and zpt.a0100 = ");
	    			str.append(dbpre+"a01.a0100");
	    			if(!(userView.isAdmin()&& "1".equals(userView.getGroupId()))&& userView.getUnit_id().length()>2){
	    			//	str.append(" and z0311 like '");
	    			//	str.append(userView.getUnit_id().substring(2));
	    			//	str.append("%'");
	    				StringBuffer tempSql=new StringBuffer("");
						String[] temp=userView.getUnit_id().split("`");
						for(int i=0;i<temp.length;i++)
						{
							tempSql.append(" or  z0311 like '"+temp[i].substring(2)+"%'");
						}
						str.append(" and ( "+tempSql.substring(3)+" ) ");
	    				
	    			}
    			}
    		}
    		
    	}
		try{
			rs=dao.search(str.toString());
			while(rs.next()){
				map.put(rs.getString("a0100"),rs.getString(email_field));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
    /**
     * 从封装的要发送人员列表中取得所有人的a0100
     * @param list
     * @return
     */
	public String getUserA0100(ArrayList list){
		StringBuffer str= new StringBuffer();
		for(int i=0;i<list.size();i++){
			String s=(String)list.get(i);
			if(s.indexOf("/")!=-1){
				str.append(",'");
				str.append(s.substring(0,s.indexOf("/")));
				str.append("'");
			}else{
				str.append(",'");
				str.append(s);
				str.append("'");
			}
		}
		if(str !=null&&str.toString().trim().length()>0 ) {
            return str.toString().substring(1);
        } else {
            return "";
        }
	}
	/**
	 * 判断是否是有效的邮件地址
	 * @param email
	 * @return
	 */
	public boolean isMail(String email){
	   
		String emailPattern ="^([a-z0-9A-Z]+[_]*[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
	    return  email.matches(emailPattern);
	}
	/**
	 * 是否有效电话号码（固定和移动电话号码）
	 * @param number
	 * @return
	 */
	public boolean isMobileNumber(String number)
	{
		//String s="^((\\d{3,4}-)?\\d{7,8})$|(0?(13|15)[0-9]{9})"; 
		String mobile_Pattern="(\\(\\d{3,4}\\)|\\d{3,4}-|\\s)?\\d{7,14}";

		return number.matches(mobile_Pattern);
	}
	/**
	 * 得到系统设置的最大发送数量
	 * @return
	 * @throws GeneralException
	 */
	
	public String getMaxSend() throws GeneralException 
	{
		String str = "";
        RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_STMP_SERVER");
        if(stmp_vo==null) {
            return "";
        }
        String param=stmp_vo.getString("str_value");
        if(param==null|| "".equals(param)) {
            return "";
        }
        try
        {
	        Document doc = PubFunc.generateDom(param);
	        Element root = doc.getRootElement();
	        Element stmp=root.getChild("stmp");	
	        str=stmp.getAttributeValue("max_send");
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ex);
        }  
        return str;
	}
	/**
	 * 当邮件指标内容为空时，将应用库中的username作为第二选择
	 * @param dbpre
	 * @param a0100s
	 * @param type
	 * @param status
	 * @param userView
	 * @return
	 */
	public HashMap getUserName(String dbpre,String a0100s,String type,String status,UserView userView){
		HashMap map = new HashMap();
		StringBuffer str = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs =null;
		if("0".equals(type)){
    		str.append("select a0100,username from ");
    		str.append(dbpre);
    		str.append("a01 where a0100 in (");
    		str.append(a0100s);
    		str.append(") order by a0100");
    	
    	}else if("1".equals(type)){
    		if("-1".equals(status)){
    			str.append("select "+dbpre+"a01.a0100,"+dbpre+"a01.username from ");
    			str.append(dbpre+"a01");
    			str.append(" where ");
    			str.append(dbpre+"a01.a0100");
    			str.append(" not in (select distinct a0100 from zp_pos_tache)");
    		}else{
    			str.append("select zpt.a0100,");
    			str.append(dbpre+"a01.username");
    			str.append(" from zp_pos_tache zpt ,z03,");
    			str.append(dbpre+"a01");
    			str.append(" where zpt.zp_pos_id=z03.z0301 and resume_flag='"+status+"'");
    			str.append(" and zpt.a0100 = ");
    			str.append(dbpre+"a01.a0100");
    			if(!(userView.isAdmin()&& "1".equals(userView.getGroupId()))&& userView.getUnit_id().length()>2){
    			//	str.append(" and z0311 like '");
    			//	str.append(userView.getUnit_id().substring(2));
    			//	str.append("%'");
    				StringBuffer tempSql=new StringBuffer("");
					String[] temp=userView.getUnit_id().split("`");
					for(int i=0;i<temp.length;i++)
					{
						tempSql.append(" or  z0311 like '"+temp[i].substring(2)+"%'");
					}
					str.append(" and ( "+tempSql.substring(3)+" ) ");
    			}
    		}
    		
    	}
		try{
			rs=dao.search(str.toString());
			while(rs.next()){
				map.put(rs.getString("a0100"),rs.getString("username"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * dml 2011-6-3 14:23:38
	 * 群发邮件 返回文件名称。方法同sendEqualEMailByUserView
	 * @throws GeneralException 
	 * 
	 * */
	public String  sendEqualEMailByUserView1(String dbpre,ArrayList list,String template_id,String type,String status,UserView userView) throws GeneralException{
		String fileName="";
		try{
			String title="";
			String content="";
			String t_c = this.getTemplateInfo(template_id);
			String[] tc_Arr=t_c.split("#");
			title=tc_Arr[0];
			content=tc_Arr[1];
	      	String email_field ="";
    		if(this.getEmailField()!=null && this.getEmailField().trim().length()>0){
	    		email_field=this.getEmailField();
    		}
	    	if(email_field==null || email_field.trim().length()<=0) {
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.employResume.noemailfield")));
            }
	
    		HashMap personMap = this.getPersonInfo(dbpre,this.getUserA0100(list),type,status,userView);
    		HashMap    emailMap=this.getEmailInfo(email_field,dbpre,this.getUserA0100(list),type,status,userView);
    		HashMap positionMap=this.getPositionInfo();
    		HashMap userNameMap=this.getUserName(dbpre,this.getUserA0100(list),type,status,userView);
    		int maxsend = 20;
    		String max=getMaxSend();
    		if(max != null && max.trim().length()>0) {
                maxsend=Integer.parseInt(max);
            }
    		if(maxsend==0) {
                maxsend=20;
            }
	    	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
            String fromAddr="";
            int n=0;
            if(list.size()%maxsend==0) {
                n=list.size()/maxsend;
            } else {
                n=list.size()/maxsend+1;
            }
            EMailBo bo = null;
            String username="";
    		String password="";
            StringBuffer buf = new StringBuffer("");
            EmailTemplateBo  etb = new EmailTemplateBo(this.conn);
            LazyDynaBean bean = etb.getFromAddrByUser(userView);
            LazyDynaBean bean2 = etb.getOperuserBean(userView);
            for(int j=0;j<n;j++){
         		  boolean flagS=false;
         		   
    			   if(bean.get("email")!=null&&!"".equals((String)bean.get("email"))&&bean.get("pw")!=null&&!"".equals((String)bean.get("pw")))
    			   {
    				   username=(String)bean.get("email");
    				   password=(String)bean.get("pw");
    				   fromAddr=username;
    				   bo = new EMailBo(this.conn,true);
    				   bo.setUsername(username);
	 		    	   bo.setPassword(password);
	 		    	  if(bo.configTransfor())
	 		          {
	 		        	  flagS=true;
	 		          }
    			   }
    			   if(!flagS)
	 		    	  {
	 		    		   if(bean2.get("email")!=null&&!"".equals((String)bean2.get("email"))&&bean2.get("pw")!=null&&!"".equals((String)bean2.get("pw")))
	 					   {
	 						   username=(String)bean2.get("email");
	 						   password=(String)bean2.get("pw");
	 						   fromAddr=username;
	 						   bo.setUsername(username);
	 		 		    	   bo.setPassword(password);
	 		 		    	   if(bo.configTransfor())
	 		 		    	   {
	 		 		    		   flagS=true;
	 		 		    	   }
	 					   }
	 		    	  }
    			   if(!flagS)
	 		       {
        	          bo = new EMailBo(this.conn,true,"");
        	          fromAddr=this.getFromAddr();
        	          if(fromAddr ==null || fromAddr.trim().length()<=0){
        	            throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.employResume.noemailservice")));
        	          }
	 		       }
        		  
        	   
     		for(int i=j*maxsend ;i<(j+1)*maxsend&&i<list.size();i++){
    			String a0100="";
    			String z0301="";
    			String s=(String)list.get(i);
    			if(s.indexOf("/") != -1){
     			    a0100=s.substring(0,s.indexOf("/"));
    			    z0301=s.substring(s.indexOf("/")+1);
			    
	    		}else{
        			a0100=s;
	    		}
	    		String email_address="";
	    	  if(emailMap.get(a0100)==null || !(this.isMail((String)emailMap.get(a0100)))){
	    		  if(userNameMap.get(a0100)==null||!(this.isMail((String)userNameMap.get(a0100)))){
				 // System.out.println("email是空的人：a0100="+a0100+":a0101="+personMap.get(a0100)+":email="+emailMap.get(a0100)+":username="+userNameMap.get(a0100));
	    			  buf.append((String)personMap.get(a0100)+",email:"+email_address+"发送失败\r\n");  
	    			  continue;
	    		  }
	    		  else {
                      email_address=(String)userNameMap.get(a0100);
                  }
	    	  } else {
                  email_address=(String)emailMap.get(a0100);
              }
   			  
    		   if(content.indexOf("[") != -1){
    			     content=content.replaceAll("\\[","");
    			     content=content.replaceAll("\\]","");
    		   }
    		   if(personMap.get(a0100) !=null) {
                   content=content.replaceAll("人员姓名\\(A0100\\)",(String)personMap.get(a0100));
               } else{
     		        content=content.replaceAll("人员姓名\\(A0100\\)","");
    		   }
    				content=content.replaceAll("\\(~系统时间~\\)",format.format(new Date()));
    				if(z0301.trim().length()>0 && positionMap.get(z0301) != null) {
                        content=content.replaceAll("应聘职位\\(z0311\\)",(String)positionMap.get(z0301));
                    } else{
    					content=content.replaceAll("应聘职位\\(z0311\\)","");
    				}
				 /*content=content.replaceAll("\r\n","<br>");
				 content=content.replace("\n","<br>");
				 content=content.replace("\r","<br>");*/
    				 try
    				 {
                            bo.sendEmail(title,content,"",fromAddr,email_address);
    				 }catch(Exception e){
    					 buf.append((String)personMap.get(a0100)+",email:"+email_address+"发送失败\r\n"); 
    					 /*e.printStackTrace();
    					 throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.employResume.sendfaild"))); */
    				 }
     			   content=tc_Arr[1];
    		}
       }
            if(buf.toString().length()>0){
            	String outName="RevokeFile_"+PubFunc.getStrg()+".txt";
            	FileOutputStream fileOut = null;
            	try{
            	fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outName);
            	fileOut.write(buf.toString().getBytes());
            	}catch(Exception e){
            		
            	}finally{
            		PubFunc.closeResource(fileOut);
            	}
				//outName=outName.replace(".txt","#");
				fileName=outName;
            	//throw GeneralExceptionHandler.Handle(new Exception(buf.toString()));
            }
//            else{
//            	 throw GeneralExceptionHandler.Handle(new Exception("邮件发送成功！")); 
//            }
			
		}catch(Exception e){
			e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(new Exception("连接邮件服务器失败，请检查邮件服务器设置和网络连接")); 
		}
		return fileName;
	}
}

