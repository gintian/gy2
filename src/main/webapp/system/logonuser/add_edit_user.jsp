<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hrms.hjsj.utils.Sql_switcher,com.hjsj.hrms.utils.ResourceFactory"%>
<%@ page import="com.hjsj.hrms.businessobject.sys.SysParamBo,com.hjsj.hrms.businessobject.sys.SysParamConstant"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.struts.valueobject.UserView" %>
<%
			boolean bv=true;
			//String value=SystemConfig.getPropertyValue("passwordrule");
            String	value=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.PASSWORDRULE);
		    if(value==null||value.length()==0||value.equalsIgnoreCase("0"))
		    	bv=false;
		    String isedit = request.getParameter("b_query");
		    pageContext.setAttribute("isedit",isedit);
		    
		    UserView userView=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
		    String bosflag = userView.getBosflag();
		    String top = "-195";
		    if(bosflag.equalsIgnoreCase("hcm"))
		    	top = "-235";
%>
<script type="text/javascript">
	function checkIsIntNum(value)
	{
		return /^[0-9]*[1-9][0-9]*$/.test(value);
	}
	function validate(pwd)
	{
	    var c,d;
	    var bflag=true;
	    var bnumber=false;
	    var bletter=false;
		for(var i=0;i<pwd.length;i++)
		{
			c=pwd.charAt(i);
			if(checkIsIntNum(c))
			{
			   bnumber=true;
			}
			else
			{
			   bletter=true;
			}
			for(var j=0;j<pwd.length;j++)
			{
			  d=pwd.charAt(j);
			  if(c==d&&(i!=j))
			  {
			    bflag=false;
			    break;
			  }
			}
			if(!bflag)
			  break;
		}
		return (bflag&&bnumber&&bletter);
	}
	
//<!--
	function getUserInfo()
	{
		//xus 17-6-15 保存时密码不符合规则也会被保存。
		if(!checkResult){
			alert("密码不符合规则！保存失败！");
			return;
		}
       var usrvo=new Object();	
       usrvo.username=$F('user_vo.string(username)');
       if(usrvo.username=="")
       {
         alert("<bean:message key="error.user.null"/>");
         return;
       }
      	var ctrlvalue="%'$#@!~^&*()_+\"'";
      var txtname=$('user_vo.string(username)').value;
      if(txtname.length>0&&(ctrlvalue.indexOf(txtname.substring(0,1))!=-1||txtname.indexOf("'")!=-1||txtname.indexOf("(")!=-1||txtname.indexOf(")")!=-1||txtname.indexOf("（")!=-1||txtname.indexOf("）")!=-1||txtname.indexOf(".")!=-1)||txtname.indexOf("\\")!=-1)
      {
      	alert("<bean:message key="error.user.number"/>");
      	return;
      }       
       /*把-转换成_*/
       var name=usrvo.username;
       <%if(Sql_switcher.searchDbServer()==2){%>
	     var namelength=0;
	     for(i = 0; i < name.length; i++){ 
	     	if(name.charCodeAt(i) > 128)
	      		namelength+=3;
	      	else
	      		namelength+=1;
	     }
	     if(namelength>10){
	     	alert("用户名不能多于10个字符[汉字占3个字符],不能创建用户!");
	     	return;
	     }
	   <%}%>
	   //验证特殊字符   用户名不允许含有特殊字符	jingq add 2014.6.13
	   	var teshu="`=[];'\\,./·【】；‘’、，。、~!@#$%^&*()+{}:\"|<>?！￥……（）：“”《》？";
		for(var j=0;j<name.length;j++){
			s = name.substring(j,j+1);
			if(teshu.indexOf(s)!=-1){
				alert("<bean:message key="error.user.usernameerror"/>"+teshu);
	   			return;
			}
		}
       var re = /-/g;
       usrvo.username=name.replace(re, "_"); 
       var fullname = $F('user_vo.string(fullname)');
       //处理fullname,过滤特殊字符  jingq  add  2014.6.3
       if(fullname.indexOf("\\")!=-1){
       		alert("<bean:message key="error.user.fullnameerror"/>");
      		return;
       }
       fullname = proStr(fullname);
       usrvo.fullname= fullname;
       <logic:empty name="isedit">
       	usrvo.password=getEncodeStr($F('user_vo.string(password)'));
      	usrvo.pwdok=getEncodeStr($F('user_vo.string(tablepriv)')); 
       </logic:empty>
       usrvo.email=$F('user_vo.string(email)'); 
       usrvo.phone=$F('user_vo.string(phone)'); 
       usrvo.org_dept=$F('user_vo.string(org_dept)');
       <logic:empty name="isedit"> 
       if(usrvo.password.toLowerCase()!=usrvo.pwdok.toLowerCase())
       {
         alert("<bean:message key="errors.sys.oldpassword"/>");
         return;       	
       }
       </logic:empty>
       <%if(bv){%>
       <logic:empty name="isedit">
       		var hashvo=new ParameterSet();
   	     hashvo.setValue("pwd_ok",$F('user_vo.string(password)'));
   			var request=new Request({asynchronous:false,onSuccess:pwd_ok,functionId:'1010010095'},hashvo);
   			function pwd_ok(outparamters)
   		  {
   		  	if("ok"==outparamters.getValue("mess")){
   		  	var state=$F('user_vo.string(state)');
    	       if(state==undefined)
    	       	 state="0";
    	       usrvo.state=state; 
    	       //if(usrvo instanceof Object) 
    	       //  alert("hello");
    	       //window.returnValue=usrvo;
    		   //window.close();
    		   returnParams(usrvo);
   		  		
   		  	}
   		  }
   			</logic:empty>
   			<logic:notEmpty name="isedit">
   			var state=$F('user_vo.string(state)');
   	       if(state==undefined)
   	       	 state="0";
   	       usrvo.state=state; 
   	       //if(usrvo instanceof Object) 
   	       //  alert("hello");
   		   //window.returnValue=usrvo;
	   		//window.close();
	   		returnParams(usrvo);
   			</logic:notEmpty>
       <%}else{%>
       
       var state=$F('user_vo.string(state)');
       if(state==undefined)
       	 state="0";
       usrvo.state=state; 
       //if(usrvo instanceof Object) 
       //  alert("hello");
	   //window.returnValue=usrvo;
	   //window.close();
	   returnParams(usrvo);
	   <%}%>
	}
	
	/*浏览器兼容，弹框返回值控制*/
	function returnParams(usrvo){
		if(window.showModalDialog){
			window.returnValue=usrvo;
		   window.close();
		}else{
			top.opener.add_or_update_user_success(usrvo);
			window.close();
		}
		
	}
	
	// 操作单位关联
	function getorg() {
	    var orgIdList = $('user_vo.string(org_dept)').value;
	    var orgTitleList = $('user_vo.string(fieldpriv)').value;
	    // 替换“`”为“,”
	    while (orgIdList.indexOf("`") > 0) {
	        orgIdList = orgIdList.replace("`", ",");
	    }
	    orgIdList = "," + orgIdList;
	    var obj = new Object();
	    obj.orgIdList = orgIdList;
	    obj.orgTitleList = orgTitleList;
	    var theurl = "/system/logonuser/add_org_tree.jsp";
	    var dw = 300,
	    dh = 400,
	    dl = (screen.width - dw) / 2,
	    dt = (screen.height - dh) / 2;
	    
	    /*浏览器兼容修改*/
	    if(window.showModalDialog){
	    	var return_vo = window.showModalDialog(theurl, obj, "dialogLeft:" + dl + "px;dialogTop:" + dt + "px;dialogWidth:" + dw + "px; dialogHeight:" + dh + "px;resizable:no;center:yes;scroll:yes;status:no");
	    	selectOrgSuccess(return_vo);
	     }else{
	    	 /*window.open无法传参，此处定义参数变量，子页面通过window.opener.paramObj获取参数  guodd 2019-03-20*/
	    	 window.paramObj = obj;
	    	 /*当弹框内设置完成时，会调用此界面的selectOrgSuccess方法  guodd 2019-03-20*/
	    	 window.open(theurl,'','width='+(dw+200)+',height='+dh+',left='+dl+',top='+dt+',resizable=no,titlebar=yes,location=no,status=no,scrollbars=no');
	     }
	    
	   // var return_vo = window.showModalDialog(theurl, obj, "dialogLeft:" + dl + "px;dialogTop:" + dt + "px;dialogWidth:" + dw + "px; dialogHeight:" + dh + "px;resizable:no;center:yes;scroll:yes;status:no");
	     
	}
	
	function selectOrgSuccess(return_vo){
		
		if (return_vo) {
	    	// 获取返回值
	        orgIdList = return_vo.orgIdList;
		    orgTitleList = return_vo.orgTitleList;
		  	
		  	// 去掉全部2015-06-13 wangzhongjun bug10068
		  	var ids = orgIdList.split(",");
		  	var title = orgTitleList.split(",");
		  	var orgIdStr = "";
		  	var orgTitleStr = "";
		  	for (i = 0; i < ids.length; i++) {
		  		if(ids[i] && ids[i] != 'UN') {
		  			orgIdStr = orgIdStr + ids[i] + ",";
		  		}
		  	}
		  	
		  	for (i = 0; i < title.length; i++) {
		  		if(title[i] && title[i] != '全部') {
		  			orgTitleStr = orgTitleStr + title[i] + ",";
		  		}
		  	}
		  	
		  	orgIdList = orgIdStr;
		  	orgTitleList = orgTitleStr;
		  	
		  	
		    if(orgIdList.indexOf(",") == 0) {
		    	orgIdList = orgIdList.substr(1);
		    }
		    // 回替换“,”为“`”
		    while (orgIdList.indexOf(",") >= 0) {
		        orgIdList = orgIdList.replace(",", "`");
		    }
		    
		    
		    // 刷新页面
		    $('user_vo.string(org_dept)').value = orgIdList;
		    $('user_vo.string(fieldpriv)').value = orgTitleList;
	    }
		
	}
	
	// 选择全部
	function setAll() {
		$('user_vo.string(fieldpriv)').value="全部";
		$('user_vo.string(org_dept)').value="UN`";		
	}
	
	function dofilter(e){
	      e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
	      var key = window.event?e.keyCode:e.which;
	      //alert(key);
	      if(key==44)//过滤半角逗号
	          return false;
	   }
	//处理特殊字符   
	function proStr(str){
		var s="";					
		var b = "";
		for(var j=0;j<str.length;j++){
			s = str.substring(j,j+1);
			if(s=='#'){
				b+="nbspa";
			} else if(s=="；"){
				b+="quanjiao;hao";
			} else {
				b+=s;
			}
		}
		str = getEncodeStr(b);
		return str;
	}
//-->
</script>
<style>
      .passWordCheck{
          background:url(/images/bubble-l.png) no-repeat left center;
          border:none;
          top:<%=top%>;
          left:365;
          width:300px;
          padding-left:10;
          position:relative;
          display:none;
      }
      .checkList{
         text-overflow:ellipsis;
         padding:2 0 2 10px;
         height:18px;
         width:270px;
         white-space: nowrap;
         background:url(/images/password.png) no-repeat 0 8
      }
   </style>
   <script>
       var numChecker = '1234567890';
       var wordChecker = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
       var charCheker = '%$#@!~^&*()+"\',';
       var checkResult = true;
       function showPassWordCheckBox(input,visible){
    	       var box = document.getElementById('passWordCheckBox');
    	       if(!visible && checkResult){
    	    	   	   box.style.display='none';
    	    	       return;
    	       }
    	       box.style.display='block';
       }
       function checkPassWord(input){
    	       var value = input.value.split(''),b,
    	       lengthRule = document.getElementById('lengthRule'),
    	         charRule = document.getElementById('charRule'),
    	         spaceRule = document.getElementById('spaceRule');
    	         lengthRule.style.color = 'black';
             charRule.style.color = 'black'; 
             spaceRule.style.color = 'black';
             lengthRule.style.backgroundPosition='0 8';
             spaceRule.style.backgroundPosition='0 8';
             charRule.style.backgroundPosition='0 8';
             checkResult = true;
            if(value.length==0)
            	   return;
           
          var hasNum = false;
          var hasWord = false;
          var hasChar = false;
          var hasSpace = false;
          var wrongChar = false;
		  var noRepeatChar = true;
          
          var prechar = '';
            
    	       for(var i=0;i<value.length;i++){
    	    	       b = value[i];
    	    	       
    	    	       if(prechar.indexOf(b)>-1)
        	    		   noRepeatChar = false;
        	    	   prechar+=b;
        	    	   
    	    	       if(b==" "){
    	    	    	   		hasSpace = true;
    	    	       }else if(numChecker.indexOf(b)>-1){
    	    	    	   		hasNum = true;
    	    	       }else if(wordChecker.indexOf(b)>-1){
  	    	    	     	hasWord = true;
	    	       }else if(charCheker.indexOf(b)>-1){
	    	    	     	hasChar = true;
	    	       }else{
	    	    	   		wrongChar = true;
	    	       }
    	    	       
    	       }
    	       
    	       if(hasSpace){
    	    	   	  spaceRule.style.color='red';
 	    	      checkResult = false;
 	    	      spaceRule.style.backgroundPosition='0 -25';
    	       }
    	       
    	       if(wrongChar){
    	    	      charRule.style.color='red';
 	    	   	  charRule.style.backgroundPosition='0 -25';
 	    	   	  checkResult = false;
    	       }
    	       
    	       var rule1 = pwdRule=='2' && hasNum && hasWord && hasChar && value.length>=pwdLength && noRepeatChar;
    	       var rule2 = pwdRule=='1' && hasNum && hasWord && value.length>=pwdLength;
    	       var rule3 = pwdRule!='1' && pwdRule!='2';
    	       
    	       if(!(rule1 || rule2 || rule3)){
           	    checkResult = false;
           	    lengthRule.style.color='red';
           	    lengthRule.style.backgroundPosition='0 -25';
           }
    	       
    	       if(checkResult){
    	    	       lengthRule.style.backgroundPosition='0 -8';
	    	       spaceRule.style.backgroundPosition='0 -8';
	    	       charRule.style.backgroundPosition='0 -8';
    	       }
    	       
    	   
       }
   </script>
<html:form action="/system/logonuser/add_edit_user">

      <table width="510" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
          <tr height="20">

       		<td  align="left" class="TableRow" id="topic" colspan="2"><bean:message key="label.add.user"/>&nbsp;</td>
           	      
          </tr> 
                      <tr class="list3">
                	      <td align="right" nowrap style="border-right:none;border-top:none;border-bottom:none;"><bean:message key="label.username"/></td>
                	      <td align="left" nowrap style="border-left:none;border-top:none;border-bottom:none;">
                	      	<%if(Sql_switcher.searchDbServer()==2){%>
                	      	<html:text styleId="useranmeele" name="logonUserForm" property="user_vo.string(username)" size="10" maxlength="20" styleClass="text" onkeydown="" style="width:200px;"/>
                	      	(用户名不能多于10个字符,汉字占3个字符)
                	      	<%}else{ %>
                	      	<html:text styleId="useranmeele" name="logonUserForm" property="user_vo.string(username)" size="20" maxlength="20" styleClass="text" onkeydown="" style="width:200px;"/>
                	      	<%} %>
                          </td>
                      </tr>
                      <tr class="list3">
                	      <td align="right" nowrap style="border-right:none;border-top:none;border-bottom:none;"><bean:message key="label.fullname"/></td>
                	      <td align="left" nowrap style="border-left:none;border-top:none;border-bottom:none;">
                	      	<html:text name="logonUserForm" property="user_vo.string(fullname)" size="20" maxlength="20" styleClass="text" style="width:200px;"/>    	      
                          </td>
                      </tr>   
                      <logic:empty name="isedit">
	                      <tr class="list3">
	                	      <td align="right" nowrap style="border-right:none;border-top:none;border-bottom:none;"><bean:message key="label.new.password"/></td>
	                	      <td align="left" nowrap style="border-left:none;border-top:none;border-bottom:none;">
	                	      	<html:password name="logonUserForm" property="user_vo.string(password)" onkeyup="checkPassWord(this)" onfocus="showPassWordCheckBox(this,1)" onblur="showPassWordCheckBox(this,0)" size="20" maxlength="20" styleClass="text"  onkeypress="return dofilter(event);" style="width:200px;"/>    	      
	                          </td>
	                      </tr>                                           
	                      <tr class="list3">
	                	      <td align="right" nowrap style="border-right:none;border-top:none;border-bottom:none;"><bean:message key="label.password.ok"/></td>
	                	      <td align="left" nowrap style="border-left:none;border-top:none;border-bottom:none;">
	                	      	<html:password name="logonUserForm" property="user_vo.string(tablepriv)" size="20" maxlength="20" styleClass="text" onkeypress="return dofilter(event);" style="width:200px;"/>    	      
	                          </td>
	                      </tr>
                      </logic:empty>
                      <tr class="list3">
                	      <td align="right" nowrap style="border-right:none;border-top:none;border-bottom:none;"><bean:message key="selfservice.param.otherparam.email_title"/></td>
                	      <td align="left" nowrap style="border-left:none;border-top:none;border-bottom:none;">
                	      	<html:text name="logonUserForm" property="user_vo.string(email)" size="20" maxlength="100" styleClass="text" style="width:200px;"/>    	      
                          </td>
                      </tr>    
                      <tr class="list3">
                	      <td align="right" nowrap style="border-right:none;border-top:none;border-bottom:none;"><bean:message key="selfservice.param.otherparam.phone_title"/></td>
                	      <td align="left" nowrap style="border-left:none;border-top:none;border-bottom:none;">
                	      	<html:text name="logonUserForm" property="user_vo.string(phone)" size="20" maxlength="20" styleClass="text" style="width:200px;"/>    	      
                          </td>
                      </tr>  
                      <tr class="list3">
                	      <td align="right" nowrap style="border-right:none;border-top:none;border-bottom:none;"><bean:message key="system.unit_id"/></td>
                	      <td align="left" nowrap style="border-left:none;border-top:none;border-bottom:none;">
                	      	<html:text name="logonUserForm" property="user_vo.string(fieldpriv)" size="20" maxlength="60" readonly="true" styleClass="text" style="width:200px;"/> 
                	      	<img src="/images/code.gif" onclick="getorg();"/>   	      
                	      	<html:hidden name="logonUserForm" property="user_vo.string(org_dept)"/>  
         					<!-- jingq add 2014.4.25 修改用户管理中新增用户时，根据权限判断操作单位是否显示全部按钮 --> 
                	      	<%
                	      	//UserView userView = (UserView)session.getAttribute(WebConstant.userView); 
                	      	String managepriv = userView.getManagePrivCode()+userView.getManagePrivCodeValue();
                	      	if(userView.isSuper_admin()==true||managepriv.equals("UN`")||managepriv.equals("UN")){ %>
         					<html:button styleClass="mybutton" property="b_all" onclick="setAll();">
            					<bean:message key="label.all"/>
	 	    				</html:button>  
	 	    				<%} %>                      	      	  	      
                          </td>
                      </tr>                                                              
                      <tr class="list3">
                	      <td align="right" nowrap style="border-right:none;">
                	      <%if(request.getParameter("b_query")!=null){ %>
                                <bean:message key="column.sys.valid"/></td><td style="border-left:none;" align="left"><html:checkbox name="logonUserForm" property="user_vo.string(state)" value="1"/>
                           <%}else{ %>
                                <bean:message key="column.sys.valid"/></td><td style="border-left:none;padding:0" align="left"><input type="checkbox" name="user_vo.string(state)" value="1" checked="checked">
                           <%} %>
                           </td>
                      </tr>                                             

                                                   
          <tr class="list3">
            <td align="center" colspan="2" style="height:35px;border:none;">
         	<html:button styleClass="mybutton" property="b_save" onclick="getUserInfo();">
            		<bean:message key="button.save"/>
	 	    </html:button>
         	<html:button styleClass="mybutton" property="br_return" onclick="window.close();">
            		<bean:message key="button.close"/>
	 	    </html:button>            
            </td>
          </tr>          
      </table>
      <%
      String passwordrule=SysParamBo.getSysParamValue("SYS_SYS_PARAM","passwordrule");
      String passwordlength = SysParamBo.getSysParamValue("SYS_SYS_PARAM","passwordlength");
   %>
   <script>
       var pwdRule = '<%=passwordrule%>';
       var pwdLength = '<%=passwordlength%>';
   </script>
  <div id="passWordCheckBox" class="passWordCheck">
           <ul style="background-color:white;border:1px #b0b0b0 solid; border-left:none;min-height:60;margin:0px;padding-left:2px;list-style:none;">
              <li id='lengthRule' class="checkList">
					   <%
                             if("2".equals(passwordrule)){
                            	 String msg = ResourceFactory.getProperty("error.password.validate.strong").replace("{0}", SysParamBo.getSysParamValue("SYS_SYS_PARAM","passwordlength")).replaceAll("\"'","");
                             %>
                              长度不少于<%=passwordlength%>位,必须包含字母、数字和符号且不重复
                              <%}else if("1".equals(passwordrule)) {
                            	  String msg = ResourceFactory.getProperty("error.password.validate.moderate").replace("{0}", SysParamBo.getSysParamValue("SYS_SYS_PARAM","passwordlength"));
                              %>
                              长度不少于<%=passwordlength%>位,必须包含字母和数字
                              <%}else{%>
                                新密码强度不做要求
                              <%} %>
			  </li>
              <li id='charRule' class="checkList">允许的符号:~ ! @ # $ % ^ & * ( ) + " ' ,</li>
              <li id='spaceRule' class="checkList">不允许有空格</li>
           </ul>
  </div>
</html:form>

<script type="text/javascript">
<!--
	if(window.dialogArguments)
	{
		Element.readonly('user_vo.string(username)');
		$('topic').innerText="<bean:message key="label.edit.user"/>";
	}else{
		var useranmeEle = document.getElementById('useranmeele');
		if(useranmeEle.value && useranmeEle.value.length>0)
			Element.readonly('user_vo.string(username)');
			
		
	}
//-->
</script>
