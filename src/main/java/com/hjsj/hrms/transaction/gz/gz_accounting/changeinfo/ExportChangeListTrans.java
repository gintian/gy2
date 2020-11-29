package com.hjsj.hrms.transaction.gz.gz_accounting.changeinfo;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:导出变动比对信息</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 10, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class ExportChangeListTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String salaryid=(String)this.getFormHM().get("salaryid");
			String opt=(String)this.getFormHM().get("opt");  //add  del  change  stop
			SalaryTemplateBo templatebo=new SalaryTemplateBo(this.getFrameconn());
			SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryid));
			String rightvalue = "";
				if("add".equals(opt)){
					rightvalue=ctrl_par.getValue(SalaryCtrlParamBo.ADD_MAN_FIELD);
				}else if("del".equals(opt)){
					rightvalue=ctrl_par.getValue(SalaryCtrlParamBo.DEL_MAN_FIELD);
				}
				

			String fieldstr=(String)this.getFormHM().get("fieldstr");
			if(fieldstr==null)
				fieldstr="null";
			String filterid=(String)this.getFormHM().get("filterid");
			if(filterid==null)
				filterid="null";
			if(!"null".equalsIgnoreCase(filterid)&&!"all".equalsIgnoreCase(filterid)&&!"new".equalsIgnoreCase(filterid))
			{
				RecordVo vo = new RecordVo("gzitem_filter");
				vo.setString("id", filterid);
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				vo = dao.findByPrimaryKey(vo);
				fieldstr=vo.getString("cfldname");
			}
			SalaryTemplateBo bo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.getUserView());
			String str="dbname,b0110,e0122,a0101,state,a0100";
			if("del".equalsIgnoreCase(opt))
				str="dbname,b0110,e0122,a0101,state,a0100";
			else if("stop".equalsIgnoreCase(opt))
				str="dbname,b0110,e0122,a0101,state,a0100,a01z0";
			else if("change".equalsIgnoreCase(opt))
			{
				str="dbname,b0110,e0122,a0101,state,b01101,e01221,a01011,a0100";
				ArrayList fieldItemList=bo.getField(SalaryCtrlParamBo.COMPARE_FIELD);
				StringBuffer buf = new StringBuffer("");
				for(int i=0;i<fieldItemList.size();i++)
				{
					FieldItem item = (FieldItem)fieldItemList.get(i);
					if(!"null".equalsIgnoreCase(filterid)&&!"all".equalsIgnoreCase(filterid)&&!"dbname".equalsIgnoreCase(item.getItemid())&&!"b0110".equalsIgnoreCase(item.getItemid())
							&&!"e0122".equalsIgnoreCase(item.getItemid())&&!"a0101".equalsIgnoreCase(item.getItemid())
							&&!"state".equalsIgnoreCase(item.getItemid())&&!"a0100".equalsIgnoreCase(item.getItemid()))
					{
						if(fieldstr.toUpperCase().indexOf(item.getItemid().toUpperCase())==-1)
							continue;
					}
					buf.append(","+item.getItemid());
				}
				str+=buf.toString();
			}
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
			String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
			if(uniquenessvalid!=null&&!"0".equals(uniquenessvalid)&&!"".equals(uniquenessvalid)&&bo.isAddColumn(onlyname, str))
		    	bo.setOnlyField(onlyname);
			if(rightvalue!=null&&!"".equals(rightvalue)){
				ArrayList list = templatebo.getadd_delList(rightvalue,salaryid,onlyname);//获取新增或减少人员的指标  搜房网  zhaoxg 2013-11-14
				bo.setAdd_delList(list);
			}
			ArrayList headList=bo.getChangeInfoHeadList(opt);
			ArrayList dataList=bo.getChangeInfoDataList(opt,salaryid,this.userView.getUserName(),fieldstr,filterid);
			String excelName=bo.createExcel(opt,headList,dataList,filterid,fieldstr,this.getUserView());
			this.getFormHM().put("fileName",SafeCode.encode(PubFunc.encrypt(excelName)));
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
