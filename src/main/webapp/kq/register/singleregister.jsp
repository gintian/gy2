 <%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.kq.register.SingleRegisterForm" %>
<%@ page import="com.hrms.struts.constant.WebConstant" %>
<%@ page import="com.hrms.struts.valueobject.UserView" %>
<%
SingleRegisterForm signle=(SingleRegisterForm)session.getAttribute("singleRegisterForm");
String lockedNumStr=signle.getLockedNum();
int lockedNum=Integer.parseInt(lockedNumStr);
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<link href="/kq/kq_tableLocked.css" rel="stylesheet" type="text/css">  
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="dailyregister.js"></script>
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="/kq/kq.js"></script>
<script language="javascript" src="/ext/adapter/ext/ext-base.js"></script>
<script language="javascript" src="/ext/ext-all.js"></script>
<script language="javascript" src="/ext/rpc_command.js"></script>
<hrms:themes /> <!-- 7.0css -->
<script language="javascript"> 
 //录入方式
  var num; 
  var row;
  var line;
  var inNum; 
  function inputType(obj,event) 
  { 
    var s=9;
    if (num==null)
      num=1;      
    var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode; 
    if (keyCode == 13) { 
        if (num==null)
          num=1;    
        var id_value=obj.getAttribute("id");
        if(id_value=="")
           id_value="1";
        var int_id=parseInt(id_value);
        var focus_id="1";
        var fObj;    
       if(num==1)
       {
         if(int_id==row*inNum)
         {
          fObj=document.getElementById(focus_id);        
         }else
         {
           focus_id=int_id+1;
           fObj=document.getElementById(focus_id);
         }
       }else
       {
         var end_row_num=(row-1)*inNum
         if(int_id<end_row_num)
         {
            focus_id=int_id+inNum;
             fObj=document.getElementById(focus_id);
         }else if(int_id==row*inNum)
         {
            fObj=document.getElementById("1");
         }else
         {
           focus_id=inNum-(row*inNum)%int_id+1;
           fObj=document.getElementById(focus_id);
         }
       
      }
      fObj.focus(); 
      return false;
   }else if(keyCode == 37||keyCode == 38||keyCode == 39||keyCode == 40)
    {
       var id_value=obj.getAttribute("id");
       if(id_value=="")
         id_value="1";
       var focus_id="1";
       var fObj;    
       var int_id=parseInt(id_value);
       if(keyCode == 37)//← 
       {
         if(int_id==1)
         {
           fObj=document.getElementById("1");  
         }else
         {
           focus_id=int_id-1;
           fObj=document.getElementById(focus_id);
         }
       }else if(keyCode == 38)//↑
       {
          if(int_id>inNum)
          {
            
            int_id=int_id-inNum;
            fObj=document.getElementById(int_id);  
          }else
          {
            fObj=document.getElementById(id_value);  
          }
       }else if(keyCode == 39)//→ 
       {
          if(int_id==row*inNum)
          {
            fObj=document.getElementById(focus_id);        
          }else
          {
            focus_id=int_id+1;
            fObj=document.getElementById(focus_id);
          }
       }else if(keyCode == 40)//↓ 
       {
           var end_row_num=(row-1)*inNum
           if(int_id<end_row_num)
           {
             focus_id=int_id+inNum;
             fObj=document.getElementById(focus_id);
           }else if(int_id==row*inNum)
           {
              fObj=document.getElementById("1");
           }else
           {
             focus_id=inNum-(row*inNum)%int_id+1;
             fObj=document.getElementById(focus_id);
           }
       }
       fObj.focus(); 
       return false;
    } 
   else 
     return true;  
  } 
  function inputNum(n)
  {
   num=n;      
  }
   function returnS(s){
     row=s;
   }
   function returnLine(l){
     line=l;
   }
   function returnInNum(n){
     inNum=n;
   }   
</script> 
  
<html:form action="/kq/register/single_register">
<script language="javascript"> 
   function getSelect(columns,code)
   {  	  
     var i=0;
     var r=0;	    
     var y=0;
     var st=0;
     st=columns.lastIndexOf(","); 
     if(st==columns.length-1)   
       columns=columns.substring(0,st); 
     columns=columns+",";
     var forms= new Array();
     var hashvo=new HashMap();//new ParameterSet();
     while(i!=-1)
     {		  
	i=columns.indexOf(",",r);
	if(i!=-1){
	   var str=columns.substring(r,i);	   
	   if(!isArray($F(str))){
	       var d=new Array();
               d=$F(str).split(",");
               forms[y]=d;              
	    }else{
	      forms[y]=$F(str);
	   }	   	        
	   y++;
	}
        r=i+1;	       	        
     }	  
     
     hashvo.put("forms",forms);	
     hashvo.put("columns",columns);
     hashvo.put("code",code);
     hashvo.put("kind","${singleRegisterForm.kind}");
     var waitInfo=eval("wait");       
     waitInfo.style.display="block";  
     Rpc({functionId:'15301110005',timeout:900000000,async:true,success:showSelect},hashvo); 	
     //var request=new Request({method:'post',onSuccess:showSelect,functionId:'15301110005'},hashvo);
   }	
  function isArray(obj) 
  { 
      return (obj.constructor.toString().indexOf('Array')!= -1);
  } 
   function showSelect(outparamters)
  { 
     //var tes=outparamters.getValue("type");
     var value=outparamters.responseText;
	 var map=Ext.decode(value);
	 var tes = map.type;
	 var waitInfo=eval("wait");       
     waitInfo.style.display="none"; 
     if(tes=="success"){
        alert("数据保存成功");
     }else if(tes=="nosave"){
       alert("考勤数据已报批,不能修改");
     }else{
        alert("数据保存失败");
     }
  }
  function IsDigit() 
  { 
    return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
  } 
</script>
<script language="javascript">
    var bean_value
    function setbean(workdate)
   {
     var hashvo=new ParameterSet();
     hashvo.setValue("workdate",workdate);	
     
     hashvo.setValue("restdate","${singleRegisterForm.rest_date}");
     hashvo.setValue("b0110","${singleRegisterForm.b0110_value}");        	
     var request=new Request({method:'post',onSuccess:getBean,functionId:'15301110999'},hashvo);
   }
    function getBean(outparamters)
    {
      bean_value=outparamters.getValue("onedate");      
    }
    function showBean()
    {      
      return bean_value;
    }
    function goback()
    {
      singleRegisterForm.action="/kq/register/daily_registerdata.do?b_query2=link";
      singleRegisterForm.submit();
    }
 </script> 
<table border="0" cellspacing="0"  cellpadding="0" width="100%" >
 <tr>
  <td>
   <table width="30%" border="0" cellspacing="0"  align="left" cellpadding="0">
    <tr>
      <td align= "left" nowrap>
        <bean:message key="kq.register.daily.lrtype"/>:&nbsp;
        <input name="pl_type" type="radio" value="row" onclick="return inputNum(1)" checked>
        <bean:message key="kq.register.daily.lrtyperow"/>&nbsp;        
       <input name="pl_type" type="radio" value="line" onclick="return inputNum(<bean:write name="singleRegisterForm"  property="num"/>)">
       <bean:message key="kq.register.daily.lrtypeline"/>&nbsp;
      </td>
    </tr>
    <tr>
      <td  align= "left" nowrap> 
       <hrms:codetoname name="singleRegisterForm" codevalue='b0110' codeid="UN" codeitem="codeitem"/>
       &nbsp;<bean:write name="codeitem" property="codename" />&nbsp; 
       <hrms:codetoname name="singleRegisterForm" codevalue='e0122' codeid="UM" codeitem="codeitem"/>
       &nbsp;<bean:write name="codeitem" property="codename" />&nbsp; 
       <hrms:codetoname name="singleRegisterForm" codevalue='e01a1' codeid="@K" codeitem="codeitem"/>
       &nbsp;<bean:write name="codeitem" property="codename" />&nbsp; 
       <bean:write name="singleRegisterForm" property="a0101" />&nbsp;
       </td>
    </tr>
    </table>
  </td>
 </tr>
 <tr>
  <td width="100%">
      
      <%int i=0;%>
      <%int s=0;%>
      <%int n=0;%>
      <%   
       String name=null;
       int num_s=0;
       int lock=0;
      %>
 <script language='javascript' >
		document.write("<div id=\"tbl-container\"  style='position:absolute;left:5;height:"+(document.body.clientHeight-121)+";width:99%'  >");
 </script> 
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF" >
      <thead>
         <tr>
           <logic:iterate id="element"    name="singleRegisterForm"  property="singfielditemlist"> 
             <logic:equal name="element" property="visible" value="true">
                <logic:equal name="element" property="itemtype" value="A">
                    <%if(i<lockedNum) {%>
                        <td align="center" class="TableRow" nowrap>
                        <bean:write  name="element" property="itemdesc"/>&nbsp; 
                        </td>
                   <%}else{ %>
                        <td align="center" class="TableRow" nowrap>
                        <bean:write  name="element" property="itemdesc"/>&nbsp; 
                        </td>
                  <%}
                   i++;
                   %>
                 </logic:equal>
                 <logic:notEqual name="element" property="itemtype" value="A">
                   <td align="center" class="TableRow" nowrap>
                     <hrms:textnewline text="${element.itemdesc}" len="5"></hrms:textnewline>   
                   </td>
                 </logic:notEqual>
              </logic:equal>
           </logic:iterate>         	        
         </tr>
      </thead>        
       
      <hrms:paginationdb id="element" name="singleRegisterForm" sql_str="singleRegisterForm.sqlstr" table="" where_str="singleRegisterForm.strwhere" columns="singleRegisterForm.columns" order_by="singleRegisterForm.orderby" pagerows="31" page_id="pagination">
         
          <%
          if(i%2==0){ 
          %>
          <tr class="trShallow" onClick="javascript:tr_onclick(this,'')">
          <%
          }else{
          %>
          <tr class="trDeep" onClick="javascript:tr_onclick(this,'DDEAFE')">
          <%}i++; 
            
          %>  
           <% int  inNum=0;lock=0;%>      
           <bean:define id="q03z0" name="element" property="q03z0" scope="page"></bean:define>  
           <logic:iterate id="info" name="singleRegisterForm"  property="singfielditemlist"> 
               
                  <logic:equal name="info" property="visible" value="false">
                     <html:hidden name="element" property="${info.itemid}"/>  
                  </logic:equal>  
                  <logic:equal name="info" property="visible" value="true">
                <!--字符型-->
                    
                      <logic:equal name="info" property="itemtype" value="A">
                        <%if(lock<lockedNum) {%>
                         <td align="left" class="RecordRow" nowrap>
                       <%}else{ %>
                          <td align="left" class="RecordRow" nowrap>
                        <% }
                       lock++;
                        %>
                         <logic:notEqual name="info" property="codesetid" value="0">
                            
                               <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
                               &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;  
                               <html:hidden name="element" property="${info.itemid}"/> 
                         </logic:notEqual>
                         <logic:equal name="info" property="codesetid" value="0">
                            <logic:notEqual name="info" property="itemid" value="q03z0">                             
                               &nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp; 
                                  <html:hidden name="element" property="${info.itemid}"/>                                 
                             </logic:notEqual>                            
                             <logic:equal name="info" property="itemid" value="q03z0">
                                 &nbsp;  
                                <script language="javascript">                                   
                                 setbean('<bean:write name="element" property="${info.itemid}" filter="true"/>'); 
                                 document.writeln(showBean());
                                </script> 
                                 &nbsp; 
                                    <html:hidden name="element" property="${info.itemid}" styleClass="text"/>&nbsp;                                 
                             </logic:equal> 
                          </logic:equal>
                         </td>
                      </logic:equal>
                      <!--字符型-->
                   <!--数字-->
                      <logic:equal name="info" property="itemtype" value="N">
                       <%
                         num_s++;
                         request.setAttribute("num_s",num_s+""); 
                       %>
                         <td align="center" class="RecordRow" nowrap>
                         <logic:notEqual name="singleRegisterForm" property="up_dailyregister" value="1"> <!--允许修改  -->           
                         <%if(userView.hasTheFunction("2702026") || userView.hasTheFunction("0C3101") || userView.isSuper_admin()) {%>
                           <logic:notEqual name="element" property="q03z5" value="01">
                            <logic:notEqual name="element" property="q03z5" value="07"> 
                                 <logic:greaterThan name="element" property="${info.itemid}" value="0">
	                           	&nbsp;&nbsp;&nbsp;<html:text name="element" property="${info.itemid}"   size="8" styleClass="text" style="text-align:right;border:0" styleId='${num_s}' onkeydown="return inputType(this,event)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>&nbsp; 
                                </logic:greaterThan>                                 
                                <html:hidden name="element" property="${info.itemid}" styleClass="text"/>&nbsp; 
                             </logic:notEqual> 
                          </logic:notEqual> 
                          <logic:equal name="element" property="q03z5" value="07"> 
                              <logic:notEqual name="element" property="${info.itemid}" value="0"> 
                                 &nbsp;<html:text name="element" property="${info.itemid}" size="8" styleClass="text" style="text-align:right;" styleId='${num_s}' onkeydown="return inputType(this,event)" onchange="checkValue(this,'${info.itemid}','${singleRegisterForm.userbase}','${singleRegisterForm.a0100}','${q03z0}');" onkeypress="event.returnValue=IsDigit();"/>&nbsp; 
                              </logic:notEqual>
                               <logic:equal name="element" property="${info.itemid}" value="0">    
                                 &nbsp;<html:text name="element" property="${info.itemid}" value="" size="8" styleClass="text" style="text-align:right;" styleId='${num_s}' onkeydown="return inputType(this,event)" onchange="checkValue(this,'${info.itemid}','${singleRegisterForm.userbase}','${singleRegisterForm.a0100}','${q03z0}');" onkeypress="event.returnValue=IsDigit();"/>&nbsp; 
                               </logic:equal>
                           </logic:equal>
                          <logic:equal name="element" property="q03z5" value="01">
                              <logic:notEqual name="element" property="${info.itemid}" value="0"> 
                               &nbsp;<html:text name="element" property="${info.itemid}" size="8" styleClass="text" style="text-align:right;" styleId='${num_s}' onkeydown="return inputType(this,event)" onchange="checkValue(this,'${info.itemid}','${singleRegisterForm.userbase}','${singleRegisterForm.a0100}','${q03z0}');" onkeypress="event.returnValue=IsDigit();"/>&nbsp; 
                              </logic:notEqual>
                              <logic:equal name="element" property="${info.itemid}" value="0">    
                                &nbsp;<html:text name="element" property="${info.itemid}" value="" size="8" styleClass="text" style="text-align:right;" styleId='${num_s}' onkeydown="return inputType(this,event)" onchange="checkValue(this,'${info.itemid}','${singleRegisterForm.userbase}','${singleRegisterForm.a0100}','${q03z0}');" onkeypress="event.returnValue=IsDigit();"/>&nbsp; 
                              </logic:equal>
                           </logic:equal>
                         <% } else { %>
                         	 <logic:notEqual name="element" property="${info.itemid}" value="0">  
	                           	&nbsp;<html:text name="element" property="${info.itemid}"   size="8" styleClass="text" styleId='${num_s}' onkeydown="return inputType(this,event)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>&nbsp; 
	                         </logic:notEqual>
	                         <logic:equal name="element" property="${info.itemid}" value="0">                                                                                                   
	                              &nbsp;<html:text name="element" property="${info.itemid}"  value="" size="8" styleClass="text" styleId='${num_s}' onkeydown="return inputType(this,event)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>&nbsp; 
	                         </logic:equal>
                         <% } %>
                         </logic:notEqual>
                         
                         <logic:equal name="singleRegisterForm" property="up_dailyregister" value="1"><%--不允许修改--%>
                         	 <logic:notEqual name="element" property="${info.itemid}" value="0">  
	                           	&nbsp;<html:text name="element" property="${info.itemid}"   size="8" styleClass="text" styleId='${num_s}' onkeydown="return inputType(this,event)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>&nbsp; 
	                         </logic:notEqual>
	                         <logic:equal name="element" property="${info.itemid}" value="0">                                                                                                   
	                              &nbsp;<html:text name="element" property="${info.itemid}"  value="" size="8" styleClass="text" styleId='${num_s}' onkeydown="return inputType(this,event)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>&nbsp; 
	                         </logic:equal>
                         </logic:equal> 
                              <%
                                inNum++;
                              %>                              
                         </td>
                    </logic:equal>
                    <!--数字结束-->
                    <logic:equal name="info" property="itemtype" value="D">
                  	<td align="center" class="RecordRow" nowrap>
						<logic:equal value="18" name="info" property="itemlength">
                	    	  &nbsp;<html:text name="element" property='${info.itemid}' size="18" maxlength="18" styleClass="text" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,true);' onchange="rep_dateValue(this);" readonly="true"/>&nbsp;
                      	</logic:equal>
                      	<logic:notEqual value="18" name="info" property="itemlength">
               	    	  	  &nbsp;<html:text name="element" property='${info.itemid}' size="15" maxlength="10" styleClass="text" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this,'${info.itemlength}');" readonly="true"/>&nbsp;
                      	</logic:notEqual>                  	
                   </td>
                  </logic:equal>
                  <logic:equal name="info" property="itemtype" value="M">
                  	<td align="center" class="t_cell_locked_b2" nowrap>
                  	    &nbsp;
                  	</td>
                  </logic:equal>                                             
               </logic:equal> 
                                    
            </logic:iterate>  
            <%
            if(inNum!=0){
              n=inNum;
            }
            %>         
          </tr>
           <%
           s++;
           %>
        </hrms:paginationdb>
        <script language="javascript"> 
            returnS(<%=s%>); 
        </script> 
        <script language="javascript"> 
            returnLine(<bean:write name="dailyRegisterForm"  property="num"/>); 
        </script> 
        <script language="javascript"> 
            returnInNum(<%=n%>); 
        </script>   	                           	    		        	        	        
      </table>
   <script language='javascript' >
	document.write("</div>");
    </script>   
     </td>
   </tr> 
   <tr>
   <td>
    <script language='javascript' >
		document.write("<div  id='page'  style='position:absolute;left:5;top:"+(document.body.clientHeight-75)+";width:99%'  >");
	</script>
     <table  width="100%"  class="RecordRowP" >
       <tr>
          <td width="40%" valign="bottom"  class="tdFontcolor" nowrap height="35px;">
             第<bean:write name="pagination" property="current" filter="true" />页
             共<bean:write name="pagination" property="count" filter="true" />条
             共<bean:write name="pagination" property="pages" filter="true" />页
	     </td>
	     <td  width="60%" align="right" valign="bottom" nowrap class="tdFontcolor">
	      <p align="right"><hrms:paginationdblink name="singleRegisterForm" property="pagination" nameId="singleRegisterForm" scope="page">
             </hrms:paginationdblink>
	     </td>
	  </tr>	
     </table>
     <table width="100%">
       <tr>
	   <td width="60%" align="left"  nowrap>
	   <!-- 28427 数据编辑完后自动保存，故保存按钮没有意义 注释掉
	     <hrms:priv func_id="2702025,0C3101">  
	       <input type="button" name="b_save" value='<bean:message key="kq.emp.button.save"/>' onclick="getSelect('<bean:write name="singleRegisterForm"  property="columns"/>','<bean:write name="singleRegisterForm"  property="code"/>');" class="mybutton">  	
	     </hrms:priv>
	     -->   	                 
               <input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="goback();" class="mybutton">						      
           </td>
	   <td width="40%"></td>
      </tr>
     </table>
      <script language='javascript' >
	  document.write("</div>");
    </script>
   </td>
 </tr>
</table>
</html:form>
<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td id="wait_title" class="td_style common_background_color" height="24"><bean:message key="classdata.isnow.wiat"/></td>
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
