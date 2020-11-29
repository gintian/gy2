package com.hjsj.hrms.businessobject.kq.set;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @see     该类是为了考勤 调整指标 编写的方法
 * 			在这里你可以获取 指标调整后 新的可显示的指标顺序
 * @author  郑文龙
 * @version 1.0
 * 
 */
public class AdjustCode {

	public static int ITEM_SAVE_D = 1; //获取数据库 指标状态
	
	public static int ITEM_SAVE_L = 2; //获取存在临时 指标状态
	
	/**
	 * 保存 考勤参数设置 调整指标 的显示与隐藏
	 * @param fieldlist 被选字段
	 * @param state     显示或隐藏的标识
	 * @return
	 * @throws SQLException 
	 * @throws GeneralException 
	 */
	public void SaveHideViewCode(ArrayList fieldlist,String[] state,Connection conn) throws SQLException{
		StringBuffer sql = new StringBuffer();
		sql.append("update t_hr_busifield set state=? where UPPER(FieldSetId)=? and UPPER(ItemId)=?");
		ArrayList list = new ArrayList();
		for (int i = 0; i < fieldlist.size(); i++) {
			FieldItem fielditem = (FieldItem) fieldlist.get(i);
			ArrayList onelist = new ArrayList();
			onelist.add(state[i]);
			onelist.add(fielditem.getFieldsetid().toUpperCase());
			onelist.add(fielditem.getItemid().toUpperCase());
			list.add(onelist);
		}
		ContentDAO dao = new ContentDAO(conn);
		dao.batchUpdate(sql.toString(), list);
	}
	
	/**
	 * 保存 考勤参数设置 调整指标 的顺序
	 * @param fieldlist 被选字段
	 * @param state     显示或隐藏的标识
	 * @return
	 * @throws SQLException 
	 * @throws GeneralException 
	 */
	public void SaveOrderCode(ArrayList code_fields,String table,Connection conn) throws SQLException{
		StringBuffer sql = new StringBuffer();
		sql.append("update t_hr_busifield set displayid=? where UPPER(FieldSetId)=? and UPPER(ItemId)=?");
		ArrayList list = new ArrayList();
		for (int i = 1; i <= code_fields.size(); i++) {
			ArrayList one_list = new ArrayList();
			String itemid = (String) code_fields.get(i - 1);
			if (itemid == null || itemid.length() <= 0) {
				continue;
			}
			one_list.add("" + i);
			one_list.add(table.toUpperCase());
			one_list.add(itemid.toUpperCase());
			list.add(one_list);
		}
		ContentDAO dao = new ContentDAO(conn);
		dao.batchUpdate(sql.toString(), list);
	}
	
	/**
	 * 得到可显示指标  已经排序
	 * @param table
	 * @return
	 */
	public ArrayList getFieldByView(String table){
		ArrayList fieldItemList = DataDictionary.getFieldList(table,
					Constant.USED_FIELD_SET);	
		ArrayList fieldItem = new ArrayList();
		for(Iterator it = fieldItemList.iterator();it.hasNext();){
			FieldItem field = (FieldItem)it.next();
			if("1".equals(field.getState())){
				fieldItem.add(field);
			}
		}
		return fieldItem;
	}

	/**
	 * 得到可显示指标  已经排序
	 * @param table
	 * @return
	 */
	public ArrayList getFieldByOrder(String table){
		ArrayList fieldItemList = DataDictionary.getFieldList(table,
					Constant.USED_FIELD_SET);
		ArrayList fieldItem = new ArrayList();
		for(Iterator it = fieldItemList.iterator();it.hasNext();){
			FieldItem field = (FieldItem)it.next();
			if(!"a0100".equals(field.getItemid())){
				fieldItem.add(field);
			}
		}
		return fieldItem;
	}
	
}
