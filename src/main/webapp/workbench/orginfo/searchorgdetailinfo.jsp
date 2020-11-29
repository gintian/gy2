<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hrms.frame.dao.RecordVo,com.hjsj.hrms.actionform.org.OrgInfoForm"%>
<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<!-- 引入ext 和代码控件      wangb 20171117 -->
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="/components/extWidget/proxy/TransactionProxy.js"></script>
<%
	  int i=0;
%>
<hrms:themes/>
<script language="javascript">
function pdselectcheck()
{
   var len=document.orgInfoForm.elements.length;
   var isCorrect=false;
   for (i=0;i<len;i++)
   {
           if (document.orgInfoForm.elements[i].type=="checkbox"&&document.orgInfoForm.elements[i].name!='selbox')
            {
              if( document.orgInfoForm.elements[i].checked==true)
                isCorrect=true;
            }
   }
   if(!isCorrect)
   {
          alert("请选择记录！");
          return false;
   }else
     return true;
}
  function deletes()
  {
     var len=document.orgInfoForm.elements.length;
     var isCorrect=false;
     for (i=0;i<len;i++)
     {
           if (document.orgInfoForm.elements[i].type=="checkbox")
            {
              if( document.orgInfoForm.elements[i].checked==true)
                isCorrect=true;
            }
     }
    if(!isCorrect)
    {
          alert("请选择记录！");
          return false;
     }
     if(confirm("确认要删除该记录？"))
     {
          orgInfoForm.action = "/workbench/orginfo/searchdetailinfolist.do?b_delete=link";
          orgInfoForm.submit();
     }
  }
   function edit1(encryptParam,i9999,edittype)
  {
     orgInfoForm.action="/workbench/orginfo/searchdetailinfolist.do?b_edit=edit&i9999="+i9999+"&edittype="+edittype+"&encryptParam="+encryptParam;
     orgInfoForm.submit();
  }
  function edit2(encryptParam,i9999,edittype)
  {
     orgInfoForm.action="/workbench/orginfo/searchdetailinfolist.do?b_edit=edit&i9999="+i9999+"&edittype="+edittype+"&encryptParam="+encryptParam;
     orgInfoForm.submit();
  }
  function exeReturn(returnStr,target)
{
  //target_url=returnStr;
 // window.open(target_url,target); 
   orgInfoForm.action=returnStr;
   orgInfoForm.target=target;
   orgInfoForm.submit();
}

function upItem(rowid,codeitemid,i9999){
	var _table=$("tableid");
	var _rowid=parseInt(rowid);
	if(_table.rows.length<3||_rowid<1){
		return;
	}
	var hashvo=new HashMap();  
	hashvo.put("rowid", _rowid);      
    hashvo.put("fieldsetid", '${orgInfoForm.setname}');
    hashvo.put("codeitemid", codeitemid);
    hashvo.put("i9999", i9999);
    hashvo.put("type", 'up');
    Rpc({functionId:'3409000022',async:false,success:upItemview},hashvo);//update by xiegh on date 20180319 bug35674 
    //var request=new Request({method:'post',onSuccess:upItemview,functionId:'3409000022'},hashvo);
}

function upItemview(outparamters){
	var result  = Ext.decode(outparamters.responseText); 
	if(getBrowseVersion()){
		var rowid=parseInt(result.rowid);
		var _rowid=rowid;
		var _table=$("tableid");
		var _row1=_table.rows[rowid];
		var _row2=_table.rows[rowid+1];
		var tempclass=_row1.className;
		_row1.className=_row2.className;
		_row2.className=tempclass;
		if(_table.rows.length<3||_rowid<=1){
			var _cell1=_row1.cells[_row1.cells.length-1].innerHTML;
			//alert(_cell1);
			//_cell1=_cell1.replace('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;','&nbsp');
			//_cell1=_cell1.replace('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;','');
			//_cell1=_cell1.replace('downItem','upItem').replace('down01','up01')+_cell1;
			//alert(_cell1);
			var _cell2=_row2.cells[_row2.cells.length-1].innerHTML;
			//alert(_cell2);
			//_cell2='&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;'+((_cell2.split('&nbsp;'))[2]);
			//alert(_cell2);
			_row1.cells[_row1.cells.length-1].innerHTML=_cell2;
			_row2.cells[_row2.cells.length-1].innerHTML=_cell1;
		}else{
			var _cell1=_row1.cells[_row1.cells.length-1].innerHTML;
			var _cell2=_row2.cells[_row2.cells.length-1].innerHTML;
			_row1.cells[_row1.cells.length-1].innerHTML=_cell2;
			_row2.cells[_row2.cells.length-1].innerHTML=_cell1;
		}
		_table.moveRow(_rowid+1,_rowid);
	}else{//非IE浏览器  排序无效 刷新页面重加加载数据排序   bug 35097  wangb 20180301
		/* location.reload(); */
		//update by xiegh on date 20180319 bug35674 不可以使用location.reload()  reload只会更新数据 但是界面的顺序不会改变
		orgInfoForm.action = "/workbench/orginfo/searchdetailinfolist.do?b_search=link";
        orgInfoForm.submit();
	}
}
function downItem(rowid,codeitemid,i9999){
	var _table=$("tableid");
	var _rowid=parseInt(rowid);
	if(_table.rows.length<3||(_rowid+2)==_table.rows.length){
		return;
	}
	var hashvo=new HashMap();          
   	hashvo.put("rowid", _rowid);          
    hashvo.put("fieldsetid", '${orgInfoForm.setname}');
    hashvo.put("codeitemid", codeitemid);
    hashvo.put("i9999", i9999);
    hashvo.put("type", 'down');
    /* var request=new Request({method:'post',onSuccess:downItemview,functionId:'3409000022'},hashvo); */
    Rpc({functionId:'3409000022',async:false,success:downItemview},hashvo);//update by xiegh on date 20180319 bug35674 
}

function downItemview(outparamters){
	var result  = Ext.decode(outparamters.responseText); 
	if(getBrowseVersion()){
		var rowid=parseInt(result.rowid);
		var _rowid=rowid;
		var _table=$("tableid");
		var _row1=_table.rows[rowid+1];
		var _row2=_table.rows[rowid+2];
		var tempclass=_row1.className;
		_row1.className=_row2.className;
		_row2.className=tempclass;
		var _cell1=_row1.cells[_row1.cells.length-1].innerHTML;
		var _cell2=_row2.cells[_row2.cells.length-1].innerHTML;
		_row1.cells[_row1.cells.length-1].innerHTML=_cell2;
		_row2.cells[_row2.cells.length-1].innerHTML=_cell1;
		_table.moveRow(_rowid+1,_rowid+2);
	}else{//非IE浏览器  排序无效 刷新页面重加加载数据排序   bug 35097  wangb 20180301
		/* location.reload(); */
		//update by xiegh on date 20180319 bug35674 不可以使用location.reload()  reload只会更新数据 但是界面的顺序不会改变
		orgInfoForm.action = "/workbench/orginfo/searchdetailinfolist.do?b_search=link";
        orgInfoForm.submit();
	}
}

function showleader(b0110,i9999){
	var org_m='${orgInfoForm.org_m }';
	var emp_e='${orgInfoForm.emp_e }';
	var link_field='${orgInfoForm.link_field }';
	var b0110field='${orgInfoForm.b0110 }';
	var orderbyfield='${orgInfoForm.order_by }';
	
	var leaderTypeValue = '${orgInfoForm.leaderTypeValue }';
	var sessionValue = '${orgInfoForm.sessionValue}';
	orgInfoForm.target='nil_body';
	var url = "/workbench/info/leader/showinfodata.do?b_leader=link&b0110="+b0110+"`i9999="+i9999+"`emp_e="
			+emp_e+"`link_field="+link_field+"`b0110field="+b0110field+"`orderbyfield="+orderbyfield
			+"`orglike=1`org_m="+org_m+"`leaderTypeValue="+leaderTypeValue+"`sessionValue="+sessionValue;
	orgInfoForm.action="/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
	turn();
	orgInfoForm.submit();
}

function turn()
{
   parent.parent.menupnl.toggleCollapse(false);
}
function batch_assave()
{
   if(!pdselectcheck())
   { 
      return false;
   }
   /*弹出选择指标项
   var dh="370px";
   var wh="440px";
   if(navigator.appVersion.indexOf('MSIE 6') != -1){
		dh="430px";
		wh="460px";
   } */ 
   /*兼容非IE流浪器弹窗,使用ext框架实现   wangb 20171120 */
   var store = Ext.create('Ext.data.Store',{
	   fields:['feildsetid','itemid','itemdesc'],
	   proxy:Ext.create("EHR.extWidget.proxy.TransactionProxy",{   
           extraParams:{
        	   setname:'${orgInfoForm.setname}'
           },
           reader:{
              type:'json',
              root:'assave_fieldlist'
           },
           functionId:'0401000009'
       }),
       autoLoad:false
   });
   store.load();
   var gridPanel = Ext.create('Ext.grid.Panel',{
	   store:store,
	   bodyStyle:'border-color:#C5C5C5;',
	   selModel:{selType:'checkboxmodel'},
	   columns:[{text:'指标名称',dataIndex:'itemdesc',flex:1,align:'center',}]
   });
   var win = Ext.create('Ext.window.Window',{
	   title:'批量另存',
	   width:navigator.appVersion.indexOf('MSIE 6') != -1 ? 430:370,
	   height:navigator.appVersion.indexOf('MSIE 6') != -1 ? 460:440,
	   modal:true,
	   layout:'fit',
	   overflowY:'auto',
	   items:gridPanel,
	   buttonAlign:'center',
	   buttons:[{
		   text:'确定',
		   type:'submit',
		   handler:function(){
			  var fielditems = win.items.items[0].getSelectionModel().getSelection();
			  if(fielditems.length <= 0){
				  Ext.Msg.alert('','请选择指标！');
				  return;
			  }
			  var values = '';
			  for(var i = 0 ; i < fielditems.length ; i++){
				  values += fielditems[i].data.itemid + ',';
			  }
			  values = values.substring(0,values.length-1);
			  var o_obj=document.getElementsByName('assavefields')[0];
			  o_obj.value=values;
		      orgInfoForm.action="/workbench/orginfo/assavefielditem.do?b_assave=link&setname=${orgInfoForm.setname}";
			  orgInfoForm.submit();
		   }
	   }]
   });
   win.show();
   /*
   var target_url="";
   var return_vo;
   target_url="/workbench/orginfo/assavefielditem.do?b_search=link&setname=${orgInfoForm.setname}";
   return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:"+wh+"; dialogHeight:"+dh+";resizable:no;center:yes;scroll:no;status:no;scrollbars:yes");
   if(return_vo)
   {
      var o_obj=document.getElementById('assavefields');
      o_obj.value=return_vo;
      orgInfoForm.action="/workbench/orginfo/assavefielditem.do?b_assave=link&setname=${orgInfoForm.setname}";
	  orgInfoForm.submit();
   }
   */
   //返回选择的指标
}

function selectLeader(){
	orgInfoForm.action="/workbench/orginfo/searchdetailinfolist.do?b_search=link";
	orgInfoForm.submit();
}
</script>
<html:form action="/workbench/orginfo/searchdetailinfolist" styleId="searchOrgInfoForm">
<html:hidden name="orgInfoForm" property="assavefields"/>
<html:hidden name="orgInfoForm" property="setname"/>
<table width="100%" border="0" cellspacing="0" cellpadding="0" >
<tr>
<td>
	<logic:equal value="leader" name="orgInfoForm" property="leader" >
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" id="tableid">
	  <tr>
	   <td align="left" >
	     <table border="0" cellspacing="0" cellpadding="0">
	      <tr>
	        <td align="left"  nowrap>
	              <logic:notEmpty  name="orgInfoForm" property="code">
		          <bean:message key="system.browse.info.currentorg"/>:
		          <hrms:codetoname codeid="UN" name="orgInfoForm" codevalue="code" codeitem="codeitem" scope="session" uplevel="5"/>  	      
	          	  <bean:write name="codeitem" property="codename" />
	          	  <hrms:codetoname codeid="UM" name="orgInfoForm" codevalue="code" codeitem="codeitem" scope="session" uplevel="5"/>  	      
	          	  <bean:write name="codeitem" property="codename" />
	          	  <hrms:codetoname codeid="@K" name="orgInfoForm" codevalue="code" codeitem="codeitem" scope="session" uplevel="5"/>  	      
	          	  <bean:write name="codeitem" property="codename" />
	          	  &nbsp;&nbsp;
		         </logic:notEmpty>
		         <logic:equal value="${orgInfoForm.org_m}" name="orgInfoForm" property="setname"> 
		               
		               <logic:notEmpty name="orgInfoForm" property="leaderType">
		               &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		                   班子类型
			               <html:select name="orgInfoForm" property="leaderTypeValue" onchange="selectLeader();">
			                   <html:optionsCollection property="leaderTypeList" label="dataName" value="dataValue"/>
			               </html:select>
		               </logic:notEmpty>
		               
		              <logic:notEmpty name="orgInfoForm" property="sessionitem"> 
		              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		               届次
		               <html:select name="orgInfoForm" property="sessionValue" onchange="selectLeader();">
		                   <html:optionsCollection property="sessionItemList" label="dataName" value="dataValue"/>
		               </html:select>
	                  </logic:notEmpty>	               
	             </logic:equal>
		    </td>       
	       </tr>
	     </table>
	   </td>
	 </tr>
	  </table>
	</logic:equal> 
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF" id="tableid" style="margin-top: 3px">    
	           <tr>
	             <td align="center" class="TableRow checkboxRecordRow" width='30' nowrap>
	              <input type="checkbox" name="selbox" onclick="batch_select(this,'orgInfoForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
	             </td>
	              <logic:equal name="orgInfoForm" property="setprv" value="2">  
	              <logic:notEqual value="leader" name="orgInfoForm" property="leader">
		             <td align="center" class="TableRow" width='40' nowrap>
						<bean:message key="button.new.insert"/>            	
		             </td>
	             </logic:notEqual> 
	             </logic:equal>
	           <logic:equal name="orgInfoForm" property="setprv" value="2"> 
	            <logic:notEqual value="leader" name="orgInfoForm" property="leader"> 
	             <td align="center" class="TableRow" width='40' nowrap>
					<bean:message key="label.edit"/>            	
	             </td> 
	             </logic:notEqual>   
	            </logic:equal> 
	             <logic:equal name="orgInfoForm" property="setprv" value="3"> 
	              <logic:notEqual value="leader" name="orgInfoForm" property="leader"> 
	             <td align="center" class="TableRow" width='40' nowrap>
					<bean:message key="label.edit"/>            	
	             </td>    
	             </logic:notEqual>
	            </logic:equal> 
	            <logic:iterate id="element"    name="orgInfoForm"  property="infofieldlist"> 
	              	<logic:match value="z0" name="element" property="itemid"><!-- 年月标示 -->
	              		<td align="center"  class="TableRow" width='90' nowrap>
	              			<bean:write  name="element" property="itemdesc"/>&nbsp; 
	              		</td>
	              	</logic:match>
	              	<logic:match value="z1" name="element" property="itemid"><!-- 次数 -->
	              		<td align="center" class="TableRow" width='40' nowrap>
	              			<bean:write  name="element" property="itemdesc"/> 
	              		</td>
	              	</logic:match>
	              	<logic:notMatch value="z0" name="element" property="itemid">
	              		<logic:notMatch value="z1" name="element" property="itemid">
		              		<td align="center" class="TableRow" nowrap>
		              			<bean:write  name="element" property="itemdesc"/>&nbsp; 
		              		</td>
	              		</logic:notMatch>
	              	</logic:notMatch>
	             </logic:iterate>
	             <logic:notEqual value="leader" name="orgInfoForm" property="leader"> 
	          	<td align="center" class="TableRow" nowrap>
					<bean:message key="label.zp_exam.sort"/>             	
				</td> 
				</logic:notEqual>
				<logic:equal value="leader" name="orgInfoForm" property="leader"> 
					<logic:equal value="${orgInfoForm.org_m}" name="orgInfoForm" property="setname"> 
					<hrms:priv func_id="231521">
					<td align="center" class="TableRow" nowrap>
						    班子成员       	
					</td> 
					</hrms:priv>
					</logic:equal>	
				</logic:equal>	
				<logic:equal value="leader" name="orgInfoForm" property="leader"> 
					<td align="center" class="TableRow" nowrap>
						  编辑         	
					</td> 
				</logic:equal>	        	        	        
	           </tr>
	           
	            <%
	           		OrgInfoForm orgInfoForm=(OrgInfoForm)session.getAttribute("orgInfoForm");
	           		int len = orgInfoForm.getOrgInfoForm().getList().size();
	            %>
	          <hrms:extenditerate id="element" name="orgInfoForm" property="orgInfoForm.list" indexes="indexes"  pagination="orgInfoForm.pagination" pageCount="${orgInfoForm.pagerows}" scope="session">
	          <%
	          if(i%2==0)
	          {
	          %>
	          <tr class="trShallow">
	          <%}
	          else
	          {%>
	          <tr class="trDeep">
	          <%
	          }
	          i++;    
	          RecordVo vo=(RecordVo)element;         
	          %>  
	            <td align="center" class="RecordRow checkboxRecordRow" nowrap>
	               <hrms:checkmultibox name="orgInfoForm" property="orgInfoForm.select" value="true" indexes="indexes"/>&nbsp;
	            </td>
	            <logic:equal name="orgInfoForm" property="setprv" value="2">  
	            <logic:notEqual value="leader" name="orgInfoForm" property="leader">
	              <td align="center" class="RecordRow" nowrap>
	              <!-- 
	            	<a href="javascript:edit1('${orgInfoForm.code}','<bean:write  name="element" property="string(i9999)" filter="true"/>','insert');"><img src="/images/goto_input.gif" border=0></a>
		           -->
		           <a href="javascript:edit1('${orgInfoForm.encryptParam}','<bean:write  name="element" property="string(i9999)" filter="true"/>','insert');"><img src="/images/goto_input.gif" border=0></a>
		      </td> 
				</logic:notEqual>  
		     </logic:equal> 
	             <logic:equal name="orgInfoForm" property="setprv" value="2"> 
	             <logic:notEqual value="leader" name="orgInfoForm" property="leader"> 
	              <td align="center" class="RecordRow" nowrap>
	            	<a href="javascript:edit1('${orgInfoForm.encryptParam}','<bean:write  name="element" property="string(i9999)" filter="true"/>','update');"><img src="/images/edit.gif" border=0></a>
		      </td> 
				</logic:notEqual>  
		     </logic:equal> 
		      <logic:equal name="orgInfoForm" property="setprv" value="3">
		      <logic:notEqual value="leader" name="orgInfoForm" property="leader">  
	              <td align="center" class="RecordRow" nowrap>
	            	<a href="javascript:edit2('${orgInfoForm.encryptParam}','<bean:write  name="element" property="string(i9999)" filter="true"/>','update');"><img src="/images/edit.gif" border=0></a>
		      </td>    
		      </logic:notEqual>
		     </logic:equal> 
	            <logic:iterate id="info"    name="orgInfoForm"  property="infofieldlist">            
	              <logic:equal  name="info" property="itemtype" value="M"> 
	               <%
	                 FieldItem item=(FieldItem)pageContext.getAttribute("info");
	                 String tx=vo.getString(item.getItemid());
	                // tx = tx.replaceAll("\n"," ");
	               %> 
	               <hrms:showitemmemo showtext="showtext" itemtype="M" setname="${orgInfoForm.setname}" tiptext="tiptext" text="<%=tx%>"></hrms:showitemmemo>             
	                  <td align="left" class="RecordRow" ${tiptext} nowrap> &nbsp;              
	                   ${showtext}&nbsp;
	                  </td> 
	              </logic:equal> 
	              <logic:notEqual  name="info" property="itemtype" value="M"> 
	                    <logic:notEqual  name="info" property="itemtype" value="N">               
	                      <td align="left" class="RecordRow" nowrap>   &nbsp;     
	                    </logic:notEqual>
	                    <logic:equal  name="info" property="itemtype" value="N">               
	                      <td align="right" class="RecordRow" nowrap>  &nbsp;      
	                    </logic:equal>          
	                   <bean:write  name="element" property="string(${info.itemid})" filter="true"/>&nbsp;
	                   </td>
	              </logic:notEqual>
	               
	           </logic:iterate> 
	           <logic:notEqual value="leader" name="orgInfoForm" property="leader">
	                <td align="center" class="RecordRow" width="60" nowrap style="padding: 0 0 0 0;">
	                 	<%if(i!=1){ %>
						&nbsp;<a href="javaScript:upItem('${indexes }','<%=vo.getString("b0110") %>','<bean:write  name="element" property="string(i9999)" filter="true"/>')">
						<img src="/images/up01.gif" width="12" height="17" border=0></a> 
						<%}else{ %>
							<script type="text/javascript">
			           			if(isIE6()){
			           				document.write("&nbsp;&nbsp;&nbsp;&nbsp;");
			           			}else{
			           				document.write("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			           			}
			           	    </script>
						<%} %>
					    <%if(len==i){ %>
					    	<script type="text/javascript">
			           			if(isIE6()){
			           				document.write("&nbsp;&nbsp;&nbsp;&nbsp;");
			           			}else{
			           				document.write("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			           			}
			           	    </script>
					    <%}else{ %>
						&nbsp;<a href="javaScript:downItem('${indexes }','<%=vo.getString("b0110") %>','<bean:write  name="element" property="string(i9999)" filter="true"/>')">
						<img src="/images/down01.gif" width="12" height="17" border=0></a> 
						<%} %>
					</td>  
				</logic:notEqual> 
				
				    <logic:equal value="leader" name="orgInfoForm" property="leader"> 
					<logic:equal value="${orgInfoForm.org_m}" name="orgInfoForm" property="setname"> 
					<hrms:priv func_id="231521">
					<td align="center" class="RecordRow" nowrap>
						<%
							UserView userView = (UserView)session.getAttribute("userView");
							String emp_e=(String)orgInfoForm.getEmp_e();
						if(!userView.analyseTablePriv(emp_e).equals("0")){ %>
						     <a href="javascript:showleader('${orgInfoForm.code}','<bean:write  name="element" property="string(i9999)" filter="true"/>');">班子成员</a>      	
						<%} %>
					</td> 
					</hrms:priv>
					</logic:equal>	
				</logic:equal>	
				 
				<logic:equal value="leader" name="orgInfoForm" property="leader"> 
					<td align="center" class="RecordRow"  nowrap>
						<logic:equal name="orgInfoForm" property="setprv" value="2">
						      <a href="javascript:edit1('${orgInfoForm.encryptParam}','<bean:write  name="element" property="string(i9999)" filter="true"/>','update');">编辑</a>     	
						</logic:equal>
						<logic:equal name="orgInfoForm" property="setprv" value="3">
						      <a href="javascript:edit1('${orgInfoForm.encryptParam}','<bean:write  name="element" property="string(i9999)" filter="true"/>','update');">编辑</a>     	
						</logic:equal>
					</td> 
				</logic:equal>  
				  	    		        	        	        
	          </tr>
	        </hrms:extenditerate>
	</table>
</td>
</tr>
<tr>
<td>
	<table  width="100%" class="RecordRowP">
			<tr>
			    <td>
			    <!--  为了可以调节页面显示的条数，使用下面的方法，这里注掉 guodd 2014-3-26	<bean:message key="label.page.serial" />
					<bean:write name="orgInfoForm" property="orgInfoForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum" />
					<bean:write name="orgInfoForm" property="orgInfoForm.pagination.count" filter="true" />
					<bean:message key="label.page.row" />
					<bean:write name="orgInfoForm" property="orgInfoForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page" />
					-->
					<hrms:paginationtag name="orgInfoForm"
									pagerows="${orgInfoForm.pagerows}" property="orgInfoForm.pagination"
									 refresh="true"></hrms:paginationtag>
				</td>
		        <td  align="right"  nowrap>
			          <p align="right"><hrms:paginationlink name="orgInfoForm" property="orgInfoForm.pagination"
					nameId="orgInfoForm" propertyId="roleListProperty">
					</hrms:paginationlink>
				</td>
			</tr>
	</table>
</td>
</tr>
</table>
<table  width="70%" align="left" cellpadding="0" cellspacing="0" style="padding-top: 5px;">
          <tr>
            <td align="left">
            <html:hidden name="orgInfoForm" property="edittype" value="new"/>
             <logic:equal name="orgInfoForm" property="setprv" value="2">
               	<hrms:submit styleClass="mybutton" property="b_edit">
            		<bean:message key="button.insert"/>
	        	</hrms:submit>           	
	 	        <input type="button" name="b_delete" class="mybutton" value="<bean:message key="button.delete" />" onclick="javascript:deletes();">
	 	        <logic:equal name="orgInfoForm" property="isWrite" value="1">
	 	          <input type="button" name="b_delete" class="mybutton" value="<bean:message key="button.batch.assave" />" onclick="javascript:batch_assave();">
	 	        </logic:equal>	 	        
	 	     </logic:equal> 
	 	     <logic:equal name="orgInfoForm" property="returnvalue" value="scan">
	 	        <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/orginfo/searchorginfodata.do?b_query=link&code=${orgInfoForm.return_codeid}','nil_body')">                 
             </logic:equal>
             <logic:equal name="orgInfoForm" property="returnvalue" value="75"><!--预警-->
             <logic:equal value="bi" name="userView" property="bosflag">
                <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/system/warn/result_manager.do?b_query=link','i_body')">                 
             </logic:equal>
             <logic:notEqual value="bi" name="userView" property="bosflag">
                <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/system/warn/result_manager.do?b_query=link','il_body')">                 
             </logic:notEqual>
             </logic:equal>	 	
            </td>
          </tr>          
 </table>

</html:form>
<script type="text/javascript">
var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
var isFF = userAgent.indexOf("Firefox") > -1; //判断是否Firefox浏览器
if(isFF){//火狐浏览器样式特殊处理  
	var form = document.getElementById('searchOrgInfoForm'); //给form表单添加滚动条    wangb 20180226 34814
	form.style.overflow = 'auto';
	form.style.height = '100%'; //给form表单设置 高度 滚动条 最下面显示  wangb 20180502 bug  36699
} 
</script>
