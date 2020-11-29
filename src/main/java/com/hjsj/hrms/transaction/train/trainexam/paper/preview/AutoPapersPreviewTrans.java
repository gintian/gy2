package com.hjsj.hrms.transaction.train.trainexam.paper.preview;

import com.hjsj.hrms.businessobject.train.resource.MyLessonBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class AutoPapersPreviewTrans extends IBusiness {

	public void execute() throws GeneralException {
		String flag = (String)this.getFormHM().get("flag");
		String r5300 = (String)this.getFormHM().get("r5300");//试卷id
		r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
		String exam_type = (String)this.getFormHM().get("exam_type");//考试类型
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		/*获得试卷基本信息*/
		int examscore = 0;
		int examtime = 0;
		String sql = "select r5301,r5303,r5304,r5305 from r53 where r5300="+r5300;
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				this.getFormHM().put("title", this.frowset.getString("r5301"));
				examtime = this.frowset.getInt("r5305");
				this.getFormHM().put("examtime", String.valueOf(examtime));
				examscore = this.frowset.getInt("r5304");
				this.getFormHM().put("examscore", String.valueOf(examscore));
				String examdescribe = this.frowset.getString("r5303");
				examdescribe=examdescribe!=null&&examdescribe.length()>1?examdescribe.replace("\r\n", "<br/>"):"";
				this.getFormHM().put("examdescribe", examdescribe);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//试题编号,试题内容,试题选项,主观题答案,客观题答案,试题解析,考试得分,题型编号,用户得分,用户主观题答案,用户客观题答案
		String columns="r5200,r5205,r5207,r5208,r5209,r5210,r5213,type_id,score,s_answer,o_answer";
		String strsql="";
		String strwhere="";
		if("1".equals(flag)||"2".equals(flag)){//预览或生成试卷
			strsql="select r.r5200,r5205,r5207,r5208,r5209,r5210,r5213,t.type_id,a.score,s_answer,o_answer";
			strwhere=" from tr_exam_paper t left join r52 r on r.r5200=t.r5200";
			strwhere+=" left join tr_exam_answer a on a.r5200=t.r5200 and a.r5300=t.r5300 and exam_type="+exam_type+" and nbase='"+userView.getDbname()+"' and a0100='"+userView.getA0100()+"'";
			strwhere+=" left join tr_exam_question_type q on q.type_id=t.type_id and q.r5300="+r5300;
			strwhere+=" where t.r5300="+r5300;
		}else if("3".equals(flag)||"4".equals(flag)){//考试或评卷
			strsql="select r.r5200,r5205,r5207,r5208,r5209,r5210,r5213,r.type_id,a.score,s_answer,o_answer";
			strwhere=" from tr_exam_answer a left join r52 r on a.r5200=r.r5200";
			strwhere+=" left join tr_exam_question_type q on q.type_id=r.type_id and q.r5300="+r5300;
			strwhere+=" where a.r5300="+r5300+" and exam_type="+exam_type+" and nbase='"+userView.getDbname()+"' and a0100='"+userView.getA0100()+"'";
		}
		
		MyLessonBo bo = new MyLessonBo(this.frameconn, this.userView);
        String enableArch = bo.getDisableExamOrEnableArch("");
        
        this.getFormHM().put("enableArch", enableArch);
		this.getFormHM().put("columns", columns);
		this.getFormHM().put("strsql", strsql);
		this.getFormHM().put("strwhere", strwhere);
		this.getFormHM().put("order_by", " order by q.norder,exam_no");
	}
}