package com.hjsj.hrms.module.workplan.plantask.transaction;

import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.module.workplan.plantask.businessobject.ExportWorkPlanExcelBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.InputStream;
import java.util.ArrayList;


/**
 * OKR-工作计划-导入计划
 * <p>Title: ImportWorkPlanTaskTrans </p>
 * <p>create time  2017-9-28 上午10:46:31</p>
 * @author linbz
 */

public class ImportWorkPlanTaskTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
    	InputStream in  = null;
        ArrayList<String> importMsgList = new ArrayList<String>();//存放返回信息
        try{
            //每次导入前先把提示消息清空
            this.getFormHM().put("importMsg", "");
            
            String objectid = (String) (formHM.get("objectId")==null ? "" : formHM.get("objectId")); // 对象id: usr00000019
            objectid = WorkPlanUtil.decryption(objectid);
            String p0700 = (String) this.getFormHM().get("p0700");//p0700
            p0700 = WorkPlanUtil.decryption(p0700);
            String periodType = (String) this.getFormHM().get("periodType");
			String periodYear =  this.getFormHM().get("periodYear")==null ? "" : (String)this.getFormHM().get("periodYear");
			String periodMonth = this.getFormHM().get("periodMonth")==null ? "" : (String)this.getFormHM().get("periodMonth");
			String periodWeek = this.getFormHM().get("periodWeek")==null ? "" : (String)this.getFormHM().get("periodWeek");
			String p0723 = (String) this.getFormHM().get("p0723");
			p0723 = WorkPlanUtil.decryption(p0723);
			
            //若没有计划，则需从新创建
            if(StringUtils.isEmpty(p0700) || "0".equals(p0700)){
            	WorkPlanBo planBo = new WorkPlanBo(this.frameconn, this.userView);
            	planBo.setA0100(objectid.substring(3));
            	planBo.setNBase(objectid.substring(0, 3));
            	planBo.initPlan(objectid, p0723, periodType, periodYear,
						periodMonth, periodWeek);
            	planBo.addPlan(); 
            	planBo.loadPlanInfo();
            	p0700 = planBo.getP0700();
            }
            
			ExportWorkPlanExcelBo exportWPBo = new ExportWorkPlanExcelBo(this.getFrameconn(), Integer.valueOf(p0700),this.userView);
			exportWPBo.setObjectId(objectid);
			exportWPBo.setP0723(Integer.valueOf(p0723));
            
			Workbook wb = null;
            String fileName = (String) this.getFormHM().get("filename");//加密后的文件名
            fileName = PubFunc.decrypt(fileName);
            String path = (String) this.getFormHM().get("path");//路径
            //zhangh 2020-3-5 使用新组件后，拿到的path就是文件id，需要根据文件id获取文件
            in = VfsService.getFile(path);
            wb = WorkbookFactory.create(in);// 创建excel
            importMsgList = exportWPBo.importTemplate(wb);
            
            this.getFormHM().put("p0700", WorkPlanUtil.encryption(p0700));
         } catch (Exception e) {
            e.printStackTrace();
            importMsgList.add(e.toString());
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            this.getFormHM().put("importMsg", importMsgList);  
            PubFunc.closeIoResource(in);
        } 
    }
    
}
