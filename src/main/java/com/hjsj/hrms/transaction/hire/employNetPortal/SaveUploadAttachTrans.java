package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeFileBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class SaveUploadAttachTrans extends IBusiness{

	public void execute() throws GeneralException {
		InputStream inputStream=null;
		try
		{
			String a0100 = (String)this.getFormHM().get("a0100");
			if("headHire".equals(a0100)){//如果是猎头招聘进来新增简历,并且当前要保存的不是基本信息的话,提示让其先维护基本信息
				throw new GeneralException(ResourceFactory.getProperty("hire.out.headhunter.preserve.basicInformation"));
			}
			a0100=PubFunc.getReplaceStr(a0100);
			String dbName = (String)this.getFormHM().get("dbName");
			dbName=PubFunc.getReplaceStr(dbName);
			String writeable=(String)this.getFormHM().get("writeable");
			HashMap<Integer, FormFile> form_files = (HashMap<Integer, FormFile>)this.getFormHM().get("attachFiles");
			ArrayList attachCodeSet = (ArrayList)this.getFormHM().get("attachCodeSet");
//			FormFile form_file = (FormFile)this.getFormHM().get("attachFile");
			String userName=(String)this.getFormHM().get("userName");
			userName=PubFunc.getReplaceStr(userName);
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String Marked = (String) hm.get("marked");
			hm.remove("marked");
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=xmlBo.getAttributeValues();
			//简历附件分类
			String attach_codeset = (String) map.get("attachCodeset");
			if(!"end".equals(Marked))
			{	
				for (Integer key : form_files.keySet()) {
					FormFile form_file = form_files.get(key);
					int fsize = form_file.getFileSize();//获取文件大小
					if(!FileTypeUtil.isFileTypeEqual(form_file)){
						throw new GeneralException(ResourceFactory.getProperty("error.common.upload.invalid"));
					}
					
					String maxFileSize = (String)this.getFormHM().get("maxFileSize");
					maxFileSize = StringUtils.isEmpty(maxFileSize)|| "0".equalsIgnoreCase(maxFileSize) ? "10" : maxFileSize;
					if(fsize>Integer.parseInt(maxFileSize)*1024*1024){
						throw new GeneralException("上传附件大小不得超过" + maxFileSize + "M!");
					}
					
					if(form_file!=null&&form_file.getFileData().length>0)
					{
						String filename = form_file.getFileName();
						String filetype = filename.substring(filename.lastIndexOf(".")+1);
						String fileTypes = ",doc,docx,xlsx,xls,rar,zip,ppt,jpg,jpeg,png,gif,bmp,txt,wps,pptx,pdf,";
						if(!fileTypes.contains("," + filetype.toLowerCase() + ",")){
							throw new GeneralException("您上传的文件格式不支持，请选择doc,docx,xls,xlsx,rar,zip,ppt,jpg,jpeg,png,gif,bmp,txt,wps,pptx,pdf等文件格式上传!");						
						}
					}
				}
				
				for (Integer key : form_files.keySet()) {
					FormFile form_file = form_files.get(key);
					
					if(form_file!=null&&form_file.getFileData().length>0)
					{
						String realName = "";
						if(StringUtils.isNotEmpty(attach_codeset)&&!"#".equals(attach_codeset)) {
							HashMap fileInfo = (HashMap) attachCodeSet.get(key);
							realName = (String)fileInfo.get("itemDesc");
						}
						//直接将简历附件保存到文件存放目录  jingq upd 2015.08.05
						ResumeFileBo resumeFileBo = new ResumeFileBo(this.getFrameconn(), this.userView);
						String path = resumeFileBo.getPath(dbName, a0100);
						boolean createResumeFile = resumeFileBo.createResumeFile(a0100,dbName,path,form_file,realName,attach_codeset);
						if(!createResumeFile){
							throw GeneralExceptionHandler.Handle(new Exception("文件上传未成功请联系管理员！"));
						}
					}
					form_file=null;
				}
				this.getFormHM().put("attachFiles",new HashMap<Integer, FormFile>());
				this.getFormHM().put("writeable", writeable);
				this.getFormHM().put("a0100",a0100);
				this.getFormHM().put("dbName",dbName);
				this.getFormHM().put("onlyField",(String)this.getFormHM().get("onlyField"));
				this.getFormHM().put("isOnlyCheck", (String)this.getFormHM().get("isOnlyChecked"));
			}
		}
		catch(Exception e)
		{	
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);  
        }finally{
        	if(inputStream!=null){
        		try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	
        }  
		
	}

}
