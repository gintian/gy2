<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.ArrayList"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.hjsj.sys.FieldSet"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.hjsj.hrms.interfaces.webservice.SysoutSyncInterf"%>
<%@page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<style>
<!--
  .tablealign td{ padding: 0px,5px,0px,5px;}
  
-->
</style>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>
<script>

var buttonshow="none";
   // 信息隐藏和显示
    function showORhidden(index,setid){ 
         var state = document.getElementById("table"+index).style.display;
         if(state == "none"){
        	 document.getElementById("table"+index).style.display="block";
             document.getElementById("flag"+index).value="Y";
             document.getElementById("arrow"+index).innerHTML="<img src='/images/show.gif' >";
        	 if(document.getElementById("Media"+index))
        		 document.getElementById("Media"+index).style.display="block";
			 //如果展开的信息集没有数据则去后查询
 			 if(!document.getElementById("data"+index)){
	 	         var hashvo=new ParameterSet();
		 		 hashvo.setValue("index",index);
		 		 hashvo.setValue("chgId",'${approvePersonForm.chg_id}');
		 		 hashvo.setValue("fieldSetId",setid);
		 		 
		     	 var request=new Request({method:'post',asynchronous:false,onSuccess:showData,functionId:'0580010012'},hashvo);
 			 }	
         } else{
        	 document.getElementById("table"+index).style.display="none";
             document.getElementById("flag"+index).value="N";
             document.getElementById("arrow"+index).innerHTML="<img src='/images/hidden.gif' >";
             if(document.getElementById("Media"+index))
        		 document.getElementById("Media"+index).style.display="none";
         }

    }
    //加载信息集数据
    function showData(param) {
 		var index=param.getValue("index");
		var itemList=param.getValue("itemList");
		var columns=param.getValue("columns");
		var fieldSetId=param.getValue("fieldSetId");
		var setid=param.getValue("setId");
		var multimedia=param.getValue("multimedia");
		var isMainSet = param.getValue("isMainSet");
		var multimedialist = param.getValue("multimedialist");
		var tr=document.getElementById("table"+index);
		var table=document.createElement("table");
		table.id="data"+index;
		table.style.width="auto";
		table.style.border=0;
		table.style.display="block";
		table.className="ListTable1";
		
		if("true" != isMainSet) {
			var headRow=table.insertRow(0);
			var stateCell=headRow.insertCell(0);
			stateCell.colSpan="2";
			stateCell.style.textAlign="center";
			stateCell.className="TableRow";
			stateCell.style.whiteSpace = "nowrap";
			stateCell.innerHTML="状态";
			
			var i=1;
    		for(var key in columns){
    			var cell=headRow.insertCell(i);
      			cell.style.height="36";
    			cell.style.textAlign="center";
    			cell.className="TableRow";
    			cell.style.whiteSpace = "nowrap";
    			cell.innerHTML=columns[key].itemdesc;
    			i++;
        	}

    		if("true" == multimedia) {
    			var mediaCell=headRow.insertCell(i);
    			mediaCell.className="TableRow";
    			mediaCell.style.whiteSpace = "nowrap";
    			mediaCell.style.textAlign="center";
    			mediaCell.innerHTML="附件";
    			i++;
    		}
			var operateCell=headRow.insertCell(i);
			operateCell.className="TableRow";
			operateCell.style.whiteSpace = "nowrap";
			operateCell.style.textAlign="center";
			operateCell.innerHTML="操作";

             if(itemList){
                var rowNum = 1
	        	for(var m = 0; m < itemList.length; m++){
	    			var dataRow=table.insertRow(rowNum);
					var data=itemList[m];

	    			if("update" == data.type){
						var dataStateCell=dataRow.insertCell(0);
		    			dataStateCell.rowSpan="2";
		    			dataStateCell.className="RecordRow";
		    			dataStateCell.style.whiteSpace = "nowrap";
			    		dataStateCell.style.textAlign="center";
			    		dataStateCell.innerHTML="修改";

						var oldCell=dataRow.insertCell(1);
						oldCell.className="RecordRow";
						oldCell.style.whiteSpace = "nowrap";
						oldCell.style.textAlign="center";
			    		oldCell.innerHTML="变动前";
			    		i = 2;
			    		for(var key in columns){
			    			var cell=dataRow.insertCell(i);
			      			cell.style.height="36";
			    			cell.className="RecordRow";
			    			cell.style.whiteSpace = "nowrap";
			    			var value=data[columns[key].itemid];
			    			if(value) {
				    			if("Y" == value.changeflag)
					    			cell.style.backgroundColor = "#CCCCCC";
	
				    			if("N" == columns[key].itemtype)
					    			cell.style.textAlign="right";
				    			else
					    			cell.style.textAlign="left";
	
				    			if("M" == columns[key].itemtype) {
					    			var tipValue = value.tipOldValue;
					    			if(tipValue){
					    				var span = "<span onmouseout=\"UnTip()\" onmouseover=\"Tip('" + tipValue + "',STICKY,true)\">";
						    			span += value.oldvalue + "</span>";
					    				cell.innerHTML= span;
						    		}
					    		} else
						    		cell.innerHTML= value.oldvalue;
			    			}
				    			
			    			i++;
			        	}
			        	
						rowNum ++;
			    		var newDataRow=table.insertRow(rowNum);
						var newCell=newDataRow.insertCell(0);
						newCell.className="RecordRow";
						newCell.style.whiteSpace = "nowrap";
						newCell.style.textAlign="center";
			    		newCell.innerHTML="变动后";
						i = 1;
						for(var key in columns){
			    			var cell=newDataRow.insertCell(i);
			    			if("N" == columns[key].itemtype)
				    			cell.style.textAlign="right";
			    			else
				    			cell.style.textAlign="left";
			    			
			      			cell.style.height="36";
			    			cell.className="RecordRow";
			    			cell.style.whiteSpace = "nowrap";
			    			var value=data[columns[key].itemid];
			    			if(value) {
				    			if("Y" == value.changeflag)
					    			cell.style.backgroundColor = "#CCCCCC";
	
				    			if("M" == columns[key].itemtype) {
					    			var tipValue = value.tipNewValue;
					    			if(tipValue){
						    			var span = "<span onmouseout=\"UnTip()\" onmouseover=\"Tip('" + tipValue + "',STICKY,true)\">";
						    			span += value.newvalue + "</span>";
					    				cell.innerHTML= span;
						    		}
					    		} else
					    			cell.innerHTML= value.newvalue;
			    			}
			    			
			    			i++;
			        	}

			        	i++;
			        	
			    	} else {
						var dataStateCell=dataRow.insertCell(0);
		    			dataStateCell.colSpan="2";
		    			dataStateCell.className="RecordRow";
		    			dataStateCell.style.whiteSpace = "nowrap";
						if("new"==data.type)
		    				dataStateCell.innerHTML="新增";
		    			else if("delete"==data.type)
			    			dataStateCell.innerHTML="删除";
		    			else if("insert"==data.type)
			    			dataStateCell.innerHTML="插入";
		    			else if("select"==data.type)
			    			dataStateCell.innerHTML="未变动";

		    			i=1;
	    			
						for(var key in columns){
			    			var cell=dataRow.insertCell(i);
			    			if("N" == columns[key].itemtype)
				    			cell.style.textAlign="right";
			    			else
				    			cell.style.textAlign="left";
			    			
			      			cell.style.height="36";
			    			cell.className="RecordRow";
			    			cell.style.whiteSpace = "nowrap";
			    			
			    			var value=data[columns[key].itemid];
			    			if(value) {
				    			if("Y" == value.changeflag)
					    			cell.style.backgroundColor = "#CCCCCC";
	
				    			if("M" == columns[key].itemtype) {
					    			var tipValue = value.tipNewValue;
					    			if(tipValue){
					    				var span = "<span onmouseout=\"UnTip()\" onmouseover=\"Tip('" + tipValue + "',STICKY,true)\">";
						    			span += value.newvalue + "</span>";
					    				cell.innerHTML= span;
						    		}
					    		} else
						    		cell.innerHTML= value.newvalue;
			    			}
				    		
			    			i++;
			        	}
			    	}

			    	if("true" == multimedia) {
			    		var mediaCell=dataRow.insertCell(i);
			    		mediaCell.className="RecordRow";
			    		mediaCell.style.whiteSpace = "nowrap";
			    		mediaCell.style.textAlign="center";
			    		if("update" == data.type)
			    			mediaCell.rowSpan="2";

				    	var html = "<IMG border=0 src='/images/muli_view.gif'" 
				    		+ "onclick=\"multimediahref('" + setid + "','" + (data.recordid?data.recordid:"") 
                	       	+ "','" + data.sequence +"','" + data.type + "')\">";
                	    mediaCell.innerHTML=html;
			    		i++;
			    	}
			    	
		    		var operateDataCell=dataRow.insertCell(i);
		    		operateDataCell.className="RecordRow";
		    		operateDataCell.style.whiteSpace = "nowrap";
		    		operateDataCell.style.textAlign="center";
		    		if("update" == data.type)
			    		operateDataCell.rowSpan="2";

		    		if("01" == data.sp_flag)
			    		operateDataCell.innerHTML="起草";
					else if("02" == data.sp_flag) {
						var html="";
						<hrms:priv func_id="260632,03082" module_id="">
                           	html += "<a href=\"javascript:approveinfo('" + fieldSetId + "','" + data.recordid 
                    	       	+ "','" + data.type + "','" + data.sequence +"','pz')\">批准</a>&nbsp;&nbsp;";
                       	</hrms:priv>
                    	<hrms:priv func_id="260631,03081" module_id="">
                    		html += "<a href=\"javascript:approveinfo('" + fieldSetId + "','" + data.recordid 
                            	+ "','" + data.type + "','" + data.sequence +"','bh')\">退回</a>";
                       	</hrms:priv>
			    		operateDataCell.innerHTML=html;
                        document.getElementById("approvetag"+index).style.display="block"; buttonshow="block";
					} else if("03" == data.sp_flag)
			    		operateDataCell.innerHTML="已批";
					else if("07" == data.sp_flag)
			    		operateDataCell.innerHTML="退回";
						
					rowNum ++;
	        	}
			}
            tr.cells[0].appendChild(table);
         } else {
	        var rowNum = 0,i = 0;
	        var dataRow;
	        var data = itemList[0];
	        if("02" == data.sp_flag)
    			document.getElementById("approvetag"+index).style.display="block"; buttonshow="block";
    			
        	for(var key in columns){
	        	if(i == 0 || i == 6) {
        			dataRow=table.insertRow(rowNum);
        			i = 0;
        			rowNum++;
	        	}

	        	var itemCell=dataRow.insertCell(i);
	        	itemCell.style.textAlign="left";
	        	itemCell.style.height="36";
	        	itemCell.className="TableRow";
	        	itemCell.style.whiteSpace = "nowrap";
	    		itemCell.innerHTML= columns[key].itemdesc;
	    		i++;
	    		
	    		var dataCell=dataRow.insertCell(i);
	    		if("N" == columns[key].itemtype)
	    			dataCell.style.textAlign="right";
    			else
    				dataCell.style.textAlign="left";
    			
	    		dataCell.style.height="36";
	    		dataCell.className="RecordRow";
	    		dataCell.style.whiteSpace = "nowrap";
	    		
	    		var value=data[columns[key].itemid];
	    		if(value) {
		    		if("Y" == value.changeflag)
		    			dataCell.style.backgroundColor = "#CCCCCC";
	
		    		if("M" == columns[key].itemtype) {
		    			var tipValue = value.tipNewValue;
		    			if(tipValue){
		    				var dataCell = "<span onmouseout=\"UnTip()\" onmouseover=\"Tip('" + tipValue + "',STICKY,true)\">";
		    				dataCell += value.newvalue + "</span>";
		    				dataCell.innerHTML= span;
			    		}
		    		} else
			    		dataCell.innerHTML= value.newvalue;
	    		}
    			
	    		i++;
	        }
		 	tr.cells[0].appendChild(table);

		 	if("true" == multimedia) {
		 		var mediaTable=document.createElement("table");
		 		mediaTable.id="Media"+index;
		 		mediaTable.style.width="500";
		 		mediaTable.style.marginTop="5px";
		 		mediaTable.style.border=0;
		 		mediaTable.style.display="block";
		 		mediaTable.className="ListTable1";
		 		var headCell = mediaTable.insertRow(0).insertCell(0);
		 		
		 		headCell.style.textAlign="left";
		 		headCell.style.height="36";
		 		headCell.className="TableRow";
		 		headCell.style.whiteSpace = "nowrap";
		 		headCell.colSpan="2";
		 		headCell.innerHTML= "附件";

		 		for(var i = 0; i < multimedialist.length; i++){
		 			var multimedia = multimedialist[i];
		 			var mediaRow = mediaTable.insertRow(i + 1);
		 			var mediaCell=mediaRow.insertCell(0);
		 			mediaCell.style.textAlign="center";
		 			mediaCell.style.height="36";
		 			mediaCell.style.width="36";
		 			mediaCell.className="TableRow";
		 			mediaCell.style.whiteSpace = "nowrap";
		 			if("new" == multimedia.type)
		 				mediaCell.innerHTML= "新增";
		 			else
		 				mediaCell.innerHTML= "删除";
    		 			
		    		var dataCell=mediaRow.insertCell(1);
		    		dataCell.style.textAlign="left";
		    		dataCell.style.height="36";
		    		dataCell.className="RecordRow";
		    		dataCell.style.whiteSpace = "nowrap";
		    		var html = "<a href='/servlet/DisplayOleContent?filePath=" + multimedia.path + "' target='_blank'>"
		    			+ getDecodeStr(multimedia.topic) + "</a>"; 
		    		dataCell.innerHTML = html;
    		    }
		 		tr.cells[0].appendChild(mediaTable);
		 	}
	     }
	}
   // 用户登记表
    function openwin(url)
    {
    	while(url.indexOf("`")!=-1){
    		url = url.replace("`","&");
    	}
       window.open(url,"_blank","left=0,top=0,width="+(screen.availWidth-10)+",height="+(screen.availHeight-40)+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
       
       //browseForm.action=url;
       //browseForm.target="_blank";
       //browseForm.submit();
        //var iframe_url="/general/query/common/iframe_query.jsp?src="+url;
           /*operuser中用户名*/
    	   //newwindow=window.open(target_url,'app','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=180,left=350,width=530,height=500');  	
        //var return_vo= window.showModalDialog(iframe_url,"app", 
        //   "dialogWidth:"+screen.availWidth+"; dialogHeight:"+screen.availHeight+";resizable:no;center:yes;scroll:yes;status:no");
    }
    // 审批
    function approveinfo(setid,recordid,type,sequence,state){
       var action = "/general/approve/personinfo/iframapp.do?b_query=link&setid="+setid;
          action = action+"&recordid="+recordid+"&type="+type+"&sequence="+sequence+"&state="+state;
       document.approvePersonForm.action = action;
       document.approvePersonForm.submit();
    }
   // 全部显示和修改部分显示
    function itemshow(){
    	var action = "/general/approve/personinfo/iframapp.do?b_query=link";
    	 document.approvePersonForm.action = action;
         document.approvePersonForm.submit();
    }
    // 批量批准
    function  allapprove(setid,state){
    	 var confirms;
    	 if(state=='allpz')
    		 confirms='批准';
    	 if(state == 'allbh')
    		 confirms='退回';
    	 
    	 if(confirm("确认"+confirms+"?")){
           var action = "/general/approve/personinfo/iframapp.do?b_query=link&setid="+setid+"&state="+state;
           document.approvePersonForm.action = action;
           document.approvePersonForm.submit();
           document.getElementById("pzall").disabled = "true";
           document.getElementById("bhall").disabled = "true";
    	 }
    }
    
    function backMain(){
    	document.location.href = "/general/approve/personinfo/approve.do?b_search=link&code=&kind=";
    }
        
    function outExcel(){
    	
    	var showflags="";
    	
    		var obj = document.getElementsByTagName("input");
    		for(var i=0;i<obj.length;i++){
    			if(obj[i].getAttribute("type")=="hidden"){
    				showflags += obj[i].value+",";
    			}
    		}
    	var hashvo=new ParameterSet();
		hashvo.setValue("b0110",'${approvePersonForm.b0110desc}');
		hashvo.setValue("e0122",'${approvePersonForm.e0122desc}');
		hashvo.setValue("name", '${approvePersonForm.username}');
		hashvo.setValue("showinfo",'${approvePersonForm.showinfo}');
		hashvo.setValue("chg_id",'${approvePersonForm.chg_id}');
		hashvo.setValue("showflags",showflags);
		
    	var request=new Request({method:'post',asynchronous:false,onSuccess:showeExcel,functionId:'0580010013'},hashvo);
    }
    
    
    function showeExcel(parameters){
    	var filename=parameters.getValue("filename");
    	filename = getDecodeStr(filename);
		var win=open("/servlet/vfsservlet?fileid=" + filename +"&fromjavafolder=true","excel");
    }
    
    function multimediahref(setid,keyvalue,sequence,state){
    		var dw=800,dh=500,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
      	thecodeurl="/general/inform/multimedia/multimedia_tree.do?b_query=link&setid="+setid+"&a0100=${approvePersonForm.a0100}&nbase=${approvePersonForm.nbase}"
      			  +"&i9999="+keyvalue+"&chg_id=${approvePersonForm.chg_id}&dbflag=A&canedit=appview&state="+state+"&sequence="+sequence;
      	window.showModalDialog(thecodeurl, "", 
      	"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:800px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no");
    }
</script>

<body >
<hrms:themes />
<html:form action="/general/approve/personinfo/iframapp" >
     <div style="overflow: visible;" >
        <bean:define id="showinfo" value="${approvePersonForm.showinfo}"/>
        <table class="tablealign" border=0 cellspacing="0" cellpadding="0" style="padding-top:0px;margin-left:-5px;">
           <% int i=0; %>
           <tr> 
              <td style="font-size: 13px">
              <bean:define id="nbase" name="approvePersonForm" property="nbase"></bean:define>
              <bean:define id="a0100" name="approvePersonForm" property="a0100"></bean:define>
              <%
              String encryptParam = PubFunc.encrypt("userbase="+PubFunc.decrypt(nbase.toString())+"&a0100="+PubFunc.decrypt(a0100.toString())+"&inforkind=1&npage=1&multi_cards=-1&userpriv=noinfo&flick=1&flag=notself");
              %>
                  ${approvePersonForm.b0110desc }/${approvePersonForm.e0122desc }/<a href="###" onclick='openwin("/general/inform/synthesisbrowse/mycard.do?b_mysearch=link`encryptParam=<%=encryptParam %>");'>${approvePersonForm.username}</a> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                  <html:radio property="showinfo" name="approvePersonForm" value="change" onclick="itemshow()">显示变动信息</html:radio>
                  <html:radio property="showinfo" name="approvePersonForm" value="all" onclick="itemshow()">显示全部信息(低亮显示为修改指标)</html:radio>
              </td>
           </tr>
            <logic:iterate id="fieldset" name="approvePersonForm" property="changelist">
                  <bean:define id="fieldsetid" value="${fieldset.value.map.setid}"/>
                  <bean:define id="showsp" value="${fieldset.value.map.showsp}"/>
                  <%String setid = PubFunc.encrypt(fieldsetid); String display = "block";
                  	if(i > 0 && showinfo.equals("all")) {%>
	                  <bean:define id="showsp" value="false"/>
                  <%} else if(i == 0 && showinfo.equals("all")){%>
                  	  <bean:define id="showsp" value="true"/>
                  <%}%>
                  
                 <tr>
                     <td > 
                         <table border=0 cellspacing="0" cellpadding="0">
                            <tr>
                               <td  align="left" style="padding-left: 0px;padding-right: 0px;" nowrap="nowrap" valign="middle">
                                   <span id="arrow<%=i %>" onclick="Javascript:showORhidden('<%=i%>','<%=fieldsetid %>');">
                                     <logic:equal value="true" name="showsp" >
                                         <img src="/images/show.gif"  />
					                 </logic:equal>
					                 <logic:notEqual value="true" name="showsp">
					                     <img src="/images/hidden.gif"  />
                                     </logic:notEqual>
                                   </span>  
                               </td>
                               <td align="left" width="150" style="padding-left: 0px;"><a href="Javascript:showORhidden('<%=i%>','<%=fieldsetid %>');">${fieldset.value.map.setdesc }</a></td>
	                               <td align="left" style="padding-left: 0px;">
	                                <span id="approvetag<%=i %>" style="display:none">
	                                   <hrms:priv func_id="260632,03082" module_id="">
	                                   <a href="###"  onclick="allapprove('<%=setid %>','allpz')">批准</a>
	                                   </hrms:priv><hrms:priv func_id="260631,03081" module_id="">
	                                   /<a href="###"  onclick="allapprove('<%=setid %>','allbh')">退回</a>
	                                   </hrms:priv>
	                                </span>
	                               </td>
                            </tr>
                         </table>  
                     </td>
                 </tr>
                 <logic:equal value="true" name="showsp" >
                       <tr id='<%="table"+i %>' style=" display:block;">
                       <input type="hidden" value="Y" id='<%="flag"+i %>' flag="setsp"/>
                 </logic:equal>
                 <logic:notEqual value="true" name="showsp">
                       <tr id='<%="table"+i %>' style=" display:none;">
                       <input type="hidden" value="N" id='<%="flag"+i %>' flag="setsp"/>
                 </logic:notEqual>
                     <td>
                     
                     <%if(i == 0 || "change".equalsIgnoreCase(showinfo)){ %>
                  	 <bean:define id="showmedia" value="${fieldset.value.map.multimedia}"/>
                  	 <bean:define id="items" value="${fieldset.value.map.itemlist}"/>    
                  	<%
                    	int size = ((ArrayList)((LazyDynaBean)((Entry)fieldset).getValue()).get("itemlist")).size();
                    	if(size<1)
                    	   display = "none";
                  	%>
                         <table border=0 class="ListTable1" id="data<%=i %>" style="display:<%=display%>"> 
                            <bean:define id="fieldsetid" value="${fieldset.value.map.setid}"/>
                              <%if(showinfo.equals("all") && fieldsetid.equals("A01")){ }else{%>
                              
		                              <tr>
		                                 <td colspan="2" class="TableRow" nowrap="nowrap">状态</td>
		                                 <logic:iterate id="column" collection="${fieldset.value.map.columns}">
		                                      <td class="TableRow" nowrap="nowrap">${column.value.itemdesc}</td>
		                                 </logic:iterate>
		                                 <logic:notEqual value="A01" name="fieldsetid">
		                                    <logic:equal value="true" name="showmedia">
		                                    		<td class="TableRow" nowrap="nowrap" align="center">附件</td>
		                                    </logic:equal>
		                                 <td class="TableRow" nowrap="nowrap" align="center">操作</td>
		                                 </logic:notEqual>
		                              </tr>
                              <%} %>
                              
                              <logic:iterate id="item" collection="${fieldset.value.map.itemlist}">
                                  <bean:define id="sp_flag" value="${item.map.sp_flag}"/>
                                  <%
                                     LazyDynaBean ildb = (LazyDynaBean)pageContext.getAttribute("item");
                                     HashMap columnsmap = (HashMap)ildb.getMap();
                                  %>
                                  
	                              <logic:equal value="update" name="item" property="type">
	                                   <%if(showinfo.equals("all") && fieldsetid.equals("A01")){ %>
	                                          
		                                      <tr>
                                                          <% int k=0; %>
	                                               <logic:iterate id="column" collection="${fieldset.value.map.columns}">
	                                                   
	                                                    <%k++;  if(k%3-1==0){ %>
	                                                        <tr>
	                                                    <%} %>
	                                                      <td class="TableRow"   nowrap="nowrap">${column.value.itemdesc}</td>
	                                                      <bean:define id="itemid" value="${column.key}"/>
	                                                      <bean:define id="itemtype" value="${column.value.itemtype }"/>
			                                                    <% 
			                                                         LazyDynaBean cldb = null;
			                                                               cldb = (LazyDynaBean)ildb.get(itemid);
			                                                                   if(cldb.get("changeflag")!=null && cldb.get("changeflag").equals("Y")){
			                                                    %>
			                                                                          <logic:equal value="M" name="itemtype" >
			                                                                          <hrms:showitemmemo showtext="showtext" itemtype="M" setname="${fieldsetid}" tiptext="tiptext" text='<%=cldb.get("newvalue").toString()%>'></hrms:showitemmemo>
						                                                            <td class="RecordRow" style="background-color:#CCCCCC;" nowrap="nowrap" ${tiptext }>
						                                                                ${showtext }
						                                                            </td>
							                                                      </logic:equal>
							                                                      <logic:notEqual value="M" name="itemtype">
						                                                            <td class="RecordRow" nowrap="nowrap" style="background-color:#CCCCCC;"><%=cldb.get("newvalue") %>&nbsp;</td>
						                                                          </logic:notEqual>
		                                                        <%             }else{ %> 
		                                                                         <logic:equal value="M" name="itemtype" >
		                                                                         <hrms:showitemmemo showtext="showtext" itemtype="M" setname="${fieldsetid}" tiptext="tiptext" text='<%=cldb.get("newvalue").toString()%>'></hrms:showitemmemo>
						                                                            <td class="RecordRow"  nowrap="nowrap" ${tiptext }>
						                                                                  ${showtext }
						                                                            </td>
							                                                      </logic:equal>
							                                                      <logic:notEqual value="M" name="itemtype">
							                                                         <logic:equal value="N" name="itemtype">
							                                                             <td class="RecordRow" nowrap="nowrap" align="right">
							                                                         </logic:equal>
							                                                         <logic:notEqual value="N" name="itemtype">
							                                                             <td class="RecordRow" nowrap="nowrap">
							                                                         </logic:notEqual>
						                                                               <%=cldb.get("newvalue") %>&nbsp;</td>
						                                                          </logic:notEqual>
		                                                        <%
		                                                                       } %>
		                                                         
		                                                         <% if(k%3 ==0){ %></tr><%}  %>
		                                                                      
	                                                      
	                                                   
	                                               </logic:iterate>
	                                               <% k =3-k%3; 
	                                                  int j=0;
	                                                  while(j<k&&k%3!=0){//tiany 修改 整除3的时候 没必要添加空单元格数据 现在产生一整行空的数据
	                                               %>
	                                                   <td class="TableRow"  nowrap="nowrap">&nbsp;</td>
	                                                   <td class="RecordRow"  nowrap="nowrap" >&nbsp;</td>
	                                               <%
	                                                 j++;  }
	                                               %>
	                                                  
	                                          </tr>
	                                          <logic:equal value="2" name="sp_flag">
	                                              <script> document.getElementById("approvetag"+'<%=i%>').style.display="block"; buttonshow="block"; </script>
	                                          </logic:equal>
	                                    <%}else{ %>
	                                       
	                                           <tr>
		                                          <td rowspan="2" class="RecordRow" nowrap="nowrap">修改</td>
		                                          <td class="RecordRow" nowrap="nowrap">变动前</td>
		                                              <logic:iterate id="column" collection="${fieldset.value.map.columns}">
		                                                  <bean:define id="itemid" value="${column.key}" />
		                                                  <bean:define id="itemtype" value="${column.value.itemtype }"/>
			                                                    <% 
			                                                         LazyDynaBean cldb = null;
			                                                         boolean haskey = columnsmap.containsKey(itemid);
			                                                           if(haskey){
			                                                               cldb = (LazyDynaBean)ildb.get(itemid);
			                                                                   if(cldb.get("changeflag").equals("Y")){
			                                                    %>
			                                                                      <logic:equal value="M" name="itemtype" >
			                                                                      <hrms:showitemmemo showtext="showtext" itemtype="M" setname="${fieldsetid}" tiptext="tiptext" text='<%=cldb.get("oldvalue").toString()%>'></hrms:showitemmemo>
						                                                            <td class="RecordRow" style="background-color:#CCCCCC;" nowrap="nowrap" ${tiptext }>
						                                                                      ${showtext }
						                                                            </td>
							                                                      </logic:equal>
							                                                      <logic:notEqual value="M" name="itemtype">
						                                                            <td class="RecordRow" nowrap="nowrap" style="background-color:#CCCCCC;"><%=cldb.get("oldvalue") %></td>
						                                                          </logic:notEqual>
		                                                                       <%}else{ %> 
		                                                                            <logic:equal value="M" name="itemtype" >
		                                                                            <hrms:showitemmemo showtext="showtext" itemtype="M" setname="${fieldsetid}" tiptext="tiptext" text='<%=cldb.get("oldvalue").toString()%>'></hrms:showitemmemo>
						                                                            <td class="RecordRow"  nowrap="nowrap" ${tiptext }>
						                                                                  ${showtext }
						                                                            </td>
							                                                      </logic:equal>
							                                                      <logic:notEqual value="M" name="itemtype">
						                                                             <logic:equal value="N" name="itemtype">
							                                                             <td class="RecordRow" nowrap="nowrap" align="right">
							                                                         </logic:equal>
							                                                         <logic:notEqual value="N" name="itemtype">
							                                                             <td class="RecordRow" nowrap="nowrap">
							                                                         </logic:notEqual>
                                                                                       <%=cldb.get("oldvalue") %></td>
						                                                          </logic:notEqual>
		                                                        <%
		                                                                       }
		                                                               }else{ 
		                                                         %>
		                                                             <td class="RecordRow">&nbsp;</td>
		                                                         <%    } %>
	                                                  </logic:iterate>
	                                            <logic:notEqual value="A01" name="fieldsetid">
												   <logic:equal value="true" name="showmedia">
		                                    				<td rowspan="2" class="RecordRow" nowrap="nowrap" align="center">
		                                    					<bean:define id="recordMedia" value="${item.map.multimedia }"/>
		                                    				    <logic:equal value="true" name="recordMedia">
		                                    					<IMG border=0 src="/images/muli_view.gif" onclick="multimediahref('${fieldsetid}','${item.map.recordid}','${item.map.sequence}','${item.map.type}')">
		                                    					</logic:equal>
		                                    				</td>
		                                    		   </logic:equal>
	                                               <td rowspan="2" class="RecordRow" nowrap="nowrap">
	                                                  
	                                                      <logic:equal value="01" name="sp_flag">
	                                                                                                                                                                         起草                                                                                                              
	                                                      </logic:equal>
	                                                      <logic:equal value="02" name="sp_flag">
	                                                      <bean:define id="recordid" value="${item.map.recordid}"></bean:define>
                                                      <%String recordId = PubFunc.encrypt(recordid.toString()); %>
	                                                      <hrms:priv func_id="260632,03082" module_id="">
	                                                           <a href="javascript:approveinfo('<%=setid %>','<%=recordId %>','${item.map.type}','${item.map.sequence}','pz')">批准</a>&nbsp;&nbsp;
	                                                      </hrms:priv>
	                                                      <hrms:priv func_id="260631,03081" module_id="">
																<a href="javascript:approveinfo('<%=setid %>','<%=recordId %>','${item.map.type}','${item.map.sequence}','bh')">退回</a>
	                                                       </hrms:priv>
	                                                           <script> document.getElementById("approvetag"+'<%=i%>').style.display="block"; buttonshow="block";</script>
	                                                      </logic:equal>
	                                                      <logic:equal value="03" name="sp_flag">
	                                                                                                                                                                              已批
	                                                      </logic:equal>
	                                                      <logic:equal value="07" name="sp_flag">
	                                                                                                                                                                              退回
	                                                      </logic:equal>
	                                               </td>
	                                            </logic:notEqual>
		                                       </tr>
		                                       <tr>
		                                          <td class="RecordRow" nowrap="nowrap">变动后</td>
		                                          <logic:iterate id="column" collection="${fieldset.value.map.columns}">
		                                                  <bean:define id="itemid" value="${column.key}"/>
		                                                  <bean:define id="itemtype" value="${column.value.itemtype }"/>
			                                                    <% 
			                                                         LazyDynaBean cldb = null;
			                                                         boolean haskey = columnsmap.containsKey(itemid);
			                                                           if(haskey){
			                                                               cldb = (LazyDynaBean)ildb.get(itemid);
			                                                                   if(cldb.get("changeflag").equals("Y")){
			                                                    %>
			                                                                      <logic:equal value="M" name="itemtype" >
			                                                                         <hrms:showitemmemo showtext="showtext" itemtype="M" setname="${fieldsetid}" tiptext="tiptext" text='<%=cldb.get("newvalue").toString()%>'></hrms:showitemmemo>
						                                                            <td class="RecordRow" style="background-color:#CCCCCC;" nowrap="nowrap" ${tiptext }>
						                                                                  ${showtext }
						                                                            </td>
							                                                      </logic:equal>
							                                                      <logic:notEqual value="M" name="itemtype" >
						                                                            <td class="RecordRow" nowrap="nowrap" style="background-color:#CCCCCC;"><%=cldb.get("newvalue") %></td>
						                                                          </logic:notEqual>
		                                                                       <%}else{ %> 
		                                                                        <logic:equal value="M" name="itemtype" >
		                                                                            <hrms:showitemmemo showtext="showtext" itemtype="M" setname="${fieldsetid}" tiptext="tiptext" text='<%=cldb.get("newvalue").toString()%>'></hrms:showitemmemo>
						                                                            <td class="RecordRow"  nowrap="nowrap" ${tiptext }>
						                                                                  ${showtext }
						                                                            </td>
							                                                      </logic:equal>
							                                                      <logic:notEqual value="M" name="itemtype">
							                                                             <logic:equal value="N" name="itemtype">
								                                                             <td class="RecordRow" nowrap="nowrap" align="right">
								                                                         </logic:equal>
								                                                         <logic:notEqual value="N" name="itemtype">
								                                                             <td class="RecordRow" nowrap="nowrap">
								                                                         </logic:notEqual>
						                                                                     <%=cldb.get("newvalue") %></td>
						                                                          </logic:notEqual>
		                                                        <%
		                                                                       }
		                                                               }else{ 
		                                                         %>
		                                                             <td class="RecordRow">&nbsp;</td>
		                                                         <%    } %>
	                                                  </logic:iterate>
		                                       </tr>
	                                    <%} %>
	                              </logic:equal>
	                              <logic:notEqual value="update" name="item" property="type">
	                                       <tr>
	                                          <td colspan="2" class="RecordRow" align="center" nowrap="nowrap">
	                                               <logic:equal value="new" name="item" property="type">新增</logic:equal>
	                                               <logic:equal value="delete" name="item" property="type">删除</logic:equal>
	                                               <logic:equal value="insert" name="item" property="type">插入</logic:equal>
	                                               <logic:equal value="select" name="item" property="type">未变动</logic:equal>
	                                          </td>
	                                          <logic:iterate id="column" collection="${fieldset.value.map.columns}">
	                                                  <bean:define id="itemid" value="${column.key}"/>
	                                                  <bean:define id="itemtype" value="${column.value.itemtype }"/>
		                                                    <% 
		                                                    LazyDynaBean cldb = null;
		                                                    boolean haskey = columnsmap.containsKey(itemid);
	                                                           if(haskey){
	                                                              cldb = (LazyDynaBean)ildb.get(itemid);
	                                                    %>
	                                                      <logic:equal value="M" name="itemtype" >
	                                                        <hrms:showitemmemo showtext="showtext" itemtype="M" setname="${fieldsetid}" tiptext="tiptext" text='<%=cldb.get("newvalue").toString()%>'></hrms:showitemmemo>
	                                                            <td class="RecordRow" nowrap="nowrap" ${tiptext }>
	                                                                 ${showtext }
	                                                            </td>
	                                                      </logic:equal>
	                                                      <logic:notEqual value="M" name="itemtype">
	                                                         <logic:equal value="N" name="itemtype">
	                                                             <td class="RecordRow" nowrap="nowrap" align="right">
	                                                         </logic:equal>
	                                                         <logic:notEqual value="N" name="itemtype">
	                                                             <td class="RecordRow" nowrap="nowrap">
	                                                         </logic:notEqual>
                                                            <%=cldb.get("newvalue") %></td>
                                                          </logic:notEqual>
                                                       <%}else{ %>
                                                            <td class="RecordRow" nowrap="nowrap">&nbsp;</td>
                                                        <%} %>
                                                  </logic:iterate>
                                               <logic:equal value="true" name="showmedia">
		                                    				<td  class="RecordRow" nowrap="nowrap" align="center">
		                                    				  <logic:notEqual value="03" name="sp_flag"></logic:notEqual>
		                                    				  	<logic:notEqual value="delete" name="item" property="type">
			                                    				  	<bean:define id="recordMedia" value="${item.map.multimedia }"/>
			                                    				    <logic:equal value="true" name="recordMedia">
			                                    						<IMG border=0 src="/images/muli_view.gif" onclick="multimediahref('${fieldsetid}','${item.map.recordid}','${item.map.sequence}','${item.map.type}')">
			                                    					</logic:equal>
		                                    				  	</logic:notEqual>
		                                    				</td>
		                                    	   </logic:equal>   
                                              <td class="RecordRow" nowrap="nowrap">
                                                       <logic:equal value="01" name="sp_flag">
	                                                                                                                                                                         起草                                                                                                              
	                                                      </logic:equal>
                                                      <logic:equal value="02" name="sp_flag" >
                                                      <bean:define id="recordid" value="${item.map.recordid}"></bean:define>
                                                      <%String recordId = PubFunc.encrypt(recordid.toString()); %>
                                                          <hrms:priv func_id="260632,03082" module_id="">
                                                           <a href="javascript:approveinfo('<%=setid %>','<%=recordId %>','${item.map.type}','${item.map.sequence}','pz')">批准</a>&nbsp;&nbsp;
                                                         </hrms:priv>
   											             <hrms:priv func_id="260631,03081" module_id="">	
                                                           <a href="javascript:approveinfo('<%=setid %>','<%=recordId %>','${item.map.type}','${item.map.sequence}','bh')">退回</a>
                                        					</hrms:priv>
                                                           <script> document.getElementById("approvetag"+'<%=i%>').style.display="block"; buttonshow="block";</script>
                                                      </logic:equal>
                                                      <logic:equal value="03" name="sp_flag">
                                                                                                                                                                              已批
                                                      </logic:equal>
                                                      <logic:equal value="07" name="sp_flag">
                                                                                                                                                                              退回
                                                      </logic:equal>
                                              </td>
	                                       </tr>
	                              </logic:notEqual>
	                              
	                              
                              </logic:iterate>
                         </table>
                         <%} %>
                     </td>
                 </tr>
                 <logic:equal value="A01" name="fieldsetid">
	                 <logic:equal value="true" name="showmedia">
	                 	<%if("change".equalsIgnoreCase(showinfo)){
	                 	   display = "block";
	                 	} %>
	                    <tr>
	                       <td style="padding-top:5px;">
	                          <table class="ListTable1" id="Media<%=i %>" style="display:<%=display%>" width=500>
	                          	<tr>
	                                  <td class="tableRow" colspan="2" nowrap>附件</td>
	                            </tr>
	                           <logic:iterate id="media" collection="${fieldset.value.map.multimedialist}">
	                           	 <bean:define id="mediatype" value="${media.map.type }"/>
	                             <bean:define id="filePath" value="${media.map.path }"/>
	                             <bean:define id="filename" value="${media.map.filename }"/>
	                             <%
	                                 filePath = filePath+"/"+filename;
	                                 filePath = PubFunc.encrypt(filePath);
	                             %>
	                             <logic:equal value="new"  name="mediatype">
	                               <tr>
	                                  <td class="recordRow" nowrap width=30>新增</td>
	                                  <td class="recordRow" nowrap>
	                                  	<a href="/servlet/DisplayOleContent?filePath=<%= filePath%>" target="_blank">
	                                  		${media.map.topic}
	                                  	</a>
	                                  </td>
	                               </tr>
	                             </logic:equal>
	                             <logic:equal value="delete" name="mediatype">
	                               <tr>
	                                  <td class="recordRow" nowrap width=30>删除</td>
	                                  <td class="recordRow" nowrap>
	                                  	<a href="/servlet/DisplayOleContent?filePath=<%= filePath%>" target="_blank">
	                                  		${media.map.topic}
	                                  	</a>
	                                  </td>
	                               </tr>
	                             </logic:equal>
	                           </logic:iterate>
	                          </table>
	                       </td>
	                    </tr>
	                 </logic:equal>
	                 <bean:define id="newsp_flag" value="${fieldset.value.map.sp_flag}"/>
	                 <logic:equal value="02" name="newsp_flag">
	                       <script> document.getElementById("approvetag"+'<%=i%>').style.display="block"; buttonshow="block";</script>
	                 </logic:equal>
	             </logic:equal>
	             <% i++; %>
            </logic:iterate>
                 <tr><td height="5px"></td></tr>
                 <tr>
                    <td>
                     <%-- button元素按钮默认为submit按钮  改为button类型 按钮   wangb 20180206 bug 34422 --%>
                     <hrms:priv func_id="260632,03082" module_id="">	
		                   <button type="button" class="mybutton" onclick="allapprove('','allpz')" id="pzall">批准</button>&nbsp;
		             </hrms:priv>
		             <hrms:priv func_id="260631,03081" module_id="">	
		                   <button type="button" class="mybutton" onclick="allapprove('','allbh')" id="bhall">退回</button>&nbsp;
		                  </hrms:priv> 
		                   <button type="button" class="mybutton" onclick="outExcel()">导出Excel</button>&nbsp;
		                   <button type="button" class="mybutton" onclick="backMain()">返回</button>
                    </td>
                 </tr>
        </table>
     </div>
     
      
</html:form>
</body>
<script>
     if(buttonshow=="none"){
    	 document.getElementById("pzall").disabled=true;
    	 document.getElementById("bhall").disabled=true;
     }
//调整 整体样式   wangb 20180206 
var form = document.getElementsByName('approvePersonForm')[0];
form.style.marginLeft = '10px';
</script>