package com.hjsj.hrms.transaction.gz.premium.premium_allocate;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>
 * Title:ImportExcelTrans.java
 * </p>
 * <p>
 * Description:部门月奖金导入数据
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-12-09 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class ImportExcelTrans extends IBusiness
{
    /** 目前只考虑更新不考虑新增 */
    public void execute() throws GeneralException
    {

	FormFile form_file = (FormFile) getFormHM().get("file");
	String theYear = (String) this.getFormHM().get("year");
	String theMonth = (String) this.getFormHM().get("month");

	String date = theYear + "-" + theMonth + "-1";
	Date src_d = DateUtils.getDate(date, "yyyy-MM-dd");
	java.sql.Date d = new java.sql.Date(src_d.getTime());

	ConstantXml xml = new ConstantXml(this.frameconn, "GZ_BONUS", "Params");
	String setid = xml.getNodeAttributeValue("/Params/BONUS_SET", "setid");// 奖金子集
	String dist_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "dist_field");// 下发标识指标
	String rep_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "rep_field");// 上报标识指标
	String keep_save_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "keep_save_field");// 封存字段
	String busiField = setid + "z0";// 业务日期字段

	String busiDateSqlStr = Sql_switcher.year(busiField) + "=" + theYear + " and " + Sql_switcher.month(busiField) + "=" + theMonth;

	ArrayList list = DataDictionary.getFieldList(setid, Constant.USED_FIELD_SET);
	HashMap itemMap = new HashMap();
	HashMap codeColMap = new HashMap();
	for (int i = 0; i < list.size(); i++)
	{
	    FieldItem item = (FieldItem) list.get(i);
	    itemMap.put(item.getItemdesc(), item.getItemid());
	}

	String sqlStr2 = "select b0110,max(i9999) i9999 from " + setid + " group by b0110";
	ContentDAO dao = new ContentDAO(this.frameconn);
	HashMap b0110Map = new HashMap();
	HashMap b0110Map2 = new HashMap();

//	HSSFWorkbook wb = null;
//	HSSFSheet sheet = null;

	InputStream _in = null;
	Workbook wb = null;
	Sheet sheet = null;	
	try
	{

	    this.frowset = dao.search(sqlStr2);
	    while (this.frowset.next())
	    {
		String b0110 = this.frowset.getString("b0110");
		Integer i9999 = new Integer(this.frowset.getInt("i9999"));
		b0110Map.put(b0110, i9999);
	    }

	    sqlStr2 = "select b0110 from " + setid + " where " + busiDateSqlStr;
	    this.frowset = dao.search(sqlStr2);
	    while (this.frowset.next())
	    {
		String b0110 = this.frowset.getString("b0110");
		b0110Map2.put(b0110, b0110);
	    }

    	_in = form_file.getInputStream();
		wb = WorkbookFactory.create(_in);
		sheet = wb.getSheetAt(0);

	    HashMap map = new HashMap();
//	    HSSFRow row = sheet.getRow(0);
	    Row row = sheet.getRow(0);
	    if (row == null)
		throw new GeneralException("文件格式不正确，请用下载的模板维护数据并导入！");
	    int cols = row.getPhysicalNumberOfCells();
	    int rows = sheet.getPhysicalNumberOfRows();
	    StringBuffer codeBuf = new StringBuffer();

	    if (row != null)
	    {
		boolean errorflag = false;
		if (cols < 2 || rows < 1)
		    errorflag = true;
		else
		{
		    // 判断是否用导出德模板来导入数据
		    for (int i = 0; i < 2; i++)
		    {
			String value = "";
//			HSSFCell cell = row.getCell((short) i);
			Cell cell = row.getCell((short) i);
			if (cell != null)
			{
			    switch (cell.getCellType())
			    {
			    case Cell.CELL_TYPE_FORMULA:
				break;
			    case Cell.CELL_TYPE_NUMERIC:
				double y = cell.getNumericCellValue();
				value = Double.toString(y);
				break;
			    case Cell.CELL_TYPE_STRING:
				value = cell.getStringCellValue();
				break;
			    default:
				value = "";
			    }
			} else
			{
			    errorflag = true;
			    break;
			}

			if (i == 0 && !"主键标识串".equalsIgnoreCase(value))
			    errorflag = true;
			else if (i == 1 && !"单位|部门".equalsIgnoreCase(value))
			    errorflag = true;

			if (errorflag)
			    break;
		    }
		}
		if (errorflag)
		    throw new GeneralException("文件格式不正确，请用下载的模板维护数据并导入！");

		for (short m = 2; m < cols; m++)
		{
//		    HSSFCell cell = row.getCell(m);
		    Cell cell = row.getCell(m);
		    if (cell != null)
		    {
			String title = "";
			switch (cell.getCellType())
			{
			case Cell.CELL_TYPE_FORMULA:
			    break;
			case Cell.CELL_TYPE_NUMERIC:
			    double y = cell.getNumericCellValue();
			    title = Double.toString(y);
			    break;
			case Cell.CELL_TYPE_STRING:
			    title = cell.getStringCellValue();
			    break;
			default:
			    title = "";
			}

			if ("".equals(title.trim()))
			    throw new GeneralException("标题行存在空标题！文件格式不正确，请用下载的模板维护数据并导入！");
			String field = (String) itemMap.get(title.trim());
			String codesetid = DataDictionary.getFieldItem(field).getCodesetid();

			if (!"0".equals(codesetid))
			{
			    if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equals(codesetid))
			    {
				codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid + "'  and codeitemid=childid  union all ");
			    } else
			    {
				codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='" + codesetid
					+ "' and  codeitemid not in (select parentid from organization where codesetid='" + codesetid + "') union all ");
			    }
			}
			if (!busiField.equalsIgnoreCase(field))
			{
			    map.put(new Short(m), field + ":" + cell.getRichStringCellValue().toString().trim());
			}
		    } else
			break;
		}
		if (codeBuf.length() > 0)
		{
		    codeBuf.setLength(codeBuf.length() - " union all ".length());

		    RowSet rs = dao.search(codeBuf.toString());
		    while (rs.next())
			codeColMap.put(rs.getString("codesetid") + "a04v2u" + rs.getString("codeitemdesc"), rs.getString("codeitemid"));

		}
	    }

	    // 取数据行
	    ArrayList addList = new ArrayList();
	    ArrayList updateList = new ArrayList();
	    for (int j = 1; j < rows; j++)
	    {
		row = sheet.getRow(j);
//		HSSFCell flagCol = row.getCell((short) 0);		
		Cell flagCol = row.getCell(0);
		String codeitemid = "";
		if (flagCol != null)
		{
		    switch (flagCol.getCellType())
		    {
		    case Cell.CELL_TYPE_BLANK:
			throw new GeneralException("主键标识串列存在空数据，导入数据失败！");
		    case Cell.CELL_TYPE_STRING:
			if (flagCol.getRichStringCellValue().toString().trim().length() == 0)
			    throw new GeneralException("主键标识串列存在空数据，导入数据失败！");
			else
			    codeitemid = flagCol.getRichStringCellValue().toString().trim();
		    }
		} else
		    continue;

		String[] temp = codeitemid.split(":");

		RecordVo vo = new RecordVo(setid);
		vo.setString("b0110", temp[0]);
		vo.setString("i9999", temp[1]);
		vo.setInt("i9999", Integer.parseInt(temp[1]));
		vo = dao.findByPrimaryKey(vo);
		String isKeepSave = vo.getString(keep_save_field.toLowerCase());
		if ("1".equals(isKeepSave))// 已经封存记录不更新
		    continue;

//		if (b0110Map2.get(codeitemid) != null)// 更新情况
//		{
//
//		    vo.setInt("i9999", ((Integer) b0110Map.get(codeitemid)).intValue());
//		    vo = dao.findByPrimaryKey(vo);
//		    String isKeepSave = vo.getString(keep_save_field.toLowerCase());
//		    if (isKeepSave.equals("1"))// 已经封存记录不更新
//			continue;
//		} else if (b0110Map2.get(codeitemid) == null)// 新增情况
//		{
//		    if (b0110Map.get(codeitemid) == null)
//			vo.setInt("i9999", 1);
//		    else
//			vo.setInt("i9999", ((Integer) b0110Map.get(codeitemid)).intValue() + 1);
//		    vo.setString("createusername", this.getUserView().getUserName());
//		    // vo.setString("modusername",
//		    // this.getUserView().getUserName());
//		    vo.setDate("createtime", new Date());
//		    // vo.setDate("modtime", new Date());
//		    vo.setString(dist_field.toLowerCase(), "2");
//		    vo.setString(rep_field.toString(), "2");
//		    // vo.setDate(busiField.toLowerCase(), d);
//		    vo.setString(keep_save_field.toLowerCase(), "2");
//		}
		vo.setString("modusername", this.getUserView().getUserName());
		vo.setDate("modtime", new Date());
//		vo.setDate(busiField.toLowerCase(), d);

		for (short c = 2; c < cols; c++)
		{
//		    HSSFCell cell1 = row.getCell(c);
		    Cell cell1 = row.getCell(c);
		    if (cell1 != null)
		    {
			String fieldItems = (String) map.get(new Short(c));
			if (fieldItems == null)
			    continue;
			String[] fieldItem = fieldItems.split(":");
			String field = fieldItem[0].toLowerCase();
			String fieldName = fieldItem[1];
			String itemtype = DataDictionary.getFieldItem(field).getItemtype();
			String codesetid = DataDictionary.getFieldItem(field).getCodesetid();
			int decwidth = DataDictionary.getFieldItem(field).getDecimalwidth();

			String value = "";
			String msg = "";
			switch (cell1.getCellType())
			{
			case Cell.CELL_TYPE_FORMULA:
			    break;
			case Cell.CELL_TYPE_NUMERIC:
			    double y = cell1.getNumericCellValue();
			    value = Double.toString(y);
			    value = PubFunc.round(value, decwidth);

			    if ("N".equals(itemtype))
			    {
				if (decwidth == 0)
				    vo.setInt(field, Integer.parseInt(value));
				else
				    vo.setDouble(field, y);
			    } else if ("D".equals(itemtype))
			    {
				value = changeNumToDate(value);
				vo.setDate(field, java.sql.Date.valueOf(value));
			    }
			    break;
			case Cell.CELL_TYPE_STRING:
			    value = cell1.getRichStringCellValue().toString();
			    if (!"0".equals(codesetid) && !"".equals(codesetid))
				if (codeColMap.get(codesetid + "a04v2u" + value.trim()) != null)
				    value = (String) codeColMap.get(codesetid + "a04v2u" + value.trim());
				else
				    value = null;

			    if ("D".equals(itemtype) && value != null && value.trim().length() > 0)
			    {
				if (!this.isDataType(decwidth, itemtype, value))
				{
				    msg = "源数据(" + fieldName + ")中数据:" + value + " 不符合格式!";
				    throw new GeneralException(msg);
				}
				value = PubFunc.replace(value, ".", "-");
				vo.setDate(field, java.sql.Date.valueOf(value));
			    } else
				vo.setString(field, value);
			    break;
			case Cell.CELL_TYPE_BLANK:// 如果什么也不填的话数值就默认更新为0
			    if ("N".equals(itemtype))
			    {
				value = PubFunc.round(value, decwidth);
				vo.setDouble(field, Double.valueOf(value).doubleValue());

			    }
			    break;
			default:
			}

			if (("N".equals(itemtype) || "D".equals(itemtype)) && value.trim().length() > 0)
			{
			    if (!this.isDataType(decwidth, itemtype, value))
			    {
				msg = "源数据(" + fieldName + ")中数据:" + value + " 不符合格式!";
				throw new GeneralException(msg);
			    }
			}
		    }
		}
//		if ((b0110Map2.get(codeitemid) == null))
//		    addList.add(vo);
//		else
		    updateList.add(vo);
	    }

	    if (addList.size() > 0)
		dao.addValueObject(addList);
	    if (updateList.size() > 0)
		dao.updateValueObject(updateList);

	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	} finally {
		PubFunc.closeResource(wb);
		PubFunc.closeIoResource(_in);
	}
    }

    /**
         * 判断 值类型是否与 要求的类型一致
         * 
         * @param columnBean
         * @param itemid
         * @param value
         * @return
         */
    public boolean isDataType(int decwidth, String itemtype, String value)
    {

	boolean flag = true;
	if ("N".equals(itemtype))
	{
	    if (decwidth == 0)
	    {
		flag = value.matches("^[+-]?[\\d]+$");
	    } else
	    {
		flag = value.matches("^[+-]?[\\d]*[.]?[\\d]+");
	    }

	} else if ("D".equals(itemtype))
	{
	    flag = value.matches("[0-9]{4}[#-.][0-9]{2}[#-.][0-9]{2}");
	}
	return flag;
    }

    public static String changeNumToDate(String s)
    {

	String rtn = "1900-01-01";
	try
	{
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    java.util.Date date1 = new java.util.Date();
	    date1 = format.parse("1900-01-01");
	    long i1 = date1.getTime();

	    // 这里要减去2，(Long.parseLong(s)-2) 不然日期会提前2天，具体原因不清楚，

	    // 估计和java计时是从1970-01-01开始有关
	    // 而excel里面的计算是从1900-01-01开始
	    i1 = i1 / 1000 + ((Long.parseLong(s) - 2) * 24 * 3600);
	    date1.setTime(i1 * 1000);
	    rtn = format.format(date1);
	} catch (Exception e)
	{
	    rtn = "1900-01-01";
	}
	return rtn;

    }
}