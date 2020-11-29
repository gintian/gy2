var whichOpen;
var whichContinue;
 
/*
 *左边菜单js
 */
function menuShow(obj,maxh,obj2,arrow) {
  if(obj.style.pixelHeight<maxh)  
  {
    obj.style.pixelHeight+=maxh/1;
 	 obj.filters.alpha.opacity+=0;
    arrow.innerHTML="<img src=/images/rarrow.gif border=0>";
    if(obj.style.pixelHeight>=maxh/1) 
    {
      obj.style.display="block";
      obj.style.pixelHeight = maxh;
    }  
    myObj=obj;
    myMaxh=maxh;
    myObj2=obj2;
    myarrow=arrow
    setTimeout("menuShow(myObj,myMaxh,myObj2,myarrow)","5");
  }
}

function menuHide(obj,maxh,obj2,arrow){
  if(obj.style.pixelHeight>0)  
  {
    if(obj.style.pixelHeight==maxh/1)
      obj.style.display="none";
    obj.style.pixelHeight-=maxh/1;
    obj.filters.alpha.opacity-=0;
    arrow.innerHTML="<img src=/images/darrow.gif border=0>";
    myObj=obj;
    myMaxh=maxh
    myObj2=obj2;
    myarrow=arrow
    setTimeout("menuHide(myObj,myMaxh,myObj2,myarrow)","5");
  }
  else
    if(whichContinue)
      whichContinue.click();
}

function menuChange(obj,maxh,obj2,arrow){
  if(obj.style.pixelHeight)  
  {
    menuHide(obj,maxh,obj2,arrow);
    whichOpen="";
    whichcontinue="";
  }
  else
    if(whichOpen) 
    {
      whichContinue=obj2;
      whichOpen.click();
    } 
    else 
    {
      menuShow(obj,maxh,obj2,arrow);
      whichOpen=obj2;
      whichContinue="";
    }
}

var preIdMenu='';
 
function changMenuStyle(id) {
  try
  {
    eval("window."+id+".style.color='#FDE804'");
    if(preIdMenu!="") {
   	if(id!=preIdMenu){
      	eval("window."+preIdMenu+".style.color='#ffffff'");
      }   
    }         
    preIdMenu=id; 
  }
  catch (ex)
  {
  	//alert(ex);
  }
}

function getIEVersion(){
	var index=window.clientInformation.userAgent.indexOf("MSIE");
	if (index<0){
		return "";
	}
	else{
		return window.clientInformation.userAgent.substring(index+5, index+8);
	}
}

/*权限控制时，可能menuTitle1第一个不出现，js会出错*/
function showFirst(frmname)
{
  var subTables=document.getElementsByTagName("table");
  var idx="0";
  var j=0;
  for(var i=0;i<subTables.length;i++)
  {
   		if(subTables[i].className!="menu_table")
      		continue;
      	idx=subTables[i].getAttribute("index");
		break;
  }
  //get second menuitem count.
  for(var i=0;i<subTables.length;i++)
  {
   		if(subTables[i].className!="menu_table")
      		continue;
      	j=j+1;
  }  
  
  var ver=getIEVersion();

  if((window.screenTop==128||window.screenTop==127)||window.screenTop==165||window.screenTop==91)//fullscreen status
  {
	   divHeight = window.screen.availHeight - window.screenTop -(j*25+21);  
	   if(ver=="6.0")
	      divHeight=divHeight+10;
  }
  else
  {
	   divHeight = window.screen.availHeight - window.screenTop -(j*25+21+20); //add other height
	   if(ver=="6.0")
	      divHeight=divHeight-10;
  }
  /*一个都没有*/
  if(idx=="0")
  	return;
  whichOpen=eval("menuTitle"+idx); //menuTitle1
  whichContinue="";
  var menu=eval("menu"+idx);
  menu.style.height =divHeight;
  menu.style.display="block";
  if(frmname!="hl")
     parent.frames[1].name = "il_body";

}

