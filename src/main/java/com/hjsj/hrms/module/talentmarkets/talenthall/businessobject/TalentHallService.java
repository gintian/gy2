package com.hjsj.hrms.module.talentmarkets.talenthall.businessobject;

import com.hrms.struts.exception.GeneralException;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.Map;

/**
 * @Description 人才展厅接口
 * @Author wangz
 * @Date 2019/10/9 18:07
 * @Version V1.0
 **/
public interface TalentHallService {
    /**
     *  查询面板数据
     */

    String OPERATE_TYPE_QUERY_DATA = "queryData";
    /**
     *  改变简历发布状态
     */
    String OPERATE_TYPE_CHANGE_RESUME_STATUS = "changeResumeStatus";
    /**
     *  改变简历点赞状态
     */
    String OPERATE_TYPE_CHANGE_APPROVAL_STATUS = "changeApprovalStatus";
    /**
     *  改变简历关注状态
     */
    String OPERATE_TYPE_CHANGE_ATTENTION_STATUS = "changeAttentionStatus";
    /**
     * 初始化参数
     */
    String OPERATE_TYPE_INIT_PARAM = "initParam";
    /**
     * 浏览详情页  获取gridconfig
     */
    String OPERATE_TYPE_GET_GRIDCONFIG = "getGridConfig";
    /**
     * 查看简历 浏览次数加1
     */
    String OPERATE_TYPE_VIEW_COUNT = "viewCount";

    /**
     * 获取指定页的数据
     * @param page 第几页
     * @param limit 一页多少条
     * @param queryValues  限定条件sql
     * @param orderyType 排序语句sql
     * @return
     * @throws GeneralException
     */
    Map getData(int page, int limit, MorphDynaBean queryValues, String orderyType )throws GeneralException;

    /**
     * 改变简历状态
     * @param guidkey  简历人员唯一标识
     * @param opt  1撤销 2发布
     * @throws GeneralException
     */
    void changeResumeStatus(String guidkey,String opt) throws GeneralException;

    /**
     * 改变简历关注状态
     * @param z8501 简历人员的唯一标识
     * @param attention 关注状态值 0 不关注  1 关注
     * @throws GeneralException
     */
    void changeAttentionStatus(String z8501,String attention) throws GeneralException;
    /**
     * 改变简历点赞状态
     * @param z8501 简历人员的唯一标识
     * @param approval 点赞状态值 0 不点赞  1 点赞
     * @throws GeneralException
     */
    void changeApprovalStatus(String z8501,String approval) throws GeneralException;

    /**
     * 获取初始化参数 例如 登记表 模板id等
     * @return
     * @throws GeneralException
     */
    Map getInitParam() throws GeneralException;

    /**
     * 获取浏览情况页面 gridconfigs
     * @param viewType  browseDetails 浏览情况按钮   browseTimes 浏览人次
     * @return
     * @throws GeneralException
     */
    String getGridConfig(String viewType,String z8501)throws GeneralException;

    /**
     * 查看简历 浏览次数增加
     * @param z8501
     * @throws GeneralException
     */
    void changeViewCount(String z8501) throws GeneralException;
}
