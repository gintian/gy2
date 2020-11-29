package com.hjsj.hrms.transaction.train.trainexam.paper;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class SortQuestionTrans extends IBusiness {
	
	public SortQuestionTrans(){
	}
	

	public void execute() throws GeneralException {
		String r5300 = (String)this.getFormHM().get("r5300");//试卷
		r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
		String type_id = (String)this.getFormHM().get("type_id");
		String r5200s = (String)this.getFormHM().get("r5200s");
		String norders = (String)this.getFormHM().get("norders");
		String dels = (String)this.getFormHM().get("dels");
//		System.out.println("r5300:"+r5300+"--type_id:"+type_id+"--dels:"+dels);
//		System.out.println("r5200s:"+r5200s);
//		System.out.println("norder:"+norders);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		try {
			String sql="";
			//删除操作
			if(dels!=null&&dels.length()>0){
				dels = dels.substring(0,dels.length()-1);
				sql="delete tr_exam_paper where r5200 in ("+dels+") and r5300="+r5300+" and type_id="+type_id;
				dao.delete(sql, new ArrayList());
			}
			
			//修改顺序
			if(r5200s!=null&&r5200s.length()>0){
				String tmpr5200[] = r5200s.split(",");
				String tmpnorder[] = norders.split(",");
				for(int i=0;i<tmpr5200.length;i++){
					if(tmpr5200[i]==null||tmpr5200[i].length()<1)
						continue;
					sql="update tr_exam_paper set norder="+tmpnorder[i]+" where r5200="+tmpr5200[i]+" and r5300="+r5300+" and type_id="+type_id;
					dao.update(sql);
				}
			}
			
			/**修改试题题型信息*/
			sql="select sum(r5213) score,sum(r5211) answer_time,count(1) max_num from tr_exam_paper t,r52 r where t.r5200=r.r5200 and r5300="+r5300+" and t.type_id="+type_id;
			this.frowset = dao.search(sql,1,1);
			if(this.frowset.next()){
				double score = this.frowset.getDouble("score");//分数
				int answer_time = this.frowset.getInt("answer_time");//考试时间
				int max_num = this.frowset.getInt("max_num");//题数
				//获取对应的知识点编码
				String know_ids="";
				sql="select distinct know_id from tr_test_knowledge where R5200 in (select r.r5200 from tr_exam_paper t,r52 r where t.r5200=r.r5200 and r5300="+r5300+" and t.type_id="+type_id+")";
				this.frowset = dao.search(sql,1,1);
				while(this.frowset.next()){
					String know_id = this.frowset.getString("know_id");
					if(know_id!=null&&know_id.length()>0)
						know_ids+=know_id+",";
				}
				if(know_ids.length()>0)
					know_ids=know_ids.substring(0, know_ids.length()-1);
				
				//修改改试卷的题型信息
				sql="update tr_exam_question_type set score="+score+",answer_time="+answer_time+",max_num="+max_num+",know_ids='"+know_ids+"'  where r5300="+r5300+" and type_id="+type_id;
				dao.update(sql);
				
				//修改改试卷的平均难度值
				//sql="update r53 set r5306=(select avg(r5203) m from tr_exam_paper t,r52 r where t.r5200=r.r5200 and r5300="+r5300+") where r5300="+r5300;
				//dao.update(sql);
			}
			this.getFormHM().put("flag", "ok");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
