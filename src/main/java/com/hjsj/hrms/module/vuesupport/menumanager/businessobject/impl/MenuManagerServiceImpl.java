package com.hjsj.hrms.module.vuesupport.menumanager.businessobject.impl;

import com.hjsj.hrms.module.vuesupport.menumanager.businessobject.MenuManagerService;
import com.hjsj.hrms.module.vuesupport.menumanager.dao.MenuManagerDao;
import com.hjsj.hrms.module.vuesupport.menumanager.dao.impl.MenuManagerDaoImpl;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MenuManagerServiceImpl implements MenuManagerService{

	MenuManagerDao menumanagerdao;
	UserView userView;

	public MenuManagerServiceImpl(Connection conn,UserView userView) {
		menumanagerdao = new MenuManagerDaoImpl(conn,userView);
		this.userView=userView; 
	}
	 
	@Override
	public Map getMenuAllData(String menu_id) throws Exception {
		// TODO Auto-generated method stub
		ArrayList listMenu = menumanagerdao.listMenu(menu_id);
	    Map menuMap = this.getMenuTree(menu_id, listMenu);
		return menuMap;
	}
	
	private Map getMenuTree(String menu_id,ArrayList listMenu) {
        HashMap menuMap = new HashMap();
        HashMap menuIdMap = new HashMap();
        if(StringUtils.isBlank(menu_id)) {
            menuMap.put("title","");
            menuMap.put("menuid","");
        }
        for (int i = 0; i < listMenu.size(); i++) {
            Map map = (Map) listMenu.get(i);
            String function_id = (String) map.get("function_id");
            if(StringUtils.isNotBlank(function_id) && !userView.hasTheFunction(function_id)) {//不为空，校验全新啊
                continue;
            }
            if(StringUtils.equalsIgnoreCase(menu_id, (String)map.get("menu_id"))) {
                menuMap.put("title",map.get("name"));
                menuMap.put("menuid",menu_id);
                continue;
            }
            Map tempMap = new HashMap();
            tempMap.put("title", map.get("name"));
            tempMap.put("menuid", map.get("menu_id"));
            tempMap.put("path", map.get("url"));
            tempMap.put("icon", map.get("icon"));
            tempMap.put("privfuncid", map.get("function_id"));
            tempMap.put("password_flag", map.get("password_flag"));
            tempMap.put("target", "0");
            String link_flag = (String) map.get("link_flag");
            String target = (String) map.get("target");
            if(StringUtils.equalsIgnoreCase("1", link_flag) && StringUtils.equalsIgnoreCase("1", target)) {
                tempMap.put("target", "1");
            }
            String parentid= (String) map.get("parent_id");
            if(StringUtils.equalsIgnoreCase(parentid, menu_id) || StringUtils.equalsIgnoreCase(parentid, (String)map.get("menu_id"))) {
                if(menuMap.containsKey("children")) {
                    ((ArrayList)menuMap.get("children")).add(tempMap);
                }else {
                    ArrayList list = new ArrayList();
                    list.add(tempMap);
                    menuMap.put("children", list);
                }
            }
            if(menuIdMap.containsKey(parentid)) {
                if(((HashMap)menuIdMap.get(parentid)).containsKey("children")) {
                   ((ArrayList)((HashMap)menuIdMap.get(parentid)).get("children")).add(tempMap);
                }else {
                    ArrayList list = new ArrayList();
                    list.add(tempMap);
//                    ((HashMap)menuIdMap.get(parentid)).remove("path");
                    ((HashMap)menuIdMap.get(parentid)).remove("target");
                    ((HashMap)menuIdMap.get(parentid)).put("children", list);
                }
            }
            menuIdMap.put(map.get("menu_id"), tempMap);
        }
        return menuMap;
    }

}
