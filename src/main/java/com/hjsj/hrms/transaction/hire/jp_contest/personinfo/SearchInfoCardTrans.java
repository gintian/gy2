package com.hjsj.hrms.transaction.hire.jp_contest.personinfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.servlet.http.HttpSession;
import javax.sql.RowSet;
import java.util.ArrayList;
/**
 * 
 *<p>Title:SearchInfoCardTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 25, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class SearchInfoCardTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String inforkind=(String)this.getFormHM().get("inforkind");
        String userpriv=(String)this.getFormHM().get("userpriv");
        String strkind="A";
        if(inforkind!=null)
        {
        	if("1".equals(inforkind))
        	{
        		this.getFormHM().put("cardtype","A");
        		strkind="A";            		
        	}
        	else if("2".equals(inforkind))
        	{
        		this.getFormHM().put("cardtype","B");
        		strkind="B";            		
        	}
        	else 
        	{
        		this.getFormHM().put("cardtype","K");
        		strkind="K";            		
        	}
        	
        }
        /**当前类型下的登记表列表*/
       ArrayList cardlist=searchcardlist(strkind);
       HttpSession session=(HttpSession)this.getFormHM().get("session");
	   session.setAttribute("changtab_synthesis","card");
	   this.getFormHM().put("cardlist", cardlist);
	   this.getFormHM().put("userpriv", userpriv!=null&&userpriv.length()>0?userpriv:"");
	}

	/**
	 * 取得卡片列表
	 * infortype A 人员，B单位,K职位 P绩效
	 * @throws GeneralException
	 */
	private ArrayList searchcardlist(String infortype)throws GeneralException
	{
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList cardlist=new ArrayList();
		StringBuffer buf=new StringBuffer();
		try
		{
			ArrayList paralist=new ArrayList();		
			paralist.add(infortype);
			buf.append("select tabid,name,flaga from rname where flaga=?"  );
			
			RowSet rset=dao.search(buf.toString(),paralist);
			while(rset.next())
			{
				String tabid=rset.getString("tabid");
				if(this.userView.isHaveResource(IResourceConstant.CARD, tabid))
				{
					CommonData data=new CommonData();
					data.setDataValue(tabid);
					data.setDataName(rset.getString("name"));
					cardlist.add(data);
				}
			}//while loop end.
			return cardlist;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}


}
