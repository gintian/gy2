package com.hjsj.hrms.module.system.distributedreporting.reportdata;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * @Titile: ReportDataTrans
 * @Description:上报数据主界面交易类
 * @Company:hjsj
 * @Create time: 2019年5月30日上午11:07:03
 * @author: Zhiyh
 * @version 1.0
 */
public class ReportDataTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            String value = (String) this.getFormHM().get("value");//显示方案
            /** 获取列头 */
            ArrayList<ColumnsInfo> columnsInfo = new ArrayList<ColumnsInfo>();
            ReportDataBo bo = new ReportDataBo(userView, frameconn);
            columnsInfo=bo.getReportDataColumnList();
            String sql = bo.getReportDataSql(value);
            
            /** 加载表格 */
            TableConfigBuilder builder = new TableConfigBuilder("reportdata", columnsInfo, "reportdata001", userView, this.getFrameconn());
            builder.setDataSql(sql);
            builder.setOrderBy(" order by id desc");
            builder.setAutoRender(true);
            builder.setTitle(ResourceFactory.getProperty("dr_report.data"));//上报数据
            builder.setSetScheme(false);//栏目设置
            //builder.setScheme(true);
            builder.setColumnFilter(false);//过滤
            builder.setSelectable(false);
            builder.setEditable(false);
            builder.setPageSize(20);
            builder.setLockable(true);
            builder.setTableTools(bo.getReportDataButtonList());
            builder.setShowPublicPlan(false);
            String config = builder.createExtTableConfig();
            this.getFormHM().put("tableConfig", config.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

}
