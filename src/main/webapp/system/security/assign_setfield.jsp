<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
	    <link rel="stylesheet" type="text/css" href="../../ext/resources/css/ext-all.css" />

	    <link rel="stylesheet" type="text/css" href="../../ext/resources/css/slate.css" />
	   <script type="text/javascript" src="../../ext/ext-all.js"></script>
	   <!-- <script type="text/javascript" src="../../ext/ext6/ext-additional.js"></script> -->
	    <script type="text/javascript" src="../../ext/rpc_command.js"></script>
	    <script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
	    <hrms:themes></hrms:themes>
<script type="text/javascript">
      var content="<div id='mycontent'></div>";
      /***全无,全写，全读0:无,=1全读,=2全写**/
      function allset(flag)
      {
      	var tablevos,tmp;
      	tablevos=document.getElementsByTagName("INPUT");
      	for(var i=0;i<tablevos.length;i++)
      	{
      		if(tablevos[i].type=="radio")
      		{
      		  tmp=tablevos[i].value;
      		  if(tmp==flag)
      		  {
      		    tablevos[i].checked=true;
      		  }
      		}
      	}      	
      }
 	  /*组装子集及指标字符串*/
      function combinePrivString()
      {
      	var tablevos,thecontent,tmp,tablename,tabname;
      	thecontent="";
      	tablevos=document.getElementsByTagName("INPUT");
      	for(var i=0;i<tablevos.length;i++)
      	{
      		if(tablevos[i].type=="radio")
      		{
      		  tmp=tablevos[i].value;
      		  tablename=tablevos[i].name;
      		  if(tmp=="0")
      		    continue;
      		  if(!tablevos[i].checked)  
      		    continue;
      		  tmp=tablename+tmp+",";
      		  thecontent=thecontent+tmp;
      		}
      	}
      	thecontent=","+thecontent;
      	return thecontent;
      }     
      /*
      *树形菜单控制,功能菜单
      */
      function show(div_id)
      {
      	var oDiv;
      	oDiv=document.getElementById(div_id);
      	if(oDiv==null)
      	  return;
      	for(var i=0;i<oDiv.childNodes.length;i++)
      	{
      		if(oDiv.childNodes[i].tagName=='DIV')
      		{
      		   if(oDiv.childNodes[i].style.display=="none")
      		     oDiv.childNodes[i].style.display="block";
      		   else
      		     oDiv.childNodes[i].style.display="none";	  
      		}
      	}		
      }    
	function saveok(response)
	{
		var value=response.responseText;
		var map=Ext.decode(value);
		if(map.succeed)
		{
			alert(map.message);
		}
		
	}

    function save(selstr)
    {
          var map = new HashMap();
    	  map.put("user_flag", "<bean:write name="privForm" property="user_flag"/>");
    	  map.put("role_id","<bean:write name="privForm" property="role_id"/>");
    	  map.put("tab_name","<bean:write name="privForm" property="current_tab"/>");
    	  map.put("privcode","${privForm.privcode}");
    	  map.put("ishaveprivcode","${privForm.ishaveprivcode}");
    	  map.put("selstr", selstr);
   　       Rpc({functionId:'1010010048',success:saveok},map);        
    }
   	function onItemClick(item)
   	{
   	    var textvalue="";
   	    if(item==0){
            textvalue='<bean:message key="button.all.clear"/>';
        }else if(item==1){
            textvalue='<bean:message key="button.all.read"/>';
        }else if(item==2){
            textvalue='<bean:message key="button.all.write"/>';
        }
        if(textvalue==""||confirm("是否设置为'"+textvalue+"'？")){
                if(item<3)
                    allset(item);

                <logic:notEqual name="privForm" property="current_tab" value="busipriv">
                var privstr=combinePrivString();
                </logic:notEqual>
                <logic:equal name="privForm" property="current_tab" value="busipriv">
                var privstr='1,'+document.getElementById('busi_org_dept1').value+'|2,'+document.getElementById('busi_org_dept2').value+'|3,'+document.getElementById('busi_org_dept3').value+'|4,'+document.getElementById('busi_org_dept4').value;
                privstr+='|5,'+document.getElementById('busi_org_dept5').value+'|6,'+document.getElementById('busi_org_dept6').value+'|7,'+document.getElementById('busi_org_dept7').value+'|8,'+document.getElementById('busi_org_dept8').value;
                //职称评审业务权限设置 jingq add 2015.10.26
                privstr+='|9,'+document.getElementById('busi_org_dept9').value;
                //证照管理业务权限设置 jingq add 2018.06.25
                privstr+='|10,'+document.getElementById('busi_org_dept10').value;
                //考勤管理业务权限设置  haosl add 2018.10.11
                privstr+='|11,'+document.getElementById('busi_org_dept11').value;
                </logic:equal>
                save(privstr);
        }

   	}
    function onItemChange(obj)
    {
        //【61626】ZCSB：系统管理/权限管理/用户管理/授权子集、指标时，切换人员分类报400，见附件
        <logic:equal name="privForm" property="current_tab" value="tablepriv">
        privForm.action="/system/security/assign_setfield.do?b_query=link&current_tab=tablepriv&a_flag="+ $URL.encode("${privForm.user_flag}&role_id=${privForm.role_id}&privcode="+obj.value+"&ishaveprivcode=1");
        privForm.submit();
        </logic:equal>
        <logic:equal name="privForm" property="current_tab" value="fieldpriv">
        privForm.action="/system/security/assign_setfield.do?b_query=link&current_tab=fieldpriv&a_flag"+ $URL.encode("=${privForm.user_flag}&role_id=${privForm.role_id}&privcode="+obj.value+"&ishaveprivcode=1");
        privForm.submit();
        </logic:equal>
    }			     
	Ext.onReady(function(){
	        var p = new Ext.Panel({
	        //title: '<span style=\"font-size: 12px;font-weight: bold;color:black;\"><bean:write name="privForm" property="tabtitle"/></span>',
	        renderTo: 'container',
            autoScroll:true, 
            border:false,
		    tbar: [{
		    	xtype:'component',
		    	html:'<button class="mybutton" type="button" onclick="onItemClick(3)"><bean:message key="button.save"/></button>'+
		    	<logic:notEqual name="privForm" property="current_tab" value="busipriv">
		    	'<button class="mybutton" type="button" onclick="onItemClick(0)"><bean:message key="button.all.clear"/></button>'+
		    	'<button class="mybutton" type="button" onclick="onItemClick(1)"><bean:message key="button.all.read"/></button>'+
		    	'<button class="mybutton" type="button" onclick="onItemClick(2)"><bean:message key="button.all.write"/></button>'+
		    	</logic:notEqual>
		    	''
		    }/*,{
		            id:'save',
		            text: '<font><span style=\"font-size:12px;font-family=微软雅黑;\"><bean:message key="button.save"/></span></font>',
		            handler:onItemClick,
		            cls:'mybutton',
		            style:'border-radius:0px;',
		            scope: this}
           			<logic:notEqual name="privForm" property="current_tab" value="busipriv">
					,{
		            id:'clear',
		            text: '<font><span style=\"font-size:12px;font-family=微软雅黑;\"><bean:message key="button.all.clear"/></span></font>',
		            handler:onItemClick,
		            cls:'mybutton',	
		            style:'border-radius:0px;',
		            scope: this},
					{
		            id:'write',
		            text: '<font><span style=\"font-size:12px;font-family=微软雅黑;\"><bean:message key="button.all.read"/></span></font>',
		            handler:onItemClick,
		            cls:'mybutton',	
		            style:'border-radius:0px;',
		            scope: this},
					{
		            id:'read',
		            text: '<font><span style=\"font-size:12px;font-family=微软雅黑;\"><bean:message key="button.all.write"/></span></font>',
		            handler:onItemClick,
		            cls:'mybutton',
		            style:'border-radius:0px;',
		            scope: this} 
		            </logic:notEqual>    
		            */
		         ]
	    });
	    Ext.get('mycontent1').update("${privForm.script_str}",false);
	});    

	function pegging(name,id){
		//alert(name+"  "+id);
		var flag = 'set';
		if(id.length==5){
			flag='item';
		}
		var theurl="/system/security/pegging.do?b_query=link`name="+name+"`id="+id+"`flag="+flag;
		var return_vo;
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
		/*弹框浏览器兼容 guodd 2019-03-23*/
		if(window.showModalDialog){
	    	return_vo= window.showModalDialog(iframe_url,1, 
	        	  "dialogWidth:660px; dialogHeight:520px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
		}else{
			var dw=660,dh=530,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
			window.open(iframe_url,"_blank",'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top='+dt+',left='+dl+',width='+dw+',height='+dh);
		}
		
	}
	
	function getorg(param){
	
	var newparam = "<bean:write name="privForm" property="user_flag"/>,<bean:write name="privForm" property="role_id"/>,"+param;
    var theurl="/system/logonuser/org_employ_tree.do?b_querycheckvalue=link`flag=0`selecttype=1`dbtype=0"+
               "`priv=1`isfilter=0`loadtype=1`param="+newparam+"`prompt=1";
    theurl = $URL.encode(theurl);
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;   
    var dw=300,dh=400,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
    
		if(window.showModalDialog){
			//var ret_vo=select_org_emp_dialog_busi(0,1,0,1,0,1,"<bean:write name="privForm" property="user_flag"/>,<bean:write name="privForm" property="role_id"/>,"+param,1);
			var ret_vo = window.showModalDialog(iframe_url,"1","dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
			if(ret_vo)
			{
				var	re=/,/g;
				var tmp=ret_vo.content;
				//alert(tmp+"  "+ret_vo.title);
				var str=tmp.replace(re,"`");
				/*
				if(ret_vo.bestrow==true){
					document.getElementById('busi_org_dept'+param+'v').innerHTML=ret_vo.title;
				}else{
					document.getElementById('busi_org_dept'+param+'v').innerHTML=deleteRepeat(document.getElementById('busi_org_dept'+param+'v').innerHTML,ret_vo.title);
				}*/
				//机构树弹窗已处理了值了 这里直接赋值页面显示    wangb 20180420 bug 36668
				document.getElementById('busi_org_dept'+param+'v').innerHTML=ret_vo.title;
				document.getElementById('busi_org_dept'+param).value=str;
			}
		}else{
			     window.open(iframe_url,'','width='+dw+',height='+dh+',left='+dl+',top='+dt+',resizable=no,titlebar=yes,location=no,status=no,scrollbars=no');
		    	 window.type='orgEmp';
		    	 window.openEmpHistoryReturn = function(ret_vo){
		    		 if(ret_vo)
		 			{
		 				var	re=/,/g;
		 				var tmp=ret_vo.content;
		 				var str=tmp.replace(re,"`");
		 				document.getElementById('busi_org_dept'+param+'v').innerHTML=ret_vo.title;
		 				document.getElementById('busi_org_dept'+param).value=str;
		 			}
		    		 
		    	 };
			
		}
	}
	//去重复 直接用逗号分隔
	function deleteRepeat(value2,value1){
		value2 = value2.replace(/\s+/g,""); //去空格
		value2 = value2.replace(",,",",");//去掉空的
		if(value2.length ==0){
			return value1;
		}
		var strs=value1.split(","); //字符分割 
		for (var i=0;i<strs.length ;i++ ) 
		{ 
			var strValue = strs[i].replace(/\s+/g,"");//去空格
			if(strValue.length!=0){
				if(value2.indexOf(strValue)<0){
					value2+=","+strValue;
				} 
			}
		} 
		value2 = value2.replace(",,",",");//去掉空的
		return value2;
	}
</script>
<html:form action="/system/security/assign_setfield" style="margin:7px 0 0 0;">
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" >
         <tr>
           <td align="left" style="padding-left:5px;padding-top:5px;"> 
            <div id="container" style="border:1px solid;border-bottom:none;" class="common_border_color">
            </div> 
            <div id='mycontent1' style="margin-top:0px;"></div>
           </td>          
           </tr>           
    </table>  
</html:form>
