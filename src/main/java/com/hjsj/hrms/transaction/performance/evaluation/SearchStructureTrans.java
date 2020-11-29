package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchStructureTrans.java</p>
 * <p>Description>:评估表结构设置</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 17, 2011 10:15:35 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class SearchStructureTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			String opt=(String)this.getFormHM().get("opt");   // 1: fieldset   2:fielditem
			String planid=(String)this.getFormHM().get("planid");
			String object_type=(String)this.getFormHM().get("object_type");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			ArrayList list=new ArrayList();
			ArrayList alist=new ArrayList();
			LoadXml loadxml = new LoadXml(this.frameconn, planid);
			if("1".equals(opt))
			{
				list.add(new CommonData("",""));
				if("2".equalsIgnoreCase(object_type))
				{
					this.frowset=dao.search("select fieldsetid,customdesc from fieldset where fieldsetid like 'A%' and useflag<>'0' order by displayorder ");
					while(this.frowset.next())
					{
						if(!(this.userView.isSuper_admin()))
						{
							if("0".equals(this.userView.analyseTablePriv(this.frowset.getString(1))))
							{
								continue;
							}
						}
						list.add(new CommonData(SafeCode.encode(this.frowset.getString(1)),SafeCode.encode((this.frowset.getString(2)))));
					}				
					this.frowset=dao.search("select fieldsetid,customdesc from fieldset where fieldsetid like 'B%' and useflag<>'0' order by displayorder ");
					while(this.frowset.next())
					{
						if(!(this.userView.isSuper_admin()))
						{
							if("0".equals(this.userView.analyseTablePriv(this.frowset.getString(1))))
							{
								continue;
							}
						}
						list.add(new CommonData(SafeCode.encode(this.frowset.getString(1)),SafeCode.encode(this.frowset.getString(2))));
					}
				}else
				{
					this.frowset=dao.search("select fieldsetid,customdesc from fieldset where fieldsetid like 'B%' and useflag<>'0' order by displayorder ");
					while(this.frowset.next())
					{
						if(!(this.userView.isSuper_admin()))
						{
							if("0".equals(this.userView.analyseTablePriv(this.frowset.getString(1))))
							{
								continue;
							}
						}
						list.add(new CommonData(SafeCode.encode(this.frowset.getString(1)),SafeCode.encode(this.frowset.getString(2))));
					}
				}
				HashMap bodyMap=new HashMap();
				this.frowset=dao.search("select body_id,name from per_mainbodyset");
				while(this.frowset.next())
				{
					bodyMap.put(this.frowset.getString("body_id"),this.frowset.getString("name"));
				}
				ArrayList planlist = loadxml.getRelatePlanValue("Plan");
				LazyDynaBean abean=null;
				for(int i=0;i<planlist.size();i++)
				{
					abean=(LazyDynaBean)planlist.get(i);
					String id=(String)abean.get("id");
					String Name=(String)abean.get("Name");
					String Type=(String)abean.get("Type");
					list.add(new CommonData(SafeCode.encode(id),SafeCode.encode(id+"."+Name)));
					
//					alist.add(new CommonData(id,Name));									
					
					HashMap itemMap=new HashMap();
					this.frowset =dao.search("select item_id,itemdesc from per_template_item where template_id=(select template_id from per_plan where plan_id="+id+")");
					while(this.frowset.next())
					{ 
						itemMap.put(this.frowset.getString("item_id"),this.frowset.getString("itemdesc"));
					}
					HashMap pointMap=new HashMap();
					String sql = "select po.point_id,po.pointname,po.pointkind,pi.item_id,po.visible,po.fielditem,po.status,pp.score,po.Kh_content,po.Gd_principle from per_template_item pi,per_template_point pp,per_point po "
					    +" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id=(select template_id from per_plan where plan_id="+id+") "
				        +" order by pp.seq";
					this.frowset = dao.search(sql);
				    while(this.frowset.next())
					{ 
				    	pointMap.put(this.frowset.getString("point_id").toLowerCase(),this.frowset.getString("pointname"));
					}
										
					/**
					”Score,Grade,Avg,Max,Min,XiShu,Order,UMOrd,Body1,Body2,Body3,Item1，Item2，XXXXX_1” 
					分别表示“得分，等级，组平均分,组最高分,组最低分，绩效系数，组内排名，部门排名，类别1，类别2，类别3,项目1，项目2，指标XXXXX_1”。为空或没有本属性默认为“得分”。
					对应字段(plan_id用n表示)：
					“G_n,G_n_Grade, G_n_Avg, G_n_Max, G_n_Min, G_n_XiShu,  G_n_Order, G_n_UMOrd, G_n_B_Id, , G_n_I_Id, , G_n_P_Id,”
					 */
					String Menus=(String)abean.get("Menus");
					String HZMenus=(String)abean.get("HZMenus");
					if(Menus!=null&&Menus.trim().length()>0)
					{
						String[] temps=Menus.split(",");
						for(int j=0;j<temps.length;j++)
						{
							String temp=temps[j].trim();
							if(temp.length()==0)
								continue;
							if("score".equalsIgnoreCase(temp))
							{
								alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":score"), SafeCode.encode(id+".得分"))); 
							}
							else if("Grade".equalsIgnoreCase(temp))
							{
								alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":Grade"), SafeCode.encode(id+".等级")));
							}
							else if("Avg".equalsIgnoreCase(temp))
							{
								alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":Avg"), SafeCode.encode(id+".组平均分")));  
							}
							else if("Max".equalsIgnoreCase(temp))
							{
								alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":Max"), SafeCode.encode(id+".组最高分")));  
							}
							else if("Min".equalsIgnoreCase(temp))
							{
								alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":Min"), SafeCode.encode(id+".组最低分"))); 
							}
							else if("XiShu".equalsIgnoreCase(temp))
							{
								alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":XiShu"), SafeCode.encode(id+".等级系数")));  
							}
							else if("Order".equalsIgnoreCase(temp))
							{
								alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":Order"), SafeCode.encode(id+".组内排名"))); 
							}
							else if("UMOrd".equalsIgnoreCase(temp))
							{
								alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":UMOrd"), SafeCode.encode(id+".部门排名")));   
							}
							else if("Mark".equalsIgnoreCase(temp))
							{
								alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":Mark"), SafeCode.encode(id+".备注")));   
							}
							else if(temp.indexOf("Body")!=-1)
							{
								String bodyid=temp.replaceAll("Body","");
								if(bodyMap.get(bodyid)!=null)
									alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":Body"+bodyid), SafeCode.encode(id+"."+(String)bodyMap.get(bodyid)))); 
							}
							else if(temp.indexOf("Item")!=-1)
							{
								String itemid=temp.replaceAll("Item","");
								if(itemMap.get(itemid)!=null)
									alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":Item"+itemid), SafeCode.encode(id+"."+(String)itemMap.get(itemid)))); 
							}
							else  
							{
								if(pointMap.get(temp.toLowerCase())!=null)
									alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":"+temp.toLowerCase()), SafeCode.encode(id+"."+(String)pointMap.get(temp.toLowerCase())))); 
							}
						}
					}else{
						alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":score"), SafeCode.encode(id+".得分")));
					}
					
              //----------------
					String temp2 ="";
					if(HZMenus!=null&&HZMenus.trim().length()>0)
					{
						String[] temps=HZMenus.split(",");
						for(int j=0;j<temps.length;j++)
						{
							String temp=temps[j].trim();
							String temp1 ="";
							
							if(temp.indexOf(":")!=-1){
							temp1 = temp.substring(0,temp.indexOf(":"));	
							temp2 = temp.substring(temp.indexOf(":")+1);
							}
							if(temp1.length()==0)
								continue;
							if("score".equalsIgnoreCase(temp1))
							{
								alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":score_Z"+temp2), SafeCode.encode(id+".得分"+getHZMenus(temp2)+""))); 
							}
							else if("Grade".equalsIgnoreCase(temp1))
							{
								alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":Grade_Z"+temp2), SafeCode.encode(id+".等级"+getHZMenus(temp2)+"")));
							}
							else if("Avg".equalsIgnoreCase(temp1))
							{
								alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":Avg_Z"+temp2), SafeCode.encode(id+".组平均分"+getHZMenus(temp2)+"")));  
							}
							else if("Max".equalsIgnoreCase(temp1))
							{
								alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":Max_Z"+temp2), SafeCode.encode(id+".组最高分"+getHZMenus(temp2)+"")));  
							}
							else if("Min".equalsIgnoreCase(temp1))
							{
								alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":Min_Z"+temp2), SafeCode.encode(id+".组最低分"+getHZMenus(temp2)+""))); 
							}
							else if("XiShu".equalsIgnoreCase(temp1))
							{
								alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":XiShu_Z"+temp2), SafeCode.encode(id+".等级系数"+getHZMenus(temp2)+"")));  
							}
							else if("Order".equalsIgnoreCase(temp1))
							{
								alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":Order_Z"+temp2), SafeCode.encode(id+".组内排名"+getHZMenus(temp2)+""))); 
							}
							else if("UMOrd".equalsIgnoreCase(temp1))
							{
								alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":UMOrd_Z"+temp2), SafeCode.encode(id+".部门排名"+getHZMenus(temp2)+"")));   
							}
							else if("Mark".equalsIgnoreCase(temp1))
							{
								alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":Mark_Z"+temp2), SafeCode.encode(id+".备注"+getHZMenus(temp2)+"")));   
							}
							else if(temp1.indexOf("Body")!=-1)
							{
								String bodyid=temp1.replaceAll("Body","");
								if(bodyMap.get(bodyid)!=null)
									alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":Body"+bodyid+"_Z"+temp2), SafeCode.encode(id+"."+(String)bodyMap.get(bodyid)+getHZMenus(temp2)))); 
							}
							else if(temp1.indexOf("Item")!=-1)
							{
								String itemid=temp1.replaceAll("Item","");
								if(itemMap.get(itemid)!=null)
									alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":Item"+itemid+"_Z"+temp2), SafeCode.encode(id+"."+(String)itemMap.get(itemid)+getHZMenus(temp2)))); 
							}
							else  
							{
								if(pointMap.get(temp1.toLowerCase())!=null)
									alist.add(new CommonData( SafeCode.encode(id+";"+Name+"`"+Type+":"+temp1.toLowerCase()+"_Z"+temp2), SafeCode.encode(id+"."+(String)pointMap.get(temp1.toLowerCase())+getHZMenus(temp2)))); 
							}
						}
					}
					
					
					
					
			  //----------------		
					
				}
				String subsetMenus = loadxml.getRelatePlanSubSetMenuValue();
				if(subsetMenus!=null&&subsetMenus.trim().length()>0)
				{
					String[] temps=subsetMenus.split(",");
					for(int j=0;j<temps.length;j++)
					{
						String temp=temps[j].trim();
						if(temp.length()==0)
							continue;
					    FieldItem fielditem = DataDictionary.getFieldItem(temp);
					    alist.add(new CommonData(SafeCode.encode(fielditem.getItemid()+":"+fielditem.getItemdesc()),SafeCode.encode(fielditem.getItemdesc())));
					}
				}								
			}
			else if("2".equals(opt))
			{																
				String feildSetid=(String)this.getFormHM().get("fieldSetid"); //子集指标
				if(feildSetid!=null && feildSetid.trim().length()>0)
				{
					ArrayList objlist=(ArrayList)this.getFormHM().get("objlist"); //界面的右栏框的所有指标选项
					String importPlanIds=(String)this.getFormHM().get("importPlanIds"); //选中"评估结果中显示"负责人"指标"参数的所有引入的考核计划的ID
					String onlyTrOrFa=(String)this.getFormHM().get("onlyTrOrFa"); //是否选择了唯一性指标的标志
					
					String menu = "";		
					String menuss = "";
					String newid = "";							
					HashMap keyMap=new HashMap();
					if(objlist!=null && objlist.size()>0)
					{
						for(int j=0;j<objlist.size();j++)
						{	
							String salarySetIDs=(String)objlist.get(j);
							salarySetIDs=SafeCode.decode(salarySetIDs);
							salarySetIDs = PubFunc.keyWord_reback(salarySetIDs);
							if((salarySetIDs.indexOf(";")!=-1) || (salarySetIDs.indexOf("；")!=-1))
							{						
								newid=salarySetIDs.substring(0,salarySetIDs.indexOf(":")); //123
								menuss=salarySetIDs.substring(salarySetIDs.indexOf(":")+1);
								if(keyMap.get(newid)==null)							
									keyMap.put(newid,menuss);							
								else
								{
									String menuss_str=(String)keyMap.get(newid);
									menuss_str+=","+menuss;
									keyMap.put(newid,menuss_str);
								}	
							}else												
								menu+=","+salarySetIDs.substring(0,salarySetIDs.indexOf(":"));																					
						}
					}
					ArrayList planlist = loadxml.getRelatePlanValue("Plan");
					LazyDynaBean abean=null;
					String str="0";
					for(int i=0;i<planlist.size();i++)
					{
						abean=(LazyDynaBean)planlist.get(i);
						String id=(String)abean.get("id");
						String Name=(String)abean.get("Name");	
						String Type=(String)abean.get("Type");
						String HZMenus=(String)abean.get("HZMenus");
						if(feildSetid.equalsIgnoreCase(id))
						{	
							if(HZMenus.indexOf("Grade")==-1)
							list.add(new CommonData(SafeCode.encode(feildSetid+";"+Name+"`"+Type+":Grade"), SafeCode.encode("等级")));
							if(HZMenus.indexOf("XiShu")==-1)
							list.add(new CommonData(SafeCode.encode(feildSetid+";"+Name+"`"+Type+":XiShu"), SafeCode.encode("等级系数")));
							if(HZMenus.indexOf("Avg")==-1)
							list.add(new CommonData(SafeCode.encode(feildSetid+";"+Name+"`"+Type+":Avg"), SafeCode.encode("组平均分")));
							if(HZMenus.indexOf("Max")==-1)
							list.add(new CommonData(SafeCode.encode(feildSetid+";"+Name+"`"+Type+":Max"), SafeCode.encode("组最高分")));
							if(HZMenus.indexOf("Min")==-1)
							list.add(new CommonData(SafeCode.encode(feildSetid+";"+Name+"`"+Type+":Min"), SafeCode.encode("组最低分")));
							if(HZMenus.indexOf("Order")==-1)
							list.add(new CommonData(SafeCode.encode(feildSetid+";"+Name+"`"+Type+":Order"), SafeCode.encode("组内排名")));
							if(HZMenus.indexOf("UMOrd")==-1)
							list.add(new CommonData(SafeCode.encode(feildSetid+";"+Name+"`"+Type+":UMOrd"), SafeCode.encode("部门排名")));
							if(HZMenus.indexOf("Mark")==-1)
							list.add(new CommonData(SafeCode.encode(feildSetid+";"+Name+"`"+Type+":Mark"), SafeCode.encode("备注")));
	
							this.frowset=dao.search("select pe.body_id,pm.name from per_plan_body pe,per_mainbodyset pm where pe.body_id=pm.body_id and pe.plan_id="+feildSetid);
							while(this.frowset.next())
							{   
								if(HZMenus.indexOf("Body"+this.frowset.getString("body_id"))!=-1)
									continue;
								list.add(new CommonData(SafeCode.encode(feildSetid+";"+Name+"`"+Type+":Body"+this.frowset.getString("body_id")),SafeCode.encode(this.frowset.getString("name"))));
							}
							
							this.frowset=dao.search("select item_id,itemdesc from per_template_item where template_id=(select template_id from per_plan where plan_id="+feildSetid+")");
							while(this.frowset.next()){
								if(HZMenus.indexOf("Item"+this.frowset.getString("item_id"))!=-1)
									continue;
								list.add(new CommonData(SafeCode.encode(feildSetid+";"+Name+"`"+Type+":Item"+this.frowset.getString("item_id")),SafeCode.encode(this.frowset.getString("itemdesc"))));
							}
							

							String sql = "select po.point_id,po.pointname,po.pointkind,pi.item_id,po.visible,po.fielditem,po.status,pp.score,po.Kh_content,po.Gd_principle from per_template_item pi,per_template_point pp,per_point po "
								+" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id=(select template_id from per_plan where plan_id="+feildSetid+") "
								+" order by pp.seq";
							this.frowset = dao.search(sql);
							while(this.frowset.next()){
								if(HZMenus.indexOf(this.frowset.getString("point_id"))!=-1||HZMenus.indexOf(this.frowset.getString("point_id").toLowerCase())!=-1)
									continue;
								list.add(new CommonData(SafeCode.encode(feildSetid+";"+Name+"`"+Type+":"+this.frowset.getString("point_id")),SafeCode.encode(this.frowset.getString("pointname"))));
							}
							
							
							str="1";
							break;
						}
					}
									
					if("0".equals(str))
					{
						StringBuffer buf = new StringBuffer();
						StringBuffer sql=null;
						if(menu!=null&&menu.trim().length()>0)
						{					
							String[] temps=menu.split(",");
							for(int j=0;j<temps.length;j++)
							{
								String temp=temps[j].trim();
								if(temp.length()==0)
									continue;
							    FieldItem fielditem = DataDictionary.getFieldItem(temp);
							    buf.append(",'" + fielditem.getItemid().toUpperCase() + "'");
							}
							
							sql=new StringBuffer("select itemid,itemdesc from fielditem where fieldsetid='"+feildSetid+"' ");
						    sql.append(" and upper(itemid) not in (" + buf.substring(1) + ",'B0110','E0122','A0101','E01A1') and useflag='1'");
						    sql.append(" order by displayid  ");
							
						}else
						{
							sql=new StringBuffer("select itemid,itemdesc from fielditem where fieldsetid='"+feildSetid+"' ");
						    sql.append(" and upper(itemid) not in ('B0110','E0122','A0101','E01A1') and useflag='1'");
						    sql.append(" order by displayid  ");
						}				    
						this.frowset=dao.search(sql.toString());					
						while(this.frowset.next())
						{		
							if(!(this.userView.isSuper_admin()))
							{
								if("0".equals(this.userView.analyseFieldPriv(this.frowset.getString(1))))
								{
									continue;
								}
							}							
							list.add(new CommonData(SafeCode.encode(this.frowset.getString(1)+":"+this.frowset.getString(2)),SafeCode.encode(this.frowset.getString(2))));					
						}
					}
				}
			}
			this.getFormHM().put("list",list);
			this.getFormHM().put("rightList",alist);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public String getHZMenus(String temp1)
	{
	
        String HZMenusValue = "";
    	if("SUM".equalsIgnoreCase(temp1))
    		HZMenusValue = ResourceFactory.getProperty("jx.evaluation.zsum");
    	if("AVG".equalsIgnoreCase(temp1))
    		HZMenusValue = ResourceFactory.getProperty("jx.evaluation.zavg");
    	if("MAX".equalsIgnoreCase(temp1))
    		HZMenusValue = ResourceFactory.getProperty("jx.evaluation.zmax");
    	if("MIN".equalsIgnoreCase(temp1))
    		HZMenusValue = ResourceFactory.getProperty("jx.evaluation.zmin");
    	    	
    	return HZMenusValue;
    }
}
