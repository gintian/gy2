package com.hjsj.hrms.transaction.train.report.lessonAnalyse;

import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ExportFileTrans extends IBusiness {
	public void execute() throws GeneralException {
		String strSql = (String) this.getUserView().getHm().get("key_train_sql1");
		strSql = PubFunc.keyWord_reback(strSql);
		ArrayList titles = getTitles();
		String outName = createExcel(titles,strSql);
		//outName = outName.replaceAll(".xls", "#");
		outName = PubFunc.encrypt(outName);
		this.getFormHM().put("outName", outName);
	}

	// 生成Excel
	//select id,nbase,a0100,b0110,e0122,e01a1,a0101,r5003,start_date,end_date,learnedhour,learnednum,lprogress 
	//from tr_selected_lesson t left join R50 r on r.R5000=t.R5000 where 1=1 order by end_date desc,id
	private String createExcel(ArrayList titles, String strSql) {
		String outputFile ="tr_"+this.userView.getUserName() + ".xls";
		HSSFWorkbook wb = null;
		try {
			// 创建excel报表并写入数据
			wb = new HSSFWorkbook();
			
			// 定义两种格式HSSFCellStyle
			// 第一种style--字体20，水平居中
			HSSFFont font1 = wb.createFont();
			font1.setFontHeightInPoints((short) 18);
			HSSFCellStyle style1 = wb.createCellStyle();
			style1.setFont(font1);
			style1.setAlignment(HorizontalAlignment.CENTER);
			
			// 第二种style--字体10，水平居中，垂直居中，黑色边框，自动换行
			HSSFFont font2 = wb.createFont();
			font2.setFontHeightInPoints((short) 10);
			HSSFCellStyle style2 = wb.createCellStyle();
			style2.setFont(font2);
			style2.setAlignment(HorizontalAlignment.CENTER);
			style2.setVerticalAlignment(VerticalAlignment.CENTER);
			style2.setWrapText(true);
			style2.setBorderBottom(BorderStyle.THIN);
			style2.setBorderLeft(BorderStyle.THIN);
			style2.setBorderRight(BorderStyle.THIN);
			style2.setBorderTop(BorderStyle.THIN);
			style2.setBottomBorderColor((short) 8);
			style2.setLeftBorderColor((short) 8);
			style2.setRightBorderColor((short) 8);
			style2.setTopBorderColor((short) 8);
			
			HSSFCellStyle style3 = wb.createCellStyle();
			style3.setFont(font2);
			style3.setAlignment(HorizontalAlignment.LEFT);
			style3.setVerticalAlignment(VerticalAlignment.CENTER);
			style3.setWrapText(true);
			style3.setBorderBottom(BorderStyle.THIN);
			style3.setBorderLeft(BorderStyle.THIN);
			style3.setBorderRight(BorderStyle.THIN);
			style3.setBorderTop(BorderStyle.THIN);
			style3.setBottomBorderColor((short) 8);
			style3.setLeftBorderColor((short) 8);
			style3.setRightBorderColor((short) 8);
			style3.setTopBorderColor((short) 8);
			
			HSSFCellStyle style4 = wb.createCellStyle();
			style4.setFont(font2);
			style4.setAlignment(HorizontalAlignment.RIGHT);
			style4.setVerticalAlignment(VerticalAlignment.CENTER);
			style4.setWrapText(true);
			style4.setBorderBottom(BorderStyle.THIN);
			style4.setBorderLeft(BorderStyle.THIN);
			style4.setBorderRight(BorderStyle.THIN);
			style4.setBorderTop(BorderStyle.THIN);
			style4.setBottomBorderColor((short) 8);
			style4.setLeftBorderColor((short) 8);
			style4.setRightBorderColor((short) 8);
			style4.setTopBorderColor((short) 8);
			
			String title = "学习情况分析报表";
			HSSFSheet sheet = wb.createSheet();
			wb.setSheetName(0, title);
			
			int len = titles.size();
			
			// 写表头
			HSSFRow row = sheet.createRow(0);
			ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) len);
			HSSFCell cell = row.createCell((short) 0);
			// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellValue(new HSSFRichTextString(title));
			cell.setCellStyle(style1);
			
			// 写列头
			HSSFRow row2 = sheet.createRow(1);
			int i = 0;
			HSSFCell cell2 = row2.createCell((short) 0);
			// cell2.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell2.setCellValue(new HSSFRichTextString("序号"));
			cell2.setCellStyle(style2);
			HashMap dataTypeMap = new HashMap();
			sheet.setColumnWidth((short) 0, (short) 1600);
			for (i = 0; i < titles.size(); i++) {
				cell2 = row2.createCell((short) (i + 1));
				// cell2.setEncoding(HSSFCell.ENCODING_UTF_16);
				LazyDynaBean abean = (LazyDynaBean) titles.get(i);
				String datatype = (String) abean.get("datatype");
				dataTypeMap.put(new Integer(i + 1), datatype);
				cell2.setCellValue(new HSSFRichTextString((String) abean
						.get("title")));
				cell2.setCellStyle(style2);
			}
			
			HSSFRow dataRow = null;
			HSSFCell dataCell = null;
			// 数据
			int xuhao = 0;
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				RowSet rs = dao.search(strSql);
				
				while (rs.next()) {
					dataRow = sheet.createRow(xuhao + 2);
					
					dataCell = dataRow.createCell((short) 0);
					// dataCell.setEncoding(HSSFCell.ENCODING_UTF_16);
					dataCell.setCellValue(++xuhao);
					dataCell.setCellStyle(style2);
					
					for (int j = 1; j <= len; j++) {
						
						dataCell = dataRow.createCell((short) (j));
						// dataCell.setEncoding(HSSFCell.ENCODING_UTF_16);
						String value = rs.getString(j+3);
						if (j == 1) {
							value = value != null ? AdminCode.getCodeName("UN",
									value) : "";
							if (xuhao == 1)
								sheet.setColumnWidth((short) j, (short) 4000);
						} else if (j == 2) {
							value = value != null ? AdminCode.getCodeName("UM",
									value) : "";
							if (xuhao == 1)
								sheet.setColumnWidth((short) j, (short) 4000);
						} else if(j == 3){
							value = value != null ? AdminCode.getCodeName("@K", value) : "";
							if (xuhao == 1)
								sheet.setColumnWidth((short) j, (short) 4000);
						}else if(j==5&&xuhao==1)
							sheet.setColumnWidth((short) j, (short) 7000);
						else if((j==6||j==7)&&xuhao==1){
							sheet.setColumnWidth((short) j, (short) 4321);
						}
						
						String datatype = (String) dataTypeMap.get(new Integer(j));
						if ("N".equalsIgnoreCase(datatype)) {
							if(j==11)
								dataCell.setCellValue(rs.getString(j+3));
							else if(j==len-1)
								dataCell.setCellValue(rs.getInt(j+3)+"%");
							else{
								dataCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
								dataCell.setCellValue(rs.getInt(j+3));
							}
							
							dataCell.setCellStyle(style4);
						} else if ("A".equalsIgnoreCase(datatype)) {
							dataCell.setCellValue(new HSSFRichTextString(value));
							if(j==6||j==7) {
								dataCell.setCellStyle(style2);
							} else{
								dataCell.setCellStyle(style3);
							}
						}
						
					}
				}
				FileOutputStream fileOut = new FileOutputStream(System
						.getProperty("java.io.tmpdir")
						+ System.getProperty("file.separator") + outputFile);
				wb.write(fileOut);
				fileOut.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(wb);
		}

		return outputFile;
	}
	

	private ArrayList getTitles(){
		ArrayList titles = new ArrayList();
		LazyDynaBean abean = new LazyDynaBean();
		abean.set("datatype", "A");
		abean.set("title", "单位名称");
		titles.add(abean);
		abean = new LazyDynaBean();
		abean.set("datatype", "A");
		abean.set("title", "部门");
		titles.add(abean);
		abean = new LazyDynaBean();
		abean.set("datatype", "A");
		abean.set("title", "岗位名称");
		titles.add(abean);
		abean = new LazyDynaBean();
		abean.set("datatype", "A");
		abean.set("title", "姓名");
		titles.add(abean);
		abean = new LazyDynaBean();
		abean.set("datatype", "A");
		abean.set("title", "课程名称");
		titles.add(abean);
		abean = new LazyDynaBean();
		abean.set("datatype", "A");
		abean.set("title", "起始时间");
		titles.add(abean);
		abean = new LazyDynaBean();
		abean.set("datatype", "A");
		abean.set("title", "终止时间");
		titles.add(abean);
		abean = new LazyDynaBean();
		abean.set("datatype", "N");
		abean.set("title", "时长");
		titles.add(abean);
		abean = new LazyDynaBean();
		abean.set("datatype", "N");
		abean.set("title", "次数");
		titles.add(abean);
		abean = new LazyDynaBean();
		abean.set("datatype", "N");
		abean.set("title", "进度");
		titles.add(abean);
		abean = new LazyDynaBean();
		abean.set("datatype", "N");
		abean.set("title", "考试成绩");
		titles.add(abean);
		return titles;
	}
}
