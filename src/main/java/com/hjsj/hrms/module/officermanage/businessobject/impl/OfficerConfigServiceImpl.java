package com.hjsj.hrms.module.officermanage.businessobject.impl;

import com.hjsj.hrms.businessobject.common.commonfunction;
import com.hjsj.hrms.module.officermanage.businessobject.OfficerConfigService;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class OfficerConfigServiceImpl implements OfficerConfigService {
	private UserView userview;
	private Connection conn;

	public OfficerConfigServiceImpl(UserView userview, Connection conn) {
		this.userview = userview;
		this.conn = conn;
	}

	@Override
    public ArrayList getFieldItemList(String fieldset) throws Exception {

		HashMap<String, Object> fielditemMap = new HashMap<String, Object>();
		ArrayList<FieldItem> itemList = DataDictionary.getFieldList(fieldset, 1);
		ArrayList list = new ArrayList();
		ArrayList<HashMap<String, String>> fieldList = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> bwList = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> fieldItemList = new ArrayList<HashMap<String, String>>();
		if (!(itemList == null || itemList.size() < 0)) {
			for (FieldItem item : itemList) {

				HashMap<String, String> fieldMap = new HashMap<String, String>();
				if ("A".equals(item.getItemtype())) {
					fieldMap.put("itemDesc", item.getItemdesc());
					fieldMap.put("itemid", item.getItemid());
					fieldMap.put("codesetId", item.getCodesetid());
					fieldMap.put("type", item.getItemtype());
					fieldItemList.add(fieldMap);
				}
				if ("UN".equalsIgnoreCase(item.getCodesetid())) {// 关联单位子集指标
					HashMap<String, String> itemMap = new HashMap<String, String>();
					itemMap.put("itemDesc", item.getItemdesc());
					itemMap.put("itemid", item.getItemid());
					itemMap.put("codesetId", item.getCodesetid());
					itemMap.put("type", item.getItemtype());
					fieldList.add(itemMap);
				}
				if ("BW".equalsIgnoreCase(item.getCodesetid())) {// 关联BW子集指标
					HashMap<String, String> itemMap = new HashMap<String, String>();
					itemMap.put("itemDesc", item.getItemdesc());
					itemMap.put("itemid", item.getItemid());
					itemMap.put("codesetId", item.getCodesetid());
					itemMap.put("type", item.getItemtype());
					bwList.add(itemMap);
				}

			}
		}
		list.add(fieldList);
		list.add(bwList);
		list.add(fieldItemList);
		return list;
	}

	/**
	 * 查询用户权限库与用户权限范围内人员子集
	 *
	 * @return
	 * @throws Exception
	 */
	@Override
    public HashMap<String, Object> getDbListFieldSetList() throws Exception {
		HashMap<String, Object> map = new HashMap<String, Object>();
		ArrayList<String> dbList = this.userview.getPrivDbList();
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		HashMap<String, String> dbmap = null;
		String sql = commonfunction.getDbcondString(dbList);// pre,dbname
		rs = dao.search(sql);
		ArrayList<HashMap<String, String>> dbMaplist = new ArrayList<HashMap<String, String>>();
		try {
			while (rs.next()) {
				dbmap = new HashMap<String, String>();
				dbmap.put("id", rs.getString("pre"));
				dbmap.put("dbname", rs.getString("dbname"));
				dbMaplist.add(dbmap);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			PubFunc.closeDbObj(rs);
		}

		map.put("dblist", dbMaplist);
		ArrayList<FieldSet> fieldSetList = DataDictionary.getFieldSetList(1, 1);// 已构库子集的人员子集
		ArrayList<HashMap<String, Object>> setList = new ArrayList<HashMap<String, Object>>();
		for (FieldSet fieldSet : fieldSetList) {
			if ("A01".equalsIgnoreCase(fieldSet.getFieldsetid()) || "A00".equalsIgnoreCase(fieldSet.getFieldsetid()))
				continue;
			HashMap<String, Object> setItemMap = new HashMap<String, Object>();
			setItemMap.put("fieldsetdesc", fieldSet.getFieldsetdesc());
			setItemMap.put("fieldsetid", fieldSet.getFieldsetid());
			setList.add(setItemMap);
		}
		map.put("setList", JSONArray.fromObject(setList).toString());
		return map;
	}

	/***
	 * 参数保存
	 */
	@Override
    public void saveSetting(HashMap map_proprites) throws Exception {
		String postSet = (String) map_proprites.get("postSet");
		String postOrg = (String) map_proprites.get("postOrg");
		String postState = (String) map_proprites.get("postState");
		String jobname = (String) map_proprites.get("jobname");
		String nbase = (String) map_proprites.get("nbase");
		String filterExpr = (String) map_proprites.get("filterExpr");
		filterExpr = StringUtils.isNotEmpty(filterExpr) ? SafeCode.decode(filterExpr) : "";
		HashMap<String, Object> mainfieldMap = (HashMap<String, Object>) map_proprites.get("mainfields");// 干部关键信息表
		ArrayList<MorphDynaBean> customlist = (ArrayList<MorphDynaBean>) map_proprites.get("customfields");// 自定义信息
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		Document doc = new Document();
		try {

			rs = dao.search("select * from constant where constant='OFFICER_PARAM'");
			String strParam = "";
			ArrayList oldCustomList = new ArrayList();// 更新操作 旧自定义内容集合
			ArrayList addList = new ArrayList();// 新增字段
			ArrayList updatList = new ArrayList();// 修改字段
			ArrayList oldColKeyList = new ArrayList();// 库中自定义字段 列名集合
			boolean isHaveData = false;
			boolean isupdateFlag = false;
			List<Element> oldMainList = null;
			if (rs.next()) {
				isHaveData = true;
				strParam = rs.getString("str_value");
				if (StringUtils.isNotEmpty(strParam)) {
					doc = PubFunc.generateDom(strParam);
					Element els = doc.getRootElement().getChild("customfields");
					List<Element> list = els.getChildren();
					HashMap map = null;
					int maxIndex = 0;// 自定义列且为存储类型列最大下标
					for (int i = 0; i < list.size(); i++) {
						Element el = list.get(i);
						map = new HashMap();
						map.put("name", el.getAttributeValue("name"));
						map.put("type", el.getAttributeValue("type"));
						map.put("value", el.getAttributeValue("value"));
						map.put("columnid", el.getAttributeValue("columnid"));
						oldColKeyList.add(el.getAttributeValue("columnid"));
						if ("2".equals(el.getAttributeValue("type"))) {
							int index = Integer.parseInt(el.getAttributeValue("columnid").substring(6));// custom1//当前col
							// id 下标
							if (maxIndex < index)
								maxIndex = index;
						}
						oldCustomList.add(map);
					}
					oldMainList = doc.getRootElement().getChild("mainfields").getChildren();
					for (int i = 0; i < customlist.size(); i++) {
						HashMap newMap = PubFunc.DynaBean2Map(customlist.get(i));
						// String newName=newMap.get("name").toString();
						String newType = newMap.get("type").toString();
						String newValue = newMap.get("value").toString();
						String columnId = "";

						if (StringUtils.isNotEmpty(newMap.get("columnid").toString())) {
							columnId = newMap.get("columnid").toString();
							oldColKeyList.remove(columnId);// 需要删除字段集合
						} else {
							if ("1".equals(newType)) {
								newMap.put("columnid", newMap.get("value"));
								customlist.get(i).set("columnid", newMap.get("value"));
							} else
								newMap.put("columnid", "custom" + (++maxIndex));// 新增列 id自增
							customlist.get(i).set("columnid", newMap.get("columnid"));
							addList.add(newMap);// 新增字段
							continue;
						}
						for (int j = 0; j < oldCustomList.size(); j++) {
							HashMap oldMap = (HashMap) oldCustomList.get(j);
							// String oldName=oldMap.get("name").toString();
							String oldType = oldMap.get("type").toString();
							String oldValue = oldMap.get("value").toString();
							if (columnId.equals(oldMap.get("columnid").toString())) {
								if (newType.equals(oldType)) {// 类型相同时名称不同
									if (oldValue.equals(newValue)) {// 名称相同时不处理
										break;
									} else {
										updatList.add(newMap);
										break;
									}
								} else {// 类型发生改变
									if ("2".equals(newType)) {
										// oldColKeyList.add(oldValue);//指标改为存储类型时 删除原有
										newMap.put("columnid", "custom" + (++maxIndex) + "`" + oldValue);
										customlist.get(i).set("columnid",
												newMap.get("columnid").toString().split("`")[0]);
									}
									updatList.add(newMap);
									break;
								}
							}
						}
					}
				}
			}

			doc = new Document();
			Element root = new Element("params");
			doc.setRootElement(root);
			Element postSetEl = new Element("postSet");
			postSetEl.setAttribute("setid", postSet);
			postSetEl.setAttribute("postOrg", postOrg);
			postSetEl.setAttribute("postState", postState);
			postSetEl.setAttribute("nbase", nbase);
			postSetEl.setAttribute("expr", filterExpr);
			postSetEl.setAttribute("jobname", jobname);
			root.addContent(postSetEl);

			Element mainEl = new Element("mainfields");

			for (String key : mainfieldMap.keySet()) {
				Element fieldEL = new Element("field");
				HashMap map = (HashMap) mainfieldMap.get(key);
				fieldEL.setAttribute("name", key);
				fieldEL.setAttribute("type", map.get("type").toString());
				fieldEL.setAttribute("value", map.containsKey("value") ? map.get("value").toString() : "");
				mainEl.addContent(fieldEL);
			}
			Element customEL = new Element("customfields");
			ArrayList customList = new ArrayList();
			if (customlist != null && customlist.size() > 0) {
				int i = 0;
				for (MorphDynaBean bean : customlist) {
					Element fieldEl = new Element("field");
					HashMap map = PubFunc.DynaBean2Map(bean);
					fieldEl.setAttribute("name", bean.get("name").toString().toLowerCase());
					fieldEl.setAttribute("type", bean.get("type").toString());
					if ("".equals(bean.get("columnid").toString())) {// 第一次新增
						if ("2".equals(map.get("type").toString())) {
							fieldEl.setAttribute("columnid", "custom" + (++i));
							map.put("columnid", fieldEl.getAttributeValue("columnid"));
						} else {
							// if("1".equals(bean.get("type").toString()))
							fieldEl.setAttribute("columnid", map.get("value").toString());
							map.put("columnid", map.get("value").toString());
						}
					} else {
						if ("1".equals(bean.get("type").toString())) {// 修改时 代码型
							fieldEl.setAttribute("columnid", bean.get("value").toString());
							map.put("columnid", bean.get("value").toString());
						} else {
							fieldEl.setAttribute("columnid", bean.get("columnid").toString());
							map.put("columnid", bean.get("columnid").toString());
						}

					}
					fieldEl.setAttribute("value",
							PubFunc.DynaBean2Map(bean).containsKey("value") ? bean.get("value").toString() : "");
					customEL.addContent(fieldEl);
					map.put("value", fieldEl.getAttributeValue("value"));

					customList.add(map);
				}
			}
			root.addContent(customEL);
			root.addContent(mainEl);

			Format format = Format.getRawFormat();
			format.setEncoding("UTF-8");
			XMLOutputter output = new XMLOutputter(format);
			String str = output.outputString(doc);
			if (isHaveData) {
				dao.update("update constant set str_value=? where constant='OFFICER_PARAM'", Arrays.asList(str));
			} else {
				dao.update(
						"insert into constant(constant,type,describe,str_value)values('OFFICER_PARAM','','干部管理配置信息',?)",
						Arrays.asList(str));
			}
			updateTable(mainfieldMap, customList, addList, updatList, oldColKeyList);// 创建/更新表结构
			updateMainField(mainfieldMap, oldMainList);
		} catch (Exception e) {
			throw e;
		} finally {
			PubFunc.closeDbObj(rs);
		}
	}

	/**
	 *
	 * @param mainfieldMap
	 *            固定字段信息
	 * @param customlist
	 *            自定义字段信息
	 * @throws Exception
	 */
	private void updateTable(HashMap<String, Object> mainfieldMap, ArrayList<HashMap> customlist,
							 ArrayList<HashMap> addList, ArrayList<HashMap> updateList, ArrayList delList) throws Exception {
		DbWizard dbWizard = new DbWizard(this.conn);
		if (dbWizard.isExistTable("om_officer_muster", false)) {
			ResultSet rs = null;
			try {
				for (int i = 0; i < delList.size(); i++) {
					String col = delList.get(i).toString();
					delColumn(col);
				}
				for (int i = 0; i < updateList.size(); i++) {
					HashMap map = updateList.get(i);
					String ids = map.get("columnid").toString();
					if (ids.indexOf("`") > -1) {
						delColumn(ids.split("`")[1]);
					} else {
						delColumn(ids);
					}

				}
				Table table = new Table("om_officer_muster");
				if (!dbWizard.isExistField("om_officer_muster", "a0101", false)) {
					Field a0101 = new Field("A0101");// 姓名列
					a0101.setDatatype(DataType.STRING);
					a0101.setLength(40);
					table.addField(a0101);
				}

				if (!dbWizard.isExistField("om_officer_muster", "report_unit", false)) {
					Field reportUN = new Field("report_unit");// 呈报单位
					reportUN.setDatatype(DataType.STRING);
					reportUN.setLength(504);
					table.addField(reportUN);
				}

				if (!dbWizard.isExistField("om_officer_muster", "a0100", false)) {
					Field a0100_field = new Field("a0100");
					a0100_field.setDatatype(DataType.STRING);
					a0100_field.setLength(8);
					table.addField(a0100_field);
				}

				for (int i = 0; i < updateList.size(); i++) {
					HashMap map = updateList.get(i);
					Field field = null;
					if ("1".equals(map.get("type").toString())) {
						field = new Field(map.get("value").toString());
						FieldItem item = DataDictionary.getFieldItem(map.get("value").toString());
						field.setDatatype(item.getItemtype());
						field.setLength((int)Math.ceil(item.getItemlength()*1.5));
					} else {
						String itemid = map.get("columnid").toString();
						if (itemid.indexOf("`") > -1)
							field = new Field(itemid.split("`")[0]);
						else
							field = new Field(itemid);
						field.setLength(50);
						field.setDatatype(DataType.STRING);
					}
					table.addField(field);
				}

				for (int i = 0; i < addList.size(); i++) {
					HashMap map = addList.get(i);
					String itemid = map.get("columnid").toString();
					Field field = new Field(itemid);
					if ("1".equals(map.get("type").toString())) {
						FieldItem item = DataDictionary.getFieldItem(itemid);
						field.setDatatype(item.getInputtype());
						field.setLength((int)Math.ceil(item.getItemlength()*1.5));
					} else {
						field.setLength(50);
						field.setDatatype(DataType.STRING);
					}
					table.addField(field);
				}

				if (!table.isEmpty())// 不为空时 新增列
					dbWizard.addColumns(table);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				PubFunc.closeDbObj(rs);
			}
		} else {
			Table table = new Table("om_officer_muster");
			HashMap<String, Integer> colMap = setColLengMap();
			Field keyField = new Field("guidkey");
			keyField.setDatatype(1);
			keyField.setLength(50);
			keyField.setNullable(false);
			keyField.setKeyable(true);
			table.addField(keyField);

			Field nbaseField = new Field("nbase");
			nbaseField.setDatatype(DataType.STRING);
			nbaseField.setLength(3);
			table.addField(nbaseField);

			Field a0100_field = new Field("a0100");
			a0100_field.setDatatype(DataType.STRING);
			a0100_field.setLength(8);
			table.addField(a0100_field);

			Field creaTime = new Field("create_time");
			creaTime.setDatatype(DataType.DATE);// 日期类型
			table.addField(creaTime);

			Field a0101 = new Field("A0101");// 姓名列
			a0101.setDatatype(DataType.STRING);
			a0101.setLength(60);
			table.addField(a0101);

			Field reportUN = new Field("report_unit");// 呈报单位
			reportUN.setDatatype(DataType.STRING);
			reportUN.setLength(756);
			table.addField(reportUN);

			Field createUser = new Field("create_user");
			createUser.setDatatype(DataType.STRING);
			createUser.setLength(75);
			table.addField(createUser);

			Field fullName = new Field("create_fullname");
			fullName.setDatatype(DataType.STRING);
			fullName.setLength(75);
			table.addField(fullName);

			Field resume = new Field("resume");// 简历
			resume.setDatatype(DataType.CLOB);// 大文本
			table.addField(resume);

			Field familyandrelation = new Field("familyandrelation");// 家庭主要成员及重要社会关系
			familyandrelation.setDatatype(DataType.CLOB);// 大文本
			table.addField(familyandrelation);

			Field id_number = new Field("id_number");// 身份证号
			id_number.setDatatype(DataType.STRING);
			id_number.setLength(18);
			table.addField(id_number);

			Field field = null;
			for (String key : colMap.keySet()) {// 固定列 全为字符串类型 长度固定
				field = new Field(key);
				field.setDatatype(DataType.STRING);// 新增字段全部为字符类型
				field.setLength(colMap.get(key));
				table.addField(field);
			}

			// 自定义列
			field = null;
			for (int i = 0; i < customlist.size(); i++) {
				HashMap map = customlist.get(i);
				if ("1".equals(map.get("type").toString())) {
					if (!map.containsKey("value")) {
						throw new Exception(
								map.get("name").toString() + ResourceFactory.getProperty("officer.fieldMapping"));
					}
					FieldItem item = DataDictionary.getFieldItem(map.get("value").toString());
					if (item == null)
						throw new Exception(
								map.get("name").toString() + ResourceFactory.getProperty("officer.notFindField"));
					field = new Field(map.get("value").toString());
					field.setDatatype(item.getItemtype());
					field.setLength((int)Math.ceil(item.getItemlength()*1.5));
				} else {
					field = new Field(map.get("columnid").toString());
					field.setLength(50);
					field.setDatatype(DataType.STRING);
				}
				if (field != null)
					table.addField(field);
				// table.getAlterFieldSqls(0);
			}
			try {
				dbWizard.createTable(table);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void updateMainField(HashMap<String, Object> newMainMap, List<Element> oldMainMap) throws Exception {
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList<String> updateList = new ArrayList<String>();
		if (oldMainMap != null) {
			for (Element el : oldMainMap) {
				String name = el.getAttributeValue("name");
				HashMap newMap = (HashMap) newMainMap.get(name);
				String type = el.getAttributeValue("type");
				String value = el.getAttributeValue("value");
				if (type.equals(newMap.get("type").toString())) {// 类型相同时
					if (!value.equals(newMap.get("value"))) {// 对应值发生改变
						updateList.add(name);
					}
				} else {// 类型不同
					updateList.add(name);
				}
			}
		}
		if (updateList.size() > 0) {
			StringBuffer sbf = new StringBuffer();
			sbf.append(" update om_officer_muster set ");
			for (int i = 0; i < updateList.size(); i++) {
				sbf.append(updateList.get(i));
				sbf.append("=''");
				if (i < updateList.size() - 1)
					sbf.append(",");
			}
			dao.update(sbf.toString());
		}
	}

	/**
	 * 删除干部管理表指定列
	 * @param column
	 * @throws Exception
	 */
	private void delColumn(String column) throws Exception {
		ContentDAO dao = new ContentDAO(this.conn);
		dao.update("ALTER TABLE om_officer_muster DROP COLUMN " + column);
	}

	private ArrayList getColList() throws Exception {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList list = new ArrayList();
		try {
			String sql = "select * from om_officer_muster where 1=2";
			stmt = this.conn.prepareStatement(sql);
			rs = stmt.executeQuery(sql);
			ResultSetMetaData data = rs.getMetaData();
			for (int i = 1; i <= data.getColumnCount(); i++) {
				String colname = data.getColumnName(i);
				list.add(colname.toLowerCase());
			}
			return list;
		} catch (Exception e) {
			throw e;
		} finally {
			PubFunc.closeDbObj(stmt);
			PubFunc.closeDbObj(rs);
		}

	}

	/****
	 * 固定列 列名 设置默认长度
	 */
	private HashMap<String, Integer> setColLengMap() {
		HashMap<String, Integer> map = new HashMap();
		map.put("sex", 3);
		map.put("birthdate", 15);
		map.put("nation", 30);
		map.put("nativeplace", 54);
		map.put("birthplace", 54);

		map.put("joinpartydate", 54);
		map.put("joinjobdate", 15);
		map.put("health", 54);
		map.put("majorpost", 270);
		map.put("majorspecialty", 330);

		map.put("education", 165);
		map.put("degree", 165);
		map.put("school", 270);
		map.put("educationmajor", 270);
		// ----
		map.put("currentpost", 972);

		map.put("preparepost", 972);
		map.put("terminalpost", 972);
		map.put("rewardsandpenalties", 1656);
		map.put("assessment", 1242);
		map.put("postreason", 1656);

		return map;
	}
	/***
	 * 查找 constant表中OFFICER_PARAM 存储数据
	 */
	@Override
    public HashMap getOfficerConstant() throws Exception {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		String xml = "";
		try {
			rs = dao.search("select * from constant where constant='OFFICER_PARAM'");
			if (rs.next()) {
				xml = rs.getString("str_value");
			}
			if (StringUtils.isNotEmpty(xml)) {
				Document doc = null;
				HashMap map = new HashMap();
				// 构建Document对象
				doc = PubFunc.generateDom(xml);
				Element root = doc.getRootElement();
				Element postEl = root.getChild("postSet");
				map.put("setid", postEl.getAttributeValue("setid"));
				map.put("postOrg", postEl.getAttributeValue("postOrg"));
				map.put("postState", postEl.getAttributeValue("postState"));
				map.put("nbase", postEl.getAttributeValue("nbase"));
				map.put("jobname",postEl.getAttributeValue("jobname"));
				map.put("expr", postEl.getAttributeValue("expr"));
				Element mainEl = root.getChild("mainfields");
				List mainfieldList = mainEl.getChildren();
				// ArrayList mainFieldList=new ArrayList();
				HashMap mainFieldMap = new HashMap();
				HashMap feildmap = null;
				for (int i = 0; i < mainfieldList.size(); i++) {
					Element el = (Element) mainfieldList.get(i);
					feildmap = new HashMap();
					String fieldItem = el.getAttributeValue("value");
					String type = el.getAttributeValue("type");
					feildmap.put("name", el.getAttributeValue("name"));
					feildmap.put("type", type);
					feildmap.put("value", fieldItem);
					if (StringUtils.isNotEmpty(fieldItem) && "1".equals(type)) {
						FieldItem item = DataDictionary.getFieldItem(fieldItem);
						feildmap.put("desc", item != null ? item.getItemdesc() : "");
					}
					// mainFieldList.add(feildmap);
					mainFieldMap.put(el.getAttributeValue("name").toString(), feildmap);
				}
				map.put("mainfields", mainFieldMap);
				// map.put("mainfields", mainFieldList);
				feildmap = null;
				ArrayList customfields = new ArrayList();
				List customList = root.getChild("customfields").getChildren();
				for (int i = 0; i < customList.size(); i++) {
					Element el = (Element) customList.get(i);
					feildmap = new HashMap();
					String itemid = el.getAttributeValue("value");
					String type = el.getAttributeValue("type");
					feildmap.put("name", el.getAttributeValue("name"));
					feildmap.put("columnid", el.getAttributeValue("columnid"));
					feildmap.put("type", type);
					feildmap.put("value", itemid);
					if (StringUtils.isNotEmpty(itemid) && "1".equals(type)) {
						FieldItem item = DataDictionary.getFieldItem(itemid);
						feildmap.put("desc", item != null ? item.getItemdesc() : "");
					}
					customfields.add(feildmap);
				}
				map.put("customfields", customfields);
				return map;
			}
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return null;
	}
}
