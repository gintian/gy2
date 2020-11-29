package com.hjsj.hrms.service.syncdata;


import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.util.ArrayList;

public class SyncDataInter {
	public Connection conn= null;
	public SyncDataParam syncDataParam = null;
	/**
	 * 同步人员基本信息(新增、更新)
	 * @param datalist
	 * @return
	 */
	public String syncEmpDataAddOrUpdate(ArrayList<LazyDynaBean> datalist){

		return "";
	}
	/**
	 * 同步人员基本信息(删除)
	 * @param datalist
	 * @return
	 */
	public String syncEmpDataDelete(ArrayList<LazyDynaBean> datalist){
		return "";
	}
	
	/**
	 * 同步组织机构基本信息(新增、更新)
	 * @param datalist
	 * @return
	 */
	public String syncOrgDataAddOrUpdate(ArrayList<LazyDynaBean> datalist){
		return "";
	}
	/**
	 * 同步组织机构基本信息(删除)
	 * @param datalist
	 * @return
	 */
	public String syncOrgDataDelete(ArrayList<LazyDynaBean> datalist){
		return "";
	}
	/**
	 * 同步岗位机构基本信息(新增、更新)
	 * @param datalist
	 * @return
	 */
	public String syncPostDataAddOrUpdate(ArrayList<LazyDynaBean> datalist){
		return "";
	}
	/**
	 * 同步岗位机构基本信息(删除)
	 * @param datalist
	 * @return
	 */
	public String syncPostDataDelete(ArrayList<LazyDynaBean> datalist){
		return "";
	}
	/**
	 * 初始化类中需要的数据
	 */
	public void init(){
	    
	}
	void setConnection(Connection aconn){
		this.conn =aconn;
	}
	
	void setSyncDataParam(SyncDataParam asyncDataParam){
		this.syncDataParam =asyncDataParam;
		
	}
}