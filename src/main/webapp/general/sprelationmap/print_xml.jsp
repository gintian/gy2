 <%@ page language="java" contentType="text/html; charset=UTF-8"%>
 <%@page import="com.hjsj.hrms.actionform.general.sprelationmap.RelationMapForm,java.util.HashMap" %>
 <%@page import="java.util.ArrayList,com.hjsj.hrms.businessobject.general.sprelationmap.RelationMapLine,com.hjsj.hrms.businessobject.general.sprelationmap.RelationMapNode" %>
<%
 RelationMapForm rmform=(RelationMapForm)session.getAttribute("relationMapForm");
 HashMap map = (HashMap)rmform.getPrintDataMap();
 ArrayList nodeList = (ArrayList)map.get("node");
 ArrayList lineList = (ArrayList)map.get("line");
 String truewidth=rmform.getTrueWidth();
 String trueheight=rmform.getTrueHeight();
 String fontStyle=rmform.getFontStyle();
StringBuffer xml=new StringBuffer("<?xml version='1.0' encoding='GBK'?>" );
xml.append("<chart caption='' xAxisMinValue='0'  xAxisMaxValue='"+truewidth+"' yAxisMinValue='0' viewMode='1'  yAxisMaxValue='"+trueheight+"'");
xml.append("  exportDialogMessage='正在导出，请稍候...'  exportEnabled='1' exportAtClient='0' exportAction='save' exportFormat='PDF' exportHandler='FCExporter'    bubbleScale='1' is3D='1' numDivLines='0' showFormBtn='0'>");
xml.append("<dataset plotborderAlpha='100'   >");

for(int i=0;i<nodeList.size();i++){
   RelationMapNode node=(RelationMapNode)nodeList.get(i);
   xml.append(node.toNodeXml());
   xml.append(" ");
} 
xml.append("</dataset>");
xml.append("<connectors color='000000' stdThickness='8'>");
for(int j=0;j<lineList.size();j++){
   RelationMapLine line = (RelationMapLine)lineList.get(j);
   xml.append(line.toConnectorXml());
   xml.append(" ");
}
xml.append("</connectors>");
xml.append("<styles>");
xml.append("<definition>");
xml.append(fontStyle);
xml.append("</definition>");
xml.append("<application>");
xml.append("<apply toObject='DATALABELS' styles='MyFirstFontStyle' />"); 
xml.append("<apply toObject='DATAVALUES' styles='MyFirstFontStyle' />");
xml.append("<apply toObject='TOOLTIP' styles='MyFirstFontStyle' />");
xml.append("</application>");
xml.append("</styles>");
xml.append("</chart>");
 
%>

<% 
  try 
	{ 
	  //System.out.println(xml.toString());
	  response.getWriter().write(xml.toString()); 
	  response.getWriter().close();		   
	} 
	catch(Exception ee) 
	{ 
      	    ee.printStackTrace(); 
	} 
%> 
