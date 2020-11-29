package com.hjsj.hrms.module.muster.mustermanage.businessobject;

import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 简单花名册业务接口类
 * @author wangbo 2019-02-21
 * @category hjsj
 * @version 1.0
 *
 */
public interface MusterManageService {
   /**
    * 获取主页面功能按钮
    * @return 按钮对象的list
    * @throws GeneralException
    */
	ArrayList getButtonList(String musterType)throws GeneralException;
	/**
	 * 花名册主页面的SQL
	 * @param musterType
	 * @return sql
	 * @throws GeneralException
	 */
	String getMusterMainSql(String musterType ,String moduleId)throws GeneralException;
	/**
	 * 获取花名册分类 集合
	 * @return
	 * @author wangb 2019-02-21
	 * @throws GeneralException
	 */
	List listMusterLstyle(String musterType) throws GeneralException;
	/**
	 * 根据子集的一个指标获取该子集当前用户权限内所有的指标
	 * @param fielditemid
	 * @return
	 * @throws GeneralException
	 */
	List getFielditemList(String fielditemid) throws GeneralException;
	   /**
     * 根据子集的一个指标获取该子集当前用户权限内所有的指标
     * @param fielditemid
     * @return
     * @throws GeneralException
     */
    List getItemList(String fielditemid) throws GeneralException;
	/**
	 * 获取指定分类下的花名册
	 * @param styleid 花名册分类id 加密
	 * @return
	 * @author wangb 2019-02-21
	 * @throws GeneralException
	 */
	List listMuster(String styleid,String musterType) throws GeneralException;
	/**
	 * 新建花名册页面展现信息获取
	 * @param operate 前台传过来的标识 对应要获取的数据
	 * @param tabid  花名册ID 某些操作需要
	 * @param musterType花名册类型  某些操作需要
	 * @param musterName花名册名称 某些操作需要
	 * @param FieldSet 子集ID 某些操作需要
	 */
	ArrayList addMusterInfo(int operate,String musterType, String musterName, String tabid,String FieldSet,String data) throws GeneralException;
	/**
	 * 删除花名册分类
	 * @param styleid 分类id 加密
	 * @author wangb 2019-02-21
	 * @throws GeneralException
	 */
	void deleteMusterLstyle(String styleid) throws GeneralException;
	
	/**
	 * 保存花名册分类
	 * @param data 花名册分类集合
	 * @return
	 * @author wangb 2019-02-21
	 * @throws GeneralException
	 */
	String saveMusterLstyle(Map data) throws GeneralException;
	/**
	 * 编辑信息的初始化
	 * @param lnameVo 存储需要的数据
	 * @param musterItem 保存的指标ID组成的字符串
	 * @throws GeneralException
	 */
	void saveMuster(RecordVo  lnameVo,String musterItem) throws GeneralException;
	/**
	 * 编辑花名册所需要的信息
	 * @param tabid 花名册id
	 * @param dataFlag 数据的标识   otherData = 其他相关数据 ； fieldData = 指标数据
	 * */
	ArrayList editMusterInit(String tabid,String dataFlag,String musterType) throws GeneralException;
	/**
	 * 更新花名册
	 * @param list 更新所用的数据集合
	 * @throws GeneralException
	 */
	void updataMuster(ArrayList list,String tabid,String musterItem) throws GeneralException;
	/**
	 * 删除花名册
	 * @param lnameVo 需要存入数据库的数据集合
	 * @param musterItem 指标ID拼接的字符串
	 * @throws GeneralException
	 */
	void deleteMuster(String tabid) throws GeneralException;
	/**
	 * 获得主页面列头
	 * @return ArrayList<ColumnsInfo>
	 * @throws GeneralException
	 */
	ArrayList<ColumnsInfo> getColumnList()throws GeneralException;
	/**
	 * 获取当前登录人的权限
	 * @param moduleId 0：员工管理；1：组织机构
	 * @return XX,XXX|XXX ：当前登录人的权限机构|权限机构的上级单位
	 * @throws GeneralException
	 */
	String getMusterPriv(String moduleId)throws GeneralException;

	/**
	 * 初始化花名册分类类型usterstyletype
	 */
	void updateMusterstyletype();
}
