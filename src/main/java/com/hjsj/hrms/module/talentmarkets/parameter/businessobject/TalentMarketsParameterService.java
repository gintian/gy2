package com.hjsj.hrms.module.talentmarkets.parameter.businessobject;

import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 *
 * @Titile TalentMarketsParameterService
 * @Description 人才市场参数设置
 * @Company hjsj
 * @Create time: 2019年8月8日下午6:32:04
 * @author  wangdi
 * @version 1.0
 *
 */
public interface TalentMarketsParameterService {

    /**
     * 保存功能
     * @param str str_value字段
     * @return 成功与否
     */
    String saveSettings(String str);

    /**
     * 加载数据 
     * @return str_value
     * 
     */
    JSONObject loadSettings();

    /**
       * 拖拽排序
     * @param oriItemList
     * @param toItemid
     * @param dropPosition
     * @return 成功与否
     */
    String dragAndDropSort(ArrayList<String> oriItemList, String toItemid, String dropPosition);

    /**
     * 查询业务字典表指标
     * @param type
     * @return 相应指标集
     */
    List<LazyDynaBean> queryFieldItem(String type);

    /**
     * 加载指标对应的相关指标
     *
     * @param fieldType
     * @param tabId
     * @param changeType
     * @param lengthLimit
     * @param searchType
     * @return
     */
    ArrayList loadTemplateItems(String fieldType, String tabId, String changeType, String lengthLimit, String searchType);

    /**
     * 检验模板是否处于流程中
     * @return 模板是否允许配置
     */
    HashMap checkConfigurable();
}
