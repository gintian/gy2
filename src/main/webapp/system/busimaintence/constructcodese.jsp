<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script language="javascript">
  function save()
  {
     	//setselectitem('right_fields');
     	//setselectitem('left_fields');
     	//busiMaintenceForm.action="/system/busimaintence/constructcodeset.do?b_save=link&id=${busiMaintenceForm.id}";
	    //busiMaintenceForm.submit();
	    var vos= document.getElementById("right");
	    if(vos.length==0)
     	{
       		alert("已选指标项不能为空！");
       		return false;
     	}
     	else
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
     var fieldsetid = document.getElementById("statusTypeID").value;
     var id = document.getElementById("fid").value;
     var strurl="/system/busimaintence/constructcodeset.do?b_dbname=link`tableid="+fieldsetid+"`id="+id;
     var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl;
     /*
     var return_vo= window.showModalDialog(iframe_url, 'template_win', 
      				"dialogWidth:410px; dialogHeight:180px;resizable:no;center:yes;scroll:yes;status:no");
     if(return_vo!=null){
     	 var hashvo=new ParameterSet();
     	 hashvo.setValue("fieldsetid",fieldsetid);
	     hashvo.setValue("code_fields",code_fields); 
	     hashvo.setValue("id",id);
	     var request=new Request({method:'post',onSuccess:showSelect,functionId:'1010061010'},hashvo);
     }
     */
     //改用ext 弹窗显示  wangb 20190323
     var win = Ext.create('Ext.window.Window',{
			id:'input_dbname',
			title:'',
			width:430,
			height:200,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.return_vo && this.return_vo!=null){
     	 				var hashvo=new ParameterSet();
     	 				hashvo.setValue("fieldsetid",fieldsetid);
	     				hashvo.setValue("code_fields",code_fields); 
	     				hashvo.setValue("id",id);
	     				var request=new Request({method:'post',onSuccess:showSelect,functionId:'1010061010'},hashvo);
     				}
				}
			}
	}); 
  }
  function showSelect(outparamters)
   { 
   		parent.frames["mil_menu"].location.reload();
     	//busiMaintenceForm.action="/system/busimaintence/constructcodeset.do?b_save=link";
	    //busiMaintenceForm.submit();
   }
  function searchFieldList()
  {
  		var id = document.getElementById("fid").value;
  		var fieldsetid = document.getElementById("statusTypeID").value;
  		var hashvo=new ParameterSet();
  		hashvo.setValue("fieldsetid",fieldsetid);
  		hashvo.setValue("id",id);
  		var request=new Request({method:'post',onSuccess:showFieldList,functionId:'1010061006'},hashvo);
  		
  }
  function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("leftlist");
		AjaxBind.bind(busiMaintenceForm.left_fields,fieldlist);
		var itemlist = outparamters.getValue("rightlist");
		AjaxBind.bind(busiMaintenceForm.right_fields,itemlist);
	}
	
</script>
<html:form action="/system/busimaintence/constructcodeset">
<div id="first" style="filter:alpha(Opacity=100);display=block;">
<table width="700" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="">
               新建应用库 
            </td>
            <td>
            	<html:hidden styleId="fid" name="busiMaintenceForm" property="id"/>
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap>
              <table>
                <tr>
                 <td align="center"  width="46%" valign="top">
                   <table align="center" width="100%" >
                    <tr>
                    <td align="left">
                        备选指标&nbsp;&nbsp;
                    </td>
                    </tr>
                    <tr>
                      <td align="center">
			            <html:select name="busiMaintenceForm" property="id" size="1"  styleId="statusTypeID" style="width:100%"  disabled="disabled" onchange="searchFieldList();">
                          <html:optionsCollection property="syselist" value="dataValue" label="dataName"/>	        
                        </html:select>
                      </td>
                    </tr>
                   <tr>
                       <td align="center">
                         <html:select name="busiMaintenceForm" property="left_fields" multiple="multiple" size="10" ondblclick="if(additem('left_fields','right_fields'))removeitem('left_fields');" style="height:250px;width:100%;font-size:9pt">
                              		      
 		                 </html:select>
                       </td>
                    </tr>
                   </table>
                </td>
               
                <td width="4%" align="center">
                   <html:button  styleClass="smallbutton" property="b_addfield" onclick="if(additem('left_fields','right_fields'))removeitem('left_fields');">
            		     <bean:message key="button.setfield.addfield"/>
	               </html:button >
	           
	          <!-- <br>
	           <br><html:button  styleClass="mybutton" property="b_delfield" onclick="additem('right_fields','left_fields');removeitem('right_fields');">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button >	     -->  
                  <!--   <html:button  styleClass="mybutton" property="b_up" onclick="upItem($('right_fields'));">
            		     <bean:message key="button.previous"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_down" onclick="downItem($('right_fields'));">
            		     <bean:message key="button.next"/>    
	           </html:button >	     -->
	           <html:button  styleClass="smallbutton" property="b_down" onclick="additem('right_fields','left_fields');removeitem('right_fields');" style="margin-top:30px;">
            		     <bean:message key="button.delete"/>    
	           </html:button >
                </td>            
                <td width="46%" align="center">
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                     已选指标&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
                  
 		            <html:select styleId="right" name="busiMaintenceForm" property="right_fields" multiple="multiple" size="10"  ondblclick="additem('right_fields','left_fields');removeitem('right_fields');" style="height:279px;width:100%;font-size:9pt">
 		             
 		            </html:select>
                  </td>
                  </tr>
                  </table>             
                </td>
                </tr>
              </table>             
            </td>
            </tr>
            <tr>
            <td colspan="3" align="center" height="35px;">
               
				<html:button  styleClass="mybutton" property="b_addfield" onclick="save();">
				        <bean:message key="button.ok" />
	           </html:button >
				<hrms:submit styleClass="mybutton" property="br_return">
						<bean:message key="button.return" />
				</hrms:submit>
				
            </td>
            </tr>
           
</table>
</div>
</html:form>
<script>
if(getBrowseVersion()){//ie兼容模式下 样式修改  wangb 20190426 bug 47270
	var b_addfield = document.getElementsByName('b_addfield')[0];
	b_addfield.style.display = 'block';
}
</script>
