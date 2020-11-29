package com.hjsj.hrms.transaction.kq.kqself;

import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hjsj.hrms.businessobject.kq.app_check_in.ViewAllApp;
import com.hjsj.hrms.businessobject.kq.kqself.KqSelfBusiness;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ViewOneRecordTrans extends IBusiness {

	public void execute() throws GeneralException {
		String table = (String) this.getFormHM().get("table");
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String id = (String) hm.get("id");
		String audit_flag = (String) hm.get("audit_flag");
		if (audit_flag == null || audit_flag.length() <= 0) {
			audit_flag = "";
		}
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String insertname = "";
		String ta = table.toLowerCase();
		ArrayList fieldlist = DataDictionary.getFieldList(table, Constant.USED_FIELD_SET);// 字段名
		/*********** 自助用户查看申请,增加申请时长 *************/
		String start_d = "";
		String end_d = "";
		for (int j = 0; j < fieldlist.size(); j++) {
			FieldItem field_new = new FieldItem();
			FieldItem field = (FieldItem) fieldlist.get(j);
			if (field.getItemid().equals(ta + "z3")) {
				field_new.setItemid(ta + "ld");
				field_new.setItemdesc("申请时长");
				field_new.setItemtype("N");
				field_new.setState("1");
				field_new.setVisible(true);
				fieldlist.add(j + 1, field_new);
				break;
			}
		}
		ArrayList list = new ArrayList();
		try {
			RecordVo vo = new RecordVo(table);
			String temp;
			vo.setString(ta + "01", id);
			insertname = ta + "01";

			vo = dao.findByPrimaryKey(vo);
			SearchAllApp searchAllApp = new SearchAllApp();
			this.getFormHM().put("selist", searchAllApp.getTableList(table, this.frameconn));
			for (int i = 0; i < fieldlist.size(); i++) {
				FieldItem field = (FieldItem) fieldlist.get(i);
				if ("q15".equalsIgnoreCase(table)&& (ta + "04").equalsIgnoreCase(field.getItemid()))// 请假申请不显示参考班次
					continue;
				if ("q11".equalsIgnoreCase(table)&& (ta + "z4").equalsIgnoreCase(field.getItemid()))// 加班申请不显示数据库中时长，显示计算时长q11ld
					continue;
				if (field.getItemid().equals(insertname))
					field.setValue(id);
				else {
					if ("D".equals(field.getItemtype())) {
						Date dd = vo.getDate(field.getItemid());
						if (dd != null) {
							if (field.getItemid().equals(ta + "z1")) {
								start_d = DateUtils.format(dd, "yyyy-MM-dd HH:mm");
								field.setValue(start_d);
							}
							if (field.getItemid().equals(ta + "z3")) {
								end_d = DateUtils.format(dd, "yyyy-MM-dd HH:mm");
								field.setValue(end_d);
							} else {
								String dd_str = DateUtils.format(dd, "yyyy-MM-dd HH:mm");
								field.setValue(dd_str);
							}
						} else {
							field.setValue(null);
						}
					} else if ("A".equals(field.getItemtype())) {
						field.setValue(vo.getString(field.getItemid().toLowerCase()));
						if ("q1503".equals(field.getItemid())) {
							this.getFormHM().put("sels", field.getValue());
						}
						if ("q1103".equals(field.getItemid())) {
							this.getFormHM().put("sels", field.getValue());
						}
						if ("q1303".equals(field.getItemid())) {
							this.getFormHM().put("sels", field.getValue());
						}
						temp = vo.getString(field.getItemid().toLowerCase());
						if (temp != null && temp.trim().length() > 0)
							temp = AdminCode.getCode(field.getCodesetid(), temp) != null ? AdminCode
									.getCode(field.getCodesetid(), temp).getCodename()
									: "";
						field.setViewvalue(temp);
					} else {
						if (field.getItemid().equals(ta + "ld")) {
							String duration = "";
							ViewAllApp viewAllApp = new ViewAllApp(this.getFrameconn());
							duration = viewAllApp.getAppTimeLenDesc(vo, ta, this.userView);
							field.setValue(duration);
						} else {
							field.setValue(vo.getString(field.getItemid().toLowerCase()));
						}
					}
				}
				if (field.getItemid().equals(ta + "01") || "nbase".equals(field.getItemid())
						|| "a0100".equals(field.getItemid()) || "b0110".equals(field.getItemid())
						|| "e0122".equals(field.getItemid()) || "e01a1".equals(field.getItemid())
						|| "a0101".equals(field.getItemid()))
					field.setVisible(false);
				else if ("q1517".equals(field.getItemid()) || "q1519".equals(field.getItemid()))
					field.setVisible(false);
				else
					field.setVisible(true);
				if (field.getItemid().equals(ta + "11")) {
					if (vo.getString(field.getItemid().toLowerCase()) != null
							&& vo.getString(field.getItemid().toLowerCase()).length() > 0) {
						field.setVisible(true);
					}
				}
				if (field.getItemid().equals(ta + "15") || field.getItemid().equals(ta + "13")) {
					if ("1".equals(field.getState())) {
						field.setVisible(true);
					} else {
						field.setVisible(false);
					}
				}
				if (field.getItemid().equals(ta + "09") || field.getItemid().equals(ta + "11")) {
					if ("1".equals(field.getState())) {
						field.setVisible(true);
					} else {
						field.setVisible(false);
						// 部门领导与部门领导意见 如果state=1显示否则不现实
						if (field.getItemid().equals(ta + "09")
								|| field.getItemid().equals(ta + "11")) {
							if (!field.isVisible()) {
								continue;
							}
						}
					}
				}
				//参考班次隐藏则不显示
				if ("q1104".equals(field.getItemid()) && "0".equals(field.getState())) {
					field.setVisible(false);
				}
				FieldItem field_n = (FieldItem) field.cloneItem();
				list.add(field_n);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("id", id);
		KqSelfBusiness kqSelfBusiness = new KqSelfBusiness(this.userView, this.getFrameconn());
		String isTemplate = "0";
		if (kqSelfBusiness.getIsTemplate(table))
			isTemplate = "1";
		this.getFormHM().put("isTemplate", isTemplate);
		this.getFormHM().put("fieldlist", list);
	}

}
