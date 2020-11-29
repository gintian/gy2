<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.taglib.CommonData"%>
<%
	String menuname = "";
%>
<style type="text/css"> 
.scroll_box {
    border: 1px solid #eee;
    height: 280px;    
    width: 270px;            
    overflow: auto;            
    margin: 1em 1;
}
</style>
<body  oncontextmenu=return(onloadMenu()) onclick="showoff();" onload="toNext()">
	<html:form action="/org/orgdata/orgdata">
		<%
		int i = 0;
		StringBuffer tabstr= new StringBuffer();
		tabstr.append("<table id=\"tableShow\" width=\"130\" ");
		tabstr.append("cellpadding=\"0\"  cellspacing=\"0\" border=\"0\">");
		%>
		<hrms:tabset name="cardset" width="100%" height="98%" type="true">
			<logic:iterate id="element" name="orgDataForm" property="setlist"
				indexId="index">
				<%
				CommonData item=(CommonData)pageContext.getAttribute("element");
            	String fieldid=item.getDataValue();
            	String fielddesc=item.getDataName();
				if(!fieldid.equalsIgnoreCase("B01")&&!fieldid.equalsIgnoreCase("K01")){	
					++i;
				tabstr.append("<tr height=\"20\" id=\"mitem"+i+"_1\" onclick=\"itemHref('");
				tabstr.append("mitem"+i);
				tabstr.append("',this);\" onMouseover=\"mover(this,'mitem"+i+"');\"");
				tabstr.append(" onMouseout=\"mout(this,'mitem"+i+"');\">");
				tabstr.append("<td style=\"cursor:pointer\">");
				%>
				<logic:equal name="orgDataForm" property="defitem" value="<%=fieldid%>">
					<%menuname = "mitem"+i;%>
				</logic:equal>
				<% 
				if(!fieldid.equalsIgnoreCase("t_vorg_staff")){
					if(i>9)
						tabstr.append(i+".");
					else
						tabstr.append("0"+i+".");
				}
				tabstr.append(fielddesc);
				tabstr.append("</td></tr>");
				%>
				<bean:define id="nid" value="<%=fieldid%>"/>
				<hrms:tab name='<%="mitem" + i%>' label="<%=fielddesc %>"
					function_id="" visible="true"
					url="/org/orgdata/orgdata.do?b_ritem=link&itemid=${orgDataForm.itemid}&fieldid=${nid}&infor=${orgDataForm.infor}">
				</hrms:tab>
				<%} %>
			</logic:iterate>
		</hrms:tabset>
		<%tabstr.append("</table>"); %>
		<div id="mlay" style="position:absolute;display:none;overflow:auto;height:150px;width:160px;">
		<%=tabstr%>
		</div>
		<input type="button" name="selectbutton" value="aaa" onclick="selectHref();" style="display:none;"/>
	</html:form>
</body>
<script language="javascript">
//菜单没有选中的背景色和文字色 
var bgc="#FFFFFF",txc="black";
//菜单选中的选项背景色和文字色
var cbgc="#FFF8D2",ctxc="black";
var menutable = "mitem1_1";

function mover(obj,menuname){
	var tabid = menuname+"_1";
	if(menutable!=tabid){
		obj.style.background=cbgc;
		obj.style.color=ctxc ;
	}
}
function mout(obj,menuname){
	var tabid = menuname+"_1";
	if(menutable!=tabid){
		obj.style.background=bgc;
		obj.style.color=txc ;
	}
}
function showoff() { 
	mlay.style.display="none"; 
} 
function onloadMenu(){
	mlay.style.display=""; 
	mlay.style.pixelTop=event.clientY; 
	mlay.style.pixelLeft=event.clientX; 
	mlay.style.background=bgc; 
	mlay.style.color=txc; 
 	return false;
}
function toNext(){
	var tabid="<%=menuname%>";
	if(tabid!=null&&tabid.length>0){
		var tab=$('cardset');
		tab.setSelectedTab("<%=menuname%>");
		menutable = tabid+"_1";
		var obj = document.getElementById(menutable);
		obj.style.background=cbgc;
		obj.style.color=ctxc ;
	}
}
function itemHref(menuname,obj){
	if(menuname!=null&&menuname.length>0){
		var tab=$('cardset');
		tab.setSelectedTab(menuname);
		var obj1 = document.getElementById(menutable);
		obj1.style.background=bgc;
		obj1.style.color=txc ;
		
		obj.style.background=cbgc;
		obj.style.color=ctxc ;
		menutable = menuname+"_1";
	}
}
function selectHref(){
	var tab=$('cardset');
	var seltab = tab.getSelectedTab();
	var menuname = seltab.tabName;
	
	if(menuname!=null&&menuname.length>0){
		var obj1 = document.getElementById(menutable);
		if(!obj1)
			return false;
		obj1.style.background=bgc;
		obj1.style.color=txc ;

		var obj = document.getElementById(menuname+"_1");
		if(!obj)
			return false;
		obj.style.background=cbgc;
		obj.style.color=ctxc ;
		menutable = menuname+"_1";
		
	}else{
		var obj = document.getElementById("mitem1_1");
		if(!obj)
			return false;
		obj.style.background=cbgc;
		obj.style.color=ctxc ;
	}
}
</script>
