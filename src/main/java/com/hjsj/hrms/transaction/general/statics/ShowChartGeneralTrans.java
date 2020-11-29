package com.hjsj.hrms.transaction.general.statics;

import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.businessobject.sys.AnychartBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class ShowChartGeneralTrans extends IBusiness {
	

	private String getStrss(String expre,List list)throws GeneralException
    {
	  String find=(String)this.getFormHM().get("find");
       String strl="";
       int ncurr=0;
       StringBuffer str=new StringBuffer();
       expre=PubFunc.keyWord_reback(expre);
       try
       {
          for(int i=0;i<expre.length();i++)
          {
             char v =expre.charAt(i);
             if(((i+1)!=expre.length())&&(v>='0'&&v<='9'))
             {
        	    strl=strl+v;
              }
              else
              {
	            if(v>='0'&&v<='9')
	            {
	        	   strl=strl+v;
	             }
                if(!"".equals(strl))
                {
                   ncurr=Integer.parseInt(strl);
    	            Factor fc=(Factor)list.get(ncurr-1);
    	            str.append(fc.getFieldname().toUpperCase());
    	            fc.setOper(com.hjsj.hrms.utils.PubFunc.keyWord_reback(fc.getOper()));
    	            str.append(fc.getOper());
	    	        if("1".equals(find)&&fc.getValue().length()>0&& "A".equalsIgnoreCase(fc.getFieldtype())&&(fc.getCodeid()==null || "0".equalsIgnoreCase(fc.getCodeid())))
	    	        {
	    	            	str.append("%");
	    	        }
    	            str.append(fc.getValue());
    	            if("1".equals(find)&&fc.getValue().length()>0&& "A".equalsIgnoreCase(fc.getFieldtype()))
    	            {
    	            	str.append("%");
    	            }
    	            str.append("`");
                }	        
	        strl="";
          }
        }      
     }
     catch(Exception ex)
     {
    	 ex.printStackTrace();
		 throw GeneralExceptionHandler.Handle(ex);    	 
     }
     return str.toString();
    }
	private String getExpr(String expr)
    {
		int n=0;
        String stre="";
        String tem="";
        int inge=0;
        expr=PubFunc.keyWord_reback(expr);
       for(int i=0;i<expr.length();i++)
	        {
	          char ch =expr.charAt(i);  
	          if(((i+1)!=expr.length())&&(ch>='0'&&ch<='9'))
	          {
	        	 stre=stre+ch;
	        	 inge=(int)(stre.length());
	        	 if(!(inge>1))
	        	 {
	        	   n++;
	               tem=tem+String.valueOf(n);
	        	 }
	          }
	          else
	          {
	      	  
		        if(ch>='0'&&ch<='9')
		        {
		        	stre=stre+ch;
		        	inge=(int)(stre.length());
		        	 if(!(inge>1))
		        	 {
		        	   n++;
		               tem=tem+String.valueOf(n);
		            
		             }
		        }
      
		        if(ch=='*'||ch=='+'||ch=='('||ch==')')
		        {
		        	stre="";
		        	tem=tem+ch;
		        }
		        
	          }
	        }    
        return tem;
    }

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
	try
	{
	     String htory=(String)this.getFormHM().get("history");
		 String userbase=(String)this.getFormHM().get("userbase");	
		 String userbases=(String)this.getFormHM().get("userbases");
		 String[] sel=(String[])this.getFormHM().get("selects");
		 String selx=(String)this.getFormHM().get("mess");
		 String result=(String)this.getFormHM().get("result");
		 ArrayList flist=(ArrayList)this.getFormHM().get("factorlist");
		 String querycond=(String)this.getFormHM().get("querycond");
		 String find=(String)this.getFormHM().get("find");
		 rebackKeyword(flist);
		 boolean blike=false;
		 //liuy 2015-4-15 8763：花名册应该只能看见19个人，可是简单查询，杨模糊查询，能查出100多条（模糊授权未按高级授权走） begin
		 //if("1".equals(find))
			 //blike=true;
		 //liuy 2015-4-15 end
		 if(selx==null|| "".equals(selx)||sel==null|| "".equals(sel))
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("general.mess.nexist"),"",""));
		 boolean hot=true;
	     if(htory==null|| "".equals(htory)||htory.length()<=0)
	     {
	    	  hot =Boolean.getBoolean(htory);
	     }else if("0".equals(htory))
	     {
	    	 hot=false;
	     }
	     boolean ret=true;
	     if(result==null|| "".equals(result)|| "0".equals(result))
	    	ret=true; 
	     else
	    	ret =false;
	     if(userbase==null|| "".equals(userbase))
			 userbase="Usr";
 	 	 String infor_Flag=(String)this.getFormHM().get("infor_Flag");
		 if(infor_Flag==null|| "".equalsIgnoreCase(infor_Flag))
			 infor_Flag="1";
		 if("2".equals(infor_Flag))
				userbase="B";
		 if("3".equals(infor_Flag))
				userbase="K";
		 int[] statvalues;
       	 String[] fieldDisplay;        
		 String SNameDisplay;
		 ArrayList rlist = new ArrayList();
		 ArrayList alist=new ArrayList();
		 ArrayList list=new ArrayList();	
		 StatDataEncapsulation simplestat=new StatDataEncapsulation();
	     StringTokenizer st=new StringTokenizer(selx,"," );
	     while(st.hasMoreTokens())
	          alist.add(st.nextToken(","));
		 for(int i=0; i<sel.length;i++)
		 {
			   rlist.clear();
			   //Factor fc=(Factor)flist.get(i);
//  		   String opt=fc.getOper();
  		       //opt=getOperator(opt);
			   rlist.add(sel[i]+"|"+this.getExpr(alist.get(i).toString())+"|"+this.getStrss(alist.get(i).toString(),flist));
	           if(userbases==null||userbases.length()==0){
	        	   statvalues=simplestat.getLexprData(rlist,hot,userView,userbase,infor_Flag,ret,querycond,blike);
	           }else
	        	   statvalues=simplestat.getLexprData(rlist,hot,userView,userbase.toUpperCase(),infor_Flag,ret,querycond,blike,userbases);
		       if (statvalues != null && statvalues.length > 0) 
		       {
			      fieldDisplay = simplestat.getDisplay();
			      int statTotal = 0;
			      for (int j = 0; j < statvalues.length; j++)
			      {
				     CommonData vo=new CommonData();
				     vo.setDataName(fieldDisplay[j]);
				     vo.setDataValue(String.valueOf(statvalues[j]));
				     list.add(vo);
			         statTotal += statvalues[j];
			     }
		      }
		 }
		 SNameDisplay = simplestat.getSNameDisplay();
		 this.getFormHM().put("snamedisplay",SNameDisplay);
		 this.getFormHM().put("list",list);
		 HashMap jfreemap=new HashMap();
		 jfreemap.put(SNameDisplay, list);
		 String xangle=AnychartBo.computeXangle(list);
		 this.getFormHM().put("xangle", xangle);
		 this.getFormHM().put("jfreemap" ,jfreemap);
	}
	catch(Exception ex)
	{
		ex.printStackTrace();
		throw GeneralExceptionHandler.Handle(ex);
	}

  }
	
	/**
	 * 符号转换
	 * @param opt
	 * @return
	 */
	private String getOperator(String opt) {
		if("<>".equals(opt))
		{
			opt=ResourceFactory.getProperty("kq.formula.not");
		}
		if(">".equals(opt))
		{
			opt=ResourceFactory.getProperty("kq.formula.over");
		}
		if(">=".equals(opt))
		{
			opt=ResourceFactory.getProperty("kq.formula.overo");
		}
		if("<".equals(opt))
		{
			opt=ResourceFactory.getProperty("kq.formula.lower");
		}
		if("<=".equals(opt))
		{
			opt=ResourceFactory.getProperty("kq.formula.lowero");
		}
		if("=".equals(opt))
		{
			opt=ResourceFactory.getProperty("kq.formula.equal");
		}
		return opt;
	}	
	
	private void rebackKeyword(ArrayList list){
		for(int i=0;i<list.size();i++){
			Factor factor = (Factor)list.get(i);
			String hz = factor.getHz();
			String oper = factor.getOper();
			String log = factor.getLog();
			String value = factor.getValue();
			String hzvalue = factor.getHzvalue();
			hz = PubFunc.hireKeyWord_filter_reback(hz);
			oper = PubFunc.hireKeyWord_filter_reback(oper);
			log = PubFunc.hireKeyWord_filter_reback(log);
			value = PubFunc.hireKeyWord_filter_reback(value);
			hzvalue = PubFunc.hireKeyWord_filter_reback(hzvalue);
			factor.setHz(hz);
			factor.setOper(oper);
			factor.setLog(log);
			factor.setValue(value);
			factor.setHzvalue(hzvalue);
		}
	}
}
