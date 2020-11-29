//** Powered by Fason
/***************************************************
*xml文件格式
*<?xml version="1.0" encoding="GB2312"?>
<TreeNode id="00" text="root" title="organization">
  <TreeNode id="1101" text="北京市市辖区" title="北京市市辖区" xml="/system/get_code_tree.jsp?codesetid=AB&amp;codeitemid=1101" icon="/images/pos_l.gif"/>
  <TreeNode id="1102" text="北京市所属县" title="北京市所属县" xml="/system/get_code_tree.jsp?codesetid=AB&amp;codeitemid=1102" icon="/images/pos_l.gif"/>
</TreeNode>
*
*
*
*
*
****************************************************/
var icon={
	root	:'/images/unit.gif',
	open	:'/images/open.png',
	close	:'/images/close.png',
	file	:'/images/file.png',
	Rplus	:'/images/Rplus.gif',
	Rminus	:'/images/Rminus.gif',
	join	:'/images/T.png',
	joinbottom:'/images/L.png',
	plus	:'/images/Tplus.png',
	plusbottom:'/images/Lplus.png',
	minus	:'/images/Tminus.png',
	minusbottom:'/images/Lminus.png',
	blank	:'/images/blank.png',
	line	:'/images/I.png'
};
var Global={
	id:0,
	getId:function(){return this.id++;},
	all:[],
	selectedItem:null,
	defaultText:"",
	defaultAction:"javascript:void(0)",
	defaultTarget:"_blank",
	closeAction:"",         //关闭事件
	checkvalue:",",      //选中的值,可以根据选中的值，置checkbox radio的状态
	checkValueTitle:"",  //传入的已选中的标题
	defaultInput:0,      //是否为0无,1　checkbox 2=radio default 无	
	checkboxScan:false,//true|false;当使用checkbox复选框时，如果此车设置为true，xtree自动检索复选框从选中状态变为未选中同步checkvalue，最终调用getSelected（）方法时取与checkvalue的并集
	showroot:false,     //根节点是否显示 checkbox,radio
	drag:false,
	showorg:0,//   =1为组织机构树,=0为其它树
	defaultchecklevel:0,   //showorg=1时  =all出现单选框,1=部门以下都出现,2=职位以下,3=只有人员出现,4=当showDb=1显示人员库时不在人员库上显示checkbox
	/**check 级联选中*/
	cascade:false,	
	defaultradiolevel:0, //0=all出现单选框,1=部门以下都出现,2=职位以下,3=只有人员出现,4=当showDb=1显示人员库时不在人员库上显示radio
	setDrag:function(bool){this.drag=bool;},
	isIE:navigator.appName.indexOf("Microsoft")!= -1,
	//needboxcheck:"" //checkbox 点击时需不需要触发事件   事件方法为 ：clickBox(this) 需自己声明与实现方法逻辑
	//已选中的指标中去除的节点的值，多个时用逗号分隔；例如UN01,UN02,...
	removeCodeValues: "",
	//页面传递的Target，如果页面传了target，以页面传递的值为准
	target:""
};

function preLoadImage(){
	for(i in icon){
		var tem=icon[i];
		icon[i]=new Image();
		icon[i].src=tem;
	}
}

preLoadImage();

function treeItem(text,action,target,title,Icon)
{
	this.id=Global.getId();
	this.level=0;
	this.text=text?text:Global.defaultText;
	this.action=action?action:Global.defaultAction;
	this.target=target?target:Global.defaultTarget;
	this.title=title?title:this.text;
	this.isLast=true;
	this.childNodes=new Array();
	this.indent=new Array();
	this.parent=null;
	var c =0; 
	//if(getCookie("item"+this.id) != null) c = getCookie("item"+this.id);
	this.open=parseInt(c);
	this.load=false;
	this.setuped=false;
	this.JsItem=null;
	this.container=document.createElement("div");
	this.icon=Icon;
	//chenmengqing added 200506
	this.bUseCheck=false ;	// 是否启用选择
	
	this.status=true;//标识出节点前是否加checkbox 或radio,同时也需要根据全局变量defaultInput定义
	
	//chenmengqing end.
	Global.all[Global.all.length]=this;
	this.dragFrom = null;
	this.dragbool = false;
}
treeItem.prototype.toString = function()
{
    var o = this;
	var oItem = document.createElement("div");
	oItem.id = "treeItem"+this.id;
	oItem.className = "treeItem";
	oItem.noWrap = true;
	oItem.onselectstart = function(){ return false;};
	oItem.oncontextmenu = function(){ return false;};
	this.JsItem = oItem;
	this.drawIndents();
	var iIcon = document.createElement("img");
	iIcon.setAttribute("style",  "vertical-align:middle");
	iIcon.align = "absmiddle";
	iIcon.src = this.childNodes.length>0?(this.open?(this.level>0?(this.isLast?icon.minusbottom.src:icon.minus.src):icon.Rminus.src):(this.level>0?(this.isLast?icon.plusbottom.src:icon.plus.src):icon.Rplus.src)):(this.level>0?(this.isLast?icon.joinbottom.src:icon.join.src):icon.blank.src);
	iIcon.id = "treeItem-icon-handle-" + this.id;
	iIcon.onclick = function(){ o.toggle();};
	oItem.appendChild(iIcon);	
	
	iIcon = document.createElement("img");
	iIcon.setAttribute("style",  "vertical-align:middle");
	iIcon.align = "absmiddle";
	iIcon.src = this.icon?this.icon:(this.childNodes.length>0?(this.open?icon.open.src:icon.close.src):icon.file.src);
	iIcon.id = "treeItem-icon-folder-" + this.id;
	iIcon.onclick = function(){ selectedClass(this.id); o.select();};
	iIcon.ondblclick = function(){ o.toggle();};
	oItem.appendChild(iIcon);
	var eText = document.createElement("span");
	var eA=document.createElement("a");
	eA.innerHTML =this.text;
	eA.target =this.target;
	eA.href =this.action;	
	eA.onkeydown = function(e){ return o.KeyDown(e);};
	var draged=false;
	var tdiv=null;
	eA.ondragstart = function(e){
		if(Global.drag){
			o.dragFrom = o;
			Global.selectedItem = o;
			temp = o;

			var ao=document.getElementById("treeItem-icon-folder-"+o.id);
		 	if(ao==null)
		   		return; 
			tdiv=document.createElement("div");
			tdiv.id = "treeItemX"+o.id;
			tdiv.style.position="absolute";
			tdiv.style.filter="alpha(opacity=70)";
			tdiv.style.display="block";
			tdiv.style.cursor="move";
			tdiv.style.top=ao.style.top;
		 	tdiv.style.left=ao.style.left;
	
			iIcon = document.createElement("img");
			iIcon.setAttribute("style",  "vertical-align:middle");
			iIcon.align = "absmiddle";
			iIcon.src = o.icon;
			tdiv.appendChild(iIcon);
			var mText = document.createElement("nobr");
			mText.innerHTML =o.text;
			tdiv.appendChild(mText);
			document.body.appendChild(tdiv);
			draged=true;
			
		}
	};
	eA.ondrag = function(e){
		if(Global.drag){
			tdiv.style.left=event.x+10;
			tdiv.style.top=event.y; 
		}
	};
	eA.ondragenter = function(e){
		if(Global.drag){
			temp = o;	//temp is object whice is mouse naturalization 
			temp.dragFrom = Global.selectedItem.dragFrom;	//dragFrom is object whice is dragstart 
			Global.selectedItem = temp;
			selectedClass("treeItem-icon-folder-"+Global.selectedItem.id);
		}
	};
	eA.ondragend= function(e){
		if(Global.drag){
			if(!draged){//解决Firefox浏览器拖拽执行多次，阻止冒泡  wangb 2020-01-14 bug 57386
				e.stopPropagation();
				return;
			}
 			draged=false;
 			if(tdiv!=null){
 				document.body.removeChild(tdiv);
 				tdiv = null;
 			}
			if(temp!=null&&o.dragFrom!=temp){
				var temp2 = temp;
				while(temp2.parent!=null){
					temp2.dragbool=false;
					if(temp2.parent==o.dragFrom)
						return;
					temp2 = temp2.parent;
				}
				temp.dragbool = true;
				temp.load = false;
				o.dragFrom.load = false;
			}
			
		}
	};
	eText.onclick = function(){o.select();};
	eText.ondblclick=function(){o.toggle();eval(Global.closeAction);};
	if(this.action == Global.defaultAction) 
	{   
	   eA.onclick = function(){ return false;};
	}
	//check or radio chenmengqing 20050620	
	if(Global.defaultInput==1)  //checkbox ,&&this.uid!="root"
	{
		var chkbox;
	  if(Global.showorg==0)
	  {
	   if(this.level>=Global.defaultchecklevel)								//当非根节点名称为root时，统一屏蔽root不合理
	     if(/*((this.uid=="root"&&Global.showroot==true)||(this.uid!="root"))*/(this.parent&&this.status)||Global.showroot==true)//chenmengqing added this.status
	     {
	     	if(Global.isIE){
		      var content="<input type='checkbox' onclick='changeState(this)' name='";
			   if(Global.checkvalue && Global.checkvalue.indexOf(","+this.uid+",")!=-1)           
		              content=content+"treeItem-check"+"' checked>";
		       else
		              content=content+"treeItem-check"+"'>";           	 		
			   chkbox=document.createElement(content);
			   chkbox.id="treeItem-check-" + this.id;
			   chkbox.value=this.uid;
		   		chkbox.title=this.title;
		   		//if(Global.needboxcheck == '1')
		   		//	chkbox.onclick = onClickBox;
	     	}else{
		   //xuj update 2011.5.14 为兼容firefox、chrome浏览器
		   var content="input";
		    chkbox=document.createElement(content);
		    chkbox.type="checkbox";
		    chkbox.id="treeItem-check-" + this.id;
		    //chkbox.name="treeItem-check";
		    chkbox.setAttribute("name","treeItem-check");
		    chkbox.value=this.uid;
		   chkbox.title=this.title;
			   
		    if(Global.checkvalue && Global.checkvalue.indexOf(","+this.uid+",")!=-1) //add by xiegh date20180410 bug36492          
	              chkbox.checked=true;
	       else
	              chkbox.checked=false;
	       }
		   if(Global.cascade)		   
		   		chkbox.onclick=function(){o.setItemState()};
		   eText.appendChild(chkbox);
	     }
	  }
	  if(Global.showorg==1)//org tree
	  {
		    var bool = false;
	    	if(this.uid!=null){
		    	if(Global.defaultchecklevel==4){
			    	if(this.uid.substring(0,2)!='@@')
			    		bool=true;
		    	}else if(Global.defaultchecklevel==3){
		    		if(this.uid.substring(0,2)!='UM'&&this.uid.substring(0,2)!='UN'&&this.uid.substring(0,2)!='@K'&&this.uid.substring(0,2)!='@@')
		    			bool=true;
		    	}else if(Global.defaultchecklevel==2){
		    			if(this.uid.substring(0,2)!='UN'&&this.uid.substring(0,2)!='UM'&&this.uid.substring(0,2)!='@@')
		    				bool=true;
		    	}else if(Global.defaultchecklevel==1){
		    			if(this.uid.substring(0,2)!='UN'&&this.uid.substring(0,2)!='@@')
		    				bool=true;
		    	}else if(Global.defaultchecklevel==0){
		    		bool=true;
		    	}
    		}
    		if(bool)
    		{
			     if(/*(this.uid=="root"&&Global.showroot==true)||(this.uid!="root")*/this.parent||Global.showroot==true)
			     {
			     	if(Global.isIE){
				       var content="<input type='checkbox' name='";
					   if(Global.checkvalue && Global.checkvalue.indexOf(","+this.uid+",")!=-1)           
				              content=content+"treeItem-check"+"' checked>";
				       else
				              content=content+"treeItem-check"+"'>";           	 		
					   chkbox=document.createElement(content);
					   chkbox.id="treeItem-check-" + this.id;
					   chkbox.value=this.uid;
					   chkbox.title=this.title;
			     	}else{
				     	//xuj update 2011.5.14 为兼容firefox、chrome浏览器
				     	var content="input";
				     	chkbox=document.createElement(content);
				     	chkbox.type="checkbox";
				     	chkbox.id="treeItem-check-" + this.id;
				     	chkbox.name="treeItem-check";    
					   chkbox.value=this.uid;
					   chkbox.title=this.title;
					   if(Global.checkvalue && Global.checkvalue.indexOf(","+this.uid+",")!=-1)   //add by xiegh date20180410 bug36492        
				              chkbox.checked=true;
				       else
				       		  chkbox.checked=false;
			     	}
			     	if(Global.cascade)		   
				   		chkbox.onclick=function(){o.setItemState()};
				   eText.appendChild(chkbox);
			     }    			
    		}
		    	
	  }
	  //给checkbox添加点击监听方法，同步Global.checkvalue值，当复选框从选中状态变为未选中状态时去除Global.checkvalue选中节点
	  if(Global.checkboxScan && chkbox){
			  if (window.addEventListener) { // Mozilla, Netscape, Firefox
				  chkbox.addEventListener("click", function(){ scanChkValue(chkbox);}, false);
				} else {// ie
					if(!chkbox.hadattachEvent){//防止重复添加事件
						chkbox.attachEvent('onclick',function(){ scanChkValue(chkbox);});
						chkbox.hadattachEvent=true;
					}
				}	  
	  }
	}
    else if(Global.defaultInput==2) //&&this.uid!="root"
    {
		
		
		if(Global.showorg==0)//xujian add 2011-1-24
	  {
	   if(this.level>=Global.defaultradiolevel)	
	     if(/*((this.uid=="root"&&Global.showroot==true)||(this.uid!="root"))*/(this.parent&&this.status)||Global.showroot==true)
	     {
	     	if(Global.isIE){
	        var content="<input type='radio' name='";
	      	   	if(Global.checkvalue==this.uid)
	      	  	{	  
	      	  	
	                    content=content+"treeItem-radio"+"' value='"+this.uid+"' title='"+this.title.replace(/'/g,"‵")+"' checked>";
	            }
	            else
	            {
	                    content=content+"treeItem-radio"+"' value='"+this.uid+"' title='"+this.title.replace(/'/g,"‵")+"'>";           	
	            }
	      	   	var radio=document.createElement(content);
	     	}else{
	     		//xuj update 2011.5.14 为兼容firefox、chrome浏览器
	     	   var content="input";
	     	   var radio=document.createElement(content);
	     	   radio.type="radio";
			     	radio.id="treeItem-radio-" + this.id;
			     	radio.name="treeItem-radio";    
				   radio.value=this.uid;
				   radio.title=this.title;
				   if(Global.checkvalue==this.uid)
	      	  	{	
	      	  		radio.checked=true;
	      	  	}else{
	      	  		radio.checked=false;
	      	  	}
	     	}
	      	   	eText.appendChild(radio);
	     }
	  }
	   if(Global.showorg==1)//org tree
	  {
	  	var bool = false;
    	if(this.uid!=null){    
    		if(Global.defaultradiolevel==4){
	    		if(this.uid.substring(0,2)!='@@')
	    			bool=true;
	    	}else if(Global.defaultradiolevel==3){
	    		if(this.uid.substring(0,2)!='UM'&&this.uid.substring(0,2)!='UN'&&this.uid.substring(0,2)!='@K'&&this.uid.substring(0,2)!='@@')
	    			bool=true;
	    	}else if(Global.defaultradiolevel==2){
	    		if(this.uid.substring(0,2)!='UN'&&this.uid.substring(0,2)!='UM'&&this.uid.substring(0,2)!='@@')
    				bool=true;
	    	}else if(Global.defaultradiolevel==1){
	    		if(this.uid.substring(0,2)!='UN'&&this.uid.substring(0,2)!='@@')
    				bool=true;
	    	}else if(Global.defaultradiolevel==0){
	    		bool=true;
	    	}
    	}    

    	if(this.uid!=null)    	
    	 if((bool&&this.status&&this.level>=Global.defaultradiolevel)||(Global.defaultradiolevel==3&&bool))
    	 {
    	   if(/*((this.uid=="UN"||this.uid=="root")&&Global.showroot==true)||(this.uid!="root"&&this.uid!="UN")*/(this.parent&&this.status)||Global.showroot==true)
           {
           	if(Global.isIE){
	            var content="<input type='radio' name='";
	      	   	if(Global.checkvalue==this.uid)
	      	  	{	  
	      	  	
	                    content=content+"treeItem-radio"+"' value='"+this.uid+"' title='"+this.title.replace(/'/g,"‵")+"' checked>";
	            }
	            else
	            {
	                    content=content+"treeItem-radio"+"' value='"+this.uid+"' title='"+this.title.replace(/'/g,"‵")+"'>";           	
	            }
	      	   	var radio=document.createElement(content);
           	}else{
           		var content="input";
	     	   var radio=document.createElement(content);
	     	   radio.type="radio";
			     	radio.id="treeItem-radio-" + this.id;
			     	radio.name="treeItem-radio";    
				   radio.value=this.uid;
				   radio.title=this.title;
				   if(Global.checkvalue==this.uid)
	      	  	{	
	      	  		radio.checked=true;
	      	  	}else{
	      	  		radio.checked=false;
	      	  	}
           	}
	      	   	eText.appendChild(radio);
			}
		}
    }
    }
	//check end.
	eText.appendChild(eA);
	eText.id = "treeItem-text-" + this.id;
	eText.className = "treeItem-unselect"
	eText.setAttribute("style", "vertical-align:middle");
	eText.align = "absmiddle";
	eText.onclick = function(){selectedClass(this.id); o.select(1);};
	eText.title = this.title;
	oItem.appendChild(eText);
	this.container.id = "treeItem-container-"+this.id;
	this.container.style.display = this.open?"":"none";
	oItem.appendChild(this.container);
	return oItem;
}

//同步Global.checkvalue值，当复选框从选中状态变为未选中状态时去除Global.checkvalue选中节点
function scanChkValue(chkbox){
	var chkvalues = Global.checkvalue;
	var checkValueTitles = Global.checkValueTitle;
	if(chkbox.checked==false){
		chkvalues = chkvalues.replace(","+chkbox.value+",", ",");
		checkValueTitles = "," + checkValueTitles;
		checkValueTitles = checkValueTitles.replace(","+chkbox.title+",", ",");
		checkValueTitles = checkValueTitles.substr(1);
	}
	Global.checkvalue = chkvalues;
	Global.checkValueTitle = checkValueTitles;
}

treeItem.prototype.root = function()
{
	var p = this;
	while(p.parent)
		p = p.parent;
	return p;
}

treeItem.prototype.setText = function(sText)
{
	if(this.root().setuped)
	{
		var oItem = document.getElementById("treeItem-text-" + this.id);
		//xuj update 20141201 解决setText后展开节点如果没有子节点时会断线问题
		//oItem.firstChild.innerHTML = sText;
		var eText = document.createElement("span");
		eText.style.margin="0 0 0 0";
		eText.innerHTML=sText;
		oItem.firstChild.replaceChild(eText, oItem.firstChild.firstChild);
	}
	this.text = sText;
}
//  设置完成关联人员后，修改节点title  jingq add 2014.10.27 
treeItem.prototype.setTitle = function(sTitle)
{
	if(this.root().setuped)
	{
		var oItem = document.getElementById("treeItem-text-"+this.id);
		oItem.firstChild.title = sTitle;
	}
	this.title = sTitle;
}
/**xuj add 2010-7-12
	用于设置选中节点的图标
  */
treeItem.prototype.setIcon = function(iCon)  
{
	if(this.root().setuped)
	{
		//alert(iCon);
		var oItem = document.getElementById("treeItem-icon-folder-" + this.id);
		//alert(oItem.src);
		//oItem.width='22';
		//oItem.height='20';
		oItem.src = iCon;
	}
	this.icon = iCon;
}
//sx修改
treeItem.prototype.setAction = function(Action)
{
	if(this.root().setuped)
	{
		var oItem = document.getElementById("treeItem-text-" + this.id);
		oItem.firstChild.href = Action;
	}
	this.action = Action;
}
treeItem.prototype.setIndent = function(l,v)
{
	for(var i=0;i<this.childNodes.length;i++)
	{
		this.childNodes[i].indent[l] = v;
		this.childNodes[i].setIndent(l,v);
	}
}

treeItem.prototype.drawIndents = function()
{
	var oItem = this.JsItem;
	for(var i=0;i<this.indent.length;i++)
	{
		var iIcon = document.createElement("img");
		iIcon.setAttribute("style",  "vertical-align:middle");
		iIcon.align = "absmiddle";
		iIcon.id = "treeItem-icon-" + this.id + "-" + i;
		iIcon.src = this.indent[i]?icon.blank.src:icon.line.src;
		iIcon.height="25";
		oItem.appendChild(iIcon);
	}
}

/**
 * 级联选中所有下级节点监听方法  xuj update 2013-12-04 以前只支持选中直接子节点
 */
treeItem.prototype.setItemState=function(oItem)
{
	var node,chkid,flag;
	if(this == Global.selectedItem){ Global.selectedItem = null;}
	chkid="treeItem-check-" + this.id;
    var checkitems=document.getElementById(chkid);
    flag=checkitems.checked;
    this.expand();
	for(var i=this.childNodes.length-1;i>=0;i--)
	{
		node=this.childNodes[i];
		cascadeCheckState(node,flag);
	}
}

/**
 * 级联选中所有下级节点递归方法  xuj add 2013-12-04
 */
function cascadeCheckState(oItem,flag){
	var node,chkid,flag;
	chkid="treeItem-check-" + oItem.id;
    var checkitems=document.getElementById(chkid);
    checkitems.checked=flag;
    if(Global.checkboxScan&&flag==false){
    	scanChkValue(checkitems);
    }
    oItem.expand();
	for(var i=oItem.childNodes.length-1;i>=0;i--)
	{
		node=oItem.childNodes[i];
		cascadeCheckState(node,flag);
	}
}

treeItem.prototype.add = function(oItem)
{
	oItem.parent=this;
	this.childNodes[this.childNodes.length]=oItem;
	oItem.level=this.level+1;
	oItem.indent=this.indent.concat();
	oItem.indent[oItem.indent.length]=this.isLast;
	if(this.childNodes.length>1){
		var o=this.childNodes[this.childNodes.length-2];
		o.isLast=false;
		o.setIndent(o.level,0);
		if(this.root().setuped)o.reload(1);
	}
	else if(this.root().setuped)
		this.reload(0);
	this.container.appendChild(oItem.toString());
	this.container.style.display=this.open?"":"none";
}

treeItem.prototype.loadChildren = function()
{
	//do something
}

treeItem.prototype.remove = function()
{
	var tmp = this.getPreviousSibling();
	//if(tmp){ tmp.select();}
	this.removeChildren();
	var p = this.parent;
	if(!p){ return };
	if(p.childNodes.length>0){
		var o = p.childNodes[p.childNodes.length-1];
		o.isLast = true;
		o.setIndent(o.level,1);
		if(o.root().setuped)o.reload(1);
	}
	else
		p.reload();
}

treeItem.prototype.removeChildren = function ()
{
	if(this == Global.selectedItem){ Global.selectedItem = null;}
	for(var i=this.childNodes.length-1;i>=0;i--)
		this.childNodes[i].removeChildren();
	var o = this;
	var p = this.parent;
	if (p) { p.childNodes = p.childNodes._remove(o);}
	Global.all[this.id] = null
	var oItem = document.getElementById("treeItem"+this.id);
	if (oItem) { oItem.parentNode.removeChild(oItem); }
}

treeItem.prototype.clearChildren = function ()
{
	if(this == Global.selectedItem){ Global.selectedItem = null;}
	for(var i=this.childNodes.length-1;i>=0;i--)
		this.childNodes[i].removeChildren();
}

treeItem.prototype.reload = function(flag)
{
	if (flag){
		for(var j=0;j<this.childNodes.length;j++){ this.childNodes[j].reload(1);}
		for(var i=0;i<this.indent.length;i++)
			document.getElementById("treeItem-icon-" +this.id+ "-"+i).src = this.indent[i]?icon.blank.src:icon.line.src;
	}
	document.getElementById("treeItem-icon-handle-" +this.id).src = this.childNodes.length>0?(this.open?(this.level>0?(this.isLast?icon.minusbottom.src:icon.minus.src):icon.Rminus.src):(this.level>0?(this.isLast?icon.plusbottom.src:icon.plus.src):icon.Rplus.src)):(this.level>0?(this.isLast?icon.joinbottom.src:icon.join.src):icon.blank.src);
	if (!this.icon)
		document.getElementById("treeItem-icon-folder-"+this.id).src = this.childNodes.length>0?(this.open?icon.open.src:icon.close.src):icon.file.src;
}

treeItem.prototype.toggle = function()
{
	if(this.childNodes.length>0){	
	
		if(this.open)
			this.collapse();
		else
			this.expand();
	}
}

treeItem.prototype.expand = function()
{
   
	this.open=1;
	if(!this.load){	   
	  this.load=true;
	  this.loadChildren();
	  this.reload(1);
	}
	else 
		this.reload(0);
	this.container.style.display = "";
}

treeItem.prototype.collapse = function()
{
    this.open=0;
	//setCookie("item"+this.id,0);	
	this.container.style.display = "none";
	this.reload(0);
	this.select(1);
}

treeItem.prototype.expandAll = function()
{
	if(this.childNodes.length>0 && !this.open)this.expand();
	this.expandChildren();
}

treeItem.prototype.collapseAll = function()
{
	this.collapseChildren();
	if(this.childNodes.length>0 && this.open)this.collapse();
}

treeItem.prototype.expandChildren = function()
{
	for(var i=0;i<this.childNodes.length;i++)
	this.childNodes[i].expandAll();
}

treeItem.prototype.collapseChildren = function()
{
	for(var i=0;i<this.childNodes.length;i++)
	this.childNodes[i].collapseAll()
}

treeItem.prototype.openURL=function()
{
	if(this.action!=Global.defaultAction){	
		window.open(this.action,this.target);
	}
}
//xujian addd 2010-12-3 如果system.properties文件中配置了org_expand_level=2，则组织机构树，支持默认展开第至二级
treeItem.prototype.expand2level=function()
{
	var myroot=this.root();
	if(myroot==null)
		return;
	for(var i=0;i<myroot.childNodes.length;i++){
		myroot.childNodes[i].expand();
	}
}


treeItem.prototype.select=function(o)
{
	if (Global.selectedItem) Global.selectedItem.unselect();
	var oItem = document.getElementById("treeItem-text-" + this.id);
	//xuj update 优化xtree通过js调用select（）方法选中节点有背景颜色，同点击选中节点效果，if判断是屏蔽树默认渲染时根节点有背景色问题
	if(this.parent)
		oItem.className = "treeItem-selected";
	//oItem.firstChild.focus();
	Global.selectedItem = this;
	if(!o) this.openURL();
}

treeItem.prototype.unselect=function()
{
	var oItem = document.getElementById("treeItem-text-" + this.id);
	//xuj update 优化xtree通过js调用select（）方法选中节点有背景颜色，同点击选中节点效果
	oItem.className = "treeItem-unselect";
	//oItem.firstChild.blur();
	Global.selectedItem = null;
}

treeItem.prototype.setup = function(oTaget)
{
	oTaget.appendChild(this.toString());
	this.setuped = true;
	if(this.childNodes.length>0 || this.open) this.expand();
}

/**********************************************/
/*
	Arrow Key Event
*/
/**********************************************/

treeItem.prototype.getFirstChild = function()
{
	if(this.childNodes.length>0 && this.open)
		return this.childNodes[0];
	return this;
}

treeItem.prototype.getLastChild = function()
{
	if(this.childNodes.length>0 && this.open)
		return this.childNodes[this.childNodes.length-1].getLastChild();
	return this;
}

treeItem.prototype.getPreviousSibling = function()
{
	if(!this.parent) return null;
	for(var i=0;i<this.parent.childNodes.length;i++)
		if(this.parent.childNodes[i] == this)break;
	if(i == 0) 
		return this.parent;
	else
		return this.parent.childNodes[i-1].getLastChild();
}

treeItem.prototype.getNextSibling = function()
{
	if(!this.parent) return null;
	for(var i=0;i<this.parent.childNodes.length;i++)
		if(this.parent.childNodes[i] == this)break;
	if(i == this.parent.childNodes.length-1)
		return this.parent.getNextSibling();
	else
		return this.parent.childNodes[i+1];
}

treeItem.prototype.KeyDown=function(e){
	var code,o;
	if(!e) e = window.event;
	code = e.which ? e.which : e.keyCode;
	o = this;
	if(code == 37)
	{
		if(o.open) o.collapse();
		else
		{
			if(o.parent) o.parent.select();
		}
		return false;
	}
	else if(code == 38)
	{
		var tmp = o.getPreviousSibling();
		if(tmp) tmp.select();
		return false;
	}
	else if(code == 39)
	{
	    if(o.childNodes.length>0)
		{
			if(!o.open) o.expand();
			else
			{
				var tmp = o.getFirstChild();
				if(tmp) tmp.select();
			}
		}
		return false;
	}
	else if(code == 40)
	{
		if(o.open&&o.childNodes.length>0)
		   o.getFirstChild().select();
		else
		{
			var tmp = o.getNextSibling();
			if(tmp) tmp.select();
		}
		return false;
	}
	else if(code == 13)
	{
		o.toggle();
		o.openURL();
		return false;
	}
	return true;
}
/*****************************************************/
Array.prototype.indexOf=function(o){
	for(var i=0;i<this.length;i++)
		if(this[i]==o)return i;
	return -1;
}

Array.prototype.removeAt=function(i){
	return this.slice(0,i).concat(this.slice(i+1))
}

Array.prototype._remove=function(o)
{
	var i=this.indexOf(o);
	if(i!= -1) return this.removeAt(i)
	return this
}
/*****************************************************/

/*****************************************************/
/*
	xtreeItem Class
	xml:后台的*.JSP
*/
/*****************************************************/

function xtreeItem(uid,text,action,target,title,Icon,xml,nodetype,selectable)
{
	this.uid=uid;
	this.base=treeItem;
	this.base(text,action,target,title,Icon);
	this.Xml=xml;
	if(nodetype=="false")
		this.status=false;
	else
		this.status=true;
	this.selectable = selectable;//根据后台传入的值，为当前节点添加选择状态
	if(selectable=="false")//add by xiegh on date 20180207 多选情况下，当前代码项为不可选状态时，不让复选框显示出来
		this.status=false;//标识出节点前是否加checkbox 或radio
	else
		this.status=true;
}

xtreeItem.prototype=new treeItem;

function XmlDocument() {}
XmlDocument.create = function () {
	try {
		if (document.implementation && document.implementation.createDocument) {
			var doc = document.implementation.createDocument("", "", null);
			if (doc.readyState == null) {
				doc.readyState = 1;
				doc.addEventListener("load", function () {
					doc.readyState = 4;
					if (typeof doc.onreadystatechange == "function")
						doc.onreadystatechange();
				}, false);
			}
			return doc;
		}
	        else if (window.ActiveXObject)
			return new ActiveXObject("Msxml.DomDocument");
	}
	catch (ex) {}
	throw new Error("Your browser does not support XmlDocument objects");
};

if (window.DOMParser &&
	window.XMLSerializer &&
	window.Node && Node.prototype && Node.prototype.__defineGetter__) {

	Document.prototype.loadXML = function (s) {
		
		s=s.replace(/^\s+|\s+$/, '');

		var doc2 = (new DOMParser()).parseFromString(s, "text/xml");
		
		while (this.hasChildNodes())
			this.removeChild(this.lastChild);
		for (var i = 0; i < doc2.childNodes.length; i++) {
			this.appendChild(this.importNode(doc2.childNodes[i], true));
		}
	};
	
	Document.prototype.__defineGetter__("xml", function () {
		return (new XMLSerializer()).serializeToString(this);
	});
	

	

	
// check for XPath implementation
  if( document.implementation.hasFeature("XPath", "3.0") )
  {
    // prototying the XMLDocument
    Document.prototype.selectNodes = function(cXPathString, xNode)
    {
    if( !xNode ) { xNode = this; }
    var oNSResolver = this.createNSResolver(this.documentElement)
    var aItems = this.evaluate(cXPathString, xNode, oNSResolver,
        XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null)
    var aResult = [];
    for( var i = 0; i < aItems.snapshotLength; i++)
    {
    aResult[i] = aItems.snapshotItem(i);
    }
    return aResult;
    }

    // prototying the Element
    Element.prototype.selectNodes = function(cXPathString)
    {
    if(this.ownerDocument.selectNodes)
    {
    return this.ownerDocument.selectNodes(cXPathString, this);
    }
    else{throw "For XML Elements Only";}
    }
  }

// check for XPath implementation
  if( document.implementation.hasFeature("XPath", "3.0") )
  {
    // prototying the XMLDocument
    Document.prototype.selectSingleNode = function(cXPathString, xNode)
    {
    if( !xNode ) { xNode = this; }
    var xItems = this.selectNodes(cXPathString, xNode);
    if( xItems.length > 0 )
    {
    return xItems[0];
    }
    else
    {
    return null;
    }
    }
  
    // prototying the Element
    Element.prototype.selectSingleNode = function(cXPathString)
    {  
    if(this.ownerDocument.selectSingleNode)
    {
    return this.ownerDocument.selectSingleNode(cXPathString, this);
    }
    else{throw "For XML Elements Only";}
    }
  }
  
	
}

xtreeItem.prototype.parseElement=function(dom)
{
	try{//24092  组织机构 信息维护 登记表 页面报错 changxy 20161108
	   return dom.selectSingleNode("/TreeNode");
	}catch(e){
	  return dom.childNodes[0];
	}
/*
	if(document.documentMode >= 10)  
		return dom.childNodes[0];
	else
		return dom.selectSingleNode("/TreeNode"); //xuj 2013-10-25 update 解决ie10不支持dom.selectSingleNode，此处xml层级有有规则可以这也变通解决
		*/
}



xtreeItem.prototype.addNodesLoop = function(oItem)
{
	for(var i=0;i<oItem.childNodes.length;i++)
	{
		var o = oItem.childNodes[i];
		if(o.nodeType!=1)
			continue;
		//alert(o.getAttribute("xml"));
		//alert(getDecodeStr(o.getAttribute("text")));
		var target = o.getAttribute("target");
		if(Global.target)
			target = Global.target;
		
		var tmp = new xtreeItem(o.getAttribute("id"),getDecodeStr(o.getAttribute("text"))/*.replace("&","&amp;")xuj 2010-8-23 解决由&得名称*/,o.getAttribute("href"),target,getDecodeStr(o.getAttribute("title"))/*.replace("&","&amp;")*/,o.getAttribute("icon"),o.getAttribute('xml'),o.getAttribute('type'),o.getAttribute("selectable"));
		//tmp.selectable = o.getAttribute("selectable");//当前节点是否可选
		// 树节点上支持传额外的自定义属性，固定为extraparam guodd 2018-09-07
		var extraparam = o.getAttribute("extraparam");
		if(extraparam)
			tmp.extraparam = extraparam;
		this.add(tmp);
		//alert(o.getAttribute("xml"));//wlh修改
		if(o.getAttribute("xml")!=null && o.getAttribute("xml")!="null") 
		{
		  //alert(o.getAttribute("xml"));
	          tmp.add(new xtreeItem("Loading..."));
		}
		else
		{
			tmp.load=true;
			tmp.addNodesLoop(o);
		}
	}
}

function isbrowser(name)
{
	var index=navigator.userAgent.indexOf(name);
	if (index<0){
		return false;
	}
	else
		return true;
}

xtreeItem.prototype.loadChildren=function()
{
	var oItem = this;	
	var oLoad = oItem.childNodes[0];	
	var xmlhttp;

    if (window.ActiveXObject) 
    {
       xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");

    }	
	else
    {
       xmlhttp = new window.XMLHttpRequest();
    } 	
	//xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	// xmlhttp = new window.XMLHttpRequest();
	var bfirefox=isbrowser("Firefox");
	var statechange=function() {
        if(xmlhttp!=null)
		if(xmlhttp.readyState==4)
		{
			//alert(xmlhttp.responseText);
			var xml;
			if(xmlhttp.responseXML==null)
			{
				xml=xmlhttp.responseText;
			}
			else
				xml=xmlhttp.responseXML.xml;
			if(bfirefox)
			    xml=xmlhttp.responseText;
			    
			if (!window.ActiveXObject)//IE浏览器 不执行trim()方法  因为xml为对象支持trim()方法
				xml = xml.trim();	 //bug35627 xml可能存在空格 导致树加载不出来
			if(xmlhttp.status==200)
			{
				if(xml == "")
				{ 
					 if(oLoad!=null)
						 oLoad.remove();//oLoad.setText("unavaible1");
					 return;
				}
				var XmlItem;
				if (window.DOMParser) { // all browsers, except IE before version 9 xuj 2013-10-25 add 解决ie10解析xml
	                var parser = new DOMParser();
	                try {
	                	XmlItem = parser.parseFromString (xml, "text/xml");
	                } catch (e) {
	                        // if text is not well-formed, 
	                        // it raises an exception in IE from version 9
	                };
	            }else{
					XmlItem = XmlDocument.create();
					XmlItem.async=false;
					XmlItem.loadXML(xml);
				}
	            var XmlItem=oItem.parseElement(XmlItem);
				//var XmlItem=oItem.parseElement(xmlhttp.responseXML.documentElement);
	            if(!XmlItem || !XmlItem.childNodes || XmlItem.childNodes.length == 0)
				{ 
					
					if(oLoad!=null)
						oLoad.remove();//如果未找到子节点，则删除此节点
					//oLoad.setText("unavaible") 
				}
				else
				{
					oItem.addNodesLoop(XmlItem);
					/*
					for(var i=0;i<oItem.childNodes.length;i++)
					{
						
						if(parseInt(getCookie("item"+oItem.childNodes[i].id)) ==1)
						{ 
							oItem.childNodes[i].expand();
						}
					}*/
					if(Global.selectedItem == oItem.childNodes[0])
					   oItem.select();
					if(oLoad!=null)
					  oLoad.remove();
				}
			}
			else
			{
				if(oLoad!=null)
					oLoad.remove();
				//oLoad.setText("unavaible");
			}
			xmlhttp = null;
			if(!Global.selectedItem)
			   oItem.select(1);
		}
	}
	try
	{
	    xmlhttp.open("POST",this.Xml,false);	
		xmlhttp.setRequestHeader("Content-Type","text/xml");
		xmlhttp.setRequestHeader('Content-type','application/x-www-form-urlencoded');
		//定义传输的文件HTTP头信息
		//xmlhttp.setRequestHeader('If-Modified-Since', '0');//清除缓存，根据第二篇文章新加的
		//xmlhttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded");//这段不能少，否则也不能正常发送数据
		/*try{
			//xmlhttp.responseType("msxml-document");
			xmlhttp.responseType='msxml-document';
		}catch(e){}*/
		//xmlhttp.setRequestHeader('Connection', 'close');
		if(!bfirefox)		
	    	xmlhttp.onreadystatechange=statechange;		
		xmlhttp.send("<root></root>");
		if(bfirefox)
			statechange();
        
	}
	catch(e)
	{ 
		if(oLoad){
			oLoad.setText("unavaible");
			oLoad.remove();
		}
	}
}

xtreeItem.prototype.setup=function(oTarget){
	this.add(new treeItem("Loading..."));
	oTarget.appendChild(this.toString());
	this.setuped=true;
	if(this.childNodes.length>0 || this.open) 
		this.expand();
}
/*****************************************
*取得选中的值列表
*格式为12,23,23,
******************************************/
xtreeItem.prototype.getSelected=function()
{ 
  var currnode,values;
  values="";
  if(Global.defaultInput==1)
  {
  		values=",";
        var checkitems=document.getElementsByName("treeItem-check");
        for(var i=0;i<checkitems.length;i++)
        {
          currnode=checkitems[i];
          if(currnode.value=="Loading...")
             continue;
          
          if(currnode.checked)
          {
            values=values+currnode.value+",";
          }
        }
      //解决复选框如果以前选中的节点如果没有展开用getSelected方法未能获取全以前选中值，当Global.checkboxScan=true时，同步Global.checkvalue值（当复选框从选中状态变为未选中状态时去除Global.checkvalue选中节点），这时getSelected方案返回的值为与Global.checkvalue并集
        if(Global.checkboxScan&&Global.checkvalue!=','){
        	var list = Global.checkvalue.split(",");
        	for(var i=0;i<list.length;i++){
        		if(list[i].length>0 && values.indexOf(","+list[i]+",")==-1){
        			values=values+list[i]+",";
        		}
        	}
        }
  }
  else if(Global.defaultInput==2)
  {
        var radioitems=document.getElementsByName("treeItem-radio");
        for(var i=0;i<radioitems.length;i++)
        {
          currnode=radioitems[i];
          if(currnode.checked)
          {
            values=currnode.value;
            break;
          }
        }   
  }
  //【4763】预警设置中，选了角色，保存完，再进来跑到组织机构里了  jingq upd 2014.11.5
  if(values.indexOf(',')==0)
	  values=values.substring(1);
  return values;
}
/***********************************
全选
************************************/
xtreeItem.prototype.allSelect=function()
{ 
  var currnode,values;
  values="";
  if(Global.defaultInput==1)
  {
        var checkitems=document.getElementsByName("treeItem-check");
        for(var i=0;i<checkitems.length;i++)
        {
          currnode=checkitems[i];
          currnode.checked=true;
        }
  }
  else if(Global.defaultInput==2)
  {
  }
}

/**
 * 清空所有节点
 */
xtreeItem.prototype.allClear = function() {
    var currnode, values;
    values = "";
    if (Global.defaultInput == 1) {
        var checkitems = document.getElementsByName("treeItem-check");
        for (var i = 0; i < checkitems.length; i++) {
            currnode = checkitems[i];
            currnode.checked = false;
        }
        if (Global.checkboxScan) {
            Global.checkvalue = ",";
            Global.checkValueTitle = "";
        }
    } else if (Global.defaultInput == 2) {
    	var radioitems = document.getElementsByName("treeItem-radio");//单选按钮选中取消
    	for(var i=0;i<radioitems.length;i++)
        {
          currnode=radioitems[i];
          if(currnode.checked)
          {
        	  currnode.checked = false;
        	  break;
          }
        }   
    }
}

/*****************************************
*取得选中的值列表中对应的描述信息
*格式为：某集团,部门,职位,
******************************************/
xtreeItem.prototype.getSelectedTitle = function() 
{
    var currnode, values;
    values = "";
    if (Global.defaultInput == 1) {
        var checkitems = document.getElementsByName("treeItem-check");
        // 追加已经勾选的复选框
        for (var i = 0; i < checkitems.length; i++) {
            currnode = checkitems[i];
            if (currnode.checked) {
                values = values + currnode.title + ",";
            }
        }
        /*解决复选框如果以前选中的节点如果没有展开用getSelected方法未能获取全以前选中值，
         * 当Global.checkboxScan=true时，同步Global.checkValueTitle值（当复选框从选中状态变为未选中状态时去除Global.checkValueTitle选中节点），
         * 这时getSelectedTitle方法返回的值为与Global.checkValueTitle并集
         */
        if (Global.checkboxScan && Global.checkValueTitle != "") {
        	// 前加“，”号，
        	values = "," + values;
            var list = Global.checkValueTitle.split(",");
            for (var i = 0; i < list.length; i++) {
                if (list[i].length > 0 && values.indexOf("," + list[i] + ",") == -1) {
                    values = values + list[i] + ",";
                }
            }
            // 去掉前加的“，”号
            values = values.replace(",", "");
        }
    } else if (Global.defaultInput == 2) {
        var radioitems = document.getElementsByName("treeItem-radio");
        for (var i = 0; i < radioitems.length; i++) {
            currnode = radioitems[i];
            if (currnode.checked) {
                values = currnode.title;
                break;
            }
        }
    }
    return values;
}

/*****************************************
*根据输入的值列表，把对应节点置上选中状态
*格式为12,23,23,
******************************************/
xtreeItem.prototype.setSelected=function(thevalue)
{ 
  var currnode;
  if(Global.defaultInput==1)
  {
        var checkitems=document.getElementsByName("treeItem-check");
        for(var i=0;i<checkitems.length;i++){
          currnode=checkitems[i];
          if(thevalue.indexOf(currnode.value)!=-1)
          	  currnode.checked=true;
        }
  }
  else if(Global.defaultInput==2)
  {
        var radioitems=document.getElementsByName("treeItem-radio");
        for(var i=0;i<radioitems.length;i++)
        {
          currnode=radioitems[i];
          if(currnode.value==thevalue)
          {
          	currnode.checked=true;
          	break;
          }
        }   
  }
}
/*****************************************************/
function setCookie(name,value)
{
    var Days = 7; 
    var exp  = new Date();
    exp.setTime(exp.getTime() + Days*24*60*60*1000);
    document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
}
function getCookie(name)
{
    var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
    if(arr != null) return unescape(arr[2]); return null;
}
function delCookie(name)
{
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
    var cval=getCookie(name);
    if(cval!=null) document.cookie= name + "="+cval+";expires="+exp.toGMTString();
}
function dragend(table,primarykey_column_name,father_column_name,function_id)
{
	if(table==null||primarykey_column_name==null||father_column_name==null){
		alert("请设置完整参数，表名、主键列名、父节点列名、执行类ID（可选）");
		return;
	}
	var currnode=this.Global.selectedItem;
	if(currnode==null||currnode.dragFrom==null)
			return;
	var flag = false;
	if(function_id==null){//文档管理拖动功能
		flag=true;
		function_id='10400201052';
	}else if('11080204059'==function_id){//常用统计拖动功能
		var fromid=currnode.dragFrom.uid;
		var fromname=currnode.dragFrom.text;
		if(fromid==fromname){
			alert("请选择统计名称修改其所属分类!");
			return;
		}
		var hashvo=new ParameterSet();
		hashvo.setValue("fromid",fromid);
		hashvo.setValue("tablename",table);
		var request=new Request({method:'post',onSuccess:checkStaticDragResult,functionId:'11080204060'},hashvo);
		function checkStaticDragResult(outparamters){
			var msg=outparamters.getValue("msg");
			if(msg!='ok'){
				alert("请选择统计名称修改其所属分类!");
			}else{
				var toid=currnode.uid;
				var toname=currnode.text;
				if(toid!='root'&&toid!=toname){
					alert("请将统计名称修改到选择的分类名称下!");
				}else{
					var hashvo=new ParameterSet();
					hashvo.setValue("fromid",fromid);
					hashvo.setValue("toid",toid);
					hashvo.setValue("tablename",table);
					var request=new Request({method:'post',onSuccess:SaveStaticDragResult,functionId:function_id},hashvo);
				
					function SaveStaticDragResult(outparamters){
						var msg=outparamters.getValue("msg");
						if(msg!='ok'){
							alert("修改所属分类失败!");
						}else{
							var currnode=Global.selectedItem;
							if(currnode==null)
								return;
							currnode=currnode.root();
							if(currnode.load||currnode.uid=='root')
							while(currnode.childNodes.length){
								currnode.childNodes[0].remove();
							}
							currnode.load=true;
							currnode.loadChildren();
							currnode.reload(1);
								
							for(var i=0;i<=currnode.childNodes.length-1;i++){
								if(toid.toUpperCase()==currnode.childNodes[i].uid.toUpperCase()){
									currnode.childNodes[i].expand();
									break;
								}
							}
						}
					}
				}
			}
		}
	}else{//用户管理拖动功能
		var hashvo=new ParameterSet();
		hashvo.setValue("fromid",currnode.dragFrom.uid);
		hashvo.setValue("toid",currnode.uid);
		hashvo.setValue("table",table);
		hashvo.setValue("primarykey_column_name",primarykey_column_name);
		hashvo.setValue("father_column_name",father_column_name);
		hashvo.setValue("function_id",function_id);
		var request=new Request({method:'post',onSuccess:checkDragResult,functionId:'1010010097'},hashvo);
	}
	if(flag){
		if(currnode.dragbool)
		{
			var hashvo=new ParameterSet();
			hashvo.setValue("fromid",currnode.dragFrom.uid);
			hashvo.setValue("toid",currnode.uid);
			hashvo.setValue("table",table);
			hashvo.setValue("primarykey_column_name",primarykey_column_name);
			hashvo.setValue("father_column_name",father_column_name);
			var request=new Request({method:'post',asynchronous:false,onSuccess:null,functionId:function_id},hashvo);
			
			if(currnode.uid=="root"){
				while(currnode.childNodes.length){
					//alert(currnode.childNodes[0].uid);
					currnode.childNodes[0].remove();
				}
				currnode.load=true;
				currnode.loadChildren();
				currnode.reload(1);
			}else{
				currnode.dragFrom.remove();
				for(var i=0;i<currnode.childNodes.length;i++){
					currnode.childNodes[i].remove();
				}
				currnode.expand();
			}
		}
	}
	
}

function setDrag(bool)
{
	this.Global.setDrag(bool);
}

function checkDragResult(outparamters){
	var msg=outparamters.getValue("msg");
	msg=getDecodeStr(msg);
	if(msg!=null&&msg.length>0){//有问题不能拖动
		alert(msg);
	}else{
		var function_id=outparamters.getValue("function_id");
		var table=outparamters.getValue("table");
		var primarykey_column_name=outparamters.getValue("primarykey_column_name");
		var father_column_name=outparamters.getValue("father_column_name");
		var currnode=this.Global.selectedItem;
		var istogroup=outparamters.getValue("istogroup");
		var toid=currnode.uid;
		//是否同组间移动 zhanghua 2019-12-19 18:30:14  1是 0不是
		var isSameGroup="0";
		if(currnode.dragbool)
		{
			if(istogroup=="true"){
				if(currnode.dragFrom.parent.uid==currnode.uid)
					return;
				if(!confirm("确认要将["+currnode.dragFrom.text+"]调整到["+currnode.text+"]下?\n注:被调整用户组或用户原有权限不会改变.")){//许建2010-2-29
					return;
				}
			}else{
				if("root"==currnode.uid){
					if(currnode.dragFrom.parent.uid==currnode.uid)
						return;
					if(!confirm("确认要将["+currnode.dragFrom.text+"]调整到[用户组]下?\n注:被调整用户组或用户原有权限不会改变.")){//许建2010-2-29
						return;
					}
				}else{

					if(currnode.dragFrom.parent.uid==currnode.parent.uid){
						if(confirm("确认要将["+currnode.dragFrom.uid+"]调整到["+currnode.uid+"]的位置？")){
							isSameGroup="1";
						}else{
							return;
						}
					}
					else if(confirm("确认要将["+currnode.dragFrom.text+"]调整到["+currnode.parent.text+"]下?\n注:被调整用户组或用户原有权限不会改变.")){//许建2010-2-29
						toid=currnode.parent.uid;
					}else{
						return;
					}
				}
			}
			var hashvo=new ParameterSet();
			hashvo.setValue("fromid",currnode.dragFrom.uid);
			hashvo.setValue("toid",toid);
			hashvo.setValue("table",table);
			hashvo.setValue("isSameGroup",isSameGroup);
			hashvo.setValue("primarykey_column_name",primarykey_column_name);
			hashvo.setValue("father_column_name",father_column_name);
			var request=new Request({method:'post',asynchronous:false,onSuccess:null,functionId:function_id},hashvo);
			var temp=currnode.dragFrom;
			currnode.dragFrom.remove();
			if(istogroup=="true"){
				
			}else{
				if("root"==currnode.uid){
				}else{
					
					currnode=currnode.parent;
				}
			}
			if(currnode.uid=="root"){
				while(currnode.childNodes.length){
					//alert(currnode.childNodes[0].uid);
					currnode.childNodes[0].remove();
				}
				currnode.load=true;
				currnode.loadChildren();
				currnode.reload(1);
			}else{
				currnode.expand();
				while(currnode.childNodes.length){
					//alert(currnode.childNodes[0].uid);
					currnode.childNodes[0].remove();
				}
				currnode.load=true;
				currnode.loadChildren();
				currnode.reload(1);
			}

		}
	}
}
var class_old_id="";
function selectedClass(id)
{
   if(id.indexOf("treeItem-icon-folder-")!=-1)
   {
     id="treeItem-text-"+id.substring(21);
   }
   var oItem = document.getElementById(id);   

   oItem.className = "treeItem-selected";  
   if(class_old_id!=""&&class_old_id!=id)
   {
     oItem = document.getElementById(class_old_id)
     if(oItem)
     	oItem.className = "treeItem-unselect";
   }
   class_old_id=id;
}



//以下方法是xuj 2010-8-23引用了basic.js的脚本，因为要解决机构树名称中有‘&’字符用到了getEncodeStr()方法

function reNew(str)
{
    var re;
	re=/%26amp;/g;
	str=str.replace(re,"&");
	re=/%26apos;/g;  
	str=str.replace(re,"'");
	re=/%26lt;/g;  
	str=str.replace(re,"<");
	re=/%26gt;/g;  
	str=str.replace(re,">");
	re=/%26quot;/g;  
	str=str.replace(re,"\"");
	re=/%25/g;
	str=str.replace(re,"%");
	re=/````/g;
	str=str.replace(re,",");
	return(str);		
}
/****************************
 *取得合法的字符串
 ****************************/
function getValidStr(str) 
{
	str += "";
	if (str=="undefined" || str=="null" || str=="NaN")
		return "";
	else
		return reNew(str);
		
}
/******************************************
 *字符串解码,汉字传输过程中出现乱码问题
 *解码规则:1) ~43~48~45~4e~48~41~4f
 *         2) ^7a0b^7389
 ******************************************/
function decode(strIn)
{
	var intLen = strIn.length;
	var strOut = "";
	var strTemp;

	for(var i=0; i<intLen; i++)
	{
		strTemp = strIn.charAt(i);
		switch (strTemp)
		{
			case "~":{
				strTemp = strIn.substring(i+1, i+3);
				strTemp = parseInt(strTemp, 16);
				strTemp = String.fromCharCode(strTemp);
				strOut = strOut+strTemp;
				i += 2;
				break;
			}
			case "^":{
				strTemp = strIn.substring(i+1, i+5);
				strTemp = parseInt(strTemp,16);
				strTemp = String.fromCharCode(strTemp);
				strOut = strOut+strTemp;
				i += 4;
				break;
			}
			default:{
				strOut = strOut+strTemp;
				break;
			}
		}

	}
	return (strOut);
}

/*******************************
 *字符串进行编码
 *******************************/

function getDecodeStr(str) {
	return ((str)?decode(getValidStr(str)):"");
}
/* 1.判断去掉勾选的节点是否是之前选中的，如果是则在记录删除节点的变量中记录下此节点；
 * 2.判断选中的节点是否是之前去除掉的，如果是则在记录删除节点的变量中去掉此节点
 *
 *	判断去掉勾选节点的title，记录下来  wangb 20180420
 */
function changeState(obj) {
  if(!obj)
	  return;
  
  if(!obj.checked) {
	  var selectCodeValue = Global.checkvalue;
	  if(selectCodeValue && selectCodeValue.indexOf("," + obj.value + ",") > -1) {
		  if(!Global.removeCodeValues){
			  Global.removeCodeValues = ",";
		  	  Global.removeCodeTitles = ",";
		  }
		  
		  Global.removeCodeValues += obj.value + ",";
		  Global.removeCodeTitles += obj.title + ",";
	  }
  } else {
	  if(Global.removeCodeValues && (Global.removeCodeValues).indexOf("," + obj.value + ",") > -1){
		  Global.removeCodeValues = (Global.removeCodeValues).replace("," + obj.value + ",", ",");
	  	  Global.removeCodeTitles  = (Global.removeCodeTitles).replace("," + obj.title + ",", ",");
	  }
  }
}
