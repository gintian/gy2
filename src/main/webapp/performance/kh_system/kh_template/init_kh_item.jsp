<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,				 
				 com.hrms.hjsj.sys.VersionControl,
				 com.hrms.struts.valueobject.UserView,				 
				 com.hrms.struts.constant.WebConstant" %>

<%	
	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	int versionFlag = 1;
	if (userView != null)
		versionFlag = userView.getVersion_flag(); // 1:专业版 0:标准版

%>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script type="text/javascript">
<!--
var opttype='-1';
var beforeitemid="-1";
var beforepointid="-1";
var beforetype=0;//=1是项目=2是指标
var isHaveItem = "${khTemplateForm.isHaveItem}";//模板是否已经有项目=0有项目=1还没有项目 
var isUsed="${khTemplateForm.isUsed}";//模板是否被使用=0已经使用=1未使用
var isVisible="${khTemplateForm.isVisible}";
var score_str="${khTemplateForm.score_str}";
var t_type="${khTemplateForm.t_type}";



function changeBgColor(id)
{
   var e = document.getElementById(id);
   e.style.backgroundColor="#98C2E8";
  
}
function goBackBgColor(id)
{
   var e = document.getElementById(id);
   e.style.backgroundColor="white";
}
function changeColor(id,type)//type=1是项目，=2是指标
{
     if(trim(beforeitemid).length>0&&document.getElementById(beforeitemid)!=null)
     {
          document.getElementById(beforeitemid).className='RecordRow';
     }
     if(trim(beforepointid).length>0&&document.getElementById(beforepointid)!=null)
     {
          document.getElementById(beforepointid).className='RecordRow';
     }
     beforetype=type;
     if(type=='1')
        beforeitemid=id;
     else
        beforepointid=id;
     var e = document.getElementById(id);
     opttype=type;
     e.className+=" selectedBackGroud";
}
// type=0 同级项目，=1下级项目，2插入项目
function addItem(type)
{
   Element.hide('date_panel');
   if(isUsed=='0'&&type!='3')
   {
      alert("模板已被使用，不能修改！");
      return;
   }
   if(isUsed=='2'){
       alert("模板已被职称评审模块的评分环节使用，不能修改！");
       return;
   }
   if(type=='3')
   {
      if(opttype!='1')
      {
         alert("请选择考核项目后进行操作！");
         return;
      }
    var thecodeurl="/performance/kh_system/kh_template/init_kh_item.do?b_rank=query`itemid="+beforeitemid+"`temp_id=${khTemplateForm.templateid}`isrefresh=1"; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);



       if(window.showModalDialog){
           var points= window.showModalDialog(iframe_url, null,
               "dialogWidth:500px; dialogHeight:360px;resizable:no;center:yes;scroll:yes;status:no");
           template_item_rank_ok(points);
       }else{
           var config = {
               width:500,
               height:380,
               type:'1',
			   id:'template_item_rank_win'
           };
           modalDialog.showModalDialogs(iframe_url,'template_item_rank_win',config);
       }


   }
   else
   {
      if(beforeitemid=='-1'&&beforepointid=='-1'&&isHaveItem=='0')
      {
    
        alert("请选择考核项目或者考核指标后进行操作！");
       return;
      }
      var newName=prompt("请输入考核项目:","");
      if(newName==null)
          return;
      if(trim(newName)=='')
      {
         alert("项目名称不能为空！");
         return;
      }
      var hashvo=new ParameterSet();
      hashvo.setValue("name",getEncodeStr(newName));
      hashvo.setValue("isHaveItem","${khTemplateForm.isHaveItem}");
      hashvo.setValue("opt",type);
      hashvo.setValue("type",beforetype);
      hashvo.setValue("itemid",beforeitemid);
      hashvo.setValue("pointid",beforepointid);
      hashvo.setValue("id","${khTemplateForm.templateid}");
      hashvo.setValue("subsys_id","${khTemplateForm.subsys_id}");
      var request=new Request({asynchronous:false,onSuccess:additem_ok,functionId:'9021001032'},hashvo); 
   }
}
function additem_ok(outparameters)
{
    window.tempObj = undefined;
   var subsys_id =outparameters.getValue("subsys_id");
   var templateid = outparameters.getValue("templateid");
   var scrollHeight=document.body.scrollHeight;
   khTemplateForm.action="/performance/kh_system/kh_template/init_kh_item.do?b_query=link&templateid="+templateid+"&subsys_id="+subsys_id+"&isVisible="+isVisible+"&scrollHeight="+scrollHeight;
   khTemplateForm.submit();
}

function template_item_rank_ok(points) {
    if(!window.showModalDialog) {
        Ext.getCmp('template_item_rank_win').close();
    }

    if(points)
    {
        var scrollHeight=document.body.scrollHeight;
        khTemplateForm.action="/performance/kh_system/kh_template/init_kh_item.do?b_query=link&templateid=${khTemplateForm.templateid}&subsys_id=${khTemplateForm.subsys_id}&isVisible="+isVisible+"&scrollHeight="+scrollHeight;
        khTemplateForm.submit();
    }
}
function del()
{
     Element.hide('date_panel');
    if(isUsed=='0')
    {
      alert("模板已被使用，不能修改！");
      return;
    }
    if(isUsed=='2'){
        alert("模板已被职称评审模块的评分环节使用，不能修改！");
        return;
    }
    if(beforeitemid=='-1'&&beforepointid=='-1'&&isHaveItem=='0')
    {
       alert("请选择考核项目或者考核指标后进行操作！");
       return;
    }
     var flag=false;
     if(beforetype=='1')
     {
     	var beforeItemId = document.getElementById(beforeitemid).innerHTML;
     	beforeItemId = beforeItemId.replace(/&nbsp;/ig, " ");
        if(confirm("确认要删除模板项目\""+beforeItemId+"\"及其包含的指标吗？")){
            flag=true;
        }
     }
     else if(beforetype=='2')
     {
       if(confirm("确认要删除指标\""+document.getElementById(beforepointid).innerHTML+"\"吗？")){      
           flag=true;
        }
     }
     if(flag)
     {
         var hashvo=new ParameterSet();
         hashvo.setValue("type",beforetype);
         hashvo.setValue("itemid",beforeitemid);
         hashvo.setValue("pointid",beforepointid);
         hashvo.setValue("id","${khTemplateForm.templateid}");
         hashvo.setValue("subsys_id","${khTemplateForm.subsys_id}");
         var request=new Request({asynchronous:false,onSuccess:additem_ok,functionId:'9021001034'},hashvo); 
     }   
}
//type=1增加=2插入
function addPoint(type)
{
    Element.hide('date_panel');
    if(isUsed=='0')
    {
      alert("模板已被使用，不能修改！");
      return;
    }
    if(isUsed=='2'){
        alert("模板已被职称评审模块的评分环节使用，不能修改！");
        return;
    }
    if(type=='1'&&beforeitemid=='-1')
    {
      alert("请选择考核项目！");
      return;
    }
    if((beforetype=='0'||beforetype=='1')&&type=='2')
    {
        alert("请选择指标，以确定要插入指标的位置！");
        return;
    }
    var infos=new Array();
	infos[0]="${khTemplateForm.templateid}";
	infos[1]="${khTemplateForm.subsys_id}";

	var thecodeurl="/performance/kh_system/kh_template/init_kh_item.do?br_selectpoint=query`type="+type;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);

    /* if(window.showModalDialog){
        var points= window.showModalDialog(iframe_url, infos,
            "dialogWidth:450px; dialogHeight:360px;resizable:no;center:yes;scroll:no;status:no");
        addpoint_ok(points,type);
    }else{
        dialogArguments=infos;
        window.open(iframe_url, arguments, "width=450; height=360;resizable=no;center=yes;scroll=no;status=no");
    } */
    
    var config = {
        width:450,
        height:360,
        type:'2',
        dialogArguments:infos
    }
    if(!window.showModalDialog){
        window.dialogArguments = infos;
    }
    window.tempObj = {};
    window.tempObj.type = type;
    modalDialog.showModalDialogs(iframe_url,'addpoint_ok_window',config, addpoint_ok);
}

function addpoint_ok(points) {
    if(points)
    {

        if(points=='undefined'||points=='')
        {
            return;
        }
        var hashvo=new ParameterSet();
        hashvo.setValue("beforetype",beforetype);
        hashvo.setValue("type", window.tempObj.type);
        hashvo.setValue("itemid",beforeitemid);
        hashvo.setValue("point",beforepointid);
        hashvo.setValue("id","${khTemplateForm.templateid}");
        hashvo.setValue("subsys_id","${khTemplateForm.subsys_id}");
        hashvo.setValue("pointids",points);
        var request=new Request({asynchronous:false,onSuccess:additem_ok,functionId:'9021001033'},hashvo);
    }
}

function save(str,status,item_rank)
{
  var s_str="";
  var r_str="";
  var arr = str.split(",");
  for(var i=0;i<arr.length;i++)
  {
    if(arr[i]==null||trim(arr[i]).length==0)
     continue;
     if(document.getElementById("s_"+arr[i])!=null)
     {
         s_str+=","+arr[i]+"/"+document.getElementById("s_"+arr[i]).value;
     }
     if(status=='1')
     {
         if(document.getElementById("r_"+arr[i])!=null)
         {
             r_str+=","+arr[i]+"/"+document.getElementById("r_"+arr[i]).value;
        }
     }
  }
  var i_s_str="";
  var i_r_str="";
  if(trim(item_rank).length>0)
  {
        var i_arr=item_rank.split(",");
        for(var j=0;j<i_arr.length;j++)
        {
            if(i_arr[j]==null||trim(i_arr[j]).length==0)
               continue;
            if(document.getElementById("si_"+i_arr[j])!=null)
            {
                i_s_str+=","+i_arr[j]+"/"+document.getElementById("si_"+i_arr[j]).value;
            }
             if(status=='1')
             {
                 if(document.getElementById("ri_"+i_arr[j])!=null)
                 {
                    i_r_str+=","+i_arr[j]+"/"+document.getElementById("ri_"+i_arr[j]).value;
                 }
             }
        }
  }
  var hashvo=new ParameterSet();
  hashvo.setValue("s_str",s_str);
  hashvo.setValue("r_str",r_str);
  hashvo.setValue("i_s_str",i_s_str);
  hashvo.setValue("i_r_str",i_r_str);
  hashvo.setValue("status",status);
  hashvo.setValue("id","${khTemplateForm.templateid}");
  hashvo.setValue("subsys_id","${khTemplateForm.subsys_id}");
  var request=new Request({asynchronous:false,onSuccess:save_ok,functionId:'9021001037'},hashvo); 
}
function save_ok(outparameters)
{  
   var subsys_id =outparameters.getValue("subsys_id");
   var templateid = outparameters.getValue("templateid");
   var msg = getDecodeStr(outparameters.getValue("msg"));
   if(msg=='ok')
   {
   var scrollHeight=document.body.scrollHeight;
     khTemplateForm.action="/performance/kh_system/kh_template/init_kh_item.do?b_query=link&templateid="+templateid+"&subsys_id="+subsys_id+"&isVisible="+isVisible+"&scrollHeight="+scrollHeight;
     khTemplateForm.submit();
   } else
   {
      alert(msg);
      return;
   }
   
}
function checkKeyCode()
{
   var code=window.event.keyCode;
    var ret=true;
    if(code==8||code==46||code==9||code==190||code==110||code==13)
    {
        if(code==13)
        window.event.keyCode=9;
    }
   else if(96<=code&&code<=105)
   {
      
   }else if(48<=code&&code<=57)
   {
   }
   else
   { 
        if((window.event.shiftKey)&&(code==48||code==49||code==57||code==56||code==187))
        {
        }
        else if(window.event.shiftKey&&code==189)
        {
           window.event.returnValue=false;
        }
        else if(code==189||code==109)
        {
        }
        else
        {
           window.event.returnValue=false;
        }
     
   }
   //onBlur:当失去输入焦点后产生该事件
   //onFocus:当输入获得焦点后，产生该文件
   
}
var beforeValue;
function saveBeforeValue(obj)
{
  beforeValue=obj.value;
}
function checkValue(obj)
{
     if(parseInt(obj.value)>1)
     {
          alert("权重值无效！");
          obj.value=beforeValue;
          return;
     }
}
function gaibian(itemid,type)
{
   var TDElement=document.getElementById(itemid);
   var a = document.getElementById(itemid).value;
   if(document.getElementById("T_"+itemid)==null)
   {
       TDElement.innerHTML="<input type='text' onblur='savedesc(\""+itemid+"\");' name='tt' id='T_"+itemid+"' value='"+TDElement.innerText+"'/>";
       document.getElementById("T_"+itemid).focus();
   }
       
}
function savedesc(itemid)
{
     
      var TDElement=document.getElementById(itemid);
      var InputElement=document.getElementById("T_"+itemid);
       if(getEncodeStr(InputElement.value).length==0)
      {
         alert("项目名称不能为空！");
         return;
      }
      //TDElement.innerHTML=InputElement.value;
      var hashvo=new ParameterSet();
    hashvo.setValue("itemid",itemid);
    hashvo.setValue("itemdesc",getEncodeStr(InputElement.value));
    var request=new Request({asynchronous:false,onSuccess:edit_ok,functionId:'9021001042'},hashvo); 
}
function edit_ok(outparameters)
{
   var itemid=outparameters.getValue("itemid");
   var itemdesc=outparameters.getValue("itemdesc");
  var TDElement=document.getElementById(itemid);
   TDElement.innerHTML=getDecodeStr(itemdesc);
}
function clearValue(value,id)
{
    if(value==0)
    {
       document.getElementById(id).value='';
    }
}
function returnFlowPhoto()
{

	if("${khTemplateForm.subsys_id}"=="35"){
		 khTemplateForm.action="/general/tipwizard/tipwizard.do?br_capability=link";
	}else{
		 khTemplateForm.action="/general/tipwizard/tipwizard.do?br_performance=link";
	}
  
   khTemplateForm.target="il_body";
   khTemplateForm.submit();
}
function buttonOK(name,id)
{

    //兼容处理新版工作计划架空页面的关联计划的功能，新版不能使用showdialog
	if(parent.parent.parent.Ext && parent.parent.parent.Ext.getCmp("template_win")){
		var template_win = parent.parent.parent.Ext.getCmp("template_win");
		//这里相当于给监控页面（workPlanHr.js）的window赋了一个全局变量，examPlanAdd.js中Ext.window的close事件回调时使用
		parent.parent.parent.return_vo_template = name+","+id;
		template_win.close();
	}else if(window.showModalDialog){
	   parent.parent.returnValue=name+","+id;
	    window.close();
	}else if(window.top.opener.getTemplate_ok){
        window.top.opener.getTemplate_ok(name+","+id);
        window.open("about:blank","_top").close();
	}
}
//-->
</script>
<style>

.ListTable_self {
    BACKGROUND-COLOR: #FFFFFF;
    BORDER-BOTTOM: medium none; 
    BORDER-COLLAPSE: collapse; 
    BORDER-LEFT: medium none; 
    BORDER-RIGHT: medium none; 
    BORDER-TOP: medium none; 
    
 }   

.RecordRow_self {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;

}

.trDeep_self {  
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;
	background-color: #DDEAFE; 
	}
.Input_self{                                                                    
  font-size:   12px;                                              
  font-weight:   bold;                                                          
  background-color:   #FFFFFF;         
  letter-spacing:   1px;                      
  text-align:   right;                        
  height:   90%;                                    
  width:   90%;                                    
  border:   1px   solid   #94B6E6;           
  cursor:   hand;     
  padding-top:6px;                               
  }   
	
</style>
<hrms:themes />
<html:form action="/performance/kh_system/kh_template/init_kh_item">
  <logic:equal value="0" name="khTemplateForm" property="t_type">
  <P ALIGN="CENTER"><strong>请选择左侧的模板，不要选择模板分类。</strong></P>
  </logic:equal>
  <logic:equal value="1" name="khTemplateForm" property="t_type">
   <table width="80%" border="0" align="center" style="margin:5px auto 0 auto;">
   <logic:equal value="2" property="isVisible" name="khTemplateForm">
   <logic:equal value="0" name="khTemplateForm" property="planStatus">
       <tr>
       <td align="LEFT" >
        <strong>${khTemplateForm.templateid}&nbsp;&nbsp;${khTemplateForm.tname}</strong>
       </td>
       </tr>
   </logic:equal>
   </logic:equal>
   <tr>
  <td align="left">
  ${khTemplateForm.tableHtml}
  </td>
   </tr>
   <logic:equal value="2" property="isVisible" name="khTemplateForm">
   <tr>
   <td align="center" style="padding-top:2px;height:30px;">
   <logic:equal value="1" name="khTemplateForm" property="planStatus">
   <input type="button" name="dd" value="<bean:message key="button.ok"/> " class="mybutton" onclick="buttonOK('${khTemplateForm.tname}','${khTemplateForm.templateid}');"/>
   </logic:equal>
   <input type="button" name="aa" value="<bean:message key="button.close"/> " class="mybutton" style="margin-top:0px;" onclick="closeWin()"/>
   </td>
   </tr>
   </logic:equal>
   </table>
   <logic:equal value="1" name="khTemplateForm" property="isHaveItem">
   <div id="date_panel" style="background:#ffffff;border:1px groove black;width:105;height:20 ">
   </logic:equal>
   <logic:notEqual value="1" name="khTemplateForm" property="isHaveItem">
   <div id="date_panel" style="background:#ffffff;border:1px groove black;width:105;height:100 ">
   </logic:notEqual>
   			<table id="date_box" name="date_box">    
			   <tr id='b_1' onclick="addItem(0);" style="cursor:hand" onMouseOver="changeBgColor('b_1')" onMouseOut="goBackBgColor('b_1')">
		    	   <td><img src="/images/add.gif" border="0" width="10" height="10"/>&nbsp;<bean:message key="label.kh.new.tjxm"/> </td>
			   </tr>
			   <tr onclick="addItem(1);" id='b_2' style="cursor:hand" onMouseOver="changeBgColor('b_2')" onMouseOut="goBackBgColor('b_2')">
			   <td >
			 <img src="/images/add.gif" border="0" width="10" height="10"/>&nbsp;<bean:message key="label.kh.new.xjxm"/>
			    </td>
			    </tr>
			     <tr onclick="addItem(2);" id='b_3' style="cursor:hand" onMouseOver="changeBgColor('b_3')" onMouseOut="goBackBgColor('b_3')">
			   <td >
			&nbsp;&nbsp; <bean:message key="label.kh.crxm"/>
			    </td>
			    </tr>
			    
			    <% if(versionFlag==1){%>
			    <logic:equal value="0" name="khTemplateForm" property="status">
			    	<logic:notEqual value="35" name="khTemplateForm" property="subsys_id">
			     		<tr onclick="addItem(3);" id='b_7' style="cursor:hand" onMouseOver="changeBgColor('b_7')" onMouseOut="goBackBgColor('b_7')">
			   				<td >
							&nbsp;&nbsp; <bean:message key='lable.kh.itemrank'/>
			    			</td>
			    		</tr>
			    	</logic:notEqual>
			    </logic:equal>
			    <% }%>
			    
			     <tr onclick="addPoint('1');" id='b_4' style="cursor:hand" onMouseOver="changeBgColor('b_4')" onMouseOut="goBackBgColor('b_4')">
			   <td >
			<img src="/images/add.gif" border="0" width="10" height="10"/>&nbsp;<bean:message key="label.kh.new.field"/>
			    </td>
			    </tr>
			     <tr onclick="addPoint('2');"  id='b_5' style="cursor:hand" onMouseOver="changeBgColor('b_5')" onMouseOut="goBackBgColor('b_5')">
			   <td>
			&nbsp;&nbsp; <bean:message key="label.kh.crzb"/>
			    </td>
			    </tr>
			     <tr id='b_6' onMouseOver="changeBgColor('b_6')" onMouseOut="goBackBgColor('b_6')" onclick="del();" style="cursor:hand">
			   <td>
			<img src="/images/del.gif" border="0" width="10" height="10"/>&nbsp;<bean:message key="label.kh.del"/>
			    </td>
			    </tr> 
			    </table>
         </div>
<script language="javascript">
	function closeWin(){
		// 兼容 showModalDialog形式和Ext.window两种弹出形式的关闭 chent 20171122 add
		if(parent.parent.parent.Ext && parent.parent.parent.Ext.getCmp("template_win")){
			var template_win = parent.parent.parent.Ext.getCmp("template_win");
			template_win.destroy();
		} else {
            parent.parent.window.close();
		}
	}
	Element.hide('date_panel');
  <% 
  String scrollHeight=request.getParameter("scrollHeight");//刷新页面后定位 滚动条位置，是页面保持原先的滚动条位置 zzk
  if(scrollHeight!=null){
%>
document.body.scrollTop=<%=scrollHeight%>;
<%  
  }%>
</script>
</logic:equal>
   </html:form>
