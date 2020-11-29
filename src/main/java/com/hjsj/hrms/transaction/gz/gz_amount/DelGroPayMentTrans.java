package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GrossManagBo;
import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.tablemodel.TableModel;
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
public class DelGroPayMentTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		//String name=(String)hm.get("position_set_table");
		ArrayList list=(ArrayList)hm.get("position_set_record");
		if(list==null||list.size()==0)
		{
			throw GeneralExceptionHandler.Handle(new Exception("请选择要删除的记录！"));
		}
		/**数据集字段列表*/
		ContentDAO dao=null;
		GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),2);
		HashMap map = bo.getValuesMap();
		HashMap mp = bo.getMap();
		String ctrl_peroid=(String)map.get("ctrl_peroid");//年月控制标识=1按年，=0按月
		if(ctrl_peroid==null|| "".equals(ctrl_peroid))
			ctrl_peroid="0";
		String ctrl_type=(String)map.get("ctrl_type");//是否控制到部门，０控制，１不控制
		String ctrl_by_level="0";//是否按层级控制=0否=1是
		String fc_flag=(String)map.get("fc_flag");
		if(map.get("ctrl_by_level")!=null&&!"".equals((String)map.get("ctrl_by_level")))
		{
			ctrl_by_level=(String)map.get("ctrl_by_level");
		}
		try{
				//GrossPayManagement grossManag = new GrossPayManagement(this.getFrameconn(),"GZ_PARAM");
				ArrayList spflaglist = (ArrayList)mp.get("sp")/*grossManag.elementName("/Params/Gz_amount","sp_flag")*/;
			
	            dao=new ContentDAO(this.getFrameconn());
	            GrossManagBo gmb = new GrossManagBo(this.getFrameconn());
			ArrayList<String> unitList = gmb.getPrivUnit(this.getUserView());
	            if(fc_flag!=null&&fc_flag.length()!=0){
	            	gmb.setFc_flag(fc_flag);
	            }
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
							throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.acount.date.del.failure")));
						}
						if(("04".equals(vo.getString(spflaglist.get(0).toString().toLowerCase()))) || ("03".equals(vo.getString(spflaglist.get(0).toString().toLowerCase())))){
							info.append(ResourceFactory.getProperty("gz.acount.only.del.drafting.suspended"));	
						}
						String b0110=vo.getString("b0110");
						if("02".equals(vo.getString(spflaglist.get(0).toString().toLowerCase()))){
							for (String unit : unitList) {
								if(unit.equalsIgnoreCase(b0110)){
									info.append("仅可删除下级机构的报批记录！");
									break;
								}
							}

						}

						if(info.length()>1)
							throw GeneralExceptionHandler.Handle(new Exception(info.toString()));
						String z0=vo.getString("aaaa");

						String year =z0.substring(0,4);
						String month=z0.substring(5);
						if("1".equals(ctrl_by_level))
						{
				    		if(gmb.isHaveChildren(b0110,ctrl_type,vo,ctrl_peroid))
					    	{
					    		String codeitem = "";
						    	String desc = AdminCode.getCodeName("UN",vo.getString("b0110"));
						    	if(desc!=null&&desc.trim().length()>0){
							    	codeitem = desc;
						    	}else{
						    		desc = AdminCode.getCodeName("UM",vo.getString("b0110"));
							    	if(desc!=null&&desc.trim().length()>0){
							    		codeitem = desc;
							    	}
						    	}
						    	String exception = "";
						    	String UnOrUm="";
						    	if("0".equals(ctrl_type)){
						    		UnOrUm=ResourceFactory.getProperty("gz.acount.havechildplan");//的下级部门已经建立记录，不能删除！
						    	}else{
						    		UnOrUm="的下级单位已经建立记录，不能删除！";
						    	}
						    	if("0".equals(ctrl_peroid))
						    	{
						    		exception=codeitem+ResourceFactory.getProperty("gz.acount.for.yearsmonth.logo")+vo.getString("aaaa")+UnOrUm;
						    	}
						    	else if("1".equals(ctrl_peroid))
						    	{
						     		exception=codeitem+ResourceFactory.getProperty("gz.acount.for.years.logo")+vo.getString(vo.getModelName()+"z0b")+UnOrUm;
						    	}
						    	else
					    		{
					    			exception=codeitem+ResourceFactory.getProperty("gz.acount.for.season.logo")+vo.getString(vo.getModelName()+"z0b")+UnOrUm;
					    		}
					    		throw GeneralExceptionHandler.Handle(new Exception(exception));
				    		}
				    	}
					}
					for(int i=0;i<list.size();i++){
						RecordVo vo=(RecordVo)list.get(i);
						
						String z0=vo.getString("aaaa");
						String b0110=vo.getString("b0110");
						String year =z0.substring(0,4);
						String month=z0.substring(5);
						
						DBMetaModel dbmeta = new DBMetaModel();
					    TableModel tableModel = dbmeta.searchTable(vo.getModelName());
					    String sql = tableModel.getDeleteSql(true);

					    sql=sql.substring(0,sql.lastIndexOf("where"))+" where "+vo.getModelName()+".b0110='"+b0110+"' and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'";
					    if("0".equalsIgnoreCase(ctrl_peroid))
					    	sql+=" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'";
					    if("2".equals(ctrl_peroid))
					    {
					    	String season = vo.getString("season");
					    	String sea=this.getSeasonCondation(Integer.parseInt(season));
					    	sql+=" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in ("+sea+")";
					    }
					    if(fc_flag!=null&&fc_flag.length()!=0){
					    	sql+=" and "+vo.getModelName()+"z1="+vo.getString(vo.getModelName()+"z1");
					    }
						dao.update(sql);
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
