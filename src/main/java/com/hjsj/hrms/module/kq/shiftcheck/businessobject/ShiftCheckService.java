package com.hjsj.hrms.module.kq.shiftcheck.businessobject;

import com.hrms.struts.exception.GeneralException;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**  
 * <p>Title: ShiftCheckService</p>  
 * <p>Description: 考勤排班审查</p>  
 * <p>Company: hjsj</p>
 * @date 2018年12月3日 下午3:39:23
 * @author linbz  
 * @version 7.5
 */  
public interface ShiftCheckService {

	/**
	 * 获取排班审查表格数据
	 * getShiftCheckData
	 * @param jsonObj
	 * @return
	 * @throws GeneralException
	 * @date 2018年12月5日 下午4:08:18
	 * @author linbz
	 */
	HashMap getShiftCheckData(JSONObject jsonObj) throws GeneralException;
	/**
	 * 获取权限内部门员工出勤人数
	 * listOrgOndutyCount
	 * @return
	 * @throws GeneralException
	 * @date 2018年12月4日 下午3:52:03
	 * @author linbz
	 */
	ArrayList listOrgOndutyCount(JSONObject jsonObj) throws GeneralException;
	/**
	 * 排班审查导出工作明细表
	 * exportWorkAnalysisTable
	 * @param jsonObj
	 * @return
	 * @throws GeneralException
	 * @date 2018年12月12日 下午1:36:13
	 * @author linbz
	 */
	String exportWorkAnalysisTable(JSONObject jsonObj) throws GeneralException;
	/**
	 * 排班审查导出排班表
	 * exportShiftTable
	 * @param jsonObj
	 * @return
	 * @throws GeneralException
	 * @date 2018年12月12日 下午1:36:46
	 * @author linbz
	 */
	String exportShiftTable(JSONObject jsonObj) throws GeneralException;

}
