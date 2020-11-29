<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<link rel="stylesheet"
	href="/gz/gz_accounting/piecerate/piecerate.css"
	type="text/css">
	<hrms:themes />
<script type="text/javascript">
<!--
var value="";
var selectItemid="";
function nextStep()
{
	var tab_id ; 
	var strIds = "";
	var dd = false;
	var index = 0;
	var obj = document.getElementsByName("ids");
	var objValue = document.getElementsByName("ordertype");
	for (var i = 0; i < objValue.length; i++) {
			if (objValue[index].value!="") {
				dd = true;
				strIds = strIds + obj[index].value +":"+objValue[index].value+ ",";
			}

			index++;

	}
	if (!dd) {
		strIds="";
	} else {
		strIds = strIds.substring(0, strIds.length - 1) ;
	}
	pieceRateTjDefineForm.orderFlds.value=strIds;
	pieceRateTjDefineForm.action="/gz/gz_accounting/piecerate/piecerate_tj_def.do?b_setComplete=link";
	pieceRateTjDefineForm.submit();
}

function defCheck(){
    var cvalue="";
    var tablevos=document.getElementsByName("ids");
	for(var i=0;i<tablevos.length;i++)
	{
    	var cvalue = tablevos[i];
    	break;
   
   }
    changebox(cvalue);
}

function changebox(checkvalue){
	checkvalue=checkvalue+"";
	if(checkvalue!=null&&checkvalue.length>0){
		tr_bgcolor(checkvalue);
	}
}

function tr_bgcolor(itemid){
	var tablevos=document.getElementsByName("ids");
	var bfinded=false;
	for(var i=0;i<tablevos.length;i++)
	{
    	var cvalue = tablevos[i];
    	var tr = cvalue.parentNode.parentNode;
    	if (cvalue.value==itemid) {
    	    tr.style.backgroundColor = '#FFF8D2' ;
    	    bfinded=true;
    	    selectItemid= cvalue.value;
    	}
    	else
    	{
    	  tr.style.backgroundColor = '';
    	}

    }
    if (!bfinded){
		for(var i=0;i<tablevos.length;i++)
		{
	    	var cvalue = tablevos[i];
	    	var tr = cvalue.parentNode.parentNode;	
    	    tr.style.backgroundColor = '#FFF8D2' ;
    	    selectItemid= cvalue.value;
    	    break;	
	
	    }
    }
}

function move(model) {
    var itemid=selectItemid;
	var tablevos=document.getElementsByName("ids");
	var bfinded=false;
	for(var i=0;i<tablevos.length;i++)
	{
    	var cvalue = tablevos[i];
    	var tr = cvalue.parentNode.parentNode;
    	if (cvalue.value==itemid) {
    	  if (model=="up"){
    	  	if (i<1){break;}
    	  	change(tr,i-1);
    	  }
    	  else {
    	  	if (i>tablevos.length-1){break;}
    	  	change(tr,i+1);
    	  }

    	}

    }
    

}

function swapNode(node1,node2){
	//获取父结点
	var _parent=node1.parentNode;
	//获取两个结点的相对位置
	var _t1=node1.nextSibling;
	var _t2=node2.nextSibling;
	//将node2插入到原来node1的位置
	if(_t1) _parent.insertBefore(node2,_t1);
	else _parent.appendChild(node2);
	//将node1插入到原来node2的位置
	if(_t2) _parent.insertBefore(node1,_t2);
	else _parent.appendChild(node1);
}


function change(soureTr,destIndex) {
    var itemid=selectItemid;
	var tablevos=document.getElementsByName("ids");
	var bfinded=false;
	if (destIndex>tablevos.length-1) {
		return;
	}
	var desttablle =tablevos[destIndex];
	var destTr = desttablle.parentNode.parentNode;
	
	if ((destTr !=null) && (soureTr !=null)){
		swapNode(soureTr,destTr);
	}

}


//-->
</script>
<base id="mybase" target="_self">
<html:form action="/gz/gz_accounting/piecerate/piecerate_tj_def">
 <html:hidden name="pieceRateTjDefineForm" property="orderFlds" />

	<fieldset style="width: 100%; height: 95%" align="center" >
		<legend>
			定义排序指标
		</legend>
		<div id="order_div" >
		  <table width="100%" align="center">
			 <tr >
				 <td width="80%">
					<table width="100%" border="0" class="ListTable1">
						<tr>
							<td width="70%" class="TableRow" align="center">
								指标名称
							</td>
							<td width="30%" class="TableRow" align="center">
								升降
							</td>
						</tr>
		
						<hrms:extenditerate id="element" name="pieceRateTjDefineForm"
							property="pageOrderFld.list" indexes="indexes"
							pagination="pageOrderFld.pagination" pageCount="2000"
							scope="session">
							<bean:define id="itemid" name="element" property="itemid" />
		
							<tr class="trShallow" onclick=" changebox('${itemid}')">
								<td align="left" class="RecordRow" nowrap>
									&nbsp;
									<bean:write name="element" property="itemname" filter="true" />
									&nbsp;
								</td>
								<td align="center" class="RecordRow" nowrap>
		
									<input type="hidden" name="ids"
										value="<bean:write  name="element" property="itemid" filter="true"/>">
									<html:select name="element" property="ordertype" onchange="" style="width:80">
										<html:option value="">
											
										</html:option>
										<html:option value="asc">
											<bean:message key="label.query.sortBase" />
										</html:option>
										<html:option value="desc">
											<bean:message key="label.query.sortDesc" />
										</html:option>
									</html:select>
		
								</td>
		
							</tr>
						</hrms:extenditerate>
					</table>
				</td>
				<td width="20%" align="center">
						<html:button styleClass="mybutton" property="b_up"
							onclick="move('up');">
							<bean:message key="button.previous" />
						</html:button>			
						<br>
						<br>
						<html:button styleClass="mybutton" property="b_down"
							onclick="move('down');">
							<bean:message key="button.next" />
						</html:button>
	
				</td>
			</tr>
		</table>	
		</div>
        <div id="divbottom" style="width: 100%">
		    <table width="100%"  border="0" align="center">
				<tr height="20px">
					<td colspan="4" align="center" style="padding-top:2px;padding-bottom:2px;">
						<hrms:submit styleClass="mybutton" property="br_resetGroup">
						  <bean:message key="button.query.pre"/>
						</hrms:submit>  					         
						<input type="button" name="query" class="mybutton" value="<bean:message key="gz.bankdisk.nextstep"/>" onclick="nextStep();">
		
					</td>
				</tr>
    		</table>
 	     </div>
	</fieldset>

</html:form>

<script language="javascript">
  defCheck();
</script>