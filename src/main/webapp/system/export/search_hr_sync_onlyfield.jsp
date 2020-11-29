<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>


<script language="javascript">

   function save()
  {  	  
     var hashvo=new ParameterSet();          
     var onlyfield=$F('onlyfield');     
     hashvo.setValue("onlyfield",onlyfield); 
     var request=new Request({method:'post',onSuccess:showSelect,functionId:'1010100123'},hashvo);
   }	

   function showSelect(outparamters)
   { 
     var types=outparamters.getValue("types");          
     if(types=="ok")
     {
        //alert("编辑成功");
        var mess=outparamters.getValue("onlyfieldstr");
        var thevo=new Object();
		thevo.mess=mess;
		if(parent.parent.Ext && parent.parent.Ext.getCmp('select_only_codefield')){
			 parent.parent.Ext.getCmp('select_only_codefield').return_vo = thevo;	
		}else{
			window.returnValue=thevo;
		}
		//window.close();
		winclose();            
     }else
     {
        alert("保存失败");
     }     
   }
   //关闭弹窗方法  wangb 20190320
    function winclose(){
    	if(parent.parent.Ext && parent.parent.Ext.getCmp('select_only_codefield')){
			var win = parent.parent.Ext.getCmp('select_only_codefield');
			win.close();
			return;
		}
    	window.close();
    }
 </script>
<html:form action="/sys/export/SearchHrSyncFiled">
<table width="420" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="2"  height="35px">
		人员唯一性指标&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <tr>

   	   <td width="20%" class="RecordRow" align="right" nowrap>
   	     指标集
   	   </td>
   	   <td width="80%" class="RecordRow">
   	           <html:select name="hrSyncForm" property="onlyfield" size="1">
                          <html:option value="#">请选择...&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                          <html:optionsCollection property="onlyfieldlist" value="dataValue" label="dataName"/>
               </html:select>&nbsp;
               (人员主集字符型必填项)
	    
        </td>
      </tr>       
      <tr>
      	<td align="center" class="RecordRow" style="height:35px" nowrap colspan="2">
        	<input type="button" name="btnreturn" value='保存' class="mybutton" onclick=" save();">
      	</td>
      </tr>   
</table>
</html:form>