package com.hjsj.hrms.transaction.hire.zp_options.positionstat;

import com.hjsj.hrms.businessobject.hire.zp_options.stat.positionstat.PositionStatBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.FileOutputStream;
import java.util.ArrayList;

public class ExportPositionStatExcelTrans extends IBusiness{

	public void execute() throws GeneralException {

		try(HSSFWorkbook workbook = new HSSFWorkbook())
		{
			String starttime = (String)this.getFormHM().get("starttime");
			String endtime = (String)this.getFormHM().get("endtime");
			PositionStatBo bo = new PositionStatBo(this.getFrameconn());
			boolean havecond =bo.getData(this.userView,starttime,endtime);
			ArrayList dataList =bo.getDataList();
			if(dataList==null)
				dataList = new ArrayList();
			String outName = "PositionStat_"+this.userView.getUserName()+".xls";

			HSSFSheet sheet = workbook.createSheet();
			HSSFRow row = null;
			HSSFCell cell = null;
			short n = 0;
			short k=this.setTitle(n, workbook, sheet,havecond);
			short h=this.setHead(k, workbook, sheet, havecond);
			String[][] data= this.getDataArray(havecond, dataList);
			//------------------------------------------------------
			/*short cellNum=0;
			HSSFCell csCell = null;
			HSSFFont font = workbook.createFont();
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			font.setFontHeightInPoints(Short.parseShort("20"));
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			//cellStyle.set
			row = sheet.createRow(h);
			csCell = row.createCell(cellNum);
			csCell.setCellStyle(cellStyle);
			csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
			csCell.setCellValue("首开\r\n集团");
			ExportExcelUtil.mergeCell(sheet, 7, (short)0, (dataList.size()+7), (short)1));	*/	
			this.setData(h, data, workbook, sheet, row, cell, havecond);	
			
			FileOutputStream fileOut = new FileOutputStream(System
					.getProperty("java.io.tmpdir")
					+ System.getProperty("file.separator") + outName);
			workbook.write(fileOut);
			fileOut.close();
			sheet = null;
			//outName=outName.replace(".xls","#");
			outName = PubFunc.encrypt(outName);
			this.getFormHM().put("outName",outName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	private void setData(short n, String[][] data, HSSFWorkbook workbook,
			HSSFSheet sheet, HSSFRow row, HSSFCell cell,boolean havecond) {
		try
		{
			short h = n;
			HSSFFont font = workbook.createFont();
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);
			int zs = 0;
			int rs=0;
			for(int i=0;i<data.length;i++)
			{
				short l=0;
				short r = 1;
				short s = 0;
				zs+=Integer.parseInt(((data[i][4]==null|| "".equals(data[i][4]))?"0":data[i][4]));
				rs+=Integer.parseInt(((data[i][5]==null|| "".equals(data[i][5]))?"0":data[i][5]));
				//row = sheet.createRow((short)(i+h));
				row = sheet.getRow((short)(i+h));
				if(row==null)
					row = sheet.createRow((short)(i+h));

				for(short t=0;t<data[i].length;t++)
				{
					cell=row.createCell(((short)(l)));
					//cell.setEncoding(arg0)
//					cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(data[i][t]);
					if(t==0||t==3||t==2)
		    		{
		    		   ExportExcelUtil.mergeCell(sheet, (i+h), (short)(s),(i+h) , (short)(r));
		    		   l++;
		    		   l++;
		    		   r=(short)(r+2);
		    		   s=(short)(s+2);
		    		}
		    		else if(t==7||t==6)
		    		{
		    			ExportExcelUtil.mergeCell(sheet, (i+h), (short)(s),(i+h), (short)(r+4));
			    		l++;
			    	    l++;
			    	    l++;
			    	    l++;
			    	    l++;
			    	    l++;
			    	    r=(short)(r+6);
			    		s=(short)(s+6);
		    		}
		    		else
		    		{
		    			ExportExcelUtil.mergeCell(sheet, (i+h), (short)(s), (i+h), (short)(s));
		    			l++;
		    			r=(short)(r+1);
		    			s=(short)(s+1);
		    		}
				}
				
			}
//			row = sheet.createRow((short)(data.length+h));
			row = sheet.getRow((short)(data.length+h));
			if(row==null)
				row = sheet.createRow((short)(data.length+h));

			short l=2;
			short r = 3;
			short s = (short)(data.length+h);
			for(int k=0;k<3;k++)
			{
		    	cell=row.createCell(((short)(l)));
//		    	cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				
				if(k==0)
				{
					HSSFFont tfont = workbook.createFont();
					tfont.setColor(HSSFFont.COLOR_RED);
					tfont.setBold(true);
					HSSFCellStyle tcellStyle = workbook.createCellStyle();
					tcellStyle.setFont(tfont);
					tcellStyle.setAlignment(HorizontalAlignment.CENTER);
					cell.setCellStyle(tcellStyle);
			    	cell.setCellValue("合计");
			    	ExportExcelUtil.mergeCell(sheet, (s), (short)(l),(s) , (short)(r+3));
			    	l=(short)(l+5);
			    	r=(short)(r+5);
				}
				if(k==1)
				{
					HSSFFont tfont = workbook.createFont();
					tfont.setColor(HSSFFont.COLOR_RED);
					tfont.setBold(true);
					HSSFCellStyle tcellStyle = workbook.createCellStyle();
					tcellStyle.setFont(tfont);
					tcellStyle.setAlignment(HorizontalAlignment.RIGHT);
					cell.setCellStyle(tcellStyle);
					cell.setCellValue(String.valueOf(zs));
					l++;
				}
				if(k==2)
				{
					HSSFFont tfont = workbook.createFont();
					tfont.setColor(HSSFFont.COLOR_RED);
					tfont.setBold(true);
					HSSFCellStyle tcellStyle = workbook.createCellStyle();
					tcellStyle.setFont(tfont);
					tcellStyle.setAlignment(HorizontalAlignment.RIGHT);
					cell.setCellStyle(tcellStyle);
					cell.setCellValue(String.valueOf(rs));
					l++;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private short setHead(short n,HSSFWorkbook workbook,
			HSSFSheet sheet,boolean havecond) 
	{
		short i= n;
		try
		{
			String[] head = null;
			if(havecond)
				head = new String[]{"序号","单位","部门","岗位|应聘专业","职数","人数","现任中层(数字为志愿顺序)","非中层(数字为志愿顺序)"};
			else
				head = new String[]{"序号","单位","部门","岗位|应聘专业","职数","人数","名单(数字为志愿顺序)"};
			HSSFRow row = null;
			HSSFCell csCell = null;
			HSSFFont font = workbook.createFont();
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			font.setFontHeightInPoints(Short.parseShort("12"));
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//			row = sheet.createRow(i);
			row = sheet.getRow(i);
			if(row==null)
				row = sheet.createRow(i);

			short l=0;
			short t = 1;
			short s = 0;
			for(int j=0;j<head.length;j++)
			{
	    		csCell = row.createCell((short)l);
	    		csCell.setCellStyle(cellStyle);
	    		csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
	    		csCell.setCellValue(head[j]);
	    		if(j==0||j==2||j==3)
	    		{
	    		   ExportExcelUtil.mergeCell(sheet, 6, (short)s, 6, (short)(t));
	    		   l++;
	    		   l++;
	    		   t=(short)(t+2);
	    		   s=(short)(s+2);
	    		}
	    		else if(j==6||j==7)
	    		{
	    			ExportExcelUtil.mergeCell(sheet, 6, (short)s, 6, (short)(t+4));
		    		l++;
		    	    l++;
		    	    l++;
		    	    l++;
		    	    l++;
		    	    l++;
		    	    t=(short)(t+6);
		    		s=(short)(s+6);
	    		}
	    		else
	    		{
	    			l++;
	    			t=(short)(t+1);
	    			s=(short)(s+1);
	    		}
			}
			i++;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return i;
	}
	private short setTitle(short n,HSSFWorkbook workbook,HSSFSheet sheet,boolean havecode) {
		short i = n;
		try
		{
			HSSFRow row = null;
			HSSFCell csCell = null;
			HSSFFont font = workbook.createFont();
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			font.setFontHeightInPoints(Short.parseShort("25"));
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//			row = sheet.createRow(i);
			row = sheet.getRow(i);
			if(row==null)
				row = sheet.createRow(i);
            short toX=14;
            if(havecode)
            	toX=20;
			csCell = row.createCell((short)0);
			csCell.setCellStyle(cellStyle);
			csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
			csCell.setCellValue("应聘岗位报名情况统计表");
			ExportExcelUtil.mergeCell(sheet, 0, (short)0, 5, (short)toX);		
			i++;
			i++;
			i++;
			i++;
			i++;
			i++;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return i;
	}
	public String[][] getDataArray(boolean havecond,ArrayList list)
	{
		int size = 7;
		if(havecond)
			size = 8;
		String[][] data = new String[list.size()][size];
		try
		{
			LazyDynaBean  bean = null;
			for(int i=0;i<list.size();i++)
			{
				bean =(LazyDynaBean)list.get(i);
				data[i][0] = (i+1)+"";
				data[i][1] = (String)bean.get("un");
				data[i][2] = (String)bean.get("um");
				data[i][3] = (String)bean.get("atk");
				data[i][4] = (String)bean.get("z0313");
				data[i][5] = (String)bean.get("count");
				data[i][6] = (String)bean.get("zc");
				if(havecond)
				   data[i][7] = (String)bean.get("fzc");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return data;
	}

}
