<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="org.apache.commons.beanutils.DynaBean"%>
<%@page import="com.hrms.frame.dao.RecordVo"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/popcalendar3.js"></script>
<hrms:themes></hrms:themes>
<script language="JavaScript" src="../../../../module/utils/js/template.js"></script>
<script type="text/javascript" language="javascript">
function updateclass(sysid){
	var fieldsetid=sysid;
  	busiMaintenceForm.action="/system/busimaintence/ShowSubsys.do?b_updateclass=link&fieldsetid="+fieldsetid+"&from=ssbs";
	busiMaintenceForm.submit();
}
function inputitem(fieldsetid){
  	var theurl="/system/busimaintence/showbusifield.do?b_input=link`query=query`fieldsetid="+fieldsetid;
  	theurl = $URL.encode(theurl);
	var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
	var dw=616,dh=530,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    // var return_vo= window.showModalDialog(iframe_url, arguments,
     //     "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
  	// //busiMaintenceForm.action="/system/busimaintence/ShowSubsys.do?b_input=link&query=query&fieldsetid="+fieldsetid;
	// //busiMaintenceForm.submit();
	// if(!return_vo)
	// 	return false;
	// if(return_vo.flag=="true")
	// 	reflesh()
    return_vo ='';
    Ext.create('Ext.window.Window', {
        id:'inputitem',
        height: 550,
        width: 630,
        resizable:false,
        modal:true,
        autoScroll:false,
        autoShow:true,
        html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+iframe_url+'"></iframe>',
        renderTo:Ext.getBody(),
        listeners: {
            'close': function () {
                if (return_vo.flag =='true') {
                    reflesh()
                }
            }
        }

    });
}
function reflesh(){
	// var sids = document.getElementById("sids").value;
	var sids = document.getElementsByName("sids")[0].value;
	document.busiMaintenceForm.action="/system/busimaintence/ShowSubsys.do?b_query=link&id="+sids;
	document.busiMaintenceForm.submit();
}
function updatedb(){
	var str="";
	for(var i=0;i<document.busiMaintenceForm.elements.length;i++)
			{
				if(document.busiMaintenceForm.elements[i].type=="checkbox")
				{
					if(document.busiMaintenceForm.elements[i].checked==true)
					{
						if(document.busiMaintenceForm.elements[i].name=="selbox")
							continue;
							str+=document.busiMaintenceForm.elements[i].value+"/";
					}
				}
			}
	if(str.length==0){
		alert(PLEASE_SEL);
		return;
	}else{
		if(confirm("<bean:message key="workbench.info.isdelete"/>?")){
				var nodes = str.substring(0).split('/');
				
						 for(var j=0;j<nodes.length;j++)
						 {
						 var currnode=parent.frames['mil_menu'].Global.selectedItem;
						 if(currnode==null)
						 	return;
						 if(currnode.load)
						 for(var i=0;i<=currnode.childNodes.length-1;i++)
						 {
						 var s = currnode.childNodes[i].uid;
						 var rtu = s.substring(0,s.length-2);
							if(nodes[j]==rtu)
							currnode.childNodes[i].remove();
			  			}
			  			}
			  			busiMaintenceForm.action="/system/busimaintence/ShowSubsys.do?b_delete=link&str="+str;
			  			busiMaintenceForm.submit();	
		}
	}
	//var fieldsetid=busiMaintenceForm.sids.value;
	//busiMaintenceForm.action="/system/busimaintence/editbusitablemake.do?b_query=link&returnvalue=ssb&id="+fieldsetid+"&operation="+opt;
	//busiMaintenceForm.submit();
}
function backtoup(){
	busiMaintenceForm.action="/system/busimaintence/showbusiname.do?b_query=link";
	busiMaintenceForm.submit();

}
function newbusitable(){
	var sid = document.getElementById("smid").value;
	busiMaintenceForm.action="/system/busimaintence/new_fieldset.do?b_init=link&mid="+sid;
	busiMaintenceForm.submit();
}
function save(setid,setdesc,mid,isrefresh){
	if(isrefresh=='save'){
		var currnode=parent.frames['mil_menu'].Global.selectedItem;
		var pt = currnode.getLastChild();
		if(pt.uid==setid)
   	 		return;
   	 	var uid = setid+"#0";
		var text = setdesc;
		var title = setdesc;
		currnode = currnode.root();
		if(currnode.uid=="root"){
			var currnode1 = currnode.childNodes[0];
			var currnode2 = currnode.childNodes[1];
			var currnode3 = currnode.childNodes[2];
			var currnode4 = currnode.childNodes[3];
			var currnode5 = currnode.childNodes[4];
			var currnode6 = currnode.childNodes[5];
			var currnode7 = currnode.childNodes[6];
			var currnode8 = currnode.childNodes[7];
			if(mid==35){
				currnode7.select();
				var action = "/system/busimaintence/showbusifield.do?b_query=link&fieldsetid="+setid+"&param=child";
				var xml = "";
				if(currnode7.load){
				var imgurl="/images/close.png";
				if(mid!=null)
   	 				parent.frames['mil_menu'].add(uid,text,action,"mil_body",title,imgurl,xml);
		}else
   	 		currnode7.expand();
			}
		}
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
</script>

<html:form action="/system/busimaintence/ShowSubsys">
	<table width="80%" border="0" cellspacing="0" id="tb" align="center" cellpadding="0" class="ListTable">
		<tr>
			<!-- <td align="center" class="TableRow" nowrap><bean:message key="column.select"/></td>-->
			<td align="center" class="TableRow" nowrap>
				<input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
			</td>
			<td align="center" class="TableRow" nowrap><bean:message key="system.item.setid"/></td>
			<td align="center" class="TableRow" nowrap><bean:message key="set.label"/></td>
			<td align="center" class="TableRow" nowrap> <bean:message key="kjg.title.import"/></td>
			<td align="center" class="TableRow" nowrap> <bean:message key="label.kh.edit"/></td>					
		</tr>
		<%int i=0;%>
		<hrms:paginationdb id="element" name="busiMaintenceForm" sql_str="busiMaintenceForm.sql" table="" where_str="busiMaintenceForm.where" columns="busiMaintenceForm.column" order_by="busiMaintenceForm.orderby" pagerows="20" page_id="pagination" indexes="indexes">	
			<%if(i%2==0){ %>
	     <tr class="trShallow" id="<%="a_"+i%>" onclick="changeTrColor('<%="a_"+i%>');">
	     <%} else { %>
	     <tr class="trDeep" id="<%="a_"+i%>" onclick="changeTrColor('<%="a_"+i%>');">
	     <%}%>
		<bean:define id="sysid" name="element" property="fieldsetid"/>
		<bean:define id="sid" name="element" property="id"/>
		<input type="hidden" name="sids" value='${sid}'/>

			<td align="center" class="RecordRow" width="30" nowrap>
				<logic:equal name="busiMaintenceForm" property="userType"  value="1">
            		<logic:equal name="element" property="useflag" value="0">	  
     		   			<hrms:checkmultibox name="busiMaintenceForm" property="pagination.select" value="${sysid}" indexes="indexes"/>&nbsp;
	    			</logic:equal>
	    		</logic:equal>
	    		<logic:equal name="busiMaintenceForm" property="userType" value="0">
	    			<logic:equal name="element" property="useflag" value="0">
	    				<logic:equal name="element" property="ownflag" value="0">
	    					<hrms:checkmultibox name="busiMaintenceForm" property="pagination.select" value="${sysid}" indexes="indexes"/>&nbsp;
	    				</logic:equal>
	    			</logic:equal>
	    		</logic:equal>     		  
			</td>
			<td class="RecordRow" width="60" >
				&nbsp;<bean:write name="element" property="fieldsetid"/>
			</td>
			<%
				DynaBean bean = (DynaBean)pageContext.getAttribute("element");
				String fieldsetid = (String)bean.get("fieldsetid");
			 %>
			<td class="RecordRow">
				&nbsp;<a href="/system/busimaintence/showbusifield.do?b_query=link&encryptParam=<%=PubFunc.encrypt("param=child&fieldsetid="+fieldsetid)%>">
				<bean:write name="element" property="customdesc"/></a>
			</td>
			<td class="RecordRow" align="center" width="60">
			<hrms:priv func_id="3007308">
				<html:img src="/images/export.gif" altKey="kjg.title.import" onclick="inputitem('${sysid}');" border="0" style="cursor:hand"/>
			</hrms:priv>
			</td>
			
			<td class="RecordRow" align="center" width="40">
			<hrms:priv func_id="3007306"> 
				<html:img src="/images/edit.gif"  altKey="label.kh.edit" onclick="updateclass('${sysid}');" border="0" style="cursor:hand"/>
			</hrms:priv>
			</td>
			
		</tr>
		<% i++; %>
		</hrms:paginationdb>
		<html:hidden name="busiMaintenceForm" property="fieldsetid" value='${sysid}'/>
		<html:hidden styleId="smid" name="busiMaintenceForm" property="mid"/>
	</table>
	  <table width="80%" align="center" class="RecordRowP">
			<tr>
				<td  valign="bottom" class="tdFontcolor" >
					<bean:message key="label.page.serial" />
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum" />
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row" />
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page" />
				</td>
				<td align="right" nowrap class="tdFontcolor">
					<p align="right"><hrms:paginationdblink name="busiMaintenceForm" property="pagination" nameId="browseRegisterForm" scope="page">
						</hrms:paginationdblink>
				</td>
			</tr>
			
		</table>
	<table width="80%" align="center">
		<tr>
		<td height="35px;" align="center">
		<hrms:priv func_id="3007305">
		<logic:equal name="busiMaintenceForm" property="id" value="35">
			<BUTTON name='gk' class='mybutton' onclick="newbusitable()"><bean:message key="button.new.add"/></BUTTON>
			&nbsp;
		</logic:equal>
		</hrms:priv>
		<hrms:priv func_id="3007307">
		<logic:equal name="busiMaintenceForm" property="id" value="35">
			<BUTTON name='xggk' class='mybutton' onclick='updatedb()'><bean:message key="button.delete"/></BUTTON>
			&nbsp;
		</logic:equal>
		</hrms:priv>
		<button name="back" class="mybutton" onclick="backtoup()"><bean:message key="button.return" /></button>
		
		</td>
		</tr>
	</table>
</html:form>

<script type="text/javascript" language="javascript">
	save('<bean:write name="busiMaintenceForm" property="setid" filter="true"/>','<bean:write name="busiMaintenceForm" property="setdesc" filter="true"/>','<bean:write name="busiMaintenceForm" property="mid" filter="true"/>','<bean:write name="busiMaintenceForm" property="isrefresh" filter="true"/>');
</script>
