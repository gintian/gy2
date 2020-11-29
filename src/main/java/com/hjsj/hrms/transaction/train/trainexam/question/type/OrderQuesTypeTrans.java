package com.hjsj.hrms.transaction.train.trainexam.question.type;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class OrderQuesTypeTrans extends IBusiness
{
	public void execute() throws GeneralException
	{
		int typeId = Integer.parseInt(this.getFormHM().get("type_id").toString());	
		if (typeId<=0)
		{
			return;
			
		}
		
		String e_flag=(String)this.getFormHM().get("e_flag");
		if(e_flag==null||e_flag.length()<=0)
		{
			return;
		}
		changeQuesTypeOrder(e_flag, typeId);
	}
	
	public void changeQuesTypeOrder(String e_flag, int typeId)throws GeneralException
	{	
		String nearOrder = "";
		int curOrder = getOrderByTypeId(typeId);

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
		sql.append(" FROM tr_question_type");
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
			sql.append("UPDATE tr_question_type");
			sql.append(" SET nOrder= ");
			sql.append(curOrder);
			sql.append(" WHERE norder=");
            sql.append(nearOrder);
            dao.update(sql.toString());

            sql.setLength(0);
			sql.append("UPDATE tr_question_type");
			sql.append(" SET nOrder= ");			
			sql.append(nearOrder);
			sql.append(" WHERE type_id=");
            sql.append(typeId);
            dao.update(sql.toString());
		}
		catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.machine.error"),"",""));
		}
		
	}
	
	private int getOrderByTypeId(int typeId) throws GeneralException
	{
		int order = -1;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT nOrder FROM tr_question_type");
			sql.append(" WHERE type_id=");
			sql.append(typeId);
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