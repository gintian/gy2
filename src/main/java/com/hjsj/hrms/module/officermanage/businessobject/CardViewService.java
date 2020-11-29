package com.hjsj.hrms.module.officermanage.businessobject;

import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public interface CardViewService {
	
	/***
	 * 获取当前操作用户dbname
	 * @param nbases
	 * @return
	 * @throws Exception
	 */
	ArrayList getdbList(String nbases) throws Exception;

	/**
	 * 获取查询sql
	 * 
	 * @param queryString
	 * @param 人员库是否需要拼接
	 * @return
	 * @throws Exception
	 */
	StringBuffer getOfficerSql(String queryString, boolean isSbfNbase) throws Exception;

	/**
	 * 获取数据中存储xml中固定指标的代码项
	 * 
	 * @return
	 * @throws Exception
	 */
	HashMap<String, String> getMainFields() throws Exception;

	/**
	 * 根据nbase A0100 查询对应人员数据
	 * 
	 * @param nbase
	 * @param a0100
	 * @return
	 * @throws Exception
	 */
	LazyDynaBean getOfferData(String nbase, String a0100) throws Exception;

	/**
	 * 
	 * @param data_obj
	 *            干部任免表数据
	 * @param data
	 *            人员guidkey nbase
	 * @throws Exception
	 */
	void saveData(MorphDynaBean data_obj, MorphDynaBean data) throws Exception;

	/**
	 * 获取选中人员数据
	 * 
	 * @param guidkey
	 * @param nbase
	 * @param A0100
	 * @return
	 * @throws Exception
	 */
	LazyDynaBean getOfficerData(String guidkey, String nbase, String A0100) throws Exception;

	/**
	 * 获取人员图像所在路径
	 * 
	 * @param nbase
	 * @param a0100
	 * @param flag
	 *            url 生成浏览器显示需要的图片 file 导出word需要的图片
	 * @return
	 */
	String getPhotoPath(String nbase, String a0100, String flag);

	/***
	 * constant 表中获取 OFFICER_PARAM 自定义字段信息
	 */
	HashMap getConstantXMl() throws Exception;

	/***
	 * 
	 * @param dataList
	 * @param bo
	 * @param type
	 *            word / pdf
	 * @param filetype
	 *            all 多人一问答 1 一人一文档
	 * @return
	 * @throws Exception
	 */
	String outwordOrPdf(ArrayList<DynaBean> dataList, String type, String filetype)
			throws Exception;

	/**
	 * 
	 * @param dataList
	 *            数据
	 * @throws Exception
	 */
	String outFile(ArrayList<DynaBean> dataList) throws Exception;

	/**
	 * 第一页导出需要参数
	 */
	LazyDynaBean getFistPageBean();

	/***
	 * 第二页导出需要参数
	 */
	LazyDynaBean getSecPageBean();
}
