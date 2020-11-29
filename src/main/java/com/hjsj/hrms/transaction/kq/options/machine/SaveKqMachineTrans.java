package com.hjsj.hrms.transaction.kq.options.machine;

import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 保存考勤机信息
 * <p>Title:SaveKqMachineTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Dec 18, 2006 10:03:36 AM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class SaveKqMachineTrans extends IBusiness {

	public void execute() throws GeneralException
	{
		RecordVo machine=(RecordVo)this.getFormHM().get("machine");
		String e_flag=(String)this.getFormHM().get("e_flag");
		if(e_flag==null||e_flag.length()<=0)
		{
			return;
		}
		if(machine==null)
		{
			return;
		}		
		if(!checkSameName(machine,e_flag))
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.machine.warn.machine_name"),"",""));
		if(!checkSameId(machine,e_flag))
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.machine.warn.machine_id"),"",""));
		if("add".equalsIgnoreCase(e_flag))
		{
			
			addMachine(machine);
		}else if("up".equalsIgnoreCase(e_flag))
		{
			upMachine(machine);
		}
	}
	public void upMachine(RecordVo machine_vo)throws GeneralException
	{
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			dao.updateValueObject(machine_vo);
		}catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.machine.error"),"",""));
		}
		
	}
	/**
	 * 新增
	 * @param machine_vo
	 * @throws GeneralException
	 */
	public void addMachine(RecordVo machine_vo)throws GeneralException
	{
		IDGenerator idg=new IDGenerator(2,this.getFrameconn());
		
		try
		{   String machine_no=machine_vo.getString("machine_no");
		    if(machine_no==null||machine_no.length()<=0)
		    {
		    	machine_no=getMAX_Machine_NO();
		    }			 
			String location_id=idg.getId("kq_machine_location.location_id").toUpperCase();
		    ContentDAO dao=new ContentDAO(this.getFrameconn());
			machine_vo.setString("location_id",location_id);
			machine_vo.setString("machine_no",machine_no);
			dao.addValueObject(machine_vo);
			if(location_id!=null&&location_id.length()>0)
			{
				int l_id=Integer.parseInt(location_id);
				UserObjectBo user_bo = new UserObjectBo(this.getFrameconn());
				user_bo.saveResource(l_id+"", this.userView,
						IResourceConstant.KQ_MACH);
			}
		}catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.machine.error"),"",""));
		}
   	    
	}
	/**
	 * 得到最大的机器编号
	 * @return
	 */
	public String getMAX_Machine_NO()
	{
    	String v_type=Sql_switcher.sqlToInt("machine_no");    	  
		String sql="select max("+v_type+")+1 as machine_no from kq_machine_location";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String machine_no="";
		try
		{
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				machine_no=this.frowset.getString("machine_no");
			}
			if(machine_no==null||machine_no.length()<=0)
				machine_no="1";
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return machine_no;
	}
	private boolean checkSameName(RecordVo machine_vo,String flag)
	{
		StringBuffer sql=new StringBuffer();
		if("add".equals(flag))
		  sql.append("select * from kq_machine_location where name='"+machine_vo.getString("name")+"'");
		else 
		{
			sql.append("select * from kq_machine_location where name='"+machine_vo.getString("name")+"'");
			sql.append(" and location_id<>'"+machine_vo.getString("location_id")+"'");
		}	
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
				return false;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	private boolean checkSameId(RecordVo machine_vo,String flag){
		StringBuffer sql=new StringBuffer();
		if("add".equals(flag))
		  sql.append("select * from kq_machine_location where machine_no='"+machine_vo.getString("machine_no")+"'");
		else 
		{
			sql.append("select * from kq_machine_location where machine_no='"+machine_vo.getString("machine_no")+"'");
			sql.append(" and location_id<>'"+machine_vo.getString("location_id")+"'");
		}	
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
				return false;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
}