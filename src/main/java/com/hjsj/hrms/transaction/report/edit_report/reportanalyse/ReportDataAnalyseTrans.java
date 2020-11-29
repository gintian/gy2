/**
 * 
 */
package com.hjsj.hrms.transaction.report.edit_report.reportanalyse;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.reportanalyse.ReportPDBAnalyse;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:编辑报表中的报表归档数据分析</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 1, 2006:4:57:40 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportDataAnalyseTrans extends IBusiness {

	/**
	 * 改变报表后的数据连动
	 * 1 获得改变后的表号
	 * 2 根据表号连动修改
	 */
	public void execute() throws GeneralException {
		HashMap map = (HashMap)(this.getFormHM().get("requestPamaHM"));
		String editOrreport = (String)map.get("editOrreport");  //wangchaoqun 2014-11-5  标志位，返回时判断用
		String tabid = (String)map.get("tabid");       //报表表号
		String codeFlag = (String)map.get("code"); //填报单位编号
		String scopeid =(String)map.get("scopeid");
		String scopeownerunitid="";
		//通过tabid获得口径的值
		//判断是否存在scopeid字段
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
		
		DbWizard dbWizard=new DbWizard(this.getFrameconn());
		if(dbWizard.isExistField("tb"+tabid, "scopeid",false)){//判断字段是否存在，
		String sql = "select scopeid from tb"+tabid+" where username='"+this.userView.getUserName()+"'";
	
			this.frowset = dao.search(sql);
		
		if(this.frowset.next()){
			if(scopeid==null)
				scopeid=""+this.frowset.getInt("scopeid");
		}
		this.getFormHM().put("use_scope_cond2", "0");
		}else{
			scopeid="0";
			this.getFormHM().put("use_scope_cond2", "1");
		}
		if("1".equals(this.getFormHM().get("use_scope_cond"))){
		if(scopeid!=null&&!"0".equals(scopeid)){
			String sql = "select * from tscope where scopeid="+scopeid;
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				scopeownerunitid = this.frowset.getString("owner_unit");
				if(scopeownerunitid.indexOf("UM")!=-1||scopeownerunitid.indexOf("UN")!=-1)
					codeFlag = scopeownerunitid.substring(2,scopeownerunitid.length());
			}
		}
		}
		ArrayList reportYearList = new ArrayList();        //年集合
		ArrayList reportCounitidList = new ArrayList();    //次数集合
		ArrayList reportWeekList=new ArrayList();
		
		ReportPDBAnalyse rpda = new ReportPDBAnalyse(this.getFrameconn());
		if(scopeid!=null||!"".equals(scopeid))
		rpda.setScopeid(scopeid);
		/**
		 * 周归挡表当变换月份时,执行特殊操作
		 */
		if(map.get("opt")!=null&& "count".equals((String)map.get("opt")))
		{
			rpda.setCountid((String)this.getFormHM().get("reportCount"));
			rpda.setYearid((String)this.getFormHM().get("reportYearid"));
			map.remove("opt");
		}
		if(map.get("opt")!=null&& "year".equals((String)map.get("opt")))
		{
			rpda.setYearid((String)this.getFormHM().get("reportYearid"));
			map.remove("opt");
		}
		
	
		TnameBo	tbo=new TnameBo(this.getFrameconn(),tabid,userView.getUserId(),userView.getUserName(),"temp");
		rpda.changeReportTabid(tabid,codeFlag,userView.getUserId(),userView.getUserName(),tbo);
		String	columnflag=rpda.tableColumnChange(tabid, userView.getUserId(),userView.getUserName(),tbo);
		
		reportYearList = rpda.getReportYearidList();
		reportCounitidList = rpda.getReportCountidList();
		reportWeekList=rpda.getReportWeekList();
	
		this.getFormHM().put("reportYearid",rpda.getYearid());
		this.getFormHM().put("reportCount",rpda.getCountid());
		this.getFormHM().put("columnflag" ,columnflag);
		this.getFormHM().put("reportTypes",rpda.getReportTypes());
		this.getFormHM().put("reportYearList" , reportYearList);
		this.getFormHM().put("reportCounitidList" ,reportCounitidList);
		this.getFormHM().put("reportWeekList",reportWeekList);
		this.getFormHM().put("reportCountInfo" , rpda.getReportCountInfo());
		this.getFormHM().put("reportHtml" ,rpda.getReportHtml());
		
		String reportState = rpda.getReportState();
		this.getFormHM().put("reportState",reportState);
		this.getFormHM().put("editOrreport", editOrreport);
	
		
		//存在归档数据
		if("null".equals(reportState)){
			ArrayList list = rpda.getChartDBList();
			this.getFormHM().put("list",list);
			this.getFormHM().put("chartTitle" ,rpda.getReportGridTitle());
			this.getFormHM().put("chartFlag" ,"yes");
		}else{
			this.getFormHM().put("chartFlag" ,"no");
		}
		this.getFormHM().put("reportTabid",tabid);
		this.getFormHM().put("reportchangeTabid",tabid);
		this.getFormHM().put("codeFlag",codeFlag);
		if(scopeid!=null||!"".equals(scopeid)){
//		获取当前用户的对应的填报单位编号
		if("1".equals(this.getFormHM().get("use_scope_cond"))){
			ArrayList reportList = new ArrayList();
			reportList=getReport();
			this.getFormHM().put("reportList", reportList);
			this.getFormHM().put("scopeid", scopeid);
			this.getFormHM().put("scopelist", getScopeList(scopeid));
		}
		
		
		}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
public ArrayList getReport(){
	StringBuffer sql = new StringBuffer();
	String sortId = (String) (this.getFormHM().get("sid")); //报表类别
	ArrayList list = new ArrayList();
	ContentDAO dao = new ContentDAO(this.getFrameconn());
	String unitcode="";
	sql.append("select reporttypes,report,unitcode from tt_organization where unitcode = (select unitcode from operuser where username = '");
	sql.append(userView.getUserName());
	sql.append("')");
	String flag ="";
	String reportTypes = "";   //当前用户负责的报表类别
	String report="";
	String report2="";
	TnameBo tnamebo  = new TnameBo(this.getFrameconn());
	HashMap scopeMap = tnamebo.getScopeMap();
	try {
		this.frowset = dao.search(sql.toString());
		if (this.frowset.next()) {
			reportTypes = (String) this.frowset.getString("reporttypes");
			report2=Sql_switcher.readMemo(this.frowset,"report");
			unitcode=this.frowset.getString("unitcode");
			if (reportTypes == null || "".equals(reportTypes)) {
				// 用户没有权限操作任何报表
//				Exception e = new Exception(ResourceFactory.getProperty("report.usernotreport"));
//				throw GeneralExceptionHandler.Handle(e);
			} else {
				if (reportTypes.charAt(reportTypes.length() - 1) == ',') {
					reportTypes = reportTypes.substring(0, reportTypes.length() - 1);
				}

			}
			if(reportTypes.length()>0){
			sql.delete(0, sql.length());
				sql.append("select tname.tabid ,tname.name,treport_ctrl.status from tname,treport_ctrl where  tname.tabid=treport_ctrl.tabid and  tname.tsortid in ("
						+ reportTypes + ")");
			sql.append(" and treport_ctrl.unitcode='"+unitcode+"'");
			if(report2!=null){
			report2 = report2.replace(",,", ",");
			if (report2.length()>0&&report2.charAt(report2.length() - 1) == ',') {
				report2 = report2.substring(0, report2.length() - 1);
			}
			if (report2.length()>0&&report2.charAt(0) == ',') {
				report2 = report2.substring(1, report2.length());
			}
			}
			if(report2.trim().length()>0){
			sql.append(" and tname.tabid not in("+report2+")");
			}
			sql.append(" order by tname.tabid");
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next()){
				report +=","+this.frowset.getString("tabid");
			}
			}

		} else {
				//从资源里找
//			if(sortId.equals("-1")){
			sql.delete(0, sql.length());
			sql.append("select TSortId sortid,Name sortname from TSort");
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next())
    		{
    			reportTypes+=this.frowset.getString("sortid")+",";
    		}
			if (reportTypes.charAt(reportTypes.length() - 1) == ',') {
				reportTypes = reportTypes.substring(0, reportTypes.length() - 1);
			}
//			}
//			SysPrivBo privbo=null;
//			if(userView.getStatus()==4) //自助用户关联业务用户
//			{
//				privbo=new SysPrivBo(userView.getDbname()+""+userView.getUserId(),"4",this.getFrameconn(),"warnpriv");
//			}else{
//				privbo=new SysPrivBo(userView.getUserName(),"0",this.getFrameconn(),"warnpriv");
//			}
//			String res_str=privbo.getWarn_str();
//			 if(res_str!=null&&res_str.indexOf("<Report>")!=-1){
//				 report =  res_str.substring(res_str.indexOf("<Report>")+8,res_str.indexOf("</Report>"));
//			 }
//			ResourceParser parser=new ResourceParser(res_str,1);
//			/**1,2,3*/
//			String str_content=","+parser.getContent()+",";
			sql.delete(0, sql.length());
			sql.append("select tabid  from tname");
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next())
    		{
			
			if(userView.isHaveResource(IResourceConstant.REPORT,this.frowset.getString("tabid")))
			{
				report+=	this.frowset.getString("tabid")+",";
			}
    		}
			flag="1";
			}
		String sqls = "";
		if("".equals(report)&&this.userView.isSuper_admin())
		sqls=	"select tabid,name,paper,tsortid  from tname";
		else{
			report = report.replace(" ", "");
			report = report.replace("R", "");
			while(report.indexOf(",,")!=-1){
				report = report.replace(",,", ",");
			}
			if (report.length()>0&&report.charAt(report.length() - 1) == ',') {
				report = report.substring(0, report.length() - 1);
			}
			if (report.length()>0&&report.charAt(0) == ',') {
				report = report.substring(1, report.length());
			}
			
		sqls=	"select tabid,name,paper,tsortid  from tname where tabid in ("+report+")";
		}
		this.frowset = dao.search(sqls);
		while(this.frowset.next())
		{
			if(scopeMap!=null&&"1".equals(scopeMap.get(this.frowset.getString("tabid")))){
			CommonData vo=new CommonData(this.frowset.getString("tabid"),"("+this.frowset.getString("tabid")+")"+this.frowset.getString("name"));
			if(this.userView==null)
				list.add(vo);
			else if(this.userView!=null&&userView.isHaveResource(com.hrms.hjsj.sys.IResourceConstant.REPORT ,this.frowset.getString("tabid")))
			{
				list.add(vo);
			}
			}
			
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	return list;
}
public ArrayList getScopeList(String scopeid){
	ArrayList scopelist = new ArrayList();
	CommonData data=new CommonData("","");
	//scopelist.add(data);
	//如果用户没有定义操作单位则按管理范围来匹配。
	ContentDAO dao=new ContentDAO(this.getFrameconn());
	ArrayList list = new ArrayList();
	StringBuffer  scopeidstr = new StringBuffer();
	try {
		this.frowset = dao.search("select * from tscope ");
		while(this.frowset.next()){
			list.add(this.frowset.getString("scopeid"));
	
		}
		for(int a=0;a<list.size();a++){
			String scopeid2 = (String)list.get(a);
			StringBuffer str = new StringBuffer(" select * from tscope where scopeid ="+scopeid2+" ");
			String temps="";
			
			if (!userView.isSuper_admin())
			{
				String operOrg = userView.getUnit_id();// 操作单位
				StringBuffer tempstr = new StringBuffer();
				if (operOrg.length() > 2)
				{
					String[] temp = operOrg.split("`");
					for (int i = 0; i < temp.length; i++)
					{
						if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))|| "UM".equalsIgnoreCase(temp[i].substring(0, 2))){
							tempstr.append(" or  owner_unit like 'UM" + temp[i].substring(2) + "%'");
							tempstr.append(" or  owner_unit like 'UN" + temp[i].substring(2) + "%'");
						}
					}
					if(tempstr.length()>3){
						temps+=tempstr.toString().substring(3);
					}
				} else
				{	//走管理范围
					
					String code = "-1";
					if (userView.getManagePrivCodeValue() != null && userView.getManagePrivCodeValue().length() > 0)// 管理范围
					{
						code = userView.getManagePrivCodeValue();
						if (code!=null)
							{
							if(code.indexOf("UN")!=-1||code.indexOf("UM")!=-1){
								tempstr.append(" or  owner_unit like 'UM" + code.substring(2) + "%'");
								tempstr.append(" or  owner_unit like 'UN" + code.substring(2) + "%'");
							}else{
								tempstr.append(" or  owner_unit like 'UN" + code + "%'");
								tempstr.append(" or  owner_unit like 'UM" + code + "%'");
							}
							}else{
								tempstr.append("and  1=2");
							}
							}else{
								tempstr.append("and  1=2");
							}
					if(tempstr.length()>3){
						temps+=tempstr.toString().substring(3);
					}
					}
			
			if(temps.length()>0){
				str.append(" and ("+temps+")");
			}
			this.frowset = dao.search(str.toString());
			if(this.frowset.next()){
				scopeidstr.append(","+scopeid2);
			}
			}else{
				scopeidstr.append(","+scopeid2);
			}
		}
	//所属机构
	String sql = "";
	if(scopeidstr.toString().length()>0){
		sql = "select * from tscope  where scopeid in("+scopeidstr.substring(1)+") order by displayid";
	}else{
		sql = "select * from tscope where 1=2 order by displayid";
	}
		
		this.frowset = dao.search(sql);
		int count =0;
		while(this.frowset.next()){
			if(count==0){

				if("0".equals(scopeid)){
					scopeid=this.frowset.getString("scopeid");
				}
				 data=new CommonData(this.frowset.getString("scopeid"),this.frowset.getString("name"));
				scopelist.add(data);
			
			}else{
			 data=new CommonData(this.frowset.getString("scopeid"),this.frowset.getString("name"));
			scopelist.add(data);
			}
			count++;
		}

}
 catch (SQLException e) {
	e.printStackTrace();
}
	return scopelist;
}
}
