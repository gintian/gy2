package com.hjsj.hrms.module.system.sdparameter.businessobject;

import com.hrms.struts.exception.GeneralException;

import java.util.HashMap;
import java.util.List;
/**
 * 二开参数设置业务接口类
 * @author wangbo
 * @category hjsj  2019-08-26
 * @version 1.0
 */
public interface SDParameterService {

	/**
	 * 保存二开参数设置数据
	 *
	 * @param parameter 二开参数集合
	 * @throws GeneralException
	 */
	void saveParameter(List parameter) throws GeneralException;

	/**
	 * 删除二开参数数据
	 *
	 * @param constants 格式 xxx,xxx
	 * @throws GeneralException
	 */
	void deleteParameter(String constants) throws GeneralException;

	/**
	 * 获取二开参数表格数据
	 *
	 * @return
	 * @throws GeneralException
	 */
	String getTableConfig(int page,int pageSize) throws GeneralException;

	/**
	 * 获取二开参数表全部记录缓存
	 *
	 * @return  
	 *  {
	 *     id:{ // id 编号 int
	 *     	  id:xxx,
	 *        constant:xxx,
	 *        ...
	 *     }
	 * 	}
	 * @throws GeneralException
	 */
	HashMap getSDParameter() throws GeneralException;

	/**
	 * 新增表格数据
	 * */
	int insertParamData(int pageSize) throws GeneralException;

}
