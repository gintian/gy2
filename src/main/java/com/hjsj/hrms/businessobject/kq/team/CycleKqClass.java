package com.hjsj.hrms.businessobject.kq.team;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 对于周期排班的处理
 * <p>Title:CycleKqClass.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Nov 4, 2006 1:26:49 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class CycleKqClass{

	private Connection conn;
	private UserView userView;
	
	public CycleKqClass()
	{
		
	}
	public CycleKqClass(Connection conn,UserView userView)
	{
		this.conn=conn;
		this.userView=userView;
	}	
	/**
	 * 保存周期班次对应基本班次信息 
	 * @param shift_id
	 * @param class_list
	 */
	public void saveKqShiftClass(String shift_id,String[] cycle_ids,String cycle_days[])throws GeneralException
	{
	   StringBuffer update=new StringBuffer();
	   update.append("update "+KqClassArrayConstant.kq_shift_class_table+" set ");
	   update.append(" days=?,"+KqClassArrayConstant.kq_shift_class_seq+"=? ");
	   update.append(" where id=?");
	   String cycle_id="";
	   String days="";
	   ArrayList shift_class=new ArrayList();
		for(int i=0;i<cycle_ids.length;i++)
		{
			cycle_id=cycle_ids[i];
			ArrayList one_list=new ArrayList();			
			if(cycle_days.length>i) {
                days=cycle_days[i];
            } else {
                days="1";
            }
			one_list.add(days);
			one_list.add(i+"");
			one_list.add(cycle_id);
			shift_class.add(one_list);
		}
	   try
	   {
		   ContentDAO dao=new ContentDAO(this.conn);
		   dao.batchUpdate(update.toString(),shift_class);
		   
	   }catch(Exception e)
	   {
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
	   }
	}
	/**
	 * 排列周期班次
	 * @param shift_id
	 * @param cycle_list
	 * @return
	 */
	public ArrayList getClassShiftList(String shift_id,String[] left_fields)
	throws GeneralException
	{
		String class_id="";
		ArrayList shift_class=new ArrayList();
		for(int i=0;i<left_fields.length;i++)
		{
			class_id=left_fields[i];
			ArrayList one_list=new ArrayList();
			one_list.add(shift_id);
			one_list.add(class_id);
			one_list.add(i+"");
			shift_class.add(one_list);
		}
		return shift_class;
	}
	/**
	 * 排列周期班次
	 * @param shift_id
	 * @param cycle_list
	 * @return
	 */
	public ArrayList getClassShiftList(String shift_id,String[] cycle_ids,String[] cycle_days)
	throws GeneralException
	{
		String class_id="";
		String days="";
		ArrayList shift_class=new ArrayList();
		for(int i=0;i<cycle_ids.length;i++)
		{
			class_id=cycle_ids[i];
			ArrayList one_list=new ArrayList();
			one_list.add(shift_id);
			one_list.add(class_id);
			if(cycle_days.length>i) {
                days=cycle_days[i];
            }
			one_list.add(days);
			one_list.add(i+"");
			shift_class.add(one_list);
		}
		return shift_class;
	}
	/**
	 * 人员信息插入临时表
	 * @param emplist
	 * @param date_list
	 * @param date_Table
	 * @param t_table
	 * @param kq_type
	 */
	public void employeeInsertTemp(ArrayList emplist,ArrayList date_list,String date_Table,String t_table,String kq_type)
	throws GeneralException
	{
		String old_b0110="";
		String nbase="";
		String a0100="";
		String rest_date="";
		String rest_b0110="";
		BaseClassShift baseClassShift=new BaseClassShift(this.userView,this.conn);
		try
		{
			for(int i=0;i<emplist.size();i++)
			{
				ArrayList one_list=(ArrayList)emplist.get(i);
				nbase=one_list.get(0).toString();
				a0100=one_list.get(1).toString();
				String b0110=baseClassShift.getEMpData(a0100,nbase,"b0110");
				if(b0110==null||b0110.length()<=0)
				{
					continue;
				}else if(!b0110.equals(old_b0110))
				{
					ArrayList restList=IfRestDate.search_RestOfWeek(b0110,userView,this.conn);
					rest_date=restList.get(0).toString();
					rest_b0110=restList.get(1).toString();		
					baseClassShift.initializtion_date_Table(date_list,rest_date,date_Table,rest_b0110,b0110);
					old_b0110=b0110;
				} 
				String sWhere="and a0100='"+a0100+"'";				
				//插入临时表, 此时要将人员的单位编码带上,不然的话,如果单位编码变了,在排班的时候从人员表和时间表取数会出现重复的记录			   
			    baseClassShift.insrtTempData(t_table,date_Table,nbase,"",sWhere,old_b0110);			
			}
		}catch(Exception e)
		{
          e.printStackTrace();			
          throw GeneralExceptionHandler.Handle(e);
		}		
	}
	/**
	 * 根据班次修改临时表的排班
	 * @param date_list
	 * @param emplist
	 * @param t_table
	 * @param class_list
	 * @param orderby
	 */
	public void shiftCycleClassTemp(ArrayList date_list,ArrayList emplist,String t_table,ArrayList class_list,String feast_postpone,String rest_postpone,String orderby)
	throws GeneralException
	{
		String updateSQL="update "+t_table+" set class_id=? where q03z0=? and nbase=? and a0100=? ";
		String class_id="";		
		String nbase="";
		String a0100="";
		ArrayList list=new ArrayList(); 
		String sql="";
		RowSet rs=null;
		String flag="";
		ContentDAO  dao=new ContentDAO(this.conn);		
		try
		{
			for(int i=0;i<date_list.size();i++)		
			{
				
				String cur_date =date_list.get(i).toString();
				int s=0;
				for(int j=0;j<emplist.size();j++)
				{
					ArrayList empOnelist=(ArrayList)emplist.get(j);
					nbase=empOnelist.get(0).toString();
					a0100=empOnelist.get(1).toString();
					ArrayList one_list=new ArrayList(); 
					if(class_list.size()<=j)
					{
						break;
					}
					class_id=class_list.get(s).toString();
					sql="select flag from "+t_table+" where a0100='"+a0100+"' and nbase='"+nbase+"' and q03z0='"+cur_date+"'";
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
				    	  s++;
				    }  
					one_list.add(cur_date);
					one_list.add(nbase);
					one_list.add(a0100);
					list.add(one_list);
				}
				if((flag!=null&& "1".equals(flag))||(flag!=null&& "3".equals(flag)&&!"1".equals(feast_postpone))||(flag!=null&& "2".equals(flag)&&!"1".equals(rest_postpone)))
				{
					if("0".equals(orderby))
					{
						emplist.add(emplist.size(),emplist.get(0));
						emplist.remove(0);	
					}else
					{
						emplist.add(0,emplist.get(emplist.size()-1));
						emplist.remove(emplist.size()-1);
					}
				}
				
			}
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
					e.printStackTrace();
				}
	    	  }
	      }
	}	
	/**
     * 如果selected_object='0'时把人员信息树组转换为list
     * @param right_fields
     * @return
     */
	public ArrayList getEmpList(String[] right_fields)
	{
		ArrayList emplist=new ArrayList();
		if(right_fields==null||right_fields.length<=0) {
            return emplist;
        }
		for(int i=0;i<right_fields.length;i++)
		{
			ArrayList one_list=new ArrayList();
			String message=right_fields[i];
			if(message==null||message.length()<=0) {
                continue;
            }
			String[] one_message=message.split("`");
			one_list.add(one_message[0]);
			one_list.add(one_message[1]);
			emplist.add(one_list);
		}
		return emplist;
	}
	public void insertClassToShift(String t_table)throws GeneralException
	{
		 String destTab=KqClassArrayConstant.kq_employ_shift_table;//目标表
		 String srcTab=t_table;//源表
		 String strJoin=destTab+".A0100="+srcTab+".A0100 and "+destTab+".nbase="+srcTab+".nbase and "+destTab+".q03z0="+srcTab+".q03z0";//关联串  xxx.field_name=yyyy.field_namex,....
		 //String  strSet=destTab+".class_id="+srcTab+".class_id";//更新串  xxx.field_name=yyyy.field_namex,....
		 String  strSet=destTab+".class_id="+srcTab+".class_id`"+destTab+".B0110="+srcTab+".B0110`"+destTab+".E0122="+srcTab+".E0122`"+destTab+".E01A1="+srcTab+".E01A1`"+destTab+".A0101="+srcTab+".A0101";//更新串  xxx.field_name=yyyy.field_namex,....
		 String strDWhere="";//destTab+".status='0'";//更新目标的表过滤条件
		 String strSWhere=destTab+".A0100="+srcTab+".A0100 and "+destTab+".nbase="+srcTab+".nbase and "+destTab+".q03z0="+srcTab+".q03z0";//源表的过滤条件  
		 String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
		 
		 update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
		 ContentDAO dao = new ContentDAO(this.conn);		
		try {			
			dao.update(update);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		StringBuffer  insertSQL=new StringBuffer();
		insertSQL.append("INSERT INTO kq_employ_shift(nbase,A0100,A0101,B0110,E0122,E01A1,Q03Z0,class_id,status)");
	    insertSQL.append(" SELECT a.nbase,a.A0100,a.A0101,a.B0110,a.E0122,a.E01A1,a.Q03Z0,a.class_id,0");
	    insertSQL.append(" FROM "+t_table+" a ");
	    insertSQL.append("WHERE NOT EXISTS(SELECT * FROM kq_employ_shift b");
	    insertSQL.append(" WHERE a.A0100=b.A0100 and a.nbase=b.nbase and a.Q03Z0=b.Q03Z0)");
	    
	    try {			
	    	ArrayList list=new ArrayList();
			dao.insert(insertSQL.toString(),list);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 将排班信息插入 班组排班表中
	 * @param date_list
	 * @param rest_list
	 * @param group_list
	 * @param t_table
	 * @param class_list
	 * @param feast_postpone
	 * @param rest_postpone
	 * @param codeset
	 */
	public void insertIntoKQ_org_dept_shift(ArrayList date_list,ArrayList rest_list,
			ArrayList group_list,String t_table,ArrayList class_list,String feast_postpone,
			String rest_postpone,String codeset) {
		StringBuffer updateSQL = new StringBuffer();
		updateSQL.append("insert into ");
		updateSQL.append(t_table);
		updateSQL.append("(class_id,q03z0,org_dept_id,codesetid) values(?,?,?,'");
		updateSQL.append(codeset);
		updateSQL.append("')");
		String class_id = "";		
		String group_id = "";			
		String cur_day = "";
		String stuts = "";		
		ArrayList list = new ArrayList(); 
		int s = 0;
		for(int i = 0; i < date_list.size(); i++) {
			 cur_day = date_list.get(i).toString();	
			 class_id = class_list.get(s).toString();
			 ArrayList ro_list = (ArrayList)rest_list.get(i);
			 stuts = ro_list.get(2).toString();
			 for(int v = 0; v < group_list.size(); v++) {
				  ArrayList one_list = new ArrayList();
				  group_id = group_list.get(v).toString();
				  if(stuts != null&& "3".equals(stuts) && "1".equals(feast_postpone)) {//年假
				     one_list.add("0");
				  } else if(stuts!=null&& "2".equals(stuts)&& "1".equals(rest_postpone)) {//公休
					 one_list.add("0");
				  } else {
					  one_list.add(class_id);
				  }
				  	  
				  one_list.add(cur_day);
				  one_list.add(group_id);				
				  list.add(one_list);
			 }
			 
			 if(stuts!=null&& "2".equals(stuts)&& "1".equals(rest_postpone)){//公休
				 continue;
			 } else {
				 s++;
			 }
		}
		
		try {
			ContentDAO dao  = new ContentDAO(this.conn);
			dao.batchInsert(updateSQL.toString(), list);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除班组的排班信息
	 * @param start
	 * @param end
	 * @param id
	 * @param codeSetId
	 */
	public void deleteKQ_org_dept_shift(String start, String end, List id, String codeSetId) {
		StringBuffer buff = new StringBuffer();
		buff.append("delete kq_org_dept_shift where");
		buff.append(" org_dept_id=? and codesetid='");
		buff.append(codeSetId);
		buff.append("' and q03z0>='");
		buff.append(start.replaceAll("-", "."));
		buff.append("' and q03z0<='");
		buff.append(end.replaceAll("-", "."));
		buff.append("'");
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			List list = new ArrayList();
			for (int i = 0; i < id.size(); i++) 
			{
				List list1 = new ArrayList();
				list1.add(id.get(i));
				list.add(list1);
			}
			dao.batchUpdate(buff.toString(), list);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public ArrayList shiftOrderclass(String [] left_fields)
	{
		ArrayList shift_class=new ArrayList();
		String class_id="";
		for(int i=0;i<left_fields.length;i++)
		{
			class_id=left_fields[i];
			
			shift_class.add(class_id);
		}
		return shift_class;
	}
	public ArrayList shiftOrderclass(String [] ids,String days[])
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
			if(day==null||day.length()<=0) {
                day="1";
            }
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
	public String getClassFormShiftClass(String id)
	{
		String sql="select class_id from "+KqClassArrayConstant.kq_shift_class_table+" where id="+id+"";
		String class_id="";
		ContentDAO dao=new ContentDAO(this.conn);
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
	    	  if(rs!=null){
	    		  try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    	  }
	      }
		return class_id;
	}
	/*****************按组排班***********************/
	/**
	 * 通过组文件编号得到人员信息
	 * @param group_list
	 * @return
	 */
	public ArrayList getEmpFromGroup(String[] group_Array)throws GeneralException
	{
		ArrayList emplist=new ArrayList();		
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs=null;	
		try
		{
			for(int i=0;i<group_Array.length;i++)
			{
				
							
				String group_id=group_Array[i];				
				String selectSQL="select nbase,a0100,b0110 from kq_group_emp where group_id='"+group_id+"'";
				rs=dao.search(selectSQL);
				while(rs.next())
				{
					ArrayList one_list=new ArrayList();
					one_list.add(rs.getString("nbase"));
					one_list.add(rs.getString("a0100"));
					one_list.add(rs.getString("b0110"));
					emplist.add(one_list);
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
	    	  if(rs!=null){
	    		  try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    	  }
	      }
		return emplist;
	}
	
	/**
	 * 组人员信息插入临时表
	 * @param right_fields
	 * @param date_list
	 * @param date_Table
	 * @param t_table
	 * @param kq_type
	 */
	public void groupEmployeeInsertTemp(String[] right_fields,ArrayList date_list,String date_Table,String t_table)
	throws GeneralException
	{
		
		BaseClassShift baseClassShift=new BaseClassShift(this.userView,this.conn);
		try
		{   
			//如果没有重复的人
			String name = baseClassShift.canEmployeeInsertTemp(right_fields);
			if(name.trim().length()>0)
			{
				throw new GeneralException(ResourceFactory.getProperty("以下人员存在于多个班组中，请处理后再排班：\n"+name));
			}
			else {
				for(int i=0;i<right_fields.length;i++)
				{
					String group_id=right_fields[i];							
				    baseClassShift.insrtTempGroupData(t_table,date_Table,group_id);//插入临时表			   
				}
			}
		}catch(Exception e)
		{
          e.printStackTrace();			
          throw GeneralExceptionHandler.Handle(e);
		}		
	}
	/**
	 * 根据组班次修改临时表的排班(按天轮班的情况)
	 * @param date_list
	 * @param emplist
	 * @param t_table
	 * @param class_list
	 * @param orderby
	 */
	public void shiftCycleClassGroupTemp(ArrayList date_list,ArrayList rest_list,String[] right_fields,String t_table,ArrayList class_list,String feast_postpone,String rest_postpone,String orderby)
	throws GeneralException
	{
		String updateSQL="update "+t_table+" set class_id=? where q03z0=? and group_id=?";
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
                {
                    one_list.add("0");
                } else if(stuts!=null&& "2".equals(stuts)&& "1".equals(rest_postpone))//公休
				{
					 one_list.add("0");
				}else
				{
					  one_list.add(class_id);
					  s++;
				}
				one_list.add(cur_date);
				one_list.add(group_id);				
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
	/**
	 * 通过组文件编号得到人员信息
	 * @param group_list
	 * @return
	 */
	public ArrayList getListFromGroupArray(String[] group_Array)throws GeneralException
	{
		ArrayList group_list=new ArrayList();			
		try
		{
			for(int i=0;i<group_Array.length;i++)
			{					
				group_list.add(group_Array[i]);						
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return group_list;
	}
	/**********************************************************/
	/**
	 * 判断周期班次对应表是否更新，没有更新则更新
	 * @throws GeneralException
	 */
	public void checkShiftTable()throws GeneralException
	{
		if(!checkShiftIDSave())
		{
			againMakeup();
		}
	}
	/**
	 * 判断是否存在id字段
	 * @return
	 */
	public boolean checkShiftIDSave()
	{
		boolean isCorrect=false;
		StringBuffer sql=new StringBuffer();
		sql.append("select * from kq_shift_class");
		RowSet rs=null;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			rs=dao.search(sql.toString());
			ResultSetMetaData rm=rs.getMetaData();
			int column_count=rm.getColumnCount();
			for(int i=1;i<=column_count;i++)
			{
				String column_name=rm.getColumnName(i);
				if(column_name==null||column_name.length()<=0) {
                    column_name="";
                }
				if("id".equalsIgnoreCase(column_name))
				{
					isCorrect=true;
					break;
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
	    	  if(rs!=null){
	    		  try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    	  }
	      }
		return isCorrect;
	}
	/**
	 * 重新创建表结构
	 *
	 */
	public void againMakeup()throws GeneralException
	{
		boolean isCorrect=true;		
		isCorrect=makeUpKq_Shift_Class();
		if(!isCorrect) {
            throw GeneralExceptionHandler.Handle(new GeneralException("","重构周期班与基本班次对应表错误","",""));
        }
		isCorrect=addIdValue();
		if(!isCorrect) {
            throw GeneralExceptionHandler.Handle(new GeneralException("","重构周期班与基本班次对应表错误","",""));
        }
	    isCorrect=setPriKeyForId();
	    if(!isCorrect) {
            throw GeneralExceptionHandler.Handle(new GeneralException("","重构周期班与基本班次对应表错误","",""));
        }
	}	
	/**
	 * 修改id主键
	 * @return
	 */
	public boolean setPriKeyForId()
	{
		boolean isCorrect=true;	
		try
		{
			String temp_table=buildTempTable();	
			buildNewShift_ClassTable(temp_table);
			KqUtilsClass kqUtilsClass=new KqUtilsClass(this.conn);
			kqUtilsClass.dropTable(temp_table);
		}catch(Exception e)
		{
			e.printStackTrace();
			isCorrect=false;	
		}
		return isCorrect;
	}
	/**
	 * 添加周期班与基本班次对应编号和天数
	 * @return
	 */
	public boolean addIdValue()
	{
		boolean isCorrect=true;		
		StringBuffer sql=new StringBuffer();
		sql.append("select * from id_factory where sequence_name='kq_shift_class.id'");
		RowSet rs=null;
		try
		{
			ContentDAO dao =new ContentDAO(this.conn);
			rs=dao.search(sql.toString());
			if(!rs.next())
			{
				StringBuffer insertSQL=new StringBuffer();
				insertSQL.append("insert into id_factory  (sequence_name, sequence_desc, minvalue, maxvalue, auto_increase, increase_order, prefix, suffix, currentid, id_length, increment_O)");
				insertSQL.append(" values ('kq_shift_class.id', '周期班与基本班次对应编号', 1, 99999999, 1, 1, Null, Null, 0, 10, 1)");
				ArrayList list=new ArrayList();
				dao.insert(insertSQL.toString(),list);				
			}
			sql=new StringBuffer();
			sql.append("select shift_id,class_id,seq from kq_shift_class");
			rs=dao.search(sql.toString());
			String shift_id="";
			String class_id="";
			String seq="";
			String updateSQL="update kq_shift_class set id=?,days=? where shift_id=? and class_id=? and seq=?";
			ArrayList list=new ArrayList();
			while(rs.next())
			{
				shift_id=rs.getString("shift_id");
				class_id=rs.getString("class_id");
				seq=rs.getString("seq");
				ArrayList one_list=new ArrayList();
				IDGenerator idg=new IDGenerator(2,this.conn);
		    	String insertid=idg.getId(("kq_shift_class.id").toUpperCase());
		    	one_list.add(insertid);
		    	one_list.add(new Integer("1"));
		    	one_list.add(new Integer(shift_id));
		    	one_list.add(new Integer(class_id));
		    	one_list.add(new Integer(seq));
		    	list.add(one_list);
			}
			dao.batchUpdate(updateSQL,list);
		}catch(Exception e)
		{
			e.printStackTrace();
			isCorrect=false;
		}finally{
	    	  if(rs!=null){
	    		  try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    	  }
	      }
		return isCorrect;
	}
	/**
	 * 修改表结构，添加序号和天，把原来的主键去除属性
	 *
	 */
	public boolean  makeUpKq_Shift_Class()
	{
		boolean isCorrect=true;
		DbWizard dbWizard =new DbWizard(this.conn);
		Table table=new Table("kq_shift_class");
		Field temp = new Field("id","序号");
		temp.setDatatype(DataType.INT);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("days","天");
		temp.setDatatype(DataType.INT);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		try
		{
			dbWizard.addColumns(table);			
			dbWizard.dropPrimaryKey("kq_shift_class");
		}catch(Exception e)
		{
			e.printStackTrace();
			isCorrect=false;
		}
		return isCorrect;
	}
	/**
	 * 建立新的kq_shift_class表
	 * @return
	 */
	public void  buildNewShift_ClassTable(String temp_table)throws GeneralException
	{
		String table_name="kq_shift_class";
		KqUtilsClass kqUtilsClass=new KqUtilsClass(this.conn);
		kqUtilsClass.dropTable(table_name);
		DbWizard dbWizard =new DbWizard(this.conn);
		Table table=new Table(table_name);	 
		Field temp = new Field("id","序号");
		temp.setDatatype(DataType.INT);
		temp.setKeyable(true);	
		temp.setNullable(false);
		table.addField(temp);
		temp = new Field("shift_id","周期表");
		temp.setDatatype(DataType.INT);
		table.addField(temp);
		temp = new Field("class_id","班次表");
		temp.setDatatype(DataType.INT);
		table.addField(temp);
		temp = new Field("days","天");
		temp.setDatatype(DataType.INT);
		table.addField(temp);
		temp = new Field("seq","排序");
		temp.setDatatype(DataType.INT);
		table.addField(temp);
		try
		{
			dbWizard.createTable(table);
		    StringBuffer sql=new StringBuffer();
			sql.append("insert into "+table_name+" (id,shift_id,class_id,days,seq)");
			sql.append("select id,shift_id,class_id,days,seq from "+temp_table+"");
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList list=new ArrayList();
			dao.insert(sql.toString(),list);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}	
	}
	/**
	 * 建立临时表
	 * @return
	 * @throws GeneralException
	 */
	public String  buildTempTable()throws GeneralException
	{
//		String table_name="kq_shift_class_Tmp_"+this.userView.getUserName();
		String table_name="t#"+this.userView.getUserName()+"_kq_shift_class";
		KqUtilsClass kqUtilsClass=new KqUtilsClass(this.conn);
		kqUtilsClass.dropTable(table_name);
		DbWizard dbWizard =new DbWizard(this.conn);
		Table table=new Table(table_name);	 
		Field temp = new Field("id","序号");
		temp.setDatatype(DataType.INT);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("shift_id","周期表");
		temp.setDatatype(DataType.INT);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("class_id","班次表");
		temp.setDatatype(DataType.INT);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("days","天");
		temp.setDatatype(DataType.INT);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("seq","排序");
		temp.setDatatype(DataType.INT);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		try
		{
			dbWizard.createTable(table);
		    StringBuffer sql=new StringBuffer();
			sql.append("insert into "+table_name+" (id,shift_id,class_id,days,seq)");
			sql.append("select id,shift_id,class_id,days,seq from kq_shift_class");
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList list=new ArrayList();
			dao.insert(sql.toString(),list);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}	
		return table_name;
	}
	/**
	 * 
	 * @param date_list
	 * @param emplist
	 * @param t_table
	 * @param ids
	 * @param days
	 * @param feast_postpone
	 * @param rest_postpone
	 * @param orderby
	 * @throws GeneralException
	 */
	public void shiftCycleTemp(ArrayList date_list,ArrayList emplist,String t_table,String [] ids,String days[],String feast_postpone,String rest_postpone,String orderby)
	throws GeneralException
	{
		String updateSQL="update "+t_table+" set class_id=? where q03z0=? and nbase=? and a0100=? ";
		String class_id="";		
		String nbase="";
		String a0100="";
		ArrayList list=new ArrayList(); 
		String sql="";
		RowSet rs=null;
		String flag="";
		ContentDAO  dao=new ContentDAO(this.conn);	
		int c=0;
		try
		{
			for(int j=0;j<emplist.size();j++)
			{
				ArrayList empOnelist=(ArrayList)emplist.get(j);
				nbase=empOnelist.get(0).toString();
				a0100=empOnelist.get(1).toString();
				ArrayList shiftclass=shiftOrderclass(ids,days);
				c=0;
				for(int i=0;i<date_list.size();i++)		
				{
					String cur_date =date_list.get(i).toString();
					ArrayList one_list=new ArrayList();
					if(c>=shiftclass.size())
					{
						c=0;
					}
					class_id=shiftclass.get(c).toString();
					sql="select flag from "+t_table+" where a0100='"+a0100+"' and nbase='"+nbase+"' and q03z0='"+cur_date+"'";
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
				    	  c++;
				    }  
				    one_list.add(cur_date);
					one_list.add(nbase);
					one_list.add(a0100);
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
			dao.batchUpdate(updateSQL,list);
			String update2="update "+t_table+" set class_id='0' where class_id is Null";
			dao.update(update2);			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
	    	  if(rs!=null){
	    		  try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    	  }
	      }
	}
	/**
	 * 周期组排班
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
	public void shiftCycleGroupTemp(ArrayList date_list,ArrayList rest_list,String[] right_fields,String t_table,String [] ids,String days[],String feast_postpone,String rest_postpone,String orderby)
	throws GeneralException
	{
		String updateSQL="update "+t_table+" set class_id=? where q03z0=? and group_id=?";
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
                {
                    one_list.add("0");
                } else if(stuts!=null&& "2".equals(stuts)&& "1".equals(rest_postpone))//公休
				{
					 one_list.add("0");
				}else
				{
					  one_list.add(class_id);
					  s++;
				}
				one_list.add(cur_date);
				one_list.add(group_id);				
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
		ContentDAO  dao=new ContentDAO(this.conn);
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
}
