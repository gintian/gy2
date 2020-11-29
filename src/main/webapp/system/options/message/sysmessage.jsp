<%@page import="java.util.Date"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
   
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String flag=userView.getBosflag();
	if(userView != null){
		userView.getHm().put("fckeditorAccessTime", new Date().getTime());	
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
%>
<hrms:themes></hrms:themes>
<script language="javascript">
   //判断是否点击删除图片
   var isDel = false;
   function save()
   {
   		if(validate('RI','days','公告天数')){
   	  var bgimageid = document.getElementById("bgimageid").value;
   	  var fileext=bgimageid.substring(bgimageid.indexOf(".")+1);
   	  /* if("jpg|gif|jpeg|png|bmp".indexOf(fileext)==-1){ */
   	  if("jpg|gif|bmp".indexOf(fileext.toLocaleLowerCase())==-1){
   	  	alert("请上传jpg,gif,bmp格式的背景图片!");
   	  	return;
   	  }
   	  /**
   	  *许硕 有图片时公告内容为空，不能保存
   	  *16/09/20
   	  **/
   	  if(bgimageid!=''&&FCKeditorAPI.GetInstance('FCKeditor1').EditorDocument.body.innerText==''){
   	  	alert("公告内容不能为空!");
   	  	return;
   	  }
      if(confirm("确认保存数据吗?"))
      {
      	var oEditor = FCKeditorAPI.GetInstance('FCKeditor1');
       	var input = document.getElementsByName("textConstant");
       	if (input && input) {
       		input[0].value = oEditor.EditorDocument.body.innerText;
       	}  
       	/**
       	*许硕 点击删除图片，再保存之后，图片未被删除bug
       	*16/09/22
       	**/  
       	if(isDel){
        sysMessageForm.action="/system/options/message/sys_manager.do?b_save=link&encryptParam=<%=PubFunc.encrypt("flag=save`backgroudimage= ")%>";
        }else{
        sysMessageForm.action="/system/options/message/sys_manager.do?b_save=link&encryptParam=<%=PubFunc.encrypt("flag=save")%>";
        }
        sysMessageForm.submit();
      }
      }
   }
   function clear_message()
   {
       if(confirm("确认清除数据吗?"))
      {
         sysMessageForm.action="/system/options/message/sys_manager.do?b_save=link&encryptParam=<%=PubFunc.encrypt("flag=clear")%>";
         sysMessageForm.submit();
      }
   }
   function checkselect(obj,name)
   {
   
      if(obj.checked==true)
      {
         var vo=document.getElementById(name);
         if(vo)
            vo.value="1";
      }else
      {
         var vo=document.getElementById(name);
         if(vo)
            vo.value="0";
      }
   }
   
   function delete_file(){
   	var request=new Request({method:'post',asynchronous:false,parameters:'flag=deletebgimage',onSuccess:onsuccess,functionId:'1010020622'});
   
   	function onsuccess(outparamters){
   		isDel = true;
   		var bgimageid = document.getElementById("bgimageid");
   		bgimageid.value="";
   		var imageid=document.getElementById("imageid");
   		imageid.style.display="none";
   	}
   }
</script>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<%try {

			%>
<html:form action="/system/options/message/sys_manager" enctype="multipart/form-data">
	
	<table width="700" border="0" cellpadding="0" cellspacing="0" align="center" style="BORDER-RIGHT: #C4D8EE 1pt solid;" class="ftable common_border_color">
		<tr height="20">
			<td align="left" class="TableRow" colspan="2">
				<bean:message key="system.options.message.head" />
				&nbsp;
			</td>
		</tr>

					<tr class="list3">

						<td align="right" nowrap valign="middle" width="20%">
							<bean:message key="conlumn.board.content" />
						</td>

						<td align="left"  nowrap>
                            <html:textarea name="sysMessageForm" property="constant" cols="80" rows="20" style="display:none;" />
                            <html:hidden name="sysMessageForm" property="textConstant" />
							<script type="text/javascript">
					              var oldInputs = document.getElementsByName('constant');   
					              var oFCKeditor = new FCKeditor( 'FCKeditor1' ) ;
					              oFCKeditor.BasePath	= '/fckeditor/';
					              oFCKeditor.Height	= 430 ;
					              oFCKeditor.ToolbarSet='Apply';					             
					              oFCKeditor.Value	= oldInputs[0].value;
					              oFCKeditor.Create() ;
           
                            </script>
						</td>
					</tr>

					<tr class="list3">
						<td align="right" nowrap>
							<bean:message key="conlumn.board.period" />
						</td>
						<td>
						   <html:text name="sysMessageForm" property="days" styleClass="text4" style="width:300px;"/>&nbsp;&nbsp;
						   <logic:notEqual name="sysMessageForm" property="start_date" value="">
						    (<bean:message key="system.options.message.start_date" />: <bean:write name="sysMessageForm" property="start_date" filter="false"/>)
						  </logic:notEqual>
						   <html:hidden name="sysMessageForm" property="start_date"/> 
						</td>
					</tr>
					<tr class="list3">
					<td align="right" nowrap>
						背景图片
					</td>
					<td align="left" nowrap>
						<html:file styleId="bgimageid" name="sysMessageForm" property="bgimage" style="font-size: 11px;cursor: pointer;width:300px;" styleClass="text6"/>
						<logic:notEmpty name="sysMessageForm" property="backgroudimage">
							<IMG id="imageid" style="cursor: pointer;" src="/images/lawdelete.gif" title="删除背景图片" onclick="delete_file();" />
						</logic:notEmpty>
						&nbsp;(为正常显示背景请上传560×350像素图片)
					</td>
					</tr>
					<%-- wangzhongjun changed at 20150417 10:22 for bug 8688,hcm版本不区分自助业务平台
						原内容：if(flag==null||!flag.equals("hl")){
					--%>
					<%if(flag==null||(!flag.equals("hl")&&!flag.equals("hcm"))){ %>
					<tr class="list3">
						<td align="right" nowrap>
							<bean:message key="system.options.message.arae" />
						</td>
						<td>
						  <logic:equal name="sysMessageForm" property="view_hr" value="1">
						      <input type="checkbox" name="view_hr1" value="1" checked onclick="checkselect(this,'view_hr');">
						  </logic:equal>
						  <logic:notEqual name="sysMessageForm" property="view_hr" value="1">
						       <input type="checkbox" name="view_hr1" value="1" onclick="checkselect(this,'view_hr');">
						  </logic:notEqual>
						  <html:hidden name="sysMessageForm" property='view_hr' styleClass="text"/>  
						  <bean:message key="system.options.message.view_hr" />
						  <logic:equal name="sysMessageForm" property="view_em" value="1">
						      <input type="checkbox" name="view_em1" value="1" checked onclick="checkselect(this,'view_em');">
						  </logic:equal>
						  <logic:notEqual name="sysMessageForm" property="view_em" value="1">
						     <input type="checkbox" name="view_em1" value="1" onclick="checkselect(this,'view_em');">
						  </logic:notEqual>
						  <html:hidden name="sysMessageForm" property='view_em' styleClass="text"/>  
						  <bean:message key="system.options.message.view_em" />						  
						</td>
					</tr>
                   <%}else{ %>
                     <html:hidden name="sysMessageForm" property='view_hr' value="1"/> 
                   <%} %>
		<tr class="list3">
			<td align="center" colspan="2" style="height:35px">				
				<input type="button" name="b_save" value='<bean:message key="button.save" />' class="mybutton" onclick="showView();document.sysMessageForm.target='_self';save();"> 			
				<logic:notEmpty name="sysMessageForm" property="constant">
					<input type="button" name="b_clear" value='<bean:message key="system.options.message.clear" />' class="mybutton" onclick="javascript:clear_message();"> 			
				</logic:notEmpty>
			</td>
		</tr>
	</table>
</html:form>
<%} catch (Exception ex) {
	//ex.printStackTrace();
			%>
<script>
		alert('添加不成功');
	</script>
<%}

		%>

<script>
    function showView() {
       var oEditor = FCKeditorAPI.GetInstance('FCKeditor1');
       var oldInputs = document.getElementsByName('constant');
       oldInputs[0].value = oEditor.GetXHTML(true);
    }
    if(getBrowseVersion() == 10){//ie11 样式修改 wangb 20190323
    	var bgimageid = document.getElementById('bgimageid');
    	bgimageid.style.lineHeight='0px';
    }
</script>
