package com.hjsj.hrms.transaction.kq.options.manager.kqcard;

import com.hjsj.hrms.businessobject.kq.options.kqcrad.OrderCardEmp;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 批量发卡排序
 * <p>Title:OrderSendEmpTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jan 6, 2007 5:10:51 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class OrderSendEmpTrans extends IBusiness{
	
    public void execute() throws GeneralException
    {
       String order_status=(String)this.getFormHM().get("order_status");
       ArrayList r_code=(ArrayList)this.getFormHM().get("r_code");
       ArrayList r_name=(ArrayList)this.getFormHM().get("r_name");
       ArrayList r_gno=(ArrayList)this.getFormHM().get("r_gno");
       if(order_status==null||order_status.length()<=0)
       {
    	    return;
       }
       if(r_code==null||r_code.size()<=0)
       {
    	    this.getFormHM().put("selected_emp","");
         	return;
       }  
       if(r_gno==null||r_gno.size()<=0)
       {
       		return;
       }
       if(r_gno.size()!=r_code.size())
       {
    	   	return;
       }
       HashMap hash=new HashMap();
       ArrayList list =new ArrayList();
       ArrayList gno_list =new ArrayList();
       if("1".equals(order_status))
       {
    	   /**人员编号**/
    	   hash=orderA0100(r_code,r_name,r_gno);    	   
       }else if("2".equals(order_status))
       {
    	   /**人员姓名**/
    	   hash=orderA0101(r_code,r_name,r_gno);
       }else if("3".equals(order_status))
       {
    	   /**工号**/
    	   hash=order_kq_gno(r_gno,r_code,r_name);
    	  
       }
       list=(ArrayList)hash.get("emp_list");
       gno_list=(ArrayList)hash.get("gno_list");         
       this.getFormHM().put("selected_emp",list);
       this.getFormHM().put("gno_list",gno_list);
    }
   /**
    * 人员编号排序
    * @param r_code
    * @param r_name
    * @return
    */
    public HashMap orderA0100(ArrayList r_code,ArrayList r_name,ArrayList r_gno)
    {
    	HashMap hash=new HashMap();
    	ArrayList list=new ArrayList();
    	ArrayList gno_list=new ArrayList();
        if(r_code==null||r_code.size()<=0)
        {
        	return hash;
        }
        ArrayList str_list=new ArrayList();
        for(int i=0;i<r_code.size();i++)
    	{
    		ArrayList one_card=new ArrayList();
    		String o_code=r_code.get(i).toString();    		   		
    		if(o_code==null||o_code.length()<=0)
    		{
    			continue;
    		}
    		String[] o_codes=o_code.split("`"); 
    		str_list.add(o_codes[1]);
    	}
        String [] str=new String[str_list.size()];
        for(int i=0;i<str_list.size();i++)
        {
        	str[i]=str_list.get(i).toString();
        }
        OrderCardEmp orderCardEmp=new OrderCardEmp();
        String[] new_str=orderCardEmp.orderByStr(str);
        CommonData vo =null;
        for(int i=0;i<new_str.length;i++)
        {
        	String o_a0100=new_str[i];
        	for(int r=0;r<r_code.size();r++)
        	{
        		String o_code=r_code.get(r).toString();
        		String o_name=r_name.get(r).toString();
        		if(o_code==null||o_code.length()<=0)
        			continue;
        		if(o_code.indexOf(o_a0100)!=-1)
        		{
        			vo=new CommonData();
        			vo.setDataName(o_name);
        			vo.setDataValue(o_code);
        			gno_list.add(r_gno.get(r));
        			r_code.set(r,"#");
        			list.add(vo);        			
        		}
        	}
        }
        hash.put("emp_list",list);
        hash.put("gno_list",gno_list);
    	return hash;
    }
    /**
     * 人员姓名排序
     * @param r_code
     * @param r_name
     * @return
     */
     public HashMap orderA0101(ArrayList r_code,ArrayList r_name,ArrayList r_gno)
     {
     	ArrayList list=new ArrayList();
     	HashMap hash=new HashMap();
     	ArrayList gno_list=new ArrayList();
         if(r_code==null||r_code.size()<=0)
         {
        	 return hash;
         }         
        
         String [] str=new String[r_name.size()];
         for(int i=0;i<r_name.size();i++)
         {
         	str[i]=r_name.get(i).toString();
         }
         OrderCardEmp orderCardEmp=new OrderCardEmp();
         String[] new_str=orderCardEmp.orderByStr(str);
         CommonData vo =null;
         for(int i=0;i<new_str.length;i++)
         {
         	String o_a0101=new_str[i];         	
         	for(int r=0;r<r_code.size();r++)
         	{
         		String o_code=r_code.get(r).toString();
         		String o_name=r_name.get(r).toString();         		
         		if(o_code==null||o_code.length()<=0)
         			continue;
         		if(o_name.equals(o_a0101))
         		{
         			vo=new CommonData();
         			vo.setDataName(o_name);
         			vo.setDataValue(o_code);
         			gno_list.add(r_gno.get(r));
         			r_name.set(r,"#");
         			list.add(vo);        			
         		}
         	}
         }
         hash.put("emp_list",list);
         hash.put("gno_list",gno_list);         
         return hash;
     }
     /**
      * 工号排序
      * @param r_code
      * @param r_name
      * @return
      */
      public HashMap order_kq_gno(ArrayList r_gno,ArrayList r_code,ArrayList r_name)
      {
        HashMap hash=new HashMap();
       	ArrayList gno_list=new ArrayList();
      	ArrayList list=new ArrayList();
        if(r_code==null||r_code.size()<=0)
        {
          	return hash;
        }  
        if(r_gno==null||r_gno.size()<=0)
        {
        	return hash;
        }
        String [] str=new String[r_gno.size()];
        for(int i=0;i<r_gno.size();i++)
        {
        	str[i]=r_gno.get(i).toString();
        } 
        OrderCardEmp orderCardEmp=new OrderCardEmp();
        String[] new_str=orderCardEmp.orderByStr(str);
        CommonData vo =null;
        for(int i=0;i<new_str.length;i++)
        {
        	String o_gno=new_str[i];
        	gno_list.add(o_gno);
        	for(int r=0;r<r_gno.size();r++)
        	{
        		String o_gnum=r_gno.get(r).toString();        		
        		if(o_gno.equals(o_gnum))
        		{
        			vo=new CommonData();
        			String o_code=r_code.get(r).toString();
            		String o_name=r_name.get(r).toString();
            		if(o_code==null||o_code.length()<=0)
            		{
            			continue;
            		}
        			vo.setDataName(o_name);
        			vo.setDataValue(o_code); 
        			r_gno.set(r,"#$");
        			list.add(vo);        			
        		}
        	}
        }
        hash.put("emp_list",list);
        hash.put("gno_list",gno_list);
      	return hash;
      }
}
