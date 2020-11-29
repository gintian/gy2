package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.businessobject.kq.team.BaseClassShift;
import com.hjsj.hrms.businessobject.kq.team.CycleKqClass;
import com.hjsj.hrms.businessobject.kq.team.KqClassArrayConstant;
import com.hjsj.hrms.businessobject.kq.team.ReliefKqClass;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;
/**
 * 个人倒班排班处理
 * <p>Title:ReliefKqClassTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Nov 7, 2006 3:11:06 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class ReliefKqClassTrans extends IBusiness implements KqClassArrayConstant{

	public void execute() throws GeneralException
	{
	   String start_date=(String)this.getFormHM().get("start_date");
	   String end_date=(String)this.getFormHM().get("end_date");
	   String[] relief_id=(String[])this.getFormHM().get("relief_id");
	   String[] relief_day=(String[])this.getFormHM().get("relief_day");
		String[] new_cycle_days = new String[relief_day.length];
		for (int i = 0; i < relief_day.length; i++) //循环天数是整数
		{
			String day = relief_day[i];
			if (day.indexOf(".") != -1) 
			{
				String one_day = day.substring(0, day.indexOf("."));
				if ("0".equals(one_day))
				{
					new_cycle_days[i] = "1";
				}else 
				{
					
					new_cycle_days[i] = one_day;
				}
			}else 
			{
				new_cycle_days[i] = day;
			}
		}
		relief_day = new_cycle_days;
	   String selected_object=(String)this.getFormHM().get("selected_object");//1:组0：人
	   String rest_postpone=(String)this.getFormHM().get("rest_postpone");
	   String feast_postpone=(String)this.getFormHM().get("feast_postpone");
	   String[] right_fields = (String[]) this.getFormHM().get("right_fields");	
	   if(feast_postpone==null||feast_postpone.length()<=0)
	    	feast_postpone="0";
	    if(rest_postpone==null||rest_postpone.length()<=0)
	    	rest_postpone="0";
	   if(selected_object==null||selected_object.length()<=0)
		   return ;
	   BaseClassShift baseClassShift=new BaseClassShift(this.userView,this.getFrameconn());
   	   String t_table=baseClassShift.tempClassTable();
   	   baseClassShift.deleteTable(t_table);
   	   String date_Table=baseClassShift.creat_KqTmp_Table(this.userView.getUserId());
   	   ArrayList date_list=baseClassShift.getDatelist(start_date,end_date);   
   	   /*KqParameter kq_paramter = new KqParameter(this.getFormHM(),this.userView,this.userView.getUserOrgId(),this.getFrameconn());  
	   String kq_type=kq_paramter.getKq_type();*/
   	   String kq_type="02";
	   CycleKqClass cycleKqClass=new CycleKqClass(this.getFrameconn(),this.userView);
	   ReliefKqClass reliefKqClass=new ReliefKqClass(this.getFrameconn(),this.userView);
	   if("1".equals(selected_object))
	   {
		   /**********按班组排序**********/
		   ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
		   String b0110=managePrivCode.getPrivOrgId();  
		   ArrayList group_list=cycleKqClass.getListFromGroupArray(right_fields);
		   ArrayList restList=IfRestDate.search_RestOfWeek(b0110,userView,this.getFrameconn());
		   String rest_date=restList.get(0).toString();
		   String rest_b0110=restList.get(1).toString();
		   ArrayList rest_list=baseClassShift.initializtion_date_Table(date_list,rest_date,date_Table,rest_b0110,b0110);
		   cycleKqClass.groupEmployeeInsertTemp(right_fields, date_list,date_Table,t_table);
		   ArrayList class_list=reliefKqClass.getClassList(date_list,relief_id,relief_day);
		   reliefKqClass.shiftReliefClassGroupTemp(date_list,rest_list,group_list,t_table,class_list,feast_postpone,rest_postpone);
		   cycleKqClass.insertClassToShift(t_table);
		   //删除排班信息表中的信息
		   cycleKqClass.deleteKQ_org_dept_shift(start_date,end_date,group_list,"@G");
		   // 将排班信息添加到排班信息表
		   cycleKqClass.insertIntoKQ_org_dept_shift(date_list,rest_list,group_list,"kq_org_dept_shift",class_list,feast_postpone,rest_postpone,"@G");
		   //更新
		   updateNull(group_list);
		   
	   }else if("0".equals(selected_object))
	   {
		   /**********按人员排序**********/
		   ArrayList emplist=cycleKqClass.getEmpList(right_fields);
		   cycleKqClass.employeeInsertTemp(emplist, date_list,date_Table,t_table,kq_type);
		   ArrayList class_list=reliefKqClass.getClassList(date_list,relief_id,relief_day);
		   reliefKqClass.shiftReliefClassTemp(date_list,emplist,t_table,class_list,feast_postpone,rest_postpone);
		   cycleKqClass.insertClassToShift(t_table);
	   }
	   baseClassShift.dropTable(t_table);
       baseClassShift.dropTable(date_Table); 
	}

	private void updateNull(List list) {
		String update2="update kq_org_dept_shift set class_id='0' where class_id is Null and org_dept_id=?";
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			List li = new ArrayList();
			for (int i = 0; i < list.size(); i++) 
			{
				List li1 = new ArrayList();
				li1.add(list.get(i));
				li.add(li1);
			}
			dao.batchUpdate(update2,li);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
