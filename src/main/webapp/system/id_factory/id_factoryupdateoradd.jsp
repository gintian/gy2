<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<hrms:themes></hrms:themes>
<script language="javascript" src="/module/utils/js/template.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<script type="text/javascript" language="javascript"><!--



function selectFielditem(flag ,type){
	var seq_names=id_Factory_Form.sename.value;
	var obj=selectFieldItem(flag,type);
	if(getBrowseVersion()){
		var info=obj.split("/");
		if(info[0].length>0){
			id_Factory_Form.sename.value=info[0]+"."+info[2];
			id_Factory_Form.guideline.value=info[1]+"/"+info[3];
		}
	}
	
}
//非IE浏览器  弹窗回调方法  wangb 20190319
function selectFieldReturnValue(obj){
	if(obj){
		var info=obj.split("/");
		if(info[0].length>0){
			id_Factory_Form.sename.value=info[0]+"."+info[2];
			id_Factory_Form.guideline.value=info[1]+"/"+info[3];
		}
	}
}

function selectYMD(){
	var dw=300,dh=120,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	var sprefix = document.getElementById('sprefix').value;
	/*
	var reobject= window.showModalDialog("/system/id_factory/id_factory.jsp",sprefix, 
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
    if(reobject==null){
    	id_Factory_Form.sprefix.value=""; //前缀 下拉框选择 ‘请选择’  文本框内容为空   wangb 29755
    	return;
    }
    var selvalue =reobject.sel;//下拉列表值
    var selok=id_Factory_Form.sprefix.value;//文本框值
    selok=selok.replace('#yyyy#',selvalue);
    selok=selok.replace('#yyyy-mm#',selvalue);
    selok=selok.replace('#yyyy.mm#',selvalue);
    selok=selok.replace('#yyyy-mm-dd#',selvalue);
    selok=selok.replace('#yyyy.mm.dd#',selvalue);
    if(id_Factory_Form.sprefix.value==selok){
    id_Factory_Form.sprefix.value=selok+selvalue;
    }else{
    id_Factory_Form.sprefix.value=selok;
    }*/
    var url = '/system/id_factory/id_factory.jsp';
    var win = Ext.create('Ext.window.Window',{
			id:'id_factory',
			title:'请选择',
			width:dw+20,
			height:dh+20,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(!this.reobject){
    					id_Factory_Form.sprefix.value=""; //前缀 下拉框选择 ‘请选择’  文本框内容为空   wangb 29755
    					return;
    				}
    				var selvalue =this.reobject.sel;//下拉列表值
    				var selok=id_Factory_Form.sprefix.value;//文本框值
    				selok=selok.replace('#yyyy#',selvalue);
    				selok=selok.replace('#yyyy-mm#',selvalue);
    				selok=selok.replace('#yyyy.mm#',selvalue);
    				selok=selok.replace('#yyyy-mm-dd#',selvalue);
    				selok=selok.replace('#yyyy.mm.dd#',selvalue);
    				if(id_Factory_Form.sprefix.value==selok){
    					id_Factory_Form.sprefix.value=selok+selvalue;
    				}else{
    					id_Factory_Form.sprefix.value=selok;
    				}
				}
			}
	});  
	win.sprefix = sprefix;
}

function save()
{
   if(validate('R','idvo.string(sequence_name)','序号','RI','idvo.string(minvalue)','最小值','RI','idvo.string(maxvalue)','最大值','RI','idvo.string(increment_o)','步长','RI','idvo.string(id_length)','长度','RI','idvo.string(currentid)','当前值','I','idvo.string(prefix_field_len)','前缀符指标内容截取长度'))
   {
      if(ifqrbc())
      {
         id_Factory_Form.action="/system/id_factory/id_factoryupdateoradd.do?b_save=link";
         id_Factory_Form.submit();
      }
      
   }
}

function check_sequence_name(param){
	if("update"==param)
		return;
	var hashvo=new ParameterSet();
	var _sename=document.getElementById("sename").value;
	if(_sename=="")
		return;
    hashvo.setValue("sename",_sename);
    hashvo.setValue("param",param);
    var request=new Request({asynchronous:false,onSuccess:checkresult,functionId:'1010050020'},hashvo);         

}
function checkresult(outparamters){
	var msg=outparamters.getValue("msg");
	var param=outparamters.getValue("param");
	if(msg=="no"){
		var _sename=$("sename");
		//_sename.value="";
		_sename.focus();
		alert("该序列名称已存在,请重新输入！");
	}
	if(msg=="nohave"){
		var itemdesc=outparamters.getValue("itemdesc");
		itemdesc=getDecodeStr(itemdesc);
		var _sename=$("guideline");
		_sename.value=itemdesc;
		//var _sename=$("sename");
		//_sename.value="";
		//_sename.focus();
		//alert("该序列名称引用的子集名称或指标名称在系统内不存在,请重新输入!\n格式如：A01.A0408,其中A01是系统内存在的子集,A0408是系统内存在指标.");
	}
	if(msg=="ok"){
		var itemdesc=outparamters.getValue("itemdesc");
		itemdesc=getDecodeStr(itemdesc);
		var _sename=$("guideline");
		_sename.value=itemdesc;
	}
}

function doinithtml(){
	var _prefix_field=($("idvo.string(prefix_field)")).value;
	if(_prefix_field==""){
		var _prefix_field_len=$("idvo.string(prefix_field_len)");
		_prefix_field_len.value="";
		_prefix_field_len.disabled=true;
		var _prefix_field_lentd=$("prefix_field_lentd");
		_prefix_field_lentd.disabled=true;
		var _byprefix=$("idvo.string(byprefix)");
		_byprefix.value='0';
		var _view=$("viewcheckbox");
		_view.checked=false;
		var _byprefixtd=$("byprefixtd");
		_byprefixtd.disabled=true;
	}else{
		var _prefix_field_len=$("idvo.string(prefix_field_len)");
		_prefix_field_len.disabled=false;
		var _prefix_field_len=$("prefix_field_lentd");
		_prefix_field_len.disabled=false;
		var _byprefix=$("byprefixtd");
		_byprefix.disabled=false;
		<logic:equal value="1" name="id_Factory_Form" property="idvo.string(byprefix)">
				var _byprefix=$("viewcheckbox");
				_byprefix.checked=true;
		</logic:equal>
	}
};

function onclickcheckbox(){
	var _byprefix=$("viewcheckbox");
	var _va=$("idvo.string(byprefix)");
	if(_byprefix.checked){
		_va.value='1';
	}else{
		_va.value='0';
	}
}
//
--></script>

<html:form action="/system/id_factory/id_factoryupdateoradd">
	<table width="700" border="0" cellspacing="0" align="center" cellpadding="0">
		<tr height="20">
			<!-- <td width=10 valign="top" class="tableft"></td>
			<td width=110 align=center class="tabcenter">
				<logic:equal value="add" name="id_Factory_Form" property="updateflag">
					<bean:message key="id_factory.addnewseq" />
				</logic:equal>
				<logic:equal value="update" name="id_Factory_Form" property="updateflag">
					<bean:message key="id_factory.editseq" />
				</logic:equal>
			</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" class="tabremain" width="500"></td> -->
			<td align="left" class="TableRow_lrt">
				<logic:equal value="add" name="id_Factory_Form" property="updateflag">
					<bean:message key="id_factory.addnewseq" />
				</logic:equal>
				<logic:equal value="update" name="id_Factory_Form" property="updateflag">
					<bean:message key="id_factory.editseq" />
				</logic:equal>
			</td>
		</tr>
		<tr>
			<td>
				<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0" class="ListTable0">
					<tr>
						<logic:equal value="add" name="id_Factory_Form" property="updateflag">
							<td align="right" class="RecordRow" nowrap>
								<bean:message key="id_factory.guideline" />&nbsp;
							</td>
							<td  align="left" class="RecordRow" nowrap>
								<input type="text" name="guideline" size="17" class="text4" style="width:180px;"/>
								<img src="/images/code.gif" onclick="selectFielditem('ALL','NC')" align="absmiddle"/>
							</td>
							
						</logic:equal>
						<td align="right" class="RecordRow" nowrap>
							<bean:message key="id_factroy.sequence_name" />&nbsp;
						</td>
							<logic:equal value="add" name="id_Factory_Form" property="updateflag">
							<td align="left" class="RecordRow" nowrap>
								<html:text styleId="sename" name="id_Factory_Form" property="idvo.string(sequence_name)" styleClass="text4" style="width:180px;"></html:text>
							</logic:equal>
							<logic:equal value="update" name="id_Factory_Form" property="updateflag">
							<td align="left" class="RecordRow" nowrap colspan="3">
								<html:text styleId="sename" name="id_Factory_Form" property="idvo.string(sequence_name)" styleClass="text4" style="width:180px;"></html:text>
								<html:hidden name="id_Factory_Form" property="old_sequence_name" value="${id_Factory_Form.old_sequence_name }"/>
							</logic:equal>
						</td>
						
					</tr>
						<tr>
							<td align="right" class="RecordRow" nowrap>
								<bean:message key="id_factory.minvalue" />&nbsp;
							</td>
							<td align="left" class="RecordRow" nowrap>
								<html:text name="id_Factory_Form" property="idvo.string(minvalue)" styleClass="text4" style="width:180px;"></html:text>
							</td>

							<td align="right" class="RecordRow" nowrap>
								<bean:message key="id_factory.maxvalue" />&nbsp;
							</td>
							<td align="left" class="RecordRow" nowrap>
								<html:text name="id_Factory_Form" property="idvo.string(maxvalue)" styleClass="text4" style="width:180px;"></html:text>
							</td>
						</tr>
						<tr>
							<td align="right" class="RecordRow" nowrap>
								<bean:message key="id_factory.currentid" />&nbsp;
							</td>
							<td align="left" class="RecordRow" nowrap>
								<html:text name="id_Factory_Form" property="idvo.string(currentid)" styleClass="text4" style="width:180px;"></html:text>
							</td>
							<td align="right" class="RecordRow" nowrap>
								<bean:message key="id_factory.id_length" />&nbsp;
							</td>
							<td align="left" class="RecordRow" nowrap>
								<html:text name="id_Factory_Form" property="idvo.string(id_length)" styleClass="text4" style="width:180px;"></html:text>
							</td>							
						</tr>
					<tr>
							<td align="right" class="RecordRow" nowrap>
							<bean:message key="id_factory.prefix_field" />&nbsp;
							</td>
							<td align="left" class="RecordRow" nowrap>
									<hrms:optioncollection name="id_Factory_Form" property="dblist" collection="dblist"/>
									<html:select name="id_Factory_Form" onchange="doinithtml();" property="idvo.string(prefix_field)" size="1" style="width:180px;">
	                   					<html:options collection="dblist" property="dataValue" labelProperty="dataName"/>
	                				</html:select>
							</td>
							<td align="right" class="RecordRow" nowrap id="prefix_field_lentd">
								<bean:message key="id_factory.prefix_field_len" />&nbsp;
							</td>
							<td align="left" class="RecordRow" nowrap>
								<html:text name="id_Factory_Form" property="idvo.string(prefix_field_len)" styleClass="text4" style="width:180px;"></html:text>
							</td>
					</tr>
					<tr>
						<td align="right" class="RecordRow" nowrap>
							<bean:message key="id_factory.prefix" />&nbsp;
						</td>
						<td align="left" class="RecordRow" nowrap>
								<html:text styleId="sprefix" name="id_Factory_Form" property="idvo.string(prefix)"  size="17" styleClass="text4" style="width:180px;"></html:text>
								<img src="/images/code.gif" onclick="selectYMD();" align="absmiddle"/>
						</td>
						<td align="right" class="RecordRow" nowrap>
							<bean:message key="id_factory.suffix" />&nbsp;
						</td>
						<td align="left" class="RecordRow" nowrap>
								<html:text name="id_Factory_Form" property="idvo.string(suffix)" styleClass="text4" style="width:180px;"></html:text>
						</td>
						
					</tr>
					
					<tr>
						<td align="right" class="RecordRow" nowrap>
							<bean:message key="id_factory.increase_order" />&nbsp;
						</td>
						<td align="left" class="RecordRow" nowrap>
								<html:select name="id_Factory_Form" property="idvo.string(increase_order)" style="width:180px;"> 
									<html:option value="1">
										<bean:message key="id_factory.asc" />
									</html:option>
									<html:option value="0">
										<bean:message key="id_factory.desc" />
									</html:option>
								</html:select>
						</td>
						<td align="right" class="RecordRow" nowrap>
							<bean:message key="id_factory.increment_o" />&nbsp;
						</td>
						<td align="left" class="RecordRow" nowrap>
							<html:text name="id_Factory_Form" property="idvo.string(increment_o)" styleClass="text4" style="width:180px;"></html:text>
						</td>
					</tr>
					<tr>
							<td align="right" class="RecordRow" nowrap>
								<bean:message key="id_factory.loop_modal" />&nbsp;
							</td>
							<td  align="left" class="RecordRow" nowrap>
								<html:select name="id_Factory_Form" property="idvo.string(loop_mode)" style="width:180px;">
								<html:option value="0"><bean:message key="lable.zp_plan_detail.status0"/></html:option>
								<html:option value="1"><bean:message key="id_factory.loop_year"/></html:option>
								</html:select>
							</td>
							<td align="right" class="RecordRow" nowrap>
								<bean:message key="id_factory.c_rule" />&nbsp;
							</td>
							<td  align="left" class="RecordRow" nowrap>
								<html:select name="id_Factory_Form" property="idvo.string(c_rule)" style="width:180px;">
								<html:option value="0"><bean:message key="id_factory.c_rule.unequal"/></html:option>
								<html:option value="1"><bean:message key="id_factory.c_rule.equal"/></html:option>
								</html:select>
							</td>
					</tr>
					<tr>
						<td align="right" class="RecordRow" nowrap>
							<bean:message key="id_factory.sequence_desc" />&nbsp;
						</td>
						<td colspan="3" align="left" class="RecordRow" nowrap>
							<html:text name="id_Factory_Form" property="idvo.string(sequence_desc)" styleClass="text4" style="width:180px;"></html:text>
						</td>
						<!-- <td colspan="2" align="right" class="framestyle2" nowrap id='byprefixtd'>
							<html:hidden  name="id_Factory_Form" property="idvo.string(byprefix)"/>
							<input type="checkbox" onclick="onclickcheckbox();" id="viewcheckbox">
							&nbsp;<bean:message key="id_factory.byprefix" />&nbsp;
						</td>  -->
					</tr>
				</table>
			</td>
		</tr>

	</table>
	<table width="50%" align="center">
		<tr>
			<td align="center" style="height:35px;">					
					 <input type="button" name="addbutton"  value="<bean:message key="button.save" />" class="mybutton" onclick="save();">
					<hrms:submit styleClass="mybutton" property="br_return">
						<bean:message key="button.return" />
					</hrms:submit>
			</td>
		</tr>
	</table>

</html:form>

<script language="javascript">
  doinithtml();
</script>

