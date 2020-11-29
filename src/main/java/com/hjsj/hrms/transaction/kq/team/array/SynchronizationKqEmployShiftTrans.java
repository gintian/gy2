package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.KqClassArrayConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 同步人员排班表
 * @author Owner
 *
 */
public class SynchronizationKqEmployShiftTrans extends IBusiness implements  KqClassArrayConstant,KqClassConstant{

	public void execute() throws GeneralException {
		String select_flag=(String)this.getFormHM().get("select_flag");
		String select_name=(String)this.getFormHM().get("select_name");
		this.getFormHM().put("select_flag",select_flag);
		this.getFormHM().put("select_name",select_name);
		String start_date=(String)this.getFormHM().get("start_date");
		String end_date=(String)this.getFormHM().get("end_date");	
		if(start_date!=null&&start_date.length()>0)
			start_date=start_date.replaceAll("\\.","-");
		if(end_date!=null&&end_date.length()>0)
			end_date=end_date.replaceAll("\\.","-");
		if(!(validateDate(start_date)&&validateDate(end_date)))
		{
			ArrayList datelist =RegisterDate.getKqDayList(this.getFrameconn()); 
			if(datelist!=null&&datelist.size()>0)
			{
				start_date=datelist.get(0).toString();
				end_date=datelist.get(datelist.size()-1).toString();
				if(start_date!=null&&start_date.length()>0)
					start_date=start_date.replaceAll("-","\\.");
				if(end_date!=null&&end_date.length()>0)
					end_date=end_date.replaceAll("-","\\.");
			}else
			{
				start_date=DateStyle.dateformat(new java.util.Date(),"yyyy.MM.dd");
				end_date=start_date; 
			}
			 //当天假单
		}
		if(start_date!=null&&start_date.length()>0)
			start_date=start_date.replaceAll("-","\\.");
		if(end_date!=null&&end_date.length()>0)
			end_date=end_date.replaceAll("-","\\.");
		ArrayList sql_db_list=this.userView.getPrivDbList();
		for(int i=0;i<sql_db_list.size();i++)
		{
			String nbase=sql_db_list.get(i).toString();
			String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);		
			synchronizationInit(nbase,whereIN,start_date,end_date);
	    }
	}
	public void synchronizationInit(String nbase,String whereIN,String start_date,String end_date)throws GeneralException
	{
		 StringBuffer sql=new StringBuffer();
		 sql.append(" and "+kq_employ_shift_q03z0+">='"+start_date+"'");
		 sql.append(" and "+kq_employ_shift_q03z0+"<='"+end_date+"'"); 
		 String destTab="kq_employ_shift";//目标表
		 String srcTab=nbase+"A01";//源表
		 String strJoin=destTab+".A0100="+srcTab+".A0100";//关联串  xxx.field_name=yyyy.field_namex,....
		 String  strSet=destTab+".A0101="+srcTab+".A0101`"+destTab+".B0110="+srcTab+".B0110`"+destTab+".E0122="+srcTab+".E0122`"+destTab+".E01A1="+srcTab+".E01A1";//更新串  xxx.field_name=yyyy.field_namex,....
		 String strDWhere=destTab+".nbase='"+nbase+"' "+sql.toString();//更新目标的表过滤条件
		 String strSWhere=srcTab+".a0100 in(select "+nbase+"A01.a0100 "+whereIN+")";//源表的过滤条件  
		 String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
		 String othWhereSql=destTab+".a0100 in(select "+nbase+"A01.a0100 "+whereIN+")";
		 update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,othWhereSql);		
		 ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {			
			dao.update(update);
		} catch (Exception e) {
			e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
	}
	private boolean validateDate(String datestr)
	{
		boolean bflag=true;
		if(datestr==null|| "".equals(datestr))
			return false;
		try
		{
			java.util.Date date=DateStyle.parseDate(datestr);
			if(date==null)
				bflag=false;
		}
		catch(Exception ex)
		{
			bflag=false;
		}
		return bflag;
	}		

}
