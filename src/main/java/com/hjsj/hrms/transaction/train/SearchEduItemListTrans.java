package com.hjsj.hrms.transaction.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:培训项目列表</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-16:17:10:55</p>
 * @author luangaojiong
 * @version 1.0
 * 
 */
public class SearchEduItemListTrans extends IBusiness {


	public void execute() throws GeneralException {
		
		/**
		 * 得到活动编号
		 */
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String eduid="0";
		if(hm.get("a_id")!=null)
		{
			eduid=hm.get("a_id").toString();
			this.getFormHM().put("eduid",eduid);
		}
		else
		{
			if(this.getFormHM().get("eduid")!=null)
			{
				eduid=this.getFormHM().get("eduid").toString();
			}
		}
		ArrayList list=new ArrayList();
		try
		{
				  String sql="select R13.R1301,R13.R1302,R13.R1303,R13.R1304,R13.R1305,R13.R1307,R13.R1309,R13.B0110 "; 
				  String sql2=" from R13,R41 where R13.R1301=R41.R4105 and R13.R1309=0";
				  String sql3="  and R41.R4103='"+eduid+"'";
				  StringBuffer sb=new StringBuffer();
				  sb.append(sql);
				  sb.append(sql2);
				  sb.append(sql3);
			 	  ContentDAO dao=new ContentDAO(this.getFrameconn());
			     
			     // System.out.println("------->com.hjsj.hrms.transaction.infopick.SearchEduItemListTrans---->while--->\n\t"+sb.toString());
			      this.frowset = dao.search(sb.toString());
			      while(this.frowset.next())
			      {
			      	DynaBean vo=new LazyDynaBean();
			      	vo.set("r1301",PubFunc.nullToStr(this.frowset.getString("r1301"))); 
			      	vo.set("r1302",PubFunc.nullToStr(this.frowset.getString("r1302")));  
			      	vo.set("r1303",PubFunc.nullToStr(this.frowset.getString("r1303")));  
			      	vo.set("r1304",PubFunc.nullToStr(this.frowset.getString("r1304"))); 
			      	vo.set("r1305",PubFunc.nullToStr(this.frowset.getString("r1305")));  
			      	vo.set("r1307",PubFunc.nullToStr(this.frowset.getString("r1307")));  
			    	vo.set("r1309",new Integer(PubFunc.chgNullInt(this.frowset.getString("r1309"))));
			      	vo.set("b0110",PubFunc.nullToStr(this.frowset.getString("b0110")));   
			      	list.add(vo);
			      }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);			
		}
		finally
		{
			 this.getFormHM().put("eduItemlist",list);
		}
		
	}


}
