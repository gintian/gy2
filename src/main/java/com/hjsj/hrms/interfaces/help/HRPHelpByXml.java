package com.hjsj.hrms.interfaces.help;

import com.hrms.struts.exception.GeneralException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.util.ArrayList;

public class HRPHelpByXml {
	
	private String params;    // 参数
	private String actionName;
	private String target;

	/**
	 * 帮助系统树
	 * 
	 * @param params
	 * @param actionName
	 */
	public HRPHelpByXml(String params, String actionName, String target) {
		this.params = params;
		this.actionName = actionName;
		this.target = target;
	}

	public String outPutHRPHelpXml() throws GeneralException {

		// 生成的XML文件
		StringBuffer xmls = new StringBuffer();

		// 创建xml文件的根元素
		Element root = new Element("TreeNode");
		// 设置根元素属性
		root.setAttribute("id", "00");
		root.setAttribute("text", "helproot");
		root.setAttribute("title", "hrphelp");

		// 创建xml文档自身
		Document myDocument = new Document(root);

		// 设置跳转字符串
		String theaction = null;

		HRPHelpXmlAnalyse hhxa = new HRPHelpXmlAnalyse();
		hhxa.init();
		ArrayList helpList = hhxa.getHRPHelps(this.params);
		
	/*	for(int i =0 ; i< helpList.size(); i++){
			HRPHelp hh =(HRPHelp)helpList.get(i);
			System.out.print("ID=" + hh.getHelp_id());
			System.out.print(" name=" + hh.getHelp_name());
			System.out.print(" url=" + hh.getHelp_url());
			System.out.print(" modeflag=" + hh.getHelp_moduleflag());
			System.out.println();
		}*/
		
		for (int i = 0; i < helpList.size(); i++) {
			HRPHelp hh = (HRPHelp) helpList.get(i);
			// 创建子元素
			Element child = new Element("TreeNode");
			// 设置子元素属性
			String help_id = hh.getHelp_id();
			String helpname = hh.getHelp_name();

			child.setAttribute("id", help_id);
			child.setAttribute("text", helpname);
			child.setAttribute("title", helpname);
			theaction = this.actionName + "?b_query=link&helpid=" + help_id;

			child.setAttribute("href", theaction);
			child.setAttribute("target", this.target);
			// child.setAttribute("icon", "/images/dept.gif");
			child.setAttribute("file", this.target);
			child.setAttribute("xml", "help_tree.jsp?params=" + help_id);

			// 将子元素作为内容添加到根元素
			root.addContent(child);
		}

		XMLOutputter outputter = new XMLOutputter();

		// 格式化输出类
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);

		// 将生成的XML文件作为字符串形式
		xmls.append(outputter.outputString(myDocument));

		return xmls.toString();

	}

}