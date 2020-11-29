var gz = {};

gz.error ={};
gz.msg ={};
gz.label ={};
gz.button ={};
gz.button.resource = {};
gz.button.sms = {};
//标准表需求通用
gz.standard = {};
//历史沿革页面
gz.standard.pkg = {};
//标准表页面
gz.standard.sd = {};
//标准表结构窗口
gz.standard.sdst = {};


gz.label.end="结束";
gz.label.execute="执行中";
gz.label.again="(重发)";
/** 薪资发放-薪资发放 start */
gz.msg.searchmsg="请输入编号、名称";
gz.msg.nothaveperson="中没有新增，减少，或信息有变化的人员！";
gz.msg.dosucceed="操作成功";
gz.msg.handImportDataCheck="数据校验中...";
gz.msg.handImportImportData="人员添加中...";


gz.msg.selectDelRecard="请选择要删除的记录！";
gz.msg.sureDelRecard="确认删除吗？";

gz.label.changecompare="变动比对";
gz.label.addperson="新增人员";
gz.label.reduceperson="减少人员";
gz.label.stopperson="停发人员";
gz.label.changeinfoperson="信息变动人员";

gz.label.gzItems="项目名称";
gz.label.selectExpItems='请选择导出的项目';
gz.label.exportPattern='文件格式';
gz.label.exportType='文件类型';
gz.label.pattern='格式';
gz.label.importComment='说明：请用下载的Excel模板来导入数据！模板格式不允许修改 ';
gz.msg.changeFailed= "修改失败";
gz.label.highVersion="office2010及以上版本";
gz.label.lowVersion="office2010以下版本";

gz.msg.selectDelRelation="请选择要删除的方案！";
gz.msg.confirmDelRelation="确认要删除所选方案？";
gz.label.selectImportMode='选择导入方案';
gz.label.sourceData='Excel列';
gz.label.selectTargetData='目标指标';
gz.label.selectRelationData='关联指标';
gz.msg.NameTooLong='名称长度过长！';
gz.button.viewData='查看数据';
gz.msg.isNotUniq='该名称已存在，请重新命名！';
gz.button.selectRelation='选择方案';
gz.button.noRelationData='未对应数据';
gz.button.repeatData='重复数据';
gz.button.savaRelation='保存方案';
gz.button.readRelation='读取方案';
gz.button.importData='导入数据';
gz.msg.specifyDataForOpposite="请为对应指标指定数据！";
gz.msg.specifyDataForRelation="请为关联关系指定数据！";
gz.msg.cannotEditorSys="此目标数据为不可修改系统项！";
gz.msg.dataCannotRepeat="目标数据不能重复！";
gz.msg.selectDataIn="请选择需要更新或关联的数据项目！";
gz.label.importEelation= "导入方案";
gz.label.nameRelation= "请输入方案名称";
gz.msg.nameRelationNotNull="方案名称不能为空！";
gz.label.newRelation="新方案";
gz.msg.selectObject="请选择需要操作的对象！";
gz.msg.selectDataRelationItem="请指定更新指标！";
gz.msg.specifyRelationItem="请指定关联指标！";
gz.label.noRelationData='未对应数据';
gz.msg.moveFalse="移动失败！";
gz.msg.forbidMultiMove="禁止多行移动！";
gz.msg.selectOnePlan="请选择一个要读取的方案！";
gz.msg.selectPlan="请选择要读取的方案！";
gz.label.read="读  取";

gz.msg.isContinue='数据未提交,重发后未提交的数据将丢失!是否继续？';
gz.label.ffCount='发放次数';
gz.label.specifyAppDate='请指定业务日期';
gz.label.redoGzIng="正在重发，请稍候...";
gz.msg.isContinueReSetDate="数据未提交，重置业务日期后，未提交的数据将丢失！是否继续？";
gz.label.isReSetDate="正在重置业务日期，请稍候...";

gz.msg.notExistsData="没有可{0}的记录！";
gz.msg.canUseData="{0} {1} 条记录，是否继续？";
gz.label.submit="提交";
gz.label.appeal="报批";

gz.msg.selectAppealData="请选择已报批的数据！";
gz.msg.isRejectAllData="确认驳回全部数据吗？";
gz.msg.isRejectSelectData="确认驳回选择的数据吗？";

gz.label.payroll="工资条";
gz.label.payrollSignature="工资发放签名表";
gz.label.salarySummary="工资汇总表";
gz.label.salaryReportAnalysis="人员结构分析表";
gz.label.insuranceSummary="保险汇总表";
gz.label.insuranceSchedule="保险明细表";
gz.label.userDefinedTable="用户自定义表";
gz.label.reportName="报表名称";
gz.label.commonly="常用";
gz.label.commonlyReport="常用报表";
gz.label.reportwindow="报表输出";



/** 薪资发放-薪资发放 end */



gz.label.inputValue="输入项";
gz.label.accumulateValue="累计项";
gz.label.importValue="导入项";
gz.label.itemName="项目名称";
gz.label.sysValue="系统项";

gz.msg.selectbosdateand="请选择业务日期！";

//薪资类别-薪资项目
gz.label.newItemField="新增项目";
gz.msg.selectDelObj="请选择要删除的对象！";
gz.msg.selectAddObj="请选择要添加的对象！";
gz.msg.selectDelItem="请选择要删除的项目！";
gz.msg.isDelItem="您确定要删除所选的项目吗？";
gz.msg.inputItemNameOrCode="请输入项目名称、项目代码";

gz.label.currentRecord="当前记录";
gz.label.firstInMonth="月内最初第一条";
gz.label.lastInMonth="月内最近第一条";
gz.label.lessFirstInMonth="小于本次月内最初第一条";
gz.label.lesslastInMonth="小于本次月内最近第一条";
gz.label.sameCountInMonth="同月同次";
gz.label.deductedIssued="扣减同月已发金额";

gz.label.notCumulative="不累积";
gz.label.accumulationInMonth="月内累积";
gz.label.accumulatedInQuarter="季度内累积";
gz.label.accumulatedYears="年内累积";
gz.label.accumulationUnconditional="无条件累积";
gz.label.accumulationSCInQuarterr="季度内同次累积";
gz.label.accumulationSCInYear="年内同次累积";
gz.label.accumulationInSame="同次累积";
gz.label.lessAccumulationInMonth="小于本次的月内累积";


gz.label.referenceItem="参考项目";
gz.label.defineAccumulae="定义积累项";
gz.label.ImportCalFormula="导入计算公式项"
gz.label.processingMode="处理方式";
gz.label.projectCode="项目代码";
gz.label.operation="操作";
gz.label.inputSalaryName="请输入类别名称";
gz.msg.sureDeleteAgain="删除类别将同步删除相应的历史数据，请再次确认是否删除？";
gz.msg.sureDelete="您真的希望删除选中的类别？";
gz.msg.selectDeleteSalary="请选择要删除的类别名称！";
gz.label.inputSaveAsName="请输入另存的类别名称";
gz.msg.saveAsOnlyOne='每次只能另存一个类别！';
gz.msg.selectSaveAsSalaryType="请选择需要另存的类别名称！";
gz.msg.selectImportSalaryTypeFile="请选择需导入的类别文件";
gz.label.addite="追加";
gz.label.cover="覆盖";
gz.label.salaryType="薪资类别";
gz.msg.selectImportSalaryType="请选择要导入的类别名称！";
gz.msg.overClearSalaryType="覆盖操作将清空原有的类别";
gz.label.typeName="类别名称";

gz.label.moneyName="货币名称";
gz.label.moneyNum="面值";
gz.label.moneyMaintain="货币维护";
gz.label.moneyTypeMaintain="币种维护";
gz.msg.selectDeleteRecord="请选择要删除的记录！";
gz.msg.isDeleteRecord="确定删除当前记录吗？";
gz.label.symbol="符号";
gz.label.unit="单位";
gz.label.ok="确定";
gz.label.cancel="取消";
gz.label.exchangeRate="汇率";
gz.label.denomination="面值";
gz.label.newMoney="新增币种";
gz.msg.rateError="汇率输入有误！";
gz.msg.NumTooLong="数值长度超过最大长度！";
gz.label.newMoneyNum="新增货币面值";
gz.msg.inputNum="请输入数字！";
gz.msg.synchronousCompletion="同步完成";
gz.msg.inSync="正在同步，请稍候......";
gz.msg.selectSyncSalary="请选择需结构同步的类别名称！"
gz.msg.selectInitSalary="请选择需初始化的类别！";
gz.msg.historyDataInitOk="历史数据初始化成功！";
gz.msg.historyDataInit="正在初始化，请稍候......";

/** 薪资类别-薪资属性 start */
gz.label.all="全部";
gz.label.addall="添加全部";
gz.label.part="部分";
gz.label.timeScope="时间范围";
gz.label.useredScope='适用范围';
gz.label.TaxParam='计税参数';
gz.label.SpMode='审批方式';
gz.label.subDataType='数据提交方式';
gz.label.otherParam='其他参数';
gz.label.daName='人员库';
gz.label.personScope='人员范围';
gz.label.simpleCon='简单条件';
gz.label.complexCon='复杂条件';
gz.label.searchCon="检索条件";
gz.label.clear="清空";
gz.msg.operClearAllCondition="该操作将会清空已定义好的简单条件和复杂条件！";
gz.label.limitUserManager='限制用户管理范围';
gz.label.selectField="选择指标";
gz.label.shareType='共享方式';
gz.label.noShare='不共享';
gz.label.share='共享';
gz.label.manager='管理员';
gz.label.set='设置';
gz.label.updateThisRecord="更新当前记录";
gz.label.newRecord="新增记录";
gz.label.noChangeRecord="当前记录不变";
gz.label.fieldSetName="子集名称";
gz.label.senior="高级";
gz.label.submitNoShowOpt="提交时不显示数据操作方式设置";
gz.label.submitNoJudgmentSetAndFieldPriv="数据提交入库不判断子集及指标权限";
gz.label.allowEditData="允许修改已归档数据";
gz.label.accumulationUpdate="累加更新";
gz.label.replaceUpdate="替换更新";
gz.label.FieldName="指标名称";
gz.label.updateType="更新方式";
gz.label.updateSeniorSet="更新高级设置";

gz.label.auditRelation="审批关系";
gz.label.defaultAuditRelation="默认审批项目";
gz.label.unitField="归属单位指标";
gz.label.departmentField="归属部门指标";
gz.label.noticeTemplate="通知模板";
gz.label.collectField="汇总指标";
gz.label.comparisonField="比对指标";
gz.button.setComparisonField="设置比对指标";
gz.label.rejectMode="驳回方式";
gz.label.layerReject="逐级驳回";
gz.label.rejectToOriginator="驳回到发起人";
gz.label.noNeedAudit="不需要审批";
gz.label.neddAudit="需要审批";
gz.msg.noB0110="(除B0110外关联UN的指标)";
gz.msg.noE0122="(除E0122外关联UM的指标)";
gz.label.noticeMode="通知方式";
gz.label.smsNotice="短信通知";
gz.label.mailNotice="邮件通知";
gz.label.calculateTaxTime="计税时间指标";
gz.label.appealTaxTime="报税时间指标";
gz.label.sendSalaryItem="发薪标识指标";
gz.label.taxType="计税方式指标";
gz.label.ratepayingDecalre="纳税项目指标";
gz.label.lsDept="归属单位指标";
gz.label.taxUnit="计税单位指标";
gz.label.hiredate="入职时间指标";
gz.label.disability="是否残疾指标";
gz.label.explain="说明：";
gz.msg.explain1="1.发薪标识，请选择工资类别中关联代码类42的指标";
gz.msg.explain2="2.计税方式，请选择工资类别中关联代码类46的指标";
gz.msg.explain3="3.纳税项目，在所得税管理中使用，请选择工资类别中的字符型指标";
gz.msg.explain4="4.归属单位，在所得税管理中使用，请选择工资类别中关联代码UM、UN的指标";
gz.msg.explain5="计税单位，按人员、计税时间、计税方式、计税单位合并计税，请选择关联UN的指标";
gz.msg.explain6="入职时间，日期型的薪资项目，按入职时间当月重新累计计算税";
gz.msg.explain7="是否残疾，关联代码类45的薪资项目，残疾生效时间<br><p style='padding-left:70px;'>在system中指定参数disability_date=指标代号<p>";
gz.label.selectMoney="选用货币";
gz.label.controlScope="控制范围";
gz.label.controlSalaryPay="控制薪资发放";
gz.label.controlSalarySp="控制薪资审批";
gz.label.controlInsurancePay="控制保险发放";
gz.label.controlInsuranceSp="控制保险审批";
gz.label.controlMode="控制方式";
gz.label.forcedControl="强制控制";
gz.label.preWarningControl="预警控制";
gz.label.salaryAmountControl="薪资总额控制";
gz.label.isControlAmount="是否进行总额控制";
gz.button.setAuditReportField="设置审核报告输出指标";
gz.label.auditFormulaControl="审核公式控制";
gz.label.isAuditFormulaControl="是否进行审核公式控制";
gz.label.StopSignControl="停发标识控制";
gz.label.showStopSign="显示停发标识";
gz.label.bonusItems="奖金项目";
gz.label.fieldPriControl="指标权限控制";
gz.label.field_priv="非写指标参与计算";
gz.label.read_field="读权限指标允许重新导入";
gz.label.collect_je_field="汇总审批发放金额指标";
gz.label.bonusItems="非写指标参与计算";

gz.msg.selectRotaltyDataSet="请选择提成数据子集!";
gz.label.rotaltyDataSet="提成数据子集";
gz.label.planDateField="计划日期指标";
gz.label.relationField="关联指标:";
gz.msg.selectRelationField="请选择关联指标！";
gz.label.royaltySalary="提成薪资";
gz.label.month="月";
gz.label.season="季";
gz.label.halfYear="半年";
gz.label.year="年";
gz.label.period="周期";
gz.label.priecerateField="计件指标";
gz.label.salaryField="薪资指标";
gz.label.dataScope="数据范围";
gz.label.natureMonth="自然月份";
gz.label.preMonth="上月";
gz.label.toThisMonth="日到本月";
gz.label.importField="引入指标：";
gz.label.priecerateSalary="计件薪资";

gz.msg.currencyNameMaxLength="最大程度为{0}个字符！";
/** 薪资类别-薪资属性 end */

/** 数据比对 start */
gz.label.infoChange="信息变动";
gz.label.Changemore="数据比对";
gz.label.outExcel="导出Excel";
gz.label.datatable="数据明细-";

gz.label.itemdescName="项目名称";
gz.label.lastdata="上期值";
gz.label.nowdata="本期值";
gz.label.margin="差异值";
gz.label.peoplenum="差异人数";

/** 数据比对 end */
gz.label.selectbosdateandcount="请选择业务日期和次数！";

/** 发送通知 start */
gz.msg.selectPerson="请选择人员！";
/** 发送通知 end */

/** 按方案导入excel格式错误提示 start */
gz.msg.importFailure="文件格式不正确，第一行为导入的文件列名且不能为空";
/** 按方案导入excel格式错误提示 end */

/** 变动对比没有选中提示 start**/
gz.msg.noSelect="请选择数据";
/** 变动对比没有选中提示 end**/

/** 薪资发放--历史填报数据 start**/
gz.label.reportedInformation="历史数据";
gz.label.detailData="个人明细数据";
gz.label.detailedInformation="明细数据";
gz.label.personnelInquire="人员查询";
gz.label.searchWarn="请输入姓名或唯一性指标来进行人员明细数据的查询";

/** 薪资发放--历史填报数据 end**/

/** 薪资发放--应用机构 start**/
gz.label.appOrganization="应用机构：";
gz.label.use="启用";
gz.label.informant="填报人：";
gz.label.addOrganization="新增机构";
gz.label.start="填报日期：从";
gz.label.to="至";
gz.label.deleteOrg="仅能删除鼠标选中的行！";
gz.label.record="的记录";
gz.label.date="业务日期";
gz.label.stateOfWrite="填报状态";
gz.label.message="消息通知";
gz.label.accept="接收人：";
gz.label.saveDate="请填写填报日期！";
gz.label.createSalary="请新建账套后进行下发操作！";
gz.label.distributeSuccess="下发成功！";
gz.label.dingDing="钉钉";
gz.label.wx="微信";
gz.label.cannotNull="消息内容不能为空！";
gz.label.hasAppeal="已报批";
gz.label.sendok="发送成功！";
gz.label.isapplic="是否下发";
gz.label.sending="发送中......";
gz.label.wait="等待";
gz.label.canDistribute="未填写申报人的机构不会下发，确认下发吗？";
gz.label.loginID="登录账号：";
gz.label.compareTip="信息变动中所选人员的 '{0}' 列将被人员信息集数据覆盖，是否继续？（如不想覆盖，可取消勾选信息变动中人员）";
/**薪资发放--个税专项申报start**/
gz.label.zxdeclare={};
gz.label.zxdeclare.cityType={};

gz.label.zxDeclareTitle="个税专项附加申报";
gz.label.zxDeclareSearch="查询方案:";
gz.label.zxDeclareTypeAll="全部分类";
gz.label.zxDeclareTypeChildEdu="子女教育";
gz.label.zxDeclareTypeContinuEdu="继续教育";
gz.label.zxDeclareTypeHouseRent="住房租金";
gz.label.zxDeclareTypeInterestExpense="房贷利息";
gz.label.zxDeclareTypeIllnessMedicalcare="大病医疗";
gz.label.zxDeclareTypeSupportElderly="赡养老人";
gz.label.zxDeclareTypeInfo="专项附加申报信息";
gz.label.approveStateAll="全部状态";
gz.label.approveStateInaudit="审核中";
gz.label.approveStateAdopt="通过";
gz.label.approveStateNotPass="未通过";
gz.label.approveStateFiled="已归档";
gz.msg.zxDeclareTitle="提示信息";
gz.msg.zxDeclareAgree="确定要同意选中的数据？";
gz.msg.zxDeclareReject="确定要退回选中的数据？";
gz.msg.zxDeclareDelete="确定要删除选中的数据？";
gz.msg.zxDeclarePleaseSelect="请选中要操作的记录";


gz.label.zxdeclare.selfInfo="个人信息";
gz.label.zxdeclare.declareInfo="专项信息";
gz.label.zxdeclare.moneydesc="元";
gz.label.zxdeclare.cityType.firstTypeCity="直辖市、省会城市、计划单列市";
gz.label.zxdeclare.cityType.secondTypeCity="市辖区户籍人口超过100万的";
gz.label.zxdeclare.cityType.threeTypeCity="市辖区户籍人口小于100万的";
gz.label.zxdeclare.agreeOperateMsg="同意该申报信息？";
gz.label.zxdeclare.returnOperateMsg="确定退回该申报信息？";
gz.label.zxdeclare.childInfoMsg="子女信息";
gz.label.zxdeclare.supportedManInfoMsg="被赡养人信息";
gz.label.zxdeclare.comonsupportManInfoMsg="共同赡养人信息";
gz.label.zxdeclare.descriptionInfo="备注信息";
gz.label.zxdeclare.fileInfo="附件";
gz.label.zxdeclare.zanwu="暂无";
gz.label.zxdeclare.quankou="全扣";
gz.label.zxdeclare.pingtan="全扣";
gz.label.zxdeclare.fentan="分摊";
gz.label.zxdeclare.declareDate="申报日期";
gz.label.zxdeclare.deductMoney="抵扣金额";
gz.label.zxdeclare.deductType="抵扣方式";
gz.label.zxdeclare.deductionStandard="抵扣标准";
gz.label.zxdeclare.deductTypeMonth="按月抵扣";
gz.label.zxdeclare.deductTypeYear="按年抵扣";
gz.label.zxdeclare.childApportionNum="分摊兄弟姐妹数";
gz.label.zxdeclare.isSelfChild="是否是独生子女";
gz.label.zxdeclare.createData="起始日期";
gz.label.zxdeclare.endDate="结束日期";
gz.label.zxdeclare.loadEndData="还贷结束日期";
gz.label.zxdeclare.loanMoney="贷款总额"
gz.label.zxdeclare.loanContractNo="贷款合同编号";
gz.label.zxdeclare.loanBank="贷款银行";
gz.label.zxdeclare.rentHouseMoney="月租金";
gz.label.zxdeclare.rentHouseCity="主要工作城市";
gz.label.zxdeclare.cityType="城市分类";
gz.label.zxdeclare.eduType="教育方式";
gz.label.zxdeclare.CareerEdu="职业资格继续教育";
gz.label.zxdeclare.continuingEdu="学历（学位）继续教育"
gz.label.zxdeclare.coupleFlat="夫妻是否平摊";
gz.label.zxdeclare.returnBtn="退回";
gz.label.zxdeclare.agreeBtn="同意";
gz.label.zxdeclare.relation="与本人关系";
gz.label.zxdeclare.memberName="子女姓名";
gz.label.zxdeclare.name="姓名";
gz.label.zxdeclare.sex="性别";
gz.label.zxdeclare.birthday="出生日期";
gz.label.zxdeclare.idType="身份证件类型";
gz.label.zxdeclare.idNumber="身份证件号码";
gz.label.zxdeclare.eduLevel="当前受教育阶段";
gz.label.zxdeclare.zeduLevel="教育阶段";
gz.label.zxdeclare.eduStartDate="当前受教育阶段起始时间";
gz.label.zxdeclare.eduEndtDate="当前受教育阶段结束时间";
gz.label.zxdeclare.eduStopDate="教育终止时间";
gz.label.zxdeclare.eduNationality="当前就读国家（地区）";
gz.label.zxdeclare.eduInstitution="当前就读学校";
gz.label.zxdeclare.deductProportion="本人扣除比例";
gz.label.zxdeclare.nationality="国籍（地区）";
gz.label.zxdeclare.currentContinuingEducationStartDate="当前继续教育起始时间";
gz.label.zxdeclare.currentContinuingEducationEndDate="（预计）当前继续教育结束时间";
gz.label.zxdeclare.currentContinuingEducationType="继续教育类型";
gz.label.zxdeclare.dateOfIssue="发证（批准）日期";
gz.label.zxdeclare.postCertificateName="证书名称";
gz.label.zxdeclare.postCertificateNumber="证书编号";
gz.label.zxdeclare.postCertificateOrg="发证机关";
gz.label.zxdeclare.rentHouseProvince="主要工作省份";
gz.label.zxdeclare.rentHouseType="类型";
gz.label.zxdeclare.rentHouseName="出租方姓名（组织名称）";
gz.label.zxdeclare.rentHouseIdType="出租方证件类型";
gz.label.zxdeclare.rentHouseIdNumber="身份证件号码（统一社会信用代码）";
gz.label.zxdeclare.rentHouseAddress="住房坐落地址";
gz.label.zxdeclare.rentHouseNo="住房租赁合同编号";
gz.label.zxdeclare.rentStartDate="租赁期起";
gz.label.zxdeclare.personal="个人";
gz.label.zxdeclare.rentEndDate="租赁期止";
gz.label.zxdeclare.houseAddress="房屋坐落地址";
gz.label.zxdeclare.loanSelfFlag="本人是否借款人";
gz.label.zxdeclare.houseType="房屋证书类型";
gz.label.zxdeclare.houseNumber="房屋证书号码";
gz.label.zxdeclare.loanFlat="是否婚前各自首套贷款且婚后分别扣除50%";
gz.label.zxdeclare.loanType="贷款类型";
gz.label.zxdeclare.loanAllotedTime="贷款期限";
gz.label.zxdeclare.loanStartDate="首次还款日期";
gz.label.zxdeclare.relation="关系";
gz.label.zxdeclare.apportionType="分摊方式";
gz.label.zxdeclare.organization="组织";
gz.label.zxdeclare.skilledPersonProfessionalQualification="技能人员职业资格";
gz.label.zxdeclare.professionalQualifications="专业技术人员职业资格";
gz.label.zxdeclare.yes="是";
gz.label.zxdeclare.no="否";
gz.label.zxdeclare.houseOwnershipCertificate="房屋所有权证";
gz.label.zxdeclare.immovableTitleCertificate="不动产权证";
gz.label.zxdeclare.houseSaleContract="房屋买卖合同";
gz.label.zxdeclare.presaleContract="房屋预售合同";
gz.label.zxdeclare.providentFundLoan="公积金贷款";
gz.label.zxdeclare.commercialLoans="商业贷款";
gz.label.zxdeclare.averageShareOfDependents="赡养人平均分摊";
gz.label.zxdeclare.dependentAgreement="赡养人约定分摊";
gz.label.zxdeclare.assignedByTheDependent="被赡养人指定分摊";
gz.label.zxdeclare.settingText="对应关系";
gz.label.zxdeclare.declareRelationshipTitle="设置对应关系";
gz.label.zxdeclare.relationshipName="税务模板名称";
gz.label.zxdeclare.taxpayerIDType="纳税人身份证件类型";
gz.label.zxdeclare.taxpayerIDTypeIsNull="纳税人身份证件类型不能为空";
gz.label.zxdeclare.taxpayerIDNumber="纳税人身份证件号码";
gz.label.zxdeclare.taxpayerIDNumberIsNull="纳税人身份证件号码不能为空";
gz.label.zxdeclare.phoneNumer="手机号码";
gz.label.zxdeclare.taxpayerIdentificationNumber="纳税人识别号";
gz.label.zxdeclare.contactAddress="联系地址";
gz.label.zxdeclare.email="电子邮箱";
gz.label.zxdeclare.withholdingAgentName="扣缴义务人名称";
gz.label.zxdeclare.withholdingAgentTaxpayerIdentificationNumber="扣缴义务人纳税人识别号";
gz.label.zxdeclare.spouseSituation="配偶情况";
gz.label.zxdeclare.spouseSituationIsNull="配偶情况不能为空";
gz.label.zxdeclare.spouseName="配偶姓名";
gz.label.zxdeclare.spouseIdType="配偶身份类型";
gz.label.zxdeclare.spouseIdNumber="配偶身份证件号码";
gz.label.zxdeclare.select="请选择...";
gz.label.zxdeclare.inputItemNameOrItemCodeSearch="输入指标名称或指标代码查询...";
gz.label.zxdeclare.saveSuccess="保存成功";
gz.label.zxdeclare.saveFail="保存失败";
gz.label.zxdeclare.importComment='请从“自然人税收管理系统/专项附加扣除信息采集”下载模板！';
gz.label.zxdeclare.importCommentAfter='采集模板格式不允许修改!';
gz.label.zxdeclare.exitsTemplateFile='若采集模板已导入，但国税局采集模板有变动请重新上传新模板!';
gz.label.zxdeclare.importFileText='请输入文件路径或选择文件 ';
gz.label.zxdeclare.preschoolEducation='学前教育阶段';
gz.label.zxdeclare.compulsoryEducation='义务教育';
gz.label.zxdeclare.highSchoolEducation='高中阶段教育';
gz.label.zxdeclare.higherEducation='高等教育';
gz.label.zxdeclare.specialist='大学专科';
gz.label.zxdeclare.bachelor='大学本科';
gz.label.zxdeclare.masterStudent='硕士研究生';
gz.label.zxdeclare.doctoralStudent='博士研究生';
gz.label.zxdeclare.other='其他';
gz.label.zxdeclare.exportTemplateTitle='导出申报数据';
gz.label.zxdeclare.selectTemplateFile='导入采集模板';
gz.label.zxdeclare.exportText='导出';
gz.label.zxdeclare.exportDeclareData='导出专项附加扣除数据';
gz.label.zxdeclare.browse='浏览';
gz.label.zxdeclare.agreeErrorTip='只能同意审批状态为审核中的数据';
gz.label.zxdeclare.rejectErrorTip='只能退回审批状态为审核中的数据';
gz.label.zxdeclare.deleteErrorTip='只能删除审批状态为审核中和未通过的数据';
gz.label.zxdeclare.noTemplateFileTip='请先上传个税专项附加申报官方模板文件！';
gz.label.zxdeclare.noTemplateFileErrorTip='请上传国家税务总局提供的标准模板！';
gz.label.zxdeclare.approveTextlabel='驳回原因';
gz.label.zxdeclare.approveTextTitile='请填写驳回原因';
gz.label.zxdeclare.successImportTip='数据导入完成，具体导入信息请到日志中查看!';
gz.label.zxdeclare.importWaitTip='正在导入数据，请稍候...';
gz.label.zxdeclare.exportWaitTip='正在导出数据，请稍候...';
gz.label.zxdeclare.importText='导入税收系统数据';
gz.label.zxdeclare.importFileTip='注意：请从“自然人税收管理系统/专项附加扣除信息采集”中导出数据，</br>然后将文件夹压缩为zip格式，再导入！';
gz.label.zxdeclare.zxDeclareTypeChildEduDeductionStandard='1000元/月/孩';
gz.label.zxdeclare.selectImportFile='请选择专项附加扣除模板';
gz.label.zxdeclare.careereduDeductionStandard='3600元/年'
gz.label.zxdeclare.continuingeduDeductionStandard='400元/月'
gz.label.zxdeclare.interestExpenseDeductionStandard='1000元/月'
gz.label.zxdeclare.supportelderlyDeductionStandard='2000元/月'
gz.label.zxdeclare.importPartPersonFailTip='部分人员导入失败具体人员请到日志中查看'
gz.label.zxdeclare.previousStep='上一步'
gz.label.zxdeclare.nextStep='下一步'
gz.label.zxdeclare.hrFiledName='HR指标名称'
gz.label.zxdeclare.selectFieldSet='选择信息集'
gz.label.zxdeclare.thirdPanelTips1='国税局采集模板已导入，如需更改请点击上一步'
gz.label.zxdeclare.thirdPanelTips2='指标对应关系已设置，如需更改请点击上一步'
gz.label.zxdeclare.successImportTemplate='成功导入{count}条记录'
gz.label.zxdeclare.failImportTemplate='成功导入{successCount}条记录！失败导入{errorCount}条记录！失败原因请查看日志'
gz.label.zxdeclare.isNot='不能为'
gz.label.zxdeclare.blanks='空'

gz.label.supportUnit="支持按归属单位进行所得税管理"
gz.label.disabilityPercent="征收比例<font style='color:red;'>(必填)</font>:"
gz.label.choosePercent="选择残疾人指标后需填写征收比例！"

gz.label.chooseSendWay="请勾选发送方式！"
gz.label.hasChange="数据有变动，请进行保存！"


/**我的薪酬 start*/
gz.label.basicInformation="基本信息";
gz.label.mySalaryTitle="我的薪酬";
gz.label.mySalaryPlan="方案";
gz.label.salaryStructure="薪资结构";
gz.label.programName="方案名称";
gz.label.salaryScale="薪资表";
gz.label.timeDimensionIndicator="时间维度指标";
gz.label.payable="应发工资";
gz.label.taxableAmount="应纳税所得额";
gz.label.personalIncomeTax="个人所得税";
gz.label.realWage="实发工资";
gz.label.affiliatedOrganization="所属单位";
gz.label.visibleRange="可见范围";
gz.label.addRoles="添加角色";
gz.label.salaryItemCategory="薪资项目分类";
gz.label.salaryItem="薪资项目";
gz.label.whetherTheLegend="是否图例";
gz.label.total="合计";
gz.label.zeroItemCtrl="为零项不显示";
gz.label.create="创建";
gz.label.update="编辑";
gz.label.view="视图";
gz.label.del="删除";
gz.label.edite="编辑";
gz.label.saveSuccess="保存成功！";
gz.button.save="保存";
gz.label.saveFail="失败";
gz.label.tips="提示";
gz.label.viewName="名称代码";
gz.label.viewTable="代码";
gz.label.add="添加";
gz.label.addViewTip="创建视图";
gz.label.updateViewTip="编辑视图";
gz.label.pleaseSetViewName="请输入视图名称！";
gz.label.pleaseSetViewNameNotBlank="视图名称不能全为空格，请重新输入视图名称!";
gz.label.pleaseSetViewTable="请输入视图代码！";
gz.label.mainDataSource="视图主表";
gz.label.pleaseSetMainDataSource="请输入视图主表！";
gz.label.pleaseSetDBName="请配置人员库！";
gz.label.pleaseSetField="请配置指标！";
gz.label.selectViewFieldExist="【{item1}】中的【{item2}】不允许重复添加！";
gz.label.fieldSetting="视图指标";
gz.label.select="请选择...";
gz.label.noSelect="不选择";
gz.label.show="只显示";
gz.label.yearSalary="年以后的薪酬";
gz.label.basicParamSetting="基础参数配置";
gz.label.viewNbasesCreateError="您没有相应人员库权限，无法创建视图！"
gz.label.viewNbasesUpdateError="您没有相应人员库权限，无法修改视图！"
gz.label.viewSettingCreateError="您没有相应子集权限和指标权限，无法创建视图！";
gz.label.viewSettingUpdateError="您没有相应子集权限和指标权限，无法修改视图！";
gz.label.searchEmptyText="输入指标名称或指标代码查询…";
gz.label.selectOnePlans="请至少选择一个方案！";
gz.label.selectOneField="请至少选择一个指标！";
gz.label.pelaseWriteAll="请填写薪资项目分类和薪资项目！";
gz.label.isSelfServiceUserTips="非自助用户无法查看我的薪酬方案!";
gz.label.isSelfTaxServiceUserTips="非自助用户无法查看我的个税!";
gz.label.viewErrorBydigital="视图代码只能由数字字母下划线组成</br>并以字母开头！";
gz.label.viewErrorBydigitalNull="视图代码不能为空！";
gz.label.viewTableRepeat="表或视图已存在，请重新输入视图表名！";
gz.label.delPlanSuccess="删除方案成功！";
gz.label.isDelPlanSuccess="您确定要删除方案吗？";
gz.label.isDelItemsSuccess="您确定要删除该项目分类吗？";
gz.label.itemDescIsEmpty="视图指标名称不能为空！";
gz.label.payDate="发薪日期";
gz.label.yearOfOwnership="所属年度";
gz.button.previousStep="上一步";
gz.button.nextStep="下一步";
gz.button.ok="确定";
gz.button.no="取消";
gz.label.noDataForThisScheme="暂无数据！";
gz.label.noSchemeForThisPerson="该员工未配置薪酬方案！";
gz.label.isClearData="您是否清空原有数据";
gz.label.isDeletRole="您确认要删除该角色吗？";
gz.label.isHaveFieldItem="此薪资项已被其他项占用！";
gz.label.repeatSalaryName="此方案名称已存在！";
gz.label.salaryItemNameIsNotNull="请填写薪资项目分类名称！";
gz.label.salaryItemIsNotNull="请填写薪资项目！";
gz.label.salaryProgramNameIsNotNull="请填写方案名称！";
gz.label.selectItem="选择项目";
gz.label.back='返回';
gz.label.isNotviewTable="视图表不存在，请到【薪资管理/参数设置/应用配置/我的薪酬】处重新配置视图！";
gz.label.getViewFieldError="获取{view}薪资表指标出错！";
gz.label.GZSchemeFiledConfigError="薪资方案时间维度指标未在薪酬表中配置，请到【薪资管理/配置参数/我的薪酬/薪酬表】处配置时间维度指标！";
gz.label.emptyScheme = "薪酬方案配置指标及薪资结构为空！"
/**我的个税 start*/
gz.label.calculationRules="计算公式";
gz.label.summaryByYear="是否累计";
gz.label.myTax="我的个税";
gz.button.creation="新增";
gz.label.delSuccess="删除记录成功！";
gz.label.delNoRecord="请至少选择一条记录删除！";
gz.label.january="一月";
gz.label.february="二月";
gz.label.march="三月";
gz.label.april="四月";
gz.label.may="五月";
gz.label.june="六月";
gz.label.july="七月";
gz.label.august="八月";
gz.label.september="九月";
gz.label.october="十月";
gz.label.november="十一月";
gz.label.december="十二月";
gz.label.noHaveData="暂无数据";
gz.label.noHaveTaxSetting="暂未配置我的个税，请联系管理员！";
gz.label.atLestSelectOneField="请至少选择一个指标项！";
gz.label.isSureDeleteSelectData="您确定要删除选中的数据吗？";
gz.label.editFormula="编辑公式";
gz.label.viewCodeMaxLength="视图代码最大长度为8个字符！";
gz.label.dueToItemChange="由于个税明细表结构发生变化，系统自动为您移除了以下配置指标:<br>";
gz.label.zxfjkc='赡养老人,大病医疗,住房贷款利息,住房租金,继续教育,子女教育';
gz.label.peleaseCheckFormula='请检查以下项目的计算公式：<br>';
gz.label.notDelItem='请至少选择一个项目删除！';
//-------haosl 20190824-新版薪资分析表---------
gz.label.please="请";
gz.label.salary="薪资";
gz.label.insurance="保险";
gz.label.salary2="工资";
gz.label.analysisReport="分析报表"
gz.label.analysisdata={};
gz.label.analysisdata.showtotal="显示合计行";
gz.label.analysisdata.statisticalinterval="统计区间";
gz.label.analysisdata.from="从";
gz.label.waitforopen="正在打开，请稍候...";
gz.label.analysisdata.setrange='设置取数范围';
gz.label.analysisdata.queryitem="请输入姓名、唯一编号、单位、部门名称";
gz.label.analysisdata.queryitemnoonlycode="请输入姓名、单位、部门名称";
gz.label.analysisdata.navigation="功能导航";
gz.label.analysisdata.pagesetting="页面设置";
gz.label.analysisdata.particularyear="年份";
gz.label.analysisdata.showmxornot="不显示每月数据";
gz.label.analysisdata.accumulate="显示月累计";
gz.label.analysisdata.incloudLowLevel="统计时包含下级";
gz.label.analysisdata.groupornot="不分类";
gz.label.analysisdata.columnchart="柱状图";
gz.label.analysisdata.piechart="饼状图";
gz.label.analysisdata.linechart="折线图";
gz.label.analysisdata.orderDepartment="按部门各月工资构成分析表";
gz.label.analysisdata.grade="级";
gz.label.analysisdata.showMonthCount="显示每月人数";
gz.label.analysisdata.levelSummary="按层级汇总";
gz.label.analysisdata.deadlineMonth="截止月份";
gz.label.analysisdata.monthlywagecomposition="各月工资构成分析表";
gz.label.analysisdata.excelall = "全部导出";
gz.label.analysisdata.excelpart = "部分导出";
gz.label.analysisdata.waitformessage = "正在执行导出Excel，请稍候...";
gz.label.analysisdata.outof1000formessage = '导出Excel人数不能超过1000，请重新筛选后再执行导出操作！';
gz.label.analysisdata.selectneedperson = "请选择要导出的人员！";
gz.label.analysisdata.dragColumnTip="本页面调整顺序不支持永久保存！";

//--新版薪资分析报表创建等--

var AnalysisTables = {};
var OptAnalysisTable = {};
AnalysisTables.title = "{0}分析表";
AnalysisTables.tableTr_tableName = "名称";
AnalysisTables.tableTr_B0110 = "所属机构";
AnalysisTables.tableTr_username = "创建人";
AnalysisTables.tableTr_createtime = "创建时间";
AnalysisTables.tableTr_opretion = "操作";
AnalysisTables.warn_message = "提示信息";
AnalysisTables.isdelete_selected_data = "是否删除{0}？";
AnalysisTables.add = "新增";
AnalysisTables.update = "修改";
AnalysisTables.dele = "删除"
AnalysisTables.dele_all = "全部删除";
AnalysisTables.copy = "复制";
AnalysisTables.linkSet = "请联系管理员完整设置该薪资分析表的人员库、薪资类别等！";
OptAnalysisTable.title = "{0}分析表";
OptAnalysisTable.firstStep = "第一步";
OptAnalysisTable.secondStep = "第二步";
OptAnalysisTable.thirdStep = "第三步";
OptAnalysisTable.select_template_type = "选择分析表类型";
OptAnalysisTable.set_num_cond = "设置取数条件";
OptAnalysisTable.define_static = "定义统计项";
OptAnalysisTable.next = "下一步";
OptAnalysisTable.prev = "上一步";
OptAnalysisTable.save = "保存";
OptAnalysisTable.back = "返回";
OptAnalysisTable.analy_table = "分析表名";
OptAnalysisTable.select_nbase = "选择人员库";
OptAnalysisTable.select_type = "选择类别";
OptAnalysisTable.number = "编号";
OptAnalysisTable.type_name = "类别名称";
OptAnalysisTable.emptyText = "请输入{0}类别编号或名称";
OptAnalysisTable.iscomplatedata = "含审批过程数据";
OptAnalysisTable.preliminary_item = "备选指标";
OptAnalysisTable.selected_item = "已选指标";
OptAnalysisTable.select_salary_type = "请选择{0}类别！";
OptAnalysisTable.must_check_project = "请选择{0}项目";
OptAnalysisTable.success = "成功";
OptAnalysisTable.fail = "失败";
OptAnalysisTable.user_salary_count = "人员{0}台账";
OptAnalysisTable.salary_project_summary_count = "{0}项目分类统计台账";
OptAnalysisTable.user_salary_project_summary_table = "人员{0}项目统计表";
OptAnalysisTable.salary_total_contant_table = "工资总额构成分析表";
OptAnalysisTable.unit_salary_project_summary = "单位部门工资项目统计表";
gz.label.analysisdata.summarytable="人员{0}汇总表";
OptAnalysisTable.activeindex="缩略图";
OptAnalysisTable.noselected='请勾选要{0}的指标！';
OptAnalysisTable.itemup='上移';
OptAnalysisTable.itemdown='下移';
OptAnalysisTable.itemadd='添加';
OptAnalysisTable.mustfillText = "请输入分析表名";
gz.label.rowNumberer="序号";
//-------20191205-新版税率表--------
gz.taxTable = {};
gz.taxTable.msg = {};
gz.taxTable.msg.getAllTaxTableError = '获取税率表方案信息出错！';
gz.taxTable.msg.getSpecifyTaxTableError = '获取指定税率表方案信息出错！';
gz.taxTable.msg.insertError = '新增税率方案信息出错！';
gz.taxTable.msg.uopdateError = '更新税率表方案信息出错！';
gz.taxTable.msg.deleteError = '删除税率表方案信息出错！';
gz.taxTable.msg.getDetailDataError = '获取指定税率明细方案信息出错！';
gz.taxTable.msg.insertDetailError = '新增税率明细方案信息出错！';
gz.taxTable.msg.updateDetailError = '更新税率明细方案信息出错！';
gz.taxTable.msg.deleteDetailError = '删除税率明细方案信息出错！';
gz.taxTable.msg.checkTaxTableTosalaryformulaError = '查询税率表方案是否在计算公式中使用出错！';
gz.taxTable.msg.getTaxModeError = '获取计税方式代码项出错！';
gz.taxTable.msg.getMaxTaxitemIdError = '获取指定税率表下税率明细表最大ID值出错！';
gz.taxTable.msg.getMaxIdError = '获取税率表或明细表最大ID值出错！';
gz.taxTable.msg.exportTaxTableError = '导出税率表出错！';
gz.taxTable.msg.importTaxTableOneError = '导入税率表创建工作空间出错！';
gz.taxTable.msg.importTaxTableOneAddError = '新增税率表数据格式错误！';
gz.taxTable.msg.importTaxTableOneAddItemError = '新增税率明细表数据格式错误！';
gz.taxTable.msg.importTaxTableOneAssembleError = '导入组装税率表数据出错！';
gz.taxTable.msg.importTaxTableOneAssembleItemError = '导入组装税率明细表数据出错！';
gz.taxTable.msg.importTaxTableFormatError = '导入税率表格式出错！';
gz.taxTable.msg.importTaxTableTwoCoverError = '覆盖数据格式错误！';
gz.taxTable.msg.importTaxTableTwoAddError = '追加数据格式错误！';
gz.taxTable.msg.codeToXmlError = '转换XML出错！';
gz.taxTable.msg.isHaveOperationPrivError = '检验税率表操作权限出错！';
gz.taxTable.msg.importTaxTableSheetError = '导入sheet表数量错误！';
gz.taxTable.msg.importTaxTableTaxRowError = '税率表列首格式错误！';
gz.taxTable.msg.importTaxTableItemRowError = '税率表明细列首格式错误！';

//------------税率明细表--------------
gz.taxTableDetail = {};
gz.taxTableDetail.msg = {};
gz.taxTableDetail.addTitle = '新建税率表';
gz.taxTableDetail.updateTitle = '修改税率表';
gz.taxTableDetail.msg.hint = '提示';
gz.taxTableDetail.msg.save = '保存';
gz.taxTableDetail.msg.back = '返回';
gz.taxTableDetail.msg.zore = '第';
gz.taxTableDetail.msg.first = '请将第';
gz.taxTableDetail.msg.second = '行上限值与第';
gz.taxTableDetail.msg.rowLowerLimit = '行下限值【';
gz.taxTableDetail.msg.consistent = '】保持一致！ ';
gz.taxTableDetail.msg.saveNone = '没有需要保存的数据！';
gz.taxTableDetail.msg.isDelete = '确定删除该条记录吗?';
gz.taxTableDetail.msg.notDelete = '税率之间存在不闭合，不能进行删除！';
gz.taxTableDetail.msg.deleteSuccess = '删除成功！';
gz.taxTableDetail.msg.ynse_up = '上限封闭';
gz.taxTableDetail.msg.ynse_down = '下限封闭';
gz.taxTableDetail.msg.name = '名称';
gz.taxTableDetail.msg.nameNone = '税表名称不能为空！';
gz.taxTableDetail.msg.sskcs = '';
gz.taxTableDetail.msg.cardinal = '基数';
gz.taxTableDetail.msg.cardinalNone = '基数不允许为空！';
gz.taxTableDetail.msg.k_baseIsNumber = ' 基数只能输入数字且小数位最多为两位！ ';
gz.taxTableDetail.msg.taxWay = '计税方式';
gz.taxTableDetail.msg.saveOK = '保存成功！';
gz.taxTableDetail.msg.downNumber = '数据不允许输入负数！' ;
gz.taxTableDetail.msg.idNumber = '只能输入数字！';
gz.taxTableDetail.msg.numberBig = '行上限数据要大于下限数据！';
gz.taxTableDetail.msg.maxNumber = '该输入项的最大长度是11位！';
gz.taxTableDetail.msg.sl = '税率请输入小数';
gz.taxTableDetail.msg.numberNotSame = '上限与下限数据不一致！';
gz.taxTableDetail.msg.dataChange = '变动数据未保存，是否继续返回？';
gz.taxTableDetail.msg.row = '行';
gz.taxTableDetail.msg.upperLimit = ' 应纳税所得额上限值【';
gz.taxTableDetail.msg.overstepLowerLimit = '】需大于下限值【';
gz.taxTableDetail.msg.leftBrackets = '【';
gz.taxTableDetail.msg.rightBrackets = '】' ;
gz.taxTableDetail.msg.andCondition = '】应与第';
gz.taxTableDetail.msg.taxExist = '已存在，请更换税率表名称！';
gz.taxTableDetail.msg.stringIllegal = '税率表名称含有非法字符！';
gz.taxTableDetail.msg.remuneration = '劳务报酬';
gz.taxTableDetail.msg.nameLength = '名称超过50个字符';
gz.taxTableDetail.msg.sskcsNone = '该项不允许为空';
gz.taxTableDetail.msg.reloadDataError = '刷新税率明细表数据异常！'

//------------税率表首页--------------u
gz.taxTableHomePage = {};
gz.taxTableHomePage.msg = {};
gz.taxTableHomePage.msg.tip = '提示';
gz.taxTableHomePage.msg.noneSeleted = '没有选择需要删除的数据！';
gz.taxTableHomePage.msg.dataIsNone = '数据为空！';
gz.taxTableHomePage.msg.numLessThanZero = '基数小于0！';
gz.taxTableHomePage.msg.numIllegal = '请输入合法数字！';
gz.taxTableHomePage.msg.stringPureBlank = '名称不能是纯空格！';
gz.taxTableHomePage.msg.stringIsNull = '名称不能为空！';
gz.taxTableHomePage.msg.stringIllegal = '名称含有非法字符！';
gz.taxTableHomePage.msg.noneModifiedRecords = '没有修改过任何信息！';
gz.taxTableHomePage.msg.getAllTaxTableError = '获取税率表首页信息失败！';
gz.taxTableHomePage.msg.packageColunmsError = '组装列信息失败！';
gz.taxTableHomePage.msg.getButtonsError = '获取按钮列表失败！';
gz.taxTableHomePage.msg.getCodeitemError = '从xml字符串中取到代码号失败！';
gz.taxTableHomePage.msg.getXmlError = '在xml添加属性失败！';
gz.taxTableHomePage.msg.exportUsedMsgError = '导出占用信息失败！';
gz.taxTableHomePage.msg.saveTaxTableSuccess = '保存税率表信息成功！';
gz.taxTableHomePage.msg.saveTaxTableError = '保存税率表信息失败！';
gz.taxTableHomePage.msg.deleteTaxTable = '确定删除选中记录？';
gz.taxTableHomePage.msg.repeatdTaxTablePre = ' 税率表名：';
gz.taxTableHomePage.msg.repeatdTaxTableSuf = '重复，请修改！ ';
gz.taxTableHomePage.msg.deleteTaxTableError = '删除税率表信息失败！';
gz.taxTableHomePage.msg.deleteTaxTableSuccess = '删除税率表信息成功！';
gz.taxTableHomePage.msg.haveUsedFormulaError = '税率表被占用，占用信息请查看TXT！';
gz.taxTableHomePage.msg.exportSelectNull = '请选择记录！';
gz.taxTableHomePage.msg.importTypeAdd = '追加';
gz.taxTableHomePage.msg.importTypeUpdate = '覆盖';
gz.taxTableHomePage.msg.importTaxName = '税表名称';
gz.taxTableHomePage.msg.importType = '操作';
gz.taxTableHomePage.msg.importTypeTitle = '税率表';
gz.taxTableHomePage.msg.importTitle = '请选择导入文件！';
gz.taxTableHomePage.msg.importSuccess = '导入成功！';
gz.taxTableHomePage.msg.importMsg = '请选择数据！';
gz.taxTableHomePage.msg.importButtonName = '导入';
gz.taxTableHomePage.msg.importCoverMsg = '确定覆盖原有税率表吗？';
gz.taxTableHomePage.msg.importHtml = '说明：请用下载的Excel模板来导入数据！模板格式不允许修改！';


gz.standard.tip = "提示";
gz.standard.saveSuccess = "保存成功！";
gz.standard.saveFail = "保存失败！";
gz.standard.confirm = "确定";
gz.standard.cancel = "取消";
gz.standard.tipValue="请选择下载的标准表";
gz.standard.getColumnInfoError = "获取列头信息出错！";

gz.standard.sd.isSureDeleteSelectList="您确定要删除选中薪资标准表吗?";
gz.standard.sd.deleteSuccess="删除成功!";
gz.standard.sd.deleteFailed="删除失败!";
gz.standard.sd.containSpecial="薪资标准表名称中不允许有特殊字符！";

gz.standard.pkg.stopClosePkg = "不允许关闭当前启用的历史沿革！";
gz.standard.pkg.firstSaveData = "请先保存修改的数据！";
gz.standard.pkg.enableFail = "历史沿革启用失败！";
gz.standard.pkg.enablePkgConfirm = "是否启用该历史沿革？";
gz.standard.pkg.noPrivEnablePkg = "无权修改历史沿革的启用状态！";
gz.standard.pkg.browsePackage = "查看历史沿革";
gz.standard.pkg.editPackage = "修改历史沿革";
gz.standard.pkg.downloadPackage = "下载历史沿革";
gz.standard.pkg.deletePackage = "删除历史沿革";

//导入
gz.standard.pkg.importfileTypesDesc="请选择文件";
gz.standard.pkg.importbuttonText="浏览";
gz.standard.pkg.importTipValue="正在导入数据";
gz.standard.pkg.importWindowText="选择导入的薪资标准";
gz.standard.pkg.importSecWindowFirButtonText="追加";
gz.standard.pkg.importSecWindowText="请选择导入的薪资标准";
gz.standard.pkg.isImport="确认执行追加导入操作";
gz.standard.pkg.importSecWindowSecButtonText="覆盖";
gz.standard.pkg.isImpotInsert="确认执行覆盖导入操作";
gz.standard.pkg.errorImpotInsert="导入标准表数据失败，详情请参照日志文件";
//导出
gz.standard.pkg.tipValue="请选择要导出的历史沿革！";
gz.standard.pkg.exportFirstColumName="编号";
gz.standard.pkg.exportSecondeColumName="标准表名称";
gz.standard.pkg.exportFirstWindowButton="确定";
gz.standard.pkg.exportSecondWindowButton="取消";
gz.standard.pkg.exportStandardError="导出时获取标准表失败";
gz.standard.pkg.exportSectStandardError="导出标准表失败";
gz.standard.pkg.noImportOrExportPriv = "您无权导入导出当前历史沿革！";
gz.standard.pkg.exportStandPackage="仅允许导出一个历史沿革！"
gz.standard.pkg.noDataToSave = "数据未修改，无需保存！";
gz.standard.pkg.mustSelectModify = "请勾选修改过的数据！";
gz.standard.pkg.deletePkgConfirm = "是否要删除该历史沿革？";
gz.standard.pkg.deletePkgFail = "删除历史沿革失败！";
gz.standard.pkg.stopDeletePkg = "该历史沿革中已引用标准表，不能删除！";
gz.standard.pkg.enablePkgConfirm = "是否启用该历史沿革？";
gz.standard.pkg.enableFail = "历史沿革启用失败！";
gz.standard.pkg.getPkgDataError = "获取历史沿革数据失败！";
gz.standard.pkg.addTitle = "新建历史沿革";
gz.standard.pkg.inputName = "输入名称";
gz.standard.pkg.start = "启用";
gz.standard.pkg.selectOrganization = "选择所属组织";
gz.standard.pkg.importStandard = "引用标准表";
gz.standard.pkg.standardName = "标准表名称";
gz.standard.pkg.selectUsed = "选用";
gz.standard.pkg.nameIsNull = "名称不允许为空！";
gz.standard.pkg.b0110IsNull = "所属组织不允许为空！";
gz.standard.pkg.inputValidate = "该输入项为必填项！";


gz.standard.importTip="请选择系统导出的zip文件进行导入!";
gz.standard.importText = "导入";
gz.standard.importSuccess = "导入成功！";
gz.standard.getStandStructInforError = "获取标准表结构出错";
gz.standard.getStandTableItemDataError = "获取标准表单元格数据出错";
gz.standard.getStandDataError = "获取标准表数据出错";
gz.standard.revisionStructure = "调整标准表";
gz.standard.importfileTypesDesc="请选择标准表模板文件";
gz.standard.deleteFailByPriv="您无权删除此薪资标准表！";
gz.standard.exportFailDesc="请保存标准表后再进行导出操作！";
gz.standard.initStandStructError="初始化标准表结构数据出错";

//标准表结构窗口
gz.standard.sdst.firstStepname = "横向指标";
gz.standard.sdst.secondStepname = "纵向指标";
gz.standard.sdst.thirdStepname = "结果指标及标准表名称";
gz.standard.sdst.titleCreate = "创建薪资标准表";
gz.standard.sdst.titleStruct = "薪资标准表调整";
gz.standard.sdst.tipValueRCom = "请选择标准表结果指标！";
gz.standard.sdst.tipValueRName = "请输入标准表名称！";
gz.standard.sdst.resultComemptyText = "结果指标";
gz.standard.sdst.resultNamemptyText = "请输入标准表名称";
gz.standard.sdst.tipValuefactor = "请选择一级指标项！";
gz.standard.sdst.tipValues_factor = "请选择二级指标项！";
gz.standard.sdst.tipValueEmpty = "请输入至少一个指标！";
gz.standard.sdst.addLexpr = "二级指标不能为空！";
gz.standard.sdst.addLexprtype = "请选择日期型或字符型！";
gz.standard.sdst.lexprtypeDfirst = "年龄（";
gz.standard.sdst.lexprtypeDsecond = "工龄（";
gz.standard.sdst.lexprtypeDthird = "年份（";
gz.standard.sdst.lexprtypeDforth = "月份（";
gz.standard.sdst.lexprOperatestore = "无";
gz.standard.sdst.lexprtitle = "数据区间";
gz.standard.sdst.lexpremptyText = "输入区间描述";
gz.standard.sdst.lexprboxLabel = "精确到天";
gz.standard.sdst.addLexprFail = "二级指标保存失败！";
gz.standard.sdst.tiplexprDes = "请输入区间描述！";
gz.standard.sdst.tiplexprValue = "请至少输入一个区间值！";
gz.standard.sdst.tiplexprLValue = "请输入左侧区间值！";
gz.standard.sdst.tiplexprRValue = "请输入右侧区间值！";
gz.standard.sdst.tiplexprDNaN = "请输入正整数！";
gz.standard.sdst.tiplexprNNaN = "请输入数值型数据！";
//薪资历史数据重构 资源定义
gz.historyData = {};
gz.historyData.msg = {};

gz.historyData.tip = '提示';
gz.historyData.confirm = '确定';
gz.historyData.cancel = '取消';
gz.historyData.title = '工资数据管理';
gz.historyData.revert_title='还原';
gz.historyData.archive_title='归档';
gz.historyData.delete_title='删除';
gz.historyData.hintmsg='提示信息';
gz.historyData.nostart='起始时间不能为空！';
gz.historyData.noend='结束时间不能为空！';
gz.historyData.start_big_end='起始时间不能大于结束时间！';
gz.historyData.change_struct='薪资历史数据表，结构已经发生变化，进行还原操作，将会丢失部分数据，确定进行还原吗？';
gz.historyData.changeSalaryType = '选择类别';
gz.historyData.DataSource = '数据来源：';
gz.historyData.archive = '归档';
gz.historyData.notArchive = '未归档';
gz.historyData.count = '次数：';
gz.historyData.all = '全部';
gz.historyData.timeLimit = '时间范围';
gz.historyData.from = '从';
gz.historyData.to = '至';
gz.historyData.standing = '正在';
gz.historyData.revertDataNo = '无数据可供还原！';
gz.historyData.achieveDataNo = '无数据可供归档！';
gz.historyData.msg.excelExportError = 'excel导出失败！';
gz.historyData.msg.getDateListError = '获取日期组件数据出错！';
gz.historyData.msg.selectSalaryType = '请选择薪资类别！';
gz.historyData.msg.dataError = '薪资历史数据请求失败！';
gz.historyData.msg.noPrivData = '您权限范围内没有可供查看的数据！';
gz.historyData.msg.archiveDataError = '归档失败！';
gz.historyData.msg.revertDataError = '还原失败!';
gz.historyData.msg.deleteDataError = '删除失败！';
gz.historyData.msg.noArchivePriv = '管理员没有给您授权业务范围，您无权归档数据！';
gz.historyData.msg.noRevertPriv = '管理员没有给您授权业务范围，您无权还原数据！';
gz.historyData.msg.noDeletePriv = '管理员没有给您授权业务范围，您无权删除归档数据！';
gz.historyData.msg.syncHistorySalaryDataError = '同步历史薪资数据结构失败！无法操作！';
gz.historyData.msg.syncTaxScheduleStructureError = '同步个税明细表结构失败！无法操作！';
gz.historyData.msg.getCountError = '获取薪资账套发放次数出错！';
gz.historyData.msg.getSalaryIdError = '初始化获取薪资账套出错！';
gz.historyData.msg.getColumnsFieldListError = '获取薪资账套表格列出错！';
gz.historyData.msg.selectBosdateAndCount="请选择业务日期和发放次数！";
gz.historyData.delete_hint = '此操作为彻底删除，数据不可恢复，请谨慎操作！';