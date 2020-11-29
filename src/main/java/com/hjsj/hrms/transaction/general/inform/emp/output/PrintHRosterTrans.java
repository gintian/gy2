package com.hjsj.hrms.transaction.general.inform.emp.output;

import com.hjsj.hrms.businessobject.ht.inform.ContracInforBo;
import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class PrintHRosterTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		String dbname = (String)this.getFormHM().get("dbname");
		dbname=dbname!=null&&dbname.trim().length()>0?dbname:"";

		String a_code = (String)this.getFormHM().get("a_code");
		a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
		a_code= "all".equalsIgnoreCase(a_code)?"":a_code;
		
		String inforkind = (String)this.getFormHM().get("inforkind");
		inforkind=inforkind!=null&&inforkind.trim().length()>0?inforkind:"1";

		String result = (String)this.getFormHM().get("flag");
		result=result!=null&&result.trim().length()>0?result:"0";
		
		String ctflag = (String)this.getFormHM().get("ctflag");
		ctflag=ctflag!=null&&ctflag.trim().length()>0?ctflag:"";
		
		if(!"2".equals(result)){
			updateResult(dao,dbname,a_code,result,inforkind,ctflag);
		}

	}
	/**
	 * 取得考勤高级花名册信息列表
	 * @param relatTabid
	 * @return
	 */
	public ArrayList getKQ_GZMusterList(String relatTabid,String nFlag)
	{
		ArrayList kq_musterList=new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			if("81".equals(nFlag))
				this.frowset=dao.search("select tabid,cname from muster_name where nModule=81 and nPrint="+relatTabid);
			else if("5".equals(nFlag))
				this.frowset=dao.search("select tabid,cname from muster_name where nModule=5 and nPrint="+relatTabid);
			while (this.frowset.next()) {
				if(!this.getUserView().isHaveResource(IResourceConstant.HIGHMUSTER,this.frowset.getString("tabid")))
					continue;
				CommonData vo=new CommonData();
				vo.setDataName(this.frowset.getString("cname"));				
				vo.setDataValue(this.frowset.getString("tabid"));
				kq_musterList.add(vo);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return kq_musterList;
	}
	private void updateResult(ContentDAO dao,String dbname,String a_code,String result,
			String infor,String ctflag){
		String tablename="";
		String itemid="";
		if("1".equals(infor)){
			tablename=dbname+"A01";
			itemid="A0100";
		}else if("2".equals(infor)){
			tablename="B01";
			dbname="b";
			itemid="B0110";
		}else if("3".equals(infor)){
			tablename="K01";
			itemid="E01A1";
			dbname="k";
		}else{
			tablename=dbname+"A01";
			itemid="A0100";
		}
		
		StringBuffer sqlstr = new StringBuffer("select "+itemid+" from ");
		sqlstr.append(tablename+" where ");
		GzDataMaintBo gzbo = new GzDataMaintBo(this.frameconn,this.userView);
		sqlstr.append(gzbo.whereStra_Code(a_code,infor));
		try {

			if("1".equals(result)){
				sqlstr.append(" and "+itemid+" in (select "+itemid+" from ");
				sqlstr.append(this.userView.getUserName()+dbname+"result)");
			}
			if(ctflag.trim().length()>0){
				ConstantXml csxml = new ConstantXml(this.frameconn,"HT_PARAM","Params");
				String htmain = csxml.getTextValue("/Params/htmain");
				htmain=htmain!=null&&htmain.trim().length()>0?htmain:"";
				if(htmain.trim().length()<1){
					htmain = csxml.getConstantValue("HETONGMAIN");
					htmain=htmain!=null&&htmain.trim().length()>0?htmain:"";
				}
				ContracInforBo ctbo = new ContracInforBo(this.frameconn,csxml);
				String htmainflagid = ctbo.getHtmainFlagID(htmain);
				if(htmainflagid!=null&&htmainflagid.length()>0&&!"all".equalsIgnoreCase(ctflag)){
					if("no".equalsIgnoreCase(ctflag)){
						sqlstr.append(" and A0100 in(select A0100 from ");
						sqlstr.append(dbname+htmain);
						sqlstr.append(" where ");
						sqlstr.append(htmainflagid);
						sqlstr.append("='' or ");
						sqlstr.append(htmainflagid);
						sqlstr.append(" is null");
						sqlstr.append(") ");
					}else{
						sqlstr.append(" and A0100 in(select A0100 from ");
						sqlstr.append(dbname+htmain);
						sqlstr.append(" where ");
						sqlstr.append(htmainflagid);
						sqlstr.append("='");
						sqlstr.append(ctflag);
						sqlstr.append("'");
						sqlstr.append(") ");
					}
				}
			}else
			{
				/*****sunxin 打印输出登记表，当前显示，结果兼职人员没有出不，不对 0018020******/
				String code="";
				if(a_code!=null&&a_code.length()>0)
					code=a_code.substring(2);
				/**加入兼职条件*/
				sqlstr.append(this.getPartwhere(dbname, code, this.getFrameconn(),this.userView,result));
			}
			ArrayList recodlist = new ArrayList();
			this.frowset=dao.search(sqlstr.toString());
			while(this.frowset.next()){
				ArrayList list = new ArrayList();
				list.add(this.frowset.getString(itemid));
				recodlist.add(list);
			}
			dao.update("delete from "+this.getUserView().getUserName()+dbname+"result");
			String addsql = "insert into "+this.getUserView().getUserName()+dbname+"result("+itemid+") values(?)";
			dao.batchInsert(addsql,recodlist);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 取得所有花名册列表
	 * 
	 * @param inforkind
	 * @return
	 */
	public ArrayList getMusterList(String inforkind) throws GeneralException {
		ArrayList list = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		int nModule = 0;
		if ("1".equals(inforkind)) // 人员库
		{
			inforkind = "A";
			nModule = 3;
		} else if ("3".equals(inforkind)) // 职位库
		{
			inforkind = "K";
			nModule = 1;
		} else if ("2".equals(inforkind)) // 单位库
		{
			inforkind = "B";
			nModule = 2;

		}
		strsql.append("select tabid,cname from muster_name where flagA='");
		strsql.append(inforkind);
		strsql.append("'");
		if ("A".equals(inforkind))
			strsql.append(" and nModule=" + nModule);
		/* 此三条记录不予显示 */
		strsql.append(" and tabid!=1000 and tabid!=1010 and tabid!=1020");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet recset = null;
		try {
			recset = dao.search(strsql.toString());
			while (recset.next()) {
				String[] temp = new String[2];
				temp[0] = recset.getString("tabid");
				temp[1] = recset.getString("cname");
				list.add(temp);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}
	/**
	 * 取得高级花名册中分组指标列表 ( 主集里的代码型指标;对人员信息，单位，职位是硬编码;对单位，B0110硬编码;对职位，E01A1硬编码)
	 * 
	 * @param inforkind
	 * @author dengc
	 * @return ArrayList created: 2006/03/21
	 */

	public ArrayList getHmusterGroupPointList(String inforkind)
			throws GeneralException {

		ArrayList arrayList = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		ArrayList pointList = new ArrayList(); // 指标列表
		String mainSet = ""; // 主集
		if ("1".equals(inforkind)) // 人员库
		{
			mainSet = "A01";
			CommonData dataobj = new CommonData("B0110", ResourceFactory
					.getProperty("tree.unroot.undesc"));
			arrayList.add(dataobj);
			CommonData dataobj2 = new CommonData("E01A1", ResourceFactory
					.getProperty("tree.kkroot.kkdesc"));
			arrayList.add(dataobj2);
		} else if ("3".equals(inforkind)) // 职位库
		{
			mainSet = "K01";
			CommonData dataobj2 = new CommonData("E01A1", ResourceFactory
					.getProperty("tree.kkroot.kkdesc"));
			//
			CommonData dataobj = new CommonData("E0122", ResourceFactory
					.getProperty("column.sys.dept"));
			
			arrayList.add(dataobj2);
			arrayList.add(dataobj);
			
		} else if ("2".equals(inforkind)) // 单位库
		{
			mainSet = "B01";
			CommonData dataobj = new CommonData("B0110", ResourceFactory
					.getProperty("tree.unroot.undesc"));
			arrayList.add(dataobj);
		}

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet recset = null;
		try {
			strsql
					.append("select itemid,itemdesc from fielditem where fieldsetid='");
			strsql.append(mainSet);
			strsql
					.append("' and codesetid!='0'and useflag='1' order by  displayid ");
			recset = dao.search(strsql.toString());
			while (recset.next()) {
				CommonData dataobj = new CommonData(recset.getString("itemid"),
						recset.getString("itemdesc"));
				arrayList.add(dataobj);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
		return arrayList;

	}
	public String getPartwhere(String userbase,String code,Connection conn,UserView userview,String result)
    {
    	String part_setid="";
		String part_unit="";
		String appoint=" ";
		String flag="";
		//兼职处理
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		ArrayList list = new ArrayList();
		list.add("flag");
		list.add("unit");
		list.add("setid");
		list.add("appoint");
    	HashMap map = sysoth.getAttributeValues(Sys_Oth_Parameter.PART_TIME,list);
    	if(map!=null&& map.size()!=0){
			if(map.get("flag")!=null && ((String)map.get("flag")).trim().length()>0)
				flag=(String)map.get("flag");
			if(flag!=null&& "true".equalsIgnoreCase(flag))
			{
				if(map.get("unit")!=null && ((String)map.get("unit")).trim().length()>0)
					part_unit=(String)map.get("unit");
				if(map.get("setid")!=null && ((String)map.get("setid")).trim().length()>0)
					part_setid=(String)map.get("setid");
				if(map.get("appoint")!=null && ((String)map.get("appoint")).trim().length()>0)
					appoint=(String)map.get("appoint");
			}		
		}
    	StringBuffer union_Sql=new StringBuffer();
    	if(part_unit!=null&&part_unit.length()>0&&part_setid!=null&&part_setid.length()>0)
    	{
    		union_Sql.append(" or ( "+userbase+"A01.a0100 in(select a0100 from "+userbase+""+part_setid+" where "+part_unit+" like '"+code+"%'");
    		if(appoint!=null&&appoint.length()>0)
    			union_Sql.append(" and "+appoint+"='0' ");
    		if("1".equals(result)){
    			union_Sql.append(" and a0100 in (select a0100 from ");
    			union_Sql.append(userview.getUserName()+userbase+"result)");
    		}
    		union_Sql.append("))");
    	}
    	return union_Sql.toString();
    }
}
