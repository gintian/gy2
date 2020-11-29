package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hjsj.hrms.businessobject.kq.team.KqClassArrayConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SetCycleToClassTran extends IBusiness implements KqClassConstant ,KqClassArrayConstant {

	public void execute() throws GeneralException
	{
	  String addclass=(String)this.getFormHM().get("addclass");	  
	  ArrayList shift_class_list=(ArrayList)this.getFormHM().get("shift_class_list");
	  if(shift_class_list==null||shift_class_list.size()<=0)
	  {
		  shift_class_list=new ArrayList();
	  }
	  String cycle_id=(String)this.getFormHM().get("cycle_id");
	  String class_Array[]=addclass.split("`");
	  String type=(String)this.getFormHM().get("type");
	  type=type!=null?type:"";
	  if("relief".equalsIgnoreCase(type))
		  shift_class_list=reliefCalssList(class_Array,shift_class_list);
	  else if("cycle".equalsIgnoreCase(type))
	    shift_class_list=addNewKqShiftClass(cycle_id,class_Array,shift_class_list);
	  this.getFormHM().put("shift_class_list",shift_class_list);
	}
	/**
	 * 个人排班
	 * @param class_Array
	 * @param shift_class_list
	 * @return
	 */
	private ArrayList reliefCalssList(String class_Array[],ArrayList shift_class_list)
	{
		  StringBuffer class_str=new StringBuffer();
		  for(int i=0;i<class_Array.length;i++)
		  {
			  class_str.append("'"+class_Array[i].toString()+"',");
		  }
		  class_str.setLength(class_str.length()-1);
		  StringBuffer sql= new StringBuffer();
		  sql.append("select "+this.kq_class_id+","+this.kq_class_name+" from "+this.kq_class_table);
		  sql.append(" where "+this.kq_class_id+" in");
		  sql.append(" ("+class_str+")");
		  ContentDAO dao=new ContentDAO(this.getFrameconn());
		  try
		  {
			  this.frowset=dao.search(sql.toString());
			  CommonData vo = null;
			  while(this.frowset.next())
			  {
				 vo = new CommonData();
	 			 vo.setDataName(this.frowset.getString(this.kq_class_name));
	 			 vo.setDataValue(this.frowset.getString(this.kq_class_id));
	 			 shift_class_list.add(vo);
			  }
		  }catch(Exception e)
		  {
			e.printStackTrace();  
		  }
		  return shift_class_list;
	}
	/**
	 * 周期排班
	 * @param shift_id
	 * @param class_Array
	 * @param shift_class_list
	 * @return
	 */
    public ArrayList addNewKqShiftClass(String shift_id,String class_Array[],ArrayList shift_class_list)
    {
       if(class_Array==null||class_Array.length<=0)
           return null;
       StringBuffer insertSQL=new StringBuffer();
 	   insertSQL.append("insert into "+kq_shift_class_table);
 	   insertSQL.append(" (id,"+kq_shift_class_shiftID+","+kq_shift_class_classID+",days,"+kq_shift_class_seq+")");
 	   insertSQL.append(" values(?,?,?,?,?)");
 	   ArrayList list=new ArrayList();
 	   try
 	   {
 		  CommonData vo = null;
 		  for(int i=0;i<class_Array.length;i++)
 	 	   {
 	 		  IDGenerator idg=new IDGenerator(2,this.getFrameconn());
 		      String insertid=idg.getId("kq_shift_class.id");
 		      String class_id=class_Array[i];
 		      ArrayList one_list=new ArrayList();
 		      one_list.add(insertid);
 		      one_list.add(shift_id);
 		      one_list.add(class_id);
 		      one_list.add(new Integer("1"));
 		      one_list.add(new Integer("0"));
 		      list.add(one_list);
 		      vo = new CommonData();
 			  vo.setDataName(getClassName(class_id));
 			  vo.setDataValue(insertid);
 			  shift_class_list.add(vo);
 	 	   }
 		  ContentDAO dao=new ContentDAO(this.getFrameconn());
 		  dao.batchInsert(insertSQL.toString(),list);
 	   }catch(Exception e)
 	   {
 		   e.printStackTrace();
 	   }
 	   return shift_class_list;
 	   
    }
    private String getClassName(String class_id)
    {
      StringBuffer sql=new StringBuffer();
      sql.append("select name from "+kq_class_table);
      sql.append(" where class_id='"+class_id+"'");
      ContentDAO dao=new ContentDAO(this.getFrameconn());
      String name="";
      RowSet rs = null;
      try
      {
    	  rs=dao.search(sql.toString());
    	  if(rs.next())
    	  {
    		  name=rs.getString("name");
    	  }
      }catch(Exception e)
      {
    	e.printStackTrace();  
      }finally{
      	if(rs!=null)
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }  
      return name;
    }
}
