var zc = {};

zc.error ={};
zc.msg ={};
zc.label ={};
zc.menu ={};
zc.reviewfile ={};
zc.reviewconsole ={};
zc.button ={};
zc.button.resource = {};
zc.button.sms = {};
zc.config = {};
zc.meetingportal={};
zc.cardview = {};

zc.msg.searchmsg="请输入会议名称、姓名";
zc.msg.cardview="卡片视图";
zc.msg.listview="列表视图";
zc.msg.inputerro="只能输入 1 到 6 位字母或数字";
zc.msg.deleinfo="是否删除选中项?";
zc.msg.unsucess="未成功";
zc.msg.savesuccess="保存成功";
zc.msg.savedefault="保存失败";
zc.msg.listinfo="列表信息";
zc.msg.summaryinfotable="材料汇总信息表";
zc.msg.selectedtarget="已选指标";

zc.label.searchScheme="查询方案";
zc.label.all="全部";
zc.label.nostart = "未开始";
zc.label.qicao="起草"
zc.label.running="进行中";
zc.label.stop="暂停";
zc.label.finish="结束";
zc.label.achived="归档";
zc.label.name="姓名";
zc.label.b0110 = "单位";
zc.label.e0122 = "部门";
zc.label.currenttitle="现任职称";
zc.label.applytitle="申报职称";
zc.label.applyfile="申报材料";
zc.label.proficientcomment="专家评价";
zc.label.state="状态";
zc.label.proficientcheckfile="专家鉴定材料";
zc.label.checkfile="鉴定材料";
zc.label.checkcomment="鉴定意见";


zc.label.peizhiPn="配置信息";
zc.label.shenbaoPn="职称申报业务模板";
zc.label.assessmentTablePn='测评表';
zc.label.guidangPn="评审结果归档方案";
zc.label.masterpiecePn="代表作导出规则";
zc.label.masterpieceTitle="设置代表作导出匹配规则";
zc.label.masterpieceChoseFile="浏览";
zc.label.masterErrorMsg="必填项不可为空";


zc.label.baomingPn="预报名申请";
zc.label.shenbaoCaiPn="材料审查";

zc.label.lunwenPn="论文送审业务模板";
zc.label.lunwenSongPn="论文送审";

zc.label.beianPn="免试备案、破格业务模板";
zc.label.pogePn="破格申请审批";
zc.label.mianshiPn="外语免试备案";

zc.label.rendingPn="认定业务模板";
zc.label.kaoshiPn="考试认定";
zc.label.zhichengPn="职称认定";

zc.label.peizhiBtn="配置";
zc.label.yewuWin="业务模板";
zc.label.selectPn="选择模板";
zc.label.renYuanNode="人员分配";

zc.label.radioCard="卡片";
zc.label.radioList="列表";
zc.label.voteWay="投票方式";
zc.label.vote="投票";
zc.label.score="打分";
zc.label.templatePrompt='请选择左侧模板，不要选择模板分类。';

zc.label.add="添加";
zc.label.forbid="禁用";
zc.label.activated="启用";
zc.label.save="保存";
zc.label.cancel="取消";
zc.label.remind="提示信息";
zc.label.fill="请填全";
zc.label.account="账号";
zc.label.password="密码";
zc.label.noempty="不能为空";
zc.label.desc="描述";
zc.label.rater="评委";
zc.label.newa="新建";
zc.label.dele="删除";
zc.label.sure="确认";
zc.label.exp="导出";
zc.label.exping="正在导出";
zc.label.wait="请稍等...";
zc.label.waiting="等待";
zc.label.randomgen="随机生成";
zc.label.inReviewNo="评委人数";
zc.label.innerNo="请输入数字";
zc.label.inExpertNo="专业(学科)组人数";
zc.label.exExpertNo="同行专家人数";
zc.label.release="评审发布";
zc.label.inReview="评委会";
zc.label.inExpert="专业组";
zc.label.inOther = '二级单位';
zc.label.exExpert="同行评议";
zc.label.innerNoEro = "输入非数字，请重新输入数字";
zc.label.innerNoN = "请输入申请数量"
zc.label.ero = "错误";
zc.label.repeat = "请重新填写";
zc.label.noData = "没有选中数据";
zc.label.singleclick = "单击";
/** 导航菜单【聘委会管理】中的菜单项配置： 聘委会 学科组 */
zc.menu.committeeshowtext = '评委会';
zc.menu.subjectsshowtext = '学科组';
zc.config.isEngageGroup = "评审环节支持二级单位评议组";
zc.config.isShowValidatecode = "评价系统登录显示校验码";
/** 上会材料中外部鉴定专家名称配置 */
zc.reviewfile.outsideexpert = '同行专家';
/** 上会材料中评审环节的名称配置： 1——4阶段 */
zc.reviewfile.step1showtext = '评委会阶段';
zc.reviewfile.step2showtext = '专业组阶段';
zc.reviewfile.step3showtext = '同行专家阶段';
zc.reviewfile.step4showtext = '二级单位评议阶段';
zc.reviewfile.pagetitle = "上会材料";
/** 申报人分组页面  start */
zc.reviewconsole.nameRepeatedUse = "该名称已被使用！";
zc.reviewconsole.chooseApplicant = "请选择申报人";
zc.reviewconsole.applicant = "申报人";
zc.reviewconsole.addFailed = "添加失败！";
zc.reviewconsole.startVote = "启动";
zc.reviewconsole.restartVote = "重新启动";
zc.reviewconsole.stopVote = "暂停";
zc.reviewconsole.voteCount = "统计票数";

zc.reviewconsole.enterName = "请输入分组名称！";
zc.reviewconsole.voteWork = "的投票工作？";
zc.reviewconsole.scoreWork = "的评分工作？";
zc.reviewconsole.deleteGroup = "确认删除该分组？";
zc.reviewconsole.confirmDelete = "确认删除该申报人？";
zc.reviewconsole.cannotDelInStartting = "申报人已开始投票，不允许删除！";
zc.reviewconsole.shouldThanZero = "所填名额不能小于0！";
zc.reviewconsole.shouldThan = '所填名额不能大于该组申报人总数！';
zc.reviewconsole.showGroup = "选择各组申报人";
zc.reviewconsole.hasSelectGroup = "已选分组";
zc.reviewconsole.waittingGroup = "待选分组";
zc.reviewconsole.choosePerson = "（&nbsp;选人规则&nbsp;:&nbsp;仅能选择待您审批的申报材料人员&nbsp;）";
zc.reviewconsole.setFinish = "是否置为已结束状态？";
zc.reviewconsole.noStartedGroups = "分组已全部启动或结束，无法导出账号！";
zc.reviewconsole.noDataToExport = "请新建分组之后再进行导出操作";
zc.reviewconsole.resultData = {};
zc.reviewconsole.resultData.noPerson = "该阶段没有分组或分组下没有申报人不需要归档！";
zc.reviewconsole.resultData.confirmMsg = '归档后将结束当前环节（结束后数据不能修改），流程将进入下一环节，是否继续？';
zc.reviewconsole.resultData.confirmMsg2 = '归档后将结束当前环节（结束后数据不能修改），并结束当前会议，是否继续？';
zc.label.on = "未";
zc.label.ing = "已";
zc.label.is = "是否";
zc.label.restart = "重新";
zc.label.start = "启动";

/** 申报人分组页面 end */

/** 新版评审会议入口页面 start by haosl 2018-4-8 */
zc.meetingportal.member = "成员";
zc.meetingportal.sendmessage = "参会提醒";
zc.meetingportal.startreview = "发起评审";
zc.meetingportal.viewResult = "查看评审情况";
zc.meetingportal.selfunit = "本单位";
zc.meetingportal.subunit = "下属单位";
zc.meetingportal.newmeeting="创建会议";
zc.meetingportal.qidong = "启动会议";
zc.meetingportal.edit = "编辑会议";
zc.meetingportal.zanting = "暂停会议";
zc.meetingportal.shanchu = "删除会议";
zc.meetingportal.attendnumber="参评人数";
zc.meetingportal.attendednumber="已评人数";
zc.meetingportal.year="年度";
/* 会议操作提示信息 */
zc.meetingportal.startmsg = "确定要启动当前会议？";
zc.meetingportal.delmsg = "确定要删除当前会议？";
zc.meetingportal.stopmsg = "当前会议正在运行中，是否暂停？";

/** 新版评审会议入口页面 end by haosl 2018-4-8 */

/** 评审会议维护界面（创建|编辑）start by haosl 2018-4-14 */
zc.editmeeting = zc.editmeeting||{};
zc.editmeeting.mainview=zc.editmeeting.mainview||{};
zc.editmeeting.mainview.paneltitle = "创建评审会议";
zc.editmeeting.mainview.edittitle = "编辑评审会议";
zc.editmeeting.mainview.viewtitle = "查看评审会议（不可编辑）";
zc.editmeeting.mainview.name = "会议名称";
zc.editmeeting.mainview.desc="会议描述";
zc.editmeeting.mainview.stratd="起始时间";
zc.editmeeting.mainview.endd="结束时间";
zc.editmeeting.mainview.segssetting="评审环节设置";
zc.editmeeting.mainview.segmentsps="评审";
zc.editmeeting.mainview.cancle="返回";
zc.editmeeting.mainview.complete="完成";
zc.editmeeting.mainview.organization="所属机构";
zc.editmeeting.mainview.until="至";
zc.editmeeting.mainview.next="下一步";
zc.editmeeting.mainview.previous="上一步";
zc.editmeeting.mainview.meetingsubject = "创建会议主题";
zc.editmeeting.mainview.expertsetting = "评审人员设置";
zc.editmeeting.mainview.reportercategories = "申报人分组";
zc.editmeeting.mainview.voterule = "投票规则设置";
zc.editmeeting.mainview.emptytext="请输入";
zc.editmeeting.mainview.tochoose="请选择";
zc.editmeeting.mainview.vote="投票";
zc.editmeeting.mainview.score="评分";
zc.editmeeting.mainview.consoleview='控制台';
zc.editmeeting.mainview.choose="选择";
zc.editmeeting.mainview.nodatas="没有数据!";
zc.editmeeting.mainview.editgroupexperts="编辑组内专家";
zc.editmeeting.mainview.error = {};
zc.editmeeting.mainview.error.noassessmenttables="暂无可用测评表,请在参数设置中勾选测评表！";
zc.editmeeting.mainview.error.noassessmenttables2="请至少选择一个测评表！";
zc.editmeeting.mainview.error.noassessmenttables3="下列评审环节未选择测评表：";//xxx未选择测评表
zc.editmeeting.mainview.error.nosegments="请至少选择一个评审环节！";
zc.editmeeting.mainview.error.timesmsg=zc.editmeeting.mainview.stratd+"不能大于"+zc.editmeeting.mainview.endd+"！";
zc.editmeeting.mainview.error.noprivmsg="您没有操作权限！";
zc.editmeeting.mainview.error.cancleSegments="该评审环节已参加过评审，不允许取消！";
zc.editmeeting.mainview.error.addSegments="评审工作已开始，不允许添加此评审环节！";
zc.editmeeting.mainview.error.editAssessmentTable="当前阶段已参加评审，修改测评表可能会导致打分数据变动，确定保存？";
zc.editmeeting.mainview.assessor = {};
zc.editmeeting.mainview.assessor.randomuser = "采用随机账号";
zc.label.person = '人';
zc.editmeeting.mainview.assessor.numberformmsg="请输入1-100的数字！";
zc.editmeeting.mainview.assessor.numbernotallowblank="随机账号人数不能为空!";

/** 评审会议维护界面（创建|编辑）end by haosl 2018-4-14 */

/** 投票评分平台 start  **/
zc.cardview.more = "最多给";
zc.cardview.doVote = "人投";
zc.cardview.vote = "票！";
zc.cardview.notDraft = "鉴定意见未制定!";
zc.cardview.checkView = "鉴定意见";
zc.cardview.reviewView = "评审意见";
zc.cardview.assess = "(已评)";
zc.cardview.haveSubmited = "您已提交本轮投票结果！";
zc.cardview.error1 = "未配置列表投票方式中页面要展示的指标项！";
zc.cardview.error2 = "当前会议未启动！";
zc.cardview.error4 = "当前分组未启动";
zc.cardview.error5 = "分组启动后审核账号不能进行登陆！";
zc.cardview.unFinish = "还没有评价，请评价后再提交！";
zc.cardview.haveNotNew = "还没有新的申报人，请稍后重试！";
zc.cardview.groupName = "分组名";
zc.cardview.showGroup = "显示分组";
zc.cardview.gotoNextGroup = "正在进入下一组...";

zc.label.agree = "赞成";
zc.label.disagree = "反对";
zc.label.giveup = "弃权";
zc.label.back = "返回";
zc.label.submitSuccess = "提交成功！";
zc.label.declare = "申报";
zc.label.confirmResult = "结果确认";
zc.label.confirm = "确定";
zc.label.refresh = "刷新";
zc.label.queue="批次";

zc.configFile = {};
zc.configFile.meetinName = "会议名称";
zc.configFile.startDate="起始日期";
zc.configFile.errorMsg = "未配置对应指标，请检查！";
/** 投票评分平台 end  **/

zc.label.endThanStart = "起始日期不能超过结束日期";
zc.label.canNotVote = "分组已被暂停或结束，没有分组可进行投票！";