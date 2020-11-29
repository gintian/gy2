package com.hjsj.hrms.businessobject.attestation.zjz;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SendEmailFormOA {
	//private Connection conn;
	private String idCardField="";
	//private ContentDAO oa_dao;
	private ContentDAO hrp_dao;
	private Connection hrconn;
	private String phono_num="";
	//private String userfield="";
	//private String pWfield="";
    public SendEmailFormOA(Connection hrpconn)
    {
    	//this.conn=getConnection();
    	
    	this.hrp_dao=new  ContentDAO(hrpconn);
    	this.hrconn=hrpconn;
    	this.idCardField=getIdCardField(hrpconn);    	
    	this.phono_num=getPhonoNumField();
    }
    /*private void getUserNamePassWordField()
    {
    	 RecordVo user_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
	     String fielduser=user_vo.getString("str_value");
	     //验证用户名和密码字段
	     String Userfield="username";
	     String PWfield="UserPassword";
		 if(fielduser !=null && fielduser.indexOf(",")>0  && fielduser.indexOf("#")==-1)
		 {
			 Userfield=fielduser.substring(0,fielduser.indexOf(","));
			 PWfield=fielduser.substring(fielduser.indexOf(",")+1);
		 }
    	 this.userfield=userfield;
    	 this.pWfield=pWfield;
    }*/
	
	private Connection getConnection(String jndanName)
	{
		Connection conn=null;
		try
		{
			conn=AdminDb.getConnection(jndanName);	
			
		}		
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		return conn;		
	}
	/**
	 * 发送邮件
	 * @param list,list中包含LazyDynaBean，其中有三个属性，分别是a0100,nbase,url
	 * @param message发送邮件信息
	 */
	public void sendEmail(ArrayList list,LazyDynaBean senduser,String title,String message)
	{
		Connection conn=null;
		ContentDAO oa_dao=null;
		Date date=new Date();
		try
		{
			conn=getConnection("ZJOAMSSQL");
			Category.getInstance("com.hjsj.hrms.businessobject.attestation.zjz.SendEmailFormOA").debug(
			"发送邮件:"+DateUtils.format(date, "yyyy.MM.dd HH:mm"));

	    	oa_dao=new ContentDAO(conn);
			if(list==null||list.size()<0) {
                return ;
            }
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean hrbean =(LazyDynaBean)list.get(i);
				Category.getInstance("com.hjsj.hrms.businessobject.attestation.zjz.SendEmailFormOA").debug(
						"发送邮件-A0100:"+hrbean.get("nbase")+hrbean.get("a0100"));
				//System.out.println("发送邮件-A0100:"+hrbean.get("nbase")+hrbean.get("a0100"));

				LazyDynaBean basebean=getHrpUserBean(hrbean);
				if(basebean==null) {
                    continue;
                }
				String idcard=(String)basebean.get("idcard");

				Category.getInstance("com.hjsj.hrms.businessobject.attestation.zjz.SendEmailFormOA").debug(
						"发送邮件-身份证号:"+idcard);
				//System.out.println("发送邮件-身份证号:"+idcard);

				//String idcard=getIdCard(hrbean);
				if(idcard==null||idcard.length()<=0) {
                    continue;
                }
				//得到电话号码
				LazyDynaBean userbean=getOAUserMessage(idcard,oa_dao);
				if(userbean==null) {
                    continue;
                }
				int mailid=getMailID(oa_dao);
				Category.getInstance("com.hjsj.hrms.businessobject.attestation.zjz.SendEmailFormOA").debug(
						"发送邮件-发送邮件:mailid="+mailid+";OAname="+userbean.get("OAname"));
				//System.out.println("发送邮件-发送邮件:mailid="+mailid+";OAname="+userbean.get("OAname"));


				saveMailInfo(userbean,mailid,title,hrbean,message,basebean,oa_dao);
				saveMailSendReceive(userbean,mailid,2,oa_dao);//收件人记录
				//sendSms(hrbean);
				if(senduser!=null)
				{
					String aidcard=getIdCard(senduser);
					if(aidcard!=null&&aidcard.length()>0)
					{
						LazyDynaBean auserbean=getOAUserMessage(aidcard,oa_dao);
						if(auserbean!=null)
						{
							Category.getInstance("com.hjsj.hrms.businessobject.attestation.zjz.SendEmailFormOA").debug(
									"发送邮件-发件人:auserbean="+auserbean.get("name"));	
							//System.out.println("发送邮件-发件人:auserbean="+auserbean.get("name"));	
							saveMailSendReceive(auserbean,mailid,1,oa_dao);//发件人记录

						}
					}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			close(conn);
		}
	}
	/**
	 * 发送邮件
	 * @param list,list中包含LazyDynaBean，其中有三个属性，分别是a0100,nbase,url
	 * @param message发送邮件信息
	 */
	public void sendEmail(ArrayList list,String senduser,String title,String message)throws GeneralException
	{
		Connection conn=null;
		ContentDAO oa_dao=null;	
		Date date=new Date();
		try
		{
			conn=getConnection("ZJOAMSSQL");

			Category.getInstance("com.hjsj.hrms.businessobject.attestation.zjz.SendEmailFormOA").debug(
					"发送邮件:"+DateUtils.format(date, "yyyy.MM.dd HH:mm"));

	    	oa_dao=new ContentDAO(conn);
			if(list==null||list.size()<0) {
                return ;
            }
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean hrbean =(LazyDynaBean)list.get(i);
				Category.getInstance("com.hjsj.hrms.businessobject.attestation.zjz.SendEmailFormOA").debug(
						"发送邮件-A0100:"+hrbean.get("nbase")+hrbean.get("a0100"));
				//System.out.println("发送邮件-A0100:"+hrbean.get("nbase")+hrbean.get("a0100"));	

				LazyDynaBean basebean=getHrpUserBean(hrbean);
				if(basebean==null) {
                    continue;
                }
				
				String idcard=(String)basebean.get("idcard");
				//String idcard=getIdCard(hrbean);
				if(idcard==null||idcard.length()<=0) {
                    continue;
                }
				Category.getInstance("com.hjsj.hrms.businessobject.attestation.zjz.SendEmailFormOA").debug(
						"发送邮件-身份证号:"+idcard);
				//System.out.println("发送邮件-身份证号:"+idcard);

				//得到电话号码
				LazyDynaBean userbean=getOAUserMessage(idcard,oa_dao);
				if(userbean==null) {
                    continue;
                }
				int mailid=getMailID(oa_dao);	
				Category.getInstance("com.hjsj.hrms.businessobject.attestation.zjz.SendEmailFormOA").debug(
						"发送邮件-发送邮件:mailid="+mailid+";OAname="+userbean.get("OAname"));	
				Category.getInstance("com.hjsj.hrms.businessobject.attestation.zjz.SendEmailFormOA").debug(
						"发送邮件-发件人:senduser="+senduser);	
				//System.out.println("发送邮件-发件人:senduser="+senduser);	
				//System.out.println("发送邮件-发送邮件:mailid="+mailid+";OAname="+userbean.get("OAname"));	
				saveMailInfo(userbean,mailid,title,hrbean,message,basebean,oa_dao);				
				saveMailSendReceive(userbean,mailid,2,oa_dao); //收件人记录			
				saveMailSendReceive("-1",senduser,mailid,1,oa_dao);//发件人记录

			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally
		{
			close(conn);
		}
	}
	private void  sendSms(LazyDynaBean bean)
	{
		//发送电话号码
	    if(bean==null) {
            return;
        }
		String a0100=(String)bean.get("a0100");
		String nbase=(String)bean.get("nbase");
	    if(a0100==null||a0100.length()<=0||nbase==null||nbase.length()<=0) {
            return;
        }
	    if(phono_num==null||phono_num.length()<=0|| "#".equals(phono_num)) {
            return ;
        }
	    String sql="select "+this.phono_num+" phono from "+nbase+"A01 where a0100='"+a0100+"'";
	    String phono="";
	    RowSet rs=null;
	    try
	    {
	    	 rs=this.hrp_dao.search(sql);
	    	 if(rs.next()) {
                 phono=rs.getString("phono");
             }
	    }catch(Exception e)
	    {
	    	 e.printStackTrace();
	    }finally
	    {
	    	if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	    }
	    if(phono!=null&&phono.length()>0)
	    {
	    	
	    }
	}
	/**
	 * 编辑信息，添加单点登陆
	 * @param userbean
	 * @param message
	 * @return
	 */
	private String  editContent(LazyDynaBean bean,String message,String name,String pass)
	{
		String url=(String)bean.get("url");
		if(url==null||url.length()<=0) {
            return message;
        }
		StringBuffer buf=new StringBuffer();
		buf.append(message);
		buf.append("<br>");
		buf.append("<br>");
		buf.append("<br>");
		buf.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		try {
			buf.append("链接：<a href='"+SystemConfig.getProperty("sso_logon_url"));
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		//etoken已被邓灿在url中添加，此处不需要了。
	//	String etoken=PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(name+","+pass));		
	//	buf.append(url+"&etoken="+etoken+"&validatepwd=false' target=\"_blank\">");
		buf.append(url+"' target=\"_blank\">");
		buf.append("<b>进入E-HR系统<b></a>");
		return buf.toString();
	}
	/**
	 * 
	 * @param userbean
	 * @param intMailID
	 * @param strContent
	 */
	private void saveMailSendReceive(LazyDynaBean userbean,int intMailID,int mailsort,ContentDAO oa_dao)throws GeneralException
	{
		/*userid是用户id
		folderid不清楚
		mailsort是区分发信人（1）还是收信人（2）
		mailid是邮件的id号
		usernameoraddress是发信人的中文名
		orderid不清楚，reflag不清楚
		mailnewflag是否新邮件，maildeletelag是否删除*/
		String intAutoReFlag="1";
		String FolderID="2";
		if(mailsort==1) {
            FolderID="3";
        }
		StringBuffer strSql=new StringBuffer();		
		strSql.append("Insert Into MailSendReceive (UserID,FolderID,MailSort,MailID,");
		strSql.append("UserNameOrAddress,OrderID,ReFlag,MailNewFlag,MailDeleteFlag)"); 
		strSql.append("Values ('"+userbean.get("id")+"',"+FolderID+","+mailsort+",'"+intMailID+"','"+userbean.get("name")+"',1,'"+intAutoReFlag+"',1,0)");
		//System.out.println(strSql.toString());
		Category.getInstance("com.hjsj.hrms.businessobject.attestation.zjz.SendEmailFormOA").debug(
				"发送邮件-saveMailSendReceive="+strSql.toString());	
		try {
			oa_dao.insert(strSql.toString(),new ArrayList());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	private void saveMailSendReceive(String userid,String name,int intMailID,int mailsort,ContentDAO oa_dao)throws GeneralException
	{
		/*[UserID] [int] NOT NULL ,收件人OA用户ID -1表示管理员
		[FolderID] [int] NOT NULL ,发件人为3，其他为2
		[MailSort] [int] NOT NULL ,1为发件人，2为收件人
		[MailID] [int] NOT NULL ,邮件ID号
		[UserNameOrAddress] [varchar] (200) COLLATE Chinese_PRC_CI_AS NOT NULL ,收件人姓名
		[OrderID] [int] NOT NULL ,默认为1
		[ReFlag] [bit] NOT NULL ,默认为1
		[MailNewFlag] [bit] NOT NULL ,1为未读邮件，0为已读邮件
		[MailDeleteFlag] [bit] NOT NULL 1为删除，0为为删除*/
		String intAutoReFlag="1";
		String FolderID="2";
		if(mailsort==1) {
            FolderID="3";
        }
		StringBuffer strSql=new StringBuffer();		
		strSql.append("Insert Into MailSendReceive (UserID,FolderID,MailSort,MailID,");
		strSql.append("UserNameOrAddress,OrderID,ReFlag,MailNewFlag,MailDeleteFlag)"); 
		strSql.append("Values ('"+userid+"',"+FolderID+","+mailsort+",'"+intMailID+"','"+name+"',1,'"+intAutoReFlag+"',1,0)");
		//System.out.println(strSql.toString());
		Category.getInstance("com.hjsj.hrms.businessobject.attestation.zjz.SendEmailFormOA").debug(
				"发送邮件-saveMailSendReceive="+strSql.toString());	
		try {
			oa_dao.insert(strSql.toString(),new ArrayList());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 存放邮件,记录邮件信息
	 * @param intMailID
	 * @param strContent
	 * [MailID] [int] NOT NULL ,
	 * [Title] [varchar] (255) COLLATE Chinese_PRC_CI_AS NOT NULL ,邮件主题
	 * [LevelID] [int] NOT NULL ,优先级
	 * [MailDate] [datetime] NOT NULL ,发送时间
	 * [AutoReFlag] [bit] NOT NULL ,自动回复
	 * [MailSideFlag] [bit] NOT NULL ,
	 * [AttachSize] [int] NOT NULL ,附件大小
	 * [MailText] [text] COLLATE Chinese_PRC_CI_AS NOT NULL 邮件正文
	 */
	private void saveMailInfo(LazyDynaBean userbean,int intMailID,String strTitle,LazyDynaBean hrbean,String message,LazyDynaBean basebean,ContentDAO oa_dao)throws GeneralException
	{
		StringBuffer strSql=new StringBuffer();
		if(strTitle==null||strTitle.length()<=0) {
            strTitle="eHR人事异动通知";
        }
		String intLevelID="1";//优先级：1：高级；2：中级；3：低级
		String intAutoReFlag="0";//自动回复，0不回复，1回复
		strSql.append("Insert Into MailInfo(MailID,Title,LevelID,MailDate,AutoReFlag,MailSideFlag,AttachSize,MailText)");
		strSql.append(" Values('"+intMailID+"','"+strTitle+"','"+intLevelID+"',getdate(),'"+intAutoReFlag+"',");
		strSql.append("0,0,?)"); 
		ArrayList list=new ArrayList();
		String loginname=(String)userbean.get("OAname");
		String loginpass=(String)userbean.get("OApass");
		String strContent=editContent(hrbean,message,loginname,loginpass);
		//System.out.println(strContent);
		list.add(strContent);	
		//System.out.println(strSql.toString());
		
		Category.getInstance("com.hjsj.hrms.businessobject.attestation.zjz.SendEmailFormOA").debug(
				"发送邮件-saveMailInfo="+strSql.toString());	
		Category.getInstance("com.hjsj.hrms.businessobject.attestation.zjz.SendEmailFormOA").debug(
				"发送邮件-strContent="+strContent);	
		try {
			oa_dao.insert(strSql.toString(),list);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 取得当前的最大MailID号
	 * @return
	 */
	private int getMailID(ContentDAO oa_dao)
	{
		String strSql="select isnull(max(MailID),0)+1 as MailID from MailInfo Where MailID >0 ";
		int mailid=1;
		RowSet rs=null;
		try {
			rs=oa_dao.search(strSql);
			if(rs.next()) {
                mailid=rs.getInt("MailID");
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}			
		return mailid;
			
	}
	/**
	 * 得到主集中存放身份证号的字段
	 * @return
	 */
	private String getIdCardField(Connection hrpconn)
	{
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(hrpconn);
		String field=sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name");		
		return field;
	}
	/**
	 * 得到oa中的人员信息
	 * @param idcard
	 * @return
	 */
	private LazyDynaBean getOAUserMessage(String idcard,ContentDAO oa_dao)
	{
		StringBuffer sql=new StringBuffer();
		sql.append("select UserID,UserNameCn,UserNameEn,PassWord from QJUserInfo where sfzh='"+idcard+"'");
		LazyDynaBean bean =null;
		RowSet rs=null;
		try {
			 rs=oa_dao.search(sql.toString());				
			if(rs.next())
			{
				bean=new LazyDynaBean();
				bean.set("id", rs.getString("UserID"));
				bean.set("name", rs.getString("UserNameCn"));				
				bean.set("sfzh", idcard);
				bean.set("OAname", rs.getString("UserNameEn"));
				String pass=rs.getString("PassWord");
				if(pass!=null&&pass.length()>0) {
                    pass=encrypt(pass);
                }
				bean.set("OApass", pass);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}				
		return bean;
	}
	/**
	 * 通过hrp平台得到身份证号
	 * @param bean
	 * @return
	 */
	private LazyDynaBean getHrpUserBean(LazyDynaBean bean)
	{
		String a0100=(String)bean.get("a0100");
		String nbase=(String)bean.get("nbase");
		StringBuffer sql=new StringBuffer();
		String idcard="";
		if(a0100==null||a0100.length()<=0||nbase==null||nbase.length()<=0) {
            return null;
        }
		/*sql.append("select username,password from operuser where a0100='"+a0100+"' and nbase='"+nbase+"'");
		//System.out.println(sql.toString());
		RowSet rs=null;
		String name="";
		String pass="";
		LazyDynaBean userbane=new LazyDynaBean();
		boolean istarner=false;
		try {
			rs=this.hrp_dao.search(sql.toString());
			if(rs.next())
			{
				name=rs.getString("username");
				pass=rs.getString("password");
				istarner=true;
			}				
			if(name==null||name.length()<=0)
				return null;
			sql.delete(0, sql.length());
			sql.append("select "+this.userfield+" username ,"+this.pWfield+" password, ");
			sql.append(this.idCardField+" as idcard");
			sql.append(" from "+nbase+"A01 where a0100='"+a0100+"'");
			sql.append("select "+this.idCardField+" as idcard from "+nbase+"A01 where a0100='"+a0100+"'");		
			rs=this.hrp_dao.search(sql.toString());
			if(rs.next())
			{
				if(!istarner)
				{
					name=rs.getString("username");
					pass=rs.getString("password");
				}
				idcard=rs.getString("idcard");
			}
			if(name==null||name.length()<=0)
				return null;
			if(idcard==null||idcard.length()<=0)					
				return null;
			if(pass==null||pass.length()<=0)
				pass="";
			userbane.set("name", name);
			userbane.set("pass", pass);
			userbane.set("idcard", idcard);
			userbane.set("a0100", a0100);
			userbane.set("nbase", nbase);			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		LazyDynaBean userbane=new LazyDynaBean();
		RowSet rs=null;
		sql.append("select "+this.idCardField+" as idcard from "+nbase+"A01 where a0100='"+a0100+"'");		
		try {
			rs=this.hrp_dao.search(sql.toString());
			if(rs.next())
			{
				idcard=rs.getString("idcard");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		if(idcard==null||idcard.length()<=0) {
            return null;
        }
		userbane.set("idcard", idcard);
		userbane.set("a0100", a0100);
		userbane.set("nbase", nbase);
		return userbane;
	}
	/**
	 * 通过hrp平台得到身份证号
	 * @param bean
	 * @return
	 */
	private String getIdCard(LazyDynaBean bean)
	{
		String a0100=(String)bean.get("a0100");
		String nbase=(String)bean.get("nbase");
		StringBuffer sql=new StringBuffer();
		String idcard="";
		if(a0100==null||a0100.length()<=0||nbase==null||nbase.length()<=0) {
            return null;
        }
		sql.append("select "+this.idCardField+" as idcard from "+nbase+"A01 where a0100='"+a0100+"'");		
		RowSet rs=null;
		try {
			rs=this.hrp_dao.search(sql.toString());
			if(rs.next())
			{
				idcard=rs.getString("idcard");
				if(idcard==null||idcard.length()<=0) {
                    return null;
                }
			}else {
                return null;
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}		
		return idcard;
	}
	private void close(Connection conn)
	{
		if(conn!=null)
		{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 得到电话号码
	 * @return
	 */
	private String  getPhonoNumField()
	{
		String sql="select str_value from constant where Constant='SS_MOBILE_PHONE'";
		List phonelist=ExecuteSQL.executeMyQuery(sql,this.hrconn);
		String phone="";
		try{
		  if(!phonelist.isEmpty())
		  {
		 	LazyDynaBean rec=(LazyDynaBean)phonelist.get(0);
		 	phone=rec.get("str_value")!=null?rec.get("str_value").toString():"";
		  }	
		  if(phone==null|| "#".equals(phone)) {
              phone="";
          }
		}catch(Exception e)
		{
			//e.printStackTrace();
		}
		return phone;
	}
	private String encrypt(String strUnEncrypt)//232336;454855 ;#C!PVG
	 {
	    	int length=strUnEncrypt.length();
	    	String strEncrypt="";
	    	String strTemp="";
	    	int iTemp=0;
	    	for(int i=0;i<length;i++)
	    	{
	    		strTemp=strUnEncrypt.substring(i,i+1);
	    		int s=i+1;
	    		iTemp=s%5;
	    		char c=strUnEncrypt.charAt(i);
	    		int cc=(int)c;  
	    		int dd=0;
	    		char ch;
	    		switch(iTemp)
	    		{
	    		    case 0:
	    		      dd=c^1;    		      
	    		      strEncrypt=strEncrypt+(char)dd;
	    		      break;	
	    		    case 1:
	    		    	dd=c^2;    		    	
	    		    	strEncrypt=strEncrypt+(char)dd;
	    		    	break;	
	    		    case 2:
	    		    	dd=c^3;    		    	
	    		    	strEncrypt=strEncrypt+(char)dd;
	    		    	break;	
	    		    case 3:
	    		    	dd=c^2;    		    	
	    		    	strEncrypt=strEncrypt+(char)dd;
	    		    	break;	
	    		    case 4:
	    		    	dd=c^1;    		    	
	    		    	strEncrypt=strEncrypt+(char)dd;
	    		    	break;	
	    		}
	    	}
	    	return strEncrypt;
	    }
}
