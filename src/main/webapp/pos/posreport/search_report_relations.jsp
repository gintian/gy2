<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
    String bosflag="";	
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  bosflag=userView.getBosflag(); 
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<SCRIPT LANGUAGE=javascript src="/js/ReportRelationSvg.js"></SCRIPT> 

<SCRIPT LANGUAGE=javascript>
var childslist;
var grades;
var constant;
function searchChildsList(code,kind,dbname,constant)
{

   var hashvo=new ParameterSet();
   hashvo.setValue("code",code);
   hashvo.setValue("kind",kind);
   hashvo.setValue("constant","${orgMapForm.constant}");
   hashvo.setValue("dbnames","${orgMapForm.dbnames}");
   hashvo.setValue("isshowpersonconut","${orgMapForm.isshowpersonconut}");
   hashvo.setValue("isshowpersonname","${orgMapForm.isshowpersonname}");
   hashvo.setValue("namesinglecell","${orgMapForm.namesinglecell}");
   hashvo.setValue("backdate","${orgMapForm.backdate}");
   hashvo.setValue("isshowposup","${orgMapForm.isshowposup}");
   var in_paramters="sss=child";
   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:getChildsList,functionId:'0405050022'},hashvo);
}
function getChildsList(outparamters)
{
   childslist=outparamters.getValue("childslist"); 
   grades=parseInt(outparamters.getValue("grades"));
} 
function searchPersonList(code,kind,dbname)
{
   var in_paramters="code="+code + "&kind=" + kind + "&dbname=" + dbname;
   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:getPersonList,functionId:'0405050007'});
}
function getPersonList(outparamters)
{
   childslist=outparamters.getValue("personlist"); 
}   
function loadChildNode(evt,parentenodestr,code,kind,rootgrade,catalog_id,constant)
{
   var SvgDocument=evt.getTarget().getOwnerDocument(); 
   var childsnodec=SvgDocument.getElementById(parentenodestr+"c");
     
   var isloadchildren=childsnodec.getAttribute("name");
   if(isloadchildren==null || isloadchildren !=null && isloadchildren.length<=23)
      searchChildsList(code,kind,"${orgMapForm.dbnames}",constant);

   var parameter=new ParameterSet();
   parameter.setValue("cellwidth",${orgMapForm.cellwidth});
   parameter.setValue("cellheight",${orgMapForm.cellheight});
   parameter.setValue("cellletteralignleft","${orgMapForm.cellletteralignleft}");
   parameter.setValue("cellletteralignright","${orgMapForm.cellletteralignright}");
   parameter.setValue("cellletteraligncenter","${orgMapForm.cellletteraligncenter}");
   parameter.setValue("celllettervaligncenter","${orgMapForm.celllettervaligncenter}");
   parameter.setValue("cellletterfitsize","${orgMapForm.cellletterfitsize}");
   parameter.setValue("cellletterfitline","${orgMapForm.cellletterfitline}");
   parameter.setValue("fontfamily","${orgMapForm.fontfamily}");
   parameter.setValue("fontsize","${orgMapForm.fontsize}");
   parameter.setValue("fontcolor","${orgMapForm.fontcolor}");
   parameter.setValue("cellhspacewidth",${orgMapForm.cellhspacewidth});
   parameter.setValue("cellvspacewidth",${orgMapForm.cellvspacewidth});
   parameter.setValue("celllinestrokewidth","${orgMapForm.celllinestrokewidth}");
   parameter.setValue("cellshape","${orgMapForm.cellshape}");
   parameter.setValue("cellcolor","${orgMapForm.cellcolor}");
   parameter.setValue("cellaspect",${orgMapForm.cellaspect});
   parameter.setValue("isshowpersonconut",${orgMapForm.isshowpersonconut});
   parameter.setValue("isshowpersonname",${orgMapForm.isshowpersonname});
   parameter.setValue("namesinglecell",${orgMapForm.namesinglecell});
   parameter.setValue("graph3d",${orgMapForm.graph3d});
   parameter.setValue("graphaspect",${orgMapForm.graphaspect});
   parameter.setValue("rectwidth","10");
   parameter.setValue("dbnames","${orgMapForm.dbnames}");
   rootgrade = rootgrade+1;
   grades=rootgrade; 
   _loadChildNode(evt,parentenodestr,childslist,grades,rootgrade,catalog_id,parameter);
} 
function loadPersonNode(evt,parentenodestr,code,kind,rootgrade,catalog_id,dbname,isupright)
{
   var SvgDocument=evt.getTarget().getOwnerDocument(); 
   var childsnodec=SvgDocument.getElementById(parentenodestr+"c");
   var isloadchildren=childsnodec.getAttribute("name");
     
   if(isloadchildren==null || isloadchildren !=null && isloadchildren.length<=23)
      searchChildsList(code,kind,"${orgMapForm.dbnames}");        
        
   var parameter=new ParameterSet();
   parameter.setValue("cellwidth",${orgMapForm.cellwidth});
   parameter.setValue("cellheight",${orgMapForm.cellheight});
   parameter.setValue("cellletteralignleft","${orgMapForm.cellletteralignleft}");
   parameter.setValue("cellletteralignright","${orgMapForm.cellletteralignright}");
   parameter.setValue("cellletteraligncenter","${orgMapForm.cellletteraligncenter}");
   parameter.setValue("celllettervaligncenter","${orgMapForm.celllettervaligncenter}");
   parameter.setValue("cellletterfitsize","${orgMapForm.cellletterfitsize}");
   parameter.setValue("cellletterfitline","${orgMapForm.cellletterfitline}");
   parameter.setValue("fontfamily","${orgMapForm.fontfamily}");
   parameter.setValue("fontsize","${orgMapForm.fontsize}");
   parameter.setValue("fontcolor","${orgMapForm.fontcolor}");
   parameter.setValue("cellhspacewidth",${orgMapForm.cellhspacewidth});
   parameter.setValue("cellvspacewidth",${orgMapForm.cellvspacewidth});
   parameter.setValue("celllinestrokewidth","${orgMapForm.celllinestrokewidth}");
   parameter.setValue("cellshape","${orgMapForm.cellshape}");
   parameter.setValue("cellcolor","${orgMapForm.cellcolor}");
   parameter.setValue("cellaspect",${orgMapForm.cellaspect});
   parameter.setValue("isshowpersonconut",${orgMapForm.isshowpersonconut});
   parameter.setValue("isshowpersonname",${orgMapForm.isshowpersonname});
   parameter.setValue("namesinglecell",${orgMapForm.namesinglecell});
   parameter.setValue("graph3d",${orgMapForm.graph3d});
   parameter.setValue("graphaspect",${orgMapForm.graphaspect});
   parameter.setValue("rectwidth","10");
   parameter.setValue("dbnames","${orgMapForm.dbnames}");
     
   grades=rootgrade+1;
     //alert(66);
   _loadChildNode(evt,parentenodestr,childslist,grades,rootgrade,catalog_id,parameter);
}  

function setorgoption()
{

   orgMapForm.action="/general/inform/org/map/setorgmapoption.do?b_searchp=link&ishistory=1";
   orgMapForm.submit();
}
function showPDF(outparamters)
{
   var url=outparamters.getValue("url");
   var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+url,"pdf");	
}

function excecutePDFSX()
{
   var hashvo=new ParameterSet();
   hashvo.setValue("constant","${orgMapForm.constant}");
   hashvo.setValue("backdate","${orgMapForm.backdate}");
   var In_paramters="exce=PDF&code=" + "${orgMapForm.code}";     
   var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showPDF,functionId:'0405050027'},hashvo);	
 
}
function viewmess(hreflink)
{
   if(hreflink!=null&&hreflink!="undefined"&&hreflink!="")
      window.open(hreflink,"_blank")
}
function returnQ()
{ 
       orgMapForm.action="/workbench/dutyinfo/editorginfo.do?b_search=link&action=editorginfodata.do&edittype=new&treetype=duty&kind=0&target=mil_body";
       orgMapForm.target="il_body";
       orgMapForm.submit();
}
</SCRIPT>

<html:form action="/pos/posreport/search_report_relations"> 
<html:hidden name="orgMapForm" property="report_relations" />

<table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
  <tr>
    <td align="left"  nowrap>       
         <input type="button" name="returnbutton"  value="<bean:message key="button.orgmapset"/>" class="mybutton" onclick="setorgoption()">
         <input type="button" value="<bean:message key="pos.report.relations.export"/>关系图"  class="mybutton" onclick="excecutePDFSX()">   
       
<%if(bosflag!=null&&(bosflag.equals("hl") || bosflag.equals("hcm"))) {%>
	<logic:equal value="dxt" name="orgMapForm" property="returnvalue">
     <input type="button" name="b_delete" value='<bean:message key="button.return"/>' class="mybutton" onclick="hrbreturn('org','2','orgMapForm');"> 
	</logic:equal>
<%} %>     
    </td>  
 </tr>
 <tr>
   <td>
     <embed  name="svgmap" pluginspage="/thirdparty/SVGView.exe" src="/general/inform/pos/reportrelationsvgmap?kind=<bean:write name="orgMapForm" property="kind" filter="false"/>&code=<bean:write name="orgMapForm" property="code" filter="false"/>&constant=<bean:write name="orgMapForm" property="constant" filter="false"/>"  height="500" width="633" type="image/svg+xml"></embed> 
   </td>
 </tr>
</table>

<!--
<object type="image/svg+xml" data="/general/inform/pos/reportrelationsvgmap?dbname=usr&kind=<bean:write name="orgMapForm" property="kind" filter="false"/>&code=<bean:write name="orgMapForm" property="code" filter="false"/>&historyorg=0" width="16300" height="1000"></object>
-->


</html:form>

   