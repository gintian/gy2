package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class SelectPersonFilterFieldTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			
	        HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String salaryid=(String)map.get("salaryid");
			String model=(String)map.get("model");//history 表示为薪资历史数据分析进入
			ArrayList list = new ArrayList();
			ArrayList aList = new ArrayList();
			ArrayList selectedFieldList = new ArrayList();
			String tableName="";
			if(!"history".equalsIgnoreCase(model)) {
				SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(), Integer.parseInt(salaryid), this.userView);
				gzbo.synchronismSalarySet();
				gzbo.syncGzTableStruct();
				BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());

				/**共享的工资类别，用报审状态做查询指标，审批状态没有意义*/
				if (gzbo.isApprove())//||(gzbo.getManager()!=null&&!gzbo.getManager().equals(""))
					aList.add(new CommonData("SP_FLAG", ResourceFactory.getProperty("label.gz.sp")));
				if (gzbo.getManager() != null && !"".equals(gzbo.getManager()))
					aList.add(new CommonData("SP_FLAG2", "报审状态"));
				aList.add(new CommonData("A00Z1", "归属次数"));//搜房网需求，放开归属次数  zhaoxg add 2013-10-24


				list = bo.getAllItemList(salaryid);
				String a01z0Flag = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0, "flag");  // 是否显示停发标识  1：有
				for (int i = 0; i < list.size(); i++) {
					CommonData data = (CommonData) list.get(i);
					if ("a01z0".equalsIgnoreCase(data.getDataValue()) && (a01z0Flag == null || "0".equals(a01z0Flag)))
						continue;
					else {
						if (!"a00z0".equalsIgnoreCase(data.getDataValue()) && !"a00z1".equalsIgnoreCase(data.getDataValue()) && !"a00z2".equalsIgnoreCase(data.getDataValue()) && !"a00z3".equalsIgnoreCase(data.getDataValue())) {
							if (!(this.userView.isSuper_admin() || "1".equals(this.userView.getGroupId()))) {
								if ("0".equals(this.userView.analyseFieldPriv(data.getDataValue())) && !"nbase".equalsIgnoreCase(data.getDataValue()))//放开人员库标识  zhaoxg update 2015-6-24
									continue;
							}
						}
					}
					aList.add(data);
				}
				list = aList;

				/**共享的工资类别，用报审状态做查询指标，审批状态没有意义*/
				if (gzbo.isApprove())//||(gzbo.getManager()!=null&&!gzbo.getManager().equals(""))
					list.add(new CommonData("SP_FLAG", ResourceFactory.getProperty("label.gz.sp")));
				if (gzbo.getManager() != null && !"".equals(gzbo.getManager()))
					list.add(new CommonData("SP_FLAG2", "报审状态"));
				list.add(new CommonData("A00Z1", "归属次数"));//搜房网需求，放开归属次数  zhaoxg add 2013-10-24
				tableName = this.userView.getUserName() + "_salary_" + salaryid;

			}else{
				BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
				String [] salaryidList=salaryid.split(",");
				LinkedHashMap<String,CommonData> fieldItemMap=new LinkedHashMap<String, CommonData>();
				for (String salary_id : salaryidList) {
					if(StringUtils.isBlank(salary_id)){
						continue;
					}
					list=bo.getAllItemList(salary_id);

					for (int i = 0; i < list.size(); i++) {
						CommonData fieldItem=(CommonData)list.get(i);

						if(!fieldItemMap.containsKey(fieldItem.getDataValue())){
							fieldItemMap.put(fieldItem.getDataValue(),fieldItem);
						}
					}
				}

				Iterator iterator=fieldItemMap.entrySet().iterator();

				list.clear();
				while (iterator.hasNext()){
					Map.Entry entry=(Map.Entry)iterator.next();
					list.add(entry.getValue());
				}

				for (int i = 0; i < list.size(); i++) {
					CommonData data = (CommonData) list.get(i);
					if ("a01z0".equalsIgnoreCase(data.getDataValue()))
						continue;
					else {
						if (!"a00z0".equalsIgnoreCase(data.getDataValue()) && !"a00z1".equalsIgnoreCase(data.getDataValue()) && !"a00z2".equalsIgnoreCase(data.getDataValue()) && !"a00z3".equalsIgnoreCase(data.getDataValue())) {
							if (!(this.userView.isSuper_admin() || "1".equals(this.userView.getGroupId()))) {
								if ("0".equals(this.userView.analyseFieldPriv(data.getDataValue())) && !"nbase".equalsIgnoreCase(data.getDataValue()))//放开人员库标识  zhaoxg update 2015-6-24
									continue;
							}
						}
					}
					aList.add(data);
				}
				list=aList;

				tableName="salaryarchive";

			}
			this.getFormHM().put("allList",list);
			this.getFormHM().put("selectedFieldList",selectedFieldList);
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("tableName",tableName);
			this.getFormHM().put("filterCondId","");
			this.getFormHM().put("model", model);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	/*public ArrayList getSelectedList(String condId,String salaryid) throws GeneralException
	{
		ArrayList selectedlist=new ArrayList();
		ArrayList list = new ArrayList();
		try
		{
			BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
			String expr=bo.getCondXML(salaryid);
			if(expr==null||expr.equals(""))
				return list;
			SalaryLProgramBo lbo = new SalaryLProgramBo(expr);
			HashMap condMap = lbo.getServiceItemMap();
			String thisExpr=(String)condMap.get(condId);
			if(thisExpr==null||thisExpr.equals(""))
				return list;
			int idx=thisExpr.indexOf('|');
			String expression=expr.substring(0,idx);
			String strfactor=expr.substring(idx+1);
			FactorList factorlist=new FactorList(expression,strfactor,"");
			list=factorlist.getAllFieldList();
			
			if(!(list==null||list.size()==0))
			{
				for(int i=0;i<list.size();i++)
				{
					 FieldItem fielditem=(FieldItem)list.get(i);
				     CommonData dataobj = new CommonData(fielditem.getItemid(), fielditem.getItemdesc());;
				     selectedlist.add(dataobj);
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	
		return selectedlist;
	}*/

}
