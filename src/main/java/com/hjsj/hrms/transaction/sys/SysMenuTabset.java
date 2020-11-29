package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 通过menu生成的tabset
 * <p>Title: SysMenuTabset </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time  2014-8-16 上午11:03:39</p>
 * @author xuj
 * @version 1.0
 */
public class SysMenuTabset extends IBusiness {
	
	public void execute() throws GeneralException {
		List menuList = new ArrayList();
		try{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String menu_id = (String)hm.get("menu_id");
			Document doc=getDocument();
			
	        Element rootnode=doc.getRootElement();
	        if(menu_id!=null && !"".equals(menu_id)){
	        	XPath xPath = XPath.newInstance("/hrp_menu/menu[@id='"+menu_id+"']");
	        	rootnode = (Element) xPath.selectSingleNode(doc);
	        	//考虑到工具条等类似menu按钮，可能会menu下的menu，根据实际情况暂最多支持menu下三级
	        	if(rootnode==null){
	        		xPath = XPath.newInstance("/hrp_menu/menu/menu[@id='"+menu_id+"']");
		        	rootnode = (Element) xPath.selectSingleNode(doc);
		        	if(rootnode==null){
		        		xPath = XPath.newInstance("/hrp_menu/menu/menu/menu[@id='"+menu_id+"']");
			        	rootnode = (Element) xPath.selectSingleNode(doc);
		        	}
	        	}
    		}
	        if(rootnode!=null){
	        	menuList=rootnode.getChildren();
	            for(int i=0;i<menuList.size();i++){
	                       Element element = (Element)menuList.get(i);
	                       String url = element.getAttributeValue("url");
	                       if(StringUtils.isNotBlank(url)) {
	                    	   url = url.replaceAll("`","&");
	                    	   url = url.substring(0,url.indexOf(".do?")+4)+"encryptParam="+PubFunc.encrypt(url.substring(url.indexOf(".do?")+4));
	                    	   element.setAttribute("url", url);
	                       }
	            }
	        }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.getFormHM().put("menulist", menuList);
		}
		
	}
	
	private String getPath() {
		String classPath = "";
		try
		{
			classPath = this.getClass().getResource("").toString();
			classPath=java.net.URLDecoder.decode(classPath,"utf-8"); 	
			//classPath = this.getClass().getResource("").toURI().getPath();
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
			/* cmq added at 20121010 for jboss eap6,但取不到实际的绝对路径
			else if(classPath.startsWith("vfs:"))//jboss Eap6
			{
				//vfs:/D:/EAP-6.0.0.GA/jboss-eap-6.0/bin/content/hrms.war/WEB-INF/lib/hrpweb3.jar/com/hjsj/hrms/taglib/general/			
				beginIndex = classPath.indexOf("vfs:") + 5;
				endIndex = classPath.lastIndexOf("hrpweb3.jar") + 11;
				classPath = classPath.substring(beginIndex, endIndex);
			}
			*/
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
			
			if(classPath.indexOf("transaction")!=-1){
				int endIndex = classPath.lastIndexOf("transaction")-1;
				String mixpath = "/constant/menu.xml/";
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

			/**cmq added for jboss eap6*/
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
		    //end.
			if(in==null)
			{
				in = new FileInputStream(file);
			}
			if(in==null)
				throw new GeneralException("NOT FOUND menu.xml FILE");
			doc = PubFunc.generateDom(in);
		}  catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(in);
			PubFunc.closeResource(jf);
		} 

		return doc;

	}
}
