package com.hjsj.hrms.module.workplan.yearplan.transaction;

import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.module.workplan.yearplan.businessobject.YearPlanBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * //导出年计划
 * @author haosl
 * @date 20170321
 *
 */
public class ExportExcelTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			YearPlanBo yearplanBo = new YearPlanBo(userView, getFrameconn());
			String subModuleId = (String)this.getFormHM().get("subModuleId");
			TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(subModuleId);
			tableCache.getColumnDisplayConfig();
			ExportExcelUtil excelUtil = new ExportExcelUtil(this.getFrameconn());
			excelUtil.setRowHeight((short)1600);
			ArrayList headList = yearplanBo.getHeadList(tableCache);
			ArrayList dataList = yearplanBo.getDataList(tableCache);
			String fileName = this.userView.getUserName()+"的年计划"+ ".xls";//根据规则生成Excel名称
			excelUtil.exportExcel(fileName, "年计划",null, headList, dataList, null, 1);
			this.getFormHM().put("fileName", SafeCode.encode(PubFunc.encrypt(fileName)));//表格名传进前台
			this.getFormHM().put("flag", true);//成功标记
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
}
