<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hjsj.hrms.actionform.selfinfomation.SelfInfoForm"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.List" %>
<%
     // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	   if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<script language="JavaScript" src="/js/function.js"></script>
<style>
body {  
	/*background-image:url(/images/back1.jpg);*/
	font-size: 12px;
	margin:0 0 0 0;
	scrolling:auto;
}
</style>
<script language="javascript">

function showDiv(obj) 
{ 
var setdiv=document.getElementById('setdiv');
if(setdiv)
{
setdiv.style.display='none';
linkDiv.style.left=getPosition(obj).x; 
linkDiv.style.top=getPosition(obj).y+obj.offsetHeight; 
linkDiv.style.position="absolute"; 
linkDiv.innerHTML="<font color='red' size='1'>*</font>";
linkDiv.style.display=''; 
}

//linkDiv.onmouseleave=function(){linkDiv.style.display='none'}; 
} 
document.oncontextmenu = function(e) {return false;}
function getPosition(el) 
{ 
for (var lx=0,ly=0;el!=null;lx+=el.offsetLeft,ly+=el.offsetTop,el=el.offsetParent); 
return {x:lx-10,y:ly-20} 
} 
function showsub(divId,arrow)
{
   var fObj=document.getElementById(divId);
   var arrowObj=document.getElementById(arrow);
   if(fObj.style.display=="none")
   {
       fObj.style.display="block";
       arrowObj.innerHTML="<img src=/images/rarrow.gif border=0>";
   }else
   {
       fObj.style.display="none";
       arrowObj.innerHTML="<img src=/images/darrow.gif border=0>";
   }
}
function winhref(herf,target)
{
   if(herf=="")
      return false;  
   selfInfoForm.action=herf;
   selfInfoForm.target=target;
   selfInfoForm.submit();
}
function checkDiv()
{
    var obj=document.getElementById('01');   
    if(!obj)
      return false;
    setdiv.style.display='none';
    linkDiv.style.left=getPosition(obj).x;    
    linkDiv.style.top=getPosition(obj).y+obj.offsetHeight;    
    linkDiv.style.position="absolute"; 
    linkDiv.innerHTML="<font color='red' size='1'>*</font>";

linkDiv.style.display=''; 
	
}
</script>
<hrms:themes />
<html:form action="/workbench/info/addinfo/add"> 
    <html:hidden name="selfInfoForm" property="a0100" styleClass="text"/>
   <table width="100%"  border="0" cellspacing="1"  align="center" cellpadding="1" >
      <% SelfInfoForm selfInfoForm=(SelfInfoForm)session.getAttribute("selfInfoForm");
       List infosetlist=(List)selfInfoForm.getInfoSetList();
      if(selfInfoForm.getInfoSetList().size()>0){%>  
         <tr>
         	<!-- 【5292】员工管理-信息维护-快速录入（照片的位置不对）jingq upd 2014.11.26  -->
           <td align="left" style="padding-left:40px;" nowrap>
             &nbsp;
             <logic:notEqual name="selfInfoForm" property="a0100" value="A0100">
             <a href="###" onclick="winhref('/workbench/info/upphotoinfo.do?b_query=link&i9999=I9999&actiontype=update&setname=','mil_body');"> 
             </logic:notEqual>
             <hrms:ole name="selfInfoForm" dbpre="selfInfoForm.userbase" a0100="a0100" scope="session" div="1" height="120" width="85"/>
             <logic:notEqual name="selfInfoForm" property="a0100" value="A0100">
             </a>
             </logic:notEqual>
           </td>
          </tr>    
       <%}%>  
      <logic:equal name="selfInfoForm" property="infosort" value="1">
          <tr>                   
                <td>
                      <table width='100%' border='0' cellspacing='1' align='center' cellpadding='1' background=''> 
                         <tr> 
                           <td align="left" nowrap>		
                           <div id='setdiv' style="display:block;"><font color="red">*</font></div>				    
            	           </td>
                          <td>  &nbsp;                         
                           <a href="#A01" id="01" onclick="winhref('/workbench/info/addinfo/add.do?b_edit=edit&i9999=I9999&actiontype=update&setname=A01','mil_body');showDiv(this);">
                            <font styleClass="settext">${selfInfoForm.a01desc}</font>
                           </a>
                         </td>   
                      </tr>
                    </table>
                </td>                
            </tr> 
             <hrms:browseinfolinksort name="" type="0" tag="SET_A" a0100="${selfInfoForm.a0100}" nbase="${selfInfoForm.userbase}" setprv="${selfInfoForm.setprv}" infoSetList="<%=infosetlist%>" />
            
             
      </logic:equal>
      <logic:notEqual name="selfInfoForm" property="infosort" value="1">
         <logic:iterate  id="setlist"   name="selfInfoForm"  property="infoSetList"> 
         <bean:define id="pageset" name="setlist" property="fieldsetid"/>
         <tr>
           <td align="left"  nowrap>
            <logic:equal name="setlist" property="priv_status" value="1"> 
            <table border="0" cellspacing="1"  cellpadding="1"><tr>
            <td>
            <div id='setdiv' style="display:block;">
            <logic:equal value="${pageset}" name="selfInfoForm" property="setname">
            
             <font color="red">*</font>
           
            </logic:equal>
            </div>&nbsp;
            </td>
            <td>
             <logic:equal name="setlist" property="fieldsetid" value="A01">   
                <a href="#A01"  onclick="winhref('/workbench/info/addinfo/add.do?b_edit=edit&i9999=I9999&actiontype=update&setname=${setlist.fieldsetid}','mil_body');showDiv(this)"><Font class="LinkRead"><bean:write  name="setlist" property="customdesc"/></font></a>
             </logic:equal>
              <logic:notEqual name="setlist" property="fieldsetid" value="A01">   
                  <logic:notEqual name="setlist" property="fieldsetid" value="A00">   
                     <a href="#${setlist.fieldsetid}" onclick="winhref('/workbench/info/addinfo/add.do?b_searchdetail=search&setname=${setlist.fieldsetid}&flag=noself','mil_body');showDiv(this)"><Font class="LinkRead"><bean:write  name="setlist" property="customdesc"/></font></a>
                  </logic:notEqual>
                  <logic:equal name="setlist" property="fieldsetid" value="A00">   
                     <a href="#A00" onclick="winhref('/workbench/media/searchmediainfolist.do?b_search=link&setname=${setlist.fieldsetid}&flag=notself&returnvalue=2','mil_body');showDiv(this)"><Font class="LinkRead"><bean:write  name="setlist" property="customdesc"/></font></a>&nbsp;
                  </logic:equal>
             </logic:notEqual>
              </td>
             </tr>
            </table>
            </logic:equal>
            
            <logic:equal name="setlist" property="priv_status" value="2"> 
            <table border="0" cellspacing="1"  cellpadding="1"><tr>
            <td>
            <div id='setdiv' style="display:block">
            <logic:equal value="${pageset}" name="selfInfoForm" property="setname">
            
             <font color="red">*</font>
           
            </logic:equal></div>&nbsp;&nbsp;
            </td>
            <td>
             <logic:equal name="setlist" property="fieldsetid" value="A01">   
               <a href="#A01" onclick="winhref('/workbench/info/addinfo/add.do?b_edit=edit&i9999=I9999&actiontype=update&setname=${setlist.fieldsetid}','mil_body');showDiv(this)"><bean:write  name="setlist" property="customdesc"/></a>
             </logic:equal>
              <logic:notEqual name="setlist" property="fieldsetid" value="A01">   
                  <logic:notEqual name="setlist" property="fieldsetid" value="A00">   
                     <a href="#${setlist.fieldsetid}" onclick="winhref('/workbench/info/addinfo/add.do?b_searchdetail=search&setname=${setlist.fieldsetid}&flag=noself','mil_body');showDiv(this);"><bean:write  name="setlist" property="customdesc"/></a>
                  </logic:notEqual>
                  <logic:equal name="setlist" property="fieldsetid" value="A00">   
                  <a href="#A00"  onclick="winhref('/workbench/media/searchmediainfolist.do?b_search=link&setname=${setlist.fieldsetid}&setprv=${setlist.priv_status}&flag=notself&returnvalue=3&userbase=<bean:write name="selfInfoForm" property="userbase"/>','mil_body');showDiv(this)"><bean:write  name="setlist" property="customdesc"/></a>&nbsp;
                  </logic:equal>
             </logic:notEqual>
              </td>
             </tr>
            </table>
            </logic:equal>
           
            </td>
            </tr>
         </logic:iterate>
      </logic:notEqual>      
   </table>
  	 <div id="linkDiv" style="display:none;width:8px;height:8px;border:0px #000000 solid ">
	</div>
	 <logic:equal name="selfInfoForm" property="infosort" value="1">
	   <script language="javascript">
	     checkDiv();
	   </script>
	 </logic:equal>
</html:form>

