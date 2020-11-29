<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>



<%
   String ctrlorg = request.getParameter("ctrlorg").toString();
   String nextorg = request.getParameter("nextorg").toString();
   String ctrlUM = request.getParameter("ctrlUM").toString();
   ctrlUM = ctrlUM.equals("0") ?"2":"1";
%>


<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<script>
function getorg()
	{
		
		var ctrlorg =document.getElementById("ctrlorg").value;
		var controlOrgDesc = document.getElementById("textDiv").innerHTML;
		var theurl="/system/logonuser/org_employ_tree.do?flag="+0+"`selecttype="+1+"`dbtype="+0+
        "`priv="+1 + "`isfilter=" + 0+"`loadtype="+<%=ctrlUM%>+"`checkvalue=,"+ctrlorg+"`nmodule="+4; //+"`needboxcheck=0`checktitle=,"+controlOrgDesc;
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);         
		var dw = 320;
		var dh = 410;
		if(isIE6()){
			dw += 10;
			dh += 20;
		}
		/*
		var ret_vo= window.showModalDialog(iframe_url,1, 
		"dialogWidth:"+width+"px; dialogHeight:"+height+"px;resizable:no;center:yes;scroll:no;status:no");
		if(ret_vo)
		{
			var tmp=ret_vo.content;
			$('textDiv').innerHTML=ret_vo.title;
			$('ctrlorg').value=tmp;
		}
		return false;
		*/
	    var obj;
		if(getBrowseVersion()) //IE浏览器
			obj = parent.parent.parent.frames[0];
		else //非IE
			obj = parent.parent.parent.frames['center_iframe'].contentWindow;
		
		if(obj.Ext.getCmp("ps_parameter_getOrg")){
			obj.Ext.getCmp("ps_parameter_getOrg").close(); //防止再次点击
	    }
		obj.Ext.create('Ext.window.Window',{
			id:'ps_parameter_getOrg',
			title:'组织机构',
			width:dw,
			height:dh,
			resizable:false,
			modal:true,
			autoScroll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff;" frameborder="0" scrolling=no height="100%" width="100%" src="'+iframe_url+'"></iframe>',
			renderTo:obj.Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.msg){
						var tmp = this.msg.content;
						$('textDiv').innerHTML = this.msg.title;
						$('ctrlorg').value = tmp;
					}
					return false;
					
				}
			}
			
		});
	}
	
	function save(){
		var ctrlorg = $('ctrlorg').value;
		var nextorg = '0';
		if($('childOrg').checked == true){
			nextorg = '1';
		}
		var extWin = parent.parent.Ext.getCmp('org_selectCtrlOrg');
		if(extWin){
			extWin.msg = nextorg+"|"+ctrlorg;
			extWin.close();
		}
		parent.window.returnValue = nextorg+"|"+ctrlorg;
		//top.close();
	}
	function closeWin(){
		var extWin = parent.parent.Ext.getCmp('org_selectCtrlOrg');
		if(extWin){
			extWin.close();
		}
		window.close();
	}
</script>
<html:form action="/pos/posparameter/ps_parameter">
<div class="fixedDiv3">
   <table width="100%"  border="0" cellspacing="0"  align="center" cellpadding="0">
      <tr>
      <td width="100%" align="center" class="RecordRow" style="padding-bottom: 10px;">
               <table  width="100%" align="center">
                  <tr><td><bean:message key="pos.posparameter.headcountctrlorg"/></td></tr>
                  <tr>
                    <td valign="top">
                        <div id="textDiv" style="width: 100%;height: 100px; padding:5px 0 5px 5px;" class="RecordRow" >
                             ${posCodeParameterForm.controlOrgDesc }
                        </div>
                        <input type="hidden" id="ctrlorg" value="<%=ctrlorg %>">
                    </td>
                    <td valign="top" align="center" width=12% style="padding-left:5px;">
                       <!-- 处理google不加type属性默认提交的问题 -->
                       <button type="button" style="margin: 0px;" onclick="return getorg();" class="mybutton"><bean:message key="kq.search_feast.select"/> </button>
                    </td>
                  </tr>
                  <tr>
                    <td>
                       <input type="checkbox" value="1" id="childOrg" onchange="changeorg=true;" ><bean:message key="pos.posparameter.nextlevelcontorl"/>
                       <script>
                            if(<%=nextorg%> == '1')
                            	 $('childOrg').checked=true;
                       </script>
                    </td>
                  </tr>
               </table>
      
      </td>
      </tr>
      <tr>
        <td align="center" style="padding-top:5px;">   
           <button class="mybutton" onclick="save();"><bean:message key="kq.emp.button.save"/></button>&nbsp;&nbsp;
           <button class="mybutton" onclick="closeWin();"><bean:message key="kq.emp.button.return"/></button>
        </td>
      </tr>
   </table>
</div>
</html:form>

