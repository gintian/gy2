package com.hjsj.hrms.transaction.sys.dbinit.gather;

import com.hjsj.hrms.businessobject.sys.gathertable.GatherTableBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * <p>Title:导出人员与单位采集表(Excel)</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 15, 2008:4:18:04 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class IndexUseExportTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HSSFWorkbook workbook = null;
		try{
			String set= (String)this.getFormHM().get("set");//指标集
			String usename = (String)this.getFormHM().get("usename");//信息集
			String udat = (String)this.getFormHM().get("udata");//代码项个数
			int udata = Integer.parseInt(udat);
			String num = (String)this.getFormHM().get("num");//指标集个数
			String usefy = (String)this.getFormHM().get("usefy");
			GatherTableBo gather = new GatherTableBo(this.getFrameconn());
			
			HashMap topname = this.topname(usename);
			workbook= new HSSFWorkbook();   // 创建新的Excel 工作簿
//			HSSFSheet sheet = workbook.createSheet("explain"); //生成一张表;
			HSSFRow row = null;  //行
			HSSFCell cell=null;   //单元格
			String outName="";
			HSSFComment comment=null;  //定义注释
//			if(usename.equals("A")){
				outName=this.userView.getUserName()+"_rycjb.xls";  //人员采集表名称
//			}else if(usename.equals("B")){
//				outName="dycjb.xls";  //单位采集表名称
//			}
			short n=0;
//			n=setindexHead(n,topname,workbook,sheet,usename,usefy); //生成表头
			HashMap map = gather.getparticular(set, udata);   //详细内容
			ArrayList indexinfolist=(ArrayList)map.get("1");  //详细内容头
			HashMap amap = (HashMap)map.get("2");			 //详细内容体
			setIndexData(amap,n,indexinfolist,workbook,row,cell,usefy,usename,topname); //写内容
			
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outName);
			workbook.write(fileOut);
			fileOut.close();	
//			sheet=null;
//			workbook=null;
			//outName=outName.replace(".xls","#");
			//xus 20/4/29 vfs改造
			outName = PubFunc.encrypt(outName);
//			outName = SafeCode.encode(PubFunc.encrypt(outName));
			this.getFormHM().put("outName",outName);
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(workbook);
		}
	}
	//生成头名称
	public HashMap topname(String usename){
		HashMap map = new HashMap();
		try{
//			if(usename.equals("A")){
//				map.put("username", ResourceFactory.getProperty("kjg.title.username"));
//			}else if(usename.equals("B")){
//				map.put("unitname", ResourceFactory.getProperty("kjg.title.unitname"));
//			}
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String retn="";
			String sql = "select classname from informationclass where classpre ='"+usename+"'";
			RowSet rowSet = dao.search(sql.toString());
			while(rowSet.next()){
				retn=rowSet.getString("classname");
				String retname = retn+ResourceFactory.getProperty("button.colcard");
				map.put("username", retname);
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
//	private short setindexHead(short n,HashMap map,HSSFWorkbook workbook,HSSFSheet sheet,String usename,String usefy){
//		short i=n;
//			try{
//				if(usefy.equals("0")){
//				HSSFRow row=null;  //行
//				HSSFCell csCell=null;  //单元格
//				HSSFPatriarch patr=sheet.createDrawingPatriarch();  //声明一个画图的顶级管理器
//
//				
//				row=sheet.createRow(i); //创建Excel中一行 sheet.getRow(i)的区别  --读取Excel中一行
//				csCell=row.createCell((short)2);
//				
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
//				if(usename.equals("A")){
//					csCell.setCellValue((String)map.get("username")); //往单元格里输入内容
//				    i++;
//				}else if(usename.equals("B")){
//					csCell.setCellValue((String)map.get("unitname")); //往单元格里输入内容
//				    i++;
//				}
//				}else if(usefy.equals("1")){
//					
//				}
//				
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//		return i;
//	}
//	写体内容
	public void setIndexData(HashMap map,short n,ArrayList infoList,HSSFWorkbook workbook,HSSFRow row,HSSFCell cell,String usefy,String usename,HashMap topname){
		try{
			CellRangeAddress region=null;
			HSSFFont font = workbook.createFont();
			if("0".equals(usefy)){
				short h=n;
				HSSFSheet sheet = workbook.createSheet("explain"); //生成一张表;
				row = sheet.getRow(h);
				if(row==null)
					row=sheet.createRow(h); //创建Excel中一行 sheet.getRow(i)的区别  --读取Excel中一行
				cell=row.createCell(1);
//				cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
//				if(usename.equals("A")){
					cell.setCellValue((String)topname.get("username")); //往单元格里输入内容
				    h++;
//				}else if(usename.equals("B")){
//					cell.setCellValue((String)topname.get("unitname")); //往单元格里输入内容
//				    h++;
//				}
//				 设置列宽,参数一，第几列
				sheet.setColumnWidth(0,6000);
				sheet.setColumnWidth(1,12000);
				for(int i=0;i<infoList.size();i++){
					LazyDynaBean bean = (LazyDynaBean)infoList.get(i);
					row = sheet.getRow(h+2);
					if(row==null)
						row = sheet.createRow(h+2);
//					定义行高度
					row.setHeightInPoints(20);   
//					 合并单元格，参数，从第几行，该行的第几个单元格，到第几行，第几个单元格
					ExportExcelUtil.mergeCell(sheet, h+2,(short)0,h+2,(short)1);
//					region= new CellRangeAddress (h+2,(short)0,h+2,(short)1);
//					sheet.addMergedRegion(region);
//					HSSFCellStyle cs=this.setBorder(workbook);
//					for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
//		    			row = sheet.createRow(p);
//			            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
//			            	cell = row.createCell((short)o);
//			                cell.setCellStyle(cs);
//			            }
//					}
					cell=row.createCell(0);  //写入的单元各位置;
//					cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
		    		cell.setCellValue((String)bean.get("fieldsetid")+" : "+(String)bean.get("customdesc"));
		    		cell.setCellStyle(this.setDateStyle(workbook,font));
		    		ArrayList fieldlist = (ArrayList)map.get((String)bean.get("fieldsetid")); //对应的fieldsetid
		    		for(int j=0;j<fieldlist.size();j++){
					String put="";
		    			LazyDynaBean abean = (LazyDynaBean)fieldlist.get(j);
//		    			row = sheet.createRow(h+5);
//		    			row.setHeightInPoints(17);  //定义行高度
//		    			cell=row.createCell((short)0); //第几个单元格
//		        		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
//		        		cell.setCellValue((String)abean.get("itemdesc"));
//		        		cell.setCellStyle(this.setDataStyless(workbook));
		        		ArrayList field = (ArrayList)map.get((String)abean.get("itemdesc")); //对应的itemdesc
			    		for(int k=0;k<field.size();k++){
			    			LazyDynaBean abeans = (LazyDynaBean)field.get(k);
			    			String itemdesc = (String)abean.get("itemdesc");
			    			if(itemdesc.equals((String)abean.get("itemdesc"))){
			    				put+=(String)abeans.get("codeitemid")+" "+(String)abeans.get("codeitemdesc")+"       ";
			    			}
			    		}
//			    		row = sheet.createRow(h+5);
//		        		cell=row.createCell((short)1);
//		        		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
//		        		cell.setCellValue(put);
//		        		cell.setCellStyle(this.setDataStyle(workbook));

		        		if(!"".equals(put)){
//			    			ExportExcelUtil.mergeCell(sheet, h+4,(short)0,h+5,(short)0)); 
		        			region= new CellRangeAddress (h+4,(short)0,h+5,(short)0);
		        			sheet.addMergedRegion(region);
		        			HSSFCellStyle css=this.setBorder(workbook);
		        			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
		        				row = sheet.getRow(p);
		        				if(row==null)
		        					row = sheet.createRow(p);
		        	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
		        	            	cell = row.createCell(o);
		        	                cell.setCellStyle(css);
		        	            }
		        			}
		        			row = sheet.getRow(h+4);
		    				if(row==null)
		    					row = sheet.createRow(h+4);
			    			cell=row.createCell(0); //第几个单元格
//			        		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			        		cell.setCellValue((String)abean.get("itemdesc"));
			        		cell.setCellStyle(this.setDataStyless(workbook,font));
			        		h++;
			    		}else{
			    			row = sheet.getRow(h+4);
							if(row==null)
								row = sheet.createRow(h+4);
			    			cell=row.createCell(0); //第几个单元格
//			        		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			        		cell.setCellValue((String)abean.get("itemdesc"));
			        		cell.setCellStyle(this.setDataStyless(workbook,font));
			        		h++;
			    		}
		        		if(!"".equals(put)){
		        			row = sheet.getRow(h+3);
		    				if(row==null)
		    					row = sheet.createRow(h+3);
		        			cell=row.createCell(1);
//			        		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			        		cell.setCellValue(" ");
			        		cell.setCellStyle(this.setDataStyle(workbook,font));
			        		row = sheet.getRow(h+4);
							if(row==null)
								row = sheet.createRow(h+4);
			        		cell=row.createCell(1);
//			        		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			        		cell.setCellValue(put);
			        		cell.setCellStyle(this.setDataStyle(workbook,font));
			        		h++;
		        		}else{
		        			row = sheet.getRow(h+3);
		    				if(row==null)
		    					row = sheet.createRow(h+3);
			    			//row.setHeightInPoints(17);  //定义行高度
			        		cell=row.createCell(1);
//			        		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			        		cell.setCellValue(put);
			        		cell.setCellStyle(this.setDataStyle(workbook,font));
			        	
		        		}
//		        		h++;
		    		}
		    		h++;
		    		h++;
		    		h++;
		    		h++;
				}
			}else if("1".equals(usefy)){
				
				HSSFSheet sheets = null;
				for(int i=0;i<infoList.size();i++){
					short h=0;
					LazyDynaBean bean = (LazyDynaBean)infoList.get(i);
					sheets = workbook.createSheet((String)bean.get("fieldsetid")+""+(String)bean.get("customdesc"));
//					 设置列宽,参数一，第几列
					sheets.setColumnWidth(0,6000);
					sheets.setColumnWidth(1,12000);
					row = sheets.getRow(h+2);
					if(row==null)
						row = sheets.createRow(h+2);
//					 合并单元格，参数，从第几行，该行的第几个单元格，到第几行，第几个单元格
					ExportExcelUtil.mergeCell(sheets, h+2,(short)0,h+2,(short)1); 
					cell=row.createCell(0);  //写入的单元各位置;
//					cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
		    		cell.setCellValue((String)bean.get("fieldsetid")+" : "+(String)bean.get("customdesc"));
		    		cell.setCellStyle(this.setDateStyle(workbook,font));
		    		ArrayList fieldlist = (ArrayList)map.get((String)bean.get("fieldsetid")); //对应的fieldsetid
		    		for(int j=0;j<fieldlist.size();j++){
		    			String put="";
		    			LazyDynaBean abean = (LazyDynaBean)fieldlist.get(j);
		    			ArrayList field = (ArrayList)map.get((String)abean.get("itemdesc")); //对应的itemdesc
			    		for(int k=0;k<field.size();k++){
			    			LazyDynaBean abeans = (LazyDynaBean)field.get(k);
			    			String itemdesc = (String)abean.get("itemdesc");
			    			if(itemdesc.equals((String)abean.get("itemdesc"))){
			    				put+=(String)abeans.get("codeitemid")+" "+(String)abeans.get("codeitemdesc")+"       ";
			    			}
			    		}
			    		if(!"".equals(put)){
			    			region= new CellRangeAddress(h+4,h+5,(short)0,(short)0);
			    			sheets.addMergedRegion(region);
			    			HSSFCellStyle css=this.setBorder(workbook);
			    			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
			    				row = sheets.getRow(p);
			    				if(row==null)
			    					row = sheets.createRow(p);
			    	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
			    	            	cell = row.createCell((short)o);
			    	                cell.setCellStyle(css);
			    	            }
			    			}
		    				row = sheets.getRow(h+4);
		    				if(row==null)
		    					row = sheets.createRow(h+4);
			    			cell=row.createCell(0); //第几个单元格
//			        		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			        		cell.setCellValue((String)abean.get("itemdesc"));
			        		cell.setCellStyle(this.setDataStyless(workbook,font));
			        		h++;
			    		}else{
		    				row = sheets.getRow(h+4);
		    				if(row==null)
		    					row = sheets.createRow(h+4);
			    			cell=row.createCell(0); //第几个单元格
//			        		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			        		cell.setCellValue((String)abean.get("itemdesc"));
			        		cell.setCellStyle(this.setDataStyless(workbook,font));
			        		h++;
			    		}
		        		if(!"".equals(put)){
		    				row = sheets.getRow(h+3);
		    				if(row==null)
		    					row = sheets.createRow(h+3);
		        			cell=row.createCell(1);
//			        		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			        		cell.setCellValue(" ");
			        		cell.setCellStyle(this.setDataStyle(workbook,font));
		    				row = sheets.getRow(h+4);
		    				if(row==null)
		    					row = sheets.createRow(h+4);
			        		cell=row.createCell(1);
//			        		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			        		cell.setCellValue(put);
			        		cell.setCellStyle(this.setDataStyle(workbook,font));
			        		h++;
		        		}else{
		    				row = sheets.getRow(h+3);
		    				if(row==null)
		    					row = sheets.createRow(h+3);
			    			//row.setHeightInPoints(17);  //定义行高度
			        		cell=row.createCell((short)1);
//			        		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			        		cell.setCellValue(put);
			        		cell.setCellStyle(this.setDataStyle(workbook,font));
			        	
		        		}
		    		}
//		    		h++;
//		    		h++;
//		    		h++;
//		    		h++;
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 设置单元格格式
	 * @param workbook
	 * @return
	 */
	//右边样式
	public HSSFCellStyle setDataStyle(HSSFWorkbook workbook,HSSFFont font) 
	{
		 // 先定义一个字体对象
//        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); // 单元格字体
        style.setAlignment(HorizontalAlignment.LEFT); // 右边对齐方式
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN); // 表格细边框
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setWrapText(true);
        return style;

	}
	public HSSFCellStyle setDateStyle(HSSFWorkbook workbook,HSSFFont font) 
	{
		 // 先定义一个字体对象
//        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 13); // 字体大小
        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); // 单元格字体
        style.setAlignment(HorizontalAlignment.CENTER); // 居中对齐方式 左右
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式 上下
        return style;

	}
	//表格左边样式
	public HSSFCellStyle setDataStyless(HSSFWorkbook workbook,HSSFFont font) 
	{
		 // 先定义一个字体对象
//        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); // 单元格字体
        style.setAlignment(HorizontalAlignment.CENTER); // 居中对齐方式  左右
        style.setVerticalAlignment(VerticalAlignment.TOP); //垂直顶端  上下
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN); // 表格细边框
        style.setBorderRight(BorderStyle.THIN); //右边框
        style.setBorderLeft(BorderStyle.THIN);  //左边框
        style.setWrapText(true);
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
}
