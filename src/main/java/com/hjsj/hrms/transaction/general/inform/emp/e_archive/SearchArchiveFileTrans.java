/**
 * 
 */
package com.hjsj.hrms.transaction.general.inform.emp.e_archive;

import com.hjsj.hrms.businessobject.general.ftp.FtpAccessBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 *<p>Title:查询电子档案文件</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-10-11:上午11:33:49</p> 
 *@author cmq
 *@version 4.0
 */
public class SearchArchiveFileTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
		    String foutname=null;
			String filename=(String)this.getFormHM().get("filename");
			FtpAccessBo ftpbo=new FtpAccessBo();
			foutname=ftpbo.getFile(filename,".tif");
			byte[] content=null;
			if(foutname!=null)
				content=PubFunc.fileToByte(foutname);
			this.getFormHM().put("content", content);
			this.getFormHM().put("outfilename",foutname);

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
