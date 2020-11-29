package com.hjsj.hrms.transaction.train.exchange;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.point.TrainPointBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class IntegralCheckTrans extends IBusiness {

	public void execute() throws GeneralException {
		String mess = "ok";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer sql = new StringBuffer();
		sql.append("select ed.ncount,ed.npoint,r5703,r5707");
		sql.append(" from tr_exchange_detail ed left join tr_award_exchange ae on id=exchange_id left join r57 r on r.R5701=ed.R5701");
		sql.append(" where R5713='04' and flag=0");
		sql.append(" and nbase='"+this.userView.getDbname()+"'");
		sql.append(" and a0100='"+this.userView.getA0100()+"'");

		try {
			int point = 0;
			int cur_point = TrainPointBo.getUsablePoint(this.frameconn, this.userView.getDbname(), this.userView.getA0100());
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next()){
				point += this.frowset.getInt("npoint");
				int ncount = this.frowset.getInt("ncount");
				int r5707 = this.frowset.getInt("r5707");
				//System.out.println("兑换数量ncount:"+ncount);
				//System.out.println("剩余数量r5707:"+r5707);
				if(ncount>r5707 && r5707 != 0){
					mess = SafeCode.encode("奖品“"+this.frowset.getString("r5703")+"”数量不足，请重新选择奖品进行兑换!");
					break;
				}else if(r5707 == 0){
					mess = SafeCode.encode("奖品“"+this.frowset.getString("r5703")+"“已被兑完,请重新选择奖品进行兑换!");
				}
			}
			//System.out.println("curpoint:"+cur_point);
			//System.out.println("point:"+point);
			if((cur_point - point) < 0){
				mess = SafeCode.encode("当前积分不足兑换列表中的奖品，请重新选择奖品进行兑换！");
			}
			if(cur_point<point)
				mess = SafeCode.encode("当前积分不足兑换列表中的奖品，请重新选择奖品进行兑换！");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if("ok".equals(mess)){
			submitEdit();
		}
		this.getFormHM().put("mess", mess);
	}
	
	//提交(须提交前先验证奖品数量及所需积分是否匹配)
	private void submitEdit(){
		TrainCourseBo bo = new TrainCourseBo(this.getFrameconn());
		StringBuffer sql = new StringBuffer();
		sql.append("select id,ed.R5701,ed.ncount,ed.npoint");
		sql.append(" from tr_exchange_detail ed left join tr_award_exchange ae on id=exchange_id left join r57 r on r.R5701=ed.R5701");
		sql.append(" where R5713='04' and flag=0");
		sql.append(" and nbase='"+this.userView.getDbname()+"'");
		sql.append(" and a0100='"+this.userView.getA0100()+"'");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			int tmp = 0;
			int point = 0;//消费积分
			ArrayList list = new ArrayList();
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next()){
				int id = this.frowset.getInt("id");
				int r5701 = this.frowset.getInt("r5701");
				int ncount = this.frowset.getInt("ncount");
				int npoint = this.frowset.getInt("npoint");
				point += npoint;
				if(tmp==0){
					list.add("update tr_award_exchange set flag=1 where id="+id);
				}
				list.add("update r57 set r5707=r5707-"+ncount+",r5709="+Sql_switcher.isnull("r5709", "0")+"+"+ncount+" where r5701="+r5701);
				tmp++;
			}
			//参数配置中的sql 减去用户消费积分
			String subset = bo.getTrparam("/param/point_set", "subset");
			String cur_point_field = bo.getTrparam("/param/point_set", "cur_point_field");
			String used_point_field = bo.getTrparam("/param/point_set", "used_point_field");
			sql.setLength(0);
			sql.append("update "+this.userView.getDbname()+subset);
			sql.append(" set "+cur_point_field+"="+cur_point_field+"-"+point);
			sql.append(","+used_point_field+"="+Sql_switcher.isnull(used_point_field, "0")+"+"+point);
			sql.append(" where a0100='"+this.userView.getA0100()+"'");
			list.add(sql.toString());
			
			dao.batchUpdate(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}