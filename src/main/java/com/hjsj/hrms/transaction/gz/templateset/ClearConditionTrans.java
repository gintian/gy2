package com.hjsj.hrms.transaction.gz.templateset;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class ClearConditionTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String salaryid=(String)this.getFormHM().get("salaryid");
			String gz_module=(String)this.getFormHM().get("gz_module");
			this.clear(salaryid);
			this.getFormHM().put("gz_module",gz_module);
			this.getFormHM().put("salaryid",salaryid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	public void clear(String id)
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RecordVo vo = new RecordVo("salarytemplate");
			vo.setInt("salaryid",Integer.parseInt(id));
			vo=dao.findByPrimaryKey(vo);
			vo.setString("cond","");
			vo.setString("cexpr","");
			dao.updateValueObject(vo);
			StringBuffer context = new StringBuffer();
			context.append(vo.getString("cname")+"("+id+")"+"属性设置中清空了条件");
			this.getFormHM().put("@eventlog", context.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
