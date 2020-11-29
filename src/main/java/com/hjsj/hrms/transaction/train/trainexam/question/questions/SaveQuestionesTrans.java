package com.hjsj.hrms.transaction.train.trainexam.question.questions;

import com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Date;

/**
 * <p>
 * Title:SearchQuestionesTrans
 * </p>
 * <p>
 * Description:保存试题信息
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
public class SaveQuestionesTrans extends IBusiness {

	public void execute() throws GeneralException {//知识点、试题分类、试题类型、难度、分数为必填项。
		// 知识点
		String addKnowledge = (String) this.getFormHM().get("addKnowledge");
		// 难度
		String addDifficulty = (String) this.getFormHM().get("addDifficulty");
		// 题型
		String addQuestionType = (String) this.getFormHM().get("addQuestionType");
		// 答题时间
		String answerTime = (String) this.getFormHM().get("answerTime");
		if (answerTime == null || answerTime.length() <= 0) {
			answerTime = "0";
		}
		// 分类
		String questionClass = (String) this.getFormHM().get("questionClass");
		
		
		// 分数
		String fraction = (String) this.getFormHM().get("fraction");
		// 所属单位
		String questionOrg = (String) this.getFormHM().get("questionOrg");
		// 试题内容
		String questionHead = (String) this.getFormHM().get("questionHead");
		questionHead = PubFunc.keyWord_reback(questionHead);
		// 试题选项
		String selection = (String) this.getFormHM().get("selection");
		selection = PubFunc.keyWord_reback(selection);
		// 试题答案
		String questionAnswer = (String) this.getFormHM().get("questionAnswer");
		questionAnswer = PubFunc.keyWord_reback(questionAnswer);
		// 试题分析
		String questionAnalysis = (String) this.getFormHM().get("questionAnalysis");
		questionAnalysis = PubFunc.keyWord_reback(questionAnalysis);
		// 是否公开，1为公开，2为不公开
		String isPublic = (String) this.getFormHM().get("isPublic");
		// 试题id，如果不为空则为编辑，为空为新增
		String questionId = (String) this.getFormHM().get("questionId");
		questionId = PubFunc.decrypt(SafeCode.decode(questionId));
		// 试题名称
		String addQuestionName = (String) this.getFormHM().get("addQuestionName");
		
		String id = "";
		QuestionesBo bo = new QuestionesBo(this.frameconn);
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			RecordVo vo = new RecordVo("r52");
			if (questionId == null || questionId.length() <= 0) {
				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
				id = idg.getId("R52.R5200");
				vo.setInt("r5200", Integer.parseInt(id));
				// R5214	使用次数	Int
				vo.setInt("r5214", 0);
			} else {
				id = questionId;
				vo.setInt("r5200", Integer.parseInt(questionId));
				vo = dao.findByPrimaryKey(vo);
			}
			vo.setString("r5201", questionClass);
			vo.setInt("r5203", Integer.parseInt(addDifficulty));
			// R5205	试题内容	Text
			vo.setString("r5205", questionHead);
			// 试题选项
//			<?xml version="1.0" encoding="GB2312"?>
//			<Params>
//				<item id="A"><![CDATA[
//				   试题选项内容…
//				]]></item>
//				<item id="B">
//				   试题选项内容…
//				</item>
//			</Params>
			String []sels = selection.split("`~&~`");
			StringBuffer sel = new StringBuffer();
			sel.append("<?xml version=\"1.0\" encoding=\"GB2312\"?><Params>");
			for (int i = 0; i < sels.length; i++) {
				String []str = sels[i].split("`:`");
				if (str[0].length() > 0) {
					sel.append("<item id=\"");
					sel.append(str[0]);
					sel.append("\"><![CDATA[");
					if (str.length > 1) {
						sel.append(str[1]);
					}
					sel.append("]]></item>");
				}
			}
			sel.append("</Params>");
			// R5207	试题选项	Text
			vo.setString("r5207", sel.toString());
			
			if (bo.getIsObjective(addQuestionType)) {
				//R5208	主观题答案	Text
				vo.setString("r5208", questionAnswer);	
			} else {
				// R5209	客观题答案	Text
				vo.setString("r5209", questionAnswer);
			}
			
			// R5210	试题解析	Text
			vo.setString("r5210", questionAnalysis);
			// r5204 试题名称
			vo.setString("r5204", addQuestionName);
			vo.setInt("r5211", Integer.parseInt(answerTime));
			//R5213	考试得分	Float
			//vo.setInt("r5213", Integer.parseInt(fraction));
			vo.setDouble("r5213", Double.parseDouble(fraction));
			//R5216	是否公开	Varchar(1)
			if ("1".equals(isPublic)) {
				vo.setString("r5216", "1");
			} else {
				vo.setString("r5216", "2");
			}
			//B0110	所属单位	Varchar(30)
			vo.setString("b0110", questionOrg);
			
			if (questionId == null || questionId.length() <= 0) {
				// R5217	审批状态	Varchar(2)
				vo.setString("r5217", "01");
				// norder	顺序号	Int
				vo.setInt("norder", bo.getNorder("r52"));
				// Create_user	创建人	Varchar(50)
				vo.setString("create_user", this.userView.getUserName());
				// create_time	创建时间	Datetime
				vo.setDate("create_time", new Date());
			}
			
			//Type_id	题型编号	Int
			vo.setInt("type_id", Integer.parseInt(addQuestionType));
			
			
			if (questionId == null || questionId.length() <= 0) { 
				dao.addValueObject(vo);
			} else {
				dao.updateValueObject(vo);
			}
			
			// 更新知识点试题关系表 
			String sql = "delete from tr_test_knowledge where r5200=" + id;
			dao.update(sql);
			String []knows = addKnowledge.split(",");
			for (int i = 0; i < knows.length; i++) {
				if (knows[i] != null && knows[i].length() > 0) {
					RecordVo v = new RecordVo("tr_test_knowledge");
					v.setInt("r5200", Integer.parseInt(id));
					v.setString("know_id", knows[i]);
					dao.addValueObject(v);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
