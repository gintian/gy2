<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%String [] onlyIndex=null;
//唯一指标，即只能有一条记录
String onlyFieldSet = "A01,A0A";
%>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7"> 
</head>
<script type="text/javascript" src="/module/utils/js/template.js"></script>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link> 
<script type="text/javascript" src="/ajax/constant.js"></script>
<script type="text/javascript" src="/ajax/common.js"></script>
<script type="text/javascript" src="/ajax/control.js"></script>
<script type="text/javascript" src="/ajax/dataset.js"></script>
<script type="text/javascript" src="/ajax/editor.js"></script>
<script type="text/javascript" src="/ajax/dropdown.js"></script>
<script type="text/javascript" src="/ajax/table.js"></script>
<script type="text/javascript" src="/ajax/menu.js"></script>
<script type="text/javascript" src="/ajax/tree.js"></script>
<script type="text/javascript" src="/ajax/pagepilot.js"></script>
<script type="text/javascript" src="/ajax/command.js"></script>
<script type="text/javascript" src="/ajax/format.js"></script>

<script type="text/javascript" src="/components/dateTimeSelector/dateTimeSelector.js"></script> 
<script type="text/javascript" src="/js/function.js"></script>
<script type="text/javascript" src="/js/constant.js"></script>
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
<script type="text/javascript" src="/hire/hireNetPortal/employNetPortal.js"></script>
<script type="text/javascript" src="/components/codeSelector/codeSelector.js"></script>
<script type="text/javascript" src="/components/extWidget/proxy/TransactionProxy.js"></script>
<script type="text/javascript">dateFormat='yyyy.mm.dd'</script>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,
                 org.apache.commons.beanutils.LazyDynaBean,
                  com.hrms.struts.taglib.CommonData,
                 java.util.*,com.hrms.hjsj.sys.ResourceFactory"%>
<%@ page import="com.hrms.hjsj.utils.Sql_switcher,
                 com.hrms.hjsj.sys.Constant,com.hrms.struts.constant.SystemConfig,
                 com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%
EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
HashMap hm = employPortalForm.getFormHM();
String channelName = (String)hm.get("channelName");
ArrayList attachCodeSet = (ArrayList)hm.get("attachCodeSet");
%>
<html>
<style type="text/css">
	.img-middle{vertical-align:middle;}
	.input {
	    width: 200px;
	    float: left;
	    background: url(../../images/hire/input_l.gif);
	    position: relative;
	    white-space: nowrap;
	    z-index:1;
    }
</style>
<script type="text/javascript">
	var a0100 = "${employPortalForm.a0100}";
	var cardid = "${employPortalForm.admissionCard}";
	var nbase = "${employPortalForm.dbName}";
	var uploadflag = false;
	<% int m=0;  %>
	<%
    String dbtype="1";
  if(Sql_switcher.searchDbServer()== Constant.ORACEL)
  {
    dbtype="2";
  }
  else if(Sql_switcher.searchDbServer()== Constant.DB2)
  {
    dbtype="3";
  }
  UserView userView=(UserView)session.getAttribute(WebConstant.userView);
  String userViewName="";
  String isHeadhunter ="";
  if(userView!=null){
      userViewName=(String)userView.getUserName();
  	  isHeadhunter = (String)userView.getHm().get("isHeadhunter");//判断是不是猎头招聘登录
  }
 %>
  var only_str="";
  var blackFieldValue="";
	function checkBrowser()
	{
	    var Sys = {};
	    var ua = navigator.userAgent.toLowerCase();
	    var s;
	    (s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1] :
	    (s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1] :
	    (s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1] :
	    (s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1] :
	    (s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;
	if (Sys.chrome) return true;
	else return false;
	}
	var operating = false;
	function sub(flag,anwserFlag)////  0：修改 1:保存并添加  2:保存&下一步 3、下一步 4、上一步finished、结束填写
	{
		if(flag=='finished'){//不保存直接结束
			Ext.Msg.show({
				title:"提示信息",
				message:YOUR_RESUME_SUBMIT_SUCCESS,
				buttons: Ext.Msg.OK,
			    icon: Ext.Msg.INFO,
			    fn: function(btn) {
			    	if (btn === 'ok') {
	        			document.location="/hire/hireNetPortal/search_zp_position.do?b_query=link&operate=init&abcd=1";
			    	}
			    }
			});
            return;
        }
		if (operating)
			return;
	
		operating = true;
	
        var onlyField=document.employPortalForm.onlyField.value;
        var blackField=document.employPortalForm.blackField.value;
        var arr=onlyField.split(",");
        if(trim(blackFieldValue).length!=0)
            blackFieldValue="";
        var a_flag=$("flag");
        if(flag=='0')
            a_flag.value='2';
        else
            a_flag.value=flag;
        if(trim(only_str).length!=0)
           only_str="";
		if(flag!='3'&&flag!='4')
		{
		<%  m=0;  %>
		<logic:iterate  id="element"    name="employPortalForm"  property="resumeFieldList" indexId="index"> 
			<logic:equal name="element" property="itemtype" value="M">
				var a<%=m%>=document.getElementsByName("resumeFieldList[<%=m%>].value");
				if(a<%=m%>[0]!=null&&trim(a<%=m%>[0].value).length!=0)
				{
					if(IsOverStrLength(a<%=m%>[0].value,5000))
					{
						if(anwserFlag=='1')
						{
							var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
							alert(replaceAll(vv.innerHTML,"<BR>","")+OVERSTEP_LENGTH_SCOPE);
						}
						else
						{
							alert("<bean:write  name="element" property="itemdesc"/>"+OVERSTEP_LENGTH_SCOPE);
						}
						operating = false;
						return;
					}
				}
			    
			</logic:equal>
                
			<logic:equal name="element" property="itemtype" value="D">
				var a<%=m%>=document.getElementsByName("resumeFieldList[<%=m%>].value");
				if(a<%=m%>[0]!=null&&trim(a<%=m%>[0].value).length!=0)
				{
					var tempValue = a<%=m%>[0].value;
					var reg = /^(\d{4})((-|\.)(\d{1,2}))?((-|\.)(\d{1,2}))?$/;
					    
					if(!reg.test(tempValue))
					{
						if(anwserFlag=='1')
						{
							var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
							alert(replaceAll(vv.innerHTML,"<BR>","")+THE_RIGHT_DATEFORMAT+"！");
						}
						else
						{
							alert("<bean:write  name="element" property="itemdesc"/> "+THE_RIGHT_DATEFORMAT+"！");
						}
						operating = false;
						return;
					}
					var year="";
					var month="";
					var day="";
					if(trim(tempValue).length<=4)
					{
						year=trim(tempValue);
						if(year<1900||year>2100)
						{
							if(anwserFlag=='1')
							{
								var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
								alert(replaceAll(vv.innerHTML,"<BR>","")+YEAR_SCORPE+"！");
							}
							else
							{
								alert("<bean:write  name="element" property="itemdesc"/> "+YEAR_SCORPE+"！");
							}
							operating = false;
							return;
						}
					}
					
					if(trim(tempValue).length>4&&trim(tempValue).length<8)
					{
						year=tempValue.substring(0,4);
						month=tempValue.substring(5,trim(tempValue).length);
						if(year<1900||year>2100)
						{
							if(anwserFlag=='1')
							{
								var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
								alert(replaceAll(vv.innerHTML,"<BR>","")+YEAR_SCORPE+"！");
							}
							else
							{
								alert("<bean:write  name="element" property="itemdesc"/> "+YEAR_SCORPE+"！");
							}
							operating = false;
							return;
						}
						if (month < 1 || month > 12) {
							if(anwserFlag=='1')
							{
								var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
								alert(replaceAll(vv.innerHTML,"<BR>","")+THE_MONTH_SCOPE+"！");
							}
							else
							{
								alert("<bean:write  name="element" property="itemdesc"/> "+THE_MONTH_SCOPE+"！");
							}
							operating = false;
							return;
						}
					}
					if(trim(tempValue).length>=8)
					{
						var split="";
						if(tempValue.indexOf(".")!=-1)
						{
							split=".";
						}
						else
						{
							split="-";
						}
						year=tempValue.substring(0,4);
						month=tempValue.substring(5,tempValue.lastIndexOf(split));
						day=tempValue.substring(tempValue.lastIndexOf(split)+1);
						if(year<1900||year>2100)
						{
							if(anwserFlag=='1')
							{
								var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
								alert(replaceAll(vv.innerHTML,"<BR>","")+YEAR_SCORPE+"！");
							}
							else
							{
								alert("<bean:write  name="element" property="itemdesc"/>"+YEAR_SCORPE+"！");
							}
							operating = false;
							return;
						}
						if(!isValidDate(day, month, year))
						{
							if(anwserFlag=='1')
							{
								var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
								alert(replaceAll(vv.innerHTML,"<BR>","")+THE_MONTH_AND_THE_DAY_SCOPE+"！");
							}
							else
							{
								alert("<bean:write  name="element" property="itemdesc"/>"+THE_MONTH_AND_THE_DAY_SCOPE+"！");
							}
							operating = false;
							return;
						}
					}
				
				}
			</logic:equal>      
                
			<logic:equal name="element" property="itemtype" value="N">
				var a<%=m%>=document.getElementsByName("resumeFieldList[<%=m%>].value")
				if(a<%=m%>[0]!=null&&trim(a<%=m%>[0].value).length!=0)
				{
					var tempValue = a<%=m%>[0].value;
					var myReg =/^(\d+)(\.\d+)?$/
					if(!myReg.test(tempValue)) 
					{
						if(anwserFlag=='1')
						{
							var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
							alert(replaceAll(vv.innerHTML,"<BR>","")+PLEASE_INPUT_NUMBER+"！");
						}
						else
						{
							alert("<bean:write  name="element" property="itemdesc"/>"+PLEASE_INPUT_NUMBER+"！");
						}
						operating = false;
						return;
					}
					
					<logic:equal name="element" property="decimalwidth" value="0">
						if(tempValue.indexOf(".")!=-1)
						{
							if(anwserFlag=='1')
							{
								var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
								alert(replaceAll(vv.innerHTML,"<BR>","")+THE_TYPE_IS_INTEGER+"！");
							}
							else
							{
								alert("<bean:write  name="element" property="itemdesc"/>"+THE_TYPE_IS_INTEGER+"！");
							}
							operating = false;
							return;
						}
					</logic:equal>
					
					<logic:notEqual name="element" property="decimalwidth" value="0">
						var vv="";
						if(tempValue.indexOf(".")!=-1)
						{
							vv=tempValue.substring(0,tempValue.indexOf("."))
						}
						else
						{
							vv=tempValue;
						}
						var dd="<bean:write  name="element" property="itemlength"/>";
						if(vv.length>(dd*1))
						{
							if(anwserFlag=='1')
							{
								var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
								alert(replaceAll(vv.innerHTML,"<BR>","")+"超出长度范围！");
							}
							else
							{
								alert("<bean:write  name="element" property="itemdesc"/>超出长度范围！");
							}
							operating = false;
							return;
						}
					</logic:notEqual>
				}
			</logic:equal>
			
			<logic:equal name="element" property="itemtype" value="A">
				<logic:equal name="element" property="codesetid" value="0">
					var a<%=m%>=document.getElementsByName("resumeFieldList[<%=m%>].value")
					var itemid="<bean:write  name="element" property="itemid"/>";
					if(itemid.toUpperCase()=="A0101"){
						if(trim(a<%=m%>[0].value)==""){
							alert("姓名不能为空!");
							operating = false;
							return;
						}
					}
					if(a<%=m%>[0]!=null&&trim(a<%=m%>[0].value).length!=0)
					{
						var tempValue = a<%=m%>[0].value;
						if(itemid.toUpperCase()==blackField.toUpperCase())
						{
							for(var t=0;t<arr.length;t++)
							{
								if(arr[t]==itemid)
								{
									only_str+=","+itemid+"/"+tempValue+"/"+"<bean:write  name="element" property="itemdesc"/>";
									break;
								}
							}
						}
						if(itemid.toUpperCase()==blackField.toUpperCase())
						{
							blackFieldValue=trim(tempValue);
						}
						if(trim(tempValue).length!=0)
						{
							if(IsOverStrLength(tempValue,<bean:write  name="element" property="itemlength"/>))
							{
								if(anwserFlag=='1')
								{
									var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
									alert(replaceAll(vv.innerHTML,"<BR>","")+OVER_LENGTH_SCOPE);
								}
								else
								{
									alert("<bean:write  name="element" property="itemdesc"/>"+OVER_LENGTH_SCOPE);
								}
								operating = false;
								return;
							}
						}
					}
				</logic:equal>
				
				<logic:notEqual name="element" property="codesetid" value="0">
					<logic:equal name="element" property="isMore" value="1">
						var a<%=m%>=document.getElementsByName("resumeFieldList[<%=m%>].value")
						var aa<%=m%>=document.getElementsByName("resumeFieldList[<%=m%>].viewvalue")
						if(a<%=m%>[0]!=null&&aa<%=m%>[0]!=null&&trim(aa<%=m%>[0].value).length==0)
						{                           
							a<%=m%>[0].value="";
						}
					</logic:equal>
				</logic:notEqual>
			</logic:equal>          
            <% m++; %>  
        </logic:iterate>
        <% m=0;%>
        //是否有值
        var fullFlag = true;
        //第一个必填指标描述
        var itemdesc = "";
        <logic:iterate id="element" name="employPortalForm" property="resumeFieldList" indexId="index">
            var a<%=m%>=document.getElementsByName("resumeFieldList[<%=m%>].value");
            <logic:equal name="element" property="must" value="1">
                if(a<%=m%>[0]!=null&&trim(a<%=m%>[0].value).length==0)
                {
                	if(!itemdesc){
	                    if(anwserFlag=='1')
	                    	itemdesc = "<bean:write  name="element" property="itemmemo2"/>";
	                    else
	                    	itemdesc = "<bean:write  name="element" property="itemdesc"/>";
                	}
			        operating = false;
                }
            </logic:equal>
			if(a<%=m%>[0]!=null&&trim(a<%=m%>[0].value).length>0){
               	if(fullFlag){
               		fullFlag = false;
          		}
            }
        <% m++; %>
        </logic:iterate>
       // 如果itemdesc有描述，说明有必填指标没填，fullFlag为false 说明有指标填写了，这种情况下，必填指标都必填
        if(!fullFlag&&itemdesc){
        	alert(itemdesc+THIS_IS_MUST_FILL+"！");
        	return;
        }
        }
        sub2(flag);
	}

	function query()
	    {
        
        <% int x=0;  %>
        <logic:iterate  id="element"    name="employPortalForm"  property="conditionFieldList" indexId="index"> 
        <% if(index<=2){ %>
                <logic:equal name="element" property="itemtype" value="D">
                    var a<%=x%>=document.getElementsByName("conditionFieldList[<%=x%>].value")
                    if(trim(a<%=x%>[0].value).length!=0)
                    {                       
                         var myReg =/^(-?\d+)(\.\d+)?$/
                         if(IsOverStrLength(a<%=x%>[0].value,10))
                         {
                             alert("<bean:write  name="element" property="itemdesc"/> "+DATE_FORMAT_IS_NOT_RIGHT+"！");
                             return;
                         }
                         else
                         {
                            if(trim(a<%=x%>[0].value).length!=10)
                            {
                                 alert("<bean:write  name="element" property="itemdesc"/> "+DATE_FORMAT_IS_NOT_RIGHT+"！");
                                 return;
                            }
                            var year=a<%=x%>[0].value.substring(0,4);
                            var month=a<%=x%>[0].value.substring(5,7);
                            var day=a<%=x%>[0].value.substring(8,10);
                            if(!myReg.test(year)||!myReg.test(month)||!myReg.test(day)) 
                            {
                                 alert("<bean:write  name="element" property="itemdesc"/> "+DATE_FORMAT_IS_NOT_RIGHT+"！");
                                 return;
                            }
                            if(year<1900||year>2100)
                            {
                                 alert("<bean:write  name="element" property="itemdesc"/> "+YEAR_SCORPE+"！");
                                 return;
                            }
                            
                            if(!isValidDate(day, month, year))
                            {
                                 alert("<bean:write  name="element" property="itemdesc"/> "+DATE_FORMAT_IS_NOT_RIGHT+"！");
                                 return;
                            }
                         }
                     }
                </logic:equal>  
                
                            
                <logic:equal name="element" property="itemtype" value="N">
                    var a<%=x%>=document.getElementsByName("conditionFieldList[<%=x%>].value")
                    if(trim(a<%=x%>[0].value).length!=0)
                    {                       
                         var myReg =/^(-?\d+)(\.\d+)?$/
                         if(!myReg.test(a<%=x%>[0].value)) 
                         {
                            alert("<bean:write  name="element" property="itemdesc"/>"+PLEASE_INPUT_NUMBER+"！");
                            return;
                         }
                     }
                </logic:equal>      
                <logic:equal name="element" property="itemtype" value="A">
                    <logic:equal name="element" property="codesetid" value="0">
                        var a<%=x%>=document.getElementsByName("conditionFieldList[<%=x%>].value")
                        if(trim(a<%=x%>[0].value).length!=0)
                        {
                            if(IsOverStrLength(a<%=x%>[0].value,<bean:write  name="element" property="itemlength"/>))
                            {
                                alert("<bean:write  name="element" property="itemdesc"/>"+OVER_LENGTH_SCOPE);
                                return;
                            }
                        }
                    
                    </logic:equal>
                    <logic:notEqual name="element" property="codesetid" value="0">
                        <logic:equal name="element" property="isMore" value="1">
                        var a<%=x%>=document.getElementsByName("conditionFieldList[<%=x%>].value")
                        var aa<%=x%>=document.getElementsByName("conditionFieldList[<%=x%>].viewvalue_value")
                        if(trim(aa<%=x%>[0].value).length==0)
                        {                           
                            a<%=x%>[0].value="";
                        }
                        </logic:equal>
                    </logic:notEqual>
                    
                </logic:equal>          
            <% x++;} %>  
        </logic:iterate>    
        
        document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_query=link&returnType=search";
        document.employPortalForm.submit();
    }
    function showlist(code){
            var floor= document.getElementById("floor"+code);
            floor.style.display='block';
                var input=document.getElementById("input"+code)
                input.style.zIndex='10000'
            
            floor.focus();
    }
    function change1(code,name,value,tt){
        var span=document.getElementById("spanf"+code);
        var floor= document.getElementById("floorf"+code);
        var input=document.getElementById("inputf"+code);
        var img=document.getElementById("img2f");
        var con=0;
        var ert=0;
        var has=false;
        if(name.length>=6){
            span.innerHTML =name.substring(0,7);
        }else{
            span.innerHTML =name;;
        }
            floor.style.display='none';
            var inpt=document.getElementsByName(tt)[0];
        inpt.value=value;
        }
        function hide3(code){
            Element.hide("floorf"+code);
            var input=document.getElementById("inputf"+code)
            input.style.zIndex='0';
        }   
    function showlist1(code){
            var floor= document.getElementById("floorf"+code);
            floor.style.display='block';
            var input=document.getElementById("inputf"+code)
            input.style.zIndex='10000'          
            floor.focus();
    }
    
    function change(code,name,value,tt){
        var span=document.getElementById("span"+code);
        var floor= document.getElementById("floor"+code);
        var input=document.getElementById("input"+code);
        if(name.length>=9){
            span.innerHTML =name.substring(0,10);
        }else{
            span.innerHTML =name;;
        }
        
        floor.style.display='none';
        var inpt=document.getElementsByName(tt)[0];
        input.style.zIndex='0';
        inpt.value=value;
    }
    function hide(code){
         Element.hide("floor"+code);
         var input=document.getElementById("input"+code)
         input.style.zIndex='0';
    }
	function initCard(){
		var rl = document.getElementById("hostname").href;     
		var aurl=rl;
		var DBType="<%=dbtype%>";
		var UserName="<%=userViewName%>";
		var obj = document.getElementById('CardPreview1');   
		var superUser="1";
		var menuPriv="";
		var tablePriv="";
		if(obj==null)
		{
			return false;
		}
		obj.SetSuperUser(superUser);
		obj.SetUserMenuPriv(menuPriv);
		obj.SetUserTablePriv(tablePriv);
		obj.SetURL(aurl);
		obj.SetDBType(DBType);
		obj.SetUserName(UserName);
		obj.SetUserFullName("su");
	}   
	function previewTableByActive(){
		var hashvo=new ParameterSet();
		hashvo.setValue("dbname","${employPortalForm.dbName}");   
		hashvo.setValue("inforkind","1"); 
		hashvo.setValue("flag","hire");
		hashvo.setValue("id","${employPortalForm.a0100}"); 
		var request=new Request({method:'post',onSuccess:showPrint,functionId:'07020100078'},hashvo);
	}
	function isHaveAttachCodeSet(){
		var htmlStr = "<table width='0' border='0' cellspacing='0' cellpadding='0' class='table table3'>";
		<%if(attachCodeSet !=null && attachCodeSet.size()>0){
			for(int i=0;i<attachCodeSet.size();i++){
				HashMap map = (HashMap)attachCodeSet.get(i);
		%>
				htmlStr += createFileSelect('<%=map.get("itemDesc")%>',<%=i%>, '<%=map.get("notNull") %>');
		<%	}
		}else{%>
			htmlStr += createFileSelect("选择附件",0,'1');
		<%}%>
		htmlStr +="<tr><td width='100%' align='center' colspan='2'>";
		htmlStr +="<br>";
		<logic:equal value="0" name="employPortalForm" property="writeable">
			htmlStr +="<img src='/images/hire/upp.gif' border=0 onclick=uploadnn('0'); style='cursor:hand'/>&nbsp;&nbsp;"
				+"<img src='/images/hire/uppandeng.gif' border=0 onclick=uploadnn('1'); style='cursor:hand'/>&nbsp;&nbsp;"
				+"<img src='/images/hire/finish.gif' border=0 onclick=uploadnn('2'); style='cursor:hand'/>";
     	</logic:equal>
     	htmlStr +="</td></tr></table>";
		document.getElementById("fileContent").innerHTML = htmlStr;
	}
	function createFileSelect(title,name, flag){
		if("0" == flag)
			return "";
		
		var isChrome = checkBrowser();
		var htmlStr ="<tr><td width='217' height='34' align='right' style='FONT-SIZE: 12px;font-family: 微软黑体;color:black;'>"+title+"&nbsp;</td>";
		htmlStr +="<td width='460'><div>";
		if(isChrome)
		{
			htmlStr += "<input id='attachFile"+name+"' name='attachFile["+name+"]' type='file' size='50'  class='input_file'  onchange='return Resume_changeName(this);' >";
			htmlStr +="<input type='text' id='resumefileattachFile["+name+"]'  readonly='true' size='38' maxlength='200'>";
			htmlStr +="&nbsp;<input type='button' value='浏览...' onclick=resume_openFile('"+name+"')>";
		}else{
			htmlStr +="<input id='attachFile"+name+"' name='attachFile["+name+"]' type='file' size='50' style='border-top:1px solid #d0d0d0;border-left:1px solid #d0d0d0;border-right:1px solid #d0d0d0;border-bottom:1px solid #d0d0d0;'>";
		}
		
		if("2" == flag)
			htmlStr += "<font style='color: red;'>*</font>";
			
		htmlStr += "</div></td></tr>";
		return htmlStr;
	}
	
	//清空已输入内容
	function clean_value(e,obj){
		 var event = e?e:window.event;
		 if(event.keyCode==8){
			 obj.value="";
			 var ojb_name = obj.name;
			 document.getElementsByName(ojb_name.split(".")[0]+".viewvalue_value")[0].value="";
			 document.getElementsByName(ojb_name.split(".")[0]+".value")[0].value="";
		 }
	} 
 
</script>
	
	<body >
	<div id='chajian' ></div>
	<form name="employPortalForm" method="post" action="/hire/hireNetPortal/search_zp_position.do" enctype="multipart/form-data" onsubmit="return validate()">
            <%
            //配置简历附件大小
            String maxFileSize = (String)employPortalForm.getFormHM().get("maxFileSize");
            int maxSize = maxFileSize==null||"".equals(maxFileSize)?-1:Integer.parseInt(maxFileSize);
            ArrayList fieldSetList=employPortalForm.getFieldSetList();          
            int index=Integer.parseInt((String)employPortalForm.getCurrentSetID());
            String a0100=employPortalForm.getA0100()==null?"":employPortalForm.getA0100();
            String dbName=employPortalForm.getDbName();
            String writeable=employPortalForm.getWriteable();
            String onlyField=employPortalForm.getOnlyField();
            String isOnlyChecked=employPortalForm.getIsOnlyCheck();
            String hireMajorCode=employPortalForm.getHireMajorCode();
            String hireMajor=employPortalForm.getHireMajor();
            String emailColumn=employPortalForm.getEmailColumn();
            ArrayList showFieldList=employPortalForm.getShowFieldList();
            ArrayList showFieldDataList=employPortalForm.getShowFieldDataList();
            String isPhoto=employPortalForm.getIsPhoto();
            String isUpPhoto=employPortalForm.getIsUpPhoto();
            String isExp=employPortalForm.getIsExp();
            String isAttach=employPortalForm.getIsAttach();
            String  birthdayName=employPortalForm.getBirthdayName();
            String  ageName=employPortalForm.getAgeName();
            String  axName=employPortalForm.getAxName();
            ArrayList uploadFIleList = employPortalForm.getUploadFileList();
            ArrayList mediaList=employPortalForm.getMediaList();
            String opt=employPortalForm.getOpt();
            HashMap editableMap=employPortalForm.getEditableMap();
            String isHaveEditableField=employPortalForm.getIsHaveEditableField();
            String idItemId="";
            String blackFieldItem=employPortalForm.getBlackField();
            String blackNbase=employPortalForm.getBlackNbase();
            String userName =employPortalForm.getUserName();
            int idIndex=-1;
            int blackIndex=-1;
            String answerSet = employPortalForm.getAnswerSet();
            String onlyName=employPortalForm.getOnlyName()==null?"":employPortalForm.getOnlyName();
            String hirechannel=employPortalForm.getHireChannel();
            String zpUnitCode=employPortalForm.getZpUnitCode();
            ArrayList conditionFieldList=employPortalForm.getConditionFieldList();
            String idType=employPortalForm.getId_type()==null?"":employPortalForm.getId_type();
          //zxj 20141231 对conditionFieldList中的数据进行安全过滤，防止跨站脚本等攻击
            for(int i=0;i< conditionFieldList.size() && i<=2;i++)
            {
                LazyDynaBean abean = (LazyDynaBean)conditionFieldList.get(i);
                if(abean == null)
                    continue;
                
                String itemid = (String)abean.get("itemid");
                abean.set("itemid", PubFunc.hireKeyWord_filter(itemid));
                
                String value = PubFunc.getReplaceStr((String)abean.get("value"));
                abean.set("value", PubFunc.hireKeyWord_filter(value));
                
                String viewvalue = PubFunc.getReplaceStr((String)abean.get("viewvalue"));
                abean.set("viewvalue", PubFunc.hireKeyWord_filter(viewvalue));
                
                String type = (String)abean.get("itemtype");
                abean.set("type", PubFunc.hireKeyWord_filter(type));
                
                String codesetid = (String)abean.get("codesetid");
                abean.set("codesetid", PubFunc.hireKeyWord_filter(codesetid));
            }
            
            hirechannel = PubFunc.hireKeyWord_filter(hirechannel);
            
            zpUnitCode = PubFunc.hireKeyWord_filter(zpUnitCode);
            
             ArrayList unitList=employPortalForm.getUnitList();
             LazyDynaBean kbean=null;
            ArrayList kll=new ArrayList();
            int hi=0;
            String username=employPortalForm.getUserName(); 
            String isResumePerfection=employPortalForm.getIsResumePerfection();
            int lis=0;
            ArrayList boardlist=employPortalForm.getBoardlist();
            if(unitList!=null&&unitList.size()>0){
                kbean=(LazyDynaBean)unitList.get(0);
                for(int k=0;k<unitList.size();k++){
                    kbean=(LazyDynaBean)unitList.get(0);
                    kll=(ArrayList)kbean.get("list");
                    lis+=lis+kll.size()+1;
                }
                
                if(a0100==null||(a0100!=null&&a0100.trim().length()==0)){
	                hi=lis*30+500;
	                if(lis<=7){
	                    hi=7*30+500;
	                }
                    
                }else{
                    if(username.length()<=4){
                        hi=lis*30+615;;
                        if(lis<=7){
                            hi=7*30+615;
                        }
                    }else{
                        hi=lis*30+615;;
                        if(lis<=7){
                            hi=7*30+615;
                        }
                    }
                }
            }else{
                if(a0100==null||(a0100!=null&&a0100.trim().length()==0))
                    hi=7*30+500;
                else{
                    hi=7*30+615;
                }
            }
			String aurl = (String)request.getServerName();
			String port=request.getServerPort()+"";
			String prl=request.getScheme();
			String url_p=prl+"://"+aurl+":"+port;
            %>
		<%if(a0100==null||a0100.equals("")) {
		    out.println("<script type=\"text/javascript\">");
		    out.println("alert('请先登录!');");
		    out.println("employPortalForm.action=\"/hire/hireNetPortal/search_zp_position.do?b_query=link&operate=init\";");
		    out.println("employPortalForm.submit();");
		    out.println("</script>");
	    }%>
    <html:hidden name="employPortalForm" property="isDefinitionActive"/>
    <a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
	<div class="body">
	<div class="tcenter" id='tc'>
	<div class="center_bg" id='cms_pnl'>  
	<div class="left">
    <div class="login">
	    <div class="dl_1">
	            <div class="we"><b><bean:message key="hire.welcome.you"/>,
			    <%if(userName.length()>6){ %>
                 </b><b>
                 <% } %>
                 ${employPortalForm.userName}</b><bean:message key="hire.welcome.you.hint"/>
                 </div>
			   <ul class="dl_list">
			    <%
				if(isHeadhunter!=null&&isHeadhunter.equals("1")){//进来的用户是猎头身份 
			 	%>
				<li><a href="/hire/hireNetPortal/recommend_resume.do?b_recommendResume=query"><bean:message key="hire.out.resume.recommend"/></a></li><!-- 推荐简历 -->
				<li><a href="/hire/hireNetPortal/search_zp_position.do?b_query=link&returnType=headHunter&hireChannel=headHire"><bean:message key="hire.out.position.employment"/></a></li><!-- 招聘岗位 -->
				<li><a href="/hire/hireNetPortal/search_zp_position.do?br_editPassword=edit"><bean:message key="label.banner.changepwd"/></a></li><!--修改密码 -->
				<%
					}else{
				%>
			   <li><a href='javascript:resumeBrowse("<%=dbName%>","<%=a0100%>")'><bean:message key="hire.browse.resume"/></a></li>
			    <!-- linbz 20160506   屏蔽打印简历功能 -->
			    <!--
			      	<logic:notEqual name="employPortalForm" property="previewTableId" value="#">
			      	<logic:equal name="employPortalForm" property="canPrint" value="1">
					<li><a href='javascript:ysmethod("<bean:write name="employPortalForm" property="previewTableId"/>")'>打印简历</a></li>
			      	</logic:equal>
			      	</logic:notEqual>
			       -->
			   <logic:equal value="true" name="employPortalForm" property="canPrintExamno">
			<li>
				<a href='javascript:printExamNo()'><bean:message key="hire.print.examcard"/></a>
			</li>
			</logic:equal>
			<logic:equal value="1" name="employPortalForm" property="canQueryScore">
			      <li>
			          <a href='javascript:showCard("","")'><bean:message key="hire.query.score"/></a>
			      </li>
			   </logic:equal>
			<li><a href="/hire/hireNetPortal/search_zp_position.do?b_showResumeList=show&setID=0&opt=1"  class="els"><bean:message key="hire.my.resume"/></a></li>
			<li><a href="javascript:void(0);" onclick='hasresume();'><bean:message key="hire.browsed.position"/></a></li>
			<li><a href="/hire/hireNetPortal/search_zp_position.do?b_applyedPosition=query"><bean:message key="hire.apply.position"/>
			</a></li>
			<li><a href="/hire/hireNetPortal/search_zp_position.do?br_editPassword=edit"><bean:message key="label.banner.changepwd"/></a></li>
			<logic:equal value="1" name="employPortalForm" property="isDefinitionActive">
			<logic:equal value="1" name="employPortalForm" property="activeValue">
			<li><a href='javascript:activeResume("<%=dbName%>","<%=a0100%>","${employPortalForm.activeValue}");'>关闭简历</a></li>
			</logic:equal>
			<logic:equal value="2" name="employPortalForm" property="activeValue">
			<li><a href='javascript:activeResume("<%=dbName%>","<%=a0100%>","${employPortalForm.activeValue}");'>激活简历</a></li>
			</logic:equal>
			</logic:equal>
			<%}%>
		   <li><a href="javascript:exit()">退出登录</a></li>
		    </ul>
		</div>
	</div>
	<div class="muen">
	<h2>&nbsp;&nbsp;&nbsp;&nbsp;招聘单位</h2>
		<logic:iterate id="unit" name="employPortalForm" property="unitList" indexId="indexx">
			<logic:equal value="<%=zpUnitCode%>" name="unit" property="codeitemid">
			<div class="firstDiv">
				<table>
					<tr><td align="left" valign="middle">
					<img src="/images/tree_collapse.gif" style="margin-top:-10px" align='absmiddle' border="0" id="<bean:write name="unit" property="id_img"/>" style="cursor:hand" onclick='changeDisplay("<bean:write name="unit" property="codeitemid"/>","<bean:write name="unit" property="count"/>");'/>
					<a href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="unit" property="codeitemid"/>"><font class="firstFont"><bean:write name="unit" property="codeitemdesc"/></font> </a>
					</td></tr>
				</table>
			</div>
			<ul class="col">
				<logic:iterate id="UnitSub" name="unit" property="list" indexId="indexid">
				<logic:equal value="<%=zpUnitCode%>" name="UnitSub" property="codeitemid">
				<li id="<bean:write name="UnitSub" property="id_r"/>"><a   class="one" title='<bean:write name="UnitSub" property="altdesc"/>' href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="UnitSub" property="codeitemid"/>"><bean:write name="UnitSub" property="codeitemdesc"/></a></li>
				</logic:equal>
				<logic:notEqual value="<%=zpUnitCode%>" name="UnitSub" property="codeitemid">
				<li id="<bean:write name="UnitSub" property="id_r"/>"><a title='<bean:write name="UnitSub" property="altdesc"/>' href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="UnitSub" property="codeitemid"/>"><bean:write name="UnitSub" property="codeitemdesc"/></a></li>
				</logic:notEqual>
				</logic:iterate>
			</ul>
			</logic:equal>
			<logic:notEqual value="<%=zpUnitCode%>" name="unit" property="codeitemid">
				<div class="firstDiv">
				<table>
					<tr><td align="left" valign="middle">
					<img src="/images/tree_collapse.gif" style="margin-top:-10px" align='absmiddle' border="0" id="<bean:write name="unit" property="id_img"/>" style="cursor:hand" onclick='changeDisplay("<bean:write name="unit" property="codeitemid"/>","<bean:write name="unit" property="count"/>");'/>
					<a href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="unit" property="codeitemid"/>"><font class="firstFont"><bean:write name="unit" property="codeitemdesc"/></font> </a>
					</td></tr>
				</table>
				</div>
				
				<ul class="col">
				<logic:iterate id="UnitSub" name="unit" property="list" indexId="indexid">
					<logic:equal value="<%=zpUnitCode%>" name="UnitSub" property="codeitemid">
					<li id="<bean:write name="UnitSub" property="id_r"/>">
					<a   class="one" title='<bean:write name="UnitSub" property="altdesc"/>' href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="UnitSub" property="codeitemid"/>"><bean:write name="UnitSub" property="codeitemdesc"/></a></li>
					</logic:equal>
					<logic:notEqual value="<%=zpUnitCode%>" name="UnitSub" property="codeitemid">
					<li id="<bean:write name="UnitSub" property="id_r"/>">
					<a title='<bean:write name="UnitSub" property="altdesc"/>' href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="UnitSub" property="codeitemid"/>"><bean:write name="UnitSub" property="codeitemdesc"/></a></li>
					</logic:notEqual>
				</logic:iterate>
				</ul>
			</logic:notEqual>
		
		</logic:iterate>                          
	</div>
	<div class="promt">
	 ${employPortalForm.promptContent}
	</div>
	</div>
     <!-- 右侧“社会/校园/猎头  招聘” -->
	<% 
	if("headHire".equals(hirechannel)){//暂时显示成这样,等做成图片后换 上来 
		out.println("<div class='right3'  id='rg'>");
	}else{
		out.println("<div class='right4'  id='rg'>");
		out.println("<h2>" + channelName + "</h2>");
	}
	if(conditionFieldList!=null&&conditionFieldList.size()>0){ 
	%>
	<div class="search">
	<h3>职位搜索</h3>
	<div class="xia">
                           
     <%
	   //人民大学职称要求为多选 z03a2职称要求，z0390职称要求隐藏代码
	     String title_Requirements = SystemConfig.getPropertyValue("title_Requirements");
	     String z03a2 = "";
	     String z0390 = "";
	     if(StringUtils.isNotEmpty(title_Requirements)&&title_Requirements.split(":").length==2) {
	     	z03a2 = title_Requirements.split(":")[0];
	     	z0390 = title_Requirements.split(":")[1];
	     }
         String codevalue="";//为了解决层级型代码选项出现后导致排列错格的问题 hidden也会作为一个页面元素占一个位置
         String selecnum="";
         boolean isprint=false;
         for(int i=0;i< conditionFieldList.size() && i<=2;i++)
         {
             out.print("<span>");
             LazyDynaBean abean=(LazyDynaBean)conditionFieldList.get(i);
             String itemid=(String)abean.get("itemid");
             String itemtype=(String)abean.get("itemtype");
             String codesetid=(String)abean.get("codesetid");
             if("Z0385".equalsIgnoreCase(itemid))
					codesetid = "35";
				else if (z03a2.equalsIgnoreCase(itemid))
					codesetid = "DL";
             String isMore=(String)abean.get("isMore");
             String itemdesc=(String)abean.get("itemdesc");
             String value=(String)abean.get("value");
             String viewvalue=(String)abean.get("viewvalue");
             String viewvalue_view = StringUtils.isEmpty((String)abean.get("viewvalue_view")) ? "" : (String)abean.get("viewvalue_view");
             out.print(""+itemdesc+"</span>");
             if(itemtype.equals("A"))
             {
                 if(codesetid.equals("0"))
                 {
                     if(itemid.equalsIgnoreCase(hireMajor) && !(hireMajorCode==null || hireMajorCode.equals("-1"))){
                         out.print("<div class=\"input_bg2\" style='width:95px;padding:0 0 0 0;'>");
                         out.print("<input  class='TEXT' type='text' name='conditionFieldList["+i+"].viewvalue_view' value='"+viewvalue_view+"'  size='10'  style='width:95px;height:20px;line-height:20px;padding-left:5px;'/>");
                         out.print("</div>");
                         out.print("<span  style='width:0px;margin-left:-2px;'>"
                                         +"<input type='hidden' name='conditionFieldList["+i+"].viewvalue_value'/>"
                                         +"<img style='float:right;margin-top:-1px;' class='img-middle'  src='/module/recruitment/image/xiala2.png' plugin='codeselector' isHideTip = 'true' codesetid='"+hireMajorCode+"' ctrltype='0' inputname='conditionFieldList["+i+"].viewvalue_view'  afterfunc='dealRes(\""+i+"\",\"conditionFieldList\")'");
							if("abgaa".equalsIgnoreCase(itemid))
								out.print(" onlyselectcodeset='true' ");

							out.print("/></span>");
                          isprint=true;
                          selecnum=selecnum+i+"@"+value+"`";
                     }
                     else
                         out.println("<div class=\"search_input_bg\"><input name=\"conditionFieldList["+i+"].value\"  class='textbox' type=\"text\" value=\""+value+"\" size='18' /></div>");
                 }
                 else
                 {
                     if("0".equals(isMore)&&false)
                     {
                         ArrayList options=(ArrayList)abean.get("options");          
                         out.print("<div class='input' id='inputf"+i+"'");
                         out.print(" style='width:122px'>");
                         out.print("<img  src='../../images/hire/input_l.gif' width='100%' height='100%' style='position:absolute;z-index:-999'/>");
                         out.print(" <div class='floor' style='outline:none' tabindex=\"0\" id='floorf"+i+"' onblur=\" hide3('"+i+"');\"> ");
                         String selected="";
                         String selectedvalue="";
                         out.println("<a  onclick=\"javascript:change1("+i+",'"+"全部"+"','"+""+"','conditionFieldList["+i+"].value');\" style='FONT-SIZE: 12px;font-family: 微软黑体;color:black;cursor:hand'>");
                         out.print("  全部"+"</a><br>");
                         for(int n=0;n<options.size();n++)
                         {
                             LazyDynaBean a_bean=(LazyDynaBean)options.get(n);
                             String avalue=(String)a_bean.get("value");
                             String aname=(String)a_bean.get("name");
                             out.println("<a  onclick=\"javascript:change1("+i+",'"+aname+"','"+avalue+"','conditionFieldList["+i+"].value');\" style='FONT-SIZE: 12px;font-family: 微软黑体;color:black;cursor:hand'>");
                             if(avalue.equals(value)){
                                 selected=aname;
                                 selectedvalue=avalue;
                                 if(selected.length()>=7){
                                     selected=selected.substring(0,7);
                                 }
                             }
                             out.print(" "+aname+"</a>");
                             if(n!=options.size()-1){
                                 out.print("<br>");
                             }
                         }
                         if(selected.trim().length()==0){
                             selected="全部";
                         }
                         out.print("<input type='hidden' name='conditionFieldList["+i+"].value' value='"+selectedvalue+"'/>");
                         out.print("</div><span id='spank"+i+"' class='img'>");
                         out.print("<a href='javascript:void(0);' onclick='showlist1("+i+");'><img src='/images/hire/xia.gif'/>&nbsp;&nbsp;</a>");
                         out.print(" </span><span style='padding-right:0px;overflow:overflow;margin-right:-2px;margin-left:1.9px' FONT-SIZE: 12px;font-family: 微软黑体;color:black; id='spanf"+i+"'>"); 
                         out.print(selected+" </span></div>");   
                     }
                     else
                     {
                     	out.print("<div class=\"input_bg2\" style='width:95px;padding:0 0 0 0;'>");
                     	out.print("<input  class='TEXT' type='text' onkeydown='clean_value(event,this)' autocomplete='off' name='conditionFieldList["+i+"].viewvalue_view' value='"+viewvalue_view+"'  size='10'  style='width:95px;height:20px;line-height:20px;padding-left:5px;'/>");
                     	out.print("</div>");
                     	out.print("<span  style='width:0px;margin-left:-2px;'>");
                   		out.print("<img style='float:right;margin-top:-1px;' class='img-middle' src='/module/recruitment/image/xiala2.png' plugin='codeselector' isHideTip = 'true' codesetid='"+codesetid+"' inputname='conditionFieldList["+i+"].viewvalue_view' ");
						if("un".equalsIgnoreCase(codesetid)||"um".equalsIgnoreCase(codesetid)||"@K".equalsIgnoreCase(codesetid))
							out.print(" ctrltype='0' ");
						//人大要求快速查询只显示有职位的单位
						if("un".equalsIgnoreCase(codesetid))
							out.print(" codesource='GetZPOrganization' ");
						if("z0385".equalsIgnoreCase(itemid)||z03a2.equalsIgnoreCase(itemid))
							out.print(" multiple='true' ");
						
						if("abgaa".equalsIgnoreCase(itemid))
							out.print(" onlyselectcodeset='true' ");
						out.print(" afterfunc='dealRes(\""+i+"\",\"conditionFieldList\")'/>");
		
						out.print("<input type='hidden' name='conditionFieldList["+i+"].viewvalue_value'/>");
						out.print("</span>");
						isprint=true;
						selecnum=selecnum+i+"@"+value+"`";
                     }
                 
                 }
             
             }
             else if(itemtype.equals("D"))
             {
                 out.println("<div class=\"input_bg1\" style='width:110px;'><input  name='conditionFieldList["+i+"].value' class='TEXT' type='text' style='width:100px;FONT-SIZE: 12px;font-family: 微软黑体;color:black;'   size='15' value='"+value+"'  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)'/></div>");
             
             }
             else if(itemtype.equals("N"))
             {
                 out.println("<div class=\"input_bg1\" style='width:110px;'><input name=\"conditionFieldList["+i+"].value\" class='TEXT' type=\"text\" style='width:100px;FONT-SIZE: 12px;font-family: 微软黑体;color:black;    value=\""+value+"\"   size='15'   /></div>");
             }
         
         }%>                      
         <a onclick="javascript:query(1);" style="margin-left:10px" id="img2"><img src="/images/hire/sarch.gif" /></a> 
          <%
             if(isprint){
                 if(null!=selecnum&&selecnum.indexOf("`")!=-1&&selecnum.indexOf("@")!=-1){
                     String []temp=selecnum.split("`");
                     for(int t=0;t<temp.length;t++){
                         String tt=temp[t];
                         if(tt.trim().length()==0)
                             continue;
                         String []gg=tt.split("@");
                         if(gg.length==1){
                         out.println("<input type='text' style='display:none;' name='conditionFieldList["+gg[0]+"].value' value=''  />&nbsp;"); 
                         }else{
                         out.println("<input type='text' style='display:none;' name='conditionFieldList["+gg[0]+"].value' value='"+gg[1]+"'  />&nbsp;"); 
                         }
                     
                     }
                 }
             
             }
          %>            
		</div>
	</div>
          <%} %>
    <div class="jj">
		<%
		if(isHeadhunter!=null&&isHeadhunter.equals("1"))//进来的用户是猎头身份 
			out.println("<h2><span>录入简历</span></h2>");
		else
			out.println("<h2><span>我的简历</span></h2>");
        out.println("<table class=zp_pos_table>");
        String currentName="";
        String fieldSetId="";
        String answerFlag="0";
        boolean openanswer =false;
        ArrayList setList = (ArrayList)fieldSetList.clone();
		ArrayList fieldSetMustList=employPortalForm.getFieldSetMustList();//获得具有必填项的子集
		LazyDynaBean lastAbean = (LazyDynaBean)setList.get(setList.size()-1);
		if(isAttach.equals("1")&&!"-1".equals(lastAbean.get("fieldSetId"))){ //这里才是附件所在 
			LazyDynaBean attachAbean = new LazyDynaBean();
			attachAbean.set("fieldSetDesc", ResourceFactory.getProperty("hire.resume.attach"));
			attachAbean.set("fieldSetId", "-1");
			setList.add(attachAbean);
        }
        int setSum = setList.size();
        int rows = setSum/5 + (setSum%5>0?1:0);
		if(-1 == index){
			index = setList.size()-1;
        }
        for(int i=0; i<rows; i++){
        	out.print("<tr>");
			for(int n=0; n<5; n++){
				out.print("<td>");
				if((i*5+n+1)<=setList.size()){
					LazyDynaBean abean = (LazyDynaBean)setList.get(i*5+n);
	                String a_fieldSetId = (String)abean.get("fieldSetId");
	                if("-1".equals(a_fieldSetId))
	                	out.print("<a href='/hire/hireNetPortal/search_zp_position.do?b_showResumeList=show&opt=2&setID=-1'>");
                	else
						out.print("<a href='/hire/hireNetPortal/search_zp_position.do?b_showResumeList=show&opt=1&setID="+PubFunc.encrypt(a_fieldSetId)+"'>");
	                
					if(index == (i*5+n)){
	                    out.print("<font color='#129AC7'><div class='textUnderline'>");
	                    currentName=(String)abean.get("fieldSetDesc");
	                    fieldSetId=a_fieldSetId;
	                    if(answerSet!=null&&answerSet.equalsIgnoreCase(a_fieldSetId)){//判断当前子集是否是开放问答子集
	                        openanswer =true;
	                        answerFlag="1";
	                    }
	                }
					out.print((String)abean.get("fieldSetDesc"));
					if(index == (i*5+n))
	                    out.print("</div></font>");
                    if(fieldSetMustList!=null&&fieldSetMustList.contains(a_fieldSetId)){
                        out.print("&nbsp;&nbsp;<FONT color=red>*</FONT>");
                    }
                    out.print("</a>");
				}
				out.print("</td>");
			}
			out.print("</tr>");
		}
        out.print("</table>");
        %>
    </div><!-- 前台要展现的子集输出完毕 -->
                                     
     <%
     boolean canSaveInfor = true;
     if("headHire".equals(a0100)&&!fieldSetId.equalsIgnoreCase("A01")){//猎头招聘,新增简历,当前维护的子集不是基本信息,那么不能保存 
     	canSaveInfor=false;
     }
     %>                                     
     <% if(opt.equals("1")){ %>
        <div class="jj zw">
         <h3>
             <span><%=currentName%>
              <%--  <%if(!onlyFieldSet.contains(fieldSetId)&&!openanswer){ %>
                  <bean:message key="hire.fill.timeseq"/><!-- 请按时间的先后顺序填写信息 --暂时先去掉系统没办法知道是不是要按时间顺序填 -->
                <% } %>  --%>
            </span>
          </h3>
          <div class="nr" >                          
             <%if(openanswer){ //如果是开发问答子集%>                                    
                  <TABLE cellSpacing=0 cellPadding=0 width=100% border=0 class="table table3">
                  <TBODY>
                      <%
                        ArrayList  resumeFieldList=employPortalForm.getResumeFieldList();
                        for(int i=0;i<resumeFieldList.size();i++)
                        {
                          LazyDynaBean abean=(LazyDynaBean)resumeFieldList.get(i);
                          String itemid=(String)abean.get("itemid");
                          String itemtype=(String)abean.get("itemtype");
                          String codesetid=(String)abean.get("codesetid");
                          String isMore=(String)abean.get("isMore");
                          String itemdesc=(String)abean.get("itemdesc");
                          String value=(String)abean.get("value");
                          String itemlength=(String)abean.get("itemlength");
                          String decimalwidth=(String)abean.get("decimalwidth");
                          if(blackFieldItem!=null&&blackFieldItem.equalsIgnoreCase(itemid))
                          {
                             blackIndex=i;
                          }
                          if(decimalwidth==null||decimalwidth.equals(""))
                             decimalwidth="0";
                           int deci=Integer.parseInt(decimalwidth);
                           int totallength=Integer.parseInt(itemlength)+(deci>0?(1+deci):deci);
                          String viewvalue=(String)abean.get("viewvalue");
                          String must=(String)abean.get("must");   //是否为必填项 1：是 0：否
                          
                          String isseqn=(String)abean.get("isseqn");
                          String itemmemo=(String)abean.get("itemmemo");
                          if(i!=0)
                          itemmemo="<br>"+itemmemo;
                          itemmemo = itemmemo.replace("\r\n","<br>");
                              
                      %>
                                    
                     <TR>
                         <TD class=tdTitle  style="TEXT-ALIGN: left;" >
                             <font class="class_text" id="<%=itemid+"memo"%>">
                                 <%=itemmemo%>
                             </font>
                         </TD>
                     </TR>
					<TR>
					    <TD>                                                                                   
					 <%
					    if(itemtype.equals("A"))
					    {
					        if(codesetid.equals("0"))
					        {
					            out.println("<div class='input_bg1'>");
					            out.println("<input type=\"text\"  class=textbox ");
					            if(itemdesc.equalsIgnoreCase("身份证号")||itemdesc.equalsIgnoreCase("身份证号码"))
					            {
					            	out.println(" maxlength='"+itemlength+"' ");
					                idIndex=i;
					                idItemId=itemid;
					            }
					            if(blackFieldItem.equalsIgnoreCase(itemid)||onlyName.equalsIgnoreCase(itemid))
					            {
					              out.print(" readOnly ");
					            }
					            else if(itemid.equalsIgnoreCase(emailColumn))
					            {
					               out.print(" readOnly ");
					            }
					            else if(onlyFieldSet.contains(fieldSetId)&&writeable.equals("1"))
					            {
					                if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null&isseqn.equals("0"))
					                    out.print("");
					                else
					                    out.print(" readOnly ");
					             }
					            else
					            {
					               if(isseqn.equals("0"))
					                   out.print("");
					               else
					                   out.print(" readOnly ");
					            }
					            out.print(" name=\"resumeFieldList["+i+"].value\"  value=\""+value+"\"  />&nbsp;");
					        }
					        else
					        {
					            if(isMore.equals("0"))
					            {
					                out.print("<div>");
					                out.print("<div class='input' id='input"+i+"'>");
					                out.print("<img  src='../../images/hire/input_l.gif' width='100%' height='100%' style='position:absolute;z-index:-999;left:0px'/>");
					                out.print(" <div class='floor' tabindex=\"0\" id='floor"+i+"' onblur=\" hide('"+i+"');\"> ");
					                
					                String selected="";
					                String selectedvalue="";
					                if(abean.get("options")!=null){
					                    ArrayList options=(ArrayList)abean.get("options");
					                    for(int n=0;n<options.size();n++)
					                    {
					                        LazyDynaBean a_bean=(LazyDynaBean)options.get(n);
					                        String avalue=(String)a_bean.get("value");
					                        String aname=(String)a_bean.get("name");
					                        out.println("<a  onclick=\"javascript:change("+i+",'"+aname+"','"+avalue+"','resumeFieldList["+i+"].value');\" style='FONT-SIZE: 12px;font-family: 微软黑体;color:black;cursor:hand'>");
					                        //out.println("<option value='"+avalue+"' ");
					                        if(avalue.equals(value)){
					                            selected=aname;
					                            selectedvalue=avalue;
					                        }
					                        if(selected.length()>=9){
					                            selected=selected.substring(0,10);
					                        }
					                        out.print(" "+aname+"</a>");
					                        if(n!=options.size()-1){
					                        out.print("<br>");
					                        }
					                    }
					                }
					                out.print("<input type='hidden' name='resumeFieldList["+i+"].value' value='"+selectedvalue+"'/>");
					                out.print("</div><span  class='img'> ");                                                                      
					                out.print("<a href='javascript:void(0);' onclick='showlist("+i+");'><img src='/images/hire/xia.gif'/>&nbsp;&nbsp;</a>");
					                out.print(" </span><span style='overflow:overflow;' id='span"+i+"'>");  
					                out.print(selected+" </span></div>");   
					            }
					            else
					            {
					                out.print("<table  cellSpacing=0 cellPadding=0 border=0  ><tr><td><div class='input_bg1' style='width:auto;'>");
					                
					                if(onlyFieldSet.contains(fieldSetId)&&writeable.equals("1"))
					                {
					                    if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null || itemid.equalsIgnoreCase(axName))
					                    {
					                      if(!isseqn.equals("1")){
					                    	  out.print("<input type='text'  name='resumeFieldList["+i+"].viewvalue_view' value='"+viewvalue+"'   class='textbox'  style='FONT-SIZE: 12px;font-family: 微软黑体;color:black;'/>"
					                                  		+"<input type='hidden' name='resumeFieldList["+i+"].viewvalue_value'/>"
					                                  		+"<img class='img-middle' src='/images/hire/xia.gif' plugin='codeselector' isHideTip = 'true' codesetid='"+codesetid+"' inputname='resumeFieldList["+i+"].viewvalue_view' afterfunc='dealRes(\""+i+"\",\"resumeFieldList\")'");
				                    	  //所有代码型指标只允许选择最底层
											/* 	if("abgaa".equalsIgnoreCase(itemid)) */
													out.print(" onlyselectcodeset='true' ");
	
												  out.print("/></td>");
					                      }
					                     }
					                }
					                else
					                {
					                	if(!isseqn.equals("1")){
					                    	out.print("<input type='text'  name='resumeFieldList["+i+"].viewvalue_view' value='"+viewvalue+"'   class='textbox'  style='FONT-SIZE: 12px;font-family: 微软黑体;color:black;'/>"
					                                		+"<input type='hidden' name='resumeFieldList["+i+"].viewvalue_value'/>"
					                                		+"<img class='img-middle' src='/images/hire/xia.gif' plugin='codeselector' isHideTip = 'true' codesetid='"+codesetid+"'   inputname='resumeFieldList["+i+"].viewvalue_view' afterfunc='dealRes(\""+i+"\",\"resumeFieldList\")'");
					                    	//所有代码型指标只允许选择最底层
											/* 	if("abgaa".equalsIgnoreCase(itemid)) */
												out.print(" onlyselectcodeset='true' ");

											out.print("/></td>");
										}
					                }
					                if(onlyFieldSet.contains(fieldSetId)&&writeable.equals("1"))
					                {
					                	
					                    if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null)
					                       out.print("</a>");
					                }
					                else
					                {
					                    out.print("</a>");
					                }
					                out.print("&nbsp;</td>");       
					                out.print("<td ><input type='hidden' value='"+value+"'  name='resumeFieldList["+i+"].value' />");
					                if(must.equals("1"))//||itemdesc.equalsIgnoreCase("身份证号")||itemdesc.equalsIgnoreCase("身份证号码")
					                    out.println("<FONT color=red>*</FONT>");
					                out.print(" </td></tr></table>");
					               
					             }
					        
					        }
					    
					    }
					    else if(itemtype.equals("D"))
					    {
					        out.println("<div class='input_bg1'>");
					        //时间 zhangcq  2016/7/19 时间显示格式为yyyy-mm-dd 
					     out.print("<input type='text'  name='resumeFieldList["+i+"].value'  class=textbox  value='"+value+"' dropDown='dropDownDate' itemlength="+itemlength);
					        if(onlyFieldSet.contains(fieldSetId)&&writeable.equals("1"))
					        {
					         if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null&isseqn.equals("0"))
					            out.print(" autocomplete='off'"); //onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)' ");
					         else
					            out.print(" readOnly ");
					        }
					        else
					        {
					           if(isseqn.equals("0"))
					              out.print(" autocomplete='off'");
					           else
					              out.print(" readOnly ");
					        }
					        
					        if(itemid.equalsIgnoreCase(birthdayName)){
					        	out.print(" readOnly ");
					       		out.println(" /><img src= '/images/hire/calendar.gif'  align='absmiddle'   id='calendar"+i+"' inputname='resumeFieldList["+i+"].value' format='Y-m-d' />");
					        }else
					        	out.println(" /><img src= '/images/hire/calendar.gif'  align='absmiddle'  plugin='datetimeselector' id='calendar"+i+"' inputname='resumeFieldList["+i+"].value' format='Y-m-d' afterfunc='me.selector.close();'/>&nbsp;");                                                            
					    }
					    else if(itemtype.equals("N"))
					    {
					        out.println("<div class='input_bg1'>");
					        out.print("<input type=\"text\" name=\"resumeFieldList["+i+"].value\"   value='"+value+"'  class=textbox  maxlength='"+totallength+"' ");
					        if(onlyFieldSet.contains(fieldSetId)&&writeable.equals("1"))
					            {
					                if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null&isseqn.equals("0"))
					                    out.print("");
					                else
					                    out.print(" readOnly ");
					             }
					            else
					            {
					               if(isseqn.equals("0"))
					                   out.print("");
					               else
					                   out.print(" readOnly ");
					            }
					        
					        if(itemid.equalsIgnoreCase(ageName)){
                                out.print(" readOnly ");
                            }
					
					        out.println("/>&nbsp;");
					    }
					    else if(itemtype.equals("M"))
					    {
					        out.print("<textarea name=\"resumeFieldList["+i+"].value\" rows='10'   wrap='OFF' class='resume_textarea'" );
					        if(onlyFieldSet.contains(fieldSetId)&&writeable.equals("1"))
					            {
					                if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null&isseqn.equals("0"))
					                    out.print("");
					                else
					                    out.print(" readOnly ");
					             }
					            else
					            {
					               if(isseqn.equals("0"))
					                   out.print("");
					               else
					                   out.print(" readOnly ");
					            }
					        out.println(">"+value+"</textarea>&nbsp;");
					    }
					    
						if(itemtype.equals("M")){
							if(must.equals("1"))
								out.print("<FONT color=red >* </FONT></div>");
						}else{
							out.print("</div>");
							if(must.equals("1")){
								if(isMore.equals("0"))
									out.print("<FONT color=red style='margin-left:10px'>* </FONT></div>");
								else
									out.print("</div>");
							}
						} %>
					  </TD>
					</TR>
                                    
                    <%
                    }
                    if(fieldSetId.equalsIgnoreCase("A01"))
                    {
                    %>
                                    
                    <TR>
                      <TD class=tdTitle valign='bottom'
                        width="30%"><font class="fieldDescriptionColor"><bean:message key="hire.cloumn.photo"/>：</font></TD>
                      <TD class='tdValue'>
                           <font color='red' style='FONT-SIZE: 12px;font-family: 微软黑体;color:black;'>
                            <% if(isUpPhoto.equals("1")){ %>
                           <bean:message key="hire.mustupload.photo"/>
                           	<%} else {%>
                           <bean:message key="hire.upload.photo.validate"/>
                           <%} %>
                           </font><br>
                           <input name="file" type="file" size="35" class='resume_file'>&nbsp;
                           <% if(isUpPhoto.equals("1")){ %>
                           <FONT color=red>* </FONT>
                           <% } %>
                      </TD>
                    </TR>
                     <% } %>       
				</TBODY>
				</TABLE>
                <br>
              <TABLE id=Table2 cellSpacing=0 cellPadding=0 width="100%" border=0>
              <TBODY>
                <TR> <TD style="TEXT-ALIGN:center">
                <%
                if(!onlyFieldSet.contains(fieldSetId)){ %>
                 
                  <a onclick='javascript:sub(4,"<%=answerFlag%>")' >
                   <IMG  src="/images/hire/up.gif" border=0 >
                  </a>
               
                <% } 
                  if((index+1)!=fieldSetList.size())//不是最后一个子集
                  {%>
                 <logic:equal value="0" name="employPortalForm" property="writeable">
                   &nbsp;&nbsp;&nbsp;&nbsp;
                    <a onclick='javascript:sub(1,"<%=answerFlag%>")' >
                  <IMG  src="/images/hire/save.gif" border=0 >
                  </a>
                  </logic:equal>
                 &nbsp;&nbsp;&nbsp;&nbsp; <a onclick='javascript:sub(3,"<%=answerFlag%>")' >
                  <IMG  src="/images/hire/down.gif" border=0 >
                  </a>
                 <%
                 }
                 if(index==(fieldSetList.size()-1))//最后一个子集
                 {
                    if(isAttach.equals("1"))
                    {%>
                     <logic:equal value="0" name="employPortalForm" property="writeable">
                  &nbsp;&nbsp;&nbsp;&nbsp;<a onclick='javascript:sub(1,"<%=answerFlag%>")' >
                  <IMG  src="/images/hire/save.gif" border=0 >
                  </a>
                  </logic:equal>
                  &nbsp;&nbsp;&nbsp;&nbsp;
                  <a onclick='javascript:sub(3,"<%=answerFlag%>")' >
                  <IMG  src="/images/hire/down.gif" border=0 >
                  </a>
                 <%}else{%>
                  <logic:equal value="0" name="employPortalForm" property="writeable">
                  &nbsp;&nbsp;&nbsp;&nbsp;
                    <a onclick='javascript:sub(1,"<%=answerFlag%>")' >
                  <IMG  src="/images/hire/save.gif" border=0 >
                  </a>&nbsp;&nbsp;&nbsp;&nbsp;
                   <%-- <a onclick='javascript:sub(0,"<%=answerFlag%>")' >
                  <IMG  src="/images/hire/saveandend2.gif" border=0 >
                  </a> --%>
                  <a onclick='javascript:sub("finished","<%=answerFlag%>")' >
                  <IMG src="/images/hire/finish.gif" border=0 >
                  </a>
                   </logic:equal>
                 <%}} %>
                  </TD>
                  </TR>
                  </TBODY></TABLE>
                      <%}else{ //如果不是开发问答子集 openanser%>
                          <%if(!onlyFieldSet.contains(fieldSetId)){ %>
						<!--如果不是基本信息,基本信息的话没有表头和以tr展现的数据  先生成表头和原有的数据 -->	                                       
                         <TABLE  id=rptb cellSpacing=0 cellPadding=1 width="100%" align=center class="table table2" border=0 style='TABLE-LAYOUT: fixed'>
                         <TBODY>
                             <TR>
                                 <!-- 
                                 <TD class=rptHead 
                                   background=/images/r_titbg01.gif>编号 </TD>
                                  -->
                                  <% int xx=0; %>
                                  <logic:iterate id="element" name="employPortalForm" property="showFieldList"  offset="0"> 
                                   <Td align="left" class="changecolor" height="26"><b><bean:write name="element" property="itemdesc" /></b></Td>
                                  <% xx++; %>
                                  </logic:iterate>
                                   <logic:equal value="0" name="employPortalForm" property="writeable">
                                   <% if(xx>0){ %>
                                  <Td  align="left" class="changecolor"  height="26"><b><bean:message key="system.infor.oper"/></b></Td><!-- 操作 -->
                                  <%} %>
                                   </logic:equal>
                               </TR>
                                <%
                                for(int i=0;i<showFieldDataList.size();i++)//如果有数据行的话,输出当前人员的相关数据 
                                {
                                   LazyDynaBean abean=(LazyDynaBean)showFieldDataList.get(i);
                                   String i9999=(String)abean.get("i9999");
                                   out.println("<tr>");
                                   String styletdClass="hj_zhaopin_list_tab_titletwo";
                                   for(int n=0;n<showFieldList.size();n++)
                                   {
                                       LazyDynaBean a_bean=(LazyDynaBean)showFieldList.get(n);
                                       String itemid=(String)a_bean.get("itemid");
                                       String value=(String)abean.get(itemid);
                                       if(value.equals(""))
                                           value="&nbsp;";
                                       if(i%2!=0){
                                           out.print("<TD align='left' style='padding-left:10px;word-WRAP: break-word;' bgcolor='#E8EBF1' height='36'>"+value+"</TD>");
                                       }else{
                                           out.print("<TD align='left' style='padding-left:10px;word-WRAP: break-word;' height='36' >"+value+"</TD>");
                                       }
                                       
                                   }
									if(writeable.equals("0"))
									{
										if(i%2!=0){
											out.println("<TD align='left' bgcolor='#E8EBF1' height='36'><A href='/hire/hireNetPortal/search_zp_position.do?b_showResumeList=show&opt=1&setID="+PubFunc.encrypt(fieldSetId)+"&userid="+a0100+"&i9999="+i9999+"'><img src='/images/hire/editor.gif' title='修改' border='0' style='cursor:hand'/></A>");
											out.print("<A href='javascript:deleteRecord(\""+i9999+"\")'><img src='/images/hire/del.gif' title='删除' border='0' style='cursor:hand'/></A></TD>");
										}else{
											out.println("<TD align='left' height='36'><A href='/hire/hireNetPortal/search_zp_position.do?b_showResumeList=show&opt=1&setID="+PubFunc.encrypt(fieldSetId)+"&userid="+a0100+"&i9999="+i9999+"'><img src='/images/hire/editor.gif' title='修改' border='0' style='cursor:hand'/></A>");
											out.print("<A href='javascript:deleteRecord(\""+i9999+"\")'><img src='/images/hire/del.gif' title='删除' border='0' style='cursor:hand'/></A></TD>");
										}
									    
									}
									out.print("</TR>");
									}
                                      %>
                                   </TBODY>
                                </TABLE>
                            <br>
                          <% } %>
                               
                               	 <!--在不是开放问答子集的情况下,现在开始输出要录入的内容  -->
                                    <table width="0" border="0" cellspacing="0" cellpadding="0" class="table table3">
                                   
                                        <%
                                        ArrayList  resumeFieldList=employPortalForm.getResumeFieldList();
                                        for(int i=0;i<resumeFieldList.size();i++)
                                        {
                                          LazyDynaBean abean=(LazyDynaBean)resumeFieldList.get(i);
                                          String itemid=(String)abean.get("itemid");
                                          String itemtype=(String)abean.get("itemtype");
                                          String codesetid=(String)abean.get("codesetid");
                                          String isMore=(String)abean.get("isMore");
                                          String itemdesc=(String)abean.get("itemdesc");//ie浏览器
                                          String itemdesc2=(String)abean.get("itemdesc");//其它浏览器
                                          String information = "";
                                          String value=(String)abean.get("value");
                                          String itemlength=(String)abean.get("itemlength");
                                          String decimalwidth=(String)abean.get("decimalwidth");
                                          if(blackFieldItem!=null&&blackFieldItem.equalsIgnoreCase(itemid))
                                          {
                                             blackIndex=i;
                                          }
                                          if(decimalwidth==null||decimalwidth.equals(""))
                                             decimalwidth="0";
                                           int deci=Integer.parseInt(decimalwidth);
                                           int totallength=Integer.parseInt(itemlength)+(deci>0?(1+deci):deci);
                                          String viewvalue=(String)abean.get("viewvalue");
                                          String must=(String)abean.get("must");   //是否为必填项 1：是 0：否
                                          String isseqn=(String)abean.get("isseqn");
                                          String itemmemo=(String)abean.get("itemmemo");                                                  
                                          String Agent = request.getHeader("User-Agent");
                                          if(Agent.indexOf(" MSIE 8.0")>0)
                                              information=itemdesc;
                                          else
                                              information=itemdesc2;
                                        %>
                                               
                                 <tr>
                                    <td width="200" height="34" align="right" class='class_text'>
                                       <%=information%>：
                                   </td>
                                   <TD <% if(!isExp.equals("1")&&itemtype.equals("M")){ %>width="200"<%} %>><!-- 如果是备注型并且不显示指标描述 宽度设置城200 -->
                                   <% if(isExp.equals("1")){ //如果显示指标描述 %>
                                   <div>
                                   <table>
                                       <tr>
                                        <td class='nowrap' style="width:210px">
                                            <%}
                                                if(itemtype.equals("A"))
                                                {
                                                    if(codesetid.equals("0"))
                                                    {
                                                        out.println("<div class='input_bg1'>");
                                                        out.println("<input type=\"text\"   class='class_text' style='padding-left:5px;'");
                                                        if(itemdesc.equalsIgnoreCase("身份证号")||itemdesc.equalsIgnoreCase("身份证号码"))
                                                        {
                                                        	out.println(" maxlength='"+itemlength+"' ");
                                                            idIndex=i;
                                                            idItemId=itemid;
                                                        }
                                                        if((onlyName.equalsIgnoreCase(itemid)||blackFieldItem.equalsIgnoreCase(itemid))&&!(isHeadhunter!=null&&isHeadhunter.equals("1")))
                                                        {
                                                          out.print(" readOnly ");
                                                        }
                                                        else if(itemid.equalsIgnoreCase(emailColumn)&&!(isHeadhunter!=null&&isHeadhunter.equals("1")))//如果是邮箱字段,那么不可以被修改。这里是否要处理一下,猎头可以修改他推荐的人的邮箱
                                                        {
                                                           out.print(" readOnly ");
                                                        }
                                                        else if(onlyFieldSet.contains(fieldSetId)&&writeable.equals("1"))
                                                        {
                                                            if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null&isseqn.equals("0"))
                                                                out.print("");
                                                            else
                                                                out.print(" readOnly ");
                                                         }
                                                        else
                                                        {
                                                            if(isseqn.equals("0"))
                                                                out.print("");
                                                            else
                                                                out.print(" readOnly ");
                                    
                                                        }
                                                        if(writeable.equals("1"))
                                                        	out.print(" readOnly ");
                                                        out.print(" name=\"resumeFieldList["+i+"].value\"   value=\""+value+"\"  />&nbsp;");
                                                        out.print("</div>");
                                                    }
                                                    else
                                                    {//如果是代码类 
                                                        //linbz   23466  一级代码支持模糊查询，先写false不用该方法   &&!writeable.equals("1")，并增加高度微调下拉区域显示位置                                                                
                                                        if(isMore.equals("0")&&false)
                                                        {
                                                            out.print("<div class='input' id='input"+i+"'>");
                                                            //修改页面下拉框图片后移
                                                          //  out.print("<img  src='../../images/hire/input_l.gif' width='100%' height='100%' style='position:absolute;z-index:-999'/>");
                                                            out.print(" <div class='floor' style='display:none;outline:none;' tabindex=\"0\" id='floor"+i+"' onblur=\" hide('"+i+"');\"> ");
                                                            String selected="";
                                                            String selectedvalue="";
                                                            if(abean.get("options")!=null){
                                                                ArrayList options=(ArrayList)abean.get("options");
                                                                for(int n=0;n<options.size();n++)
                                                                {
                                                                    LazyDynaBean a_bean=(LazyDynaBean)options.get(n);
                                                                    String avalue=(String)a_bean.get("value");
                                                                    String aname=(String)a_bean.get("name");
                                                                    if(aname.equals("请选择")){
                                                                    	 out.println("<a onclick=\"javascript:change("+i+",'','"+avalue+"','resumeFieldList["+i+"].value');\" style='FONT-SIZE: 12px;font-family: 微软黑体;color:black;cursor:pointer'>");     
                                                                    }else{
                                                                    	 out.println("<a onclick=\"javascript:change("+i+",'"+aname+"','"+avalue+"','resumeFieldList["+i+"].value');\" style='FONT-SIZE: 12px;font-family: 微软黑体;color:black;cursor:pointer'>");     
                                                                    }
                                                                    if(avalue.equals(value)){
                                                                        selected=aname;
                                                                        selectedvalue=avalue;
                                                                    }
                                                                    if(selected.length()>9){
                                                                        selected=selected.substring(0,10);
                                                                    }
                                                                    out.print(" "+aname+"</a>");
                                                                    if(n!=options.size()-1){
                                                                    out.print("<br>");
                                                                    }
                                                                }
                                                            }
                                                            out.print("<input type='hidden' name='resumeFieldList["+i+"].value' value='"+selectedvalue+"'/>");
                                                            //修改页面下拉框图片后移
                                                            out.print("</div><span class='img' style='margin-right:-10px;'>");
                                                            out.print("<a href='javascript:void(0);' onclick='showlist("+i+");'><img src='/images/hire/xia.gif' style='margin-left'/>&nbsp;&nbsp;</a>");                                                                                                            
                                                            out.print(" </span><span id='span"+i+"' style='FONT-SIZE: 12px;font-family: 微软黑体;color:black; margin-right:0px;padding-right:0px;overflow:overflow;'>");    
                                                            if(selected.equals("请选择")){
                                                            	out.print(" </span>");
                                                            }else{
                                                            	out.print(selected+" </span>");
                                                            }
                                                            
                                                            out.print("</div>");                                                                        
                                                        }
                                                        else
                                                        {
                                                            out.println("<div class='input_bg1' style='vertical-align: middle'>");
                                                           
                                                            if(onlyFieldSet.contains(fieldSetId)&&writeable.equals("1"))
                                                            {
                                                                if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null || itemid.equalsIgnoreCase(axName))
                                                                {
                                                                	if( itemid.equalsIgnoreCase(axName)){
                     					                			   out.print("<input type='text' name='resumeFieldList["+i+"].viewvalue' value='"+viewvalue+"' readonly unselectable='on' style='padding-left:5px;height:23px;' />"); 
                     					                		   }else if(!isseqn.equals("1")){
                                                                   	  out.print("<input type='text'  name='resumeFieldList["+i+"].viewvalue_view' value='"+viewvalue+"' style='padding-left:5px;height:23px;line-height:23px;vertical-align: middle'/>"
                                                                               		+"<input type='hidden' name='resumeFieldList["+i+"].viewvalue_value'/>"
                                                                               		+"<img  class='img-middle' src='/images/hire/xia.gif' plugin='codeselector' codesetid='"+codesetid+"'   inputname='resumeFieldList["+i+"].viewvalue_view' afterfunc='dealRes(\""+i+"\",\"resumeFieldList\")'/>"
                                                                                 );
                                                                	 }
                                                                     //out.print("<a href='javascript:openInputCodeDialog2(\""+codesetid+"\",\"resumeFieldList["+i+"].viewvalue\",\"\",\"\",\"0\");' >   ");
                                                                 }
                                                                  else
                                                                	  out.print("<input type='text' name='resumeFieldList["+i+"].viewvalue' value='"+viewvalue+"' readonly />");
                                                            }
                                                            else
                                                            {
                                                                if(!isseqn.equals("1") && !writeable.equals("1")){
                                                                	 out.print("<input type='text' ");
                                                               	 	 if(idType.equalsIgnoreCase(itemid))
	                          								          {
	                          								        	 out.print("  readOnly name='resumeFieldList["+i+"].viewvalue_view' value='"+viewvalue+"' style='padding-left:5px;height:23px;line-height:23px;vertical-align: middle'/>"
                                                                        		+"<input type='hidden' name='resumeFieldList["+i+"].viewvalue_value'/>"
                                                                        		+"<img  class='img-middle' src='/images/hire/xia.gif'  ");
	                          								          }else{
	                          								        	  out.print("  name='resumeFieldList["+i+"].viewvalue_view' value='"+viewvalue+"' style='padding-left:5px;height:23px;line-height:23px;vertical-align: middle'/>"
                                                                        		+"<input type='hidden' name='resumeFieldList["+i+"].viewvalue_value'/>"
                                                                        		+"<img  class='img-middle' src='/images/hire/xia.gif' plugin='codeselector' ");
	                          								        	//所有代码型指标只允许选择最底层
	                                                              		//应聘身份限制只能选择叶子节点
	                                                                  	/* if("35".equals(codesetid) || "abgaa".equalsIgnoreCase(itemid)) */
	                                                                  		out.print(" onlySelectCodeset='true' "); 
	                                                                 		out.print(" codesetid='"+codesetid+"'   inputname='resumeFieldList["+i+"].viewvalue_view' afterfunc='dealRes(\""+i+"\",\"resumeFieldList\")'/>");
	                          								          }
                                                            	
                                                            		
                                                                }
                                                                else{
                                                             	out.print(" <input type='text' name='resumeFieldList["+i+"].viewvalue' value='"+viewvalue+"' style='padding-left:5px;' readonly />");                                                             
                                                                 out.print("<input type='hidden' value='"+value+"'  name='resumeFieldList["+i+"].value' /> &nbsp;");
                                                                }
                                                                    out.print("<a onclick='javascript:openInputCodeDialog2(\""+codesetid+"\",\"resumeFieldList["+i+"].viewvalue\",\"\",\"\",\"0\");' >   ");
                                                            }
                                                            //out.print(" <input type='text' name='resumeFieldList["+i+"].viewvalue' value='"+viewvalue+"' readonly />");                                                             
                                                            out.print("<input type='hidden' value='"+value+"'  name='resumeFieldList["+i+"].value' /> &nbsp;");
                                                           // out.print("<img  src='/images/overview_obj.gif' border=0 width=20 height=20 />");
                                                            if(onlyFieldSet.contains(fieldSetId)&&writeable.equals("1"))
                                                            {
                                                                if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null)
                                                                   out.print("</a>");
                                                            }
                                                            else
                                                            {
                                                                out.print("</a>");
                                                            }
                                                            out.print("</div>");
                                                        }
                                                        
                                                    }
                                                
                                                }
                                                else if(itemtype.equals("D"))
                                                {
                                                    out.println("<div class='input_bg1'>");
                                                    out.print("<input type='text'  name='resumeFieldList["+i+"].value'  value='"+value+"' style='padding-left:5px;'  dropDown='dropDownDate' itemlength="+itemlength);
                                                    if(onlyFieldSet.contains(fieldSetId)&&writeable.equals("1"))
                                                    {
                                                     if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null&isseqn.equals("0"))
                                                        out.print(" autocomplete='off'");
                                                     else
                                                        out.print(" readOnly ");
                                                    }
                                                    else
                                                    {
                                                       if(isseqn.equals("0")) {
                                                          out.print(" autocomplete='off'");
                                                       }
                                                       else
                                                          out.print(" readOnly ");
                                                    }
                                                    if(writeable.equals("1"))
                                                    	out.print(" readOnly ");
                                                    
                                                    if(itemid.equalsIgnoreCase(birthdayName)){
                                                    	out.print(" readOnly ");
                            					        out.println(" /><img src= '/images/hire/calendar.gif'  align='absmiddle'   id='calendar"+i+"' inputname='resumeFieldList["+i+"].value' format='Y-m-d' />");
                                                    }else
                                                    	out.println(" /><img src= '/images/hire/calendar.gif'  align='absmiddle'  plugin='datetimeselector' id='calendar"+i+"' inputname='resumeFieldList["+i+"].value' format='Y-m-d' afterfunc='me.selector.close();'/>");
                                                    out.print("</div>");
                                                }
                                                else if(itemtype.equals("N"))
                                                {
                                                    out.println("<div class='input_bg1'>");
                                                    out.print("<input type=\"text\" name=\"resumeFieldList["+i+"].value\"   value='"+value+"' style='padding-left:5px;' class=textbox  maxlength='"+totallength+"' ");
                                                    if(onlyFieldSet.contains(fieldSetId)&&writeable.equals("1"))
                                                        {
                                                            if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null&isseqn.equals("0"))
                                                                out.print("");
                                                            else
                                                                out.print(" readOnly ");
                                                         }
                                                        else
                                                        {
                                                           if(isseqn.equals("0"))
                                                               out.print("");
                                                           else
                                                               out.print(" readOnly ");
                                                        }
                                                    if(itemid.equalsIgnoreCase(ageName)){
                                                        out.print(" readOnly ");
                                                    }
                                                    
                                                    out.println("/>&nbsp;");
                                                    out.print("</div>");
                                                }
                                                else if(itemtype.equals("M"))
                                                {
                                                    
                                                    out.print("<textarea name=\"resumeFieldList["+i+"].value\" rows='10' style='padding-left:5px;width:350px;'  wrap='true'  class='class_text'");
                                                    if(onlyFieldSet.contains(fieldSetId)&&writeable.equals("1"))
                                                        {
                                                            if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null&isseqn.equals("0"))
                                                                out.print("");
                                                            else
                                                                out.print(" readOnly ");
                                                         }
                                                        else
                                                        {
                                                           if(isseqn.equals("0"))
                                                               out.print("");
                                                           else
                                                               out.print(" readOnly ");
                                                        }
                                                    out.println(">"+value+"</textarea>&nbsp;");
                                                }
                                            if(itemtype.equals("M")){
                                                if(must.equals("1"))
                                                    out.print("</td><td align='left'>&nbsp;<FONT color=red>* </FONT></td>");
                                                 if(isExp.equals("1"))
                                                    out.print("<td ><font color='#535455'> "+itemmemo+"</font></td></tr></table>");
                                            }else{                                                      
                                                if(must.equals("1")&&!isExp.equals("1")){//!isExp.equals("1")显示指标描述
                                                        out.print("&nbsp;<FONT color=red >* </FONT>");
                                                }
                                                    
                                                if(isExp.equals("1")){
                                                    out.print("</td>");
                                                    if(must.equals("1"))//||itemdesc.equalsIgnoreCase("身份证号")||itemdesc.equalsIgnoreCase("身份证号码")
                                                        out.print("<td><FONT color=red>* </FONT></td>");
                                                    
                                                    out.print("<td><font color='#535455'> "+itemmemo+"</font></td></tr></table>");
                                                } 
                                            }%>
                                            </div>
                                       </TD>
                                     </TR>                           
                                     <%                              
                                     }
                                     if(fieldSetId.equalsIgnoreCase("A01"))
                                     {
                                    %>
                                     <TR>
                                       <TD class=tdTitle valign="bottom"width="30%">
                                       	<font  class='class_text'><bean:message key="hire.cloumn.photo"/>：</font>
                                       </TD>
                                       <TD class=tdValue   >
                                       
                                            <font color='red'  class='class_text'>
                                             <% 
                                             	if(isUpPhoto.equals("1")){//如果必须上传照片
                                             %>
                                            	<bean:message key="hire.mustupload.photo"/>
                                             <%
                                            	} else{
                                             %>
                                            	<bean:message key="hire.upload.photo.validate"/>
                                            <%} %>
                                            </font><br>
                                            
                                            <div id="p"></div>
                                            <script type="text/javascript">
                                            var isChrome = checkBrowser();
                                            if(isChrome){
                                            	var str = "<input name='file' type='file' size='50' id='theFile' class='input_file'  onchange='return changeName(this);' accept='image/jpg,image/jpeg,image/bmp,image/gif'>";
                                           	str+="<input type='text'  id='textFile' readonly='true' size='38' maxlength='200'>";
                                           	str+="<input type='button'  value='浏览...' onclick='openFile()'>";
                                           	str+="<% if(isUpPhoto.equals("1")){ %> <FONT color=red >* </FONT> <% } %>";
                                           	p.innerHTML=str;
                                            }
                                           else{
                                           	var str2="<input name='file' id='theFile' type='file' size='35' style='background-image:url();border-top:1px solid #d0d0d0;border-left:1px solid #d0d0d0;border-right:1px solid #d0d0d0;border-bottom:1px solid #d0d0d0;'  accept='image/jpg,image/jpeg,image/bmp,image/gif'>&nbsp;";                             
                                           	str2+="<% if(isUpPhoto.equals("1")){ %> <FONT color=red >* </FONT> <% } %>";
                                           	document.getElementById("p").innerHTML=str2;
                                           }
                                           </script>
                                           
                                       </TD>
                                     </TR>
									<%if(isPhoto.equals("1")) {
										out.print("<tr><td></td><td>（已上传照片）</td></tr>");
										}
									}%>       
                                    
                                    </TABLE>
                                     <br>
                                  <TABLE id=Table2 cellSpacing=0 cellPadding=0 width="100%" border=0>
                                     <TBODY>
                                         <TR> 
                                            <TD style="TEXT-ALIGN:center">
                                                <%
                                                 if(!fieldSetId.equalsIgnoreCase("A01")){
                                                 %>                          
                                                    <a onclick='javascript:sub(4,"<%=answerFlag%>")' >
                                                     <IMG  src="/images/hire/up.gif" border=0 ><!-- 上一步 -->
                                                    </a>
                                                    &nbsp;&nbsp;&nbsp;&nbsp;
                                                <% }%>
                                                <logic:equal value="0" name="employPortalForm" property="writeable">
                                                   <%-- <a onclick='javascript:sub(2,"<%=answerFlag%>")' >
                                                   <IMG  src="/images/hire/bcdown.gif" border=0 ><!-- 保存&下一步 -->
                                                   </a>--%>
                                                   <a onclick='javascript:sub(1,"<%=answerFlag%>")' >
                                                   <IMG  src="/images/hire/save.gif" border=0 ><!-- 保存   -->
                                                   </a>
                                                        &nbsp;&nbsp;&nbsp;&nbsp;
                                                </logic:equal> 
                                               <%if((index+1)!=fieldSetList.size())
                                               {
                                             	%>
                                                <a onclick='javascript:sub(3,"<%=answerFlag%>")' >
                                                    <IMG  src="/images/hire/down.gif" border=0 ><!-- 下一步 -->
                                                </a>
                                              <%
                                               }
                                              if(index==(fieldSetList.size()-1))
                                              {
                                                   if(isAttach.equals("1")){
                                                   %>
                                                     <a onclick='javascript:sub(3,"<%=answerFlag%>")' >
                                                        <IMG  src="/images/hire/down.gif" border=0 ><!-- 下一步 -->
                                                    </a>
                                                  <%}else{%>
                                                   <logic:equal value="0" name="employPortalForm" property="writeable">
                                                        <%-- <a onclick='javascript:sub(0,"<%=answerFlag%>")' >
                                                        <IMG  src="/images/hire/saveandend2.gif" border=0 ><!-- 保存并结束 -->
                                                       </a> --%>
                                                       <a onclick='javascript:sub("finished","<%=answerFlag%>")' >
										                  <IMG src="/images/hire/finish.gif" border=0 >
										                  </a>
                                                    </logic:equal>
                                              <%
                                                     }
                                              } %>
                                           </TD>
                                       </TR>
                                 </TBODY>
                               </TABLE>
                                   
                                   <%}//如果不是开放问答子集end 
                                   
                                   } else{//opt!=1  这个只显示简历附件的信息,做什么用的 ？没有分析明白 
                                   %>
                                       <div class="jj zw">
                                           <h3>
                                               <span style='FONT-SIZE: 12px;font-family: 微软黑体;color:black;'>简历附件
                                                    <%-- <%if(!fieldSetId.equalsIgnoreCase("A01")&&!openanswer){ %>
                                                        <bean:message key="hire.fill.timeseq"/>
                                                    <% } %> --%>
                                                                   
                                                </span>
                                            </h3>
                                           <div class="nr" >                                       
                                               <table id="fileTable" width="0" border="0" cellspacing="0" cellpadding="0" class="table table2">
                                                     <tr  align='center' > 
                                                           <td align="center" class="changecolor"  height='26' nowrap>
                                                               <b style="color: #444a58;">
                                                                   <bean:message key="conlumn.mediainfo.info_id"/>
                                                               </b>                    
                                                           </td>
                                                            
                                                           <td align="center" class="changecolor"  height='26' nowrap>
                                                               <b style="color: #444a58;">
                                                                   <bean:message key="column.law_base.filename"/>
                                                               </b>                    
                                                           </td>
                                                            <logic:equal value="0" name="employPortalForm" property="writeable">
                                                             <td align="center" class="changecolor"  height='26' nowrap>
                                                                 <b style="color: #444a58;">
                                                                   <bean:message key="lable.tz_template.delete"/>
                                                                 </b>                  
                                                             </td>
                                                           </logic:equal>
                                                       </tr>
               
                                                       <%
                                                       for(int i=0;i<uploadFIleList.size();i++)
                                                       {
                                                            LazyDynaBean abean = (LazyDynaBean)uploadFIleList.get(i);
                                                            String styletdClass="hj_zhaopin_list_tab_titletwo";
                                                            out.println("<tr>");
                                                            if(i%2!=0){
                                                               out.println("<td align='center' height='36'bgcolor='#E8EBF1' >"+(i+1)+"</td>");
                                                               //out.println("<td align='center' bgcolor='#E8EBF1'>"+(String)abean.get("sortname")+"</td>");
                                                               out.println("<td align='center' id='fileName" + i + "' bgcolor='#E8EBF1' nowrap>"+(String)abean.get("fileName")+"</td>");
                                                               if(writeable.equals("0"))
                                                               {																																									//【11482】外网简历上传改为保存文件到文件夹，删除简历附件时，不能再使用i9999，改为使用文件id删除  jingq upd 2015.08.05
                                                                   out.println("<td align='center' bgcolor='#E8EBF1'><img src='/images/hire/del.gif' title='删除' border='0' style='cursor:pointer' onclick=\"deleteattach('','"+(String)abean.get("fileName")+"','"+(String)abean.get("nbase")+"','"+(String)abean.get("id")+"')\"/></td>");
                                                               }
                                                            }else{
                                                               out.println("<td align='center' height='36' >"+(i+1)+"</td>");
                                                               out.println("<td align='center' id='fileName" + i + "' nowrap>"+(String)abean.get("fileName")+"</td>");
                                                               if(writeable.equals("0"))
                                                               {
                                                                   out.println("<td align='center'><img src='/images/hire/del.gif' title='删除' border='0' style='cursor:pointer' onclick=\"deleteattach('','"+(String)abean.get("fileName")+"','"+(String)abean.get("nbase")+"','"+(String)abean.get("id")+"')\"/></td>");
                                                               }
                                                            }
                                                           out.println("</tr>"); 
                                                       }
                                                        %>
                                     
                                                    </table> 
                                                  <div id = "fileContent">
                                                  <script type="text/javascript">
                                                  isHaveAttachCodeSet();
                                                  </script>
                                                  </div>                                                                                                                       
	                                   <%} %>
          
                          <Input type="hidden" name='i9999' value="${employPortalForm.i9999}" /> 
                          <Input type='hidden' name='flag' value='1' />
                          <html:hidden name="employPortalForm" property="writeable"/>
                          <html:hidden name="employPortalForm" property="onlyField"/>
                          <html:hidden name="employPortalForm" property="isOnlyCheck"/>
                          <html:hidden name="employPortalForm" property="blackField"/>
                          <html:hidden name="employPortalForm" property="hireChannel"/>
                          <html:hidden name="employPortalForm" property="zpUnitCode"/>
                          <html:hidden name="employPortalForm" property="extendFile"/>
                  </div>
              </div>
	        <%-- <div class="operation">
	        <table  class="smallsize-font small-font">
	        <tr><td>保存：保存当前信息集；</td>
	        <%if(isAttach.equals("1")) {
        		out.print("<td>上传：上传文件；</td></tr>");
        		out.print("<tr><td>上传并结束：上传文件，并且结束填写；</td>");
	        }
       		out.print("<td>结束：结束填写；</td></tr>");
     		%>
	        </table>
	        </div> --%>

          </div>
           <div class='footer' style="height:0px;"> &nbsp;&nbsp;</div>
      </div>
        
    </div>
    </form>
    <!-- 查看文件属性插件  （文件大小等等）
        <OBJECT
	  id="FileView"
	  classid="clsid:152FC577-6940-4B1E-99BB-D4D5B8BF182E"
      codebase="/cs_deploy/FileViewerX.cab#version=1,0,0,4"
	  width=0
	  height=0
	  align=center
	  hspace=0
	  vspace=0
>
</OBJECT>
    
     -->

	<script type="text/javascript">    
	function sub2(flag)
    {
        var canSaveInfor = <%=canSaveInfor%>;
        if(!canSaveInfor){
        	alert(hire_preserve_basicInformation);
        	operating = false;
        	return;
        }
        var bool=true;
        <%//canSaveInfor  用来判断当前维护的信息是否能保存 
        if(fieldSetId.equalsIgnoreCase("A01"))
        {
            if(isPhoto.equals("0")&&isUpPhoto.equals("1"))   ///如果必须上传照片，但是还没有上传照片
            {
            %>
            if(flag!='3'&&flag!='4'&&trim(document.employPortalForm.file.value).length==0)
            {
                alert(PLEASE_UPLOAD_PHOTO+"！");///请您上传照片
                operating = false;
                return;
            }
            if(flag!='3'&&flag!='4'&&trim(document.employPortalForm.file.value).length!=0)
            {
                if(!validateUploadFilePath(document.employPortalForm.file.value)){//文件上传漏洞
                    alert("上传文件为不符合要求！请选择正确的文件上传！");
                    operating = false;
                    return;
                }
                
            }
            <%
            }
        
        %>
        var filePath = document.employPortalForm.file.value;
        if(trim(filePath).length!=0)
        {
            var extendFile=trim(filePath.substring(filePath.indexOf(".")+1,filePath.length));
            while(extendFile.indexOf(".")!=-1)
            {
                  extendFile=trim(extendFile.substring(extendFile.indexOf(".")+1,extendFile.length));
            }
            if(",jpg,jpeg,gif,bmp,png,".indexOf("," + extendFile.toLowerCase() + ",")<0)
            {
                alert(UPLOAD_FILE_FORMAT_MUST+"!");
                operating = false;
                return;
            }
            if(!validateUploadFilePath(filePath)){//文件上传漏洞
                alert("上传文件为不符合要求！请选择正确的文件上传！");
                operating = false;
                return;
            }  
            var fileSize = 0;
			var isIE = /msie/i.test(navigator.userAgent) && !window.opera;    
			var facSize = 0;          
			if (isIE && !filePath.files) {  
				try {
					var fileSystem = new ActiveXObject("Scripting.FileSystemObject");         
					var file = fileSystem.GetFile (filePath);  
					facSize = file.Size;
				} catch(e) {
					facSize = 1024;
				}
			} else {     
				facSize = document.getElementById("theFile").files[0].size;      
			}
            var  photo_maxsize="512";
            if(parseInt(photo_maxsize,10)>0&&parseInt(photo_maxsize,10)<parseInt(facSize,10)/1024)
            {  
               alert("上传文件请控制在512KB以下！");
               operating = false;
               return;
            } 
              
            document.employPortalForm.extendFile.value=extendFile;
        }
        <%
        }
        if(idIndex==-1)//如果没有身份证的验证 
        {
             if(blackIndex!=-1)
             {//如果有黑名单中的值 
               %>
                bool=false;
                var hashvo=new ParameterSet();
                hashvo.setValue("type","2");
                hashvo.setValue('a0100',"${employPortalForm.a0100}");
                hashvo.setValue("blackFieldItem","<%=blackFieldItem%>");
                hashvo.setValue("blackNbase","<%=blackNbase%>");
                if(blackFieldValue==null||blackFieldValue=='')
                    blackFieldValue=document.getElementsByName("resumeFieldList[<%=blackIndex%>].value")[0].value;
                hashvo.setValue("blackFieldValue",getEncodeStr(blackFieldValue));
                blackFieldValue="";
                var In_paramters="dbname=${employPortalForm.dbName}";  
                var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000171'},hashvo);
                return;
               <%
             }
              if(isAttach.equals("1"))
             {//如果有上传简历
                
                 if(index==(fieldSetList.size()-1))
                 {
                %>
                  // 1:保存并添加  0：修改 2:保存走下一步 3下一步；4上一步
                  if(flag == '3' || flag=='2')
                	  document.employPortalForm.action='/hire/hireNetPortal/search_zp_position.do?b_addInfo=add&opt=2&flag='+flag;
                  else
                     document.employPortalForm.action='/hire/hireNetPortal/search_zp_position.do?b_addInfo=add&flag='+flag;
                <%
                 } else
                 {
                 %>
                     document.employPortalForm.action='/hire/hireNetPortal/search_zp_position.do?b_addInfo=add&flag='+flag;  
                <%
                 }
              }
             else
             {
                if(index==(fieldSetList.size()-1))
                 {
                 %>
                 
                   if(flag=='0')//保存并结束
                  {
                      document.employPortalForm.action='/hire/hireNetPortal/search_zp_position.do?b_addeInfo=add&finished=1';
                  }else
                  {
                      document.employPortalForm.action='/hire/hireNetPortal/search_zp_position.do?b_addInfo=add&flag='+flag;
                  }
                 <%
                 }
                 else
                 {
                 %>
                     document.employPortalForm.action='/hire/hireNetPortal/search_zp_position.do?b_addInfo=add&flag='+flag;  
                <%
                  }
             }
             %>
             <%
             if(fieldSetId.equalsIgnoreCase("A01"))
             {
             if((isOnlyChecked.equals("1")&&onlyField.length()>0))
             {
             %> 
             var hashvo=new ParameterSet();
             hashvo.setValue("type","1");
             hashvo.setValue('a0100',"${employPortalForm.a0100}");
             hashvo.setValue("only_str",getEncodeStr(only_str));
             hashvo.setValue("blackFieldItem","<%=blackFieldItem%>");
             hashvo.setValue("blackNbase","<%=blackNbase%>");
             hashvo.setValue("blackFieldValue",getEncodeStr(blackFieldValue));
             only_str="";
             blackFieldValue="";
             var In_paramters="dbname=${employPortalForm.dbName}";
             var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000171'},hashvo);
             return;
             <%}
             else
             {
               %>
                 document.employPortalForm.action='/hire/hireNetPortal/search_zp_position.do?b_addInfo=add&flag='+flag;  
                // document.employPortalForm.submit(); //下面已经有一次提交表单了
               <%
             }
             }
             %>
             if(bool){
                  document.employPortalForm.submit();
	              
             }
        <%
        }
        else if(isOnlyChecked.equals("0")||onlyField.length()<=0)
        {
        %>
       		var a<%=idIndex%>=document.getElementsByName("resumeFieldList[<%=idIndex%>].value");
            var len=a<%=idIndex%>[0].value.replace(/[^\x00-\xff]/g,"**").length;
            if(flag!='3'&&flag!='4'&&len!=13&&len!=15&&len!=18)
            {
                alert(IDCARD_FILL_NOT_RIGHT+"!");
                operating = false;
                return;
            }
            var hashvo=new ParameterSet();
            hashvo.setValue("type","0");
            hashvo.setValue("idValue",a<%=idIndex%>[0].value);
            hashvo.setValue("idItem",'<%=idItemId%>');          
            hashvo.setValue('a0100',"${employPortalForm.a0100}");
            hashvo.setValue("blackFieldItem","<%=blackFieldItem%>");
            hashvo.setValue("blackNbase","<%=blackNbase%>");
            if(blackFieldValue==null||blackFieldValue=='')
                blackFieldValue=document.getElementsByName("resumeFieldList[<%=blackIndex%>].value")[0]==null?"":document.getElementsByName("resumeFieldList[<%=blackIndex%>].value")[0].value;
            hashvo.setValue("blackFieldValue",getEncodeStr(blackFieldValue));
            blackFieldValue="";
            var In_paramters="dbname=${employPortalForm.dbName}";
            new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000171'},hashvo);
			//如果只有主集信息，保存并结束，也给出提示信息并跳转页面
            if(flag==0){
            	Ext.Msg.show({
    				title:"提示信息",
    				message:YOUR_RESUME_SUBMIT_SUCCESS,
    				buttons: Ext.Msg.OK,
    			    icon: Ext.Msg.INFO,
    			    fn: function(btn) {
    			    	if (btn === 'ok') {
                			document.location="/hire/hireNetPortal/search_zp_position.do?b_query=link&operate=init&abcd=1";
    			    	}
    			    }
            	});
            }
            return;
        <%
        }else{
        	%>
        	document.employPortalForm.action='/hire/hireNetPortal/search_zp_position.do?b_addInfo=add&flag='+flag;   
            document.employPortalForm.submit();
        	<%
        }
        %>
        
    }

    function returnInfo(outparamters)
    {
        var info=outparamters.getValue("info");
        var type=outparamters.getValue("type");
        if(info=='failue')
        {
            if(type=='0')
            {
                var msg=getDecodeStr(outparamters.getValue("msg"));
                if(msg=='1')
                {
                   alert(IDCARD_NUMBER_ALREADY_HAVE+"！");
                   operating = false;
                   return;
                }
                else
                {
                    alert(msg);
                    operating = false;
                    return;
                }
            } 
            else
            {
                var msg=getDecodeStr(outparamters.getValue("msg"));
                alert(msg);
                operating = false;
                return;
            }
        }
        else
        {
            document.employPortalForm.action='/hire/hireNetPortal/search_zp_position.do?b_addInfo=add';
            document.employPortalForm.submit();
        }
    }
    <%
    if(request.getParameter("finished")!=null&&request.getParameter("finished").equals("1"))
    {
      if(isResumePerfection.equals("0")){
    %>
	    Ext.Msg.show({
			title:"提示信息",
			message:"您的简历资料没有填写完整，请完善您的简历！",
			buttons: Ext.Msg.OK,
		    icon: Ext.Msg.INFO,
		    fn: function(btn) {
		    	if (btn === 'ok') {
		            document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_showResumeList=show&setID=0&opt=1";
		            document.employPortalForm.submit();
		    	}
		    }
	    });
    <%} else if(!"1".equals(isResumePerfection)){%>
    		var str = "<%=isResumePerfection%>";
    		var arr = str.split("-");
    		if(arr.length>2)
    			alert(arr[1]+arr[2]);
    		else
    			alert(arr[1]+"必须填写！");
    		document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_showResumeList=show&setID="+arr[0]+"&opt=1";
            document.employPortalForm.submit();
    <%}else{%>
    window.setTimeout('alerts()',200);   
    function alerts()
    {
    <%
    	if(isHeadhunter!=null&&isHeadhunter.equals("1")){//进来的用户是猎头身份 ,跳转到推荐简历页面
    %>
    	document.location="/hire/hireNetPortal/recommend_resume.do?b_recommendResume=query";
    <% 		
    	}else{
    %>
        alert(YOUR_RESUME_SUBMIT_SUCCESS);
        document.location="/hire/hireNetPortal/search_zp_position.do?b_query=link&operate=init&abcd=1";
    <%
    	}
    %>
    }
    <%
    } }%>
    
    var fileSum = 0;
	function uploadnn(flag)
	{
		if(uploadflag)
			return;
		var canSaveInfor = <%=canSaveInfor%>;
	    if(!canSaveInfor){
	    	alert(hire_preserve_basicInformation);
	    	return;
	    }
	  if(uploadflag)
		return;
		var canSaveInfor = <%=canSaveInfor%>;
	    if(!canSaveInfor){
	    	alert(hire_preserve_basicInformation);
	    	return;
	    }
	    var selectflag = false;
	    <%if(attachCodeSet==null||attachCodeSet.size()==0){%>
	    	selectflag = checkFile(flag,0,"文件附件", '0');
			if(!selectflag)
				return;
		<%}else{
			for(int i=0;i<attachCodeSet.size();i++){
				HashMap map = (HashMap)attachCodeSet.get(i);
				if("0".equals(map.get("notNull")))
				    continue;
		%>
			selectflag = checkFile(flag,<%=i%>,"<%=map.get("itemDesc")%>",'<%=map.get("notNull") %>');
			if(!selectflag)
				return;
		<%}}%>
		if(fileSum < 1 && flag!=2){
			alert(SELECT_FIELD+"!");
			return;
		}
		
		uploadflag = true;
		if(flag=='1')
			document.employPortalForm.action = "/hire/hireNetPortal/search_zp_position.do?b_uploade=upload&finished=1&hireChannel=<%=hirechannel%>";
		else if(flag=='2')
	    	document.employPortalForm.action = "/hire/hireNetPortal/search_zp_position.do?b_uploade=upload&finished=1&marked=end&hireChannel=<%=hirechannel%>";
		else
	    	document.employPortalForm.action = "/hire/hireNetPortal/search_zp_position.do?b_upload=upload";
		document.employPortalForm.submit();
	}
	
	function checkFile(flag,index,desc, notNull){
		var path=document.getElementById("attachFile"+index).value;
		if(path){
			var extendFile=trim(path.substring(path.indexOf(".")+1,path.length));
			while(extendFile.indexOf(".")!=-1)
			{
				extendFile=trim(extendFile.substring(extendFile.indexOf(".")+1,extendFile.length));
			}
			if("doc,docx,xls,xlsx,rar,zip,ppt,jpg,jpeg,png,bmp,txt,wps,pptx,pdf".indexOf(extendFile.toLowerCase())==-1){
				alert("文件格式不对,请选择doc,docx,xls,xlsx,rar,zip,ppt,jpg,jpeg,png,bmp,txt,wps,pptx,pdf等文件格式上传!");
				return false;
			}
			if(flag==0 || flag==1)
			{
				if(!validateUploadFilePath(path))//文件上传漏洞
					return false;
				
				var fileSize = 0;
				var isIE = /msie/i.test(navigator.userAgent) && !window.opera;    
				var fileSize = 0;  
				var fileName ="";
				if (isIE && !path.files) {  
					try {
						var fileSystem = new ActiveXObject("Scripting.FileSystemObject");         
						var file = fileSystem.GetFile (path);  
						fileSize = file.Size;
						fileName = file.name;
					} catch(e) {
						fileSize = 1024;
					}
				} else {     
					fileSize = document.getElementById("attachFile"+index).files[0].size;
					fileName = document.getElementById("attachFile"+index).files[0].name;
				}
				
				if (fileName.split(".").length > 2) {
					alert("文件上传失败 ,文件名称不能带有特殊字符'.' " );
					return false;			
				}
				
				if(fileSize=="0"){
					alert(desc+"为空文件!");
					return false;
				}
				if(fileSize><%=maxSize*1024*1024 %>){
					alert("上传附件大小不得超过" + <%=maxSize %> + "M!");
					return false;					
				}
				
			}
			
			fileSum++;
		} else if("2" == notNull){
			var fileFlag = false;
			var rows = document.getElementById("fileTable").rows.length;
			for(var i=0; i < rows-1; i++){
				var name = document.getElementById("fileName" + i);
				if(name) {
					var fileName = name.innerHTML;
					if(fileName.lastIndexOf(".") > -1)
						fileName = fileName.substring(0, fileName.lastIndexOf("."));
					
					if(desc == fileName) {
						fileFlag = true;
						break;
					}
				}
			}
			
			if(!fileFlag) {
				alert(desc + "附件不能为空！");
				return false;
			}
		}
		return true;
	}
	function openFile()
	{
	///document.all("theFile").click();  
	    document.getElementById("theFile").click();
	}
	function changeName(obj)
	{   
	    var file_url = obj.value;
	    var index = file_url.lastIndexOf("\\");
	    var filename = file_url.substring(index+1,file_url.length);
	    document.getElementById("textFile").value=filename; 
	}
	function resume_openFile(obj)
	{
	    document.getElementById("attachFile"+obj).click();
	}   
	function Resume_changeName(obj)
	{
	    var file_url = obj.value;
	    var index = file_url.lastIndexOf("\\");
	    var filename = file_url.substring(index+1,file_url.length);
	    document.getElementById("resumefile"+obj.name).value=filename;
	}
	function dealRes(i,name){
		document.getElementsByName(name+"["+i+"].viewvalue_view")[0].value = replaceAll(document.getElementsByName(name+"["+i+"].viewvalue_view")[0].value, "|", ",");
		document.getElementsByName(name+"["+i+"].value")[0].value = document.getElementsByName(name+"["+i+"].viewvalue_value")[0].value;
	}

</script>
</body>
</html>
<script type="text/javascript">
     initCard();
</script> 