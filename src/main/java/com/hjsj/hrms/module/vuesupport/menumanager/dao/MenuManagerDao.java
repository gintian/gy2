package com.hjsj.hrms.module.vuesupport.menumanager.dao;

import java.util.ArrayList;

public interface MenuManagerDao {

	/**
     * 获取某个菜单id下所有菜单数据
     *
     * @throws Exception
     */
    public ArrayList listMenu(String menu_id) throws Exception;
	
}
