package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * <p>Title:SearchYesORnoTrans.java</p>
 * <p>Description>:判断结果表中是否有分数相同但考核等级不同的考核对象</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Feb 28, 2011 10:19:32 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class SearchYesORnoTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		try
		{			
			String plan_id=(String)this.getFormHM().get("planid");
			String flag=(String)this.getFormHM().get("flag");
			String code=(String)this.getFormHM().get("code");
			
			PerEvaluationBo bo = new PerEvaluationBo(this.getFrameconn());
			String userPriv = bo.getPrivWhere(this.userView);
	    	if (code!=null && code.trim().length()>0 && !"-1".equals(code))
			{
				if (AdminCode.getCodeName("UN", code) != null && AdminCode.getCodeName("UN", code).length() > 0)
					userPriv += " and b0110 like '" + code + "%'";
				else if (AdminCode.getCodeName("UM", code) != null && AdminCode.getCodeName("UM", code).length() > 0)
					userPriv += " and e0122 like '" + code + "%'";
			}
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			ArrayList list=new ArrayList();
			ArrayList alist=new ArrayList();
			HashMap map=new HashMap();
			
			String sql = "select a0101,score,resultdesc from per_result_"+plan_id+" ";
			if(userPriv!=null&&userPriv.trim().length()>0)
				 sql+=" where 1=1 "+userPriv;
			sql+=" order by score desc ";
			this.frowset=dao.search(sql);
			LazyDynaBean abean=null;
			LazyDynaBean bean=null;
			while(this.frowset.next())
			{	
				String a0101=this.frowset.getString("a0101")!=null?this.frowset.getString("a0101"):"";
				String score=this.frowset.getString("score")!=null?this.frowset.getString("score"):"";				
				String resultdesc=this.frowset.getString("resultdesc")!=null?this.frowset.getString("resultdesc"):"";
				
				bean=new LazyDynaBean();
				bean.set("a0101",this.frowset.getString("a0101")!=null?this.frowset.getString("a0101"):"");
				bean.set("score",this.frowset.getString("score")!=null?this.frowset.getString("score"):"");
				bean.set("resultdesc",this.frowset.getString("resultdesc")!=null?this.frowset.getString("resultdesc"):"");
				
				alist.add(bean);
				if(score!=null && score.length()>0)
					map.put(score, alist);				
				if(this.frowset.next()!=false)
				{
					if(score!=null && score.length()>0)
						if(!score.equalsIgnoreCase(this.frowset.getString("score")!=null?this.frowset.getString("score"):""))
							alist=new ArrayList();
					
				}				
				this.frowset.previous();

			}
						
			Set keySet=map.keySet();
			java.util.Iterator t=keySet.iterator();
			while(t.hasNext())
			{
				String strKey = (String)t.next();  //键值	    
				ArrayList strValue = (ArrayList)map.get(strKey);   //value值   
				LazyDynaBean lybean=null;
				String resultdesc="";
				String yesORno="N";
				for(int i=0;i<strValue.size();i++)
				{						
					lybean=(LazyDynaBean)strValue.get(i);
					if(i==0)
					{
						resultdesc=(String)lybean.get("resultdesc");
					}
					String resultdescnext=(String)lybean.get("resultdesc");
					
					if(!resultdesc.equalsIgnoreCase(resultdescnext))
					{
						yesORno="Y";
						break;						
					}					
				}
				if("Y".equalsIgnoreCase(yesORno))
				{					
					this.getFormHM().put("yScoreNGrade","yes");
				}			
			}
			this.getFormHM().put("flag",flag);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}