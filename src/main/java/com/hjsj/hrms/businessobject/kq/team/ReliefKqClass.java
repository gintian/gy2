package com.hjsj.hrms.businessobject.kq.team;

import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class ReliefKqClass {
	private Connection conn;	
	private UserView userView;
    public ReliefKqClass(){}
    public ReliefKqClass(Connection conn,UserView userView)
    {
    	this.conn=conn;
    	this.userView=userView;
    }
	public void shiftReliefClassTemp(ArrayList date_list,ArrayList emplist,String t_table,ArrayList class_list,String feast_postpone,String rest_postpone)
	throws GeneralException
	{
		String updateSQL="update "+t_table+" set class_id=? where q03z0=? and nbase=? and a0100=? ";
		String class_id="";		
		String nbase="";
		String a0100="";		
		String cur_day="";
		ArrayList list=new ArrayList(); 
		int s=0;		
		String sql="";
		RowSet rs=null;
		String flag="";
		ContentDAO  dao=new ContentDAO(this.conn);
		try
		{
			
			for(int i=0;i<date_list.size();i++)		
			{
				 cur_day =date_list.get(i).toString();	
				 class_id=class_list.get(s).toString();
				 boolean isCorrect=false;
				 for(int v=0;v<emplist.size();v++)
				 {
					  ArrayList one_list=new ArrayList();
					  ArrayList empOnelist=(ArrayList)emplist.get(v);
					  nbase=empOnelist.get(0).toString();
				      a0100=empOnelist.get(1).toString();
				      sql="select flag from "+t_table+" where a0100='"+a0100+"' and nbase='"+nbase+"' and q03z0='"+cur_day+"'";
				      rs=dao.search(sql);
				      if(rs.next()) {
                          flag=rs.getString("flag");
                      }
				      if(flag!=null&& "3".equals(flag)&& "1".equals(feast_postpone))
				      {
				    	  one_list.add("0");
				      }else if(flag!=null&& "2".equals(flag)&& "1".equals(rest_postpone))
				      {
				    	  one_list.add("0");
				      }else
				      {
				    	  one_list.add(class_id);
				    	  isCorrect=true;
				      }  
					  one_list.add(cur_day);
					  one_list.add(nbase);
					  one_list.add(a0100);
					  list.add(one_list);
				   }
				 if(isCorrect) {
                     s++;
                 }
			}
			//System.out.println(list);
			
			dao.batchUpdate(updateSQL,list);
			String update2="update "+t_table+" set class_id='0' where class_id is Null";
			dao.update(update2);
			/*String update="";
			 if(feast_postpone.equals("1"))
			 {
				 update="update "+t_table+" set class_id='0' where flag='3'";
				 dao.update(update);	 
			 }
			 if(rest_postpone.equals("1"))
			 {
				 update="update "+t_table+" set class_id='0' where flag='2'";
				 dao.update(update);
			 }*/
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
	    	  if(rs!=null){
	    		  try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	  }
	      }
	}
	/**
	 * 对个人倒班排列班次
	 * @param date_list
	 * @param relief_id
	 * @param relief_day
	 * @return
	 */
	public ArrayList getClassList(ArrayList date_list,String[]relief_id,String[]relief_day)
	{
		
		ArrayList class_list=new ArrayList();		
		String class_id="";		
		Date cur_date=null;
		for(int i=0;i<date_list.size();)		
		{
			
			 for(int j=0;j<relief_day.length;j++)
			 {
				 if(i>=date_list.size()) {
                     break;
                 }
				 String start_date =date_list.get(i).toString();	
				 Date s_date=DateUtils.getDate(start_date,"yyyy.MM.dd");
				 String n_day=relief_day[j];
				 if(n_day==null||n_day.length()<=0) {
                     n_day="1";
                 }
				 cur_date=DateUtils.addDays(s_date,Integer.parseInt(n_day));
				 int diff=RegisterDate.diffDate(s_date,cur_date); 
				 for(int v=0;v<diff;v++)
				 {
					 class_id=relief_id[j];
					 class_list.add(class_id);
					 i++;
					 if(i>=date_list.size()) {
                         break;
                     }
				 }				 
			 }
		}		
		return class_list;
	}
	/**
	 * 分组排班
	 * @param date_list 日子
	 * @param group_list
	 * @param t_table
	 * @param class_list
	 * @param feast_postpone
	 * @param rest_postpone
	 * @throws GeneralException
	 */
	public void shiftReliefClassGroupTemp(ArrayList date_list,ArrayList rest_list,ArrayList group_list,String t_table,ArrayList class_list,String feast_postpone,String rest_postpone)
	throws GeneralException
	{
		String updateSQL="update "+t_table+" set class_id=? where q03z0=? and group_id=?";
		String class_id="";		
		String group_id="";			
		String cur_day="";
		String stuts="";		
		ArrayList list=new ArrayList(); 
		int s=0;
		for(int i=0;i<date_list.size();i++)		
		{
			 cur_day =date_list.get(i).toString();	
			 class_id=class_list.get(s).toString();
			 ArrayList ro_list=(ArrayList)rest_list.get(i);
			 stuts=ro_list.get(2).toString();
			 for(int v=0;v<group_list.size();v++)
			 {
				  ArrayList one_list=new ArrayList();
				  group_id=group_list.get(v).toString();
				  if(stuts!=null&& "3".equals(stuts)&& "1".equals(feast_postpone))//年假
                  {
                      one_list.add("0");
                  } else if(stuts!=null&& "2".equals(stuts)&& "1".equals(rest_postpone))//公休
				  {
					 one_list.add("0");
				  }else
				  {
					  one_list.add(class_id);
//					  s++;
				  }
				  	  
				  one_list.add(cur_day);
				  one_list.add(group_id);				
				  list.add(one_list);
			 }
			 if(stuts!=null&& "2".equals(stuts)&& "1".equals(rest_postpone))//公休
			 {
				 continue;
			 }else
			 {
				 s++;
			 }
		}
		//System.out.println(list);
		ContentDAO  dao=new ContentDAO(this.conn);
		try
		{
			dao.batchUpdate(updateSQL,list);
			String update2="update "+t_table+" set class_id='0' where class_id is Null";
			dao.update(update2);
			/*String update="";
			 if(feast_postpone.equals("1"))
			 {
				 update="update "+t_table+" set class_id='0' where flag='3'";
				 dao.update(update);	 
			 }
			 if(rest_postpone.equals("1"))
			 {
				 update="update "+t_table+" set class_id='0' where flag='2'";
				 dao.update(update);
			 }*/
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
