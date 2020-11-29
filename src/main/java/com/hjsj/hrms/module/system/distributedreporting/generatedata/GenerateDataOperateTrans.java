package com.hjsj.hrms.module.system.distributedreporting.generatedata;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Title: GenerateDataOperateTrans
 * @Description:生成上报数据页面数据操作
 * @Company:hjsj
 * @Create time: 2019/5/23 14:12:46
 * @author: wangbs
 * @version: 1.0
 */
public class GenerateDataOperateTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        try {
            String operaType = (String) this.formHM.get("operaType");
            GenerateDataBo gdBo = new GenerateDataBo(userView, frameconn);

            if ("search".equals(operaType)) {//查询日志数据
                String searchPlan = (String) this.formHM.get("searchPlan");
                /** 获取列头 */
                ArrayList<ColumnsInfo> columnsInfo = gdBo.getColumnList();
                /** 创建表格对象 */
                TableConfigBuilder builder = new TableConfigBuilder("generatedata", columnsInfo, "generatedata", userView, this.getFrameconn());
                ArrayList dataList = gdBo.getReportData(searchPlan);//获取表格数据

                if (dataList.get(0) == "success") {
                    dataList.remove(0);
                    builder.setDataList(dataList);

                    builder.setAutoRender(true);//自动渲染到页面
                    builder.setTitle(ResourceFactory.getProperty("dr_generate.report.data"));//数据上报
                    builder.setSetScheme(false);//栏目设置
                    builder.setColumnFilter(false);//过滤
                    builder.setPageSize(20);
                    builder.setSelectable(true);//是否有复选框列
                    builder.setEditable(false);
                    builder.setLockable(true);
                    builder.setTableTools(gdBo.getButtonList());
                    String config = builder.createExtTableConfig();
                    this.formHM.put("return_code", "success");
                    this.formHM.put("tableConfig", config);
                }else{
                    this.formHM.put("return_code", "fail");
                }
            } else if ("delete".equals(operaType)) {//删除日志数据
                List delIdList = (ArrayList) this.formHM.get("delIdList");
                String return_code = gdBo.deleteLog(delIdList);
                this.formHM.put("return_code", return_code);

            } else if ("echoReportDataWinInfo".equals(operaType)) {//回显上报数据window页面信息
                Map winDataMap = gdBo.echoReportDataWinInfo();
                this.formHM.put("winDataMap", winDataMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
