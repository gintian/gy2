package com.hjsj.hrms.transaction.stat;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class EditStatItemTrans extends IBusiness{
	
	  private HashMap factorMap=new HashMap();
	  private int num=0;
	  public void execute() throws GeneralException 
	  {
		  String editid=(String)this.getFormHM().get("editid");
		  String statid=(String)this.getFormHM().get("statid");
		  String opflag=(String)this.getFormHM().get("opflag");
		  if(opflag==null||opflag.length()<=0)
			  opflag="new";
		  if(statid==null||statid.length()<=0)
			  throw GeneralExceptionHandler.Handle(new GeneralException("该统计项不存在！"));
		  if("edit".equals(opflag)&&(editid==null||editid.length()<=0))
			  throw GeneralExceptionHandler.Handle(new GeneralException("该统计条件不存在！"));
		
  		  
  		  ArrayList lists=new ArrayList();
  		  StringBuffer strexpr=new StringBuffer();  
  		  String title="";
  		  String flist="";
  		  String htory="";
  		  ContentDAO dao=new ContentDAO(this.getFrameconn());
  		  StringBuffer sql=new StringBuffer();
  		  sql.append("select name,flag,Infokind from sname where id='"+statid+"'");
  		  String stat_name="";
  		  try {
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
			{
				flist=this.frowset.getString("Infokind");
				stat_name=this.frowset.getString("name");
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String texts="";
		ArrayList list =new ArrayList();
		if("edit".equals(opflag))
		{
			  sql.setLength(0);
	  		  sql.append("select nOrder,legend,lexpr,factor,flag from SLegend where id='"+statid+"' and norder ='"+editid+"' order by nOrder");
	  		  int j=0;  		  
	  		  try {
				this.frowset=dao.search(sql.toString());
				Factor factor=null;			
				if(this.frowset.next())
				{
					String factorstr=this.frowset.getString("factor");
					String lexpr=this.frowset.getString("lexpr");
					title=this.frowset.getString("legend");	
					htory=this.frowset.getString("flag");	
					Factor[] factors=getFactorList(factorstr,lexpr);
					if(factors!=null)
					{
						for(int i=0;i<factors.length;i++)
						{
							factor=factors[i];
							if(factor==null)
								continue;
							list.add(factor);
							j++;
							strexpr.append(j);
			                strexpr.append("*");
						}					
					}				
					texts=lexpr;
					//item=DataDictionary.getFieldItem(fieldname.toUpperCase());
				}
				if(strexpr.length()>0)
		            	strexpr.setLength(strexpr.length()-1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else
		{
			editid="";
		}
  		this.getFormHM().put("opflag", opflag);  
		this.getFormHM().put("history",htory);
		this.getFormHM().put("texts", texts);
		this.getFormHM().put("flist", flist);
		this.getFormHM().put("factorlist",list);
        this.getFormHM().put("mes","0");
        this.getFormHM().put("infor_Flag",flist);
        this.getFormHM().put("title", title);
        this.getFormHM().put("editid", editid);              
        this.getFormHM().put("statid",statid);
        this.getFormHM().put("stat_name", stat_name);
        
	  }
	  /**
	   * 返回Factor
	   * @param factorStr
	   * @return
	   */
      private Factor[] getFactorList(String factorStr,String lexpr)
      {
    	  int nInform=1;
    	  if(factorStr==null||factorStr.length()<=0)
              return null;
    	  String[] lexprFactors=factorStr.split("`");
    	  Factor[] factors=new Factor[lexprFactors.length];
    	  FieldItem item=null;
    	  Factor factor=null;
    	  for(int i=0;i<lexprFactors.length;i++)
    	  {
    		  
    		 String lexprFactor=lexprFactors[i];
    		 String isExist=(String) factorMap.get(lexprFactor);
    		 if(isExist==null||isExist.length()<=0)
    			 factorMap.put(lexprFactor,(num++)+"");
    		 else
    			 continue;
    		 String oper=queryOper(lexprFactor);    		 
    		 int operindex=lexprFactor.indexOf(oper);
    		 String itemid=lexprFactor.substring(0,operindex);
    		 String value=lexprFactor.substring(operindex+oper.length());    	
    		 if(value==null||value.length()<=0|| "Null".equalsIgnoreCase(value))
    			 value="";
    		 item=DataDictionary.getFieldItem(itemid.toUpperCase());
    		 if(item==null)
    		 {
    			 
    			 continue;
    		 }	 
    		 factor=new Factor(nInform);
             factor.setCodeid(item.getCodesetid());
             if(!"0".equals(item.getCodesetid())&&value!=null&&!"".equals(value))
             {
            	 String codedesc=AdminCode.getCodeName(item.getCodesetid(),value); 
            	 if(codedesc!=null&&codedesc.length()>0)//修改bug 0027474
            	   factor.setHzvalue(codedesc);
            	 else if("".equals(codedesc) && "b0110".equals(itemid)){  //add by wangchaoqun on 2014-10-17 由于统计条件里单位选到部门，返回相应的部门编码值
            		 codedesc=AdminCode.getCodeName("UM",value); 
            		 if(codedesc!=null && codedesc.length()>0){
            			 factor.setHzvalue(codedesc);
            		 }else{
            			 factor.setHzvalue(value); 
            		 }
            	 }else{
            		 factor.setHzvalue(value); 
            	 }
             }else
             {
            	 factor.setHzvalue("");
             }
             factor.setValue(value);
             factor.setFieldname(item.getItemid());
             factor.setHz(item.getItemdesc());
             factor.setFieldtype(item.getItemtype());
             factor.setItemlen(item.getItemlength());
             factor.setItemdecimal(item.getDecimalwidth());
             factor.setOper(oper);
             factor.setLog("*");
             factors[i]=factor;
    	  }
    	  
    	  return factors;
      }
      private String getExprs(String expr,String factorstr) throws GeneralException
      {
  	    int n=0;
  	    if(factorstr==null||factorstr.length()<=0)
          return null;
	    String[] lexprFactors=factorstr.split("`");
        String stre="";
        String tem="";
        String station="";
        for(int i=0;i<expr.length();i++)
  	    {
  	          char ch =expr.charAt(i);
  	          if(((i+1)!=expr.length())&&(ch>='0'&&ch<='9'))
  	          {
  	        	 stre=stre+ch;
  	        	    	         	 
  	          }
  	          else
  	          {
  	      	  
  		        if(ch>='0'&&ch<='9')
  		        {
  		        	stre=stre+ch;
  		        			        	
  		        }
        
  		        if(ch=='*'||ch=='+'||ch=='('||ch==')')
  		        {
  		        	int istation=Integer.parseInt(stre);
  		        	
  		        	if(istation<=lexprFactors.length)
  		        	{
  		        		String faction=lexprFactors[istation-1];
  	  		        	station=(String)this.factorMap.get(faction);
  	  		        	tem=tem+station;
  		        	}
  		        	stre="";
  		        	tem=tem+ch;
  		        }
  	          }
  	      }
          int istation=Integer.parseInt(stre);      	
      	  if(istation<=lexprFactors.length)
      	  {
      		  String faction=lexprFactors[istation];
	          station=(String)this.factorMap.get(faction);
	          tem=tem+station;
      	  }
          return tem;
      }
      private String queryOper(String lexprFactor)
      {
    	  String oper="";
    	  if(lexprFactor.indexOf("<=")!=-1)
    	  {
    		  oper="<=";
    	  }else if(lexprFactor.indexOf(">=")!=-1)
    	  {
    		  oper=">=";
    	  }else if(lexprFactor.indexOf("<>")!=-1)
    	  {
    		  oper="<>";
    	  }else if(lexprFactor.indexOf("=")!=-1)
    	  {
    		  oper="=";
    	  }else if(lexprFactor.indexOf(">")!=-1)
    	  {
    		  oper=">";
    	  }else if(lexprFactor.indexOf("<")!=-1)
    	  {
    		  oper="<";
    	  }
    	  return   oper;
      }
}
