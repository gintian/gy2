package com.hjsj.hrms.transaction.kq.team.array_group;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class QueryCondDesignTrans extends IBusiness {
	  
	public void execute() throws GeneralException {
     
		String [] right_fields=(String [])this.getFormHM().get("right_fields");
//		System.out.println("trans-->"+right_fields.toString());
        ArrayList list=new ArrayList();
//         ArrayList selectfieldlist=new ArrayList();
//        for(int k=0;k<right_fields.length;k++)
//        {
//        	String selectfieldname=right_fields[k];
//            if(selectfieldname==null||selectfieldname.equals(""))
//               continue;
//            FieldItem item = DataDictionary.getFieldItem(selectfieldname.toUpperCase());
//            selectfieldlist.add(item);
//        }
//        this.getFormHM().put("selectfieldlist",selectfieldlist);
      
        int j=0;
        StringBuffer expression=new StringBuffer();
        /**信息类型定义default=1（人员类型）*/
        int nInform=1;
        try
         {
          /**定义条件项*/
          FieldItem item=null;
          for(int i=0;i<right_fields.length;i++)
          {
            String fieldname=right_fields[i];
            if(fieldname==null|| "".equals(fieldname))
               continue;
            item=DataDictionary.getFieldItem(fieldname.toUpperCase());

            Factor factor=null;
            if(item!=null)
            {
               /**已定义的因子再现*/
//               if(list.size()>0)
//               {
//                  factor=findFactor(fieldname,list);
//                  if(factor!=null)
//                   continue;
//                }
                factor=new Factor(nInform);
                factor.setCodeid(item.getCodesetid());
                factor.setFieldname(item.getItemid());
                factor.setHz(item.getItemdesc());
                factor.setFieldtype(item.getItemtype());
                factor.setItemlen(item.getItemlength());
                factor.setItemdecimal(item.getDecimalwidth());
                factor.setOper("=");//default
                factor.setLog("*");//default
                list.add(factor);
                ++j;
                expression.append(j);
                expression.append("*");
              }                
            }
              if(expression.length()>0)
            	expression.setLength(expression.length()-1);
            }
             catch(Exception ee)
             {
             	ee.printStackTrace();
             	throw GeneralExceptionHandler.Handle(ee);
             }
             finally
             {
                 this.getFormHM().put("factorlist",list);
                 this.getFormHM().put("expression",expression.toString());
             }
             ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
 			 String userOrgId=managePrivCode.getPrivOrgId();  
             KqParameter para=new KqParameter(this.getFormHM(),this.userView,"UN"+userOrgId,this.getFrameconn());
             String nbase=para.getNbase();
             ArrayList alistd=new ArrayList();
             alistd=this.getDbase(nbase);
             this.getFormHM().put("dblist",alistd);
             String dbpre=getFirstDbase(alistd);
             this.getFormHM().put("dblist",alistd);
             this.getFormHM().put("dbpre",dbpre);
    }
	  private Factor findFactor(String name,ArrayList list)
	    {
	    	Factor factor=null;
	    	for(int i=0;i<list.size();i++)
	    	{
	    		factor=(Factor)list.get(i);
	    		if(name.equalsIgnoreCase(factor.getFieldname()))
	    			break;
	    		factor=null;
	    	}
	    	return factor;
	    }
	  private String getFirstDbase(ArrayList dblist) throws GeneralException
	   {
		  
	     	CommonData vo=(CommonData)dblist.get(0);
	    	return vo.getDataValue();
		  
	   }
	   
	   private ArrayList getDbase(String dlist) throws GeneralException
	   {
		   
		   StringBuffer stb=new StringBuffer();
		   ContentDAO dao=new ContentDAO(this.getFrameconn());
		   ArrayList dbaselist=userView.getPrivDbList(); 
		   ArrayList slist=new ArrayList();
		  // String[] base=dlist.split(",");
		   try{
			   CommonData cd = new CommonData("all","全部人员库");
		        slist.add(cd);
			    stb.append("select * from dbname");
			    this.frowset = dao.search(stb.toString());
			    while(this.frowset.next())
			    {
			    	 String dbpre=this.frowset.getString("pre");
	              	   for(int i=0;i<dbaselist.size();i++)
	            	   {
	               		 String userbase=dbaselist.get(i).toString();
	               		  if((dlist.indexOf(userbase)!=-1&&dbpre==userbase)||(dlist.indexOf(userbase)!=-1&&dbpre.equals(userbase)))
	               		  {
	               			  CommonData vo=new CommonData(this.frowset.getString("pre"),this.frowset.getString("dbname"));
	                           slist.add(vo);
	               		  }
	            	   }
			       }
			    if(slist.size() == 2)
	            	slist.remove(0);
			  } catch(Exception sqle)
	          {
			         sqle.printStackTrace();
			         throw GeneralExceptionHandler.Handle(sqle);            
		       }
			  return slist;
			
	   }
}
