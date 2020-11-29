package com.hjsj.hrms.module.kq.kqdata.businessobject;

import com.hrms.struts.exception.GeneralException;

import java.util.ArrayList;
import java.util.HashMap;

public interface KqDataArchiveService {

	/**
	 * 获取考勤归档方案信息
	 * @return 归档信息{
	 * 	 fieldsetid: 归档子集
	 *   field_item_list : 源指标数据集合
	 *   mapping_list : 归档子集指标集合  to_item_id 对应源指标 item_id
	 *   set_list : 子集集合
	 *   }
	 * @author wangbo
     * @date 11:29 2018/11/7
     * @throws GeneralException 接口方法必须抛出异常,异常信息需自己定义
	 */
	HashMap getKqDataArchive() throws GeneralException;
	/**
	 * 保存考勤归档方案信息
	 * @param fieldsetid  归档子集编号
	 * @param mappingList 归档指标集合   to_item_id : 对应Q35表 指标
	 * @return true 保存成功|false 失败
	 * @throws GeneralException
	 * @author wangbo
     * @date 11:29 2018/11/7
     * @throws GeneralException 接口方法必须抛出异常,异常信息需自己定义
	 */
	boolean saveKqDataArchive(String fieldsetid,ArrayList mappingList) throws GeneralException;
}
