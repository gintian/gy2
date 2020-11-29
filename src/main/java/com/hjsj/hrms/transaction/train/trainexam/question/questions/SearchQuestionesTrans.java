package com.hjsj.hrms.transaction.train.trainexam.question.questions;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

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
public class SearchQuestionesTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		HashMap map = (HashMap) this.getFormHM().get("requestPamaHM");
		// 试题分类
		String code = (String) map.get("a_code");
		code = PubFunc.decrypt(SafeCode.decode(code));
		// 知识点
		String knowledge = (String) this.getFormHM().get("knowledge");
		
		// 题型
		String questionType = (String) this.getFormHM().get("questionType");
		
		// 难度
		String difficulty = (String) this.getFormHM().get("difficulty");
		
		QuestionesBo bo = new QuestionesBo(this.frameconn);
		
		if (code == null) {
			code = "";
		}
		
		// 题型列表
		ArrayList questionTypeList = bo.getQuestionTypeList();
		// questionType默认为空
		if (questionType == null || questionType.length() <= 0) {
//			if (questionTypeList.size() > 0) {
//				CommonData data = (CommonData) questionTypeList.get(0);
//				questionType = data.getDataValue();
//			}
			questionType = "";
		}
		
		// 难度类表
		ArrayList difficultyList = bo.getDifficultyList();
		// difficulty默认为第一项的值
		if (difficulty == null || difficulty.length() <= 0) {
//			if (difficultyList.size() > 0) {
//				CommonData data = (CommonData) difficultyList.get(0);
//				difficulty = data.getDataValue();
//			}
			difficulty = "";
		}
		
		StringBuffer priv = new StringBuffer();
		StringBuffer head = new StringBuffer();
		// 权限过滤
		if (!this.userView.isSuper_admin()) {
			TrainCourseBo to = new TrainCourseBo(this.userView);
			String unit = to.getUnitIdByBusi();//this.userView.getUnitIdByBusi("6");
			if(unit.indexOf("UN`")==-1){
				String []units = unit.split("`");
				if (units.length > 0 && unit.length() > 0) {
					head.append(" case when (");
					priv.append(" and (");
					for (int i = 0; i < units.length; i++) {
						if (i != 0) {
							priv.append(" or ");
							head.append(" or ");
						} 
						String b0110s = units[i].substring(2);
						priv.append("b0110=" + Sql_switcher.substr("'"+b0110s+"'", "1", Sql_switcher.length("b0110")));
						priv.append(" or b0110 like '");
						priv.append(b0110s);
						priv.append("%'");
						head.append("b0110 like '");
						head.append(b0110s);
						head.append("%'");
					}
					
					head.append(") then '1' else '0' end flag");
					
					priv.append(" or "+Sql_switcher.isnull("b0110", "'-1'"));
					priv.append("='-1'");
					if (Sql_switcher.searchDbServer() == 1) {
						priv.append(" or b0110=''");
					}
					priv.append(" or a.r5216=1)");
				} else {
					priv.append(" and (");
					priv.append(Sql_switcher.isnull("b0110", "'-1'"));
					priv.append("='-1'");
					if (Sql_switcher.searchDbServer() == 1) {
						priv.append(" or b0110=''");
					}
					priv.append(" or a.r5216=1)");
					
					
					head.append(" case when (");
					head.append(Sql_switcher.isnull("b0110", "'-1'"));
					head.append("='-1'");
					if (Sql_switcher.searchDbServer() == 1) {
						head.append(" or b0110=''");
					}
					head.append(") then '1' else '0' end flag ");
					
				}
			}else
				head.append(" '1' flag ");
		} else {
			head.append(" '1' flag ");
		}
		
		
		// 查询试题的sql语句
		String strsql = "select * ";
		// 查询条件
		StringBuffer strwhere = new StringBuffer();
		strwhere.append("from (select r5200,a.type_id,Type_name,R5203,R5204,R5205,R5213,flag,norders ");
		strwhere.append("from (");
		strwhere.append("select r5200,R5201,Type_id,R5204,R5205,R5213,R5203,r5216,norder norders,b0110,");
		strwhere.append(head.toString());
		
		strwhere.append(" from R52 r ");
		strwhere.append(") a left join tr_question_type b on a.type_id = b.type_id where R5201 like '");
		strwhere.append(code);
		strwhere.append("%' ");
		
		if (difficulty != null && difficulty.length() > 0) {
			strwhere.append(" and r5203=");
			strwhere.append(difficulty);
		}
		
		if (questionType != null && questionType.length() > 0) {
			strwhere.append(" and a.Type_id=");
			strwhere.append(questionType);
		}
//		strwhere.append(" and R5201 like '");
//		strwhere.append(code);
//		strwhere.append("%' ");
		
		strwhere.append(priv.toString());
		
		// 知识点条件过滤
		if (knowledge != null && knowledge.length() > 0) {
			strwhere.append(" and r5200 in (");
//			strwhere.append("select r5200 from tr_test_knowledge m left join (");
//			strwhere.append("select codeitemid,codeitemdesc from codeitem where codesetid='68') n");
			//strwhere.append(" on m.know_id = n.codeitemid where codeitemdesc like '%");
			//strwhere.append(knowledge);
			//strwhere.append("%')");
			
//			strwhere.append(" on m.know_id = n.codeitemid where codeitemid ='");
//			strwhere.append(knowledge);
//			strwhere.append("')");
			strwhere.append("select r5200 from tr_test_knowledge where ");
			String []knowid = knowledge.split(",");
			for (int i = 0; i < knowid.length; i++) {
				if(i>0)
					strwhere.append(" or ");
				strwhere.append("know_id like '"+knowid[i]+"%'");
			}
			strwhere.append(")");
			
		}
		strwhere.append(") st where 1=1 ");
		// 查询的列
		String columns = "r5200,Type_name,R5203,R5204,R5205,R5213,flag";
		// 顺序
		String order = " order by type_id,norders,r5200";
		
		// 添加全部
		CommonData data = new CommonData("", "全部");
		questionTypeList.add(0, data);
		difficultyList.add(0, data);
		
		this.getFormHM().put("strsql", strsql);
		this.getFormHM().put("strwhere", strwhere.toString());
		this.getFormHM().put("columns", columns);
		this.getFormHM().put("order", order);
		this.getFormHM().put("knowledge", knowledge);
		this.getFormHM().put("questionType", questionType);
		this.getFormHM().put("difficulty", difficulty);
		this.getFormHM().put("questionTypeList", questionTypeList);
		this.getFormHM().put("difficultyList", difficultyList);
		this.getFormHM().put("aCode", code);
		
		
	}

}
