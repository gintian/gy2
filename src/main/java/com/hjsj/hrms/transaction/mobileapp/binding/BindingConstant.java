package com.hjsj.hrms.transaction.mobileapp.binding;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 绑定缓存类
 * @author imac
 *
 */
public class BindingConstant {

	//已经绑定认证记录
	private static HashMap bindingMap;
	//未认证申请绑定记录
	private static HashMap noAouthMap;
	
	/**
	 * 获取绑定状态
	 * @param username
	 * @return 0未绑定，跳转到新手机绑定界面  1已绑定，可以登录  2申请绑定未认证，提示等待认证 3与登录账号与绑定设备不一致，不允许登录
	 */
	public static int bindingStatus(String username,String deviceid){
		int flag = 0;
		if(bindingMap==null||noAouthMap==null){
			initMap();
		}
		if(bindingMap.containsKey(username)){
			if(deviceid.equals(bindingMap.get(username))){
				flag=1;
			}else{
				flag=3;
			}
		}else{
			if(noAouthMap.containsKey(username)){
				if(deviceid.equals(noAouthMap.get(username))){
					flag=2;
				}else{
					flag=0;
				}
			}else{
				flag=0;
			}
		}
		return flag;
	}
	
	/**
	 * 移动服务点击手工申请，插入待审绑定数据后更新缓存
	 */
	public static void applyAouth(String username,String idfv){
		if(noAouthMap==null){
			initMap();
		}else{
			bindingMap.remove(username);
			noAouthMap.put(username, idfv);
		}
	}
	
	/**
	 * 1、移动服务点击短信激活，插入已认证绑定数据后更新缓存
	 * 2、点击认证操作
	 */
	public static void smsAouth(String username,String idfv){
		if(bindingMap==null){
			initMap();
		}else{
			bindingMap.put(username, idfv);
			noAouthMap.remove(username);
		}
	}
	
	/**
	 * 移动服务删除数据时，更新待审绑定数据和已认证绑定数据缓存
	 * @Title: delAouth   
	 * @Description:    
	 * @param list 待删除的数据
	 * @return void
	 */
	public static void delAouth(ArrayList<String> list){
		for (int i = 0; i < list.size(); i++) {
			String username = (String) list.get(i);
			if(noAouthMap==null||bindingMap==null){
				initMap();
			}
			noAouthMap.remove(username);
			bindingMap.remove(username);
		}
	}
	
	private static void initMap(){
		Connection conn=null;
		RowSet rs = null;
		try{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sbsql = new StringBuffer();
			sbsql.append("select username,mobile_bind_id,mobile_is_oauth from t_sys_login_user_info where mobile_bind_id is not null");
			if(Sql_switcher.searchDbServer()==1){
				sbsql.append(" and mobile_bind_id<>''");
			}
			//sbsql.append(" and mobile_is_oauth=1");
			rs = dao.search(sbsql.toString());
			bindingMap = new HashMap();
			noAouthMap = new HashMap();
			while(rs.next()){
				int mobile_is_oauth = rs.getInt("mobile_is_oauth");
				if(1==mobile_is_oauth){
					bindingMap.put(rs.getString("username"), rs.getString("mobile_bind_id"));
				}else{
					noAouthMap.put(rs.getString("username"), rs.getString("mobile_bind_id"));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
