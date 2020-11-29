<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<hrms:themes></hrms:themes>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script type="text/javascript">
function to_set_db()
{
    var target_url="/sys/export/SetHrSyncDb.do?b_query=link";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
    var dw=300,dh=260,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    /*
    var return_vo= window.showModalDialog(iframe_url,window, 
         "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null)
    {
        var in_obj=document.getElementById('db');  
        in_obj.innerHTML=return_vo.mess;
    }
    */
    //改用ext 弹窗显示  wangb 20190320
    var win = Ext.create('Ext.window.Window',{
			id:'select_db',
			title:'请选择',
			width:dw+20,
			height:dh+40,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.return_vo)
    				{
        				var in_obj=document.getElementById('db');  
        				in_obj.innerHTML=this.return_vo.mess;
    				}
				}
			}
	});
}

function to_set_field(type)
{
    //var target_url="/sys/export/SearchHrSyncFiled.do?b_set=link";
    //var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
    //var return_vo= window.showModalDialog(iframe_url,1, 
    //    "dialogWidth:540px; dialogHeight:390px;resizable:no;center:yes;scroll:yes;status:yes;scrollbars:yes");
    //if(return_vo!=null)
    //{
    //  var in_obj=document.getElementById('field');  
    //  in_obj.innerHTML=return_vo.mess;
    //  var in_obj2=document.getElementById('codefield');  
    //  in_obj2.innerHTML=return_vo.mess2;
    //}
    hrSyncForm.action="/sys/export/SearchHrSyncFiled.do?b_search=link&type="+type;
    hrSyncForm.submit();

}
function to_set_codefield()
{
    var target_url="/sys/export/SearchHrSyncFiled.do?b_codefield=link";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
    var dw=540,dh=390,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    /*
    var return_vo= window.showModalDialog(iframe_url,1, 
         "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null)
    {
        var in_obj=document.getElementById('codefield');  
        in_obj.innerHTML=return_vo.mess;
    }
	*/
	//改用ext 弹窗显示  wangb 20190318
	var win = Ext.create('Ext.window.Window',{
			id:'select_codefield',
			title:'请选择',
			width:dw+20,
			height:dh+20,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.return_vo)
    				{
        				document.getElementById('codefield').innerHTML=this.return_vo.mess.replace('<br>','');
    				}
				}
			}
	});  
	
}
function to_set_orgfield()
{
    var target_url="/sys/export/SearchHrSyncFiled.do?b_orgfield=link";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
    var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:540px; dialogHeight:390px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
    if(return_vo!=null)
    {
        var in_obj=document.getElementById('orgfield');  
        in_obj.innerHTML=return_vo.mess;
        var in_obj2=document.getElementById('orgcodefield');  
        in_obj2.innerHTML=return_vo.mess2;
    }

}
function to_set_orgcodefield()
{
    var target_url="/sys/export/SearchHrSyncFiled.do?b_orgcodefield=link";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
    var dw=540,dh=390,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    /*
    var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null)
    {
        var in_obj=document.getElementById('orgcodefield');  
        in_obj.innerHTML=return_vo.mess;
    }
    */
    //改用ext 弹窗显示  wangb 20190318
	var win = Ext.create('Ext.window.Window',{
			id:'select_orgcodefield',
			title:'请选择',
			width:dw+30,
			height:dh+30,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.return_vo)
    				{
        				document.getElementById('orgcodefield').innerHTML=this.return_vo.mess.replace('<br>','');
    				}
				}
			}
	}); 

}
function to_set_postcodefield()
{
    var target_url="/sys/export/SearchHrSyncFiled.do?b_postcodefield=link";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
    var dw=540,dh=390,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	/*   
    var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null)
    {
        var in_obj=document.getElementById('postcodefield');  
        in_obj.innerHTML=this.return_vo.mess;
    }
	*/
	//改用ext 弹窗显示  wangb 20190318
	var win = Ext.create('Ext.window.Window',{
			id:'select_postcodefield',
			title:'请选择',
			width:dw+30,
			height:dh+30,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.return_vo)
    				{
        				document.getElementById('postcodefield').innerHTML=this.return_vo.mess.replace('<br>','');
    				}
				}
			}
	}); 
}
function to_save_sync()
{   
    var up = "";
    if(confirm("是否重新同步数据？"))
    {
        up="yes";
        hrSyncForm.action="/sys/export/SearchHrSyncSet.do?b_save_sync=link&isSync=1&up="+up;
        hrSyncForm.submit();
    }
    else
        up="no"
}
function to_save()
{
   if(confirm("确定保存数据？"))
    {
        up="yes";
        hrSyncForm.action="/sys/export/SearchHrSyncSet.do?b_save=link&isSync=0&up="+up;
        hrSyncForm.submit();
    }
}

function to_return()
{
    hrSyncForm.action="/sys/export/SearchEmpSync.do?b_query=link";
    hrSyncForm.submit();
}
function setCheckBox(obj){
    if(obj.checked)
        document.getElementsByName("code_value")[0].value="1";
    else
    	document.getElementsByName("code_value")[0].value="0";
}
function choice()
{
    
}
function selectCheckBox(obj,hiddname)
{
   if(obj.checked==true)
   {
      var vo=document.getElementById(hiddname);
      if(vo)
         vo.value="1";
   }else
   {
         var vo=document.getElementById(hiddname);
      if(vo)
         vo.value="0";
   }

}
function to_only_codefield()
{
     var target_url="/sys/export/SearchHrSyncFiled.do?b_onlyfield=link";
     var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
     var dw=430,dh=150,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
     /*
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
     if(return_vo!=null)
     {
        var in_obj=document.getElementById('onlyfield');  
        in_obj.innerHTML=return_vo.mess;
     }
     */
     //改用ext 弹窗显示  wangb 20190318
	var win = Ext.create('Ext.window.Window',{
			id:'select_only_codefield',
			title:'请选择',
			width:dw+30,
			height:dh+30,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.return_vo)
    				{
        				var in_obj=document.getElementById('onlyfield');  
        				in_obj.innerHTML=this.return_vo.mess;
    				}
				}
			}
	}); 
}
function IsDigit() 
 { 
       return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
 }
 
 function mode(sync_mode)
 {
  
   if(sync_mode=="trigger")
   {
      //Element.hide('job_tr');
      Element.show('fail_tr');
   }else
   {
      Element.show('job_tr');
      //Element.hide('fail_tr');
   }  
 }  
 function syncMmode(mode)
 {
    if(mode=="trigger")
    {
          //Element.hide('job_tr');
          Element.show('fail_tr');
     
    }else
    {
        Element.show('job_tr');
        //Element.hide('fail_tr');
    }
    
 }
 function to_sys_outsync()
 {
    var url="/system/outsync/outsynclist.do?b_query=link";
    hrSyncForm.action=url;
    hrSyncForm.submit(); 
 }
</script>

<html:form action="/sys/export/SearchHrSyncSet"> 
	<table  border="0" cellspacing="0" align="center" cellpadding="0"  width="80%" class="ListTable">
        <thead>
        <tr>
        <td  class="TableRow" align="left" nowrap colspan="3">
            <bean:message key="sys.export.sync_set"/>
        </td>
        
        </tr>
        </thead>
        
        <tr>
        <td  align="center"  class="RecordRow" nowrap width="100">
            <bean:message key="label.dbase"/>
        </td>
        <td  align="left" id="db" class="RecordRow" width="70%" nowrap>

            <bean:write  name="hrSyncForm" property="dbnamestr" filter="false"/>
        </td>
        <td  align="center" class="RecordRow" nowrap >
            <html:button styleClass="mybutton" property="apply" onclick="to_set_db()">
                <bean:message key="button.orgmapset"/>
            </html:button>
        </td>
        </tr>
        
        
        <tr>
        <td  align="center" class="RecordRow" nowrap>
            人员<bean:message key="kq.wizard.target"/>
        </td>
        <td  align="left" id="field" class="RecordRow" nowrap>

            <bean:write  name="hrSyncForm" property="fieldstr" filter="false"/>
        </td>
        <td  align="center" class="RecordRow" nowrap>
            <html:button styleClass="mybutton" property="apply" onclick="to_set_field('A')">
                <bean:message key="button.orgmapset"/>
            </html:button>
        </td>
        </tr>
        <tr>
        <td  align="center" class="RecordRow" nowrap>
            翻译<bean:message key="kq.wizard.target"/>
        </td>
        <td  align="left" id="codefield" class="RecordRow" nowrap>

            <bean:write  name="hrSyncForm" property="codefieldstr" filter="false"/>
        </td>
        <td  align="center" class="RecordRow" nowrap>
            <html:button styleClass="mybutton" property="apply" onclick="to_set_codefield()">
                <bean:message key="button.orgmapset"/>
            </html:button>
        </td>
        </tr>
        <tr>
        <td  align="center" class="RecordRow" nowrap>
            唯一性<bean:message key="kq.wizard.target"/>
        </td>
        <td  align="left" id="onlyfield" class="RecordRow" nowrap>

            <bean:write  name="hrSyncForm" property="onlyfieldstr" filter="false"/>
        </td>
        <td  align="center" class="RecordRow" nowrap>
            <html:button styleClass="mybutton" property="apply" onclick="to_only_codefield()">
                <bean:message key="button.orgmapset"/>
            </html:button>
        </td>
        </tr>
        <tr>
        <td  align="center" class="RecordRow" nowrap>
            机构<bean:message key="kq.wizard.target"/>
        </td>
        <td  align="left" id="orgfield" class="RecordRow" nowrap>

            <bean:write  name="hrSyncForm" property="orgfieldstr" filter="false"/>
        </td>
        <td  align="center" class="RecordRow" nowrap>
            <html:button styleClass="mybutton" property="apply" onclick="to_set_field('B')">
                <bean:message key="button.orgmapset"/>
            </html:button>
        </td>
        </tr>
        <tr>
        <td  align="center" class="RecordRow" nowrap>
            翻译<bean:message key="kq.wizard.target"/>
        </td>
        <td  align="left" id="orgcodefield" class="RecordRow" nowrap>

            <bean:write  name="hrSyncForm" property="orgcodefieldstr" filter="false"/>
        </td>
        <td  align="center" class="RecordRow" nowrap>
            <html:button styleClass="mybutton" property="apply" onclick="to_set_orgcodefield()">
                <bean:message key="button.orgmapset"/>
            </html:button>
        </td>
        </tr>
        <tr>
        <td  align="center" class="RecordRow" nowrap>
            岗位<bean:message key="kq.wizard.target"/>
        </td>
        <td  align="left" id="postfield" class="RecordRow" nowrap>

            <bean:write  name="hrSyncForm" property="postfieldstr" filter="false"/>
        </td>
        <td  align="center" class="RecordRow" nowrap>
            <html:button styleClass="mybutton" property="apply" onclick="to_set_field('K')">
                <bean:message key="button.orgmapset"/>
            </html:button>
        </td>
        </tr>
        <tr>
        <td  align="center" class="RecordRow" nowrap>
            翻译<bean:message key="kq.wizard.target"/>
        </td>
        <td  align="left" id="postcodefield" class="RecordRow" nowrap>

            <bean:write  name="hrSyncForm" property="postcodefieldstr" filter="false"/>
        </td>
        <td  align="center" class="RecordRow" nowrap>
            <html:button styleClass="mybutton" property="apply" onclick="to_set_postcodefield()">
                <bean:message key="button.orgmapset"/>
            </html:button>
        </td>
        </tr>
        <tr>
          <td  align="center" class="RecordRow" nowrap>
            同步方式
          </td>
          <td  align="left" id="postcodefield" class="RecordRow" nowrap colspan="2">

            &nbsp;<html:radio name="hrSyncForm" property="sync_mode" value="trigger"/>触发器&nbsp;&nbsp;
            
            <html:radio name="hrSyncForm" property="sync_mode" value="time_job"/>定时任务
          </td>     
        </tr>
        <tr id="fail_tr">
          <td  align="center" class="RecordRow" nowrap>
            失败次数上限
          </td>
          <td  align="left" id="postcodefield" class="RecordRow" nowrap colspan="2">

            &nbsp;&nbsp;&nbsp;<html:text name="hrSyncForm" property="fail_limit" size="10"  styleClass="text" onkeypress="event.returnValue=IsDigit();" style="text-align:right;" />&nbsp;次 
         
            &nbsp;&nbsp;&nbsp;
            <html:button styleClass="mybutton" property="apply" onclick="to_sys_outsync();">
                外部系统配置
            </html:button>
          </td>     
        </tr>
        <!--
        <tr>
        <td  align="center" class="RecordRow" nowrap>
            <bean:message key="sys.export.update_set"/>
        </td>
        <td  align="left" class="RecordRow" nowrap>

            <hrms:optioncollection name="hrSyncForm" property="sync_fieldlist" collection="list" />
            <html:select name="hrSyncForm" property="sync_field" size="1" style="width:50%" onchange="choice()">
            <html:options collection="list" property="dataValue" labelProperty="dataName"/>
            </html:select>
        </td>
        <td  align="center" class="RecordRow" nowrap>           
        </td>
        </tr>-->
         
        
        <tr id="job_tr">
        <td  align="center" class="RecordRow" nowrap>           
        </td>
        <td  align="left" class="RecordRow" nowrap colspan="2">
        <logic:equal value="0"  name="hrSyncForm" property="code_value">
            <input type="checkbox" name="codeValue" onclick="setCheckBox(this);">
            <html:hidden name="hrSyncForm" property="code_value"/> 
        </logic:equal>
        <logic:equal value="1"  name="hrSyncForm" property="code_value">
            <input type="checkbox" name="codeValue" onclick="setCheckBox(this);" checked>
            <html:hidden name="hrSyncForm" property="code_value"/> 
        </logic:equal>    
            <bean:message key="label.all" /><bean:message key="sys.export.code" />  
            
        <logic:equal value="1"  name="hrSyncForm" property="fieldAndCode">
            <input type="checkbox" name="fieldAndCodeInfo" value="1"  onclick="selectCheckBox(this,'fieldAndCode');" checked>
         </logic:equal>
         <logic:notEqual value="1"  name="hrSyncForm" property="fieldAndCode">
            <input type="checkbox" name="fieldAndCodeInfo" value="1"  onclick="selectCheckBox(this,'fieldAndCode');">
         </logic:notEqual>
         <html:hidden name="hrSyncForm"  property="fieldAndCode" styleId="fieldAndCode"/> 
        翻译指标含代码
        &nbsp;&nbsp;分隔符<!-- 【7705】系统管理/应用设置/数据交换/数据视图,分隔符显示太靠上了，不居中  jingq upd 2015.02.25 -->
        <html:text name="hrSyncForm"  property="fieldAndCodeSeq" styleId="fieldAndCodeSeq" styleClass="RecordRow" style="border-top-width: 0px;border-left-width: 0px;border-right-width: 0px;width:40px;height:18px !important;line-height:18px;"/> 
        <br>
            
         <logic:equal value="1"  name="hrSyncForm" property="sync_A01">
            <input type="checkbox" name="a01Value"  value="1"  onclick="selectCheckBox(this,'sync_A01');" checked>
         </logic:equal>
         <logic:notEqual value="1"  name="hrSyncForm" property="sync_A01">
            <input type="checkbox" name="a01Value" value="1"  onclick="selectCheckBox(this,'sync_A01');">
         </logic:notEqual>
         <html:hidden name="hrSyncForm"  property="sync_A01" styleId="sync_A01"/> 
         <bean:message key="sys.export.sync.a01" /> 
         <logic:equal value="1"  name="hrSyncForm" property="sync_B01">
            <input type="checkbox" name="b01Value" value="1" onclick="selectCheckBox(this,'sync_B01');" checked>
         </logic:equal>
         <logic:notEqual value="1"  name="hrSyncForm" property="sync_B01">
            <input type="checkbox" name="b01Value" value="1"  onclick="selectCheckBox(this,'sync_B01');">
         </logic:notEqual>
         <html:hidden name="hrSyncForm"  property="sync_B01" styleId="sync_B01"/> 
         <bean:message key="sys.export.sync.b01" /> 
         <logic:equal value="1"  name="hrSyncForm" property="sync_K01">
            <input type="checkbox" name="k01Value" value="1" onclick="selectCheckBox(this,'sync_K01');" checked>
         </logic:equal>
         <logic:notEqual value="1"  name="hrSyncForm" property="sync_K01" >
            <input type="checkbox" name="k01Value" value="1"  onclick="selectCheckBox(this,'sync_K01');">
         </logic:notEqual>
         <html:hidden name="hrSyncForm"  property="sync_K01" styleId="sync_K01"/> 
         <bean:message key="sys.export.sync.k01" /> 
         <logic:equal value="1"  name="hrSyncForm" property="jz_field">
            <input type="checkbox" name="jzValue" value="1"  onclick="selectCheckBox(this,'jz_field');" checked>
         </logic:equal>
         <logic:notEqual value="1"  name="hrSyncForm" property="jz_field">
            <input type="checkbox" name="jzValue" value="1"  onclick="selectCheckBox(this,'jz_field');">
         </logic:notEqual>
         <html:hidden name="hrSyncForm"  property="jz_field" styleId="jz_field"/> 
         同步兼职记录
         <logic:equal value="1"  name="hrSyncForm" property="photo">
            <input type="checkbox" name="photocheck" value="1"  onclick="selectCheckBox(this,'photo');" checked>
         </logic:equal>
         <logic:notEqual value="1"  name="hrSyncForm" property="photo">
            <input type="checkbox" name="photocheck" value="1"  onclick="selectCheckBox(this,'photo');">
         </logic:notEqual>
         <html:hidden name="hrSyncForm"  property="photo" styleId="photo"/> 
         同步照片
         <logic:equal value="1"  name="hrSyncForm" property="fieldChange">
            <input type="checkbox" name="fieldChangeInfo" value="1"  onclick="selectCheckBox(this,'fieldChange');" checked>
         </logic:equal>
         <logic:notEqual value="1"  name="hrSyncForm" property="fieldChange">
            <input type="checkbox" name="fieldChangeInfo" value="1"  onclick="selectCheckBox(this,'fieldChange');">
         </logic:notEqual>
         <html:hidden name="hrSyncForm"  property="fieldChange" styleId="fieldChange"/> 
        跟踪指标变化前后信息
        
        
        </td>
        <!-- td  align="center" class="RecordRow" nowrap>
        </td-->
        </tr>
        
        <tr >
        <td align="center" style="height: 35px !important;" class="RecordRow" colspan="3">
        &nbsp;&nbsp;
            <html:button styleClass="mybutton" property="apply" onclick="to_save();">
                保存
            </html:button>
            <%if (!"true".equalsIgnoreCase(SystemConfig.getPropertyValue("hiddenReSync"))) {%>
            <!--  取消保存并同步按钮  wangb 20170809
            <html:button styleClass="mybutton" property="apply" onclick="to_save_sync();">
                保存并同步
            </html:button>
             -->
            <%} %>
            &nbsp;
            <html:button styleClass="mybutton" property="apply" onclick="to_return();">
                <bean:message key="button.return"/>
            </html:button>
            
            
            
        </td>
            
        </tr>
        
    </table>
</html:form>

<script language="javascript">
    //mode("${hrSyncForm.sync_mode}");
</script>
