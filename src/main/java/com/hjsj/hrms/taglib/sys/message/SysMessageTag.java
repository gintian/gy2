package com.hjsj.hrms.taglib.sys.message;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.ViewAllApp;
import com.hjsj.hrms.businessobject.kq.kqself.NetSignIn;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.performance.WarnNoscoreBo;
import com.hjsj.hrms.businessobject.sys.options.message.SysMessage;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.sys.warn.ConfigCtrlInfoVO;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SysMessageTag extends BodyTagSupport
{

	public int doStartTag() throws JspException
	{
		Connection conn=null;
		RowSet rs=null;
		try
		{
			conn=AdminDb.getConnection();
			UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);	
			String bosflag="";
			if(userview!=null)
				bosflag=userview.getBosflag();	
			else
				return EVAL_BODY_BUFFERED;
			
			
			// 绩效考核计划预警  JinChunhai 2012.05.26
			ArrayList planList = new ArrayList();
			HashMap roleMap = new HashMap();
			ArrayList roleList = userview.getRolelist();			
			String a0100 = userview.getA0100();
			if(a0100!=null && a0100.trim().length()>0) // 自助用户登录平台
			{
				for (int i = 0; i < roleList.size(); i++) 
				{
					String role = (String) roleList.get(i);					
					roleMap.put(role, "role");					
				}				
				WarnNoscoreBo wbo = new WarnNoscoreBo(conn,userview);
				planList = wbo.getWarnPlanList(roleMap,a0100);
				
				if(planList!=null && planList.size()>0)
				{
					String plan_ids = "";
					for( int i=0; i<planList.size(); i++)
					{
						CommonData cData = (CommonData)planList.get(i);
						plan_ids += ";"+cData.getDataValue();
					}					
					StringBuffer strhtml=new StringBuffer();
					strhtml.append("var dl=(screen.width-596)/2;dt=(screen.height-360)/2;");
					strhtml.append("window.open(");
					strhtml.append("'/performance/warnPlan/selfNoScoreorCardList.do?b_view=link&plan_ids="+plan_ids.substring(1)+"',");
					strhtml.append("'view_message','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top='+dt+'px,left='+dl+'px,width=596px,height=360px'");
					strhtml.append(");");			
					pageContext.getOut().println(strhtml.toString());					
				}				
			}
			
			//考勤登陆提醒 2013年6月4日8:41:54
			String nbase = userview.getDbname();
			ContentDAO dao = new ContentDAO(conn);
			if (nbase != null && nbase.length() > 0 && a0100 != null && a0100.trim().length()>0) 
			{
				String remindType = "1";
				boolean needRemind = false;
				HashMap map = new HashMap();	
				map.put("remindType", "1");
				map.put("needRemind", "0");
				map = KqLogonHint(conn, remindType, needRemind, a0100, nbase, userview, dao);
				
				if ("1".equals((String)map.get("needRemind"))) 
				{
					StringBuffer html = new StringBuffer();
					html.append("var dl=(screen.width-596)/2;dt=(screen.height-360)/2;");
					html.append("window.open(");
					html.append("'/kq/machine/netsignin/signinWarn.jsp?remindType=" + (String)map.get("remindType") + "',");
					html.append("'kq_message','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top='+dt+'px,left='+dl+'px,width=596px,height=360px'");
					html.append(");");			
					pageContext.getOut().println(html.toString());	
				}
			}
			
			SysMessage sysMessage=new SysMessage(conn);
			HashMap hashMap =sysMessage.getAllSysNoteXML();
			String start_date=(String)hashMap.get("start_date");
			String days=(String)hashMap.get("days");
			String constant=(String)hashMap.get("constant");	
		    if(bosflag!=null&&("hl".equals(bosflag)|| "hl4".equals(bosflag)|| "ul".equals(bosflag)))
		    {
		    	String view_hr=(String)hashMap.get("view_hr");;
		    	if(view_hr!=null&& "0".equalsIgnoreCase(view_hr))
			    {
		    		//return EVAL_BODY_BUFFERED;  
			    }
		    }
		    if(bosflag!=null&&("em5".equals(bosflag)|| "el4".equals(bosflag)|| "el".equals(bosflag)))
		    {
		    	String view_em=(String)hashMap.get("view_em");;
		    	if(view_em!=null&& "0".equalsIgnoreCase(view_em))
			    {
		    		//return EVAL_BODY_BUFFERED;  
			    }
		    }
			if(days==null||days.length()<=0)
			{
				days="0";
			}
			boolean isCorrect=false;
			if(start_date!=null&&start_date.length()>0&&constant!=null&&constant.length()>0)
			{
				Date date_d=DateUtils.getDate(start_date,"yyyy.MM.dd");
				Date message_d=DateUtils.addDays(date_d,Integer.parseInt(days)); 
				Calendar now = Calendar.getInstance();
				Date cur_d=now.getTime();		
				int diff=RegisterDate.diffDate(message_d,cur_d); 
				if(diff<=0)
				{
					StringBuffer strhtml=new StringBuffer();
					strhtml.append("var dl=(screen.width-596)/2;dt=(screen.height-380)/2;");
					strhtml.append("window.open(");
					strhtml.append("'/system/options/message/sys_manager.do?b_view=link',");
					strhtml.append("'view_message','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top='+dt+'px,left='+dl+'px,width=596px,height=380px'");
					strhtml.append(");");			
					pageContext.getOut().println(strhtml.toString());
					isCorrect=true;
				}
			}
	
			if(!isCorrect)
			{
				DbWizard dbWizard =new DbWizard(conn);
				if(!dbWizard.isExistTable("appoint_news",false))
				{
					
				}else
				{
					Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
					String birthday_wid=sysbo.getValue(Sys_Oth_Parameter.BIRTHDAY_WID);
					if(birthday_wid!=null&&birthday_wid.length()>0&&!"#".equals(birthday_wid)&&!"null".equals(birthday_wid))
					{
						//UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);	
						StringBuffer sql=new StringBuffer();				
						String cur_time=PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss");	
						switch(Sql_switcher.searchDbServer())
					    {	  case Constant.KUNLUN:
							  case Constant.ORACEL:
						      {
						    	  cur_time="to_date('"+cur_time+"','yyyy-mm-dd  hh24:mi:ss')";
						    	  break;
						      }
						}
						RecordVo vo = new RecordVo("hrpwarn");//new RecordVo( Key_HrpWarn_Table );
						vo.setInt("wid", Integer.parseInt(birthday_wid));
					    if(vo==null)
					    	return EVAL_BODY_BUFFERED;
				        vo = dao.findByPrimaryKey(vo);
				        if(vo.getString("valid")!=null&& "0".equals(vo.getString("valid")))
				        	return EVAL_BODY_BUFFERED;  
				        ConfigCtrlInfoVO ctrlVo =  new ConfigCtrlInfoVO(vo.getString("warn_ctrl"));
				        String sendspace=ctrlVo.getStrSendspace();
				        if(sendspace==null||sendspace.length()<=0)
				        	sendspace="7";
						sql.append("select * from appoint_news where wid='"+birthday_wid+"'");
						sql.append(" and inceptuser='"+userview.getUserName()+"'");
						if(Sql_switcher.searchDbServer()==Constant.ORACEL || Sql_switcher.searchDbServer()==Constant.KUNLUN)
						  sql.append(" and "+Sql_switcher.diffDays(""+cur_time+"","sendtime")+"<="+(Integer.parseInt(sendspace)+1)+"");
						else
						  sql.append(" and "+Sql_switcher.diffDays("'"+cur_time+"'","sendtime")+"<="+(Integer.parseInt(sendspace)+1)+"");
						//System.out.println(sql.toString());
						
						rs=dao.search(sql.toString());
						if(rs.next())
						{
							StringBuffer strhtml=new StringBuffer();
							strhtml.append("var dl=(screen.width-596)/2;dt=(screen.height-380)/2;");
							strhtml.append("window.open(");
							strhtml.append("'/system/options/message/sys_manager.do?b_view=link',");
							//xuj update scrollbar=no 否则出现双滚动条 20141104
							strhtml.append("'view_message','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top='+dt+'px,left='+dl+'px,width=596px,height=380px'");
							strhtml.append(");");			
							pageContext.getOut().println(strhtml.toString());
						}
					}
				}				
			}
		
		}catch(Exception e)
		{
			e.printStackTrace();
			
		}
		finally
		{
			try{
			 if(rs!=null)
				 rs.close();
			 if (conn != null)
	             conn.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
	          
		}
		return EVAL_BODY_BUFFERED;  
		
	}
	
	private HashMap KqLogonHint(Connection conn,String remindType,boolean needRemind,String a0100,String nbase,UserView userview,ContentDAO dao){
		HashMap map = new HashMap();
		KqParam kqParam = KqParam.getInstance();
		String logon_kq_hint = kqParam.getLogon_kq_hint();
		if ("1".equals(logon_kq_hint)) 
		{
			KqParameter para=new KqParameter(userview,"",conn);
			HashMap hashmap =para.getKqParamterMap();
			String kq_type=(String)hashmap.get("kq_type");//考勤方式字段
			String kq_way = "";
			RecordVo rv = new RecordVo(nbase + "A01");
			rv.setString("a0100", a0100);
			if (kq_type != null && kq_type.length() > 0)
			{
				if(dao.isExistRecordVo(rv))
				{
					try {
						rv = dao.findByPrimaryKey(rv);
					} catch (GeneralException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					if ("02".equals(rv.getString(kq_type.toLowerCase()))) 
					{
						kq_way = "02";
					}
				}
				if   ("02".equals(kq_way)) 
				{
					Date nowDate = new Date();
					String strDate = OperateDate.dateToStr(nowDate, "yyyy-MM-dd HH:mm");
					String date = strDate.substring(0, 10);
					String time = strDate.substring(11,16);
					nowDate = OperateDate.strToDate(date + " " + time, "yyyy-MM-dd HH:mm");
					ViewAllApp viewAllApp = new ViewAllApp(conn);
					KqUtilsClass kqUtilsClass = new KqUtilsClass(conn);
					NetSignIn netSignIn=new NetSignIn(userview,conn);
					String classid = viewAllApp.getClassid(a0100, nbase, nowDate);
					
					if (classid != null && classid.length() > 0 && !"0".equals(classid)) 
					{
						HashMap classMap = kqUtilsClass.classDetails(classid);
						for (int i = 1; i < 4; i++) 
						{
							String needCard1 = (String) classMap.get("onduty_card_" + i);
							String onduty = (String)classMap.get("onduty_" + i);
							if ("1".equals(needCard1) && onduty != null && onduty.length()>0) 
							{
								String onduty_start = (String)classMap.get("onduty_start_" + i);
								onduty_start = onduty_start != null && onduty_start.length() > 0 ? onduty_start : onduty;
								Date onduty_start_date = OperateDate.strToDate(date + " " + onduty_start, "yyyy-MM-dd HH:mm");
								
								String onduty_end = (String)classMap.get("onduty_end_" + i);
								onduty_end = onduty_end != null && onduty_end.length() > 0 ? onduty_end : onduty;
								Date onduty_end_date = OperateDate.strToDate(date + " " + onduty_end, "yyyy-MM-dd HH:mm");
								
								if (nowDate.getTime() >= onduty_start_date.getTime()) 
								{
									needRemind = kqUtilsClass.needCard(a0100, nbase, date, onduty_start, onduty_end, onduty);
								}
								else
									break;
								
								if(!netSignIn.ifNetSign_logon(nbase, a0100, date.replace("-", "."), onduty, "q15"))
									needRemind = false;
								if(!netSignIn.ifNetSign_logon(nbase, a0100, date.replace("-", "."), onduty, "q13"))
									needRemind = false;
								
								if(nowDate.getTime() >= onduty_start_date.getTime() 
										&& nowDate.getTime() <= onduty_end_date.getTime() && needRemind)
									remindType = "0";
							}
							if (needRemind)
								break;
							
							String needCard2 = (String) classMap.get("offduty_card_" + i);
							String offduty =  (String)classMap.get("offduty_" + i);
							if ("1".equals(needCard2) && offduty != null && offduty.length()>0) 
							{	
								String offduty_start = (String)classMap.get("offduty_start_" + i);
								offduty_start = offduty_start != null && offduty_start.length() > 0 ? offduty_start : offduty;
								Date offduty_start_date = OperateDate.strToDate(date + " " + offduty_start, "yyyy-MM-dd HH:mm");
								
								String offduty_end = (String)classMap.get("offduty_end_" + i);
								offduty_end = offduty_end != null && offduty_end.length() > 0 ? offduty_end : offduty;
								Date offduty_end_date = OperateDate.strToDate(date + " " + offduty_end, "yyyy-MM-dd HH:mm");
								
								
								if (nowDate.getTime() >= offduty_start_date.getTime()) 
								{
									needRemind = kqUtilsClass.needCard(a0100, nbase, date, offduty_start, offduty_end, offduty);
								}
								else
									break;
								if(!netSignIn.ifNetSign_logon(nbase, a0100, date.replace("-", "."),offduty, "q15"))
									needRemind = false;
								if(!netSignIn.ifNetSign_logon(nbase, a0100, date.replace("-", "."), offduty, "q13"))
									needRemind = false;
								
								if(nowDate.getTime() >= offduty_start_date.getTime() 
										&& nowDate.getTime() <= offduty_end_date.getTime() && needRemind)
									remindType = "0";
							}
							if (needRemind)
								break;
						}
					}
				}
			}
		}
		map.put("remindType", remindType);
		if (needRemind) 
			map.put("needRemind", "1");
		else
			map.put("needRemind", "0");
		return map;
	}
	

}
