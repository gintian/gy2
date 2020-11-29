package com.hjsj.hrms.transaction.kq.options.kq_class;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class EditKqClassTreeTrans extends IBusiness implements KqClassConstant{

	public void execute() throws GeneralException
	{
	  String class_id=(String)this.getFormHM().get("class_id");
	  String class_flag=(String)this.getFormHM().get("class_flag");
	  String class_name=(String)this.getFormHM().get("class_name");
	  if(class_flag==null||class_flag.length()<=0)
	    return;	 
	  if("add".equals(class_flag))
	  {
		  class_id = addKqClass(class_name);
		  this.getFormHM().put("class_id",class_id);
	  }else if("up".equals(class_flag))
	  {
		  updateKqClass_name(class_id,class_name);
		  this.getFormHM().put("class_id",class_id);
	  }
	  this.getFormHM().put("class_flag",class_flag);
	  this.getFormHM().put("class_name",class_name);
	  
	}
	/**
	 * 建立新的基本班次
	 *
	 */
    public String addKqClass(String class_name)throws GeneralException
    {
    	String class_id="";
    	if(class_name==null||class_name.length()<=0)
    		return class_id;
    	ArrayList list =new  ArrayList();
    	IDGenerator idg=new IDGenerator(2,this.getFrameconn());
    	ContentDAO dao=new ContentDAO(this.getFrameconn()); 
    	KqUtilsClass kqcl = new KqUtilsClass(this.getFrameconn(),this.userView);
    	try
    	{
    		 this.frowset= dao.search("select 1 from "+this.kq_class_table+" where "+this.kq_class_name+"='"+class_name+"'");
       		 if(this.frowset.next())
       			 throw GeneralExceptionHandler.Handle(new GeneralException("","班次名称已经存在","",""));
    		class_id=idg.getId(this.kq_class_table+"."+this.kq_class_id).toUpperCase();
        	String sql="insert into "+this.kq_class_table+" ("+this.kq_class_id+", "+this.kq_class_name+", "+this.kq_class_orgId+","+this.kq_class_displayorder+") values (?,?,?,?)";
    	    list.add(class_id);
    	    list.add(class_name);
    	    // 获取当前用户所在部门编码，并加逗号 支持多选部门
    	    list.add(kqcl.getKqClassManageCode() + ",");
    	    list.add(class_id);
    	    dao.insert(sql,list);
    	    if(class_id!=null&&class_id.length()>0)
			{
				int l_id=Integer.parseInt(class_id);
				UserObjectBo user_bo = new UserObjectBo(this.getFrameconn());
				user_bo.saveResource(l_id+"", this.userView,
						IResourceConstant.KQ_BASE_CLASS);
			}
    	    String aa = class_id.replaceAll("[^0]","#");
    	    class_id = class_id.substring(aa.indexOf("#"));
    	    return class_id;
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e); 
    	}    	
    }
    
    public void updateKqClass_name(String class_id,String name)throws GeneralException 
    {
    	if(class_id==null||class_id.length()<=0)
    		return;
    	StringBuffer sql=new StringBuffer();
    	sql.append("update "+this.kq_class_table+" set");
    	sql.append(" "+this.kq_class_name+"='"+name+"'");
    	sql.append(" where "+this.kq_class_id+"='"+class_id+"'");
    	ContentDAO dao= new ContentDAO(this.getFrameconn());
    	try
    	{
    		 String sq="select 1 from "+this.kq_class_table+" where "+this.kq_class_name+"='"+name+"' and "+this.kq_class_id+"<>'"+class_id+"'";
    		 this.frowset= dao.search(sq);
       		 if(this.frowset.next())
       			 throw GeneralExceptionHandler.Handle(new GeneralException("","班次名称已经存在","",""));
    		dao.update(sql.toString());
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    }
}
