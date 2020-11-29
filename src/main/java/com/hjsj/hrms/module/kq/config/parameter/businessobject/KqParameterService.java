package com.hjsj.hrms.module.kq.config.parameter.businessobject;

import com.hrms.struts.exception.GeneralException;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**  
 * <p>Title: KqParameterService</p>  
 * <p>Description: 考勤参数</p>  
 * <p>Company: hjsj</p>
 * @date 2018年11月16日 下午2:23:25
 * @author linbz  
 * @version 7.5
 */  
public interface KqParameterService {

	/**
	 * 获取考勤参数
	 * getKqParameter
	 * @return
	 * @throws GeneralException
	 * @date 2018年11月16日 下午3:20:24
	 * @author linbz
	 */
	HashMap getKqParameter() throws GeneralException;
	/**
	 * 获取库集合
	 * listNbase
	 * @return
	 * @throws GeneralException
	 * @date 2018年11月16日 下午3:20:27
	 * @author linbz
	 */
	ArrayList<HashMap<String, String>> listNbase() throws GeneralException;
	/**
	 * 获取主集字符串集合
	 * listA01Str
	 * @param flag	=0工号指标备选；=1考勤部门备选
	 * @return
	 * @throws GeneralException
	 * @date 2018年11月19日 下午1:58:48
	 * @author linbz
	 */
	ArrayList<HashMap<String, String>> listA01Str(String flag) throws GeneralException;
	/**
	 * 保存参数
	 * saveKqParameter
	 * @param jsonObj
	 * @return
	 * @throws GeneralException
	 * @date 2018年11月17日 下午4:03:53
	 * @author linbz
	 */
	String saveKqParameter(JSONObject jsonObj) throws GeneralException;
	/**
	 * 获取变动子集指标集合
     * listChangeFieldItemid
     * @param setid 子集
     * @param flag	=0子集部门指标；=1日期型指标
	 * @return
	 * @throws GeneralException
	 * @date 2019年2月20日 上午11:49:04
	 * @author linbz
	 */
	ArrayList<HashMap<String, String>> listFieldItemid(String setid, String flag) throws GeneralException;
	/**
	 * 获取人员子集 集合
	 * listFieldSet
	 * @param flag
	 * @return
	 * @throws GeneralException
	 * @date 2019年2月20日 上午11:58:29
	 * @author linbz
	 */
	ArrayList<HashMap<String, String>> listFieldSet(String flag) throws GeneralException;
	/**
	 * 获取考勤开始结束 日期型指标下拉数据
	 * listDateFieldItemid
	 * @param flag
	 * @return
	 * @throws GeneralException
	 * @date 2019年6月24日 下午6:45:24
	 * @author linbz
	 */
	ArrayList<HashMap<String, String>> listDateFieldItemid(String flag) throws GeneralException;
	
}
