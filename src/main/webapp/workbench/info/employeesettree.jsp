<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hjsj.hrms.actionform.selfinfomation.SelfInfoForm"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.hjsj.sys.DataDictionary,com.hrms.hjsj.sys.FieldSet"%>
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
<style>
body {  
	/*background-image:url(/images/back1.jpg);*/
	font-size: 12px;
	margin:0 0 0 0;
}
</style>
<script language="JavaScript" src="/js/function.js"></script>

<script language="javascript">
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
function winhref(herf,target)
{
   if(herf=="")
      return false;      
   selfInfoForm.action=herf;
   selfInfoForm.target=target;
   selfInfoForm.submit();
}
function turn()
{
  parent.menuc.toggleCollapse(false);
} 
</script>
<hrms:themes />
<html:form action="/workbench/info/addselfinfo">
     <html:hidden name="selfInfoForm" property="a0100" styleClass="text"/>
	<table width="100%" border="0" cellspacing="1" align="center" cellpadding="1" >
		<% SelfInfoForm selfInfoForm=(SelfInfoForm)session.getAttribute("selfInfoForm");
		List infosetlist=(List)selfInfoForm.getInfoSetList();
		FieldSet set = DataDictionary.getFieldSetVo("A01");
      if(selfInfoForm.getInfoSetList().size()>0){%>
		<tr>
			<!-- 5107：员工管理-记录录入-新增（录入界面照片的位置有问题） jingq upd 2014.11.20 -->
			<td align="left" style="padding-left:40px;">
				&nbsp;
				<logic:notEqual name="selfInfoForm" property="a0100" value="A0100">
				<a href="###" style="cursor: hand" onclick="winhref('/workbench/info/upphotoinfo.do?b_query=link&i9999=I9999&actiontype=update&setname=','mil_body');"> 
				</logic:notEqual>
				<hrms:ole ids="photo" name="selfInfoForm"  dbpre="selfInfoForm.userbase" a0100="a0100"   scope="session" height="120" width="85" />
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
                           <td align="right" nowrap width="4">		
                           <div id='setdiv' style="display:block;"><font color="red">*</font></div>				    
            	           </td>
                          <td nowrap align="left"> &nbsp;                          
                           <a href="#A01"  id="01" onclick="winhref('/workbench/info/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname=A01&infosort=1','mil_body');showDiv(this);">
                          <font styleClass="settext"><%=set!=null?set.getCustomdesc():"人员基本情况" %></font>
                           </a>
                         </td>   
                      </tr>
                    </table>
                </td>                
            </tr> 
          <hrms:browseinfolinksort nbase="${selfInfoForm.userbase}" name="" type="2" tag="SET_A" a0100="${selfInfoForm.a0100}" infoSetList="<%=infosetlist%>" returnvalue="${selfInfoForm.returnvalue}" />
     </logic:equal>     
     <logic:notEqual name="selfInfoForm" property="infosort" value="1">
		<logic:iterate id="setlist" name="selfInfoForm" property="infoSetList">
			<bean:define id="pageset" name="setlist" property="fieldsetid" />
			<tr>
				<td align="left" nowrap>
					

					<table>
						<tr>
							<td>
							   	<logic:equal value="${pageset}" name="selfInfoForm" property="setname">
								   <div id='setdiv' style="display:block;">	
                                     <font color="red">*</font>
                                    </div>
								</logic:equal>
								
								&nbsp;
							</td>
							<td>
								<logic:equal name="setlist" property="priv_status" value="1">
									<logic:equal name="setlist" property="fieldsetid" value="A01">
										<a href="#A01"  name="seta01" onclick="winhref('/workbench/info/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname=${setlist.fieldsetid}','mil_body');showDiv(this)">
										<Font class="LinkRead"><bean:write	name="setlist" property="customdesc" /></font></a>
									</logic:equal>
									<logic:notEqual name="setlist" property="fieldsetid" value="A01">
										<logic:notEqual name="setlist" property="fieldsetid" value="A00">
											<a href="#${setlist.fieldsetid}" onclick="winhref('/workbench/info/searchselfdetailinfo.do?b_searchsort=search&setname=${setlist.fieldsetid}&flag=noself','mil_body');showDiv(this)"><Font class="LinkRead"><bean:write name="setlist"
														property="customdesc" /></font></a>
										</logic:notEqual>
										<logic:equal name="setlist" property="fieldsetid" value="A00">
											<logic:equal name="selfInfoForm" property="writeable" value="1">
												<a href="#A00" onclick="winhref('/workbench/media/searchmediainfolist.do?b_search=link&setname=${setlist.fieldsetid}&setprv=1&flag=notself&returnvalue=5','mil_body');showDiv(this)"><Font class="LinkRead"><bean:write name="setlist"
														property="customdesc" /></font></a>&nbsp;
											</logic:equal>
											<logic:notEqual name="selfInfoForm" property="writeable" value="1">
												<a href="#A00" onclick="winhref('/workbench/media/searchmediainfolist.do?b_search=link&setname=${setlist.fieldsetid}&setprv=1&flag=notself&returnvalue=2','mil_body');showDiv(this)"><Font class="LinkRead"><bean:write name="setlist"
														property="customdesc" /></font></a>&nbsp;
											</logic:notEqual>
                  						</logic:equal>
									</logic:notEqual>
								</logic:equal>
								<logic:equal name="setlist" property="priv_status" value="2">
									<logic:equal name="setlist" property="fieldsetid" value="A01">
										<a href="#A01" name="seta01" onclick="winhref('/workbench/info/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname=${setlist.fieldsetid}','mil_body');showDiv(this)"><bean:write name="setlist" property="customdesc" /></a>
									</logic:equal>
									<logic:notEqual name="setlist" property="fieldsetid" value="A01">
										<logic:notEqual name="setlist" property="fieldsetid" value="A00">
											<a href="#${setlist.fieldsetid}" onclick="winhref('/workbench/info/searchselfdetailinfo.do?b_searchsort=search&setname=${setlist.fieldsetid}&flag=noself','mil_body');showDiv(this)"><bean:write name="setlist" property="customdesc" /></a>
										</logic:notEqual>
										<logic:equal name="setlist" property="fieldsetid" value="A00">
											<a href="#A00" 
												onclick="winhref('/workbench/media/searchmediainfolist.do?b_search=link&isUserEmploy=1&setname=${setlist.fieldsetid}&setprv=2&flag=notself&returnvalue=${selfInfoForm.returnvalue}&userbase=<bean:write name="selfInfoForm" property="userbase"/>','mil_body');showDiv(this)"><bean:write name="setlist" property="customdesc" /></a>&nbsp;
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
	 <logic:equal name="selfInfoForm" property="infosort" value="1">
	   <script language="javascript">
	     checkDiv();
	   </script>
	 </logic:equal>
</html:form>
