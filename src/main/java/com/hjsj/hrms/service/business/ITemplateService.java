package com.hjsj.hrms.service.business;


import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:发送表单数据外部接口标准</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:November  18, 2016</p> 
 *@author songjy
 *@version 1.0
 */
public interface ITemplateService {
	
	/**
	 * 
	 * @param userview  发起人UserView
	 * @param taskid 流程ID
	 * @param tabid 模板号
	 * @param lists 表单信息
	 * @return
	 */

	
	public boolean CreateProcessInstance(UserView userview,String taskid,String tabid,ArrayList<ArrayList<LazyDynaBean>> lists);
	
	/*
	 * 返回"true",表示成功
	 * 返回"false",表示失败
	 * 返回其他则表示为跳转url
	 */
	public String createProcessNavigateTo(UserView userview,String taskid,String tabid,ArrayList<ArrayList<LazyDynaBean>> lists);
	
	
	
}
