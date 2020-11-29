  var _checkBrowser=true;
  var _disableSystemContextMenu=false;
  var _processEnterAsTab=true;
  var _showDialogOnLoadingData=true;
  var _enableClientDebug=true;
  var _theme_root="/ajax/images";
  var _application_root="";
  var __viewInstanceId="968";
  var ViewProperties=new ParameterSet();
  var childslist;
  var grades;
  var old_rootgrade;
  var dbnames=parent.document.getElementById("dbnames").value;
  var isshowpersonconut=parent.document.getElementById("isshowpersonconut").value;
  var isshoworgconut=parent.document.getElementById("isshoworgconut").value;
  var isshowpersonname=parent.document.getElementById("isshowpersonname").value;
  var isshowposname=parent.document.getElementById("isshowposname").value;
  var isshowdeptname=parent.document.getElementById("isshowdeptname").value;
  var namesinglecell=parent.document.getElementById("namesinglecell").value;
  var orgtype=parent.document.getElementById("orgtype").value;
  var backdate=parent.document.getElementById("backdate").value;
  var cellwidth=parent.document.getElementById("cellwidth").value;
  var cellheight=parent.document.getElementById("cellheight").value;
  var cellletteralignleft=parent.document.getElementById("cellletteralignleft").value;
  var cellletteralignright=parent.document.getElementById("cellletteralignright").value;
  var cellletteraligncenter=parent.document.getElementById("cellletteraligncenter").value;
  var celllettervaligncenter=parent.document.getElementById("celllettervaligncenter").value;
  var cellletterfitsize=parent.document.getElementById("cellletterfitsize").value;
  var cellletterfitline=parent.document.getElementById("cellletterfitline").value;
  var fontfamily=parent.document.getElementById("fontfamily").value;
  var fontstyle=parent.document.getElementById("fontstyle").value;
  var fontsize=parent.document.getElementById("fontsize").value;
  var fontcolor=parent.document.getElementById("fontcolor").value;
  var cellhspacewidth=parent.document.getElementById("cellhspacewidth").value;
  var cellvspacewidth=parent.document.getElementById("cellvspacewidth").value;
  var celllinestrokewidth=parent.document.getElementById("celllinestrokewidth").value;
  var cellshape=parent.document.getElementById("cellshape").value;
  var cellcolor=parent.document.getElementById("cellcolor").value;
  var cellaspect=parent.document.getElementById("cellaspect").value;
  var graph3d=parent.document.getElementById("graph3d").value;
  var graphaspect=parent.document.getElementById("graphaspect").value;
  var code=parent.document.getElementById("code").value;
  var kind=parent.document.getElementById("kind").value;
  function searchChildsList(code,kind,dbname)
  {
      var hashvo=new ParameterSet();
      hashvo.setValue("code",code);
      hashvo.setValue("kind",kind);
      hashvo.setValue("dbnames",dbnames);
      hashvo.setValue("isshowpersonconut",isshowpersonconut);
      hashvo.setValue("isshoworgconut",isshoworgconut);
      hashvo.setValue("isshowpersonname",isshowpersonname);
      hashvo.setValue("isshowposname",isshowposname);
      hashvo.setValue("isshowdeptname",isshowdeptname);
      hashvo.setValue("namesinglecell",namesinglecell);
      hashvo.setValue("orgtype",orgtype);
      hashvo.setValue("backdate",backdate);
     var in_paramters="sss=child";
     var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:getChildsList,functionId:'0405050002'},hashvo);
  }
  function getChildsList(outparamters)
  {
    childslist=outparamters.getValue("childslist");    
    var gv=outparamters.getValue("grades");  
    if(!gv)
      gv=old_rootgrade;          
    grades=parseInt(gv);    
  } 
  function searchPersonList(code,kind,dbname)
  {
    var in_paramters="code="+code + "&kind=" + kind + "&dbname=" + dbname;
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:getPersonList,functionId:'0405050007'});
  }
  function getPersonList(outparamters)
  {
    childslist=outparamters.getValue("personlist"); 
  }   
  function loadChildNode(evt,parentenodestr,code,kind,rootgrade,catalog_id)
  {
     old_rootgrade=rootgrade;
     //var SvgDocument=evt.getTarget().getOwnerDocument();
     var SvgDocument=evt.target.ownerDocument; 
     var childsnodec=SvgDocument.getElementById(parentenodestr+"c");
     var isloadchildren=childsnodec.getAttribute("name");
     if(isloadchildren==null || isloadchildren !=null && isloadchildren.length<=23)
        searchChildsList(code,kind,dbnames);      
     var parameter=new ParameterSet();
     parameter.setValue("cellwidth",cellwidth);
     parameter.setValue("cellheight",cellheight);
     parameter.setValue("cellletteralignleft",cellletteralignleft);
     parameter.setValue("cellletteralignright",cellletteralignright);
     parameter.setValue("cellletteraligncenter",cellletteraligncenter);
     parameter.setValue("celllettervaligncenter",celllettervaligncenter);
     parameter.setValue("cellletterfitsize",cellletterfitsize);
     parameter.setValue("cellletterfitline",cellletterfitline);
     parameter.setValue("fontfamily",fontfamily);
     parameter.setValue("fontstyle",fontstyle);
     parameter.setValue("fontsize",fontsize);
     parameter.setValue("fontcolor",fontcolor);
     parameter.setValue("cellhspacewidth",cellhspacewidth);
     parameter.setValue("cellvspacewidth",cellvspacewidth);
     parameter.setValue("celllinestrokewidth",celllinestrokewidth);
     parameter.setValue("cellshape",cellshape);
     parameter.setValue("cellcolor",cellcolor);
     parameter.setValue("cellaspect",cellaspect);
     parameter.setValue("isshowpersonconut",isshowpersonconut);
     parameter.setValue("isshowpersonname",isshowpersonname);
     parameter.setValue("isshowposname",isshowposname);
     parameter.setValue("namesinglecell",namesinglecell);
     parameter.setValue("graph3d",graph3d);
     parameter.setValue("graphaspect",graphaspect);
     parameter.setValue("rectwidth","10");
     parameter.setValue("dbnames",dbnames);     ;
     grades=grades-rootgrade; 
     _loadChildNode(evt,parentenodestr,childslist,grades,rootgrade,catalog_id,parameter);
  } 
  function loadPersonNode(evt,parentenodestr,code,kind,rootgrade,catalog_id,dbname,isupright)
  {
     var SvgDocument=evt.target.ownerDocument; 
     var childsnodec=SvgDocument.getElementById(parentenodestr+"c");
     var isloadchildren=childsnodec.getAttribute("name");
     if(isloadchildren==null || isloadchildren !=null && isloadchildren.length<=23)
       searchChildsList(code,kind,dbnames);
        
        
     var parameter=new ParameterSet();
     parameter.setValue("cellwidth",cellwidth);
     parameter.setValue("cellheight",cellheight);
     parameter.setValue("cellletteralignleft",cellletteralignleft);
     parameter.setValue("cellletteralignright",cellletteralignright);
     parameter.setValue("cellletteraligncenter",cellletteraligncenter);
     parameter.setValue("celllettervaligncenter",celllettervaligncenter);
     parameter.setValue("cellletterfitsize",cellletterfitsize);
     parameter.setValue("cellletterfitline",cellletterfitline);
     parameter.setValue("fontfamily",fontfamily);
     parameter.setValue("fontstyle",fontstyle);
     parameter.setValue("fontsize",fontsize);
     parameter.setValue("fontcolor",fontcolor);
     parameter.setValue("cellhspacewidth",cellhspacewidth);
     parameter.setValue("cellvspacewidth",cellvspacewidth);
     parameter.setValue("celllinestrokewidth",celllinestrokewidth);
     parameter.setValue("cellshape",cellshape);
     parameter.setValue("cellcolor",cellcolor);
     parameter.setValue("cellaspect",cellaspect);
     parameter.setValue("isshowpersonconut",isshowpersonconut);
     parameter.setValue("isshowpersonname",isshowpersonname);
     parameter.setValue("isshowposname",isshowposname);
     parameter.setValue("namesinglecell",namesinglecell);
     parameter.setValue("graph3d",graph3d);
     parameter.setValue("graphaspect",graphaspect);
     parameter.setValue("rectwidth","10");
     parameter.setValue("dbnames",dbnames);
     
     grades=rootgrade+1;     
     _loadChildNode(evt,parentenodestr,childslist,grades,rootgrade,catalog_id,parameter);
  }  

function viewmess(hreflink)
{
  if(hreflink!=null&&hreflink!="undefined"&&hreflink!=""){
  	var a=parent.document.getElementById("aid");
  	a.href=hreflink;
  	a.click();
  }
}