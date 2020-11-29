package com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.transaction;

import com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.businessobject.DownAttachUtils;
import com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.businessobject.ExcelUpLoadBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.json.JSONObject;

import java.io.InputStream;
/**
 * @Title: TemplateUpLoadTrans.java
 * @Package com.hjsj.hrms.zcps.templete.htmlmodule.transaction
 * @Description: 人事异动-上传Excle模板布局
 * @author songyl
 * @date 2019-12-05
 * @version V76.1
 */
public class ExcelLayoutUpLoadTrans extends IBusiness {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public void execute() throws GeneralException {
        String tabid = (String) getFormHM().get("tabid");
        String fileid = (String) getFormHM().get("fileid");// 导入的文件
        String filename=(String)getFormHM().get("filename");
        filename = PubFunc.decrypt(filename);
        //fileid = PubFunc.decrypt(fileid);
        InputStream inputStream=null;
        try {
        	inputStream = VfsService.getFile(fileid);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        
        String ins_id = (String) this.userView.getHm().get("ins_id");// 流程实例号，进入流程之前是0
        ins_id = ins_id != null && ins_id.length() > 0 ? ins_id : "0";
        /**模板号*/
        String moudle_id = (String) getFormHM().get("moudle_id");
        ExcelUpLoadBo excelBo=new ExcelUpLoadBo(Integer.parseInt(tabid),this.frameconn,this.userView);
        excelBo.setMoudle_id(moudle_id);
        
        /**引入Excel布局文件*/
        DownAttachUtils attach=new DownAttachUtils(this.userView, this.frameconn, tabid);
        JSONObject json=excelBo.getLayoutByExcel(inputStream);
        try {
        	inputStream = VfsService.getFile(fileid);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        //保存文件到指定目录
        attach.saveFileToDisk(inputStream);
        /**将json字符串存放在服务器文本上*/
        attach.saveJsonStr(json);
        String errorFileName="";
		this.getFormHM().put("errorFileName", errorFileName);//导入失败提示信息 暂无
		PubFunc.closeIoResource(inputStream);
    }

}