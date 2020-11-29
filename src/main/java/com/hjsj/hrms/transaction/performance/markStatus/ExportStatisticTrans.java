package com.hjsj.hrms.transaction.performance.markStatus;

import com.hjsj.hrms.businessobject.performance.markStatus.ScoreStatusBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
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
import java.util.HashMap;
import java.util.Set;

/**
 * <p>Title:ExportStatisticTrans.java</p>
 * <p>Description:导出Excel</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-07-09 11:11:11</p>
 * @author JinChunhai
 * @version 5.0
 */

public class ExportStatisticTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			String plan_id=PubFunc.decrypt(SafeCode.decode((String)this.getFormHM().get("plan_id")));	// 考核计划号	
			String selectFashion=(String)this.getFormHM().get("selectFashion"); // 查询方式 1:按考核主体  2:考核对象
			String scoreType=(String)this.getFormHM().get("scoreType"); // 评分状态
	
			ScoreStatusBo sb=new ScoreStatusBo(this.getFrameconn(),this.getUserView());
			
			ArrayList nameList=new ArrayList();
			nameList.add(ResourceFactory.getProperty("b0110.label"));
			
			FieldItem fielditem = DataDictionary.getFieldItem("E0122");			  			 	
			nameList.add(fielditem.getItemdesc());	
			
			if("1".equals(selectFashion))
				nameList.add(ResourceFactory.getProperty("jx.selfScore.mainbodyList"));			
			else if("2".equals(selectFashion))
				nameList.add(ResourceFactory.getProperty("jx.selfScore.objectList"));
			
			if("all".equalsIgnoreCase(scoreType))
			{ 
				nameList.add(ResourceFactory.getProperty("lable.performnace.wpf"));
				nameList.add(ResourceFactory.getProperty("lable.performnace.nowpingscore"));
				nameList.add(ResourceFactory.getProperty("lable.performnace.havepingscore"));
				
		    }else if("01".equalsIgnoreCase(scoreType))
		    	nameList.add(ResourceFactory.getProperty("lable.performnace.wpf"));	    	
		    else if("02".equalsIgnoreCase(scoreType))
		    	nameList.add(ResourceFactory.getProperty("lable.performnace.nowpingscore"));	    	
		    else if("03".equalsIgnoreCase(scoreType))
		    	nameList.add(ResourceFactory.getProperty("lable.performnace.havepingscore"));	    	
		    		
			// 获得某编号的考核计划的所有信息
			RecordVo planVo=getPlanVo(plan_id);
			
			if("1".equals(selectFashion))
				this.createExcel("1", scoreType, planVo.getString("name"), nameList, sb.getMainbodyMap());			
			else if("2".equals(selectFashion))
				this.createExcel("2", scoreType, planVo.getString("name"), nameList, sb.getObjectMap());
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
									
	}
	public void createExcel(String selectFashion, String scoreType, String planName, ArrayList nameList, HashMap personScoreMap) throws GeneralException
	{
		HSSFWorkbook wb = null;
		try{
			wb =new HSSFWorkbook();
			HSSFSheet sheet=wb.createSheet();
			HSSFFont font2 = wb.createFont();
			font2.setFontHeightInPoints((short)15);
			HSSFFont font3 = wb.createFont();
			font3.setFontHeightInPoints((short)10);
			HSSFCellStyle style2 = wb.createCellStyle();
			HSSFCellStyle style3=wb.createCellStyle();
			HSSFCellStyle style33=wb.createCellStyle();
			HSSFCellStyle stylenum=wb.createCellStyle();
			HSSFCellStyle stylenumg=wb.createCellStyle();
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
			
			style33.setFont(font3);
			style33.setAlignment(HorizontalAlignment.LEFT);
			style33.setVerticalAlignment(VerticalAlignment.CENTER);
			style33.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style33.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);  
			style33.setWrapText(true);
			style33.setBorderBottom(BorderStyle.THIN);
			style33.setBorderLeft(BorderStyle.THIN);
			style33.setBorderRight(BorderStyle.THIN);
			style33.setBorderTop(BorderStyle.THIN);
			style33.setBottomBorderColor((short) 8);
			style33.setLeftBorderColor((short) 8);
			style33.setRightBorderColor((short) 8);
			style33.setTopBorderColor((short) 8);
			
			stylenum.setFont(font3);
			stylenum.setAlignment(HorizontalAlignment.RIGHT);
			stylenum.setVerticalAlignment(VerticalAlignment.CENTER);
			stylenum.setWrapText(true);
			stylenum.setBorderBottom(BorderStyle.THIN);
			stylenum.setBorderLeft(BorderStyle.THIN);
			stylenum.setBorderRight(BorderStyle.THIN);
			stylenum.setBorderTop(BorderStyle.THIN);
			stylenum.setBottomBorderColor((short) 8);
			stylenum.setLeftBorderColor((short) 8);
			stylenum.setRightBorderColor((short) 8);
			stylenum.setTopBorderColor((short) 8);
			
			stylenumg.setFont(font3);
			stylenumg.setAlignment(HorizontalAlignment.RIGHT);
			stylenumg.setVerticalAlignment(VerticalAlignment.CENTER);
			stylenumg.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			stylenumg.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index); 
			stylenumg.setWrapText(true);
			stylenumg.setBorderBottom(BorderStyle.THIN);
			stylenumg.setBorderLeft(BorderStyle.THIN);
			stylenumg.setBorderRight(BorderStyle.THIN);
			stylenumg.setBorderTop(BorderStyle.THIN);
			stylenumg.setBottomBorderColor((short) 8);
			stylenumg.setLeftBorderColor((short) 8);
			stylenumg.setRightBorderColor((short) 8);
			stylenumg.setTopBorderColor((short) 8);
			
			HSSFCellStyle style_title = wb.createCellStyle();
			style_title.setFont(font2);
			style_title.setAlignment(HorizontalAlignment.CENTER);
			style_title.setVerticalAlignment(VerticalAlignment.CENTER);
			HSSFRow row = null;
			HSSFCell cell = null;
			if(selectFashion.endsWith("1"))
			{
				row=sheet.createRow(0);
				row.setHeight((short)700);			
				cell = row.createCell(0);
				cell.setCellValue(planName+ResourceFactory.getProperty("jx.selfScore.markStatusList"));
				cell.setCellStyle(style_title);
				ExportExcelUtil.mergeCell(sheet, 0, (short)0, 0,(short) (nameList.size()-1));
			}else
			{
				row=sheet.createRow(0);
				row.setHeight((short)700);
				cell = row.createCell(0);
				cell.setCellValue(planName+ResourceFactory.getProperty("jx.selfScore.markStatusList"));
				cell.setCellStyle(style_title);
				ExportExcelUtil.mergeCell(sheet, 0, (short)0, 0,(short) (nameList.size()-1));
			}
				
				row=sheet.createRow(1);
				for(int i=0;i<nameList.size();i++)
				{
					sheet.setColumnWidth(i, (short)8000);
					row.setHeight((short)500);
					cell=row.createCell(i);
					cell.setCellValue((String)nameList.get(i));
					cell.setCellStyle(style2);
				}
				
							
				HashMap existWriteB0100=new HashMap();  // 放已画过的单位	 		
		 		Set keySet=personScoreMap.keySet();
				java.util.Iterator t=keySet.iterator();
				int n=2;
				while(t.hasNext())
				{
					String strKey = (String)t.next();  //键值	    
					ArrayList personScoreList = (ArrayList)personScoreMap.get(strKey);   //value值   
								 
			 		for(int i=0;i<personScoreList.size();i++)
			 		{
			 			LazyDynaBean abean=(LazyDynaBean)personScoreList.get(i);
	 					String e0122=(String)abean.get("e0122");
	 					String allScore=(String)abean.get("allScore");
	 					String noScore=(String)abean.get("noScore");
	 					String nowScore=(String)abean.get("nowScore");
	 					String endScore=(String)abean.get("endScore");						
	 					
	 					int m=0;
	 					row=sheet.createRow(n);
	 					row.setHeight((short)500);					
	 					
						if(existWriteB0100.get(strKey)==null)
					    {	
							String strDesc = "";
					    	if("b0110".equalsIgnoreCase(strKey))
					    		strDesc = "总计";
				    		else
				    			strDesc = AdminCode.getCodeName("UN", strKey);
					    	
					    	int num = n;
							if(personScoreList.size()!=0)
								num = (n+personScoreList.size()-1);												
							
							cell=row.createCell(0);
		 					cell.setCellValue(strDesc);
		 					if("unit".equalsIgnoreCase(strKey))
		 						cell.setCellStyle(style33);
				    		else
				    			cell.setCellStyle(style3);	 						 					
		 					ExportExcelUtil.mergeCell(sheet, n, (short)m, num,(short)(m));	
		 					m++;
				    	}else
				    		m++;
						
						existWriteB0100.put(strKey,"1");
												
				  		cell=row.createCell(1);
	 					cell.setCellValue(AdminCode.getCodeName("UM", e0122));					
	 					if((("b0110".equalsIgnoreCase(strKey)) && (e0122==null || e0122.trim().length()<=0)) || (e0122!=null && e0122.trim().length()>0))
	 						cell.setCellStyle(style3);
						else
							cell.setCellStyle(style33);					
	 					ExportExcelUtil.mergeCell(sheet, n, (short)m, n,(short)(m));
	 					m++;	
	 					
				  		cell=row.createCell(2);			  		
				  		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
						cell.setCellValue(new Double(allScore).doubleValue());	
						if((("b0110".equalsIgnoreCase(strKey)) && (e0122==null || e0122.trim().length()<=0)) || (e0122!=null && e0122.trim().length()>0))
	 						cell.setCellStyle(stylenum);
						else
							cell.setCellStyle(stylenumg);	
	 					ExportExcelUtil.mergeCell(sheet, n, (short)m, n,(short)(m));
	 					m++;
	 					
				  		if("all".equalsIgnoreCase(scoreType))
				  		{				  		
					  		cell=row.createCell(3);
					  		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
							cell.setCellValue(new Double(noScore).doubleValue());
//		 					cell.setCellValue(noScore);
							if((("b0110".equalsIgnoreCase(strKey)) && (e0122==null || e0122.trim().length()<=0)) || (e0122!=null && e0122.trim().length()>0))
		 						cell.setCellStyle(stylenum);
							else
								cell.setCellStyle(stylenumg);
		 					ExportExcelUtil.mergeCell(sheet, n, (short)m, n,(short)(m));
		 					m++;
		 					
		 					cell=row.createCell(4);
		 					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
							cell.setCellValue(new Double(nowScore).doubleValue());
//		 					cell.setCellValue(nowScore);
							if((("b0110".equalsIgnoreCase(strKey)) && (e0122==null || e0122.trim().length()<=0)) || (e0122!=null && e0122.trim().length()>0))
		 						cell.setCellStyle(stylenum);
							else
								cell.setCellStyle(stylenumg);
		 					ExportExcelUtil.mergeCell(sheet, n, (short)m, n,(short)(m));
		 					m++;	
		 					
		 					cell=row.createCell(5);
		 					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
							cell.setCellValue(new Double(endScore).doubleValue());
//		 					cell.setCellValue(endScore);
							if((("b0110".equalsIgnoreCase(strKey)) && (e0122==null || e0122.trim().length()<=0)) || (e0122!=null && e0122.trim().length()>0))
		 						cell.setCellStyle(stylenum);
							else
								cell.setCellStyle(stylenumg);
		 					ExportExcelUtil.mergeCell(sheet, n, (short)m, n,(short)(m));				  						  		
		 					m++;
		 					
				  		}else if("01".equalsIgnoreCase(scoreType))
				  		{
				  			cell=row.createCell(3);
				  			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
							cell.setCellValue(new Double(noScore).doubleValue());
//		 					cell.setCellValue(noScore);
							if((("b0110".equalsIgnoreCase(strKey)) && (e0122==null || e0122.trim().length()<=0)) || (e0122!=null && e0122.trim().length()>0))
		 						cell.setCellStyle(stylenum);
							else
								cell.setCellStyle(stylenumg);
		 					ExportExcelUtil.mergeCell(sheet, n, (short)m, n,(short)(m));
		 					m++;
					  		
				  		}else if("02".equalsIgnoreCase(scoreType))
				  		{
				  			cell=row.createCell(3);
				  			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
							cell.setCellValue(new Double(nowScore).doubleValue());
//		 					cell.setCellValue(nowScore);
							if((("b0110".equalsIgnoreCase(strKey)) && (e0122==null || e0122.trim().length()<=0)) || (e0122!=null && e0122.trim().length()>0))
		 						cell.setCellStyle(stylenum);
							else
								cell.setCellStyle(stylenumg);
		 					ExportExcelUtil.mergeCell(sheet, n, (short)m, n,(short)(m));
		 					m++;	
					  		
				  		}else if("03".equalsIgnoreCase(scoreType))
				  		{
				  			cell=row.createCell(3);
				  			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
							cell.setCellValue(new Double(endScore).doubleValue());
//		 					cell.setCellValue(endScore);
							if((("b0110".equalsIgnoreCase(strKey)) && (e0122==null || e0122.trim().length()<=0)) || (e0122!=null && e0122.trim().length()>0))
		 						cell.setCellStyle(stylenum);
							else
								cell.setCellStyle(stylenumg);
		 					ExportExcelUtil.mergeCell(sheet, n, (short)m, n,(short)(m));				  						  		
		 					m++;	
				  		}
				  		
				  		n++;
				 	} 			 				 						 	
		 		}
				
				String outName="";
				FileOutputStream fileOut = null;
				try 
				{
					if(selectFashion.endsWith("1"))
					{
						outName=planName+ResourceFactory.getProperty("jx.selfScore.markStatusList")+PubFunc.getStrg()+".xls";
					}else
						outName=planName+ResourceFactory.getProperty("jx.selfScore.markStatusList")+PubFunc.getStrg()+".xls";
					
					fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator")+outName);
					wb.write(fileOut);
					fileOut.close();
					
				} catch (Exception e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					PubFunc.closeResource(fileOut);
				}
				outName = PubFunc.encrypt(outName);
				//20/3/6 xus vfs改造
//				outName = SafeCode.encode(outName);
				sheet = null;
				this.getFormHM().put("name", outName);
			
		}catch(Exception e){
			e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeResource(wb);
		}
		
	}
	/**
	 * plan_id 计划号
	 * 获得某编号的考核计划的所有信息
	 * @throws GeneralException 
	 */
	public RecordVo getPlanVo(String plan_id) throws GeneralException
	{
		RecordVo vo=new RecordVo("per_plan");
		try
		{	if("".equals(plan_id))
			throw GeneralExceptionHandler.Handle(new Exception("未找到对应记录!"));
			vo.setInt("plan_id",Integer.parseInt(plan_id));
			ContentDAO dao = new ContentDAO(this.frameconn);
			vo=dao.findByPrimaryKey(vo);
			if(vo.getInt("method")==0)
				vo.setInt("method",1);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		}
		return vo;
	}
	/**
	 * 画excel的格子（合并单元格）
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @param content
	 * @param aStyle
	 * @throws GeneralException 
	 */
	public void executeCell(int a, short b, int c, short d, HSSFSheet sheet, HSSFCellStyle aStyle, String content, String type) throws GeneralException 
	{	try{
		
			HSSFComment comm = null;
			HSSFRow row = sheet.getRow(a);
			if(row==null)
				row = sheet.createRow(a);
			
			HSSFCell cell = row.getCell(b);
			if(cell==null)
				cell = row.createCell(b);
			
			if("num".equalsIgnoreCase(type)&& (content!=null&&content.length()>0))
			{
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				cell.setCellValue(new Double(content).doubleValue());
			}else
			{
				cell.setCellValue(new HSSFRichTextString(content));
			}
			cell.setCellStyle(aStyle);
					
			short b1 = b;
			while (++b1 <= d) 
			{
				cell = row.getCell(b1);
				if(cell==null)
					cell = row.createCell(b1);
				cell.setCellStyle(aStyle);
			}
			for (int a1 = a + 1; a1 <= c; a1++) 
			{
				row = sheet.getRow(a1);
				if(row==null)
					row = sheet.createRow(a1);
				b1 = b;
				while (b1 <= d)
				{
					cell = row.getCell(b1);
					if(cell==null)
						cell = row.createCell(b1);
					cell.setCellStyle(aStyle);
					b1++;
				}
			}
	
			ExportExcelUtil.mergeCell(sheet, a, b, c, d);

		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
