<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hjsj.hrms.actionform.kq.machine.NetSigninForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<script type="text/javascript" src="/kq/kq.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script language="javascript">
 function change()
   {
      netSigninForm.action="/kq/machine/netsignin/empNetSingnin_data.do?b_query=link&code=${netSigninForm.code}&kind=${netSigninForm.kind}";
      netSigninForm.submit();
  }
   function changes()
   {
      netSigninForm.action="/kq/machine/netsignin/empNetSingnin_data.do?b_search=link&code=${netSigninForm.code}&kind=${netSigninForm.kind}";
      netSigninForm.submit();
  }
  function changes1()
   {
   var cursignin = document.getElementById("cursignin").value;
   var curclass = document.getElementById("curclass").value;
   var curname = document.getElementById("curclass").name;
   if(curclass == null)
   {
   	return;
   }else
   {
   	if(curclass=="All")
   {
   		alert("不能在全部班次下查看签到情况,请选择需要查看的考勤班次！");
   		return;
   }
      netSigninForm.action="/kq/machine/netsignin/empNetSingnin_data.do?b_search=link&code=${netSigninForm.code}&kind=${netSigninForm.kind}";
      netSigninForm.submit();
   }
  }
  function formationStoreroom(a0100sign,nbase)
  {
    //var dbsign = document.getElementById("select_pre").value;
    var dbsign =nbase;
  	netSigninForm.action="/kq/machine/netsignin/signinlist.do?b_self=link&dbsign="+dbsign+"&a0100sign="+a0100sign+"&filg=1";
    netSigninForm.submit();
  }  
function sdStoreroom(a0100sign,nbase)
{
	//var dbsign = document.getElementById("select_pre").value;
	var dbsign =nbase;
	netSigninForm.action="/kq/machine/netsignin/sdsigninlist.do?b_sdlist=link&dbsign="+dbsign+"&a0100sign="+a0100sign+"&filg=1";
    netSigninForm.submit();
}

function selectTenolate(singin_flag)
{
	  
	   var issdao = document.getElementById("issdao").value;
	   var tab=$('tbl_r');
	   var rows=tab.rows.length;	  
	   if(rows<=1)
	   {
	       alert("没有选择人员！");
	       return;
	   }
	     
	   var  thetr;
	   var thechkbox,a0100;  
       var objarr=new Array();	   	   
	   for(var i=1;i<=tab.rows.length-1 ;i++)
	   {
	   	   var orderli=new Array();
           thetr = tab.rows[i];
           thechkbox=thetr.cells[0].children[0];
       	   if(!thechkbox.checked)
        		continue;	   
           a0100=thetr.cells[1].innerHTML;
           //nbase=thetr.cells[7].innerHTML;
           if(issdao=='false')
           	  nbase=thetr.cells[6].innerHTML;
           if(issdao=='true')
           	  nbase=thetr.cells[7].innerHTML;
           orderli.push(a0100);
           orderli.push(nbase);  
           //objarr.push(a0100);
           objarr.push(orderli);
	   }	  
       if(objarr.length>0)
       {
   	   		var hashvo=new ParameterSet();
       		hashvo.setValue("nbase","${netSigninForm.select_pre}");	
       		hashvo.setValue("workdate","${netSigninForm.registerdate}");	
       		hashvo.setValue("objlist",objarr);
       		hashvo.setValue("singin_flag",singin_flag);
       		var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'15221400005'},hashvo);
       }else
       {
          alert("请选择人员！");
       }
}
function oneNetSingnin(a0100,singin_flag,nbase)
{
   var hashvo=new ParameterSet();
   //hashvo.setValue("nbase","${netSigninForm.select_pre}");
   hashvo.setValue("nbase",nbase);
   hashvo.setValue("workdate","${netSigninForm.registerdate}");	
   hashvo.setValue("a0100",a0100);
   hashvo.setValue("singin_flag",singin_flag);
   var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'15221400004'},hashvo);
}   
function oneLoadNetSingnin(a0100,singin_flag,nbase)
{
   if(singin_flag=="1")
   {
   		if(confirm("是否取消上岛签到?"))
   		{
   			var hashvo=new ParameterSet();
   			//hashvo.setValue("nbase","${netSigninForm.select_pre}");
   			hashvo.setValue("nbase",nbase);
   			hashvo.setValue("workdate","${netSigninForm.registerdate}");	
   			hashvo.setValue("a0100",a0100);
   			hashvo.setValue("singin_flag",singin_flag);
   			var request=new Request({asynchronous:false,onSuccess:isSuccessLoad,functionId:'15221400006'},hashvo);
   		}
   }else
   {
   		var hashvo=new ParameterSet();
   		//hashvo.setValue("nbase","${netSigninForm.select_pre}");
   		hashvo.setValue("nbase",nbase);
   		hashvo.setValue("workdate","${netSigninForm.registerdate}");	
   		hashvo.setValue("a0100",a0100);
   		hashvo.setValue("singin_flag",singin_flag);
   		var request=new Request({asynchronous:false,onSuccess:isSuccessLoad,functionId:'15221400006'},hashvo);
   }
}  
function batchLoadNetSingnin(singin_flag)
{
	   var tab=$('tbl_r');
	   var rows=tab.rows.length;	  
	   if(rows<=1)
	   {
	       alert("没有选择人员！");
	       return;
	   }
	     
	   var  thetr;
	   var thechkbox,a0100,nbase;  
       var objarr=new Array();	   	   
	   for(var i=1;i<=tab.rows.length-1 ;i++)
	   {
	   	   var orderli=new Array();
           thetr = tab.rows[i];
           thechkbox=thetr.cells[0].children[0];
       	   if(!thechkbox.checked)
        		continue;	   
           a0100=thetr.cells[1].innerHTML;
           nbase=thetr.cells[7].innerHTML;
           orderli.push(a0100);
           orderli.push(nbase);
           objarr.push(orderli);
	   }
       if(objarr.length>0)
       {
   	   		var hashvo=new ParameterSet();
       		hashvo.setValue("nbase","${netSigninForm.select_pre}");	
       		hashvo.setValue("workdate","${netSigninForm.registerdate}");	
       		hashvo.setValue("objlist",objarr);
       		hashvo.setValue("singin_flag",singin_flag);
       		var request=new Request({asynchronous:false,onSuccess:isSuccessLoad,functionId:'15221400007'},hashvo);
       }else
       {
          alert("请选择人员！");
       }
}
function isSuccess(outparamters)
{
	var flag=outparamters.getValue("flag");
	var mess=outparamters.getValue("mess");
	var signin_flag=outparamters.getValue("signin_flag");
	var signin_type=outparamters.getValue("signin_type");
	var a0100=outparamters.getValue("a0100");
	var nbase=outparamters.getValue("nbase");
	if(flag=="ok")
	{
	    alert(mess)
	    if(signin_type=="one")
	    {
	       if(signin_flag=="0")
	       {
	          var vo_on=document.getElementById(nbase+a0100+"_singin_on"); 
	          vo_on.innerHTML="<font color='#555555'>已签</font>&nbsp;&nbsp;";
	       }else
	       {
	          var vo=document.getElementById(nbase+a0100+"_singin_off");
	          vo.innerHTML="&nbsp;&nbsp;<font color='#555555'>已签</font>";
	       }
	    }else
	    {
	       change();
	    }	    
	}else
	{
	   alert("签到失败！");
	}

}
function isSuccessLoad(outparamters)
{
	var flag=outparamters.getValue("flag");	
	var signin_flag=outparamters.getValue("signin_flag");
	var signin_type=outparamters.getValue("signin_type");
	var a0100=outparamters.getValue("a0100");
	var nbase=outparamters.getValue("nbase");
	var mess=outparamters.getValue("mess");
	if(flag=="ok")
	{
	    alert(mess);	   
	    if(signin_type=="one")
	    {
	       if(signin_flag=="0")
	       {
	          var vo_on=document.getElementById(nbase+a0100+"_on_load"); 	          
	          vo_on.innerHTML="<font color='#555555'>已签</font>&nbsp;&nbsp;";
	          var voOff=document.getElementById(nbase+a0100+"_off_load");
	           voOff.innerHTML="&nbsp;&nbsp;<a href=\"javascript:oneLoadNetSingnin('"+a0100+"','1','"+nbase+"')\">取消</a>"; 
	       }else
	       {
	          var vo_on=document.getElementById(nbase+a0100+"_on_load"); 	   
	          vo_on.innerHTML="<a href=\"javascript:oneLoadNetSingnin('"+a0100+"','0','"+nbase+"')\">签到</a>&nbsp;&nbsp;";
	          var vo=document.getElementById(nbase+a0100+"_off_load");
	          vo.innerHTML="&nbsp;&nbsp;<font color='#555555'>取消</font>";
	       }
	    }else
	    {
	       change();
	    }	    
	}else
	{
	   alert("签到失败！");
	}

}
</script> 
<script language="javascript">
    var bean_value
    function setbean(a0100,nbase)
   {
     var hashvo=new ParameterSet();
     //hashvo.setValue("nbase","${netSigninForm.select_pre}");
     hashvo.setValue("nbase",nbase);
     hashvo.setValue("workdate","${netSigninForm.registerdate}");	
     hashvo.setValue("a0100",a0100);
     hashvo.setValue("sdao_count_field","${netSigninForm.sdao_count_field}")
     var request=new Request({method:'post',onSuccess:getBean,functionId:'15221400011'},hashvo);
   }
    function getBean(outparamters)
    {
      var onsingin=outparamters.getValue("onsingin");
      var a0100=outparamters.getValue("a0100");

      var nbase=outparamters.getValue("nbase");  //人员库
      var offsingin=outparamters.getValue("offsingin");
      var vo_on=document.getElementById(nbase+a0100+"_singin_on");
      var class_mess=outparamters.getValue("class_mess");
      var sdao=outparamters.getValue("sdao");
      if(class_mess==undefined||class_mess==null)
      {
          var mess_vo=document.getElementById("class_name_"+a0100+nbase); 
          mess_vo.innerHTML="";
      }else
      {
          var mess_vo=document.getElementById("class_name_"+a0100+nbase); 
          mess_vo.innerHTML=class_mess;
      }
      if(vo_on!=null)
      {
      	 if(onsingin=="true")
      	{
         	vo_on.innerHTML="<font color='#555555'>已签</font>&nbsp;&nbsp;";
      	}else
      	{	
       	 	vo_on.innerHTML="<a href=\"javascript:oneNetSingnin('"+a0100+"','0','"+nbase+"')\">签到</a>&nbsp;&nbsp;"; 
      	}
      }
      if(offsingin=="true")
      {
         var vo=document.getElementById(nbase+a0100+"_singin_off");
         if(vo!=null)
         {
         	vo.innerHTML="&nbsp;&nbsp;<font color='#555555'>已签</font>";
         }
      }else
      {
         var vo=document.getElementById(nbase+a0100+"_singin_off");
         if(vo!=null)
         {
         	vo.innerHTML="&nbsp;&nbsp;<a href=\"javascript:oneNetSingnin('"+a0100+"','1','"+nbase+"')\">签退</a>";
         }
      }
      sdaoHtml(sdao,a0100,nbase);
    }
    function sdaoHtml(sdao,a0100,nbase)
    {
       if(sdao=="1")
       {  
           var voOn=document.getElementById(nbase+a0100+"_on_load");
           var voOff=document.getElementById(nbase+a0100+"_off_load");
           voOn.innerHTML="<font color='#555555'>已签</font>&nbsp;&nbsp;";
           voOff.innerHTML="&nbsp;&nbsp;<a href=\"javascript:oneLoadNetSingnin('"+a0100+"','1','"+nbase+"')\">取消</a>"; 
       }else if(sdao=="0")
       {
            var voOn=document.getElementById(nbase+a0100+"_on_load");
            var voOff=document.getElementById(nbase+a0100+"_off_load");
            voOn.innerHTML="<a href=\"javascript:oneLoadNetSingnin('"+a0100+"','0','"+nbase+"')\">签到</a>&nbsp;&nbsp;";
            voOff.innerHTML="&nbsp;&nbsp;<font color='#555555'>取消</font>"; 
       }else if(sdao=="xx")
       {
          var voOn=document.getElementById(nbase+a0100+"_on_load");
          var voOff=document.getElementById(nbase+a0100+"_off_load");
          if(voOn)
            voOn.innerHTML="<font color='#555555'>签到</font>&nbsp;&nbsp;";
          if(voOff)
            voOff.innerHTML="&nbsp;&nbsp;<font color='#555555'>取消</font>"; 
       }
    }
    
    function goback()
    {
      singleRegisterForm.action="/kq/register/daily_registerdata.do?b_query2=link";
      singleRegisterForm.submit();
    }
    function editClass(a0100,nbase)
    {
    	var a_code = a0100;
    	var days = document.getElementById("editor1").value;
    	if(days.length<=0)
    	{
    	    alert("工作日期不能为空!");
    		return;
    	}
    	days = days.replace('.','-');
    	days = days.replace('.','-');
    	var target_url;
      	var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
      	target_url="/kq/team/array/normal_array_data.do?b_normal=link`a_code="+a_code+"`nbase="+nbase+"`session_data="+days+"";
      	if($URL)
      		target_url = $URL.encode(target_url);
      	var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
      	var return_vo= window.showModalDialog(iframe_url,window, 
        	"dialogWidth:440px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
    	if(!return_vo)
	    	return;
	    if(return_vo.flag=="true")
      	{
         	//var waitInfo=eval("wait");	   
	     	//waitInfo.style.display="block";  
	     	//netSigninForm.action="/kq/machine/netsignin/empNetSingnin_data.do?b_search=link";
	     	//netSigninForm.submit();
      	}	
    }
 
 </script> 
<html:form action="/kq/machine/netsignin/empNetSingnin_data" onsubmit="">
<% int i=0; %>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" >
<html:hidden name="netSigninForm" property="issdao" styleId='issdao' styleClass="text"/>
  <tr height="25" >
     <td >
        <table border="0" cellspacing="0"  align="left" cellpadding="0">
           <tr height="25">
              <td nowrap> &nbsp;
                 <html:select name="netSigninForm" property="select_pre" styleId="select_pre" size="1" onchange="changes();">
                     <html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>	        
                  </html:select>&nbsp;
              </td>
              <td nowrap>&nbsp;
                工作日期
                   <input type="text" name="registerdate" value="${netSigninForm.registerdate}" extra="editor" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="editor1"  dropDown="dropDownDate">
              </td>
              <td nowrap>&nbsp;
                按考勤班次<html:select name="netSigninForm" property="curclass" size="1" onchange="changes();">
                     <html:optionsCollection property="classlist" value="dataValue" label="dataName"/>	        
                  </html:select>
              </td>
              <td nowrap>&nbsp;
                  按签到<html:select name="netSigninForm" property="cursignin" size="1" onchange="changes1();">
                     <html:optionsCollection property="signinlist" value="dataValue" label="dataName"/>	        
                  </html:select>
              </td>
              <td align="left" nowrap>&nbsp;
                  姓名
                 <input type="text" name="select_name" value="${netSigninForm.select_name}" class="inputtext" style="width:100px;font-size:10pt;text-align:left">		
                 <input type="button" name="br_return" value='查询' class="mybutton" onclick="changes();">
              </td>
              
           </tr>
        </table>
        
     </td>
  </tr>	
  <tr>
       <td>
       <div class="fixedDiv2" style="margin-top: 2px;">
        <table  id="tbl_r"  width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
         <tr>
            <td align="center" class="TableRow" style="border-top:none;border-left: none;" nowrap>
		      <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
            </td> 
            <td align="center" class="TableRow" style="display:none">
			</td>	
            <!--<td align="center" class="TableRow" nowrap>
               单位
            </td>-->           
            <td align="center" class="TableRow" style="border-top:none;" nowrap>
               部门
	        </td>
            <td align="center" class="TableRow" style="border-top:none;" nowrap>
               姓名
	        </td>
            <td align="center" class="TableRow" style="border-top:none;" nowrap>
               班次
	        </td>
	        <hrms:priv func_id="0C3460,0C3461,0C3462">
	        <td align="center" class="TableRow" style="border-top:none;" nowrap>
               签到
	        </td>
	        </hrms:priv>
	      <logic:equal name="netSigninForm" property="issdao" value="true">
	        <td align="center" class="TableRow" style="border-top:none;" nowrap>
               上岛签到
	        </td>
	     </logic:equal>
	     <logic:equal name="netSigninForm" property="classA01" value="1">
	        <td align="center" class="TableRow" style="border-top:none;" nowrap>
               主集班次
	        </td>
	     </logic:equal>
	     <logic:equal name="netSigninForm" property="cardnoId" value="1">
	        <td align="center" class="TableRow" style="border-top:none;" nowrap>
	        	<bean:write name="netSigninForm" property="cardnoName" />&nbsp; 
	        </td>
	     </logic:equal>
	     <hrms:priv func_id="0C3465">
	     	<td align="center" class="TableRow" style="border-top:none;border-right: none;" nowrap>
               更改班次
	        </td>
	     </hrms:priv>
         </tr>
         <hrms:paginationdb id="element" name="netSigninForm" sql_str="netSigninForm.sqlstr" table="" where_str="" columns="netSigninForm.columns" order_by="netSigninForm.ordeby" pagerows="${netSigninForm.pagerows}" page_id="pagination">
          <%
          if(i%2==0){ 
          %>
          <tr class="trShallow">
          <%
          }else{
          %>
          <tr class="trDeep">
          <%}i++;            
          %>  
          <%
             	LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
             	String a0100=(String)abean.get("a0100"); 
             	String Nbase=(String)abean.get("nbase");
                NetSigninForm netSigninForm=(NetSigninForm)session.getAttribute("netSigninForm");
                String cardnoid = netSigninForm.getCardno();
                cardnoid=cardnoid.toLowerCase();
                String a01000 = PubFunc.encrypt("EP"+a0100.toString());
                String a01001 = PubFunc.encrypt(a0100.toString());
                String nbase1 = PubFunc.encrypt(Nbase.toString());
          %>
            <td align="center" class="RecordRow" style="border-left:none;" nowrap>
            	<hrms:kqdurationjudge startDate="${netSigninForm.registerdate}">
		          <hrms:checkmultibox name="netSigninForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
                </hrms:kqdurationjudge>
                </td> 
           <td align="center" class="TableRow" style="display:none">
			    <bean:write name="element" property="a0100" filter="true"/>
			</td>
            <!--<td class="RecordRow" nowrap>&nbsp; 
               <hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem"  scope="page"/>  	      
          	     <bean:write name="codeitem" property="codename" />&nbsp;  
            </td>-->           
            <td  class="RecordRow" nowrap>&nbsp; 
                <hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" uplevel="${netSigninForm.uplevel}"  scope="page"/>  	      
          	     <bean:write name="codeitem" property="codename" />  
	        </td>
            <td align="left" class="RecordRow" nowrap>
              &nbsp;<bean:write name="element" property="a0101" filter="true"/>&nbsp;
	        </td>
            <td align="left" class="RecordRow" nowrap id="class_name_<%=a01001 %><%=nbase1 %>">
              &nbsp;
	        </td>
	        <hrms:priv func_id="0C3460,0C3461,0C3462">
	        <td align="center" class="RecordRow" align="center"  nowrap>
	          <table widht="100%"  border="0" cellspacing="0" cellpadding="0"> 
	             <tr>
	             <hrms:priv func_id="0C3460"> 
	               <td widht="33%" align="center" id="<%=nbase1 %><%=a01001 %>_singin_on">&nbsp;
	               </td>
	              </hrms:priv>
	              <hrms:priv func_id="0C3461">
	               <td widht="33%"  align="center" id="<%=nbase1 %><%=a01001 %>_singin_off">&nbsp;
	               </td>
	               </hrms:priv>
	               <hrms:priv func_id="0C3462">
	               <td widht="33%" widht="33%" >
	                  &nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:formationStoreroom('<%=a01001 %>','<%=nbase1 %>')">明细</a> 
	               </td>
	               </hrms:priv> 
	             </tr>
	          </table>
	        </td>
	        </hrms:priv>
	       <logic:equal name="netSigninForm" property="issdao" value="true">
	        <td align="center" class="RecordRow" align="center" nowrap>
	          <table widht="100%"  border="0" cellspacing="0" cellpadding="0"> 
	             <tr>
	               <td widht="33%" align="center" id="<%=nbase1 %><%=a01001 %>_on_load">
	               &nbsp;
	               </td>
	               <td widht="33%"  align="center" id="<%=nbase1 %><%=a01001 %>_off_load">
	               &nbsp;
	               </td>
	               <td widht="33%" widht="33%" >
	                    &nbsp;&nbsp;&nbsp;&nbsp;
                     <a href="javascript:sdStoreroom('<%=a01001 %>','<%=nbase1 %>')">明细</a>  
	               </td>
	             </tr>
	          </table>
	        </td>
	       </logic:equal>
	       <td style='display:none'>
	       	<bean:write name="element" property="nbase" filter="true"/>
	       </td>
	       <logic:equal name="netSigninForm" property="classA01" value="1">
	          <logic:iterate id="info" name="netSigninForm"  property="fieldlist" indexId="index">
	          	<logic:notEqual name="info" property="codesetid" value="0">
	          	  <td align="left" class="RecordRow" nowrap>
                             &nbsp;<hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${netSigninForm.uplevel}"/>  	      
                               <bean:write name="codeitem" property="codename" />  &nbsp;
                          </td>
                </logic:notEqual>
                <logic:equal name="info" property="codesetid" value="0">
                	<td align="left" class="RecordRow" nowrap>
                		&nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
                	</td>
                </logic:equal> 
	          </logic:iterate>
          	</logic:equal>
          	<logic:equal name="netSigninForm" property="cardnoId" value="1">
          	<td align="center" class="RecordRow" nowrap>
          		<bean:write name="element" property="<%=cardnoid%>" />  
          	</td>
          	</logic:equal>
          	<hrms:priv func_id="0C3465">
          	<td align="center" class="RecordRow" style="border-right:none;" nowrap>
          		<hrms:kqdurationjudge startDate="${netSigninForm.registerdate}">   
          		<a href="javascript:editClass('<%=a01000 %>','<%=nbase1 %>')"><img src="/images/edit.gif" border=0></a>
          		</hrms:kqdurationjudge>
          	</td>
          	</hrms:priv>
	        <script language="javascript"> 
                  setbean('<%=a01001 %>','<%=nbase1 %>'); 
            </script> 
          </tr>
          </hrms:paginationdb>
        </table>
        </div>
     </td>
  </tr>
  <tr>
    <td>
    <div style="*width:expression(document.body.clientWidth-10);">
       <table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
					<hrms:paginationtag name="netSigninForm" pagerows="${netSigninForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="netSigninForm" property="pagination" nameId="netSigninForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
	   </table>
	   </div>
    </td>
  </tr>
  <tr>
    <td>
      <table width="85%" align="left" border="0" cellspacing="0" cellpadding="0">
		<tr height="40">
		    <td align="left">
		    	<hrms:priv func_id="0C3460">		    
                <html:button styleClass="mybutton" property="bc_btn1" onclick="selectTenolate('0')">
				     批量签到
				</html:button>&nbsp;
				</hrms:priv>
				<hrms:priv func_id="0C3461">
				<html:button styleClass="mybutton" property="bc_btn1" onclick="selectTenolate('1')">
				     批量签退
				</html:button>&nbsp;
				</hrms:priv>
		        <logic:equal name="netSigninForm" property="issdao" value="true">
				<html:button styleClass="mybutton" property="bc_btn1" onclick="batchLoadNetSingnin('0')">
					批量上岛签到
				</html:button>&nbsp;
				</logic:equal>
			</td>
		</tr>
	</table>
    </td>
  </tr>
</table>
</html:form>
<script language="javascript">
hide_nbase_select('select_pre');
</script>
<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style common_background_color" height=24>正排班,请不要刷新页面</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
        </table>
</div> 