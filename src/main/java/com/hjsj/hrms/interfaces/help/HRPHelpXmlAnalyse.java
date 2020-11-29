/**
 * 
 */
package com.hjsj.hrms.interfaces.help;

import com.hjsj.hrms.utils.PubFunc;
import org.jdom.Document;
import org.jdom.Element;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:HRPHelpXmlAnalyse.java</p>
 * <p>Description:解析帮助系统XML文件</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 25, 2006:2:02:43 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class HRPHelpXmlAnalyse {

	private Document doc;
	
	
	public HRPHelpXmlAnalyse(){}
	
	/**
	 * 初始化XML数据
	 */
	public void init(){
		InputStream in = null;
		try {
			in=this.getClass().getResourceAsStream("/com/hjsj/hrms/constant/help.xml");
			doc = PubFunc.generateDom(in);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			PubFunc.closeIoResource(in);
		}
	}
	
	/**
	 * 获得指定元素的信息对象
	 * @param help_id  帮助ID
	 * @return
	 */
	public HRPHelp getHRPHelp(String help_id){
		HRPHelp hh = new HRPHelp();
		if("-1".equals(help_id)){//根
			Element root = doc.getRootElement();
			List list = root.getChildren();
			Element child = (Element)list.get(0);				
			hh.setHelp_id(child.getAttributeValue("help_id"));
			hh.setHelp_name(child.getAttributeValue("name"));
			hh.setHelp_url(child.getAttributeValue("url"));
			hh.setHelp_moduleflag(child.getAttributeValue("moduleflag"));				
	
		}else{
			Element child = this.searchHelpElement(help_id);
			hh.setHelp_id(child.getAttributeValue("help_id"));
			hh.setHelp_name(child.getAttributeValue("name"));
			hh.setHelp_url(child.getAttributeValue("url"));
			hh.setHelp_moduleflag(child.getAttributeValue("moduleflag"));	
		}	
		
		return hh;
		
	}
	
	/**
	 * 依据帮助ID获取其子节点的信息
	 * @param help_id 帮助ID
	 * @return    
	 */
	public ArrayList getHRPHelps(String help_id){
		ArrayList helpList = new ArrayList();		
		if("-1".equals(help_id)){//根
			Element root = doc.getRootElement();
			List list = root.getChildren();
			for(int i = 0; i<list.size();i++){
				Element child = (Element)list.get(i);				
				HRPHelp hh = new HRPHelp();
				hh.setHelp_id(child.getAttributeValue("help_id"));
				hh.setHelp_name(child.getAttributeValue("name"));
				hh.setHelp_url(child.getAttributeValue("url"));
				hh.setHelp_moduleflag(child.getAttributeValue("moduleflag"));				
				helpList.add(hh);
			}
		}else{
			Element child = this.searchHelpElement(help_id);
			if(child == null){				
			}else{
				List list = child.getChildren();
				for(int i = 0; i<list.size();i++){
					Element child1 = (Element)list.get(i);				
					HRPHelp hh = new HRPHelp();
					hh.setHelp_id(child1.getAttributeValue("help_id"));
					hh.setHelp_name(child1.getAttributeValue("name"));
					hh.setHelp_url(child1.getAttributeValue("url"));
					hh.setHelp_moduleflag(child1.getAttributeValue("moduleflag"));				
					helpList.add(hh);
				}
			}
		}	
		return helpList;
	}
	
	/**
	 * 依据帮助ID查找指定的元素
	 * @param help_id
	 * @return
	 */
	public Element searchHelpElement(String help_id){
		Element helpElement = null;	
		Element root = doc.getRootElement();
		List list = root.getChildren();
		for(int i = 0; i<list.size();i++){
			Element child = (Element)list.get(i);
			if(child.getAttributeValue("help_id").equals(help_id)){
				helpElement = child;
				return helpElement;
			}else{
				List list1 = child.getChildren();
				for(int j = 0; j<list1.size();j++){
					Element child1 = (Element)list1.get(j);
					if(child1.getAttributeValue("help_id").equals(help_id)){
						helpElement = child1;
						return helpElement;
					}else{
						List list2 = child1.getChildren();
						for(int k = 0; k<list2.size();k++){
							Element child2 = (Element)list2.get(k);
							if(child2.getAttributeValue("help_id").equals(help_id)){
								helpElement = child2;
								return helpElement;
							}
						}
					}
				}
			}
		}
		
		return helpElement;
		
	}
	/**
	 * 测试
	 * @param args
	 */
	public static void main(String[] args) {
		HRPHelpXmlAnalyse ha = new HRPHelpXmlAnalyse();
		ha.init();
		ArrayList list = ha.getHRPHelps("10");
		for(int i =0 ; i< list.size(); i++){
			HRPHelp hh =(HRPHelp)list.get(i);
			System.out.print("ID=" + hh.getHelp_id());
			System.out.print(" name=" + hh.getHelp_name());
			System.out.print(" url=" + hh.getHelp_url());
			System.out.print(" modeflag=" + hh.getHelp_moduleflag());
			System.out.println();
		}
	}

}
