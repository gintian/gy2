<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
	/* 兼容fireEvent方法 */
 	function myFireEvent(el) { 
  		var evt; 
  		if (document.createEvent) {
   			evt = document.createEvent("MouseEvents"); 
   			evt.initMouseEvent("change", true, true, window, 
   			0, 0, 0, 0, 0, false, false, false, false, 0, null); 
   			el.dispatchEvent(evt); 
  		} else if (el.fireEvent) { // IE 
   			el.fireEvent("onchange"); 
  		} 
 	}
	
   function savefield()
  {  	  
     var hashvo=new ParameterSet();          
     var vos= document.getElementById("right");
     //var appfield= document.getElementById("appfield").value;
     //if(appfield==null||appfield=="")
     //{
     //	alert("自定义指标名称不能为空！");
	//	return false;
     //}
     if(vos==null||vos.length==0)
     {
       alert("已选指标项不能为空！");
       return false;
     }else
     {
        var code_fields=new Array();        
        for(var i=0;i<vos.length;i++)
        {
          var valueS=vos.options[i].value;          
          code_fields[i]=valueS;
          for(var j=i+1;j<vos.length;j++)
          {
          	if(valueS==vos.options[j].value)
          	{
          		alert("有相同指标存在，请重新选择");
          		return false;
          	}
          }
          //if(i>0)
          //	{
          //		alert("只能选择1个指标！");
          //		return false;
          //	}
          //	var valueS=vos.options[i].value;          
          //	code_fields[i]=valueS;
        }       
     }
     hashvo.setValue("code_fields",code_fields); 
     //hashvo.setValue("appfield",appfield);
     hashvo.setValue("type","${hrSyncForm.type}");
     var request=new Request({method:'post',onSuccess:showSelect,functionId:'1010100116'},hashvo);
   }	

   function showSelect(outparamters)
   { 
     var types=outparamters.getValue("types");          
     if(types=="ok")
     {
        //alert("编辑成功");
        var mess=outparamters.getValue("mess");
        var mess2=outparamters.getValue("mess2");
        var thevo=new Object();
		thevo.mess=mess;
		thevo.mess2=mess2;
		if(parent.parent.Ext && parent.parent.Ext.getCmp('select_field')){
			 parent.parent.Ext.getCmp('select_field').return_vo = thevo;	
		}else{
			window.returnValue=thevo;
		}
		//window.close();
		winclose();          
     }else
     {
        alert("编辑失败");
     }     
   }
   
   function searchFieldList()
	{
	   var hashvo=new ParameterSet();
	   var tablename=$F('setlist');
	   var in_paramters="tablename="+tablename;
	   hashvo.setValue("type","change");
   	   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'1010100107'},hashvo);
	}
	
	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");
		AjaxBind.bind(hrSyncForm.left_fields,fieldlist);
	}
	
	function MusterInitData()
	{
	   var hashvo=new ParameterSet();
	   hashvo.setValue("type","init");
   	   var request=new Request({method:'post',asynchronous:false,onSuccess:showSetList,functionId:'1010100113'},hashvo);
	}
	
	/**从后台取得相应的数据,初始化前台*/
	function showSetList(outparamters)
	{
		var setlist=outparamters.getValue("setlist");
		var itemlist = outparamters.getValue("itemlist");
		AjaxBind.bind(hrSyncForm.setlist,setlist);
			if($('setlist').options.length>0)
			{
		  		$('setlist').options[0].selected=true;
		  		//$('setlist').fireEvent("onchange");
		  		myFireEvent($('setlist'));
			}
		AjaxBind.bind(hrSyncForm.right_fields,itemlist);	
	}
	function IsDigit()
    {
    	return (((event.keyCode > 47) && (event.keyCode <= 57))|| ((event.keyCode >= 65)&& (event.keyCode <= 90))|| ((event.keyCode >= 97)&& (event.keyCode <= 122))|| (event.keyCode == 95));
    }
    //关闭弹窗方法  wangb 20190320
    function winclose(){
    	if(parent.parent.Ext && parent.parent.Ext.getCmp('select_field')){
			var win = parent.parent.Ext.getCmp('select_field');
			win.close();
			return;
		}
    	window.close();
    }
</script>
<html:form action="/sys/export/SearchHrSyncFiled">

<table width="530" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="3">
		<bean:message key="label.query.selectfield"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr class="trDeep">
            <td width="100%" align="center" nowrap >
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
                     <select name="setlist" size="1"  style="width:100%" onchange="searchFieldList();">    
			    	 <option value="1111">#</option>
                     </select>
                      </td>
                    </tr>
                    <tr>
                       <td align="center">
                         <select name="left_fields" multiple="multiple" ondblclick="additem('left_fields','right_fields');" style="height:229px;width:100%;font-size:9pt">
                         </select>
                    </td>
                    
                    </tr>
                   
                   </table>
                </td>
               
                <td width="4%" align="center">
                   <html:button  styleClass="smallbutton" property="b_addfield" onclick="additem('left_fields','right_fields');">
            		    <bean:message key="button.setfield.addfield"/> 
	           	   </html:button >
	           <html:button  styleClass="smallbutton" property="b_delfield" onclick="removeitem('right_fields');" style="margin-top:30px;">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button >	     
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
	                  <html:select styleId="right" name="hrSyncForm" property="right_fields" multiple="multiple" size="10" ondblclick="removeitem('right_fields');" style="height:250px;width:100%;font-size:9pt">
	 		     	  </html:select>     
	                  </td>
	                  </tr>
	                  <tr>
	                  <!-- td width="100%" align="left">
	                  	自定义指标名称
	                  </td>
	                  </tr>
	                  <tr>
	                  <td width="100%" align="left">
	                  	<html:text styleId ="appfield" name="hrSyncForm" property="appfield" maxlength="30" onkeypress="event.returnValue=IsDigit();"/>
	                  </td>
	                  </tr-->
	                  </table>             
                </td>
                <td width="4%" align="center">
					<html:button styleClass="smallbutton" property="b_up" onclick="upItem($('right_fields'));">
						<bean:message key="button.previous" />
					</html:button>
					<html:button styleClass="smallbutton" property="b_down" onclick="downItem($('right_fields'));" style="margin-top:30px;">
						<bean:message key="button.next" />
					</html:button>
				</td>               
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap  colspan="3" style="padding-top:5px;padding-bottom:5px;">
               <input type="button" name="btnreturn" value='确定' class="mybutton" onclick=" savefield();">
	     <input type="button" name="btnreturn" value='关闭' class="mybutton" onclick="winclose();">
          </td>
          </tr>
</table>
</html:form>

<script language="javascript">
  		MusterInitData();
  		if(!getBrowseVersion()){//非ie浏览器样式兼容  wangb 20190320
			var table = document.getElementsByClassName('RecordRow')[0];
			var td = table.getElementsByTagName('tr')[1].getElementsByTagName('td')[0];
			td.style.borderRight='#C4D8EE 1pt solid';
		}
</script>
