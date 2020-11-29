package com.hjsj.hrms.transaction.general.email_template;

import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.util.ArrayList;
import java.util.HashMap;

public class SaveEmailAttachTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String type="1";
			if(map!=null)
			{
				String type_str=(String)map.get("type");
				if(type_str!=null&&!"".equals(type_str))
					type=type_str;
			}
			FormFile file=(FormFile)getFormHM().get("file");
			//String path=(String)this.getFormHM().get("path");
			if(file!=null&&file.getFileData().length>0)
			{
				boolean accept = FileTypeUtil.isFileTypeEqual(file);
				if(accept){
	    	        EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
		    		String templateId=(String)this.getFormHM().get("id");
		    		//String test=file.
		    		//bo.updateAttach(templateId,path);
		    		String attach_id=String.valueOf(bo.getAttachId()+1);
		    		
		    		//xus 20/4/29 vfs 上传改造
		    		String fileid = bo.updFileToVfs(file,this.getUserView().getUserName());
		    		bo.insertEmail_attach(templateId,attach_id,file.getFileName(),fileid);
//		    		bo.updateEmail_attach(file,attach_id);
		    		ArrayList attachlist = bo.getAttachList(templateId);
					this.getFormHM().put("attachlist",attachlist);
		    		this.getFormHM().put("isok",type);
		    		this.getFormHM().put("id",templateId);
				} else {
					throw GeneralExceptionHandler.Handle(new Throwable(ResourceFactory.getProperty("error.fileuploaderror")));
				}
			}else
			{
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.email.attach")));
			}	
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
