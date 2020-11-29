package com.hjsj.hrms.transaction.train.trainexam.paper;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class AddQuestionTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap map = (HashMap) this.getFormHM().get("requestPamaHM");
		map.remove("b_add");
		if(map.get("type_id")!=null){
			map.remove("type_id");
			this.getFormHM().put("difficulty", "");
			this.getFormHM().put("knowledge", "");
			this.getFormHM().put("knowledgeviewvalue", "");
		}
		String type_id = (String)this.getFormHM().get("type_id");
		String r5300 = (String)this.getFormHM().get("r5300");
		r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
		// 难度
		String difficulty = (String) this.getFormHM().get("difficulty");
		//知识点
		String knowledge = (String)this.getFormHM().get("knowledge");
		
		QuestionesBo bo = new QuestionesBo(this.frameconn);
		// 难度类表
		ArrayList questions = bo.getDifficultyList();
		
		try{
			
			String columns = "r5200,r5204,r5203,R5213,id";
			String strsql = "select r5200,r5204,r5203,R5213,(select t.r5200 from tr_exam_paper t where t.r5200=r52.r5200 and r5300="+r5300+") id";
			String strwhere=" from r52 where type_id="+type_id;
			
			// 权限过滤
			if (!this.userView.isSuper_admin()) {
				TrainCourseBo tb = new TrainCourseBo(this.userView);
				String unit = tb.getUnitIdByBusi();//this.userView.getUnitIdByBusi("6");
				if(unit.indexOf("UN`")==-1){
					String []units = unit.split("`");
					strwhere+=" and (";
					if (units.length > 0 && unit.length() > 0) {
						for (int i = 0; i < units.length; i++) {
							String b0110s = units[i].substring(2);
							strwhere+="b0110=" + Sql_switcher.substr("'"+b0110s+"'", "1", Sql_switcher.length("b0110"));
							strwhere+=" or b0110 like '";
							strwhere+=b0110s;
							strwhere+="%'";
							strwhere+=" or ";
						}
					}
					strwhere+=Sql_switcher.isnull("b0110", "'-1'");
					strwhere+="='-1'";
					if (Sql_switcher.searchDbServer() == 1) {
						strwhere+=" or b0110=''";
					}
					strwhere+=" or r5216=1)";
				}
			}
			
			if(difficulty!=null&&difficulty.length()>0){
				strwhere+=" and r5203="+difficulty;
			}
			if(knowledge!=null&&knowledge.length()>0){
				strwhere+=" and r5200 in (select r5200 from tr_test_knowledge where ";
				String[] tmp = knowledge.split(",");
				for (int i = 0; i < tmp.length; i++) {
					if(i>0)
						strwhere+=" or ";
					strwhere += "know_id like '"+tmp[i]+"%'";
				}
				strwhere += ")";
			}
			//getStateInfo(strwhere);
			this.getFormHM().put("columns", columns);
			this.getFormHM().put("strsql", strsql);
			this.getFormHM().put("strwhere", strwhere);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("questions", questions);
			
			this.getFormHM().put("order_by", "order by norder");
		}
	}
	
	private void getStateInfo(String strwhere){
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select te.norder "+strwhere;
		try {
			this.frowset = dao.search(sql+" order by te.norder");
			if(this.frowset.next())
				this.getFormHM().put("start", this.frowset.getInt("norder")+"");
			
			this.frowset = dao.search(sql+" order by te.norder desc");
			if(this.frowset.next())
				this.getFormHM().put("end", this.frowset.getInt("norder")+"");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


}
