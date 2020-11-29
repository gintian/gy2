package com.hjsj.hrms.module.kq.kqdata.businessobject;

import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.struts.exception.GeneralException;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考勤数据明细 业务类接口
 * create time  2018-10-25
 * @author haosl
 *
 */
public interface KqDataMxService {
	
	/**
	 *   获取考勤明细数据
	 *  （方法名以list开头 后接驼峰格式表名）
     *
     * @param sqlWhere      数据范围
     * @param parameterList 参数 (不需要写where 以and开头)
     * @param sqlSort       排序sql(不需要写order by 仅写字段即可，后可跟desc)
     * @return ArrayList<LazyDynaBean> (LazyDynaBean内为该表查询结果的全部字段，查询结果 bean 中的key为全小写字段名)
     * @throws GeneralException 接口方法必须抛出异常,异常信息需自己定义
     * @author haosl
     * @date 11:29 2018/10/31
     */
	ArrayList<LazyDynaBean> listQ35(String sqlWhere, ArrayList parameterList, String sqlSort) throws GeneralException;
	
	/**
	 * 获取表格控件配置
	 * @param jsonObj 
	 * 		前台传递的参数的json对象
	 * @param showMxBtn 
	 * 		是否显示明细按钮
	 * @param confirmFlag
	 * 		员工是否需要确认
	 * @param showDetailFlag
	 * 		方案设置是否显示日明细"true"/"false"
	 * @param role 
	 * 		当前登陆人的考勤角色
	 * @throws GeneralException
	 */
	String getTableConfig(JSONObject jsonObj, boolean showMxBtn, Integer confirmFlag, String showDetailFlag, int role) throws GeneralException;
	
	/**
	 * 获得考勤方案中选择的班次和考勤项目
	 * @param schemeId 考勤方案id(加密后)
	 * @param model   =0 获取班次和考勤项目；=1 获取班次；=2 获取考勤项目
	 * @return
	 * @throws GeneralException 
	 */
	HashMap<String,LazyDynaBean> getClassAndItems(String schemeId, String model) throws GeneralException;
	
	/**
	 * 保存考勤数据明细
	 * @param scheme_id 方案id
	 * @param kq_duration 考勤期间
	 * @param kq_year 考勤年度
	 * @param guidkey
	 * @param orgId 所属机构
	 * @throws GeneralException
	 */
	void saveKqDataMx(String scheme_id, String kq_duration, String kq_year, String guidkey, JSONObject paramValue
			, String orgId, String enableModifys) throws Exception;
	
	/**
	 * 获得表格控件需要的sql
	 * @return
	 */
	String getTableSql(String kq_year, String kq_duration, String orgId, String scheme_id, String cbase);
	/**
	 *    获得表格列头
	 * @param showMx 
	 * @param kq_duration 
	 * 		是否显示明细按钮
	 * @return
	 * @throws GeneralException 
	 */
	ArrayList<ColumnsInfo> getColumnList(String showMx, String kq_duration, String kq_year, String schemeId, Integer confirmFlag) throws GeneralException;
	
	/**
	 * 	获得有序的考勤班次和项目
	 * @param schemeId
	 * @param model
	 * @return
	 * @throws GeneralException
	 */
	List<LazyDynaBean> getClassAndItemsOrder(String schemeId, String model) throws GeneralException;
	/**
	 * 获得所有班次和考勤项目
	 * @return
	 * @throws GeneralException 
	 */
	List<LazyDynaBean> getAllClassAndItems() throws GeneralException;
    /**
     *  获得人员变动新增和减少的人员数据
     * @param scheme_id
     * @param kq_year
     * @param kq_duration
     * @param orgId 可传可不传，不为空的时候比对单个部门，为空比对方案下的所有下级机构
     * @return
     * @throws GeneralException
     */
    Map getChangeStaffs(String scheme_id, String kq_year, String kq_duration, String orgId, int limit, int page
            , String type, LazyDynaBean shemeBean) throws GeneralException;
    /**
     * 人员变动调整
     * @param scheme_id
     * @param kq_year
     * @param kq_duration
     * @param guidkeys
     *             人员的guidkeys 的集合
     * @param opration
     *          add:新增  del：删除
     * @throws GeneralException
     */
    void changeStaffs(String scheme_id, String kq_year, String kq_duration, List<String> guidkeys, String opration, String orgId) throws GeneralException;

    /**
     * 查询当前期间下的变动岗人员可编辑的区域
     * @param kq_year
     * @param kq_duration
     * @param org_id
     * @param scheme_id
     * @return Map<String,List<String>>
     *     key:guidkey; value:List 存放可以修改的日期列如q3501-q3531
     */
    Map<String,List<String>> searchChangePerData(String kq_year, String kq_duration, String org_id, String scheme_id)throws GeneralException;

    /**
     * 删除考勤人员
     * @param scheme_id
     * @param kq_year
     * @param kq_duration
     * @param orgId
     * @param guidkeys
     */
    void deletePersons(String scheme_id, String kq_year, String kq_duration, String orgId, List<String> guidkeys) throws GeneralException;

    /**
     * 获得考勤方案中配置的统计指标
     * @param scheme_id
     * @return
     * @throws GeneralException
     */
    String getSumsByScheme(String scheme_id) throws GeneralException;
    /**
	 * 获取出勤异常项目
	 * @param scheme_id
	 * @return
	 * @throws GeneralException
	 */
	String getSumsBySchemeCheck(String scheme_id) throws GeneralException;
}
