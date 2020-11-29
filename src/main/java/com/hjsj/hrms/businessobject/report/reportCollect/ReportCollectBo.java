
package com.hjsj.hrms.businessobject.report.reportCollect;

import com.hjsj.hrms.businessobject.report.TgridBo;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.TnameExtendBo;
import com.hjsj.hrms.businessobject.report.formulaAnalyse.ReportCollectFormulaAnalyse;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.*;
import java.util.*;
/**
 * 
 * <p>Title:</p>
 * <p>Description:报表汇总类</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 13, 2006:9:15:32 AM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class ReportCollectBo {
	Connection conn=null;
	
	public ReportCollectBo(Connection conn)
	{
		this.conn=conn;
	}
	
	
	
	
	//是填报单位否对报表拥有操作权限
	public boolean isPopedom(ArrayList unitcodeList,ArrayList tabidList)
	{
		boolean isPopedom=false;
		
		StringBuffer sql_table=new StringBuffer("");
		for(Iterator t=tabidList.iterator();t.hasNext();)
		{		
			String temp=(String)t.next();
			String[] tt=temp.split("§");
			sql_table.append(" or tabid="+tt[0]);
		}
		String sql_table_str=sql_table.substring(3);
		StringBuffer sql_whl=new StringBuffer("");
		for(int i=0;i<unitcodeList.size();i++)
		{
			String temp=(String)unitcodeList.get(i);
			String[] tt=temp.split("§");			
			sql_whl.append(" or ( unitcode='"+tt[0]+"' and ( "+sql_table_str+" ))");
		}
		String sql="select count(unitcode) from treport_ctrl where "+sql_whl.substring(3)+" ";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			recset=dao.search(sql);
			if(recset.next())
			{
				int count=recset.getInt(1);
				if(count==unitcodeList.size()*tabidList.size()) {
                    isPopedom=true;
                }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return isPopedom;
	}
	
	
	
	
	
	/**
	 * 判断列表中的单位对应的报表是否已上报
	 * @param unitcodeList 单位列表
	 * @param tabidList	   报表列表
	 * @return 
	 */
	public ArrayList isNotAppeal(ArrayList unitcodeList,ArrayList tabidList)
	{	
		ArrayList errorInfoList=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			
			//首先判断统计结果表 tt_xxx是否已产生
			ArrayList list=isExistTables(tabidList);
			errorInfoList=(ArrayList)list.get(0);				//错误信息列表
			ArrayList tablist=(ArrayList)list.get(1);			//存在的上报表
		    HashMap tableMap=new HashMap();
		    HashMap unitMap=new HashMap();
			StringBuffer sql=new StringBuffer("");
			
			StringBuffer sql_table=new StringBuffer("");
			StringBuffer like=new StringBuffer(); 
			for(Iterator t=tablist.iterator();t.hasNext();)
			{		
				String temp=(String)t.next();
				String[] tt=temp.split("§");
				sql_table.append(" or tabid="+tt[0]);
				like.append(" or tt.report not like '%,");
				like.append(tt[0]);
				like.append(",%'");
				
				tableMap.put(tt[0],tt[1]);
			}
			if(StringUtils.isEmpty(sql_table.toString())) {
				return errorInfoList;
			}
			String sql_table_str=sql_table.substring(3);
			StringBuffer sql_whl=new StringBuffer("");
			for(int i=0;i<unitcodeList.size();i++)
			{
				String temp=(String)unitcodeList.get(i);
				String[] tt=temp.split("§");
				unitMap.put(tt[0],tt[1]);
				sql.setLength(0);
				sql.append("select * from tt_organization tt left join treport_ctrl tr on tt.unitcode=tr.unitcode where tt.unitcode='");
				sql.append(tt[0]);
				sql.append("' and (");
				sql.append(sql_table.substring(3));
				sql.append(") and ((");
				
				sql.append(like.substring(3));
				sql.append(")  or tt.report is null )");
				sql.append("and (tr.status!=1 and tr.status!=3)");
//				sql_whl.append(",'");
//				sql_whl.append(tt[0]);
//				sql_whl.append("'");
				recset=dao.search(sql.toString());
				while(recset.next())
				{
					String unitcode=recset.getString("unitcode");
					String tabid=recset.getString("tabid");
					errorInfoList.add((String)unitMap.get(unitcode)+" "+ResourceFactory.getProperty("edit_report.noAppeal")+" "+(String)tableMap.get(tabid));
					
					
				}
				
			}
			//sql.append("select * from treport_ctrl where unitcode in ("+sql_whl.substring(1)+") and (status!=1 and status!=3) and ( "+sql_table.substring(3)+" ) ");			
			
			
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return errorInfoList;
	}
	
	
	
//	直属汇总数据 与 汇总单位数据比较
	public String compareChildData3(String unitcode,String tabid,double[][] value)
	{
		StringBuffer info=new StringBuffer("");
		
		
		TnameBo tnameBo=new TnameBo(this.conn,tabid);
		HashMap colsMap=tnameBo.getSerialfromIndex(2);		 //报表的列信息：封装了列在报表中的实际位置，传入公式中列数得到实际的列针对与二维数组的下标
		HashMap rowsMap=tnameBo.getSerialfromIndex(1);		 //报表的行信息：封装了行在报表中的实际位置，传入公式中行数得到实际的列针对与二维数组的下标
		int[][] digitalResults= tnameBo.getDigitalResults();
		int rows=tnameBo.getMaxRowNumber(rowsMap);
		String lexper="";
		String rexper="";
		ArrayList le=new ArrayList();
		ArrayList re=new ArrayList();
		HashMap lre=new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);
		boolean colFlag = false;    //代表纵表栏上甲是否存在
		boolean rowFlag = false;    //代表横表栏上编号是否存在
		RowSet recset=null;
		try
		{
			TTorganization ttorganization=new TTorganization(this.conn);
			//取得所有直属单位
			String tsortid="";
			StringBuffer sql=new StringBuffer();
			sql.append("select tsortid from tname where tabid='"+tabid+"'");
			dao=new ContentDAO(this.conn);
			try {
				recset=dao.search(sql.toString());
				if(recset.next()){
					tsortid=recset.getString(1);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			ArrayList basic_unitcodeList=ttorganization.getUnderUnitList(unitcode,tsortid);

			String a_unitcode="";
			String a_basiccode="";
			recset=dao.search("select * from tformula where tabid='"+tabid +"' and ColRow=5 ");
			while(recset.next()){
				lexper=recset.getString("lexpr");
				rexper=recset.getString("rexpr");
				
				if(lre.get(lexper)!=null){
					lre.remove(lexper);
					lre.put(lexper, rexper);
				}else{
					le.add(lexper);
					lre.put(lexper, rexper);
				}
			}
			StringBuffer basic_unitcode_str=new StringBuffer("");	//直属单位sql
			a_unitcode=unitcode;
			
			for(int i=0;i<basic_unitcodeList.size();i++)
			{
				DynaBean temp=(DynaBean)basic_unitcodeList.get(i);	
				basic_unitcode_str.append(" or unitcode='"+((String)temp.get("unitcode"))+"'");
				a_basiccode=((String)temp.get("unitcode"));
			}
			
			if(basic_unitcode_str.length()==0)
			{
				info.append(ResourceFactory.getProperty("report_collect.info9")+"！");
				return info.toString();
			}
			else
			{		
					String tt = tabid; //报表表号
					recset=dao.search("select unitcode from tt_organization where ( report not like '%,"+tt+",%' or report is null )   and ("+basic_unitcode_str.substring(3)+")");
					StringBuffer a_basic_unitcode_str=new StringBuffer("");
					while(recset.next())
					{
						a_basic_unitcode_str.append(" or unitcode='"+((String)recset.getString("unitcode"))+"'");
					}
					
					if(a_basic_unitcode_str.length()==0)
					{
						RowSet rowSet3=dao.search("select name from tname where tabid="+tt);
						if(rowSet3.next()) {
                            info.append(rowSet3.getString(1)+"  "+ResourceFactory.getProperty("report_collect.info10"));
                        }
						return info.toString();
					}
					
					
					recset=dao.search("select * from tt_"+tt+" where 1=2 ");
					ResultSetMetaData data=recset.getMetaData();
					int cols=data.getColumnCount()-2;
					
					StringBuffer sql_insert=new StringBuffer("insert into  tt_"+tt+" (unitcode,secid");
					StringBuffer sql_value=new StringBuffer("");
				
					StringBuffer sql_w2=new StringBuffer(" select secid");  
					for(int i=1;i<=cols;i++)
					{
						sql_insert.append(",C"+i);
						sql_value.append(",0");
						sql_w2.append(",sum(C"+i+") C"+i);
					}
					sql_w2.append(" from tt_"+tt+" where ( ");
					sql_w2.append(a_basic_unitcode_str.substring(3)+" ) group by secid");
					/***********防止某一方没有数据************/
					int w2_count=0;
					
					RowSet rowSet2=dao.search("select count(secid) a_count from ("+sql_w2+") a");
					if(rowSet2.next()) {
                        w2_count=rowSet2.getInt("a_count");
                    }
					if(w2_count==0)
					{
						for(int i=1;i<=value.length;i++)
						{
							dao.insert(sql_insert.toString()+" ) values ('"+a_basiccode+"',"+i+sql_value.toString()+")",new ArrayList());
						}
					}
					/**********************************/
					RowSet recset2=dao.search(sql_w2.toString()+" order by secid");
					
					//wangcq 2014-12-18 判断是否有甲行和编号列 begin 
					ArrayList colInfoBGridList = tnameBo.getColInfoBGrid();
					ArrayList rowInfoBGridList = tnameBo.getRowInfoBGrid();
					for(int k = 0;k<colInfoBGridList.size();k++){
						RecordVo colVo = (RecordVo) colInfoBGridList.get(k);
						if (colVo.getInt("flag1") == 4){
							colFlag = true;
							break;
						}
					}
					for(int k = 0;k<rowInfoBGridList.size();k++){
						RecordVo rowVo = (RecordVo) rowInfoBGridList.get(k);
						if (rowVo.getInt("flag1") == 4){
							rowFlag = true;
							break;
						}
					}
					//wangcq 2014-12-18 end
					int j=0;
					if(colFlag){
						recset2.next();   //存在甲行时跳过此结果
						j=1;
					}
					StringBuffer error_info=new StringBuffer("");
					while(recset2.next())
					{
						j++;
						int i=1;
						int lreflag=0; //wangcq 2015-1-9 编号列占了一行，无编号列时则公式是正确的排序，无需减1
						if(rowFlag){//存在编号列则不取此列数据
							i=2;
							lreflag=1;
						}
						for(;i<=cols;i++)
						{
							if(lre.get(String.valueOf(i-lreflag))!=null){
								continue;
							}
							String value1=PubFunc.round(String.valueOf(value[j-1][i-1]),digitalResults[j-1][i-1]);
							String value2=PubFunc.round(recset2.getString("C"+i),digitalResults[j-1][i-1]);//dml getfloat()精度问题
							if(!value1.equals(value2))
							{
								String i_str=(String)colsMap.get(String.valueOf(i-1));
								String j_str=(String)rowsMap.get(String.valueOf(j-1));	
								
								if(i_str.length()==1) {
                                    i_str=" "+i_str;
                                }
								if(j_str.length()==1) {
                                    j_str=" "+j_str;
                                }
								error_info.append("\r\n"+ResourceFactory.getProperty("label.page.serial")+j_str+ResourceFactory.getProperty("reportanalyse.row")+i_str+ResourceFactory.getProperty("reportanalyse.column")+" ("+ResourceFactory.getProperty("report_collect.collectUnitData")+") "+value[j-1][i-1]+"<>"+recset2.getFloat("C"+i)+" ("+ResourceFactory.getProperty("report_collect.underUnitData")+")");
							}
						}	
					}
					if(error_info.length()>0)
					{
						info.append(error_info.toString());
					}
					
			}
			ArrayList unitlist=ttorganization.getUnderUnit(unitcode);
			ReportCollectFormulaAnalyse rcfa=new ReportCollectFormulaAnalyse(this.conn,tabid,unitlist);
			String sub_sql="";
			for(int i=0;i<le.size();i++){
				sub_sql=rcfa.reportCollectFormulaAnalyse2((String)le.get(i),(String)lre.get(le.get(i)));
				Iterator it=rowsMap.entrySet().iterator();
				StringBuffer error_info=new StringBuffer("");
				HashMap hm=new HashMap();
				
				while (it.hasNext()) {
					StringBuffer temp=new StringBuffer("");
					StringBuffer searchsql=new StringBuffer(" ");
					Map.Entry entry = (Map.Entry) it.next();
					String keys = (String) entry.getKey();
					String values = (String)entry.getValue();
					searchsql.append(sub_sql);
					searchsql.append(" and secid="+String.valueOf(Integer.parseInt(keys)+1) +"" );
					recset=dao.search(searchsql.toString());
					String i_str=String.valueOf(Integer.parseInt((String)le.get(i)));
					String j_str=String.valueOf(Integer.parseInt(values));
					if(recset.next()){
						 int jflag = 0; //wangcq 2015-1-15 
						 if(!rowFlag)  //不存在编号列时，比较时j要加1
                         {
                             jflag=1;
                         }
							for(int j=0;j<value[Integer.parseInt(keys)].length ;j++){
								if(j+jflag!=Integer.parseInt((String)le.get(i))){
									continue;
								}else{
									String value1=PubFunc.round(String.valueOf(value[Integer.parseInt(keys)][j]),digitalResults[Integer.parseInt(keys)][j]);
									String value2=PubFunc.round(recset.getString(1),digitalResults[Integer.parseInt(keys)][j]);
									if(!value1.equals(value2)){
										
										if(i_str.length()==1) {
                                            i_str=" "+i_str;
                                        }
										if(j_str.length()==1) {
                                            j_str=" "+j_str;
                                        }
										temp.append("\r\n"+ResourceFactory.getProperty("label.page.serial")+j_str+ResourceFactory.getProperty("reportanalyse.row")+i_str+ResourceFactory.getProperty("reportanalyse.column")+" ("+ResourceFactory.getProperty("report_collect.collectUnitData")+") "+value[Integer.parseInt(keys)][j]+"<>"+recset.getFloat(1)+" ("+ResourceFactory.getProperty("report_collect.underUnitData")+")");
										hm.put(j_str.trim(), temp.toString());
									}else{
										
									}
									break;
								}
							}
					}
					
				}
				for(int kk=1;kk<=rows;kk++){
					String temp=(String)hm.get(String.valueOf(kk));
					if(temp==null||temp.length()==0){
						continue;
					}
					error_info.append(temp);
				}
				if(error_info.length()>0)
				{
					if(error_info!=null&&error_info.length()!=0) {
                        info.append("\r\n********************");
                    }
					info.append(error_info.toString());
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(info.length()==0) {
            info.append(ResourceFactory.getProperty("label.kh.template.validated")+"！");
        }
		return info.toString();
	}
	
	
	
	
	
	
	
	
    //	直属汇总数据 与 汇总单位数据比较
	public String compareChildData2(String unitcode,ArrayList tabidList)
	{
		StringBuffer info=new StringBuffer("");
		
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			TTorganization ttorganization=new TTorganization(this.conn);
			//取得所有直属单位
			ArrayList basic_unitcodeList=ttorganization.getUnderUnitList(unitcode);

			String a_unitcode="";
			String a_basiccode="";
			StringBuffer unitcode_str=new StringBuffer("");			//汇总单位sql
			StringBuffer basic_unitcode_str=new StringBuffer("");	//直属单位sql
						
			unitcode_str.append(" or unitcode='"+unitcode+"'");
			a_unitcode=unitcode;
			
			for(int i=0;i<basic_unitcodeList.size();i++)
			{
				DynaBean temp=(DynaBean)basic_unitcodeList.get(i);	
				basic_unitcode_str.append(" or unitcode='"+((String)temp.get("unitcode"))+"'");
				a_basiccode=((String)temp.get("unitcode"));
			}
			
			if(basic_unitcode_str.length()==0)
			{
				info.append("<br>"+ResourceFactory.getProperty("report_collect.info9")+"！");
			}
			else
			{
				for(Iterator t=tabidList.iterator();t.hasNext();)
				{
					RecordVo vo = (RecordVo)t.next();
					String tt = vo.getString("tabid"); //报表表号
					String tname = vo.getString("name");   //用户名
					recset=dao.search("select unitcode from tt_organization where ( report not like '%,"+tt+",%' or report is null ) and ("+basic_unitcode_str.substring(3)+")");
					StringBuffer a_basic_unitcode_str=new StringBuffer("");
					while(recset.next())
					{
						a_basic_unitcode_str.append(" or unitcode='"+((String)recset.getString("unitcode"))+"'");
					}
					
					if(a_basic_unitcode_str.length()==0)
					{
						info.append("<br>"+tname+"&nbsp;&nbsp;"+ResourceFactory.getProperty("report_collect.info10")+"");
						continue;
					}
					String dmlsql="select * from tformula where tabid='"+tt +"' and ColRow=5";
					String dmllexper="";
					String dmlrexper="";
					ArrayList dmlle=new ArrayList();
					HashMap dmllre=new HashMap();
					recset=dao.search(dmlsql);
					while(recset.next()){
						dmllexper=recset.getString("lexpr");
						dmlrexper=recset.getString("rexpr");
						if(dmllre.get(dmllexper)!=null){
							dmllre.remove(dmllexper);
							dmllre.put(dmllexper, dmlrexper);
						}else{
							dmlle.add(dmllexper);
							dmllre.put(dmllexper, dmlrexper);
						}
					}
					TnameBo tnameBo=new TnameBo(this.conn,tt);
					HashMap colsMap=tnameBo.getSerialfromIndex(2);		 //报表的列信息：封装了列在报表中的实际位置，传入公式中列数得到实际的列针对与二维数组的下标
					HashMap rowsMap=tnameBo.getSerialfromIndex(1);		 //报表的行信息：封装了行在报表中的实际位置，传入公式中行数得到实际的列针对与二维数组的下标
					int[][] digitalResults=tnameBo.getDigitalResults();
					boolean colFlag = false;    //代表纵表栏上甲是否存在
					boolean rowFlag = false;    //代表横表栏上编号是否存在
					int rows=tnameBo.getMaxRowNumber(rowsMap);
					
					
					recset=dao.search("select * from tt_"+tt+" where 1=2 ");
					ResultSetMetaData data=recset.getMetaData();
					int cols=data.getColumnCount()-2;
					
					StringBuffer sql_insert=new StringBuffer("insert into  tt_"+tt+" (unitcode,secid");
					StringBuffer sql_value=new StringBuffer("");
					
				//	StringBuffer sql_f=new StringBuffer("insert into  tt_"+tt+" (unitcode,secid");
				
					StringBuffer sql_w1=new StringBuffer(" select secid");	
					StringBuffer sql_w2=new StringBuffer(" select secid");  
					for(int i=1;i<=cols;i++)
					{
						sql_insert.append(",C"+i);
						sql_value.append(",0");
					//	sql_f.append(",C"+i);
						sql_w1.append(", C"+i);	
						sql_w2.append(",sum(C"+i+") C"+i);
					}
					sql_w1.append(" from tt_"+tt+" where ( ");
					sql_w1.append(unitcode_str.substring(3)+"  ) ");
					sql_w2.append(" from tt_"+tt+" where ( ");
					sql_w2.append(a_basic_unitcode_str.substring(3)+" ) group by secid ");
					/***********防止某一方没有数据************/
					int w1_count=0;
					int w2_count=0;
					RowSet rowSet2=dao.search("select count(secid)a_count from ("+sql_w1+") a");
					if(rowSet2.next()) {
                        w1_count=rowSet2.getInt("a_count");
                    }
					rowSet2=dao.search("select count(secid) a_count from ("+sql_w2+") a");
					if(rowSet2.next()) {
                        w2_count=rowSet2.getInt("a_count");
                    }
					if(w1_count==0&&w2_count!=0)
					{
						for(int i=1;i<=w2_count;i++)
						{
							dao.insert(sql_insert.toString()+" ) values ('"+a_unitcode+"',"+i+sql_value.toString()+")",new ArrayList());
						}
						
					}
					if(w1_count!=0&&w2_count==0)
					{
						for(int i=1;i<=w1_count;i++)
						{
							dao.insert(sql_insert.toString()+" ) values ('"+a_basiccode+"',"+i+sql_value.toString()+")",new ArrayList());
						}
					}
					/**********************************/
					recset=dao.search(sql_w1.toString()+" order by secid");
					
					RowSet recset2=dao.search(sql_w2.toString()+" order by secid");
					//wangcq 2014-12-18 判断是否有甲行和编号列 begin 
					ArrayList colInfoBGridList = tnameBo.getColInfoBGrid();
					ArrayList rowInfoBGridList = tnameBo.getRowInfoBGrid();
					for(int k = 0;k<colInfoBGridList.size();k++){
						RecordVo colVo = (RecordVo) colInfoBGridList.get(k);
						if (colVo.getInt("flag1") == 4){
							colFlag = true;
							break;
						}
					}
					for(int k = 0;k<rowInfoBGridList.size();k++){
						RecordVo rowVo = (RecordVo) rowInfoBGridList.get(k);
						if (rowVo.getInt("flag1") == 4){
							rowFlag = true;
							break;
						}
					}
					//wangcq 2014-12-18 end
					int j=0;
					if(colFlag){
						recset.next();
						recset2.next();   //存在甲行时跳过此结果
						j=1;
					}
					
					StringBuffer error_info=new StringBuffer("");
					HashMap hm=new HashMap();
					while(!recset.isLast()&&!recset2.isLast()&&recset.next()&&recset2.next())
					{
						j++;
						int i=1;
						int lreflag=0; //wangcq 2015-1-9 编号列占了一行，无编号列时则公式是正确的排序，无需减1
						if(rowFlag){//存在编号列则不取此列数据
							i=2;
							lreflag=1;
						}
						for(;i<=cols;i++)
						{
						//	String i_str=i<=9?" ":"";
						//	String j_str=j<=9?" ":"";
							HashMap vmap=new HashMap();
							if(dmllre.get(String.valueOf(i-lreflag))!=null){
								if(hm.get(String.valueOf(i-1))!=null){
									HashMap tem=(HashMap)hm.get(String.valueOf(i-1));
									hm.remove(String.valueOf(i-1));
									tem.put(String.valueOf(j-1), recset.getString("C"+i));
									hm.put(String.valueOf(i-1), tem);
								}else{
									vmap.put(String.valueOf(j-1), recset.getString("C"+i));
									hm.put(String.valueOf(i-1), vmap);
								}
								
								continue;
							}
							if(recset.getFloat("C"+i)!=recset2.getFloat("C"+i))
							{
								String i_str=(String)colsMap.get(String.valueOf(i-1));
								String j_str=(String)rowsMap.get(String.valueOf(j-1));
								if(i_str.length()==1) {
                                    i_str=" "+i_str;
                                }
								if(j_str.length()==1) {
                                    j_str=" "+j_str;
                                }
								
								error_info.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;"+ResourceFactory.getProperty("label.page.serial")+j_str+ResourceFactory.getProperty("reportanalyse.row")+i_str+ResourceFactory.getProperty("reportanalyse.column")+" ("+ResourceFactory.getProperty("report_collect.collectUnitData")+")&nbsp;"+recset.getFloat("C"+i)+"<>"+recset2.getFloat("C"+i)+"&nbsp;("+ResourceFactory.getProperty("report_collect.underUnitData")+")");
							
							
							
							}
						}	
					}
					if(error_info.length()>0)
					{
						info.append("<br><br>"+tt+"."+tname+"<br>");
						info.append(error_info.toString());
					}
					ArrayList underlist=ttorganization.getUnderUnit(unitcode);
					ReportCollectFormulaAnalyse rcfa=new ReportCollectFormulaAnalyse(this.conn,tt,underlist);
					String sub_sql="";
					for(int i=0;i<dmlle.size();i++){
						sub_sql=rcfa.reportCollectFormulaAnalyse2((String)dmlle.get(i),(String)dmllre.get(dmlle.get(i)));
						Iterator it=rowsMap.entrySet().iterator();
						StringBuffer eifo=new StringBuffer("");
						HashMap hhm=new HashMap();
						int jflag = 0; //wangcq 2015-1-15 
						if(!rowFlag)  //不存在编号列时，比较时j要加1
                        {
                            jflag=1;
                        }
						HashMap valuemap=(HashMap)hm.get(String.valueOf(Integer.parseInt((String)dmlle.get(i))-jflag));
						while (it.hasNext()) {
							StringBuffer temp=new StringBuffer("");
							StringBuffer searchsql=new StringBuffer(" ");
							Map.Entry entry = (Map.Entry) it.next();
							String keys = (String) entry.getKey();
							String values = (String)entry.getValue();
							searchsql.append(sub_sql);
							searchsql.append(" and secid="+String.valueOf(Integer.parseInt(keys)+1) +"" );
							recset=dao.search(searchsql.toString());
							String i_str=String.valueOf(Integer.parseInt((String)dmlle.get(i)));
							String j_str=String.valueOf(Integer.parseInt(values));
							if(recset.next()){
								String valuek=(String)valuemap.get(String.valueOf(Integer.parseInt(keys)));
								String value1=PubFunc.round(valuek,digitalResults[Integer.parseInt(keys)][Integer.parseInt((String)dmlle.get(i))-jflag]);
								String value2=PubFunc.round(recset.getString(1),digitalResults[Integer.parseInt(keys)][Integer.parseInt((String)dmlle.get(i))-jflag]);
								if(!value1.equals(value2)){
									if(i_str.length()==1) {
                                        i_str=" "+i_str;
                                    }
									if(j_str.length()==1) {
                                        j_str=" "+j_str;
                                    }
									temp.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;"+ResourceFactory.getProperty("label.page.serial")+j_str+ResourceFactory.getProperty("reportanalyse.row")+i_str+ResourceFactory.getProperty("reportanalyse.column")+" ("+ResourceFactory.getProperty("report_collect.collectUnitData")+")&nbsp;"+valuek+"<>"+recset.getFloat(1)+"&nbsp;("+ResourceFactory.getProperty("report_collect.underUnitData")+")");
									hhm.put(j_str.trim(), temp.toString());
								}
							}
						}
						for(int kk=1;kk<=rows;kk++){
							String temp=(String)hhm.get(String.valueOf(kk));
							if(temp==null||temp.length()==0){
								continue;
							}
							eifo.append(temp);
						}
						if(eifo.length()>0)
						{
							if(info!=null&&info.length()!=0) {
                                info.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;***********************");
                            }
							info.append(eifo.toString());
						}
					}
					
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return info.toString();
	}
	
	
	//直属汇总数据 与 汇总单位数据比较
	public String  compareChildData(String unitcode,ArrayList tabidList)
	{
		String info="";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			TTorganization ttorganization=new TTorganization(this.conn);
			//取得所有直属单位
			ArrayList basic_unitcodeList=ttorganization.getUnderUnitList(unitcode);
			
			if(basic_unitcodeList.size()==0) {
                return info;
            }
			
			String a_unitcode="";
			String a_basiccode="";
			
			StringBuffer unitcode_str=new StringBuffer("");			//直属单位sql
			StringBuffer basic_unitcode_str=new StringBuffer("");			//基层单位sql
						
			unitcode_str.append(" or unitcode='"+unitcode+"'");
			a_unitcode=unitcode;
			
			for(int i=0;i<basic_unitcodeList.size();i++)
			{
				DynaBean temp=(DynaBean)basic_unitcodeList.get(i);	
				basic_unitcode_str.append(" or unitcode='"+((String)temp.get("unitcode"))+"'");
				a_basiccode=((String)temp.get("unitcode"));
			}
			
			
			for(Iterator t=tabidList.iterator();t.hasNext();)
			{
				String tt=(String)t.next();
				recset=dao.search("select unitcode from tt_organization where ( report not like '%,"+tt+",%' or report is null ) and ("+basic_unitcode_str.substring(3)+")");
				StringBuffer a_basic_unitcode_str=new StringBuffer("");
				while(recset.next())
				{
					a_basic_unitcode_str.append(" or unitcode='"+((String)recset.getString("unitcode"))+"'");
				}
				
				if(a_basic_unitcode_str.length()==0)
				{
					continue;
				}
				String dmlsql="select * from tformula where tabid='"+tt +"' and ColRow=5";
				String dmllexper="";
				String dmlrexper="";
				ArrayList dmlle=new ArrayList();
				HashMap dmllre=new HashMap();
				recset=dao.search(dmlsql);
				while(recset.next()){
					dmllexper=recset.getString("lexpr");
					dmlrexper=recset.getString("rexpr");
					if(dmllre.get(dmllexper)!=null){
						dmllre.remove(dmllexper);
						dmllre.put(dmllexper, dmlrexper);
					}else{
						dmlle.add(dmllexper);
						dmllre.put(dmllexper, dmlrexper);
					}
				}
				
				recset=dao.search("select * from tt_"+tt+" where 1=2 ");
				ResultSetMetaData data=recset.getMetaData();
				int cols=data.getColumnCount()-2;
				
				StringBuffer sql_insert=new StringBuffer("insert into  tt_"+tt+" (unitcode,secid");
				StringBuffer sql_value=new StringBuffer("");
				
				StringBuffer sql_f=new StringBuffer("insert into  tt_"+tt+" (unitcode,secid");
			
				StringBuffer sql_w1=new StringBuffer(" select secid");	//直属
				StringBuffer sql_w2=new StringBuffer(" select secid");   //基层
				for(int i=1;i<=cols;i++)
				{
					sql_insert.append(",C"+i);
					sql_value.append(",0");
					sql_f.append(",C"+i);
					sql_w1.append(", C"+i);	
					sql_w2.append(",sum(C"+i+") C"+i);
				}
				sql_w1.append(" from tt_"+tt+" where ( ");
				sql_w1.append(unitcode_str.substring(3)+" )");
				sql_w2.append(" from tt_"+tt+" where ( ");
				sql_w2.append(a_basic_unitcode_str.substring(3)+" ) group by secid");
				/***********防止某一方没有数据************/
				int w1_count=0;
				int w2_count=0;
				RowSet rowSet2=dao.search("select count(secid)a_count from ("+sql_w1+") a");
				if(rowSet2.next()) {
                    w1_count=rowSet2.getInt("a_count");
                }
				rowSet2=dao.search("select count(secid) a_count from ("+sql_w2+") a");
				if(rowSet2.next()) {
                    w2_count=rowSet2.getInt("a_count");
                }
				if(w1_count==0&&w2_count!=0)
				{
					for(int i=1;i<=w2_count;i++)
					{
						dao.insert(sql_insert.toString()+" ) values ('"+a_unitcode+"',"+i+sql_value.toString()+")",new ArrayList());
					}
					
				}
				if(w1_count!=0&&w2_count==0)
				{
					for(int i=1;i<=w1_count;i++)
					{
						dao.insert(sql_insert.toString()+" ) values ('"+a_basiccode+"',"+i+sql_value.toString()+")",new ArrayList());
					}
				}
				/**********************************/
				
				
				StringBuffer select_str=new StringBuffer("select ");
				StringBuffer temp_str=new StringBuffer("");
				for(int i=1;i<=cols;i++) {
                    temp_str.append(",a1.C"+i+"-a2.C"+i);
                }
				select_str.append(temp_str.substring(1));
				select_str.append(" from ( "+sql_w1.toString()+" ) a1,(");
				select_str.append(sql_w2+" ) a2 where a1.secid=a2.secid ");
				
				
				
				//System.out.println(select_str.toString());
				recset=dao.search(select_str.toString());
				while(recset.next())
				{
					boolean vary=false;
					for(int i=1;i<=cols;i++)
					{
						if(dmllre.get(String.valueOf(i-1))!=null) {
                            continue;
                        }
						if(recset.getFloat(i)!=0)
						{
							vary=true;
							break;
						}
					}
					if(vary)
					{
						RowSet rowSet3=dao.search("select name from tname where tabid="+tt);
						if(rowSet3.next()) {
                            info+="\r\n  "+rowSet3.getString(1)+" "+ResourceFactory.getProperty("report_collect.info11");
                        }
						break;
					}
				}
				ArrayList underlist=ttorganization.getUnderUnit(unitcode);
				ReportCollectFormulaAnalyse rcfa=new ReportCollectFormulaAnalyse(this.conn,tt,underlist);
				String sub_sql="";
				TnameBo tnameBo=new TnameBo(this.conn,tt);
				HashMap colsMap=tnameBo.getSerialfromIndex(2);		 //报表的列信息：封装了列在报表中的实际位置，传入公式中列数得到实际的列针对与二维数组的下标
				HashMap rowsMap=tnameBo.getSerialfromIndex(1);		 //报表的行信息：封装了行在报表中的实际位置，传入公式中行数得到实际的列针对与二维数组的下标
				int[][] digitalResults=tnameBo.getDigitalResults();
				int rows=tnameBo.getMaxRowNumber(rowsMap);
				for(int i=0;i<dmlle.size();i++){
					sub_sql=rcfa.reportCollectFormulaAnalyse2((String)dmlle.get(i),(String)dmllre.get(dmlle.get(i)));
					Iterator it=rowsMap.entrySet().iterator();
					StringBuffer eifo=new StringBuffer("");
					String sql="select C"+(Integer.parseInt((String)dmlle.get(i))+1)+ " from tt_"+tt +" where unitcode='"+unitcode+"' order by secid";
					recset=dao.search(sql);
					HashMap valueMap=new HashMap();
					int trow=0;
					while(recset.next()){
						valueMap.put(String.valueOf(trow), recset.getString(1));
						trow++;
					}
					HashMap hhm=new HashMap();
					while (it.hasNext()) {
						StringBuffer temp=new StringBuffer("");
						StringBuffer searchsql=new StringBuffer(" ");
						Map.Entry entry = (Map.Entry) it.next();
						String keys = (String) entry.getKey();
						String values = (String)entry.getValue();
						searchsql.append(sub_sql);
						searchsql.append(" and secid="+String.valueOf(Integer.parseInt(values)+1) +"" );
						recset=dao.search(searchsql.toString());
						String i_str=String.valueOf(Integer.parseInt((String)dmlle.get(i)));
						String j_str=String.valueOf(Integer.parseInt(values));
						if(recset.next()){
							String value2=PubFunc.round(recset.getString(1),digitalResults[Integer.parseInt(values)][Integer.parseInt((String)dmlle.get(i))]);
							String pagevalue=PubFunc.round((String)valueMap.get(values),digitalResults[Integer.parseInt(values)][Integer.parseInt((String)dmlle.get(i))]);
							if(!pagevalue.equals(value2)){
								if(i_str.length()==1) {
                                    i_str=" "+i_str;
                                }
								if(j_str.length()==1) {
                                    j_str=" "+j_str;
                                }
								temp.append(j_str+"行"+i_str+"列汇总数据与直属单位汇总数据不平");
								hhm.put(j_str.trim(), temp.toString());
							}
						}
					}
					for(int kk=1;kk<=rows;kk++){
						String temp=(String)hhm.get(String.valueOf(kk));
						if(temp==null||temp.length()==0){
							continue;
						}
						eifo.append(temp);
					}
					if(eifo.length()>0)
					{
						if(info!=null&&info.length()!=0) {
                            info+="**";
                        }
						info+=eifo.toString();
					}
					
				}
			
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return info;
	}
	
	
	
	
	
//	直属汇总-基层汇总比较
	public boolean compareCollect(String unitcode,ArrayList tabidList)
	{
		boolean issuccess=true;
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			TTorganization ttorganization=new TTorganization(this.conn);
			//取得所有基层单位
			ArrayList basic_unitcodeList=ttorganization.getGrassRootsUnit(unitcode);
			//取得所有直属单位
			ArrayList  unitChildcodeList=ttorganization.getUnderUnitList(unitcode);
			
			String a_unitcode="";
			String a_basiccode="";
			
			StringBuffer unitcode_str=new StringBuffer("");			//直属单位sql
			StringBuffer basic_unitcode_str=new StringBuffer("");			//基层单位sql
			for(int i=0;i<unitChildcodeList.size();i++)
			{				
				DynaBean temp=(DynaBean)unitChildcodeList.get(i);				
				unitcode_str.append(" or unitcode='"+((String)temp.get("unitcode"))+"'");
				a_unitcode=(String)temp.get("unitcode");
			}
			for(int i=0;i<basic_unitcodeList.size();i++)
			{
				RecordVo temp=(RecordVo)basic_unitcodeList.get(i);				
				basic_unitcode_str.append(" or unitcode='"+temp.getString("unitcode")+"'");
				a_basiccode=temp.getString("unitcode");
			}
			
			
			for(Iterator t=tabidList.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				String[] tt=temp.split("§");
				
				
				recset=dao.search("select unitcode from tt_organization where ( report not like '%,"+tt[0]+",%' or report is null ) and ("+basic_unitcode_str.substring(3)+")");
				StringBuffer a_basic_unitcode_str=new StringBuffer("");
				while(recset.next())
				{
					a_basic_unitcode_str.append(" or unitcode='"+((String)recset.getString("unitcode"))+"'");
				}
				
				
				dao.delete("delete from tt_"+tt[0]+" where unitcode='"+unitcode+"'",new ArrayList());
				recset=dao.search("select * from tt_"+tt[0]+" where 1=2 ");
				ResultSetMetaData data=recset.getMetaData();
				int cols=data.getColumnCount()-2;
				
				StringBuffer sql_insert=new StringBuffer("insert into  tt_"+tt[0]+" (unitcode,secid");
				StringBuffer sql_value=new StringBuffer("");
				
				StringBuffer sql_f=new StringBuffer("insert into  tt_"+tt[0]+" (unitcode,secid");
			
				StringBuffer sql_w1=new StringBuffer(" select '"+unitcode+"' unitcode,secid");	//直属
				StringBuffer sql_w2=new StringBuffer(" select '"+unitcode+"' unitcode,secid");   //基层
				for(int i=1;i<=cols;i++)
				{
					sql_insert.append(",C"+i);
					sql_value.append(",0");
					sql_f.append(",C"+i);
					sql_w1.append(",sum(C"+i+") C"+i);	
					sql_w2.append(",sum(C"+i+") C"+i);
				}
				sql_w1.append(" from tt_"+tt[0]+" where ( ");
				sql_w1.append(unitcode_str.substring(3)+" ) group by secid");
				sql_w2.append(" from tt_"+tt[0]+" where ( ");
				sql_w2.append(a_basic_unitcode_str.substring(3)+" ) group by secid");
				/***********防止某一方没有数据************/
				int w1_count=0;
				int w2_count=0;
				RowSet rowSet2=dao.search("select count(secid)a_count from ("+sql_w1+") a");
				if(rowSet2.next()) {
                    w1_count=rowSet2.getInt("a_count");
                }
				rowSet2=dao.search("select count(secid) a_count from ("+sql_w2+") a");
				if(rowSet2.next()) {
                    w2_count=rowSet2.getInt("a_count");
                }
				if(w1_count==0&&w2_count!=0)
				{
					for(int i=1;i<=w2_count;i++)
					{
						dao.insert(sql_insert.toString()+" ) values ('"+a_unitcode+"',"+i+sql_value.toString()+")",new ArrayList());
					}
					
				}
				if(w1_count!=0&&w2_count==0)
				{
					for(int i=1;i<=w1_count;i++)
					{
						dao.insert(sql_insert.toString()+" ) values ('"+a_basiccode+"',"+i+sql_value.toString()+")",new ArrayList());
					}
				}
				/**********************************/
				
				
				StringBuffer select_str=new StringBuffer("select '"+unitcode+"',a1.secid");
				for(int i=1;i<=cols;i++) {
                    select_str.append(",a1.C"+i+"-a2.C"+i);
                }
				select_str.append(" from ( "+sql_w1.toString()+" ) a1,(");
				select_str.append(sql_w2+" ) a2, treport_ctrl c  where a1.secid=a2.secid  and a1.unitcode =c.unitcode and a2.unitcode = c.unitcode and c.tabid='"+tt[0]+"' and c.status = '1' ");
				
				sql_f.append(" ) "+select_str.toString());
				
				dao.insert(sql_f.toString(),new ArrayList());
				// 直属汇总-基层汇总 （参数）比较
				compareCollectParam(tt[0],unitcode,unitcode_str.substring(3),a_basic_unitcode_str.substring(3));
				
				if(data!=null) {
                    data=null;
                }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			issuccess=false;
		}
		
		
		return issuccess;
	}
	
//////////////////////////////////////////////      start       //////////////////////////////////////////////////////////	
	/**
	 * 直属汇总-基层汇总 （参数）比较
	 * @param tabid   
	 * @param unitcode
	 * @param unitcode_str  //直属单位sql
	 * @param base_unitcode_str //基层单位sql
	 */
	public void compareCollectParam(String tabid,String unitcode,String unitcode_str,String base_unitcode_str)
	{
		ArrayList paramColectList=getCollectParamList(tabid);
		ArrayList setParamList=getColectParamListByScope("1",paramColectList);   //表类参数
		ArrayList p_ParamList=getColectParamListByScope("0",paramColectList);    //全局参数
		ArrayList tabParamList=getColectParamListByScope("2",paramColectList);   //表参数
		DbWizard dbWizard=new DbWizard(this.conn);
		TnameExtendBo tnameExtendBo=new TnameExtendBo(this.conn);
		if(setParamList.size()>0)
		{
			String sortid="";
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet recset=null;
			try
			{
				recset=dao.search("select tsortid from tname where tabid="+tabid);
				if(recset.next()) {
                    sortid=recset.getString("tsortid");
                }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			tnameExtendBo.isExistAppealParamTable(2,"",sortid,dbWizard);  //表类参数			
			updateOrinsertParam("1",tabid,sortid,unitcode,unitcode_str,base_unitcode_str,setParamList);
		}
		if(p_ParamList.size()>0)
		{
			tnameExtendBo.isExistAppealParamTable(1,"","",dbWizard);  //全局参数
			updateOrinsertParam("0",tabid,"",unitcode,unitcode_str,base_unitcode_str,p_ParamList);
		}
		if(tabParamList.size()>0)
		{
			tnameExtendBo.isExistAppealParamTable(3,tabid,"",dbWizard);  //表参数
			updateOrinsertParam("2",tabid,"",unitcode,unitcode_str,base_unitcode_str,tabParamList);
		}
	}
	
	
	
	/**
	 * 
	 * @param type					0:全局参数  1：表类参数  2：表参数
	 * @param tabid
	 * @param sortid
	 * @param unitcode
	 * @param unitcode_str          直属单位sql
	 * @param base_unitcode_str     基层单位sql
	 */
	public void updateOrinsertParam(String type,String tabid,String sortid,String unitcode,String unitcode_str,String base_unitcode_str,ArrayList paramList)
	{
		
		String tableName=getTableName(type,tabid,sortid);  //得到表名
		boolean isRecord=isRecord(unitcode,tableName);     //是否已有记录
		HashMap unitValueMap=getParamValueMap(tableName,unitcode_str,paramList);              //直属单位
		HashMap base_unitValueMap=getParamValueMap(tableName,base_unitcode_str,paramList);    //基层单位
		StringBuffer sql=new StringBuffer("");
		if(isRecord)
		{
			StringBuffer sql1=new StringBuffer("");			
			for(int i=0;i<paramList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)paramList.get(i);
				sql1.append(","+(String)abean.get("paramename")+"=("+unitValueMap.get((String)abean.get("paramename"))+"-"+base_unitValueMap.get((String)abean.get("paramename"))+")");
			}
			sql.append("update "+tableName+" set "+sql1.substring(1)+" where unitcode='"+unitcode+"'");
		}
		else
		{
			StringBuffer sql1=new StringBuffer("");
			StringBuffer sql2=new StringBuffer("");
			for(int i=0;i<paramList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)paramList.get(i);
				sql1.append(","+(String)abean.get("paramename"));
				sql2.append(","+unitValueMap.get((String)abean.get("paramename"))+"-"+base_unitValueMap.get((String)abean.get("paramename")));
			}
			sql.append("insert into "+tableName+"(unitcode"+sql1.toString()+")values("+unitcode+sql2.toString()+")");
		}
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			dao.insert(sql.toString(),new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	public HashMap getParamValueMap(String tableName,String unitcode_str,ArrayList paramList)
	{
		HashMap map=new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		StringBuffer sql=new StringBuffer("");
		for(int i=0;i<paramList.size();i++)
		{
			LazyDynaBean abean=(LazyDynaBean)paramList.get(i);
			sql.append(",sum("+(String)abean.get("paramename")+") "+(String)abean.get("paramename"));
		}
		try
		{
			recset=dao.search("select "+sql.substring(1)+" from "+tableName+" where "+unitcode_str);
			if(recset.next())
			{
				for(int i=0;i<paramList.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)paramList.get(i);
					if(recset.getString((String)abean.get("paramename"))!=null)
					{
						map.put((String)abean.get("paramename"),recset.getString((String)abean.get("paramename")));
					}
					else {
                        map.put((String)abean.get("paramename"),"0");
                    }
				}
			}
			else
			{
				for(int i=0;i<paramList.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)paramList.get(i);
					map.put((String)abean.get("paramename"),"0");
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	//是否存在纪录
	public boolean isRecord(String unitcode,String tableName)
	{
		boolean flag=false;
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			recset=dao.search("select * from "+tableName+" where unitcode='"+unitcode+"'");
			if(recset.next())
			{
				flag=true;
			}	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	
	public String getTableName(String flag,String tabid,String sortid)
	{
		String tablename="";
		if("1".equals(flag)) {
            tablename="tt_s"+sortid;
        } else if("0".equals(flag)) {
            tablename="tt_p";
        } else if("2".equals(flag)) {
            tablename="tt_t"+tabid;
        }
		return tablename;
	}
	
	/**
	 * 
	 * @param flag  0:全局参数  1：表类参数  2：表参数
	 * @param list
	 * @return
	 */
	public ArrayList getColectParamListByScope(String flag,ArrayList list)
	{
		ArrayList lists=new ArrayList();
		for(Iterator t=list.iterator();t.hasNext();)
		{
			LazyDynaBean abean=(LazyDynaBean)t.next();
			if(((String)abean.get("paramscope")).equals(flag)) {
                lists.add(abean);
            }
		}
		return lists;
	}
	
	public HashMap getSortid(ArrayList tabid){
		HashMap map=new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try {
		if(tabid!=null&&tabid.size()>0){
			StringBuffer sql=new StringBuffer();
			for(int i=0;i<tabid.size();i++){
				sql.append(" select tsortid from tname where tabid='");
				String tabids=(String)tabid.get(i);
				sql.append(tabids);
				sql.append("'");
				String sortid="";
				recset=dao.search(sql.toString());
				if(recset.next()){
				  sortid=recset.getString(1);
				}
				if(sortid==null||"".equalsIgnoreCase(sortid)){
					
				}else{
					map.put(tabids, sortid);
				}
			}
		}
		} catch (SQLException e) {
		
			e.printStackTrace();
		}
		return map;
	}
	
	
	/**
	 * 得到报表的汇总参数信息集
	 * @param tabid
	 * @return
	 */
	public ArrayList getCollectParamList(String tabid)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			recset=dao.search("select tparam.* from tparam,tpage where tpage.hz=tparam.paramname and tpage.flag=9 and tpage.tabid="+tabid+" and paramsum=1");
			while(recset.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("paramename",recset.getString("paramename"));
				abean.set("paramscope",recset.getString("paramscope"));
				list.add(abean);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
//////////////////////////////////////////////////  end     ///////////////////////////////////////////////////////	
	
	
	
	//判断该统计结果表是否已存在,不存在才有返回信息
	public ArrayList isExistTables(ArrayList tabidList)
	{
		ArrayList list=new ArrayList();
		ArrayList infoList=new ArrayList();
		ArrayList tablist=new ArrayList();
		try
		{
			DbWizard dbWizard=new DbWizard(this.conn);
			for(Iterator t=tabidList.iterator();t.hasNext();)
			{
				String temp=SafeCode.decode((String)t.next());				
				String[] tt=temp.split("§");
				String tabid=tt[0];
				if(!isExistTable(tabid,dbWizard))
				{
					infoList.add(tt[0]+":"+tt[1]+" "+ResourceFactory.getProperty("eidt_report.noUnitAppeal"));
				}
				else {
                    tablist.add(temp);
                }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		list.add(infoList);
		list.add(tablist);
		return list;
	}
	
	
	
	
	//判断该统计结果表是否已存在
	public boolean  isExistTable(String tabid,DbWizard dbWizard)
	{
		boolean flag=true;
		try
		{
			Table table=new Table("tt_"+tabid);			
			if(!dbWizard.isExistTable(table.getName(),false))
			{
				flag=false;
				//如果不存在该统计结果表，则新建一个
				ArrayList fieldList=getTT_TableFields(tabid);
				for(Iterator t=fieldList.iterator();t.hasNext();)
				{
					Field temp=(Field)t.next();
					table.addField(temp);
				}
				table.setCreatekey(false);					
				dbWizard.createTable(table);	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	
	
	
	/**
	 * 得到统计结果表中列的集合
	 * @param  
	 * @param  
	 * @param  
	 * @return
	 */
	public ArrayList getTT_TableFields(String tabid)
	{

		ArrayList fieldsList=new ArrayList();	
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			TnameBo tnameBo=new TnameBo(this.conn,tabid);
			TgridBo tgridBo=new TgridBo(this.conn);
			int cols=tnameBo.getRowInfoBGrid().size();
			fieldsList.add(tgridBo.getField1("unitcode",ResourceFactory.getProperty("ttOrganization.unit.unitcode"),"DataType.STRING",30));
			Field temp21=new Field("secid",ResourceFactory.getProperty("ttOrganization.record.secid"));
			temp21.setDatatype(DataType.INT);
			temp21.setKeyable(true);			
			temp21.setVisible(false);			
			fieldsList.add(temp21);
	
		    for(int i=0;i<cols;i++)
		    {
		    	String fieldname="C"+(i+1);
				Field obj=tgridBo.getField2(fieldname,fieldname,"N");
				fieldsList.add(obj);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	/*	finally
		{
			try
			 {
				 if(recset!=null)
					 recset.close();
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}*/
	    return fieldsList;
	}

	
	
	/**
	 * 逐层汇总
	 * @param unitcode
	 * @param tabidList 报表id
	 * @return
	 */
	public boolean layerCollect(String unitcode,ArrayList tabidList,ArrayList noLeafUnitList)
	{
		boolean issuccess=true;
		ArrayList layerUnitList=new ArrayList();
		HashMap subUnitMap=new HashMap();
		try
		{
			
			
			TTorganization ttOrganization=new TTorganization(this.conn);
		//	ArrayList allUnitInfoList=ttOrganization.getAllUnitInfo();
			ArrayList unitcodeList=new ArrayList();
			RecordVo vo=ttOrganization.getSelfUnit2(unitcode);
			unitcodeList.add(vo);
			layerUnitList.add(unitcodeList);	
			
			//递归查找子单位的信息，用于逐层汇总
			int layNum=ttOrganization.getOrganizationLayNum(subUnitMap);
		//	ttOrganization.findSubUnit_layer(allUnitInfoList,layerUnitList,unitcodeList,subUnitMap);
			deleteNoLeafNode(layerUnitList,subUnitMap,tabidList);
			
			
			HashMap tableColumnNumMap=getTabColumnNums(tabidList);
			ArrayList list=getParamList(tabidList);
			ArrayList paramList=(ArrayList)list.get(0);
			HashSet   tabidSet=(HashSet)list.get(1);
			HashSet   sortidSet=(HashSet)list.get(2);
			ArrayList commonParamList=(ArrayList)list.get(3);
			for(int i=layNum-1;i>=1;i--)
			{
				ArrayList a_layList=(ArrayList)subUnitMap.get(String.valueOf(i));
				ArrayList sonList=(ArrayList)subUnitMap.get(String.valueOf(i+1));
				for(Iterator t=a_layList.iterator();t.hasNext();)
				{
					LazyDynaBean a_vo=(LazyDynaBean)t.next();
				//	ArrayList subUnitList=(ArrayList)subUnitMap.get(a_vo.getString("unitcode"));
				//	if(subUnitList.size()>0)
					{
						collectReport(a_vo,tabidList,new ArrayList(),tableColumnNumMap,commonParamList,paramList,tabidSet,sortidSet,sonList);
						noLeafUnitList.add(a_vo.get("unitcode"));
					}
				}
				
			}
			
		/*	for(int i=layerUnitList.size()-3;i>=0;i--)
			{
				ArrayList a_layList=(ArrayList)layerUnitList.get(i);
				for(Iterator t=a_layList.iterator();t.hasNext();)
				{
					RecordVo a_vo=(RecordVo)t.next();
					ArrayList subUnitList=(ArrayList)subUnitMap.get(a_vo.getString("unitcode"));
					if(subUnitList.size()>0)
					{
						collectReport(a_vo.getString("unitcode"),tabidList,subUnitList,tableColumnNumMap,commonParamList,paramList,tabidSet,sortidSet);
						noLeafUnitList.add(a_vo.getString("unitcode"));
					}
				}
			}*/
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			issuccess=false;
		}
		return issuccess;
	}
	
	
	//取得每个表的列数
	public HashMap getTabColumnNums(ArrayList tabidList)
	{
		HashMap map=new HashMap();
		RowSet recset=null;
		Connection selfConn=null;
		
		try
		{
			selfConn=AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(selfConn);
			for(Iterator t=tabidList.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				String[] tt=temp.split("§");
				recset=dao.search("select * from tt_"+tt[0]+" where 1=2 ");
				ResultSetMetaData data=recset.getMetaData();
				int cols=data.getColumnCount()-2;
				map.put(tt[0],String.valueOf(cols));
				if(data!=null) {
                    data=null;
                }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
		finally
		{
			try
			 {				
				 if(recset!=null) {
                     recset.close();
                 }
				 if(selfConn!=null) {
                     selfConn.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		
		return map;
	}
	
	
	
	
	//删除所有非叶结点的汇总数据
	public void deleteNoLeafNode(ArrayList layerUnitList,HashMap subUnitMap,ArrayList tabidList)
	{
		Connection selfConn=null;
		StringBuffer sql_unit=new StringBuffer("");
		try
		{
			selfConn=AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(selfConn);
		/*	for(int i=layerUnitList.size()-3;i>=0;i--)
			{
				ArrayList a_layList=(ArrayList)layerUnitList.get(i);
				for(Iterator t=a_layList.iterator();t.hasNext();)
				{
					RecordVo a_vo=(RecordVo)t.next();
					ArrayList subUnitList=(ArrayList)subUnitMap.get(a_vo.getString("unitcode"));
					if(subUnitList.size()>0)
						sql_unit.append(",'"+a_vo.getString("unitcode")+"'");
						//collectReport(a_vo.getString("unitcode"),tabidList,subUnitList);
				}
			}*/
			Calendar d = Calendar.getInstance();
    	    int year = d.get(Calendar.YEAR);
    	    int month = d.get(Calendar.MONTH)+1;
    	    int day = d.get(Calendar.DAY_OF_MONTH);
    	    String date = year+"-"+month+"-"+day;
			String strSql = "";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                strSql = " and start_date<=to_date('"+date+"','yyyy-mm-dd') and end_date>=to_date('"+date+"','yyyy-mm-dd') ";
            } else {
                strSql = " and start_date<=convert(datetime,'"+date+"') and end_date>=convert(datetime,'"+date+"') ";
            }
			
			for(Iterator t=tabidList.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				String[] tt=temp.split("§");
			//	dao.delete("delete from tt_"+tt[0]+" where unitcode in ("+sql_unit.substring(1)+")",new ArrayList());
				dao.delete("delete from tt_"+tt[0]+" where exists (select parentid from tt_organization where parentid=tt_"+tt[0]+".unitcode and ( report not like '%,"+tt[0]+",%' or report is null ) "+strSql+" )",new ArrayList());
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
		finally
		{
			try
			 {				
				
				 if(selfConn!=null) {
                     selfConn.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
	}
	
	
	
	/**
	 * 直属单位报表汇总
	 * @param unitcode   汇总单位的编码
	 * @param tabidList		报表id
	 * @param unitcodelist	子单位编码
	 */
	public boolean collectReport(LazyDynaBean a_vo,ArrayList tabidList,ArrayList unitcodelist,HashMap tableColumnNumMap,ArrayList commonParamList,ArrayList paramList,HashSet tabidSet,HashSet sortidSet,ArrayList<LazyDynaBean> sonList)
	{
		boolean issuccess=true;
		String unitcode = (String)a_vo.get("unitcode");
		RowSet recset=null;
		try
		{
			
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer unitcode_str=new StringBuffer("");			
		/*	for(int i=0;i<unitcodelist.size();i++)
			{
				String temp=(String)unitcodelist.get(i);
				String[] tt=temp.split("§");
				unitcode_str.append(" or unitcode='"+tt[0]+"'");
			}*/
			
			String leafSql = "'0000',";//虚拟code  避免报错
			String unleafSql = "'0000',";
			for(LazyDynaBean bean : sonList){//xiegh add 20170717  bug:29563
				
				String currcode = (String)bean.get("unitcode");
				String parentcode = (String)bean.get("parentid");
				String isleaf = (String)bean.get("isleaf");
				if(unitcode.equalsIgnoreCase(parentcode)&&"true".equals(isleaf)){
					leafSql=leafSql+"'"+currcode+"',";
				}
				if(unitcode.equalsIgnoreCase(parentcode)&&"false".equals(isleaf)){
					unleafSql=unleafSql+"'"+currcode+"',";
				}
			}
			leafSql=leafSql.substring(0, leafSql.length()-1);
			unleafSql=unleafSql.substring(0, unleafSql.length()-1);
			a_vo.set("leafSql", leafSql);
			a_vo.set("unleafSql", unleafSql);
			DbWizard dbWizard=new DbWizard(this.conn);
			TnameExtendBo tnameExtendBo=new TnameExtendBo(this.conn);
			TnameBo tnameBo=new TnameBo(this.conn);
			StringBuffer tabid_str=new StringBuffer("");
			
			HashMap isSubMap=new HashMap();
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			for(Iterator t=tabidList.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				String[] tt=temp.split("§");
				tabid_str.append(","+tt[0]);
			
				recset=dao.search("select unitcode from tt_organization where  ( report not like '%,"+tt[0]+",%' or report is null ) and parentid='"+unitcode+"' "+ext_sql+" ");
				if(recset.next())
				{
					isSubMap.put(unitcode+"/"+tt[0],"1");
					int cols=Integer.parseInt((String)tableColumnNumMap.get(tt[0]));
					StringBuffer sql_f=new StringBuffer("insert into  tt_"+tt[0]+" (unitcode,secid");
					StringBuffer sql_w=new StringBuffer(" select '"+unitcode+"',secid");
					for(int i=1;i<=cols;i++)
					{
						sql_f.append(",C"+i);
						sql_w.append(",sum(C"+i+")");	
					}
					sql_f.append(" )");
					sql_f.append(sql_w.toString());
					sql_f.append(" from tt_"+tt[0]+" a where ");
					sql_f.append(" EXISTS ( (select unitcode from tt_organization where  a.unitcode=tt_organization.unitcode   and  ( report not like '%,"+tt[0]+",%'  or report is null ) and unitcode<>'"+unitcode+"' and unitcode in ( "+ a_vo.get("unleafSql")+" ) "+ext_sql+" )");
					sql_f.append(" union all (select b.unitcode from tt_organization b, treport_ctrl c where  a.unitcode=b.unitcode  and  c.unitcode=b.unitcode  and  a.unitcode=c.unitcode  and c.tabid ='"+tt[0]+"' and c.status ='1'  and  ( report not like '%,"+tt[0]+",%'  or report is null ) and b.unitcode<>'"+unitcode+"' and b.unitcode in ( "+ a_vo.get("leafSql")+" ) "+ext_sql+" ))");
					sql_f.append(" group by secid");
					dao.insert(sql_f.toString(),new ArrayList());
				//	tnameBo.upOrInsertReport_ctrl(unitcode,tt[0],"",0);
					
				}
			}
			//参数汇总
			//getCollectParamInfo2(tabid_str.substring(1),unitcode,unitcode_str.substring(3),commonParamList,paramList,tabidSet,sortidSet);
			getCollectParamInfo2(tabid_str.substring(1),unitcode,"",commonParamList,paramList,tabidSet,sortidSet,isSubMap);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			issuccess=false;
		}
		
		return issuccess;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 直属单位报表汇总
	 * @param unitcode   汇总单位的编码
	 * @param tabidList		报表id
	 * @param unitcodelist	子单位编码
	 */
	public boolean collectReport(String unitcode,ArrayList tabidList,ArrayList unitcodelist)
	{
		boolean issuccess=true;
		
		ResultSet recset=null;
		Connection selfConn=null;
		Statement statement=null;
		try
		{
			selfConn=AdminDb.getConnection();
			statement=selfConn.createStatement();
			//ContentDAO dao=new ContentDAO(selfConn);
			StringBuffer unitcode_str=new StringBuffer("");	
			String a_temp="";
			for(int i=0;i<unitcodelist.size();i++)
			{
				a_temp=(String)unitcodelist.get(i);
				String[] tt=a_temp.split("§");
				unitcode_str.append(",'"+tt[0]+"'");
				//unitcode_str.append(" or unitcode='"+tt[0]+"'");
			}
			
			if(unitcode_str.length()==0) {
                return issuccess;
            }
			
			DbWizard dbWizard=new DbWizard(this.conn);
			TnameExtendBo tnameExtendBo=new TnameExtendBo(this.conn);
			TnameBo tnameBo=new TnameBo(this.conn);
			StringBuffer tabid_str=new StringBuffer("");
			
			for(Iterator t=tabidList.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				String[] tt=temp.split("§");
				//dao.delete("delete from tt_"+tt[0]+" where unitcode='"+unitcode+"'",new ArrayList());
				DbSecurityImpl impl = new DbSecurityImpl();
				try {
					
					String sql = "delete from tt_"+tt[0]+" where unitcode='"+unitcode+"'";
					impl.open(selfConn, sql);
					statement.execute(sql);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					impl.close(selfConn);
				}
				tabid_str.append(","+tt[0]);
				
				DbSecurityImpl impl2 = new DbSecurityImpl();
				StringBuffer a_unitcode_str=new StringBuffer("");
				try {
					String sql = "select unitcode from tt_organization where ( report not like '%,"+tt[0]+",%'  or report is null ) and unitcode in ("+unitcode_str.substring(1)+")";
					impl2.open(selfConn, sql);
					recset=statement.executeQuery(sql);
					while(recset.next())
					{
						a_unitcode_str.append(",'"+recset.getString("unitcode")+"'");
					}
					if(a_unitcode_str.length()==0) {
                        continue;
                    }
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					PubFunc.closeDbObj(recset);
					impl2.close(selfConn);
				}
				//recset=dao.search("select * from tt_"+tt[0]+" where 1=2 ");
				DbSecurityImpl impl3 = new DbSecurityImpl();
				ResultSetMetaData data=null;
				try {
					String sql = "select * from tt_"+tt[0]+" where 1=2 ";
					impl3.open(selfConn, sql);
					recset=statement.executeQuery(sql);
					data=recset.getMetaData();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					PubFunc.closeDbObj(recset);
					impl3.close(selfConn);
				}
				
				int cols=data.getColumnCount()-2;
				
				StringBuffer sql_f=new StringBuffer("insert into  tt_"+tt[0]+" (unitcode,secid");
				StringBuffer sql_w=new StringBuffer(" select '"+unitcode+"',secid");
				for(int i=1;i<=cols;i++)
				{
					sql_f.append(",C"+i);
					sql_w.append(",sum(C"+i+")");	
				}
				sql_f.append(" )");
				sql_f.append(sql_w.toString());
				sql_f.append(" from tt_"+tt[0]+" a , treport_ctrl b  where  a.unitcode = b.unitcode  and b.status = '1' and  b.tabid = '"+tt[0]+"' and a.unitcode in ( ");
				sql_f.append(a_unitcode_str.substring(1)+" ) group by secid");
				
				//dao.insert(sql_f.toString(),new ArrayList());
				DbSecurityImpl impl4 = new DbSecurityImpl();
				try {
					impl4.open(selfConn, sql_f.toString());
					statement.executeUpdate(sql_f.toString());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					impl4.close(selfConn);
				}
				tnameBo.upOrInsertReport_ctrl(unitcode,tt[0],"",0);
				
				if(data!=null) {
                    data=null;
                }
				
			}
			//参数汇总
			getCollectParamInfo(tabid_str.substring(1),unitcode,unitcode_str.toString());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			issuccess=false;
		}
		finally
		{

			PubFunc.closeResource(recset);
			PubFunc.closeResource(statement);
			PubFunc.closeResource(selfConn);
		}
		return issuccess;
	}
	
	
	
	public ArrayList getParamList(ArrayList tabidList)
	{
		
		StringBuffer tabid_str=new StringBuffer("");
		for(Iterator t=tabidList.iterator();t.hasNext();)
		{
			String temp=(String)t.next();
			String[] tt=temp.split("§");	
			tabid_str.append(","+tt[0]);
		}
		
		ArrayList list=new ArrayList();
		ArrayList paramList=new ArrayList();
		HashSet tabidSet=new HashSet();
		HashSet sortidSet=new HashSet();
		ArrayList commonParamList=new ArrayList();
		RowSet recset=null;
		Connection selfConn=null;
		try {
			selfConn = AdminDb.getConnection();
			DbWizard dbWizard = new DbWizard(selfConn);
			
			ContentDAO dao=new ContentDAO(selfConn);
			String sql="select tp.paramname,tp.paramename,tp.paramfmt,tp.paramlen,tp.paramscope,tableParam.tabid,tname.tsortid  from tparam tp ,"
					+"( select hz,tabid from tpage where flag=9 and tabid in ("+tabid_str.substring(1)+") ) tableParam,tname "
					+" where tp.paramname=tableParam.hz and tp.paramtype='数值'  and tp.paramsum=1 and tname.tabid=tableParam.tabid ";
			recset=dao.search(sql);
			while(recset.next())
			{
				DynaBean bean = new LazyDynaBean();
				bean.set("paramname",recset.getString("paramname"));
				bean.set("paramename",recset.getString("paramename"));
				bean.set("paramfmt",recset.getString("paramfmt"));
				bean.set("paramlen",recset.getString("paramlen"));
				bean.set("paramscope",recset.getString("paramscope"));
				bean.set("tabid",recset.getString("tabid"));
				bean.set("tsortid",recset.getString("tsortid"));
				paramList.add(bean);
				
				if("2".equals(recset.getString("paramscope"))) {
                    tabidSet.add(recset.getString("tabid"));
                }
				if("1".equals(recset.getString("paramscope"))) {
                    sortidSet.add(recset.getString("tsortid"));
                }
				if("0".equals(recset.getString("paramscope"))) {
                    commonParamList.add(bean);
                }
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (recset != null) {
                    recset.close();
                }
				if (selfConn != null) {
                    selfConn.close();
                }

			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
		list.add(paramList);
		list.add(tabidSet);
		list.add(sortidSet);
		list.add(commonParamList);
		
		return list;
	}
	
	
	
// 对需汇总的 表内\表类\全局参数 进行汇总操作(逐层汇总)
	public void  getCollectParamInfo2(String tabid_str,String unitcode,String unitcode_str,ArrayList commonParamList,ArrayList paramList,HashSet tabidSet,HashSet sortidSet,HashMap isSubMap)
	{

		
		try
		{
			RowSet recset=null;
			DbWizard dbWizard=new DbWizard(this.conn);
			TnameExtendBo tnameExtendBo=new TnameExtendBo(this.conn);
			ContentDAO dao=new ContentDAO(this.conn);
			//全局参数
			
		/*	StringBuffer common_str=new StringBuffer("");
			StringBuffer common_str2=new StringBuffer("");
			for(Iterator t=commonParamList.iterator();t.hasNext();)
			{
				DynaBean bean=(DynaBean)t.next();
				common_str.append(","+(String)bean.get("paramename"));		
				common_str2.append(",sum("+(String)bean.get("paramename")+")");	
			}*/
		//	boolean isSub=false;
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("tt_organization.end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("tt_organization.end_date")+"="+yy+" and "+Sql_switcher.month("tt_organization.end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("tt_organization.end_date")+"="+yy+" and "+Sql_switcher.month("tt_organization.end_date")+"="+mm+" and "+Sql_switcher.day("tt_organization.end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("tt_organization.start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("tt_organization.start_date")+"="+yy+" and "+Sql_switcher.month("tt_organization.start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("tt_organization.start_date")+"="+yy+" and "+Sql_switcher.month("tt_organization.start_date")+"="+mm+" and "+Sql_switcher.day("tt_organization.start_date")+"<="+dd+" ) ) ");	 			
			
			if(commonParamList.size()>0)
			{
				recset=dao.search("select unitcode from tt_organization where  parentid='"+unitcode+"'  and unitcode<>'"+unitcode+"' "+ext_sql+" ");
				if(recset.next())
				{
				//	isSub=true;
					tnameExtendBo.isExistAppealParamTable(1,"","",dbWizard);
					insertNullRecord_param("tt_p",unitcode);
					
					
					for(Iterator t=commonParamList.iterator();t.hasNext();)
					{
						DynaBean bean=(DynaBean)t.next();
						StringBuffer common_str=new StringBuffer("");
						StringBuffer common_str2=new StringBuffer("");
						common_str.append(","+(String)bean.get("paramename"));		
						common_str2.append(",sum("+(String)bean.get("paramename")+")");	
					
						StringBuffer insertStr=new StringBuffer("update tt_p set ");
					/*	if(commonParamList.size()>1)
						{
							insertStr.append("("+common_str.substring(1)+")");
						}
						else if(commonParamList.size()==1)*/
						{
							insertStr.append(common_str.substring(1));
						}
						
						insertStr.append(" =(select "+common_str2.substring(1)+" from tt_p t, tt_organization ");
						insertStr.append(" where t.unitcode=tt_organization.unitcode and parentid='"+unitcode+"' and tt_organization.unitcode<>'"+unitcode+"' "+ext_sql+")");
						insertStr.append(" where tt_p.unitcode='"+unitcode+"' ");
						dao.update(insertStr.toString());
					}
				}
			}
			//表类参数
			if(sortidSet.size()>0)
			{
			
				for(Iterator t=sortidSet.iterator();t.hasNext();)
				{
					String sortid=(String)t.next();
					
				/*	int a=0;
					for(Iterator t1=paramList.iterator();t1.hasNext();)
					{
						DynaBean bean=(DynaBean)t1.next();
						if(((String)bean.get("tsortid")).equals(sortid)&&((String)bean.get("paramscope")).equals("1"))
						{
							a++;
							sort_str.append(","+(String)bean.get("paramename"));		
							sort_str2.append(",sum("+(String)bean.get("paramename")+")");
						}	
					}
					if(a>0) */
					{
						tnameExtendBo.isExistAppealParamTable(2,"",sortid,dbWizard);
						insertNullRecord_param("tt_s"+sortid,unitcode);
						//dao.update("delete from tt_s"+sortid+" where unitcode='"+unitcode+"'");
					
						for(Iterator t1=paramList.iterator();t1.hasNext();)
						{
							DynaBean bean=(DynaBean)t1.next();
							if(((String)bean.get("tsortid")).equals(sortid)&& "1".equals((String)bean.get("paramscope")))
							{
								StringBuffer sort_str=new StringBuffer("");
								StringBuffer sort_str2=new StringBuffer("");
								sort_str.append(","+(String)bean.get("paramename"));		
								sort_str2.append(",sum("+(String)bean.get("paramename")+")");
								
								StringBuffer insertStr=new StringBuffer("update tt_s"+sortid+" set ");
							/*	if(sortidSet.size()>1)
								{
									insertStr.append(" ( "+sort_str.substring(1)+" )");
								}
								else if(sortidSet.size()==1)*/
								{
									insertStr.append(sort_str.substring(1));
								}
								
								insertStr.append(" =(select "+sort_str2.substring(1)+" from tt_s"+sortid+" t, tt_organization ");
								insertStr.append(" where t.unitcode=tt_organization.unitcode and parentid='"+unitcode+"' and tt_organization.unitcode<>'"+unitcode+"' "+ext_sql+")");
								insertStr.append(" where tt_s"+sortid+".unitcode='"+unitcode+"' ");
								dao.update(insertStr.toString());
							}
						}
					}
				}
			}
			
			//表参数
			if(tabidSet.size()>0)
			{
			
				for(Iterator t=tabidSet.iterator();t.hasNext();)
				{
					String tabid=(String)t.next();
				/*	int a=0;
					for(Iterator t1=paramList.iterator();t1.hasNext();)
					{
						DynaBean bean=(DynaBean)t1.next();
						if(((String)bean.get("tabid")).equals(tabid)&&((String)bean.get("paramscope")).equals("2"))
						{
							a++;
							tab_str.append(","+(String)bean.get("paramename"));		
							tab_str2.append(",sum("+(String)bean.get("paramename")+")");
						}	
					}*/
					if(isSubMap.get(unitcode+"/"+tabid)!=null)
					{
						tnameExtendBo.isExistAppealParamTable(3,tabid,"",dbWizard);
						insertNullRecord_param("tt_t"+tabid,unitcode);
						
						for(Iterator t1=paramList.iterator();t1.hasNext();)
						{
							DynaBean bean=(DynaBean)t1.next();
							if(((String)bean.get("tabid")).equals(tabid)&& "2".equals((String)bean.get("paramscope")))
							{
								StringBuffer tab_str=new StringBuffer("");
								StringBuffer tab_str2=new StringBuffer("");
								tab_str.append(","+(String)bean.get("paramename"));		
								tab_str2.append(",sum("+(String)bean.get("paramename")+")");
								StringBuffer insertStr=new StringBuffer("update tt_t"+tabid+" set ");
							/*	if(tabidSet.size()>1)
								{
									insertStr.append(" ( "+tab_str.substring(1)+" )");
								}
								else if(tabidSet.size()==1)*/
								{
									insertStr.append(tab_str.substring(1));
								}
								
								insertStr.append(" =(select "+tab_str2.substring(1)+" from tt_t"+tabid+" t, tt_organization ,treport_ctrl");
								insertStr.append(" where t.unitcode=tt_organization.unitcode  and treport_ctrl.unitcode=t.unitcode  and treport_ctrl.unitcode=tt_organization.unitcode   and  treport_ctrl.tabid='"+tabid+"'  and treport_ctrl.status='1' and ( report not like '%,"+tabid+",%' or report is null ) and tt_organization.unitcode<>'"+unitcode+"'  and parentid='"+unitcode+"' "+ext_sql+")");//xiegh 处理表参数 20170621 关联状态表treport_ctrl
								insertStr.append(" where tt_t"+tabid+".unitcode='"+unitcode+"' ");
								dao.update(insertStr.toString());
							}
						}
						
					}
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	public void insertNullRecord_param(String tname,String unitcode)
	{
		
		try
		{
			
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from "+tname+" where unitcode='"+unitcode+"'");
			boolean is=false;
			if(rowSet.next())
			{
				is=true;
			}
			if(!is)
			{
				RecordVo vo=new RecordVo(tname.toLowerCase());
				vo.setString("unitcode",unitcode);
				dao.addValueObject(vo);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	//对需汇总的 表内\表类\全局参数 进行汇总操作
	public void  getCollectParamInfo(String tabid_str,String unitcode,String unitcode_str)
	{

		
		RowSet recset=null;
		
		try
		{
			
			DbWizard dbWizard=new DbWizard(this.conn);
			TnameExtendBo tnameExtendBo=new TnameExtendBo(this.conn);
			ArrayList paramList=new ArrayList();
			
			HashSet tabidSet=new HashSet();
			HashSet sortidSet=new HashSet();
			ArrayList commonParamList=new ArrayList();
			
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select tp.paramname,tp.paramename,tp.paramfmt,tp.paramlen,tp.paramscope,tableParam.tabid,tname.tsortid  from tparam tp ,"
					+"( select hz,tabid from tpage where flag=9 and tabid in ("+tabid_str+") ) tableParam,tname "
					+" where tp.paramname=tableParam.hz and tp.paramtype='数值'  and tp.paramsum=1 and tname.tabid=tableParam.tabid ";
			recset=dao.search(sql);
			while(recset.next())
			{
				DynaBean bean = new LazyDynaBean();
				bean.set("paramname",recset.getString("paramname"));
				bean.set("paramename",recset.getString("paramename"));
				bean.set("paramfmt",recset.getString("paramfmt"));
				bean.set("paramlen",recset.getString("paramlen"));
				bean.set("paramscope",recset.getString("paramscope"));
				bean.set("tabid",recset.getString("tabid"));
				bean.set("tsortid",recset.getString("tsortid"));
				paramList.add(bean);
				
				if("2".equals(recset.getString("paramscope"))) {
                    tabidSet.add(recset.getString("tabid"));
                }
				if("1".equals(recset.getString("paramscope"))) {
                    sortidSet.add(recset.getString("tsortid"));
                }
				if("0".equals(recset.getString("paramscope"))) {
                    commonParamList.add(bean);
                }
			}
			//全局参数
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("tt_organization.end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("tt_organization.end_date")+"="+yy+" and "+Sql_switcher.month("tt_organization.end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("tt_organization.end_date")+"="+yy+" and "+Sql_switcher.month("tt_organization.end_date")+"="+mm+" and "+Sql_switcher.day("tt_organization.end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("tt_organization.start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("tt_organization.start_date")+"="+yy+" and "+Sql_switcher.month("tt_organization.start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("tt_organization.start_date")+"="+yy+" and "+Sql_switcher.month("tt_organization.start_date")+"="+mm+" and "+Sql_switcher.day("tt_organization.start_date")+"<="+dd+" ) ) ");	 			
			
		//	boolean isSub=false;
			if(commonParamList.size()>0)
			{
				
				recset=dao.search("select unitcode from tt_organization where  parentid='"+unitcode+"'  and unitcode<>'"+unitcode+"' "+ext_sql+" ");
				if(recset.next())
				{
				//	isSub=true;
					tnameExtendBo.isExistAppealParamTable(1,"","",dbWizard);
					insertNullRecord_param("tt_p",unitcode);
					
					HashMap map=new HashMap();
					for(Iterator t=commonParamList.iterator();t.hasNext();)
					{
						DynaBean bean=(DynaBean)t.next();
						if(map.get((String)bean.get("paramename"))==null)
						{
							StringBuffer common_str=new StringBuffer("");
							StringBuffer common_str2=new StringBuffer("");
							common_str.append(","+(String)bean.get("paramename"));		
							common_str2.append(",sum("+(String)bean.get("paramename")+")");
							map.put((String)bean.get("paramename"), "1");
					
					
							StringBuffer insertStr=new StringBuffer("update tt_p set ");
						/*	if(commonParamList.size()>1)
							{
								insertStr.append("("+common_str.substring(1)+")");
							}
							else if(commonParamList.size()==1)*/
							{
								insertStr.append(common_str.substring(1));
							}
							
							insertStr.append(" =(select "+common_str2.substring(1)+" from tt_p t, tt_organization ");
							insertStr.append(" where t.unitcode=tt_organization.unitcode  "+ext_sql+" and t.unitcode in ("+unitcode_str.substring(1)+")  )"); // and parentid='"+unitcode+"' and tt_organization.unitcode<>'"+unitcode+"')");
							insertStr.append(" where tt_p.unitcode='"+unitcode+"' ");
							dao.update(insertStr.toString());

						}
					}
					
					
				}
			}
			//表类参数
			if(sortidSet.size()>0)
			{
				for(Iterator t=sortidSet.iterator();t.hasNext();)
				{
					String sortid=(String)t.next();
					StringBuffer sort_str=new StringBuffer("");
					StringBuffer sort_str2=new StringBuffer("");
				/*	int a=0;
					HashMap amap=new HashMap();
					for(Iterator t1=paramList.iterator();t1.hasNext();)
					{
						DynaBean bean=(DynaBean)t1.next();
						if(((String)bean.get("tsortid")).equals(sortid)&&((String)bean.get("paramscope")).equals("1"))
						{
							if(amap.get((String)bean.get("paramename"))==null)
							{
								a++;
								sort_str.append(","+(String)bean.get("paramename"));		
								sort_str2.append(",sum("+(String)bean.get("paramename")+")");
								amap.put((String)bean.get("paramename"), "1");
							}
						}	
					}
					
					if(a>0) */
					{
						tnameExtendBo.isExistAppealParamTable(2,"",sortid,dbWizard);
						insertNullRecord_param("tt_s"+sortid,unitcode);
						//dao.update("delete from tt_s"+sortid+" where unitcode='"+unitcode+"'");
						HashMap amap=new HashMap();
						for(Iterator t1=paramList.iterator();t1.hasNext();)
						{
							DynaBean bean=(DynaBean)t1.next();
							if(((String)bean.get("tsortid")).equals(sortid)&& "1".equals((String)bean.get("paramscope")))
							{
								if(amap.get((String)bean.get("paramename"))==null)
								{
								
									sort_str.append(","+(String)bean.get("paramename"));		
									sort_str2.append(",sum("+(String)bean.get("paramename")+")");
									amap.put((String)bean.get("paramename"), "1");
							
						
									StringBuffer insertStr=new StringBuffer("update tt_s"+sortid+" set ");
								/*	if(sortidSet.size()>1)
									{
										insertStr.append(" ( "+sort_str.substring(1)+" )");
									}
									else if(sortidSet.size()==1)*/
									{
										insertStr.append(sort_str.substring(1));
									}
									
									insertStr.append(" =(select "+sort_str2.substring(1)+" from tt_s"+sortid+" t, tt_organization ");
									insertStr.append(" where t.unitcode=tt_organization.unitcode  "+ext_sql+"  and t.unitcode in ("+unitcode_str.substring(1)+")  )");      //  and parentid='"+unitcode+"' and tt_organization.unitcode<>'"+unitcode+"')");
									insertStr.append(" where tt_s"+sortid+".unitcode='"+unitcode+"' ");
									dao.update(insertStr.toString());
						
						
								}
							}	
						}
					}
				}
			}
			
			//表参数
			if(tabidSet.size()>0)
			{
			
				for(Iterator t=tabidSet.iterator();t.hasNext();)
				{
					String tabid=(String)t.next();
					HashMap amap=new HashMap();
					recset=dao.search("select unitcode from tt_organization where (  report not like '%,"+tabid+",%' or report is null ) and  parentid='"+unitcode+"'  "+ext_sql+" ");
					if(recset.next())
					{
						tnameExtendBo.isExistAppealParamTable(3,tabid,"",dbWizard);
						insertNullRecord_param("tt_t"+tabid,unitcode); 
						for(Iterator t1=paramList.iterator();t1.hasNext();)
						{
							DynaBean bean=(DynaBean)t1.next();
							if(((String)bean.get("tabid")).equals(tabid)&& "2".equals((String)bean.get("paramscope")))
							{
								if(amap.get((String)bean.get("paramename"))==null)
								{
									StringBuffer tab_str=new StringBuffer("");
									StringBuffer tab_str2=new StringBuffer("");
									tab_str.append(","+(String)bean.get("paramename"));		
									tab_str2.append(",sum("+(String)bean.get("paramename")+")");
									amap.put((String)bean.get("paramename"), "1");
									
									
									StringBuffer insertStr=new StringBuffer("update tt_t"+tabid+" set ");
								/*	if(tabidSet.size()>1)
									{
										insertStr.append(" ( "+tab_str.substring(1)+" )");
									}
									else if(tabidSet.size()==1)*/
									{
										insertStr.append(tab_str.substring(1));
									}
									
									insertStr.append(" =(select "+tab_str2.substring(1)+" from tt_t"+tabid+" t, tt_organization ,treport_ctrl c");
									insertStr.append(" where t.unitcode=tt_organization.unitcode  and t.unitcode=c.unitcode  and  tt_organization.unitcode=c.unitcode  and c.status ='1' and c.tabid='"+tabid+"' and ( report not like '%,"+tabid+",%' or report is null )  "+ext_sql+"  and t.unitcode in ("+unitcode_str.substring(1)+")  )");  //tt_organization.unitcode<>'"+unitcode+"' and parentid='"+unitcode+"' )");
									insertStr.append(" where tt_t"+tabid+".unitcode='"+unitcode+"' ");
									dao.update(insertStr.toString());
									
								}
							}	
						}
					}
				/*	if(a>0)
					{
						recset=dao.search("select unitcode from tt_organization where (  report not like '%,"+tabid+",%' or report is null ) and  parentid='"+unitcode+"'  ");
						if(recset.next())
						{
							
							tnameExtendBo.isExistAppealParamTable(3,tabid,"",dbWizard);
							insertNullRecord_param("tt_t"+tabid,unitcode);
							
							StringBuffer insertStr=new StringBuffer("update tt_t"+tabid+" set ");
							if(tabidSet.size()>1)
							{
								insertStr.append(" ( "+tab_str.substring(1)+" )");
							}
							else if(tabidSet.size()==1)
							{
								insertStr.append(tab_str.substring(1));
							}
							
							insertStr.append(" =(select "+tab_str2.substring(1)+" from tt_t"+tabid+" t, tt_organization ");
							insertStr.append(" where t.unitcode=tt_organization.unitcode  and ( report not like '%,"+tabid+",%' or report is null )  and t.unitcode in ("+unitcode_str.substring(1)+")  )");  //tt_organization.unitcode<>'"+unitcode+"' and parentid='"+unitcode+"' )");
							insertStr.append(" where tt_t"+tabid+".unitcode='"+unitcode+"' ");
							dao.update(insertStr.toString());
						}
						
					}*/
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	
	
	
	/**
	 * 得到与填报信息单位相关联的所有表类涉及到的代码型全局参数和指定的表类涉及到的表类参数
	 * @param unitcode
	 * @param a_sortid 
	 * @return
	 */
	public ArrayList getCommonsParam(String unitcode,HashSet a_sortidSet)
	{
		ArrayList list=new ArrayList();
		Connection con=null;
		RowSet recset=null;
		try
		{
			con=AdminDb.getConnection();
			TTorganization ttorganization=new TTorganization(con);
			//取得本人的填报单位信息
			RecordVo selfvo=ttorganization.getSelfUnit2(unitcode);
			String sortid=selfvo.getString("reporttypes");
			sortid=sortid.substring(0,sortid.lastIndexOf(","));
			ContentDAO dao=new ContentDAO(con);
		
			String sql="select paramename,paramname,paramCode,paramscope,'#' sortid from tparam where paramname in( select hz from tpage where tabid in (select tabid from tname where tsortid in("+sortid+")) and flag=9 ) and paramtype='代码' and paramscope=0 ";
			for(Iterator t=a_sortidSet.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				sql+=" union select paramename,paramname,paramCode,paramscope,'"+temp+"' sortid from tparam where paramname in( select hz from tpage where tabid in (select tabid from tname where tsortid="+temp+") and flag=9 ) and paramtype='代码' and paramscope=1 ";
			}
		
			recset=dao.search(sql);
			while(recset.next())
			{
				DynaBean bean = new LazyDynaBean();
				bean.set("paramename",recset.getString("paramename"));
				bean.set("paramname",recset.getString("paramname"));
				bean.set("paramCode",recset.getString("paramCode"));
				bean.set("paramscope",recset.getString("paramscope"));
				bean.set("sortid",recset.getString("sortid"));
				list.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();		
		}
		finally
		{
			try
			 {
				 if(recset!=null) {
                     recset.close();
                 }
				 if(con!=null) {
                     con.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return list;
		
	}
	
	
	
	/**
	 * 得到与填报信息单位相关联的所有表类涉及到的代码型全局参数和指定的表类涉及到的表类参数
	 * @param unitcode
	 * @param a_sortid 
	 * @return
	 */
	public ArrayList getCommonsParam2(String unitcode,HashSet a_sortidSet)
	{
		ArrayList list=new ArrayList();
		TTorganization ttorganization=new TTorganization(this.conn);
		//取得本人的填报单位信息
		RecordVo selfvo=ttorganization.getSelfUnit2(unitcode);
		String sortid=selfvo.getString("reporttypes");
		sortid=sortid.substring(0,sortid.lastIndexOf(","));
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			String sql="select paramename,paramname,paramCode,paramscope ,'#' sortid from tparam where paramname in( select hz from tpage where tabid in (select tabid from tname where tsortid in("+sortid+")) and flag=9 ) and paramtype='代码' and paramscope=0 ";
			for(Iterator t=a_sortidSet.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
			//	System.out.println("temp="+temp);
				
				sql+=" union select paramename,paramname,paramCode,paramscope,'#' sortid  from tparam where paramname in( select hz from tpage where tabid in (select tabid from tname where tsortid="+temp+") and flag=9 ) and paramtype='代码' and paramscope=1 ";
			}
		//	System.out.println(sql);
			recset=dao.search(sql);
			while(recset.next())
			{
				DynaBean bean = new LazyDynaBean();
				bean.set("paramename",recset.getString("paramename"));
				bean.set("paramname",recset.getString("paramname"));
				bean.set("paramCode",recset.getString("paramCode"));
				bean.set("paramscope",recset.getString("paramscope"));	
				bean.set("sortid",recset.getString("sortid"));
				list.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();		
		}
	/*	finally
		{
			try
			 {
				 if(recset!=null)
					 recset.close();
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}*/
		return list;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	//根据代码集id得到相关代码子集  flag 0: CommonData  1:DynaBean
	public ArrayList getCodeItemList(String codesetID,int flag)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			recset=dao.search("select * from codeitem where codesetid='"+codesetID+"'");
			while(recset.next())
			{
				if(flag==0)
				{
					String codeid=recset.getString("codeitemid");
					String codeName=recset.getString("codeitemdesc");
					CommonData dataobj = new CommonData(codeid,codeName);
					list.add(dataobj);
				}
				else
				{
					DynaBean bean = new LazyDynaBean();
					bean.set("codeitemid",recset.getString("codeitemid"));
					bean.set("codeitemdesc",recset.getString("codeitemdesc"));
					list.add(bean);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();		
		}
	/*	finally
		{
			try
			 {
				 if(recset!=null)
					 recset.close();
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}*/
		return list;
	}
	
	
	
	
	/**
	 * 根据复杂条件取得基层单位编码信息
	 * @param relationList		与或信息
	 * @param paramenameList	参数信息
	 * @param operateList		比较信息
	 * @param codeValue			参数代码值
	 * @return
	 */
	public ArrayList getComplexConditionUnit(String unitcode,ArrayList relationList,ArrayList paramenameList,ArrayList operateList,ArrayList codeValue)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
					
			HashSet tableSet=new HashSet();
			StringBuffer sql_whl=new StringBuffer("");
			StringBuffer sql_from=new StringBuffer("");
		
			int isCommon = 0;
			HashSet paramSet = new HashSet();
			String sortid="";
			for (int i = 0; i < relationList.size(); i++) {
				String relation = (String) relationList.get(i);
				String relationSql = " or  ";
				if ("*".equals(relation)) {
                    relationSql = " and ";
                }

				String param_str = (String) paramenameList.get(i);
				paramSet.add(param_str);
				String[] param = param_str.split("§§");
				
				if ("0".equals(param[1])) // 全局
				{
					tableSet.add("tt_p");
					sql_whl.append(relationSql + param[0]
							+ PubFunc.keyWord_reback((String) operateList.get(i)) + "'"  //50803 特殊字符串全角转半角
							+ (String) codeValue.get(i) + "'");
				} else // 表类
				{
					tableSet.add("tt_s" + param[2]);
					sortid=param[2];
					sql_whl.append(relationSql + param[0]
							+ PubFunc.keyWord_reback((String) operateList.get(i))+ "'"  //50803 特殊字符串全角转半角
							+ (String) codeValue.get(i) + "'");

				}
			}
			for (Iterator t = tableSet.iterator(); t.hasNext();) {
				sql_from.append("," + (String) t.next());
			}
			StringBuffer sql=new StringBuffer("select unitcode from ");	
			if(tableSet.size()>1)
			{
				sql.setLength(0);
				sql.append("select tt_p.unitcode from ");
			}
			sql.append(sql_from.substring(1) + " where ");
			if(tableSet.size()>1)
			{
				sql.append(" tt_p.unitcode=tt_s" +sortid+".unitcode and ");
			}
			sql.append(sql_whl.substring(5));

			// System.out.println("sql="+sql.toString());
			TnameExtendBo tnameExtendBo = new TnameExtendBo(this.conn);
			DbWizard dbWizard = new DbWizard(this.conn);

//			得到基层单位信息集合
			TTorganization ttorganization=new TTorganization(this.conn);
			ArrayList grassRootUnitlist=ttorganization.getGrassRootsUnit(unitcode);
			StringBuffer grassRootUnit=new StringBuffer("");
			HashMap map=new HashMap();
			for(Iterator t=grassRootUnitlist.iterator();t.hasNext();)
			{
				RecordVo vo=(RecordVo)t.next();
				grassRootUnit.append(", '"+vo.getString("unitcode")+"'");
				map.put(vo.getString("unitcode"),vo.getString("unitname"));
			}
			
			
			//判断上报参数表是否存在，如果存在则建立
			for(Iterator t=paramSet.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				String[] param=temp.split("§§");				
				int flag=1;  //全局
				if(!"0".equals(param[1])) {
                    flag=2;
                }
				tnameExtendBo.isExistAppealParamTable(flag,"",param[2],dbWizard);
				
			}
			recset=dao.search("select a.unitcode from ("+sql.toString()+" ) a where a.unitcode in ("+grassRootUnit.substring(1)+" )");
			while(recset.next())
			{
				String a_unitcode=recset.getString("unitcode");
				list.add(a_unitcode+"§"+(String)map.get(a_unitcode));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();		
		}
		return list;
	}
	
	
	
	
	
	/**
	 * 获得满足简单条件的基层单位信息
	 * @param paramename		全局/表类  参数英文名§§关联代码§§代码范围§§表类id
	 * @param paramValueList	全局/表类参数值集合
	 * @return  
	 */
	public ArrayList getSimpleConditionUnit(String paramename,ArrayList paramValueList,String unitcode)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			TTorganization ttorganization=new TTorganization(this.conn);
			TnameExtendBo tnameExtendBo=new TnameExtendBo(this.conn);
			DbWizard dbWizard=new DbWizard(this.conn);
			//得到基层单位信息集合
			ArrayList grassRootUnitlist=ttorganization.getGrassRootsUnit(unitcode);
			StringBuffer grassRootUnit=new StringBuffer("");
			HashMap map=new HashMap();
			for(Iterator t=grassRootUnitlist.iterator();t.hasNext();)
			{
				RecordVo vo=(RecordVo)t.next();
				grassRootUnit.append(", '"+vo.getString("unitcode")+"'");
				map.put(vo.getString("unitcode"),vo.getString("unitname"));
			}
			StringBuffer sql=new StringBuffer("");
			String[] param_arr=paramename.split("§§");
			if("0".equals(param_arr[2]))	//全局
			{
				sql.append("select unitcode from tt_p where "+param_arr[0]+" in (");
				tnameExtendBo.isExistAppealParamTable(1,"",param_arr[3],dbWizard);
			}
			else
			{
				sql.append("select unitcode from tt_s"+param_arr[3]+" where "+param_arr[0]+" in (");
				tnameExtendBo.isExistAppealParamTable(2,"",param_arr[3],dbWizard);
			}
			StringBuffer whl=new StringBuffer("");
			for(Iterator t=paramValueList.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				whl.append(", '"+temp+"'");
			}
			sql.append(whl.substring(1));
			sql.append(" ) and unitcode in("+grassRootUnit.substring(1)+")");
		//	System.out.println("sql="+sql.toString());
			recset=dao.search(sql.toString());
			while(recset.next())
			{
				String a_unitcode=recset.getString("unitcode");
				list.add(a_unitcode+"§"+(String)map.get(a_unitcode));
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();		
		}
	/*  finally
		{
			try
			 {
				 if(recset!=null)
					 recset.close();
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}*/
		return list;
	}
	
	
	
	
	

}
