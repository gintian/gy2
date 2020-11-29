package com.hjsj.hrms.transaction.performance.commend_table;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:提交推荐表</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 29, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class SubCommendTable1Trans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String limitNum=(String)this.getFormHM().get("limitNum");
			
			ArrayList resultList=new ArrayList();
			StringBuffer a0100s=new StringBuffer("");
			for(int i=0;i<Integer.parseInt(limitNum);i++)
			{
				String commended_=(String)this.getFormHM().get("commended_"+i);
				if(commended_.trim().length()==1)
					break;
				commended_ = commended_.replaceAll("／", "/");
				String[] values=commended_.split("/");
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("a0100",values[1]);
				abean.set("a0101",values[0]);
				a0100s.append(" or a0100='"+values[1]+"'");
				resultList.add(abean);
			}
			
			
			
			String recommend_unit=(String)this.getFormHM().get("recommend_unit");
			String flag=(String)this.getFormHM().get("flag");  // 0:保存 1：完成  2:提交
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.update("delete from per_recommend_result where a0100='"+this.userView.getA0100()+"' and upper(nbase)='"+this.userView.getDbname().toUpperCase()+"' and c11=1");
			String personTypeItem="";
			if(SystemConfig.getPropertyValue("recommend_type")!=null&&SystemConfig.getPropertyValue("recommend_type").trim().length()>0)
			{
				personTypeItem=SystemConfig.getPropertyValue("recommend_type").trim();			
			}
			
			HashMap a0100TypeMap=new HashMap();
			if(a0100s.length()>0&&personTypeItem.length()>0)
			{
				RowSet rowSet=dao.search("select "+personTypeItem+",a0100 from UsrA01 where "+a0100s.substring(3));
				while(rowSet.next())
				{
					a0100TypeMap.put(rowSet.getString("a0100"), rowSet.getString(personTypeItem)!=null?rowSet.getString(personTypeItem):"");
				}
				
			}
			
			int num=0;
			LazyDynaBean abean=null;
			for(int i=0;i<resultList.size();i++)
			{
				abean=(LazyDynaBean)resultList.get(i);
				String a0100=(String)abean.get("a0100");
				String a0101=(String)abean.get("a0101");
				String type=(String)a0100TypeMap.get(a0100);
				appendRecord(a0101,a0100,type,recommend_unit,dao,Integer.parseInt(flag),(i+1));
				num++;
			}
			
			 
			if(num==0&&(Integer.parseInt(flag)==1||Integer.parseInt(flag)==2))
				appendRecord("无合适人员","","",recommend_unit,dao,Integer.parseInt(flag),0);
			
			this.getFormHM().put("flag",flag);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	
	public void appendRecord(String name,String a0100_1,String recommend_type,String recommend_unit,ContentDAO dao,int flag,int order)
	{
		try
		{
			int maxid=0;
			IDGenerator idg = new IDGenerator(2,this.getFrameconn());
			maxid=Integer.parseInt(IDGenerator.getKeyId("per_recommend_result", "id", 1));
			RecordVo vo=new RecordVo("per_recommend_result");
			vo.setInt("id", maxid);
			vo.setInt("c11", 1);
			vo.setString("a0100",this.userView.getA0100());
			vo.setString("nbase", this.userView.getDbname());
			vo.setString("e0122",this.userView.getUserDeptId());
			vo.setString("b0110",this.userView.getUserOrgId());
			vo.setString("a0101_1",name);
			vo.setString("a0100_1", a0100_1);
			vo.setString("recommend_unit", recommend_unit);
			vo.setString("recommend_type", recommend_type);
			vo.setInt("order_num",order);
			vo.setInt("flag", flag);
			dao.updateValueObject(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
