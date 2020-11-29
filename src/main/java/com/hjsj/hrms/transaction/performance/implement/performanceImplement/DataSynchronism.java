package com.hjsj.hrms.transaction.performance.implement.performanceImplement;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.ResultSetMetaData;
/**
 * <p>DataSynchronism.java</p>
 * <p>Description:人员同步</p>
 * <p>Company:hjsj</p>
 * <p>create time:2019-09-10 13:00:00</p>
 * @author xuzhe
 * @version 1.0
 */

public class DataSynchronism extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			
		 
		String planid = (String) this.getFormHM().get("plan_id");
		String A0101="";
		String explain ="";
		StringBuffer A0101s = new StringBuffer("");
		StringBuffer mA0101s = new StringBuffer("");
		String sql  ="SELECT A0101 FROM per_object WHERE per_object.plan_id="+planid+" AND not EXISTS(SELECT * FROM USRA01";
		       sql +="  WHERE per_object.plan_id= "+planid+"";
		       sql +="  AND per_object.object_id=USRA01.A0100 AND per_object.A0101=USRA01.A0101) ORDER BY A0000";
	    this.frowset=dao.search(sql);  
	    int i=0;
	    while(this.frowset.next()){
	           A0101 = this.frowset.getString("A0101");
	           A0101s.append(A0101+",");
	           i++;
	    }
	    if(i!=0){   
		String A0101ss =A0101s.toString().substring(0,A0101s.toString().length()-1)+" (共"+i+"人)";
		explain="考核对象【"+A0101ss+"】";
	    }
	    String sql2  ="SELECT distinct mainbody_id,A0101 FROM per_mainbody WHERE per_mainbody.plan_id="+planid+" AND not EXISTS(SELECT * FROM USRA01";
	           sql2 +="  WHERE per_mainbody.plan_id= "+planid+"";
	           sql2 +="  AND per_mainbody.mainbody_id=USRA01.A0100 AND per_mainbody.A0101=USRA01.A0101)";
        this.frowset=dao.search(sql2);  
        int n=0;
        while(this.frowset.next()){
                A0101 = this.frowset.getString("A0101");
                mA0101s.append(A0101+",");
                n++;
              }
        if(n!=0){
        String A0101ss =mA0101s.toString().substring(0,mA0101s.toString().length()-1)+" (共"+n+"人)";
        explain	+="考核主体【"+A0101ss+"】";
        }
        if(i!=0||n!=0){
        	explain+="在在职人员库中已不存在或ID已变更，请在考核实施中将相关考核对象（或主体）删除并重新增加。如果不处理，考核还可以继续进行，但相关人员考评结果不能归档！";
        }
	    
		getSynchronism("per_Object", "plan_id="+planid+"", "object_id", "0");
		getSynchronism("per_mainbody", "plan_id="+planid+"", "mainbody_id", "0");
		getSynchronism("P04", "plan_id="+planid+"", "A0100", "0");

		getSynchronism("per_article", "plan_id="+planid+"", "A0100", "0");
		getSynchronism("per_object_std", "", "object_id", "0");
		
		getSynchronism("per_target_mx", "", "object_id", "NoE01A1");
		getSynchronism("per_Result_"+planid+"","","object_id", "0");
		
		getSynchronism("per_mainbody_std","", "mainbody_id", "0");
		getSynchronism("per_key_event","","A0100", "NoE01A1");
		getSynchronism("P01","", "A0100", "0");
		getSynchronism("P03","", "A0100", "NoE01A1");
		
		this.getFormHM().put("a0101s", explain);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

   private String getSynchronism(String table,String plan_id,String id,String E01A1 ) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.frameconn);
		String sql = "";
		try {
			RowSet rowSet=dao.search("select * from P01 where 1=2");
			ResultSetMetaData metaData=rowSet.getMetaData();
			for(int i=1;i<=metaData.getColumnCount();i++)
			{
				if("E01A1".equalsIgnoreCase(metaData.getColumnName(i))&&metaData.getColumnDisplaySize(i)<30)//p01表e01a1字段长度如果不到30就修改为30  zhaoxg add 2014-11-2
				{
					DbWizard dbw=new DbWizard(this.frameconn);
					Table _table=new Table("P01");
					Field field=null;
					field=new Field("E01A1","E01A1");
					field.setDatatype(DataType.STRING);
					field.setLength(30);
					field.setNullable(true);
					_table.addField(field);
					dbw.alterColumns(_table);
				}
			}
			
			if (Sql_switcher.searchDbServer() == 1){
				sql="UPDATE "+table+" set "+table+".B0110=usra01.B0110,"+table+".E0122=usra01.E0122";
				if(!"NoE01A1".equals(E01A1))
				sql += ","+table+".E01A1=usra01.E01A1";
				sql += " from usra01 where "+table+"."+id+" = usra01.A0100 and "+table+".A0101= usra01.A0101 ";
				if(!"".equals(plan_id))
				sql += "and "+table+"."+plan_id+"";
			}else{
				sql="UPDATE "+table+" set ";
				if(!"NoE01A1".equals(E01A1)){
				sql+="(B0110,E0122,E01A1)=(select B0110,E0122,E01A1 ";
				sql+="from usra01 where "+table+"."+id+" = usra01.A0100 and "+table+".A0101= usra01.A0101  )where exists(select * from usra01 where "+table+"."+id+"=usra01.A0100 and "+table+".A0101=usra01.A0101";
				}else{
				sql+="(B0110,E0122)=(select B0110,E0122 ";
				sql+="from usra01 where "+table+"."+id+" = usra01.A0100 and "+table+".A0101= usra01.A0101  )where exists(select * from usra01 where "+table+"."+id+"=usra01.A0100 and "+table+".A0101=usra01.A0101";
				}
				if(!"".equals(plan_id))
				sql+=" and "+table+"."+plan_id+")";
				else
				sql+=")";
	
			}
		
			dao.update(sql);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	return "";

}
}