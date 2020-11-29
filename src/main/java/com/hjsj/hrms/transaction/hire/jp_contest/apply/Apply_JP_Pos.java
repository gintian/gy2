package com.hjsj.hrms.transaction.hire.jp_contest.apply;

import com.hjsj.hrms.businessobject.hire.JingPingPosBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:Apply_JP_Pos.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 21, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class Apply_JP_Pos extends IBusiness {
	public void execute() throws GeneralException {
		try 
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			JingPingPosBo jpbo = new JingPingPosBo(this.getFrameconn());
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
			String z0700 = (String)hm.get("z0700");
			if(z0700==null || "".equals(z0700))
			{
				z0700 = (String)this.getFormHM().get("z0700");
			}
//			System.out.println(z0700);
			String postion = this.getPosition(z0700,dao);	    
		    String pos_parent = this.getPosParent(z0700,dao);
		    String posid = this.getApplyPosId(z0700,dao);
		    String applystate = this.getApplyPosState(z0700,dao);
			ArrayList apply_file_list = this.getApplyFile(z0700,dao);
			String returnflag = (String)this.getFormHM().get("returnflag");
			this.getFormHM().put("apply_file_list",apply_file_list);
			this.getFormHM().put("postion",postion);
			this.getFormHM().put("pos_parent",pos_parent);
			this.getFormHM().put("z0700",z0700);
			this.getFormHM().put("posid",posid);
			this.getFormHM().put("applystate",applystate);
			this.getFormHM().put("a0100",this.userView.getA0100());
			this.getFormHM().put("nbase",this.userView.getDbname());
			this.getFormHM().put("userpriv",this.userView.getDbpriv().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public ArrayList getApplyFile(String z0700,ContentDAO dao)
	{
		ArrayList retlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append(" select * from ZP_APPLY_FILE ");
		sql.append("  where ID in (");
		sql.append(" select ID from ZP_APPLY_JOBS where Z0700= ");
		sql.append(z0700);
		sql.append(" and A0100='"+this.userView.getA0100()+"')" );
		try 
		{
			retlist = dao.searchDynaList(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return retlist;
	}
	
	public String getPosParent(String z0700,ContentDAO dao)
	{
		String pos_parent = "";
		String p_sql = "select parentid from organization where codeitemid='"+z0700+"'";
		p_sql = p_sql.toUpperCase();
		try 
		{
			this.frowset = dao.search(p_sql);
			if(this.frowset.next())
			{
				pos_parent = this.frowset.getString("PARENTID");
				String temp = "";
				if(pos_parent !=null && pos_parent.trim().length()>0)
					temp=AdminCode.getCode("UN",pos_parent)!=null?AdminCode.getCode("UN",pos_parent).getCodename():"";
				if(temp ==null && temp.trim().length()<1)
					temp=AdminCode.getCode("UM",pos_parent)!=null?AdminCode.getCode("UM",pos_parent).getCodename():"";					
				if(!(temp==null || "".equals(temp)))
				{
					pos_parent = temp;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return pos_parent.trim();
	}
	
	public String getPosition(String z0700,ContentDAO dao)
	{
		String postion = "";
		try 
		{
			RecordVo vo = new RecordVo("Z07");
			vo.setString("z0700",z0700);
		    RecordVo a_vo =dao.findByPrimaryKey(vo);
		    postion = a_vo.getString("z0701");
		    String temp = "";
			if(postion !=null && postion.trim().length()>0)
				temp=AdminCode.getCode("@K",postion)!=null?AdminCode.getCode("@K",postion).getCodename():"";
				if(!(temp==null || "".equals(temp)))
				{
					postion = temp;
				}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	    return postion.trim();
	}
	
	public String getApplyPosId(String z0700,ContentDAO dao)
	{
		String id = "";
		StringBuffer sql = new StringBuffer();
		sql.append(" select ID from ZP_APPLY_JOBS ");
		sql.append("  where Z0700 = "+z0700);
		sql.append(" and A0100='"+this.userView.getA0100()+"'");
		try 
		{
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next())
			{
				id = this.frowset.getInt("ID")+"";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return id;
	}

	public String getApplyPosState(String z0700,ContentDAO dao)
	{
		String state = "";
		StringBuffer sql = new StringBuffer();
		sql.append(" select STATE from ZP_APPLY_JOBS ");
		sql.append("  where Z0700 = "+z0700);
		sql.append(" and A0100='"+this.userView.getA0100()+"'");
		try 
		{
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next())
			{
				state = this.frowset.getString("STATE");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return state;
	}


}