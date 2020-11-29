package com.hjsj.hrms.transaction.train.trainexam.question.type;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 保存题型信息 
 * @author zxj
 * @version 1.0
 *
 */
public class SaveQuesTypeTrans extends IBusiness {

	public void execute() throws GeneralException
	{
		RecordVo quesType=(RecordVo)this.getFormHM().get("quesType");
		String e_flag=(String)this.getFormHM().get("e_flag");
		if(e_flag==null||e_flag.length()<=0)
		{
			return;
		}
		
		if(quesType==null)
		{
			return;
		}	
		
		String typeName = quesType.getString("type_name");
		if ((typeName == null)||(typeName.trim().length() == 0))
		{
			throw GeneralExceptionHandler.Handle(
					new GeneralException("", "题型名称不允许为空！", "", ""));
		}
		
		if(!checkSameName(quesType,e_flag))
			throw GeneralExceptionHandler.Handle(
					new GeneralException("","名为" + typeName + "题型","",""));
		
		if("add".equalsIgnoreCase(e_flag))
		{			
			addQuesType(quesType);
		}
		else if("up".equalsIgnoreCase(e_flag))
		{
			upQuesType(quesType);
		}
	}
	
	public void upQuesType(RecordVo quesType_vo)throws GeneralException
	{
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			dao.updateValueObject(quesType_vo);
		}
		catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.machine.error"),"",""));
		}
		
	}
	/**
	 * 新增
	 * @param quesType_vo
	 * @throws GeneralException
	 */
	public void addQuesType(RecordVo quesType_vo)throws GeneralException
	{		
		IDGenerator idg=new IDGenerator(2,this.getFrameconn());		
		try
		{   		    
			String typeId = idg.getId("tr_question_type.type_id").toUpperCase();
		    ContentDAO dao = new ContentDAO(this.getFrameconn());		    
		    quesType_vo.setString("type_id",typeId);
		    
		    int nOrder = getNextOrderNO();
			quesType_vo.setInt("norder", nOrder);
			
			dao.addValueObject(quesType_vo);
		}
		catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.machine.error"),"",""));
		}
   	    
	}
	
	private int getNextOrderNO()
	{
		String sql = "SELECT MAX(norder) AS maxOrder FROM tr_question_type";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset = dao.search(sql);
			if (this.frowset.next())
			{
				String maxOrder = this.frowset.getString("maxOrder");
				if (maxOrder==null||maxOrder.length()==0)
				{
					return 0;
				}
				else
				{
					return Integer.parseInt(maxOrder) + 1;
				}
			}
			else
			{
				return 0;				
			}
			
		}
		catch(Exception e)
		{
			return -1;
		}		
		
	}
	
	private boolean checkSameName(RecordVo quesType_vo,String flag)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("select * from tr_question_type");
		sql.append(" where type_name='" + quesType_vo.getString("type_name") + "'");
		if(!"add".equals(flag))
		{
			sql.append(" and type_id<>'" + quesType_vo.getString("type_id") + "'");
		}	
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset = dao.search(sql.toString());
			if(this.frowset.next())
				return false;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
}