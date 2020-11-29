package com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.dao;

import com.hrms.frame.dao.RecordVo;

import java.util.ArrayList;
import java.util.HashMap;

public interface ExcelLayoutDao {

	/**获取模板名称*/
	public String getTempletName(int tabid);
	/**获取 页数*/
	public ArrayList getPageIdList(int tabid,String noshow_pageno);
	
	/**获取page 设置信息*/
	public ArrayList getPageSetList(int tabid,String pageid);
	/**
	 * 获得模板信息
	 * @param tabid 模板ID
	 * @param conn
	 * @return
	 */
	public RecordVo getTableVo(int tabid);
	/**
	 * 获得模板所有私有和公有临时变量
	 * @param tabId 模板ID
	 * @return
	 */
	public HashMap getAllVariableHm(int tabId);
	/**
	 * 获得节点定义的指标必填项，变化后指标，无读值为0，写值为2，写并且必填值3
	 * @param task_id
	 * @return
	 */
  public HashMap getFieldPrivFillable(String task_id,int tabid);
  /**
   * 功能：根据代码项获取最大层级
   * @param codeid
   * @return
   */
  public int getLayerByCodesetid(String codeid);
  
  /**
   * 功能：根据代码项获取最大层级
   * @param codeid
   * @return
   */
  public int getMaxPageId(String tabId);
  /**
   * 功能：判断当前代码是否是末端代码，是的话返回false
   * @param codeid
   * @return
   */
  public boolean getLeafCode(String codeid);
}