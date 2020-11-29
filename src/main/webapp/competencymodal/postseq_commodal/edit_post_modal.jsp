<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.competencymodal.PostModalForm,java.util.ArrayList,org.apache.commons.beanutils.LazyDynaBean,com.hrms.struts.taglib.CommonData"%>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script language="JavaScript" src="/competencymodal/postseq_commodal/postmodal.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<script type="text/javascript">
<%if(request.getParameter("isClose")!=null&&request.getParameter("isClose").equals("1")){%>
  if(window.showModalDialog){
	  window.returnValue="1";
  }else{
	  if(parent.window.opener.editPostModal_callback){
		//19/9/9 xus 能力素质，素质模型，岗位序列素质模型，点编辑没有反应
		parent.window.opener.editPostModal_callback("1");
	  }
  }
  parent.window.close();
<%}%>
function sub()
{
    <%int m=0;%>
    <logic:iterate  id="element"    name="postModalForm"  property="editPostModalList" indexId="index"> 
       <logic:equal name="element" property="editable"  value="1" >
          <logic:equal name="element" property="itemtype"  value="N" >
              var a<%=m%>=document.getElementsByName("editPostModalList[<%=m%>].value");
              if(a<%=m%>[0].value!='')
			  {
					var myReg =/^(-?\d+)(\.\d+)?$/
					if(!myReg.test(a<%=m%>[0].value)) 
					{
					   alert("<bean:write  name="element" property="itemdesc"/>"+PLEASEWRITENUMBER+"！");
						return;
					}
			  }
          </logic:equal>
       </logic:equal>
    <%m++;%>
    </logic:iterate>
    var hashvo=new ParameterSet();
	hashvo.setValue("object_type",'${postModalForm.object_type}');
	hashvo.setValue("object_id",'${postModalForm.codeitemid}');
	hashvo.setValue("point_id",'${postModalForm.pointCode}');
    var request=new Request({asynchronous:false,onSuccess:confirmSavePostModal,functionId:'90100170066'},hashvo); 
}
</script>
<html:form action="/competencymodal/postseq_commodal/post_modal_list">
<html:hidden name="postModalForm" property="object_type"/>
<html:hidden name="postModalForm" property="codesetid"/>
<html:hidden name="postModalForm" property="codeitemid"/>
<html:hidden name="postModalForm" property="pointCode"/>
<table width="440px" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr>
<td align="left" colspan="2" class="TableRow">
&nbsp;&nbsp;<bean:message key="button.edit"/>
</td>
</tr>
<%
 PostModalForm postModalForm=(PostModalForm)session.getAttribute("postModalForm");
 ArrayList editPostModalList=postModalForm.getEditPostModalList();
 for(int i=0;i<editPostModalList.size();i++)
 {
      LazyDynaBean abean =(LazyDynaBean)editPostModalList.get(i);
      String itemid=(String)abean.get("itemid");
      String itemdesc=(String)abean.get("itemdesc");
      String editable=(String)abean.get("editable");
      String value=(String)abean.get("value");
      String viewvalue=(String)abean.get("viewvalue");
      String codesetid=(String)abean.get("codesetid");
      String itemtype=(String)abean.get("itemtype");
      
          out.println("<tr><td class=\"RecordRow\" align=\"right\" width=\"40%\">"+itemdesc+"&nbsp;</td>");
          out.print("<td class=\"RecordRow\" align=\"left\"  width=\"60%\">&nbsp;");
          if(itemid.equalsIgnoreCase("gradecode"))
          {
             ArrayList options=(ArrayList)abean.get("options");
             out.print("<select name=\"editPostModalList["+i+"].value\" style=\"width:100;\">");
             for(int j=0;j<options.size();j++)
             {
                CommonData cd = (CommonData)options.get(j);
                out.print("<option value=\""+cd.getDataValue()+"\"");
                if(value.equalsIgnoreCase(cd.getDataValue()))
                {
                   out.print(" selected ");
                }
                out.print(">"+cd.getDataName()+"</option>");
             }
             out.print("</select>");
          }
          else
          {
             if(editable.equals("1"))
             {
                if(itemtype.equalsIgnoreCase("A"))
                {
                   if(codesetid.equals("0"))
                   {
                     out.print("<input type=\"text\" size=\"20\" name=\"editPostModalList["+i+"].value\" value=\""+value+"\" class='inputtext'/>"); 
                   }
                   else
                   {
                     out.print("<input  style=\"vertical-align:middle\" type=\"text\" name=\"editPostModalList["+i+"].viewvalue\" size=\"20\" value=\""+viewvalue+"\" class='inputtext'/>&nbsp;");
                     out.print("<a href='javascript:openInputCodeDialog(\""+codesetid+"\",\"editPostModalList["+i+"].viewvalue\");' ><img src=\"/images/code.gif\"  style=\"vertical-align:middle\" border=\"0\"/></a>");
                     out.print("<input type=\"hidden\" name=\"editPostModalList["+i+"].value\" value=\""+value+"\"/>"); 
                   }
                }
                else if(itemtype.equalsIgnoreCase("D"))
                {
                  out.print("<input type='text'  size='20'  name='editPostModalList["+i+"].value'  value='"+value+"'");
				  out.print("  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)' ");	
				  out.print("   class='inputtext'/>&nbsp;");
                }
                else
                {
                   out.print("<input type=\"text\" name=\"editPostModalList["+i+"].value\" value=\""+value+"\" class='inputtext'/>"); 
                   if(itemid.equals("rank"))
                       out.print("%");
                }
             }else{
                if(codesetid.equals("0"))
                {
                   out.print(value);
                }else{
                  out.print(viewvalue);
                }
             }
           }
           out.println("</td></tr>");
     
 }
 %>
 <tr>
 <td align="center" colspan="2" style="padding-top:3px;">
<input type="button" name="sv" value="<bean:message key="button.save"/>" onclick="sub();" class="mybutton"/>

<input type="button" name="cc" value="<bean:message key="button.cancel"/>" onclick="parent.window.close();" class="mybutton"/>
</td>
</tr>
</table>
</html:form>