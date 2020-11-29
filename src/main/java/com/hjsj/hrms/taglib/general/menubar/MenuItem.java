/**
 * 
 */
package com.hjsj.hrms.taglib.general.menubar;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:MenuItem</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-12-18:11:41:40</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class MenuItem implements MenuItemControl {
	/**menuitem's name*/
	private String name;
	/**menuitem's label*/
	private String label;
	/**icon's url*/
	private String icon_url;
	/**菜单是否可见*/
	private boolean visible;
	/**菜单使能标识*/
	private boolean enabled;
	/**command*/
	private String command;
	/**打开新页面 for /xxxx/yyyy/aaa.do*/
	private String path;
	/**功能号*/
	private String func_id;
	private ArrayList itemlist;
	/**单击，点击一次就变灰*/
	private boolean onceclicked=false;
	/**
	 * 
	 */
	public MenuItem(String name) {
		this.name=name;
		this.label=name;
		this.visible=true;
		this.enabled=true;
		this.onceclicked=false;
		itemlist=new ArrayList();
	}
	public MenuItem(String name,String label)
	{
		this(name);
		this.label=label;
	}
	
	public MenuItem addItem(String name, String label) {
        MenuItem menuitem = new MenuItem(name, label);
        itemlist.add(menuitem);
        return menuitem;
	}

	public MenuItem addItem(String name) {
		return addItem(name,name);
	}

	public MenuItem getItem(String name) {
        MenuItem menuitem = null;
        List list;
        int j = (list = items()).size();
        for(int k = 0; k < j; k++)
        {
            MenuItem menuitem1 = (MenuItem)list.get(k);
            if(!name.equals(menuitem1.getName()))
                continue;
            menuitem = menuitem1;
            break;
        }
        return menuitem;
	}

	public List items() {
		return itemlist;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getIcon_url() {
		return icon_url;
	}

	public void setIcon_url(String icon_url) {
		this.icon_url = icon_url;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public ArrayList getList() {
		return itemlist;
	}

	public void setList(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public String getFunc_id() {
		return func_id;
	}
	public void setFunc_id(String func_id) {
		this.func_id = func_id;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public boolean isOnceclicked() {
		return onceclicked;
	}
	public void setOnceclicked(boolean onceclicked) {
		this.onceclicked = onceclicked;
	}

}
