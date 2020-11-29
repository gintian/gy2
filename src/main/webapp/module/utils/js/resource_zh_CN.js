
// 公共的资源文件，全部以common开头
var common ={};
common.error ={};
common.msg ={};
common.label ={};
common.button ={};
common.button.resource = {};
common.button.sms = {};

common.button.toreturn="返回";
common.button.todelete="删除";
common.button.abate="废除";
common.button.save="保存";
common.button.saveas="另存为";
common.button.rename="重命名";
common.button.move="移库";
common.button.batchinout="批量导入";
common.button.edit="编辑";
common.button.update="修改";
common.button.view="浏览";
common.button.insert="新增";
common.button.writeoff="注销";
common.button.query="查询";
common.button.remove="清除";
common.button.hquery="简单查询";
common.button.gquery="常用查询";
common.button.cquery="通用查询";
common.button.ok="确定";
common.button.allview="全显";
common.button.app="申请";
common.button.cancel="取消";
common.button.clear="重置";
common.button.clearup="清空";
common.button.addfield="添加";
common.button.allclear="全无";
common.button.allwrite="全写";
common.button.allread="全读";
common.button.next="下移";
common.button.previous="上移";
common.button.newenroll="新用户注册";
common.button.close="关闭";
common.button.affirm="确认";
common.button.combine="合并";
common.button.transfer="划转";
common.button.abolish="撤销";
common.button.leave="返回";
common.button.open="打开";
common.button.fillout="重填";
common.button.appeal="报批";
common.button.report="报审";
common.button.audit="审核";
common.button.approve="批准";
common.button.issue="发布";
common.button.reject="驳回";
common.button.rejeect2="退回";
common.button.movenextpre="调整顺序";
common.button.priv="模块授权";
common.button.cardpdf="生成PDF";
common.button.cardspdf="批量生成PDF";
common.button.resource.assign="资源分配";
common.button.sms.send="发送";
common.button.submit="提交";
common.button.checkcond="条件校验";
common.button.print="打印";
common.button.newadd="新增";
common.button.newinsert="插入";
common.button.apply="审批";
common.button.kill="终止";
common.button.reassign="重新分派";
common.button.fill="填表";
common.button.batapply="批量审批";
common.button.priview="打印预演";
common.button.pridata="打印数据";
common.button.copy="复制";
common.button.replace="批量替换";
common.button.check="签批";
common.button.reload="发布内容";
common.button.find="查找";
common.button.toexport="导出";
common.button.toimport="导入";
common.button.overimport="覆盖导入";
common.button.additionimport="追加导入";
common.button.setcode="代码设置";
common.button.savereturn="保存&继续";
common.button.propelling="推送";
common.button.cond="条件";
common.button.wizard="向导";
common.button.allselect="全选";
common.button.allreset="全撤";
common.button.promptmessage="提示信息";
common.button.refresh="刷新";
common.label.appDate='业务日期';
common.button.set="设置";
common.msg.isDelete="确认删除？";
common.msg.selectData="请选择数据！";
common.button.upLoad="上传";
common.button.downLoad="下载";
common.button.downLoadTitle="下载模板";
common.label.fileUpLoadFaile="文件上传失败";
common.button.toTop="置顶";
common.button.year="年份";

/** 导入导出 start **/
common.msg.exporting="正在导出，请稍候";
common.msg.importing="正在导入，请稍候";
common.msg.wait="等待";
common.error.uploadFailed="上传失败";
common.label.selectImportFile='请选择导入文件';	
common.msg.successImport='成功导入';
common.msg.dataNums="条数据";
common.msg.affirmImport="确认要导入数据？";
/** 导入导出 start **/

/** 计算公式 start **/
common.label.formula="公式";
common.label.standardTable="标准表";
common.label.taxRateTable="税率表";
common.label.state="状态";
common.label.enable="启用";
common.label.name="名称";
common.label.execute="执行";
common.label.DragDropData="拖放数据";
common.label.operationaSymbol="运算符号";
common.label.referenceItems="参考项目";
common.label.fieldsets="信息集";
common.label.items="项目";
common.label.code="代码";
common.label.formulaList="公式列表";
common.label.spFormula="审核公式";
common.label.computeFormula="计算公式";
common.msg.saveSucess="保存成功";
common.msg.saveFailed="保存失败";
common.label.formulaName="公式名称";
common.label.spTips="审核提示";
common.label.isDeleteSelected="是否删除选中的";
common.label.isDeleteFormula="是否删除选中的公式";
common.msg.selectDelFormula="请选择要删除的公式！"
common.msg.selectEditFormula="请选择要编辑的公式！"
common.label.deleteFailed="删除失败";
common.label.syntaxError="此处有语法错误";
common.msg.noSaveFormula="您有未保存的公式，请先保存后再新增！";
common.msg.moveToDesignated="请移动到指定位置";
common.label.resultField="结果指标：";
common.label.lrtyperow="横向：";
common.label.lrtypeline="纵向：";
common.button.If="如果";
common.button.Else="那么";
common.button.And="且";
common.button.Or="或";
common.button.Not="非";
common.button.Like="包    含";
common.button.functionGuide="函数向导";
common.button.computeCondition="计算条件";
common.button.formulaSave="公式保存";
common.msg.formulanoNull="公式不能为空！";
common.msg.FormulaNameCanNotNull="公式名称不能为空！";
common.msg.isCheckAllFormula="是否对所有计算项进行公式检查？";
common.msg.hasFormulaNoCond="有计算项没有定义公式，请定义公式后再关闭！";
common.msg.formulaCanNotNull="公式列表不能为空！";
common.msg.exprCanNotNull="公式不能为空！";
common.msg.selectFormulaVar="请选择计算公式！";
common.msg.FormulaNameTooLong="名称长度过长！";
common.msg.FormulaNameNotNull="公式名称不能为空！"
common.msg.notHaveFormulaName="公式名称为空，无法启用！";

common.label.year='年';
common.label.month='月';
common.label.day='日';
common.label.count='次';
/** 计算公式 end **/

/** 临时公式 start **/
common.label.tempVar="临时变量";
common.label.select="选择";
common.label.share="共享";
common.label.codesetid="代码类";
common.label.fldlen="长度";
common.label.flddec="位数";
common.label.ntype="类型";
common.label.countn="数值型";
common.label.charat="字符型";
common.label.date="日期型";
common.label.codeType="代码型";
common.label.relatedReference="相关引用";
common.msg.pleseSelectVar="请选择临时变量！";
common.msg.onlySelectOne="只能查看一个临时变量，请重新选择！";
common.msg.flddecNoGreaterfldlen="位数不能大于长度！";
common.msg.valueNotNull="输入值不能为空！";
common.msg.noReference="该临时变量暂没被引用！";
common.msg.deleteSuccess="删除成功";
common.label.fieldset="子集";
common.label.selectFieldset="请选择子集";
common.label.item="指标";
common.label.selectItem="请选择子集指标";
common.error.notFirstNum="临时变量第一位不能为数字！";
common.error.tempVarNotNull="临时变量名称不能为空！";
common.error.notSpecialChar="临时变量名称不能包含特殊字符！";
common.msg.expressionaIsNull="表达式为空！";
common.msg.selectTempVar="请选中相关临时变量！";
common.label.expression2="表达式";
/** 临时公式 end **/

common.label.from="从";
common.label.to="至";

/** 临时公式 start **/
common.button.commonConditions="常用条件";
common.button.saveConditions="保存条件";
common.msg.selectOneCondition="请选择一个常用条件！";
common.msg.onlyOneCondition="只能选择一个常用条件！";
common.msg.selecDeleteCon="请选择要删除的常用条件！";
common.msg.confirmDelCon="确认要删除所选常用条件？";
/** 临时公式 end **/

/** 简单查询 start */
common.label.and="并"
common.label.or="或";
common.label.logic="逻辑符";
common.label.relation="关系符";
common.label.field="查询指标";
common.label.value="查询值";
common.label.like="模糊查询";
common.label.search_result="查询结果";
common.label.history="历史记录查询";
common.label.second="二次查询";
common.label.nextStep="下一步";
common.label.alternativeField="备选指标";
common.label.hasSelectField="已选指标";
common.label.checkAndSave="校验&保存";
common.label.preStep="上一步";
common.msg.checkSuccess="校验成功！";
common.label.expression="因式表达式";
common.label.order="序号";
common.label.itemName="项目名称";
common.msg.selectAddObj="请选择要添加的对象！";
common.msg.selectDelObj="请选择要删除的对象！";
common.msg.selectQueryField="请选择查询指标！";
common.msg.selectDbname="请选择人员库！";
/** 简单查询 end */

/** 定义条件 start */
common.label.conditionStr="条件表达式";
/** 定义条件 end */

/** 报表输出 start */
common.msg.selectSalaryItems="请选择项目名称！";
common.msg.SalaryReportNameNotNull="请填写报表名称!";
/** 报表输出 end */

/** 公式列表为空，调试顺序提示 **/
common.msg.emptyalert="公式列表为空！";
common.button.dataempty="请选择组织架构！";

/** 复杂条件组件 **/
common.label.pattern="格式";
common.label.currentRecord="当前记录";
common.label.firstInMonth="月内最初第一条";
common.label.lastInMonth="月内最近第一条";
common.label.lessFirstInMonth="小于本次月内最初第一条";
common.label.lesslastInMonth="小于本次月内最近第一条";
common.label.sameCountInMonth="同月同次";
common.label.deductedIssued="扣减同月已发金额";
common.label.notCumulative="不累积";
common.label.accumulationInMonth="月内累积";
common.label.accumulatedInQuarter="季度内累积";
common.label.accumulatedYears="年内累积";
common.label.accumulationUnconditional="无条件累积";
common.label.accumulationSCInQuarterr="季度内同次累积";
common.label.accumulationSCInYear="年内同次累积";
common.label.accumulationInSame="同次累积";
common.label.lessAccumulationInMonth="小于本次的月内累积";

common.label.znjy="子女教育";
common.label.jxjy="继续教育";
common.label.zfzj="住房租金";
common.label.zfdk="住房贷款利息";
common.label.sylr="赡养老人";

common.label.selectRecord="请勾选数据后操作！"