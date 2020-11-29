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
import java.util.Iterator;
import java.util.Map;

/**
 * <p>
 * Title:UpdateScoreTrans.java
 * </p>
 * <p>
 * Description:更新分数
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
public class UpdateScoreTrans extends IBusiness{

	public void execute() throws GeneralException {
		
		HashMap map = this.getFormHM();
		ArrayList updateList = new ArrayList();
		// 考试编号
		String paper_id = (String) map.get("paper_id");
		paper_id = PubFunc.decrypt(SafeCode.decode(paper_id));
		// 试卷编号
		String r5300 = (String) map.get("r5300");
		r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
		// flag
		String flag = (String) map.get("flag");
		//  人员编号
		String a0100 = (String) map.get("a0100");
		a0100 = PubFunc.decrypt(SafeCode.decode(a0100));
		// 人员库
		String nbase = (String) map.get("nbase");
		nbase = PubFunc.decrypt(SafeCode.decode(nbase));
		// 是保存还是提交，1为提交，0或null为保存
		String isSubmit = (String) map.get("issubmit");
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		String biaozhi = "ok";
		try {
			
			ArrayList inserList = new ArrayList();
			Iterator it = map.entrySet().iterator();
			while (it.hasNext()){
				Map.Entry entry = (Map.Entry) it.next();
				String key = (String) entry.getKey();
				if (key.startsWith("score_")) {
					RecordVo vo = new RecordVo("tr_exam_answer");
					ArrayList list = new ArrayList();
					String value = entry.getValue().toString();
					list.add(Float.valueOf(value));
					if ("7".equals(flag)) {
						list.add(a0100);
						vo.setString("a0100", a0100);
						list.add(nbase);
						vo.setString("nbase", nbase);
					} else {
						list.add(this.userView.getA0100());
						vo.setString("a0100", this.userView.getA0100());
						list.add(this.userView.getDbname());
						vo.setString("nbase", this.userView.getDbname());
					}
					list.add(Integer.valueOf(paper_id));
					vo.setInt("exam_no", Integer.parseInt(paper_id));
					if ("7".equals(flag)) {
						list.add(Integer.valueOf(2));
						vo.setInt("exam_type", 2);
					} else {
						list.add(Integer.valueOf(1));
						vo.setInt("exam_type", 1);
					}
					list.add(Integer.valueOf(key.replace("score_", "")));
					vo.setInt("r5200", Integer.parseInt(key.replace("score_", "")));
					vo.setInt("r5300", Integer.parseInt(r5300));
					if (! dao.isExistRecordVo(vo)) {
						inserList.add(vo);
					}
					
					updateList.add(list);
				}
			}
			dao.addValueObject(inserList);
			String update = "update tr_exam_answer set score=? where a0100=? and nbase=? and exam_no=? and exam_type=? and r5200=?";
			
			dao.batchUpdate(update, updateList);
			
			if ("7".equals(flag)) {
				StringBuffer sql = new StringBuffer();
				sql.append("update r55 set r5501=");
				sql.append("(select sum(score) from tr_exam_answer a left join");
				sql.append(" (select r5200,r5300,ques_type from tr_exam_paper c left join tr_question_type d on c.type_id=d.type_id) p on a.r5200=p.r5200 and ");
				sql.append("a.r5300=p.r5300 where a.a0100='");
				sql.append(a0100);
				sql.append("' and a.nbase='");
				sql.append(nbase);
				sql.append("' and a.exam_no=");
				sql.append(paper_id);
				sql.append(" and a.exam_type=2 and p.ques_type=1),");
				sql.append("r5503=");
				sql.append("(select sum(score) from tr_exam_answer a left join");
				sql.append(" (select r5200,r5300,ques_type from tr_exam_paper c left join tr_question_type d on c.type_id=d.type_id) p on a.r5200=p.r5200 and ");
				sql.append("a.r5300=p.r5300 where a.a0100='");
				sql.append(a0100);
				sql.append("' and a.nbase='");
				sql.append(nbase);
				sql.append("' and a.exam_no=");
				sql.append(paper_id);
				sql.append(" and a.exam_type=2 and p.ques_type=2),");				
				sql.append("r5504=");
				sql.append("(select sum(score) from tr_exam_answer a left join");
				sql.append(" (select r5200,r5300,ques_type from tr_exam_paper c left join tr_question_type d on c.type_id=d.type_id) p on a.r5200=p.r5200 and ");
				sql.append("a.r5300=p.r5300 where a.a0100='");
				sql.append(a0100);
				sql.append("' and a.nbase='");
				sql.append(nbase);
				sql.append("' and a.exam_no=");
				sql.append(paper_id);
				sql.append(" ),");
				if ("1".equals(isSubmit)) {
					this.frowset = dao.search("select r5411 from r54 where r5400="+paper_id);
					if(this.frowset.next()&&"04".equals(this.frowset.getString("r5411")))//阅卷已发布状态的计划 该人员状态直接变为 发布
						sql.append("r5515=2,r5517='");
					else
						sql.append("r5515=1,r5517='");
				} else {
					sql.append("r5515=0,r5517='");
				}
				String name = this.userView.getUserFullName();
				if (name == null || name.length() <= 0) {
					sql.append(this.userView.getUserName());
				} else {
					sql.append(name);
				}
				sql.append("' where a0100='");
				sql.append(a0100);
				sql.append("' and nbase='");
				sql.append(nbase);
				sql.append("' and r5400=");
				sql.append(paper_id);
				
				dao.update(sql.toString());
			} else if ("2".equals(flag)){
				// 更新考试分数
				MyTestBo bo = new MyTestBo(this.frameconn);
				bo.updateTestScore(this.userView, paper_id,"2");
			} else {
				// 更新自测分数
				MyTestBo bo = new MyTestBo(this.frameconn);
				bo.updateScore(this.userView, paper_id,"1",r5300,"", "");
			}
		} catch (Exception e) {
			biaozhi = "no";
			e.printStackTrace();
		}
	
		this.getFormHM().put("biaozhi", biaozhi);
		this.getFormHM().put("paper_id", SafeCode.encode(PubFunc.encrypt(paper_id)));
		
	}

}
