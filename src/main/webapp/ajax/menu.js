var _fileIncluded_menu=true;

var _top_menuItem=null;
var _current_menuItem=null;
var _stored_item=null;
var _stored_frame=null;
var _array_menu=new Array();

function _menu_getPopupContainer() {
	return this.popupContainer;
}

function _menu_setPopupContainer(popupContainer) {
	this.popupContainer=popupContainer;
}

function _menu_getTarget() {
	return this.target;
}

function _menu_setTarget(targetFrame) {
	this.targetFrame=target;
}

function _menu_getTopItem() {
	return this.topItem;
}


function createMenu(id) {
	var menu=new Object();
	menu.id=id;
	menu.items=new Array();	
	menu.topItem=_createMenuItem(menu, null);
	menu.showMenu=menu_showMenu;
	menu.getPopupContainer=_menu_getPopupContainer;	
	menu.setPopupContainer=_menu_setPopupContainer;	
	menu.getTarget=_menu_getTarget;	
	menu.setTarget=_menu_setTarget;	
	menu.getTopItem=_menu_getTopItem();	
	return menu;
}

function getAllCheckedMenus(menu)
{
    var chkmenu=new Array();
	for(var i=0;i<menu.items.length;i++)
	{
		var menuitem=menu.items[i];
		if(menuitem.checked)
			chkmenu[chkmenu.length]=menuitem.name;
	}
	return chkmenu;
}

function getMenuItem(name)
{
   var menuitem=null;
   for(var i=0;i<_array_menu.length;i++)
   {
     var menu=_array_menu[i];
     for(var j=0;j<menu.items.length;j++)
     {
         var topmenuitem=menu.items[j];  
		 if(topmenuitem.name==name)
		 {
		 	menuitem=topmenuitem;
		 	break;
		 }
     }
   }	
   return menuitem;
}

function initMenu(menu){
	/**chenmengqing changed at 20051223 for support firefox,ie*/
	var menuxml=document.getElementById("__"+menu.id);
	if(menuxml!=null)
	{
	  //eval("var xmlIsland=__"+menu.id);		
	  root=menuxml.documentElement;
	  if(!root)
	    return;
	  initMenuItems(menu, menu.topItem, root);
	  _xml_list[_xml_list.length]=menuxml;//xmlIsland;	  
	}
	/* ie ,not support firefox
	eval("var isXmlExist=(typeof(__"+menu.id+")==\"object\")");
	if (isXmlExist) 
	{
		eval("var xmlIsland=__"+menu.id);
		root=xmlIsland.documentElement;
		if (!root) 
		   return;
		initMenuItems(menu, menu.topItem, root);
		_xml_list[_xml_list.length]=xmlIsland;
	}
	*/
	menu.menuItems=null;
	_array_menu[_array_menu.length]=menu;
}

function _menuItem_getName() {
	return this.name;
}

function _menuItem_getLabel() {
	return this.label;
}

function _menuItem_setLabel(label) {
	this.label = label;
}

function _menuItem_getLevel() {
	return this.level;
}

function _menuItem_isEnabled() {
	return this.enabled;
}

function _menuItem_setEnabled(enabled) {
	this.enabled = enabled;
}

function _menuItem_isVisible() {
	return this.visible;
}

function _menuItem_setVisible(visible) {
	this.visible = visible;
}

function _menuItem_getIcon() {
	return this.icon;
}

function _menuItem_setIcon(icon) {
	this.icon = icon;
}

function _menuItem_getPath() {
	return this.path;
}

function _menuItem_setPath(path) {
	this.path = path;
}

function _menuItem_getCommand() {
	return this.command;
}

function _menuItem_setCommand(command) {
	this.command = command;
}

function _menuItem_getTag() {
	return this.tag;
}

function _menuItem_setTag(tag) {
	this.tag = tag;
}

function _menuItem_getMenu() {
	return this.menu;
}

function _menuItem_items() {
	return this.items;
}

function _menuItem_setChecked(checked) {
	this.checked=checked;
}

function _menuItem_getChecked() {
	return this.checked;
}

function _menuItem_setGroupIndex(groupindex) {
	this.groupindex=groupindex;
}

function _menuItem_getGroupIndex() {
	return this.groupindex;
}

function _menuItem_setHint(hint) {
	this.hint=hint;
}

function _menuItem_getHint() {
	return this.hint;
}

function _createMenuItem(menu, parentItem) {
	var item=new Object();
	
	item.getName=_menuItem_getName;
	item.getLabel=_menuItem_getLabel;
	item.setLabel=_menuItem_setLabel;
	item.getLevel=_menuItem_getLevel;
	item.isEnabled=_menuItem_isEnabled;
	item.setEnabled=_menuItem_setEnabled;
	item.isVisible=_menuItem_isVisible;
	item.setVisible=_menuItem_setVisible;
	item.getIcon=_menuItem_getIcon;
	item.setIcon=_menuItem_setIcon;
	item.getPath=_menuItem_getPath;
	item.setPath=_menuItem_setPath;
	item.getCommand=_menuItem_getCommand;
	item.setCommand=_menuItem_setCommand;
	item.getTag=_menuItem_getTag;
	item.setTag=_menuItem_setTag;
	item.getMenu=_menuItem_getMenu;
	item.items=_menuItem_items;
	item.getChecked=_menuItem_getChecked;
	item.setChecked=_menuItem_setChecked;
	item.getGroupIndex=_menuItem_getGroupIndex;
	item.setGroupIndex=_menuItem_setGroupIndex;
	item.getHint=_menuItem_getHint;
	item.setHint=_menuItem_setHint;
	//
	menu.items[menu.items.length]=item;
	item.items=new Array();
	item.menu=menu;
	if (parentItem){
		item.level=parentItem.level+1;
		item.zIndex=parentItem.zIndex+1;
		item.parentItem=parentItem;
		parentItem.items[parentItem.items.length]=item;
	}
	else{
		item.level=0;
		item.zIndex=10000;
	}
	return item;
}

function initMenuItems(menu, parentItem, node){		
	var itemNodes = node.childNodes;
	for (var i=0; i<itemNodes.length; i++) {
		var itemNode = itemNodes.item(i);			
		var newItem=_createMenuItem(menu, parentItem);
		newItem.name=itemNode.getAttribute("name");	
		newItem.label=itemNode.getAttribute("label");
		newItem.enabled=isTrue(itemNode.getAttribute("enabled"));			
		newItem.visible=isTrue(itemNode.getAttribute("visible"));			
		newItem.icon=itemNode.getAttribute("icon");			
		newItem.path=itemNode.getAttribute("path");		
		newItem.command=itemNode.getAttribute("command");			
		newItem.tag=itemNode.getAttribute("tag");
		newItem.checked=isTrue(itemNode.getAttribute("checked"));
		newItem.groupindex=itemNode.getAttribute("groupindex");
		newItem.onceclicked=itemNode.getAttribute("onceclick");
		newItem.hint=itemNode.getAttribute("hint");

		initMenuItems(menu, newItem, itemNode);
	}
}

function prepareMenu(menuItem){
	if (menuItem.items.length<1) 
	   return;

	var frame=menuItem.subFrame;

	if (!frame){
		frame=document.createElement("<div extra=menuframe class=\"menuframe\" style=\"position:absolute; visibility:hidden" +
			"z-index: "+menuItem.zIndex+"\"></div>");
		document.body.appendChild(frame);
		with (frame){
			innerHTML="<table width=128px border=0 cellspacing=0 cellpadding=4 rules=all class=menu></table>";
			onmouseover=_menu_onmouseover;
			onmousedown=_menu_onmousedown;
		}

		var row=frame.firstChild.insertRow();
		row.extra="menuitem";

				
		var cell=row.insertCell();
		cell.width="16px";
		
		cell=row.insertCell();
		cell.noWrap=true;
		
		cell=row.insertCell();
		cell.width="9px";
		
		cell=row.insertCell();
		cell.width="9px";
				
		frame.repeatrow=row.cloneNode(true);

		frame.menuItem=menuItem;
		menuItem.subFrame=frame;
	}

	var tBody=frame.firstChild.tBodies[0];
	for (var i=tBody.rows.length-1; i>=0; i--) tBody.rows[i].removeNode(true);

	var row, cell;
	for(var i=0; i<menuItem.items.length; i++){
		var item=menuItem.items[i];
		fireUserEvent(getElementEventName(menuItem.menu, "onRefreshItem"), [menuItem.menu, item]);
		if (!item.visible) continue;

		row=frame.repeatrow.cloneNode(true);
		frame.firstChild.tBodies[0].insertAdjacentElement("beforeEnd", row);

		row.className=(item.enabled)?"":"row_disabled";
		
		if (item.icon) row.cells[0].innerHTML="<img src=\""+item.icon+"\">";
		row.cells[1].innerHTML=item.label;
		if (item.toolTip) row.title=item.toolTip;
		if (item.items.length>0) row.cells[2].innerHTML="<label style=\"font-family: Webdings; font-size: 7pt\">4</label>";
		if(item.checked)
			row.cells[3].innerHTML="<img src=\"/images/cc1.gif\">";		
		item.row=row;
		item.frame=frame;
		row.menuItem=item;
	}
	return frame;
}

function showMenu(menu){
	if (_top_menuItem){
		hideMenu();
		return;
	}

	menu.showMode="popup";
	_showSubMenu(menu.topItem, "popup", null, true);
	_stored_frame=menu.topItem.subFrame;
	setTimeout("_stored_frame.focus();", 0);
	_top_menuItem=menu.topItem;
}

function menu_showMenu(){
	showMenu(this);
}

function showButtonMenu(menu, button){
	if (_top_menuItem){
		hideMenu();
		return;
	}

	menu.showMode="button";
	_showSubMenu(menu.topItem, "button", button, true);
	_stored_frame=menu.topItem.subFrame;
	setTimeout("_stored_frame.focus();", 0);
	_top_menuItem=menu.topItem;
}

function showBarMenu(menuItem, button){
	if (_top_menuItem) hideMenu();

	menuItem.button=button;
	menuItem.menu.showMode="menubar";
	_showSubMenu(menuItem, "button", menuItem.button, false);
	_top_menuItem=menuItem;
}

function _locateMenu(frame, locateMode, element){
	switch (locateMode){
	case "popup":
		var tmp_left, tmp_top;
		if (event.x+frame.offsetWidth>document.body.clientWidth-2)
			tmp_left=event.x-frame.offsetWidth+5;
		else
			tmp_left=event.x-5;

		if (event.y+frame.offsetHeight>document.body.clientHeight-1)
			tmp_top=event.y-frame.offsetHeight+6;
		else
			tmp_top=event.y-4;

		frame.style.posLeft=tmp_left+document.body.scrollLeft;
		frame.style.posTop=tmp_top+document.body.scrollTop;
		break;
	case "button":
		var pos=getAbsPosition(element, document.body);

		if (pos[0]+frame.offsetWidth>document.body.clientWidth-2)
			frame.style.posLeft=pos[0]+element.offsetWidth-frame.offsetWidth-2;
		else
			frame.style.posLeft=pos[0];

		if (pos[1]+element.offsetHeight+frame.offsetHeight>document.body.clientHeight-1)
			frame.style.posTop=pos[1]-frame.offsetHeight-1;
		else
			frame.style.posTop=pos[1]+element.offsetHeight+2;
		break;
	case "submenu":
		var pos=getAbsPosition(element, document.body);

		if (pos[0]+element.offsetWidth+frame.offsetWidth>document.body.clientWidth-2)
			frame.style.posLeft=pos[0]-frame.offsetWidth;
		else
			frame.style.posLeft=pos[0]+element.offsetWidth;
		//chenmengqing 菜单太多啦。。。
		//if (pos[1]+frame.offsetHeight>document.body.clientHeight-1)
		//	frame.style.posTop=pos[1]+element.offsetHeight-frame.offsetHeight+1;
		//else
			frame.style.posTop=pos[1]+1;
		break;
	}
}

function _showSubMenu(menuItem, locateMode, element, animate){
	var frame=prepareMenu(menuItem);
	if (!frame) return;
	_locateMenu(frame, locateMode, element);
	if (frame.filters.blendTrans.status!=2){
		if (!animate || getIEVersion()<"5.5"){
			frame.style.visibility="visible";
		}
		else{
			frame.filters.blendTrans.apply();
			frame.style.visibility="visible";
			frame.filters.blendTrans.play();
		}
	}
	return frame;
}

function _checkMenu(menuItem)
{
	if(!menuItem.groupindex)
	  return;
    var grpidx=menuItem.groupindex;
    var menuname=menuItem.name;
    var row;
    if(grpidx=="-1") 
       return;
    var parentItem=menuItem.parentItem;
	if (parentItem)
	{
		for(var i=0; i<parentItem.items.length; i++){
			var item=parentItem.items[i];
		   	row=item.row;
			if(item.name==menuname)
			       item.checked=true;
			else
			{
				if(item.groupindex==grpidx)
					item.checked=false;
			}
		}
	}
}

function _hideMenu(menuItem){
	if (menuItem.currentMenuItem) _hideMenu(menuItem.currentMenuItem);
	if (menuItem.parentItem) menuItem.parentItem.currentMenuItem=null;
	if (menuItem==_current_menuItem) _current_menuItem=null;
	if (menuItem==_top_menuItem){
		if (_top_menuItem.menu.showMode=="menubar"){
			//_top_menuItem.cell.style.backgroundImage = "url("+_theme_root+"/button.gif)";
			_top_menuItem.cell.className="button";
			var menubar=getTableByCell(_top_menuItem.cell);
			menubar.setAttribute("menuOpened", false);
		}
		_top_menuItem=null;
	}

	var frame=menuItem.subFrame;
	if (!frame) return;
	if (frame.style.visibility!="visible") return;
	frame.style.visibility="hidden";
}

function hideMenu(){
	if (!_top_menuItem) return;
	_hideMenu(_top_menuItem);
}

function _findMenuItemHolder(element){
	while (element){
		if (element.getAttribute("extra")=="menuitem")
			return element;
		element=element.parentElement;
	}
}

function _menu_onmouseover() {
	var element=_findMenuItemHolder(event.srcElement);

	if (element){
		var menuItem=element.getAttribute("menuItem");
		if (menuItem==_current_menuItem) return;
		_current_menuItem=menuItem;
		if (menuItem){
			if (menuItem.enabled){
				element.className="row_selected";
				_showSubMenu(menuItem, "submenu", element, true);
			}

			var currentSlideItem=menuItem.parentItem.currentMenuItem;
			var newSlideItem=null;
			if (currentSlideItem){
				if (currentSlideItem!=menuItem){
					_hideMenu(currentSlideItem);
				}
				if (currentSlideItem.parentItem==menuItem.parentItem){
					currentSlideItem.row.className=(currentSlideItem.enabled)?"":"row_disabled";
					menuItem.parentItem.currentMenuItem=menuItem;
				}
			}
			else{
				menuItem.parentItem.currentMenuItem=menuItem;
			}
		}
	}
}

function _menu_onmousedown() {
	var frame=_current_menuItem.frame;
	if (frame && frame.filters.blendTrans.status==2) return;

	if (event.button!=2){
		var element=_findMenuItemHolder(event.srcElement);

		if (element){
			var menuItem=element.getAttribute("menuItem");
			if (menuItem && isTrue(menuItem.enabled)) _processMenuItemClick(menuItem.menu, menuItem);
		}
	}
	hideMenu();
}

function _menuBar_getMenu() {
	return this.menu;
}

function initMenuBar(menubar){
	menubar.getMenu=_menuBar_getMenu;
	menubar.refreshBar = menubar_refreshBar;
	menubar.refreshBar();
}

function _menubar_refreshBar(menubar) {
	var menu=menubar.getAttribute("menu");
	if (typeof(menu)=="string" && menu!="")
	{
		eval("menu="+menu);
		menubar.menu=menu;
	}

	if (menubar.menu)
	{
		for(var i=0; i<menubar.tBodies[0].rows.length; i++)
		{
      			var row=menubar.tBodies[0].rows[i];
     			 row.removeNode(true);
    		}

		var row=menubar.tBodies[0].insertRow();
		row.align="center";
		for(var i=0; i<menu.topItem.items.length; i++)
		{
			var item=menu.topItem.items[i];
			fireUserEvent(getElementEventName(menu, "onRefreshItem"), [menu, item]);
			if (!item.visible) 
			   continue;

			var cell=row.insertCell();
			cell.style.borderRight="1px solid #c5c5c5";
			cell.innerHTML="<button hideFocus=\"true\"></button>";
			var button=cell.firstChild;

			button.extra="menuitem";
			button.className="button";
			if (item.icon){
				button.innerHTML="<img src=\""+item.icon+"\" style=\"margin-right: 4px\">"+item.label;
			}
			else{
				button.innerText=item.label;
			}
			//button.style.backgroundImage = "url("+_theme_root+"/button.gif)";
			button.onmouseover=_menubar_onmouseover;
			button.onmouseout=_menubar_onmouseout;
			button.onclick=_menubar_onclick;
			if(item.toolTip)
			   button.title=item.toolTip;
			button.menuItem=item;
			button.disabled=!isTrue(item.enabled);
			item.cell=button;
		}
	}
}

function menubar_refreshBar() {
	 _menubar_refreshBar(this);
}

function _menubar_onmouseover() {
	var button=_findMenuItemHolder(event.srcElement);
	if (button){
		//button.style.backgroundImage = "url()";
		button.className="button_hot";

		var menubar=getTableByCell(button);
		if (menubar.getAttribute("menuOpened")) {
			var menuItem=button.getAttribute("menuItem");
			if (menuItem==_current_menuItem) return;

			if (menuItem){
				var currentSlideItem=menuItem.parentItem.currentMenuItem;
				var newSlideItem=null;

				if (menuItem.items.length>0){
					//button.style.backgroundImage = "url()";
					button.className="button_active";

					showBarMenu(menuItem, button);
					_stored_frame=menubar;
					setTimeout("_stored_frame.focus();", 0);
				}

				if (currentSlideItem){
					if (currentSlideItem!=menuItem){
						_hideMenu(currentSlideItem);
					}
				}
				else{
					menuItem.parentItem.currentMenuItem=menuItem;
				}

				menubar.setAttribute("menuOpened", true);
			}
		}
	}
}

function _menubar_onmouseout() {
	var button=_findMenuItemHolder(event.srcElement);
	if (button){
		var menubar=getTableByCell(button);
		var menuItem=button.getAttribute("menuItem");
		if (!menubar.getAttribute("menuOpened") || menuItem.items.length==0) {
			//button.style.backgroundImage = "url("+_theme_root+"/button.gif)";
			button.className="button";
		}
	}
}

function _menubar_onclick() {
	if (event.button!=2){
		var button=_findMenuItemHolder(event.srcElement);
		if (button){
			var menuItem=button.getAttribute("menuItem");
			if (menuItem && isTrue(menuItem.enabled)) _processMenuItemClick(menuItem.menu, menuItem);

			if (menuItem.items.length>0){
				var menubar=getTableByCell(button);
				if (!menubar.getAttribute("menuOpened")){
					menubar.setAttribute("menuOpened", true);
					_menubar_onmouseover();
					return;
				}
			}
		}
	}
}

function _processMenuItemClick(menu, menuItem){	
	try{
		_checkMenu(menuItem);
		hideMenu();
		var event_name=getElementEventName(menu, "onItemClick");
		if (isUserEventDefined(event_name)){
			var event_result=fireUserEvent(event_name, [menu, menuItem]);
			if (event_result) throw event_result;
		}
		
		if (menuItem.command) {
			eval("var command = " + menuItem.command);
			var hint=menuItem.getHint();
			if(hint&&hint!="")
			{
				if(confirm(hint))
				  command.execute();
			}
			else
				  command.execute();			
			//command.execute();

		}
		else if (getValidStr(menuItem.path)!=""){
			eval(menuItem.path);
			//open(menuItem.path, menu.target);
		}
		if(menuItem.onceclicked=="true")
		    menuItem.enabled=false;
	}
	catch(e){
		processException(e);
	}
}