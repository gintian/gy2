/**
 * 查看问卷模板
 * changxy 20160808
 */
 Ext.define('QuestionnairePlan.SeeTemplate',{
       requires:["QuestionnaireTemplate.QuestionnaireBuilder"],
       funcpriv:undefined,
       viewport:undefined,
       container:undefined,
       constructor:function(config){
       	 Ext.apply(this,config);
         this.createMainPanel();
       },
 	   createMainPanel:function(){
 	   	var me=this;
 	   	var map=new HashMap();
 	   	map.put('SeeTemplate','SeeTemplate');
 	      Rpc({functionId:'QN20000001',async:false,success:me.getConfig,scope:me},map);
 	   },
 	   getConfig:function(res){
 	    var me = this;
        var param = Ext.decode(res.responseText);
        me.container.remove(me.mainPanel);
        me.tableObj = new BuildTableObj(param.configStr);
        me.container.add(me.tableObj.getMainPanel());
        me.tableObj.insertItem({
            xtype:'box',
            border:'0 0 0 0',
            padding:'10 0 0 5',
            html:'<span>问卷分类&nbsp;&nbsp;:&nbsp;&nbsp;' +
                   '<a id="template_all" href=javascript:QN_global.searchTemplateLib(\"all\");>全部</a>&nbsp;&nbsp;'+
                   '<a id="template_1" href=javascript:QN_global.searchTemplateLib(\"1\");>调查</a>&nbsp;&nbsp;'+
                   '<a id="template_2" href=javascript:QN_global.searchTemplateLib(\"2\");>投票</a>&nbsp;&nbsp;'+
                   '<a id="template_3" href=javascript:QN_global.searchTemplateLib(\"3\");>评估</a>&nbsp;&nbsp;'+
                   '<a id="template_4" href=javascript:QN_global.searchTemplateLib(\"4\");>表单</a></span>&nbsp;'
        },0);
       var titlebar= me.tableObj.getTitleBar();
       var tools=Ext.widget("tool",{//添加返回 关闭
                type:'close',
                handler : function() {
                     me.goBackFn();
                    }
       });
       titlebar.add(tools);
        Ext.get('delbtn').on('click',function(){me.delTemplate();});
        Ext.get('savebtn').on('click',function(){me.saveData();});
        //Ext.get('backbtn').on('click',function(){me.goBackFn()});
	    //zhangh 2020-2-12 【57916】点击【查看模板】按钮，进入到查看模板界面，默认显示全部的问卷模板，应该有下划线定位到问卷分类中的全部
	    Ext.getDom('template_all').style.textDecoration='underline';
 	   },
 	   delTemplate:function(){
 	      var me=this;
 	      var selectDataList=me.tableObj.tablePanel.getSelectionModel().getSelection();
 	      if(selectDataList.length<1){
 	          Ext.Msg.alert("提示信息","请选择数据");
 	          return;
 	      }else{
 	      	  var ids="";
 	          for(var i=0;i<selectDataList.length;i++){
 	              if(selectDataList[i].data.canedit==0){
 	                  Ext.showAlert("您不是["+selectDataList[i].data.qnname+"]模板的创建人，不允许删除此模板。");
 	                  return;
                  }
 	              ids+=selectDataList[i].data.qnid;
 	              if(i<selectDataList.length-1)
 	                  ids+=",";
 	          }
 	      var map=new HashMap();
          map.put('SeeTemplate','SeeTemplate');
          map.put('planids',ids);
          Ext.Msg.confirm("提示信息","确定要删除数据？",function(btn){
            if(btn=='yes'){
              Rpc({functionId:'QN10000004',async:false,success:QN_global.busiCallBack,scope:me},map);
            }
          });
 	      }
 	   },saveData:function(){
 	   	  var store=Ext.data.StoreManager.lookup("qnLib_dataStore");
 	   	  var updateList=store.getModifiedRecords();
 	   	  var updaterecord=[];
 	   	  if(updateList.length<1)
 	   	      return;
 	   	  else{
 	   	      for(var i=0;i<updateList.length;i++){
 	   	          var record=updateList[i].data;
 	   	          var qnname=record.qnname;
 	   	          //去除空格
                  qnname = trim(qnname);
 	   	          if(qnname==""){
 	   	            Ext.Msg.alert("提示信息","问卷模板名称不能为空");
 	   	            return;
 	   	          }
                  //【58223】V771问卷调查：查看模板，共享状态改为私有，所属机构没有清空
                  //是否共享，1：共享，0：私有
                  var isshare = record.isshare;
                  if(isshare =='0'){
                      //当模板设置为私有时，所属机构就没用了，需要清空
                      record.b0110 = '';
                  }
 	   	          updaterecord.push(record);
 	   	      }
 	   	  }
 	   	var hashvo = new HashMap();
        hashvo.put("updaterecord",updaterecord);
        hashvo.put("SeeTemplate","SeeTemplate");
        Rpc({functionId:'QN20000003',scope:this,success:function(res){
                var resultObj = Ext.decode(res.responseText);
                if(resultObj.result!=undefined && !resultObj.result){
                    Ext.Msg.alert('提示信息',"保存失败！");
                    return;
                }
                //xiegh 2017/3/15 模板名字有重复时不允许保存,前台给出提示信息
        		if(resultObj.errorMsg){
        		     Ext.showAlert(resultObj.errorMsg);
        		     return;
        		}
                Ext.Msg.alert('提示信息',"保存成功！");
                var store = Ext.data.StoreManager.lookup('qnLib_dataStore');
                store.reload();
            }},hashvo); 
 	   }
 });