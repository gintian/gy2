package com.hjsj.hrms.service;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.constant.SystemConfig;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IssuanceServiceXml {

	/**
	 * 读参数的内容
	 *
	 */
	private Document doc;
	private String xmlcontent="";
	private String hrp_logon_url="";
	public IssuanceServiceXml()
	{
		try {
			this.hrp_logon_url=SystemConfig.getPropertyValue("hrp_logon_url");
			if (StringUtils.isBlank(this.hrp_logon_url)) {
				System.out.println("必须配置参数hrp_logon_url");
			}
			init();
			this.doc=PubFunc.generateDom(xmlcontent.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	private void init()
	{
		StringBuffer strxml=new StringBuffer();
		
		strxml.append("<?xml version='1.0'?>");
		strxml.append("<rss version=\"2.0\">");
		strxml.append("<channel>");
		strxml.append("<title>人力资源系统</title>");
		strxml.append("<link>"+this.hrp_logon_url+"</link>");
		strxml.append("<description>集团人力资源系统</description>");
		strxml.append("<language>zh-cn</language>");
		strxml.append("</channel>");
		strxml.append("</rss>");		
		xmlcontent=strxml.toString();
	}
	public String saveParamAttribute(ArrayList list,String username)
	{
		StringBuffer buf=new StringBuffer();
		if(list==null||list.size()<=0)
			return "";
		try
		{
			String path="/rss/channel";
			XPath xpath=XPath.newInstance(path);
			List childlist=xpath.selectNodes(doc);
			Element elementChannel=(Element)childlist.get(0);
			Element element=null;
			LazyDynaBean bean=null;
			Date date=new Date();
			String dates=DateUtils.format(date, "yyyy-MM-dd HH:mm:ss");
			
			for(int i=0;i<list.size();i++)
			{
				bean=(LazyDynaBean)list.get(i);
				element=new Element("item");
				Element child=new Element("title");				
				element.addContent(child.setText((String)bean.get("title")));
				child=new Element("link");	
				//先判断是否已经存在了http字符,有就不再加hrp_logon_url
				if(bean.get("url")!=null){
					if(!((String)bean.get("url")).contains("http")){
						child.setText(this.hrp_logon_url+""+(String)bean.get("url"));
					}else{
						child.setText((String)bean.get("url"));
					}
				}else{
					child.setText("");
				}
				
				element.addContent(child);
				child=new Element("description");
				child.setText((String)bean.get("description"));
				element.addContent(child);
				child=new Element("author");
				child.setText(username);
				element.addContent(child);
				
				// 申请人
				child=new Element("applyname");
				String applyname = (String)bean.get("applyname");
				applyname = applyname == null ? "" : applyname;
				child.setText(applyname);
				element.addContent(child);
				
				child=new Element("pubDate");
				String datetime = (String)bean.get("datetime");
				if(datetime!=null && datetime.trim().length()>0)				
					child.setText(datetime);
				else				
					child.setText(PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"));									
			//	child.setText(date.toGMTString());
				element.addContent(child);  
				elementChannel.addContent(element);
			}			
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(doc));
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return buf.toString();
	}
	public String  errorMessage(String message)
	{
		StringBuffer strxml=new StringBuffer();
		
		strxml.append("<?xml version='1.0'?>");
		strxml.append("<rss version=\"2.0\">");
		strxml.append("<channel>");
		strxml.append("<title>人力资源系统</title>");
		strxml.append("<link></link>");
		strxml.append("<description>集团人力资源系统</description>");
		strxml.append("<language>zh-cn</language>");
		strxml.append("<error>"+message+"</error>");
		strxml.append("</channel>");
		strxml.append("</rss>");		
		return strxml.toString();
	}
}
