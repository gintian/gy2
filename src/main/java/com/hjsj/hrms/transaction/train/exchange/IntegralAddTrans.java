package com.hjsj.hrms.transaction.train.exchange;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.Date;

public class IntegralAddTrans extends IBusiness {

	public void execute() throws GeneralException {
		String mess = "ok";
		String r5701s = (String)this.getFormHM().get("r5701");
		//int r5707 = Integer.parseInt(this.getFormHM().get("r5707").toString());
		
		String msg = "";
		if(r5701s==null||r5701s.length()<1)
			return;
		
		String[] r5701 = r5701s.split(",");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			RecordVo rv = null;
			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			int flag = 1;//该订单是否存在0为提交订单
			int id = -1;
			int count = 0 ;
			int r5707 = 0 ;
			String prizes = "";
			this.frowset = dao.search("select id from tr_award_exchange where flag=0 and nbase='"+this.userView.getDbname()+"' and a0100='"+this.userView.getA0100()+"'");
			if(this.frowset.next()){
				id = this.frowset.getInt("id");
				flag = 0;
			}else
				id = Integer.parseInt(idg.getId("tr_award_exchange.id"));
				
			for(int j = 0 ; j < r5701.length ; j ++){
			String str = " select ed.ncount,r5707,r5703 from tr_exchange_detail ed left join tr_award_exchange ae on id=exchange_id left join r57 r on r.R5701=ed.R5701 where R5713='04' and flag=0 and id = "+id+" and r.r5701 = "+PubFunc.decrypt(SafeCode.decode(r5701[j]));
			this.frowset = dao.search(str);
			if(this.frowset.next()){
				count = this.frowset.getInt("ncount");
				r5707 = this.frowset.getInt("r5707");
				prizes = this.frowset.getString("r5703");
				if(r5707 < count+1){
					msg = "奖品"+prizes+"数量不足,请选择其他奖品!";
					this.getFormHM().put("msg", msg);
				}
			}
			}
			if(!"".equals(msg) && msg != null){
				msg = "奖品"+prizes+"数量不足,请选择其他奖品!";
				this.getFormHM().put("msg", msg);
			}else{
			int ncount = 0;
			int npoint = 0;
			for (int i = 0; i < r5701.length; i++) {
			    String r57id = PubFunc.decrypt(SafeCode.decode(r5701[i]));
				int r5705 = getNpoint(r57id);
				ncount += 1;
				npoint += r5705;
				rv = new RecordVo("tr_exchange_detail");
				rv.setInt("exchange_id", id);
				rv.setInt("r5701", Integer.parseInt(r57id));
				if(flag==0&&dao.isExistRecordVo(rv)){
					dao.update("update tr_exchange_detail set ncount=ncount+1,npoint=npoint+"+r5705+" where exchange_id="+id+" and r5701="+r57id);
					continue;
				}
				rv.setInt("ncount", 1);
				rv.setInt("npoint", r5705);
				dao.addValueObject(rv);
			}
			
			if(flag==1){
				rv = new RecordVo("tr_award_exchange");
				rv.setInt("id", id);
				rv.setString("nbase", this.userView.getDbname());
				rv.setString("a0100", this.userView.getA0100());
				rv.setString("a0101", this.userView.getUserFullName());
				rv.setString("b0110", this.userView.getUserOrgId());
				rv.setString("e0122", this.userView.getUserDeptId());
				rv.setString("e01a1", this.userView.getUserPosId());
				rv.setInt("ncount", ncount);
				rv.setInt("npoint", npoint);
				rv.setDate("exchangedtime", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
				rv.setInt("flag", 0);
				dao.addValueObject(rv);
			}else{
				dao.update("update tr_award_exchange set ncount=ncount+"+ncount+",npoint=npoint+"+npoint+" where id="+id);
			}
		  }
		} catch (SQLException e) {
			mess = "error";
			e.printStackTrace();
		}finally{
			this.getFormHM().put("mess", mess);
		}
	}
	
	private int getNpoint(String r5701){
		int npoint = 0;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select r5705 from r57 where r5701="+r5701;
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next())
				npoint = this.frowset.getInt("r5705");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return npoint;
	}
	
	
}
