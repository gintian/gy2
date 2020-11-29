package com.hjsj.hrms.transaction.performance.achivement.kpiOriginalData;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 *<p>Title:SaveKpiOriginalDataTrans.java</p> 
 *<p>Description:保存KPI原始数据</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:July 29, 2011</p> 
 *@author JinChunhai
 *@version 5.0
 */

public class SaveKpiOriginalDataTrans extends IBusiness 
{
	
	public void execute() throws GeneralException 
	{
		
		StringBuffer buf=new StringBuffer("");
		ArrayList obList = new ArrayList();
		String msg="ok";
		try
		{		
			
			ContentDAO dao = new ContentDAO(this.frameconn);
			
			String s_str=(String)this.getFormHM().get("s_str");		
			s_str = s_str.replaceAll("／", "/");
			if(s_str!=null && s_str.trim().length()>0)
			{
				String[] s_str_arr=s_str.split(",");
				
				for(int i=0;i<s_str_arr.length;i++)
				{
					if(s_str_arr[i]==null || s_str_arr[i].trim().length()<=0)
						continue;
					String[] s_t=s_str_arr[i].split("/");
					/**指标分值*/
					String id=s_t[0];					
					String p_score="0.00";
					if(s_t.length==2)
						p_score=s_t[1]==null || s_t[1].trim().length()<=0?"0.00":s_t[1];										
				    
					if((p_score.indexOf(".")!=-1) && (p_score.substring(0,p_score.indexOf("."))).length()>8)					
					{
						msg = "数字'"+ p_score +"'超出了数字表示范围！";	
						break;
						
					}else if((p_score.indexOf(".")==-1) && (p_score.length()>8))
					{
						msg = "数字'"+ p_score +"'超出了数字表示范围！";	
						break;
					}
					//firefox 对js的execCommand（允许运行命令来操纵可编辑区域的内容）支持的不好，所以后台增加校验
					String reg = "^[-\\+]?[0-9]+(\\.[0-9]+)?$";
					if(p_score.matches(reg)) {			
					    buf.append("update per_kpi_data set actual_value="+p_score);
					    buf.append(" where id='"+id+"' ");				    
					    obList.add(buf.toString());
						buf.setLength(0);
					}else {
						msg = "'"+ p_score +"'不是数字！";
						break;
					}
				}
			}
			
			dao.batchUpdate(obList);
						
			if(msg==null || msg.trim().length()==0)
			{
				msg="ok";
			}
			this.getFormHM().put("msg",SafeCode.encode(msg));
						
			
//			KpiOriginalDataBo bo = new KpiOriginalDataBo(this.getFrameconn(),this.userView);						
//			bo.saveData(matters);			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
}
