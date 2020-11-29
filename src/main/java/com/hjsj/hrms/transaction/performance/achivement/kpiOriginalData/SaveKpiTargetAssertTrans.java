package com.hjsj.hrms.transaction.performance.achivement.kpiOriginalData;

import com.hjsj.hrms.businessobject.performance.achivement.kpiOriginalData.KpiOriginalDataBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;

/**
 * <p>SaveKpiTargetAssertTrans.java</p>
 * <p>Description:保存KPI指标</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-07-25 10:09:23</p>
 * @author JinChunhai
 * @version 5.0
 */

public class SaveKpiTargetAssertTrans extends IBusiness
{

	public void execute() throws GeneralException
	{
/*		
		RecordVo votemp = (RecordVo) this.getFormHM().get("kpiTargetvo");		
		String item_id = votemp.getString("item_id");
		
		String b0110 = "";
		String unit = votemp.getString("b0110");
		if (unit!=null && unit.length() > 0)
		{
			String[] temp = unit.split("`");
			for (int i = 0; i < temp.length; i++)
			{				
				b0110 += ","+temp[i].substring(2);
			}			
		} 
		
		String itemdesc = isNull(votemp.getString("itemdesc"));
		if(itemdesc!=null && itemdesc.trim().length()>0 && itemdesc.indexOf("'")!=-1)							
			itemdesc = itemdesc.replaceAll("'","‘"); 
		
		String item_type_desc = isNull(votemp.getString("item_type_desc"));
		if(item_type_desc!=null && item_type_desc.trim().length()>0 && item_type_desc.indexOf("'")!=-1)				
			item_type_desc = item_type_desc.replaceAll("'","‘"); 
		
		String end_date = isNull(votemp.getString("end_date"));
		if(end_date==null || end_date.trim().length()<=0)							
			end_date = "9999-12-31"; 
		
		RecordVo vo = new RecordVo("per_kpi_item");		
		vo.setString("id", votemp.getString("id"));
		vo.setString("item_id", isNull(votemp.getString("item_id")));
		vo.setString("itemdesc", itemdesc);
		vo.setString("description", isNull(votemp.getString("description")));
		vo.setString("cycle", isNull(votemp.getString("cycle")));					
		vo.setString("item_type_desc", item_type_desc);
		vo.setDate("start_date", isNull(votemp.getString("start_date")));
		vo.setDate("end_date", end_date);
		vo.setString("seq", isNull(votemp.getString("seq")));				
		vo.setString("b0110", b0110);	
		vo.setString("b0110desc", isNull(votemp.getString("b0110desc")));
*/
		
		String hidKpiItemType = (String) this.getFormHM().get("hidKpiItemType");
		if(hidKpiItemType!=null && hidKpiItemType.trim().length()>0 && hidKpiItemType.indexOf("'")!=-1)				
			hidKpiItemType = hidKpiItemType.replaceAll("'","‘"); 
//		System.out.println(hidKpiItemType+"============");
		
		
		LazyDynaBean kpiBean = (LazyDynaBean) this.getFormHM().get("kpiBean");		
		String item_id = (String)kpiBean.get("item_id");
		
		String b0110 = "";
		String unit = (String)kpiBean.get("b0110");
		if (unit!=null && unit.trim().length() > 0)
		{
			if(unit.indexOf("`")==-1)
			{
				b0110 = unit;
			}
			else
			{
				String[] temp = unit.split("`");
				for (int i = 0; i < temp.length; i++)
				{				
					b0110 += ","+temp[i].substring(2);
				}
			}
		} 
		
		String itemdesc = isNull((String)kpiBean.get("itemdesc"));
		if(itemdesc!=null && itemdesc.trim().length()>0 && itemdesc.indexOf("'")!=-1)							
			itemdesc = itemdesc.replaceAll("'","‘"); 
		
//		String item_type_desc = isNull((String)kpiBean.get("item_type_desc"));
//		if(item_type_desc!=null && item_type_desc.trim().length()>0 && item_type_desc.indexOf("'")!=-1)				
//			item_type_desc = item_type_desc.replaceAll("'","‘"); 
		
		String end_date = isNull((String)kpiBean.get("end_date")).replace(".", "-");
		String start_date = isNull((String)kpiBean.get("start_date")).replace(".", "-");
		if(end_date==null || end_date.trim().length()<=0)							
			end_date = "9999-12-31"; 
		
		RecordVo vo = new RecordVo("per_kpi_item");		
		vo.setString("id", (String)kpiBean.get("id"));
		vo.setString("item_id", isNull((String)kpiBean.get("item_id")));
		vo.setString("itemdesc", itemdesc);
		vo.setString("description", isNull((String)kpiBean.get("description")));
		vo.setString("cycle", isNull((String)kpiBean.get("cycle")));					
//		vo.setString("item_type_desc", item_type_desc);
		vo.setString("item_type_desc", hidKpiItemType);
		vo.setDate("start_date", start_date);
		vo.setDate("end_date", end_date);
		vo.setString("seq", isNull((String)kpiBean.get("seq")));				
		if (b0110!=null && b0110.trim().length() > 0)		
			vo.setString("b0110", b0110);	
		else
			vo.setString("b0110", null);	
//		vo.setString("b0110desc", isNull((String)kpiBean.get("b0110desc")));
		
		KpiOriginalDataBo bo = new KpiOriginalDataBo(this.getFrameconn(),this.userView);
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
//			boolean idEdit = false;
			if (bo.isExist(item_id))// 更新操作
			{
				dao.updateValueObject(vo);
//				idEdit = true;
			} else
			// 新增操作
			{
				dao.addValueObject(vo);
				
//				String sql = "update per_kpi_item set a0000=a0000+1  where a0000 is not null";
//				dao.update(sql);
//				sql = "update per_kpi_item set a0000=1  where plan_id=" + planId;
//				dao.update(sql);
			}			

		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
	public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str))
		    str = "";
		return str;
    }
	
}