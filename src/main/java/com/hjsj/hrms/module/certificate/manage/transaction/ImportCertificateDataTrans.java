package com.hjsj.hrms.module.certificate.manage.transaction;

import com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo;
import com.hjsj.hrms.module.certificate.manage.businessobject.CertificateManageBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 证书导入数据校验
 * @Title:        ImportCertificateDataTrans.java
 * @Description:  用于校验导入的证书数据的交易类
 * @Company:      hjsj     
 * @Create time:  2018年8月2日 上午11:20:28
 * @author        chenxg
 * @version       1.0
 */
public class ImportCertificateDataTrans extends IBusiness {

	private StringBuffer onlyValues = new StringBuffer("'");
	// 模板中要导入的且在系统中存在的人员的唯一性指标和姓名
	private String onlyFieldValue = "";
	// 保存模板中关键指标对应的记录数量
	HashMap<String, ArrayList<String>> msgMap = new HashMap<String, ArrayList<String>>();
	// 证书子集设置的指标
	private StringBuffer certificeteItems = new StringBuffer();
	String errorMessage = "";

	@Override
	public void execute() throws GeneralException {
		String isupdate = "0";
		StringBuffer msgJson = new StringBuffer();
		try {
			// 上传组件 vfs改造 
            String fileid = (String)this.getFormHM().get("fileid");
            
			CertificateManageBo cmbo = new CertificateManageBo(this.userView, this.frameconn);
			// 获取系统设置的唯一性指标
			String onlyField = cmbo.getOnlyFieldItem().get(0);
			if (StringUtils.isEmpty(onlyField)) {
				this.errorMessage = "人员唯一性指标不能为空！";
				return;
			}

			CertificateConfigBo ccbo = new CertificateConfigBo(this.frameconn, this.userView);
			// 获取证书子集
			String fieldSetId = ccbo.getCertSubset();
			String certNOItemId = ccbo.getCertNOItemId();
			String certCategoryItemId = ccbo.getCertCategoryItemId();
			ArrayList<String> userbaseList = ccbo.getCertNbase();
            String certStatusitemId = ccbo.getCertStatus();
            // 证书信息集证书名称
            String certNameItemId = ccbo.getCertName();
            String certOrgItemId = ccbo.getCertOrganization();
            String certBorrowStateItemId = ccbo.getCertBorrowState();
            String certCategoryCode = ccbo.getCertCategoryCode();
            if (StringUtils.isEmpty(fieldSetId)) {
                this.errorMessage = "证书子集不能为空！";
                return;
            }

            if (StringUtils.isEmpty(certNOItemId)) {
                this.errorMessage = "请设置证书编号指标！";
                return;
            }

            certificeteItems.append("'" + certNOItemId + "',");
            if (StringUtils.isEmpty(certStatusitemId)) {
                this.errorMessage = "请设置证书状态指标！";
                return;
            }

            certificeteItems.append("'" + certStatusitemId + "',");
            if (StringUtils.isEmpty(certCategoryItemId)) {
                this.errorMessage = "请设置证书类别指标！";
                return;
            }

            certificeteItems.append("'" + certCategoryItemId + "',");
            if (StringUtils.isEmpty(certNameItemId)) {
                this.errorMessage = "请设置证书名称指标！";
                return;
            }
            
            certificeteItems.append("'" + certNameItemId + "',");
            if (StringUtils.isEmpty(certOrgItemId)) {
                this.errorMessage = "请设置证书归属机构指标！";
                return;
            }
            
            certificeteItems.append("'" + certOrgItemId + "',");
            if (StringUtils.isEmpty(certBorrowStateItemId)) {
                this.errorMessage = "请设置证书是否借阅指标！";
                return;
            }
            
            certificeteItems.append("'" + certBorrowStateItemId + "',");
            if (StringUtils.isEmpty(certCategoryCode)) {
                this.errorMessage = "请设置证书类别代码类！";
                return;
            }
            
            certificeteItems.append("'" + certCategoryCode + "',");
			if (userbaseList == null || userbaseList.size() == 0) {
				this.errorMessage = "人员库不能为空！";
				return;
			}
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			ArrayList<Object[]> mapsList = readExcel(fileid, msgJson, onlyField, fieldSetId, certNOItemId, certCategoryItemId, dao);
			if(mapsList == null)
			    return;

			for (int num = 0; num < mapsList.size(); num++) {
				Object[] maps = (Object[]) mapsList.get(num);

				HashMap<String, FieldItem> fieldMap = (HashMap<String, FieldItem>) maps[0];
				ArrayList<HashMap<String, String>> valueList = (ArrayList<HashMap<String, String>>) maps[1];
				ArrayList<String> keyList = (ArrayList<String>) maps[2];
				HashMap<String, String> personMap = (HashMap<String, String>) maps[3];
				// 存放Excle中证书信息集中人员对应的唯一标志
				String certificatePrimarykeys = (String) maps[4];

				StringBuffer sql = new StringBuffer();
				if (keyList.size() < 1)
					continue;

				if(!keyList.contains(certNOItemId) || !keyList.contains(certStatusitemId) || !keyList.contains(certCategoryItemId) 
						|| !keyList.contains(certNameItemId)) {
					StringBuffer fieldDescs = new StringBuffer();
					FieldItem fi = DataDictionary.getFieldItem(certNOItemId);
					if(fi != null) 
						fieldDescs.append(fi.getItemdesc());
					
					fi = DataDictionary.getFieldItem(certStatusitemId);
					if(fi != null) 
						fieldDescs.append(fi.getItemdesc() + "、");
					
					fi = DataDictionary.getFieldItem(certCategoryItemId);
					if(fi != null) 
						fieldDescs.append(fi.getItemdesc() + "、");
					
					fi = DataDictionary.getFieldItem(certNameItemId);
					if(fi != null) 
						fieldDescs.append(fi.getItemdesc() + "、");
					
					if(fieldDescs.toString().endsWith("、"))
						fieldDescs.setLength(fieldDescs.length() - 1);
					
					this.errorMessage = ResourceFactory.getProperty("certificate.info.import.error.template");
					this.errorMessage = this.errorMessage.replace("{0}", fieldDescs.toString());
					return;
				}
					
				// 检查代码型的字段从excel中读取的codeitemdesc值是否在库中有对应的codeitemid
				for (int n = 0; n < keyList.size(); n++) {
					String key = (String) keyList.get(n);
					FieldItem item = (FieldItem) fieldMap.get(key);
					if (item == null)
						continue;
					String itemid = item.getItemid();
					if ((item.getCodesetid() != null && !"".equals(item.getCodesetid())
							&& !"0".equals(item.getCodesetid())) && "A".equalsIgnoreCase(item.getItemtype())) {
						if ("UN".equalsIgnoreCase(item.getCodesetid()) || "UM".equalsIgnoreCase(item.getCodesetid())
								|| "@K".equalsIgnoreCase(item.getCodesetid())) {
							sql.setLength(0);
							sql.append("select codeitemdesc,codeitemid from organization where upper(codesetid)='");
							sql.append(item.getCodesetid().toUpperCase());
							sql.append("' and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd")));
							sql.append(" between start_date and end_date");
						} else {
							sql.setLength(0);
							sql.append("select codeitemdesc,codeitemid from codeitem where upper(codesetid)='");
							sql.append(item.getCodesetid().toUpperCase() + "'");
						}

						this.frowset = dao.search(sql.toString());
						HashMap<String, String> codemap = new HashMap<String, String>();
						while (this.frowset.next()) {
							codemap.put(this.frowset.getString("codeitemid").trim(),
							        this.frowset.getString("codeitemdesc").trim());
						}

						HashMap<String, String> leafItemMmaps = new HashMap<String, String>();
						DbWizard db = new DbWizard(this.frameconn);
						if (db.isExistField("codeset", "leaf_node", false)) {
							this.frowset = dao.search("select leaf_node from codeset where upper(codesetid)='"
									+ item.getCodesetid().toUpperCase() + "'");
							if (this.frowset.next()) {
								String leafNode = this.frowset.getString("leaf_node");
								if ("1".equals(leafNode)) {
									this.frowset = dao.search(sql + " and codeitemid=childid");
									while (this.frowset.next()) {
										leafItemMmaps.put(this.frowset.getString("codeitemid").trim(),
												this.frowset.getString("codeitemdesc").trim());
									}
								}
							}
						}

						for (int m = 0; m < valueList.size(); m++) {
							HashMap<String, String> valuemap = (HashMap<String, String>) valueList.get(m);
							String primaryKeyValue = (String) valuemap.get(onlyField);
							String value = (String) valuemap.get(itemid);
							value = value == null ? "" : value;
							if (!codemap.containsKey(value) && !codemap.containsValue(value) && !"".equals(value)) {
							    
								valuemap.put(itemid, "```");
								if (msgMap.containsKey(primaryKeyValue)) {
									ArrayList<String> msgList = msgMap.get(primaryKeyValue);
									msgList.add((msgList.size() + 1) + ".&nbsp;[" + item.getItemdesc() + "]"
											+ ResourceFactory.getProperty("workbench.info.import.error.codenol") + value
											+ ResourceFactory.getProperty("workbench.info.import.error.codenor"));

									msgMap.put(primaryKeyValue, msgList);
								} else {
									ArrayList<String> msgList = new ArrayList<String>();
									msgList.add("1.&nbsp;[" + item.getItemdesc() + "]"
											+ ResourceFactory.getProperty("workbench.info.import.error.codenol") + value
											+ ResourceFactory.getProperty("workbench.info.import.error.codenor"));

									msgMap.put(primaryKeyValue, msgList);
								}

							} else {
								if (!"".equals(value)) {
									String codeitemDesc = codemap.get(value);
									if (StringUtils.isEmpty(codeitemDesc))
									    codeitemDesc = value;

									if (!leafItemMmaps.isEmpty() && !leafItemMmaps.containsKey(value)) {
										valuemap.put(itemid, "```");
										StringBuffer msg = new StringBuffer();
										msg.append(".&nbsp;[" + item.getItemdesc() + "]");
										msg.append("仅可选择末端代码，");
										msg.append("[" + codeitemDesc + "]不可选择！");
										if (msgMap.containsKey(primaryKeyValue)) {
											ArrayList<String> msgList = msgMap.get(primaryKeyValue);
											msgList.add((msgList.size() + 1) + msg.toString());

											msgMap.put(primaryKeyValue, msgList);
										} else {
											ArrayList<String> msgList = new ArrayList<String>();
											msgList.add("1" + msg.toString());

											msgMap.put(primaryKeyValue, msgList);
										}
									} else
										valuemap.put(itemid, value);
								}
							}
						}
					} else if (item != null && item.isSequenceable() && "A".equalsIgnoreCase(item.getItemtype())) {
						for (int m = 0; m < valueList.size(); m++) {
							HashMap<String, String> valuemap = (HashMap<String, String>) valueList.get(m);
							String value = (String) valuemap.get(itemid);
							if ((value == null || value.length() == 0)) {
								IDGenerator idg = new IDGenerator(2, this.frameconn);
								String idd = idg.getId(item.getSequencename());
								value = idd;
								valuemap.put(itemid, value);
							}
						}
					}
				}

				HashMap<String, String> alldbmap = new HashMap<String, String>();
				HashMap<String, String> allmap = new HashMap<String, String>();
				sql.setLength(0);
				sql.append("select pre,dbname from dbname order by dbid");
				this.frowset = dao.search(sql.toString());
				ArrayList<String> onlyFieldList = cmbo.getOnlyFieldItem();
				String setdb = onlyFieldList.get(1);
				while (this.frowset.next()) {
					allmap.put(this.frowset.getString("pre"), this.frowset.getString("dbname"));
					if (!"all".equals(setdb.toString())
							&& setdb.indexOf(this.frowset.getString("pre").toLowerCase()) == -1)
						continue;

					alldbmap.put(this.frowset.getString("pre"), this.frowset.getString("dbname"));
				}

				ArrayList<String> prList = new ArrayList<String>();
				String[] keyValues = onlyValues.toString().substring(1).split("','");
				if (keyValues.length > 500) {
					StringBuffer sb = new StringBuffer(",");
					int n = 0;
					boolean flag = false;
					for (int i = 0; i < keyValues.length; i++) {
						if ("".equals(keyValues[i]))
							continue;

						flag = false;
						sb.append(keyValues[i] + ",");
						n++;
						if (n > 498) {
							flag = true;
							prList.add(sb.toString());
							sb = new StringBuffer();
							n = 0;
						}
					}

					if (!flag)
						prList.add(sb.toString());

				} else {
					prList.add(onlyValues.toString());
				}
				// 需要校验唯一性的人员库（非当前选择的人员库）中已存在的人员数据
				StringBuffer othDbnamePerson = new StringBuffer();
				String nameField = "a0101,";
				if ("a0101".equalsIgnoreCase(onlyField))
					nameField = "";

				ArrayList<String> repeatValues = new ArrayList<String>();
				for (int n = 0; n < userbaseList.size(); n++) {
					String userbase = userbaseList.get(n);
					sql.setLength(0);
					sql.append("select a0100," + nameField + onlyField + " from ");
					sql.append(userbase + "A01");
					if (Sql_switcher.searchDbServer() == Constant.MSSQL)
						sql.append(" where CHARINDEX(upper(" + onlyField + "), ?)>0");
					else
						sql.append(" where INSTR(?, upper(" + onlyField + "))>0");

					this.frowset = dao.search(sql.toString(), prList);
					while (this.frowset.next()) {
						String a0100 = this.frowset.getString("a0100");
						String primaryvalue = this.frowset.getString(onlyField);
						String dbname = alldbmap.get(userbase);
						if (personMap.containsKey(primaryvalue)) {
							if (!othDbnamePerson.toString().contains(primaryvalue))
								othDbnamePerson.append(primaryvalue);

							if (msgMap.containsKey(primaryvalue)) {
								ArrayList<String> msgList = msgMap.get(primaryvalue);
								msgList.add((msgList.size() + 1) + ".&nbsp;" + dbname + "中此人已存在！");

								msgMap.put(primaryvalue, msgList);
							} else {
								ArrayList<String> msgList = new ArrayList<String>();
								msgList.add("1.&nbsp;" + dbname + "中此人已存在！");
								String othNbase = personMap.get(primaryvalue).substring(0, 3);
								String othNbaseName = alldbmap.get(othNbase);
								msgList.add("2.&nbsp;" + othNbaseName + "中此人已存在！");
								msgMap.put(primaryvalue, msgList);
							}

							repeatValues.add(primaryvalue);
						} else {
							personMap.put(primaryvalue, userbase + a0100);
						}

					}
					// 去除在多个人员库中存在的唯一性指标的值
					for (String value : repeatValues)
						personMap.remove(value);

				}

				// 如果在其他需要校验唯一性的人员库中已存在的人员数量和需要导入的人员数量相同则在提示信息页面不显示导入按钮和记录更新选择框
				if (StringUtils.isNotEmpty(othDbnamePerson.toString())) {
					String[] existA0100s = othDbnamePerson.toString().trim().split(",");
					if (keyValues.length <= existA0100s.length)
						// 需要导入的主键值与已存在的主键个数一样的时候不显示导入按钮
						this.getFormHM().put("RepeatPrimaryKey", "1");
				}

				sql.setLength(0);
				for (int n = 0; n < userbaseList.size(); n++) {
					String nbase = userbaseList.get(n);
					if (n > 0)
						sql.append(" union ");

					sql.append(" select a0101," + onlyField);
					sql.append(" from " + nbase + "A01");
					sql.append(" where " + onlyField + "=?");
				}

				StringBuffer notImportData = new StringBuffer();
				for (int i = 0; i < valueList.size(); i++) {
					HashMap<String, String> valueMap = valueList.get(i);
					String onlyFieldValue = valueMap.get(onlyField);
					if (!personMap.containsKey(onlyFieldValue)) {
					    if(!notImportData.toString().contains("," + onlyFieldValue.toUpperCase() + ",")) {
					        notImportData.append("," + onlyFieldValue.toUpperCase() + ",");
					        if (msgMap.containsKey(onlyFieldValue)) {
					            ArrayList<String> msgList = msgMap.get(onlyFieldValue);
					            msgList.add((msgList.size() + 1) + ".&nbsp;此人不在权限范围内或不存在！");
					            msgMap.put(onlyFieldValue, msgList);
					        } else {
					            ArrayList<String> msgList = new ArrayList<String>();
					            msgList.add("1.&nbsp;此人不在权限范围内或不存在！");
					            msgMap.put(onlyFieldValue, msgList);
					        }
					    }
					    
					    valueList.remove(i);
						continue;
					}
					
					String a0101 = valueMap.get("a0101");
					String certificateClass = valueMap.get(certCategoryItemId);
					ArrayList<String> paramList = new ArrayList<String>();
					for(int m = 0; m < userbaseList.size(); m++) 
						paramList.add(valueMap.get(onlyField));
					
					this.frowset = dao.search(sql.toString(), paramList);
					if (this.frowset.next()) {
						String personName = this.frowset.getString("a0101");
						if (!personName.equalsIgnoreCase(a0101)) {
							if (msgMap.containsKey(onlyFieldValue)) {
								ArrayList<String> msgList = msgMap.get(onlyFieldValue);
								msgList.add((msgList.size() + 1) + ".&nbsp;模板中的姓名与数据库中的姓名不符，不允许导入！");

								msgMap.put(onlyFieldValue, msgList);
							} else {
								ArrayList<String> msgList = new ArrayList<String>();
								msgList.add("1.&nbsp;模板中的姓名与数据库中的姓名不符，不允许导入！");
								msgMap.put(onlyFieldValue, msgList);
							}

							personMap.remove(onlyFieldValue);
							continue;
						}
					} else {
						if (msgMap.containsKey(onlyFieldValue)) {
							ArrayList<String> msgList = msgMap.get(onlyFieldValue);
							msgList.add((msgList.size() + 1) + ".&nbsp;模板中的人员不存在，不允许导入！");

							msgMap.put(onlyFieldValue, msgList);
						} else {
							ArrayList<String> msgList = new ArrayList<String>();
							msgList.add("1.&nbsp;模板中的人员不存在，不允许导入！");
							msgMap.put(onlyFieldValue, msgList);
						}

						personMap.remove(onlyFieldValue);
						continue;
					}

					String personId = personMap.get(onlyFieldValue);
					String nbase = personId.substring(0, 3);
					String a0100 = personId.substring(3);
					String certNO = valueMap.get(certNOItemId);
					paramList.clear();
					paramList.add(a0100);
					paramList.add(certNO);
					StringBuffer certSql = new StringBuffer();
					certSql.append("select " + certCategoryItemId);
					certSql.append(" from " + nbase + fieldSetId);
					certSql.append(" where a0100=? and " + certNOItemId + "=?");
					String date = DateUtils.format(new Date(), "yyyy-MM-dd");
					StringBuffer where = new StringBuffer();
					where.append(" and " + certCategoryItemId);
					where.append(" in (select codeitemid from codeitem");
					where.append(" where codesetid='" + certCategoryCode + "'");
					where.append(" and " + Sql_switcher.isnull(Sql_switcher.dateToChar("end_date", "yyyy-MM-dd"), "'9999-12-31'"));
		            where.append(">='" + date + "')");
					this.frowset = dao.search(certSql.toString(), paramList);
					while (this.frowset.next()) {
						String classification = this.frowset.getString(certCategoryItemId);
						if (!classification.equalsIgnoreCase(certificateClass)
								&& certificatePrimarykeys.toString().contains("," + certNO + ",")) {
							if (msgMap.containsKey(onlyFieldValue)) {
								ArrayList<String> msgList = msgMap.get(onlyFieldValue);
								msgList.add((msgList.size() + 1) + ".&nbsp;模板中的编号[" + certNO + "]在不同的分类下同时存在！");

								msgMap.put(onlyFieldValue, msgList);
							} else {
								ArrayList<String> msgList = new ArrayList<String>();
								msgList.add("1.&nbsp;模板中的姓名与数据库中的姓名不符，不允许导入！");
								msgMap.put(onlyFieldValue, msgList);
							}

							certificatePrimarykeys = certificatePrimarykeys.replace("," + certNO + ",", ",");
							break;
						} else
							certificatePrimarykeys += certNO + ",";

					}
					
					 maps[4] = certificatePrimarykeys;
				}
			}

			if (this.msgMap != null && this.msgMap.size() > 0) {
				msgJson.append("[");
				Iterator<Entry<String, ArrayList<String>>> it = this.msgMap.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, ArrayList<String>> entry = it.next();
					String primaryKey = entry.getKey();
					msgJson.append("{primaryKey:\"" + primaryKey + "\",message:\"");
					ArrayList<String> valueList = entry.getValue();
					for (int i = 0; i < valueList.size(); i++) {
						String value = valueList.get(i);
						if (i > 0)
							msgJson.append("<br>");

						msgJson.append(value);
					}

					msgJson.append("\"},");
				}

				if (msgJson.toString().endsWith(","))
					msgJson.setLength(msgJson.length() - 1);

				msgJson.append("]");
			}

			this.userView.getHm().put("certifiacteData", mapsList);
		} catch (Exception e) {
			e.printStackTrace();
			this.errorMessage = e.toString();
		} finally {
			this.getFormHM().put("errorMessage", this.errorMessage);
			this.getFormHM().put("msgJson", msgJson.toString());
			this.getFormHM().put("isupdate", isupdate);
		}
	}

	/**
	 * 读取excel数据放入集合对象
	 * 
	 * @param file
	 * @param dao
	 * @param hashMap
	 * @return
	 * @throws Exception
	 */
	private ArrayList<Object[]> readExcel(String fileid, StringBuffer json, String onlyField, String fieldSetId,
			String certNOItemId, String certCategoryItemId, ContentDAO dao) throws Exception {

		ArrayList<Object[]> mapsList = new ArrayList<Object[]>();
		Workbook owb = null;
		Sheet osheet = null;
		InputStream ism = null;
		try {
			ism = VfsService.getFile(fileid);
			owb = WorkbookFactory.create(ism);
			ArrayList<HashMap<String, String>> valueList = new ArrayList<HashMap<String, String>>();
			// key=//当前指标值：所在cell列数 value=FieldItem
			HashMap<String, FieldItem> fieldMap = new HashMap<String, FieldItem>();
			HashMap<String, Integer> fieldIdex = new HashMap<String, Integer>();
			ArrayList<String> keyList = new ArrayList<String>();
			HashMap<String, String> personMap = new HashMap<String, String>();
			// 存放Excle中证书信息集中人员对应的唯一标志
			String certificatePrimarykeys = ",";
			Object[] maps = { fieldMap, valueList, keyList, personMap, certificatePrimarykeys };
			osheet = owb.getSheetAt(0);
			Row orow = osheet.getRow(0);// 第一行标题
			if (orow == null) {
			    this.errorMessage = ResourceFactory.getProperty("workbench.info.import.error.excel");
			    return null;
			}
			
			// 总列数
			int cols = orow.getPhysicalNumberOfCells();
			// 总行数
			int rows = osheet.getPhysicalNumberOfRows();
			// 遍历列
			for (int c = 0; c < cols; c++) {
			    String itemid = "";
			    Cell cell = orow.getCell(c);
			    if (cell != null) {
			        if(cell.getCellComment() == null)
			            break;
			        
			        itemid = cell.getCellComment().getString().getString().toLowerCase();
			    }
			    
			    if ("".equals(itemid))
			        break;
			    
			    FieldItem item = DataDictionary.getFieldItem(itemid);
			    if (item == null)
			        this.errorMessage = ResourceFactory.getProperty("workbench.info.import.error.excel");
			    
			    fieldIdex.put(itemid, new Integer(c));
			    // 不是系统唯一性指标和证书子集的指标进行解析
			    if (!"a0101".equalsIgnoreCase(itemid) && !fieldSetId.equalsIgnoreCase(item.getFieldsetid())
			            && !onlyField.equalsIgnoreCase(item.getItemid()))
			        continue;
			    
			    // 当前指标值：所在cell列数
			    fieldMap.put(itemid, item);
			    keyList.add(itemid);
			}
			
			if(keyList == null || keyList.size() < 1 ||!keyList.contains(onlyField) 
			        || !keyList.get(0).equalsIgnoreCase(onlyField)) {
			    this.errorMessage = ResourceFactory.getProperty("workbench.info.import.error.excel");
			    return null;
			}
			
			cols = keyList.size();
			HashMap<String, Integer> certificateMap = new HashMap<String, Integer>();
			HashMap<String, Integer> cFMap = new HashMap<String, Integer>();
			for (int j = 1; j < rows; j++) {
			    orow = osheet.getRow(j);
			    if (orow == null)
			        continue;
			    
			    // key=指标itemid value=值
			    HashMap<String, String> valueMap = new HashMap<String, String>();
			    this.onlyFieldValue = "";
			    String certNOValue = "";
			    String certCategoryValue = "";
			    boolean isAdd = true;
			    for (int n = 0; n < cols; n++) {
			        String key = (String) keyList.get(n);
			        FieldItem item = (FieldItem) fieldMap.get(key);
			        if (item == null)
			            continue;
			        
			        int index = ((Integer) fieldIdex.get(key)).intValue();
			        Cell cell = orow.getCell(index);
			        if (cell == null) {
			            if(index == 0)
			                break;
			            else {
			            	if(item.isFillable() || certificeteItems.toString().contains("'" + item.getItemid() + "'")) {
					            isAdd = false;
					            isFillableDesc(j, item);
					        }
			            	
			            	continue;
			            }
			        }
			        
			        String value = "";
			        switch (cell.getCellTypeEnum()) {
			        case FORMULA: {
			            value = checkData(item, cell, json, j);
			            break;
			        }
			        case STRING: {
			            value = checkData(item, cell, json, j);
			            break;
			        }
			        case NUMERIC: {
			            value = checkData(item, cell, json, j);
			            break;
			        }
			        default: {
			            value = checkData(item, cell, json, j);
			        }
			        }
			        
			        if((StringUtils.isEmpty(value) || "```".equals(value)) 
			                && (item.isFillable() || certificeteItems.toString().contains("'" + item.getItemid() + "'"))) {
			            isAdd = false;
			            isFillableDesc(j, item);
			            
			            continue;
			        }
			        
			        if (StringUtils.isNotEmpty(value) && value.length() > item.getItemlength()) {
			            if (msgMap.containsKey(this.onlyFieldValue)) {
			                ArrayList<String> msgList = msgMap.get(this.onlyFieldValue);
			                msgList.add((msgList.size() + 1) + ".&nbsp;第" + (j + 1) + "行[" + item.getItemdesc()
			                + "]" + ResourceFactory.getProperty("workbench.info.import.error.intlength"));
			                
			                msgMap.put(this.onlyFieldValue, msgList);
			            } else {
			                ArrayList<String> msgList = new ArrayList<String>();
			                msgList.add("1.&nbsp;第" + (j + 1) + "行[" + item.getItemdesc() + "]"
			                        + ResourceFactory.getProperty("workbench.info.import.error.intlength"));
			                
			                msgMap.put(this.onlyFieldValue, msgList);
			            }
			            
			            continue;
			        }
			        
			        if (onlyField.equalsIgnoreCase(item.getItemid())) {
			            if (StringUtils.isEmpty(value)) {
			            	String errorMsg = ResourceFactory.getProperty("certificate.info.import.error.primaryValue");
			            	errorMsg = errorMsg.replace("{0}", String.valueOf((j + 1)));
			            	if (msgMap.containsKey(this.onlyFieldValue)) {
				                ArrayList<String> msgList = msgMap.get(this.onlyFieldValue);
				                msgList.add((msgList.size() + 1) + ".&nbsp;" + errorMsg);
				                
				                msgMap.put(this.onlyFieldValue, msgList);
				            } else {
				                ArrayList<String> msgList = new ArrayList<String>();
				                msgList.add("1.&nbsp;" + errorMsg);
				                
				                msgMap.put("&nbsp;", msgList);
				            }
			            	break;
			            } else
			                this.onlyFieldValue = value;
			        }
			        
			        if (certNOItemId.equalsIgnoreCase(item.getItemid()))
			            certNOValue = value;
			        
			        if (certCategoryItemId.equalsIgnoreCase(item.getItemid()))
			        	certCategoryValue = value;
			        
			        valueMap.put(key, value);
			    }
			    
			    if (!isAdd || StringUtils.isEmpty(onlyFieldValue) || StringUtils.isEmpty(certNOValue))
			        continue;
			    
			    String primaryValue = onlyFieldValue + "*" + certCategoryValue + "*" + certNOValue;
			    String cfPrimaryValue = certCategoryValue + "*" + certNOValue;
			    if (certificateMap.containsKey(primaryValue) || cFMap.containsKey(cfPrimaryValue)) {
			    	if(certificateMap.containsKey(primaryValue)) {
			    		int count = certificateMap.get(primaryValue);
			    		certificateMap.put(primaryValue, count + 1);
			    	}
			    	
			    	if(cFMap.containsKey(cfPrimaryValue)) {
			    		int count = cFMap.get(cfPrimaryValue);
			    		cFMap.put(cfPrimaryValue, count + 1);
			    	}
			    	
			        if (msgMap.containsKey(onlyFieldValue)) {
			            ArrayList<String> msgList = msgMap.get(onlyFieldValue);
			            msgList.add((msgList.size() + 1) + ".&nbsp;" + certNOValue + "在模板中存在重复数据！");
			            
			            msgMap.put(onlyFieldValue, msgList);
			        } else {
			            ArrayList<String> msgList = new ArrayList<String>();
			            msgList.add("1.&nbsp;" + certNOValue + "在模板中存在重复数据！");
			            
			            msgMap.put(onlyFieldValue, msgList);
			        }
			        
			        continue;
			    } else {
			        certificateMap.put(primaryValue, 1);
			        cFMap.put(cfPrimaryValue, 1);
			    }
			    
			    valueList.add(valueMap);
			    if (!onlyValues.toString().contains("'" + onlyFieldValue + "'"))
			        onlyValues.append(onlyFieldValue + "','");
			    
			}
			
			mapsList.add(maps);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler
					.Handle(new Exception(ResourceFactory.getProperty("workbench.info.import.error.excel")));
		} finally {
			PubFunc.closeIoResource(ism);
			PubFunc.closeIoResource(owb);
		}
		return mapsList;
	}

	private String checkdate(String str) {
		str = StringUtils.isEmpty(str) ? "" : str.replace("/", "-");
		if (str.indexOf("日") > -1)
			str = str.replace(" ", "");

		String dateStr = "false";
		if (str.length() < 4)
			dateStr = "false";
		else if (str.length() == 4) {
			Pattern p = Pattern.compile("^(\\d{4})$");
			Matcher m = p.matcher(str);
			if (m.matches())
				dateStr = str + "-01-01";
			else
				dateStr = "false";
		} else if (str.length() < 6) {
			Pattern p = Pattern.compile("^(\\d{4})年$");
			Matcher m = p.matcher(str);
			if (m.matches())
				dateStr = str.replace("年", "-") + "01-01";
			else
				dateStr = "false";
		} else if (str.length() == 7) {
			if (str.indexOf("月") != -1) {
				Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]$");
				Matcher m = p.matcher(str);
				if (m.matches()) {
					if (str.indexOf("月") != -1)
						dateStr = str.replace("年", "-").replace(".", "-").replace("月", "-") + "01";
					else
						dateStr = str.replace("年", "-").replace(".", "-") + "-01";
				} else
					dateStr = "false";
			} else {
				Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])$");
				Matcher m = p.matcher(str);
				if (m.matches())
					dateStr = str.replace("年", "-").replace(".", "-") + "-01";
				else
					dateStr = "false";
			}
		} else if (str.length() < 8) {// 2010年3 2010年3月
			Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]*$");
			Matcher m = p.matcher(str);
			if (m.matches()) {
				if (str.indexOf("月") != -1)
					dateStr = str.replace("年", "-").replace(".", "-").replace("月", "-") + "01";
				else
					dateStr = str.replace("年", "-").replace(".", "-") + "-01";
			} else
				dateStr = "false";
		} else if (str.length() == 8) {// 2010年3 2010年3月1
			Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])*$");
			Matcher m = p.matcher(str);
			if (m.matches()) {
				str = str.replace("年", "-").replace(".", "-").replace("月", "-");
				if (str.lastIndexOf("-") == str.length()) {
					if (str.length() < 10)
						dateStr = str + "01";
				} else {
					String[] temps = str.split("-");
					if (temps.length > 2)
						dateStr = checkMothAndDay(str);
					else
						dateStr = "false";
				}
			} else {
				dateStr = "false";
			}
		} else if (str.length() <= 11) {// 2017年1月1日
			Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])[日]*$");
			Matcher m = p.matcher(str);
			if (m.matches()) {
				String temp = str.replace("年", "-").replace(".", "-").replace("月", "-").replace("日", "");
				dateStr = checkMothAndDay(temp);
			} else
				dateStr = "false";

		} else {// 2017年1月1日1时1分 2017年1月1日1时1分1秒
			str = str.replace("时", ":").replace("分", ":");
			if (str.endsWith(":"))
				str = str.substring(0, str.length() - 1);

			Pattern p = null;
			if (str.split(":").length < 3)
				p = Pattern.compile(
						"^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])[\\s日]([01]*\\d{1}|2[0-3])[:时]([0-5]*\\d{1})[:分]*$");
			else
				p = Pattern.compile(
						"^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])[\\s日]([01]*\\d{1}|2[0-3])[:时]([0-5]*\\d{1})[:分]([0-5]*\\d{1})[秒]*$");

			Matcher m = p.matcher(str);
			if (m.matches()) {
				String tempDate = str.replace("年", "-").replace(".", "-").replace("月", "-").replace("日", " ");
				String temp = tempDate.split(" ")[0];
				dateStr = checkMothAndDay(temp);
				if (!"false".equalsIgnoreCase(dateStr)) {
					String tempTime = tempDate.split(" ")[1];
					dateStr += " " + tempTime;
				}
			} else
				dateStr = "false";
		}

		if (!"false".equals(dateStr))
			dateStr = formatDate(dateStr);

		return dateStr;
	}

	/**
	 * 校验月与日是否符合规则
	 * 
	 * @param date
	 *            日期数据
	 * @return
	 */
	private String checkMothAndDay(String date) {
		String tempDate = "false";
		String[] dates = date.split("-");
		if (dates[0].length() > 0 && dates[1].length() > 0 && dates[2].length() > 0) {
			int year = Integer.parseInt(dates[0]);
			int month = Integer.parseInt(dates[1]);
			int day = Integer.parseInt(dates[2]);
			switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12: {
				if (1 <= day && day <= 31)
					tempDate = date;

				break;
			}
			case 4:
			case 6:
			case 9:
			case 11: {
				if (1 <= day && day <= 30)
					tempDate = date;

				break;
			}
			case 2: {
				if (isLeapYear(year)) {
					if (1 <= day && day <= 29)
						tempDate = date;

				} else {
					if (1 <= day && day <= 28)
						tempDate = date;
				}
				break;
			}
			}
		}
		return tempDate;
	}

	/**
	 * 闰年的条件是： ① 能被4整除，但不能被100整除； ② 能被100整除，又能被400整除。
	 * 
	 * @param year
	 * @return
	 */
	private boolean isLeapYear(int year) {
		boolean t = false;
		if (year % 4 == 0) {
			if (year % 100 != 0) {
				t = true;
			} else if (year % 400 == 0) {
				t = true;
			}
		}
		return t;
	}

	/**
	 * 将日期数据1900-1-1 1:1:1转换成1900-01-01 01:01:01
	 * 
	 * @param date
	 *            校验完成的数据
	 * @return
	 */
	private String formatDate(String date) {
		String newDate = "";
		String[] dates = date.split(" ");
		String year = dates[0].split("-")[0];
		String month = dates[0].split("-")[1];
		month = Integer.parseInt(month) < 10 && month.length() == 1 ? "0" + month : month;
		String day = dates[0].split("-")[2];
		day = Integer.parseInt(day) < 10 && day.length() == 1 ? "0" + day : day;
		newDate = year + "-" + month + "-" + day;

		if (dates.length == 2) {
			String[] oldTime = dates[1].split(":");
			String hour = oldTime[0];
			hour = Integer.parseInt(hour) < 10 && hour.length() == 1 ? "0" + hour : hour;
			newDate += " " + hour;
			if (oldTime.length > 1) {
				String min = oldTime[1];
				min = Integer.parseInt(min) < 10 && min.length() == 1 ? "0" + min : min;
				newDate += ":" + min;
			}

			if (oldTime.length > 2) {
				String second = oldTime[2];
				second = Integer.parseInt(second) < 10 && second.length() == 1 ? "0" + second : second;
				newDate += ":" + second;
			}
		}

		return newDate;
	}

	/**
	 * 校验导入的数据
	 * 
	 * @param item
	 *            对应的指标
	 * @param cell
	 *            对应的单元格
	 * @param json
	 *            提示信息
	 * @param j
	 *            模板中的第几行
	 * @return
	 */
	public String checkData(FieldItem item, Cell cell, StringBuffer json, int j) {
		String value;
		DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
		if ("N".equals(item.getItemtype())) {
			value = String.valueOf(cell.getNumericCellValue());
			// 如果存在两个.则也提示为无效数值
			if ((value + " ").split("\\.").length > 2) {
				if (msgMap.containsKey(this.onlyFieldValue)) {
					ArrayList<String> msgList = msgMap.get(this.onlyFieldValue);
					msgList.add((msgList.size() + 1) + ".&nbsp;第" + (j + 1) + "行[" + item.getItemdesc()
							+ "]为数值类型，而上传的值无效，上传的值为：" + value);

					msgMap.put(this.onlyFieldValue, msgList);
				} else {
					ArrayList<String> msgList = new ArrayList<String>();
					msgList.add("1.&nbsp;第" + (j + 1) + "行[" + item.getItemdesc() + "]为数值类型，而上传的值无效，上传的值为：" + value);

					msgMap.put(this.onlyFieldValue, msgList);
				}

				value = "```";
			} else {
				Pattern p = Pattern.compile("[+-]?[\\d.]*");
				Matcher m = p.matcher(value);
				if (!m.matches()) {
					if (msgMap.containsKey(this.onlyFieldValue)) {
						ArrayList<String> msgList = msgMap.get(this.onlyFieldValue);
						msgList.add((msgList.size() + 1) + ".&nbsp;第" + (j + 1) + "行["
								+ item.getItemdesc() + "]为数值类型，而上传的值无效，上传的值为：" + value);

						msgMap.put(this.onlyFieldValue, msgList);
					} else {
						ArrayList<String> msgList = new ArrayList<String>();
						msgList.add("1.&nbsp;第" + (j + 1) + "行[" + item.getItemdesc() 
						        + "]为数值类型，而上传的值无效，上传的值为：" + value);

						msgMap.put(this.onlyFieldValue, msgList);
					}

					value = "```";
				} else {
					if ("N".equals(item.getItemtype())) {
						int dw = item.getDecimalwidth();
						if (dw == 0) {
							if (value.indexOf('.') != -1)
								value = value.substring(0, value.indexOf('.'));
						} else {
							// 数值型指标长度限制去除
							int il = item.getItemlength();
							int intValueLength = 0;
							if (value.indexOf('.') != -1) {
								intValueLength = value.substring(0, value.indexOf('.')).length();
								String dec = value.substring(value.indexOf('.') + 1);
								if (dec.length() > dw)
									value = value.substring(0, value.indexOf('.') + dw + 1);
							} else
								intValueLength = value.length();

							if (intValueLength > il) {
								if (msgMap.containsKey(this.onlyFieldValue)) {
									ArrayList<String> msgList = msgMap.get(this.onlyFieldValue);
									msgList.add((msgList.size() + 1) + ".&nbsp;第" + (j + 1) + "行"
											+ item.getItemdesc() + "指标中值长度超过指标长度！");

									msgMap.put(this.onlyFieldValue, msgList);
								} else {
									ArrayList<String> msgList = new ArrayList<String>();
									msgList.add("1.&nbsp;第" + (j + 1) + "行" + item.getItemdesc() + "指标中值长度超过指标长度！");

									msgMap.put(this.onlyFieldValue, msgList);
								}

								value = "```";
							}
						}

						value = value.replaceAll("\\+", "");
					}
				}
			}
		} else if ("D".equals(item.getItemtype())) {
		    String valueTemp = "";
			try {
			    switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING: {
                    value = cell.getStringCellValue();
                    break;
                }
                case Cell.CELL_TYPE_NUMERIC: {
                    Date d = cell.getDateCellValue();
                    value = formater.format(d);
                    break;
                }
                default: {
                    value = cell.getStringCellValue();
                }
			    }
			    valueTemp = value;
				value = checkdate(value);
				if ("false".equalsIgnoreCase(value)) {
					if (msgMap.containsKey(this.onlyFieldValue)) {
						ArrayList<String> msgList = msgMap.get(this.onlyFieldValue);
						msgList.add((msgList.size() + 1) + ".&nbsp;第" + (j + 1) + "行[" + item.getItemdesc()
										+ "]指标为日期类型，而上传的值无效，上传的值为：" + valueTemp);

						msgMap.put(this.onlyFieldValue, msgList);
					} else {
						ArrayList<String> msgList = new ArrayList<String>();
						msgList.add("1.&nbsp;第" + (j + 1) + "行[" + item.getItemdesc()
								+ "]指标为日期类型，而上传的值无效，上传的值为：" + valueTemp);

						msgMap.put(this.onlyFieldValue, msgList);
					}

					value = "```";
				}
			} catch (Exception e) {
				if (msgMap.containsKey(this.onlyFieldValue)) {
					ArrayList<String> msgList = msgMap.get(this.onlyFieldValue);
					msgList.add((msgList.size() + 1) + ".&nbsp;第" + (j + 1) + "行[" + item.getItemdesc()
							+ "]指标为日期类型，而上传的值无效，上传的值为：" + valueTemp);

					msgMap.put(this.onlyFieldValue, msgList);
				} else {
					ArrayList<String> msgList = new ArrayList<String>();
					msgList.add("1.&nbsp;第" + (j + 1) + "行[" + item.getItemdesc()
							+ "]指标为日期类型，而上传的值无效，上传的值为：" + valueTemp);

					msgMap.put(this.onlyFieldValue, msgList);
				}

				value = "```";
				return value;
			}
		} else {
			cell.setCellType(CellType.STRING);
			value = StringUtils.isEmpty(cell.getStringCellValue()) ? "" : cell.getStringCellValue();
			if (!"0".equalsIgnoreCase(item.getCodesetid())) {
				ArrayList<CodeItem> codeitemList = AdminCode.getCodeItemList(item.getCodesetid());
				for (CodeItem codeitem : codeitemList) {
					if (codeitem.getCodename().equals(value)) {
						value = codeitem.getCodeitem();
						break;
					}
				}
			}
		}

		return value;
	}
	
	private void isFillableDesc(int rowIndex, FieldItem item) {
		if (msgMap.containsKey(this.onlyFieldValue)) {
            ArrayList<String> msgList = msgMap.get(this.onlyFieldValue);
            msgList.add((msgList.size() + 1) + ".&nbsp;第" + (rowIndex + 1) + "行[" + item.getItemdesc()
            + "]为必填项，模板中的值错误或为空，此行数据不导入！");
            
            msgMap.put(this.onlyFieldValue, msgList);
        } else {
            ArrayList<String> msgList = new ArrayList<String>();
            msgList.add("1.&nbsp;第" + (rowIndex + 1) + "行[" + item.getItemdesc() 
            + "]为必填项，模板中的值错误或为空，此行数据不导入！");
            
            msgMap.put(this.onlyFieldValue, msgList);
        }
	}
}
