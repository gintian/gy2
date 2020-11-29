package com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.transaction;

import com.alibaba.druid.util.StringUtils;
import com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.businessobject.DownAttachUtils;
import com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.businessobject.ExcelDownLoadBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * @Title: ExcelLayoutdownLoadTrans.java
 * @Package com.hjsj.hrms.zcps.templete.htmlmodule.transaction
 * @Description: 人事异动-下载Excle模板布局模板
 * @author songyl
 * @date 2019-12-10
 * @version V76.1
 */
public class ExcelLayoutdownLoadTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        // 及时子集中没有数据，其键值（sub_domain）也是有数据的
        String tabid = (String) this.getFormHM().get("tabid");// 模板id
        //1：下载模板  2：下载上传的文件。
        String downtype = (String) this.getFormHM().get("downtype");
        if("2".equals(downtype)){
        	DownAttachUtils attach=new DownAttachUtils(this.userView, this.frameconn, tabid);
        	//该文件是否存在，如果存在，则返回文件名
        	HashMap fileInfo = attach.checkFileisExists();
        	if(fileInfo.containsKey("errorinfo")){
        		String errorinfo=(String) fileInfo.get("errorinfo");
        		getFormHM().put("errorinfo", errorinfo);
        	}else{
        		String fieldid=(String) fileInfo.get("fieldid");
        		getFormHM().put("fieldid", fieldid);
        	}
        }else{
        	ExcelDownLoadBo downloadBo=new ExcelDownLoadBo(Integer.parseInt(tabid),frameconn,this.userView);
        	String fieldid =downloadBo.outExcel();
        	if(StringUtils.isEmpty(fieldid)){
        		getFormHM().put("errorinfo", "程序运行报错，请联系管理员");
        	}else{
        		getFormHM().put("fieldid", fieldid);
        	}
        }
    }
}