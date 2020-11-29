package com.hjsj.hrms.transaction.performance.achivement.achivementTask;

import com.hjsj.hrms.businessobject.performance.achivement.AchivementTaskBo;
import com.hjsj.hrms.businessobject.performance.achivement.dataCollection.GetdataTrans;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;

import java.io.InputStream;
import java.util.HashMap;

/**
 * <p>Title:ExportExcelTrans.java</p>
 * <p>Description:业绩任务书导入模板数据</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-08-21 10:34:23</p>
 * @author JinChunhai
 * @version 5.0
 */

public class ImportTempDataTrans extends IBusiness
{
	public void execute()throws GeneralException
	{
		InputStream is = null;
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			AchivementTaskBo bo=new AchivementTaskBo(this.getFrameconn(),this.userView);
			String targetid=(String)this.getFormHM().get("target_id");
			String fileName = (String) this.getFormHM().get("filename");//加密后的文件名
            //20/3/5 xus vfs改造
            fileName = PubFunc.decrypt(fileName);
            String fileId = (String) this.getFormHM().get("fileid");
//            String path = (String) this.getFormHM().get("path");//路径
//            path = PubFunc.decrypt(path);
//            String filePath = path + fileName;
//            String localname = (String) this.getFormHM().get("localname");
            try {
            	is = VfsService.getFile(fileId);
            }catch (Exception e) {
            	throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
            
            
            
//            File file = new File(filePath);
//			boolean flag = FileTypeUtil.isFileTypeEqual(file);
//			if(!flag){
//				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
//			}
			String errorname="";
			String okcount="0";
			
			GetdataTrans gt=new GetdataTrans(this.frameconn,this.userView,targetid);
			HashMap errorname1=gt.importname(is,fileName);
			errorname=(String)errorname1.get("errorname");
			okcount=(String)errorname1.get("okcount");
			if(errorname!=null&&errorname.trim().length()>0)
				this.getFormHM().put("errorname", SafeCode.encode(PubFunc.encrypt(errorname)));
			else
				this.getFormHM().put("errorname", errorname);
			this.getFormHM().put("okcount", okcount);
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeIoResource(is);
		}
	}
}