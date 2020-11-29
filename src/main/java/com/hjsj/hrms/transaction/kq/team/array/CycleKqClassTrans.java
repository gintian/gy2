package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.businessobject.kq.team.BaseClassShift;
import com.hjsj.hrms.businessobject.kq.team.CycleKqClass;
import com.hjsj.hrms.businessobject.kq.team.KqClassArrayConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;

public class CycleKqClassTrans  extends IBusiness{

	public void execute() throws GeneralException
	{
		String start_date=(String)this.getFormHM().get("start_date");  //开始时间
		String end_date=(String)this.getFormHM().get("end_date");  //结束时间
		String cycle_id=(String)this.getFormHM().get("cycle_id");
		String[] cycle_ids=(String[])this.getFormHM().get("cycle_ids");
		String[] cycle_days=(String[])this.getFormHM().get("cycle_days");
		   
		String[] new_cycle_days = new String[cycle_days.length];
		for (int i = 0; i < cycle_days.length; i++) //循环天数是整数
		{
			String day = cycle_days[i];
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
		cycle_days = new_cycle_days;
		
		CycleKqClass cycleKqClass=new CycleKqClass(this.getFrameconn(),this.userView);	
		cycleKqClass.saveKqShiftClass(cycle_id,cycle_ids,cycle_days);//保存周期班次		   
		ArrayList shift_list=cycleKqClass.shiftOrderclass(cycle_ids,cycle_days);	   
		BaseClassShift baseClassShift=new BaseClassShift(this.userView,this.getFrameconn());
	   	String t_table=baseClassShift.tempClassTable();
	    baseClassShift.deleteTable(t_table);
	   	String date_Table=baseClassShift.creat_KqTmp_Table(this.userView.getUserId());
	   	ArrayList date_list=baseClassShift.getDatelist(start_date,end_date); 
	    String take_turns=(String)this.getFormHM().get("take_turns");	   
	    if(take_turns==null||!"1".equals(take_turns))
		   cycleShift(baseClassShift,cycleKqClass,date_list,cycle_ids,cycle_days,t_table,date_Table);
	   else
		   cycleTaketurnShift(baseClassShift,cycleKqClass,date_list,shift_list,t_table,date_Table); 
	}   
	/**
	 * 周期性轮班（最开始的周期班)每天轮一人
	 * @param baseClassShift
	 * @param cycleKqClass
	 * @param date_list
	 * @param shift_list//班次list
	 * @param t_table//数据临时表
	 * @param date_Table//时间临时表
	 * @throws GeneralException
	 */
	private void cycleTaketurnShift(BaseClassShift baseClassShift,CycleKqClass cycleKqClass,ArrayList date_list,
			ArrayList shift_list,String t_table,String date_Table)throws GeneralException
	{
		  
		String selected_object=(String)this.getFormHM().get("selected_object");//1:组0：人
		String rest_postpone=(String)this.getFormHM().get("rest_postpone");
		String feast_postpone=(String)this.getFormHM().get("feast_postpone");
		if(feast_postpone==null||feast_postpone.length()<=0)
		    	feast_postpone="0";
		if(rest_postpone==null||rest_postpone.length()<=0)
		    	rest_postpone="0";
		if(selected_object==null||selected_object.length()<=0)
			   return ;
		String[] right_fields = (String[]) this.getFormHM().get("right_fields");//班组list	
	   	String kq_type="02";	  
		if("1".equals(selected_object))
		{
			   /**********按班组排序**********/
			   //ArrayList emplist=cycleKqClass.getEmpFromGroup(right_fields);
			   ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
			   String b0110=managePrivCode.getPrivOrgId();  
			   ArrayList restList=IfRestDate.search_RestOfWeek(b0110,userView,this.getFrameconn());
			   String rest_date=restList.get(0).toString();
			   String rest_b0110=restList.get(1).toString();
			   ArrayList rest_list =baseClassShift.initializtion_date_Table(date_list,rest_date,date_Table,rest_b0110,b0110);
			   cycleKqClass.groupEmployeeInsertTemp(right_fields, date_list,date_Table,t_table);
			   cycleKqClass.shiftCycleClassGroupTemp(date_list,rest_list,right_fields,t_table,shift_list,feast_postpone, rest_postpone,"1");
			   cycleKqClass.insertClassToShift(t_table);
			   dropka_org_dept_shift(right_fields,date_Table); //先删除排班表中存在的班组
			   insertkq_org_dept_shift(right_fields, date_list,date_Table); //插入
			   shiftkq_org_dept_shift(date_list,rest_list,right_fields,t_table,shift_list,feast_postpone, rest_postpone,"1");  //更新
		}else if("0".equals(selected_object))
	    {
			   /**********按人员排序**********/
			   ArrayList emplist=cycleKqClass.getEmpList(right_fields);
			   cycleKqClass.employeeInsertTemp(emplist, date_list,date_Table,t_table,kq_type);
			   cycleKqClass.shiftCycleClassTemp(date_list,emplist,t_table,shift_list,feast_postpone, rest_postpone,"0");
			   cycleKqClass.insertClassToShift(t_table);
		}
		baseClassShift.dropTable(t_table);
	    baseClassShift.dropTable(date_Table); 
	}
	/**
	 * 按人排班
	 * @param baseClassShift
	 * @param cycleKqClass
	 * @param date_list
	 * @param shift_list
	 * @param t_table
	 * @param date_Table
	 * @throws GeneralException
	 */
	private void cycleShift(BaseClassShift baseClassShift,CycleKqClass cycleKqClass,ArrayList date_list,
			String [] ids,String days[],String t_table,String date_Table)throws GeneralException
	{
		String selected_object=(String)this.getFormHM().get("selected_object");//1:组0：人
		String rest_postpone=(String)this.getFormHM().get("rest_postpone");
		String feast_postpone=(String)this.getFormHM().get("feast_postpone");
		if(feast_postpone==null||feast_postpone.length()<=0)
		    	feast_postpone="0";
		if(rest_postpone==null||rest_postpone.length()<=0)
		    	rest_postpone="0";
		if(selected_object==null||selected_object.length()<=0)
			   return ;
		String[] right_fields = (String[]) this.getFormHM().get("right_fields");	
		String kq_type="02";	  
		if("1".equals(selected_object))
		{
			   /**********按班组排序**********/
			   //ArrayList emplist=cycleKqClass.getEmpFromGroup(right_fields);
			   ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
			   String b0110=managePrivCode.getPrivOrgId();  
			   ArrayList restList=IfRestDate.search_RestOfWeek(b0110,userView,this.getFrameconn());
			   String rest_date=restList.get(0).toString();
			   String rest_b0110=restList.get(1).toString();
			   ArrayList rest_list =baseClassShift.initializtion_date_Table(date_list,rest_date,date_Table,rest_b0110,b0110);
			   cycleKqClass.groupEmployeeInsertTemp(right_fields, date_list,date_Table,t_table);
			   cycleKqClass.shiftCycleGroupTemp(date_list,rest_list,right_fields,t_table,ids,days,feast_postpone, rest_postpone,"0");
			   cycleKqClass.insertClassToShift(t_table);
			   dropka_org_dept_shift(right_fields,date_Table); //先删除排班表中存在的班组
			   insertkq_org_dept_shift(right_fields, date_list,date_Table); //插入
			   shiftCycleGroupkq_org_dept_shift(date_list,rest_list,right_fields,t_table,ids,days,feast_postpone, rest_postpone,"0");  //更新
		}else if("0".equals(selected_object))
	    {
			   /**********按人员排序**********/
			   ArrayList emplist=cycleKqClass.getEmpList(right_fields);
			   cycleKqClass.employeeInsertTemp(emplist, date_list,date_Table,t_table,kq_type);
			   cycleKqClass.shiftCycleTemp(date_list,emplist,t_table,ids,days,feast_postpone,rest_postpone,"0");
			   cycleKqClass.insertClassToShift(t_table);			   
		}
		baseClassShift.dropTable(t_table);
	    baseClassShift.dropTable(date_Table); 
	}
	/**
	 * 将排班信息记录到部门排班表 先删除,已经存在的班组
	 * @param right_fields 班组String[]
	 * @param date_Table 时间表
	 */
	private void dropka_org_dept_shift(String[] right_fields,String date_Table)
	{
		try
		{
			for(int i=0;i<right_fields.length;i++)
			{
				String group_id=right_fields[i];
				deleteka_org_dept(group_id,date_Table);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private void deleteka_org_dept(String group_id,String date_Table)
	{
		StringBuffer deleteSQL = new StringBuffer();
		ArrayList deletelist= new ArrayList();
		deleteSQL.append("delete from kq_org_dept_shift where kq_org_dept_shift.q03z0 in ");
		deleteSQL.append("(select "+date_Table+".Sdate from "+date_Table+")");
		deleteSQL.append(" and kq_org_dept_shift.org_dept_id='"+group_id+"' and kq_org_dept_shift.codesetid='@G'");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			dao.delete(deleteSQL.toString(), deletelist);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 插入
	 * @param right_fields 班组
	 * @param date_list 时间
	 * @param date_Table  时间表
	 */
	private void insertkq_org_dept_shift(String[] right_fields,ArrayList date_list,String date_Table)throws GeneralException
	{
		try
		{
			for(int i=0;i<right_fields.length;i++)
			{
				String group_id=right_fields[i];							
				insertkq_org(date_Table,group_id);//插入临时表			   
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	private void insertkq_org(String date_Table,String group_id)throws GeneralException
	{
		StringBuffer insertSQL = new StringBuffer();
		String codesetid ="@G";
		insertSQL.append("INSERT INTO kq_org_dept_shift(org_dept_id,q03z0,class_id,codesetid)");
		insertSQL.append("SELECT '"+group_id+"',Sdate,0,'"+codesetid+"' FROM "+date_Table+"");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			ArrayList list=new ArrayList();
    		dao.insert(insertSQL.toString(),list); 
		}catch(Exception e)
		{
			e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		}
	}
	private void shiftkq_org_dept_shift(ArrayList date_list,ArrayList rest_list,String[] right_fields,String t_table,ArrayList class_list,String feast_postpone,String rest_postpone,String orderby)
	throws GeneralException
	{
		String updateSQL="update kq_org_dept_shift set class_id=? where q03z0=? and org_dept_id=? and codesetid=?";
		String class_id="";			
		String group_id="";
		ArrayList list=new ArrayList(); 
		ArrayList grouplist=new ArrayList();
		String stuts="";
		for(int i=0;i<right_fields.length;i++)
		{
			grouplist.add(right_fields[i]);
		}
		for(int i=0;i<date_list.size();i++)		
		{
			
			String cur_date =date_list.get(i).toString();
			ArrayList ro_list=(ArrayList)rest_list.get(i);
			stuts=ro_list.get(2).toString();
			int s=0;
			for(int j=0;j<grouplist.size();j++)
			{
				ArrayList one_list=new ArrayList(); 
				if(class_list.size()<=j)
				{
					break;
				}
				class_id=class_list.get(s).toString();
				group_id=grouplist.get(j).toString();
				if(stuts!=null&& "3".equals(stuts)&& "1".equals(feast_postpone))//年假
				     one_list.add("0");
				else if(stuts!=null&& "2".equals(stuts)&& "1".equals(rest_postpone))//公休
				{
					 one_list.add("0");
				}else
				{
					  one_list.add(class_id);
					  s++;
				}
				one_list.add(cur_date);
				one_list.add(group_id);
				one_list.add("@G");
				list.add(one_list);
			}
			if((stuts!=null&& "1".equals(stuts))||(stuts!=null&& "3".equals(stuts)&&!"1".equals(feast_postpone))||(stuts!=null&& "2".equals(stuts)&&!"1".equals(rest_postpone)))
			{
				if("0".equals(orderby))
				{
					grouplist.add(grouplist.size(),grouplist.get(0));
					grouplist.remove(0);	
				}else
				{
					grouplist.add(0,grouplist.get(grouplist.size()-1));
					grouplist.remove(grouplist.size()-1);
				}
			}
		}
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			dao.batchUpdate(updateSQL,list);
			String update2="update "+t_table+" set class_id='0' where class_id is Null";
			dao.update(update2);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 周期组排班不循环
	 * @param date_list
	 * @param rest_list
	 * @param right_fields
	 * @param t_table
	 * @param ids
	 * @param days
	 * @param feast_postpone
	 * @param rest_postpone
	 * @param orderby
	 * @throws GeneralException
	 */
	private void shiftCycleGroupkq_org_dept_shift(ArrayList date_list,ArrayList rest_list,String[] right_fields,String t_table,String [] ids,String days[],String feast_postpone,String rest_postpone,String orderby)
	throws GeneralException
	{
		String updateSQL="update kq_org_dept_shift set class_id=? where q03z0=? and org_dept_id=? and codesetid=?";
		String class_id="";			
		String group_id="";
		ArrayList list=new ArrayList(); 
		ArrayList grouplist=new ArrayList();
		String stuts="";
		for(int i=0;i<right_fields.length;i++)
		{
			grouplist.add(right_fields[i]);
		}
		for(int j=0;j<grouplist.size();j++)
		{
			group_id=grouplist.get(j).toString();
			ArrayList shiftclass=shiftOrderclass(ids,days);
			int s=0;
			for(int i=0;i<date_list.size();i++)		
			{
				ArrayList one_list=new ArrayList();
				String cur_date =date_list.get(i).toString();
				ArrayList ro_list=(ArrayList)rest_list.get(i);
				stuts=ro_list.get(2).toString();
				if(s>=shiftclass.size())
				{
					s=0;
				}
				class_id=shiftclass.get(s).toString();
				if(stuts!=null&& "3".equals(stuts)&& "1".equals(feast_postpone))//年假
				     one_list.add("0");
				else if(stuts!=null&& "2".equals(stuts)&& "1".equals(rest_postpone))//公休
				{
					 one_list.add("0");
				}else
				{
					  one_list.add(class_id);
					  s++;
				}
				one_list.add(cur_date);
				one_list.add(group_id);
				one_list.add("@G");
				list.add(one_list);
			}
			if("0".equals(orderby))
			{
				String newIds[]=new String[ids.length];
				String newdays[]=new String[days.length];
				for(int i=0;i<ids.length;i++)
				{
					if(i!=ids.length-1)
					{
						newIds[i]=ids[i+1];
						newdays[i]=days[i+1];
					}else
					{
						newIds[i]=ids[0];
						newdays[i]=days[0];
					}
				}
				ids=newIds;
				days=newdays;
			}else
			{
				String newIds[]=new String[ids.length];
				String newdays[]=new String[days.length];
				for(int i=0;i<ids.length;i++)
				{
					if(i==0)
					{
						newIds[0]=ids[ids.length-1];
						newdays[0]=days[days.length-1];
					}else
					{
						newIds[i]=ids[i-1];
						newdays[i]=days[i-1];
					}
				}
				ids=newIds;
				days=newdays;
			}
		}
		ContentDAO  dao=new ContentDAO(this.getFrameconn());
		try
		{
            dao.batchUpdate(updateSQL,list);
			String update2="update "+t_table+" set class_id='0' where class_id is Null";
			dao.update(update2);			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	private ArrayList shiftOrderclass(String [] ids,String days[])
	{
		ArrayList shift_class=new ArrayList();
		String id="";
		String class_id="";
		String day="";
		for(int i=0;i<ids.length;i++)
		{
			id=ids[i];
			class_id=getClassFormShiftClass(id);
			day=days[i];
			if(day==null||day.length()<=0)
				day="1";
			int in_day=Integer.parseInt(day);
			for(int r=1;r<=in_day;r++)
			{
				shift_class.add(class_id);				
			}
		}
		return shift_class;
	}
	/**
	 * 通过id得到班次id
	 * @param id
	 * @return
	 */
	private String getClassFormShiftClass(String id)
	{
		String sql="select class_id from "+KqClassArrayConstant.kq_shift_class_table+" where id="+id+"";
		String class_id="";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		RowSet rs =null;
		try
		{
			rs=dao.search(sql);
			if(rs.next())
			{
				class_id=rs.getString("class_id");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
		    KqUtilsClass.closeDBResource(rs);
	    }
		
		return class_id;
	}
}
