package com.hjsj.hrms.utils.components.funcmenu;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class SearchMenuTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		Document doc = this.getDocument();
		
		List child = doc.getRootElement().getChildren();
		HashMap map = new HashMap();
		addChild(child,map);
		
		List list = (ArrayList)map.get("children");
		this.formHM.put("children", list);

	}
	
	private void addChild(List eles,HashMap parent){
		
		ArrayList list = new ArrayList();
		for(int i=0;i<eles.size();i++){
			Element e = (Element)eles.get(i);
			String funcid = e.getAttributeValue("func_id").toString();
			if(!userView.hasTheFunction(funcid))
				continue;
			String menuhide = e.getAttributeValue("menuhide");
    			if("false".equalsIgnoreCase(menuhide))
    				continue;
    			
    			String url=e.getAttributeValue("url");
    			if(!PubFunc.hasPriMenu(funcid,url,userView)) //根据锁版本号控制人事异动or薪资的新旧程序
	        		continue;
    			String id=e.getAttributeValue("id");
	        	/**左边主菜单区域排除top快捷菜单项项,移动应用平台,网络学院*/
	        	if("9999".equalsIgnoreCase(id)/*||id.equalsIgnoreCase("21")*/|| "50".equalsIgnoreCase(id)|| "55".equalsIgnoreCase(id))
	        		continue;
			HashMap map = new HashMap();
			
			map.put("menuid",e.getAttributeValue("id"));
			map.put("funcid",funcid);
			map.put("name", e.getAttributeValue("name"));
			String desc = e.getAttributeValue("desc");
			desc = desc==null?"":desc;
			map.put("desc",desc);
			String qicon = e.getAttributeValue("qicon");
			qicon = qicon==null?"/images/menuicon.png":qicon;
			map.put("qicon",qicon);
			map.put("url",e.getAttributeValue("url"));
			map.put("target",e.getAttributeValue("target"));
			
			List child = e.getChildren();
			if(child.size()>0)
				addChild(child,map);
			else
				map.put("leaf", true);
			list.add(map);
		}
        if(list.size()>0)
        	   parent.put("children", list);
		
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
		    		file = file.replace("hjsj-components.jar", "hrpweb3.jar");
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
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeIoResource(in);
			PubFunc.closeIoResource(jf);
		}

		return doc;

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

		classPath = classPath.replace("hjsj-components.jar", "hrpweb3.jar");
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

			if(classPath.indexOf("utils")!=-1){
				int endIndex = classPath.lastIndexOf("utils")-1;
				String mixpath = "/constant/menu.xml/";
				classPath = classPath.substring(beginIndex, endIndex) + mixpath;
			}
		}

		return classPath;
	}

}
