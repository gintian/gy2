package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ImportTrainees extends IBusiness{

	public void execute() throws GeneralException {
		try {
			FormFile file = (FormFile) this.getFormHM().get("file");
			
			if(!FileTypeUtil.isFileTypeEqual(file))
			    throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.fileuploaderror")));
			
			readExcel(file);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	public void readExcel(FormFile file) throws Exception{
		HashMap fieldMap = new HashMap();// key=//当前指标值：所在cell列数
		HashMap msgMap = new HashMap();
		HashMap othermap = new HashMap(); //获取导入的其他信息集的信息
		HashMap othHm = new HashMap(); //获取非已批状态的学员
		//excel中存在的导入唯一性指标值
		ArrayList<String> excelKeys = new ArrayList<String>();
		String students = "";//获取导入的人员编号
		ArrayList keyList = new ArrayList();
		String r3101 = (String) this.formHM.get("r3101");
		ContentDAO dao = new ContentDAO(this.frameconn);
		Workbook wb = null;
		Sheet sheet = null;
		InputStream is = null;
		try {
			is = file.getInputStream();
			wb = WorkbookFactory.create(is);
			sheet = wb.getSheetAt(0);
			
			Row row = sheet.getRow(0);
			if(row == null){
				return;
			}
			
			int cols = row.getPhysicalNumberOfCells();
			int rows = sheet.getPhysicalNumberOfRows();
			
			String primarykey = "";
			boolean flag = false;
			int n=0;
			
			for (int c = 0; c < cols; c++) { // 遍历列指标
				String itemid = "";
				FieldItem item = new FieldItem();
				Cell cell = row.getCell(c);
				if (cell != null) {
					itemid = cell.getCellComment().getString().getString().toLowerCase();
				}

				String t[] = itemid.split("`");

				itemid = t[0];
				if ("".equals(itemid))
					break;
				
				if (t.length == 2) {
					primarykey = t[0];
					n = c;

					if (!"r4002".equalsIgnoreCase(primarykey))
						item = DataDictionary.getFieldItem(itemid, "a01");
					else
						item = DataDictionary.getFieldItem(itemid, "r40");
				} else
					item = DataDictionary.getFieldItem(itemid, "r40");
				
				if (item == null) {
					continue;
				}

				fieldMap.put(itemid, item); // 键值对
			
				keyList.add(itemid);

			}
			if(primarykey.length()<1 || primarykey == null)
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workbench.info.import.error.excel")));

			FieldItem fi = (FieldItem) fieldMap.get(primarykey);
			String primarykeyLabel = fi.getItemdesc();
			FieldItem fieldItemKey = DataDictionary.getFieldItem(primarykey, "a01");
			
			if(fieldItemKey == null || !"1".equalsIgnoreCase(fieldItemKey.getUseflag()))
				flag = true;
			
			cols = keyList.size();
			DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
			String value = "";
			List list = null;
			double dvalue = 0 ; 
			HashMap maps = null;
			HashMap m = new HashMap();
			ArrayList msg = new ArrayList();
			
			othermap = getOtherMap();
			othHm = getOthStudent(r3101);
			
			for (int i = 1; i < rows; i++) {
				int count = 0;
				String startTime = "";
				String endTime = "";
				row = sheet.getRow(i);
				if (row != null) {
					String s = "";
					list = new ArrayList();
					maps = new HashMap();
					String primarykeyValue = "";
					Cell cell = row.getCell(n);
					switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_FORMULA:
                            primarykeyValue = cell.getStringCellValue().trim();
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            primarykeyValue = String.valueOf(cell.getNumericCellValue()).trim();
                            primarykeyValue = PubFunc.round(primarykeyValue, fieldItemKey.getDecimalwidth());
                            break;
                        case Cell.CELL_TYPE_STRING:
                            primarykeyValue = cell.getStringCellValue().trim();
                            break;
                        default:
                            primarykeyValue = cell.getStringCellValue().trim();
                            break;
                    }
					
					primarykeyValue = primarykeyValue.replaceAll("['|‘|’]", "");// update  去掉单引号  影响sql
					if (primarykeyValue == null || "".equals(primarykeyValue)) {// 主键为空就不提取此行记录
						continue;
					}
					String Persons = getPersons(primarykey, primarykeyValue, dao);//判断按唯一性指标查询后是否有重复的数据
					if("0".equalsIgnoreCase(Persons)){
						ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
						if (sb != null) {
							int no = sb.size();
							sb.add((no + 1) + ".&nbsp;'" + primarykeyValue + ResourceFactory.getProperty("train.info.import.error.codenor"));
						} else {
							sb = new ArrayList();
							sb.add("1.&nbsp;'" + primarykeyValue + ResourceFactory.getProperty("train.info.import.error.codenor"));
						}
						msgMap.put(primarykeyValue, sb);
						primarykeyValue = null;
						continue;
					}
					if("2".equalsIgnoreCase(Persons)){
						ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
						if (sb != null) {
							int no = sb.size();
							sb.add((no + 1) + ".&nbsp;'" + primarykeyValue + "'" + ResourceFactory.getProperty("train.info.import.more.inttype"));
						} else {
							sb = new ArrayList();
							sb.add("1.&nbsp;'" + primarykeyValue + "'" + ResourceFactory.getProperty("train.info.import.more.inttype"));
						}
						msgMap.put(primarykeyValue, sb);
						primarykeyValue = null;
						continue;
					}
					String[] vPersons = Persons.split(",");
					String nbase = vPersons[0];
					String a0100 = vPersons[1];
					String a0101 = vPersons[2];
					String b0110 = vPersons[3];
					String e0122 = vPersons[4];
					
					if(othHm.containsKey(a0100)){
						ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
						if (sb != null) {
							int no = sb.size();
							sb.add((no + 1) + ".&nbsp;'" + primarykeyValue + "'" + ResourceFactory.getProperty("train.info.import.student.in")
											+ (String) othHm.get(a0100) + ResourceFactory.getProperty("train.info.import.student.exist"));
						} else {
							sb = new ArrayList();
							sb.add("1.&nbsp;'" + primarykeyValue + "'" + ResourceFactory.getProperty("train.info.import.student.in")
									+ (String) othHm.get(a0100) + ResourceFactory.getProperty("train.info.import.student.exist"));
						}
						msgMap.put(primarykeyValue, sb);
						primarykeyValue = null;
						continue;
					}

					if (excelKeys.contains(primarykeyValue)) {
						ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
						if (sb == null) {
							sb = new ArrayList();
						}
						int no = sb.size() + 1;
						sb.add(no + ".&nbsp;'" + primarykeyValue + "'" + ResourceFactory.getProperty("train.info.import.student.repeat.excel"));
						msgMap.put(primarykeyValue, sb);
						primarykeyValue = null;
						continue;
					}
					excelKeys.add(primarykeyValue);
					
					s = i + "|" + nbase + "," + a0100;

					for (int j = 0; j < cols; j++) {
						String key = (String) keyList.get(j); // 所有列的指标
						FieldItem item = (FieldItem) fieldMap.get(key);// 通过指标得到详细

						if((key.equalsIgnoreCase(primarykey) && flag) || "a0101".equalsIgnoreCase(key) || "a0100".equalsIgnoreCase(key))
							continue;
						
						cell = row.getCell(j);
						if (cell != null) {
							switch (cell.getCellType()) {
							case Cell.CELL_TYPE_FORMULA:
								break;
							case Cell.CELL_TYPE_NUMERIC:
								if ("D".equals(item.getItemtype())) {
									Date d = cell.getDateCellValue();
									try {
										String st = formater.format(d);
										if ("r4006".equalsIgnoreCase(item.getItemid())) {
											startTime = st;
										} else if ("r4007".equalsIgnoreCase(item.getItemid())) {
											endTime = st;
										}
										if (!"".equalsIgnoreCase(startTime) && !"".equalsIgnoreCase(endTime)) {
											long day = getQuot(endTime, startTime);
											if (day < 0) {
												ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
												if (sb != null) {
													int no = sb.size();
													sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc() + "]"
																	+ ResourceFactory.getProperty("开始时间要小于结束时间，") 
																	+ "开始:" + startTime + "结束:" + endTime);
												} else {
													sb = new ArrayList();
													sb.add("1..&nbsp;[" + item.getItemdesc() + "]"
																	+ ResourceFactory.getProperty("开始时间要小于结束时间，") 
																	+ "开始:" + startTime + "结束:" + endTime);
												}
												msgMap.put(primarykeyValue, sb);
												maps.put("r4006", "");
												maps.put(key, "");
											}
										}
										maps.put(key, st);
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else if ("A".equals(item.getItemtype()) && "a0110".equalsIgnoreCase(item.getItemid())) {
									ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
									if (sb != null) {
										int no = sb.size();
										sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc() + "]"
														+ ResourceFactory.getProperty("workbench.info.import.error.inttype") 
														+ cell.getNumericCellValue());
									} else {
										sb = new ArrayList();
										sb.add("1.&nbsp;[" + item.getItemdesc() + "]"
														+ ResourceFactory.getProperty("workbench.info.import.error.inttype") 
														+ cell.getNumericCellValue());
									}
									msgMap.put(primarykeyValue, sb);
									maps.put(key, "");
								} else if ("A".equals(item.getItemtype()) && "a0405".equalsIgnoreCase(item.getItemid())) {
									ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
									if (sb != null) {
										int no = sb.size();
										sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc() + "]" 
														+ ResourceFactory.getProperty("workbench.info.import.error.inttype") 
														+ cell.getNumericCellValue());
									} else {
										sb = new ArrayList();
										sb.add("1.&nbsp;[" + item.getItemdesc() + "]"
														+ ResourceFactory.getProperty("workbench.info.import.error.inttype") 
														+ cell.getNumericCellValue());
									}
									msgMap.put(primarykeyValue, sb);
									maps.put(key, "");
								} else if ("r4009".equalsIgnoreCase(item.getItemid())) {
									dvalue = cell.getNumericCellValue();
									String str = (int) dvalue + "";
									list.add(str);
									maps.put(key, str);
								} else {
									dvalue = cell.getNumericCellValue();
									String str = dvalue + "";
									list.add(str);
									maps.put(key, str);
								}
								break;
							case Cell.CELL_TYPE_STRING:
								value = cell.getStringCellValue().trim();
								if(value.length()<1 || value == null){
									maps.put(key, value);
									break;
								}
								if("D".equals(item.getItemtype())){
									value = value.replace("/", "-").replace(".", "-");
								}
								if ("D".equals(item.getItemtype()) && "r4006".equalsIgnoreCase(item.getItemid())
										|| "r4007".equalsIgnoreCase(item.getItemid())) {
									if ("r4006".equalsIgnoreCase(item.getItemid())) {
										startTime = value;
									} else if ("r4007".equalsIgnoreCase(item.getItemid())) {
										endTime = value;
									}
									if (!"".equalsIgnoreCase(startTime) && !"".equalsIgnoreCase(endTime)) {
										long day = getQuot(endTime, startTime);
										if (day < 0) {
											ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
											if (sb != null) {
												int no = sb.size();
												sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc() + "]"
																+ ResourceFactory.getProperty("开始时间要小于结束时间， ") 
																+ "开始:" + startTime + "结束:" + endTime);
											} else {
												sb = new ArrayList();
												sb.add("1.&nbsp;[" + item.getItemdesc() + "]"
																+ ResourceFactory.getProperty("开始时间要小于结束时间， ") 
																+ "开始:" + startTime + "结束:" + endTime);
											}
											msgMap.put(primarykeyValue, sb);
											maps.put("r4006", "");
											value = "";
										}
									}
									try {
										String v = value.replace("/", "-");
										if (v.equals(value)) {
											v = value.replace(".", "-");
										}
										SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
										if (!"".equals(value) && value != null) {
											Date date1 = ft.parse(v);
											value = ft.format(date1);
										}
									} catch (Exception e) {
										ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
										if (sb != null) {
											int no = sb.size();
											sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc() + "]"
															+ ResourceFactory.getProperty("workbench.info.import.error.datetype") 
															+ cell.getStringCellValue());
										} else {
											sb = new ArrayList();
											sb.add("1.&nbsp;[" + item.getItemdesc() + "]"
															+ ResourceFactory.getProperty("workbench.info.import.error.datetype") 
															+ cell.getStringCellValue());
										}
										msgMap.put(primarykeyValue, sb);
										value = "";
									}
								}
								if ("D".equals(item.getItemtype())&& "a0112".equalsIgnoreCase(item.getItemid())) {
									try {
										String v = value.replace("/", "-");
										if (v.equals(value)) {
											v = value.replace(".", "-");
										}
										  SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
										  if(!"".equals(value) && value != null){											  
											  Date date1 = ft.parse(v);
											  value = ft.format(date1);
 										  }
									} catch (Exception e) {
										ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
										if (sb != null) {
											int no = sb.size();
											sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc() + "]"
															+ ResourceFactory.getProperty("workbench.info.import.error.datetype") 
															+ cell.getStringCellValue());
										} else {
											sb = new ArrayList();
											sb.add("1.&nbsp;[" + item.getItemdesc() + "]"
															+ ResourceFactory.getProperty("workbench.info.import.error.datetype") 
															+ cell.getStringCellValue());
										}
										msgMap.put(primarykeyValue, sb);
										value = "";
									}
								}
								if ("N".equals(item.getItemtype())) {
									try {
										value = value == null || value.length()<1 ? "0" : value;
										if(value != null && !"".equalsIgnoreCase(value)){
											double d = Double.parseDouble(value);
											value = d + "";
										}
									} catch (Exception e) {
										ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
										if (sb != null) {
											int no = sb.size();
											sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc() + "]"
															+ ResourceFactory.getProperty("workbench.info.import.error.inttype")
															+ cell.getStringCellValue());
										} else {
											sb = new ArrayList();
											sb.add("1.&nbsp;[" + item.getItemdesc() + "]"
															+ ResourceFactory.getProperty("workbench.info.import.error.inttype") 
															+ cell.getStringCellValue());
										}
										msgMap.put(primarykeyValue, sb);
										value = "0";
									}
								}
								if("e01a1".equalsIgnoreCase(item.getItemid()))
									value = value.substring(value.lastIndexOf("(") + 1, value.lastIndexOf(")"));
								if((value != null && value.length()>0) && !"e0122".equalsIgnoreCase(item.getItemid()) && !"b0110".equalsIgnoreCase(item.getItemid())
										&& !"e01a1".equalsIgnoreCase(item.getItemid()) && isCode(item.getCodesetid())
										){
									String sql = "select codeitemid from codeitem where upper(codesetid)='"
										+ item.getCodesetid().toUpperCase() + "' and codeitemdesc='" + value + "'";
									this.frowset = dao.search(sql);
									if(this.frowset.next())
										value = this.frowset.getString("codeitemid");
									else {
										ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
										if (sb != null) {
											int no = sb.size();
											sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc() + "]" 
													+ ResourceFactory.getProperty("workbench.info.import.error.codenol")
													+ value + ResourceFactory.getProperty("workbench.info.import.error.codenor"));
										} else {
											sb = new ArrayList();
											sb.add("1.&nbsp;[" + item.getItemdesc() + "]" 
													+ ResourceFactory.getProperty("workbench.info.import.error.codenol")
													+ value + ResourceFactory.getProperty("workbench.info.import.error.codenor"));
										}
										msgMap.put(primarykeyValue, sb);
										value = "";
									}
								}
								maps.put(key, value);
								break;
							default:
								if (item.isFillable()) { // 判断必填项是否为空
									ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
									if (sb != null) {
										int no = sb.size();
										sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc() + "]"
														+ ResourceFactory.getProperty("edit_report.parameter.checkemptys") 
														+ cell.getStringCellValue());
									} else {
										sb = new ArrayList();
										sb.add("1.&nbsp;[" + item.getItemdesc() + "]"
														+ ResourceFactory.getProperty("edit_report.parameter.checkemptys") 
														+ cell.getStringCellValue());
									}
									msgMap.put(primarykeyValue, sb);
									count++;
								} else if("N".equalsIgnoreCase(item.getItemtype()))
									maps.put(item.getItemid(), "0" );
								else
									maps.put(item.getItemid(), "" );
								break;
							}
						}
					}
					
					e0122 = e0122 == null || e0122.length() < 1 ? "" : e0122;
					
					maps.put("r4002", a0101);
					maps.put("b0110", b0110);
					maps.put("e0122", e0122);
					maps.putAll(getInformation(othermap, a0100, nbase, dao));
					if (count == 0) {
					    students += a0100+",";
						m.put(s, maps);
					} else {
						maps.clear();
					}
				}
			}
			for (Iterator i = msgMap.keySet().iterator(); i.hasNext();) {
				String key = (String) i.next();
				LazyDynaBean ldb = new LazyDynaBean();
				ldb.set("keyid", key);
				ArrayList sb = (ArrayList) msgMap.get(key);
				StringBuffer sbb = new StringBuffer();
				for (int a = 0; a < sb.size(); a++) {
					sbb.append("&nbsp;" + (String) sb.get(a) + "</br>");
				}
				ldb.set("content", sbb.toString());
				msg.add(ldb);
			}
			m.put("classid", r3101);
			this.getFormHM().put("msg", msg);
			this.getFormHM().put("maps", m);
			this.getFormHM().put("persons", students);
			this.getFormHM().put("primarykeyLabel", primarykeyLabel);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workbench.info.import.error.excel")));
		}finally {
            PubFunc.closeIoResource(is);
            PubFunc.closeResource(wb);
        }
	}
	/**
	 * 判断时间输入是否正确
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static long getQuot(String time1, String time2){
		  long quot = 0;
		  time1 = time1.replace(".", "-").replace("/", "-");
		  time2 = time2.replace(".", "-").replace("/", "-");
		  SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		  try {
		   Date date1 = ft.parse( time1 );
		   Date date2 = ft.parse( time2 );
		   quot = date1.getTime() - date2.getTime();
		   quot = quot / 1000 / 60 / 60 / 24;
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
		  return quot;
		}

	/**
	 * 获取学员基本信息及判断是唯一性指标相关数据是否有重复的
	 * @param itemid
	 * @param value
	 * @param dao
	 * @return 0 ：不存在|2： 有重复项 |人员库,人员编号,单位,部门
	 * @throws GeneralException
	 */
	public String getPersons(String itemid, String value, ContentDAO dao) throws GeneralException {
		String Persons = "0";
		int m = 0;

		if("r4002".equalsIgnoreCase(itemid)){
			itemid = "a0101";
		}
		ConstantXml constantbo = new ConstantXml(this.frameconn, "TR_PARAM");
		String tmpnbase = constantbo.getTextValue("/param/post_traincourse/nbase");
		if (tmpnbase == null || tmpnbase.length() < 1)
			throw GeneralExceptionHandler.Handle(new Exception("未设置人员库！<br><br>请到   培训管理>参数设置>其它参数>岗位培训指标设置   中设置人员库。"));
		
		String strWhere = TrainCourseBo.getUnitIdByBusiStrWhere(this.userView);
		String[] tmpnbases = tmpnbase.split(",");
		StringBuffer sql = new StringBuffer();
		for (int i = 0; i < tmpnbases.length; i++) {
			sql.append(" select a0100,a0101,b0110,e0122,'" + tmpnbases[i] + "' as nbase from " + tmpnbases[i] + "a01");
			sql.append(" where " + itemid + "='" + value + "' " + strWhere);
			sql.append(" union all");
		}
		String sqlstr = sql.toString().substring(0, sql.lastIndexOf("union all"));
		try {
			this.frowset = dao.search(sqlstr);
			while(this.frowset.next()){
				String a0100 = this.frowset.getString("a0100");
				String a0101 = this.frowset.getString("a0101");
				String b0110 = this.frowset.getString("b0110");
				String e0122 = this.frowset.getString("e0122");
				String nbase = this.frowset.getString("nbase");
				//防止e0122是null的情况导致空指针
				if(e0122!=null&&e0122.length() < 1)
				    e0122 = null;
				Persons = nbase + "," + a0100 + "," + a0101 + "," + b0110 + "," + e0122;
				m++;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		if(m>1)
			Persons = "2";
		return Persons;
	}
	/**
	 * 判断指标是否是非单位部门岗位的代码类
	 * @param codesetid
	 * @return
	 */
	public boolean isCode(String codesetid) {
		boolean flag = true;
		if("UN".equalsIgnoreCase(codesetid))
			flag = false;
		if("UM".equalsIgnoreCase(codesetid))
			flag = false;
		if("@K".equalsIgnoreCase(codesetid))
			flag = false;
		if("0".equalsIgnoreCase(codesetid))
			flag = false;
		if(codesetid.indexOf("1_")!=-1)
			flag = false;
		return flag;
	}
	/**
	 * 获取引入的主集或子集的指标
	 * @return
	 */
	public HashMap getOtherMap(){
		HashMap otherMap = new HashMap();
		ArrayList fieldlist = DataDictionary.getFieldList("r40", 1);
		for(int i=0; i<fieldlist.size(); i++){
			FieldItem fielditem = (FieldItem) fieldlist.get(i);
			fielditem = DataDictionary.getFieldItem(fielditem.getItemid());
			if("r40".equalsIgnoreCase(fielditem.getCodesetid().toLowerCase()) || "b0110".equalsIgnoreCase(fielditem.getItemid()))
				continue;
			if("0".equalsIgnoreCase(fielditem.getState()))
				continue;
			String fieldsetid = fielditem.getFieldsetid().toLowerCase();
			String itemid = fielditem.getItemid();
			if(otherMap.containsKey(fieldsetid)){
				String value = (String) otherMap.get(fieldsetid);
				value += "," + fieldsetid.toUpperCase() + "." + itemid;
				otherMap.put(fieldsetid, value);
			}else 
				otherMap.put(fieldsetid, fieldsetid.toUpperCase() + "." + itemid);
		}
		return otherMap;
	}
	/**
	 * 获取学员相关的信息
	 * @param hm
	 * @param a0100
	 * @param nbase
	 * @param dao
	 * @return
	 * @throws GeneralException 
	 */
	public HashMap getInformation(HashMap hm, String a0100, String nbase, ContentDAO dao) throws GeneralException {
		HashMap IFmap = new HashMap();
		try {
			for (Iterator i = hm.entrySet().iterator(); i.hasNext();) {
				Map.Entry entrys = (Map.Entry) i.next();
				String key = (String) entrys.getKey();
				String value = (String) entrys.getValue();
				if (key.startsWith("a")) {
					value = value.replaceAll(key.toUpperCase(), nbase + key);
					StringBuffer sql = new StringBuffer(); 
					sql.append("select " + value + " from " + nbase + key); 
					sql.append(" where a0100='" + a0100 + "'");
					if(!"a01".equalsIgnoreCase(key.toLowerCase())) {
					    sql.append(" and I9999 =(");
					    sql.append("select max(i9999) from " + nbase + key); 
	                    sql.append(" where a0100='" + a0100 + "')");
					    
					}
					    
					this.frowset = dao.search(sql.toString());
					if (this.frowset.next()) {
						String[] itemids = value.split(",");
						for (int k = 0; k < itemids.length; k++) {
							String itemid = itemids[k];
							String[] itms=itemid.split("[.]");
							itemid = itms[1];
							if("b0110".equalsIgnoreCase(itemid) || "e0122".equalsIgnoreCase(itemid))
								continue;
							String itemvalue = this.frowset.getString(itemid);
							IFmap.put(itemid, itemvalue);
						}
					}
				} else if (key.startsWith("b")) {
					String sql = "select " + value + " from " + nbase + "a01," + key + " where " + nbase + "a01.b0110=" + key
							+ ".b0110 and " + nbase + "a01.a0100='" + a0100 + "'";
					this.frowset = dao.search(sql);
					if (this.frowset.next()) {
						String[] itemids = value.split(",");
						for (int k = 0; k < itemids.length; k++) {
							String itemid = itemids[k];
							String[] itms=itemid.split("[.]");
							itemid = itms[1];
							String itemvalue = this.frowset.getString(itemid);
							IFmap.put(itemid, itemvalue);
						}
					}
				} else if (key.startsWith("k")) {
					String sql = "select " + value + " from " + nbase + "a01," + key + " where " + nbase + "a01.b0110=" + key
							+ ".e01a1 and " + nbase + "a01.a0100='" + a0100 + "'";
					this.frowset = dao.search(sql);
					if (this.frowset.next()) {
						String[] itemids = value.split(",");
						for (int k = 0; k < itemids.length; k++) {
							String itemid = itemids[k];
							String[] itms=itemid.split("[.]");
							itemid = itms[1];
							String itemvalue = this.frowset.getString(itemid);
							IFmap.put(itemid, itemvalue);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return IFmap;
	}
	/**
	 * 获取培训班下非已批状态的学员
	 * @param r3101
	 * @return
	 * @throws GeneralException 
	 */
	public HashMap getOthStudent(String r3101) throws GeneralException {
		HashMap othHm = new HashMap();
		String sql = "select r4001,r4013 from r40 where r4005='" + r3101 + "' and r4013<>'03'";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(sql);
			while(this.frowset.next()) {
				String r4013 = this.frowset.getString("r4013");
				othHm.put(this.frowset.getString("r4001"), codetoName(r4013));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return othHm;
	}
	/**
	 * 将状态转换为可见文字
	 * @param codeitemid
	 * @return
	 * @throws GeneralException
	 */
	public String codetoName(String codeitemid) throws GeneralException {
		String codeName = "";
		RowSet rs = null;
		String sql = "select codeitemdesc from codeitem  where codesetid='23' and codeitemid='" + codeitemid + "'";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			rs = dao.search(sql);
			if (rs.next()) {
				codeName = rs.getString("codeitemdesc");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return codeName;
	}
	
}
