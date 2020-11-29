package com.hjsj.hrms.businessobject.general.orgmap;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import java.sql.Connection;
import java.util.List;

/**
 * 
 * <p>Title:生成机构图excel</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Dec 16, 2008:11:02:30 AM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class OrgMapExcelBo {
	private String orgtype="";
	public String getOrgtype() {
		return orgtype;
	}
	public void setOrgtype(String orgtype) {
		this.orgtype = orgtype;
	}
	int list[]= new int [20]; 
	int ka=0;
	int list2[]=new int [20];  
	
	int PAGESIZELIMIT = 1440000000; //原限制14400，不知是何含义？ 现改大些，相当于不限制。
	
	private HSSFFont fontBlackBlod12 = null;
	private HSSFFont fontBlack10 = null;
	
	private void createFont(HSSFWorkbook workbook){
		fontBlackBlod12 = workbook.createFont();
		fontBlackBlod12.setFontName("黑体");
		fontBlackBlod12.setFontHeightInPoints((short) 12); // 字体大小
		fontBlackBlod12.setBold(true);//粗体字
		
		fontBlack10 = workbook.createFont();
		fontBlack10.setFontName("黑体");
		fontBlack10.setFontHeightInPoints((short)10);
	}
	
	public void createOrgMap(List rs,List rootnode,String rootdesc,ParameterBo parameter,Connection conn,String code,boolean ishistory,HSSFWorkbook workbook){
		createFont(workbook);
		//wangcq 2014-12-03 begin
		//处理把颜色值转换成十六进制并放入一个数 
		String fontColor = parameter.getFontcolor();
		int[] color=new int[3]; 
		color[0]=Integer.parseInt(fontColor.substring(1, 3), 16); 
		color[1]=Integer.parseInt(fontColor.substring(3, 5), 16); 
		color[2]=Integer.parseInt(fontColor.substring(5, 7), 16); 
		//自定义颜色 
		HSSFPalette palette = workbook.getCustomPalette();
		short index = 12; //字体颜色用12作为代号
		palette.setColorAtIndex(index,(byte)color[0], (byte)color[1], (byte)color[2]); 
		fontBlack10.setColor(index);//设置字体颜色
		//wangcq 2014-12-03 end
		
		HSSFSheet sheet= workbook.createSheet("sheet1");
		if("false".equalsIgnoreCase(parameter.getIsshowpersonname())&& "true".equalsIgnoreCase(parameter.getIsshowpersonconut())){
			noname(0,rs,sheet,parameter,workbook,rootdesc,rootnode); //不显示人员，只显示人数
		}else if("true".equalsIgnoreCase(parameter.getIsshowpersonname())&& "true".equalsIgnoreCase(parameter.getIsshowpersonconut())){
			bianli(0,rs,sheet,workbook,rootdesc,rootnode,parameter);  //显示人员
		}else if("false".equalsIgnoreCase(parameter.getIsshowpersonname())&& "false".equalsIgnoreCase(parameter.getIsshowpersonconut())){
			/**姓名，人数都不显示**/
			nulnameconut(0,rs,sheet,parameter,workbook,rootdesc,rootnode);
		}else if("true".equalsIgnoreCase(parameter.getIsshowpersonname())&& "false".equalsIgnoreCase(parameter.getIsshowpersonconut())){
			/**显示人员，不显示人数 **/
			scalar(0,rs,sheet,parameter,workbook,rootdesc,rootnode);
		}
		
		
	}
	/**姓名，人数都不显示**/
	public void nulnameconut(int n, List rs, HSSFSheet sheet, ParameterBo parameter, HSSFWorkbook workbook,
			String rootdesc, List rootnode) {
		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		HSSFCell cell = null;
		HSSFRow row = null;
		CellRangeAddress region = null;
		if (parameter.getPagewidth() > PAGESIZELIMIT || parameter.getPageheight() > PAGESIZELIMIT) {
			sheet.addMergedRegion(new CellRangeAddress(3, 5, 2, 9));
			// row = sheet.createRow(3); // 定义是那一页的row
			row = sheet.getRow(3);
			if (row == null) {
                row = sheet.createRow(3);
            }

			cell = row.createCell((short) (2)); // 写入的单元各位置;
			// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellValue("输出的机构图超过了最大范围。请选择较小范围输出");
			cell.setCellStyle(this.setnull(workbook));
		} else {
			if (rootnode != null && !rootnode.isEmpty()) {
				// HSSFClientAnchor af = new HSSFClientAnchor(0, 0, 1023, 255,
				// (short) 1, 1, (short) 2, 2);
				region = new CellRangeAddress(1, 2, 1, 2);
				sheet.addMergedRegion(region);
				/** 画线 **/
				HSSFClientAnchor a1 = new HSSFClientAnchor();
				a1.setAnchor((short) 3, 2, 0, 0, (short) 4, 2, 0, 0);
				HSSFSimpleShape shape2 = patriarch.createSimpleShape(a1);
				shape2.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);

				// HSSFTextbox tex = patriarch.createTextbox(af);
				// tex.setString(new HSSFRichTextString(rootdesc));
				HSSFCellStyle cs = this.setBorder(workbook);
				for (int p = region.getFirstRow(); p <= region.getLastRow(); p++) {
					// row = sheet.createRow(p);
					row = sheet.getRow(p);
					if (row == null) {
                        row = sheet.createRow(p);
                    }

					for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
						cell = row.createCell((short) o);
						cell.setCellStyle(cs);
					}
				}
				// row = sheet.createRow(1); // 定义是那一页的row
				row = sheet.getRow(1);
				if (row == null) {
                    row = sheet.createRow(1);
                }

				cell = row.createCell((short) (1)); // 写入的单元各位置;
				// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(rootdesc);
				cell.setCellStyle(this.setDateStylesy(workbook, parameter));
				list[ka] = 2;
				list2[ka] = 2;
				ka++;
				for (int i = n; i < rs.size(); i++) {
					LazyDynaBean orgmapbean = (LazyDynaBean) rs.get(i);
					String codesetid = orgmapbean.get("codesetid").toString();
					String codeitemdesc = orgmapbean.get("text").toString();
					String grade = orgmapbean.get("grade").toString();
					String childs = orgmapbean.get("childs").toString();
					if ("0".equalsIgnoreCase(grade)) {
						// HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,
						// (short) 1, 1, (short) 2, 2);
						// HSSFTextbox textbox1 = patriarch.createTextbox(a);
						region = new CellRangeAddress(1, 2, 1, 2);
						sheet.addMergedRegion(region);
						for (int p = region.getFirstRow(); p <= region.getLastRow(); p++) {
							// row = sheet.createRow(p);
							row = sheet.getRow(p);
							if (row == null) {
                                row = sheet.createRow(p);
                            }

							for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
								cell = row.createCell((short) o);
								cell.setCellStyle(cs);
							}
						}
						// row = sheet.createRow(1); // 定义是那一页的row
						row = sheet.getRow(1);
						if (row == null) {
                            row = sheet.createRow(1);
                        }
						cell = row.createCell((short) (1)); // 写入的单元各位置;
						if ("".equals(codeitemdesc)) {
							// codeitemdesc="null";
							// textbox1.setString(new HSSFRichTextString(codeitemdesc));
							// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
							cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							cell.setCellValue(codeitemdesc);
							cell.setCellStyle(this.setDateStyle(workbook));
						} else {
							// textbox1.setString(new HSSFRichTextString(codeitemdesc));
							// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
							cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							cell.setCellValue(codeitemdesc);
							cell.setCellStyle(this.setDateStyle(workbook));
						}
						list[ka] = 2;
						list2[ka] = 2;
						ka++;
						continue;
					}
					if (Integer.parseInt(grade) == ka) {
						if (list[ka] == 0) {
							int s = list2[ka - 1];
							int s1 = list[ka - 1];
							// HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,
							// (short) (s+3), s1+1, (short) (s+4),s1+2);
							region = new CellRangeAddress(s1 + 1, s1 + 2, s + 3, s + 4);
							sheet.addMergedRegion(region);
							/** 画线 **/
							HSSFClientAnchor a11 = new HSSFClientAnchor();
							a11.setAnchor((short) (s + 2), s1 + 2, 0, 0, (short) (s + 3), s1 + 2, 0, 0);
							HSSFSimpleShape shape22 = patriarch.createSimpleShape(a11);
							shape22.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);

							HSSFClientAnchor a2 = new HSSFClientAnchor();
							a2.setAnchor((short) (s + 2), s1, 0, 0, (short) (s + 2), s1 + 2, 0, 0);
							HSSFSimpleShape shape3 = patriarch.createSimpleShape(a2);
							shape3.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
							if (!"0".equalsIgnoreCase(childs)) {

								HSSFClientAnchor a3 = new HSSFClientAnchor();
								a3.setAnchor((short) (s + 5), s1 + 2, 0, 0, (short) (s + 6), s1 + 2, 0, 0);
								HSSFSimpleShape shape4 = patriarch.createSimpleShape(a3);
								shape4.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
							}
							// HSSFTextbox textbox1 = patriarch.createTextbox(a);
							for (int p = region.getFirstRow(); p <= region.getLastRow(); p++) {
								// row = sheet.createRow(p);
								row = sheet.getRow(p);
								if (row == null) {
                                    row = sheet.createRow(p);
                                }

								for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
									cell = row.createCell((short) o);
									cell.setCellStyle(cs);
								}
							}
							// row = sheet.createRow(s1+1); // 定义是那一页的row
							row = sheet.getRow(s1 + 1);
							if (row == null) {
                                row = sheet.createRow(s1 + 1);
                            }

							cell = row.createCell((short) (s + 3)); // 写入的单元各位置;
							if ("".equals(codeitemdesc)) {
								// codeitemdesc="null";
								// textbox1.setString(new HSSFRichTextString(codeitemdesc));
								// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
								cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
								cell.setCellValue(codeitemdesc);
								cell.setCellStyle(this.setDateStyle(workbook));
							} else {
								// textbox1.setString(new HSSFRichTextString(codeitemdesc));
								// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
								cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
								cell.setCellValue(codeitemdesc);
								cell.setCellStyle(this.setDateStyle(workbook));
							}
							list[ka] = s1 + 2;
							list2[ka] = (s + 4);
						}
						ka++;
						continue;
					} else {
						int msx = 0;
						int gao = list[Integer.parseInt(grade) - 1];
						for (int j = 0; j < ka; j++) {
							if (msx < list[j]) {
								msx = list[j];
							}
						}
						int s = list2[Integer.parseInt(grade)];
						// HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,
						// (short) (s-1), msx+2, (short) s,msx+3);
						region = new CellRangeAddress(msx + 2, msx + 3, s - 1, s);
						sheet.addMergedRegion(region);
						/** 画线 **/
						HSSFClientAnchor a12 = new HSSFClientAnchor();
						a12.setAnchor((short) (s - 1), msx + 3, 0, 0, (short) (s - 2), msx + 3, 0, 0);
						HSSFSimpleShape shape1 = patriarch.createSimpleShape(a12);
						shape1.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);

						HSSFClientAnchor a2 = new HSSFClientAnchor();
						a2.setAnchor((short) (s - 2), msx + 3, 0, 0, (short) (s - 2), gao, 0, 0);
						HSSFSimpleShape shape22 = patriarch.createSimpleShape(a2);
						shape22.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);

						if (!"0".equalsIgnoreCase(childs)) {
							HSSFClientAnchor a3 = new HSSFClientAnchor();
							a3.setAnchor((short) (s + 1), msx + 3, 0, 0, (short) (s + 2), msx + 3, 0, 0);
							HSSFSimpleShape shape4 = patriarch.createSimpleShape(a3);
							shape4.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
						}
						// HSSFTextbox textbox1 = patriarch.createTextbox(a); //写入文字
						for (int p = region.getFirstRow(); p <= region.getLastRow(); p++) {
							// row = sheet.createRow(p);
							row = sheet.getRow(p);
							if (row == null) {
                                row = sheet.createRow(p);
                            }

							for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
								cell = row.createCell((short) o);
								cell.setCellStyle(cs);
							}
						}
						// row = sheet.createRow(msx+2); // 定义是那一页的row
						row = sheet.getRow(msx + 2);
						if (row == null) {
                            row = sheet.createRow(msx + 2);
                        }

						cell = row.createCell((short) (s - 1)); // 写入的单元各位置;
						if ("".equals(codeitemdesc)) {
							// codeitemdesc="null";
							// textbox1.setString(new HSSFRichTextString(codeitemdesc));
							// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
							cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							cell.setCellValue(codeitemdesc);
							cell.setCellStyle(this.setDateStyle(workbook));
						} else {
							// textbox1.setString(new HSSFRichTextString(codeitemdesc));
							// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
							cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							cell.setCellValue(codeitemdesc);
							cell.setCellStyle(this.setDateStyle(workbook));
						}
						list[Integer.parseInt(grade)] = msx + 3;

					}
				}
			} else {
				for (int i = n; i < rs.size(); i++) {
					LazyDynaBean orgmapbean = (LazyDynaBean) rs.get(i);
					String codesetid = orgmapbean.get("codesetid").toString();
					String codeitemdesc = orgmapbean.get("text").toString();
					String grade = orgmapbean.get("grade").toString();
					String childs = orgmapbean.get("childs").toString();
					if ("0".equalsIgnoreCase(grade)) {
						// HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,
						// (short) 1, 1, (short) 2, 2);
						region = new CellRangeAddress(1, 2, 1, 2);
						sheet.addMergedRegion(region);
						/** 画线 **/
						if (!"0".equalsIgnoreCase(childs)) {
							HSSFClientAnchor a1 = new HSSFClientAnchor();
							a1.setAnchor((short) 3, 2, 0, 0, (short) 4, 2, 0, 0);
							HSSFSimpleShape shape2 = patriarch.createSimpleShape(a1);
							shape2.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
						}
						// HSSFTextbox textbox1 = patriarch.createTextbox(a);
						// HSSFCellStyle cs=this.setBorder(workbook); //原来
						HSSFCellStyle cs = this.setDateStylesy(workbook, parameter); // 自定义颜色
						for (int p = region.getFirstRow(); p <= region.getLastRow(); p++) {
							// row = sheet.createRow(p);
							row = sheet.getRow(p);
							if (row == null) {
                                row = sheet.createRow(p);
                            }

							for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
								cell = row.createCell((short) o);
								cell.setCellStyle(cs);
							}
						}

						// row = sheet.createRow(1); // 定义是那一页的row
						row = sheet.getRow(1);
						if (row == null) {
                            row = sheet.createRow(1);
                        }
						cell = row.createCell((short) (1)); // 写入的单元各位置;
						if ("".equals(codeitemdesc)) {
							// codeitemdesc="null";
							// textbox1.setString(new HSSFRichTextString(codeitemdesc));
							// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
							cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							cell.setCellValue(codeitemdesc);
							cell.setCellStyle(this.setDateStyle(workbook));
						} else {
							// textbox1.setString(new HSSFRichTextString(codeitemdesc));
							// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
							cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							cell.setCellValue(codeitemdesc);
							cell.setCellStyle(this.setDateStyle(workbook));
						}
						list[ka] = 2;
						list2[ka] = 2;
						ka++;
						continue;
					}
					if (Integer.parseInt(grade) == ka) {
						if (list[ka] == 0) {
							int s = list2[ka - 1];
							int s1 = list[ka - 1];
							// HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,
							// (short) (s+3), s1+1, (short) (s+4),s1+2);
							region = new CellRangeAddress(s1 + 1, s1 + 2, s + 3, s + 4);
							sheet.addMergedRegion(region);
							/** 线 **/
							HSSFClientAnchor a1 = new HSSFClientAnchor();
							a1.setAnchor((short) (s + 2), s1 + 2, 0, 0, (short) (s + 3), s1 + 2, 0, 0);
							HSSFSimpleShape shape2 = patriarch.createSimpleShape(a1);
							shape2.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);

							HSSFClientAnchor a2 = new HSSFClientAnchor();
							a2.setAnchor((short) (s + 2), s1, 0, 0, (short) (s + 2), s1 + 2, 0, 0);
							HSSFSimpleShape shape3 = patriarch.createSimpleShape(a2);
							shape3.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
							if (!"0".equalsIgnoreCase(childs)) {

								HSSFClientAnchor a3 = new HSSFClientAnchor();
								a3.setAnchor((short) (s + 5), s1 + 2, 0, 0, (short) (s + 6), s1 + 2, 0, 0);
								HSSFSimpleShape shape4 = patriarch.createSimpleShape(a3);
								shape4.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
							}
							// HSSFTextbox textbox1 = patriarch.createTextbox(a);
							HSSFCellStyle cs = this.setBorder(workbook);
							for (int p = region.getFirstRow(); p <= region.getLastRow(); p++) {
								// row = sheet.createRow(p);
								row = sheet.getRow(p);
								if (row == null) {
                                    row = sheet.createRow(p);
                                }

								for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
									cell = row.createCell((short) o);
									cell.setCellStyle(cs);
								}
							}
							// row = sheet.createRow(s1+1); // 定义是那一页的row
							row = sheet.getRow(s1 + 1);
							if (row == null) {
                                row = sheet.createRow(s1 + 1);
                            }

							cell = row.createCell((short) (s + 3)); // 写入的单元各位置;
							if ("".equals(codeitemdesc)) {
								// codeitemdesc="null";
								// textbox1.setString(new HSSFRichTextString(codeitemdesc));
								// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
								cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
								cell.setCellValue(codeitemdesc);
								cell.setCellStyle(this.setDateStyle(workbook));
							} else {
								// textbox1.setString(new HSSFRichTextString(codeitemdesc));
								// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
								cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
								cell.setCellValue(codeitemdesc);
								cell.setCellStyle(this.setDateStyle(workbook));
							}
							list[ka] = s1 + 2;
							list2[ka] = (s + 4);
						}
						ka++;
						continue;
					} else {
						int msx = 0;
						int gao = list[Integer.parseInt(grade) - 1];
						for (int j = 0; j < ka; j++) {
							if (msx < list[j]) {
								msx = list[j];
							}
						}
						int s = list2[Integer.parseInt(grade)];
						// HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,
						// (short) (s-1), msx+2, (short) s,msx+3);
						region = new CellRangeAddress(msx + 2, msx + 3, s - 1, s);
						sheet.addMergedRegion(region);
						/** 线 **/
						HSSFClientAnchor a1 = new HSSFClientAnchor();
						a1.setAnchor((short) (s - 1), msx + 3, 0, 0, (short) (s - 2), msx + 3, 0, 0);
						HSSFSimpleShape shape1 = patriarch.createSimpleShape(a1);
						shape1.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);

						HSSFClientAnchor a2 = new HSSFClientAnchor();
						a2.setAnchor((short) (s - 2), msx + 3, 0, 0, (short) (s - 2), gao, 0, 0);
						HSSFSimpleShape shape2 = patriarch.createSimpleShape(a2);
						shape2.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);

						if (!"0".equalsIgnoreCase(childs)) {
							HSSFClientAnchor a3 = new HSSFClientAnchor();
							a3.setAnchor((short) (s + 1), msx + 3, 0, 0, (short) (s + 2), msx + 3, 0, 0);
							HSSFSimpleShape shape4 = patriarch.createSimpleShape(a3);
							shape4.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
						}
						// HSSFTextbox textbox1 = patriarch.createTextbox(a);
						HSSFCellStyle cs = this.setBorder(workbook);
						for (int p = region.getFirstRow(); p <= region.getLastRow(); p++) {
							// row = sheet.createRow(p);
							row = sheet.getRow(p);
							if (row == null) {
                                row = sheet.createRow(p);
                            }

							for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
								cell = row.createCell((short) o);
								cell.setCellStyle(cs);
							}
						}
						// row = sheet.createRow(msx+2); // 定义是那一页的row
						row = sheet.getRow(msx + 2);
						if (row == null) {
                            row = sheet.createRow(msx + 2);
                        }

						cell = row.createCell((short) (s - 1)); // 写入的单元各位置;
						if ("".equals(codeitemdesc)) {
							// codeitemdesc="null";
							// textbox1.setString(new HSSFRichTextString(codeitemdesc));
							// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
							cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							cell.setCellValue(codeitemdesc);
							cell.setCellStyle(this.setDateStyle(workbook));
						} else {
							// textbox1.setString(new HSSFRichTextString(codeitemdesc));
							// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
							cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							cell.setCellValue(codeitemdesc);
							cell.setCellStyle(this.setDateStyle(workbook));
						}
						list[Integer.parseInt(grade)] = msx + 3;

					}
				}
			}
		}
	}
	/**不显示姓名但是显示人数 **/
	public void noname(int n,List rs,HSSFSheet sheet,ParameterBo parameter,HSSFWorkbook workbook,String rootdesc,List rootnode){
		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		HSSFCell cell=null;
		HSSFRow row=null;
		CellRangeAddress region=null;
		if(parameter.getPagewidth()>PAGESIZELIMIT || parameter.getPageheight()>PAGESIZELIMIT){
			region= new CellRangeAddress(3, 5, 2, 9);
			sheet.addMergedRegion(region);
//			row = sheet.createRow(3); // 定义是那一页的row
			row = sheet.getRow(3);
			if(row==null) {
                row = sheet.createRow(3);
            }

			cell=row.createCell((short)(2));  //写入的单元各位置;
//			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    		cell.setCellValue("输出的机构图超过了最大范围。请选择较小范围输出");
    		cell.setCellStyle(this.setnull(workbook));
		}else{
		if(rootnode!=null && !rootnode.isEmpty()){
//			HSSFClientAnchor af = new HSSFClientAnchor(0, 0, 1023, 255,    
//	                (short) 1, 1, (short) 2, 2);
//			合并单元格
			region = new CellRangeAddress(1, 2, 1, 2);
			sheet.addMergedRegion(region);
			/** 横线**/
			HSSFClientAnchor a1=new HSSFClientAnchor();
			a1.setAnchor((short) 3, 2, 0, 0, (short) 4,2, 0, 0);
			HSSFSimpleShape shape2 = patriarch.createSimpleShape(a1);
			shape2.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
//			HSSFTextbox tex = patriarch.createTextbox(af); 
//			tex.setString(new HSSFRichTextString(rootdesc));
			HSSFCellStyle cs=this.setBorder(workbook);
			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//				row = sheet.createRow(p);
				row = sheet.getRow(p);
				if(row==null) {
                    row = sheet.createRow(p);
                }

	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
	            	cell = row.createCell((short)o);
	                cell.setCellStyle(cs);
	            }
			}
//			row = sheet.createRow(1); // 定义是那一页的row
			row = sheet.getRow(1);
			if(row==null) {
                row = sheet.createRow(1);
            }
			cell=row.createCell((short)(1));  //写入的单元各位置;
//			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    		cell.setCellValue(rootdesc); 
    		cell.setCellStyle(this.setDateStylesy(workbook,parameter));
			list[ka] = 2; 
			list2[ka]=2;
			ka++;  
			for(int i = n;i < rs.size();i++){
				LazyDynaBean orgmapbean = (LazyDynaBean)rs.get(i);
				String codesetid=orgmapbean.get("codesetid").toString();
				String codeitemdesc = orgmapbean.get("text").toString();
				String grade = orgmapbean.get("grade").toString();
				String childs = orgmapbean.get("childs").toString();
				if("0".equalsIgnoreCase(grade)){
//					HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,    
//			                (short) 1, 1, (short) 2, 2);
					region = new CellRangeAddress(1, 2, 1, 2);
					sheet.addMergedRegion(region);
					for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//						row = sheet.createRow(p);
						row = sheet.getRow(p);
						if(row==null) {
                            row = sheet.createRow(p);
                        }
			            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
			            	cell = row.createCell((short)o);
			                cell.setCellStyle(cs);
			            }
					}
//							HSSFTextbox textbox1 = patriarch.createTextbox(a); 
//							row = sheet.createRow(1); // 定义是那一页的row
							row = sheet.getRow(1);
							if(row==null) {
                                row = sheet.createRow(1);
                            }

							cell=row.createCell((short)(1));  //写入的单元各位置;
							if("".equals(codeitemdesc)){
								//codeitemdesc="null";
//								textbox1.setString(new HSSFRichTextString(codeitemdesc));
//								cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					    		cell.setCellValue(codeitemdesc); 
					    		cell.setCellStyle(this.setDateStyle(workbook));
							}else{
//								textbox1.setString(new HSSFRichTextString(codeitemdesc));
//								cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					    		cell.setCellValue(codeitemdesc); 
					    		cell.setCellStyle(this.setDateStyle(workbook));
							}
							list[ka] = 2; 
							list2[ka]=2;
							ka++;  
							continue;
				}
				if(Integer.parseInt(grade) == ka){
					if(list[ka]==0){
						int s=list2[ka-1]; 
						int s1=list[ka-1];
//						HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,    
//				                (short) (s+3), s1+1, (short) (s+4),s1+2);
						region= new CellRangeAddress(s1 + 1, s1 + 2, s + 3, s + 4);
						sheet.addMergedRegion(region);
						/** 线**/
						HSSFClientAnchor a11=new HSSFClientAnchor();
						a11.setAnchor((short) (s+2), s1+2, 0, 0, (short) (s+3),s1+2, 0, 0);
						HSSFSimpleShape shape22 = patriarch.createSimpleShape(a11);
						shape22.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
						
						HSSFClientAnchor a2=new HSSFClientAnchor();
						a2.setAnchor((short) (s+2), s1, 0, 0, (short) (s+2),s1+2, 0, 0);
						HSSFSimpleShape shape3 = patriarch.createSimpleShape(a2);
						shape3.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
						if(!"0".equalsIgnoreCase(childs)){
							
							HSSFClientAnchor a3=new HSSFClientAnchor();
							a3.setAnchor((short) (s+5), s1+2, 0, 0, (short) (s+6),s1+2, 0, 0);
							HSSFSimpleShape shape4 = patriarch.createSimpleShape(a3);
							shape4.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
						}
//						HSSFTextbox textbox1 = patriarch.createTextbox(a); 
						for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//							row = sheet.createRow(p);
							row = sheet.getRow(p);
							if(row==null) {
                                row = sheet.createRow(p);
                            }
				            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
				            	cell = row.createCell((short)o);
				                cell.setCellStyle(cs);
				            }
						}
//						row = sheet.createRow(s1+1); // 定义是那一页的row
						row = sheet.getRow(s1+1);
						if(row==null) {
                            row = sheet.createRow(s1+1);
                        }

						cell=row.createCell((short)(s+3));  //写入的单元各位置;
						if("".equals(codeitemdesc)){
							//codeitemdesc="null";
//							textbox1.setString(new HSSFRichTextString(codeitemdesc));
//							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				    		cell.setCellValue(codeitemdesc); 
				    		cell.setCellStyle(this.setDateStyle(workbook));
						}else{
//							textbox1.setString(new HSSFRichTextString(codeitemdesc));
//							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				    		cell.setCellValue(codeitemdesc); 
				    		cell.setCellStyle(this.setDateStyle(workbook));
						}
						list[ka]=s1+2;  
						list2[ka]=(s+4);
					}
					ka++;
					continue;
				}else{
					int msx=0;
					int gao = list[Integer.parseInt(grade)-1];
					for(int j=0;j<ka;j++){
						if(msx<list[j]){
							msx=list[j];
						}
					}
					int s=list2[Integer.parseInt(grade)]; 
//					HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,    
//			                (short) (s-1), msx+2, (short) s,msx+3);
					region= new CellRangeAddress(msx + 2, msx+3,  s-1, s);
					sheet.addMergedRegion(region);
					/**线**/
					HSSFClientAnchor a12=new HSSFClientAnchor();
					a12.setAnchor((short) (s-1), msx+3, 0, 0, (short) (s-2),msx+3, 0, 0);
					HSSFSimpleShape shape1 = patriarch.createSimpleShape(a12);
					shape1.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
					
					HSSFClientAnchor a2=new HSSFClientAnchor();
					a2.setAnchor((short) (s-2), msx+3, 0, 0, (short) (s-2),gao, 0, 0);
					HSSFSimpleShape shape22 = patriarch.createSimpleShape(a2);
					shape22.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
					
					if(!"0".equalsIgnoreCase(childs)){
						HSSFClientAnchor a3=new HSSFClientAnchor();
						a3.setAnchor((short) (s+1), msx+3, 0, 0, (short) (s+2),msx+3, 0, 0);
						HSSFSimpleShape shape4 = patriarch.createSimpleShape(a3);
						shape4.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
					}
//					HSSFTextbox textbox1 = patriarch.createTextbox(a); //写入文字
					for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//						row = sheet.createRow(p);
						row = sheet.getRow(p);
						if(row==null) {
                            row = sheet.createRow(p);
                        }
			            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
			            	cell = row.createCell((short)o);
			                cell.setCellStyle(cs);
			            }
					}
//					row = sheet.createRow(msx+2); // 定义是那一页的row
					row = sheet.getRow(msx+2);
					if(row==null) {
                        row = sheet.createRow(msx+2);
                    }

					cell=row.createCell((short)(s-1));  //写入的单元各位置;
					if("".equals(codeitemdesc)){
						//codeitemdesc="null";
//						textbox1.setString(new HSSFRichTextString(codeitemdesc));
//						cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			    		cell.setCellValue(codeitemdesc); 
			    		cell.setCellStyle(this.setDateStyle(workbook));
					}else{
//						textbox1.setString(new HSSFRichTextString(codeitemdesc));
//						cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			    		cell.setCellValue(codeitemdesc); 
			    		cell.setCellStyle(this.setDateStyle(workbook));
					}
					list[Integer.parseInt(grade)]=msx+3;
					
				}
			}
		}else{
			for(int i = n;i < rs.size();i++){
				LazyDynaBean orgmapbean = (LazyDynaBean)rs.get(i);
				String codesetid=orgmapbean.get("codesetid").toString();
				String codeitemdesc = orgmapbean.get("text").toString();
				String grade = orgmapbean.get("grade").toString();
				String childs = orgmapbean.get("childs").toString();
				if("0".equalsIgnoreCase(grade)){
//					HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,    
//			                (short) 1, 1, (short) 2, 2);
					region= new CellRangeAddress(1, 2, 1, 2);
					sheet.addMergedRegion(region);
						/** 横线**/
						if(!"0".equalsIgnoreCase(childs)){
							HSSFClientAnchor a1=new HSSFClientAnchor();
							a1.setAnchor((short) 3, 2, 0, 0, (short) 4,2, 0, 0);
							HSSFSimpleShape shape2 = patriarch.createSimpleShape(a1);
							shape2.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
						}
//							HSSFTextbox textbox1 = patriarch.createTextbox(a);
//						HSSFCellStyle cs=this.setBorder(workbook); 原来
						HSSFCellStyle cs=this.setDateStylesy(workbook, parameter);  //自定义颜色
						for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//							row = sheet.createRow(p);
							row = sheet.getRow(p);
							if(row==null) {
                                row = sheet.createRow(p);
                            }
				            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
				            	cell = row.createCell((short)o);
				                cell.setCellStyle(cs);
				            }
						}
//						row = sheet.createRow(1); // 定义是那一页的row
						row = sheet.getRow(1);
						if(row==null) {
                            row = sheet.createRow(1);
                        }
						cell=row.createCell((short)(1));  //写入的单元各位置;
							if("".equals(codeitemdesc)){
								//codeitemdesc="null";
//								textbox1.setString(new HSSFRichTextString(codeitemdesc));
//								cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					    		cell.setCellValue(codeitemdesc); 
					    		cell.setCellStyle(this.setDateStyle(workbook));
							}else{
//								textbox1.setString(new HSSFRichTextString(codeitemdesc));
//								cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					    		cell.setCellValue(codeitemdesc); 
					    		cell.setCellStyle(this.setDateStyle(workbook));
							}
							list[ka] = 2; 
							list2[ka]=2;
							ka++;  
							continue;
				}
				if(Integer.parseInt(grade) == ka){
					if(list[ka]==0){
						int s=list2[ka-1]; //宽
						int s1=list[ka-1];
//						HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,    
//				                (short) (s+3), s1+1, (short) (s+4),s1+2);
						region= new CellRangeAddress(s1+1, s1+2, s+3, s+4);
						sheet.addMergedRegion(region);
						/** 线**/
						HSSFClientAnchor a1=new HSSFClientAnchor();
						a1.setAnchor((short) (s+2), s1+2, 0, 0, (short) (s+3),s1+2, 0, 0);
						HSSFSimpleShape shape2 = patriarch.createSimpleShape(a1);
						shape2.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
						
						HSSFClientAnchor a2=new HSSFClientAnchor();
						a2.setAnchor((short) (s+2), s1, 0, 0, (short) (s+2),s1+2, 0, 0);
						HSSFSimpleShape shape3 = patriarch.createSimpleShape(a2);
						shape3.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
						if(!"0".equalsIgnoreCase(childs)){
							
							HSSFClientAnchor a3=new HSSFClientAnchor();
							a3.setAnchor((short) (s+5), s1+2, 0, 0, (short) (s+6),s1+2, 0, 0);
							HSSFSimpleShape shape4 = patriarch.createSimpleShape(a3);
							shape4.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
						}
//						HSSFTextbox textbox1 = patriarch.createTextbox(a); //写入文字
						HSSFCellStyle cs=this.setBorder(workbook);
						for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//							row = sheet.createRow(p);
							row = sheet.getRow(p);
							if(row==null) {
                                row = sheet.createRow(p);
                            }
				            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
				            	cell = row.createCell((short)o);
				                cell.setCellStyle(cs);
				            }
						}
//						row = sheet.createRow(s1+1); // 定义是那一页的row
						row = sheet.getRow(s1+1);
						if(row==null) {
                            row = sheet.createRow(s1+1);
                        }

						cell=row.createCell((short)(s+3));  //写入的单元各位置;
						if("".equals(codeitemdesc)){
							//codeitemdesc="null";
//							textbox1.setString(new HSSFRichTextString(codeitemdesc));
//							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				    		cell.setCellValue(codeitemdesc); 
				    		cell.setCellStyle(this.setDateStyle(workbook));
						}else{
//							textbox1.setString(new HSSFRichTextString(codeitemdesc));
//							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				    		cell.setCellValue(codeitemdesc); 
				    		cell.setCellStyle(this.setDateStyle(workbook));
						}
						list[ka]=s1+2;  
						list2[ka]=(s+4);
					}
					ka++;
					continue;
				}else{
					int msx=0;
					int gao = list[Integer.parseInt(grade)-1]; 
					for(int j=0;j<ka;j++){
						if(msx<list[j]){
							msx=list[j];
						}
					}
					int s=list2[Integer.parseInt(grade)]; 
//					HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,    
//			                (short) (s-1), msx+2, (short) s,msx+3);
					region = new CellRangeAddress(msx+2, msx+3, s-1, s);
					sheet.addMergedRegion(region);
					/**线**/
					HSSFClientAnchor a1=new HSSFClientAnchor();
					a1.setAnchor((short) (s-1), msx+3, 0, 0, (short) (s-2),msx+3, 0, 0);
					HSSFSimpleShape shape1 = patriarch.createSimpleShape(a1);
					shape1.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
					
					HSSFClientAnchor a2=new HSSFClientAnchor();
					a2.setAnchor((short) (s-2), msx+3, 0, 0, (short) (s-2),gao, 0, 0);
					HSSFSimpleShape shape2 = patriarch.createSimpleShape(a2);
					shape2.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
					
					if(!"0".equalsIgnoreCase(childs)){
						HSSFClientAnchor a3=new HSSFClientAnchor();
						a3.setAnchor((short) (s+1), msx+3, 0, 0, (short) (s+2),msx+3, 0, 0);
						HSSFSimpleShape shape4 = patriarch.createSimpleShape(a3);
						shape4.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
					}
//					HSSFTextbox textbox1 = patriarch.createTextbox(a); //写入文字
					HSSFCellStyle cs=this.setBorder(workbook);
					for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//						row = sheet.createRow(p);
						row = sheet.getRow(p);
						if(row==null) {
                            row = sheet.createRow(p);
                        }
			            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
			            	cell = row.createCell((short)o);
			                cell.setCellStyle(cs);
			            }
					}
//					row = sheet.createRow(msx+2); // 定义是那一页的row
					row = sheet.getRow(msx+2);
					if(row==null) {
                        row = sheet.createRow(msx+2);
                    }

					cell=row.createCell((short)(s-1));  //写入的单元各位置;
					if("".equals(codeitemdesc)){
						//codeitemdesc="null";
//						textbox1.setString(new HSSFRichTextString(codeitemdesc));
//						cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			    		cell.setCellValue(codeitemdesc); 
			    		cell.setCellStyle(this.setDateStyle(workbook));
					}else{
//						textbox1.setString(new HSSFRichTextString(codeitemdesc));
//						cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			    		cell.setCellValue(codeitemdesc); 
			    		cell.setCellStyle(this.setDateStyle(workbook));
					}
					list[Integer.parseInt(grade)]=msx+3;
					
				}
			}
		}
		}
	}
	/**显示人员与人数**/
	public void bianli(int n,List rs,HSSFSheet sheet,HSSFWorkbook workbook,String rootdesc,List rootnode,ParameterBo parameter){
		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();//定义一个顶级图片编辑器；
		HSSFCell cell=null;
		HSSFRow row=null;
		CellRangeAddress region=null;
		if(parameter.getPagewidth()>PAGESIZELIMIT || parameter.getPageheight()>PAGESIZELIMIT){
			region= new CellRangeAddress(3, 5, 2, 9);
			sheet.addMergedRegion(region);
//			row = sheet.createRow(3); // 定义是那一页的row
			row = sheet.getRow(3);
			if(row==null) {
                row = sheet.createRow(3);
            }

			cell=row.createCell((short)(2));  //写入的单元各位置;
//			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    		cell.setCellValue("输出的机构图超过了最大范围。请选择较小范围输出");
    		cell.setCellStyle(this.setnull(workbook));
		}else{
		if(rootnode!=null && !rootnode.isEmpty()){
//			HSSFClientAnchor af = new HSSFClientAnchor(0, 0, 1023, 255,    
//	                (short) 1, 1, (short) 2, 2);
//			 合并单元格，参数，从第几行，该行的第几个单元格，到第几行，第几个单元格
			region= new CellRangeAddress(1,2,1,2);
			sheet.addMergedRegion(region);
			HSSFCellStyle cs=this.setBorder(workbook);
			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//				row = sheet.createRow(p);
				row = sheet.getRow(p);
				if(row==null) {
                    row = sheet.createRow(p);
                }
	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
	            	cell = row.createCell((short)o);
	                cell.setCellStyle(cs);
	            }
			}
//			row = sheet.createRow(1); // 定义是那一页的row
			row = sheet.getRow(1);
			if(row==null) {
                row = sheet.createRow(1);
            }
			cell=row.createCell((short)(1));  //写入的单元各位置;
//			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    		cell.setCellValue(rootdesc); 
    		cell.setCellStyle(this.setDateStylesy(workbook,parameter));
    		
			/** 横线**/
//			if(!childs.equalsIgnoreCase("0")){
				HSSFClientAnchor a1=new HSSFClientAnchor();
				a1.setAnchor((short) 3, 2, 0, 0, (short) 4,2, 0, 0);
				HSSFSimpleShape shape2 = patriarch.createSimpleShape(a1);
				shape2.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
//			}
//			HSSFTextbox tex = patriarch.createTextbox(af); //生成文本框
//			tex.setString(new HSSFRichTextString(rootdesc));//写入文字
			list[ka] = 2; //高
			list2[ka]=2;
			ka++;  //等于0的时候ka++
			for(int i = n;i < rs.size();i++){
				
				LazyDynaBean orgmapbean = (LazyDynaBean)rs.get(i);
				String codesetid=orgmapbean.get("codesetid").toString();
				String codeitemdesc = orgmapbean.get("text")!=null?orgmapbean.get("text").toString():"";
				String personcount =orgmapbean.get("personcount").toString();
				String grade = orgmapbean.get("grade").toString();
				String childs = orgmapbean.get("childs").toString();
				if("0".equalsIgnoreCase(grade)){
//					HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,    
//	                (short) 1, 1, (short) 2, 2);
//					HSSFTextbox textbox1 = patriarch.createTextbox(a); 
//					 合并单元格，参数，从第几行，该行的第几个单元格，到第几行，第几个单元格
					region= new CellRangeAddress(1, 2, 1, 2);
					sheet.addMergedRegion(region);
					if("zz".equalsIgnoreCase(codesetid)){
						if("".equals(codeitemdesc)){
							//codeitemdesc="null";
//							textbox1.setString(new HSSFRichTextString(codeitemdesc));
							HSSFCellStyle css=this.setBorder(workbook);
							for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//								row = sheet.createRow(p);
								row = sheet.getRow(p);
								if(row==null) {
                                    row = sheet.createRow(p);
                                }
					            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
					            	cell = row.createCell((short)o);
					                cell.setCellStyle(css);
					            }
							}
//							row = sheet.createRow(1); // 定义是那一页的row
							row = sheet.getRow(1);
							if(row==null) {
                                row = sheet.createRow(1);
                            }
							cell=row.createCell((short)(1));  //写入的单元各位置;
//							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				    		cell.setCellValue(codeitemdesc); //人员库授权
				    		cell.setCellStyle(this.setDateStyle(workbook));
						}else{
//							textbox1.setString(new HSSFRichTextString(codeitemdesc));
							HSSFCellStyle css=this.setBorder(workbook);
							for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//								row = sheet.createRow(p);
								row = sheet.getRow(p);
								if(row==null) {
                                    row = sheet.createRow(p);
                                }
					            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
					            	cell = row.createCell((short)o);
					                cell.setCellStyle(css);
					            }
							}
//							row = sheet.createRow(1); // 定义是那一页的row
							row = sheet.getRow(1);
							if(row==null) {
                                row = sheet.createRow(1);
                            }
							cell=row.createCell((short)(1));  //写入的单元各位置;
//							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				    		cell.setCellValue(codeitemdesc); //人员库授权
				    		cell.setCellStyle(this.setDateStyle(workbook));
						}
					}else{
//						textbox1.setString(new HSSFRichTextString(codeitemdesc+"("+personcount+"人)"));
						HSSFCellStyle css=this.setBorder(workbook);
						for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//							row = sheet.createRow(p);
							row = sheet.getRow(p);
							if(row==null) {
                                row = sheet.createRow(p);
                            }
				            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
				            	cell = row.createCell((short)o);
				                cell.setCellStyle(css);
				            }
						}
//						row = sheet.createRow(1); // 定义是那一页的row
						row = sheet.getRow(1);
						if(row==null) {
                            row = sheet.createRow(1);
                        }
						cell=row.createCell((short)(1));  //写入的单元各位置;
//						cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			    		cell.setCellValue(codeitemdesc+"("+personcount+"人)"); 
			    		cell.setCellStyle(this.setDateStyle(workbook));
					}
					list[ka] = 2; 
					list2[ka]=2;
					ka++;  
					continue;
				}
				if(Integer.parseInt(grade) == ka){
					if(list[ka]==0){
						int s=list2[ka-1]; 
						int s1=list[ka-1];
//						HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,    
//				                (short) (s+3), s1+1, (short) (s+4),s1+2);
//						 合并单元格，参数，从第几行，该行的第几个单元格，到第几行，第几个单元格
						region= new CellRangeAddress((s1+1),(s1+2),(short)(s+3),(short)(s+4));
						sheet.addMergedRegion(region);
						/** 线**/
						HSSFClientAnchor a11=new HSSFClientAnchor();
						a11.setAnchor((short) (s+2), s1+2, 0, 0, (short) (s+3),s1+2, 0, 0);
						HSSFSimpleShape shape22 = patriarch.createSimpleShape(a11);
						shape22.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
						
						HSSFClientAnchor a2=new HSSFClientAnchor();
						a2.setAnchor((short) (s+2), s1, 0, 0, (short) (s+2),s1+2, 0, 0);
						HSSFSimpleShape shape3 = patriarch.createSimpleShape(a2);
						shape3.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
						if(!"0".equalsIgnoreCase(childs)){
							
							HSSFClientAnchor a3=new HSSFClientAnchor();
							a3.setAnchor((short) (s+5), s1+2, 0, 0, (short) (s+6),s1+2, 0, 0);
							HSSFSimpleShape shape4 = patriarch.createSimpleShape(a3);
							shape4.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
						}
//						HSSFTextbox textbox1 = patriarch.createTextbox(a); 
						HSSFCellStyle css=this.setBorder(workbook);
						for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//							row = sheet.createRow(p);
							row = sheet.getRow(p);
							if(row==null) {
                                row = sheet.createRow(p);
                            }
				            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
				            	cell = row.createCell((short)o);
				                cell.setCellStyle(css);
				            }
						}
//						row = sheet.createRow(s1+1); // 定义是那一页的row
						row = sheet.getRow(s1+1);
						if(row==null) {
                            row = sheet.createRow(s1+1);
                        }
						cell=row.createCell((short)(s+3));  //写入的单元各位置;
						
						if("zz".equalsIgnoreCase(codesetid)){
						if("".equals(codeitemdesc)){
							//codeitemdesc="null";
//							textbox1.setString(new HSSFRichTextString(codeitemdesc));
//							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				    		cell.setCellValue(codeitemdesc); 
				    		cell.setCellStyle(this.setDateStyle(workbook));
						}else{
//							textbox1.setString(new HSSFRichTextString(codeitemdesc));
//							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				    		cell.setCellValue(codeitemdesc); 
				    		cell.setCellStyle(this.setDateStyle(workbook));
						}
					}else{
//						textbox1.setString(new HSSFRichTextString(codeitemdesc+"("+personcount+"人)"));
//						cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			    		cell.setCellValue(codeitemdesc+"("+personcount+"人)"); 
			    		cell.setCellStyle(this.setDateStyle(workbook));
					}
						list[ka]=s1+2;  
						list2[ka]=(s+4);
						
					}
					ka++;
					continue;
				}else{
					int msx=0;
					int gao = list[Integer.parseInt(grade)-1]; 
					for(int j=0;j<ka;j++){
						if(msx<list[j]){
							msx=list[j];
						}
					}
					
					int s=list2[Integer.parseInt(grade)]; //宽
//					HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,    
//			                (short) (s-1), msx+2, (short) s,msx+3);
//					 合并单元格，参数，从第几行，该行的第几个单元格，到第几行，第几个单元格
					region = new CellRangeAddress(msx+2,msx+3,(short)(s-1),(short)(s));
					sheet.addMergedRegion(region);
					/**线**/
					HSSFClientAnchor a12=new HSSFClientAnchor();
					a12.setAnchor((short) (s-1), msx+3, 0, 0, (short) (s-2),msx+3, 0, 0);
					HSSFSimpleShape shape1 = patriarch.createSimpleShape(a12);
					shape1.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
					
					HSSFClientAnchor a2=new HSSFClientAnchor();
					a2.setAnchor((short) (s-2), msx+3, 0, 0, (short) (s-2),gao, 0, 0);
					HSSFSimpleShape shape22 = patriarch.createSimpleShape(a2);
					shape22.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
					
					if(!"0".equalsIgnoreCase(childs)){
						HSSFClientAnchor a3=new HSSFClientAnchor();
						a3.setAnchor((short) (s+1), msx+3, 0, 0, (short) (s+2),msx+3, 0, 0);
						HSSFSimpleShape shape4 = patriarch.createSimpleShape(a3);
						shape4.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
					}
//					HSSFTextbox textbox1 = patriarch.createTextbox(a); //写入文字
					HSSFCellStyle css=this.setBorder(workbook);
					for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//						row = sheet.createRow(p);
						row = sheet.getRow(p);
						if(row==null) {
                            row = sheet.createRow(p);
                        }
			            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
			            	cell = row.createCell((short)o);
			                cell.setCellStyle(css);
			            }
					}
//					row = sheet.createRow(msx+2); // 定义是那一页的row
					row = sheet.getRow(msx+2);
					if(row==null) {
                        row = sheet.createRow(msx+2);
                    }
					cell=row.createCell((short)(s-1));  //写入的单元各位置;
					if("zz".equalsIgnoreCase(codesetid)){
					if("".equals(codeitemdesc)){
						//codeitemdesc="null";
//						textbox1.setString(new HSSFRichTextString(codeitemdesc));
//						cell.setEncoding(HSSFCell.ENCODING_UTF_16);
						cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						cell.setCellValue(codeitemdesc); 
						cell.setCellStyle(this.setDateStyle(workbook));
					}else{
//						textbox1.setString(new HSSFRichTextString(codeitemdesc));
//						cell.setEncoding(HSSFCell.ENCODING_UTF_16);
						cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						cell.setCellValue(codeitemdesc); 
						cell.setCellStyle(this.setDateStyle(workbook));
					}
				}else{
//					textbox1.setString(new HSSFRichTextString(codeitemdesc+"("+personcount+"人)"));
//					cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					cell.setCellValue(codeitemdesc+"("+personcount+"人)"); 
					cell.setCellStyle(this.setDateStyle(workbook));
				}
					list[Integer.parseInt(grade)]=msx+3;
					
				}
			}
		}else {
			for(int i = n;i < rs.size();i++){
				LazyDynaBean orgmapbean = (LazyDynaBean)rs.get(i);
				String codesetid=orgmapbean.get("codesetid").toString();
				String codeitemdesc = orgmapbean.get("text")!=null?orgmapbean.get("text").toString():"";
				String personcount =orgmapbean.get("personcount").toString();
				String grade = orgmapbean.get("grade").toString();
				String childs = orgmapbean.get("childs").toString();

				if("0".equalsIgnoreCase(grade)){
					/**矩形框**/
//					HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,    
//	                (short) 1, 1, (short) 2, 2);
//					HSSFSimpleShape shaple = patriarch.createSimpleShape(a);
//					shaple.setShapeType(HSSFSimpleShape.OBJECT_TYPE_RECTANGLE);
					// 合并单元格，参数，从第几行，该行的第几个单元格，到第几行，第几个单元格
					region = new CellRangeAddress(1,2,1,2);
					sheet.addMergedRegion(region);
					/**写入文字**/
//					HSSFTextbox textbox1 = patriarch.createTextbox(a);
//					textbox1.setString(new HSSFRichTextString("ffff"));
//					HSSFCellStyle cs=this.setBorder(workbook);
					HSSFCellStyle cs=this.setDateStylesy(workbook, parameter);
					for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//						row = sheet.createRow(p);
						row = sheet.getRow(p);
						if(row==null) {
                            row = sheet.createRow(p);
                        }
			            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
			            	cell = row.createCell((short)o);
			                cell.setCellStyle(cs);
			            }
					}
//					row = sheet.createRow(1); // 定义是那一页的row
					row = sheet.getRow(1);
					if(row==null) {
                        row = sheet.createRow(1);
                    }
					cell=row.createCell((short)(1));  //写入的单元各位置;
					if("zz".equalsIgnoreCase(codesetid) || "xn".equalsIgnoreCase(codesetid)){
						if(codeitemdesc==null&&codeitemdesc==""){
							//codeitemdesc="null";
//							textbox1.setString(new HSSFRichTextString(codeitemdesc));
//							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
							cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							cell.setCellValue(codeitemdesc); //人员库授权
							cell.setCellStyle(this.setDateStyle(workbook));
						}else{
//							textbox1.setString(new HSSFRichTextString(codeitemdesc));
//							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
							cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							cell.setCellValue(codeitemdesc); //人员库授权
							cell.setCellStyle(this.setDateStyle(workbook));
						}
					}else{
//						textbox1.setString(new HSSFRichTextString(codeitemdesc+"("+personcount+"人)"));
//						cell.setEncoding(HSSFCell.ENCODING_UTF_16);
						cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						cell.setCellValue(codeitemdesc+"("+personcount+"人)"); //人员库授权
						cell.setCellStyle(this.setDateStyle(workbook));
					}
					/** 线**/
					if(!"0".equalsIgnoreCase(childs)){
						HSSFClientAnchor a1=new HSSFClientAnchor();
						a1.setAnchor((short) 3, 2, 0, 0, (short) 4,2, 0, 0);
						HSSFSimpleShape shape2 = patriarch.createSimpleShape(a1);
						shape2.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
					}
//					HSSFSimpleShape shape1 = patriarch.createSimpleShape(a);
//		            shape1.setShapeType(HSSFSimpleShape.OBJECT_TYPE_RECTANGLE);
					list[ka] = 2; 
					list2[ka]=2;
					ka++;  
					continue;
				}
				if(Integer.parseInt(grade) == ka){
					if(list[ka]==0){
						int s=list2[ka-1]; 
						int s1=list[ka-1]; 
//						HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,    
//				                (short) (s+3), s1+1, (short) (s+4),s1+2);
						region= new CellRangeAddress(s1+1,s1+2,(s+3),(s+4));
						sheet.addMergedRegion(region);
						/** 线**/
						HSSFClientAnchor a1=new HSSFClientAnchor();
						a1.setAnchor((short) (s+2), s1+2, 0, 0, (short) (s+3),s1+2, 0, 0);
						HSSFSimpleShape shape2 = patriarch.createSimpleShape(a1);
						shape2.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
						
						HSSFClientAnchor a2=new HSSFClientAnchor();
						a2.setAnchor((short) (s+2), s1, 0, 0, (short) (s+2),s1+2, 0, 0);
						HSSFSimpleShape shape3 = patriarch.createSimpleShape(a2);
						shape3.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
						if(!"0".equalsIgnoreCase(childs)){
							
							HSSFClientAnchor a3=new HSSFClientAnchor();
							a3.setAnchor((short) (s+5), s1+2, 0, 0, (short) (s+6),s1+2, 0, 0);
							HSSFSimpleShape shape4 = patriarch.createSimpleShape(a3);
							shape4.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
						}
//						HSSFTextbox textbox1 = patriarch.createTextbox(a); //写入文字
						HSSFCellStyle cs=this.setBorder(workbook);
						for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//							row = sheet.createRow(p);
							row = sheet.getRow(p);
							if(row==null) {
                                row = sheet.createRow(p);
                            }
				            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
				            	cell = row.createCell((short)o);
				                cell.setCellStyle(cs);
				            }
						}
//						row = sheet.createRow(s1+1); // 定义是那一页的row
						row = sheet.getRow(s1+1);
						if(row==null) {
                            row = sheet.createRow(s1+1);
                        }
						cell=row.createCell((short)(s+3));  //写入的单元各位置;
						if("zz".equalsIgnoreCase(codesetid) || "xn".equalsIgnoreCase(codesetid)){
						if("".equals(codeitemdesc)){
							//codeitemdesc="null";
//							textbox1.setString(new HSSFRichTextString(codeitemdesc));
//							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				    		cell.setCellValue(codeitemdesc); 
				    		cell.setCellStyle(this.setDateStyle(workbook));
						}else{
//							textbox1.setString(new HSSFRichTextString(codeitemdesc));
//							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				    		cell.setCellValue(codeitemdesc); 
				    		cell.setCellStyle(this.setDateStyle(workbook));
						}
					}else{
//						textbox1.setString(new HSSFRichTextString(codeitemdesc+"("+personcount+"人)"));
//						cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			    		cell.setCellValue(codeitemdesc+"("+personcount+"人)"); 
			    		cell.setCellStyle(this.setDateStyle(workbook));
					}
						list[ka]=s1+2; 
						list2[ka]=(s+4);
						
					}
					ka++;
					continue;
				}else{
					int msx=0;
					int gao = list[Integer.parseInt(grade)-1]; 
					for(int j=0;j<ka;j++){
						if(msx<list[j]){
							msx=list[j];
						}
					}
					
					int s=list2[Integer.parseInt(grade)]; 
//					HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,    
//			                (short) (s-1), msx+2, (short) s,msx+3);
//					合并单元格，参数，从第几行，该行的第几个单元格，到第几行，第几个单元格
					region = new CellRangeAddress(msx+2,msx+3,(s-1),(s));
					sheet.addMergedRegion(region);
					/**线**/
					HSSFClientAnchor a1=new HSSFClientAnchor();
					a1.setAnchor((short) (s-1), msx+3, 0, 0, (short) (s-2),msx+3, 0, 0);
					HSSFSimpleShape shape1 = patriarch.createSimpleShape(a1);
					shape1.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
					
					HSSFClientAnchor a2=new HSSFClientAnchor();
					a2.setAnchor((short) (s-2), msx+3, 0, 0, (short) (s-2),gao, 0, 0);
					HSSFSimpleShape shape2 = patriarch.createSimpleShape(a2);
					shape2.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
					
					if(!"0".equalsIgnoreCase(childs)){
						HSSFClientAnchor a3=new HSSFClientAnchor();
						a3.setAnchor((short) (s+1), msx+3, 0, 0, (short) (s+2),msx+3, 0, 0);
						HSSFSimpleShape shape4 = patriarch.createSimpleShape(a3);
						shape4.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
					}
//					HSSFTextbox textbox1 = patriarch.createTextbox(a); //写入文字
					HSSFCellStyle cs=this.setBorder(workbook);
					for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//						row = sheet.createRow(p);
						row = sheet.getRow(p);
						if(row==null) {
                            row = sheet.createRow(p);
                        }
			            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
			            	cell = row.createCell((short)o);
			                cell.setCellStyle(cs);
			            }
					}
//					row = sheet.createRow(msx+2); // 定义是那一页的row
					row = sheet.getRow(msx+2);
					if(row==null) {
                        row = sheet.createRow(msx+2);
                    }
					cell=row.createCell((short)(s-1));  //写入的单元各位置;
					if("zz".equalsIgnoreCase(codesetid) || "xn".equalsIgnoreCase(codesetid)){
					if("".equals(codeitemdesc)){
						//codeitemdesc="null";
//						textbox1.setString(new HSSFRichTextString(codeitemdesc));
//						cell.setEncoding(HSSFCell.ENCODING_UTF_16);
						cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						cell.setCellValue(codeitemdesc); 
						cell.setCellStyle(this.setDateStyle(workbook));
					}else{
//						textbox1.setString(new HSSFRichTextString(codeitemdesc));
//						cell.setEncoding(HSSFCell.ENCODING_UTF_16);
						cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						cell.setCellValue(codeitemdesc); 
						cell.setCellStyle(this.setDateStyle(workbook));
					}
				}else{
//					textbox1.setString(new HSSFRichTextString(codeitemdesc+"("+personcount+"人)"));
//					cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					cell.setCellValue(codeitemdesc+"("+personcount+"人)"); 
					cell.setCellStyle(this.setDateStyle(workbook));
				}
					list[Integer.parseInt(grade)]=msx+3;
					
				}
			}
			

		}
		}
		
}
	/** 显示人员，不显示人数**/
	public void scalar(int n,List rs,HSSFSheet sheet,ParameterBo parameter,HSSFWorkbook workbook,String rootdesc,List rootnode){
		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		HSSFCell cell=null;
		HSSFRow row=null;
		CellRangeAddress region=null;
		if(parameter.getPagewidth()>PAGESIZELIMIT || parameter.getPageheight()>PAGESIZELIMIT){
			region= new CellRangeAddress(3,5,2,9);
			sheet.addMergedRegion(region);
//			row = sheet.createRow(3); // 定义是那一页的row
			row = sheet.getRow(3);
			if(row==null) {
                row = sheet.createRow(3);
            }
			cell=row.createCell((short)(2));  //写入的单元各位置;
//			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    		cell.setCellValue("输出的机构图超过了最大范围。请选择较小范围输出");
    		cell.setCellStyle(this.setnull(workbook));
		}else{
		if(rootnode!=null && !rootnode.isEmpty()){
//			HSSFClientAnchor af = new HSSFClientAnchor(0, 0, 1023, 255,    
//	                (short) 1, 1, (short) 2, 2);
			region= new CellRangeAddress(1,2,1,2);
			sheet.addMergedRegion(region);
			HSSFClientAnchor a1=new HSSFClientAnchor();
			a1.setAnchor((short) 3, 2, 0, 0, (short) 4,2, 0, 0);
			HSSFSimpleShape shape2 = patriarch.createSimpleShape(a1);
			shape2.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
//			HSSFTextbox tex = patriarch.createTextbox(af); 
//			tex.setString(new HSSFRichTextString(rootdesc));
			HSSFCellStyle cs=this.setBorder(workbook);
			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//				row = sheet.createRow(p);
				row = sheet.getRow(p);
				if(row==null) {
                    row = sheet.createRow(p);
                }
	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
	            	cell = row.createCell((short)o);
	                cell.setCellStyle(cs);
	            }
			}
//			row = sheet.createRow(1); // 定义是那一页的row
			row = sheet.getRow(1);
			if(row==null) {
                row = sheet.createRow(1);
            }
			cell=row.createCell((short)(1));  //写入的单元各位置;
//			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    		cell.setCellValue(rootdesc); 
    		cell.setCellStyle(this.setDateStylesy(workbook,parameter));
			list[ka] = 2; 
			list2[ka]=2;
			ka++;  //等于0的时候ka++
			for(int i = n;i < rs.size();i++){
				LazyDynaBean orgmapbean = (LazyDynaBean)rs.get(i);
				String codesetid=orgmapbean.get("codesetid").toString();
				String codeitemdesc = orgmapbean.get("text")!=null?orgmapbean.get("text").toString():"";
				String grade = orgmapbean.get("grade").toString();
				String childs = orgmapbean.get("childs").toString();
				if("0".equalsIgnoreCase(grade)){ //第一个永远都是固定的
//					HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,    
//			                (short) 1, 1, (short) 2, 2);
					region= new CellRangeAddress(1,2,(short)1,(short)2);
					sheet.addMergedRegion(region);
//							HSSFTextbox textbox1 = patriarch.createTextbox(a); //写入文字
					for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//						row = sheet.createRow(p);
						row = sheet.getRow(p);
						if(row==null) {
                            row = sheet.createRow(p);
                        }
			            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
			            	cell = row.createCell((short)o);
			                cell.setCellStyle(cs);
			            }
					}
//					row = sheet.createRow(1); // 定义是那一页的row
					row = sheet.getRow(1);
					if(row==null) {
                        row = sheet.createRow(1);
                    }
					cell=row.createCell((short)(1));  //写入的单元各位置;
							if("".equals(codeitemdesc)){
								//codeitemdesc="null";
//								textbox1.setString(new HSSFRichTextString(codeitemdesc));
//								cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					    		cell.setCellValue(codeitemdesc); 
					    		cell.setCellStyle(this.setDateStyle(workbook));
							}else{
//								textbox1.setString(new HSSFRichTextString(codeitemdesc));
//								cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					    		cell.setCellValue(codeitemdesc); 
					    		cell.setCellStyle(this.setDateStyle(workbook));
							}
							list[ka] = 2; //高
							list2[ka]=2;
							ka++;  //等于0的时候ka++
							continue;
				}
				if(Integer.parseInt(grade) == ka){
					if(list[ka]==0){
						int s=list2[ka-1]; //宽
						int s1=list[ka-1];
//						HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,    
//				                (short) (s+3), s1+1, (short) (s+4),s1+2);
						region= new CellRangeAddress(s1+1,s1+2,(short)(s+3),(short)(s+4));
						sheet.addMergedRegion(region);
						/** 线**/
						HSSFClientAnchor a11=new HSSFClientAnchor();
						a11.setAnchor((short) (s+2), s1+2, 0, 0, (short) (s+3),s1+2, 0, 0);
						HSSFSimpleShape shape22 = patriarch.createSimpleShape(a11);
						shape22.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
						
						HSSFClientAnchor a2=new HSSFClientAnchor();
						a2.setAnchor((short) (s+2), s1, 0, 0, (short) (s+2),s1+2, 0, 0);
						HSSFSimpleShape shape3 = patriarch.createSimpleShape(a2);
						shape3.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
						if(!"0".equalsIgnoreCase(childs)){
							
							HSSFClientAnchor a3=new HSSFClientAnchor();
							a3.setAnchor((short) (s+5), s1+2, 0, 0, (short) (s+6),s1+2, 0, 0);
							HSSFSimpleShape shape4 = patriarch.createSimpleShape(a3);
							shape4.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
						}
//						HSSFTextbox textbox1 = patriarch.createTextbox(a); //写入文字
						for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//							row = sheet.createRow(p);
							row = sheet.getRow(p);
							if(row==null) {
                                row = sheet.createRow(p);
                            }
				            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
				            	cell = row.createCell((short)o);
				                cell.setCellStyle(cs);
				            }
						}
//						row = sheet.createRow(s1+1); // 定义是那一页的row
						row = sheet.getRow(s1+1);
						if(row==null) {
                            row = sheet.createRow(s1+1);
                        }
						cell=row.createCell((short)(s+3));  //写入的单元各位置;
						if("".equals(codeitemdesc)){
							//codeitemdesc="null";
//							textbox1.setString(new HSSFRichTextString(codeitemdesc));
//							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				    		cell.setCellValue(codeitemdesc); 
				    		cell.setCellStyle(this.setDateStyle(workbook));
						}else{
//							textbox1.setString(new HSSFRichTextString(codeitemdesc));
//							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				    		cell.setCellValue(codeitemdesc); 
				    		cell.setCellStyle(this.setDateStyle(workbook));
						}
						list[ka]=s1+2;  
						list2[ka]=(s+4);
					}
					ka++;
					continue;
				}else{
					int msx=0;
					int gao = list[Integer.parseInt(grade)-1];
					for(int j=0;j<ka;j++){
						if(msx<list[j]){
							msx=list[j];
						}
					}
					int s=list2[Integer.parseInt(grade)]; //宽
//					HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,    
//			                (short) (s-1), msx+2, (short) s,msx+3);
					region= new CellRangeAddress(msx+2,msx+3,(short)(s-1),(short)s);
					sheet.addMergedRegion(region);
					/**前横线**/
					HSSFClientAnchor a12=new HSSFClientAnchor();
					a12.setAnchor((short) (s-1), msx+3, 0, 0, (short) (s-2),msx+3, 0, 0);
					HSSFSimpleShape shape1 = patriarch.createSimpleShape(a12);
					shape1.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
					/**竖线**/
					HSSFClientAnchor a2=new HSSFClientAnchor();
					a2.setAnchor((short) (s-2), msx+3, 0, 0, (short) (s-2),gao, 0, 0);
					HSSFSimpleShape shape22 = patriarch.createSimpleShape(a2);
					shape22.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
					/**后横线**/
					if(!"0".equalsIgnoreCase(childs)){
						HSSFClientAnchor a3=new HSSFClientAnchor();
						a3.setAnchor((short) (s+1), msx+3, 0, 0, (short) (s+2),msx+3, 0, 0);
						HSSFSimpleShape shape4 = patriarch.createSimpleShape(a3);
						shape4.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
					}
//					HSSFTextbox textbox1 = patriarch.createTextbox(a); //写入文字
					for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//						row = sheet.createRow(p);
						row = sheet.getRow(p);
						if(row==null) {
                            row = sheet.createRow(p);
                        }
			            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
			            	cell = row.createCell((short)o);
			                cell.setCellStyle(cs);
			            }
					}
//					row = sheet.createRow(msx+2); // 定义是那一页的row
					row = sheet.getRow(msx+2);
					if(row==null) {
                        row = sheet.createRow(msx+2);
                    }
					cell=row.createCell((short)(s-1));  //写入的单元各位置;
					if("".equals(codeitemdesc)){
						//codeitemdesc="null";
//						textbox1.setString(new HSSFRichTextString(codeitemdesc));
//						cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			    		cell.setCellValue(codeitemdesc); 
			    		cell.setCellStyle(this.setDateStyle(workbook));
					}else{
//						textbox1.setString(new HSSFRichTextString(codeitemdesc));
//						cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			    		cell.setCellValue(codeitemdesc); 
			    		cell.setCellStyle(this.setDateStyle(workbook));
					}
					list[Integer.parseInt(grade)]=msx+3;
					
				}
			}
		}else{
			for(int i = n;i < rs.size();i++){
				LazyDynaBean orgmapbean = (LazyDynaBean)rs.get(i);
				String codesetid=orgmapbean.get("codesetid").toString();
				String codeitemdesc = orgmapbean.get("text")!=null?orgmapbean.get("text").toString():"";
				String grade = orgmapbean.get("grade").toString();
				String childs = orgmapbean.get("childs").toString();
				if("0".equalsIgnoreCase(grade)){ //第一个永远都是固定的
//					HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,    
//			                (short) 1, 1, (short) 2, 2);
					region= new CellRangeAddress(1,2,1,2);
					sheet.addMergedRegion(region);
					/** 横线**/
					if(!"0".equalsIgnoreCase(childs)){
						HSSFClientAnchor a1=new HSSFClientAnchor();
						a1.setAnchor((short) 3, 2, 0, 0, (short) 4,2, 0, 0);
						HSSFSimpleShape shape2 = patriarch.createSimpleShape(a1);
						shape2.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
					}
//							HSSFTextbox textbox1 = patriarch.createTextbox(a); //写入文字
//					HSSFCellStyle cs=this.setBorder(workbook);  原来
					HSSFCellStyle cs=this.setDateStylesy(workbook,parameter); //自定义颜色
					for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//						row = sheet.createRow(p);
						row = sheet.getRow(p);
						if(row==null) {
                            row = sheet.createRow(p);
                        }
			            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
			            	cell = row.createCell((short)o);
			                cell.setCellStyle(cs);
			            }
					}
//					row = sheet.createRow(1); // 定义是那一页的row
					row = sheet.getRow(1);
					if(row==null) {
                        row = sheet.createRow(1);
                    }
					cell=row.createCell((short)(1));  //写入的单元各位置;
							if("".equals(codeitemdesc)){
								//codeitemdesc="null";
//								textbox1.setString(new HSSFRichTextString(codeitemdesc));
//								cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					    		cell.setCellValue(codeitemdesc); 
					    		cell.setCellStyle(this.setDateStyle(workbook));
							}else{
//								textbox1.setString(new HSSFRichTextString(codeitemdesc));
//								cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					    		cell.setCellValue(codeitemdesc); 
					    		cell.setCellStyle(this.setDateStyle(workbook));
							}
							list[ka] = 2; //高
							list2[ka]=2;
							ka++;  //等于0的时候ka++
							continue;
				}
				if(Integer.parseInt(grade) == ka){
					if(list[ka]==0){
						int s=list2[ka-1]; //宽
						int s1=list[ka-1];
//						HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,    
//				                (short) (s+3), s1+1, (short) (s+4),s1+2);
						region= new CellRangeAddress(s1+1,s1+2,(short)(s+3),(short)(s+4));
						sheet.addMergedRegion(region);
						/** 前横线**/
						HSSFClientAnchor a1=new HSSFClientAnchor();
						a1.setAnchor((short) (s+2), s1+2, 0, 0, (short) (s+3),s1+2, 0, 0);
						HSSFSimpleShape shape2 = patriarch.createSimpleShape(a1);
						shape2.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
						/**竖线**/
						HSSFClientAnchor a2=new HSSFClientAnchor();
						a2.setAnchor((short) (s+2), s1, 0, 0, (short) (s+2),s1+2, 0, 0);
						HSSFSimpleShape shape3 = patriarch.createSimpleShape(a2);
						shape3.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
						if(!"0".equalsIgnoreCase(childs)){
							/** 后横线**/
							HSSFClientAnchor a3=new HSSFClientAnchor();
							a3.setAnchor((short) (s+5), s1+2, 0, 0, (short) (s+6),s1+2, 0, 0);
							HSSFSimpleShape shape4 = patriarch.createSimpleShape(a3);
							shape4.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
						}
//						HSSFTextbox textbox1 = patriarch.createTextbox(a); //写入文字
						HSSFCellStyle cs=this.setBorder(workbook);
						for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//							row = sheet.createRow(p);
							row = sheet.getRow(p);
							if(row==null) {
                                row = sheet.createRow(p);
                            }
				            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
				            	cell = row.createCell((short)o);
				                cell.setCellStyle(cs);
				            }
						}
//						row = sheet.createRow(s1+1); // 定义是那一页的row
						row = sheet.getRow(s1+1);
						if(row==null) {
                            row = sheet.createRow(s1+1);
                        }
						cell=row.createCell((short)(s+3));  //写入的单元各位置;
						if("".equals(codeitemdesc)){
							//codeitemdesc="null";
//							textbox1.setString(new HSSFRichTextString(codeitemdesc));
//							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				    		cell.setCellValue(codeitemdesc); 
				    		cell.setCellStyle(this.setDateStyle(workbook));
						}else{
//							textbox1.setString(new HSSFRichTextString(codeitemdesc));
//							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				    		cell.setCellValue(codeitemdesc); 
				    		cell.setCellStyle(this.setDateStyle(workbook));
						}
						list[ka]=s1+2;  //高
						list2[ka]=(s+4);
					}
					ka++;
					continue;
				}else{
					int msx=0;
					int gao = list[Integer.parseInt(grade)-1];
					for(int j=0;j<ka;j++){
						if(msx<list[j]){
							msx=list[j];
						}
					}
					int s=list2[Integer.parseInt(grade)]; //宽
//					HSSFClientAnchor a = new HSSFClientAnchor(0, 0, 1023, 255,    
//			                (short) (s-1), msx+2, (short) s,msx+3);
					region= new CellRangeAddress(msx+2,msx+3,(short)(s-1),(short)s);
					sheet.addMergedRegion(region);
					/**前横线**/
					HSSFClientAnchor a1=new HSSFClientAnchor();
					a1.setAnchor((short) (s-1), msx+3, 0, 0, (short) (s-2),msx+3, 0, 0);
					HSSFSimpleShape shape1 = patriarch.createSimpleShape(a1);
					shape1.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
					/**竖线**/
					HSSFClientAnchor a2=new HSSFClientAnchor();
					a2.setAnchor((short) (s-2), msx+3, 0, 0, (short) (s-2),gao, 0, 0);
					HSSFSimpleShape shape2 = patriarch.createSimpleShape(a2);
					shape2.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
					/**后横线**/
					if(!"0".equalsIgnoreCase(childs)){
						HSSFClientAnchor a3=new HSSFClientAnchor();
						a3.setAnchor((short) (s+1), msx+3, 0, 0, (short) (s+2),msx+3, 0, 0);
						HSSFSimpleShape shape4 = patriarch.createSimpleShape(a3);
						shape4.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
					}
//					HSSFTextbox textbox1 = patriarch.createTextbox(a); //写入文字
					HSSFCellStyle cs=this.setBorder(workbook);
					for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//						row = sheet.createRow(p);
						row = sheet.getRow(p);
						if(row==null) {
                            row = sheet.createRow(p);
                        }
			            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
			            	cell = row.createCell((short)o);
			                cell.setCellStyle(cs);
			            }
					}
//					row = sheet.createRow(msx+2); // 定义是那一页的row
					row = sheet.getRow(msx+2);
					if(row==null) {
                        row = sheet.createRow(msx+2);
                    }
					cell=row.createCell((short)(s-1));  //写入的单元各位置;
					if("".equals(codeitemdesc)){
						//codeitemdesc="null";
//						textbox1.setString(new HSSFRichTextString(codeitemdesc));
//						cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			    		cell.setCellValue(codeitemdesc); 
			    		cell.setCellStyle(this.setDateStyle(workbook));
					}else{
//						textbox1.setString(new HSSFRichTextString(codeitemdesc));
//						cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			    		cell.setCellValue(codeitemdesc); 
			    		cell.setCellStyle(this.setDateStyle(workbook));
					}
					list[Integer.parseInt(grade)]=msx+3;
					
				}
			}
		}
		}
	}
	public int[] getOrgMapPageSize(ParameterBo parameter,String code,Connection conn,String username,String backdate) throws GeneralException
	{   //算出一个高度
		StringBuffer sqlstr=new StringBuffer();
		String isshowposname=parameter.getIsshowposname();	
		String isshowdeptname=parameter.getIsshowdeptname();
		boolean bShowPos=true;
		boolean bShowDept=true;
		if(isshowdeptname==null||isshowdeptname.length()<=0|| "false".equals(isshowdeptname)) {
            bShowDept=false;
        }
		if(isshowposname==null||isshowposname.length()<=0|| "false".equals(isshowposname)) {
            bShowPos=false;
        }
		if(this.orgtype!=null&& "vorg".equalsIgnoreCase(this.orgtype))
		{
			if("true".equalsIgnoreCase(parameter.getIsshowpersonname()))
			{
				if(code!=null && code.length()>0)
				{
					sqlstr.append("select aa.grade-bb.grade as grade,cc.counts + dd.counts AS leafagechildscount from ");
					sqlstr.append("(SELECT MAX(grade) + 1 as grade from ");
					sqlstr.append(username);
					sqlstr.append("organization where codeitemid like '");
					sqlstr.append(code);
					sqlstr.append("%') aa,");
					sqlstr.append("(SELECT grade FROM ");
					sqlstr.append(username);
					sqlstr.append("organization WHERE codeitemid = '");
					sqlstr.append(code);
					sqlstr.append("') bb,");
					sqlstr.append("(SELECT COUNT(*) as counts FROM ");
					sqlstr.append(username);
					sqlstr.append("organization b WHERE b.parentid LIKE '");
					sqlstr.append(code);
					sqlstr.append("%' AND b.codeitemid = b.childid and b.parentid like '");
					sqlstr.append(code);
					sqlstr.append("%') cc,");
					sqlstr.append("(SELECT COUNT(*) as counts FROM ");
					sqlstr.append(username);
					sqlstr.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '");
					sqlstr.append(code);
					sqlstr.append("%') AND (NOT EXISTS (SELECT * FROM ");
					sqlstr.append(username);
					sqlstr.append("organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
					sqlstr.append(code);
					sqlstr.append("%') dd");
					//System.out.println("d" + sqlstr.toString());
				}else
				{
					sqlstr.append("select aa.grade,cc.counts + dd.counts  leafagechildscount from ");
					sqlstr.append("(SELECT MAX(grade) +1 AS grade from ");
					sqlstr.append(username);
					sqlstr.append("organization) aa,");
					sqlstr.append("(SELECT COUNT(*) as counts FROM ");
					sqlstr.append(username);
					sqlstr.append("organization b WHERE b.parentid LIKE '");
					sqlstr.append(code);
					sqlstr.append("%' AND b.codeitemid = b.childid) cc,");
					sqlstr.append("(SELECT COUNT(*) as counts FROM ");
					sqlstr.append(username);
					sqlstr.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '");
					sqlstr.append(code);
					sqlstr.append("%') AND (NOT EXISTS (SELECT * FROM ");
					sqlstr.append(username);
					sqlstr.append("organization orge WHERE orge.codeItemId = org.childId))) dd");
				}
			}
			else
			{
				if(code!=null && code.length()>0)
				{
					sqlstr.append("select aa.grade -bb.grade  as grade,cc.counts + dd.counts as leafagechildscount from ");
					sqlstr.append("(SELECT MAX(grade) + 1 as grade from vorganization where codeitemid like '");
					sqlstr.append(code);
					sqlstr.append("%'");
					sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between vorganization.start_date and vorganization.end_date and (vorganization.view_chart<>1 or vorganization.view_chart is null)");
					sqlstr.append(") aa,"); 
					sqlstr.append("(SELECT grade FROM vorganization WHERE codeitemid = '");
					sqlstr.append(code);
					sqlstr.append("' and "+Sql_switcher.dateValue(backdate)+" between vorganization.start_date and vorganization.end_date and (vorganization.view_chart<>1 or vorganization.view_chart is null)");
					sqlstr.append(") bb,");
					sqlstr.append("(SELECT COUNT(*) as counts FROM vorganization b WHERE b.parentid LIKE '");
					sqlstr.append(code);
					sqlstr.append("%' AND b.codeitemid = b.childid and b.codeitemid like '");
					sqlstr.append(code);
					sqlstr.append("%'");
					sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between b.start_date and b.end_date and (b.view_chart<>1 or b.view_chart is null)");
					sqlstr.append(") cc,");
					sqlstr.append("(SELECT COUNT(*) as counts FROM vorganization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '");
					sqlstr.append(code);
					sqlstr.append("%') AND (NOT EXISTS (SELECT * FROM vorganization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
					sqlstr.append(code);
					sqlstr.append("%'");
					sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null)");
					sqlstr.append(") dd,");
				}else
				{
					sqlstr.append("select aa.grade,bb.counts + cc.counts as leafagechildscount from ");
					sqlstr.append("(SELECT MAX(grade) +1 AS grade from vorganization where codeitemid like '");
					sqlstr.append(code);
					sqlstr.append("%' and "+Sql_switcher.dateValue(backdate)+" between vorganization.start_date and vorganization.end_date and (vorganization.view_chart<>1 or vorganization.view_chart is null)) aa,(SELECT COUNT(*) as counts FROM vorganization b WHERE b.parentid LIKE '");
					sqlstr.append(code);
					sqlstr.append("%' AND b.codeitemid = b.childid and b.codeitemid like '");
					sqlstr.append(code);
					sqlstr.append("%' and "+Sql_switcher.dateValue(backdate)+" between b.start_date and b.end_date and (b.view_chart<>1 or b.view_chart is null)) bb");
					sqlstr.append(",(SELECT COUNT(*) as counts FROM vorganization org WHERE (codeitemid <> childid) and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null) AND (org.codeitemid LIKE '");
					sqlstr.append(code);
					sqlstr.append("%') AND (NOT EXISTS (SELECT * FROM vorganization orge WHERE orge.codeItemId = org.childId))) cc ");
				}
			}
		}else
		{
			if("true".equalsIgnoreCase(parameter.getIsshowpersonname()))
			{
				if(code!=null && code.length()>0)
				{
					sqlstr.append("select aa.grade-bb.grade as grade,cc.counts + dd.counts AS leafagechildscount from ");
					sqlstr.append("(SELECT MAX(grade) + 1 as grade from ");
					sqlstr.append(username);
					sqlstr.append("organization where codeitemid like '");
					sqlstr.append(code);
					sqlstr.append("%') aa,");
					sqlstr.append("(SELECT grade FROM ");
					sqlstr.append(username);
					sqlstr.append("organization WHERE codeitemid = '");
					sqlstr.append(code);
					sqlstr.append("') bb,");
					sqlstr.append("(SELECT COUNT(*) as counts FROM ");
					sqlstr.append(username);
					sqlstr.append("organization b WHERE b.parentid LIKE '");
					sqlstr.append(code);
					sqlstr.append("%' AND b.codeitemid = b.childid and b.parentid like '");
					sqlstr.append(code);
					sqlstr.append("%') cc,");
					sqlstr.append("(SELECT COUNT(*) as counts FROM ");
					sqlstr.append(username);
					sqlstr.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '");
					sqlstr.append(code);
					sqlstr.append("%') AND (NOT EXISTS (SELECT * FROM ");
					sqlstr.append(username);
					sqlstr.append("organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
					sqlstr.append(code);
					sqlstr.append("%') dd");
					//System.out.println("d" + sqlstr.toString());
				}else
				{
					sqlstr.append("select aa.grade,cc.counts + dd.counts  leafagechildscount from ");
					sqlstr.append("(SELECT MAX(grade) +1 AS grade from ");
					sqlstr.append(username);
					sqlstr.append("organization) aa,");
					sqlstr.append("(SELECT COUNT(*) as counts FROM ");
					sqlstr.append(username);
					sqlstr.append("organization b WHERE b.parentid LIKE '");
					sqlstr.append(code);
					sqlstr.append("%' AND b.codeitemid = b.childid) cc,");
					sqlstr.append("(SELECT COUNT(*) as counts FROM ");
					sqlstr.append(username);
					sqlstr.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '");
					sqlstr.append(code);
					sqlstr.append("%') AND (NOT EXISTS (SELECT * FROM ");
					sqlstr.append(username);
					sqlstr.append("organization orge WHERE orge.codeItemId = org.childId))) dd");
				}
			}
			else
			{
				if(code!=null && code.length()>0)
				{
					sqlstr.append("select aa.grade -bb.grade  as grade,cc.counts + dd.counts as leafagechildscount from ");
					sqlstr.append("(SELECT MAX(grade) + 1 as grade from organization where codeitemid like '");
					sqlstr.append(code+"%'");
					if(!bShowPos) {
                        sqlstr.append(" and codesetid<>'@K'");
                    }
					if(!bShowDept) {
                        sqlstr.append(" and codesetid<>'UM'");
                    }
					sqlstr.append(") aa,"); 
					sqlstr.append("(SELECT grade FROM organization WHERE codeitemid = '");
					sqlstr.append(code+"'");
					if(!bShowPos) {
                        sqlstr.append(" and codesetid<>'@K'");
                    }
					if(!bShowDept) {
                        sqlstr.append(" and codesetid<>'UM'");
                    }
					sqlstr.append(") bb,");
					sqlstr.append("(SELECT COUNT(*) as counts FROM organization b WHERE b.parentid LIKE '");
					sqlstr.append(code+"%'");
					if(!bShowPos) {
                        sqlstr.append(" and codesetid<>'@K'");
                    }
					if(!bShowDept) {
                        sqlstr.append(" and codesetid<>'UM'");
                    }
					sqlstr.append(" AND b.codeitemid = b.childid and b.codeitemid like '");
					sqlstr.append(code);
					sqlstr.append("%') cc,");
					sqlstr.append("(SELECT COUNT(*) as counts FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '");
					sqlstr.append(code+"%')");
					if(!bShowPos) {
                        sqlstr.append(" and codesetid<>'@K'");
                    }
					if(!bShowDept) {
                        sqlstr.append(" and codesetid<>'UM'");
                    }
					sqlstr.append(" AND (NOT EXISTS (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
					sqlstr.append(code);
					sqlstr.append("%') dd");
				}else
				{
					sqlstr.append("select aa.grade,bb.counts + cc.counts as leafagechildscount from ");
					sqlstr.append("(SELECT MAX(grade) +1 AS grade from organization where codeitemid like '");
					sqlstr.append(code+"%'");					
					if(!bShowPos) {
                        sqlstr.append(" and codesetid<>'@K'");
                    }
					if(!bShowDept) {
                        sqlstr.append(" and codesetid<>'UM'");
                    }
					sqlstr.append(") aa,(SELECT COUNT(*) as counts FROM organization b WHERE b.parentid LIKE '");
					sqlstr.append(code+"%'");
					if(!bShowPos) {
                        sqlstr.append(" and codesetid<>'@K'");
                    }
					if(!bShowDept) {
                        sqlstr.append(" and codesetid<>'UM'");
                    }
					sqlstr.append(" AND b.codeitemid = b.childid and b.codeitemid like '");
					sqlstr.append(code);
					sqlstr.append("%') bb");
					sqlstr.append(",(SELECT COUNT(*) as counts FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '");
					sqlstr.append(code+"%')");
					if(!bShowPos) {
                        sqlstr.append(" and codesetid<>'@K'");
                    }
					if(!bShowDept) {
                        sqlstr.append(" and codesetid<>'UM'");
                    }
					sqlstr.append(" AND (NOT EXISTS (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId))) cc ");
				}
			}
		}
		
		List rs=ExecuteSQL.executeMyQuery(sqlstr.toString(),conn);
		int[] pagesize=new int[2];
		if(!rs.isEmpty()&&rs.size()>0)
		{
			if("true".equalsIgnoreCase(parameter.getGraphaspect()))
			{
				LazyDynaBean orgbean=(LazyDynaBean)rs.get(0);
				//orgbean.get("grade").toString 有可能是7.00 Integer.parseInt转会报错
				int id=(int)(Float.parseFloat(orgbean.get("grade").toString()));
				Integer shu = new Integer(id);
//				pagesize[0]=(1+Integer.parseInt(orgbean.get("grade").toString()))*(Integer.parseInt(parameter.getCellwidth()) +Integer.parseInt(parameter.getCellheight())/6 + Integer.parseInt(parameter.getCellhspacewidth())*2) + parameter.getPagespacewidth()*2;
				pagesize[0]=(1+shu.intValue())*(Integer.parseInt(parameter.getCellwidth()) +Integer.parseInt(parameter.getCellheight())/6 + Integer.parseInt(parameter.getCellhspacewidth())*2) + parameter.getPagespacewidth()*2;
				pagesize[1]=(1+Integer.parseInt(orgbean.get("leafagechildscount").toString())) * (Integer.parseInt(parameter.getCellheight()) + Integer.parseInt(parameter.getCellvspacewidth())) + parameter.getPagespaceheight() *2;
			}else
			{
				LazyDynaBean orgbean=(LazyDynaBean)rs.get(0);
				int id=(int)(Float.parseFloat(orgbean.get("grade").toString()));
				Integer shu = new Integer(id);
				pagesize[0]=(1+Integer.parseInt(orgbean.get("leafagechildscount").toString())) * (Integer.parseInt(parameter.getCellwidth()) + Integer.parseInt(parameter.getCellhspacewidth())) + parameter.getPagespacewidth() *2;
//				pagesize[1]=(1+Integer.parseInt(orgbean.get("grade").toString()))*(Integer.parseInt(parameter.getCellheight())*7/6 + Integer.parseInt(parameter.getCellvspacewidth())*2) + parameter.getPagespaceheight()*2;
				pagesize[1]=(1+shu.intValue())*(Integer.parseInt(parameter.getCellheight())*7/6 + Integer.parseInt(parameter.getCellvspacewidth())*2) + parameter.getPagespaceheight()*2;
			}
		    if(pagesize[0]>PAGESIZELIMIT)
		    {
		    	//pagesize[0]=PAGESIZELIMIT;
		    	//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("label.org.adderrors"),"",""));
		    }
			if(pagesize[1]>PAGESIZELIMIT)
			{
				//pagesize[1]=PAGESIZELIMIT;
				//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("label.org.adderrors"),"",""));
			}
			 if(pagesize[0]<180)
			 {
				 pagesize[0]=180;
			 }
			 if(pagesize[1]<180)
			{
				 pagesize[1]=180;
				//pagesize[1]=PAGESIZELIMIT;
					//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("label.org.adderrors"),"",""));
			}
		}
		return pagesize;
	}
	   public String ConverDBsql(String dbname)
		{
			String resultsql="";
			switch (Sql_switcher.searchDbServer()) {
			case Constant.MSSQL: {
				resultsql=" + '(' + Convert(Varchar,count(" + dbname + "A01.a0100)) + '人)'" ;
				break;
			}
			case Constant.DB2: {
				resultsql=" + '(' + To_Char(count(" + dbname + "A01.a0100)) + '人)'" ;
				break;
			}
			case Constant.ORACEL: {
				resultsql=" || '(' || count(" + dbname + "A01.a0100) || '人)'" ;
				break;
			}
			}
			return resultsql;
		}
	   /*
		 * 格式字体样式
		 */
		public HSSFCellStyle setDateStyle(HSSFWorkbook workbook) 
		{
			 // 先定义一个字体对象
			
//	        HSSFFont font = workbook.createFont();
//	        font.setFontName("黑体");
//	        font.setFontHeightInPoints((short) 10); // 字体大小
//	        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体字
	        // 定义表头单元格格式
	        HSSFCellStyle style = workbook.createCellStyle();//创建单元各风格
	        style.setAlignment(HorizontalAlignment.CENTER); // 居中对齐方式 左右
	        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式 上下
	        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style.setFillForegroundColor(HSSFColor.WHITE.index);//颜色黄色
			style.setWrapText(true);  //换行
	        style.setFont(fontBlack10); // 单元格字体
	        style.setBorderBottom(BorderStyle.THIN); //下边
	        style.setBorderLeft(BorderStyle.THIN); //左边
	        style.setBorderRight(BorderStyle.THIN); //右边
	        style.setBorderTop(BorderStyle.THIN); //上边
	        return style;

		}
		//合并单元格边框
		public HSSFCellStyle setBorder(HSSFWorkbook workbook){
			HSSFCellStyle style = workbook.createCellStyle();//创建单元各风格
			style.setFillForegroundColor(HSSFColor.YELLOW.index);//颜色黄色
			style.setBorderBottom(BorderStyle.THIN); //下边
	        style.setBorderLeft(BorderStyle.THIN); //左边
	        style.setBorderRight(BorderStyle.THIN); //右边
	        style.setBorderTop(BorderStyle.THIN); //上边
	       return style;
		}
		public HSSFCellStyle setnull(HSSFWorkbook workbook){
			 // 先定义一个字体对象
//	        HSSFFont font = workbook.createFont();
//	        font.setFontName("黑体");
//	        font.setFontHeightInPoints((short) 12); // 字体大小
//	        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体字
	        HSSFCellStyle style = workbook.createCellStyle();//创建单元各风格
	        style.setAlignment(HorizontalAlignment.CENTER); // 居中对齐方式 左右
	        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式 上下
	        
	        style.setWrapText(true);  //换行
	        style.setFont(fontBlackBlod12); // 单元格字体
			return style;
		}
		public HSSFCellStyle setDateStylesy(HSSFWorkbook workbook,ParameterBo parameter) 
		{
			 // 先定义一个字体对象
//	        HSSFFont font = workbook.createFont();
	        String colerdd=parameter.getCellcolor(); //代码值
	        int integer1=Integer.parseInt(colerdd.substring(1,3),16);
	        int integer2=Integer.parseInt(colerdd.substring(3,5),16);
	        int integer3=Integer.parseInt(colerdd.substring(5,7),16);
//	        font.setFontName("黑体");
//	        font.setFontHeightInPoints((short) 10); // 字体大小
//	        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体字
	        // 定义表头单元格格式
	        HSSFCellStyle style = workbook.createCellStyle();//创建单元各风格
	        style.setAlignment(HorizontalAlignment.CENTER); // 居中对齐方式 左右
	        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式 上下
	        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	        HSSFPalette palette = workbook.getCustomPalette(); 
	        palette.setColorAtIndex((short)9, (byte) (integer1), (byte) (integer2), (byte) (integer3));
	        style.setFillForegroundColor((short)9);
			
	        style.setWrapText(true);  //换行
	        style.setFont(fontBlack10); // 单元格字体
	        style.setBorderBottom(BorderStyle.THIN); //下边
	        style.setBorderLeft(BorderStyle.THIN); //左边
	        style.setBorderRight(BorderStyle.THIN); //右边
	        style.setBorderTop(BorderStyle.THIN); //上边
	        return style;
		}
}
