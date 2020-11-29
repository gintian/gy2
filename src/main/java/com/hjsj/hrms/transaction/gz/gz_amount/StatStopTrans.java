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
public class StatStopTrans extends IBusiness {
	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		//String name=(String)hm.get("position_set_table");
		ArrayList list=(ArrayList)hm.get("position_set_record");
		String viewUnit=(String)hm.get("viewUnit");
		if(list==null||list.size()==0)
		{
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.account.select.bohui.record")));
		}
		/**数据集字段列表*/
		ContentDAO dao=null;
		GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),2);
		HashMap map = bo.getValuesMap();
		HashMap mp = bo.getMap();
		String ctrl_peroid=(String)map.get("ctrl_peroid");//年月控制标识=1按年，=0按月
		if(ctrl_peroid==null|| "".equals(ctrl_peroid))
			ctrl_peroid="0";
		String fc_flag=(String)map.get("fc_flag");
		try{
			 	GrossPayManagement gross = new GrossPayManagement(this.getFrameconn(),"GZ_PARAM");
	    	 	ArrayList spflaglist = (ArrayList)mp.get("sp")/*gross.elementName("/Params/Gz_amount","sp_flag")*/;
			
	            dao=new ContentDAO(this.getFrameconn());

				if(!(list==null||list.size()==0)){
					/*for(int i=0;i<list.size();i++){
						RecordVo vo=(RecordVo)list.get(i);
						String info=getInfo(vo);
						if(info!=null)
							throw new GeneralException(info.toString());
					}*/
					StringBuffer info=new StringBuffer("");
					for(int i=0;i<list.size();i++){
						RecordVo vo=(RecordVo)list.get(i);
						int state=vo.getState();
						if(state==-1){
							throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.acount.stop.failure")));
						}
						/*if(!vo.getString(spflaglist.get(0).toString().toLowerCase()).equals("04")){
							info.append(ResourceFactory.getProperty("gz.acount.only.stop.drafting.suspended"));	
						}*/
						 if(fc_flag!=null&&fc_flag.length()!=0){
							 
							if("1".equalsIgnoreCase(vo.getString(fc_flag.toLowerCase()))){
								String b0110 = vo.getString("b0110a");
								String _b0110 = AdminCode.getCode("UN", b0110) != null ? AdminCode.getCode("UN", b0110).getCodename():"";
								if(_b0110!=null&&_b0110.length()==0){
									_b0110 = AdminCode.getCode("UM", b0110) != null ? AdminCode.getCode("UM", b0110).getCodename():"";
								}
								if(_b0110!=null&&_b0110.length()==0){
									_b0110 = AdminCode.getCode("@K", b0110) != null ? AdminCode.getCode("@K", b0110).getCodename():"";
								}
								if(_b0110!=null&&_b0110.length()==0){
									_b0110 = b0110;
								}
								if("0".equalsIgnoreCase(ctrl_peroid))
									info.append(_b0110+vo.getString("aaaa").substring(vo.getString("aaaa").indexOf(".")+1)+"月"+vo.getString(vo.getModelName()+"z1")+"次已封存不能被操作!");
								if("1".equalsIgnoreCase(ctrl_peroid))
									info.append(_b0110+vo.getString(vo.getModelName()+"z0b")+"年"+vo.getString(vo.getModelName()+"z1")+"次已封存不能被操作!");
								if("2".equalsIgnoreCase(ctrl_peroid))
									info.append(_b0110+vo.getString(vo.getModelName()+"z0b")+vo.getString(vo.getModelName()+"z1")+"次已封存不能被操作!");
							}
						 }
						if(info.length()>1)
							throw GeneralExceptionHandler.Handle(new Exception(info.toString()));
					}

					/**
					 * 仅可处理下级机构的数据
					 */
					GrossManagBo grossManagBo=new GrossManagBo(this.getFrameconn(),this.getUserView());
					ArrayList<String> unitList=grossManagBo.getPrivUnit(this.userView);
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


					for(int i=0;i<list.size();i++){
						RecordVo vo=(RecordVo)list.get(i);
						if((!"04".equals(vo.getString(spflaglist.get(0).toString().toLowerCase()))) && (!"03".equals(vo.getString(spflaglist.get(0).toString().toLowerCase()))) && (!"02".equals(vo.getString(spflaglist.get(0).toString().toLowerCase())))){
							throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.acount.only.stop.drafting.suspended")));
						}
						String z0=vo.getString("aaaa");
						String b0110=vo.getString("b0110");
						String year =z0.substring(0,4);
						String month=z0.substring(5);
						String season = vo.getString("season");
						StringBuffer sql = new StringBuffer();
						sql.append("update ");
						sql.append(vo.getModelName());
						sql.append(" set "+(String)spflaglist.get(0)+"='07' where b0110='");
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
					    if(fc_flag!=null&&fc_flag.length()!=0){//dml 2011-6-16 8:49:18
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
