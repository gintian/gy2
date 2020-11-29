/**
 * 
 */
package com.hjsj.hrms.taglib.general.menubar;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.io.InputStream;
import java.util.List;


/**
 * <p>Title:Menu</p>
 * <p>Description:菜单类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-12-18:11:58:50</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class Menu implements MenuItemControl {
	private String module_name;
	/**菜单条名称**/
	private String name;
	/**菜单条描述*/
	private String label;
	
	private MenuItem rootitem;
	/**菜单和功能权限相关*/
	private UserView userView;

	private static Category cat = Category.getInstance("com.hjsj.hrms.taglib.general.menubar.Menu");
	
	/**
	 * 
	 */
	public Menu(String module_name,String name,UserView userView) {
		rootitem=new MenuItem("root");
		this.name=name;
		this.label=name;
		this.module_name=module_name;
		this.userView=userView;
	}

	protected void init()
    {
        InputStream in=null;
        try{
        	in = this.getClass().getResourceAsStream("/com/hjsj/hrms/constant/menu.xml");
	        StringBuffer smatch=new StringBuffer();
	        smatch.append("//MenuBar[@name=\"");
	        smatch.append(this.module_name);
	        smatch.append("\"]");        
	        try
	        {
		        Document doc = PubFunc.generateDom(in);
		        Element root = doc.getRootElement();
		        XPath xpath = XPath.newInstance(smatch.toString());
		       // List list=xpath.selectNodes(root);
		        Element node=(Element)xpath.selectSingleNode(root);
		        if(node==null)
		        	return;
		        List list = node.getChildren("MenuItem");
		        for(int i=0;i<list.size();i++)
		        {
			        Element childnode = (Element) list.get(i);
			        loadItems(childnode,rootitem);
		        }
	        }
	        catch(Exception ex)
	        {
	        	ex.printStackTrace();
	        }
        }finally{
        	PubFunc.closeResource(in);
        }
    }

    protected void loadItems(Element node,MenuItem item)
    {
    	MenuItem citem=new MenuItem(node.getAttributeValue("name"),node.getAttributeValue("label"));
    	citem.setCommand(node.getAttributeValue("command"));
    	citem.setPath(node.getAttributeValue("path"));
    	if(node.getAttributeValue("enabled")==null|| "".equals(node.getAttributeValue("enabled")))
    		citem.setEnabled(true);
    	else
    		citem.setEnabled(Boolean.valueOf(node.getAttributeValue("enabled")).booleanValue());
    	if(node.getAttributeValue("visible")==null|| "".equals(node.getAttributeValue("visible")))
    		citem.setVisible(true);
    	else
    		citem.setVisible(Boolean.valueOf(node.getAttributeValue("visible")).booleanValue());
    	citem.setIcon_url(node.getAttributeValue("icon"));
    	citem.setFunc_id(node.getAttributeValue("functionId"));
    	item.items().add(citem);    	
    	List list=node.getChildren("MenuItem");
        for(int i=0;i<list.size();i++)
        {
	        Element childnode = (Element) list.get(i);
	        
	        loadItems(childnode,citem);
        }    	
    }
    /**
     * 如果功能号未定义或用户对象为空时,则权限有
     * 否则根据用户权限表和功能号进行分析.
     * @param func_id
     * @return
     */
    private boolean isHaveFunction(String func_id)
    {
    	if(this.userView==null||func_id==null|| "".equals(func_id))
    		return true;
    	return this.userView.hasTheFunction(func_id);
    }
    
	private void  outMenuItem(Element parent,MenuItem item)
	{
    	List itemlist=item.items();
    	if(itemlist.size()==0)
    		return;
    	for(int i=0;i<itemlist.size();i++)
    	{
    		MenuItem menuitem=(MenuItem)itemlist.get(i);
    		Element child=new Element("item");
    		child.setAttribute("name",menuitem.getName());
    		child.setAttribute("label",menuitem.getLabel());
    		if(!(menuitem.getCommand()==null|| "".equals(menuitem.getCommand())))
    			child.setAttribute("command",menuitem.getCommand());
    		if(!(menuitem.getPath()==null|| "".equals(menuitem.getPath())))
    			child.setAttribute("path",menuitem.getPath());
    		if(!(menuitem.getIcon_url()==null))    		
    			child.setAttribute("icon",menuitem.getIcon_url());
    		if(!(menuitem.getIcon_url()==null))
    		
    		child.setAttribute("enabled",String.valueOf(isHaveFunction(menuitem.getFunc_id()))/*String.valueOf(menuitem.isEnabled())*/);
    		child.setAttribute("visible",String.valueOf(menuitem.isVisible()));    	
    		parent.addContent(child);
    		outMenuItem(child,menuitem);
    	}
	}
	
    public String outMenuxml()
    {
    	StringBuffer strcontent=new StringBuffer();
    	init();
    	List itemlist=this.rootitem.items();
    	if(itemlist.size()==0)
    		return "";
    	Element root=new Element("xml");
    	root.setAttribute("id","__"+this.name);
    	Element childs=new Element("items");
    	root.addContent(childs);
    	//Document myDocument = new Document(root);

    	for(int i=0;i<itemlist.size();i++)
    	{
    		MenuItem menuitem=(MenuItem)itemlist.get(i);
    		Element child=new Element("item");
    		child.setAttribute("name",menuitem.getName());
    		child.setAttribute("label",menuitem.getLabel());
    		if(!(menuitem.getCommand()==null|| "".equals(menuitem.getCommand())))
    			child.setAttribute("command",menuitem.getCommand());
    		if(!(menuitem.getPath()==null|| "".equals(menuitem.getPath())))
    			child.setAttribute("path",menuitem.getPath());
    		if(!(menuitem.getIcon_url()==null))
    			child.setAttribute("icon",menuitem.getIcon_url());
    		child.setAttribute("enabled",String.valueOf(menuitem.isEnabled()));
    		child.setAttribute("visible",String.valueOf(menuitem.isVisible()));    	
    		outMenuItem(child,menuitem);
    		childs.addContent(child);
    	}
 	    XMLOutputter outputter = new XMLOutputter();
	    Format format=Format.getPrettyFormat();
	    format.setEncoding("UTF-8");
	    outputter.setFormat(format);   
	    strcontent.append(outputter.outputString(/*myDocument*/root));
    	return strcontent.toString();
    }
    

    
	public MenuItem addItem(String name, String label) {
        return rootitem.addItem(name, label);
	}

	public MenuItem addItem(String name) {
		return addItem(name,name);
	}

	public MenuItem getItem(String name) {
		return rootitem.getItem(name);
	}

    public MenuItem findItem(String s)
    {
        String as[];
        if((as = StringUtils.split(s, "/\\.")).length > 0)
        {
            MenuItem menuitem = rootitem;
            for(int i = 0; i < as.length && menuitem != null; i++)
                menuitem = menuitem.getItem(as[i]);
            return menuitem;
        } 
        else
        {
            return null;
        }
    }
    
   
	public List items() {
		return rootitem.items();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
