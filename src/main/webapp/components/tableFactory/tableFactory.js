/**
 * 此文件为兼容旧程序
 */
Ext.Loader.setConfig({
	scriptCharset:'UTF-8',
	paths:{
		"EHR":"/components"
	}
});
if(!Ext.additionLoaded)
	document.write("<script type='text/javascript' src='/ext/ext6/ext-additional.js' ></script>");
document.write("<script type='text/javascript' src='/components/tableFactory/TableBuilder.js' onload='DefineTableFactory()' onreadystatechange='DefineTableFactory(this)'></script>");
function DefineTableFactory(loadObj){
	if(loadObj && loadObj.readyState!='complete')
           return;
    Ext.define("BuildTableObj",{
        extend:'EHR.tableFactory.TableBuilder'
    });       
}
