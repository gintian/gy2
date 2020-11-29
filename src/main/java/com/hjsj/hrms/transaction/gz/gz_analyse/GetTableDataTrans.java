package com.hjsj.hrms.transaction.gz.gz_analyse;

import com.hjsj.hrms.businessobject.gz.gz_analyse.GzAnalyseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

public class GetTableDataTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			GzAnalyseBo bo = new GzAnalyseBo(this.getFrameconn(),this.getUserView());
			String opt=(String)this.getFormHM().get("opt");
			String rsid=(String)this.getFormHM().get("rsid");
			String rsdtlid=(String)this.getFormHM().get("rsdtlid");
			Calendar b=null;
			String year = (String)this.getFormHM().get("year");
			HashMap name_map = bo.getName(rsid, rsdtlid);
			String flitem="";
			String id = (String)this.getFormHM().get("id");
			if(id==null||id.length()==0){
				id="00";
			}
			bo.setId(id);
			if("1".equals(opt))
			{
				if("9".equals(rsid))
				{
					flitem = (String)this.getFormHM().get("flitem");
				}
				String rowflag = (String)this.getFormHM().get("rowflag");
				String visibleMonth=(String)this.getFormHM().get("visibleMonth");
				ArrayList headList = null;
				if("1".equals(rowflag))
				{
					
	     	    	headList = bo.getTableHeadlist(rsdtlid,rsid,flitem,visibleMonth);
				}
				else
					headList = bo.getTableHeadList5();
	     		this.getFormHM().put("tableHeadList",headList);
			}
			if("2".equals(opt))
			{
				String codesetid=(String)this.getFormHM().get("codesetid");
				String analyseProjectitem=(String)this.getFormHM().get("analyseProjectitem");
				String analyseFLitem = (String)this.getFormHM().get("analyseFLitem");
				String pre=(String)this.getFormHM().get("pre");
				String salaryid=(String)this.getFormHM().get("salaryid");
				String firstsalaryid=(String)this.getFormHM().get("firstsalaryid");
				String a0100=(String)this.getFormHM().get("a0100");
				String fielditemid=(String)this.getFormHM().get("fielditemid");
				String fielditemvalue=(String)this.getFormHM().get("fielditemvalue");
				String tree_codeitemid=(String)this.getFormHM().get("tree_codeitemid");
				String tree_codesetid=(String)this.getFormHM().get("tree_codesetid");
				String classFlag = (String)this.getFormHM().get("classFlag");
				String stopMonth = (String)this.getFormHM().get("stopMonth");
				String isAll=(String)this.getFormHM().get("isAll");
				String orderSql = (String)this.getFormHM().get("orderSql");
				String starttime=(String)this.getFormHM().get("starttime");
				String endtime = (String)this.getFormHM().get("endtime");
				String rowFlag = (String)this.getFormHM().get("rowflag");
				String statflag=(String)this.getFormHM().get("statflag");
				String month=(String)this.getFormHM().get("month");
				//------------------------zhaoxg 2013-5-6 增加按工资查询功能-----------------------
				String zxgcombox=(String)this.getFormHM().get("zxgcombox");
				String zxgfrom=(String)this.getFormHM().get("zxgfrom");
				String zxgto=(String)this.getFormHM().get("zxgto");
				String TotalFlagCheck = (String)this.getFormHM().get("TotalFlagCheck");
				String XiaJiFlagCheck = (String)this.getFormHM().get("XiaJiFlagCheck");
				if(zxgcombox==null){
					zxgcombox="";
				}
				if(zxgfrom==null){
					zxgfrom="";
				}
				if(zxgto==null){
					zxgto="";
				}
				/**加入权限控制*/
				String role=(String)this.getFormHM().get("role");
				String privCode=(String)this.getFormHM().get("privCode");
				String privCodeValue=(String)this.getFormHM().get("privCodeValue");
				String condid=(String)this.getFormHM().get("condid");
				/**=1按部门=2按单位*/
				String isShowUnitData=(String)this.getFormHM().get("isShowUnitData");
				if("7".equals(rsid)|| "11".equals(rsid)|| "16".equals(rsid))
				{
					String bgroup =bo.getBgroup(rsdtlid, rsid);
					this.getFormHM().put("bgroup",bgroup);
				}
				if("6".equals(rsid)|| "15".equals(rsid))
				{
					Vector JFCChartItemVector=bo.getJFCChartItemVector(rsdtlid);
					String chartTitle=year+"年度工资构成统计分析图"; 
					//Vector classFieldVector=bo.getClassFieldVector(salaryid);
					
					this.getFormHM().put("chartVector",JFCChartItemVector);
					this.getFormHM().put("chartTitle",chartTitle);
					
				}
				String level =(String)this.getFormHM().get("level");
				String visibleMonth=(String)this.getFormHM().get("visibleMonth");
				String archive=(String)this.getFormHM().get("archive");
				if(archive!=null&& "0".equals(archive))
					bo.setTableName("salaryarchive");
				bo.setSpFlag(archive);//是否包含审批过程的数据  3：包含  否则不包含 zhaoxg add 2013-12-5
				bo.createIndexArchive();
				ArrayList recordList=null;
                if("9".equalsIgnoreCase(rsid))
                {
                    recordList=bo.getRecordList9(Integer.parseInt(year), pre, "-1".equals(firstsalaryid)?salaryid:firstsalaryid, analyseProjectitem, analyseFLitem, codesetid,tree_codeitemid,tree_codesetid,role,privCode,privCodeValue,level,visibleMonth);
                    recordList=bo.sorckRecordList(recordList,codesetid);//对树形代码进行排序
                }
				else if("8".equals(rsid)|| "17".equals(rsid))
				{
					recordList=bo.getRecordList8(pre, salaryid, Integer.parseInt(year), analyseProjectitem,tree_codeitemid,tree_codesetid,role,privCode,privCodeValue);
				}
				else if("10".equals(rsid))
				{
					recordList=bo.getRecordList10(rsid, rsdtlid, Integer.parseInt(year), pre, salaryid, fielditemid, fielditemvalue, Integer.parseInt(stopMonth),isAll,role,privCode,privCodeValue,XiaJiFlagCheck);
				}
				else if("11".equalsIgnoreCase(rsid))
				{
					recordList=bo.getRecordList11(rsid, rsdtlid, pre, salaryid, Integer.parseInt(year), month, a0100, fielditemid, fielditemvalue, tree_codeitemid, tree_codesetid, classFlag, orderSql, starttime, endtime, statflag, role, privCode, privCodeValue,isShowUnitData);
				}
				else 
				{
					if("1".equals(rowFlag))
	    			    recordList = bo.getRecordList(rsid, rsdtlid, pre, salaryid,Integer.parseInt(year),a0100,fielditemid,fielditemvalue,tree_codeitemid,tree_codesetid,classFlag,orderSql,starttime,endtime,statflag,role,privCode,privCodeValue,condid,zxgcombox,zxgfrom,zxgto,TotalFlagCheck,XiaJiFlagCheck);
					else
						recordList = bo.getRecordList5ChangeClo_Row(rsid, rsdtlid, pre, salaryid, Integer.parseInt(year), a0100);
				}
				int recordNums = bo.getRecordNums();
				this.getFormHM().put("recordNums", recordNums+"");
				byte[] bytes =PubFunc.zipBytes_object(recordList);
				this.getFormHM().put("data_bytes",bytes);
				this.getFormHM().put("name",(String)name_map.get("name"));
			}
			if("3".equals(opt))
			{
				String archive=(String)this.getFormHM().get("archive");
				if(archive!=null&& "0".equals(archive))
					bo.setTableName("salaryarchive");
				bo.setSpFlag(archive);//是否包含审批过程的数据  3：包含  否则不包含 zhaoxg add 2013-12-5
				String privDb = (String)this.getFormHM().get("privDb");
				ArrayList list = /*bo.getDbList(privDbList)*/bo.getDbList3(privDb);
				this.getFormHM().put("preList",list);
			}
			if("4".equals(opt))
			{
				String pyear=(String)this.getFormHM().get("year");
				String pre = (String)this.getFormHM().get("pre");
				String role = (String)this.getFormHM().get("role");
				String privCode =(String)this.getFormHM().get("privCode");
				String privCodeValue  = (String)this.getFormHM().get("privCodeValue");
				String salaryid = (String)this.getFormHM().get("sid");
				String tree_codeitemid=(String)this.getFormHM().get("tree_codeitemid");
				String tree_codesetid=(String)this.getFormHM().get("tree_codesetid");
				String archive=(String)this.getFormHM().get("archive");
				if(archive!=null&& "0".equals(archive))
					bo.setTableName("salaryarchive");
				bo.setSpFlag(archive);//是否包含审批过程的数据  3：包含  否则不包含 zhaoxg add 2013-12-5
				bo.createIndexArchive();
				Vector vector=bo.getPersonList(pre, pyear, role, privCode, privCodeValue,salaryid,tree_codeitemid,tree_codesetid);
				this.getFormHM().put("person_data",vector);
			}
			if("5".equalsIgnoreCase(opt))
			{
				String salaryid=(String)this.getFormHM().get("salaryid");
				String fielditemid=(String)this.getFormHM().get("fielditemid");
				String fielditem=(String)this.getFormHM().get("classFielditem");	
				String role = (String)this.getFormHM().get("role");
				String privCode =(String)this.getFormHM().get("privCode");
				String privCodeValue  = (String)this.getFormHM().get("privCodeValue");
				Vector classFieldVector=bo.getClassFieldVector(salaryid);
			    
				if(fielditemid==null||fielditemid.length()==0)
				{
					fielditemid=(String)(((LazyDynaBean)classFieldVector.get(0)).get("value"));
				}
				if(fielditem==null||fielditem.length()==0)
				{
					fielditem=(String)(((LazyDynaBean)classFieldVector.get(0)).get("codesetid"));
				}
				String archive=(String)this.getFormHM().get("archive");
				if(archive!=null&& "0".equals(archive))
					bo.setTableName("salaryarchive");
				bo.setSpFlag(archive);//是否包含审批过程的数据  3：包含  否则不包含 zhaoxg add 2013-12-5
				bo.createIndexArchive();
				Vector classFieldItemVector = bo.getClassFielditemVector(fielditem,this.getUserView());
				String fielditemvalue=(String)(((LazyDynaBean)classFieldItemVector.get(0)).get("value"));
				this.getFormHM().put("classField", fielditemid);
				this.getFormHM().put("classFielditem", fielditem);
				this.getFormHM().put("classFieldItemVector",classFieldItemVector);
				this.getFormHM().put("classFieldVector",classFieldVector);
				this.getFormHM().put("fielditemvalue",fielditemvalue);
			}
			if("7".equals(opt))
			{
				String salaryid=(String)this.getFormHM().get("salaryid");
				String firstsalaryid=(String)this.getFormHM().get("firstsalaryid");
				String archive=(String)this.getFormHM().get("archive");
				if(archive!=null&& "0".equals(archive))
					bo.setTableName("salaryarchive");
				bo.setSpFlag(archive);//是否包含审批过程的数据  3：包含  否则不包含 zhaoxg add 2013-12-5
				bo.createIndexArchive();
				Vector classFieldVector=null;
				if("9".equals(rsid))
			    	classFieldVector=bo.getClassFieldVector("-1".equals(firstsalaryid)?salaryid:firstsalaryid);
				else
					classFieldVector=bo.getClassFieldVector(salaryid);
				String analyseFLitem=(String)(((LazyDynaBean)classFieldVector.get(0)).get("value"));
				String codesetid=(String)(((LazyDynaBean)classFieldVector.get(0)).get("codesetid"));
				Vector analyseProjectVector =null;
				if("9".equals(rsid))
					analyseProjectVector=bo.getAnalyseProjectVector("-1".equals(firstsalaryid)?salaryid:firstsalaryid);
				else
					analyseProjectVector=bo.getAnalyseProjectVector(salaryid);
				String analyseProjectitem="";
				if(analyseProjectVector.size()>0)
					analyseProjectitem=(String)(((LazyDynaBean)analyseProjectVector.get(0)).get("value"));
				this.getFormHM().put("classFieldVector",classFieldVector);
				this.getFormHM().put("codesetid",codesetid);
				this.getFormHM().put("analyseFLitem", analyseFLitem);
				this.getFormHM().put("analyseProjectVector", analyseProjectVector);
				this.getFormHM().put("analyseProjectitem", analyseProjectitem);
				if("9".equals(rsid))
				{
					Vector salarySetVector=bo.getSalarySetVector(salaryid);
					this.getFormHM().put("salarySetVector",salarySetVector);
				}
				
			}
			if("8".equals(opt))
			{
				Vector convector = bo.getCondVector(rsid);
				this.getFormHM().put("convector",convector);
			}
			if("9".equals(opt))
			{
				if("9".equals(rsid))
				{
					flitem = (String)this.getFormHM().get("flitem");
				}
				String rowflag = (String)this.getFormHM().get("rowflag");
				String visibleMonth=(String)this.getFormHM().get("visibleMonth");
				ArrayList headList = null;
				if("1".equals(rowflag))
				{
	     	    	headList = bo.getTableHeadlist(rsdtlid,rsid,flitem,visibleMonth);
				}
				Vector zxgvector = new Vector();
				LazyDynaBean bean = new LazyDynaBean();
				LazyDynaBean bean1 = null;
				bean.set("value", "");
				bean.set("name", "(空)");
				zxgvector.addElement(bean);
				for(int i=0;i<headList.size();i++){
					bean = new LazyDynaBean();
					bean1 = new LazyDynaBean();
					bean1 = (LazyDynaBean) headList.get(i);
					String itemdesc = ((String)bean1.get("itemdesc")).trim();
					String itemid = ((String)bean1.get("itemid")).trim();
					bean.set("value", itemid);
					bean.set("name", itemdesc);
					zxgvector.addElement(bean);
				}
				this.getFormHM().put("zxgvector",zxgvector);
			}
			if("10".equals(opt)){
				Vector zxgvector = new Vector();
				bo.initXML();
				zxgvector=bo.getFameVetor(this.getUserView().getUserName());
				this.getFormHM().put("zxgvector",zxgvector);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	

}
