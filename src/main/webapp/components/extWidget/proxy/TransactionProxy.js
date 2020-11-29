/**
 * http动态代理
 * @author guodd
 * @date   2015-08-25
 * @description 通过ajax 调用 交易类
 *
 * 使用方法：
 * 1、先加载此js，
 * 2、在store中使用
 * var store= Ext.create("Ext.data.Store",{
 *       fields:.......,
 *       proxy:{//注意：使用此proxy 千万不要设置url属性
 *          type:'transaction',  // type=transaction 代表使用此动态代理
            functionId:'xxxxxxxxxx',
 *          extraParams:{....},  // 此处设置参数
 *          reader:{
 *            ......
 *          }
 *       }
 * });
 * 
 *
 * 
 */
Ext.Loader.loadScript({url:"/js/sm4.js"});
Ext.define("EHR.extWidget.proxy.TransactionProxy",{
	extend:'Ext.data.proxy.Ajax',
	alias: 'proxy.transaction',
	functionId:undefined,
	url:'/ajax/ajaxService',
	actionMethods:{create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'},
    convertParams:function(config){
        for(var obj in config){
		    if(config[obj]===undefined || config[obj]===null)
		        delete config[obj];//去除空值元素
		}
        config.functionId=this.functionId;
       
        var configStr = Ext.encode(config);//对象转json
    		
        var encodeStr = this.encrypt(configStr);//aes加密
		
        return { __xml:encodeStr, __type : "extTrans" };
    },
    //SM4加密
    encrypt:function(data) {
        var key = 'hjsoftjsencryptk';
        return SM4.encode({
            key:key,
            input:data
        });
    },
	buildRequest: function(operation) {
	    var originParams = this.extraParams;
	    this.extraParams = this.convertParams(originParams);
	    var request = this.callParent(arguments);
	    this.extraParams = originParams;
	    return request;
    }
});