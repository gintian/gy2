/*
 * Created on 2005-5-25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.valueobject.tree;

import com.hjsj.hrms.utils.PubFunc;

import java.io.Serializable;
import java.util.Vector;





/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TreeItemView implements Serializable{
	
	/*private static String org_expand_level;
	static{
		org_expand_level=com.hrms.struts.constant.SystemConfig.getPropertyValue("org_expand_level");
	}*/
	private String name;//节点名字
	private String action;//链接
	private String text;//节点标签
	private String icon;//图标
	private String onicon;//图标
	private String openIcon; //展开图标
	private String target;//链接的target属性
	private String tag;//保留属性	
	private Vector children; 
	private String title;
	private String xml;
	private String rootdesc;
	/**
	 * @return Returns the rootdesc.
	 */
	public String getRootdesc() {
		return rootdesc;
	}
	/**
	 * @param rootdesc The rootdesc to set.
	 */
	public void setRootdesc(String rootdesc) {
		this.rootdesc = rootdesc;
	}
	/**
	 * @return Returns the xml.
	 */
	public String getXml() {
		return xml;
	}
	/**
	 * @param xml The xml to set.
	 */
	public void setXml(String xml) {
		if(xml!=null&&xml.indexOf("encryptParam")==-1){
			//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
			int index = xml.indexOf("&amp;");
			if(index>-1){
				String allurl = xml.substring(0,index);
				String allparam = xml.substring(index);
				xml=allurl+"&amp;encryptParam="+PubFunc.encrypt(allparam);
			}
			//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
		}
		this.xml = xml;
	}
	/**
	 * 不加密连接参数 取代setXml
	 * @param xml
	 */
	public void setSimpleXml(String xml) {
		this.xml = xml;
	}
	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	private TreeItemView parent;
	private String loadChieldAction;//加载child node的命令
	public  TreeItemView(){}
	public  TreeItemView(String strText){		
		this.text = strText; 
	}
	
	public  TreeItemView(String strName,String strText){
		this.name = strName;
		this.text = strText;		
	}
	
	//添加子节点
	public void addChild(TreeItemView node){
		if (children == null ) 
			children = new Vector();
		children.add(node);
		if (node.parent == null)
			node.parent = new TreeItemView();
		node.parent = this; 
	}
		
	//输出生成节点的JavaScript代码
	public String toJS(){
		StringBuffer strBf = new StringBuffer();
		strBf.append("var ");
		strBf.append(this.name);
		strBf.append("=new xtreeItem(\"root\",\"");
		strBf.append(this.rootdesc);
		strBf.append("\",");
		strBf.append("\"" + this.action + "\",");
		strBf.append("\"" + this.target + "\",");
		strBf.append("\"" + this.getText() + "\",");
		strBf.append("\"" + this.icon + "\",");
		strBf.append("\"" + this.loadChieldAction + "\");\n");
		strBf.append(this.name);
		strBf.append(".setup(document.getElementById(\"treemenu\"));\n");
		//strBf.append(this.name);   //chenmengqing changed at 0707
		//strBf.append(".openURL();");
		/*if("2".equals(TreeItemView.org_expand_level)){
			strBf.append(this.name); //xujian add 2010-12-3 如果system.properties文件中配置了org_expand_level=2，则组织机构树，支持默认展开第至二级
			strBf.append(".expand2level();");
		}*/
		return strBf.toString();
	}
	
//	输出生成节点的JavaScript代码
	public String toJS2(){
		StringBuffer strBf = new StringBuffer();
		strBf.append("var ");
		strBf.append(this.name);
		strBf.append("=new xtreeItem(\"root\",\"");
		strBf.append(this.rootdesc);
		strBf.append("\",");
		strBf.append("\"" + this.action + "\",");
		strBf.append("\"" + this.target + "\",");
		strBf.append("\"" + this.getText() + "\",");
		strBf.append("\"" + this.icon + "\",");
		strBf.append("\"" + this.loadChieldAction + "\");\n");
		strBf.append(this.name);
		strBf.append(".setup(document.getElementById(\"treemenu\"));\n");
	//	strBf.append(this.name);
	//	strBf.append(".openURL();");
		/*if("2".equals(TreeItemView.org_expand_level)){
			strBf.append(this.name); //xujian add 2010-12-3 如果system.properties文件中配置了org_expand_level=2，则组织机构树，支持默认展开第至二级
			strBf.append(".expand2level();");
		}*/
		return strBf.toString();
	}
	
	
	public String toChildNodeJS(){
		StringBuffer strBf = new StringBuffer();		  
		strBf.append("<TreeNode id=\"");
		strBf.append(this.name);
		strBf.append("\" text=\"");
		strBf.append(this.getText());
		strBf.append("\"");			
		if(!(this.title==null|| "".equals(this.title)))
		{
			strBf.append(" title=\"");
			strBf.append(this.title);
			strBf.append("\"");			
		}
		strBf.append(" href=\"");
		//System.out.println("action " + this.action);
		strBf.append(this.action);
		strBf.append("\" target=\"");
		strBf.append(this.target);
		strBf.append("\"");		
		if(!(this.icon==null|| "".equals(this.icon)))
		{
			strBf.append(" icon=\"");
			strBf.append(this.icon);
			strBf.append("\"");
		}
		/*if(this.onicon!=null&&!this.onicon.equals(""))
		{
			strBf.append(" onicon=\"");
			strBf.append(this.onicon);
			strBf.append("\"");
		}*/
		strBf.append(" xml=\"");
		strBf.append(this.xml);
		strBf.append("\" />");	
		//System.out.println("xml" + strBf.toString());
		return strBf.toString();
	}	

	/**
	 * @return Returns the action.
	 */
	public String getAction() {
		return action;
	}
	/**
	 * @param action The action to set.
	 */
	public void setAction(String action) {
		if(action!=null&&action.indexOf("encryptParam")==-1){
			//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
			int index = action.indexOf("&");
			if(index>-1){
				String allurl = action.substring(0,index);
				String allparam = action.substring(index);
				action=allurl+"&amp;encryptParam="+PubFunc.encrypt(allparam);
			}
			//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
		}
		this.action = action;
	}
	/**
	 * 不加密连接参数，取代setAction
	 * @param xml
	 */
	public void setSimpleAction(String action) {
		this.action = action;
	}
	/**
	 * @return Returns the children.
	 */
	public Vector getChildren() {
		return children;
	}
	/**
	 * @param children The children to set.
	 */
	public void setChildren(Vector children) {
		this.children = children;
	}
	/**
	 * @return Returns the icon.
	 */
	public String getIcon() {
		return icon;
	}
	/**
	 * @param icon The icon to set.
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Returns the openIcon.
	 */
	public String getOpenIcon() {
		return openIcon;
	}
	/**
	 * @param openIcon The openIcon to set.
	 */
	public void setOpenIcon(String openIcon) {
		this.openIcon = openIcon;
	}
	/**
	 * @return Returns the tag.
	 */
	public String getTag() {
		return tag;
	}
	/**
	 * @param tag The tag to set.
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}
	/**
	 * @return Returns the target.
	 */
	public String getTarget() {
		return target;
	}
	/**
	 * @param target The target to set.
	 */
	public void setTarget(String target) {
		this.target = target;
	}
	/**
	 * @return Returns the text.
	 */
	public String getText() {
		if(text!=null && text.trim().length()>0)
		  return text;
		else 
		  return "";
	}
	/**
	 * @param text The text to set.
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * @return Returns the loadChieldAction.
	 */
	public String getLoadChieldAction() {
		return loadChieldAction;
	}
	/**
	 * @param loadChieldAction The loadChieldAction to set.
	 */
	public void setLoadChieldAction(String loadChieldAction) {
		if(loadChieldAction!=null&&loadChieldAction.indexOf("encryptParam")==-1){
			//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
			int index = loadChieldAction.indexOf("&");
			if(index>-1){
				String allurl = loadChieldAction.substring(0,index);
				String allparam = loadChieldAction.substring(index);
				loadChieldAction=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
			}
			//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
		}
		this.loadChieldAction = loadChieldAction;
	}
	/**
	 * 不加密连接参数 取代setSimpleLoadchieldAction
	 * @param loadChieldAction
	 */
	public void setSimpleLoadChieldAction(String loadChieldAction) {
		this.loadChieldAction = loadChieldAction;
	}
	
	public String getOnicon() {
		return onicon;
	}
	public void setOnicon(String onicon) {
		this.onicon = onicon;
	}
	
}
