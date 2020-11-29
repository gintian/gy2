<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
 <script language="javascript" src="/js/function.js"></script>
<script language='javascript' >
 var desc=GZ_ACCOUNTING_GZ;
 <logic:equal name="gztemplateSetForm" property="gz_module" value="1">
 desc=GZ_ACCOUNTING_POLICY;
 </logic:equal>
function add()
{
	var length = '${gztemplateSetForm.length}';
	//alert(length);
	var name=window.prompt(GZ_TEMPLATESET_INPUT+desc+GZ_TEMPLATESET_TYPENAME+"：","");
	if(name&&IsOverStrLength(trim(name),length))
	{
			alert(GZ_TEMPLATESET_INFO34+"!");
			return;
	}
	if(name&&trim(name).length>0)
	{
		name=replaceAll(name,"'","’");
		name=replaceAll(name,"\"","”");
		var hashvo=new ParameterSet();
        hashvo.setValue("name",getEncodeStr(name));
        hashvo.setValue("gz_module","${gztemplateSetForm.gz_module}");
        hashvo.setValue("type","2");
        hashvo.setValue("salaryid","-1");
        var request=new Request({asynchronous:false,onSuccess:check_ok,functionId:'3020030019'},hashvo);		 
	}

}

function sort()
{
	var url="/gz/templateset/gz_templatelist.do?br_sort=query";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+url;
    var obj=window.showModalDialog(iframe_url,null,"dialogWidth=530px;dialogHeight=320px;resizable:no;center:yes;scroll:yes;status:no");   
    if(obj!=null&&obj=='1')
    {
    	document.location="/gz/templateset/gz_templatelist.do?b_query=link&gz_module=${gztemplateSetForm.gz_module}";
    }
}
 

function del()
{
		var num=0;
		for(var i=0;i<document.gztemplateSetForm.elements.length;i++)
  		{
  			if(document.gztemplateSetForm.elements[i].type=='checkbox'&&document.gztemplateSetForm.elements[i].name!='selbox')
  			{
  				if(document.gztemplateSetForm.elements[i].checked==true)
  				{
  					num++;
  				}
  			}
  		}
  		if(num==0)
  		{
  			alert(GZ_ACCOUNTING_IFNO3+desc+GZ_TEMPLATESET_TYPE+"！");
  		    return;
  		}
		
		if(confirm(GZ_ACCOUNTING_INFO2+desc+GZ_TEMPLATESET_TYPE+"?"))
		{
		    if(confirm("删除"+desc+"类别将删除该类别的"+desc+"历史数据，再次确认是否删除？")){
		    	document.gztemplateSetForm.action="/gz/templateset/gz_templatelist.do?b_del=del";
			    document.gztemplateSetForm.submit();
			}
		}
}

function check_ok(outparameters)
{
    var type=outparameters.getValue("type");
    var name=outparameters.getValue("name");
    var msg=outparameters.getValue("msg");
    if(msg!="0")
    {
      alert(msg);
      return;
    }
    if(type=="1")
    {
       if(name&&trim(name).length>0)
		{
        	document.gztemplateSetForm.salarySetName.value=trim(name);
	    	document.gztemplateSetForm.action="/gz/templateset/gz_templatelist.do?b_saveAs=rename";
	    	document.gztemplateSetForm.submit();
	    }
     }else if(type=="2"){
        if(name&&trim(name).length>0)
		{
     		document.gztemplateSetForm.salarySetName.value=trim(name);

			
			var hashvo=new ParameterSet();
        	hashvo.setValue("salarySetName",name);
        	hashvo.setValue("isAdd","1");
        	var request=new Request({asynchronous:false,onSuccess:check_ok1,functionId:'3020030001'},hashvo);	
		}
     }
     else
     {
         if(name&&trim(name).length>0)
		{
			
			document.gztemplateSetForm.salarySetName.value=trim(name);
			document.gztemplateSetForm.action="/gz/templateset/gz_templatelist.do?b_rename=rename";
			document.gztemplateSetForm.submit();
		}
		
     }
}
function check_ok1(outparameters){
			var isAdd=outparameters.getValue("isAdd");
			document.gztemplateSetForm.action="/gz/templateset/gz_templatelist.do?b_query=add&isAdd="+isAdd;
			document.gztemplateSetForm.submit();
}
function saveAs()
{
	var num=0;
		var selectIndex=0;
		var checkNum=0;
		for(var i=0;i<document.gztemplateSetForm.elements.length;i++)
  		{
  			if(document.gztemplateSetForm.elements[i].type=='checkbox'&&document.gztemplateSetForm.elements[i].name!='selbox')
  			{
  				if(document.gztemplateSetForm.elements[i].checked==true)
  				{
  					selectIndex=checkNum;
  					num++;
  				}
  				checkNum++;
  			}
  		}
  		if(num==0)
  		{
  			alert(GZ_TEMPLATESET_INFO35+desc+GZ_TEMPLATESET_TYPE+"！");
  		    return;
  		}
		
		if(num>1)
		{
  			alert(GZ_TEMPLATESET_INFO36+desc+GZ_TEMPLATESET_TYPE+"！");
  		    return;
  		}
		
		var names=eval("document.gztemplateSetForm.names");
		var name="";
		if(checkNum==1)
		{
			name=document.gztemplateSetForm.names.value;
			
		}
		else
		{
			name=names[selectIndex].value;
		}
		var oldname = name;
		var length = '${gztemplateSetForm.length}';
		var name=window.prompt(GZ_TEMPLATESET_INFO37+desc+GZ_TEMPLATESET_TYPENAME+"：",name);
		if(name&&IsOverStrLength(trim(name),length))
		{
			alert(GZ_TEMPLATESET_INFO34+"!");
			return;
		}
		if(name&&trim(name).length>0)
		{
			name=replaceAll(name,"'","’");
			name=replaceAll(name,"\"","”");
			var hashvo=new ParameterSet();
            hashvo.setValue("name",getEncodeStr(name));
            hashvo.setValue("gz_module","${gztemplateSetForm.gz_module}");
            hashvo.setValue("type","1");
            hashvo.setValue("salaryid","-1");
            hashvo.setValue("oldname",getEncodeStr(oldname));    //传递要另存的薪资类别名
            var request=new Request({asynchronous:false,onSuccess:check_ok,functionId:'3020030019'},hashvo);		
		}
	

}



function rename()
{
		var num=0;
		var selectIndex=0;
		var checkNum=0;
		var salaryid;
		for(var i=0;i<document.gztemplateSetForm.elements.length;i++)
  		{
  			if(document.gztemplateSetForm.elements[i].type=='checkbox'&&document.gztemplateSetForm.elements[i].name!='selbox')
  			{
  				if(document.gztemplateSetForm.elements[i].checked==true)
  				{
  					selectIndex=checkNum;
  					num++;
  				}
  				checkNum++;
  			}
  		}
  		if(num==0)
  		{
  			alert(GZ_TEMPLATESET_INFO38+desc+GZ_TEMPLATESET_TYPE+"！");
  		    return;
  		}
		
		if(num>1)
		{
  			alert(GZ_TEMPLATESET_INFO39+desc+GZ_TEMPLATESET_TYPE+"！");
  		    return;
  		}
		
		var names=eval("document.gztemplateSetForm.names");
		var salaryids=eval("document.gztemplateSetForm.salaryid");
		var name="";
		if(checkNum==1)
		{
			name=names.value;
			salaryid=salaryids.value;

		
		}
		else
		{
			name=names[selectIndex].value;
			salaryid=salaryids[selectIndex].value;
		}
		var length = '${gztemplateSetForm.length}';
		var name=window.prompt(GZ_TEMPLATESET_INPUT+desc+GZ_TEMPLATESET_TYPENAME+"：",name);
		if(name&&IsOverStrLength(trim(name),length))
		{
			alert(GZ_TEMPLATESET_INFO34+"!");
			return;
		}
		 if(name&&trim(name).length>0)
		{
		    name=replaceAll(name,"'","’");
			name=replaceAll(name,"\"","”");
		   	var hashvo=new ParameterSet();
            hashvo.setValue("name",getEncodeStr(name));
            hashvo.setValue("gz_module","${gztemplateSetForm.gz_module}");
            hashvo.setValue("type","0");
            hashvo.setValue("salaryid",salaryid);
            var request=new Request({asynchronous:false,onSuccess:check_ok,functionId:'3020030019'},hashvo);		    
		}
		

}


function init()
{
		var num=0;
		for(var i=0;i<document.gztemplateSetForm.elements.length;i++)
  		{
  			if(document.gztemplateSetForm.elements[i].type=='checkbox')
  			{
  				if(document.gztemplateSetForm.elements[i].checked==true)
  				{
  					num++;
  				}
  			}
  		}
  		if(num==0)
  		{
  			alert(GZ_TEMPLATESET_INFO40+desc+GZ_TEMPLATESET_TYPE+"！");
  		    return;
  		}

		document.gztemplateSetForm.action="/gz/templateset/gz_templatelist.do?br_init=int";
		document.gztemplateSetForm.submit();
}


function openProperty(salaryid)
{
	var infos=new Array();
	var thecodeurl="/gz/templateset/gz_templateProperty.do?b_query=select`gz_module=<bean:write name="gztemplateSetForm" property="gz_module" filter="true"/>`salaryid="+salaryid+"`nmodule=5";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    if(isIE6()){
        var return_value= window.showModalDialog(iframe_url, infos, 
		        "dialogWidth:570px; dialogHeight:630px;resizable:no;center:yes;scroll:no;status:no");
    }else{
        var return_value= window.showModalDialog(iframe_url, infos, 
		        "dialogWidth:550px; dialogHeight:630px;resizable:no;center:yes;scroll:no;status:no");
    }		
}

function exportZip()
{
	    //document.gztemplateSetForm.action="/gz/templateset/gz_templatelist.do?b_output=int";
		//document.gztemplateSetForm.submit();
		
		
		var ids="";
  		var num=0;
  		var salaryid_obj=eval("document.gztemplateSetForm.salaryid");
  		for(var i=0;i<document.gztemplateSetForm.elements.length;i++)
  		{
  			if(document.gztemplateSetForm.elements[i].type=='checkbox'&&document.gztemplateSetForm.elements[i].name!='selbox')
  			{
  				if(document.gztemplateSetForm.elements[i].checked==true)
  				{
  					
  					if(salaryid_obj[num])
  					{
  						/* 将#号分隔符改为与逗号（因为后台是使用逗号来分割的） xiaoyun 2014-9-20 start */
  						//ids=ids+"#"+salaryid_obj[num].value; 
  						ids=ids+","+salaryid_obj[num].value;
  						/* 将#号分隔符改为与逗号（因为后台是使用逗号来分割的） xiaoyun 2014-9-20 end */
  					}
  					else {
  						/* 将#号分隔符改为与逗号（因为后台是使用逗号来分割的） xiaoyun 2014-9-20 end */
  						//ids=ids+"#"+salaryid_obj.value;
  						ids=ids+","+salaryid_obj.value;
  						/* 将#号分隔符改为与逗号（因为后台是使用逗号来分割的） xiaoyun 2014-9-20 start */
  					} 					
  				} 
  				num++;				
  			}
  		}
  		
  		if(ids.length==0){
  			alert("请选择薪资类别！");
  			return;
  		}
  		var In_paramters="salaryid="+ids.substring(1); 
		var request=new Request({method:'post',asynchronous:true,parameters:In_paramters,onSuccess:returnInfo_export,functionId:'3020010120'});				
}

 function returnInfo_export(outparamters)
 {
			var outName=outparamters.getValue("outName");	
			var fieldName = getDecodeStr(outName);
			var win=open("/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true","zip");
 }


function importZip()
{
	document.gztemplateSetForm.action="/gz/templateset/gz_templatelist.do?br_importput=int";
		document.gztemplateSetForm.submit();
}

//结构同步
function structSynchro()
{
		var gz_moudle= "${gztemplateSetForm.gz_module}";
		var salaryIds="";
		var salaryid_obj=eval("document.gztemplateSetForm.salaryid");
		
		var index=0;
		for(var i=0;i<document.gztemplateSetForm.elements.length;i++)
  		{
  			if(document.gztemplateSetForm.elements[i].type=='checkbox'&&document.gztemplateSetForm.elements[i].name!='selbox')
  			{
  				if(document.gztemplateSetForm.elements[i].checked==true)
  				{
  					
  					if(salaryid_obj[index])
  					{
  						salaryIds+=","+salaryid_obj[index].value;
  					}
  					else
  					{
  						salaryIds+=","+salaryid_obj.value;
  					}
  				}
  				index++;
  			}
  		}
  		if(salaryIds.length==0)
  		{
  		  if(gz_moudle=='0')
  		     	alert("请选择需结构同步的薪资类别！");
  		  	else 
  		  	    alert("请选择需结构同步的保险类别！");
  		    return;
  		}
  		
  		var waitInfo=eval("wait");
		waitInfo.style.display="block";	

  		var hashvo=new ParameterSet();
        hashvo.setValue("salaryids",salaryIds);
        var request=new Request({asynchronous:true,onSuccess:synchro_ok,functionId:'3020030021'},hashvo);		
}

function synchro_ok(outparamters)
{
	var waitInfo=eval("wait");
	waitInfo.style.display="none";
	
	alert("同步完成!");

}

</script>
<html:form action="/gz/templateset/gz_templatelist">
	<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style"  height=24>
					正在同步数据，请稍候......
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
						<table cellspacing="1" cellpadding="0">
							<tr height=8>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
							</tr>
						</table>
					</marquee>
				</td>
			</tr>
		</table>
	</div>
 

<table align='center' width='100%' style="margin-left:-5px;"><tr><td>
	
<table width="100%" border="0" cellspacing="0"    align="center" cellpadding="0" class="ListTableF">
   	  <thead>
        <tr>
         <td align="center" class="TableRow" nowrap>
		    <input type="checkbox" name="selbox" onclick="batch_select(this,'setlistform.select');" title='<bean:message key="label.query.selectall"/>'>
         </td>         
         <td align="center" class="TableRow" nowrap >
		   <bean:message key="report.number"/>
	     </td>          
         <td align="center" class="TableRow" nowrap >
		  <logic:equal name="gztemplateSetForm" property="gz_module" value="0">
		   <bean:message key="label.gz.salarytype"/>
		  </logic:equal>
		   <logic:equal name="gztemplateSetForm" property="gz_module" value="1">
		    <bean:message key="sys.res.ins_set"/>
			</logic:equal>
	     </td>         
         <td align="center" class="TableRow" nowrap >
		    <bean:message key="label.gz.property"/>
	     </td>
         <td align="center" class="TableRow" nowrap >
			<bean:message key="label.gz.variable"/>
	     </td>
         <td align="center" class="TableRow" nowrap >
			<bean:message key="label.gz.formula"/>
         </td>
         <td align="center" class="TableRow" nowrap >
			<bean:message key="label.gz.shformula"/>
         </td>
            <td align="center" class="TableRow" nowrap >
             <logic:equal name="gztemplateSetForm" property="gz_module" value="0">
				<bean:message key="label.gz.gzitem"/>
			</logic:equal>
			 <logic:equal name="gztemplateSetForm" property="gz_module" value="1">
			 	<bean:message key="label.gz.insitem"/>
			 </logic:equal>
            </td> 
                           		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="gztemplateSetForm" property="setlistform.list" indexes="indexes"  pagination="setlistform.pagination" pageCount="15" scope="session">
          <bean:define id="nid" name="element" property="salaryid"/>
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow"  onclick='tr_onclick(this,"#F3F5FC");' >
          <%}
          else
          {%>
          <tr class="trDeep" onclick='tr_onclick(this,"#E4F2FC");'   >
          <%
          }
          i++;          
          %>  
            <td align="center" class="RecordRow" nowrap>
     		   <hrms:checkmultibox name="gztemplateSetForm" property="setlistform.select" value="true" indexes="indexes"/>
   	        	<html:hidden name="element" property="salaryid" />
   	        </td>          
            <td align="left" class="RecordRow" nowrap>
            &nbsp;<bean:write name="element" property="salaryid" filter="true"/>
	    </td>        
            <td align="left" class="RecordRow" nowrap>
                  &nbsp; <bean:write name="element" property="cname" filter="true"/>
                   <Input type='hidden' value='<bean:write name="element" property="cname" filter="true"/>'  name='names' />
	    </td>
         
            <td align="center" class="RecordRow">
            <hrms:priv func_id="3240808,3250508">	  
            <a href="javascript:openProperty('<bean:write name="element" property="salaryid" filter="true"/>')" >
			 <img src="/images/edit.gif" border=0>    
			</a>
			</hrms:priv>
            </td>
            <td align="center" class="RecordRow" nowrap>
            <hrms:priv func_id="3240809,3250509">
            <a href="/gz/tempvar/viewtempvar.do?b_query=link&state=${nid}&type=1&nflag=0"><img src="/images/edit.gif" border=0></a>    
	   		</hrms:priv>
	    	</td>   
            <td align="center" class="RecordRow" nowrap>
            <hrms:priv func_id="3240810,3250510">
			 <a href="/gz/formula/viewformula.do?b_query=link&salaryid=${nid}"><img src="/images/edit.gif" border=0></a>    
	    	</hrms:priv>
	    	</td> 
	    	<td align="center" class="RecordRow" nowrap>
	    	<hrms:priv func_id="3240812,3250512">
			 <a href="/gz/templateset/spformula/sp_formula.do?b_query=link&opt=0&returnType=0&salaryid=${nid}"><img src="/images/edit.gif" border=0></a>    
	    	</hrms:priv>
	    	</td>   
            <td align="center" class="RecordRow" nowrap>
             <hrms:priv func_id="3240811,3250511">
			<a href="/gz/templateset/salaryItem.do?b_query=query&salaryid=${nid}" >
			 <img src="/images/edit.gif" border=0>  
			</a> 
			</hrms:priv>
	    </td>  	 	   	    
	                
          </tr>
        </hrms:extenditerate>
        
</table>


<table  width='100%'  class='RecordRowP'  align='center'>
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="gztemplateSetForm" property="setlistform.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="gztemplateSetForm" property="setlistform.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="gztemplateSetForm" property="setlistform.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="gztemplateSetForm" property="setlistform.pagination"
				nameId="setlistform" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>

</td></tr></table>

<table  width="100%" align="center">
          <tr>
            <td align="center">
          <hrms:priv func_id="3240801,3250501">	  
         	<input type='button' class="mybutton" property="b_add"  onclick='add()' value='<bean:message key="button.insert"/>'  />
          </hrms:priv>
          <hrms:priv func_id="3240802,3250502">	
            <input type='button' class="mybutton" property="b_delete"  onclick='del()' value='<bean:message key="button.delete"/>'  />
         </hrms:priv>
         <hrms:priv func_id="3240803,3250503">	
         	<input type='button' class="mybutton" property="b_saveas"  onclick='saveAs()' value='<bean:message key="button.other_save"/>'  />
         </hrms:priv>
         <hrms:priv func_id="3240804,3250504">	
         	<input type='button' class="mybutton" property="b_rename"  onclick='rename()' value='<bean:message key="button.rename"/>'  />
         </hrms:priv>
         <hrms:priv func_id="3240813,3250513">	
         <input type='button' class="mybutton" property="b_sort"  onclick='sort()' value='<bean:message key="button.movenextpre"/>'  />
         </hrms:priv>
         <hrms:priv func_id="3240805,3250505">		
         	<input type='button' class="mybutton" property="b_rename"  onclick="exportZip()" value='<bean:message key="button.export"/>'  />
         </hrms:priv>
         <hrms:priv func_id="3240805,3250505">	
         	<input type='button' class="mybutton" property="b_rename"  onclick="importZip()" value='<bean:message key="button.import"/>'  />
         </hrms:priv>
         <hrms:priv func_id="3240806,3250506">	
         	<input type="button" class="mybutton" name="money" value="<bean:message key="button.money"/>" onclick="window.location.href='/gz/templateset/moneystyle/initMoneyStyle.do?b_init=init'">
	 	 </hrms:priv>
	 	 <hrms:priv func_id="3240807,3250507">	
	 		<input type='button' class="mybutton" property="b_rename"  onclick='init()' value='<bean:message key="button.gzdata.init"/>'  />
         </hrms:priv>	
         <hrms:priv func_id="3240814,3250514">		
            <input type='button' class="mybutton" property="b_synchro"  onclick='structSynchro()' value='<bean:message key="button.gzdata.synchronize"/>'  />
          </hrms:priv>	
         
         <logic:equal name="gztemplateSetForm" property="gz_module" value="0">
		<hrms:tipwizardbutton flag="compensation" target="il_body" formname="gztemplateSetForm"/>
		</logic:equal>
		<logic:equal name="gztemplateSetForm" property="gz_module" value="1">
		<hrms:tipwizardbutton flag="insurance" target="il_body" formname="gztemplateSetForm"/>
		</logic:equal>
         	 			 		
            </td>
          </tr>          
</table>

<input type='hidden' name='salarySetName'  value='' />

</html:form>
