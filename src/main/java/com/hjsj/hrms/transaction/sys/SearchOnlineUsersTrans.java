package com.hjsj.hrms.transaction.sys;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;


/**
 * <p>Title:SearchOnlineUsersTrans</p>
 * <p>Description:查询在线用户列表</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 8, 2005:9:05:01 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SearchOnlineUsersTrans extends IBusiness {

    /**
     * 
     */
    public SearchOnlineUsersTrans() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
//      ArrayList list0 =userView.getPrivFieldList("A04");
//      if(list0!=null)
//      {
//          for(int i=0;i<list0.size();i++)
//          {
//              FieldItem item=(FieldItem)list0.get(i);
//              //System.out.println("item="+item.toString());
//          }
//      }
//          
//      FactorList list=new FactorList("1*2*3","G0101=ss`C0102=ss`C0101=12`","usr",true,true,true,1,userView.getUserName());
//      if(!list.existError())
//      {
//    	 System.out.println("--->99999");
//    	 return;
//      }
//      String expr=list.getSqlExpression();
//      System.out.println("SQL="+expr);  
//      list.reset("1+2","A0101=1`A0405=1`","usr",false,true,true,1,userView.getUserName());
//      expr=list.getSqlExpression();
//      System.out.println("SQL="+expr);      
//      list.reset("1+2","A0101=1`A0405=1`","usr",true,true,false,1,userView.getUserName());
//      expr=list.getSqlExpression();   
//      System.out.println("SQL="+expr);  
//      list.reset("1+2","A0101=1`A0405=1`","usr",false,true,false,1,userView.getUserName());
//      expr=list.getSqlExpression();   
//      System.out.println("SQL="+expr);  
//      CombineFactor factor=new CombineFactor();
//      ArrayList listfactor=new ArrayList();
//      listfactor.add("1+2|A0101=`A0111=$THISMONTH[]`");
//      listfactor.add("(1+2*3)|A0101=1`A0405=1`A0405=45`");
//      System.out.println("Expression="+factor.getCombineFactorExpr(listfactor,0));
//      listfactor.clear();
//      listfactor.add("1+2|A0101=`A0111=$THISMONTH[]`");
//      listfactor.add("(1+2*3)|A0101=1`A0405=1`A0405=45`");
//      System.out.println("Expression="+factor.getCombineFactorExpr(listfactor,1));
      
    }

}
