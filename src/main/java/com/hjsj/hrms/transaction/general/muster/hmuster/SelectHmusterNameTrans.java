package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

public class SelectHmusterNameTrans extends IBusiness {

	public SelectHmusterNameTrans() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String  nFlag=(String)hm.get("nFlag");
			nFlag=nFlag!=null&&nFlag.trim().length()>0?nFlag:"";
			this.getFormHM().put("modelFlag",nFlag);
			ArrayList groupPointList=new ArrayList();
			ArrayList hmusterlist=new ArrayList();
			if("2".equals(nFlag)|| "3".equals(nFlag)|| "21".equals(nFlag)|| "41".equals(nFlag)){
				/**未定义信息类别,默认为人员信息*/
				String infor_kind=(String)hm.get("a_inforkind");
				if(infor_kind==null|| "".equals(infor_kind))
					infor_kind="1";
				this.getFormHM().put("inforkind",infor_kind);
				/**权限范围内的人员库列表*/
				ArrayList dblist=this.userView.getPrivDbList();
				DbNameBo dbvo=new DbNameBo(this.getFrameconn());
				dblist=dbvo.getDbNameVoList(dblist);
				ArrayList list=new ArrayList();
				for(int i=0;i<dblist.size();i++){
					CommonData vo=new CommonData();
					RecordVo dbname=(RecordVo)dblist.get(i);
					vo.setDataName(dbname.getString("dbname"));
					vo.setDataValue(dbname.getString("pre"));
					list.add(vo);
				}
				this.getFormHM().put("dblist",list);
				
				/**花名册列表*/			
				ArrayList a_hmusterlist=getMusterList(nFlag);					
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
	
				/** 取得高级花名册中分组指标列表 */
				
				groupPointList=getHmusterGroupPointList(infor_kind);
				
			}else if("81".equals(nFlag)|| "5".equals(nFlag)){
				String relatTableid=(String)hm.get("relatTableid");
				String condition=(String)this.getFormHM().get("condition");
//				condition=condition.replaceAll("%20"," ");在连接地址后面传的时候，那个空格，有时候会变成20%
				condition = PubFunc.keyWord_reback(condition);
	            condition = condition!=null&&condition.trim().length()>0?SafeCode.decode(condition):"";
	            condition = PubFunc.keyWord_reback(condition);
	            
				String returnURL=(String)this.getFormHM().get("returnURL");
				returnURL = PubFunc.hireKeyWord_filter_reback(returnURL);//还原字符串
				hmusterlist=getKQ_GZMusterList(relatTableid,nFlag);
				this.getFormHM().put("relatTableid",relatTableid);
				this.getFormHM().put("condition",condition);
				this.getFormHM().put("returnURL",returnURL);
				this.getFormHM().put("inforkind","81");

			}
			
			this.getFormHM().put("groupPointList",groupPointList);
			this.getFormHM().put("hmusterlist",hmusterlist);
			
			/**  由于form属性为session，所以清空属性值    **/		
			this.getFormHM().put("clears","1");			
			System.gc();
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
		  throw GeneralExceptionHandler.Handle(ex);
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
	
	
	
	/**
	 * 取得所有花名册列表
	 * 
	 * @param nFlag
	 * @return
	 */
	public ArrayList getMusterList(String nModule) throws GeneralException {
		ArrayList list = new ArrayList();
		StringBuffer strsql = new StringBuffer();

		strsql.append("select tabid,cname from muster_name where  nModule=");
		strsql.append(nModule);
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
