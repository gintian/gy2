package com.hjsj.hrms.transaction.train.trainexam.paper;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class SaveQuestionTrans extends IBusiness {

	public void execute() throws GeneralException {
		String r5300 = (String)this.getFormHM().get("r5300");//试卷
		r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
		String type_id = (String)this.getFormHM().get("type_id");//试题类型
		String allsels = (String)this.getFormHM().get("allsels");//页面展示的数据
		allsels=allsels!=null&&allsels.length()>0?allsels.substring(0, allsels.length()-1):"";
		String sels = (String)this.getFormHM().get("sels");//选中的数据
		sels=sels!=null&&sels.length()>0?sels.substring(0, sels.length()-1):"";
		//System.out.println(r5300+"--"+type_id+"---"+allsels+"---"+sels);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		String[] allsel = allsels.split(",");
        int n = 0;
        String id = "";
        ArrayList list = new ArrayList();

        for (int i = 0; i < allsel.length; i++) {
            if (n > 0)
                id += ",";
            id += "'" + PubFunc.decrypt(SafeCode.decode(allsel[i])) + "'";
            n++;

            if (n == 1000) {
                list.add(id);
                id = "";
                n = 0;
            }
        }

        if (id != null && id.length() > 0) {
            list.add(id);
        }
        
		try {
			String tmpSels[]=sels.split(",");
			for (int i = 0; i < tmpSels.length; i++) {
			    String tid = PubFunc.decrypt(SafeCode.decode(tmpSels[i]));
				if(tmpSels[i]==null||tmpSels[i].length()<1)
					continue;
				this.frowset = dao.search("select 1 from tr_exam_paper where r5300="+r5300+" and r5200="+tid);
				if(!this.frowset.next()){
					dao.insert("insert into tr_exam_paper(r5200,r5300,type_id,norder) values ("+tid+","+r5300+","+type_id+","+(getMaxExamPaper(dao)+1)+")", new ArrayList());
					
					//修改试题信息的使用次数和最后使用时间
					dao.update("update r52 set r5214=r5214+1,r5215="+Sql_switcher.dateValue(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"))+" where r5200="+tid);
				}
			}
			
			/**修改试题题型信息*/
			String sql="select sum(r5213) score,sum(r5211) answer_time,count(1) max_num from tr_exam_paper t,r52 r where t.r5200=r.r5200 and r5300="+r5300+" and t.type_id="+type_id+"";
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				double score = this.frowset.getDouble("score");//分数
				int answer_time = this.frowset.getInt("answer_time");//考试时间
				int max_num = this.frowset.getInt("max_num");//题数
				//获取对应的知识点编码
				String know_ids="";
				sql="select distinct know_id from tr_test_knowledge where R5200 in (select r.r5200 from tr_exam_paper t,r52 r where t.r5200=r.r5200 and r5300="+r5300+" and t.type_id="+type_id+")";
				this.frowset = dao.search(sql);
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
	
	private int getMaxExamPaper(ContentDAO dao){
		int i=0;
		try {
			this.frowset = dao.search("select max(norder) m from tr_exam_paper");
			if(this.frowset.next())
				i=this.frowset.getInt("m");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i;
	}
}
