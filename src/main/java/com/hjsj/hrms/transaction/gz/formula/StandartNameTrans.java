package com.hjsj.hrms.transaction.gz.formula;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:计算公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class StandartNameTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM();
		
		String hfactor=(String)this.getFormHM().get("hfactor");
		hfactor=hfactor!=null&&hfactor.trim().length()>0?hfactor:"";
		hm.put("hfactor",hfactor);
		
		String s_hfactor=(String)this.getFormHM().get("s_hfactor");
		s_hfactor=s_hfactor!=null&&s_hfactor.trim().length()>0?s_hfactor:"";
		hm.put("s_hfactor",s_hfactor);
		
		String vfactor=(String)this.getFormHM().get("vfactor");
		vfactor=vfactor!=null&&vfactor.trim().length()>0?vfactor:"";
		hm.put("vfactor",vfactor);
		
		String s_vfactor=(String)this.getFormHM().get("s_vfactor");
		s_vfactor=s_vfactor!=null&&s_vfactor.trim().length()>0?s_vfactor:"";
		hm.put("s_vfactor",s_vfactor);
		
		String item=(String)this.getFormHM().get("item");
		item=item!=null&&item.trim().length()>0?item:"";
		hm.put("item",item);
		
		String hcontent=(String)this.getFormHM().get("hcontent");
		hcontent=hcontent!=null&&hcontent.trim().length()>0?hcontent:"";
		hm.put("hcontent",hcontent);
		
		String vcontent=(String)this.getFormHM().get("vcontent");
		vcontent=vcontent!=null&&vcontent.trim().length()>0?vcontent:"";
		hm.put("vcontent",vcontent);
	}

}
