package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.common.StationPosView;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddressList;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExportTrainees extends IBusiness {
	private int index = 0;
	private HSSFSheet codesetSheet=null;

	public void execute() throws GeneralException {
		String outName = "";
		try {
			LazyDynaBean bean=(LazyDynaBean)this.getFormHM().get("obj");
			String items = (String)bean.get("selectitems");
			String codeid = (String)bean.get("codeid");
			String ims[] =(items).split("`");
			ArrayList ls = new ArrayList();
			ArrayList code = new ArrayList();
			if (codeid != null && !"".equalsIgnoreCase(codeid)) {
				FieldItem fi = DataDictionary.getFieldItem(codeid, "a01");
				if (fi != null && !"".equals(fi))
					ls.add(fi);
			}
			if (codeid == null || codeid.length() < 1) {
				FieldItem fi = DataDictionary.getFieldItem("r4002", "r40");
				ls.add(fi);
			}
				
			for (int i = 0; i < ims.length; i++) {
				if("r4002".equalsIgnoreCase(ims[i]) && (codeid == null || codeid.length() < 1))
					continue;
				if(codeid != null && codeid.length() > 0 && codeid.equals(ims[i]))
					continue;
				if("a0101".equalsIgnoreCase(codeid) && "r4002".equalsIgnoreCase(ims[i]))
					continue;
				FieldItem fieldItem = DataDictionary.getFieldItem(ims[i], "r40");
				if(fieldItem == null || "".equals(fieldItem))
					continue;
				ls.add(fieldItem);
				
				if(!"0".equals(fieldItem.getCodesetid()) && !"1".equals(fieldItem.getCodesetid())
						&& !"UM".equals(fieldItem.getCodesetid()) && !"UN".equals(fieldItem.getCodesetid())){
					String c  = fieldItem.getItemid() + "|" + fieldItem.getCodesetid();
					code.add(c);
				}
			}

			outName = this.creatExcel(ls,code);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			
			this.getFormHM().put("outName", PubFunc.encrypt(outName));
			this.getFormHM().put("student", SafeCode.encode("student"));
		}
	}

	private String creatExcel(ArrayList list,ArrayList code) throws Exception {
		ArrayList ls = null;
		List lss = new ArrayList();
		String r3101 = (String) this.getFormHM().get("r3101");
		
		TrainClassBo cbo = new TrainClassBo(this.frameconn);
		if(!cbo.checkClassPiv(r3101, this.userView)){
		    throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("train.info.chang.nopiv")));
		}


		try(
			HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
		) {
			HSSFSheet sheet = wb.createSheet();
			// sheet.setProtect(true);

			HSSFFont font1 = wb.createFont(); //设置样式
			font1.setFontHeightInPoints((short) 20);
			font1.setBold(true);
			font1.setColor(HSSFFont.COLOR_NORMAL);
			HSSFCellStyle style2 = wb.createCellStyle();
			style2.setFont(font1);
			style2.setAlignment(HorizontalAlignment.CENTER);
			style2.setVerticalAlignment(VerticalAlignment.CENTER);
			style2.setWrapText(true);
			style2.setBorderLeft(BorderStyle.valueOf((short) 1)); //设置左边框
			style2.setBorderRight(BorderStyle.valueOf((short) 1)); //设置有边框
			style2.setBorderTop(BorderStyle.valueOf((short) 1)); //设置下边框
			style2.setBorderBottom(BorderStyle.valueOf((short) 1));
			// style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			// style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

			HSSFFont font2 = wb.createFont();
			font2.setFontHeightInPoints((short) 10);
			HSSFCellStyle style1 = wb.createCellStyle();
			style1.setFont(font2);
			style1.setAlignment(HorizontalAlignment.CENTER);
			style1.setVerticalAlignment(VerticalAlignment.CENTER);
			style1.setWrapText(true);
			style1.setBorderLeft(BorderStyle.valueOf((short) 1)); //设置左边框
			style1.setBorderRight(BorderStyle.valueOf((short) 1)); //设置有边框
			style1.setBorderTop(BorderStyle.valueOf((short) 1)); //设置下边框
			style1.setBorderBottom(BorderStyle.valueOf((short) 1));
			//style1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			//style1.setFillBackgroundColor(HSSFColor.LIGHT_GREEN.index);
			style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式

			HSSFPatriarch patr = sheet.createDrawingPatriarch();

			HSSFRow row = sheet.getRow(0);
			if (row == null) {
				row = sheet.createRow(0);
			}
			row.setHeight((short) 1000);
			HSSFCell cell = null;
			HSSFComment comm = null;

			sheet.setColumnWidth((1), 15 * 500); //设置列宽
			sheet.setColumnWidth((3), 15 * 500); //设置列宽
			// 设置第一行的数据
			StringBuffer s = new StringBuffer();
			StringBuffer items = new StringBuffer();
			ArrayList codeCols = new ArrayList();
			for (int i = 0; i < list.size(); i++) {
				FieldItem field = (FieldItem) list.get(i);
				s.append(field.getItemid() + ",");
				if (i == 0 && "A01".equalsIgnoreCase(field.getFieldsetid()))
					items.append("A01." + field.getItemid() + ",");
				else
					items.append("r40." + field.getItemid() + ",");
				String fieldName = field.getItemdesc();

				cell = row.getCell(i);
				if (cell == null) {
					cell = row.createCell(i);
				}
				cell.setCellStyle(style1);
				cell.setCellValue(fieldName);
				comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (i + 1), 0, (short) (i + 2), 1));
				String con = "";
				if (i == 0 && ("A01".equalsIgnoreCase(field.getFieldsetid()) || "r4002".equalsIgnoreCase(field.getItemid())))
					con = field.getItemid() + "`A01";
				else if (i > 0)
					con = field.getItemid();
				comm.setString(new HSSFRichTextString(con));// 制定ID
				cell.setCellComment(comm);
				if ("A".equalsIgnoreCase(field.getItemtype()) && (field.getCodesetid() != null
						&& !"".equals(field.getCodesetid()) && !"0".equals(field.getCodesetid()))) {
					if ("r4117".equalsIgnoreCase(field.getItemid()) || "r4118".equalsIgnoreCase(field.getItemid()))
						continue;
					else if (field.getCodesetid().indexOf("1_") != -1)
						continue;
					else
						codeCols.add(field.getCodesetid() + ":" + new Integer(i).toString());
				}
			}
			RowSet r = null;
			try {
				String c = s.toString().substring(0, s.length() - 1);
				ContentDAO dao = new ContentDAO(this.frameconn);
				StringBuffer sql = new StringBuffer();

				ConstantXml constantbo = new ConstantXml(this.frameconn, "TR_PARAM");
				String tmpnbase = constantbo.getTextValue("/param/post_traincourse/nbase");
				if (tmpnbase == null || tmpnbase.length() < 1)
					throw GeneralExceptionHandler.Handle(new Exception("未设置人员库！<br><br>请到   培训管理>参数设置>其它参数>岗位培训指标设置   中设置人员库。"));
				String[] tmpnbases = tmpnbase.split(",");

				for (int i = 0; i < tmpnbases.length; i++) {
					String item = items.toString().substring(0, items.length() - 1).replace("A01", tmpnbases[i] + "A01");
					sql.append(" select " + item + " from r40");
					sql.append("," + tmpnbases[i] + "a01 where r40.r4001=" + tmpnbases[i] + "a01.a0100");
					sql.append(" and r40.r4005='" + r3101 + "' and r40.r4013 = '03' and r40.nbase='" + tmpnbases[i] + "'");
					sql.append(" union all");
				}
				String sqlstr = "select * from (" + sql.toString().substring(0, sql.lastIndexOf("union all")) + ") student";
				sqlstr += " order by b0110,e0122";
				r = dao.search(sqlstr);

				while (r.next()) {
					ls = new ArrayList();
					String[] cs = c.split(",");
					for (int k = 0; k < cs.length; k++) {

						FieldItem item = DataDictionary.getFieldItem(cs[k]);
						String itemtype = item.getItemtype();

						//判断是否是单位字段
						if ("b0110".equalsIgnoreCase(cs[k])) {
							String unit = this.getDescById(r.getString("b0110"));
							ls.add(unit);
							//判断是否是 部门 字段
						} else if ("e0122".equalsIgnoreCase(cs[k])) {
							String department = this.getDetailById(r.getString("e0122"), r.getString("b0110"));
							ls.add(department);
						} else if ("e01a1".equalsIgnoreCase(cs[k])) {
							String department = this.getStationById(r.getString("e01a1"), r.getString("e0122"));
							ls.add(department);
						}
						//判断是否是日期类型
						else if ("D".equalsIgnoreCase(itemtype)) {
							SimpleDateFormat theDateFormat = new SimpleDateFormat("yyyy/MM/dd");
							if (null != r.getObject(cs[k])) {
								ls.add(theDateFormat.format(r.getDate(cs[k])));
							} else {
								ls.add(null);
							}
						} else if ("N".equalsIgnoreCase(itemtype)) {//判断是否是数值类型
							int d = item.getDecimalwidth();
							String valuestr = r.getString(cs[k]);
							if (null != r.getObject(cs[k])) {
								valuestr = String.valueOf(new BigDecimal(valuestr).setScale(d, BigDecimal.ROUND_HALF_UP));
								ls.add(valuestr);
							} else {
								ls.add(null);
							}
						}
						//剩下就是字符类型的 字段了或代码类型了
						else {
							if ("".equals(r.getString(cs[k]))) {
								ls.add(null);
							} else if (item.getCodesetid() == null || "0".equalsIgnoreCase(item.getCodesetid())) {
								ls.add(r.getString(cs[k]));
							} else {
								String[] demos = new String[code.size()];
								ArrayList as = new ArrayList();
								if (code.size() < 1)
									continue;
								for (int j = 0; j < code.size(); j++) {
									String demo = code.get(j).toString();
									demos = demo.split("[|]");
									as.add(demos[0]);
									if (!cs[k].equalsIgnoreCase(demos[0]))
										continue;
									String desc = this.getCodeItemDescById(r.getString(demos[0]), demos[1]);
									if ("".equals(desc)) {
										desc = null;
										ls.add(desc);
									} else
										ls.add(desc);
								}
							}
						}
					}
					lss.add(ls);
				}

				int rowCount = 1;
				ArrayList styleList = new ArrayList();
				while (rowCount < 1001) {


					row = sheet.getRow(rowCount);
					if (row == null) {
						row = sheet.createRow(rowCount);
					}
					if (rowCount < lss.size() + 1) {
						List l = (List) lss.get(rowCount - 1);
						for (int i = 0; i < list.size(); i++) {
							sheet.setColumnWidth((i), 3500);
							sheet.setColumnWidth((3), 15 * 500);
							HSSFCellStyle style = null;
							FieldItem field = (FieldItem) list.get(i);
							String itemtype = field.getItemtype();
							int decwidth = field.getDecimalwidth();

							if (rowCount == 1) {
								if ("N".equals(itemtype))
									style = styleN(decwidth, wb);
								styleList.add(style);
							}

							cell = row.getCell(i);
							if (cell == null)
								cell = row.createCell(i);
							if ("N".equals(itemtype)) {
								cell.setCellStyle((HSSFCellStyle) styleList.get(i));
								cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							} else {
								cell.setCellStyle(style1);
								cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							}

							if (i < l.size()) {

								if (null != l.get(i) && !"".equals(l.get(i).toString().trim())) {
									cell.setCellValue(l.get(i).toString());
								}
							}
						}
					} else {
						cell = row.getCell(0);
						if (cell == null)
							cell = row.createCell(0);
						cell.setCellStyle(style1);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);

						for (int i = 0; i < list.size(); i++) {
							FieldItem field = (FieldItem) list.get(i);
							String itemtype = field.getItemtype();
							int decwidth = field.getDecimalwidth();

							cell = row.getCell(i);
							if (cell == null)
								cell = row.createCell(i);
							if ("N".equals(itemtype)) {
								cell.setCellStyle(styleN(decwidth, wb));
								cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
								cell.setCellValue("");
							} else {
								cell.setCellStyle(style1);
								cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							}
						}
					}
					rowCount++;
				}
				rowCount--;

				String codesetSheetName = "hjehr_codeset_" + index / 255;
				if (codeCols.size() > 0) {
					if (index % 255 == 0) {// 当sheet列数满255时，重新生成一个sheet
						codesetSheet = wb.createSheet(codesetSheetName);
					}
					wb.setSheetHidden(wb.getSheetIndex(codesetSheet), true);
					codesetSheet.setColumnWidth((index), 0);
				}
				String[] lettersUpper = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
						"U", "V", "W", "X", "Y", "Z"};
				for (int n = 0; n < codeCols.size(); n++) {
					int m = 0;
					String codeCol = (String) codeCols.get(n);
					String[] temp = codeCol.split(":");
					String codesetid = temp[0];
					int codeCol1 = Integer.valueOf(temp[1]).intValue();
					StringBuffer codeBuf = new StringBuffer();
					if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid)) {
						codeBuf.append("select count(*) from codeitem where codesetid='" + codesetid + "'");

						this.frowset = dao.search(codeBuf.toString());
						if (this.frowset.next()) {
							if (this.frowset.getInt(1) < 200) {
								codeBuf.setLength(0);
								codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid
										+ "' order by codeitemid");
							} else {
								continue;
							}
						}
					} else {// 生成下拉列表
						if (!"UN".equals(codesetid)) {
							m = loadorg(codesetSheet, row, cell, index, m, dao, codesetid);
						} else if ("UN".equals(codesetid)) {
							codeBuf.setLength(0);
							if (this.userView.isSuper_admin()) {
								codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid
										+ "' and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd"))
										+ " between start_date and end_date order by a0000,codeitemid");
							} else {
								TrainCourseBo bo = new TrainCourseBo(this.userView);
								String manpriv = bo.getUnitIdByBusi();
								String codepiv = "";
								StringBuffer tmpstr = new StringBuffer();
								StringBuffer e0122s = new StringBuffer();
								if (manpriv != null && manpriv.trim().length() > 2 && manpriv.indexOf("UN`") == -1) {
									String[] tmp = manpriv.split("`");
									for (int i = 0; i < tmp.length; i++) {
										codepiv = tmp[i];
										if ("UN".equalsIgnoreCase(codepiv.substring(0, 2))) {
											if (i > 0)
												tmpstr.append(" or ");
											tmpstr.append("codeitemid like '" + codepiv.substring(2, codepiv.length()) + "%'");
										} else {
											e0122s.append("'" + codepiv.substring(2, codepiv.length()) + "',");
										}

										if (tmpstr == null || tmpstr.length() < 1)
											tmpstr.append("codeitemid in(" + getB0110(e0122s.subSequence(0, e0122s.length() - 1).toString()) + ")");

									}
									codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='"
											+ codesetid + "' and (" + tmpstr + ") and "
											+ Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd"))
											+ " between start_date and end_date order by grade,a0000,codeitemid");
								} else if (manpriv.indexOf("UN`") != -1)
									codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='"
											+ codesetid + "' and "
											+ Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd"))
											+ " between start_date and end_date order by grade,a0000,codeitemid");
								else
									codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where 1=2");
							}
						}
					}

					if (!"UM".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid)) {

						this.frowset = dao.search(codeBuf.toString());
						while (this.frowset.next()) {
							row = codesetSheet.getRow(m + 0);
							if (row == null)
								row = codesetSheet.createRow(m + 0);

							cell = row.createCell((index));

							if ("UN".equals(codesetid)) {
								int grade = this.frowset.getInt("grade");
								StringBuffer sb = new StringBuffer();
								sb.setLength(0);
								for (int i = 1; i < grade; i++) {
									sb.append("  ");
								}
								cell.setCellValue(new HSSFRichTextString(sb.toString()
										+ this.frowset.getString("codeitemdesc") + "(" + this.frowset.getString("codeitemid") + ")"));
							} else {
								cell.setCellValue(new HSSFRichTextString(this.frowset.getString("codeitemdesc")));
							}
							m++;
						}
					}
					if (m == 0) {
						continue;
					}
					// 放到单独页签
					String strFormula = "";
					if (index <= 25) {
						strFormula = codesetSheetName + "!$" + lettersUpper[index] + "$1:$" + lettersUpper[index] + "$" + Integer.toString(m); // 表示BA列1到m行作为下拉列表来源数据
					} else if (index > 25) {
						strFormula = codesetSheetName + "!$" + lettersUpper[index / 26 - 1] + lettersUpper[index % 26] + "$1:$" + lettersUpper[index / 26 - 1] + lettersUpper[index % 26] + "$" + Integer.toString(m); // 表示BA列1到m行作为下拉列表来源数据
					}
					CellRangeAddressList addressList = new CellRangeAddressList(1, rowCount, codeCol1, codeCol1);// rowCount
					DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
					HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
					dataValidation.setSuppressDropDownArrow(false);
					sheet.addValidationData(dataValidation);
					index++;
				}

			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				try {
					if (r != null)
						r.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			String outName = this.userView.getUserName() + "_train.xls";

			try {
				FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
				wb.write(fileOut);
				fileOut.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			sheet = null;
			return outName;
		}

	}

	public HSSFRichTextString cellStr(String context) {
		HSSFRichTextString textstr = new HSSFRichTextString(context);
		return textstr;
	}

	public String decimalwidth(int len) {

		StringBuffer decimal = new StringBuffer("0");
		if (len > 0)
			decimal.append(".");
		for (int i = 0; i < len; i++) {
			decimal.append("0");
		}
		decimal.append("_ ");
		return decimal.toString();
	}

	public HSSFCellStyle dataStyle(HSSFWorkbook workbook) {
		HSSFCellStyle style = workbook.createCellStyle();
		// style.setVerticalAlignment(VerticalAlignment.CENTER);
		return style;
	}

	/**
	 * 通过培训班ID找到培训班名称
	 * @param id
	 * @return
	 */
	public String getNameById(String id) {
		String name = "";
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			String sql = "select r3130 from r31 where r3101 = '" + id + "'";
			rs = dao.search(sql);
			if (rs.next()) {
				name = rs.getString("r3130");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return name;
	}

	/**
	 * 得到人员库名称
	 * @param pre
	 * @return
	 */
	public String getDbNameByPre(String pre) {
		String name = "";
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			String sql = "select dbName from dbname where pre = '" + pre + "'";
			rs = dao.search(sql);
			if (rs.next()) {
				name = rs.getString("dbname");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return name;
	}

	/**
	 * 通过单位ID查找到单位
	 * @param id
	 * @return
	 */
	public String getDescById(String id) {
		String desc = "";
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			String sql = "select codeitemdesc from organization where codeitemid = '" + id + "' and codesetid = 'UN'";
			rs = dao.search(sql);
			if (rs.next()) {
				desc = rs.getString("codeitemdesc");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(id == null || "".equalsIgnoreCase(id))
			return "";
		return desc + "(" + id + ")";
	}

	/**
	 * 通过单位下的部门ID 找到部门名称
	 * @param id
	 * @param parentId
	 * @return
	 */
	public String getDetailById(String id, String parentId) {
		String desc = "";
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			String sql = " select codeitemdesc from organization where codeitemid = '"
					+ id + "' and parentid like '" + parentId + "%' and codesetid = 'UM'";
			rs = dao.search(sql);
			if (rs.next()) {
				desc = rs.getString("codeitemdesc");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ((id == null || "".equalsIgnoreCase(id)) || (parentId == null || "".equalsIgnoreCase(parentId)))
			return "";
		return desc + "(" + id + ")";
	}
	/**
	 * 通过单位下的岗位ID 找到岗位名称
	 * @param id
	 * @param parentId
	 * @return
	 */
	public String getStationById(String id, String parentId) {
		String desc = "";
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			String sql = " select codeitemdesc from organization where codeitemid = '" + id + "' and parentid = '" + parentId + "' and codesetid = '@K'";
			rs = dao.search(sql);
			if (rs.next()) {
				desc = rs.getString("codeitemdesc");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ((id == null || "".equalsIgnoreCase(id)) || (parentId == null || "".equalsIgnoreCase(parentId)))
			return "";
		return desc + "(" + id + ")";
	}
	/**
	 * 按id找到代码类的名称
	 * @param id
	 * @param codesetId
	 * @return
	 */
	public String getCodeItemDescById(String id , String codesetId){
		String desc = "";
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			String sql = "select codeitemdesc from codeitem where codesetid = '"+codesetId+"' and codeitemid = '"+id+"'";
		//	System.out.println(sql);
			rs = dao.search(sql);
			if(rs.next()){
				desc = rs.getString("codeitemdesc");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (rs != null)
					rs.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return desc;
	}
	/**
	 * 数值类型指标的单元格格式
	 * @param i
	 * @param wb
	 * @return
	 */
	public HSSFCellStyle styleN(int i, HSSFWorkbook wb) {
		HSSFCellStyle styleN = dataStyle(wb);
		styleN.setAlignment(HorizontalAlignment.CENTER);
		styleN.setVerticalAlignment(VerticalAlignment.CENTER); 
		styleN.setWrapText(true);
		styleN.setBorderBottom(BorderStyle.THIN);
		styleN.setBorderLeft(BorderStyle.THIN);
		styleN.setBorderRight(BorderStyle.THIN);
		styleN.setBorderTop(BorderStyle.THIN);
		styleN.setBottomBorderColor((short) 8);
		styleN.setLeftBorderColor((short) 8);
		styleN.setRightBorderColor((short) 8);
		styleN.setTopBorderColor((short) 8);
		HSSFDataFormat df5 = wb.createDataFormat();
		styleN.setDataFormat(df5.getFormat(decimalwidth(i)));
		return styleN;
	}
	/**
	 * 生成下拉列表
	 * @param sheet
	 * @param row
	 * @param cell
	 * @param index
	 * @param m
	 * @param dao
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private int loadorg(HSSFSheet sheet,HSSFRow row,HSSFCell cell,int index,int m,ContentDAO dao,String type) throws Exception {
		Statement st=null;
		ResultSet rs = null;
		try{
			String sql="";
			if(this.userView.isSuper_admin()){
				sql="select codesetid,codeitemid,childid,codeitemdesc,grade from organization where codesetid<>'@K' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid";
			}else{
				TrainCourseBo bo = new TrainCourseBo(this.userView);
				String manpriv = bo.getUnitIdByBusi();
				String code = "";
				StringBuffer tmpstr=new StringBuffer();
				if (manpriv != null && manpriv.trim().length() > 2 && manpriv.indexOf("UN`")==-1) {
					String[] tmp = manpriv.split("`");
					for(int i=0;i<tmp.length;i++){
						code = tmp[i];
						if(i>0)
							tmpstr.append(" or ");
						if ("UN".equalsIgnoreCase(code.substring(0, 2)) || "UM".equalsIgnoreCase(code.substring(0, 2)))
							tmpstr.append("codeitemid like '" + code.substring(2, code.length()) + "%'");
					}
					sql="select codesetid,codeitemid,childid,codeitemdesc,grade from organization where ("+tmpstr+") and codesetid<>'@K' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid";
				} else if(manpriv.indexOf("UN`")!=-1)
					sql="select codesetid,codeitemid,childid,codeitemdesc,grade from organization where codesetid<>'@K' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid";
				else
					sql="select codesetid,codeitemid,childid,codeitemdesc,grade from organization where 1=2";
			}
			rs=dao.search(sql);
			String codeitemid="";
			String codeitemdesc="";
			int grade=0;
			while(rs.next()){
				codeitemid=rs.getString("codeitemid");
				codeitemdesc=rs.getString("codeitemdesc");
				grade=rs.getInt("grade");
				row = sheet.getRow(m + 0);
				if (row == null)
					row = sheet.createRow(m + 0);
				// 放到单独页签
				cell = row.createCell((index));
				StringBuffer sb=new StringBuffer();
				sb.setLength(0);
				for(int i=1;i<grade;i++){
					sb.append("  ");
				}
				cell.setCellValue(new HSSFRichTextString(sb.toString()+codeitemdesc+"("+codeitemid+")"));
				m++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs!=null){
				rs.close();
			}
			if(st!=null){
				st.close();
			}
		}
		return m;
	}
	/**
     * 获取部门的所属单位
     * @param e0122s
     * @return
     */
    private String getB0110(String e0122s) {
        String b0110 = "";
        try {
            String[] e0122 = e0122s.split(",");
            for (int i = 0; i < e0122.length; i++) {

                if (e0122[i] == null || e0122[i].trim().length() < 1)
                    continue;
                
                List savePos = getStationPos(e0122[i]);
                for (int n = 0; n < savePos.size(); n++) {
                    StationPosView posview = (StationPosView) savePos.get(n);
                    if (!"b0110".equalsIgnoreCase(posview.getItem()))
                        continue;

                    b0110 += "'" + posview.getItemvalue() + "',";
                    break;
                }
            }

            if(b0110 !=null && b0110.length() > 0)
                b0110 = b0110.substring(0, b0110.length()-1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b0110;
    }
    /**
     * 获取部门的所属单位
     * @param code
     * @return
     */
    private ArrayList getStationPos(String code) {
        ArrayList poslist = new ArrayList();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        boolean isdep = false;
        boolean isorg = false;
        StringBuffer strsql = new StringBuffer();
        try {
            String pre = "UM";
            conn = this.getFrameconn();
            ContentDAO db = new ContentDAO(conn);
            while (!"UN".equalsIgnoreCase(pre)) {
                if (strsql != null && strsql.length() > 0)
                    strsql.delete(0, strsql.length());

                strsql.append("select * from organization");
                strsql.append(" where codeitemid=");
                strsql.append(code);
                rs = db.search(strsql.toString()); // 执行当前查询的sql语句
                if (rs.next()) {
                    StationPosView posview = new StationPosView();
                    pre = rs.getString("codesetid");
                    if ("UM".equalsIgnoreCase(pre)) {
                        if (isdep == false) {
                            posview.setItem("e0122");
                            posview.setItemvalue(rs.getString("codeitemid"));
                            posview.setItemviewvalue(rs.getString("codeitemdesc"));
                            isdep = true;
                            poslist.add(posview);
                        }
                    } else if ("UN".equalsIgnoreCase(pre)) {
                        if (isorg == false) {
                            posview.setItem("b0110");
                            posview.setItemvalue(rs.getString("codeitemid"));
                            posview.setItemviewvalue(rs.getString("codeitemdesc"));
                            isorg = true;
                            poslist.add(posview);
                        }
                    }

                    code = "'"+rs.getString("parentid")+"'";
                }
            }  
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
            PubFunc.closeResource(stmt);
        }

        return poslist;
    }
}
