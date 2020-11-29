package com.hjsj.hrms.module.template.signature.transaction;

import com.hjsj.hrms.module.template.signature.businessobject.SignatureBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DelSignatureTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String flag = (String) this.getFormHM().get("flag");
			SignatureBo bo=new SignatureBo(this.frameconn, this.userView);
			if("1".equals(flag)) {
				String signatureid = (String) this.getFormHM().get("signatureid");
				bo.delSingatureMarkid(signatureid);
				ArrayList list = new ArrayList();
				String sql = "delete from signature where signatureid=?";
				list.add(signatureid);
				dao.delete(sql, list);
			}else {
				ArrayList list = new ArrayList();
				ArrayList selectedlist=(ArrayList) this.getFormHM().get("selectdata");
				for(int i=0;i<selectedlist.size();i++)
				{	
					ArrayList ldlist = new ArrayList();
					String signatureid=(String)selectedlist.get(i);  
					ldlist.add(signatureid);
					list.add(ldlist);
					bo.delSingatureMarkid(signatureid);
				}
				String sql = "delete from signature where signatureid=?";
				dao.batchUpdate(sql, list);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
