package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
/**
 *  
 * <p>Title:InitReFfGzTrans.java</p>
 * <p>Description:展现重发工资设置界面</p> 
 * <p>Company:hjsj</p> 
 * create time at:2013-10-8 上午09:42:00 
 * @author dengcan
 * @version 6.x
 */
public class InitReFfGzTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{ 
			String count="";
			String salaryid=(String)this.getFormHM().get("salaryid");//薪资类别id
			salaryid=SafeCode.decode(salaryid); //解码
			salaryid =PubFunc.decrypt(salaryid); //解密
			
			String bosdate=(String)this.getFormHM().get("bosdate");//处理业务日期
			String oper = (String)this.getFormHM().get("oper");//操作类型
			SalaryAccountBo salaryAccountBo=new SalaryAccountBo(this.getFrameconn(),this.userView,Integer.parseInt(salaryid));
			//当前薪资类别在薪资历史数据表中已发放的历史业务日期列表
			ArrayList datelist=salaryAccountBo.getSubDateList();
			if(StringUtils.isBlank(bosdate) && datelist.size()>0)
			{
				bosdate=((CommonData)datelist.get(0)).getDataValue();  
			}

			if("count".equals(oper)){
				ArrayList countlist=new ArrayList();
				if(StringUtils.isNotBlank(bosdate))
				{
					countlist=salaryAccountBo.getRfCountList(bosdate);
				}
				
				this.getFormHM().put("countlist", countlist);
			}else{
				this.getFormHM().put("datelist",datelist);
			}
			
		}
		catch(Exception e)
		{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
		}
	}

}
