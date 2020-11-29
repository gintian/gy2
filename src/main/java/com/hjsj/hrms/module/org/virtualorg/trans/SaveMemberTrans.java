package com.hjsj.hrms.module.org.virtualorg.trans;

import com.hjsj.hrms.module.org.virtualorg.bo.VirturalRoleTransBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 保存虚拟组织机构人员
 * @author xus
 *	16/11/24
 */
public class SaveMemberTrans extends IBusiness{
	private static final long serialVersionUID = 1L;

	@Override
    public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
		ArrayList<MorphDynaBean> al = (ArrayList<MorphDynaBean>)hm.get("savedata");
		VirturalRoleTransBo vrbo = new VirturalRoleTransBo(this.frameconn,this.userView);
		vrbo.saveMember(al);
	}
}
