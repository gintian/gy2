package com.hjsj.hrms.transaction.hire.parameterSet.zpReport;

import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterSetBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class InitZpReportsTrans extends IBusiness {
	/**
	 * <p>Title:</p>
	 * <p>Description:</p>
	 * <p>Company:hjsj</p>
	 * <p>create time:Jun 15, 2011:3:55:11 PM</p>
	 * @author duml
	 * @version 1.0
	 * 
	 */
	public void execute() throws GeneralException {
		ArrayList zpReportList=new ArrayList();
		ParameterSetBo parameterSetBo = new ParameterSetBo(this.getFrameconn());
		ArrayList hireObjList = parameterSetBo.getCodeValueList();//取得招聘对象集合
		CommonData cd=null;
		cd=new CommonData();
		cd.setDataName("首页公告");
		cd.setDataValue("0");
		zpReportList.add(cd);
		for(int i=0;i<hireObjList.size();i++)//将获取招聘对象改成自动获取，不是指定的社会招聘和校园招聘
		{
			LazyDynaBean abean = (LazyDynaBean)hireObjList.get(i);
			String codeitemid = (String)abean.get("codeitemid");
			String invalid = (String)abean.get("invalid");
			
			//招聘对象状态设置为无效时不输出
			if("0".equalsIgnoreCase(invalid))
				continue;
			
			if(!"03".equals(codeitemid) && !"03".equals(codeitemid)) {
				cd=new CommonData();
				cd.setDataName(AdminCode.getCode("35", codeitemid).getCodename() + "公告");
				cd.setDataValue(":" + codeitemid);//由于招聘公示是13，可能这里也有value是13的导致后台不知道谁是谁了，加上：进行区别
				zpReportList.add(cd);
			}
			
		}
		//新增猎头招聘的公告 猎头招聘暂不使用
		/*cd = new CommonData();
		cd.setDataName(ResourceFactory.getProperty("hire.out.headhunter.zpReport"));
		cd.setDataValue("4");
		zpReportList.add(cd);*/
		//新增招聘公示的公告
		cd = new CommonData();
		cd.setDataName("招聘公示");
		cd.setDataValue("13");
		zpReportList.add(cd);
		
		String zpReportContent="";
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String zpReport=(String)hm.get("zpReport");
		StringBuffer sql=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		
		if(zpReport!=null&&zpReport.length()!=0){
			
			if("1".equalsIgnoreCase(zpReport)){
				sql.append("select * from constant where constant='");
				sql.append("ZP_SY_MESSAGE'");
			}
			
			if("2".equalsIgnoreCase(zpReport)){
				sql.append("select * from constant where constant='");
				sql.append("ZP_SOCIAL_MESSAGE'");
				
			}
			
			if("3".equalsIgnoreCase(zpReport)){
				sql.append("select * from constant where constant='");
				sql.append("ZP_SCHOOL_MESSAGE'");
			}
			
			if("4".equalsIgnoreCase(zpReport)){//猎头招聘
				sql.append("select * from constant where constant='");
				sql.append("ZP_HEADHUNTER_MESSAGE'");
			}
			try {
				this.frowset=dao.search(sql.toString());
				Sql_switcher sqlswitcher = new Sql_switcher();
				if(this.frowset.next())
					zpReportContent= sqlswitcher.readMemo(this.frowset,"str_value");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else{
			zpReport="1";
			
			sql.append("select * from constant where constant='");
			sql.append("ZP_SY_MESSAGE'");
			
			try {
				this.frowset=dao.search(sql.toString());
				Sql_switcher sqlswitcher = new Sql_switcher();
				if(this.frowset.next())
					zpReportContent= sqlswitcher.readMemo(this.frowset,"str_value");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(hm.get("b_init")!=null){
			zpReport="1";
			hm.remove("b_init");
		}
		this.getFormHM().put("zpReport", zpReport);
		if(hm.get("b_change")!=null){
			hm.remove("b_change");
		}else
			this.getFormHM().put("zpReportList", zpReportList);
		this.getFormHM().put("zpReportContent", zpReportContent);
	}

}
