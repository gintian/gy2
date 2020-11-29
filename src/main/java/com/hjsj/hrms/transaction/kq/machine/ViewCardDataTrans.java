package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ViewCardDataTrans extends IBusiness{
	
	public void execute()throws GeneralException
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String a0100=(String)hm.get("a0100");
		a0100 = PubFunc.decrypt(a0100);
		String nbase=(String)hm.get("nbase");
		nbase = PubFunc.decrypt(nbase);
		String work_date=(String)hm.get("work_date");
		work_date = PubFunc.decrypt(work_date);
		String work_time=(String)hm.get("work_time");
		work_time = PubFunc.decrypt(work_time);
		if (work_time != null) 
		{
			if (work_time.indexOf("%20") != -1) //链接传递参数 空格 会变成  %20
			{
				work_time = work_time.replaceAll("%20", " ");
			}
		}
		StringBuffer sql=new StringBuffer();
		sql.append("select * from kq_originality_data where a0100='"+a0100+"' and nbase='"+nbase+"'");
		sql.append(" and work_date='"+work_date+"'");
		sql.append(" and work_time='"+work_time+"'");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		RecordVo vo=new RecordVo("kq_originality_data");
		try
		{
			this.frowset=dao.search(sql.toString());		
			if(this.frowset.next())
			{
				vo.setString("a0100",a0100);
				vo.setString("a0101",this.frowset.getString("a0101"));
				vo.setString("work_date",work_date);
				vo.setString("work_time",work_time);
				vo.setString("card_no",this.frowset.getString("card_no"));
				vo.setString("nbase",nbase);
				vo.setString("inout_flag",this.frowset.getString("inout_flag"));//出入标志
			    if(this.frowset.getDate("oper_time")!=null)
			    {
			    	vo.setString("oper_time",DateUtils.format(this.frowset.getTimestamp("oper_time"),"yyyy.MM.dd HH:mm"));
			    }else
			    {
			    	vo.setString("oper_time","");			    	
			    }
			    vo.setString("oper_user",this.frowset.getString("oper_user"));
			    vo.setString("oper_cause",this.frowset.getString("oper_cause"));
			    vo.setString("sp_flag",this.frowset.getString("sp_flag"));
			    vo.setString("sp_user",this.frowset.getString("sp_user"));
			    if(this.frowset.getDate("sp_time")!=null)
			    {
			    	vo.setString("sp_time",DateUtils.format(this.frowset.getTimestamp("sp_time"),"yyyy.MM.dd HH:mm"));
			    }else
			    {
			    	vo.setString("sp_time","");			    	
			    }
			    
			}
		}catch(Exception e)
		{
			
		}
		this.getFormHM().put("view_vo",vo);
	}

}
