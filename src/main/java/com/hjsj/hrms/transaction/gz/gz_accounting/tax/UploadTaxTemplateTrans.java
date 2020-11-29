package com.hjsj.hrms.transaction.gz.gz_accounting.tax;

import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hjsj.hrms.businessobject.gz.TaxMxExcelBo;
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

public class UploadTaxTemplateTrans extends IBusiness{
	
	public void execute()throws GeneralException 
	{
		String path = SafeCode.decode((String)this.getFormHM().get("path"));
//		System.out.println(path);
		FormFile file=(FormFile)getFormHM().get("tempalefile");
		
		/* 安全问题 文件上传 xiaoyun 2014-9-2 end */
//		System.out.println(file.getFileName());
		String fileName = file.getFileName();
		String filetype = this.getPostfix(fileName);
		TaxMxBo taxbo=new TaxMxBo(this.getFrameconn());	
		InputStream input = null;
		FileOutputStream output = null;
		int t=0;
		try{
			/* 安全问题 所得税管理 文件上传 xiaoyun 2014-9-2 start */
			boolean isOk = FileTypeUtil.isFileTypeEqual(file);
			if(!isOk) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
			/* 安全问题 所得税管理 文件上传 xiaoyun 2014-9-2 end */
			// error :  0.上传成功
			//          1.文件类型不对,无法上传,请选择Excel文件
			//          2.缺乏"所得项目","税率","人数",这三个必须导出的指标,无法上传
			//          3.文件已存在,请更换文件名

				if(this.checkFile(taxbo, file))
				{
					File f=new File(path,fileName); 
					if(f.exists())
					{
						t++;
						this.getFormHM().put("error","3");
					}	
					input = file.getInputStream();
//					f.createNewFile();
					output = new FileOutputStream(path+"//"+fileName);
					byte[] b = new byte[1024];
					int len;
					while((len = input.read(b)) != -1){
						output.write(b,0,len);
					}
					if(t<1)
						this.getFormHM().put("error","0");
				}else
				{
					this.getFormHM().put("error","2");
					return ;
				}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
        } finally {
            if (input != null)
                PubFunc.closeResource(input);
            if (output != null)
                PubFunc.closeResource(output);
        }
		
	}

	/**
	 * 获得后缀
	 * @param filepath
	 * @return
	 */
	public String getPostfix(String filepath)
	{
		String postfix = "";
		int num = filepath.lastIndexOf(".");
		postfix = filepath.substring(num+1,filepath.length());
		return postfix;
	}
	
	public boolean checkFile(TaxMxBo taxbo,FormFile file)
	{
		boolean ret = false;
		InputStream in = null;
		try
		{
			TaxMxExcelBo tmeb=new TaxMxExcelBo(this.frameconn);
			in = file.getInputStream();
			tmeb.getSelfAttribute(in);
			ret=tmeb.checkUploadFile(2);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(in);
		}
		return ret;
	}

	
}
