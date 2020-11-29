package com.hjsj.hrms.transaction.train.setparam;

import com.hjsj.hrms.businessobject.train.MediaServerParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Map;
/**
 * <p>Title:SetMediaServerTrans.java</p>
 * <p>Description:流媒体服务器设置</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2011-08-18 下午13:52:55</p>
 * @author wangzhongjun
 * @version 1.0
 */
public class SetMediaServerTrans extends IBusiness {

	public void execute() throws GeneralException {
		// 链接中的参数
		Map map = (Map) this.getFormHM().get("requestPamaHM");
		
		// 区分查询和更新的标志
		String opt = (String) map.get("opt");
		if ("querry".equalsIgnoreCase(opt)) {
			this.getFormHM().put("filePath", MediaServerParamBo.getFilePath());
			this.getFormHM().put("fileSize", MediaServerParamBo.getFileSize());
			this.getFormHM().put("ftpServerAddress", MediaServerParamBo.getFtpServerAddress());
			this.getFormHM().put("ftpServerPort", MediaServerParamBo.getFtpServerPort());
			this.getFormHM().put("ftpServerPwd", MediaServerParamBo.getFtpServerPwd());
			this.getFormHM().put("ftpServerUserName", MediaServerParamBo.getFtpServerUserName());
			this.getFormHM().put("isDownload", MediaServerParamBo.getIsDownload());
			this.getFormHM().put("mediaServerAddress", MediaServerParamBo.getMediaServerAddress());
			this.getFormHM().put("mediaServerPort", MediaServerParamBo.getMediaServerPort());
			this.getFormHM().put("mediaServerType", MediaServerParamBo.getMediaServerType());
			this.getFormHM().put("mediaServerPubRoot", MediaServerParamBo.getMediaServerPubRoot());
			//this.getFormHM().put("openOfficeAdd", MediaServerParamBo.getOpenOfficeAdd());
			//this.getFormHM().put("openOfficePort", MediaServerParamBo.getOpenOfficePort());
		} else if ("save".equalsIgnoreCase(opt)) {
			String mediaServerType = (String) this.getFormHM().get("mediaServerType");
			String mediaServerAddress = (String) this.getFormHM().get("mediaServerAddress"); 
			String mediaServerPort = (String) this.getFormHM().get("mediaServerPort"); 
			String ftpServerAddress = (String) this.getFormHM().get("ftpServerAddress"); 
			String ftpServerPort = (String) this.getFormHM().get("ftpServerPort"); 
			String ftpServerUserName = (String) this.getFormHM().get("ftpServerUserName"); 
			String ftpServerPwd = (String) this.getFormHM().get("ftpServerPwd"); 
			String filePath = (String) this.getFormHM().get("filePath");
			filePath = PubFunc.keyWord_reback(filePath);
			String fileSize = (String) this.getFormHM().get("fileSize"); 
			String isDownload = (String) this.getFormHM().get("isDownload");
			String mediaServerPubRoot = (String) this.getFormHM().get("mediaServerPubRoot");
			//String openOfficePort = (String) this.getFormHM().get("openOfficePort");
			//String openOfficeAdd = (String) this.getFormHM().get("openOfficeAdd");
			this.getFormHM().put("filePath", filePath);
			MediaServerParamBo.update(mediaServerType, mediaServerAddress, mediaServerPort, ftpServerAddress, ftpServerPort, ftpServerUserName, ftpServerPwd, filePath, fileSize, isDownload,mediaServerPubRoot);
		}
	}
}
