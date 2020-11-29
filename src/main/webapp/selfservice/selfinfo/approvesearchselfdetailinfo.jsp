<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
<%@ page import="com.hjsj.hrms.actionform.selfinfomation.SelfInfoForm"%>
<%
	  int i=0;
%>
<!-- 引入ext框架      wangb 20180208 -->
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script language="javascript">
function ykcard()
{
  target_url="/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase=${selfInfoForm.userbase}&tabid=${selfInfoForm.emp_cardId}&multi_cards=-1&inforkind=1&npage=1&userpriv=${selfInfoForm.userpriv}";
 // window.open(target_url,"nn"); 
    selfInfoForm.action=target_url;
    selfInfoForm.target="_blank";
    selfInfoForm.submit();
}
function bedit()
{
    selfInfoForm.action="/selfservice/selfinfo/searchselfdetailinfo.do?b_edit=link";
    selfInfoForm.target="_self";
    selfInfoForm.submit();  
}

function change() {
 	var list = document.getElementById("list");
 	va = list.value;
 	if (va == "A01") {
 		selfInfoForm.action = "/selfservice/selfinfo/appEditselfinfo.do?b_defend=link&isAppEdite=1&setname="+va+"&i9999=I9999&actiontype=update";
 	} else if(va == "A00"){
 		selfInfoForm.action ="/workbench/media/searchmediainfolist.do?b_appsearch=link&setname=A00&setprv=2&flag=self&returnvalue=3&isUserEmploy=0&button=0";
 							 
 	} else{
 		selfInfoForm.action = "/selfservice/selfinfo/appEditselfinfo.do?b_defendother=search&setname="+va+"&flag=infoself&isAppEdite=1"
 	}
 	selfInfoForm.target="mil_body";
	selfInfoForm.submit();
}

function exReturn() {
	selfInfoForm.action = "/selfservice/selfinfo/searchselfdetailinfo.do?b_search=search&setname=${selfInfoForm.setname}&flag=infoself"
	selfInfoForm.target="mil_body";
	selfInfoForm.submit();
}
function multimediahref(keyvalue,sequence,id,state){
	var thecodeurl =""; 
	var setname = '${selfInfoForm.setname}';
	var a0100 = '${selfInfoForm.selfA0100}';
	if(!a0100)
		a0100 = '${selfInfoForm.a0100}';
		
	var dbname = '${selfInfoForm.selfBase}';
	if(!dbname)
		dbname = '${selfInfoForm.userbase}';
		
	var dw=800,dh=500,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	var i9999 = keyvalue;
	var reload;
	if(!sequence || !id || !state){
		keyvalue = sequence = id = state="";
	}
	//else{
	//	i9999 = "";
	//}
//  	thecodeurl="/general/inform/multimedia/multimedia_tree.do?b_query=link&dbflag=A&canedit=selfedit&setid="+setname+"&a0100="+a0100+"&nbase="
//  			   +dbname+"&i9999="+i9999+"&chg_id="+id+"&state="+state+"&sequence="+sequence;
  	thecodeurl="/general/inform/multimedia/multimedia_tree.do?b_query=link`dbflag=A`canedit=selfedit`setid="+setname+"`a0100="+a0100+"`nbase="
  			   +dbname+"`i9999="+i9999+"`chg_id="+id+"`state="+state+"`sequence="+sequence;
  	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
  	if(getBrowseVersion()){
  		reload = window.showModalDialog(iframe_url, "", 
  		"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:800px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no");
  		if(reload=='true'){
  			selfInfoForm.action = "/selfservice/selfinfo/appEditselfinfo.do?b_defendother=link&isAppEdite=1";
  			selfInfoForm.target="mil_body";
  			selfInfoForm.submit();
  		}
  	}else{//非IE浏览器 使用ext window显示  bug 34710 wangb 20180208
  		var dialog=[];dialog.dw=dw;dialog.dh=dh;dialog.iframe_url=iframe_url;
  		openWin(dialog);
  	}
  	
}
//兼容非IE浏览器 ext弹窗方法  wangb 20180208 bug 34710
function openWin(dialog){
		Ext.create("Ext.window.Window",{
		id:'showfj',
		width:dialog.dw,
		height:dialog.dh,
		title:'请选择',
		resizable:false,
		modal:true,
		autoScroll:true,
		renderTo:Ext.getBody(),
		html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+dialog.iframe_url+"'></iframe>"
	}).show();	
}
//关闭ext弹窗方法  wangb 20180208 bug 34710
function winClose(){
	Ext.getCmp('showfj').close();
}
function returnValue(reload){
	if(reload=='true'){
  			selfInfoForm.action = "/selfservice/selfinfo/appEditselfinfo.do?b_defendother=link&isAppEdite=1";
  			selfInfoForm.target="mil_body";
  			selfInfoForm.submit();
  	}
}
</script>
<hrms:themes />
<html:form action="/selfservice/selfinfo/appEditselfinfo">
<html:hidden  name="selfInfoForm" property="setname"/>
	<logic:equal name="selfInfoForm" property="isAppEdite" value="1">
		<logic:equal name="selfInfoForm" property="setprv" value="2">
			<div style="margin-left:0px;margin-bottom:5px;margin-top:8px;">
				<bean:message key="selfinfo.listinfo"/>
					<select id="list" name="fieldsetid" onchange="change();">
						<logic:iterate id="setList" name="selfInfoForm" property="infoSetList">
							<logic:equal name="setList" property="priv_status" value="2">
						
									<logic:equal value="${setList.fieldsetid }" name="selfInfoForm" property="setname">
										<option value="${setList.fieldsetid }" selected="selected">
											<bean:write name="setList" property="customdesc"/>
										</option>
									</logic:equal>
									<logic:notEqual value="${setList.fieldsetid }" name="selfInfoForm" property="setname">
										<option value="${setList.fieldsetid }">
											<bean:write name="setList" property="customdesc"/>
										</option>
									</logic:notEqual>
	
							</logic:equal>
						</logic:iterate>
					</select>
			</div>
		</logic:equal>
	</logic:equal>
  <html:hidden name="selfInfoForm" property="a0100"/>
  <html:hidden name="selfInfoForm" property="userbase" styleClass="text"/>
<table width="100%" style="margin-top: 3px;" border="0" cellspacing="0"  align="center" cellpadding="0">
    <tr>
           <td align="left"  nowrap>
                (<bean:message key="label.title.org"/>: <bean:write  name="selfInfoForm" property="b0110" filter="true"/>&nbsp;
                <bean:message key="label.title.dept"/>: <bean:write  name="selfInfoForm" property="e0122" filter="true"/>&nbsp;
                <bean:message key="label.title.name"/>: <bean:write  name="selfInfoForm" property="a0101" filter="true"/>&nbsp;
                 )
              </td>
          </tr>
</table>
<table width="100%" border="0" cellspacing="0"  align="center" style="MARGIN-LEFT: 5px;" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <logic:equal name="selfInfoForm" property="setprv" value="2">
             <td align="center" class="TableRow" nowrap>
              <!--<bean:message key="column.select"/>-->
              <input type="checkbox" name="sfull" onclick="full();">
             </td>
            <logic:equal value="1" name="selfInfoForm" property="isAppEdite">
            	<logic:equal value="1" name="selfInfoForm" property="isAble">
             <td align="center" class="TableRow" nowrap>
             <bean:message key='button.new.insert'/>
             </td>
             </logic:equal>
             </logic:equal>
             </logic:equal>
             <logic:equal value="1" name="selfInfoForm" property="approveflag">
             <logic:notEqual value="1" name="selfInfoForm" property="inputchinfor">   
               <td align="center" class="TableRow" nowrap>
                   <bean:message key="info.appleal.statedesc"/>
                </td>
             </logic:notEqual>
           </logic:equal>
            <logic:greaterThan name="selfInfoForm" property="setprv" value="1">
            <logic:equal value="1" name="selfInfoForm" property="isAppEdite">
            	<logic:equal value="1" name="selfInfoForm" property="isAble">
            		<logic:equal value="1" name="selfInfoForm" property="multimedia_file_flag">
               		<td align="center" class="TableRow" nowrap><bean:message key='conlumn.resource_list.name'/></td>
               </logic:equal> 
             <td align="center" class="TableRow" nowrap>
		<bean:message key='column.operation'/> 	
             </td>
             </logic:equal>
             </logic:equal>
             <!-- yuxiaochun add programe -->   
            </logic:greaterThan>  
             <logic:iterate id="element"    name="selfInfoForm"  property="infoFieldList"> 
              <td align="center" class="TableRow" nowrap>
                  <hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>
              </td>
             </logic:iterate>
             <logic:equal value="1" name="selfInfoForm" property="isAppEdite">
           		<td align="center" class="TableRow" nowrap>
                  <bean:message key="selfinfo.status"/>
              </td>
              </logic:equal>  		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="selfInfoForm" property="page.list" indexes="indexes"  pagination="page.pagination" pageCount="${selfInfoForm.num_per_page}" scope="session">
         <bean:define id="i9" name="element" property="string(i9999)"/>
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
           <html:hidden name="selfInfoForm" property="actiontype" value="new"/>
            <logic:equal name="selfInfoForm" property="setprv" value="2">
            <td align="center" class="RecordRow" nowrap>
               <logic:notEqual value="approve" name="element" property="string(state)">
              <hrms:checkmultibox name="selfInfoForm" property="page.select" value="true" indexes="indexes"/>
            	</logic:notEqual>	
            </td>
           <logic:equal value="1" name="selfInfoForm" property="isAppEdite">
           <logic:equal value="1" name="selfInfoForm" property="isAble">
            <td align="center" class="RecordRow" nowrap>
            
            <!-- 
            这里增加新的纪录
            -->
            <logic:notEqual value="new" name="element" property="string(state)">
				<logic:notEqual value="update" name="element" property="string(state)">
					<logic:notEqual value="delete" name="element" property="string(state)">
						<logic:notEqual value="insert" name="element" property="string(state)">
							<logic:notEqual value="approve" name="element" property="string(state)">
					            <logic:notEqual value="approve" name="element" property="string(state)">
					             <img src="/images/goto_input.gif" border=0 onclick="cadd(${i9})">
					             </logic:notEqual>
					        </logic:notEqual>
					    </logic:notEqual>
					</logic:notEqual>
				</logic:notEqual>
			</logic:notEqual>
            </td>
            </logic:equal>
            </logic:equal>
            </logic:equal>
            <logic:equal value="1" name="selfInfoForm" property="approveflag">
            <logic:notEqual value="1" name="selfInfoForm" property="inputchinfor">   
            <td align="center" class="RecordRow" nowrap>
               <logic:equal name="element" property="string(state)" value="0">
                  <bean:message key="info.appleal.state0"/>
               </logic:equal>
               <logic:equal name="element" property="string(state)" value="1">
                  <bean:message key="info.appleal.state1"/>
               </logic:equal>
               <logic:equal name="element" property="string(state)" value="2">
                  <bean:message key="info.appleal.state2"/>
               </logic:equal>
               <logic:equal name="element" property="string(state)" value="3">
                  <bean:message key="info.appleal.state3"/>
               </logic:equal>
               <logic:equal name="element" property="string(state)" value="4">
                  <bean:message key='button.app'/>
               </logic:equal>
               <logic:equal name="element" property="string(state)" value="5">
                  <bean:message key='workdiary.message.can.update'/>
               </logic:equal>
               <logic:equal name="element" property="string(state)" value="6">
                  <bean:message key='info.appleal.state6'/>
               </logic:equal>
               <logic:notEqual name="element" property="string(state)" value="0">
                 <logic:notEqual name="element" property="string(state)" value="1">
                   <logic:notEqual name="element" property="string(state)" value="2">
                      <logic:notEqual name="element" property="string(state)" value="3">
                      <logic:notEqual name="element" property="string(state)" value="4">
                      <logic:notEqual name="element" property="string(state)" value="5">
                      <logic:notEqual name="element" property="string(state)" value="6">
                        <bean:message key="info.appleal.state3"/>
                        </logic:notEqual>
                        </logic:notEqual>
                        </logic:notEqual>
                      </logic:notEqual>
                    </logic:notEqual>
                  </logic:notEqual>
               </logic:notEqual>
            </td>
             </logic:notEqual>
            </logic:equal>
            <logic:equal value="1" name="selfInfoForm" property="isAppEdite">
            <logic:greaterThan name="selfInfoForm" property="setprv" value="1"> 
            <logic:equal value="1" name="selfInfoForm" property="isAble">
            <!-- 附件按钮-->
            	   <logic:equal value="1" name="selfInfoForm" property="multimedia_file_flag">
            	   	<td align="center" class="RecordRow" nowrap>
            	   		<logic:notEqual value="new" name="element" property="string(state)">
							<logic:notEqual value="update" name="element" property="string(state)">
								<logic:notEqual value="delete" name="element" property="string(state)">
									<logic:notEqual value="insert" name="element" property="string(state)">
										<logic:notEqual value="approve" name="element" property="string(state)">
	           								<IMG border=0 src="/images/muli_view.gif" onclick="multimediahref(${i9})">
	           							</logic:notEqual>	            
									</logic:notEqual>
								</logic:notEqual>
							</logic:notEqual>
					</logic:notEqual>
               		<logic:equal value="new" name="element" property="string(state)">
						<IMG border=0 src="/images/muli_view.gif" onclick="multimediahref('<bean:write name="element" property="string(a0100)"/>','<bean:write name="element" property="string(i9999)"/>','<bean:write name="element" property="string(id)"/>','<bean:write name="element" property="string(state)"/>');">
					</logic:equal>
					<logic:equal value="update" name="element" property="string(state)">
						<IMG border=0 src="/images/muli_view.gif" onclick="multimediahref('<bean:write name="element" property="string(a0100)"/>','<bean:write name="element" property="string(i9999)"/>','<bean:write name="element" property="string(id)"/>','<bean:write name="element" property="string(state)"/>');">
					</logic:equal>
					<logic:equal value="insert" name="element" property="string(state)">
						<IMG border=0 src="/images/muli_view.gif" onclick="multimediahref('<bean:write name="element" property="string(a0100)"/>','<bean:write name="element" property="string(i9999)"/>','<bean:write name="element" property="string(id)"/>','<bean:write name="element" property="string(state)"/>');">
					</logic:equal>
				</td>
			   </logic:equal>
			<!-- 附件按钮 结束-->
              <td align="center" class="RecordRow" nowrap>
                		<logic:notEqual value="new" name="element" property="string(state)">
							<logic:notEqual value="update" name="element" property="string(state)">
								<logic:notEqual value="delete" name="element" property="string(state)">
									<logic:notEqual value="insert" name="element" property="string(state)">
										<logic:notEqual value="approve" name="element" property="string(state)">
	           								<img src="/images/edit.gif" border=0 onclick="cedit(${i9})">
	           							</logic:notEqual>	            
									</logic:notEqual>
								</logic:notEqual>
							</logic:notEqual>
						</logic:notEqual>
						<logic:equal value="new" name="element" property="string(state)">
							<img src="/images/edit.gif" border=0 onclick="appcedit('<bean:write name="element" property="string(a0100)"/>','<bean:write name="element" property="string(state)"/>','<bean:write name="element" property="string(i9999)"/>','<bean:write name="element" property="string(id)"/>')"/>
						</logic:equal>
						<logic:equal value="update" name="element" property="string(state)">
							<img src="/images/edit.gif" border=0 onclick="appcedit('<bean:write name="element" property="string(a0100)"/>','<bean:write name="element" property="string(state)"/>','<bean:write name="element" property="string(i9999)"/>','<bean:write name="element" property="string(id)"/>')">
						</logic:equal>
						<logic:equal value="insert" name="element" property="string(state)">
							<img src="/images/edit.gif" border=0 onclick="appcedit('<bean:write name="element" property="string(a0100)"/>','<bean:write name="element" property="string(state)"/>','<bean:write name="element" property="string(i9999)"/>','<bean:write name="element" property="string(id)"/>')">
						</logic:equal>
						<logic:equal value="delete" name="element" property="string(state)">
							<img src="/images/edit.gif" border=0>
						</logic:equal>
	      </td>
	   </logic:equal>
	      
	      
            </logic:greaterThan>
            </logic:equal>	      
	           <logic:iterate id="info"    name="selfInfoForm"  property="infoFieldList">            
               <logic:equal  name="info" property="itemtype" value="A">  
               	<logic:equal  name="info" property="codesetid" value="0">   
                	<td align="left" class="RecordRow" nowrap>  
                 		<bean:write  name="element" property="string(${info.itemid})" filter="true"/>
                	</td>
              	</logic:equal>
              	<logic:notEqual  name="info" property="codesetid" value="0">  
              		<bean:define id="viemvalue" name="element" property="string(${info.itemid})"/> 
                	<td align="left" class="RecordRow" nowrap>  
                 		${viemvalue}
                	</td>
              	</logic:notEqual>
              </logic:equal>
              <logic:equal  name="info" property="itemtype" value="D">               
                <td align="left" class="RecordRow" nowrap>  
                 <bean:write  name="element" property="string(${info.itemid})" filter="true"/>
                </td>   
              </logic:equal>   
              <logic:equal  name="info" property="itemtype" value="N">               
                <td align="right" class="RecordRow" nowrap>   
                  <bean:write  name="element" property="string(${info.itemid})" filter="true"/>
                </td>     
              </logic:equal>   
               <logic:equal  name="info" property="itemtype" value="M">  
               <%
                 FieldItem item=(FieldItem)pageContext.getAttribute("info");
                 String tx=vo.getString(item.getItemid());
               %> 
               <hrms:showitemmemo showtext="showtext" itemtype="M" setname="${selfInfoForm.setname}" tiptext="tiptext" text="<%=tx%>"></hrms:showitemmemo>                          
                <td align="left" ${tiptext} class="RecordRow" nowrap>   
                   ${showtext}
                </td>     
              </logic:equal>
             
             </logic:iterate> 
             
				<td align="left" class="RecordRow" nowrap>  
						<logic:equal value="new" name="element" property="string(state)">新增</logic:equal>
						<logic:equal value="update" name="element" property="string(state)">更新</logic:equal>
						<logic:equal value="delete" name="element" property="string(state)">删除</logic:equal>
						<logic:equal value="insert" name="element" property="string(state)">插入</logic:equal>
						<logic:equal value="approve" name="element" property="string(state)">报批</logic:equal>
						<logic:notEqual value="new" name="element" property="string(state)">
							<logic:notEqual value="update" name="element" property="string(state)">
								<logic:notEqual value="delete" name="element" property="string(state)">
									<logic:notEqual value="insert" name="element" property="string(state)">
										<logic:notEqual value="approve" name="element" property="string(state)">
										已批	
										</logic:notEqual>		
									</logic:notEqual>
								</logic:notEqual>
							</logic:notEqual>
						</logic:notEqual>
					
                </td>
		               	    		        	        	        
          </tr>
        </hrms:extenditerate>
         
</table>
<table  width="100%" align="center" class="RecordRowP" style="MARGIN-LEFT: 5px;">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		    <!--<logic:equal name="selfInfoForm" property="setprv" value="2">
		    <bean:message key='label.query.selectall'/><input type="checkbox" name="sfull" onclick="full();">
		    </logic:equal>-->
		    		<bean:message key="label.page.serial"/>
					<bean:write name="selfInfoForm" property="page.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="selfInfoForm" property="page.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="selfInfoForm" property="page.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="selfInfoForm" property="page.pagination"
				nameId="page" propertyId="roleListProperty">
				</hrms:paginationlink></p>
			</td>
			
		</tr>
</table>
<table width="70%" align="left" cellspacing="0" cellpadding="0">
<tr><td height="5px"></td></tr>
          <tr>
            <td align="left" style="PADDING-LEFT: 5px;">
             <html:hidden name="selfInfoForm" property="actiontype" value="new"/>
             <logic:equal name="selfInfoForm" property="setprv" value="2">              
	 	       <logic:equal value="1" name="selfInfoForm" property="isAppEdite">
	 	       <logic:equal value="1" name="selfInfoForm" property="isAble">
	 	       <hrms:priv func_id="01030115">
	 	       <input type="button" class="mybutton" name="zheng" value="整体报批" onclick="approveall();"/>&nbsp;
	 	       </hrms:priv>
	 	       <hrms:priv func_id="01030104">
	 	       <button name="apd" class="mybutton" onclick="proveAll('<bean:write name="selfInfoForm" property="setname"/>');"><bean:message key="button.appeal"/></button>&nbsp;
              	</hrms:priv>
	 	       <button name="apd" class="mybutton" onclick="bedit();"><bean:message key="button.insert"/></button>&nbsp;
	 	<button name="apdd" class="mybutton" onclick="cdel();"> <bean:message key="button.delete"/></button>&nbsp;
	 	<button name="back" class="mybutton" onclick="backdel('<bean:write name="selfInfoForm" property="setname"/>');"> <bean:message key="selfinfo.backdel"/></button>&nbsp;
	 	</logic:equal>
	 	<button class="mybutton" onclick="exReturn();">返回</button>&nbsp;
	 	</logic:equal>
	 	<logic:notEqual value="1" name="selfInfoForm" property="isAppEdite">
	 		<button name="sf" class="mybutton" onclick="appEdite('<bean:write name="selfInfoForm" property="setname"/>');"><bean:message key="selfinfo.defend"/></button>&nbsp;
	 	</logic:notEqual>
	 	<logic:equal value="1" name="selfInfoForm" property="approveflag">
	 	<hrms:priv func_id="01030104">
	    <logic:notEqual value="1" name="selfInfoForm" property="inputchinfor">
	    	<button name="apd" class="mybutton" onclick="prove();"><bean:message key="button.appeal"/></button>&nbsp;
	    </logic:notEqual>
	    </hrms:priv>
	    </logic:equal>
	    </logic:equal>  
	    <logic:notEqual value="1" name="selfInfoForm" property="isAppEdite">
	    <logic:notEqual  name="selfInfoForm" property="emp_cardId" value="-1">               
                    &nbsp;&nbsp;<button name="apd" class="mybutton" onclick="ykcard();"><bean:message key='sys.res.card'/></button>     
            </logic:notEqual>
	    </logic:notEqual>
	       
            </td>
          </tr>          
 </table>
</html:form>
<%
	String a01001 = (String)request.getParameter("a0100");
	if(a01001==null){
%>
<logic:equal value="02" name="selfInfoForm" property="checksave">
<script language="javascript">
alert(LAST_INFOR_NOTUPDATE_APP+"!");
</script>
</logic:equal>
<%}%>
<script type="text/javascript">
function prove(){
if(confirm(APP_DATA_NOT_UPDATE+"?")){
selfInfoForm.action="/selfservice/selfinfo/searchselfdetailinfo.do?b_detailappeal=link";
selfInfoForm.submit();
}else{
return;
}
}
function backdel(fieldsetid){
if (confirm("该操作只对已批数据的删除操作进行撤销！您确定要撤销删除吗？"))
selfInfoForm.action="/selfservice/selfinfo/appEditselfinfo.do?b_search=backdel&setname="+fieldsetid+"&flag=infoself&isAppEdite=1";
selfInfoForm.submit();
}

function proveAll(fieldsetid){
if(confirm("数据一旦报批，将不能进行修改，是否报批？")){
selfInfoForm.action="/selfservice/selfinfo/appEditselfinfo.do?b_search=prove&setname="+fieldsetid+"&flag=infoself&isAppEdite=1";
selfInfoForm.submit();
}else{
return;
}

}
function cdel(){
if(confirm(CONFIRMATION_DEL)){
selfInfoForm.action="/selfservice/selfinfo/searchselfdetailinfo.do?b_appdelete=link&isAppEdite=1";
selfInfoForm.submit();
}else{
return;
}
}
function getcheckbox(){
	var tablevos=document.getElementsByTagName("INPUT");
		var flag=false;
      	for(var i=0;i<tablevos.length;i++)
      	{
      		if(tablevos[i].type=="checkbox"&&tablevos[i].value=="on")
      		{
      		  flag= true;
      		  return flag;
      		}
      	}
      	return flag;

}

  function capp(i9999){
  
   if(!confirm(APPLIC_OK+'？')){
   
  		return;
   }else{
   selfInfoForm.action="/selfservice/selfinfo/editselfinfo.do?b_apps=link&a0100=${selfInfoForm.a0100}&i9999="+i9999+"&actiontype=update";
   selfInfoForm.submit();
   
   }
  }

  function cedit(i9999){
  
  
   selfInfoForm.action="/selfservice/selfinfo/editselfinfo.do?b_edit=edit&a0100=${selfInfoForm.a0100}&i9999="+i9999+"&actiontype=update";
   selfInfoForm.submit();
   
  
  }
  
  function appcedit(keyvalue, type, sequence,chg_id){
  
  
   selfInfoForm.action="/selfservice/selfinfo/appEditselfinfo.do?b_editother=link&a0100=${selfInfoForm.a0100}&keyvalue="+keyvalue+"&type="+type+"&sequence="+sequence+"&isDraft=1&chg_id="+chg_id;
   selfInfoForm.submit();
   
  
  }
  function cadd(i9999){
  
  	selfInfoForm.action="/selfservice/selfinfo/editselfinfo.do?b_edit=edit&a0100=${selfInfoForm.a0100}&i9999="+i9999+"&actiontype=new&insert=1";
    selfInfoForm.submit();
  
  }

function full(){ 
  			for(var i=0;i<document.forms[0].elements.length;i++)
				{			
					if(document.forms[0].elements[i].type=='checkbox')
			   		{	
						document.forms[0].elements[i].checked =selfInfoForm.sfull.checked;
					}
				}
		}
function appEdite(fieldsetid) {
	selfInfoForm.action="/selfservice/selfinfo/appEditselfinfo.do?b_defendother=search&setname="+fieldsetid+"&flag=infoself&isAppEdite=1&current=1";
    selfInfoForm.submit();
}

function approveall() {
	if (confirm("您确定要整体报批吗？整体报批后个人信息将不能修改！")) {	
		selfInfoForm.action = "/selfservice/selfinfo/appEditselfinfo.do?b_approveall=link&savEdit=appbaopi&a0100=${selfInfoForm.a0100}&userbase=${selfInfoForm.userbase}";
		selfInfoForm.target="mil_body";
		selfInfoForm.submit();
	}
}
</script>
<logic:notEmpty name="selfInfoForm" property="formationMsg">
	<script language='javascript'>
	  alert('<bean:write  name="selfInfoForm" property="formationMsg"/>');
	</script>
	<%
	  SelfInfoForm selfInfoForm = (SelfInfoForm) session.getAttribute("selfInfoForm");
	  selfInfoForm.setFormationMsg("");
	%>

</logic:notEmpty> 
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  