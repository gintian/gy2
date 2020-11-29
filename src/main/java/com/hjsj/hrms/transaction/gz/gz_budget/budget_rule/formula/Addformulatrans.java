package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.formula;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Addformulatrans extends IBusiness {

	private String setid = "";// 预算总额
	private HashMap sysOptionMap = new HashMap(); // 系统项参数

	public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
		HashMap requestHm = (HashMap) this.getFormHM().get("requestPamaHM");
		String mode = (String) requestHm.get("mode");
		String curformulaid = (String) requestHm.get("curformulaid");
		mode = (mode != null) && (mode.length() > 0) ? mode : "add";// 新增 或 插入
		curformulaid = (curformulaid != null) && (curformulaid.length() > 0) ? curformulaid
				: "0";

		ContentDAO dao = new ContentDAO(this.frameconn);
		BudgetSysBo bo = new BudgetSysBo(this.getFrameconn(), this.userView);
		this.sysOptionMap = bo.getSysValueMap();
		setid = (String) sysOptionMap.get("ysze_set");
		ArrayList list = new ArrayList();
		ArrayList list1 = new ArrayList();
		ArrayList list2 = new ArrayList();
		ArrayList l1 = new ArrayList();
		ArrayList l2 = new ArrayList();
		String sqlstr = "select tab_id,tab_name from gz_budget_tab order by seq";
		String sql = "select itemid,itemdesc from t_hr_busifield where fieldsetid = 'SC01' order by displayid";
		String sqll = "select itemid,itemdesc from fielditem where fieldsetid = '"
				+ setid + "' order by displayid";
		ArrayList dylist = null;
		ArrayList hrlist = null;
		ArrayList fieldlist = null;
		try {
			String tabid = (String) hm.get("tab_id");
			dylist = dao.searchDynaList(sqlstr);
			for (Iterator it = dylist.iterator(); it.hasNext();) {
				DynaBean dynabean = (DynaBean) it.next();
				String tab_id = dynabean.get("tab_id").toString();
				String tab_name = dynabean.get("tab_name").toString();

				CommonData dataobj = new CommonData(tab_id, tab_name);
				list.add(dataobj);
			}
			String tab_id = "1";
			if (list.size() > 0) {
				CommonData dataobj1 = (CommonData) (list.get(0));
				tab_id = dataobj1.getDataValue().toString();
			}
			hm.put("tab_id", tab_id);
			hm.put("list", list);

			hm.put("itemid", "");
			hm.put("itemid1", "");
			// 员工名册
			hrlist = dao.searchDynaList(sql);
			String s = "";
			s = "budget_id,B0110,beginMonth,endmonth,A0100,A0101,E0122,NBase,SC010,tab_id,A0000"
					.toLowerCase();
			for (Iterator it = hrlist.iterator(); it.hasNext();) {
				DynaBean dynabean = (DynaBean) it.next();
				String itemid = dynabean.get("itemid").toString();
				if (s.indexOf(itemid.toLowerCase()) >= 0) {
					continue;
				}
				String itemdesc = dynabean.get("itemdesc").toString();
				l1.add(itemdesc);
				CommonData hr = new CommonData(itemid, itemdesc);
				list1.add(hr);
			}
			hm.put("list1", list1);
			s = this.setid + "Z0," + this.setid + "Z1,";
			if (this.sysOptionMap.get("ysze_idx_menu") != null)
				s = s + (String) this.sysOptionMap.get("ysze_idx_menu") + ",";
			if (this.sysOptionMap.get("ysze_ze_menu") != null)
				s = s + (String) this.sysOptionMap.get("ysze_ze_menu") + ",";
			if (this.sysOptionMap.get("ysze_status_menu") != null)
				s = s + (String) this.sysOptionMap.get("ysze_status_menu")
						+ ",";
			s = s.toLowerCase();
			fieldlist = dao.searchDynaList(sqll);
			for (Iterator it = fieldlist.iterator(); it.hasNext();) {
				DynaBean dynabean = (DynaBean) it.next();
				String itemid1 = dynabean.get("itemid").toString();
				if (s.indexOf(itemid1.toLowerCase()) >= 0) {
					continue;
				}
				String itemdesc = dynabean.get("itemdesc").toString();
				l2.add(itemdesc);
				CommonData field = new CommonData(itemid1, itemdesc);
				list2.add(field);
			}
			hm.put("list2", list2);
			if (!"0".equals(tabid)) {
				hm.put("tab_id", tabid);
			}
			hm.put("addmode", mode);
			hm.put("addcurformulaid", curformulaid);
			if (l1.size()>0) 
				hm.put("l1", l1.get(0));
			else
				hm.put("l1", "");
			if (l2.size()>0) 
				hm.put("l2", l2.get(0));
			else
				hm.put("l2", "");
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}

	}
}
