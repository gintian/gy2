package com.hjsj.hrms.transaction.performance.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class InitAddPerDegreeTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String degreeId = (String) hm.get("degreeId");

	RecordVo vo = new RecordVo("per_degree");
	ContentDAO dao = new ContentDAO(this.getFrameconn());

	String info = (String) this.getFormHM().get("info");
	try
	{
	    if ("edit".equals(info))
	    {
		StringBuffer strsql = new StringBuffer();
		strsql.append("select degree_id,degreename,degreedesc,topscore,used,flag,domainflag,B0110  from per_degree  where degree_id=");
		strsql.append(degreeId);
		strsql.append(" order by  degree_id");
		this.frowset = dao.search(strsql.toString());
		if (this.frowset.next())
		{
		    vo.setString("degree_id", this.frowset.getString("degree_id"));
		    vo.setString("degreename", this.frowset.getString("degreename"));
		    vo.setString("degreedesc", this.frowset.getString("degreedesc"));
		    vo.setString("topscore", this.frowset.getString("topscore"));
		    vo.setString("used", this.frowset.getString("used"));
		    vo.setString("flag", this.frowset.getString("flag"));
		    vo.setString("domainflag", this.frowset.getString("domainflag"));
		    // vo.setString("B0110",
                        // this.frowset.getString("B0110"));
		}
	    } else
	    {
		vo.setString("degree_id", "");
		vo.setString("degreename", "");
		vo.setString("degreedesc", "");
		vo.setString("topscore", "");
		vo.setString("used", "");
		String busitype=(String)this.getFormHM().get("busitype");
		if(busitype!=null&& "1".equals(busitype))
			vo.setString("flag", "4");
		else
			vo.setString("flag", "");
		vo.setString("domainflag", "");
	    }
	} catch (Exception ex)
	{
	    ex.printStackTrace();
	    throw GeneralExceptionHandler.Handle(ex);
	} finally
	{
	    this.getFormHM().put("perdegreevo", vo);
	}
    }
}
