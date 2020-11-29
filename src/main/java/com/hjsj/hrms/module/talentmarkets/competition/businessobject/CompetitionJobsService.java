package com.hjsj.hrms.module.talentmarkets.competition.businessobject;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description 内部竞聘岗位列表接口实现类
 * @Author wangz
 * @Date 2019/7/24 10:31
 * @Version V1.0
 **/
public interface CompetitionJobsService {
    /**
     * 导出excel按钮权限功能号
     */
    String EXPORT_EXCEL_FUNC_ID = "4010101";
    /**
     * 导出面试功能名单按钮权限功能号
     */
    String EXPORT_INTERVIEW_LIST_FUNC_ID = "4010102";
    /**
     * 岗位编制按钮权限功能号
     */
    String JOB_PREPARATION_FUNC_ID = "4010103";
    /**
     * 新建按钮权限功能号
     */
    String CREATE_FUNC_ID = "4010104";
    /**
     * 导入按钮权限功能号
     */
    String IMPORT_FUNC_ID = "4010105";
    /**
     * 删除按钮权限功能号
     */
    String DELETE_FUNC_ID = "4010106";
    /**
     * 发布按钮权限功能号
     */
    String PUBLISH_FUNC_ID = "4010107";
    /**
     * 暂停按钮权限功能号
     */
    String SUSPEND_FUNC_ID = "4010108";
    /**
     * 结束按钮权限功能号
     */
    String END_FUNC_ID = "4010109";
    /**
     * 面试安排功能权限号
     */
    String INTERVIEW_ARRANGEMENT_FUNC_ID = "4010110";
    /**
     * 公示按钮权限功能号
     */
    String PUBLICITY_FUNC_ID = "4010111";
    /**
     * 报批按钮权限功能号
     */
    String REPORT_FUNC_ID = "4010113";
    /**
     * 批准按钮权限功能号
     */
    String APPROVE_FUNC_ID = "4010114";
    /**
     * 退回按钮权限功能号
     */
    String REFUSE_FUNC_ID = "4010115";
    /**
     * z81业务字典表系统内置字段
     */
    String DEFALUT_FIELDS = "z81:create_time,z81:Z8101,z81:Z8103,z81:B0110,z81:E01A1,z81:Z8105,z81:Z8107,z81:Z8109,z81:Z8111,z81:Z8113,z81:Z8115,z81:E0122,z81:Z8117,z81:create_user";




    /**
     * 操作类型枚举类
     */
    enum operateType {
        /**
         * 查询岗位竞聘列表信息
         */
        search,
        /**
         * 保存岗位竞聘列表信息
         */
        save,
        /**
         * 导入数据初始化
         */
        importInit,
        /**
         * 导入数据
         */
        importData,
        /**
         * 删除
         */
        delete,
        /**
         * 修改岗位转态
         */
        changeState,
        /**
         * 获取岗位竞聘范围的描述
         */
        getCompetitiveScopeDesc,
        /**
         * 公示按钮相关操作
         */
        publicity,
        /**
         * 获取审批过程格式化数据
         */
        approvalFormat,
        /**
         * 根据竞聘范围，保存发布通知
         */
        savePublishedNotice,
        /**
         * 加密岗位代码
         */
        encryptE01a1,
        /**
         * 公示后修改竞聘人员状态
         */
        changePersonnelStatus,
        /**
         * 导出面试名单
         */
        exportInterviewList,
        /**
         * 新建岗位初始化数据
         */
        createInitData,
        /**
         * 保存新建岗位数据
         */
        saveCreatePostData,
        /**
         * 用于check新建竞聘岗位的状态
         */
        checkPostStatus,
        /**
         * 获取在流程中的岗位的e01a1
         */
        getIngE01a1,
        /**
         * 校验岗位下有没有报名审核中的数据
         */
        checkIngPersonInPost,
        /**
         * 公示时查询岗位下是否有(报名通过，安排面试中，面试通过，录用审批中的人员)
         */
        checkPublicityPersonStatus
    }

    /**
     * 竞聘岗位状态
     */
    enum competitivePositionStatus {
        /**
         * 起草
         */
        drafting("01"),
        /**
         * 申请中
         */
        application("02"),
        /**
         * 已批准
         */
        approved("03"),
        /**
         * 已发布
         */
        published("04"),
        /**
         * 暂停
         */
        suspend("05"),
        /**
         * 结束
         */
        end("06"),
        /**
         * 公示中
         */
        publicized("07"),
        /**
         * 审批未通过
         */
        approvalFailed("08"),
        /**
         * 公示结束
         */
        publicizend("09"),
        /**
         * 回退
         */
        refuse("10");

        private String value;

        competitivePositionStatus(String num) {
            this.value = num;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 用于获取竞聘岗位列表的gridConfig配置参数
     *
     * @return gridConfig配置参数
     * @param statusFlag
     */
    String getCompetitionJobsGridConfigs(String statusFlag) throws GeneralException;

    /**
     * 刷新内部竞聘岗位列表数据
     *
     * @param queryMethod 岗位状态 参见competitivePositionStatus
     */
    void refsTableData(String queryMethod);

    /**
     * 根据输入的内容生成查询条件
     *
     * @param valueList 输入的内容集合
     * @return sql语句
     */
    String getSqlCondition(List<String> valueList);

    /**
     * 保存岗位列表数据
     *
     * @param dataList 修改的列表数据
     * @throws GeneralException
     */
    void saveCompetitionJobsData(List<MorphDynaBean> dataList) throws GeneralException;

    /**
     * 改变岗位竞聘状态 （发布、暂停、结束功能）
     *
     * @param status 岗位竞聘状态
     * @param ids    岗位id字符串 格式 xxx,xxxx,xxx
     * @throws GeneralException
     */
    void changeState(String status, String ids,String notice_time) throws GeneralException;

    /**
     * 删除岗位列表数据
     *
     * @param ids      要删除的岗位id字符串 格式 xxx,xxxx,xxx
     * @param isConfim 当要删除的竞聘岗位下有竞聘 人员数据时 只有此参数为1才可以进行删除
     * @return 是否再次确认是否可以删除
     * @throws GeneralException
     */
    String deleteCompetitionJobsData(String ids, String isConfim) throws GeneralException;

    /**
     * 获取公示相关数据(备选指标，分组指标)
     *
     * @return 公示相关数据
     */
    Map getNoticeData() throws GeneralException;

    /**
     * 获取公示岗位的人员数据
     *
     * @param ids z8101字符串加密  多个用，隔开
     * @return 公示岗位的人员数据
     * @throws GeneralException
     */
    List getPublicityPeopleData(String ids) throws GeneralException;
    /**
     * 导入职位-下载模板
     * @author: caoqy
     * @return: java.lang.String
     * @date: 2019-8-13 11:44
     * @throws GeneralException
     */
    String downloadTemplate() throws GeneralException;

    /**
     * 通过模板导入职位
     * @author: caoqy
     * @param fileId 文件的加密id
     * @return: java.lang.String
     * @date: 2019-8-14 13:59
     */
    String importData(String fileId) throws GeneralException;

    /**
     * 获取竞聘岗位数
     * @param B0110 查询单位
     * @param jobName 模糊查询 岗位名称
     * @return
     * @throws GeneralException
     */
    int getReleaseCompetitionJobsTotal(String B0110,String jobName) throws GeneralException;
    
    /**
     * 获取发布状态下的竞聘岗位
     * @param B0110 查询单位
     * @param jobName 模糊查询 岗位名称
     * @param pageIndex 第几页
     * @param pageSize 每页显示数
     * @return
     * @throws GeneralException
     */
    List listReleaseCompetitionJobs(String B0110,String jobName,int pageIndex, int pageSize) throws GeneralException;

    /**
     * 获取竞聘岗位详细信息
     * @param id 岗位编号 加密
     * @return
     */
    Map getCompetitionJobDetailData(String id) throws GeneralException ;


    /**
     * 获取竞聘分析中的相关数据
     * @Author xuchangshun
     * @param nmoudle : 所属模块 4为组织机构
     * @param userView :当前登录用户
     * @param paramMap :前端传递来的数据参数
     * @return java.util.HashMap 数据集合
     * @throws GeneralException  错误异常
     * @Date 2019/8/15 9:55
     */
    Map getChartsData(String nmoudle, UserView userView, Map<String,String> paramMap) throws GeneralException;

    /**
     * 保存发布通知信息
     * @param selectList
     * @param postList
     * @param topic
     */
    void savePublishedNotice(ArrayList selectList, ArrayList postList, String topic);

    /**
     * 公示后修改竞聘人员状态
     * @param ids
     */
    void changePersonnelStatus(String ids) throws GeneralException;

    /**
     * 导出面试名单
     * @param z8101s_e z8101加密字符串
     * @return 加密的文件名
     * @throws GeneralException
     */
    String exportInterviewList(String z8101s_e)throws GeneralException;

    /**
     * 判断是否有岗位详情登记表权限
     * @return true 有 false 没有
     * @throws GeneralException
     */
    boolean isHaveThePosTab() throws GeneralException;

    /**
     * 获取新建竞聘岗位指标数据
     * @return
     * @throws GeneralException
     */
    Map getCreatePostFieldList() throws GeneralException;

    /**
     * 保存新建岗位数据
     * @param basicInformation  基本信息数据
     * @param competitiveScopeData 竞聘范围
     * @param interviewerData 面试官
     */
    void saveCreatePostData(Map basicInformation,List competitiveScopeData,List interviewerData) throws GeneralException;
    boolean checkPostStatus(ArrayList postList) throws GeneralException;

    /**
     * 获取在流程中的岗位id
     * @return  岗位id集合
     * @throws GeneralException
     */
    List getIngE01a1()throws GeneralException;

    /**
     * 判断岗位下是否有报名审批中的人员数据
     * @return
     */
    List isHaveIngPersonInPostList(String ids,String status)throws GeneralException;

    /**
     * 结束报名审批中的人事异动流程
     */
    void endApplyTask(String postIds) throws GeneralException;

    /**
     * 组装竞聘范围描述和岗位编号加密数据
     * @author wangbs
     * @param z8115Map 竞聘范围map
     * @param e01a1Map 岗位编号map
     * @return void
     * @throws GeneralException 抛出异常
     * @date 2020/3/30
     */
    void assembleDescAndEncryptData(Map<String, String> z8115Map, Map<String, String> e01a1Map) throws GeneralException;
}
