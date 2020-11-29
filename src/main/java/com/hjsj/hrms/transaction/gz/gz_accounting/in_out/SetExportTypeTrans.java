package com.hjsj.hrms.transaction.gz.gz_accounting.in_out;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.frame.dbstruct.Field;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 
 *<p>Title:SetExportTypeTrans.java</p> 
 *<p>Description:设置导出格式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 20, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class SetExportTypeTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String salaryid=(String)this.getFormHM().get("salaryid");
			 
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
//			ArrayList list=gzbo.getSalaryItemList();
			//为了让指标的顺序和界面的一致作如下修改
			ArrayList salaryItemList2=gzbo.getSalaryItemList();
			HashMap salaryItemMap = new HashMap();
			for(int i=0;i<salaryItemList2.size();i++ )
			{
				LazyDynaBean abean=(LazyDynaBean)salaryItemList2.get(i);
				String itemid=((String)abean.get("itemid")).toUpperCase();
				salaryItemMap.put(itemid, abean);
			}

			LazyDynaBean bean = new LazyDynaBean();
			bean.set("itemid", "appprocess");
			bean.set("itemdesc", "审批意见");
			salaryItemMap.put("APPPROCESS", bean);

			ArrayList salaryItemList3=gzbo.getFieldlist();//与界面的字段顺序一致	
			
			ArrayList list = new ArrayList(); //放置排好序的列
			for(int i=0;i<salaryItemList3.size();i++ )
			{
				Field field=(Field)salaryItemList3.get(i);
				String itemid=field.getName().toUpperCase();					
				
				if(salaryItemMap.get(itemid)!=null)
					list.add(salaryItemMap.get(itemid));
			}			
			
			ArrayList salaryItemList=new ArrayList();
			String manager=gzbo.getManager();
			String a01z0Flag=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  		

			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)list.get(i);
				String itemid=(String)abean.get("itemid");
				
				if("a01z0".equalsIgnoreCase(itemid)&&(a01z0Flag==null|| "0".equals(a01z0Flag)))
					continue;
				if(!"A0000".equals(itemid)&&!"A0100".equals(itemid)&&!"NBASE".equals(itemid))
				{
				//	if(manager.length()==0||manager.equalsIgnoreCase(this.userView.getUserName()))
					if(this.userView.isSuper_admin())
						salaryItemList.add(abean);
					else
					{
					//	System.out.println((String)abean.get("itemdesc")+"   "+ this.userView.analyseFieldPriv(itemid));
						String flag=(String)abean.get("initflag");
						if("3".equals(flag)||"APPPROCESS".equalsIgnoreCase((String)abean.get("itemid")))//审批意见默认可选
							salaryItemList.add(abean);
						else if(!"3".equals(flag)&&!"0".equals(this.userView.analyseFieldPriv(itemid)))
							salaryItemList.add(abean);
					}
				
				}
			}
			
			
			this.getFormHM().put("aimDataList",salaryItemList);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
