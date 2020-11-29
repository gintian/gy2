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

public class EditKeyDefinitionTrans extends IBusiness {

	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String fid = (String) hm.get("set_id");

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select * from ds_key_factor where factorid='" + fid + "'";

		String name = ""; // 关键指标
		String desc = ""; // 指标解释
		String formula = "";// 公式
		String flag = ""; // 库标识
		String standardValue = "";// 标准值
		String controlValue = ""; // 控制值
		String typeid = ""; // 关键指标类
		String staticMethod = ""; // 统计方法
		String fieldName = ""; // 统计指标
		String codeitemValues = ""; // 代码项

		try {
			this.frowset = dao.search(sql);
			if (this.frowset.next()) {
				name = this.frowset.getString("name");
				desc = this.frowset.getString("description");
				formula = this.frowset.getString("formula");
				flag = this.frowset.getString("flag");
				standardValue = this.frowset.getString("standard_value");
				controlValue = this.frowset.getString("control_value");
				typeid = this.frowset.getString("typeid");
				staticMethod = this.frowset.getString("static_method");
				fieldName = this.frowset.getString("field_name");
				codeitemValues = this.frowset.getString("codeitem_value");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (formula.indexOf('/') != -1) {
			String[] temp = formula.split("/");
			this.getFormHM().put("onefielditemvalue", temp[0]);
			this.getFormHM().put("onefielditem", this.getItemDesc(temp[0]));
			this.getFormHM().put("twofielditemvalue", temp[1]);
			this.getFormHM().put("twofielditem", this.getItemDesc(temp[1]));
			this.getFormHM().put("box", "1");
		} else {
			this.getFormHM().put("onefielditemvalue", formula);
			this.getFormHM().put("onefielditem", this.getItemDesc(formula));
		}
		this.getFormHM().put("typeid", typeid);
		this.getFormHM().put("staticmethod", staticMethod);
		this.getFormHM().put("codeitemvalues", codeitemValues);
		this.getFormHM().put("codeitemdescs",
				this.getCodeItemDesc(fieldName, codeitemValues));

		// System.out.println("aaaa=" +
		// this.getCodeItemDesc(fieldName,codeitemValues));

		this.getFormHM().put("name", name);
		this.getFormHM().put("desc", desc);
		this.getFormHM().put("standartvalue", standardValue);
		this.getFormHM().put("controlvalue", controlValue);

		this.getFormHM().put("fieldsetlist", this.getFieldSetList(flag));
		this.getFormHM().put("fielditemlist",
				this.getFieldItemList(flag, fieldName));

		this.getFormHM().put("fieldname", this.getItemDesc(fieldName));
		this.getFormHM().put("fieldset", this.getFieldSetDesc(fieldName));
		this.getFormHM().put("operateflag", "2");

		this.getFormHM().put("factorid", fid);

	}

	public String getFieldSetDesc(String fieldName) {
		String fieldSetDesc = "";
		String sql = "select fieldsetdesc,customdesc from fieldset where fieldsetid"
				+ " = (select fieldsetid from fielditem  where itemid ='"
				+ fieldName + "')";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql);
			if (this.frowset.next()) {
				if (this.frowset.getString("customdesc") == null
						|| "".equals(this.frowset.getString("customdesc"))) {
					fieldSetDesc = this.frowset.getString("fieldsetdesc");
				} else {
					fieldSetDesc = this.frowset.getString("customdesc");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fieldSetDesc;
	}

	/**
	 * 
	 * @param fieldName
	 * @param codeitemValues
	 * @return
	 */
	public String getCodeItemDesc(String fieldName, String codeitemValues) {
		StringBuffer codeItemDescInfo = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		if ("B0110".equalsIgnoreCase(fieldName)
				|| "E0122".equalsIgnoreCase(fieldName)
				|| "E01A1".equalsIgnoreCase(fieldName)) {
			String t = "";
			if ("B0110".equalsIgnoreCase(fieldName)) {
				t = "UN";
			} else if ("E0122".equalsIgnoreCase(fieldName)) {
				t = "UM";
			} else {
				t = "@K";
			}

			String[] temp = codeitemValues.split(",");
			for (int i = 0; i < temp.length; i++) {
				String sql = "select codeitemdesc from  organization where codeitemid='"
						+ temp[i] + "' and codesetid='" + t + "'";
				try {
					this.frowset = dao.search(sql);
					if (this.frowset.next()) {
						codeItemDescInfo.append(this.frowset
								.getString("codeitemdesc"));
						codeItemDescInfo.append(",");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		} else {
			String[] temp = codeitemValues.split(",");
			for (int i = 0; i < temp.length; i++) {
				String sql = "select codeitemdesc from  codeitem where codesetid = "
						+ "(select codesetid from fielditem where itemid = '"
						+ fieldName + "') and codeitemid='" + temp[i] + "' ";
				try {
					this.frowset = dao.search(sql);
					if (this.frowset.next()) {
						codeItemDescInfo.append(this.frowset
								.getString("codeitemdesc"));
						codeItemDescInfo.append(",");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		}

		return codeItemDescInfo.toString();
	}

	/**
	 * 
	 * @param itemid
	 * @return
	 */
	public String getItemDesc(String itemid) {
		String desc = "";
		String sql = "select itemdesc from fielditem  where itemid='" + itemid
				+ "'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql);
			if (this.frowset.next()) {
				desc = this.frowset.getString("itemdesc");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return desc;
	}

	/**
	 * 
	 * @param flag
	 * @return
	 */
	public ArrayList getFieldSetList(String flag) {
		ArrayList fieldsetlist = new ArrayList();
		ArrayList fieldSetList = DataDictionary.getFieldSetList(
				Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		for (int i = 0; i < fieldSetList.size(); i++) {
			FieldSet fieldset = (FieldSet) fieldSetList.get(i);
			String temp = fieldset.getFieldsetid();
			String fieldsetdesc = fieldset.getCustomdesc();
			if(fieldsetdesc == null || "".equals(fieldsetdesc)){
				fieldsetdesc = fieldset.getFieldsetdesc();
			}
			if (this.checkFieldSet(temp)) {
				if ("A".equals(flag)) {
					if (!"A00".equals(temp)
							&& "A".equals(temp.substring(0, 1))) {
						CommonData dataobj = new CommonData(temp,fieldsetdesc);
						fieldsetlist.add(dataobj);
					}
				} else if ("B".equals(flag)) {
					if ("B".equals(temp.substring(0, 1))) {
						CommonData dataobj = new CommonData(temp, fieldsetdesc);
						fieldsetlist.add(dataobj);
					}
				} else {
					if ("K".equals(temp.substring(0, 1))) {
						CommonData dataobj = new CommonData(temp, fieldsetdesc);
						fieldsetlist.add(dataobj);
					}
				}
			}

		}

		return fieldsetlist;
	}

	/**
	 * 
	 * @param itemid
	 * @return
	 */
	public ArrayList getFieldItemList(String flag, String itemid) {
		ArrayList itemList = new ArrayList();
		String sql = "select fieldsetid from fielditem where itemid='" + itemid
				+ "'";
		String fieldsetid = "";
		if ("B0110".equalsIgnoreCase(itemid)
				|| "E0122".equalsIgnoreCase(itemid)
				|| "E01A1".equalsIgnoreCase(itemid)) {
			if ("A".equalsIgnoreCase(flag)) {
				fieldsetid = "A01";
			} else if ("B".equalsIgnoreCase(flag)) {
				fieldsetid = "B01";
			} else if ("K".equalsIgnoreCase(flag)) {
				fieldsetid = "K01";
			}
		} else {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				this.frowset = dao.search(sql);
				if (this.frowset.next()) {
					fieldsetid = this.frowset.getString("fieldsetid");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		// System.out.println("itemid=" + itemid);
		// System.out.println("fieldsetid=" + fieldsetid);

		if ("A01".equals(fieldsetid)) {
			CommonData dataobj = new CommonData("B0110", "单位");
			itemList.add(dataobj);
			CommonData dataobj1 = new CommonData("E01A1", "职位");
			itemList.add(dataobj1);
		} else if ("B01".equals(fieldsetid)) {
			CommonData dataobj = new CommonData("B0110", "单位");
			itemList.add(dataobj);
			CommonData dataobj2 = new CommonData("E0122", "部门");
			itemList.add(dataobj2);
			CommonData dataobj1 = new CommonData("E01A1", "职位");
			itemList.add(dataobj1);

		} else if ("K01".equals(fieldsetid)) {
			CommonData dataobj = new CommonData("B0110", "单位");
			itemList.add(dataobj);
			CommonData dataobj2 = new CommonData("E0122", "部门");
			itemList.add(dataobj2);
			CommonData dataobj1 = new CommonData("E01A1", "职位");
			itemList.add(dataobj1);
		}

		ContentDAO dao = new ContentDAO(this.getFrameconn());

		sql = "select * from fielditem where fieldsetid='" + fieldsetid
				+ "' and codesetid <> '0' and useflag='1'";

		// System.out.println(sql);

		try {
			this.frowset = dao.search(sql);

			while (this.frowset.next()) {
				CommonData dataobj = new CommonData(this.frowset
						.getString("itemid"), this.frowset
						.getString("itemdesc"));
				itemList.add(dataobj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return itemList;
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
