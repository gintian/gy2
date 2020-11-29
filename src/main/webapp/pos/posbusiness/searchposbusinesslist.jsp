<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.dao.RecordVo"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.*,
				com.hjsj.hrms.actionform.pos.PosBusinessForm" %>
<%
	  int i=0;
	    String userName = null;
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    
%>
  <logic:equal name="posBusinessForm" property="isrefresh" value="yes">
    <script language="javascript">
       //parent.mil_menu.location.reload();
   </script>
  </logic:equal>
  <script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
  <link href="/css/xtree.css" rel="stylesheet" type="text/css">
  <SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>

<script language="javascript">
     var itemids="";
    function exeButtonAction(actionStr,target_str)
   {
       target_url=actionStr;
       window.open(target_url,target_str); 
   }
   function deleterec()
   {
  	   var len=document.posBusinessForm.elements.length;
       var uu;
       for (i=0;i<len;i++)
       {
           if (document.posBusinessForm.elements[i].type=="checkbox"&&document.posBusinessForm.elements[i].name!="box")
           {
              if(document.posBusinessForm.elements[i].checked==true)
              {
                uu="dd";
                break;
               }
           }
       }
       if(uu!="dd")
       {
          alert(NOTING_SELECT);
          return false;
       }
      if(confirm("确认要删除吗?"))
      {
        posBusinessForm.action="/pos/posbusiness/searchposbusinesslist.do?b_delete=del";
        posBusinessForm.submit(); 
       }
   }
   function deleter(isrefresh,code)
   {
   		//alert(isrefresh+"  "+code);
   		if(isrefresh=='ok')
   		{
			var currnode=parent.frames['mil_menu'].Global.selectedItem;
			if(currnode==null)
					return;
			//var childlist = new Array();
			//childlist = parent.frames['mil_menu'].Global.selectChildList();
			//for(var i=0;i<childlist.length;i++){
			//	if(childlist[i].uid==code)
			//		childlist[i].remove();
			//}
			if(currnode.load)
			for(var i=0;i<=currnode.childNodes.length-1;i++){
				if(code==currnode.childNodes[i].uid)
					currnode.childNodes[i].remove();
			}
		}
   }
   function save(isrefresh,a_code,codesetid,codeitemid,codeitemdesc)
   {
   		
   		a0000='${posBusinessForm.a0000 }';
   	 if(isrefresh=='save'&&a0000.length==0)
   	 {
   	 	//alert(a_code+' zj4 '+codesetid+' zj '+codeitemid+' 5 '+codeitemdesc+' 5 ');
   	 	var currnode=parent.frames['mil_menu'].Global.selectedItem;
   	 	var pt = currnode.getLastChild();
   	 	if(pt.uid==codesetid+a_code+codeitemid)
   	 		return;
   	 	var uid = a_code+codeitemid;
   	 	var text = codeitemdesc;
   	 	var title = codeitemdesc;
   	 	var action = "/pos/posbusiness/searchposbusinesslist.do?b_query=link&a_code="+a_code+codeitemid;
   	 	//var xml = "/pos/posbusiness/get_code_tree.jsp?codesetid="+codesetid+"&codeitemid="+codeitemid;
   	 	//var xml = "/servlet/codesettree?flag=3&codesetid="+codesetid+"&parentid="+a_code.substring(2)+codeitemid;
   	 	if("68"==codesetid)
   	 		var xml="/pos/posbusiness/get_code_tree.jsp?codesetid="+codesetid+"&codeitemid="+codeitemid+"&action=/pos/posbusiness/searchposbusinesslist.do"
   	 	else
   	 		var xml = "/servlet/codesettree?flag=3&codesetid="+codesetid+"&parentid="+a_code.substring(2)+codeitemid;
   	 	if(currnode.load)
   	 	{
   	 		var imgurl;
   	 		//imgurl="/images/table.gif";
   	 		//alert(uid+'  '+text+'  '+action+'   '+"mil_body"+'     '+title+'     '+imgurl+'   '+xml);
   	 		//var tmp = new xtreeItem(uid,text,action,"mil_body",title,imgurl,xml);
   	 		
   	 		/*if(parent.frames['mil_menu'])
   	 		{
   	 		  parent.frames['mil_menu'].add1(uid,text,action,"mil_body",title,imgurl,xml);
   	 		}*/
   	 	if(currnode==null)
			return;
		if(currnode.load){
					while(currnode.childNodes.length){
						//alert(currnode.childNodes[0].uid);
						currnode.childNodes[0].remove();
					}
					currnode.load=true;
					currnode.loadChildren();
					currnode.reload(1);
				}
   	 		
   	 	}else{
   	 		currnode.expand();
   	 	}
   	 }else{
   	 	var currnode=parent.frames['mil_menu'].Global.selectedItem;
		if(currnode==null)
			return;
		if(currnode.load){
					while(currnode.childNodes.length){
						//alert(currnode.childNodes[0].uid);
						currnode.childNodes[0].remove();
					}
					currnode.load=true;
					currnode.loadChildren();
					currnode.reload(1);
				}
   	 }
   }
   function update(isrefresh,codesetid,codeitemid,codeitemdesc)
   {
   	
   	if(isrefresh=='update')
   	{
   		var currnode=parent.frames['mil_menu'].Global.selectedItem;
   		if(currnode.load)
			for(var i=0;i<=currnode.childNodes.length-1;i++){
				if((codesetid+codeitemid)==currnode.childNodes[i].uid)
					currnode.childNodes[i].setText(codeitemdesc);
   		}
   	}
   }
   function toretrun()
   {
   	 	posBusinessForm.action="/system/codemaintence/codetree.do?b_search=link";
        posBusinessForm.submit();
   }
   function closel()
   {
   		window.returnValue='<bean:write name="posBusinessForm" property="codesetid" filter="true"/>';
		window.close();
   }
   //[bug 13365] 点击弹窗页面右上角叉号 关闭页面 参数无法传递报错 加上关闭之前的判断传参操作   upd hej 2015/10/21
   window.onbeforeunload = function(){  
	   if(event.clientX>document.body.clientWidth && event.clientY < 0 || event.altKey){//关闭页面
			 window.returnValue='<bean:write name="posBusinessForm" property="codesetid" filter="true"/>';
			}else{
			}      
    } 
   function quash(){
   	   var len=document.posBusinessForm.elements.length;
       var uu;
       for (i=0;i<len;i++)
       {
           if (document.posBusinessForm.elements[i].type=="checkbox"&&document.posBusinessForm.elements[i].name!="box")
           {
              if(document.posBusinessForm.elements[i].checked==true)
              {
                uu="dd";
                break;
               }
           }
       }
       if(uu!="dd")
       {
          alert(NOTING_SELECT);
          return false;
       }
       for (i=0;i<len;i++)
       {
           if (document.posBusinessForm.elements[i].type=="checkbox"&&document.posBusinessForm.elements[i].name!="box")
           {
              if(document.posBusinessForm.elements[i].checked==true)
              {
              	var _name=document.posBusinessForm.elements[i].name;
              	var _index=_name.indexOf("[");
              	var _t=_name.substring(_index+1,_index+2);
                var startdate=document.getElementById(_t+"D").value;
                var told=(startdate).replace(/-/g, "/");
		   		var dnew=new Date();
		   		var dold=new Date(Date.parse(told+' 23:59:59'));
		   		if(dnew<=dold){
		   				alert("不能撤销有效日期起为当天的记录!");
		   				return false;
		   		}
               }
           }
       }
   	   if(confirm("确认要撤销吗?"))
      {
        posBusinessForm.action="/pos/posbusiness/searchposbusinesslist.do?b_quash=link";
        posBusinessForm.submit(); 
       }
   }
	function changeinvalid(v){
        posBusinessForm.action="/pos/posbusiness/searchposbusinesslist.do?b_quash=link&invalid="+v;
        posBusinessForm.submit(); 
	
	}
	
	function showDateSelectBox(srcobj)
   {
      if(document.getElementById("date_panel").style.display=='none'){
      date_desc=srcobj;      
      Element.show('date_panel');   
      for(var i=0;i<document.posBusinessForm.date_box.options.length;i++)
  	  {
  	  	document.posBusinessForm.date_box.options[i].selected=false;
  	  }
      var pos=getAbsPosition(srcobj);
	  with($('date_panel'))
	  {
	        style.position="absolute";
	        //alert(pos[0]+" "+pos[1]);
	        if(navigator.appName.indexOf("Microsoft")!= -1){
    			style.posLeft=pos[0];
				style.posTop=pos[1]+srcobj.offsetHeight;
				style.width=36+"px";
			}else{
				style.left=pos[0]+"px";
				style.top=pos[1]+srcobj.offsetHeight+"px";
				style.width=32+"px";
			}
			
      }            
     }else{
     	 Element.hide('date_panel');
     }
   }
function setSelectValue()
   {
       Element.hide('date_panel');  
       var len=document.posBusinessForm.elements.length;
       var uu;
       for (i=0;i<len;i++)
       {
           if (document.posBusinessForm.elements[i].type=="checkbox"&&document.posBusinessForm.elements[i].name!="box")
           {
              if(document.posBusinessForm.elements[i].checked==true)
              {
                uu="dd";
                break;
               }
           }
       }
       if(uu!="dd")
       {
          alert(NOTING_SELECT);
          return false;
       }		
  		for(var i=0;i<document.posBusinessForm.date_box.options.length;i++)
  		{
  			if(document.posBusinessForm.date_box.options[i].selected)
  			{
  				if(confirm(KHPLAN_INFO6+document.posBusinessForm.date_box.options[i].text+KHPLAN_INFO7))  									
  					changeinvalid(document.posBusinessForm.date_box.options[i].value); 			
  				else
  					break;
  			}
  		}
   }
   
   function add(){
   		posBusinessForm.action="/pos/posbusiness/searchposbusinesslist.do?b_add=link&encryptParam=<%=PubFunc.encrypt("a0000=")%>";
   		posBusinessForm.submit();
   }
   
function upItem(codeitemid,a0000){
	var hashvo=new ParameterSet();        
    hashvo.setValue("a_code", '${posBusinessForm.a_code}');
    hashvo.setValue("codeitemid", codeitemid);
    hashvo.setValue("a0000", a0000);
    hashvo.setValue("type", 'up');
    if(itemids.length>1)
    	hashvo.setValue("itemids",itemids);
    var request=new Request({method:'post',onSuccess:itemview,functionId:'18010000028'},hashvo);
}

function itemview(outparamters){
	var currnode=parent.frames['mil_menu'].Global.selectedItem;
		if(currnode==null)
			return;
		if(currnode.load){
					while(currnode.childNodes.length){
						//alert(currnode.childNodes[0].uid);
						currnode.childNodes[0].remove();
					}
					currnode.load=true;
					currnode.loadChildren();
					currnode.reload(1);
				}
	posBusinessForm.action="/pos/posbusiness/searchposbusinesslist.do?b_query=link";
	posBusinessForm.submit();
}
function downItem(codeitemid,a0000){
	var hashvo=new ParameterSet();             
    hashvo.setValue("a_code", '${posBusinessForm.a_code}');
    hashvo.setValue("codeitemid", codeitemid);
    hashvo.setValue("a0000", a0000);
    hashvo.setValue("type", 'down');
    if(itemids.length>1)
    	hashvo.setValue("itemids",itemids);
    var request=new Request({method:'post',onSuccess:itemview,functionId:'18010000028'},hashvo);
}

</script>

<hrms:themes></hrms:themes>

<html:form action="/pos/posbusiness/searchposbusinesslist">
<html:hidden name="posBusinessForm" property="isrefresh" value=""/>
<html:hidden name="posBusinessForm" property="validateflag"/>
<html:hidden name="posBusinessForm" property="fromflag"/>
<html:hidden name="posBusinessForm" property="object_type"/>
<html:hidden name="posBusinessForm" property="historyDate"/>
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr><td>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
             <td align="center" class="TableRow" nowrap width="30px">
                <input type="checkbox" name="box" id="checkAll" onclick="batch_select(this,'codeitemForm.select');" title='<bean:message key="button.all.select"/>'/>&nbsp;
             </td>
             <logic:equal value="PS_C_CODE" property="param" name="posBusinessForm">
             	<td align="center" class="TableRow" nowrap width="40px">
			  		<bean:message key="button.new.insert"/>            	
	             </td>
             </logic:equal>
             <td align="center" class="TableRow" nowrap>
                <bean:message key="orglist.reportunitlist.codename"/>&nbsp;
             </td>
             <td align="center" class="TableRow" nowrap>
                <bean:message key="orglist.reportunitlist.code"/>&nbsp;
             </td> 
             <logic:notEqual value="yes" name="posBusinessForm" property="islevel">
	             <td align="center" class="TableRow" nowrap>
	             	<logic:equal value="PS_CODE" property="param" name="posBusinessForm">
	                   		<bean:message key="conlumn.codeitemid.pscaption"/>&nbsp;
	                   </logic:equal>
	                   <logic:equal value="PS_C_CODE" property="param" name="posBusinessForm">
	                   		<bean:message key="conlumn.codeitemid.ps_psccaption"/>&nbsp;
	                   </logic:equal>
	                   <logic:notEqual value="PS_CODE" property="param" name="posBusinessForm">
	                  		<logic:notEqual value="PS_C_CODE" property="param" name="posBusinessForm">
	                  		转换代码&nbsp;
	                		</logic:notEqual>
	                	</logic:notEqual>
	             </td>
             </logic:notEqual>
             <logic:equal value="1" name="userView" property="version_flag">
             <logic:equal value="1" name="posBusinessForm" property="validateflag"><!-- 时间标识代码类 2009-10-10xuj -->
		   		<td align="center" class="TableRow" nowrap>
	                <bean:message key="conlumn.codeitemid.start_date"/>&nbsp;
	             </td>
	             <td align="center" class="TableRow" nowrap>
	                <bean:message key="conlumn.codeitemid.end_date"/>&nbsp;
	             </td>
       		</logic:equal>  
       		 <logic:notEqual value="1" name="posBusinessForm" property="validateflag"><!-- 非时间标识代码类 2009-10-10xuj -->
	   			<td align="center" class="TableRow" nowrap>
                	<bean:message key="column.warn.valid"/>&nbsp;
             	</td>
	   		</logic:notEqual> 
	   		</logic:equal>
	   		
	   		<logic:equal value="2" name="posBusinessForm" property="fromflag"> 
	   			<hrms:priv func_id="360201011,360202011,360203011">             
             		<td align="center" class="TableRow" nowrap width="40px">
		  				<bean:message key="label.edit"/>            	
             		</td>  
             	</hrms:priv>
            </logic:equal>
            <logic:equal value="3" name="posBusinessForm" property="fromflag"> 
                <hrms:priv func_id="323812">
             		<td align="center" class="TableRow" nowrap width="40px">
		  				<bean:message key="label.edit"/>            	
             		</td>  
             	</hrms:priv>
            </logic:equal>            
            <logic:equal value="5" name="posBusinessForm" property="fromflag"> 
                <hrms:priv func_id="3110803">
                    <td align="center" class="TableRow" nowrap width="40px">
                        <bean:message key="label.edit"/>                
                    </td>  
                </hrms:priv>
            </logic:equal>
            <logic:equal value="6" name="posBusinessForm" property="fromflag"> 
                <hrms:priv func_id="40002">
                    <td align="center" class="TableRow" nowrap width="40px">
                        <bean:message key="label.edit"/>                
                    </td>  
                </hrms:priv>
            </logic:equal>
	   		<logic:notEqual value="2" name="posBusinessForm" property="fromflag"> 
	   		<logic:notEqual value="3" name="posBusinessForm" property="fromflag"> 
	   		<logic:notEqual value="5" name="posBusinessForm" property="fromflag"> 
	   		<logic:notEqual value="6" name="posBusinessForm" property="fromflag">
	   			<hrms:priv func_id="3007207">             
             		<td align="center" class="TableRow" nowrap width="40px">
		  				<bean:message key="label.edit"/>            	
             		</td>  
             	</hrms:priv>
            </logic:notEqual>
            </logic:notEqual>
            </logic:notEqual>
             </logic:notEqual>
             
             <logic:equal value="PS_C_CODE" property="param" name="posBusinessForm">
             </logic:equal> 
             	<td align="center" class="TableRow" nowrap width="60px">
			  		<bean:message key="label.zp_exam.sort"/>            	
	             </td> 
                  
           </tr>
   	  </thead>
   	  <%
   	  		PosBusinessForm posBusinessForm = (PosBusinessForm)session.getAttribute("posBusinessForm");
			//int llen=posBusinessForm.getLlen();
   	  		int len = posBusinessForm.getCodeitemForm().getAllList().size();
            int curpage = posBusinessForm.getCodeitemForm().getPagination().getCurrent()-1;
			int pagerows = posBusinessForm.getPagerows();//每页显示数写死导致 排序箭头显示不对    wangb 20190316
			int remainder = len-(pagerows*curpage);
			/*if(remainder>pagerows){
				len=pagerows;
			}else{
				len=remainder;
			}*/
			len=remainder;
			String validateflag = posBusinessForm.getValidateflag();
   	   %>
          <hrms:extenditerate id="element" name="posBusinessForm" property="codeitemForm.list" indexes="indexes"  pagination="codeitemForm.pagination" pageCount="${posBusinessForm.pagerows }" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %>  
          <bean:define id="codeid" name="element" property="string(codesetid)"/>
          <bean:define id="itemid" name="element" property="string(codeitemid)"/>
          <bean:define id="itemdesc" name="element" property="string(codeitemdesc)"/>
          <bean:define id="flag" name="posBusinessForm" property="validateflag"/>
          <bean:define id="start_dates" name="element" property="string(start_date)"/>
          <bean:define id="end_dates" name="element" property="string(end_date)"/>
          <bean:define id="code" name="element" property="string(corcode)"/>
          <bean:define id="a0000" name="element" property="int(a0000)"/>
          <% String codesetids = SafeCode.encode(PubFunc.encrypt(codeid.toString()));
          String codeitemids = SafeCode.encode(PubFunc.encrypt(itemid.toString()));
          String a0000s = SafeCode.encode(PubFunc.encrypt(a0000.toString()));%>
            <td align="center" class="RecordRow" nowrap>
            <logic:notEqual value="0" name="element" property="string(b0110)">
               <hrms:checkmultibox name="posBusinessForm" property="codeitemForm.select" value="true" indexes="indexes" />&nbsp;
               <input type="hidden" name='<%=i+"H"%>' id='<%=i+"H"%>' value='<%=codesetids %>`<%=codeitemids %>'>
               <input type="hidden" name='<%=(i-1)+"D"%>' id='<%=(i-1)+"D"%>' value='<bean:write name="element" property="string(start_date)" filter="true"/>'>
            </logic:notEqual>
            </td>
            <logic:equal value="PS_C_CODE" property="param" name="posBusinessForm">
             	<td align="center" class="RecordRow" nowrap width="40px">
             	<%String encryptParam = PubFunc.encrypt("a0000="+a0000.toString());%>
			  		  <a href="/pos/posbusiness/searchposbusinesslist.do?b_add=link&encryptParam=<%=encryptParam %>"  target="mil_body"><img src="/images/goto_input.gif" border=0></a>
	             </td>
             </logic:equal>
            <td align="left" class="RecordRow" nowrap>                
                &nbsp;<bean:write  name="element" property="string(codeitemdesc)" filter="true"/>&nbsp;
            </td>
             <td align="left" class="RecordRow" nowrap> 
                &nbsp;<bean:write  name="element" property="string(codeitemid)" filter="true"/>&nbsp;
            </td>
            <logic:notEqual value="yes" name="posBusinessForm" property="islevel">
            	<td align="left" class="RecordRow" nowrap> 
	             &nbsp;<bean:write  name="element" property="string(corcode)" filter="true"/>&nbsp;
             	</td>
             </logic:notEqual>
             <logic:equal value="1" name="userView" property="version_flag">
            <logic:equal value="1" name="posBusinessForm" property="validateflag"><!-- 时间标识代码类 2009-10-10xuj -->
	   		<td align="left" class="RecordRow" nowrap>                
                &nbsp;<bean:write  name="element" property="string(start_date)" filter="true"/>&nbsp;
            </td>
            <td align="left" class="RecordRow" nowrap>                
                &nbsp;<bean:write  name="element" property="string(end_date)" filter="true"/>&nbsp;
            </td>
       		</logic:equal>
            <logic:notEqual value="1" name="posBusinessForm" property="validateflag"><!-- 非时间标识代码类 2009-10-10xuj -->
	   			<td align="left" class="RecordRow" nowrap>&nbsp;                
                	<logic:equal value="1" name="element" property="string(invalid)">
                		<bean:message key="lable.lawfile.availability" />
                	</logic:equal>
                	<logic:notEqual value="1" name="element" property="string(invalid)">
                		<bean:message key="lable.lawfile.invalidation" />
                	</logic:notEqual>
            	</td>
	   		</logic:notEqual>
	   		</logic:equal>
	   		
	   		
	   		<logic:equal value="2" name="posBusinessForm" property="fromflag"> 
	   			<hrms:priv func_id="360201011,360202011,360203011">
	   				<td align="center" class="RecordRow" nowrap>
	           			<logic:notEqual value="0" name="element" property="string(b0110)">
	           				<logic:equal value="1" name="posBusinessForm" property="validateflag"><!-- 时间标识代码类 2009-10-10xuj -->
	              				<a href="/pos/posbusiness/searchposbusinesslist.do?br_update=link&encryptParam=<%=PubFunc.encrypt("codesetid="+codeid.toString()+"&codeitemid="+itemid.toString()+"&codeitemdesc="+itemdesc.toString()+"&validateflag="+flag.toString()+"&start_date="+start_dates.toString()+"&end_date="+end_dates.toString()+"&corcode="+code.toString())%>"  target="mil_body"><img src="/images/edit.gif" border=0></a>
		  					</logic:equal>
		  					<logic:notEqual value="1" name="posBusinessForm" property="validateflag">
		          				<a href="/pos/posbusiness/searchposbusinesslist.do?br_update=link&encryptParam=<%=PubFunc.encrypt("codesetid="+codeid.toString()+"&codeitemid="+itemid.toString()+"&codeitemdesc="+itemdesc.toString()+"&corcode="+code.toString())%>"  target="mil_body"><img src="/images/edit.gif" border=0></a>
		  					</logic:notEqual>
		  				</logic:notEqual>
		   			</td>
	   			</hrms:priv>
	   		</logic:equal>
	   		<logic:equal value="3" name="posBusinessForm" property="fromflag"> 
	   		     <hrms:priv func_id="323812">
	   				<td align="center" class="RecordRow" nowrap>
	           			<logic:notEqual value="0" name="element" property="string(b0110)">
	           				<logic:equal value="1" name="posBusinessForm" property="validateflag"><!-- 时间标识代码类 2009-10-10xuj -->
	              				<a href="/pos/posbusiness/searchposbusinesslist.do?br_update=link&encryptParam=<%=PubFunc.encrypt("codesetid="+codeid.toString()+"&codeitemid="+itemid.toString()+"&codeitemdesc="+itemdesc.toString()+"&validateflag="+flag.toString()+"&start_date="+start_dates.toString()+"&end_date="+end_dates.toString()+"&corcode="+code.toString())%>"  target="mil_body"><img src="/images/edit.gif" border=0></a>
		  					</logic:equal>
		  					<logic:notEqual value="1" name="posBusinessForm" property="validateflag">
		          				<a href="/pos/posbusiness/searchposbusinesslist.do?br_update=link&encryptParam=<%=PubFunc.encrypt("codesetid="+codeid.toString()+"&codeitemid="+itemid.toString()+"&codeitemdesc="+itemdesc.toString()+"&corcode="+code.toString())%>"  target="mil_body"><img src="/images/edit.gif" border=0></a>
		  					</logic:notEqual>
		  				</logic:notEqual>
		   			</td>
		   		</hrms:priv>
	   		</logic:equal>
	   		<logic:equal value="5" name="posBusinessForm" property="fromflag"> 
                 <hrms:priv func_id="3110803">
                    <td align="center" class="RecordRow" nowrap>
                        <logic:notEqual value="0" name="element" property="string(b0110)">
                            <logic:equal value="1" name="posBusinessForm" property="validateflag"><!-- 时间标识代码类 2009-10-10xuj -->
                                <a href="/pos/posbusiness/searchposbusinesslist.do?br_update=link&encryptParam=<%=PubFunc.encrypt("codesetid="+codeid.toString()+"&codeitemid="+itemid.toString()+"&codeitemdesc="+itemdesc.toString()+"&validateflag="+flag.toString()+"&start_date="+start_dates.toString()+"&end_date="+end_dates.toString()+"&corcode="+code.toString())%>"  target="mil_body"><img src="/images/edit.gif" border=0></a>
                            </logic:equal>
                            <logic:notEqual value="1" name="posBusinessForm" property="validateflag">
                                <a href="/pos/posbusiness/searchposbusinesslist.do?br_update=link&encryptParam=<%=PubFunc.encrypt("codesetid="+codeid.toString()+"&codeitemid="+itemid.toString()+"&codeitemdesc="+itemdesc.toString()+"&corcode="+code.toString())%>"  target="mil_body"><img src="/images/edit.gif" border=0></a>
                            </logic:notEqual>
                        </logic:notEqual>
                    </td>
                </hrms:priv>
            </logic:equal>
            <logic:equal value="6" name="posBusinessForm" property="fromflag"> 
                 <hrms:priv func_id="40002">
                    <td align="center" class="RecordRow" nowrap>
                        <logic:notEqual value="0" name="element" property="string(b0110)">
                            <logic:equal value="1" name="posBusinessForm" property="validateflag"><!-- 时间标识代码类 2009-10-10xuj -->
                                <a href="/pos/posbusiness/searchposbusinesslist.do?br_update=link&encryptParam=<%=PubFunc.encrypt("codesetid="+codeid.toString()+"&codeitemid="+itemid.toString()+"&codeitemdesc="+itemdesc.toString()+"&validateflag="+flag.toString()+"&start_date="+start_dates.toString()+"&end_date="+end_dates.toString()+"&corcode="+code.toString())%>"  target="mil_body"><img src="/images/edit.gif" border=0></a>
                            </logic:equal>
                            <logic:notEqual value="1" name="posBusinessForm" property="validateflag">
                                <a href="/pos/posbusiness/searchposbusinesslist.do?br_update=link&encryptParam=<%=PubFunc.encrypt("codesetid="+codeid.toString()+"&codeitemid="+itemid.toString()+"&codeitemdesc="+itemdesc.toString()+"&corcode="+code.toString())%>"  target="mil_body"><img src="/images/edit.gif" border=0></a>
                            </logic:notEqual>
                        </logic:notEqual>
                    </td>
                </hrms:priv>
            </logic:equal>
	   		<logic:notEqual value="2" name="posBusinessForm" property="fromflag"> 	
	   		<logic:notEqual value="3" name="posBusinessForm" property="fromflag"> 	
	   		<logic:notEqual value="5" name="posBusinessForm" property="fromflag"> 
	   		<logic:notEqual value="6" name="posBusinessForm" property="fromflag"> 
		   		<hrms:priv func_id="3007207">
	           		<td align="center" class="RecordRow" nowrap>
	           			<logic:notEqual value="0" name="element" property="string(b0110)">
	           				<%
	           					RecordVo vo = (RecordVo)pageContext.getAttribute("element");
	           					String codesetid = vo.getString("codesetid");
	           					String codeitemid = vo.getString("codeitemid");
	           					String codeitemdesc = vo.getString("codeitemdesc");
	           					String start_date = vo.getString("start_date");
	           					String end_date = vo.getString("end_date");
	           					String corcode = vo.getString("corcode");
	           				 %>
	           				<logic:equal value="1" name="posBusinessForm" property="validateflag"><!-- 时间标识代码类 2009-10-10xuj -->
	              				<a href="/pos/posbusiness/searchposbusinesslist.do?br_update=link&encryptParam=<%=PubFunc.encrypt("codesetid="+codesetid+"&codeitemid="+codeitemid+"&codeitemdesc="+codeitemdesc+"&validateflag="+validateflag+"&start_date="+start_date+"&end_date="+end_date+"&corcode="+corcode) %>"  target="mil_body"><img src="/images/edit.gif" border=0></a>
		  					</logic:equal>
		  					<logic:notEqual value="1" name="posBusinessForm" property="validateflag">
		          				<a href="/pos/posbusiness/searchposbusinesslist.do?br_update=link&encryptParam=<%=PubFunc.encrypt("codesetid="+codesetid+"&codeitemid="+codeitemid+"&codeitemdesc="+codeitemdesc+"&corcode="+corcode)%>"  target="mil_body"><img src="/images/edit.gif" border=0></a>
		  					</logic:notEqual>
		  				</logic:notEqual>
		   			</td>
		   		</hrms:priv>
	   		</logic:notEqual>
	   		</logic:notEqual>
	   		</logic:notEqual>
	   		</logic:notEqual>
          
             	<td align="center" class="RecordRow" nowrap width="60px">
			  		<%if(i!=1||curpage!=0){ %>
					&nbsp;<a href="javaScript:upItem('<%=codeitemids %>','<%=a0000s %>');">
					<img src="/images/up01.gif" width="12" height="17" border=0></a> 
					<%}else{ %>
						<script type="text/javascript">
		           			if(isIE6()){
		           				document.write("&nbsp;&nbsp;&nbsp;&nbsp;");
		           			}else{
		           				document.write("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		           			}
		           	    </script>
					<%} %>
				    <%if(len==i){ %>
				    	<script type="text/javascript">
		           			if(isIE6()){
		           				document.write("&nbsp;&nbsp;&nbsp;&nbsp;");
		           			}else{
		           				document.write("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		           			}
		           	    </script>
				    <%}else{ %>
					&nbsp;<a href="javaScript:downItem('<%=codeitemids %>','<%=a0000s %>');">
					<img src="/images/down01.gif" width="12" height="17" border=0></a> 
					<%} %>  
					<logic:equal value="PS_C_CODE" property="param" name="posBusinessForm">
					   <script> itemids+='<bean:write name="element" property="string(codeitemid)" filter="true"/>,';</script>   
                    </logic:equal> 
					  	
	             </td>
                              	    		        	        	        
          </tr>
        </hrms:extenditerate>
         
</table>
</td></tr>
<tr><td>
<table  width="100%" height="35px;" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		    <!-- 系统管理，代码体系，分页标签支持设置每页条数  jingq upd 2014.12.06 -->
					<hrms:paginationtag name="posBusinessForm" pagerows="${posBusinessForm.pagerows}" property="codeitemForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="posBusinessForm" property="codeitemForm.pagination"
				nameId="codeitemForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
</td></tr></table>
<table  width="70%" align="center">
     <tr>
       <td id ="clo" align="center" nowrap="nowrap" height="35px;">
				<div id="date_panel" style="width:108px; height:38px; overflow: hidden; border: 1px solid #94B6E6" class="common_border_color">
					<select name="date_box" multiple="multiple" size="2"
						style="width:37;position:absolute; left:-2; top:-2; width:171px; height:50px; clip: rect(2 108 38 2);font-size:12px;" onclick="" onfocus="" onchange="setSelectValue();">
						<option value="1" class="c" style="">
							<bean:message key='kh.field.yx' />
						</option>
						<option value="0">
							<bean:message key='kh.field.wx' />
						</option>
					</select>
				</div>
	   			
	   		<logic:equal value="2" name="posBusinessForm" property="fromflag">
		         <input type=button class=mybutton value="<bean:message key="button.insert" />" onclick=add() />
		         <input type="button" name="addbutton"  value="<bean:message key="button.delete"/>" class="mybutton" onclick='deleterec()'>		          	
		    </logic:equal> 
		    <logic:notEqual value="2" name="posBusinessForm" property="fromflag">
		    <logic:notEqual value="3" name="posBusinessForm" property="fromflag"> 	
		    <logic:notEqual value="5" name="posBusinessForm" property="fromflag">   
		    <logic:notEqual value="6" name="posBusinessForm" property="fromflag"> 			            
	   			<hrms:priv func_id="3007206">
	   			<logic:equal name="posBusinessForm" property="cflag" value="1"><!-- 开发商模式 -->
	   				<input type=button class=mybutton value="<bean:message key="button.insert" />" onclick=add() />
	   			</logic:equal>
				<logic:notEqual name="posBusinessForm" property="cflag" value="1"><!-- 用户模式，非特殊代码类 -->
				   			<logic:notEqual name="posBusinessForm" property="valueflag" value="2">
				   				<input type=button class=mybutton value="<bean:message key="button.insert" />" onclick=add() />
				   			</logic:notEqual>
				</logic:notEqual>
				</hrms:priv>
				<hrms:priv func_id="3007208">
				<logic:equal name="posBusinessForm" property="cflag" value="1">
					<input type="button" name="addbutton"  value="<bean:message key="button.delete"/>" class="mybutton" onclick='deleterec()'>
	   			</logic:equal>
				<logic:notEqual name="posBusinessForm" property="cflag" value="1">
				   			<logic:notEqual name="posBusinessForm" property="valueflag" value="2">
				   				<input type="button" name="addbutton"  value="<bean:message key="button.delete"/>" class="mybutton" onclick='deleterec()'>
				   			</logic:notEqual>
				</logic:notEqual>
				</hrms:priv>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			
			<logic:equal value="3" name="posBusinessForm" property="fromflag">
				<hrms:priv func_id="323812">
		   			<logic:equal name="posBusinessForm" property="cflag" value="1"><!-- 开发商模式 -->
		   				<input type=button class=mybutton value="<bean:message key="button.insert" />" onclick=add() />
		   				<input type="button" name="addbutton"  value="<bean:message key="button.delete"/>" class="mybutton" onclick='deleterec()'>
		   			</logic:equal>
					<logic:notEqual name="posBusinessForm" property="cflag" value="1"><!-- 用户模式，非特殊代码类 -->
					   			<logic:notEqual name="posBusinessForm" property="valueflag" value="2">
					   				<input type=button class=mybutton value="<bean:message key="button.insert" />" onclick=add() />
					   				<input type="button" name="addbutton"  value="<bean:message key="button.delete"/>" class="mybutton" onclick='deleterec()'>
					   			</logic:notEqual>
					</logic:notEqual>
					
					<logic:equal value="1" name="userView" property="version_flag">
					<logic:equal value="1" name="posBusinessForm" property="validateflag"><!-- 时间标识代码类 2009-10-10xuj -->
				       		<input type="button" name="addbutton"  value="<bean:message key="button.abolish" />" class="mybutton" onclick="quash();"/>
				   </logic:equal>
				   <logic:notEqual value="1" name="posBusinessForm" property="validateflag"><!-- 非时间标识代码类 2009-10-10xuj -->
			   			<input type='button' id='setButton'
						value='<bean:message key='button.orgmapset'/>'
						onclick="showDateSelectBox(document.getElementById('setButton'));"
						onblur="" class="mybutton" />
		   			</logic:notEqual> 
	   			</logic:equal>
		        </hrms:priv>
			</logic:equal>
			
			<logic:notEqual value="3" name="posBusinessForm" property="fromflag">
			<logic:notEqual value="5" name="posBusinessForm" property="fromflag">
			<logic:notEqual value="6" name="posBusinessForm" property="fromflag">
				<hrms:priv func_id="3007209">
				<logic:equal value="1" name="userView" property="version_flag">
					<logic:equal value="1" name="posBusinessForm" property="validateflag"><!-- 时间标识代码类 2009-10-10xuj -->
				       		<input type="button" name="addbutton"  value="<bean:message key="button.abolish" />" class="mybutton" onclick="quash();"/>
				   </logic:equal>
				   <logic:notEqual value="1" name="posBusinessForm" property="validateflag"><!-- 非时间标识代码类 2009-10-10xuj -->
			   			<input type='button' id='setButton'
						value='<bean:message key='button.orgmapset'/>'
						onclick="showDateSelectBox(document.getElementById('setButton'));"
						onblur="" class="mybutton" />
		   			</logic:notEqual> 
	   			</logic:equal> 
	   			</hrms:priv>  		   			
	   		</logic:notEqual>
	   		</logic:notEqual>
	   		</logic:notEqual>
	   		
	   		<logic:equal value="5" name="posBusinessForm" property="fromflag">
	   		     <hrms:priv func_id="3110803">
                   <input type=button class=mybutton value="<bean:message key="button.insert" />" onclick=add() />
                   <input type="button" name="addbutton"  value="<bean:message key="button.delete"/>" class="mybutton" onclick='deleterec()'>
                   <logic:equal value="1" name="posBusinessForm" property="validateflag"><!-- 时间标识代码类 2009-10-10xuj -->
				       		<input type="button" name="addbutton"  value="<bean:message key="button.abolish" />" class="mybutton" onclick="quash();"/>
				   </logic:equal>
				   <logic:notEqual value="1" name="posBusinessForm" property="validateflag"><!-- 非时间标识代码类 2009-10-10xuj -->
			   			<input type='button' id='setButton'
						value='<bean:message key='button.orgmapset'/>'
						onclick="showDateSelectBox(document.getElementById('setButton'));"
						onblur="" class="mybutton" />
		   			</logic:notEqual>
                 </hrms:priv>                 
            </logic:equal> 
            
            <logic:equal value="6" name="posBusinessForm" property="fromflag">
	   		     <hrms:priv func_id="40002">
                   <input type=button class=mybutton value="<bean:message key="button.insert" />" onclick=add() />
                   <input type="button" name="addbutton"  value="<bean:message key="button.delete"/>" class="mybutton" onclick='deleterec()'>
                   <logic:equal value="1" name="posBusinessForm" property="validateflag"><!-- 时间标识代码类 2009-10-10xuj -->
				       		<input type="button" name="addbutton"  value="<bean:message key="button.abolish" />" class="mybutton" onclick="quash();"/>
				   </logic:equal>
				   <logic:notEqual value="1" name="posBusinessForm" property="validateflag"><!-- 非时间标识代码类 2009-10-10xuj -->
			   			<input type='button' id='setButton'
						value='<bean:message key='button.orgmapset'/>'
						onclick="showDateSelectBox(document.getElementById('setButton'));"
						onblur="" class="mybutton" />
		   			</logic:notEqual>
                 </hrms:priv>                 
            </logic:equal> 
            
        <!-- hrms:tipwizardbutton flag="org" target="il_body" formname="posBusinessForm"/--> 
        <logic:equal name="posBusinessForm" property="codeitem" value="index">
        	<!--<input type="button" name="addbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="history.back();">-->
        </logic:equal>
        <logic:notEqual name="posBusinessForm" property="codeitem" value="index">
        	<!--<input type="button" name="addbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick='toretrun()'>-->
        </logic:notEqual>
		 <logic:equal name="posBusinessForm" property="returnvalue" value="dxt">
        	<input type="button" name="addbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="hrbreturn('org','2','posBusinessForm');">
        </logic:equal>
        <logic:equal value="2" name="posBusinessForm" property="fromflag">
          <input type="button" name="rbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick='rePM("${posBusinessForm.object_type}","${posBusinessForm.historyDate}");'>
        </logic:equal>
        </td>	  
      </tr>
</table>
</html:form>

<script>
Element.hide('date_panel');	
	  var srcobj = 	$('setButton');
	  var pos=getAbsPosition(srcobj);
	  with($('date_panel'))
	  {
	        style.position="absolute";
    		if(navigator.appName.indexOf("Microsoft")!= -1){
    			style.posLeft=pos[0];
				style.posTop=pos[1]+srcobj.offsetHeight;
			}else{
				style.left=pos[0]+"px";
				style.top=pos[1]+srcobj.offsetHeight+"px";
			}
			style.width=37;
      }
      
if(window.dialogArguments)
{
	var in_obj=document.getElementById("clo"); 
	var strhtml = "<input type=\"button\" name=\"clobutton\"  value=\"<bean:message key='button.close'/>\" class=\"mybutton\" onclick='closel()'> ";
	in_obj.innerHTML=in_obj.innerHTML+strhtml;
	
}
var value_s="";
<%
	PosBusinessForm pbf = (PosBusinessForm)session.getAttribute("posBusinessForm");
	ArrayList codelist = pbf.getCodelist();
	if(codelist!=null){
		for(int y=0;y<codelist.size();y++){
%>
	value_s="<%=codelist.get(y)%>";
	deleter('<bean:write name="posBusinessForm" property="isrefresh" filter="true"/>',value_s);
<%
		}
		codelist.clear();
	}
%>
//if(!window.dialogArguments)
//{
	document.getElementsByName("isrefresh")[0].value="";
	//注释掉，在ie11下报错   wangb 20190323
	//save('<bean:write name="posBusinessForm" property="isrefresh" filter="true"/>','<bean:write name="posBusinessForm" property="a_code" filter="true"/>','<bean:write name="posBusinessForm" property="codesetid" filter="true"/>','<bean:write name="posBusinessForm" property="codeitemid" filter="true"/>','<bean:write name="posBusinessForm" property="codeitemdesc" filter="true"/>');	
	update('<bean:write name="posBusinessForm" property="isrefresh" filter="true"/>','<bean:write name="posBusinessForm" property="codesetid" filter="true"/>','<bean:write name="posBusinessForm" property="codeitemid" filter="true"/>','<bean:write name="posBusinessForm" property="codeitemdesc" filter="true"/>');
//}
function rePM(object_type,historyDate)
{
    parent.location="/competencymodal/postseq_commodal/post_modal_tree.do?b_tree=tree&object_type="+object_type+"&historyDate="+historyDate;
}	   
</script>
