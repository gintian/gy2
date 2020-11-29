package com.hjsj.hrms.transaction.kq.machine.analyse;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 删除分析的数据
 *<p>Title:DeleteDataAnalyseTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Oct 30, 2007</p> 
 *@author sunxin
 *@version 4.0
 */
public class DeleteDataAnalyseTrans extends IBusiness 
{
    public void execute() throws GeneralException 
	{
    	ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");
    	
    	if(selectedinfolist==null||selectedinfolist.size()==0)
            return;
    	String analyse_type=(String)this.getFormHM().get("analyse_type");
    	if(analyse_type==null||analyse_type.length()<=0)
    		return;
    	if("result".equals(analyse_type))
    	{
    		String temp_Table=(String)this.getFormHM().get("analyseTempTab");
        	if(temp_Table==null||temp_Table.length()<=0)
        	{
        		throw GeneralExceptionHandler.Handle(new GeneralException("","没有找到原始数据分析临时表，请重新分析数据！","",""));
        	}
        	deleteAnalyeResultTran(selectedinfolist,temp_Table);
    	}else if("except".equals(analyse_type))
    	{
    		String except_Tab=(String)this.getFormHM().get("exceptCardTab");
        	if(except_Tab==null||except_Tab.length()<=0)
        	{
        		throw GeneralExceptionHandler.Handle(new GeneralException("","没有找到原始数据分析异常表，请重新分析数据！","",""));
        	}
    		deleteAnalyeExcept(selectedinfolist,except_Tab);
    	}
    	
	}
    /**
     * 删除数据处理
     * @param selectedinfolist
     * @param temp_Table
     * @throws GeneralException
     */
    private void deleteAnalyeResultTran(ArrayList selectedinfolist,String temp_Table)throws GeneralException
    {
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
    }
    /**
     * 删除异常表数据，同时把原始数据表数据也给删除了
     * @param selectedinfolist
     * @param except_Tab
     * @throws GeneralException
     */
    private void deleteAnalyeExcept(ArrayList selectedinfolist,String except_Tab)throws GeneralException
    {
    	ArrayList list=new ArrayList();    	
    	String kq_date_Tab="kq_originality_data";
    	for(int i=0;i<selectedinfolist.size();i++)
        {
    		ArrayList one_list=new ArrayList();
    		LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i); 
    		String nbase=rec.get("nbase").toString();
    		String a0100=rec.get("a0100").toString();    	
    		String work_date=rec.get("work_date").toString();
    		String work_time=rec.get("work_time").toString();
    		one_list.add(nbase);
    		one_list.add(a0100);    		
    		one_list.add(work_date);
    		one_list.add(work_time);
    		list.add(one_list);
        }
    	String exceptSQL="delete from "+except_Tab+" where nbase=? and a0100=? and work_date=? and work_time=?";
    	String sql="delete from "+kq_date_Tab+" where  nbase=? and a0100=? and work_date=? and work_time=?";
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	try
    	{
    		dao.batchUpdate(exceptSQL,list);
    		dao.batchUpdate(sql,list);
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(new GeneralException("","删除失败！","",""));
    	}
    }
}
