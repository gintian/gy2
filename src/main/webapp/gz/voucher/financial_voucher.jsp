<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.voucher.VoucherForm" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.lang.*"%>
<%@ page import="com.hrms.struts.taglib.CommonData,com.hrms.struts.constant.WebConstant,com.hrms.struts.valueobject.UserView"%>

<%	
	VoucherForm voucherForm=(VoucherForm)session.getAttribute("financial_voucherForm");
	String []columnArray=(String[])voucherForm.getXmlArray();
	ArrayList none_field=(ArrayList)voucherForm.getNone_field();
	ArrayList  itemlist = voucherForm.getList();
	ArrayList  titleList=  voucherForm.getTitlelist();
	int ls=0;
	if(titleList.size()>0){
	   ls=titleList.size();
	}	 
	int i=voucherForm.getVoucherForm().getPagination().getStart();
	HashMap tempMap=voucherForm.getTempMap();
	ArrayList nloanList=voucherForm.getNloanList();
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
	String privflag = (String)request.getParameter("privflag");
%>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript">
	var localfl_id;
	function excecuteExcel()
   {
  	
	var hashvo=new ParameterSet();		
	hashvo.setValue("pn_id","${financial_voucherForm.pn_id}");
	hashvo.setValue("none_fieldValue","${financial_voucherForm.none_fieldValue}");
	hashvo.setValue("titleValue","${financial_voucherForm.titleValue}");
	hashvo.setValue("sqlValue","${financial_voucherForm.sqlValue}");
	hashvo.setValue("xmlValue","${financial_voucherForm.xmlValue}");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showExcel,functionId:'3020073014'},hashvo);
   }	
   function showExcel(outparamters)
   {
	var url=outparamters.getValue("excelfile");	
	url = getDecodeStr(url);
	var win=open("/servlet/vfsservlet?fileid="+url+"&fromjavafolder=true","excel");
   }
   //计算公式
   function complexquerysubmit(src){
   			var p=document.getElementById("pn_id").value;
			var url="/gz/voucher/complexquery.do?b_query=link&fl_id="+src+"&pn_id="+p+"&clsflag=1&privflag=<%=privflag%>"; 
			var parameter = ""; 
			var obj= window.showModalDialog(url, parameter, "dialogWidth:525px; dialogHeight:473px;resizable:no;center:yes;scroll:no;status:no"); 
			var flag=0;
			if(obj != null){
			var temp=document.getElementById("ci").value=obj;
			var clsflag="1";
			var hashvo=new ParameterSet();
		  	hashvo.setValue("fl_id",src);
			hashvo.setValue("pn_id","${financial_voucherForm.pn_id}");
			hashvo.setValue("c_itemsql",temp);
			hashvo.setValue("clsflag",clsflag);
		  	var request=new Request({method:'post',asynchronous:true,onSuccess:save_ok,functionId:'3020073019'},hashvo);
		}
	 }
	 //本币计算公式
   function complexquerysubmit1(src){
   			var p=document.getElementById("pn_id").value;
			var url="/gz/voucher/complexquery.do?b_query=link&fl_id="+src+"&pn_id="+p+"&clsflag=3&privflag=<%=privflag%>"; 
			var parameter = ""; 
			var obj= window.showModalDialog(url, parameter, "dialogWidth:525px; dialogHeight:473px;resizable:no;center:yes;scroll:no;status:no"); 
			var flag=0;
			if(obj != null){
			var temp=document.getElementById("ci").value=obj;
			var clsflag="3";
			var hashvo=new ParameterSet();
		  	hashvo.setValue("fl_id",src);
			hashvo.setValue("pn_id","${financial_voucherForm.pn_id}");
			hashvo.setValue("c_itemsql",temp);
			hashvo.setValue("clsflag",clsflag);
		  	var request=new Request({method:'post',asynchronous:true,onSuccess:save_ok,functionId:'3020073019'},hashvo);
		}
	 }
	 
	  function limitquerysubmit(src){
   			var p=document.getElementById("pn_id").value;
			var url="/gz/voucher/complexquery.do?b_query=link&fl_id="+src+"&pn_id="+p+"&clsflag=2&privflag=<%=privflag%>"; 
			var parameter = ""; 
			var obj= window.showModalDialog(url, parameter, "dialogWidth:525px; dialogHeight:473px;resizable:no;center:yes;scroll:no;status:no"); 
			var clsflag="2";
			if(obj != null){
			var temp=document.getElementById("ci").value=obj;
			var hashvo=new ParameterSet();
		  	hashvo.setValue("fl_id",src);
			hashvo.setValue("pn_id","${financial_voucherForm.pn_id}");
			hashvo.setValue("c_itemsql",temp);
			hashvo.setValue("clsflag",clsflag);
		  	var request=new Request({method:'post',asynchronous:true,onSuccess:save_ok,functionId:'3020073019'},hashvo);
		}
	 }
	 function save_ok(outparameters)
	{
	}
	function showSelectBox(srcobj,src){
		localfl_id=src;
		var textValue=document.getElementById(localfl_id).value;
		var sel = document.getElementById("nloanselect");
		for(var i=0;i<sel.length;i++){
			if(textValue==sel[i].value){
				sel[i].selected=true;
				break;
			}
		}
   		var pos=getAbsPosition(srcobj);  	
   		document.getElementById("nloanValue").style.posLeft=pos[0]-1;
 		document.getElementById("nloanValue").style.posTop=pos[1]-1+srcobj.offsetHeight;
		document.getElementById("nloanValue").style.width=(srcobj.offsetWidth<100)?100:srcobj.offsetWidth+1;
		document.getElementById("nloanselect").style.width=(srcobj.offsetWidth<100)?100:srcobj.offsetWidth+1;
		document.getElementById("nloanValue").style.display="block";          
	}
	function document.onkeyup()
 	{
	  if ( event.keyCode=='13' )
	  {
	    document.getElementById("nloanValue").style.display="none";
	    var n_loan=document.getElementById(localfl_id).value;
		var p=document.getElementById("pn_id").value;
		var hashvo=new ParameterSet();
		hashvo.setValue("fl_id",localfl_id);
		hashvo.setValue("pn_id",p);
		hashvo.setValue("n_loan",n_loan);
		var request=new Request({method:'post',asynchronous:true,onSuccess:checknloan_ok,functionId:'3020073011'},hashvo);
  	   }
	 }

 
	function removeSlectBox(){
	var act = document.activeElement.id;
	if(act==""){
		document.getElementById("nloanValue").style.display="none";
	}
}
	function checknloan(src)
	{
		var n_loan=document.getElementById(src).value;
		var p=document.getElementById("pn_id").value;
		var hashvo=new ParameterSet();
		hashvo.setValue("fl_id",src);
		hashvo.setValue("pn_id",p);
		hashvo.setValue("n_loan",n_loan);
		var request=new Request({method:'post',asynchronous:true,onSuccess:checknloan_ok,functionId:'3020073011'},hashvo);
	}
	function checknloan_ok(outparameters)
	{
	}
	
	function setc_group(src){
   			var p=document.getElementById("pn_id").value;
			var url="/gz/voucher/setgroup.do?b_query=link&fl_id="+src+"&privflag=<%=privflag%>&pn_id="+p; 
			var parameter = ""; 
			var obj= window.showModalDialog(url, parameter, "dialogWidth:525px; dialogHeight:473px;resizable:no;center:yes;scroll:no;status:no"); 
			if(obj != null){
			var pn_id=obj[0];
			var interface_type=obj[1];
			financial_voucherForm.action="/gz/voucher/searchvoucherdate.do?b_query=link&pn_id="+pn_id+"&interface_type="+interface_type+"&showflag=2";
			financial_voucherForm.submit();
		}
	 }
	 function del(){
	 	var len=document.financial_voucherForm.elements.length;
       var uu;
       for (var i=0;i<len;i++)
       {
           if(document.financial_voucherForm.elements[i].type=='checkbox'&&document.financial_voucherForm.elements[i].name!="selbox")
	       {	
                if(document.financial_voucherForm.elements[i].checked==true)
                 {
                  uu="dd";
                  break;
                 }
              
           }
       }
       if(uu!="dd")
       {
          alert("没有选择分录！");
          return false;
       }else{
            if(!confirm("确认要删除吗?")){
                return false;
            }
       } 
	 }
	 function getN_loan(objSelect){	
		var n_loan=objSelect.options[objSelect.selectedIndex].text;
		document.getElementById("nloanValue").style.display="none";
		//var n_loan=document.getElementById(localfl_id).value;
		var p=document.getElementById("pn_id").value;
		var item = document.getElementById(localfl_id);
		item.value=n_loan;
		var hashvo=new ParameterSet();
		hashvo.setValue("fl_id",localfl_id);
		hashvo.setValue("pn_id",p);
		hashvo.setValue("n_loan",n_loan);
		var request=new Request({method:'post',asynchronous:true,onSuccess:checknloan_ok,functionId:'3020073011'},hashvo);
	}
</script>
<style type="text/css">
	#whole{
		width:100%;
		padding-left:10px;
		padding-right:10px;
		overflow-x:auto;
	}
</style>
<body>
<div id="whole" >
<html:form action="/gz/voucher/searchvoucherdate">
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>

	<logic:equal name="financial_voucherForm" property="pn_id" value="">
		<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable" id="nop">
	          <tr height="20">
	       		<td  align="left" class="TableRow">&nbsp;<bean:message key="label.information"/>&nbsp;</td>	      
	          </tr> 
	          <tr>
	              	 <td align="left" nowrap style="height:60px">&nbsp;&nbsp;&nbsp;<bean:message key="gz.voucher.undifiend"/></td>
	          </tr> 
  		</table>
	</logic:equal>
	<logic:notEqual name="financial_voucherForm" property="pn_id" value="">
	<html:hidden  property="pn_id" />
	<html:hidden  property="interface_type" />
	<html:hidden  property="c_itemsql" styleId="ci"/>
	<table  border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" width="<%=(ls-4)*100+760%>px">
		 <thead>
		 	<tr>
				<td align="center" class="TableRow" nowrap width="40px">
					<input type="checkbox" name="selbox" onclick="batch_select(this,'voucherForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap width="50px">&nbsp;
					<bean:message key="label.edit"/>&nbsp;
            	</td> 
            	<%
            	   for(int s=0;s<titleList.size();s++){
            	       if("摘要".equals(titleList.get(s))||"分录名称".equals(titleList.get(s))){
            	%>
            	   <td align="center" class="TableRow" width="200px">&nbsp;<%=titleList.get(s)%>&nbsp;</td>
            	<%
            	       }else if("顺序号".equals(titleList.get(s))){
            	%>
            	       <td align="center" class="TableRow" width="50px">&nbsp;<%=titleList.get(s)%>&nbsp;</td>
            	<%
            	       }else if("摘要".equals(titleList.get(s))){
            	%>
            	   <td align="center" class="TableRow" width="230px">&nbsp;<%=titleList.get(s)%>&nbsp;</td>
            	<%
            	       }else{
            	%>
            	   <td align="center" class="TableRow" width="100px">&nbsp;<%=titleList.get(s)%>&nbsp;</td>
            	<%
            	       }
            	   } 
            	%>
		 	</tr>
		 </thead>
	     	<hrms:extenditerate id="element" name="financial_voucherForm" property="voucherForm.list" indexes="indexes"  pagination="voucherForm.pagination" pageCount="21" scope="session">
			<%
				if(i%2==0)
				{
			%>
				<tr class="trShallow">
			<%
				}
				else{
			%>
				<tr class="trDeep">
			<%
				}     
			%>  
			<td align="center" class="RecordRow"  width="40px">
	   			<hrms:checkmultibox name="financial_voucherForm" property="voucherForm.select" value="true" indexes="indexes"/>&nbsp;
		    </td> 
		    	<%
		    		HashMap itemMap =(HashMap) itemlist.get(i);
		    		for(int j=0;j<columnArray.length;j++){
		    		if("fl_id".equals(columnArray[j].toLowerCase())){
		    	%>
		    	<td align="center" class="RecordRow"  width="40px">
	            	<a href="/gz/voucher/searchvoucherdate.do?b_edit=link&a_id=<%=(itemMap.get(columnArray[j])==null)?"": itemMap.get(columnArray[j])%>&privflag=<%=privflag %>">
					<img src="/images/edit.gif" border=0></a>
		    	</td> 
		    	<%
		    		continue;
		    		}
		    		 if(none_field.contains(columnArray[j].toLowerCase())){
	    					continue;
	    		}
	    		if("c_itemsql".equals(columnArray[j].toLowerCase())){//计算公式
	    		%>
	    			<td align="center" class="RecordRow" width="100px">
	    			 	<a href="javascript:complexquerysubmit('<%=(itemMap.get("fl_id")==null)?"": itemMap.get("fl_id")%>');"><bean:message key="kq.item.count"/></a>&nbsp;
	    			</td>
	    		<%
	    				}
	    		else if("c_extitemsql".equals(columnArray[j].toLowerCase())){//本币计算公式
		    		%>
		    			<td align="center" class="RecordRow" width="100px">
		    			 	<a href="javascript:complexquerysubmit1('<%=(itemMap.get("fl_id")==null)?"": itemMap.get("fl_id")%>');"><bean:message key="kq.item.count"/></a>&nbsp;
		    			</td>
		    		<%
		    				}
	    		
	    				else if("c_where".equals(columnArray[j].toLowerCase())){//限制条件
	    		%>
	    				<td align="center" class="RecordRow"  width="100px">
	    			 		<a href="javascript:limitquerysubmit('<%=(itemMap.get("fl_id")==null)?"": itemMap.get("fl_id")%>');"> <bean:message key="gz.voucher.limit"/></a>&nbsp;
	    				</td>
	    		<%
	    				}else if("n_loan".equals(columnArray[j].toLowerCase())){
	    		%>
	    				<td align="left" class="RecordRow"  width="100px">
	    				&nbsp;
								<%
										for(int m=0;m<nloanList.size();m++){
											String temp=(String)nloanList.get(m);
											String ss="";
											ss=(tempMap.get(i)==null||"".equals(tempMap.get(i)))?"":(String)tempMap.get(i);
											if(ss.equals(temp)){
								%>
								<%=temp%>
								<!--  
									<input type="text"  style=" border:0px; BORDER-TOP-STYLE: none; BORDER-RIGHT-STYLE: none; BORDER-LEFT-STYLE: none; BORDER-BOTTOM-STYLE: none" size="10" id="<%=(itemMap.get("fl_id")==null)?"": itemMap.get("fl_id")%>"value="<%=temp%>" onclick="showSelectBox(this,<%=(itemMap.get("fl_id")==null)?"": itemMap.get("fl_id")%>)" onblur="removeSlectBox()" maxlength="2">
									<div id="nloanValue"style="border-style:nono;display:none;position:absolute ; onclick="otherFoucs()">
											<select id="nloanselect"size="5"  ondblclick="getN_loan(this)" >
												<logic:iterate  id="element" name="financial_voucherForm"  property="nloanList">
													<option value="<bean:write name='element'/>" > <bean:write name="element"/> </option>
												</logic:iterate> 
											</select>
									</div>
								  -->
								<%
											}
										}
								%>
								
	    				</td>
	    		<%
	    				}else if("c_group".equals(columnArray[j].toLowerCase())){//分录分组指标
	    		%>
	    				<td align="center" class="RecordRow"  width="100px">
	    			 		<a href="javascript:setc_group('<%=(itemMap.get("fl_id")==null)?"": itemMap.get("fl_id")%>');"><bean:message key="gz.voucher.groupFileditem"/></a>&nbsp;
	    				</td>
	    		<%
	    			}else if("c_subject".equals(columnArray[j].toLowerCase())){
	    				
	    		%>
	    			<td align="left" class="RecordRow"  width="230px">
	    				&nbsp;<%=(itemMap.get(columnArray[j])==null)?"": itemMap.get(columnArray[j])%>
	    			</td>
	    		<%
	    			}else if("c_mark".equals(columnArray[j].toLowerCase())){
	    		%>
	    		    <td align="left" class="RecordRow"  width="200px">
                        &nbsp;<%=(itemMap.get(columnArray[j])==null)?"": itemMap.get(columnArray[j])%>
                    </td>
	    		<% 
	    		     }else if("fl_name".equals(columnArray[j].toLowerCase())){
	    		%>
	    		    <td align="left" class="RecordRow"  width="200px">
                        &nbsp;<%=(itemMap.get(columnArray[j])==null)?"": itemMap.get(columnArray[j])%>
                    </td> 
	    		<%
	    		    }else if("seq".equalsIgnoreCase(columnArray[j].toLowerCase())){
	    		%>
	    			<td align="center" class="RecordRow" width="50px">
	    				&nbsp;<%=(itemMap.get(columnArray[j])==null)?"": itemMap.get(columnArray[j])%>
	    			</td>
	    		<%
	    			}else{
	    		%>
	    		    <td align="center" class="RecordRow" width="100px">
                        &nbsp;<%=(itemMap.get(columnArray[j])==null)?"": itemMap.get(columnArray[j])%>
                    </td>
	    		<%	
	    			}
		    	}
		    	i++;     
		   	 %>
		    </tr>
		  
	     	</hrms:extenditerate>
	</table>
	<table  width="<%=(ls-4)*100+760%>px" align="center" class="RecordRowP" id="tabc">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="financial_voucherForm" property="voucherForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="financial_voucherForm" property="voucherForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="financial_voucherForm" property="voucherForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="financial_voucherForm" property="voucherForm.pagination"
				nameId="voucherForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="60%" align="center" id="tabl">
          <tr>
            <td align="center">
         	<hrms:submit styleClass="mybutton" property="b_add" disabled="disabled">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
         	<hrms:submit styleClass="mybutton" property="b_delete" onclick="return del();">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
		<input type="button" name="b_excel" value="<bean:message key="goabroad.collect.educe.excel"/>" class="mybutton" onclick="excecuteExcel();">
            </td>
          </tr>          
</table>
</logic:notEqual>
</html:form>

</div>
<script type="text/javascript">
	<%if("3".equals(privflag)){%>
		document.getElementsByName("b_add")[0].disabled=true;
		document.getElementsByName("b_delete")[0].disabled=true;
	<%}%>
</script>
</body>