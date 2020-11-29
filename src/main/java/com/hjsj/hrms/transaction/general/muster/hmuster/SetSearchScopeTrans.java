package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hjsj.hrms.businessobject.general.muster.hmuster.HmusterBo;
import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.interfaces.general.HmusterXML;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SetSearchScopeTrans extends IBusiness {

	public SetSearchScopeTrans() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String tabID=(String)this.getFormHM().get("tabID");	
		
		String modelFlag=(String)this.getFormHM().get("modelFlag");
		
		WeekUtils weekutils = new WeekUtils();
		String startime=(String)this.getFormHM().get("startime");
		startime=startime!=null&&startime.trim().length()>0?startime:weekutils.firstDateOfMonth();
		String endtime=(String)this.getFormHM().get("endtime");
		Calendar calendar = Calendar.getInstance();
		int yearnum = calendar.get(Calendar.YEAR);
		int monthnum = calendar.get(Calendar.MONTH)+1;
		endtime=endtime!=null&&endtime.trim().length()>0?endtime:weekutils.lastMonthStr(yearnum, monthnum);
		HmusterBo hmusterBo=new HmusterBo(this.getFrameconn());
		hmusterBo.setModelFlag(modelFlag);
		/** 判断库中该表是否已有记录  */
		
		String temptable = this.userView.getUserName()+"_muster_"+tabID;
		if(temptable.indexOf("（")!=-1||temptable.indexOf("）")!=-1){
			temptable = "\""+temptable+"\"";
		}
		
		hmusterBo.setTemptable(temptable);
		String   isRecords=hmusterBo.isTable(temptable);
		/** 判断是否有用户结果表*/
		String   infor_flag=(String)this.getFormHM().get("infor_Flag");
		String   dbpre=(String)this.getFormHM().get("dbpre");
		String isResultTable="0";
		boolean sysResult = (this.getUserView().getStatus()!=0)||"5".equals(infor_flag)/*基准岗位*/;
		if(!sysResult)
		{
    		String   tempTable="";
	    	if("1".equals(infor_flag)){
	    		tempTable=this.userView.getUserName()+dbpre+"Result";
			
	    	}
	    	else if("2".equals(infor_flag)){
	    		tempTable=this.userView.getUserName()+"BResult";
	    	}
    		else if("3".equals(infor_flag)){
	    		tempTable=this.userView.getUserName()+"KResult";
    		}
	    	if(tempTable.indexOf("（")!=-1||tempTable.indexOf("）")!=-1){
	    		tempTable = "\""+tempTable+"\"";
	    	}
	    	if("1".equals(infor_flag)&&"ALL".equals(dbpre))
	    	    isResultTable="1";
	    	else
	    	    isResultTable=hmusterBo.isTable(tempTable);
		}else
		{
			isResultTable=hmusterBo.isTable("t_sys_result");
		}
		
		/** 判断高级花名册指标中是否有子集指标及子集指标中是否有年月标识  */
		String flag=hmusterBo.isSubClass(tabID);
		/** 得到可按部分历史纪录查询所选的子标集列表 */
		ArrayList list=hmusterBo.getSubClassList(tabID);
		
		//依据用户权限判断是否要显示查询结果单选按钮
		int searchResultNumber = userView.getStatus();
		/**自助用户也可以用查询结果 2010-04-08*/
		/*if(searchResultNumber == 0){*/
			this.getFormHM().put("srnflag","yes");
		/*}else{
			this.getFormHM().put("srnflag","no");
		}*/
		String checkflag = (String)hm.get("checkflag");
		checkflag=checkflag!=null?checkflag:"";
		hm.remove("checkflag");
		
		this.getFormHM().put("dbpre",dbpre);
		this.getFormHM().put("flag",flag);
		this.getFormHM().put("subPointList",list);
		this.getFormHM().put("isRecords",isRecords);
		this.getFormHM().put("isResultTable",isResultTable);
		this.getFormHM().put("checkflag",checkflag);
		this.getFormHM().put("tabID",tabID);
		this.getFormHM().put("inforkind",infor_flag);
		this.getFormHM().put("modelFlag",modelFlag);
		this.getFormHM().put("startime",startime);
		this.getFormHM().put("endtime",endtime);
		this.getFormHM().put("isReData", "0");
		HmusterXML hmxml = new HmusterXML(this.getFrameconn(),tabID);
		String NO_MANAGE_PRIV=hmxml.getValue(HmusterXML.NO_MANAGE_PRIV);
		this.getFormHM().put("no_manager_priv",NO_MANAGE_PRIV);
		String column=hmxml.getValue(HmusterXML.COLUMN);
		String hz=hmxml.getValue(HmusterXML.HZ);
		if("1".equals(column))
			column = hz;
		String dataarea = hmxml.getValue(HmusterXML.DATAAREA);
		dataarea=dataarea!=null&&dataarea.trim().length()>0?dataarea:"0";
		
		this.getFormHM().put("column",column);
		this.getFormHM().put("dataarea",dataarea);
		String pix=hmxml.getValue(HmusterXML.PIX);
		this.getFormHM().put("pix",pix);
		String columnLine=hmxml.getValue(HmusterXML.COLUMNLINE);
		this.getFormHM().put("columnLine",columnLine);
		String groupPoint=hmxml.getValue(HmusterXML.GROUPFIELD);
		this.getFormHM().put("groupPoint",groupPoint);
		String groupPoint2=hmxml.getValue(HmusterXML.GROUPFIELD2);
		this.getFormHM().put("groupPoint2", groupPoint2);
		String multigroups=hmxml.getValue(HmusterXML.MULTIGROUPS);
		this.getFormHM().put("multigroups",multigroups);
		String layerid=hmxml.getValue(HmusterXML.GROUPLAYER);
		layerid=layerid!=null?layerid:"";
		this.getFormHM().put("layerid",layerid);
		String layerid2=hmxml.getValue(HmusterXML.GROUPLAYER2);
		layerid2=layerid2==null?"":layerid2;
		this.getFormHM().put("layerid2", layerid2);
		
		String codesetid = "UN";
		if(groupPoint!=null&& "E0122".equalsIgnoreCase(groupPoint))
			codesetid="UM";
		String codesetid2 = "UN";
		if(groupPoint2!=null&& "E0122".equalsIgnoreCase(groupPoint2))
			codesetid2="UM";
		if("21".equals(modelFlag)&&(groupPoint==null|| groupPoint.length()==0))
			this.getFormHM().put("layerlist",hmusterBo.getGroupLayer("B0110",""));
		else
			this.getFormHM().put("layerlist",hmusterBo.getGroupLayer(groupPoint,codesetid));
		if("21".equals(modelFlag)&&(groupPoint2==null|| groupPoint2.length()==0))
			this.getFormHM().put("layerlist2",hmusterBo.getGroupLayer("B0110",""));
		else
			this.getFormHM().put("layerlist2",hmusterBo.getGroupLayer(groupPoint2,codesetid2));
		String emptyRow=hmxml.getNgrid();
		this.getFormHM().put("emptyRow",emptyRow);
		
		String sortitem=hmxml.getValue(HmusterXML.SORTSTR);
		sortitem=sortitem!=null?sortitem:"";
		if(!"".equals(sortitem))
		{
			if(!sortitem.endsWith("`"))
			{
				sortitem+="`";
			}
		}
		this.getFormHM().put("sortitem",sortitem);

		String isGroupPoint="0";
		if(groupPoint!=null&&groupPoint.trim().length()>0){
			isGroupPoint="1";
		}
		String isGroupPoint2="0";
		if(groupPoint2!=null&&groupPoint2.trim().length()>0)
			isGroupPoint2="1";
		this.getFormHM().put("isGroupPoint",isGroupPoint);
		this.getFormHM().put("isGroupPoint2", isGroupPoint2);
		this.getFormHM().put("countflag","0");
		
		if("5".equals(modelFlag)){
			String relatTableid = (String)this.getFormHM().get("relatTableid");
			ArrayList groupPointList=getGroupPointList(relatTableid);
			this.getFormHM().put("groupPointList",groupPointList);
			this.getFormHM().put("relatTableid",relatTableid);
		}else if("81".equals(modelFlag)){
//			String relatTableid = (String)this.getFormHM().get("relatTableid");
			ArrayList groupPointList=getGroupPointList81(tabID);
			this.getFormHM().put("groupPointList",groupPointList);
			this.getFormHM().put("history", "2");
		}
		ArrayList combineList = this.getCombineList();
		this.getFormHM().put("combineFieldList", combineList);
		String combineField=(String)this.getFormHM().get("combineField");
		if(combineField==null|| "".equals(combineField))
			combineField="1";//不汇总
		this.getFormHM().put("combineField", combineField);
		
	}
	
	/**
	 * 取得高级花名册中分组指标列表 ( 主集里的代码型指标;对人员信息，单位，职位是硬编码;对单位，B0110硬编码;对职位，E01A1硬编码)
	 * 
	 * @param inforkind
	 * @author dengc
	 * @return ArrayList created: 2006/03/21
	 */

	public ArrayList getGroupPointList(String tabid)
			throws GeneralException {
		ArrayList itemlist = new ArrayList();
	
		CommonData dataobj = new CommonData("B0110_1","B0110_1:"+ResourceFactory.getProperty("kjg.title.unit"));
		itemlist.add(dataobj);
		
		dataobj = new CommonData("E0122_1","E0122_1:"+ResourceFactory.getProperty("columns.archive.um"));
		itemlist.add(dataobj);
		StringBuffer buf = new StringBuffer();
		buf.append("select Field_Name,Field_hz,ChgState from Template_Set where ");
		buf.append("CodeId<>'0' and CodeId is not null and CodeId<>'' and TabId='");
		buf.append(tabid);
		buf.append("' and (not (field_name in ('B0110', 'E0122'))) ORDER BY nSort");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset=dao.search(buf.toString());
			while(this.frowset.next()){
				String Field_hz = this.frowset.getString("Field_hz");
				String Field_Name = this.frowset.getString("Field_Name");
				FieldItem fielditem = DataDictionary.getFieldItem(Field_Name);
				if(fielditem==null)
					continue;
				String ChgState = this.frowset.getString("ChgState");
				ChgState=ChgState!=null?ChgState:"";
				if("1".equals(ChgState)){
					dataobj = new CommonData(Field_Name+"_1",Field_Name+"_1:"+Field_hz);
					itemlist.add(dataobj);
				}else if("2".equals(ChgState)){
					dataobj = new CommonData(Field_Name+"_2",Field_Name+"_2:"+Field_hz);
					itemlist.add(dataobj);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return itemlist;
	}

	/**
	 * 取得高级花名册中分组指标列表 ( 主集里的代码型指标;对人员信息，单位，职位是硬编码;对单位，B0110硬编码;对职位，E01A1硬编码)
	 * 
	 * @param inforkind
	 * @author dengc
	 * @return ArrayList created: 2006/03/21
	 */

	public ArrayList getGroupPointList81(String tabid)
			throws GeneralException {
		ArrayList itemlist = new ArrayList();
	
		CommonData dataobj = null;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String fieldsetid=getFieldSetids(dao,tabid);
		
		StringBuffer buf = new StringBuffer();
		buf.append("select ItemId,itemdesc from t_hr_busifield where ");
		buf.append("CODESETID<>'0' AND UPPER(FIELDSETID)='");
		buf.append("Q03");
		buf.append("' AND (USEFLAG='1' OR USEFLAG='2')  ORDER BY DISPLAYID");
		
		try {
			this.frowset=dao.search(buf.toString());
			while(this.frowset.next()){
				String ItemId = this.frowset.getString("ItemId");
				if("Q07".equalsIgnoreCase(fieldsetid)|| "Q09".equalsIgnoreCase(fieldsetid))
				{
					if("state".equalsIgnoreCase(ItemId)|| "A0100".equalsIgnoreCase(ItemId)|| "NBASE".equalsIgnoreCase(ItemId)||
							"E0122".equalsIgnoreCase(ItemId)|| "E01A1".equalsIgnoreCase(ItemId)|| "Q03Z3".equalsIgnoreCase(ItemId)|| "Q03Z5".equalsIgnoreCase(ItemId))
						continue;
				}
				String itemdesc = this.frowset.getString("itemdesc");
				dataobj = new CommonData(ItemId,ItemId+":"+itemdesc);
				itemlist.add(dataobj);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemlist;
	}
	private String getFieldSetids(ContentDAO dao,String tabid){
		String fieldsetid="";
		try {
			this.frowset=dao.search("select SetName,Field_Name from muster_cell where Tabid='"+tabid+"'");
			while(this.frowset.next()){
				String setname = this.frowset.getString("SetName");
				/*String Field_Name = this.frowset.getString("Field_Name");
				if(setname!=null&&setname.trim().length()>0){
					if(Field_Name!=null&&Field_Name.trim().length()>0){
						if(Field_Name.indexOf(setname)!=-1){
							fieldsetid=setname;
							break;
						}else{
							if(setname.substring(0,1).equalsIgnoreCase(Field_Name.substring(0,1))){
								fieldsetid=Field_Name.substring(0,3);
								break;
							}
						}
					}
				}*/
				if(setname!=null&&setname.trim().length()>0)
				{
					fieldsetid=setname;
					break;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fieldsetid;
	}
	private ArrayList getCombineList()
	{
		ArrayList list = new ArrayList();
		list.add(new CommonData("0","不汇总"));
		list.add(new CommonData("1","按人员，报税时间和计税方式汇总"));
		return list;
	}


}
