package com.hjsj.hrms.transaction.train.attendance;

import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.train.attendance.TrainAtteBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/**
 * <p>SaveTrainAtteCourseTrans.java</p>
 * <p>Description:培训考勤-</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2011-03-03 上午09:07:55</p>
 * @author LiWeichao
 * @version 5.0
 */
public class SaveTrainAtteCourseTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			String id=(String)this.getFormHM().get("id");
			String r4101=(String)this.getFormHM().get("r4101");
			if(r4101 != null && r4101.length() > 0)
				r4101 = PubFunc.decrypt(SafeCode.decode(r4101));

			String start_date=(String)this.getFormHM().get("start_date");
			String stop_date=(String)this.getFormHM().get("stop_date");
			String begin_time=(String)this.getFormHM().get("begin_time");
			String end_time=(String)this.getFormHM().get("end_time");
			String begin_card=(String)this.getFormHM().get("begin_card");
			String end_card=(String)this.getFormHM().get("end_card");
			String holiday=(String)this.getFormHM().get("holiday");
			String feast=(String)this.getFormHM().get("feast");
			begin_time = recomposeDate(begin_time);
			end_time = recomposeDate(end_time);

			begin_time=begin_time==null?"00:00":begin_time;
			end_time=end_time==null?"00:00":end_time;
			stop_date=stop_date==null||stop_date.length()<10?start_date:stop_date;
			String minuteStr=(String)this.getFormHM().get("minute");

			double classMinute = 0;
			try {
				classMinute = Double.parseDouble(minuteStr);
			} catch (NumberFormatException e) {
				throw new GeneralException("每课时分钟数必须为整数！");
			}

			double len=getClass_len(begin_time,end_time,Double.parseDouble(minuteStr));//获取课时
			ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
			String leave_early = constantbo.getTextValue("/param/attendance/leave_early");
			String late_for = constantbo.getTextValue("/param/attendance/late_for");
			leave_early=leave_early==null||leave_early.length()<1?"0":leave_early;
			late_for=late_for==null||late_for.length()<1?"0":late_for;
			int minute=Integer.parseInt(leave_early)+Integer.parseInt(late_for);
			double hour=minute/Double.parseDouble(minuteStr);
			if(len<=0)
				return;
			else if(len<hour){//排班课时需大于参数设置中的 (XX分钟算迟到+XX分钟前算早退)/60.0
				this.getFormHM().put("mess", ResourceFactory.getProperty("train.b_plan.classplan.savemess2.1")
						+minute+ResourceFactory.getProperty("train.b_plan.classplan.savemess2.2"));
				return;
			}else{
				DecimalFormat df = new DecimalFormat("#.##");
				len=Double.parseDouble(df.format(len));
			}

			id=id==null||id.length()<1?"0":id;
			if(id != null && id.length() > 0 && !"0".equalsIgnoreCase(id))
				id = PubFunc.decrypt(SafeCode.decode(id));

			ContentDAO dao=new ContentDAO(this.getFrameconn());
			TrainAtteBo tb=new TrainAtteBo();
			if("0".equals(id)){//id=0新增排班信息
				if(!checkAddTime(r4101,start_date,stop_date)) return;//添加时间范围和培训课程时间比较
				if(tb.verdictDate(start_date, stop_date)){
					ArrayList list=tb.displayEveryDate(start_date, stop_date);
					for (int i = 0; i < list.size(); i++) {
						String date=(String) list.get(i);
						boolean flag=true;
						if("1".equals(feast)){
						////判断当前是否是节假日
							String f=IfRestDate.if_Feast(date.replaceAll("-", "\\."), this.getFrameconn());//判断当前是否是节假日
							flag=f==null||f.length()<1?true:false;
						}
						if("1".equals(holiday)){
							////判断当前是否是公休日
							ArrayList restList=IfRestDate.search_RestOfWeek(null, this.getUserView(), this.getFrameconn());
							if (restList!=null&&restList.size()>0) {
								if(IfRestDate.if_Rest(date.replaceAll("-", "\\."), this.getUserView(), (String)restList.get(0)))
									flag=false;
							}
						}
						if(!checkIsRepeat(id,r4101,date,begin_time,end_time))return;
						if(!flag) list.remove(i--);//continue;
					}
					if(checkIsAppend(id,r4101,list.size(),len)){
						tb.addClassPlanColumn(this.getFrameconn());//添加字段minute 每课时分钟数
						for (int i = 0; i < list.size(); i++) {
							String date=(String) list.get(i);
							RecordVo vo=new RecordVo("tr_classplan");
							int t_id = getMaxID();
							vo.setInt("id", ++t_id);
							vo.setString("r4101", r4101);
							vo.setDate("train_date", date);
							vo.setString("begin_time", begin_time);
							vo.setString("end_time", end_time);
							vo.setDouble("class_len", len);
							vo.setString("begin_card", begin_card);
							vo.setString("end_card", end_card);
							vo.setDouble("minute", Double.valueOf(minuteStr).doubleValue());
							dao.addValueObject(vo);
						}
						this.getFormHM().put("mess", "success");
					}
				}else
					this.getFormHM().put("mess", ResourceFactory.getProperty("train.b_plan.classplan.savemess1"));
			}else{//id!=0 修改排班信息
				try {
					if(checkIsAppend(id,r4101,1,len)&&checkIsRepeat(id,r4101,start_date,begin_time,end_time)){
						tb.addClassPlanColumn(this.getFrameconn());//添加字段minute 每课时分钟数
						RecordVo vo=new RecordVo("tr_classplan");
						vo.setInt("id", Integer.parseInt(id));
						vo.setString("r4101", r4101);
						vo.setDate("train_date", start_date);
						vo.setString("begin_time", begin_time);
						vo.setString("end_time", end_time);
						vo.setDouble("class_len", len);
						vo.setString("begin_card", begin_card);
						vo.setString("end_card", end_card);
						vo.setDouble("minute", Double.valueOf(minuteStr).doubleValue());
						dao.updateValueObject(vo);
						this.getFormHM().put("mess", "success");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public double getClass_len(String begin_time,String end_time,double minute){
		String date="2011-03-08";//临时测试用 参数
		SimpleDateFormat d= new SimpleDateFormat("yyyy-MM-dd HH:mm");//格式化时间
		double result=0;
		try {
	        result=(d.parse(date+" "+end_time).getTime()-d.parse(date+" "+begin_time).getTime())/1000;//当前时间减去测试时间   这个的除以1000得到秒，
	        result=result/60/minute;//相应的60000得到分，minute得到课时
	        if(result<=0){
	        	this.getFormHM().put("mess", ResourceFactory.getProperty("train.b_plan.classplan.savemess2"));
	        }
		} catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return result;
	}
	
	/**
	 * 判断排班课时是否超过未排班课时
	 * @param r4101
	 * @param dayNum 天数
	 * @param class_len 要添加的课时
	 * @return boolean
	 */
	public boolean checkIsAppend(String id,String r4101,int dayNum,double class_len){
		boolean flag=false;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			double lenNum=0;
			String sql="select sum(class_len) num from tr_classplan where r4101='"+r4101+"'";
			if(!"0".equals(id))
				sql+=" and id <> "+id;
			this.frecset=dao.search(sql);
			if(this.frecset.next()){
				lenNum=this.frecset.getDouble("num");
			}
			TrainAtteBo tb=new TrainAtteBo();
			String[] obj=tb.getR41Info(this.getFrameconn(), r4101);
			double item=lenNum+class_len*dayNum;
			if(item>Double.parseDouble(obj[2]))
				this.formHM.put("mess", ResourceFactory.getProperty("train.b_plan.classplan.savemess3"));
			else
				flag=true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 判断是否有重复时间段
	 * @param r4101
	 * @param cur_date 当前时间
	 * @param begin_time
	 * @param end_time
	 * @return boolean true=否,false=是
	 */
	public boolean checkIsRepeat(String id,String r4101,String cur_date,String begin_time,String end_time){
		boolean flag=false;
		StringBuffer sqlstr=new StringBuffer();
		sqlstr.append("select id from tr_classplan");
		sqlstr.append(" where r4101='"+r4101+"'");
		if(!"0".equals(id))
			sqlstr.append(" and id <> "+id);
		sqlstr.append(" and train_date = "+Sql_switcher.dateValue(cur_date)+"");
		sqlstr.append(" and ('"+begin_time+"' between begin_time and end_time");
		sqlstr.append(" or '"+end_time+"' between begin_time and end_time)");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			this.frecset=dao.search(sqlstr.toString());
			if(this.frecset.next())
				this.getFormHM().put("mess", ResourceFactory.getProperty("train.b_plan.classplan.savemess4"));
			else
				flag=true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 添加时间范围和培训课程时间比较
	 * @param r4101
	 * @param start_date
	 * @param stop_date
	 * @return
	 */
	public boolean checkAddTime(String r4101,String start_date,String stop_date){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		boolean flag=true;
		Date begin_time=null;
		Date end_time=null;
		try {
			String sql="select r4108,r4110 from r41 where r4101='"+r4101+"'";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frecset=dao.search(sql);
			if(this.frecset.next()){
				begin_time=this.frecset.getDate("r4108");
				end_time=this.frecset.getDate("r4110");
			}
			Date date1 = (Date)format.parseObject(start_date);
			Date date2 = (Date)format.parseObject(stop_date);
			begin_time = begin_time == null || "".equals(begin_time)?date1:begin_time;
			end_time = end_time == null || "".equals(end_time)?date2:end_time;
			if(date1.before(begin_time)||date2.after(end_time)){
				this.formHM.put("mess", ResourceFactory.getProperty("train.b_plan.classplan.savemess5"));
				flag=false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	private int getMaxID(){
		String sql = "SELECT MAX(ID) id FROM tr_classplan";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				return this.frowset.getInt("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	/**
	 * 修改时间格式
	 * <p>Create Time:2012-11-1 下午3:00:39</p>
	 * <p>@author:jianc</p>
	 */
	private String recomposeDate(String date){
		String[] str = date.split(":");
		for (int i = 0; i < str.length; i++) {
			try {
				if(str[i].length()<2)
					str[i] = "0" + str[i];
			} catch (Exception e) {
				return "00:00";
			}
		}
		return str[0]+":"+str[1];
	}
}
