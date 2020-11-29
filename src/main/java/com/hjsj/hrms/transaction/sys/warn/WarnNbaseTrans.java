package com.hjsj.hrms.transaction.sys.warn;

import com.hjsj.hrms.interfaces.sys.warn.IConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;

public class WarnNbaseTrans extends IBusiness implements IConstant{

    public void execute() throws GeneralException {
    	
    	
        ArrayList prlist=new ArrayList();
        String nbases=(String)this.getFormHM().get(Key_HrpWarn_Nbase);
        nbases=nbases!=null&&nbases.length()>0?nbases:"";
        ArrayList dblist=getKqNbaseList(this.userView.getPrivDbList(),nbases);
        this.getFormHM().put("dblist", dblist);
    }
    public ArrayList getKqNbaseList(ArrayList list,String nbases)
	{
	     ArrayList kq_list=new ArrayList();
       if(list==null||list.size()<=0)
       	return kq_list;
       StringBuffer buf=new StringBuffer();
       buf.append("(");
       for(int i=0;i<list.size();i++)
       {
       	buf.append(" Upper(pre)='"+list.get(i).toString().toUpperCase()+"'");
       	if(i!=list.size()-1)
       		buf.append(" or ");
       }
       buf.append(")");
       StringBuffer sql=new StringBuffer();
       sql.append("select dbname,pre from dbname where 1=1 and ");
       if(buf!=null&&buf.toString().length()>0)
           sql.append(buf.toString());
       sql.append(" order by dbid");
       ContentDAO dao=new ContentDAO(this.getFrameconn());
       ArrayList prlist=new ArrayList();
       try
       {
      	 RowSet rs=dao.search(sql.toString());
      	 CommonData da=new CommonData();   
      	 while(rs.next())
      	 {
      		 da=new CommonData();
      		 String dbpre=rs.getString("pre").toLowerCase();
			 if((nbases.toLowerCase()).indexOf(dbpre)!=-1)
			    	prlist.add("1");
	         else
	            	prlist.add("0");
      		 da.setDataName(rs.getString("dbname"));
      		 da.setDataValue(rs.getString("pre")+"`"+rs.getString("dbname"));
      		 kq_list.add(da);
      	 }
       }catch(Exception e)
       {
      	 e.printStackTrace();
       }
       this.getFormHM().put("perlist", prlist);
       return kq_list;
   }
}
