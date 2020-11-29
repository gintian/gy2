package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GrossManagBo;
import com.hjsj.hrms.businessobject.gz.GrossPayManagement;
import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

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
public class StateStartTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		//String name=(String)hm.get("position_set_table");
		ArrayList list=(ArrayList)hm.get("position_set_record");
		GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),2);
		HashMap map =bo.getValuesMap();
		HashMap ma=bo.getMap();
		String ctrl_type=(String)map.get("ctrl_type");//是否控制到部门，０控制，１不控制
		String ctrl_peroid=(String)map.get("ctrl_peroid");//年月控制标识=1按年，=0按月
		if(ctrl_peroid==null|| "".equals(ctrl_peroid))
			ctrl_peroid="0";
		String fc_flag=(String)map.get("fc_flag");
		String ctrl_by_level="0";
		String un = "ctrl_item";
		ArrayList checkList = new ArrayList();
		ArrayList dataList = new ArrayList();
		dataList=(ArrayList) map.get(un.toLowerCase());
		HashMap plan = new HashMap();
		for(int j=0;j<dataList.size();j++)
		{
    		LazyDynaBean bean = (LazyDynaBean)dataList.get(j);
			String planitem = (String)bean.get("planitem");
			String realitem = (String)bean.get("realitem");
			String balanceitem = (String)bean.get("balanceitem");
			plan.put(planitem.toLowerCase(),planitem);
		}
		if(map.get("ctrl_by_level")!=null&&!"".equals((String)map.get("ctrl_by_level")))
		{
			ctrl_by_level=(String)map.get("ctrl_by_level");
		}
		/**数据集字段列表*/
		ContentDAO dao=null;
		try{


			/**
			 * 仅可处理下级机构的数据
			 */
			GrossManagBo grossManagBo = new GrossManagBo(this.getFrameconn(), this.getUserView());

			ArrayList<String> unitList = grossManagBo.getPrivUnit(this.userView);
			if (unitList != null) {
				for (int i = 0; i < list.size(); i++) {
					RecordVo vo = (RecordVo) list.get(i);
					String b0110 = vo.getString("b0110");
					for (String unit : unitList) {
						if (unit.equalsIgnoreCase(b0110)) {
							throw GeneralExceptionHandler.Handle(new Exception("仅可处理下级机构数据！"));
						}
					}
				}
			}

				GrossPayManagement gross = new GrossPayManagement(this.getFrameconn(),"GZ_PARAM");
				ArrayList spflaglist = (ArrayList)ma.get("sp")/*gross.elementName("/Params/Gz_amount","sp_flag")*/;
    	 	
	            dao=new ContentDAO(this.getFrameconn());
				if(!(list==null||list.size()==0)){
					/*for(int i=0;i<list.size();i++){
						RecordVo vo=(RecordVo)list.get(i);
						String info=getInfo(vo);
						if(info!=null)
							throw new GeneralException(info.toString());
					}*/
					for(int j=0;j<list.size();j++)
					{
						RecordVo vo=(RecordVo)list.get(j);
						/**未保存到数据库中的也要效验*/
						for(int i=0;i<dataList.size();i++)
						{
							LazyDynaBean bean = new LazyDynaBean();
							bean.set("b0110", vo.getString("b0110"));
							bean.set("year",vo.getString("aaaa"));
							bean.set(vo.getModelName()+"z1",vo.getString(vo.getModelName()+"z1"));//后面会用到  zhaoxg add
				    		LazyDynaBean abean = (LazyDynaBean)dataList.get(i);
							String planitem = (String)abean.get("planitem");
							bean.set(planitem.toLowerCase(), vo.getString(planitem.toLowerCase()));
							bean.set("itemid",planitem);
							checkList.add(bean);
						}			
					}
					StringBuffer info=new StringBuffer("");
					for(int i=0;i<list.size();i++){
						RecordVo vo=(RecordVo)list.get(i);
						int state=vo.getState();
						if(state==-1){
							throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.acount.data.published.failure")));
						}
						if(!"02".equals(vo.getString(spflaglist.get(0).toString().toLowerCase()))){
							info.append(ResourceFactory.getProperty("gz.acount.only.published.drafting.suspended"));	
						}
						if(info!=null&&info.length()!=0&&!"ok".equalsIgnoreCase(info.toString())){
					     	throw GeneralExceptionHandler.Handle(new Exception(info.toString()));
				    	}
						if("1".equals(ctrl_by_level))
						{
					    	info=this.getParentRecord(vo, ctrl_peroid, spflaglist.get(0).toString());
					    	if(info.length()>1)
						    	throw GeneralExceptionHandler.Handle(new Exception(info.toString()));
					    	info = this.checkChildRecords(vo, ctrl_type,ctrl_peroid, spflaglist.get(0).toString());
					    	if(info.length()>1)
						    	throw GeneralExceptionHandler.Handle(new Exception(info.toString()));
					    	if(fc_flag!=null&&fc_flag.length()!=0){
								String tem="";
								tem =gross.upValue(ctrl_peroid, vo, dao, plan, checkList, ctrl_type, fc_flag);
								if(tem!=null&&tem.length()!=0){
					    			info.append(tem);
					    		}
					    	}else{
					    		String tem="";
					    		tem =gross.upValue(ctrl_peroid,vo,dao,plan,checkList,ctrl_type);
					    		if(tem!=null&&tem.length()!=0){
					    			info.append(tem);
					    		}
					    	}
					    	if(info!=null&&info.length()!=0&&!"ok".equalsIgnoreCase(info.toString())){
						     	throw GeneralExceptionHandler.Handle(new Exception(info.toString()));
					    	}
					    	String tem="";
				    		tem =gross.underValue(ctrl_peroid, vo, dao, plan, checkList, ctrl_type, fc_flag);
				    		if(tem!=null&&tem.length()!=0){
				    			info.setLength(0);
				    			info.append(tem);
				    		}
				    		if(info!=null&&info.length()!=0&&!"ok".equalsIgnoreCase(info.toString())){
						     	throw GeneralExceptionHandler.Handle(new Exception(info.toString()));
					    	}
						}
						
						
				    	
						
					}
					for(int i=0;i<list.size();i++){
						RecordVo vo=(RecordVo)list.get(i);
						//StringBuffer childitemid=this.getChildItem(vo.getString("b0110"), ctrl_type);
						String z0=vo.getString("aaaa");
						String b0110=vo.getString("b0110");
						String season = vo.getString("season");
						String year =z0.substring(0,4);
						String month=z0.substring(5);
						StringBuffer sql = new StringBuffer();
						sql.append("update ");
						sql.append(vo.getModelName());
						sql.append(" set "+spflaglist.get(0).toString().toLowerCase()+"='03' where "+spflaglist.get(0).toString().toLowerCase()+"='02' and b0110 like '"+vo.getString("b0110")+"%'");
						sql.append(" and ");
					    sql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
					    if("0".equalsIgnoreCase(ctrl_peroid))//按月份
				     	    sql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'"); 
				     	if("2".equalsIgnoreCase(ctrl_peroid))//按季度
					    {
					    	String sea = this.getSeasonCondation(Integer.parseInt(season));
					    	sql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in ("+sea+")");
					    }
					    if(fc_flag!=null&fc_flag.length()!=0){//dml 2011-6-16 8:49:18
				    		sql.append(" and ");
				    		sql.append(vo.getModelName()+"z1");
				    		sql.append("='");
				    		sql.append(vo.getString(vo.getModelName()+"z1"));
				    		sql.append("' and ");
				    		sql.append(fc_flag+"=2");
				    	}
						dao.update(sql.toString());
					}
				}
		}
		catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
			
		}
		
	}
	public StringBuffer getParentRecord(RecordVo vo,String ctrl_peroid,String spitem)
	{
		StringBuffer buf = new StringBuffer("");
		try
		{
			String z0=vo.getString("aaaa");
			String b0110=vo.getString("b0110");
			String year =z0.substring(0,4);
			String month=z0.substring(5);
			StringBuffer sql = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String parentid="";
			RowSet rs = dao.search("select parentid from organization where codeitemid='"+b0110+"'");
			while(rs.next())
			{
				if(rs.getString("parentid").equalsIgnoreCase(b0110))
					return buf;
				parentid=rs.getString("parentid");
			}
			String setname=vo.getModelName();
			sql.append(" select * from ");
			sql.append(setname+" where ");
			sql.append("b0110='"+parentid+"' and ");
			sql.append(Sql_switcher.year(setname+"z0")+"="+year);
			if("0".equals(ctrl_peroid)|| "2".equals(ctrl_peroid))
	    		sql.append(" and "+Sql_switcher.month(setname+"z0")+"="+month);
			sql.append(" and ("+spitem+"='04' or "+spitem+"='03')");//03：已批  04：已发布
			rs =dao.search(sql.toString());
			//当父节点没有数据时弹出如下错误信息
			if(!rs.next())
			{
				String desc=AdminCode.getCodeName("UN",vo.getString("b0110"));
			    if(desc==null|| "".equals(desc))
				{
				    desc = AdminCode.getCodeName("UM", vo.getString("b0110"));
			    }
			    buf.append(desc);
				if("0".equals(ctrl_peroid))//按月份
				{
					buf.append(ResourceFactory.getProperty("gz.account.uporg"));
					buf.append(year);
					//columns.archive.year=年
					//columns.archive.month=月
					
					buf.append(ResourceFactory.getProperty("columns.archive.year"));
					buf.append(month);
					buf.append(ResourceFactory.getProperty("columns.archive.month"));
					buf.append(ResourceFactory.getProperty("gz.acount.noapprove"));
				}
				else if("1".equals(ctrl_peroid))//按年份
				{
					buf.append(ResourceFactory.getProperty("gz.account.uporg"));
					buf.append(year);
					//columns.archive.year=年
					//columns.archive.month=月
					buf.append(ResourceFactory.getProperty("columns.archive.year"));
					buf.append(ResourceFactory.getProperty("gz.acount.noapprove"));
				}
				else if("2".equals(ctrl_peroid))//按季度
				{
					buf.append(ResourceFactory.getProperty("gz.account.uporg"));
					buf.append(year);
					GrossManagBo bo = new GrossManagBo();
					//columns.archive.year=年
					//columns.archive.month=月
					buf.append(bo.getSeasonZH(Integer.parseInt(month)));
					buf.append(ResourceFactory.getProperty("gz.acount.noapprove"));
					
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf;
	}
	
	public StringBuffer checkChildRecords(RecordVo vo,String ctrl_type,String ctrl_peroid,String spitem){
		StringBuffer buf = new StringBuffer("");
		try{
			String z0=vo.getString("aaaa");
			String b0110=vo.getString("b0110");
			String year =z0.substring(0,4);
			String month=z0.substring(5);
			StringBuffer sql = new StringBuffer();
			StringBuffer sqlstr = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			
			String setname=vo.getModelName();
			sql.append(" select b0110 from ");
			sql.append(setname);
			if("1".equals(ctrl_type)){//如果不按部门控制
				sql.append(" left join organization o on "+setname+".b0110=o.codeitemid ");
			}
			sql.append(" where ");
			sql.append("1=1 and ");
			if("1".equals(ctrl_type)){//如果不按部门控制
				sql.append("UPPER(o.codesetid)='UN' and ");
			}
			sql.append(Sql_switcher.year(setname+"z0")+"="+year);
			if("0".equals(ctrl_peroid)|| "2".equals(ctrl_peroid))
	    		sql.append(" and "+Sql_switcher.month(setname+"z0")+"="+month);
			sql.append(" and ("+spitem+"='01' or "+spitem+"='07' or "+spitem+"='09')");//01：起草  07：驳回  09：暂停
			
			sqlstr.append("select codeitemid from organization where codeitemid like '"+b0110+"%' and codeitemid<>'"+b0110+"' and codeitemid in ("+sql+") order by codeitemid");
			RowSet rs = dao.search(sqlstr.toString());

			int count = 0;
			while(rs.next()){
				count ++;
				if(count==6){
			    	buf.setLength(buf.length()-1);
			    	buf.append("等");
			    	break;
			    }
				String itemid = rs.getString("codeitemid");
				String desc=AdminCode.getCodeName("UN",itemid);
			    if(desc==null|| "".equals(desc))
				{
				    desc = AdminCode.getCodeName("UM", itemid);
			    }
			    buf.append(desc+"，");
			    
			}
			if(buf.length()>0){
				if(buf.charAt(buf.length()-1)=='，')
					buf.setLength(buf.length()-1);
				buf.append("尚未报批，不予批准！");
			}
			return buf;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf;
	}
	
	public String getSeasonCondation(int season)
	{
		StringBuffer buf = new StringBuffer();
		try
		{
			buf.append(season+","+(season+1)+","+(season+2));     
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
	private String getInfo(RecordVo vo) {
		// TODO Auto-generated method stub
		return null;
	}
	public StringBuffer getChildItem(String itemid,String ctrl_type)
	{
		StringBuffer buf = new StringBuffer("");
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append(" select codeitemid from organization where codeitemid like '"+itemid+"%' ");
			if("1".equals(ctrl_type))
				sql.append(" and UPPER(codesetid)='UN' ");
			else
				sql.append(" and (UPPER(codesetid)='UN' or UPPER(codesetid)='UM')");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rs = null;
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				buf.append(",'");
				buf.append(rs.getString("codeitemid"));
				buf.append("'");
			}
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf;
	}
}
