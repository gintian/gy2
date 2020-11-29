<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hjsj.hrms.actionform.selfinfomation.SelfInfoForm"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.List,com.hrms.hjsj.sys.DataDictionary" %>
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
	SelfInfoForm selfInfoForm=(SelfInfoForm)session.getAttribute("selfInfoForm");
	  String inputchinfor = selfInfoForm.getInputchinfor();
	  String approveflag = selfInfoForm.getApproveflag();
	   String a01desc=DataDictionary.getFieldSetVo("A01").getCustomdesc();
%>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<style>
body {  	
	font-size: 12px;
	margin:0 0 0 0;
}
.pading {
	padding-top: 6px;
	padding-top: 3px\9;
}
</style>
<script language="JavaScript" src="/js/function.js"></script>

<script language="JavaScript">
function pf_ChangeFocus(e) 
			{
				  e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
			      var key = window.event?e.keyCode:e.which;
			      var t=e.target?e.target:e.srcElement;
			      if ( key==0xD && t.tagName!='TEXTAREA') /*0xD*/
			      {    
			   		   if(window.event)
			   		   	e.keyCode=9;
			   		   else
			   		   	e.which=9;
			      }
			   //按F5刷新问题,重复提交问题,右键菜单也设法去掉
			   if ( key==116)
			   {
			   		if(window.event){
			   		   	e.keyCode=0;
			   		   	e.returnValue=false;
			   		}else{
			   		   	e.which=0;
			   		   	e.preventDefault();
			   		}
			   }   
			   if ((e.ctrlKey)&&(key==82))//屏蔽 Ctrl+R  
			   {    
			        if(window.event){
			   		   	e.keyCode=0;
			   		   	e.returnValue=false;
			   		}else{
			   		   	e.which=0;
			   		   	e.preventDefault();
			   		}
			   } 
			}

//屏蔽右键,实在没有办法的采用此办法,解决重复提交问题

document.oncontextmenu = function() {return false;} 
//49280 hrms:browseinfolinksort标签中后台拼接的js方法名showDiv是固定的  不知道为什么之前注释掉
function showDiv(obj) 
{ 
linkDiv.style.left=getPosition(obj).x; 
linkDiv.style.top=getPosition(obj).y+obj.offsetHeight; 
linkDiv.style.position="absolute"; 
linkDiv.className="pading"; 
linkDiv.innerHTML="<font color='red' size='1'>*</font>";

linkDiv.style.display='';
} 

function changeSelectMark(obj) 
{ 
linkDiv.style.left=getPosition(obj).x; 
linkDiv.style.top=getPosition(obj).y+obj.offsetHeight; 
linkDiv.style.position="absolute"; 
linkDiv.className="pading"; 
linkDiv.innerHTML="<font color='red' size='1'>*</font>";

linkDiv.style.display=''; 
//linkDiv.onmouseleave=function(){linkDiv.style.display='none'}; 
}

function getPosition(el) 
{ 
for (var lx=0,ly=0;el!=null;lx+=el.offsetLeft,ly+=el.offsetTop,el=el.offsetParent); 
return {x:lx-10,y:ly-20} 
} 
function winhref2(fieldsetid,priv_status)
{
   if(priv_status=="up")
   {
       selfInfoForm.action="/selfservice/selfinfo/upphotoinfo.do?b_query=link&i9999=I9999&actiontype=update&setname=A01";
       selfInfoForm.target="mil_body";
       selfInfoForm.submit();
   }else if(priv_status=="1")
   {
      if(fieldsetid=='A01')
      {
         selfInfoForm.action="/selfservice/selfinfo/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
         selfInfoForm.target="mil_body";
         selfInfoForm.submit();
      }else if(fieldsetid=='A00')
      {
         selfInfoForm.action="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&setprv="+priv_status+"&flag=notself&returnvalue=3&isUserEmploy=0";
         selfInfoForm.target="mil_body";
         selfInfoForm.submit();
      }else
      {
         selfInfoForm.action="/selfservice/selfinfo/searchselfdetailinfo.do?b_search=search&setname="+fieldsetid+"&flag=infoself";
         selfInfoForm.target="mil_body";
         selfInfoForm.submit();
      }
   }else
   {
      if(fieldsetid=='A01')
      {
         selfInfoForm.action="/selfservice/selfinfo/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
         selfInfoForm.target="mil_body";
         selfInfoForm.submit();
      }else if(fieldsetid=='A00')
      {
         selfInfoForm.action="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&setprv="+priv_status+"&flag=notself&returnvalue=3&isUserEmploy=0";
         selfInfoForm.target="mil_body";
         selfInfoForm.submit();
      }else
      {
         selfInfoForm.action="/selfservice/selfinfo/searchselfdetailinfo.do?b_search=search&setname="+fieldsetid+"&flag=infoself";
         selfInfoForm.target="mil_body";
         selfInfoForm.submit();
      }
   }
}

function medias(priv_status) {
		selfInfoForm.action="/workbench/media/searchmediainfolist.do?b_appsearch=link&setname=A00&setprv="+priv_status+"&flag=self&returnvalue=3&isUserEmploy=0&button=0";
        selfInfoForm.target="mil_body";
        selfInfoForm.submit();
}
function winhrefOT(herf,target)
{
   if(herf=="")
      return false;
   selfInfoForm.action=herf;
   selfInfoForm.target=target;
   selfInfoForm.submit();
}
function winhref(herf,target,obj)
{
 var parentWindow=parent.parent.document.getElementsByName("il_body")[0];
 if(parentWindow){
	 var mil_body=parentWindow.contentWindow.document.getElementsByName("mil_body");//[0].contentWindow.dateIsChange();
	 if(mil_body){
		 if( typeof mil_body[0].contentWindow.dataIsChange==='function'){
			var flag=mil_body[0].contentWindow.dataIsChange();
			if(flag){
				var type=confirm("信息已修改请保存数据，否则可能会导致数据丢失。确认跳转？");
				if(!type){
					return ;
				}
			}
		 }
		
	 }
	 
 }
   if(herf=="")
      return false;
   selfInfoForm.action=herf;
   selfInfoForm.target=target;
   selfInfoForm.submit();
   // 49280 hrms:browseinfolinksort标签中后台拼接的js方法名都是固定的 但是参数不一致 导致obj为空
   //changeSelectMark(obj);
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
</script>
<hrms:themes />
<html:form action="/selfservice/selfinfo/addselfinfo"> 
<html:hidden name="selfInfoForm" property="a0100" styleClass="text"/>
<html:hidden name="selfInfoForm" property="userbase" styleClass="text"/>
   <table width="170" border="0" cellspacing="1"  align="left" cellpadding="1">
     <% 
       List infosetlist=(List)selfInfoForm.getInfoSetList();
       if(selfInfoForm.getInfoSetList().size()>0){%>  
          <tr>
           <td align="center">

             <hrms:ole name="selfInfoForm" dbpre="selfInfoForm.userbase" a0100="a0100" scope="session"  href="#PO" onclick="winhrefOT('/selfservice/selfinfo/upphotoinfo.do?b_query=link&i9999=I9999&actiontype=update&setname=A01','mil_body')" height="120" width="85"/>
     
           </td>
          </tr>   
         <%}%>    
      <logic:equal name="selfInfoForm" property="infosort" value="1">
          <tr>                   
                <td>
                     <table width='100%' border='0' cellspacing='1' align='center' cellpadding='1' style="margin-left: 5px" background=''> 
                         <tr> 
                           <td id=tdid align="right" nowrap width="4">	
                           		    
            	           </td>
                          <td nowrap align="left">                           
                           <a href="#A01"  id="01" onclick="winhref('/selfservice/selfinfo/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname=A01','mil_body',this);changeSelectMark(this)">
                          <font styleClass="settext"><%=a01desc %></font>
                           </a>
                         </td>   
                      </tr>
                    </table>
                </td>                
            </tr> 
          <hrms:browseinfolinksort nbase="${selfInfoForm.userbase}" name="" type="self" tag="SET_A" a0100="${selfInfoForm.a0100}" infoSetList="<%=infosetlist%>" />
     </logic:equal>
     <logic:notEqual name="selfInfoForm" property="infosort" value="1">
      <logic:iterate  id="setlist"   name="selfInfoForm"  property="infoSetList"> 
      <bean:define id="pageset" name="setlist" property="fieldsetid"/>
         <tr>
           <td align="left"  nowrap>
           <table>
           <tr>
           <td>
           <logic:equal value="A01" name="setlist" property="fieldsetid">
            
            </logic:equal>
           </td>
           <td>
            <logic:equal name="setlist" property="priv_status" value="1"> 
             <logic:equal name="setlist" property="fieldsetid" value="A01">   
              <a href="#${setlist.fieldsetid}" onclick="winhref2('${setlist.fieldsetid}','${setlist.priv_status}');changeSelectMark(this)"><Font class="LinkRead"><bean:write  name="setlist" property="customdesc"/></font></a>
             </logic:equal>
              <logic:notEqual name="setlist" property="fieldsetid" value="A01"> 
                   <logic:notEqual name="setlist" property="fieldsetid" value="A00">   
                      <a href="#${setlist.fieldsetid}"   onclick="winhref2('${setlist.fieldsetid}','${setlist.priv_status}');changeSelectMark(this)"><Font class="LinkRead"><bean:write  name="setlist" property="customdesc"/></font></a>
                   </logic:notEqual>
                   <logic:equal name="setlist" property="fieldsetid" value="A00">   
                      <a href="#${setlist.fieldsetid}" onclick="winhref2('${setlist.fieldsetid}','${setlist.priv_status}');changeSelectMark(this)"><Font class="LinkRead"><bean:write  name="setlist" property="customdesc"/></font></a>
                   </logic:equal>  
                </logic:notEqual>
            </logic:equal>
            <logic:equal name="setlist" property="priv_status" value="2"> 
               <logic:equal name="setlist" property="fieldsetid" value="A01">   
                 <a href="#${setlist.fieldsetid}" onclick="winhref2('${setlist.fieldsetid}','${setlist.priv_status}');changeSelectMark(this)"><bean:write  name="setlist" property="customdesc"/></a>
               </logic:equal>
               <logic:notEqual name="setlist" property="fieldsetid" value="A01"> 
                   <logic:notEqual name="setlist" property="fieldsetid" value="A00">   
                      <a href="#${setlist.fieldsetid}" onclick="winhref2('${setlist.fieldsetid}','${setlist.priv_status}');changeSelectMark(this)"><bean:write  name="setlist" property="customdesc"/></a>
                   </logic:notEqual>
                   <logic:equal name="setlist" property="fieldsetid" value="A00">  
                  		<%if(inputchinfor.equals("1")&&approveflag.equals("1")) {%>
                  			<a href="#${setlist.fieldsetid}" onclick="medias('${setlist.priv_status}');changeSelectMark(this)"><bean:write  name="setlist" property="customdesc"/></a>
                  		<%}else { %>
                           <a href="#${setlist.fieldsetid}" onclick="winhref2('${setlist.fieldsetid}','${setlist.priv_status}');changeSelectMark(this)"><bean:write  name="setlist" property="customdesc"/></a>
                        <%} %>
			 	</logic:equal>  
                </logic:notEqual>
                
            </logic:equal>
            </td>
            </tr>
            </table>
            </td>
            </tr>
         </logic:iterate>
       </logic:notEqual> 
			
   </table>
    <div id="linkDiv" style="display:none;width:8px;height:8px;border:0px #000000 solid ">
	</div>
<script language="JavaScript">
var obj = document.getElementById("01");
if(obj)
	changeSelectMark(obj);
</script>
</html:form>
