<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript" src="/js/dict.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript" src="/module/utils/js/template.js"></script>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
<script type="text/javascript">
<!--
function initIsvisible()
{
    //var visible=getCookie("isvisible");
   // if(visible=="")
   // {
   //     visible="0";//显示
  //  }
  //  var obj = document.getElementById("visib");
  //  var tab = document.getElementById("tab");
 //   if(visible=="0")
  //  {
   ///    obj.checked=true;
  //     tab.style.display="block";
  //  }
  //  else
   // {
   //     obj.checked=false;
   //     tab.style.display="none";
   // }
   	var point_id="${khFieldForm.point_id}";
    parent.ril_body2.location="/performance/kh_system/kh_field/search_field_grade.do?b_init=init&point_id="+point_id;

}
/**
 * 判断当前浏览器是否为ie6
 * 返回boolean 可直接用于判断 
 * @returns {Boolean}
 */
function isIE6() 
{ 
	if(navigator.appName == "Microsoft Internet Explorer") 
	{ 
		if(navigator.userAgent.indexOf("MSIE 6.0")>0) 
		{ 
			return true;
		}else{
			return false;
		}
	}else{
		return false;
	}
}
 function getCookie(name)
    {
       var strCookie=document.cookie;
       var arrCookie=strCookie.split("; ");
       for(var i=0;i<arrCookie.length;i++)
       { // 遍历cookie数组，处理每个cookie对
          var arr=arrCookie[i].split("=");
          if(arr[0]==name)
          {
              return unescape(arr[1]);
          }
       }
    return "";
    }
    function addCookie(name,value,expireHours)
	{
       var cookieString=name+"="+escape(value);
       //判断是否设置过期时间
       if(expireHours>0)
       {
            var date=new Date();
            date.setTime(date.getTime+expireHours*3600*1000); // 转换为毫秒
            cookieString=cookieString+"; expire="+date.toGMTString();
       }
     document.cookie=cookieString;
    }
 function isvisible(obj)
 {
     var tab = document.getElementById("tab");
     var vi="";
     if(obj.checked)
     {
       vi="0";
       tab.style.display="block";
     }
     else
     {
       vi=1;
       tab.style.display="none";
     }
     addCookie("isvisible",vi,0);
    //self.parent.mil_menu.test(i);
 }
 function refreshIframe(point_id)
 {
     parent.ril_body2.location="/performance/kh_system/kh_field/search_field_grade.do?b_init=init&point_id="+point_id;

     //window.ril_body2.location="/performance/kh_system/kh_field/search_field_grade.do?b_init=init&point_id="+point_id;
 }
 function addOrEdit(type,point_id,id)
 {
    if(parseInt(type)==2)
    {
         changeTrColor(id);
         refreshIframe(point_id);
    }
     var pointsetid=khFieldForm.pointsetid.value;
     var subsys_id=khFieldForm.subsys_id.value;
     if(pointsetid=="-1")
     {
       return;
     }
      var theurl = "/performance/kh_system/kh_field/add_edit_field.do?b_init=init`tabid=1`type="+type+"`point_id="+point_id+"`pointsetid="+pointsetid;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);

    var config = {
   	    width:600,
   	    height:500,
   	    dialogArguments:arguments,
   	    type:'2'
   	}
   	if(!window.showModalDialog)
   		window.dialogArguments = arguments;
   	modalDialog.showModalDialogs(iframe_url,"addOrEdit_id",config,SetFiled_win_ok);
   	
     /* if (window.showModalDialog) {
         var return_vo = window.showModalDialog(iframe_url, arguments,
             "dialogWidth:600px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no");
         SetFiled_win_ok(return_vo);

     } else {
         window.open(iframe_url, arguments, "width=600; height=500;resizable=no;center=yes;scroll=no;status=no");
     } */
 }
 
 function SetFiled_win_ok(return_vo) {
	if(!return_vo){
	    return;
	}

     var hashvo=new ParameterSet();
     hashvo.setValue("id","1");
     var request=new Request({asynchronous:false,onSuccess:no_savepoint,functionId:'9020020206'},hashvo);

     var obj = new Object();
     obj.refresh = return_vo.refresh;
     obj.pid=return_vo.pid;
     if(obj.refresh=="2")
     {
         var currnode=window.parent.parent.frames['mil_menu'].Global.selectedItem;
         currnode.openURL();
     }
 }

 function  closeSetFiled_win() {
    if(Ext.getCmp('SetFiled_win')){
        Ext.getCmp('SetFiled_win').close();
	}

 }
 function no_savepoint(outparameters)
 {
     
 }
 function deleteField()
 {
     var obj=document.getElementsByName("choose");
     var pointsetid = khFieldForm.pointsetid.value;
     var len = obj.length;
     var num=0;
     var ids = "";
     for(var i=0;i<len;i++)
       {
          if(obj[i].checked)
          {
             ids+="/"+obj[i].value;
             num++;
          }
       }
       if(num==0)
       {
         Ext.showAlert("请选择要删除的指标！");
         return;
       }
    
       var hashvo=new ParameterSet();
       hashvo.setValue("ids",ids.substring(1));
       hashvo.setValue("pointsetid",pointsetid);
       hashvo.setValue("subsys_id",'${khFieldForm.subsys_id}');
       hashvo.setValue("opt","1");
       var request=new Request({asynchronous:false,onSuccess:del_check_ok,functionId:'9021001016'},hashvo);    
   
 }
 
  function del_check_ok(outparameters)
 {
     var msg = outparameters.getValue("msg");
     var pointsetid =outparameters.getValue("pointsetid");
     var subsys_id =outparameters.getValue("subsys_id");
     var ids=outparameters.getValue("ids");
     var duxie = outparameters.getValue("duxie");
     if(msg =='1'&& duxie=='1')
     {
    	 Ext.showConfirm("确认删除所选择的指标？",
             function (s) {
                 if (s == "yes") {
               	  var hashvo=new ParameterSet();
                     hashvo.setValue("ids",ids);
                     hashvo.setValue("pointsetid",pointsetid);
                     hashvo.setValue("subsys_id",subsys_id);
                     hashvo.setValue("opt","2");
                     var request=new Request({asynchronous:false,onSuccess:delete_ok,functionId:'9021001016'},hashvo);
                 }
             }
         );
     }
     else if(msg !='1')
     {
    	 Ext.showAlert(msg);
       	 return;
     }
     else if(duxie !='1'){
    	 Ext.showAlert(duxie);
       	 return;
     }
 }
 
 function delete_ok(outparameters)
 {
  var pointsetid =outparameters.getValue("pointsetid");
  khFieldForm.action="/performance/kh_system/kh_field/init_kh_field.do?b_query=link&pointsetid="+pointsetid;
  khFieldForm.target="ril_body1";
  khFieldForm.submit();
 }

function plField() {
    var obj = document.getElementsByName("choose");
    var pointsetid = khFieldForm.pointsetid.value;
    var len = obj.length;
    var num = 0;
    var ids = "";
    for (var i = 0; i < len; i++) {
        if (obj[i].checked) {
            ids += obj[i].value + "/";
            num++;
        }
    }
    if (num == 0) {
    	Ext.showAlert("请选择另存的指标！");
        return;
    }

    var infos = new Array();
    infos[0] = "-1";
    infos[1] = '${khFieldForm.subsys_id}';
    var thecodeurl = "/performance/kh_system/kh_field/batch_save_field.do?br_select=query";
    var iframe_url = "/general/query/common/iframe_query.jsp?src=" + thecodeurl;
	
    var config = {
   	    width: 470,
   	    height: 370,
   	    dialogArguments: infos,
   	    type: '2'
   	}
   	if(!window.showModalDialog)
   		window.dialogArguments = infos;
   	modalDialog.showModalDialogs(iframe_url,"plField_id",config,plField_check_window_ok);
   	
    /* if (window.showModalDialog) {
        var return_vo = window.showModalDialog(iframe_url, infos,
            "dialogWidth:470px; dialogHeight:370px;resizable:no;center:yes;scroll:no;status:no");
        plField_check_window_ok(return_vo);

    } else {
        window.dialogArguments = infos;
        window.open(iframe_url, infos, "width=470; height=370;resizable=no;center=yes;scroll=no;status=no");
    } */

}
	
	function plField_check_window_ok(return_vo) {
        if(return_vo)
        {
            var obj = document.getElementsByName("choose");
            var len = obj.length;
            var num = 0;
            var ids = "";
            for (var i = 0; i < len; i++) {
                if (obj[i].checked) {
                    ids += obj[i].value + "/";
                    num++;
                }
            }
            if(return_vo=='undefined'||return_vo=='')
            {
                return;
            }
            var infoArray = return_vo.split(",");

            var subsys_id = document.getElementsByName("subsys_id")[0].value;
            
            var hashvo=new ParameterSet();
            hashvo.setValue("point_id",ids);
            hashvo.setValue("pointsetid",infoArray[0]);
            hashvo.setValue("subsys_id",subsys_id);
            var request=new Request({asynchronous:false,onSuccess:plField_check_ok,functionId:'9021001051'},hashvo);
        }
    }
	
       
function plField_check_ok(outparameters){

  var msg=outparameters.getValue("msg");
  if(msg!=""){
	  Ext.showAlert(msg);
  	  return;
  }

  var pointsetid =outparameters.getValue("pointsetid");
  var isgs =outparameters.getValue("isgs");
  if(isgs=='2'){
	  Ext.showAlert("您没有该指标类别的权限！");
  	 return;
  }else{
  
	  Ext.showAlert("批量另存成功！");
	  khFieldForm.action="/performance/kh_system/kh_field/init_kh_field.do?b_query=link";
	  khFieldForm.target="ril_body1";
	  khFieldForm.submit();
  }


 }

 function sort(pointsetid,pointCount)
 {
    if(pointCount=='0')
    {
    	Ext.showAlert("没有指标不能调整顺序！");
	    return;
  	}
  	var theurl = "/performance/kh_system/kh_field/sort_field_class.do?b_query=link`pointsetid="+pointsetid;
  	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
	
  	var config = {
	    width:400,
	    height:380,
	    dialogArguments:arguments,
	    type:'2'
	}
  	if(!window.showModalDialog)
  		window.dialogArguments = arguments;
	modalDialog.showModalDialogs(iframe_url,"",config,Change_sort_OK);
  /* 
     if (window.showModalDialog) {
         var return_vo= window.showModalDialog(iframe_url, arguments,
             "dialogWidth:360px; dialogHeight:370px;resizable:no;center:yes;scroll:yes;status:no");
         Change_sort_OK(return_vo);

     } else {
         window.open(iframe_url, arguments, "width=400; height=370;resizable=no;center=yes;scroll=no;status=no");
     } */
 }
 function Change_sort_OK(return_vo) {
     if(return_vo==null)
         return;
     var vo = new Object();
     vo.ids = return_vo.ids;
     vo.pointsetid = return_vo.pointsetid;
     vo.subsys_id = return_vo.subsys_id;
     vo.sorttype=return_vo.sorttype
     var hashvo=new ParameterSet();
     hashvo.setValue("newsortvo",vo);
     var request=new Request({asynchronous:false,onSuccess:sort_ok,functionId:'9021001007'},hashvo);
 }
 function sort_ok(outparameters)
 {
    var pointsetid=outparameters.getValue("pointsetid");
    var subsys_id=outparameters.getValue("subsys_id");
    khFieldForm.action="/performance/kh_system/kh_field/init_kh_field.do?b_query=link&pointsetid="+pointsetid+"&subsys_id="+subsys_id;
    khFieldForm.target="ril_body1";
    khFieldForm.submit();
 }
 function changeTrColor(id)
 {
    var ob=document.getElementById("tb");
    var j=ob.rows.length;
    for(var i=1;i<j;i++)
    {
         var o=ob.rows[i].id;
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
 function allSelectOptions(obj)
 {
     var arr=document.getElementsByName("choose");
     if(arr)
     {
       for(var i=0;i<arr.length;i++)
       {
         if(obj.checked)
         {
            arr[i].checked=true;
         }
         else
         {
            arr[i].checked=false;
         }
       }
     }
 }
 
 // 能力课程
 function selectAbilityClass(point_id,pointsetid,subsys_id)
 {		 
	 window.parent.location="/performance/kh_system/kh_field/init_kh_field.do?b_search=search&personStation=perStation&point_id="+point_id+"&pointsetid="+pointsetid+"&subsys_id="+subsys_id;

 }
 
//-->
</script>

<html:form action="/performance/kh_system/kh_field/init_kh_field">

<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top:1px;">

<tr>
<td colspan="2" width='100%' style="border-top: 5px solid #FFFFFF;"  >
<table width="99%" border="0" cellspacing="0" id="tb" align="left" cellpadding="0" class="ListTable">
   	  <thead>
		 <tr>
			 <td align="center" class="TableRow">
			 	<input type="checkbox" name="chk" value="1" onclick="allSelectOptions(this);"/>
			 </td>
			 <td align="center" class="TableRow" nowrap>
			  	<bean:message key="kh.field.num"/>
			 </td>
			 <td align="center" class="TableRow" nowrap>
			  	<bean:message key="kh.field.classname"/>
			 </td>			 
			 <logic:notEqual name="khFieldForm" property="subsys_id" value="35">
				 <td align="center" class="TableRow" nowrap>
				  	<bean:message key="kh.field.type"/>
				 </td>
			 </logic:notEqual>
			 
			 <hrms:priv func_id="360101">
				 <logic:equal name="khFieldForm" property="subsys_id" value="35">
					 <td align="center" class="TableRow" nowrap>
					  	<bean:message key="kh.field.abilitycourse"/>
					 </td>
				 </logic:equal>
			 </hrms:priv>
			 
			 <td align="center" class="TableRow" nowrap>
			  	<bean:message key="kh.field.flag"/>
			 </td>
			 <td align="center" class="TableRow" nowrap>
			  	<bean:message key="label.edit.user"/>
			 </td>
			 <td align="center" class="TableRow" nowrap>
			  	<bean:message key="jx.khplan.saveas"/>
			 </td>
		 </tr>
	 </thead>
	 <% int i=0; %>
	 <hrms:extenditerate id="element" name="khFieldForm" property="fieldinfolistForm.list" indexes="indexes"  pagination="fieldinfolistForm.pagination" pageCount="10" scope="session">
		 <%if(i%2==0){ %>
	     <tr class="trShallow" id="<bean:write name="element" property="point_id"/>" onclick="changeTrColor('<bean:write name="element" property="point_id"/>');">
	     <%} else { %>
	     <tr class="trDeep" id="<bean:write name="element" property="point_id"/>" onclick="changeTrColor('<bean:write name="element" property="point_id"/>');">
	     <%}%>
	     <td align="center" class="RecordRow">
	     <input type="checkbox" width="3%" name="choose" value="<bean:write name="element" property="point_id"/>"/>
	     </td>
	     <td align="left" class="RecordRow" onclick="refreshIframe('<bean:write name="element" property="point_id"/>');">
	     <bean:write name="element" property="point_id"/>
	     </td>
	     <td width="67%" align="left" class="RecordRow" onclick="refreshIframe('<bean:write name="element" property="point_id"/>');">
	     <bean:write name="element" property="pointname" filter="false"/>
	     </td>	     
	     <logic:notEqual name="khFieldForm" property="subsys_id" value="35">
		     <td width="10%" align="center" class="RecordRow" onclick="refreshIframe('<bean:write name="element" property="point_id"/>');">
		     	<bean:write name="element" property="pointkind"/>
		     </td>
	     </logic:notEqual>
	     
	     <hrms:priv func_id="360101">
		     <logic:equal name="khFieldForm" property="subsys_id" value="35">	     	 											     	
			     <td width="10%" align="center" class="RecordRow" onclick="refreshIframe('<bean:write name="element" property="point_id"/>');">
			     <img src="/images/book.gif" BORDER="0" style="cursor:hand;" onclick="selectAbilityClass('<bean:write name="element" property="point_id"/>','${khFieldForm.pointsetid}','${khFieldForm.subsys_id}');">
			     </td>
		     </logic:equal>
	     </hrms:priv>
	     
	     <td width="10%" align="center" class="RecordRow" onclick="refreshIframe('<bean:write name="element" property="point_id"/>');" nowrap>
	     <bean:write name="element" property="validflag"/>
	     </td>

	     <td width="5%" align="center" onclick="refreshIframe('<bean:write name="element" property="point_id"/>');" class="RecordRow" nowrap>
	    	      <logic:equal value="1" name="element" property="duxie">
	     <img src="/images/edit.gif" border="0" style="cursor:hand;" onclick="addOrEdit('2','<bean:write name="element" property="point_id"/>','<bean:write name="element" property="point_id"/>');"/>
	         </logic:equal>
	     </td>
	  
	     <td width="5%" align="center" onclick="refreshIframe('<bean:write name="element" property="point_id"/>');" class="RecordRow" nowrap>
	     <logic:equal value="1" name="element" property="isgs">
	     <img src="/images/edit.gif" border="0" style="cursor:hand;" onclick="addOrEdit('3','<bean:write name="element" property="point_id"/>','<bean:write name="element" property="point_id"/>');"/>
	      </logic:equal>
	     </td>
	     <% i++; %>
	 </hrms:extenditerate>
</table>
</td>
</tr>

<tr>
<td colspan="2">
<table  width="99%" align="left" class="RecordRowP">
		<tr>
		   <td valign="bottom" class="tdFontcolor" nowrap>
		            <bean:message key="label.page.serial"/>
		   ${khFieldForm.fieldinfolistForm.pagination.current}
					<bean:message key="label.page.sum"/>
		   ${khFieldForm.fieldinfolistForm.pagination.count}
					<bean:message key="label.page.row"/>
		   ${khFieldForm.fieldinfolistForm.pagination.pages}
					<bean:message key="label.page.page"/>
		   </td>
		   <td align="right" class="tdFontcolor" nowrap>
		   <p align="right">
            <hrms:paginationlink name="khFieldForm" property="fieldinfolistForm.pagination" nameId="fieldinfolistForm" propertyId="fieldinfolistProperty">
		   </hrms:paginationlink>
		   </p>
		   </td>
		</tr> 
</table>
</td>
</tr>


<tr>
<td width="50%">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top:3px;">
<tr style="height:35">
<td align="center" width="30%">
<html:hidden name="khFieldForm" property="pointsetid"/>
<html:hidden name="khFieldForm" property="subsys_id"/>
<logic:equal value="-1" name="khFieldForm" property="pointsetid">
 <input type="button" name="new" disabled value='<bean:message key="kh.field.new"/>' class="mybutton" onclick="addOrEdit('1','','');"/>
<input type="button" name="ne" disabled value='<bean:message key="kh.field.delete"/>' class="mybutton" onclick="deleteField();"/>
<input type="button" name="n" disabled value='<bean:message key="kh.field.sort"/>' class="mybutton" onclick="sort('${khFieldForm.pointsetid}','${khFieldForm.pointCount}');"/>
<input type="button" name="nee"  disabled value='<bean:message key="button.batch.assave"/>' class="mybutton" onclick="plField();"/>
</logic:equal>
<logic:notEqual value="-1" name="khFieldForm" property="pointsetid">
 <input type="button" name="new" value='<bean:message key="kh.field.new"/>' class="mybutton" onclick="addOrEdit('1','','');"/>
<input type="button" name="ne" value='<bean:message key="kh.field.delete"/>' class="mybutton" onclick="deleteField();"/>
<input type="button" name="n" value='<bean:message key="kh.field.sort"/>' class="mybutton" onclick="sort('${khFieldForm.pointsetid}','${khFieldForm.pointCount}');"/>
<input type="button" name="nee"  value='<bean:message key="button.batch.assave"/>' class="mybutton" onclick="plField();"/>
</logic:notEqual>
<logic:equal value="35" name="khFieldForm" property="subsys_id">
         <hrms:tipwizardbutton flag="capability" target="il_body" formname="khFieldForm"/>  
</logic:equal>
<logic:equal value="33" name="khFieldForm" property="subsys_id">
         <hrms:tipwizardbutton flag="performance" target="il_body" formname="khFieldForm"/>  
</logic:equal>

</td>


</tr>
</table>
</td>
<!--  
<td width="50%">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td>
<input type="checkbox" id="visib" name="visiblebd" value="0" onclick="isvisible(this);"/><bean:message key="kh.field.visible"/>
</td>
</tr>
</table>
</td>
-->

</tr>
</table>

</html:form>
<script language="javascript">
  initDocument();
  initIsvisible();
  changeTrColor("${khFieldForm.point_id}");
</script>
