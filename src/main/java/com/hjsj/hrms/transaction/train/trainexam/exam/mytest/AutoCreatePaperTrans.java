package com.hjsj.hrms.transaction.train.trainexam.exam.mytest;

import com.hjsj.hrms.businessobject.train.trainexam.question.questiones.AutoGroupPaperBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>
 * Title:SearchMyTestTrans.java
 * </p>
 * <p>
 * Description:自由组卷
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
public class AutoCreatePaperTrans extends IBusiness{

	public void execute() throws GeneralException {
		
		HashMap map = this.getFormHM();
		// 试卷编号
		String r5300 = map.get("r5300").toString();
		r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
		// 课程编号
		String r5000 = map.get("r5000").toString();
		r5000 = PubFunc.decrypt(SafeCode.decode(r5000));
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		String flag = "ok";
		try {
			//组卷
				String sql = "select r5304,r5305 from r53 where r5300="+r5300;
				this.frowset = dao.search(sql);
				int examscore = 0;
				int examtime = 0;
				if(this.frowset.next()){
					examscore = this.frowset.getInt("r5304");
					examtime = this.frowset.getInt("r5305");
				}
				AutoGroupPaperBo bo = new AutoGroupPaperBo(this.getFrameconn(),this.getUserView(),r5300,examscore,examtime);
				flag = bo.getGroupPaperIds();
				//System.out.println(bo.getIdsList());
				if("ok".equals(flag)){
					ArrayList idsList = bo.getIdsList();
					ArrayList typeIdsList = bo.getTypeIdsList();
					IDGenerator idg = new IDGenerator(2, this.getFrameconn());
					String id = idg.getId("tr_selfexam_paper.paper_id");
					this.getFormHM().put("paper_id", SafeCode.encode(PubFunc.encrypt(id)));
					RecordVo vo = new RecordVo("tr_selfexam_paper");
					vo.setInt("paper_id", Integer.parseInt(id));
					vo.setInt("r5300", Integer.parseInt(r5300));
					vo.setString("nbase", this.userView.getDbname());
					vo.setString("a0100", this.userView.getA0100());
					vo.setString("nbase", this.userView.getDbname());
					vo.setDate("create_time", new Date());
					
					dao.addValueObject(vo);
					
					ArrayList insertList = new ArrayList();
					ArrayList updateList = new ArrayList();
					for (int i = 0; i < idsList.size(); i++) {
						ArrayList list = new ArrayList();
						ArrayList list2 = new ArrayList();
						list.add(idsList.get(i));
						list.add(Integer.valueOf(id));
						list.add(typeIdsList.get(i));
						list.add(Integer.valueOf(i + 1));
						list2.add(idsList.get(i));
						insertList.add(list);
						updateList.add(list2);
											
					}
					dao.batchInsert("insert into tr_selfexam_test(r5200,paper_id,type_id,norder) values (?,?,?,?)", insertList);
			
					//修改试题信息的使用次数和最后使用时间
					dao.batchUpdate("update r52 set r5214=r5214+1,r5215="+Sql_switcher.dateValue(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"))+" where r5200=?",updateList);
					
					this.frowset = dao.search("select r5315 from r53 where r5300=" + r5300);
					if(this.frowset.next())
					    this.getFormHM().put("modelType", this.frowset.getString("r5315"));
				
				}else {
					flag=SafeCode.encode(flag);
				}
		
		} catch (Exception e) {
			flag = "erro";
			e.printStackTrace();
		}
		
		this.getFormHM().put("biaozhi", flag);
		this.getFormHM().put("r5300", SafeCode.encode(PubFunc.encrypt(r5300)));
		this.getFormHM().put("r5000", SafeCode.encode(PubFunc.encrypt(r5000)));
		
		
	}

}
