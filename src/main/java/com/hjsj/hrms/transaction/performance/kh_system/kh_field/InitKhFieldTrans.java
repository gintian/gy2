package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:InitKhFieldTrans.java</p>
 * <p>Description:展现考核指标</p>
 * @author JinChunhai
 */

public class InitKhFieldTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String pointsetid=(String)map.get("pointsetid");
			String subsys_id = (String)map.get("subsys_id");
			RecordVo vo = new RecordVo("per_point");
			String entery=(String)map.get("entery");
			if(!vo.hasAttribute("pointtype")||!vo.hasAttribute("pointctrl"))
			{
				throw GeneralExceptionHandler.Handle(new Exception("未找到\"定量指标计分规则\"所需字段，请升级数据库！"));
			}
			
			//  程序自动添加:行为建议 proposal 字段
			editArticle();
			
			String point_id = "-1";
			ArrayList fieldinfolist=null;
			if("root".equalsIgnoreCase(pointsetid)|| "-1".equals(pointsetid))
			{
				 fieldinfolist = new ArrayList();
				 pointsetid="-1";
			}
			else
			{
	    		KhFieldBo bo = new KhFieldBo(this.getFrameconn());
	    	    fieldinfolist = bo.getKhFieldInfo(pointsetid,this.userView);
	    		
	    		if("1".equals(entery)&&fieldinfolist.size()>0)
	    		{
	    			String pointId = (String)this.getFormHM().get("point_id");
	    			if(pointId!=null && pointId.trim().length()>0)
	    				point_id = pointId;
	    			else
	    				point_id=(String)(((LazyDynaBean)fieldinfolist.get(0)).get("point_id"));
		    	}
	    		else
	    		{
	    			if(fieldinfolist.size()>0)
	    		    	point_id=(String)map.get("pointid");
	    				
	    		}
			}
			this.getFormHM().put("fieldinfolist",fieldinfolist);
			this.getFormHM().put("pointCount",fieldinfolist.size()+"");
			this.getFormHM().put("pointsetid",pointsetid);
			this.getFormHM().put("point_id", point_id);
			this.getFormHM().put("subsys_id",subsys_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	// 检查per_point表中有没有proposal字段，若没有就创建  JinChunhai 2011.10.28
    public void editArticle() throws GeneralException
	{
		try
		{			
			String tablename = "per_point";
			Table table = new Table(tablename);
			DbWizard dbWizard = new DbWizard(this.frameconn);
			boolean flag = false;
			if (!dbWizard.isExistField(tablename, "proposal", false))
			{
				Field obj = new Field("proposal");	
				obj.setDatatype(DataType.CLOB);
				obj.setKeyable(false);
				table.addField(obj);
				flag = true;
			}						
			if (flag)
				dbWizard.addColumns(table);// 更新列
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
    }

}
