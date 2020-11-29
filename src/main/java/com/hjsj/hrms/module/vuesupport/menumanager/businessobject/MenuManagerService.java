package com.hjsj.hrms.module.vuesupport.menumanager.businessobject;

import java.util.Map;

public interface MenuManagerService {

	/**
     * 获取某个菜单下全部子菜单
     * @param menu_id
     * @return
     * @throws Exception
     */
    Map getMenuAllData(String menu_id) throws Exception;
}
