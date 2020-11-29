package com.hjsj.hrms.module.template.utils;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;

public interface BusinessService {
    /**流程发起*/
    public final static String OPT_APPLY  = "apply";
    /**流程报批*/
    public final static String OPT_APPEAL = "appeal";
    /**流程驳回*/
    public final static String OPT_REJECT = "reject";
    /**流程提交入库*/
    public final static String OPT_SUBMIT = "submit";
    
	/**
	 * 各业务类同步人事异动数据通用接口
	 * @param recordVoList  模板数据
	 * @param tabid 模板ID
	 * @param opt  apply:流程发起时调用  
	 *             appeal:流程报批时调用    
	 *             reject:流程驳回时调用   
	 *             submit:流程提交入库时调用
	 *             delete:删除表单人员
	 *             stop:流程终止或撤回
	 * @throws GeneralException
	 */
	void execution(ArrayList recordVoList,int tabid,String opt,UserView userview) throws GeneralException;

	
	 
	
	
	/** 
	 * 各业务类同步人事异动数据通用接口
	 * @param recordVoList  模板数据
	 * @param tabid 模板ID
	 * @param opt  apply:流程发起时调用  
	 *             appeal:流程报批时调用   
	 *             reject:流程驳回时调用   
	 *             submit:流程提交入库时调用
	 *             delete:删除表单人员
	 *             stop:流程终止或撤回
	 * @param busi_tab  业务表名 举例：Q15
	 * @param mapping_str   业务表指标与模板指标对应关系  Q1502:A0102_2,Q1503:A0302_2
	 * @throws GeneralException
	 */
	void execution(ArrayList recordVoList,int tabid,String opt,UserView userview,String busi_tab,String mapping_str) throws GeneralException;
	
}
