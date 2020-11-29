package com.hjsj.hrms.transaction.performance.nworkdiary.myworkdiary.weekwork;

import com.hjsj.hrms.businessobject.performance.nworkdiary.myworkdiary.CalendarBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class CalendarWeekTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			//获得链接中的4个参数
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String year = "";
			String month = "";
			String day = "";
			String staff_url = "";
			//员工日志
			String frompage = (String)hm.get("frompage")==null?"":(String)hm.get("frompage");//-1:菜单   1：员工日志
			hm.remove("frompage");
			
			if(frompage==null || "".equals(frompage)){//在周报中切换
				frompage = (String)this.getFormHM().get("frompage");
				year = (String)hm.get("year");
				month = (String)hm.get("month");
				day = (String)hm.get("day");
				hm.remove("year");
				hm.remove("month");
				hm.remove("day");
				if("".equals(year)){
					GregorianCalendar ca = new GregorianCalendar();
					year = String.valueOf(ca.get(Calendar.YEAR));
					month = String.valueOf(ca.get(Calendar.MONTH)+1);
					day = String.valueOf(ca.get(Calendar.DAY_OF_MONTH));
				}
				staff_url = (String)this.getFormHM().get("staff_url");
			}else if("1".equals(frompage)){//如果是从员工日志中进入
				this.userView.getHm().put("a0100", (String)hm.get("a0100"));
				this.userView.getHm().put("nbase", (String)hm.get("nbase"));
				this.userView.getHm().put("isOwner", "1");
				year = (String)hm.get("year");
				month = (String)hm.get("month");
				day = (String)hm.get("day");
				hm.remove("year");
				hm.remove("month");
				hm.remove("day");
				staff_url = (String)this.getFormHM().get("staff_url");
				hm.remove("a0100");
				hm.remove("nbase");
			}else if("-1".equals(frompage)){//如果是从菜单进入的
				this.userView.getHm().put("a0100", this.userView.getA0100());
				this.userView.getHm().put("nbase", this.userView.getDbname());
				this.userView.getHm().put("isOwner", "0");
				GregorianCalendar ca = new GregorianCalendar();
				year = String.valueOf(ca.get(Calendar.YEAR));
				month = String.valueOf(ca.get(Calendar.MONTH)+1);
				day = String.valueOf(ca.get(Calendar.DAY_OF_MONTH));
				staff_url = "";
			}
			String a0100 = (String)this.userView.getHm().get("a0100");
			String nbase = (String)this.userView.getHm().get("nbase");
			String isowner = (String)this.userView.getHm().get("isOwner");
			CalendarBo bo = new CalendarBo(this.getFrameconn(),this.userView,a0100,nbase);
			
			//画出表格
			String[] array = bo.getTableHtml(year,month,day,staff_url,frompage);
			String tableHtml = "";//输出表头
			String weekHtml = "";//输出周一至周六
			String jsonstr = "";//全天事件
			String periodjsonstr = "";//时间段事件
			tableHtml = array[0];
			weekHtml = array[1];
			jsonstr = array[2];
			periodjsonstr = array[3];
			
			this.getFormHM().put("tableHtml", tableHtml);
			this.getFormHM().put("weekHtml", weekHtml);
			this.getFormHM().put("jsonstr", jsonstr);
			this.getFormHM().put("periodjsonstr", periodjsonstr);
			
			this.getFormHM().put("frompage", frompage);
			this.getFormHM().put("a0100", a0100);
			this.getFormHM().put("nbase", nbase);
			this.getFormHM().put("staff_url", staff_url);
			this.getFormHM().put("isowner", isowner);
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
