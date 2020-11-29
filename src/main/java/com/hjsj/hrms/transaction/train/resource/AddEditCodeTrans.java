package com.hjsj.hrms.transaction.train.resource;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class AddEditCodeTrans extends IBusiness {

	public void execute() throws GeneralException {
	
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String codeitemid = (String)hm.get("itemid");
		codeitemid=codeitemid!=null&&codeitemid.trim().length()>0?codeitemid:"";
		hm.remove("itemid");
		codeitemid= "root".equalsIgnoreCase(codeitemid)?"":codeitemid;
		
		String codesetid = (String)hm.get("setid");
		codesetid=codesetid!=null&&codesetid.trim().length()>0?codesetid:"";
		codesetid = PubFunc.decrypt(SafeCode.decode(codesetid));
		hm.remove("setid");
		
		String flag = (String)hm.get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"";
		hm.remove("flag");
		if("update".equalsIgnoreCase(flag)){
			String codedesc = (String)hm.get("codedesc");
			codedesc=codedesc!=null&&codedesc.trim().length()>0?codedesc:"";
			hm.remove("codedesc");
			codedesc = SafeCode.decode(codedesc);
			this.getFormHM().put("codeitemdesc", codedesc);
		}else
			this.getFormHM().put("codeitemdesc", "");
		
		String codetitle = "";
		if("54".equals(codesetid)){
			codetitle="培训资料分类";
		}else if("55".equals(codesetid)){
			codetitle="培训课程分类";
		}
		if("add".equals(flag))
			codetitle ="新增"+codetitle;
		else if("update".equals(flag))
			codetitle ="修改"+codetitle;
		
		this.getFormHM().put("codesetid", SafeCode.encode(PubFunc.encrypt(codesetid)));
		this.getFormHM().put("codeitemid", codeitemid);
		this.getFormHM().put("checkflag", flag);
		this.getFormHM().put("codetitle", codetitle);
	}
	
}
