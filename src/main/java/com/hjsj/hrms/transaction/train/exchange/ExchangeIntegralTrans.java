package com.hjsj.hrms.transaction.train.exchange;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.point.TrainPointBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class ExchangeIntegralTrans extends IBusiness {

	public void execute() throws GeneralException {
		String strwhere1 = "from tr_exchange_detail ed left join tr_award_exchange ae on id=exchange_id left join r57 r on r.R5701=ed.R5701 where R5713='04' and flag=0 and nbase='"+this.userView.getDbname()+"' and A0100='"+this.userView.getA0100()+"'";
		if (this.userView.getA0100() == null || this.userView.getA0100().length() <= 0) 
			throw GeneralExceptionHandler.Handle(new GeneralException("","非自助用户不能使用此功能！","",""));
		
		TrainCourseBo bo = new TrainCourseBo(this.frameconn);
		String subset = bo.getTrparam("/param/point_set", "subset");
		String cur_point_field = bo.getTrparam("/param/point_set", "cur_point_field");
		String used_point_field = bo.getTrparam("/param/point_set", "used_point_field");
		if(subset.length()<1||cur_point_field.length()<1||used_point_field.length()<1)
			throw GeneralExceptionHandler.Handle(new GeneralException("","未设置积分子集参数！请到培训管理->参数设置->其它参数->积分管理设置进行参数设置。","",""));
		
		String searchstr = (String)this.getFormHM().get("searchstr");
		this.getFormHM().put("searchstr", "");
		
		//id，名称，兑换积分，剩余数量，以兑数量，状态
		String columns="r5701,r5703,r5705,r5707,r5709";
		StringBuffer strwhere = new StringBuffer(" from r57 where r5713='04'");
		strwhere.append(" and (b0110 = '"+this.userView.getUserOrgId()+"' or b0110='HJSJ') ");
		strwhere.append(" and "+Sql_switcher.isnull("r5707", "0")+">0");//已经兑换完的奖品将不再列出
		//查询条件
		if(searchstr!=null&&searchstr.trim().length()>0) 
			strwhere.append(" and r5703 like '%"+searchstr+"%'"); 
		int userPoint = TrainPointBo.getUsablePoint(this.frameconn, this.userView.getDbname(), this.userView.getA0100());
		this.formHM.put("usable_npoint", String.valueOf(userPoint));
		this.formHM.put("columns", columns);
		this.formHM.put("strsql", "select " + columns);
		this.formHM.put("strwhere", strwhere.toString());
		this.formHM.put("order_by", " order by createtime desc");
		
		String counts = "";
		if(this.getFormHM().get("counts")!= null){			
			counts = this.getFormHM().get("counts").toString();
		}
		
		this.getFormHM().put("counts", counts);
		this.getFormHM().put("ccount", getCount(strwhere1)+"");
		this.getFormHM().put("npoint", summarizing(strwhere1)+"");
	}
	
	private int summarizing(String strwhere1){
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		int count = 0;
		int point = 0;
		try {
			int id = 0;
			String sql = "select id,sum(ed.ncount) count,sum(ed.npoint) point " + strwhere1 + " group by id";
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
	
	private int getCount(String strwhere1){
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		int count = 0;
		try {
			String sql = "select id,sum(ed.ncount) count,sum(ed.npoint) point " + strwhere1 + " group by id";
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				count = this.frowset.getInt("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}	
}
