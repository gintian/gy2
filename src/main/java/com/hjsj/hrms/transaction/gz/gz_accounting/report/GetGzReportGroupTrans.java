package com.hjsj.hrms.transaction.gz.gz_accounting.report;

import com.hjsj.hrms.businessobject.gz.SalaryReportBo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GetGzReportGroupTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String salaryid=(String)this.getFormHM().get("salaryid");
			String rsdtlid=(String)hm.get("rsdtlid");
			SalaryReportBo gzbo=new SalaryReportBo(this.getFrameconn(),salaryid);
			
			String gzGroupCodesetid="";
			String gzGroupCodeitemid="-1";
			
			LazyDynaBean abean=gzbo.getGzDetailBydID(rsdtlid);
			String f_groupItem=(String)abean.get("fgroup");
			FieldItem item=DataDictionary.getFieldItem(f_groupItem.toLowerCase());
			if("B0110".equalsIgnoreCase(f_groupItem)|| "UN".equalsIgnoreCase(item.getCodesetid()))
			{
				if(!(this.getUserView().isAdmin()&& "1".equals(this.getUserView().getGroupId())))
					gzGroupCodeitemid=this.getUserView().getUnitIdByBusi("1");
				StringBuffer _gzGroupCodeitemid = new StringBuffer();
				ArrayList list = getTopCodeitemid(gzGroupCodeitemid);
				if(list.size()>0){
					for(int i=0;i<list.size();i++){
						_gzGroupCodeitemid.append(",");
						_gzGroupCodeitemid.append("'"+list.get(i)+"'");
					}
				}
				if(_gzGroupCodeitemid.length()>0){
					gzGroupCodeitemid = _gzGroupCodeitemid.toString().substring(1);
				}
				if(gzGroupCodeitemid.length()==0)
					gzGroupCodeitemid="-1";
				gzGroupCodesetid="UN";
			}
			else if("E0122".equalsIgnoreCase(f_groupItem)|| "UM".equalsIgnoreCase(item.getCodesetid()))
			{
				if(!(this.getUserView().isAdmin()&& "1".equals(this.getUserView().getGroupId())))
					gzGroupCodeitemid=this.getUserView().getUnitIdByBusi("1");
				StringBuffer _gzGroupCodeitemid = new StringBuffer();
				ArrayList list = getTopCodeitemid(gzGroupCodeitemid);
				if(list.size()>0){
					for(int i=0;i<list.size();i++){
						_gzGroupCodeitemid.append(",");
						_gzGroupCodeitemid.append("'"+list.get(i)+"'");
					}
				}
				if(_gzGroupCodeitemid.length()>0){
					gzGroupCodeitemid = _gzGroupCodeitemid.toString().substring(1);
				}
				if(gzGroupCodeitemid.length()==0)
					gzGroupCodeitemid="-1";
				gzGroupCodesetid="UM";
			}
			else
			{
				
				String codesetid=item.getCodesetid();
				gzGroupCodesetid=codesetid;
			}
			this.getFormHM().put("gzGroupCodesetid", gzGroupCodesetid);
			this.getFormHM().put("gzGroupCodeitemid", gzGroupCodeitemid);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
/**
 * 取得顶级机构节点
 * @param b_units
 * @return
 * @throws GeneralException
 */	
public ArrayList getTopCodeitemid(String b_units) throws GeneralException{
	ArrayList valuelist = new ArrayList();
	ArrayList newlist = new ArrayList();
	try
	{
		String unitarr[] =b_units.split("`");	
		for(int i=0;i<unitarr.length;i++)
		{
			String codeid=unitarr[i];
			if(codeid==null|| "".equals(codeid))
				continue;
			if(codeid!=null&&codeid.trim().length()>2)
			{
				String privCode = codeid.substring(0,2);
				String privCodeValue = codeid.substring(2);	
				boolean flag = true;
				for(int j=0;j<valuelist.size();j++){//取范围内最顶级的几个节点
					String obj = (String) valuelist.get(j);
					obj = "'"+obj;
					String value = "'"+privCodeValue;
					if(obj.indexOf(value)!=-1){
						valuelist.set(j, privCodeValue);
						flag = false;
					}
					if(value.indexOf(obj)!=-1){
						flag = false;
					}
				}
				if(flag){
					valuelist.add(privCodeValue);
				}
			}
		}
		HashSet set = new HashSet();
		set.addAll(valuelist);
		newlist.addAll(set);
	}		
	catch(Exception e)
	{
		e.printStackTrace();
		throw GeneralExceptionHandler.Handle(e);
	}
	return newlist;
}
}
