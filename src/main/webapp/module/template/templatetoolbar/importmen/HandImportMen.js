/**
 * 人事异动-手工选择
 */
Ext.define('TemplateImportmenUL.HandImportMen',{
        salaryid:'',
        thisHandImport:'',
        constructor:function(config){
			thisHandImport = this;
			thisHandImport.tab_id = config.tabid;
			thisHandImport.view_type=config.view_type;
			thisHandImport.infor_type=config.infor_type;
			thisHandImport.orgId=config.orgId;
			thisHandImport.deprecate=config.deprecate;
			thisHandImport.nbases=config.nbases;//进入时人员库
			// 按检索条件和人员范围 begin
			thisHandImport.isPrivExpression=true;
			if(config.isPrivExpression!=null&&typeof(config.isPrivExpression)!='underfined'&&!config.isPrivExpression)////是否启用人员范围
			{
				thisHandImport.isPrivExpression=config.isPrivExpression;
				thisHandImport.orgId='';
			}
			// 按检索条件和人员范围 end
			thisHandImport.sqlwhere_factor = "";
			if (thisHandImport.infor_type=="1"){
			  	thisHandImport.createHandImportMen();
			}
			else if(thisHandImport.infor_type=="2"){//UM
				thisHandImport.sqlwhere_factor = config.sqlwhere_factor;
				thisHandImport.createHandImportOrg('UN,UM');
			}
			else if(thisHandImport.infor_type=="3"){//@K
				thisHandImport.sqlwhere_factor = config.sqlwhere_factor;
				thisHandImport.createHandImportOrg('UN,UM,@K');
			}
			if(thisHandImport.nbases==-1){
				Ext.showAlert("您没有模板设置的进入时人员库权限。");
			}
        },
        createHandImportMen:function(){
        	var f = document.getElementById("getHandTemp");
        	//控制是选人还是选择组织机构
        	this.addu=true;
        	if(thisHandImport.infor_type=='1'){
        		this.addu=false;
        	}
        	//调用选人控件        	
			var p = new PersonPicker({
				addunit:this.addu, //是否可以添加单位
				adddepartment:this.addu, //是否可以添加部门
				multiple: true,//为true可以多选
				orgid:thisHandImport.orgId,
				isPrivExpression:thisHandImport.isPrivExpression,//是否启用人员范围（含高级条件）
				extend_str:"template/"+thisHandImport.tab_id,
				validateSsLOGIN:false,//是否启用认证库
				selectByNbase:true,//是否按不同人员库显示
				deprecate : thisHandImport.deprecate,//不显示的人员
				nbases:thisHandImport.nbases,
				text: "确定",
				callback: function (c) {
					var staffids = "";
					for (var i = 0; i < c.length; i++) {
						staffids += c[i].id + "'";
					}
					thisHandImport.importData(staffids);
				}
			}, f);
			p.open();
        },
        createHandImportOrg:function(codesetid){
        		var map = new HashMap();
			    map.put('codesetidstr',codesetid);
				map.put('codesource','');
				map.put('nmodule','4');
				map.put('ctrltype','3');
				map.put('parentid','');
				map.put('searchtext',encodeURI(""));
				map.put('multiple',true);
				map.put('isencrypt',true);
				map.put('confirmtype','1');
				map.put('sqlwhere',thisHandImport.sqlwhere_factor)
				map.put('callbackfunc',thisHandImport.getOrgList);
				Ext.require('EHR.orgTreePicker.OrgTreePicker', function(){          
					Ext.create('EHR.orgTreePicker.OrgTreePicker',{map:map});
				},this);
        },
        getOrgList:function(record){
        	var staffids = "";
        	for(var i=0;i<record.length;i++){
        		if(thisHandImport.infor_type=="3"){
        			if(record[i].codesetid.indexOf("UN")!=-1||record[i].codesetid.indexOf("UM")!=-1){
        				Ext.showAlert("您只能选择岗位！请不要选择单位或部门！");
        				return;
        			}
        		}
        		staffids += record[i].id +"'";
        	}
        	thisHandImport.importData(staffids);
        },
        importData:function(ids){
        	var hashvo = new HashMap();
			hashvo.put("tab_id",thisHandImport.tab_id);
			hashvo.put("ids",ids);
			hashvo.put("infor_type",thisHandImport.infor_type);
			if(ids.length>0){
				Rpc( {functionId : 'MB00002005',async:false,success:function(form,action){//
					var result = Ext.decode(form.responseText);	
					if(!result.succeed){
			            Ext.showAlert(result.message);
			            return;
		            }else{
		            	var flag=result.flag;
						if(flag){
							templateTool_me.refreshAll("false");//刷新列表
						}else{//捕捉后台抛出的异常 throw new GeneralException(ResourceFactory.getProperty("xxxxx"));
							Ext.showAlert("无此人或者没有权限！");
						}
		            }
		 		}}, hashvo);
			}
        }
 });