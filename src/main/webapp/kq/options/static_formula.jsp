<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
  
<script language="javascript">
       
 var temp =new Array();

	function showSetList(outparamters)
	{
		var setlist=outparamters.getValue("setlist");

		AjaxBind.bind(kqItemForm.setlist,setlist);
		if($('setlist').options.length>0)
		{
		  $('setlist').options[0].selected=true;
		  $('setlist').fireEvent("onchange");
		}
	}
        
  function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("klist");
		var ss=outparamters.getValue("fieldlist");
		if(ss!=null)
		{
       var i=0;
    
       for(i=0;i<ss.length;i++)
        {
          temp[i] = new Array(ss[i].label,ss[i].name);
           //alert(temp[i]);
        }
		}

				
		AjaxBind.bind(kqItemForm.left_fields,fieldlist);
	}
	
  function searchFieldList()
	{
	   var tablename=$F('setlist');
	   var in_paramters="tablename="+tablename;
   	   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'15204110010'});
	}

	function MusterInitData(infor)
	{
   	   var request=new Request({method:'post',asynchronous:false,onSuccess:showSetList,functionId:'15204110011'});
	}
	
	
	function insertTxt(strtxt)
	{
		var expr_editor=$('s_expr');
	        expr_editor.focus();
		var element = document.selection;
		if (element!=null) 
		{
			var rge = element.createRange();
			if (rge!=null)	
		  	   rge.text=strtxt;
		}
	}
  
 
  function  fnOpen()  
  {  
     var  wName; 
     wName=window.showModalDialog("formula_wizard.jsp",temp);  
     insertTxt(wName);
  }  
   

</script>
<html:form action="/kq/options/static_formula">
<fieldset>
	<legend><bean:message key="kq.item.formula"/></legend>
<table border="0" cellspacing="1"  width="100%" align="left" cellpadding="0" >
  <tr> 
    <td width="36%" height="22"> 
       <bean:message key="kq.formula.subset"/> 
       <select name="setlist" size="1"  style="width:100%" onchange="searchFieldList();">    
			   <option value="1111">#</option>
        </select>
      </td>
     <td width="6%">&nbsp;</td>
     <td width="58%">
       <bean:message key="kq.formula.fashion"/>
       <html:select name="kqItemForm" property="manner">
          <option value="count"><bean:message key="kq.formula.count"/></option>
          <option value="sum"><bean:message key="kq.formula.sum"/></option>
          <option value="max"><bean:message key="kq.formula.max"/></option>
          <option value="min"><bean:message key="kq.formula.min"/></option>
          <option value="average"><bean:message key="kq.formula.average"/></option>
        </html:select>
        <input type="button" name="Submit" value="<bean:message key="kq.formula.function"/>" class="mybutton" onClick="fnOpen();">
      </td>
   </tr>
   <tr> 
    <td valign="top"> 
      <select name="left_fields" multiple="multiple"  ondblclick="insertTxt($F('left_fields'));" style="height:230px;width:100%;font-size:9pt">
       </select>
      </td>
    <td></td>
    <td bordercolor="#666666"> 
      <html:textarea name="kqItemForm" property="s_expr"   cols="50" rows="20" style="height:230px;width:100%;font-size:9pt"/>
      <div id="Layer1"> 
        <p> 
          <input type="button" name="Submit4" value="0" class="mybutton" onclick="insertTxt(this.value);" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit42" value="1" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit43" value="2" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit44" value="3" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit45" value="4" class="mybutton" onclick="insertTxt(this.value)"style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit46" value="(" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit477" value="=" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit4763" value=">=" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit4753" value="<bean:message key="kq.formula.not"/>" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:8%;font-size:9pt">
          <input type="button" name="Submit46" value="<bean:message key="kq.formula.if"/>" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:8%;font-size:9pt">
          <br>
          <input type="button" name="Submit47" value="5" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit472" value="6" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit473" value="7" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit474" value="8" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit475" value="9" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit476" value=")" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit4722" value=">" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit4754" value="<=" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit4752" value="<bean:message key="kq.formula.even"/>" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:8%;font-size:9pt">
          <input type="button" name="Submit46" value="<bean:message key="kq.formula.then"/>" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:8%;font-size:9pt">
          <br>
          <input type="button" name="Submit47" value="+" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit472" value="-" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit473" value="*" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit474" value="/" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit475" value="\" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit476" value="%" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit4732" value="<" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit4742" value="<>" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:5%;font-size:9pt">
          <input type="button" name="Submit4762" value="<bean:message key="kq.formula.or"/>" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:8%;font-size:9pt">
          <input type="button" name="Submit4764" value="~" class="mybutton" onclick="insertTxt(this.value)" style="height:18px;width:8%;font-size:9pt">
          <br>
      </div>
      </td>
  </tr>
  <tr> 
   <td height="44" colspan="3" align="center">
    	<hrms:submit styleClass="mybutton" property="b_save">
         <bean:message key="kq.formula.true"/>
	      </hrms:submit>
       <hrms:submit styleClass="mybutton" property="b_return">
          <bean:message key="button.return"/>
	      </hrms:submit> 
	    </td>
    </tr>
</table>
</fieldset>
</html:form>
<script language="javascript">
   MusterInitData();
</script>