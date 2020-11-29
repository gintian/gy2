/**
 * 异动模板国际化文件
*/
var MB = {};
MB.ERROR ={};
MB.MSG ={};
MB.LABLE ={};
MB.BUTTON ={};
MB.MSG.IMPORTMEN={};

MB.RESOURCE = {};
MB.CHANGELOG={};
MB.PROCESSARCHIVING = {};
MB.RESOURCE.SAVE_SUCCESS="保存成功";
MB.RESOURCE.BUTTON_CARD="卡片";
MB.RESOURCE.BUTTON_LIST="列表";
MB.MSG.IMPORTMEN_CONDITION0="是否按模板的检索条件选人?";
MB.MSG.IMPORTMEN_CONDITION1="模板中没有人员,是否按模板的检索条件选人?";
MB.MSG.IMPORTMEN_CONDITION2="是:清空当前模板的人员,重新按检索条件选人 <br>否:不清空当前模板的人员,增加符合检索条件的人 <br>取消:什么操作也不做,不清空不使用检索条件";
MB.MSG.isOperate="您确认要执行此操作？";
MB.MSG.SUBMIT_APPLY_INFO="审批表单的信息将作为最终结果归档入库,是否确认提交?";
MB.MSG.select_to_update_items="请选中需要更新的指标！";
MB.MSG.assign_sucess="审批完成！";

MB.CARD = {};
MB.CARD.PersonnelBusiness = "人事业务";
MB.CARD.UNITCHANGE = "机构调整";
MB.CARD.CONTRACTMANAGEMENT = "合同办理";
MB.CARD.SALARYCHANGE = "薪资变动";
MB.CARD.INSURANCECHANGE = "保险变动";
MB.CARD.STATIONCHANGE = "岗位变动";
MB.CARD.BUSINESSMANAGEMENT = "业务办理";
MB.CARD.GOABROAD="出国办理";
MB.CARD.DBTASK = "待办任务";
MB.CARD.YBTASK = "已办任务";
MB.CARD.MYAPPLY = "我的申请";
MB.CARD.CTRLTASK = "任务监控";
MB.CARD.BUSINESSAPPLY = "业务申请";
MB.CARD.ZGPS="资格评审";
MB.CARD.ZSGL="证照管理";

/*MB.MSG.dbClickShowSub = "请双击查看编辑子集";*/
MB.MSG.dbClickShowSub = "双击可放大信息新增修改界面";
MB.MSG.subAttachmentNoDir="请在【系统管理-应用设置-参数设置-系统参数】中配置文件存放根目录！";
MB.MSG.selectDateByCompent="请通过日期控件选择日期！";
MB.MSG.inputRightDateFormat="请输入正确日期格式！";
MB.MSG.selectCombineData="请选择需要合并的记录！";
MB.MSG.selectCombineData2="请选择需要划转的记录！";
MB.MSG.laterDateError="最近日期输入有误，请重新输入！";

/**  任务监控  start**/
MB.LABLE.filter_condition="请选择过滤方式";
MB.LABLE.byDate="按日期最近";
MB.LABLE.byTimeDomain="按时间段";
MB.LABLE.finish="结束";
MB.LABLE.running="运行中";
MB.LABLE.hasEnded="已终止";
MB.LABLE.bpTask="报批任务";
MB.LABLE.bbTask="报备任务";
MB.LABLE.taskType="任务类型";
MB.LABLE.templateName="模板名称";
MB.MSG.selectData="请选择数据！";
MB.MSG.selectDeleteAttachment="请您选择要删除的附件！";
MB.MSG.bydelete="被撤销";
/**  任务监控  end**/

/**  附件  start**/
MB.LABLE.name="名称";
MB.LABLE.createUser="创建人";
MB.LABLE.createDate="创建日期";
MB.LABLE.oper="操作";
MB.LABLE.attachment="上传附件";
MB.LABLE.sortname="文件分类";
MB.MSG.deleteFailed="删除失败！";
MB.MSG.downLoadFailed="下载失败！";
MB.LABLE.mediaType="多媒体分类";
MB.LABLE.file="文件";
MB.LABLE.selectFile="选择导入文件";
MB.MSG.selectUploadFile="请选择要导入的文件！"
MB.MSG.noSelectType="请选择多媒体分类！"
/**  附件  end**/
MB.MSG.downtempleFile="模板文件";	
MB.MSG.savetemplateSubset="正在保存,请稍候";
MB.MSG.delTemplateSubset="正在删除,请稍候";
	
/**  日志变动 start**/
MB.CHANGELOG.personInfo="人员信息";
MB.CHANGELOG.orgInfo="机构信息";
MB.CHANGELOG.fieldSetName="信息集名称";
MB.CHANGELOG.templateName="模板名称";
MB.CHANGELOG.changeLog="变动日志";
MB.CHANGELOG.fieldName="指标名称";
MB.CHANGELOG.emptyText="请输入姓名或者唯一性指标值";
MB.CHANGELOG.addRecord="新增";
MB.CHANGELOG.updateRecord="修改";
MB.CHANGELOG.deleteRecord="删除";
MB.CHANGELOG.cell="单元格";
/**  日志变动 end**/
/** 签章  start**/
MB.LABEL = {};
MB.LABEL.SELECTSIGNATURE="选择印章";
MB.LABEL.USERSIGNATURE="用户印章";
MB.LABEL.PLEASESELECT="请选择";
MB.LABEL.SELECT="选择";
MB.LABEL.SIGNATUREPASSWORD="印章密码";
MB.LABEL.SIGNATUREPASSWORDERROR="印章密码错误";
MB.LABEL.SURE="确定";
MB.LABEL.CANCEL="取消";
MB.LABEL.CANCELSIGNATURE="撤销印章";
MB.LABEL.ALL="全部";
MB.LABEL.BUSINESSUSER="业务用户";
MB.LABEL.SELFSERVICEUSER="自助用户";
MB.LABEL.SEARCHTEXT="按姓名、用户名查询...";
MB.LABEL.EDIT="编辑";
MB.LABEL.DELETE="删除";
MB.LABEL.USERNAME="用户名称";
MB.LABEL.USERTYPE="用户类型";
MB.LABEL.SIGNATUREFILE="印章列表";
MB.LABEL.ADDSIGNATURE="添加印章";
MB.LABEL.EDITSIGNATURE="编辑印章";
MB.LABEL.NOUSERNOADD="印章用户没有添加，不能新增印章！";
MB.LABEL.NOFILENOADD="印章文件没有添加，不能新增印章！";
MB.LABEL.NOFILENOEDIT="印章文件没有添加，不能保存印章！";
MB.LABEL.SUREDELETESIGNATURE="确认要删除选中的数据吗?";
MB.LABEL.NOSELECTRECORD="没有选中记录！";
MB.LABEL.REMEMBERPASSWORD="记住密码";
MB.LABEL.FILEUPLOAD="上传";
MB.LABEL.PLEASEWRITEPASSWORD="请输入印章密码";
MB.LABEL.PLEASEDOUBLECLICKTOSIGN="请双击进行签章";
MB.LABEL.CURRENTUSERNOSIGNATURE="当前登录用户没有配置印章！";
MB.LABEL.IMGNAME="图片名称";
MB.LABEL.IMGTYPE="图片类型";
MB.LABEL.IMGSIZE="图片大小";
MB.LABEL.UPLOADIMG="上传图片";
MB.LABEL.PLEASESELECTIMG="请选择要上传的文件！";
MB.LABEL.INDIVIDUALSIGNATURE="个别签章";
MB.LABEL.BATCHSIGNATURE="批量签章";
MB.LABEL.BROWSE="浏览";
MB.LABEL.SELECTNEEDBATCHSIGNATURE="请选择需要批量签章的";
MB.LABEL.PERSON="人员";
MB.LABEL.UNIT="单位";
MB.LABEL.STATION="岗位";
MB.LABEL.UDUNID="U盾ID";
MB.LABEL.PLEASEBINDINGUDUN="请绑定U盾";
MB.LABEL.BINDINGUDUN="绑定U盾";
MB.LABEL.SAVETOUDUN="签章图片存储到U盾";
MB.LABEL.CHECKNOHJSOFTCLOCKNOBANDING="检测到不是发布的锁，无法进行绑定操作！";
MB.LABEL.CHECKNOHJSOFTCLOCKNOSIGNATURE="检测到不是发布的锁，无法进行签章操作！";
MB.LABEL.CHECKNOBANDINGCURRENTUSERNOSIGNATURE="检测到本锁未与当前登录用户绑定，无法进行签章操作！";
MB.LABEL.NOMORECLOCKBANDING="不支持多个锁同时绑定，请依次插入锁进行绑定操作！";
MB.LABEL.CHECKMORECLOCK="检测到多个锁，系统仅支持一个用户操作一把锁！";
MB.LABEL.CHECKNOCLOCK="没有检测到有效的锁！";
MB.LABEL.BANDINGSUCCESS="绑定U盾成功";
MB.LABEL.PLEASESETFILE="请设置文件属性";
MB.LABEL.PLEASEBINDINGUDUNFIRST="请先绑定U盾！";
MB.LABEL.ALREADLYBANDINGUSER="该锁已绑定用户，确定重新绑定吗?";
MB.LABEL.PLEASEADDUSERFORBANDING="请先添加印章用户，再进行绑定操作！";
MB.LABEL.CHEXIAOSIGNATURE="撤销签章";
/** 签章  end**/
/** 流程归档 start**/
MB.PROCESSARCHIVING.PROCESSARCHIV = "流程归档";
MB.PROCESSARCHIVING.ARCHIVING = '归档';
MB.PROCESSARCHIVING.CANCEL= '取消';
MB.PROCESSARCHIVING.SURE= '确定';
MB.PROCESSARCHIVING.ALL= '全部';
MB.PROCESSARCHIVING.PREVIOUSDATA = '以前数据';
MB.PROCESSARCHIVING.WAITMESSAGE1 = "正在执行流程归档，请稍候...";
MB.PROCESSARCHIVING.CONGIRMMESSAGE = "流程归档会删除归档日期之前的相关数据，确认流程归档吗？";
MB.PROCESSARCHIVING.SELECTARCHIVEDATA = "请选择要归档的模板数据！";
MB.PROCESSARCHIVING.NOARCHIVE = "未归档";
MB.PROCESSARCHIVING.QUERYTEXT = "请输入模板ID,模板名称...";
MB.PROCESSARCHIVING.WARNINGTEXT = '注意：';
MB.PROCESSARCHIVING.WARNINGTEXT1 = '&nbsp;&nbsp;(1)建议在系统空闲时进行，避免影响业务';
MB.PROCESSARCHIVING.WARNINGTEXT2 = '&nbsp;&nbsp;(2)建议分批次归档，一次归档半年数据为佳';
MB.PROCESSARCHIVING.AGODATA = '之前（包括）';
MB.PROCESSARCHIVING.WAITMESSAGE = '正在执行导出Excel操作';
MB.PROCESSARCHIVING.WAIT = '等待';
MB.PROCESSARCHIVING.SELECTEXPORTDATA = '请选择要导出的数据！';
MB.PROCESSARCHIVING.SEARCHTEXT = '请输入姓名,唯一指标...';
MB.PROCESSARCHIVING.SELECTARCHIVEDATE = '请选择归档日期！';
MB.PROCESSARCHIVING.SHOWMESSAGEBIGDATE = '您选择的日期大于系统当前日期，无法进行流程归档！';
/** 流程归档 end**/