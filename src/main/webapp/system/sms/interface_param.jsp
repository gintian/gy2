<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes></hrms:themes>
<link rel="stylesheet" href="/css/tabpane.css" type="text/css">


<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/js/constant.js"></script>

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
    var webserver=1;
    
</script>


<script language="JavaScript">

    function checkIt()
    {
    
        document.getElementById("btnImport").disabled=true;
        document.getElementById("wait").style.display="block";
        
        var form = document.getElementById("form1");
        form.action="/system/sms/interface_param.do?b_check=link";
        form.submit();      
        
    }
    
    
    function saveIt()
    {    
        var trs=document.getElementsByName("delete");
    
        var str="";
        
        for(var i = 0;i<trs.length;i++)
        {
            var comValue=trs[i].cells[0].getElementsByTagName("input")[0].value;
            if(comValue==null || comValue=="")
            {
            	 alert(SMS_COM_EMPTY);
                 return;
            }

            for(var j=i+1;j<trs.length;j++)
            {
                var comValue1=trs[j].cells[0].getElementsByTagName("input")[0].value;
                if(comValue1==null || comValue1=="")
                {
                    alert(SMS_COM_EMPTY);
                    return;
                }
                 
                if((comValue)==comValue1)
                {
                    alert(SMS_COM_REPEAT);
                    return;
                }
            }
        }

        for(var i=0; i<trs.length; i++)
        {
            for(var j=0;j<6;j++)
            {
                if(5==j)
                {
                     if(document.getElementById("check"+i).checked)
                         str+="1";
                     else
                         str+="0";
                }
                else{
                    //V771封版:系统管理/应用设置/短信接口参数，保存不上。（谷歌有问题，ie是好的）
                    str+=trs[i].getElementsByTagName("td")[j].getElementsByTagName("input")[0].value+",";
                }
            }
        
            if(i!=trs.length-1)
                str=str+";";
        }

        document.getElementById("btnSave").disabled=true; 
        document.getElementById("wait2").style.display="block";
        
        var hashvo=new ParameterSet();
        hashvo.setValue("saveStr",str);
        var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'1010020202'},hashvo);
        
    }
    
    function showfile(outparamters){
      var infor = outparamters.getValue("infor");
        if(infor=='ok'){
        
            document.getElementById("btnSave").disabled=false;
        
            document.getElementById("wait2").style.display="none";
        
            alert("<bean:message key='label.posbusiness.success'/>！");
            return false;
        }
    }

    
</script>
  

<html:form action="/system/sms/interface_param" styleId="form1">
 <table width="535" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable" style="margin-top:7px;margin-left:2px;">
   <tr height="20">
    <td align="center" class="TableRow"><bean:message key="system.sms.com"/></td>
    <td align="center" class="TableRow"><bean:message key="system.sms.business"/></td>
    <td align="center" class="TableRow"><bean:message key="system.sms.style"/></td>
    <td align="center" class="TableRow"><bean:message key="system.sms.baudrate"/></td>
    <td align="center" class="TableRow"><bean:message key="system.sms.pin"/></td>
    <td align="center" class="TableRow"><bean:message key="system.sms.able"/></td>      
   </tr> 

    <logic:notEmpty name="interParamForm" property="ywList">
       <logic:iterate id="element" name="interParamForm" property="ywList" indexId="index">
    <tr class="trDeep" id="delete" name="delete">
        <td width="13%">
            <html:text name="element" property="com" styleClass="text" style="width:45px"/>
        </td>
        <td width="22%">              
            <html:text name="element" property="company"  styleClass="text" style="width:100px"/>
        </td>
        <td width="30%">       
            <html:text name="element" property="modeltype" styleClass="text" style="width:140px"/>
        </td>
        <td width="10%">            
            <html:text name="element" property="bit" styleClass="text" style="width:50px"/>
        </td>
        <td width="15%">            
            <html:password name="element" property="password" styleClass="text" style="width:65px"/>
        
        </td>
        <logic:equal name="element" property="valid" value="1">
        <td width="5%" align="center">              
            <input type="checkbox" name="check<%=index %>" checked="true" value=<bean:write name="element" property="valid"/>" id="check<%=index %>"/>
        </td>
        </logic:equal>
        
        <logic:notEqual name="element" property="valid" value="1">
        <td width="5%" align="center">              
            <input type="checkbox" name="check<%=index %>" value="<bean:write name="element" property="valid"/>" id="check<%=index %>"/>
        </td>
        </logic:notEqual>
    </tr>
    
</logic:iterate>
    </logic:notEmpty>
</table>




<table width="99%" align="center">
        <logic:empty name="interParamForm" property="ywList">
            <tr height="35px;">
                <td align="center">
                    <input type="button" name="b_check" class="mybutton"  id="btnImport" onclick="checkIt()" value="<bean:message key="system.sms.checkport"/>"/>
                </td>
            </tr> 
        </logic:empty>
    
        <logic:notEmpty name="interParamForm" property="ywList">
    
            <tr  height="35px;">
                <td align="center">
                    <input type="button" name="b_save" class="mybutton" id="btnSave" onclick="saveIt()" value="<bean:message key="button.save"/>"/>&nbsp;&nbsp;
                    <input type="button" name="b_check" class="mybutton"  id="btnImport" onclick="checkIt()" value="<bean:message key="system.sms.checkport"/>"/>
                </td>
            </tr> 
        </logic:notEmpty>
    
</table>


  <div id='wait' style='position:absolute;top:200;left:50;display:none;'>
        <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="left">
           <tr>
             <td class="td_style" height=24><bean:message key="system.sms.checking"/></td>
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
    
    <div id='wait2' style='position:absolute;top:200;left:50;display:none;'>
        <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="left">
           <tr>
             <td class="td_style" height=24><bean:message key="system.sms.saveparam"/></td>
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
    
    
</html:form>
<script>
if(!getBrowseVersion()){//非IE浏览器 样式修改  wangb 20180522 48180
	var ftable = document.getElementsByClassName('ftable')[0];
	ftable.style.margin = '0 auto';
}

</script>

