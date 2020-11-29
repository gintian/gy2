package com.hjsj.hrms.utils.components.defineformula.transaction.standard;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
/**
 * 项目名称 ：ehr
 * 类名称：SaveStandardIDTrans
 * 类描述：保存标准表
 * 创建人： lis
 * 创建时间：2016-2-5
 */
public class SaveStandardIDTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		
		String salaryid = (String)hm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.length()>0?salaryid:"";
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		
		String itemid = (String)hm.get("itemid");
		itemid=itemid!=null&&itemid.length()>0?itemid:"";
		
		String standardid = (String)hm.get("standardid");
		if(StringUtils.isBlank(standardid))
			standardid = null;
		
		String runflag = (String)hm.get("runflag");
		runflag=runflag!=null&&runflag.length()>0?runflag:"";
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		

		try {
			//----------------------执行公式的时候  日志信息  zhaoxg add 2015-4-29------
			RecordVo vo=new RecordVo("salaryformula");
			vo.setInt("salaryid", Integer.parseInt(salaryid));
			vo.setInt("itemid", Integer.parseInt(itemid));
			try {
				vo = dao.findByPrimaryKey(vo);	
			} catch(SQLException e) {
				e.printStackTrace();
			}
			String value = vo.getString("runflag");//=0,计算公式 =1,执行标准 =2,执行税表
			if("0".equals(value)){
				value = ResourceFactory.getProperty("gz_new.gz_expressions");//公式
			}else if("1".equals(value)){
				value = ResourceFactory.getProperty("gz.formula.standart");//标准
			}else if("2".equals(value)){
				value = ResourceFactory.getProperty("gz.formula.scale");//税率表
			}
			String tovalue = ResourceFactory.getProperty("gz.formula.scale");//税率表
			if("0".equals(runflag)){
				tovalue = ResourceFactory.getProperty("gz_new.gz_expressions");//公式
			}else if("1".equals(runflag)){
				tovalue = ResourceFactory.getProperty("gz.formula.standart");//标准
			}else if("2".equals(runflag)){
				tovalue = ResourceFactory.getProperty("gz.formula.scale");//税率表
			}
			StringBuffer context = new StringBuffer();
			SalaryTemplateBo bo = new SalaryTemplateBo(this.frameconn);
			String name = bo.getSalaryName(salaryid);
			context.append(ResourceFactory.getProperty("gz.formula.implementation") + "："+name+"（"+salaryid+"）"+ResourceFactory.getProperty("gz_new.gz_editFormula")+"("+vo.getString("hzname")+")："+value+"--->"+tovalue+"<br>");
			this.getFormHM().put("@eventlog", context.toString());
			//------------------------------------------------------------------------
			String sqlstr = "update salaryformula set standid=?,runflag=? where salaryid=? and itemid=?";
			dao.update(sqlstr,Arrays.asList(standardid,runflag,salaryid,itemid));
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
