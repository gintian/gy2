package com.hjsj.hrms.transaction.general.inform.emp.output;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
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
public class HistoryHrosterTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm = (HashMap)this.getFormHM().get("requestPamaHM");
		String dbname = (String)reqhm.get("dbname");
		dbname=dbname!=null&&dbname.trim().length()>0?dbname:"";
		reqhm.remove("dbname");
		this.getFormHM().put("dbpre",dbname);
		
		String inforkind = (String)reqhm.get("infor");
		inforkind=inforkind!=null&&inforkind.trim().length()>0?inforkind:"1";
		reqhm.remove("infor");
		this.getFormHM().put("infor_Flag",inforkind);
		
		ArrayList hmusterlist=new ArrayList();
		ArrayList a_hmusterlist=getMusterList(inforkind);					
		for(int i=0;i<a_hmusterlist.size();i++)
		{
			CommonData vo=new CommonData();
			String[] temp=(String[])a_hmusterlist.get(i);		
			if(!this.getUserView().isHaveResource(IResourceConstant.HIGHMUSTER,temp[0]))
				continue;
			vo.setDataName(temp[0]+"."+temp[1]);				
			vo.setDataValue(temp[0]);
			hmusterlist.add(vo);
		}
		ArrayList groupPointList=getHmusterGroupPointList(inforkind);
		this.getFormHM().put("groupPointList",groupPointList);
		this.getFormHM().put("hmusterlist",hmusterlist);
		this.getFormHM().put("modelFlag","3");
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
}
