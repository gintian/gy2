package com.hjsj.hrms.transaction.gz.bonus.inform;

import com.hjsj.hrms.businessobject.ht.ContractBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>
 * Title:SearchBonusTrans.java
 * </p>
 * <p>
 * Description:奖金导人模板数据excel
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-07-06 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class GetTemplDataTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	FormFile form_file = (FormFile) getFormHM().get("file");
	String bonusSet = (String) this.getFormHM().get("bonusSet");
	String jobnumFld = (String) this.getFormHM().get("jobnumFld");

	HashMap map = new HashMap();
	FieldItem item = new FieldItem();
	item.setCodesetid("@@");
	item.setUseflag("1");
	item.setItemtype("A");
	item.setItemid("dbase");
	item.setAlign("left");
	item.setItemdesc("人员库");
	map.put("人员库", item);
	map.put("姓名", DataDictionary.getFieldItem("a0101"));
	if (jobnumFld != null && jobnumFld.trim().length() > 0)
	{
	    item = DataDictionary.getFieldItem(jobnumFld);
	    item.setItemdesc("工号");
	    map.put("工号", item);
	}
	ArrayList list = DataDictionary.getFieldList(bonusSet, Constant.USED_FIELD_SET);
	HashMap codesetMap = new HashMap();
	String doStatusFld="";
	for (int i = 0; i < list.size(); i++)
	{
	    FieldItem fielditem = (FieldItem) list.get(i);
	    map.put(fielditem.getItemdesc(), fielditem);
	    if (!"0".equals(fielditem.getCodesetid()))
		codesetMap.put(fielditem.getCodesetid(), "");
	    if("处理状态".equals(fielditem.getItemdesc()))
		doStatusFld=fielditem.getItemid();
	}
	// excel中需要从汉字转换为编码
	HashMap codeItemMap = this.getCodeItems(codesetMap);
	ContractBo bo = new ContractBo(this.frameconn, this.userView);
	HashMap dbmap = bo.searchNbase();
	Set keySet = dbmap.keySet();
	for (Iterator iter = keySet.iterator(); iter.hasNext();)
	{
	    String dbpri = (String) iter.next();
	    String dbname = (String) dbmap.get(dbpri);
	    codeItemMap.put(dbname, dbpri);
	}

	ContentDAO dao = new ContentDAO(this.frameconn);
//	HSSFWorkbook wb = null;
//	HSSFSheet sheet = null;
	Workbook wb = null;
	Sheet sheet = null;
	InputStream _in = null;
	try
	{
//	    wb = new HSSFWorkbook(form_file.getInputStream());
//	    sheet = wb.getSheetAt(0);
		_in = form_file.getInputStream();
		 wb = WorkbookFactory.create(_in);
		 sheet = wb.getSheetAt(0);
	} catch (Exception e)
	{
	    System.out.println(e);
	} finally {
		PubFunc.closeResource(wb);
		PubFunc.closeIoResource(_in);
	}
//	HSSFRow row = sheet.getRow(0);
	Row row = sheet.getRow(0);
	if(row==null)
	    throw new GeneralException("请用导出的模板Excel来导入数据！");
	int cols = row.getPhysicalNumberOfCells();
	int rows = sheet.getPhysicalNumberOfRows();
	ArrayList fieldList = new ArrayList();
	for (short c = 0; c < cols; c++)
	{
	    Cell cell = row.getCell(c);
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
		    throw new GeneralException("标题行存在空标题！请用导出的模板Excel来导入数据！");
		else
		{
		    boolean flag = false;
		    if (c == 0 && !"人员库".equals(title))
			flag = true;
		    else if (c == 1 && !"姓名".equals(title))
			flag = true;

		    if (jobnumFld != null && jobnumFld.trim().length() > 0)
		    {
			if (c == 2 && !"工号".equals(title))
			    flag = true;
			else if (c == 3 && !"金额".equals(title))
			    flag = true;
			else if (c == 4 && !"业务日期".equals(title))
			    flag = true;
			else if (c == 5 && !"奖金项目".equals(title))
			    flag = true;
		    } else
		    {
			if (c == 2 && !"金额".equals(title))
			    flag = true;
			else if (c == 3 && !"业务日期".equals(title))
			    flag = true;
			else if (c == 4 && !"奖金项目".equals(title))
			    flag = true;
		    }
		    if (flag)
			throw new GeneralException("请用导出模板导入数据！");
		    if(map.get(title)==null)
			throw new GeneralException("标题["+title+"]在奖金子集中找不到对应字段！");
		    fieldList.add(map.get(title));
		}
	    }
	}
	String createUserName = this.userView.getUserFullName();
	String createTime = PubFunc.getStringDate("yyyy-MM-dd");
	for (int j = 1; j < rows; j++)
	{
	    row = sheet.getRow(j);
	    
	    if(row==null || (row!=null && row.getCell((short) 0)==null))
		continue;
	    
	    String dbpri = "";
	    String jobnumFldValue = "";
	    String fieldStr = "";
	    String valueStr = "";
	    String a0100 = "";
	    int i9999 = 0;
	    ArrayList dataList = new ArrayList();
	    for (int i = 0; i < fieldList.size(); i++)
	    {
		FieldItem fieldItem = (FieldItem) fieldList.get(i);
		String itemid = fieldItem.getItemid();
		String itemtype = fieldItem.getItemtype();
		String itemDesc = fieldItem.getItemdesc();
		String codesetid = fieldItem.getCodesetid();
		int decwidth = fieldItem.getDecimalwidth();
		Cell cell = row.getCell((short) i);

		if (!"人员库".equals(itemDesc) && !"姓名".equals(itemDesc) && !"工号".equals(itemDesc))
		{
		    fieldStr += itemid + ",";
		    valueStr += "?,";
		}
		  String msg = "";
		if (cell != null)
		{
		    String value = "";
		    switch (cell.getCellType())
		    {
		    case Cell.CELL_TYPE_FORMULA:
			break;
		    case Cell.CELL_TYPE_NUMERIC:
			double y = cell.getNumericCellValue();
			value = PubFunc.round(Double.toString(y), decwidth);
			if (!"人员库".equals(itemDesc) && !"姓名".equals(itemDesc) && !"工号".equals(itemDesc))
			{
			    if ("N".equals(itemtype))
				dataList.add(new Double(value));
			    else if ("D".equals(itemtype))
			    {
				value = changeNumToDate(value);
				dataList.add(java.sql.Date.valueOf(value));
			    }
			}
			break;
		    case Cell.CELL_TYPE_STRING:
			value = cell.getRichStringCellValue().toString();
			if (!"人员库".equals(itemDesc) && !"姓名".equals(itemDesc) && !"工号".equals(itemDesc))
			{
			    if (!"0".equals(codesetid) && !"".equals(codesetid))
				if (codeItemMap.get(codesetid + "a04v2u" + value.trim()) != null)
				    value = (String) codeItemMap.get(codesetid + "a04v2u" + value.trim());
				else
				    value = null;
			    if("D".equals(itemtype) && value!=null)
			    {
				if (!this.isDataType(decwidth, itemtype, value))
				{
				    msg = "源数据(" + fieldItem.getItemdesc() + ")中数据:" + value + " 不符合格式!";
				    throw new GeneralException(msg);
				}
				value = PubFunc.replace(value, ".", "-");
				dataList.add(java.sql.Date.valueOf(value));
			    }
			    else				
				dataList.add(value);
			}
			break;
		    case Cell.CELL_TYPE_BLANK:// 如果什么也不填的话数值就默认更新为0
			if ("N".equals(itemtype))
			{
			    value = PubFunc.round("0", decwidth);
			    if (!"人员库".equals(itemDesc) && !"姓名".equals(itemDesc) && !"工号".equals(itemDesc))
				dataList.add(new Double(value));
			} else if (!"人员库".equals(itemDesc) && !"姓名".equals(itemDesc) && !"工号".equals(itemDesc))
			    dataList.add(null);
			break;
		    default:
			if (!"人员库".equals(itemDesc) && !"姓名".equals(itemDesc) && !"工号".equals(itemDesc))
			    dataList.add(null);
		    }
		  
		    if ("N".equals(itemtype) || "D".equals(itemtype))
		    {
			if (!this.isDataType(decwidth, itemtype, value))
			{
			    msg = "源数据(" + fieldItem.getItemdesc() + ")中数据:" + value + " 不符合格式!";
			    throw new GeneralException(msg);
			}
		    }

		    value = value.trim();
		    if ("dbase".equalsIgnoreCase(itemid) && value.trim().length() > 0)
		    {			
			if(codeItemMap.get(value)==null)
			    break;
			else
			    dbpri = (String) codeItemMap.get(value);  
		    }
			
		    if (jobnumFldValue.length() == 0)
		    {
			if (jobnumFld != null && jobnumFld.trim().length() > 0)
			{
			    if (itemid.equalsIgnoreCase(jobnumFld) && value.trim().length() > 0)
				jobnumFldValue = value;
			} else
			{
			    if ("a0101".equalsIgnoreCase(itemid) && value.trim().length() > 0)
				jobnumFldValue = value;
			}
		    }

		}
		else
		{
		    if (!"人员库".equals(itemDesc) && !"姓名".equals(itemDesc) && !"工号".equals(itemDesc))
			dataList.add(null);
		}
		
	    }
	    
	    if (dbpri==null || (dbpri!=null && dbpri.length() == 0) || jobnumFldValue.length() == 0) // 库前缀和工号字段为空就不继续导入数据了
		continue;
	    else if (dbpri.length() > 0 && jobnumFldValue.length() > 0)
	    {
		String selSql = "select a0100 from " + dbpri + "A01 where "+Sql_switcher.trim(jobnumFld != null && jobnumFld.trim().length() > 0 ? jobnumFld : "a0101") + "='"+jobnumFldValue+"'";
		try
		{
		    RowSet rs = dao.search(selSql);
		    if (rs.next())
			a0100 = rs.getString(1);
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
	    }
	    if (a0100.length() > 0)// 主集中存在这个人才导入数据
	    {
		i9999 = this.getI9999(dbpri + a0100, bonusSet);
		String insertSql = "insert into " + dbpri + bonusSet + "(" + fieldStr + "CreateUserName,CreateTime,i9999,a0100,"+doStatusFld+") values(" + valueStr + "?,?,?,?,?)";
		dataList.add(createUserName);
		dataList.add(java.sql.Date.valueOf(createTime));
		dataList.add(new Integer(i9999));
		dataList.add(a0100);
		dataList.add(codeItemMap.get("51a04v2u无效"));
		try
		{
		    dao.insert(insertSql, dataList);
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
	    }

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

    public int getI9999(String a0100, String bonusSet)
    {

	int i9999 = 1;
	ContentDAO dao = new ContentDAO(this.frameconn);
	StringBuffer strSql = new StringBuffer();
	strSql.append("select "+Sql_switcher.isnull("max(i9999)", "0")+" n  from ");
	strSql.append(a0100.substring(0, 3) + bonusSet);
	strSql.append(" where a0100='");
	strSql.append(a0100.substring(3));
	strSql.append("'");
	int count = 1;
	try
	{
	    RowSet rs = dao.search(strSql.toString());
	    if (rs.next())
		count = rs.getInt(1) + 1;
	} catch (SQLException e)
	{
	    e.printStackTrace();
	}
	i9999 = new Integer(count).intValue();
	return i9999;
    }

    public HashMap getCodeItems(HashMap codesetMap)
    {

	HashMap map = new HashMap();
	if (codesetMap.size() == 0)
	    return map;
	ContentDAO dao = new ContentDAO(this.frameconn);
	StringBuffer buf = new StringBuffer();
	Set keySet = codesetMap.keySet();
	for (Iterator iter = keySet.iterator(); iter.hasNext();)
	{
	    String codesetid = (String) iter.next();
	    buf.append(",'" + codesetid + "'");
	}
	String sql = "select * from codeitem where codesetid in (" + buf.substring(1) + ") order by codesetid";
	try
	{
	    RowSet rs = dao.search(sql);
	    while (rs.next())
		map.put(rs.getString("codesetid") + "a04v2u" + rs.getString("codeitemdesc"), rs.getString("codeitemid"));
	} catch (SQLException e)
	{
	    e.printStackTrace();
	}
	return map;
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
