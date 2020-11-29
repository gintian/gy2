package com.hjsj.hrms.transaction.org.orgdata;

import com.hjsj.hrms.businessobject.general.info.EmpMaintenanBo;
import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.businessobject.org.orgdata.OrgDataBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

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
public class OrgDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap reqhm = (HashMap) this.getFormHM().get("requestPamaHM");
		
		String a_code = (String)reqhm.get("a_code");
		a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
		a_code= "all".equalsIgnoreCase(a_code)?"":a_code;
		reqhm.remove("a_code");
		
		String infor = (String)reqhm.get("infor");
		infor=infor!=null&&infor.trim().length()>0?infor:"2";
		reqhm.remove("infor");
		
		OrgDataBo orgbo = new OrgDataBo(this.frameconn,this.userView);
		ArrayList fieldlist = new ArrayList();
		
		String viewsearch=(String)this.getFormHM().get("viewsearch");
		viewsearch=viewsearch!=null&&viewsearch.trim().length()>0?viewsearch:"0";
		
		String viewdata = (String)this.getFormHM().get("viewdata");
		viewdata=viewdata!=null&&viewdata.trim().length()>0?viewdata:"0";
		
		String sort_fields = (String)this.getFormHM().get("sort_str");
		sort_fields=sort_fields!=null&&sort_fields.trim().length()>0?sort_fields:"";
		
		
		
		String maintable = "";
		String mainitem = "";
		String temptable = "";
		if("2".equals(infor)){
			maintable = "B01";
			mainitem = "B0110";
			temptable=this.userView.getUserName()+"Bresult";
			fieldlist = this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
		}else if("3".equals(infor)){
			maintable = "K01";
			mainitem = "E01A1";
			fieldlist = this.userView.getPrivFieldSetList(Constant.POS_FIELD_SET);
			temptable=this.userView.getUserName()+"Kresult";
		}else{
			maintable = "B01";
			mainitem = "B0110";
			temptable=this.userView.getUserName()+"Bresult";
			fieldlist = this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
		}
		GzAmountXMLBo xmlbo = new GzAmountXMLBo(this.getFrameconn(),0);
		String base_set = xmlbo.getValue("base_set");
		String ins_base_set = xmlbo.getValue("ins_base_set");
		xmlbo = new GzAmountXMLBo(this.getFrameconn(),1);
		HashMap gzhm = xmlbo.getValuesMap();//薪资总额子集
		String setid = "";
		if (gzhm != null)
			setid = (String) gzhm.get("setid");
		ArrayList list = new ArrayList();
		for(int i=0;i<fieldlist.size();i++){
			FieldSet fieldset = (FieldSet)fieldlist.get(i);
			if(fieldset!=null){
				if(base_set.toLowerCase().indexOf(fieldset.getFieldsetid().toLowerCase())!=-1)
					continue;
				if(ins_base_set.toLowerCase().indexOf(fieldset.getFieldsetid().toLowerCase())!=-1)
					continue;
				if(setid!=null&&setid.trim().length()>1&&fieldset.getFieldsetid().equalsIgnoreCase(setid))
					continue;
				list.add(fieldset);
			}
		}
		fieldlist = list;
		
		String setname = (String)this.getFormHM().get("setname");
		setname=setname!=null?setname:"";
		if(setname.length()<1&&fieldlist.size()>0){
			FieldSet fieldset = (FieldSet)fieldlist.get(0);
			setname = fieldset.getFieldsetid();
		}
		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		ArrayList itemlist = orgbo.itemList(fieldset,infor);
		String reserveitem = "";
		for(int i=0;i<itemlist.size();i++){
			Field field = (Field)itemlist.get(i);
			if(field!=null&&field.getLabel().indexOf("<font color='red'>*</font>")!=-1){
				if(!field.isReadonly()){
					reserveitem+=field.getName()+",."+field.getLabel().replace("<font color='red'>*</font>", "")+"`";
				}
			}
		}
		if("B00".equalsIgnoreCase(fieldset.getFieldsetid())|| "K00".equalsIgnoreCase(fieldset.getFieldsetid())){
			itemlist.addAll(orgbo.a00ItemList(infor));
		}
		this.getFormHM().put("reserveitem", reserveitem);
		String priItem = this.userView.analyseTablePriv(setname);
		
		String tablename = setname;

		GzDataMaintBo gzbo = new GzDataMaintBo(this.frameconn,this.userView);

		EmpMaintenanBo embo = new EmpMaintenanBo(this.getFrameconn());
		StringBuffer strsql=new StringBuffer();
		if(setname!=null&&setname.trim().length()>0){
			strsql.append("select '' oper,"+gzbo.vilStr(setname,gzbo.fieldList(setname)));
			strsql.append(gzbo.whereStr(setname,a_code,viewdata));
			if(sort_fields.trim().length()>0){
				if(!gzbo.checkOrderbyStr(sort_fields,setname)){
					sort_fields = "";
				}
			}
			if(viewsearch!=null&& "1".equals(viewsearch)){
				if(embo.checkResult(temptable)){
					strsql.append(" and "+maintable+"."+mainitem+" in (select "+mainitem+" from ");
					strsql.append(temptable+")");
				}else{
					strsql.append(" and 1=2");
				}
			}
			if(sort_fields.trim().length()>0){
				strsql.append(" order by ");
				strsql.append(gzbo.getoMianOrderbyStr(sort_fields));
			}	
		}
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String checkadd="1";
		String arr[] = a_code.split(":");
		if(arr.length==3){
			checkadd = "0";
		}else{
			if(fieldset.isMainset()){
				if(isSqlNull(dao,a_code,maintable,mainitem)){
					checkadd = "0";
				}
				if(!"3".equals(infor)){
					if(!isSelfNull(dao,a_code,maintable,mainitem)&&a_code.trim().length()>2)
						checkadd = "1";
				}else{
					if(!isSelfNull(dao,a_code,maintable,mainitem)&&a_code.trim().length()>2&&a_code.indexOf("@K")!=-1)
						checkadd = "1";
				}
			}else{
				if(!isOrgNull(dao,a_code,infor)){
					checkadd = "0";
				}
			}
			if("3".equals(infor)){
				if(a_code.indexOf("@K")==-1){
					checkadd = "0";
				}
			}
		}
		
		ArrayList setlist = orgbo.setList(fieldlist);
		this.getFormHM().put("setlist",setlist);
		this.getFormHM().put("setname",setname);
		this.getFormHM().put("itemlist",itemlist);
		this.getFormHM().put("tablename",tablename);
		this.getFormHM().put("a_code",a_code);
		this.getFormHM().put("selectsql",strsql.toString());
		this.getFormHM().put("viewsearch",viewsearch);
		this.getFormHM().put("sort_str",sort_fields);
		this.getFormHM().put("viewdata",viewdata);
		this.getFormHM().put("searchlist",searchTable(dao,infor));
		this.getFormHM().put("checkadd",checkadd);
		this.getFormHM().put("infor",infor);
		this.getFormHM().put("priItem",priItem);
	}
	private ArrayList searchTable(ContentDAO dao,String type){
		ArrayList searchlist = new ArrayList();
		
		String sqlstr = "select id,name from LExpr where Type="+type;
		try {
			this.frowset=dao.search(sqlstr);
			int n=1;
			while(this.frowset.next()){
				if(!(this.userView.isHaveResource(IResourceConstant.LEXPR,this.frowset.getString("id"))))
                	continue;
				CommonData job=new CommonData();
				job.setDataName(this.frowset.getString("id"));
				job.setDataValue(this.frowset.getString("id")+"."+this.frowset.getString("name"));
				searchlist.add(job);
				n++;
				if(n>7)
					break;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return searchlist;
	}
	private boolean isSqlNull(ContentDAO dao,String a_code,String maintable,String mainitem){
		boolean flag=true;
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select "+mainitem+" from "+maintable);
		sqlstr.append(" where "+mainitem+" like '");
		if(a_code!=null&&a_code.length()>2)
			sqlstr.append(a_code.substring(2,a_code.length())+"%'");
		else
			sqlstr.append("%'");
		try {
			this.frowset = dao.search(sqlstr.toString());
			if(!this.frowset.next()){
				flag=false;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}
	private boolean isOrgNull(ContentDAO dao,String a_code,String infor){
		boolean flag=true;
		StringBuffer sqlstr = new StringBuffer();
		try {
			
			if(infor!=null&& "2".equals(infor)){
				sqlstr.append("select * from organization where parentid=");
				if(a_code!=null&&a_code.length()>2){
					sqlstr.append("'"+a_code.substring(2,a_code.length())+"'");
				}else{
					sqlstr.append("codeitemid");
				}
				sqlstr.append(" and codesetid<>'@K'");
			}else{
				sqlstr.append("select * from organization where codeitemid=");
				if(a_code!=null&&a_code.length()>2){
					sqlstr.append("'"+a_code.substring(2,a_code.length())+"'");
				}else{
					sqlstr.append("codeitemid");
				}
			}
			this.frowset = dao.search(sqlstr.toString());
			if(!this.frowset.next()){
				flag=false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}
	private boolean isSelfNull(ContentDAO dao,String a_code,String maintable,String mainitem){
		boolean flag=true;
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select "+mainitem+" from "+maintable);
		sqlstr.append(" where "+mainitem+"='");
		if(a_code!=null&&a_code.length()>2)
			sqlstr.append(a_code.substring(2,a_code.length())+"'");
		else
			sqlstr.append("'");
		try {
			this.frowset = dao.search(sqlstr.toString());
			if(!this.frowset.next()){
				flag=false;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}

}
