package com.hjsj.hrms.transaction.gz.templateset.tax_table;

import com.hjsj.hrms.businessobject.gz.templateset.tax_table.TaxTableSetBo;
import com.hjsj.hrms.businessobject.gz.templateset.tax_table.TaxTableXMLBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ExportInExcelTrans extends IBusiness{
	public void execute() throws GeneralException {
		InputStream stream = null;
		Workbook workbook=null;
		try
		{
			// WritableWorkbook  book=null;jxl
			TaxTableSetBo bo = new TaxTableSetBo(this.getFrameconn());
			FormFile file = (FormFile)this.getFormHM().get("formfile");
			String repeats = (String) this.getFormHM().get("repeats");//具体的覆盖还是追加的信息  zhaoxg add 2014-11-12
			repeats = repeats.substring(1);
			/* 安全问题 文件上传 税率表 导入 xiaoyun 2014-9-16 start */
			boolean isOk = FileTypeUtil.isFileTypeEqual(file);
			if(!isOk) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
			/* 安全问题 文件上传 税率表 导入 xiaoyun 2014-9-16 end */
			int info = 0;
			String retInfo="";
			if(file==null||file.getFileData().length==0)
			{
				info=2;
				this.getFormHM().put("info",String.valueOf(info));
				this.getFormHM().put("retInfo","您选择的文件是个空文件");
				return;
			}

			stream = file.getInputStream();
			workbook = WorkbookFactory.create(stream);
			
//			HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
			int num=workbook.getNumberOfSheets();
			if(num<2)
			{
				info=3;
				retInfo="由于您选择的文件不是税率表文件,导入失败";
				this.getFormHM().put("info",String.valueOf(info));
				this.getFormHM().put("returnInfo",retInfo);
				return;
			}
//			HSSFSheet sheet1=workbook.getSheetAt(0);
//			HSSFSheet sheet2=workbook.getSheetAt(1);
			
			Sheet sheet1=workbook.getSheetAt(0);
			Sheet sheet2=workbook.getSheetAt(1);
			
			if(sheet1==null||sheet2==null)
			{
				info=3;
				retInfo="由于您选择的文件不是税率表文件,导入失败";
				this.getFormHM().put("info",String.valueOf(info));
				this.getFormHM().put("returnInfo",retInfo);
				return;
			}
			String sheet1name=workbook.getSheetName(0);
			String sheet2name=workbook.getSheetName(1);
			if(!"税率表".equalsIgnoreCase(sheet1name))
			{
				info=1;
				retInfo="税率表";
				this.getFormHM().put("info",String.valueOf(info));
				this.getFormHM().put("returnInfo","EXCEL中没找到:"+retInfo);
				return;
			}
			if(!"税率表明细".equalsIgnoreCase(sheet2name))
			{
				info=1;
				retInfo="税率表明细";
				this.getFormHM().put("info",String.valueOf(info));
				this.getFormHM().put("returnInfo","EXCEL中没找到:"+retInfo);
				return;
			}
			ArrayList taxidsList = new ArrayList();
			HashMap taxidsMap = new HashMap();
			LazyDynaBean bean=null;
			String[] taxids = repeats.split(",");
			for(int i=0;i<taxids.length;i++){
				bean = new LazyDynaBean();
				String[] _ids = taxids[i].split("`");
				bean.set("taxflag", _ids[1]);
				bean.set("oldid", _ids[2]);
				taxidsMap.put(_ids[0], bean);
			}
			int maxTaxId=bo.getTaxId("gz_tax_rate","taxid");
			int maxDetailId=bo.getTaxId("gz_taxrate_item","taxitem");
			HashMap map = exportInTaxData2(sheet1,maxTaxId,taxidsMap);
			exportInTaxDetailData2(sheet2,map,maxDetailId,taxidsMap);
			this.getFormHM().put("info",String.valueOf(info));
			this.getFormHM().put("returnInfo",retInfo);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(workbook);
			PubFunc.closeIoResource(stream);
		}
		
	}
	public HashMap exportInTaxData2(Sheet sheet,int taxMaxId,HashMap taxidsMap)
	{
		HashMap map = new HashMap();
		try
		{
			Row row = null;
			Cell cell=null;		
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			StringBuffer insertSql = new StringBuffer();
			int rows=sheet.getPhysicalNumberOfRows();
			LazyDynaBean bean = null;
			rows:for(int i=1;i<rows;i++)
			{
				bean = new LazyDynaBean();
				insertSql.append("insert into gz_tax_rate(taxid,description,k_base,param) values(?,?,?,?)");
				row=sheet.getRow(i);
				int cells=row.getPhysicalNumberOfCells();
				ArrayList list = new ArrayList();
				for(short j=0;j<cells;j++)
				{
					cell=row.getCell(j);
					if(j==0)
					{
						bean = (LazyDynaBean) taxidsMap.get(cell.getStringCellValue());
						if(bean==null){
							insertSql.setLength(0);
							continue rows;
						}
						String taxflag = (String) bean.get("taxflag");
						if("0".equals(taxflag)){
				    		if(getCellDataType(cell.getCellType())==1)
				    		{
			    				map.put(cell.getStringCellValue(),String.valueOf(taxMaxId));
			    			}
			    			if(getCellDataType(cell.getCellType())==2)
			    			{
				    			map.put(String.valueOf(/*Integer.parseInt(cell.getStringCellValue()))*/(int)cell.getNumericCellValue()),String.valueOf(taxMaxId));
				    		}
			    			list.add(String.valueOf(taxMaxId));
						}else{
				    		if(getCellDataType(cell.getCellType())==1)
				    		{
			    				map.put(cell.getStringCellValue(),(String)bean.get("oldid"));
			    			}
			    			if(getCellDataType(cell.getCellType())==2)
			    			{
				    			map.put(String.valueOf((int)cell.getNumericCellValue()),(String)bean.get("oldid"));
				    		}
							insertSql.setLength(0);
							insertSql.append("update gz_tax_rate set description=?,k_base=?,param=? where taxid='"+(String)bean.get("oldid")+"'");
						}	    			
					}
					else if(j==3)
					{
						String param = getParam(cell.getStringCellValue());
						list.add(param);
					}
					else
					{
					    if(getCellDataType(cell.getCellType())==1)
			    		{
					    	list.add(cell.getStringCellValue());
		    			}
		    			if(getCellDataType(cell.getCellType())==2)
		    			{
		    				list.add(cell.getNumericCellValue()+"");
			    		}
					}
				}
				if("0".equals(bean.get("taxflag"))){
					dao.insert(insertSql.toString(),list);
					taxMaxId++;
				}else{
					dao.update(insertSql.toString(),list);
				}				
				insertSql.setLength(0);
				list.clear();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public HashMap exportInTaxData(HSSFSheet sheet,int taxMaxId)
	{
		HashMap map = new HashMap();
		try
		{
			HSSFRow row = null;
			HSSFCell cell=null;
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			TaxTableXMLBo bo = new TaxTableXMLBo();
			StringBuffer insertSql = new StringBuffer();
			int rows=sheet.getPhysicalNumberOfRows();
			for(int i=1;i<rows;i++)
			{
				insertSql.append("insert into gz_tax_rate(taxid,description,k_base,param) values(");
				row=sheet.getRow(i);
				int cells=row.getPhysicalNumberOfCells();
				ArrayList list = new ArrayList();
				for(short j=0;j<cells;j++)
				{
					cell=row.getCell(j);
					if(j==0)
					{
			    		if(getCellDataType(cell.getCellType())==1)
			    		{
		    				map.put(cell.getStringCellValue(),String.valueOf(taxMaxId));
		    			}
		    			if(getCellDataType(cell.getCellType())==2)
		    			{
			    			map.put(String.valueOf(/*Integer.parseInt(cell.getStringCellValue()))*/(int)cell.getNumericCellValue()),String.valueOf(taxMaxId));
			    		}
		    			insertSql.append(String.valueOf(taxMaxId));
		    			
					}
					else if(j==3)
					{
						String param = getParam(cell.getStringCellValue());
						insertSql.append(",");
						insertSql.append("?");
						insertSql.append("");
						list.add(param);
					}
					else
					{
					    insertSql.append(",'");
					    if(getCellDataType(cell.getCellType())==1)
			    		{
					    	insertSql.append(cell.getStringCellValue());
		    			}
		    			if(getCellDataType(cell.getCellType())==2)
		    			{
		    				insertSql.append(cell.getNumericCellValue());
			    		}
	    				insertSql.append("'");	
					}
				}
				insertSql.append(")");
				dao.insert(insertSql.toString(),list);
				taxMaxId++;
				insertSql.setLength(0);
				list.clear();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public int getCellDataType(int celltype)
	{
		int n=1;
		try
		{
			switch(celltype)
			{
			case HSSFCell.CELL_TYPE_NUMERIC:
			    n=2;
			    break;
			case HSSFCell.CELL_TYPE_STRING:
				n=1;
				break;
			case HSSFCell.CELL_TYPE_BLANK:
				break;
			case HSSFCell.CELL_TYPE_BOOLEAN:
				break;
			case HSSFCell.CELL_TYPE_ERROR:
				break;
			case HSSFCell.CELL_TYPE_FORMULA:
				break;
			default:
				n=1;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return n;
	}
	public void exportInTaxDetailData2(Sheet sheet,HashMap map,int maxId,HashMap taxidsMap)
	{
		try
		{
			Row row = null;
			Cell cell=null;
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			HashMap tempMap = new HashMap();//用于记住哪些明细表删除过，删除过就不在删除
			StringBuffer insertSql = new StringBuffer();
			int rows=sheet.getPhysicalNumberOfRows();
			LazyDynaBean bean = null;
			rows:for(int i=1;i<rows;i++)
			{
				insertSql.append("insert into gz_taxrate_item(taxid,taxitem,ynse_down,ynse_up,sl,sskcs,flag,description,kc_base) values(");
				row=sheet.getRow(i);
				int cells=row.getPhysicalNumberOfCells();
				for(short j=0;j<cells;j++)
				{
					cell=row.getCell(j);
					if(j==0)
					{						
						bean = (LazyDynaBean) taxidsMap.get(cell.getStringCellValue());
						if(bean==null){
							insertSql.setLength(0);
							continue rows;
						}
						String taxflag = (String) bean.get("taxflag");
						if("0".equals(taxflag)){
							
						}else{
							String taxid = "";
							if(getCellDataType(cell.getCellType())==1)
							{
								taxid = cell.getStringCellValue();
							}
							if(getCellDataType(cell.getCellType())==2)
							{
								taxid = String.valueOf((int)cell.getNumericCellValue());
							}
							if(tempMap.get(taxid)==null){
								dao.delete("delete from gz_taxrate_item where taxid='"+taxid+"'", new ArrayList());
								tempMap.put(taxid, taxid);
							}							
						}
						if(getCellDataType(cell.getCellType())==1)
						{
							insertSql.append((String)map.get(cell.getStringCellValue()));
						}
						if(getCellDataType(cell.getCellType())==2)
						{
							insertSql.append((String)map.get(String.valueOf((int)cell.getNumericCellValue())));
						}
					}
					else if(j==1)
					{
						insertSql.append(",");
						insertSql.append(String.valueOf(maxId));
					}
					else if(j == 6) 
					{
						insertSql.append(",");
						insertSql.append(getFlagState(cell.getStringCellValue()));
					}
					else
					{
					   
					    if(j==7)
			    		{
					    	insertSql.append(",'");
					    	insertSql.append(cell.getStringCellValue()==null?"":cell.getStringCellValue());
					    	insertSql.append("'");
		    			}
					    else
		    			{
					    	if(cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC)
					    	{
					    		insertSql.append(",");
		    	    			insertSql.append(cell.getNumericCellValue());
					    	}
					    	else
					    	{
					    		insertSql.append(",'");
		    	    			insertSql.append((cell.getStringCellValue()==null|| "".equals(cell.getStringCellValue()))?"0":cell.getStringCellValue());
		    	    			insertSql.append("'");
					    	}
					    	
			    		}
	    					
					}
				}
				insertSql.append(")");
				dao.insert(insertSql.toString(),new ArrayList());
				maxId++;
				insertSql.setLength(0);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 上限封闭和下线封闭导入式换成标识
	 * @param value
	 */
	private int getFlagState(String value) {
		int val = 0;// 默认上限封闭
		if(value.equals(ResourceFactory.getProperty("jx.param.downmargin"))) {
			// 下限封闭
			val = 1;
		}
		return val;
	}
	
	public void exportInTaxDetailData(HSSFSheet sheet,HashMap map,int maxId)
	{
		try
		{
			HSSFRow row = null;
			HSSFCell cell=null;
			ContentDAO dao = new ContentDAO(this.getFrameconn());
		
			StringBuffer insertSql = new StringBuffer();
			int rows=sheet.getPhysicalNumberOfRows();
			for(int i=1;i<rows;i++)
			{
				insertSql.append("insert into gz_taxrate_item(taxid,taxitem,ynse_down,ynse_up,sl,sskcs,flag,description,kc_base) values(");
				row=sheet.getRow(i);
				int cells=row.getPhysicalNumberOfCells();
				for(short j=0;j<cells;j++)
				{
					cell=row.getCell(j);
					if(j==0)
					{
						if(getCellDataType(cell.getCellType())==1)
						{
							insertSql.append((String)map.get(cell.getStringCellValue()));
						}
						if(getCellDataType(cell.getCellType())==2)
						{
							insertSql.append((String)map.get(String.valueOf((int)cell.getNumericCellValue())));
						}
					}
					else if(j==1)
					{
						insertSql.append(",");
						insertSql.append(String.valueOf(maxId));
					}
					else
					{
					   
					    if(j==7)
			    		{
					    	insertSql.append(",'");
					    	insertSql.append(cell.getStringCellValue()==null?"":cell.getStringCellValue());
					    	insertSql.append("'");
		    			}
					    else
		    			{
					    	if(cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC)
					    	{
					    		insertSql.append(",");
		    	    			insertSql.append(cell.getNumericCellValue());
					    	}
					    	else
					    	{
					    		insertSql.append(",'");
		    	    			insertSql.append((cell.getStringCellValue()==null|| "".equals(cell.getStringCellValue()))?"0":cell.getStringCellValue());
		    	    			insertSql.append("'");
					    	}
					    	
			    		}
	    					
					}
				}
				insertSql.append(")");
				dao.insert(insertSql.toString(),new ArrayList());
				maxId++;
				insertSql.setLength(0);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	public String getParam(String codedesc)
	{
		String param = "";
		try
		{
			TaxTableXMLBo bo = new TaxTableXMLBo();
		    String codevalue="";
		    ArrayList list = AdminCode.getCodeItemList("46");
		    for(int i = 0; i < list.size(); i++) {
		    	CodeItem codeitem = (CodeItem) list.get(i);
		    	String codeitemdesc = codeitem.getCodename();
		    	if(codedesc.equalsIgnoreCase(codeitemdesc)) {
		    		codevalue=codeitem.getCodeitem();
		    		break;
		    	}
		    }
			param=bo.getParam(codevalue);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return param;
	}

}
