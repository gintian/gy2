package com.hjsj.hrms.businessobject.gz;


import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import javax.sql.RowSet;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Nov 7, 2007</p> 
 *@author fxb
 *@version 4.0
 */
public class TaxMxExcelBo {
	private Connection conn=null;
	private HSSFWorkbook wb=null;
	private HSSFSheet sheet=null;
	private HSSFCellStyle style=null;
	private HSSFCellStyle style_l=null;
	private HSSFCellStyle style_r=null;
	short rowNum=1;
	
	private Workbook wb2=null;
	private Sheet sheet2=null;
	
	public TaxMxExcelBo(Connection con)
	{
		this.conn=con;
	}
	
	/**
	  * 初始化 导入文件信息
	  * @param inputStream
	  */
	 public void getSelfAttributeTwo(FileInputStream fileInputStream)
	 {
		 try
		 {
			 this.wb= new HSSFWorkbook(fileInputStream);
			 this.sheet=this.wb.getSheetAt(0);
		 }
		 catch (Exception e) {
				System.out.println(e);
		}
	 }
	 /**
	  * 初始化 导入文件信息
	  * @param inputStream
	  */
	public void getSelfAttribute(InputStream inputStream) {
		try {
			this.wb2 = WorkbookFactory.create(inputStream);
			this.sheet2 = this.wb2.getSheetAt(0);
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			PubFunc.closeResource(inputStream);
		}
	}
	
	 /**
	 * 获取ItemDesc
	 * @param itemdesc
	 * @param dao
	 * @return
	 */
	public String getItemDesc(String itemdesc)
	{
		RowSet rs;
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sql = new StringBuffer();
		sql.append(" select itemid,fieldsetid from fielditem");
		sql.append(" where itemdesc like '"+itemdesc+"'");
		String retstr = itemdesc;
		try
		{
			rs = dao.search(sql.toString());
			if(rs.next())
			{
				if("a01".equalsIgnoreCase(rs.getString("fieldsetid")))
				{
					retstr = "-"+itemdesc;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return retstr;
	}
	 /**
	  * 取得 导入excel表里 rowNum行所有的信息
	  * @param rowNum
	  * @return
	  */
	public ArrayList getRowAllInfo(int rowNum)throws GeneralException
	{
		ArrayList list=new ArrayList();
//		int rows = this.sheet.getPhysicalNumberOfRows();
		int rows = this.sheet2.getPhysicalNumberOfRows();
		if(rowNum>rows)
			return list;
		Row row = sheet2.getRow(rowNum);
		if (row != null) {
			int cells = row.getPhysicalNumberOfCells();
			for (short c = 0; c < cells; c++) {
				String value = "";
				Cell cell = row.getCell(c);
				if (cell != null) {
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_FORMULA:
						//   
						break;
					case Cell.CELL_TYPE_NUMERIC:
						value= String.valueOf((long) cell.getNumericCellValue());
						break;
					case Cell.CELL_TYPE_STRING:
						value= cell.getStringCellValue();
						break;
					default:
						value= "";
					}
				}
				if("".equals(value))
					throw GeneralExceptionHandler.Handle(new Exception("文件格式不正确，第一行为导入的文件列名，不能为空"));
				String itemdesc = this.getItemDesc(value.toUpperCase());
				list.add(new CommonData(itemdesc,value.toUpperCase()));
			}
		}
		return list;
	}
	
	public String getTemplateTitle(int rowNum) throws GeneralException
	{
		String title = "";
		int rows =this.sheet.getPhysicalNumberOfRows();
		if(rows<1)
			return title;
		HSSFRow row = sheet.getRow(rowNum);
		if(row!=null)
		{
			int cells = row.getPhysicalNumberOfCells();
			HSSFCell cell = row.getCell((short)0);
			if(cell!=null)
				if(cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC)
				{
					title =PubFunc.round(cell.getNumericCellValue()+"",0);
				}else if(cell.getCellType()==HSSFCell.CELL_TYPE_STRING)
	        		title = cell.getStringCellValue();
				else if(cell.getCellType()==HSSFCell.CELL_TYPE_FORMULA)
					title=cell.getCellFormula();
		}
		return title;
	}
	/**
	  * 取得 导入excel表里 rowNum行所有的信息
	  * @param rowNum
	  * @return
	  */
	public ArrayList getExportFieldList(int rowNum)throws GeneralException
	{
		ArrayList list=new ArrayList();
		int rows = this.sheet.getPhysicalNumberOfRows();
		if(rows<1)
			return list;
		HSSFRow row = sheet.getRow(rowNum);
		if (row != null) {
			int cells = row.getPhysicalNumberOfCells();
			for (short c = 0; c < cells; c++) {
				String value = "";
				HSSFCell cell = row.getCell(c);
				if (cell != null) {
					switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_FORMULA:
						//   
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						value= String.valueOf((long) cell.getNumericCellValue());
						break;
					case HSSFCell.CELL_TYPE_STRING:
						value= cell.getStringCellValue();
						break;
					default:
						value= "";
					}
				}
//				if(value.equals(""))
//					throw GeneralExceptionHandler.Handle(new Exception("文件格式不正确，第一行为导入的文件列名，不能为空"));
				String itemid = this.getExportField(value.toUpperCase());
				if(!(itemid==null || "".equals(itemid)))
					list.add(itemid);
			}
		}
		return list;
	}
	
	/**
	  * 取得 导入excel表里 rowNum行所有的信息
	  * @param rowNum
	  * @return
	  */
	public boolean checkUploadFile(int rowNum)throws GeneralException
	{
		boolean ret = false;
		ArrayList list=new ArrayList();
		int one=0;
		int two=0;
		int three=0;
//		int rows = this.sheet.getPhysicalNumberOfRows();
		int rows = this.sheet2.getPhysicalNumberOfRows();
		if(rows<1)
			return ret;
//		HSSFRow row = sheet.getRow(rowNum);
		Row row = sheet2.getRow(rowNum);
		if (row != null) {
			int cells = row.getPhysicalNumberOfCells();
			for (short c = 0; c < cells; c++) {
				String value = "";
				Cell cell = row.getCell(c);
				if (cell != null) {
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_FORMULA:
						//   
						break;
					case Cell.CELL_TYPE_NUMERIC:
						value= String.valueOf((long) cell.getNumericCellValue());
						break;
					case Cell.CELL_TYPE_STRING:
						value= cell.getStringCellValue();
						break;
					default:
						value= "";
					}
				}
//				if(value.equals(""))
//					throw GeneralExceptionHandler.Handle(new Exception("文件格式不正确，第一行为导入的文件列名，不能为空"));
				if("所得项目".equalsIgnoreCase(value.toUpperCase()))
					one++;
				if("税率".equalsIgnoreCase(value.toUpperCase()))
					two++;
				if("人数".equalsIgnoreCase(value.toUpperCase()))
					three++;
				
			}
		}
		if(one>0 && two>0 && three>0)
			ret = true;
		return ret;
	}
	/**
	 * 获取ItemDesc
	 * @param itemdesc
	 * @param dao
	 * @return
	 */
	public boolean checkNewField(String itemdesc)
	{
		boolean ret = false;		
		RowSet rs;
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sql = new StringBuffer();	
		try
		{
			
			if(!("所得项目".equalsIgnoreCase(itemdesc) || "税率".equalsIgnoreCase(itemdesc)
					 || "人数".equalsIgnoreCase(itemdesc) || "计税金额".equalsIgnoreCase(itemdesc)
					 || "纳税额".equalsIgnoreCase(itemdesc)))
			{
				sql.append(" select * from fielditem");
				sql.append(" where itemdesc like '"+itemdesc+"'");
				
				rs = dao.search(sql.toString());
				if(rs.next())
				{
					if("A".equalsIgnoreCase(rs.getString("itemtype")))
					{
						String itemid = rs.getString("itemid");
						TaxMxBo taxbo=new TaxMxBo(this.conn);
						ArrayList list = taxbo.getFieldlist();
						for(int i=0;i<list.size();i++)
						{
							Field fi = (Field)list.get(i);
							if(fi!=null)
							{
								if(itemid.equalsIgnoreCase(fi.getName()))
								{
									ret = true;
									break ;
								}
							}
							
						}
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
	/**
	 * 获取ItemDesc
	 * @param itemdesc
	 * @param dao
	 * @return
	 */
	public String getExportField(String itemdesc)
	{
		String ret = "";		
		RowSet rs;
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sql = new StringBuffer();	
		TaxMxBo taxbo=new TaxMxBo(this.conn);
		ArrayList temp = taxbo.getFieldlist();
		HashMap fieldmap = taxbo.getFieldMap(temp);
		try
		{
			if(!("所得项目".equalsIgnoreCase(itemdesc) || "税率".equalsIgnoreCase(itemdesc)
					 || "人数".equalsIgnoreCase(itemdesc)))
			{

				if("计税金额".equalsIgnoreCase(itemdesc))
				{
					ret="jtynse";
				}else if("纳税额".equalsIgnoreCase(itemdesc))
				{
					ret="jtSds";
				}else
				{
					if(fieldmap.containsKey(itemdesc))
					{
						Field fi = (Field)fieldmap.get(itemdesc);
						if(fi.getDataType()==DataType.INT || fi.getDataType()==DataType.FLOAT
							|| fi.getDataType()==DataType.DOUBLE)
						{
							ret=fi.getName();
						}
						
					}
	//				else
	//				{
	//					sql.append(" select * from fielditem");
	//					sql.append(" where itemdesc like '"+itemdesc+"'");				
	//					rs = dao.search(sql.toString());
	//					if(rs.next())
	//					{
	//						if(rs.getString("itemtype").equalsIgnoreCase("N"))
	//						{
	//							String itemid = rs.getString("itemid");						
	//							ArrayList list = taxbo.getFieldlist();
	//							for(int i=0;i<list.size();i++)
	//							{
	//								Field fi = (Field)list.get(i);
	//								if(fi!=null)
	//								{
	//									if(itemdesc.equalsIgnoreCase(fi.getLabel()))
	//									{
	//										ret=itemid;
	//										break;
	//									}
	//								}
	//								
	//							}
	//						}
	//					}
	//				}
	
				}
				
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
		
	}
	/**
	  * 取得 导入excel表里 rowNum行所有的信息
	  * @param rowNum
	  * @return
	  */
	public ArrayList getRowFirstInfo(int rowNum)throws GeneralException
	{
		ArrayList list=new ArrayList();
//		int rows = this.sheet.getPhysicalNumberOfRows();
		int rows = this.sheet2.getPhysicalNumberOfRows();
		if(rowNum>rows)
			return list;
		Row row = sheet2.getRow(rowNum);
		if (row != null) {
			int cells = row.getPhysicalNumberOfCells();
			for (short c = 0; c < cells; c++) {
				String value = "";
				Cell cell = row.getCell(c);
				if (cell != null) {
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_FORMULA:
						//   
						break;
					case Cell.CELL_TYPE_NUMERIC:
						value= String.valueOf((long) cell.getNumericCellValue());
						break;
					case Cell.CELL_TYPE_STRING:
						value= cell.getStringCellValue();
						break;
					default:
						value= "";
					}
				}
				if("".equals(value))
					throw GeneralExceptionHandler.Handle(new Exception("文件格式不正确，第一行为导入的文件列名，不能为空"));
				list.add(new CommonData(value.toUpperCase(),value.toUpperCase()));
			}
		}
		return list;
	}
	/**
	 * 取得excel 数据的行数
	 * @return
	 */
	public int getTotalDataRows()
	{
		return this.sheet.getPhysicalNumberOfRows()-1;
	}
	public int getTotalDataRows2()
	{
		return this.sheet2.getPhysicalNumberOfRows()-1;
	}
	/**
	 * 获得要导入的所有数据 
	 * @param fromRow
	 * @param toRow
	 * @param columnDataList
	 * @return
	 */
	public HashMap getImportData(int allRow,ArrayList columnDataList,String[] nbaseItem,ArrayList allImportDataList)
	{
		HashMap keyMap = new HashMap();
		for(int i=0;i<nbaseItem.length;i++)
		{
			keyMap.put(nbaseItem[i],nbaseItem[i]);
		}
		String mapkey = nbaseItem[0];
		
		HashMap retmap = new HashMap();
		ArrayList list=new ArrayList();
		try
		{
			LazyDynaBean abean=null;
			CommonData data=null;
			for (int r = 1; r <= allRow; r++) {
//				HSSFRow row = sheet.getRow(r);
				Row row = sheet2.getRow(r);
				if (row != null) {
					StringBuffer getkey = new StringBuffer();
					abean = new LazyDynaBean();
					for (short c = 0; c < columnDataList.size(); c++) {
						data = (CommonData) columnDataList.get(c);
						String columnName = data.getDataValue();
						String value = "";
						Cell cell = row.getCell(c);
						if (cell != null) {
							switch (cell.getCellType()) {
							case Cell.CELL_TYPE_FORMULA:
								//   
								break;
							case Cell.CELL_TYPE_NUMERIC:
								value = String.valueOf( cell
										.getNumericCellValue());
								break;
							case Cell.CELL_TYPE_STRING:
								value = cell.getStringCellValue();
								break;
							default:
								value = "";
							} 
						}
						// 如果人员标识列，去其值作为Map的Key，如身份证号码
						if(keyMap.containsKey(columnName))
						{
							getkey.append(value.trim());
						}
						abean.set(columnName, value.trim());						
					}
					retmap.put(getkey.toString(),abean);
					allImportDataList.add(abean);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return retmap;
	}
}
