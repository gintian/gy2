function objcat(destination, source) {
  for (property in source) {
    destination[property] = source[property];
  }
  return destination;
}

HashMap.prototype.put = function(name,value) {
	this[name]=value;
}
HashMap.prototype.get = function(name) {
	return this[name];
}

function HashMap()
{
	
}

function Rpc(options,hashvo)
{		
       var setopt={type:'post',url:'/ajax/ajaxService',functionId:'',dataType: "html"};//.extend(options||{});
       setopt=objcat(setopt,options);
	   var functionId=setopt['functionId'];
	   var jsonstr;
	   hashvo.put("functionId",functionId);

	   if(hashvo instanceof HashMap)
	   {
	   	  jsonstr=JSON.stringify(hashvo);//Ext.util.JSON.encode(hashvo);
	   }
	   var jsonobj={"__xml":jsonstr,"__type":"extTrans"};
	   setopt.data=jsonobj;
		
	   $.ajax(setopt);
}