package com.hjsj.hrms.transaction.sys.options.template;

import com.hjsj.hrms.businessobject.hire.HireTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;



public class AddTemplateTrans extends IBusiness{
	public void execute() throws GeneralException {
		
			HashMap hm =(HashMap)this.getFormHM().get("requestPamaHM");
			int template_id = 0;
			String id =(String)this.getFormHM().get("id");
		     String temp_template_id =((String)this.getFormHM().get("template_id"));
		     int flag = 0;
		     if(temp_template_id != null&&temp_template_id.trim().length() !=0){
			    template_id = new Integer(temp_template_id).intValue();
		     }else{
			    IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			    template_id = new Integer(idg.getId("t_sys_msgtemplate.template_id")).intValue();
			    flag = 1;
		     }
	     try{ 
	    	 /*String a0100=this.userView.getA0100();
	    	 String pre=this.userView.getDbname();
		     String b0110 ="";
		     String temp=this.userView.getUnit_id();
		     if(temp!=null&&temp.trim().length()>2)
		     {
		    	 b0110=temp.substring(2);
		     }else
		     {
		    	 if(pre!=null&&pre.trim().length()>0)
	    	         b0110=this.getUserUnitId(a0100,pre);
		     }*/
	    	 HireTemplateBo bo = new HireTemplateBo(this.getFrameconn());
	    	 String b0110=bo.getB0110(this.userView);
		     int template_type = 0;
		     ContentDAO dao = new ContentDAO(this.getFrameconn());
		     RecordVo vo = new RecordVo("t_sys_msgtemplate");
		     vo.setString("name",PubFunc.keyWord_reback((String)this.getFormHM().get("name")));
		     vo.setString("zploop",PubFunc.keyWord_reback((String)this.getFormHM().get("zpLoopNew")));
		     if("0".equals(this.getFormHM().get("type")))
			       template_type = 0;
		     else if("1".equals(this.getFormHM().get("type")))
			       template_type = 1;
		     vo.setInt("template_type",template_type);
		     vo.setInt("template_id",template_id);
		     vo.setString("title",PubFunc.keyWord_reback((String)this.getFormHM().get("title")));
		     vo.setString("content",PubFunc.keyWord_reback((String)this.getFormHM().get("content")));
		     vo.setString("adress",PubFunc.keyWord_reback((String)this.getFormHM().get("address")));
		     vo.setString("b0110",b0110);
		     vo.setString("id",id);
		     if(flag == 1){
		          dao.addValueObject(vo);
		     }else
			      dao.updateValueObject(vo);
		     //this.getFormHM().put("zpLoop","#");
	          }catch(Exception e){
		          e.printStackTrace();
	          }
	          hm.remove("b_add2");
		}
	
	/**
	 * 取得登录用户的单位编码
	 * @param a0100
	 * @param pre
	 * @return
	 */
	public String getUserUnitId(String a0100,String pre)
	{
		String b0110="";
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append(" select b0110 from ");
			buf.append(pre+"a01 where a0100='");
			buf.append(a0100+"'");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(buf.toString());
			while(this.frowset.next())
			{
				b0110=this.frowset.getString("b0110");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return b0110;
	}
	

}
