package com.hjsj.hrms.transaction.org.orgpre;

import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
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
public class DepTableTrans extends IBusiness {

	public void execute() throws GeneralException {
	    try{
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String setid = (String)hm.get("setid");
		hm.remove("setid");
		
		String a_code = (String)hm.get("a_code");
		hm.remove("a_code");
		
		String b0110 = (String)hm.get("b0110");
		hm.remove("b0110");
		
		String infor = (String)hm.get("infor");
		hm.remove("infor");
		
		String unit_type = (String)hm.get("unit_type");
		hm.remove("unit_type");
		
		String nextlevel = (String)hm.get("nextlevel");
		nextlevel=nextlevel!=null&&nextlevel.trim().length()>0?nextlevel:"0";
		hm.remove("nextlevel");
		
		String dpname="";
		String codesetid="UN";
		if(a_code.indexOf("UN")!=-1)
			codesetid="UN";
		else if(a_code.indexOf("UM")!=-1)
			codesetid="UM";
		else if(a_code.indexOf("@K")!=-1)
			codesetid="K";
		dpname=AdminCode.getCodeName(b0110,codesetid);
		if(dpname==null||dpname.trim().length()<1)
			dpname=codeName(codesetid,b0110);
		dpname=dpname!=null&&dpname.trim().length()>0?dpname:b0110;
		
		StringBuffer sql = new StringBuffer();
		sql.append("select  ");
		StringBuffer wherestr = new StringBuffer();
		StringBuffer columns = new StringBuffer();
		StringBuffer orderby = new StringBuffer();
		
		GzDataMaintBo gzbo = new GzDataMaintBo(this.frameconn,this.userView);
		ArrayList list = gzbo.fieldList(setid);
		ArrayList resultlist = new ArrayList();
		FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
		for(int i=0;i<list.size();i++){
			Field fielditem = (Field)list.get(i);
			if("b0110".equals(fielditem.getName()))
				continue;
			String pri = this.userView.analyseFieldPriv(fielditem.getName());
			if("0".equals(pri))
				continue;
			
			if("i9999".equals(fielditem.getName().toLowerCase()))
			  //sql.append("Row_Number() OVER(order by i9999) i9999"); sql2005以下版本不支持Row-Number函数
				continue;
			else
			  sql.append(fielditem.getName());
			columns.append(fielditem.getName());
			//if(i<list.size()-1){
				columns.append(",");
				sql.append(",");
			//}
			
			FieldItem item = DataDictionary.getFieldItem(fielditem.getName());
			if("i9999".equalsIgnoreCase(item.getItemid()))
				item.setItemdesc(ResourceFactory.getProperty("kjg.gather.xuhao"));
			
			resultlist.add(item);
		}
        if (columns.length()>0)
                columns.delete(columns.length()-1, columns.length());
            else {
                
                throw GeneralExceptionHandler.Handle(new Exception("无指标权限,不能查看！"));
        }
		sql.delete(sql.length()-1, sql.length());
		wherestr.append(" from ");
		wherestr.append(setid);
		wherestr.append(" where ");
		if("B".equalsIgnoreCase(setid.substring(0,1))){
			wherestr.append(" B0110='");
			wherestr.append(b0110);
			wherestr.append("'");
		}else{
			wherestr.append(" where E01A1='");
			wherestr.append(b0110);
			wherestr.append("'");
		}
		if(!fieldset.isMainset())
			orderby.append("order by I9999");
		
		
		this.getFormHM().put("setid",setid);
		this.getFormHM().put("a_code",a_code);
		this.getFormHM().put("infor",infor);
		this.getFormHM().put("unit_type",unit_type);
		this.getFormHM().put("sql",sql.toString());
		this.getFormHM().put("wherestr",wherestr.toString());
		this.getFormHM().put("columns",columns.toString());
		this.getFormHM().put("orderby",orderby.toString());
		this.getFormHM().put("resultlist",resultlist);
		this.getFormHM().put("dpname",dpname);
		this.getFormHM().put("nextlevel",nextlevel);
        }catch(Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
     }
	}
	private String codeName(String codeid,String codeitem){
		String codename="";
		String sql = "select codeitemdesc from organization where codeitemid='"+codeitem+"'";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next())
				codename=this.frowset.getString("codeitemdesc");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return codename;
	}

}
