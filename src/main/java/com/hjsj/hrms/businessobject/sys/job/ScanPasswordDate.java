package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hjsj.hrms.businessobject.sys.SysParamConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/**
 * 密码到期提醒功能会根据作业频率只要密码到期就会发送，即使已经发送过还会发送（目前程序没有记录发送过的邮件）
 * 建议使用场景为密码到期提醒语密码到期未修改锁定功能组合使用，密码到期未修改锁后邮件不会在发送
 *<p>Title:ScanPasswordDate.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Jun 5, 2009:5:40:20 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class ScanPasswordDate implements Job {

	@Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
		String passwordlockdays=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.PASSWORD_LOCK_DAYS);
		String passworddays=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.PASSWORDDAYS);
		if(passworddays.length()>0||passwordlockdays.length()>0){
			Connection conn = null;
			RowSet rs  =null;
			try{
				conn = (Connection)AdminDb.getConnection();
				ContentDAO dao = new ContentDAO(conn);
				//业务用户
				StringBuffer sql = new StringBuffer();
				sql.append("insert into t_sys_login_user_info (username,first_login,pwd_modtime) select username,'1',"+Sql_switcher.dateValue(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()))+" from operuser where RoleID<>1 and UserName not in(select UserName from t_sys_login_user_info)");
				dao.update(sql.toString());
				sql.setLength(0);
				//自助用户
				String username="username";
		        String password="userpassword";
				RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
		        if(login_vo!=null){
			        String login_name = login_vo.getString("str_value");
			        int idx=login_name.indexOf(",");
			        if(idx>3)
			        {
			        	username=login_name.substring(0,idx);
				        password=login_name.substring(idx+1);
			        }
			    }
		        login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
		        String pres="";
		        if(login_vo!=null) {
					pres = login_vo.getString("str_value").toLowerCase();
				}
		        String [] press = pres.split(",");
		        for(int i=0;i<press.length;i++){
		        	String pre = press[i];
		        	if(pre.length()==3){
		        		sql.setLength(0);
						sql.append("insert into t_sys_login_user_info (username,first_login,pwd_modtime) select distinct "+username+",'1',"+Sql_switcher.dateValue(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()))+" from "+pre+"a01 where "+username+" not in(select UserName from t_sys_login_user_info) and "+username+" is not null");
						if(Sql_switcher.searchDbServer()==1) {
							sql.append(" and "+username+"<>''");
						}
						dao.update(sql.toString());
		        	}
		        }
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				
				 try
				   {
					 if(rs!=null) {
						 rs.close();
					 }
					if(conn!=null) {
						conn.close();
					}
				   }
				   catch(Exception ex)
				   {
					   ex.printStackTrace();
				   }
			}
		}
        
		// TODO Auto-generated method stub
		//String passworddays = SystemConfig.getPropertyValue("passworddays");
		
		
		if(passworddays.length()>0){
			StringBuffer sql = new StringBuffer();
			/*sql.append("select a0100,email,modtime from operuser where ");
			switch(Sql_switcher.searchDbServer())
		    {
				  case Constant.ORACEL:
				  { 
					  sql.append(" modtime+5 <"+Sql_switcher.sqlNow()+"");
					  break;
				  } case Constant.DB2:
				  {
					  sql.append(" (modtime +5 day)< "+Sql_switcher.sqlNow()+"");
					  break;
				  }
				  default:
					  sql.append(" dateadd(day,"+passworddays+",modtime) < "+Sql_switcher.sqlNow()+"");
		    }*/
			
			
			Connection conn = null;
			RowSet rs  =null;
			try {
				conn = (Connection)AdminDb.getConnection();
				ContentDAO dao = new ContentDAO(conn);
				//业务用户
				sql.append("select t1.username,t2.email email from operuser t2 left join t_sys_login_user_info t1 on t1.username=t2.username where t2.email is not null");
				if(Sql_switcher.searchDbServer()==1){
					sql.append(" and t2.email<>'' ");
				}
				sql.append(" and t2.state=1");
				sql.append(" and "+Sql_switcher.diffDays(Sql_switcher.dateValue(new SimpleDateFormat("yyyy-MM-dd").format(new Date())),"t1.pwd_modtime")+">"+passworddays);
				rs = dao.search(sql.toString());
				String content = ResourceFactory.getProperty("job.ScanPasswordDate.content");//"您的密码已经很长时间没有更改过，建议您定期更改密码以保证账户安全！";
				String title=ResourceFactory.getProperty("job.ScanPasswordDate.title");
				EMailBo bo = new EMailBo(conn,true,"");
				String fromaddr=getFromAddr();
				while(rs.next()){
					String email = rs.getString("email");
					if(email==null||email.length()<1) {
						continue;
					}
					bo.sendEmail(title,content,"",fromaddr,email);
				}
				
				//自助用户
				String username="username";
		        String password="userpassword";
				RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
		        if(login_vo!=null){
			        String login_name = login_vo.getString("str_value");
			        int idx=login_name.indexOf(",");
			        if(idx>3)
			        {
			        	username=login_name.substring(0,idx);
				        password=login_name.substring(idx+1);
			        }
			    }
				RecordVo SS_EMAIL=ConstantParamter.getConstantVo("SS_EMAIL");
				RecordVo vo=ConstantParamter.getConstantVo("SS_LOGIN_LOCK_FIELD");
		        String lockfield="";
				if(SS_EMAIL!=null&&vo!=null){
					String emailfield = SS_EMAIL.getString("str_value");
					lockfield= vo.getString("str_value");
					if(emailfield!=null&&lockfield!=null&&emailfield.length()==5&&lockfield.length()==5){
						login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
			            String pres="";
			            if(login_vo!=null) {
							pres = login_vo.getString("str_value").toLowerCase();
						}
			            String [] press = pres.split(",");
			            for(int i=0;i<press.length;i++){
			            	String dbpre = press[i];
			            	if(dbpre.length()==3){
			            		sql.setLength(0);
			            		sql.append("select t1.username,t2."+emailfield+" email from "+dbpre+"A01 t2 left join t_sys_login_user_info t1 on t1.username=t2."+username+" where t2."+emailfield+" is not null");
			            		if(Sql_switcher.searchDbServer()==1){
			    					sql.append(" and t2."+emailfield+"<>'' ");
			    				}
			    				sql.append(" and "+Sql_switcher.diffDays(Sql_switcher.dateValue(new SimpleDateFormat("yyyy-MM-dd").format(new Date())),"t1.pwd_modtime")+">"+passworddays);
			    				rs = dao.search(sql.toString());
			    				while(rs.next()){
			    					String email = rs.getString("email");
			    					if(email==null||email.length()<1) {
										continue;
									}
			    					bo.sendEmail(title,content,"",fromaddr,email);
			    				}
			            	}
			            }
					}
				}
					
				
			} catch (GeneralException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				
				 try
				   {
					 if(rs!=null) {
						 rs.close();
					 }
					if(conn!=null) {
						conn.close();
					}
				   }
				   catch(Exception ex)
				   {
					   ex.printStackTrace();
				   }
			}
		}
		
		if(passwordlockdays.length()>0){
			Connection conn = null;
			RowSet rs  =null;
			try {
				int days = Integer.parseInt(passwordlockdays);
				conn = (Connection)AdminDb.getConnection();
				ContentDAO dao = new ContentDAO(conn);
				StringBuffer sql = new StringBuffer();
				sql.append("select username from t_sys_login_user_info where "+Sql_switcher.diffDays(Sql_switcher.dateValue(new SimpleDateFormat("yyyy-MM-dd").format(new Date())),"pwd_modtime")+">"+passwordlockdays);
				rs = dao.search(sql.toString());
				ArrayList users = new ArrayList();
				while(rs.next()){
					String userid = rs.getString("username");
					ArrayList userids = new ArrayList();
					userids.add(userid);
					users.add(userids);
				}
				sql.setLength(0);
				sql.append("update operuser set state=0 where username=? and state=1");
				dao.batchUpdate(sql.toString(),users);
				
				String username="username";
		        String password="userpassword";
				RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
		        if(login_vo!=null){
			        String login_name = login_vo.getString("str_value");
			        int idx=login_name.indexOf(",");
			        if(idx>3)
			        {
			        	username=login_name.substring(0,idx);
				        password=login_name.substring(idx+1);
			        }
			    }
		        login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
		        String pres="";
		        if(login_vo!=null) {
					pres = login_vo.getString("str_value").toLowerCase();
				}
		        String [] press = pres.split(",");
				RecordVo vo=ConstantParamter.getConstantVo("SS_LOGIN_LOCK_FIELD");
		        String lockfield="";
		        if(vo!=null)
		        {
		        	lockfield= vo.getString("str_value"); 
		        	if(lockfield.length()==5) {
						for(int i=0;i<press.length;i++){
							String pre = press[i];
							if(pre.length()==3){
								sql.setLength(0);
								sql.append("update "+pre+"a01 set "+lockfield+"=1 where "+username+"=? and ("+lockfield+"<>1 or "+lockfield+" is null)");
								dao.batchUpdate(sql.toString(),users);
							}
						}
					}
		        }
		        
		        //更新登录类缓存
		        for(int i=0;i<users.size();i++){
		        	ArrayList userids = (ArrayList)users.get(i);
		        	if(userids.size()>0){
		        		String usernameid = (String)userids.get(0);
		        		ConstantParamter.setUserAttribute(usernameid, "locked_login", "1");
		        		
		        	}
		        }
				
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try
				   {
					 if(rs!=null) {
						 rs.close();
					 }
					if(conn!=null) {
						conn.close();
					}
				   }
				   catch(Exception ex)
				   {
					   ex.printStackTrace();
				   }
			}
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
}
