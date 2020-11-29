package com.hjsj.hrms.businessobject.gz;

import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class SendSpEmail implements Job{

	@Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Connection conn=null;
		RowSet rs = null;
		RowSet subSet=null;
		try
		{
			conn=AdminDb.getConnection();
			EMailBo bo  = new EMailBo(conn,true,"");
			RecordVo avo=ConstantParamter.getRealConstantVo("SS_EMAIL");
			String email_field="";
			if(avo!=null)
				email_field=avo.getString("str_value");
			if(bo.getSAddr().trim().length()>0)
			{
				ContentDAO dao  = new ContentDAO(conn);
				StringBuffer buf = new StringBuffer("");
				buf.append(" select salaryid,cname,ctrl_param from salarytemplate ");
				buf.append(" where (cstate is null or cstate='')");// 薪资类别
				SalaryCtrlParamBo ctrlparam=null;
				rs= dao.search(buf.toString());
				StringBuffer temp=new StringBuffer("");
				while(rs.next())
				{
					String ctrl_param=Sql_switcher.readMemo(rs, "ctrl_param");
					ctrlparam=new SalaryCtrlParamBo(conn,rs.getInt("salaryid"),ctrl_param); 
					String flow_flag=ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag");
					if(!"1".equalsIgnoreCase(flow_flag))
						continue;
					temp.append(" or salaryid="+rs.getString("salaryid"));
				}
				if(temp.toString().length()>0)
				{
					buf.setLength(0);
					buf.append("select count(*),curr_user from(select salaryid,curr_user from salaryhistory where ("+temp.toString().substring(3)+")");
					buf.append(" and (sp_flag='02' or sp_flag='07') group by salaryid,curr_user,a00z2,a00z3) T group by curr_user");
					rs = dao.search(buf.toString());
					int i=0;
					HashMap tempMap = this.getSalarytempTableData(temp.toString().substring(3), dao);
					while(rs.next()){
						if(i!=0&&i%10==0)
						{
							bo  = new EMailBo(conn,true,"");
						}
						String curr_name=rs.getString(2);;
						RecordVo vo = new RecordVo("operuser");
						vo.setString("username", curr_name);
						if(dao.isExistRecordVo(vo))
						{
							vo = dao.findByPrimaryKey(vo);
							String email=vo.getString("email");
							String fullname=vo.getString("fullname");
							String fName=fullname==null|| "".equals(fullname)?curr_name:fullname;
							StringBuffer title = new StringBuffer();
							title.append("薪资系统邮件提醒：您有新待办需要处理");
							int count = rs.getInt(1);
							if(tempMap.get(curr_name.toUpperCase())!=null)
							{
								count+=((Integer)tempMap.get(curr_name.toUpperCase())).intValue();
								tempMap.remove(curr_name.toUpperCase());
							}
							StringBuffer content = new StringBuffer();
							/**移动专版*/
							content.append("<strong>"+fName+"：</strong><br><br>");							
							if(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
							{
								content.append("&nbsp;&nbsp;&nbsp;&nbsp;<strong>您好，现有"+count+"条待办需要您处理，请您登录KM【人力资源门户】【我的待办】进行处理。</strong><br><br>");
								content.append("&nbsp;&nbsp;&nbsp;&nbsp;<strong>联系电话：82516/82518，谢谢。</strong>");
							}else
							{
								content.append("&nbsp;&nbsp;&nbsp;&nbsp;<strong>您好，现有"+count+"条待办需要您处理，请您登录【人力资源系统】【我的任务】进行处理。</strong><br><br>");
								content.append("&nbsp;&nbsp;&nbsp;&nbsp;<strong>谢谢。</strong>");
							}
							String toAddr="";
							if(isMail(email))
							{
								toAddr=email;
							}
							else
							{
								String a0100=vo.getString("a0100");
								String nbase=vo.getString("nbase");
								if(email_field!=null&&!"".equals(email_field)&&!"#".equals(email_field)&&a0100!=null&&!"".equals(a0100.trim())&&nbase!=null&&!"".equals(nbase.trim()))
								{
									subSet = dao.search("select "+email_field+" from "+nbase+"A01 where a0100='"+a0100+"'");
									while(subSet.next())
									{
										String email_addr=subSet.getString(1);
										if(isMail(email_addr))
										{
											toAddr=email_addr;
										}
									}
								}
							}
							if(toAddr!=null&&!"".equals(toAddr.trim()))
							{
								bo.sendEmail(title.toString(), content.toString(), "", bo.getSAddr(), toAddr);
							}
						}
						i++;
					}
					if(tempMap.size()>0)
					{
						Set keySet = tempMap.keySet();
						for(Iterator t = keySet.iterator();t.hasNext();)
						{
							if(i!=0&&i%10==0)
							{
								bo  = new EMailBo(conn,true,"");
							}
							String key = (String)t.next();
							Integer count = (Integer)tempMap.get(key);
							RecordVo vo = new RecordVo("operuser");
							vo.setString("username", key);
							if(dao.isExistRecordVo(vo))
							{
								vo = dao.findByPrimaryKey(vo);
								String email=vo.getString("email");
								String fullname=vo.getString("fullname");
								String fName=fullname==null|| "".equals(fullname)?key:fullname;
								StringBuffer title = new StringBuffer();
								title.append("薪资系统邮件提醒：您有新待办需要处理");
								StringBuffer content = new StringBuffer();
								content.append("<strong>"+fName+"：</strong><br><br>");
								if(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
								{
									content.append("&nbsp;&nbsp;&nbsp;&nbsp;<strong>您好，现有"+count+"条待办需要您处理，请您登录KM【人力资源门户】【我的待办】进行处理。</strong><br><br>");
									content.append("&nbsp;&nbsp;&nbsp;&nbsp;<strong>联系电话：82516/82518，谢谢。</strong>");
								}else
								{
									content.append("&nbsp;&nbsp;&nbsp;&nbsp;<strong>您好，现有"+count+"条待办需要您处理，请您登录【人力资源系统】【我的任务】进行处理。</strong><br><br>");
									content.append("&nbsp;&nbsp;&nbsp;&nbsp;<strong>谢谢。</strong>");
								}
								String toAddr="";
								if(isMail(email))
								{
									toAddr=email;
								}
								else
								{
									String a0100=vo.getString("a0100");
									String nbase=vo.getString("nbase");
									if(email_field!=null&&!"".equals(email_field)&&!"#".equals(email_field)&&a0100!=null&&!"".equals(a0100.trim())&&nbase!=null&&!"".equals(nbase.trim()))
									{
										subSet = dao.search("select "+email_field+" from "+nbase+"A01 where a0100='"+a0100+"'");
										while(subSet.next())
										{
											String email_addr=subSet.getString(1);
											if(isMail(email_addr))
											{
												toAddr=email_addr;
											}
										}
									}
								}
								if(toAddr!=null&&!"".equals(toAddr.trim()))
								{
									bo.sendEmail(title.toString(), content.toString(), "", bo.getSAddr(), toAddr);
								}
							}
							i++;
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			if(subSet!=null)
			{
				try
				{
					subSet.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			if(conn!=null)
			{
				try{
					conn.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
	}
	public boolean isMail(String email){
		   
		if(email==null||email.trim().length()==0)
			return false;
		String emailPattern ="^([a-z0-9A-Z]+[_]*[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
	    return  email.matches(emailPattern);
	}
	public HashMap getSpCount(ContentDAO dao)
	{
		HashMap map = new HashMap();
		RowSet rs = null;
		try
		{
			String sql="select count(*),curr_user from (select salaryid,curr_user from salaryhistory where (sp_flag='02' or sp_flag='07') group by salaryid,curr_user,A00Z2,a00z3)T group by curr_user";
			rs=dao.search(sql);
			while(rs.next())
			{
				/*if(map.get(rs.getString(2).toUpperCase())==null)
				{
			    	map.put(rs.getString(2).toUpperCase(), rs.getInt(1)+"");
				}
				else
				{
					int coun=Integer.parseInt(((String)map.get(rs.getString(2))).toUpperCase());
					map.put(rs.getString(2).toUpperCase(),(coun+rs.getInt(1))+"");
				}*/
				map.put(rs.getString(2).toUpperCase(), rs.getInt(1)+"");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return map;
	}
	public HashMap getSalarytempTableData(String temp,ContentDAO dao)
	{
		HashMap map = new HashMap();
		RowSet rs = null;
		RowSet subSet = null;
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select salaryid,username from gz_extend_log where sp_flag<>'06' and ("+temp+")");
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				String salaryid=rs.getString("salaryid");
				String username=rs.getString("userName");
				subSet  = dao.search(" select sp_flag from "+username+"_salary_"+salaryid+" where sp_flag='07'");
				while(subSet.next())
				{
					if(map.get(username.toUpperCase())!=null)
					{
						Integer count = (Integer)map.get(username.toUpperCase());
						Integer s = new Integer(count.intValue()+1);
						map.put(username.toUpperCase(), s);
					}
					else
					{
						Integer s = new Integer(1);
						map.put(username.toUpperCase(), s);
					}
					break;
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			if(subSet!=null)
			{
				try
				{
					subSet.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return map;
	}

}
