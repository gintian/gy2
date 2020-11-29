package com.hjsj.hrms.transaction.train.exchange;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

public class ExchangeRecordTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
//		TrainCourseBo bo = new TrainCourseBo(this.frameconn);
		//陈旭光修改：兑换奖品界面点击根节点显示信息，上面注释为原来代码
		TrainCourseBo bo = new TrainCourseBo(this.userView);
		String a_code = (String) hm.get("a_code");
		String a0101 = (String)this.getFormHM().get("a0101");
		String searchstr = (String)this.getFormHM().get("searchstr");
		this.getFormHM().put("searchstr", "");
		String startdate = (String)this.getFormHM().get("startdate");
		String enddate = (String)this.getFormHM().get("enddate");
		
		String model = (String)this.getFormHM().get("model");
		
		//姓名，奖品名称，兑换日期，兑换数量，积分支出，状态
		String columns="id,nbase,a0100,a0101,b0110,e0122,e01a1,r5701,r5703,exchangedtime,ncount,npoint";
		String strsql="select id,nbase,a0100,a0101,ae.b0110,e0122,e01a1,ed.r5701,r5703,exchangedtime,ed.ncount,ed.npoint";
		StringBuffer strwhere = new StringBuffer();
		strwhere.append(" from tr_award_exchange ae,tr_exchange_detail ed,r57 r where ed.r5701=r.r5701 and exchange_id=ae.id and flag=1");
		if("3".equals(model)){
			strwhere.append(" and nbase='"+this.userView.getDbname()+"'");
			strwhere.append(" and a0100='"+this.userView.getA0100()+"'");
		}
		
		if((!this.userView.isSuper_admin())&&(a_code==null||a_code.length()<3)&&!"3".equals(model)){
			String priv = bo.getUnitIdByBusi();
			if (priv != null && priv.length() > 2) {
				String tmpwhere = bo.getPrivSql("", priv);
				String tmparr[] = priv.split("`");
				for (int i = 0; i < tmparr.length; i++) {
					String tmp = tmparr[i];
					if ("UN".equalsIgnoreCase(tmp.substring(0, 2)) && (tmpwhere == null || "".equals(tmpwhere))) {
						strwhere.append(" and (1=1)");
					} else if (tmpwhere.length() > 0) {
						strwhere.append(" and (ae." + tmpwhere + ")");
					} else
						strwhere.append(" and 1=2");
				}
			}
		}else if(a_code!=null&&a_code.length()>2&&!"3".equals(model)){
			String codeid = a_code.substring(0,2);
			String codesetid = a_code.substring(2);
			if("UN".equalsIgnoreCase(codeid))
				strwhere.append(" and ae.b0110 like '"+codesetid+"%'");
			else if("UM".equalsIgnoreCase(codeid))
				strwhere.append(" and ae.e0122 like '"+codesetid+"%'");
			else if("@K".equalsIgnoreCase(codeid))
				strwhere.append(" and ae.e01a1 like '"+codesetid+"%'");
			else if(a_code.length()>3)
				strwhere.append(" and nbase='"+a_code.substring(0,3)+"' and a0100='"+a_code.substring(3)+"'");
		}
		
		//查询条件
		if(!"3".equals(model)){
			if(a0101!=null&&a0101.trim().length()>0)
				strwhere.append(" and a0101 like '%"+a0101+"%'");
		}
		if(searchstr!=null&&searchstr.trim().length()>0)
			strwhere.append(" and r5703 like '%"+searchstr+"%'");
		if(startdate!=null&&startdate.trim().length()>0)
			strwhere.append(" and exchangedtime >= "+Sql_switcher.dateValue(startdate+" 00:00:00"));
		if(enddate!=null&&enddate.trim().length()>0)
			strwhere.append(" and exchangedtime <= "+Sql_switcher.dateValue(enddate+" 23:59:59"));
		
		getNpoint(strwhere.toString());
		
		this.formHM.put("columns", columns);
		this.formHM.put("strsql", strsql);
		this.formHM.put("strwhere", strwhere.toString());
		this.formHM.put("order_by", " order by exchangedtime desc");
	}
	
	private void getNpoint(String strwhere){
		int sum = 0;
		int count = 0;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search("select sum(ed.ncount) c,sum(ed.npoint) s"+strwhere);
			if(this.frowset.next()){
				count = this.frowset.getInt("c");
				sum = this.frowset.getInt("s");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			this.formHM.put("ncount", count+"");
			this.formHM.put("npoint", sum+"");
		}
	}
}