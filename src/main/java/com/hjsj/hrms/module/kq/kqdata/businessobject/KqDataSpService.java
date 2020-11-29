package com.hjsj.hrms.module.kq.kqdata.businessobject;

import com.hrms.struts.exception.GeneralException;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 考勤审批接口
 *
 * @author ZhangHua
 * @version V75
 * @date 16:39 2018/10/30
 */
public interface KqDataSpService {

    /**
     * 获取考勤填报记录表
     *
     * @param sqlWhere
     * @param parameterList
     * @param sqlSort
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 16:38 2018/10/30
     */
    ArrayList<LazyDynaBean> listKq_extend_log(String sqlWhere, ArrayList parameterList, String sqlSort) throws GeneralException;
    /**
     * 获取该方案当前期间的数据记录
     * listKqSpMainData
     * @param jsonStrObject	前台传参数
     * @param schemeBean	该方案的详细信息
     * @return
     * @throws GeneralException
     * @date 2019年4月18日 下午5:05:45
     * @author linbz
     */
    ArrayList<HashMap<String, String>> listKqSpMainData(JSONObject jsonStrObject, LazyDynaBean schemeBean) throws GeneralException;

    HashMap<String, String> getKqDate(ArrayList<LazyDynaBean> listKq_extend) throws GeneralException;

    ArrayList<HashMap<String, String>> listKqSchemeByMySelf() throws GeneralException;

    /**
     * 新建考勤数据
     *
     * @param scheme_id   方案id
     * @param kq_year     考勤年度
     * @param kq_duration 考勤期间
     * @param org_Id 创建机构id
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 14:51 2018/11/5
     */
    void createNewKqData(String scheme_id, String kq_year, String kq_duration, List<String> org_Id) throws GeneralException;


    /**
     * 报批考勤数据
     *
     * @param viewType    页面区分 0:考勤上报 1:考勤审批
     * @param scheme_id   考勤方案id
     * @param kq_year     考勤年份
     * @param kq_duration 考勤期间
     * @param org_id     所属机构id
     * @param photo_info 机构审核人报批传签章图片信息
     * @return
     * @author ZhangHua
     * @date 17:36 2018/11/6
     */
    boolean appealKqData(String viewType, String scheme_id, String kq_year, String kq_duration, String org_id, JSONObject photo_info) throws GeneralException;


    /**
     * 驳回考勤数据
     *
     * @param scheme_id   考勤方案id
     * @param kq_year     考勤年份
     * @param kq_duration 考勤期间
     * @param org_Id      所属机构id
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 20:29 2018/11/7
     */
    boolean rejectKqData(String viewType, String scheme_id, String kq_year, String kq_duration, String org_Id, String user_id, String role_id) throws GeneralException;


    /**
     * 批准考勤数据
     *
     * @param scheme_id   考勤方案id
     * @param kq_year     考勤年份
     * @param kq_duration 考勤期间
     * @param orgList     所属机构id
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 18:00 2018/11/7
     */
    boolean approveKqData(String scheme_id, String kq_year, String kq_duration, ArrayList<String> orgList, String viewType, JSONObject photo_info) throws GeneralException;

    /**
     * @param scheme_id_e 考勤方案id(加密)
     * @param kq_year     考勤年份
     * @param kq_duration 考勤期间
     * @param orgIds      所属机构id 逗号分隔
     * @param coverDataFlag  =0 覆盖手工修改的数据 =1 不覆盖
     * @return 是否成功
     * @throws GeneralException
     * @author Hao Shulin
     * @date 2018-11-13
     */
    boolean calculateMxData(String scheme_id_e, String kq_year, String kq_duration, String orgIds, int coverDataFlag) throws GeneralException;


    /**
     * 归档考勤数据
     *
     * @param viewType    页面区分 0:考勤上报 1:考勤审批
     * @param scheme_id   考勤方案id
     * @param kq_year     考勤年份
     * @param kq_duration 考勤期间
     * @throws GeneralException
     * @author ZhangHua
     * @date 11:52 2018/11/16
     */
    void submitKqData(String viewType, String scheme_id, String kq_year, String kq_duration, String org_Id, boolean isCover) throws GeneralException;


    /**
     * 获取考勤员配置的通讯参数
     *
     * @param userName 业务用户账号
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 17:11 2018/11/22
     */
    HashMap<String, String> mapMsgConfig(String userName) throws GeneralException;


    /**
     * 获取退回人员列表
     * @param scheme_id
     * @param org_id
     * @param viewType
     * @param resetFlag		重置标识=0重置，=其他为退回
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 18:38 2018/12/24
     */
    ArrayList<HashMap<String,String>> listrejectPer(String scheme_id, String org_id, String viewType, String resetFlag) throws GeneralException;

    /**
     * 保存导出方案
     * @param detailsVal
     *          日明细导出列
     * @param sumsVal
     *          月汇总导出列
     * @param sumsVal
     *          考勤方案ID
     * @return
     */
    void saveExportScheme(String detailsVal, String sumsVal, String scheme_id) throws GeneralException;

    /**
     * 获得考勤导出Excel配置方案用于回显
     * @param scheme_id
     * @return
     * @throws GeneralException
     */
    Map<String,String> getExportScheme(String scheme_id) throws GeneralException;
    /**
     * 代确认 需要将待办置为已办
     * @param jsonStrObject
     * @return
     */
    void doReplaceConfirm(JSONObject jsonStrObject) throws GeneralException;
    /**
     * 下发操作（即将sp_flag由08置为01）
     * @param scheme_id
     * @param kq_year
     * @param kq_duration
     * @param orgList
     * @param shemeBean
     * @throws GeneralException
     * @author linbz
     * @date 18:38 2019/10/09
     */
    void doDownward(String scheme_id, String kq_year, String kq_duration, List<String> orgList
			, LazyDynaBean shemeBean) throws GeneralException;
    /**
     * 填写审批意见
     * @param type
     * @param kq_year
     * @param scheme_id
     * @param org_id
     * @throws GeneralException
     */
    void fillProcessMsg(String kq_year,String kq_duration,String scheme_id,String org_id,String sp_message,String sp_flag) throws GeneralException;
    /**
     * 获取审批意见列表
     * @param kq_year
     * @param kq_duration
     * @param scheme_id
     * @param org_id
     * @return
     * @throws GeneralException
     */
    List listProcessMsg(String kq_year,String kq_duration,String scheme_id,String org_id) throws GeneralException;
}
