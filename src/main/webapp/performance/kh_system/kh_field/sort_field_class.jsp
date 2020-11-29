<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.performance.kh_system.kh_field.KhFieldForm,com.hrms.struts.taglib.CommonData,java.util.ArrayList"%>
<script type="text/javascript">
<!--
function getNewSort()
	{
       var newsortvo=new Object();
       newsortvo.pointsetid=document.khFieldForm.pointsetid.value;
       newsortvo.subsys_id = document.khFieldForm.subsys_id.value;
       var temp="";
       for(var i=0;i<document.khFieldForm.list.options.length;i++){
         temp += document.khFieldForm.list.options[i].value+"/";
         }
       newsortvo.ids = temp;
       newsortvo.sorttype=document.khFieldForm.sorttype.value;
        parent.window.returnValue=newsortvo;
        if(window.showModalDialog) {
            parent.window.close();;
        }else{
            parent.window.opener.window.Change_sort_OK(newsortvo);
            window.open("about:blank","_top").close();
        }
	}

function closewindow()
{
    if(window.showModalDialog) {
        parent.window.close();;
    }else{
        window.open("about:blank","_top").close();
    }
}

//-->
</script>
<%
 KhFieldForm khFieldForm=(KhFieldForm)session.getAttribute("khFieldForm");
 ArrayList list = khFieldForm.getList();
 int length=0;
 
 %>
<html:form action="/performance/kh_system/kh_field/sort_field_class">

<div id="first" style="filter:alpha(Opacity=100);display:block;margin-top:-3px;">
<table width="310" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr>
<td>
		<fieldset align="left" style="width:100%;">
							<legend>
									<bean:message key="kh.field.sort"/>
							</legend>
<table>
   	  <tr>
   	  <td class='RecordRow'>
   	  <div style="overflow:auto;width:220px;height:260px;">
   	  <table style="width:100%;">
   	  <tr>
   	  <td>
                    
                    <%
                    for(int i=0;i<list.size();i++)
                    {
                       CommonData cd = (CommonData)list.get(i);
                       String value=cd.getDataValue();
                       String name=cd.getDataName();
                       if(name.length()>length)
                           length=name.length();
                     }
                     int size=12;
                     if(list.size()>12)
                           size=list.size();
                     String strsize=size+"";                           
                     if(length>=18)
                     {
                     %>
                      <html:select name="khFieldForm" property="list" multiple="multiple" style="width:100%;height:235px;" >
                      <html:optionsCollection property="list" value="dataValue" label="dataName"/>
                    </html:select> 
                    <%}else{ %>
                     <html:select name="khFieldForm" property="list" multiple="multiple"  style="width:100%;height:235px;">
                      <html:optionsCollection property="list" value="dataValue" label="dataName"/>
                    </html:select> 
                    <%} %>
                    </td>
                    </tr>
                    </table>
                    </div>  	     
   	   </td>
   	  
   	    <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_up" onclick="upItem($('list'));">
            		     <bean:message key="button.previous"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_down" onclick="downItem($('list'));">
            		     <bean:message key="button.next"/>    
	           </html:button >	     
             </td>      
   	  </tr>
          
          <tr> <td><html:hidden name="khFieldForm" property="pointsetid"/></td>
               <td><html:hidden name="khFieldForm" property="subsys_id"/>
               <html:hidden name="khFieldForm" property="sorttype"/></td></tr>  
</table>
	</fieldset>
</td>
</tr>
</table>
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:2px;">
<tr>
          <td align="center"  nowrap  colspan="3" style="padding-top:5px;">
              <html:button styleClass="mybutton" property="b_savemove" onclick="getNewSort();">
            		      <bean:message key="button.save"/>
	      </html:button> 	 
	      <html:button styleClass="mybutton" property="clo" onclick="closewindow();" >
	      <bean:message key="button.close"/>
	      </html:button>      
          </td>
          </tr> 
          </table>
</div>
</html:form>
