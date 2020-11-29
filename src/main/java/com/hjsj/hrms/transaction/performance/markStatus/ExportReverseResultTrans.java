package com.hjsj.hrms.transaction.performance.markStatus;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * <p>Title:ExportReverseResultTrans.java</p>
 * <p>Description:反查结果导出Excel</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-07-11 11:11:11</p>
 * @author JinChunhai
 * @version 5.0
 */

public class ExportReverseResultTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		String plan_id=PubFunc.decrypt(SafeCode.decode((String)this.getFormHM().get("plan_id")));	// 考核计划号	
		String selectFashion=(String)this.getFormHM().get("selectFashion"); // 查询方式 1:按考核主体  2:考核对象
		String object_type=(String)this.getFormHM().get("object_type"); // 类型 
		String b0110=(String)this.getFormHM().get("b0110");     // 单位
		String e0122=(String)this.getFormHM().get("e0122");     // 部门
		String type=(String)this.getFormHM().get("type"); // 状态  
				
		ReverseResultTrans rrt = new ReverseResultTrans();
		
		ArrayList nameList=new ArrayList();
		nameList.add(ResourceFactory.getProperty("conlumn.mediainfo.info_id"));
		nameList.add(ResourceFactory.getProperty("b0110.label"));
		
		if((!"2".equalsIgnoreCase(object_type)) && ("2".equalsIgnoreCase(selectFashion)))
		{			  			 	
			nameList.add(ResourceFactory.getProperty("org.performance.unorum"));	        	
		}else
		{		 
			FieldItem fielditem = DataDictionary.getFieldItem("E0122");			  			 	
			nameList.add(fielditem.getItemdesc());
			nameList.add(ResourceFactory.getProperty("e01a1.label"));
			nameList.add(ResourceFactory.getProperty("kq.card.emp.name"));
		}		
				
		this.createExcel(b0110, e0122, type, selectFashion, object_type, nameList, rrt.getReverseResultList(this.getFrameconn(),this.getUserView(), selectFashion, b0110, e0122, type, object_type, plan_id));						
								
	}
	public void createExcel(String unit, String sector, String type, String selectFashion, String object_type, ArrayList nameList, ArrayList reverseResultList)
	{
		HSSFWorkbook wb = null;
		try {
			wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFFont font2 = wb.createFont();
			font2.setFontHeightInPoints((short) 15);
			HSSFFont font3 = wb.createFont();
			font3.setFontHeightInPoints((short) 10);
			HSSFCellStyle style2 = wb.createCellStyle();
			HSSFCellStyle style3 = wb.createCellStyle();
			HSSFCellStyle stylenum = wb.createCellStyle();
			style2.setFont(font3);
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
			style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

			style3.setFont(font3);
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

			stylenum.setFont(font3);
			stylenum.setAlignment(HorizontalAlignment.RIGHT);
			stylenum.setVerticalAlignment(VerticalAlignment.CENTER);

			HSSFRow row = null;
			HSSFCell cell = null;

			// 头
			row = sheet.createRow(0);
			for (int i = 0; i < nameList.size(); i++) {
				sheet.setColumnWidth(i, (short) 8000);
				row.setHeight((short) 500);
				cell = row.createCell(i);
				cell.setCellValue((String) nameList.get(i));
				cell.setCellStyle(style2);
			}

			// 体
			for (int i = 0; i < reverseResultList.size(); i++) {
				LazyDynaBean abean = (LazyDynaBean) reverseResultList.get(i);

				String numbers = "";
				String b0110 = "";
				String e0122 = "";
				String e01a1 = "";
				String a0101 = "";

				if ((!"2".equalsIgnoreCase(object_type)) && ("2".equalsIgnoreCase(selectFashion))) {
					numbers = (String) abean.get("numbers");
					b0110 = (String) abean.get("b0110");
					a0101 = (String) abean.get("a0101");
				} else {
					numbers = (String) abean.get("numbers");
					b0110 = (String) abean.get("b0110");
					e0122 = (String) abean.get("e0122");
					e01a1 = (String) abean.get("e01a1");
					a0101 = (String) abean.get("a0101");
				}

				int m = 0;
				row = sheet.createRow(i + 1);
				row.setHeight((short) 500);

				if ((!"2".equalsIgnoreCase(object_type)) && ("2".equalsIgnoreCase(selectFashion))) {
					cell = row.createCell(0);
					cell.setCellValue(numbers);
					cell.setCellStyle(style3);

					cell = row.createCell(1);
					cell.setCellValue(b0110);
					cell.setCellStyle(style3);

					cell = row.createCell(2);
					cell.setCellValue(a0101);
					cell.setCellStyle(style3);
				} else {
					cell = row.createCell(0);
					cell.setCellValue(numbers);
					cell.setCellStyle(style3);

					cell = row.createCell(1);
					cell.setCellValue(b0110);
					cell.setCellStyle(style3);

					cell = row.createCell(2);
					cell.setCellValue(e0122);
					cell.setCellStyle(style3);

					cell = row.createCell(3);
					cell.setCellValue(e01a1);
					cell.setCellStyle(style3);

					cell = row.createCell(4);
					cell.setCellValue(a0101);
					cell.setCellStyle(style3);
				}
			}

			String outName = "";
			FileOutputStream fileOut = null;
			try {
				String name = "";
				if ("b0110".equalsIgnoreCase(unit))
					name = "总计";
				else
					name = AdminCode.getCodeName("UN", unit);

				if (((!"b0110".equalsIgnoreCase(unit)) && (sector == null || sector.trim().length() <= 0)))
					name = AdminCode.getCodeName("UN", unit);
				else if (((!"b0110".equalsIgnoreCase(unit)) && (sector != null && sector.trim().length() > 0)))
					name += AdminCode.getCodeName("UM", sector);

				if ("allScore".equalsIgnoreCase(type))
					name += "全部";
				else if ("0".equalsIgnoreCase(type))
					name += "未评分";
				else if ("1".equalsIgnoreCase(type) || "5".equalsIgnoreCase(type))
					name += "正评分";
				else if ("2".equalsIgnoreCase(type))
					name += "已评分";

				outName = name + "的反查结果" + PubFunc.getStrg() + ".xls";

				fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
				wb.write(fileOut);
				fileOut.close();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				PubFunc.closeResource(fileOut);
			}
			outName = PubFunc.encrypt(outName);
			//20/3/6 xus vfs改造
//			outName = SafeCode.encode(outName);

			sheet = null;
			this.getFormHM().put("name", outName);
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(wb);
		}
		
	}
	
}

