/*
 * Created on 2005-5-23
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.businessobject.general.inform.CorField;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.ScanFormationBo;
import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 *
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveSelfInfoTrans extends IBusiness {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	private String epmName; // 姓名，用于生产拼音码
	// 2013-4-17 WJH 处理取两人同时修改记录，取代码型指标
	private RowSet rSet = null;
	private boolean isNoData = false;

	private boolean isSameCodeItemValue(String sql, FieldItem fieldItem) {
		if (!isNoData && (rSet == null)) {
			ContentDAO dao = new ContentDAO(getFrameconn());
			try {
				rSet = dao.search(sql);
				isNoData = !rSet.next();
			} catch (Exception e) {
				isNoData = true;
				e.printStackTrace();
			}
		}

		// 取到数据，进行比较
		if (!isNoData) {
			try {
				if (fieldItem.getValue() == null || fieldItem.getValue().length() == 0)
					// 两个都为空，认为没有修改
					return rSet.getString(fieldItem.getItemid()) == null || rSet.getString(fieldItem.getItemid()).length() == 0;
				else {
					if ("N".equalsIgnoreCase(fieldItem.getItemtype())) {
						return Double.parseDouble(fieldItem.getValue()) - rSet.getDouble(fieldItem.getItemid()) <= 0.000000000001;
					} else {
						return fieldItem.getValue().equals(rSet.getString(fieldItem.getItemid()));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public void execute() throws GeneralException {
		DbNameBo db = new DbNameBo(this.getFrameconn());
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		HashMap reqhm = (HashMap) this.getFormHM().get("requestPamaHM");
		String actiontype = (String) this.getFormHM().get("actiontype");
		String A0100 = (String) this.getFormHM().get("a0100");
		String I9999 = (String) this.getFormHM().get("i9999");
		String part_unit = (String) this.getFormHM().get("part_unit");
		String part_setid = (String) this.getFormHM().get("part_setid");
		String n9999 = "";
		if (reqhm != null) {
			n9999 = (String) reqhm.get("i9999");
		}
		if ("I9999".equalsIgnoreCase(n9999) && (String) this.getFormHM().get("a0000") != null) {
			n9999 = (String) this.getFormHM().get("a0000");
			this.getFormHM().remove("a0000");
		}
		ArrayList infofieldlist = (ArrayList) this.getFormHM().get("infofieldlist");
		ArrayList fieldlist = new ArrayList();
		String setname = (String) this.getFormHM().get("setname");
		String userbase = (String) this.getFormHM().get("userbase");

		String tablename = userbase + setname;
		if ("A0100".equals(A0100) || "su".equalsIgnoreCase(A0100))
			A0100 = getUserId(tablename);

		try {
			// 检查人员库和人员id是否在权限之内
			CheckPrivSafeBo cpsb = new CheckPrivSafeBo(frameconn, userView);
			String result = cpsb.checkDb(userbase);
			if (!result.equals(userbase)) {
				throw new Exception("您的操作权限错误！");
			} else if (!A0100.equals(cpsb.checkA0100("", userbase, A0100, "")) && !A0100.equals(userView.getA0100())) {
				throw new Exception("您的操作权限错误！");
			}

			// 验证唯一性指标有无超出长度范围，超出长度范围的提示出来，且不进行插入操作
			String sqlQLenght = "select * from " + userbase + "A01 where 1=2";
			this.frecset = dao.search(sqlQLenght);
			ResultSetMetaData metaData = this.frecset.getMetaData();
			int columnCount = metaData.getColumnCount();

			StringBuffer fields = new StringBuffer();
			StringBuffer fieldvalues = new StringBuffer();
			for (int i = 0; i < infofieldlist.size(); i++) {
				FieldItem fieldItem = (FieldItem) infofieldlist.get(i);
				if("#####".equalsIgnoreCase(fieldItem.getItemid()))
					continue;
				
				if (!setname.equalsIgnoreCase(fieldItem.getFieldsetid())) {
					infofieldlist.remove(i);
					i--;
				} else {
					fieldlist.add(fieldItem);
				}
			}
			String[] fieldsname = new String[fieldlist.size()];
			String[] fieldcode = new String[fieldlist.size()];
			String org_id = "";
			String pos_id = "";
			String UN_code = "";
			String UM_code = "";
			String value = "";
			epmName = null;
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getFrameconn());
			String inputchinfor = sysbo.getValue(Sys_Oth_Parameter.INPUTCHINFOR);
			inputchinfor = inputchinfor != null && inputchinfor.trim().length() > 0 ? inputchinfor : "1";
			String approveflag = sysbo.getValue(Sys_Oth_Parameter.APPROVE_FLAG);
			approveflag = approveflag != null && approveflag.trim().length() > 0 ? approveflag : "1";

			String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "name"); // 身份证指标
			String idType = sysbo.getValue(Sys_Oth_Parameter.CHK_IdTYPE); // 证件类型指标
			String idTypeValue = sysbo.getIdTypeValue();// 身份证件类型默认值
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name"); // 验证唯一性指标
			String chkvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "valid");// 身份证验证是否启用
			String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");// 唯一性验证是否启用
			String dbchk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "db");// 验证身份证适用的人员库
			dbchk = StringUtils.isNotEmpty(dbchk) ? dbchk : DataDictionary.getDbpreString();
			String dbonly = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "db");// 验证唯一性适用的人员库
			dbonly = StringUtils.isNotEmpty(dbonly) ? dbonly : DataDictionary.getDbpreString();
			String blacklist_per = sysbo.getValue(Sys_Oth_Parameter.BLACKLIST, "base");// 黑名单人员库
			String blacklist_field = sysbo.getValue(Sys_Oth_Parameter.BLACKLIST, "field");// 黑名单指标
			/** 拼音简码 */
			String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
			OtherParam op=new OtherParam(this.getFrameconn());
			Map cardMap=op.serachAtrr("/param/formual[@name='bycardno']");
			//是否启用身份证关联结算
			String cardflag = "false";
			if(cardMap!=null&&cardMap.size()==6){
				cardflag=(String) cardMap.get("valid");
			}
			
			//证件类型值
			String idTypeTemp = "";
			//先获取姓名，用于保存时生成拼音简码
			for (int i = 0; i < fieldlist.size(); i++) {
				FieldItemView fieldItem = (FieldItemView) fieldlist.get(i);
				if ("a0101".equalsIgnoreCase(fieldItem.getItemid())) {
					this.epmName = PubFunc.splitString(fieldItem.getValue(), fieldItem.getItemlength());
				}
				if(fieldItem.getItemid().equalsIgnoreCase(idType))
					idTypeTemp = fieldItem.getValue();
			}
			
			// 2013-4-17 WJH 处理取两人同时修改记录，取代码型指标
			// 数值型的 viewvalue 没有值
			// 2013-5-6 WJH actiontype = "new" 表示新增，不比较
			rSet = null;
			isNoData = false;
			StringBuffer errorMsg = new StringBuffer();
			boolean compareFlag = "update".equals(actiontype);
			for (int i = 0; i < fieldlist.size(); i++) {
				FieldItemView fieldItem = (FieldItemView) fieldlist.get(i);
				if("2".equalsIgnoreCase(userView.analyseFieldPriv(fieldItem.toString())) 
						&& fieldItem.isFillable() && StringUtils.isEmpty(fieldItem.getValue().trim())) {
					errorMsg.append(fieldItem.getItemdesc() + ResourceFactory.getProperty("workbench.info.isRequired") + "<br>");
				}
				
				// 拼音简码没有值时，自动生成拼音简码
				if (StringUtils.isNotEmpty(pinyin_field) && !"#".equals(pinyin_field)&& StringUtils.isNotEmpty(this.epmName) 
						&& "A01".equalsIgnoreCase(setname)
						&& pinyin_field.equalsIgnoreCase(fieldItem.getItemid())
						&& StringUtils.isBlank(fieldItem.getValue())) {
					String pinyin = PubFunc.getPinym(this.epmName);
					fieldItem.setValue(pinyin);
				}
				// WJH 2013-4-17 两人同时修改记录处理
				if (compareFlag) {
					// xupengyu 2013-5-21 两人同时修改记录处理
					if (fieldItem.getValue() != null) {
						if (fieldItem.getValue().equals(fieldItem.getOldvalue()))
							continue;
					}

				}

				fields.append(fieldItem.getItemid());
				fieldsname[i] = fieldItem.getItemid();
				// 【8954】员工管理-记录录入（录入一个姓名带有英文半角括号，保存后，但是在查询的时候查询不出来这个人） jingq upd 2015.04.22
				String s = PubFunc.getStr(fieldItem.getValue());
				s = StringUtils.isEmpty(s) ? "" : s;
				s = PubFunc.keyWord_reback(s);
				s = PubFunc.stripScriptXss(s);
				s = PubFunc.replaceSQLkey(s);
				fieldItem.setValue(s);
				if (fieldItem.isMainSet() && "A01".equalsIgnoreCase(fieldItem.getFieldsetid())) {
					if ("1".equals(uniquenessvalid)) {
						if (dbonly.trim().length() > 2 && dbonly.toUpperCase().indexOf(userbase.toUpperCase()) != -1) {
							if (fieldItem.getItemid().equalsIgnoreCase(onlyname) && fieldItem.getValue() != null && fieldItem.getValue().trim().length() > 0) {
								String onlynameflag = db.checkOnlyName(dbonly, fieldItem.getFieldsetid(), fieldItem.getItemid(), fieldItem.getValue(), A0100);
								if (!"true".equalsIgnoreCase(onlynameflag))
									throw new GeneralException(onlynameflag);
							}
						}
					}
					if (blacklist_per != null && blacklist_per.length() > 2) {// 添加判断黑名单
						if (fieldItem.getItemid().equalsIgnoreCase(blacklist_field) && fieldItem.getValue() != null && fieldItem.getValue().trim().length() > 0) {
							// 设置当前新增人员的人员库
							db.setNbase(userbase.trim());
							String onlynameflag = db.checkOnlyName(blacklist_per, fieldItem.getFieldsetid(), fieldItem.getItemid(), fieldItem.getValue(), A0100);
							if (!"true".equalsIgnoreCase(onlynameflag)) {
								String tempStr = onlynameflag.indexOf("[") > 0 ? onlynameflag.substring(0, onlynameflag.indexOf("[")) + "列入黑名单!" : onlynameflag;
								throw new GeneralException(tempStr);
							}
						}
					}
					if ((fieldItem.isFillable()||(!fieldItem.isFillable()&&StringUtils.isNotBlank(fieldItem.getValue())))&&StringUtils.isNotBlank(chk)&&((StringUtils.isNotEmpty(idTypeValue)&&idTypeValue.equals(idTypeTemp))||StringUtils.isEmpty(idType))) {
						if (fieldItem.getItemid().equalsIgnoreCase(chk)) {
							if(!PubFunc.idCardValidate(fieldItem.getValue()))
								throw new GeneralException("false:身份证号不正确");
						}
					}
					if ("true".equals(cardflag)&&((StringUtils.isNotEmpty(idTypeValue)&&idTypeValue.equals(idTypeTemp))||StringUtils.isEmpty(idType))) {
						if (dbchk.trim().length() > 2) {
							if (fieldItem.getItemid().equalsIgnoreCase(chk)) {
								CorField cof = new CorField();
								String sex = "";
								String birthday = "";
								for (int j = 0; j < fieldlist.size(); j++) {
									FieldItem fieldItem1 = (FieldItem) fieldlist.get(j);
									if (fieldItem1 != null) {
										if (fieldItem1.getItemid() != null) {
											if (fieldItem1.getItemid().equalsIgnoreCase(cof.getItemid(CorField.SEX_ITEMID, this.frameconn))) {
												sex = fieldItem1.getValue();
												sex = sex != null && sex.trim().length() > 0 ? sex : "";
											}
											if (fieldItem1.getItemid().equalsIgnoreCase(cof.getItemid(CorField.BIRTHDAY_ITEMID, this.frameconn))) {
												birthday = fieldItem1.getValue();
												birthday = birthday != null && birthday.trim().length() > 0 ? birthday : "";
												if (birthday.trim().length() > 0) {
													WeekUtils weekUtils = new WeekUtils();
													birthday = weekUtils.dateTostr(weekUtils.strTodate(birthday));
													birthday = birthday.replaceAll("-", "").replaceAll("\\.", "");
												}
											}
										}
									}
								}
								if (fieldItem.getValue() != null && fieldItem.getValue().trim().length() > 0) {
									String check = db.checkIdNumber(fieldItem.getValue(), birthday, sex);
									String arr[] = check.split(":");
									if (arr.length == 2) {
										if ("false".equalsIgnoreCase(arr[0]))
											throw new GeneralException(arr[1]);
									}
								}
							}
						}
					}
					if (dbchk.trim().length() > 2 && dbchk.toUpperCase().indexOf(userbase.toUpperCase()) != -1 && "1".equals(chkvalid)&&fieldItem.getItemid().equalsIgnoreCase(chk)) {
						//分别校验18位身份证号和15位身份证号是否唯一
						String onlynameflag = db.uniquenessCheck(dbchk, fieldItem.getFieldsetid(), fieldItem.getItemid(), fieldItem.getValue(), A0100, true);
						if (!"true".equalsIgnoreCase(onlynameflag))
							throw new GeneralException(onlynameflag);
						else {
							onlynameflag = db.uniquenessCheck(dbchk, fieldItem.getFieldsetid(), fieldItem.getItemid(), db.changeCardID(fieldItem.getValue(), ""), A0100, true);
							if (!"true".equalsIgnoreCase(onlynameflag))
								throw new GeneralException(onlynameflag);
						}
					}
				}
				
				if (fieldItem.isSequenceable() && "new".equals(actiontype)) {
					String idd = "";
					fieldvalues.append("'" + idd + "'");
					fieldcode[i] = "'" + idd + "'";
					fieldItem.setValue(idd);
				} else if ("D".equals(fieldItem.getItemtype())) {
					String itemValue = fieldItem.getValue();
					itemValue = PubFunc.DateStringChange(itemValue);
					fieldvalues.append(itemValue);
					fieldcode[i] = itemValue;
					itemValue = dateFormatter(fieldItem.getValue(), fieldItem.getItemlength());
					fieldItem.setValue(itemValue);
				} else if ("M".equals(fieldItem.getItemtype())) {
					if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue())) {
						fieldcode[i] = "null";
						fieldvalues.append("null");
					} else {
						int itemLength = 0;
						String columnName = "";
						for (int j = 1; j <= columnCount; j++) {
							columnName = metaData.getColumnName(j).toLowerCase();
							itemLength = fieldItem.getItemlength();
							if (fieldItem.getItemid().equalsIgnoreCase(columnName)) {
								break;
							}
						}
						int pyLenght = 0;

						pyLenght = fieldItem.getValue().trim().length();

						if (itemLength != 0 && itemLength != 10 & pyLenght > itemLength) {
							throw new Exception(fieldItem.getItemdesc() + "内容的长度超过限制！");
						}
						fieldcode[i] = "'" + fieldItem.getValue() + "'";
						fieldvalues.append("'" + fieldItem.getValue() + "'");
					}
				} else if ("N".equals(fieldItem.getItemtype())) {
					if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue())) {
						fieldcode[i] = "null";
						fieldvalues.append("null");
					} else {
						fieldcode[i] = fieldItem.getValue();
						fieldvalues.append(fieldItem.getValue());
					}
				} else {
					if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue())) {
						fieldcode[i] = "null";
						fieldvalues.append("null");
					} else {
						if (fieldItem.isCode()) {
							if (part_unit != null && part_unit.equalsIgnoreCase(fieldItem.getItemid().toString()) && part_setid != null && part_setid.equalsIgnoreCase(setname)) {
								value = AdminCode.getCodeName("UN", fieldItem.getValue());
								if (value == null || value.length() <= 0)
									value = AdminCode.getCodeName("UM", fieldItem.getValue());
								if (value == null || value.length() <= 0) {
									fieldItem.setValue("");
								}
							} else {
								// tianye update start
								// 关联部门的指标支持指定单位（部门中查不出信息就去单位中查找）
								if (!"e0122".equalsIgnoreCase(fieldItem.getItemid())) {
									CodeItem codeItem = InfoUtils.getUMOrUN(fieldItem.getCodesetid(), fieldItem.getValue());
									value = (codeItem != null ? codeItem.getCodename() : "");
								} else {
									value = AdminCode.getCodeName(fieldItem.getCodesetid(), fieldItem.getValue());
								}
								// end
								if (value == null || value.length() <= 0) {
									fieldItem.setValue("");
								}
							}

						}
						fieldcode[i] = "'" + PubFunc.splitString(fieldItem.getValue(), fieldItem.getItemlength()) + "'";
						if ("b0110".equalsIgnoreCase(fieldItem.getItemid()) || "e0122".equalsIgnoreCase(fieldItem.getItemid())) {
							org_id = PubFunc.splitString(fieldItem.getValue(), fieldItem.getItemlength());
							if ("b0110".equalsIgnoreCase(fieldItem.getItemid())) {
								UN_code = PubFunc.splitString(fieldItem.getValue(), fieldItem.getItemlength());
							} else {
								UM_code = PubFunc.splitString(fieldItem.getValue(), fieldItem.getItemlength());
							}
						}
						if ("e01a1".equalsIgnoreCase(fieldItem.getItemid())) {
							pos_id = PubFunc.splitString(fieldItem.getValue(), fieldItem.getItemlength());
						}
						fieldvalues.append("'" + PubFunc.splitString(fieldItem.getValue(), fieldItem.getItemlength()) + "'");
					}
				}
				fields.append(",");
				fieldvalues.append(",");
			}

			if(StringUtils.isNotEmpty(errorMsg.toString()))
				throw new GeneralException(errorMsg.toString());
			
			if (rSet != null)
				rSet.close();

			boolean flag = false;
			StructureExecSqlString structureExecSqlString = new StructureExecSqlString();
			structureExecSqlString.setFieldcode(fieldcode);
			String checksave = "01";
			ArrayList msglist = new ArrayList();

			ScanFormationBo scanFormationBo = new ScanFormationBo(this.getFrameconn(), this.userView);
			if (scanFormationBo.doScan()) {
				boolean bScan = true;
				StringBuffer itemids = new StringBuffer();
				LazyDynaBean scanBean = new LazyDynaBean();
				setScanBeanList(actiontype, userbase, setname, A0100, I9999, fieldlist, itemids, scanBean);

				if ("true".equals(scanFormationBo.getPart_flag()) && setname.equals(scanFormationBo.getPart_setid())) {// 兼职子集
					String part_fld = "";
					part_fld = scanFormationBo.getPart_unit();
					if ((part_fld != null) && (!"".equals(part_fld)))
						itemids.append(",b0110");
					part_fld = scanFormationBo.getPart_dept();
					if ((part_fld != null) && (!"".equals(part_fld)))
						itemids.append(",e0122");
					part_fld = scanFormationBo.getPart_pos();
					if ((part_fld != null) && (!"".equals(part_fld)))
						itemids.append(",e01a1");
				}
				if (("," + itemids + ",").indexOf(",e01a1,") < 0) {
					scanFormationBo.setPosChange(false);
				}
				if (scanFormationBo.needDoScan(userbase + ',', itemids.toString())) {

					scanBean.set("objecttype", "1");
					scanBean.set("nbase", userbase);
					scanBean.set("a0100", A0100);
					scanBean.set("ispart", "0");

					if ("new".equals(actiontype)) {
						scanBean.set("addflag", "1");
					} else {
						scanBean.set("addflag", "0");
					}

					if ("01".equals(setname.substring(1, 3))) {// 主集
					} else {
						if ("true".equals(scanFormationBo.getPart_flag()) && setname.equals(scanFormationBo.getPart_setid())) {// 兼职子集
							scanBean.set("ispart", "1");
							scanBean.set("i9999", I9999);
							String part_fld = "";
							part_fld = scanFormationBo.getPart_unit();
							if ((part_fld != null) && (!"".equals(part_fld)))
								if ((String) scanBean.get(part_fld) != null)
									scanBean.set("b0110", (String) scanBean.get(part_fld));
							part_fld = scanFormationBo.getPart_dept();
							if ((part_fld != null) && (!"".equals(part_fld)))
								if ((String) scanBean.get(part_fld) != null)
									scanBean.set("e0122", (String) scanBean.get(part_fld));
							part_fld = scanFormationBo.getPart_pos();
							if ((part_fld != null) && (!"".equals(part_fld)))
								if ((String) scanBean.get(part_fld) != null)
									scanBean.set("e01a1", (String) scanBean.get(part_fld));
						} else {// 普通子集 最近一条才检查
							scanBean.set("addflag", "0"); // 普通子集都为修改
							if ("new".equals(actiontype)) {
								if (reqhm.containsKey("insert1")) {// 新增
									bScan = false;
								}
							} else {
								if (!(getMaxI9999(userbase, setname, A0100, dao).equals(I9999))) {// 不是最近一条
									bScan = false;
								}
							}
						}
					}

					if (bScan) {
						ArrayList beanList = new ArrayList();
						beanList.add(scanBean);
						scanFormationBo.execDate2TmpTable(beanList);
						String mess = scanFormationBo.isOverstaffs();
						if (!"ok".equals(mess)) {
							if ("warn".equals(scanFormationBo.getMode())) {
								msglist.add(mess);
							} else {
								throw GeneralExceptionHandler.Handle(new GeneralException("", mess, "", ""));
							}
						}

					}
				}

			}

			if ("new".equals(actiontype)) {
				if ((tablename.length() == 3 && "01".equals(tablename.substring(1, 3))) || (tablename.length() == 6 && "01".equals(tablename.substring(4, 6)))) {
					A0100 = structureExecSqlString.InfoInsert("1", tablename, fields.toString(), fieldvalues.toString(), A0100, userView.getUserName(), this.getFrameconn());
					/******* 新增人员超出编制 ******/
					if (reqhm.containsKey("insert1")) {
						// 插入
						String tempii = n9999;
						reqhm.remove("insert1");
						this.updateInsertA0000(tempii, tablename, A0100, this.getFrameconn());
					}
					db.dateLinkage(org_id, pos_id, 1, "+");// 调用数据联动
					String returnvalue = (String) this.getFormHM().get("returnvalue");
					if ("64".equals(returnvalue) || "65".equals(returnvalue) || "66".equals(returnvalue)) {// 如果是党团工会处添加群众人员则自动在子集政治面貌中增加一条记录（如果设置的政治面貌字段在子集中)
																											// xuj
																											// 2010-2-25
						ConstantXml xml = new ConstantXml(this.frameconn, "PARTY_PARAM");
						String polity = xml.getNodeAttributeValue("/param/polity", "column");
						polity = polity != null && polity.length() > 0 ? polity : "";
						String person = xml.getNodeAttributeValue("/param/polity/person", "value");
						String[] ps = person.split(",");
						if (polity != null && polity.length() > 0 && ps.length > 0) {
							String[] p = polity.split("\\.");
							if (p.length == 2) {
								if (!"A01".equalsIgnoreCase(p[0])) {

									String sql = "insert into usr" + p[0] + " (a0100,i9999," + p[1] + ",CreateTime,ModTime,CreateUserName,ModUserName) values(?,?,?,?,?,?,?)";
									ArrayList values = new ArrayList();
									values.add(A0100);
									values.add(new Integer(this.getMaxI9999(p[0], A0100, dao)));
									values.add(ps[0]);
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									Date start_date = null;
									String start_date1 = sdf.format(new java.util.Date());
									try {
										start_date = new Date(sdf.parse(start_date1).getTime());
									} catch (ParseException e1) {
										e1.printStackTrace();
									}
									values.add(start_date);
									values.add(start_date);
									values.add(userView.getUserName());
									values.add(userView.getUserName());
									dao.insert(sql, values);
								}
							}
						}
					}
				} else {

					// 兼职控制编制
					/** 兼职参数 */
					String partflag = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "flag");// 是否启用，true启用
					// 兼职岗位占编 1：占编
					String takeup_quota = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "takeup_quota");
					String ps_parttime = "0";
					if ("true".equals(partflag) && "1".equals(takeup_quota)) {
						ps_parttime = "1";
					}
					String pos_ctrl = sysbo.getValueS(Sys_Oth_Parameter.WORKOUT, "pos");
					String tempii = n9999;
					I9999 = structureExecSqlString.InfoInsert("1", tablename, fields.toString(), fieldvalues.toString(), A0100, userView.getUserName(), this.getFrameconn());
					if (reqhm.containsKey("insert1")) {
						reqhm.remove("insert1");
						this.updateRecord(tempii, tablename, A0100, this.getFrameconn());
					}
				}
			} else {
				RecordVo vo_old = null;
				if (tablename.length() == 3 && "01".equals(tablename.substring(1, 3)) || tablename.length() == 6 && "01".equals(tablename.substring(4, 6))) {
					vo_old = db.getRecordVoA01(tablename, A0100);
				} else {
					vo_old = db.getRecordVoA01(tablename, A0100, Integer.parseInt(I9999));
				}
				// 兼职控制编制
				/** 兼职参数 */
				String partflag = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "flag");// 是否启用，true启用
				// 兼职岗位占编 1：占编
				String takeup_quota = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "takeup_quota");
				String ps_parttime = "0";
				if ("true".equals(partflag) && "1".equals(takeup_quota)) {
					ps_parttime = "1";
				}
				String pos_ctrl = sysbo.getValueS(Sys_Oth_Parameter.WORKOUT, "pos");
				flag = new StructureExecSqlString().InfoUpdate("1", tablename, fieldsname, fieldcode, A0100, I9999, userView.getUserName(), this.getFrameconn());
				if (pos_id != null && pos_id.trim().length() > 0) {
					String pos_old_id = vo_old.getString("e01a1");
					int ncount = 1;
					if (pos_old_id != null && pos_old_id.equals(pos_id))
						ncount = 0;
					if (ncount > 0) {
						if (tablename.length() == 3 && "01".equals(tablename.substring(1, 3)) || tablename.length() == 6 && "01".equals(tablename.substring(4, 6))) {
							db.dateLinkage("", pos_id, 1, "+");
							db.dateLinkage("", pos_old_id, 1, "-");
						}
					}
				}
			}

			if ("new".equals(actiontype)) // 邓灿追加生成序列号
			{
				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
				InfoUtils infoUtils = new InfoUtils();
				for (int i = 0; i < fieldlist.size(); i++) {
					FieldItem fieldItem = (FieldItem) fieldlist.get(i);
					if (fieldItem.isSequenceable()) {
						// 主集
						FieldItem _fieldItem = DataDictionary.getFieldItem(fieldItem.getItemid().toLowerCase());
						RecordVo pvo = new RecordVo(userbase + "A01");
						pvo.setString("a0100", A0100);
						pvo = dao.findByPrimaryKey(pvo);
						String _value = infoUtils.getSequenceableValue(_fieldItem.getItemid(), userbase, setname, this.frameconn, A0100, I9999, idg);
						if ((tablename.length() == 3 && "01".equals(tablename.substring(1, 3)))
								|| (tablename.length() == 6 && "01".equals(tablename.substring(4, 6)))) {
							pvo.setString(_fieldItem.getItemid(), _value);
							dao.updateValueObject(pvo);
						} else {
							dao.update("update " + userbase + setname + " set " + _fieldItem.getItemid() + "='" + _value + "' where a0100='" + A0100 + "' and i9999=" + I9999);
						}
						
						fieldItem.setValue(_value);
						for (int m = 0; m < infofieldlist.size(); m++) {
							FieldItem fi = (FieldItem) infofieldlist.get(m);
							if("#####".equalsIgnoreCase(fieldItem.getItemid()))
								continue;
							
							if(fieldItem.getItemid().equalsIgnoreCase(fi.getItemid())) {
								fi.setValue(_value);
							}
						}
					}
				}
			}

			this.getFormHM().put("infofieldlist", infofieldlist);
			this.getFormHM().put("a0100", A0100);
			this.getFormHM().put("actiontype", "update");
			this.getFormHM().put("i9999", I9999);
			this.getFormHM().put("checksave", checksave);
			this.getFormHM().put("actiontype", "update");
			if (msglist.size() > 0) {
				StringBuffer msg = new StringBuffer();
				for (int i = 0; i < msglist.size(); i++) {
					if (msglist.size() > 1) {
						msg.append((i + 1) + ":" + msglist.get(i) + "\\n");
					} else {
						msg.append(msglist.get(i));
					}
				}
				this.getFormHM().put("msg", msg.toString());
			} else
				this.getFormHM().put("msg", "");
			
			if ("new".equals(actiontype)) {
				this.getFormHM().put("@eventlog", ResourceFactory.getProperty("workbench.info.log.saveInsert"));
			} else {
				this.getFormHM().put("@eventlog", ResourceFactory.getProperty("workbench.info.log.saveUpdate"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if ("new".equals(actiontype)) {
				try {
					StringBuffer sql = new StringBuffer();
					sql.append("delete from " + tablename);
					sql.append(" where a0101 is null");
					sql.append(" and CreateTime is null");
					sql.append(" and ModTime is null");
					sql.append(" and CreateUserName is null");
					sql.append(" and ModUserName is null");
					sql.append(" and a0100=?");

					ArrayList<String> value = new ArrayList<String>();
					value.add(A0100);
					dao.update(sql.toString(), value);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}

			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	public synchronized String getUserId(String tableName) throws GeneralException {
		return DbNameBo.insertMainSetA0100(tableName, this.getFrameconn());
	}

	private void updateRecord(String I9999, String tablename, String A0100, Connection conn) {

		String upsql1 = "update " + tablename + " set I9999=I9999+1 where I9999>=" + I9999 + " and a0100='" + A0100 + "' ";
		String upsql = "update " + tablename + " set I9999=" + I9999 + " where I9999=(select max(I9999) from " + tablename + " where a0100='" + A0100 + "')  and a0100='" + A0100 + "'";
		try {
			ContentDAO dao = new ContentDAO(conn);
			dao.update(upsql1);
			dao.update(upsql);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 主集插入修改a0000
	 * 
	 * @param a0000
	 * @param tablename
	 * @param A0100
	 * @param conn
	 */
	private void updateInsertA0000(String a0000, String tablename, String A0100, Connection conn) {

		String upsql1 = "update " + tablename + " set a0000=a0000+1 where a0000>=" + a0000 + " ";
		String upsql = "update " + tablename + " set a0000=" + a0000 + " where a0100='" + A0100 + "'";
		try {
			ContentDAO dao = new ContentDAO(conn);
			dao.update(upsql1);
			dao.update(upsql);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private ArrayList updateList(ArrayList newFieldList, ArrayList oldFieldList, ArrayList fieldlist) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < fieldlist.size(); i++) {
			FieldItem item = (FieldItem) fieldlist.get(i);
			FieldItem newitem = (FieldItem) newFieldList.get(i);
			FieldItem olditem = (FieldItem) oldFieldList.get(i);
			if (!newitem.getValue().equalsIgnoreCase(olditem.getValue())) {
				list.add(newitem);
			} else {
				list.add(item);
			}
		}

		return list;
	}

	private int getMaxI9999(String fieldsetid, String a0100, ContentDAO dao) throws SQLException {
		int maxi9999 = 0;
		String sql = "select max(i9999) i9999 from usr" + fieldsetid + " where a0100='" + a0100 + "'";
		this.frecset = dao.search(sql);
		if (this.frecset.next())
			maxi9999 = this.frecset.getInt("i9999");

		return maxi9999 + 1;
	}

	private String getMaxI9999(String usrbase, String fieldsetid, String a0100, ContentDAO dao) throws SQLException {
		String maxi9999 = "0";
		String sql = "select max(i9999) i9999 from " + usrbase + fieldsetid + " where a0100='" + a0100 + "'";
		this.frecset = dao.search(sql);
		if (this.frecset.next())
			maxi9999 = String.valueOf(this.frecset.getInt("i9999"));

		return maxi9999;
	}

	private void setScanBeanList(String actiontype, String userbase, String setname, String A0100, String I9999, ArrayList fieldlist, StringBuffer scanItemIds, LazyDynaBean scanBean) {
		scanItemIds.setLength(0);
		boolean compareFlag = "update".equals(actiontype);
		LazyDynaBean rec = null;
		if (compareFlag) {
			List rs = null;
			StringBuffer strsql = new StringBuffer();
			strsql.append("select * from ");
			strsql.append(userbase + setname);
			strsql.append(" where A0100='");
			strsql.append(A0100);
			strsql.append("'");
			if (!"A01".substring(1, 3).equals(setname.substring(1, 3))) // 如果子集的修改则条件有I9999
			{
				strsql.append(" and I9999=");
				strsql.append(I9999);
			}
			rs = ExecuteSQL.executeMyQuery(strsql.toString(), this.getFrameconn());
			boolean isExistData = !rs.isEmpty();
			compareFlag = false;
			if (isExistData) {
				rec = (LazyDynaBean) rs.get(0);
				compareFlag = true;
			}

		}

		for (int i = 0; i < fieldlist.size(); i++) {
			FieldItemView fieldItem = (FieldItemView) fieldlist.get(i);
			if (fieldItem.isSequenceable())
				continue;

			String itemid = fieldItem.getItemid().toLowerCase();
			String value = PubFunc.getStr(fieldItem.getValue());

			if ((value == null) || ("null".equalsIgnoreCase(value))) {
				value = "";
			}
			scanBean.set(itemid, value);

			if (compareFlag) {
				value = "";
				if ("A".equals(fieldItem.getItemtype()) || "M".equals(fieldItem.getItemtype())) {
					value = rec.get(fieldItem.getItemid()) != null ? rec.get(fieldItem.getItemid()).toString() : "";
				} else if ("D".equals(fieldItem.getItemtype())) {
					if (rec.get(fieldItem.getItemid()) != null && rec.get(fieldItem.getItemid()).toString().length() >= 10 && fieldItem.getItemlength() == 10) {
						value = (new FormatValue().format(fieldItem, rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0, 10)));
					} else if (rec.get(fieldItem.getItemid()) != null && rec.get(fieldItem.getItemid()).toString().length() >= 10 && fieldItem.getItemlength() == 4) {
						value = (new FormatValue().format(fieldItem, rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0, 4)));
					} else if (rec.get(fieldItem.getItemid()) != null && rec.get(fieldItem.getItemid()).toString().length() >= 10 && fieldItem.getItemlength() == 7) {
						value = (new FormatValue().format(fieldItem, rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0, 7)));
					} else {
						value = "";
					}
				} else {
					value = (PubFunc.DoFormatDecimal(rec.get(fieldItem.getItemid()) != null ? rec.get(fieldItem.getItemid()).toString() : "", fieldItem.getDecimalwidth()));
				}
				if (fieldItem.getValue() != null) {
					if (fieldItem.getValue().equals(value))
						continue;
				} else {
					if ("".equals(value))
						continue;
				}
			}

			if (!"".equals(scanItemIds.toString())) {
				scanItemIds.append(",");
			}
			scanItemIds.append(itemid);

		}
	}

	/**
	 * 日期类型的指标格式化
	 * 
	 * @param value
	 *            日期类型指标的值
	 * @param length
	 *            日期类型指标的长度
	 * @return
	 */
	private String dateFormatter(String value, int length) {
		try {
			if(StringUtils.isEmpty(value))
				return value;
			
			value = value.replace("-", ".");
			value = value.replace("/", ".");
			value = value.replace("\\", ".");
			java.util.Date date = DateUtils.getDate(value, getPattern(value.length()));
			value = DateUtils.format(date, getPattern(length));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	private String getPattern (int length) {
		String pattern = "yyyy.MM.dd";
		if (length == 4)
			pattern = "yyyy";
		else if (length == 7)
			pattern = "yyyy.MM";
		else if (length == 16)
			pattern = "yyyy.MM.dd hh:mm";
		else if (length >= 18)
			pattern = "yyyy.MM.dd hh:mm:ss";
		else
			pattern = "yyyy.MM.dd";
		
		return pattern;
	}
}
