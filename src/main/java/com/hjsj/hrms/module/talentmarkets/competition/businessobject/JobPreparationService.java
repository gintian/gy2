package com.hjsj.hrms.module.talentmarkets.competition.businessobject;

import com.hrms.struts.exception.GeneralException;

import java.util.ArrayList;

/**
 * @Description 岗位编制接口
 * @Author manjg
 * @Date 2019/7/24 10:45
 * @Version V1.0
 **/
public interface JobPreparationService {

    /**
     * 获取表格配置信息
     * @return
     */
    String getTableConfig() throws GeneralException;

    /**
     * 查询方案刷新数据
     * @param queryMethod
     */
    void refreshTableData(int queryMethod);

    /**
     * 检验发布申请的岗位状态
     * @param postList
     */
    void checkPostStatus(ArrayList<String> postList) throws GeneralException;
}
