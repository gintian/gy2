package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class QueryPersonResultTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			  String salaryid=(String)this.getFormHM().get("salaryid");
			  String model=(String)this.getFormHM().get("model");
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			  String type=map.containsKey("type")?(String) map.get("type"):"";//history 表示为薪资历史数据分析进入
			map.put("type","");
			String tableName="";
			ArrayList filterList = (ArrayList) this.getFormHM().get("personFilterList");
			HashMap fieldItemMap=new HashMap();
			BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
			if(!"history".equalsIgnoreCase(type)) {
				  tableName = this.userView.getUserName() + "_salary_" + salaryid;
				  SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(), Integer.parseInt(salaryid), this.userView);
				  String manager = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
				  String priv_mode = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PRIV_MODE, "flag");
				  if ("0".equals(model)) {
					  if (manager.length() == 0 || this.userView.getUserName().equalsIgnoreCase(manager))
						  tableName = this.userView.getUserName() + "_salary_" + salaryid;
					  else
						  tableName = manager + "_salary_" + salaryid;
				  } else
					  tableName = "SalaryHistory";
				  fieldItemMap = bo.getFieldItemMap(Integer.parseInt(salaryid), this.userView);


			  }else{
				tableName="salaryarchive";
				HistoryDataBo hbo=new HistoryDataBo(this.getFrameconn(),this.getUserView());
				for (Field field : hbo.searchAllGzItem()) {
					FieldItem item = new FieldItem();
					item.setCodesetid(field.getCodesetid());
					item.setUseflag("1");
					if(field.getDatatype()== DataType.DATE)
					{
						item.setItemtype("D");
					}
					else if(field.getDatatype()==DataType.STRING)
					{
						item.setItemtype("A");
					}
					else if(field.getDatatype()==DataType.INT||field.getDatatype()==DataType.FLOAT)
					{
						item.setItemtype("N");
					}
					else if(field.getDatatype()==DataType.CLOB)
					{
						item.setItemtype("M");
					}
					else
						item.setItemtype("A");
					item.setItemid(field.getName().toUpperCase());
					item.setAlign(field.getAlign());
					item.setItemdesc(field.getLabel());

					fieldItemMap.put(field.getName().toUpperCase(),item);
				}
			}
			for (int i = 0; i < filterList.size(); i++) {
				Factor factor = (Factor) filterList.get(i);
				factor.setOper(PubFunc.keyWord_reback(factor.getOper()));
		            /* 自助服务-员工信息-数据上报-星号和问号错误 xiaoyun 2014-10-25 start */
				String hzValue = factor.getHzvalue();
				if (hzValue != null && hzValue.trim().length() > 0) {
					hzValue = hzValue.replaceAll("％2A", "*");//换*
					hzValue = hzValue.replaceAll("％2a", "*");//换*
					hzValue = hzValue.replaceAll("＊", "*");
					hzValue = hzValue.replaceAll("？", "?");
				}
				String value = factor.getValue();
				if (value != null && value.trim().length() > 0) {
					value = value.replaceAll("％2A", "*");//换*
					value = value.replaceAll("％2a", "*");//换*
					value = value.replaceAll("＊", "*");
					value = value.replaceAll("？", "?");
				}
				factor.setValue(value);
				factor.setHzvalue(hzValue);
		            /* 自助服务-员工信息-数据上报-星号和问号错误 xiaoyun 2014-10-25 end */
			}
			StringBuffer factor = new StringBuffer();
			String sexpr = (String) this.getFormHM().get("expr");
			sexpr = PubFunc.keyWord_reback(sexpr);
			String sql = combineFactor(filterList, factor, sexpr, fieldItemMap, tableName);
			String filterCondId = (String) this.getFormHM().get("filterCondId");
			if(filterCondId==null|| "".equals(filterCondId))
				  filterCondId="new";
	          this.getFormHM().put("personFilterList",filterList);
	          this.getFormHM().put("filterSql",sql);
			  this.getFormHM().put("salaryid",salaryid);
			  this.getFormHM().put("tableName",tableName);
			  this.getFormHM().put("issave","1");
			  this.getFormHM().put("filterCondId",filterCondId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	private String combineFactor(ArrayList factorlist,/* String like,*/StringBuffer sfactor, String sexpr,HashMap fieldItemMap,String tableName) throws GeneralException
	{
		String sql ="";
		try
		{
		for(int i=0;i<factorlist.size();i++)
        {
            Factor factor=(Factor)factorlist.get(i);
          /*  if(i!=0)
            {
                sexpr.append(factor.getLog());
            }
            sexpr.append(i+1);*/
            sfactor.append(factor.getFieldname().toUpperCase());
            
            sfactor.append(factor.getOper());
            String q_value=factor.getValue().trim();
            if("M".equals(factor.getFieldtype()))
            {
            	if(!("".equals(q_value)))
            		sfactor.append("*");
            }
            sfactor.append(factor.getValue());  
            /**对字符型指标有模糊*/
            if("M".equals(factor.getFieldtype()))
            {
            	if(!("".equals(q_value)))
                    sfactor.append("*");
            }
            sfactor.append("`");            
        }
	     FactorList factor_bo=new FactorList(sexpr.toString(),sfactor.toString(),this.userView.getUserId(),fieldItemMap);
  	     sql=factor_bo.getSingleTableSqlExpression(tableName);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return sql;
	}


}
