Ext.Loader.loadScript({url:(rootPath||"")+"/components/ckEditor/xss.min.js",onLoad:function(){
	for(var key in filterXSS.whiteList){
		filterXSS.whiteList[key].push("style");
	}
}});
Ext.define("EHR.ckEditor.CKEditor", {
	extend:'Ext.Container',
	alias:'widget.ckeditor',
	width:200,
	height:200,
	border:1,
	/**ckeditor默认值，只用于初始化时展示**/
	value:'',
	margin:0,
	padding:0,
	/**是否允许输入javascript*/
	allowScript:false,
	/**ckEditor的菜单配置，如果没有则默认全功能**/
	ckEditorConfig:undefined,
	
	functionType:'standard',
	
	fullFunction:{
		  toolbarGroups:[
			{ name: 'document', groups: [ 'mode', 'document', 'doctools' ] },
			{ name: 'clipboard', groups: [ 'clipboard', 'undo' ] },
			{ name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },
			{ name: 'forms', groups: [ 'forms' ] },
			'/',
			{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
			{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },
			{ name: 'links', groups: [ 'links' ] },
			{ name: 'insert', groups: [ 'insert' ] },
			'/',
			{ name: 'styles', groups: [ 'styles' ] },
			{ name: 'colors', groups: [ 'colors' ] },
			{ name: 'tools', groups: [ 'tools' ] },
			{ name: 'others', groups: [ 'others' ] },
			{ name: 'about', groups: [ 'about' ] }
		],
		removeButtons : 'About'
	},
	
	standardFunction:{
		toolbarGroups:[
			{ name: 'document', groups: [ 'mode', 'document', 'doctools' ] },
			{ name: 'clipboard', groups: [ 'clipboard', 'undo' ] },
			{ name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },
			{ name: 'forms', groups: [ 'forms' ] },
			{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
			{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },
			{ name: 'links', groups: [ 'links' ] },
			{ name: 'insert', groups: [ 'insert' ] },
			{ name: 'styles', groups: [ 'styles' ] },
			{ name: 'colors', groups: [ 'colors' ] },
			{ name: 'tools', groups: [ 'tools' ] },
			{ name: 'others', groups: [ 'others' ] },
			{ name: 'about', groups: [ 'about'] }
		],
		removeButtons:'Cut,Copy,Paste,PasteText,PasteFromWord,Scayt,Form,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,SelectAll,Anchor,About,SpecialChar,Flash,Iframe,Language'
	},
	
	simpleFunction:{
		toolbarGroups : [
			{ name: 'document', groups: [ 'mode', 'document', 'doctools' ] },
			{ name: 'clipboard', groups: [ 'clipboard', 'undo' ] },
			{ name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },
			{ name: 'forms', groups: [ 'forms' ] },
			{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
			{ name: 'styles', groups: [ 'styles' ] },
			{ name: 'colors', groups: [ 'colors' ] },
			{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },
			{ name: 'links', groups: [ 'links' ] },
			{ name: 'insert', groups: [ 'insert' ] },
			'/',
			{ name: 'tools', groups: [ 'tools' ] },
			{ name: 'others', groups: [ 'others' ] },
			{ name: 'about', groups: [ 'about' ] }
		],
		baseFloatZIndex : 19900,
		removeButtons : 'Source,About,Maximize,Image,Flash,Table,HorizontalRule,Smiley,SpecialChar,PageBreak,Iframe,Link,Unlink,Anchor,Language,BidiRtl,BidiLtr,Save,NewPage,Preview,Print,Templates,Cut,Copy,Paste,PasteText,PasteFromWord,Undo,Redo,Find,Replace,SelectAll,Form,Scayt,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,NumberedList,BulletedList,Outdent,Indent,Blockquote,CreateDiv,JustifyLeft,JustifyCenter,JustifyRight,JustifyBlock,ShowBlocks'
	},
	/**用于判断ckeditor是否加载完成，**/
	ckeditorReady:false,
	constructor:function(){
		var me = this;
		me.callParent(arguments);
		if(!window.CKEDITOR || !window.CKFinder){
		    window.CKEDITOR_BASEPATH=rootPath+'/ckeditor/';
			Ext.Loader.loadScriptsSync([
				rootPath+'/ckeditor/ckeditor.js',
				rootPath+'/ckfinder/ckfinder.js'
			]);
		}
		    
		//padding置为0，否则会对ckeditor有影响，改用margin即可
		me.padding = 0;
		//置为false，只有ckeditor初始化后才为true
		me.ckeditorReady = false;
		me.setStyle({overflow:'hidden',position:'absolute'});
		me.add(Ext.create('Ext.Component'));
	},
	listeners:{
		render:function(){
			this.initParam();
		},
		afterlayout:function(){
			this.setEditorSize();
		},
		show:function(){
			this.setEditorSize();
		}
	},
	initParam:function(){
		var me = this,
			id = me.items.items[0].id;
		me.setEditorConfig();
		me.editor = CKEDITOR.replace(id, me.CKConfig);
		CKFinder.setupCKEditor(me.editor,'/ckfinder');
		me.editor.setData(me.value);
		me.editor.on('instanceReady', function(e){
			me.editor.element.getNext().setStyle('border','none');
			me.ckeditorReady = true;
			me.editor.window.$.document.documentElement.style.height = '100%';
			if(!me.hidden)
				me.setEditorSize();
			e.editor.on('maximize', function(evt) {
				if(evt.editor.config.elementMode!=CKEDITOR.ELEMENT_MODE_INLINE) {
					//如果是Ext model的形式，那么在全屏的时候z-index小于ext的model的z-index，这里修改一下
					evt.editor.container.$.childNodes[1].style.zIndex = evt.editor.config.baseFloatZIndex*2;
				}
		    })
		});
	},
	setEditorConfig:function(){
	   var config;
	   if(this.ckEditorConfig){
	        config = this.ckEditorConfig;
	   }else if(this.functionType=='standard'){
	        config = this.standardFunction;
	   }else if(this.functionType=='full'){
	   		config = this.fullFunction;
	   }else{
	        config = this.simpleFunction;
	   }
		config.language = "zh-cn";
		config.resize_enabled = false;//是否使用“拖动以改变大小”功能
		config.removePlugins = "elementspath";//去掉底部路径显示栏
		config.image_previewText = ' ';//图片预览区域显示内容
		config.baseFloatZIndex = 19900;//保证弹出菜单在最上层，不会被Ext菜单遮住
		config.allowedContent = true;//关闭标签过滤 hej add 2016-4-14
		config.startupFocus = true;//页面载入时，编辑框是否立即获得焦点 hej add 2016-4-15
		config.enterMode = CKEDITOR.ENTER_BR;//回车默认添加<p>标签，改为<br>
		config.shiftEnterMode = CKEDITOR.ENTER_P;
		this.CKConfig = config;
	},
	/**
	 * 设置ckeditor大小
	 */
	setEditorSize:function(){
		var cw = this.getEl().dom.clientWidth,
			ch = this.getEl().dom.clientHeight;
		if(this.ckeditorReady)
			this.editor.resize(cw, ch);
	},
	/**
	 * 获取html
	 * @returns
	 */
	getHtml:function(){
		if(this.editor){
			var html = this.editor.getData();
			if(this.allowScript)
				return html;
			else
				return filterXSS(html);
		} else {
			return "";
		}
	},
	/**
	 * 获取纯文字
	 * @returns
	 */
	getText:function(){
		if(this.editor){
			return this.editor.document.getBody().getText();
		} else {
			return "";
		}
	},
	/**
	 * 设置ckeditor值
	 * @param value
	 */
	setValue:function(value){
		if(this.editor){
			this.editor.setData(value);
		}
	}
});