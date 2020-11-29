package com.hjsj.hrms.transaction.mobileapp.utils;

import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p> Title:GetQuickQueryFieldListTrans.java </p>
 * <p> Description>:从数据库中获取员工快速查询要显示的指标 </p>
 * <p> Company:HJSJ </p>
 * <p> Create Time:2013-11-11 上午11:16:31 </p>
 * 
 * @version: 1.0
 * @author:chenmq
 */
// 功能实现：yangj，2013-11-11
public class GetQuickQueryFieldListTrans extends IBusiness {

	private static final long serialVersionUID = 1L;
	/** 查询快速查询设置*/
	private final String GET_QUICK_QUERY = "1"; 

	public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
		String succeed = "false";
		String message = "";
		try {
			String transType = (String) hm.get("transType");
			hm.remove("transType");
			hm.remove("message");
			hm.remove("succeed");
			if (transType != null) {
				if (GET_QUICK_QUERY.equals(transType)) {// 根据人员权限查询人员
					ArrayList list = this.getQuickQuery();
					// 返回的list集合
					hm.put("fieldlist", list);
					hm.put("transType", GET_QUICK_QUERY);
					succeed = "true";
				}

			}

		} catch (Exception ex) {
			succeed = "false";
			String errorMsg = ex.toString();
			int index_i = errorMsg.indexOf("description:");
			message = errorMsg.substring(index_i + 12);
			hm.put("message", message);
		} finally {
			hm.put("succeed", succeed);
		}

	}

	/**
	 * 
	 * @Title: getQuickQuery   
	 * @Description: 获取快速查询条件
	 * @throws GeneralException 
	 * @return ArrayList
	 */
	private ArrayList getQuickQuery() throws GeneralException {
		/** 取得定义查询项,考虑指标权限 */
		ArrayList list = new ArrayList();
		// /**测试数据,换成从定义中参数取得对应的指标*/
		// FieldItem fielditem=DataDictionary.getFieldItem("A0107");
		// list.add(fielditem);
		// fielditem=DataDictionary.getFieldItem("A0111");
		// list.add(fielditem);
		// fielditem=DataDictionary.getFieldItem("C0101");
		// list.add(fielditem);
		// fielditem=DataDictionary.getFieldItem("A0177");
		// list.add(fielditem);
		// map.put("fieldlist", list);
		try {
			// 从常量表获取数据
			RecordVo vo = ConstantParamter.getRealConstantVo("SS_QUERYTEMPLATE");
			// 判断是否为空
			if (vo != null) 
			{
				String str_Value = vo.getString("str_value");
				if (str_Value != null && str_Value.length() > 0) 
				{
					// 分割字符串
					String[] str = str_Value.split(",");
					FieldItem fielditem;
					for (int i = 0, n = str.length; i < n; i++) 
					{
						// 判定指标权限
						String flag = this.userView.analyseFieldPriv(str[i]);
						if (!("0".equals(flag) || "".equals(flag))) 
						{
							fielditem = DataDictionary.getFieldItem(str[i]);
							// 判断是否构库
							if (!("0".equals(fielditem.getUseflag()))) 
							{
								list.add(fielditem);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}


}
