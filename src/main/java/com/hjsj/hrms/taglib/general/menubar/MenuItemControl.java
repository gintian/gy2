/**
 * 
 */
package com.hjsj.hrms.taglib.general.menubar;

import java.util.List;

/**
 * <p>Title:MenuItemControl</p>
 * <p>Description:菜单项接口</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-12-18:11:40:01</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public interface MenuItemControl {
    public abstract MenuItem addItem(String s, String s1);

    public abstract MenuItem addItem(String s);

    public abstract MenuItem getItem(String s);

    public abstract List items();
    
}
