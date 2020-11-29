package com.hjsj.hrms.transaction.train.postAnalyse;

import com.hjsj.hrms.businessobject.info.SortFilter;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.TrainInfoUtils;
import com.hjsj.hrms.businessobject.train.station.TrainStationBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 不符合本岗位培训要求
 * <p>Title:NotAccordPostInfoTrans.java</p>
 * <p>Description>:NotAccordPostInfoTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:May 23, 2011 2:34:20 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class NotAccordPostInfoTrans extends IBusiness {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String code=(String)this.getFormHM().get("code");
		if(!"@K".equalsIgnoreCase(code)&&(code==null||code.length()<3||"root".equalsIgnoreCase(code))){
			TrainCourseBo bo = new TrainCourseBo(this.userView);
			code = bo.getUnitIdByBusi().replaceAll("`", ",");
		}
		code = "root".equalsIgnoreCase(code)||"UN,".equalsIgnoreCase(code) ? null : code;
		String chwhere = (String)this.getFormHM().get("chwhere");//条件查询
		this.getFormHM().remove("chwhere");
		
		String flag=(String)this.getFormHM().get("flag");
		String classid=(String)this.getFormHM().get("classid");
		classid = PubFunc.keyWord_reback(classid);
		String query=(String)this.getFormHM().get("query");     
		String userbase=(String)this.getFormHM().get("dbpre");
		flag=flag!=null&&flag.length()>0?flag:"1";//1本单位；2 非本单位
		String codes[]=null;
		if(code!=null)
			codes=code.split(",");
		String kind="";
		TrainStationBo trainStationBo=new TrainStationBo();
		HashMap map=trainStationBo.getStationSett(this.getFrameconn());
		String postSetId=(String)map.get("post_setid");//岗位培训子集
		String postCloumn=(String)map.get("post_coursecloumn");//岗位培训子集中参培课程指标
		String empSetId=(String)map.get("emp_setid");;//人员培训子集
		String empCloumn=(String)map.get("emp_coursecloumn");//人员培训子集中参培课程指标
		String emp_passcloumn=(String)map.get("emp_passcloumn");//人员培训结果指标
		String emp_passvalues=(String)map.get("emp_passvalues");//人员培训合格结果值
		if(code!=null&&code.length()>0&&codes!=null&&codes.length>0)
		{
			StringBuffer columns=new StringBuffer();
			StringBuffer strsql=new StringBuffer();
			StringBuffer ensql=new StringBuffer();
			ensql.append(" where ");
			strsql.append("select a0000,");		    
			strsql.append("a0100");
			columns.append("A0100,a0000");
			String fieldstr=new SaveInfo_paramXml(this.getFrameconn()).getInfo_paramNode("browser");
			if(fieldstr!=null&&fieldstr.length()>0)
			{
				 fieldstr=new SortFilter().getBrowseFields(fieldstr,userbase,this.getFrameconn(),this.userView);
				 if(fieldstr==null||fieldstr.length()<=0){				
						fieldstr=",B0110,E0122,E01A1,A0101,UserName";				
				 }
				 if(fieldstr.indexOf("state")!=-1)
			     {
			    	   fieldstr=fieldstr+",state";
			     }		
			     strsql.append(fieldstr);
				 columns.append(fieldstr);      
		           					
			}else{
				fieldstr=",B0110,E0122,E01A1,A0101,UserName,state";
				strsql.append(",B0110,E0122,E01A1,A0101,UserName,state");
				columns.append(",B0110,E0122,E01A1,A0101,UserName,state");			
			}
			ArrayList infoFieldList=new ArrayList();
			String flag22="infoself";
			if("infoself".equalsIgnoreCase(flag22)&&this.userView.getStatus()==1)
			{
				infoFieldList=userView.getPrivFieldList("A01",0);   //获得当前子集的所有属性
			}
		    else
			{	
				infoFieldList=userView.getPrivFieldList("A01");      //获得当前子集的所有属性
			}
			ArrayList fields=new ArrayList();//
			String[] f=fieldstr.split(",");
			for(int i=0;i<f.length;i++){
				for(int j=0;j<infoFieldList.size();j++){
					FieldItem fieldItem=(FieldItem)infoFieldList.get(j);
					if(fieldItem.getPriv_status() !=0)                //只加在有读写权限的指标
					{
						if(f[i].equalsIgnoreCase(fieldItem.getItemid()))
						{
							fields.add(fieldItem);
						}
					}
				}
			}
			this.getFormHM().put("fieldstr", fieldstr);			
			this.getFormHM().put("browsefields",fields);
			this.getFormHM().put("cloumn",columns.toString());			
			StringBuffer buf=new StringBuffer();
			String term_Sql="";
			String org_field="";
			TrainInfoUtils trainInfoUtils=new TrainInfoUtils();
			ArrayList classlist=new ArrayList();
			if(codes.length==1)
			{
				code=codes[0];
				String codesetid=code.substring(0,2);
				String itemid=code.substring(2);
				if("@K".equalsIgnoreCase(codesetid))
					classlist=trainInfoUtils.getPostClassList(this.getFrameconn(), itemid,postSetId,postCloumn);
			}
			StringBuffer passsql=new StringBuffer();
			if(emp_passcloumn!=null&&emp_passcloumn.length()>0&&emp_passvalues!=null&&emp_passvalues.length()>0)
	        {
				passsql.append(" and b."+emp_passcloumn+" in(");
				String p_values[]=emp_passvalues.split(",");
	        	for(int i=0;i<p_values.length;i++)
	        	{
	        		passsql.append("'"+p_values[i]+"',");
	        	}
	        	if(passsql.length()>0)
	        		passsql.setLength(passsql.length()-1);
	        	passsql.append(")");
	        }
			
			StringBuffer wherestr = new StringBuffer();
			
			for(int i=0;i<codes.length;i++)
			{
				code=codes[i];
				if(code.length()<=0)
					continue;
				String codesetid=code.substring(0,2);
				String itemid=code.substring(2);
				buf.append(strsql.toString());
				buf.append(" from "+userbase+"A01 ");
				if("UN".equals(codesetid))
				{
					kind="2";
					org_field="b0110";
				}
				else if("UM".equals(codesetid))				
				{
					kind="1";
					org_field="e0122";
				}	
				else{
					kind="0";	
					org_field="e01a1";
				}
				if("2".equals(flag)&&this.userView.isSuper_admin())
		        {
		        	term_Sql=getWhereSQLExists(this.getFrameconn(),this.userView,userbase,"",true,"","","","");
		        	term_Sql = term_Sql.replaceAll("AND 1=2", "");
		        }else        	
		        {
				   term_Sql=getWhereSQLExists(this.getFrameconn(),this.userView,userbase,itemid,true,kind,"","","");
				   term_Sql = term_Sql.replaceAll("AND 1=2", "");
		        }
				buf.append(" where exists(select a0100 from ("+term_Sql.toString()+")A where  "+userbase+"A01.a0100=A.a0100)");
				ensql.append(" (exists(select a0100 from ("+term_Sql.toString()+")A where  "+userbase+"A01.a0100=A.a0100)");
				if(!"1".equals(query)&& "2".equals(flag))
				{
//					buf.append(" and "+userbase+"A01."+org_field+"<>'"+itemid+"'");
//					ensql.append(" and "+userbase+"A01."+org_field+"<>'"+itemid+"'");
					buf.append(" and "+userbase+"A01."+org_field+" not like '"+itemid+"%'");
					ensql.append(" and "+userbase+"A01."+org_field+" not like '"+itemid+"%'");
				}else// if("1".equals(query))
				{
					buf.append(" and "+userbase+"A01."+org_field+" like '"+itemid+"%'");
					ensql.append(" and "+userbase+"A01."+org_field+" like '"+itemid+"%'");
				}
				if(!(classid!=null&&classid.length()>0/*&&!classid.equalsIgnoreCase("###")*/&&"3".equals(query)&& "2".equals(flag)))
				{
					if(userView.getStatus()==4)
					{
						String tabldName = "t_sys_result";
						Table table = new Table(tabldName);
						DbWizard dbWizard = new DbWizard(this.getFrameconn());
						if (!dbWizard.isExistTable(table)) {
							return;
						}
						
						buf.append(" and exists(select 1 from "+tabldName+" where "+tabldName+".obj_id="+userbase+"A01.a0100 ");
						buf.append(" and "+tabldName+".nbase='"+userbase+"')");
						
						ensql.append(" and exists(select 1 from "+tabldName+" where "+tabldName+".obj_id="+userbase+"A01.a0100 ");
						ensql.append(" and "+tabldName+".nbase='"+userbase+"')");
					}else
					{
						if("1".equals(query)){
							String tabldName=userView.getUserName()+userbase+"result";
							buf.append(" and exists(select 1 from "+tabldName+" where "+tabldName+".a0100="+userbase+"A01.a0100 )");
							ensql.append(" and exists(select 1 from "+tabldName+" where "+tabldName+".a0100="+userbase+"A01.a0100 )");
						} else{
							String a0100s = getA0100s(userbase, "", postSetId, postCloumn, empSetId, empCloumn, emp_passcloumn, emp_passvalues);
							buf.append(" and " + userbase + "A01.a0100 in(" + a0100s + ")");
							ensql.append(" and " + userbase + "A01.a0100 in(" + a0100s + ")");
						}
					}	
				}
				if(classid!=null&&classid.length()>0&&!"###".equalsIgnoreCase(classid))
				{
						
					if("1".equals(query))//不符合本岗位培训要求的人员数据
					{
						buf.append(" and not exists(select 1 from "+userbase+empSetId+" b where b.a0100="+userbase+"A01.a0100 and b."+empCloumn+"='"+classid+"' "+passsql+")");
						ensql.append(" and not exists(select 1 from "+userbase+empSetId+" b where b.a0100="+userbase+"A01.a0100 and b."+empCloumn+"='"+classid+"' "+passsql+")");
						//System.out.println(empSetId+"--"+"--"+empCloumn+"--"+classid);
					}else if("2".equals(query)||"4".equals(query))
					{
						//正在分析按本岗位培训要求匹配的人员数据
						buf.append(" and exists(select 1 from "+userbase+empSetId+" b where b.a0100="+userbase+"A01.a0100 and b."+empCloumn+"='"+classid+"' "+passsql+")");
						ensql.append(" and exists(select 1 from "+userbase+empSetId+" b where b.a0100="+userbase+"A01.a0100 and b."+empCloumn+"='"+classid+"' "+passsql+")");
					}else if("3".equals(query))
					{
						//正在分析按非本岗位培训要求匹配的人员数据
						buf.append(" and exists(select 1 from "+userbase+empSetId+" b where b.a0100="+userbase+"A01.a0100 and b."+empCloumn+"='"+classid+"' "/*+passsql*/+passsql+")");
						ensql.append(" and exists(select 1 from "+userbase+empSetId+" b where b.a0100="+userbase+"A01.a0100 and b."+empCloumn+"='"+classid+"' "/*+passsql*/+passsql+")");
					}
				}else if("###".equalsIgnoreCase(classid)){
				    if("1".equals(query))//不符合本岗位培训要求的人员数据
                    {
                        buf.append(" and not exists(select 1 from "+userbase+empSetId+" b where b.a0100="+userbase+"A01.a0100 "+passsql+")");
                        ensql.append(" and not exists(select 1 from "+userbase+empSetId+" b where b.a0100="+userbase+"A01.a0100 "+passsql+")");
                        //System.out.println(empSetId+"--"+"--"+empCloumn+"--"+classid);
                    }else if("2".equals(query)||"4".equals(query))
                    {
                        //正在分析按本岗位培训要求匹配的人员数据
                        buf.append(" and exists(select 1 from "+userbase+empSetId+" b where b.a0100="+userbase+"A01.a0100 "+passsql+")");
                        ensql.append(" and exists(select 1 from "+userbase+empSetId+" b where b.a0100="+userbase+"A01.a0100 "+passsql+")");
                    }else if("3".equals(query))
                    {
                        //正在分析按非本岗位培训要求匹配的人员数据,全部课程
                        if(classlist!=null&&classlist.size()>1)
                        {
                            for(int r=1;r<classlist.size();r++)
                            {
                                CommonData da=(CommonData)classlist.get(r);
                                buf.append(" and exists(select 1 from "+userbase+empSetId+" b where b.a0100="+userbase+"A01.a0100 and b."+empCloumn+"='"+da.getDataValue()+"' "+passsql+")");
                                ensql.append(" and exists(select 1 from "+userbase+empSetId+" b where b.a0100="+userbase+"A01.a0100 and b."+empCloumn+"='"+da.getDataValue()+"' "+passsql+")");
                            }                           
                        }else {
                            buf.append(" and exists(select 1 from "+userbase+empSetId+" b where b.a0100="+userbase+"A01.a0100 "+passsql+")");
                            ensql.append(" and exists(select 1 from "+userbase+empSetId+" b where b.a0100="+userbase+"A01.a0100 "+passsql+")");
                        }
                    }
				}
				
				if("@K".equalsIgnoreCase(code) || code.indexOf("@K") == -1){
				    StringBuffer getE01a1 = new StringBuffer();
                    getE01a1.append("select 1 from ");
                    getE01a1.append(postSetId);
                    
                    if (Sql_switcher.searchDbServer() == Constant.ORACEL)
                        getE01a1.append(" ea where " + postCloumn + " is not null");
                    else
                        getE01a1.append(" ea where " + Sql_switcher.isnull(postCloumn, "''") + "<>''");
                    
                    buf.append(" and exists("+getE01a1+" and ea.e01a1="+userbase+"A01.e01a1)");
                    ensql.append(" and exists("+getE01a1+" and ea.e01a1="+userbase+"A01.e01a1)");
				}
				
				if(chwhere!=null&&chwhere.length()>0&&!"3".equals(query)){
					buf.append(" and a0100 in(select a0100 "+PubFunc.keyWord_reback(chwhere)+")");
					ensql.append(" and a0100 in(select a0100 "+PubFunc.keyWord_reback(chwhere)+")");
				}
				
				buf.append(" union ");
				ensql.append(") or ");
				
				if(i>0)
				    wherestr.append(" and "+userbase+"A01."+org_field+" not like '"+itemid+"%'");
			}
			buf.setLength(buf.length()-7);
			ensql.setLength(ensql.length()-3);
			
			if("3".equals(query) && buf.indexOf("union")>-1)
			{
			    buf.setLength(buf.indexOf("union", 0));
			    buf.append(wherestr);
			    ensql.setLength(ensql.indexOf(") or ", 0));
			    ensql.append(wherestr);
			    
			    String a0100s = getA0100s(userbase, "", postSetId, postCloumn, empSetId, empCloumn, emp_passcloumn, emp_passvalues);
                buf.append(" and " + userbase + "A01.a0100 in(" + a0100s + ")");
                ensql.append(" and " + userbase + "A01.a0100 in(" + a0100s + "))");
			    
			}
			//xiexd 2014.09.25将sql保存至服务器
			this.userView.getHm().put("muster_excel_sql", SafeCode.encode(ensql.toString()));
			this.getFormHM().put("ensql",SafeCode.encode(ensql.toString()));
			this.getFormHM().put("where","");
			this.getFormHM().put("classlist", classlist);
			this.getFormHM().put("sqlstr",buf.toString());
		}else
		{   
			StringBuffer passsql=new StringBuffer();
			if(emp_passcloumn!=null&&emp_passcloumn.length()>0&&emp_passvalues!=null&&emp_passvalues.length()>0)
	        {
				passsql.append(" and b."+emp_passcloumn+" in(");
				String p_values[]=emp_passvalues.split(",");
	        	for(int i=0;i<p_values.length;i++)
	        	{
	        		passsql.append("'"+p_values[i]+"',");
	        	}
	        	if(passsql.length()>0)
	        		passsql.setLength(passsql.length()-1);
	        	passsql.append(")");
	        }
			
			if(code!=null&&code.length()>0)
			{
				kind="0";
			}
			StringBuffer columns=new StringBuffer();
			StringBuffer strsql=new StringBuffer();
			strsql.append("select "+userbase+"A01.a0000 a0000,");
		    strsql.append(userbase);
			strsql.append("A01.A0100 a0100");
			columns.append("A0100,a0000");
			String fieldstr=new SaveInfo_paramXml(this.getFrameconn()).getInfo_paramNode("browser");
			if(fieldstr!=null&&fieldstr.length()>0)
			{
				 fieldstr=new SortFilter().getBrowseFields(fieldstr,userbase,this.getFrameconn(),this.userView);
				 if(fieldstr==null||fieldstr.length()<=0){				
						fieldstr=",B0110,E0122,E01A1,A0101,UserName";				
				 }
				 if(fieldstr.indexOf("state")!=-1)
			     {
			    	   fieldstr=fieldstr+",state";
			     }		
			     strsql.append(fieldstr);
				 columns.append(fieldstr);      
		           					
			}else{
				fieldstr=",B0110,E0122,E01A1,A0101,UserName,state";
				strsql.append(",B0110,E0122,E01A1,A0101,UserName,state");
				columns.append(",B0110,E0122,E01A1,A0101,UserName,state");			
			}
			ArrayList infoFieldList=new ArrayList();
			String flag22="infoself";			
			if("infoself".equalsIgnoreCase(flag22)&&this.userView.getStatus()==1)
			{
				infoFieldList=userView.getPrivFieldList("A01",0);   //获得当前子集的所有属性
			}
		    else
			{	
				infoFieldList=userView.getPrivFieldList("A01");      //获得当前子集的所有属性
			}
			ArrayList fields=new ArrayList();
			String[] f=fieldstr.split(",");
			for(int i=0;i<f.length;i++){
				for(int j=0;j<infoFieldList.size();j++){
					FieldItem fieldItem=(FieldItem)infoFieldList.get(j);
					if(fieldItem.getPriv_status() !=0)                //只加在有读写权限的指标
					{
						if(f[i].equalsIgnoreCase(fieldItem.getItemid()))
						{
							fields.add(fieldItem);
						}
					}
				}
			}
			this.getFormHM().put("fieldstr", fieldstr);			
			this.getFormHM().put("browsefields",fields);
			this.getFormHM().put("cloumn",columns.toString());			
		    this.getFormHM().put("sqlstr",strsql.toString());
		    StringBuffer buf=new StringBuffer();
	        String term_Sql="";
	        if("2".equals(flag)&&this.userView.isSuper_admin())
	        {
	        	term_Sql=getWhereSQLExists(this.getFrameconn(),this.userView,userbase,"",true,"","","","");  
	        }else        	
	        {
	        	term_Sql=getWhereSQLExists(this.getFrameconn(),this.userView,userbase,code,true,kind,"","",""); 
	        	term_Sql = term_Sql.replaceAll("AND 1=2", "");
	        }      
	        if(term_Sql!=null&&term_Sql.length()>0)
			   buf.append(" where exists(select a0100 from ("+term_Sql.toString()+")A where  "+userbase+"A01.a0100=A.a0100)");
	        else 
	        {
	        	if(!this.userView.isSuper_admin())
	        		throw GeneralExceptionHandler.Handle(new GeneralException("","操作用户没有管理范围权限！","",""));	        		
	        	else
	        		buf.append(" where 1=1" );
	        }	        
			if("2".equals(flag))
			{
				buf.append(" and "+userbase+"A01.e01a1<>'"+code+"'");
			}
			if(!(classid!=null&&classid.length()>0&&!"###".equalsIgnoreCase(classid)&&"3".equals(query)&& "2".equals(flag)))
			{
				if(userView.getStatus()==4)
				{
					String tabldName = "t_sys_result";
					Table table = new Table(tabldName);
					DbWizard dbWizard = new DbWizard(this.getFrameconn());
					if (!dbWizard.isExistTable(table)) {
						return;
					}
					
					buf.append(" and exists(select 1 from "+tabldName+" where "+tabldName+".obj_id="+userbase+"A01.a0100 ");
					buf.append(" and "+tabldName+".nbase='"+userbase+"')");
				}else
				{
					if ("1".equals(query)) {
						String tabldName = userView.getUserName() + userbase + "result";
						buf.append(" and exists(select 1 from " + tabldName + " where " + tabldName + ".a0100=" + userbase + "A01.a0100 )");
					} else {
						String a0100s = getA0100s(userbase, "", postSetId, postCloumn, empSetId, empCloumn, emp_passcloumn, emp_passvalues);
						buf.append(" and " + userbase + "A01.a0100 in(" + a0100s + ")");
					}
				}	
			}	
			if(classid!=null&&classid.length()>0&&!"###".equalsIgnoreCase(classid))
			{
					
				if("1".equals(query))//不符合本岗位培训要求的人员数据
				{
					buf.append(" and not exists(select 1 from "+userbase+empSetId+" b where b.a0100="+userbase+"A01.a0100 and b."+empCloumn+"='"+classid+"' "+passsql+")");

				}else if("2".equals(query))
				{
					//正在分析按本岗位培训要求匹配的人员数据
					buf.append(" and exists(select 1 from "+userbase+empSetId+" b where b.a0100="+userbase+"A01.a0100 and b."+empCloumn+"='"+classid+"' "+passsql+")");
					
				}else if("3".equals(query))
				{
					//正在分析按非本岗位培训要求匹配的人员数据
					buf.append(" and exists(select 1 from "+userbase+empSetId+" b where b.a0100="+userbase+"A01.a0100 and b."+empCloumn+"='"+classid+"' "+passsql+")");
				}else if("4".equals(query))
				{
					//正在分析按本岗位培训要求匹配的人员数据
					buf.append(" and exists(select 1 from "+userbase+empSetId+" b where b.a0100="+userbase+"A01.a0100 and b."+empCloumn+"='"+classid+"' "+passsql+")");
				}
			}		
			if(chwhere!=null&&chwhere.length()>0){
				buf.append(" and a0100 in(select a0100 "+PubFunc.keyWord_reback(chwhere)+")");
			}
			TrainInfoUtils trainInfoUtils=new TrainInfoUtils();
			ArrayList classlist=trainInfoUtils.getPostClassList(this.getFrameconn(), code,postSetId,postCloumn);
			//xiexd 2014.09.25将sql保存至服务器
			this.userView.getHm().put("muster_excel_sql", SafeCode.encode(buf.toString()));
			this.getFormHM().put("ensql",SafeCode.encode(buf.toString()));	
			this.getFormHM().put("where"," from "+userbase+"A01 "+buf.toString());
			this.getFormHM().put("classlist", classlist);
		}
		
	}
	/**
     * 兼职子集没有A000字段时
     * @param userbase
     * @param code
     * @param kind
     * @param orgtype
     * @param personsortfield
     * @param personsort
     * @param part_unit
     * @param part_setid
     * @param part_appoint
     * @param where_n
     * @return
     * @throws GeneralException
     */
    public String getWhereSQLExists(Connection conn,UserView userView,String userbase,String code,boolean isCodeLike,String kind,String orgtype,String personsortfield
        	,String personsort)throws GeneralException 
        	   
    {
    	StringBuffer union_Sql=new StringBuffer();
    	StringBuffer wheresql=new StringBuffer();
    	String main_Tablename=userbase+"A01";
    	String strwhere="";   
    	String strsqlA010="select A.*";	
		if(userView.isSuper_admin()){                    //超级用户	
			//生成没有高级条件的from后的sql语句
			if(!"vorg".equals(orgtype))
			{
				wheresql.append(" from ");
				wheresql.append(userbase);
				wheresql.append("A01 A");
				if("2".equals(kind) && code!=null && code.length()>0)
				{
					if(isCodeLike)
					{
						wheresql.append(" where ((b0110 like '");
					    wheresql.append(code);
					    wheresql.append("%'");
					}else
					{
						wheresql.append(" where ((b0110 = '");
					    wheresql.append(code);
					    wheresql.append("'");
					}					
				}			   
				else if("1".equals(kind)  && code!=null && code.length()>0)
				{
					if(isCodeLike)
					{
						wheresql.append(" where ((e0122 like '"); 
						wheresql.append(code);
						wheresql.append("%'");
					}else
					{
						wheresql.append(" where ((e0122 = '"); 
						wheresql.append(code);
						wheresql.append("'");
					}
					
				}				
				else if("0".equals(kind) && code!=null && code.length()>0)
				{
					if(isCodeLike)
					{
						wheresql.append(" where ((e01a1 like '");
						wheresql.append(code);
						wheresql.append("%'");
					}else
					{
						wheresql.append(" where ((e01a1 = '");
						wheresql.append(code);
						wheresql.append("'");
					}
					
					
				}else if("2".equals(kind))
				{
					wheresql.append(" where ((1=1");
				}
				else
				{
					wheresql.append(" where ((1=1");
				}
			    strwhere=wheresql.toString();
			    if(personsortfield!=null && !"null".equalsIgnoreCase(personsortfield) && personsortfield.length()>0&&personsort!=null&&!"All".equalsIgnoreCase(personsort))
			    {
			    	strwhere=strwhere + " and " + personsortfield + "='" + personsort + "'";
			    }		
			    union_Sql.append(strsqlA010.toString());
			    union_Sql.append(strwhere);	
			    //if(personsort==null||personsort.length()<=0)
			    {
			    	DbNameBo dbbo=new DbNameBo(conn);
			    	String strWhere=dbbo.getQueryFromPartTimeLikeExists(userView, userbase, code,isCodeLike,"A",kind);
			    	if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
			    	{
			    		union_Sql.append(" or exists( ");		    		
			    		union_Sql.append(strWhere);	
			    		union_Sql.append(")");
			    	}
			    }			    	
		    	union_Sql.append(")");		    	
		    	union_Sql.append(")");		    	
			}else
			{
				wheresql.append(" from ");
				wheresql.append(userbase);
				wheresql.append("A01 A,vorganization v,t_vorg_staff b");
				wheresql.append(" where ((v.codeitemid='"+code+"'"); 
				wheresql.append(" and b.B0110= v.codeitemid ");
				wheresql.append(" and b.state=1 ");				
				wheresql.append(" and Upper(b.dbase)='"+userbase.toUpperCase()+"'");
				wheresql.append(" and A.A0100=b.a0100");
				strwhere=wheresql.toString();;
				union_Sql.append(strsqlA010.toString());
			    union_Sql.append(strwhere);			
			    //union_Sql.append(" UNION ");
			    DbNameBo dbbo=new DbNameBo(conn);
		    	String strWhere=dbbo.getQueryFromPartTimeLikeExists(userView, userbase, code,isCodeLike,"A","");
		    	if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
		    	{
		    		union_Sql.append(" or exists( ");		    		
		    		union_Sql.append(strWhere);	
		    		union_Sql.append(")");
		    	}
		    	union_Sql.append(")");
		    	union_Sql.append(")");		    	
			}
			
		}
		else{   
			if(!"vorg".equals(orgtype))
			{
			  ArrayList fieldlist=new ArrayList();
		        try
		        {
		          /* String expr="1";
		           String factor="";
		           kind="2";
		           code="21";
		           if("2".equals(kind))
		           {
		    		 factor="B0110=";
		    		 if(code!=null && code.length()>0)
					 {
						 factor+=code;
						 if(isCodeLike)
						   factor+="%`";
						 else
						   factor+="`"; 
					 }
					 else
					 {
					    expr="1+2";
					    factor+=code;
					    factor+="%`B0110=`";	
					 }
		           }
				   else if("1".equals(kind)){
				   	    factor="E0122="; 
				   	 if(code!=null && code.length()>0)
					 {
						 factor+=code;
						 if(isCodeLike)
							factor+="%`";
					     else
						    factor+="`"; 
					 }
					 else
					 {
					    expr="1+2";
					   factor+=code;
					   factor+="%`E0122=`";	
					 }
				   }
				   else if("0".equals(kind)){
				   	    factor="E01A1=";
				   	    if(code!=null && code.length()>0)
					    {
						 factor+=code;
						 if(isCodeLike)
							 factor+="%`";
					     else
							 factor+="`"; 
					    }
					     else
					   {
					      expr="1+2";
					      factor+=code;
					      factor+="%`E01A1=`";	
					    }
				   }else
				   {
					   if("UN".equals(userView.getManagePrivCode()))
						{
						   factor="B0110=";
						}else if("UM".equals(userView.getManagePrivCode()))
						{
							   factor="E0121=";
						}else if("@K".equals(userView.getManagePrivCode()))
						{
							   factor="E01A1=";
						}
				   }*/
		        	   String expr="1";
			           String factor="";
			           TrainCourseBo tb = new TrainCourseBo(userView);
			           String priv = tb.getUnitIdByBusi();
			           String tmp[] = priv.split("`");
			           for (int i = 0; i < tmp.length; i++) {
							String t = tmp[i];
							if(t.startsWith("UN"))
							{
								factor+="B0110=";
								kind="2";
								if(t.length()>2)
								{
									factor+=t.substring(2);
									factor+="%`";
								}
								else
								{
									factor+="%`B0110=`";
									//expr=expr+"+"+(++m);
								}
							}
							else if(t.startsWith("UM"))
							{
								factor="E0122="; 
								kind="1";
								if(t.length()>2)
								{
									factor+=t.substring(2);
									factor+="%`";
								}
								else
								{
									factor+="%`E0122=`";
									//expr="1+2";
								}
							}
							else if(t.startsWith("@K"))
							{
								factor="E01A1=";
								kind="0";
								if(t.length()>2)
								{
									factor+=t.substring(2);
									factor+="%`";
								}
								else
								{
									factor+="%`E01A1=`";
									//expr="1+2";
								}
							}
							else
							{
								//expr="1+2";
								factor="B0110=";
								kind="2";
								if(t.length()>2)
									factor+=t.substring(2);
								factor+="%`B0110=`";
							}
					   }
			           if(factor.length()>0){
			        	   for (int j = 1; j < factor.split("`").length; j++) {
			        		   expr=expr+"+"+(j+1);
			        	   }
			           }
		           if(factor==null||factor.length()<=0)
		        	   return "";
		            /**表过式分析*/
		            /**非超级用户且对人员库进行查询*/
		            strwhere=userView.getPrivSQLExpression(expr+"|"+factor,userbase,false,isCodeLike,true,fieldlist);		            
		            strwhere=getPrivSqlExists(strwhere,main_Tablename,"A");
		            if(personsortfield!=null && !"null".equalsIgnoreCase(personsortfield) && personsortfield.length()>0&&personsort!=null&&!"All".equalsIgnoreCase(personsort))
			    		strwhere=strwhere + " and " + personsortfield + "='" + personsort + "'";
		            union_Sql.append(strsqlA010.toString());
				    union_Sql.append(strwhere);
				    //if(personsort==null||personsort.length()<=0)
				    {
				    	DbNameBo dbbo=new DbNameBo(conn);				    	
				    	String strWhere=dbbo.getQueryFromPartTimeLikeExists(userView, userbase, code,kind,isCodeLike,"A");
				    	if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
				    	{
				    		union_Sql.append(" or exists( ");		    		
				    		union_Sql.append(strWhere);	
				    		union_Sql.append(")");
				    	}
				    }				    			    	
		        }catch(Exception e){
		          e.printStackTrace();	
		        }
			}else
	        {
				wheresql.append(" from ");
				wheresql.append(userbase);
				wheresql.append("A01 A,vorganization v,t_vorg_staff b");
				wheresql.append(" where ((v.codeitemid='"+code+"'"); 
				wheresql.append(" and b.state=1 ");	
				wheresql.append(" and b.B0110= v.codeitemid ");
				wheresql.append(" and Upper(b.dbase)='"+userbase.toUpperCase()+"'");
				wheresql.append(" and A.A0100=b.a0100");
				strwhere=wheresql.toString();;
				union_Sql.append(strsqlA010.toString());
			    union_Sql.append(strwhere);			
			    //union_Sql.append(" UNION ");
			    DbNameBo dbbo=new DbNameBo(conn);
			    String strWhere=dbbo.getQueryFromPartTimeLikeExists(userView, userbase, code,isCodeLike,"A","");
		    	if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
		    	{
		    		union_Sql.append(" or exists( ");		    		
		    		union_Sql.append(strWhere);	
		    		union_Sql.append(")");
		    	}	
		    	union_Sql.append(")");
		    	union_Sql.append(")");		    	
	        }
		}			
		return union_Sql.toString();
    }
    public String getPrivSqlExists(String strwhere,String old_table,String replace_table)
	{
		String s_top=strwhere.substring(0,strwhere.indexOf(old_table)+6);
        strwhere=strwhere.substring(strwhere.indexOf(old_table)+6);		            
        strwhere=strwhere.replaceAll(old_table, replace_table);
        strwhere=s_top+" "+replace_table+" "+strwhere;
        return strwhere;
	}
    /**
     * 获取当前用户的result表中符合岗位培训要求的a0100
     * @param userbase
     * @param codes
     * @param postSetId
     * @param postCloumn
     * @param empSetId
     * @param empCloumn
     * @param emp_passcloumn
     * @param emp_passvalues
     * @return
     */

    public String getA0100s(String userbase,String codes,String postSetId,String postCloumn,String empSetId,String empCloumn,
    		String emp_passcloumn,String emp_passvalues){
    	
    	String a0100s="";
    	String a0100="";
    	String e01a1="";
    	String emp_passvalues1="";
    	String[] emp_passvalues0 = emp_passvalues.split(",");
    	for(int i=0;i<emp_passvalues0.length;i++){
    		if(emp_passvalues0[i]!=null&&emp_passvalues0[i].length()>0)
    			emp_passvalues1+="'"+emp_passvalues0[i]+"',";
    	}
    	String tabldName=userView.getUserName()+userbase+"result";
    	 if(this.userView.getStatus()==4)
    	     tabldName="t_sys_result";
              
    	ContentDAO dao=new ContentDAO(this.frameconn);
    	StringBuffer buf = new StringBuffer();
    	TrainInfoUtils trainInfoUtils = new TrainInfoUtils();
    	ArrayList classlist=new ArrayList();
    	RowSet rs= null;
    	String sql = "select a.a0100 a0100,a.e01a1 e01a1 from "+tabldName+" b join "+userbase+"a01 a on";
    	if(this.userView.getStatus()==4)
    	    sql += " a.a0100=b.obj_id and b.nbase='"+userbase+"' and b.username='"+userView.getUserName()+"'";
    	else
    	    sql += " a.a0100=b.a0100";
    	
    	try{
    		this.frowset= dao.search(sql);
    		while(this.frowset.next()){
    			a0100=this.frowset.getString("a0100");
    			e01a1=this.frowset.getString("e01a1");
    			
    			buf.delete(0, buf.length());
    			buf.append("select distinct a0100 from "+userbase+empSetId+" where a0100='"+a0100+"' and ");
    			
    			
    			classlist.clear();
    			classlist=trainInfoUtils.getPostClassList(this.getFrameconn(),e01a1,postSetId,postCloumn);
    			if(classlist!=null&&classlist.size()>1){
					for(int r=1;r<classlist.size();r++){
						CommonData da=(CommonData)classlist.get(r);
						buf.append("exists(select 1 from "+userbase+empSetId+" where a0100='"+a0100+"' and "+empCloumn+"='"+da.getDataValue()+"' and "+emp_passcloumn+" in ("+emp_passvalues1.substring(0, emp_passvalues1.length()-1)+")) and ");
					}	
				}
				buf.setLength(buf.length()-4);
    			rs=dao.search(buf.toString());
    			if(rs.next())
    				a0100s+="'"+rs.getString("a0100")+"',";
    		}
    	}catch (Exception e) {
    		e.printStackTrace();
    	}
    	if(a0100s!=null&&a0100s.length()>0)
    		a0100s=a0100s.substring(0, a0100s.length()-1);
    	else
    		a0100s="null";
    	return a0100s;
    }
}
