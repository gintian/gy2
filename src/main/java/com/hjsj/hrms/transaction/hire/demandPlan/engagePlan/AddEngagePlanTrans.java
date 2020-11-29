package com.hjsj.hrms.transaction.hire.demandPlan.engagePlan;

import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * 
 * <p>Title:AddEngagePlanTrans.java</p>
 * <p>Description:添加招聘计划</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 26, 2006 9:46:17 AM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class AddEngagePlanTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		try
		{
			String z0101 = "";
			int flag = 1;  // 1:添加  0：修改
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RecordVo vo = new RecordVo("Z01");
			ArrayList planFieldList=(ArrayList)this.getFormHM().get("planFieldList");
			
			
			
			for(int i=0;i<planFieldList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)planFieldList.get(i);
				if("z0101".equalsIgnoreCase((String)abean.get("itemid")))
				{
					if(abean.get("value")!=null&&((String)abean.get("value")).trim().length()>=1)
					{	
						flag=0;
						z0101=((String)abean.get("value")).trim();
						vo.setString((String)abean.get("itemid"),((String)abean.get("value")).trim());
					}
					else
					{
						IDGenerator idg = new IDGenerator(2, this.getFrameconn());
						z0101 = idg.getId("Z01.Z0101");
						vo.setString((String)abean.get("itemid"),z0101);
					}
				}
				else if(abean.get("value")!=null&&((String)abean.get("value")).trim().length()>=0)
				{
					String itemtype=(String)abean.get("itemtype");
					String itemid=((String)abean.get("itemid")).toLowerCase();
					String value=((String)abean.get("value")).trim();
					String decimalwidth=(String)abean.get("decimalwidth");
					if("A".equals(itemtype)|| "M".equals(itemtype))
						vo.setString(itemid,value);
					else if("N".equals(itemtype))
					{
						if("0".equals(decimalwidth))
						{
							if(value.indexOf(".")!=-1)
								value=value.substring(0,value.indexOf("."));
							vo.setInt(itemid,Integer.parseInt(value));
						}
						else{
							if("".equals(value))
								value="0";
							vo.setDouble(itemid,Double.parseDouble(value));
						}
							
					}
					else if("D".equals(itemtype))
					{  if(value!=null&&value.length()>0){

						String[] temp=value.split("-");
						Calendar a=Calendar.getInstance();						
						a.set(Calendar.YEAR,Integer.parseInt(temp[0]));
						a.set(Calendar.MONTH,(Integer.parseInt(temp[1])-1));
						a.set(Calendar.DATE,Integer.parseInt(temp[2]));
						vo.setDate(itemid,a.getTime());
						}
					}
				}
			}
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String origin=(String)hm.get("origin");
			if("a".equals(origin))
			{
				vo.setDate("z0123",new Date());
			}
			if(vo.getString("z0103")==null||vo.getString("z0103").trim().length()==0)
			{
				throw GeneralExceptionHandler.Handle(new Exception("请填写计划名称!"));
			}
		    if(vo.getDate("z0107")==null||vo.getDate("z0109")==null)
		    {
		    	throw GeneralExceptionHandler.Handle(new Exception("请填写开始时间和结束时间!"));
		    }
		    {
		    	Calendar startD=Calendar.getInstance();
		    	Calendar endD=Calendar.getInstance();
		    	startD.setTime(vo.getDate("z0107"));
		    	endD.setTime(vo.getDate("z0109"));
		    	if(startD.get(Calendar.YEAR)>endD.get(Calendar.YEAR)||(startD.get(Calendar.YEAR)==endD.get(Calendar.YEAR)&&startD.get(Calendar.MONTH)>endD.get(Calendar.MONTH))
		    		||(startD.get(Calendar.YEAR)==endD.get(Calendar.YEAR)&&startD.get(Calendar.MONTH)==endD.get(Calendar.MONTH)&&startD.get(Calendar.DATE)>endD.get(Calendar.DATE)))
		    		throw GeneralExceptionHandler.Handle(new Exception("开始时间不能大于结束时间!"));
		    }
		    
		    if(vo.getString("z0127")==null||vo.getString("z0127").trim().length()==0)
		    {
		    	throw GeneralExceptionHandler.Handle(new Exception("请填写计划招聘对象!"));
		    }
		    if(vo.getString("z0105")==null||vo.getString("z0105").trim().length()==0)
		    {
		    	throw GeneralExceptionHandler.Handle(new Exception("请填写所属单位!"));
		    }
			
			if (flag == 1)
				dao.addValueObject(vo);
			else
				dao.updateValueObject(vo);
			
			String selectID=(String)this.getFormHM().get("selectID");
			if(selectID!=null&&selectID.trim().length()>0)
			{
				StringBuffer sql_whl=new StringBuffer("");
				selectID=selectID.replaceAll("\\^","'");
				/**安全平台改造,判断z0301是不是存在的当前操作人员的招聘需求begin**/
				String z0301s[]=selectID.substring(1).split(",");
				String checksql = (String) this.userView.getHm().get("hire_sql");
				int index = checksql.indexOf("order by");
				if(index!=-1){
					checksql = checksql.substring(0, index);
				}	
				checksql = checksql+" and z0301 in(";
				checksql = checksql+selectID.substring(1);
				checksql=checksql+")";
				dao=new ContentDAO(this.getFrameconn());	
				try {
					this.frowset = dao.search(checksql);
					int count=0;
					while(this.frowset.next()){
						count++;
					}
					if(count<z0301s.length){
						throw new GeneralException(ResourceFactory.getProperty("label.hireemploye.no.contorl"));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				/**安全平台改造,判断z0301是不是存在的当前操作人员的招聘需求end**/
				dao.update("update z03 set z0101='"+z0101+"',z0319='04',z0317='1' where z0301 in("+selectID.substring(1)+")");			
				this.frowset=dao.search("select sum(z0315) from  z03 where z0301 in("+selectID.substring(1)+")");
				if(this.frowset.next())
				{
					dao.update("update z01 set z0115="+this.frowset.getString(1)+",z0129='04' where z0101='"+z0101+"'");
				}
				ArrayList z03list=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
				ArrayList z04list=DataDictionary.getFieldList("Z04",Constant.USED_FIELD_SET);
				String str=selectID.replaceAll("'", "");
				String[] ids=str.split(","); 
				PositionDemand bo = new PositionDemand(this.getFrameconn());
				for(int j=0;j<ids.length;j++)
				{
					if(ids[j]==null|| "".equals(ids[j]))
						continue;
					RecordVo avo = new RecordVo("z03");
					avo.setString("z0301",ids[j]);
					avo = dao.findByPrimaryKey(avo);
					String shrs="0";
					if(avo.getString("z0315")!=null&&avo.getInt("z0315")!=0)
					{
						shrs=avo.getString("z0315");
					}
					else
					{
						if(avo.getString("z0313")!=null&&avo.getInt("z0313")!=0)
						{
				    		shrs=avo.getString("z0313");
						}
					}
					if(!"09".equals(avo.getString("z0319")))
		    			bo.addHireOrder(z03list, z04list, Integer.parseInt(shrs), avo.getString("z0301"),this.getUserView());
				}
			}
			
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
