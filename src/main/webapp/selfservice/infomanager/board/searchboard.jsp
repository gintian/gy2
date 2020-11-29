<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.board.BoardForm, com.hrms.frame.dao.RecordVo,com.hrms.frame.codec.SafeCode"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes></hrms:themes>
<script language="javascript">
function selectobject(obj)
{
	var objecttype = obj.value;
	var object1 = document.getElementById("04");
	var object2 = document.getElementById("05");
	var object3 = document.getElementById("01");
	var object4 = document.getElementById("03");
	object1.value="";
    object2.value="";
    object3.value="";
    if(objecttype=="00"){
    	object1.style.display="none";
    	object2.style.display="none";
    	object4.style.display="none";
    	object3.style.display="none";
    }
    if(objecttype=="01"||objecttype=="02"||objecttype=="03"){
    	if(object1.style.display!="none"||object2.style.display!="none"||object4.style.display!="none"){
    		object1.style.display="none";
    		object2.style.display="none";
    		object4.style.display="none";
    		object3.style.display="block";
    	} else {
	    	object3.style.display="block";
	    }
	} 
	if(objecttype=="04"||objecttype=="05"){
		if(object3.style.display!="none"){
			object3.style.display="none";
			object1.style.display="block";
	    	object2.style.display="block";
	    	object4.style.display="block";
		} else {
		    object1.style.display="block";
		    object2.style.display="block";
		    object4.style.display="block";
	    }
	}
}

function query(){
	var selparam = document.getElementById("01").value;
	var str1 = document.getElementById("04").value;
	var str2 = document.getElementById("05").value;
	if(TestTime(str1)==false||TestTime(str2)==false){
			document.getElementById("04").value="";
			document.getElementById("05").value="";
			document.getElementById("04").focus;
	}else{
		var s = "";		//特殊字符处理
		var b = "";
		for(var j=0;j<selparam.length;j++){
			s = selparam.substring(j,j+1);
			if(s=='#'){		
				b+="nbspa";
			} else if(s=="；"){
				b+="quanjiao;hao";
			}else {
				b+=s;
			}
		}
		selparam = b;
		selparam = getEncodeStr(selparam);
		selparam = $URL.encode(selparam);
		boardForm.action = "/selfservice/infomanager/board/searchboard.do?b_query=link&opt=${boardForm.opt}&announce=${boardForm.announce}&seltype=1&selparam="+selparam;
		boardForm.submit();
	}
}
	//判断输入的日期是否为指定格式	YYYY-MM-DD
	function TestTime(str){
		var temp = true;
		if(str!=null&&str.length>0){
			if(str.length==10){
				var s = str.split("");
				for(var i=0;i<s.length;i++){
					if(i==4||i==7){
						if(s[i]!="-"){
							alert('<bean:message key="search.date_style.error"/>');
							temp = false;
							break;
						}
					} else {
						var reg = /^[0-9]+[0-9]*]*$/;
						if(!reg.test(s[i])){
							alert('<bean:message key="search.date_style.error"/>');
							temp = false;
							break;
						}
					}
				}
			} else {
				alert('<bean:message key="search.date_style.error"/>');
				temp = false;
			}
		} else {
			temp = true;
		}
		return temp;
	}
	
	document.onkeyup = function(event){		
		var e = event||window.event;
		var keycode = e.keyCode||e.swhich;
		switch(keycode){
			case 13:	//Enter键
				$("button1").click();
				break;
			default:
				break;
		}
	}
</script>
<%
	int i=0;
	
	try
	{
		String opt=request.getParameter("opt");
		
		String ret=request.getParameter("br_return");
		BoardForm bs=(BoardForm)session.getAttribute("boardForm");
		opt=bs.getOpt();
		if(opt==null){
			opt="1";
		}
		String selparam = bs.getSelparam();
		StringBuffer sb = new StringBuffer();
		String s = "";
		String n = "";
		//处理\"编译不通过
		selparam = SafeCode.decode(selparam);
		if(selparam!=null&&selparam.length()>0){
			selparam = selparam.replace("quanjiao;hao", "；");
			for(i = 0;i<selparam.length();i++){
				s = selparam.substring(i, i+1);
				if("\\".equals(s)){
					s = s+s;
					sb.append(s);
				} else if("\"".equals(s)){
					sb.append("\\\"");
				} else {
					sb.append(s);
				}
			}
			selparam = sb.toString();
		}
		String begintime = bs.getBegintime();
		String endtime = bs.getEndtime();
%>

<html:form action="/selfservice/infomanager/board/searchboard">
<html:hidden name="boardForm" property="chflag"/>
<html:hidden name="boardForm" property="opt"/>
<!-- 公告栏维护模糊查询   jingq  add 2014.5.9 -->
<table width="85%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" style="margin-top:-4px"> 
	 <tr>
            <td width="100%" height="35px;" align="left" nowrap valign="top">
            <table style="border-collapse:collapse;" cellspacing="0" cellpadding="1">
            	<tr height="35px">
            	<td align="left" noWrap valign="middle">
            	<bean:message key="kq.class.applyscope"/>
            	</td>
            	<td align="left" noWrap valign="middle">
            	<hrms:optioncollection name="boardForm" property="typelist"
                    collection="list" />
                <html:select name="boardForm" property="thistype"
                    onchange="selectobject(this);" style="width:80">
                    <html:options collection="list" property="dataValue"
                        labelProperty="dataName" />
                </html:select>
                </td>
                <logic:equal name="boardForm" property="thistype" value="01">
                	<script language="javascript">
                		function myfun1(){
                			var obj1 = document.getElementById("01");
                			obj1.value = "<%=selparam%>";
                			obj1.style.display = "block";
                		}
                		window.onload = myfun1;
                	</script>
                </logic:equal>
                <logic:equal name="boardForm" property="thistype" value="02">
                	<script language="javascript">
                		function myfun1(){
                			var obj1 = document.getElementById("01");
                			obj1.value = "<%=selparam%>";
                			obj1.style.display = "block";
                		}
                		window.onload = myfun1;
                	</script>
                </logic:equal>
                <logic:equal name="boardForm" property="thistype" value="03">
                	<script language="javascript">
                		function myfun1(){
                			var obj1 = document.getElementById("01");
                			obj1.value = "<%=selparam%>";
                			obj1.style.display = "block";
                		}
                		window.onload = myfun1;
                	</script>
                </logic:equal>
                <logic:equal name="boardForm" property="thistype" value="04">
                	<script language="javascript">
                		function myfun2(){
                			var obj1 = document.getElementById("04");
                			var obj2 = document.getElementById("05");
                			var obj3 = document.getElementById("03");
                			obj1.value="<%=begintime%>";
                			obj2.value="<%=endtime%>";
                			obj1.style.display = "block";
                			obj2.style.display = "block";
                			obj3.style.display = "block";
                		}
                		window.onload = myfun2;
                	</script>
                </logic:equal>
                <logic:equal name="boardForm" property="thistype" value="05">
                	<script language="javascript">
                		function myfun2(){
                			var obj1 = document.getElementById("04");
                			var obj2 = document.getElementById("05");
                			var obj3 = document.getElementById("03");
                			obj1.value="<%=begintime%>";
                			obj2.value="<%=endtime%>";
                			obj1.style.display = "block";
                			obj2.style.display = "block";
                			obj3.style.display = "block";
                		}
                		window.onload = myfun2;
                	</script>
                </logic:equal>
                <td align="left" noWrap valign="middle">
                <input type="text" name="selparam" id="01" style="display:none;text-align:left;height:20" class="editor text4"></input>
                <input type="text" id="04" style="display:none;text-align:left;height:20" name="begintime" extra="editor" dropDown="dropDownDate" title="<bean:message key="search.date_style.text"/>" class="text4"/>
                </td>
                <td valign="middle" align="left">
                <div id="03" style="display:none;border:none;"><bean:message key="kq.init.tand"/></div>
                </td>
                <td valign="middle" align="left">
                <input type="text" id="05" style="display:none;text-align:left;height:20" name="endtime" extra="editor" dropDown="dropDownDate" title="<bean:message key="search.date_style.text"/>" class="text4"></input>
                </td>
                <td align="left" noWrap valign="middle">
                <input type="button" id="button1" class="mybutton" value="<bean:message key="button.query"/>" onclick="query();"/>
	 			</td>
	 			</tr>
	 		</table>
        </td>
    </tr>                                                     
</table>
<table style="margin-top: -3px;" width="85%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
            <input type="checkbox" name="selbox" onclick="batch_select(this,'boardForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;	    
            </td>           
            <td align="center" class="TableRow" nowrap width="30%">
		<bean:message key="conlumn.board.topic"/>&nbsp;
	    </td>
            <!--<td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.board.content"/>&nbsp;
	    </td>-->
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.board.createuser"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.board.createtime"/>&nbsp;
	    </td>
	    <%if(!opt.equalsIgnoreCase("2")){ %>
	    <td align="center" class="TableRow" nowrap>
	    	<bean:message key="conlumn.board.approvetime"/>&nbsp;
	    </td>
	    <%} %>
	     <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.board.period"/>&nbsp;
	    </td>
	    <td align="center" class="TableRow" nowrap>
			<bean:message key="conlumn.board.priority"/>&nbsp;
	    </td>
	    <td align="center" class="TableRow" nowrap>
			<bean:message key="lable.resource_plan.org_id"/>&nbsp;
	    </td>
	    <%if(!opt.equalsIgnoreCase("2")){ %>
            <td align="center" class="TableRow" nowrap>
			<bean:message key="conlumn.board.approve"/>&nbsp;
	  	  </td>
             <%} %>	    
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.view"/>            	
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>            	
	    </td>
	     <%if(!opt.equalsIgnoreCase("2")){ %>
		 <hrms:priv func_id="07050101">
         <td align="center" class="TableRow" nowrap>
			<bean:message key="conlumn.board.approveoperation"/>            	
		 </td>	
		 </hrms:priv>
		 <%} %>
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="boardForm" property="boardForm.list" indexes="indexes"  pagination="boardForm.pagination" pageCount="${boardForm.pagerows }" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow" id="<bean:write name="element" property="string(id)" filter="true"/>">
          <%}
          else
          {%>
          <tr class="trDeep" id="<bean:write name="element" property="string(id)" filter="true"/>">
          <%
          }
          i++;          
          %>  
            <td align="center" class="RecordRow" nowrap>
     		   <hrms:checkmultibox name="boardForm" property="boardForm.select" value="true" indexes="indexes"/>&nbsp;
	    </td>            
            <td align="left" class="RecordRow" >
                   &nbsp;<bean:write name="element" property="string(topic)" filter="true"/>&nbsp;
	    </td>
            <!--
            <td align="left" class="RecordRow" nowrap>
          
                   <bean:write name="element" property="string(content)" filter="false"/>&nbsp;
	    </td>-->
            <td align="left" class="RecordRow" nowrap>
                   &nbsp;<bean:write name="element" property="string(createuser)" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                   &nbsp;<bean:write name="element" property="string(createtime)" filter="true"/>&nbsp;
	    </td> 
	     <%if(!opt.equalsIgnoreCase("2")){ %>
	    <td align="left" class="RecordRow" nowrap>
	    	  &nbsp;<bean:write name="element" property="string(approvetime)" filter="true"/>&nbsp;
	    </td>	                
	    <%} %>
        <td align="left" class="RecordRow" nowrap>
                   &nbsp;<bean:write name="element" property="string(period)" filter="true"/>&nbsp;
	    </td>
	     <td align="left" class="RecordRow" nowrap>
                   &nbsp;<bean:write name="element" property="string(priority)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
                   <% 
                   RecordVo boardvo = (RecordVo)pageContext.getAttribute("element");
                   String unitCode = boardvo.getString("unitcode");
                   if(unitCode==null || unitCode.equals("")){
                   %>
                	   &nbsp;
                	   <bean:message key="jx.khplan.hjsj"/>
                	   &nbsp;
                	   <% 
                   }else{
                	   %>
                	   &nbsp;
                       <hrms:codetoname codeid="UN" name="element" codevalue="string(unitcode)" codeitem="codeitem" scope="page"/>     
   					<bean:write name="codeitem" property="codename" />
                       &nbsp; 
                       <% 
                   }
                   %> 
                  
	    </td>
	     <%if(!opt.equalsIgnoreCase("2")){ %>
	    <td align="left" class="RecordRow" nowrap>
                   &nbsp;<bean:write name="element" property="string(approve)" filter="true"/>&nbsp;
	    </td>
	    <%} %>
	    <%
	    	RecordVo vo = (RecordVo)pageContext.getAttribute("element");
	    	String a_id = vo.getString("id");
	     %>
            <td align="center" class="RecordRow" nowrap>
            	<a href="/selfservice/infomanager/board/viewboard.do?b_query=link&encryptParam=<%=PubFunc.encrypt("a_id="+a_id+"&opt="+opt)%>"><img src="/images/view.gif" border=0></a>
	    </td>
            <td align="center" class="RecordRow" nowrap>
             
            	<a href="/selfservice/infomanager/board/addboard.do?b_query=link&encryptParam=<%=PubFunc.encrypt("a_id="+a_id+"&opt="+opt)%>"><img src="/images/edit.gif" border=0></a>
	    	
	    </td>
	     <%if(!opt.equalsIgnoreCase("2")){ %>
			 <hrms:priv func_id="07050101">
            <td align="center" class="RecordRow" nowrap>
            
            	<a href="/selfservice/infomanager/board/replyboard.do?b_query=link&encryptParam=<%=PubFunc.encrypt("a_id="+a_id)%>"><img src="/images/edit.gif" border=0></a>
	     
			 </td>
			 </hrms:priv>
			 <%} %>
          </tr>
        </hrms:extenditerate> 
        
</table>

<table  width="85%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		      <hrms:paginationtag name="boardForm" pagerows="${boardForm.pagerows}" property="boardForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
			</td>
	        <td  align="right" nowrap class="tdFontcolor">
		      <p align="right">
		          <hrms:paginationlink name="boardForm" property="boardForm.pagination"
				nameId="boardForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>

<table  width="85%" align="center">
          <tr>
            <td align="center" height="35px;">
            <!--
            <html:button styleClass="mybutton" property="b_issue" onclick="selectobject()">
            		<bean:message key="button.issue"/>
	 	    </html:button>-->
         	<hrms:submit styleClass="mybutton" property="b_add">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
         	<hrms:submit styleClass="mybutton" property="b_delete" onclick="return ifdel();">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
        
            </td>
          </tr>          
</table>
<input type="hidden" name="usrnames" value=""/>
</html:form> 
<%
	}
	catch(OutOfMemoryError error)
	{
		error.printStackTrace();
	}
	catch(Exception ex)
	{
	ex.printStackTrace();
	}
%>
<script type="text/javascript">
<!--
function ifdel()
{	
	   var isSelected=false;
   	   for(var i=0;i<document.boardForm.elements.length;i++)
   	   {
   			if(document.boardForm.elements[i].type=='checkbox'&&document.boardForm.elements[i].name.length>17&&document.boardForm.elements[i].name.substring(0,17)=='boardForm.select[')
   			{
   				if(document.boardForm.elements[i].checked==true)
   				{
   					isSelected=true;
   					
   				}  				
   			}
   		}
   		
  		if(!isSelected)
  		{
  			alert(PLASE_SELECT_RECORD+"！");
  			return false ;
  		}else{
  			return ( confirm('确认删除选择的项目？') );
  		}		

}

//-->
</script>