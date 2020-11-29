package com.hjsj.hrms.module.system.distributedreporting.datalog.transaction;

import com.hjsj.hrms.module.system.distributedreporting.businessobject.SetupSchemeBo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * @Description:数据接收日志主界面交易类
 * @author: zhiyh
 * @date: 2019年3月13日 上午9:29:03 
 * @version: 1.0
 */
public class ShowLogRecordTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			String value = (String) this.getFormHM().get("value");//显示方案
			/** 获取列头 */
			ArrayList<ColumnsInfo> columnsInfo = new ArrayList<ColumnsInfo>();
			SetupSchemeBo bo = new SetupSchemeBo(userView, frameconn);
			columnsInfo=bo.getLogRecordColumnList();
			String sql = bo.getRecordLogSql(value);
			/** 加载表格 */
			TableConfigBuilder builder = new TableConfigBuilder("datacontrol", columnsInfo, "datacontrol001", userView, this.getFrameconn());
			builder.setDataSql(sql);
			builder.setOrderBy(" order by id desc");
			builder.setAutoRender(true);
			builder.setTitle("数据接收日志");
			builder.setSetScheme(false);//栏目设置
			//builder.setScheme(true);
			builder.setColumnFilter(false);//过滤
			builder.setSelectable(true);
			builder.setEditable(false);
			builder.setPageSize(20);
			builder.setLockable(true);
			builder.setTableTools(bo.getLogRecordButtonList());
			builder.setShowPublicPlan(false);
			//builder.setSearchConfig("ZG00001012","请输入方案名称...", false);
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
