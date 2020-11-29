package com.hjsj.hrms.transaction.kq.options.kq_class;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class DeleteKqClassTrans extends IBusiness implements KqClassConstant{

	public void execute() throws GeneralException
	{
	  //HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");   
	  //String class_id = (String)hm.get("class_id");
	  //String class_flag = (String)hm.get("class_flag");
	  String class_id = (String)this.getFormHM().get("class_id");
	  String class_flag = (String)this.getFormHM().get("class_flag");
	  if(class_flag == null||class_flag.length()<= 0)
		    return;
	  if("del".equals(class_flag))
	  {
		  deleteKaClass( class_id);
		  this.getFormHM().put("class_id","");
	  }
	}
	/**
     * 删除
     * @param class_id
     * @throws GeneralException
     */
    public void deleteKaClass(String class_id)throws GeneralException
    {
    	if(class_id == null||class_id.length()<= 0)
    		return;
    	ArrayList list  = new ArrayList();
    	String sql = "delete from "+KqClassConstant.kq_class_table+" where "+KqClassConstant.kq_class_id+" = ?";
    	ContentDAO dao = new ContentDAO(this.getFrameconn());
    	try
    	{
    		if (this.ClassIsUsed(class_id, dao)) 
			{
    			list.add(class_id);
    			dao.delete(sql,list);
    			this.getFormHM().put("err_message",null);
			}else 
			{
				this.getFormHM().put("err_message", ResourceFactory.getProperty("kq.class.delete.error"));
			}
    		
    	}catch(Exception e)
    	{
    	   e.printStackTrace();
    	   throw GeneralExceptionHandler.Handle(e);
    	}
    }
    /**
     * 检查班次是否可以删除
     * @param classid
     * @param dao
     * @return
     */
    private boolean ClassIsUsed(String classid,ContentDAO dao){
    	boolean isCorrect = true;
        KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(),this.userView);
		try {
			ArrayList kqList = (ArrayList)RegisterDate.getKqDayList(frameconn);
			if (kqList == null || kqList.size() <= 0) 
			{
				return isCorrect;
			}
			
			ArrayList dblist = kqUtilsClass.getKqPreList();
			if (dblist == null || dblist.size() <= 0) 
			{
				return isCorrect;
			}else 
			{
				//人员排班
				 StringBuffer sql = new StringBuffer();
				for (int i = 0; i < dblist.size(); i++) 
				{
					sql.append("select");
					if (Sql_switcher.searchDbServer() != Constant.ORACEL) 
					{
						sql.append(" top 1");
						
					}
					sql.append(" a0101");
					sql.append(" from kq_employ_shift");
					String nbase = (String) dblist.get(i);
					sql.append(" where nbase = '" + nbase + "'");
					sql.append(" and class_id = '" + classid + "'");
					if (Sql_switcher.searchDbServer() == Constant.ORACEL) 
					{
						sql.append(" and ROWNUM<=1");
					}
					this.frecset = dao.search(sql.toString());
					sql.setLength(0);
					if (this.frecset.next()) 
					{
						isCorrect = false;
					}
				}
				//单位部门排班
				if (isCorrect)
				{
					sql.setLength(0);
					sql.append("select");
					if (Sql_switcher.searchDbServer() != Constant.ORACEL) 
					{
						sql.append(" top 1");
						
					}
					sql.append(" org_dept_id");
					sql.append(" from kq_org_dept_shift");
					sql.append(" where class_id = '" + classid + "'");
					if (Sql_switcher.searchDbServer() == Constant.ORACEL) 
					{
						sql.append(" and ROWNUM<=1");
					}
					this.frecset = dao.search(sql.toString());
					if (this.frecset.next()) 
					{
						isCorrect = false;
					}
				}
				//周期排班
				if (isCorrect) 
				{
					sql.setLength(0);
					sql.append("select");
					if (Sql_switcher.searchDbServer() != Constant.ORACEL) 
					{
						sql.append(" top 1");
						
					}
					sql.append(" shift_id");
					sql.append(" from kq_shift_class");
					sql.append(" where class_id = '" + classid + "'");
					if (Sql_switcher.searchDbServer() == Constant.ORACEL) 
					{
						sql.append(" and ROWNUM<=1");
					}
					this.frecset = dao.search(sql.toString());
					if (this.frecset.next()) 
					{
						isCorrect = false;
					}
				}
				
				//加班表
				if (isCorrect) 
				{
					sql.setLength(0);
					sql.append("select");
					if (Sql_switcher.searchDbServer() != Constant.ORACEL) 
					{
						sql.append(" top 1");
						
					}
					sql.append(" Q1101");
					sql.append(" from Q11");
					sql.append(" where Q1104 = '" + classid + "'");
					if (Sql_switcher.searchDbServer() == Constant.ORACEL) 
					{
						sql.append(" and ROWNUM<=1");
					}
					this.frecset = dao.search(sql.toString());
					if (this.frecset.next()) 
					{
						isCorrect = false;
					}
				}
				
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}	 
		return isCorrect;
    }

}
