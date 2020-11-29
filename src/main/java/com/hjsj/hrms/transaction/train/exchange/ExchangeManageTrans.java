package com.hjsj.hrms.transaction.train.exchange;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ExchangeManageTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		TrainCourseBo bo = new TrainCourseBo(this.userView);
		String a_code = (String) hm.get("a_code");
		String searchstr = (String)this.getFormHM().get("searchstr");
		String r5713 = (String)this.getFormHM().get("r5713");
		
		//id，名称，兑换积分，剩余数量，以兑数量，归属单位，状态
		String columns="r5701,r5703,r5705,r5707,r5709,b0110,r5713";
		StringBuffer strwhere = new StringBuffer(" from r57 where 1=1");
		
		if(!this.userView.isSuper_admin()&&(a_code==null||a_code.length()<3)){
			String priv = bo.getUnitIdByBusi();
			if(priv!=null&&priv.length()>2){
			//	strwhere.append(" and (");
				String tmparr[] = priv.split("`");
				for(int i=0;i<tmparr.length;i++){
					String tmp = tmparr[i];
					if(tmp!=null&&tmp.length()>=2){
						if ("UN".equalsIgnoreCase(tmp.substring(0, 2)))
							strwhere.append(" and ( B0110 like '"
									+ tmp.substring(2, tmp.length()) + "%' or ");
					}
				}
				if(strwhere.length()>29){
					strwhere.setLength(strwhere.length()-3);
					strwhere.append(" or B0110 is null or B0110='' or B0110='HJSJ')");
				}else
					strwhere.append(" and 1=2");
			}
		}else if(a_code!=null&&a_code.length()>2){
			strwhere.append(" and b0110 like '"+a_code.substring(2)+"%' or b0110='HJSJ' ");
		}
		
		//查询条件
		if(searchstr!=null&&searchstr.trim().length()>0)
			strwhere.append(" and r5703 like '%"+searchstr+"%'");
		if(r5713!=null&&r5713.trim().length()>1&&!"00".equals(r5713))
			strwhere.append(" and r5713 = '"+r5713+"'");
		this.formHM.put("columns", columns);
		this.formHM.put("strsql", "select " + columns);
		this.formHM.put("strwhere", strwhere.toString());
		this.formHM.put("order_by", " order by createtime desc");
	}
	
}
