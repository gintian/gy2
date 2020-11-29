//Ext.Loader 对象Ext4及以上版本才有，如果没有通过JS创建script节点加载aes.js文件
if(Ext.Loader && Ext.Loader.loadScript){
    Ext.Loader.loadScript({url:"/js/sm4.js"});
}else{
	var oHead = document.getElementsByTagName('HEAD').item(0); 
    var oScript= document.createElement("script"); 
    oScript.type = "text/javascript"; 
    oScript.src="/js/sm4.js";
    oHead.appendChild( oScript); 
}
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
		if(!window.SM4){//避免加载sm4.js慢导致window找不到SM4对象的问题
			setTimeout(function(){
				Rpc(options,hashvo);
			},100);
			return;
		}
		
	   var __type = "extTrans";
	   if(hashvo.__type){
	   		__type = hashvo.__type;
	   		delete hashvo.__type;
	   }		
       var setopt={method:'post',url:'/ajax/ajaxService',functionId:'',failure:requestError};//.extend(options||{});
       setopt=objcat(setopt,options);           
	   var functionId=setopt['functionId'];	    
	   var action=setopt['action']; 	   
	   var jsonstr;
	   hashvo.put("functionId",functionId);
	   
	   if(hashvo instanceof HashMap)
	   {
	   	   jsonstr=Ext.encode(hashvo);
	   }
	   
	   var encodeStr = encrypt(jsonstr);
	   var jsonobj={"__xml":encodeStr,"__type":__type};
	   setopt.params=jsonobj;
	   setopt.timeout=1800000;
	   if(options.success){
		   /*异步调用ajax在回调方法中调用window.open时，在safari浏览器下window.open失败(safari机制问题)。
		     这里控制一下，如遇到上述问题，改为同步 guodd 2016-08-19*/
		   var callbackStr = options.success.toString();
		   //查找有没有window.open('字符，并且window.open前面一个字符不是字母和数字
		   var patt1=/[^a-zA-Z0-9]window.open\(['|"]/;
		   //如果有并且是safari浏览器，改成同步
		   if(patt1.test(callbackStr)&& Ext.isSafari)
		      setopt.async = false;
	   }
	   Ext.Ajax.request(setopt);
}

/**
 * 长链接rpc
 * 使用轮询获取数据，解决需要较长时间的交易链接中断
 * @param options
 * @param hashvo
 * @param time 间隔时间 默认为5秒
 * @param maxcount 最大轮询次数 默认为轮询1小时
 * @constructor
 *  zhanghua 2018-12-15
 */
function LRpc(options,hashvo,time,maxcount) {

    var functionId = options.functionId;
    if(hashvo==undefined){
        hashvo=new HashMap();
	}

    hashvo.put("getResult_Type", "init");
    hashvo.put("getResult_Functionid", functionId);
    Rpc({functionId: 'ZJ100000311', async: true}, hashvo);

    // 默认间隔为10秒 总时长为1小时
    if (time == undefined||time==0||time=='') {
        time = 5000;
    }
    if (maxcount == undefined||maxcount==0||maxcount=='') {
        maxcount = 3600000 / time;
    }
    pollingGetResult(options, hashvo, time, 0,maxcount);
}

/**
 * 递归轮询方法
 * @param options
 * @param hashvo
 * @param time 间隔时间
 * @param count 执行次数
 * @param maxCount 最大执行次数
 * @constructor zhanghua 2018-12-15
 */
function pollingGetResult(options, hashvo, time, count,maxCount) {
    var iscontinue = true;
    setTimeout(function () {
        var map = new HashMap();
        var functionId = options.functionId;
        map.put("getResult_Type", "search");
        map.put("getResult_Functionid", functionId);
        Rpc({
            functionId: 'ZJ100000311', success: function (form, action) {
                var result = Ext.decode(form.responseText);
                var succeed = result.succeed;
                count++;
                if (succeed == true) {
                	if(result.getResult_status == '0'){
                		if(count<=maxCount) {
                            pollingGetResult(options, hashvo, time, count, maxCount);
                        }else{
                            result.succeed=false;
                            result.message="系统数据处理时间过长，请稍后再试！";
                            form.responseText=Ext.encode(result);
                            Ext.callback(options.success, null, [form, action]);
						}
					} else if (result.getResult_status == '1') {
                        iscontinue = false;
                        if (options.success) {
                            Ext.callback(options.success, null, [form, action]);
                        }
                    } else if (result.getResult_status == '2') {
                        iscontinue = false;
                        if (options.success) {
                            Ext.callback(options.success, null, [form, action]);
                        }
                    }

                } else {
                    iscontinue = false;
                    if (options.success) {
                        Ext.callback(options.success, null, [form, action]);
                    }
                }
            }
        }, map);
    }, time);

}

//SM4加密
function encrypt(data) {
        var key = 'hjsoftjsencryptk';
        return SM4.encode({
           key:key,
           input:data
        });
}
/**异步调用RPC时，出错回调方法 wangb 20190529*/
function requestError(req){
	if(Ext && Ext.Viewport && Ext.Viewport.setMasked)
		Ext.Viewport.setMasked(false);
	if(Ext && Ext.Msg)
		Ext.Msg.alert('提示','网络异常！');
}