package com.hjsj.hrms.transaction.kq.options.manager.kqcard;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 得到一个发卡人员
 * <p>Title:WorkSendCardTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jan 6, 2007 1:59:19 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class WorkSendCardTrans extends IBusiness{
	
    public void execute() throws GeneralException
    {
    	String nbase=(String)this.getFormHM().get("nbase");
    	String a_code=(String)this.getFormHM().get("a_code");
    	String kq_gno=(String)this.getFormHM().get("kq_gno");
    	ArrayList r_code=(ArrayList)this.getFormHM().get("r_code");
    	
    	if(a_code==null||a_code.length()<=0)
    	{
    		return;
    	}
    	if(nbase==null||nbase.length()<=0)
    	{
    		return;
    	}
    	String a0100=a_code.substring(2);
    	ArrayList selected_emp=new ArrayList();
    	if(r_code!=null&&r_code.size()>0)
    	{
    		for(int i=0;i<r_code.size();i++)
    		{
    			if(r_code.get(i).toString().equals(nbase+"`"+a0100))
    			{
    				this.getFormHM().put("selected_emp",selected_emp);
    				return;
    			}
    		}
    	}
    	String sql_gon="";
    	if(kq_gno==null||kq_gno.length()<=0)
    	{
    		kq_gno="";
    	}else
    	{
    		sql_gon=","+Sql_switcher.isnull(kq_gno,"''")+" as "+kq_gno;
    	}
    	
    	
    	String sql="select a0101 "+sql_gon+" from "+nbase+"A01 where a0100='"+a0100+"'";
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	ArrayList g_list=new ArrayList();
    	
    	try
    	{
    		this.frowset=dao.search(sql);
    		if(this.frowset.next())
        	{
    			CommonData vo = new CommonData();
    			vo.setDataName(this.frowset.getString("a0101"));
    			vo.setDataValue(nbase+"`"+a0100);
    			selected_emp.add(vo);
    			String g_no="";
    			if(kq_gno!=null&&kq_gno.length()>0)
    	    	{
    				g_no=this.frowset.getString(kq_gno)!=null?this.frowset.getString(kq_gno):"";
    	    	}
    			g_list.add(g_no);
        	}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
        this.getFormHM().put("selected_emp",selected_emp);
        this.getFormHM().put("gno_list",g_list);
    }

}
