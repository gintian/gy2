/**
 * 
 */
package com.hjsj.hrms.taglib.general;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.VersionControl;
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
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author cmq
 * May 30, 20109:21:55 PM
 */
public class ExtMenuTag extends BodyTagSupport {
	/**
	 * menu.xml定义业务模板ID
	 */
	private String moduleid;	
	/**
	 * 菜单容器
	 */
	private String container="toolbar";
	/**移动应用*/
	private boolean mobile_app=false;
	/**
	 * 默认为ext菜单条
	 * jquery menu
	 */
	private String menutype="ext";
	

	
	public String getMenutype() {
		return menutype;
	}

	public void setMenutype(String menutype) {
		this.menutype = menutype;
	}

	/**
	 * 
	 */
	public ExtMenuTag() {
        super();
	}
	
   private boolean haveFuncPriv(String function_id,String module_id)
   {
      boolean bfunc=true,bmodule=true;
      /**
       * 在这里进行权限分析
       */
       /**版本功能控制*/
      VersionControl ver_ctrl=new VersionControl();	
      UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);	      
      EncryptLockClient lock=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
      ver_ctrl.setVer(lock.getVersion());

      if(!(module_id==null|| "".equals(module_id)))
      {
    	String[] modules =StringUtils.split(module_id,",");
        for(int i=0;i<modules.length;i++)
        {
        	module_id=modules[i];
        	bmodule=lock.isBmodule(Integer.parseInt(module_id),userview.getUserName());
        	if(bmodule)
        		break;
        }
      }	      
      
      if(!(function_id==null|| "".equals(function_id)))
      {	      
    	  String[] funcs =StringUtils.split(function_id,","); 
    	  for(int i=0;i<funcs.length;i++)
    	  {
    		  bfunc=ver_ctrl.searchFunctionId(funcs[i],userview.hasTheFunction(funcs[i]));
    		  if(bfunc)
    			  break;
    	  }   
     }
	 return (bfunc&bmodule);
   }		
	/**
	 * 输出子节点的内容
	 * 	<menu id="0101" url="" param="" icon="" func_id="" target="">ssss</menu>
	 * 
	 * 
	 * @param parent
	 * @return
	 */
	private String outSubMenuJs(Element parent)
	{
		//添加 通过锁版本判断调用Ext版本 guodd 2015-06-29
	    EncryptLockClient lock=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
		StringBuffer buf=new StringBuffer();
		List list=parent.getChildren();
		if(list.size()>0)
		{
			buf.append("items:[");
			boolean badd=false;
			for(int i=0;i<list.size();i++)
			{
	        	Element element=(Element)list.get(i);
	        	String func_id=element.getAttributeValue("func_id");
	        	String mod_id=element.getAttributeValue("mod_id");
	        	String name=element.getAttributeValue("name");	
	        	String url=element.getAttributeValue("url");	
	        	String target=element.getAttributeValue("target");	
	        	String menuhide = element.getAttributeValue("menuhide");
	        	if("false".equalsIgnoreCase(menuhide))
	        		continue;
	  			if(haveFuncPriv(func_id,mod_id))
	  			{
	        		if(badd)
	        			buf.append(",");	  				
	        		buf.append("{");
	        		buf.append("text:'<font size=2>");
	        		buf.append(name);
	        		buf.append("</font>'");
	        		/**工具条上的菜单挂接有事件*/
	        		if(url!=null&&url.length()>0)
	        		{
	        			buf.append(","); 	        			
		        		buf.append("href:'");
		        		buf.append(url);
		        		buf.append("'");
		        		
	        			buf.append(","); 	        			
		        		buf.append("hrefTarget:'");
		        		buf.append(target);
		        		buf.append("'");
	        		}
	        		String tmp=element.getAttributeValue("icon");
	        		if(!(tmp==null||tmp.length()==0))
	        		{
	        			buf.append(",iconCls:'");
	        			buf.append(tmp);
	        			buf.append("'");
	        		}		        		
	        		String menu=outSubMenuJs(element);
	        		if(menu.length()>0)
	        		{
	        			buf.append(",menu:{"); 
	        			buf.append(menu);
	        			//70以下版本调用旧版ext，不支持下面的代码
	        			if(lock.getVersion()>=70){
	        				/* 设置鼠标移开菜单自动隐藏 xiaoyun 2014-8-5 start */
	        				buf.append(",listeners : {").append("\n");
			        		buf.append("mouseover : function (obj,e){").append("\n");
			    			buf.append("obj.show();").append("\n");	
			    			buf.append("},").append("\n");
			    			buf.append("mouseleave : function (obj, e){").append("\n");
			    			buf.append("obj.hide();").append("\n");	
			    			//buf.append("obj.findParentByType('menu').hide();").append("\n");
			    			buf.append("var menu = obj.findParentByType('menu');").append("\n");
			    			buf.append("var xPos;").append("\n");
			    			buf.append("var yPos;").append("\n");
			    			buf.append("var x = menu.getX();").append("\n");
			    			buf.append("var y = menu.getY();").append("\n");
			    			buf.append("var tox = menu.getWidth()+x;").append("\n");
			    			buf.append("var toy = menu.getHeight()+y;").append("\n");
			    			buf.append("if(e.getPageX()){").append("\n");
			    			buf.append("xPos=e.getPageX();").append("\n");
			    			buf.append("yPos=e.getPageY();").append("\n");
			    			
			    			buf.append("} else {").append("\n");
			    			buf.append("xPos=e.clientX+document.body.scrollLeft-document.body.clientLeft;").append("\n");
			    			buf.append("yPos=e.clientY+document.body.scrollTop-document.body.clientTop;").append("\n");
			    			buf.append("}").append("\n");
			    			buf.append("if(x > xPos || tox < xPos || y > yPos || toy < yPos){").append("\n");
			    			buf.append("menu.hide();").append("\n");
			    			buf.append("}").append("\n");
			    			
			    			buf.append("}").append("\n");
			    			buf.append("}").append("\n");
		        			/* 设置鼠标移开菜单自动隐藏 xiaoyun 2014-8-5 end */
	        			}
	        			buf.append("}");
	        		}	
	        		buf.append("}");	
	        		badd=true;	        		
	  			}	        	
			}//for loop end .
			buf.append("]");
			//70以下版本调用旧版ext，不支持下面的代码
			if(lock.getVersion()>=70){
				/* 设置鼠标移开菜单自动隐藏 xiaoyun 2014-8-5 start */
				buf.append(",listeners : {").append("\n");
	    		buf.append("mouseover : function (obj,e){").append("\n");
				buf.append("obj.show();").append("\n");	
				buf.append("},").append("\n");
				buf.append("mouseleave : function (obj, e){").append("\n");
				buf.append("obj.hide();").append("\n");
				//buf.append("obj.findParentByType('menu').hide();").append("\n");
				buf.append("}").append("\n");
				buf.append("}").append("\n");
				/* 设置鼠标移开菜单自动隐藏 xiaoyun 2014-8-5 end */
			}
		}		
		return buf.toString();
	}
	
	private String getPath() {
		String classPath = "";
		try
		{
			classPath = this.getClass().getResource("").toString();
			classPath=java.net.URLDecoder.decode(classPath,"utf-8"); 				
			//classPath = this.getClass().getResource("").toURI().getPath();//  toString();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		if (classPath.indexOf("hrpweb3.jar") != -1) 
		{
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
		} 
		else 
		{
			Properties props=System.getProperties(); //系统属性
			String sysname = props.getProperty("os.name");
			
			//zhaoxj 20150514 开发环境下，windows平台需要去掉路径开头的“/”，其它如OSX，linux等不能去掉
			int beginIndex = classPath.indexOf("/");
			if(sysname.startsWith("Win")){
				beginIndex++;
			}
		
			if(classPath.indexOf("taglib")!=-1){
				int endIndex = classPath.lastIndexOf("taglib")-1;
				String mixpath = "/constant/menu.xml";
				classPath = classPath.substring(beginIndex, endIndex) + mixpath;
			}
		}
		return classPath;
	}


	private  Document getDocument()throws GeneralException {
		String file = this.getPath();
		Document doc = null;
		String EntryName = "com/hjsj/hrms/constant/menu.xml";
		InputStream in = null;
		JarFile jf = null;
		try 
		{
			/**cmq added for jboss eap6 at 20121019,增加对jboss服务的单独处理*/
		    String webserver=SystemConfig.getProperty("webserver");
		    if("jboss".equalsIgnoreCase(webserver)|| "inforsuite".equalsIgnoreCase(webserver))
		    {
		    	in=this.getClass().getResourceAsStream("/com/hjsj/hrms/constant/menu.xml");
		    }
		    else
		    {			
				if(file.indexOf("hrpweb3.jar")!=-1)
				{
					jf = new JarFile(file);
					Enumeration es = jf.entries();
					while (es.hasMoreElements()) 
					{
						JarEntry je = (JarEntry) es.nextElement();
						if (je.getName().equals(EntryName)) 
						{
							in = jf.getInputStream(je);
							break;
						}
	
					}
				}
		    }
		    
			if(in==null)
			{
				in = new FileInputStream("/"+file);
			}
			if(in==null)
				throw new GeneralException("NOT FOUND menu.xml FILE");
			doc = PubFunc.generateDom(in);
		}  catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeIoResource(in);
			PubFunc.closeIoResource(jf);
		}

		return doc;

	}
	/**
	 * 移动应用框架平台
	 * @return
	 */
	private String outMobileMenuJs()
	{
		StringBuffer buf=new StringBuffer();
		try
		{
			Document doc=getDocument();
	        String xpath="//menu[@id="+this.moduleid+"]";
			XPath xPath = XPath.newInstance(xpath);
			Element rootnode=(Element) xPath.selectSingleNode(doc);
	        List list=rootnode.getChildren();

    		buf.append("var tb = new Ext.Toolbar();");
    		buf.append("tb.render('");
    		buf.append(this.container);
    		buf.append("');");
    		buf.append("tb.add(");
    		boolean badd=false;
	        for(int i=0;i<list.size();i++)
	        {
	        	Element element=(Element)list.get(i);
	        	String func_id=element.getAttributeValue("func_id");
	        	String mod_id=element.getAttributeValue("mod_id");
	        	String name=element.getAttributeValue("name");	
	        	String url=element.getAttributeValue("url");
	        	String target=element.getAttributeValue("target");	        	
	        	if(haveFuncPriv(func_id,mod_id))
	        	{
	        		if(badd)
	        			buf.append(",");	        		
	        		buf.append("{");
	        		buf.append("text:'");
	        		buf.append(name);
	        		buf.append("'");
	        		/**工具条上的菜单挂接有事件*/
	        		if(url!=null&&url.length()>0)
	        		{
	        			buf.append(","); 	        			
		        		buf.append("href:'");
		        		buf.append(url);
		        		buf.append("'");
		        		
	        			buf.append(","); 	        			
		        		buf.append("hrefTarget:'");
		        		buf.append(target);
		        		buf.append("'");
		        		
	        			buf.append(","); 	        		
	        			buf.append("handler:clickHandler");
	        		}
	        		String tmp=element.getAttributeValue("icon");
	        		if(!(tmp==null||tmp.length()==0))
	        		{
	        			buf.append(",iconCls:'");
	        			buf.append(tmp);
	        			buf.append("'");
	        		}	
	        		String menu=outSubMenuJs(element);
	        		if(menu.length()>0)
	        		{
	        			buf.append(",menu:{zIndex:100,"); 
	        			buf.append(menu);
	        			buf.append("}");
	        		}
	        		buf.append("}");
	        		badd=true;
	        	}
	        }//for i loop end.
	        
    		buf.append(");");
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return buf.toString();
	}
	/**
	 * JQuery Mobile
	 * @return
	 */
	private String outJMobileMenuJs()
	{
		StringBuffer buf=new StringBuffer();
		try
		{
			Document doc=getDocument();
			UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);	 		
	        String xpath="//menu[@id="+this.moduleid+"]";
			XPath xPath = XPath.newInstance(xpath);
			Element rootnode=(Element) xPath.selectSingleNode(doc);
			if(rootnode==null)
				return "";
			String menuhide = rootnode.getAttributeValue("menuhide");
        	if("false".equalsIgnoreCase(menuhide))
        		return "";
	        List list=rootnode.getChildren();
    		boolean badd=false;
    		boolean bself=true;
    		int status=userview.getStatus(); 
    	    if(status!=4)
    	    {
    	        String a0100=userview.getA0100();
    	    	if(a0100==null||a0100.length()==0)
    	    	{
    	    		bself=false;
    	    	}
    	    }
	        for(int i=0;i<list.size();i++)
	        {
	        	Element element=(Element)list.get(i);
	        	String func_id=element.getAttributeValue("func_id");
	        	String mod_id=element.getAttributeValue("mod_id");
	        	String name=element.getAttributeValue("name");
	        	String url=element.getAttributeValue("url");	        	
	        	String id=element.getAttributeValue("id");
	        	menuhide = element.getAttributeValue("menuhide");
	        	if("false".equalsIgnoreCase(menuhide))
	        		continue;
	        	if(("5007".equalsIgnoreCase(id)|| "5008".equalsIgnoreCase(id))&&!bself){
	        		continue;
	        	}
	        	if("5005".equalsIgnoreCase(id))//5005，我的薪酬
	        	{
	        		
	        		url=url+"&amp;userbase="+userview.getDbname();
	        		
	        	}
	    		List childlist=element.getChildren();
	    		int nc=childlist.size();
	    		/**如果有子菜单*/
	    		if(nc!=0)
	    		{
	    			url="/phone-app/s_mainpanel.do?br_go=link&moduleid="+id+"&name="+name;
	    		}
	        	if(url==null||url.length()==0)
	        		url="#";
	        	String target=element.getAttributeValue("target");	
	        	/**一级菜单用按钮*/
                //<div class="ui-block-c"><a href="index.html" data-role="button"  class="ui-icon-myicon" data-iconpos="top" >通讯录</a></div>
	        	/**二级菜单用listview*/
	        	/*
		    <ul data-role="listview" data-inset="true">
                <li><a href="index.html">Inbox</a> <span class="ui-li-count">12</span></li>
                <li><a href="index.html">Outbox</a> <span class="ui-li-count">0</span></li>
                <li><a href="index.html">Drafts</a> <span class="ui-li-count">4</span></li>
                <li><a href="index.html">Sent</a> <span class="ui-li-count">328</span></li>
                <li><a href="index.html">Trash</a> <span class="ui-li-count">62</span></li>
            </ul>	        	 
	        	 */
	        	badd=false;
	        	if(haveFuncPriv(func_id,mod_id))
	        	{
	        	  /**一级菜单*/	
	        	  if("50".equalsIgnoreCase(this.moduleid))
	        	  {
	  	        	/**设置为空仅为了按钮钮设置高点*/
	  	        	name="&nbsp;";	        		  
	        		buf.append("<div class=\"ui-block-c\" align=\"center\">");
	        		if(url!=null&&url.length()>0)
	        		{
	        			buf.append("<a href=\"");
	        			buf.append(url);
	        			buf.append("\"");
	        			buf.append(" data-role=\"button\" data-rel=\"dialog\" rel=\"external\" data-iconpos=\"top\"");
	                    if(target!=null&&target.length()>0)
	        			  buf.append("target=\""+target+"\"");
		        		String tmp=element.getAttributeValue("icon");
		        		if(!(tmp==null||tmp.length()==0))
		        		{
		        			buf.append(" class=\"");
		        			buf.append(tmp);
		        			buf.append("\"");
		        		}	 	        			
	        			buf.append(">");
	        			
	        			badd=true;
	        		}
	        		buf.append(name);
	        		if(badd)
	        			buf.append("</a>");
	        		name=element.getAttributeValue("name");
	        		name=name==null?"":name;
	        		buf.append("<span style=\"font-size: 12px\">"+name+"</span>");
	        		buf.append("</div>");
	        	  }
	        	  else
	        	  {
	        		    //<li><a href="index.html">Inbox</a> <span class="ui-li-count">12</span></li>
		        		buf.append("<li>");
		        		if(url!=null&&url.length()>0)
		        		{
		        			buf.append("<a href=\"");
		        			buf.append(url);
		        			buf.append("\"");
		        			buf.append(" data-rel=\"dialog\" rel=\"external\" data-iconpos=\"top\" target=\"");
		        			buf.append(target);
		        			buf.append("\"");
			        		String tmp=element.getAttributeValue("icon");
			        		if(!(tmp==null||tmp.length()==0))
			        		{
			        			buf.append(" class=\"");
			        			buf.append(tmp);
			        			buf.append("\"");
			        		}	 	        			
		        			buf.append(">");
		        			
		        			badd=true;
		        		}
		        		buf.append(name);
		        		if(badd)
		        			buf.append("</a>");
		        		buf.append("</li>");	        		  
	        	  }
	        	  badd=true;
	        	}
	        }//for i loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return buf.toString();
	}	
	/**
	 * 根据业务模块号输入JQUERY MNEU主菜单代码
	 * 第一级菜单为tabs
	 * 输入内格式串如下：
	 *  [
	 *   {
	 *      "id":"20101",
	 *      "text":"测试A",
	 *      "href":"http://www.hjsoft.com.cn",
	 *      "iconCls":"icon-ok",
	 *      "target":"il_body",
	 *      "children":
	 *      [
	 *        {
	 *        	"id":"20101",
	 *          "text":"测试A",
	 *          "href":"http://www.hjsoft.com.cn",
	 *          "iconCls":"icon-ok",
	 *          "target":"il_body",
	 *          "children":[{},{}]
	 *        },
	 *        {...}
	 *      ]
	 *   },
	 *   {
	 *     ....
	 *   }
	 *  ]
	 * @return
	 */
	private String outJquerySubMenuJs(Element parent)
	{
		StringBuffer buf=new StringBuffer();
		List list=parent.getChildren();
		if(list.size()>0)
		{
			buf.append("[");
			boolean badd=false;
			for(int i=0;i<list.size();i++)
			{
	        	Element element=(Element)list.get(i);
	        	String func_id=element.getAttributeValue("func_id");
	        	String mod_id=element.getAttributeValue("mod_id");
	        	String name=element.getAttributeValue("name");	
	        	String url=element.getAttributeValue("url");	
	        	String target=element.getAttributeValue("target");	
	        	String id=element.getAttributeValue("id");		        	
	  			if(haveFuncPriv(func_id,mod_id))
	  			{
	        		if(badd)
	        			buf.append(",");	  				
	        		buf.append("{");
	        		buf.append("text:'");
	        		buf.append(name);
	        		buf.append("',");
	        		buf.append("id:'");
	        		buf.append(id);
	        		buf.append("'");	        		
	        		/**工具条上的菜单挂接有事件*/
	        		if(url!=null&&url.length()>0)
	        		{
	        			buf.append(","); 	        			
		        		buf.append("href:'");
		        		buf.append(url);
		        		buf.append("'");
		        		
	        			buf.append(","); 	        			
		        		buf.append("target:'");
		        		buf.append(target);
		        		buf.append("'");
	        		}
	        		String tmp=element.getAttributeValue("icon");
	        		if(!(tmp==null||tmp.length()==0))
	        		{
	        			buf.append(",iconCls:'");
	        			buf.append(tmp);
	        			buf.append("'");
	        		}		        		
	        		String menu=outJquerySubMenuJs(element);
	        		if(menu.length()>0)
	        		{
	        			buf.append(",children:"); 
	        			buf.append(menu);
	        		}	
	        		buf.append("}");	
	        		badd=true;	        		
	  			}	        	
			}//for loop end .
			buf.append("]");
		}		
		return buf.toString();
	}
	
	private String outJquerMenuJs()
	{
		StringBuffer buf=new StringBuffer();
		try
		{
			Document doc=getDocument();
	        String xpath="//menu[@id="+this.moduleid+"]";
			XPath xPath = XPath.newInstance(xpath);
			Element rootnode=(Element) xPath.selectSingleNode(doc);
			if(rootnode==null)
				return "";
			String menuhide = rootnode.getAttributeValue("menuhide");
        	if("false".equalsIgnoreCase(menuhide))
        		return "";
	        List list=rootnode.getChildren();
	        
    		buf.append("var ");
    		buf.append(this.container);
    		buf.append("=[");
    		boolean badd=false;
	        for(int i=0;i<list.size();i++)
	        {
	        	Element element=(Element)list.get(i);
	        	String func_id=element.getAttributeValue("func_id");
	        	String mod_id=element.getAttributeValue("mod_id");
	        	String name=element.getAttributeValue("name");	
	        	String url=element.getAttributeValue("url");
	        	String target=element.getAttributeValue("target");	
	        	String id=element.getAttributeValue("id");
	        	menuhide = element.getAttributeValue("menuhide");
	        	if("false".equalsIgnoreCase(menuhide))
	        		continue;
	        	if(haveFuncPriv(func_id,mod_id))
	        	{
	        		if(badd)
	        			buf.append(",");	        		
	        		buf.append("{");
	        		buf.append("text:'");
	        		buf.append(name);
	        		buf.append("',");
	        		buf.append("id:'");
	        		buf.append(id);
	        		buf.append("'");	        		
	        		/**工具条上的菜单挂接有事件*/
	        		if(url!=null&&url.length()>0)
	        		{
	        			buf.append(","); 	        			
		        		buf.append("href:'");
		        		buf.append(url);
		        		buf.append("'");
		        		
	        			buf.append(","); 	        			
		        		buf.append("target:'");
		        		buf.append(target);
		        		buf.append("'");

	        		}
	        		String tmp=element.getAttributeValue("icon");
	        		if(!(tmp==null||tmp.length()==0))
	        		{
	        			buf.append(",iconCls:'");
	        			buf.append(tmp);
	        			buf.append("'");
	        		}	
	        		String menu=outJquerySubMenuJs(element);
	        		if(menu.length()>0)
	        		{
	        			buf.append(",children:"); 
	        			buf.append(menu);
	        		}
	        		buf.append("}");
	        		badd=true;
	        	}
	        }//for i loop end.
	        
    		buf.append("];");
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return buf.toString();		
	}
	/**
	 * 根据业务模板号输入主菜单代码
	 * 第一级菜单为工具条功能菜单
	 * @return
	 */
	private String outMenuJs()
	{
		StringBuffer buf=new StringBuffer();
		try
		{
			Document doc=getDocument();
	        String xpath="//menu[@id="+this.moduleid+"]";
			XPath xPath = XPath.newInstance(xpath);
			Element rootnode=(Element) xPath.selectSingleNode(doc);
			/**菜单节点找不到*/
			if(rootnode==null)
				return "";
			String menuhide = rootnode.getAttributeValue("menuhide");
        	if("false".equalsIgnoreCase(menuhide)&&!"21".endsWith(this.moduleid))
        		return "";
	        List list=rootnode.getChildren();

    		buf.append("var tb = new Ext.Toolbar();");
    		buf.append("tb.render('");
    		buf.append(this.container);
    		buf.append("');");
    		buf.append("tb.add(");
    		boolean badd=false;
	        for(int i=0;i<list.size();i++)
	        {
	        	Element element=(Element)list.get(i);
	        	String func_id=element.getAttributeValue("func_id");
	        	String mod_id=element.getAttributeValue("mod_id");
	        	String name=element.getAttributeValue("name");	
	        	String url=element.getAttributeValue("url");	
	        	String target=element.getAttributeValue("target");
	        	menuhide = element.getAttributeValue("menuhide");
	        	if("false".equalsIgnoreCase(menuhide))
	        		continue;
	        	if(haveFuncPriv(func_id,mod_id))
	        	{
	        		if(badd)
	        			buf.append(",");	        		
	        		buf.append("{");
	        		buf.append("text:'<font size=2>");
	        		buf.append(name);
	        		buf.append("</font>'");
	        		/**工具条上的菜单挂接有事件*/
	        		if(url!=null&&url.length()>0)
	        		{
	        			buf.append(","); 	        			
		        		buf.append("href:'");
		        		buf.append(url);
		        		buf.append("'");
		        		
	        			buf.append(","); 	        			
		        		buf.append("hrefTarget:'");
		        		buf.append(target);
		        		buf.append("'");
		        		
	        			buf.append(","); 	        		
	        			buf.append("handler:clickHandler");
	        		}
	        		String tmp=element.getAttributeValue("icon");
	        		if(!(tmp==null||tmp.length()==0))
	        		{
	        			buf.append(",iconCls:'");
	        			buf.append(tmp);
	        			buf.append("'");
	        		}	
	        		String menu=outSubMenuJs(element);
	        		if(menu.length()>0)
	        		{
	        			buf.append(",menu:{zIndex:100,"); 
	        			buf.append(menu);
	        			buf.append("}");
	        		}
	        		buf.append("}");
	        		badd=true;
	        	}
	        }//for i loop end.
	        
    		buf.append(");");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return buf.toString();
	}

	
	public int doStartTag() throws JspException {
        try
        {   
        	StringBuffer buf=new StringBuffer();
        	String js="";
        	if(this.mobile_app)
        	{
        		js=outJMobileMenuJs();
        	}
        	else
        	{
        		//jqeury menu type( for 网络学院)
        		if("jquery".equalsIgnoreCase(this.menutype))
        		{
        			js=outJquerMenuJs();
        		}
        		else
        			js=outMenuJs();
        	}
        	//System.out.println("javascript="+js);
			buf.append(js);
        	
	        pageContext.getOut().println(buf.toString());

	        return EVAL_BODY_BUFFERED;   
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	return SKIP_BODY;
        }
	}

	public String getModuleid() {
		return moduleid;
	}
	public void setModuleid(String moduleid) {
		this.moduleid = moduleid;
	}

	public String getContainer() {
		return container;
	}

	public void setContainer(String container) {
		this.container = container;
	}

	public boolean isMobile_app() {
		return mobile_app;
	}

	public void setMobile_app(boolean mobile_app) {
		this.mobile_app = mobile_app;
	}

}
