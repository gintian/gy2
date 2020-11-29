package com.hjsj.hrms.module.system.sdparameter.utils;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class SDParameterUtils {

	private static HashMap SDParameterHM = null;

	/**
	 * 加载二开参数设置表数据缓存
	 * @param conn
	 */
	private static void getSDParameterData(Connection conn){
		if(SDParameterHM == null){
			SDParameterHM = new HashMap();
		}
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		try {
			rs = dao.search("select id,constant,describe,str_value from t_sys_sd_param");
			while(rs.next()){
				HashMap map = new HashMap();
				map.put("id",rs.getInt("id"));
				map.put("constant", rs.getString("constant"));
				map.put("describe", rs.getString("describe"));
				map.put("str_value", rs.getString("str_value"));
				SDParameterHM.put(rs.getString("constant"),map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取二开参数设置表记录缓存
	 * @param conn 
	 * @return
	 */
	public static HashMap getSDParameterHM(Connection conn){
		if(SDParameterHM == null){
			getSDParameterData(conn);
		}
		return SDParameterHM;
	}
	
	/**
	 * 更新二开参数设置表数据缓存
	 * @param conn
	 * @param type 操作类型     add 新增操作     delete 删除操作
	 */
	public static void updateSDParameterHM(Connection conn,String type,List parameterList){
		if(SDParameterHM == null){
			getSDParameterData(conn);
		}
		if("delete".equalsIgnoreCase(type)){
			for(int i = 0; i < parameterList.size(); i++){
				String constant = (String) parameterList.get(i);
				SDParameterHM.remove(constant);
			}
		}else if("add".equalsIgnoreCase(type)){
			for(int i = 0; i < parameterList.size(); i++){
				HashMap map = (HashMap) parameterList.get(i);
				SDParameterHM.put((String)map.get("constant"), map);
			}
		}else if("update".equalsIgnoreCase(type)){
			for(int i = 0; i < parameterList.size(); i++){
				HashMap map = (HashMap) parameterList.get(i);
				SDParameterHM.put((String)map.get("constant"), map);
			}
		}
	}
	/**
	 * 查询数据库中最大的id值
	 * */
	public static int getCount(Connection conn){
		int count = 0;
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		try {
			String sql = "select MAX(id) num from t_sys_sd_param";
			rs = dao.search(sql);
			while (rs.next()){
				count = rs.getInt("num");
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return count;
	}

	/**
	 * 查询数据库数据总数
	 * */
	public static int getDataCount(Connection conn){
		String sql = "";
		int countData = 0;
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			sql = "select COUNT(constant) count from t_sys_sd_param";
			rs = dao.search(sql);
			while(rs.next()){
				countData = rs.getInt("count");
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return countData;
	}
	
	/**
	 * 获取对应二开参数值
	 * @param conn 参数名称
	 * @param constant
	 * @return
	 * @throws GeneralException
	 */
	public static String getSDParameterValue(Connection conn,String constant) throws GeneralException {
		// TODO Auto-generated method stub
		HashMap map = (HashMap) getSDParameterHM(conn).get(constant);
		if(map == null) {
			return "";
		}
		return (String) map.get("str_value");
	}
	
}
