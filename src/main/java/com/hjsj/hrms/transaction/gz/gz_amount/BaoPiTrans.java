package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GrossPayManagement;
import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author GuoFeng
 * @version 1.0
 * 
 */
public class BaoPiTrans extends IBusiness {
	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		//String name=(String)hm.get("position_set_table");
		String ctrl_by_level="0";
		
		ArrayList list=(ArrayList)hm.get("position_set_record");
		if(list==null||list.size()==0)
		{
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.account.select.baopi.record")));
		}
		/**数据集字段列表*/
		ContentDAO dao=null;
		GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),2);
		HashMap map = bo.getValuesMap();
		String ctrl_type=(String)map.get("ctrl_type");//是否控制到部门，０控制，１不控制
		if(map.get("ctrl_by_level")!=null&&!"".equals((String)map.get("ctrl_by_level")))
		{
			ctrl_by_level=(String)map.get("ctrl_by_level");
		}
		HashMap mp = bo.getMap();
		String ctrl_peroid=(String)map.get("ctrl_peroid");//年月控制标识=1按年，=0按月
		if(ctrl_peroid==null|| "".equals(ctrl_peroid))
			ctrl_peroid="0";
		String fc_flag=(String)map.get("fc_flag");//封存字段
		String un = "ctrl_item";
		ArrayList checkList = new ArrayList();
		ArrayList dataList = new ArrayList();
		dataList=(ArrayList) map.get(un.toLowerCase());
		HashMap plan = new HashMap();
		//取出xml文件里的数据放入HashMap中
		for(int j=0;j<dataList.size();j++)
		{
    		LazyDynaBean bean = (LazyDynaBean)dataList.get(j);
			String planitem = (String)bean.get("planitem");
			String realitem = (String)bean.get("realitem");
			String balanceitem = (String)bean.get("balanceitem");
			plan.put(planitem.toLowerCase(),planitem);
		}
		try{
			 	GrossPayManagement gross = new GrossPayManagement(this.getFrameconn(),"GZ_PARAM");
	    	 	ArrayList spflaglist = (ArrayList)mp.get("sp")/*gross.elementName("/Params/Gz_amount","sp_flag")*/;
			
	            dao=new ContentDAO(this.getFrameconn());

				if(!(list==null||list.size()==0)){
					//将总额参数设置中的项目设置逐条和所选的值对应放入LazyDynaBean塞进list中
					for(int j=0;j<list.size();j++)
					{
						RecordVo vo=(RecordVo)list.get(j);
						/**未保存到数据库中的也要效验*/
						for(int i=0;i<dataList.size();i++)
						{
							LazyDynaBean bean = new LazyDynaBean();
							bean.set("b0110", vo.getString("b0110"));
							bean.set("year",vo.getString("aaaa"));
							//20141212 dengcan
							if(fc_flag!=null&&fc_flag.length()!=0){
								bean.set(vo.getModelName()+"z1",vo.getString(vo.getModelName()+"z1")); 
							}
							
				    		LazyDynaBean abean = (LazyDynaBean)dataList.get(i);
							String planitem = (String)abean.get("planitem");
							bean.set(planitem.toLowerCase(), vo.getString(planitem.toLowerCase()));
							bean.set("itemid",planitem);
							checkList.add(bean);
						}			
					}
					
					StringBuffer info=new StringBuffer("");
					//主要作用是各种条件的判断，限制
					for(int i=0;i<list.size();i++){
						RecordVo vo=(RecordVo)list.get(i);
						int state=vo.getState();
						if(state==-1){
							throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.acount.appeal.failure")));
						}
						if((!"01".equals(vo.getString(spflaglist.get(0).toString().toLowerCase()))) && (!"07".equals(vo.getString(spflaglist.get(0).toString().toLowerCase()))) && (!"09".equals(vo.getString(spflaglist.get(0).toString().toLowerCase())))){
							throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.account.only.appeal")));
						}
						
						
						
						if("1".equals(ctrl_by_level))
						{
					    	if(fc_flag!=null&&fc_flag.length()!=0){
								String tem="";
								tem =gross.upValue(ctrl_peroid, vo, dao, plan, checkList, ctrl_type, fc_flag);
								if(tem!=null&&tem.length()!=0){
					    			info.append(tem);
					    		}
					    	}else{
					    		String tem="";
					    		//进行总额的比较
					    		tem =gross.upValue(ctrl_peroid,vo,dao,plan,checkList,ctrl_type);
					    		if(tem!=null&&tem.length()!=0){
					    			info.append(tem);
					    		}
					    	}
					    	String tempinfo = ResourceFactory.getProperty("gz.acount.noparent");
					    	if(info.indexOf(tempinfo)!=-1){
					    		info.replace(info.length()-3, info.length()-1, "报批");
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
				    		info.setLength(0);
						}
						
					}
					for(int i=0;i<list.size();i++){
						RecordVo vo=(RecordVo)list.get(i);
						if((!"01".equals(vo.getString(spflaglist.get(0).toString().toLowerCase()))) && (!"07".equals(vo.getString(spflaglist.get(0).toString().toLowerCase()))) && (!"09".equals(vo.getString(spflaglist.get(0).toString().toLowerCase())))){
							throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.account.only.appeal")));
						}
						
						String z0=vo.getString("aaaa");
						String b0110=vo.getString("b0110");
						String year =z0.substring(0,4);
						String month=z0.substring(5);
						String season = vo.getString("season");
						StringBuffer sql = new StringBuffer();
						sql.append("update ");
						sql.append(vo.getModelName());
						sql.append(" set "+(String)spflaglist.get(0)+"='02' where b0110='");
						sql.append(vo.getString("b0110"));
						sql.append("' and ");
					    sql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
					    if("0".equalsIgnoreCase(ctrl_peroid))
			    		    sql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'");
					    if("2".equals(ctrl_peroid))
					    {
					    	String sea = this.getSeasonCondation(Integer.parseInt(season));
					    	sql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in ("+sea+")");
					    }
					    if(fc_flag!=null&fc_flag.length()!=0){//dml 2011-6-16 8:49:18
				    		sql.append(" and ");
				    		sql.append(vo.getModelName()+"z1");
				    		sql.append("='");
				    		sql.append(vo.getString(vo.getModelName()+"z1"));
				    		sql.append("' and  " +fc_flag+"=2");
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
}
