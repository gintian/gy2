<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.dao.RecordVo"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.sys.dbinit.DbinitForm" %>
<%@ page import="com.hrms.hjsj.sys.VersionControl"%>
    <%! boolean bdisplayMultimedia=false;
    %>
    <%
	VersionControl vc = new VersionControl();
	if(vc.searchFunctionId("03040110")){ 
	    bdisplayMultimedia=true;
	}
	%>
	<%
		String infor=(String)session.getAttribute("infor");
		if(request.getParameter("infor")!=null)
		{			
			infor = request.getParameter("infor");
		}
		session.setAttribute("infor", infor);
		
	%>
<logic:equal name="dbinitForm" property="isrefresh" value="yes">
    <script language="javascript">
       //parent.mil_menu.location.reload();
   </script>
  </logic:equal>

<script language="JavaScript" src="../../../../module/utils/js/template.js"></script>
<script language="javascript">

function reflesh(){
   		document.dbinitForm.action="/system/dbinit/fieldsetlist.do?b_query1=link";
	    document.dbinitForm.submit();
}

function voider(infor,fieldsetid,changeflag,useflag,fieldsetdesc,customdesc,multimedia_file_flag){
		var theurl="/system/dbinit/fieldsetlist.do?b_amend=link&infor="+infor+"&id="+fieldsetid+"&flag="+changeflag+"&useflag="+useflag+"&fieldsetdesc="+$URL.encode(getEncodeStr(fieldsetdesc))+"&customdesc="+$URL.encode(getEncodeStr(customdesc))+"&multimedia_file_flag="+multimedia_file_flag;
    	   
    	/*var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
    	var return_vo= window.showModalDialog(iframe_url, 'template_win', 
      				"dialogWidth:410px; dialogHeight:280px;resizable:no;center:yes;scroll:yes;status:yes");
		if(return_vo!=null){
			 reflesh();*/
			 
	    document.dbinitForm.action=theurl;
	    document.dbinitForm.submit();
}
	
function checkdelete(){
		var str="";
		for(var i=0;i<document.dbinitForm.elements.length;i++)
			{
				if(document.dbinitForm.elements[i].type=="checkbox")
				{
					if(document.dbinitForm.elements[i].checked==true)
					{
						if(document.dbinitForm.elements[i].name=="selbox")
							continue;
							str+=document.dbinitForm.elements[i].value+"/";
					}
				}
			}
		if(str.length==0)
			{
				alert("请选择！");
				return;
			}else{
				if(confirm("<bean:message key="workbench.info.isdelete"/>?"))
    			{
    				var nodes = str.substring(0).split('/');
				
						 for(var j=0;j<nodes.length;j++)
						 {
							 var currnode=parent.frames['mil_menu'].Global.selectedItem;
							 if(currnode==null)
							 	return;
							 if(currnode.uid=="root"){
								 var nodess = currnode.childNodes;
						   	 		for(index in nodess){
						   	 			var node = nodess[index];
						   	 			if("${dbinitForm.infor}"==node.uid){
						   	 				if(node.load)
											 for(var i=0;i<=node.childNodes.length-1;i++)
											 {
												if(nodes[j]==node.childNodes[i].uid)
													node.childNodes[i].remove();
								  			}
						   	 			}
						   	 		}
							 }else{
							 if(currnode.load)
							 for(var i=0;i<=currnode.childNodes.length-1;i++)
							 {
								if(nodes[j]==currnode.childNodes[i].uid)
								currnode.childNodes[i].remove();
				  			}
						 	}
			  			}	
					dbinitForm.action="/system/dbinit/fieldsetlist.do?b_delete=link&deletestr="+str; 
				 	dbinitForm.submit();
				 	//parent.frames["mil_menu"].location.reload();
				}
			}
}

function save(code,name,infor,isrefresh){
	if(isrefresh=='save'){
		var currnode=parent.frames['mil_menu'].Global.selectedItem;
		//var pt = currnode.getLastChild();
		//if(pt.uid==code)
   	 	//	return;
   	 	var uid = code;
		var text = code+'.'+name;
		var title = name;
   	 	currnode = currnode.root();
   	 	if(currnode.uid=="root"){
   	 		var nodes = currnode.childNodes;
   	 		for(index in nodes){
   	 			var node = nodes[index];
   	 			if(infor==node.uid){
   	 				if(node.load){
   	 					parent.frames['mil_menu'].Global.selectedItem=node;
   	 					var imgurl="/images/close.png";
						var xml = "";
	 					parent.frames['mil_menu'].add(uid,text,"/system/dbinit/fielditemlist.do?b_query=link&setid="+code,"mil_body",title,imgurl,xml);
   	 				}else{
   	 					node.expand();
   	 				}
   	 				
   	 			}
   	 		}
   	 	}
   	 		/*var currnode1 = currnode.childNodes[0];
     		var currnode2 = currnode.childNodes[1];
     		var currnode3 = currnode.childNodes[2];
     		if(infor=="A"){
     			//currnode1.select();
     			parent.frames['mil_menu'].Global.selectedItem=currnode1;
					if(currnode1.load){
						var imgurl="/images/close.png";
						var xml = "";
   	 						parent.frames['mil_menu'].add(uid,text,"/system/dbinit/fielditemlist.do?b_query=link&setid="+code,"mil_body",title,imgurl,xml);
   	 						//dbinitForm.document.location = currnode1.action.replace('link','aa');
   	 				}else
   	 					currnode.expand();
     		}else if(infor=="B"){
     			//currnode2.select();
     			parent.frames['mil_menu'].Global.selectedItem=currnode2;
					if(currnode2.load){
						var imgurl="/images/close.png";
						var xml = "";
   	 					parent.frames['mil_menu'].add(uid,text,"/system/dbinit/fielditemlist.do?b_query=link&setid="+code,"mil_body",title,imgurl,xml);
   	 				}else
   	 					currnode.expand();
     		}else if(infor=="K"){
     			//currnode3.select();
     			parent.frames['mil_menu'].Global.selectedItem=currnode3;
					if(currnode3.load){
						var imgurl="/images/close.png";
						var xml = "";
   	 					parent.frames['mil_menu'].add(uid,text,"/system/dbinit/fielditemlist.do?b_query=link&setid="+code,"mil_body",title,imgurl,xml);
   	 				}else
   	 					currnode.expand();
     		}
   	 	}*/
   	 	//var uid = code;
   	 	
		//var text = code+'.'+name;
		//var title = name;
		//var action = "/system/dbinit/fielditemlist.do?b_query=link&setid="+code;
		//var xml = "";
		//if(currnode.load){
		//	var imgurl="/images/close.png";
		//	if(infor!=null)
   	 	//		parent.frames['mil_menu'].add(uid,text,action,"mil_body",title,imgurl,xml);
		//}else
   	 	//	currnode.expand();
	}
}
function changeTrColor(id)
 {
    var ob=document.getElementById("tb");
    var j=ob.rows.length;
    for(var i=0;i<j-1;i++)
    {
         var o="a_"+i;
         var obj=document.getElementById(o);
         if(o==id)
         {
           if(o!=null)
           {
               obj.className="selectedBackGroud";
           }
         }
         else
         {
           if(i%2==0)
           {
              if(o!=null)
              {
                obj.className="trShallow";
              }
           }
           else
           {
               if(o!=null)
               {
                  obj.className="trDeep";
               }
           }
         }
    }
      
 }
 
 function moveorder(){
 	var infor='${dbinitForm.infor }';
 		var thecodeurl="/system/dbinit/fielditemlist.do?b_sorting=link&fieldsetid="+infor;
 		// var dw=400,dh=430,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
		// var return_vo= window.showModalDialog(thecodeurl, "",
		// 	"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
		// if(return_vo!=null){
		// 	refresh();
		// 	window.location.href="/system/dbinit/fieldsetlist.do?b_query=link&infor="+infor;
		// }
     return_vo ='';
     var theUrl = thecodeurl;
     Ext.create('Ext.window.Window', {
         id:'indexSorting',
         height: 460,
         width: 400,
         resizable:false,
         modal:true,
         autoScroll:false,
         autoShow:true,
         html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
         renderTo:Ext.getBody(),
         listeners:{
             'close':function () {
                 if (return_vo) {
                     window.location.href="/system/dbinit/fieldsetlist.do?b_query=link&infor="+infor;
                 }
             }}

     }).show();
 }
 
 function refresh(){
		var currnode=parent.frames['mil_menu'].Global.selectedItem;
		if(currnode==null)
			return;
	    currnode = currnode.root();
		//alert(currnode.uid);
		if(currnode.load)
		while(currnode.childNodes.length){
			//alert(currnode.childNodes[0].uid);
			currnode.childNodes[0].remove();
		}
		currnode.load=true;
		currnode.loadChildren();
		currnode.reload(1);
	}
</script>

<html:form action="/system/dbinit/fieldsetlist">
<table width="100%" border="0" cellspacing="0"  id="tb" align="center" cellpadding="0" class="ListTable">
   	  <thead>
      <tr>
        <td align="center" class="TableRow" nowrap>
		 <!--   <bean:message key="column.select"/>&nbsp;-->
		 <input type="checkbox" name="selbox" onclick="batch_select(this,'listForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
        </td>              
        <td align="center" class="TableRow" nowrap>
		<bean:message key="system.set.state"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="system.item.setid"/>&nbsp;
	    </td>	    
            <td align="center" class="TableRow" nowrap>
		<bean:message key="system.set.name"/>&nbsp;
	    </td>
        <td align="center" class="TableRow" nowrap>
		<bean:message key="system.set.custname"/>&nbsp;
	    </td>
        <td align="center" class="TableRow" nowrap>
		<bean:message key="system.set.chgflag"/>&nbsp;
	    </td>  
	       <%	
		if(bdisplayMultimedia){ 
		%>
			<%if(infor.equals("A")){ %>
	    <td align="center" class="TableRow" nowrap>
	    <bean:message key="system.set.multimediaFileFlag"/>&nbsp;
	    </td>
	    	<%} %>
	    <%	
		}
		%>
	    <hrms:priv func_id="3007111">         
        <td align="center" class="TableRow" nowrap>
		<bean:message key="system.infor.oper"/>            	
	    </td>
       </hrms:priv>     	    	    		        	        	        
           </tr>
   	  </thead>
   	  <%int i=0; %>
      <hrms:extenditerate id="element" name="dbinitForm" property="listForm.list" indexes="indexes"  pagination="listForm.pagination" pageCount="20" scope="session">
         <%if(i%2==0){ %>
	     <tr class="trShallow" id="<%="a_"+i%>" onclick="changeTrColor('<%="a_"+i%>');">
	     <%} else { %>
	     <tr class="trDeep" id="<%="a_"+i%>" onclick="changeTrColor('<%="a_"+i%>');">
	     <%}%>
         <bean:define id="fieldsetid" name='element' property='string(fieldsetid)'/>
        <td align="center" class="RecordRow" nowrap>
            <logic:equal name="element" property="string(useflag)" value="0">     		  
     		   <hrms:checkmultibox name="dbinitForm" property="listForm.select" value="${fieldsetid}" indexes="indexes"/>&nbsp;
	    	</logic:equal>     		  
	    </td>           
        <td align="center" class="RecordRow" nowrap>
            <logic:notEqual name="element" property="string(useflag)" value="0">
               <img src="/images/open1.png" border=0>
            </logic:notEqual>
	    </td>
            <td align="left" class="RecordRow" nowrap>
                    &nbsp;<bean:write  name="element" property="string(fieldsetid)" filter="true"/>&nbsp;
	    </td>         
            <td align="left" class="RecordRow" nowrap>
                    &nbsp;<bean:write  name="element" property="string(fieldsetdesc)" filter="true"/>&nbsp;
	    </td>
	    <%
	    	RecordVo vo = (RecordVo)pageContext.getAttribute("element");
	    	String setid = vo.getString("fieldsetid");
	     %>
        <td align="left" class="RecordRow" nowrap>
                    &nbsp;<a href="/system/dbinit/fielditemlist.do?b_query=link&encryptParam=<%=PubFunc.encrypt("setid="+setid)%>"><bean:write  name="element" property="string(customdesc)" filter="true"/></a>&nbsp;
	    </td>
        <td align="left" class="RecordRow" nowrap>
            <logic:equal name="element" property="string(changeflag)" value="0">  
				&nbsp;<bean:message key="system.setstate.c"/>
	    	</logic:equal>
            <logic:equal name="element" property="string(changeflag)" value="1">  
				&nbsp;<bean:message key="system.setstate.m"/>
	    	</logic:equal> 
            <logic:equal name="element" property="string(changeflag)" value="2">  
				&nbsp;<bean:message key="system.setstate.y"/>
	    	</logic:equal> 	    		    	           
	    </td>
	    	       <%	
		if(bdisplayMultimedia){ 
		%>
			<%if(infor.equals("A")){ %>
	    <td align="center" class="RecordRow" nowrap>
	    	<logic:equal name="element" property="string(multimedia_file_flag)" value="1">
            	<img src="/images/cc1.gif" border=0>
            </logic:equal>
	    </td>
	    	<%} %>
	    <%	
		}
		%>
	    <hrms:priv func_id="3007111"> 
        <td align="center" class="RecordRow" nowrap>
			<a href="javascript:voider('${sessionScope.infor}','<bean:write  name="element" property="string(fieldsetid)" filter="true"/>','<bean:write  name="element" property="string(changeflag)" filter="true"/>','<bean:write  name="element" property="string(useflag)" filter="true"/>','<bean:write  name="element" property="string(fieldsetdesc)" filter="true"/>','<bean:write  name="element" property="string(customdesc)" filter="true"/>','<bean:write  name="element" property="string(multimedia_file_flag)" filter="true"/>')">修改</a>&nbsp;
	    </td>
	    </hrms:priv>
        </tr>
         <% i++; %>
      </hrms:extenditerate>
</table>
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="dbinitForm" property="listForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="dbinitForm" property="listForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="dbinitForm" property="listForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>  
	        <td  align="right" nowrap class="tdFontcolor">
		      <p align="right">
		          <hrms:paginationlink name="dbinitForm" property="listForm.pagination"
				nameId="listForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="center" height="35px;">
            <hrms:priv func_id="3007110">
             <hrms:submit styleClass="mybutton" property="b_add">
            		<bean:message key="button.new.add"/>
	 	     </hrms:submit>
	 	     </hrms:priv>
	 	      <hrms:priv func_id="3007112">
          	 <input type='button' class="mybutton" property="b_delete"  onclick='checkdelete()' value='<bean:message key="button.delete"/>'/>
          	 </hrms:priv>
          	  <hrms:priv func_id="3007113">
          	 <input type='button' class="mybutton" property="b_move"  onclick='moveorder()' value='<bean:message key="button.movenextpre"/>'/>
            	</hrms:priv>
            </td>
          </tr>          
</table>
</html:form>
<script language="javascript">
 save('<bean:write name="dbinitForm" property="code" filter="true"/>','<bean:write name="dbinitForm" property="name" filter="true"/>','<bean:write name="dbinitForm" property="infor" filter="true"/>','<bean:write name="dbinitForm" property="isrefresh" filter="true"/>');
<%
			DbinitForm dbinitForm = (DbinitForm)session.getAttribute("dbinitForm");
			dbinitForm.setIsrefresh("");
		%>
</script>
