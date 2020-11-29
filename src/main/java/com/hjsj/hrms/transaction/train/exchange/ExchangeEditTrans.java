package com.hjsj.hrms.transaction.train.exchange;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ExchangeEditTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		//up数量+1，down数量-1，del删除
		String flag = (String)hm.get("flag");
		hm.remove("flag");
		String id = (String)hm.get("id");
		id = PubFunc.decrypt(SafeCode.decode(id));
		hm.remove("id");
		String r5701 = (String)hm.get("r5701");
		r5701 = PubFunc.decrypt(SafeCode.decode(r5701));
		hm.remove("r5701");
		String count = (String)hm.get("count");
		hm.remove("count");
		
		if("del".equals(flag))
			delEdit(id, r5701);
		else
			countEdit(flag, id, r5701,count);
		
	}
	
	//删除
	private void delEdit(String id,String r5701){
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			String sql = "delete tr_exchange_detail where exchange_id="+id+" and r5701="+r5701;
			dao.delete(sql, new ArrayList());
			
			sql = "select 1 from tr_exchange_detail where exchange_id="+id;
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				
			}else{
				sql = "delete tr_award_exchange where id="+id;
				dao.delete(sql, new ArrayList());
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//修改数量
	private void countEdit(String flag,String id,String r5701,String count){
		String sign = "+";
		if("down".equalsIgnoreCase(flag))
			sign = "-";
		try {
			int ncount = 0;//兑换数量
			int r5705 = 0;//奖品积分
			int r5707 = 0;//奖品剩余个数
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			StringBuffer sql = new StringBuffer();
			sql.append("select ed.ncount,r5705,r5707");
			sql.append(" from tr_exchange_detail ed left join tr_award_exchange ae on id=exchange_id left join r57 r on r.R5701=ed.R5701");
			sql.append(" where R5713='04' and flag=0");
			sql.append(" and id="+id);
			sql.append(" and r.r5701="+r5701);
			this.frowset = dao.search(sql.toString());
			if(this.frowset.next()){
				ncount = this.frowset.getInt("ncount");
				r5705 = this.frowset.getInt("r5705");
				r5707 = this.frowset.getInt("r5707");
			}else
				return;
			
			if("change".equals(flag)){
				if(Integer.parseInt(count)>r5707)
					count = r5707+"";
				if(Integer.parseInt(count)<1)
					count = "1";
				
				sql.setLength(0);
				sql.append("update tr_exchange_detail set ncount="+count+",npoint="+(Integer.parseInt(count)*r5705));
				sql.append(" where exchange_id="+id+" and r5701="+r5701);
				dao.update(sql.toString());
				return;
			}
			
			if("down".equals(flag)&&ncount==1)
				return;
			
			if("up".equals(flag)&&ncount>=r5707)
				return;
			
			
			sql.setLength(0);
			sql.append("update tr_exchange_detail set ncount=ncount"+sign+"1,npoint=npoint"+sign+r5705);
			sql.append(" where exchange_id="+id+" and r5701="+r5701);
			dao.update(sql.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
