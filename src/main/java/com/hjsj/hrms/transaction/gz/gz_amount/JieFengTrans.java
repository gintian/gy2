package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GrossPayManagement;
import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
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
 * <p>create time:2011-6-15 9:59:35:${time}</p>
 * @author dml
 * @version 1.0
 * 
 */
public class JieFengTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		ArrayList list=(ArrayList)hm.get("position_set_record");
		GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),2);
		HashMap map =bo.getValuesMap();
		HashMap ma=bo.getMap();
		String ctrl_peroid=(String)map.get("ctrl_peroid");//年月控制标识=1按年，=0按月,2按季
		ContentDAO dao=null;
		ArrayList spflaglist = (ArrayList)ma.get("sp");
		String fc_flag=(String)map.get("fc_flag");
		try{
			GrossPayManagement gross = new GrossPayManagement(this.getFrameconn(),"GZ_PARAM");
			String info=this.hasJf(list, ctrl_peroid, fc_flag, spflaglist);
			if(info!=null&&info.length()!=0){
				throw GeneralExceptionHandler.Handle(new Exception(info.toString()));
			}
			for(int i=0;i<list.size();i++){
				RecordVo rmap=(RecordVo)list.get(i);
				String b011=rmap.getString("b0110");
				String setname=rmap.getModelName();
				String sp=rmap.getString(spflaglist.get(0).toString().toLowerCase());
				StringBuffer sql=new StringBuffer("update ");
				sql.append(setname);
				sql.append(" set ");
				sql.append(fc_flag);
				sql.append("='2'");
				sql.append(" where b0110='");
				sql.append(rmap.getString("b0110"));
				sql.append("' and ");
				if(("04".equalsIgnoreCase(sp)) || ("03".equalsIgnoreCase(sp))){
					if("0".equalsIgnoreCase(ctrl_peroid)){//月
						String aaa=rmap.getString("aaaa");
						String month=aaa.substring(aaa.indexOf(".")+1);
						String year=aaa.substring(0, aaa.indexOf("."));
						sql.append(Sql_switcher.year(setname+"z0"));
						sql.append("=");
						sql.append(year);
						sql.append(" and ");
						sql.append(Sql_switcher.month(setname+"z0"));
						sql.append("=");
						sql.append(month);
						sql.append(" and ");
						sql.append(setname+"z1='");
						sql.append(rmap.getString(setname+"z1"));
						sql.append("'");
					}
					if("1".equalsIgnoreCase(ctrl_peroid)){//年
						sql.append(Sql_switcher.year(setname+"z0"));
						sql.append("=");
						sql.append(rmap.getString(setname+"z0b"));
						sql.append(" and  ");
						sql.append(setname+"z1='");
						sql.append(rmap.getString(setname+"z1"));
						sql.append("'");
					}
					if("2".equalsIgnoreCase(ctrl_peroid)){//季度
						String jidu=rmap.getString(setname+"z0b");
						String month=this.getMonth(jidu);
						String aaa=rmap.getString("aaaa");
						String year=aaa.substring(0,aaa.indexOf("."));
						sql.append(Sql_switcher.year(setname+"z0"));
						sql.append("=");
						sql.append(year);
						sql.append(" and ");
						sql.append(Sql_switcher.month(setname+"z0"));
						sql.append(" in");
						sql.append(month);
						sql.append(" and ");
						sql.append(setname+"z1='");
						sql.append(rmap.getString(setname+"z1"));
						sql.append("'");
					}
					dao=new ContentDAO(this.frameconn);
					dao.update(sql.toString());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
			
		}

	}
	public String hasJf(ArrayList list,String ctrl_peroid,String fc_flag,ArrayList splist){
		StringBuffer info=new StringBuffer();
		HashMap hasMap=new HashMap();
		for(int i=0;i<list.size();i++){
			String keys="";
			RecordVo vo=(RecordVo)list.get(i);
			if("1".equalsIgnoreCase(vo.getString(fc_flag.toLowerCase()))){
				if("0".equalsIgnoreCase(ctrl_peroid)){
					keys=keys+vo.getString("aaaa")+vo.getString("b0110");
					if(hasMap.get(keys)!=null){
						info.append("不能同时解封同一单位下同一时期两条以上的不同记录！");
					}else{
						hasMap.put(keys, keys);
					}
					if(info.length()>1){
						return info.toString();
					}
				}
				if("1".equalsIgnoreCase(ctrl_peroid)){
					keys=keys+vo.getString("aaaa").substring(0,4)+vo.getString("b0110");
					if(hasMap.get(keys)!=null){
						info.append("不能同时解封同一单位下同一时期两条及两条以上的不同记录！");
					}else{
						hasMap.put(keys, keys);
					}
					if(info.length()>1){
						return info.toString();
					}
				}
				if("2".equalsIgnoreCase(ctrl_peroid)){
					keys=keys+vo.getString("aaaa").substring(0,4)+vo.getString(vo.getModelName()+"z0b")+vo.getString("b0110");
					if(hasMap.get(keys)!=null){
						info.append("不能同时解封同一单位下同一时期两条及两条以上的不同记录！");
					}else{
						hasMap.put(keys, keys);
					}
					if(info.length()>1){
						return info.toString();
					}
				}
			}else{
				info.append("未封存记录不能被解封！请选择已封存记录！");
				break;
			}
			
		}
		if(info==null||info.length()==0){
			info.append(this.validate(list, ctrl_peroid, fc_flag, splist));
		}
		if(info!=null&&info.length()!=0){
			return info.toString();
		}
		return info.toString();
	}
	public String validate(ArrayList list,String ctrl_period,String fc_flag,ArrayList splist){
		StringBuffer info=new StringBuffer();
		for(int i=0;i<list.size();i++){
			RecordVo vo=(RecordVo)list.get(i);
			StringBuffer sql=new StringBuffer();
			sql.append(" select * from ");
			sql.append(vo.getModelName());
			sql.append(" where b0110='");
			sql.append(vo.getString("b0110"));
			sql.append("' and ");
			sql.append(fc_flag);
			sql.append("='2' and ");
			if("0".equalsIgnoreCase(ctrl_period)){//yue
				sql.append(Sql_switcher.year(vo.getModelName()+"z0"));
				sql.append("=");
				sql.append(vo.getString("aaaa").substring(0,4));
				sql.append(" and ");
				sql.append(Sql_switcher.month(vo.getModelName()+"z0"));
				sql.append("=");
				sql.append(vo.getString("aaaa").substring(5));
			}
			if("1".equalsIgnoreCase(ctrl_period)){//nian
				sql.append(Sql_switcher.year(vo.getModelName()+"z0"));
				sql.append("=");
				sql.append(vo.getString(vo.getModelName()+"z0b"));
			}
			if("2".equalsIgnoreCase(ctrl_period)){//jidu
				String jidu=vo.getString(vo.getModelName()+"z0b");
				String month=this.getMonth(jidu);
				sql.append(Sql_switcher.year(vo.getModelName()+"z0"));
				sql.append("=");
				sql.append(vo.getString("aaaa").substring(0, 4));
				sql.append(" and ");
				sql.append(Sql_switcher.month(vo.getModelName()+"z0"));
				sql.append("in ");
				sql.append(month);
			}
			ContentDAO dao=new ContentDAO(this.frameconn);
			try {
				this.frowset=dao.search(sql.toString());
				if(this.frowset.next()){
					info.append("存在已解封记录，不能解封");
					if("0".equalsIgnoreCase(ctrl_period)){
						info.append(vo.getString("aaaa"));
						info.append("月");
						info.append("第");
						info.append(vo.getString(vo.getModelName()+"z1"));
						info.append("条记录！");
					}
					if("1".equalsIgnoreCase(ctrl_period)){
						info.append(vo.getString("aaaa").subSequence(0, 4));
						info.append("年");
						info.append("第");
						info.append(vo.getString(vo.getModelName()+"z1"));
						info.append("条记录！");
					}
					if("2".equalsIgnoreCase(ctrl_period)){
						info.append(vo.getString("aaaa").subSequence(0, 4));
						info.append("年");
						info.append(vo.getString(vo.getModelName()+"z0b"));
						info.append(vo.getString(vo.getModelName()+"z1"));
						info.append("条记录！");
					}
					return info.toString();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return info.toString();
	}
	public String getMonth(String ss) {
		String month="";
		if("第一季度".equalsIgnoreCase(ss)){
			month="(1,2,3)";
		}else if("第二季度".equalsIgnoreCase(ss)){
			month="(4,5,6)";
		}else if("第三季度".equalsIgnoreCase(ss)){
			month="(7,8,9)";
		}else if("第四季度".equalsIgnoreCase(ss)){
			month="(10,11,12)";
		}
		
		return month;
	}
}
