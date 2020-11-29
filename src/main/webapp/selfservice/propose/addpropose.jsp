<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<jsp:useBean id="proposeForm" class="com.hjsj.hrms.actionform.propose.ProposeForm" scope="session"/>
<script>
	function MM_findObj_(n, d)
{
	var p,i,x;

	if(!d)
		d=document;

	if((p=n.indexOf("?"))>0&&parent.frames.length)
	{
		d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);
	}
	if( !(x=d[n]) && d.all )
		x=d.all[n];

	for (i=0;!x&&i<d.forms.length;i++)
		x=d.forms[i][n];

	for(i=0;!x&&d.layers&&i<d.layers.length;i++)
		x=MM_findObj_(n,d.layers[i].document);

	return x;
}


	function checkonchick(xname)
	{	
		var val = xname;
		if(val.checked==true)
		{
			val.value="on";
			<%
			proposeForm.setCheck("on");
			%>
			
		}
		else
		{	
			
			val.value="";
			<%
			proposeForm.setCheck("");
			%>
						
    		}
    		
       }
</script>
<hrms:priv func_id="010401,1105">
<html:form action="/selfservice/propose/addpropose">
      <table width="500" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable" style="margin-top:6px;">
          <tr height="20">
       		<td align="left" class="TableRow" colspan="2"><bean:message key="label.suggest.box"/>&nbsp;</td>
          </tr> 
                      <tr class="list3">
                	      <td align="right" nowrap valign="top"><bean:message key="column.submit.propose"/></td>
                	      <td align="left"  nowrap>
                	      	<html:textarea name="proposeForm" property="proposevo.string(scontent)" cols="80" style="font-size:13px" rows="20"/>
                          </td>
                      </tr> 

          <tr>
            <td colspan="2" >
                <logic:equal name="proposeForm" property="proposevo.string(annymous)" value="1">
		 <input type="checkbox" name="check" checked   onclick="checkonchick(this);"><bean:message key="column.annoymous"/>
            	</logic:equal>
            	 <logic:notEqual name="proposeForm" property="proposevo.string(annymous)" value="1">
            	 <input type="checkbox" name="check"  value="" onclick="checkonchick(this);"><bean:message key="column.annoymous"/>
            	 </logic:notEqual>
            </td>
          </tr>          
                                                    
          <tr class="list3">
            <td align="center" colspan="2" style="height:35px">
         	<hrms:submit styleClass="mybutton" property="b_save" onclick="document.proposeForm.target='_self';validate('R','proposevo.string(scontent)','意见内容');return (document.returnValue && ifqrbc());">
            		<bean:message key="button.save"/>
	 	</hrms:submit>
		<html:reset styleClass="mybutton" property="reset"><bean:message key="button.clear"/></html:reset>	 	
		<logic:equal name="proposeForm" property="ctrl_return" value="1"> 
         	<hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	    </hrms:submit> 
        </logic:equal>	 	               
            </td>
          </tr>          
      </table>
</html:form>
</hrms:priv>
<script>
if(!getBrowseVersion()){//兼容非IE浏览器 页面样式
	var textareas = document.getElementsByTagName('textarea')[0];//文本域禁止拖拽  wangb 20180206 bug 34629
	textareas.style.resize ='none';
}
</script>
