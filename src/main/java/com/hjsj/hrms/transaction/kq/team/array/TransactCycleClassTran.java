package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.team.KqClassArrayConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class TransactCycleClassTran extends IBusiness implements KqClassArrayConstant {
	public void execute() throws GeneralException
	{
		String cycle_id=(String)this.getFormHM().get("cycle_id");
		String cycle_flag=(String)this.getFormHM().get("cycle_flag");
		String cycle_name=(String)this.getFormHM().get("cycle_name");
		
		if(cycle_flag==null||cycle_flag.length()<=0)
			return;
		if("add".equals(cycle_flag))
		{
			if(cycle_name!=null||cycle_name.length()>0)
			{
				cycleAdd(cycle_name);
			}
		}else if("del".equals(cycle_flag))
		{
			if(cycle_id!=null||cycle_id.length()>0)
			{
				cycleDelete(cycle_id);
			}
		}
	}
    public void cycleDelete(String cycle_id)
    {
    	StringBuffer sql= new StringBuffer();
		 sql.append("delete from "+kq_shift_table);
	     sql.append(" where "+kq_shift_ID+"=?");
	     ArrayList list=new ArrayList();
	     list.add(cycle_id);
	     ContentDAO dao=new ContentDAO(this.getFrameconn());
	     try
	     {
	    	 dao.delete(sql.toString(),list);
	    	 this.getFormHM().put("cycle_id","");
	    	 this.getFormHM().put("cycle_flag","");
	 		 this.getFormHM().put("cycle_name","");
	     }catch(Exception  e)
	     {
	    	 e.printStackTrace();
	     }
    }
    public void cycleAdd(String cycle_name)
    {
    	if(cycle_name==null||cycle_name.length()<=0)
    		return;
    	StringBuffer sql= new StringBuffer();    	
	     ContentDAO dao=new ContentDAO(this.getFrameconn());
	     try
	     {
	    	 IDGenerator idg=new IDGenerator(2,this.getFrameconn());
	    	 String cycle_id=idg.getId(this.kq_shift_table+"."+this.kq_shift_ID).toUpperCase();
			 sql.append(" insert into "+this.kq_shift_table);
		     sql.append(" ("+this.kq_shift_ID+","+this.kq_shift_name+")");
		     sql.append(" values(?,?)");
		     ArrayList list=new ArrayList();
		     list.add(cycle_id);
		     list.add(cycle_name);
	    	 dao.insert(sql.toString(),list);
	    	 this.getFormHM().put("cycle_id",cycle_id);
	    	 this.getFormHM().put("cycle_flag","");
	 		 this.getFormHM().put("cycle_name","");
	     }catch(Exception  e)
	     {
	    	 e.printStackTrace();
	     }
    }
}
