package com.hjsj.hrms.module.kq.config.shifts.businessobject;

import com.hrms.struts.exception.GeneralException;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public interface ShiftsService{

    /**
     * 获取考勤班次
     * （方法名以list开头 后接驼峰格式表名）
     *
     * @param  sqlWhere      数据范围
     * @param parameterList 参数 (不需要写where 以and开头)
     * @param sqlSort       排序sql(不需要写order by 仅写字段即可)
     * @return ArrayList<LazyDynaBean> (LazyDynaBean内为该表查询结果的全部字段)
     * @throws GeneralException 接口方法必须抛出异常
     * @author haosl
     * @date 11:29 2018/10/30
     */
    ArrayList<LazyDynaBean> listKq_class(String sqlWhere ,ArrayList parameterList,String sqlSort) throws GeneralException;

	String getShiftsTableConfig() throws GeneralException;
	/**
	 * 保存班次信息
	 * @param valueBean
	 * @return
	 * @throws GeneralException 
	 */
	String saveShift(JSONObject jsonObj);
	
	/**
	 * 删除班次
	 * @param idArr
	 * @return
	 * @throws GeneralException 
	 */
	String delShit(String[] idArr);
	
	/**
	 * 获取指定班次的信息
	 * @throws Exception 
	 */
	LazyDynaBean getClassInfo(String classId) throws Exception;
	/**
	 * 	启用|停用班次
	 * @param classId
	 * @param validate
	 * @return
	 */
	String editValidate(String classId, String validate);
	/**
	 * 调整班次顺序
	 * @param from_id
	 * @param to_id
	 * @param ori_seq
	 * @param to_seq
	 * @return
	 */
	String adjustClassSeq(String from_id, String to_id);
	
	/**
	 *获得权限范围内的组织机构
	 * @return
	 */
	String getPriveCode();
	/**
	 * 验证班次是否被使用
	 * @param classId
	 * @return
	 */
	String checkValidate(String classId);
}
