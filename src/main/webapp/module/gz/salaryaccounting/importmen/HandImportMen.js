/**
 * 手工引入
 * lis 2010-10-10
 */

Ext.define('SalaryUL.importmen.HandImportMen',{
        salaryid:'',
        thisHandImport:'',
        constructor:function(config){
			thisHandImport = this;
			thisHandImport.salaryid = config.salaryid;    
			thisHandImport.cbase = config.cbase; 
			thisHandImport.Scope=config.Scope;//手工引入是否走人员范围加高级 0不走 1走
			thisHandImport.orgid = config.orgid;
            thisHandImport.appdate=config.appdate;
            thisHandImport.createHandImportMen();
        },
        createHandImportMen:function(){
        	var f = document.getElementById("handImport");
        	var extend_str="salaryid="+thisHandImport.salaryid+";appdate="+thisHandImport.appdate;

        	//调用选人控件
			var p = new PersonPicker({
				multiple: true,
				nbases:thisHandImport.cbase,
				isPrivExpression:thisHandImport.Scope=='1'?true:false,
				selectByNbase:true,
				orgid:thisHandImport.orgid,
				text: gz.label.ok,//"确定",
				callback:thisHandImport.importData,
                extend_str:extend_str,
                beforeFinishVerifyFunc: function (c) {
					var staffids = "";
					for (var i = 0; i < c.length; i++) {
						staffids += c[i].id + "'";
					}
					if(staffids==""){
                        Ext.getCmp('person_picker_multiple_view').close();
						return;
					}
					var hashvo = new HashMap();
					hashvo.put("salaryid",salaryid);
					hashvo.put("ids",staffids);
					hashvo.put("actionType","check");
					Ext.MessageBox.wait(gz.msg.handImportDataCheck,gz.label.wait);//数据校验中...
					Rpc( {functionId : 'GZ00000031',async:false,success:function(form,action){
						Ext.MessageBox.close();
						var result = Ext.decode(form.responseText);	
						var flag=result.succeed;
						if(flag==true){
							var msg=result.msg;
							if(msg=="OK"){
                                thisHandImport.importData(c);
                                Ext.getCmp('person_picker_multiple_view').close();
							}else{
								//存在重复的人
								Ext.showConfirm(msg,function (oper) {
									if(oper=='yes') {
                                        thisHandImport.importData(c);
                                    }else{
                                        return false;
									}
                                    Ext.getCmp('person_picker_multiple_view').close();
                                },this);
							}
						}else{//捕捉后台抛出的异常 throw new GeneralException(ResourceFactory.getProperty("xxxxx"));
							Ext.showAlert(result.message);
						}
						return false;
				 }}, hashvo);
				}
			}, f);
			p.open();
        },
	importData:function (c) {
        var staffids = "";
        for (var i = 0; i < c.length; i++) {
            staffids += c[i].id + "'";
        }
        var hashvo = new HashMap();
        hashvo.put("salaryid",salaryid);
        hashvo.put("ids",staffids);
        hashvo.put("actionType","import");
        Ext.MessageBox.wait(gz.msg.handImportImportData, gz.label.wait);//人员添加中...
        Rpc( {functionId : 'GZ00000031',success:function(form,action){
            Ext.MessageBox.close();
            var result = Ext.decode(form.responseText);
            var flag=result.succeed;
            var ff_bosdate=result.ff_bosdate;
            var count = result.count;
            if(flag==true){
                accounting.appdate = ff_bosdate;//业务日期  这块只限薪资发放调用，别处会报错，加入的时候只是薪资发放调用了  zhaoxg add 2016-10-10
                accounting.count = count;//次数
                GzGlobal.reloadStore();
            }else{//捕捉后台抛出的异常 throw new GeneralException(ResourceFactory.getProperty("xxxxx"));
                Ext.showAlert(result.message);
            }
        }}, hashvo);

    }
 });