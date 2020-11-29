<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<style type="text/css"> 
#dis_sort_table {
           border: 1px solid #eee;
           height: 230px;    
           width: 230px;            
           overflow: auto;            
           margin: 1em 1;
}
</style>
<script language="javascript">
	function addfield()
	{
		var itemid = document.getElementById("sortleft_field").value;
		var operation = "add";
		var sort_table_detail = document.getElementById("sort_table_detail").value;
		var salaryid = ${accountingForm.salaryid};
		var hashVo=new ParameterSet();
		hashVo.setValue("itemid",itemid);
		hashVo.setValue("operation",operation);
		hashVo.setValue("sort_table_detail",sort_table_detail);
		hashVo.setValue("salaryid",salaryid);
		var request=new Request({method:'post',asynchronous:false,onSuccess:refresh,functionId:'3020070302'},hashVo);			
	}
	function breturn()
    {
        window.close();  
    }

	function sub()
	{
		var sort_table_detail = document.getElementById("sort_table_detail").value;
		window.returnValue=sort_table_detail;
  	    window.close();
  	    
	}
	function refresh(outparamters)
	{
		var sort_table = outparamters.getValue("sort_table");
		var sort_table_mp = outparamters.getValue("sort_table_detail");
		var flag = outparamters.getValue("flag");
		if(flag=="ok"){
			var itemid = outparamters.getValue("itemid");
			var itemdesc = outparamters.getValue("itemdesc");
			additemright("sortleft_field",itemid,itemdesc);
		}
		document.getElementById("sort_table_detail").value = sort_table_mp;
		document.getElementById("dis_sort_table").innerHTML = getDecodeStr(sort_table);
	}
	function updown(itemid)
	{
		var table_value="";
		var sortvalue = document.getElementById(itemid+"_updown").value;
		var sort_table_detail = document.getElementById("sort_table_detail").value;
		var arr = sort_table_detail.split(",");
		for(var i=0;i<arr.length;i++){
			var arrid = arr[i].split(":");
			if(arrid[0]==itemid){
				arr[i]=itemid+":"+sortvalue;
			}
			table_value+=arr[i];
			if(i+1<arr.length){
				table_value+=",";
			}
		}
//		alert(table_value);
		document.getElementById("sort_table_detail").value = table_value;
	}
	function tr_bgcolor(nid,itemid)
	{
		var tablevos=document.getElementsByTagName("select");
		for(var i=0;i<tablevos.length;i++){
	    	var cvalue = tablevos[i];
	    	var td = cvalue.parentNode.parentNode;
	    	td.style.backgroundColor = '';
   		}
		var c = document.getElementById(nid);
		var tr = c.parentNode.parentNode;
		if(tr.style.backgroundColor!='')
		{
			tr.style.backgroundColor = '' ;
		}else{
			tr.style.backgroundColor = '#add6a6' ;
		}
		document.getElementById("sortitemid").value=itemid;
//		 alert(itemid);
	}
	
	
	function downSort(){
	var itemid = document.getElementById("sortitemid").value;
	var sort_table_detail = document.getElementById("sort_table_detail").value;
//	alert(sort_table_detail);
	var arr = sort_table_detail.split(",");
	var n;
	var sortitem;
	for(var i=0;i<arr.length;i++){
		var arrid = arr[i].split(":");
		var arrid1 = arr[arr.length-1].split(":");
		if(arrid1[0]==itemid){
			break;
		}
		if(arrid[0]==itemid){
			n=i;
			sortitem=arr[i];
			break;
		}
	}
	if(n!=null){
		arr[n]=arr[n+1];
		arr[n+1]=sortitem;
	}
//	alert(arr);
		var retstr = "";
		if(arr!="")
		{
			for(var t=0;t<arr.length;t++)
			{
				retstr += ","+arr[t]; 				
			}
				retstr = retstr.substring(1);
		}else{
			retstr = ""; 
		}
		document.getElementById("sort_table_detail").value = retstr;	
//		alert(retstr);
		var operation = "upsort";
		var hashVo = new ParameterSet();
		var salaryid = ${accountingForm.salaryid};
		hashVo.setValue("sort_table_detail",retstr);
		hashVo.setValue("operation",operation);
		hashVo.setValue("salaryid",salaryid);
		var request=new Request({method:'post',asynchronous:false,onSuccess:refresh,functionId:'3020070302'},hashVo);
	}
	function upSort()
	{
		var itemid = document.getElementById("sortitemid").value;
		var sort_table_detail = document.getElementById("sort_table_detail").value;
//		alert(sort_table_detail);
		var arr = sort_table_detail.split(",");
		var n;
		var sortitem;
		for(var i=0;i<arr.length;i++){
			var arrid = arr[i].split(":");
			var arrid1 = arr[0].split(":");
			if(arrid1[0]==itemid){
				break;
			}
			if(arrid[0]==itemid){
				n=i;
				sortitem=arr[i];
				break;
			}
		}
		if(n!=null){
			arr[n]=arr[n-1];
			arr[n-1]=sortitem;
		}
//		alert(arr);
		var retstr = "";
		if(arr!="")
		{
			for(var t=0;t<arr.length;t++)
			{
				retstr += ","+arr[t]; 				
			}
			retstr = retstr.substring(1);
		}else{
			retstr = ""; 
		}
		document.getElementById("sort_table_detail").value = retstr;	
//		alert(retstr);
		var operation = "downsort";
		var hashVo = new ParameterSet();
		var salaryid = ${accountingForm.salaryid};
		hashVo.setValue("sort_table_detail",retstr);
		hashVo.setValue("operation",operation);
		hashVo.setValue("salaryid",salaryid);
		var request=new Request({method:'post',asynchronous:false,onSuccess:refresh,functionId:'3020070302'},hashVo);
	}
	function deletefield()
	{
		var itemid = document.getElementById("sortitemid").value;
		var sortitemid = document.getElementById("sort_table_detail").value;
//		alert(sortitemid);
		var arr = sortitemid.split(",");
		for(var i=0;i<arr.length;i++)
		{
			var arrid = arr[i].split(":");
			var arrid1 = arr[0].split(":");
			if(arrid1[0]==itemid && arr.length==1)
			{
				arr="";
				break;
			}
			if(arrid[0]==itemid)
			{
				for(var i=i;i<arr.length;i++)
				{
					arr[i]=arr[i+1];
					if(i+1==arr.length)
					{
						arr[i]="";
					}
				}
				break;				
			}
		}
//		alert(arr);
		var retstr = "";
		if(arr!="")
		{
			for(var t=0;t<arr.length;t++)
			{
				if(t+1==arr.length)
				{
					retstr += ""; 
				}else{
					retstr += ","+arr[t]; 
				}
			}
			retstr = retstr.substring(1);
		}
		else
		{
			retstr = ""; 
		}
		document.getElementById("sort_table_detail").value = retstr;	
//		alert(retstr);
		var operation = "delete";
		var hashVo = new ParameterSet();
		var salaryid = ${accountingForm.salaryid};
		hashVo.setValue("sort_table_detail",retstr);
		hashVo.setValue("operation",operation);	
		hashVo.setValue("itemid",itemid);			
		hashVo.setValue("salaryid",salaryid);
		var flag = "ok"
		hashVo.setValue("flag",flag);
		var request=new Request({method:'post',asynchronous:false,onSuccess:refresh,functionId:'3020070302'},hashVo);
}
function additemright(sourcebox_id,itemid,itemdesc)
{
  var left_vo,vos,i;
  vos= document.getElementsByName(sourcebox_id);
  if(vos==null)
  	return false;
  left_vo=vos[0];
  var no = new Option();
  no.value=itemid;
  no.text=itemdesc;
  left_vo.options[left_vo.options.length]=no;
 return true;	
}
</script>

<html:form action="/gz/gz_accounting/sortgzemp">
<Br>

<table width='97%' border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="3"  >
				<bean:message key="label.query.selectfield"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap>
              <table>
              	<tr valign="bottom">
              		<td align="left" valign="bottom">
        	             <bean:message key="selfservice.query.queryfield"/>     
                    </td>
                    <td align="left" valign="bottom">   
                    </td>
                  	<td width="100%" align="left"  valign="bottom">
                   		<bean:message key="selfservice.query.queryfieldselected"/>
                  	</td>
                  	<td width="100%" align="left" valign="bottom" >
                  	</td>
              	</tr>
                <tr>
                 <td align="center"  width="44%">
                   <table align="center" width="100%">              
                   <tr>
                       <td align="center">
 		       		 <hrms:optioncollection name="accountingForm" property="sortemplist" collection="list"/>
		              <html:select name="accountingForm" size="10" property="sortleft_field" multiple="multiple" ondblclick="addfield();removeitem('sortleft_field');" style="height:230px;width:100%;font-size:9pt">
		              <html:options collection="list" property="name" labelProperty="label"/>
		        	</html:select>	
                       </td>
                    </tr>
                   </table>
                </td>
               
                <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_addfield" onclick="addfield();('sortleft_field');">
            		     <bean:message key="button.setfield.addfield"/> 
		           </html:button >
	           <br>
	           <br>
		           <html:button  styleClass="mybutton" property="b_delfield" onclick="deletefield();">
            		     <bean:message key="button.setfield.delfield"/>    
	    	       </html:button >	     
                </td>         
                
                <td width="44%" align="center" >
                 	<table align="center"  width="100%" >
                  	<tr>
                  	<td width="100%"  >	                  
				       <div id="dis_sort_table">${accountingForm.sort_table}</div>    
                 	</td>
                  </tr>
                  </table>             
                </td>
                <td width="4%" align="center">
                    <html:button  styleClass="mybutton" property="b_up" onclick="upSort();">
            		     <bean:message key="button.previous"/> 
	           		</html:button >
	           <br>
	           <br>
	           		<html:button  styleClass="mybutton" property="b_down" onclick="downSort();">
            		     <bean:message key="button.next"/>    
	           		</html:button >	     
                </td>                                
                </tr>
              </table>             
            </td>
            </tr>
          <tr >
          <td align="center" colspan="3">
                <html:button styleClass="mybutton" property="b_next" onclick="sub()">
            		      <bean:message key="button.ok"/>
	      		</html:button> 
	       		<html:button styleClass="mybutton" property="b_return" onclick="breturn()">
            		      <bean:message key="button.cancel"/>
	      		</html:button> 	       
          </td>
          </tr>   
</table>
<html:hidden name="accountingForm" property="sort_table_detail" />
<input type="hidden" name="sortitemid">
</html:form>