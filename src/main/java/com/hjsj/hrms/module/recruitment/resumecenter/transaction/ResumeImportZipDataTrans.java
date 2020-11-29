package com.hjsj.hrms.module.recruitment.resumecenter.transaction;

import com.hjsj.hrms.module.recruitment.interfaces.ResumeInterface;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
/**
 * 简历中心导入交易类 
 * @Title:        ResumeImportZipDataTrans.java
 * @Description:  用于简历中心通过ZIP文件导入简历
 * @Company:      hjsj     
 * @Create time:  2016-5-4 上午11:35:31
 * @author        chenxg
 * @version       1.0
 */
public class ResumeImportZipDataTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        String msg = "ok";
        try {
            //导入简历参数：=0 不忽略异常信息；=1 不忽略异常信息；=2 有异常信息取消上传，删除服务器文件；默认为0
            String importParam = (String) this.getFormHM().get("importParam");
            importParam = StringUtils.isEmpty(importParam) ? "0" : importParam;
            String fileId = (String) getFormHM().get("fileId");  
            //有异常信息取消上传后，删除服务器文件
            if("2".equalsIgnoreCase(importParam)){
                return;
            }
            
            String dbpre = "";
            RecordVo vo= ConstantParamter.getConstantVo("ZP_DBNAME");
            if(vo != null)
                dbpre=vo.getString("str_value");
                
            if(StringUtils.isEmpty(dbpre))
                throw new GeneralException("", "未设置招聘人员库,请到招聘设置-参数设置-后台参数设置招聘人员库！", "", "");
                
            ResumeInterface bo = new ResumeInterface(this.frameconn, this.userView, true, importParam);
            msg = bo.ImportResumeZip(fileId, dbpre);
            if(msg.startsWith("导入完成") || msg.startsWith("文件中没有需要导入的数据"))	
                this.getFormHM().put("flag", "true");
            else if (msg.startsWith("缺少必要"))
                this.getFormHM().put("flag", "error");
            else if (msg.startsWith("["))
                this.getFormHM().put("flag", "false");
            
            this.getFormHM().put("info", msg);
            this.getFormHM().put("filePath", fileId);
        } catch (Exception e) {
            this.getFormHM().put("flag", "error");
            this.getFormHM().put("info", e.toString());
            e.printStackTrace();
        }
    }

}
