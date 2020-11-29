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
 * @Description 获取我的申请列表数据交易类
 * @Author wangz
 * @Date 2020/6/1 11:49
 * @Version V1.0
 **/
public class MyApplyDataList extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        ITaskCenterService iTaskCenterService = new ITaskCenterServiceImpl(this.frameconn, this.userView);
        String timeFilter = (String) this.getFormHM().get("timeFilter");
        String days = (String) this.getFormHM().get("daysFilter");
        String pageIndex = (String) this.getFormHM().get("pageIndex");
        String pageSize = (String) this.getFormHM().get("pageSize");
        String query_method = (String) this.getFormHM().get("query_method");
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
        paramBean.set("topic_info", "");
        //1 运行中 2已结束 3 已终止
        paramBean.set("query_method", query_method);
        //1.我的申请
        paramBean.set("sp_flag", "1");
        List myApplyTaskList = new ArrayList();
        List<LazyDynaBean> myApplyListTaskTempList = iTaskCenterService.getMyApplyList(paramBean);
        for (LazyDynaBean bean : myApplyListTaskTempList) {
            myApplyTaskList.add(PubFunc.DynaBean2Map(bean));
        }
        int startIndex = 0;
        int endIndex = 0;
        startIndex = (Integer.valueOf(pageIndex) - 1) * Integer.valueOf(pageSize);
        endIndex = Integer.valueOf(pageIndex) * Integer.valueOf(pageSize);
        int totalCount = myApplyTaskList.size();
        if (endIndex > totalCount) {
            endIndex = totalCount;
        }
        myApplyTaskList = myApplyTaskList.subList(startIndex, endIndex);
        this.getFormHM().put("myApplyTaskList", myApplyTaskList);
        this.getFormHM().put("totalCount", totalCount);
    }
}
