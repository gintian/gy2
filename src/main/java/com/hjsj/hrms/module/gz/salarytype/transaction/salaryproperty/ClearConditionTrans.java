package com.hjsj.hrms.module.gz.salarytype.transaction.salaryproperty;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class ClearConditionTrans extends IBusiness{

	@Override
    public void execute() throws GeneralException {
		try
		{
			String salaryid=(String)this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			this.clear(salaryid);
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
			context.append(vo.getString("cname")+"("+id+")"+ResourceFactory.getProperty("gz_new.gz_propertyClearCon"));//属性设置中清空了条件
			this.getFormHM().put("@eventlog", context.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
