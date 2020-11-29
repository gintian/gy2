package com.hjsj.hrms.transaction.general.inform.emp.e_archive;

import com.hjsj.hrms.businessobject.general.inform.e_archive.E_ArchiveBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class NoteLogTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String filename=(String)this.getFormHM().get("filename");
			//String ip=(String)map.get("ip");
			String ip=(String)this.getFormHM().get("ip");
			
			String pcname=(String)this.getFormHM().get("name");
			String a0100=(String)this.getFormHM().get("a0100");
			String nbase=(String)this.getFormHM().get("nbase");
			String typeid=(String)this.getFormHM().get("typeid");
			E_ArchiveBo bo = new E_ArchiveBo(this.getFrameconn());
			HashMap infomap = bo.getUNAndUMAndName(nbase, a0100);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String logintime=format.format(new Date());
			int logid=bo.getPamaryKey();
			//---
			String loga0100=this.userView.getA0100();
			String pre=this.userView.getDbname();
			String username=this.userView.getUserName();
			String personname="";
			if(pre!=null&&!"".equals(pre)&&loga0100!=null&&!"".equals(loga0100))
			{
				personname=(String)bo.getUNAndUMAndName(pre, loga0100).get("a0101");
			}
			bo.noteLog(logid,ip,logintime, pcname,personname, username,typeid,(String)infomap.get("a0101"),
					(String)infomap.get("b0110"), (String)infomap.get("e0122"), a0100, nbase);
			this.getFormHM().put("filename",filename);
			this.getFormHM().put("logid",logid+"");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
