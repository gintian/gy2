package com.hjsj.hrms.transaction.train.trainexam.exam.mytest;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:SearchMyTestTrans.java
 * </p>
 * <p>
 * Description:查询是否答案全部写完
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-11-24 11:28:00
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class SearchAllAnswerTrans extends IBusiness{

	public void execute() throws GeneralException {
		
		HashMap map = this.getFormHM();
		ArrayList updateList = new ArrayList();
		// 试卷编号
		String r5300 = (String) map.get("r5300");
		r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
		// 课程编号
		String flag = (String) map.get("flag");
		// 考试编号
		String paper_id = (String) map.get("paper_id");
		paper_id = PubFunc.decrypt(SafeCode.decode(paper_id));
		        
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		String sign = "ok";
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from (select r5200 from ");
			if ("5".equals(flag)){
				sql.append(" tr_selfexam_test where paper_id=");
				sql.append(paper_id);
				
			} else  if ("2".equals(flag)) {
				sql.append(" tr_exam_paper where r5300=");
				sql.append(r5300);
			}
			
			sql.append(") a left join (select r5200,s_answer,o_answer from tr_exam_answer where a0100='");
			sql.append(this.userView.getA0100());
			sql.append("' and nbase='");
			sql.append(this.userView.getDbname());
			sql.append("' and exam_no=");
			sql.append(paper_id);
			sql.append(" and exam_type=");
			if ("5".equals(flag)) {
				sql.append("1");
			} else if ("2".equals(flag)){
				sql.append("2");
			}
			sql.append(") b on a.r5200=b.r5200 where ");
			sql.append("(b.o_answer is null ");
			if (Sql_switcher.searchDbServer() == 1) {
				sql.append(" or convert(varchar(200), b.o_answer)=''");
			}
			sql.append(") and (b.s_answer is null ");
			if (Sql_switcher.searchDbServer() == 1) {
				sql.append(" or convert(varchar(200),b.s_answer)=''");
			}
			sql.append(") ");
			
			this.frowset = dao.search(sql.toString());
			if (this.frowset.next()) {
				sign = "no";
			}
		} catch (Exception e) {
			sign = "no";
			e.printStackTrace();
		}
		
		this.getFormHM().put("r5300", SafeCode.encode(PubFunc.encrypt(r5300)));
		this.getFormHM().put("flag", flag);
		this.getFormHM().put("paper_id", SafeCode.encode(PubFunc.encrypt(paper_id)));
		this.getFormHM().put("biaozhi", sign);
		
		
	}

}
