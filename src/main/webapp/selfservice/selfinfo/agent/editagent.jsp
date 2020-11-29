<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,java.text.SimpleDateFormat,java.util.Date"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<%
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	String date = sdf.format(new Date());
%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/js/validateDate.js"></script>
<script type="text/javascript">
<!--
	function select_org_emp_agent(flag,selecttype,dbtype,priv,isfilter,loadtype)
    {
	   if(dbtype!=1)
	 	  dbtype=0;
	   if(priv!=0)
	      priv=1;
	   var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
        var theurl="/selfservice/selfinfo/agent/agentemptree.do?flag="+flag+"&selecttype="+selecttype+"&dbtype="+dbtype+
                "&priv="+priv + "&isfilter=" + isfilter+"&loadtype="+loadtype;
        var return_vo= window.showModalDialog(theurl,1, 
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	    return return_vo;
    }
	function getSelectedEmploy(status)
	{
	  
       var return_vo=select_org_emp_agent(1,2,1,0,0); 
       if(!return_vo)  
         return false;
       var agent_fullname=return_vo.title;
       var agent_id=return_vo.content;	
       if(status=="4")
       {
          var vo=document.getElementById('agent_fullname');
          var a0100_vo=document.getElementById('a0100');  
          var nbase_vo=document.getElementById('nbase');  
          var principal_id="${agentForm.principal_id}";       
          if(principal_id==agent_id)
          {
            alert("委托人不能选定自己为代理人！");
            return false;
          }
          vo.value=agent_fullname;
          nbase_vo.value=agent_id.substring(0,3);
          a0100_vo.value=agent_id.substring(3);          
       }else
       {
          var vo=document.getElementById('agent_fullname');
          var id_vo=document.getElementById('agent_id');  
          var principal_id="${agentForm.principal_id}";       
          if(principal_id==agent_id)
          {
            alert("委托人不能选定自己为代理人！");
            return false;
          }
          vo.value=agent_fullname;
          id_vo.value=agent_id;
       }       
	}
	function save()
	{
	   var vo=document.getElementById('agent_fullname');
	   var fullname=vo.value;
	   if(fullname=="")
	   {
	      alert("代理人不能为空！");
	      return false;
	   }
	   vo=document.getElementById('agent_id');
	   var agent_id=vo.value;
	   vo=document.getElementById('start_date');
	   var start_date=vo.value;
	   if(start_date=="")
	   {
	      alert("有效日期起不能为空！");
	      return false;
	   }
	   vo=document.getElementById('end_date');
	   var end_date=vo.value;
	   if(end_date=="")
	   {
	      alert("有效日期止不能为空！");
	      return false;
	   }
	   vo=document.getElementById('id');
	   var id=vo.value;
	   var a0100_vo=document.getElementById('a0100');
	   var a0100=a0100_vo.value;
	   var nbase_vo=document.getElementById('nbase');
	   var nbase=nbase_vo.value;	  
	   if(confirm('确定保存吗？'))
	   {
	       var hashvo=new ParameterSet();	       
	       hashvo.setValue("agent_id",agent_id);
	       hashvo.setValue("start_date",start_date);
	       hashvo.setValue("end_date",end_date);
	       hashvo.setValue("editflag","${agentForm.editflag}");	 
	       hashvo.setValue("a0100",a0100);	 
	       hashvo.setValue("nbase",nbase);	 
	       hashvo.setValue("id",id);	
	       hashvo.setValue("agent_status","4");      
           var request=new Request({asynchronous:false,onSuccess:save_result,functionId:'1101300004'},hashvo);        
	   }
	}
	function save_result(outparamters)
	{
	    var save_result=outparamters.getValue("save_result");
	    if(save_result=="ok")
	      alert("保存成功！");
	    else
	      alert("保存失败！");
	    window.returnValue="ok";  
	    window.close();
	}
//-->
</script>
<hrms:themes />
<html:form action="/selfservice/selfinfo/agent/agentinfo"> 
  <table width="440" border="0" cellspacing="0"  align="center" cellpadding="0"  > 
  <tr >  <td colspan="2" class="framestyle1">
    <table  width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
	 <tr align="center">
		<td valign="middle" align="left" class="TableRow" style="border-left:none;border-right:none;">
		   <logic:equal name="agentForm" property="editflag" value="add">
		     新增代理人
		   </logic:equal>
		   <logic:equal name="agentForm" property="editflag" value="update">
		     修改代理人
		   </logic:equal>
		    <html:hidden name="agentForm" property="id" styleClass="text"/>
		  &nbsp;
		</td>
	 </tr>         
      <tr height="20">
          <td>&nbsp;</td>
      </tr>
      <tr  align="center" class="list3">
         <td align="center">
             <table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" >
               <tr>
                 <td height="30">
                     代&nbsp;&nbsp;&nbsp;理&nbsp;&nbsp;&nbsp;人
                 </td>
                 <td>
                 	<logic:equal value="add" name="agentForm" property="editflag">
                   		<input type="text" name="agent_fullname" value="${agentForm.agent_fullname}" style="width:250px;font-size:10pt;" readonly="readonly" class="text4">
                   	</logic:equal>
                   	<logic:notEqual value="add" name="agentForm" property="editflag">
                   		<input type="text" name="agent_fullname" value="${agentForm.agent_fullname}" style="width:250px;font-size:10pt;" disabled class="text4">
                   	</logic:notEqual>
                   	<logic:equal value="add" name="agentForm" property="editflag">
                   <img src="/images/code.gif" onclick="getSelectedEmploy('4');" align="absmiddle"/>
                   </logic:equal>
                   <html:hidden name="agentForm" property="agent_id" styleClass="text"/>
                   <html:hidden name="agentForm" property="nbase" styleClass="text"/>
                   <html:hidden name="agentForm" property="a0100" styleClass="text"/>
                 </td>
               </tr>
              <tr><td><br><br></td></tr>
               <tr>
                 <td height="30">
                   有效日期起
                 </td>
                 <td>
                   <input type="text" name="start_date" value="${agentForm.start_date}" maxlength="50" style="width:250px;font-size:10pt;" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'时间点')) {this.focus(); this.value='${agentForm.start_date}'; }" class="text4"/>
                 </td>
               </tr>
             <tr><td><br><br></td></tr>
               <tr>
                 <td height="30">
                   有效日期止
                 </td>
                 <td>
                   <input type="text" name="end_date" value="${agentForm.end_date}" maxlength="50" style="width:250px;font-size:10pt;" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'时间点')) {this.focus(); this.value='${agentForm.end_date}'; }" class="text4"/>
                 </td>
               </tr>
             </table>
            </td>
         </tr>
             <tr height="20">
            	<td>&nbsp;</td>
            </tr> 
            </table>
            </td>
            </tr>
      </table>  
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0"> 
   
    <tr><td align="center" height="35px;">
         	 <html:button styleClass="mybutton" property="" onclick="save();">
            		<bean:message key="button.ok"/>
	 	    </html:button>
         	<html:button styleClass="mybutton" property="" onclick="window.close();">
            		<bean:message key="button.cancel"/>
	 	    </html:button>  
            </td>         
         </tr>        
   </table>
</html:form>
