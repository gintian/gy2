package com.hjsj.hrms.transaction.info.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *领导班子条件选人
 *
 */
public class SearchLeaderPearsonTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		String like = (String)reqhm.get("like");
		like=like!=null&&like.trim().length()>0?like:"";
		reqhm.remove("like");
		ArrayList dblist = new ArrayList();
		//wangcq 2014-12-09 begin 获取组织机构-领导班子参数配置中人员库设置，如未设置则默认全部人员库。
		LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn());
      	String bz_pre=leadarParamXML.getTextValue(LeadarParamXML.BZDBPRE);
      	String[] dbpres = bz_pre.split(",");
      	if(dbpres.length>0 && !"".equals(dbpres[0])){
      		for(int i=0; i<dbpres.length; i++){
          		if(userView.hasTheDbName(dbpres[i]))
          			dblist.add(dbpres[i]);
          	}
      	}else{
      		dblist=userView.getPrivDbList();
      	}
        //wangcq 2014-12-09 end
		String sexpr=(String)reqhm.get("sexpr");
		sexpr=sexpr!=null?sexpr:"";
		sexpr = SafeCode.decode(sexpr);
		sexpr = PubFunc.keyWord_reback(sexpr);
		
		String sfactor=(String)this.getFormHM().get("sfactor");
		sfactor=sfactor!=null?sfactor:"";
		sfactor = PubFunc.keyWord_reback(sfactor);
		sfactor = PubFunc.reBackWord(sfactor);
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		StringBuffer tablestr = new StringBuffer();
		
		ArrayList fieldlist = new ArrayList();
		FieldItem fielditem1 = new FieldItem();
		fielditem1.setItemid("a0100");
		fielditem1.setItemtype("A");
		fielditem1.setFieldsetid("a01");
		fielditem1.setCodesetid("0");
		fieldlist.add(fielditem1);
		FieldItem fielditem2 = new FieldItem();
		fielditem2.setItemid("dbname");
		fielditem2.setItemtype("A");
		fielditem2.setFieldsetid("a01");
		fielditem2.setCodesetid("0");
		fielditem2.setItemdesc(ResourceFactory.getProperty("workbench.info.import.error.havedb"));
		fieldlist.add(fielditem2);
		FieldItem fielditem = DataDictionary.getFieldItem("a0101", "a01");
		fieldlist.add(fielditem);
		fielditem = DataDictionary.getFieldItem("b0110", "a01");
		fieldlist.add(fielditem);
		fielditem = DataDictionary.getFieldItem("e0122", "a01");
		fieldlist.add(fielditem);
		fielditem = DataDictionary.getFieldItem("e01a1", "a01");
		fieldlist.add(fielditem);
		
		if(sfactor.length()>0&&sexpr.length()>0&&dblist!=null){
			for(int i=0;i<dblist.size();i++){
				String dbpre = (String)dblist.get(i);
				if(dbpre==null||dbpre.length()<1)
					continue;
				
				String dbname="";
				String dbid="";
				try {
					this.frowset = dao.search("select DbId,DBName from dbname where Pre='"+dbpre+"'");
					if(this.frowset.next()){
						dbid=this.frowset.getString("dbid");
						dbname=this.frowset.getString("dbname");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(sexpr.startsWith("*"))
					sexpr=sexpr.substring(1);
				
				String strWhere=userView.getPrivSQLExpression(sexpr.toString()+"|"+PubFunc.getStr(sfactor.toString()),dbpre,false,true,new ArrayList());
				
				StringBuffer buf = new StringBuffer();
				String tablename=dbpre+"A01";
				
				buf.append("select " + tablename + ".A0100," + tablename + ".A0101," + tablename + ".B0110," + tablename + ".E0122," 
						+ tablename + ".E01A1,");
				buf.append("'" + dbpre + "' as dbpre,'");
				buf.append(dbname + "' as dbname,'" + dbid + "' as dbid");
				buf.append(strWhere);
				
				if(tablestr!=null&&tablestr.length()>1)
					tablestr.append(" union ");
				tablestr.append(buf.toString());
			}
		}
        this.getFormHM().put("sqlstr",tablestr.toString());
        this.getFormHM().put("titlelist",fieldlist);
	}
	
}
