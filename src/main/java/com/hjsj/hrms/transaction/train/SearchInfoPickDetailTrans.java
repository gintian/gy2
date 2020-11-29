package com.hjsj.hrms.transaction.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:添加需求采集子项处理类</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-15:10:00:57</p>
 * @author luangaojiong
 * @version 1.0
 * 
 */
public class SearchInfoPickDetailTrans extends IBusiness {

	/* 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		 DoCodeBean addlist=new DoCodeBean();
		 ArrayList infoDetailAddList=addlist.getDynamicList(this.getFrameconn(),1);
		 
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		
		 DateStyle first_date=new DateStyle();
		 String id ="0";
		 if(hm.get("r22id")!=null)
		 {
			id=hm.get("r22id").toString();
 		 }
		 String r19id="0";
		
		/**
		 * 得到调查表id
		 */
		if(hm.get("r19id")!=null)
		{
			r19id=hm.get("r19id").toString();
			this.getFormHM().put("newr19id",r19id);
		}
				
		ContentDAO dao = new ContentDAO(this.getFrameconn());
	    PreparedStatement ps=null;
		try
		{
		    RecordVo vo = new RecordVo("R22");
		    String sql="select *  from r22 where r2202='"+id+"'";
		    
	        this.frowset=dao.search(sql.toString());
	        ArrayList list=new ArrayList();
	    	ArrayList lst=new ArrayList();
	      	/**
	      	 * 判断列是否存在
	      	 */
	      	for(int i=0;i<infoDetailAddList.size();i++)
	      	{
	      		BusifieldBean bsb=(BusifieldBean)infoDetailAddList.get(i);
	      		if(this.frowset.findColumn(bsb.getItemid())>0)
	      		{
	      			list.add(bsb);
	      		}
	      	}
	      	infoDetailAddList=list;
	      	
	      	/**
	      	 * 得到编码处理对象
	      	 */
	      	DoCodeBean doCodeBean=new DoCodeBean();
	      	RelatingFactory relatingFactory=new RelatingFactory();
	      	relatingFactory.getInstance();
		    vo.setString("r2202",id);
		    if(this.frowset.next())
		    {
			  	String value="";
			  	vo.setString("r2201",PubFunc.nullToStr(this.frowset.getString("r2201")));
			  	vo.setString("r2206",PubFunc.NullToZero(this.frowset.getString("r2206")));
			  	vo.setString("r2208",PubFunc.nullToStr(this.frowset.getString("r2208")));
			  	vo.setString("r2209",PubFunc.nullToStr(this.frowset.getString("r2209")));
			  	for(int i=0;i<infoDetailAddList.size();i++)
	      		{
	      			BusifieldBean bsb=(BusifieldBean)infoDetailAddList.get(i);
	      			if("0".equals(bsb.getCodesetid()))
	      			{

	      				if("D".equals(bsb.getItemtype()))
	      				{
	      					bsb.setValue(this.frowset.getDate(bsb.getItemid()).toString().replaceAll("-", "."));	
	      				}
			        	else if("M".equals(bsb.getItemtype()))
			        	{
			        		bsb.setValue(Sql_switcher.readMemo(this.getFrowset(),bsb.getItemid()));
			        	}	      				
	      				else
	      				{
		      				value=this.frowset.getString(bsb.getItemid());	      					
	      					bsb.setValue(value);
	      				}
	      			}
	      			else
	      			{
	      				if("1".equals(bsb.getCodeflag()))
	      				{
	      					value=this.frowset.getString(bsb.getItemid());
	      					RelatingcodeBean rcb=relatingFactory.getDisplayField(bsb);
	      					bsb.setValue(value);
	      					bsb.setViewvalue(doCodeBean.getRelCodeName(rcb,value));
	      				}
	      				else
	      				{
	      					value=this.frowset.getString(bsb.getItemid());
	      					bsb.setValue(value);
	      					String codeid=bsb.getCodesetid();
	      					bsb.setViewvalue(doCodeBean.getCodeName(codeid,value));
	      				}
	      			}
	      			lst.add(bsb);
	      		}
		  }

		  this.getFormHM().put("infoDetailAddList",lst);
          //vo=dao.findByPrimaryKey(vo);

          if(!(vo.getDate("r2205")==null))
          {
            first_date.setDateString(DateUtils.format(vo.getDate("r2205"),"yyyy-MM-dd"));          	
          }
          this.getFormHM().put("first_date",first_date);
          this.getFormHM().put("infoPickDetailTb",vo);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}

	}

}
