package com.hjsj.hrms.module.hire.api.v1;

import com.hjsj.hrms.module.hire.businessobject.ResumeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResumeFileTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			String return_code = "success";
			String operate = (String) this.getFormHM().get("operate");
			HashMap<String, Object> return_data = new HashMap<String, Object>();
			ResumeBo bo = new ResumeBo(this.frameconn, this.userView);
			//保存照片
			if("update_photo".equalsIgnoreCase(operate)) {
				String filename = (String) this.getFormHM().get("filename");
				String filetype = (String) this.getFormHM().get("filetype");
				String file = (String) this.getFormHM().get("file");
				file = file.substring(file.indexOf(",")+1);
				filename += "."+filetype;
				return_code = bo.savePhoto(filename, file, "1");
			//保存简历子集附件
			} else if("update_set_file".equalsIgnoreCase(operate)) {
				ArrayList<Object> params = (ArrayList) this.getFormHM().get("params");
				//记录上传失败的文件
				ArrayList<String> fail_name = bo.uploadFiles(params);
				if(fail_name.size()>0) {
				    return_code = "fail";
				}
				return_data.put("fail_name", fail_name);
			//保存其他文件
			} else if("update_file".equalsIgnoreCase(operate)) {
				/* 此处应该已经废弃使用fileuploadservlet 上传*/
				ArrayList<Object> params = (ArrayList) this.getFormHM().get("params");
				ArrayList<String> fail_name = bo.uploadOthFiles(params);
				String fileId = "";
				String encrypt_file_name ="";
				if(fail_name.size()>0)
					return_code = "fail";
				else {
				    
				    Map<String,String> param = bo.getFileId("zp_attachment");
				    fileId = param.get("fileId");
				    encrypt_file_name =param.get("encrypt_file_name");
				    
				} //上传文件成功后获取个人最大文件id
				return_data.put("file_id", fileId==null||"".equals(fileId)?"":PubFunc.encrypt(fileId));
				return_data.put("encrypt_file_name",encrypt_file_name);
				return_data.put("fail_name", fail_name);
			//删除子集附件
			} else if("delete_set_file".equalsIgnoreCase(operate)) {
				ArrayList deleteFileList = (ArrayList) this.getFormHM().get("deleteFileList");
				return_code = bo.deleteFile(deleteFileList);
			//删除简历附件
			} else if("delete_oth_file".equalsIgnoreCase(operate)) {
				ArrayList deleteOthFile = (ArrayList) this.getFormHM().get("deleteFileList");
				return_code = bo.deleteOthFile(deleteOthFile);
			}else if("download_set_file".equalsIgnoreCase(operate)) {
			    String mediaid = (String)this.getFormHM().get("mediaid");
			    Map map = bo.downloadSetFile(mediaid);
			    String filename = (String) map.get("filename");
			    String srcfilename = (String) map.get("filename");
			    return_data.put("filename", filename);
			    return_data.put("displayfilename", srcfilename);
			    return_code = (String) map.get("return_code");
			}else if("updateAttachCodeSetFile".equalsIgnoreCase(operate)){//保存简历分类附件
				/* 此处应该已经废弃使用fileuploadservlet 上传*/
			    ArrayList<Object> params = (ArrayList) this.getFormHM().get("params");
                ArrayList<String> fail_name = bo.uploadAttachCodeSetFiles(params);
                String fileId = "";
                String encrypt_file_name ="";
                if(fail_name.size()>0)
                    return_code = "fail";
                else {
                    
                    Map<String,String> param = bo.getFileId("zp_attachment");
                    fileId = param.get("fileId");
                    encrypt_file_name =param.get("encrypt_file_name");
                    
                } //上传文件成功后获取个人最大文件id
                return_data.put("file_id", fileId==null||"".equals(fileId)?"":PubFunc.encrypt(fileId));
                return_data.put("encrypt_file_name",encrypt_file_name);
                return_data.put("fail_name", fail_name);
			}
			this.getFormHM().put("return_code", return_code);
			this.getFormHM().put("return_data", return_data);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
