(function(){
     var scriptEls = document.getElementsByTagName('script'),
          len = scriptEls.length,
          rootPath,script,src,n;
          
      for(n=0;n<len;n++){
      		src = (script = scriptEls[n]).src;
            if (!src) {
               continue;
            }
            if(src.indexOf('/module/utils/js/template.js')!=-1){
                rootPath = src.replace('/module/utils/js/template.js','');
                break;
            }
      
      }
      window.rootPath = rootPath;
  })();
  
document.write("<script type='text/javascript' src='"+rootPath+"/ext/ext6/ext-all.gzjs' onload='ResourceLoader.setEHRPath()' onreadystatechange='ResourceLoader.setEHRPath(this)'></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/ext/ext6/locale-zh_CN.js' ></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/ext/ext6/ext-additional.js' ></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/module/secondary-dev.js' ></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/ext/ext6/ext-SecondaryDev-controller.js' ></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/ext/ext6/charts.js' ></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/ext/rpc_command.js'></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/ajax/basic.js'></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/js/validate.js'></script>");
document.write("<script type='text/javascript' src='"+rootPath+"/module/utils/js/htmlSessionOut.js' ></script>");
document.write("<link rel='stylesheet' href='"+rootPath+"/ext/ext6/resources/ext-theme.css' type='text/css' />");


// 此处先固定，以后可以修改此变量
var language = "zh_CN";
// 获取公共国际化文件
document.write("<script type='text/javascript' src='"+rootPath+"/module/utils/js/resource_" + language + ".js'></script>");
// 获取模块标识
var moduleIndex = window.location.pathname.indexOf("/module/");
if(moduleIndex!=-1)
{
	var moduleStr = window.location.pathname.substring(moduleIndex + 8);
	moduleStr = moduleStr.substring(0, moduleStr.indexOf("/"));
	if(moduleStr!='system')
		document.write("<script type='text/javascript' src='"+rootPath+"/module/" + moduleStr + "/" + moduleStr + "_resource_" + language + ".js'></script>");
}

//加载项目资源文件对象
(function(){
    ResourceLoader = window.ResourceLoader||{};
    ResourceLoader.setEHRPath = function(loadObj){
       if(loadObj && loadObj.readyState!='complete')
           return;
       //设置自定义组件加载路径，并设置js加载编码为GBK
       Ext.Loader.setConfig({
		   scriptCharset:'UTF-8',
		   paths:{
				"EHR":rootPath+"/components"
		   }
	   });
    };
    //传入项目路径
    ResourceLoader.load = function(projectPath){
	    if(new RegExp("/$").test(projectPath))
	   		projectPath = projectPath.substring(0,projectPath.length-1);
	   	var projectName = projectPath.substring(projectPath.lastIndexOf('/')+1);
	   	document.write("<script type='text/javascript' src='"+ rootPath+projectPath + "/" + projectName + "_resource_" + language + ".js'></script>");
	};
})();