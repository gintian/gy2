package com.hjsj.hrms.transaction.general.query.complex;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.transaction.query.AutoCreateQueryResultTable;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public class SearchInterfaceResultTrans extends IBusiness {
	
	
	public void execute() throws GeneralException 
	{
		ArrayList dbaselist=userView.getPrivDbList();    
		String dbpre=(String)this.getFormHM().get("dbpre");
		String comple_db=(String)this.getFormHM().get("comple_db");
		if(dbaselist==null||dbaselist.size()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("","没有授权人员库！","",""));
		if(comple_db == null || "".equals(comple_db) || "ALL".equals(comple_db)){ //全部用户库数据(对人员要加权限)
			dbaselist=userView.getPrivDbList();  
		}else
		{
			dbaselist=new ArrayList();
			dbaselist.add(comple_db);
		}
		ArrayList browsefields=(ArrayList)this.getFormHM().get("browsefields");
		String fieldstr=(String)this.getFormHM().get("fieldstr");
		String userTableName="";		
		StringBuffer sbSQLselect=new StringBuffer();		
		String strWhere="";
		InfoUtils infoUtils = new InfoUtils();
		String privCodeValue = "";
		String privCode = "";
        if(!this.userView.isSuper_admin()) {
        	privCodeValue = this.userView.getManagePrivCodeValue();
        	privCode = this.userView.getManagePrivCode();
        }
        
        String kind = "2";
        if("UM".equalsIgnoreCase(privCode))
        	kind = "1";
        else if("@k".equalsIgnoreCase(privCode))
        	kind = "0";
         
		if(dbpre == null || "".equals(dbpre) || "ALL".equals(dbpre)){ //全部用户库数据(对人员要加权限)
			dbpre="";
			for(int i=0;i<dbaselist.size();i++)
			{
                String strPre = (String) dbaselist.get(i);
                userTableName=this.userView.getUserName()+strPre+"Result";
                if(this.userView.getStatus()==4)
				{
                	 userTableName="t_sys_result";
				}else{
					AutoCreateQueryResultTable.execute(this.getFrameconn(), userTableName, "1");
				}
                
				sbSQLselect.append(" select ");
				sbSQLselect.append(i+" as i,'");
				sbSQLselect.append(strPre);	
				sbSQLselect.append("' nbase, ");	
				sbSQLselect.append(strPre + "a01.a0100,");
				if(fieldstr!=null&&fieldstr.length()>0)
				{
					String[] f=fieldstr.split(",");
					for(int r=0;r<f.length;r++){
						if(f[r]!=null&&f[r].length()>0)
						{
							sbSQLselect.append(strPre + "a01."+f[r]+",");
						}	
					}
				}else
				{
					sbSQLselect.append(strPre + "a01.a0101 , ");
					sbSQLselect.append(strPre + "a01.b0110 , ");
					sbSQLselect.append(strPre + "a01.e0122 , ");
					sbSQLselect.append(strPre + "a01.e01a1, ");		
				}
				
				sbSQLselect.append(""+strPre + "a01.a0000 ");	
				String term_Sql = infoUtils.getWhereSQLExists(this.frameconn,this.userView,strPre,privCodeValue,true,kind,"org","","All");
				if(StringUtils.isNotEmpty(term_Sql))
				    sbSQLselect.append("  from (" + term_Sql + ")" + strPre + "a01");
				else
				    sbSQLselect.append("  from " + strPre + "a01");
				    
				sbSQLselect.append(","+userTableName);
				if(this.userView.getStatus()==4){
					 sbSQLselect.append(" where " + strPre + "a01.a0100="+userTableName+".obj_id and upper("+userTableName+".nbase)='"+strPre.toUpperCase()+"' and "+userTableName+".username='"+userView.getUserName()+"'" );
				}else{
					sbSQLselect.append(" where " + strPre + "a01.a0100="+userTableName+".a0100" );	
				}
				
				if (i < dbaselist.size() - 1) {
					sbSQLselect.append(" union all ");
				}
				
			}
		}else
		{
			userTableName=this.userView.getUserName()+dbpre+"Result";
			if(this.userView.getStatus()==4)
			{
            	 userTableName="t_sys_result";
			}else{
				AutoCreateQueryResultTable.execute(this.getFrameconn(), userTableName, "1");
			}
			
            String strPre = dbpre;			
			sbSQLselect.append(" select ");
			sbSQLselect.append("0 as i,'");
			sbSQLselect.append(strPre);	
			sbSQLselect.append("' nbase, ");				
			sbSQLselect.append(strPre + "a01.a0100,");
			if(fieldstr!=null&&fieldstr.length()>0)
			{
				String[] f=fieldstr.split(",");
				for(int r=0;r<f.length;r++){
					if(f[r]!=null&&f[r].length()>0)
					{
						sbSQLselect.append(strPre + "a01."+f[r]+",");
					}	
				}
			}else
			{
				sbSQLselect.append(strPre + "a01.a0101 , ");
				sbSQLselect.append(strPre + "a01.b0110 , ");
				sbSQLselect.append(strPre + "a01.e0122 , ");
				sbSQLselect.append(strPre + "a01.e01a1 ,");		
			}
			sbSQLselect.append(""+strPre + "a01.a0000 ");	
			
			String term_Sql = infoUtils.getWhereSQLExists(this.frameconn,this.userView,strPre,privCodeValue,true,kind,"org","","All");
			if(StringUtils.isNotEmpty(term_Sql))
			    sbSQLselect.append("  from (" + term_Sql + ")" + strPre + "a01");
			else
                sbSQLselect.append("  from " + strPre + "a01");
            
			sbSQLselect.append(","+userTableName);
			if(this.userView.getStatus()==4){
				 sbSQLselect.append(" where " + strPre + "a01.a0100="+userTableName+".obj_id and upper("+userTableName+".nbase)='"+strPre.toUpperCase()+"' and "+userTableName+".username='"+userView.getUserName()+"'" );
			}else{
				sbSQLselect.append(" where " + strPre + "a01.a0100="+userTableName+".a0100" );
			}
		}
		DbNameBo dbvo=new DbNameBo(this.getFrameconn());
		dbaselist=dbvo.getDbNameVoList(dbaselist);		
		ArrayList dbList=new ArrayList();
		for(int i=0;i<dbaselist.size();i++)
		{
			CommonData vo=new CommonData();
			RecordVo dbname=(RecordVo)dbaselist.get(i);
			vo.setDataName(dbname.getString("dbname"));
			vo.setDataValue(dbname.getString("pre"));
			dbList.add(vo);
		}
		this.getFormHM().put("dblist",dbList);
		StringBuffer columns = new StringBuffer();
		if(fieldstr!=null&&fieldstr.length()>0)
		{
			columns.append("nbase,A0100"+fieldstr);
		}else
		{
			columns.append("a0101,b0110,e0122,e01a1,a0100,nbase,");
		}		
		String order="";
		if(dbpre==null|| "".equals(dbpre)){
			if(dbaselist.size()>0){
				order="order by i,A0000";
			}else
				order="order by i";
		}else
			order="order by i,A0000";
		//System.out.println(sbSQLselect.toString());
		this.getFormHM().put("strsql", "select i,"+columns.toString()+" from ("+sbSQLselect.toString()+")t");
		this.getFormHM().put("columns",columns.toString());
		this.getFormHM().put("order", order);
		this.getFormHM().put("dbpre", dbpre);	
		this.getFormHM().put("comple_db", comple_db);
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String photo_other_view=sysbo.getValue(Sys_Oth_Parameter.PHOTO_OTHER_VIEW);
		if(photo_other_view==null||photo_other_view.length()<=0)
			photo_other_view="";
		this.getFormHM().put("photo_other_view", photo_other_view);
	} 	
}
