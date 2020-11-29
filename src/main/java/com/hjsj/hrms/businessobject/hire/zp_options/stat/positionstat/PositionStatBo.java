package com.hjsj.hrms.businessobject.hire.zp_options.stat.positionstat;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class PositionStatBo {
	private Connection conn;
	private ArrayList dataList = new ArrayList();
	public PositionStatBo(Connection conn)
	{
		this.conn=conn;
	}
	public ArrayList getRecords(String starttime,String endtime,ArrayList condlist,UserView userView )
	{
		ArrayList list = new ArrayList();
		try
		{
			ParameterXMLBo bo2 = new ParameterXMLBo(this.conn, "1");
			HashMap map0 = bo2.getAttributeValues();
			String hireMajor="";			//xieguiquan 2010-09-17
			if(map0.get("hireMajor")!=null) {
                hireMajor=(String)map0.get("hireMajor");  //招聘专业指标
            }
			boolean hireMajorIsCode=false;
			FieldItem hireMajoritem=null;
			if(hireMajor.length()>0)
			{
				hireMajoritem=DataDictionary.getFieldItem(hireMajor.toLowerCase());
				if(hireMajoritem.getCodesetid().length()>0&&!"0".equals(hireMajoritem.getCodesetid())) {
                    hireMajorIsCode=true;
                }
			}
			StringBuffer sql_select=new StringBuffer();
			if(hireMajor.length()>0&&",z0301,z0311,codeitemdesc,z0329,z0331,z0313,z0336".indexOf(","+hireMajor.toLowerCase())==-1){
				sql_select=new StringBuffer("select z0301,z0311,org1.codeitemdesc,org2.codeitemdesc un,org3.codeitemdesc um");
			 	sql_select.append(",z0329,z0331,z0313,z0336,"+hireMajor+"  ");
			}else{
				sql_select=new StringBuffer("select z0301,z0311,org1.codeitemdesc,org2.codeitemdesc un,org3.codeitemdesc um");
				sql_select.append(",z0329,z0331,z0313,z0336 ");
			}
			
			StringBuffer sql_from=new StringBuffer(" from z03 left join (select * from organization where codesetid='@K') org1  on z03.z0311=org1.codeitemid");
			sql_from.append(" left join (select * from organization where codesetid='UN') org2   on  z03.z0321=org2.codeitemid");
			sql_from.append(" left join (select * from organization where codesetid='UM') org3   on  z03.z0325=org3.codeitemid");
			
			/*if(starttime!=null&&!starttime.equals(""))
				sql_from.append(getDateSql("<=","z0329",endtime));
			if(endtime!=null&&!endtime.equals(""))
			{
				sql_from.append(" and ");
			    sql_from.append(getDateSql(">=","z0331",starttime));
			}*/
			String z0331="";
			String z0329="";
			if(starttime!=null&&!"".equals(starttime))
			{
				z0331 = getDateSql("<","z0331",starttime);
			}
			if(endtime!=null&&!"".equals(endtime))
			{
				z0329 = getDateSql(">","z0329",endtime);
			}
			if(!("".equals(z0331)|| "".equals(z0329)))
			{
				sql_from.append(" where z0301 not in(select z0301 from z03 where ");
				sql_from.append(z0331);
				sql_from.append(" or ");
				sql_from.append(z0329);
				sql_from.append(")");
			}
					
					
			sql_from.append(" order by z0301 ");
			ContentDAO dao = new ContentDAO(this.conn);
			HashMap countMap = this.getExamineeCount(dao);
			RowSet rs = null;
			//System.out.println(sql_select.toString()+sql_from.toString());
			rs = dao.search(sql_select.toString()+sql_from.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("un",rs.getString("un")==null?"":rs.getString("un"));
				bean.set("um",rs.getString("um")==null?"":rs.getString("um"));
				String count="0";
				if(countMap.get(rs.getString("z0301"))!=null) {
                    count=(String)countMap.get(rs.getString("z0301"));
                }
				bean.set("count", count==null?"0":count);
				bean.set("z0313",rs.getString("z0313")==null?"0":rs.getString("z0313"));
				String z0336=rs.getString("z0336");
				String posname=rs.getString("codeitemdesc")==null?"":rs.getString("codeitemdesc");
				if(hireMajor.length()>0&&z0336!=null&&z0336.length()>0&& "01".equals(z0336))
				{
					if(hireMajorIsCode)
					{
						posname=rs.getString(hireMajor)==null?"":rs.getString(hireMajor).trim();
						posname=AdminCode.getCodeName(hireMajoritem.getCodesetid(),posname);
					}
					else
					{
						 posname=rs.getString(hireMajor);
					}
				}
				
				bean.set("atk",posname);//@k
				bean.set("z0301",rs.getString("z0301"));//zp_pos_id
				bean.set("z0311", rs.getString("z0311"));
				for(int j=0;j<condlist.size();j++)
				{
					LazyDynaBean t = (LazyDynaBean)condlist.get(j);
					String condid = (String)t.get("id");
					String condcount = this.getCondPerson(condid, rs.getString("z0301"),userView);
					bean.set(condid,condcount);
				}
				list.add(bean);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public String getCondPerson(String condid,String zp_pos_id,UserView userView)
	{
		String ret="0";
		try
		{       ContentDAO dao = new ContentDAO(this.conn);
		        RowSet rs = null;
		        String a0100 = this.getA0100(condid, zp_pos_id, userView, dao);
	            String sql ="select count(a0100) countNum from zp_pos_tache where zp_pos_tache.zp_pos_id='"+zp_pos_id+"' and a0100 in("+(a0100.trim().length()>0?a0100.substring(1):"''")+")";
	            rs = dao.search(sql);
	            while(rs.next())
	            {
	            	ret=("0".equals(rs.getString("countNum"))||rs.getString("countNum")==null)?"0":rs.getString("countNum");
	            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	public String getA0100(String condid,String zp_pos_id,UserView userView,ContentDAO dao)
	{
		  StringBuffer a0100 = new StringBuffer("");
		try
		{
			RecordVo cvo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname="";
			if(cvo!=null) {
                dbname=cvo.getString("str_value");
            } else {
                throw GeneralExceptionHandler.Handle(new Exception("请在参数设之中配置招聘人才库！"));
            }
			RowSet rs = null;
			
			 RecordVo vo=new RecordVo("lexpr");
		     vo.setString("id",condid);
		     vo=dao.findByPrimaryKey(vo);
	         String expr=vo.getString("lexpr");
	         String factor=vo.getString("factor");
	         String type=vo.getString("type");
	         String fuzzy=vo.getString("fuzzyflag");
	         if(fuzzy==null|| "".equals(fuzzy)) {
                 fuzzy="0";
             }
	         boolean blike=false;
	         if("1".equals(fuzzy)) {
                 blike=true;
             }
	        // factor=factor.replaceAll("\\$THISMONTH\\[\\]","当月");  
	         ArrayList fieldlist = new ArrayList();
	         String strwhere="";  
	       
	            if((!userView.isSuper_admin())&& "1".equals(type))
	            {
	                strwhere=userView.getPrivSQLExpression(expr+"|"+factor,dbname,false,blike,true,fieldlist);
	            }
	            else
	            {
	                FactorList factorlist=new FactorList(expr,factor,dbname,false ,blike,true,Integer.parseInt(type),userView.getUserId());
	                fieldlist=factorlist.getFieldList();
	                strwhere=factorlist.getSqlExpression();
	            }
	            rs = dao.search(" select "+dbname+"A01.a0100 "+strwhere);
	           // a0100.setLength(0);
	            while(rs.next())
	            {
	            	a0100.append(",'");
	            	a0100.append(rs.getString("a0100"));
	            	a0100.append("'");
	            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return a0100.toString();
	}
	/**
	 * 取得应聘的简历数量
	 * @param dao
	 * @return
	 */
	public HashMap getExamineeCount(ContentDAO dao)
	{
		HashMap map = new HashMap();
		try
		{
			String sql ="select count(a0100) countNum,zp_pos_id from zp_pos_tache,z03 where zp_pos_tache.zp_pos_id=z03.z0301  group by zp_pos_id";
			RowSet rs = null;
			rs = dao.search(sql);
			while(rs.next())
			{
				map.put(rs.getString("zp_pos_id"),(rs.getString("countNum")==null|| "".equals(rs.getString("countNum")))?"0":rs.getString("countNum"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public String getDateSql(String operate,String itemid,String value)
	{
		StringBuffer sql=new StringBuffer("");
		value=value.replaceAll("\\.","-");
		String values[]=value.split("-");
		
	//	Calendar d=Calendar.getInstance();
		String year=values[0];
		String month=values[1];
		String day=values[2];
		if(">".equals(operate)|| "<".equals(operate))
		{
			sql.append("  ( "+Sql_switcher.year(itemid)+operate+year);
			sql.append(" or ("+Sql_switcher.year(itemid)+"="+year+" and "+Sql_switcher.month(itemid)+operate+month+"  )");
			sql.append(" or ("+Sql_switcher.year(itemid)+"="+year+" and "+Sql_switcher.month(itemid)+"="+month+" and "+Sql_switcher.day(itemid)+operate+day+"  )");	
			sql.append(" ) ");
		}
		else if(">=".equals(operate)|| "<=".equals(operate))
		{
			if(">=".equals(operate)) {
                sql.append("  ( "+Sql_switcher.year(itemid)+">"+year);
            } else {
                sql.append("  ( "+Sql_switcher.year(itemid)+"<"+year);
            }
			
			if(">=".equals(operate)) {
                sql.append(" or ("+Sql_switcher.year(itemid)+"="+year+" and "+Sql_switcher.month(itemid)+">"+month+"  )");
            } else {
                sql.append(" or ("+Sql_switcher.year(itemid)+"="+year+" and "+Sql_switcher.month(itemid)+"<"+month+"  )");
            }
			
			sql.append(" or ("+Sql_switcher.year(itemid)+"="+year+" and "+Sql_switcher.month(itemid)+"="+month+" and "+Sql_switcher.day(itemid)+operate+day+"  )");	
			sql.append(" ) ");
		}
		else if("=".equals(operate)) {
            sql.append("  ("+Sql_switcher.year(itemid)+"="+year+" and "+Sql_switcher.month(itemid)+"="+month+" and "+Sql_switcher.day(itemid)+"="+day+"  )");
        }
		
		return sql.toString();
	}
	
	
	public HashMap getSqlAndColumns(String zp_pos_id,String dbname,String atk,UserView userView,String condid,int type)
	{
		HashMap map = new HashMap();
		try
		{
			
			String select_sql=" select b.a0100,a.a0101,b.thenumber ";
			StringBuffer where_sql = new StringBuffer();
			where_sql.append(" from zp_pos_tache b,");
			where_sql.append(dbname+"a01 a where  a.a0100=b.a0100 and b.zp_pos_id='"+zp_pos_id+"'");
			if(type==2)
			{
				ContentDAO  dao = new ContentDAO(this.conn);
				String a0100 = this.getA0100(condid, zp_pos_id, userView, dao);
				where_sql.append("  and b.a0100 in(");
				where_sql.append(a0100.trim().length()>1?a0100.trim().substring(1):"''");
				where_sql.append(")");
			}
			String order_sql =" order by b.thenumber";
			String columns = "a0101,thenumber";
			String zp_pos_name="";
			ContentDAO dao = new ContentDAO(this.conn);
			RecordVo vo = new RecordVo("z03");
			vo.setString("z0301", zp_pos_id);
			vo=dao.findByPrimaryKey(vo);
			String z0336=vo.getString("z0336");
			if(z0336!=null&&z0336.trim().length()>0&& "01".equals(z0336))
			{
				ParameterXMLBo bo2 = new ParameterXMLBo(this.conn, "1");
				HashMap amap = bo2.getAttributeValues();
				String hireMajor="";
				if(amap.get("hireMajor")!=null)
				{
					hireMajor=(String)amap.get("hireMajor");
				}
				if(hireMajor!=null&&hireMajor.length()>0)
				{
					FieldItem item = DataDictionary.getFieldItem(hireMajor.toLowerCase());
					String value=vo.getString(hireMajor.toLowerCase())==null?"":vo.getString(hireMajor.toLowerCase());
					if(item.isCode()) {
                        value=AdminCode.getCodeName(item.getCodesetid(), value);
                    }
					zp_pos_name="应聘专业："+value;
				}
			}
			if(zp_pos_name==null||zp_pos_name.length()==0)
			{
		    	String sql = "select codeitemdesc from organization where upper(codesetid)='@K' and codeitemid='"+atk+"'";
		    	RowSet rs = null;
		    	rs = dao.search(sql);
		    	while(rs.next())
		    	{
			    	zp_pos_name="岗位："+rs.getString("codeitemdesc");
		    	}
			}
			map.put("1",select_sql);
			map.put("2", where_sql.toString());
			map.put("3",order_sql);
			map.put("4",columns);
			map.put("5",zp_pos_name);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public String getNameAndNumber(String zp_pos_id,String dbname,String filterSql ,int type)
	{
		StringBuffer buf = new StringBuffer("");
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select b.a0100,a.a0101,b.thenumber ");
			sql.append("from zp_pos_tache b,");
			sql.append(dbname+"a01 a where  a.a0100=b.a0100 and b.zp_pos_id='"+zp_pos_id+"'");
			if(type==2)
			{
				sql.append(filterSql);
			}
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql.toString());
			int i=0;
			while(rs.next())
			{
				if(i!=0&&i%8==0)
				{
					buf.append("\r\n");
				}
				buf.append(rs.getString("a0101"));
				buf.append("("+rs.getString("thenumber")+")");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
	public boolean getData(UserView userView,String starttime,String endtime)
	{
		ArrayList list = new ArrayList();
		boolean flag = false;
		try
		{
			ParameterXMLBo bo2 = new ParameterXMLBo(this.conn, "1");
			HashMap map0 = bo2.getAttributeValues();
			String hireMajor="";			//xieguiquan 2010-09-17
			if(map0.get("hireMajor")!=null) {
                hireMajor=(String)map0.get("hireMajor");  //招聘专业指标
            }
			boolean hireMajorIsCode=false;
			FieldItem hireMajoritem=null;
			if(hireMajor.length()>0)
			{
				hireMajoritem=DataDictionary.getFieldItem(hireMajor.toLowerCase());
				if(hireMajoritem.getCodesetid().length()>0&&!"0".equals(hireMajoritem.getCodesetid())) {
                    hireMajorIsCode=true;
                }
			}
			StringBuffer sql_select=new StringBuffer();
			if(hireMajor.length()>0&&",z0301,z0311,codeitemdesc,z0329,z0331,z0313,z0336".indexOf(","+hireMajor.toLowerCase())==-1){
				sql_select=new StringBuffer("select z0301,z0311,org1.codeitemdesc,org2.codeitemdesc un,org3.codeitemdesc um");
			 	sql_select.append(",z0329,z0331,z0313,z0336,"+hireMajor+"  ");
			}else{
				sql_select=new StringBuffer("select z0301,z0311,org1.codeitemdesc,org2.codeitemdesc un,org3.codeitemdesc um");
				sql_select.append(",z0329,z0331,z0313,z0336 ");
			}
//			StringBuffer sql_select=new StringBuffer("select z0301,z0311,org1.codeitemdesc,org2.codeitemdesc un,org3.codeitemdesc um");
//			sql_select.append(",z0329,z0331,z0313 ");
			
			StringBuffer sql_from=new StringBuffer(" from z03 left join (select * from organization where codesetid='@K') org1  on z03.z0311=org1.codeitemid");
			sql_from.append(" left join (select * from organization where codesetid='UN') org2   on  z03.z0321=org2.codeitemid");
			sql_from.append(" left join (select * from organization where codesetid='UM') org3   on  z03.z0325=org3.codeitemid");
		/*	if(starttime!=null&&!starttime.equals(""))
				sql_from.append(getDateSql("<=","z0329",endtime));
			if(endtime!=null&&!endtime.equals(""))
			{
				sql_from.append(" and ");
			    sql_from.append(getDateSql(">=","z0331",starttime));
			}*/
			String z0331="";
			String z0329="";
			if(starttime!=null&&!"".equals(starttime))
			{
				z0331 = getDateSql("<","z0331",starttime);
			}
			if(endtime!=null&&!"".equals(endtime))
			{
				z0329 = getDateSql(">","z0329",endtime);
			}
			if(!("".equals(z0331)|| "".equals(z0329)))
			{
				sql_from.append(" where z0301 not in(select z0301 from z03 where ");
				sql_from.append(z0331);
				sql_from.append(" or ");
				sql_from.append(z0329);
				sql_from.append(")");
			}
					
			sql_from.append(" order by z0301 ");
			ContentDAO dao = new ContentDAO(this.conn);
			HashMap countMap = this.getExamineeCount(dao);
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn);
			HashMap map=parameterXMLBo.getAttributeValues();
			String condid = "";
			StringBuffer sql = new StringBuffer();
			StringBuffer whereSql = new StringBuffer();
			RecordVo cvo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname="";
			if(cvo!=null) {
                dbname=cvo.getString("str_value");
            } else {
                throw GeneralExceptionHandler.Handle(new Exception("请在参数设之中配置招聘人才库！"));
            }
			if(dbname==null||dbname.trim().length()<=0) {
                throw GeneralExceptionHandler.Handle(new Exception("请在参数设之中配置招聘人才库！"));
            }
			RowSet rs = null;
			StringBuffer a0100 = new StringBuffer();
			if(map!=null&&map.get("common_query")!=null)
			{
				condid = (String)map.get("common_query");
			}
			if(condid==null|| "".equals(condid))//未定义查询条件
			{
				rs = dao.search(sql_select+sql_from.toString());
				while(rs.next())
				{
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("un",rs.getString("un")==null?"":rs.getString("un"));
					bean.set("um",rs.getString("um")==null?"":rs.getString("um"));
					String count="0";
					if(countMap.get(rs.getString("z0301"))!=null) {
                        count=(String)countMap.get(rs.getString("z0301"));
                    }
					bean.set("count", count);
					bean.set("z0313",rs.getString("z0313")==null?"":rs.getString("z0313"));
					String z0336=rs.getString("z0336");
					String posname=rs.getString("codeitemdesc")==null?"":rs.getString("codeitemdesc");
					if(hireMajor.length()>0&&z0336!=null&&z0336.length()>0&& "01".equals(z0336))
					{
						if(hireMajorIsCode)
						{
							posname=rs.getString(hireMajor).trim();
							posname=AdminCode.getCodeName(hireMajoritem.getCodesetid(),posname);
						}
						else
						{
							 posname=rs.getString(hireMajor);
						}
					}
					
					bean.set("atk",posname);//@k
				//	bean.set("atk",rs.getString("codeitemdesc")==null?"":rs.getString("codeitemdesc"));
					bean.set("z0301",rs.getString("z0301")==null?"":rs.getString("z0301"));
					bean.set("z0311", rs.getString("z0311")==null?"":rs.getString("z0311"));
					
					String zhong = this.getNameAndNumber(rs.getString("z0301"), dbname,"", 1);
					bean.set("zc",zhong);
					bean.set("fzc","");
					list.add(bean);
				}
			}
			else//定义了查询条件
			{
				flag = true;
				String[] temp = condid.split(",");
				for(int i=0;i<temp.length;i++)
				{
					if(temp[i]==null|| "".equals(temp[i])) {
                        continue;
                    }
					 RecordVo vo=new RecordVo("lexpr");
				     vo.setString("id",temp[i]);
				     try
				     {
				         vo=dao.findByPrimaryKey(vo);
				     }
				     catch(Exception e)
				     {
				    	 continue;
				     }
			         String expr=vo.getString("lexpr");
			         String factor=vo.getString("factor");
			         String type=vo.getString("type");
			         String fuzzy=vo.getString("fuzzyflag");
			         if(fuzzy==null|| "".equals(fuzzy)) {
                         fuzzy="0";
                     }
			         boolean blike=false;
			         if("1".equals(fuzzy)) {
                         blike=true;
                     }
			        // factor=factor.replaceAll("\\$THISMONTH\\[\\]","当月");  
			         ArrayList fieldlist = new ArrayList();
			         String strwhere="";      
			         try
			         {
			            if((!userView.isSuper_admin())&& "1".equals(type))
			            {
			                strwhere=userView.getPrivSQLExpression(expr+"|"+factor,dbname,false,blike,true,fieldlist);
			            }
			            else
			            {
			                FactorList factorlist=new FactorList(expr,factor,dbname,false ,blike,true,Integer.parseInt(type),userView.getUserId());
			                fieldlist=factorlist.getFieldList();
			                strwhere=factorlist.getSqlExpression();
			            }
			         }
			         catch(Exception e)
			         {
			        	 continue;
			         }
			            String t = "";
			            if(a0100.toString().trim().length()>0) {
                            t = a0100.toString().trim().substring(1);
                        }
			            rs = dao.search(" select "+dbname+"a01.a0100 "+strwhere+("".equals(t)?"":(" and "+dbname+"a01.a0100 in("+t+")")));
			            a0100.setLength(0);
			            while(rs.next())
			            {
			            	a0100.append(",'");
			            	a0100.append(rs.getString("a0100"));
			            	a0100.append("'");
			            }
				} //for i loop end
				String zc = "''";
				if(a0100.toString().trim().length()>0) {
                    zc=a0100.toString().trim().substring(1);
                }
				rs = dao.search(sql_select+sql_from.toString());
				while(rs.next())
				{
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("un",rs.getString("un")==null?"":rs.getString("un"));
					bean.set("um",rs.getString("um")==null?"":rs.getString("um"));
					String count="0";
					if(countMap.get(rs.getString("z0301"))!=null) {
                        count=(String)countMap.get(rs.getString("z0301"));
                    }
					bean.set("count", count);
					bean.set("z0313",rs.getString("z0313")==null?"":rs.getString("z0313"));
					String z0336=rs.getString("z0336");
					String posname=rs.getString("codeitemdesc")==null?"":rs.getString("codeitemdesc");
					if(hireMajor.length()>0&&z0336!=null&&z0336.length()>0&& "01".equals(z0336))
					{
						if(hireMajorIsCode)
						{
							posname=rs.getString(hireMajor).trim();
							posname=AdminCode.getCodeName(hireMajoritem.getCodesetid(),posname);
						}
						else
						{
							 posname=rs.getString(hireMajor);
						}
					}
					
					bean.set("atk",posname);//@k
					//bean.set("atk",rs.getString("codeitemdesc")==null?"":rs.getString("codeitemdesc"));
					bean.set("z0301",rs.getString("z0301")==null?"":rs.getString("z0301"));
					bean.set("z0311", rs.getString("z0311")==null?"":rs.getString("z0311"));
					
					String zhong = this.getNameAndNumber(rs.getString("z0301"), dbname," and a.a0100 in("+zc+")", 2);
					String fzhong = this.getNameAndNumber(rs.getString("z0301"), dbname, "''".equals(zc)?"":" and a.a0100 not in("+zc+")", 2);
					bean.set("zc",zhong);
					bean.set("fzc",fzhong);
					list.add(bean);
				}
				
			}
			this.setDataList(list);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	public ArrayList getDataList() {
		return dataList;
	}
	public void setDataList(ArrayList dataList) {
		this.dataList = dataList;
	}
	public ArrayList getSelectedCommonQueryCondList(String ids,String type)
	{
		ArrayList list = new ArrayList();
		try
		{
			if(ids==null|| "".equals(ids)) {
                return list;
            }
			StringBuffer strsql = new StringBuffer();
			strsql.append("select id,name,type from lexpr where type='");//
	        strsql.append(type);
	        strsql.append("' and id in("+ids+") order by id");
	        ContentDAO dao = new ContentDAO(this.conn);
	        RowSet rs = null;
	        rs = dao.search(strsql.toString());
	        while(rs.next())
	        {
	        	LazyDynaBean  bean = new LazyDynaBean();
	        	bean.set("id",rs.getString("id"));
	        	bean.set("name",rs.getString("name"));
	        	list.add(bean);
	        	//list.add(new CommonData(rs.getString("id"),rs.getString("id")+":"+rs.getString("name")));
	        }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

}
