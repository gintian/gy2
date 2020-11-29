package com.hjsj.hrms.module.system.hrcloud.businessobject;

import com.hrms.struts.exception.GeneralException;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.SQLException;

public interface SyncConfigService {
	/**
	 * 获取constant中HRCLOUD_CONFIG存放的Json
	 * @return
	 */
	JSONObject getConstantParamJson();
	/**
	 * 获取云配置参数
	 * @return
	 */
	JSONObject getCloudAPIConfigData();
	
	/**
	 * 保存云配置参数
	 * appId、tenantId、appSecret
	 * @param configDataBean
	 * @throws SQLException 
	 * @throws GeneralException 
	 */
	void saveCloudAPIConfigData(MorphDynaBean configDataBean) throws Exception;
	
	void saveCloudConfigData(MorphDynaBean configDataBean) throws Exception;
	
	/**
	 * 保存全部云配置参数
	 * appId、tenantId、appSecret
	 * @param configDataBean
	 * @param strValue 
	 * @throws SQLException 
	 * @throws GeneralException 
	 */
	void saveAllCloudConfigData(MorphDynaBean configDataBean, JSONObject strValue) throws Exception;
	
	/**
	 * 将前台传入的bean类型参数转为json格式
	 * @param configDataBean
	 * @throws Exception
	 */
	JSONObject getStrValue(MorphDynaBean configDataBean) throws Exception;
	
	/**
	 * 刷新后台作业
	 * @throws Exception
	 */
	public void refreshWarnScanJob() throws Exception;
	
	/**
	 * 清空接口数据
	 * @return
	 */
	boolean cleanInterfaceData();
	
	/**
	 * 获取云子集
	 * @param cloudReturnJson 
	 * @return
	 */
	JSONArray getCloudTOhrSet();
	
	
	/**
	 * 获取hr同步到云 的hrTOcloud json
	 * @param resDataJson
	 * @return
	 */
	JSONObject getCloudDataJson();
	
	/**
	 * 获取云当前页面的详细参数
	 * @param string
	 * @return
	 */
	JSONObject getCloudTOhrCurrentSet(String string);
	
	void setCloudFieldDataJson(JSONObject cloudReturnJson);
	
	JSONObject getCloudFieldDataJson();
	
	/**
	 * 获取hr对应云自动关联的指标
	 * @param tabelsList
	 * @return
	 */
	JSONObject getMatchConfig(JSONArray tabelsList);
	
	/**
	 * 获取云对应hr自动关联的指标
	 * @param tabelsList
	 * @return
	 */
	JSONObject getCloudMatchJson(JSONArray setMapping);
	
	void setConstantParamJson(JSONObject constantParamJson);
}
