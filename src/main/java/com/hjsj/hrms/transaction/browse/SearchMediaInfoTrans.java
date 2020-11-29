/*
 * Created on 2006-3-23
 *
 */
package com.hjsj.hrms.transaction.browse;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author wlh
 *
 */
public class SearchMediaInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
 	public void execute() throws GeneralException {
		    
// 	        String subsetPriv = userView.analyseTablePriv("A00");    
// 	        if(subsetPriv.equals("0"))
// 	            throw GeneralExceptionHandler.Handle(new GeneralException("","您没有使用该功能的权限！","",""));
 	    
			String userbase=(String)this.getFormHM().get("userbase");//人员库
			String A0100=(String)this.getFormHM().get("a0100");      //获得人员ID
			//String setname=(String)this.getFormHM().get("setname");      //获得人员ID
			
			if("A0100".equals(A0100))
				A0100=userView.getUserId();
			  //操纵表的名称
		    String tablename=userbase + "A00";
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
		    StringBuffer strsql=new StringBuffer();
     		strsql.delete(0,strsql.length());
			//保存sql的字符串
		    ArrayList list=new ArrayList();                             //封装子集的数据
			if("A0100".equals(A0100) || "su".equalsIgnoreCase(A0100))
				A0100=userView.getUserId();                             //如果A0100的值为A0100表示员工资助取其ID
			strsql.append("select mediasort.SORTNAME,");
			strsql.append(tablename);
			strsql.append(".title,");
			strsql.append(tablename);
			strsql.append(".I9999,");
			strsql.append(tablename);
			strsql.append(".STATE,");
			strsql.append(tablename);
			strsql.append(".a0100 from " + tablename);
			strsql.append(" left join mediasort ");
			strsql.append("on mediasort.flag=");
            strsql.append(tablename);
            strsql.append(".flag where ");
			strsql.append(tablename);
			strsql.append(".A0100='");
			strsql.append(A0100);
			strsql.append("' and (");
			strsql.append(tablename);
			strsql.append(".flag<>'P' or ");
			strsql.append(tablename);
			strsql.append(".flag is null)");
//			sunx,1110,+
			if(mediasort!=null&&mediasort.length()>0)
			  strsql.append(" and (mediasort.flag is null or mediasort.flag in("+mediasort+"))");
	        try
			{	   
	          this.frowset = dao.search(strsql.toString());             //获取子集的纪录数据
			  while(this.frowset.next())
			  {
				  String title = this.getFrowset().getString("TITLE");
				  if(title==null){
					  title="";
				  }
			     DynaBean vo=new LazyDynaBean();
			     vo.set("a0100",this.getFrowset().getString("A0100"));
			     vo.set("i9999",Integer.toString(this.getFrowset().getInt("I9999")));
			     vo.set("title",("".equalsIgnoreCase(title)?"未知文件名":title));
			     vo.set("flag",StringUtils.isEmpty(this.getFrowset().getString("SORTNAME")) ? "" : this.getFrowset().getString("SORTNAME"));
			     String state=this.getFrowset().getString("STATE");
			     vo.set("state",(state==null|| "".equals(state))?"3":state);
		         list.add(vo);
			  }
			 }catch(SQLException sqle)
			 {
			   sqle.printStackTrace();
			   throw GeneralExceptionHandler.Handle(sqle);
			 }
			 this.getFormHM().put("detailinfolist",list); 
			 //this.getFormHM().put("setname","A00");
		}

}
