/**
 * 
 * 查询浏览虚拟组织成员
 * 
 */
 Ext.define('VirtualorgURL.VirtualMember',{
       funcpriv:undefined,
       viewport:undefined,
       container:undefined,
       constructor:function(config){
         Ext.apply(this,config);
         this.createMainPanel();
       },createMainPanel:function(){
       	     var me=this;
       	     me.code;
       	    var hrefs=self.location.href;
                if(hrefs.indexOf("&")>-1)
                    me.code=hrefs.split("&")[1]/*.split("=")[1]*/;
             var map=new HashMap();
             map.put("code",me.code);
                     Rpc({functionId:'ORG0000016',async:false,success:me.getConfig,scope:me},map);
                
       },getConfig:function(res){
             var me = this;
            var param = Ext.decode(res.responseText);
           // me.container.remove(me.mainPanel);
            me.tableObj = new BuildTableObj(param.config);
       	    me.tableObj.toolBar.insert(2,{
       	                                                  xtype: 'checkbox',
       	                                                  fieldLabel: ' 显示当前机构所有人员',
       	                                                  labelWidth:120,
       	                                                  id:'radioselect',
       	                                                  checked:true,
       	                                                  listeners:{
       	                                                        click: {
                                                                        element: 'el', 
                                                                        fn: function(){
                                                                        	       var flag=Ext.getCmp('radioselect').checked;
                                                                        	           var map=new HashMap();
                                                                        	           if(flag)
                                                                        	           map.put('checkflag',"1");
                                                                        	           else
                                                                        	           map.put('checkflag',"0");
                                                                        	           map.put('code',me.code);
                                                                        	           Rpc({functionId:'ORG0000017',async:false,success:function(){
                                                                        	                me.tableObj.tablePanel.getStore().reload();
                                                                        	           },scope:me},map);
                                                                        	}
                                                                    }
       	                                                  }
       	                                           });
       }
 })