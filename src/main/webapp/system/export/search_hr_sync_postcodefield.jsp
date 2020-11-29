<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">

   function savefield()
  {  	  
     var hashvo=new ParameterSet();          
     var vos= document.getElementById("right");
     if(vos==null)
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
        }       
     }
     hashvo.setValue("code_fields",code_fields); 
     var request=new Request({method:'post',onSuccess:showSelect,functionId:'1010100128'},hashvo);
   }	

   function showSelect(outparamters)
   { 
     var types=outparamters.getValue("types");          
     if(types=="ok")
     {
        //alert("编辑成功");
        var mess=outparamters.getValue("mess");
        var thevo=new Object();
		thevo.mess=mess;
		if(parent.parent.Ext && parent.parent.Ext.getCmp('select_postcodefield')){
			 parent.parent.Ext.getCmp('select_postcodefield').return_vo = thevo;	
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
   	   var request=new Request({method:'post',asynchronous:false,onSuccess:showSetList,functionId:'1010100126'},hashvo);
	}
	
	/**从后台取得相应的数据,初始化前台*/
	function showSetList(outparamters)
	{
		var setlist=outparamters.getValue("setlist");
		var itemlist = outparamters.getValue("itemlist");
		AjaxBind.bind(hrSyncForm.left_fields,setlist);
		AjaxBind.bind(hrSyncForm.right_fields,itemlist);	
	}
    //关闭弹窗方法  wangb 20190320
    function winclose(){
    	if(parent.parent.Ext && parent.parent.Ext.getCmp('select_postcodefield')){
			var win = parent.parent.Ext.getCmp('select_postcodefield');
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
   	   <tr>
            <td width="100%" align="center" nowrap>
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
                         <html:select styleId="left" name="hrSyncForm" property="left_fields" multiple="multiple" size="10" ondblclick="additem('left_fields','right_fields')" style="height:250px;width:100%;font-size:9pt">
	 		     	  	 </html:select>
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
