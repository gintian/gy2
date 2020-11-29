package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.edit_report.EditReport;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
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
import org.apache.poi.ss.util.CellRangeAddressList;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ExportExcelU02Trans extends IBusiness
{

    public void execute() throws GeneralException
    {
    	String unitcode=(String)this.getFormHM().get("unitcode");
    	String id=(String)this.getFormHM().get("id");
    	String Report_id=(String)this.getFormHM().get("report_id");
    	String sqlStr=(String)this.getFormHM().get("sqlStr");
    	String grad=(String)this.getFormHM().get("grad");
    	EditReport editReport=new EditReport();
    	ArrayList list=editReport.getU02FieldList(this.getFrameconn(),Report_id,false,1);
        HashMap gradHash=editReport.getUnitCodeGrad(this.getFrameconn(),grad);
    	HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
    	String sheetname = getReportName(Report_id);
    	HSSFSheet sheet = wb.createSheet(sheetname);
//    	sheet.setProtect(true);
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
    	style1.setAlignment(HorizontalAlignment.CENTER);
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

    	//sheet.setColumnWidth((short) 0, (short) 0); 
    	HSSFPatriarch patr = sheet.createDrawingPatriarch();

    	HSSFRow row = sheet.getRow(0);
    	if(row==null)
			row = sheet.createRow(0);

    	HSSFCell cell = null;
    	//String fieldExplain="";
    	HSSFComment comm =null;
    	ArrayList codeCols = new ArrayList();
    	FieldItem unitcodeFieldItem=new FieldItem();
//    	unitcodeFieldItem.setItemid("unitcode");
//    	unitcodeFieldItem.setItemdesc("单位名称");//
//    	unitcodeFieldItem.setFieldsetid("UNITCODE");
//    	unitcodeFieldItem.setItemtype("A");
//    	unitcodeFieldItem.setState("1");
//    	unitcodeFieldItem.setItemid("unitcode");
    	unitcodeFieldItem.setItemdesc("二级单位名称");//xgq修改为2级单位
    	unitcodeFieldItem.setFieldsetid("UNITCODE");
    	unitcodeFieldItem.setItemtype("A");
    	unitcodeFieldItem.setState("1");
    	unitcodeFieldItem.setItemid("unitcode");
    	list.add(1,unitcodeFieldItem);
    	unitcodeFieldItem=new FieldItem();
    	unitcodeFieldItem.setItemdesc("三级单位名称");//xgq修改为三级单位
    	unitcodeFieldItem.setFieldsetid("UNITCODE3");
    	unitcodeFieldItem.setItemtype("A");
    	unitcodeFieldItem.setState("1");
    	unitcodeFieldItem.setItemid("unitcode");
    	list.add(2,unitcodeFieldItem);
//    	String ss =unitcodeFieldItem.getFieldsetid();
    	for (int i = 0; i < list.size(); i++)
    	{
    		FieldItem field = (FieldItem) list.get(i);    		
    	    String fieldName = field.getItemid();
    	    String fieldLabel = field.getItemdesc();
//    	    if(fieldName.equalsIgnoreCase("unitcode"))
//    	    	sheet.setColumnWidth((short) (i), (short) (100*100)); 
//    	    else
//    	    {
    	    	if("1".equals(field.getState()))
    	    	   sheet.setColumnWidth((short) (i), (short) (50*100));     	    
//    	    	else
//    	    	   sheet.setColumnWidth((short) 0, (short) 0); 
////    	    }
//    	    	  if("u0200".equalsIgnoreCase(fieldName)){
//    	  	    	  sheet.setColumnWidth((short) 0, (short) 0); 
//    	  	    }
    	    //fieldExplain = DataDictionary.getFieldItem(fieldName).getExplain();
    	    cell = row.createCell((short) (i));
    	   // cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    	    cell.setCellValue(cellStr(fieldLabel));    	   
    	    cell.setCellStyle(style2);       	    
    	    comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (i+1), 0, (short) (i+2), 1));
    	    comm.setString(new HSSFRichTextString(fieldName));
    	    cell.setCellComment(comm);
    	    if (!"0".equals(field.getCodesetid())&&!"unitcode".equalsIgnoreCase(fieldName))
    	      codeCols.add(field.getCodesetid() + ":" + new Integer(i).toString());
    	    
    	}
    	sqlStr = SafeCode.decode(sqlStr);
    	sqlStr = PubFunc.keyWord_reback(sqlStr);   //add by wangchaoqun on 2014-9-16
    	try
    	{
    	    int rowCount = 1;
    	    ContentDAO dao=new ContentDAO(this.getFrameconn());
    	    RowSet rset = dao.search(sqlStr);
    	    String itemid="";
    	    String itemfieldid ="";
    	    String itemtype="";
    	    int decwidth=0;
    	    String codesetid ="";
    	    int rsetCount=0;
    	    int count =0;
    	    int ext = 0;
    	    while (rset.next())
    	    {
    	    	count++;
    	    	if(count>65000){
    	    		count=0;
    	    		ext++;
    	    		rsetCount=0;
    	    		sheet = wb.createSheet(sheetname+ext);
    	    		 patr = sheet.createDrawingPatriarch();
    	    		 row = sheet.getRow(0);
    					if(row==null)
    						row = sheet.createRow(0);
//    	        	 row = sheet.createRow(0);
    	        	 rowCount=1;
    	        	 	for (int i = 0; i < list.size(); i++)
    	            	{
    	            		FieldItem field = (FieldItem) list.get(i);    		
    	            	    String fieldName = field.getItemid();
    	            	    String fieldLabel = field.getItemdesc();
//    	            	    if(fieldName.equalsIgnoreCase("unitcode"))
//    	            	    	sheet.setColumnWidth((short) (i), (short) (100*100)); 
//    	            	    else
//    	            	    {
    	            	    	if("1".equals(field.getState()))
    	            	    	   sheet.setColumnWidth((short) (i), (short) (50*100));     	    
    	            	    	else
    	            	    	   sheet.setColumnWidth((short) 0, (short) 0); 
//    	            	    }
    	            	    
    	            	    //fieldExplain = DataDictionary.getFieldItem(fieldName).getExplain();
    	            	    cell = row.createCell((short) (i));
    	            	   // cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    	            	    cell.setCellValue(cellStr(fieldLabel));    	   
    	            	    cell.setCellStyle(style2);       	    
    	            	    comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (i+1), 0, (short) (i+2), 1));
    	            	    comm.setString(new HSSFRichTextString(fieldName));
    	            	    cell.setCellComment(comm);
    	            	    if (!"0".equals(field.getCodesetid())&&!"unitcode".equalsIgnoreCase(fieldName))
    	            	      codeCols.add(field.getCodesetid() + ":" + new Integer(i).toString());
    	            	    
    	            	}
    	    	}
    	    	row = sheet.getRow(rowCount);
				if(row==null)
					row = sheet.createRow(rowCount);
				rowCount++;
    	    	//row = sheet.createRow(rowCount++);
    	    	row.setHeight((short)600);
    	    	rsetCount++;
    	    	for (int i = 0; i < list.size(); i++)
    			{
    	    		cell = row.createCell((short) (i));
    	    		FieldItem field=(FieldItem)list.get(i);    	    		
    	    		itemid=field.getItemid();
    	    		itemtype=field.getItemtype();
    	    		decwidth=field.getDecimalwidth();
    	    		codesetid=field.getCodesetid();
    	    		itemfieldid = field.getFieldsetid();
    	    		if ("N".equals(itemtype))
    			    {
//速度    				   if (decwidth == 0)
//    				      cell.setCellStyle(styleN);
//    				   else if (decwidth == 1)
//    				      cell.setCellStyle(styleF1);
//    				   else if (decwidth == 2)
//    				      cell.setCellStyle(styleF2);
//    				   else if (decwidth == 3)
//    				      cell.setCellStyle(styleF3);
//    				   else if (decwidth == 4)
//    				      cell.setCellStyle(styleF4);    				   
//    				   cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    				   cell.setCellValue(rset.getDouble(itemid));
    			    } else if("D".equals(itemtype))
    			    {
    			    	Date date=rset.getDate(itemid);
    			    	if(date==null)
    			    	{
    			    		 cell.setCellValue("");
    			    	}else
    			    	{
    			    		String value =DateUtils.format(date,"yyyyMM");
    			    		cell.setCellValue(new HSSFRichTextString(value));
    			    	}    			    	
  //  			    	 cell.setCellStyle(style1);//速度
    			    }else
    			    {
    			       String value = rset.getString(itemid);    
    			    
    				   if(value!=null)
    				   {
    					  if("unitcode".equalsIgnoreCase(itemid))
    	    			  {
    						  LazyDynaBean bean=(LazyDynaBean)gradHash.get(value);
    						  if(bean==null){
    							  cell.setCellValue("");
    							  continue;
    						  }
    						  if("UNITCODE".equalsIgnoreCase(itemfieldid)){
    							  
    							  if(bean!=null&& "2".equals(bean.get("grade")))
        						  {
    								  if(bean.get("grademess")!=null){
    								  cell.setCellValue((String)bean.get("grademess"));
    								  }else{
    									  cell.setCellValue(""); 
    								  }
        						  }else{
        							  if(bean.get("grademessx")!=null){
        							  cell.setCellValue((String)bean.get("grademessx"));
        							  }else{
        								  cell.setCellValue("");   
        							  }
        						  }
    						  
    						  }else{
    							  if(bean!=null&& "3".equals(bean.get("grade")))
        						  {
    								  if(bean.get("grademess")!=null){
        								  cell.setCellValue((String)bean.get("grademess"));
        								  }else{
        									  cell.setCellValue(""); 
        								  }
        						  }else{
        							  if(bean.get("grademessx")!=null){
            							  cell.setCellValue((String)bean.get("grademessx"));
            							  }else{
            								  cell.setCellValue("");   
            							  }
            						  }
    						  }
    						 
    						
    	    			  }else
    	    			  {
    	    				  String codevalue = value;
        				      if (codevalue.trim().length() > 0 && codesetid != null && codesetid.trim().length() > 0 && !"0".equals(codesetid))
        					     value=AdminCode.getCode(codesetid, codevalue) != null ? AdminCode.getCode(codesetid, codevalue).getCodename() : "";
        				      cell.setCellValue(new HSSFRichTextString(value));
    	    			  }
    				     
    				   }else
    					   cell.setCellValue("");
 //   				   cell.setCellStyle(style1);//速度
    			    }
    			}
    	    }
    	    
    	    rowCount++;
    	    int index = 0;
    	    String[] lettersUpper={"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    	    for (int n = 0; n < codeCols.size(); n++)
    	    {
    		    String codeCol = (String) codeCols.get(n);
    		    String[] temp = codeCol.split(":");
    		    codesetid = temp[0];
    		    int codeCol1 = Integer.valueOf(temp[1]).intValue();
    		    StringBuffer codeBuf = new StringBuffer();
    		    if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equals(codesetid))
    		    {
    		       codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid + "' and codeitemid=childid");
    		    } else
    		    {
    		        codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='" + codesetid
    			    + "' and  codeitemid not in (select parentid from organization where codesetid='" + codesetid + "')");
    		    }
    		    rset = dao.search(codeBuf.toString());
    		    int m = 0;
    			while (rset.next())
    			{
    				row = sheet.getRow(m + 0);
    				if(row==null)
    					row = sheet.createRow(m + 0);

    			  //  row = sheet.createRow(m + 0);
    			    cell = row.createCell((short) (52+index));
    			    cell.setCellValue(new HSSFRichTextString(rset.getString("codeitemdesc")));
    			    m++;
    			}
    			if(m>0){//liuy 2015-2-5 7365：精算报表/编辑报表：下载模板和导出excel后台报错 (添加判断，当代码类不存在的时候不执行下面)
    				sheet.setColumnWidth((short)(52+index), (short)0);
    				String strFormula = "$B"+lettersUpper[index]+"$1:$B"+lettersUpper[index]+"$" + Integer.toString(m); // 表示BA列1-m行作为下拉列表来源数据
//    			HSSFDataValidation data_validation = new HSSFDataValidation((short) 1, (short) codeCol1, (short) rowCount, (short) codeCol1); // 定义生成下拉筐的范围
//    			data_validation.setDataValidationType(HSSFDataValidation.DATA_TYPE_LIST);
//    			data_validation.setFirstFormula(strFormula);
//    			data_validation.setSecondFormula(null);
//    			data_validation.setExplicitListFormula(true);
//    			data_validation.setSurppressDropDownArrow(false);
//    			data_validation.setEmptyCellAllowed(false);
//    			data_validation.setShowPromptBox(false);
//    			sheet.addValidationData(data_validation);
    				CellRangeAddressList addressList = new CellRangeAddressList( 1, rowCount, codeCol1, codeCol1);
    				DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
    				HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
    				dataValidation.setSuppressDropDownArrow(false);		
    				sheet.addValidationData(dataValidation);
    			}

    			index++;
    	    }
    	    resetSize(rsetCount,sheet);
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	String outName = this.userView.getUserName()+"_"+Report_id;    	
    	outName += ".xls";

    	try
    	{
    	    FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
    	    wb.write(fileOut);
    	    fileOut.close();
    	} catch (Exception e)
    	{
    	    e.printStackTrace();
    	    throw GeneralExceptionHandler.Handle(e);
    	}
//    	outName = outName.replace(".xls", "#");
    	outName = PubFunc.encrypt(outName);  //add by wangchaoqun on 2014-9-16
    	getFormHM().put("outName", SafeCode.decode(outName));
    	sheet = null;
    	wb = null;       
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
    public HSSFRichTextString cellStr(String context)
    {

	HSSFRichTextString textstr = new HSSFRichTextString(context);
	return textstr;
    }
    public void resetSize(int temp,HSSFSheet sheet)
	{
		
		for(int i=0;i<temp;i++)
		{

			HSSFRow row = sheet.getRow(i+1);
			if(row==null)
				row = sheet.createRow(i+1);
			row.setHeight(Short.parseShort(String.valueOf(600)));			
		}
	}
    private String getReportName(String reportid)
    {
    	if(reportid==null||reportid.length()<=0)
    		return "";
    	if("U01".equalsIgnoreCase(reportid))
    		return "表1-特别事项";
    	else if("U02_1".equalsIgnoreCase(reportid))
    		return "表2-1离休人员";
    	else if("U02_2".equalsIgnoreCase(reportid))
    		return "表2-2退休人员";
    	else if("U02_3".equalsIgnoreCase(reportid))
    		return "表2-3内退人员";
    	else if("U02_4".equalsIgnoreCase(reportid))
    		return "表2-4遗属";
    	else if("U03".equalsIgnoreCase(reportid))
    		return "表3财务信息";
    	else if("U04".equalsIgnoreCase(reportid))
    		return "表4人员统计表";
    	else if("U05".equalsIgnoreCase(reportid))
    		return "表5人员变动及人均福利对照表";
    	return "";
    }
}
