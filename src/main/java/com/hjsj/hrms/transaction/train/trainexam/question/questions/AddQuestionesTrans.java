package com.hjsj.hrms.transaction.train.trainexam.question.questions;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:SearchQuestionesTrans
 * </p>
 * <p>
 * Description:查询符合条件的试题信息
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-10-18
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class AddQuestionesTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		HashMap map = (HashMap) this.getFormHM().get("requestPamaHM");
		// 操作
		String opt = (String) map.get("opt");
		QuestionesBo bo = new QuestionesBo(this.frameconn);
		ArrayList fieldList = DataDictionary.getFieldList("r52", Constant.USED_FIELD_SET);
		// 题型列表
		ArrayList questionTypeList = bo.getQuestionTypeList();
		
		// 难度类表
		ArrayList difficultyList = bo.getDifficultyList();
		
		if ("add".equalsIgnoreCase(opt)) {// 新增
			// 试题分类
			String questionClass = (String) map.get("questionClass");
			if (questionClass == null) {
				questionClass = "";
			}
			
			// 所属单位
			String questionOrg = "";
			String unit = this.userView.getUnitIdByBusi("6");
			String []units = unit.split("`");
			if (units.length > 0 && unit.length() > 0) {
				if (units[0].length() > 0) {
					questionOrg = units[0].substring(2);
				}
			}
			
			this.getFormHM().put("questionClass", questionClass);
			this.getFormHM().put("addQuestionType", "");
			this.getFormHM().put("questionTypeList", questionTypeList);
			this.getFormHM().put("addDifficulty", "");
			this.getFormHM().put("difficultyList", difficultyList);
			this.getFormHM().put("addKnowledge", "");
			this.getFormHM().put("answerTime", "");
			this.getFormHM().put("fraction", "");
			this.getFormHM().put("questionOrg", questionOrg);
			this.getFormHM().put("questionHead", "");
			this.getFormHM().put("selection", "");
			this.getFormHM().put("questionAnswer", "");
			this.getFormHM().put("questionAnalysis", "");
			this.getFormHM().put("questionId", "");
			this.getFormHM().put("addKnowledgeNames", "");
			this.getFormHM().put("addQuestionName", "");
			this.getFormHM().put("liulan", "0");
			
		} else if ("addcontinue".equalsIgnoreCase(opt)){
		
		
			this.getFormHM().put("questionTypeList", questionTypeList);
//			this.getFormHM().put("addDifficulty", "");
			this.getFormHM().put("difficultyList", difficultyList);
//			this.getFormHM().put("addKnowledge", "");
			this.getFormHM().put("answerTime", "");
			this.getFormHM().put("fraction", "");
//			this.getFormHM().put("questionOrg", questionOrg);
			this.getFormHM().put("questionHead", "");
			this.getFormHM().put("selection", "");
			this.getFormHM().put("questionAnswer", "");
			this.getFormHM().put("questionAnalysis", "");
			this.getFormHM().put("questionId", "");
			this.getFormHM().put("addKnowledgeNames", bo.getKnowledgeNamesByIds((String) this.getFormHM().get("addKnowledge")));
			this.getFormHM().put("addQuestionName", "");
			this.getFormHM().put("liulan", "0");
		} else {//  编辑
			String id = (String) map.get("id");
			id = PubFunc.decrypt(SafeCode.decode(id));
			RecordVo vo = bo.getQuestionVoById(id);
			
			// 试题分类
			String questionClass = vo.getString("r5201");
			String addDifficulty = String.valueOf(vo.getInt("r5203"));
			String addQuestionType = String.valueOf(vo.getInt("type_id"));
			String addKnowledge = bo.getKnowledgeIdById(id);
			int answerTime = vo.getInt("r5211");
			double fraction =  vo.getDouble("r5213");
			String questionOrg = vo.getString("b0110");
			String questionHead = vo.getString("r5205");
			String selection = vo.getString("r5207");
			String addQuestionName = vo.getString("r5204");
			String isPublic = vo.getString("r5216");
			isPublic = isPublic == null ? "2" : isPublic;
			addQuestionName = addQuestionName == null ? "" : addQuestionName;
			selection = bo.getStrSelection(selection);
			selection = bo.marksSpecialStr(selection);
			String questionAnswer = "";
				if (bo.getIsObjective(addQuestionType)) {
					questionAnswer = vo.getString("r5208");
					this.getFormHM().put("isObjective", "1");
				} else {
					questionAnswer = vo.getString("r5209");
					this.getFormHM().put("isObjective", "0");
				}
			String questionAnalysis = vo.getString("r5210");
			
			
			this.getFormHM().put("questionId", SafeCode.encode(PubFunc.encrypt(id)));
			this.getFormHM().put("questionClass", questionClass);
			this.getFormHM().put("addQuestionType", addQuestionType);
			this.getFormHM().put("questionTypeList", questionTypeList);
			this.getFormHM().put("addDifficulty", addDifficulty);
			this.getFormHM().put("difficultyList", difficultyList);
			this.getFormHM().put("addKnowledge", addKnowledge);
			this.getFormHM().put("addKnowledgeNames", bo.getKnowledgeNamesByIds(addKnowledge));
			this.getFormHM().put("answerTime", answerTime + "");
			this.getFormHM().put("fraction", fraction + "");
			this.getFormHM().put("questionOrg", questionOrg);
			this.getFormHM().put("questionHead", questionHead);
			this.getFormHM().put("selection", SafeCode.encode(selection));
			this.getFormHM().put("questionAnswer", questionAnswer);
			this.getFormHM().put("questionAnalysis", questionAnalysis);
			this.getFormHM().put("addQuestionName", addQuestionName);
			this.getFormHM().put("isPublic", isPublic);
			
			if ("liulan".equalsIgnoreCase(opt)) {
				this.getFormHM().put("liulan", "1");
			} else {
				this.getFormHM().put("liulan", "0");
			}
			
			
			
		}
		
		/**新增课件控制显示部门 业务用户先判断操作单位 无单位判断管理范围 liwc*/
		String temp="";
//		if(!userView.isSuper_admin()){
//			if(userView.getStatus()==4)
//				temp=this.getUserView().getManagePrivCodeValue();
//			else{
//				String codeall = userView.getUnit_id();
//				if(codeall!=null&&codeall.length()>2)
//					temp=codeall;//.split("`")[0].substring(2);
//				if("".equals(temp))
//					temp=this.getUserView().getManagePrivCodeValue();
//			}
//		}else
//			temp=this.getUserView().getManagePrivCodeValue();
		if(!userView.isSuper_admin()){
			TrainCourseBo tb = new TrainCourseBo(this.userView);
			temp=tb.getUnitIdByBusi();
		}
		this.getFormHM().put("orgparentcode",temp);
		this.getFormHM().put("fieldList", fieldList);
				
	}

}
