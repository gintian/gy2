<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script type="text/javascript">
<!--
function addToTarget(source,targetSource,type)
{
    var sourceObj=document.getElementById(source);
    var targetObj = document.getElementById(targetSource);
    var num=0;
    var count=0;
    var flag=true;
    var stt='';
    for(var i=0;i<sourceObj.options.length;i++)
    { 
        flag=true;
        if(sourceObj.options[i].selected)
        {
            
              var arr=sourceObj.options[i].value.split("/");
              if(type==1&&arr[1]!='A')
              {		 
                  count++;
                  continue;
              }
               if(type==2&&arr[1]=='N')
              {
                  count++;
                  continue;
              }
            if(type==3&&arr[1]!='N')
              { 
                  count++;
                  continue;
              }
             for(var j=0;j<targetObj.options.length;j++)
             {
                 if(targetObj.options[j].value==sourceObj.options[i].value)
                 {
                     num++;
                     flag=false;
                     break;
                 }
             }
             if(flag)
             {
                 var opt=new Option();
                 opt.value=sourceObj.options[i].value;
                 opt.text=sourceObj.options[i].text;
                 targetObj.add(opt);
               	 stt=stt+","+i;
             }
            
        }
    }
     var arr=stt.split(",");
     var i=0;
     for(var k=1;k<arr.length;k++){
     	if(k==1){
     		i++;
     	 	sourceObj.options.remove(arr[k]);
     	 }else{
     	 	sourceObj.options.remove(arr[k]-i);
     	 	i++;
     	 }
     }
    if(count>0&&num>0)
    {
        if(type==1||type==2)
        {
           alert("系统自动过滤了重复选择的和非字符型的指标");
        }else{
           alert("系统自动过滤了重复选择的和非数值型的指标");
        }
        return;
    }else if(count>0)
    {
         if(type==1||type==2)
        {
           alert("系统自动过滤了非字符型的指标");
        }else{
           alert("系统自动过滤了非数值型的指标");
        }
        return;
    }
    else if(num>0)
    {
       
        alert("系统自动过滤了重复选择的指标");
        return;
    } 
}
	<% 
	if(request.getParameter("opt")!=null&&request.getParameter("opt").trim().equalsIgnoreCase("new"))
	{
	%>
		//window.open("/hire/demandPlan/positionDemand/getNeedFields.do?br_execute=execute","_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=yes,resizable=yes,status=yes");
		
		 	window.dialogWidth=screen.availWidth;
		 	window.dialogHeight=screen.availHeight;
		//window.showModalDialog("/hire/demandPlan/positionDemand/getNeedFields.do?br_execute=execute","","dialogWidth:"+screen.availWidth+"px;dialogHeight:"+screen.availHeight+"px;status:no; help:no;scroll:no");
	
		  window.location.href="/hire/demandPlan/positionDemand/getNeedFields.do?br_execute=execute";
		
	<%
	}
	%>

function selectOK(obj)
{
   var group=document.getElementById("rf2");
   var count=document.getElementById("rf");
   var count1=document.getElementById("rf1");
   var groupField="";
   var countField="";
   var countField1="";
   for(var i=0;i<group.options.length;i++)
   {
      groupField+="`"+group.options[i].value+"/"+group.options[i].text;
   }
   for(var i=0;i<count.options.length;i++)
   {
      countField+="`"+count.options[i].value+"/"+count.options[i].text;
   }
   for(var i=0;i<count1.options.length;i++)
   {
      countField1+="`"+count1.options[i].value+"/"+count1.options[i].text;
   }
 	if(groupField=='')
   {
      
   }
   if(countField=='')
   {
      alert("请选择行指标！");
      return;
   }
   if(countField1=='')
   {
      alert("请选择结果指标！");
      return;
   }
   var waitInfo=eval("wait");
	waitInfo.style.display="block";
	obj.disabled="true";
  document.positionDemandForm.lineFields.value=groupField;	
  document.positionDemandForm.lieFields.value=countField;	
  document.positionDemandForm.resultFields.value= countField1;	
  positionDemandForm.action="/hire/demandPlan/positionDemand/getNeedFields.do?b_execute=execute&opt=new";
  positionDemandForm.target="_self";		
  positionDemandForm.submit();
}
//exportok 这个方法没有用写在这里做什么？
function removeitem1(sourcebox_id)
{
  var vos,right_vo,i;
  vos= document.getElementsByName(sourcebox_id);
  var sourceObj=document.getElementById("zpNeedsField");
  if(vos==null)
  	return false;
  right_vo=vos[0];
  for(i=right_vo.options.length-1;i>=0;i--)
  {
    if(right_vo.options[i].selected)
    {
    	//alert(i);
     var opt=new Option();
     opt.value=right_vo.options[i].value;
     opt.text=right_vo.options[i].text;
     sourceObj.add(opt);
	right_vo.options.remove(i);
	 
    }
    
  }
  
  return true;	  	
}
//-->
</script>
<style>
.toptable{
    width:97%;
    position:absolute;
    left:20px;
    top:10px;
}
</style>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
    String classname="toptable";
    if(bosflag!=null&&bosflag.equals("hcm")){
        classname="tablelayout";
%>
    <link href="/hire/css/layout.css" rel="stylesheet" type="text/css">
<%
    }
 
%>
<html:form action="/hire/demandPlan/positionDemand/getNeedFields">
<table border="0" cellspacing="0"  align="center" cellpadding="1" class="ListTable <%=classname%>">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap>
		<bean:message key="label.query.selectfield"/>&nbsp;&nbsp;<!-- 选择指标 --> 
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap >
              <table width="100%">
                <tr  valign="top">
                 <td align="center"  width="46%"  valign="top">
                   <table align="center" width="100%">
                    <tr>
                      <td align="left" width="90%">
                         &nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="selfservice.query.queryfield"/><!--备选指标 --> 
                      </td>
                    </tr>
                   <tr>
                       <td align="center">
                       <select  id="zpNeedsField"  style="height:450px;width:90%;font-size:9pt" multiple="multiple" name="left_fields">
  							<logic:iterate id="element" name="positionDemandForm" property="zpNeedsFieldsList"  > 
	               	 			<option value='<bean:write name="element" property="dataValue" />' ><bean:write name="element" property="dataName" /></option>             
	              		 	</logic:iterate>
  					</select>
                       		
                       </td>
                    </tr>
                   </table>
                </td>   
                    
                <td align="center">
                <table align="center" width="100%" >
                <tr height="150">
	                 <td width="8%" align="center" valign="middle" class="buttonpadding">
	                	 <input type=button class="mybutton buttonmargin" name="save" id='b_addfield' value='<bean:message key="button.setfield.addfield"/>' onclick="addToTarget('zpNeedsField','right_fields1',1);">
		               <br>
		                <input type=button class="mybutton buttonmargin" name="save" id='b_delfield' value='<bean:message key="button.setfield.delfield"/>' onclick="removeitem1('right_fields1');">  
		            </td>
		            <td>
		            </td>
                </tr>
                 <tr height="160">
	                 <td width="8%" align="center" valign="middle">
	                	 <input type=button class="mybutton buttonmargin" name="save" id='b_addfield' value='<bean:message key="button.setfield.addfield"/>' onclick="addToTarget('zpNeedsField','right_fields2',2);">
		               <br>
		                <input type=button class="mybutton buttonmargin" name="save" id='b_delfield' value='<bean:message key="button.setfield.delfield"/>' onclick="removeitem1('right_fields2');">  
		            </td>
                </tr>
                  <tr height="150">
	                  <td width="8%" align="center" valign="middle">
	                	 <input type=button class="mybutton buttonmargin" name="save" id='b_addfield' value='<bean:message key="button.setfield.addfield"/>' onclick="addToTarget('zpNeedsField','right_fields3',3);">
		               <br>
		                <input type=button class="mybutton buttonmargin" name="save" id='b_delfield' value='<bean:message key="button.setfield.delfield"/>' onclick="removeitem1('right_fields3');">  
		            </td>
                </tr>
                </table>
                </td>
                 <!--  button -->  
                <td width="46%" align="left"  valign="top">
                 <table width="100%">
	                  <tr  valign="top" >
		                  <td width="100%" align="left"  valign="top">
		                    列指标
		                  </td>
	                  </tr>
	                  <tr>          
	                  	<td width="100%" align="left" valign="top">		     		       
			              <select  size="7" id='rf2' name="right_fields1" multiple="multiple" ondblclick="removeitem1('right_fields1');" style="height:125px;width:90%;font-size:9pt">		              
			              </select>			    
	  					</td>  
	                  </tr>
                  </table>   
                   <table width="100%">
	                  <tr>
		                  <td width="100%" align="left"  valign="top">
		                     行指标
		                  </td>
	                  </tr>
	                  <tr>
		                  <td width="100%" align="left"  valign="top"> 		      		  
				              <select  size="7" id='rf' name="right_fields2" multiple="multiple" ondblclick="removeitem1('right_fields2');" style="height:130px;width:90%;font-size:9pt">		              
				              </select>			    
		  				  </td>
	                  </tr>
                  </table>  
                  <table width="100%">
	                  <tr>
		                  <td width="100%" align="left"  valign="top">
		                     结果指标
		                  </td>
	                  </tr>
	                  <tr>
		                  <td width="100%" align="left" valign="top"> 		      		  
				              <select  size="7" id='rf1' name="right_fields3" multiple="multiple" ondblclick="removeitem1('right_fields3');" style="height:125px;width:90%;font-size:9pt">		              
				              </select>			    
		  				  </td>
	                  </tr>
                  </table>              
                </td>               
                </tr>
              </table> 
              	<input type='hidden' name="lineFields" value="${positionDemandForm.lineFields}"/>  
              	<input type='hidden' name="lieFields" value="${positionDemandForm.lieFields}"/>   
              	<input type='hidden' name="resultFields" value="${positionDemandForm.resultFields}"/>    
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" style="height:35px;" nowrap>
              <html:button styleClass="mybutton" property="b_next" onclick="selectOK(this);">
            		      <bean:message key="reporttypelist.confirm"/>
	      	</html:button> 	
	      <html:button styleClass="mybutton" property="b_close" onclick="window.close();">
            		      <bean:message key="button.close"/>
	      </html:button> 	       
          </td>
          </tr>   
</table>
	<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style"  height=24>
					<bean:message key="report.reportlist.reportqushu"/>
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10" >
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
</html:form>