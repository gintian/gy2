package com.hjsj.hrms.transaction.hire.employSummarise.hireSummarise;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SetStateTrans extends IBusiness {

	public void execute() throws GeneralException {
	//	ArrayList engagePlanlist=(ArrayList)this.getFormHM().get("selectedlist");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String state=(String)hm.get("state");   // del:删除   
		String viewType=(String)this.getFormHM().get("viewType");   //1:用工需求  2：招聘计划
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer whl_str=new StringBuffer("");
		String ids=(String)hm.get("ids");
		try
		{
			String[] id=ids.split("~");
			for(int i=0;i<id.length;i++)
        	{
        		if("2".equals(viewType))
        			whl_str.append(",'"+id[i]+"'");
        		else if("1".equals(viewType))
        			whl_str.append(",'"+id[i]+"'");
        	}
			if(id.length>0)
			{
				if("del".equalsIgnoreCase(state))
				{
					if("2".equals(viewType))
					{
						dao.delete("delete from z01 where z0101 in ("+whl_str.substring(1)+")",new ArrayList());
						dao.delete("delete from z05 where a0100 in (select a0100 from z03 ,zp_pos_tache zpt where zpt.zp_pos_id=z03.z0301 and z03.z0101 in ("+whl_str.substring(1)+"))",new ArrayList());
						dao.delete("delete from zp_pos_tache where a0100 in (select a0100 from z03 ,zp_pos_tache zpt where zpt.zp_pos_id=z03.z0301 and z03.z0101 in ("+whl_str.substring(1)+"))",new ArrayList());
						dao.delete("delete from z03 where z0101 in ("+whl_str.substring(1)+")",new ArrayList());
					}
					else if("1".equals(viewType))
					{
						dao.delete("delete from z03 where z0301 in ("+whl_str.substring(1)+")",new ArrayList());
						dao.delete("delete from zp_test_template where z0101 in ("+whl_str.substring(1)+")",new ArrayList());
						dao.delete("delete from z05 where a0100 in (select a0100 from zp_pos_tache where zp_pos_id in ("+whl_str.substring(1)+") )",new ArrayList());
						dao.delete("delete from zp_pos_tache where zp_pos_id in  ("+whl_str.substring(1)+") ",new ArrayList());
						
					}
				}
				else
				{
					if("2".equals(viewType))
					{
						if("04".equals(state))
						{
							Calendar d=Calendar.getInstance();
							String date=Sql_switcher.dateValue(d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE));
							//System.out.println("update z01 set z0129='"+state+"',z0123='"+date+"' where  z0101 in ("+whl_str.substring(1)+")");
							dao.update("update z01 set z0129='"+state+"',z0123="+date+" where  z0101 in ("+whl_str.substring(1)+")",new ArrayList());
						}
						else
							dao.update("update z01 set z0129='"+state+"' where  z0101 in ("+whl_str.substring(1)+")",new ArrayList());
					}
					else if("1".equals(viewType))
					{
						//System.out.println("update z03 set z0319='"+state+"' where  z0301 in ("+whl_str.substring(1)+")");
						dao.update("update z03 set z0319='"+state+"' where  z0301 in ("+whl_str.substring(1)+")",new ArrayList());
					}
					
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		hm.remove("state");
	}

}
