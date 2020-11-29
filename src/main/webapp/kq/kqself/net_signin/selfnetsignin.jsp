<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<% int i=0;%>
<script type="text/javascript" src="/js/validate.js"></script>
<script language="javascript">
  function changes()
  {
      baseNetSignInForm.action="/kq/kqself/net_signin/selfnetsignin.do?b_self=link";
      baseNetSignInForm.submit();
  }
  function makeup()
  {
      baseNetSignInForm.action="/kq/kqself/net_signin/net_signin.do?b_makeup=link";
      baseNetSignInForm.submit();
  }
  function goback()
  {
      baseNetSignInForm.action="/kq/kqself/net_signin/net_signin.do";
      baseNetSignInForm.submit();
  }
  //function netsingin(singin_flag)
  // {
   // var ip_addr=document.getElementById("ipaddr").value;
    //签到，如果IP不绑定则不走取IP的控件
   // if($F('net_sign_check_ip')!="0")
   // {
    //	if(ip_addr==""||ip_addr=="undefined")
    //	{
        	//ip_addr=getLocalIPAddress();
     //   	ip_addr=getLocalIPAddressf();
    //	}
    	//if(ip_addr=="")
    //	{
       //		alert("找不到本地IP，请在Internet选项中对ActiveX配置\r\n对未标记为可安全执行脚本的ActiveX控件初始化并执行-提示");
      // 		return false;
    	//}
    //}   
   // var hashvo=new ParameterSet();			
    //hashvo.setValue("singin_flag",singin_flag);	
    //hashvo.setValue("ip_addr",ip_addr);
    //var request=new Request({method:'post',asynchronous:false,onSuccess:showReturn,functionId:'15502110200'},hashvo);
   //}
   //function showReturn(outparamters)
   //{
    //  var mess=outparamters.getValue("mess");
     // //var signflag=outparamters.getValue("signflag");
     // alert(mess);
     // if(mess.indexOf("成功")!=-1){
      //	baseNetSignInForm.action="/kq/kqself/net_signin/selfnetsignin.do?b_self=link";
      	//baseNetSignInForm.submit();
     // }
   //}
     /**取得本地机器ip地址*/
//function getLocalIPAddressf()
//{
//    var obj = null;
//    var rslt = "";   
//    try
//    {
//        obj=document.getElementById('SetIE');
//        	rslt = obj.GetIP();
//        obj = null;
//    }
//    catch(e)
//    {
    	//异常发生
//    }
//    return rslt;
//}
</script>
<html:form action="/kq/kqself/net_signin/selfnetsignin">
<input type="hidden" name="txtIPAddr" id="ipaddr" value="">
<html:hidden name="baseNetSignInForm" property="net_sign_check_ip"/> 
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
  <thead>
  	<tr class="RecordRow" style="background-color:white;">
      <td align="left"   nowrap colspan="6">  
			<bean:message key="label.from"/>
   	  	 	<input type="text" name="start_date" value="${baseNetSignInForm.start_date}" extra="editor" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="editor1"  dropDown="dropDownDate">
   	  	 	<bean:message key="label.to"/>
   	  	 	<input type="text" name="end_date"  value="${baseNetSignInForm.end_date}" extra="editor" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="editor2"  dropDown="dropDownDate">
            &nbsp;<input type="button" name="br_return" value='<bean:message key="button.query"/>' class="mybutton" onclick="changes();"> 
      </td>      	 
  	</tr>
    <tr>
     <td align="center"  class="TableRow" nowrap>
	姓名
     </td>
      <td align="center" class="TableRow" nowrap>
	卡号
      </td> 
      <td align="center" class="TableRow" nowrap>
	日期
      </td> 
      <td align="center" class="TableRow" nowrap>
	时间
      </td> 
      <td align="center" class="TableRow" nowrap>
	说明
      </td>
      <td align="center" class="TableRow" nowrap>
	审批标志
      </td>
    </tr>  
  </thead>
  <hrms:paginationdb id="element" name="baseNetSignInForm" sql_str="baseNetSignInForm.sql_self" table="" where_str="baseNetSignInForm.where_self" columns="${baseNetSignInForm.column_self}" order_by="${baseNetSignInForm.order_self}" page_id="pagination" pagerows="20"  indexes="indexes">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %>     
           <td align="center" class="RecordRow" nowrap>
             <bean:write name="element" property="a0101" filter="false"/>
           </td>
           <td align="center" class="RecordRow" nowrap>
              <bean:write name="element" property="card_no" filter="false"/>
           </td>
           <td align="center" class="RecordRow" nowrap>
              <bean:write name="element" property="work_date" filter="false"/>
           </td>
           <td align="center" class="RecordRow" nowrap>
              <bean:write name="element" property="work_time" filter="false"/>
           </td>
           <td align="center" class="RecordRow" nowrap>
              <bean:write name="element" property="location" filter="false"/>
           </td>  
           <td align="center" class="RecordRow" nowrap>
             <hrms:codetoname codeid="23" name="element" codevalue="sp_flag" codeitem="codeitem" scope="page"/>  	      
                <bean:write name="codeitem" property="codename" />&nbsp;  
           </td>        
       </tr>
      </hrms:paginationdb>
</table>
<table  width="80%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="pagination" property="current" filter="true" />
					页
					共
					<bean:write name="pagination" property="count" filter="true" />
					条
					共
					<bean:write name="pagination" property="pages" filter="true" />
					页
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="baseNetSignInForm" property="pagination" nameId="baseNetSignInForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
    
  </table>
   <table  width="80%" align="center">
		<tr>
		    <td align="center">	
		    	<hrms:priv func_id="0B404">         
                        <input type="button" name="br_return" value='补签' class="mybutton" onclick="makeup();"> 
		        </hrms:priv>
		        <input type="button" name="br_return" value='<bean:message key="button.return"/>' class="mybutton" onclick="goback();"> 
		        
			</td>
		</tr>
  </table>
</html:form>
<script type="text/javascript">
	//function msg(){
    //      <%
    //      String sign=(String)session.getServletContext().getAttribute("sign");           
    //      if(sign!=null&&sign.equalsIgnoreCase("in"))
    //      {
    //           session.getServletContext().setAttribute("sign","");
    //      %>
    //           netsingin('0'); 
    //      <%
    //      }else if(sign!=null&&sign.equalsIgnoreCase("out"))
    //      {
    //      %>
    //         netsingin('1');             
    //      <% 
    //         session.getServletContext().setAttribute("sign","");
    //      }
    //      %>          
	//}
</SCRIPT>
<%
  session.getServletContext().setAttribute("sign","");
%>
<script type="text/javascript">
<!--
 //window.setTimeout("msg()",100);
//-->
</script>
