<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<%@ page import="com.hjsj.hrms.actionform.kq.options.KqItemForm" %>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
<script language="javascript" src="../../ajax/constant.js"></script>
<script language="javascript" src="../../ajax/basic.js"></script>
<script language="javascript" src="../../ajax/common.js"></script>
<script language="javascript" src="../../ajax/control.js"></script>
<script language="javascript" src="../../ajax/dataset.js"></script>
<script language="javascript" src="../../ajax/editor.js"></script>
<script language="javascript" src="../../ajax/dropdown.js"></script>
<script language="javascript" src="../../ajax/table.js"></script>
<script language="javascript" src="../../ajax/menu.js"></script>
<script language="javascript" src="../../ajax/tree.js"></script>
<script language="javascript" src="../../ajax/pagepilot.js"></script>
<script language="javascript" src="../../ajax/command.js"></script>
<script language="javascript" src="../../ajax/format.js"></script>
<script language="javascript" src="../../js/validate.js"></script>
<script type="text/javascript" src="../../general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>
<%
	int i=0;
	String returnFlag = request.getParameter("returnFlag");
%>
<script language="javascript">
  function adds(str)
  {
        
    	   target_url="/kq/options/change_order.do?b_add=link`item_id="+str;
    	   if($URL)
    	      	target_url = $URL.encode(target_url);
    	   var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
    	  // newwindow=window.open(iframe_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=150,left=350,width=500,height=424');
    	   newwindow=window.showModalDialog(iframe_url,window, 
            "dialogWidth:500px; dialogHeight:424px;resizable:no;center:yes;scroll:no;status:no;scrollbars:no");
	  
  }
  
    function messs()
  {
     alert('<bean:message key="error.kq.exist"/>');
  }
  function choice(obj){
  	var hid_id = obj.parentElement.id+"_h";15204110016
	var hid_obj = document.getElementById(hid_id);
  	var hashvo = new ParameterSet();
  	hashvo.setValue("items",hid_obj.value);
  	hashvo.setValue("sdata_src",obj.value);
  	var request=new Request({asynchronous:false,onSuccess:choice_ok,functionId:'15204110016'},hashvo);
  }
  function choice_ok(outparamters){
  	
  }
  //导入指标
  function openappstatistics(item_id)
  {
      var hashvo = new ParameterSet();
      hashvo.setValue("items",item_id);
  	  var request=new Request({asynchronous:false,onSuccess:items_ok,functionId:'15204110026'},hashvo);
  }
  function items_ok(outparamters)
  {
  	  var mse=outparamters.getValue("mse");
  	  var src=outparamters.getValue("src");
  	  var it=outparamters.getValue("it");
  	  if(mse=="0")
  	  {
  	  	alert("未选择统计指标!");
  	  	return;
  	  }
  	  //else if(src=="0")
  	  //{
  	  //	alert("请设定统计数据来源!");
  	  //	return;
  	  //}
  	  else
  	  {
  	  	 kqItemForm.action="/kq/options/add_item.do?b_import=link&akq_item="+it;
    	 kqItemForm.submit();
  	  }
  }
</script>
<html:form action="/kq/options/kq_item_details">
<div  class="fixedDiv2" style="height: 100%;border: none">
<div class="fixedDiv5" style="margin-top: 10px;height: 100%;" >
  <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
    <tr class="fixedHeaderTr"> 
      <td align="center" class="TableRow" style="border-top: none;border-left: none;border-right: none;" nowrap><bean:message key="kq.item.code"/></td>
      <td align="center" class="TableRow" style="border-top: none; none;border-right: none;"  nowrap><bean:message key="kq.item.name"/></td>
      <logic:empty name="kqItemForm" property="gw_flag">
	      <td align="center" class="TableRow" style="border-top: none; none;border-right: none;"  nowrap><bean:message key="kq.item.rest"/></td>
	      <td align="center" class="TableRow" style="border-top: none; none;border-right: none;"  nowrap><bean:message key="kq.item.feria"/></td>	 
	      <td align="center" class="TableRow" style="border-top: none; none;border-right: none;"  nowrap><bean:message key="kq.item.cellect"/></td>	   
      </logic:empty>
      <td align="center" class="TableRow" style="border-top: none; none;border-right: none;"  nowrap><bean:message key="kq.item.sign"/></td>	 	  
      <logic:empty name="kqItemForm" property="gw_flag">
	      <td align="center" class="TableRow" style="border-top: none; none;border-right: none;"  nowrap><bean:message key="kq.item.color"/></td>
	      <td align="center" class="TableRow" style="border-top: none; none;border-right: none;"  nowrap><bean:message key="kq.item.measure"/></td>
	      <td align="center" class="TableRow" style="border-top: none; none;border-right: none;"  nowrap><bean:message key="kq.item.stat"/></td>
	      <td align="center" class="TableRow" style="border-top: none; none;border-right: none;"  nowrap><bean:message key="kq.item.sformula"/></td>
	      <td align="center" class="TableRow" style="border-top: none; none;border-right: none;"  nowrap><bean:message key="kq.item.order"/></td>
	      <td align="center" class="TableRow" style="border-top: none; none;border-right: none;"  nowrap>导入指标</td>	  	 
	      <td align="center" class="TableRow" style="border-top: none; none;border-right: none;"  nowrap><bean:message key="kq.item.day.count"/></td>	
	      <td align="center" class="TableRow" style="border-top: none; none;border-right: none;"  nowrap><bean:message key="kq.item.mo.count"/></td>     	   	  
      </logic:empty>	 
      <td align="center" class="TableRow" style="border-top: none; none;border-right: none;"  nowrap><bean:message key="kq.item.edit"/></td>
    </tr>
    
    <hrms:extenditerate id="element" name="kqItemForm" property="kqItemForm.list" indexes="indexes"  
        pagination="kqItemForm.pagination" pageCount="500" scope="session">
          <%
          KqItemForm kqItemForm = (KqItemForm)request.getSession().getAttribute("kqItemForm");
          RecordVo vo=(RecordVo)pageContext.getAttribute("element");
          String other_param = vo.getString("other_param");
          String c_expr = vo.getString("c_expr");
          String exprd = "";
          String exprm = "";
          if(c_expr!=null && c_expr.trim().length()>0 && !"^".equals(c_expr))
          {
        	  String[] dm=c_expr.split("\\^");
        	  exprd=dm[0];
        	  if(dm.length==2)
        	  exprm=dm[1];
          }
          if(i%2==0)
          {
          %>
          <tr class="trShallow" onclick="tr_onclick(this,'')">
          <%}
          else
          {%>
          <tr class="trDeep" onclick="tr_onclick(this,'')">
          <%
          }
          i++;          
          %>  
             <td align="left" class="RecordRow" style="border-top: none;border-left: none;border-right: none;" nowrap>              
                   &nbsp;<bean:write  name="element" property="string(item_id)" filter="true"/>&nbsp;
             </td>  
             <td align="left" class="RecordRow" style="border-top: none; none;border-right: none;"   nowrap>              
                   &nbsp;<bean:write  name="element" property="string(item_name)" filter="true"/>&nbsp;
             </td>  
          	<logic:empty name="kqItemForm" property="gw_flag">
             <td align="center" class="RecordRow" style="border-top: none; none;border-right: none;"   nowrap>  
             	<logic:equal name="element" property="string(has_rest)" value="1">
                   <bean:message key="kq.item.yes"/>&nbsp;
               </logic:equal>
               <logic:notEqual name="element" property="string(has_rest)" value="1">
                   <bean:message key="kq.item.no"/>&nbsp;
               </logic:notEqual>               
              </td>                  
              <td align="center" class="RecordRow" style="border-top: none; none;border-right: none;"   nowrap>  
              	<logic:equal name="element" property="string(has_feast)" value="1">
                   <bean:message key="kq.item.yes"/>&nbsp;
               </logic:equal>
               <logic:notEqual name="element" property="string(has_feast)" value="1">
                   <bean:message key="kq.item.no"/>&nbsp;
               </logic:notEqual>               
              </td> 
              <td align="center" class="RecordRow" style="border-top: none; none;border-right: none;"   nowrap> 
              	<logic:equal name="element" property="string(want_sum)" value="1">
                   <bean:message key="kq.item.yes"/>&nbsp;
               </logic:equal>
               <logic:notEqual name="element" property="string(want_sum)" value="1">
                   <bean:message key="kq.item.no"/>&nbsp;
               </logic:notEqual>    
              </td> 
          	</logic:empty>
              <td align="left" class="RecordRow" style="border-top: none; none;border-right: none;"   nowrap>              
                   <bean:write  name="element" property="string(item_symbol)" filter="true"/>&nbsp;
              </td> 
             <logic:empty name="kqItemForm" property="gw_flag">
              <td bgcolor='<bean:write  name="element" property="string(item_color)" filter="true"/>' style="border-top: none; none;border-right: none;"   class="RecordRow">
              &nbsp;</td>
              <td align="left" class="RecordRow" style="border-top: none; none;border-right: none;"   nowrap>    
          	   <hrms:codetoname codeid="28" name="element" codevalue="string(item_unit)" codeitem="codeitem" scope="page"/>  	      
          	   &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;  
              </td> 
              <td align="left" class="RecordRow" style="border-top: none; none;border-right: none;"   nowrap>   
                 <hrms:fieldtoname name="element" fieldname="string(fielditemid)" fielditem="fielditem"/>
	              &nbsp;<bean:write name="fielditem" property="dataValue" />&nbsp;     
              </td> 
               <td align="center" class="RecordRow" style="border-top: none; none;border-right: none;"   nowrap id="sle_<%=i%>">
               	 <html:select name="element" property="string(sdata_src)" size="1" onchange="choice(this)" >
		         	<html:optionsCollection  property="klist" value="dataValue" label="dataName" />
		         </html:select>
		         <INPUT type="hidden" id="sle_<%=i%>_h" name="item_id_h" value='<bean:write name="element" property="string(item_id)" filter="true"/>' />
              </td>
              <td align="right" class="RecordRow" style="border-top: none; none;border-right: none;"   nowrap>
                <bean:write  name="element" property="string(displayorder)" filter="true"/>&nbsp;
              </td>
               <bean:define id="item_id1" name="element" property="string(item_id)"/>
		         <%
		         		//参数加密
		    		     String item_id = PubFunc.encrypt(item_id1.toString());
		         %>
              <td align="center" class="RecordRow" style="border-top: none; none;border-right: none;"   nowrap>
            	<% if(other_param.trim().length()>0) {%>
            	<img src="../../images/wjj_c.gif" onclick="openappstatistics('<%=item_id %>')" border=0>
            	<% } else { %>
            	<img src="../../images/edit.gif" onclick="openappstatistics('<%=item_id %>')" border=0>
            	<%} %>
            	
	          </td>
               <td align="center" class="RecordRow" style="border-top: none; none;border-right: none;"   nowrap> 
               		 <%
			         		//参数加密
			    		     String str1 = "akq_item="+item_id1+"&expr_flag=day";
			         %>
               	<a href="/kq/options/computer_formula.do?b_querys=link&encryptParam=<%=PubFunc.encrypt(str1) %>">
               	<% if(exprd.trim().length()>0) {%>
               	<img src="../../images/wjj_c.gif" border=0>
               	<% } else { %>
               		<img src="../../images/edit.gif" border=0>
               	<% }  %>
               	</a> 
              </td>	
              <td align="center" class="RecordRow" style="border-top: none; none;border-right: none;"   nowrap> 
              		 <%
			         		//参数加密
			    		     String str2 = "akq_item="+item_id1+"&expr_flag=mo";
			         %>
               	<a href="/kq/options/computer_formula.do?b_querys=link&encryptParam=<%=PubFunc.encrypt(str2) %>">
               <% if(exprm.trim().length()>0) {%>
               	<img src="../../images/wjj_c.gif" border=0>
               	<% } else { %>
               		<img src="../../images/edit.gif" border=0>
               	<% }  %>
               	</a> 
              </td>
             </logic:empty>
               <bean:define id="item_id" name="element" property="string(item_id)"/>
              <td align="center" class="RecordRow" style="border-top: none; none;border-right: none;"   nowrap>
              		 <%
			         		//参数加密
			    		     String str3 = "akq_item="+item_id+"&returnFlag="+returnFlag;
			         %>
            	<a href="/kq/options/add_item.do?b_query=link&encryptParam=<%=PubFunc.encrypt(str3) %>"><img src="/images/edit.gif" border=0></a>
	          </td>                                   	    
          </tr>
        </hrms:extenditerate> 
  </table>
</div>
<table width="90%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
	<tr>
		<td align="center" style="height:35px;width: 100%;">             
			<input type="button" name="b_saveb" value="<bean:message key="kq.item.change.order"/>" class="mybutton" onclick="adds('<bean:write name="kqItemForm" property="codeitemid" filter="true"/>')"> 
			<hrms:tipwizardbutton flag="workrest" target="il_body" formname="kqItemForm"/>
			<logic:notEmpty name="kqItemForm" property="gw_flag" >
		   	     <input type="button" value="<bean:message key="button.close"/>" class="mybutton" onclick="window.close();"/>
			</logic:notEmpty>
		</td>
	</tr>          
</table>
</div>
<logic:equal name="kqItemForm" property="sys" value="2">
<script language="javascript">
	messs();
</script>
</logic:equal>


</html:form>

