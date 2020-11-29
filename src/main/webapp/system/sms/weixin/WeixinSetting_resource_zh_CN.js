
var weixin = {};
weixin.setting = {};

weixin.setting.enterprise = "企业微信";
weixin.setting.serviceNumber = "服务号";
weixin.setting.serviceNumberConfig = "服务号配置";
weixin.setting.serviceNumberSetting = "服务号设置";
weixin.setting.noticeTemplate = "通知模板";
weixin.setting.infoTemplateId = "消息模板ID";
weixin.setting.templateContent = "模板内容";
weixin.setting.variableMap = "模板变量对应";
weixin.setting.infoTitle = "消息开始语";
weixin.setting.infoDetail = "消息详情";
weixin.setting.posName = "职位名称";
weixin.setting.resumeState = "简历筛选状态";
weixin.setting.operateTime = "操作时间";
weixin.setting.stopTemplateIdNull = "消息模板ID不能为空！";
weixin.setting.stopTemplateContentNull = "模板内容不能为空！";
weixin.setting.stopTemplateVariableNull = "模板变量不能为空！";
weixin.setting.customMenu = "自定义菜单";
weixin.setting.appSetting = "企业应用配置";
weixin.setting.configSetting = "企业号配置";
weixin.setting.save = "保 存";
weixin.setting.publish = "发 布";
weixin.setting.enterpriseConfig = "企业应用配置";
weixin.setting.appName = "应用名称";
weixin.setting.appAgentId = "应用agentid";
weixin.setting.appAPPID = "应用APPID";
weixin.setting.appAppSecrit = "应用AppSecrit";
weixin.setting.appSecret = "应用secret";
weixin.setting.trustDomainName = "信任域名";
weixin.setting.appType = "应用主页类型";
weixin.setting.appTypeMain = "工作台应用主页";
weixin.setting.appTypeMenu = "自定义菜单";
weixin.setting.funcType = "应用主页"
weixin.setting.appDescription = "应用简介";
weixin.setting.appLogo = "应用logo";
weixin.setting.appDescInfo = "简介长度为4-120个字";
weixin.setting.appCorpID = "企业ID";
weixin.setting.addressBookSecret = "通讯录Secret";
weixin.setting.assistantAgentID = "企业小助手AgentID";
weixin.setting.assistantSecret = "企业小助手Secret";
weixin.setting.appServiceAddress = "应用服务地址";
weixin.setting.wxServiceAddress = "微信服务地址";
weixin.setting.wxmerchants = "微信商户号";
weixin.setting.credentialKey = "证书key";
weixin.setting.ok = "确认";
weixin.setting.cancel = "取消";
weixin.setting.pleaseSelect = "请选择...";
weixin.setting.promptmessage = "提示信息";
weixin.setting.mustSelectServiceNum = "请先选择服务号";
weixin.setting.stopDeleteFirstMenu = "请删除全部子菜单后，切换主菜单功能";
weixin.setting.mustSelectFile = "请选择应用logo";
weixin.setting.saveSuccess = "保存成功";
weixin.setting.saveFail = "保存失败";
weixin.setting.stopSave = "信息未改动，无需保存";
weixin.setting.publishSuccess = "发布成功";
weixin.setting.publishFail = "发布失败，请与管理者联系！";
weixin.setting.firstSave = "请先保存参数的修改";
weixin.setting.stopPublish = "信息未改动，无需发布";
weixin.setting.realDeleteApp = "确认删除该应用吗？";
weixin.setting.deleteSuccess = "删除成功";
weixin.setting.deleteFail = "删除失败";
weixin.setting.applyFor = "我要应聘";
weixin.setting.service = "服务";
weixin.setting.other = "其他";
weixin.setting.personalCenter = "个人中心";
weixin.setting.personalResume = "个人简历";
weixin.setting.applyForPosition = "应聘职位";
weixin.setting.collectionPosition = "收藏职位";
weixin.setting.modifyApplyForIdentity = "修改应聘身份";
weixin.setting.firstMenuName = "主菜单名称";
weixin.setting.secondMenuName = "子菜单名称";
weixin.setting.firstMenuFunction = "主菜单功能";
weixin.setting.secondMenuFunction = "菜单功能";
weixin.setting.secondMenuAddress = "子菜单地址";
weixin.setting.addFirstMenu = "添加主菜单";
weixin.setting.addsecondMenu = "添加子菜单";
weixin.setting.maxIs = "最大上限为";
weixin.setting.a = "个";
weixin.setting.inputAccessAddress = "请输入地址...";
weixin.setting.inputAndSelectAddress = "请输入地址或选择功能...";
weixin.setting.selectFirstMenuFunction = "请选择主菜单功能";
weixin.setting.menuInstruction = "可创建最多三个主菜单，每个主菜单下最多可创建五个子菜单。";
weixin.setting.tip="提示";
weixin.setting.maxWord16= "最多输入8个汉字或16个字符！";
weixin.setting.maxWord8= "最多输入4个汉字或8个字符！";
weixin.setting.delBefore= "确认删除名为\"";
weixin.setting.delAfter= "\"的子菜单吗？";
weixin.setting.urlDescText = "跳转到网页";
weixin.setting.urlConfigTitle = "设置菜单链接跳转地址";
weixin.setting.appidfaildesc = "应用APPID不能为空！";
weixin.setting.appsecritfaildesc = "应用AppSecrit不能为空！";
weixin.setting.appurlfaildesc = "应用服务地址不能为空！";
weixin.setting.enterpriseFuncCustomMenu = "应用自定义菜单配置";
weixin.setting.appDescTip = "应用简历内容长度不符合格式！"
weixin.setting.pleaseSelectFunction="请选择  ";
weixin.setting.functionsOrInput="  功能,或输入链接地址";
weixin.setting.pleaseInput="请输入  ";
weixin.setting.urlOrAddSecondFunction="  链接地址,或为其添加子菜单";
weixin.setting.invalidAppid="无效的应用Appid或AppSecrit";
weixin.setting.weixinSystembusy="微信系统繁忙，请稍候再试";
weixin.setting.appTitle1="自动推送";
weixin.setting.appTitle2="手工配置";
weixin.setting.appEnterpriseFuncTitle="企业微信功能secret配置(微信企业号无需配置此项)";
weixin.setting.appEnterpriseFuncSelect="选择功能";
weixin.setting.appEnterpriseFuncDesc1="功能不能为空！";
weixin.setting.appEnterpriseFuncDesc2="该功能已经存在！";
weixin.setting.appEnterpriseFuncLink="生成链接";
weixin.setting.appEnterpriseCorpId="请填正确填写企业号CorpId";
weixin.setting.appEnterpriseAddress="请填正确填写微信服务地址，如:http://www.hjsoft.com.cn:8089";
weixin.setting.appEnterpriseAddressTip="微信服务地址必须以http://或 https://开头";
weixin.setting.appEnterpriseFuncLinkAddress="功能链接地址如下";
weixin.setting.add="添加";
weixin.setting.deleteMenu="删除";