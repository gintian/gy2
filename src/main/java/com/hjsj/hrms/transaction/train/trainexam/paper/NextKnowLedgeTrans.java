package com.hjsj.hrms.transaction.train.trainexam.paper;

import com.hjsj.hrms.businessobject.train.trainexam.question.questiones.KnowLedgeDiffBo;
import com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class NextKnowLedgeTrans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String r5300 = (String)this.getFormHM().get("r5300");
		r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
		/*	String max_nums = (String)map.get("max_nums");
		String scores = (String)map.get("scores");
		String knowledges = (String)map.get("knowledges");
		knowledges=knowledges==null?"":knowledges;
		//System.out.println(r5300+"\r\n"+max_nums+"\r\n"+scores+"\r\n"+knowledges);
		
		//火狐浏览器通过地址栏传递值会讲 ` 翻译成 %6
		knowledges = knowledges.replaceAll("%6", "`").replaceAll("`", " `");//避免两个相邻为空的截取数组会忽略
		String know_ids[] = knowledges.split("`");
		scores = scores.replaceAll("%6", "`").replaceAll("`", " `");
		String score[] = scores.split("`");
		max_nums = max_nums.replaceAll("%6", "`").replaceAll("`", " `");
		String max_num[] = max_nums.split("`");
		ArrayList sqls = new ArrayList();
		try {
			//修改题型(题数、分数和知识点)内容
			this.frowset = dao.search("select type_id from tr_exam_question_type where r5300="+r5300+" order by norder");
			int i=0;
			while(this.frowset.next()){
				String tmpScore=score[i];
				tmpScore=tmpScore==null||tmpScore.trim().length()<1?"0":tmpScore.trim();
				String tmpMax_num=max_num[i];
				tmpMax_num=tmpMax_num==null||tmpMax_num.trim().length()<1?"0":tmpMax_num.trim();
				String tmpKnow_ids=know_ids[i];
				tmpKnow_ids=tmpKnow_ids==null?"":tmpKnow_ids.trim();
				String sql = "update tr_exam_question_type set score="+tmpScore+",max_num="+tmpMax_num+",know_ids='"+tmpKnow_ids+"'";
				sql+=" where r5300="+r5300+" and type_id="+this.frowset.getInt("type_id");
				sqls.add(sql);
				i++;
			}
			//System.out.println(sqls);
			dao.batchUpdate(sqls);
			*/
			/*设置知识点难度比例*/
		try {
			QuestionesBo bo = new QuestionesBo();
			ArrayList knowledgeList = new ArrayList();
			String sql = "select type_id,know_ids from tr_exam_question_type where r5300="+r5300+" order by norder";//取出改试卷中存在的知识点
			String tmpKnow_ids="";
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				String tmpk = this.frowset.getString("know_ids");
				String tmpt = this.frowset.getString("type_id");
				if(tmpk!=null&&tmpk.length()>0&&!"0".equals(tmpk)){
					tmpKnow_ids=tmpk+",";
					String knowledge[] = tmpKnow_ids.toString().split(",");
					for (int j = 0; j < knowledge.length; j++) {
						//if(!knowledgeList.contains(knowledge[j]))
							knowledgeList.add(tmpt+"_"+knowledge[j]);
					}
				}//else
					//knowledgeList.add(tmpt+"_");
			}
			
			new KnowLedgeDiffBo(this.getFrameconn(),r5300);//初始化改试卷已有的难度比例值
			
			this.getFormHM().put("knowledgeList", knowledgeList);
			this.getFormHM().put("difficultyList", bo.getDifficultyList());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
