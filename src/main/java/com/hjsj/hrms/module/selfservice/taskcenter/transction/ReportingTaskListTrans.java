package com.hjsj.hrms.module.selfservice.taskcenter.transction;

import com.hjsj.hrms.module.selfservice.taskcenter.businessobject.ITaskCenterService;
import com.hjsj.hrms.module.selfservice.taskcenter.businessobject.impl.ITaskCenterServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 获取报备列表数据交易类
 * @Author wangz
 * @Date 2020/6/1 11:49
 * @Version V1.0
 **/
public class ReportingTaskListTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        ITaskCenterService iTaskCenterService = new ITaskCenterServiceImpl(this.frameconn, this.userView);
        String timeFilter = (String) this.getFormHM().get("timeFilter");
        String days = (String) this.getFormHM().get("daysFilter");
        String searchFilter = (String) this.getFormHM().get("searchFilter");
        String pageIndex = (String) this.getFormHM().get("pageIndex");
        String pageSize = (String) this.getFormHM().get("pageSize");
        LazyDynaBean paramBean = new LazyDynaBean();
        String query_type = "";
        if (StringUtils.isNotEmpty(timeFilter)) {
            if (!StringUtils.equalsIgnoreCase("recent", timeFilter)) {
                query_type = timeFilter;
            } else {
                if (StringUtils.isNotEmpty(days)) {
                    query_type = "1";
                }
            }
        }
        paramBean.set("query_type", query_type);
        paramBean.set("days", days != null ? days : "");
        paramBean.set("tabid", "");
        paramBean.set("module_id", "9");
        paramBean.set("bs_flag", "3");
        paramBean.set("topic_info", searchFilter);
        List bbTaskList = new ArrayList();
        List<LazyDynaBean> bbTaskTempList = iTaskCenterService.getReportTaskList(paramBean);
        for (LazyDynaBean bean : bbTaskTempList) {
            bbTaskList.add(PubFunc.DynaBean2Map(bean));
        }
        int startIndex = 0;
        int endIndex = 0;
        startIndex = (Integer.valueOf(pageIndex) - 1) * Integer.valueOf(pageSize);
        endIndex = Integer.valueOf(pageIndex) * Integer.valueOf(pageSize);
        int totalCount = bbTaskList.size();
        if (endIndex > totalCount) {
            endIndex = totalCount;
        }
        bbTaskList = bbTaskList.subList(startIndex, endIndex);
        this.getFormHM().put("bbTaskList", bbTaskList);
        this.getFormHM().put("totalCount", totalCount);
    }
}
