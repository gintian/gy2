<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
<%@ page import="com.hjsj.hrms.actionform.selfinfomation.SelfInfoForm" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag="";
    if(userView!=null){
    	bosflag = userView.getBosflag();
    }
%>
<%
	  int i=0;
%>
<script language="javascript">
    function exeButtonAction(actionStr,target_str)
   {
       target_url=actionStr;
       window.open(target_url,target_str); 
   }
   function OrderRecord()
   {
        var ordernum=showModalDialog('orderrecordnum.jsp','glWin','dialogHeight:200px;dialogWidth:300px;center:yes;help:no;resizable:no;status:no;'); 

        if(ordernum!=null && ordernum.length>0)
        {
          selfInfoForm.action="/workbench/info/searchselfdetailinfo.do?b_moveorder=order&ordernum=" + ordernum ;
          selfInfoForm.submit(); 
        }
   }  
   function winhref(url)
{
   if(url=="")
      return false;
   selfInfoForm.action=url;
   selfInfoForm.target="mil_body";
   selfInfoForm.submit();
}  
function trun()
   {
      parent.parent.menupnl.toggleCollapse(true);
   }
   
   function change() {
 	var list = document.getElementById("list");
 	va = list.value;
 	if (va == "A01") {
 		selfInfoForm.action = "/workbench/info/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname=A01&isAppEdite=1";
 	} else if(va == "A00"){
 		selfInfoForm.action = "/workbench/media/searchmediainfolist.do?b_search=link&setname="+va+"&setprv=2&flag=notself&returnvalue=${selfInfoForm.returnvalue}&userbase=<bean:write name="selfInfoForm" property="userbase"/>&isAppEdite=1";
 	}else {
 	
 	selfInfoForm.action = "/workbench/info/searchselfdetailinfo.do?b_searchsort=search&setname="+va+"&flag=noself&isAppEdite=1";
 	}
 	selfInfoForm.target="mil_body";
	selfInfoForm.submit();
} 
function upItem(rowid,a0100,i9999){
	var _table=document.getElementById("tableid");	
	var _rowid=parseInt(rowid);	
	if(_table.rows.length<2||_rowid<1){
		return;
	}
	var hashvo=new ParameterSet();  
	hashvo.setValue("rowid", _rowid);      
    hashvo.setValue("nbase", '${selfInfoForm.userbase}');
	hashvo.setValue("a0100", a0100);
	hashvo.setValue("setname", '${selfInfoForm.setname}');
    hashvo.setValue("i9999", i9999);
    hashvo.setValue("type", 'up');
    var request=new Request({method:'post',onSuccess:upItemview,functionId:'1101100035'},hashvo);
}

function upItemview(outparamters){
	/*
	var rowid=parseInt(outparamters.getValue("rowid"));
	var infomsg=outparamters.getValue("infomsg");
	if ((infomsg!=null) &&(infomsg!="")){
	  alert(infomsg);
	}
	var _rowid=rowid;
	var _table=$("tableid");
	var _row1=_table.rows[rowid];
	var _row2=_table.rows[rowid+1];
	var tempclass=_row1.className;
	_row1.className=_row2.className;
	_row2.className=tempclass;
	if(_table.rows.length<2||_rowid<=1){
		var _cell1=_row1.cells[_row1.cells.length-1].innerHTML;		
		var _cell2=_row2.cells[_row2.cells.length-1].innerHTML;		
		_row1.cells[_row1.cells.length-1].innerHTML=_cell2;
		_row2.cells[_row2.cells.length-1].innerHTML=_cell1;
	}else{
		var _cell1=_row1.cells[_row1.cells.length-1].innerHTML;
		var _cell2=_row2.cells[_row2.cells.length-1].innerHTML;
		_row1.cells[_row1.cells.length-1].innerHTML=_cell2;
		_row2.cells[_row2.cells.length-1].innerHTML=_cell1;
	}
	_table.moveRow(_rowid+1,_rowid);
	*/
	
	selfInfoForm.action="/workbench/info/searchselfdetailinfo.do?b_searchsort=refresh";
	selfInfoForm.submit();
	
	//window.location.reload();
}
function downItem(rowid,a0100,i9999){
	var _table=$("tableid");
	var _rowid=parseInt(rowid);
	if(_table.rows.length<3||(_rowid+2)==_table.rows.length){
		return;
	}	
	var hashvo=new ParameterSet();          
   	hashvo.setValue("rowid", _rowid);          
    hashvo.setValue("setname", '${selfInfoForm.setname}');
    hashvo.setValue("nbase", '${selfInfoForm.userbase}');
    hashvo.setValue("a0100", a0100);
    hashvo.setValue("i9999", i9999);
    hashvo.setValue("type", 'down');
    var request=new Request({method:'post',onSuccess:downItemview,functionId:'1101100035'},hashvo);
}

function downItemview(outparamters){
	/*
	var rowid=parseInt(outparamters.getValue("rowid"));
	var infomsg=outparamters.getValue("infomsg");
	if ((infomsg!=null) &&(infomsg!="")){
	  alert(infomsg);
	}
	
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
	*/
	selfInfoForm.action="/workbench/info/searchselfdetailinfo.do?b_searchsort=refresh";
	selfInfoForm.submit();
	
	//window.location.reload();
}
function multimediahref(setprv,dbname,a0100,i9999){
	var result=false;
	if(setprv==2)
	{
		result=true;
	}else{
		result=false;
	}
	var thecodeurl =""; 
	var return_vo=null;
	var setname = "${selfInfoForm.setname}";
	var dw=800,dh=500,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
  	thecodeurl="/general/inform/multimedia/multimedia_tree.do?b_query=link&setid="+setname+"&a0100="+a0100+"&nbase="+dbname+"&i9999="+i9999+"&dbflag=A&canedit="+result;
  	 if(getBrowseVersion()){//update by xiegh on date 20180316 bug35665
		  	return_vo= window.showModalDialog(thecodeurl, "", 
  		    "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:800px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no");
  	 }else{
  		var iTop = (window.screen.availHeight - 30 - dh) / 2;  //获得窗口的垂直位置
		var iLeft = (window.screen.availWidth - 10 - dw) / 2; //获得窗口的水平位置 
  		window.open(thecodeurl,"","width="+dw+",height="+dh+",resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
  	 }
}

function checkSelect() {
	var inputs=document.getElementsByTagName("input");
	var flag = false;
	for(var i = 0; i < inputs.length; i++) {
		if(inputs[i].checked) {
			flag = true;
			break;
		}
	}

	if(flag)
		return ifdelinfo();
	else
		alert(CHOISE_DELETE_NOT);
}

window.onresize = function(){
	setDivStyle();
}

function setDivStyle(){
	document.getElementById("dataBox").style.height = document.body.clientHeight-130;
    if(!getBrowseVersion()) {
    	document.getElementById("dataBox").style.width = document.body.clientWidth-15; 
    	document.getElementById("pageDiv").style.width = document.body.clientWidth-12; 
    } else if(!isCompatibleIE()) {
    	document.getElementById("dataBox").style.width = document.body.clientWidth-10; 
    	document.getElementById("pageDiv").style.width = document.body.clientWidth-24;
    }
}

</script>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  
<hrms:themes />
<style>
.TableRow_left{
	BORDER-TOP: 0pt solid;
}
</style>
<html:form action="/workbench/info/searchselfdetailinfo">
<html:hidden property="setname" name="selfInfoForm"/>
<html:hidden property="userbase" name="selfInfoForm"/>
<html:hidden property="a0100" name="selfInfoForm"/>
	<logic:equal value="1" name="selfInfoForm" property="isBrowse">
		<logic:equal name="selfInfoForm" property="setprv" value="2">
			<div style="margin-left:0px;margin-bottom:5px;">
				<bean:message key="selfinfo.listinfo"/>
					<select id="list" name="fieldsetid" onchange="change();">
						<logic:iterate id="setList" name="browseForm" property="infosetlist">
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
<%if("hcm".equals(bosflag)){ %>
	<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1">
<%}else{ %>
	<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" style="margin-top: 8px">
<%} %>
    <tr>
           <td align="left"  nowrap>
                (<bean:message key="label.title.org"/>: <bean:write  name="selfInfoForm" property="b0110" filter="true"/>&nbsp;
                <bean:message key="label.title.dept"/>: <bean:write  name="selfInfoForm" property="e0122" filter="true"/>&nbsp;
                <bean:message key="label.title.name"/>: <bean:write  name="selfInfoForm" property="a0101" filter="true"/>&nbsp;
                 )
              </td>
          </tr>
</table>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">

   <tr>
     <td>
<div id="dataBox" class="fixedDiv2" >
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" id="tableid">
   	  <thead>
           <tr>
           <logic:notEqual value="${selfInfoForm.setname}" name="selfInfoForm" property="virAxx">
            <logic:equal name="selfInfoForm" property="setprv" value="2">
             <td width="5%" align="center" class="TableRow_top" nowrap>
               <input type="checkbox" name="selbox" onclick="batch_select(this,'selfInfoForm.select');" title='<bean:message key="label.query.selectall"/>'>
             </td>
             
             <td width="5%" align="center" class="TableRow_left" nowrap>
              <bean:message key="button.new.insert"/> 
             </td>
             </logic:equal>
           
               <td width="5%" align="center" class="TableRow_left" nowrap>
		  <bean:message key="label.edit"/>            	
               </td> 
               </logic:notEqual>
            <logic:equal name="selfInfoForm" property="multimedia_file_flag" value="1">
	 		      <td width="5%" align="center" class="TableRow_left" nowrap>
					<bean:message key="conlumn.resource_list.name"/>             	
				  </td> 
			  </logic:equal>
            <logic:iterate id="element"    name="selfInfoForm"  property="infoFieldList"> 
              <td align="center" height="22" class="TableRow_left" nowrap>
                   <hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>
              </td>
             </logic:iterate>  
             	 	        	        
 		      <td width="5%" align="center" class="TableRow_left" nowrap>
				<bean:message key="label.zp_exam.sort"/>             	
			  </td> 	 	        	        
           </tr>
   	  </thead>
   	       <%
           		SelfInfoForm selfInfoForm=(SelfInfoForm)session.getAttribute("selfInfoForm");
           		int pagerows = selfInfoForm.getPagerows();
           		int currpage = selfInfoForm.getSelfInfoForm().getPagination().getCurrent();
           		int counts = selfInfoForm.getSelfInfoForm().getAllList().size();
           		int len =0;
           		if(currpage==1)
           			len = pagerows*currpage>counts?counts:pagerows;
           		else
           			len = pagerows*currpage>counts?counts-pagerows*(currpage-1):pagerows;
            %>
          <hrms:extenditerate id="element" name="selfInfoForm" property="selfInfoForm.list" indexes="indexes"  pagination="selfInfoForm.pagination" pageCount="${selfInfoForm.pagerows}" scope="session">
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
          <logic:notEqual value="${selfInfoForm.setname}" name="selfInfoForm" property="virAxx">
          <logic:equal name="selfInfoForm" property="setprv" value="2"> 
            <td align="center" class="RecordRow_right" nowrap>
               <hrms:checkmultibox name="selfInfoForm" property="selfInfoForm.select" value="true" indexes="indexes"/>
            </td>
            
            <td align="center" class="RecordRow" nowrap>
            	<a href="###" onclick="winhref('/workbench/info/editselfinfo.do?b_edit=edit&a0100=${selfInfoForm.a0100}&i9999=<bean:write  name="element" property="string(i9999)" filter="true"/>&actiontype=new&insert=1');"><img src="/images/goto_input.gif" border=0></a>
	      </td>
           </logic:equal> 
              <td align="center" class="RecordRow" nowrap>
            	<a href="###"  onclick="winhref('/workbench/info/editselfinfo.do?b_edit=edit&a0100=${selfInfoForm.a0100}&i9999=<bean:write  name="element" property="string(i9999)" filter="true"/>&actiontype=update');"><img src="/images/edit.gif" border=0></a>
	      </td>
	      </logic:notEqual>
             <logic:equal name="selfInfoForm" property="multimedia_file_flag" value="1">
		      	 <logic:equal name="selfInfoForm" property="setprv" value="2">
	             	<td align="center" class="RecordRow" nowrap>
	            		<a href="###"  onclick="multimediahref('${selfInfoForm.setprv}','${selfInfoForm.userbase}','${selfInfoForm.a0100}','<bean:write  name="element" property="string(i9999)" filter="true"/>');"><img src="/images/muli_view.gif" border=0></a>
		      		</td>
		      	 </logic:equal>
	             <logic:notEqual name="selfInfoForm" property="setprv" value="2">
	             	<td align="center" class="RecordRow" nowrap>
	            		<a href="###"  onclick="multimediahref('${selfInfoForm.setprv}','${selfInfoForm.userbase}','${selfInfoForm.a0100}','<bean:write  name="element" property="string(i9999)" filter="true"/>');"><img src="/images/muli_view.gif" border=0></a>
		      		</td>
		      	 </logic:notEqual>
	      	</logic:equal>
            <logic:iterate id="info"    name="selfInfoForm"  property="infoFieldList"> 
               <logic:notEqual  name="info" property="itemtype" value="N">    
                <logic:notEqual  name="info" property="itemtype" value="M">               
                   <td align="left" class="RecordRow" nowrap>   
                    <bean:write  name="element" property="string(${info.itemid})" filter="true"/>
                 
                   </td>     
               </logic:notEqual>
             </logic:notEqual>
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
                <td align="left" class="RecordRow" ${tiptext} nowrap>   
                
                 ${showtext}
               </td>  
              </logic:equal> 
             </logic:iterate>
             
           	 <td align="center" class="RecordRow_left"  nowrap>
                 	<%if(i!=1){ %>
					&nbsp;<a href="javaScript:upItem('<%=(i-1)%>','<%=vo.getString("a0100") %>','<bean:write  name="element" property="string(i9999)" filter="true"/>')">
					<img src="/images/up01.gif" width="12" height="17" border=0></a> 
					<%}else{ %>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<%} %>
				    <%if(len==i){ %>
				    	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				    <%}else{ %>
					&nbsp;<a href="javaScript:downItem('<%=(i-1)%>','<%=vo.getString("a0100") %>','<bean:write  name="element" property="string(i9999)" filter="true"/>')">
					<img src="/images/down01.gif" width="12" height="17" border=0></a> &nbsp;
					<%} %>
			</td>		        	        	        
          </tr>
        </hrms:extenditerate>
          </table>
     </div>
     </td>
   </tr>
   <tr><td>
   <div style="height:35;border:0" id="pageDiv" class="fixedDiv2" >
	<table align="left" class="RecordRowP" style="width:100%;">
			<tr>
			    <td class="tdFontcolor">
			    <hrms:paginationtag name="selfInfoForm" pagerows="${selfInfoForm.pagerows}" property="selfInfoForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
				</td>
		               <td align="right" nowrap class="tdFontcolor">
			          <p align="right"><hrms:paginationlink name="selfInfoForm" property="selfInfoForm.pagination"
					nameId="selfInfoForm" propertyId="roleListProperty">
					</hrms:paginationlink>
				</td>
			</tr>
	</table>
	</div>
   </td></tr>	  
         
</table>
<table  width="70%" align="left" border="0" cellspacing="0" cellpadding="0">
          <tr>
          	<td height="5px"></td>
          </tr>
          <tr>
            <td align="left">
               <html:hidden name="selfInfoForm" property="tolastpageflagsub"/>
                  <html:hidden name="selfInfoForm" property="tolastpageflag"/>
               <html:hidden name="selfInfoForm" property="actiontype" value="new"/>
              <logic:equal name="selfInfoForm" property="setprv" value="2">
               <logic:notEqual name="selfInfoForm" property="a0100" value="su">
                <logic:notEqual name="selfInfoForm" property="a0100" value="A0100">
	 	           <logic:notEqual value="${selfInfoForm.setname}" name="selfInfoForm" property="virAxx">      
               	   <hrms:submit styleClass="mybutton" property="b_edit">
            		<bean:message key="button.insert"/>
	 	           </hrms:submit>  	 	    
	         	   <hrms:submit styleClass="mybutton" property="b_delete" onclick="return checkSelect()">
	            		 <bean:message key="button.delete"/>
		 	   	   </hrms:submit> 	
	 	   		   </logic:notEqual> 	  
	 	<!--  <hrms:submit styleClass="mybutton" property="b_insert">
            		<bean:message key="button.new.insert"/>
	 	   </hrms:submit> -->
	 	  <!-- <input type="button" name="returnbutton"  value="<bean:message key="button.movenextpre"/>" class="mybutton" onclick="OrderRecord()"> -->
	       </logic:notEqual>
	      </logic:notEqual>
	     </logic:equal>
	     <logic:notEqual name="selfInfoForm" property="writeable" value="1">
		     <%String checkOk = "";%>
			<logic:equal value="1" name="selfInfoForm" property="generalsearch"><!-- zgd 2014-7-1 高级查询后，人员信息中主集和子集点击返回，正常返回需执行查询。条件为check=ok -->
				<%checkOk = "&check=ok";%>
			</logic:equal>
			<logic:notEqual value="1" name="selfInfoForm" property="generalsearch">
				<%checkOk = "";%>
			</logic:notEqual>
	       <logic:equal name="selfInfoForm" property="returnvalue" value="1">
	       		<logic:equal value="1" name="selfInfoForm" property="isBrowse">
	 	      		<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/workbench/browse/showselfinfodetail.do?b_search=link&setname=${selfInfoForm.setname}&a0100=${selfInfoForm.a0100 }','mil_body')">
	 	      	</logic:equal>
	 	      	<logic:notEqual value="1" name="selfInfoForm" property="isBrowse">
	 	      		<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/workbench/info/editselfinfo.do?b_return=link','il_body')"> 
	 	    	</logic:notEqual>
	 	    </logic:equal>
	 	      <logic:equal name="selfInfoForm" property="returnvalue" value="2">
	 	      	<logic:equal value="1" name="selfInfoForm" property="isBrowse">
	 	      		<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/workbench/browse/showselfinfodetail.do?b_search=link&setname=${selfInfoForm.setname}&a0100=${selfInfoForm.a0100 }','mil_body')">
	 	      	</logic:equal>
	 	      	<logic:notEqual value="1" name="selfInfoForm" property="isBrowse">
	 	      		<logic:equal value="1" name="selfInfoForm" property="returns">
	 	      		<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/workbench/info/showinfodata.do?b_queryinfo=link&returnvalue=dxt<%=checkOk %>','nil_body')"> 
	 	      		</logic:equal>
	 	      		<logic:notEqual value="1" name="selfInfoForm" property="returns">
	 	      			<logic:equal value="1" name="selfInfoForm" property="isAdvance">
	 	      				<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/workbench/info/showinfodata.do?b_queryinfo=link&check=ok','nil_body')"> 
	 	      			</logic:equal>
	 	      			<logic:notEqual value="1" name="selfInfoForm" property="isAdvance">
		 	      				<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/workbench/info/showinfodata.do?b_queryinfo=link<%=checkOk %>','nil_body')"> 
		 	      				<!-- <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/workbench/info/showinfodata.do?b_queryinfo=link&check=no','nil_body')">  -->
	 	      			</logic:notEqual>
	 	      		</logic:notEqual>
				</logic:notEqual>
			</logic:equal>
			<logic:equal name="selfInfoForm" property="returnvalue" value="64"><!-- 党组织人员管理 -->
                               <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/dtgh/party/person/searchbusinesslist.do?b_search=link&politics=${selfInfoForm.politics }&param=Y&a_code=${selfInfoForm.a_code }','mil_body')">                 
                        </logic:equal>
                        <logic:equal name="selfInfoForm" property="returnvalue" value="65"><!-- 团组织人员管理 -->
                               <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/dtgh/party/person/searchbusinesslist.do?b_search=link&politics=${selfInfoForm.politics }&param=V&a_code=${selfInfoForm.a_code }','mil_body')">                 
                        </logic:equal>
		    <logic:equal name="selfInfoForm" property="returnvalue" value="74">
                 <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/system/warn/result_manager.do?b_query2=link','il_body');trun();">                 
            </logic:equal>
            <logic:equal name="selfInfoForm" property="returnvalue" value="73">	
                 <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/system/warn/result_manager.do?b_query2=link','i_body')">                 
            </logic:equal>
            <logic:equal name="selfInfoForm" property="returnvalue" value="75">
                 <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/system/warn/result_manager.do?b_query2=link','il_body')">                 
            </logic:equal>
	 	 </logic:notEqual>
	 	 <!-- <a href="/workbench/info/editselfinfo.do?br_return=link" target="il_body"><bean:message key="button.return"/></a>--> 
           <logic:equal name="selfInfoForm" property="writeable" value="1">
					<input type="button" name="returnbutton" value='<bean:message key="button.return"/>' class="mybutton" onclick="window.close();">
						
		</logic:equal>
         
            </td>
          </tr>          
 </table>
</html:form>
<script language='javascript'>
<logic:notEmpty name="selfInfoForm" property="msg">
alert('<bean:write  name="selfInfoForm" property="msg"/>');
</logic:notEmpty> 
setDivStyle();
</script>