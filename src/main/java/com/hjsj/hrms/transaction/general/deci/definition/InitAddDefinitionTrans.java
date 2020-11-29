package com.hjsj.hrms.transaction.general.deci.definition;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class InitAddDefinitionTrans extends IBusiness {

	public void execute() throws GeneralException {

		// System.out.println("***********************************************");

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String flag = (String) hm.get("object");
		String nam = (String) hm.get("nam");

		// 默认指标集
		ArrayList fieldsetlist = new ArrayList();
		ArrayList fieldSetList = DataDictionary.getFieldSetList(
				Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		for (int i = 0; i < fieldSetList.size(); i++) {
			FieldSet fieldset = (FieldSet) fieldSetList.get(i);
			String temp = fieldset.getFieldsetid();
			if (this.checkFieldSet(temp)) {
				if ("A".equals(flag)) {
					if (!"A00".equals(temp)
							&& !"B".equals(temp.substring(0, 1))
							&& !"K".equals(temp.substring(0, 1))) {
						CommonData dataobj = new CommonData(temp, fieldset.getCustomdesc());
						fieldsetlist.add(dataobj);
					}
				} else if ("B".equals(flag)) {
					if ("B".equals(temp.substring(0, 1))) {
						CommonData dataobj = new CommonData(temp, fieldset.getCustomdesc());
						fieldsetlist.add(dataobj);
					}
				} else {
					if ("K".equals(temp.substring(0, 1))) {
						CommonData dataobj = new CommonData(temp, fieldset.getCustomdesc());
						fieldsetlist.add(dataobj);
					}
				}
			}
		}

		// 默认指标
		String dialog = "";
		ArrayList fielditemlist = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "";
		if ("A".equals(flag)) {
			sql = "select * from fielditem where fieldsetid='A01' and codesetid <> '0' and useflag='1'";
			CommonData dataobj = new CommonData("B0110", "单位");
			fielditemlist.add(dataobj);
			CommonData dataobj1 = new CommonData("E01A1", "职位");
			fielditemlist.add(dataobj1);
		} else if ("B".equals(flag)) {
			sql = "select * from fielditem where fieldsetid='B01' and codesetid <> '0' and useflag='1'";
			CommonData dataobj = new CommonData("B0110", "单位");
			fielditemlist.add(dataobj);
			CommonData dataobj2 = new CommonData("E0122", "部门");
			fielditemlist.add(dataobj2);
			CommonData dataobj1 = new CommonData("E01A1", "职位");
			fielditemlist.add(dataobj1);

		} else {
			sql = "select * from fielditem where fieldsetid='K01' and codesetid <> '0' and useflag='1'";
			CommonData dataobj = new CommonData("B0110", "单位");
			fielditemlist.add(dataobj);
			CommonData dataobj2 = new CommonData("E0122", "部门");
			fielditemlist.add(dataobj2);
			CommonData dataobj1 = new CommonData("E01A1", "职位");
			fielditemlist.add(dataobj1);
		}

		try {
			this.frowset = dao.search(sql);
			/*
			 * if(this.frowset.next()){ String itemid =
			 * this.frowset.getString("itemid"); dialog = itemid; String
			 * itemdesc = this.frowset.getString("itemdesc"); CommonData dataobj =
			 * new CommonData(itemid,itemdesc ); fielditemlist.add(dataobj);
			 */

			while (this.frowset.next()) {
				CommonData dataobj = new CommonData(this.frowset
						.getString("itemid"), this.frowset
						.getString("itemdesc"));
				fielditemlist.add(dataobj);
			}

			/* } */

		} catch (SQLException e) {
			e.printStackTrace();
		}

		String temp = "";
		/*
		 * if(dialog == null ||dialog.equals("")){ }else{ temp =
		 * DataDictionary.getFieldItem(dialog).getCodesetid(); }
		 */
		temp = DataDictionary.getFieldItem("B0110").getCodesetid();

		// System.out.println(temp);

		// System.out.println(fieldsetlist.size());
		// System.out.println(fielditemlist.size());

		this.getFormHM().put("fieldsetlist", fieldsetlist);
		this.getFormHM().put("fielditemlist", fielditemlist);

		this.getFormHM().put("object", flag);
		this.getFormHM().put("dialog", temp);
		this.getFormHM().put("typeid", nam);

		this.getFormHM().put("onefielditemvalue", "");
		this.getFormHM().put("onefielditem", "");
		this.getFormHM().put("twofielditemvalue", "");
		this.getFormHM().put("twofielditem", "");
		this.getFormHM().put("box", "");

		// this.getFormHM().put("typeid","");
		this.getFormHM().put("staticmethod", "");
		this.getFormHM().put("codeitemvalues", "");
		this.getFormHM().put("codeitemdescs", "");

		this.getFormHM().put("name", "");
		this.getFormHM().put("desc", "");
		this.getFormHM().put("standartvalue", "");
		this.getFormHM().put("controlvalue", "");

		this.getFormHM().put("operateflag", "1");
		this.getFormHM().put("fieldname", "");
		this.getFormHM().put("fieldset", "");

		this.getFormHM().put("factorid", "");

	}

	public boolean checkFieldSet(String fieldsetid) {
		boolean b = false;
		if ("A01".equalsIgnoreCase(fieldsetid)
				|| "B01".equalsIgnoreCase(fieldsetid)
				|| "K01".equalsIgnoreCase(fieldsetid)) {
			return true;
		}
		String sql = "select * from fielditem where fieldsetid='" + fieldsetid
				+ "' and codesetid <>'0' and useflag='1'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql);
			if (this.frowset.next()) {
				b = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return b;
	}
}
