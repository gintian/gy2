package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveEvaluate;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *<p>Title:参与评分却只有确认权限的主体进行“同意”操作</p> 
 *<p>Description:将当前操作人置为提交状态：2，意见写入reasons字段</p> 
 *<p>Company:HJSJ</p> 
 *<p>Create time:2014-4-18:下午20:05:28</p> 
 * @author 刘蒙
 *@version 1.0
 */
public class SaveAgreeOpinionTrans extends IBusiness {

	public void execute() throws GeneralException {
		Map hm = (HashMap) this.getFormHM();
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		try {
			String object_id = (String) hm.get("object_id");
			String whole_grade_id = (String) hm.get("whole_id"); // 录入等级
			String whole_score = (String) hm.get("wholeEvalScoreId"); // 录入分值
			int plan_id = Integer.parseInt((String) hm.get("plan_id"));
			String opinion = (String) hm.get("opinion"); // 同意意见
			opinion = SafeCode.decode(opinion); // 解码
			String mainbody_id = this.userView.getA0100();
	
			// ************************************** 更新per_mainbody表开始 **************************************
			StringBuffer pmbSql = new StringBuffer("SELECT id FROM per_mainbody");
			pmbSql.append(" WHERE plan_id=?");
			pmbSql.append(" AND object_id=?");
			pmbSql.append(" AND mainbody_id=?");
			int pmb_pk = 0;
			rs = dao.search(
					pmbSql.toString(),
					Arrays.asList(new Object[] { new Integer(plan_id), object_id, mainbody_id }));
			if (rs.next()) {
				pmb_pk = rs.getInt("id");
			}
			RecordVo pmb_vo = new RecordVo("per_mainbody");
			pmb_vo.setInt("id", pmb_pk);
			pmb_vo = dao.findByPrimaryKey(pmb_vo);
			pmb_vo.setInt("status", 2);
			pmb_vo.setString("reasons", opinion);
			if (whole_grade_id != null) {
				pmb_vo.setString("whole_grade_id", whole_grade_id);
			}
			if (whole_score != null) {
				pmb_vo.setString("whole_score", whole_score);
			}
			dao.updateValueObject(pmb_vo);
			// ************************************** 更新per_mainbody表结束 **************************************
			
			// ###################################### 更新per_object开始 ######################################
			StringBuffer poSql = new StringBuffer("SELECT id,score_process FROM per_object");
			poSql.append(" WHERE plan_id=?");
			poSql.append(" AND object_id=?");
			int po_pk = 0;
			String score_process = "";
			rs = dao.search(poSql.toString(),
					Arrays.asList(new Object[] { new Integer(plan_id), object_id }));
			if (rs.next()) {
				po_pk = rs.getInt("id");
				score_process = Sql_switcher.readMemo(rs, "score_process");
			}
			hm.put("score_process", score_process);
			
			RecordVo po_vo = new RecordVo("per_object");
			po_vo.setInt("id", po_pk);
			po_vo = dao.findByPrimaryKey(po_vo);
			po_vo.setString("score_process", editScoreProcessXML(hm));
			dao.updateValueObject(po_vo);
			// ###################################### 更新per_object结束 ######################################
			ObjectCardBo _bo=new ObjectCardBo(this.getFrameconn(),plan_id+"",object_id,this.getUserView());
			String body_id="";
			this.frowset=dao.search("select distinct body_id from per_mainbody where plan_id="+plan_id+""+" and  mainbody_id='"+mainbody_id+"'");
			while(this.frowset.next()){
				body_id = this.frowset.getString("body_id");
			}
			RecordVo planVo=new RecordVo("per_plan");
			planVo.setInt("plan_id",Integer.parseInt(plan_id+"")); 
			planVo=dao.findByPrimaryKey(planVo);
			//发待办   start  zhaoxg add 2014-9-4
			/** 先判断是否可以置为已办*/
			PendingTask pt = new PendingTask();
			LazyDynaBean be = new LazyDynaBean();
			String NoApproveTargetCanScore=_bo.getNoApproveTargetCanScore();
			String temp_sql="";
			if("False".equalsIgnoreCase(NoApproveTargetCanScore)){
				temp_sql="select pm.* from per_mainbody pm,per_object po where pm.object_id=po.object_id and po.plan_id='"+plan_id+""+"' and pm.plan_id='"+plan_id+""+"' and pm.mainbody_id='"+this.userView.getA0100()+"' and po.sp_flag='03'  and "+Sql_switcher.isnull("status", "0")+"<>2";
			}else{
				temp_sql="select * from per_mainbody  where plan_id='"+plan_id+""+"' and "+Sql_switcher.isnull("status", "0")+"<>2 and mainbody_id='"+this.userView.getA0100()+"'";
			}	
			be.set("oper", "start");
			be.set("sql", temp_sql);
			LazyDynaBean temp_bean=PerformanceImplementBo.updatePendingTask(this.frameconn, this.userView,"Usr"+this.userView.getA0100(),plan_id+"",be,"2");
			if("update".equals(temp_bean.get("selfflag"))){
				pt.updatePending("P", "P"+temp_bean.get("selfpending_id"), 1, "计划打分", this.userView);
			}
			
					
			LoadXml parameter_content = null;
			if (BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"") == null) {
				parameter_content = new LoadXml(this.frameconn,plan_id+"");
				BatchGradeBo.getPlanLoadXmlMap().put(plan_id+"",parameter_content);
			} else {
				parameter_content = (LoadXml) BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"");
			}
			Hashtable params = parameter_content.getDegreeWhole();
			String GradeByBodySeq = "false";// 按考核主体顺序号控制评分流程(True,
											// False默认为False)
			if (params.get("GradeByBodySeq") != null)
				GradeByBodySeq = (String) params.get("GradeByBodySeq");
			ArrayList list = null;
			StringBuffer buf = new StringBuffer("");
			if ("true".equalsIgnoreCase(GradeByBodySeq)) {
				list = _bo.getUpLevelInfo(plan_id+"",object_id, buf);
			} else {
				list = _bo.getUpLevelInfo(plan_id+"", object_id,body_id);
				if (list.size() == 0 && !"-2".equals(body_id)) {
					String followBodyId = _bo.getfollowBodyid(body_id);
					int currentLevel = _bo.getCurrentLevel(followBodyId);
					// 目标卡制订支持几级审批
					String targetMakeSeries = (String) params.get("targetMakeSeries");

					for (int t = currentLevel + 1; t <= Integer.parseInt(targetMakeSeries); t++) {
						ArrayList appealObjectList = _bo.getUpLevelInfo(plan_id+"",object_id, followBodyId);
						if (appealObjectList.size() > 0) {
							list = appealObjectList;
							break;
						}
						followBodyId = _bo.getfollowBodyid(followBodyId);
					}
				}
			}
			LazyDynaBean abean = null;
			for (int j = 0; j < list.size(); j++) {
				abean = (LazyDynaBean) list.get(j);
				String appealObject_id = (String) abean.get("appealObject_id");
				
				/**评分给下个审批人推送待办  zhaoxg add 2014-9-1 */
				LazyDynaBean bean = new LazyDynaBean();
				String _title=planVo.getString("name")+"_(评分)";
				String _sql="";
				if("False".equalsIgnoreCase(NoApproveTargetCanScore)){
					_sql="select pm.* from per_mainbody pm,per_object po where pm.object_id=po.object_id and po.plan_id='"+plan_id+""+"' and pm.plan_id='"+plan_id+""+"' and pm.mainbody_id='"+this.userView.getA0100()+"' and po.sp_flag='03'  and "+Sql_switcher.isnull("status", "0")+"<>2";
				}else{
					_sql="select * from per_mainbody  where plan_id='"+plan_id+""+"' and "+Sql_switcher.isnull("status", "0")+"<>2 and mainbody_id='"+this.userView.getA0100()+"'";
				}	
				String href="";
                if ("2".equals(planVo.getString("object_type")))
                	href = "/performance/objectiveManage/objectiveEvaluate/objective_evaluate_list.do?b_init=init&plan_id=" + plan_id+"" + "&returnflag=8&opt=1&entranceType=0&isSort=0";
                else
                	href = "/performance/objectiveManage/orgPerformance/org_performance_list.do?b_init=init&plan_id=" + plan_id+"" + "&returnflag=8&opt=1";
				bean.set("oper", "start");
				bean.set("title", _title);
				bean.set("url", href);
				bean.set("sql", _sql);
				LazyDynaBean _bean=PerformanceImplementBo.updatePendingTask(this.frameconn, this.userView,"Usr"+appealObject_id,plan_id+"",bean,"1");
				if("add".equals(_bean.get("flag"))){
					pt.insertPending("PER"+_bean.get("pending_id"),"P",_title,this.userView.getDbname()+this.userView.getA0100(),"Usr"+appealObject_id,href,0,1,"计划打分",this.userView);	
				}	
				if("update".equals(_bean.get("selfflag"))){
					pt.updatePending("P", "P"+_bean.get("selfpending_id"), 1, "计划打分", this.userView);
				}
				/*String corpid = (String) ConstantParamter.getAttribute("wx","corpid");  
				if(corpid!=null&&corpid.length()>0){//推送微信公众号  zhaoxg add 2015-5-5
					WeiXinBo.sendMsgToPerson("Usr", appealObject_id, _title, "", "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png", "");
				}*/
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeneralException(e.getMessage());
		} finally {
			PubFunc.closeResource(rs);
		}
	}
	
	/**
	 * per_object.score_process字段
	 * @param hm 包含用到的值，目前用到：object_id,opinion,score_process
	 * @return xml格式的文本
	 * @throws Exception 可能的错误包括：xml解析错误
	 */
	public String editScoreProcessXML(Map hm) throws Exception {
		String object_id = (String) hm.get("object_id");
		String opinion = (String) hm.get("opinion");
		opinion = SafeCode.decode(opinion); // 解码
		String score_process = (String) hm.get("score_process");
		
		Document doc = null;
		Element root = null;
		if (score_process != null && score_process.trim().length() > 0) {
			// 读取文档，如果存在的话
			StringReader reader = new StringReader(score_process);
			doc = PubFunc.generateDom(score_process);
			root = doc.getRootElement();
		} else {
			// 创建xml文档
			root = new Element("root");
			doc = new Document(root);
		}
		
		// record节点
		Element record = new Element("record");
		root.addContent(record);
		// opt_object属性
		record.setAttribute("opt_object", object_id);
		// name属性
		record.setAttribute("name", AdminCode.getCodeName("UN",userView.getUserOrgId()) + "/" // 单位
				+ AdminCode.getCodeName("UM",userView.getUserDeptId()) + "/" // 部门
				+ AdminCode.getCodeName("@K",userView.getUserPosId()) + "/" // 岗位
				+ userView.getUserFullName()); // 用户名
		// date属性Admincode
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		record.setAttribute("date", sdf.format(new Date()));
		// status属性
		record.setAttribute("status", "2");
		// status_desc属性
		record.setAttribute("status_desc", "同意");
		// reason属性
		record.setAttribute("reason", opinion);
		// report_to属性
		record.setAttribute("report_to", "");

		// 格式化输出
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		
		hm.remove("score_process"); // 移除掉该值，1、界面用不到，2、包含特殊字符，页面会报错
		
		return outputter.outputString(doc);
	}

}
