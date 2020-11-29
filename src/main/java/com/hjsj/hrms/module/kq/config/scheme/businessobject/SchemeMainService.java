package com.hjsj.hrms.module.kq.config.scheme.businessobject;

import com.hrms.struts.exception.GeneralException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public interface SchemeMainService {
    /**
     * 考勤方案查询列表
     *
     * @param currentPage当前页
     * @param pageSize每页大小
     * @param inputValue搜索框
     * @return
     * @throws GeneralException
     */
    HashMap getSchemeDataList(int currentPage, int pageSize, ArrayList inputValue) throws GeneralException;

    /**
     * 获取方案详细信息
     *
     * @param scheme_id
     * @return
     * @throws GeneralException
     */
    HashMap<String, String> getSchemeDetailDataList(String scheme_id) throws GeneralException;

    /**
     * 保存方案
     *
     * @param jsonObject数据信息
     * @return
     * @throws GeneralException
     */
    int saveData(JSONObject jsonObject) throws GeneralException;

    /**
     * 应用范围有父节点时，其子节点不用保存
     *
     * @param org_ids: id的集合
     * @param org_name: name的集合
     * @param org_id_appear: 数据上报的机构集合
     * @return
     * @throws GeneralException
     */
    HashMap getSortRangeMap(JSONArray org_ids, JSONArray org_name, JSONArray org_id_appear) throws GeneralException;

    /**
     * 修改生效状态
     *
     * @param jsonObject
     */
    int changeState(JSONObject jsonObject);

    /**
     * 删除方案
     *
     * @param jsonObject，就是ids的集合
     * @return
     */
    int deleteScheme(JSONObject jsonObject);
    
    /**
     * 根据各种条件查询kq_scheme表中的内容
     * @param sqlWhere sql条件
     * @param parameterList 参数
     * @param sqlSort 排序字段：无需order by,直接写字段即可
     * @return ArrayList<LazyDynaBean>
     * @throws GeneralException
     */
    ArrayList<LazyDynaBean> listKq_scheme(String sqlWhere, ArrayList parameterList, String sqlSort) throws GeneralException;
    /**
     * 根据各种条件查询kq_scheme表中的内容 根据年月获取有效机构
     * @param sqlWhere sql条件
     * @param parameterList 参数
     * @param sqlSort 排序字段：无需order by,直接写字段即可
     * @param kq_year 考勤年份
     * @param kq_duration 考勤月份
     * @return ArrayList<LazyDynaBean>
     * @throws GeneralException
     */
    ArrayList<LazyDynaBean> listKq_scheme(String sqlWhere, ArrayList parameterList, String sqlSort,String kq_year,String kq_duration) throws GeneralException;

    ArrayList getFillingAgencysTree(String scheme_id);
    
    /**
     * 通过业务用户获取自主用户的图片路径
     * @param busiName
     * @return
     */
    String getZiZhuImg(String busiName);
    
    /**
     * 列表界面修改审核员和考勤员
     * @param username 用户名
     * @param fullname 全程
     * @param flag: true: 审核员，false:考勤员
     * @param org_id
     * @param scheme_id
     * @return
     * @throws GeneralException
     */
    int changeClerkOrReviewFromList(String username, String fullname, boolean flag, String org_id, String scheme_id, String old_name) throws GeneralException;
    /**
     * 校验审核人是否存在待办
     * @param org_id	所属机构/为空则表示为方案审核人
     * @param scheme_id	方案id
     * @param old_name 	审核人
     * @return
     * @throws GeneralException
     * @date 2019年12月02日 下午3:22:16
     * @author linbz
     */
    boolean checkReviewPersonDealt(String org_id, String scheme_id, String old_name) throws GeneralException;
}
