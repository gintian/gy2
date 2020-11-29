<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="java.util.HashMap" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
  
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">  
<html>  
  <head>  
<script language="JavaScript" src="/components/tableFactory/tableFactory.js"></script>
<script language="JavaScript" src="../../../module/recruitment/recruitment_resource_zh_CN.js"></script>
<script type="text/javascript" src="../../../../js/hjsjUrlEncode.js"></script>
<link href="/module/recruitment/css/style.css" rel="stylesheet" type="text/css" />  
    <style type="text/css">
    body,ul,ol,li,p,h1,h2,h3,h4,h5,h6,form,fieldset,img,div,dl,dt,dd,span,table,tr,td{margin:0;padding:0; border:none;}
    .x-window-default {padding: 0;border-width: 0px;border-style: none;background-color: white}
    </style>
      
  </head>  
  <body>
    <div id="border">
    </div>  
    <div id="borders" style="overflow: hidden;white-space:nowrap;">
    <logic:iterate id="element" name="positionForm" property="positionInfo">
    <input type="hidden" id="pageNum" value="<bean:write name="element" property="pageNum"/>"/>
    <input type="hidden" id="searchStr" value="<bean:write name="element" property="searchStr"/>"/>
    <input type="hidden" id="pagesize" value="<bean:write name="element" property="pagesize"/>"/>
    <input type="hidden" id="z0381" value="<bean:write name="element" property="z0381"/>"/>
    <input type="hidden" id="z0301" value="<bean:write name="element" property="z0301"/>"/>
    <input type="hidden" id="from" value="<bean:write name="element" property="from"/>"/>
    <input type="hidden" id="status" value="<bean:write name="element" property="status"/>"/>
    <input type="hidden" id="page" value="<bean:write name="element" property="page"/>"/>
    <input type="hidden" id="node_id" value="<bean:write name="element" property="node_id"/>"/>
    <input type="hidden" id="link_id" value="<bean:write name="element" property="link_id"/>"/>
    <input type="hidden" id="status_id" value="<bean:write name="element" property="status_id"/>"/>
   <div class="hj-wzm-xq-all">
        <div class="hj-zm-cplc-all" style="margin-left:5px" >
            <div class="hj-zm-cplc-all-one">
            <span style="margin-right:20px"><h3><bean:write name="element" property="position"/></h3></span>
            <span style="margin-right:20px"><bean:write name="element" property="department"/>（<bean:write name="element" property="number"/>人）</span>
			<span style="margin-right:20px">到岗日期&nbsp;<bean:write name="element" property="endTime"/></span>
        	<span style="margin-right:20px"><bean:write name="element" property="statu"/></span>
        	<span style="position:absolute;right:60px">
				<div style="position:absolute;right:140px;display: none;" id="operation">
				<logic:notEqual name="element" property="status" value="04">
					<div style="float:right" id="changA">
					<hrms:priv  func_id="3110108"><a href="javascript:void(0)" onclick="toEdit()">编辑</a></hrms:priv>
					</div>
				</logic:notEqual>
				</div>
				<div style="float:right;display:none;position:absolute;right:140px;" id="filterDiv">
                  <hrms:priv func_id="311010901">
                	<a href="javascript:void(0)" onclick="resumeFilterMethods(1)" style="margin-left:10px">设置筛选指标</a>
                  </hrms:priv>
                  <hrms:priv func_id="311010902">
                	<a href="javascript:void(0)" onclick="resumeFilterMethods(2)" style="margin-left:10px">新增筛选规则</a>
                  </hrms:priv>
                  <hrms:priv func_id="311010903">
                	<a href="javascript:void(0)" onclick="resumeFilterMethods(3)" style="margin-left:10px"><font id="savefont">保存</font></a>
                	<span style="color: gray">|</span>
                  </hrms:priv>
                </div>
	            <logic:equal value="04" name="element" property="status">
	            <hrms:priv func_id="3110103">
	                <a style="color:#1b4a98;" href="javascript:void(0);" onclick="toStopPosi()">暂停招聘</a>
	            </hrms:priv>
	            <hrms:priv func_id="3110104">
	                <a style="color:#1b4a98"  href="javascript:void(0);" onclick="toEndPosi()">结束招聘</a>
	            </hrms:priv>
	            </logic:equal>
	            <logic:equal value="09" name="element" property="status">
	            <hrms:priv func_id="3110102">
	                <a style="color:#1b4a98"  href="javascript:void(0);" onclick="toPublishPosi()">发布职位</a>
	            </hrms:priv>
	            <hrms:priv func_id="3110104">
	                <a style="color:#1b4a98"  href="javascript:void(0);" onclick="toEndPosi()">结束招聘</a>
	            </hrms:priv>
	            </logic:equal>
	            <logic:equal value="03" name="element" property="status">
	            <hrms:priv func_id="3110102">
	            <a style="color:#1b4a98"  href="javascript:void(0);" onclick="toPublishPosi()"> 发布职位</a>
	            </hrms:priv>
	            </logic:equal>
            </span>
            <a style="color:#1b4a98;position:absolute;right:10px" href="javascript:void(0)" onclick="returnPos()">返回</a>
            </div>
            </div>
            <div class="hj-zm-cplc-all-two">
                <div class="hj-zm-cplc-all-two-top">
                    <ul>
                        <li><a id="posiDetail" onclick="posidati()" href="javascript:void(0);" style="border-right:1;border-bottom: none;">职位详情</a></li>
                        <hrms:priv func_id="3110109">
                          <li><a id="resumeFilte" onclick="resumeFilter()" href="javascript:void(0);" style="border-left:0;border-right:1;border-bottom: none;">简历筛选</a></li>
                        </hrms:priv>
                        <hrms:priv func_id="3110117">
                       <li><a id="posiResum" onclick="recruit()" href="javascript:void(0);" style="border-left:0;border-bottom: none;">职位候选人</a></li>
                        </hrms:priv>
                    </ul>
                </div>
            </div>
            
    <div style="position: absolute;top: 68px;margin:0;padding:0; width:100%;height:1px;background-color:#B5B5B5;overflow:hidden;float: left;"></div> 
    </div>
    </logic:iterate>
    </div> 
  </body>  
  <script type="text/javascript">  
  var pageNum = Ext.getDom("pageNum").value;
  var searchStr = Ext.getDom("searchStr").value;
  var pagesize = Ext.getDom("pagesize").value;
  var z0301 = Ext.getDom("z0301").value;
  var z0381 = Ext.getDom("z0381").value;
  var z0319 = Ext.getDom("status").value;
  var from = Ext.getDom("from").value;
  var page = $URL.encode(Ext.getDom("page").value);
  var node_id = Ext.getDom("node_id").value;
  var link_id = Ext.getDom("link_id").value;
  var status_id = Ext.getDom("status_id").value;
             var sign = "${positionForm.sign}";
        Ext.onReady(function() {
             var html;
             var winHeight = parent.document.body.clientHeight-10; 
             var z0301 = Ext.getDom("z0301").value;
             var z0381 = Ext.getDom("z0381").value;
             if("1"==sign){
                 html='/recruitment/position/position.do?b_toedit=link&z0319='+z0319+'&z0301='+z0301+'&pageNum='+pageNum+'&searchStr='+searchStr+'&pagesize='+pagesize+'&from='+from;
                 var posiDetail = Ext.getDom('posiDetail');
                 if(posiDetail!=null)
                	 posiDetail.style.background="#E4E4E4";
                 Ext.getDom("operation").style.display="block";
             }else{
                 html='/recruitment/recruitprocess/recruitprocesslist.do?b_query=link&z0319='+z0319+'&z0301='+z0301+'&z0381='+z0381+'&pageNum='+pageNum+'&searchStr='+searchStr+'&pagesize='+pagesize+'&page='+page;
                 if(link_id!=""&&node_id!="")
                 {
                     html+="&node_id="+$URL.encode(node_id)+"&link_id="+link_id;
                 }else{
                	 var node = status_id!=null&&status_id!=""?status_id:"01";
                	 html+="&node_id="+$URL.encode(node)+"&link_id="+link_id;
                 }
                 var posiResum = Ext.getDom('posiResum');
                 if(posiResum!=null)
                	 posiResum.style.background="#E4E4E4";
             }
             var panel = Ext.widget('viewport',{ 
                  height: winHeight,  
                  //width: '100%',  
                  id:'position_viewport',
                  border:false,
                  margin:'50 5 0 5',
                  //renderTo: 'border',  
                  layout: 'border',   //表格布局  
                  items: [{    
                            region: 'north', 
                            id:'northpanel', 
                            height: 74,
                            contentEl:'borders',  
                            border:false
                           },  
                           {    
                              html: '<iframe name="ifra" src="'+html+'"  width="100%" id="recruitProcess" frameborder=0 height="100%"></iframe>',  
                              region: 'center',  
                              height: '100%',
                              width:'100%',
                              border:false
                           }
                      ]  
             });  
        });
        function posidati(){
        	Ext.getDom("filterDiv").style.display="none";
        	document.getElementsByName("ifra")[0].src='/recruitment/position/position.do?b_toedit=link&z0319='+z0319+'&z0301='+z0301+'&pageNum='+pageNum+'&searchStr='+searchStr+'&pagesize='+pagesize+'&from='+from;
        	Ext.getDom('posiDetail').style.background="#E4E4E4";
        	Ext.getDom('posiResum').style.background="";
        	if(!Ext.isEmpty(Ext.getDom('resumeFilte')))
                Ext.getDom('resumeFilte').style.background="";
        	sign="1";
        	if(z0319!="04")
        		Ext.getDom("operation").innerHTML='<div style="float:right" id="changA"><hrms:priv  func_id="3110108"><a href="javascript:void(0)" onclick="toEdit()">编辑</a></hrms:priv></div>';

           	if(!Ext.isEmpty(Ext.getDom("operation")))
            	Ext.getDom("operation").style.display="block";
        }
        function resumeFilter(){
        	document.getElementsByName("ifra")[0].src="/module/recruitment/position/resumefilter/showResumeFilterList.html";
       		Ext.getDom("filterDiv").style.display="block";
        	
        	Ext.getDom('resumeFilte').style.background="#E4E4E4";
        	Ext.getDom('posiDetail').style.background="";
        	if(!Ext.isEmpty(Ext.getDom('posiResum')))
            	Ext.getDom('posiResum').style.background="";
            if(!Ext.isEmpty(Ext.getDom("operation")))
                Ext.getDom("operation").style.display="none";
        }
        //简历筛选方法     flag:  1--设置筛选指标        2--新增筛选器         3---保存
        function resumeFilterMethods(flag){
            //获取iframe对应页面的window对象    或使用  document.getElementsByName("ifra")[0].contentWindow进行获取
			var resumeWindow = window.frames["ifra"];
			if(flag == 1){
				addFilterItem();
				Ext.getDom("savefont").color="";
			}
			else if(flag == 2){
				resumeWindow.resumeFilter.addFilter();
				Ext.getDom("savefont").color="";
			}
			else if(flag == 3){
				resumeWindow.resumeFilter.saveFilter();
				//Ext.getDom("savefont").color="gray";//保存后置灰
			}
        }
        
      //设置筛选指标
    	function addFilterItem(){
    		//保存当前页面中已填写的值或已新增的筛选器
    		if(!Ext.isEmpty(window.frames["ifra"].resumeFilter.filterItemId))
    			window.frames["ifra"].resumeFilter.saveFilter(2);
    		
    		Ext.Loader.setConfig({
    			enabled: true,
    			paths: {
    				'EHR': '/components'
    			}
    		});
    		if(undefined==Ext.getCmp('addWindow')){
    			Ext.require('EHR.fielditemmultiselector.Selector', function(){
    				Ext.create("EHR.fielditemmultiselector.Selector",{fieldset:'A',module:'ZP',items:Ext.encode(window.frames["ifra"].resumeFilter.temJsonObj.rule),afterfunc:'window.frames["ifra"].resumeFilter.refreshPage'});
    			});
    		}
    	}
      
        function recruit(){
        	Ext.getDom("filterDiv").style.display="none";
       		var node = status_id!=null&&status_id!=""?status_id:node_id;
        	if(link_id!=""&&node!="")
            {
                document.getElementsByName("ifra")[0].src='/recruitment/recruitprocess/recruitprocesslist.do?b_query=link&&node_id='+node+'&link_id='+link_id+'&z0319='+z0319+'&z0301='+z0301+'&z0381='+z0381+'&pageNum='+pageNum+'&searchStr='+searchStr+'&pagesize='+pagesize+'&page='+page;
            }else{
            	document.getElementsByName("ifra")[0].src='/recruitment/recruitprocess/recruitprocesslist.do?b_query=link&node_id=0101&link_id='+link_id+'&z0319='+z0319+'&z0301='+z0301+'&z0381='+z0381+'&pageNum='+pageNum+'&searchStr='+searchStr+'&pagesize='+pagesize+'&page='+page;
            }
            sign="2";
        	Ext.getDom('posiDetail').style.background="";
        	
        	if(!Ext.isEmpty(Ext.getDom('resumeFilte')))
        	    Ext.getDom('resumeFilte').style.background="";
    	    
            Ext.getDom('posiResum').style.background="#E4E4E4";
            
            if(!Ext.isEmpty(Ext.getDom('operation')))
                Ext.getDom("operation").style.display="none";
        }
        function toStopPosi(){
        	
        	Ext.Msg.confirm("提示信息","确认要暂停职位吗?",function(btn){ 
                if(btn=="yes"){ 
                // 确认触发，继续执行后续逻辑。 
		        	var hashvo=new ParameterSet();
		        	hashvo.setValue("z0301s",z0301);
		        	hashvo.setValue("act","stop");
		            var request=new Request({asynchronous:false,onSuccess:toSuccess ,functionId:'ZP0000002074'},hashvo); 
                } 
            });
        	
        }
        function toEndPosi(){
        	Ext.Msg.confirm("提示信息","确认要结束职位吗?",function(btn){ 
                if(btn=="yes"){ 
                // 确认触发，继续执行后续逻辑。 
		        	var hashvo=new ParameterSet();
		            hashvo.setValue("z0301s",z0301);
		            hashvo.setValue("act","end");
		            var request=new Request({asynchronous:false,onSuccess:toSuccess ,functionId:'ZP0000002074'},hashvo); 
                } 
            });
        }
        function toPublishPosi(){
            Ext.Msg.confirm("提示信息","确认要发布职位吗?",function(btn){ 
                if(btn=="yes"){ 
                	if(z0319=="04"){
        				Ext.MessageBox.alert(PROMPT_INFORMATION,"已经是发布状态！");
        				return;
        			}
        			if(z0319!="03" && z0319!="09" ){
        				Ext.MessageBox.alert(PROMPT_INFORMATION,"当前状态无法发布，仅已批和暂停状态的职位可以发布！");
        				return;
        			}
               		// 确认触发，继续执行后续逻辑。 
                    var hashvo=new ParameterSet();
                    hashvo.setValue("z0301s",z0301);
                    hashvo.setValue("act","publish");
                    var request=new Request({asynchronous:false,onSuccess:toSuccess ,functionId:'ZP0000002074'},hashvo); 
                } 
            });
        }
        function toSuccess(outparamters){
        	window.location.href="/recruitment/position/position.do?b_search=link&z0301="+z0301+"&z0381="+z0381+"&pageNum="+pageNum+"&searchStr="+searchStr+"&pagesize="+pagesize+"&sign="+sign+"&from="+from;
        };
        
        function returnPos(){
        	if(parent.Global){
        		parent.Global.reload();
                parent.Ext.getCmp("recommendWinID").close();
            }else
            	window.location.href="/templates/index/hcm_portal.do?b_query=link";
        	
        }
        function toEdit(){
        	var elem = Ext.getDom("changA");
        	elem.innerHTML='<a href="javascript:void(0)"  onclick="onlySave()" id="save">保存</a><a href="javascript:void(0)" onclick="toView()">取消</a>';
            ifra.window.Global.toEdit();
        }
        
        function onlySave(){
        	var elem = Ext.getDom("changA");
        	var temp = ifra.window.Global.onlySave();
        	if(temp !=1)
        	    elem.innerHTML = '<hrms:priv  func_id="3110108"><a href="javascript:void(0)" onclick="toEdit()">编辑</a></hrms:priv>';
        }
        function toView(){
        	var elem = Ext.getDom("changA");
            elem.innerHTML = '<hrms:priv  func_id="3110108"><a href="javascript:void(0)" onclick="toEdit()">编辑</a></hrms:priv>';
            ifra.window.Global.toView();
        } 
        
      //必须含有body元素才可屏蔽backspace
        window.onload=function(){
        	document.getElementsByTagName("body")[0].onkeydown =function(e){            
                //获取事件对象
                var event = e?e:window.event;
        		var elem = event.relatedTarget || event.srcElement || event.target ||event.currentTarget;   
                if(event.keyCode==8){//判断按键为backSpace键  
        			//获取按键按下时光标做指向的element  
                    var elem = event.srcElement || event.currentTarget;   
                    //判断是否需要阻止按下键盘的事件默认传递  
                    var name = elem.nodeName;  
                    if(name!='INPUT' && name!='TEXTAREA'){
                    	return _stopIt(event);  
                    }  
                    var type_e = elem.type.toUpperCase();  
                    if(name=='INPUT' && (type_e!='TEXT' && type_e!='TEXTAREA' && type_e!='PASSWORD' && type_e!='FILE')){  
                    	return _stopIt(event);
                    }  
                    if(name=='INPUT' && (elem.readOnly==true || elem.disabled ==true)){  
                    	return _stopIt(event);  
                    }  
                }  
            }
        }; 
        function _stopIt(e){  
        	if(e.returnValue){  
        		e.returnValue = false ;  
            }  
            if(e.preventDefault ){  
                e.preventDefault();  
            }                 
            return false;
        } 
        
    </script>
</html>  