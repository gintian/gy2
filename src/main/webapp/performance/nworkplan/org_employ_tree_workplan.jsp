<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hrms.frame.codec.SafeCode"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
	String showDb="0";
	if(request.getParameter("showDb")!=null)
		showDb=request.getParameter("showDb");
		
	String showSelfNode="0";
	if(request.getParameter("showSelfNode")!=null)
		showSelfNode=request.getParameter("showSelfNode");
		String level ="-1";
			if(request.getParameter("level")!=null)
		level=request.getParameter("level");	//该参数可以控制单选按钮，比如所属单位  xgq20101108
		String viewunit = "";
			if(request.getParameter("viewunit")!=null&&("0".equals(request.getParameter("viewunit"))||"1".equals(request.getParameter("viewunit"))))
		viewunit=request.getParameter("viewunit");	//该参数可以控制按操作单位或管理范围进行展现，1为操作单位，0为管理范围  xgq20101108
    String flag="0";
    if(request.getParameter("flag")!=null)
        flag=request.getParameter("flag");
    String selecttype="0";
    if(request.getParameter("selecttype")!=null)
    {
        selecttype=request.getParameter("selecttype");
    }
    String dbpre="usr";
     if(request.getParameter("dbpre")!=null)
    {
        dbpre=request.getParameter("dbpre");
    }
    String priv="1";
    if(request.getParameter("priv")!=null)
    {
        priv=request.getParameter("priv");
    }
    
    String tabid="-1";
    if(request.getParameter("tabid")!=null)  //人事异动模板id
    {
        tabid=request.getParameter("tabid");
    }
    
    String isfilter="0";
    if(request.getParameter("isfilter")!=null)
    {
        isfilter=request.getParameter("isfilter");
    }
    String SYS_FILTER_FACTOR="";
    if(isfilter.equals("1"))
    	SYS_FILTER_FACTOR=SafeCode.encode((String)request.getSession().getAttribute("SYS_FILTER_FACTOR"));
    String nmodule = "";
    if(request.getParameter("nmodule")!=null)
    {
    	nmodule=request.getParameter("nmodule");
    }
    
    String dbtype="0";
     if(request.getParameter("dbtype")!=null)
    {
        dbtype=request.getParameter("dbtype");
    }
    String checklevel = "3";
    if(request.getParameter("checklevel") != null) {
        checklevel = request.getParameter("checklevel");
    }
    //为人事异动姓名查询列增加提示信息
    String generalmessage = "";
     if(request.getParameter("generalmessage") != null) {
        generalmessage = request.getParameter("generalmessage");
        generalmessage = SafeCode.decode(generalmessage);
    }
%>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<hrms:themes />
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<script type="text/javascript">
<!--
	var selecttype='<%=selecttype%>';
	
	function getemploy()
	{
	/*
     var currnode=Global.selectedItem; 
     var iconurl=currnode.icon;
     if(iconurl!="/images/man.gif") 
       return;    
	 window.returnValue=currnode.uid;
	 */
	 var thevo=new Object();
	 if(trim(root.getSelected()).length==0)
	 {
	     var obj=document.getElementById("date_box");
	     var cont="";
	     var tit="";
	     var num=0;
	     if(obj)
	     {
	        for(var i=0;i<obj.options.length;i++)
	        {
	           if(obj.options[i].selected)
	           {
	               num++;
	               cont+=obj.options[i].value+",";
	              var temp=obj.options[i].text;
	             if(temp.indexOf("/")==-1)
	             {
	                if(temp.indexOf("(")==-1)
	                    tit+=temp+",";
	                else
	                   tit+=temp.substring(0,temp.indexOf("("))+",";
	             }
	             else
	             {
	                 var arr =temp.split("/");
	                temp=arr[arr.length-1];
	                 if(temp.indexOf("(")==-1)
	                    tit+=temp+",";
	                else
	                   tit+=temp.substring(0,temp.indexOf("("))+",";
	             }
	           }
	        }
	     }
	     if(selecttype=='2'&&cont.length>0)
	     {
	     	cont=cont.substring(0,cont.length-1);
	    	tit=tit.substring(0,tit.length-1);
	     }
	    
	     thevo.content=cont;
	     thevo.title=tit;
	    
	 }
	 else
	 {
	     thevo.content=root.getSelected();
	     thevo.title=root.getSelectedTitle();
	 }
	 window.returnValue=thevo;
     window.close();

	}
	function okSelect()
	{
	     var thevo=new Object();
	
	     var obj=document.getElementById("date_box");
	     var cont="";
	     var tit="";
	     var num=0;
	     if(obj)
	     {
	       for(var i=0;i<obj.options.length;i++)
	       {
	          if(obj.options[i].selected)
	          {
	             num++;
	             cont+=obj.options[i].value+",";
	             var temp=obj.options[i].text;
	             if(temp.indexOf("/")==-1)
	             {
	                if(temp.indexOf("(")==-1)
	                    tit+=temp+",";
	                else
	                   tit+=temp.substring(0,temp.indexOf("("))+",";
	             }
	             else
	             {
	                 var arr =temp.split("/");
	                temp=arr[arr.length-1];
	                 if(temp.indexOf("(")==-1)
	                    tit+=temp+",";
	                else
	                   tit+=temp.substring(0,temp.indexOf("("))+",";
	             }
	        }
	    }
	 }
	    
	  if(selecttype=='2'&&cont.length>0)
	  {
	     	cont=cont.substring(0,cont.length-1);
	    	tit=tit.substring(0,tit.length-1);
	  }  
	    
	 thevo.content=cont;
	 thevo.title=tit;
	 window.returnValue=thevo;
     window.close();
	}
	Global.defaultradiolevel = 3;//choice user
	var lever = <%=level%>
	if(lever!="-1"){
	Global.defaultradiolevel = lever;
	}
	function showDateSelectBox(srcobj)
   {
   		if($F('selectname')=="")
   		{
   			Element.hide('date_panel');
   			return false ;
   		}
      date_desc=document.getElementById(srcobj);
      Element.show('date_panel');
      var pos=getAbsPosition(date_desc);
	  with($('date_panel'))
	  {
        style.position="absolute";
		style.posLeft=pos[0];
		style.posTop=pos[1]-date_desc.offsetHeight+42;
		style.width=(date_desc.offsetWidth<20)?150:date_desc.offsetWidth+1;
      }
      var hashVo = new ParameterSet();
      hashVo.setValue("name",getEncodeStr(document.logonUserForm.a_name.value));
	  hashVo.setValue("dbpre","<%=dbpre%>");
	  hashVo.setValue("showDb","<%=showDb%>");
	  hashVo.setValue("priv","<%=priv%>");
	  hashVo.setValue("showSelfNode","<%=showSelfNode%>");
	  hashVo.setValue("dbtype","<%=dbtype%>");
	  hashVo.setValue("viewunit","<%=viewunit%>");
	  hashVo.setValue("nmodule","<%=nmodule%>");
	  hashVo.setValue("isfilter","<%=isfilter%>");
	  hashVo.setValue("tabid","<%=tabid%>");
	  hashVo.setValue("SYS_FILTER_FACTOR","<%=SYS_FILTER_FACTOR%>");
	  hashVo.setValue("flag","4");
      var request=new Request({method:'post',onSuccess:shownamelist,functionId:'3020071012'},hashVo);
   }
   function shownamelist(outparamters)
   {
   		var namelist=outparamters.getValue("namelist");
		if(namelist.length==0){
			Element.hide('date_panel');
		}
		else{
			AjaxBind.bind(logonUserForm.contenttype,namelist);
		}
   }
//-->
</script>
   <style type="text/css">
	#treemenu {  
	height: 300px;overflow: 
	auto;border-style:inset ;
	border-width:2px
	}
   </style>
<html:form action="/system/logonuser/org_employ_tree"> 
   <table width="100%" border="0" cellspacing="1"  align="center" >   
	 <tr align="left">
		<td valign="top" nowrap>
		<%if(!flag.equals("0")&&!selecttype.equals("0")){ %>
		<bean:message key="columns.archive.name"/>:&nbsp;<Input type='text' name='a_name'  size='35' id="selectname" onkeyup="showDateSelectBox('selectname')"  title='<%=generalmessage %>'/>
		<%} %>
		</td>
	 </tr>          
         <tr>
           <td align="left"> 
                 <hrms:orgtree flag="1" loadtype="1" showroot="false" selecttype="1" isShowSelfDepts="1" viewunit="1" divStyle="height:320px;width:290px;overflow-x:auto;overflow-y:auto;" />			           
           </td>
         </tr>   
         <tr>
            <td align="center" colspan="2">
         	<html:button styleClass="mybutton" property="b_save" onclick="getemploy();">
            		<bean:message key="button.ok"/>
	 	    </html:button>
         	<html:button styleClass="mybutton" property="br_return" onclick="window.close();">
            		<bean:message key="button.close"/>
	 	    </html:button>            
            </td>         
         </tr>        
   </table>
   <div id="date_panel" style="display:none;">
     <%if(selecttype.equals("2")){ %>
		<select id="date_box" name="contenttype"  onblur="Element.hide('date_panel');" style="width:254" size="6" ondblclick="okSelect();">
        </select>
        <%}else{ %>
        <select id="date_box" name="contenttype"  onblur="Element.hide('date_panel');"  multiple="multiple"  style="width:254" size="6" ondblclick="okSelect();">
        </select>
        <%} %>
	 </div>
</html:form>
