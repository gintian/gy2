package com.hjsj.hrms.transaction.train.exchange;

import com.hjsj.hrms.businessobject.train.point.TrainPointBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class ExchangeAccountTrans extends IBusiness {

	public void execute() throws GeneralException {
		//id，名称，兑换积分，剩余数量，以兑数量，状态
		String columns="id,r5701,r5703,r5705,ncount,npoint";
		String strsql="select ae.id,ed.r5701,r5703,r5705,ed.ncount,ed.npoint";
		String strwhere = "from tr_exchange_detail ed left join tr_award_exchange ae on id=exchange_id left join r57 r on r.R5701=ed.R5701 where R5713='04' and flag=0 and nbase='"+this.userView.getDbname()+"' and A0100='"+this.userView.getA0100()+"'";
		//System.out.println(strsql+strwhere);
		int userPoint = TrainPointBo.getUsablePoint(this.frameconn, this.userView.getDbname(), this.userView.getA0100());
		//System.out.println("r5703:"+getR5703());
		
		this.formHM.put("r5703", getR5703());
		this.formHM.put("usable_npoint", String.valueOf(userPoint));
		this.formHM.put("npoint", summarizing(strwhere)+"");
		this.formHM.put("columns", columns);
		this.formHM.put("strsql", strsql);
		this.formHM.put("strwhere", strwhere);
		this.formHM.put("order_by", " order by createtime desc");
	}
	
	private int summarizing(String strwhere){
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		int count = 0;
		int point = 0;
		try {
			int id = 0;
			String sql = "select id,sum(ed.ncount) count,sum(ed.npoint) point " + strwhere + " group by id";
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				id = this.frowset.getInt("id");
				count = this.frowset.getInt("count");
				point = this.frowset.getInt("point");
			}
			dao.update("update tr_award_exchange set ncount="+count+",npoint="+point+" where id="+id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return point;
	}
	
	private String getR5703(){
		String r5703 = "";
		String sql = "select ae.id,ed.r5701,r5703,r5705,ed.ncount,ed.npoint from tr_exchange_detail ed left join tr_award_exchange ae on id=exchange_id left join r57 r on r.R5701=ed.R5701 where R5713='04' and flag=0 and nbase='"+this.userView.getDbname()+"' and A0100='"+this.userView.getA0100()+"'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				r5703 = this.frowset.getString("r5703");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return r5703;
	}
}