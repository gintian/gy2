package com.hjsj.hrms.transaction.gz.gz_accounting.tax;

import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author ${FengXiBin}
 *@version 4.0
  */
public class ImportExcelDataTrans extends IBusiness{
	
	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			/**人员标识*/
			String[] nbaseItem=(String[])this.getFormHM().get("nbaseItem");   
			/**关联指标*/
			String[] oppositeItem=(String[])this.getFormHM().get("oppositeItem"); 
			FormFile form_file = (FormFile) getFormHM().get("importfile");
			String fromTable=(String)this.getFormHM().get("fromTable");
			if(fromTable==null)
				fromTable="gz_tax_mx";
			/**EXCEL文件列名（字段）信息 excelDataFiledList中封装commondata对象**/
			//ArrayList excelDataFiledList=(ArrayList)this.getFormHM().get("excelDataFiledList");
			/***/
			//ArrayList nbaseList=(ArrayList)this.getFormHM().get("nbaseList");  //源数据 列信息
			TaxMxBo taxbo=new TaxMxBo(this.getFrameconn());
			/* 所得税管理-文件-导入申报明细表 xiaoyun 2014-10-9 start */
			for(int i = 0; i < oppositeItem.length; i++) {
				oppositeItem[i] = PubFunc.keyWord_reback(oppositeItem[i]);
			}
			/* 所得税管理-文件-导入申报明细表 xiaoyun 2014-10-9 end */
			int dataRows=taxbo.importFileDataToTaxMx(nbaseItem, oppositeItem, form_file,this.getUserView(), dao,fromTable);
			this.getFormHM().put("importInfo",ResourceFactory.getProperty("gz.tax.import_data")+"["+dataRows+"]"+ResourceFactory.getProperty("gz.tax.totalrecord"));
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
