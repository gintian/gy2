package com.hjsj.hrms.transaction.performance.data_collect;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.performance.data_collect.DataCollectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
* 
* 类名称：DataAppralTrans   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 21, 2013 1:15:00 PM   
* 修改人：zhaoxg   
* 修改时间：Aug 21, 2013 1:15:00 PM   
* 修改备注：   报批
* @version    
*
 */
public class DataAppralTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{		
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String fieldsetid=(String)this.getFormHM().get("fieldsetid");
			
			String ym=(String)this.getFormHM().get("ym");
			DataCollectBo bo = new DataCollectBo(this.frameconn,"Params");
			DataCollectBo databo = new DataCollectBo(this.frameconn,this.userView);
			boolean isHaveItem = databo.isHaveItem(fieldsetid);
			if(!isHaveItem){
				throw GeneralExceptionHandler.Handle(new Exception("当前用户没有该子集下的全部指标权限，不允许报批!"));
			}
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			String _flag  = bo.getXmlValue1("flag",fieldsetid);//flag 1:简单条件  2:复杂条件
			String _value = bo.getValue(fieldsetid);
			String set_id  = bo.getXmlValue1("set_id",fieldsetid);
			String state_id  = bo.getXmlValue1("state_id",fieldsetid);
			String cbase  = bo.getXmlValue1("cbase",fieldsetid);
			HashMap tempym = this.userView.getHm();
			String pre = (String) tempym.get("pre");
//			String[] pre = cbase.split(",");
//			for(int i=0;i<pre.length;i++){
				String dbname = pre;
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
				if(ym!=null&&!"".equals(ym)){
					ym=PubFunc.keyWord_reback(ym);
					strwhere+=" and "+fieldsetid+"z0="+Sql_switcher.dateValue(ym)+" ";
				}
				if(priv.toString().length()>0)
					strwhere+=" and "+sql1+" ";
//				if(tempsql.toString().length()>0)
//					strwhere+=" "+tempsql+"";
				databo.appeal(dao, tablename, state_id, strwhere);
//			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();		
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			
		}
	}

}
