package com.hjsj.hrms.module.org.virtualorg.trans;

import com.hjsj.hrms.module.org.virtualorg.bo.VirturalRoleTransBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 删除虚拟机构人员
 * @author xus
 * 16/11/24
 */
public class DeleteMemberTrans  extends IBusiness{
	private static final long serialVersionUID = 1L;

	@Override
    public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
		VirturalRoleTransBo vrbo = new VirturalRoleTransBo(this.frameconn,this.userView);
		vrbo.deleteMember(hm);
	}
}
