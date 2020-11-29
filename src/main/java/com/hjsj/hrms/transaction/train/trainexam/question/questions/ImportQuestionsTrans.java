package com.hjsj.hrms.transaction.train.trainexam.question.questions;

import com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo;
import com.hjsj.hrms.transaction.train.b_plan.MessBean;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImportQuestionsTrans extends IBusiness {
	private String privM = "b0110";
	private String privMv = "";

	public void execute() throws GeneralException {
		HashMap msgMap = new HashMap();
		ArrayList msglist = new ArrayList();
		String isupdate = "0";
		int[] counts = { 0, 0, 0, 0, 0 };
		try {
			this.initPriv();
			FormFile file = (FormFile) this.getFormHM().get("file");
			if(!FileTypeUtil.isFileTypeEqual(file))
			    throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.fileuploaderror")));
			
			Object[] maps = readExcel(file, msgMap, counts);
			this.getFormHM().put("maps", maps);

			HashMap fieldMap = (HashMap) maps[0];
			ArrayList valueList = (ArrayList) maps[1];
			String primarykeys = ((StringBuffer) maps[2]).toString();
			primarykeys = "','" + primarykeys;
			ArrayList keyList = (ArrayList) maps[3];
			StringBuffer a0100sb = (StringBuffer) maps[4];
			HashMap key2num = (HashMap) maps[5];

			String sql = "";

			if (keyList.size() < 1) {
				return;
			}

			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String primarykey = (String) keyList.get(0);
			// 过滤单位部门岗位是否正确
			this.doOrgCheck(valueList, dao);
			this.doUNUMKCheck(valueList, fieldMap, dao);

			// 检查代码型的字段从excel中读取的codeitemdesc值是否在库中有对应的codeitemid
			for (int n = 0; n < keyList.size(); n++) {
				String key = (String) keyList.get(n);
				FieldItem item = (FieldItem) fieldMap.get(key);
				if (item == null)
					continue;
				String itemid = item.getItemid();
				if ((item.getCodesetid() != null && !"".equals(item.getCodesetid()) && !"0".equals(item.getCodesetid())) && "A".equalsIgnoreCase(item.getItemtype())) {
					if ("UN".equalsIgnoreCase(item.getCodesetid()) || "UM".equalsIgnoreCase(item.getCodesetid()) || "@K".equalsIgnoreCase(item.getCodesetid())) {
						sql = "select codeitemdesc,codeitemid from organization where upper(codesetid)='" + item.getCodesetid().toUpperCase() + "' and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd")) + " between start_date and end_date";
					} else {
						sql = "select codeitemdesc,codeitemid from codeitem where upper(codesetid)='" + item.getCodesetid().toUpperCase() + "'";
					}
					this.frowset = dao.search(sql);
					HashMap codemap = new HashMap();
					while (this.frowset.next()) {
						codemap.put(this.frowset.getString("codeitemdesc"), this.frowset.getString("codeitemid"));
					}
					for (int m = 0; m < valueList.size(); m++) {
						HashMap valuemap = (HashMap) valueList.get(m);
						String primarykeyvalue = (String) valuemap.get(primarykey);
						String value = (String) valuemap.get(itemid);

						/*if (item.getItemid().equalsIgnoreCase("b0110")) {
							if (value == null || value.equals("")) {
								ArrayList sb = (ArrayList) msgMap
										.get(primarykeyvalue);
								if (sb != null) {
									int no = sb.size();
									sb
											.add((no + 1)
													+ ".&nbsp;"
													+ ResourceFactory
															.getProperty(
																	"workbench.info.import.error.nob0110")
															.substring(1));
								} else {
									sb = new ArrayList();
									sb
											.add("1.&nbsp;"
													+ ResourceFactory
															.getProperty(
																	"workbench.info.import.error.nob0110")
															.substring(1));
									msgMap.put(primarykeyvalue, sb);
								}
								valueList.remove(valuemap);
								primarykeys = primarykeys.replaceAll("','"
										+ primarykeyvalue + "','", "','");
								--m;
								counts[2] = counts[2] + 1;
								continue;
							}
						}*/
						if (value == null)
							continue;
						if ("e0122".equalsIgnoreCase(item.getItemid()) || "b0110".equalsIgnoreCase(item.getItemid()) || "e01a1".equalsIgnoreCase(item.getItemid())) {
							Pattern p = Pattern.compile("[A-Z0-9]*");
							Matcher ma = p.matcher(value);// 支持直接输入机构编码，可解决机构名重复的问题
							if (!codemap.containsKey(value) && !"".equals(value) && !ma.matches()) {
								valuemap.put(itemid, "```");
								ArrayList sb = (ArrayList) msgMap.get(primarykeyvalue);
								if ("b0110".equalsIgnoreCase(item.getItemid())) {
									if (sb != null) {
										int no = sb.size();
										sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("workbench.info.import.error.codenol") + value + ResourceFactory.getProperty("workbench.info.import.error.codenor") + ResourceFactory.getProperty("workbench.info.import.error.nob0110"));
									} else {
										sb = new ArrayList();
										sb.add("1.&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("workbench.info.import.error.codenol") + value + ResourceFactory.getProperty("workbench.info.import.error.codenor") + ResourceFactory.getProperty("workbench.info.import.error.nob0110"));
										msgMap.put(primarykeyvalue, sb);
									}
									valueList.remove(valuemap);
									primarykeys = primarykeys.replaceAll("','" + primarykeyvalue + "','", "','");
									--m;
									counts[2] = counts[2] + 1;
								} else {
									if (sb != null) {
										int no = sb.size();
										sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("workbench.info.import.error.codenol") + value + ResourceFactory.getProperty("workbench.info.import.error.codenor"));
									} else {
										sb = new ArrayList();
										sb.add("1.&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("workbench.info.import.error.codenol") + value + ResourceFactory.getProperty("workbench.info.import.error.codenor"));
										msgMap.put(primarykeyvalue, sb);
									}
								}
								msgMap.put(primarykeyvalue, sb);
							} else {
								if (!"".equals(value) && !ma.matches()) {
									String tmpvalue = (String) codemap.get(value);
									valuemap.put(itemid, tmpvalue);
									if (!"admin".equalsIgnoreCase(this.privM) && this.privM.equalsIgnoreCase(item.getItemid())) {
										if (this.privMv.indexOf("`") == -1) {
											if (tmpvalue.toUpperCase().indexOf(this.privMv) == -1) {
												ArrayList sb = (ArrayList) msgMap.get(primarykeyvalue);
												if (sb != null) {
													int no = sb.size();
													sb.add((no + 1) + ".&nbsp;" + ResourceFactory.getProperty("workbench.info.import.error.nopriv"));
												} else {
													sb = new ArrayList();
													sb.add("1.&nbsp;" + ResourceFactory.getProperty("workbench.info.import.error.nopriv"));
													msgMap.put(primarykeyvalue, sb);
												}
												valueList.remove(valuemap);
												primarykeys = primarykeys.replaceAll("','" + primarykeyvalue + "','", "','");
												--m;
												counts[3] = counts[3] + 1;
											}
										} else {
											String[] tmp = this.privMv.split("`");
											boolean flag = true;
											for (int i = tmp.length - 1; i >= 0; i--) {
												if (tmpvalue.toUpperCase().indexOf(tmp[i]) != -1) {
													flag = false;
													break;
												}
											}
											if (flag) {
												ArrayList sb = (ArrayList) msgMap.get(primarykeyvalue);
												if (sb != null) {
													int no = sb.size();
													sb.add((no + 1) + ".&nbsp;" + ResourceFactory.getProperty("workbench.info.import.error.nopriv"));
												} else {
													sb = new ArrayList();
													sb.add("1.&nbsp;" + ResourceFactory.getProperty("workbench.info.import.error.nopriv"));
													msgMap.put(primarykeyvalue, sb);
												}
												valueList.remove(valuemap);
												primarykeys = primarykeys.replaceAll("','" + primarykeyvalue + "','", "','");
												--m;
												counts[3] = counts[3] + 1;
											}
										}
									}
								} else if (!"".equals(value) && ma.matches()) {
									if (!"admin".equalsIgnoreCase(this.privM) && this.privM.equalsIgnoreCase(item.getItemid())) {
										if (this.privMv.indexOf("`") == -1) {
											if (value.toUpperCase().indexOf(this.privMv) == -1) {
												ArrayList sb = (ArrayList) msgMap.get(primarykeyvalue);
												if (sb != null) {
													int no = sb.size();
													sb.add((no + 1) + ".&nbsp;" + ResourceFactory.getProperty("workbench.info.import.error.nopriv"));
												} else {
													sb = new ArrayList();
													sb.add("1.&nbsp;" + ResourceFactory.getProperty("workbench.info.import.error.nopriv"));
													msgMap.put(primarykeyvalue, sb);
												}
												valueList.remove(valuemap);
												primarykeys = primarykeys.replaceAll("','" + primarykeyvalue + "','", "','");
												--m;
												counts[3] = counts[3] + 1;
											}
										} else {
											String[] tmp = this.privMv.split("`");
											boolean flag = true;
											for (int i = tmp.length - 1; i >= 0; i--) {
												if (value.toUpperCase().indexOf(tmp[i]) != -1) {
													flag = false;
													break;
												}
											}
											if (flag) {
												ArrayList sb = (ArrayList) msgMap.get(primarykeyvalue);
												if (sb != null) {
													int no = sb.size();
													sb.add((no + 1) + ".&nbsp;" + ResourceFactory.getProperty("workbench.info.import.error.nopriv"));
												} else {
													sb = new ArrayList();
													sb.add("1.&nbsp;" + ResourceFactory.getProperty("workbench.info.import.error.nopriv"));
													msgMap.put(primarykeyvalue, sb);
												}
												valueList.remove(valuemap);
												primarykeys = primarykeys.replaceAll("','" + primarykeyvalue + "','", "','");
												--m;
												counts[3] = counts[3] + 1;
											}
										}
									}
								}
							}
						} else if ("UN".equalsIgnoreCase(item.getCodesetid()) || "UM".equalsIgnoreCase(item.getCodesetid()) || "@K".equalsIgnoreCase(item.getCodesetid())) {
							Pattern p = Pattern.compile("[A-Z0-9]*");
							Matcher ma = p.matcher(value);// 支持直接输入机构编码，可解决机构名重复的问题
							if (!codemap.containsKey(value) && !"".equals(value) && !ma.matches()) {
								valuemap.put(itemid, "```");
								ArrayList sb = (ArrayList) msgMap.get(primarykeyvalue);
								if (sb != null) {
									int no = sb.size();
									sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("workbench.info.import.error.codenol") + value + ResourceFactory.getProperty("workbench.info.import.error.codenor"));
								} else {
									sb = new ArrayList();
									sb.add("1.&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("workbench.info.import.error.codenol") + value + ResourceFactory.getProperty("workbench.info.import.error.codenor"));
									msgMap.put(primarykeyvalue, sb);
								}
							} else {
								if (!"".equals(value) && !ma.matches()) {
									valuemap.put(itemid, codemap.get(value));
								}
							}
						} else {
							if (!codemap.containsKey(value) && !"".equals(value)) {
								valuemap.put(itemid, "```");
								ArrayList sb = (ArrayList) msgMap.get(primarykeyvalue);
								if (sb != null) {
									int no = sb.size();
									sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("workbench.info.import.error.codenol") + value + ResourceFactory.getProperty("workbench.info.import.error.codenor"));
								} else {
									sb = new ArrayList();
									sb.add("1.&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("workbench.info.import.error.codenol") + value + ResourceFactory.getProperty("workbench.info.import.error.codenor"));
									msgMap.put(primarykeyvalue, sb);
								}
							} else {
								if (!"".equals(value)) {
									valuemap.put(itemid, codemap.get(value));
								}
							}
						}
					}
				}
			}

			primarykeys = primarykeys.substring(3);
			ArrayList prList = new ArrayList();
			String ttt[] = primarykeys.split("','");
			if (ttt.length > 500) {
				StringBuffer sb = new StringBuffer();
				int n = 0;
				boolean f = false;
				for (int i = 0; i < ttt.length; i++) {
					if ("".equals(ttt[i])) {
						continue;
					}
					f = false;
					sb.append(ttt[i] + "','");
					if (n > 498) {
						f = true;
						prList.add(sb.toString());
						sb = new StringBuffer();
						n = 0;
					}
					n++;
				}
				if (!f)
					prList.add(sb.toString());
			} else {
				prList.add(primarykeys);
			}

			// 记录下已经存在的，暂时处理成直接不导入
			for (int m = 0; m < prList.size(); m++) {
				String tempp = (String) prList.get(m);
				if ("admin".equals(this.privM)) {
					sql = "select R5200," + primarykey + " from r52 where upper(" + primarykey + ") in('" + tempp.toUpperCase() + "##')";
				} else {
					sql = "select " + this.privM + ",5200," + primarykey + " from r52 where upper(" + primarykey + ") in('" + tempp.toUpperCase() + "##')";
				}
				this.frowset = dao.search(sql);
				while (this.frowset.next()) {
					String tmp = this.frowset.getString(primarykey);
					counts[4] = counts[4] + 1;
					a0100sb.append(tmp + ",");
					ArrayList sb = (ArrayList) msgMap.get(tmp);
					if (sb != null) {
						int no = sb.size();
						sb.add((no + 1) + ".&nbsp;" + ResourceFactory.getProperty("workbench.info.import.error.haveset"));
					} else {
						sb = new ArrayList();
						sb.add("1.&nbsp;" + ResourceFactory.getProperty("workbench.info.import.error.haveset"));
						msgMap.put(tmp, sb);
					}
					isupdate = "1";
				}
			}
			counts[0] = valueList.size() - counts[4];
			TreeSet list = new TreeSet();
			String key = "";

			for (Iterator i = msgMap.keySet().iterator(); i.hasNext();) {
				key = i.next().toString();
				// LazyDynaBean ldb= new LazyDynaBean();
				MessBean mb = new MessBean();
				if (!"".equals(key)) {
					if (null != key2num.get(key) && !"".equals(key2num.get(key).toString())) {
						mb.setKey2num(key2num.get(key).toString());
						mb.setKeyid(key);
					}
					ArrayList sb = (ArrayList) msgMap.get(key);
					StringBuffer sbb = new StringBuffer();
					for (int n = 0; n < sb.size(); n++) {
						sbb.append("&nbsp;" + (String) sb.get(n) + "</br>");
					}
					mb.setContent(sbb.toString());
					if (null != mb) {
						list.add(mb);
					}
				}
			}

			for (Iterator i = list.iterator(); i.hasNext();) {
				msglist.add(i.next()); //页面提示信息
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			this.getFormHM().put("msglist", msglist);
			this.getFormHM().put("isupdate", isupdate);
			this.getFormHM().put("counts", counts);
		}
	}

	/**
	 * 读取excel数据放入集合对象
	 * 
	 * @param file
	 * @param hashMap
	 * @return
	 * @throws Exception
	 */
	private Object[] readExcel(FormFile file, HashMap msgMap, int[] counts) throws Exception {
		ArrayList valueList = new ArrayList();
		HashMap fieldMap = new HashMap();// key=//当前指标值：所在cell列数
		HashMap fieldIdex = new HashMap();
		StringBuffer keysb = new StringBuffer();
		ArrayList keyList = new ArrayList();
		StringBuffer a0100sb = new StringBuffer();
		HashMap key2num = new HashMap();// 对应excel多少行

		Object[] maps = { fieldMap, valueList, keysb, keyList, a0100sb, key2num };
		Workbook owb = null;
		Sheet osheet = null;
		ArrayList tlist = new ArrayList();// 记录12-20列的列名
		ArrayList valueMapList = new ArrayList();
		String temp = "";
		InputStream stream = null;
		try {
			stream = file.getInputStream();
			owb = WorkbookFactory.create(stream);
			osheet = owb.getSheetAt(0);
			Row orow = osheet.getRow(1);
			if (orow == null) {
				return maps;
			}
			int cols = orow.getPhysicalNumberOfCells();
			int rows = osheet.getPhysicalNumberOfRows();

			ArrayList list = DataDictionary.getFieldList("r52", Constant.USED_FIELD_SET);
			int colums = 0;
            for (int i = 0; i < list.size(); i++) {
                FieldItem item = (FieldItem) list.get(i);
                if (!("R5200".equalsIgnoreCase(item.getItemid()) || "R5201".equalsIgnoreCase(item.getItemid())//只显示表中部分列 其余的则不显示
                        || "R5208".equalsIgnoreCase(item.getItemid()) || "R5209".equalsIgnoreCase(item.getItemid()) || "R5214".equalsIgnoreCase(item.getItemid()) || "R5215".equalsIgnoreCase(item.getItemid()) || "R5217".equalsIgnoreCase(item.getItemid()) || "R5207".equalsIgnoreCase(item.getItemid()) || "create_time".equalsIgnoreCase(item.getItemid()) || "b0110".equalsIgnoreCase(item.getItemid()))) {
                    colums++;
                }
            }
            
			for (int c = 0; c < colums; c++) { //遍历有指标的列
				String itemid = "";
				Cell cell = orow.getCell(c);
				if (cell != null) {
					itemid = cell.getCellComment().getString().getString().toLowerCase();
				}

				String t[] = itemid.split("`");

				itemid = t[0];
				if ("".equals(itemid))
					break;
				FieldItem item = DataDictionary.getFieldItem(itemid);
				if (item == null) {
					continue;
				}

				fieldIdex.put(itemid, new Integer(c));
				fieldMap.put(itemid, item);// 当前指标值：所在cell列数
				keyList.add(itemid);
			}

			int num = 0 ; //动态获取选项列长度
			for(int k = 0 ; k < cols ; k ++){
				Cell cell = orow.getCell(k);
				if(null != cell && !"".equals(cell.getStringCellValue().trim())){
					num++;
				}
			}
			// 取出列名 放入集合
			for (int d = 10; d < num; d++) {
				Cell cell = orow.getCell(d);
				
				if (null != cell && !"".equals(cell)) {
					if (null != cell.getStringCellValue().trim() && !"".equals(cell.getStringCellValue().trim())) {
						tlist.add(cell.getStringCellValue().trim());
					}
				}
			}

			this.getFormHM().put("tlist", tlist);

			cols = keyList.size();
			String value = "";
			double dvalue = 0;
			String sss = "";
			DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
			ArrayList dlist = new ArrayList();
			QuestionesBo bo = new QuestionesBo(this.frameconn);
			for (int j = 2; j < rows; j++) { // 遍历行
				orow = osheet.getRow(j);

				if (orow != null) {
					HashMap valueMap = new HashMap();// key=指标itemid value=值
					HashMap value2Map = new HashMap();
					String primarykeyValue = "";
					// ll:
					// double a =cell2.getNumericCellValue();
					// String str = NumberFormat.getNumberInstance().format(a);
					// String ve=String.valueOf((str));
					// System.out.println("VEEEE:"+ve);
					for (int n = 0; n < cols; n++) { // 每一行的列
						String key = (String) keyList.get(n); // 所有列的指标
						FieldItem item = (FieldItem) fieldMap.get(key);// 通过指标得到详细
						int idex = ((Integer) fieldIdex.get(key)).intValue();// 从0开始到8
						// 0:第一列
						
						Cell cell = orow.getCell(idex);

						//if (cell != null) {
							switch (cell.getCellType()) {
							case Cell.CELL_TYPE_FORMULA:
								break;
							case Cell.CELL_TYPE_NUMERIC: {
								if ("D".equals(item.getItemtype())) {
									Date d = cell.getDateCellValue();

									try {
										value = formater.format(d);
										valueMap.put(key, value);
									} catch (Exception e) {
										ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
										if (sb != null) {
											sb.add((j + 1) + ".&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("workbench.info.import.error.datetype") + cell.getNumericCellValue());
										} else {
											sb = new ArrayList();
											sb.add((j + 1) + ".&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("workbench.info.import.error.datetype") + cell.getNumericCellValue());
										}
										msgMap.put(primarykeyValue, sb);
									}
									break;
								} else {
									dvalue = cell.getNumericCellValue();
									String str = NumberFormat.getNumberInstance().format(dvalue);

									while (str.indexOf(",") > -1) {
										str = str.substring(0, str.indexOf(",")) + str.substring(str.indexOf(",") + 1);
									}
									Pattern p = Pattern.compile("[+-]?[\\d.]*");
									Matcher m = p.matcher(String.valueOf(str));
									if (!m.matches()) {
										ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);

										if (sb != null) {
											sb.add((j + 1) + ".&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("workbench.info.import.error.inttype") + str);
										} else {
											sb = new ArrayList();
											sb.add((j + 1) + ".&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("workbench.info.import.error.inttype") + str);
										}
										msgMap.put(primarykeyValue, sb);
									} else {
										value = String.valueOf((str));
										if ("N".equals(item.getItemtype())) {
											int dw = item.getDecimalwidth();

											if (dw == 0) {
												if (value.indexOf('.') != -1)
													value = value.substring(0, value.indexOf('.'));
											} else {
												if (value.indexOf('.') != -1) {
													String dec = value.substring(value.indexOf('.') + 1);
													if (dec.length() > dw)
														value = value.substring(0, value.indexOf('.') + dw + 1);
												}
											}
											value = value.replaceAll("\\+", "");
										}
										if (value.length() > 9)
											valueMap.put(key, null);
										else
											valueMap.put(key, value);
									}
									break;
								}
							}
							case Cell.CELL_TYPE_STRING: {
								value = cell.getStringCellValue().trim();
								if (n == 0) {
									primarykeyValue = cell.getStringCellValue().trim();
									if (value == null || "".equals(value)) {// 主键为空就不提取此行记录
										break;
									}
									keysb.append(primarykeyValue + "','");
								}
								// 得到文本的值 并且存入到方法中								
								if ("N".equals(item.getItemtype()) && !"r5203".equals(item.getItemid()) && !"type_id".equals(item.getItemid())) {
									Pattern p = Pattern.compile("[+-]?[\\d.]*");
									Matcher m = p.matcher(value);
									if (!m.matches()) {
										ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);

										if (sb != null) {
											sb.add((j + 1) + ".&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("workbench.info.import.error.inttype") + value);
										} else {
											sb = new ArrayList();
											sb.add((j + 1) + ".&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("workbench.info.import.error.inttype") + value);
										}
										msgMap.put(primarykeyValue, sb);
									} else {
										if ("N".equals(item.getItemtype())) {
											int dw = item.getDecimalwidth();
											if (dw == 0) {
												if (value.indexOf('.') != -1)
													value = value.substring(0, value.indexOf('.'));
											} else {
												if (value.indexOf('.') != -1) {
													String dec = value.substring(value.indexOf('.') + 1);
													if (dec.length() > dw)
														value = value.substring(0, value.indexOf('.') + dw + 1);
												}
											}
											if("r5213".equalsIgnoreCase(item.getItemid())){
												if("".equalsIgnoreCase(value)){
													ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
													if (sb != null) {
														sb.add((j + 1) + ".&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("edit_report.parameter.checkempty"));
													} else {
														sb = new ArrayList();
														sb.add((j + 1) + ".&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("edit_report.parameter.checkempty"));
													}
													msgMap.put(primarykeyValue, sb);
												}
											}else{
												value = value.replaceAll("\\+", "");
											}
										}
										if (value.length() > 9)
											value = null;
									}
								}
								if ("D".equals(item.getItemtype())) {
									if (!"".equals(value)) {
										ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
										if (sb != null) {
											sb.add((j + 1) + ".&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("workbench.info.import.error.datetype") + value);
										} else {
											sb = new ArrayList();
											sb.add((j + 1) + ".&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("workbench.info.import.error.datetype") + value);
										}
										msgMap.put(primarykeyValue, sb);
									}
								}
							}
							if("".equals(value.trim()) || null == value){
								if (item.isFillable()) { // 判断必填项是否为空
									ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
									if (sb != null) {
										sb.add((j + 1) + ".&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("edit_report.parameter.checkempty"));
									} else {
										sb = new ArrayList();
										sb.add((j + 1) + ".&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("edit_report.parameter.checkempty"));
									}
									msgMap.put(primarykeyValue, sb);
									if (!"".equals(item.getItemdesc())) {
										temp = primarykeyValue;
										int jj = j + 1;
										sss = jj + "";
										primarykeyValue = "";
									}
								}
							}else{
								valueMap.put(key, value);}
								break;
							default:
								//System.out.print(item.getItemdesc() + item.getItemid());
								if (item.isFillable()) { // 判断必填项是否为空
									ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
									if (sb != null) {
										sb.add((j + 1) + ".&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("edit_report.parameter.checkempty"));
									} else {
										sb = new ArrayList();
										sb.add((j + 1) + ".&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("edit_report.parameter.checkempty"));
									}
									msgMap.put(primarykeyValue, sb);
									if (!"".equals(item.getItemdesc())) {
										temp = primarykeyValue;
										int jj = j + 1;
										sss = jj + "";
										primarykeyValue = "";
									}
								}else if("r5213".equalsIgnoreCase(item.getItemid())){//因为考试得分是默认为必填项的 无需判断 所以此处必填项为空的判断加上一个考试得分的判断
									ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
									if (sb != null) {
										sb.add((j + 1) + ".&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("edit_report.parameter.checkempty"));
									} else {
										sb = new ArrayList();
										sb.add((j + 1) + ".&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("edit_report.parameter.checkempty"));
									}
									msgMap.put(primarykeyValue, sb);
									if (!"".equals(item.getItemdesc())) {
										temp = primarykeyValue;
										int jj = j + 1;
										sss = jj + "";
										primarykeyValue = "";
									}
								}
							}
						}

			//		}
					
					//知识点的问题  暂时先做此处理 知识点为空给出提示 并且不进行导入
					Cell cells = orow.getCell(colums);
					if(null != cells){
						if(null != cells.getStringCellValue() && !"".equalsIgnoreCase(cells.getStringCellValue().trim())){
							boolean flag = true;
							ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
							String[] str = cells.getStringCellValue().split(",");
							for (int k = 0; k < str.length; k++) {
								String sid = bo.getCodeitemId(str[k].trim()); // 通过知识点名称得到ID
								if(sid == null || sid.length()<1){
									if (sb != null) {
										sb.add((j + 1) + ".&nbsp;[" + "知识点" + "]&nbsp;&nbsp;" +str[k].trim()+"&nbsp;&nbsp;"+ResourceFactory.getProperty("constant.e_factornoexist"));
									} else {
										sb = new ArrayList();
										sb.add((j + 1) + ".&nbsp;[" + "知识点" + "]&nbsp;&nbsp;" +str[k].trim()+"&nbsp;&nbsp;"+ResourceFactory.getProperty("constant.e_factornoexist"));
									}
									flag = false;
								}
									
							}
							if(flag)
							   dlist.add(cells.getStringCellValue());
							else
								msgMap.put(primarykeyValue, sb);
						}else{
							ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
							if (sb != null) {
								sb.add((j + 1) + ".&nbsp;[" + "知识点" + "]" + ResourceFactory.getProperty("edit_report.parameter.checkempty"));
							} else {
								sb = new ArrayList();
								sb.add((j + 1) + ".&nbsp;[" + "知识点" + "]" + ResourceFactory.getProperty("edit_report.parameter.checkempty"));
							}
							msgMap.put(primarykeyValue, sb);
						}
					}else{
						ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
						if (sb != null) {
							sb.add((j + 1) + ".&nbsp;[" + "知识点" + "]" + ResourceFactory.getProperty("edit_report.parameter.checkempty"));
						} else {
							sb = new ArrayList();
							sb.add((j + 1) + ".&nbsp;[" + "知识点" + "]" + ResourceFactory.getProperty("edit_report.parameter.checkempty"));
						}
						msgMap.put(primarykeyValue, sb);
					}
					
					for (int d = colums+2; d < num; d++) {
						String key = "";
						if (d - (cols + 1) < (num-(cols+1))) {
							key = (String) tlist.get(d - (cols + 2));
						}

						Cell cell = orow.getCell(d-1);
						if (null != cell && !"".equals(cell)) {
							if (cell.getCellType() == 1) { //如果是文本格式的列 则按文本格式取值
								if (null != cell.getStringCellValue().trim() && !"".equals(cell.getStringCellValue().trim())) {
									value2Map.put(key, cell.getStringCellValue().trim());
								}
							} else if (cell.getCellType() == 0) {
								dvalue = cell.getNumericCellValue();
								String str = NumberFormat.getNumberInstance().format(dvalue);
								value2Map.put(key, str);
							}
						}
					}
					valueMapList.add(value2Map);
					if (!"".equals(primarykeyValue)) {// 必填项为空就不提取此行记录
						valueList.add(valueMap);
						key2num.put(primarykeyValue, "" + (j + 1));
					} else if ("".equals(primarykeyValue)) {
						key2num.put(temp, "" + sss);
						for (Iterator i = valueMap.keySet().iterator(); i.hasNext();) {
							String key = (String) i.next();
							if (valueMap.get(key) != null && valueMap.get(key).toString().length() > 0) {
								counts[1] = counts[1] + 1;
								break;
							}
						}
					}
				}
			}
			this.getFormHM().put("dlist", dlist);
			this.getFormHM().put("valueMapList", valueMapList);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workbench.info.import.error.excel")));
		}finally{
			PubFunc.closeIoResource(stream);
			PubFunc.closeResource(owb);
		}
		return maps;
	}

	private void initPriv() {
		if (this.userView.isSuper_admin()) {
			this.privM = "admin";
			return;
		}
		if (userView.getStatus() == 0) {
			String codeall = userView.getUnit_id();
			if (codeall != null && codeall.length() > 2) {
				codeall = PubFunc.getTopOrgDept(codeall);
				if ("UN`".equalsIgnoreCase(codeall)) {
					this.privM = "admin";
				} else {
					String tmp[] = codeall.split("`");
					StringBuffer codevalue = new StringBuffer();
					for (int i = tmp.length - 1; i >= 0; i--) {
						String t = tmp[i];
						if (t.indexOf("UN") != -1) {
							codevalue.append("`" + t.substring(2));
						}
					}
					this.privMv = codevalue.substring(1).toUpperCase();
				}
			} else {
				this.privMv = this.userView.getManagePrivCodeValue().toUpperCase();
			}
		} else {
			this.privMv = this.userView.getManagePrivCodeValue().toUpperCase();
		}
	}

	private void doOrgCheck(ArrayList valueList, ContentDAO dao) throws Exception {
		Pattern p = Pattern.compile("[A-Z0-9]*");
		for (int m = 0; m < valueList.size(); m++) {
			HashMap valuemap = (HashMap) valueList.get(m);
			String value = "";
			String b0110 = (String) valuemap.get("b0110");
			if (b0110 != null && b0110.endsWith(")")) {
				value = b0110.substring(b0110.lastIndexOf("(") + 1, b0110.lastIndexOf(")"));
				Matcher ma = p.matcher(value);
				if (ma.matches()) {
					if (AdminCode.getCodeName("UN", value).length() > 0) {
						valuemap.put("b0110", value);
						b0110 = value;
					} else {
						value = b0110.substring(0, b0110.lastIndexOf("("));
						valuemap.put("b0110", value);
						b0110 = value;
					}
				}
			}
			String e0122 = (String) valuemap.get("e0122");
			if (e0122 != null && e0122.endsWith(")")) {
				value = e0122.substring(e0122.lastIndexOf("(") + 1, e0122.lastIndexOf(")"));
				Matcher ma = p.matcher(value);
				if (ma.matches()) {
					if (AdminCode.getCodeName("UM", value).length() > 0) {
						valuemap.put("e0122", value);
						e0122 = value;
					} else {
						value = b0110.substring(0, b0110.lastIndexOf("("));
						valuemap.put("e0122", value);
						e0122 = value;
					}
				}
			}

			if (b0110 != null && !"".equals(b0110)) {
				if (e0122 != null && !"".equals(e0122)) {
					if (checkUNUM(b0110, e0122, dao)) {
						valuemap.put("e0122", "");
					}
				}
			} else {
				valuemap.put("e0122", "");
			}
		}
	}

	private void doUNUMKCheck(ArrayList valueList, HashMap fieldMap, ContentDAO dao) throws Exception {
		Pattern p = Pattern.compile("[A-Z0-9]*");
		for (int m = 0; m < valueList.size(); m++) {
			HashMap valuemap = (HashMap) valueList.get(m);
			String value = "";
			for (Iterator i = valuemap.keySet().iterator(); i.hasNext();) {
				String itemid = (String) i.next();
				if (!"b0110".equalsIgnoreCase(itemid) && !"e0122".equalsIgnoreCase(itemid) && !"e01a1".equalsIgnoreCase(itemid)) {
					FieldItem item = (FieldItem) fieldMap.get(itemid);
					if (item != null) {
						if ("UN".equalsIgnoreCase(item.getCodesetid()) || "UM".equalsIgnoreCase(item.getCodesetid()) || "@K".equalsIgnoreCase(item.getCodesetid())) {
							String b0110 = (String) valuemap.get(itemid);
							if (b0110 != null && b0110.endsWith(")")) {
								value = b0110.substring(b0110.lastIndexOf("(") + 1, b0110.lastIndexOf(")"));
								Matcher ma = p.matcher(value);
								if (ma.matches()) {
									if (AdminCode.getCodeName(item.getCodesetid(), value).length() > 0)
										valuemap.put(itemid, value);
									else {
										value = b0110.substring(0, b0110.lastIndexOf("("));
										valuemap.put(itemid, value);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean checkUNUM(String b0110, String e0122, ContentDAO dao) throws Exception {
		boolean flag = false;
		Pattern p = Pattern.compile("[A-Z0-9]*");
		Matcher ma = p.matcher(b0110);//支持直接输入机构编码，可解决机构名重复的问题
		String unCodeitemid = "";
		String umCodeitemid = "";
		if (ma.matches()) {
			unCodeitemid = b0110;
		} else {
			this.frecset = dao.search("select codeitemid from organization where codeitemdesc='" + b0110 + "' and codesetid='UN'");
			if (this.frecset.next()) {
				unCodeitemid = this.frecset.getString("codeitemid");
			} else {
				flag = true;
			}
		}
		ma = p.matcher(e0122);
		if (ma.matches()) {
			umCodeitemid = e0122;
		} else {
			this.frecset = dao.search("select codeitemid from organization where codeitemdesc='" + e0122 + "' and codesetid='UM'");
			if (this.frecset.next()) {
				umCodeitemid = this.frecset.getString("codeitemid");
			} else {
				flag = true;
			}
		}
		if (!"".equals(unCodeitemid) && !"".equals(umCodeitemid)) {
			flag = this.dochildchecknm(unCodeitemid, umCodeitemid, dao);
		}
		return flag;
	}

	

	private boolean dochildchecknm(String unCodeitemid, String umCodeitemid, ContentDAO dao) throws Exception {
		RowSet rs = null;
		boolean flag = true;
		String sql = "select codeitemid from organization where codesetid<>'@K' and parentid='" + unCodeitemid + "' and parentid<>codeitemid";
		try {
			rs = dao.search(sql);
			String codeitemid = "";
			while (rs.next()) {
				codeitemid = rs.getString("codeitemid");
				if (umCodeitemid.equalsIgnoreCase(codeitemid)) {
					flag = false;
					break;
				} else if (umCodeitemid.indexOf(codeitemid) != -1) {
					flag = this.dochildchecknm(codeitemid, umCodeitemid, dao);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		return flag;
	}

}
