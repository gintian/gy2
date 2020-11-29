		/*插入公式弹出窗口*/
	function addFormat(flag,filed_id){
		 var store = Ext.create('Ext.data.SimpleStore',{
 	  		 	 fields:['id','dateFormula'],  
           		 data:[['1','1980.07.09'],['2','1980.7.9'],
           		 ['3','80.7.9'],['4','1983.02'],
           		 ['5','1982.5'],['6','80.07'],
           		 ['7','80.7'],['8','1987年03月09日'],['9','1982年7月9日'],
           		 ['10','1987年07月'],['11','1987年7月'],
           		 ['12','80年07月09日'],['13','80年7月9日'],
           		 ['14','80年7月'],['15','年限'],
           		 ['16','年份'],['17','月份'],
           		 ['18','日份']]
 	  		 });
 	  		 store.load();
 	  		 var title = "插入公式"
 	  		 if(flag==2)
 	  			title = "修改公式"
			 var num = Ext.create('Ext.form.NumberField',{
			    name: "num",
                columnWidth:0.3,
                fieldLabel: "长度",
                labelWidth:50,
                labelAlign:'left',
                id:'numId',
               	maxValue: 250,
                minValue: 0,
                value: 1,
                border:false,
                hideTrigger: false,
                keyNavEnabled: true,
                mouseWheelEnabled: true,//取消鼠标滚动的效果
                step: 1
			 });
			 
			 var dateFormula = Ext.create('Ext.form.ComboBox',{
			     id:'dateId',
			     labelWidth:60,
                 labelAlign:'left',
			     columnWidth:0.4,
			 	 store:store,
			 	 displayField:'dateFormula', 
			 	 valueField:'id',
			 	 fieldLabel:'日期格式',
			 	 mode : 'local', // 设置local，combox将从本地加载数据
			     triggerAction : 'all',// 触发此表单域时,查询所有
				 selectOnFocus : true,
				 anchor : '90%',
				 typeAhead : true,// 设置true，完成自动提示 
				 blankText:'请选择……',
				 forceSelection : true 
			 });
			 var numInteger = Ext.create('Ext.form.NumberField',{
			      name: "numInteger",
                  columnWidth:0.3,
                  fieldLabel: "整数位",
                  labelWidth:50,
                  labelAlign:'left',
                  id:'integerId',
               	  maxValue: 100,
            	  minValue: 1,
                  value: 8,
                  border:false,
                  hideTrigger: false,
                  keyNavEnabled: true,
                  mouseWheelEnabled: true,//取消鼠标滚动的效果
                  step: 1
			 });
			 var numDecimal = Ext.create('Ext.form.NumberField',{
			      name: "numDecimal",
                  columnWidth:0.3,
                  fieldLabel: "小数位",
                  labelWidth:50,
                  labelAlign:'left',
                  id:'decimalId',
                  maxValue: 100,
                  minValue: 0,
                  value: 0,
                  border:false,
                  hideTrigger: false,
                  keyNavEnabled: true,
                  mouseWheelEnabled: true,//取消鼠标滚动的效果
                  step: 1
			 });
			var fielditemStore = Ext.create('Ext.data.SimpleStore',{
 	  		 	 fields:['fieldSetId','fieldSetDesc'],  
           		 data:[['A01','人员基本信息']] 
 	  		 });
			var win = new Ext.Window({
				title: title,  
                width: 720,  
                height: 510,  
                id:'fomulaWinId',
                resizable: false,  
                modal: true,  
                //closable: true,  
                items:[{
                	xtype:'form',
                	itemId:'inputForm',
			   		header:false,
			 		layout: {
			      	  type: 'vbox',
			       	  align: 'left',
			       	  padding:'20 300 0 50'
			        },
			    	border:false,
			        items:[{
			        	xtype:'textfield',
				  		fieldLabel:'公式标题',
				  		//labelSeparator:null,
				  		name:'name',
				  		maxLength:50,
				  		id:'formulaTitleId',
						enforceMaxLength:true,
				   		labelWidth:60,
				  		allowBlank:false,
				  		width:600,
				  		labelAlign:'left',
				  		labelSeparator:null,
				  		beforeLabelTextTpl:"<font color='red'> * </font>",
				  		 listeners:{
          			     	'change':function(){
          			     	  var fieldtitle=Ext.getCmp('formulaTitleId').getValue();
          			     	  var fieldcontent=Ext.getCmp('formulaId').getValue();
          			     	  if(fieldcontent!=null&&trim(fieldcontent).length>0 &&fieldtitle!=null&&trim(fieldtitle).length>0)
						      {
						      		//Ext.getCmp('formulaSaveButton').setDisabled(false);
						      }else{
						      		//Ext.getCmp('formulaSaveButton').setDisabled(true);
						      }
          			     	}
          			     }
				  		
			        },{
			       		 xtype:'panel',
			       		 layout:'column',
			       		 width:600,
			       		 id:'radionPanel',
			       		 style:'margin-top:10px',
			       		 border:false,
			       		 items:[{
			       		 	xtype:'radiogroup',
			        		fieldLabel:'公式类型',
			        		columnWidth:0.4,
			          	    labelWidth:60,
			          	    width:200,
			          	    id: 'rd1',
			          	    columns:3,
			          	    vertical: true,
          			        labelAlign:'right',
          			        labelSeparator:null,
			             	items:[ 
			                	{ boxLabel: '字符', name: 'rd', inputValue: '1',checked:true},   
                   				{ boxLabel: '日期', name: 'rd', inputValue: '2' },  
                   				{ boxLabel: '数值', name: 'rd', inputValue: '3' }],
                   			listeners:{
                   				'afterrender':function(){
                   					Ext.getCmp('radionPanel').add(num);
                   					Ext.getCmp('radionPanel').add(dateFormula);
                   					Ext.getCmp('radionPanel').add(numInteger);
                   					Ext.getCmp('radionPanel').add(numDecimal);
                   					Ext.getCmp('numId').setVisible(true);
                   					Ext.getCmp('dateId').setVisible(false);
                   					Ext.getCmp('integerId').setVisible(false);
                   					Ext.getCmp('decimalId').setVisible(false);
                   				},
                   				'change':function(){ 
                   					if(Ext.getCmp('rd1').getChecked()[0].inputValue=='1'){
                   						Ext.getCmp('numId').setVisible(true);
                   						Ext.getCmp('dateId').setVisible(false);
                   						Ext.getCmp('integerId').setVisible(false);
                   						Ext.getCmp('decimalId').setVisible(false);
                   					}
                   					if(Ext.getCmp('rd1').getChecked()[0].inputValue=='2'){
                   						Ext.getCmp('numId').setVisible(false);
                   						Ext.getCmp('dateId').setVisible(true);
                   						Ext.getCmp('integerId').setVisible(false);
                   						Ext.getCmp('decimalId').setVisible(false);
                   					}
                   					if(Ext.getCmp('rd1').getChecked()[0].inputValue=='3'){
                   						Ext.getCmp('numId').setVisible(false);
                   						Ext.getCmp('dateId').setVisible(false);
                   						Ext.getCmp('integerId').setVisible(true);
                   						Ext.getCmp('decimalId').setVisible(true);
                   					}
                   				}
                   				
                   			}
			       		 }]
			        	
			        },{
			        	 xtype:'textarea',
       				     fieldLabel:'公式内容',
       				     name:'bb_text',
       				     id:'formulaId',
         				 width:600,
         				 //IE 下高度不加'px'高度显示不正常
          			     height:'190px',
          			     labelSeparator:null,
          			     labelWidth:60,
          			     labelAlign:'top',
          			     enableKeyEvents:true,
          			     beforeLabelTextTpl:"<font color='red'> * </font>",
          			     listeners:{
          			     	'change':function(){
          			     	  var fieldtitle=Ext.getCmp('formulaTitleId').getValue();
          			     	  var fieldcontent=Ext.getCmp('formulaId').getValue();
          			     	  if(fieldcontent!=null&&trim(fieldcontent).length>0 &&fieldtitle!=null&&trim(fieldtitle).length>0)
						      {
						      		//Ext.getCmp('formulaSaveButton').setDisabled(false);
						      }else{
						      		//Ext.getCmp('formulaSaveButton').setDisabled(true);
						      }
          			     	if(isIE()){
          			     		markBook('formulaId');
      			     		}
          			     	},
          			     	'blur':function(){
          			     		if(!isIE()){
	          			     		markBook('formulaId');
          			     		}
          			     	},
          			     	'click': {
			                    element: 'el',
			                    fn: function(){ 
				        			if(Ext.isIE){
				        				markBook('formulaId');//获得光标位置
				        			}
			        			}
			                },
			                'keypress':function(text,e){
			                	if(Ext.isIE){
			                		markBook('formulaId');
			                	}
			                }
          			     }
          			     
			        }],
			       buttons:[/**{
			       	text:'函数向导',
 	    			width:80,
 	    			height:25,
 	    			style:'float:left;margin-top:0px',
 	    			listeners:{
                	  "click":function(){
                     	 function_Wizardss('','formulaId');//不能起名为function_Wizard  因为validate.js里面也有这个方法，为了避免进入validate.js中的那个方法，重命名
                	  }
             	    }
			       },**/{
			       	text:'函数向导',
			       	width:80,
 	    			height:25,
 	    			style:'float:left;margin-top:0px',
			       	listeners:{
			       		'click':function(){
			       			markBook('formulaId');
			       			Ext.Loader.setPath("functionWizard","/components/functionWizard");
							Ext.require('EHR.functionWizard.FunctionWizard',function(){
								Ext.create("EHR.functionWizard.FunctionWizard",{keyid:'',opt:"4",checktemp:'salary',mode:'xzgl_jsgs',callbackfunc:'getfunctionWizard'});
							})
			       		}
			       	}
			       },{
			       	text:'公式检查',
 	    			width:80,
 	    			height:25,
 	    			style:'margin-right:60px;float:left',
 	    			listeners:{
                	  "click":function(){
                     	 checkformula();
                	  }
             	    }
			       }]
			    
			  },{
			  	xtype:'panel',
			  	itemId:'inputForm2',
			    align: 'left',
			    padding:'0 20 20 50',
     			labelWidth:50,  
      		    buttonAlign:'center',  
    		    width:700,  
			    border:false,
			    items:[{
			    	layout: 'column',
			    	 border:false,
			    	 items:[{
			    	   xtype:'fieldset',
                       title:'参考项目',
                       height:100,
                       columnWidth:.41,  
                       layout:'form',  
                       id:'referitemsid',
                       //collapsible:true,
                       items:[{
                            xtype:'panel',
 	    				   	id:'fieldSetId',
 	    				   	border:false,
 	    				    style:'border-spacing: 4px',
//			                html:'<input id="b_value" type="hidden" name="bb_value"  onpropertychange ="setCodeItem()"/><br/><input type="hidden" name="bb_num" value="1"/><input style="width:230px" type="text" id="b" name="bb_view" plugin="fieldItemselector" fieldItemId="A01" inputname="bb_view"  entityFn="" formula="true" afterfunc="afterFunction()" onclick="clearcodeitem();markBook('formulaId');" onblur="fieldtimeout()" />',
	    				    html:'<input id="b_value" type="hidden" name="bb_value" /><br/>'
	    				    	+ '<input type="hidden" name="bb_num" value="1"/><input type="text"'  
	    				    	+ 'class="x-form-text-default-bb_view" style="height:22px;width:200px;"' 
	    				    	+ 'name="bb_view" /><img id="b" style="margin-left: -19px; cursor: pointer;"' 
	    				    	+ 'class="img-middle" src="/module/recruitment/image/xiala2.png"  inputname="bb_view"'
	    				    	+ 'valuename="bb_num" plugin="fieldItemselector" fieldItemId="A" inputname="bb_view"'  
	    				    	+ 'entityFn="" formula="true" afterfunc="afterFunction" onclick="clearcodeitem();"/>',
	    				    	
 	    				    listeners:{
			                	'render':function(){
			                		var arr=['b'];
			                		setFiledEleConnect(arr);
			                	}
			                }
	 	    				   }]
			    	},{
			    	   xtype:'fieldset',
                       title:'运算符号',
                       layout:'column',  
                       layout:'vbox',  
                       height:100,
                       columnWidth:.56,  
                       style:'margin-left:5px;margin-right:5px;algin:center',  
                       items:[{
                    	   xtype:'container',
                    	   layout:'hbox',
                    	   items:[{xtype:'button',text:'0',listeners:{"click":function(){ gzemail_gettxt(this.text);}} },
	                       		{xtype:'button',text:'1',style:'margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
	                       		{xtype:'button',text:'2',style:'margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
	                       		{xtype:'button',text:'3',style:'margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
	                       		{xtype:'button',text:'4',style:'margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
	                       		{xtype:'button',text:'(',style:'margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
	                       		{xtype:'button',text:'=',style:'margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
	                       		{xtype:'button',text:'>=',style:'margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
	                       		{xtype:'button',text:' 非 ',style:'margin-left:4px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
	                       		{xtype:'button',text:'~',style:'margin-left:4px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
                   				{xtype:'button',text:' 分情况 ',style:'margin-left:4px;',listeners:{"click":function(){ gzemail_gettxt(this.text);}}}
                   				]
                       	},{
                     	   xtype:'container',
                     	   layout:'hbox',
                     	   items:[
                       		{xtype:'button',text:'5',style:'margin-top:5px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
                       		{xtype:'button',text:'6',style:'margin-top:5px;margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
                       		{xtype:'button',text:'7',style:'margin-top:5px;margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
                       		{xtype:'button',text:'8',style:'margin-top:5px;margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
                       		{xtype:'button',text:'9',style:'margin-top:5px;margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
                       		{xtype:'button',text:')',style:'margin-top:5px;margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
                       		{xtype:'button',text:'>',style:'margin-top:5px;margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
                       		{xtype:'button',text:'<=',style:'margin-top:5px;margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
                       		{xtype:'button',text:' 且 ',style:'margin-top:5px;margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
                       		{xtype:'button',text:' 如果 ',style:'margin-top:5px;margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
                       		{xtype:'button',text:' 否则 ',style:'margin-top:5px;margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}}
                       		]
                       	},{
                     	   xtype:'container',
                     	   layout:'hbox',
                     	   items:[
                       		{xtype:'button',text:'+',style:'margin-top:5px;',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
                       		{xtype:'button',text:'-',style:'margin-top:5px;margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
                       		{xtype:'button',text:'*',style:'margin-top:5px;margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
                       		{xtype:'button',text:'/',style:'margin-top:5px;margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
                       		{xtype:'button',text:'\\',style:'margin-top:5px;margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
                       		{xtype:'button',text:'%',style:'margin-top:5px;margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
                       		{xtype:'button',text:'<',style:'margin-top:5px;margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
                       		{xtype:'button',text:'<>',height:22,style:'margin-top:5px;margin-left:3px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
                       		{xtype:'button',text:' 或 ',style:'margin-top:5px;margin-left:2.5px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
                       		{xtype:'button',text:' 那么 ',style:'margin-top:5px;margin-left:2.5px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}},
                       		{xtype:'button',text:' 结束',style:'margin-top:5px;margin-left:2.5px',listeners:{"click":function(){ gzemail_gettxt(this.text);}}}
                       		]
                       	}
                     ]
			    	}]
			    	}]
			    	
			    	
			  }],
			  listeners:{
			  	'afterrender':function(){
			  	/**
			  		Ext.get('formulaId').on('select',function(e,t,eOpts){
			  			console.log(e);
			  			console.log(t);
			  			console.log(eOpts);
			  		});
			  		**/
			  	}
			  },
			   buttonAlign: 'center',
			   buttons:[{
			       	text:'确定',
 	    			width:80,
 	    			height:25,
 	    			//disabled:true,
 	    			id:'formulaSaveButton',
 	    			style:'float:center;margin-top:0px',
 	    			listeners:{
                	  "click":function(){
                	  	var fieldtitle=Ext.getCmp('formulaTitleId').getValue();
					    if(fieldtitle==null||trim(fieldtitle).length==0)
					    {
					      Ext.showAlert("请输入公式标题");
					      return;
					    }
					    var fieldcontent=Ext.getCmp('formulaId').getValue();
				        if(fieldcontent==null||trim(fieldcontent).length==0)
					    {
					      Ext.showAlert("请输入公式内容");
					      return;
					    }
                     	 gzemail_chooseformula(flag,filed_id);
                	  }
             	    }
			       },{
			       	text:'关闭',
 	    			width:80,
 	    			height:25,
 	    			style:'margin-right:60px;float:center',
 	    			listeners:{
                	  "click":function(){
                     	Global.cancelAddFoluma();
                	  }
             	    }
			       }]
			  
			});
			
			win.show();
			//Ext.getCmp('formulaTitleId').focus();
		}


//插入公式 退出按钮
Global.cancelAddFoluma = function(){
	Ext.getCmp('fomulaWinId').close();

}

function symbol(editor,obj){
	Ext.getCmp(editor).focus(); 
	var element = document.selection;
	if (element!=null) {
		var rge = element.createRange();
		if (rge!=null)	
		rge.text=obj;
	}
}
//函数向导
function function_Wizardss(salaryid,formula){
    var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_query=link&salaryid="+salaryid
    				+"&tableid=&checktemp=salary&mode=xzgl_jsgs"; 
    var return_vo="";
    /**
     * window.screen.availWidth		返回当前屏幕宽度(空白空间) 
     * window.screen.availHeight	返回当前屏幕高度(空白空间) 
     */
    var left = (window.screen.availWidth-400-10)/2;//计算窗口距离屏幕左侧的间距  400是窗口宽度，10是边框大小
    var top = (window.screen.availHeight-430-30)/2;//窗口距离屏幕上方的间距         430是窗口高度，30是边框和标题栏大小(20)
    if(Ext.isChrome){//chrome浏览器
    	return_vo=window.open(thecodeurl, "", 
                "width=400px,height=430px,top="+top+",left="+left+",resizable=no,center=yes,scroll=yes,locationbar=no,location=no,status=no");
    }else if(!isIE()&&!Ext.isChrome){//非ie和chrome  主要针对火狐和safari中弹窗的位置
    	return_vo=window.open(thecodeurl, "", 
                "width=400px,height=430px,screenY="+top+",screenX="+left+",resizable=no,center=yes,scroll=yes,locationbar=no,location=no,status=no");
    }else{
    	return_vo= window.showModalDialog(thecodeurl, "", 
    			"dialogWidth:400px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no,location:no");
    }
    if(return_vo!=null)
		symbol(formula,return_vo);
}
function trim(str){ //删除左右两端的空格
　　     return str.replace(/(^\s*)|(\s*$)/g, "");
}

function gzemail_gettxt(obj)
{
   var ieSelectionBookMark = null;
   var wobj = Ext.getCmp('formulaId');
   wobj.focus();
   if(!isIE()){
	  //获取当前光标的位置
	   ieSelectionBookMark = wobj.inputEl.dom.selectionStart;
	}
   
   if(isIE()){
	   if(!isCompatibleIE()){
		   //IE 非兼容模式
		   ieSelectionBookMark = wobj.inputEl.dom.selectionStart;
	   }else{
		   //获取当前光标的位置
		   var rangeObj = document.selection.createRange();
		   ieSelectionBookMark = rangeObj.getBookmark();
	   }
   }
	
   insertTxt("4",trim(obj),"formulaId","",ieSelectionBookMark);
}

function insertTxt(type,str,obj,id,ieSelectionBookMark)
{
   if(str==null)
   {
       return;
   }
   if(parseInt(type)==1)
   {
       var strtxt="$"+id+":"+str+"$";
    }
    if(parseInt(type)==2)
    {
       var strtxt=" "+str+" ";
    }
    if(parseInt(type)==3)
    {
       var strtxt="#"+id+":"+str+"#";
    }
    if(parseInt(type)==4)
    {
       var strtxt=str;
    }
    if(parseInt(type)==5)
    {
       var strtxt='"'+str+'"';
    }
   Ext.getCmp(obj).focus();
   
   if(isIE()){
	   var wobj = Ext.getCmp(obj);
	   wobj.focus();
	   if(!isCompatibleIE()){
		   //IE 非兼容模式
		   var orValue=wobj.getValue();
		   ieSelectionBookMark = wobj.inputEl.dom.selectionStart;
		   var newBookMark =  ieSelectionBookMark +strtxt.length;
		   wobj.setValue(orValue.substring(0,ieSelectionBookMark)+strtxt+orValue.substring(ieSelectionBookMark,orValue.length));
		   wobj.inputEl.dom.selectionStart =newBookMark ;
		   wobj.inputEl.dom.selectionEnd =newBookMark;
	   }else{
		   var rangeObj = document.selection.createRange();
		   rangeObj.select();
		   rangeObj.text = strtxt;
	   }   
   }
   
   if(!isIE()){
	   var wobj = Ext.getCmp(obj);
	   var orValue=wobj.getValue();
	   ieSelectionBookMark = wobj.inputEl.dom.selectionStart;
	   var newBookMark =  ieSelectionBookMark +strtxt.length;
	   wobj.setValue(orValue.substring(0,ieSelectionBookMark)+strtxt+orValue.substring(ieSelectionBookMark,orValue.length));
	   wobj.inputEl.dom.selectionStart =newBookMark ;
	   wobj.inputEl.dom.selectionEnd =newBookMark;
   }
	             
   
}

//公式检查
   function checkformula()
   {
       var formula=Ext.getCmp('formulaId').getValue();
		if(formula.length==0){
			return;
		}
       var arr=document.getElementById('b_value').value.split(":");
       var itemid=arr[1];
       var obj=Ext.getCmp('rd1').getChecked()[0].inputValue;
       if(itemid=="#"||itemid==""){
         Ext.showAlert("请选择指标");
         return;
       }
          var fieldtype=""
          if(obj=='1'){
         	 fieldtype="A";
          }
          if(obj=='2'){
         	 fieldtype="D";
          }
          if(obj=='3'){
         	 fieldtype="N";
          }
          if(obj==null)
          {
              fieldtype="L";
          }
		var map = new HashMap();
		map.put("c_expr",getEncodeStr(formula));
	    map.put("itemid",itemid);
	    map.put("type",fieldtype);
	    map.put("flag",'1');
	    map.put("field_id",'1');
	    map.put("salaryid",'-1');
		Rpc({
			functionId :'ZP0000002340',
			success : Global.resultCheckInfo
		}, map);
		
       
   }
  Global.resultCheckInfo=function(response){
 	var value = response.responseText;
	var map = Ext.decode(value);
	var info = map.info;
	if(info=="ok"){
	     Ext.showAlert("公式正确");
	    return;
	}else{
		Ext.showAlert(getDecodeStr(info));
		return;
	}
} 

//公式保存
     function gzemail_chooseformula(flag,field_id)
   {
         var formula=Ext.getCmp('formulaId').getValue();
         var arr=document.getElementById('b_value').value.split(":");
         var itemid=arr[1];
         var obj=Ext.getCmp('rd1').getChecked()[0].inputValue;
         var fieldtype=""
          if(obj=='1'){
         	 fieldtype="A";
          }
          if(obj=='2'){
         	 fieldtype="D";
          }
          if(obj=='3'){
         	 fieldtype="N";
          }
          if(obj==null)
          {
              fieldtype="L";
          }
        var map = new HashMap();
		map.put("c_expr",getEncodeStr(formula));
	    map.put("itemid",itemid);
	    map.put("type",fieldtype);
	    map.put("flag",flag);
	    map.put("field_id",getEncodeStr(field_id));
		Rpc( {
			functionId : 'ZP0000002340',
			success : gzemail_choose_ok
		}, map);
   }
   
   //fieldtitle   公式标题或指标名称
   //fieldcontent  公式内容或指标id
   //ndec  小数点位数
   //nflag =0是指标，=1是公式
   function gzemail_choose_ok(response)
   {
		  var value = response.responseText;
		  var map = Ext.decode(value);
		  var info = map.info;
		  var flag = map.flag;
		  var field_id = getDecodeStr(map.field_id);
		  if(info=="ok") {
			  var templateId = Ext.getCmp('tempalteId').getValue();
		      var fieldtitle=Ext.getCmp('formulaTitleId').getValue();
		      var fieldlen="";
	          var ndec="";
	          var dateformat="0";
		      var fieldtype="";
		      if(fieldtitle==null||trim(fieldtitle).length==0)
		      {
		      Ext.showAlert("请输入公式标题");
		      return;
		      }
		      var fieldcontent=Ext.getCmp('formulaId').getValue();
	         if(fieldcontent==null||trim(fieldcontent).length==0)
		      {
		      Ext.showAlert("请输入公式内容");
		      return;
		      }
	          
	          var obj=Ext.getCmp('rd1').getChecked()[0].inputValue;
	          var fieldtype=""
	          if(obj=='1'){
	         	 fieldtype="A";
	         	 fieldlen=Ext.getCmp('numId').getValue();
	         	 ndec=0;   
	         	 dateformat=0;
	          }
	          if(obj=='2'){
	         	 fieldtype="D";
	         	 dateformat=Ext.getCmp('dateId').getValue();
	         	 fieldlen=0;
	         	 ndec=0; 
	          }
	          if(obj=='3'){
	         	 fieldtype="N";
	         	 fieldlen=Ext.getCmp('integerId').getValue();
	         	 ndec=Ext.getCmp('decimalId').getValue();
	         	 dateformat=0;
	         	 
	          }
	          var codeset = "";
	          var nflag = "1";
	          var fieldset="";
	          var  setobj= new Array();
	          setobj[0]=fieldtitle;
	          setobj[1]=fieldcontent;
	          setobj[2]=fieldtype;
	          setobj[3]=fieldlen;
	          setobj[4]=ndec;
	          setobj[5]=dateformat;
	          setobj[6]=codeset;
	          setobj[7]=nflag;
	          setobj[8]=fieldset;
	          //insertTxt("3",fieldtitle,'contentId',fieldid); 
	          //var reg = new RegExp("#[0-9]+:[a-zA-Z0-9\u4e00-\u9fa5]+#", "g");
	          //var context = Ext.getCmp('contentId').getValue();
			 // var arr = context.match(reg);
			 // var arrField = new Array();
			 // for(var i = 0;i<arr.length;i++){
			  //	arrField[i]= arr[i].substring(arr[i].indexOf("#")+1,arr[i].indexOf(":"));
			  //}
			  //fieldid = parseInt(Math.max.apply(null, arrField))+1;
	          var arrValue = "";
	          if(flag=='1'){
	          	  setobj[9]=fieldid;
	         	  var strtxt="#"+fieldid+":"+fieldtitle+"#";
				
				 var wobj = Ext.getCmp("contentId");
			     if(isIE()){
			    	 wobj.focus(true);
			    	 var orValue=wobj.getValue();
			    	 if(!isCompatibleIE()){
							//IE 非兼容模式
							wobj.setValue(orValue.substring(0,orValue.length-4)+trim(strtxt));
							if(orValue.substring(orValue.length-13)=="<P>&nbsp;</P>"){
								wobj.setValue(orValue.substring(0,orValue.length-13)+trim(strtxt));
							}else if(orValue.substring(orValue.length-4)=="</P>"){
								wobj.setValue(orValue.substring(0,orValue.length-4)+trim(strtxt)+"</P>");
							}else {
								wobj.setValue(orValue.substring(0)+trim(strtxt));
							}
					 }else{
				    	var rge = range;//通过替换光标保证位置正确
						if (rge!=null&&rge.offsetLeft!=0)//用户点击过计算公式部分 offsetLeft==0说明该对象未被渲染到当前页面上
						{ 
							rge.text=strtxt;
							rge.select();
						}
						else
						{//用户未点击过计算公式部分需要创建一个range。让用户选择的选项在开头插入
							var element = document.selection;
							if (element!=null) {
								var rge = element.createRange();
								if (rge!=null)	
								{ 
									rge.text=strtxt;
									rge.select();
								}
							}
						}
						range=rge;
					 }
				  }
			 	 if(!isIE()){
			 		wobj.focus();
					wobj.insertAtCursor(strtxt);
				  }
	             
			 	 
			 	  var wobj = Ext.getCmp("contentId");
			 	  wobj.focus();
			 	  var txt =wobj.getValue();
			 	  if(txt.indexOf(strtxt) != -1 ){
			 		  formula_array[parseInt(fieldid)] = setobj;
			 		  arrValue =templateId+"`"+fieldid+"`"+fieldtitle+"`"+fieldtype+"`"+fieldcontent+"`"+dateformat+"`"+fieldlen+"`"+ndec+"`"+codeset+"`"+fieldset+"`"+nflag;
					  fieldid=parseInt(fieldid)+1;  
					  email_array.push(getEncodeStr(arrValue));
			 	  }
		                
	          }
	          if(flag=='2'){
	         	 var reg = new RegExp("#"+field_id+":[a-zA-Z0-9\u4e00-\u9fa5]+#");
	         	 var context = Ext.getCmp('contentId').getValue();
	         	 var strtxt="#"+field_id+":"+fieldtitle+"#";
			 	 var arr = context.replace(reg,strtxt);
			 	 Ext.getCmp('contentId').setValue(arr);
	             setobj[9]=field_id;
	          	 formula_array[parseInt(field_id)] = setobj;
	          	 arrValue =templateId+"`"+field_id+"`"+fieldtitle+"`"+fieldtype+"`"+fieldcontent+"`"+dateformat+"`"+fieldlen+"`"+ndec+"`"+codeset+"`"+fieldset+"`"+nflag;
	          	 email_array.push(getEncodeStr(arrValue));
	          }
	         // Ext.getCmp('contentId').getDoc().focus(true,true);
	        //  Ext.getCmp('contentId').toggleSourceEdit(true);
	         // Ext.getCmp('contentId').insertAtCursor(strtxt);
	        //  Ext.getCmp('contentId').toggleSourceEdit(false);  
	         
			
			  arrValue=undefined;
			  setobj=undefined;
			  Ext.getCmp('fomulaWinId').close();
		  }else{
		  	Ext.showAlert(getDecodeStr(info));
			return;
		  }
 }
  function afterFunction(itemid,text){
  	if(itemid!=null && itemid.length>0 && itemid!=''){
	 	var map = new HashMap();
   			map.put("itemid",itemid);
			Rpc( {
				functionId : 'ZP0000002338',
				success : Global.searchCode
			}, map);
 	}else{
 		return;
 	}
 	//insertTxt("2",trim(text),"formulaId","",ieSelectionBookMark);
 	var wobj = Ext.getCmp("formulaId");
	wobj.focus();
	if(isIE()){
		if("ActiveXObject" in window){
			var orValue=wobj.getValue();
			wobj.setValue(orValue.substring(0,startMark)+trim(text)+orValue.substring(endMark,orValue.length));
//   			wobj.setValue(orValue+trim(text));
		}else{
			var rangeObj = document.selection.createRange();
			rangeObj.moveToBookmark(ieSelectionBookMark);
			rangeObj.select();
			rangeObj.text = trim(text);
		}
	}
	if(!isIE()){
		//wobj.insertAtCursor(trim(text));
   		var orValue=wobj.getValue();
   		wobj.setValue(orValue.substring(0,startMark)+trim(text)+orValue.substring(endMark,orValue.length));
	}
 
 }
 Global.searchCode = function(response){
 	var value = response.responseText;
	var map = Ext.decode(value);
	codesetid = map.codesetid;
	if(Ext.isDefined(Ext.getCmp('addpanelid'))){
	 	return;
	}
	if(!Ext.isEmpty(codesetid) && codesetid!="0"){
	 	var panel = Ext.create('Ext.form.Panel',{
	 		border:false,
			width:230,
	 		id:'addpanelid',
	 		style:'border-spacing:4px;',
	 	    items:[{
		 	  xtype:'codecomboxfield',
			  border:false,
  			  width:200,
			  id:'codeitemId',
			  codesetid:map.codesetid,
			  listeners:{
				'select':function(pick,value){
					var wobj = Ext.getCmp("formulaId");
				    wobj.focus();
				     if(isIE()){
				     	if("ActiveXObject" in window){
							var orValue=wobj.getValue();
							wobj.setValue(orValue.substring(0,startMark)+'"'+trim(pick.getValue().split('`')[0])+'"'+orValue.substring(endMark,orValue.length));
//				   			wobj.setValue(orValue+'"'+trim(pick.getValue().split('`')[1])+'"');
						}else{
							var rangeObj = document.selection.createRange();
							rangeObj.moveToBookmark(ieSelectionBookMark);
							rangeObj.select();
							rangeObj.text = '"'+trim(pick.getValue().split('`')[0])+'"';
						}
					  }
				 	 if(!isIE()){
						var orValue=wobj.getValue();
   						wobj.setValue(orValue.substring(0,startMark)+'"'+trim(pick.getValue().split('`')[0])+'"'+orValue.substring(endMark,orValue.length));
					  }
				},
				'render':function(){
					Ext.get('codeitemId').on('click',function(){
						
					});
				}
			  }
	 	    }]
	 	});
	 	Ext.getCmp('referitemsid').insert(panel);
	}
 }
 var startMark=0;
 var endMark=0;
 var startEdit=0;
 var endEdit=0;
 var ieSelectionBookMark=null;
 var range=null;//光标的范围，内容，所在区域的范围等等
 function markBook(id){
 	ieSelectionBookMark = null;
    var wobj = Ext.getCmp(id);
    if(!isIE()){
	  //获取当前光标的位置
	   startMark = wobj.inputEl.dom.selectionStart;
	   endMark = wobj.inputEl.dom.selectionEnd;
	 }
	 if(isIE()){
		 var re = "";
		 var rc = "";
		 var reEdit = "";
		 var rcEdit = "";
		 if(id=="contentId"){
			 wobj.focus();
			 var a = window.frames["contentId-inputCmp-iframeEl"].document;
			 var ccText = window.document.getElementsByName("cc_text")[0];
			 reEdit = a.body.createTextRange();
			 //reEdit = a.selection.createRange(); //选中内容
			 rcEdit = reEdit.duplicate(); //所有内容
			 if(a.selection){
					var r = a.selection.createRange();
					range = r;
					if (r == null) { 
						startMark = 0; 
					    } 
					//定位到指定位置
					reEdit.moveToBookmark(r.getBookmark()); 
					//【为了保持选区】rc的开始端不动，rc的结尾放到re的开始
					rcEdit.setEndPoint('EndToStart', reEdit); 
				}
			 startEdit = rcEdit.text.length; 
		     endEdit = startEdit + reEdit.text.length;
		 }else{
			var el = wobj.inputEl.dom;
			el.focus();
			re = el.createTextRange(); //选中内容
			rc = re.duplicate(); //所有内容
			if(Ext.getDoc(id).dom.selection){
				var r = Ext.getDoc(id).dom.selection.createRange();
				if (r == null) { 
					startMark = 0; 
				    } 
				//定位到指定位置
				re.moveToBookmark(r.getBookmark()); 
				//【为了保持选区】rc的开始端不动，rc的结尾放到re的开始
				rc.setEndPoint('EndToStart', re); 
			}
		    startMark = rc.text.length; 
		    endMark = startMark + re.text.length;
		 }
//		 //定位到指定位置
//	    re.moveToBookmark(r.getBookmark()); 
//	    //【为了保持选区】rc的开始端不动，rc的结尾放到re的开始
//	    rc.setEndPoint('EndToStart', re); 
//	    if(id=="contentId"){
//	    	startEdit = rcEdit.text.length; 
//	    	endEdit = startMark + reEdit.text.length;
//	    }else{
//		    startMark = rc.text.length; 
//		    endMark = startMark + re.text.length;
//	    }
//		 if("ActiveXObject" in window){
//			//获取当前光标的位置
//		   startMark = document.selection.selectionStart;
//		   endMark = document.selection.selectionEnd;
//		 }else{
//		 //获取当前光标的位置
//			var rangeObj = document.selection.createRange();
//			ieSelectionBookMark = rangeObj.getBookmark();
//			startMark = rangeObj.text;
//		 
//		 }
	 }
 }
 function clearcodeitem(){ 
 	if(Ext.isDefined(Ext.getCmp("addpanelid")))
 		Ext.getCmp("addpanelid").destroy(); 
 }
 function getfunctionWizard(obj){
 	var wobj = Ext.getCmp("formulaId");
	if(isIE()){
		wobj.focus();
		if("ActiveXObject" in window){
			var orValue=wobj.getValue();
			wobj.setValue(orValue+trim(obj));
		}else{
			var rangeObj = document.selection.createRange();
			rangeObj.moveToBookmark(ieSelectionBookMark);
			rangeObj.select();
			rangeObj.text = obj;
		}
	}
	if(!isIE()){
		var orValue=wobj.getValue();
   		wobj.setValue(orValue.substring(0,startMark)+obj+orValue.substring(endMark,orValue.length));
	}
 }
  