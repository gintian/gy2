package com.hjsj.hrms.transaction.general.inform.synthesisbrowse;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.http.HttpSession;
import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchSynthesisCardTrans extends IBusiness {

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
				if(this.getUserView()!=null&&this.getUserView().isHaveResource(IResourceConstant.CARD, tabid))
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

	
	public void execute() throws GeneralException {
			//liuy 2014-10-22 新兴保信自助服务 start
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String tabid = (String)hm.get("tabid");
			tabid=tabid==null?"":tabid;
			hm.remove("tabid");
			this.getFormHM().put("tabid", tabid);
			//liuy end
            String inforkind=(String)this.getFormHM().get("inforkind");
            String userpriv=(String)this.getFormHM().get("userpriv");
            String a0100=(String)this.getFormHM().get("a0100"); 
            String flag = (String)this.getFormHM().get("flag");
            String userbase = (String)this.getFormHM().get("userbase");
            String bizDate = (String)this.getFormHM().get("bizDate");
//          wangjh  2013-3-19  
            String username = (String)this.getFormHM().get("username");
            if (username!=null && !"".equals(username)) {
	            userView=new UserView(username,this.getFrameconn());
	            try {
					userView.canLogin();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            bizDate = bizDate==null?"":bizDate;
             
            if(a0100!=null&&a0100.trim().length()>0&& "~".equalsIgnoreCase(a0100.substring(0,1))) //dengcan 2012-2-10 如果是通过转码传过来的需解码
            { 
            	String _temp=PubFunc.keyWord_reback(a0100.substring(1));  // 有全角=
            	a0100=PubFunc.convert64BaseToString(SafeCode.decode(_temp));
            }
            if(!"infoself".equals(flag)){
            	if(!"nopriv".equals(flag)){
		            CheckPrivSafeBo checkPrivSafeBo = new CheckPrivSafeBo(this.frameconn,userView);
		            if("1".equals(inforkind)){
		            	userbase = checkPrivSafeBo.checkDb(userbase);
		            	a0100=checkPrivSafeBo.checkA0100("", userbase, a0100, "");
		        	}else if("2".equals(inforkind)){
		        		a0100=checkPrivSafeBo.checkOrg(a0100, "4");
		        	}else{
		        		a0100=checkPrivSafeBo.checkOrg(a0100, "4");
		        	}
            	}
            }
            else{
                a0100 = this.userView.getA0100();
                userbase = this.userView.getDbname();
            }
            this.getFormHM().put("bizDate", bizDate);
            this.getFormHM().put("a0100", a0100);
            this.getFormHM().put("userbase", userbase);
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
            	else if("6".equals(inforkind))// 基准岗位
            	{
            		this.getFormHM().put("cardtype","H");
            		strkind="H";            		
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
    	   String dbType="1";
   		switch(Sql_switcher.searchDbServer())
   	    {
   			  case Constant.MSSQL:
   		      {
   		    	  dbType="1";
   				  break;
   		      }
   			  case Constant.ORACEL:
   			  { 
   				  dbType="2";
   				  break;
   			  }
   			  case Constant.DB2:
   			  {
   				  dbType="3";
   				  break;
   			  }
   	    }
   		this.getFormHM().put("dbType", dbType);
	}

}
