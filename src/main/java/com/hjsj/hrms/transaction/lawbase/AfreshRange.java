package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class AfreshRange extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String base_ids=(String)hm.get("a_base_ids");
		base_ids = PubFunc.decrypt(SafeCode.decode(base_ids));

		if(base_ids==null||base_ids.length()<=0)
			 return;
		ArrayList list = (ArrayList) this.getFormHM().get("selectedlist");
		
		ArrayList uplist = new ArrayList();
		for(int i=0;i<list.size();i++)
		{
			ArrayList fileidlist = new ArrayList();
			RecordVo vo=(RecordVo)list.get(i);
			String file_id= vo.getString("file_id");
			if (!userView.isHaveResource(IResourceConstant.LAWRULE_FILE, file_id))
				continue;
			fileidlist.add(base_ids);
			fileidlist.add(file_id);
			uplist.add(fileidlist);
		}
		ContentDAO dao = new ContentDAO(this.frameconn);
		String sql = "update law_base_file set base_id = ? where file_id = ?";
		try {
			dao.batchUpdate(sql,uplist);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
