<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<script language="javascript">
        /**查询指标*/
	function searchFieldList()
	{
	   var tablename=$F('emp_e');
	   var in_paramters="tablename="+tablename+"&flag=leader";
   	   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'18010000014'});
	}
	/**显示指标*/
	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");
		AjaxBind.bind(orgInfoForm.link_field,fieldlist);
		var b0110list=outparamters.getValue("b0110list");
		AjaxBind.bind(orgInfoForm.b0110,b0110list);
		AjaxBind.bind(orgInfoForm.order_by,fieldlist);
		
	}
	function refresh(){
		posCodeParameterForm.submit();
	}
	function changeResh(obj){
		$('org_c_view').value="";
		$('org_c').value="";
		
		
		var request=new Request({method:'post',asynchronous:false,parameters:"setName="+obj.value,onSuccess:setSelectItem,functionId:'0401004014'});
	}
	
	function setSelectItem(outparam){
		var leaderTypeList = outparam.getValue("leaderTypeList");
		AjaxBind.bind(orgInfoForm.leaderType,leaderTypeList);
		var sessionitemList = outparam.getValue("sessionitemList");
		AjaxBind.bind(orgInfoForm.sessionitem,sessionitemList);
	}
	
	function getorgc(){
		var org_m=$F('org_m');
		if(org_m.length==0)
		{
			alert("请选择班子基本情况!");
			return;
		}
		var org_c=$F('org_c');
        var strUrl="/workbench/browse/history/parameters_deploy.do?b_norm=link&org_m="+org_m+"&org_c="+org_c;
        var dw = 660;
		var dh = 380;
		if(navigator.appVersion.indexOf('MSIE 6') != -1){
			dh = 410;
		}
		Ext.create('Ext.window.Window',{
			id:'org_leaderParameter',
			title:'选择子集',
			width:dw,
			height:dh,
			resizable:false,
			modal:true,
			autoScroll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff;" frameborder="0" scrolling=no height="100%" width="100%" src="'+strUrl+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.msg != null && this.msg.length > 0){
			        	var norm_v=this.msg.split("#");
			        	$('org_c_view').value=norm_v[1].substring(0,norm_v[1].length-1);
						$('org_c').value=norm_v[0].substring(0,norm_v[0].length-1);
						return false;
			        }
				}
			}
		});
	}
	
	function checkfield(){
		var i9999=$F('link_field');
		var orderby=$F('order_by');
		if(i9999.length>1&&i9999==orderby){
			alert("关联指标不能和成员顺序指标相同!");
			return false;
		}
	
	}
	
	function setField(field_falg)
	{
	    var target_url="/general/deci/leader/param.do?b_setfeild=link`field_falg="+field_falg;
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	    var dw = 580;
	    var dh = 400;
	    if(isIE6()){
	    	dw += 20;
	    	dh += 10;
	    }
		Ext.create('Ext.window.Window',{
			id:'org_setfield',
			title:'选择指标',
			width:dw,
			height:dh,
			resizable:false,
			modal:true,
			autoScroll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff;" frameborder="0" scrolling=no height="100%" width="100%" src="'+iframe_url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.msg != null){
						var in_obj = document.getElementById(field_falg);
						in_obj.value = this.msg.mess.replace(/<br>/g,"");
				    }else {
						var in_obj = document.getElementById(field_falg);  
				    }
					return false;
				}
			}
			
		});
	}
	
	function setSname()
	{
	    var target_url="/general/deci/leader/param.do?b_sname=link";
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
	    var dw = 580;
	    var dh = 400;
	    if(isIE6()){
	    	dw += 20;
	    	dh += 10;
	    }
		Ext.create('Ext.window.Window',{
			id:'org_setSname',
			title:'选择条件',
			width:dw,
			height:dh,
			resizable:false,
			modal:true,
			autoScroll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff;" frameborder="0" scrolling=no height="100%" width="100%" src="'+iframe_url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.msg != null){
						var in_obj = document.getElementById('gcond');
						in_obj.value = this.msg.mess.replace(/<br>/g,"");
				    }else{
						var in_obj = document.getElementById("gcond");  
				    }
				    return false;
				}
			}
			
		});
	    
	}
	function setDb(field_falg)
{
    var target_url="/general/deci/leader/param.do?b_setdb=link`field_falg="+field_falg;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);

    var dw = 380;
	var dh = 300;

	Ext.create('Ext.window.Window',{
		id:'org_setDb',
		title:'人员库设置',
		width:dw,
		height:dh,
		resizable:false,
		modal:true,
		autoScroll:false,
		autoShow:true,
		autoDestroy:true,
		html:'<iframe style="background-color:#fff;" frameborder="0" scrolling=no height="100%" width="100%" toolbar=no location=no resizable=no src="'+iframe_url+'"></iframe>',
		renderTo:Ext.getBody(),
		listeners:{
			'close':function(){
				if(this.msg != null){
					var in_obj = document.getElementById(field_falg);  
				    in_obj.innerHTML = this.msg.mess;
			    }else {
					var in_obj = document.getElementById(field_falg);  
			    } 
				return false;
			}
		}
		
	});
    
}
</script>
<html:form action="/workbench/orginfo/searchorginfo">
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		领导班子参数配置
            </td>            	        	        	        
           </tr>
   	  </thead>
    <tr>
	    <td align="left">
	     <fieldset align="center"  style="margin-left: 5px;margin-right: 5px;margin-top: 5px;">
	         <legend>班子信息集</legend>
	          <table align="center" border=0 width="100%"> 
		         <tr>
		            <td align="right" nowrap width="15%">班子主集</td>
	     	        <td width="76%">
		     	       <html:select name="orgInfoForm" onchange="changeResh(this);" property="org_m" size="1" style="width:200px">
		                       <html:optionsCollection property="org_mlist" value="dataValue" label="dataName"/>
		                </html:select>
		                (除单位基本情况子集之外的单位子集)
	     	       	</td>
	     	       	<td >
	                </td>               	        	        
		        </tr> 
		        <tr>
		            <td align="right" nowrap>领导班子类别指标</td>
		            <td>
		                <html:select name="orgInfoForm" property="leaderType"  style="width:200px">
		                    <html:optionsCollection property="leaderTypeList" value="dataValue" label="dataName"/>
		                </html:select>
		                (班子主集中关联代码类72的指标)
		            </td>
		            <td >
	                </td>
		        </tr>
		        <tr>
		            <td align="right" nowrap>届次指标</td>
		            <td>
		                <html:select name="orgInfoForm" property="sessionitem"  style="width:200px">
		                   <html:optionsCollection property="sessionItemList" value="dataValue" label="dataName"/>
		                </html:select>
		                (班子主集中数值型或字符型指标)
		            </td>
	                <td >
	                </td>
		        </tr>
		        <tr >
		            <td valign="top" align="right" nowrap > 班子子集</td>
		     	             
	  	            <td style="padding: 0px,0px,10px,0px;">
	  	                   <html:textarea property="org_c_view" name="orgInfoForm" style="width:99%" rows="5" readonly="true"></html:textarea>
	  	                   <!-- 解决非IE浏览器默认提交的问题，必须加type属性 -->
		            </td>
	                <td valign="bottom">
	     	  		  	   <span style="vertical-align: bottom;">&nbsp;<button type="button" class=mybutton onclick="return getorgc();">...</button></span>
	     	               <html:hidden property="org_c" name="orgInfoForm" />
		     	    </td>
		        </tr>
	        </table>
	       </fieldset>  
	      </td>
   		</tr> 
   		<tr>
            <td>
               <fieldset align="center"  style="margin-left: 5px;margin-right: 5px;margin-top: 5px;">
                   <legend>班子成员集</legend>
                   <table align="center" border=0 width="100%">
                      <tr>
                         <td align="right" width="15%" nowrap > 班子成员信息</td>
                         <td>
				     	       <html:select name="orgInfoForm" onchange="searchFieldList();" property="emp_e" size="1" style="width:200px">
				     	       	<html:optionsCollection property="emp_elist" value="dataValue" label="dataName"/>
				                </html:select>
				                (除人员基本情况子集之外的人员子集)
                         </td>
                      </tr>
                   </table>
                   <table align="center" border=0 width="100%" >   
                      <tr>
                          <td align="right" width="15%" nowrap > 人员库</td>
                           <td width="200px">
				           		<span class="RecordRow" id="bz" width="194px" style="display:block;line-height:25px;height:auto;">
				           		    <bean:write  name="orgInfoForm" property="bz_mess" filter="false"/>
				           		</span>
				           </td>
                          <td>
                              <span style="vertical-align: top;">&nbsp;<button type="button" class=mybutton onclick="return setDb('bz')">设置人员库</button></span>
                          </td>
                      </tr>
                   </table>
               </fieldset>
            </td>
        </tr> 
        <tr>
            <td >
               <fieldset align="center"  style="margin-left: 5px;margin-right: 5px;margin-top: 5px;">
                   <legend>其他设置（系统项，必须指定）</legend>
                     <table align="center" width="100%">
                        <tr>
                            <td align="right" width="15%" nowrap="nowrap">关联指标</td>
                            <td width="76%" nowrap="nowrap">
                                <html:select name="orgInfoForm" onchange="" property="link_field" size="1" style="width:120px">
				                   <html:optionsCollection property="link_fieldlist" value="dataValue" label="dataName"/>
				                </html:select>
				                (班子成员信息子集，整型指标，选班子成员时自动生成值，等于班子记录号)
                            </td>
                            <td >
                             </td>
                         </tr>
                         <tr>
                            <td align="right" nowrap="nowrap" >顺序指标</td>
                            <td>
                                <html:select name="orgInfoForm" onchange="" property="order_by" size="1" style="width:120px">
				                   <html:optionsCollection property="link_fieldlist" value="dataValue" label="dataName"/>
				                </html:select>
				                 (班子成员信息子集，整型指标，调整成员顺序时系统自动生成值)
                            </td>
                            <td >
                             </td>
                        </tr>
                        <tr>
                            <td align="right" nowrap="nowrap">单位 | 部门指标</td>
                            <td nowrap="nowrap">
                                <html:select name="orgInfoForm" onchange="" property="b0110" size="1" style="width:120px">
				                   <html:optionsCollection property="b0110list" value="dataValue" label="dataName"/>
				                </html:select>
				                (班子成员信息子集，关联代码类UN或UM的指标，班子成员所在的单位或部门) 
                            </td>
                             <td >
                             </td>
                        </tr>
                        <tr>
                           <td valign="top" align="right" nowrap="nowrap">班子成员浏览指标</td>
                           <td>
                              <html:textarea name="orgInfoForm" property="display_mess" styleId="display" style="width:99%" readonly="true" rows="5"></html:textarea>
                             </td>
                             <td valign="bottom">
                              <span style="vertical-align: bottom;">&nbsp;<button type="button" class=mybutton onclick="return setField('display');">...</button></span>
                           </td>
                        </tr>
                        <tr>
                           <td valign="top" align="right" nowrap="nowrap">分析条件</td>
                           <td>
                              <html:textarea name="orgInfoForm" property="gcond_mess" styleId="gcond" style="width:99%" readonly="true" rows="5"></html:textarea>
                             </td>
                             <td valign="bottom">
                               <span style="vertical-align: bottom;">&nbsp;<button type="button" class=mybutton onclick="return setSname();">...</button></span>
                           </td>
                        </tr>
                     </table>
	               </fieldset>
	            </td>
	        </tr> 
   <tr height="1"><td>&nbsp;</td></tr>
</table>
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" style="padding-top:2px;">
<tr>
        <td align="center"  nowrap>
           &nbsp;&nbsp;<html:submit property="b_saveparameter" styleClass="mybutton" onclick="return checkfield();" >&nbsp;<bean:message key='button.save' />&nbsp;</html:submit>
        </td>
   </tr> 
</table>

</html:form>
