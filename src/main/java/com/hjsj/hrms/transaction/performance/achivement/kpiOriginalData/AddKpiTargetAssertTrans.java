package com.hjsj.hrms.transaction.performance.achivement.kpiOriginalData;

import com.hjsj.hrms.businessobject.performance.achivement.kpiOriginalData.KpiOriginalDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.HashMap;

/**
 * <p>Title:AddKpiTargetAssertTrans.java</p>
 * <p>Description:新增和编辑KPI指标</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-07-25 10:09:23</p>
 * @author JinChunhai
 * @version 5.0
 */

public class AddKpiTargetAssertTrans extends IBusiness
{

	public void execute() throws GeneralException
	{	
			
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		// 如果为编辑,item_id能取到值
		String item_id = (String) hm.get("item_id");
		hm.remove("item_id");

		String creatDate = PubFunc.getStringDate("yyyy-MM-dd");
//		RecordVo vo = new RecordVo("per_kpi_item");
		LazyDynaBean kpiBean = new LazyDynaBean();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rowSet = null;
		String kpiItemType = "";		
		try
		{
			KpiOriginalDataBo bo = new KpiOriginalDataBo(this.getFrameconn(),this.userView);
			
			if (item_id == null || item_id.trim().length()<=0 || "".equals(item_id))
			{												
				/**增加一条记录*/
				int maxid=0;				
		        rowSet = dao.search("select max(id) from per_kpi_item");
		        while(rowSet.next())
		        {
		        	String id = rowSet.getString(1);
		        	if((id!=null) && (id.trim().length()>0) && (id.indexOf(".")!=-1))
		        		id=id.substring(0,id.indexOf("."));
		        	if((id!=null) && (id.trim().length()>0))
		        		maxid=Integer.parseInt(id);	
		        }
		        ++maxid;	        						
								
/*				
				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
				String num = idg.getId("per_kpi_item.id");
				Integer id = new Integer(num);
*/				
/*				vo.setInt("id", maxid);					
				vo.setString("item_id", bo.getNextItemId("item_id", "per_kpi_item")); // 随机生成KPI指标编号				
				vo.setString("itemdesc", "");
				vo.setString("description", "");
				vo.setString("cycle", "0"); // 考核周期默认为：年度
				vo.setString("item_type_desc", "");
				vo.setDate("start_date", creatDate); // 起始时间默认为：当前时间
				vo.setDate("end_date", "");
//				vo.setDate("end_date", "9999-12-31");
				vo.setString("seq", String.valueOf(maxid)); // 排序字段默认为：指标编号
				vo.setString("b0110", "");	
				vo.setString("b0110desc", "");
*/
		        kpiBean.set("id", String.valueOf(maxid));					
		        kpiBean.set("item_id", bo.getNextItemId("item_id", "per_kpi_item")); // 随机生成KPI指标编号				
		        kpiBean.set("itemdesc", "");
		        kpiBean.set("description", "");
		        kpiBean.set("cycle", "0"); // 考核周期默认为：年度
		        kpiBean.set("item_type_desc", "");
		        kpiBean.set("start_date", creatDate); // 起始时间默认为：当前时间
		        kpiBean.set("end_date", "");
		        kpiBean.set("seq", String.valueOf(maxid)); // 排序字段默认为：指标编号
		        kpiBean.set("b0110", "");	
		        kpiBean.set("b0110desc", "");
		        kpiItemType = "";	
		        
			} else
			{
				StringBuffer strsql = new StringBuffer();
				strsql.append("select * from per_kpi_item where item_id='" +item_id+ "' ");
				this.frowset = dao.search(strsql.toString());
				while(this.frowset.next())
				{
/*					
					vo.setString("id", this.frowset.getString("id"));
					vo.setString("item_id", isNull(this.frowset.getString("item_id")));
					vo.setString("itemdesc", isNull(this.frowset.getString("itemdesc")));
					vo.setString("description", isNull(this.frowset.getString("description")));
					vo.setString("cycle", isNull(this.frowset.getString("cycle")));					
					vo.setString("item_type_desc", isNull(this.frowset.getString("item_type_desc")));
					vo.setDate("start_date", this.frowset.getDate("start_date"));
					vo.setDate("end_date", this.frowset.getDate("end_date"));
					vo.setString("seq", isNull(this.frowset.getString("seq")));
//					vo.setString("b0110", isNull(this.frowset.getString("b0110")));					
					vo.setString("b0110desc", isNull(this.frowset.getString("b0110desc")));
					
//					vo.setString("method", isNull(this.frowset.getString("method")).equals("2") ? "2" : "1");
*/					
					
					kpiBean.set("id", this.frowset.getString("id"));
					kpiBean.set("item_id", isNull(this.frowset.getString("item_id")));
					kpiBean.set("itemdesc", isNull(this.frowset.getString("itemdesc")));
					kpiBean.set("description", isNull(this.frowset.getString("description")));
					kpiBean.set("cycle", isNull(this.frowset.getString("cycle")));					
					kpiBean.set("item_type_desc", isNull(this.frowset.getString("item_type_desc")));					
					String start_date=PubFunc.FormatDate(this.frowset.getDate("start_date"));
					kpiBean.set("start_date",start_date);
				    String end_date=PubFunc.FormatDate(this.frowset.getDate("end_date"));
				    kpiBean.set("end_date",end_date);										
					kpiBean.set("seq", isNull(this.frowset.getString("seq")));
					kpiBean.set("b0110", isNull(this.frowset.getString("b0110")));	
					
					String b0110 = this.frowset.getString("b0110")==null?"":this.frowset.getString("b0110").toUpperCase(); // 归属单位
					String gsunit = "";//归属单位
					if(b0110!=null && b0110.trim().length()>0 && !"".equals(b0110.trim()))
					{										    	
					    String[] temp_arr=b0110.split(",");				    	
				    	for(int i=0;i<temp_arr.length;i++)
				    	{   					    		
				    		if(temp_arr[i]==null|| "".equals(temp_arr[i]))
				    			continue;
				    		String desc = "";
				    		if(AdminCode.getCodeName("UN",temp_arr[i])!=null && !"".equals(AdminCode.getCodeName("UN",temp_arr[i])))
				    		{
				    			desc = AdminCode.getCodeName("UN",temp_arr[i]);
				    		}
				    		else if(AdminCode.getCodeName("UM",temp_arr[i])!=null&&!"".equals(AdminCode.getCodeName("UM",temp_arr[i])))
				    		{
				    			desc = AdminCode.getCodeName("UM",temp_arr[i]);
				    		}				    							    	
				    		gsunit+=desc+",";
				    	}
				    	if(gsunit.length()>0)
				    	{
				    		gsunit = gsunit.substring(0,gsunit.length()-1);				    		
				    	}				    						    	
					}
					kpiBean.set("b0110desc", isNull(gsunit));
					
//					System.out.println(isNull(this.frowset.getString("b0110desc")));
					
					kpiItemType = isNull(this.frowset.getString("item_type_desc"));			
				}
			}
			this.getFormHM().put("kpiItemTypeList",bo.getTargetType());
			this.getFormHM().put("kpiItemType",kpiItemType);
			
			if(rowSet!=null)
		    	rowSet.close();
			
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally
		{
//			this.getFormHM().put("kpiTargetvo", vo);
			this.getFormHM().put("kpiBean",kpiBean);
		}
	}
	
	public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str))
		    str = "";
		return str;
    }	
	
}