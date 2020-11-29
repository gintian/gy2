package com.hjsj.hrms.module.workplan.plantask.transaction;

import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.module.workplan.plantask.businessobject.ExportWorkPlanExcelBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

/**
 * <p>Title: DownloadTemplateTrans </p>
 * OKR-工作计划-下载模板
 * <p>create time  2017-9-28 上午10:46:31</p>
 * @author linbz
 */
@SuppressWarnings("serial")
public class DownloadWorkPlanTaskTrans extends IBusiness{

	//导入数据-下载时需要去除的列--负责人、任务成员、时间安排、甘特图
    public static String exportNo = ",principal,participant,timearrange,gantt,";
    //导入数据-下载时需要隐藏的列--任务ID
    public static String exportHidden = ",p0800,p0831,";
    
    @SuppressWarnings("unchecked")
    @Override
    public void execute() throws GeneralException {

        try {
			String p0700 = (String) this.getFormHM().get("p0700");
			p0700 = WorkPlanUtil.decryption(p0700);
			p0700 = StringUtils.isEmpty(p0700)?"0":p0700;
			String p0723 = (String) this.getFormHM().get("p0723");
			p0723 = WorkPlanUtil.decryption(p0723);
			String periodType = (String) this.getFormHM().get("periodType");
			String periodYear =  (this.getFormHM().get("periodYear")==null ? "" : (String)this.getFormHM().get("periodYear"));
			String periodMonth = (this.getFormHM().get("periodMonth")==null ? "" : (String)this.getFormHM().get("periodMonth"));
			String periodWeek = (this.getFormHM().get("periodWeek")==null ? "" : (String)this.getFormHM().get("periodWeek"));
			String objectId = (String) this.getFormHM().get("objectId");
			objectId = WorkPlanUtil.decryption(objectId);
			WorkPlanBo workPlanBo = new WorkPlanBo(this.frameconn, this.userView);
			// 得到导出文件的名称
			String plan_title = workPlanBo.getExportPlanName(periodType,
					periodYear, periodMonth, periodWeek, objectId, p0723);
			String fileName = plan_title+".xls";
			ExportWorkPlanExcelBo exportWPBo = new ExportWorkPlanExcelBo(this.getFrameconn(), Integer.parseInt(p0700),this.userView);
			exportWPBo.createExcel(fileName, "sheet", Integer.valueOf(p0723),Integer.valueOf(p0700),periodType);
			fileName = SafeCode.encode(PubFunc.encrypt(fileName));
			this.getFormHM().put("fileName", fileName);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        
    }

}
