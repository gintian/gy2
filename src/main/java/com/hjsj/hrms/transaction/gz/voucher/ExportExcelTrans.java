package com.hjsj.hrms.transaction.gz.voucher;

import com.hjsj.hrms.businessobject.gz.templateset.tax_table.TaxTableXMLBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
* 
* 类名称：ExportExcelTrans   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 16, 2013 5:43:51 PM   
* 修改人：zhaoxg   
* 修改时间：Aug 16, 2013 5:43:51 PM   
* 修改备注：   科目导入excel
* @version    
*
 */
public class ExportExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		InputStream stream = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			FormFile file = (FormFile)this.getFormHM().get("formfile");
			/* 安全问题 文件上传 财务凭证定义-设置-导入 xiaoyun 2014-9-16 start */
			boolean isOk = FileTypeUtil.isFileTypeEqual(file);
			if(!isOk) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
			/* 安全问题 文件上传 财务凭证定义-设置-导入 xiaoyun 2014-9-16 end */
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
			try(Workbook workbook = WorkbookFactory.create(stream)) {
				Sheet sheet1 = workbook.getSheetAt(0);
				exportInTaxData(sheet1, dao);
			}
			this.getFormHM().put("info",String.valueOf(info));
			this.getFormHM().put("returnInfo",retInfo);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(stream);
		}
	}
	public HashMap exportInTaxData(Sheet sheet,ContentDAO dao) throws GeneralException
	{
	    ArrayList SyscodeList = this.getAllCode(dao);
	    ArrayList ExecodeList = new ArrayList();
		HashMap map = new HashMap();
		try
		{
			Row row = null;
			Cell cell=null;		
			TaxTableXMLBo bo = new TaxTableXMLBo();
			StringBuffer insertSql = new StringBuffer();
			int rows=sheet.getPhysicalNumberOfRows();
			for(int i=2;i<=rows;i++)
			{
				insertSql.append("insert into GZ_code(i_id,ccode,ccode_name,igrade) values("+getMaxid(dao)+"");
				row=sheet.getRow(i);
				int cells=row.getPhysicalNumberOfCells();
				if(cells!=3){
				    throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("gz.voucher.FileFormat.Eroor"),"",""));
				}
				ArrayList list = new ArrayList();
				for(short j=0;j<cells;j++)
				{
					cell=row.getCell(j);
					String value = "";
					if (cell != null)
					{
						switch (cell.getCellType())//根据类型获取值  zhaoxg update 2015-2-4
						{
						case Cell.CELL_TYPE_FORMULA:
							break;
						case Cell.CELL_TYPE_NUMERIC:
							int y = (int)cell.getNumericCellValue();
							value = y+"";
							break;
						case Cell.CELL_TYPE_STRING:
							value = cell.getStringCellValue();
							break;
						default:
							value = "";
						}
					}
					    insertSql.append(",'");
					    if(j==0){
					        String code = value;
					        if(SyscodeList.contains(code)){
					            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("gz.voucher.codeExit"),"",""));
					        }
					        if(ExecodeList.contains(code)){
					            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("gz.voucher.excelCodeExit"),"",""));
					        }
					        ExecodeList.add(code);
					    	insertSql.append(code);
					    }
					    if(j==1)
			    		{
					    	insertSql.append(value);
		    			}
		    			if(j==2)
		    			{
		    				insertSql.append(value);
			    		}
	    				insertSql.append("'");	

				}
				insertSql.append(")");
				dao.insert(insertSql.toString(),list);
				insertSql.setLength(0);
				list.clear();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
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
	public String getMaxid(ContentDAO dao){
		String maxid = "1";
		try
		{
			String sql = "select max("+Sql_switcher.sqlToInt("i_id")+") as max from GZ_code";
			this.frowset=dao.search(sql);
			if(this.frowset.next()){
				maxid=Integer.parseInt(this.frowset.getString("max")==null?"0":this.frowset.getString("max"))+1+"";
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return maxid;
	}
	public ArrayList getAllCode(ContentDAO dao){
	    String sql ="select ccode from gz_code";
	    ArrayList codeList = new ArrayList();
	    try{
	        this.frowset=dao.search(sql);
	        while(this.frowset.next()){
	            codeList.add(this.frowset.getString(1));
	        }
	    }
	    catch(Exception e)
        {
            e.printStackTrace();
        }
	    return codeList;
	}
}
