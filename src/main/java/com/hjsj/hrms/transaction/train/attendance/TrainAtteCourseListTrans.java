package com.hjsj.hrms.transaction.train.attendance;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.attendance.TrainAtteBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>TrainAtteCourseListTrans.java</p>
 * <p>Description:培训考勤列表</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2011-03-03 上午09:07:55</p>
 * @author LiWeichao
 * @version 5.0
 */
public class TrainAtteCourseListTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String courseplan=(String)hm.get("courseplan");
		if(courseplan != null && courseplan.length()>0)
		    courseplan = PubFunc.decrypt(SafeCode.decode(courseplan));
		String a_code=(String)hm.get("a_code");
		a_code = a_code!=null&&a_code.trim().length()>0?a_code:"";
		hm.remove("courseplan");
		
//		if("".equals(a_code)&&(userView.getStatus()==4||userView.isSuper_admin())){
//			a_code=this.getUserView().getManagePrivCode()+this.getUserView().getManagePrivCodeValue();
//		}
//		/**liwc 业务用户走操作单位，没有操作单位时走管理范围=lmm*/
//		else if("".equals(a_code)&&(userView.getStatus()==0&&!userView.isSuper_admin())){
//			String codeall = userView.getUnit_id();
//			if(codeall!=null&&codeall.length()>2)
//				a_code=codeall.split("`")[0];
//			else if("".equals(a_code))
//				a_code=this.getUserView().getManagePrivCode()+this.getUserView().getManagePrivCodeValue();
//		}
		if("".equals(a_code)&&!userView.isSuper_admin()){
			TrainCourseBo bo = new TrainCourseBo(this.userView);
			a_code = bo.getUnitIdByBusi();
			if(a_code.length()<3)
				throw new GeneralException(ResourceFactory.getProperty("train.job.authorization1"));
		}
		
		DbWizard dbw = new DbWizard(this.getFrameconn());
		if(!dbw.isExistTable("tr_classplan", false))
			throw new GeneralException(ResourceFactory.getProperty("培训课程表不存在！"));
		
		String columns="id,r4101,train_date,begin_time,end_time,class_len,begin_card,end_card";
		String sql_str="select "+columns;
		StringBuffer cond_str=new StringBuffer();
		cond_str.append(" from tr_classplan");
		//权限范围内的培训班
		TrainAtteBo bo = new TrainAtteBo();
		ArrayList classplanlist=bo.getTrainClass(this.getFrameconn(), a_code);
		try {
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String flag=(String)hm.get("queryflag");
			//hm.remove("queryflag");//queryflag=1 选择培训班或课程进行查询
			if("1".equals(flag))
				cond_str.append(" where r4101='"+courseplan+"'");
			else if(classplanlist.size()>0){ //第一次进入 默认显示第一个培训班的第一个课程的排班信息
				CommonData cd=(CommonData) classplanlist.get(0);
				String sql="select r4101 from r41,r13 where r1301=r4105 and r4103='"+PubFunc.decrypt(SafeCode.decode(cd.getDataValue()))+"'";
				this.frecset=dao.search(sql);
				if(this.frecset.next())
					cond_str.append(" where r4101='"+this.frecset.getString("r4101")+"'");
				else
					cond_str.append(" where 1=2");
			}else
				cond_str.append(" where 1=2");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.userView.getHm().put("train_sql", sql_str + cond_str.toString() + " order by train_date,begin_time");
		this.userView.getHm().put("train_columns", columns);
		this.getFormHM().put("courseplan", SafeCode.encode(PubFunc.encrypt(courseplan)));
		this.getFormHM().put("classplanlist", classplanlist);
		this.getFormHM().put("columns", columns);
		this.getFormHM().put("sql_str", sql_str);
		this.getFormHM().put("cond_str", cond_str.toString());
		this.getFormHM().put("order_str", " order by train_date,begin_time");
		
	}
}
