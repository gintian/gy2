<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_accounting.bankdisk.BankDiskForm,com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
 <%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}

	BankDiskForm bankDiskForm=(BankDiskForm)session.getAttribute("bankDiskForm");
     String model=bankDiskForm.getModel();//history 表示为薪资历史数据分析进入
	/* 薪资发放-人员筛选-下一步 直接关闭当前窗口的问题 xiaoyun 2014-9-23 start */
	String filter_sql = bankDiskForm.getFilterSql();
	if(StringUtils.isNotEmpty(filter_sql)) {
		filter_sql=PubFunc.encrypt(filter_sql);
	}	
	/* 薪资发放-人员筛选-下一步 直接关闭当前窗口的问题 xiaoyun 2014-9-23 end */
%>
<script type="text/javascript">
<!--


var model="gz";
<% if("history".equalsIgnoreCase(model)){ %>
model="history";
<%}%>

function bankdisk_queryResult()
{
 <%int n=0;%> 
  <logic:iterate id="element" name="bankDiskForm" property="personFilterList" indexId="index"> 
	   	      	    
             <logic:equal name="element" property="fieldtype" value="N">
               var a<%=n%>=document.getElementsByName("personFilterList[<%=n%>].value");
               if(a<%=n%>[0].value !=''){
                  var myReg =/^(-?\d+)(\.\d+)?$/
	        	  if(!myReg.test(a<%=n%>[0].value)) 
	        	   {
	            	    alert("<bean:write  name="element" property="hz"/>"+GZ_BANKDISK_INFO4+"!");
	            	    return;
	         	   }
         		}
		</logic:equal>
             
        
	         
        <%n++;%>
 </logic:iterate>
bankDiskForm.action="/gz/gz_accountingt/bankdisk/personFilterResult.do?b_query=query&type="+model;
bankDiskForm.submit();

}
function bankdisk_isClose(){
var sql ="<%=filter_sql%>";
var isclose="${bankDiskForm.issave}";
var conid="${bankDiskForm.filterCondId}";
var obj=new Object();
if(parseInt(isclose)==2)
{
   obj.isclose=isclose;
   obj.sql=sql; 
   obj.condid=conid;
   returnValue=obj;
  // window.close();
}
if(!(sql==null||trim(sql).length==0))
{
obj.sql=sql
if(conid!=null&&trim(conid).length>0)
{
   obj.condid=conid;
}
returnValue=obj;
window.close();
}
}
var date_desc;
function setSelectValue()
   {
     if(date_desc)
     {
       date_desc.value=$F('date_box');
       Element.hide('date_panel');   
     }
   }
function showDateSelectBox(srcobj)
   {
       //if(event.button==2)
       //{
          date_desc=srcobj;
          Element.show('date_panel');   
          var pos=getAbsPosition(srcobj);
	  with($('date_panel'))
	  {
	        style.position="absolute";
    		style.posLeft=pos[0]-1;
		style.posTop=pos[1]-1+srcobj.offsetHeight;
		style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
          }                 
       //}
   }
   function bankdisk_savecond(str)
   {
      var one=document.getElementById("hz0");
      var desc=one.innerHTML;
      var two=document.getElementsByName("personFilterList["+0+"].oper");
      var oper=two[0].value;
      var three=document.getElementsByName("personFilterList["+0+"].hzvalue");
      if(three==null||three.length==0)
      {
         three=document.getElementsByName("personFilterList["+0+"].value");
      }
     var value=three[0].value;
     var theArr=new Array(desc,oper,value); 
     var thecodeurl ="/gz/gz_accountingt/bankdisk/personFilter.do?br_input=link";
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    var return_vo= window.showModalDialog(iframe_url,theArr, 
              "dialogWidth:350px; dialogHeight:190px;resizable:no;center:yes;scroll:yes;status:no");
     if(return_vo==null)
     {
       return;
     }
     var nameobj=new Object();
     nameobj.name=return_vo.name;
     name = nameobj.name;
     if(trim(name).length==0)
     {
         alert(GZ_BANKDISK_INFO5);
         return;
     }
     bankDiskForm.condName.value=name;
     bankDiskForm.action="/gz/gz_accountingt/bankdisk/personFilter.do?b_save=save&model="+model;
     bankDiskForm.submit();
      }
function checkExpr(type,size)
{
   <%int t=0;%> 
   var arr=new Array();
  <logic:iterate id="element" name="bankDiskForm" property="personFilterList" indexId="index"> 
	   	      	    
             <logic:equal name="element" property="fieldtype" value="N">
               var a<%=t%>=document.getElementsByName("personFilterList[<%=t%>].value");
               if(a<%=t%>[0].value !=''){
                  var myReg =/^(-?\d+)(\.\d+)?$/
	        	  if(!myReg.test(a<%=t%>[0].value)) 
	        	   {
	            	    alert("<bean:write  name="element" property="hz"/>"+GZ_BANKDISK_INFO4+"!");
	            	    return;
	         	   }
         		}
		</logic:equal>
		var a<%=t%>=document.getElementsByName("personFilterList[<%=t%>].value");
         var obj=new Object();
         obj.value=a<%=t%>[0].value;
         var bb= document.getElementsByName("personFilterList[<%=t%>].oper")[0]; 
         for(var i=0;i<bb.options.length;i++)
         {
            if(bb.options[i].selected){
               obj.oper=bb.options[i].value;
               break;
            }
         } 
         obj.fieldname="<bean:write  name="element" property="fieldname"/>";
         obj.log="";
        arr[<%=t%>]=obj;	         
        <%t++;%>
 </logic:iterate>
    var expr = bankDiskForm.expr.value;
    var hashvo=new ParameterSet();
    hashvo.setValue("expr",expr);
    hashvo.setValue("size",size);
    hashvo.setValue("type",type);
    hashvo.setValue("arr",arr);
    hashvo.setValue("sid",document.getElementById("sid").value);
    hashvo.setValue("tableName",document.getElementById("tn").value);
    hashvo.setValue("fromflag",model);
   	var request=new Request({asynchronous:false,onSuccess:check_ok,functionId:"3020100021"},hashvo);
} 
function check_ok(outparameters)
{

   var type=outparameters.getValue("type");
   var info=outparameters.getValue("info");
   if(info=='0')
   {
      if(type=='1')
      {
        bankdisk_savecond(GZ_BANKDISK_INFO6);
      }
      if(type=='2')
      {
         bankdisk_queryResult();
      }
   }
   else
   {
     alert(info);
      return;
   }
}
function insertText(strtxt)
{
   var expr_editor=$("expr");
   expr_editor.focus();
   var element = document.selection;
   if (element!=null) 
   {
    var rge = element.createRange();
		if (rge!=null)	
	        rge.text=strtxt;
   }
}
function check()
{
    var code=window.event.keyCode;
    //106.109.107
    var ret=true;
    if(code==106||code==109||code==107)
    {
    }
    else if(code==8||code==46)
    {
    }
   else if(97<=code&&code<=105)
   {
      
   }else if(48<=code&&code<=57)
   {
   }
   else
   { 
        if((window.event.shiftKey)&&(code==48||code==49||code==57||code==56||code==187))
        {
        }
        else
        {
           window.event.returnValue=false;
        }
     
   }
}
function ctrlKey()
{
    var code =window.event.keyCode;
    if((window.event.shiftKey)&&code==222)
    {
        window.event.returnValue=false;
    }
}
//-->
</script>
<html:form action="/gz/gz_accountingt/bankdisk/personFilter">
<%if("hl".equals(hcmflag)){ %>
<br>
<%}%>

<table width="570px;" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-right: 3px;">
<thead>
<tr height="20">
       		<td align='left' class='TableRow' colspan='4'><bean:message key="gz.bankdisk.querycondition"/></td>
       		</tr>
<tr>
<td align="center"  class="TableRow"><bean:message key="gz.bankdisk.sequencenumber"/></td><td align="center"  class="TableRow"><bean:message key="gz.bankdisk.queryfield"/></td><td align="center"  class="TableRow"><bean:message key="gz.bankdisk.relationcharacter"/></td><td align="center"  class="TableRow"><bean:message key="gz.bankdisk.queryvalue"/></td>
</tr>
</thead>
<% int i=0;%>
<logic:iterate id="element" name="bankDiskForm" property="personFilterList" indexId="index">
<tr>
 <td align="center" class="RecordRow" nowrap >
          <%=i+1%>
 </td>
     <td align="center" id=<%="hz"+i%> class="RecordRow" nowrap >
     <bean:write name="element" property="hz"/>                       
      </td> 
   <td align="center" class="RecordRow" nowrap >
     <html:select name="bankDiskForm" property='<%="personFilterList["+i+"].oper"%>' size="1">
  <html:optionsCollection property="operlist" value="dataValue" label="dataName"/>                                   
  </html:select>
    </td>  
         <!--日期型 -->                            
                          <logic:equal name="element" property="fieldtype" value="D">
                            <td align="left" class="RecordRow" nowrap> 
 <html:text name="bankDiskForm" property='<%="personFilterList["+i+"].value"%>'  size="20"  maxlength="${element.itemlen}" ondblclick="showDateSelectBox(this);" onblur="Element.hide('date_panel');" onkeydown="ctrlKey();"/>         
				
				</td>                                        
                          </logic:equal>
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="fieldtype" value="A">
                            <td align="left" class="RecordRow" nowrap>
                              <logic:notEqual name="element" property="codeid" value="0">
                              <html:hidden name="bankDiskForm" property='<%="personFilterList["+i+"].value"%>'/>
                                 <html:text name="bankDiskForm" property='<%="personFilterList["+i+"].hzvalue"%>'  size="20"  maxlength="${element.itemlen}" onchange="fieldcode(this,1)" onkeydown="ctrlKey();"/>         
                                <img src="/images/code.gif" style="vertical-align:-15%;" onclick='openCondCodeDialogsx("${element.codeid}","<%="personFilterList["+i+"].hzvalue"%>");'/>
                              </logic:notEqual>               
                              <logic:equal name="element" property="codeid" value="0">
                              <html:text name="bankDiskForm" property='<%="personFilterList["+i+"].value"%>' size="20" maxlength='${element.itemlen}' onkeydown="ctrlKey();"/>                                 
                              </logic:equal>                               
                            </td>                           
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="fieldtype" value="N">
                            <td align="left"  class="RecordRow" nowrap>    
                            <html:text name="bankDiskForm" property='<%="personFilterList["+i+"].value"%>' size="20" maxlength='${element.itemlen}' onkeydown="ctrlKey();"/>                      
                            </td>                           
                          </logic:equal>    
                          <!--备注型--> 
                          <logic:equal name="element" property="fieldtype" value="M">
                            <td align="left" class="RecordRow" nowrap>    
                            <html:text name="bankDiskForm" property='<%="personFilterList["+i+"].value"%>' size="20" maxlength="${element.itemlen}" onkeydown="ctrlKey();"/>                      
                            </td>                           
                          </logic:equal>                   
                          
           
  </tr>                      
                          
   <%i++;%>  
</logic:iterate>
<tr>
<td colspan="4" align="left" class="RecordRow" nowrap>
<bean:message key="gz.bankdisk.factorexpression"/>
</td></tr>
<tr>
<td align="center" colspan="4" class="RecordRow" nowrap>
<html:textarea name="bankDiskForm" property="expr" rows="4" cols="90" onkeydown="check();"></html:textarea>
</td>
</tr>
<tr height="35px">
<td colspan="4" align="left" class="RecordRow" nowrap style="padding-top:2px;padding-bottom:2px;">
<input type="button" value="<bean:message key="gz.bankdisk.moveover"/>" name="" class="smallbutton" onclick="insertText('*');"/>
<input type="button" value="<bean:message key="gz.bankdisk.or"/>" name="" class="smallbutton" onclick="insertText('+');"/>
<input type="button" value="<bean:message key="gz.bankdisk.not"/>" name="" class="smallbutton" onclick="insertText('!');"/>
<input type="button" value="(" name="" class="smallbutton" onclick="insertText('(');"/>
<input type="button" value=")" name="" class="smallbutton" onclick="insertText(')');"/>
</td>
</tr>
<tr height="35px">
<td colspan="4" align="center" class="RecordRow" style="padding-top:2px;padding-bottom:2px;">
<input type="button" class="mybutton" name="query" value="<bean:message key="button.ok"/>" onclick="checkExpr('2','<%=i%>');"/>
<hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.query.pre"/>
	         </hrms:submit>
	         
<input type="button" class="mybutton" name="save" value="<bean:message key="button.save"/>" onclick="checkExpr('1','<%=i%>');"/>

<input type="button" class="mybutton" name="cancel" value="<bean:message key="button.cancel"/>" onclick="window.close();"/>
<input type="hidden" name="salaryid" id="sid" value="${bankDiskForm.salaryid}"/>
<input type="hidden" name="tableName" id="tn" value="${bankDiskForm.tableName}"/>
<input type="hidden" name="rightFields" value="${bankDiskForm.rightFields}"/>
<input type="hidden" name="condName" value=""/>
</td>
</tr>
</table>

          <div id="date_panel">
   			<select name="date_box" multiple="multiple" size="10"  style="width:200" onchange="setSelectValue();">    
			    <option value="$YRS[10]"><bean:message key="gz.bankdisk.yearlimit"/></option>
			    <option value="<bean:message key="gz.bankdisk.currentyear"/>"><bean:message key="gz.bankdisk.currentyear"/></option>
			    <option value="<bean:message key="gz.bankdisk.currentmonth"/>"><bean:message key="gz.bankdisk.currentmonth"/></option>
			    <option value="<bean:message key="gz.bankdisk.currentday"/>"><bean:message key="gz.bankdisk.currentday"/></option>					    
			    <option value="<bean:message key="gz.bankdisk.today"/>"><bean:message key="gz.bankdisk.today"/></option>
			    <option value="<bean:message key="gz.bankdisk.stopdate"/>"><bean:message key="gz.bankdisk.stopdate"/></option>
                <option value="1992.4.12">1992.4.12</option>	
                <option value="1992.4">1992.4</option>	
                <option value="1992">1992</option>			    
			    <option value="????.??.12">????.??.12</option>
			    <option value="????.4.12">????.4.12</option>
			    <option value="????.4">????.4</option>			    			    		    
                        </select>
                    </div>
</html:form>
<script type="text/javascript">
<!--
 bankdisk_isClose();
 Element.hide('date_panel');
//-->
</script>
