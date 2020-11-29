package com.hjsj.hrms.transaction.train.trainexam.exam;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class OrderExamPlanTrans extends IBusiness
{
	public void execute() throws GeneralException
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String planid = hm.get("r5400").toString();
		planid = PubFunc.decrypt(SafeCode.decode(planid));
		int r5400 = Integer.parseInt(planid);
		if (r5400<=0)
		{
			return;			
		}
		
		String e_flag=(String)this.getFormHM().get("e_flag");
		if(e_flag==null||e_flag.length()<=0)
		{
			return;
		}
		changeExamPlanOrder(e_flag, r5400);
	}
	
	public void changeExamPlanOrder(String e_flag, int r5400)throws GeneralException
	{	
		String nearOrder = "";
		int curOrder = getOrderByR5400(r5400);

		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT ");
		if ("uporder".equalsIgnoreCase(e_flag))
		{
			sql.append("MAX");
		}
		else
		{
			sql.append("MIN");
		}
		sql.append("(nOrder) AS nearOrder");
		sql.append(" FROM R54");
		sql.append(" WHERE nOrder");
		if ("uporder".equalsIgnoreCase(e_flag))
		{
			sql.append("<");
		}
		else
		{
			sql.append(">");
		}
		sql.append(curOrder);
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset = dao.search(sql.toString());
			if (this.frowset.next())
			{
				nearOrder = this.frowset.getString("nearOrder");				
			}
			
			if ((nearOrder==null))
			{
				return;
			}
			
			//交换位置
			sql.setLength(0);
			sql.append("UPDATE R54");
			sql.append(" SET nOrder= ");
			sql.append(curOrder);
			sql.append(" WHERE norder=");
            sql.append(nearOrder);
            dao.update(sql.toString());

            sql.setLength(0);
			sql.append("UPDATE R54");
			sql.append(" SET nOrder= ");			
			sql.append(nearOrder);
			sql.append(" WHERE R5400=");
            sql.append(r5400);
            dao.update(sql.toString());
		}
		catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.machine.error"),"",""));
		}
		
	}
	
	private int getOrderByR5400(int r5400) throws GeneralException
	{
		int order = -1;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT nOrder FROM R54");
			sql.append(" WHERE R5400=");
			sql.append(r5400);
			this.frowset = dao.search(sql.toString());
			if (this.frowset.next())
			{
				order = this.frowset.getInt("nOrder");
			}
			
		}
		catch(Exception e)
		{
			return -1;
		}
		return order;
	}
}