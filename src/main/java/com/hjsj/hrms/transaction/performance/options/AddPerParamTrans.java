package com.hjsj.hrms.transaction.performance.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:AddPerParamTrans.java</p>
 * <p>Description>:保存评语模板</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 17, 2010 12:15:35 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class AddPerParamTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

		RecordVo votemp = (RecordVo) this.getFormHM().get("perparamvo");
		String id = votemp.getString("id");
		String kind = "EvalTemplate";
		String content = votemp.getString("content");
		String username = votemp.getString("username");
		String paramName = votemp.getString("param_name");
	
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RecordVo vo = new RecordVo("per_param");
		try
		{
	
		    if (id == null || "".equals(id))
		    {
				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
				id = idg.getId("per_param.id");
				vo.setString("id", id);
				vo.setString("kind", kind);
				vo.setString("content", content);
				vo.setString("username", username);
				vo.setString("param_name", paramName);
				dao.addValueObject(vo);
	
		    } else
		    {
				vo.setString("id", id);
				vo.setString("kind", kind);
				vo.setString("content", content);
				vo.setString("username", username);
				vo.setString("param_name", paramName);
				dao.updateValueObject(vo);
		    }
		    vo.setString("id", "");
		    vo.setString("kind", "");
		    vo.setString("content", "");
		    vo.setString("username", "");
		    vo.setString("param_name", "");
		    
		} catch (Exception exx)
		{
		    exx.printStackTrace();
		    throw GeneralExceptionHandler.Handle(exx);
		} finally
		{
		    this.getFormHM().put("perparamvo", vo);
		}
    }

    public synchronized int getId() throws GeneralException
    {

		int num = 0; // 序号默认为0
		String sql = "select max(id) as num  from per_param";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
		    this.frowset = dao.search(sql.toString());
		    if (this.frowset.next())
		    {
		    	num = this.frowset.getInt("num");
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return num + 1;
    }

}
