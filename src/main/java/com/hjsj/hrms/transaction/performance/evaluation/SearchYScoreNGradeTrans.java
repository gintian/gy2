package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

/**
 * <p>Title:SearchYScoreNGradeTrans.java</p>
 * <p>Description>:列出分数相同但考核等级不同的考核对象</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Feb 28, 2011 14:17:38 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class SearchYScoreNGradeTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		try
		{			
			String plan_id=(String)this.getFormHM().get("planid");
			Hashtable planParamSet = new Hashtable();
			LoadXml loadxml = new LoadXml(this.getFrameconn(), plan_id);
		    planParamSet = loadxml.getDegreeWhole();
			int KeepDecimal = Integer.parseInt((String) planParamSet.get("KeepDecimal"));
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			ArrayList list=new ArrayList();
			ArrayList alist=new ArrayList();
			HashMap map=new HashMap();
			
			this.frowset=dao.search("select a0101,score,resultdesc from per_result_"+plan_id+" order by score desc");
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
					this.frowset=dao.search("select a0101,score,resultdesc from per_result_"+plan_id+" where score="+strKey+" order by score desc");
					LazyDynaBean xbean=null;
					while(this.frowset.next())
					{	
						double scoe = this.frowset.getDouble("score");
						String score = Double.toString(scoe);						
						xbean=new LazyDynaBean();
						xbean.set("a0101",this.frowset.getString("a0101")!=null?this.frowset.getString("a0101"):"");
						xbean.set("score",PubFunc.round(score, KeepDecimal));
						xbean.set("resultdesc",this.frowset.getString("resultdesc")!=null?this.frowset.getString("resultdesc"):"");
						list.add(xbean);
					}
					this.getFormHM().put("yScoreNGrade","yes");
				}
//					list.add(strValue);				
			}
			this.getFormHM().put("yScoreNGradeList",list);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}