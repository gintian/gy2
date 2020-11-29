package com.hjsj.hrms.transaction.performance.achivement.kpiOriginalData;

import com.hjsj.hrms.businessobject.performance.achivement.kpiOriginalData.KpiOriginalDataBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.DataDictionary;
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
 * <p>Title:DownKpiDataTemplateTrans.java</p>
 * <p>Description:KPI原始数据下载模板</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-09-20 11:11:11</p>
 * @author JinChunhai
 * @version 5.0
 */

public class DownKpiDataTemplateTrans extends IBusiness 
{
	private HSSFSheet sheet = null;
	
	public void execute() throws GeneralException 
	{
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd"); // 获得系统当前时间
		
		// 查询的参数
		String onlyFild = (String)this.getFormHM().get("onlyFild"); // 唯一性指标
		String refreshKey = (String)this.getFormHM().get("refreshKey");
		String cycle = (String)this.getFormHM().get("cycle");	// 考核周期	
		String noYearCycle = (String)this.getFormHM().get("noYearCycle");	// 非年度考核周期	
		String objectType = (String) this.getFormHM().get("objectType"); // 对象类别：1 单位 2 人员
		String year = (String) this.getFormHM().get("year");
		String unionOrgCode = (String)this.getFormHM().get("unionOrgCode");	
		
		if(cycle==null || cycle.trim().length()<=0 || "-1".equalsIgnoreCase(cycle))
			cycle = "0";	
		if((noYearCycle==null || noYearCycle.trim().length()<=0) || (refreshKey!=null && refreshKey.trim().length()>0 && "changeCycle".equalsIgnoreCase(refreshKey)) )
			noYearCycle = "01";
		if("0".equalsIgnoreCase(cycle))
			noYearCycle = "";		
		if(year==null || year.trim().length()<=0)
			year = creatDate.substring(0, 4);
		
		String checkName = (String)this.getFormHM().get("checkName");				
		if(checkName.indexOf("'")!=-1)				
			checkName = checkName.replaceAll("'","‘");  
		
		KpiOriginalDataBo bo = new KpiOriginalDataBo(this.getFrameconn(),this.userView);							
	    ArrayList setlist = bo.searchKpiOriginalData(cycle,objectType,year,noYearCycle,checkName,unionOrgCode);
	   		    
//	    this.getFormHM().put("setlist", setlist.get(0));
//	    this.getFormHM().put("object_ids", setlist.get(1));
				
		ArrayList nameList=new ArrayList();
		nameList.add(ResourceFactory.getProperty("kpi.originalData.targetXuhao")+":xuhao");
		nameList.add(ResourceFactory.getProperty("kpi.originalData.businessTime")+":businessTime");
		nameList.add(ResourceFactory.getProperty("org.performance.unorum")+":unorum");
		
		if("2".equalsIgnoreCase(objectType))
			nameList.add(ResourceFactory.getProperty("hire.employActualize.name")+":name");	        	
		
		nameList.add(DataDictionary.getFieldItem(onlyFild).getItemdesc()+":onlyFild");
		nameList.add(ResourceFactory.getProperty("kpi.originalData.KpiTarget")+":kpiTarget");	  
		nameList.add(ResourceFactory.getProperty("kpi.originalData.targetDescription")+":targetDescription");	  
		nameList.add(ResourceFactory.getProperty("kpi.originalData.targetScore")+":targetScore");	  
		nameList.add(ResourceFactory.getProperty("kpi.originalData.targetStatus")+":targetStatus");	  
				
				
		this.createExcel(objectType, nameList, setlist);						
								
	}
	public void createExcel(String objectType, ArrayList nameList, ArrayList reverseResultList)
	{
		HSSFWorkbook wb =new HSSFWorkbook(); 
		sheet=wb.createSheet();
		
		/**EXCEL样式*/
		HSSFCellStyle style2 = style(wb, 1);  
		HSSFCellStyle style3 = style(wb, 2);  
		HSSFCellStyle style4 = style(wb, 3); 
		HSSFCellStyle stylenum = style(wb, 4); 
		
		HSSFPatriarch patr = sheet.createDrawingPatriarch();
		HSSFComment comm = null;
		HSSFRow row = null;
		HSSFCell cell = null;		
		
		// 头
		row=sheet.createRow(0);
		for(int i=0;i<nameList.size();i++)
		{
			String titleName = (String)nameList.get(i);
			
			if(i==0)
			{
				sheet.setColumnWidth(i, (short)0);
				row.setHeight((short)500);
			}else
			{
				sheet.setColumnWidth(i, (short)6000);
				row.setHeight((short)500);	
			}					
			executeCell(0,(short)i,0,(short)i,titleName.substring(0,titleName.indexOf(":")),style2,"str",patr,"yes",titleName.substring(titleName.indexOf(":")+1));
		}
		  
		// 体
		ArrayList kpiOriginalDataList = new ArrayList();
		if(reverseResultList!=null && reverseResultList.size()>0)	    
			kpiOriginalDataList = (ArrayList)reverseResultList.get(0);
		
		String theyear = "";
		for(int i=0;i<kpiOriginalDataList.size();i++)
		{
		 	LazyDynaBean abean=(LazyDynaBean)kpiOriginalDataList.get(i);
		 			
 			String a0101 = "";			
 			String id=(String)abean.get("id");
 			String theTime=(String)abean.get("theTime");
 			theyear=(String)abean.get("theyear");
 			String object_id=(String)abean.get("object_id");
 			String codeitemdesc=(String)abean.get("codeitemdesc");
 			
		 	if("2".equalsIgnoreCase(objectType))
		 		a0101=(String)abean.get("a0101");						
		 	
		 	String item_id=(String)abean.get("item_id");
		 	String itemdesc=(String)abean.get("itemdesc");
			String description=(String)abean.get("description");
			String actual_value=(String)abean.get("actual_value");
			String status=(String)abean.get("status");
			String type = "起草";
			if("01".equalsIgnoreCase(status))
				type = "起草";
			else
				type = "生效";
					
 			int m=0;
 			row=sheet.createRow(i+1);
 			row.setHeight((short)500);					 			

 			executeCell(i+1,(short)0,i+1,(short)0,id,style3,"str",patr,"yes",id);		
 			
 			executeCell(i+1,(short)1,i+1,(short)1,theyear,style3,"str",patr,"no","no");						
			
 			if("2".equalsIgnoreCase(objectType))
			{	
 				executeCell(i+1,(short)2,i+1,(short)2,codeitemdesc,style3,"str",patr,"no","no");
 				
 				executeCell(i+1,(short)3,i+1,(short)3,a0101,style3,"str",patr,"no","no");
				
 				executeCell(i+1,(short)4,i+1,(short)4,object_id,style3,"str",patr,"no","no");
 				
 				executeCell(i+1,(short)5,i+1,(short)5,itemdesc,style3,"str",patr,"no","no");

 				executeCell(i+1,(short)6,i+1,(short)6,description,style3,"str",patr,"no","no");
				
 				executeCell(i+1,(short)7,i+1,(short)7,actual_value,stylenum,"num",patr,"no","no");
				
 				executeCell(i+1,(short)8,i+1,(short)8,type,style4,"str",patr,"no","no");
			}
			else
			{
				executeCell(i+1,(short)2,i+1,(short)2,codeitemdesc,style3,"str",patr,"no","no");
				
				executeCell(i+1,(short)3,i+1,(short)3,object_id,style3,"str",patr,"no","no");
				
				executeCell(i+1,(short)4,i+1,(short)4,itemdesc,style3,"str",patr,"no","no");

				executeCell(i+1,(short)5,i+1,(short)5,description,style3,"str",patr,"no","no");
				
				executeCell(i+1,(short)6,i+1,(short)6,actual_value,stylenum,"num",patr,"no","no");
				
				executeCell(i+1,(short)7,i+1,(short)7,type,style4,"str",patr,"no","no");
											
			}
		}
			
			String outName="";
			FileOutputStream fileOut = null;
			try 
			{
				String name = theyear;				
				if("2".equalsIgnoreCase(objectType))
					name += "人员";//去掉人员前面的空格，有空格时火狐浏览器下载有问题 bug 35009   by haosl  2018-2-27
				else 
					name += "单位或部门";				
				
				outName=name+"的KPI原始数据"+PubFunc.getStrg()+".xls ";
				fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator")+outName);
				wb.write(fileOut);
				fileOut.close();
				
			} catch (Exception e) 
			{
				e.printStackTrace();
			}finally {
				PubFunc.closeResource(fileOut);
			}
			
			sheet = null;
			wb = null;
			
			outName = PubFunc.encrypt(outName);
			//20/3/6 xus vfs改造
//			outName = SafeCode.encode(outName);
			this.getFormHM().put("name", outName);		
		
	}
	
	/**
	 * 画excel的格子（合并单元格）
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @param content
	 * @param aStyle
	 */
	public void executeCell(int a, short b, int c, short d, String content,HSSFCellStyle aStyle,String type,HSSFPatriarch patr,String xorzb,String comment) 
	{		
		try {
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
			
			if("yes".equalsIgnoreCase(xorzb))
			{
				comm = patr.createComment(new HSSFClientAnchor(a, 0, a, 1, (short)(b+1), a, (short)(b+2), c+1));
				comm.setString(new HSSFRichTextString(comment));
				cell.setCellComment(comm);
			}
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
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 设置excel的样式
	 * @param workbook
	 * @param styles
	 * @return
	 */
	public HSSFCellStyle style(HSSFWorkbook workbook, int styles)
	{		
		HSSFCellStyle style = workbook.createCellStyle();
		
		switch (styles)
		{
		
			case 0:
			    HSSFFont fonttitle = fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.black.font"), 10);
			    fonttitle.setBold(false);// 加粗
			    style.setFont(fonttitle);
			    style.setBorderBottom(BorderStyle.THIN);
			    style.setBorderLeft(BorderStyle.THIN);
			    style.setBorderRight(BorderStyle.THIN);
			    style.setBorderTop(BorderStyle.THIN);
			    style.setVerticalAlignment(VerticalAlignment.CENTER);
			    style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
			    style.setAlignment(HorizontalAlignment.CENTER);
			    break;
			case 1:
			    style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 13));
			    style.setAlignment(HorizontalAlignment.CENTER);
			    style.setVerticalAlignment(VerticalAlignment.CENTER);
			    style.setWrapText(true);
			    style.setBorderBottom(BorderStyle.THIN);
			    style.setBorderLeft(BorderStyle.THIN);
			    style.setBorderRight(BorderStyle.THIN);
			    style.setBorderTop(BorderStyle.THIN);

			    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			    style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index); 
			    break;
			case 2:
				style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));
				style.setAlignment(HorizontalAlignment.LEFT);
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				style.setWrapText(true);
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);				
			    break;
			case 3:
				style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));
				style.setAlignment(HorizontalAlignment.CENTER);
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				style.setWrapText(true);
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);							
			    break;
			case 4:
				style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));
				style.setAlignment(HorizontalAlignment.RIGHT);
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				style.setWrapText(true);
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);			
			    break;			
			default:
			    style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 12));
			    style.setAlignment(HorizontalAlignment.LEFT);
			    style.setBorderBottom(BorderStyle.THIN);
			    style.setBorderLeft(BorderStyle.THIN);
			    style.setBorderRight(BorderStyle.THIN);
			    style.setBorderTop(BorderStyle.THIN);
			    break;
		}
		style.setWrapText(true);
		return style;
	}
	/**
	 * 设置excel的字体
	 * @param workbook
	 * @param fonts
	 * @param size
	 * @return
	 */
	public HSSFFont fonts(HSSFWorkbook workbook, String fonts, int size)
	{
	
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) size);
		font.setFontName(fonts);
		return font;
	}
	
}
