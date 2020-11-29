package com.hjsj.hrms.transaction.kq.machine;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
/**
 * 卡机时间
 * <p>Title:MachineTimeTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Mar 6, 2007 4:07:49 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class MachineTimeTrans extends IBusiness 
{
    public void execute() throws GeneralException 
	{
    	Calendar now = Calendar.getInstance();
    	Date cur_d=now.getTime();
    	String machine_date=DateUtils.format(cur_d,"yyyy-MM-dd");
    	String machine_hh=DateUtils.format(cur_d,"HH");
    	String machine_mm=DateUtils.format(cur_d,"mm");
    	this.getFormHM().put("machine_date",machine_date);
    	this.getFormHM().put("machine_hh",machine_hh);
    	this.getFormHM().put("machine_mm",machine_mm);
    	ArrayList machinelist=new ArrayList();
		StringBuffer sql=new StringBuffer();
		sql.append("select name,location_id from kq_machine_location");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		CommonData vo = new CommonData() ;
		vo.setDataName("");
		vo.setDataValue("");
		machinelist.add(vo);
		try
		{
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{
				if (userView.isHaveResource(IResourceConstant.KQ_MACH, frowset.getString("location_id")))
				{
				    vo = new CommonData() ;
				    vo.setDataName(this.frowset.getString("name"));
				    vo.setDataValue(this.frowset.getString("location_id"));
				    machinelist.add(vo);
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("machinelist",machinelist);
	}

}
