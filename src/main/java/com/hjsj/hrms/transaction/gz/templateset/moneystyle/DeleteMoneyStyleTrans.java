package com.hjsj.hrms.transaction.gz.templateset.moneystyle;

import com.hjsj.hrms.businessobject.gz.templateset.moneystyle.MoneyStyleSetBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class DeleteMoneyStyleTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String nstyleid=(String)this.getFormHM().get("selectID");
			
			MoneyStyleSetBo bo = new MoneyStyleSetBo(this.getFrameconn());
			bo.deteleMoneyStyleInfo(getIdsInfo(nstyleid),1);
			bo.deteleMoneyStyleInfo(getIdsInfo(nstyleid),2);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * 解析前台传入的id字符串
	 * @param ids
	 * @return
	 */
	public String getIdsInfo(String ids){
		StringBuffer retStr=new StringBuffer();
		try
		{
			if(ids.indexOf("#")==-1)
			{
				retStr.append("'");
				retStr.append(ids.substring(1));
				retStr.append("'");
			}
			else
			{
				String[] retArr=ids.substring(1).split("#");
				for(int i=0;i<retArr.length;i++)
				{
					retStr.append(",'");
					retStr.append(retArr[i]);
					retStr.append("'");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return retStr.toString();
	}

}
