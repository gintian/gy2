package com.hjsj.hrms.transaction.general.statics;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;

//
public class GetGeneralNameTrans extends IBusiness {
	
	
	  private ArrayList splits(String strtext,String token)
	  {
	    int idx=0;
	    ArrayList slist=new ArrayList();
	    StringBuffer strx=new StringBuffer();
	    strx.append(strtext);
	    idx=strx.indexOf(token);
	    while(idx!=-1)
	    {
	      String value=strx.substring(0,idx);
	      strx.delete(0,idx+1);
	      slist.add(value);
	      idx=strx.indexOf(token);
	    }
	    String mm=strx.substring(idx+1,(int)strx.length());
	    slist.add(mm);
	    return slist;
	  }
	
	/**
	 * 求得表达式分析串对应的名称
	 * @param expression
	 * @param list
	 * @return
	 * @throws GeneralException
	 */
    private String getLegendTitle(String expression,List list)throws GeneralException
    {
       String strlastno="";
       int ncurr=0;
       int nmax=list.size();
       String tem=(String)this.getFormHM().get("gvalue");
       tem =SafeCode.decode(tem);
       ArrayList alist=new ArrayList();
       alist=this.splits(tem,",");
   	 // System.out.println("ss=="+alist.toString());
       
     StringBuffer strtitle=new StringBuffer();
     try
     {
        for(int i=0;i<expression.length();i++)
        {
          char v =expression.charAt(i);
          if(((i+1)!=expression.length())&&(v>='0'&&v<='9'))
          {
            strlastno=strlastno+v;
          }
          else
          {
      	  
	        if(v>='0'&&v<='9')
	        {
	            strlastno=strlastno+v;
	          
	        }
            if(!"".equals(strlastno))
            {
                ncurr=Integer.parseInt(strlastno);
                if(ncurr>nmax)
                {
                    throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistexpr"),"",""));
                }
    	          Factor fc=(Factor)list.get(ncurr-1);
    	          if(fc.getHzvalue()==null|| "".equals(fc.getHzvalue()))
    	          {
    	        	if((alist.get(ncurr-1)==null|| "".equals(alist.get(ncurr-1))))
    	        	{
    	        		strtitle.append(fc.getHz());
        	        	strtitle.append(ResourceFactory.getProperty("label.null"));
        	        	
    	        	 }else
    	        	 {
    	        	     strtitle.append(alist.get(ncurr-1).toString());
    	             }
    	          }else
    	          {
    	        	strtitle.append(fc.getHzvalue());
    	          }
            }
            if(v=='('||v==')')
            {
            	strtitle.append(v);
            }
	        if(v=='*')
	        {
		    	  strtitle.append(ResourceFactory.getProperty("general.mess.and"));            	
	        }
	        else if(v=='+')
	        {
		    	  strtitle.append(ResourceFactory.getProperty("general.mess.or"));	            	
	        }	        
        
	  
	        strlastno="";
          }
        }//for loop end.       
     }
     catch(Exception ex)
     {
    	 ex.printStackTrace();
		 throw GeneralExceptionHandler.Handle(ex);    	 
     }
     return strtitle.toString();
    }	

	public void execute() throws GeneralException {
	  try
	  {
//		  HashMap hm=(HashMap)this.getFormHM();
//		  ArrayList glist=(ArrayList)hm.get("glist");

		String text=(String)this.getFormHM().get("texts");
		ArrayList flist=(ArrayList)this.getFormHM().get("factorlist");
		String legendtitle=getLegendTitle(text,flist);
        this.getFormHM().put("names",legendtitle);
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
		  throw GeneralExceptionHandler.Handle(ex);
	  }
      
	}
}
