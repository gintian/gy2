package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCardMonitor;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectiveCardTypeBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.utility.AdminCode;
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
 * <p>Title:ExportMBOTrans.java</p>
 * <p>Description:导出Excel</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-04-26 14:15:22</p>
 * @author JinChunhai
 * @version 1.0
 */


public class ExportMBOTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		String itemid=(String)this.getFormHM().get("itemid");		
		String entry=(String)this.getFormHM().get("entry");
		String whl=(String)this.getFormHM().get("whl");
		whl = PubFunc.keyWord_reback(whl);
		
		ObjectiveCardTypeBo bo = new ObjectiveCardTypeBo(this.getFrameconn(),this.getUserView());
		if("2".equals(entry))
		{
			
			ArrayList mboTableList=bo.getMBOTableList(itemid,whl); 
			ArrayList nameList=new ArrayList();
			nameList.add(ResourceFactory.getProperty("tree.unroot.undesc"));
			nameList.add(ResourceFactory.getProperty("lable.performance.perPlan"));
			nameList.add(ResourceFactory.getProperty("lable.performance.persionNumList"));
			nameList.add(ResourceFactory.getProperty("label.hiremanage.status1"));
			nameList.add(ResourceFactory.getProperty("performance.spflag.ybp"));
			nameList.add(ResourceFactory.getProperty("performance.spflag.ybl"));
			this.createExcel("2", nameList, mboTableList);
			
		}else if("3".equals(entry))
		{
			
			ArrayList mboTableList=bo.getMBOScoreList(itemid, whl);
			ArrayList nameList=new ArrayList();
			nameList.add(ResourceFactory.getProperty("tree.unroot.undesc"));
			nameList.add(ResourceFactory.getProperty("lable.performance.perPlan"));
			nameList.add(ResourceFactory.getProperty("lable.performance.persionNumList"));
			nameList.add(ResourceFactory.getProperty("lable.performnace.wpf"));
			nameList.add(ResourceFactory.getProperty("lable.performnace.nowpingscore"));
			nameList.add(ResourceFactory.getProperty("lable.performnace.havepingscore"));
			this.createExcel("3", nameList, mboTableList);
			
		}
	}
	public void createExcel(String entry,ArrayList nameList,ArrayList dataList)
	{
		String outName="";
		FileOutputStream fileOut = null;
		HSSFWorkbook wb = null;
		try 
		{
			wb =new HSSFWorkbook();
			HSSFSheet sheet=wb.createSheet();
			HSSFFont font2 = wb.createFont();
			font2.setFontHeightInPoints((short)15);
			HSSFFont font3 = wb.createFont();
			font3.setFontHeightInPoints((short)10);
			HSSFCellStyle style2 = wb.createCellStyle();
			HSSFCellStyle style3=wb.createCellStyle();
			HSSFCellStyle stylenum=wb.createCellStyle();
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
			
			stylenum.setFont(font3);
			stylenum.setAlignment(HorizontalAlignment.RIGHT);
			stylenum.setVerticalAlignment(VerticalAlignment.CENTER);
			
			HSSFCellStyle style_title = wb.createCellStyle();
			style_title.setFont(font2);
			style_title.setAlignment(HorizontalAlignment.CENTER);
			style_title.setVerticalAlignment(VerticalAlignment.CENTER);
			HSSFRow row = null;
			HSSFCell cell = null;
			if(entry.endsWith("2"))
			{
				row=sheet.createRow(0);
				row.setHeight((short)700);			
				cell = row.createCell(0);
				cell.setCellValue("MBO目标设定及审批进度统计表 ");
				cell.setCellStyle(style_title);
				ExportExcelUtil.mergeCell(sheet, 0, (short)0, 0,(short) 5);
			}else
			{
				row=sheet.createRow(0);
				row.setHeight((short)700);
				cell = row.createCell(0);
				cell.setCellValue("MBO目标总结考评进度统计表 ");
				cell.setCellStyle(style_title);
				ExportExcelUtil.mergeCell(sheet, 0, (short)0, 0,(short) 5);
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
			for(int i=0;i<dataList.size();i++)
			{
				row=sheet.createRow(i+2);
				row.setHeight((short)500);
				LazyDynaBean bean=(LazyDynaBean)dataList.get(i);
				String b0110=(String)bean.get("b0110");
				String units="";
				if("HJSJ".equalsIgnoreCase(b0110))
				{
					units=ResourceFactory.getProperty("jx.khplan.hjsj");
				}else
				{
					if(AdminCode.getCode("UN",b0110)!=null){
						units=AdminCode.getCode("UN",b0110).getCodename().toString();
					}else
					{
						if(AdminCode.getCode("UM",b0110)!=null)
						{
							units=AdminCode.getCode("UM",b0110).getCodename().toString();
						}else
						{
							if(AdminCode.getCode("@k",b0110)!=null)
								units=AdminCode.getCode("@k",b0110).getCodename().toString();
						}
					}
				}
				cell=row.createCell(0);
				cell.setCellValue(units);
				cell.setCellStyle(style3);
				cell=row.createCell(1);
				String plan_name=(String)bean.get("plan_name");
				cell.setCellValue(plan_name);
				cell.setCellStyle(style3);
				ArrayList numlist=(ArrayList)bean.get("numList");
				for(int k=0;k<numlist.size();k++)
				{
					LazyDynaBean abean=(LazyDynaBean)numlist.get(k);
					cell=row.createCell(k+2);
//					cell.setCellValue((String)abean.get("num"));
					
					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
					cell.setCellValue(new Double((String)abean.get("num")).doubleValue());					
					cell.setCellStyle(stylenum);
				}
			}
			if(entry.endsWith("2"))
			{
				outName="MBO目标设定及审批进度统计表"+PubFunc.getStrg()+".xls ";
			}else
				outName="MBO目标总结考评进度统计表"+PubFunc.getStrg()+".xls ";
			
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator")+outName);
			wb.write(fileOut);
			fileOut.close();
			sheet = null;
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(wb);
		}
		
		outName = PubFunc.encrypt(outName);
		//xus 20/4/28 vfs改造
//		outName = SafeCode.encode(outName);
		this.getFormHM().put("name", outName);		
		
		
	}
}
