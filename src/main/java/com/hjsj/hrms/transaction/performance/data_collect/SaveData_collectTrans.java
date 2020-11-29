package com.hjsj.hrms.transaction.performance.data_collect;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.performance.data_collect.DataCollectBo;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
* 
* 类名称：SaveData_collectTrans   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 21, 2013 12:00:45 PM   
* 修改人：zhaoxg   
* 修改时间：Aug 21, 2013 12:00:45 PM   
* 修改备注：   数据采集保存
* @version    
*
 */
public class SaveData_collectTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("data_collect_table");
		ArrayList list=(ArrayList)hm.get("data_collect_record");
		cat.debug("table name="+name);
		String fieldsetid=name.substring(3);
		try
		{
			 
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo vo=null;
			boolean subed=false;
			for(int i=0;i<list.size();i++)
			{
				vo=(RecordVo)list.get(i);			 
				String zt=vo.getString("zt");
				if(zt!=null&&("02".equalsIgnoreCase(zt)|| "03".equalsIgnoreCase(zt)|| "06".equalsIgnoreCase(zt)))
						subed=true;
			}
			if(!subed)
			{
				
				try
				{
					dao.updateValueObject(list);

					DataCollectBo bo = new DataCollectBo(this.frameconn,"Params");
					DataCollectBo databo = new DataCollectBo(this.frameconn,this.userView);
					ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
					String _flag  = bo.getXmlValue1("flag",fieldsetid);//flag 1:简单条件  2:复杂条件
					String _value = bo.getValue(fieldsetid);
					String set_id  = bo.getXmlValue1("set_id",fieldsetid);
					String state_id  = bo.getXmlValue1("state_id",fieldsetid);
					String cbase  = bo.getXmlValue1("cbase",fieldsetid);
					
					StringBuffer buf=new StringBuffer();
					buf.append("select hzName,itemname,useflag,itemid,rexpr,cond,standid,itemtype,runflag from salaryformula  where salaryid='");
					buf.append("-2");
					buf.append("' and useflag=1 and (cstate is null or cstate='"+set_id+"') ");//and cstate = '"+set_id+"'   and useflag=1
					buf.append(" order by salaryid,sortid");
					RowSet rset=dao.search(buf.toString());
					ArrayList itemids = new ArrayList();
					while(rset.next()){
						itemids.add(rset.getString("itemid"));
					}
					
					String[] pre = cbase.split(",");
					for(int i=0;i<pre.length;i++){
						String dbname = pre[i];
						String tablename = dbname+set_id;
						StringBuffer tempsql = new StringBuffer("");
						if("1".equals(_flag)&&_value!=null&&!"".equals(_value)){
							FactorList factor = new FactorList("1", _value,dbname, false, false, true, 1, this.userView.getUserId());				
							String strSql = factor.getSqlExpression();
							tempsql.append(" and "+dbname+"A01.a0100 in ( select "+dbname+"A01.a0100 "+strSql+")"); 
						}else if("2".equals(_flag)&&_value!=null&&!"".equals(_value)){
							String tempTableName ="";
							String w ="";
							int infoGroup = 0; // forPerson 人员
							int varType = 8; // logic	
							String whereIN="select "+dbname+"A01.a0100 from "+dbname+"A01";
							alUsedFields.addAll(databo.getMidVariableList(set_id));
							YksjParser yp = new YksjParser(this.userView ,alUsedFields,
									YksjParser.forSearch, varType, infoGroup, "Ht",dbname);
							YearMonthCount ymc=null;							
							yp.run_Where(_value, ymc,"","hrpwarn_result", dao, whereIN,this.frameconn,"A", null);
							tempTableName = yp.getTempTableName();
							w = yp.getSQL();
							tempsql.append("and "+tablename+".A0100 in (select "+dbname+"A01.A0100 from "+dbname+"A01 where 1=1 and exists (select null from "+tempTableName+" where "+tempTableName+".a0100="+dbname+"A01.a0100 and ( "+w+" )))");
						}
						StringBuffer priv = new StringBuffer("");
			    		if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId()))
			    		{
			    			
			    		}
			    		else
			    		{
							String priStrSql = InfoUtils.getWhereINSql(this.userView, dbname);
							priv.append("select "+dbname+"a01.A0100 ");
							if (priStrSql.length() > 0)
								priv.append(priStrSql);
							else
								priv.append(" from "+dbname+"a01");
			    		}
			    		String sql1=""+tablename+".A0100 in ("+priv+")";
			    		
						/**人员计算过滤条件*/
						String strwhere="";		
						strwhere+=" where "+state_id+" in ('01','07')";
						if(priv.toString().length()>0)
							strwhere+=" and "+sql1+" ";
//						if(tempsql.toString().length()>0)
//							strwhere+=" "+tempsql+"";
						HashMap tempym = this.userView.getHm();
						String ym = (String) tempym.get("ym");
						YearMonthCount ymc=null;
						if(ym!=null&&!"".equals(ym)){
							String[] _ym=StringUtils.split(ym, ".");
							String stry=_ym[0];
							String strm=_ym[1];
							String strc="01";
							ymc=new YearMonthCount(Integer.parseInt(stry),Integer.parseInt(strm),Integer.parseInt(strc));
						}else{
							ymc=null;
						}
						databo.computing(strwhere,itemids,set_id,tablename,dbname,ymc);
					}
				}
				catch(Exception ee)
				{
				 
					ee.printStackTrace();
					String message=ee.getMessage();
					if(message.indexOf("data is not corrected")!=-1)
						throw GeneralExceptionHandler.Handle(new Exception("修改的数据超出最大长度限制!"));	
					if(message.indexOf("转换为数据类型")!=-1)
						throw GeneralExceptionHandler.Handle(new Exception("修改的数据超出最大长度限制!"));	 
				}
			}
			else
				throw GeneralExceptionHandler.Handle(new Exception("状态为已报批、已批的记录不允许修改"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}


	}

}
