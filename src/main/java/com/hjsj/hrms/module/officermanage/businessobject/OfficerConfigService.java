package com.hjsj.hrms.module.officermanage.businessobject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 干部管理参数配置与更新干部管理表
 * @author changxy
 *
 */
public interface OfficerConfigService {
	/**
	 * 根据fieldSet 查询关联单位子集指标与关联bw子集指标
	 * @param fieldset
	 * @return
	 * @throws Exception
	 */
	ArrayList getFieldItemList(String fieldset) throws Exception;
	/**
	 * 查询用户权限库与用户权限范围内人员子集
	 * @return
	 * @throws Exception
	 */
	HashMap<String, Object> getDbListFieldSetList() throws Exception;
	/**
	 * 保存干部管理参数配置
	 * @throws Exception
	 */
	void saveSetting(HashMap map) throws Exception;
	/**
	 * 获取干部管理参数配置信息
	 * constant表中OFFICER_PARAM 存储数据
	 * @return
	 * @throws Exception
	 */
	HashMap getOfficerConstant() throws Exception;
}
