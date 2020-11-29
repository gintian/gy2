
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<hrms:themes></hrms:themes>
<style type="text/css">
.RecordRowC {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;
}
</style>
<%	
	int i=0;
%>
<script language="javascript">
function test(name)
{
	if(name!=null)
	{		
		var obj=$('pageset');
		obj.setSelectedTab("tab2");
	}
}

  function add(flag)
  {
       adjustcodeFrom.action="/kq/options/adjustcode/adjustcode.do?b_savehideview1=link&opt=2";         
       adjustcodeFrom.submit();         
       window.returnValue="ok";
       window.close();
   }
  function adjust()
  {
     var vos= document.getElementsByName("fashion_flag");  
     var radio_value="";
     for(var i=0;i<vos.length;i++)
     {
       if(vos[i].checked==true)
         radio_value=vos[i].value;
     }
     vos=document.getElementsByName("table");
     var table_value="";
     var select_vo=vos[0];
     for(var i=0;i<select_vo.options.length;i++)
     {
       if(select_vo.options[i].selected)
         table_value=select_vo.options[i].value;
     }
     if(radio_value=="0")
     {
       //调整指标顺序
        adjustcodeFrom.action="/kq/options/adjustcode/adjustcode.do?b_order=link&table="+table_value;
          adjustcodeFrom.submit();
     }else if(radio_value=="1")
     {
       //隐藏显示指标
        adjustcodeFrom.action="/kq/options/adjustcode/adjustcode.do?b_hideview=link&table="+table_value;
          adjustcodeFrom.submit();
     }
  }
  function adjust0()
  {
     vos=document.getElementsByName("table");
     var table_value="";
     var select_vo=vos[0];
     for(var i=0;i<select_vo.options.length;i++)
     {
       if(select_vo.options[i].selected)
         table_value=select_vo.options[i].value;
     }
     //调整指标顺序
     adjustcodeFrom.action="/kq/options/adjustcode/adjustcode.do?b_order=link&table="+table_value;
     adjustcodeFrom.submit();
   
  }
  function adjust1()
  {
     vos=document.getElementsByName("table");
     var table_value="";
     var select_vo=vos[1];
     for(var i=0;i<select_vo.options.length;i++)
     {
       if(select_vo.options[i].selected)
         table_value=select_vo.options[i].value;
     }
       //隐藏显示指标
      adjustcodeFrom.action="/kq/options/adjustcode/adjustcode.do?b_hideview1=link&&opt=2&table="+table_value;
      adjustcodeFrom.submit();
  }
    function searchFieldList1()
	{
	    vos=document.getElementsByName("table");
	     var table_value="";
	     var select_vo=vos[0];
	     for(var i=0;i<select_vo.options.length;i++)
	     {
	       if(select_vo.options[i].selected)
	         table_value=select_vo.options[i].value;
	     }
	   var hashvo=new ParameterSet();
	   hashvo.setValue("table",table_value);
	   hashvo.setValue("flag","${adjustcodeFrom.flag}");
	   hashvo.setValue("isSave","${adjustcodeFrom.isSave}");
   	   var request=new Request({method:'post',onSuccess:showFieldList1,functionId:'15204110032'},hashvo);
	}
	function showFieldList1(outparamters)
	{
		var fieldlist=outparamters.getValue("field_list");
		AjaxBind.bind(adjustcodeFrom.code_fields,fieldlist);
	}
	
  function saveCode()
  {
     var hashvo=new ParameterSet();          
     var vos= document.getElementsByName("code_fields"); 
     if(vos==null || vos[0].length==0)
     {
  	return; 
     }
     var codevo=vos[0];      
     var code_fields=new Array();        
     for(var i=0;i<codevo.options.length;i++)
     {
          var valueS=codevo.options[i].value;          
          code_fields[i]=valueS;
     }       
     var voss=document.getElementsByName("table");
	 var table_value="";
	 var select_vo=voss[0];
	     for(var i=0;i<select_vo.options.length;i++)
	     {
	       if(select_vo.options[i].selected)
	         table_value=select_vo.options[i].value;
	     }
     hashvo.setValue("code_fields",code_fields);        
     //hashvo.setValue("table","${adjustcodeFrom.table}");
     hashvo.setValue("table",table_value); 
     hashvo.setValue("isSave","yes");           	
     var request=new Request({method:'post',onSuccess:returninfo,functionId:'15204110033'},hashvo);
     
   }
   function returninfo(outparamters)
   {
      var types=outparamters.getValue("types");          
      if(types=="ok")
      {
        alert("操作成功!");
        window.returnValue="ok";
         window.close();
      }else
      {
        alert("操作失败");
      }
   }
  
</script>
<body onload="test(<%=(request.getParameter("opt"))%>);">
<html:form action="/kq/options/adjustcode/adjustcode">
<hrms:tabset name="pageset"  width="100%" height="100%" type="false"> 
 <hrms:tab name="tab1" label="menu.gz.sortitem" visible="true">
 <br><br>
	<table width="40%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<thead>
			<tr>
				<td align="center" class="TableRow" colspan="1" nowrap>
					调整指标顺序&nbsp;
				</td>
			</tr>
		</thead>
		<tr>
			<td height="50" class="RecordRow" align="center"  nowrap >
			<table width="80%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="80%" nowrap="nowrap" height="30">
						请选择调整表单:&nbsp;
				
		              	<html:select name="adjustcodeFrom" property="table" size="1" onchange="searchFieldList1()">
							<hrms:priv func_id="27039a01">  
								<html:option value="q03" >明细数据表&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
							</hrms:priv>
							<hrms:priv func_id="27039a02">  
								<html:option value="q11">加班申请表</html:option>
							</hrms:priv>
							<hrms:priv func_id="27039a03">  
								<html:option value="q13">公出申请表</html:option>
							</hrms:priv>
							<hrms:priv func_id="27039a04">  
								<html:option value="q15">请假申请表</html:option>
							</hrms:priv>
							<hrms:priv func_id="27039a05">  
									<html:option value="q17">假期管理表</html:option>
							</hrms:priv>
							<hrms:priv func_id="27039a06">  
								<html:option value="q29">部门年休假计划表</html:option>
							</hrms:priv>
							<hrms:priv func_id="27039a07">  
								<html:option value="q31">员工个人休假申请表</html:option>
							</hrms:priv>
						</html:select>
					</td>
					<td  width="20%" >
					</td>			     
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td align="center" colspan="1" class="RecordRow" nowrap>
			 <table width="80%" border="0" cellpadding="0" cellspacing="0">
			 	<tr>
			 		<td width="95%">
		        		<select name="code_fields" multiple="multiple" size="10"  style="height:230px;width:95%;font-size:9pt;margin-top:5px;margin-bottom:5px;">
                		</select>       
                	</td>
                	<td width="5%">
                   	   	<html:button  styleClass="mybutton" property="b_up" onclick="upItem($('code_fields'));">
            				<bean:message key="button.previous"/> 
	           			</html:button >
	           		<br>
	           		<br>
		           		<html:button  styleClass="mybutton" property="b_down" onclick="downItem($('code_fields'));">
	            			<bean:message key="button.next"/>    
		          		</html:button >	  
                	</td>
                </tr>
             </table>	      	       
			</td>
		</tr>
		<tr>
			<td align="center" colspan="1" class="RecordRow" nowrap style="height:35px;">
				&nbsp;&nbsp;			
			        <input type="button" name="btnreturn" value="<bean:message key="button.save"/>" class="mybutton" onclick="saveCode();">
			        <hrms:tipwizardbutton flag="workrest" target="il_body" formname="adjustcodeFrom"/>       	      	       
			</td>
		</tr>
	</table>
	</hrms:tab>
	<hrms:tab name="tab2" label="显示&隐藏指标" visible="true">
		<table width="80%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" style="margin-tOp:10px;">
		<thead>
			<tr>
				<td align="center" class="TableRow" colspan="3" nowrap>
					显示&隐藏指标&nbsp;
				</td>
			</tr>
		</thead>
		<tr>
		     <td height="50" class="RecordRow" colspan="3" nowrap>
		         <table width="90%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td  width="10%" nowrap>
							&nbsp;
						</td>
						<td width="25%" align="right">
							&nbsp; 请选择调整表单:&nbsp;
						</td>
						<td width="65%" nowrap height="30">
							<html:select name="adjustcodeFrom" property="table" size="1" onchange="adjust1()"> 
								<hrms:priv func_id="27039a01">  
									<html:option value="q03">明细数据表&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
								</hrms:priv>
								<hrms:priv func_id="27039a02">  
									<html:option value="q11">加班申请表</html:option>
								</hrms:priv>
								<hrms:priv func_id="27039a03">  
									<html:option value="q13">公出申请表</html:option>
								</hrms:priv>
								<hrms:priv func_id="27039a04">  
									<html:option value="q15">请假申请表</html:option>
								</hrms:priv>
								<hrms:priv func_id="27039a05">  
									<html:option value="q17">假期管理表</html:option>
								</hrms:priv>
								<hrms:priv func_id="27039a06">  
									<html:option value="q29">部门年休假计划表</html:option>
								</hrms:priv>
								<hrms:priv func_id="27039a07">  
									<html:option value="q31">员工个人休假申请表</html:option>
								</hrms:priv>
							</html:select>
						</td>			     
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td height="20" class="TableRow" nowrap colspan="3" align="center">
				<bean:write  name="adjustcodeFrom" property="tablemess"/>&nbsp;&nbsp;
				<html:hidden name="adjustcodeFrom" property="table" styleClass="text"/> 
			</td>            	        	        	        
		</tr>
		<tr>     
			<td align="center" class="TableRow" nowrap> 
				序号
			</td>
       		<td align="center" class="TableRow" nowrap>
				指标名称&nbsp;
			</td>  
			<td align="center" class="TableRow" nowrap>
				状态&nbsp;
			</td>  
		</tr>                    
   	    <hrms:extenditerate id="element" name="adjustcodeFrom" property="recordListForm.list"  pagination="recordListForm.pagination" scope="session" indexes="indexes" pageCount="100">     		  	 	 
         
	     <%
               if(i%2==0){ 
             %>
             <tr class="trShallow">
             <%
               }else{
             %>
             <tr class="trDeep">
             <%}
             %>
             <%i++;%>  
              <td align="center" class="RecordRow" nowrap width="10%">   
              <%=i%>
              </td>        
              <td align="center" class="RecordRow" nowrap width="45%">               
               <bean:write name="element" property="itemdesc" filter="true"/>
              </td>   
              <td align="center" class="RecordRow" nowrap>   
               <hrms:optioncollection name="adjustcodeFrom" property="v_h_list" collection="list" />
	       <html:select name="element" property="state" size="1" >
               <html:options collection="list" property="dataValue" labelProperty="dataName"/>
               </html:select>                
              </td>  
             
             
	     </tr>	     
             </hrms:extenditerate> 
 <tr>
 <td align="center" class="RecordRow"  colspan="3" nowrap style="height:35px;">    
     <input type="button" name="tt" value="<bean:message key="button.save"/>"  class="mybutton" onclick="add('${adjustcodeFrom.flag}');">
     <hrms:tipwizardbutton flag="workrest" target="il_body" formname="adjustcodeFrom"/>
 </td>
 </tr>
 </table>   
	</hrms:tab>
</hrms:tabset>
</html:form>
</body>
<script language="javascript">
   		searchFieldList1();
</script>