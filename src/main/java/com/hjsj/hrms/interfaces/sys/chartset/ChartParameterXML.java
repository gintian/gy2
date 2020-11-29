/**
 * 
 */
package com.hjsj.hrms.interfaces.sys.chartset;

import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Owner
 *
 */
public class ChartParameterXML {
	
	/*jfreechart图形参数设置
	<?xml version="1.0" encoding="GB2312"?>
	<chartParameter>
	 <user id="">
		<chartTitle align="left|center|right"/>
		<chartMargin  marginLeft="" marginRight="" marginTop="" marginBottom="" />
		<chartValueAxis isAuto="true|false" startValue="" endValue="" increment=""/>
	 </user>
	</chartParameter>
	*/
	
	
	private Connection conn=null;
	private String chartParameterxml=null;
	private Document document=null;
	
	public ChartParameterXML(String chartParameterxml){
		if(chartParameterxml == null || "".equals(chartParameterxml)){
		}else{
			this.chartParameterxml = chartParameterxml;
			this.initDocument();
		}
		
	}

	public ChartParameterXML(Connection conn){
		this.conn = conn;
		this.initChartParameterXML();
		this.initDocument();
	}
	
	
	/**
	 * 查询DB 获取XML文件字符串
	 */
	private void initChartParameterXML(){
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{	
			//常量表中查找rp_param常量
			rs=dao.search("select STR_VALUE  from CONSTANT where CONSTANT='CHART_PARAMETER'");
			if(rs.next()){
				//获取XML文件
				int dbserver = Sql_switcher.searchDbServer();
				if(dbserver == 2){//oracle
					this.chartParameterxml = Sql_switcher.readMemo(rs,"STR_VALUE");
				}else{ //mssql
					//获取XML文件
					this.chartParameterxml = rs.getString("STR_VALUE");	
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 获得ChartParameterXML文档对象
	 */
	private void initDocument() {
		if(this.chartParameterxml==null || "".equals(this.chartParameterxml)){
		}else{
			try {
				this.document = PubFunc.generateDom(chartParameterxml);
			} catch (Exception e) {
				e.printStackTrace();
				//throw GeneralExceptionHandler.Handle(e); 
			}
		}
	}
	
	/**
	 * 判断用户是否存在jfreechart设置
	 * @param userName 用户名
	 * @return
	 * @throws GeneralException
	 */
	public boolean isUserExist(String userName) throws GeneralException{
		boolean b = false;
		if(this.chartParameterxml == null || "".equals(this.chartParameterxml)){
			return b;
		}
		if(userName == null || "".equals(userName)){
			return b;
		}
		
		StringBuffer temp = new StringBuffer();
		temp.append("/chartParameter/user[@id='");
		temp.append(userName);
		temp.append("']");
		try {
			XPath xPath = XPath.newInstance(temp.toString());
			Element user = (Element) xPath.selectSingleNode(this.document);
			if (user == null) {
			} else {
				b=true;
			}
		} catch (JDOMException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
		return b;
	}
	
	/**
	 * 创建chartParameterXML参数配置信息（系统第一次设置时调用）
	 * @param userName
	 * @param chartParameter
	 */
	public void createChartParameterXML(String userName , ChartParameter chartParameter){
		
		if(userName == null || "".equals(userName) || chartParameter == null ){
			return ;
		}

		String align=chartParameter.getChartTitleAlign();
		String marginLeft = String.valueOf(chartParameter.getItemLeftMargin());
		String marginRight = String.valueOf(chartParameter.getItemRightMargin());
		String marginTop = String.valueOf(chartParameter.getItemLowerMargin());
		String marginBottom = String .valueOf(chartParameter.getItemUpperMargin());
		boolean isAuto = chartParameter.isAutoRangeValue();
		String startValue = "";
		String endValue = "";
		if(!isAuto){
			startValue = String.valueOf(chartParameter.getNumberAxisStartValue());
			endValue = String.valueOf(chartParameter.getNumberAxisEndValue());
		}
		String increment = String.valueOf(chartParameter.getNumberAxisIncrement());

		Element chartParam = new Element("chartParameter");
		
		Element user = new Element("user");
		user.setAttribute("id",userName);
	
		Element chartTitle = new Element("chartTitle");
		chartTitle.setAttribute("align",align);
		
		Element chartMargin = new Element("chartMargin");
		chartMargin.setAttribute("marginLeft",marginLeft);
		chartMargin.setAttribute("marginRight",marginRight);
		chartMargin.setAttribute("marginTop",marginTop);
		chartMargin.setAttribute("marginBottom",marginBottom);
		
		Element chartValueAxis = new Element("chartValueAxis");
		chartValueAxis.setAttribute("isAuto",String.valueOf(isAuto));
		chartValueAxis.setAttribute("startValue",startValue);
		chartValueAxis.setAttribute("endValue",endValue);
		chartValueAxis.setAttribute("increment",increment);
		
		user.addContent(chartTitle);
		user.addContent(chartMargin);
		user.addContent(chartValueAxis);
		
		chartParam.addContent(user);
		
		Document myDocument = new Document(chartParam);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		String chartParameterXMLResult= outputter.outputString(myDocument);
		
		/*System.out.println("*************创建XML********************");
		System.out.println(chartParameterXMLResult);
		System.out.println();*/
		
		//添加记录到DB
		String sql="insert into CONSTANT (CONSTANT,Str_Value) values('CHART_PARAMETER','"+chartParameterXMLResult+"')";
		this.executeSQL(sql);
		
	}
	
	/**
	 * 追加一个用户设置
	 * @param userName
	 * @param chartParameter
	 */
	public void addChartParameter(String userName, ChartParameter chartParameter){
		String chartParameterXMLResult = null;
		
		Element root = document.getRootElement();
		String align=chartParameter.getChartTitleAlign();
		String marginLeft = String.valueOf(chartParameter.getItemLeftMargin());
		String marginRight = String.valueOf(chartParameter.getItemRightMargin());
		String marginTop = String.valueOf(chartParameter.getItemLowerMargin());
		String marginBottom = String .valueOf(chartParameter.getItemUpperMargin());
		boolean isAuto = chartParameter.isAutoRangeValue();
		String startValue = "";
		String endValue = "";
		if(!isAuto){
			startValue = String.valueOf(chartParameter.getNumberAxisStartValue());
			endValue = String.valueOf(chartParameter.getNumberAxisEndValue());
		}
		String increment = String.valueOf(chartParameter.getNumberAxisIncrement());

		Element user = new Element("user");
		user.setAttribute("id",userName);
	
		Element chartTitle = new Element("chartTitle");
		chartTitle.setAttribute("align",align);
		
		Element chartMargin = new Element("chartMargin");
		chartMargin.setAttribute("marginLeft",marginLeft);
		chartMargin.setAttribute("marginRight",marginRight);
		chartMargin.setAttribute("marginTop",marginTop);
		chartMargin.setAttribute("marginBottom",marginBottom);
		
		Element chartValueAxis = new Element("chartValueAxis");
		chartValueAxis.setAttribute("isAuto",String.valueOf(isAuto));
		chartValueAxis.setAttribute("startValue",startValue);
		chartValueAxis.setAttribute("endValue",endValue);
		chartValueAxis.setAttribute("increment",increment);
		
		user.addContent(chartTitle);
		user.addContent(chartMargin);
		user.addContent(chartValueAxis);
		
		root.addContent(user);
		
		Document myDocument = new Document(root);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		chartParameterXMLResult= outputter.outputString(myDocument);
		
		/*System.out.println("*************添加用户配置XML********************");
		System.out.println(chartParameterXMLResult);
		System.out.println();*/
		
		String sql = "update CONSTANT set Str_Value='"+chartParameterXMLResult+"' where CONSTANT='CHART_PARAMETER'";
		this.executeSQL(sql);
	}
	
	/**
	 * 修改指定用户设置
	 * @param userName
	 * @param chartParameter

	 */
	public void updateChartParameter(String userName,ChartParameter chartParameter){
		String chartParameterXMLResult="";
		StringBuffer temp1 = new StringBuffer();
		temp1.append("/chartParameter/user[@id='");
		temp1.append(userName);
		temp1.append("']");
		try {
			XPath xPath = XPath.newInstance(temp1.toString());
			Element user = (Element) xPath.selectSingleNode(this.document);
			
			String align=chartParameter.getChartTitleAlign();
			String marginLeft = String.valueOf(chartParameter.getItemLeftMargin());
			String marginRight = String.valueOf(chartParameter.getItemRightMargin());
			String marginTop = String.valueOf(chartParameter.getItemLowerMargin());
			String marginBottom = String .valueOf(chartParameter.getItemUpperMargin());
			boolean isAuto = chartParameter.isAutoRangeValue();
			String startValue = "";
			String endValue = "";
			if(!isAuto){
				startValue = String.valueOf(chartParameter.getNumberAxisStartValue());
				endValue = String.valueOf(chartParameter.getNumberAxisEndValue());
			}
			String increment = String.valueOf(chartParameter.getNumberAxisIncrement());
		
			Element chartTitle = user.getChild("chartTitle");
			chartTitle.getAttribute("align").setValue(align);
			
			Element chartMargin = user.getChild("chartMargin");
			chartMargin.getAttribute("marginLeft").setValue(marginLeft);
			chartMargin.getAttribute("marginRight").setValue(marginRight);
			chartMargin.getAttribute("marginTop").setValue(marginTop);
			chartMargin.getAttribute("marginBottom").setValue(marginBottom);
			
			Element chartValueAxis = user.getChild("chartValueAxis");
			chartValueAxis.getAttribute("isAuto").setValue(String.valueOf(isAuto));
			chartValueAxis.getAttribute("startValue").setValue(startValue);
			chartValueAxis.getAttribute("endValue").setValue(endValue);
			chartValueAxis.getAttribute("increment").setValue(increment);
			
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			chartParameterXMLResult = outputter.outputString(this.document);
		} catch (JDOMException e) {
			e.printStackTrace();
			//throw GeneralExceptionHandler.Handle(e);
		}

		/*System.out.println("*************修改用户XML********************");
		System.out.println(chartParameterXMLResult);
		System.out.println();*/
		
		String sql = "update CONSTANT set Str_Value='"+chartParameterXMLResult+"' where CONSTANT='CHART_PARAMETER'";
		this.executeSQL(sql);
	}
	
	/**
	 * 删除指定用户设置信息
	 * @param userName
	 * @return
	 */
	public void deleteChartParameter(String userName){
		String chartParameterXMLResult="";
		StringBuffer temp1 = new StringBuffer();
		temp1.append("/chartParameter/user[@id='");
		temp1.append(userName);
		temp1.append("']");
		try {
			XPath xPath = XPath.newInstance(temp1.toString());
			Element user = (Element) xPath.selectSingleNode(this.document);
			if(user == null){
			}else{
				this.document.removeContent(user);
			}
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			chartParameterXMLResult = outputter.outputString(this.document);
		} catch (JDOMException e) {
			e.printStackTrace();
			//throw GeneralExceptionHandler.Handle(e);
		}
		String sql = "update CONSTANT set Str_Value='"+chartParameterXMLResult+"' where CONSTANT='CHART_PARAMETER'";
		this.executeSQL(sql);
	}
	
	/**
	 * 查找指定用户设置
	 * @param userName 用户名
	 * @return 用户图例设置对象 系统默认设置/用户特定设置
	 */
	public ChartParameter searchChartParameter(String userName){
		ChartParameter chartParameter = new ChartParameter();
		
		if(this.chartParameterxml == null || "".equals(this.chartParameterxml)){
			return chartParameter;
		}
		
		StringBuffer temp1 = new StringBuffer();
		temp1.append("/chartParameter/user[@id='");
		temp1.append(userName);
		temp1.append("']");
		try {
			XPath xPath = XPath.newInstance(temp1.toString());
			Element user = (Element) xPath.selectSingleNode(this.document);
			if(user == null){
				return chartParameter;
			}else{
				Element chartTitle = user.getChild("chartTitle");
				
				//System.out.println("__"+chartTitle.getAttributeValue("align"));
				
				chartParameter.setChartTitleAlign(chartTitle.getAttributeValue("align"));
				
				Element chartMargin = user.getChild("chartMargin");
				chartParameter.setItemLeftMargin(Double.parseDouble(chartMargin.getAttributeValue("marginLeft")));
				chartParameter.setItemRightMargin(Double.parseDouble(chartMargin.getAttributeValue("marginRight")));
				chartParameter.setItemLowerMargin(Double.parseDouble(chartMargin.getAttributeValue("marginTop")));
				chartParameter.setItemUpperMargin(Double.parseDouble(chartMargin.getAttributeValue("marginBottom")));
				
				Element chartValueAxis = user.getChild("chartValueAxis");
				boolean b = Boolean.parseBoolean(chartValueAxis.getAttributeValue("isAuto"));				
				chartParameter.setAutoRangeValue(b);
				if(!b){
					chartParameter.setNumberAxisStartValue(Double.parseDouble(chartValueAxis.getAttributeValue("startValue")));
					chartParameter.setNumberAxisEndValue(Double.parseDouble(chartValueAxis.getAttributeValue("endValue")));
				}
				chartParameter.setNumberAxisIncrement(Double.parseDouble(chartValueAxis.getAttributeValue("increment")));
			}
		} catch (JDOMException e) {
			e.printStackTrace();
			//throw GeneralExceptionHandler.Handle(e);
		}
		return chartParameter;
	}
	
	/**
	 * jfreechart参数设置控制器
	 * @param userName		 用户名
	 * @param chartParameter 参数类 
	 * @throws GeneralException
	 */
	public void chartParameterControl(String userName ,ChartParameter chartParameter) throws GeneralException{
		//没有XML配置信息->创建新XML配置信息
		if(this.chartParameterxml == null || "".equals(this.chartParameterxml)){
			this.createChartParameterXML(userName,chartParameter);
		}else{//有XML配置信息
			if(this.isUserExist(userName)){//存在此用户配置信息
				this.updateChartParameter(userName,chartParameter);
			}else{//不存在此用户配置信息
				this.addChartParameter(userName,chartParameter);
			}
		}
	}
	
	/**
	 * 执行常量表中图例配置信息 （增/删/改）操作
	 * @param sql
	 */
	public void executeSQL(String sql){
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			dao.update(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
