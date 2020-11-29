package com.hjsj.hrms.transaction.kq.app_check_in.exportExcel;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SelectExportExcelFiledTrans extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String table = (String) hm.get("table");
		String flag = (String) hm.get("flag");
		RowSet rs = null;
		try {
			ArrayList searchfieldlist = codesetidQ(table, flag);
			/** szk 取唯一性性指标 **/
			String code = "";
			if (this.userView.isSuper_admin()) {
				code = "UN";
			}
			else {
				ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
				code = managePrivCode.getPrivOrgId();
			}
			KqParameter para = new KqParameter(this.userView, code, this.getFrameconn());
			HashMap hashmap = para.getKqParamterMap();
			String kq_g_no = (String) hashmap.get("g_no");
			StringBuffer stb = new StringBuffer();
			stb.append("SELECT itemdesc FROM fielditem  WHERE fieldsetid ='A01' AND itemtype='A' AND itemid<>'A0101' AND useflag='1' AND (codesetid='0' or codesetid IS NULL)");
			stb.append(" and itemid = '" + kq_g_no + "' ");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			rs = dao.search(stb.toString());
			rs.next();
			String desc = "";
			if (rs.getString("itemdesc") != null) {
				desc = rs.getString("itemdesc");
			}

			this.getFormHM().put("desc", desc);
			this.getFormHM().put("table", table);
			this.getFormHM().put("selectFieldList", searchfieldlist);
			this.getFormHM().put("excelFieldList", searchfieldlist);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new GeneralException("请在参数设置中设置工号对应的指标！");
		}
		finally {
			KqUtilsClass.closeDBResource(rs);
		}

	}

	public ArrayList codesetidQ(String table, String flag) {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer sql = new StringBuffer();
		RowSet rowSet = null;
		ArrayList list = new ArrayList();
		String itemid = "";
		String itemdesc = "";
		String itemtype = "";
		try {
			sql.append("select itemid,itemdesc,itemtype from t_hr_busifield t where fieldsetid='" + table + "'");
			sql.append("and state='1' and useflag = '1' ");
			// 2014.10.27 xiexd申请导出Excel中需要导出审批状态和审批时间，导出Excel模板中不需要这两个指标
			if (!"0".equals(flag) && !"0".equals(flag)) {
				sql.append("and not exists(select itemid,itemdesc from t_hr_busifield tt where ");
				sql.append("(itemid = '" + table + "Z5' or itemid = '" + table + "Z7') and ");
				sql.append("t.itemid = tt.ItemId)  order by displayid ");
			}
			KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
			HashMap hashmap = para.getKqParamterMap();
			String g_no = (String) hashmap.get("g_no");
			
			ArrayList lists = DataDictionary.getFieldList("A01", Constant.USED_FIELD_SET);
			for (int i = 0; i < lists.size(); i++) {
				FieldItem item = new FieldItem();
				item = (FieldItem) lists.get(i);
				if (g_no.equalsIgnoreCase(item.getItemid())) {
					CommonData field_new = new CommonData();
					field_new.setDataName(item.getItemdesc());
					field_new.setDataValue(g_no);
					list.add(field_new);
				}
			}
			rowSet = dao.search(sql.toString());
			while (rowSet.next()) {
				CommonData field_new = new CommonData();
				itemid = rowSet.getString("itemid");
				itemdesc = rowSet.getString("itemdesc");
				itemtype = rowSet.getString("itemtype");
				if ("M".equals(itemtype))
					continue;
				if ("1".equals(flag)) {
					if ((table + "z0").equalsIgnoreCase(itemid) || (table + "z5").equalsIgnoreCase(itemid) || (table + "z0").equalsIgnoreCase(itemid) || (table + "z0").equalsIgnoreCase(itemid)
							|| (table + "z0").equalsIgnoreCase(itemid) || (("q13".equalsIgnoreCase(table) || "q15".equalsIgnoreCase(table)) && (table + "04").equalsIgnoreCase(itemid))
							|| ("q15".equalsIgnoreCase(table) && ("q1517".equalsIgnoreCase(itemid) || "q1519".equalsIgnoreCase(itemid))))
						continue;
				}
				field_new.setDataName(itemdesc);
				field_new.setDataValue(itemid);
				list.add(field_new);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rowSet != null)
				try {
					rowSet.close();
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return list;
	}
}
