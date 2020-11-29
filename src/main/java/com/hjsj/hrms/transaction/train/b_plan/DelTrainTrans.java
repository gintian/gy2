package com.hjsj.hrms.transaction.train.b_plan;

import com.hjsj.hrms.businessobject.train.TrainBudgetBo;
import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.businessobject.train.TrainPlanBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * <p>Title:培训计划</p>
 * <p>Description:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class DelTrainTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String r3101 = (String)this.getFormHM().get("r3101");
		r3101=r3101!=null&&r3101.length()>0?r3101:"";
		if(r3101.length()<1)
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("train.b_plan.del.record")+"!"));
		String checkflag = (String)this.getFormHM().get("checkflag");
		checkflag=checkflag!=null&&checkflag.length()>0?checkflag:"";
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		if("del".equalsIgnoreCase(checkflag))
			delRecord(dao,r3101);
		else if("introd".equalsIgnoreCase(checkflag)){
			String r2501 = (String)this.getFormHM().get("r2501");
			r2501=r2501!=null&&r2501.length()>0?r2501:"";
			if(r2501.length()>1)
				introdRecord(dao,r3101,r2501);
		}else if("abolish".equalsIgnoreCase(checkflag))
			abolishRecord(dao,r3101);
			
			
		
		
	}
	private ArrayList getRecorder(ContentDAO dao,String r3101){
		ArrayList list = new ArrayList();
		String arr[] = r3101.split(",");
		for(int i=0;i<arr.length;i++){
			String id = PubFunc.decrypt(SafeCode.decode(arr[i]));
			if(id!=null&&id.length()>0){
				RecordVo vo = new RecordVo("r31");
				vo.setString("r3101",id);
				try {
					vo = dao.findByPrimaryKey(vo);
					list.add(vo);
				} catch (GeneralException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return list;
	}
	private void delRecord(ContentDAO dao,String r3101){
		ArrayList list = getRecorder(dao,r3101);
		StringBuffer exper = new StringBuffer("");
		String flag = "no";
		try {
			String namestr="";
			TrainClassBo bo = new TrainClassBo(this.frameconn);
			ArrayList valuelist = new ArrayList();
			for(int i=0;i<list.size();i++){
				RecordVo vo=(RecordVo)list.get(i);
				String sp = vo.getString("r3127");
				namestr =  vo.getString("r3130");
				if("01".equals(sp)){
					exper.append("\n\n["+namestr+"]"+ResourceFactory.getProperty("train.b_plan.del.submit.drafting.error")+"!");
					continue;
				}else if("07".equals(sp)){
					exper.append("\n\n["+namestr+"]"+ResourceFactory.getProperty("train.b_plan.del.dismissed.error")+"!");
					continue;
				}else if("04".equals(sp)){
					exper.append("\n\n["+namestr+"]"+ResourceFactory.getProperty("train.b_plan.del.published.error")+"!");
					continue;
				}else if("05".equals(sp)){
					exper.append("\n\n["+namestr+"]"+ResourceFactory.getProperty("train.b_plan.del.perform.error")+"!");
					continue;
				}else if("06".equals(sp)){
					exper.append("\n\n["+namestr+"]"+ResourceFactory.getProperty("train.b_plan.del.end.error")+"!");
					continue;
				}
				
				if(!bo.checkClassPiv(vo.getString("r3101"), this.userView))
                    continue;
				
				valuelist.add(vo);
			}
			if(valuelist.size()>0){
				//培训预算
				TrainBudgetBo tbb = new TrainBudgetBo(this.getFrameconn());
				if(tbb.getBudget()!=null&&tbb.getBudget().length()>0){
					for (int i = 0; i < list.size(); i++) {
						RecordVo vo=(RecordVo)valuelist.get(i);
						if("03".equals(vo.getString("r3127"))||"09".equals(vo.getString("r3127")))
							tbb.updateTrainBudget("1", vo.getString("r3101"), -999999,null);
					}
			    }
				
				dao.deleteValueObject(valuelist);
			}
			flag = "ok";
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getFormHM().put("flag",flag);
		this.getFormHM().put("exper",SafeCode.encode(exper.toString()));
	}
	private void introdRecord(ContentDAO dao,String r3101,String r2501){
		String flag = "no";
		TrainPlanBo bo = new TrainPlanBo(this.frameconn);
		if(!bo.checkPlanPiv(r2501, this.userView)){
		    this.getFormHM().put("flag",flag);
		    return;
		}
		    
		try {
			TrainBudgetBo tbb = new TrainBudgetBo(this.getFrameconn());
			StringBuffer buf = new StringBuffer();
			buf.append("update r31 set r3125='");
			buf.append(r2501);
			buf.append("' where r3101=?");
			ArrayList valuelist = new ArrayList();
			String arr[] = r3101.split(",");
			for(int i=0;i<arr.length;i++){
				String id = PubFunc.decrypt(SafeCode.decode(arr[i]));
				if(id!=null&&id.length()>0){
					ArrayList list = new ArrayList();
					list.add(id);
					valuelist.add(list);
					if(tbb.getBudget()!=null&&tbb.getBudget().length()>0)
						tbb.updateTrainBudget("2", id, 0,r2501);//费用预算
				}
			}
			dao.batchUpdate(buf.toString(),valuelist);
			flag="ok";
		}  catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getFormHM().put("flag",flag);
	}
	private void abolishRecord(ContentDAO dao,String r3101){
		String flag = "no";
		try {
			TrainBudgetBo tbb = new TrainBudgetBo(this.getFrameconn());
			StringBuffer buf = new StringBuffer();
			buf.append("update r31 set r3125='' where r3101=?");
			ArrayList valuelist = new ArrayList();
			String arr[] = r3101.split(",");
			for(int i=0;i<arr.length;i++){
				String id = PubFunc.decrypt(SafeCode.decode(arr[i]));
				if(id!=null&&id.length()>0){
					ArrayList list = new ArrayList();
					list.add(id);
					valuelist.add(list);
					if(tbb.getBudget()!=null&&tbb.getBudget().length()>0)
						tbb.updateTrainBudget("2", id, 0,null);//费用预算
				}
			}
			dao.batchUpdate(buf.toString(),valuelist);
			flag="ok";
		}  catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getFormHM().put("flag",flag);
	}
}
