package com.hjsj.hrms.module.kq.config.shiftgroup.businessobject;

import com.hrms.struts.exception.GeneralException;
import net.sf.json.JSONObject;

import java.util.HashMap;

/**  
 * <p>Title: ShiftGroupService</p>  
 * <p>Description: </p>  
 * <p>Company: hjsj</p>
 * @date 2018年11月1日 下午1:51:47
 * @author linbz  
 * @version 7.5
 */  
public interface ShiftGroupService {

	/**
	 * 获取班组列表信息
	 * getShiftGroupTableConfig
	 * @return
	 * @throws GeneralException
	 * @date 2018年11月1日 下午5:12:04
	 * @author linbz
	 */
	String getShiftGroupTableConfig(String validityflag) throws GeneralException;
	/**
	 * 获取班组信息
	 * getShiftGroup
	 * @param jsonObj
	 * @return
	 * @throws GeneralException
	 * @date 2018年11月1日 下午4:58:05
	 * @author linbz
	 */
	String getShiftGroup(String groupId) throws GeneralException;
	/**
	 * 保存或更新班组信息
	 * saveShiftGroup
	 * @param jsonObj
	 * @return
	 * @throws GeneralException
	 * @date 2018年11月1日 下午4:58:35
	 * @author linbz
	 */
	String saveShiftGroup(JSONObject jsonObj) throws GeneralException;
	/**
	 * 删除班组信息
	 * delShiftGroup
	 * @param groupId	班组id
	 * @return
	 * @throws GeneralException
	 * @date 2018年11月1日 下午4:58:23
	 * @author linbz
	 */
	String delShiftGroup(String groupId) throws GeneralException;
	/**
	 * 获取班组需要的其他参数信息等
	 * getShiftGroupInfo
	 * @return
	 * @throws GeneralException
	 * @date 2019年2月28日 上午11:35:50
	 * @author linbz
	 */
	HashMap getShiftGroupInfo() throws GeneralException;
}
