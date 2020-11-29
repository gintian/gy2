package com.hjsj.hrms.transaction.kq.machine;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 删除原始数据
 * <p>Title:DeleteDataAnalyseTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Feb 9, 2007 2:53:25 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class DeleteDataAnalyseTrans extends IBusiness 
{
    public void execute() throws GeneralException 
	{
    	String strSql=(String)this.getFormHM().get("strSql");
    	String whereStr=(String)this.getFormHM().get("whereStr");
    	String column=(String)this.getFormHM().get("column");
    	ArrayList fieldList=(ArrayList)this.getFormHM().get("fieldList");
    	ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");
    	String temp_Table=(String)this.getFormHM().get("temp_Table");
    	if(temp_Table==null||temp_Table.length()<=0)
    	{
    		throw GeneralExceptionHandler.Handle(new GeneralException("","没有找到原始数据分析临时表，请重新分析数据！","",""));
    	}
    	if(selectedinfolist==null||selectedinfolist.size()==0)
            return;
    	ArrayList list=new ArrayList();
    	for(int i=0;i<selectedinfolist.size();i++)
        {
    		ArrayList one_list=new ArrayList();
    		LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i); 
    		String nbase=rec.get("nbase").toString();
    		String a0100=rec.get("a0100").toString();
    		String q03z0=rec.get("q03z0").toString();
    		one_list.add(nbase);
    		one_list.add(a0100);
    		one_list.add(q03z0);
    		list.add(one_list);
        }
    	String sql="delete from "+temp_Table+" where nbase=? and a0100=? and q03z0=?";
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	try
    	{
    		dao.batchUpdate(sql,list);
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(new GeneralException("","删除失败！","",""));
    	}
		this.getFormHM().put("strSql",strSql);
		this.getFormHM().put("whereStr",whereStr);
		this.getFormHM().put("fieldList",fieldList);
		this.getFormHM().put("column",column);
		this.getFormHM().put("order","order by nbase,b0110,e0122");
	}

}
