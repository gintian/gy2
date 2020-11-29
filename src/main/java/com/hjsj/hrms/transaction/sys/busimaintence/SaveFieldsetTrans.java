package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.sys.busimaintence.BusiSelStr;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveFieldsetTrans extends IBusiness{

	
	public void execute() throws GeneralException {
		try
		{
			String mid = (String)this.getFormHM().get("mid");
			String setid = (String)this.getFormHM().get("setid");
		    String setdesc =(String)this.getFormHM().get("setdesc");
		    String changeflag =(String)this.getFormHM().get("changeflag");
		    RecordVo vo = new RecordVo("t_hr_BusiTable");
		    BusiSelStr bss = new BusiSelStr();
		    vo.setString("fieldsetid", setid);
		    vo.setString("id",mid);
		    vo.setString("changeflag",changeflag);
		    vo.setString("fieldsetdesc", setdesc);
		    vo.setString("customdesc",setdesc);
		    vo.setString("useflag","0");
		    vo.setString("displayorder",""+bss.getMaxDisplayOrder(this.getFrameconn()));
		    ContentDAO dao = new ContentDAO(this.getFrameconn());
		    dao.addValueObject(vo);
		    bss.createFieldByChangeFlag(mid, setid, changeflag, this.getFrameconn());
		    this.getFormHM().put("setid",setid);
		    this.getFormHM().put("setdesc",setdesc);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
