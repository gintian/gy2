package com.hjsj.hrms.utils.components.tablefactory.transaction;

import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ExportPlanAnalyseExcel extends IBusiness {
	HSSFWorkbook workbook = null;
	HSSFSheet sheet = null;
	public void execute() throws GeneralException {
             String subModuleId = this.formHM.get("subModuleId").toString();
             
             HashMap excelData = (HashMap)userView.getHm().get(subModuleId+"_export");
             
             ArrayList hItems = (ArrayList)excelData.get("hItems");
             ArrayList vItems = (ArrayList)excelData.get("vItems");
             ArrayList values = (ArrayList)excelData.get("values");
             String analyseType = excelData.get("analyseType").toString();
             String itemType = (String)excelData.get("itemType");
             String filename = "grid_"+userView.getUserName()+".xls";
             FileOutputStream fileOut = null;
             try{
             
             workbook = new HSSFWorkbook();
             sheet = workbook.createSheet("sheet1");
             sheet.setDefaultColumnWidth(10);
             sheet.setDefaultRowHeight((short)300);
             int rownum = 2;
             int rownum2 = 2;
             int colnum = 2;
             int colnum2 = 2;
             HSSFCell cell = null;
             HSSFCellStyle titleStyle = workbook.createCellStyle();
             //titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
             //titleStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);// 设置背景色
             titleStyle.setBorderBottom(BorderStyle.THIN);
             titleStyle.setBorderTop(BorderStyle.THIN);
             titleStyle.setBorderLeft(BorderStyle.THIN);
             titleStyle.setBorderRight(BorderStyle.THIN);
             titleStyle.setAlignment(HorizontalAlignment.CENTER);
             titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
             
	         HSSFRow row1 = sheet.createRow(0);
	         HSSFRow row2 = sheet.createRow(1);
	         cell = row1.createCell(0);
	         cell.setCellValue("项目");
	         cell.setCellStyle(titleStyle);
	         ExportExcelUtil.mergeCell(sheet, 0, (short)0, 1, (short)1);
	         
	         if(hItems.isEmpty()){
	        	 String title = "值";
			       if(!"1".equals(analyseType)){
			    	   FieldItem fi = DataDictionary.getFieldItem(itemType);
			    	   title = fi.getItemdesc();
			       }
	        	 cell = row1.createCell(colnum);
	        	 cell.setCellValue(title);
	        	 cell.setCellStyle(titleStyle);
	        	 //ExportExcelUtil.mergeCell(sheet, 0, (short)colnum, 1, (short)colnum));
	        	 processMergedRegion(0,1,colnum,colnum);
	        	 colnum++;
	        	 cell = row1.createCell(colnum);
	        	 cell.setCellStyle(titleStyle);
	        	 cell.setCellValue("合计");
	        	 processMergedRegion(0,1,colnum,colnum);
	         }else{
	             for(int i=0;i<hItems.size();i++){
	             	HashMap db = (HashMap)hItems.get(i);
	             	String itemdesc = db.get("itemName").toString();
	             	ArrayList code = (ArrayList)db.get("conds");
	             	ArrayList child = (ArrayList)db.get("child");
	             	if(child.isEmpty()){
	             		cell = row1.createCell(colnum);
	             		cell.setCellValue(itemdesc);
	             		cell.setCellStyle(titleStyle);
	             		//ExportExcelUtil.mergeCell(sheet, 0, (short)(colnum), 0, (short)(colnum+code.size()-1)));
	             		processMergedRegion(0,0,colnum,colnum+code.size()-1);
	             		colnum=colnum+code.size();
	             		for(int k=0;k<code.size();k++){
	             			String desc = ((HashMap)code.get(k)).get("condName").toString();
	             			cell = row2.createCell(colnum2);
	             			cell.setCellValue(desc);
	             			cell.setCellStyle(titleStyle);
	             			colnum2++;
	             		}
	             		
	             		continue;
	             	}
	             	
	             	for(int k=0;k<code.size();k++){
	             		String codedesc = ((HashMap)code.get(k)).get("condName").toString();
	             	    int colspanNum = 0;
	 	            	for(int b=0;b<child.size();b++){
	 	            		HashMap cdb = (HashMap)child.get(b);
	 	                	ArrayList ccode = (ArrayList)cdb.get("conds");
	 	                	colspanNum+=ccode.size();
	 	                	for(int d =0;d<ccode.size();d++){
	 	                		String desc = ((HashMap)ccode.get(d)).get("condName").toString();
	 	                		cell = row2.createCell(colnum2);
	 	                		cell.setCellValue(desc);
	 	                		cell.setCellStyle(titleStyle);
	 	                		colnum2++;
	 	                	}
	 	                	
	 	            	}
	 	            	cell = row1.createCell(colnum);
	 	            	cell.setCellValue(codedesc);
	 	            	cell.setCellStyle(titleStyle);
	 	            	//ExportExcelUtil.mergeCell(sheet, 0, (short)(colnum), 0, (short)(colnum+colspanNum-1)));
	 	            	processMergedRegion(0,0,colnum,colnum+colspanNum-1);
	 	            	colnum= colnum+colspanNum;
	             	}
	             
	             }
	             cell = row1.createCell(colnum2);
	             cell.setCellValue("合计");
	             cell.setCellStyle(titleStyle);
	             //ExportExcelUtil.mergeCell(sheet, 0, (short)(colnum2), 1, (short)(colnum2)));
	             processMergedRegion(0,1,colnum2,colnum2);
	         }
             
	         if(vItems.isEmpty()){
	        	 String title = "值";
			       if(!"1".equals(analyseType)){
			    	   FieldItem fi = DataDictionary.getFieldItem(itemType);
			    	   title = fi.getItemdesc();
			       }
			       cell = sheet.createRow(rownum).createCell(0);
			       cell.setCellValue(title);
			       cell.setCellStyle(titleStyle);
			       //ExportExcelUtil.mergeCell(sheet, rownum, (short)0, rownum, (short)1));
			       processMergedRegion(rownum,rownum,0,1);
			       rownum++;
			       cell = sheet.createRow(rownum).createCell(0);
			       cell.setCellValue("合计");
			       cell.setCellStyle(titleStyle);
			       //ExportExcelUtil.mergeCell(sheet, rownum, (short)0, rownum, (short)1));
			       processMergedRegion(rownum,rownum,0,1);
	         }else{
	             for(int i=0;i<vItems.size();i++){
	 		    	HashMap db = (HashMap)vItems.get(i);
	             	String itemdesc = db.get("itemName").toString();
	             	ArrayList code = (ArrayList)db.get("conds");
	             	ArrayList child = (ArrayList)db.get("child");
	             	if(child.isEmpty()){
	             		for(int k=0;k<code.size();k++){
	             			String desc = ((HashMap)code.get(k)).get("condName").toString();
	             			row1  = sheet.createRow(rownum2);
	             			cell = row1.createCell(1);
	             			cell.setCellValue(desc);
	             			cell.setCellStyle(titleStyle);
	             			rownum2++;
	             		}
	             		cell = sheet.getRow(rownum).createCell(0);
	             		cell.setCellValue(itemdesc);
	             		cell.setCellStyle(titleStyle);
	             		//ExportExcelUtil.mergeCell(sheet, rownum, (short)0, rownum+code.size()-1, (short)0));
	             		processMergedRegion(rownum,rownum+code.size()-1,0,0);
	             		rownum = rownum+code.size();
	             		continue;
	             	}
	             	
	             	for(int k=0;k<code.size();k++){
	             	    String codedesc = ((HashMap)code.get(k)).get("condName").toString();
	 	            	int rowspanNum = 0;
	 	            	ArrayList strs = new ArrayList();
	 	            	for(int b=0;b<child.size();b++){
	 	            		HashMap cdb = (HashMap)child.get(b);
	 	                	ArrayList ccode = (ArrayList)cdb.get("conds");
	 	                	rowspanNum+=ccode.size();
	 	                	for(int d =0;d<ccode.size();d++){
	 	                		String desc = ((HashMap)ccode.get(d)).get("condName").toString();
	 	                		cell = sheet.createRow(rownum2).createCell(1);
	 	                		cell.setCellValue(desc);
	 	                		cell.setCellStyle(titleStyle);
	 	                		rownum2++;
	 	                	}
	 	            	}
	 	            	cell = sheet.getRow(rownum).createCell(0);
	 	            	cell.setCellValue(codedesc);
	 	            	cell.setCellStyle(titleStyle);
	 	            	//ExportExcelUtil.mergeCell(sheet, rownum, (short)0, rownum+rowspanNum-1, (short)0));
	 	            	processMergedRegion(rownum,rownum+rowspanNum-1,0,0);
	 	            	rownum = rownum+rowspanNum;
	             	}
	 		    }
	         }
             
            rownum=2; 
            colnum=2;
            
            double total = 0;
		    double[] htotal = null;
		    
            for(int i=0;i<values.size();i++){
            	String[] rowvalue = values.get(i).toString().split(",");
            	row1 = sheet.getRow(rownum);
            	double  hTotal = 0;
            	if(htotal==null)
		    		htotal = new double[rowvalue.length];
            	for(int k=0;k<rowvalue.length;k++){
            		cell = row1.createCell(colnum);
            		double value = Double.parseDouble(rowvalue[k]);
            		cell.setCellStyle(titleStyle);
            		cell.setCellValue(value);
            		hTotal+=value;
		    		total+= value;
		    		htotal[k] = htotal[k]+value;
            		colnum++;
            	}
            	cell = row1.createCell(colnum);
            	cell.setCellStyle(titleStyle);
            	cell.setCellValue(hTotal);
            	rownum++;
            	colnum=2;
            }
            
            row1 = sheet.createRow(rownum);
            cell = row1.createCell(0);
            cell.setCellStyle(titleStyle);
            cell.setCellValue("合计");
            //ExportExcelUtil.mergeCell(sheet, rownum, (short)0, rownum, (short)1));
            processMergedRegion(rownum,rownum,0,1);
            
            for(int i=0;i<htotal.length;i++){
            	cell = row1.createCell(colnum);
            	cell.setCellStyle(titleStyle);
            	cell.setCellValue(htotal[i]);
            	colnum++;
            }
            
            cell = row1.createCell(colnum);
            cell.setCellStyle(titleStyle);
            cell.setCellValue(total);
            
	            fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+filename);
	  			workbook.write(fileOut);
	  			fileOut.flush();
             }catch(Exception e){
            	 e.printStackTrace();
             }finally{
     			PubFunc.closeResource(fileOut);
     		}
             
 			
 			filename = PubFunc.encrypt(filename);
 			this.getFormHM().put("filename",filename);
 			
 			//userView.getHm().remove(subModuleId+"_export");
             
	}

	/**
	 * add by xiegh on date 20180129 bug:33867
	 * 用addMergedRegion合并单元格有问题  合成后的单元格一半有边线，一半没有边线
	 * @param firstRow
	 * @param lastRow
	 * @param firstCol
	 * @param lastCol
	 * @return
	 */
	private void processMergedRegion(int firstRow, int lastRow, int firstCol, int lastCol) {
		if(firstRow >= lastRow && firstCol >= lastCol)
			return;
		
		 CellRangeAddress cra=new CellRangeAddress(firstRow,lastRow,firstCol,lastCol);
		 try{
			 //zhangh 2019-11-22 解决bug【55119】前面已经调用过了合并单元格的方法，不能再次合并了
			 sheet.addMergedRegion(cra);
		 }catch(Exception e){

		 }
		 RegionUtil.setBorderBottom(1, cra, sheet);
		 RegionUtil.setBorderLeft(1, cra, sheet);
		 RegionUtil.setBorderRight(1, cra, sheet);
		 RegionUtil.setBorderTop(1, cra, sheet);
	}

}
