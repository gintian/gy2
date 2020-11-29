<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="javascript">
/**查询指标*/
        var temp =new Array();
        var infos = "${feastForm.fieldlist}";
        var tempp=new Array();
        var infos = "${feastForm.fieldItems}";	
	if(infos == ""){}else{
		tempp = infos.split(",");
	}
	
	function searchFieldList()
	{
	
	   var tablename=$F('setname');
	   var in_paramters="tablename="+tablename;	   
   	   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'15208000004'});
	}
	/**显示指标*/
	function showFieldList(outparamters)
	{
	        var fieldlist=outparamters.getValue("fieldlist");
	        AjaxBind.bind(feastForm.left_fields,fieldlist);	
	        var onefiledlist=outparamters.getValue("onefiledlist");
	        var waitInfo=eval("wait");	
	        waitInfo.style.display="none";	        
	        oneFieldList(onefiledlist);
	        getSetnameValue();	
			
	}
	/**初化数据*/
	function MusterInitData(infor,expr_flag)
	{
	   if(expr_flag=="1")
	   {
	      var waitInfo=eval("wait");	   
	      waitInfo.style.display="block";
	   }
	   
	   var pars="base="+infor;
	   var hashvo=new ParameterSet();	 
   	   var request=new Request({method:'post',asynchronous:true,parameters:pars,onSuccess:showSetList,functionId:'15208000003'},hashvo);
	}
	/**从后台取得相应的数据,初始化前台*/
	function showSetList(outparamters)
	{
	        var setlist=outparamters.getValue("setlist");
		AjaxBind.bind(feastForm.setname,/*$('setlist')*/setlist);
		if($('setname').options.length>0)
		{
		  $('setname').options[0].selected=true;
		  $('setname').fireEvent("onchange");		 
		}	
	}
	//取得指标代码
	function searchCodeList()
	{
	
	   var tablename=$F('setname');	   
	   var in_paramters="tablename="+tablename;	   
	   var filedname=$F('left_fields');
	   var hashvo=new ParameterSet();
	   hashvo.setValue("tablename",tablename);
	   hashvo.setValue("filedname",filedname);
   	   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeList,functionId:'15208000005'},hashvo);
	
	}
	function showCodeList(outparamters)
	{
	    var flag_code=outparamters.getValue("flag_code");
	    var filed_desc=outparamters.getValue("filed_desc");	    
		   insertTxt(filed_desc);
	    if(flag_code=="true")
	    {
	       var left_codes=outparamters.getValue("left_codes");	       
	       AjaxBind.bind(feastForm.left_codes,/*$('left_codes')*/left_codes);
	       show();
	    }else
	    {
	      closes();
	    }
	}
	function insertCodeList()
	{
	  var codeid=$F('left_codes');
	  codeid="\""+codeid+"\"";	  
	  insertTxt(codeid);
	}
	function show()
        {
	  var bb=eval("b");
	  bb.style.display="block";
        }
        function closes()
        {
	  var bb=eval("b");
	  bb.style.display="none"; 
        }  
        
        
        function insertTxt(strtxt)
	{
	     if(strtxt==null)
             return ;
             if((strtxt.toString()).indexOf("(")!=-1)
                 strtxt="["+strtxt+"]";
    
             if(strtxt=="<bean:message key="kq.formula.even"/>"||strtxt=="<bean:message key="kq.formula.if"/>"||strtxt=="<bean:message key="kq.formula.or"/>"||strtxt=="<bean:message key="kq.formula.fou"/>"||strtxt=="<bean:message key="kq.formula.end"/>"||strtxt=="<bean:message key="kq.formula.then"/>"||strtxt=="<bean:message key="kq.formula.not"/>")
             {
                 var ddd=" "+strtxt+" ";
                 var expr_editor=$('c_expr');
	         expr_editor.focus();
		  var element = document.selection;
		  if (element!=null) 
		  {
		     var rge = element.createRange();
		     if (rge!=null)	
		  	    rge.text=ddd;
		  }
                  }else
                  {
                     var expr_editor=$('c_expr');
	             expr_editor.focus();
		     var element = document.selection;
		     if (element!=null) 
		     {
		  	var rge = element.createRange();
		   	if (rge!=null)	
		  	     rge.text=strtxt;
		     }
		 }
	 }  
	 function  fnOpen()  
        {  
           var  wName; 
           wName=window.showModalDialog("formula_wizard.jsp",tempp,'dialogLeft:455;dialogTop:200;dialogHeight:370px;dialogWidth:490px;status:no');   
           insertTxts(wName);
        } 
        
         function insertTxts(strtxt)
         {
            if(strtxt==null)
            return ;
            var ggg;
            if(strtxt=="<bean:message key="kq.wizard.ifa"/>")
            {
               var dgf=strtxt.replace(" ","\n");
               var dgg=dgf.replace(" ","\n");
               var ddd=dgg.replace(" ","\n");
               ggg=ddd.replace(" ","\n");
            }else if(strtxt=="<bean:message key="kq.wizard.thing"/>")
            {
                ggg="如果 Lexp1 那么 exp1"+"\n"+"如果 Lexp2 那么 exp2"+"\n"+"否则 expn..."+"\n"+"结束";
            }else{
                ggg=strtxt;  
            }
            var expr_editor=$('c_expr');
	    expr_editor.focus();
	    var element = document.selection;
	    if (element!=null) 
	    {
	  	var rge = element.createRange();
	   	if (rge!=null)	
	  	     rge.text=ggg;
	    }
         }
         function getSetnameValue()
         {
           var tablename=$F('setname');	   
	   var in_paramters="tablename="+tablename;	   
	   var filedname=$F('left_fields');
	   var hashvo=new ParameterSet();
	   hashvo.setValue("tablename",tablename);
	   hashvo.setValue("filedname",filedname);
   	   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:getCodeList,functionId:'15208000005'},hashvo);
           
         }
         function getCodeList(outparamters)
	 {
	    var flag_code=outparamters.getValue("flag_code");    
	    if(flag_code=="true")
	    {
	       var left_codes=outparamters.getValue("left_codes");	       
	       AjaxBind.bind(feastForm.left_codes,/*$('left_codes')*/left_codes);
	       show();
	    }else
	    {
	      closes();
	    }
	}
	function change()
	{
		var tablename=$F('setname');	
	   feastForm.action="/kq/feast_manage/managerdata.do?b_exp=link&setname="+tablename;
       feastForm.submit();  
	}
	function formula_wizard()
	{
	   var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_query=link&salaryid=&tableid=&salarytemp="; 
       var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
       insertTxts(return_vo);       
	}
	function formula_check()
	{
		feastForm.action="/kq/feast_manage/managerdata.do?b_check=link";
	    feastForm.submit();
	}
</script>
<html:form action="/kq/feast_manage/managerdata">
<div id='wait' style='position:absolute;top:100px;left:250;display:none;'   >
  <table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style common_background_color" height=24>正在提取数据，请稍候......</td>
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
	
<br>
<br/>
<table border="0" cellspacing="0"  cellpadding="0"  width="70%" align="center" cellpadding="0" class="framestyle" >
    <logic:equal name="feastForm" property="sige" value="2">   
     		 <font color=#000000> <bean:write name="feastForm" property="sigh"  filter="true"/></font>
       </logic:equal>
       <logic:equal name="feastForm" property="sige" value="1">    
     		 <font color=#ff000> <bean:write name="feastForm" property="sigh"  filter="true"/></font>
                 <font color=#ff000> <bean:write name="feastForm" property="errormsg"  filter="true"/></font>
       </logic:equal>
       <logic:equal name="feastForm" property="sige" value="4">    
     		 <font color=#ff000> <bean:write name="feastForm" property="sigh"  filter="true"/></font>
       </logic:equal>
       <logic:equal name="feastForm" property="sige" value="5">    
     		 <font color=#000000> <bean:write name="feastForm" property="sigh"  filter="true"/></font>
       </logic:equal>   
    <thead>
      <tr>
            <td align="left" class="TableRow" nowrap colspan="3" style="border-top:0px;border-left:0px;border-right:0px;">
		<bean:write name="feastForm" property="hols_name"  filter="true"/>
                <html:hidden name="feastForm" property="hols_status" styleClass="text"/> 
                <html:hidden name="feastForm" property="hols_name" styleClass="text"/>
                <html:select name="feastForm" property="exp_field" size="1" onchange="change();">
                <html:optionsCollection property="exp_fieldlist" value="dataValue" label="dataName"/>	        
                </html:select>
            </td>            	        	        	        
       </tr>
     </thead> 
     
    
    <tr>
     
    <td bordercolor="#666666" valign="top" width="100%"> 
      <table  border="0" cellspacing="0"  cellpadding="0" width="100%"><tr>    
      <td width="100%" align="center" valign="top">
      <html:textarea name="feastForm" property="c_expr"   cols="50" rows="20" style="height:280px;width:98%;font-size:9pt;margin-top:3px;"/>
      </td></tr></table>
    </td>
    </tr>
     <tr>
	<td height="5"/>
    </tr>
    <tr>
     <td>
      <table width="100%">
      <tr>
        <td colspan="3" align="right">
		<input type="button" name="Submit" value="<bean:message key="kq.formula.function"/>" class="mybutton" onClick="formula_wizard();" style="height:20px;width:70px;">
		<input type="button" name="Submit" value="公式<bean:message key="kq.formula.check"/>" class="mybutton" onclick="formula_check();" style="height:20px;width:72px">
		</td>
      </tr>
      <tr>      
      <td width="48%" height="100">
      <!--参考项目-->
      <fieldset align="center" style="width:100%;height:100%;">
    		<legend >参考项目</legend>
         <table border="0" cellspacing="0"  cellpadding="0" width="100%">
         <tr><td height="5px"></td></tr>
          <tr>
           <td valign="middle" width="15%">
             &nbsp;&nbsp;<bean:message key="kq.feast.targets"/>
           </td>
           <td valign="middle">&nbsp;
              <hrms:optioncollection name="feastForm" property="setlist" collection="list" />
	          <html:select name="feastForm" property="setname" size="1" style="width:75%"  onchange="searchFieldList();">
                <html:options collection="list" property="dataValue" labelProperty="dataName"/>
              </html:select> 
           </td>
          </tr>
          <tr><td height="5px"></td></tr>
          <tr>
             <td valign="middle" width="15%">
	             &nbsp;&nbsp;<bean:message key="kq.feast.target"/>
	         </td>
	         <td valign="middle">&nbsp;
	             <hrms:optioncollection name="feastForm" property="fieldlist" collection="list" />
		         <html:select name="feastForm" property="left_fields" size="1" style="width:75%"  onchange="searchCodeList();">
	               <html:options collection="list" property="dataValue" labelProperty="dataName"/>
	             </html:select> 
             </td>
          </tr>
            <tr><td height="5px"></td></tr>
            <tr id="b" style="display:none;">
             <td valign="middle" width="15%">
               &nbsp;&nbsp;<bean:message key="kq.feast.code"/>
             </td>
             <td  valign="middle">&nbsp;              
                <select name="left_codes" size="1" style="width:75%" onchange="insertCodeList();">
                </select>
             </td>
          </tr>
            </fieldset>
          </table>
      </td>
      <td width="2%">&nbsp; </td>
      <td width="50%">
      <fieldset align="center" style="width:100%;height=100%">
    	<legend >公式规则</legend>
        <table width="100%" align="center">
          <tr align="center">
			<td>
			<input type="button" name="Submit4" value="0" class="smallbutton"
				onclick="insertTxt(this.value);"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit42" value="1"
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit43" value="2"
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit44" value="3"
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit45" value="4"
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit46" value="("
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit477" value="="
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit4763" value="&gt;="
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit4753"
				value="<bean:message key="kq.formula.not"/>" class="smallbutton"
				onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit4764" value="~"
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit46"
				value="<bean:message key="kq.wizard.thing"/>"
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 13%; font-size: 9pt">
			</td>
		  </tr>
		  <tr>
			<td align="center">
			<input type="button" name="Submit47" value="5"
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit472" value="6"
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit473" value="7"
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit474" value="8"
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit475" value="9"
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit476" value=")"
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit4722" value="&gt;"
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit4754" value="&lt;="
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit4752"
				value="<bean:message key="kq.formula.even"/>"
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit46"
				value="<bean:message key="kq.formula.if"/>" class="smallbutton"
				onclick="insertTxt(this.value)"
				style="height: 20px; width: 10%; font-size: 9pt">
			<input type="button" name="Submit4764"
				value="<bean:message key="kq.formula.fou"/>" class="smallbutton"
				onclick="insertTxt(this.value)"
				style="height: 20px; width: 10%; font-size: 9pt">
			</td>
		  </tr>
		  <tr>
			<td align="center">
			<input type="button" name="Submit47" value="+"
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit472" value="-"
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit473" value="*"
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit474" value="/"
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit475" value="\"
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit476" value="%"
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit4732" value="&lt;" class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit4742" value="&lt;>"
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit4762"
				value="<bean:message key="kq.formula.or"/>" class="smallbutton"
				onclick="insertTxt(this.value)"
				style="height: 20px; width: 7%; font-size: 9pt">
			<input type="button" name="Submit46"
				value="<bean:message key="kq.formula.then"/>"
				class="smallbutton" onclick="insertTxt(this.value)"
				style="height: 20px; width: 10%; font-size: 9pt">
			<input type="button" name="Submit4764"
				value="<bean:message key="kq.formula.end"/>" class="smallbutton"
				onclick="insertTxt(this.value)"
				style="height: 20px; width: 10%; font-size: 9pt">
			</td>
		  </tr>
        </table>   
       </td>
       </tr></table>
     </td>
     </fieldset>
  </tr>

</table>
<table align="center">
<tr> 
    <td height="35"  align="center">
   
    <hrms:submit styleClass="mybutton" property="b_save">
        <bean:message key="button.save"/>
	   </hrms:submit>
      <hrms:submit styleClass="mybutton" property="br_return">
         <bean:message key="button.return"/>
	  </hrms:submit> 
  </tr>
</table>
</html:form>
<script language="javascript">
  // MusterInitData('<bean:write name="feastForm"  property="infor_Flag"/>','<bean:write name="feastForm"  property="expr_flag"/>');
  searchFieldList();
    closes();
    function oneFieldList(onefiledlist)
    {
       if(onefiledlist!=null)
	{
            var i=0;
            for(i=0;i<onefiledlist.length;i++)
            {
              
               temp[i] = new Array(onefiledlist[i].label,onefiledlist[i].name);
            }
	}
			
   }
</script>
