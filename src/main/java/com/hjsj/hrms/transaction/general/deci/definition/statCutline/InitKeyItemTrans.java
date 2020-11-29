package com.hjsj.hrms.transaction.general.deci.definition.statCutline;

import com.hjsj.hrms.businessobject.general.deci.definition.StatCutlineBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class InitKeyItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String item_id = (String) hm.get("item_id");// 统计指标号
		StatCutlineBo statCutlineBo = new StatCutlineBo(this.getFrameconn());
		String a_flag = (String) this.getFormHM().get("object");

		ArrayList fieldSetList = getFieldSetList(a_flag);// 指标集

		String fieldSetID = statCutlineBo.getFirstInfoId(fieldSetList);

		ArrayList fieldItemList = statCutlineBo.getFieldItemList(null,
				fieldSetID);

		String fieldItemID = statCutlineBo.getFirstInfoId(fieldItemList);
		String codeValues = "";
		String codeItemValue = "";
		String defaultCodeSetID = statCutlineBo.getCodeSetIDmByID(fieldItemID);
		ArrayList keyFactorList = statCutlineBo.getKeyFactorList((String) this
				.getFormHM().get("object"));

		String a_typeid = (String) this.getFormHM().get("typeid");
		String a_itemid = "";
		String a_itemname = "";
		String a_keyFactors = "";
		String aa_keyFactors = "";

		if (item_id != null && item_id.trim().length() > 0) {

			RecordVo recordvo = statCutlineBo.getKeyItemByID(item_id);
			a_itemid = item_id;
			a_typeid = recordvo.getString("typeid");
			a_itemname = recordvo.getString("itemname");
			a_keyFactors = recordvo.getString("key_factors");
			aa_keyFactors = recordvo.getString("key_factors");

			a_flag = recordvo.getString("flag");
			fieldItemID = recordvo.getString("field_name");// 指标ID
			fieldSetID = getFieldSetidByitemID(a_flag, fieldItemID);// 指标集ID

			codeItemValue = recordvo.getString("codeitem_value");
			String codeSet = statCutlineBo.getCodeSetIDmByID(fieldItemID);
			defaultCodeSetID = codeSet;
			codeValues = statCutlineBo.getCodeValues(codeItemValue, codeSet);
			// fieldItemList = statCutlineBo.getFieldItemList(fieldItemID,
			// null);
			fieldItemList = this.getFieldItemList(a_flag, fieldSetID);
			fieldSetList = this.getFieldSetList(a_flag);
		}
		this.getFormHM().put("aa_keyFactors", aa_keyFactors);
		this.getFormHM().put("fieldItemList", fieldItemList);
		this.getFormHM().put("fieldSetList", fieldSetList);
		this.getFormHM().put("fieldItemID", fieldItemID);
		this.getFormHM().put("fieldSetID", fieldSetID);
		this.getFormHM().put("codeValues", codeValues);
		this.getFormHM().put("codeItemValue", codeItemValue);
		this.getFormHM().put("keyFactorList", keyFactorList);
		this.getFormHM().put("a_itemid", a_itemid);
		this.getFormHM().put("a_itemname", a_itemname);
		this.getFormHM().put("a_keyFactors", a_keyFactors);
		this.getFormHM().put("a_typeid", a_typeid);
		this.getFormHM().put("a_flag", a_flag);
		this.getFormHM().put("defaultCodeSetID", defaultCodeSetID);
		hm.remove("item_id");
	}

	public String getFieldSetidByitemID(String flag, String fieldItemid) {
		String fieldSetId = "";
		if ("B0110".equalsIgnoreCase(fieldItemid)
				|| "E0122".equalsIgnoreCase(fieldItemid)
				|| "E01A1".equalsIgnoreCase(fieldItemid)) {
			if ("A".equalsIgnoreCase(flag)) {
				fieldSetId = "A01";
			} else if ("B".equalsIgnoreCase(flag)) {
				fieldSetId = "B01";
			} else if ("K".equalsIgnoreCase(flag)) {
				fieldSetId = "K01";
			}
		} else {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				this.frowset = dao
						.search("select fieldsetid from fielditem where itemid='"
								+ fieldItemid + "'");
				if (this.frowset.next())
					fieldSetId = this.frowset.getString("fieldsetid");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return fieldSetId;
	}

	// 得到指标集
	public ArrayList getFieldSetList(String a_flag) {
		// System.out.println("a_flag=" + a_flag);
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			String sql = "select fieldSetid,customdesc,fieldsetdesc from fieldset where useflag='1' ";
			if ("B".equals(a_flag)) {
				sql += " and fieldSetId like 'B%' ";
			} else if ("K".equals(a_flag)) {
				sql += " and fieldSetId like 'K%'";
			} else if ("A".equalsIgnoreCase(a_flag)) {
				sql += " and fieldSetId like 'A%'";
			}
			this.frowset = dao.search(sql);
			while (this.frowset.next()) {
				String t = this.frowset.getString("fieldsetid");
				if (this.checkFieldSet(t)) {// 筛选
					CommonData aCommonData = null;
					if (this.frowset.getString("customdesc") == null
							|| "".equals(this.frowset.getString("customdesc"))) {
						aCommonData = new CommonData(t, this.frowset
								.getString("fieldsetdesc"));
					} else {
						aCommonData = new CommonData(t, this.frowset
								.getString("customdesc"));
					}
					list.add(aCommonData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 判断指定的指标集是否包含代码型指标
	 * 
	 * @param fieldsetid
	 * @return
	 */
	public boolean checkFieldSet(String fieldsetid) {
		boolean b = false;
		if ("A01".equalsIgnoreCase(fieldsetid)
				|| "B01".equalsIgnoreCase(fieldsetid)
				|| "K01".equalsIgnoreCase(fieldsetid)) {
			return true;
		}
		String sql = "select * from fielditem where fieldsetid='" + fieldsetid
				+ "' and codesetid <> '0' and useflag='1'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			RowSet rs = dao.search(sql);
			if (rs.next()) {
				b = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return b;
	}

	public ArrayList getFieldItemList(String flag, String fieldSetid) {
		ArrayList list = new ArrayList();

		String sql = "select * from fielditem where fieldsetid='" + fieldSetid
				+ "' and codesetid <> '0' and useflag='1'";
		if ("A01".equalsIgnoreCase(fieldSetid)) {
			CommonData aCommonData1 = new CommonData("b0110", "单位");
			list.add(aCommonData1);
			CommonData aCommonData3 = new CommonData("e01a1", "职位");
			list.add(aCommonData3);
		} else if ("B01".equalsIgnoreCase(fieldSetid)) {
			CommonData aCommonData1 = new CommonData("b0110", "单位");
			list.add(aCommonData1);
			CommonData aCommonData2 = new CommonData("e0122", "部门");
			list.add(aCommonData2);
			CommonData aCommonData3 = new CommonData("e01a1", "职位");
			list.add(aCommonData3);
		} else if ("K01".equalsIgnoreCase(fieldSetid)) {
			CommonData aCommonData1 = new CommonData("b0110", "单位");
			list.add(aCommonData1);
			CommonData aCommonData2 = new CommonData("e0122", "部门");
			list.add(aCommonData2);
			CommonData aCommonData3 = new CommonData("e01a1", "职位");
			list.add(aCommonData3);
		}

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql);
			while (this.frowset.next()) {
				CommonData aCommonData = new CommonData(this.frowset
						.getString("itemid"), this.frowset
						.getString("itemdesc"));
				list.add(aCommonData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}
}
