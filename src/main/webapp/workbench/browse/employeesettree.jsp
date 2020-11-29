<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hjsj.hrms.actionform.browse.BrowseForm"%>
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
	BrowseForm browseForm=(BrowseForm)session.getAttribute("browseForm");
	  String inputchinfor = browseForm.getInputchinfor();
	  String approveflag = browseForm.getApproveflag();
%>

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<script src="../../js/validate.js"></script>
<style>
body {  
	/*background-image:url(/images/back1.jpg);*/
	font-size: 12px;
	margin:0 0 0 0;
}
.pading {
	padding-top: 6px;
	padding-top: 3px\9;
}
</style>
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
setdiv.style.display='none';
linkDiv.style.left=getPosition(obj).x; 
linkDiv.style.top=getPosition(obj).y+obj.offsetHeight; 
linkDiv.style.position="absolute"; 
linkDiv.innerHTML="<font color='red' size='1'>*</font>";

linkDiv.style.display=''; 
//linkDiv.onmouseleave=function(){linkDiv.style.display='none'}; 
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
    linkDiv.className="pading"; 
    linkDiv.innerHTML="<font color='red' size='1'>*</font>";

linkDiv.style.display=''; 
	
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
function winhref(herf,target)
{
   browseForm.action=herf;
   browseForm.target=target;
   browseForm.submit();
}
function medias(priv_status) {
		browseForm.action="/workbench/media/searchmediainfolist.do?b_appsearch=link&setname=A00&setprv="+priv_status+"&flag=notself&returnvalue=3&isUserEmploy=0&button=1&userbase=<bean:write name='browseForm' property='userbase'/>&";
        browseForm.target="mil_body";
        browseForm.submit();
}

function multimediahref(dbname,a0100){
	var thecodeurl =""; 
	var return_vo=null;
	var setname = "A01";
	var dw=800,dh=500,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	thecodeurl="/general/inform/multimedia/multimedia_tree.do?b_query=link&setid="+setname+"&a0100="+a0100+"&nbase="+dbname+"&dbflag=A&canedit=false";
	if(getBrowseVersion()){
	  	return_vo= window.showModalDialog(thecodeurl, "", 
	  	"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:800px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no");
	}else{
		window.open(thecodeurl,"","width="+dw+",height="+dh+",resizable=no,scrollbars=no,status=no,left="+dl+",top="+dt);
	}
}
</script>
<%

//pageContext.setAttribute("parammap", taskmap);
%>
<hrms:themes />
<html:form action="/workbench/browse/showselfinfo">
 <html:hidden name="browseForm" property="a0100" styleClass="text"/>
	<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" >

		<% 
		List infosetlist=(List)browseForm.getInfosetlist();
	    int ii=0;
        if(browseForm.getInfosetlist()!=null && browseForm.getInfosetlist().size()>0){%>
		<tr>
			<td align="left" width="100%" nowrap>
			<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0">
			   <tr>
			     <td>
			       &nbsp;
			     </td>
			     <td align="left" style="padding-left:40px;"><br>
			       <a href="###">
				<hrms:ole name="browseForm" dbpre="browseForm.userbase" a0100="a0100" scope="session" height="120" width="85" />
			    </a>
			     </td>
			   </tr>
			</table>
				
			</td>
		</tr>
		<%}%>		
        <logic:equal name="browseForm" property="infosort" value="1">
            <tr>                   
                <td>
                      <table width='100%' border='0' cellspacing='1' align='center' cellpadding='1' background=''> 
                         <tr> 
                           <td align="right" width="5px"  nowrap>	
                           <div id='setdiv' style="display:block;"><font color="red">*</font></div>				    
            	           </td>
                          <td  nowrap>
                            &nbsp; <a href="###" id="01" onclick="winhref('/workbench/browse/browseinfo.do?b_search=link&setname=A01&infosort=1','mil_body');showDiv(this);"><font styleClass="settext">${browseForm.a01desc}</font>
                            <logic:equal name="browseForm" property="multimedia_file_flag" value="1">
                            &nbsp;&nbsp;<hrms:browseaffix pertain_to="record" onlyImg="0" a0100="${browseForm.a0100}" nbase="${browseForm.userbase}" setId="A01"></hrms:browseaffix>
                            </logic:equal>	
                             </a>
                            
                         </td>   
                      </tr>
                    </table>
                </td>                
            </tr>             
            <hrms:browseinfolinksort name="" nbase="${browseForm.userbase}" type="1" returnvalue="${browseForm.returnvalue}" tag="SET_A" a0100="${browseForm.a0100}" infoSetList="<%=infosetlist%>" />
            <logic:equal name="browseForm" property="type" value="1">
			 <hrms:priv func_id="0101010">  		
			 <tr>
			  <td align='left' nowrap>
				<table width='100%' border='0' cellspacing='0'  cellpadding='0'>	
				<tr>
					<td>&nbsp;</td>
					<td><a href="#DZ" onclick="winhref('/general/inform/emp/e_archive/e_archive_list.do?b_init=init&userbase=<bean:write name="browseForm" property="userbase"/>','mil_body');showDiv(this);">&nbsp;电子档案&nbsp;</a></td>
				</tr>
				</table>
			  </td>
			</tr>
			</hrms:priv>
			</logic:equal>
			<logic:equal name="browseForm" property="type" value="2">
			 <hrms:priv func_id="0301010"> 
			  <tr>
			  <td align='left' nowrap>
				<table width='100%' border='0' cellspacing='0'  cellpadding='0'>				  
				  <tr>
					<td>&nbsp;</td>
					<td><a href="#DZ" onclick="winhref('/general/inform/emp/e_archive/e_archive_list.do?b_init=init&userbase=<bean:write name="browseForm" property="userbase"/>','mil_body');showDiv(this);">&nbsp;电子档案&nbsp;</a></td>
				 </tr>
			   </table>
			 </td>
			</tr>
			</hrms:priv>
			</logic:equal>
        </logic:equal>        
        <logic:notEqual name="browseForm" property="infosort" value="1">
          <logic:iterate id="setlist" name="browseForm" property="infosetlist">
			<bean:define id="pageset" name="setlist" property="fieldsetid" />
			<tr>
			<td align="left" nowrap>
				<table>
					<tr>
						<td align="right" width="5px"  nowrap>	
							<logic:equal value="A01" name="setlist" property="fieldsetid">
            				<div id='setdiv' style="display:block;"><font color="red">*</font></div>
            				</logic:equal>
						</td>
						<td>
						<logic:equal name="setlist" property="fieldsetid" value="A01">
							&nbsp; <a href="#A01" onclick="winhref('/workbench/browse/browseinfo.do?b_search=link&setname=${setlist.fieldsetid}','mil_body');showDiv(this);"><font styleClass="settext"> <bean:write name="setlist" property="customdesc" /></font></a>
							<logic:equal name="browseForm" property="multimedia_file_flag" value="1">
                            &nbsp;&nbsp;<hrms:browseaffix pertain_to="record" onlyImg="0" a0100="${browseForm.a0100}" nbase="${browseForm.userbase}" setId="A01"></hrms:browseaffix>
                            </logic:equal>	
						</logic:equal>
						<logic:notEqual name="setlist" property="fieldsetid" value="A01">
							<logic:notEqual name="setlist" property="fieldsetid" value="A00">
									
										&nbsp; <a href="#${setlist.fieldsetid}" onclick="winhref('/workbench/browse/showselfinfodetail.do?b_search=link&setname=${setlist.fieldsetid}','mil_body');showDiv(this);"><font styleClass="settext"> <bean:write name="setlist" property="customdesc" /></font></a>
									
							</logic:notEqual>
							<logic:equal name="setlist" property="fieldsetid" value="A00">
									<%if(inputchinfor.equals("1")&&approveflag.equals("1")&&"1".equals(browseForm.getReturnvalue())) {%>
                  			&nbsp; <a href="#${setlist.fieldsetid}" onclick="medias('${setlist.priv_status}');showDiv(this)"><bean:write  name="setlist" property="customdesc"/></a>
                  		<%}else { %>
										&nbsp; <a href="#A00" onclick="winhref('/workbench/browse/showmediainfodetail.do?b_search=link&setname=A00','mil_body');showDiv(this);"><font styleClass="settext"> <bean:write name="setlist" property="customdesc" /></font></a>
								<%} %>	
							</logic:equal>
						</logic:notEqual>
						</td>
				    </tr>
			     </table>
			</td>
			</tr>
			</logic:iterate>
			<logic:equal name="browseForm" property="type" value="1">
			 <hrms:priv func_id="0101010">  		
			 <tr>
			  <td align="left" nowrap>
				<table border="0">	
				<tr>
					<td>&nbsp;</td>
					<td><a href="#DZ" onclick="winhref('/general/inform/emp/e_archive/e_archive_list.do?b_init=init&userbase=<bean:write name="browseForm" property="userbase"/>','mil_body');showDiv(this);">&nbsp;电子档案&nbsp;</a></td>
				</tr>
				</table>
			  </td>
			</tr>
			</hrms:priv>
			</logic:equal>
			<logic:equal name="browseForm" property="type" value="2">
			 <hrms:priv func_id="0301010"> 
			  <tr>
			  <td align="left" nowrap>
				<table border="0">				  
				  <tr>
					<td>&nbsp;</td>
					<td><a href="#DZ" onclick="winhref('/general/inform/emp/e_archive/e_archive_list.do?b_init=init&userbase=<bean:write name="browseForm" property="userbase"/>','mil_body');showDiv(this);">&nbsp;电子档案&nbsp;</a></td>
				 </tr>
			   </table>
			 </td>
			</tr>
			</hrms:priv>
			</logic:equal>
        </logic:notEqual>
		
			


	</table>


  	<div id="linkDiv" style="display:none;width:8px;height:8px;border:0px #000000 solid ">
	</div>
	 <logic:equal name="browseForm" property="infosort" value="1">
	   <script language="javascript">
	     checkDiv();
	   </script>
	 </logic:equal>
</html:form>
