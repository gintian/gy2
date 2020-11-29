<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,com.hrms.struts.constant.WebConstant,com.hrms.struts.valueobject.UserView"%>
 <%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
	String model=(String)request.getParameter("model");//history 表示为薪资历史数据分析进入
%>
<script language="javascript">

    var model="";
    <% if("history".equalsIgnoreCase(model)){ %>
    model="history";
    <%}%>

	function init()
	{ 
		var rightFields=$('del_pro_str');
		var isFirst = true;
		/* 显示-项目过滤 xiaoyun 2014-9-26 start */
		for(var i=0;i<rightFields.options.length;i++) {
			if(rightFields.options[i].selected) {
				isFirst = false;
				break;
			}
		}
		if(rightFields.options.length>0 && isFirst)
		/* 显示-项目过滤 xiaoyun 2014-9-26 end */
		{
			rightFields.options[0].selected=true;
		}
	}
//   function bdelete()
//	{
//		var result = ifdel();
//		alert(result);
//		if(result==true)
//		{
//			var del_pro = document.getElementById("del_pro_str").value;
//			var hashVo=new ParameterSet();
//			var operation = "delete";
//			hashVo.setValue("del_pro_id",del_pro);
//			hashVo.setValue("operation",operation);
//			hashVo.setValue("projectname","");
//			hashVo.setValue("proright_str","");
//			var request=new Request({method:'post',asynchronous:false,functionId:'3020070202'},hashVo);		
			//var request=new Request({method:'post',asynchronous:false,onSuccess:refresh,functionId:'3020070202'},hashVo);						
//			returnValue = del_pro;
			//removeitem('del_pro');
		    //window.close();  
//		}
//	}	
	function bdelete(type,salaryid)
	{
	var operitems = '${accountingForm.operitems}';
	if(type=='1')
	{
		
			//var del_pro = document.getElementById("del_pro_str").value;
			//alert(del_pro);
			
			var obj=document.getElementById("del_pro_str");
			var values="";
			for(var i=0;i<obj.options.length;i++)
			{
				if(obj.options[i].selected==true){
					values+="/"+obj.options[i].value;
			if(operitems.indexOf(","+obj.options[i].value+",")!=-1){
			alert(""+obj.options[i].text+"不是你创建的，不允许删除！");
			return;
			}
			}
			}
		var result = ifdel();
		
		if(result==true)
		{
			accountingForm.action="/gz/gz_accounting/gz_pro_filter_delete.do?b_query=link&model="+model+"&del_pro_id="+values.substring(1)
			                       +"&salaryid="+salaryid;
	        accountingForm.submit();	
		}
    }else
    {
        var obj=document.getElementById("del_pro_str");
		var values="";
		var name="";
		var num=0;
		for(var i=0;i<obj.options.length;i++)
		{
			if(obj.options[i].selected==true)
			{
				values=obj.options[i].value;
				name=obj.options[i].text;
				num++;
			}
		}
		if(num>1)
		{
		   alert("一次只能重命名一条记录！");
		   return;
		}
		if(num==0)
		{
		  alert("请选择要重命名的记录！");
		  return;
		}
			if(operitems.indexOf(","+values+",")!=-1){
			alert("没权限操作该项目！");
			return;
			}
		var thecodeurl ="/gz/gz_accounting/gz_save_pro_filter.jsp?rename=1&scopeflag=2"; 
		 var theArr=new Array(name); 
	    var return_vo= window.showModalDialog(thecodeurl, theArr, 
	              "dialogWidth:280px; dialogHeight:150px;resizable:no;center:yes;scroll:yes;status:no");
	    if(return_vo!=null)	 
	    {
	         var hashVo=new ParameterSet();
	         hashVo.setValue("record",values);
	         hashVo.setValue("salaryid",salaryid);
	          hashVo.setValue("name",getEncodeStr(return_vo.pname));
	         hashVo.setValue("opt","0");
	         hashVo.setValue("scopeflag",return_vo.sflag);
	         var request=new Request({method:'post',asynchronous:false,onSuccess:refresh,functionId:'30200710260'},hashVo);
	       
	    }
    }
	}
	function ifdel()
	{
		return ( confirm(GZ_ACCOUNTING_ENTERDELETEITEM+'？') );	
	}
	function refresh()
	{
		alert(55);
		window.reload();	
	}
	function selectok()
	{
	      var obj=document.getElementById("del_pro_str");
			var values="";
			var num=0;
			for(var i=0;i<obj.options.length;i++)
			{
				if(obj.options[i].selected==true)
				{
					values=obj.options[i].value;
					num++;
				}
			}
			if(num>1)
			{
			   alert(GZ_ACCOUNTING_NOTSELETFILTER+"!");
			   return;
			}
			if(num==0)
			{
			    alert(GZ_ACCOUNTING_SELECTFILTER+"!");
			    return;
			}
			window.returnValue=values; 
			window.close();
	}
	function newFilter(salaryid)
	{
	     var thecodeurl ="/gz/gz_accounting/gzprofilter.do?b_query=link&model="+model+"&opt=1&salaryid="+salaryid;
	     if(isIE6() ){
	     	     var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:480px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");
	     }else{
	     	     var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:450px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	     }

         if(return_vo)
         {
             accountingForm.action="/gz/gz_accounting/gzprofilter.do?b_delete=link&model="+model+"&salaryid="+salaryid;
		     accountingForm.submit();
         }     
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
	function editItem(salaryid)
	{
	        var obj=document.getElementById("del_pro_str");
			var values="";
			var num=0;
			for(var i=0;i<obj.options.length;i++)
			{
				if(obj.options[i].selected==true)
				{
					values=obj.options[i].value;
					num++;
				}
			}
			if(num>1)
			{
			   alert(GZ_ACCOUNTING_NOTSELETFILTER+"!");
			   return;
			}
			if(num==0)
			{
			    alert(GZ_ACCOUNTING_SELECTFILTER+"!");
			    return;
			}
			var operitems = '${accountingForm.operitems}';
			if(operitems.indexOf(","+values+",")!=-1){
			alert("没权限操作该项目！");
			return;
			}
	    var thecodeurl ="/gz/gz_accounting/gzprofilter.do?b_query=link&opt=1&model="+model+"&salaryid="+salaryid+"&chkid="+values;
	    if(isIE6() ){
	        	var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:500px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");
	    }else{
	        	var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:480px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	    }

         if(return_vo)
         {
             accountingForm.action="/gz/gz_accounting/gzprofilter.do?b_delete=link&model="+model+"&salaryid="+salaryid;
		     accountingForm.submit();
         }     
	}
	function sort(salaryid)
	{
	   var obj=document.getElementById("dps");
	   var num=0;
	   var ids="";
	   if(obj)
	   { 
	      for(var i=0;i<obj.options.length;i++)
	      {
	          ids+="/"+obj.options[i].value;
	          num++;
	      }
	      if(num==0||num==1)
	      {
	        return;
	      }
	      var hashVo=new ParameterSet();
	      hashVo.setValue("record",ids.substring(1));
	      hashVo.setValue("salaryid",salaryid);
	      hashVo.setValue("opt","1");
	      var request=new Request({method:'post',asynchronous:false,onSuccess:refresh,functionId:'30200710260'},hashVo);
	   }
	}
	function refresh(outparameters)
	{
	   var salaryid=outparameters.getValue("salaryid");
	   accountingForm.action="/gz/gz_accounting/gzprofilter.do?b_delete=link&model="+model+"&salaryid="+salaryid;
	   accountingForm.submit();
	}
</script>
<base target=_self />
<html:form action="/gz/gz_accounting/gzprofilter">
<%if("hl".equals(hcmflag)){ %>
<table width='435px;' border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable" >
<%}else{ %>
<table width='435px;' border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable" style="margin-top:-1px;margin-left:-1px;">
<%} %>

   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="3">
		<bean:message key="label.gz.project.delete"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr class="trShallow">
            <td width="100%" align="center" class="RecordRow" nowrap colspan="2" style="border-top=0px">
              <table>
                <tr>               
                <td width="46%" align="center">
                 <table width="100%">               
                  <tr>
                  <td width="100%" align="left">
 		     
 		        <hrms:optioncollection name="accountingForm" property="del_filter_pro" collection="list"/>
		              <html:select styleId="dps" name="accountingForm" size="10" property="del_pro_str" multiple="multiple" style="height:230px;width:100%;font-size:9pt">
		              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>	
 		     
                 </td>
                  </tr>
                  </table>             
                </td>
               <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_up" onclick="upItem($('del_pro_str'));">
            		     <bean:message key="button.previous"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_down" onclick="downItem($('del_pro_str'));">
            		     <bean:message key="button.next"/>    
	           </html:button >	     
                </td>                                               
                </tr>
               
              </table>             
            </td>
            </tr>
             <tr>
                <td colspan="3" height="35" align="center">
                      
                   
	      			<hrms:priv func_id="3240740201,3240223,3240323,3250323,3250223">
                   <html:button styleClass="mybutton" property="b_delete" onclick="newFilter('${accountingForm.salaryid}');">
            		      <bean:message key="menu.gz.new"/>
	      			</html:button>
	      			</hrms:priv>
	      		<html:button styleClass="mybutton" property="b_edit" onclick="editItem('${accountingForm.salaryid}');">
            		      修改
	      			</html:button>
	            <html:button styleClass="mybutton" property="b_delete" onclick="bdelete('1','${accountingForm.salaryid}');">
            		      <bean:message key="button.delete"/>
	      			</html:button> 	 
	      		<html:button styleClass="mybutton" property="b_sort" onclick="sort('${accountingForm.salaryid}');">
            		      保存排序
	      			</html:button>
                    <html:button styleClass="mybutton" property="b_delete" onclick="selectok();">
            		      <bean:message key="button.ok"/>
	      			</html:button>
	      			<html:button styleClass="mybutton" property="b_cancel" onclick="window.close();">
            		      <bean:message key="button.close"/>
	      			</html:button> 	
                </td>
                </tr>
</table>
</html:form>
<script language="javascript">
	init();
</script>