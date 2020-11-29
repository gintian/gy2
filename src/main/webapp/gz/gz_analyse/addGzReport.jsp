<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <head>
  </head>
  <style type="text/css">
	div#scroll_box {
	           border: 1px solid #ccc;
	           height: 150px;    
	           width: 270px;            
	           overflow: auto;            
	           margin: 1em 0;
	       }
	</style>
  <script language='javascript' >
  
  function changeFieldSet()
  {
    var setList = $("salaryid");
    if(setList)
    {
       var id="-1";
       for(var i=0;i<setList.options.length;i++)
       {
         if(setList.options[i].selected)
         {
            id=setList.options[i].value;
            break;
         }
       }
       	var gz_module = "${gzAnalyseForm.gz_module}";   
       	var rsid = "${gzAnalyseForm.rsid}";   
       	var buf="";
       	var rf=$('right_fields');
       	 for(var i=0;i<rf.options.length;i++)
        {
          buf+=",/"+rf.options[i].value+"/";
        }
        var hashvo=new ParameterSet();	
        hashvo.setValue("setid",id);
        hashvo.setValue("gz_model",gz_module);
        hashvo.setValue("buf",buf);
        hashvo.setValue("rsid",rsid);
     	var In_paramters="flag=1"; 	
    	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:resultChangeFieldSet,functionId:'3020130023'},hashvo);	
    }
    else
    {
      return;
    }
  }
   function resultChangeFieldSet(outparamters){
  	var fielditemlist=outparamters.getValue("resumeFieldsList");
	AjaxBind.bind(gzAnalyseForm.itemid,fielditemlist);
	
  }
 function sub()
 {
    var name=gzAnalyseForm.rsname.value;
    if(trim(name).length==0)
    {
       alert("名称不能为空");
       return;
    }
    var rf=$('right_fields');
    var num=0;
    var buf="";
    for(var i=0;i<rf.options.length;i++)
    {
       buf+=",/"+rf.options[i].value+"/";
       num++;
    }
    if(num==0)
    { 
        alert("请选择工资项目");
        return;
    }
    $('selectedids').value=buf;
    var id="${gzAnalyseForm.reportTabId}";
    if(parseInt(id)==7||parseInt(id)==11)
    {
       var element=document.getElementById("groupby");
       if(element.checked)
       {
         element.value="1";
       }
       else
       {
         element.value="0";
         element.checked=true;
       }
    }
    gzAnalyseForm.action="/gz/gz_analyse/addGzReport.do?b_save=save&isclose=1";
    gzAnalyseForm.submit();
    }
function isclose(isclose)
    {
     if(isclose=="1")
     {
       returnValue="11";
       window.close();
     }
}
  </script>
  
  <body>
   <html:form action="/gz/gz_analyse/addGzReport">

   <hrms:tabset name="pageset" width="585px;" height="340" type="false"> 
  		<hrms:tab name="tab1" label="基本信息" visible="true">
		 <table width="100%"  height='100%'   align="center"> 
			<tr> <td class="framestyle" valign="top"  align='center'>
				<Br>
				 <table width="90%"  border="0" align='center' cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
			             <tr>
				               <td width="15%" height="30" >
				                  名&nbsp;称&nbsp;<input type='text' size='30' class="text4" maxlength="40" name='rsname' value='${gzAnalyseForm.rsname}' />
				               </td>
				               <td>
				               
				              &nbsp;&nbsp;&nbsp;&nbsp; <html:radio property="ownerType" value="0" name="gzAnalyseForm">公有</html:radio>
				               &nbsp;&nbsp;&nbsp;
				               <html:radio property="ownerType" value="1" name="gzAnalyseForm">私有</html:radio>
							   </td>
				         </tr>		 
				         <tr>
				         <td  colspan="2">
				  
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
			 <hrms:optioncollection name="gzAnalyseForm" property="setList" collection="list" />
		             <html:select styleId="sid" name="gzAnalyseForm" property="salaryid" size="1" onchange="changeFieldSet();" style="width:100%">
		             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>		
                      </td>
                    </tr>
                   <tr>
                       <td align="center">
                     <hrms:optioncollection name="gzAnalyseForm" property="itemlist" collection="list"/>
		              <html:select name="gzAnalyseForm" size="8" property="itemid" multiple="multiple" ondblclick="additem('itemid','right_fields');removeitem('itemid');" style="height:180px;width:100%;font-size:9pt">
		              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>		
                       </td>
                    </tr>
                   </table>
                </td>
               
                <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('itemid','right_fields');removeitem('itemid');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="additem('right_fields','itemid');removeitem('right_fields');">
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
 		     
 		        <hrms:optioncollection name="gzAnalyseForm" property="selectedList" collection="list"/>
		              <html:select name="gzAnalyseForm" styleId="rf" size="8" property="right_fields" multiple="multiple" ondblclick="additem('right_fields','itemid');removeitem('right_fields');" style="height:211px;width:100%;font-size:9pt">
		              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>	
 		    
 		    
 		    
 		    
 		    
 		     
                 </td>
                  </tr>
                  </table>             
                </td>                         
                </tr>
              </table>             
				         		         
				         </td>
				         </tr>
	  			 </table>
			</td></tr>
		 </table>
		</hrms:tab>

   <logic:equal   name="gzAnalyseForm" property="reportTabId" value="7" >
  		<hrms:tab name="tab2" label="其他选项" visible="true">
		 <table width="100%"  height='100%'  align="center"> 
			<tr> <td class="framestyle" valign="top"  align='left'>
		    <br>
		    <br>
		    <br>
	  		<html:checkbox name="gzAnalyseForm" property="bgroup" styleId="groupby" value="1">按部门分页打印</html:checkbox>
			</td></tr>
		 </table>
		</hrms:tab>
  </logic:equal>
  <logic:equal   name="gzAnalyseForm" property="reportTabId" value="11" >
  		<hrms:tab name="tab2" label="其他选项" visible="true">
		 <table width="100%"  height='100%'  align="center"> 
			<tr> <td class="framestyle" valign="top"  align='left'>
		    <br>
		    <br>
		    <br>
	  		<html:checkbox name="gzAnalyseForm" property="bgroup" styleId="groupby" value="1">按单位分页打印</html:checkbox>
			</td></tr>
		 </table>
		</hrms:tab>
  </logic:equal>
     </hrms:tabset>
   <table width="90%"  border="0" align='center' >
   <tr>
   <td align="center">
     <Input type='button' value='确 定'  class="mybutton"  onclick='sub()' /> 
  	 <Input type='button' value='取 消'  class="mybutton"  onclick='window.close()' /> 
   	<input type="hidden" id="sti" name="selectedids" value=""/>
   	 <input type='hidden' name='rsdtlid' value='${gzAnalyseForm.rsdtlid}'/>
   	 <input type='hidden' name='reportTabId' value='${gzAnalyseForm.reportTabId}'/>
   	 </td>
   	 </tr>
   	 </table>
 </html:form>
 <script type="text/javascript">
 isclose(<%=request.getParameter("isclose")%>);
 </script>
  </body>
</html>
