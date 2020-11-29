package com.hjsj.hrms.module.talentmarkets.competition.businessobject;

import com.hrms.struts.exception.GeneralException;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.List;
import java.util.Map;

/**
 * @Title CompetitionService
 * @Description 竞聘人员列表页面接口类
 * @Company hjsj
 * @Author wangbs、caoqy
 * @Date 2019/7/24
 * @Version 1.0.0
 */
public interface CompetitionService {
    /** 操作类型-查询竞聘人员列表数据 */
    String SEARCH = "search";
    /** 操作类型-加密nbaseA0100*/
    String ENCRYPT = "encrypt";
    /** 操作类型-保存列表中修改的信息 */
    String SAVEGRIDDATA = "saveGridData";
    /** 操作类型-查询面试安排界面数据 */
    String INTERVIEW_INIT = "interviewInit";
    /** 操作类型-保存面试安排信息 */
    String INTERVIEW_SAVE = "interviewSave";
    /** 操作类型-发送通知 */
    String SEND_NOTICE = "sendNotice";
    /** 操作类型-翻页时保存候选人信息 */
    String SAVE_CANDIDATES_DATA = "saveCandidatesData";
    /** 操作类型-保存面试官信息*/
    String SAVE_INTERVIEWERS_DATA = "saveInterviewersData";
    /** 操作类型-导出简历PDF */
    String EXPORT_PDF = "exportPdf";
    /** 操作类型-导入数据-下载模板 */
    String IMPORT_INIT = "importInit";
    /** 操作类型-导入数据 */
    String IMPORT_DATA = "importData";
    /** 操作类型-审批过程格式化数据**/
    String APPROVE_FORMAT_DATA = "approvalFormatData";



    /** 人员列表表格组件ID */
    String COMPETITIONPSN = "competitionPsn";
    /** 面试安排表格组件ID */
    String INTERVIEWPLAN = "interviewPlan";
    /** 快速查询 */
    String QUICK_SEARCH = "1";
    /** 复杂查询 */
    String COMPLEX_SEARCH = "2";
    /** 查询当前正在进行的数据 (04||05) */
    String CURRENT = "current";
    /** 查询历史的数据 (06||07) */
    String HISTORY = "history";
    /** z83业务字典表系统内置字段 */
    String DEFALUT_FIELDS = "Z83:Z8101,Z83:Z8301,Z83:Z8303,Z83:Z8305,Z83:Z8307,Z83:Z8309,A01:A0101,A01:B0110,A01:E0122,A01:E01A1";
    /** z83业务字典表隐藏字段 栏目设置添加弹窗做过滤 */
    String HIDDEN_FIELDS = "Z83:Z8101,Z83:Z8301,A01:A0100";

    /** 导出Excel按钮功能号 */
    String EXPORT_EXCEL_FUNCID = "4010201";
    /** 导出简历PDF按钮功能号 */
    String EXPORT_PDF_FUNCID = "4010202";
    /** 导入成绩按钮功能号 */
    String IMPORT_GRADE = "4010203";
    /** 拟录用审批按钮功能号 */
    String DRAFT_EMPLOY_APPROVE = "4010204";
    /** 面试通过按钮功能号 */
    String INTERVIEW_PASS = "4010205";
    /** 面试未通过按钮功能号 */
    String INTERVIEW_NOT_PASS = "4010206";
    /** 保存默认方案按钮功能号 */
    String SAVE_INIT_PLAN = "4010212";

    enum exportExcelType{
        /**
         * 导入成绩
         */
        scope("Z83");

        private String value;

        exportExcelType(String num) {
            this.value = num;
        }

        public String getValue() {
            return value;
        }

    }
    /**
     * 获取tableConfig
     * @author wangbs
     * @param fromValue   页面从哪来
     * @param statusValue 查询什么状态的数据
     * @param posValue    点击柱子查询某岗位的申报人列表
     * @return String
     */
    String getTableConfig(String fromValue, String statusValue, String posValue);

    /**
     * 保存人员列表修改的数据
     * @param modifyDataList 改动的数据
     * @author wangbs
     * @return void
     * @throws GeneralException 抛出异常
     */
    void saveGridData(List<MorphDynaBean> modifyDataList) throws GeneralException;

    /**
     * 拼接querybox组件的过滤条件
     * @author wangbs
     * @param valueList 过滤条件
     * @return String
     */
    String getSqlCondition(List<String> valueList);

    /**
     * 根据竞聘岗位编号获取面试页面信息
     * @author wangbs
     * @param selectId 竞聘岗位编号
     * @return String
     * @throws GeneralException 抛出异常
     */
    Map getInterviewPageInfo(String selectId) throws GeneralException;

    /**
     * 保存面试安排信息
     * @author wangbs
     * @param compePosNum 竞聘岗位编号
     * @param interviewPlan 面试安排信息
     * @param extendFlag 面试官是否是继承上次标识
     * @return void
     * @throws GeneralException 抛出异常
     */
    void saveInterviewPlan(String compePosNum, Map interviewPlan, boolean extendFlag) throws GeneralException;

    /**
     * 翻页时保存候选人信息
     * @author wangbs
     * @param compePosNum 竞聘岗位编号
     * @param candidatesDataList 候选人信息
     * @return void
     * @throws GeneralException 抛出异常
     */
    void saveCandidatesData(String compePosNum, List candidatesDataList) throws GeneralException;

    /**
     * 校验发送通知的配置是否配好
     * @author wangbs
     * @param noticeWay 通知方式
     * @return List
     * @throws GeneralException 抛出异常
     */
    List checkSendNoticeServer(String noticeWay)throws GeneralException;

    /**
     * 给面试官和候选人发通知并改变人员状态
     * @author wangbs
     * @param compePosNum 岗位编号
     * @param noticeWay 通知方式
     * @param noticeTitle 通知标题
     * @param noticeContent 通知内容
     * @param posDesc 岗位描述
     * @param interviewersList 面试官信息
     * @param sendCandidatesNoticeList 符合条件的候选人信息
     * @param changeCompeStatusList 需改变状态的人
     * @return List
     */
    List sendNoticeAndChangeStatus(String compePosNum,String noticeWay, String noticeTitle, String noticeContent, String posDesc, List interviewersList, List sendCandidatesNoticeList, List changeCompeStatusList);

    /**
     * 根据posid获取候选人信息tablebuilderconfig
     * @author wangbs
     * @param posId 岗位编号
     * @param targetPage 加载哪一页
     * @param pageSize 一页几条数据
     * @return Map
     * @throws GeneralException 抛出异常
     */
    Map getCandidatesTableInfo(String posId, int targetPage, int pageSize) throws GeneralException;

    /**
     * 导出简历PDF
     *
     * @author: caoqy
     * @param paramsMap: 所需参数
     * @return: java.util.Map<java.lang.String,java.lang.String>
     * @date: 2019-8-2 11:21
     * @throws GeneralException
     */
    Map<String,String> exportPdf(Map paramsMap) throws GeneralException;

    /**
     * 下载成绩导入模板(仅提供报名"安排面试中"（04）状态的人员)
     *
     * @author: caoqy
     * @return: java.util.Map<java.lang.String,java.lang.String>
     * @date: 2019-8-2 16:21
     * @throws GeneralException
     * @param fromValue
     * @param statusValue
     * @param posValue
     */
    String downloadScoreTemplate(String fromValue, String statusValue, String posValue) throws GeneralException;


    /**
     * 判断是否有正在竞聘的岗位
     * @return
     * @throws GeneralException
     */
    boolean checkMobileMyCompetitionPost(String guidkey) throws GeneralException;
    
    /**
     * 功能描述：获取我竞聘的岗位  包括 正在进行竞聘  和 竞聘结束的
     * @param state =executing 进行中   = end 结束   =其他值 获取我竞聘过的岗位
     * @param guidkey 人员唯一标识
     * @return 竞聘岗位信息
     * @throws GeneralException
     */
    List listMobileMyCompetitionPost(String state,String guidkey) throws GeneralException;

    /**
     * 通过excel模板导入成绩
     * @author: caoqy
     * @param fileId 文件加密id
     * @return: java.util.ArrayList<java.lang.Object>
     * @date: 2019-8-7 13:45
     */
    String importData(String fileId) throws GeneralException;
}
