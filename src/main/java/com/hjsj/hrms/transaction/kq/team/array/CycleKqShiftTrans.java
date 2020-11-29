package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.team.CycleKqClass;
import com.hjsj.hrms.businessobject.kq.team.KqClassArray;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 周期排班
 * <p>Title:CycleKqShiftTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 25, 2006 9:31:42 AM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class CycleKqShiftTrans extends IBusiness implements KqClassConstant{

	public void execute() throws GeneralException
	{
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");   
		String a_code= (String)hm.get("a_code");
		String nbase= (String)hm.get("nbase");
		if(a_code==null||a_code.length()<=0)
			return;
		KqClassArray kqClassArray=new KqClassArray(this.getFrameconn(),userView);
		ArrayList cyclelist=kqClassArray.getCycleList();
		String cycle_id=(String)this.getFormHM().get("cycle_id");
		if(cycle_id==null||cycle_id.length()<=0|| "add".equalsIgnoreCase(cycle_id))
		{
		  if(cyclelist!=null&&cyclelist.size()>0)
		  {
			CommonData vo=(CommonData)cyclelist.get(0);
			cycle_id=vo.getDataValue();
		  }
		}else
		{
			cycle_id=Float.parseFloat(cycle_id)+"";
			cycle_id=cycle_id.substring(0,cycle_id.indexOf("."));
		}		
		/*String start_date=PubFunc.getStringDate("yyyy-MM-dd");
		Date d_start=DateUtils.getDate(start_date,"yyyy-MM-dd");
		Date d_end=DateUtils.addDays(d_start,30);*/
		String session_data=(String)this.getFormHM().get("session_data");
		ArrayList date_list =RegisterDate.getOneDurationDate(this.getFrameconn(),session_data);
		String start_date=date_list.get(0).toString().replaceAll("\\.","-");
		String end_date=date_list.get(date_list.size()-1).toString().replaceAll("\\.","-");
		this.getFormHM().put("start_date",start_date);
		this.getFormHM().put("end_date",end_date);
		this.getFormHM().put("rest_postpone","");
		this.getFormHM().put("feast_postpone","");
		this.getFormHM().put("cyclelist",cyclelist);
		this.getFormHM().put("take_turns", "");
		this.getFormHM().put("cycle_id",cycle_id);
		this.getFormHM().put("a_code",a_code);
		this.getFormHM().put("nbase",nbase);
		CycleKqClass cycleKqClass=new CycleKqClass(this.getFrameconn(),this.userView);
		cycleKqClass.checkShiftTable();
		
	}
}
