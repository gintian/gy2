/**
 * 存储当前模板所有参数，为全局变量，整个模板模块都可以调用。
 * 如果有两个及以上的功能点需要使用同样的参数，前台使用或传送到后台，就可以考虑将参数变为全局变量，初始模板模块时就赋值。
 * 注意：所有在此定义的全局参数必须在初始化进入模板（templatemaintrans）时就要赋值，不要在需要使用时才赋值，以避免值不同步，
 *      各个功能模块单独传递的参数，需自己获取）。
 * 
 * 
*/
templateGlobalBean={		
	/*********************     前台输入参数  begin  *******************/			
	  /**必须传输的参数：sys_type、return_flag、module_id、tab_id、task_id****/			
	  /**可选参数：approve_type、view_type、card_view_type、other_param**/			
	/**平台标识  便于以后扩展使用
	 1： 1：bs平台  2：移动平台  
	*/	
	sys_type:"1",	
	
	/**调用的模块标识、返回模块标识
	 * 1：返回待办任务界面
	 * 2：返回已办任务界面
	 * 3：返回我的申请界面
	 * 4：返回任务监控界面
	 * 5:返回业务申请界面
	 * 6:返回到业务分类界面
	 * 7-xx:预警列表 xx:预警id
 	 * 8-xx:返回党组织人员与团组织人员得列表页面（属于党团管理模块）xx Y 党组织人员列表 V 团组织人员列表 M 工会人员列表
	 * 。7-10。暂时保留
	 * 11.首页待办
	 * 12、首页待办列表
	 * 13、关闭（来自第三方系统或邮件），提交后自动关闭
	 * 14、无关闭、返回按钮，提交后不跳转
	*/
	return_flag:"",	
	
	/** 审批类型 区分报审、报备、加签 
	 * 0:浏览 1：报审(起草) 2：加签  3 报备 
	*/
	approve_flag:"0",
	
	/** 模板号
	 * 
	*/
	tab_id:"",  
	
	/** 模块ID
	 * 1、人事异动
	 * 2、薪资管理 
	 * 3、劳动合同
	 * 4、保险管理
	 * 5、出国管理
	 * 6、资格评审
	 * 7、机构管理
	 * 8、岗位管理 
	 * 9、业务申请（自助）
	 * 10、考勤管理
	 * 11、职称评审 
	 * 12、证照管理
	*/	
	module_id:"1",	 
	
	/** 任务号（批量时为多个任务号 以逗号分隔）需加密 
	 * 0：起草；加密的任务号（非0）：审批中，批量时以逗号分隔，分隔后再统一加密，后台读取时需先解密。
	*/
	task_id:"0",		
	
	/** 模板卡片显示视图（可选）
	 * list:列表 ; card:卡片 
	*/	
	view_type:'', 	
	
	/** 卡片界面：是否显示左侧人员列表 （可选）
	 * 0：系统默认展现视图
	 * 1:左侧人员列表不显示
	*/
	card_view_type:"0", 
	
	/** 扩暂参数 （可选）
	 * 以`分隔object_id="usr00000000001`";  
     * object_id  控制只显示的某人的记录 职称评审用 加密
     * visible_toolbar 是否可见工具栏，“0”不可见，“1”可见
     * visible_title 是否可见标题，“0”不可见，“1”可见
     * noshow_pageno 控制不显示的页签，如有此参数则按此参数控制，否则按照模板设置控制 例如1,2,3 第一页对应数字0 ，继而第几页对应数字几-1
     * search_sql 查询控件的查询条件SQL语句
     * sub_moduleId 表格唯一id
     * deprecate_flag 增加选人控件不显示的人员参数，=1获取到已选中人员ID后直接返回
     * recallflag 撤回按钮标识 1是有 0是无
     * taskid_validate 判断展现人事异动页面以及导出pdf,word是否需要判断指标权限 此参数是task_id+一个时间戳  然后再PubFunc加密 
     * 前台传参扩展的方法template_util.js==》setTemplPropetyOthParam(key, value)
     * iniValue 模板参数 键@KEY@值@INIT@分隔 i_AB101_2@KEY@01@INIT@i_AB109_2@KEY@2017(i_为需要替换为空的标识，详细参照TemplateFrontProperty.java)
     *autoLogColor 自动记录日志字体显示颜色
     *isAutoLog    是否记录日志变动
     */ 
	other_param:"",
	
	/*********************    前台输入参数 end  ****************/	
	
	
	/*********************  以下参数为便于其他功能模块调用使用 begin  ****************/	
	/* 
	 * 信息群类型
	 * 1：人员 2： 单位 3： 岗位 后台根据模板类型判断
	*/	
	infor_type:"1",		
	/* 
	 * 版本标识 hcm bi 等
	 * 
	*/	
	bos_flag:"1",		
    table_name:"", //表名  加密 //要废弃后台使用 以下方法获取 new TemplateUtilBo(this.frameconn,this.userView).getTableName(moduleId,Integer.parseInt(tabId), taskId);
	
	
	xx:"1" //无用属性 
		

		
   /*********************    以下参数为便于其他功能模块调用使用 end       ****************/	
};
//默认属性 保存
templateGlobalBeanDefault={};
Ext.apply(templateGlobalBeanDefault,templateGlobalBean);
    
 