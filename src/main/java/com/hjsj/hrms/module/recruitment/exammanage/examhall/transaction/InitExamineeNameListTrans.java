package com.hjsj.hrms.module.recruitment.exammanage.examhall.transaction;

import com.hjsj.hrms.module.recruitment.exammanage.examhall.businessobject.ExamineeNameListBo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 * 项目名称：hcm7.x 
 * 类名称：InitExamineeNameListTrans 
 * 类描述：加载考生名单
 * 创建人：sunming 
 * 创建时间：2015-11-3
 * 
 * @version
 */
public class InitExamineeNameListTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		try {
			/** 考场id */
			String hallId =  (String) this.getFormHM().get("hall_id");
			/** 考场名称 */
			String hallName = (String) this.getFormHM().get("hallName");
			/** 批次名称 */
			String batchName = (String) this.getFormHM().get("batchName");
			ExamineeNameListBo bo = new ExamineeNameListBo(this.getFrameconn(),this.userView);
			/** 获取列头 */
			ArrayList<ColumnsInfo> columnsInfo = new ArrayList<ColumnsInfo>();
			columnsInfo = bo.getColumnList();
			/** 获取sql */
			String sql = bo.getDataSql(hallId);
			/** 加载表格 */
			TableConfigBuilder builder = new TableConfigBuilder("examineeNameList", columnsInfo, "examineeNameList001", userView, this.getFrameconn());
			builder.setDataSql(sql);
			builder.setAutoRender(true);
			builder.setTitle("考生名单("+hallName+"--"+batchName+")");
			builder.setConstantName("recruitment/examineeNameList");
			builder.setSelectable(true);
			builder.setScheme(true);
			builder.setSetScheme(true);
			builder.setPageSize(20);
			builder.setTableTools(bo.getButtonList());
			builder.setSchemeSaveCallback("examineenamelist.schemeSaveCallback");
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}


}
