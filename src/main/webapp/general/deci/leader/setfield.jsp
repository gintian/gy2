<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
   function change()
   {
      setselectitem('right_fields');
      leaderParamForm.action="/general/deci/leader/param.do?b_addfeild=link";
      leaderParamForm.submit();
   }
	function savephotofield(){
		var hashvo=new ParameterSet();
     	var vos= document.getElementById("right");
       	var code_fields=new Array();
	    if(vos.length==0)
	    {
	    }else
	    {
	       	if(vos.length>4){
				alert("显示指标不能多于4个！");
	       		return false;
			}else if(vos.length>=1){//【bug11022】一个指标时也提示 add hej 2015.7.15
				var allvalue="";
				for(var i=0;i<vos.length;i++){
					allvalue+=vos.options[i].value+",";
				}
				if(!(allvalue.indexOf("a0101")!=-1))
		     	{
		     		alert("必须选择姓名指标！");
		     		return false;
		     	}
			}        
			for(var i=0;i<vos.length;i++)
	        {
	        	var valueS=vos.options[i].value; 
	         	code_fields[i]=valueS;
		        for(var j=i+1;j<vos.length;j++)
		        {
		        	if(valueS==vos.options[j].value)
		         	{
		         		alert("有相同指标存在，请重新选择！");
		         		return false;
		         	}
		     	}
	    	}       
		}
		hashvo.setValue("code_fields",code_fields); 
     	hashvo.setValue("field_falg","${leaderParamForm.field_falg}");   
     	var request=new Request({method:'post',onSuccess:showSelect,functionId:'05603000005'},hashvo);
	}
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
          		alert("有相同指标存在，请重新选择！");
          		return false;
          	}
          }
        }       
     }
     hashvo.setValue("code_fields",code_fields); 
     hashvo.setValue("field_falg","${leaderParamForm.field_falg}");   
     var request=new Request({method:'post',onSuccess:showSelect,functionId:'05603000005'},hashvo);
   }	
   function showSelect(outparamters)
   { 
     var types=outparamters.getValue("types");          
     if(types=="ok")
     {
        //alert("编辑成功");
        var mess=outparamters.getValue("mess");
        var code=outparamters.getValue("code");
        var thevo=new Object();
		thevo.mess=mess;
		thevo.code=code;
		if(parent.parent.Ext && parent.parent.Ext.getCmp('org_setfield')){
			var extWin = parent.parent.Ext.getCmp('org_setfield');
			extWin.msg = thevo;
			extWin.close();
		}else if(parent.parent.Ext && parent.parent.Ext.getCmp('select_field')){
			var win = parent.parent.Ext.getCmp('select_field');
			win.return_vo = thevo;
			win.close();
		}else
			parent.window.returnValue=thevo;
			window.close();        
     }else
     {
        alert("编辑失败");
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
		AjaxBind.bind(leaderParamForm.left_fields,fieldlist);
	}
	
	function MusterInitData(infor)
	{
	   var pars="base="+infor;
	   var hashvo=new ParameterSet();
	   hashvo.setValue("field_falg","${leaderParamForm.field_falg}");
   	   var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:showSetList,functionId:'05603000019'},hashvo);
	}
	
	/**从后台取得相应的数据,初始化前台*/
	function showSetList(outparamters)
	{
		var setlist=outparamters.getValue("setlist");
		var itemlist = outparamters.getValue("itemlist");
		var infor = outparamters.getValue("base");
		if(infor==1)
		{
			AjaxBind.bind(leaderParamForm.setlist,setlist);
			if($('setlist').options.length>0)
			{
		  		$('setlist').options[0].selected=true;
		  		//$('setlist').fireEvent("onchange");
		  		$('setlist').onchange();
			}
			AjaxBind.bind(leaderParamForm.right_fields,itemlist);
		}
		else if(infor==2)
		{
			AjaxBind.bind(leaderParamForm.left_fields,setlist);
			AjaxBind.bind(leaderParamForm.right_fields,itemlist);
		}else if(infor==3){
			AjaxBind.bind(leaderParamForm.setlist,setlist);
			if($('setlist').options.length>0)
			{
		  		$('setlist').options[0].selected=true;
		  		//$('setlist').fireEvent("onchange");
		  		$('setlist').onchange();
			}
		}
		
		
	}   
	//关闭弹窗  wangb 20190319
	function winclose(){
		if(parent.parent.Ext && parent.parent.Ext.getCmp('org_setfield'))
			parent.parent.Ext.getCmp('org_setfield').close();
		else if(parent.parent.Ext &&parent.parent.Ext.getCmp('select_field'))
			parent.parent.Ext.getCmp('select_field').close();
		else 
			window.close();
	}  
</script>
<html:form action="/general/deci/leader/param">
<logic:notEqual name="leaderParamForm" property="field_falg" value="unitfile">

<table width="530" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable1">
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
                  <hrms:optioncollection name="leaderParamForm" property="photoitemlist" collection="selectedlist"/> <!--缺陷2962 zgd 2014-7-15 右侧指标框中显示上次所选 -->
                  <html:select styleId="right" name="leaderParamForm" property="right_fields" multiple="multiple" size="10" ondblclick="removeitem('right_fields');" style="height:230px;width:100%;font-size:9pt">
 		     	  	  <html:options collection="selectedlist"  property="dataValue" labelProperty="dataName"/>
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
          <td align="center" class="RecordRow" nowrap  colspan="3" style="height: 35px">
          <logic:equal name="leaderParamForm" property="field_falg" value="photo_other_view">
               <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" onclick="savephotofield();">
          </logic:equal>
          <logic:notEqual name="leaderParamForm" property="field_falg" value="photo_other_view">
               <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" onclick="savefield();">
          </logic:notEqual>
	     <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' class="mybutton" onclick="winclose();">
          </td>
          </tr>
</table>
</logic:notEqual>
<logic:equal name="leaderParamForm" property="field_falg" value="unitfile">
<table width="530" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable1">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="3">
				<bean:message key="label.query.selectfield"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" style="border-right:0px;" nowrap>
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
                         <select name="left_fields" multiple="multiple" ondblclick="additem('left_fields','right_fields');" style="height:209px;width:100%;font-size:9pt">
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
                  <html:select styleId="right" name="leaderParamForm" property="right_fields"  multiple="multiple" size="10" ondblclick="removeitem('right_fields');" style="height:230px;width:100%;font-size:9pt">
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
          <td align="center" class="RecordRow" nowrap  colspan="3" style="height: 35px">
               <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" onclick="savefield();">
	      <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' class="mybutton" onclick="winclose();">
          </td>
          </tr>
</table>
</logic:equal>
</html:form>
<script language="javascript">
   //var ViewProperties=new ParameterSet();
   if("${leaderParamForm.field_falg}"=="photo_other_view")
   		MusterInitData('3');
   else if("${leaderParamForm.field_falg}"=="unitfile")
   		MusterInitData('2');
   else
  		MusterInitData('1');
  
   if(!getBrowseVersion() || getBrowseVersion() == 10){//非ie浏览器样式问题处理   wangb 20190523 bug 48244
   		var td = document.getElementsByName('leaderParamForm')[0].getElementsByClassName('ListTable1')[0].getElementsByTagName('tr')[1].getElementsByTagName('td')[0];
   		td.style.borderRight='';
   }
</script>
