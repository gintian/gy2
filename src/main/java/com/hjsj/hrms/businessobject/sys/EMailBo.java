package com.hjsj.hrms.businessobject.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.sql.RowSet;
import java.security.Security;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * <p>Title:EMailBo</p>
 * <p>Description:主要用于系统邮件支持</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-13:17:01:36</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class EMailBo {
	/**邮件地址的提供方式
	 * =false按人员编码,
	 * =true按实际邮件地址
	 */
	private boolean bflag=false;
	/**应用库前缀*/
	private String dbase;
	/**所有库前缀串*/
	private StringBuffer str_dbff=new StringBuffer();
	/**发送方邮件地址*/
	private String sAddr;
	/**接收方邮件地址*/
	private List dAddrlist;
	/**抄送邮件地址*/
	private List ccAddrlist;
	/**数据库连接*/
	private Connection conn;
	/**存放用户电子信箱的字段*/
	private String email_field;
	
	/**stmp server options*/
	private String username;
	private String password;
	/**是否要进行用户认证*/
	private String authy="0";
	private String port;
	private String encryption;
	/**发送人昵称*/
	private String sendername;
	
	/**系统属性*/
	Properties props = new Properties();//System.getProperties();
	private Session session=null;
	private Transport transport=null;
	
	/**邮件风格模板*/
	// 是否使用风格模板
	private boolean useTemplate = true;
	// 风格模板中链接
	private String templateHref = "";
	private String templateHrefDesc = "查看详情";
	
	Category log = Category.getInstance(EMailBo.class.getName());
	
	public String getEmail_field() {
		return email_field;
	}
	public void setEmail_field(String email_field) {
		this.email_field = email_field;
	}
	
	private void getDbString()throws GeneralException
	{
		try
		{
			DbNameBo db_vo=new DbNameBo(this.conn);
			ArrayList list=db_vo.getAllDbNameVoList();
			for(int i=0;i<list.size();i++)
			{
				RecordVo vo=(RecordVo)list.get(i);
				str_dbff.append(vo.getString("pre").toUpperCase());
				str_dbff.append("'");
			}
		}
		catch(Exception ex)
		{
			//ex.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	/**
	 * 设置stmp服务器的属性值
	 * @throws GeneralException
	 */
	private void setStmpOptions()throws GeneralException
	{
        RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_STMP_SERVER");
        if(stmp_vo==null) {
            return ;
        }
        String param=stmp_vo.getString("str_value");
        if(param==null|| "".equals(param)) {
            return;
        }
        try
        {
	        Document doc = PubFunc.generateDom(param);
	        Element root = doc.getRootElement();
	        Element stmp=root.getChild("stmp");
			props.put("mail.smtp.host",stmp.getAttributeValue("host"));
			//System.out.println("--->"+stmp.getAttributeValue("host"));
			String authy=stmp.getAttributeValue("authy");
			if(authy==null|| "".equals(authy)) {
                throw new GeneralException("",ResourceFactory.getProperty("errors.stmp_server.notdefine"),"","");
            }
			if("1".equals(authy)) {
                props.put("mail.smtp.auth","true");
            }
			//props.setProperty("mail.smtp.auth", "true");
			this.username=stmp.getAttributeValue("username");
			this.password=stmp.getAttributeValue("password");
			this.sAddr=stmp.getAttributeValue("from_addr");
			this.port=stmp.getAttributeValue("port");
			String tmp=stmp.getAttributeValue("authy");
			if(tmp==null||tmp.length()==0) {
                tmp="0";
            }
			this.authy=tmp;
			
			this.sendername = stmp.getAttributeValue("sendername");
			
			String encryption = stmp.getAttributeValue("encryption");
			encryption = encryption==null?"":encryption;
			this.encryption = encryption;
			if(this.encryption.length()<1) {
                return;
            }
			
			if("tls".equals(encryption)){
				props.put("mail.smtp.user",this.username);
				props.put("mail.smtp.starttls.enable","true");
				props.put("mail.smtp.auth","true");
				props.put("mail.smtp.socketFactory.port",this.port);
				props.put("mail.smtp.socketFactory.fallback", "false");
				MailSSLSocketFactory sf = new MailSSLSocketFactory();  
			    sf.setTrustAllHosts(true);  
			    props.put("mail.smtp.ssl.socketFactory", sf);
			}else if( "ssl".equals(encryption)){
				Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
				props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				props.put("mail.smtp.socketFactory.fallback", "false");
				props.put("mail.smtp.auth","true");
				props.put("mail.smtp.port", this.port);
				props.put("mail.smtp.socketFactory.port", this.port);
				props.put("mail.smtp.ssl.checkserveridentity", true); // Compliant
			}
        }
        catch(Exception ex)
        {
        	//ex.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ex);
        }        
	}
	

    /**   
     * @Title: getSession   
     * @Description: 获取 Session 对象  
     * @param @return 
     * @return Session 
     * @author:wangrd   
     * @throws   
    */
    private Session getSession() {
        //this.session = Session.getDefaultInstance(props,null);
        //原来使用getDefaultInstance，可能报SecurityException错误，bug5863,如果报错的话使用getInstance wangrd 20141218
        boolean bExcept=false;
        
	    //  只有需要认证时才会创建一个认证器对象
	    Authenticator auth = !Boolean.parseBoolean(props.getProperty("mail.smtp.auth")) ? null :new MyAuthenticator(username, password);
        Session ss=null;
        try{
            ss= Session.getDefaultInstance(props,auth);
        } catch (Exception ex) {
            bExcept=true;
            //ex.printStackTrace();
        }
        if (bExcept){
            ss= Session.getInstance(props, auth); 
        }
        return ss;
    }
	
	/**
	 * @param conn
	 */
	public EMailBo(Connection conn,boolean bflag,String dbase) throws GeneralException{
		this.conn = conn;
		this.bflag=bflag;
		this.dbase=dbase;
		RecordVo vo=ConstantParamter.getRealConstantVo("SS_EMAIL");
	    if(vo!=null) {
            this.email_field=vo.getString("str_value");
        }
		/**设置SMTP主机*/
		setStmpOptions();
		getDbString();		
		/**邮件会话对象*/
		try
		{
			//this.session = Session.getDefaultInstance(props,null);
		    this.session =getSession();
			this.session.setDebug(false);
	 		this.transport =this.session.getTransport("smtp");
	 		if(this.port==null|| "".equals(this.port.trim()))
	 		{
	   	     	this.transport.connect((String)props.get("mail.smtp.host"),MimeUtility.encodeText(this.username),this.password);
	 		}else{
	   		    this.transport.connect((String)props.get("mail.smtp.host"), Integer.parseInt(this.port), MimeUtility.encodeText(this.username), this.password);

	 		}
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("发送失败，请检查您的邮件服务器配置是否正确！"));
		}
	}
	public EMailBo(Connection conn,boolean bflag) throws GeneralException{
		try
		{
	    	this.conn = conn;
	    	this.bflag=bflag;
	    	this.dbase=dbase;
	    	
	    	RecordVo vo=ConstantParamter.getRealConstantVo("SS_EMAIL");
	    	if(vo!=null) {
                this.email_field=vo.getString("str_value");
            }
	    	/**设置SMTP主机*/
		    setStmpOptions();
	    	getDbString();	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/** 
     * <p>Title: </p> 
     * <p>Description: </p> 
     * @param conn 
     * @param bflag
     * @param dbase
     * @param edit 标志是招聘外网注册进来,必须进行身份验证
    */
    public EMailBo(Connection conn, boolean bflag, String dbase, String edit) throws GeneralException{
        this.conn = conn;
        this.bflag=bflag;
        this.dbase=dbase;
        RecordVo vo=ConstantParamter.getRealConstantVo("SS_EMAIL");
        if(vo!=null) {
            this.email_field=vo.getString("str_value");
        }
        /**设置SMTP主机*/
        setStmpOptions();
        getDbString();    
        props.put("mail.smtp.auth","true");
        /**邮件会话对象*/
        try
        {
            //this.session = Session.getDefaultInstance(props,null);
            this.session =getSession();
            this.session.setDebug(false);
            this.transport =this.session.getTransport("smtp");
            if(this.port==null|| "".equals(this.port.trim()))
            {
                this.transport.connect((String)props.get("mail.smtp.host"),MimeUtility.encodeText(this.username),this.password);
            }else{
                this.transport.connect((String)props.get("mail.smtp.host"), Integer.parseInt(this.port), MimeUtility.encodeText(this.username), this.password);

            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
    }
    public boolean configTransfor() {
		boolean flag=true;
		/**邮件会话对象*/
		try
		{
			//this.session = Session.getDefaultInstance(props,null);
		    this.session =getSession();
			this.session.setDebug(false);
	 		this.transport =this.session.getTransport("smtp");
	 		if(this.port==null|| "".equals(this.port.trim())) {
                this.transport.connect((String)props.get("mail.smtp.host"),MimeUtility.encodeText(this.username),this.password);
            } else {
                this.transport.connect((String)props.get("mail.smtp.host"), Integer.parseInt(this.port), MimeUtility.encodeText(this.username), this.password);
            }

		}
		catch(Exception ex)
		{
			flag=false;
			//ex.printStackTrace();
			//throw GeneralExceptionHandler.Handle(ex);
		}
		return flag;
	}
/**
 * 取得电子信箱的SQL语句
 * @param a0100List
 * @return
 */
	private String getSqlByA0100List(List a0100List)
	{
		StringBuffer strSql=new StringBuffer();
		strSql.append("select A0101,");
		strSql.append(this.email_field);
		strSql.append(" from ");
		strSql.append(this.dbase);
		strSql.append("A01 where A0100 in ('");
		for(int i=0;i<a0100List.size();i++)
		{
			strSql.append(a0100List.get(i));
			strSql.append("',");
		}
		strSql.append(")");
		return strSql.toString();
	}

	private String getSqlByA0100(String  a0100)
	{
		String dbpre=a0100.substring(0,3);
		if(str_dbff.indexOf(dbpre.toUpperCase())!=-1)
		{
			a0100=a0100.substring(3);
		}
		else {
            dbpre=this.dbase;
        }
		StringBuffer strSql=new StringBuffer();
		strSql.append("select A0101,");
		strSql.append(this.email_field);
		strSql.append(",userpassword from ");
		strSql.append(dbpre);
		strSql.append("A01 where A0100 ='");
		strSql.append(a0100);
		strSql.append("'");
		return strSql.toString();
	}	
	/**
	 * 群发同一封信(信件内容及附件相同)
	 * @param topic 邮件标题
	 * @param bodylist 邮件主体部分,列表中保存的是字符串,主体内容列表,可以有多个主体
	 * @param filelist 附近列表 ,列表中保存是字符串,带目录结构的文件名称,可以有多个附件
	 * @param sAddr  源邮件地址
	 * @param repAddr 目的邮件地址 ,字符串列表
	 * @param ccList  抄送地址,字符串列表
	 */
	public void sendEqualEmail(String topic,List bodylist,List filelist,String fromAddr,List toAddr,List ccList) throws GeneralException
	{
		
		/**根据人员编码从实际的数据库查得对应的电子邮箱的地址*/
		if(!this.bflag)
		{
			ArrayList temp=new ArrayList();
			temp.add(fromAddr);
			ArrayList list=getRealEmailAddr(temp);
			if(list.size()>0) {
                this.sAddr=(String)list.get(0);
            }
			this.dAddrlist=getRealEmailAddr(toAddr);
			this.ccAddrlist=getRealEmailAddr(ccList);			
		}
		else
		{
			this.sAddr=fromAddr;
			ArrayList list0=new ArrayList();
			for(int i=0;i<toAddr.size();i++)
			{
				DynaBean vo=new LazyDynaBean();
				vo.set(this.email_field,toAddr.get(i));
				list0.add(vo);
			}
			this.dAddrlist=list0;
			ArrayList list1=new ArrayList();
			if(ccList!=null)
			{
			for(int i=0;i<ccList.size();i++)
			{
				DynaBean vo=new LazyDynaBean();
				vo.set(this.email_field,ccList.get(i));
				list1.add(vo);
			}
			}
			this.ccAddrlist=list1;
		}
		Multipart mp = new MimeMultipart("related");
		/**MIME邮件对象*/
		MimeMessage mimeMsg = new MimeMessage(session);
		try
		{
		  mimeMsg.setSubject(topic);
		  /**邮件主体*/
		  if(!(bodylist==null||bodylist.size()==0))
		  {
			  for(int i=0;i<bodylist.size();i++)
			  {
			      String bodycontent = (String)bodylist.get(i);
			      if (useTemplate) {
                      bodycontent = applyTemplate(topic, bodycontent);
                  }
			      
				  BodyPart bp = new MimeBodyPart();		
				//问题bug号: 14051,生僻字解析不了
				  bp.setContent(bodycontent,"text/html;charset=UTF-8");
				  mp.addBodyPart(bp);
			  }
		  }
		  /**邮件附件*/
	      if(!(filelist==null||filelist.size()==0))
	      {
	    	 for(int j=0;j<filelist.size();j++)
	    	 {
	    	  FileDataSource fds = new FileDataSource((String)filelist.get(j));
	    	  BodyPart bp2 = new MimeBodyPart();
	    	  bp2.setFileName(MimeUtility.encodeText(fds.getName()));
	    	  bp2.setText(MimeUtility.encodeText(fds.getName()));
	    	  bp2.setDataHandler(new DataHandler(fds));
	    	  bp2.setHeader("Content-ID", "<" + MimeUtility.encodeText(fds.getName()) + ">");
			  mp.addBodyPart(bp2);	    	  
	    	 }
	      }
	      /**收件人*/
	      StringBuffer stremail=new StringBuffer();
	      for(int k=0;k<this.dAddrlist.size();k++)
	      {
	    	  DynaBean vo=(DynaBean)this.dAddrlist.get(k);
	    	  stremail.append(vo.get(this.email_field));
	    	  stremail.append(",");
	      }
   	      mimeMsg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(stremail.toString()));
   	      /**设置发信人*/
      	  mimeMsg.setFrom(new InternetAddress(sAddr));
      	  /**抄送人*/
      	  stremail.setLength(0);
      	  
	      for(int k=0;k<this.ccAddrlist.size();k++)
	      {
	    	  DynaBean vo=(DynaBean)this.ccAddrlist.get(k);
	    	  stremail.append(vo.get(this.email_field));
	    	  stremail.append(",");
	      }
	      mimeMsg.setRecipients(Message.RecipientType.CC,InternetAddress.parse(stremail.toString()));
	      /****************************/
   		  mimeMsg.setContent(mp);
   		  mimeMsg.saveChanges();      	  
   		  //Authenticator  authenticator=new Authenticator();
   		  this.transport.sendMessage(mimeMsg, mimeMsg.getAllRecipients());

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		
		finally
		{
//			try
//			{
//				if(this.transport!=null)
//					this.transport.close();	
//			}
//			catch(Exception ex)
//			{
//				ex.printStackTrace();
//			}
		}
	}
	
	
	/**
	 * 对单个人发送邮件(根据每个发送者的邮件地址发送邮件)
	 * @param topic 标题
	 * @param bodycontent 正文(可以是网页格式)
	 * @param filename 附近,文件名
	 * @param fromAddr 发送地址
	 * @param toAddr 目的地址
	 * @throws GeneralException
	 */
	public void sendEmail3(String topic,String bodycontent,String filename,String fromAddr,String fromPassword,String toAddr) throws GeneralException
	{
		
		this.sAddr=fromAddr;
		this.password=fromPassword;
		this.username=fromAddr;
		Multipart mp = new MimeMultipart("related");
		/**MIME邮件对象*/
		MimeMessage mimeMsg = new MimeMessage(session);
		try
		{
		  mimeMsg.setSubject(topic);
		  /**邮件主体*/
		  if(!(bodycontent==null|| "".equals(bodycontent)))
		  {
		      if (useTemplate) {
                  bodycontent = applyTemplate(topic, bodycontent);
              }
		      
			  BodyPart bp = new MimeBodyPart();		
			  bp.setContent(bodycontent,"text/html;charset=GB2312");
			  mp.addBodyPart(bp);
		  }
		  
		  /**邮件附件*/
	      if(!(filename==null|| "".equals(filename)))
	      {
	    	  FileDataSource fds = new FileDataSource(filename);
	    	  BodyPart bp2 = new MimeBodyPart();
	    	  bp2.setFileName(MimeUtility.encodeText(fds.getName()));
	    	  bp2.setText(MimeUtility.encodeText(fds.getName()));
	    	  bp2.setDataHandler(new DataHandler(fds));
	    	  bp2.setHeader("Content-ID", "<" + MimeUtility.encodeText(fds.getName()) + ">");
	    	  //bp2.setDisposition("");
			  mp.addBodyPart(bp2);	    	  
	      }
		  
	      /**收件人*/
   	      mimeMsg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(toAddr));
   	      /**设置发信人*/
      	  mimeMsg.setFrom(new InternetAddress(sAddr)); 
	      /****************************/
   		  mimeMsg.setContent(mp);
   		  mimeMsg.setSentDate(new Date());
   		  //mimeMsg.saveChanges();      	  
   		  //Authenticator  authenticator=new Authenticator();
   		  this.session=null;
    	  this.transport=null;
   		  //this.session = Session.getDefaultInstance(props,null);
   		  this.session =getSession();
		  this.session.setDebug(false);
 		  this.transport =this.session.getTransport("smtp");
 		  if(this.port!=null&&!"".equals(this.port)) {
              this.transport.connect((String)props.get("mail.smtp.host"),Integer.parseInt(this.port),MimeUtility.encodeText(sAddr),this.password);
          } else
 		  {
   	     	  this.transport.connect((String)props.get("mail.smtp.host"),MimeUtility.encodeText(sAddr),this.password);
 		  }
   		  transport.sendMessage(mimeMsg, mimeMsg.getAllRecipients());
   		  //transport.close();
		}
		catch(Exception ex)
		{
			String msg=ex.getMessage();
			if(msg.startsWith("553")) {
                throw new GeneralException(ResourceFactory.getProperty("error.stmp.authorized"));
            }
			if(msg.startsWith("354")) {
                throw new GeneralException(ResourceFactory.getProperty("label.sendmail.fail"));
            }
			//xus 18/2/5邮件服务器不存在 不抛异常只记录日志。
			log.error(msg);
//			ex.printStackTrace();
//			throw GeneralExceptionHandler.Handle(ex);
		}		
	}
	
	
	
	/**
	 * 对单个人发送邮件(根据每个发送者的邮件地址发送邮件)
	 * @param topic 标题
	 * @param bodycontent 正文(可以是网页格式)
	 * @param filename 附近,文件名
	 * @param fromAddr 发送地址
	 * @param toAddr 目的地址
	 * @throws GeneralException
	 */
	public void sendEmail2(String topic,String bodycontent,String filename,UserView userView,String toAddr) throws GeneralException
	{
		/**根据人员编码从实际的数据库查得对应的电子邮箱的地址*/
		try
		{
			userView = new UserView(userView.getUserName(), this.conn);
			if(!userView.canLogin(false)) {
                return;
            }
			
			this.sAddr=userView.getUserEmail(); 
			if(this.sAddr==null||this.sAddr.trim().length()==0) {
                return;
            }
			
			this.password=userView.getPassWord();
			this.username=userView.getUserEmail();
			if(!this.bflag)
			{
				DynaBean vo=getRealEmailAddr(toAddr);
				toAddr=(String)vo.get(this.email_field);
			}
			
			Multipart mp = new MimeMultipart("related");
			/**MIME邮件对象*/
			MimeMessage mimeMsg = new MimeMessage(session);
			try
			{
			  mimeMsg.setSubject(topic);
			  /**邮件主体*/
			  if(!(bodycontent==null|| "".equals(bodycontent)))
			  {
			      if (useTemplate) {
                    bodycontent = applyTemplate(topic, bodycontent);
                  }
			      
				  BodyPart bp = new MimeBodyPart();		
				  bp.setContent(bodycontent,"text/html;charset=GB2312");
				  mp.addBodyPart(bp);
			  }
			  /**邮件附件*/
		      if(!(filename==null|| "".equals(filename)))
		      {
		    	  FileDataSource fds = new FileDataSource(filename);
		    	  BodyPart bp2 = new MimeBodyPart();
		    	  bp2.setFileName(MimeUtility.encodeText(fds.getName()));
		    	  bp2.setText(MimeUtility.encodeText(fds.getName()));
		    	  bp2.setDataHandler(new DataHandler(fds));
		    	  bp2.setHeader("Content-ID", "<" + MimeUtility.encodeText(fds.getName()) + ">");
		    	  //bp2.setDisposition("");
				  mp.addBodyPart(bp2);	    	  
		      }
			  
		      /**收件人*/
	   	      mimeMsg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(toAddr));
	   	      /**设置发信人*/
	      	  mimeMsg.setFrom(new InternetAddress(sAddr)); 
		      /****************************/
	   		  mimeMsg.setContent(mp);
	   		  mimeMsg.setSentDate(new Date());
	   		  //mimeMsg.saveChanges();      	  
	   		  //Authenticator  authenticator=new Authenticator();
	   		  this.session=null;
	    	  this.transport=null;
	   		 // this.session = Session.getDefaultInstance(props,null);
	    	  this.session =getSession();
			  this.session.setDebug(false);
	 		  this.transport =this.session.getTransport("smtp");
	 		  if(this.port!=null&&!"".equals(this.port)) {
                  this.transport.connect((String)props.get("mail.smtp.host"),Integer.parseInt(this.port),MimeUtility.encodeText(sAddr),this.password);
              } else {
                  this.transport.connect((String)props.get("mail.smtp.host"),MimeUtility.encodeText(sAddr),this.password);
              }
	   		  transport.sendMessage(mimeMsg, mimeMsg.getAllRecipients());
	   		  //transport.close();
			}
			catch(Exception ex)
			{
				String msg=ex.getMessage();
				if(msg.startsWith("553")) {
                    throw new GeneralException(ResourceFactory.getProperty("error.stmp.authorized"));
                }
				if(msg.startsWith("354")) {
                    throw new GeneralException(ResourceFactory.getProperty("label.sendmail.fail"));
                }
				//xus 18/2/5邮件服务器不存在 不抛异常只记录日志。
				log.error(msg);
//				ex.printStackTrace();
//				throw GeneralExceptionHandler.Handle(ex);
			}	
		}
		catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	/**
	 * 对单个人发送邮件
	 * @param topic 标题
	 * @param bodycontent 正文(可以是网页格式)
	 * @param filename 附近,文件名
	 * @param fromAddr 发送地址
	 * @param toAddr 目的地址
	 * @throws GeneralException
	 */
	public void sendEmail(String topic,String bodycontent,String filename,String fromAddr,String toAddr) throws GeneralException
	{
	    ArrayList fileList = new ArrayList();
	    if (null != filename && !"".equals(filename)) {
            fileList.add(filename);
        }
		sendEmail(topic, bodycontent, fileList, fromAddr, toAddr);
	}
	
	/**
     * 对单个人发送邮件 zxj 20141023
     * @param topic 标题
     * @param bodycontent 正文(可以是网页格式)
     * @param fileList 附近,文件名
     * @param fromAddr 发送地址
     * @param toAddr 目的地址
     * @throws GeneralException
     */
    public void sendEmail(String topic, String bodycontent, ArrayList fileList, String fromAddr, String toAddr)
            throws GeneralException {
        /**根据人员编码从实际的数据库查得对应的电子邮箱的地址*/
        if ("0".equalsIgnoreCase(this.authy)) {
            if (!this.bflag) {
                DynaBean vo = null;
                if (fromAddr.trim().length() > 0) {
                    vo = getRealEmailAddr(fromAddr);
                }
                
                if (vo == null) {
                    throw new GeneralException(ResourceFactory.getProperty("error.notlink.a0100"));
                }
                
                this.sAddr = (String) vo.get(this.email_field);
                vo = getRealEmailAddr(toAddr);
                toAddr = (String) vo.get(this.email_field);
                if (toAddr == null || toAddr.length() == 0) {
                    throw new GeneralException(vo.get("A0101").toString() + "邮件地址不能为空");
                }
            } else {
                this.sAddr = fromAddr;
            }
        } else {
            if (!this.bflag) {
                DynaBean vo = null;
                if (fromAddr.trim().length() > 0) {
                    vo = getRealEmailAddr(fromAddr);
                }
                
                if (vo == null) {
//                	//xus 18/2/5邮件服务器不存在 不抛异常只记录日志。
                	this.log.error(ResourceFactory.getProperty("error.notlink.a0100"));
                	return;
//                    throw new GeneralException(ResourceFactory.getProperty("error.notlink.a0100"));
                }
                
                this.sAddr = (String) vo.get(this.email_field);
                vo = getRealEmailAddr(toAddr);
                toAddr = (String) vo.get(this.email_field);
                if (toAddr == null || toAddr.length() == 0) {
                    throw new GeneralException(vo.get("A0101").toString() + "邮件地址不能为空");
                }
            } else {
                if (fromAddr != null && fromAddr.length() > 0) {
                    this.sAddr = fromAddr;
                }
            }
        }

        Multipart mp = new MimeMultipart("related");
        /**MIME邮件对象*/
        MimeMessage mimeMsg = new MimeMessage(session);
        try {
            mimeMsg.setSubject(topic);

            /**邮件主体*/
             if (!(bodycontent == null || "".equals(bodycontent))) {
                if (useTemplate) {
                    bodycontent = applyTemplate(topic, bodycontent);
                }
                
                BodyPart bp = new MimeBodyPart();
                //xus  17/9/26邮件编码格式错乱
                bp.setContent(bodycontent, "text/html;charset=utf-8");
                mp.addBodyPart(bp);
            }
            /**邮件附件*/
            if (!(fileList == null || fileList.size() == 0)) {
                for (int j = 0; j < fileList.size(); j++) {
                    FileDataSource fds = new FileDataSource((String) fileList.get(j));
                    BodyPart bp2 = new MimeBodyPart();
                    bp2.setFileName(MimeUtility.encodeText(fds.getName()));
                    bp2.setText(MimeUtility.encodeText(fds.getName()));
                    bp2.setDataHandler(new DataHandler(fds));
                    bp2.setHeader("Content-ID", "<" + MimeUtility.encodeText(fds.getName()) + ">");
                    mp.addBodyPart(bp2);
                }
            }

            /**收件人*/
            mimeMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddr));
            /**设置发信人*/
            InternetAddress internetAddress=new InternetAddress(sAddr);
			//添加发件人昵称
			if(StringUtils.isNotBlank(this.sendername)){
				internetAddress.setPersonal(this.sendername);
			}
            mimeMsg.setFrom(internetAddress);
            /****************************/
            //xus  17/9/26邮件编码格式错乱
            mimeMsg.setContent(mp,"text/html;charset=utf-8");
            mimeMsg.setSentDate(new Date());
            //xus 18/2/5邮件服务器不存在 不抛异常只记录日志。
        	if (sAddr == null && sAddr.length() <= 0){
            	this.log.error("邮件服务器不存在 ");
            	return;
        	}
            transport.sendMessage(mimeMsg, mimeMsg.getAllRecipients());
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (msg.startsWith("553")) {
                throw new GeneralException(ResourceFactory.getProperty("error.stmp.authorized"));
            }
            if (msg.startsWith("354")) {
                throw new GeneralException(ResourceFactory.getProperty("label.sendmail.fail"));
            }
          //xus 18/2/5邮件服务器不存在 不抛异常只记录日志。
            log.error(msg);
//            ex.printStackTrace();
//            throw GeneralExceptionHandler.Handle(ex);
        }
    }
	
	/**
	 * 根据邮件服务器中定义的帐号发送邮件
	 * @param topic
	 * @param bodycontent
	 * @param filename
	 * @param toAddr
	 * @throws GeneralException
	 */
	public void sendEmail(String topic,String bodycontent,String filename,String toAddr) throws GeneralException
	{
		this.sendEmail(topic, bodycontent, filename,this.sAddr,toAddr);
	}	
	

	/**
	 * 关闭连接通道
	 */
	public void close()
	{
		try
		{
			if(this.transport!=null) {
                this.transport.close();
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}		
	}
	/**
	 * 取得实际的电子信箱地址
	 * @param a0100List
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList getRealEmailAddr(List a0100List)throws GeneralException
	{
		ArrayList addrlist=null;
		DynaBean  bean=null;
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		try
		{
			rset=dao.search(getSqlByA0100List(a0100List));
			addrlist=new ArrayList();
			while(rset.next())
			{
				bean=new LazyDynaBean();
				bean.set("A0101",rset.getString("A0101"));
				bean.set(this.email_field,rset.getString(this.email_field));
				addrlist.add(bean);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return addrlist;
	}

	/**
	 * 求得A0100对应的EMAIL ADDRESS
	 * @param a0100 应用库前缀+人员编码
	 * @return DynaBean 两个属性，一个为姓名A0101 ,一个为
	 * @throws GeneralException
	 */
	private DynaBean getRealEmailAddr(String a0100)throws GeneralException
	{
		DynaBean  bean=null;
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		try
		{
			rset=dao.search(getSqlByA0100(a0100));
			if(rset.next())
			{
				bean=new LazyDynaBean();
				bean.set("A0101",rset.getString("A0101"));
				bean.set(this.email_field,rset.getString(this.email_field)==null?"":rset.getString(this.email_field));
				bean.set("password", rset.getString("userpassword")!=null?rset.getString("userpassword"):"");
			}
			else//如果未找到按真实地址处理
			{
				bean=new LazyDynaBean();
				bean.set("A0101",a0100);
				bean.set(this.email_field,a0100);	
				bean.set("password","");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bean;
	}
	
	/**
	 * 根据人员编码取得对应的实际电子邮箱的地址
	 * @param a0100  应用库前缀＋人员编码
	 * @return
	 * @throws GeneralException
	 */
	public String getEmailAddrByA0100(String a0100)throws GeneralException
	{
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		String addr=null;
		try
		{
			rset=dao.search(getSqlByA0100(a0100));
			if(rset.next()) {
                addr=rset.getString(this.email_field);
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return addr;
	}
	
	/**
	 * 根据登录用户求电子邮箱地址,operuser
	 * @param logonname
	 * @return
	 * @throws GeneralException
	 */
	public String getEmailAddrByLogon(String logonname)throws GeneralException
	{
		ContentDAO dao=new ContentDAO(this.conn);
		StringBuffer buf=new StringBuffer();
		RowSet rset=null;
		String addr=null;
		try
		{
			ArrayList paralist=new ArrayList();
			paralist.add(logonname);
			
			buf.append("select email from operuser where username=?");
			rset=dao.search(buf.toString(),paralist);
			if(rset.next()) {
                addr=rset.getString("email");
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return addr;
	}
	
    private String applyTemplate(String subject, String bodyText) {
        StringBuffer buf = new StringBuffer();
        buf.append("<!DOCTYPE html>\n");
        buf.append("<html>\n");
        buf.append("<head>\n");
        buf.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />\n");
        buf.append("</head>\n");
        buf.append("<body>\n");
        buf.append("\t<center><div style='text-align:center;width:600px;background:#F4F4F4;margin:0px auto;padding:40px 20px 40px 20px;'>\n");
        buf.append("\t\t<div style='width:540px;text-align:left;background:#FFFFFF;padding:40px 30px 70px 30px;'>\n");
        //主题
        buf.append("\t\t\t<h2 style='color:#929292;font-size:30px;font-weight:normal; border-bottom:1px #E2E2E2 solid;padding-bottom:16px; font-family:微软雅黑'>");
        if (null == subject || "".equals(subject)) {
            subject = "通知";
        }
        buf.append(subject);
        buf.append("</h2>\n");
        
        //正文
        // 添加样式white-space:pre;保留模板定义时的空白 lium
        buf.append("\t\t\t<pre><div style='font-size:14px; font-weight:bolder;margin-top:40px;white-space:pre-wrap;*white-space:pre;*word-wrap:break-word;'>");
        if (null == bodyText || "".equals(bodyText)) {
            bodyText = "无通知内容。";
        }
        buf.append(bodyText);
        buf.append("</div></pre>\n");
        
        //链接
        if (null != this.getTemplateHref() && !"".equals(this.getTemplateHref())) {
            buf.append("\t\t\t<div style='margin-top:40px;border-bottom:1px #E2E2E2 solid;padding-bottom:30px;'>\n");
            buf.append("\t\t\t\t<a href='");
            buf.append(this.getTemplateHref());
            buf.append("' style='display:#display#;background:#FF7F1E;padding:0 8px;height:30px;line-height:30px;text-align:center;color:#FFF;text-decoration:none;'>");
            buf.append(this.getTemplateHrefDesc());
            buf.append("</a>\n");
            buf.append("\t\t\t</div>\n");
        }
        
        //系统信息
        buf.append("\t\t\t<div style='margin-top:30px;color:#BFBFBF;'>本邮件由e-HR自动发出，请勿回复。</div>\n");
        buf.append("\t\t\t<div style='margin-top:15px;color:#BFBFBF; float:right;'>发送时间：");
        buf.append(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        buf.append("</div>\n");
        
        buf.append("\t\t</div>\n");
        buf.append("\t</div></center>\n");
        buf.append("</body>\n");
        buf.append("</html>\n");
        
        return buf.toString();
    }
    
	public boolean isBflag() {
		return bflag;
	}

	public List getCcAddrlist() {
		return ccAddrlist;
	}

	public void setCcAddrlist(ArrayList ccAddrlist) {
		this.ccAddrlist = ccAddrlist;
	}

	public List getDAddrlist() {
		return dAddrlist;
	}

	public void setDAddrlist(ArrayList addrlist) {
		dAddrlist = addrlist;
	}

	public String getSAddr() {
		return sAddr;
	}

	public void setSAddr(String addr) {
		sAddr = addr;
	}

	public void setBflag(boolean bflag) {
		this.bflag = bflag;
	}

	public String getDbase() {
		return dbase;
	}

	public void setDbase(String dbase) {
		this.dbase = dbase;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setUseTemplate(boolean usedTemplate) {
	    this.useTemplate = usedTemplate;
	}
    public void setTemplateHref(String templateHref) {
        this.templateHref = templateHref;
    }
    public String getTemplateHref() {
        return templateHref;
    }
    public void setTemplateHrefDesc(String templateHrefDesc) {
        this.templateHrefDesc = templateHrefDesc;
    }
    public String getTemplateHrefDesc() {
        return null == templateHrefDesc ? "查看详情" : templateHrefDesc ;
    }
}

