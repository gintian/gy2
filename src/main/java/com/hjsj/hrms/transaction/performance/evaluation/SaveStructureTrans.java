package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * <p>Title:SaveStructureTrans.java</p>
 * <p>Description>:保存评估表结构设置</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 17, 2011 12:15:35 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class SaveStructureTrans extends IBusiness
{
	public void execute() throws GeneralException
	{	
		try
		{		
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String rightCount = (String) hm.get("rightCount");			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String importPlanIds=(String)this.getFormHM().get("importPlanIds"); //选中"评估结果中显示"负责人"指标"参数的所有引入的考核计划的ID
    		String onlyFild ="";
    		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			onlyFild = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");//是否选择了唯一性指标的标志
			
			String[] salarySetIDs=null;
//			if(rightCount.equals("0"))			
//				salarySetIDs=null;
//			else
				salarySetIDs=(String[])this.getFormHM().get("salarySetIDs");
			String planid=(String)this.getFormHM().get("planid");			
			LoadXml loadxml = new LoadXml(this.frameconn, planid);
			PerEvaluationBo pb = new PerEvaluationBo(this.getFrameconn(),this.userView);
								
			String menu = "";
			String menuNames = "";			
			String menuss = "";
			String newid = "";			
			String LinkMuti ="False";
			
			HashMap keyMap=new HashMap();
			HashMap keyMap2=new HashMap();
			if(salarySetIDs!=null && salarySetIDs.length>0)
			{
				for(int j=0;j<salarySetIDs.length;j++)
				{	
					salarySetIDs[j] = PubFunc.keyWord_reback(salarySetIDs[j]);
					if((salarySetIDs[j].indexOf(";")!=-1) || (salarySetIDs[j].indexOf("；")!=-1))
					{						
						newid=salarySetIDs[j].substring(0,salarySetIDs[j].indexOf(":")); //123
	//					name=salarySetIDs[j].substring((salarySetIDs[j].indexOf(";")+1),(salarySetIDs[j].indexOf(":"))); //name
						menuss=salarySetIDs[j].substring(salarySetIDs[j].indexOf(":")+1);
						
						String menzz ="";
						if(menuss.indexOf("_")!=-1&&menuss.length()>4)
						  menzz  = menuss.substring(menuss.length()-4,menuss.length());
						if(menuss.indexOf("_")!=-1&&("ZAVG".equalsIgnoreCase(menzz)|| "ZSUM".equalsIgnoreCase(menzz)|| "ZMAX".equalsIgnoreCase(menzz)|| "ZMIN".equalsIgnoreCase(menzz))){
							if(keyMap2.get(newid+"HZMenus")==null)
							keyMap2.put(newid+"HZMenus",menuss.replaceAll("_Z",":"));
							else
							{
								String menuss_str2=(String)keyMap2.get(newid+"HZMenus");
								menuss_str2+=","+menuss.replaceAll("_Z",":");
								keyMap2.put(newid+"HZMenus",menuss_str2);
							}
						}else{
							
						if(keyMap.get(newid)==null)
						{
							keyMap.put(newid,menuss);
						}
						else
						{
							String menuss_str=(String)keyMap.get(newid);
							menuss_str+=","+menuss;
							keyMap.put(newid,menuss_str);
						}
						}
					}else
					{	
						
						if(!"".equals(importPlanIds.trim())&&salarySetIDs[j].substring(0,salarySetIDs[j].indexOf(":")).equalsIgnoreCase(onlyFild))
							LinkMuti ="True";	
						menu+=","+salarySetIDs[j].substring(0,salarySetIDs[j].indexOf(":"));
						menuNames+=","+salarySetIDs[j].substring(salarySetIDs[j].indexOf(":")+1);																
					}
				}
			}
			ArrayList list = new ArrayList();
			ArrayList planlist = loadxml.getRelatePlanValue("Plan");
			LazyDynaBean abean=null;
			
			for(int i=0;i<planlist.size();i++)
			{	
				abean=(LazyDynaBean)planlist.get(i);
				String id=(String)abean.get("id");
				String Name=(String)abean.get("Name");
				String Type=(String)abean.get("Type");
				
				boolean isSame=false;
				String new_id="";
				String menu_s="";
				String type="";
				String strValue="";
				String HZstrValue="";
				Set keySet=keyMap.keySet();
				java.util.Iterator t=keySet.iterator();
				while(t.hasNext())
				{
					String strKey = (String)t.next();  //键值	    
					strValue = (String)keyMap.get(strKey);   //value值  
				  if(keyMap2.get(strKey+"HZMenus")==null)
					HZstrValue ="";	
				  else
					HZstrValue = (String)keyMap2.get(strKey+"HZMenus");   //value值   
					new_id=strKey.substring(0,strKey.indexOf(";")); //123
					menu_s=strKey.substring((strKey.indexOf(";")+1),(strKey.indexOf("`"))); //name
					type=strKey.substring(strKey.indexOf("`")+1);	//name 
					if(new_id.equals(id))
					{
						isSame=true;
						break;
					}
				}
				LazyDynaBean bean = null;
				if(isSame)
				{
					bean = new LazyDynaBean();
					bean.set("ID", new_id);
					bean.set("Name", menu_s);
					bean.set("Type", type);
					if("0".equals(rightCount)){
						bean.set("Menus", "score");
						bean.set("HZMenus", "");
						bean.set("LinkMuti", LinkMuti);
					}else{
						bean.set("Menus", strValue);
						bean.set("HZMenus", HZstrValue);
						bean.set("LinkMuti", LinkMuti);
                 }
				}else
				{
					bean = new LazyDynaBean();
					bean.set("ID", id);
					bean.set("Name", Name);
					bean.set("Type", Type);
					bean.set("Menus", "score");
					bean.set("HZMenus", "");
					bean.set("LinkMuti", LinkMuti);
				}
				list.add(bean);
			}						
			
			ArrayList idlist = new ArrayList();
			idlist.add("ID");
			idlist.add("Name");
			idlist.add("Type");
			idlist.add("Menus");
			idlist.add("HZMenus");
			idlist.add("LinkMuti");
			loadxml.saveRelatePlanValue("Plan", idlist, list);
			
			ArrayList rlist = new ArrayList();
			LazyDynaBean sbean = new LazyDynaBean();
			if((menu==null || menu.length()<=0) || ("0".equals(rightCount)))
			{								
				sbean.set("Menus", "");
				sbean.set("MenuNames", "");				
			}else{								
				sbean.set("Menus", menu.substring(1));
				sbean.set("MenuNames", menuNames.substring(1));				
			}
			rlist.add(sbean);
			ArrayList dlist = new ArrayList();				
			dlist.add("Menus");
			dlist.add("MenuNames");
			loadxml.saveRelatePlanValue("Subset", dlist, rlist);
			
			// 检查per_result_planid表中有没有调整后的表结构的字段，若没有就创建
			pb.editResult(planid);
			
			// 更新per_result_planid表中调整后的表结构的"子集"字段的值
			pb.updateSubset(planid);
			
			// 更新per_result_planid表中调整后的表结构的"引入计划"的字段的值
			pb.updateResultTable(planid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
}

