package com.hjsj.hrms.module.selfservice.taskcenter.businessobject;

import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 模板数据服务接口
 * @Author wangz
 * @Date 2020/5/11 16:34
 * @Version V1.0
 **/
public interface ITaskCenterService {
    /**
     * 获取待办、已办任务的数量
     * @param taskType
     * @return
     * @throws GeneralException
     */
    String getTaskNumber(String taskType)throws GeneralException;

    /**
     * 根据传递的参数获取待办的任务列表数据
     * @param paramBean
     * @return
     * @throws GeneralException
     */
    List getPendingTaskList(LazyDynaBean paramBean)throws GeneralException;

    /**
     * 根据传递的参数获取待办的已办列表数据
     * @param paramBean
     * @return
     */
    List getApprovedTaskList(LazyDynaBean paramBean) throws GeneralException;

    /**
     * 根据传入的参数获取模板数据信息
     * @param paramMap
     * @return
     * @throws GeneralException
     */
    Map getTemplateDataInfo(HashMap paramMap) throws GeneralException;

    /**
     * 保存模板数据信息
     * @param paramStr
     * @return
     * @throws GeneralException
     */
    String saveTemplateDataInfo(String paramStr)throws GeneralException;

    /**
     * 办理模板任务
     * @param taskList
     * @param operateType
     * @return
     * @throws GeneralException
     */
    String dealTask(List taskList, String operateType) throws GeneralException;

    /**
     * 根据传递的参数获取报备的任务列表数据
     * @param paramBean
     * @return
     * @throws GeneralException
     */
    List getReportTaskList(LazyDynaBean paramBean)throws GeneralException;

    /**
     * 根据传递的参数获取我的申请列表数据
     * @param paramBean
     * @return
     * @throws GeneralException
     */
    List getMyApplyList(LazyDynaBean paramBean)throws GeneralException;
    Map getApplyTableData(String tabid, String insId, String taskId, String module_id, int pageSize, int pageNum, String searchName) throws GeneralException;

}
