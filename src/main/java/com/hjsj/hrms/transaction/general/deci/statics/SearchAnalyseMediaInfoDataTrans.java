/**
 * 
 */
package com.hjsj.hrms.transaction.general.deci.statics;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Owner
 *
 */
public class SearchAnalyseMediaInfoDataTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String dbpre=(String)this.getFormHM().get("dbpre");//人员库
		String A0100=(String)this.getFormHM().get("a0100");      //获得人员ID
		//String setname=(String)this.getFormHM().get("setname");      //获得人员ID
		
		if("A0100".equals(A0100))
			A0100=userView.getUserId();
		  //操纵表的名称
	    String tablename=dbpre + "A00";
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
		strsql.append(".a0100 from mediasort," + tablename);
		strsql.append(" where ");
		strsql.append(tablename);
		strsql.append(".A0100='");
		strsql.append(A0100);
		strsql.append("' and ");
		strsql.append(tablename);
		strsql.append(".flag<>'p' and mediasort.flag=");
		strsql.append(tablename);
		strsql.append(".flag");
        try
		{	   
          ContentDAO dao=new ContentDAO(this.getFrameconn());
		  this.frowset = dao.search(strsql.toString());             //获取子集的纪录数据
		  while(this.frowset.next())
		  {
		     DynaBean vo=new LazyDynaBean();
		     vo.set("a0100",this.getFrowset().getString("A0100"));
		     vo.set("i9999",Integer.toString(this.getFrowset().getInt("I9999")));
		     vo.set("title",this.getFrowset().getString("TITLE"));
		     vo.set("flag",this.getFrowset().getString("SORTNAME"));
		     System.out.println(this.getFrowset().getString("STATE"));
		     vo.set("state",this.getFrowset().getString("STATE"));
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
