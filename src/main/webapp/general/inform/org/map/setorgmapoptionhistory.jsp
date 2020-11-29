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
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<script language="javascript">
	function aligncheck(name)
	{
	    var no_selectl;
	    if(name=="left")
	    {
	       var l_vos=document.getElementsByName("cellletteralignright"); 
	       if(l_vos!=null)
	       {
	           no_selectl=l_vos[0];
	           no_selectl.checked=false;
	       }
	       var r_vos=document.getElementsByName("cellletteraligncenter");   
	       if(r_vos!=null)
	       {
	           no_selectl=r_vos[0];
	           no_selectl.checked=false;
	       }
	    }else if(name=="right")
	    {
	       var l_vos=document.getElementsByName("cellletteralignleft");   
	       if(l_vos!=null)
	       {
	           no_selectl=l_vos[0];
	           no_selectl.checked=false;
	       }
	       var r_vos=document.getElementsByName("cellletteraligncenter");   
	       if(r_vos!=null)
	       {
	           no_selectl=r_vos[0];
	           no_selectl.checked=false;
	       }
	    }else if(name=="center")
	    {
	       var l_vos=document.getElementsByName("cellletteralignleft");   
	       if(l_vos!=null)
	       {
	           no_selectl=l_vos[0];
	           no_selectl.checked=false;
	       }
	       var l_vos=document.getElementsByName("cellletteralignright"); 
	       if(l_vos!=null)
	       {
	           no_selectl=l_vos[0];
	           no_selectl.checked=false;
	       }
	    }
	}
function checkText()
{
   //cellhspacewidth','左右间距','F','cellvspacewidth','上下间距','F','celllinestrokewidth','线宽','F','cellwidth','单元格宽','F','cellheight','单元格高'
   var l_vos=document.getElementsByName("cellhspacewidth"); 
   var vo;
   var re=true;
   if(l_vos!=null)
   {
	   vo=l_vos[0];
	   re=checkText2(vo,"左右间距");
	   if(!re)
	     return false;
   }
   l_vos=document.getElementsByName("cellvspacewidth"); 
   if(l_vos!=null)
   {
	   vo=l_vos[0];
	   re=checkText2(vo,"上下间距");
	   if(!re)
	     return false;
   }
   l_vos=document.getElementsByName("celllinestrokewidth");
   if(l_vos!=null)
   {
	   vo=l_vos[0];
	   re=checkText2(vo,"线宽");
	   if(!re)
	     return false;
   } 
   l_vos=document.getElementsByName("cellwidth"); 
   if(l_vos!=null)
   {
	   vo=l_vos[0];
	   re=checkText2(vo,"单元格宽");
	   if(!re)
	     return false;
   }
   l_vos=document.getElementsByName("cellheight"); 
   if(l_vos!=null)
   {
	   vo=l_vos[0];
	   re=checkText2(vo,"单元格高");
	   if(!re)
	     return false;
   }
   return re;
}
function checkText2(obj,name)
{
   if(obj.value=="")
   {
      alert(name+"不能为空！");
      return false;
   }else
   {
     var theNum=obj.value;
     var oneNum;
     for(var i=0;i<theNum.length;i++){
      oneNum=theNum.substring(i,i+1);
      if (oneNum<"0" || oneNum>"9")
      {
         alert(name+"必须为数字！");
         return false;
      }
     }
     return true;
   }
}
function save()
{
  if(checkText())
  {
     if(ifqrbc())
	 {
	        orgMapForm.action="/general/inform/org/map/searchhistoryorgmapset.do?b_save=link&catalog_id=${orgMapForm.catalog_id}&&code=${orgMapForm.code}&kind=${orgMapForm.kind}";
   	        orgMapForm.submit();
	 }
  }
	 
}
function returnr()
{
	 orgMapForm.action="/general/inform/org/map/searchhistoryorgmapset.do?b_return=link&catalog_id=${orgMapForm.catalog_id}&&code=${orgMapForm.code}&kind=${orgMapForm.kind}";
   	 orgMapForm.submit();
}
 function IsDigit() 
  { 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
  } 
</script>
<SCRIPT LANGUAGE=javascript src="/js/color.js"></SCRIPT> 
<div style="z-index:1">
<html:form action="/general/inform/org/map/searchhistoryorgmapset"> 
      <br>
      <br>
      <fieldset align="center" style="width:90%;">
         <legend ><bean:message key="general.inform.org.graph"/></legend>
         <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
           <tr>
              <td align="left"  nowrap>
                 <html:checkbox name="orgMapForm" property="graph3d" value="true"><bean:message key="general.inform.org.graph3D"/></html:checkbox> 
               </td>
           </tr>   
           <tr>
               <td align="left"  nowrap>
               <logic:equal name="orgMapForm" property="graphaspect" value="true">
                 <html:radio name="orgMapForm" property="graphaspect" value="true"><bean:message key="general.inform.org.graphvaspect"/></html:radio> 
                 <html:radio name="orgMapForm" property="graphaspect" value="false"><bean:message key="general.inform.org.graphhaspect"/></html:radio> 
               </logic:equal>
               <logic:notEqual name="orgMapForm" property="graphaspect" value="true">
                 <html:radio name="orgMapForm" property="graphaspect" value="true"><bean:message key="general.inform.org.graphvaspect"/></html:radio> 
                 <html:radio name="orgMapForm" property="graphaspect" value="false"><bean:message key="general.inform.org.graphhaspect"/></html:radio> 
               </logic:notEqual>
               </td>
           </tr>           
         </table>
      </fieldset>
      <br>
      <!--
      <fieldset align="center" style="width:90%;">
         <legend ><bean:message key="general.inform.org.cell"/></legend>
         <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
           <tr>
              <td align="left"  nowrap valign="top">
                <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1">
                     <tr>
                        <td align="left"  nowrap valign="top">
                          <html:radio name="orgMapForm" property="cellletteralignleft" value="1"><bean:message key="general.inform.org.cellletteralignleft"/></html:radio>
                        </td>
                       </tr>
                       <tr>
                         <td align="left"  nowrap valign="top" >
                          <html:radio name="orgMapForm" property="cellletteralignleft" value="2"><bean:message key="general.inform.org.cellletteralignright"/></html:radio>
                         </td>
                       </tr>
                       <tr>
                        <td align="left"  nowrap valign="top" colspan="2">  
                          <html:radio name="orgMapForm" property="cellletteralignleft" value="3"><bean:message key="general.inform.org.cellletteraligncenter"/></html:radio>
                         </td>
                       </tr>
                   </table>
              </td> 
              <td align="left"  nowrap valign="top">
                  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1">
                      <tr>
                         <td align="left"  nowrap valign="top">
                           <html:checkbox name="orgMapForm" property="celllettervaligncenter" value="valign-center"><bean:message key="general.inform.org.celllettervaligncenter"/></html:checkbox>   
                         </td>
                       </tr>
                   </table>             
              </td>  
               <td align="left"  nowrap valign="top">
                <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1">
                      <tr>
                         <td align="left"  nowrap valign="top">
                           <html:checkbox name="orgMapForm" property="cellletterfitsize" value="true"><bean:message key="general.inform.org.cellletterfitsize"/></html:checkbox> 
                         </td>
                       </tr>
                       <tr>
                         <td align="left"  nowrap valign="top">   
                           <html:checkbox name="orgMapForm" property="cellletterfitline" value="true"><bean:message key="general.inform.org.cellletterfitline"/></html:checkbox>     
                         </td>
                       </tr>
                   </table>    
               </td> 
           </tr>         
      </table>
 </fieldset>
 --> 
 <br> 
  <fieldset align="center" style="width:90%;">
         <legend ><bean:message key="general.inform.org.cellproperty"/></legend>
         <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
           <tr>
              <td align="left"  nowrap valign="top">
                   <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1">
                        <tr>
                           <td align="right"  nowrap valign="top">
                               <bean:message key="general.inform.org.cellhspacewidth"/>
                            </td>
                            <td align="left"  nowrap valign="top"> 
                               <html:text   name="orgMapForm" property="cellhspacewidth" size="6" styleClass="textColorWrite" onkeypress="event.returnValue=IsDigit();"/>
                            </td> 
                        </tr>
                        <tr>
                           <td align="right"  nowrap>
                             <bean:message key="general.inform.org.cellvspacewidth"/>
                            </td>
                            <td align="left"  nowrap valign="top"> 
                             <html:text   name="orgMapForm" property="cellvspacewidth" size="6" styleClass="textColorWrite" onkeypress="event.returnValue=IsDigit();"/>
                            </td>
                         </tr>
                        <tr>
                           <td align="right"  nowrap valign="top"> 
                             <bean:message key="general.inform.org.celllinestrokewidth"/>
                            </td>
                            <td align="left"  nowrap valign="top"> 
                             <html:text   name="orgMapForm" property="celllinestrokewidth" size="6" styleClass="textColorWrite" onkeypress="event.returnValue=IsDigit();"/>
                           </td>
                        </tr>
                    </table> 
              </td>
              <td align="left"  nowrap valign="top">
                   <table width="100%" border="0" cellspacing="1"  valign="top" cellpadding="1">
                        <tr>
                          <td align="left"  nowrap valign="top">
                            <!--<bean:message key="general.inform.org.cellshape"/>
                             <html:select name="orgMapForm" property="cellshape" size="1" disabled = "true">
                                  <html:option value="rect"><bean:message key="general.inform.org.cellrectshape"/></html:option>
                             </html:select>&nbsp;     -->                        
                          </td>
                         </tr>
                    </table>
              </td>
              <td align="left"  nowrap valign="top">
                   <table width="100%" border="0" cellspacing="1"  valign="top" cellpadding="1">
                        <tr>
                           <td align="right"  nowrap valign="top">
                             <bean:message key="general.inform.org.cellwidth"/>
                            </td>
                            <td align="left"  nowrap valign="top"> 
                             <html:text   name="orgMapForm" property="cellwidth" size="6" styleClass="textColorWrite" onkeypress="event.returnValue=IsDigit();"/>
                           </td>
                        </tr>
                        <tr>
                           <td align="right"  nowrap valign="top">
                               <bean:message key="general.inform.org.cellheight"/>
                            </td>
                            <td align="left"  nowrap valign="top"> 
                               <html:text   name="orgMapForm" property="cellheight"  size="6" styleClass="textColorWrite" onkeypress="event.returnValue=IsDigit();"/>
                            </td>
                        </tr>
                        <tr>
                           <td align="right"  nowrap valign="top">
                               <bean:message key="general.inform.org.cellcolor"/>
                            </td>
                            <td align="left"  nowrap valign="top"> 
                               <html:text  name="orgMapForm" property="cellcolor" alt="clrDlg" size="6" style="BACKGROUND-COLOR:${orgMapForm.cellcolor}" styleClass="textColorWrite" readonly="true"/>
                            </td>
                        </tr>
                        <!--  <tr>
                        <logic:equal name="orgMapForm" property="graphaspect" value="true">
                           <td align="right"  nowrap valign="top">
                               <html:radio name="orgMapForm" property="cellaspect" value="true"><bean:message key="general.inform.org.cellvaspect"/></html:radio> 
                            </td>
                           <td align="left"  nowrap>                               
                               <html:radio name="orgMapForm" property="cellaspect" value="false"><bean:message key="general.inform.org.cellhaspect"/></html:radio> 
                           </td>
                         </logic:equal>
                          <logic:notEqual name="orgMapForm" property="graphaspect" value="true">
                           <td align="right"  nowrap valign="top">
                               <html:radio name="orgMapForm" property="cellaspect" value="true"><bean:message key="general.inform.org.cellvaspect"/></html:radio> 
                            </td>
                           <td align="left"  nowrap>                               
                               <html:radio name="orgMapForm" property="cellaspect" value="false"><bean:message key="general.inform.org.cellhaspect"/></html:radio> 
                           </td>
                         </logic:notEqual>
                        </tr>-->
                    </table>                    
              </td> 
          </tr>
      </table>
 </fieldset>
 <br>
 <br>
 <fieldset align="center" style="width:90%;">
         <legend ><bean:message key="general.inform.org.fontfamily"/></legend>
         <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
            <tr>
              <td align="left"  nowrap valign="top">
                  <bean:message key="general.inform.org.fontfamily"/>
                  <html:select name="orgMapForm" property="fontfamily">
                      <html:option value="song"><bean:message key="font_family.song"/></html:option>
                      <html:option value="kaiti"><bean:message key="font_family.kaiti"/></html:option>
                      <html:option value="lishu"><bean:message key="font_family.lishu"/></html:option>
                      <html:option value="youyuan"><bean:message key="font_family.youyuan"/></html:option>
                  </html:select>  
                  <bean:message key="general.inform.org.fontstyle"/>
                  <html:select name="orgMapForm" property="fontstyle">
                      <html:option value="general"><bean:message key="font_style.general"/></html:option>
                      <html:option value="italic"><bean:message key="font_style.italic"/></html:option>
                      <html:option value="thick"><bean:message key="font_style.thick"/></html:option>
                      <html:option value="italicthick"><bean:message key="font_style.italicthick"/></html:option>
                  </html:select>
                  <bean:message key="general.inform.org.fontsize"/>
                      <html:select name="orgMapForm" property="fontsize">
                      <html:option value="8">8</html:option>
                      <html:option value="9">9</html:option>
                      <html:option value="10">10</html:option>
                      <html:option value="11">11</html:option>
                      <html:option value="12">12</html:option>
                      <html:option value="14">14</html:option>
                      <html:option value="16">16</html:option>
                      <html:option value="18">18</html:option>
                      <html:option value="20">20</html:option>
                      <html:option value="22">22</html:option>
                      <html:option value="24">24</html:option>
                      <html:option value="26">26</html:option>
                      <html:option value="28">28</html:option>
                      <html:option value="36">36</html:option>
                      <html:option value="48">48</html:option>
                      <html:option value="72">72</html:option>
                  </html:select>                   
                   <bean:message key="general.inform.org.fontcolor"/>           
                   <html:text  name="orgMapForm" property="fontcolor" alt="clrDlg" size="6" style="BACKGROUND-COLOR:${orgMapForm.fontcolor}"  styleClass="textColorWrite" readonly="true"/>
              </td>              
          </tr>
      </table>
 </fieldset>
 
     <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
           <tr style="height: 35px;">
             <td align="center"  nowrap>
              <!--<hrms:submit styleClass="mybutton" property="b_save" onclick="document.orgMapForm.target='_self';validate('F','cellhspacewidth','左右间距','F','cellvspacewidth','上下间距','F','celllinestrokewidth','线宽','F','cellwidth','单元格宽','F','cellheight','单元格高');return (document.returnValue && ifqrbc());">
                   <bean:message key="button.save"/>
              </hrms:submit>
               <hrms:submit styleClass="mybutton" property="b_return">
                   <bean:message key="button.return"/>
              </hrms:submit>-->
              <input type="button" name="savebutton"  value="<bean:message key="button.save"/>" class="mybutton" onclick="save()">              		
         			<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>"  class="mybutton" onclick="returnr()"> 
               </td> 
           </tr>
         </table>            
</html:form>
</div>
<div id="colorpanel" style="position:absolute;display:none;width:253px;height:177px;z-index:3"></div>    

