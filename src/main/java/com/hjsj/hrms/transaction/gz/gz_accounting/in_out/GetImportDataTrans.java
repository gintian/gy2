package com.hjsj.hrms.transaction.gz.gz_accounting.in_out;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:取得</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:9:20:08 AM</p> 
 *@author dengcan
 *@version 4.0
 */
public class GetImportDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		InputStream inputStream = null;
		OutputStream output = null;
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String salaryid=(String)hm.get("salaryid");
			FormFile form_file = (FormFile) getFormHM().get("file");
			/**薪资类别*/
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			
			/* 薪资 安全问题：文件上传漏洞处理 xiaoyun 2014-9-3 start */
			boolean isOk = FileTypeUtil.isFileTypeEqual(form_file);
			if(!isOk) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
			/* 薪资 安全问题：文件上传漏洞处理 xiaoyun 2014-9-3 end */

			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			
			ArrayList originalDataList=gzbo.getOriginalDataFiledList(form_file);
//			String originalDataFile = gzbo.writeFile(form_file.getInputStream());
			inputStream = form_file.getInputStream();
			String originalDataFile = "importGzData_"+PubFunc.getStrg()+".xls";
			File file = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator"), originalDataFile);
			output = new FileOutputStream(file);
			byte[] bt = new byte[1024];
			int read = 0;
			while ((read = inputStream.read(bt)) != -1) {
				output.write(bt, 0, read);
			}
			
//			String originalDataFile = gzbo.writeFile2(inputStream);
			/* 安全问题 文件下载 薪资发放：导入/设置对应指标页面，点击【原始数据】，显示空白页面 xiaoyun 2014-9-19 start */
			originalDataFile = SafeCode.encode(PubFunc.encrypt(originalDataFile));
			/* 安全问题 文件下载 薪资发放：导入/设置对应指标页面，点击【原始数据】，显示空白页面 xiaoyun 2014-9-19 end */
			ArrayList aimDataList=gzbo.getAimDataFieldList();
			
			this.getFormHM().put("originalDataList",originalDataList);
			this.getFormHM().put("aimDataList",aimDataList);
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("relationItemList",new ArrayList());
			this.getFormHM().put("oppositeItemList",new ArrayList());
			this.getFormHM().put("originalDataFile",originalDataFile);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(inputStream);//资源释放  jingq 2014.12.29
			PubFunc.closeResource(output);
		}

	}

}
