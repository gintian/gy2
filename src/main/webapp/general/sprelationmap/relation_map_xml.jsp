 <%@ page language="java" contentType="text/html; charset=UTF-8"%>
 <%@page import="com.hjsj.hrms.actionform.general.sprelationmap.RelationMapForm,java.util.HashMap" %>
 <%@page import="java.util.ArrayList,com.hjsj.hrms.businessobject.general.sprelationmap.RelationMapLine,com.hjsj.hrms.businessobject.general.sprelationmap.RelationMapNode" %>
<%
 RelationMapForm rmform=(RelationMapForm)session.getAttribute("relationMapForm");
 HashMap map = (HashMap)rmform.getDataMap();
 ArrayList nodeList = (ArrayList)map.get("node");
 ArrayList lineList = (ArrayList)map.get("line");
 String truewidth=rmform.getTrueWidth();
 String trueheight=rmform.getTrueHeight();
 String fontStyle=rmform.getFontStyle();
StringBuffer xml=new StringBuffer("<?xml version='1.0' encoding='GBK'?>" );
xml.append("<chart caption='' bgColor='AEEEEE,FFFFFF'  BorderAlpha='0'  canvasBgAlpha='0,0' canvasBorderAlpha='0' xAxisMinValue='0'  xAxisMaxValue='"+truewidth+"' yAxisMinValue='0' viewMode='1'  yAxisMaxValue='"+trueheight+"'");
xml.append("   allowDrag='1'   bubbleScale='1' is3D='1' numDivLines='0' showFormBtn='0'>");
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

xml.append("</application>");
xml.append("</styles>");
xml.append("</chart>");
 
%>
<body>
<html:form action="/general/sprelationmap/relation_map_drawable">
<% 
  try 
	{ 
	  
	  response.getWriter().write(xml.toString()); 
	  response.getWriter().close();		   
	} 
	catch(Exception ee) 
	{ 
      	    ee.printStackTrace(); 
	} 
%> 
</html:form>
</body>