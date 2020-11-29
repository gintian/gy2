/**
 * 
 */
package com.hjsj.hrms.taglib.general;

import com.hjsj.hrms.businessobject.sys.options.PortalTailorXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author cmq Jun 2, 20104:34:54 PM
 */
public class ExtPortalTag extends BodyTagSupport {

	/**
	 * extjs 门户面板规则
	 * 门户面板号 columns:整数,门户面板的列数,考虑屏幕大小,最大可支持3列 url:网页地址 icon:图标
	 * hide:是否显示,=true时，不管有没有权限前台都不显示.
	 * priv:是否需要权限控制,=true,授权给用户才能显示,=false,不用授权,对所有用户公开 colwidth:列宽比例 ...
	 * <portal id="01" name="业务平台" columns="1"> ... <column id="011"
	 * colwidth="0.5" name="第一列"> #最多只能存在三列 <panel id="011" name="A1"
	 * height="230" url="" icon="" hide="false" priv="true"/> <panel id="012"
	 * name="A2" height="230" url="" icon="" hide="false" priv="true"/> <panel
	 * id="013" name="A3" height="230" url="" icon="" hide="false" priv="true"/>
	 * </column> ... </portal> ...
	 */
	private String portalid;
	/**
	 * 针对每个用户的个性化设置是否显示 如021,022,.... 目前只对业务平台的面板有此控制
	 */
	private String hideids;
	private String[] showorder;// 面板显示顺序
	
	private boolean autoshow = true;
	/**
	 * 门户面板类型
	 * jquery
	 * ext
	 */
	private String portaltype="";
	public String getPortaltype() {
		return portaltype;
	}

	public void setPortaltype(String portaltype) {
		this.portaltype = portaltype;
	}

	/**
	 * 输出每列的内容面板 
	 * ... <panel id="011" name="A1" height="230" url="" icon="" hide="false" priv="true"/> ...
	 * new Ext.ux.Portlet({ title: '日常业务s40', html: '', layout:'fit',
	 * collapsible:true, height:230 })
	 * 
	 * @param parent
	 * @return
	 */
	private void outPortalColumnPanel(ArrayList allpanels, StringBuffer value,
			int num) {
		int base = allpanels.size();
		int rows = 0, cols = 0;
		cols = num % base;
		((ArrayList) allpanels.get(cols)).add(value);
	}

	/**
	 * 获取所有portal.xml文件中的面板
	 * 
	 * @param parent
	 * @return
	 */
	private void outPortalColumnPanel(Element parent, TreeMap map,
			ArrayList newpanels) {
		List list = parent.getChildren();
		UserView userview = (UserView) pageContext.getSession().getAttribute(
				WebConstant.userView);
		String bosflag = userview.getBosflag();
		EncryptLockClient lockclient=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
		int version = lockclient.getVersion();
		String framedegrade = SystemConfig.getPropertyValue("framedegrade");
		framedegrade = "true".equals(framedegrade)&&version<70?"true":"false";
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Element element = (Element) list.get(i);
				String height = element.getAttributeValue("height");
				if(height==null||height.length()==0){
					height="400";
				}
				String name = element.getAttributeValue("name");
				String id = element.getAttributeValue("id");
				String url = element.getAttributeValue("url");
				String icon = element.getAttributeValue("icon");
				String iconv7 = element.getAttributeValue("iconv7");
				String hide = element.getAttributeValue("hide");
				String priv = element.getAttributeValue("priv");
				String seftrender = element.getAttributeValue("seftrender");
				/** 此面板隐藏 */
				if ("true".equalsIgnoreCase(hide))
					continue;
				/** 此面板需要授权时 */
				if ("true".equalsIgnoreCase(priv)) {
					if (!userview.isSuper_admin()&&!userview.isHaveResource(IResourceConstant.PORTAL, id)) {
						continue;
					}
				}
				if("0234".equals(id)&&userview.getVersion()>=70) {//70锁隐藏我的任务
					continue;
				}
				if (this.hideids.indexOf(id) != -1)// xuj add 2010-7-14
													// 针对每个用户的个性化设置是否显示
					continue;
				//60锁是没有职称的，所以这里过滤
				if(version < 70 && url.indexOf("jobtitle") != -1)
					continue;
				StringBuffer buf = new StringBuffer();
				buf.append("new Ext.ux.Portlet({");
				buf.append("xtype:'portlet',id:'portlet-");
				buf.append(id);
				buf.append("',");
				if("hcm".equalsIgnoreCase(bosflag)){
					//此处需要根据平台接口面板配置图片显示
//					buf.append("title:'<div style=\"background:url(/images/hcm/themes/default/icon/"+iconv7+") no-repeat 1px 0;padding-left:20px;height:18px;line-height:18px;font-size:14px;\">");
					if("icon_business.png".equalsIgnoreCase(iconv7)){//判断 如果是大图  需要图片定位 wangb 20170815
						String position = element.getAttributeValue("position");
						String[] pos = position.split(",");
						buf.append("title:'<div style=\"height:18px;line-height:18px;font-size:14px;\"><div style=\"float:left;width:16px;height:16px;margin:1px 3px 0px 1px;background:url(/images/hcm/themes/default/icon/"+iconv7+") no-repeat "+pos[0]+"px "+pos[1]+"px;\"></div>");
					}else{//不是  显示默认图片 wang 20150815
						buf.append("title:'<div style=\"height:18px;line-height:18px;font-size:14px;\"><div style=\"float:left;width:16px;height:16px;margin:1px 3px 0px 1px;background:url(/images/hcm/themes/default/icon/"+iconv7+") no-repeat 0 0;\"></div>");
					}
				}else{
					buf.append("title:'<div style=\"font-size:14px;vertical-align: middle;\">");//liuy add (vertical-align: middle;) 2014-12-20
				}
				buf.append(name);
				buf.append("</div>',");
				if("hcm".equalsIgnoreCase(bosflag)){
					//暂时先只有我的任务采用加载完在显示more链接按钮的形式（如果其他也要采用可直接把此判断条件去掉）
					if(url.indexOf("flag=matter")!=-1)
						buf.append("tools:[{id:'tol"+id+"',xtype:'tool',hidden:true,");
					else
						buf.append("tools:[{id:'tol"+id+"',xtype:'tool',");
					buf.append("handler: function(e, target, panelHeader, tool){_more('iframe"+id+"')}}],");
				}
				/** html属性,采用iframe实现 */
				// portal.xml中配置了url则采用iframe方式；没有配置url时，采用Ext组件渲染方式 chent 20160523 start
				if(StringUtils.isEmpty(seftrender)){
					
					buf.append("html:'");
					buf.append("<iframe ");
					if("hcm".equalsIgnoreCase(bosflag)){
						buf.append("id=\"iframe"+id+"\"");
					}
					buf.append(" src=\"");
					buf.append(url);
					buf.append("\" width=\"100%\" height=\"100%\" frameborder=\"0\" ></iframe>");
					buf.append("',");
				} else{
					buf.append("items:[{xtype:'container',border:false,header:false,listeners :{afterrender:{fn:function(){ this.add(new JobtitlePortalURL.JobtitlePortal({}));}} }}");
					buf.append("],");
				}
				// portal.xml中配置了url则采用iframe方式；没有配置url时，采用Ext组件渲染方式 chent 20160523 end
				if("true".equals(framedegrade) && !"hcm".equals(userview.getBosflag())){
					buf.append("layout:'fit',collapsible:true,draggable:true,cls:'x-portlet',");
				}else{
					buf.append("layout:'fit',collapsible:true,cls:'x-portlet',");
				}
				//buf.append("anchor:'100%',");
				//buf.append("autoHeight:true,");
				buf.append("autoWidth:true,");
				if("hcm".equalsIgnoreCase(bosflag)){
					buf.append("titleCollapse:true,");
				}
				/** 面板内容高度 */
				buf.append("height:");
				buf.append(height);
				buf.append("})");

				int order = 0;
				boolean isnew = true;
				for (int o = 0; o < this.showorder.length; o++) {
					if (id.equals(this.showorder[o])) {
						isnew = false;
						order = o;
						break;
					}
				}
				if (isnew) {
					newpanels.add(buf);
				} else {
					isnew = true;
					map.put(new Integer(order), buf);
				}

			}// for i loop end.
		}
	}

	/**
	 * 自助用户面板
	 * @param parent
	 * @param colList
	 */
	private void outPortalColumnPanel(Element parent, ArrayList colList) {
		List list = parent.getChildren();
		UserView userview = (UserView) pageContext.getSession().getAttribute(
				WebConstant.userView);
		EncryptLockClient lockclient=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
		int version = lockclient.getVersion();
		String bosflag = userview.getBosflag();
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Element element = (Element) list.get(i);
				String height = element.getAttributeValue("height");
				if(height==null||height.length()==0){
					height="400";
				}
				String name = element.getAttributeValue("name");
				String id = element.getAttributeValue("id");
				String url = element.getAttributeValue("url");
				String icon = element.getAttributeValue("icon");
				String iconv7 = element.getAttributeValue("iconv7");
				String hide = element.getAttributeValue("hide");
				String priv = element.getAttributeValue("priv");
				String seftrender = element.getAttributeValue("seftrender");
				/** 此面板隐藏 */
				if ("true".equalsIgnoreCase(hide))
					continue;
				/** 此面板需要授权时 */
				if ("true".equalsIgnoreCase(priv)) {
					if (!userview.isSuper_admin()&&!userview.isHaveResource(IResourceConstant.PORTAL, id)) {
						continue;
					}
				}
				if("0234".equals(id)&&userview.getVersion()>=70) {//70锁隐藏我的任务
					continue;
				}
				if (this.hideids.indexOf(id) != -1)// xuj add 2010-7-14
													// 针对每个用户的个性化设置是否显示
					continue;
				
				//60锁是没有职称的，所以这里过滤
				if(version < 70 && url.indexOf("jobtitle") != -1)
					continue;
				
				StringBuffer buf = new StringBuffer();
				buf.append("new Ext.ux.Portlet({");
				buf.append("id:'portlet-");
				buf.append(id);
				buf.append("',");
				buf.append("style:{background:'white'},");
				if("hcm".equalsIgnoreCase(bosflag)){
					//此处需要根据平台接口面板配置图片显示
//					buf.append("title:'<div style=\"background:url(/images/hcm/themes/default/icon/"+iconv7+") no-repeat 1px 0;padding-left:20px;height:18px;line-height:18px;font-size:14px;\">");
					if("icon_business.png".equalsIgnoreCase(iconv7)){//如果是大图  图片需要定位获取 wangb 20170815
						String position = element.getAttributeValue("position");
						String[] pos = position.split(",");
						buf.append("title:'<div style=\"height:18px;line-height:18px;font-size:14px;\"><div style=\"float:left;width:16px;height:16px;margin:1px 3px 0px 1px;background:url(/images/hcm/themes/default/icon/"+iconv7+") no-repeat "+pos[0]+"px "+pos[1]+"px;\"></div>");
					}else{//不是 显示默认图片 wang 20170815
						buf.append("title:'<div style=\"height:18px;line-height:18px;font-size:14px;\"><div style=\"float:left;width:16px;height:16px;margin:1px 3px 0px 1px;background:url(/images/hcm/themes/default/icon/"+iconv7+") no-repeat 0 0;\"></div>");
					}
				}else{
					buf.append("title:'<div style=\"font-size:14px;vertical-align: middle;\">");//liuy add (vertical-align: middle;) 2014-12-20
				}
				buf.append(name);
				buf.append("</div>',");

				if("hcm".equalsIgnoreCase(bosflag)){
					//暂时先只有我的任务采用加载完在显示more链接按钮的形式（如果其他也要采用可直接把此判断条件去掉）
					if(url.indexOf("flag=matter")!=-1)
						buf.append("tools:[{id:'tol"+id+"',xtype:'tool',hidden:true,");
					else
						buf.append("tools:[{id:'tol"+id+"',xtype:'tool',");
					buf.append("handler: function(e, target, panelHeader, tool){_more('iframe"+id+"')}}],");
				}
				/** html属性,采用iframe实现 */
				// portal.xml中配置了url则采用iframe方式；没有配置url时，采用Ext组件渲染方式 chent 20160523 start
				if(StringUtils.isEmpty(seftrender)){
					
					buf.append("html:'");
					buf.append("<iframe ");
					if("hcm".equalsIgnoreCase(bosflag)){
						buf.append("id=\"iframe"+id+"\"");
					}
					buf.append(" src=\"");
					buf.append(url);
					buf.append("\" width=\"100%\" height=\"100%\" frameborder=\"0\" ></iframe>");
					buf.append("',");
				} else{
					buf.append("items:[{xtype:'container',border:false,header:false,listeners :{afterrender:{fn:function(){ this.add(new JobtitlePortalURL.JobtitlePortal({}));}} }}");
					buf.append("],");
				}
				// portal.xml中配置了url则采用iframe方式；没有配置url时，采用Ext组件渲染方式 chent 20160523 end
				buf.append("layout:'fit',collapsible:true,");
				buf.append("autoWidth:true,");
				if("hcm".equalsIgnoreCase(bosflag)){
					buf.append("titleCollapse:true,");
				}
				/** 面板内容高度 */
				buf.append("height:");
				buf.append(height);
				buf.append("})");

				colList.add(buf);
			}// for i loop end.
		}
	}

	private String getPath() {
		String classPath = "";
		try
		{
			classPath = this.getClass().getResource("").toString();
			/**目录有空格或汉字之类字符*/
			classPath=java.net.URLDecoder.decode(classPath,"utf-8"); 				
			//classPath = this.getClass().getResource("").toURI().getPath();//  toString();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		if (classPath.indexOf("hrpweb3.jar") != -1) {
			int beginIndex=-1,endIndex=-1;
			/**weblogic,环境布署时*/
			if(classPath.startsWith("zip:"))
			{
				beginIndex = classPath.indexOf("zip:") + 4;
				endIndex = classPath.lastIndexOf("hrpweb3.jar") + 11;
				classPath = classPath.substring(beginIndex, endIndex);				
			}
			else
			{
				Properties props=System.getProperties(); //系统属性
				String sysname = props.getProperty("os.name");
				if(sysname.startsWith("Win")){
					beginIndex = classPath.indexOf("/") + 1;
					endIndex = classPath.lastIndexOf("hrpweb3.jar") + 11;
					classPath = classPath.substring(beginIndex, endIndex);
					}else{
						beginIndex = classPath.indexOf("/") ;
						endIndex = classPath.lastIndexOf("hrpweb3.jar") + 11;
						classPath = classPath.substring(beginIndex, endIndex);
					}
			}
		} else {
			// @:file:/D:/Tomcat5.5/webapps/hrms/WEB-INF/classes/com/hjsj/hrms/businessobject/sys/bos/portal/
			Properties props=System.getProperties(); //系统属性
			String sysname = props.getProperty("os.name");
			//zhaoxj 20150514 开发环境下，windows平台需要去掉路径开头的“/”，其它如OSX，linux等不能去掉
			int beginIndex = classPath.indexOf("/");
			if(sysname.startsWith("Win")){
				beginIndex++;
			}
			
			if (classPath.indexOf("taglib") != -1) {
				int endIndex = classPath.lastIndexOf("taglib") - 1;
				String mixpath = "/constant/portal.xml";
				classPath = classPath.substring(beginIndex, endIndex) + mixpath;
			}
		}
		return classPath;
	}

	private Document getDocument() throws GeneralException {
		String file = this.getPath();
		Document doc = null;
		String EntryName = "com/hjsj/hrms/constant/portal.xml";
		InputStream in = null;
		JarFile jf = null;
		try 
		{
			/**cmq added for jboss eap6 at 20121019*/
		    String webserver=SystemConfig.getProperty("webserver");
		    if("jboss".equalsIgnoreCase(webserver)|| "inforsuite".equalsIgnoreCase(webserver))
		    {
	    		in=this.getClass().getResourceAsStream("/com/hjsj/hrms/constant/portal.xml");
		    }
		    else
		    {
				if (file.indexOf("hrpweb3.jar") != -1) {
						jf = new JarFile(file);
						Enumeration es = jf.entries();
						while (es.hasMoreElements()) {
							JarEntry je = (JarEntry) es.nextElement();
							if (je.getName().equals(EntryName)) {
								in = jf.getInputStream(je);
								break;
							}
							
						}
						
				}
		    }
			if (in == null) {
				in = new FileInputStream(file);
			}
			if (in == null)
				throw new GeneralException("NOT FOUND portal.xml FILE");
			doc = PubFunc.generateDom(in);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeIoResource(in);
			PubFunc.closeIoResource(jf);
		}

		return doc;

	}

	/**
	 * 输出门户面板的内容
	 * 
	 * @return
	 */
	private String outPortalJs() {
		StringBuffer buf = new StringBuffer();
		try {
			EncryptLockClient lockclient=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
			int version = lockclient.getVersion();
			String framedegrade = SystemConfig.getPropertyValue("framedegrade");
			framedegrade = "true".equals(framedegrade)&&version<70?"true":"false";
			Document doc = getDocument();
			String xpath = "//portal[@id=" + this.portalid + "]";
			XPath xPath = XPath.newInstance(xpath);
			Element rootnode = (Element) xPath.selectSingleNode(doc);
			String flag=rootnode.getAttributeValue("flag");
			UserView userview = (UserView) pageContext.getSession().getAttribute(WebConstant.userView);		
			if("p".equalsIgnoreCase(flag)){
				return this.outPortalHtml(rootnode, userview);
			}
			
			List list = rootnode.getChildren();
			String scol = rootnode.getAttributeValue("columns");
			if (scol == null || "".equalsIgnoreCase(scol))
				scol = "1";
			buf.append("portalid='"+this.portalid+"';");
			//buf.append("viiewportal = new Ext.Viewport({layout:'border',items:[{");
			buf.append("portalPanel = new Ext.ux.Portal({");
			if("true".equals(framedegrade) && !"hcm".equals(userview.getBosflag())){
				buf.append("xtype:'portal',region:'center',margins:'0 0 0 0',border: false,cls:'empty',items:[");
			}else{
				if("hcm".equals(userview.getBosflag())){
					buf.append("xtype:'portal',region:'center',margins:'-1 0 0 -1',bodyStyle:'padding:5 0 0 0;background:#F7F7F7;',border: false,cls:'empty',items:[");
				}else{
					buf.append("xtype:'portal',region:'center',margins:'0 0 0 0',bodyStyle:'padding:5 0 0 0',border: false,cls:'empty',items:[");
				}
			}

			/** 面板的列数 */
			int icol = Integer.parseInt(scol);
			if (list.size() == 0)
				return "";
			boolean bflag = false, badd = false;
			ArrayList allpanels = new ArrayList();
			ArrayList newpanels = new ArrayList();
			//xus 18/9/26 过滤掉工作桌面、服务大厅
			if("02".equals(this.portalid)){
				ArrayList newList = new ArrayList();
				for (int i = 0; i < list.size(); i++) {
					Element colchild = (Element) list.get(i);
					String attId = colchild.getAttributeValue("id");
					if(!"024".equals(attId)&&!"025".equals(attId)&&!"026".equals(attId)){
						newList.add(colchild);
					}
				}
				list=newList;
			}
			String[] swidths = new String[list.size()];
			if (this.showorder == null || this.showorder.length == 0) {// 用户初始化时默认按portal.xml 规定方式显示
				for (int i = 0; i < list.size(); i++) {
					allpanels.add(new ArrayList());
					Element colchild = (Element) list.get(i);
					String swidth = colchild.getAttributeValue("colwidth");
					swidths[i] = swidth;
					this.outPortalColumnPanel(colchild, (ArrayList) allpanels
							.get(i));
				}
				int firstColSize = ((ArrayList) allpanels.get(0)).size();

				for (int i = 1; i < allpanels.size(); i++) {
					ArrayList temps = (ArrayList) allpanels.get(i);
					while (temps.size() > firstColSize) {
						newpanels.add(temps.remove(temps.size() - 1));
					}
				}
			} else {
				// 获取所有要显示的面板 并且做好排序 新添加的默认在最后
				TreeMap allpanelmap = new TreeMap(); // 按键值自然顺序自动排序
				for (int i = 0; i < list.size(); i++) {
					allpanels.add(new ArrayList());
					Element colchild = (Element) list.get(i);
					String swidth = colchild.getAttributeValue("colwidth");
					swidths[i] = swidth;
					this.outPortalColumnPanel(colchild, allpanelmap, newpanels);
				}

				int num = 0;
				for (Iterator i = allpanelmap.values().iterator(); i.hasNext();) {
					StringBuffer value = (StringBuffer) i.next();
					outPortalColumnPanel(allpanels, value, num);
					num++;
				}

				// 填平列
				for (int i = 0; i < allpanels.size() - 1; i++) {
					if (((ArrayList) allpanels.get(i)).size() > ((ArrayList) allpanels
							.get(i + 1)).size()) {
						if (newpanels.size() > 0) {
							((ArrayList) allpanels.get(i + 1))
									.add((StringBuffer) newpanels.get(0));
							newpanels.remove(0);
						}
					}
				}
			}
			int base = list.size();
			for (int i = 0; i < newpanels.size(); i++) {
				int cols = i % base;
				((ArrayList) allpanels.get(cols)).add((StringBuffer) newpanels
						.get(i));
			}
			ArrayList panels = new ArrayList();
			for (int i = 0; i < allpanels.size(); i++) {
				StringBuffer pnl = new StringBuffer();
				ArrayList cols = (ArrayList) allpanels.get(i);
				for (int c = 0; c < cols.size(); c++) {
					if (bflag)
						pnl.append(",");
					pnl.append(cols.get(c));
					bflag = true;
				}
				bflag = false;
				panels.add(pnl);
			}
			for (int i = 0; i < panels.size(); i++) {
				String panel = ((StringBuffer) panels.get(i)).toString();
				if (panel.length() > 0) {
					if (bflag)
						buf.append(",");
					buf.append("new Ext.ux.PortalColumn({columnWidth:");
					buf.append(swidths[i]);
					if("true".equals(framedegrade) && !"hcm".equals(userview.getBosflag())){
					buf.append(",style:'padding:5px 12px 12px 12px;',");
					}else{
						if("hcm".equals(userview.getBosflag())){
							if(i == 0) {
								if(i == (panels.size() - 1)) {
									buf.append(",padding:'6 10 10 10',");
								} else {
									buf.append(",padding:'6 5 10 10',");
								}
							} else {
								if(i == (panels.size() - 1)) {
									buf.append(",padding:'6 10 10 5',");
								} else {
									buf.append(",padding:'6 5 10 5',");
								}
							}
						}else{
							/* hr首页线条调整 xiaoyun 2014-7-25 start */
							//buf.append(",padding:'5 12 12 12',");
							buf.append(",padding:'5 10 12 10',");
							/* hr首页线条调整 xiaoyun 2014-7-25 end */
						}
					}
					buf.append("items:[");
					buf.append(panel);
					buf.append("]})");
					bflag = true;
					badd = true;
				} else {
					bflag = false;
				}
			}// for i loop end.

			buf.append("]");
			//buf.append("}]});");
			buf.append("});");
				
			// buf.append(" viewport.render();");
			/** 如果没有面板内容则不输出portal javascript代码 */
			if (!badd)
				buf.setLength(0);

			/*
			 * var fcolum= new Ext.ux.PortalColumn({ columnWidth:.50,
			 * style:'padding:10px 10px 100px 10px;', items:[item1,item2,item3,
			 * new Ext.ux.Portlet({ title: '日常业务s40', html: '', layout:'fit',
			 * collapsible:true, height:230 }) ]});
			 * 
			 * var scolum= new Ext.ux.PortalColumn({ columnWidth:.50,
			 * style:'padding:10px 10px 100px 10px;',
			 * items:[items1,items2,items3,items4] }); var viewport = new
			 * Ext.Viewport({ layout:'border', items: [{ xtype:'portal',
			 * region:'center', margins:'0 0 0 0', border: true, cls:'empty',
			 * items: [ fcolum,scolum ] }] });
			 */

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return buf.toString();
	}

	/**
	 * 门户面板定义
	 * <portal id="01" name="总裁平台" columns="1" colwidth="1">
	 * 	  	 <column id="011" colwidth="0.5" name="第一列">
	 *         	   <panel id="0110" name="xx" height="350" url="" icon="" hide="false" priv="false"/>
     *			   .......
	 *       </column>
	 * 	  	 <column id="012" colwidth="0.5" name="第二列">
	 *         	   <panel id="0110" name="xx" height="350" url="" icon="" hide="false" priv="false"/>
     *			   .......
	 *       </column>     
	 *       ......
	 * </portal> 
	 * 转换成JSON对象格式如下：
	 * [
	 *  {
	 *    id:'xxx',
	 *    colwidth:20,
	 *    name:'',
	 *    children:[
	 *    {
	 *         id:'',
	 *         name:'',
	 *         height:200,
	 *         url:'',
	 *         iconCls:''
	 *    },
	 *       ......
	 *    ]
	 *  },
	 *  {
	 *   ......
	 *  } 
	 * ]
	 */
	private String outJqueryPortalJs() {
		StringBuffer buf = new StringBuffer();
		UserView userview = (UserView) pageContext.getSession().getAttribute(WebConstant.userView);		
		try 
		{
			Document doc = getDocument();
			String xpath = "//portal[@id=" + this.portalid + "]";
			XPath xPath = XPath.newInstance(xpath);
			Element rootnode = (Element) xPath.selectSingleNode(doc);
			String flag=rootnode.getAttributeValue("flag");
			if("p".equalsIgnoreCase(flag)){
				return this.outPortalHtml(rootnode, userview);
			}
			List list = rootnode.getChildren();
			String scol = rootnode.getAttributeValue("columns");
			if (scol == null || "".equalsIgnoreCase(scol))
				scol = "1";
			buf.append("var portals=");
			buf.append("[");
			for (int i = 0; i < list.size(); i++) {
				if(i!=0)
					buf.append(",");
				buf.append("{");				
				Element columnchild = (Element) list.get(i);
				buf.append("id:'");
				buf.append(columnchild.getAttributeValue("id"));
				buf.append("',");
				buf.append("colwidth:");
				buf.append(columnchild.getAttributeValue("colwidth"));
				buf.append(",");				
				buf.append("name:'");
				buf.append(columnchild.getAttributeValue("name"));
				List panellist = columnchild.getChildren();
				if(panellist.size()!=0)
					buf.append("',children:[");				
				/**门户面板*/
				boolean badd=false;
				for(int j=0;j<panellist.size();j++)
				{
					Element panelchild = (Element) panellist.get(j);
					String hide = panelchild.getAttributeValue("hide");
					String priv = panelchild.getAttributeValue("priv");
					/** 此面板隐藏 */
					if ("true".equalsIgnoreCase(hide))
						continue;
					/** 此面板需要授权时 */
					if ("true".equalsIgnoreCase(priv)) {
						if (!userview.isSuper_admin()&&!userview.isHaveResource(IResourceConstant.PORTAL, panelchild.getAttributeValue("id"))) {
							continue;
						}
					}					
					if(badd)
						buf.append(",");						
					buf.append("{");
					buf.append("id:'");
					buf.append(panelchild.getAttributeValue("id"));
					buf.append("',");
					buf.append("name:'");
					buf.append(panelchild.getAttributeValue("name"));
					buf.append("',");					
					buf.append("url:'");
					buf.append(panelchild.getAttributeValue("url"));
					buf.append("',");	
					buf.append("height:");
					buf.append(panelchild.getAttributeValue("height"));
					buf.append(",");	
					buf.append("iconCls:'");
					buf.append(panelchild.getAttributeValue("icon"));
					buf.append("'}");						
					badd=true;
				}//门户面板 loop j end.
				if(columnchild.getChildren().size()!=0)
					buf.append("]");
				buf.append("}");				
			}//for loop i 
			buf.append("];");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return buf.toString();
	}
	public String outPortalHtml(Element rootnode,UserView userView){
		StringBuffer buf = new StringBuffer();
		userView.getHm().put("isEpmLoginFlag", "1");
        try{
        	String scol = rootnode.getAttributeValue("columns");
			if (scol == null || "".equalsIgnoreCase(scol))
				scol = "1";
			List list = rootnode.getChildren();
			buf.append("<table align=\"center\" valign=\"middle\" width=\"95%\" cellspacing=\"0\" cellpadding=\"0\" >");
			buf.append("<tr><td height=\"3\"></td></tr>");
			buf.append("<tr>");
			for(int i=0;i<list.size();i++){
				Element column=(Element)list.get(i);
				String colwidth=column.getAttributeValue("colwidth");
				buf.append("<td align=\"center\" valign=\"top\" width=\""+getPercentWidth(colwidth)+"\">");
				List panelList  = column.getChildren();
				for(int j=0;j<panelList.size();j++){
					Element panel = (Element)panelList.get(j);
					String hide = panel.getAttributeValue("hide")==null?"":panel.getAttributeValue("hide");
					String priv = panel.getAttributeValue("priv")==null?"":panel.getAttributeValue("priv");
					String id=panel.getAttributeValue("id")==null?"":panel.getAttributeValue("id");
					String url = panel.getAttributeValue("url")==null?"":panel.getAttributeValue("url");
					String mod_id=panel.getAttributeValue("mod_id")==null?"-1":panel.getAttributeValue("mod_id");
					String more = panel.getAttributeValue("more")==null?"":panel.getAttributeValue("more");
					/** 此面板隐藏 */
					if ("true".equalsIgnoreCase(hide))
						continue;
					/** 此面板需要授权时 */
					if ("true".equalsIgnoreCase(priv)) {
						if (!userView.isSuper_admin()&&!userView.isHaveResource(IResourceConstant.PORTAL, id)) {
							continue;
						}
					}		
					//setNavigation(fromurl,frommodeid)
					String panelheight=panel.getAttributeValue("height");
					if("".equals(panelheight)||panelheight==null)
						panelheight="400";
					buf.append("<table width=\"100%\" height=\""+panelheight+"\" align=\"center\"  cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">");
					buf.append("<tr>");
				    buf.append("<td align=\"left\" width=\"100%\" height=\"27\" valign=\"middle\" class=\"epm-panel-title\" nowrap>");
				    buf.append("<span><a href=\"javascript:setNavigation('"+more+"','"+mod_id+"');\">更多》</a></span>&nbsp;&nbsp;<img src=\"/images/epm-shu-01.gif\" border=\"0\"/>&nbsp;"+panel.getAttributeValue("name"));
				    //buf.append("<div class=\"epm-title\">");
					//buf.append("<div class=\"epm-title-left\"></div>");
				    //buf.append("<div class=\"epm-title-center\"><span><a href=\"javascript:setNavigation('','"+mod_id+"');\">更多》</a></span>"+panel.getAttributeValue("name")+"</div>");
				  //  buf.append("<div class=\"epm-title-right\"></div>");
				  //  buf.append("</div>");
					buf.append("</td></tr>");
					buf.append("<tr><td align=\"center\" width=\"100%\" height=\""+(Double.parseDouble(panelheight)-27)+"\" class=\"panel_border_p\">");
					if(url.length()>0){
						buf.append("<iframe src=\"");
						buf.append(url);
						buf.append("\" scrolling=\"auto\" width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>");
					}else{
						buf.append("<div style=\"height:"+(Double.parseDouble(panelheight)-27)+"px;width:100%;overflow-y:auto;overflow-x:hidden;\">");
						buf.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">");
					    List menuList = panel.getChildren();
					    for(int k=0;k<menuList.size();k++){
					    	Element menu=(Element)menuList.get(k);
					    	String func_id = menu.getAttributeValue("func_id")==null?"":menu.getAttributeValue("func_id");
					    	if(!"".equals(func_id)&&!userView.hasTheFunction(func_id))
					    		continue;
							List childList = menu.getChildren();
							if(childList.size()>0){
								StringBuffer buf_t = new StringBuffer("");
								this.isContinue(menu, userView, buf_t);
								if(buf_t.length()>0){
									buf.append("<tr>");
									buf.append("<td width=\"100%\" height=\"30\" align=\"left\" valign=\"middle\" class=\"panel_menu\" >");
									buf.append("<img src=\"/images/tree_expand.gif\" width=\"16\" height=\"16\" onclick=\"expendChildMenu(this,'"+menu.getAttributeValue("id")+"');\"/>&nbsp;");
									buf.append(menu.getAttributeValue("name"));
									buf.append("</td></tr>");
									this.outChildMenu(buf, childList, menu.getAttributeValue("id"), 0,userView,mod_id);
									buf.append("</td></tr>");
								}
							}else{
								buf.append("<tr>");
								buf.append("<td width=\"100%\" height=\"30\" align=\"left\" valign=\"middle\" class=\"panel_menu\" >");
								if(menu.getAttributeValue("icon")!=null&&menu.getAttributeValue("icon").length()>0)
						    		buf.append("&nbsp;&nbsp;<img src=\"/images/"+menu.getAttributeValue("icon")+"\" valign=\"middle\" border=\"0\"/>&nbsp;&nbsp;");
								else
									buf.append("&nbsp;&nbsp;&nbsp;");
								if(menu.getAttributeValue("url")!=null&&menu.getAttributeValue("url").trim().length()>0){
							        buf.append("<a href=\"javascript:setNavigation('"+menu.getAttributeValue("url")+"','"+mod_id+"');\" ");
							        if(menu.getAttributeValue("target")!=null&&menu.getAttributeValue("target").trim().length()>0){
							        	buf.append(" target=\""+menu.getAttributeValue("target")+"\"");
							        }
							        buf.append(">"+menu.getAttributeValue("name")+"</a>");
								}else{
									 buf.append(menu.getAttributeValue("name"));
								}
								buf.append("</td></tr>");
							}
					    }
					    buf.append("</table>");
						buf.append("</div>");
					}
					buf.append("</td>");
					buf.append("</tr>");
					buf.append("<tr><td height=\"5\"></td></tr>");
					buf.append("</table>");
				}
				buf.append("</td>");
				buf.append("<td width=\"1%\" >&nbsp;</td>");
			}
			buf.append("</tr></table>");
        }catch(Exception e){
        	e.printStackTrace();
        }
        return buf.toString();
	}
	public void outChildMenu(StringBuffer buf,List childList,String parentMenuid,int lay,UserView userView,String mod_id){
		buf.append("<tr id=\""+parentMenuid+"\" style=\"display:none;\">");
		buf.append("<td width=\"100%\" height=\"30\" align=\"left\" >");
		buf.append("<table width=\"100%\" align=\"center\" >");
		lay++;
		for(int i=0;i<childList.size();i++){
			Element element=(Element)childList.get(i);
			String func_id = element.getAttributeValue("func_id")==null?"":element.getAttributeValue("func_id");
	    	if(!"".equals(func_id)&&!userView.hasTheFunction(func_id))
	    		continue;
			List list = element.getChildren();
			if(list.size()>0){
				StringBuffer buf_t = new StringBuffer("");
				this.isContinue(element, userView, buf_t);
				if(buf_t.length()>0){
					buf.append("<tr>");
					buf.append("<td width=\"100%\" height=\"30\" align=\"left\" valign=\"middle\" class=\"panel_menu\" >");
					for(int j=0;j<=lay;j++){
						buf.append("&nbsp;&nbsp;");
					}
					buf.append("<img src=\"/images/tree_expand.gif\" width=\"16\" height=\"16\" onclick=\"expendChildMenu(this,'"+element.getAttributeValue("id")+"');\"/>&nbsp;");
					buf.append(element.getAttributeValue("name"));
					buf.append("</td></tr>");
					this.outChildMenu(buf, list, element.getAttributeValue("id"), lay,userView,mod_id);
					buf.append("</td></tr>");
				}
			}else{
				buf.append("<tr>");
				buf.append("<td width=\"100%\" height=\"30\" align=\"left\" valign=\"middle\" class=\"panel_menu\" >");
				for(int j=0;j<=lay;j++){
					buf.append("&nbsp;&nbsp;");
				}
				if(element.getAttributeValue("icon")!=null&&element.getAttributeValue("icon").length()>0)
		    		buf.append("<img src=\"/images/"+element.getAttributeValue("icon")+"\" valign=\"middle\" border=\"0\"/>&nbsp;&nbsp;");
				else
					buf.append("&nbsp;&nbsp;&nbsp;");
				if(element.getAttributeValue("url")!=null&&element.getAttributeValue("url").trim().length()>0){
			        buf.append("<a href=\"javascript:setNavigation('"+element.getAttributeValue("url")+"','"+mod_id+"');\" ");
			        if(element.getAttributeValue("target")!=null&&element.getAttributeValue("target").trim().length()>0){
			        	buf.append(" target=\""+element.getAttributeValue("target")+"\"");
			        }
			        buf.append(">"+element.getAttributeValue("name")+"</a>");
				}else{
					 buf.append(element.getAttributeValue("name"));
				}
				buf.append("</td></tr>");
			}
			
		}
		buf.append("</table></td></tr>");
	}
	public ExtPortalTag() {
		super();
	}
	public void isContinue(Element menu,UserView userView,StringBuffer buf_t){//如果一个父菜单的所有子菜单都没有权限，则不显示了
		try{
			if(buf_t.length()>0)
				return;
			List childList = menu.getChildren();
			for(int i=0;i<childList.size();i++){
				Element el = (Element)childList.get(i);
				String func_id=el.getAttributeValue("func_id");
				if("".equals(func_id)||userView.hasTheFunction(func_id)){
					buf_t.append("0");
				}
				this.isContinue(el, userView, buf_t);	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public String getPortalid() {
		return portalid;
	}

	public void setPortalid(String portalid) {
		this.portalid = portalid;
	}
    public String getPercentWidth(String width){
    	String str="";
    	try{
    		BigDecimal a = new BigDecimal(width);
    		BigDecimal b = new BigDecimal("100");
    		str=a.multiply(b).setScale(2, RoundingMode.HALF_UP).toString()+"%";
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return str;
    }
	public int doStartTag() throws JspException {
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			String hideandorder = new PortalTailorXml()
					.ReadOutParameterXmlHideValueAndShowOrder(conn, ((UserView) pageContext.getSession().getAttribute(WebConstant.userView)).getUserName(), this.portalid);
			String[] temps = hideandorder.split("`");
			//隐藏工作桌面和服务大厅   isHideTableAndHall 不设置或者为false，则显示，为true，则隐藏
			String isHideTableAndHall=SystemConfig.getPropertyValue("isHideTableAndHall");
			/*
			 * hideanorder样式为XXX`时，split返回的数组只有一个元素[XXX];
			 *            样式为`XXX时，split返回的数组有两个元素[,XXX];
			 *            样式为XXX`YYY时，split返回的数组有两个元素[XXX,YYY];
			 * 因此当portal全部为隐藏时（即XXX`样式），判断temps.length == 2是错误的
			 * 反而导致portal全部显示
			 * zxj changed at 2013-05-22
			 */
			if (temps.length >= 1) {
				this.hideids = temps[0];
				if (temps.length == 2)
				    this.showorder = temps[1].split(",");
				else
				    this.showorder = null;
			} else {
				this.hideids = "";
				this.showorder = temps;
			}
			StringBuffer buf = new StringBuffer();
			String js;
			/**jquery portal,调整顺序可能针对jquery portal无效
			 * cmq changed at 2012-09-26  
			 */
			portalCode:if("jquery".equalsIgnoreCase(this.portaltype))
				js = outJqueryPortalJs();				
			else{
				js = outPortalJs();
				
				UserView userview = (UserView) pageContext.getSession().getAttribute(
						WebConstant.userView);
				String bosflag = userview.getBosflag();
				//如果不是业务平台,或者不是hcm，直接viewport显示
				if(!"02".equals(this.portalid)  || !"hcm".equals(bosflag)){
					js = js.concat(js.length()>0?"\n viiewportal = new Ext.Viewport({layout:'border',items:portalPanel});\n":"");
					break portalCode;
				}
				
				String worktableStr = null;
				String servicehallStr = null;
				String templateTableStr=null;
				
				//xus 18/9/26 门户定制 增加工作桌面、服务大厅
				Document doc = getDocument();
				String xpath = "//portal[@id=" + this.portalid + "]";
				XPath xPath = XPath.newInstance(xpath);
				Element rootnode = (Element) xPath.selectSingleNode(doc);
				List list = rootnode.getChildren();
				boolean isWorkTableInUse=false;
				boolean isServiceHallInUse=false;
				boolean isTemplateTableUse=false;
				if("02".equals(this.portalid)){
					for (int i = 0; i < list.size(); i++) {
						Element colchild = (Element) list.get(i);
						String attId = colchild.getAttributeValue("id");
						//工作桌面
						if("024".equals(attId)){
							isWorkTableInUse="false".equals(colchild.getAttributeValue("hide"))?true:false;
						}
						//服务大厅
						if("025".equals(attId)){
							isServiceHallInUse="false".equals(colchild.getAttributeValue("hide"))?true:false;;
						}
						if("026".equals(attId)) {
							isTemplateTableUse="false".equals(colchild.getAttributeValue("hide"))?true:false;
						}
					}
				}
				//当显示参数数组长度为0时，赋给默认值
				temps = temps.length==0?new String[]{"",""}:temps;
				if(isWorkTableInUse&&temps[0].indexOf("worktable")==-1&&!"true".equals(isHideTableAndHall)){
					worktableStr = " var workTable = Ext.create('EHR.homewidget.WorkTable'); \n";
				}
				if(isServiceHallInUse&&temps[0].indexOf("servicehall")==-1&&!"true".equals(isHideTableAndHall)){
					servicehallStr = " var serviceHall = Ext.create('EHR.homewidget.ServiceHall'); \n";
				}
				if(isTemplateTableUse&&temps[0].indexOf("templatetable")==-1) {
					templateTableStr=" var templateTable = Ext.create('EHR.homewidget.TemplateTable'); \n";
				}
				//如果是业务平台，不显示工作桌面和服务大厅，直接viewport显示
				if((worktableStr==null && servicehallStr==null&&templateTableStr==null)){
					js = js.concat(js.length()>0?"\n viiewportal = new Ext.Viewport({layout:'border',items:portalPanel});\n":"");
					break portalCode;
				}
				
				/**
				 * 走到这里表示是业务平台，并且工作桌面和服务大厅至少显示一个，根据情况加载显示面板
				 */
				StringBuffer htmlStr = new StringBuffer();
				htmlStr.append("\n var portalItems = []; \n");
				
				if(worktableStr!=null){
					htmlStr.append(worktableStr);
					htmlStr.append(" portalItems.push(workTable); \n");
				}
				if(servicehallStr!=null){
					htmlStr.append(servicehallStr);
					htmlStr.append(" portalItems.push(serviceHall); \n");
				}
				if(templateTableStr!=null) {
					htmlStr.append(templateTableStr);
					htmlStr.append(" portalItems.push(templateTable); \n");
				}
				
				
				if(js.length()>0){
					htmlStr.append(" portalPanel.border = false; portalPanel.style='border:none;'; \n");
					htmlStr.append(" portalItems.push({xtype:'container',id:'viiewportal',style:'background:#F7F7F7;',items:portalPanel}); \n");
				}
				
				htmlStr.append("new Ext.Viewport({layout:'fit',items:{xtype:'container',style:'background:#F7F7F7;',overflowY:'auto',overflowX:'hidden',items:portalItems}}); \n");
				htmlStr.append(" window.viiewportal = Ext.getCmp('viiewportal'); \n");
				js = js.concat(htmlStr.toString());
			}
			buf.append(js);
			// System.out.println("javascript="+buf.toString());
			pageContext.getOut().println(buf.toString());
			return EVAL_BODY_BUFFERED;
		} catch (Exception ex) {
			ex.printStackTrace();
			return SKIP_BODY;
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}


}
