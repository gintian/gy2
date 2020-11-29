<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<%
    // 鍦ㄦ爣棰樻爮鏄剧ず褰撳墠鐢ㄦ埛鍜屾棩鏈� 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	  	 bosflag=userView.getBosflag(); 
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<!-- script type="text/javascript" src="/js/svg.js"></script-->
<script type="text/javascript">
  /*var childslist;
  var grades;
  var old_rootgrade;
  function searchChildsList(code,kind,dbname)
  {
      var hashvo=new ParameterSet();
      hashvo.setValue("code",code);
      hashvo.setValue("kind",kind);
      hashvo.setValue("dbnames","${orgMapForm.dbnames}");
      hashvo.setValue("isshowpersonconut","${orgMapForm.isshowpersonconut}");
      hashvo.setValue("isshowpersonname","${orgMapForm.isshowpersonname}");
      hashvo.setValue("isshowposname","${orgMapForm.isshowposname}");
      hashvo.setValue("isshowdeptname","${orgMapForm.isshowdeptname}");
      hashvo.setValue("namesinglecell","${orgMapForm.namesinglecell}");
      hashvo.setValue("orgtype","${orgMapForm.orgtype}");
      hashvo.setValue("backdate","${orgMapForm.backdate}");
     var in_paramters="sss=child";
     var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:getChildsList,functionId:'0405050002'},hashvo);
  }
  function getChildsList(outparamters)
  {
    childslist=outparamters.getValue("childslist");    
    var gv=outparamters.getValue("grades");  
    if(!gv)
      gv=old_rootgrade;          
    grades=parseInt(gv);    
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
  function loadChildNode(evt,parentenodestr,code,kind,rootgrade,catalog_id)
  {
     old_rootgrade=rootgrade;
     //var SvgDocument=evt.getTarget().getOwnerDocument();
     var SvgDocument=evt.target.ownerDocument; 
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
     parameter.setValue("fontstyle","${orgMapForm.fontstyle}");
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
     parameter.setValue("isshowposname",${orgMapForm.isshowposname});
     parameter.setValue("namesinglecell",${orgMapForm.namesinglecell});
     parameter.setValue("graph3d",${orgMapForm.graph3d});
     parameter.setValue("graphaspect",${orgMapForm.graphaspect});
     parameter.setValue("rectwidth","10");
     parameter.setValue("dbnames","${orgMapForm.dbnames}");     ;
     grades=grades-rootgrade; 
     _loadChildNode(evt,parentenodestr,childslist,grades,rootgrade,catalog_id,parameter);
  } 
  function loadPersonNode(evt,parentenodestr,code,kind,rootgrade,catalog_id,dbname,isupright)
  {
     var SvgDocument=evt.target.ownerDocument; 
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
     parameter.setValue("fontstyle","${orgMapForm.fontstyle}");
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
     parameter.setValue("isshowposname",${orgMapForm.isshowposname});
     parameter.setValue("namesinglecell",${orgMapForm.namesinglecell});
     parameter.setValue("graph3d",${orgMapForm.graph3d});
     parameter.setValue("graphaspect",${orgMapForm.graphaspect});
     parameter.setValue("rectwidth","10");
     parameter.setValue("dbnames","${orgMapForm.dbnames}");
     
     grades=rootgrade+1;     
     _loadChildNode(evt,parentenodestr,childslist,grades,rootgrade,catalog_id,parameter);
  }  

function viewmess(hreflink)
{
  if(hreflink!=null&&hreflink!="undefined"&&hreflink!="")
    window.open(hreflink,"_blank")
}
*/
function setorgoption()
{
   orgMapForm.action="/general/inform/org/map/setorgmapoption.do?b_search=link&ishistory=0&code=${orgMapForm.code}&kind=${orgMapForm.kind}&orgtype=${orgMapForm.orgtype}&backdate=${orgMapForm.backdate}";
   orgMapForm.submit();
}
function showPDF(outparamters)
{
    var url=outparamters.getValue("url");
//  var win=open("/servlet/DisplayOleContent?filename="+url,"pdf");	
    //20/3/17 xus vfs改造
    var win=open("/servlet/vfsservlet?fileid="+url+"&fromjavafolder=true","pdf");
}

function excecutePDF()
{
    var In_paramters="exce=PDF&kind=" + "${orgMapForm.kind}&code=" + "${orgMapForm.code}&orgtype=${orgMapForm.orgtype}&backdate=${orgMapForm.backdate}";  
    var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showPDF,functionId:'0405050012'});	
}

function excecuteEXCEL()
{
	var In_paramters="exce=PDF&kind=" + "${orgMapForm.kind}&code=" + "${orgMapForm.code}&orgtype=${orgMapForm.orgtype}&backdate=${orgMapForm.backdate}";
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showEXCEL,functionId:'0405050017'});
}
function showEXCEL(outparamters)
{
	var url=outparamters.getValue("url");
	//var name=url.substring(0,url.length-1)+".xls";
//  var win=open("/servlet/DisplayOleContent?filename="+url,"excel");
    //20/3/17 xus vfs改造
    var win=open("/servlet/vfsservlet?fileid="+url+"&fromjavafolder=true","excel");
}
</script>

<html:form action="/general/inform/org/map/showorgmap"> 
<html:hidden name="orgMapForm" property="report_relations" />
<table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
  <tr>
    <td align="left"  nowrap valign="middle" style="padding-top: 5px;">       
         <input type="button" name="returnbutton"  value="<bean:message key="button.orgmapset"/>" class="mybutton" onclick="setorgoption()">
         <input type="button" value="<bean:message key="button.createorgmap"/>"  class="mybutton" onclick="excecutePDF()">          
    	 <input type="button" value="<bean:message key="goabroad.collect.educe.excel"/>" class="mybutton" onclick="excecuteEXCEL()">
    	 <logic:equal name="orgMapForm" property="returnvalue" value="leaderdxt">
    	   <input type="button" name="returnemp" class="mybutton" value="<bean:message key="button.return"/>" onclick="hrbreturn('leader','il_body','orgMapForm');"/>
    	 </logic:equal>
    	 <logic:notEqual name="orgMapForm" property="returnvalue" value="leaderdxt">
           <hrms:tipwizardbutton flag="org" target="il_body" formname="orgMapForm"/>  
         </logic:notEqual>           
    </td>  
 </tr>
 <tr>
   <td>
   	 <html:hidden name="orgMapForm" property="dbnames" styleId="dbnames"/>
   	 <html:hidden name="orgMapForm" property="isshowpersonconut" styleId="isshowpersonconut"/>
   	 <html:hidden name="orgMapForm" property="isshoworgconut" styleId="isshoworgconut"/>
   	 <html:hidden name="orgMapForm" property="isshowpersonname" styleId="isshowpersonname"/>
   	 <html:hidden name="orgMapForm" property="isshowposname" styleId="isshowposname"/>
   	 <html:hidden name="orgMapForm" property="isshowdeptname" styleId="isshowdeptname"/>
   	 <html:hidden name="orgMapForm" property="namesinglecell" styleId="namesinglecell"/>
   	 <html:hidden name="orgMapForm" property="orgtype" styleId="orgtype"/>
   	 <html:hidden name="orgMapForm" property="backdate" styleId="backdate"/>
   	 <html:hidden name="orgMapForm" property="cellwidth" styleId="cellwidth"/>
   	 <html:hidden name="orgMapForm" property="cellheight" styleId="cellheight"/>
   	 <html:hidden name="orgMapForm" property="cellletteralignleft" styleId="cellletteralignleft"/>
   	 <html:hidden name="orgMapForm" property="cellletteralignright" styleId="cellletteralignright"/>
   	 <html:hidden name="orgMapForm" property="cellletteraligncenter" styleId="cellletteraligncenter"/>
   	 <html:hidden name="orgMapForm" property="celllettervaligncenter" styleId="celllettervaligncenter"/>
   	 <html:hidden name="orgMapForm" property="cellletterfitsize" styleId="cellletterfitsize"/>
   	 <html:hidden name="orgMapForm" property="cellletterfitline" styleId="cellletterfitline"/>
   	 <html:hidden name="orgMapForm" property="fontfamily" styleId="fontfamily"/>
   	 <html:hidden name="orgMapForm" property="fontstyle" styleId="fontstyle"/>
   	 <html:hidden name="orgMapForm" property="fontsize" styleId="fontsize"/>
   	 <html:hidden name="orgMapForm" property="fontcolor" styleId="fontcolor"/>
   	 <html:hidden name="orgMapForm" property="cellhspacewidth" styleId="cellhspacewidth"/>
   	 <html:hidden name="orgMapForm" property="cellvspacewidth" styleId="cellvspacewidth"/>
   	 <html:hidden name="orgMapForm" property="celllinestrokewidth" styleId="celllinestrokewidth"/>
   	 <html:hidden name="orgMapForm" property="cellshape" styleId="cellshape"/>
   	 <html:hidden name="orgMapForm" property="cellcolor" styleId="cellcolor"/>
   	 <html:hidden name="orgMapForm" property="cellaspect" styleId="cellaspect"/>
   	 <html:hidden name="orgMapForm" property="graph3d" styleId="graph3d"/>
   	 <html:hidden name="orgMapForm" property="graphaspect" styleId="graphaspect"/>
   	 <html:hidden name="orgMapForm" property="code" styleId="code"/>
   	 <html:hidden name="orgMapForm" property="kind" styleId="kind"/>
     <embed wmode="transparent" name="svgmap" id="svgmap" pluginspage="" src="/general/inform/org/getsvgmap?kind=<bean:write name="orgMapForm" property="kind" filter="false"/>&code=<bean:write name="orgMapForm" property="code" filter="false"/>&orgtype=${orgMapForm.orgtype}&backdate=${orgMapForm.backdate}" height="450" width="750" type="image/svg+xml"></embed>
     <!-- 
    <iframe name="svgmap" src="/general/inform/org/getsvgmap?kind=<bean:write name="orgMapForm" property="kind" filter="false"/>&code=<bean:write name="orgMapForm" property="code" filter="false"/>&orgtype=${orgMapForm.orgtype}&backdate=${orgMapForm.backdate}" height="500" width="750"></iframe>
   	-->
   </td> 
 </tr>
</table>

<!--
<object type="image/svg+xml" data="/general/inform/org/getsvgmap?dbname=usr&kind=<bean:write name="orgMapForm" property="kind" filter="false"/>&code=<bean:write name="orgMapForm" property="code" filter="false"/>&historyorg=0" width="16300" height="1000"></object>
-->


</html:form>
<a id="aid" href="" target="_blank"></a>
