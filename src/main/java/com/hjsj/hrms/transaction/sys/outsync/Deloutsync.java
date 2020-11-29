package com.hjsj.hrms.transaction.sys.outsync;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Deloutsync extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String flag = (String) hm.get("flag");
		List list = (List) this.getFormHM().get("selected");
		ArrayList listId = new ArrayList();
		ContentDAO dao = new ContentDAO(this.frameconn);
		StringBuffer sql = new StringBuffer();
		if ("3".equals(flag))
			sql.append("delete t_sys_outsync where sys_id in(");
		if ("2".equals(flag))
			sql.append("update t_sys_outsync set STATE = 1 where sys_id in(");

		for (int i = 0; i < list.size(); i++) {
			LazyDynaBean from = (LazyDynaBean) list.get(i);
			sql.append("'" + (String) from.get("sys_id") + "',");
			listId.add(from.get("sys_id"));
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(")");
		try {
			HrSyncBo bo = new HrSyncBo(this.frameconn);
			if ("3".equals(flag)) {
				dao.delete(sql.toString(), null);
				bo.delSysOutsyncFlag("t_org_view",
						listId);
				bo.delSysOutsyncFlag("t_hr_view",
						listId);
				bo.delSysOutsyncFlag("t_post_view",
						listId);
				
			}
			if ("2".equals(flag)) {
				dao.update(sql.toString());
				bo.addSysOutsyncFlag("t_org_view",
						listId);
				bo.addSysOutsyncFlag("t_hr_view",
						listId);
				bo.addSysOutsyncFlag("t_post_view",
						listId);
			}
			//19/9/9 xus 清除视图中的照片字段
			Table table = new Table("t_hr_view");
			DbWizard dbw = new DbWizard(this.frameconn);
			DBMetaModel dbmodel = new DBMetaModel(this.frameconn);
			for(Object o:listId){
				if(dbw.isExistField("t_hr_view", (String)o + "p",false)) {
					Field item = new Field("hrcloudp", "hrcloudp");
					table.addField(item);
					dbw.dropColumns(table);
					dbmodel.reloadTableModel("t_hr_view");
				}
			}
		} catch (SQLException e) {
			throw new GeneralException(e.getMessage());
		}
	}

}
