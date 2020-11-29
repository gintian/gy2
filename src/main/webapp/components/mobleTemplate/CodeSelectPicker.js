Ext.define('EHR.mobleTemplate.CodeSelectPicker',{
	extend:'Ext.picker.Picker',
	xtype:'codeselectpicker',
	config:{
		codesetid:'',
	    ctrltype:'',
	    nmodule:'',
	    doneButton:'<font style="font-size:18px;">确定</font>',
	    cancelButton:'<font style="font-size:18px;">取消</font>'
	},
	initialize:function(){
		this.callParent();
		this.loadData();
	},
	loadData:function(){
   		var vo = new HashMap();
        vo.put('codesetid',this.getCodesetid());
        vo.put('ctrltype',this.getCtrltype());
        vo.put('nmodule',this.getNmodule());
        vo.put('node','root');
   		Rpc({functionId:'ZJ100000131',success:this.insertItem,scope:this},vo);
	},
	insertItem:function(response){
		var result = Ext.decode(response.responseText);
        var child = result.children;
        var data = new Array();
        data.push({'text':'','value':''});
        for(var i=0 ; i < child.length ; i++){
        	data.push({'text':child[i].itemdesc,'value':child[i].id});
        }
        var vo = new HashMap();
        vo.put('name',this.getCodesetid());
        vo.put('title',this.getCodesetid());
        vo.put('data',data);
        vo.put('align','center')
        this.config.data = data;
        this.setStyle('font-size:16px;');
        this.setSlots(vo);
	},
	getError:function(resp){
		console.log(resp);
	}
	
});