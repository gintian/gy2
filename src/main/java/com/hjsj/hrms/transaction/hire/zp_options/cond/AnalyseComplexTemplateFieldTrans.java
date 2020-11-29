package com.hjsj.hrms.transaction.hire.zp_options.cond;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hjsj.hrms.businessobject.hire.zp_options.ZpCondTemplateXMLBo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class AnalyseComplexTemplateFieldTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String templateid=(String)hm.get("templateid");
			String type=(String)this.getFormHM().get("zp_cond_template_type");
			String fieldsId = (String)hm.get("ids");
			getSelectedItem(fieldsId,templateid);
			this.getFormHM().put("zp_cond_template_type", type);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	public void getSelectedItem(String ids,String templateid)
	{
		ArrayList list = new ArrayList();
		ArrayList selectedFieldsList = new ArrayList();
		StringBuffer strexpr=new StringBuffer();
		String templateName="";
		int xx=0;
		String expression="";
		try
		{
			String [] arr=ids.split(",");
			int j=0;
			Factor factor = null;
			int nInform=1;
			FieldItem item=null;
			HashMap map=new HashMap();
			ZpCondTemplateXMLBo bo = new ZpCondTemplateXMLBo(this.getFrameconn());
			if(!"-1".equals(templateid))
			{
				HashMap mp=bo.getFactorExpr(templateid);
				templateName=(String)mp.get("name");
				BankDiskSetBo bdsb=new BankDiskSetBo(this.getFrameconn());
				xx=((String)mp.get("factor")).split("`").length;
				expression=(String)mp.get("expr");
				map=bdsb.getCondField((String)mp.get("expr")+"|"+(String)mp.get("factor"), "1");
			}
			CommonData com=null;
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i]))
				{
					continue;
				}
			
				if("createtime".equalsIgnoreCase(arr[i]))
				{
					factor = new Factor(nInform);
					factor.setCodeid("0");
	                factor.setFieldname("createtime");
	                factor.setHz("简历入库时间");
	                factor.setFieldtype("D");
	                factor.setItemlen(10);
	                factor.setItemdecimal(0);
	                factor.setValue("");
	                factor.setOper("=");//default
	               // factor.setLog("*");//default
	                list.add(factor);
	                ++j;
                    strexpr.append(j);
                    strexpr.append("*");
					continue;
				}
				item=DataDictionary.getFieldItem(arr[i].toUpperCase());
				if(item!=null)
				{
					 factor=new Factor(nInform);
	                    factor.setCodeid(item.getCodesetid());
	                    factor.setFieldname(item.getItemid());
	                    factor.setHz(item.getItemdesc());
	                    factor.setFieldtype(item.getItemtype());
	                    factor.setItemlen(item.getItemlength());
	                    factor.setItemdecimal(item.getDecimalwidth());
	                    if(map.get(item.getItemid().toUpperCase()+i)!=null)
	    				{
	    					LazyDynaBean abean=(LazyDynaBean)map.get(item.getItemid().toUpperCase()+i);
	    					factor.setValue((String)abean.get("value"));
	    					factor.setOper((String)abean.get("oper"));
	    					if(("A".equalsIgnoreCase(item.getItemtype())&&!("0".equals(item.getCodesetid()))))
	    					{
	    				    	factor.setHzvalue(AdminCode.getCodeName(item.getCodesetid(),(String)abean.get("value")));
	    					}
	    				}
	                    else
	                    {
	                       factor.setValue("");
	                       factor.setOper("=");//default
	                    }
	                    //factor.setLog("*");//default
	                    list.add(factor);
	                    ++j;
	                    strexpr.append(j);
	                    strexpr.append("*");
	                    com=new CommonData(item.getItemid(),item.getItemdesc());
	                    selectedFieldsList.add(com);
				}
			}
			if(arr.length==xx)
			{
				strexpr.setLength(0);
				strexpr.append(expression);
			}
			else if(strexpr.length()>0)
	            	strexpr.setLength(strexpr.length()-1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			this.getFormHM().put("templateName", templateName);
			this.getFormHM().put("selectedFieldsList", selectedFieldsList);
			this.getFormHM().put("factorlist",list);
			this.getFormHM().put("expression", strexpr.toString());
		}
	}

}
