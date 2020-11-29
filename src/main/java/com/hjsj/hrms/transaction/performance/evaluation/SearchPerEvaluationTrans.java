package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.achivement.PointCtrlXmlBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.*;

/**
 * <p>Title:SearchPerEvaluationTrans.java</p>
 * <p>Description>:绩效评估主页面</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 05, 2008 09:15:57 AM</p>
 * <p>@author: JinChunhai </p>
 * <p>@version: 1.0</p>
 */

public class SearchPerEvaluationTrans extends IBusiness
{

	public void execute() throws GeneralException
	{
		try
		{
			PerformanceImplementBo pb = new PerformanceImplementBo(this.getFrameconn());
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String startEditScore = (String) hm.get("startEditScore"); // 手动录入结果在点击了打分按钮后才能录入分值
			if (startEditScore == null || !"1".equals(startEditScore))
				startEditScore = "0";
			hm.remove("startEditScore");
			this.getFormHM().put("startEditScore", startEditScore);

			String code = (String) hm.get("code");// 点击机构树传过来的编码
			hm.remove("code");
			String operate = (String) hm.get("operate");
			String noEditResult = (String)hm.get("noEditResult");//是否修改评估结果表
			String objStr = (String) this.getFormHM().get("objStr");
			String objStr_temp = (String) this.getFormHM().get("objStr_temp");
			
			// 1: 得分统计  2:主体票数统计  3:指标票数分统计  4:主体得分统计
			String computeFashion = (String) this.getFormHM().get("computeFashion");
			// 统计方式 <--> 排序方式 add by 刘蒙
			Map computeFashionSQLMap = (HashMap) this.getFormHM().get("computeFashionSQLMap");
			// 当请求是通过点击“排序”按钮发出的时候,tmpStr不为空;如果是“统计方式”下拉框的change事件发出的,tmpStr为空
			Object tmp_order_str = this.getFormHM().get("order_str");
			Object tmpStr = computeFashionSQLMap.get(computeFashion);
			String order_str = (String) (tmp_order_str == null || "".equals(tmp_order_str) ? tmpStr : tmp_order_str);
			if (computeFashion != null && !"".equals(computeFashion)) { // 首次从“考核评估”进入统计界面时，统计方式为“”
				computeFashionSQLMap.put(computeFashion, order_str); // 更新当前统计方式对应的排序方式
			}
			
			if (operate == null || "init00".equals(operate))
			{
				// code=pb.getPrivCode(this.getUserView());
				objStr = "";
				objStr_temp = "";
				order_str = "";
				computeFashionSQLMap.clear();
				this.getFormHM().put("isDispAll", "true");
				this.getFormHM().put("showDetails", "false");
			} else
			{
				if (code == null && "init0".equals(operate))
					code = (String) this.formHM.get("code");
			}

			// 刚进入该模块
			if (operate == null || "init00".equals(operate))
				this.getFormHM().put("pointResult", "1");
			hm.remove("operate");
			String object_type = "";
			String template_id = ""; // 考核摸板id
			String method = "1";
			String byModel = "";//按岗位素质模型
			String planid = (String) this.getFormHM().get("planid");
			
			CheckPrivSafeBo bo = new CheckPrivSafeBo(this.frameconn,this.userView);
			boolean _flag = bo.isHavePriv(this.userView, planid);
			if(!_flag){
				return;
			}
			String busitype = (String) this.getFormHM().get("busitype");  // 业务分类字段 =0(绩效考核); =1(能力素质)
			// ExamPlanBo planbo = new ExamPlanBo(this.frameconn);
			// if(planid.length()>0 && !planbo.isExist(planid))
			// planid="";
			//			
			// if(hm.get("changeScope")!=null&&hm.get("changeScope").equals("1"))
			// {
			// planid="";
			// hm.remove("changeScope");
			// }

			String voteStatis = (String) hm.get("voteStatis");
			hm.remove("voteStatis");
			if (voteStatis != null)
				this.getFormHM().put("isAlert", "1");
			else
				this.getFormHM().put("isAlert", "0");

			ArrayList computeFashionList = new ArrayList();
			if (operate != null && "init00".equals(operate))// 第一次点击该模块 计算方式要显示第一个下拉项
				computeFashion = "";
			String showDetails=(String) this.getFormHM().get("showDetails");
			String evaluationTableHtml = "";
			// String plan_scope=(String)this.getFormHM().get("plan_scope");
			String pointResult = (String) this.getFormHM().get("pointResult");
			// if(plan_scope==null||plan_scope.length()==0)
			// plan_scope="all";
			if (pointResult == null || pointResult.length() == 0)
				pointResult = "1";
			// ArrayList planList=pb.getPlanListByScope(plan_scope); //考核计划列表
			// if((planid==null||planid.length()==0)&&planList.size()>0)
			// {
			// planid=((CommonData)planList.get(0)).getDataValue(); //考核计划
			// }

			String bodyid = (String) this.getFormHM().get("bodyid");
			if (bodyid == null || bodyid.length() == 0 || "init00".equals(operate))
				bodyid = "all";

			String whl = "";
			String gather_type="";//绩效考核打分方式 分机读和手动机读没有考核主体，所以在票数及占比反馈表中统计考核主体个数的时候就无意义了，弃权票也就为0
			if (planid != null && planid.length() > 0)
			{
				RecordVo vo = pb.getPerPlanVo(planid);
				object_type = String.valueOf(vo.getInt("object_type")); // 1部门 2：人员
				template_id = vo.getString("template_id");
				method = vo.getString("method") != null ? vo.getString("method") : "1";
				byModel = vo.getString("bymodel")==null?"":vo.getString("bymodel");
				PerEvaluationBo pe = new PerEvaluationBo(this.getFrameconn(), planid, template_id,this.userView);
					
				pe.setShowDetails(showDetails);
				pe.setObject_type(object_type);
				//评估表结构设置完成后，需要刷新页面，代码执行到这里其实不需要再执行一次表结构设置了。会造成DDL 并行报错！  haosl 2018-8-31
				if(!"true".equalsIgnoreCase(noEditResult)) {
					// 检查per_result_planid表中有没有调整后的表结构的字段，若没有就创建
					pe.editResult(planid);
					// 更新per_result_planid表中调整后的表结构的"子集"字段的值
					pe.updateSubset(planid);				
					// 更新per_result_planid表中调整后的表结构的"引入计划"的字段的值
					pe.updateResultTable(planid);
				}
				// 是否[综合测评表显示测评说明，评估中显示得分及票数统计]
				LoadXml parameter_content = new LoadXml(this.getFrameconn(), planid);
				Hashtable params = parameter_content.getDegreeWhole();
		    	this.getFormHM().put("planParamSet", params);
		    	
		    	
		    	//判断引入的考核计划的"评估结果中显示"负责人"指标"参数是否选中    JinChunhai 2011.02.19		    	
		    	ArrayList planlist = parameter_content.getRelatePlanValue("Plan");
				LazyDynaBean abean=null;
				String importPlanIds = "";
				for(int i=0;i<planlist.size();i++)
				{	
					abean=(LazyDynaBean)planlist.get(i);
					String id=(String)abean.get("id");					
					
					LoadXml loadXml = new LoadXml(this.getFrameconn(), id);
					Hashtable paramters = loadXml.getDegreeWhole();
					String showEvalDirector=(String)paramters.get("ShowEvalDirector");
					
					if("true".equalsIgnoreCase(showEvalDirector))
						importPlanIds+=","+id;
					
				}
				if(importPlanIds!=null && importPlanIds.trim().length()>0)					
					this.getFormHM().put("importPlanIds",importPlanIds.substring(1));
				else
					this.getFormHM().put("importPlanIds",importPlanIds);
				
				String showAppraiseExplain = (String) params.get("showAppraiseExplain");				
				String handEval = (String) params.get("HandEval");
				String handScore = "0";
				if (handEval != null && "TRUE".equalsIgnoreCase(handEval))// 启动录入结果
					handScore = "1";
				else
					handScore = "0";
//				显示计算方式只要满足这两个中的一个
				if (showAppraiseExplain!=null && "false".equalsIgnoreCase(showAppraiseExplain) || "1".equals(handScore))
					this.getFormHM().put("isShowComputFashion", "0");
				else
					this.getFormHM().put("isShowComputFashion", "1");
				
				String showBackTables = (String) params.get("ShowBackTables");
				this.getFormHM().put("showBackTables", showBackTables == null ? "" : showBackTables);
				this.getFormHM().put("isHandScore", pe.getIsHandScore(handScore, template_id));
				this.getFormHM().put("handScore", handScore);
				if(!"true".equalsIgnoreCase(noEditResult)) {
					// 检查per_result_planid表中有没有字段：分组平均分 个人系数 分组最高分、分组最低分、组人数、备注
					pe.testFields(planid);
				}
				computeFashionList = pe.getComputeFashionList();
				if (computeFashion == null || computeFashion.length() == 0)
					computeFashion = ((CommonData) computeFashionList.get(0)).getDataValue();

				if ("3".equals(computeFashion))// 指标票数统计要生成PER_POINTVOTE_XXX表(主体选票统计表)
				{
					pe.refreshPointVoteData(bodyid);

				} else if ("2".equals(computeFashion))// 生成主体选票统计表 PER_BODYVOTE_XXX 数据
				{
					// 填写 对象的主体选票统计表 PER_BODYVOTE_XXX 数据
					pe.insertBodyVoteData();
				}
				String html="";
				if(computeFashion != null&& "6".equalsIgnoreCase(computeFashion.trim()) ){
					
					String objec_id=(String)hm.get("find");// dml 2012年1月19日16:05:05 考核对象 null 默认取第一个 
					String showMethod=(String)hm.get("showmethod");// 展现方式 null 0/1 横向/纵向
					String showabstain=(String)hm.get("showaband");//是否显示弃权票 null 0 /1 不显示/显示
					String showabbenbu=(String)hm.get("showbenbu");//本部null 0 /1 不显示/显示
				}
				// computeFashion="2";
				whl = pb.getPrivWhere(userView);// 根据用户权限先得到一个考核对象的范围
				if (code != null && !"-1".equals(code))
				{
					if (AdminCode.getCodeName("UN", code) != null && AdminCode.getCodeName("UN", code).length() > 0)
						whl += " and b0110 like '" + code + "%'";
					else if (AdminCode.getCodeName("UM", code) != null && AdminCode.getCodeName("UM", code).length() > 0)
						whl += " and e0122 like '" + code + "%'";

				}
				// 这个给绩效面谈传递参数 不包括手工选择和条件查询对考核对象的限制
				this.getFormHM().put("khObjWhere", whl);
				if("init1".equals(operate)&&objStr_temp.length()>0){
					objStr = "";
				}else if("init".equals(operate)&&objStr.length()>0){
					objStr_temp = "";
				}
				if (objStr.length() > 0){
					objStr = PubFunc.keyWord_reback(objStr);
					String _str = objStr.contains("'") ? objStr : PubFunc.decrypt(SafeCode.decode(objStr));
					whl += " and object_id in (" + _str + ") ";
				}
				if(objStr_temp.length()>0 && !"true".equals(formHM.get("isDispAll"))){
					objStr_temp = PubFunc.keyWord_reback(objStr_temp);
					whl += " and object_id in (" + PubFunc.decrypt(SafeCode.decode(objStr_temp)) + ") ";
				}
				// 这个给绩效评估模块内部用 包括所有对考核对象的限制 （登录用户操作单位管理范围之类限制,组织机构树选中某结点的限制，手工选择和条件查询对考核对象的限制）
				this.getFormHM().put("khObjWhere2", whl);
				String deviationScoreUsed=(String) params.get("deviationScoreUsed");//是否使用纠偏总分 0不是  1是
				this.getFormHM().put("deviationScore", deviationScoreUsed);
				boolean flag=pe.isProAppraise();//判断是否定义了“描述性评议项”
				if(flag){
					this.getFormHM().put("proAppraise", "true");
				}else{
					this.getFormHM().put("proAppraise", "false");
				}			
				if (!"6".equals(computeFashion))/////取得 考核评估列表
					evaluationTableHtml = pe.getEvaluationTableHtml(computeFashion, whl, pointResult, order_str, bodyid, handScore, busitype);
				else{
					String objec_id	 =(String)hm.get("find");// dml 2012年1月19日16:05:05 考核对象 null 默认取第一个 
					hm.remove("find");
					String a01001="";
					String rownum ="";
					if(objec_id!=null&&objec_id.trim().length()!=0){
						if("first".equalsIgnoreCase(objec_id)|| "end".equalsIgnoreCase(objec_id)){
							a01001=objec_id;
							rownum="";
						}else{
							a01001=(objec_id==null||objec_id.trim().length()==0)?null:objec_id.split("`")[0];
							rownum =(objec_id==null||objec_id.trim().length()==0)?null:objec_id.split("`")[1];
						}
					}else{
						
					}
					String showMethod  ="";
					String showabstain="";
					String showabbenbu ="";
					String showcomment="";
					showMethod  =(String)hm.get("showmethod")==null?"0":(String)hm.get("showmethod");// 展现方式 null 0/1 横向/纵向
					showabstain =(String)hm.get("showaband")==null?"0":(String)hm.get("showaband");//是否显示弃权票 null 0 /1 不显示/显示
					showabbenbu =(String)hm.get("showbenbu")==null?"1":(String)hm.get("showbenbu");//本部null 0 /1 不显示/显示						
					showcomment="1";
					gather_type=vo.getString("gather_type");
					
					
					hm.remove("showaband");
					hm.remove("showbenbu");
					hm.remove("showmethod");
					pe.setAccountingFlag("Accounting");    //当为票数及占比反馈表时，设置AccountingFlag=accounting
					ArrayList list=pe.getAccountingHtmlForObject(a01001, rownum,showMethod, showabstain, code, showabbenbu,bodyid,gather_type);
					if(list!=null && list.size()>0){
						evaluationTableHtml=(String)list.get(0);
						String a0100=(String)list.get(1);
						String a01002=(String)list.get(2);
						String name=(String)list.get(3);
						String upa0100=(String)list.get(4);
						String nexta0100=(String)list.get(5);
						this.getFormHM().put("object_name", name);
						this.getFormHM().put("upa0100", upa0100);
						this.getFormHM().put("nexta0100", nexta0100);
						this.getFormHM().put("a0100", a01002);
						
					}
					this.getFormHM().put("showbenbu", showabbenbu);
					this.getFormHM().put("showmethod", showMethod);
					this.getFormHM().put("showaband", showabstain);
				}
				this.getFormHM().put("planStatus", String.valueOf(pe.getPlanVo().getInt("status")));
				this.getFormHM().put("feedback", String.valueOf(pe.getPlanVo().getInt("feedback")));
				if (!"6".equals(computeFashion))
					this.getFormHM().put("bodylist", pe.getBodyList());
				else
					this.getFormHM().put("bodylist", pe.getBodyList2());
				String dispUnitScore = "0";// 是否显示统一打分的菜单项
				HashMap tempMap = pe.getTotalScorePointList(template_id);
				Set keySet = tempMap.keySet();
				for (Iterator t = keySet.iterator(); t.hasNext();)
				{
					String key = (String) t.next();
					LazyDynaBean aabean = (LazyDynaBean) tempMap.get(key);
					String Pointctrl = (String) aabean.get("Pointctrl");
					String pointkind = (String) aabean.get("pointkind");
					String Pointtype = (String) aabean.get("Pointtype");
					String status = (String) aabean.get("status");

					if ("1".equals(pointkind) && (status != null && "1".equals(status)) && (Pointtype == null || "0".equals(Pointtype)))
					{
						HashMap map = PointCtrlXmlBo.getAttributeValues(Pointctrl);
						if (map.get("computeRule") == null || map.get("computeRule") != null && "0".equals((String) map.get("computeRule")))
						{
							dispUnitScore = "1";
							break;
						}
					}
				}
				this.getFormHM().put("dispUnitScore", dispUnitScore);

				ArrayList setlist = pe.getDataList();
				this.getFormHM().put("setlist", setlist);

				String jxReportInfo = this.GetPerformanceReportInfo(planid,object_type);
				this.getFormHM().put("jxReportInfo", jxReportInfo);
				this.getFormHM().put("Plan_type", vo.getString("plan_type"));//返回计划类型0:不记名 1:记名 chent 20160115
			} else
			{
				this.getFormHM().put("planStatus", "");
				this.getFormHM().put("bodylist", new ArrayList());
				this.getFormHM().put("setlist", new ArrayList());
				this.getFormHM().put("dispUnitScore", "0");
			}
			//考核对象唯一性指标  JinChunhai 2011.02.19
			String onlyFild = "";
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
			if("2".equalsIgnoreCase(object_type))
			{
				onlyFild = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
//				String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");   //获取唯一性指标前面的checkbox是否选中 0未选中，1选中 
//				if(uniquenessvalid.equals("0"))
//					onlyFild ="";
			}else
			{
				RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.frameconn);
				if(unit_code_field_constant_vo!=null)
				{
					onlyFild=unit_code_field_constant_vo.getString("str_value");	
				}				
			}
			
			this.getFormHM().put("onlyFild",onlyFild);
			
			this.getFormHM().put("method", method);
			this.getFormHM().put("byModel", byModel);
			this.getFormHM().put("order_str", order_str);
			//将排序后的字符串放入服务器中，绩效修正分值导出模板排序
			this.userView.getHm().put("performance_order_str",order_str);
			this.getFormHM().put("objStr", objStr);
			this.getFormHM().put("objStr_temp", objStr_temp);
			this.getFormHM().put("code", code);
			this.getFormHM().put("bodyid", bodyid);
			this.getFormHM().put("evaluationTableHtml", evaluationTableHtml);
			this.getFormHM().put("computeFashion", computeFashion);
			this.getFormHM().put("computeFashionList", computeFashionList);
			this.getFormHM().put("object_type", object_type);
			// this.getFormHM().put("planList",planList);
			this.getFormHM().put("planid", planid);
			this.getFormHM().put("templateid", template_id);
			// this.getFormHM().put("plan_scope",plan_scope);
			this.getFormHM().put("pointResult", pointResult);
			this.getFormHM().put("interViewType", this.getInterViewType());
			
			hm.remove("noEditResult");
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	/** 获得面谈记录的类型* */
	public String getInterViewType()
	{
		AnalysePlanParameterBo abo = new AnalysePlanParameterBo(this.getFrameconn());
		Hashtable ht_table = abo.analyseParameterXml();
		String templet_id = "";
		String flag = "1"; // 1:传统绩效面谈 2：调用模版面谈(操作人：考核主体) 3：调用模版面谈(操作人：考核对象)
		if (ht_table != null)
		{
			if (ht_table.get("interview_template") != null)
				templet_id = (String) ht_table.get("interview_template");
		}
		if (templet_id.length() > 0 && !"-1".equalsIgnoreCase(templet_id))
		{
			flag = "0";// 模板方式
		} else
			flag = "1";// 文字方式
		return flag;
	}

	/**
	 * 绩效报告提示
	 * 
	 * @throws GeneralException
	 */
	public String GetPerformanceReportInfo(String planId,String object_type) throws GeneralException
	{		
		PerEvaluationBo pebo = new PerEvaluationBo(this.getFrameconn(),this.userView);
		
		// 检查per_article表中有没有state;fileflag;description字段，若没有就创建  JinChunhai 2011.02.22
		pebo.editArticle();
				
		StringBuffer info = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		PerformanceImplementBo pb = new PerformanceImplementBo(this.getFrameconn());
		String priWhl = pb.getPrivWhere(userView);// 根据用户权限先得到一个考核对象的范围
		
		String sql = "select count(*) from per_object where plan_id=" + planId+" "+priWhl;

		try
		{
			int sumCount = 0;
			this.frowset = dao.search(sql);
			if (this.frowset.next())
				sumCount = this.frowset.getInt(1);
			info.append("考核对象个数：" + sumCount + "\n");

			int count = 0;
			HashMap map = new HashMap();//考核对象类别是人员 此处保存考核对象 考核对象是非人员 此处保存团队负责人
			sql = "select * from per_article where plan_id=" + planId + "  AND article_type=2 ";
			sql += " and a0100 in (select a0100 from per_article where plan_id=" + planId + "  AND article_type=2 and state in (1,2)) ";
			if("2".equals(object_type))
				sql+=priWhl;

			this.frowset = dao.search(sql);
			while (this.frowset.next())
			{
				String context = Sql_switcher.readMemo(this.frowset, "content");
				int fileflag = this.frowset.getInt("fileflag"); // =1(文本) =2(附件)
				if (!(fileflag == 1 && context.trim().length() == 0))// 空文本不算在报告数中
				{
					map.put(this.frowset.getString("a0100"), "");
					count++;
				}
			}

			if (count == 0)
				return "";
			
			if("2".equals(object_type))
			{
				info.append("提交报告考核对象个数：" + map.size() + "\n");
				info.append("提交报告总数：" + count + "\n");
				info.append("未提交报告考核对象个数：" + (sumCount - map.size()) + "  ");

				sql = "select * from per_object where plan_id=" + planId +" "+priWhl +" order by b0110,e0122";
				this.frowset = dao.search(sql);
				String tempE0122 = "";
				int n = 0;
				while (this.frowset.next())
				{
					String b0110 = this.frowset.getString("b0110") == null ? "" : this.frowset.getString("b0110");
					String e0122 = this.frowset.getString("e0122") == null ? "" : this.frowset.getString("e0122");
					String a0101 = this.frowset.getString("a0101") == null ? "" : this.frowset.getString("a0101");
					String a0100 = this.frowset.getString("object_id") == null ? "" : this.frowset.getString("object_id");

					b0110 = AdminCode.getCode("UN", b0110) != null ? AdminCode.getCode("UN", b0110).getCodename() : "";
					e0122 = AdminCode.getCode("UM", e0122) != null ? AdminCode.getCode("UM", e0122).getCodename() : "";
					if (map.get(a0100) == null)
					{
						if (!e0122.equalsIgnoreCase(tempE0122))
						{
							tempE0122 = e0122;
							info.setLength(info.length() - 1);
							if (n > 0 && n <= 10)
								info.append("共" + n + "人");
							else if (n > 0)
								info.append("等" + n + "人");

							info.append("\n " + b0110 + " " + e0122 + ": \n   ");
							n = 0;
						}

						info.append(a0101 + "、");
						n++;
					}
				}
				info.setLength(info.length() - 1);
				if (n > 0 && n <= 10)
					info.append("共" + n + "人");
				else if (n > 0)
					info.append("等" + n + "人");
			}else
			{
				HashMap teamLeaderMap = new HashMap();
				sql = "select object_id,mainbody_id from per_mainbody where body_id=-1 and plan_id=" + planId ;
				this.frowset = dao.search(sql);
				while (this.frowset.next())				
					teamLeaderMap.put(this.frowset.getString("object_id"), this.frowset.getString("mainbody_id"));
								
				int submitObjCount = 0;
				int noSubmitObjCount = 0;
				StringBuffer noSubmitBuf = new StringBuffer();			

				sql = "select * from per_object where plan_id=" + planId +" "+priWhl +" order by b0110,e0122";
				this.frowset = dao.search(sql);		
				while (this.frowset.next())
				{
					String b0110 = this.frowset.getString("b0110") == null ? "" : this.frowset.getString("b0110");
					String e0122 = this.frowset.getString("e0122") == null ? "" : this.frowset.getString("e0122");
					String a0101 = this.frowset.getString("a0101") == null ? "" : this.frowset.getString("a0101");					
					b0110 = AdminCode.getCode("UN", b0110) != null ? AdminCode.getCode("UN", b0110).getCodename() : "";
					e0122 = AdminCode.getCode("UM", e0122) != null ? AdminCode.getCode("UM", e0122).getCodename() : "";
					String object_id = this.frowset.getString("object_id") == null ? "" : this.frowset.getString("object_id");
									
					String mainbody_id = teamLeaderMap.get(object_id) == null ? "" : (String)teamLeaderMap.get(object_id);
					if("".equals(mainbody_id))//没有设置团队负责人
					{
						if(AdminCode.getCode("UM", object_id) != null)
							noSubmitBuf.append("\n " + b0110 + "-" + e0122);
						else if(AdminCode.getCode("UN", object_id) != null)
							noSubmitBuf.append("\n " + b0110 );										
						noSubmitObjCount++;						
					}
					else //设置了团队负责人
					{
						if (map.get(mainbody_id) == null)//团队负责人没有提交绩效报告
						{
							if(AdminCode.getCode("UM", object_id) != null)
								noSubmitBuf.append("\n " + b0110 + "-" + e0122);
							else if(AdminCode.getCode("UN", object_id) != null)
								noSubmitBuf.append("\n " + b0110 );										
							noSubmitObjCount++;
						}else
							submitObjCount++;
					}				
				}
			
				info.append("提交报告考核对象个数：" + submitObjCount + "\n");
				info.append("提交报告总数：" + count + "\n");
				info.append("未提交报告考核对象个数：" + noSubmitObjCount + "  "+noSubmitBuf);
				
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return info.toString();
		
	}
	
}
