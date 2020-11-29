package com.hjsj.hrms.transaction.train.trainexam.exam.mytest;

import com.hjsj.hrms.businessobject.train.trainexam.exam.mytest.MyTestBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:SearchMyTestTrans.java
 * </p>
 * <p>
 * Description:交卷算分
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-11-24 09:02:00
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class SubmitMyTestTrans extends IBusiness{

	public void execute() throws GeneralException {
		
		HashMap map = this.getFormHM();
		ArrayList updateList = new ArrayList();
		// 考试编号
		String paper_id = (String) map.get("paper_id");
		paper_id = PubFunc.decrypt(SafeCode.decode(paper_id));
		// 试卷编号
		String r5300 = (String) map.get("r5300");
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select an.r5200,an.o_answer,r.r5209,an.r5300,r.r5213 from tr_exam_answer an left join r52 r on an.r5200=r.r5200 where an.nbase='");
			sql.append(this.userView.getDbname());
			sql.append("' and an.a0100='"+this.userView.getA0100()+"' and an.exam_type=1 and an.exam_no=");
			sql.append(paper_id);
			sql.append(" and r5300=");
			sql.append(r5300);
			
			this.frowset = dao.search(sql.toString());
			
			while (this.frowset.next()) {
				//  试题编号
				int r5200 = this.frowset.getInt("r5200");
				// 用户答案
				String answer = this.frowset.getString("o_answer");
				answer = answer == null ? "" : answer;
				if (answer.startsWith(",")) {
					answer = answer.substring(1);
				}
				
				// 试题正确答案
				String quest = this.frowset.getString("r5209");
				quest = quest == null ? "" : quest;
				if (quest.startsWith(",")) {
					quest = quest.substring(1);
				}
				
				// 试题分数
				float score = this.frowset.getFloat("r5213");
				
				// 判断答案是否正确
				boolean judge = true;
				
				if (answer.split(",").length == quest.split(",").length) {
					String []qu = quest.split(",");
					for (int i = 0; i < qu.length; i++) {
						if (! answer.contains(qu[i])) {
							judge = false;
							break;
						}
					}
				} else {
					judge = false;
				}
				
				// 保存分数
				RecordVo vo = new RecordVo("tr_exam_answer");
				vo.setString("nbase", this.userView.getDbname());
				vo.setString("a0100", this.userView.getA0100());
				vo.setInt("exam_no", Integer.parseInt(paper_id));
				vo.setInt("exam_type", 1);
				vo.setInt("r5200", r5200);
				vo.setInt("r5300", Integer.parseInt(r5300));
				if(judge) {
					vo.setDouble("score", score);
				} else {
					vo.setDouble("score", 0);
				}
				vo.setString("o_answer", answer);
				
				updateList.add(vo);
				
				
			}
			
			dao.updateValueObject(updateList);
			
			// 更新自测分数
			MyTestBo bo = new MyTestBo(this.frameconn);
			bo.updateScore(this.userView, paper_id,"1",r5300, "", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		
	}

}
