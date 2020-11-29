package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.BaseClassShift;
import com.hjsj.hrms.businessobject.kq.team.KqShiftClass;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * 保存未排班人员的排班数据
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 7, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class SaveSingKqShiftTrans extends IBusiness implements KqClassConstant{

	public void execute() throws GeneralException
	{
	   HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	   String a0100=(String)hm.get("a0100");
	   a0100 = PubFunc.decrypt(a0100);
	   String nbase=(String)hm.get("dbase");
	   nbase = PubFunc.decrypt(nbase);
	   String start_date=(String)hm.get("start_date");
	   String end_date=(String)hm.get("end_date");	   
	   String selected_class=(String)hm.get("selected_class");
	   String rest_postpone=(String)hm.get("rest_postpone");
	   String feast_postpone=(String)hm.get("feast_postpone");
	   String codeitemid=a0100.substring(2);
	   shift_employee(nbase,codeitemid,start_date,end_date,selected_class,rest_postpone,feast_postpone,"02");
	   this.getFormHM().put("a0100", codeitemid);
	   this.getFormHM().put("dbase", nbase);
	   /*********显示结果***********/
	   KqShiftClass kqShiftClass=new KqShiftClass(this.getFrameconn(),this.userView);
	   ArrayList datelist=new ArrayList();
	   datelist=RegisterDate.getKqDayList(this.getFrameconn());
	   start_date=(String)datelist.get(0);
	   end_date=(String)datelist.get(datelist.size()-1);
	   start_date=start_date.replaceAll("-","\\.");
	   end_date=end_date.replaceAll("-","\\.");
	   Date s_d=DateUtils.getDate(start_date, "yyyy.MM.dd");
	   Date e_d=DateUtils.getDate(end_date, "yyyy.MM.dd");
	   int spacedate = DateUtils.dayDiff(s_d,e_d);
	   datelist=new ArrayList();
	   Date dd;
	   for (int i = 0; i <=spacedate; i++) {
		   dd=DateUtils.addDays(s_d,i);	
		   datelist.add(DateUtils.format(dd,"yyyy.MM.dd"));	
		}
	   String table_html=kqShiftClass.returnShiftHtml(datelist,a0100,nbase);
	   this.getFormHM().put("table_html",table_html);
    }
	public void shift_employee(String nbase,String codeitemid,String start_date,String end_date,String class_id,String rest_postpone,String feast_postpone,String kq_type)
	   throws GeneralException
	   {
		   if(nbase==null||nbase.length()<=0)
			   return;
		   BaseClassShift baseClassShift=new BaseClassShift(this.userView,this.getFrameconn());
		   String b0110=baseClassShift.getEMpData(codeitemid,nbase,"b0110");
		   ArrayList restList=IfRestDate.search_RestOfWeek(b0110,userView,this.getFrameconn());
		   String rest_date=restList.get(0).toString();
		   String rest_b0110=restList.get(1).toString();
		   String t_table=baseClassShift.tempClassTable();
	   	   String date_Table=baseClassShift.creat_KqTmp_Table(this.userView.getUserId());
	   	   ArrayList date_list=baseClassShift.getDatelist(start_date,end_date);    	
	       baseClassShift.initializtion_date_Table(date_list,rest_date,date_Table,rest_b0110,b0110);	
	       String sWhere="and a0100='"+codeitemid+"'";
	       String whereIN=RegisterInitInfoData.getWhereINSql(this.userView,nbase);
	       baseClassShift.insrtTempData(t_table,date_Table,nbase,whereIN,sWhere);//插入临时表
	       baseClassShift.insertClassToTemp(class_id,t_table,rest_postpone,feast_postpone);//修改临时表
	       baseClassShift.insertClassToShift(t_table,whereIN);
	       baseClassShift.dropTable(t_table);
	   	   baseClassShift.dropTable(date_Table); 
	   }
	
}
