package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hjsj.hrms.module.system.distributedreporting.businessobject.SetupSchemeBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * @Description:设置接收方案主界面交易类
 * @author: zhiyh
 * @date: 2019年3月13日 上午9:30:00 
 * @version: 1.0
 */
public class ShowProgrammeTrans extends IBusiness {


	@Override
	public void execute() throws GeneralException {
		try {
			/** 获取列头 */
			ArrayList<ColumnsInfo> columnsInfo = new ArrayList<ColumnsInfo>();
			SetupSchemeBo bo = new SetupSchemeBo(userView, frameconn);
			columnsInfo = bo.getColumnList();
			ArrayList<LazyDynaBean> dataList = bo.getSchemeData();
			/** 加载表格 */
			TableConfigBuilder builder = new TableConfigBuilder("setupscheme", columnsInfo, "setupscheme001", userView, this.getFrameconn());
			builder.setDataList(dataList);
			builder.setAutoRender(true);
			builder.setTitle(ResourceFactory.getProperty("dr_set.receive.scheme"));
			builder.setSetScheme(false);//栏目设置
			//builder.setScheme(true);设置此值居左无效
			builder.setColumnFilter(false);//过滤
			builder.setSelectable(false);//是否有复选框列
			builder.setEditable(false);
			builder.setPageSize(20);
			builder.setLockable(true);
			builder.setTableTools(bo.getButtonList());
			builder.setShowPublicPlan(false);
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
