package com.hjsj.hrms.module.certificate.manage.transaction;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo;
import com.hjsj.hrms.module.certificate.manage.businessobject.CertificateManageBo;
import com.hjsj.hrms.module.certificate.utils.CertificatePrivBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Date;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
/**
 * 保存导入的证书数据
 * @Title:        SaveImportCertificateDataTrans.java
 * @Description:  将导入的证书数据保存到数据库
 * @Company:      hjsj     
 * @Create time:  2018年8月2日 上午11:22:27
 * @author        chenxg
 * @version       1.0
 */
public class SaveImportCertificateDataTrans extends IBusiness {
	private int num = 0;
	private String privFields = "";
	private String fieldSetId = "";
	private String certNOItemId = "";

	@Override
	public void execute() throws GeneralException {
		String errorMessage = "";
		try {
			CertificateManageBo cmbo = new CertificateManageBo(this.userView, this.frameconn);
			// 获取系统设置的唯一性指标
			String onlyField = cmbo.getOnlyFieldItem().get(0);
			if (StringUtils.isEmpty(onlyField)) {
				errorMessage = "人员唯一性指标不能为空！";
				return;
			}
			CertificateConfigBo ccbo = new CertificateConfigBo(this.frameconn, this.userView);
			// 获取证书子集
			this.fieldSetId = ccbo.getCertSubset();
			this.certNOItemId = ccbo.getCertNOItemId();
			String certCategoryItemId = ccbo.getCertCategoryItemId();
			String certOrg = ccbo.getCertOrganization();
			String certBorrowState = ccbo.getCertBorrowState();
			ArrayList<String> userbaseList = ccbo.getCertNbase();
			if (userbaseList == null || userbaseList.size() == 0) {
				errorMessage = "人员库不能为空！";
				return;
			}

			if (StringUtils.isEmpty(this.fieldSetId)) {
				errorMessage = "证书子集不能为空！";
				return;
			}

			if (StringUtils.isEmpty(certNOItemId)) {
				errorMessage = "证书编号指标不能为空！";
				return;
			}
			
			if (StringUtils.isEmpty(certCategoryItemId)) {
				errorMessage = "证书类别指标不能为空！";
				return;
			}

			if (StringUtils.isEmpty(certOrg)) {
				errorMessage = "证书所属组织指标不能为空！";
				return;
			}
			
			if (StringUtils.isEmpty(certBorrowState)) {
				errorMessage = "证书是否借出指标不能为空！";
				return;
			}

			ArrayList privfieldList = this.userView.getPrivFieldList(this.fieldSetId);
			StringBuffer sb = new StringBuffer(",");
			for (int i = 0; i < privfieldList.size(); i++) {
				FieldItem item = (FieldItem) privfieldList.get(i);
				if (item.getPriv_status() == 2) {
					sb.append(item.getItemid() + ",");
				}
			}

			CertificatePrivBo bo = new CertificatePrivBo();
			String orgPiv = bo.getB0110(this.userView);
			String[] orgPivs = orgPiv.split("`");
			orgPiv = "";
			if(orgPivs != null && orgPivs.length > 0)
				orgPiv = orgPivs[0];
			
			if(StringUtils.isEmpty(orgPiv) || "HJSJ".equalsIgnoreCase(orgPiv)) {
				ContentDAO dao = new ContentDAO(this.frameconn);
				this.frowset = dao.search("select codeitemid from organization where codeitemid=parentid order by a0000");
				if(this.frowset.next())
					orgPiv = this.frowset.getString("codeitemid");
			}
				
				
			this.privFields = sb.toString();
			InfoUtils infoUtils = new InfoUtils();
			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			ArrayList<Object[]> mapsList = (ArrayList<Object[]>) this.userView.getHm().get("certifiacteData");
			for (Object[] maps : mapsList) {
				HashMap<String, FieldItem> fieldMap = (HashMap<String, FieldItem>) maps[0];
				ArrayList<HashMap<String, String>> valueList = (ArrayList<HashMap<String, String>>) maps[1];
				ArrayList<String> keyList = (ArrayList<String>) maps[2];
				HashMap<String, String> personMap = (HashMap<String, String>) maps[3];
				HashMap<String, ArrayList<String>> paramMap = new HashMap<String, ArrayList<String>>();
				Iterator<Entry<String, String>> entries = personMap.entrySet().iterator();
				while (entries.hasNext()) {
					Entry<String, String> entry = entries.next();
					String value = entry.getValue();
					String nbase = value.substring(0, 3);
					String a0100 = value.substring(3);
					if (paramMap.containsKey(nbase)) {
						ArrayList<String> a0100List = paramMap.get(nbase);
						String a0100s = a0100List.get(a0100List.size() - 1);
						if (a0100s.split("','").length < 1000) {
							a0100List.remove(a0100List.size() - 1);
							a0100s += "','" + a0100;
							a0100List.add(a0100s);
						} else
							a0100List.add(a0100);

						paramMap.put(nbase, a0100List);
					} else {
						ArrayList<String> a0100List = new ArrayList<String>();
						a0100List.add(a0100);
						paramMap.put(nbase, a0100List);
					}
				}
				// 存放Excle中证书信息集中人员对应的唯一标志
				String certificatePrimarykeys = (String) maps[4];

				StringBuffer columns = new StringBuffer("a0100,");
				StringBuffer columnValue = new StringBuffer("?,");
				for (String itemid : keyList) {
					FieldItem fi = fieldMap.get(itemid);
					if (fi == null || !this.privFields.contains("," + fi.getItemid() + ","))
						continue;

					columns.append(itemid + ",");
					columnValue.append("?,");
				}

				columns.append(certOrg + ",i9999,updateflag,modtime,modusername,createtime,createusername");
				columnValue.append("?,?,?,?,?,?,?");
				boolean flag = true;
				if(columns.indexOf(certBorrowState) < 0) {
					keyList.add(certBorrowState);
					flag = false;
					columns.append("," + certBorrowState);
					columnValue.append(",?");
				}
				
				keyList.add(certOrg);
				HashMap<String, String> maxI999Map = getMaxI999(paramMap);
				HashMap<String, ArrayList<ArrayList<Object>>> valuesMap = new HashMap<String, ArrayList<ArrayList<Object>>>();
				
				for (HashMap<String, String> valueMap : valueList) {
					String personId = personMap.get(valueMap.get(onlyField));
					if(StringUtils.isEmpty(personId))
					    continue;
					
					String nbase = personId.substring(0, 3);
					String a0100 = personId.substring(3);
					ArrayList<Object> paramList = new ArrayList<Object>();
					paramList.add(a0100);
					for (String itemid : keyList) {
						if (!this.privFields.contains("," + itemid + ",")
								|| itemid.equalsIgnoreCase(certOrg))
							continue;

						if(!flag && itemid.equalsIgnoreCase(certBorrowState))
							continue;
						
						FieldItem item = DataDictionary.getFieldItem(itemid);
						if ("D".equals(item.getItemtype())) {
							String value = valueMap.get(itemid);
							if (value == null || "".equals(value)) {
								paramList.add(null);
								continue;
							}

							String format = "yyyy-MM-dd";
							int length = value.length();
							if (length == 4)
								format = "yyyy";
							else if (length == 7)
								format = "yyyy-MM";
							else if (length == 13)
								format = "yyyy-MM-dd hh";
							else if (length == 16)
								format = "yyyy-MM-dd hh:mm";
							else if (length == 19)
								format = "yyyy-MM-dd hh:mm:ss";

							if (!"```".equals(value))
								paramList.add(new Timestamp(DateUtils.getDate(value, format).getTime()));
							else
								paramList.add(null);

						} else if ("N".equals(item.getItemtype())) {
							String value = (String) valueMap.get(itemid);
							if (item.isSequenceable())
								value = infoUtils.getSequenceableValue(itemid, nbase, fieldSetId, this.frameconn, a0100, "",
										idg);

							if (value == null || "".equals(value)) {
								paramList.add(null);
								continue;
							}

							if (!"```".equals(value)) {
								int dw = item.getDecimalwidth();
								if (dw == 0)
									paramList.add(Integer.parseInt(value) + "");
								else
									paramList.add(Double.valueOf(value).doubleValue() + "");

							} else
								paramList.add(null);

						} else {
							String value = (String) valueMap.get(itemid);
							if (item.isSequenceable())
								value = infoUtils.getSequenceableValue(itemid, nbase, this.fieldSetId, this.frameconn,
										a0100, "", idg);

							if (value == null || "".equals(value)) {
								paramList.add("");
								continue;
							}

							if ("A".equals(item.getItemtype()) && "0".equals(item.getCodesetid()))
								value = splitString(value, item.getItemlength());

							if (!"```".equals(value))
								paramList.add(value);
							else
								paramList.add("");
						}
					}

					paramList.add(orgPiv);
					String certNOValue = valueMap.get(certNOItemId);
					if (certificatePrimarykeys.contains("," + certNOValue + ",")) {
						paramList.add("-1");
						paramList.add("1");
					} else {
						String i9999 = maxI999Map.get(personId);
						i9999 = StringUtils.isEmpty(i9999) ? "0" : i9999;
						paramList.add(Integer.valueOf(i9999) + 1);
						paramList.add("0");
						maxI999Map.put(personId, Integer.valueOf(i9999) + 1 + "");
					}

					paramList.add(new Date(System.currentTimeMillis()));
					paramList.add(this.userView.getUserName());
					paramList.add(new Date(System.currentTimeMillis()));
					paramList.add(this.userView.getUserName());
					if(!flag)
						paramList.add("2");
					
					if (valuesMap.containsKey(nbase)) {
						ArrayList<ArrayList<Object>> valuesList = valuesMap.get(nbase);
						valuesList.add(paramList);
					} else {
						ArrayList<ArrayList<Object>> valuesList = new ArrayList<ArrayList<Object>>();
						valuesList.add(paramList);
						valuesMap.put(nbase, valuesList);
					}
				}

				Iterator<Entry<String, ArrayList<ArrayList<Object>>>> valuesEntries = valuesMap.entrySet().iterator();
				while (valuesEntries.hasNext()) {
					Entry<String, ArrayList<ArrayList<Object>>> entry = valuesEntries.next();
					ArrayList<ArrayList<Object>> valuesList = entry.getValue();
					String nbase = entry.getKey();
					String tempTableName = getTempTableName(nbase);
					StringBuffer insertSql = new StringBuffer();
					insertSql.append("insert into " + tempTableName);
					insertSql.append(" (" + columns);
					insertSql.append(") values (");
					insertSql.append(columnValue);
					insertSql.append(")");

					ArrayList<String> itemList = getItemList(keyList, nbase);
					HashMap<String, Object> tableMap = new HashMap<String, Object>();
					tableMap.put("insertSql", insertSql.toString());
					tableMap.put("values", valuesList);
					tableMap.put("nbase", nbase);
					tableMap.put("itemList", itemList);
					insertDate(tableMap);
					DbWizard db = new DbWizard(this.frameconn);
					if (db.isExistTable(tempTableName, false))
						db.dropTable(tempTableName);
				}
			}
			
			this.formHM.put("count", this.num);
		}catch (Exception e) {
			e.printStackTrace();
			errorMessage = e.getMessage();
		} finally {
			this.formHM.put("msg", errorMessage);
		}
	}

	private HashMap<String, String> getMaxI999(HashMap<String, ArrayList<String>> personMap) {
		HashMap<String, String> maxI9999Map = new HashMap<String, String>();
		if (personMap == null || personMap.size() == 0)
			return maxI9999Map;

		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			Iterator<Entry<String, ArrayList<String>>> paranEntry = personMap.entrySet().iterator();
			while (paranEntry.hasNext()) {
				Entry<String, ArrayList<String>> entry = paranEntry.next();
				String key = entry.getKey();
				ArrayList<String> values = entry.getValue();
				for (int i = 0; i < values.size(); i++) {
					String value = values.get(i);
					StringBuffer sql = new StringBuffer();
					sql.append("select a.a0100,Max(" + Sql_switcher.isnull("b.I9999", "0") + ") as i9999");
					sql.append(" from " + key + "A01 a");
					sql.append(" left join " + key + this.fieldSetId + " b");
					sql.append(" on a.a0100=b.a0100");
					sql.append(" where a.a0100 in ('" + value + "')");
					sql.append(" group by a.a0100");

					rs = dao.search(sql.toString());

					while (rs.next()) {
						String a0100 = rs.getString("a0100");
						String i9999 = rs.getString("i9999");
						maxI9999Map.put(key + a0100, i9999);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}

		return maxI9999Map;
	}

	/**
	 * 获取导入数据的指标并创建临时表
	 * 
	 * @param valueMap
	 *            导入的数据
	 * @param nbase
	 *            人员库
	 * @param fieldsetId
	 *            要导入的子集
	 * @return
	 */
	private ArrayList<String> getItemList(ArrayList<String> fieldList, String nbase) {
		ArrayList<String> list = new ArrayList<String>();
		if (fieldList == null || fieldList.size() < 1 || StringUtils.isEmpty(nbase)
				|| StringUtils.isEmpty(this.fieldSetId))
			return list;

		Statement stmt = null;
		try {
			String temTableName = getTempTableName(nbase);
			DbWizard db = new DbWizard(this.frameconn);
			if (db.isExistTable(temTableName, false))
				db.dropTable(temTableName);

			StringBuffer tempSql = new StringBuffer();
			tempSql.append("Create Table ");
			tempSql.append(temTableName);
			tempSql.append(" (a0100 varchar(8),");
			for (int i = 0; i < fieldList.size(); i++) {
				String itemid = fieldList.get(i);
				// 没有此指标写权限
				if (!this.userView.isAdmin() && this.privFields.indexOf(itemid) == -1)
					continue;

				FieldItem fi = DataDictionary.getFieldItem(itemid, this.fieldSetId);
				if (fi == null || "0".equalsIgnoreCase(fi.getUseflag()))
					continue;

				list.add(itemid);

				tempSql.append(fi.getItemid());
				if ("A".equalsIgnoreCase(fi.getItemtype())) {
					tempSql.append(" varchar(" + fi.getItemlength() + "),");
				} else if ("N".equalsIgnoreCase(fi.getItemtype())) {
					tempSql.append(" numeric(" + (fi.getItemlength() + fi.getDecimalwidth()) + ","
							+ fi.getDecimalwidth() + "),");
				} else if ("D".equalsIgnoreCase(fi.getItemtype())) {
					if (Sql_switcher.searchDbServer() == Constant.MSSQL)
						tempSql.append(" Datetime,");
					else
						tempSql.append(" Date,");
				} else if ("M".equalsIgnoreCase(fi.getItemtype())) {
					if (Sql_switcher.searchDbServer() == Constant.MSSQL){
						tempSql.append(" text,");
					} else if(Sql_switcher.searchDbServerFlag()==Constant.ORACEL) {
						tempSql.append(" clob,");
					}else if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
						//zhangh 解决达梦数据库报错：试图在blob或者clob列上排序或比较的问题
						tempSql.append(" varchar(4000),");
					}
				}
			}

			// 用于区分是新增还是更新的指标
			tempSql.append("updateflag int,");
			tempSql.append("i9999 int,");

			if (Sql_switcher.searchDbServer() == Constant.MSSQL)
				tempSql.append(" createtime dateTime,modtime  dateTime,");
			else
				tempSql.append(" createtime date,modtime  date,");

			tempSql.append("createusername  varchar(50),modusername varchar(50)");

			tempSql.append(")");

			stmt = this.frameconn.createStatement();
			stmt.execute(tempSql.toString());
		} catch (Exception e) {
			this.getFormHM().put("message", "导入数据出错！");
			e.printStackTrace();
		} finally {
			PubFunc.closeIoResource(stmt);
		}
		return list;
	}

	/**
	 * 往临时表中插入数据
	 * 
	 * @param map
	 *            插入数据的sql语句、数据、指标、表名
	 */
	private void insertDate(HashMap<String, Object> map) {
		try {
			String sql = (String) map.get("insertSql");
			ArrayList<ArrayList> valuelist = (ArrayList<ArrayList>) map.get("values");
			if (valuelist == null || valuelist.size() < 1)
				return;

			ContentDAO dao = new ContentDAO(this.frameconn);
			dao.batchInsert(sql, valuelist);

			ArrayList<String> itemList = (ArrayList<String>) map.get("itemList");
			String nbase = (String) map.get("nbase");

			insertInfo(nbase, itemList);

		} catch (Exception e) {
			this.getFormHM().put("message", "导入数据出错！");
			e.printStackTrace();
		}
	}

	/**
	 * 将临时表的数据更到对应的子集中
	 * 
	 * @param tableName
	 *            子集名称
	 * @param itemList
	 *            指标
	 */
	private void insertInfo(String nbase, ArrayList<String> itemList) {
		String fieldsetDesc = "";
		RowSet rs = null;
		String tableName = nbase + this.fieldSetId;
		try {
			FieldSet fieldset = DataDictionary.getFieldSetVo(this.fieldSetId);
			fieldsetDesc = fieldset.getFieldsetdesc();

			String tempTable = getTempTableName(nbase);
			// 执行更新并且是非兼职的人员的指标
			StringBuffer updateColumn = new StringBuffer();

			StringBuffer selectColumn = new StringBuffer();
			for (int i = 0; i < itemList.size(); i++) {
				String itemid = itemList.get(i);
				// 获取有读写权限的指标
				if (!this.userView.isSuper_admin() && this.privFields.indexOf(itemid) == -1)
					continue;

				updateColumn.append("a." + itemid + ",");
				selectColumn.append(itemid + ",");

			}

			updateColumn.append("a.modtime,a.modusername");
			selectColumn.append("modtime,modusername");

			ArrayList<String> sqlList = new ArrayList<String>();
			StringBuffer updateTime = new StringBuffer();
			if (Sql_switcher.searchDbServer() != Constant.MSSQL) {
				StringBuffer updateSql = new StringBuffer();
				updateSql.append("update " + tableName + " a set (state,");
				updateSql.append(updateColumn);
				updateSql.append(")=(select '3' state,");
				updateSql.append(selectColumn);
				updateSql.append(" from " + tempTable);
				updateSql.append(" b where b.updateflag=1");
				updateSql.append(" and a." + this.certNOItemId + "= b." + this.certNOItemId);
				updateSql.append(" and a.a0100=b.a0100)");
				updateSql.append(" where exists (select a0100 from ");
				updateSql.append(tempTable);
				updateSql.append(" c where a.a0100=c.a0100 and c.updateflag=1");
				updateSql.append(" and a." + this.certNOItemId + "=c." + this.certNOItemId);
				updateSql.append(")");
				sqlList.add(updateSql.toString());

				// 拼接执行插入操作的sql语句
				StringBuffer insertSql = new StringBuffer();
				insertSql.append("insert into ");
				insertSql.append(tableName);
				insertSql.append(" a (state,");
				insertSql.append(updateColumn);
				insertSql.append(",a.createtime,a.createusername,a.a0100");
				insertSql.append(",a.i9999");
				insertSql.append(") select '3' state,");
				insertSql.append(selectColumn);
				insertSql.append(",createtime,createusername,a0100");
				insertSql.append(",i9999");
				insertSql.append(" from " + tempTable);
				insertSql.append(" b where b.updateflag=0");
				sqlList.add(insertSql.toString());

			} else {
				StringBuffer mssqlColumn = new StringBuffer();
				StringBuffer mssqlIsPartColumn = new StringBuffer();
				StringBuffer mssqlInserColumn = new StringBuffer();
				for (int i = 0; i < itemList.size(); i++) {
					String itemid = itemList.get(i);
					// 获取有读写权限的指标
					if (!this.userView.isSuper_admin() && this.privFields.indexOf(itemid) == -1)
						continue;

					mssqlColumn.append(tableName + "." + itemid + "=b." + itemid + ",");
					mssqlInserColumn.append(itemid + ",");

				}

				mssqlColumn.append(tableName + ".modtime=b.modtime,");
				mssqlColumn.append(tableName + ".modusername=b.modusername");
				mssqlIsPartColumn.append(tableName + ".modtime=b.modtime,");
				mssqlIsPartColumn.append(tableName + ".modusername=b.modusername");
				mssqlInserColumn.append("modtime,modusername");

				StringBuffer updateSql = new StringBuffer();
				updateSql.append("update " + tableName + " set state='3',");
				updateSql.append(mssqlColumn);
				updateSql.append(" from " + tempTable);
				updateSql.append(" b where b.updateflag=1");
				updateSql.append(" and " + tableName + "." + this.certNOItemId + "= b." + this.certNOItemId);

				updateSql.append(" and " + tableName + ".a0100=b.a0100");
				updateSql.append(" and exists (select a0100 from ");
				updateSql.append(tempTable);
				updateSql.append(" c where " + tableName + ".a0100=c.a0100 and c.updateflag=1");
				updateSql.append(" and " + tableName + "." + this.certNOItemId + "=c." + this.certNOItemId);
				updateSql.append(")");
				sqlList.add(updateSql.toString());

				// 拼接执行插入操作的sql语句
				StringBuffer insertSql = new StringBuffer();
				insertSql.append("insert into ");
				insertSql.append(tableName);
				insertSql.append(" (state,");
				insertSql.append(mssqlInserColumn);
				insertSql.append(",createtime,createusername,a0100");
				insertSql.append(",i9999");
				insertSql.append(") select '3' state,");
				insertSql.append(mssqlInserColumn);
				insertSql.append(",createtime,createusername,a0100");
				insertSql.append(",i9999");
				insertSql.append(" from " + tempTable);
				insertSql.append(" b where b.updateflag=0");
				sqlList.add(insertSql.toString());
			}

			ContentDAO dao = new ContentDAO(this.frameconn);
			int updateCount = 0;
			for (String sql : sqlList) {
				String selectSql = "";
				if (sql.startsWith("insert")) {
					selectSql = sql.substring(sql.indexOf("from"));
					selectSql = "select count(1) recordNum " + selectSql;
				} else {
					selectSql = sql.substring(sql.indexOf("exists"));
					selectSql = selectSql.replace(tableName + ".", "a.");
					selectSql = "select count(1) recordNum from " + tableName + " a where " + selectSql;
				}

				rs = dao.search(selectSql);
				if (rs.next()) {
					int count = rs.getInt("recordNum");
					updateCount = updateCount + count;
				}
			}

			int[] nums = dao.batchUpdate(sqlList);
			// 此条sql只是更新新增人员数据的创建时间和创建帐号，因此不计算到更新成功的记录数量中
			if (StringUtils.isNotEmpty(updateTime.toString()))
				dao.update(updateTime.toString());

			int countRow = 0;
			for (int i = 0; i < nums.length; i++)
				countRow = countRow + nums[i];
			// 低版本的jdbc中update方法返回的值有问题，当更新语句返回的记录条数为0时，即认为查询出的记录全部更新完成
			if (0 == countRow)
				this.num += updateCount;
			else
				this.num += countRow;

		} catch (Exception e) {
			String error = e.getMessage();
			String message = fieldsetDesc + "子集导入数据出错";
			if (error.indexOf("ORA-01427") > -1)
				message += "，请检查模板中是否有关键指标重复的数据";

			this.getFormHM().put("message", message + "！");
			error = error.substring(error.indexOf(":") + 2).replace("\n", "");
			this.getFormHM().put("error", error);
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
	}

	/**
	 * 生成临时表名
	 * 
	 * @param tableName
	 *            人员库
	 * @return
	 */
	private String getTempTableName(String nbase) {
		String tempTableName = "T_" + this.userView.getUserName() + nbase + this.fieldSetId;
		if (Sql_switcher.searchDbServer() != Constant.MSSQL)
			tempTableName = "Temp_" + this.userView.getUserName() + nbase + this.fieldSetId;

		return tempTableName;
	}

	private String splitString(String source, int len) {
		byte[] bytes = source.getBytes();
		int bytelen = bytes.length;
		int j = 0;
		int rlen = 0;
		if (bytelen <= len)
			return source;

		for (int i = 0; i < len; ++i) {
			if (bytes[i] < 0)
				++j;
		}

		if (j % 2 == 1)
			rlen = len - 1;
		else
			rlen = len;

		byte[] target = new byte[rlen];
		System.arraycopy(bytes, 0, target, 0, rlen);
		String dd = new String(target);
		return dd;
	}
}
