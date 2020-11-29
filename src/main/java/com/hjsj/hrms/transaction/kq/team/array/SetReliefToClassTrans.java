package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SetReliefToClassTrans extends IBusiness implements KqClassConstant {

	public void execute() throws GeneralException
	{
	  String addclass=(String)this.getFormHM().get("addclass");	  
	  ArrayList shift_class_list=(ArrayList)this.getFormHM().get("shift_class_list");
	  if(shift_class_list==null||shift_class_list.size()<=0)
	  {
		  shift_class_list=new ArrayList();
	  }
	  String class_Array[]=addclass.split("`");
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
	  this.getFormHM().put("shift_class_list",shift_class_list);
	}
    

}
