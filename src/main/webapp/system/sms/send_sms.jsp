<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<hrms:themes></hrms:themes>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/js/xtree.js"></SCRIPT>
<script language="javascript" src="/js/meizzDate_saveop.js"></script>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script language="javascript" src="/ext/rpc_command.js"></script>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<script type="text/javascript">
	
	function getSelectedEmploy()
	{
     //var return_vo=select_org_emp_dialog8(1,1,"","","","","0");  
     //var return_vo=select_org_emp_dialog2("1","1","0","1","1","1"); 
 	 //19/3/19 xus 浏览器兼容 系统管理-发送短信 机构树弹窗
      var theurl="/system/logonuser/org_employ_tree.do?flag=1`showDb=1`selecttype=1`dbtype=0"+
                 "`priv=1`isfilter=1`loadtype=1";
       var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);  
       var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2; 
       if(!window.showModalDialog){
    	   dw=340;dh=440;
       }
       var config = {id:'getSelectedEmploy_showModalDialogs',width:dw,height:dh};
   	   modalDialog.showModalDialogs(iframe_url,'',config,openReturnValue);
       
     /*   
   	   var return_vo= window.showModalDialog(iframe_url,1, 
     		 "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	 if(return_vo)
	 {
	 	$('user_').value=return_vo.title;
	 	$('user_h').value=return_vo.content;
	 }
	  */
	}
	//19/3/19 xus 浏览器兼容  高级弹窗回调函数
	function openReturnValue(return_vo){
		if(return_vo)
		 {
		 	$('user_').value=return_vo.title;
		 	$('user_h').value=return_vo.content;
		 }
	}
	//19/3/19 xus 浏览器兼容 关闭Ext弹窗窗口
	function closeExtWin(){
		if(Ext.getCmp('getselectcond_showModalDialogs'))
			Ext.getCmp('getselectcond_showModalDialogs').close();
	}
	function is_success(outparam)
	{
	//	var info=outparam.getValue("message");
		var value=outparam.responseText;
		var map=Ext.JSON.decode(value);		
		//alert(map+"-----------"+map.succeed);
		
		if(map.succeed)
		{
	//		alert("11111111111111");		
			var info=map.message;
			var waitInfo=document.getElementById("wait");			
			waitInfo.style.display="none";
			//if(!info)
			//	return;		
			alert(info);
		}
		else
		{
	//		alert("22222222222222");
			var waitInfo=document.getElementById("wait");			
			waitInfo.style.display="none";
			alert("短信发送失败！");
		}
		
	}
	
	function send_msg()
	{
		 if(!($F('scontent')))
		 {
		 	alert("信息内容不能为空！");
		 	return;
		 }	
		 
		 if ($F('user_out')) {
		 	var str = $F('user_out');
		 	var test = /^([0-9|\,])+$/;
		 	if (!test.test(str)) {
		 		alert("外部收信人中有非法字符，只能是数字和英文逗号！");
		 		return false;
		 	}
		 }
		 var waitInfo=eval("wait");
		 waitInfo.style.display="block";	
	     //var hashvo=new ParameterSet();
	     //hashvo.setValue("receiver",$F('user_h'));
	     //hashvo.setValue("receiverq",$F('user_q'));
	     //hashvo.setValue("out_receiver",$F('user_out'));
	     //hashvo.setValue("msg",$F('scontent'));	        
	    //var request=new Request({asynchronous:true,onSuccess:is_success,functionId:'1010010210'},hashvo);  
	   
	        var hashvo=new HashMap(); //new ParameterSet();
	        hashvo.put("receiver",$F('user_h'));
	     hashvo.put("receiverq",$F('user_q'));
	     hashvo.put("out_receiver",$F('user_out'));
	     hashvo.put("msg",$F('scontent'));
	 
		Rpc({functionId:'1010010210',success:is_success},hashvo);      
	
	}
	
	// 高级
	function getselectcond() {
		var dat = new Date();
		var url="/system/sms/send_sms_query.do?b_query=link`time="+dat.getTime();
		var iframe_url = "/general/query/common/iframe_query.jsp?src="+$URL.encode(url)+"&tim="+dat.getTime();
		var height=440;
		if(window.showModalDialog){
            height=400;
        }

		var config = {id:'getselectcond_showModalDialogs',width:730,height:height};
		modalDialog.showModalDialogs(iframe_url,'',config,getselectcond_callbackfunc);
		/* 
       	var  return_vo = window.showModalDialog(iframe_url, "", 
              "dialogWidth:700px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");
        if (return_vo) {
        	document.getElementById("user_h").value= "HQ";
        	document.getElementById("user_").value = return_vo.desc;
        	document.getElementById("user_q").value = return_vo.expr+";"+return_vo.factor+";"+return_vo.like+";"+return_vo.pre;
        }
         */
	}
	function getselectcond_callbackfunc(return_vo) {
		if (return_vo) {
        	document.getElementById("user_h").value= "HQ";
        	document.getElementById("user_").value = return_vo.desc;
        	document.getElementById("user_q").value = return_vo.expr+";"+return_vo.factor+";"+return_vo.like+";"+return_vo.pre;
        }
	}
</script>
<html:form action="/system/sms/send_sms">
<div id='wait' style='position:absolute;top:200;left:250;display:none;'   >
  <table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style" height=24><bean:message key="label.sms.sending"/></td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
        </table>
</div>
      <table width="450" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable" style="margin-top: 2px;">
          <tr height="20">
       		<td align="left" class="TableRow" colspan="2"><bean:message key="label.sms.send"/></td>
          	      
          </tr> 
                      <tr class="list3">
                	   <td align="right" nowrap ><bean:message key="label.sms.in.receiver"/></td>
                	   <td align="left" nowrap >
                	   	<INPUT type="text" id="user_" value=""  size="48" maxlength="200" class="inputtext" style="width: 300px">
                	   	<img align=absmiddle src="/images/code.gif" onclick="getSelectedEmploy();" align="middle"/>
                	   	<INPUT type="hidden" id="user_h" value=""  size=30>&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" name="hight"  value="<bean:message key="button.sys.cond"/>" onclick="getselectcond()" class="mybutton"/>
                	   	<INPUT type="hidden" id="user_q" value=""  size=30> 
                       </td>
                      </tr>
                      <tr class="list3">
                	   <td align="right" nowrap ><bean:message key="label.sms.out.receiver"/></td>
                	   <td align="left" nowrap >
                	   	<INPUT type="text" id="user_out" value=""  size="48" maxlength="200" class="inputtext" style="width: 300px">
                       </td>
                      </tr>                      
                      <tr class="list3">
                	   <td align="right" nowrap valign="top"><bean:message key="label.sms.notes"/></td>
                	   <td align="left"  nowrap>
						<textarea id="scontent" cols="50" rows="10" style="width: 300px"></textarea>                	      	
                       </td>
                      </tr>

          <tr class="list3">
            <td align="left" colspan="2">
		&nbsp;  <bean:message key="label.sms.info"/>         
            </td>
          </tr>                                                      
          <tr class="list3">
            <td align="center" colspan="2" style="height:35px">
   	           <html:button styleClass="mybutton" property="send" onclick="send_msg();"><bean:message key="button.sms.send"/></html:button>
            </td>
          </tr>          
      </table>
</html:form>
