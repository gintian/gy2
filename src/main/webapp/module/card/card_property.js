/**
 * 链接配置参数说明
 * tabid： 登记表号（不配置时查询全部权限范围内的登记表）
 * inforkind:调用模块1：人员，2：单位，4：岗位；5：绩效；6：岗位说明书7：薪酬(我的薪酬 员工薪酬) 8：领导桌面 9：招聘,10 培训自助-我的积分
 * a0100:'' a0100为空时显示人员(权限范围内数据)  显示人员登记表：a0100:'dbname`A0100' (加密)
 * bizDate:业务日期
 * plan_id temp_id 绩效登记表所需参数
 * fieldpurv 是否按人员范围权限显示指标 1 不控制权限 0 控制权限（加密）
 * */

cardGlobalBean={
	fieldpurv:'',
	tabid:'',
    a0100:'',
    bizDate:'',
    inforkind:'',
    plan_id:'',//绩效
    temp_id:'',//绩效
    queryflag:'',//此参数无需传参 
    Callbackfunc:'',//回调函数。此参数不为空时 添加关闭按钮
    cardFlag:'',
    btnFunction:undefined,
    url:undefined,//调用打印预览url
    hcmFlag:undefined,
    zp_flag:undefined,
    isFitFlag:true//默认自动填充整个页面，false 以对象的形式供调用者使用
}

cardGlobalBeanDefault={};
Ext.apply(cardGlobalBeanDefault,cardGlobalBean);
