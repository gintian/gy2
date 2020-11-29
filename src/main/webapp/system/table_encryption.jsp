<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" type="text/css" href="/ext/resources/css/ext-all.css" />
<script type="text/javascript" src="/ext/ext-all.js"></script>
 <script type="text/javascript" src="/ext/rpc_command.js"></script> 
 <script>
 function formline(response)
	{
	 var respText = Ext.decode(response.responseText);
	 Ext.getCmp("salaryid").setValue(respText.salaryid);
	 Ext.getCmp("gjhmcid").setValue(respText.gjhmcid);
	 Ext.getCmp("cyhmcid").setValue(respText.cyhmcid);
	 Ext.getCmp("rsydid").setValue(respText.rsydid);
	 Ext.getCmp("reportid").setValue(respText.reportid);
	 Ext.getCmp("tablename").setValue(respText.tablename);
	}
	function saveok(response){
	var value=response.responseText;
	var map=Ext.decode(value);
	if(map.succeed)
		{
	   alert("加密成功！");
	    }
	}
 </script>
 <script>
  
 Ext.onReady(function(){
    var p = Ext.create('Ext.panel.Panel', {
    title: 'Oracle透明加密设置',
    height: 650,
    width: 900,
    renderTo:'dbsecurity',
    border: 2,
    layout: {
	        type: 'vbox',
	        pack: 'center',          
	        align: 'center'
        },
    items: [{
        	xtype:'panel',
        	width:'100%',
        	height:40,
        	border:false,
        	padding:'5 50 10 200',
        	layout:{type:'hbox',align: 'center'},
        	items:[{xtype:'label',
        	        html:'薪资类别编号:'},
        		{
		            id: "salaryid",
		            xtype:'textfield',
		            padding:'0 0 0 12',
		            width:400
					}
        	]}
        	,{
        	xtype:'panel',
        	width:'100%',
        	height:40,
        	border:false,
        	padding:'5 50 10 200',
        	layout:{type:'hbox',align: 'center'},
        	items:[{xtype:'label',
        	        html:'高级花名册编号:'},
        		{
		            id: "gjhmcid",
		            xtype:'textfield',
		            width:400
					}
        	]},{
        	xtype:'panel',
        	width:'100%',
        	height:40,
        	border:false,
        	padding:'5 50 10 200',
        	layout:{type:'hbox',align: 'center'},
        	items:[{xtype:'label',
        	        html:'常用花名册编号:'},
        		{
		            id: "cyhmcid",
		            xtype:'textfield',
		            width:400
					}
        	]},{
        	xtype:'panel',
        	width:'100%',
        	height:40,
        	border:false,
        	padding:'5 50 10 200',
        	layout:{type:'hbox',align: 'center'},
        	items:[{xtype:'label',
        	        html:'业务模板号:'},
        		{
		            id: "rsydid",
		            xtype:'textfield',
		            padding:'0 0 0 24',
		            width:400
					}
        	]},{
        	xtype:'panel',
        	width:'100%',
        	height:40,
        	border:false,
        	padding:'5 50 10 200',
        	layout:{type:'hbox',align: 'center'},
        	items:[{xtype:'label',
        	        html:'统计报表表号:'},
        		{
		            id: "reportid",
		            xtype:'textfield',
		            padding:'0 0 0 11',
		            width:400
					}
        	]},{
        	xtype:'panel',
        	width:'100%',
        	height:200,
        	border:false,
        	padding:'5 50 10 200',
        	layout:{type:'hbox',align: 'center'},
        	items:[{xtype:'label',
        	        html:'表名:'},
        		{
		            id: "tablename",
		            xtype:'textarea',
		            padding:'0 0 0 57',
		            width:400,
		            height:150
					}
        	]},{
            xtype:'label',
            text:'注意： 多个项用英文逗号隔开',
            padding:'5 50 10 100'
        }
        ],buttons:[{
		    	text:'确认',
		    	handler:function(){
					   var salaryid = Ext.getCmp("salaryid").getValue();
		    	    var gjhmcid =Ext.getCmp("gjhmcid").getValue();
		    	    var cyhmcid = Ext.getCmp("cyhmcid").getValue();
		    	    var rsydid = Ext.getCmp("rsydid").getValue();
		    	    var reportid = Ext.getCmp("reportid").getValue();
		    	    var tablename = Ext.getCmp("tablename").getValue();
		    		var map = new HashMap();
					   map.put("rsydid", rsydid);
					   map.put("gjhmcid",gjhmcid);
					   map.put("salaryid",salaryid);
					   map.put("cyhmcid", cyhmcid);
					   map.put("reportid", reportid);
					   map.put("tablename", tablename);
					    Rpc({functionId:'101001004833333',success:saveok},map);
		    		}
	    },{
		    	text:'取消',style : 'margin-left:100px;margin-right:55px',
		    	handler:function(){
		    		//this.ownerCt.ownerCt.close();
		    		Ext.getCmp("salaryid").setValue("");
					 Ext.getCmp("gjhmcid").setValue("");
					 Ext.getCmp("cyhmcid").setValue("");
					 Ext.getCmp("rsydid").setValue("");
					 Ext.getCmp("reportid").setValue("");
					 Ext.getCmp("tablename").setValue("");
		    	}
	    }],
	    buttonAlign:'center', 
	    listeners:{
	        render:function(){
	            var map = new HashMap();
	            Rpc({functionId:'101001004833332',success:formline},map);
	    }   
	    }
}).show();

 });
 

 
 </script>
 <form>
 <div id="dbsecurity" style='position:relative;top:5px;left:50%;margin-left:-450px;'></div>
 </form>
