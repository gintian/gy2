package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.sys.sysout.SyncBo;
import com.hjsj.hrms.service.ProcessService;
import com.hjsj.hrms.service.ladp.PareXmlUtils;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import org.apache.log4j.Category;
import org.jdom.Element;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.sql.RowSet;
import javax.xml.soap.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>Title:TransferWebService.java</p>
 * <p>Description>:通用的调用第三方WebService接口后台作业类</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2014-03-28 上午09:56:54</p>
 * <p>@version: 1.0</p>
 * <p>@author:JinChunhai</p>
 */

public class TransferWebService implements Job 
{
	// 记录日志
	private Category log = Category.getInstance(this.getClass().getName());	
	// 
	private ProcessService pro = new ProcessService();

	/**
	 * 根据接口标准，实现execute方法
	 */
	@Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException
	{		
		// 数据库链接
		Connection conn = null;
		try 
		{
			// 获取数据库链接
			conn = AdminDb.getConnection();
			log.debug("获取了数据库链接:" + conn.toString());
			
			// 此处调用SyncBo类，只是为了获取配置文件路径
			SyncBo bo = new SyncBo(conn);
			// 获取当前使用的中间件的名称，由于jboss与其他中间件不同，所有jboss要分情况处理
			String webserver = SystemConfig.getPropertyValue("webserver");
			
			// 扫描classpath目录中是否有syncService.xml文件
			File file = null;
			if (webserver!=null && ("jboss".equalsIgnoreCase(webserver)|| "inforsuite".equalsIgnoreCase(webserver)))
			{
				String pathStr = SystemConfig.getPropertyValue("jbossxmlpath");
				file = new File(pathStr, "syncService.xml");
			} else 
			{
				file = bo.getFilePath("syncService.xml");
			}
			
			// 未找到配置文件,停止执行程序
			if (file == null) 
			{
				log.error("在${tomcat}/config/目录下未找到syncService.xml文件，程序停止运行！linux系统请注意大小写的问题！");
				return ;
			}
						
			// 解析配置文件并获取需要的配置信息
			PareXmlUtils utils = new PareXmlUtils(file);			
			log.debug("解析文件正确");
			
			// 解析xml获得第三方WebService方法参数信息
			HashMap paramMap = pareXMLParamMap(conn,utils);
			
			// 获取xml后解析同步数据到数据库
			String succeedXml = syncXmlToDatabase(utils,paramMap); 			
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			log.error("程序执行中出现异常情况，程序停止执行,错误原因：" + e.getMessage());
		} finally 
		{
			try 
			{
				if (conn != null) 
				{
					conn.close();
					log.debug("关闭数据库链接");
				}
			} 
			catch(Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取xml后解析同步数据到数据库
	 * @param utils syncService.xml配置文件
	 * @return String
	 */
	private String syncXmlToDatabase(PareXmlUtils utils,HashMap paramMap) 
	{
		// 将解析后的配置信息保存到map中
		String succeedXml = "";
		try 
		{	/**
			 <?xml version="1.0" encoding = "GB2312" ?>
				<sync>
					<!--定义WebService方法的参数-->
					<params>
						<!--name:参数名; type:参数类型 constant常量 sql 变量; value:参数值 -->
						<param name="strValue" type="constant" value="ORs1P9DMem9byoEeSArcK+T60iqKxo6h2M7ONzAhR7w="></param>
						<param name="datetime" type="sql" value="select getdate()"></param>		
					</params>		
	
					<!--定义WebService地址-->
					<webservice_url>
						<!-- typename:调用流程集成 syncProcess(String type, String xml)需要的type值  url:WebService地址; username:连接webservice的用户名; 
		     			 	 password:连接webservice的密码; type:密码加密类型;  prefix:命名空间;  uri:命名空间地址 -->
						<webservice typename="SYNC_ALAN" url="" username="" password="" type="" prefix="" uri="" >用soapUI生成的soap串</webservice>
						<webservice typename="SYNC_CRED" url="" username="" password="" type="" prefix="" uri="" >用soapUI生成的soap串</webservice>			
						<webservice typename="SYNC_WEAL" url="" username="" password="" type="" prefix="" uri="" >用soapUI生成的soap串</webservice>		
					</webservice_url>	
				</sync>			 
			**/			
			
			// 获取所有webservice节点
			List paramList = utils.getNodes("/sync/webservice_url/webservice");
			log.debug("获取所有webservice节点，解析正确");
			
			for (int i = 0; i < paramList.size(); i++) 
			{
				Element paramEl = (Element)paramList.get(i);
				
				// 调用流程集成 syncProcess(String type, String xml)需要的type值
				String typename = paramEl.getAttributeValue("typename");
				typename = typename == null ? "" : typename;				
				// WebService地址
				String url = paramEl.getAttributeValue("url");
				url = url == null ? "" : url;			
				// 连接webservice的用户名
				String username = paramEl.getAttributeValue("username");
				username = username == null ? "" : username;
				// 连接webservice的密码
				String password = paramEl.getAttributeValue("password");
				password = password == null ? "" : password;
				// 密码加密类型
				String type = paramEl.getAttributeValue("type");
				type = type == null ? "" : type;
				// 命名空间
				String prefix = paramEl.getAttributeValue("prefix");
				prefix = prefix == null ? "" : prefix;
				// 命名空间地址
				String uri = paramEl.getAttributeValue("uri");
				uri = uri == null ? "" : uri;				
				// WebService生成的soap串
				String soapUIStr = paramEl.getText();
				
				
				// soapUI方式调用WebService 获得返回值
				String xml = soapUIWebService(url,soapUIStr,paramMap);
//				System.out.println(xml.toString());
				log.debug("信贷系统返回值：" + xml.toString());
/*				
				if(typename!=null && typename.trim().length()>0 && typename.equalsIgnoreCase("SYNC_WEAL"))
				{
					System.out.println(xml);
					File fileaa = new File("c:/bb", "text.txt");
					FileWriter writer = new FileWriter(fileaa);
					writer.write(xml.toString());
					writer.flush();
				}
*/								
				// 调用流程集成方法
				succeedXml = pro.syncProcess(typename,xml);
				if(succeedXml!=null && succeedXml.trim().length()>0)
				{
					log.debug("获取数据成功！");					
				}
			}			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			log.error("解析xml节点出错，错误原因" + e.getMessage());
		} 		
		
		return succeedXml;
	}
	
	/**
	 * soapUI方式调用WebService
	 * @param utils syncService.xml配置文件
	 * @return String
	 */
	private String soapUIWebService(String url,String soapUIStr,HashMap paramMap) 
	{
		String xml = "";
		SOAPConnection con = null;
		try 
		{			
			SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
			con = factory.createConnection();
			SOAPMessage request = MessageFactory.newInstance().createMessage();
			SOAPPart soapPart = request.getSOAPPart();
									
			// 替换soapUI串中的参数
			Iterator iter = paramMap.entrySet().iterator();
			while (iter.hasNext()) 
			{
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String)entry.getKey();
				String value = (String)entry.getValue();
				
				soapUIStr = soapUIStr.replace("$"+key+"$",value);										
			}			
		//	System.out.println(soapUIStr);
						
			Reader reader = new StringReader(soapUIStr);						
			Source source = new StreamSource(reader);
			soapPart.setContent(source);			
						
			SOAPMessage response = con.call(request, url);			
		//	StringBuffer responseStr = new StringBuffer();
		//	response.writeTo(System.out);
			SOAPBody responseBody = response.getSOAPBody();
			xml = responseBody.getFirstChild().getFirstChild().getTextContent();
			
			log.debug("调用WebService获得的返回值："+xml);					
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			log.error("调用WebService出错，错误原因" + e.getMessage());
		} 
		
		return xml;
		
	}
	
	/**
	 * 解析xml文件
	 * @param utils syncService.xml配置文件
	 * @return HashMap
	 */
	private HashMap pareXMLParamMap(Connection conn,PareXmlUtils utils) 
	{
		// 将解析后的配置信息保存到map中
		HashMap paramMap = new HashMap();
		RowSet rs = null;
		try 
		{
			ContentDAO dao = new ContentDAO(conn);
			/**
			 <?xml version="1.0" encoding = "GB2312" ?>
				<sync>
					<!--定义WebService方法的参数-->
					<params>
						<!--name:参数名; type:参数类型 constant常量 sql 变量; value:参数值 -->
						<param name="strValue" type="constant" value="ORs1P9DMem9byoEeSArcK+T60iqKxo6h2M7ONzAhR7w="></param>
						<param name="datetime" type="sql" value="select getdate()"></param>		
					</params>		
	
					<!--定义WebService地址-->
					<webservice_url>
						<!-- typename:调用流程集成 syncProcess(String type, String xml)需要的type值  url:WebService地址; username:连接webservice的用户名; 
		     			 	 password:连接webservice的密码; type:密码加密类型;  prefix:命名空间;  uri:命名空间地址 -->
						<webservice typename="SYNC_ALAN" url="" username="" password="" type="" prefix="" uri="" >用soapUI生成的soap串</webservice>
						<webservice typename="SYNC_CRED" url="" username="" password="" type="" prefix="" uri="" >用soapUI生成的soap串</webservice>			
						<webservice typename="SYNC_WEAL" url="" username="" password="" type="" prefix="" uri="" >用soapUI生成的soap串</webservice>		
					</webservice_url>	
				</sync>			 
			**/
			
			// 获取所有params节点
			List paramList = utils.getNodes("/sync/params/param");
			log.debug("获取所有param节点，解析正确");
			
			for (int i = 0; i < paramList.size(); i++) 
			{
				Element paramEl = (Element)paramList.get(i);
				
				// 参数名
				String paramName = paramEl.getAttributeValue("name");
				paramName = paramName == null ? "" : paramName;
				log.debug("获取参数名" + paramName + "，解析正确");				
				// 参数类型
				String paramType = paramEl.getAttributeValue("type");
				paramType = paramType == null ? "" : paramType;
				log.debug("获取参数类型" + paramType + "，解析正确");				
				// 参数值
				String paramValue = paramEl.getAttributeValue("value");
				paramValue = paramValue == null ? "" : paramValue;
				log.debug("获取参数值" + paramValue + "，解析正确");
				
				String value = paramValue;
				if((paramType!=null && paramType.trim().length()>0 && "sql".equalsIgnoreCase(paramType))
					&& (paramValue!=null && paramValue.trim().length()>0))
				{
					rs = dao.search(paramValue.toString());
					if (rs.next()) 
					{
						value = rs.getString(1);
					}					
				}

				paramMap.put(paramName, value);
			}			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			log.error("解析xml节点出错，错误原因" + e.getMessage());
		} 
		finally 
		{
			try 
			{
				if (rs != null) 
				{
					rs.close();
					log.debug("关闭数据集链接");
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		
		return paramMap;
	}
	
	/**
	 * 获取解析的xml值
	 * @param xml
	 * @param nodePath
	 * @return
	 */
/*
	private ArrayList getMapList(String xml, String nodePath, String prefix, String uri) 
	{
		PareXmlUtils xmlUtils = new PareXmlUtils(xml);
		ArrayList list = new ArrayList();
		List nodeList = xmlUtils.getNamespaceNodes(nodePath,prefix,uri);
		for (int i = 0; i < nodeList.size(); i++) 
		{
			Map map = new HashMap();
			Element el = (Element) nodeList.get(i);
			List li = el.getChildren();
			if (li != null) 
			{
				for (int j = 0; j < li.size(); j++) 
				{
					Element e = (Element) li.get(j);
					String value = e.getText();
					if (value == null) 					
						value = "";
					
					map.put(e.getName(), value);
				}
			}
			list.add(map);
		}
		return list;
	}
*/	
	
}