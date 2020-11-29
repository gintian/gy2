package com.hjsj.hrms.transaction.performance.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class InitAddCheckBodyObjectTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

	String info = (String) this.getFormHM().get("info");
	ContentDAO dao = new ContentDAO(this.getFrameconn());
	RecordVo vo = new RecordVo("per_mainbodyset");
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String bodyId = (String) hm.get("bodyId");

	try
	{
	    if ("edit".equals(info))
	    {
		StringBuffer strsql = new StringBuffer();
		strsql.append("select * from per_mainbodyset where body_id=");
		strsql.append(bodyId);
		this.frowset = dao.search(strsql.toString());
		if (this.frowset.next())
		{
		    vo.setString("body_id", frowset.getString("body_id"));
		    vo.setString("name", frowset.getString("name"));
		    vo.setString("status", frowset.getString("status"));
		    vo.setString("seq", frowset.getString("seq"));
		    if (Sql_switcher.searchDbServer() == Constant.ORACEL)
			vo.setString("level_o", this.frowset.getString("level_o") == null ? "6" : this.frowset.getString("level_o"));
		    else
			vo.setString("level", this.frowset.getString("level") == null ? "6" : this.frowset.getString("level"));
		    vo.setString("object_type", frowset.getString("object_type")==null?"0":frowset.getString("object_type"));
		    vo.setString("scope", frowset.getString("scope")==null?"0":frowset.getString("scope"));
		}
		this.getFormHM().put("info", "initedit");
		this.getFormHM().put("show", "edit");
	    } else
	    {
		vo.setString("body_id", "");
		vo.setString("name", "");
		vo.setString("status", "");
		vo.setString("seq", "");
		if (Sql_switcher.searchDbServer() == Constant.ORACEL)
		    vo.setString("level_o", "6");
		else
		    vo.setString("level", "6");
		this.getFormHM().put("info", "initsave");
		this.getFormHM().put("show", "save");
		  vo.setString("object_type", "0");
	    }
	} catch (Exception ex)
	{
	    ex.printStackTrace();
	    throw GeneralExceptionHandler.Handle(ex);
	} finally
	{
	    this.getFormHM().put("checkbodyobjectvo", vo);
	}
    }
}
