package com.hjsj.hrms.transaction.performance.implement.performanceImplement;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
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

/**
 * <p>Title:ClearMainBodyTrans.java</p>
 * <p>Description:考核实施/导出excel</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-04-08 13:00:00</p>
 * @author JinChunhai
 * @version 1.0
 */

public class ExportKhObjsTrans extends IBusiness
{

	public void execute() throws GeneralException
	{
		String code = (String) this.getFormHM().get("code");
		String codeset = (String) this.getFormHM().get("codeset");
		String planid = (String) this.getFormHM().get("plan_id");
		CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
        if(!_bo.isHavePriv(this.userView, planid)){	
        	return;
        } 
		String orderSql = (String) this.getFormHM().get("orderSql");
		String isDistribute = (String) this.getFormHM().get("isDistribute");
		String queryA0100=(String)this.getFormHM().get("queryA0100");
		queryA0100 = SafeCode.decode(queryA0100);
		orderSql = SafeCode.decode(orderSql);
		
		PerformanceImplementBo pb = new PerformanceImplementBo(this.getFrameconn());
		String whl = pb.getPrivWhere(this.userView);//根据用户权限先得到一个考核对象的范围
		if(code!=null && !"-1".equals(code))
		{
			if(AdminCode.getCodeName("UN",code)!=null&&AdminCode.getCodeName("UN",code).length()>0)
				whl+=" and b0110 like '"+code+"%'";
			else if(AdminCode.getCodeName("UM",code)!=null&&AdminCode.getCodeName("UM",code).length()>0)
				whl+=" and e0122 like '"+code+"%'";
			
		}		
		
		RecordVo vo = pb.getPerPlanVo(planid);
		String object_type = String.valueOf(vo.getInt("object_type"));
		String plan_name = vo.getString("name");
		String method = String.valueOf(vo.getInt("method"));

		HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
		HSSFSheet sheet = wb.createSheet();

		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 10);

		HSSFCellStyle style1 = dataStyle(wb);
		style1.setAlignment(HorizontalAlignment.CENTER);
		style1.setFont(font);

		HSSFCellStyle style2 = dataStyle(wb);
		style2.setAlignment(HorizontalAlignment.LEFT);
		style2.setFont(font);

		HSSFFont fontStyle = wb.createFont();
		fontStyle.setFontName("宋体");
		fontStyle.setFontHeightInPoints((short) 15);
		fontStyle.setBold(true);

		HSSFCellStyle style3 = dataStyle(wb);
		style3.setAlignment(HorizontalAlignment.CENTER);
		style3.setFont(fontStyle);
		
		HSSFCellStyle style4 = dataStyle(wb);
		style4.setAlignment(HorizontalAlignment.CENTER);
		style4.setFont(font);
		style4.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style4.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);		
		
		
		boolean isKHRela = false;	//是否显示考核关系
		if ("2".equals(method) && ("2".equals(object_type) || (!"2".equals(object_type) && "1".equals(isDistribute))))
			isKHRela = true;

		HSSFRow row = sheet.createRow(0);
		row.setHeight((short) (15.625 * 40));
		HSSFCell cell;

		cell = row.createCell(0);
		cell.setCellValue(cellStr(plan_name + "-考核对象"));
		cell.setCellStyle(style3);

		int i = 0;
		if ("2".equals(object_type))// 人员
		{
			row = sheet.createRow(1);
			
			sheet.setColumnWidth(i, 1500);
			cell = row.createCell(i++);
			cell.setCellValue(cellStr("序号"));
			cell.setCellStyle(style4);

			sheet.setColumnWidth(i, 5000);
			cell = row.createCell(i++);
			cell.setCellValue(cellStr(ResourceFactory.getProperty("b0110.label")));
			cell.setCellStyle(style4);

			sheet.setColumnWidth(i, 5000);
			cell = row.createCell(i++);
			FieldItem fielditem = DataDictionary.getFieldItem("E0122");         		         		 	
			cell.setCellValue(cellStr(fielditem.getItemdesc()));
			cell.setCellStyle(style4);

			sheet.setColumnWidth(i, 5000);
			cell = row.createCell(i++);
			cell.setCellValue(cellStr(ResourceFactory.getProperty("e01a1.label")));
			cell.setCellStyle(style4);

			sheet.setColumnWidth(i, 4000);
			cell = row.createCell(i++);
			cell.setCellValue(cellStr(ResourceFactory.getProperty("hire.employActualize.name")));
			cell.setCellStyle(style4);

			sheet.setColumnWidth(i, 4000);
			cell = row.createCell(i++);
			cell.setCellValue(cellStr(ResourceFactory.getProperty("performance.implement.objecttype")));
			cell.setCellStyle(style4);

		} else if ("1".equals(object_type) || "3".equals(object_type) || "4".equals(object_type))// 团队 单位 部门
		{
			row = sheet.createRow(1);
			sheet.setColumnWidth(i, 1500);
			cell = row.createCell(i++);
			cell.setCellValue(cellStr("序号"));
			cell.setCellStyle(style4);

			sheet.setColumnWidth(i, 5000);
			cell = row.createCell(i++);
			cell.setCellValue(cellStr(ResourceFactory.getProperty("b0110.label")));
			cell.setCellStyle(style4);

			sheet.setColumnWidth(i, 5000);
			cell = row.createCell(i++);
			cell.setCellValue(cellStr(ResourceFactory.getProperty("org.performance.unorum")));
			cell.setCellStyle(style4);

			sheet.setColumnWidth(i, 4000);
			cell = row.createCell(i++);
			cell.setCellValue(cellStr(ResourceFactory.getProperty("performance.implement.objecttype")));
			cell.setCellStyle(style4);

		}
		if (isKHRela)
		{
			sheet.setColumnWidth(i, 4000);
			cell = row.createCell(i++);
			cell.setCellValue(cellStr(ResourceFactory.getProperty("performance.relation")));
			cell.setCellStyle(style4);
		}

		int rowNum = 2;
		ExportExcelUtil.mergeCell(sheet, 0, 0, 0, --i);
		ArrayList perObjectDataList = pb.getPerObjectDataList(planid, object_type, whl, orderSql,queryA0100);
		for (int j = 0; j < perObjectDataList.size(); j++)
		{
			LazyDynaBean abean = (LazyDynaBean) perObjectDataList.get(j);
			String a0101 = (String) abean.get("a0101");
			String b0110 = (String) abean.get("b0110");
			String e0122 = (String) abean.get("e0122");
			String e01a1 = (String) abean.get("e01a1");
			String objectTypeName = (String) abean.get("objectTypeName");
			String kh_relations_name = (String) abean.get("kh_relations_name");
			row = sheet.createRow(rowNum);
			i = 0;
			if ("2".equals(object_type))// 人员
			{
				cell = row.createCell(i++);
				cell.setCellValue(cellStr(Integer.toString(rowNum - 1)));
				cell.setCellStyle(style1);

				cell = row.createCell(i++);
				cell.setCellValue(cellStr(b0110));
				cell.setCellStyle(style2);

				cell = row.createCell(i++);
				cell.setCellValue(cellStr(e0122));
				cell.setCellStyle(style2);

				cell = row.createCell(i++);
				cell.setCellValue(cellStr(e01a1));
				cell.setCellStyle(style2);

				cell = row.createCell(i++);
				cell.setCellValue(cellStr(a0101));
				cell.setCellStyle(style2);

				cell = row.createCell(i++);
				cell.setCellValue(cellStr(objectTypeName));
				cell.setCellStyle(style2);

			} else if ("1".equals(object_type) || "3".equals(object_type) || "4".equals(object_type))// 团队 单位 部门
			{
				cell = row.createCell(i++);
				cell.setCellValue(cellStr(Integer.toString(rowNum - 1)));
				cell.setCellStyle(style1);

				cell = row.createCell(i++);
				cell.setCellValue(cellStr(b0110));
				cell.setCellStyle(style2);

				cell = row.createCell(i++);
				cell.setCellValue(cellStr(a0101));
				cell.setCellStyle(style2);

				cell = row.createCell(i++);
				cell.setCellValue(cellStr(objectTypeName));
				cell.setCellStyle(style2);

			}
			if (isKHRela)
			{
				cell = row.createCell(i++);
				cell.setCellValue(cellStr(kh_relations_name));
				cell.setCellStyle(style2);
			}
			rowNum++;
		}
		
		String outName = planid+"号计划_"+this.userView.getUserName() +".xls";
		FileOutputStream fileOut = null;
		try
		{
		    fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
		    wb.write(fileOut);
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeResource(fileOut);
		}
//		outName = outName.replace(".xls", "#");
		outName = PubFunc.encrypt(outName);
		outName = SafeCode.encode(outName);

		this.getFormHM().put("outName", outName);
		sheet = null;
		wb = null;	
		
	}

	public HSSFCellStyle dataStyle(HSSFWorkbook workbook)
	{

		HSSFCellStyle style = workbook.createCellStyle();
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBottomBorderColor((short) 8);
		style.setLeftBorderColor((short) 8);
		style.setRightBorderColor((short) 8);
		style.setTopBorderColor((short) 8);
		style.setWrapText(true);
		return style;
	}

	public HSSFRichTextString cellStr(String context)
	{

		HSSFRichTextString textstr = new HSSFRichTextString(context);
		return textstr;
	}
}
