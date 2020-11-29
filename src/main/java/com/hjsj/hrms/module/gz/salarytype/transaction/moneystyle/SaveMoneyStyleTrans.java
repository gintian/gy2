package com.hjsj.hrms.module.gz.salarytype.transaction.moneystyle;

import com.hjsj.hrms.module.gz.salarytype.businessobject.MoneyStyleSetBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

/**
 * 项目名称 ：ehr7.x
 * 类名称：SaveMoneyStyleTrans
 * 类描述：保存币种
 * 创建人： lis
 * 创建时间：2015-12-3
 */
public class SaveMoneyStyleTrans extends IBusiness{

	@Override
    public void execute() throws GeneralException {
		try
		{
			MorphDynaBean formValues=(MorphDynaBean)this.getFormHM().get("formValues");
			String cname="";
		    String ctoken="";
		    String cunit="";
		    double nratio=0;
		    int nstyleid=0;
		    int flag=0;
		    String id=(String)formValues.get("nstyleid");
		    if(StringUtils.isNotBlank(id))//编辑
		    {
		    	nstyleid=Integer.parseInt(id);
		    	nratio=Double.valueOf(formValues.get("nratio").toString());
		    	flag=0;
		    }
		    
		    else//新增
		    {
		        MoneyStyleSetBo bo = new MoneyStyleSetBo(this.getFrameconn());
		        nstyleid=bo.getMoneyStyleId();
		        nratio=Double.valueOf(formValues.get("nratio").toString());
		        flag=1;
		    }
		    ContentDAO dao = new ContentDAO(this.getFrameconn());
		    cname=(String)formValues.get("cname");
		    ctoken=(String)formValues.get("ctoken");
		    cunit=(String)formValues.get("cunit");
		    RecordVo vo = new RecordVo("moneystyle");
		    vo.setInt("nstyleid",nstyleid);
		    vo.setString("ctoken",PubFunc.hireKeyWord_filter_reback(ctoken));
		    vo.setString("cunit",cunit);
		    vo.setDouble("nratio",nratio);
		    vo.setString("cstate","");
		    vo.setString("cname",cname);
		    if(flag==0)
		    {
		    	dao.updateValueObject(vo);
		    }
		    else if(flag==1)
		    {
		    	dao.addValueObject(vo);
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
