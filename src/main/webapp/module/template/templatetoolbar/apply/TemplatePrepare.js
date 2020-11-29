 /**
  * 报批之前的准备
  * 1、先保存数据
  * 2、判断当前是否有选中的记录，如果没有，则弹出信息进行提示
  * 3、判断是否符合业务规则
  * 4、判断编制  
  * 5、校验必填
  * 6、弹框
  * 7、判断是否特殊角色
  * 8、开始审批  
  */
 Ext.define('TemplateApplyUL.TemplatePrepare',{
	tab_id:'',
	task_id:'',
	sp_mode:"1",
	no_sp_yj:"0",
	templPropety:"",
	resultObj:{},
	bOpenDialog:false,//标识是否打开过对话框
   	no_sp_yj:'',//不需要填写审批意见
   	sp_mode:'' ,//审批方式 1 手工 0 自动
   	def_flow_self:'0',//是否是自定义审批
   	startFlag:'',//是否是发起人
   	approveFunc:'',	//是否有批准权限
   	displayAgreeBtn:"0",	//是否显示同意按钮 1:同意 
   	endUser:'',	//业务办理人
   	endUserType:'',//业务办理人类型	
   	endUserFullName:'',//业务办理人全称	
   	isSendMessage:'0',//不走审批是否弹窗 0 不弹窗，1 邮件， 2  短信， 3 邮件和短信
   	isSendCopyMessage:'1',//抄送邮件的权限 0 没有 1有
    constructor:function(config){
    	//liuyz bug31743 用户点击报批、审批、驳回、提交后将按钮置灰，防止用户多次点击产生错误数据
    	templateTool_me.disabledButton("template_rejectButton");
		templateTool_me.disabledButton("template_applyButton");
		templateTool_me.disabledButton("template_submitButton");
    	templatePrepare_me=this;
    	this.templPropety=config.templPropety;
        this.resultObj.applyFlag= config.type; //0:直接提交  1 报批 ; 2 驳回 ;  3 ：批准 ; 4:手动审批
        this.allNum = config.allNum;
    	this.validate();
    },
    /**
     * 判断是否符合业务规则、编制、必填、校验公式
     */
    validate:function(){		
    	if (templateTool_me.save('true','true')) {//报批之前将未保存的数据保存。
	    	var map = new HashMap();
            initPublicParam(map,this.templPropety);        
            map.put("applyFlag", this.resultObj.applyFlag); 
            map.put("allNum",this.allNum+"");
            if(this.templPropety.fillInfo){
            	if(this.templPropety.fillInfo=="1"&&templatePrepare_me.templPropety.module_id=='9'){//自助业务申请 且fillInfo='1'
            		//弹窗 验证码
            		var checkWin = Ext.widget("window",{
            			id:'checkWin',
            			title:'请输入验证码',
            			height:150,
            			width:300,
            			layout:'vbox',
            			modal:true,
            			closeAction:'destroy',
            			items:[{xtype:'container',layout:'hbox',width:300,
	            			items:[{xtype:'textfield',height:28,id:'checkid',width:100,padding:'20 0 0 50'},{
								xtype:'box',
								margin:'20 0 0 10',
								id:'vaildataCode',
								width:80,
								height:30,
								style : {cursor : 'pointer'},
								autoEl: {  
							        tag: 'img',    //指定为img标签  
						        	src:'/servlet/vaildataCode?out=true&channel=1'
							    },
							    listeners:{
									render:function(){
										var me = this;
									  	this.getEl().on('click',function(){
									  		var url = document.getElementById('vaildataCode').src;
											document.getElementById('vaildataCode').src = url+"&id=" + Math.random();
									  	});
									}
								}
		            		}]},{xtype:'label',hidden:true,id:'checkFalseText',style:'color:red;font-size:14px !important;',padding:'5 0 0 110'}],
	            		buttonAlign:'center',
	            		buttons:[{text:"确认",handler: function() {
					        var data = "";
						    //获取验证码
						    Ext.Ajax.request({
								url : '/servlet/GetvaildataCodeServlet',
								async:false,
								success : function(res) {
									data = Ext.decode(res.responseText);
								}
							});
							var value = Ext.getCmp("checkid").getValue();
							map.put("checkdata", data);
							map.put("checkvalue", value);
						    Rpc({functionId:'MB00005001',async:true,success:templatePrepare_me.validateSuccess},map);
					    }}]
            		});
            		checkWin.show();
            	}
    		}else{
    			Ext.MessageBox.wait("正在执行验证操作，请稍候...", "等待");
                Rpc({functionId:'MB00005001',async:true,success:templatePrepare_me.validateSuccess},map);
    		}
    	}	 
    },
    /**
     * 判断是否符合业务规则OK
     */
    validateSuccess:function(form,action){
    	var result = Ext.decode(form.responseText);
    	if(templatePrepare_me.templPropety.module_id=='9'){
    		if(result.checkflag!='false'&&templatePrepare_me.templPropety.fillInfo=="1"){
         		Ext.MessageBox.wait("正在执行验证操作，请稍候...", "等待");
         		Ext.getCmp('checkWin').close();
         	}else if(result.checkflag=='false'&&templatePrepare_me.templPropety.fillInfo=="1"){
         		var url = document.getElementById('vaildataCode').src;
    			document.getElementById('vaildataCode').src = url+"&id=" + Math.random();
    			Ext.getCmp('checkFalseText').setVisible(true);
    			Ext.getCmp('checkFalseText').setText('验证码错误！');
                return;
         	}
    	}
    	Ext.MessageBox.close();	
        if(!result.succeed){
        	var message = result.message;
			if(message&&message.indexOf("拆分审批")!=-1){
				templateTool_me.checkSpllit(message);
				return;
        	}else{
        		Ext.showAlert(result.message,function(){
             	   //liuyz bug31743 用户点击报批、审批、驳回、提交后将按钮置灰，防止用户多次点击产生错误数据
                    templatePrepare_me.enabledButton();
                });
                return;
        	}
        }
        var info=result.info;
        templatePrepare_me.validateFlag=result.validateFlag;
    	templatePrepare_me.no_sp_yj =result.no_sp_yj; 
    	templatePrepare_me.sp_mode =result.sp_mode; 
    	templatePrepare_me.def_flow_self =result.def_flow_self; 
    	templatePrepare_me.startFlag =result.startFlag; 
    	templatePrepare_me.approveFunc =result.approveFunc; 
    	templatePrepare_me.isSendMessage =result.isSendMessage;
    	templatePrepare_me.deprecate = result.deprecate;
    	templatePrepare_me.taskIntoType = result.taskIntoType;
    	templatePrepare_me.isSendCopyMessage = result.isSendCopyMessage;//是否有抄送邮件权限，0没有，1：有
    	if (result.sp_mode=="1"){
    		if (result.displayAgreeBtn=="true"){//显示同意按钮
		    	templatePrepare_me.displayAgreeBtn ="1"; 
		    	templatePrepare_me.endUser =result.endUser; 
		    	templatePrepare_me.endUserType =result.endUserType; 
		    	templatePrepare_me.endUserFullName =result.endUserFullName; 
    		}
    	}
    	if (info!=""){//有校验提示信息
	    	if (templatePrepare_me.validateFlag=="3"){//校验编制
	    	     var type=result.headControlType;
	    	     if (type=="warn"){//非强制
	    	         Ext.showConfirm(info, function(btn) {
		                if (btn == 'yes') {
		                   templatePrepare_me.validateOK();
		                } else {
		                    //liuyz bug31743 用户点击报批、审批、驳回、提交后将按钮置灰，防止用户多次点击产生错误数据
		                	templatePrepare_me.enabledButton();
		                  return;
		                }
	                  }, this);
                   }
                   else {//强制
                       Ext.showAlert(info,function(){
                    	   //liuyz bug31743 用户点击报批、审批、驳回、提交后将按钮置灰，防止用户多次点击产生错误数据
                           templatePrepare_me.enabledButton(); 
                       });
                       return;
                   }
    	    }
	    	else if(templatePrepare_me.validateFlag=="4"){//校验机构撤销
	    		Ext.showConfirm(info, function(btn) {
	                if (btn == 'yes') {
	                   templatePrepare_me.validateOK();
	                } else {
	                   //liuyz bug31743 用户点击报批、审批、驳回、提交后将按钮置灰，防止用户多次点击产生错误数据
	                   templatePrepare_me.enabledButton();
	                   return;
	                }
                  }, this);
	    	}
    	    else {//其他校验未通过
    	         Ext.showAlert(info,function(){
    	        	 //liuyz bug31743 用户点击报批、审批、驳回、提交后将按钮置灰，防止用户多次点击产生错误数据
        	         templatePrepare_me.enabledButton();
    	         });
    	         return;
    	    }
        }
        else {
             templatePrepare_me.validateOK();
        }
    },
    /**
     * 如果符合业务规则，开始走下面的流程。
     */
    validateOK:function(){
        if (this.resultObj.applyFlag=="0"){//直接提交    
        	if(templatePrepare_me.validateFlag=='4'){
        		if(templatePrepare_me.isSendMessage=='0')
        			templatePrepare_me.submit();
        		else
        			templatePrepare_me.openDialog();
        	}
        	else{
        		Ext.showConfirm(MB.MSG.isOperate,function(value){
            		if(value=="yes"){
            			if(templatePrepare_me.isSendMessage=='0')
                			templatePrepare_me.submit();
                		else
                			templatePrepare_me.openDialog();
            		}else
            			templatePrepare_me.enabledButton();
                });
        	}
        }
        else {//报批、驳回、审批，需要判断是否弹框
        	/*if(templatePrepare_me.sp_mode=='1'&&templatePrepare_me.def_flow_self=='1'&&templatePrepare_me.templPropety.task_id=="0"){//如果是手工在起草状态且勾选了自定义审批流程但是没有定义审批流程
        		Ext.showAlert("您没有配置自定义审批流程，请在功能导航-自定义流程菜单处配置审批流程！");
        		return;
        	}*/
            templatePrepare_me.openDialog();
        }
       
    },
    /**
     * 提交入库。
     */
    submit:function(dialogObj){
        var map = new HashMap();
        initPublicParam(map,templatePrepare_me.templPropety);
        if(templatePrepare_me.isSendMessage!='0'){
        	map.put("isSendMessage",templatePrepare_me.isSendMessage);
	   		map.put("context",getEncodeStr(dialogObj.context)); 
	     	map.put("title",getEncodeStr(dialogObj.title)); 
	     	map.put("sendid",getEncodeStr(dialogObj.sendid));
	     	map.put("email_staff_value",dialogObj.email_staff_value);
        }
        Rpc({functionId:'MB00005005',async:false,success:templatePrepare_me.refresh},map); 
    },
    
    /**
     * 判断是否需要弹出框，控制对话框的展现方式。
     */
    openDialog:function(){
    	if (this.resultObj.applyFlag=="0"){//直接提交
    		var map = new HashMap();
    		map.put("callBackFunc",templatePrepare_me.submit);
    		Ext.require('TemplateApplyUL.TemplateSendMessage',function(){
	        	var re = Ext.create("TemplateApplyUL.TemplateSendMessage",{map:map});
	        })
    	}else{//报批、驳回、审批，需要判断是否弹框
	        var no_sp_yj=templatePrepare_me.no_sp_yj;//是否要填写审批意见 0:填写  1：不填	        
	        var sp_mode=templatePrepare_me.sp_mode;  // 审批模式=0自动流转，=1手工指派
	        var def_flow_self=templatePrepare_me.def_flow_self;//是否自定义审批流程  
	        //初始化弹出框的展现参数，控制对话框的展现方式  需要弹框（手动、自动、报批及审批组合情况）or不需要弹框
	        var bNeedDialog=(no_sp_yj=="0"|| sp_mode=="1");//是否需要弹框
	        var bDraft=(templatePrepare_me.templPropety.task_id=="0");//起草报批
	        //业务申请也不需要弹框 陈总提
	        if (sp_mode=="0" && bDraft && templatePrepare_me.templPropety.module_id=="9"){
	            bNeedDialog=false;
	        }
	        if (sp_mode=="1"&& bDraft &&def_flow_self=="2"){
	        	bNeedDialog=false;
	        }
	        
	        if (bNeedDialog){
		        /*
	             ***手工报批: 确定、关闭 报送对象
	             ***自定义报批:  确定、关闭
	             ***自动报批：确定、关闭 无报送对象
	             ***手工审批: 继续报批、批准、  驳回（不是发起人才显示） 
	             ***自动审批：确定、关闭   
	             */
	            var btnOk="0";//确定
	            var btnAgree="0";//同意
	            var btnContinueApply="0";//继续报批
	            var btnApprove="0";//批准
	            var btnReject="0";//驳回
	            var ApplyObject="0";//报送对象
	            var ApplyContent="1"; //审批意见
	            var isReport=templatePrepare_me.isSendCopyMessage;//抄送对象
	            var winTitle="报批";
	            if (bDraft){//起草
	                ApplyContent="0";
	                btnOk="1";  
		            if(sp_mode=="1"){//如果是手动流转	                
			             ApplyObject="1";
	                }else{  
	                }
	            }
	            else {
					if(sp_mode=="0" || def_flow_self=="2"){//如果是自动流转及自定义                 
					  btnOk="1";
					}else{  
					  ApplyObject="1";
	                  btnContinueApply="1";
	                  btnAgree=templatePrepare_me.displayAgreeBtn;//同意
	                  btnApprove=templatePrepare_me.approveFunc ;
	                  btnReject="1"; 
	                  winTitle="审批";  
	                  if (templatePrepare_me.startFlag=="1"){//开始节点，不能驳回,同首次报批界面
	                    btnReject="0";
	                    btnApprove="0";
	                    ApplyContent="0";
	                    btnContinueApply="0";
	                    btnAgree="0";
	                    btnOk="1";
	                  }
	                  if(templatePrepare_me.taskIntoType=="07"){//驳回
	                	  //btnReject="0";
	                	  btnApprove="0";
	                  }
					}
	            }
	            if (no_sp_yj=="1"){
	               ApplyContent="0";
	            }
	            if (templatePrepare_me.resultObj.applyFlag=="2"){//驳回
	                isReport="0";
	                winTitle="驳回";
	            }else if (templatePrepare_me.resultObj.applyFlag=="3"){//批准
	                winTitle="提交";
	            }
	            this.bOpenDialog=true;
	            var map = new HashMap();//页面展现参数
	            map.put("isPri", "1");//优先级
	            map.put("isReport", isReport);//抄送对象
	           	map.put("isApply", ApplyObject);//报送对象
	           	map.put("isApplyContent", ApplyContent);
	           	map.put("btnAgree", btnAgree);//同意
	           	map.put("btnContinueApply", btnContinueApply);
	           	map.put("btnReject", btnReject);
	           	map.put("btnApprove", btnApprove);
	           	map.put("btnCancel", "1");
	           	map.put("btnOk", btnOk);
	           	map.put("winTitle", winTitle);
	            map.put("callBackFunc",templatePrepare_me.executeOK);
	            Ext.require('TemplateApplyUL.TemplateSubmit',function(){
	                   var re = Ext.create("TemplateApplyUL.TemplateSubmit",{map:map});
	            })
	        }
	        else {//不需要弹框 模拟弹出框返回值。	        
	           //模拟弹框返回值
	            templatePrepare_me.resultObj.pri="1";//优先级
	            templatePrepare_me.resultObj.actorType="";//报送对象类型
	            templatePrepare_me.resultObj.actorId="";//报送对象
	            templatePrepare_me.resultObj.actorName="";//报送对象名称
	            templatePrepare_me.resultObj.content="";//意见
	            templatePrepare_me.resultObj.reportObjectId="";//报送对象
	            /*
	            var infoFlag="报批";	
	            if (templatePrepare_me.resultObj.applyFlag=="2"){
	                infoFlag="驳回";
	            }
	            else  if (templatePrepare_me.resultObj.applyFlag=="3"){
	                infoFlag="提交";
	            }
	            var info ="你确定要"+infoFlag+"记录吗?";
	            Ext.Msg.confirm(common.button.promptmessage, "", function(btn) {
	                        if (btn == 'yes') {
	                           templatePrepare_me.getNextNode();
	                        } else {
	                          return;
	                        }
	    
	             }, this);  
	             */    
	             if (sp_mode=="0" && bDraft && templatePrepare_me.templPropety.module_id=="9"){//弹出确认提示框
			        	 Ext.showConfirm(MB.MSG.isOperate,function(value){
			        		if(value=="yes"){
			        			 templatePrepare_me.getNextNode();
			        		}else{
			        			templatePrepare_me.enabledButton();
			        			return;
			        		}
		           		 });
	        	 }else
	           		 templatePrepare_me.getNextNode();
	        }
        }
    },
    
    /**
     * 点击确定、报批、驳回、批准按钮后的调用方法
     * 
     */
    executeOK:function(dialogObj){
        //获取对话框的目前选择的参数。     
        templatePrepare_me.resultObj.pri=dialogObj.pri;//优先级
        templatePrepare_me.resultObj.actorType=dialogObj.actorType;//报送对象类型
        templatePrepare_me.resultObj.actorId=dialogObj.actorId;//报送对象
        templatePrepare_me.resultObj.actorName=dialogObj.actorName;//报送对象名称
        templatePrepare_me.resultObj.content=getEncodeStr(dialogObj.content);//意见
        templatePrepare_me.resultObj.reportObjectId=dialogObj.reportObjectId;//报送对象
        if (templatePrepare_me.resultObj.applyFlag=="2"){//驳回
           // templatePrepare_me.resultObj.applyFlag="2";
        }
        else { //按钮事件dialogObj.buttonFlag 1：确定、继续报批 ；2 驳回； 3批准   4：同意
            templatePrepare_me.resultObj.applyFlag=dialogObj.buttonFlag;
        }

        if(dialogObj.buttonFlag=="4"){//同意
        	if (templatePrepare_me.endUserType=="1"){//自助
        		templatePrepare_me.resultObj.actorType="1";
        	}
        	else {//业务用户
        		templatePrepare_me.resultObj.actorType="4";
        	}
	        templatePrepare_me.resultObj.actorId=templatePrepare_me.endUser;//报送对象
	        templatePrepare_me.resultObj.actorName=templatePrepare_me.endUserFullName;//报送对象名称
	        templatePrepare_me.resultObj.applyFlag="1";//置为报批
        }

        //判断是否是特殊角色
        templatePrepare_me.getNextNode();
    },
    /**
     * 获取下一流程节点 主要是校验特殊角色。
     * 
     */
    getNextNode:function(form,action){
        var map = new HashMap();
        initPublicParam(map,templatePrepare_me.templPropety);
        if (templatePrepare_me.sp_mode=="1"){
           map.put("actorType",templatePrepare_me.resultObj.actorType+""); 
           map.put("actorId",templatePrepare_me.resultObj.actorId+""); 
        }     
        map.put("applyFlag",templatePrepare_me.resultObj.applyFlag); //审批标记
        Rpc({functionId:'MB00005002',async:false,success:templatePrepare_me.getNextNodeOK},map);
    },
     /**
        * 获取下一流程节点OK，如果是特殊角色且有多个审批人 ，则弹框，只能选择一个审批人。
        * 
        */
    getNextNodeOK:function(form,action){
           var result = Ext.decode(form.responseText); 
           if(!result.succeed){
              Ext.showAlert(result.message,function(){
            	  //liuyz bug31743 用户点击报批、审批、驳回、提交后将按钮置灰，防止用户多次点击产生错误数据
            	  templatePrepare_me.enabledButton();
            	  if (templatePrepare_me.bOpenDialog){//关闭对话框
                      templateSubmit_me.submit_EnableButton();//将弹出框中按钮还原
                  }
              });
              return;
           }
           var specialRoleUserStr=result.specialRoleUserStr;
           var selectSpecial=result.selectSpecial;
           var task_id_bak=templatePrepare_me.templPropety.task_id;
           if(task_id_bak!='0'&&task_id_bak.indexOf(',')!=-1&&task_id_bak.substring(0,task_id_bak.length-1).split(',').length>1){
           		selectSpecial = 'false';
           		specialRoleUserStr = '';
           }
           if (selectSpecial=="true"){
	           var specialUserList=result.specialUserList;
	           var specialNodeList=result.specialNodeList;
           
              //特殊角色弹出窗体
               //var temp0=specialRoleUserStr.split("`"); 
              // var obj_vo =window.showModalDialog("/general/template/myapply/busiPage.do?b_select=link&selfapply=0&roleid="+temp0[0]+"&role_property="+temp0[1]+"&sp_mode=1&tabid="+tabid,null,"dialogWidth=650px;dialogHeight=450px;status=no");  
				
				Ext.require('TemplateApplyUL.SelectSpecialRoleUser',function(){
				       var re = Ext.create("TemplateApplyUL.SelectSpecialRoleUser",
				            {templPropety:templatePrepare_me.templPropety,
				             specialUserList:specialUserList,
				             specialNodeList:specialNodeList,
				             callBackFunc:templatePrepare_me.startTask
				            });
				})
           }
           else {
               templatePrepare_me.startTask(specialRoleUserStr);
           }
    },    
    /**
    * 开始后台处理下一流程操作。
    * 
    */
    startTask:function(specialRoleUserStr){
    	var map = new HashMap();
    	initPublicParam(map,templatePrepare_me.templPropety);
    	//liuyz 报批多人浏览器同步进程造成假死状态增加等待提示rpc使用异步方式提交数据
    	Ext.MessageBox.wait("数据正在提交中，请稍候...", "等待")
  	    map.put("pri",templatePrepare_me.resultObj.pri+"");   //优先级
  	    map.put("actorId",templatePrepare_me.resultObj.actorId);   //报送对象
  	    map.put("actorType",templatePrepare_me.resultObj.actorType);   //报送对象类型
  	    map.put("actorName",templatePrepare_me.resultObj.actorName);   //报送对象名称
  	    map.put("reportObjectId",templatePrepare_me.resultObj.reportObjectId);   //抄送的人格式：   ,1:Usr00000049
  	    map.put("content",templatePrepare_me.resultObj.content);   //
        map.put("specialOperate","");  //后台好像没用了 暂传空值。     		
  	    map.put("specialRoleUserStr",specialRoleUserStr);  ////特殊角色指定的用户 
  	    map.put("flag",templatePrepare_me.resultObj.applyFlag);  //报批类型 
  	    map.put("def_flow_self",templatePrepare_me.def_flow_self);//是否自定义审批流程
  	    Rpc({functionId:'MB00005004',async:true,success:templatePrepare_me.refresh},map); 
    },
     /**
     * 判断是否关闭前台页面，及返回。
     * 
     */
	refresh:function(form,action){
	    //liuyz 报批多人浏览器同步进程造成假死状态判断有等待框才关闭
		if(Ext.MessageBox)
		   Ext.MessageBox.close();
	    var result = Ext.decode(form.responseText);
	    if(!result.succeed){
              Ext.showAlert(result.message,function(){
            	//liuyz bug31743 用户点击报批、审批、驳回、提交后将按钮置灰，防止用户多次点击产生错误数据
          		templatePrepare_me.enabledButton();
          		if (templatePrepare_me.bOpenDialog){//关闭对话框
                    templateSubmit_me.submit_EnableButton();//将弹出框中按钮还原
                }
              });
              return;
        } 		    
        var info=result.info;
        var othersUrl = result.othersUrl;
        if(info==""){
            if (templatePrepare_me.bOpenDialog){//关闭对话框
                templateSubmit_me.closeWin();
            }
            if (templatePrepare_me.isSendMessage!='0'&&templatePrepare_me.resultObj.applyFlag=="0"){//关闭对话框
                templateSendMessage_me.closeWin();
            }
            var bApply=(templatePrepare_me.templPropety.task_id=="0");//报批
            if (!bApply){//审批 
                 var unDealedTaskIds=result.unDealedTaskIds;//未处理单据号
                 if (unDealedTaskIds&&unDealedTaskIds!=""){//如果有未处理完的人员，则刷新页面
                    templatePrepare_me.templPropety.task_id=unDealedTaskIds; 
            		templatePrepare_me.enabledButton();//bug 33758 提交后提交按钮没有将置灰还原。
                    templateTool_me.reLoadForm();
                 }
                 else { //如果人员都处理完了，则返回上一页面。
             		templatePrepare_me.enabledButton();
                 	templateTool_me.returnBack();
                 }
            }
            else {//留在当前页面 刷新
                if (templatePrepare_me.templPropety.module_id=="9"){//业务申请
                	if(templatePrepare_me.templPropety.return_flag=='14'){//自助配置业务申请链接，无返回特殊处理
                		Ext.showAlert('报批成功！',function(){
                			templatePrepare_me.enabledButton();
                 			templateTool_me.refreshApply();
                 		});
                    }else{
                    	if(templatePrepare_me.templPropety.fillInfo=="1"){
                 	 		Ext.showAlert("提交成功！",function(){
                 	 			templatePrepare_me.enabledButton();
                 	 			templateTool_me.returnBack();
                 	 		});
                 	 	}else{
                 			templatePrepare_me.enabledButton();
                    		templateTool_me.returnBack();
                 	 	}
                    }
                }
                else {
            		templatePrepare_me.enabledButton();
	                templateTool_me.refreshAll();
                }
            }  
        }
        else {
            Ext.showAlert(info,function(){
        		templatePrepare_me.enabledButton();
            });
        }
        if(othersUrl!=''&&othersUrl!=undefined){
    		templatePrepare_me.enabledButton();
        	window.open(othersUrl,"_blank","height="+(document.body.offsetHeight+60)+",width="+(document.body.offsetWidth+170)+",top=0,left=0,toolbar=yes,menubar=yes,scrollbars=yes, resizable=yes,location=yes, status=yes");
        }
	},
	 enabledButton:function(){
    	templateTool_me.enabledButton("template_rejectButton");
		templateTool_me.enabledButton("template_applyButton");
		templateTool_me.enabledButton("template_submitButton");
	 }
 });
