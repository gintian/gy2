package com.hjsj.hrms.transaction.gz.gz_accounting.voucher;

import com.hjsj.hrms.businessobject.gz.GzVoucherBo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchGzVoucherTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hmMap=(HashMap)this.getFormHM().get("requestPamaHM");
			String a_code=(String)hmMap.get("a_code");
			String _code="";
			if(a_code==null|| "UN".equalsIgnoreCase(a_code))
				_code="";
			else
				_code=a_code;
			String voucher_id=(String)this.getFormHM().get("voucher_id");
			String timeInfo=(String)this.getFormHM().get("timeInfo");
			String status=(String)this.getFormHM().get("status");
			String dbilltimes=(String)this.getFormHM().get("dbilltimes"); // 发放次数
			DbWizard dbw = new DbWizard(this.frameconn);
			if(!dbw.isExistField("GZ_Warrant", "b0110", false)){
				Table table=new Table("GZ_Warrant");
				Field field=new Field("b0110","b0110");
				field.setDatatype(DataType.STRING);
				field.setLength(30);
				table.addField(field);	
				dbw.addColumns(table);
				DBMetaModel dbmodel=new DBMetaModel(this.frameconn);
				dbmodel.reloadTableModel("GZ_Warrant");	
			}
			GzVoucherBo bo=new GzVoucherBo(this.getFrameconn(),this.getUserView());
			ArrayList _voucherList=bo.getVoucherList();
			
			//update by xiegh on date 20180613 测试提：bug34756  实际 还有一种情况就是未定义财务凭证
			if(_voucherList.size()==0)
				throw GeneralExceptionHandler.Handle(new Exception("资源分配中未授权凭证中所需的薪资类别！"));
			
			ArrayList voucherList=bo.getVoucherDataList(_voucherList);
			if((voucher_id==null||voucher_id.trim().length()==0)&&_voucherList.size()>0)
				voucher_id=(String)((LazyDynaBean)_voucherList.get(0)).get("pn_id");
			
			LazyDynaBean abean=bo.getVoucherBean(voucher_id,_voucherList);
			String type=(String)abean.get("type"); // 凭证财务接口 1 或 空 ：财务凭证 	2：按月汇总
			ArrayList statusList=bo.getStatusList(type);
			ArrayList dbilltimesList=bo.getDbilltimesList(voucher_id,timeInfo);
			ArrayList timeList=bo.getTimeList(voucher_id,type);
			
			if(timeInfo!=null&&timeInfo.length()>0&&timeList.size()>1)
			{
				boolean flag=false;
				for(int i=0;i<timeList.size();i++)
				{
					if(((CommonData)timeList.get(i)).getDataValue().equalsIgnoreCase(timeInfo))
						flag=true;
				}
				if(!flag)
					timeInfo="";
			}
			
			if((timeInfo==null||timeInfo.trim().length()==0)&&timeList.size()>1)
			{
				timeInfo= ((CommonData)timeList.get(1)).getDataValue();
			}
			
			
			
			if(status==null||status.trim().length()<=0)
				status="all";
			if(dbilltimes==null||dbilltimes.trim().length()<=0)
				dbilltimes="all";
			if("1".equals(type))
			{
				ArrayList headList=bo.getVoucherItems(abean);
				ArrayList voucherInfoList=bo.getvoucherInfoList(status,timeInfo,voucher_id,headList,_code,dbilltimes);
				this.getFormHM().put("voucherInfoList",voucherInfoList);
				this.getFormHM().put("headList",headList);
			}
			else
			{
				ArrayList headList = bo.getMonthCollectHeadList(voucher_id);
				ArrayList dataList=bo.getMonthCollectInfoList(status, timeInfo, voucher_id, headList, _code);
				this.getFormHM().put("voucherInfoList",dataList);
				this.getFormHM().put("headList",headList);
			}
			if(_code.length()>0)
			{
				this.getFormHM().put("_code", _code);
			}
			this.getFormHM().put("type",type);
			this.getFormHM().put("timeList",timeList);
			this.getFormHM().put("timeInfo", timeInfo);
			this.getFormHM().put("statusList",statusList);
			this.getFormHM().put("status",status);
			this.getFormHM().put("dbilltimesList",dbilltimesList);
			this.getFormHM().put("dbilltimes",dbilltimes);
			this.getFormHM().put("voucherList",voucherList);
			this.getFormHM().put("voucher_id", voucher_id);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
