package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hjsj.hrms.businessobject.performance.commend.CommendSetBo;
import com.hjsj.hrms.businessobject.performance.commend.CommendXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class EveryYearAnalyseVoteTrans extends IBusiness{
	public void execute() throws GeneralException{
		try{
			ArrayList list = new ArrayList();	
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String type=(String)map.get("type");
			String um="aaaaa";
			String cYear="aaaaa";
			if("2".equals(type))
			{
				um=(String)map.get("um");
				cYear = (String)map.get("year");
			}
			CommendSetBo bo = new CommendSetBo(this.getFrameconn());
			HashMap commendFieldMap=getCommendField();
		    HashMap commendFieldCodesetidMap=getCommendFieldCodesetid(commendFieldMap);
		  
			list=bo.getEveryYearAnalyseVoteList(commendFieldMap,commendFieldCodesetidMap,type,um,cYear);
		    ArrayList umList = bo.getUMList();
		    ArrayList yearlist=bo.getAllYearList();
		    this.getFormHM().put("analyseVoteList",list);
		    this.getFormHM().put("umList",umList);
		    this.getFormHM().put("um",um);
		    this.getFormHM().put("year",cYear);
		    this.getFormHM().put("yearList", yearlist);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 取得每个推荐记录的推荐职务指标
	 * @return
	 */
	public HashMap getCommendField()
	{
		HashMap map = new HashMap();
		StringBuffer sb= new StringBuffer();
		CommendXMLBo bo=new CommendXMLBo(this.getFrameconn());
		String sql = "select p0201 from p02 where p02.p0209='06' order by p02.p0201";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				String p0201=this.frowset.getString("p0201");
				String commend_field=bo.getCtrl_paraValue(p0201,CommendXMLBo.commend_field);
				map.put(p0201,commend_field);
				sb.append(","+p0201);
			}
			if(sb.toString().indexOf(",") !=-1)
			     map.put("p0201_str",sb.toString().substring(1));
			else
				map.put("p0201_str","");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 取得每个推荐记录的推荐职务指标的codesetid
	 * @return
	 */
	public HashMap getCommendFieldCodesetid(HashMap hm)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select p0201 from p02 where p02.p0209='06' order by p02.p0201";
			ArrayList list = DataDictionary.getFieldList("P03",Constant.USED_FIELD_SET);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				String p0201=this.frowset.getString("p0201");
				for(int i=0;i<list.size();i++)
				{
					FieldItem item = (FieldItem) list.get(i);
					if(item.getItemid().equalsIgnoreCase((String)hm.get(p0201)))
					{
						map.put(p0201,item.getCodesetid());
						break;
					}/** if end */
				}/** for i end */
			}/**while end*/	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	
}
