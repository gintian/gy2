package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqDBHelper;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.SqlDifference;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * Title:ExportExcelTrans.java
 * </p>
 * <p>
 * Description:考勤导入模板数据
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-12-08 13:00:00
 * </p>
 * 
 * @author xujian
 * @version 1.0
 * 
 */
public class ImportTemplateTrans extends IBusiness {

	public void execute() throws GeneralException {

		FormFile form_file = (FormFile) getFormHM().get("file");
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String tablename = (String) hm.get("tablename");
		try {
			if(!FileTypeUtil.isFileTypeEqual(form_file))
			{
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		StringBuffer sql = new StringBuffer();
		sql.append("update " + tablename + " set ");
		
		int updateFidsCount = 0;// 将要更新的字段数目
		ArrayList fieldList = new ArrayList();
		Workbook wb = null;
		Sheet sheet = null;
		InputStream in = null;
		try {
			in = form_file.getInputStream();
			wb = WorkbookFactory.create(in);
			sheet = wb.getSheetAt(0);

			HashMap map = new HashMap();
			Row row = sheet.getRow(0);
			if (row == null)
				throw new GeneralException("请用导出的Excel模板来导入数据！");

			int cols = row.getPhysicalNumberOfCells();
			int rows = sheet.getPhysicalNumberOfRows();
			StringBuffer a0100s = new StringBuffer();
			StringBuffer codeBuf = new StringBuffer();
			int x = 0;
			ContentDAO dao = new ContentDAO(this.frameconn);
			HashMap dbMap = new HashMap();
			String dbSql = "select pre , dbname  from dbname";
			this.frowset = dao.search(dbSql);
			while (this.frowset.next()) {
				dbMap.put(this.frowset.getString("dbname"), this.frowset.getString("pre"));
			}
			String gnoField = getGNOField();
			// 识别模板类型
			boolean priKeyColExist = false; // 固定模板“主键标识”列存在
			boolean q03z0ColExist = false; // 自定义模板“日期”列
			boolean gnoColExists = false; // 自定义模板“工号”列

			HashMap codeColMap = new HashMap();
			if (row != null) {

				if (cols < 1 || rows < 1) {
					priKeyColExist = false;
					q03z0ColExist = false;
					gnoColExists = false;
				} else {
					for (int i = 0; i < 2; i++) {
						String value = "";
						Cell cell = row.getCell((short) i);
						if (cell != null) {
							switch (cell.getCellType()) {
							case Cell.CELL_TYPE_FORMULA:
								break;
							case Cell.CELL_TYPE_NUMERIC:
								double y = cell.getNumericCellValue();
								value = Double.toString(y);
								break;
							case Cell.CELL_TYPE_STRING:
								value = cell.getStringCellValue();
								break;
							default:
								value = "";
							}
						} else {
							priKeyColExist = false;
							q03z0ColExist = false;
							gnoColExists = false;
							break;
						}

						if (i == 0) {
							if ("主键标识串".equalsIgnoreCase(value))
								priKeyColExist = true;
							else if ("日期".equalsIgnoreCase(value)) {
								q03z0ColExist = true;
								gnoColExists = true;
							}
						}
						if (i == 1) {
							if (!"工号".equalsIgnoreCase(value) && !priKeyColExist) {
								gnoColExists = false;
								throw new GeneralException("请用导出的Excel模板来导入数据！");
							}

						} else if ((1 == i) && (!"工号".equalsIgnoreCase(value))) {
							gnoColExists = false;
						}

						if (!priKeyColExist && !q03z0ColExist && !gnoColExists)
							break;
					}
				}

				if (!priKeyColExist && !q03z0ColExist && !gnoColExists)
					throw new GeneralException("请用导出的Excel模板来导入数据！");

				// 固定模板
				short initCol = 2;
				if (priKeyColExist)
					initCol = 1;
				for (short c = initCol; c < cols; c++) {
					Cell cell = row.getCell(c);
					if (cell != null) {
						String title = "";
						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_FORMULA:
							break;
						case Cell.CELL_TYPE_NUMERIC:
							double y = cell.getNumericCellValue();
							title = Double.toString(y);
							break;
						case Cell.CELL_TYPE_STRING:
							title = cell.getStringCellValue();
							break;
						default:
							title = "";
						}

						if ("".equals(title.trim()))
							throw new GeneralException("标题行存在空标题！请用导出的Excel模板来导入数据！");

						String field = cell.getCellComment().getString().toString().toLowerCase().trim();
						FieldItem fieldItem = DataDictionary.getFieldItem(field);
						
						if (fieldItem == null)
							throw new GeneralException("第" + c + "列指标“" + title + "”不存在！请重新下载模板！");
						
						String codesetid = fieldItem.getCodesetid();
						if (!"0".equals(codesetid)) {
							if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equals(codesetid)) {
								codeBuf.append("select codesetid,codeitemid,codeitemdesc");
								codeBuf.append(" from codeitem");
								codeBuf.append(" where codesetid='");
								codeBuf.append(codesetid);
								codeBuf.append("' and codeitemid=childid");
								codeBuf.append(" union all ");
							} else {
								codeBuf.append("select codesetid,codeitemid,codeitemdesc");
								codeBuf.append(" from organization");
								codeBuf.append(" where codesetid='");
								codeBuf.append(codesetid);
								codeBuf.append("' and  codeitemid not in (select parentid from organization");
								codeBuf.append(" where codesetid='"	+ codesetid + "')");
								codeBuf.append(" union all ");
							}
						}
						
						String itemtype = DataDictionary.getFieldItem(field).getItemtype();
						if (allowedImportItem(field, itemtype)) {
							map.put(new Short(c), field + ":" + cell.getStringCellValue());
							sql.append(field + "=?,");
							updateFidsCount++;
							fieldList.add(field);
						}
					} else
						break;
				}
				if (codeBuf.length() > 0) {
					codeBuf.setLength(codeBuf.length() - " union all ".length());
					RowSet rs = null;
					try {
						rs = dao.search(codeBuf.toString());
						while (rs.next())
							codeColMap.put(rs.getString("codesetid") + "a04v2u"
									+ rs.getString("codeitemdesc"), rs.getString("codeitemid"));
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
					    KqUtilsClass.closeDBResource(rs);
					}
				}

				sql.setLength(sql.length() - 1);
				sql.append(" where NBASE=? and A0100=? and Q03Z0=?");

				x = sql.length() + 4;
				if (this.userView.isAdmin()) {
					sql.append(" and 1=1 ");
				} else {
					// 要控制人员范围
					String a_code = RegisterInitInfoData.getKqPrivCode(this.userView)
							+ RegisterInitInfoData.getKqPrivCodeValue(this.userView);
					if (a_code.length() >= 2) {
						String codesetid = a_code.substring(0, 2);
						String value = a_code.substring(2);
						if ("UN".equalsIgnoreCase(codesetid)) {
							sql.append(" and (B0110 like '");
							sql.append(value);
							sql.append("%'");
							if ("".equalsIgnoreCase(value))
								sql.append(" or B0110 is null");
							sql.append(")");
						} else if ("UM".equalsIgnoreCase(codesetid)) {
							sql.append(" and E0122 like '");
							sql.append(value);
							sql.append("%'");
						}
						String whereIn =  RegisterInitInfoData.getKqEmpPrivWhr(this.frameconn, this.userView, tablename);
						sql.append(" and " + whereIn + "");

					} else if (a_code.trim().length() == 0)// 没有管理权限
						sql.append(" and 1=2 ");
				}
				// 只能修改审批状态为起草（01）和驳回（07）的记录
				// sql.append(" and Q03Z5 in ('01','07')");

			}
			ArrayList list2 = new ArrayList();
			String Q03Z0 = null;
			String gno = null;
			String nbase = null;
			String a0100 = null;

			// 获取考勤期间
			String kqDuration = RegisterDate.getKqDuration(this.frameconn);
			
			//获取考勤期间起止日期
			ArrayList list1 = RegisterDate.getKqDayList(this.frameconn);
			String ksdate = "";
			String jsdate = "";
			if (list1 != null && list1.size() > 0) {
				ksdate = list1.get(0).toString(); // 考勤期间开始日期
				jsdate = list1.get(1).toString(); // 考勤期间结束日期
			}
			String start = ksdate.replaceAll("\\.", "-");
			String end = jsdate.replaceAll("\\.", "-");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			Date s = sdf1.parse(start);
			Date e = sdf1.parse(end);
			long ss = s.getTime();
			long ee = e.getTime();
			StringBuffer sqlq03z0 = new StringBuffer();
			cols = getActualColumnNum(sheet.getRow(0), priKeyColExist);//实际列数
			
			// zxj 20190418 从循环种提出来，减少重复查库次数，提高效率
			HashMap unitMap = (HashMap) getKqItmeUnit(fieldList);
			
			for (int j = 1; j < rows; j++) {
				ArrayList list = new ArrayList();
				row = sheet.getRow(j);
				if (row == null)
					continue;
				
				// 固定模板取nbase\a0100\q03z0值
				if (priKeyColExist) {
					Cell flagCol = row.getCell((short) 0);
					
					if (null == flagCol)
					    continue;
					
					switch (flagCol.getCellType()) {
					case Cell.CELL_TYPE_BLANK:
						throw new GeneralException("主键标识串列存在空数据，导入数据失败！");
					case Cell.CELL_TYPE_STRING:
						if (flagCol.getRichStringCellValue().toString().trim().length() == 0)
							throw new GeneralException("主键标识串列存在空数据，导入数据失败！");
					}

					String[] temp = flagCol.getStringCellValue().trim().split("\\|");
					if (temp.length != 3)
						continue;

					if (temp[0].trim().length() == 0 || temp[1].trim().length() == 0
							|| temp[2].trim().length() == 0)
						continue;

					nbase = temp[0];
					a0100 = temp[1];

					// if(Q03Z0==null){
					Q03Z0 = temp[2];
					// }
					if ("Q05".equals(tablename) && !(Q03Z0.length() == 7)) {
						throw new GeneralException("月汇总数据导入需要月汇总数据模板，请导入月汇总数据模板！");
					}
					
					if ("Q05".equals(tablename) && (Q03Z0.length() == 7)) {
						if (!kqDuration.equals(Q03Z0)) {
							continue;
						}
					}
					
					if (("Q03".equals(tablename)) && (Q03Z0.length() == 7)) {
						throw new GeneralException("日明细数据导入需要日明细数据模板，请导入日明细数据模板！");
					}
					
					if ("Q03".equals(tablename) && !(Q03Z0.length() == 7)) {
						String q03z0 = Q03Z0.replaceAll("\\.", "-");
						Date q03 = sdf1.parse(q03z0);
						long date = q03.getTime();
						if (date < ss || date > ee) {
							continue;
						}
					}
				} else {
					// 自定义模板
					// 取日期列
					if ("Q05".equals(tablename)) {
						throw new GeneralException("月汇总数据导入需要月汇总数据模板，请导入月汇总数据模板！");
					}
					
					Cell q03z0Col = row.getCell(0);
					if (q03z0Col == null)
						continue;

					switch (q03z0Col.getCellType()) {
					case Cell.CELL_TYPE_STRING:
						String q03z0 = q03z0Col.getStringCellValue().toString().trim().replaceAll(
								"\\.", "-");
						Date q = sdf1.parse(q03z0);
						long qq = q.getTime();
						if (qq >= ss && qq <= ee) {
							Q03Z0 = q03z0.replaceAll("-", ".");
							break;
						} else {
							continue;
						}
					case 0:
						Date d = q03z0Col.getDateCellValue();
						long dd = d.getTime();
						if (dd >= ss && dd <= ee) {
							SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
							String datestr = sdf2.format(d);
							Q03Z0 = datestr.replaceAll("-", ".");
							break;
						} else {
							continue;
						}
					default:
						continue;
					}

					if ("".equals(Q03Z0))
						throw new GeneralException("日期列存在空数据，导入数据失败！");

					// 取工号数据
					Cell gnoCol = row.getCell(1);
					if (gnoCol == null)
						continue;

					switch (gnoCol.getCellType()) {
					case Cell.CELL_TYPE_STRING:
						gno = gnoCol.getStringCellValue().toString().trim();
						break;
					case Cell.CELL_TYPE_NUMERIC:
						gno = NumberToTextConverter.toText(gnoCol.getNumericCellValue());
					default:
						break;
					}

					if ("".equals(gno))
						throw new GeneralException("工号列存在空数据，导入数据失败！");

					// 根据工号取nbase,a0100
					String[] empInfo = findEmpInfoByGNO(dbMap, gnoField, gno);
					if ("".equals(empInfo[0]))
						continue;

					nbase = empInfo[0];
					a0100 = empInfo[1];
				}
				
				short initCol = 1;
				for (short c = initCol; c < cols; c++) {
				    String fieldItems = (String) map.get(new Short(c));
				    if (fieldItems == null)// 过滤掉只读的列
				        continue;
				    
				    String[] fieldItem = fieldItems.split(":");
				    String field = fieldItem[0];
				    String fieldName = fieldItem[1];
				    String itemtype = DataDictionary.getFieldItem(field).getItemtype();
				    String codesetid = DataDictionary.getFieldItem(field).getCodesetid();
				    int decwidth = DataDictionary.getFieldItem(field).getDecimalwidth();
				    if (!allowedImportItem(field, itemtype)) {
				        continue;
				    }
				    
					Cell cell1 = row.getCell(c);
					if (cell1 != null) {
						String value = "";
						
						switch (cell1.getCellType()) {
						case Cell.CELL_TYPE_FORMULA:
							if ("N".equalsIgnoreCase(itemtype)) {
								double y = cell1.getNumericCellValue();
								value = Double.toString(y);
								value = PubFunc.round(value, decwidth);
								checkKqDailyData(DataDictionary.getFieldItem(field), value, tablename, unitMap, fieldName, j);
								list.add(new Double((PubFunc.round(value, decwidth))));
							}
							break;
						case Cell.CELL_TYPE_NUMERIC:
							double y = cell1.getNumericCellValue();
							value = Double.toString(y);
							value = PubFunc.round(value, decwidth);
							checkKqDailyData(DataDictionary.getFieldItem(field), value, tablename, unitMap, fieldName, j);
							list.add(new Double((PubFunc.round(value, decwidth))));
							break;
						case Cell.CELL_TYPE_STRING:
							value = cell1.getRichStringCellValue().toString();
							if (!"N".equals(itemtype)) {
								if (!"nbase".equalsIgnoreCase(field)) {
									if (!"0".equals(codesetid) && !"".equals(codesetid)){
										if (codeColMap.get(codesetid + "a04v2u"	+ value.trim()) != null)
											value = (String) codeColMap.get(codesetid + "a04v2u" + value.trim());
										else
											value = null;
									}else if ("D".equals(itemtype)) {
										value = "".equals(value) ? null : value;
									}
								} else {
									value = dbMap.get(value) == null ? "" : (String) dbMap.get(value);
								}
								checkKqDailyData(DataDictionary.getFieldItem(field), value, tablename, unitMap, fieldName, j);
								list.add(value);
							} else if ("N".equals(itemtype)) {
								if (value.trim().length() == 0) {
									value = null;
									list.add(value);
								} else {
									if (!value.matches("\\d*")) {
										String msg = "源数据(" + fieldName + ")中数据:" + value + " 不符合格式！";
										throw new GeneralException(msg);
									} else {
										value = PubFunc.round(value, decwidth);
										checkKqDailyData(DataDictionary.getFieldItem(field), value, tablename, unitMap, fieldName, j);
										list.add(new Double(value));
									}
								}
							}
							break;
							
						case Cell.CELL_TYPE_BLANK:// 如果什么也不填的话数值就默认更新为0
							if ("N".equals(itemtype)) {
								value = PubFunc.round(value, decwidth);
								checkKqDailyData(DataDictionary.getFieldItem(field), value, tablename, unitMap, fieldName, j);
								list.add(new Double(value));
							} else
								list.add(null);
							break;
						default:
							list.add(null);
						}
						
						String msg = "";
						if ("N".equals(itemtype) || "D".equals(itemtype)) {
							if (value != null && !"".equalsIgnoreCase(value)
									&& !this.isDataType(decwidth, itemtype, value)) {
								msg = "源数据(" + fieldName + ")中数据:" + value + " 不符合格式！";
								throw new GeneralException(msg);
							}
						}
					} else {
						list.add(null);
					}
				}

				a0100s.append("'" + nbase + a0100 + "',");
				if ("Q05".equals(tablename)) {
					Q03Z0.substring(0, 6);
				}
				list.add(nbase);
				list.add(a0100);
				list.add(Q03Z0);
				list2.add(list);
				sqlq03z0.append("'" + Q03Z0 + "'" + ",");
			}
			if (sqlq03z0.length() > 0) {
				sqlq03z0.deleteCharAt(sqlq03z0.length() - 1);

			}
			if (updateFidsCount == 0)
				return;
			
			//第一步，创建存放excel数据的临时表
			int outSideCount = 0; 
			int readOnlyCount = 0;
			int importRecCount = 0;
			KqDBHelper kqDBHelper = new KqDBHelper(this.getFrameconn()); 
			String tempTab = kqDBHelper.createTempTab("Q03", this.userView.getUserName());
			try {
    			if ("".equals(tempTab))
    			    throw new GeneralException("创建临时表失败，无法导入！");
    			
    			//第二步，插入excel数据到临时表
    		    StringBuffer insertSQL = new StringBuffer();
    		    insertSQL.append("INSERT INTO " + tempTab + "(");
    		    
    		    StringBuffer insertSQLValues = new StringBuffer();
    		    insertSQLValues.append(" VALUES(");
    		    
    		    for (int i = 0; i < fieldList.size(); i++) {
    		        String aField = (String)fieldList.get(i);
    		        insertSQL.append(aField + ","); 
    		        insertSQLValues.append("?,");
    		    }
    		    
    		    insertSQL.append("nbase,a0100,q03z0)");
    		    insertSQLValues.append("?,?,?)");
    		    
    		    insertSQL.append(insertSQLValues.toString());
    			dao.batchInsert(insertSQL.toString(), list2);
    			
    			//第三步，删除临时表中管理范围外的记录
    		    String whr = RegisterInitInfoData.getKqEmpPrivWhr(this.frameconn, this.userView, tempTab);
    		    outSideCount = list2.size() - kqDBHelper.getRecordCount(tempTab, whr);
    			if (0 < outSideCount) {
    			    dao.update("DELETE FROM " + tempTab + " WHERE NOT (" + whr + ")");
    			}
    			
    		    //第四步，删除临时表中在日明细中对应的记录为非起草和驳回以及不存在的数据
    			StringBuffer readOnlyWhr = new StringBuffer();
    			readOnlyWhr.append(" NOT EXISTS(SELECT 1 FROM " + tablename + " A"); 
    			readOnlyWhr.append(" WHERE A.nbase=" + tempTab + ".nbase");
    			readOnlyWhr.append(" AND A.a0100=" + tempTab + ".a0100"); 
    			readOnlyWhr.append(" AND A.q03z0=" + tempTab + ".q03z0");
    			// 只能修改审批状态为起草（01）和驳回（07）的记录	 20181012 linbz 增加审批标识为空的情况
    			readOnlyWhr.append(" and (A.Q03Z5 in ('01','07') or A.Q03Z5 IS NULL ");
    	        if (Constant.MSSQL == Sql_switcher.searchDbServer()) 
    	        	readOnlyWhr.append(" or A.Q03Z5='' ");
    	        readOnlyWhr.append(")");
    	        
    			if ("q03".equalsIgnoreCase(tablename)) {
    			    readOnlyWhr.append(" AND A.q03z0>='" + list1.get(0).toString() + "'");
    			    readOnlyWhr.append(" AND A.q03z0<='" + list1.get(1).toString() + "'");
    			} else {
    			    readOnlyWhr.append(" AND A.q03z0='" + kqDuration + "'");
    			}
    			readOnlyWhr.append(")");
    			
    			readOnlyCount = kqDBHelper.getRecordCount(tempTab, readOnlyWhr.toString());
    			if (0 < readOnlyCount) {
    			    dao.update("DELETE FROM " + tempTab + " WHERE" + readOnlyWhr.toString());
    			}
    		
    			importRecCount = list2.size() - outSideCount - readOnlyCount;
    			//没有数据可导入直接抛出异常
    			if (0 >= importRecCount)
    			    throwImportErrorException(priKeyColExist, "Q03".equalsIgnoreCase(tablename));
    			
    			if (a0100s.length() == 0)
    			    throwImportErrorException(priKeyColExist, "Q03".equalsIgnoreCase(tablename));
			} catch(Exception ex) {
			    throw ex;
			} finally {
			    DbWizard dbWizard = new DbWizard(this.getFrameconn());
		        if(dbWizard.isExistTable(tempTab,false))
		        {
		            dbWizard.dropTable(tempTab);
		        }
			}
			
			a0100s.setLength(a0100s.length() - 1);
			StringBuffer buf = new StringBuffer("select * from " + tablename + " where nbase"
					+ SqlDifference.getJoinSymbol() + "a0100 in (" + a0100s.toString()
					+ ") and Q03Z0 in (" + sqlq03z0 + ")");
			int count = 0;
			if (sql.length() > x) {
				buf.append(" and  not " + sql.substring(x, sql.length())
						+ " and Q03Z5 in ('01','07')");
				/*
				 * RowSet rs = dao.search(buf.toString()); String errorInfo
				 * = "一些人员数据导入失败！可能由于数据正在审批或者没有修改人员的权限。 请检查一下人员：\n";
				 * //找出不可以导入的人员的名字 ArrayList list3 = new ArrayList();
				 * ArrayList list4 = new ArrayList(); while (rs.next()) {
				 * list3.add(rs.getString("a0101")); count++; if (count % 10
				 * == 0) errorInfo += "\n"; } for(int
				 * j=0;j<list3.size();j++){
				 * if(!list4.contains(list3.get(j))){
				 * list4.add(list3.get(j)); } } for(int m =0;m <
				 * list4.size();m++){ errorInfo +=list4.get(m) + "  "; }
				 */

				// if (count > 0 && count <= c){
				// errorInfo +="<br>成功导入人员数据条数为："+(c-count)+"条！";
				// throw new GeneralException(errorInfo);
				// }
				/*
				 * RowSet rs1 = dao.search(buf.toString());
				 * while(rs1.next()){ throw new
				 * GeneralException(errorInfo+"<br>请检查后再重新导入数据！"); }
				 */
			}
			// 只能修改审批状态为起草（01）和驳回（07）的记录	 20181012 linbz 增加审批标识为空的情况
			sql.append(" and (Q03Z5 in ('01','07') or Q03Z5 IS NULL ");
	        if (Constant.MSSQL == Sql_switcher.searchDbServer()) 
                sql.append(" or Q03Z5='' ");
	        sql.append(")");
				        
			int j[] = dao.batchUpdate(sql.toString(), list2);
			String success = "";
			int l = 0;
			for (int k = 0; k < j.length; k++) {
				success = success + String.valueOf(j[k]);
				if (j[k] == -2 || j[k] == 1)
					l = l + 1;
			}
			if (count == 0 && l > 0) {
			    String msg = "导入完成！共导入数据" + importRecCount + "条。";
			    if (outSideCount > 0)
			        msg = msg + outSideCount + "条管理范围外的数据未导入。";
			    if (readOnlyCount > 0)
			        msg = msg + readOnlyCount + "条非起草或驳回数据未导入。";
				this.getFormHM().put("msg", msg);
			} else {
				throwImportErrorException(priKeyColExist, "Q03".equalsIgnoreCase(tablename));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeIoResource(wb);
			PubFunc.closeIoResource(in);
		}
	}
	
	private void throwImportErrorException(boolean isDefaultTemplate, boolean isDailyData) throws GeneralException {
        String errorInfo = ResourceFactory.getProperty("kq.dailydata.import.hint");
        if (isDailyData) {
            errorInfo = errorInfo 
                      + "<br>&nbsp;&nbsp;" + ResourceFactory.getProperty("kq.dailydata.import.hint1")
                      + "<br>&nbsp;&nbsp;" + ResourceFactory.getProperty("kq.dailydata.import.hint2")
                      + "<br>&nbsp;&nbsp;工号填写是否正确，人员是否为当前的管理范围" ;
            
        } else {
            errorInfo = errorInfo 
                      + "<br>&nbsp;&nbsp;" + ResourceFactory.getProperty("kq.collectdata.import.hint1")
                      + "<br>&nbsp;&nbsp;" + ResourceFactory.getProperty("kq.collectdata.import.hint2");
        }
        throw new GeneralException(errorInfo);
	}
	
	/**
	 * 可以导入的指标
	 * @Title: canImportItem   
	 * @Description:    
	 * @param itemId
	 * @param itemType
	 * @return
	 */
	private boolean allowedImportItem(String itemId, String itemType) {
	    return !isDefaultReadOnlyItem(itemId) && !isMainSetItem(itemType, itemId);
	}

	/**
	 * 是默认不允许修改的指标
	 * @Title: isDefaultReadOnlyItem   
	 * @Description:    
	 * @param itemId
	 * @return
	 */
	private boolean isDefaultReadOnlyItem(String itemId) {
	    return ",b0110,e01a1,e0122,a0100,a0101,nbase,q03z0".contains(itemId.toLowerCase());
	}
	
	/**
	 * 根据指标类型和编号，判断是否为主集指标 
	 * @param itemtype
	 * @param itemid
	 * @return
	 */
	private boolean isMainSetItem(String itemType, String itemId) {
	    // zxj 20190418 改为从内存字典取数，减少查库次数，提高效率
		FieldItem item = DataDictionary.getFieldItem(itemId, "A01");
		if (item == null) 
		    return false;
		
		if (!item.getItemtype().equalsIgnoreCase(itemType)) 
		    return false;
	
		return true;
	}

	/**
	 * 判断 值类型是否与 要求的类型一致
	 * 
	 * @param columnBean
	 * @param itemid
	 * @param value
	 * @return
	 */
	private boolean isDataType(int decwidth, String itemtype, String value) {

		boolean flag = true;
		if ("N".equals(itemtype)) {
			if (decwidth == 0) {
				flag = value.matches("^[+-]?[\\d]+$");
			} else {
				flag = value.matches("^[+-]?[\\d]*[.]?[\\d]+");
			}
		} 
		return flag;
	}
	/**
	 * 判断值是否是有效的日明细数据
	 * 
	 * @param columnBean
	 * @param itemid
	 * @param value
	 * @return
	 * @throws GeneralException 
	 */
	private void checkKqDailyData(FieldItem fieldItem, String value, String table, HashMap unitMap, String item_name, int rowNum) throws GeneralException {
		String mess = "";
		String itemid = fieldItem.getItemid();
		String itemtype = fieldItem.getItemtype();
		int itemlength = fieldItem.getItemlength();
		ArrayList daylist = RegisterDate.getKqDurationList(frameconn);
		int day = 0;
		if (daylist != null && daylist.size() > 0) 
		{
			day = daylist.size();
		}
		String item_unit = (String) unitMap.get(itemid);
		if ("N".equals(itemtype)) {
			float unit = Float.parseFloat(value);
			if ("01".equals(item_unit)) {//单位为小时
				if ("q03".equalsIgnoreCase(table) && unit > 24) {
					mess = "第" + rowNum + "行“" + item_name + "”指标单位为小时，输入的值大于24小时！";
				}else if ("q05".equalsIgnoreCase(table) && unit > (24 * day)) 
				{
					mess = "第" + rowNum + "行“" + item_name + "”指标单位为小时，输入的值大于" + (day * 24) + "小时！";
				}
			} else if ("03".equals(item_unit)) {//单位为分钟
				if ("q03".equalsIgnoreCase(table) && unit > 1440) {
					mess = "第" + rowNum + "行“" + item_name + "”指标单位为分钟，输入的值大于1440分钟！";
				}else if ("q05".equalsIgnoreCase(table) && unit > (1440 * day)) 
				{
					mess = "第" + rowNum + "行“" + item_name + "”指标单位为分钟，输入的值大于" + 1440 * day + "分钟！";
				}
			} else if ("02".equals(item_unit)) {//单位为天
				if ("q03".equalsIgnoreCase(table) && unit > 1) {
					mess = "第" + rowNum + "行“" + item_name + "”指标单位为天，输入的值大于1天！";
				}else if ("q05".equalsIgnoreCase(table) && unit > day) 
				{
					mess = "第" + rowNum + "行“" + item_name + "”指标单位为天，输入的值大于" + day + "天！";
				}
			} else if ("04".equals(item_unit)) {//单位为次

			}
		} else if ("D".equals(itemtype) && value != null && value.length() > 0) {
			try {
				value = value.replace(".", "-");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				sdf.setLenient(false);//严格的时间格式控制
				if (itemlength == 10 ) 
				{
					sdf.parse(value).getTime();
				}else if(itemlength == 7)
				{
					sdf.parse(value + "-01");
				}else if (itemlength == 4) 
				{
					sdf.parse(value + "-01-01");
				}else
				{
					sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					sdf.setLenient(false);
					sdf.parse(value);
				}
			} catch (ParseException e) {
				mess = "第" + rowNum + "行“" + item_name + "”指标日期格式不正确，请输入正确的日期格式！";
			}
		}

		if (mess.length() > 0) {
			throw new GeneralException(mess);
		}
	}
	
	/**
	 * 获取列头考勤规则指标的单位
	 * @param fieldList
	 * @return
	 */
	private Map getKqItmeUnit(ArrayList fieldList){
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer sql;
		try {
			for (int i = 0; i < fieldList.size(); i++) {
				String itemid = (String) fieldList.get(i);
				sql = new StringBuffer();
				sql.append("select item_name,item_unit from kq_item");
				sql.append(" where Upper(fielditemid)='" + itemid.toUpperCase() + "'");
				String item_unit = "";
				this.frowset = dao.search(sql.toString());
				if (this.frowset.next()) 
					item_unit = this.frowset.getString("item_unit");
				if (item_unit == null || item_unit.length() <= 0) {
					item_unit = "03";
				}
				map.put(itemid, item_unit);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return map;
	}
	private String getGNOField() {
		KqParameter para = new KqParameter(this.userView, "", this.frameconn);
		HashMap hashmap = para.getKqParamterMap();
		String g_no = ((String) hashmap.get("g_no")).toLowerCase();
		return g_no;
	}

	private String[] findEmpInfoByGNO(HashMap dbMap, String gnoField, String gno) {
		String[] empInfo = { "", "" };
		String nbase = "";
		String a0100 = "";

		Iterator iterator = dbMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry dbname = (Map.Entry) iterator.next();
			nbase = (String) dbname.getValue();
			a0100 = queryA0100FromDB(nbase, gnoField, gno);
			if (!"".equals(a0100)) {
				empInfo[0] = nbase;
				empInfo[1] = a0100;
				break;
			}
		}

		return empInfo;
	}

	private String queryA0100FromDB(String nbase, String gnoField, String gno) {
		String a0100 = "";
		String sql = "SELECT a0100 FROM " + nbase + "A01" + " WHERE " + gnoField + "=?";

		ArrayList ls = new ArrayList();
		ls.add(gno);

		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			rs = dao.search(sql, ls);
			if (rs.next())
				a0100 = rs.getString("a0100");

		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
			KqUtilsClass.closeDBResource(rs);
		}

		return a0100;
	}

	/**
	 * 获取实际列数
	 * 
	 * @param row
	 * @param priKeyColExist
	 * @return
	 */
	private int getActualColumnNum(Row row, boolean priKeyColExist) {
		int actualNum = 0;
		int physicalNum = row.getPhysicalNumberOfCells();
		if (physicalNum > 1) {
			for (int i = 0; i <= physicalNum; i++) {
				Cell cell = row.getCell((short) i);
				if (cell != null) {
					String title = "";
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_FORMULA:
						break;
					case Cell.CELL_TYPE_NUMERIC:
						double y = cell.getNumericCellValue();
						title = Double.toString(y);
						break;
					case Cell.CELL_TYPE_STRING:
						title = cell.getStringCellValue();
						break;
					default:
						title = "";
					}

					if ("".equals(title.trim())) {
						actualNum = i;
						break;
					}

				} else {
					actualNum = i;
					break;
				}
			}
		}
		return actualNum;
	}
}
