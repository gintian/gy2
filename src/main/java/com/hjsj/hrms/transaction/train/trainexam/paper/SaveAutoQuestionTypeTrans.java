package com.hjsj.hrms.transaction.train.trainexam.paper;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class SaveAutoQuestionTypeTrans extends IBusiness {

	public void execute() throws GeneralException {
		String type = (String)this.getFormHM().get("type");
		type = type!=null&&type.length()>0?type:"1";
		String r5300 = (String)this.getFormHM().get("r5300");
		r5300 = r5300!=null&&r5300.length()>0?r5300:"";
		if(type.length()>0&&r5300.length()>0){
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				this.frowset = dao.search("select 1 from tr_exam_question_type where r5300="+r5300+" and type_id="+type);
				if(this.frowset.next()){
					this.getFormHM().put("flag", "no");
				}else{
					RecordVo vo = new RecordVo("tr_exam_question_type");
					ArrayList keylist = new ArrayList();
					keylist.add("r5300");
					keylist.add("type_id");
					vo.setInt("r5300", Integer.parseInt(r5300));
					vo.setInt("type_id", Integer.parseInt(type));
					vo.setKeylist(keylist);
					vo.setInt("score", 0);//分数
					vo.setInt("answer_time", 0);//考试时间
					vo.setInt("max_num", 0);//试题数量
					//vo.setString("know_ids", "");//知识点
					vo.setInt("norder", getMaxNorder(dao)+1);
					dao.addValueObject(vo);
					this.getFormHM().put("flag", "ok");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				this.getFormHM().put("flag", "error");
				e.printStackTrace();
			}
		}
	}

	private int getMaxNorder(ContentDAO dao){
		int i=0;
		String sql = "select max(norder) a from tr_exam_question_type";
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next())
				i = this.frowset.getInt("a");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}
}
