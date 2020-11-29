<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%
	String model=(String)request.getParameter("model");
%>


<script language="Javascript" src="/gz/salary.js"></script>
<script type="text/javascript">
<!--

var model="${bankDiskForm.model}";
<% if("history".equalsIgnoreCase(model)){ %>
model="history";//history 表示为薪资历史数据分析进入
<%}%>
function saveSort(salaryid)
{
    var obj=document.getElementById("cl");
    if(obj)
    {
       var num=0;
       var ids="";
       for(var i=0;i<obj.options.length;i++)
       {
          num++;
          ids+="/"+obj.options[i].value;
       }
       if(num==0||num==1)
       {
          return;
       }
       var hashVo=new ParameterSet();
       hashVo.setValue("sortStr",ids.substring(1));
       hashVo.setValue("salaryid",salaryid);
       hashVo.setValue("model",model);
       var request=new Request({method:'post',asynchronous:false,onSuccess:save_ok,functionId:'30200710258'},hashVo);
    }
}
function save_ok(outparameters)
{
   var salaryid=outparameters.getValue("salaryid");
   bankDiskForm.action="/gz/gz_accounting/bankdisk/delete_filter_cond.do?b_query=query&model="+model+"&salaryid="+salaryid;
   bankDiskForm.target="_self";
   bankDiskForm.submit();
}
function select_ok(type,salaryid)
{
  var arr=document.getElementById("cl");
  var ids="";
  var num=0;
  var name="";
  if(arr.options.length==0)
  {
     return;
  }
  for(var i=0;i<arr.options.length;i++)
  {
        if(arr.options[i].selected)
        {
            ids+=","+arr.options[i].value;
            name=arr.options[i].text;
            num++;
        }
  }
 
   if(num==0)
   {
       alert(GZ_BANKDISK_INFO1);
       return;
   }
   var obj=new Object();
   obj.ids=ids.substring(1);
   obj.type=type;
   if(parseInt(type)==1)
   {
     if(ifdel())
     {
      var hashVo=new ParameterSet();
      hashVo.setValue("condid",obj.ids);
      hashVo.setValue("salaryid",salaryid);
      hashVo.setValue("model",model);
      var In_parameters="opt=1";
      var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:delete_cond_ok,functionId:'3020100019'},hashVo);
      }
   }
  if(parseInt(type)==2)//open
  {
    if(num>1)
    {
       alert(GZ_BANKDISK_INFO2);
       return;
    }
    returnValue=obj;
    window.close();
  }
  if(parseInt(type)==3)
  {
        if(num>1)
        {
            alert("一次只能重命名一个筛选条件!");
            return;
        }
        var theArr=new Array(name); 
        var thecodeurl ="/gz/gz_accountingt/bankdisk/personFilter.do?br_input=link`rename=1";
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
        var return_vo= window.showModalDialog(iframe_url,theArr, 
              "dialogWidth:350px; dialogHeight:190px;resizable:no;center:yes;scroll:no;status:no");
        if(return_vo)
        {
            var hashVo=new ParameterSet();
            hashVo.setValue("condid",obj.ids);
            hashVo.setValue("salaryid",salaryid);
             hashVo.setValue("name",getEncodeStr(return_vo.name));
             hashVo.setValue("model",model);
            var In_parameters="opt=2";
            var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:delete_cond_ok,functionId:'3020100019'},hashVo);  
        }
  }
}
function delete_cond_ok(outparameters)
{
  var salaryid=outparameters.getValue("salaryid");
  var theUrl="/gz/gz_accounting/bankdisk/delete_filter_cond.do?b_query=query&model="+model+"&salaryid="+salaryid;
  bankDiskForm.action=theUrl;
  bankDiskForm.submit();
}
//-->
</script>
<style>
<!--
.TableRow1 {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 0pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
.buttonPadding {
	padding:0 3px 0 3px;
}
-->
</style>
<html:form action="/gz/gz_accounting/bankdisk/delete_filter_cond">
<table width='295px;' border="0" cellspacing="0"  align="center" cellpadding="1" class="ListTable"><!-- modify by xiaoyun 薪资审批/显示/人员筛选：读取页面 缺线问题 2014-9-26 -->
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="3">
		<bean:message key="gz.filter.tsort"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap>
              <table>
                <tr>               
                <td width="46%" align="center">
                 <table width="100%">               
                  <tr>
                  <td width="100%" align="left">
 		        <select id="cl" name="condlist" size="10"  multiple="multiple" style="height:230px;width:100%;font-size:9pt">
 		        <logic:iterate id="element" name="bankDiskForm" property="condbeanlist" offset="0">
 		        <option value="<bean:write name="element" property="condid"/>"><bean:write name="element" property="name"/></option>
 		        </logic:iterate>
 		        </select>
                 </td>
                  </tr>
                  </table>             
                </td>
               <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_up" onclick="upItem($('condlist'));">
            		     <bean:message key="button.previous"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_down" onclick="downItem($('condlist'));">
            		     <bean:message key="button.next"/>    
	           </html:button >	     
                </td>                                               
                </tr>

              </table>             
            </td>
            </tr>
                <tr>
                <td align="center" height='35px'>
	      	         <html:button styleClass="mybutton buttonPadding" property="open" onclick="select_ok('2','${bankDiskForm.salaryid}');">
            		     <bean:message key="button.ok"/>
	               </html:button>
	                  
                    <html:button styleClass="mybutton buttonPadding" property="b_delete" onclick="saveSort('${bankDiskForm.salaryid}');">
            		      保存排序
	      			</html:button>
	      			<html:button styleClass="mybutton buttonPadding" property="ok" onclick="select_ok('3','${bankDiskForm.salaryid}');">
            		     重命名
	              </html:button>
	              <html:button styleClass="mybutton buttonPadding" property="ok" onclick="select_ok('1','${bankDiskForm.salaryid}');">
            		     <bean:message key="button.delete"/>
	              </html:button>
	      			<html:button styleClass="mybutton buttonPadding" property="b_cancel" onclick="window.close();">
            		      <bean:message key="button.close"/>
	      			</html:button> 	
                </td>
                </tr>
</table>
</html:form>