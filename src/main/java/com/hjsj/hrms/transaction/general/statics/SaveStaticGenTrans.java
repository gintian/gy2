package com.hjsj.hrms.transaction.general.statics;

import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SaveStaticGenTrans extends IBusiness {
	
	private String getMaxId()throws GeneralException
	{
		int nid=-1;
		StringBuffer sql=new StringBuffer("select max(id)+1 as nmax from sname");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
			{
				nid=this.frowset.getInt("nmax");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
   	       throw GeneralExceptionHandler.Handle(ex);			
		}
		return String.valueOf(nid);
	}
	
	
	
	private void deleteSName(String id)throws GeneralException
	{
		StringBuffer sql=new StringBuffer();

		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			sql.append("delete from sname where id=");
			sql.append(id);			
			dao.update(sql.toString());
			sql.setLength(0);
			sql.append("delete from slegend where id=");
			sql.append(id);	
			dao.update(sql.toString());			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
	   	    throw GeneralExceptionHandler.Handle(ex);					
		}
	}
	
	   private String getStr(String expre,List list)throws GeneralException
	    {
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
	    	         String oper=PubFunc.keyWord_reback(fc.getOper());
	    	         fc.setOper(oper);
	    	         str.append(oper);
	    	         if("".equals(fc.getValue())||fc.getValue()==null)
	    	         {
	    	        	 str.append("Null");
	    	         }else{
	    	        	 str.append(fc.getValue());
	    	         }
	    	         
	            }	        
		        if(v=='*'||v=='+')
		        {
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
	
	private String getExprs(String expr) throws GeneralException
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
		String[] sel=(String[])this.getFormHM().get("selects");
		String selx=(String)this.getFormHM().get("mess");
        String find=(String)this.getFormHM().get("find");
        String htory=(String)this.getFormHM().get("history");
        String title = (String)this.getFormHM().get("title");
        String infor_Flag = (String)this.getFormHM().get("infor_Flag");
        String id = (String)this.getFormHM().get("hvalue");
        ArrayList flist=(ArrayList)this.getFormHM().get("factorlist");
        rebackKeyword(flist);
        if(find==null|| "".equals(find)||find.length()<=0)
        {
            find="0"; //如果没有选择，默认值为0;
        }
        if(htory==null|| "".equals(htory)||htory.length()<=0)
        {
        	htory="0";
        }
        if(selx==null||selx.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("general.mess.nexist"),"",""));
        ArrayList alist=new ArrayList();
        StringTokenizer st=new StringTokenizer(selx,"," );
        while(st.hasMoreTokens())
        {
        	alist.add(st.nextToken(","));
        }
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        if(id==null|| "".equals(id))
        	id=getMaxId();
        else
        {
        	deleteSName(id);        	
        }
		StringBuffer sql=new StringBuffer();
		ArrayList paralist=new ArrayList();		
		sql.append("insert into sname(Id,Name,Flag,Type,InfoKind)values(?,?,?,?,?)");
		paralist.add(new Integer(id));
		paralist.add(title);
		paralist.add(find);
		paralist.add("1");
		paralist.add(new Integer(infor_Flag));
		try
		{
			dao.update(sql.toString(),paralist);
			sql.setLength(0);
			sql.append("insert into slegend(Id,nOrder,Legend,LExpr,Factor,Direction,flag)values(?,?,?,?,?,?,?)");
			//Factor factor=null;
			//for(int i=0; i<flist.size();i++)
			for(int i=0; i<sel.length;i++)
		    {
				/*if(i>=alist.size())
					break;
				//factor=(Factor)flist.get(i);				
*/				paralist.clear();		    	
		    	paralist.add(new Integer(id));
		    	paralist.add(new Integer(i+1));
		    	/*if(factor.getCodeid()!=null&&!factor.getCodeid().equals("0"))
		    	{
		    		String value=AdminCode.getCodeName(factor.getCodeid(),factor.getValue());
		    		paralist.add(PubFunc.splitString(value,20));
		    	}
		    	else
		    	    paralist.add(PubFunc.splitString(factor.getValue(),20));*/
		    	paralist.add(PubFunc.splitString(sel[i],20));//09年3.3修改，保存覆盖后，只有一条记录
		    	paralist.add(this.getExprs(alist.get(i).toString()));
		    	paralist.add(this.getStr((alist.get(i)).toString(),flist));
		    	paralist.add("0");
		    	paralist.add(htory);
		    	dao.update(sql.toString(),paralist);
		    }
		    /**保存资源,chenmengqing*/
			UserObjectBo user_bo=new UserObjectBo(this.getFrameconn());
			user_bo.saveResource(id,this.userView,IResourceConstant.STATICS);
		    
        }
		catch(Exception exx)
		{
      	     exx.printStackTrace();
      	     throw GeneralExceptionHandler.Handle(exx);
		}
		finally{
			
			ArrayList rlist=new ArrayList();
			/*Factor factor=null;
			CommonData cdata=null;
			for(int m=0; m<flist.size();m++)
		    {
				if(m>=alist.size())
					break;
				
			   factor=(Factor)flist.get(m);		
			   if(factor.getCodeid()!=null&&!factor.getCodeid().equals("0"))
		       {
		    		String value=AdminCode.getCodeName(factor.getCodeid(),factor.getValue());
		    		cdata=new CommonData(value,alist.get(m).toString());
		       }else
		       {
		    	   cdata=new CommonData(factor.getValue(),alist.get(m).toString());
		       }			   
			   rlist.add(cdata);
			}*/
			for(int m=0;m<sel.length;m++)
			{
			   CommonData cdata=new CommonData(sel[m],alist.get(m).toString());
			   rlist.add(cdata);
			}
			this.getFormHM().put("mes","3");
		    this.getFormHM().put("rlist",rlist);
			 //System.out.print("dd=="+rlist.toString());
		}
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
