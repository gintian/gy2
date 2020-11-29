package com.hjsj.hrms.transaction.gz.gz_accounting.voucher;

import com.hjsj.hrms.businessobject.gz.GzVoucherBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Calendar;

/** 
 * <p>Title:CreateVoucherDataTrans.java</p>
 * <p>Description>:生成凭证数据</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Aug 9, 2012 10:36:05 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author:dengc
 */
public class CreateVoucherDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{ 
			String type=(String)this.getFormHM().get("type");
			String year=(String)this.getFormHM().get("year");
			String month=(String)this.getFormHM().get("month");
			String count=(String)this.getFormHM().get("count");
			String voucher_date=(String)this.getFormHM().get("voucher_date");
			String deptcode=(String)this.getFormHM().get("deptcode");
			String voucher_id=(String)this.getFormHM().get("voucher_id");
			String oper=(String)this.getFormHM().get("oper");
			GzVoucherBo bo=new GzVoucherBo(this.getFrameconn(),this.getUserView());
			String flag=(String)this.getFormHM().get("flag");  //1：本单位  2：下一级单位  3:都包含
			
			
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			if(!safeBo.isVoucherPriv(voucher_id))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.acount.noVoucherAuthority")+"!"));
			
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if("1".equals(type)){
				String temp="";
				if("1".equals(flag)|| "3".equals(flag)) //当前选中机构
				{
					temp=deptcode;
				}
				if("2".equals(flag)|| "3".equals(flag)) //下一级机构
				{
					Calendar d=Calendar.getInstance();
					int today=d.get(Calendar.YEAR)*10000+(d.get(Calendar.MONTH)+1)*100+d.get(Calendar.DATE);
					String _sql="select codeitemid from organization where ( codesetid='UN' or  codesetid='UM' ) and parentid='"+deptcode+"'";
					_sql+=" and "+Sql_switcher.year("start_date")+"*10000+"+Sql_switcher.month("start_date")+"*100+"+Sql_switcher.day("start_date")+"<="+today;
					_sql+=" and "+Sql_switcher.year("end_date")+"*10000+"+Sql_switcher.month("end_date")+"*100+"+Sql_switcher.day("end_date")+">="+today;
					this.frowset=dao.search(_sql);
					while(this.frowset.next())
					{
						temp+=","+this.frowset.getString("codeitemid");
					}
					
				}
				
				String[] temps=temp.split(",");
				for(int n=0;n<temps.length;n++)
				{
					if(temps[n].trim().length()>0)
					{
						if(AdminCode.getCode("UM",temps[n].trim())==null)
							deptcode="UN"+temps[n].trim();
						else
							deptcode="UM"+temps[n].trim();
						bo.CreateVoucherData(year,month,count,voucher_date,deptcode,voucher_id);
					}
				}
			}else{
				if(AdminCode.getCode("UM",deptcode)==null)
					deptcode="UN"+deptcode;
				else
					deptcode="UM"+deptcode;
				
				String url =bo.collectData(year, month, voucher_date, deptcode, voucher_id, oper);
				this.getFormHM().put("url", PubFunc.encrypt(url));
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
