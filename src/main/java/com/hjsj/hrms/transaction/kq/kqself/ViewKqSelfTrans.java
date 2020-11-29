package com.hjsj.hrms.transaction.kq.kqself;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hjsj.hrms.utils.PubFunc;
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

public class ViewKqSelfTrans extends IBusiness {

	public void execute() throws GeneralException {
		String table = (String) this.getFormHM().get("table");
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String id = (String) hm.get("id");
		id = PubFunc.decrypt(id);
		String audit_flag = (String) hm.get("audit_flag");
		audit_flag = PubFunc.decrypt(audit_flag);
		if (audit_flag == null || audit_flag.length() <= 0) {
			audit_flag = "";
		}
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String insertname = "";
		String ta = table.toLowerCase();
		ArrayList fieldlist = DataDictionary.getFieldList(table,
				Constant.USED_FIELD_SET);// 字段名
		ArrayList list = new ArrayList();
		try {
			RecordVo vo = new RecordVo(table);
			String temp;
			vo.setString(ta + "01", id);
			insertname = ta + "01";

			vo = dao.findByPrimaryKey(vo);
			SearchAllApp searchAllApp = new SearchAllApp();
			this.getFormHM().put("selist",searchAllApp.getTableList(table, this.getFrameconn()));
			for (int i = 0; i < fieldlist.size(); i++) {
				FieldItem field = (FieldItem) fieldlist.get(i);
				if ("q11".equalsIgnoreCase(table)&& (ta + "z4").equalsIgnoreCase(field.getItemid()))// 加班申请不显示数据库中时长，显示计算时长q11ld
					continue;
				if (field.getItemid().equals(insertname))
					field.setValue(id);
				else {
					if ("D".equals(field.getItemtype())) {
						Date dd = vo.getDate(field.getItemid());
						if (dd != null) {
							String dd_str = DateUtils.format(dd,
									"yyyy-MM-dd HH:mm");
							field.setValue(dd_str);
						}
						// .replace('-','.' )
					} else if ("A".equals(field.getItemtype())) {
						field.setValue(vo.getString(field.getItemid()
								.toLowerCase()));
						if ("q1503".equals(field.getItemid())) {
//							this.getFormHM().put(
//									"selist",
//									searchAllApp.getOneList("0", this
//											.getFrameconn()));
							this.getFormHM().put("sels", field.getValue());
						}
						if ("q1103".equals(field.getItemid())) {
//							this.getFormHM().put(
//									"selist",
//									searchAllApp.getOneList("1", this
//											.getFrameconn()));
							this.getFormHM().put("sels", field.getValue());
						}
						if ("q1303".equals(field.getItemid())) {
//							this.getFormHM().put(
//									"selist",
//									searchAllApp.getOneList("3", this
//											.getFrameconn()));
							this.getFormHM().put("sels", field.getValue());
						}
						temp = vo.getString(field.getItemid().toLowerCase());
						if (temp != null && temp.trim().length() > 0)
							temp = AdminCode
									.getCode(field.getCodesetid(), temp) != null ? AdminCode
									.getCode(field.getCodesetid(), temp)
									.getCodename()
									: "";
						field.setViewvalue(temp);
					} else
						field.setValue(vo.getString(field.getItemid()
								.toLowerCase()));
				}
				if (field.getItemid().equals(ta + "01")
						|| field.getItemid().equals(ta + "09")
						|| field.getItemid().equals(ta + "11")
						|| field.getItemid().equals(ta + "13")
						|| field.getItemid().equals(ta + "15")
						|| field.getItemid().equals(ta + "z5")
						|| field.getItemid().equals(ta + "z0")
						|| "nbase".equals(field.getItemid())
						|| "a0100".equals(field.getItemid())
						|| "b0110".equals(field.getItemid())
						|| "e0122".equals(field.getItemid())
						|| "e01a1".equals(field.getItemid())
						|| "a0101".equals(field.getItemid()))
					field.setVisible(false);
				else if ("q1517".equals(field.getItemid())
						|| "q1519".equals(field.getItemid()))
					field.setVisible(false);
				else
					field.setVisible(true);
				if (field.getItemid().equals(ta + "11")) {
					if (vo.getString(field.getItemid().toLowerCase()) != null
							&& vo.getString(field.getItemid().toLowerCase())
									.length() > 0) {
						field.setVisible(true);
					}
				}
				if (field.getItemid().equals(ta + "15")) {
					if (vo.getString(field.getItemid().toLowerCase()) != null
							&& vo.getString(field.getItemid().toLowerCase())
									.length() > 0) {
						field.setVisible(true);
					}
				}
				FieldItem field_n = (FieldItem) field.cloneItem();
				list.add(field_n);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("fieldlist", list);
		KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());
		ArrayList class_list = kqUtilsClass.getKqClassList();
		
//		for(int i = 0;i< class_list.size();i++){
//			CommonData class_id = (CommonData) class_list.get(i);
//			String classValue = class_id.getDataValue();
//			if(classValue.equals("#")){
//				continue;
//			}else if (!userView.isHaveResource(IResourceConstant.KQ_BASE_CLASS, classValue)){
//				class_list.remove(class_id);
//				i--;
//			}
//		}
		this.getFormHM().put("class_list", class_list);
	}

}
