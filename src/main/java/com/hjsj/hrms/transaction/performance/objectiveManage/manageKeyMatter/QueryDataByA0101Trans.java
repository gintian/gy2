package com.hjsj.hrms.transaction.performance.objectiveManage.manageKeyMatter;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.performance.objectiveManage.manageKeyMatter.KeyMatterBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.analyse.IParserConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * <p>Title:QueryDataByA0101Trans.java</p>
 * <p>Description:按姓名进行查询</p> 
 * <p>Company:hjsj</p> 
 * create time at:May 04, 2011 15:36:18 PM
 * @author JinChunhai
 * @version 5.0
 */

public class QueryDataByA0101Trans extends IBusiness 
{	
	
	public void execute() throws GeneralException 
	{
		String a0101=(String)this.getFormHM().get("objectName");
		String infor_type=(String)this.getFormHM().get("objecType");
		String kind=(String)this.getFormHM().get("kind");
		String orgcode=(String)this.getFormHM().get("code");
		orgcode = PubFunc.keyWord_reback(orgcode);
		
	//	System.out.println(orgcode+"hhhhhh");		
		
		if(infor_type==null || infor_type.trim().length()<=0)
			infor_type="1";

		a0101=SafeCode.decode(a0101);
		if(a0101.indexOf("'")!=-1)
		{
			String str = a0101;
			String str1 = "'"; 
			String str2 = str.replaceAll(str1,"‘"); 			
			a0101=str2;
		}
		
		int maxlength=0;
		ArrayList objlist=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if("2".equals(infor_type))
			{
				String filter_factor=(String)this.getFormHM().get("filter_factor");
				if(filter_factor!=null&&filter_factor.trim().length()>0)
					filter_factor=SafeCode.decode(filter_factor);
								
				/** 是否加权限控制：=1加=0不加*/
				String isPriv="1";
				if(this.getFormHM().get("isPriv")!=null)
				{
					isPriv=(String)this.getFormHM().get("isPriv");
					this.getFormHM().remove("isPriv");
				}
				KeyMatterBo bo = new KeyMatterBo(this.getFrameconn());
				ArrayList dblist=bo.getUsrDbNameVoList();																	
					
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
				String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
				
				if(onlyname!=null && onlyname.trim().length()>0 && !"#".equals(onlyname))
				{
					FieldItem fielditem = DataDictionary.getFieldItem(onlyname);
					String useFlag = fielditem.getUseflag(); 
					if("0".equalsIgnoreCase(useFlag))
						throw new GeneralException("定义的唯一性指标未构库,请构库后再进行此操作！");	
				}
				
/*				String valid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
				if(valid.equals("0"))
					onlyname="";
*/				
				int i=0; 
				if(!(a0101==null|| "".equalsIgnoreCase(a0101)))
				{
					String _onlyname="";
					if(onlyname!=null && onlyname.trim().length()>0)
						_onlyname=","+onlyname;
					RowSet rset=dao.search(getQueryString(a0101,dblist,filter_factor,isPriv,_onlyname,kind,orgcode));			
					while(rset.next())
					{
						if(i>40)
							break;
						CommonData objvo=new CommonData();
						String b0110=rset.getString("b0110");
						String name=AdminCode.getCodeName("UN",b0110);

						String ename="";
						String e0122=rset.getString("e0122");
						if(e0122!=null)
							ename=AdminCode.getCodeName("UM", e0122);
						if(ename!=null&&!"".equals(ename))
							name+="/"+ename;
						
/*						String kname="";
						String e01a1=rset.getString("e01a1");
						if(e01a1!=null)
							kname=AdminCode.getCodeName("@K", e01a1);
						if(kname!=null&&!kname.equals(""))
							ename+="/"+kname;
							
						name+="/"+ename;
*/						
						String onlyValue = "";
						if(onlyname!=null && onlyname.trim().length()>0)
						{														
							String only_value=rset.getString(onlyname)==null?"":rset.getString(onlyname);							
							if(only_value!=null && only_value.trim().length()>0)
								onlyValue = " ("+only_value+")";							
						}
						
						if(name.length()>maxlength)
							maxlength=name.length();
						
						if(onlyValue!=null && onlyValue.trim().length()>0)
							objvo.setDataName("("+rset.getString("a0101")+") "+ name + onlyValue);
						else
							objvo.setDataName("("+rset.getString("a0101")+") "+ name);
						
						objvo.setDataValue(rset.getString("a0100")+"`"+b0110+"`"+e0122+"`"+rset.getString("a0101"));
						objlist.add(objvo);
						++i;
					}
				}
				
			}else if("1".equals(infor_type))
			{				
				String sql="select * from organization where codeitemdesc like '%"+a0101+"%' ";
				
				StringBuffer str = new StringBuffer(); 			
				if (orgcode != null && kind != null)
				{
				    if ("0".equals(kind))
						str.append(getUnitPrivWhere(this.userView));
				    else if ("1".equals(kind))//部门
				    	str.append(" and codesetid <> '@K' and codeitemid like '" + orgcode+ "%' ");
				    else if ("2".equals(kind))//单位
				    	str.append(" and codesetid <> '@K' and codeitemid like '" + orgcode+ "%' ");			    
				    else if ("3".equals(kind))//人员
				    	str.append(getUnitPrivWhere(this.userView));
				    else if ("4".equals(kind))//操作单位
				    	str.append(getUnitPrivWhere(this.userView));
				    				    
				    sql+=str.toString();
				}								
		
				Calendar d=Calendar.getInstance();
				int yy=d.get(Calendar.YEAR);
				int mm=d.get(Calendar.MONTH)+1;
				int dd=d.get(Calendar.DATE); 
				sql+=" and ( "+Sql_switcher.year("end_date")+">"+yy;
				sql+=" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ";
				sql+=" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ";
				
				sql+=" and ( "+Sql_switcher.year("start_date")+"<"+yy;
				sql+=" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ";
				sql+=" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ";
							
				if("1".equals(infor_type))
					sql+=" and ( codesetid='UM' or codesetid='UN' ) ";
//				else if(infor_type.equals("3"))
//					sql+=" and codesetid='@K' ";
				sql+=" order by a0000";
				this.frowset=dao.search(sql);
				while(this.frowset.next())
				{
					String name=this.frowset.getString("codeitemdesc")+"("+this.frowset.getString("codeitemid")+")";
					String value=this.frowset.getString("codeitemid")+"`"+this.frowset.getString("codeitemdesc");
					if(name.length()>maxlength)
						maxlength=name.length();
					CommonData objvo=new CommonData(value,name);
					objlist.add(objvo);
				}
			}
			 
			this.getFormHM().put("objlist",objlist);
			this.getFormHM().put("objecType",infor_type);
			this.getFormHM().put("maxlength", ""+maxlength);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/*
	 *  //wlh修改添加检索过滤
	 */
	private String getFilterSQL(UserView uv,String BasePre,ArrayList alUsedFields,Connection conn1,String filter_factor)
	{
		String sql=" (1=1)";
		try
		{
			filter_factor=filter_factor.replaceAll("@","\"");
			ContentDAO dao=new ContentDAO(conn1);
			//this.filterfactor="性别 <> '1'";
			int infoGroup = 0; // forPerson 人员
			int varType = 8; // logic								
			String whereIN=InfoUtils.getWhereINSql(uv,BasePre);
			whereIN="select a0100 "+whereIN;							
			YksjParser yp = new YksjParser( uv ,alUsedFields,
					YksjParser.forSearch, varType, infoGroup, "Ht", BasePre);
			YearMonthCount ymc=null;							
			yp.run_Where(filter_factor, ymc,"","hrpwarn_resulta", dao, whereIN,conn1,"A", null);
			String tempTableName = yp.getTempTableName();			
			sql=yp.getSQL();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}	
		return sql;
	}
	
	private String getQueryString(String a0101,ArrayList dblist,String filter_factor,String isPriv,String onlyname,String kind,String orgCode)throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
    	ArrayList fieldlist=new ArrayList();
		String strWhere=null;    
		String sexpr="1";
		String sfactor="A0101="+a0101+"*`";
		/**加权限过滤*/
		ArrayList alUsedFields=null;
		if(filter_factor!=null && !"undefined".equalsIgnoreCase(filter_factor)&& !"null".equalsIgnoreCase(filter_factor) &&  filter_factor.trim().length()>0)
	    { 
			alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			/**
			 *保持和以前的程序兼容，因为先前单位编码和职位编码、
			 *单位名称和职位名称未统一起来 
			 */
			FieldItem item=new FieldItem();
			item.setItemid("b0110");
			item.setCodesetid("UN");
			item.setItemdesc("单位编码");
			item.setItemtype("A");
			item.setFieldsetid("A01");
			item.setUseflag("2");
			item.setItemlength(30);
			item.setItemlength(30);
			alUsedFields.add(item);

			item=new FieldItem();
			item.setItemid("e01a1");
			item.setCodesetid("@K");
			item.setItemdesc("职位编码");
			item.setItemtype("A");
			item.setFieldsetid("A01");
			item.setUseflag("2");
			item.setItemlength(30);
			item.setItemlength(30);
			alUsedFields.add(item);		  
	    }
		DbNameBo dbnamebo=new DbNameBo(this.getFrameconn());
  	    for(int i=0;i<dblist.size();i++)
 	    {
  	    	RecordVo vo=(RecordVo)dblist.get(i);
  	    	String pre=vo.getString("pre");
    		strWhere=userView.getPrivSQLExpression(sexpr+"|"+sfactor,pre,false,fieldlist);
  	    	buf.append("select "+pre+"a01.a0000, "+pre+"a01.a0101,"+pre+"a01.b0110,"+pre+"a01.a0100,"+pre+"a01.e0122,"+pre+"a01.e01a1, '");
  	    	buf.append(vo.getString("pre"));
  	    	buf.append("' as dbpre "+onlyname);
  	    	/** 是否加权限控制：=1加=0不加*/
//  	    	if(isPriv.equals("1"))
  	    	{
//  	        	buf.append(strWhere);  
  	    	}
//  	    	else
  	    	{
  	    		buf.append(" from "+vo.getString("pre")+"a01 where ");
  	    		buf.append(vo.getString("pre")+"a01.a0101 like '"+a0101+"%' ");
  	    	}
  	    	
  	    	StringBuffer str = new StringBuffer(); 			
			if (orgCode != null && kind != null)
			{
			    if ("0".equals(kind))
			    {
					String emps = searchEmpByPosition(orgCode,pre);
					if(emps.length()>0)
						str.append(" and a0100 in(" + emps  + ") ");
					
			    }else if ("1".equals(kind))//部门
			    	str.append(" and e0122 like '" + orgCode+ "%' ");
			    else if ("2".equals(kind))//单位
			    	str.append(" and b0110 like '" +  orgCode + "%' ");
			    else if ("3".equals(kind))//人员
			    	str.append(" and a0100 ='" + orgCode + "' ");
			    else if ("4".equals(kind))//操作单位
			    	str.append(" " + orgCode + " ");
			    
			    buf.append(str.toString());
			}
  	    	
  	    	//wlh修改添加检索过滤
     	    if(filter_factor!=null && !"null".equalsIgnoreCase(filter_factor) &&  filter_factor.trim().length()>0)
			{ 
     	    	buf.append(" and " + dbnamebo.getComplexCond(this.userView, pre, alUsedFields, filter_factor, IParserConstant.forPerson)/*getFilterSQL(userView,vo.getString("pre"),alUsedFields,this.getFrameconn(),filter_factor)*/);
			}
  	    	buf.append(" UNION ");
 	    }
  	    buf.setLength(buf.length()-7);
  	    buf.append(" order by dbpre desc,a0000");
		return buf.toString();
	}
	
	public String searchEmpByPosition(String orgcode,String userbase)
	{
		StringBuffer emps = new StringBuffer();	
		String strSql = "select a0100 from "+userbase+"a01 where e01a1='" + orgcode + "'";
		List list = ExecuteSQL.executeMyQuery(strSql);
		for (int i=0;i<list.size();i++)
		{
			DynaBean bean = (DynaBean) list.get(i);
			emps.append(",'"+(String) bean.get("a0100")+"'");	    
		}
		if(emps.length()>1)
			return emps.substring(1);
		else
			return "";	
	}	
	
	/**
	 * 根据用户权限获得计划列表 先看操作单位 再看管理范围
	 * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
	 */
	public String getUnitPrivWhere(UserView userView)
	{
		String str = "";
//		if (!userView.isSuper_admin())
		{
			StringBuffer buf = new StringBuffer();
			String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if (operOrg!=null && operOrg.length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++)
				{
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or  codeitemid like '" + temp[i].substring(2) + "%'");
					else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or  codeitemid like '" + temp[i].substring(2) + "%'");
				}
				buf.append(" and codesetid <> '@K' and (" + tempSql.substring(3) + " ) ");
				
			} 
			else if((!userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{
				String codeid = userView.getManagePrivCode();
				String codevalue = userView.getManagePrivCodeValue();
				String a_code = codeid + codevalue;

				if (a_code.trim().length() > 0)// 说明授权了
				{
					if ("UN".equalsIgnoreCase(a_code))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
						buf.append(" and 1=1 ");
					else
						buf.append(" and codesetid <> '@K' and codeitemid like '" + codevalue + "%'");
				} else
					buf.append(" and 1=2 ");
			}
			str = buf.toString();
		}

		return str;
	}
}

