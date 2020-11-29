package com.hjsj.hrms.module.vuesupport.menumanager.dao.impl;

import com.hjsj.hrms.module.vuesupport.menumanager.dao.MenuManagerDao;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MenuManagerDaoImpl implements MenuManagerDao{

	private ContentDAO dao;
	private UserView userView;
	public MenuManagerDaoImpl(Connection conn, UserView userView) {
		dao = new ContentDAO(conn);
		this.userView = userView;
	}
	
	@Override
	public ArrayList listMenu(String id) throws Exception {
		// TODO Auto-generated method stub
		ArrayList menuList = new ArrayList();
		RowSet rs = null;
        try{
        	rs= this.dao.search("select * from t_sys_menu_manager where hide_flag=1 and menu_id like ? order by layer,norder,menu_id",Arrays.asList(id+"%"));
            while (rs.next()) {
                Map map = new HashMap();
                String menu_id = rs.getString("menu_id");
                String name = rs.getString("name");
                String parent_id = rs.getString("parent_id");
                String layer = rs.getString("layer");
                String icon = rs.getString("icon");
                String function_id = rs.getString("function_id");
                if(function_id !=null && function_id.trim().length()>0 && !userView.hasTheFunction(function_id)) {//不为空，校验全新啊
                    continue;
                }
                String link_flag = rs.getString("link_flag");
                String url = rs.getString("url");
                String hide_flag = rs.getString("hide_flag");
                String password_flag = rs.getString("password_flag");
                String target =  rs.getString("target");
                String norder = rs.getString("norder");
                map.put("menu_id", menu_id);
                map.put("name", name);
                map.put("icon", icon);
                map.put("parent_id", parent_id);
                map.put("layer", layer);
                map.put("function_id", function_id == null ? "":function_id);
                map.put("link_flag", link_flag);
                map.put("url", url == null ? "":url);
                map.put("password_flag", password_flag);
                map.put("hide_flag", hide_flag);
                map.put("norder", norder);
                map.put("target", target);
                menuList.add(map);
            }
        }catch (Exception e) {
        	e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
        return menuList;
	}

}
