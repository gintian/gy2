package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class GradeOpinionEditTrans  extends IBusiness{
	/**
	 * 30200710261
	 * <p>Title:GradeOpinionEditTrans.java</p>
	 * <p>Description>:目标卡多人评分操作评价</p>
	 * <p>Company:HJSJ</p>
	 * <p>@author: zhanghua
	 */
	@Override
	public void execute() throws GeneralException {
		try {
			String plan_id=(String)this.getFormHM().get("plan_id");
			plan_id=PubFunc.decrypt(SafeCode.decode(plan_id));
			ContentDAO dao=new ContentDAO(this.frameconn);
			String strP0400=(String)this.getFormHM().get("strP0400");
			strP0400=PubFunc.decrypt(SafeCode.decode(strP0400));
			String object_id=(String)this.getFormHM().get("object_id");
			object_id=PubFunc.decrypt(SafeCode.decode(object_id));
			String type=(String)this.getFormHM().get("type");//type 1:加载评论 2:添加评论 3:删除评论
			
			if("1".equals(type)){
				StringBuffer strSql=new StringBuffer();
				strSql.append("select p.a0100, p.a0101,"+Sql_switcher.dateToChar("p.Create_date", "yyyy-mm-dd,hh24:mi:ss")+" as Create_date,opinion ");//暂时只支持在职库
				strSql.append(" from per_grade_members_Opinion p where p0400=? order by Create_date ");
				ArrayList list=new ArrayList();
				list.add(strP0400);
				RowSet rs = dao.search(strSql.toString(),list);
				ArrayList<LazyDynaBean> opinion=new ArrayList<LazyDynaBean>();
				list=new ArrayList();
				while(rs.next()){
					LazyDynaBean bean=new LazyDynaBean();
					bean.set("name", rs.getString("a0101"));
					bean.set("time", rs.getString("Create_date"));
					bean.set("value", rs.getString("opinion"));
					bean.set("ismyself", this.userView.getA0100().equals(rs.getString("a0100"))?"1":"0");
					list.add(bean);
					
				}
				this.getFormHM().put("opinion", list);
				
			}else if("2".equals(type)){//添加评论
				String content=(String)this.getFormHM().get("content");
				content=SafeCode.decode(content);
				
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
				String date=df.format(new Date());// 不使用数据库的时间是因为需要向前台渲染，不想再从数据库取
				StringBuffer strSql=new StringBuffer();
				strSql.append("insert into per_grade_members_Opinion(P0400,NBASE,A0101,A0100,Opinion,Create_date) values(?,?,?,?,?,");
				
				if(Sql_switcher.searchDbServer() == Constant.ORACEL)
					strSql.append(" to_date('"+date+"','yyyy-mm-dd,hh24:mi:ss') ");
				else
					strSql.append(Sql_switcher.charToDate("'"+date+"'"));
				strSql.append(")");
				
				ArrayList list=new ArrayList();
				list.add(strP0400);
				list.add(this.userView.getDbname());
				list.add(this.userView.getUserFullName());
				list.add(this.userView.getA0100());
				list.add(content);
				int i=dao.update(strSql.toString(),list);
				this.getFormHM().put("time", date);
				if(i>0){//添加成功
					this.getFormHM().put("isok", '1');
					//修改代办状态
					
					HashMap map=PerformanceImplementBo.isHavePendingtask(this.userView.getDbname()+this.userView.getA0100(),this.frameconn,"PERPJ_"+plan_id+"_"+PubFunc.encrypt(object_id));
					if(map!=null&&map.size()>0&&!"1".equals(map.get("pending_status"))) {
						strSql.setLength(0);
						strSql.append("update t_hr_pendingtask set Pending_status='1',Lasttime="+Sql_switcher.dateValue(date)+"");
						strSql.append(" where Pending_type='"+33+"'");
						strSql.append(" and Receiver='" + this.userView.getDbname()+this.userView.getA0100() + "'");
						strSql.append(" and Pending_status='0' and pending_id="+map.get("pending_id")+"");
						dao.update(strSql.toString());
						
						PendingTask pt = new PendingTask();
						pt.updatePending("P", "PER"+map.get("pending_id"), 1, "评价目标卡", this.userView);
						
					}
				}
				else
					this.getFormHM().put("isok", '0');
				
				
			}else if("3".equals(type)){//仅能删除自己的评论
				String time=(String)this.getFormHM().get("time");
				time=SafeCode.decode(time);
				StringBuffer strSql=new StringBuffer();
				strSql.append("delete from per_grade_members_Opinion where P0400=? and  a0100=? and upper(nbase) =? and Create_date=");
				
				if(Sql_switcher.searchDbServer() == Constant.ORACEL)
					strSql.append(" to_date(?,'yyyy-mm-dd,hh24:mi:ss') ");
				else
					strSql.append(Sql_switcher.charToDate("?"));
				
				ArrayList list=new ArrayList();
				list.add(strP0400);
				list.add(userView.getA0100());
				list.add(userView.getDbname().toUpperCase());
				list.add(time);
				int i=dao.update(strSql.toString(),list);
				if(i>0){
					this.getFormHM().put("isok", '1');//前台判定是否更新成功
				}else
					this.getFormHM().put("isok", '0');
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
