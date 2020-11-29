<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<hrms:themes />
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<style>
.fixedDiv_self
{ 
	overflow:auto; 
	height:380 ; 
	width:585; 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}

</style>
<script>
   function add()
   {   
	    var bodyType = document.getElementById("bodyType").value;	
	    var target_url="/performance/options/checkbodystartadd.jsp?bodyType="+bodyType;

       if(window.showModalDialog){
           var return_vo= window.showModalDialog(target_url, "",
               "dialogWidth:400px; dialogHeight:300px;resizable:no;center:yes;scroll:no;status:no");
           edit_window_ok(return_vo);
       }else{
    	   var top= window.screen.availHeight-320>0?window.screen.availHeight-320:0;
   	       var left= window.screen.availWidth-510>0?window.screen.availWidth-510:0;
   	       top = top/2;
   	       left = left/2;
           window.open(target_url, "glWin", 
        		   "top="+top+",left="+left+",width=450; height=300;resizable=no;center=yes;scroll=no;status=no");
       }

   }
   
   function reflesh()
   {
   		var bodyType = document.getElementById("bodyType").value;
		document.checkBodyObjectForm.action="/performance/options/checkBodyObjectList.do?b_query2=link&haveOrNot=having&bodyType="+bodyType+"&busitype=${checkBodyObjectForm.busitype}";
	    document.checkBodyObjectForm.submit();
   }
   function edit(bodyId)
   {   
   	   var target_url="/performance/options/checkBodyObjectAdd.do?b_edit=link`bodyId="+bodyId+"`info=edit";
	   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	   var iTop = (window.screen.height-500)/2; //获得窗口的垂直位置;
	    var iLeft = (window.screen.width-460)/2;  //获得窗口的水平位置;
       if(window.showModalDialog){
           var return_vo=window.showModalDialog(iframe_url,'glWin','dialogWidth:400px; dialogHeight:300px;resizable:no;center:yes;scroll:no;status:no');
           edit_window_ok(return_vo);
       }else{
           window.open(iframe_url, "glWin", "width=450, height=300,top="+iTop+",left="+iLeft+",resizable=no,center=yes,scroll=no,status=no");
       }
   }
   function  edit_window_ok(return_vo) {
       if(return_vo==null)
           return ;
       if(return_vo.flag=="true")
           reflesh();
   }

   function checkdelete()
   {
		var str="";
		var bodyType = document.getElementById("bodyType").value;
		for(var i=0;i<document.checkBodyObjectForm.elements.length;i++)
		{
			if(document.checkBodyObjectForm.elements[i].type=="checkbox")
			{
				if(document.checkBodyObjectForm.elements[i].checked==true && document.checkBodyObjectForm.elements[i].name!="selbox")
				{
					str+=document.checkBodyObjectForm.elements[i+1].value+"/";
				}
			}
		}
		if(str.length==0)
		{
			alert("<bean:message key='jx.paramset.selDel'/>");
			return;
		}else
		{			
			var message = "";
			if(bodyType==1)
				message = "确认删除对象类别？";
			else
				message = "确认删除主体类别？";
				
			if(confirm(message))
    		{	
				//checkBodyObjectForm.action="/performance/options/checkBodyObjectList.do?b_delete2=link&deletestr="+str+"&bodyType="+bodyType; 
				 //checkBodyObjectForm.submit();
				 	
				var hashvo=new ParameterSet();			
				hashvo.setValue("deletestr",str);
				hashvo.setValue("bodyType",bodyType);
				var request=new Request({method:'post',asynchronous:false,onSuccess:delRefresh,functionId:'9026001005'},hashvo);				 	
			}
		}
	 }
	function delRefresh(outparamters)
	{
		reflesh() ;
	}
	  
	function moveSeq(num)
	{
		var str="";
		var seq="";
		var j=0;
		for(var i=0;i<document.checkBodyObjectForm.elements.length;i++)
		{
			if(document.checkBodyObjectForm.elements[i].type=="checkbox")
			{
				if(document.checkBodyObjectForm.elements[i].checked==true)
				{
					str+=document.checkBodyObjectForm.elements[i+1].value;
					seq+=document.checkBodyObjectForm.elements[i+2].value;
					j++;
				}
			}
		}
		if(j>1)
		{
			alert("<bean:message key='jx.paramset.info3'/>?");
			return;
		}
			
		if(str.length==0)
		{
			alert("<bean:message key='label.select'/>!");
			return;
		}else{
			checkBodyObjectForm.action="/performance/options/checkBodyObjectList.do?b_remove=link&deletestr="+str+"&num="+num+"&seq="+seq; 
			checkBodyObjectForm.submit();
		}	
	}
	  
	function IfWindowClosed() 
	{
		if (newwindow.closed == true) 
		{ 
			window.clearInterval(timer)
			checkBodyObjectForm.action="/performance/options/checkBodyObjectList.do?b_query=link"
		    checkBodyObjectForm.submit();
		}
	}
	function tr_bgcolor(nid)
	{
		var tablevos=document.getElementsByTagName("input");
		for(var i=0;i<tablevos.length;i++)
		{
		    if(tablevos[i].type=="checkbox")
		    {
		    	var cvalue = tablevos[i];
		    	var td = cvalue.parentNode.parentNode;
		    	td.style.backgroundColor = '';
			}
	    }
		var c = document.getElementById(nid);
		var tr = c.parentNode.parentNode;
		if(tr.style.backgroundColor!=''){
			tr.style.backgroundColor = '' ;
		}else{
			tr.style.backgroundColor = '#add6a6' ;
		}
	}

	function toSorting()
	{
	   var bodyType = document.getElementById("bodyType").value;
	   var target_url="/performance/options/checkBodySort.do?b_sort=link`bodyType="+bodyType+"`busitype=${checkBodyObjectForm.busitype}";
	   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	   /* var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:500px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no"); */
	   var width = 550;
       if(getBrowseVersion()) {
            width = 500;
       }
	   var config = {
		    width:550,
		    height:400,
		    type:'2',
		    id:'toSorting_win'
		}

		modalDialog.showModalDialogs(iframe_url,"toSorting_win",config,reflesh);
	   
	}	
	function myClose()
	{
		var thevo=new Object();
		<% 
		if(request.getParameter("haveOrNot")!=null && request.getParameter("haveOrNot").equals("having"))
		{
		%>
			thevo.flag="true";
		<% 
		}else{
		%>
			thevo.flag="false";
		<% 
		}
		%>
		window.returnValue=thevo;
	}
</script>
<hrms:themes />
<%
int i = 0;
%>
<body onbeforeunload="myClose();">
	<html:form action="/performance/options/checkBodyObjectList">
	<table  border="0" align="center">
		<tr>
			<td align="center">
				<div class="fixedDiv_self common_border_color" >
		<table width="100%" border="0" cellspacing="0" align="center" cellpadding="1">
			<input type="hidden" name="bodyType" id="bodyType"
				value="${checkBodyObjectForm.bodyType}" />
			<thead>
				<tr class="fixedHeaderTr">
					<td align="center" class="TableRow" nowrap style="border-left:0px;border-top:0px;">
						&nbsp;<input type="checkbox" name="selbox"
							onclick="batch_select(this, 'setlistform.select');">&nbsp;
					</td>
					<td align="center" class="TableRow" nowrap style="border-left:0px;border-top:0px;">
						<bean:message key="report.number" />
					</td>


					<logic:equal name="checkBodyObjectForm" property="bodyType"
						value="0">
						<td align="center" class="TableRow" nowrap style="border-left:0px;border-top:0px;">
							<bean:message key="jx.paramset.mainbodyname" />
						</td>
						<td align="center" class="TableRow" nowrap style="border-left:0px;border-top:0px;">
							<bean:message key="jx.param.dengji" />
						</td>
					</logic:equal>
					<logic:equal name="checkBodyObjectForm" property="bodyType"
						value="1">
						<td align="center" class="TableRow" nowrap style="border-left:0px;border-top:0px;">
							<bean:message key="jx.paramset.objectbodyname" />
						</td>
						<td align="center" class="TableRow" nowrap style="border-left:0px;border-top:0px;">
							<bean:message key="jx.param.objectype" />
						</td>
					</logic:equal>

					<td align="center" class="TableRow" nowrap style="border-left:0px;border-top:0px;">
						<bean:message key="kh.field.flag" />
					</td>
					<td align="center" class="TableRow" nowrap  style="border-left:0px;border-right:0px;border-top:0px;">
						<bean:message key="label.edit.user" />
					</td>

				</tr>
			</thead>
			<hrms:extenditerate id="element" name="checkBodyObjectForm"
				property="setlistform.list" indexes="indexes"
				pagination="setlistform.pagination" pageCount="1000" scope="session">
				<bean:define id="nid" name="element" property="string(body_id)" />
				<%
						if (i % 2 == 0)
						{
				%>
				<tr class="trShallow">
					<%
							} else
							{
					%>
				
				<tr class="trDeep">
					<%
							}
							i++;
					%>
					<td align="center" class="RecordRow" nowrap  style="border-left:0px;border-top:0px;">
						&nbsp;<logic:notEqual name="element" property="string(body_id)"
							value="-1">
						<logic:notEqual name="element" property="string(body_id)"
							value="5">
							<hrms:checkmultibox name="checkBodyObjectForm"
								property="setlistform.select" value="true" indexes="indexes" />
						</logic:notEqual>
					</logic:notEqual>&nbsp;
					</td>
					<td align="left" class="RecordRow" style="border-left:0px;border-top:0px;" nowrap>
						<bean:write name="element" property="string(body_id)"
							filter="true" />
						<Input type='hidden'
							value='<bean:write name="element" property="string(body_id)" filter="true"/>'
							name='bodyId' />
						<Input type='hidden'
							value='<bean:write name="element" property="string(seq)" filter="true"/>'
							name='seq' />
					</td>
					<td align="left" class="RecordRow" style="border-left:0px;border-top:0px;" nowrap>
						<bean:write name="element" property="string(name)" filter="true" />
					</td>
					<logic:equal name="checkBodyObjectForm" property="bodyType" value="1">
						<td align="left" class="RecordRow" style="border-left:0px;border-top:0px;" nowrap>
							 &nbsp;<bean:write name="element" property="string(object_type)" filter="true" />
						</td>
					</logic:equal>
					<logic:equal name="checkBodyObjectForm" property="bodyType"
						value="0">
						<td align="left" class="RecordRow" style="border-left:0px;border-top:0px;" nowrap>
							<logic:notEqual name="checkBodyObjectForm" property="dbType"
								value="oracle">
								<logic:equal name="element" property="string(level)" value="-2">
									<bean:message key='jx.param.degree8' />
								</logic:equal>
								<logic:equal name="element" property="string(level)" value="-1">
									<bean:message key='jx.param.degree7' />
								</logic:equal>
								<logic:equal name="element" property="string(level)" value="0">
									<bean:message key='jx.param.degree0' />
								</logic:equal>
								<logic:equal name="element" property="string(level)" value="1">
									<bean:message key='jx.param.degree1' />
								</logic:equal>
								<logic:equal name="element" property="string(level)" value="2">
									<bean:message key='jx.param.degree2' />
								</logic:equal>
								<logic:equal name="element" property="string(level)" value="3">
									<bean:message key='jx.param.degree3' />
								</logic:equal>
								<logic:equal name="element" property="string(level)" value="4">
									<bean:message key='jx.param.degree4' />
								</logic:equal>
								<logic:equal name="element" property="string(level)" value="5">
									<bean:message key='jx.param.degree5' />
								</logic:equal>
								<logic:equal name="element" property="string(level)" value="6">
									<bean:message key='jx.param.degree6' />
								</logic:equal>
							</logic:notEqual>
							<logic:equal name="checkBodyObjectForm" property="dbType"
								value="oracle">
								<logic:equal name="element" property="string(level_o)" value="-2">
									<bean:message key='jx.param.degree8' />
								</logic:equal>
								<logic:equal name="element" property="string(level_o)" value="-1">
									<bean:message key='jx.param.degree7' />
								</logic:equal>
								<logic:equal name="element" property="string(level_o)" value="0">
									<bean:message key='jx.param.degree0' />
								</logic:equal>
								<logic:equal name="element" property="string(level_o)" value="1">
									<bean:message key='jx.param.degree1' />
								</logic:equal>
								<logic:equal name="element" property="string(level_o)" value="2">
									<bean:message key='jx.param.degree2' />
								</logic:equal>
								<logic:equal name="element" property="string(level_o)" value="3">
									<bean:message key='jx.param.degree3' />
								</logic:equal>
								<logic:equal name="element" property="string(level_o)" value="4">
									<bean:message key='jx.param.degree4' />
								</logic:equal>
								<logic:equal name="element" property="string(level_o)" value="5">
									<bean:message key='jx.param.degree5' />
								</logic:equal>
								<logic:equal name="element" property="string(level_o)" value="6">
									<bean:message key='jx.param.degree6' />
								</logic:equal>
							</logic:equal>
						</td>
					</logic:equal>
					<td align="left" class="RecordRow" style="border-left:0px;border-top:0px;" nowrap>
						<logic:equal name="element" property="string(status)" value="1">
							<bean:message key='column.law_base.status' />
						</logic:equal>
						<logic:equal name="element" property="string(status)" value="0">
							<bean:message key='lable.lawfile.invalidation' />
						</logic:equal>
					</td>
					<td align="center" class="RecordRow" nowrap  style="border-left:0px;border-right:0px;border-top:0px;">					
						<a
							onclick="edit('<bean:write name="element" property="string(body_id)" filter="true"/>');"><img
								src="/images/edit.gif" border=0 style="cursor:hand;">
						</a>						
					</td>
				</tr>
			</hrms:extenditerate>
		</table>
		</div>
	</td>
				</tr>
	</table>

		<table width="100%">
			<tr>
				<td align="center">

						<input type='button' class="mybutton" property="b_add"
							onclick='add()' value='<bean:message key="button.insert"/>' />

						<input type='button' class="mybutton" property="b_delete"
							onclick='checkdelete()'
							value='<bean:message key="button.delete"/>' />

						<input type="button" value="<bean:message key='kq.item.change'/>"
							onclick="toSorting();" Class="mybutton">						
				</td>
			</tr>
		</table>
	</html:form>
</body>
