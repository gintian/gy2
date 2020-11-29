<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript"src="../../../module/utils/js/template.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
<SCRIPT LANGUAGE=javascript src="/report/report_collect/reportCollect.js"></SCRIPT>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language='javascript'>
function change()
{ 
	var hashvo=new ParameterSet();
	var a_object=eval("document.reportCollectForm.sortid");
	
	var obj2=$('operater');
	var avalue;
	for(var i=0;i<obj2.length;i++)
	{
		if(obj2[i].checked)
			avalue=obj2[i].value;
	}
	hashvo.setValue("operater",avalue);	
	hashvo.setValue("sortid",a_object.value);	
	hashvo.setValue("flag","1");
	var request=new Request({method:'post',asynchronous:false,onSuccess:returnChangeSort,functionId:'03030000032'},hashvo); 
}


function setType(operate)
{
	var hashvo=new ParameterSet();
	var a_object=eval("document.reportCollectForm.sortid");
	hashvo.setValue("operater",operate);	
	hashvo.setValue("sortid",a_object.value);
	hashvo.setValue("flag","2");	
	var request=new Request({method:'post',asynchronous:false,onSuccess:returnChangeUnit,functionId:'03030000032'},hashvo); 
}

function returnChangeUnit(outparamters)
{
	var unit_str=getDecodeStr(outparamters.getValue("unit_str"));
	var tabArray=new Array();
	
	
	vos=eval("document.reportCollectForm.unitCode");;
	for(var i=vos.options.length-1;i>=0;i--)
	{
			vos.options.remove(i);
	}
	
	if(unit_str.length>0)
	{
		if(unit_str.indexOf("#")==-1)
		{
			tabArray[0]=unit_str;
		}
		else
			tabArray=unit_str.split("#");
		vos=eval("document.reportCollectForm.unitCode");;
		for(var i=vos.options.length-1;i>=0;i--)
		{
			vos.options.remove(i);
		}
		for(var i=0;i<tabArray.length;i++)
		{
			var temp=tabArray[i].split("~");
			var o=new Option();
			o.value=temp[0]+"§"+temp[1];
			o.text=temp[0]+":"+temp[1];
			vos.options[vos.options.length]=o;
		}

	}


}


function returnChangeSort(outparamters)
{
	var tab_str=getDecodeStr(outparamters.getValue("tab_str"));
	
	var tabArray=new Array();
	if(tab_str.length>0)
	{
		if(tab_str.indexOf("#")==-1)
		{
			tabArray[0]=tab_str;
		}
		else
			tabArray=tab_str.split("#");
		vos=eval("document.reportCollectForm.tabid");;
		for(var i=vos.options.length-1;i>=0;i--)
		{
			vos.options.remove(i);
		}
		for(var i=0;i<tabArray.length;i++)
		{
			var temp=tabArray[i].split("~");
			var o=new Option();
			o.value=temp[0]+"§"+temp[2]+"§"+temp[1];
			o.text=temp[0]+"."+temp[2];
			vos.options[vos.options.length]=o;
		}

	}
	
	
	var unit_str=getDecodeStr(outparamters.getValue("unit_str"));
	var tabArray=new Array();
	if(unit_str.length>0)
	{
		if(unit_str.indexOf("#")==-1)
		{
			tabArray[0]=unit_str;
		}
		else
			tabArray=unit_str.split("#");
		vos=eval("document.reportCollectForm.unitCode");;
		for(var i=vos.options.length-1;i>=0;i--)
		{
			vos.options.remove(i);
		}
		for(var i=0;i<tabArray.length;i++)
		{
			var temp=tabArray[i].split("~");
			var o=new Option();
			o.value=temp[0]+"§"+temp[1];
			o.text=temp[0]+":"+temp[1];
			vos.options[vos.options.length]=o;
		}

	}
	
}

/*
* flag  1:表内计算 2：表间计算
*/
var info=new Array();
function batchCompute(flag)
{
	var unitcodeArray=new Array();
	var tabArray = new Array();
	var unitcodes = "";
	var tabids = "";
	
	unitcodeArray=getSelectInfos("unitCode");
	if(unitcodeArray.length==0)
	{
		alert(REPORT_INFO37+"！");
		return;
	}else{
		for(var i = 0 ; i< unitcodeArray.length; i++){
			var temp = unitcodeArray[i];
			utarray = temp.split("§");
			unitcodes += utarray[0];
			unitcodes += ",";
		}	
	}
	
	tabArray=getSelectInfos("tabid");
	if(tabArray.length==0)
	{	
		alert(REPORT_INFO42+"！");
		return;
	}else{
		for(var i = 0 ; i< tabArray.length; i++){
			var temp1 = tabArray[i];
			tbarray = temp1.split("§");
			tabids += tbarray[0];
			tabids += ",";
		}	
	} 
	info[0]=unitcodes; 
	var thecodeurl="/report/edit_collect/reportCollect.do?b_showformula=show&type="+flag+"&tabids="+tabids;
	
	var config = {
			width:450,
			height:450,
			title:'',
			theurl:thecodeurl,
			id:'showformula'
		}
	openWin(config);
}

function openWin(config){
    Ext.create("Ext.window.Window",{
    	id:config.id,
    	width:config.width,
    	height:config.height,
    	title:config.title,
    	resizable:false,
    	autoScroll:false,
    	modal:true,
    	renderTo:Ext.getBody(),
    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+config.theurl+"'></iframe>",
    	listeners:{
    		'close':function(){
    			var unitcodeList=new Array();	
    			if(objectlist&&objectlist.length>0)
    			{
    				for(var i=0;i<objectlist.length;i++)
    					unitcodeList[i]=objectlist[i];
    				unitcodelist=unitcodeList;
    			}
    			else
    			{
    				unitcodelist=new Array();
    				return;
    			}
    		}
    	}
	    }).show();	
}

function closeWindow()
{
	var valWin = parent.Ext.getCmp('editCollect');
	if(valWin)
		valWin.close();
	else
		window.close();	
}
</script>
<style>
.mybutton{
	width:70px
	}
</style>
<html>
  <head>
  </head>
  <hrms:themes />
  <body>
  <html:form action="/report/edit_collect/reportCollect">	 
   <table width="100%" height="90%" align="center" border="0" cellpadding="0" cellspacing="0">
  <tr> 
    <td valign="top">   
 		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
 		<tr>
 		<td>
        <fieldset align="center" style="width:94%;">
        <legend >批量表内表间计算</legend>
        <table width='100%' >
        	<tr>
        	<td>
        <table width="500" border="0" align="center" cellpadding="0" cellspacing="0" >
          <tr >
            <td width="671">
            	<table width='100%' >
            		<tr>
		               <td><input name="operater" type="radio"  onclick="setType('1')"  value="1" checked  >所有单位 </td>
		               <td><input type="radio" name="operater"  onclick="setType('2')"     value="2"  >直属单位 </td>
		               <td><input type="radio" name="operater"  onclick="setType('3')"     value="3" >基层单位 </td>
					</tr>
				</table>
			</td>
          </tr>
         <tr> 
           <td align="left"> 
            <select name="unitCode" size="11" multiple style="width:500px;height:180px;">
	           <logic:iterate id="element" name="reportCollectForm" property="c_unitList"  > 
	              <option value='<bean:write name="element" property="unitcode" />§<bean:write name="element" property="unitname" />' ><bean:write name="element" property="unitcode" />:<bean:write name="element" property="unitname" /></option>                 
	           </logic:iterate>
            </select> 
           </td>
           <td align='left' style="padding-left: 5px" valign="top" >
		       <input type="button"   value="全      选" id="queryall" class="mybutton" onClick="selectAll('tabid')">
		       <input type="button"   value="表内计算" style="margin-top: 10px;" class="mybutton" onClick="batchCompute(1)">
		       <input type="button"   value="表间计算" style="margin-top: 10px;" class="mybutton" onClick="batchCompute(2)">
		       <input type="button"   value="取      消" id="cancel" style="margin-top: 10px;" class="mybutton" onClick="closeWindow()">
		       <script type="text/javascript">
           			if(isIE6()){
           				document.getElementById("queryall").value="全    选";
           				document.getElementById("cancel").value="取    消";
           			}
           	   </script>
	       </td> 
          </tr>
         <tr>
          	<td align="left" height='40'>
          	<br> 
	         	 <select name="sortid" size="1"  onchange="change();" >
	               <logic:iterate id="element" name="reportCollectForm" property="c_sortIdList"  > 
	               	 	<option value='<bean:write name="element" property="sortid" />' ><bean:write name="element" property="sortid" />:<bean:write name="element" property="sortname" /></option>             
	               </logic:iterate>
             	 </select> 
          	</td>
          </tr>
          <tr > 
            <td align="left"> 
             <select name="tabid" size="14" multiple style="width:500px;height:200px;">
              	<logic:iterate id="element" name="reportCollectForm" property="c_tableList"  > 
             		<OPTION  value='<bean:write name="element" property="tabid" />§<bean:write name="element" property="name" />§<bean:write name="element" property="tsortid" />' > <bean:write name="element" property="tabid" />: <bean:write name="element" property="name" /></OPTION>
         		</logic:iterate>
              </select>
            </td>
          </tr>

        </table>
        </td>
	        <!--
	        <td align='right' valign='bottom' >
	        <input type="button"   value="全    选"  class="mybutton" onClick="selectAll('tabid')">&nbsp;<br><br><br>
	        <input type="button"   value="表内计算"  class="mybutton" onClick="batchCompute(1)">&nbsp;<br><br><br>
	        <input type="button"   value="表间计算"  class="mybutton" onClick="batchCompute(2)">&nbsp;<br><br><br>
	        <input type="button"   value="取    消"  class="mybutton" onClick="javascript:window.close();">&nbsp;<br><br>&nbsp;
	        </td>-->
        </tr></table>
        
        
        </fieldset>
        </td>
        </tr>
        
        </table>
      </form></td>
  </tr>
</table>
   
   </html:form>
  </body>
</html>
