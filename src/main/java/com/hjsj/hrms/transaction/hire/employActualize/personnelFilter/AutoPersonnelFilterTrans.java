package com.hjsj.hrms.transaction.hire.employActualize.personnelFilter;

import com.hjsj.hrms.businessobject.hire.EmployActualize;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 人岗匹配
 * @author Owner
 *
 */
public class AutoPersonnelFilterTrans extends IBusiness {
	 /**组合查询SQL*/
    private String combine_SQL(String pos_cond,String dbpre) throws GeneralException
    {
        StringBuffer strexpr=new StringBuffer();
        StringBuffer strfactor=new StringBuffer();
        for (int i = 0; i < pos_cond.length(); i++) {
        
           if("|".equals(pos_cond.substring(i, i + 1))){
           	strexpr.append(pos_cond.substring(0,i));
           	strfactor.append(pos_cond.substring(i+1,pos_cond.length()));
           }
        }
        ArrayList fieldlist=new ArrayList();
      
        FactorList factorlist=new FactorList(strexpr.toString(),strfactor.toString(),dbpre,false,false,true,1,userView.getUserName());
        return factorlist.getSqlExpression();
    }
	
    
    
    
	public void execute() throws GeneralException {
		EmployActualize employActualize=new EmployActualize(this.getFrameconn());
		String codeid=(String)this.getFormHM().get("codeid");
		String dbname=employActualize.getZP_DB_NAME();
		
		
		StringBuffer sql1=new StringBuffer("select zp.zp_pos_id, z03.z0311,organization.pos_cond  from zp_pos_tache zp");
		sql1.append(" left join  Z03 on zp.ZP_POS_ID=Z03.Z0301 left join  Z01 on Z03.Z0101=Z01.Z0101 ");
		sql1.append(" left join  organization on z03.z0311=organization.codeitemid ");
		sql1.append(" where 1=1 ");
		if(!"0".equals(codeid))
			sql1.append(" and Z03.Z0311 like '"+codeid+"%' ");
		sql1.append(" and Z01.Z0129='04' ");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list = new ArrayList();
	    try
	    {
	    	 this.frowset = dao.search(sql1.toString());
		     while(this.frowset.next())
		     {
		    	 boolean flag = false;
		    	 String zp_pos_id=this.getFrowset().getString("zp_pos_id");
		         String z0311 = this.getFrowset().getString("z0311");
		         String pos_cond = Sql_switcher.readMemo(this.frowset,"pos_cond");
		         if(pos_cond == null || "".equals(pos_cond))
		          	continue; 
		         String strwhere=combine_SQL(pos_cond,dbname);
		         
		         /*删除不符合条件的职位申请*/
		          sql1.setLength(0);
		          sql1.append("delete from zp_pos_tache where a0100 not in (select ");
		          sql1.append(dbname);
		          sql1.append("a01.a0100 ");
		          sql1.append(strwhere);
		          sql1.append(" or "+dbname+"a01.state!='10' ");  //只能删除 未选状态 的数据
		          sql1.append(") and zp_pos_id='");
		          sql1.append(zp_pos_id);
		          sql1.append("'");
		        //  System.out.println(sql1.toString());
		          dao.delete(sql1.toString(),list);
		     }
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }

	}

}
