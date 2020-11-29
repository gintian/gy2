package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class DelPositionDemandTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		
		//String name=(String)hm.get("position_set_table");
		//ArrayList list=(ArrayList)hm.get("position_set_record");		
		ContentDAO dao=null;
		try
		{
			StringBuffer sql_whl=new StringBuffer("");
			StringBuffer zpt_whl=new StringBuffer("");
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			/**由于安全平台改造，/被转换成了全角的所以要把这个转换回来**/
			String tempZ0301=(String) hm.get("z0301");
			tempZ0301=tempZ0301.replaceAll("／", "/");
			String[]  z0301s=tempZ0301.split("/");
			/**安全改造,判断是否由要删除的z0301是否存在后台begin**/
			//如果查询出来的数据比要删除的少，那么就说明至少有一个没有权限删除
			String sql = (String) this.userView.getHm().get("hire_sql");
			int index = sql.indexOf("order by");
			if(index!=-1){
				sql = sql.substring(0, index);
			}	
			sql = sql+" and z0301 in(";
			for(int i=0;i<z0301s.length;i++){
				String temp_Z0301=z0301s[i];
				if(i==0){
					sql=sql+temp_Z0301;
				}else{
					sql=sql+","+temp_Z0301;
				}
			}
			sql=sql+")";
			dao=new ContentDAO(this.getFrameconn());	
			this.frowset = dao.search(sql);
			int count=0;
			while(this.frowset.next()){
				count++;
			}
			if(count<z0301s.length){
				throw new GeneralException(ResourceFactory.getProperty("label.hireemploye.no.contorl"));
			}
			/**安全改造,判断是否由要删除的z0301是否存在后台end**/
			StringBuffer z04=new StringBuffer("");
			PositionDemand pd = new PositionDemand(this.getFrameconn());
			String moreLevelSP=(String) this.getFormHM().get("moreLevelSP");
			for(int i=0;i<z0301s.length;i++)
			{
				if(moreLevelSP!=null&& "1".equals(moreLevelSP)){//如果是多级审批那么只有当前操作人才有驳回审批报批的权限
					pd.checkCanOperate(z0301s[i], userView);//查看当前用户是否是选中记录的当前操作人员
				}
				sql_whl.append(" or z0301='"+z0301s[i]+"'");
				zpt_whl.append(" or zp_pos_id='"+z0301s[i]+"'");
				z04.append(" or z0407='"+z0301s[i]+"'");
			}
					
			dao.delete("delete from z03 where "+sql_whl.substring(3),new ArrayList());
			dao.delete("delete from zp_pos_tache where "+zpt_whl.substring(3),new ArrayList());
			VersionControl vc = new VersionControl();
			if(vc.searchFunctionId("31015"))
	     		dao.delete("delete from z04 where "+z04.substring(3),new ArrayList());
			
		/*	if(!(list==null||list.size()==0))
			{
				StringBuffer sql_whl=new StringBuffer("");
				StringBuffer info=new StringBuffer("");
				for(int i=0;i<list.size();i++)
				{
					RecordVo vo=(RecordVo)list.get(i);
					
					if(!vo.getString("z0319").equals("01")&&!vo.getString("z0319").equals("06")&&!vo.getString("z0319").equals("02")&&!vo.getString("z0319").equals("07"))
					{	info.append("只能删除起草、已报批、驳回、结束状态的需求！");
						
					}
					if(info.length()>1)
						throw GeneralExceptionHandler.Handle(new Exception(info.toString()));
					sql_whl.append(" or z0301='"+vo.getString("z0301")+"'");
				}					
				if(sql_whl.length()>3)
				{
					dao=new ContentDAO(this.getFrameconn());			
	   			    dao.delete("delete from "+name+" where "+sql_whl.substring(3),new ArrayList());
				}*/
	   		//	    PositionDemand positionDemand=new PositionDemand(this.getFrameconn());
   			//    positionDemand.updateMusterRecidx(name,"z0301");
				
			//}
	    }
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		
		

	}

}
