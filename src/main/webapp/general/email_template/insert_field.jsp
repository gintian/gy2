<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="Javascript" src="/gz/salary.js"></script>
<script type="text/javascript">
<!--
function gzemail_xianshi(setobj)
{
   var num = 0;
   var id="";
   for(var i=0;i<setobj.options.length;i++)
   {
   if(setobj.options[i].selected)
   {
      num++;
      id=setobj.options[i].value;
   }
   }
   if(num >1)
   {
      alert("不能同时选择多个指标");
      return;
   }
   var hashvo=new ParameterSet(); 	
    hashvo.setValue("itemid",id);
    hashvo.setValue("type","1");
    hashvo.setValue("nmodule","${gzEmailForm.nmodule}");
    hashvo.setValue("fieldsetid",gzEmailForm.formulafieldsetid.value);
   	var In_paramters="flag=1"; 	
    var request=new Request({method:'post',asynchronous:false,
	parameters:In_paramters,onSuccess:which_hidden,functionId:'0202030008'},hashvo);	
  
}
function which_hidden(outparamters)
{
   var type=outparamters.getValue("type");
   var itemtype=outparamters.getValue("itemtype");
   var codesetid=outparamters.getValue("codesetid");
   var itemlength=outparamters.getValue("itemlength");
   var decimalwidth=outparamters.getValue("decimalwidth");
   var obj=document.getElementById("d");
   var setobj=document.getElementById("n");
  if(itemtype=="n")
  {

      obj.style.display="none";
      setobj.style.display="block";
      document.getElementById("year_s").value=itemlength;
      document.getElementById("card_s").value=decimalwidth;
  }
 else if(itemtype=="d")
  {
     obj.style.display="block";
     setobj.style.display="none";
 }
 else
 {
      obj.style.display="none";
      setobj.style.display="none";
 }
 gzEmailForm.fieldtype.value=itemtype;
 gzEmailForm.codesetid.value=codesetid;
 
}
//"/fieldtitle/fieldtype/fieldcontent/dateformat/fieldlen/ndec/codeset/nflag"
//fieldtitle   公式标题或指标名称
//fieldcontent  公式内容或指标id
//ndec  小数点位数
//nflag =0是指标，=1是公式
function gzemail_chooseFieldOk()
{
   var setobj=$('itemid');
   var itemtype=gzEmailForm.fieldtype.value;
   var codesetid=gzEmailForm.codesetid.value;
   var fieldcontent="";
   var fieldtitle="";
   var fieldlen=gzEmailForm.integerdigit.value;
   var ndec=gzEmailForm.decimalfractiondigit.value;
   var format="0";
   var obj = new Object();
   for(var i=0;i<setobj.options.length;i++)
   {
      if(setobj.options[i].selected)
      {
         fieldcontent=setobj.options[i].value;
         fieldtitle=setobj.options[i].text;
         break;
      }
   }
   obj.fieldcontent=fieldcontent;
   obj.fieldtitle=fieldtitle;
   obj.fieldtype=itemtype;
   obj.fieldlen=fieldlen;
   obj.ndec=ndec;
   obj.codeset=codesetid;
   obj.nflag="0";
   if(itemtype=="d")
   {
      var formatobj=$('dateFormat');
      for(var j=0;j<formatobj.options.length;j++)
      {
         if(formatobj.options[j].selected)
         {
            format=formatobj.options[j].value;
            break;
         }
      }
   }
   obj.dateformat=format;
    //兼容谷歌 wangbs 20190320
    closeWindow(obj);
}
function closeWindow(obj)
{
    //兼容谷歌 wangbs 20190320
    if(parent.parent.itemSelectReturn){
        parent.parent.itemSelectReturn(obj);
    }else{
        returnValue=null;
        window.close();
    }
}
//-->
</script>
<style type="text/css">
body {
	background-color: transparent;
	margin:0px;
}
.m_frameborder {
	border-left: 1px inset #D4D0C8;
	border-top: 1px inset #D4D0C8;
	border-right: 1px inset #FFFFFF;
	border-bottom: 1px inset #FFFFFF;
	width: 40px;
	height: 19px;
	background-color: transparent;
	overflow: hidden;
	text-align: right;
	font-family: "Tahoma";
	font-size: 6px;
}
.m_arrow {
	width: 16px;
	height: 8px;
	font-family: "Webdings";
	font-size: 7px;
	line-height: 2px;
	padding-left: 2px;
	cursor: default;
}
.m_input {
	width: 18px;
	height: 14px;
	border: 0px solid black;
	font-family: "Tahoma";
	font-size: 9px;
	text-align: right;
}
</style>

<html:form action="/general/email_template/insert_field">
<table width='290' border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow">
<%--
<THEAD>
<TR>
<TD align="left" class="TableRow" colspan="2" nowrap>
备选指标
</TD>
</TR>

</THEAD>
--%>
<tr>
<td align="center" colspan="2" nowrap height="30px;">
<html:select name="gzEmailForm" property="formulafieldsetid" size="1" onchange="changeFieldSet();" style="width:100%;">
			<html:optionsCollection property="fieldsetlist" value="dataValue" label="dataName"/>
		    </html:select>
</td>
</tr>
<tr>
<td align="center" colspan="2" nowrap>
  <html:select name="gzEmailForm" size="10" property="itemid" onchange="gzemail_xianshi(this);" style="height:200px;width:100%;font-size:9pt;border:1px solid">
		              <html:optionsCollection property="itemlist" value="dataValue" label="dataName"/>
		        </html:select>	
</td>
</tr>
<tr>
<td align="left" colspan="2" nowrap>
    <%--兼容浏览器 wangbs 20190320--%>
<table width="100%" border="0" id="d" style="display:none;margin:4px 0px;" cellspacing="0" cellpadding="0">
<tr>
<td width="50%">

格式<html:select name="gzEmailForm" size="1" property="dateFormat" style="width:90%;">
		              <html:optionsCollection property="dateFormatList" value="dataValue" label="dataName"/>




		        </html:select>		
		        </td>
		        </tr>
		        </table>        
</td>
</tr>
<tr>
<td align="center" colspan="2">
        <%--兼容浏览器 wangbs 20190320--%>
<table width="100%" border="0" id="n" style="display:none" cellspacing="0" cellpadding="0">
<tr>
<td>
<table width="50%" border="0"  cellspacing="0" cellpadding="0">
                    <tr> 
                      <td align="left" width="30%" nowrap>&nbsp;整数位&nbsp;</td>
                      <td valign="middle">                      
                       <html:text name="gzEmailForm" styleId='year_s' property="integerdigit" size="4"  onkeypress="event.returnValue=IsDigit();" value="8"/>&nbsp;                       
                      </td>
                      <td valign="middle" align="left">
                        <table border="0" cellspacing="2" cellpadding="0">
                          <tr><td><button id="1_up" class="m_arrow" onmouseup="IsInputValue('year_s','1');">5</button></td></tr>
                          <tr><td><button id="1_down" class="m_arrow" onmouseup="IsInputValue('year_s','1');">6</button></td></tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                  </td>
                  <td align="center">
                  
     <table width="50%" border="0" cellspacing="0" cellpadding="0">
                    <tr> 
                      <td align="left" width="30%" nowrap>&nbsp;小数位&nbsp;</td>
                      <td valign="middle">                      
                       <html:text name="gzEmailForm" styleId='card_s' property="decimalfractiondigit" size="4"  onkeypress="event.returnValue=IsDigit();" value='0'/>&nbsp;                       
                      </td>
                      <td valign="middle" align="left">
                        <table border="0" cellspacing="2" cellpadding="0">
                          <tr><td><button id="1_up" class="m_arrow" onmouseup="IsInputValue('card_s','2');">5</button></td></tr>
                          <tr><td><button id="1_down" class="m_arrow" onmouseup="IsInputValue('card_s','2');">6</button></td></tr>
                        </table>
                      </td>
                    </tr>
                  </table>  
                  </td>
                  </tr>
                  </table>     
</td>
</tr>
</table>
<table width="290">
	<TR>
	<td align="center" colspan="2" nowrap height="35px;">
	<input type="button" name="ok" value="确定" class="mybutton" onclick="gzemail_chooseFieldOk();"/>
	<input type="button" name="clos" value="取消" class="mybutton" onclick="closeWindow();"/>
	<input type="hidden" name="fieldtype" value=""/>
	<input type="hidden" name="codesetid" value=""/>
	<html:hidden name="gzEmailForm" property="nmodule"/>
	</td>
	</TR>
</table>
</html:form>
<script>
if(!getBrowseVersion() || getBrowseVersion() == 10){
	var year_s = document.getElementById('year_s');
	year_s.style.marginTop='15px';
	var card_s = document.getElementById('card_s');
	card_s.style.marginTop='15px';
}
</script>