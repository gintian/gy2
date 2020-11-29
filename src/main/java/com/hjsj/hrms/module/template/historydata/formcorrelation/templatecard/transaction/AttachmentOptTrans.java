package com.hjsj.hrms.module.template.historydata.formcorrelation.templatecard.transaction;

import com.hjsj.hrms.module.template.historydata.formcorrelation.templatecard.businessobject.AttachmentBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.UUID;

/**
 * 操作附件
* @Title: AttachmentOptTrans
* @Description:
* @author: hej
* @date 2019年11月20日 下午5:43:07
* @version
 */
public class AttachmentOptTrans extends IBusiness {
	
	@Override
	public void execute() throws GeneralException {
		try {
			String record_id = (String)this.getFormHM().get("record_id");
			String archive_year = (String)this.getFormHM().get("archive_year");
			String archive_id = (String)this.getFormHM().get("archive_id");
			String type=(String)this.getFormHM().get("type");
			String attachmenttype = (String)this.getFormHM().get("attachmenttype");
			if("download".equals(type)){//下载文件
				InputStream in = null;
				String tabid = (String)this.getFormHM().get("tabid");
				String file_id =(String)this.getFormHM().get("file_id");
				String isIE =(String)this.getFormHM().get("isIE");
				if(file_id!=null&&file_id.trim().length()>0){
					file_id = PubFunc.decrypt(SafeCode.decode(file_id));
				}
				if(file_id==null||file_id.trim().length()==0){
					this.getFormHM().put("ok", "0");
					return;
				}
				AttachmentBo attachmentBo = new AttachmentBo(userView, frameconn, tabid);
				HashMap fileMap = attachmentBo.downloadFile(file_id,archive_id,record_id,archive_year,attachmenttype);
				String filePath = (String)fileMap.get("filepath");
				in = (InputStream)fileMap.get("ole");
				String srcfilename =attachmentBo.getDestFileName();
				String ext = attachmentBo.getExt().toLowerCase();
				if(StringUtils.isNotBlank(filePath)){
					////归档附件下载 兼容vfs
					if(StringUtils.isNumeric(PubFunc.decrypt(filePath))) {
						this.getFormHM().put("filePath", filePath);
						this.getFormHM().put("ext", ext);
					}else {
						File file = new File(filePath);
						if (!file.exists()) {
							if(in!=null)
								this.saveInputStreamToFile(filePath,attachmentBo,file_id,srcfilename,ext,isIE,in);
							else
								throw new GeneralException("未找到文件" + "(" + srcfilename + ")！");
						}else {
							this.downSuccess(filePath,srcfilename,ext,isIE,file);
						}
					}
					
				}else if(in!=null){
					this.saveInputStreamToFile(filePath,attachmentBo,file_id,srcfilename,ext,isIE,in);
				}else{
					throw new GeneralException("未找到文件！");//"未找到文件！"
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(this.frowset);
		}
	}
	/**
	 * 将二进制转换成路径存储并保存文件
	 * @param filePath 
	 * @param attachmentBo
	 * @param file_id
	 * @param srcfilename
	 * @param ext
	 * @param isIE
	 * @param in 
	 * @throws GeneralException 
	 */
	private void saveInputStreamToFile(String filePath, AttachmentBo attachmentBo, String file_id, String srcfilename, String ext, String isIE, InputStream in) throws GeneralException {
		OutputStream output = null;
		try{
		    attachmentBo.initParam(true);
		    String middlepath = "";
			if("\\".equals(File.separator)){//证明是windows
				middlepath = "subdomain\\template_";
			}else if("/".equals(File.separator)){//证明是linux
				middlepath = "subdomain/template_";
			}
			UUID uuid = UUID.randomUUID();
		    String fileuuidname = uuid.toString();
		    filePath = attachmentBo.getAbsoluteDir(fileuuidname,middlepath)+File.separator+fileuuidname + ext;
		    String rootDir = attachmentBo.getRootDir();
		    String absolutFilePath=attachmentBo.getAbsoluteDir(fileuuidname,middlepath);
		    if(!absolutFilePath.startsWith(rootDir)){
		    	absolutFilePath=rootDir+File.separator+attachmentBo.getAbsoluteDir(fileuuidname,middlepath);
		    }
		    // 保存文件
			File file = new File(absolutFilePath, fileuuidname + ext);
			output = new FileOutputStream(file);
			byte[] bt = new byte[1024];
			int read = 0;
			while ((read = in.read(bt)) != -1) {
				output.write(bt, 0, read);
			}
			
			this.downSuccess(filePath,srcfilename,ext,isIE,file);
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeIoResource(output);
		}
	}
	/**
	 * 下载文件回传信息
	 * @param filePath
	 * @param srcfilename
	 * @param ext
	 * @param isIE
	 * @param file
	 * @throws GeneralException
	 */
	private void downSuccess(String filePath, String srcfilename, String ext, String isIE, File file) throws GeneralException{
		try{
	    	String docExt = ",.docx,.doc,.dot,.xlsx,.xls,.pptx,.ppt,";
	    	if("true".equals(isIE) && docExt.indexOf(","+ext+",") > -1){
	    		srcfilename = PubFunc.encryption(file.getName());
	    	}else{
	    		srcfilename = SafeCode.encode(srcfilename);
	    	}
			filePath = PubFunc.encryption(filePath);
			this.getFormHM().put("filePath", filePath);
			this.getFormHM().put("ext", ext);
			this.getFormHM().put("displayfilename", srcfilename);
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
