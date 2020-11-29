package com.hjsj.hrms.transaction.train.b_plan;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.b_plan.PlanTransBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:培训计划</p>
 * <p>Description:培训计划显示</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class PlanTrainTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		
		String a_code = (String)hm.get("a_code");
		a_code = a_code!=null&&a_code.trim().length()>0?a_code:"";
		hm.remove("a_code");
		String a_code1 = a_code;
		/**liwc 业务用户走操作单位，没有操作单位时走管理范围=lmm*/
		if("".equals(a_code)){
//			if("".equals(a_code)&&userView.getStatus()==4){
//				a_code=this.getUserView().getManagePrivCode()+this.getUserView().getManagePrivCodeValue();
//			}else if("".equals(a_code)&&userView.getStatus()==0){
//				String codeall = userView.getUnit_id();
//				if(codeall!=null&&codeall.length()>2)
//					a_code=codeall;//.split("`")[0];
//				else if("".equals(a_code))
//					a_code=this.getUserView().getManagePrivCode()+this.getUserView().getManagePrivCodeValue();
//			}
			TrainCourseBo bo = new TrainCourseBo(this.userView);
			a_code = bo.getUnitIdByBusi();
			if("".equals(a_code)||a_code.length()<3)
				throw new GeneralException(ResourceFactory.getProperty("train.job.authorization1"));
		}
		
		String model = (String)hm.get("model");
		model = model!=null&&model.trim().length()>0?model:"1";
		hm.remove("model");
		String model1 = (String)this.formHM.get("model1");

		String spflag = (String)this.getFormHM().get("spflag");
		spflag=spflag!=null&&spflag.trim().length()>0?spflag:"";
		spflag= "00".equalsIgnoreCase(spflag)?"":spflag;
		
		String edit="false";
		if("2".equals(model)){
			spflag= "01".equals(spflag)?"":spflag;
			if("02".equals(spflag)){
				edit="true";
			}else if("03".equals(spflag)){
				edit="true";
			}
		}else{
			if("01".equals(spflag)){
				edit="true";
			}else if("07".equals(spflag)){
				edit="true";
			}
		}
		
		String timeflag = (String)this.getFormHM().get("timeflag");
		timeflag=timeflag!=null&&timeflag.trim().length()>0?timeflag:"";
		timeflag= "00".equalsIgnoreCase(timeflag)?"":timeflag;
		
		String startime = (String)this.getFormHM().get("startime");
		startime=startime!=null&&startime.trim().length()>0?startime:"";
		
		String endtime = (String)this.getFormHM().get("endtime");
		endtime=endtime!=null&&endtime.trim().length()>0?endtime:"";
		
		String searchstr = (String)this.getFormHM().get("searchstr");
		searchstr=searchstr!=null&&searchstr.trim().length()>0?searchstr:"";
		
		if (!model.equals(model1)) {
			spflag = "";
			timeflag="";
			startime="";
			endtime="";
		}
		
		PlanTransBo transbo = new PlanTransBo(this.getFrameconn(),model); 
		
		StringBuffer sqlstr = new StringBuffer();
		String times = transbo.timesSql(timeflag,startime,endtime);
		sqlstr.append(transbo.sqlColum());
		String wheresr = transbo.sqlWhere(this.userView,SafeCode.decode(searchstr),a_code,times,spflag);
		sqlstr.append(wheresr);
		if("1".equals(model)){
			sqlstr.append(" and R2509 in('01','02','03','04','06','07','09')");
		}else{
			sqlstr.append(" and R2509 in('02','03','04','06','09')");
		}
		sqlstr.append(" order by I9999");
		ContentDAO dao  = new ContentDAO(this.frameconn);
		int countvalue=0;
		try {
			this.frowset = dao.search("select count(R2501) as countid "+wheresr);
			if(this.frowset.next()){
				countvalue = this.frowset.getInt("countid");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList itemlist = transbo.itemList();
		
		this.userView.getHm().put("train_sql", sqlstr.toString());
		
		this.getFormHM().put("tablename","r25");
		this.getFormHM().put("a_code",a_code1);
		this.getFormHM().put("strsql",sqlstr.toString());
		this.getFormHM().put("sqlstr", SafeCode.encode(sqlstr.toString()));
		this.getFormHM().put("model",model);
		this.getFormHM().put("model1",model);
		this.getFormHM().put("spflag",spflag);
		this.getFormHM().put("timeflag",timeflag);
		this.getFormHM().put("startime",startime);
		this.getFormHM().put("endtime",endtime);
		this.getFormHM().put("searchstr","");
		this.getFormHM().put("itemlist",itemlist);
		this.getFormHM().put("flaglist",transbo.spFlagList());
		this.getFormHM().put("timelist",transbo.timeFlagList());
		this.getFormHM().put("username",this.userView.getUserName());
		this.getFormHM().put("fieldSize",itemlist.size()+"");
		this.getFormHM().put("novalue",countvalue+"");
		this.getFormHM().put("searchlist",searchTable(dao,"4"));
		this.getFormHM().put("edit",edit);
	}
	
	private ArrayList searchTable(ContentDAO dao,String type){
		ArrayList searchlist = new ArrayList();
		
		String sqlstr = "select Id,Name,LExpr,Factor,FuzzyFlag from LExpr where Type="+type;
		try {
			this.frowset=dao.search(sqlstr);
			int n=1;
			while(this.frowset.next()){
				if(!(this.userView.isHaveResource(IResourceConstant.LEXPR,this.frowset.getString("Id"))))
                	continue;
				String LExpr = this.frowset.getString("LExpr");
				String Factor = this.frowset.getString("Factor");
				String FuzzyFlag = this.frowset.getString("FuzzyFlag");
				
				CommonData job=new CommonData();
				job.setDataName(SafeCode.encode(LExpr+"::"+Factor+"::"+FuzzyFlag));
				job.setDataValue(this.frowset.getString("Id")+"."+this.frowset.getString("name"));
				searchlist.add(job);
				n++;
				if(n>10)
					break;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return searchlist;
	}
}
