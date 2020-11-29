package com.hjsj.hrms.transaction.kq.register.historical;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class OverruleKqTextTrans extends IBusiness {
	public void execute() throws GeneralException
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String userbase = (String) hm.get("userbase");
		String a0100 = (String) hm.get("a0100");
		String kq_duration = (String) hm.get("kq_duration");
		StringBuffer sql=new StringBuffer();
		sql.append("Select nbase,a0100,e01a1,b0110,e0122,a0101,overrule");
		sql.append(" from Q05_arc where nbase='"+userbase+"'");
		sql.append(" and a0100='"+a0100+"' and Q03Z0='"+kq_duration+"'");
		ContentDAO dao = new ContentDAO(this.getFrameconn());	
		RecordVo one_vo = new RecordVo("Q05_arc",1);
		try
		{
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
			{
				one_vo.setString("nbase",this.frowset.getString("nbase"));
				one_vo.setString("a0100",this.frowset.getString("a0100"));
				one_vo.setString("e01a1",this.frowset.getString("e01a1"));
				one_vo.setString("b0110",this.frowset.getString("b0110"));
				one_vo.setString("e0122",this.frowset.getString("e0122"));
				one_vo.setString("a0101",this.frowset.getString("a0101"));
				one_vo.setString("overrule",this.frowset.getString("overrule"));	
				
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("one_vo",one_vo);
	}

}
