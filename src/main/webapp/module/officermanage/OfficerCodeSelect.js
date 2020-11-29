/**
 * 干部任免表本地代码项选择控件
 * 扩充支持gridpanel 下拉选代码组件
 */
Ext.define('OfficerMange.OfficerCodeSelect', {
	 extend: 'Ext.tree.Panel',
	 xtype:'officercodeSelect',
	 id:'officerCodeSelect',
	 width: 150,
	 height: 200,
	 codeSet:undefined,
	 shadow:false,
	 floating:true,
	 rootVisible: false,
	 renderId:undefined,
	 callbackFn:undefined,
	 customTree:undefined,
	 constructor: function(config) {
		    this.callParent([config])
		    Ext.apply(this, config)
		    var me=this;
		    this.setStoreData(this.renderId)
		    if(!this.customTree){
		    	 Ext.getDoc().on("mousedown",function(e,t,o){
				    	if(me&&!me.owns(t)){
				    		if(t&&t.id){
				    			if(t.id==me.renderId){
				    				return
				    			}
				    		}
				    		Ext.callback(eval(me.callbackFn))
				    		me.destroy()
				    	}
					 },this);
		    	this.on("itemclick",function(view,record,item,index){
		     		 if (record.get("isCategory")!=null && record.get("isCategory")=="1"){
				         return
				      }
		     		 var text=record.get("text");
		     		 if(this.renderId.indexOf("nativeplace")>-1||this.renderId.indexOf("birthplace")){
		     			text = text.replace(/省/g,"").replace(/市/g,"");
		     		 }
		     		 document.getElementById(this.renderId).value=text;
		     		 Ext.callback(eval(this.callbackFn))
		     		 this.destroy( )
		     	},this)
		    }
	 },
	 setStoreData:function(id){
		 var children=[];
		 if(id==='sex_input'){
			 children=[ { text: '男', leaf: true },{ text: '女', leaf: true }]
		 }else if(id==='nation_input'){
			 children=[ 
				 { text: '汉族', leaf: true },
				 { text: '蒙古族', leaf: true },
				 { text: '回族', leaf: true },
				 { text: '藏族', leaf: true },
				 { text: '维吾尔族', leaf: true },
				 { text: '苗族', leaf: true },
				 { text: '彝族', leaf: true },
				 { text: '壮族', leaf: true },
				 { text: '布依族', leaf: true },
				 { text: '朝鲜族', leaf: true },
				 { text: '满族', leaf: true },
				 { text: '侗族', leaf: true },
				 { text: '瑶族', leaf: true },
				 { text: '白族', leaf: true },
				 { text: '土家族', leaf: true },
				 { text: '哈尼族', leaf: true },
				 { text: '哈萨克族', leaf: true },
				 { text: '傣族', leaf: true },
				 { text: '黎族', leaf: true },
				 { text: '傈傈族', leaf: true },
				 { text: '佤族', leaf: true },
				 { text: '畲族', leaf: true },
				 { text: '高山族', leaf: true },
				 { text: '拉祜族', leaf: true },
				 { text: '水族', leaf: true },
				 { text: '东乡族', leaf: true },
				 { text: '纳西族', leaf: true },
				 { text: '景颇族', leaf: true },
				 { text: '柯尔克孜族', leaf: true },
				 { text: '土族', leaf: true },
				 { text: '达翰尔族', leaf: true },
				 { text: '仫佬族', leaf: true },
				 { text: '羌族', leaf: true },
				 { text: '布朗族', leaf: true },
				 { text: '撒拉族', leaf: true },
				 { text: '毛南族', leaf: true },
				 { text: '仡佬族', leaf: true },
				 { text: '锡伯族', leaf: true },
				 { text: '阿昌族', leaf: true },
				 { text: '普米族', leaf: true },
				 { text: '塔吉克族', leaf: true },
				 { text: '怒族', leaf: true },
				 { text: '乌孜别克族', leaf: true },
				 { text: '俄罗斯族', leaf: true },
				 { text: '鄂温克族', leaf: true },
				 { text: '德昂族', leaf: true },
				 { text: '保安族', leaf: true },
				 { text: '裕固族', leaf: true },
				 { text: '京族', leaf: true },
				 { text: '塔塔尔族', leaf: true },
				 { text: '独龙族', leaf: true },
				 { text: '鄂伦春族', leaf: true },
				 { text: '赫哲族', leaf: true },
				 { text: '门巴族', leaf: true },
				 { text: '珞巴族', leaf: true },
				 { text: '基诺族', leaf: true },
				 { text: '其他组', leaf: true },
				 { text: '外国血统', leaf: true },
				 { text: '外国民族', leaf: true },
				 { text: '加入中国籍的外国人', leaf: true }
				 ]
		 }else if(id==='education_full_input'||id==='education_work_input'){//学历    
			 children=[ 
				 		{ text: '研究生教育', leaf: true },
				 		{ text: '研究生', leaf: true },
				 		{ text: '研究生班', leaf: true },
				 		{ text: '中央党校研究生', leaf: true },
				 		{ text: '省（区、市）委党校研究生', leaf: true },
				 		{ text: '本科教育', leaf: true },
				 		{ text: '大学', leaf: true },
				 		{ text: '中央党校大学', leaf: true },
				 		{ text: '省（区、市）委党校大学', leaf: true },
				 		{ text: '专科教育', leaf: true },
				 		{ text: '大专', leaf: true },
				 		{ text: '省（区、市）委党校大专', leaf: true },
				 		{ text: '大学普通班', leaf: true },
				 		{ text: '中央党校大专', leaf: true },
				 		{ text: '中专', leaf: true },
				 		{ text: '中技', leaf: true },
				 		{ text: '高中', leaf: true },
				 		{ text: '初中', leaf: true },
				 		{ text: '小学', leaf: true }
				 	   ]
		 }else if(id==='degree_full_input'||id==='degree_work_input'){   
			 children=[
				 { text: '名誉博士', leaf: true },
		         { text: '博士', expanded: false, 
					 children: [
			                { text: '哲学博士', leaf: true },
			                { text: '经济学博士', leaf: true },
			                { text: '法学博士', leaf: true },
			                { text: '教育学博士', leaf: true },
			                { text: '文学博士', leaf: true },
			                { text: '历史学博士', leaf: true },
			                { text: '理学博士', leaf: true },
			                { text: '工学博士', leaf: true },
			                { text: '农学博士', leaf: true },
			                { text: '医学博士', leaf: true },
			                { text: '军事学博士', leaf: true },
			                { text: '管理学博士', leaf: true },
			                { text: '临床医学博士专业', leaf: true },
			                { text: '兽医博士专业', leaf: true },
			                { text: '口腔医学博士专业', leaf: true }
		                ]
				 },
				 { text: '硕士', expanded: false, 
					 children: [
			                { text: '哲学硕士', leaf: true },
			                { text: '经济学硕士', leaf: true },
			                { text: '法学硕士', leaf: true },
			                { text: '教育学硕士', leaf: true },
			                { text: '文学硕士', leaf: true },
			                { text: '历史学硕士', leaf: true },
			                { text: '理学硕士', leaf: true },
			                { text: '工学硕士', leaf: true },
			                { text: '农学硕士', leaf: true },
			                { text: '医学硕士', leaf: true },
			                { text: '军事学硕士', leaf: true },
			                { text: '管理学硕士', leaf: true },
			                { text: '法律硕士专业', leaf: true },
			                { text: '教育硕士专业', leaf: true },
			                { text: '建筑学硕士专业', leaf: true },
			                { text: '临床医学硕士专业', leaf: true },
			                { text: '工商管理硕士专业', leaf: true },
			                { text: '农业推广硕士专业', leaf: true },
			                { text: '兽医硕士专业', leaf: true },
			                { text: '公共管理硕士专业', leaf: true },
			                { text: '口腔医学硕士专业', leaf: true },
			                { text: '公共卫生硕士专业', leaf: true },
			                { text: '军士硕士专业', leaf: true }
		                ]
				 },
				 { text: '学士', expanded: false,
					 children: [
						 { text: '哲学学士', leaf: true },
						 { text: '经济学学士', leaf: true },
						 { text: '法学学士', leaf: true },
						 { text: '教育学学士', leaf: true },
						 { text: '文学学士', leaf: true },
						 { text: '历史学学士', leaf: true },
						 { text: '理学学士', leaf: true },
						 { text: '工学学士', leaf: true },
						 { text: '农学学士', leaf: true },
						 { text: '医学学士', leaf: true },
						 { text: '军事学学士', leaf: true },
						 { text: '管理学学士', leaf: true },
						 { text: '建筑学学士', leaf: true }
		                ]
				 }
			 ] 
		 }else if(id.indexOf("ChengWei")===0){
			 children=[ 
					 	{ text: '父亲', leaf: true },
					 	{ text: '母亲', leaf: true },
					 	{ text: '丈夫', leaf: true },
					 	{ text: '妻子', leaf: true },
					 	{ text: '儿子', leaf: true },
					 	{ text: '女儿', leaf: true },
					 	{ text: '哥哥', leaf: true },
					 	{ text: '弟弟', leaf: true },
					 	{ text: '姐姐', leaf: true },
					 	{ text: '妹妹', leaf: true },
					 	{ text: '长子', leaf: true },
					 	{ text: '次子', leaf: true },
					 	{ text: '三子', leaf: true },
					 	{ text: '四子', leaf: true },
					 	{ text: '五子', leaf: true },
					 	{ text: '养子', leaf: true },
					 	{ text: '继子', leaf: true },
					 	{ text: '女婿', leaf: true },
					 	{ text: '长女', leaf: true },
					 	{ text: '次女', leaf: true },
					 	{ text: '三女', leaf: true },
					 	{ text: '四女', leaf: true },
					 	{ text: '五女', leaf: true },
					 	{ text: '养女', leaf: true },
					 	{ text: '继女', leaf: true },
					 	{ text: '儿媳', leaf: true },
					 	{ text: '其他女儿', leaf: true },
					 	{ text: '孙子', leaf: true },
					 	{ text: '孙女', leaf: true },
					 	{ text: '外孙子', leaf: true },
					 	{ text: '外孙女', leaf: true },
					 	{ text: '公公', leaf: true },
					 	{ text: '婆婆', leaf: true },
					 	{ text: '岳父', leaf: true },
					 	{ text: '继父', leaf: true },
					 	{ text: '养父', leaf: true },
					 	{ text: '继母', leaf: true },
					 	{ text: '养母', leaf: true },
					 	{ text: '祖父', leaf: true },
					 	{ text: '祖母', leaf: true },
					 	{ text: '外祖父', leaf: true },
					 	{ text: '外祖母', leaf: true },
					 	{ text: '曾祖父', leaf: true },
					 	{ text: '曾祖母', leaf: true },
					 	{ text: '嫂子', leaf: true },
					 	{ text: '弟媳', leaf: true },
					 	{ text: '姐夫', leaf: true },
					 	{ text: '妹夫', leaf: true },
					 	{ text: '伯父', leaf: true },
					 	{ text: '伯母', leaf: true },
					 	{ text: '叔父', leaf: true },
					 	{ text: '婶母', leaf: true },
					 	{ text: '舅父', leaf: true },
					 	{ text: '舅母', leaf: true },
					 	{ text: '姨夫', leaf: true },
					 	{ text: '姨母', leaf: true },
					 	{ text: '姑父', leaf: true },
					 	{ text: '姑母', leaf: true },
					 	{ text: '堂兄', leaf: true },
					 	{ text: '堂弟', leaf: true },
					 	{ text: '堂姐', leaf: true },
					 	{ text: '堂妹', leaf: true },
					 	{ text: '表兄', leaf: true },
					 	{ text: '表弟', leaf: true },
					 	{ text: '表姐', leaf: true },
					 	{ text: '表妹', leaf: true },
					 	{ text: '侄子', leaf: true },
					 	{ text: '侄女', leaf: true },
					 	{ text: '侄女', leaf: true },
					 	{ text: '外甥', leaf: true },
					 	{ text: '外甥女', leaf: true }
				 	  ]
		 }else if(id.indexOf("ZhengZhiMianMao")==0){
			 children=[ 
					 { text: '中共党员', leaf: true },
					 { text: '预备党员', leaf: true },
					 { text: '共青团员', leaf: true },
					 { text: '民革', leaf: true },
					 { text: '民盟', leaf: true },
					 { text: '民建', leaf: true },
					 { text: '民进', leaf: true },
					 { text: '农工党', leaf: true },
					 { text: '致公党', leaf: true },
					 { text: '九三', leaf: true },
					 { text: '台盟', leaf: true },
					 { text: '无党派', leaf: true },
					 { text: '群众', leaf: true }
				 ]
		 }
		 var store = Ext.create('Ext.data.TreeStore', {
			    root: {
			        expanded: true,
			        children: children
			    }
			});
		 if(this.codeSet&&this.codeSet!==""){
			 store=this.getTreeStore();
		 }
		 
		 this.setStore(store);
	 },
	 getTreeStore:function(searchtext){
		 if(typeof(searchtext)=='undefined'){
			 searchtext='';
		 }
		 var codestore = Ext.create('Ext.data.TreeStore',{
				autoLoad:false,
				id:'officer_codeSelect_store',
		    	fields: ['text','id','codesetid','itemdesc','layerdesc','selectable'], 
		        proxy: Ext.create("EHR.extWidget.proxy.TransactionProxy",{   
	            extraParams:{
	            	codesetid  : this.codeSet,
	            	codesource : '',
	            	nmodule    : "8",
	            	ctrltype   : "3",
	            	parentid   : '',
	            	searchtext : searchtext!==''?encodeURI(searchtext):'',
	            	onlySelectCodeset:'',
	            	multiple:false,
	            	isShowLayer:'1',
	            	isHideTip:true
	            },
	            reader:{
	               type:'json',
	               root:'children'
	            },
	            functionId:'ZJ100000131'
		        })
		    });
		   codestore.proxy.extraParams.expandTop=false;
		   codestore.load();
//		   codestore.proxy.extraParams.expandTop=false;
		   return codestore
	 }
});
