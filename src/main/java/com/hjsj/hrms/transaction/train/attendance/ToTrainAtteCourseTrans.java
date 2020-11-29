package com.hjsj.hrms.transaction.train.attendance;

import com.hjsj.hrms.businessobject.train.attendance.TrainAtteBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
/**
 * <p>ToTrainAtteCourseTrans.java</p>
 * <p>Description:培训考勤to:添加排班页面</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2011-03-04 上午09:07:55</p>
 * @author LiWeichao
 * @version 5.0
 */
public class ToTrainAtteCourseTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String id=(String)hm.get("id");
		String r4101=(String)hm.get("r4101");
		hm.remove("id");
		hm.remove("r4101");
		id=id==null||id.length()<1?"0":id;
		if(id != null && id.length()>0 && !"0".endsWith(id))
		    id = PubFunc.decrypt(SafeCode.decode(id));
		
		r4101=r4101==null||r4101.length()<1?"0":r4101;
		if(r4101 != null && r4101.length()>0)
		    r4101 = PubFunc.decrypt(SafeCode.decode(r4101));
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		TrainAtteBo tb=new TrainAtteBo();
		if("0".equals(id)){//id=0 新增 初始化数据
			String[] date=tb.getR41Info(this.getFrameconn(), r4101);
			this.getFormHM().put("start_date", date[0]);
			this.getFormHM().put("stop_date", date[1]);
			Date d=new Date();
			sdf=new SimpleDateFormat("HH:mm");
			this.getFormHM().put("begin_time", sdf.format(d));
			this.getFormHM().put("end_time", sdf.format(d));
			this.getFormHM().put("class_len", "");
			this.getFormHM().put("begin_card", "0");
			this.getFormHM().put("end_card", "0");
			this.getFormHM().put("r4101", SafeCode.encode(PubFunc.encrypt(r4101)));
			this.getFormHM().put("minute", "60");
			this.getFormHM().put("id", id);
		}else{
			String sql="select * from tr_classplan where id="+id;
			try {
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				tb.addClassPlanColumn(this.getFrameconn());
				this.frecset=dao.search(sql);
				while(this.frecset.next()){
					Date date=this.frecset.getDate("train_date");
					String train_date=date!=null?sdf.format(date):"";
					this.getFormHM().put("start_date", train_date);
					this.getFormHM().put("stop_date", train_date);
					this.getFormHM().put("begin_time", this.frecset.getString("begin_time"));
					this.getFormHM().put("end_time", this.frecset.getString("end_time"));
					this.getFormHM().put("class_len", this.frecset.getString("class_len"));
					this.getFormHM().put("begin_card", this.frecset.getString("begin_card"));
					this.getFormHM().put("end_card", this.frecset.getString("end_card"));
					this.getFormHM().put("r4101", SafeCode.encode(PubFunc.encrypt(this.frecset.getString("r4101"))));
					String minute=this.frecset.getString("minute");
					minute=minute==null||minute.length()<1?"60":minute;
					if(minute.indexOf(".")!=-1)
						minute = minute.substring(0, minute.indexOf("."));
					this.getFormHM().put("minute", minute);
					this.getFormHM().put("id", SafeCode.encode(PubFunc.encrypt(id)));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		this.getFormHM().put("requestPamaHM", hm);
	}
}
