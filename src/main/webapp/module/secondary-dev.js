/*
前端二开定义文件。二开个性化内容定义到HJSJ_EXT_CLASS_OVERRIDE_CONFIG对象中。
更多详细说明请查看KM系统中《HCM系统二次开发规范》
二开定义规则示例说明：
1、模块首页Ext对象中注册模块id： 
Ext.HJSJ_MODULE_ID='renshiyidong';

2、定义二开内容
var HJSJ_EXT_CLASS_OVERRIDE_CONFIG = {
	//key为Ext中注册的模块id。
	renshiyidong:{
		//需要二开的组件名称
		EHR.my.MyClass:{
			//初级二开方式：组件初始化完毕后执行的方法，例如可以动态添加一些组件、button等。此方法的scope为当前组件
			afterrender:function(){
			   //TODO
			},
			//高级二开方式，组件创建时会将override属性覆盖当前组件的属性
			override:{
			   //TODO
			}
		}
	}	
		
}
 */
var HJSJ_EXT_CLASS_OVERRIDE_CONFIG = {
		
		questionnaire:{
			'QuestionnairePlan.MyQuestionnaire':{
				
			}
		}
		
}