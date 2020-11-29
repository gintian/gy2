package com.hjsj.hrms.transaction.general.sys.options;

import com.hjsj.hrms.businessobject.sys.options.PortalTailorXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.LabelValueView;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class SearchPortalTailorTrans extends IBusiness {

	private ArrayList showitem=new ArrayList();
	private HashMap map = new HashMap();
	private String[] idorder = null;
	private TreeMap treeMap=new TreeMap();
	private int maxorder=0;
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String portalid = (String)this.getFormHM().get("portalid");
		this.getFormHM().put("portalid", "");
		if(portalid!=null&&!"".equals(portalid)){//新版本（自5）从每户定制来读
			ArrayList beans=new PortalTailorXml().NReadOutParameterXml(this.getFrameconn(),this.userView.getUserName(),portalid);
			idorder=new String[beans.size()];
			maxorder = beans.size();
			if(this.maxorder==0){
				outPortalJs(portalid);
			}else{
				int index=0;
				for(Iterator i=beans.iterator();i.hasNext();){
					LazyDynaBean bean = (LazyDynaBean)i.next();
					String id = (String)bean.get("id");
					if("0234".equals(id)&&userView.getVersion()>=70) {//70锁隐藏我的任务 
						continue;
					}
					if("0236".equals(id)&&userView.getVersion()<70) {//70以下锁不显示职称评审  wangb 2019p-07-17 bug 50376
						continue;
					}
					idorder[index]=id;
					map.put(id, bean);
					index++;
				}
				outPortalJs(portalid);
			}
			for(Iterator i=treeMap.values().iterator();i.hasNext();){
				this.showitem.add((LazyDynaBean)i.next());
			}
			String showTable="1";
			String showserviceHall = "1";
			String showTemplateTable="1";
			LazyDynaBean bean1 = (LazyDynaBean)map.get("worktable");
			if(null!=bean1){
				showTable= (String)bean1.get("show");
				showTable = showTable==null?"1":showTable;
			}
			LazyDynaBean bean2 = (LazyDynaBean)map.get("servicehall");
			if(null!=bean2){
				showserviceHall= (String)bean2.get("show");
				showserviceHall = showserviceHall==null?"1":showserviceHall;
			}
			LazyDynaBean bean3 = (LazyDynaBean)map.get("templatetable");
			if(null!=bean3){
				showTemplateTable= (String)bean3.get("show");
				showTemplateTable = showTemplateTable==null?"1":showTemplateTable;
			}
			
			if(PubFunc.isUseNewPrograme(this.userView)){//xiegh add 20170804
				//{id=0236, scroll=0, name=职称评审, show=1, twinkle=0}
				LazyDynaBean worktableBean = new LazyDynaBean();
				worktableBean.set("id","worktable");
				worktableBean.set("scroll","0");
				worktableBean.set("name","工作桌面");
				worktableBean.set("show",showTable);
				worktableBean.set("twinkle","0");
				showitem.add(0, worktableBean);
				
				LazyDynaBean serviceHallBean = new LazyDynaBean();
				serviceHallBean.set("id","servicehall");
				serviceHallBean.set("scroll","0");
				serviceHallBean.set("name","服务大厅");
				serviceHallBean.set("show",showserviceHall);
				serviceHallBean.set("twinkle","0");
				showitem.add(1, serviceHallBean);
				
				LazyDynaBean templateTable = new LazyDynaBean();
				templateTable.set("id","templateTable");
				templateTable.set("scroll","0");
				templateTable.set("name","我的申请与待办任务");
				templateTable.set("show",showTemplateTable);
				templateTable.set("twinkle","0");
				showitem.add(2, templateTable);
			}
			
			//.......
			
		}else{
			ArrayList nodeslist=new PortalTailorXml().ReadOutParameterXml("SYS_PARAM",this.getFrameconn(),this.userView.getUserName());
			/*
			 * 1公告栏 bulletin
			 * 2预警提示 warn
			 * 3常用花名册 muster
			 * 4常用查询  query
			 * 5常用统计分析 stat
			 * 6常用登记表 card
			 * 7常用报表 report
			 * 8代办事宜  matter
			 * 9薪资审批  salary
			 * */
			
			for(int i=0;nodeslist!=null && i<nodeslist.size();i++)
			{
				ArrayList attributelist=(ArrayList)nodeslist.get(i);
				for(int j=0;j<attributelist.size();j++)
				{
					LabelValueView item=(LabelValueView)attributelist.get(j);
	
					if("id".equals(item.getLabel()) && "1".equals(item.getValue()))
					{
					   putvaluetojsp("bulletin",attributelist);
					   LazyDynaBean bean=new LazyDynaBean();
					   bean.set("itemid","1");
					   showitem.add(bean);
					}
					if("id".equals(item.getLabel()) && "2".equals(item.getValue()))
					{
					   putvaluetojsp("warn",attributelist);
					   LazyDynaBean bean=new LazyDynaBean();
					   bean.set("itemid","2");
					   showitem.add(bean);
					}	    			
					if("id".equals(item.getLabel()) && "3".equals(item.getValue()))
					{
					   putvaluetojsp("muster",attributelist);
					   LazyDynaBean bean=new LazyDynaBean();
					   bean.set("itemid","3");
					   showitem.add(bean);
					}
					if("id".equals(item.getLabel()) && "4".equals(item.getValue()))
					{
					   putvaluetojsp("query",attributelist);
					   LazyDynaBean bean=new LazyDynaBean();
					   bean.set("itemid","4");
					   showitem.add(bean);
					}
					if("id".equals(item.getLabel()) && "5".equals(item.getValue()))
					{
					   putvaluetojsp("stat",attributelist);
					   LazyDynaBean bean=new LazyDynaBean();
					   bean.set("itemid","5");
					   showitem.add(bean);
					}
					if("id".equals(item.getLabel()) && "6".equals(item.getValue()))
					{
					   putvaluetojsp("card",attributelist);
					   LazyDynaBean bean=new LazyDynaBean();
					   bean.set("itemid","6");
					   showitem.add(bean);
					}
					if("id".equals(item.getLabel()) && "7".equals(item.getValue()))
					{
					   putvaluetojsp("report",attributelist);
					   LazyDynaBean bean=new LazyDynaBean();
					   bean.set("itemid","7");
					   showitem.add(bean);
					}
					if("id".equals(item.getLabel())&& "8".equals(item.getValue()))
					{
						if(userView.getVersion()<70) {//70锁隐藏我的任务
							putvaluetojsp("matter",attributelist);
							LazyDynaBean bean=new LazyDynaBean();
							bean.set("itemid","8");
							showitem.add(bean);
						}
					}
					if("id".equals(item.getLabel())&& "9".equals(item.getValue()))
					{
						putvaluetojsp("salary",attributelist);
						LazyDynaBean bean=new LazyDynaBean();
						bean.set("itemid","9");
						showitem.add(bean);
					}
				}
				
			}
		
		}
		
		this.getFormHM().put("showitem",showitem);
		/*StringBuffer itemsstr=new StringBuffer();
		itemsstr.append("<tr>");
		itemsstr.append("<td align=\"left\" class=\"RecordRow\" nowrap>");
		itemsstr.append("<bean:message key=\"system.options.itembulletin\"/>&nbsp;&nbsp;");
		itemsstr.append("</td>");   
	    itemsstr.append("  <td align=\"center\" class=\"RecordRow\" nowrap>");
		itemsstr.append("<html:checkbox name=\"portalTailorForm\" property=\"bulletintwinkle\" value=\"1\"/>&nbsp;&nbsp;");
	    itemsstr.append(" </td>"); 
	    itemsstr.append("  <td align=\"center\" class=\"RecordRow\" nowrap>");
		itemsstr.append("<html:checkbox name=\"portalTailorForm\" property=\"bulletinscroll\" value=\"1\"/>&nbsp;&nbsp;");
	    itemsstr.append(" </td>"); 
	    itemsstr.append("  <td align=\"center\" class=\"RecordRow\" nowrap>");
		itemsstr.append("<html:checkbox name=\"portalTailorForm\" property=\"bulletinshow\" value=\"1\"/>&nbsp;&nbsp;");
	    itemsstr.append("</td>");
	    itemsstr.append("</tr>"); 
	    this.getFormHM().put("showitem",itemsstr.toString());*/
	}
	private void putvaluetojsp(String pre,ArrayList attributelist)
	{
		for(int i=0;i<attributelist.size();i++)
		{
			LabelValueView item=(LabelValueView)attributelist.get(i);
		    if("show".equals(item.getLabel())&& "1".equals(item.getValue()))
		       this.getFormHM().put(pre + "show",item.getValue());
		    else
		    	if(!"id".equals(item.getLabel()))
			          this.getFormHM().put(pre + item.getLabel(),item.getValue());
		}
	}	                                                            
	
	/**
	 * 获得绝对路径
	 */
	public String getPath() {

		String classPath = "";
		try
		{
			classPath = this.getClass().getResource("").toString();
			classPath=java.net.URLDecoder.decode(classPath,"utf-8"); 				
			//classPath = PortalMainBo.class.getResource("").toURI().getPath();// toString();
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
			Properties props=System.getProperties(); //系统属性
			String sysname = props.getProperty("os.name");
			//zhaoxj 20150514 开发环境下，windows平台需要去掉路径开头的“/”，其它如OSX，linux等不能去掉
			int beginIndex = classPath.indexOf("/");
			if(sysname.startsWith("Win")){
				beginIndex++;
			}
			
			if(classPath.indexOf("transaction")!=-1){
				int endIndex = classPath.lastIndexOf("transaction")-1;
				String mixpath = "/constant/portal.xml";
				classPath = classPath.substring(beginIndex, endIndex) + mixpath;
			}
			
		}
//		System.out.println("classPath@:" + classPath);
		return classPath;
	}
	private  Document getDocument()throws GeneralException {
		String file = this.getPath();
		Document doc = null;
		String EntryName = "com/hjsj/hrms/constant/portal.xml";
		InputStream in = null;

		try 
		{
			/**cmq added for jboss eap6 at 20121019,增加对jboss服务的单独处理*/
		    String webserver=SystemConfig.getProperty("webserver");
		    if("jboss".equalsIgnoreCase(webserver)|| "inforsuite".equalsIgnoreCase(webserver))
		    {
		    	in=this.getClass().getResourceAsStream("/com/hjsj/hrms/constant/portal.xml");
		    }
		    else
		    {
				if(file.indexOf("hrpweb3.jar")!=-1)
				{
					try(JarFile jf = new JarFile(file)) {
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
		    }
			
			if(in==null)
			{
				in = new FileInputStream(file);
			}
			if(in==null)
				throw new GeneralException("NOT FOUND portal.xml FILE");
			doc = PubFunc.generateDom(in);
		}  catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
		finally{
			if (in!=null) {
				PubFunc.closeIoResource(in);
			}
		}
		return doc;

	}
	
	private void outPortalJs(String portalid)
	{
		try
		{ 
			Document doc=getDocument();
	        String xpath="//portal[@id="+portalid+"]";//此处应根据portal.xml文件业务平台id修改
			XPath xPath = XPath.newInstance(xpath);
			Element rootnode=(Element) xPath.selectSingleNode(doc);
	        List list=rootnode.getChildren();
	        
	        if(this.maxorder>0){
		        for(int i=0;i<list.size();i++)
		        {
		        	Element colchild=(Element)list.get(i);
		        	outPortalColumnPanel(colchild);
		        } 
	        }else{
        		ArrayList allpanels = new ArrayList();
        		for (int i = 0; i < list.size(); i++) {
					allpanels.add(new ArrayList());
					Element colchild = (Element) list.get(i);
					outPortalColumnPanel(colchild,(ArrayList)allpanels.get(i));
        		}
        		int base = allpanels.size();
        		for(int i=0;i<allpanels.size();i++){
        			ArrayList colList =(ArrayList)allpanels.get(i);
        			for(int n=0;n<colList.size();n++){
        				this.treeMap.put(new Integer(base*n+i), (LazyDynaBean)colList.get(n));
        			}
        		}
        	}

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}		
	}
	
	private void outPortalColumnPanel(Element parent)
	{
		List list=parent.getChildren();
		if(list.size()>0)
		{
			for(int i=0;i<list.size();i++)
			{
	        	Element element=(Element)list.get(i);
	        	String name=element.getAttributeValue("name");	
	        	String id=element.getAttributeValue("id");
	        	String hide=element.getAttributeValue("hide");
	        	String priv=element.getAttributeValue("priv");		        	
	        	/**此面板隐藏*/
	        	if("true".equalsIgnoreCase(hide))
	        		continue;
	        	if("0234".equals(id)&&userView.getVersion()>=70) {//70锁隐藏我的任务
	        		continue;
	        	}
	        	if("0236".equals(id)&&userView.getVersion()<70) {//70以下锁不显示职称评审 wangb 2019p-07-17 bug 50376 
					continue;
				}
	        	/**此面板需要授权时*/
	        	if("true".equalsIgnoreCase(priv))
	        	{
	        		if(userView.isSuper_admin() || userView.isHaveResource(IResourceConstant.PORTAL, id))
	        		{
	        			LazyDynaBean bean = (LazyDynaBean)map.get(id);
	        			if(bean!=null){
							bean.set("name",name);
							for(int o=0;o<this.idorder.length;o++){
								if(id.equals(this.idorder[o])){
									treeMap.put(new Integer(o), bean);
								}
							}
	        			}else{//读数据库中是否有此用户的滚动、闪烁、显示配置，否则默认都没有
	        				bean=new LazyDynaBean();
	        				bean.set("id",id);
							bean.set("name",name);
							bean.set("scroll","0");
	    	      			bean.set("twinkle","0");
	    	      			bean.set("show","1");//默认显示
	    	      			treeMap.put(new Integer(++this.maxorder), bean);
	        			}
						//showitem.add(bean);
	        		}
	        	}else{
	        		LazyDynaBean bean = (LazyDynaBean)map.get(id);
        			if(bean!=null){
						bean.set("name",name);
						for(int o=0;o<this.idorder.length;o++){
							if(id.equals(this.idorder[o])){
								treeMap.put(new Integer(o), bean);
							}
						}
        			}else{//读数据库中是否有此用户的滚动、闪烁、显示配置，否则默认都没有
        				bean=new LazyDynaBean();
        				bean.set("id",id);
						bean.set("name",name);
						bean.set("scroll","0");
    	      			bean.set("twinkle","0");
    	      			bean.set("show","1");
    	      			treeMap.put(new Integer(++this.maxorder), bean);
        			}
					//showitem.add(bean);
	        	}
			}
		}
	}
	private void outPortalColumnPanel(Element parent,ArrayList colList)
	{
		List list=parent.getChildren();
		if(list.size()>0)
		{
			for(int i=0;i<list.size();i++)
			{
	        	Element element=(Element)list.get(i);
	        	String name=element.getAttributeValue("name");	
	        	String id=element.getAttributeValue("id");
	        	String hide=element.getAttributeValue("hide");
	        	String priv=element.getAttributeValue("priv");		        	
	        	/**此面板隐藏*/
	        	if("true".equalsIgnoreCase(hide))
	        		continue;
	        	if("0234".equals(id)&&userView.getVersion()>=70) {//70锁隐藏我的任务
	        		continue;
	        	}
	        	if("0236".equals(id)&&userView.getVersion()<70) {//70以下锁不显示职称评审 wangb 2019p-07-17 bug 50376 
					continue;
				}
	        	/**此面板需要授权时*/
	        	if("true".equalsIgnoreCase(priv))
	        	{
	        		if(userView.isSuper_admin() || userView.isHaveResource(IResourceConstant.PORTAL, id))
	        		{
	        			//读数据库中是否有此用户的滚动、闪烁、显示配置，否则默认都没有
	        				LazyDynaBean bean=new LazyDynaBean();
	        				bean.set("id",id);
							bean.set("name",name);
							bean.set("scroll","0");
	    	      			bean.set("twinkle","0");
	    	      			bean.set("show","1");//默认显示
	    	      			colList.add(bean);
	        		}
	        	}else{
	        		 //读数据库中是否有此用户的滚动、闪烁、显示配置，否则默认都没有
	        			LazyDynaBean bean=new LazyDynaBean();
        				bean.set("id",id);
						bean.set("name",name);
						bean.set("scroll","0");
    	      			bean.set("twinkle","0");
    	      			bean.set("show","1");
    	      			colList.add(bean);
	        	}
			}
		}
	}
}
