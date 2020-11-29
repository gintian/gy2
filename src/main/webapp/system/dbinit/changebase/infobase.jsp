<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript" src="/ajax/basic.js"></script>
<script language="JavaScript" src="../../../../module/utils/js/template.js"></script>
<script language="javascript"><!--

function updatedb(dbid,dbname)
{
	var strurl="/system/dbinit/changebase.do?b_addedit=link`vflag=1`dbid="+dbid;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	var dw=300,dh=150,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2; 
    // var return_vo= window.showModalDialog(iframe_url, null,
    //     "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
    // if(return_vo!=null)
    // {
    // 	var dbvo = new Object();
    // 	dbvo.dbname = return_vo.dbname;
    // 	dbvo.pre = return_vo.pre;
    // 	dbvo.vflag = "1";
    // 	var hashvo = new ParameterSet();
    // 	hashvo.setValue("dbvo",dbvo);
    // 	var e2 = document.getElementsByName("dbnamevalue");
    // 	var b = false;
    // 	for(var i=0;i<e2.length;i++)
    // 	{
    // 		if(e2[i].value!=dbname&&e2[i].value==dbvo.dbname){
    // 			b=true;
    // 			break;
    // 		}
    // 	}
    // 	if(b){
    // 		alert("有相同人员库名存在！请重新命名");
    // 	}else{
    // 		var request=new Request({asynchronous:false,onSuccess:updateDb_ok,functionId:'1020010203'},hashvo);
    // 	}
    // }
    return_vo ='';
    var theUrl = iframe_url;
    Ext.create('Ext.window.Window', {
        id:'newDb',
        height: 190,
        width: 330,
        resizable:false,
        modal:true,
        autoScroll:false,
        autoShow:true,
        html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
        renderTo:Ext.getBody(),
        listeners: {
            'close': function () {
                if (return_vo) {
                    var dbvo = new Object();
                    dbvo.dbname = return_vo.dbname;
                    dbvo.pre = return_vo.pre;
                    dbvo.vflag = "1";
                    var hashvo = new ParameterSet();
                    hashvo.setValue("dbvo", dbvo);
                    var e2 = document.getElementsByName("dbnamevalue");
                    var b = false;
                    for (var i = 0; i < e2.length; i++) {
                        if (e2[i].value != dbname && e2[i].value == dbvo.dbname) {
                            b = true;
                            break;
                        }
                    }
                    if (b) {
                        alert("有相同人员库名存在！请重新命名");
                    } else {
                        var request = new Request({
                            asynchronous: false,
                            onSuccess: updateDb_ok,
                            functionId: '1020010203'
                        }, hashvo);
                    }
                }
            }
        }

    });
}
function updateDb_ok(outparamters)
{
	window.location.href="/system/dbinit/inforlist.do?b_chgbase=link";
}

function newDb()
{
	var strurl="/system/dbinit/changebase.do?b_addedit=link`vflag=0";
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
    // var dw=300,dh=150,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    // var return_vo= window.showModalDialog(iframe_url, null,
    //      "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
    // if(return_vo!=null)
    // {
    // 	var dbvo = new Object();
    // 	dbvo.dbname = trimStr(return_vo.dbname);
    // 	dbvo.pre = trimStr(return_vo.pre);
    // 	dbvo.vflag = "0";
    // 	var hashvo = new ParameterSet();
    // 	hashvo.setValue("dbvo",dbvo);
    // 	var e = document.getElementsByName("prevalue");
    // 	var e2 = document.getElementsByName("dbnamevalue");
    // 	var b = false;
    // 	for(var i=0;i<e.length;i++)
    // 	{
    // 		if(e[i].value.toLowerCase()==dbvo.pre.toLowerCase()){
    // 			b=true;
    // 			break;
    // 		}
    // 		if(e2[i].value==dbvo.dbname){
    // 			b=true;
    // 			break;
    // 		}
    // 	}
    // 	if(b){
    // 		alert("有相同人员库名称或前缀存在！请重新增加");
    // 	}else{
    // 		var request=new Request({asynchronous:false,onSuccess:updateDb_ok,functionId:'1020010203'},hashvo);
    // 	}
    // }
    return_vo ='';
    var theUrl = iframe_url;
    Ext.create('Ext.window.Window', {
        id:'newDb',
        height: 190,
        width: 330,
        resizable:false,
        modal:true,
        autoScroll:false,
        autoShow:true,
        html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
        renderTo:Ext.getBody(),
        listeners: {
            'close': function () {
                if (return_vo) {
                    var dbvo = new Object();
                    dbvo.dbname = trimStr(return_vo.dbname);
                    dbvo.pre = trimStr(return_vo.pre);
                    dbvo.vflag = "0";
                    var hashvo = new ParameterSet();
                    hashvo.setValue("dbvo",dbvo);
                    var e = document.getElementsByName("prevalue");
                    var e2 = document.getElementsByName("dbnamevalue");
                    var b = false;
                    for(var i=0;i<e.length;i++)
                    {
                        if(e[i].value.toLowerCase()==dbvo.pre.toLowerCase()){
                            b=true;
                            break;
                        }
                        if(e2[i].value==dbvo.dbname){
                            b=true;
                            break;
                        }
                    }
                    if(b){
                        alert("有相同人员库名称或前缀存在！请重新增加");
                    }else{
                        var request=new Request({asynchronous:false,onSuccess:updateDb_ok,functionId:'1020010203'},hashvo);
                    }
                }
            }
        }

    });
}
function deletedb()
{
	var len=document.dbinitForm.elements.length;
	var selectDb='';
	//var choise = new Array();
	//var j=0;
	for (var i=0;i<len;i++)
	{
	   if (document.dbinitForm.elements[i].type=="checkbox"&&document.dbinitForm.elements[i].name!='selbox')
	   {
	      if(document.dbinitForm.elements[i].checked==true)
	      {
	    	    var pre = document.dbinitForm.elements[i].parentNode.getAttribute("dbpre");
	    	    selectDb+=pre+",";
	        //choise[j] = trim(document.dbinitForm.elements[i].parentElement.parentElement.cells[2].innerHTML);
	        //j++;
	        //break;
	       }
	   }
	}
	if(selectDb=='')
	{
	  alert(NOTING_SELECT);
	  return false;
	}
	
	//校验选中库是否可以删除 guodd 2016-06-23
	var hashvo = new ParameterSet();
	hashvo.setValue("selectDb",selectDb);
	var request=new Request({asynchronous:false,onSuccess:function(resp){
		var msg = resp.getValue("msg");
		if(msg!='true'){
			alert(msg);
			return;
		}
		
		if(confirm("删除所选人员库将使人员库的数据丢失，要继续吗？")){
			dbinitForm.action = "/system/dbinit/inforlist.do?b_deletedb=link";
			dbinitForm.submit();
		}
		
	},functionId:'11111111111'},hashvo);
	/* var b = true;
	var name; */
	//for(var i=0;i<choise.length;i++)
	//{
	//	if(choise[i].toUpperCase()=='USR'||choise[i].toUpperCase()=='RET'||choise[i].toUpperCase()=='TRS'||choise[i].toUpperCase()=='OTH'){
	//		name = choise[i];
	//		b = false;
	//		break;
	//	}
	//}
	/* if(b){
		if(confirm("删除所选人员库将使人员库的数据丢失，要继续吗？")){
			dbinitForm.action = "/system/dbinit/inforlist.do?b_deletedb=link";
			dbinitForm.submit();
		}
	}else{
		alert(SYSTEM_DBINIT_CHANGEBASE_INFO01+name+SYSTEM_DBINIT_CHANGEBASE_INFO02);
	} */
}
function adjust_order()
{
	target_url="/system/dbinit/changebase.do?b_order=link";
	var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
	var dw=596,dh=360,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2; 
    // var return_vo= window.showModalDialog(iframe_url,null,
    // "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
    // if(return_vo!=null){
    // 	window.location.href='/system/dbinit/inforlist.do?b_chgbase=link';
    // }
    return_vo ='';
    var theUrl = iframe_url;
    parent.parent.Ext.create('Ext.window.Window', {
        id:'adjust_order',
        height: 420,
        width: 620,
        resizable:false,
        //modal:true,
        autoScroll:false,
        autoShow:true,
        html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
        renderTo:parent.parent.Ext.getBody(),
        listeners: {
            'close': function () {
                if (parent.parent.return_vo) {
                    window.location.href='/system/dbinit/inforlist.do?b_chgbase=link';
                }
            }
        }

    });

}
function winClose() {
    if(parent.parent.Ext.getCmp('changebase')){
        parent.parent.Ext.getCmp('changebase').close();
    }
}
//
--></script>
<style>
<!--
.fixedDiv2 
{ 
	overflow:auto; 
	height:expression(document.body.clientHeight-100);
	width:expression(document.body.clientWidth); 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
-->
</style>
<hrms:themes></hrms:themes>
<%int i=0;%>
<html:form action="/system/dbinit/inforlist">
<div class="fixedDiv2" style="border-top:none;height:180px;">
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
		<THEAD>
			<TR class="fixedHeaderTr">
				<TD align="center" class="TableRow" width="30" nowrap style="border-left:none;">
					<input type="checkbox" name="selbox" onclick="batch_select(this, 'dbListForm.select');">&nbsp;
				</TD>
				<TD align="center" class="TableRow" nowrap>
					<bean:message key="column.name"/>&nbsp;
				</TD>
				<TD align="center" class="TableRow" nowrap>
					<bean:message key="id_factory.prefix"/>&nbsp;
				</TD>
				<hrms:priv func_id="30071052">
					<TD align="center" class="TableRow" width="40" nowrap style="border-right:none;">
						<bean:message key="column.operation"/>&nbsp;
					</TD>
				</hrms:priv>
			</TR>
		</THEAD>
		<hrms:extenditerate id="element" name="dbinitForm" property="dbListForm.list" indexes="indexes"  pagination="dbListForm.pagination" pageCount="100" scope="session">
        	<%
				if(i%2==0)
				{
			%>
				<tr class="trShallow">
			<%	}
				else
				{
			%>
				<tr class="trDeep">
			<%
				}
				i++;          
			%>
			<logic:equal name="element" property="string(pre)" value="Usr">
				<td align="center" class="RecordRow" nowrap style="border-left:none;">
				</td>
			</logic:equal>
			
			<logic:notEqual name="element" property="string(pre)" value="Usr">
          	<td align="center" class="RecordRow" nowrap style="border-left:none;" dbpre ='<bean:write  name="element" property="string(pre)" filter="true"/>' >
          		<hrms:checkmultibox name="dbinitForm" property="dbListForm.select"  value="true" indexes="indexes"/>&nbsp;
	    	</td>
	    	</logic:notEqual>
	    	
	    	<td align="left" class="RecordRow" nowrap>
	    		<INPUT type="hidden" name="dbnamevalue" value="<bean:write  name='element' property='string(dbname)' filter='true'/>">
	    		&nbsp;<bean:write  name="element" property="string(dbname)" filter="true"/>&nbsp;
	    	</td>
	    	<td id="pre" align="left" class="RecordRow" nowrap>
	    	<INPUT type="hidden" name="prevalue" value="<bean:write  name='element' property='string(pre)' filter='true'/>">
				&nbsp;<bean:write  name="element" property="string(pre)" filter="true"/>
	    	</td>
	    	<hrms:priv func_id="30071052">
	    	<td align="center" class="RecordRow" nowrap style="border-right:none;">
	    		<a href="javascript:updatedb('<bean:write name="element" property="string(dbid)" />','<bean:write  name='element' property='string(dbname)' filter='true'/>')"><img src="/images/edit.gif" border=0></a>&nbsp;
	    	</td>
	    	</hrms:priv>
        	</tr>
        </hrms:extenditerate>
	</table>
</div>
<div class="fixedDiv3">
	<table width="100%" align="center">
		<tr>
			<td align="center" nowrap height="35px;">
				<hrms:priv func_id="30071051">
					<input type="button" name="b_add" class="mybutton" value="<bean:message key="lable.tz_template.new"/>" onclick="newDb()" />
				</hrms:priv>
				<hrms:priv func_id="30071053">
					<input type="button" name="b_delete" class="mybutton" value="<bean:message key="button.delete"/>" onclick="deletedb()" />
				</hrms:priv>
				<hrms:priv func_id="30071054">
					<input type="button" name="b_movenextpre" class="mybutton" value="<bean:message key="button.movenextpre"/>" onclick="adjust_order()" />
				</hrms:priv>
				<%--<input type="button" name="br_approve" value='<bean:message key="button.close"/>' class="mybutton" onclick="window.close();">--%>
				<input type="button" name="br_approve" value='<bean:message key="button.close"/>' class="mybutton" onclick="winClose();">
			</td>
		</tr>
	</table>
</div>

</html:form>