/**
 * 
 */
Ext.define('OfficerMange.MonthPicker',{
	extend:'Ext.form.field.Date',
	xtype:'customMonthPicker',
	format:undefined,
	constructor:function(config){
		this.callParent([config])
		if(!this.format||this.format==''){
			this.format='Y.m'
		}
	},
	createPicker : function(){
			var me=this
			var month=Ext.create({
			    xtype: 'monthpicker',
			    floating:true,
			    shadow:false,
			    preventRefocus: true,
	            hidden: true,
			    onSelect: function() {
			    	var date=this.getValue()[1]+"."+((this.getValue()[0]+1)<10?("0"+(this.getValue()[0]+1)):(this.getValue()[0]+1))
			    	if(me.format==='Y.m'){
			    		date=this.getValue()[1]+"."+((this.getValue()[0]+1)<10?("0"+(this.getValue()[0]+1)):(this.getValue()[0]+1))
			    	}else{
			    		date=this.getValue()[1]+((this.getValue()[0]+1)<10?("0"+(this.getValue()[0]+1)):(this.getValue()[0]+1))
			    	}
			    	me.setValue(date)
			    	this.setHidden(true)
			    },
			    listeners: {
			        okclick: 'onSelect',
			        monthdblclick: 'onSelect',
			        yeardblclick: 'onSelect',
			        cancelclick: function () {
			        	this.setHidden(true)
			        }
			    }
			});
			return month
	},
	safeParse:function(value, format){
		if(!value)
			return
		if(Ext.Date.parse(value,this.format))
		return Ext.Date.clearTime(Ext.Date.parse(value,this.format))
	},
	onExpand:function(){
		 this.picker.setValue(this.value, true);
	},
	getSubmitValue:function(){
	     return this.getValue();
	},
	getValue:function(){
	    var val = this.callParent(arguments);
	    if(!val)
	        return '';
	    return this.formatDate(val,this.format)
	}
});