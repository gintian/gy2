/**
 * FileName: IDeclareService
 * Author:   xucs
 * Date:     2018/12/5 13:52
 * Description: 个税专项申报接口类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.hjsj.hrms.module.gz.zxdeclare.businessobject;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈接口功能描述〉<br>
 * 〈个税专项申报接口类〉
 *
 * @author xucs
 * @create 2018/12/5
 * @since 1.0.0
 */
public interface IDeclareService {
    /**操作类型查询**/
    String C_OPERATE_TYPE_SEARCH = "search";
    /**操作类型同意**/
    String C_OPERATE_TYPE_APPROVE = "approve";
    /**操作类型退回**/
    String C_OPERATE_TYPE_REJECT = "reject";
    /**操作类型删除**/
    String C_OPERATE_TYPE_DELETE = "delete";
    /**操作导出Excel**/
    String C_OPERATE_TYPE_EXPORTEXCEL="exportExcel";
    /**保存指标对应关系*/
    String C_OPERATE_TYPE_SAVE_RELATION="saveRelation";
    /**获取指标对应关系*/
    String C_OPERATE_TYPE_GET_RELATION="getRelation";
    /**获取子集指标*/
    String C_OPERATE_TYPE_GET_Field="getField";
    /**获取stepView currentIndex*/
    String C_OPERATE_TYPE_GET_Current_Index="getCurrentIndex";
    /**获取stepView currentIndex*/
    String C_OPERATE_TYPE_Check_File="checkFile";
    /**按模板导出excel*/
    String C_OPERATE_TYPE_EXPORT_TEMPLATE_EXCEL="exportTemplateExcel";
    /**保存模版文件到指定目录*/
    String C_OPERATE_TYPE_SAVE_TEMPLATE_FILE="saveTemplateFile";
    /**审核状态起草**/
    String C_APPROVE_STATE_DRAFT = "01";
    /**审核状态审核中**/
    String C_APPROVE_STATE_INAUDIT = "02";
    /**审核状态通过**/
    String C_APPROVE_STATE_ADOPT = "03";
    /**审核状态未通过**/
    String C_APPROVE_STATE_NOTPASS = "04";
    /**审核状态归档**/
    String C_APPROVE_STATE_FILED = "05";
    /**审核状态全部**/
    String C_APPROVE_STATE_ALL = "-1";


    /**专项附加类型全部**/
    String C_DECLARE_TYPE_ALL = "-1";
    /**专项附加类型子女教育**/
    String C_DECLARE_TYPE_CHILDEDU = "01";
    /**专项附加类型继续教育**/
    String C_DECLARE_TYPE_CONTINU_EDU = "02";
    /**学历继续教育**/
    String C_DECLARE_TYPE_CONTINU_EDU_EDU="01";
    /**在职继续教育**/
    String C_DECLARE_TYPE_CONTINU_EDU_PROFESSION="02";

    /**专项附加类型住房租金**/
    String C_DECLARE_TYPE_HOUSING_RENT = "03";
    /**专项附加类型房贷利息**/
    String C_DECLARE_TYPE_INTEREST_EXPENSE = "04";
    /**专项附加类型大病医疗**/
    String C_DECLARE_TYPE_ILLNESS_MEDICALCARE = "05";
    /**专项附加类型赡养老人**/
    String C_DECLARE_TYPE_SUPPORT_ELDERLY = "06";
    /**专项附件类型赡养老人  赡养人信息标识**/
    String C_DECLARE_TYPE_SUPPORT_ELDERLY_OLD="1";
    /**专项附件类型赡养老人  共同赡养人信息标识**/
    String C_DECLARE_TYPE_SUPPORT_ELDERLY_CHILD="2";

    /**导入模板数据**/
    String C_BUTTON_IMPORTDATA = ResourceFactory.getProperty("gz.zxdeclare.label.importExcelData");
    /**按钮同意**/
    String C_BUTTON_AGREE = ResourceFactory.getProperty("gz.zxdeclare.buttonAgree");
    /**按钮同意**/
    String C_BUTTON_REJECT = ResourceFactory.getProperty("gz.zxdeclare.buttonReject");
    /**按钮删除**/
    String C_BUTTON_DELETE = ResourceFactory.getProperty("gz.zxdeclare.buttonDelete");
    /**按钮按模板导出**/
    String C_BUTTON_EXPORT_TEMPLATE_EXCEL = ResourceFactory.getProperty("gz.zxdeclare.buttonExportTemplateExcel");
    /**导出Excel**/
    String C_BUTTON_EXPORT_EXCEL = ResourceFactory.getProperty("gz.zxdeclare.buttonExportExcel");
    /**下载附件**/
    String C_BUTTON_DOWN_ATTACH = ResourceFactory.getProperty("gz.zxdeclare.buttonDownAttach");
    /**按月抵扣**/
    String C_DECLARE_TYPE_MONTH="01";
    /**按年抵扣**/
    String C_DECLARE_TYPE_YEAR="02";

    /**导入模板 首页数据   **/
    String C_DECLARE_TYPE_MAIN_SHEET=ResourceFactory.getProperty("gz.zxdeclare.label.importExcelSheetMain");
    /**导入模板 子女教育数据   **/
    String C_DECLARE_TYPE_CHILDEDU_SHEET=ResourceFactory.getProperty("gz.zxdeclare.label.importExcelSheetChildEdu");
    /**导入模板 继续教育数据   **/
    String C_DECLARE_TYPE_CONTINUEDU_SHEET=ResourceFactory.getProperty("gz.zxdeclare.label.importExcelSheetJXEdu");
    /**导入模板 住房租金数据   **/
    String C_DECLARE_TYPE_HOUSINGRENT_SHEET=ResourceFactory.getProperty("gz.zxdeclare.label.importExcelSheetHouseRent");
    /**导入模板 住房贷款利息数据   **/
    String C_DECLARE_TYPE_INTERESTEXPENSE_SHEET=ResourceFactory.getProperty("gz.zxdeclare.label.importExcelSheetHouseLoan");
    /**导入模板 赡养老人数据   **/
    String C_DECLARE_TYPE_SUPPORTELDERLY_SHEET=ResourceFactory.getProperty("gz.zxdeclare.label.importExcelSheetSYOld");


    /**
     * 根据专项申报类型以及审批状态查询专项申报列表
     * @param declareType 专项申报类型
     * @param approveState 审批状态
     * @param userView 当前登录用户信息
     * @throws GeneralException 出错异常
     * @return 列表数据
     */
    String searchDeclareList(String declareType, String approveState, UserView userView) throws GeneralException;

    /**
     * 刷新专项申报数据列表
     * @param declareType 专项申报类型
     * @param approveState 审批状态
     * @param userView 登录用户数据
     * @throws GeneralException 出错异常
     * @return 成功 success 失败 fail
     */
    String refsDeclareList(String declareType, String approveState, UserView userView) throws GeneralException;

    /**
     *@Description: 同意个税专项附加申报数据
     *
     * @param declares:专项申报ids,使用逗号进行分割
     * @return: String 成功返回 success 失败返回 faile
     * @Author xucs
     * @Date: 2018/12/18
     * @throw: GeneralException 出错异常
     */
    String approveDeclares(String declares) throws GeneralException;

    /**
     * 退回专项申报
     * @param declares 专项申报ids,使用逗号进行分割
     * @throws GeneralException 出错异常
     * @return 成功返回 success 失败返回 faile
     */
    String rejectDeclares(String declares,Map<String,Object> param) throws GeneralException;

    /**
     * 删除专项申报
     * @param declares 专项申报 ids,使用逗号进行分割
     * @throws GeneralException 出错异常
     * @return 成功返回success 失败返回faile
     */
    String deleteDeclares(String declares) throws GeneralException;

    /**
     * 获取单个专项申报的数据
     * @param declarid 专项申报的id,加密的
     * @throws GeneralException 出错异常
     * @return 专项申报数据的Map集合
     */
    Map getDeclareInfor(String declarid) throws GeneralException;

    /**
     * 根据输入的内容生成查询条件
     * @param valueList 输入的内容集合
     * @throws GeneralException 出错异常
     * @return sql语句
     */
    String getSqlCondition(List<String> valueList) throws GeneralException;


    /**
	 * 获取6项申报记录
	 * @param userView    人员
	 * @author wangb 2018-12-06
	 * @throws GeneralException
	 * @return HashMap 格式如下：  专项未申报   key值不存在
	 * {
	 *   "zx_01":[{},{}],//子女教育
	 *   "zx_02":[{},{}],//继续教育
	 *   "zx_03":[{},{}],//赡养老人
	 *   "zx_04":[{},{}],//住房贷款利息
	 *   "zx_05":[{},{}],//住房租房
	 *   "zx_06":[{},{}]//大病医疗
	 * }
	 */
	HashMap listZXDeclare(UserView userView) throws GeneralException;

    /**
     * 获取最早的发薪日期
     * @param userView 人员
     * @author wangbs 2019-2-15
     * @return String 最早发薪日期
     * @throws GeneralException
     */
    String getMinPayDate(UserView userView) throws GeneralException;

	/**
	 * 获取申报记录
	 * @param whereSql  查询条件  不需要where开头 直接  xxxx=? and xxx=? 格式 没值 直接传null
	 * @param valueList 查询条件对应数据 没值直接 传null
	 * @param orderSql  排序 sql   没值 null   例如： order by xxx,xxx
	 * @author wangb 2018-12-06
	 * @throws GeneralException
	 * @return 申报数据集合
	 */
	ArrayList<HashMap> listZXDeclare(String whereSql,ArrayList valueList,String orderSql,UserView userView) throws GeneralException;


	/**
	 * 保存专项申报记录
	 * @param param   保存数据
	 * @param userView 人员信息
	 * @author wangb 2-18-12-06
	 * @throws GeneralException
	 * @return  success 成功，fail 失败
	 */
	String saveZXDeclare(HashMap param,UserView userView) throws GeneralException;


	/**
	 * 提交专项申报记录
	 * @param param   保存数据
	 * @param userView 人员信息
	 * @author wangb 2-18-12-06
	 * @throws GeneralException
	 * @return  success 成功，fail 失败
	 */
	String submitZXDeclare(HashMap param,UserView userView) throws GeneralException;


	/**
	 * 变更专项申报记录
	 * @param id  申报id
	 * @param userView 人员信息
	 * @author wangb 2-18-12-06
	 * @throws GeneralException
	 * @return  true 成功，false 失败
	 */
	String changeZXDeclare(String id,UserView userView) throws GeneralException;


	/**
	 * 撤销专项申报记录
	 * @param userView 人员信息
	 * @author wangb 2-18-12-06
	 * @throws GeneralException
	 * @return  true 成功，false 失败
	 */
	String revokeZXDeclare(String id,UserView userView) throws GeneralException;


    /**
     * 已过期的申报记录状态改为归档
     * @author wangb 2-18-12-13
     */
    void revokeZXDeclare() throws GeneralException;

    /**
     * 撤销专项申报记录模板导入地址
     * @param userView 人员信息
     * @author wangb 2018-12-26
     * @throws GeneralException
     * @return  true 成功，false 失败
     */
    String getImportPath(UserView userView) throws GeneralException;

    /**
     *
     * @param filePath
     * @return
     * @throws GeneralException
     */
    Map importZXDeclareData(String filePath,UserView userView) throws GeneralException;
    /**
     * 专项附加类型保存指标对应关系
     * @author wangz 2018-12-27
     */
    String SaveRelation(List fieldsList);
    /**
     * 专项附加申报获取对应指标信息
     * @author wangz 2018-12-27
     */
    String getRelation();
    /**
     * 专项附加申报按模板导出excel
     * @author wangz 2018-12-28
     */
    Map exportTemplateExcel(UserView userView,String fileid);

    /**
     * 获取模版文件存储路径
     * @author wangz 2018-12-28
     */
    String getTemplateFilePath();
    /**
     * 保存模板文件
     * @author wangz 2018-12-28
     */
    String saveTemplateFile(String filePath);


}
