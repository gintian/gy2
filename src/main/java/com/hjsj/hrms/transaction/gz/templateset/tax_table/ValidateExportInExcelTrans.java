package com.hjsj.hrms.transaction.gz.templateset.tax_table;

import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 初始化税率表导入
 * @author zhaoxg 2014-11-12
 *
 */
public class ValidateExportInExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
	    InputStream is = null;
		Workbook workbook=null;
		try
		{
			FormFile file = (FormFile)this.getFormHM().get("formfile");
			boolean isOk = FileTypeUtil.isFileTypeEqual(file);
			if(!isOk) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
			int info = 0;
			String retInfo="";
			if(file==null||file.getFileData().length==0)
			{
				info=2;
				this.getFormHM().put("info",String.valueOf(info));
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.select.isnull")));
//				this.getFormHM().put("retInfo","您选择的文件是个空文件");
			}
			is = file.getInputStream();
			workbook = WorkbookFactory.create(is);
			int num=workbook.getNumberOfSheets();
			if(num<2)
			{
				info=3;
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.select.isNotTax")));
//				retInfo="由于您选择的文件不是税率表文件,导入失败";
//				this.getFormHM().put("info",String.valueOf(info));
//				this.getFormHM().put("returnInfo",retInfo);
			}		
			Sheet sheet1=workbook.getSheetAt(0);
			Sheet sheet2=workbook.getSheetAt(1);			
			if(sheet1==null||sheet2==null)
			{
				info=3;
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.select.isNotTax")));
//				retInfo="由于您选择的文件不是税率表文件,导入失败";
//				this.getFormHM().put("info",String.valueOf(info));
//				this.getFormHM().put("returnInfo",retInfo);
//				return;
			}
			String sheet1name=workbook.getSheetName(0);
			if(!"税率表".equalsIgnoreCase(sheet1name))
			{
				info=1;
				retInfo="税率表";
				throw GeneralExceptionHandler.Handle(new Exception("EXCEL中没找到:"+retInfo));
//				this.getFormHM().put("info",String.valueOf(info));
//				this.getFormHM().put("returnInfo","EXCEL中没找到:"+retInfo);
//				return;
			}

			Row row = null;
			Cell cell=null;		
			LazyDynaBean bean=null;
			ArrayList list = new ArrayList();
			int rows=sheet1.getPhysicalNumberOfRows();
			HashMap map = getTaxid();
			for(int i=1;i<rows;i++)
			{
				bean=new LazyDynaBean();
				row=sheet1.getRow(i);
				int cells=row.getPhysicalNumberOfCells();
				for(short j=0;j<cells;j++)
				{
					cell=row.getCell(j);
					if(j==0){
						bean.set("id", cell.getStringCellValue());
					}else if(j==1){
						bean.set("name", cell.getStringCellValue());
					}					
				}
				if(map.get(bean.get("id"))!=null){
					bean.set("taxflag", "1");//库里有 既能追加也能覆盖
					bean.set("oldid",bean.get("id"));				
				}else{
					bean.set("taxflag", "0");//库里没有只能追加
					bean.set("oldid","add");
				}			
				list.add(bean);
			}
			this.getFormHM().put("validateinfo",String.valueOf(info));
			this.getFormHM().put("validatereturnInfo",retInfo);
			this.getFormHM().put("validateList", list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally {
			PubFunc.closeResource(workbook);
		    PubFunc.closeResource(is);
		}
	}
	/**
	 * 判断库里是否已经存在对应的税率表号
	 * @return
	 */
	public HashMap getTaxid()
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select taxid from gz_tax_rate";
			ContentDAO dao = new ContentDAO(this.frameconn);
			RowSet rs = null;
			rs = dao.search(sql);
			while(rs.next())
			{
				map.put(rs.getString("taxid"),rs.getString("taxid"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

}
