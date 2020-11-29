/**
 * xuanz
 * 2019年8月19日上午11:21:09
 */
package com.hjsj.hrms.module.kq.kqdata.businessobject;

import com.hrms.struts.exception.GeneralException;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * @author admin
 *
 */
public interface KqExceptService {
	
	String getKqExcept(String guidkey,String startDate,String endDate);
	String getKqExceptTableConfig(String guidkey, String startDate, String endDate)throws GeneralException;
	/**
	 * 出勤异常导出表
	 * exportKqExceptTable
	 * @param jsonObj
	 * @return
	 * @throws GeneralException
	 * @date 2019年8月26日 
	 * @author xuanz
	 */
	String exportKqExceptTable(JSONObject jsonObj) throws GeneralException;
	/**
	 * 获取出勤异常数据
	 * @param sql
	 * @return
	 * @throws GeneralException
	 */
	ArrayList<LazyDynaBean> listKqExceptData(String sql)throws GeneralException;
}
