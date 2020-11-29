package com.hjsj.hrms.transaction.performance.achivement.dataCollection;

import com.hjsj.hrms.businessobject.performance.achivement.dataCollection.DataCollectBo;
import com.hjsj.hrms.businessobject.performance.options.ConfigParamBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * <p>Title:ExportExcelTrans.java</p>
 * <p>Description:业绩数据采集目标跟踪下载模板</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-11-11 13:00:00</p>
 * @author JinChunhai
 * @version 1.0
 */

public class ExportTargetTemplateTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {

		String planID = (String) this.getFormHM().get("planID");
		String sql = (String) this.getFormHM().get("sql");
		sql = SafeCode.decode(sql);
		sql = PubFunc.keyWord_reback(sql);
		
		if(sql.toLowerCase().indexOf("order by")!=-1)
			sql=sql.substring(0,sql.indexOf("order by"));
		
		DataCollectBo bo = new DataCollectBo(this.getFrameconn(), planID, this.userView);
		RecordVo vo = bo.getPlanVo();
		String object_type = vo.getString("object_type");
		String planName = bo.getPlanCnName();
		
		sql+=" order by b0110";
		if ("2".equals(object_type))
		    sql+=",a0101";
		
		String targetTraceItem = "";
		String targetCollectItem = "";
	
		// 取得目标跟踪显示和采集指标
		// 1.取对应于考核计划的参数设置中定义的 目标跟踪显示和采集指标
		LoadXml parameter_content = new LoadXml(this.getFrameconn(), planID);
		Hashtable params = parameter_content.getDegreeWhole();
		String targetTraceEnabled = (String) params.get("TargetTraceEnabled");
		if ("true".equals(targetTraceEnabled))
		{
		    targetTraceItem = (String) params.get("TargetTraceItem");
		    targetCollectItem = (String) params.get("TargetCollectItem");
		} else
		// 2.从绩效模块参数配置中取目标跟踪显示和采集指标
		{
		    ConfigParamBo configParamBo = new ConfigParamBo(this.getFrameconn());
		    targetTraceItem = configParamBo.getTargetTraceItem();
		    targetCollectItem = configParamBo.getTargetCollectItem();
	//	    targetCollectItem = "P0419,P04A3,P04A1,P04A2";
		}
	
		ArrayList fieldList = new ArrayList();// 变动的字段
		ArrayList list = DataDictionary.getFieldList("P04", Constant.USED_FIELD_SET);
		for (int i = 0; i < list.size(); i++)
		{
		    FieldItem item = (FieldItem) list.get(i);
		    String itemid = item.getItemid();
		    if (targetCollectItem.toLowerCase().indexOf(itemid.toLowerCase()) != -1)
			fieldList.add(item);
		}
	

		FileOutputStream fileOut = null;
		HSSFWorkbook wb = null;
		try
		{
			wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
			HSSFSheet sheet = wb.createSheet();
			// sheet.setProtect(true);
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
			style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

			HSSFCellStyle style1 = wb.createCellStyle();
			style1.setFont(font2);
			style1.setAlignment(HorizontalAlignment.LEFT);
			style1.setVerticalAlignment(VerticalAlignment.CENTER);
			style1.setWrapText(true);
			style1.setBorderBottom(BorderStyle.THIN);
			style1.setBorderLeft(BorderStyle.THIN);
			style1.setBorderRight(BorderStyle.THIN);
			style1.setBorderTop(BorderStyle.THIN);
			style1.setBottomBorderColor((short) 8);
			style1.setLeftBorderColor((short) 8);
			style1.setRightBorderColor((short) 8);
			style1.setTopBorderColor((short) 8);
			style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式

			HSSFCellStyle styleN = dataStyle(wb);
			styleN.setAlignment(HorizontalAlignment.RIGHT);
			styleN.setWrapText(true);
			HSSFDataFormat df = wb.createDataFormat();
			styleN.setDataFormat(df.getFormat(decimalwidth(0)));

			HSSFCellStyle styleF1 = dataStyle(wb);
			styleF1.setAlignment(HorizontalAlignment.RIGHT);
			styleF1.setWrapText(true);
			HSSFDataFormat df1 = wb.createDataFormat();
			styleF1.setDataFormat(df1.getFormat(decimalwidth(1)));

			HSSFCellStyle styleF2 = dataStyle(wb);
			styleF2.setAlignment(HorizontalAlignment.RIGHT);
			styleF2.setWrapText(true);
			HSSFDataFormat df2 = wb.createDataFormat();
			styleF2.setDataFormat(df2.getFormat(decimalwidth(2)));

			HSSFCellStyle styleF3 = dataStyle(wb);
			styleF3.setAlignment(HorizontalAlignment.RIGHT);
			styleF3.setWrapText(true);
			HSSFDataFormat df3 = wb.createDataFormat();
			styleF3.setDataFormat(df3.getFormat(decimalwidth(3)));

			HSSFCellStyle styleF4 = dataStyle(wb);
			styleF4.setAlignment(HorizontalAlignment.RIGHT);
			styleF4.setWrapText(true);
			HSSFDataFormat df4 = wb.createDataFormat();
			styleF4.setDataFormat(df4.getFormat(decimalwidth(4)));

			HSSFCellStyle styleF5 = dataStyle(wb);
			styleF5.setAlignment(HorizontalAlignment.RIGHT);
			styleF5.setWrapText(true);
			HSSFDataFormat df5 = wb.createDataFormat();
			styleF5.setDataFormat(df5.getFormat(decimalwidth(5)));

			sheet.setColumnWidth((short) 0, (short) 0);
			sheet.setColumnWidth((short) 1, (short) 4000);
			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = row.createCell((short) 0);

			cell.setCellValue(cellStr("主键标识串"));
			cell.setCellStyle(style2);



			int i = 1;
			if ("2".equals(object_type))// 人员
			{
				sheet.setColumnWidth((short) i, (short) 4000);
				cell = row.createCell((short) i++);

				cell.setCellValue(cellStr("单位名称"));
				cell.setCellStyle(style2);

				sheet.setColumnWidth((short) i, (short) 4000);
				cell = row.createCell((short) i++);
				FieldItem fielditem = DataDictionary.getFieldItem("E0122");
				cell.setCellValue(cellStr(fielditem.getItemdesc()));
				cell.setCellStyle(style2);
			}

			cell = row.createCell((short) 3);
			cell.setCellValue(cellStr("考核对象"));
			cell.setCellStyle(style2);

			i=4;
			sheet.setColumnWidth((short) i, (short) 6000);
			cell = row.createCell((short) i++);

			cell.setCellValue(cellStr(DataDictionary.getFieldItem("p0407").getItemdesc()));
			cell.setCellStyle(style2);

			for (int j = 0; j < fieldList.size(); j++)
			{
				FieldItem item = (FieldItem) fieldList.get(j);

				if("M".equals(item.getItemtype()))
					sheet.setColumnWidth((short)i, (short)6000);
				else if("N".equals(item.getItemtype()))
					sheet.setColumnWidth((short)i, (short)2500);
				else if("D".equals(item.getItemtype()))
					sheet.setColumnWidth((short)i, (short)3000);
				else if("A".equals(item.getItemtype()))
					sheet.setColumnWidth((short)i, (short)3500);

				cell = row.createCell((short) i++);

				cell.setCellValue(cellStr(item.getItemdesc()));
				cell.setCellStyle(style2);

			}

			sql = "select * " + sql.substring(sql.indexOf("from P04 where plan_id="));
			ContentDAO dao = new ContentDAO(this.frameconn);

		    int rowCount = 1;
		    RowSet rset = dao.search(sql);
		    while (rset.next())
		    {
				String p0400 = rset.getString("p0400");
				String a0100 = rset.getString("A0100");
				String a0101 = rset.getString("a0101");
				String b0110 = rset.getString("b0110");
				String e0122 = rset.getString("e0122");
				String p0407  = rset.getString("p0407");
				
				row = sheet.createRow(rowCount++);
		
				cell = row.createCell((short) 0);
				
				cell.setCellValue(cellStr(p0400));
				cell.setCellStyle(style1);
		
				int colIndex = 1;
				if ("2".equals(object_type))
				{
				    b0110 = b0110 != null ? AdminCode.getCodeName("UN", b0110) : "";
				    e0122 = e0122 != null ? AdminCode.getCodeName("UM", e0122) : "";
				    
				    cell = row.createCell((short) colIndex++);
				    
				    cell.setCellValue(cellStr(b0110));
				    cell.setCellStyle(style1);
		
				    cell = row.createCell((short) colIndex++);
				    
				    cell.setCellValue(cellStr(e0122));
				    cell.setCellStyle(style1);
				}
		
				//非人员的考核对象 单位和部门的值都存在b0110字段中
				cell = row.createCell((short) 3);
				String orgTemp="";
				if ("2".equals(object_type))
				    cell.setCellValue(cellStr(a0101));
				else if ("3".equals(object_type))
				{
				    orgTemp = b0110 != null ? AdminCode.getCodeName("UN", b0110) : "";
				    cell.setCellValue(cellStr(orgTemp));
				}
				else if ("4".equals(object_type))
				{		    
				    orgTemp = b0110 != null ? AdminCode.getCodeName("UM", b0110) : "";
				    cell.setCellValue(cellStr(orgTemp));
				}		    
				else if ("1".equals(object_type))
				{
				    orgTemp = b0110 != null ? AdminCode.getCodeName("UN", b0110) : "";
				    if("".equals(orgTemp))
					orgTemp = b0110 != null ? AdminCode.getCodeName("UM", b0110) : "";
				    cell.setCellValue(cellStr(orgTemp));
				}
				cell.setCellStyle(style1);
				
				colIndex = 4;
				cell = row.createCell((short) colIndex++);
				    
				cell.setCellValue(cellStr(p0407));
				cell.setCellStyle(style1);
				
				for (int j = 0; j < fieldList.size(); j++)
				{
				    FieldItem item = (FieldItem) fieldList.get(j);
				    String fieldName = item.getItemid().toLowerCase();
				    String itemtype = item.getItemtype();
				    String codesetid = item.getCodesetid();
				    int decwidth = item.getDecimalwidth();
		
				    cell = row.createCell((short) colIndex++);
				    if ("N".equals(itemtype))
				    {
						if (decwidth == 0)
						    cell.setCellStyle(styleN);
						else if (decwidth == 1)
						    cell.setCellStyle(styleF1);
						else if (decwidth == 2)
						    cell.setCellStyle(styleF2);
						else if (decwidth == 3)
						    cell.setCellStyle(styleF3);
						else if (decwidth == 4)
						    cell.setCellStyle(styleF4);
						// else if(decwidth==5)
						// cell.setCellStyle(styleF5);
						
						if(rset.getObject(fieldName)!=null)
						    cell.setCellValue(rset.getDouble(fieldName));
						cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						
				    } else if("A".equals(itemtype))
				    {
						String value =  rset.getString(fieldName);
						if (value != null)
						{
						    String codevalue = value;
						    if (codevalue.trim().length() > 0 && codesetid != null && codesetid.trim().length() > 0 && !"0".equals(codesetid))
						    	value = AdminCode.getCode(codesetid, codevalue) != null ? AdminCode.getCode(codesetid, codevalue).getCodename() : "";
						    cell.setCellValue(new HSSFRichTextString(value));
						}
						cell.setCellStyle(style1);
				    }else if("D".equals(itemtype))
				    {
						String value = PubFunc.DoFormatDate(PubFunc.FormatDate(rset.getDate(fieldName)));
						if(rset.getDate(fieldName)!=null){
						    value = PubFunc.replace(value, ".", "-");
						cell.setCellValue(new HSSFRichTextString(value)); }
						cell.setCellStyle(styleN);
				
				    }else if( "M".equals(itemtype))
				    {
						String value =  Sql_switcher.readMemo(rset,fieldName);
						if(rset.getObject(fieldName)!=null)
						    cell.setCellValue(new HSSFRichTextString(value));
						cell.setCellStyle(style1);
				    }
				}		
		    }
	
		    String outName = planName + "_" + this.userView.getUserName() + ".xls";
		 
		    fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
		    wb.write(fileOut);
		    fileOut.close();
		    
		   // outName = outName.replace(".xls", "#");
		    //xus 20/4/30 vfs改造
		    outName=PubFunc.encrypt(outName);
		    this.getFormHM().put("outName", outName);
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(wb);
		}

    }

    public HSSFRichTextString cellStr(String context)
    {

		HSSFRichTextString textstr = new HSSFRichTextString(context);
		return textstr;
    }

    public String decimalwidth(int len)
    {

		StringBuffer decimal = new StringBuffer("0");
		if (len > 0)
		    decimal.append(".");
		for (int i = 0; i < len; i++)
		{
		    decimal.append("0");
		}
		decimal.append("_ ");
		return decimal.toString();
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
		return style;
    }
}
