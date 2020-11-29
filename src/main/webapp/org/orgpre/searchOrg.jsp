<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<style>

.tableDiv{
  border:1px solid;
  height:250px;
  margin: 10,5,10,5px;
  border-collapse:collapse; 
  overflow: auto;
}

.clearTdLeftborder{
	border-left:0px;
}

.clearTdRightborder{
	border-right:0px;
}

</style>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<logic:equal value="false" name="orgPreForm" property="searchOrg">
<script>
    window.returnValue="ok";
    window.top.close();
</script>
</logic:equal>
<html:form action="/org/orgpre/orgpretable">
 <input type="hidden" name="itemlength" id="itemlength">
<div>
  <table border=0 width=696 >
         <tr>
           <td  width=30% valign="top" style="padding-top:10px;">
              <fieldset>
                 <legend><bean:message key="selfservice.query.queryfield"/></legend>
                 <table>
                    <tr>
                       <td>
                          <html:select property="searchSetId" name="orgPreForm"  value="B01" onchange="initfields(this)" style="width:190px;" styleId="setbox">
                            <html:optionsCollection name="orgPreForm" property="setList" label="customdesc" value="fieldsetid" />
                          </html:select>
                       </td>
                    </tr>
                    <tr>
                      <td>
                        <select  multiple="multiple"  id="itembox" ondblclick="checkitem(this)" style="height:240px;width:190px;"> 
                        </select>
                      </td>
                    </tr>
                 </table>
              </fieldset>
           
           </td>
           <td align="center"   valign="top" width=60>
           <br><br><br><br><br><br><br>
              <button class="myButton" onclick="checkitem(document.getElementById('itembox'))"><bean:message key="gz.acount.filter.add"/> </button><br><br>
              <button class="myButton" onclick="deleteRow()"><bean:message key="gz.acount.filter.delete"/></button>
           </td>
           <td valign="top" style="padding-top:10px;" >
               <fieldset>
                  <legend><bean:message key="general.inform.search.condset"/></legend>
                  <div class="tableDiv common_border_color" >
                  <table width=100% border=0 id="searchTable" style="border-collapse: collapse;">
                      <tr>
                         <td align="center" nowrap class="TableRow" style="border-left:none;border-top:none;" width=60><bean:message key="label.query.logic"/></td>
                         <td align="center" nowrap class="TableRow" style="border-top:none;"><bean:message key="label.query.field"/></td>
                         <td align="center" nowrap class="TableRow" style="border-top:none;" width=50><bean:message key="label.query.relation"/></td>
                	     <td align="center" nowrap class="TableRow"  style="border-top:none;border-right:none;" width=160><bean:message key="label.query.value"/></td>
                      </tr>
                  </table>
                  </div>
               </fieldset>
           </td>
         </tr>
         
         <tr>
            <td >
                  <input type="checkbox" name="querylike" value="1"><bean:message key="label.query.like"/> 
            </td>
            <td colspan=2 style="padding-top:8px;">
                  <input type="radio" name="searchtype" value="UM"><bean:message key="label.query.dept"/>&nbsp;&nbsp;  
                  <input type="radio" name="searchtype" value="UN"><bean:message key="label.query.org"/>&nbsp;&nbsp;
                  <input type="radio" name="searchtype" value="ALL" checked><bean:message key="label.query.all"/>&nbsp;&nbsp;&nbsp;&nbsp;
                  <button class="myButton" onclick="subform()"><bean:message key="infor.menu.query"/> </button> 
                  <button class="myButton" onclick="window.top.close();"><bean:message key="button.cancel"/> </button>
            </td>
         </tr>
         <tr>
            <td colspan=3>
                  <bean:message key="infor.menu.query.cue2"/> 
            </td>
         </tr>
  </table>
   
   
  
</div>
</html:form>

   <div id="date_panel" style="display:none">
	<select name="date_box" multiple="multiple" size="10"  style="width:120" onchange="setSelectValue();" onclick="setSelectValue();">    
			    <option value="$YRS[10]"><bean:message key='general.inform.search.years'/></option>
		<option value="<bean:message key='general.inform.search.this.years'/>"><bean:message key='general.inform.search.this.years'/></option>
		<option value="<bean:message key='general.inform.search.this.month'/>"><bean:message key='general.inform.search.this.month'/></option>
		<option value="<bean:message key='general.inform.search.this.day'/>"><bean:message key='general.inform.search.this.day'/></option>				    
		<option value="<bean:message key='general.inform.search.day'/>"><bean:message key='general.inform.search.day'/></option>
		<option value="1992.4.12">1992.04.12</option>	
		<option value="1992.4">1992.04</option>	
		<option value="1992">1992</option>			    
		<option value="1992-04-12">1992-04-12</option>
		<option value="1992-04">1992-04</option>	
	</select>
</div>
<script>
var setid;
var rownum=0;
var rowid;
var codesetid;
    function initfields(selectObj){
    	var setOptions = selectObj.options;
    	setid = setOptions[selectObj.selectedIndex].value;
    	
    	var request=new Request({method:'post',asynchronous:false,parameters:"searchSet="+setid,onSuccess:putfields,functionId:'0401000048'});
    	function putfields(outParam){
    		var options = outParam.getValue("options");
    		var optionArr = options.split("|");
    		var itembox = document.getElementById("itembox");
    		itembox.innerHTML="";
    		for(var i=0;i<optionArr.length;i++){
    			var optionInfo = optionArr[i].split(":");
    			if(optionInfo.length==1)
    				continue;
    			var optionObj = new Option(optionInfo[0],optionInfo[1]);
    			optionObj.setAttribute("itemtype",optionInfo[2]);
    			optionObj.setAttribute("codesetid",optionInfo[3]);
    			itembox.options.add(optionObj);
    		}
    		
    	}
    }
   
    function checkitem(itembox){
    	var options = itembox.options;
    	if(itembox.selectedIndex < 0)
    		return;
    	var tableObj = document.getElementById("searchTable");
    	for(var i=0;i<options.length;i++){
    		if(options[i].selected==true){
    			addTableRow(options[i],tableObj);
    		}
    	}
    	
    }
    
    function addTableRow(optionObj,tableObj){
    	var itemid = optionObj.value;
    	var itemdesc = optionObj.text;
    	var itemtype = optionObj.getAttribute("itemtype");
    	var codesetid = optionObj.getAttribute("codesetid");
    	var celltext;
    	var newRow = tableObj.insertRow();
    	newRow.id = "itemRow_"+rownum;
    	newRow.onclick = function(){ br_color(); this.style.backgroundColor="#FFF8D2"; rowid = this.id;};
    	newCell = newRow.insertCell(0);
    	newCell.className="RecordRow clearTdLeftborder";
    	newCell.align="center"; 
    	if(rownum==0 || tableObj.rows.length<3)
    		celltext="&nbsp;";
    	else
    	    celltext = "<SELECT name='logics["+rownum+"]'><OPTION SELECTED VALUE='and'>并且</OPTION><OPTION VALUE='or'>或</OPTION></SELECT>";
    	newCell.innerHTML=celltext;
    	newCell = newRow.insertCell(1);
    	newCell.className="RecordRow";
    	newCell.align="left";
    	newCell.nowrap=true;
    	celltext = "<INPUT TYPE='HIDDEN' NAME='itemids["+rownum+"]' VALUE='"+itemid+"'/>"+itemdesc;
    	newCell.innerHTML=celltext;
    	newCell = newRow.insertCell(2);
    	newCell.className="RecordRow";
    	newCell.align="center";
    	celltext="<SELECT NAME='factors["+rownum+"]' ><OPTION SELECTED VALUE='1'> = </OPTION><OPTION VALUE='2'> > </OPTION><OPTION VALUE='3'> >= </OPTION><OPTION VALUE='4'> < </OPTION><OPTION VALUE='5'> <= </OPTION><OPTION VALUE='6'> <> </OPTION></SELECT>"
    	newCell.innerHTML=celltext;
    	newCell = newRow.insertCell(3);
    	newCell.className="RecordRow clearTdRightborder";
    	newCell.align="left";
    	
    	if(codesetid!="0"){
    		celltext="<INPUT TYPE='TEXT' name='viewvalue["+rownum+"]' class='text4' onchange='fieldcodes(this)' />";
    		celltext+="&nbsp;<img src='/images/code.gif' onclick='opendialog(\""+codesetid+"\",\""+rownum+"\");'  align='absmiddle' />";
    		celltext+="<INPUT TYPE='HIDDEN' name='itemvalues["+rownum+"]' />"
    	}else{
    		if(itemtype == "D")
    		  celltext="<INPUT TYPE='TEXT' NAME='itemvalues["+rownum+"]' class='text4' ondblclick=\"showDateSelectBox(this);\" />";
    		else if(itemtype == "N")
    			celltext="<INPUT TYPE='TEXT' NAME='itemvalues["+rownum+"]' class='text4' onkeypress=\"event.returnValue=IsDigit();\" />";
    		else
    		  celltext="<INPUT TYPE='TEXT' NAME='itemvalues["+rownum+"]' class='text4'  />"; 
    	}
    	newCell.innerHTML=celltext;
    	rownum++;
    }
    
    function br_color(){
    	var tableObj = document.getElementById("searchTable");
    	var rows = tableObj.getElementsByTagName('tr');
    	for(var i=0;i<rows.length;i++){
    		rows[i].style.backgroundColor="";
    	}
    }
    
    function deleteRow(){
    	var tableObj = document.getElementById("searchTable");
    	var rows = tableObj.getElementsByTagName('tr');
    	for(var i=0;i<rows.length;i++){
    		if(rows[i].id == rowid){
    			tableObj.deleteRow(i);
    			if(tableObj.rows[i]!=null)
    				tableObj.rows[i].onclick();
    			else if(i!=1)
    				tableObj.rows[i-1].onclick();
    		}
    	}
    	if(tableObj.rows[1]!=null)
    	   tableObj.rows[1].cells[0].innerHTML="&nbsp;";
    	
    }
    
    function opendialog(codesetid,rownum){
    	openCodeDialog(codesetid,"viewvalue["+rownum+"]",'${orgPreForm.privOrg}',1);
    }
    
    function openCodeDialog(codeid,mytarget,managerstr,flag) 
    {
        var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
        if(mytarget==null)
          return;
        var oldInputs=document.getElementsByName(mytarget);
        oldobj=oldInputs[0];
        //根据代码显示的对象名称查找代码值名称	
        target_name=oldobj.name;
        hidden_name=target_name.replace("viewvalue","itemvalues"); 
        var hiddenInputs=document.getElementsByName(hidden_name);
        if(hiddenInputs!=null&&hiddenInputs.length>0)
        {
        	hiddenobj=hiddenInputs[0];
        	codevalue=managerstr;
        }else{
        	hiddenobj=document.getElementById(hidden_name);
        	codevalue=managerstr;
        }
        var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,flag);  
        if(codeid == "UN"){
        	ctrl_type='${orgPreForm.ctrl_type}';
        	levelctrl = '${orgPreForm.levelctrl}';
        	thecodeurl="/org/orgpre/getorgcode.jsp?ctrl_type="+ctrl_type+"&levelctrl="+levelctrl;
        }else
            thecodeurl="/system/codeselectposinputpos.jsp?codesetid="+codeid+"&codeitemid=&isfirstnode=" + flag; 
        var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
        var popwin= window.showModalDialog(thecodeurl, theArr, 
            "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    }
    
    function fieldcodes(sourceobj)
    {
    var targetobj,target_name,hidden_name,hiddenobj;
        target_name=sourceobj.name;
          hidden_name=target_name.replace("viewvalue","itemvalues");
        var hiddenInputs=document.getElementsByName(hidden_name);    
        if(hiddenInputs!=null)
        {
        	hiddenobj=hiddenInputs[0];    	
        	codevalue="";
        }   
        hiddenobj.value=sourceobj.value;
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
 	        if(navigator.appName.indexOf("Microsoft")!= -1){
 	    		style.posLeft=pos[0]-1;
 				style.posTop=pos[1]-1+srcobj.offsetHeight;
 			}else{
 				style.left=pos[0]+"px";
 				style.top=pos[1]+srcobj.offsetHeight+"px";
 			}
 			style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
        }                 
        //}
    }

    var date_desc;
    function setSelectValue(){
    	if(date_desc){
    		date_desc.value=$F('date_box');
           	Element.hide('date_panel'); 
    	}
    }
    
    function subform(){
    	document.getElementById("itemlength").value=rownum;
    	orgPreForm.action="/org/orgpre/orgpretable.do?b_search2=link";
    	orgPreForm.submit();
    }
    
    function IsDigit(){ 
        return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
    }
    
    initfields(document.getElementById("setbox"));
</script>
