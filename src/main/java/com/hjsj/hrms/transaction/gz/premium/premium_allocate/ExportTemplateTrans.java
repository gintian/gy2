package com.hjsj.hrms.transaction.gz.premium.premium_allocate;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>
 * Title:ExportTemplateTrans.java
 * </p>
 * <p>
 * Description:部门月奖金下载模板
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-12-08 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class ExportTemplateTrans extends IBusiness
{
    HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿

    HSSFSheet sheet = wb.createSheet();

    HSSFFont font2 = wb.createFont();

    public void execute() throws GeneralException
    {

	String theYear = (String) this.getFormHM().get("theYear");
	String theMonth = (String) this.getFormHM().get("theMonth");

	String operOrg = this.userView.getUnit_id();// 操作单位

	if (operOrg.trim().length() == 0|| "UN".equalsIgnoreCase(operOrg))
	    throw new GeneralException(ResourceFactory.getProperty("error.notdefine.operOrg"));

	StringBuffer buf = new StringBuffer();
	StringBuffer tempBuf = new StringBuffer();
	buf.append("select codeitemid ,codeitemdesc,a0000 from organization where  codesetid in ('UM','UN') ");
	String[] temp = operOrg.split("`");

	for (int i = 0; i < temp.length; i++)
	    tempBuf.append(" or  (parentid = '" + temp[i].substring(2) + "' and codeitemid!='"+temp[i].substring(2) +"') ");

	if (temp.length > 0)
	    buf.append(" and (" + tempBuf.substring(3) + " )");

	ConstantXml xml = new ConstantXml(this.frameconn, "GZ_BONUS", "Params");
	String setid = xml.getNodeAttributeValue("/Params/BONUS_SET", "setid");// 奖金子集
	String dist_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "dist_field");// 下发标识指标
	String rep_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "rep_field");// 上报标识指标
	String keep_save_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "keep_save_field");// 封存字段
	String busiField = setid + "z0";// 业务日期字段
	String busics =  setid + "z1";// 业务次数字段
	
	String strYm = DateUtils.format(new Date(), "yyyy-MM-dd");
	String[] tmp = StringUtils.split(strYm, "-");
	strYm = tmp[0] + "-" + tmp[1] + "-01";

	font2.setFontHeightInPoints((short) 10);

	sheet.setColumnWidth((short) 0, (short) 0);
	sheet.setColumnWidth((short) 1, (short) 4000);
	HSSFRow row = sheet.createRow(0);
	HSSFCell cell = row.createCell((short) 0);
//	cell.setEncoding(HSSFCell.ENCODING_UTF_16);
	cell.setCellValue(cellStr("主键标识串"));
	cell.setCellStyle(this.getDataStyle(2, 0));

	cell = row.createCell((short) 1);
//	cell.setEncoding(HSSFCell.ENCODING_UTF_16);
	cell.setCellValue(cellStr("单位|部门"));
	cell.setCellStyle(this.getDataStyle(2, 0));

	HashMap formulaFields = new HashMap();// 统计项、导入项和计算项
	ContentDAO dao = new ContentDAO(this.frameconn);
	String sqlStr = "select * from bonusformula where Upper(setid)='" + setid.toUpperCase() + "'";
	try
	{
	    this.frowset = dao.search(sqlStr);
	    while (this.frowset.next())
		formulaFields.put(this.frowset.getString("itemname").toLowerCase(), "");

	    int index = 2;
	    HashMap map = new HashMap();
	    ArrayList list = DataDictionary.getFieldList(setid, Constant.USED_FIELD_SET);
	    StringBuffer sql = new StringBuffer("select ");

	    for (int i = 0; i < list.size(); i++)
	    {
		FieldItem fielditem = (FieldItem) list.get(i);
		Field field = fielditem.cloneField();
		String itemid = field.getName();
		String itemtype = fielditem.getItemtype();
		int decimalLen = fielditem.getDecimalwidth();
		String itemdesc = fielditem.getItemdesc();
		if ("0".equals(this.userView.analyseFieldPriv(itemid, 0)) && "0".equals(this.userView.analyseFieldPriv(itemid, 1)))
		    continue;
		if ("1".equals(this.userView.analyseFieldPriv(itemid, 0)) && "1".equals(this.userView.analyseFieldPriv(itemid, 1)))
		    continue;
		if (!"2".equals(this.userView.analyseTablePriv(setid)))
		    continue;
		if (formulaFields.get(itemid.toLowerCase()) != null)
		    continue;

		if (itemid.equalsIgnoreCase(dist_field))
		    continue;
		if (itemid.equalsIgnoreCase(rep_field))
		    continue;
		if (itemid.equalsIgnoreCase(keep_save_field))
		    continue;
		
		sql.append("b." + itemid + ",");
		
		if ((busiField.toLowerCase()+","+busics.toLowerCase()).indexOf(itemid.toLowerCase()) != -1)//业务日期和次数字段不显示出来
		    continue;
		
		map.put(Integer.toString(index), fielditem);

		cell = row.createCell((short) index++);
//		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(cellStr(itemdesc));
		cell.setCellStyle(this.getDataStyle(2, 0));
	    }

	    String busiDateSqlStr = Sql_switcher.year("b." + busiField) + "=" + theYear + " and " + Sql_switcher.month("b." + busiField) + "=" + theMonth;
	    sql.append("b."+busiField+",b."+busics+",b.i9999,a.codeitemid,a.codeitemdesc from (");
	    sql.append(buf.toString());
//	    sql.append(") a left join ");如果子集表中没有的机构就不要导出来了
	    sql.append(") a  join ");
	    sql.append(setid);
	    sql.append(" b on a.codeitemid=b.b0110 and ");
	    sql.append(busiDateSqlStr);
	    sql.append(" order by a.a0000");
	    
	    int rowCount = 1;
	    RowSet rset = dao.search(sql.toString());
	    while (rset.next())
	    {
		String codeitemid = rset.getString("codeitemid");
		String codeitemdesc = rset.getString("codeitemdesc");
		int i9999 = rset.getInt("i9999");		
		
		row = sheet.createRow(rowCount++);

		cell = row.createCell((short) 0);
//		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(cellStr(codeitemid+":"+i9999));
		cell.setCellStyle(this.getDataStyle(1, 0));

		cell = row.createCell((short) 1);
//		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(cellStr(codeitemdesc));
		cell.setCellStyle(this.getDataStyle(1, 0));

		for (int m = 2; m < index; m++)
		{
		    FieldItem fieldItem = (FieldItem) map.get(Integer.toString(m));
		    String itemid = fieldItem.getItemid();
		    String codesetid = fieldItem.getCodesetid();
		    int decimalLen = fieldItem.getDecimalwidth();
		    String itemtype = fieldItem.getItemtype();
		    HSSFCellStyle style = null;
		    cell = row.createCell((short) m);

		    if ("A".equalsIgnoreCase(itemtype))
		    {
			style = this.getDataStyle(1, 0);
			String value = rset.getString(itemid);
			if (value != null)
			{
			    String codevalue = value;
			    if (codevalue.trim().length() > 0 && codesetid != null && codesetid.trim().length() > 0 && !"0".equals(codesetid))
				value = AdminCode.getCode(codesetid, codevalue) != null ? AdminCode.getCode(codesetid, codevalue).getCodename() : "";
			    cell.setCellValue(new HSSFRichTextString(value));
			}
		    } else if (("D".equalsIgnoreCase(itemtype)))
		    {
			style = this.getDataStyle(4, 0);
			
			String value = PubFunc.DoFormatDate(PubFunc.FormatDate(rset.getDate(itemid)));
			if (rset.getDate(itemid) != null)
			{
			    value = PubFunc.replace(value, ".", "-");
			    cell.setCellValue(new HSSFRichTextString(value));
			}
			if(itemid.equalsIgnoreCase(busiField))
			    cell.setCellValue(new HSSFRichTextString(strYm));
		    } else if (("N".equalsIgnoreCase(itemtype)))
		    {
			style = this.getDataStyle(3, decimalLen);
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			
			if(rset.getObject(itemid)!=null)
			    cell.setCellValue(rset.getDouble(itemid));
		    }else if("M".equalsIgnoreCase(itemtype))
		    {
		    	
			String value =  Sql_switcher.readMemo(rset,itemid);
			style = this.getDataStyle(1, 0);
			if(rset.getObject(itemid)!=null)
			    cell.setCellValue(new HSSFRichTextString(value));
		    }
//		    cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		    
		    cell.setCellStyle(style);
		}
	    }
	    if(rset!=null)
		rset.close();
	    
	    String outName =  "月奖金_" + this.userView.getUserName() + ".xls";
		 
	    FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
	    wb.write(fileOut);
	    fileOut.close();
	    
	    this.getFormHM().put("outName", PubFunc.encrypt(outName));	    
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
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

    public HSSFCellStyle getDataStyle(int x, int decimalLen)
    {

	HSSFCellStyle style = dataStyle(wb);

	if (x == 1)// 文本格式
	{
	    style.setFont(font2);
	    style.setAlignment(HorizontalAlignment.LEFT);
	    style.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
	} else if (x == 2)// 标题格式
	{
	    style.setFont(font2);
	    style.setAlignment(HorizontalAlignment.CENTER);
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
	} else if (x == 3)// 数值型
	{
	    style.setAlignment(HorizontalAlignment.RIGHT);
	    HSSFDataFormat df = wb.createDataFormat();
	    style.setDataFormat(df.getFormat(decimalwidth(decimalLen)));
	} else if (x == 4)// 日期型
	{
	    style.setAlignment(HorizontalAlignment.RIGHT);
	}
	style.setWrapText(true);
	return style;
    }
}
