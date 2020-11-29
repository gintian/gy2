package com.hjsj.hrms.transaction.dutyinfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:SearchMudiaInfoTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 7, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class SearchMudiaInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		String code = (String)reqhm.get("code");
		String kind = (String)reqhm.get("kind");
		this.getFormHM().put("kind",kind);
		//String A0100=(String)this.getFormHM().get("a0100"); 
		//获得人员ID
		if(code==null){
			code="";
		}
		//操纵表的名称
		String tablename="";
		if("0".equalsIgnoreCase(kind))
			tablename="k00";
		else //if(kind.equalsIgnoreCase("1"))
			tablename="b00";
	      
		  StringBuffer cond=new StringBuffer();
		  cond.append("select flag,sortname from mediasort");
		  ContentDAO dao=new ContentDAO(this.getFrameconn());
		  String mediasort="''";
		  int n=0;
		  try{
			  this.frowset=dao.search(cond.toString());
	          while(this.frowset.next())
	          {
	              String flagsort=this.frowset.getString("flag");
	              /**多媒体类型权限分析*/
	              if(userView.isSuper_admin())
	              {
              		mediasort+=",";
	              	mediasort+="'" + flagsort + "'";
	              	n++;
	              }
	              else
	              {
		              if(userView.hasTheMediaSet(flagsort))
		              {
		           		mediasort+=",";
		              	mediasort+="'" + flagsort + "'";
		              	n++;
		              }	    
	              }        
	          }
		 }catch(Exception e)
		 {}
	
		  cond.delete(0,cond.length());
		  cond.append("select flag,sortname from mediasort where flag in (");
		  cond.append(mediasort);
		  cond.append(")");
		  if("0".equalsIgnoreCase(kind))
			  cond.append(" and dbflag ='3'");
		  else
			  cond.append(" and dbflag ='2'");
		  /**应用库前缀过滤条件*/
		 // System.out.println(cond.toString());
		 this.getFormHM().put("sortcond",cond.toString());
		StringBuffer strsql=new StringBuffer();
		String codeitemdesc="";
		try{
		    strsql.append("select codeitemdesc from ");
		    strsql.append("organization ");
		    strsql.append(" where codeitemid='");
		    strsql.append(code);
		    strsql.append("'");
		    this.frowset = dao.search(strsql.toString()); 
		    if(this.frowset.next())
			{
		    	codeitemdesc=this.getFrowset().getString("codeitemdesc");		
			 }
		}catch(Exception e){}
		strsql.delete(0,strsql.length());
		//保存sql的字符串
	    ArrayList list=new ArrayList();                             //封装子集的数据
		//if("A0100".equals(A0100) || "su".equalsIgnoreCase(A0100))
			//A0100=userView.getUserId();                             //如果A0100的值为A0100表示员工资助取其ID
		strsql.append("select ");
		strsql.append(tablename);
		if("0".equalsIgnoreCase(kind))
			strsql.append(".e01a1,");
		else //if(kind.equalsIgnoreCase("1"))
			strsql.append(".b0110,");
		strsql.append(tablename);
		strsql.append(".i9999,");
		strsql.append(tablename);
		strsql.append(".state,");
		strsql.append(tablename);
		strsql.append(".title,");
		strsql.append(tablename);
		strsql.append(".flag,");
		strsql.append("mediasort.sortname from mediasort, organization ," + tablename);
		strsql.append(" where ");
		strsql.append(tablename);
		if("0".equalsIgnoreCase(kind))
			strsql.append(".e01a1='");
		else //if(kind.equalsIgnoreCase("1"))
			strsql.append(".b0110='");
		strsql.append(code);
		strsql.append("' and ");
		strsql.append("organization.codeitemid=");
		strsql.append(tablename);
		if("0".equalsIgnoreCase(kind))
			strsql.append(".e01a1");
		else //if(kind.equalsIgnoreCase("1"))
			strsql.append(".b0110");
		strsql.append(" and ");
		strsql.append(tablename);
		strsql.append(".flag<>'p' and mediasort.flag=");
		strsql.append(tablename);
		strsql.append(".flag");
        //sunx,1110,+
		if(mediasort!=null&&mediasort.length()>0)
		  strsql.append(" and mediasort.flag in("+mediasort+")");
        try
		{	     
		  this.frowset = dao.search(strsql.toString());             //获取子集的纪录数据
		  while(this.frowset.next())
		  {
		     DynaBean vo=new LazyDynaBean();
		     if("0".equalsIgnoreCase(kind))
		    	 vo.set("a0100",this.getFrowset().getString("e01a1"));
		     else //if(kind.equalsIgnoreCase("1"))
		    	 vo.set("a0100",this.getFrowset().getString("b0110"));
		     vo.set("i9999",Integer.toString(this.getFrowset().getInt("I9999")));
		     vo.set("title",this.getFrowset().getString("TITLE"));
		     vo.set("flag",this.getFrowset().getString("SORTNAME"));
		     vo.set("state",this.getFrowset().getString("STATE"));
	         list.add(vo);
		  }
		 }catch(SQLException sqle)
		 {
		   sqle.printStackTrace();
		   throw GeneralExceptionHandler.Handle(sqle);
		 }
		 finally
		 {
    	    this.getFormHM().put("detailinfolist",list);
    	    this.getFormHM().put("codeitemdesc",codeitemdesc);
		 }

	}

}
