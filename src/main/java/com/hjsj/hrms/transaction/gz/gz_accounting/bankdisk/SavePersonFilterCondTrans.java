package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
public class SavePersonFilterCondTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			ArrayList list = (ArrayList)this.getFormHM().get("personFilterList");
			String condName=(String)this.getFormHM().get("condName");
			String salaryid =(String)this.getFormHM().get("salaryid");
		    String condid=(String)this.getFormHM().get("filterCondId");
		    String expr=(String)this.getFormHM().get("expr");
		    String model=this.getFormHM().get("model")==null?"":(String)this.getFormHM().get("model");//history 表示为薪资历史数据分析进入
		    expr=PubFunc.keyWord_reback(expr);
		    /* 自助服务-员工信息-数据上报-星号和问号错误 xiaoyun 2014-10-25 start */
		    if(condName != null && condName.trim().length() > 0) {
			    condName = condName.replaceAll("％2A","*");//换*
			    condName = condName.replaceAll("％2a","*");//换*
			    condName = condName.replaceAll("＊", "*");
			    condName = condName.replaceAll("？", "?");
		    }
		    /* 自助服务-员工信息-数据上报-星号和问号错误 xiaoyun 2014-10-25 end */
		    for(int i=0;i<list.size();i++)
	        {
	            Factor factor=(Factor)list.get(i);
	            /* 自助服务-员工信息-数据上报-星号和问号错误 xiaoyun 2014-10-25 start */
	            String hzValue = factor.getHzvalue();
	            if(hzValue != null && hzValue.trim().length() > 0) {
	            	hzValue = hzValue.replaceAll("％2A","*");//换*
	            	hzValue = hzValue.replaceAll("％2a","*");//换*
		            hzValue = hzValue.replaceAll("＊", "*");
		            hzValue = hzValue.replaceAll("？", "?");
	            }	            
	            String value = factor.getValue();
	            if(value != null && value.trim().length() > 0) {
	            	value = value.replaceAll("％2A","*");//换*
		            value = value.replaceAll("％2a","*");//换*
		            value = value.replaceAll("＊", "*");
		            value = value.replaceAll("？", "?");
	            }

	            factor.setValue(value);
	            factor.setHzvalue(hzValue);
	            /* 自助服务-员工信息-数据上报-星号和问号错误 xiaoyun 2014-10-25 end */
	            factor.setOper(PubFunc.keyWord_reback(factor.getOper()));
	        }
			BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());

		    if(!"history".equalsIgnoreCase(model)) {
				String xml = bo.getCondXML(salaryid);
				SalaryLProgramBo sLPBo = new SalaryLProgramBo(xml);
				HashMap condMap = getCondMap(condName, list);
				condMap.put("user_name", this.getUserView().getUserName()); //xieguiquan 20100828
				if (condid != null && !"".equals(condid)) {
					sLPBo.updateServiceItem(condid, condMap, expr);
				} else {
					condid = String.valueOf(sLPBo.setSeiveItem(condMap, expr));
				}
				String newXml = sLPBo.outPutContent();
				bo.updateLprogram(salaryid, newXml);
			}else{
				HistoryDataBo hbo=new HistoryDataBo(this.getFrameconn(),this.getUserView());
				HashMap condMap = getCondMap(condName, list);
				condid=hbo.savePersionFilter(condid,expr,condMap);

			}
			this.getFormHM().put("personFilterList",list);
			this.getFormHM().put("issave","2");
			this.getFormHM().put("filterCondId",condid);
			this.getFormHM().put("expr", expr);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	private HashMap getCondMap(String condName,ArrayList factorlist)
	{
		HashMap map = new HashMap();
		try
		{
			//StringBuffer sexpr= new StringBuffer();
			StringBuffer sfactor = new StringBuffer();
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
			map.put("Name",condName);
			map.put("Factor",sfactor.toString());
			//map.put("Expr",sexpr.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	

}
