package com.hjsj.hrms.transaction.hire.parameterSet.configureParameter;

import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.HashMap;

/**
 * 0202001022
 * <p>Title:EditSchoolPositionTrans.java</p>
 * <p>Description>:EditSchoolPositionTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Oct 14, 2010  4:08:12 PM </p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class EditSchoolPositionTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String positionID=(String)map.get("positionID");
			String schoolPositionOrg="";
			String schoolPositionOrgDesc="";
			String schoolPositionDesc="";
			String schoolPositionId="";
			String oldID="1";
			if(positionID!=null&&positionID.trim().length()>0)
			{
				ParameterSetBo bo = new ParameterSetBo(this.getFrameconn());
				LazyDynaBean bean = bo.getSchoolPositionInfo(positionID);
				if(bean.get("schoolPositionOrg")!=null)
					schoolPositionOrg=(String)bean.get("schoolPositionOrg");
				if(bean.get("schoolPositionOrgDesc")!=null)
					schoolPositionOrgDesc=(String)bean.get("schoolPositionOrgDesc");
				if(bean.get("schoolPositionDesc")!=null)
					schoolPositionDesc=(String)bean.get("schoolPositionDesc");
				if(bean.get("schoolPositionId")!=null)
					schoolPositionId=(String)bean.get("schoolPositionId");
				oldID="2";
				if(bean.get("schoolPositionId")==null)//zzk
					oldID="1";
			}
			this.getFormHM().put("schoolPositionOrg",schoolPositionOrg);
			this.getFormHM().put("schoolPositionOrgDesc", schoolPositionOrgDesc);
			this.getFormHM().put("schoolPositionDesc", schoolPositionDesc);
			this.getFormHM().put("schoolPositionId", schoolPositionId);
			this.getFormHM().put("oldID", oldID);
			this.getFormHM().put("posdesc", schoolPositionDesc);//dumeilong
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
