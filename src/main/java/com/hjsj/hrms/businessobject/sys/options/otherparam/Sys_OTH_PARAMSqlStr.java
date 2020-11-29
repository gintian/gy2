package com.hjsj.hrms.businessobject.sys.options.otherparam;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Sys_OTH_PARAMSqlStr {
	public String[] getDbnameSQL(){
		String[] sqlstr=new String[4];
		String sql="select dbname,pre";
		String where=null;
		/**oracle应用库前缀的乱,需要加上排序 chenmengqing changed at 20100424
		 * 但在mssql环境下,select count(*) as ncount from dbname order by dbid 有错
		 */
		if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
            where ="from dbname order by dbid";
        } else {
            where ="from dbname ";
        }
		String column="dbname,pre";
		String orderby="";
		sqlstr[0]=sql;
		sqlstr[1]=where;
		sqlstr[2]=column;
		sqlstr[3]=orderby;
		return sqlstr;
	}
	public String getA01Code(){
		String sql="select * from fielditem where fieldsetid='A01' and codesetid<>'0' and useflag='1' and itemid<>'E0122'";
		return sql ;
	}
	public String[] getCodeitem(String codesetid){
		String[] sqlstr=new String[4];
		String sql="select codeitemid,codeitemdesc";
		String where="from codeitem where codesetid='"+codesetid+"'";
		String column="codeitemid,codeitemdesc";
		String orderby=" order by CodeItemId";
		sqlstr[0]=sql;
		sqlstr[1]=where;
		sqlstr[2]=column;
		sqlstr[3]=orderby;
		return sqlstr;
	}
	public String  getA01Codelist(ContentDAO dao,String sel) throws GeneralException{
		List l=dao.searchDynaList(this.getA01Code());
		StringBuffer sbSel=new StringBuffer();
		sbSel.append("<select name='codesetid' onchange='getcodesetid();'>");
		for(Iterator it=l.iterator();it.hasNext();){
			DynaBean dynabean=(DynaBean)it.next();
			String code=(String) dynabean.get("codesetid");
			if(code.equalsIgnoreCase(sel)){
				sbSel.append("<option value='"+dynabean.get("codesetid")+"/"+dynabean.get("itemid")+"' selected='selected'>");
				sbSel.append(dynabean.get("itemdesc"));
				sbSel.append("</option>");
			}else{
				sbSel.append("<option value='"+dynabean.get("codesetid")+"/"+dynabean.get("itemid")+"'>");
				sbSel.append(dynabean.get("itemdesc"));
				sbSel.append("</option>");
			}
		}
		sbSel.append("</select>");
		return sbSel.toString();
	}
	/**
	 * 人员主集选择非代码字符型指标
	 * @param dao
	 * @return
	 * @throws Exception
	 */
	public ArrayList getSrc(ContentDAO dao) throws Exception{
		ArrayList al=new ArrayList();
		List l=dao.searchDynaList("select * from fielditem where fieldsetid='A01' and useflag='1' and codesetid='0' and itemtype='A'");
		for(Iterator it=l.iterator();it.hasNext();){
			DynaBean dynabean=(DynaBean)it.next();
			CommonData dataobj = new CommonData();
			dataobj.setDataName((String) dynabean.get("itemdesc"));
			dataobj.setDataValue((String) dynabean.get("itemid"));
			al.add(dataobj);
		}
		return al;
	}
	/**
	 * 人员主集选择日期指标
	 * @param dao
	 * @return
	 * @throws Exception
	 */
	public ArrayList getDestBirth(ContentDAO dao) throws Exception{
		ArrayList al=new ArrayList();
		List l=dao.searchDynaList("select * from fielditem where fieldsetid='A01' and useflag='1' and itemtype='D'");
		for(Iterator it=l.iterator();it.hasNext();){
			DynaBean dynabean=(DynaBean)it.next();
			CommonData dataobj = new CommonData();
			dataobj.setDataName((String) dynabean.get("itemdesc"));
			dataobj.setDataValue((String) dynabean.get("itemid"));
			al.add(dataobj);
		}
		return al;
	}
	/**
	 * 人员主集数据值型指标
	 * @param dao
	 * @return
	 * @throws Exception
	 */
	public ArrayList getAge(ContentDAO dao) throws Exception{
		ArrayList al=new ArrayList();
		List l=dao.searchDynaList("select * from fielditem where fieldsetid='A01' and useflag='1' and itemtype='N'");
		for(Iterator it=l.iterator();it.hasNext();){
			DynaBean dynabean=(DynaBean)it.next();
			CommonData dataobj = new CommonData();
			dataobj.setDataName((String) dynabean.get("itemdesc"));
			dataobj.setDataValue((String) dynabean.get("itemid"));
			al.add(dataobj);
		}
		return al;
	}
	public ArrayList getAx(ContentDAO dao) throws Exception{
		ArrayList al=new ArrayList();
		String sql="select * from fielditem where fieldsetid='A01' and useflag='1' and codesetid='AX' and itemtype='A'";
		List l=dao.searchDynaList(sql);
		for(Iterator it=l.iterator();it.hasNext();){
			DynaBean dynabean=(DynaBean)it.next();
			CommonData dataobj = new CommonData();
			dataobj.setDataName((String) dynabean.get("itemdesc"));
			dataobj.setDataValue((String) dynabean.get("itemid"));
			al.add(dataobj);
		}
		return al;
	}
}
