<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%@ page import="com.hrms.struts.valueobject.UserView" %>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>

<%
   String hidetopbar = request.getParameter("from");
   if (hidetopbar == null)
       hidetopbar = "0";
%>
<html>
    <%@ page contentType="text/html; charset=UTF-8"%>
    <%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>   
    <%@ taglib uri="/tags/struts-html" prefix="html" %>
<head>
    <title>网络学院门户</title>

    <script type="text/javascript" src="/templates/index/jquery.menutool-1.0.js"></script>
            
    <link rel="stylesheet" type="text/css" href="/css/login7.css">         
    <style>
        .tabs-header-plain{
            border:0px;
            background:#0066CC url(/images/ilearning/menutool_bg.gif) repeat-x left top;
        }   
    
        .tabs-tool{
            position:absolute;
            border:0;
            background-color:transparent;
            overflow:hidden;
        }
        
        .self_info{
            Height:28px;
            width:100%;
            background:#dfe8f6;
            border:1px;
            padding:0;
            margin:0;
		        font: normal 11px tahoma,arial,helvetica,sans-serif;
		        padding-top:6px;
        }
        
        #left{
            float:left; 
            height: 90%;
            width:20%; 
            border-style:solid;
            border-width:1px;
            border-color:#99bbe8;
        }
           
        #hj-ui-top {
		 top:0;
		 left:0; 
		 height:100px;
		 background:#0066CC url(/images/ilearning/header_Bg.gif) repeat-x left top;
		 width: 100%; 
		} 
		
		.icon-expand{
		    background: url(/jquery/themes/default/images/layout_button_down.gif) no-repeat;
		}
		
		.icon-unexpand{
            background: url(/jquery/themes/default/images/layout_button_up.gif) no-repeat;
        }

    </style>
    <script type="text/javascript">
    
    function logout()
    {
      if(confirm("确定要注销吗？"))
      {
          var url = "/templates/index/ilearning.jsp";
          url="/servler/sys/logout?flag=55";
          newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
          //window.opener=null;//不会出现提示信息
          //parent.window.close();    
      }
    }  
    
    function isclose()
    {
      if(confirm("确定要退出吗？"))
      {
          var url = "/templates/close.jsp";
          newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
      }
    }
    /*
     收缩top区域
    */
    function expand()
    {
        var height=$(document.body).height();
        var nh;
        if($('#hj-ui-top').is(':hidden'))
        {
            $('#hj-ui-top').show();
            nh=159;
            $("#expandBtn").linkbutton({
            	iconCls: 'icon-unexpand'
            });
        }
        else
        {
            $('#hj-ui-top').hide();
            nh=59;
            $("#expandBtn").linkbutton({
                iconCls: 'icon-expand'
            });
        }
        $("#ifrm").attr("height",height-nh);
    }
    
    function resize()
    {
        var height=$(document.body).height();
        var nh;
        if($('#hj-ui-top').is(':hidden'))
        {
            nh=159;
        }
        else
        {
            nh=59;
        }
        $("#ifrm").attr("height",height-nh); 
        
        //第一次进入并且不是最大化 重新加载页面
        if(firstin && !fullScreen){
        	 getSelectedTab();
             initPage();
             $('#maintabs').tabs('select', count); 
        }
        firstin = false;
    }
    
    //是否第一次进入页面
    var firstin=false;
    //窗口是否最大化
    var fullScreen=false;
    $(document).ready(function(){ 
    	initPage(); 
    	
    	var clientWidth = document.body.clientWidth;
    	var availWidth  = window.screen.availWidth;
    	if((availWidth-clientWidth)<10)
    		fullScreen=true;
    	firstin = true;
        
    });
    
    $(window).resize(function() {
        resize();
    });
    
    //记录被选中的选项卡
    var count;
    function getSelectedTab(){
    	var tab = $('#maintabs').tabs('getSelected');
    		
    	var mains = $('div.panel');
    	for(var i=0;i<mains.length;i++){
    		var div = mains[i].firstChild;
    		if(div.id == tab.panel('options').id){
    			count=i;
    			break;
    		}
    	}
    	
    }
    
    function initPage(){
    	<hrms:extmenu moduleid="552" menutype="jquery" container="minimenuobj"/>            
        /**顶部小工具条*/
        $('#min-menutools').addMenuTool(minimenuobj);
        
        /**主菜单*/
        <hrms:extmenu moduleid="551" menutype="jquery" container="mainmenuobj"/>            
        $('#maintabs').addTab(mainmenuobj);
       
        /**tabs工具条*/
        <hrms:extmenu moduleid="553" menutype="jquery" container="tabmenuobj"/>
        $('#tab-tools').addMenuTool(tabmenuobj);
        var expandmenu=[{text:'',id:'expandBtn',iconCls:'icon-unexpand',herf:'#'}];
        $('#tab-tools').addMenuTool(expandmenu);
        $('#expandBtn').bind('click', expand);
        $('#maintabs').tabs({tools:'#tab-tools'});
        
        
    }
    
    //添加新选项卡
    function tabs(flag){
    	var m = $('ul.tabs').children();
    	var n=m.length;
        if(flag == 1){
	    	var tab=[{id:'6',text:'正学必修课程',href:'/train/resource/mylessons.do?b_query=link&amp;opt=ing&flag=1',target:'il_body',closable:true}];
	    	var title = tab[0].text;
	    	if ($('#maintabs').tabs('exists', title))
			{ 
				$('#maintabs').tabs('select', title); 
			} 
			else 
			{ 
				$('#maintabs').addTab(tab);
				$("#maintabs").tabs( "select" , n );
				}
        }else if(flag == 2){
        	var tab=[{id:'7',text:'正学选修课程',href:'/train/resource/mylessons.do?b_query=link&amp;opt=ing&flag=2',target:'il_body',closable:true}];
        	var title = tab[0].text;
	    	if ($('#maintabs').tabs('exists', title))
			{ 
				$('#maintabs').tabs('select', title); 
			} 
			else 
			{ 
    		$('#maintabs').addTab(tab);
    		$("#maintabs").tabs( "select" , n );
			}
        }else if(flag == 3){
        	var tab=[{id:'8',text:'已学必修课程',href:'/train/resource/mylessons.do?b_query=link&amp;opt=ed&flag=3',target:'il_body',closable:true}];
        	var title = tab[0].text;
	    	if ($('#maintabs').tabs('exists', title))
			{ 
				$('#maintabs').tabs('select', title); 
			} 
			else 
			{ 
    		$('#maintabs').addTab(tab);
    		$("#maintabs").tabs( "select" , n );
			}
        }else if(flag == 4){
        	var tab=[{id:'9',text:'已学选修课程',href:'/train/resource/mylessons.do?b_query=link&amp;opt=ed&flag=4',target:'il_body',closable:true}];
        	var title = tab[0].text;
	    	if ($('#maintabs').tabs('exists', title))
			{ 
				$('#maintabs').tabs('select', title); 
			} 
			else 
			{ 
    		$('#maintabs').addTab(tab);
    		$("#maintabs").tabs( "select" , n );
    		}
        }else if(flag == 5){
        	var tab=[{id:'10',text:'可用积分',href:'/general/card/MyIntegral.do?b_showcard=link',target:'il_body',closable:true}];
        	var title = tab[0].text;
	    	if ($('#maintabs').tabs('exists', title))
			{ 
				$('#maintabs').tabs('select', title); 
			} 
			else 
			{ 
    		$('#maintabs').addTab(tab);
    		$("#maintabs").tabs( "select" , n );
    		}
        }else if(flag == 0){
        	var tab=[{id:'2001',text:'我的信息',href:'/workbench/browse/showselfinfo.do?b_search=link&amp;a0100=A0100&amp;flag=infoself',target:'il_body',closable:true}];
        	var title = tab[0].text;
	    	if ($('#maintabs').tabs('exists', title))
			{ 
				$('#maintabs').tabs('select', title); 
			} 
			else 
			{ 
    		$('#maintabs').addTab(tab);
    		$("#maintabs").tabs( "select" , n );
    		}
        }
    }   
    </script>
        
</head>

<body style="height:100%;width:100%;overflow:hidden;margin:0;padding:0;TEXT-ALIGN:center;">
  <div style="height:100%; width:80%;overflow:hidden;margin:0;padding:0;MARGIN-RIGHT: auto;MARGIN-LEFT: auto;">
 
       <% if(hidetopbar.equals("0")) { %>
       <div id="hj-ui-top">
             <div class="inner_top_right">
                  <div class="hj-ui-top-menutools">
                     <a href="#" class="easyui-linkbutton"  menu="#mm" iconCls="icon-redo" plain="true" onclick="javascript:logout();">
                        <bean:message key="label.banner.exit"/>
                     </a>  
                     <a href="#" class="easyui-linkbutton" iconCls="icon-no" plain="true" onclick="javascript:isclose();">
                        <bean:message key="config.sys.setup.logonout"/>
                     </a>
                </div>
                <div class="hj-ui-top-menutools" id="min-menutools">  
                </div>
             </div>
       </div>  
       <%} %>
       <div style="float:right; width:100%; height:90%;" id="mainDiv">
              
           <div class="easyui-tabs" fit="true" plain="true" id="maintabs" >  
        
           </div>
           <div id="tab-tools" >
                
           </div>
           
       </div>
   </div>
 </div>

 <!-- 
 <div style="height:70px;width:80%;overflow:hidden;margin:0;padding:0;MARGIN-RIGHT: auto;MARGIN-LEFT: auto;">
 <br>
    中国化学工程集团公司 <br>
                  电话:+86 (010) 59765555    传真:+86 (010) 59765588    E-mail:cncec@cncec.com.cn <br>
                 办公地址:中国北京市东城区东直门内大街2号 京ICP备05027490号 京公网安备110401400099 系统提供商：世纪
 </div>
  -->
</body>

</html>