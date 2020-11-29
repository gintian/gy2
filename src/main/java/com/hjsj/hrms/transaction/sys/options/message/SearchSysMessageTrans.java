package com.hjsj.hrms.transaction.sys.options.message;

import com.hjsj.hrms.businessobject.sys.options.message.SysMessage;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.sys.warn.ConfigCtrlInfoVO;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
/**
 * 显示系统通知
 * <p>Title:SearchSysMessageTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 16, 2006 1:45:00 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class SearchSysMessageTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		SysMessage sysMessage=new SysMessage(this.getFrameconn());
		HashMap hashMap =sysMessage.getAllSysNoteXML();
		String start_date=(String)hashMap.get("start_date");
		String days=(String)hashMap.get("days");
		String constant=(String)hashMap.get("constant");	
		//【5006】系统公告维护中，输入内容出现换行符等内容不对  jingq add 2014.11.13
		constant = PubFunc.hireKeyWord_filter_reback(constant);
		String view_hr=(String)hashMap.get("view_hr");
	    String view_em=(String)hashMap.get("view_em");
	    String backgroudimage = (String)hashMap.get("backgroudimage");
		if(start_date==null||start_date.length()<=0)
		{
			Calendar now = Calendar.getInstance();
			Date cur_d=now.getTime();
			start_date=DateUtils.format(cur_d,"yyyy.MM.dd");
		}
		if(days==null||days.length()<0)
  	    {
  		   days="";
  	    }  	   
  	    if(constant==null||constant.length()<0)
  	    {
  		   constant="";
  	    }
  	    DbWizard dbWizard =new DbWizard(this.getFrameconn());
		if(dbWizard.isExistTable("appoint_news",false))
		{
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String birthday_wid=sysbo.getValue(Sys_Oth_Parameter.BIRTHDAY_WID);
			if(birthday_wid!=null&&birthday_wid.length()>0&&!"#".equals(birthday_wid))
			{
				StringBuffer sql=new StringBuffer();				
				/*sql.append("select title,content from appoint_news where wid='"+birthday_wid+"'");
				sql.append(" and senduser='"+this.userView.getUserName()+"'");*/
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				try
				{
					String cur_time=PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss");	
					RecordVo vo = new RecordVo("hrpwarn");//new RecordVo( Key_HrpWarn_Table );				
					vo.setString("wid", birthday_wid);
			        vo = dao.findByPrimaryKey(vo);
			        ConfigCtrlInfoVO ctrlVo =  new ConfigCtrlInfoVO(vo.getString("warn_ctrl"));
			        String sendspace=ctrlVo.getStrSendspace();
			        if(sendspace==null||sendspace.length()<0)
			        	sendspace="7";
					sql.append("select * from appoint_news where wid='"+birthday_wid+"'");
					sql.append(" and inceptuser='"+this.userView.getUserName()+"'");
					if(Sql_switcher.searchDbServer()==Constant.ORACEL){
						sql.append(" and abs(ROUND(TO_NUMBER(sendtime-to_date('"+cur_time+"','yyyy-mm-dd hh24:mi:ss'))))<="+sendspace+" order by news_id desc");
					}else
						sql.append(" and abs("+Sql_switcher.diffDays("sendtime","'"+cur_time+"'")+")<="+sendspace+" order by news_id desc");
					RowSet rs=dao.search(sql.toString());
					if(rs.next())
					{
						if(constant!=null&&constant.length()>0)
							constant=constant+"<br>";
						constant=constant+Sql_switcher.readMemo(rs, "content");
					}
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		this.getFormHM().put("backgroudimage", backgroudimage);
		this.getFormHM().put("view_hr", view_hr);
		this.getFormHM().put("view_em", view_em);
		this.getFormHM().put("start_date",start_date);
		this.getFormHM().put("days",days);
		this.getFormHM().put("constant",constant);
		
	}

}
