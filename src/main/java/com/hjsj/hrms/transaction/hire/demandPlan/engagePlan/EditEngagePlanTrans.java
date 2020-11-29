package com.hjsj.hrms.transaction.hire.demandPlan.engagePlan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class EditEngagePlanTrans extends IBusiness {

	public void execute() throws GeneralException {
		this.getFormHM().put("operate","b");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String z0101=(String)hm.get("z0101");
		

		String z0103="";
		String z0105="";	
		String z0105View="";
		String z0107="";
		String z0109="";
		String z0111="";		
		String z0115="";
		String z0119="";
		String z0119View="";
		String z0121="";
		String z0123="";
	    String z0125="";
		String z0127="";
		String z0129="";
		String z0113="";
		String z0117="";
		
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer sql=new StringBuffer("select z0103,z0105,a.codeitemdesc z0105View,");
			sql.append(Sql_switcher.numberToChar(Sql_switcher.year("z0107"))+Sql_switcher.concat()+"'-'"+Sql_switcher.concat()+Sql_switcher.numberToChar(Sql_switcher.month("z0107"))+Sql_switcher.concat()+"'-'"+Sql_switcher.concat()+Sql_switcher.numberToChar(Sql_switcher.day("z0107")));
			sql.append(" z0107,");
			sql.append(Sql_switcher.numberToChar(Sql_switcher.year("z0109"))+Sql_switcher.concat()+"'-'"+Sql_switcher.concat()+Sql_switcher.numberToChar(Sql_switcher.month("z0109"))+Sql_switcher.concat()+"'-'"+Sql_switcher.concat()+Sql_switcher.numberToChar(Sql_switcher.day("z0109")));
			sql.append(" z0109,z0111,z0113,z0115,");
			sql.append("z0117,z0119,b.codeitemdesc z0119View,z0121,z0123,z0125,z0127,z0129 ");
			sql.append(" from z01 left join organization a  on  z01.z0105=a.codeitemid  left join organization b  on  z01.z0119=b.codeitemid where  z0101='"+z0101+"'");
			
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
			{
				z0103=this.frowset.getString("z0103");
				z0105=this.frowset.getString("z0105");	
				z0105View=this.frowset.getString("z0105View");
				z0107=this.frowset.getString("z0107");
				z0109=this.frowset.getString("z0109");
				z0111=this.frowset.getString("z0111");
				z0115=this.frowset.getString("z0115");
				z0119=this.frowset.getString("z0119");
				z0119View=this.frowset.getString("z0119View");
				z0121=this.frowset.getString("z0121");
				z0123=this.frowset.getString("z0123");
			    z0125=this.frowset.getString("z0125");
				z0127=this.frowset.getString("z0127");
				z0129=this.frowset.getString("z0129");
				z0113=this.frowset.getString("z0113");
				z0117=this.frowset.getString("z0117");
			}
			
			ArrayList hireObjectList=new ArrayList();
			this.frowset=dao.search("select * from codeitem where codesetid='35'");
			while(this.frowset.next())
			{
				CommonData vo=new CommonData(this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
				hireObjectList.add(vo);
			}
			this.getFormHM().put("hireObjectList",hireObjectList);
			
			this.getFormHM().put("z0101",z0101);
			this.getFormHM().put("z0103",z0103);
			this.getFormHM().put("z0105",z0105);
			this.getFormHM().put("z0105View",z0105View);
			this.getFormHM().put("z0107",z0107);
			this.getFormHM().put("z0109",z0109);
			this.getFormHM().put("z0111",z0111);
			this.getFormHM().put("z0115",z0115);
			this.getFormHM().put("z0119",z0119);
			this.getFormHM().put("z0119View",z0119View);
			this.getFormHM().put("z0121",z0121);
			this.getFormHM().put("z0123",z0123);
			this.getFormHM().put("z0125",z0125);
			this.getFormHM().put("z0127",z0127);
			this.getFormHM().put("z0129",z0129);
			this.getFormHM().put("z0113",z0113);
			this.getFormHM().put("z0117",z0117);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		

	}

}
