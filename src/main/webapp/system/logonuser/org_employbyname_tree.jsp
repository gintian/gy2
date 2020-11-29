<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
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
	String showDb="0";
	if(request.getParameter("showDb")!=null)
		showDb=request.getParameter("showDb");
		
	String showSelfNode="0";
	if(request.getParameter("showSelfNode")!=null)
		showSelfNode=request.getParameter("showSelfNode");		
%>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<script type="text/javascript">
<!--

function getemploy2()
	{
	 var thevo=new Object();
		
	 thevo.content=root.getSelected();

	 thevo.title=root.getSelectedTitle();
	 if(parent && parent.opener && parent.opener.openReturnValue){
	 	parent.opener.openReturnValue(thevo);
	 	parent.window.close();
	 }else{
		 parent.window.returnValue=thevo;
		 parent.window.close();
	 }

	}
	function getemploy()
	{
  var content="";
  var title="";
  var sel_box=document.getElementById("a_box");
  if(sel_box)
  {
     for(var i=0;i<sel_box.options.length;i++)
     {
        if(sel_box.options[i].selected)
        {
              title+=sel_box.options[i].text;
              content+=sel_box.options[i].value;
        }
     }
  }
	 var thevo=new Object();	
	 thevo.content=content;
	 thevo.title=title;
     //alert(thevo.content+thevo.title)
	 if(parent && parent.opener && parent.opener.openReturnValue){
	 	parent.opener.openReturnValue(thevo);
	 	parent.window.close();
	 }else{
		 parent.window.returnValue=thevo;
		 parent.window.close();
	 }

	}
	Global.defaultradiolevel = 3;//choice user
	function showSelectBox(obj)
   {
   	  var srcobj = document.getElementById(obj);
      Element.show('a0101_pnl');   
      var pos=getAbsPosition(srcobj);
	  with($('a0101_pnl'))
	  {
	        style.position="absolute";
    		style.posLeft=pos[0]-1;
 		    style.posTop=pos[1]-1+srcobj.offsetHeight;
		    style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
      }                 
 } 
function showA0101(outparamters)
{
		showSelectBox('a0101');
		var objlist=outparamters.getValue("objlist");
        var maxlength=outparamters.getValue("maxlength");
        var a_box=document.getElementById("a_box");
        if(maxlength*1>320)
          alert(maxlength);
        if(a_box)
        {  
            if(maxlength*1<=0||maxlength*1<320)
                a_box.style.width=320;
            else
                a_box.style.width=maxlength*1+50;
        }
		if(objlist!=null)
		  AjaxBind.bind($('a_box'),objlist);		
}
      
function query()
{
   	 var a0101=$F('a0101');
   	 if(a0101==""){
   	 	Element.hide('a0101_pnl');
   	 	return false;
   	 }
   	 var hashvo=new ParameterSet();	
     hashvo.setValue("a0101",a0101);
     hashvo.setValue("isVisibleUM","1");
     hashvo.setValue("isVisibleK","1");	
     hashvo.setValue("isPriv","${logonUserForm.priv}");		
     hashvo.setValue("dbtype","${logonUserForm.dbtype}");
     var request=new Request({asynchronous:false,onSuccess:showA0101,functionId:'0570010128'},hashvo); 
}
function setSelectValue()
{
  var sel_box=document.getElementById("a_box");
  if(sel_box)
  {
     for(var i=0;i<sel_box.options.length;i++)
     {
        if(sel_box.options[i].selected)
        {
           //title+=sel_box.options[i].text.substring(sel_box.options[i].text.indexOf("(")+1,sel_box.options[i].text.indexOf(")"))+",";
           //content+=sel_box.options[i].value+",";
           document.getElementById("a0101").value=sel_box.options[i].text;
           break;
        }
     }
  }
  Element.hide('a0101_pnl');
}
function winclose(){
	if(parent && parent.opener && parent.opener.openReturnValue)
		parent.window.close();
	else
		window.close();
}
//-->
</script>
   <style type="text/css">
	#treemenu {  
	height: 300px;
	overflow:auto;
    border-style:solid; 
    border-width:1px;
    width:100%;
	}
   </style>
   <hrms:themes></hrms:themes>
<html:form action="/system/logonuser/org_employbyname_tree"> 
   <table width="290" border="0" cellspacing="0"  align="center" >   
	 <tr align="left">
		<td valign="top" colspan="2">
		姓名&nbsp;<input type="text" name="a0101" value="" class="editor" style="width: expression(document.body.clientWidth-42);font-size:10pt;text-align:left" id="a0101" onkeyup='query();'/>
		</td>
	 </tr>          
         <tr>
        
           <td align="left"> 

                 <hrms:orgtree flag="${logonUserForm.flag}"  showDb="<%=showDb%>" showSelfNode="<%=showSelfNode%>"  loadtype="${logonUserForm.loadtype}" showroot="false" selecttype="${logonUserForm.selecttype}" dbtype="${logonUserForm.dbtype}" priv="${logonUserForm.priv}" isfilter="${logonUserForm.isfilter}"/>			           
           </td>
         </tr>   
         <tr>
            <td align="center" colspan="2" height="35px;">
         	<html:button styleClass="mybutton" property="b_save" onclick="getemploy2();">
            		<bean:message key="button.ok"/>
	 	    </html:button>
         	<html:button styleClass="mybutton" property="br_return" onclick="winclose();">
            		<bean:message key="button.close"/>
	 	    </html:button>            
            </td>         
         </tr>  
               
   </table>
    <div id="a0101_pnl" style="border-style:nono">
  	<select name="a0101_box" onblur="Element.hide('a0101_pnl');" id="a_box" size="10" class="dropdown_frame" style="width:350px" ondblclick='getemploy();'>    
    </select>
  </div>  
</html:form>
<script type="text/javascript">
Element.hide('a0101_pnl');
</script>