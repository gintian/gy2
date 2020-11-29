<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.browse.SynthesisBrowseForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="java.util.*"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>

<%
	response.setHeader("Pragma", "No-cache");
 	response.setHeader("Cache-Control", "no-cache");
 	response.setDateHeader("Expires", 0);
%>

<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
%>
<script language='javascript'>
var employarray;
var n=0;
function reLoadTree(dbpre)
{
  synthesisBrowseForm.action="/general/inform/synthesisbrowse.do?b_search=link&dbpre=" + dbpre;
  synthesisBrowseForm.target="il_body";
  synthesisBrowseForm.submit();
}
function reLoadTree2(obj)
{
  var dbpre=obj.value;
  synthesisBrowseForm.action="/general/inform/synthesisbrowse.do?b_search=link&dbpre=" + dbpre;
  synthesisBrowseForm.target="il_body";
  synthesisBrowseForm.submit();
}


function query()
{
   var queryname=$F('queryname');
   var hashvo=new ParameterSet();
   hashvo.setValue("queryname",getEncodeStr(queryname));
   hashvo.setValue("dbpre","${synthesisBrowseForm.dbpre}");
   var request=new Request({method:'post',onSuccess:onsuccess,functionId:'3220300006'},hashvo);
}
function onsuccess(outparamters)
{
   employarray=outparamters.getValue("employlist");
   if(employarray.length>0)
   {
      synthesisBrowseForm.action="/general/inform/synthesisbrowse/synthesisbrowseinfo.do?b_search=link&a_code=" + employarray[0];
      synthesisBrowseForm.target="mil_body";
      synthesisBrowseForm.submit();
      if(employarray.length>1)
      {
        Element.hide('querybutton');
        Element.show('nextbutton'); 
      }     
   }
   else
   {
      alert("没有此人员信息");
   }
 }
 function nextemploy()
 {
     n=n+1;
     if(employarray.length>n)
     {
        synthesisBrowseForm.action="/general/inform/synthesisbrowse/synthesisbrowseinfo.do?b_search=link&a_code=" + employarray[n];
        synthesisBrowseForm.target="mil_body";
        synthesisBrowseForm.submit();
     }
     if(employarray.length==n)
     {
        Element.hide('nextbutton');
        Element.show('querybutton');
        n=0;
     }
 }
 function hidenextbutton()
 {
   Element.hide('nextbutton');
 }
</script>
 <style type="text/css">
    .RecordRow_top {
	border: inset 1px #94B6E6;	
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;

	}	
	
 </style>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<hrms:themes />
<html:form action="/general/inform/synthesisbrowse"> 
<%
  SynthesisBrowseForm synthesisBrowseForm=(SynthesisBrowseForm)session.getAttribute("synthesisBrowseForm");
  ArrayList dblist=synthesisBrowseForm.getDblist();
   String dbpre=synthesisBrowseForm.getDbpre();
%>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" >   
	 <tr>
	           <td width="40" align="right" nowrap="nowrap">
		          人员库
		       </td>
		       <td align="left">
		           <select name="dbpre" size="1" style="width:100;" onchange="reLoadTree2(this);">
		           <%if(dblist!=null && dblist.size()>0)
		           {
	                       for(int i=0;i<dblist.size();i++){
	                         LazyDynaBean dbbean=(LazyDynaBean)dblist.get(i);
	                         String pre=dbbean.get("pre").toString();
	                         request.setAttribute("a_pre",pre);
	                         if(dbpre!=null&&dbpre.equalsIgnoreCase(pre))
	                         {
	                             out.println("<option value=\""+pre+"\" selected=\"selected\">"+dbbean.get("dbname").toString()+"</option>");
	                         }else
	                            out.println("<option value=\""+pre+"\">"+dbbean.get("dbname").toString()+"</option>");
	                         }
	                }%>         
                 </select>          
		      </td>
		</tr>
	    <tr>
		   <td width="40" align="right" >
		        姓&nbsp;&nbsp;&nbsp;名
		      </td>
		      <td align="left" nowrap="nowrap" valign="bottom">
		         <input type="text"  name="queryname" class="text4" size="15">
		         <span style="vertical-align: middle;">
	                 <input type="button" name="querybutton"  value='查 找' class="mybutton" onclick="query()">  
	                 <input type="button" name="nextbutton"  value='下一个' class="mybutton" onclick="nextemploy()"> 
                 </span>
		       </td>
		 </tr>
         <tr>        
           <td align="left" colspan="2" class='recordRow' style="border-left-width: 0px;border-right-width: 0px;border-bottom-width: 0px;"> 
             <!-- 避免滚动条闪烁: divStyle="height:expression(document.body.clientHeight-100);width:expression(document.body.clientWidth);overflow-x: hidden;overflow-y:auto;" -->
             <hrms:orgtree action="/general/inform/synthesisbrowse/synthesisbrowseinfo.do?b_search=link" target="mil_body" flag="1" nmodule="4" priv="1" showroot="false" dbpre="${synthesisBrowseForm.dbpre}" lv="1" />

           </td>
         </tr>            
   </table>
<script language='javascript'>
     hidenextbutton(); 
</script>
</html:form>
