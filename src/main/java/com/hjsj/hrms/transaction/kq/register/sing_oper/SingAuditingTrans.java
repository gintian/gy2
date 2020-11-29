package com.hjsj.hrms.transaction.kq.register.sing_oper;

import com.hjsj.hrms.businessobject.kq.register.sing.SingOpinVo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class SingAuditingTrans  extends IBusiness {
	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	private String  error_return=" history.back();";
	public void execute() throws GeneralException 
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String flag=(String)hm.get("flag");
		String validate="false";
		String q03z5="01";
		if(flag==null||flag.length()<=0)
		{
			this.getFormHM().put("validate",validate);
			return;
		}else if("8".equals(flag))
		{
			q03z5="08";
		}else if("2".equals(flag))
		{
			q03z5="02";
		}else
		{
			this.getFormHM().put("validate",validate);
			return;
		}
		ArrayList opinlist=(ArrayList)this.getFormHM().get("opinlist");	
		String kq_duration=(String)this.getFormHM().get("kq_duration");
		if(opinlist==null||opinlist.size()==0)
            return;		
		ArrayList datelist=(ArrayList)this.getFormHM().get("datelist");
		CommonData vo_date=(CommonData)datelist.get(0);
    	String start_date=vo_date.getDataValue();    	
    	vo_date=(CommonData)datelist.get(datelist.size()-1);	    	 
   	    String end_date=vo_date.getDataValue();
     	for(int i=0;i<opinlist.size();i++)
       {
			
			SingOpinVo rec=(SingOpinVo)opinlist.get(i);   
    	    String nbase=rec.getNbase();
       	    String a0100=rec.getA0100();   	    
    	    updateQ03Sql(a0100,nbase,start_date,end_date,q03z5);
    	    updateQ05(a0100,nbase,kq_duration,q03z5);
       }
       validate="true"; 	   
       this.getFormHM().put("validate",validate);      
       this.getFormHM().put("error_flag","0");
	}
	 /****************
     * @param whereIN select in子句 
     * @param tablename 表名
     * @return 返回？号的update的SQL语句
     * 
     * ***/
    public void updateQ03Sql(String a0100,String dbase,String start_date,String end_date,String q03z5)throws GeneralException
    {
    	
    	StringBuffer updatesql=new StringBuffer();
    	updatesql.append("update Q03 set ");
    	updatesql.append(" q03z5=? where ");
    	updatesql.append(" nbase=? ");    	
    	updatesql.append(" and Q03Z0 >=? ");
    	updatesql.append(" and Q03Z0 <=? ");
    	updatesql.append(" and a0100 =?");
    	updatesql.append(" and q03z5 in ('01','07')");
    	ArrayList u_list=new ArrayList();
  	    u_list.add(q03z5);
  	    u_list.add(dbase);  	   
  	    u_list.add(start_date);
  	    u_list.add(end_date);
  	    u_list.add(a0100);
	    ArrayList list= new ArrayList();
	    list.add(u_list);
	    ContentDAO dao = new ContentDAO(this.getFrameconn());
	    try{
	         dao.batchUpdate(updatesql.toString(),list);	         
	        
	    }catch(Exception e){
	    	   e.printStackTrace();
	    	    String error_message=ResourceFactory.getProperty("kq.register.refer.lost");	
		 		this.getFormHM().put("error_message",error_message);
		 	    this.getFormHM().put("error_return",this.error_return);  
		 	    this.getFormHM().put("error_flag","1");
		 	    return;	
	    	   //throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.refer.lost"),"",""));
	    }
	    
    }
    /****************
     * @param whereIN select in子句 
     * @param tablename 表名
     * @return 返回？号的update的SQL语句
     * 
     * ***/
    public void updateQ05(String a0100,String dbase,String kq_duration,String q03z5)throws GeneralException
    {
    	
    	StringBuffer updatesql=new StringBuffer();
    	updatesql.append("update Q05 set ");
    	updatesql.append(" q03z5=? where ");
    	updatesql.append(" nbase=? ");    	
    	updatesql.append(" and Q03Z0 =? ");    
    	updatesql.append(" and a0100=?");
    	updatesql.append(" and q03z5 in ('01','07')");
    	ArrayList u_list=new ArrayList();
  	    u_list.add(q03z5);
  	    u_list.add(dbase);  	   
  	    u_list.add(kq_duration);
  	    u_list.add(a0100);
	    ArrayList list= new ArrayList();
	    list.add(u_list);
	    ContentDAO dao = new ContentDAO(this.getFrameconn());
	    try{
	         dao.batchUpdate(updatesql.toString(),list);	         
	        
	    }catch(Exception e){
	    	   e.printStackTrace();
	    	   String error_message=ResourceFactory.getProperty("kq.register.refer.lost");	
		 		this.getFormHM().put("error_message",error_message);
		 	    this.getFormHM().put("error_return",this.error_return);  
		 	    this.getFormHM().put("error_flag","1");
		 	    return;	
	    	   //throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.refer.lost"),"",""));
	    }
    }
  
}
