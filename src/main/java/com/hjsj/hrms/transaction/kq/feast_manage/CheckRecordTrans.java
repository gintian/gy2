package com.hjsj.hrms.transaction.kq.feast_manage;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.feast_manage.FeastComputer;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 检查是该假期类型已经进行过计算
 * <p>Title:CheckRecordTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 11, 2007 11:21:26 AM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class CheckRecordTrans  extends IBusiness {

	public void execute() throws GeneralException
	{
		String code=(String)this.getFormHM().get("code");
		String kind=(String)this.getFormHM().get("kind");
		String year=(String)this.getFormHM().get("year");
		String hols_status=(String)this.getFormHM().get("hols_status");
		ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
		String b0110=managePrivCode.getPrivOrgId();  
		if(kind==null||kind.length()<=0||code==null||code.length()<=0)
		{
			kind="2";
			code=b0110;
		}
		ArrayList kq_dbase_list=RegisterInitInfoData.getB0110Dase(this.getFormHM(),this.userView,this.getFrameconn(),b0110);
		StringBuffer whl=new StringBuffer();
		if(kq_dbase_list==null||kq_dbase_list.size()<=0)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.dbase.nosave"),"",""));
		}
		String whereIN="";
		for(int i=0;i<kq_dbase_list.size();i++)
		{
			String userbase= userView.getPrivDbList().get(i).toString();
			whereIN=RegisterInitInfoData.getWhereINSql(userView,userbase);
			whl.append("select * from q17");
			whl.append(" where 1=1");
			if("2".equals(kind))
				whl.append(" and b0110='"+code+"'");
			else if("1".equals(kind))
			  whl.append(" and e0122='"+code+"'");
			else if("0".equals(kind))
				  whl.append(" and e01a1='"+code+"'");
			whl.append(" and q1701='"+year+"'");
			whl.append(" and q1709='"+hols_status+"'");
			whl.append(" and a0100 in(select a0100 "+whereIN+")");
			whl.append(" UNION ");
		   }
		whl.setLength(whl.length()-7);	
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    FeastComputer feastComputer=new FeastComputer(this.getFrameconn(),this.userView);
	    ArrayList holi_list=feastComputer.getHolsList(hols_status);	
	    String status_name="";
	    if(holi_list!=null&&holi_list.size()>0)
	    {
	    	CommonData co=(CommonData)holi_list.get(0);
	    	status_name=co.getDataName();
	    }
	    
	    try
	    {
	    	this.frowset=dao.search(whl.toString());
	    	StringBuffer mess=new StringBuffer();
	    	if(this.frowset.next())
	    	{
	    		mess.append(year+"年度已进行过"+year+"年"+status_name+"计算！\n如果重新计算，将会覆盖原休假数据。\n您确定要重新计算吗？");
	    		this.getFormHM().put("mess",mess.toString());
	    	}else
	    	{
	    		mess.append("您确认计算"+year+"年"+status_name+"吗？");
	    		this.getFormHM().put("mess",mess.toString());
	    	}
	    }catch(Exception ee)
	    {
	    	ee.printStackTrace();
	    }
		
		
	}

}
