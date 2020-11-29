package com.hjsj.hrms.businessobject.report.user_defined_reoprt;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析自定义报表
 * <p>
 * Title:UserdefinedReport.java
 * </p>
 * <p>
 * Description>:UserdefinedReport.java
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:Mar 8, 2010 8:58:06 AM
 * </p>
 * <p>
 * 
 * @version: 4.0
 *           </p>
 *           <p>
 * @author: s.xin
 */
public class UserdefinedReport {

	// 是否取得权限范围内的数据，默认为true
	private boolean ispriv = true;
	// 登录用户
	private UserView userView;
	// 数据库连接
	private Connection conn;
	// sql 模板
	private Document doc = null;
	// 数据库操作对象
	private ContentDAO dao = null;
	// excel对象
	private Workbook workbook = null;
	// xml内容
	private String xmlcontent = "";
	private InputStream in = null;
	// xml模板或html模板的文件扩展名(支持xls、xlsx、xlt、xltx、html、htm)
	private String ext = "";
	// 生成的文件的名称
	private String filename = "";
	// html模板的内容
	private String htmlcontent;
	// 报表id
	private String report_id;
	// 公共参数集合(修改后提交的参数值)
	private HashMap publicParamMap;
	// 输入参数
	private HashMap inputParamMap = new HashMap();


	// 所有参数的名称
	private String[] names;
	// excel的存放路径（不再使用）
	private String exclepath;
	// 是否上传了sql模板，默认为true
	private boolean hashSQL = true;
	// 自定义单元格的集合
	HashMap cellsMap = new HashMap();
	// 错误信息
	private String errerMessage;
	//对0处理的设置
	String zeroHandler = "zero";
	/**
	 * 构造方法，初始化参数
	 * 
	 * @param userView
	 *            UserView 用户
	 * @param conn
	 *            Connection 数据库连接
	 * @param report_id
	 *            String 报表id
	 * @param ispriv
	 *            boolean 是否取得权限范围内的数据
	 */
	public UserdefinedReport(UserView userView, Connection conn,
			String report_id, boolean ispriv) {
		// 赋值
		this.userView = userView;
		this.conn = conn;
		this.ispriv = ispriv;
		this.dao = new ContentDAO(conn);
		this.report_id = report_id;

		// 根据报表id，初始化xmlcontent,in,ext,filename
		init(report_id);
	}

	/**
	 * 启动配置数据
	 * 
	 * @throws GeneralException
	 */
	private void startupConfigData() throws GeneralException {
		// 报表模板必须存在，否则抛异常
		if (ext == null || ext.length() <= 0) {
			throw GeneralExceptionHandler.Handle(new GeneralException(
					"没有定义报表模板文件！"));
		}

		// 根据扩展名配置数据
		if (".html".equalsIgnoreCase(ext) || ".htm".equalsIgnoreCase(ext)) {
			// 配置html数据
			startupHtmlConfigData();
		} else if ("html".equalsIgnoreCase(ext) || "htm".equalsIgnoreCase(ext)) {
			// 配置html数据
			startupHtmlConfigData();
		} else if ("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext)) {
			// 配置excel数据
			startupExcelConfigData();
		} else if (".xls".equalsIgnoreCase(ext)
				|| ".xlsx".equalsIgnoreCase(ext)) {
			// 配置excel数据
			startupExcelConfigData();
		} else if (".xlt".equalsIgnoreCase(ext)
				|| ".xltx".equalsIgnoreCase(ext)) {
			// 配置excel数据
			startupExcelConfigData();
		} else if ("xlt".equalsIgnoreCase(ext) || "xltx".equalsIgnoreCase(ext)) {
			// 配置excel数据
			startupExcelConfigData();
		} else if (".mht".equalsIgnoreCase(ext)) {
			// 配置html数据
			startupHtmlConfigData();
		}else { // 不支持除xls、xlsx、xlt、xltx、html、htm以外的模板格式
			throw GeneralExceptionHandler.Handle(new GeneralException(
					"自定义报表模板文件类型错误！"));
		}
	}

	/**
	 * 启动Excel模板配置数据
	 * 
	 * @throws GeneralException
	 */
	private void startupExcelConfigData() throws GeneralException {

		// 根据xmlcontent内容来判读是否存在sql模板，
		// 没有xml模板时，只将定义报表模板输出
		if (this.xmlcontent == null || this.xmlcontent.length() <= 0) {
			this.hashSQL = false;
		} else {
			this.hashSQL = true;
		}

		// 没有定义报表模板
		if (this.in == null) {
			throw GeneralExceptionHandler.Handle(new GeneralException(
					"没有定义报表模板文件！"));
		}

		// sax方式解析xml
		if (this.hashSQL) {
			try {
                this.doc = PubFunc.generateDom(xmlcontent.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
		}

		try {
			// 根据是否为xlt，来创建excel(xlt文件需要用POIFSFileSystem包装)
			if (".xlt".equalsIgnoreCase(ext) || ".xltx".equalsIgnoreCase(ext)) {
				POIFSFileSystem fs = new POIFSFileSystem(this.in);
				this.workbook = WorkbookFactory.create(fs);
			} else {
				this.workbook = WorkbookFactory.create(this.in);
			}

		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 启动Html模板配置数据
	 * 
	 * @throws GeneralException
	 */
	private void startupHtmlConfigData() throws GeneralException {

		// 根据xmlcontent内容来判读是否存在sql模板，
		// 没有xml模板时，只将定义报表模板输出
		if (this.xmlcontent == null || this.xmlcontent.length() <= 0) {
			this.hashSQL = false;
		} else {
			this.hashSQL = true;
		}

		// 没有定义报表模板
		if (this.in == null) {
			throw GeneralExceptionHandler.Handle(new GeneralException(
					"没有定义报表模板文件！"));
		}

		// sax方式解析xml
		if (this.hashSQL) {
			try {
                this.doc = PubFunc.generateDom(xmlcontent.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
		}

		// 读取html的内容
		this.htmlcontent = getStringFromStream(this.in);
	}

	/**
	 * 初始化
	 * 
	 * @param report_id
	 *            自定义报表id
	 */
	private void init(String report_id) {

		// 查询sql
		StringBuffer sqlBuff = new StringBuffer();
		sqlBuff.append(" select Sqlfile,Templatefile,");
		sqlBuff.append(" ext,name from t_custom_report");
		sqlBuff.append(" where id='");
		sqlBuff.append(report_id);
		sqlBuff.append("'");

		RowSet rs = null;

		try {
			rs = this.dao.search(sqlBuff.toString());
			if (rs.next()) {
				// sql模板
				xmlcontent = Sql_switcher.readMemo(rs, "Sqlfile");
				// 模板
				in = rs.getBinaryStream("Templatefile");
				// 自定制报表扩展名
				this.ext = rs.getString("ext") != null ? rs.getString("ext")
						: "";
				// 报表名称
				this.filename = rs.getString("name");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 解析自定义Excel模板
	 * 
	 * @return String 根据参数生成的html代码
	 * @throws GeneralException
	 */
	public String analyseUserdefinedExcelReport() throws GeneralException {
		// 配置数据
		startupConfigData();
		// 获得工作薄（Workbook）中工作表（Sheet）的个数
		int number = this.workbook.getNumberOfSheets();
		UserdefineSqlFile userdefineSqlFile = null;
		if (this.hashSQL) { // 存在sql模板
			Sheet sheet = null;
			userdefineSqlFile = new UserdefineSqlFile(this.doc, this.userView,
					this.dao, this.ispriv,this.conn);
//			BufferedWriter writer = null;
//			try {
//			writer = new BufferedWriter(new FileWriter("c:\\1.txt"));
//			userdefineSqlFile.setWriter(writer);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			// 获得xml文件中的set设置
			Map setMap = userdefineSqlFile.getParamSetMap();
			//获得对0处理的设置
			String zerohandler = "zero";
			if (setMap != null && setMap.containsKey("zerohandler")) {
				zerohandler = (String) setMap.get("zerohandler");
				zerohandler = zerohandler == null || zerohandler.trim().length() <= 0 ? "zero" : zerohandler;
			}
			zeroHandler = zerohandler;
			
			if (this.names != null && this.names.length > 0) {
				HashMap map = userdefineSqlFile.getPublicParamMap();
				for (int i = 0; i < names.length; i++) {
					String name = names[i];
					if (name.contains(":M")) {
						name = name.replace(":M", "");
					}
					if (publicParamMap.get(name) != null) {
						map.put(name, publicParamMap.get(name));
					}
				}
				
				// 将参数值转换
				Iterator its = userdefineSqlFile.getTransMap().entrySet().iterator();
				try {
					while (its.hasNext()) {
						Map.Entry entry = (Map.Entry)its.next();
						ITransCode transCode = (ITransCode) Class.forName(entry.getValue().toString()).newInstance();
						String value = transCode.transCode(map, entry.getKey().toString());
						userdefineSqlFile.getTransOldMap().put(entry.getKey().toString(), map.get(entry.getKey().toString()));
						map.put(entry.getKey().toString(), value);
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				userdefineSqlFile.setPublicParamMap(map);
			} 
			
//			else {
//				HashMap map = userdefineSqlFile.getPublicParamMap();
//				if (map == null) {
//					map = new HashMap();
//				}
//				userdefineSqlFile.setPublicParamMap(map);
//			}

			userdefineSqlFile.setInputParamMap(this.inputParamMap);
			// 公式执行类
			FormulaEvaluator evaluator = workbook.getCreationHelper()
					.createFormulaEvaluator();

			// 循环每个工作表
			for (int i = 0; i < number; i++) {
				// 工作表
				sheet = workbook.getSheetAt(i);
				// 工作表名称
				String sheetname = sheet.getSheetName();
				// sql模板中定义的单元格集合（无顺序）
				cellsMap = userdefineSqlFile.getSheetCellValue(sheetname);
				// sql模板中按照从上到下顺序的单元格集合（有序）
				ArrayList sqlList = userdefineSqlFile.getSql_List();

				// 按照定义的单元格从上到下的顺序执行相应的sql语句
				for (int j = 0; j < sqlList.size(); j++) {
					Element cell_Element = (Element) sqlList.get(j);
					List list = cell_Element.getChildren();

					// ..end情况时，行数间隔是几,默认为1,
					String step = cell_Element.getAttributeValue("step");
					step = this.handleStep(step);

					// 批量处理
					if (list != null && list.size() > 0) {
						ArrayList arrValueList = userdefineSqlFile
								.getCellValueList(cell_Element);

						// 产生异常
						if (userdefineSqlFile.errorFlag) {
							throw GeneralExceptionHandler
									.Handle(new GeneralException("报表模板出错！"
											+ userdefineSqlFile.errorMr));
						}
						// 值为空
						if (arrValueList == null || arrValueList.size() <= 0) {
							continue;
						}

						// 按照定义的列写数据
						for (int k = 0; k < list.size(); k++) {
							// 自定义的单元格
							Element el = (Element) list.get(k);
							String cell_name = el
									.getAttributeValue("cell_name");
							// 列
							String column = el.getAttributeValue("col");
							// 代码类
							String codeSetId = el.getAttributeValue("codesetid");
							// 层级
							String level = el.getAttributeValue("uplevel");
							int uplevel = 0;
							if (level != null && "true".equalsIgnoreCase(level)) {
								uplevel = this.getuplevel();
							}
							// 数据类型
							String type = el.getAttributeValue("type");
							if (type == null || type.length() == 0) {
								type = "A";
							}
							// 小数点位数
							String deci = el.getAttributeValue("deci");
							if (deci == null || deci.length() == 0) {
								deci = "0";
							}
							// ..end情况时，行数间隔是几,默认为1,以
							String childStep = el.getAttributeValue("step");
							if (childStep == null || childStep.length() == 0) {
								childStep = step;
							}

							// 处理step
							childStep = this.handleStep(childStep);

							int col = Integer.parseInt(column);
							// 重新封装数据
							ArrayList newarrValueList = new ArrayList();

							for (int m = 0; m < arrValueList.size(); m++) {
								ArrayList olList = (ArrayList) arrValueList
										.get(m);
								ArrayList nolist = new ArrayList();
								try {
									if (type != null
											&& "n".equalsIgnoreCase(type)
											&& deci != null
											&& deci.length() > 0) { // 数字类型
										String vv = "";
										if (olList.size() > col) {
											vv = (String) olList.get(col);
										}
										
										if (vv.indexOf(".") != -1) {
											String va = PubFunc.round(vv, Integer.parseInt(deci));
											
											nolist.add(handlerZero(va));
										} else {
											int num = Integer.parseInt(deci);
											for (int l = 0; l < num; l++) {
												if (l == 0) {
													vv += "." + "0";
												} else {
													vv += "0";
												}
											}
											String va = PubFunc.round(vv, num);
											
											nolist.add(handlerZero(va));

										}
									} else { // 字符类型
										String vv = "";
										if (olList.size() > col) {
											vv = (String) olList.get(col);
										}
										// 根据代码值查询描述
										if (codeSetId != null && codeSetId.length() > 0) {
											vv = getCodeDesc(codeSetId, vv.trim(),uplevel);
										}
										
										nolist.add(handlerZero(vv));
									}
								} catch (Exception exc) {
									throw GeneralExceptionHandler
											.Handle(new GeneralException(
													"sql语句中没有第" + col + "列"));
								}
								newarrValueList.add(nolist);
							}

							// 将内容转化格式
							String cellvalie = put_in_orderValue(newarrValueList);
							if (cellvalie == null || cellvalie.length() <= 0) {
                                cellvalie = "";
                            }

							// 将数据写到excel
							doExcelCell(cell_name, sheet, cellvalie,
									newarrValueList, Integer.parseInt(childStep),type);

						}
					} else {
						// 定义的单元格名称
						String cell_name = cell_Element
								.getAttributeValue("cell_name");
						String type = cell_Element.getAttributeValue("type");
						// 获得代码类
						String codeSetId = cell_Element.getAttributeValue("codesetid");
						// 层级
						String level = cell_Element.getAttributeValue("uplevel");
						int uplevel = 0;
						if (level != null && "true".equalsIgnoreCase(level)) {
							uplevel = this.getuplevel();
						}
						
						if (type == null || type.length() == 0) {
							type = "A";
						}
						// 单元格的值
						ArrayList arrValueList = userdefineSqlFile
								.getCellValueList(cell_Element);

						
						
						if (userdefineSqlFile.errorFlag) {
							throw GeneralExceptionHandler
									.Handle(new GeneralException("报表模板出错！"
											+ userdefineSqlFile.errorMr));
						}
						// 没有值
						if (arrValueList == null || arrValueList.size() <= 0) {
							continue;
						}

						// 根据代码值查询描述
						if (codeSetId != null && codeSetId.length() > 0) {
							// 处理codesetid
							arrValueList = dealWithCodeSet(arrValueList, codeSetId, uplevel);
						}
												
						
						arrValueList  = newHandler(arrValueList);
						
						String cellvalie = put_in_orderValue(arrValueList);
						// 获得分隔符
						String separator = cell_Element
								.getAttributeValue("separator_single");
						if (separator != null && separator.length() > 0) {
							cellvalie = cellvalie.replaceAll("\\n", separator);
						}

						// 无值
						if (cellvalie == null || cellvalie.length() <= 0) {
							continue;
						}

						// 将数据写到excel中
						doExcelCell(cell_name, sheet, cellvalie, arrValueList,
								Integer.parseInt(step), type);
					}

				}

				// 查找现有自定义的公式，并执行
				/*
				 * for(Row r : sheet) {java 1.5特性，不兼容1.4 for(Cell c : r) {
				 * if(c.getCellType() == Cell.CELL_TYPE_FORMULA) {
				 * evaluator.evaluateFormulaCell(c); } } }
				 */
				// 循环所有行
				for (int m = 0; m <= sheet.getLastRowNum(); m++) {
					Row row = sheet.getRow(m);
					// 防止隔行书写
					if (row == null) {
						continue;
					}
					// 循环行中所有的单元格
					for (int n = 0; n <= row.getLastCellNum(); n++) {
						Cell cell = row.getCell(n);
						// 防止隔列写单元格
						if (cell == null) {
							continue;
						}
						if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
							// 执行公式
							evaluator.evaluateFormulaCell(cell);
						}
					}
				}

			}
//			if (writer != null) {
//				try {
//				writer.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
		}
		String excel_filename = "";
		try {

			if (this.ext.indexOf(".") == -1) {
				// 文件名
				excel_filename = PubFunc.getStrg() + "." + this.ext;
			} else
				// 文件名
            {
                excel_filename = PubFunc.getStrg() + "" + this.ext;
            }

			// 将文件保存到临时文件夹中
			FileOutputStream fileOut = new FileOutputStream(System
					.getProperty("java.io.tmpdir")
					+ System.getProperty("file.separator") + excel_filename);
			// 将数据写到excel中
			workbook.write(fileOut);
			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (this.hashSQL) {
			String sele = userdefineSqlFile.getExcelHtmlString(report_id);
			this.setHtmlcontent(sele);
		} else {
			StringBuffer buff = new StringBuffer();
			buff
					.append("<div id=\"div\" class=\"noprint lin\" style=\"height:100%\"><div style=\"height:50%\"></div><form style=\"height:50%;margin-top:0\" id=\"form1\" name=\"form1\" method=\"post\" onsubmit=\"return false;\"");
			buff.append("action=\"\">");
			buff
					.append("<input type=\"button\" class=\"mybutton\" style=\"margin-left:3px\"  name=\"download\" value=\"下载\" onclick=\"downLoadExcel()\" /></form></div>");
			this.setHtmlcontent(buff.toString());
		}
		return excel_filename;
	}
	
	private ArrayList newHandler(ArrayList arrValueList) {
		
		ArrayList newValueList = new ArrayList();
		// 重新处理数据
		for (int m = 0; m < arrValueList.size(); m++) {
			if (arrValueList.get(m) instanceof String) {
				String str = (String) arrValueList.get(m);
				str = this.handlerZero(str);
				newValueList.add(str);
			} else {
				List oList = (List) arrValueList.get(m);
				ArrayList newList = new ArrayList();
				for (int n = 0; n < oList.size(); n++) {
					String str = (String) oList.get(n);
					str = this.handlerZero(str);
					newList.add(str);
				}
				newValueList.add(newList);
			}
			
		}
		
		return newValueList;
	}
	private String handlerZero(String vv) {
		String value = "";
		vv = vv == null ? "" : vv;
		if (vv.matches("[0]*[.]?[0]*") && "space".equalsIgnoreCase(this.zeroHandler)) {
			vv = "";
		} 
		value = vv;
		return value;
	}
	
	/**
	 * 获得系统设置的部门层级
	 * @return int 层级
	 */
	private int getuplevel() {
		 // 部门层级
        Sys_Oth_Parameter sys = new Sys_Oth_Parameter(this.conn);
        String uplevel = sys.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
        if (uplevel == null || uplevel.length() <= 0) {
        	uplevel = "0";
        }
        return Integer.parseInt(uplevel);
	}
	/**
	 * 根据代码项和代码值查询描述
	 * @param codeSetId 
	 * @param codeValue
	 * @return 描述，如果没有，返回空字符窜
	 */
	private String getCodeDesc (String codeSetId, String codeValue, int uplevel) {
		CodeItem item = null;
		if (("UM".equalsIgnoreCase(codeSetId)) && (uplevel > 0)) {
			item = AdminCode.getCode(codeSetId, ((String)codeValue).trim(), uplevel);
		} else if (uplevel > 0) {
			item = AdminCode.getCode(codeSetId, ((String)codeValue).trim(), uplevel);
		} else {
			item = AdminCode.getCode(codeSetId, ((String)codeValue).trim());
		}
		if (item == null) {
			return codeValue;
		}
		return item.getCodename();
	}
	
	/**
	 * 处理codesetid的数据
	 * @param list
	 * @param codeSetId
	 * @return
	 */
	private ArrayList dealWithCodeSet(ArrayList list, String codeSetId, int uplevel) {
		
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof String) {
				String oldVa = (String) list.get(i);
				String newVa = getCodeDesc(codeSetId, oldVa.trim(), uplevel);
				if (newVa != null && newVa.length() > 0) {
					list.remove(i);
					list.add(i, newVa);
				}
			} else {
				ArrayList child = (ArrayList) list.get(i);
				if (child.size() >= 1) {
					String oldVa = (String) child.get(0);
					String newVa = getCodeDesc(codeSetId, oldVa.trim(), uplevel);
					if (newVa != null && newVa.length() > 0) {
						child.remove(0);
						child.add(0, newVa);
					}
				}
				list.remove(i);
				list.add(i, child);
			}
		}		
		return list;		
	}
	/**
	 * 处理数字字符窜
	 * 
	 * @param step
	 * @return
	 * @throws GeneralException
	 */
	private String handleStep(String step) throws GeneralException {
		if (step == null || step.length() == 0) {
			step = "1";
		} else {
			try {
				Integer.parseInt(step);
			} catch (Exception e) {
				throw GeneralExceptionHandler.Handle(new GeneralException(
						"报表sql模板出错！\r\n step必须为数字！"));
			}
		}

		return step;
	}

	private void doExcelCell(String cell_name, Sheet sheet, String cellvalie,
			ArrayList arrValueList, int step, String type) {
		Row row = null;
		Cell cell = null;
		// 处理分组的情况
		if (!cell_name.contains(",")) {
			if (!cell_name.toLowerCase().contains("..end")) {// 不包含end
				int colNum = getExcelCellCol(cell_name);
				int rowNum = getExcelCellRow(cell_name);
				row = sheet.getRow(rowNum);
				if (row == null) {
					row = sheet.createRow(rowNum);
				}

				if (row != null) {
					cell = row.getCell(colNum);
					if (cell != null) {
						CellStyle cellStyle = cell.getCellStyle();
						cellStyle.setWrapText(true);
						cell.setCellStyle(cellStyle);
						if ("N".equalsIgnoreCase(type)) {// 数字
							double value = 0;
							if (cellvalie != null && cellvalie.length() > 0) {
								value = Double.parseDouble(cellvalie);
							}				
							cell.setCellValue(value);
							//cell.setCellType(Cell.CELL_TYPE_NUMERIC);
						} else {// 字符
							
							cell.setCellValue(cellvalie);
						
						}

					} else {
						greatCell(cellvalie, colNum, rowNum, sheet, type);
					}
				} else {
					greatCell(cellvalie, colNum, rowNum, sheet, type);
				}
			} else { // 包含end
				writeValue(cell_name, sheet, cellvalie, arrValueList, step, type);
			}
		} else {
			if (!cell_name.toLowerCase().contains("..end")) {
				String[] cell_names = cell_name.split(",");
				for (int j = 0; j < cell_names.length; j++) {
					int colNum = getExcelCellCol(cell_names[j]);
					int rowNum = getExcelCellRow(cell_names[j]);
					row = sheet.getRow(rowNum);
					if (row == null) {
						row = sheet.createRow(rowNum);
					}
					if (j < arrValueList.size()) {
						ArrayList list = new ArrayList();
						list.add(arrValueList.get(j));
						String cellvalie2 = put_in_orderValue(list);
						if (cellvalie2 == null || cellvalie2.length() <= 0) {
                            cellvalie2 = "";
                        }
						cell = row.getCell(colNum);
						if (cell != null) {
							CellStyle cellStyle = cell.getCellStyle();
							cellStyle.setWrapText(true);
							cell.setCellStyle(cellStyle);
							if ("N".equalsIgnoreCase(type)) {// 数字
								double value = 0;
								if (cellvalie2 != null && cellvalie2.length() > 0) {
									value = Double.parseDouble(cellvalie2);
								}				
								cell.setCellValue(value);
							} else {// 字符
								cell.setCellValue(cellvalie2);
							}

						} else {
							greatCell(cellvalie2, colNum, rowNum, sheet, type);
						}

					}
				}
			} else {
				writeValue(cell_name, sheet, cellvalie, arrValueList, step, type);
			}
		}
	}

	/**
	 * 填写到excel中,处理有..end 的情况
	 * 
	 * @param cell_name
	 * @param sheet
	 * @param cellvalie
	 * @param arrValueList
	 */
	private void writeValue(String cell_name, Sheet sheet, String cellvalie,
			ArrayList arrValueList, int step, String type) {
		Row row = null;
		Cell cell = null;
		String[] cell_names = null;
		int rowNum = 0;
		int colNum = 0;
		int start = 0;
		if (cell_name.contains(",")) {
			cell_names = cell_name.split(",");
			for (int j = 0; j < cell_names.length - 1; j++) {
				colNum = getExcelCellCol(cell_names[j]);
				rowNum = getExcelCellRow(cell_names[j]);
				row = sheet.getRow(rowNum);
				if (row == null) {
					row = sheet.createRow(rowNum);
				}
				if (j < arrValueList.size()) {
					ArrayList list = new ArrayList();
					list.add(arrValueList.get(j));
					String cellvalie2 = put_in_orderValue(list);
					if (cellvalie2 == null || cellvalie2.length() <= 0) {
                        cellvalie2 = "";
                    }
					cell = row.getCell(colNum);
					if (cell != null) {
						CellStyle cellStyle = cell.getCellStyle();
						cellStyle.setWrapText(true);
						cell.setCellStyle(cellStyle);
						if ("N".equalsIgnoreCase(type)) {// 数字
							double value = 0;
							if (cellvalie2 != null && cellvalie2.length() > 0) {
								value = Double.parseDouble(cellvalie2);
							}				
							cell.setCellValue(value);
						} else {// 字符
							cell.setCellValue(cellvalie2);
						}

					} else {
						greatCell(cellvalie2, colNum, rowNum, sheet, type);
					}
					start = j;
				}

			}

			// 最后的n5..end
			cell_name = cell_names[cell_names.length - 1];
			int end = cell_name.indexOf("..");
			String cellName = cell_name.substring(0, end);
			colNum = getExcelCellCol(cellName);
			rowNum = getExcelCellRow(cellName);
			start = start + 1;
			while (start < arrValueList.size()) {
				row = sheet.getRow(rowNum);
				if (row == null) {
					row = sheet.createRow(rowNum);
				}
				ArrayList list = new ArrayList();
				list.add(arrValueList.get(start));
				String cellvalie2 = put_in_orderValue(list);
				if (cellvalie2 == null || cellvalie2.length() <= 0) {
                    cellvalie2 = "";
                }
				cell = row.getCell(colNum);
				if (cell != null) {
					CellStyle cellStyle = cell.getCellStyle();
					cellStyle.setWrapText(true);
					cell.setCellStyle(cellStyle);
					if ("N".equalsIgnoreCase(type)) {// 数字
						double value = 0;
						if (cellvalie2 != null && cellvalie2.length() > 0) {
							value = Double.parseDouble(cellvalie2);
						}				
						cell.setCellValue(value);
					} else {// 字符
						cell.setCellValue(cellvalie2);
					}

				} else {
					greatCell(cellvalie2, colNum, rowNum, sheet, type);
				}
				start++;
				rowNum += step;
			}
		} else {
			int end = cell_name.indexOf("..");
			String cellName = cell_name.substring(0, end);
			colNum = getExcelCellCol(cellName);
			rowNum = getExcelCellRow(cellName);

			while (start < arrValueList.size()) {
				row = sheet.getRow(rowNum);
				if (row == null) {
					row = sheet.createRow(rowNum);
				}
				ArrayList list = new ArrayList();
				list.add(arrValueList.get(start));
				String cellvalie2 = put_in_orderValue(list);
				if (cellvalie2 == null || cellvalie2.length() <= 0) {
                    cellvalie2 = "";
                }
				cell = row.getCell(colNum);
				if (cell != null) {
					CellStyle cellStyle = cell.getCellStyle();
					cellStyle.setWrapText(true);
					cell.setCellStyle(cellStyle);
					if ("N".equalsIgnoreCase(type)) {// 数字
						double value = 0;
						if (cellvalie2 != null && cellvalie2.length() > 0) {
							value = Double.parseDouble(cellvalie2);
						}				
						cell.setCellValue(value);
					} else {// 字符
						cell.setCellValue(cellvalie2);
					}

				} else {
					greatCell(cellvalie2, colNum, rowNum, sheet, type);
				}

				start++;
				rowNum+= step;
			}
		}
	}

	/**
	 * 将文件复制到hrms/system/options/customreport/up/文件夹下
	 * 
	 * @param excel_filename
	 *            excle的文件名称，例如aa.xls
	 */
	private void copyExcel(String excel_filename) {
		FileInputStream filein = null;
		FileOutputStream output = null;
		
		try {
			File f = new File(System.getProperty("java.io.tmpdir")
					+ System.getProperty("file.separator") + excel_filename);
			filein = new FileInputStream(f);
			byte[] bt = new byte[1024];
			output = new FileOutputStream(this.exclepath + excel_filename);
			int read = 0;
			while ((read = filein.read(bt)) != -1) {
				output.write(bt, 0, read);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeIoResource(filein);
			PubFunc.closeIoResource(output);
		}

	}

	/**
	 * 解析自定义html模板
	 * 
	 * @param url
	 *            String html的保存路径
	 * @return String[]
	 * @throws GeneralException
	 */
	public String[] analyseUserdefinedHtmlReport(String url)
			throws GeneralException {
		// 配置数据
		startupConfigData();

		UserdefineSqlFile userdefineSqlFile = null;
		if (this.hashSQL) {// 存在sql模板
			userdefineSqlFile = new UserdefineSqlFile(this.doc, this.userView,
					this.dao, this.ispriv,this.conn);
			// 替换提交后的参数
			if (this.names != null && this.names.length > 0) {
				HashMap map = userdefineSqlFile.getPublicParamMap();
				for (int i = 0; i < names.length; i++) {
					String name = names[i];
					if (name.contains(":M")) {
						name = name.replace(":M", "");
					}
					if (publicParamMap.get(name) != null) {

						map.put(name, publicParamMap.get(name));
					}
				}
				
				// 将参数值转换
				Iterator its = userdefineSqlFile.getTransMap().entrySet().iterator();
				try {
					while (its.hasNext()) {
						Map.Entry entry = (Map.Entry)its.next();
						ITransCode transCode = (ITransCode) Class.forName(entry.getValue().toString()).newInstance();
						String value = transCode.transCode(map, entry.getKey().toString());
						userdefineSqlFile.getTransOldMap().put(entry.getKey().toString(), map.get(entry.getKey().toString()));
						map.put(entry.getKey().toString(), value);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				userdefineSqlFile.setPublicParamMap(map);
			}
			
			
			
			userdefineSqlFile.setInputParamMap(this.inputParamMap);
			
			//获得xml文件中的set设置
			Map setMap = userdefineSqlFile.getParamSetMap();
			//获得对0处理的设置
			String zerohandler = "zero";
			if (setMap != null && setMap.containsKey("zerohandler")) {
				zerohandler = (String) setMap.get("zerohandler");
				zerohandler = zerohandler == null || zerohandler.trim().length() <= 0 ? "zero" : zerohandler;
			}
			zeroHandler = zerohandler;

			// 获得定义的单元格的信息
			HashMap cellsMap = userdefineSqlFile.getCellElement();
			ArrayList sqlList = userdefineSqlFile.getSql_List();

			for (int i = 0; i < sqlList.size(); i++) {
				Element cell_Element = (Element) sqlList.get(i);
				ArrayList arrValueList = userdefineSqlFile
						.getCellValueList(cell_Element);
				List list = cell_Element.getChildren();

				String step = cell_Element.getAttributeValue("step");
				step = this.handleStep(step);
				
				// 模板异常
				if (userdefineSqlFile.errorFlag) {
					throw GeneralExceptionHandler.Handle(new GeneralException(
							"报表模板出错！" + userdefineSqlFile.errorMr));
				}
				if (arrValueList == null || arrValueList.size() <= 0) {
                    continue;
                }
				// 批量处理
				if (list != null && list.size() > 0) {
					for (int k = 0; k < list.size(); k++) {
						Element el = (Element) list.get(k);
						// 单元格名称
						String cell_name = el.getAttributeValue("cell_name");
						// 第几列
						String column = el.getAttributeValue("col");
						// 数据类型
						String type = el.getAttributeValue("type");
						// 小数点个数
						String deci = el.getAttributeValue("deci");
						// 代码类
						String codeSetId = el.getAttributeValue("codesetid");
						// 层级
						String level = cell_Element.getAttributeValue("uplevel");
						int uplevel = 0;
						if (level != null && "true".equalsIgnoreCase(level)) {
							uplevel = this.getuplevel();
						}
						String childStep = cell_Element.getAttributeValue("step");
						if (childStep == null || childStep.length() == 0) {
							childStep = step;
						}
						
						childStep = this.handleStep(childStep);
						
						int col = Integer.parseInt(column);
						// 重新封装数据
						ArrayList newarrValueList = new ArrayList();

						for (int m = 0; m < arrValueList.size(); m++) {
							ArrayList olList = (ArrayList) arrValueList.get(m);
							ArrayList nolist = new ArrayList();
							try {
								if (type != null && "n".equalsIgnoreCase(type)
										&& deci != null && deci.length() > 0) { // 数字类型
									String vv = "";

									if (olList.size() > col) {
										vv = (String) olList.get(col);
									}
									// 处理小数点后面的位数
									if (vv.indexOf(".") != -1) {
										String str = PubFunc.round(vv, Integer
												.parseInt(deci));
										nolist.add(handlerZero(str));
									} else {
										int num = Integer.parseInt(deci);
										for (int l = 0; l < num; l++) {
											if (l == 0) {
												vv += "." + "0";
											} else {
												vv += "0";
											}
										}
										String str = PubFunc.round(vv, num);
										nolist.add(handlerZero(str));

									}

								} else {// 字符型
									String vv = "";
									if (olList.size() > col) {
										vv = (String) olList.get(col);
									}
									// 根据代码值查询描述
									if (codeSetId != null && codeSetId.length() > 0) {
										vv = getCodeDesc(codeSetId, vv.trim(),uplevel);
									}
									
									nolist.add(handlerZero(vv));
								}
							} catch (Exception exc) {
								throw GeneralExceptionHandler
										.Handle(new GeneralException(
												"sql语句中没有第" + col + "列"));
							}

							newarrValueList.add(nolist);
						}

						// 封装数据
						String cellvalie = put_in_orderValue2(newarrValueList);
						if (cellvalie == null || cellvalie.length() <= 0) {
                            cellvalie = "";
                        }
						// 将数据替换html中的变量名称
						doHtmlCell(cell_name, cellvalie, newarrValueList,Integer.parseInt(childStep));

					}
				} else {// 一个单元格的处理
					String cell_name = cell_Element
							.getAttributeValue("cell_name");
					// 根据代码值查询描述
					String codeSetId = cell_Element.getAttributeValue("codesetid");
					// 层级
					String level = cell_Element.getAttributeValue("uplevel");
					int uplevel = 0;
					if (level != null && "true".equalsIgnoreCase(level)) {
						uplevel = this.getuplevel();
					}
					
					if (codeSetId != null && codeSetId.length() > 0) {
						// 处理codesetid
						arrValueList = dealWithCodeSet(arrValueList, codeSetId, uplevel);
					}
					
					arrValueList  = newHandler(arrValueList);
					
					String cellvalie = put_in_orderValue2(arrValueList);
					doHtmlCell(cell_name, cellvalie, arrValueList, Integer.parseInt(step));
				}

			}
		}
		String sele = "";
		if (this.hashSQL) {
			sele = userdefineSqlFile.getSelectedListString(report_id);
		} else {
			//sele = userdefineSqlFile.getNoSqlHtmlString();

		}
		StringBuffer script = new StringBuffer();

		// 操作父窗体脚本
		script.append(" <script><!--\r\n");
		script.append("		if (window.opener != null) { \r\n");
		script.append(" 		try { \r\n");
		script.append(" 			if(window.opener.getMark() == 1) { \r\n");
		script.append("					window.opener.show();\r\n");
		script.append("				}\r\n");
		script.append("			} catch(e){} \r\n");
		script.append("		}\r\n");
		script.append("		-->\r\n");
		script.append(" </script>\r\n");

		// 替换html的body标签
		String regex = "<\\s*[B|b][O|o][D|d][Y|y].*?>";
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(htmlcontent);
		if (matcher.find()) {
			String replaceStr = matcher.group();
			htmlcontent = htmlcontent.replace(replaceStr, replaceStr + sele
					+ "<br/>");
			htmlcontent = htmlcontent + script;
		}

		// excel的文件名称,由随机字符窜组成
		String excel_filename = "";
		try {
			if (this.ext.indexOf(".") == -1) {
				excel_filename = PubFunc.getStrg() + "." + this.ext;
			} else {
				excel_filename = PubFunc.getStrg() + "" + this.ext;
			}

			// 将html写到某个路径下
			String path = url;
			File f = new File(path);
			if (!f.exists()) {
				f.mkdirs();
			}
			File file = null;
			FileWriter writer = null;
			try{				
				file = new File(path + System.getProperty("file.separator")
						+ excel_filename);
				writer = new FileWriter(file);
				BufferedWriter buffWriter = new BufferedWriter(writer);
				buffWriter.write(htmlcontent);
			}finally{
				PubFunc.closeIoResource(writer);
				PubFunc.closeIoResource(file);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		String[] str = new String[3];
		str[0] = excel_filename;
		str[1] = sele;
		str[2] = htmlcontent;
		return str;
	}

	
	/**
	 * 解析自定义html模板
	 * 
	 * @param url
	 *            String html的保存路径
	 * @return String[]
	 * @throws GeneralException
	 */
	public String[] analyseUserdefinedMhtReport(String url)
			throws GeneralException {
		// 配置数据
		startupConfigData();

		String sele = "";

		

		// excel的文件名称,由随机字符窜组成
		String excel_filename = "";
		FileWriter writer = null;
		BufferedWriter buffWriter = null;
		try {
			if (this.ext.indexOf(".") == -1) {
				excel_filename = PubFunc.getStrg() + "." + this.ext;
			} else {
				excel_filename = PubFunc.getStrg() + "" + this.ext;
			}

			// 将html写到某个路径下
			String path = url;
			File f = new File(path);
			if (!f.exists()) {
				f.mkdirs();
			}
			File file = new File(path + System.getProperty("file.separator")
					+ excel_filename);
			writer = new FileWriter(file);
			buffWriter = new BufferedWriter(writer);
			buffWriter.write(htmlcontent);
			//buffWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
		    PubFunc.closeIoResource(writer);
		    PubFunc.closeIoResource(buffWriter);
		}

		String[] str = new String[3];
		str[0] = excel_filename;
		str[1] = sele;
		str[2] = htmlcontent;
		return str;
	}
	
	/**
	 * 替换html中的数据
	 * @param cell_name String 自定义的变量名称
	 * @param cellvalie 
	 * @param arrValueList 
	 */
	private void doHtmlCell(String cell_name, String cellvalie,
			ArrayList arrValueList, int step) {
		if (!cell_name.contains(",")) {
			if (!cell_name.toLowerCase().contains("..end")) {
				if (cellvalie == null || cellvalie.length() <= 0) {
					this.htmlcontent = htmlcontent.replaceAll("\\b" + cell_name
							+ "\\b", "&nbsp;");
				} else {
					this.htmlcontent = htmlcontent.replaceAll("\\b" + cell_name
							+ "\\b", cellvalie);
				}
			} else {
				writeValueHTML(cell_name, cellvalie, arrValueList, step);
			}
		} else {
			if (!cell_name.toLowerCase().contains("..end")) {
				String[] cell_names = cell_name.split(",");
				for (int j = 0; j < cell_names.length; j++) {

					if (j < arrValueList.size()) {
						ArrayList list = new ArrayList();
						list.add(arrValueList.get(j));
						String cellvalie2 = put_in_orderValue(list);
						if (cellvalie2 == null || cellvalie2.length() <= 0) {
							this.htmlcontent = htmlcontent.replaceAll("\\b"
									+ cell_names[j] + "\\b", "&nbsp;");
						} else {
							this.htmlcontent = htmlcontent.replaceAll("\\b"
									+ cell_names[j] + "\\b", cellvalie2);
						}

					}
				}
			} else {
				writeValueHTML(cell_name, cellvalie, arrValueList, step);
			}
		}
	}

	/**
	 * 填写到excel中,处理有..end 的情况
	 * 
	 * @param cell_name
	 * @param sheet
	 * @param cellvalie
	 * @param arrValueList
	 */
	private void writeValueHTML(String cell_name, String cellvalie,
			ArrayList arrValueList,int step) {
		String[] cell_names = null;
		int rowNum = 0;
		String rowName = "";
		int start = 0;
		if (cell_name.contains(",")) {
			cell_names = cell_name.split(",");
			for (int j = 0; j < cell_names.length - 1; j++) {
				if (j < arrValueList.size()) {
					ArrayList list = new ArrayList();
					list.add(arrValueList.get(j));
					String cellvalie2 = put_in_orderValue(list);
					if (cellvalie2 == null || cellvalie2.length() <= 0) {
						this.htmlcontent = htmlcontent.replaceAll("\\b"
								+ cell_names[j] + "\\b", "&nbsp;");
					} else {
						this.htmlcontent = htmlcontent.replaceAll("\\b"
								+ cell_names[j] + "\\b", cellvalie2);
					}
					start = j;
				}

			}

			// 最后的n5..end
			cell_name = cell_names[cell_names.length - 1];
			int end = cell_name.indexOf("..");
			String cellName = cell_name.substring(0, end);
			rowNum = getExcelCellRow(cellName.replace("[@]", "")) + step;
			rowName = getHtmlRowname(cellName);
			start = start + 1;
			while (start < arrValueList.size()) {
				ArrayList list = new ArrayList();
				list.add(arrValueList.get(start));
				String cellvalie2 = put_in_orderValue(list);
				if (cellvalie2 == null || cellvalie2.length() <= 0) {
					this.htmlcontent = htmlcontent.replaceAll("\\b" + rowName
							+ rowNum + "\\b", "&nbsp;");
				} else {
					this.htmlcontent = htmlcontent.replaceAll("\\b" + rowName
							+ rowNum + "\\b", cellvalie2);
				}

				start++;
				rowNum++;
			}
		} else {
			int end = cell_name.indexOf("..");
			String cellName = cell_name.substring(0, end);
			cellName = cellName.replace("[@]", "");
			rowNum = getExcelCellRow(cellName) + step;
			rowName = getHtmlRowname(cellName);
			while (start < arrValueList.size()) {
				ArrayList list = new ArrayList();
				list.add(arrValueList.get(start));
				String cellvalie2 = put_in_orderValue(list);
				if (cellvalie2 == null || cellvalie2.length() <= 0) {
					this.htmlcontent = htmlcontent.replaceAll("\\b" + rowName
							+ rowNum + "\\b", "&nbsp;");
				} else {
					this.htmlcontent = htmlcontent.replaceAll("\\b" + rowName
							+ rowNum + "\\b", cellvalie2);
				}

				start++;
				rowNum++;
			}
		}
	}

	/**
	 * 获得某个单元格
	 * @param cellvalie
	 * @param colNum
	 * @param rowNum
	 * @param sheet
	 */
	private void greatCell(String cellvalie, int colNum, int rowNum, Sheet sheet,String type) {

		Row row = sheet.getRow(rowNum);
		if (row == null) {
			row = sheet.createRow(rowNum);
		}
		Cell cell = row.getCell(colNum);
		if (cell == null) {
			cell = row.createCell(colNum);
			CellStyle cellStyle = cell.getCellStyle();
			cellStyle.setWrapText(true);
			cell.setCellStyle(cellStyle);
			if ("N".equalsIgnoreCase(type)) {// 数字
				double value = 0;
				if (cellvalie != null && cellvalie.length() > 0) {
					value = Double.parseDouble(cellvalie);
				}				
				cell.setCellValue(value);
			} else {// 字符
				cell.setCellValue(cellvalie);
			}
		} else {
			CellStyle cellStyle = cell.getCellStyle();
			cellStyle.setWrapText(true);
			cell.setCellStyle(cellStyle);
			if ("N".equalsIgnoreCase(type)) {// 数字
				double value = 0;
				if (cellvalie != null && cellvalie.length() > 0) {
					value = Double.parseDouble(cellvalie);
				}				
				cell.setCellValue(value);
			} else {// 字符
				cell.setCellValue(new HSSFRichTextString(cellvalie));
			}
			
		}

	}

	/**
	 * excel方式将数据进行格式转化
	 * 
	 * @param twoDimList二维数据阵列
	 * @return String 单元格内容
	 */
	private String put_in_orderValue(ArrayList twoDimList) {
		if (twoDimList == null || twoDimList.size() <= 0) {
			return "";
		}

		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < twoDimList.size(); i++) {
			// 增加个方法判断list取出的对象是String类型还是ArrayList类型
			Object obj = twoDimList.get(i);
			if (obj instanceof String) {
				buf.append((String) obj);
			} else if (obj instanceof ArrayList) {
				ArrayList olist = (ArrayList) twoDimList.get(i);
				// 会车
				if (i > 0) {
					buf.append("\n");
				}

				// 列和列之间用空格
				for (int s = 0; s < olist.size(); s++) {
					if (s > 0) {
						buf.append("  ");
						buf.append(olist.get(s));
					} else {
						buf.append(olist.get(s));
					}
				}
			}

		}

		// 单元格内容
		return buf.toString();
	}

	/**
	 * excel方式将数据进行格式转化
	 * 
	 * @param twoDimList二维数据阵列
	 * @return String
	 */
	private String put_in_orderValue2(ArrayList twoDimList) {
		boolean tabNeed = false;
		if (twoDimList == null || twoDimList.size() <= 0) {
            return "";
        }
		StringBuffer buf = new StringBuffer();
		Object objList = twoDimList.get(0);
		if (objList instanceof ArrayList) {
			ArrayList inList = (ArrayList) objList;
			if (inList.size() > 1) {
				buf.append("<table border='0' cellpadding='0'");
				buf.append(" cellspacing=\"0\">");
				tabNeed = true;
			}
		}

		for (int i = 0; i < twoDimList.size(); i++) {
			// 增加个方法判断list取出的对象是String类型还是ArrayList类型
			Object obj = twoDimList.get(i);
			if (obj instanceof String) {
				buf.append((String) obj);
			} else if (obj instanceof ArrayList) {
				ArrayList olist = (ArrayList) twoDimList.get(i);
				if (tabNeed) {
					buf.append("<tr>");
				} else {
					if (i > 0) {
						buf.append("<br/>");
					}
				}
				for (int s = 0; s < olist.size(); s++) {
					if (s > 0) {
						buf.append("<td nowrap=\"nowrap\">&nbsp;");
						if (olist.get(s) != null) {
							buf.append(olist.get(s));
						} else {
							buf.append("&nbsp;");
						}
						buf.append("&nbsp;</td>");
					} else {
						if (tabNeed) {
							buf.append("<td nowrap=\"nowrap\">&nbsp;");
							if (olist.get(s) != null) {
								buf.append(olist.get(s));
							} else {
								buf.append("&nbsp;");
							}
							buf.append("&nbsp;</td>");
						} else {
							buf.append(olist.get(s));
						}

					}
				}

				if (tabNeed) {
					buf.append("</tr>");
				}
			}

		}

		if (tabNeed) {
			buf.append("</table>");
		}
		return buf.toString();
	}

	/**
	 * 计算excel行
	 * 
	 * @param cell_name
	 * @return String 行号，0为第一行，1为第二行。。。
	 */
	private int getExcelCellRow(String cell_name) {
		if (cell_name == null || cell_name.length() <= 0) {
            return -1;
        }
		String[] lettersUpper = { "0", "1", "2", "3", "4", "5", "6", "7", "8",
				"9" };
		byte[] charArr = cell_name.getBytes();
		int i = 0;
		int row = 0;
		StringBuffer buf = new StringBuffer();
		while (i < charArr.length) {
			char charStr = (char) (charArr[i]);
			for (int s = 0; s <= (lettersUpper.length - 1); s++) {
				String x = lettersUpper[s];
				if (x.equalsIgnoreCase(String.valueOf(charStr))) {
					buf.append(x);
					break;
				}
			}
			i++;
		}
		if (buf.length() > 0) {
            row = Integer.parseInt(buf.toString());
        }
		return --row;
	}

	/**
	 * 计算excel列
	 * 
	 * @param cell_name
	 * @return int 列,0为A列,1为B列
	 */
	private int getExcelCellCol(String cell_name) {
		if (cell_name == null || cell_name.length() <= 0) {
            return -1;
        }
		String[] lettersUpper = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
				"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
				"V", "W", "X", "Y", "Z" };
		byte[] charArr = cell_name.getBytes();
		int i = 0;
		int col = 0;
		ArrayList list = new ArrayList();
		while (i < charArr.length) {
			char charStr = (char) (charArr[i]);
			for (int s = 0; s < lettersUpper.length; s++) {
				String x = lettersUpper[s];
				if (x.equalsIgnoreCase(String.valueOf(charStr))) {
					list.add(new Integer(s + 1));
					break;
				}
			}
			i++;
		}

		// excel 最多256列，65536行
		if (list.size() > 1) {
			for (i = 0; i < list.size() - 1; i++) {
				int num = Integer.parseInt(list.get(i).toString());
				col = col + (num * 26);
			}
		}

		int ss = list.size() - 1;
		col = col + Integer.parseInt(list.get(ss).toString());

		return --col;
	}

	/**
	 * 获得名称的字母部分
	 * 
	 * @param cell_name
	 * @return
	 */
	private String getHtmlRowname(String cell_name) {
		String returnLe = "";
		String letter = "abcdefghijklmnopqrstuvwxyz";
		for (int i = 0; i < cell_name.length(); i++) {
			char ch = cell_name.toLowerCase().charAt(i);
			if (letter.indexOf(ch) != -1) {
				returnLe += cell_name.charAt(i);
			} else {
				break;
			}
		}

		return returnLe;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	/**
	 * 根据输入流获得文本内容
	 * 
	 * @param in
	 *            InputStream xml输入流
	 * @return String xml内容
	 */
	private String getStringFromStream(InputStream in) {
		StringBuffer str = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String read = "";
			// 按行读取，并加上换行符
			while ((read = reader.readLine()) != null) {
				str.append(read + "\r\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// xml内容
		return str.toString();
	}

	public HashMap getPublicParamMap() {
		return publicParamMap;
	}

	public void setPublicParamMap(HashMap publicParamMap) {
		this.publicParamMap = publicParamMap;
	}

	public String[] getNames() {
		return names;
	}

	public void setNames(String[] names) {
		this.names = names;
	}

	public String getHtmlcontent() {
		return htmlcontent;
	}

	public void setHtmlcontent(String htmlcontent) {
		this.htmlcontent = htmlcontent;
	}

	public String getExclepath() {
		return exclepath;
	}

	public void setExclepath(String exclepath) {
		this.exclepath = exclepath;
	}
	
	public HashMap getInputParamMap() {
		return inputParamMap;
	}

	public void setInputParamMap(HashMap inputParamMap) {
		this.inputParamMap = inputParamMap;
	}
}
