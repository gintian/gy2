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
       alert(ALREADY_CHOICE_IDENTIFIER_NOT_EMPTY);
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
          		alert(SAME_ITEM_RESET_ITEM);
          		return false;
          	}
          }
        }       
     }
     hashvo.setValue("code_fields",code_fields); 
     hashvo.setValue("field_falg","${engageParamForm.field_falg}");
     var request=new Request({method:'post',onSuccess:showSelect,functionId:'3970004004'},hashvo);
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
		window.returnValue=thevo;
		window.close();        
     }else
     {
        alert(EDITFAILING);
     }     
   }
   function searchFieldList()
	{
	   var tablename=$F('setlist');
	   var in_paramters="tablename="+tablename;
   	   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'0520000002'});
	}
	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");
		AjaxBind.bind(engageParamForm.left_fields,fieldlist);
	}
	
	function MusterInitData(infor)
	{
	   var pars="base="+infor;
	   var hashvo=new ParameterSet();
	   hashvo.setValue("field_falg","${engageParamForm.field_falg}");
   	   var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:showSetList,functionId:'3970004003'},hashvo);
	}
	
	/**从后台取得相应的数据,初始化前台*/
	function showSetList(outparamters)
	{
		var setlist=outparamters.getValue("setlist");
		var itemlist = outparamters.getValue("itemlist");
		var infor = outparamters.getValue("base");
		if(infor==1)
		{
			AjaxBind.bind(engageParamForm.setlist,setlist);
			if($('setlist').options.length>0)
			{
		  		$('setlist').options[0].selected=true;
		  		$('setlist').fireEvent("onchange");
			}
		}
		else if(infor==2)
			AjaxBind.bind(engageParamForm.left_fields,setlist);
		AjaxBind.bind(engageParamForm.right_fields,itemlist);
		
		
	}
     
</script>
<html:form action="/hire/jp_contest/param/engageparam">

<table width="95%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap colspan="3">
		<bean:message key="label.query.selectfield"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap>
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
                         <select name="left_fields" multiple="multiple" ondblclick="additem('left_fields','right_fields');" style="height:209px;width:100%;font-size:9pt">
                         </select>
                    </td>
                    
                    </tr>
                   
                   </table>
                </td>
               
                <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('left_fields','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');">
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
                  <html:select styleId="right" name="engageParamForm" property="right_fields" multiple="multiple" size="10" ondblclick="removeitem('right_fields');" style="height:230px;width:100%;font-size:9pt">
 		     </html:select>     
                  </td>
                  </tr>
                  </table>             
                </td>
                <td width="8%" align="center">
							<html:button styleClass="mybutton" property="b_up" onclick="upItem($('right_fields'));">
								<bean:message key="button.previous" />
							</html:button>
							<br>
							<br>
							<html:button styleClass="mybutton" property="b_down" onclick="downItem($('right_fields'));">
								<bean:message key="button.next" />
							</html:button>
						</td>               
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap  colspan="3">
               <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" onclick=" savefield();">
	     <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' class="mybutton" onclick=" window.close();">
          </td>
          </tr>
</table>
</html:form>

<script language="javascript">
   //var ViewProperties=new ParameterSet();
  	MusterInitData('1');
</script>
