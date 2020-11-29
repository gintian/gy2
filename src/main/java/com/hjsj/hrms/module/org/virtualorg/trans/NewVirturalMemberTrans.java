package com.hjsj.hrms.module.org.virtualorg.trans;

import com.hjsj.hrms.module.org.virtualorg.bo.VirturalRoleTransBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class NewVirturalMemberTrans extends IBusiness{

	private static final long serialVersionUID = 1L;

	@Override
    public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
		String code=(String)this.getFormHM().get("code");
		code=PubFunc.decrypt(code.split("=")[1]);
		code = code.split("=")[1];
		ArrayList<MorphDynaBean> al = (ArrayList<MorphDynaBean>)hm.get("selectPersons");
		VirturalRoleTransBo vrbo = new VirturalRoleTransBo(this.frameconn,this.userView);
		vrbo.addNewMember(al,code);
	}
}
