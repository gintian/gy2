package com.hjsj.hrms.businessobject.general.deci.definition;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

public class StatCutlineBo {
	private Connection conn = null;

	private RowSet frowset = null;

	public StatCutlineBo(Connection conn) {
		this.conn = conn;
	}

	// 根据 itemid or fieldSetid 得到代码项指标集合
	public ArrayList getFieldItemList(String itemid, String fieldSetid) {
		ArrayList list = new ArrayList();

		ContentDAO dao = new ContentDAO(this.conn);
		String a_fieldSetID = "";
		try {
			String sql = "";
			if (fieldSetid != null) {
				sql = "select * from fielditem where fieldsetid='" + fieldSetid
						+ "' and codesetid<>'0'";
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

			} else { // 修改
				if (!"b0110".equalsIgnoreCase(itemid)
						&& !"e0122".equalsIgnoreCase(itemid)
						&& !"e01a1".equalsIgnoreCase(itemid)) {
					this.frowset = dao
							.search("select fieldsetid from fielditem where itemid='"
									+ itemid + "'");
					if (this.frowset.next()) {
						a_fieldSetID = this.frowset.getString("fieldsetid");
					}
				} else {
					a_fieldSetID = "A01";
				}
				sql = "select * from fielditem where fieldsetid='"
						+ a_fieldSetID + "' and codesetid<>'0'";
			}

			if (fieldSetid != null || "A01".equalsIgnoreCase(a_fieldSetID)) {
				// if((fieldSetid!=null&&fieldSetid.equalsIgnoreCase("A01"))||a_fieldSetID.equalsIgnoreCase("A01"))
				// {
				/*CommonData aCommonData1 = new CommonData("b0110", "单位名称");
				list.add(aCommonData1);
				CommonData aCommonData2 = new CommonData("e0122", "部门名称");
				list.add(aCommonData2);
				CommonData aCommonData3 = new CommonData("e01a1", "职位名称");
				list.add(aCommonData3);*/
				// }
			}
			this.frowset = dao.search(sql);
			while (this.frowset.next()) {
				CommonData aCommonData = new CommonData(this.frowset
						.getString("itemid"), this.frowset
						.getString("itemdesc"));
				list.add(aCommonData);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 根据 fielditemid 得到 相应代码id
	 * 
	 * @param fieldItemID
	 * @return
	 */
	public String getCodeSetIDmByID(String fieldItemID) {
		String codesetid = "";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			if ("b0110".equals(fieldItemID)) {
                codesetid = "UN";
            } else if ("e0122".equals(fieldItemID)) {
                codesetid = "UM";
            } else if ("e01a1".equals(fieldItemID)) {
                codesetid = "@K";
            } else {
				RowSet rowSet = dao
						.search("select * from fielditem where itemid='"
								+ fieldItemID + "'");
				if (rowSet.next()) {
                    codesetid = rowSet.getString("codesetid");
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return codesetid;
	}

	/**
	 * 根据代码项目值 和 代码类别 取得 代码项目值描述
	 * 
	 * @param codeItemValue
	 *            代码项目值
	 * @param codeSet
	 *            代码类别
	 * @return String
	 */
	public String getCodeValues(String codeItemValue, String codeSet) {
		StringBuffer stringBuffer = new StringBuffer("");
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			String sql = "";
			if (codeItemValue.indexOf(",") == -1) {
				if (!"UN".equals(codeSet) && !"@K".equals(codeSet)
						&& !"UM".equals(codeSet)) {
                    sql = "select * from codeitem where codesetid='" + codeSet
                            + "' and codeitemid='" + codeItemValue + "'";
                } else {
                    sql = "select * from organization where  codeitemid='"
                            + codeItemValue + "'";
                }
			} else {
				String[] codeitemids = codeItemValue.split(",");
				if (!"UN".equals(codeSet) && !"@K".equals(codeSet)
						&& !"UM".equals(codeSet)) {
					sql = "select * from codeitem where codesetid='" + codeSet
							+ "' and ( codeitemid='" + codeitemids[0] + "'";
					for (int i = 1; i < codeitemids.length; i++) {
                        sql += " or codeitemid='" + codeitemids[i] + "'";
                    }
					sql += " )";
				} else {
					sql = "select * from organization where  codeitemid='"
							+ codeitemids[0] + "'";
					for (int i = 1; i < codeitemids.length; i++) {
                        sql += " or codeitemid='" + codeitemids[i] + "'";
                    }

				}
			}
			RowSet rowSet = dao.search(sql);
			while (rowSet.next()) {
				stringBuffer.append(rowSet.getString("codeitemdesc") + ",");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer.toString();
	}

	/**
	 * 得到图例分类集合
	 * 
	 * @return
	 */
	public ArrayList getDsKeyItemtypeList() {
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		try {
			this.frowset = dao.search("select * from ds_key_itemtype ");
			while (this.frowset.next()) {
				RecordVo vo = new RecordVo("ds_key_itemtype");
				vo.setString("typeid", this.frowset.getString("typeid"));
				vo.setString("name", this.frowset.getString("name"));
				vo.setInt("status", this.frowset.getInt("status"));
				list.add(vo);
			}

		} catch (Exception sqle) {
			sqle.printStackTrace();
		}
		return list;
	}

	/**
	 * 根据id得到关键指标图例信息记录
	 * 
	 * @param itemid
	 * @return
	 */
	public RecordVo getKeyItemByID(String itemid) {
		RecordVo vo = new RecordVo("ds_key_item");
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			this.frowset = dao
					.search("select * from ds_key_item where itemid='" + itemid
							+ "'");
			if (this.frowset.next()) {
				vo.setString("itemid", this.frowset.getString("itemid"));
				vo.setString("itemname", this.frowset.getString("itemname"));
				vo.setString("typeid", this.frowset.getString("typeid"));
				vo
						.setString("field_name", this.frowset
								.getString("field_name"));
				vo.setString("flag", this.frowset.getString("flag"));
				vo.setString("codeitem_value", Sql_switcher.readMemo(
						this.frowset, "codeitem_value"));
				vo.setString("key_factors", Sql_switcher.readMemo(this.frowset,
						"key_factors"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	/**
	 * 根据id得到图例分类记录
	 * 
	 * @param typeid
	 * @return
	 */
	public RecordVo getItemtypeByID(String typeid) {
		RecordVo vo = new RecordVo("ds_key_itemtype");
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			this.frowset = dao
					.search("select * from ds_key_itemtype where typeid='"
							+ typeid + "'");
			if (this.frowset.next()) {
				vo.setString("typeid", this.frowset.getString("typeid"));
				vo.setString("name", this.frowset.getString("name"));
				vo.setString("status", this.frowset.getString("status"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	/**
	 * 保存 或 更新 图例分类记录
	 * 
	 * @param typeid
	 * @param name
	 *            名称
	 * @param status
	 *            是否有效
	 */
	public void saveOrUpdate_keyItemtype(String typeid, String name,
			String status) {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RecordVo vo = new RecordVo("ds_key_itemtype");
			if (typeid == null || typeid.trim().length() == 0) {
				IDGenerator idg = new IDGenerator(2, this.conn);
				String tid = idg.getId("ds_key_itemtype.typeid");
				vo.setString("typeid", tid);
				vo.setString("name", name);
				vo.setString("status", status);

				dao.addValueObject(vo);

			} else {

				vo.setString("typeid", typeid);
				vo.setString("name", name);
				vo.setString("status", status);
				dao.updateValueObject(vo);
			}
		} catch (Exception exx) {
			exx.printStackTrace();
		}
	}

	/**
	 * 保存 或 更新 关键指标图列信息
	 * 
	 * @param typeid
	 * @param name
	 *            名称
	 * @param status
	 *            是否有效
	 */
	public void saveOrUpdate_dskeyItem(String a_itemid, String a_typeid,
			String a_itemname, String a_keyFactors, String a_flag,
			String codeItemValue, String fieldItemID) {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RecordVo vo = new RecordVo("ds_key_item");
			if (a_itemid == null || a_itemid.trim().length() == 0) {
				IDGenerator idg = new IDGenerator(2, this.conn);
				String tid = idg.getId("ds_key_item.itemid");
				vo.setString("itemid", tid);
				vo.setString("itemname", a_itemname);
				vo.setString("typeid", a_typeid);
				vo.setString("field_name", fieldItemID);
				vo.setString("codeitem_value", codeItemValue);
				vo.setString("key_factors", a_keyFactors);
				vo.setString("flag", a_flag);
				dao.addValueObject(vo);

			} else {
				vo.setString("itemid", a_itemid);
				vo.setString("itemname", a_itemname);
				vo.setString("typeid", a_typeid);
				vo.setString("field_name", fieldItemID);
				vo.setString("codeitem_value", codeItemValue);
				vo.setString("key_factors", a_keyFactors);
				vo.setString("flag", a_flag);
				dao.update("update ds_key_item set itemname='" + a_itemname
						+ "',typeid='" + a_typeid + "',field_name='"
						+ fieldItemID + "',codeitem_value='" + codeItemValue
						+ "',key_factors='" + a_keyFactors + "',flag='"
						+ a_flag + "' where itemid='" + a_itemid + "'");
			}
		} catch (Exception exx) {
			exx.printStackTrace();
		}
	}

	/**
	 * 根据统计对象和统计图例分类得到关键指标图例信息集合
	 * 
	 * @param object
	 *            统计对象
	 * @param typeid
	 *            统计图例分类id
	 * @return ArrayList
	 */
	public ArrayList getStatCutlineList(String object, String typeid) {
		ArrayList statCutlineList = new ArrayList();
		StringBuffer sql = new StringBuffer(
				"select itemid,itemname,codeitem_value,field_name,key_factors  from ds_key_item where  typeid='");
		sql.append(typeid + "' ");
		sql.append(" and flag='");
		sql.append(object + "'");
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			frowset = dao.search(sql.toString());
			while (frowset.next()) {
				RecordVo vo = new RecordVo("ds_key_item");
				vo.setString("itemid", frowset.getString("itemid"));
				vo.setString("itemname", frowset.getString("itemname"));
				vo.setString("field_name", frowset.getString("field_name"));
				String codeitem_value = Sql_switcher.readMemo(frowset,
						"codeitem_value");
				vo.setString("codeitem_value", getStatCodeValue(codeitem_value,
						frowset.getString("field_name")));
				vo.setString("key_factors", Sql_switcher.readMemo(frowset,
						"key_factors"));
				statCutlineList.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statCutlineList;
	}

	/**
	 * 将代码值串 转换成 代码描述字符串
	 * 
	 * @param codeValue
	 *            代码值 1,2,3 || 2
	 * @param field_name
	 *            指标编码
	 * @return String
	 */
	public String getStatCodeValue(String codeValue, String field_name) {
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer value = new StringBuffer("");
		String codeitemValue = "";
		String[] codeitemid = null;
		if (codeValue == null || codeValue.trim().length() == 0) {
            return "";
        } else if (codeValue.indexOf(",") == -1) {
			codeitemid = new String[1];
			codeitemValue = "'" + codeValue + "'";
		} else {
			codeitemid = codeValue.split(",");
			for (int i = 0; i < codeitemid.length; i++) {
				codeitemValue += ",'" + codeitemid[i] + "'";
			}
			codeitemValue = codeitemValue.substring(1);
		}
		try {
			RowSet rowSet = null;
			if (!"b0110".equalsIgnoreCase(field_name)
					&& !"e0122".equalsIgnoreCase(field_name)
					&& !"e01a1".equalsIgnoreCase(field_name)) {
				rowSet = dao
						.search("select * from codeitem where codesetid=(select codesetid from fielditem where itemid='"
								+ field_name
								+ "') and codeitemid in ("
								+ codeitemValue + ")");
			} else {
				rowSet = dao
						.search("select * from organization where codeitemid in ("
								+ codeitemValue + ")");
			}

			while (rowSet.next()) {
				value.append("," + rowSet.getString("codeitemdesc"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (value.length() > 0) {
            return value.substring(1);
        }
		return value.toString();
	}

	/**
	 * 得到当前信息群中的关键指标集合
	 * 
	 * @param flag
	 *            A：人员 B：单位 K：职位
	 * @return ArrayList
	 */
	public ArrayList getKeyFactorList(String flag) {
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		try {
			this.frowset = dao
					.search("select factorid,name from ds_key_factor where flag='"
							+ flag + "'");
			String name = "";
			while (this.frowset.next()) {
				name = this.frowset.getString("name");
				CommonData aCommonData = new CommonData(this.frowset
						.getString("factorid"), name);
				list.add(aCommonData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	// 得到集合中第一条记录的id
	public String getFirstInfoId(ArrayList list) {
		String id = "0";
		if (list.size() > 0) {
			CommonData aCommonData = (CommonData) list.get(0);
			id = aCommonData.getDataValue();
		}
		return id;
	}

	/**
	 * 得到对象例表
	 * 
	 * @return ArrayList
	 */
	public ArrayList getObjectList() {
		ArrayList list = new ArrayList();
		CommonData aCommonData1 = new CommonData("A", "人员");
		CommonData aCommonData2 = new CommonData("B", "单位");
		CommonData aCommonData3 = new CommonData("K", "职位");
		list.add(aCommonData1);
		list.add(aCommonData2);
		list.add(aCommonData3);
		return list;
	}

	/**
	 * 得到指标图例分类集合
	 * 
	 * @return
	 */
	public ArrayList getTypeList() {
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		try {
			this.frowset = dao
					.search("select * from ds_key_itemtype where status=1 ");
			while (this.frowset.next()) {
				CommonData aCommonData = new CommonData(this.frowset
						.getString("typeid"), this.frowset.getString("name"));
				list.add(aCommonData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
