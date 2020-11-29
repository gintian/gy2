package com.hjsj.hrms.transaction.train.request;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
/**
 * 查询对应信息集的详细指标
 */
public class QueryClassFieldInfoTrans extends IBusiness {

	public void execute() throws GeneralException {

		String fieldsetid = (String) this.getFormHM().get("fieldsetid");
		String selectlist = (String) this.getFormHM().get("selectlist");
		ArrayList fieldList = new ArrayList();
		ArrayList fieldlist = new ArrayList();
		Connection conn = this.getFrameconn();
		String fieldsetdesc = "";
		try {
			ContentDAO dao = new ContentDAO(conn);
			String sql = "select fieldsetdesc from t_hr_busiTable where fieldsetid='" + fieldsetid + "'";// 获取指标集编码对应的名称
			RowSet rs = dao.search(sql);
			if (rs.next()) {
				fieldsetdesc = rs.getString("fieldsetdesc");
			}
			fieldsetid = fieldsetid != null && fieldsetid.length() > 0 ? fieldsetid : "R31";
			fieldList = DataDictionary.getFieldList(fieldsetid, 1);
			if (fieldList != null)
				for (int i = 0; i < fieldList.size(); i++) {
					FieldItem fi = (FieldItem) fieldList.get(i);
					//过滤不需要显示的指标 r3101、r3127、r4101、r4103为系统自动生成   r3130默认选中 b0110为必填项
					if ("r3101".equalsIgnoreCase(fi.getItemid()) || "r3130".equalsIgnoreCase(fi.getItemid())
							|| "r3127".equalsIgnoreCase(fi.getItemid()) || "r4101".equalsIgnoreCase(fi.getItemid())
							|| "r4103".equalsIgnoreCase(fi.getItemid()) || "r4001".equalsIgnoreCase(fi.getItemid())
							|| "r4005".equalsIgnoreCase(fi.getItemid()) || "r4013".equalsIgnoreCase(fi.getItemid())
							|| "r4015".equalsIgnoreCase(fi.getItemid())
							|| ("b0110".equalsIgnoreCase(fi.getItemid()) && "r31".equalsIgnoreCase(fi.getFieldsetid()))
							|| "0".equalsIgnoreCase(fi.getState())) {
						continue;
					}
					LazyDynaBean ld = new LazyDynaBean();
					ld.set("itemdesc", fi.getItemdesc());
					ld.set("itemid", fi.getItemid());
					if (fi.isFillable())//判断必填项
						ld.set("reserveitem", "1");
					else
						ld.set("reserveitem", "0");
					fieldlist.add(ld);
				}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.getFormHM().put("fieldlist", fieldlist);
			this.getFormHM().put("selectlist", selectlist);
			this.getFormHM().put("fieldsetdesc", fieldsetdesc);
		}
	}

}
