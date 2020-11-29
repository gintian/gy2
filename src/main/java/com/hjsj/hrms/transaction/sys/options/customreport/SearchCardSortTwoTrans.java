/*
 * Created on 2006-2-7
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.sys.options.customreport;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchCardSortTwoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		//this.getFormHM().clear();
		// TODO Auto-generated method stub 
		TreeItemView treeItem=new TreeItemView();
		treeItem.setName("root");		
		treeItem.setIcon("/images/add_all.gif");	
		treeItem.setTarget("mil_body");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		//获得等级表的类型A人员B机构K职位
	    String strInfkind=(String)hm.get("inforkind");
		String rootdesc=ResourceFactory.getProperty("general.card.allcard");
		this.getFormHM().put("inforkind",strInfkind);
	    treeItem.setRootdesc(rootdesc);
	    if("1".equals(strInfkind))
	    	strInfkind="A";
	    else if("2".equals(strInfkind))
	    	strInfkind="B";
	    else if("4".equals(strInfkind))
	    	strInfkind="K";
//	    treeItem.setLoadChieldAction("/general/card/searchcardstree?flag=1&moduleflag=&flaga=" + strInfkind);
	    treeItem.setLoadChieldAction("/general/card/searchcardstreetwo?flag=1");
	    treeItem.setAction("javascript:void(0)");	   
	    this.getFormHM().put("treeCode",treeItem.toJS());	  
//	    ArrayList dblist=userView.getPrivDbList();
//		 String dbcond=commonfunction.getDbcondString(dblist);
//		this.getFormHM().put("dbcond",dbcond);
//		
//		String dbname=(String)hm.get("dbname");
//		dbname=dbname!=null&&dbname.trim().length()>0?dbname:"";
//		hm.remove("dbname");
//		
//		String userbase="";
//		if(dblist.size()>0){
//			for(int i=0;i<dblist.size();i++){
//				if(dbname.toUpperCase().indexOf(dblist.get(i).toString().toUpperCase())!=-1){
//					userbase=dblist.get(i).toString(); 
//					break;
//				}
//			}  
//        }else{
//        	userbase="Usr";
//        }
//		
//		userbase=userbase!=null&&userbase.trim().length()>0?userbase:"Usr";
//		this.getFormHM().put("userbase",userbase);
//		ContentDAO dao=new ContentDAO(this.getFrameconn());
//
//        StringBuffer sql=new StringBuffer();
//        /**自助平台的用户*/
//        if(this.userView.getStatus()==4)
//        {
//        	UserObjectBo userbo=new UserObjectBo(this.getFrameconn());
//        	userbo.createResultTable(userbase, strInfkind, this.userView.getUserName());
//        }
//        if("A".equals(strInfkind))
//        {
//        	sql.append("select ");
//        	sql.append(userbase);
//        	sql.append("A01.a0100,");
//        	sql.append(userbase);
//        	sql.append("A01.a0101 from "+this.userView.getUserName());
//        	sql.append(userbase);
//        	sql.append("Result,");
//        	sql.append(userbase);
//        	sql.append("A01 where "+this.userView.getUserName());
//        	sql.append(userbase);
//        	sql.append("Result.a0100=");
//        	sql.append(userbase);
//        	sql.append("A01.a0100 order by ");
//        	sql.append(userbase);
//        	sql.append("a01.a0000");
//        }    	
//	    else if("B".equals(strInfkind)) {
//        	sql.append("select "+this.userView.getUserName()+"BResult.b0110,organization.codeitemdesc from "+this.userView.getUserName()+"BResult,organization where organization.codeitemid="+this.userView.getUserName()+"BResult.b0110");
//        }    	
//	    else if("K".equals(strInfkind))
//	    	sql.append("select "+this.userView.getUserName()+"KResult.E01A1,organization.codeitemdesc from "+this.userView.getUserName()+"KResult,organization where organization.codeitemid="+this.userView.getUserName()+"KResult.E01A1");       
//	    try
//		{
//	    	this.frowset=dao.search(sql.toString());
//            String a0100=null;
//            if("A".equals(strInfkind))
//            {
//        	   while(this.frowset.next())
//        	  {
//        		if(a0100==null)
//        			a0100=this.frowset.getString("a0100");
//        	  } 
//            }else if("B".equals(strInfkind)) {
//            	 while(this.frowset.next())
//           	  {
//           		if(a0100==null)
//           			a0100=this.frowset.getString("b0110");
//           	  } 
//           }    	
//    	    else if("K".equals(strInfkind)){
//    	      while(this.frowset.next())
//           	  {
//           		if(a0100==null)
//           			a0100=this.frowset.getString("e01a1");
//           	  } 
//    	    }        	
//        	this.getFormHM().put("a0100",a0100);
//        	sql.delete(0,sql.length());
//        	sql.append("SELECT tabid as tabid from Rname where FlagA='");
//        	sql.append(strInfkind);
//        	sql.append("' order by tabid");
//        	cat.debug(sql);
//        	this.frowset=dao.search(sql.toString());
//        	boolean istabid=false;
//        	String zpid=userView.getResourceString(0);
//        	while(!istabid && this.frowset.next())
//        	{
//        		  if((userView.isHaveResource(IResourceConstant.CARD,this.frowset.getString("tabid"))) || userView.getUserName().equalsIgnoreCase("su"))
//        		  {
//        		     this.getFormHM().put("tabid",this.frowset.getString("tabid"));
//        		     istabid=true;
//        		  }
//        	}
//           	if(!istabid)
//        	{
//        		this.getFormHM().put("tabid","-1");
//        		// throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("general.card.nopriv"),"",""));
//        		
//        	}
//        }catch(Exception e)
//		{
//        	e.printStackTrace();
//        	throw GeneralExceptionHandler.Handle(e);
//        }
//        String dbType="1";
//		switch(Sql_switcher.searchDbServer())
//	    {
//			  case Constant.MSSQL:
//		      {
//		    	  dbType="1";
//				  break;
//		      }
//			  case Constant.ORACEL:
//			  { 
//				  dbType="2";
//				  break;
//			  }
//			  case Constant.DB2:
//			  {
//				  dbType="3";
//				  break;
//			  }
//	    }
//		this.getFormHM().put("dbType", dbType);
	}

}
