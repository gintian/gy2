package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.sys.busimaintence.BusiSelStr;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SearchRelinfo extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
//		RecordVo t_hr_relatingcode=new RecordVo("t_hr_relatingcode");
//		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		RecordVo t_hr_relatingcode=new RecordVo("t_hr_relatingcode");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		BusiSelStr bssr=new BusiSelStr();
		String codetable="",codevalue="",codedesc="",upcodevalue="";
		if(hm.get("flag")!=null&& "1".equals(hm.get("flag"))){
			String codesetid=(String) hm.get("codesetid");
			t_hr_relatingcode.setString("codesetid",codesetid);
			try {
				t_hr_relatingcode=dao.findByPrimaryKey(t_hr_relatingcode);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			codetable=t_hr_relatingcode.getString("codetable");
			codevalue=t_hr_relatingcode.getString("codevalue");
			codedesc=t_hr_relatingcode.getString("codedesc");
			upcodevalue=t_hr_relatingcode.getString("upcodevalue");
		}
		if(hm.get("sysvalue")!=null){
			hm.put("zijilist",bssr.getzijiStr(dao,(String) hm.get("sysvalue")));
			hm.put("codetable",codetable);
		}
		if(hm.get("zijivalue")!=null){
			hm.put("itemlist",bssr.getItemStr(dao,(String)hm.get("zijivalue")));
			hm.put("codevalue",codevalue);
			hm.put("codedesc",codedesc);
			hm.put("upcodevalue",upcodevalue);
		}
		hm.put("systemlist",bssr.getSubsys(dao,null));
	}

}
