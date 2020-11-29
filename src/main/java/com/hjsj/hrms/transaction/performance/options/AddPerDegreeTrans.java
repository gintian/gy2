package com.hjsj.hrms.transaction.performance.options;

import com.hjsj.hrms.businessobject.performance.options.PerDegreeBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;  

/**
 * <p>Title:AddPerDegreeTrans.java</p>
 * <p>Description:考核等级/新增考核等级</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-07-15 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class AddPerDegreeTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		RecordVo votemp = (RecordVo) this.getFormHM().get("perdegreevo");
		String degreeId = votemp.getString("degree_id");
		String degreename = votemp.getString("degreename");
		String degreedesc = votemp.getString("degreedesc");
		String topscore = votemp.getString("topscore");
		String domainflag = votemp.getString("domainflag");
		String flag = votemp.getString("flag");
		String used = votemp.getString("used");		

		String info = (String) this.getFormHM().get("info");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
//		String degreeId=(String)this.getFormHM().get("degreeId");
//		String degreename=(String)this.getFormHM().get("degreename");
//		String degreedesc=(String)this.getFormHM().get("degreedesc");
//		String topscore=(String)this.getFormHM().get("topscore");
//		String used=(String)this.getFormHM().get("used");
//		String flag=(String)this.getFormHM().get("flag");
//		String domainflag=(String)this.getFormHM().get("domainflag");
//		System.out.println("degreeId:"+degreeId+" degreename:"+degreename+" degreedesc:"+degreedesc+" topscore:"+topscore+" domainflag:"+domainflag+" flag:"+flag+" used:"+used);
		
		String b0110 ="HJSJ";		
		PerDegreeBo bo = new PerDegreeBo(this.getFrameconn());
		if(this.userView.getStatus()==0)
		{
			//Operuser中的用户如果未指定操作单位，则填HJSJ,对所有单位可见，否则填指定操作单位编码
			if(this.userView.getManagePrivCodeValue()!=null&&!"".equals(this.userView.getManagePrivCodeValue()))
			{
				b0110 = this.userView.getManagePrivCodeValue();
			} 
		}
		else if(this.userView.getStatus()==4)
		{
			//如果是自助平台用户，则取其单位编码，为空的话，则为HJSJ
			String a0100=this.userView.getA0100();
			String pre = this.userView.getDbname();
			String unit=bo.getB0110(pre, a0100);
			if(unit!=null&&!"".equals(unit))
			{
				b0110 = unit;
			}
		}	
	
		
		try
		{
			RecordVo vo=new RecordVo("per_degree");
			if("".equals(degreeId)||degreeId==null)
			{
				IDGenerator idg=new IDGenerator(2,this.getFrameconn());
				degreeId=idg.getId("per_degree.degree_id"); 				  
                vo.setString("degree_id",degreeId);
                vo.setString("degreename",degreename);
                vo.setString("degreedesc",degreedesc); 
                vo.setString("used",used); 
                vo.setString("flag",flag);
                vo.setString("domainflag",domainflag); 
                vo.setString("b0110", b0110);
	            dao.addValueObject(vo);
	            this.getFormHM().put("info","addend");  
	              
    	    }else
    	    {
	    		vo.setString("degree_id",degreeId);
                vo.setString("degreename",degreename);
                vo.setString("degreedesc",degreedesc); 
                vo.setString("topscore",topscore);
                vo.setString("used",used); 
                vo.setString("flag",flag);
                vo.setString("domainflag",domainflag); 
                vo.setString("b0110", b0110);
	   		    dao.updateValueObject(vo);	
    	   		this.getFormHM().put("info","updateend");
    	   		this.getFormHM().put("degreeId","");
    	    }
			  
	    }catch(Exception exx)
	    {
	       exx.printStackTrace();
	       throw GeneralExceptionHandler.Handle(exx);
	    }finally
	    {
//          this.getFormHM().put("type",null);
//          this.getFormHM().put("typeid","");
        }

	}
	
	
	
	public synchronized int getDegreeId() throws GeneralException
	{
		int num = 0;  //序号默认为0
		String sql="select max(degree_id) as num  from per_degree";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{	
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
			{
				num = this.frowset.getInt("num");
			}
			
		}catch(Exception e)
		{
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}	
		return num+1;		
	}

}
