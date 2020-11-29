package com.hjsj.hrms.transaction.gz.gz_accounting.tax;

import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class SetImportDataList extends IBusiness{

	public void execute() throws GeneralException 
	{
	    InputStream in = null;
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			FormFile form_file = (FormFile) getFormHM().get("importfile");
			/* 安全问题：文件上传 xiaoyun 2014-9-12 start */
			boolean isOk = FileTypeUtil.isFileTypeEqual(form_file);
			if(!isOk) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
			/* 安全问题：文件上传 xiaoyun 2014-9-12 end */
			TaxMxBo taxbo=new TaxMxBo(this.getFrameconn());
			ArrayList excelDataFiledList=taxbo.getExcelDataFiledList(form_file);
			in = form_file.getInputStream();
			taxbo.writeFile(in);
			ArrayList taxMxField=taxbo.getTaxMxItemList();
			
			this.getFormHM().put("excelDataFiledList",excelDataFiledList);
			this.getFormHM().put("taxMxField",taxMxField);
			this.getFormHM().put("nbaseList",new ArrayList());
			this.getFormHM().put("oppositeItemList",new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
		    if(in != null)
		        PubFunc.closeIoResource(in);
		}
	}
}
