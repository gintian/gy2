<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="javascript">
	function InitSetData(){
		var url = decodeURIComponent(location.href); 
		var baseId = url.substring(url.indexOf("baseId=")+7);
		var paramters = url.substring(url.indexOf("&"));
		document.getElementById("base_id").setAttribute("value",baseId);
		var hashvo = new ParameterSet();
		hashvo.setValue("paramters",paramters);
		var request=new Request({asynchronous:false,onSuccess:showSetList,functionId:'10400201055'},hashvo);
	}
	
	function showSetList(outparamters){
		var queryfieldlist = outparamters.getValue("queryfieldlist");
		var queryfieldlist1 = outparamters.getValue("queryfieldlist1");
		var queryfieldlist2 = outparamters.getValue("queryfieldlist2");
		var flag = outparamters.getValue("selectFlag");
	    if(flag == "left"){
	    	var s = outparamters.getValue("table_fields");
	    	var b = outparamters.getValue("table_value");
	    	document.getElementById("table_fields").value = s;
	    	document.getElementById("table_value").value = b;
	    	document.getElementById("selectFlag").value = flag;
	    	AjaxBind.bind(lawbaseForm.left_fields1,queryfieldlist2);
		    AjaxBind.bind(lawbaseForm.right_fields1,queryfieldlist);
	    }else{
		    AjaxBind.bind(lawbaseForm.left_fields1,queryfieldlist);
	    	AjaxBind.bind(lawbaseForm.right_fields1,queryfieldlist1);
	    }
	}
	
	function save(){
  		var arrField = $('right_fields1').options;
  		var fields = "";
  		var fieldsname = "";
  		for(var i=0;i<arrField.length;i++){
  			fields = fields + arrField[i].value + ",";
  			fieldsname = fieldsname + arrField[i].innerHTML + "、";
  		}
  		
  		if(fields && fields.length >　0) {
  			fields = fields.substring(0, fields.length - 1);
  			fieldsname = fieldsname.substring(0, fieldsname.length - 1);
  		}
  		
		var arrFields = $('left_fields1').options;
		var titleItem ="";
		for(var j=0;j<arrFields.length;j++){
			if(arrFields[j].value == "title")
				titleItem = arrFields[j].innerHTML;
		}
		var flag = document.getElementById("selectFlag").value;
		var s,b = "";
		if("left" == flag){
			if(fields.indexOf("title") == -1 && "" != fields){
				alert("备选指标指标中'"+titleItem+"'为必选项！");
				return;
			}
			s = document.getElementById("table_fields").value;
			b = document.getElementById("table_value").value;
		}else{
			s = document.getElementById("usable_fields").value;
			b = document.getElementById("usable_value").value;
		}
	    var hashvo = new ParameterSet();
	    var baseId = document.getElementById("base_id").value;
	    hashvo.setValue("fields",fields);
	    hashvo.setValue("fieldsname",fieldsname);
	    hashvo.setValue("base_id",baseId);
	    hashvo.setValue("save_flag",flag);
	    hashvo.setValue("s",s);
	    hashvo.setValue("b",b);
        var request=new Request({asynchronous:false,onSuccess:save_ok,functionId:'10400201059'},hashvo);
	}
	
	function save_ok(outparamters){
		var isok = outparamters.getValue("isok");
		var base_id = outparamters.getValue("base_id");
		var file_index_fields = outparamters.getValue("file_index_fields");
		var file_index_value = outparamters.getValue("file_index_value");
		var right_fields = outparamters.getValue("right_fields");
		var right_value = outparamters.getValue("right_value");
		var thevo=new Object();
        thevo.flag="true";
        thevo.file_index_fields = file_index_fields;
        thevo.file_index_value = file_index_value;
        thevo.right_fields = right_fields;
        thevo.right_value = right_value;
        window.returnValue=thevo;
        window.close();
	}
	
	function edit_ok(outparamters){
		var queryfieldlist=outparamters.getValue("queryfieldlist");
	    AjaxBind.bind(lawbaseForm.right_fields1,queryfieldlist);
	}
	
	function additem(){
		var arrayId = "";
	  	var arrayName = "";
		var leftField = $('left_fields1').options;
		var rightField = $('right_fields1').options;
		var isC = false;
		for(var i=0;i<leftField.length;i++){
	  		if(leftField[i].selected){
	  			for(var j=0;j<rightField.length;j++){
		  			if(leftField[i].value == rightField[j].value){
		  				isC = true;
		  				rightField[j].selected = true;
		  			}else{
		  				rightField[j].selected = false;
		  			}
	  			}
	  		}
	  	}
	  	if(!isC){
	  		for(var j=0;j<rightField.length;j++){
	  			arrayId = arrayId + rightField[j].value + "`";
				arrayName = arrayName + rightField[j].innerHTML + "`";
	  		}
	  		for(var i=0;i<leftField.length;i++){
	  			if(leftField[i].selected){
	  				arrayId = arrayId + leftField[i].value + "`";
					arrayName = arrayName + leftField[i].innerHTML + "`";
	  			}
	  		}
	  		var hashvo = new ParameterSet();
			hashvo.setValue("arrayId",arrayId);
			hashvo.setValue("arrayName",arrayName);
			var request=new Request({asynchronous:false,onSuccess:add_ok,functionId:'10400201057'},hashvo);
	  	}
	}
	
	function add_ok(outparamters){
		var queryfieldlist=outparamters.getValue("queryfieldlist");
	    AjaxBind.bind(lawbaseForm.right_fields1,queryfieldlist);
	    
	}
	
	function rename(){
		if($('right_fields1').options.length > 0)
	  	{
	  		var arrField = $('right_fields1').options;
	  		var arrayId = "";
	  		var arrayName = "";
	  		var innerText = "";
	  		var isSel = false;
	  		for(var i=0;i<arrField.length;i++){
	  			if(arrField[i].selected){
	  				isSel = true;
	  				var selected_name = arrField[i].text;
	  				var dw=360,dh=180,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	  				var url = "/selfservice/lawbase/setIndex.do?br_edit=link"
	  				var return_vo = window.showModalDialog(url,selected_name,
						"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:no;status:no");
					if(return_vo){
						arrayId = arrayId + arrField[i].value + "`";
						arrayName = arrayName + return_vo + "`";
						continue;
					}
				}
				arrayId = arrayId + arrField[i].value + "`";
				arrayName = arrayName + arrField[i].text + "`";
	  		}
	  		if(!isSel){
	  			alert("请选择需要重命名的指标！");
	  		} else {
	  			var hashvo = new ParameterSet();
				hashvo.setValue("arrayId",arrayId);
				hashvo.setValue("arrayName",arrayName);
				var request=new Request({asynchronous:false,onSuccess:edit_ok,functionId:'10400201057'},hashvo);
	  		}
        }
	}
	
</script>
<html:form action="/selfservice/lawbase/setIndex">
	<html:hidden name="lawbaseForm" property="base_id" styleId="base_id"/>
	<html:hidden name="lawbaseForm" property="selectFlag" styleId="selectFlag"/>
	<html:hidden name="lawbaseForm" property="table_fields" styleId="table_fields"/>
	<html:hidden name="lawbaseForm" property="table_value" styleId="table_value"/>
	<html:hidden name="lawbaseForm" property="usable_fields" styleId="usable_fields"/>
	<html:hidden name="lawbaseForm" property="usable_value" styleId="usable_value"/>
	 <table  width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">
	 <tr align="center">
		<td valign="top" colspan="2">

	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" style="padding-left:20px;">
	<thead>
		<tr>
            <td align="center" class="TableRow" nowrap>
				<bean:message key="label.query.selectfield"/>&nbsp;&nbsp;
            </td>            	        	        	        
        </tr>
	</thead>
		<tr>
            <td width="100%" align="center" class="RecordRow" style="border-top: 0;" nowrap>
            <table>
                <tr>
                
                 <td align="center"  width="46%">
                   <table align="center" width="100%">
                    <tr>
                      <td align="left">
                        <bean:message key="selfservice.query.queryfield"/>&nbsp;&nbsp;
                      </td>
                    </tr>
                    <tr>
                       <td align="center">
                         <select name="left_fields1" multiple="multiple" ondblclick="additem('left_fields1','right_fields1');" style="height:230px;width:100%;font-size:9pt"> 
            			 	
            			 </select> 
            			 
                       </td>
                    </tr>
                  </table>
                </td>
                
               <td width="8%" align="center">
                  <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('left_fields1','right_fields1');">
           		     <bean:message key="button.setfield.addfield"/> 
                     </html:button>
	           <br>
	           <br>
		           <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields1');">
	            		     <bean:message key="button.setfield.delfield"/>    
		           </html:button>	     
               </td>  
                       
                <td width="46%" align="center">
                  <table width="100%">
	                  <tr>
		                  <td width="100%" align="left">
		                     <bean:message key="selfservice.query.queryfieldselected"/>&nbsp;&nbsp;
		                  </td>
	                  </tr>
	                  <tr>
		                  <td width="100%" align="left">
				       		  <select name="right_fields1"  multiple="multiple" size="11" ondblclick="removeitem('right_fields1');" style="height:230px;width:100%;font-size:9pt">
				 		      	
				 		      </select>
		                  </td>
	                  </tr>
                   </table>             
                 </td>
                 <td width="8%" align="center">
					<html:button styleClass="mybutton" property="b_up"
						onclick="upItem($('right_fields1'));">
						<bean:message key="button.previous" />
					</html:button>
					<br>
					<br>
					<html:button styleClass="mybutton" property="b_down"
						onclick="downItem($('right_fields1'));">
						<bean:message key="button.next" />
					</html:button>
				 </td>
                </tr>
              </table>   
            </td>
          </tr>
	</table>
	</td>
	</tr>
	</table>
	<div align="center" style="margin-top: 5px;">
         <html:button styleClass="mybutton" property="b_save" onclick="save();">
           	  <bean:message key="button.ok"/>
         </html:button> 
         <input type="button" class="mybutton" value='<bean:message key="wb.lawbase.button.rename"/>' onclick="rename();" />
         <html:button styleClass="mybutton" property="cancel" onclick="window.close();">
			<bean:message key="button.cancel" />
		</html:button>	
	</div>
</html:form>
<script language="javascript">
    InitSetData();
</script>
