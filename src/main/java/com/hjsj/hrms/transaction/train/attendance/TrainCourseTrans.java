package com.hjsj.hrms.transaction.train.attendance;

import com.hjsj.hrms.businessobject.train.attendance.TrainAtteBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.text.DecimalFormat;
/**
 * <p>TrainAtteCourseListTrans.java</p>
 * <p>Description:培训考勤列表</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2011-03-04 上午09:07:55</p>
 * @author LiWeichao
 * @version 5.0
 */
public class TrainCourseTrans extends IBusiness {

	/**
	 * flag=0;对应课程信息  时间范围classInfo：xxxx-xx-xx~xxxx-xx-xx  classnum1：已排班课时 classnum2：未排班课时
	 * flag=1;根据培训班编号查询相应培训课程 value:值列表 text：内容列表
	 * flag=2;修改上课是否签到或下课是否签退
	 */
	public void execute() throws GeneralException {
		String flag=(String)this.getFormHM().get("flag");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		TrainAtteBo tb=new TrainAtteBo();
		if("0".equals(flag)){
			String r4101=(String)this.getFormHM().get("r4101");
			if(r4101 != null && r4101.length()>0)
			    r4101 = PubFunc.decrypt(SafeCode.decode(r4101));
			String[] r41Info=tb.getR41Info(this.getFrameconn(), r4101);
			String infodate="";
			String classnum1="";
			String classnum2="";
			DecimalFormat df = new DecimalFormat("0.00");
			if(r41Info[0]!=null&&r41Info[0].length()>5)
				infodate+=r41Info[0]+"~";
			if(r41Info[1]!=null&&r41Info[1].length()>5)
				infodate+=r41Info[1];
			try {
				String sql="select sum(class_len) num from tr_classplan where r4101='"+r4101+"'";
				this.frecset=dao.search(sql);
				if(this.frecset.next()){
					classnum1=df.format(this.frecset.getDouble("num"));
				}
				
				classnum2=r41Info[2]==null||r41Info[2].length()<1?"0":r41Info[2];
				classnum2=df.format(Double.parseDouble(r41Info[2])-Double.parseDouble(classnum1));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			this.getFormHM().put("classInfo", infodate);
			this.getFormHM().put("classnum1", classnum1);
			this.getFormHM().put("classnum2", classnum2);
		}else if("1".equals(flag)){
			String classplan=(String)this.getFormHM().get("classplan");
			if(classplan != null && classplan.length()>0)
			    classplan = PubFunc.decrypt(SafeCode.decode(classplan));
			String value="";
			String text="";
			try {
				String sql="select r4101,r1302 from r41,r13 where r1301=r4105 and r4103='"+classplan+"'";
				this.frecset=dao.search(sql);
				while(this.frecset.next()){
					text+=this.frecset.getString("r1302")+",";
					value+=SafeCode.encode(PubFunc.encrypt(this.frecset.getString("r4101")))+",";
				}
				if(text!=null&&text.length()>1)
					text=text.substring(0, text.length()-1);
				if(value!=null&&value.length()>1)
					value=value.substring(0, value.length()-1);
				this.getFormHM().put("value", value);
				this.getFormHM().put("text", text);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else if("2".equals(flag)){
			try {
				String column=(String)this.getFormHM().get("column");
				String state=(String)this.getFormHM().get("state");
				String id=(String)this.getFormHM().get("id");
				if(id != null && id.length()>0)
				    id = PubFunc.decrypt(SafeCode.decode(id));
				String sql="update tr_classplan set "+column+"="+state+" where id="+id;
				dao.update(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}
