package com.hjsj.hrms.transaction.report.edit_report.static_statement;

import com.hjsj.hrms.businessobject.report.TgridBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
public class AutoGetDataSelectTrans extends IBusiness {

	public void execute() throws GeneralException {
	HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
	String tabid = (String)hm.get("tabid");
	String scopeid =(String)hm.get("scopeid");
	hm.remove("scopeid");
	//权限范围下的统计口径 
	//用户根据自己的操作单位是否与统计口径的所属机构相匹配来展现范围内的可选口径。
	//如果用户没有定义操作单位则按管理范围来匹配。
	ContentDAO dao=new ContentDAO(this.getFrameconn());
	ArrayList list = new ArrayList();
	StringBuffer  scopeidstr = new StringBuffer();
	try {
		this.frowset = dao.search("select * from tscope ");
		while(this.frowset.next()){
			list.add(this.frowset.getString("scopeid"));
	
		}
		for(int a=0;a<list.size();a++){
			String scopeid2 = (String)list.get(a);
			StringBuffer str = new StringBuffer(" select * from tscope where scopeid ="+scopeid2+" ");
			String temps="";
			
			if (!userView.isSuper_admin())
			{
				String operOrg = userView.getUnit_id();// 操作单位
				StringBuffer tempstr = new StringBuffer();
				if (operOrg.length() > 2)
				{
					String[] temp = operOrg.split("`");
					for (int i = 0; i < temp.length; i++)
					{
						if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))|| "UM".equalsIgnoreCase(temp[i].substring(0, 2))){
							tempstr.append(" or  owner_unit like 'UM" + temp[i].substring(2) + "%'");
							tempstr.append(" or  owner_unit like 'UN" + temp[i].substring(2) + "%'");
						}
					}
					if(tempstr.length()>3){
						temps+=tempstr.toString().substring(3);
					}
				} else
				{	//走管理范围
					
					String code = "-1";
					if (userView.getManagePrivCodeValue() != null && userView.getManagePrivCodeValue().length() > 0)// 管理范围
					{
						code = userView.getManagePrivCodeValue();
						if (code!=null)
							{
							if(code.indexOf("UN")!=-1||code.indexOf("UM")!=-1){
								tempstr.append(" or  owner_unit like 'UM" + code.substring(2) + "%'");
								tempstr.append(" or  owner_unit like 'UN" + code.substring(2) + "%'");
							}else{
								tempstr.append(" or  owner_unit like 'UN" + code + "%'");
								tempstr.append(" or  owner_unit like 'UM" + code + "%'");
							}
							}else{
								tempstr.append("and  1=2");
							}
							}else{
								tempstr.append("and  1=2");
							}
					if(tempstr.length()>3){
						temps+=tempstr.toString().substring(3);
					}
					}
			
			if(temps.length()>0){
				str.append(" and ("+temps+")");
			}
			this.frowset = dao.search(str.toString());
			if(this.frowset.next()){
				scopeidstr.append(","+scopeid2);
			}
			}else{
				scopeidstr.append(","+scopeid2);
			}
		}
		
		
		
	String scopeownerunit="";	//所属机构
	String scopeownerunitid="";	//所属机构id
	ArrayList unitslist = new ArrayList();
	ArrayList scopelist = new ArrayList();
	
	
	//判断是否存在scopeid字段
	DbWizard dbWizard=new DbWizard(this.getFrameconn());
	if(dbWizard.isExistField("tb"+tabid, "scopeid",false)){//判断字段是否存在，没则生成，同时付默认值0
	}else{
		TgridBo tgridBo=new TgridBo(this.getFrameconn());
		Table table=new Table("tb"+tabid);
		table.addField(tgridBo.getField2("scopeid","统计口径id","I"));
		dbWizard.addColumns(table);
		
		
			dao.update(" update tb"+tabid+" set scopeid=0 ");
		
	}
	//所属机构
	String sql = "";
	if(scopeidstr.toString().length()>0){
		sql = "select * from tscope  where scopeid in("+scopeidstr.substring(1)+") order by displayid";
	}else{
		sql = "select * from tscope where 1=2 order by displayid";
		throw GeneralExceptionHandler.Handle(new GeneralException("","权限范围下的统计口径不存在!","",""));
	}
		
		this.frowset = dao.search(sql);
		int count =0;
		while(this.frowset.next()){
			if(count==0){

				if("0".equals(scopeid)){
					scopeid=this.frowset.getString("scopeid");

					scopeownerunitid = this.frowset.getString("owner_unit");
					if(scopeownerunitid.indexOf("UN")!=-1||scopeownerunitid.indexOf("UM")!=-1){
						scopeownerunit= AdminCode.getCodeName(scopeownerunitid.substring(0,2),scopeownerunitid.substring(2,scopeownerunitid.length()));
						}
					String scopeunits =  Sql_switcher.readMemo(this.frowset, "units");
					String scopeunits2[] = scopeunits.split("`");
					for(int i=0;i<scopeunits2.length;i++){
						if(scopeunits2[i].indexOf("UN")!=-1||scopeunits2[i].indexOf("UM")!=-1){
							String scopeunitsname=AdminCode.getCodeName(scopeunits2[i].substring(0,2),scopeunits2[i].substring(2,scopeunits2[i].length()));
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("unitid",scopeunits2[i].substring(2,scopeunits2[i].length()));
					abean.set("unitname",scopeunitsname);
					unitslist.add(abean);
						}
				}
				
				}else{
					if(scopeid.equals(this.frowset.getString("scopeid"))){
						scopeownerunitid = this.frowset.getString("owner_unit");
						if(scopeownerunitid.indexOf("UN")!=-1||scopeownerunitid.indexOf("UM")!=-1){
							scopeownerunit= AdminCode.getCodeName(scopeownerunitid.substring(0,2),scopeownerunitid.substring(2,scopeownerunitid.length()));
							}
						String scopeunits = Sql_switcher.readMemo(this.frowset, "units");
						String scopeunits2[] = scopeunits.split("`");
						for(int i=0;i<scopeunits2.length;i++){
							if(scopeunits2[i].indexOf("UN")!=-1||scopeunits2[i].indexOf("UM")!=-1){
								String scopeunitsname=AdminCode.getCodeName(scopeunits2[i].substring(0,2),scopeunits2[i].substring(2,scopeunits2[i].length()));
						LazyDynaBean abean=new LazyDynaBean();
						abean.set("unitid",scopeunits2[i].substring(2,scopeunits2[i].length()));
						abean.set("unitname",scopeunitsname);
						unitslist.add(abean);
							}
					}
					}
				}
				CommonData data=new CommonData(this.frowset.getString("scopeid"),this.frowset.getString("name"));
				scopelist.add(data);
			
			}else{
			if(scopeid.equals(this.frowset.getString("scopeid"))){
				scopeownerunitid = this.frowset.getString("owner_unit");
				if(scopeownerunitid.indexOf("UN")!=-1||scopeownerunitid.indexOf("UM")!=-1){
					scopeownerunit=AdminCode.getCodeName(scopeownerunitid.substring(0,2),scopeownerunitid.substring(2,scopeownerunitid.length()));
					}
				String scopeunits =  Sql_switcher.readMemo(this.frowset, "units");
				String scopeunits2[] = scopeunits.split("`");
				for(int i=0;i<scopeunits2.length;i++){
					if(scopeunits2[i].indexOf("UN")!=-1||scopeunits2[i].indexOf("UM")!=-1){
						String scopeunitsname=AdminCode.getCodeName(scopeunits2[i].substring(0,2),scopeunits2[i].substring(2,scopeunits2[i].length()));
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("unitid",scopeunits2[i].substring(2,scopeunits2[i].length()));
				abean.set("unitname",scopeunitsname);
				unitslist.add(abean);
					}
			}
			}
			CommonData data=new CommonData(this.frowset.getString("scopeid"),this.frowset.getString("name"));
			scopelist.add(data);
			}
			count++;
		}
	this.getFormHM().put("scopeownerunit", scopeownerunit);
	this.getFormHM().put("unitslist", unitslist);
	this.getFormHM().put("scopelist", scopelist);
	this.getFormHM().put("scopeid", scopeid);
	this.getFormHM().put("tabid",tabid);
	//统计口径
	} catch (SQLException e) {
		e.printStackTrace();
	}
	}

}
