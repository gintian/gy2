package com.hjsj.hrms.module.gz.analysistables.util;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.dataview.businessobject.DataViewBo;
import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.jxcell.CellException;
import com.jxcell.CellFormat;
import com.jxcell.ChartShape;
import com.jxcell.View;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Connection;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;

public class GzAnalysisUtil { 
	private Connection conn;
	private UserView view;
	
	public GzAnalysisUtil(Connection con,UserView userview){ 
		this.conn = con;
		this.view=userview;
	}
	
	
	/**
     * 获得用户权限范围内的分析表类别
     * @param module 0:薪资  1：保险
     * @return list<HashMap>
     */
	public ArrayList<HashMap>  getReportCategoryList(int module)  throws GeneralException
	{
		ArrayList<HashMap> list=new ArrayList<HashMap>();
		RowSet rowSet=null;
		try
		{
			upgradeReportdetailStruct();
			ContentDAO dao = new ContentDAO(this.conn);
			String sql="select * from reportstyle where rsid>=5 and rsid<12 order by rsid";
			if(module==1)
				sql="select * from reportstyle where rsid=14  or rsid=15 or rsid=16 or rsid=17  order by rsid";
			rowSet = dao.search(sql);
			HashMap map=null; //rsid,rsname
			while(rowSet.next()){
				map=new HashMap();
				//对于没有权限的不显示
				if(!view.isSuper_admin()&&!view.isHaveResource(IResourceConstant.GZ_REPORT_STYLE, rowSet.getString("rsid")))
    				continue;
				map.put("rsid", rowSet.getString("rsid"));
				map.put("rsname", rowSet.getString("rsname"));
				list.add(map);
			}
			
			map=new HashMap();
			map.put("rsid", "12");
			map.put("rsname",ResourceFactory.getProperty("gz.report.selfdefined"));
			list.add(map);
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally {
		    PubFunc.closeDbObj(rowSet);
        }
        return list;
	}
	
	
	
	 /**
     * 获得某分析表类别下的分析表
     *
     * @param module 0:薪资  1：保险
     * @param rsid   分析表类别号
     * @return list<CommonData>
     */
	public ArrayList<HashMap>  getAnaysisReportList(int module,int rsid)  throws GeneralException
	{ 
		ArrayList<HashMap> list=new ArrayList<HashMap>();
		RowSet rowSet=null;
		try
		{
			ArrayList paramList=new ArrayList();
			ContentDAO dao = new ContentDAO(this.conn);
			String sql="";
			if(rsid==12) //自定义表
			{
				if(module==0)
					sql="select tabid,cname from muster_name where nModule=6 and nPrint='-1'";     	
				else 
					sql="select tabid,cname from muster_name where nModule=8 and nPrint='-1'"; 
			}
			else
			{ 
				String unitPriv = view.getUnitIdByBusi("1");//获取权限
				//如果什么权限都没有直接不显示
				if("UN".equalsIgnoreCase(unitPriv)) {
					return list;
				}
				unitPriv = unitPriv.replaceAll("`", ",");//每种权限分割符可能会不一样
			    String[] unitPrivs = unitPriv.split(",");
			    sql="select create_time,rsdtlid,rsdtlname,b0110,create_fullname,1 as opretion from reportdetail where stid=0 and rsid=? and (create_user = ? ";
				paramList.add(new Integer(rsid));
				paramList.add(this.view.getUserName());
				//找到自己和下级
				if (unitPriv.indexOf("UN,") == -1) {
		            for (int i = 0; i < unitPrivs.length; i++) {
		            	sql+=" or " + Sql_switcher.substr("B0110", "3", Sql_switcher.length("B0110"))+ " like ?  ";
		                paramList.add(unitPrivs[i].substring(2) + "%");
		            } 
		        }
				else 
					sql+=" or 1=1 ";
				sql+=" ) ";  
				
				if(!this.view.isSuper_admin()) {
					//找到上级如果用case when也可以实现，就是如果单位过多的时候，sql可能过长
					sql += " union all "; 
					sql += "select create_time,rsdtlid,rsdtlname,b0110,create_fullname,4 as opretion from reportdetail where stid=0 and rsid=? and "
							+ "((create_user is null or create_user <> ?) and (nullif(B0110,'') is null ";
	                paramList.add(new Integer(rsid));
	                paramList.add(this.view.getUserName());
					if (unitPriv.indexOf("UN,") == -1) {
			            for (int i = 0; i < unitPrivs.length; i++) {
			                sql+=" or ? like " + Sql_switcher.substr("B0110", "3", Sql_switcher.length("B0110")) + Sql_switcher.concat() + "'%' ";
			                paramList.add(unitPrivs[i].substring(2));
			            } 
			        }
					else 
						sql+=" or 1=1 ";
					sql+=" )) "; 
				}
			} 
			rowSet=dao.search(sql,paramList);
			HashMap map=null;
			while(rowSet.next()){
				map=new HashMap();
				if(rsid==12) //自定义表
				{
					if(view.isHaveResource(IResourceConstant.HIGHMUSTER,rowSet.getString("tabid")))
					{
						map.put("tabid",PubFunc.encrypt(rowSet.getString("tabid")));
						map.put("tabname",rowSet.getString("cname"));
						map.put("b0110","");
						map.put("name","");
						map.put("opretion","3");
						map.put("create_time","");
						map.put("flagToDiffCustom","1");
					}else {
						continue;
					}
				}
				else
				{
	                map.put("create_time",PubFunc.FormatDate(rowSet.getTimestamp("create_time"), "yyyy-MM-dd HH:mm"));
					map.put("tabid",PubFunc.encrypt(rowSet.getString("rsdtlid")));
					map.put("tabname",rowSet.getString("rsdtlname"));
					map.put("b0110",rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"");
					map.put("name",rowSet.getString("create_fullname")!=null?rowSet.getString("create_fullname"):"");
					map.put("opretion",rowSet.getString("opretion"));
					
				}
				list.add(map);	
			} 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} finally {
		    PubFunc.closeDbObj(rowSet);
        }

		return list;
	}
	
	public ArrayList getCustomData(int module, String rsid) {
		ArrayList child_list = new ArrayList();
		ArrayList<HashMap> list = new ArrayList<HashMap>();
		HashMap map = null;
		try {
			String nmodule="35";
			//保险
			if(module == 1) {
				nmodule="42";
			}
			
			list.addAll(getCustomReport(nmodule));
			DataViewBo dataViewBo = new DataViewBo(conn, this.view, nmodule);
			ArrayList<LazyDynaBean> data_list = dataViewBo.createDataUrl();
			for(int i = 0; i < data_list.size(); i++) {
				LazyDynaBean bean = data_list.get(i);
				map=new HashMap();
		     	map.put("tabid", (String)bean.get("id"));
		     	map.put("tabname", (String)bean.get("name"));
		     	map.put("link_tabid", (String)bean.get("link_tabid"));
		     	map.put("ext", (String)bean.get("ext"));
		     	map.put("report_type", "4");
		     	map.put("url", (String)bean.get("url"));
		     	list.add(map);
			}
			child_list.addAll(getResultData(list, rsid));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return child_list;
	}
		
	private ArrayList getResultData(ArrayList<HashMap> repList, String rsid) {
		ArrayList child_list = new ArrayList();
		try {
			for(HashMap hash:repList){
				LazyDynaBean bean_tem = new LazyDynaBean();
				// 针对特殊报表单独判断
				String ext=(String)hash.get("ext");
				String report_type=(String)hash.get("report_type");
				String urlLink=(String)hash.get("url");
				String tableName = (String)hash.get("tabname");
				String ntype="0";//区分自定义表，还是花名册
				if("3".equals(report_type))
				{
					RecordVo vo = (RecordVo)hash.get("vo");
					int muster_nmodule=vo.getInt("nmodule");
					if(muster_nmodule==3)//人员名册
					{
						ntype="3";
					}
					else if(muster_nmodule==21)//机构名册
					{
						ntype="21";
					}else if(muster_nmodule==41)//职位
					{
						ntype="41";
					}
				}else if("4".equals(report_type)) {
					if(StringUtils.isNotBlank(urlLink))//通过判断url是否存在输出简单报表
						ntype="51";//简单名册
					else
						continue;
				}else if("0".equals(report_type)){
					if(StringUtils.isBlank(ext)){
						continue;
					}
				}else{
					continue;
				}
				String link_tabid=(String)hash.get("link_tabid");
				String id=(String)hash.get("tabid");
				if("0".equals(ntype))
				{
					bean_tem.set("tabid", id);
				}
				else if("51".equals(ntype))
				{
					bean_tem.set("tabid", urlLink);
				}
				else
				{
					bean_tem.set("tabid", link_tabid);
				}
				if("0".equals(ntype)){
					if(".html".equalsIgnoreCase(ext)|| ".htm".equalsIgnoreCase(ext)){
						bean_tem.set("flag", "showCustom");
					}
					else if(".xls".equalsIgnoreCase(ext)|| ".xlsx".equalsIgnoreCase(ext)|| ".xlt".equalsIgnoreCase(ext)|| ".xltx".equalsIgnoreCase(ext))
					{
						bean_tem.set("flag", "showXLSCustom");
					}
		    		
				}else if("3".equals(ntype)){
					bean_tem.set("flag", "showOpenMusterOne");
				}else if("21".equals(ntype)){
					bean_tem.set("flag", "showOpenMusterTwo");
				}else if("41".equals(ntype)){
					bean_tem.set("flag", "showOpenMusterThree");
				}else if("51".equals(ntype)) {
					bean_tem.set("flag", "showSimpleMuster");
				}
				bean_tem.set("rsid", rsid);
				bean_tem.set("rsid_enc", PubFunc.encrypt(rsid));
				bean_tem.set("report_type", report_type);
				bean_tem.set("jump2set", "false");
				bean_tem.set("leaf", true);
                bean_tem.set("iconCls","treeiconCls");
                bean_tem.set("tableName", tableName);
				child_list.add(bean_tem);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return child_list;
	}
	private ArrayList<HashMap> getCustomReport(String nmodule) {
		ArrayList<HashMap> list=new ArrayList<HashMap>();
		RowSet rowSet=null;
		HashMap map=null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			
			String sql = "select * from t_custom_report where flag=1 and Moduleid = ? order by id";
			rowSet=dao.search(sql.toString(), Arrays.asList(new String[] {nmodule}));
			while(rowSet.next()) {
				if(this.view.isHaveResource(IResourceConstant.CUSTOM_REPORT, rowSet.getString("id"))) {
					map=new HashMap();
			     	map.put("tabid", String.valueOf(rowSet.getInt("id")));
			     	map.put("tabname", rowSet.getString("name")==null?"":rowSet.getString("name"));
			     	map.put("ext", rowSet.getString("ext")==null?"":rowSet.getString("ext"));
			    	String link_tabid="";
			    	int rtype=rowSet.getInt("report_type");
			    	if(rtype==3)
			 	    	link_tabid=rowSet.getString("link_tabid");
			    	map.put("link_tabid",link_tabid);
				    /**高级花名册*/
				    if(rtype==3) {
				    	RecordVo  vo = new RecordVo("muster_name");
				    	vo.setInt("tabid",Integer.parseInt(link_tabid));
				    	vo=dao.findByPrimaryKey(vo);
				    	map.put("vo", vo);
				    }
				    map.put("report_type", String.valueOf(rtype));
			    	list.add(map);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
		    PubFunc.closeDbObj(rowSet);
        }
		return list;
	}
	/**
	 * 获得用户权限范围内的薪资|保险账套
	 * @param module  0:薪资  1：保险
	 * @param queryText 查询条件（名称 or id）
	 * @return
	 */
	public ArrayList<HashMap> getSalarySetList(int module,String queryText)  throws GeneralException
	{
		ArrayList<HashMap> list=new ArrayList<HashMap> ();
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet salarytemplateRs = null; 
		try {
			StringBuffer buf = new StringBuffer();
			ArrayList<String> sqlList = new ArrayList<String>();
		 	buf.append("select salaryid,cname,cbase,seq,cond from salarytemplate "); 
		 	if (module==0){// 薪资类别
				buf.append(" where (cstate is null or cstate='')");
			}else {
				buf.append(" where cstate='1'");// 险种类别
			}
		 	//queryText=queryText.replaceAll("|",",");
		 	String[] values=queryText.split(",");
		 	 Pattern pattern = Pattern.compile("[0-9]+"); //整数
			// 快速查询
		 	StringBuffer strbuf = new StringBuffer();
			for(int i = 0; i < values.length; i++){
				String queryVal = values[i];
				if(pattern.matcher(queryVal).matches()){
					if(i == 0){
						strbuf.append(" and ( ");
					}else{
						strbuf.append(" or ");
					}
					strbuf.append("(salaryid=? or cname like ?)");
					sqlList.add(queryVal);
					sqlList.add("%"+queryVal+"%");
				}else{
					if(i == 0){
						strbuf.append(" and ( ");
					}else{
						strbuf.append(" or ");
					}
					strbuf.append("cname like ?");
					sqlList.add("%"+queryVal+"%");
				}
			}
			if(strbuf.length() > 0){
				strbuf.append(")");
			}
			buf.append(strbuf.toString());
			buf.append(" order by seq");
			salarytemplateRs = dao.search(buf.toString(), sqlList);
			HashMap map=null;
		    while(salarytemplateRs.next()){
					// 加上权限过滤
					if (module==0){
						if (!view.isHaveResource(IResourceConstant.GZ_SET, salarytemplateRs.getString("salaryid")))
							continue;
					}else {
						if (!view.isHaveResource(IResourceConstant.INS_SET, salarytemplateRs.getString("salaryid")))
							continue;
					}

					map=new HashMap();
					map.put("salaryid", salarytemplateRs.getString("salaryid"));
					map.put("cname", salarytemplateRs.getString("cname"));
					list.add(map);
		   }


		}catch (Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			PubFunc.closeDbObj(salarytemplateRs);
		}
		return list;

	}

	/**
	 * 获得某薪资账套下的薪资项
	 * @param salaryid 账套id
	 * @param itemTypes 指标类别，以逗号分隔可以是多个    举例 ： A,N,D,M,AC     （AC-->代码型指标）
	 * @return list<HashMap>  itemid,itemdesc,itemtype,itemcode
	 */
	public ArrayList getSalaryItemList(int salaryid,String itemTypes,String rsid)  throws GeneralException
	{
		ArrayList list=new ArrayList();
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
            StringBuffer buf = new StringBuffer("select * from salaryset");
            ArrayList valueList=new ArrayList();
            valueList.add(salaryid);
            buf.append(" where salaryid=? and lower(itemid) in ('a00z0','a00z1','a00z2','a00z3','nbase')");


            StringBuffer buf1 = new StringBuffer("select s.* ");
            buf1.append("from salaryset s,fielditem f where salaryid=?");
            buf1.append(" and s.itemid = f.itemid");
            //除7号、16号类别外其余分析表只能选择数值型指标。
            if("7".equalsIgnoreCase(rsid) || "16".equalsIgnoreCase(rsid)){
                buf1.append(" and lower(s.itemid) not in ('b0110','e0122','a0101') ");
            }else{
                buf1.append(" and f.itemtype ='N' ");
            }
			if(itemTypes!=null&&itemTypes.trim().length()>0)
			{
				int n=0;
				String[] temps=itemTypes.split(",");
				String temp_str="";
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						if("AC".equals(temps[i]))
						{
							temp_str+=" or  ( t.itemtype=? and t.codesetid<>? ) ";
							valueList.add("A");
							valueList.add("0");
						}
						else if("A".equals(temps[i]))
						{
							temp_str+=" or  ( t.itemtype=? and t.codesetid=? ) ";
							valueList.add("A");
							valueList.add("0");
						}
						else
						{
							temp_str+=" or  t.itemtype=?";
							valueList.add(temps[i]);
						}
						n++;
					}
				}
				if(n>0)
				{
					buf.append(" and ( "+temp_str.substring(3)+" )");
                    buf1.append(" and ( "+temp_str.substring(3)+" )");
				}
			}

			buf.append(" order by sortid");
			buf1.append(" order by sortid");
            HashMap map=null;
            if("7".equalsIgnoreCase(rsid) || "16".equalsIgnoreCase(rsid)){
                rowSet=dao.search(buf.toString(),valueList);
                while(rowSet.next())
                {
                    map=new HashMap();
                    String itemid = rowSet.getString("itemid").toLowerCase();
                    map.put("itemid",itemid);
                    map.put("fieldid",rowSet.getString("fieldid"));
                    map.put("itemdesc",rowSet.getString("itemdesc")!=null?rowSet.getString("itemdesc"):"");
                    map.put("itemtype",rowSet.getString("itemtype"));
                    String codesetid=rowSet.getString("codesetid")!=null?rowSet.getString("codesetid"):"";
                    if(codesetid.length()>0&&!"0".equals(codesetid)&& "A".equals(rowSet.getString("itemtype")))
                        map.put("itemtype","AC");
                    map.put("itemcode",codesetid);
                    list.add(map);

                }
            }
			rowSet=dao.search(buf1.toString(),valueList);
            String addedStr = ",a00z0,a00z1,a00z2,a00z3,nbase,";
			while(rowSet.next())
			{
                    String itemid = rowSet.getString("itemid").toLowerCase();
                    if(addedStr.contains(","+itemid+",")){
                        continue;
                    }
					map=new HashMap();
					map.put("itemid",itemid);
					map.put("fieldid",rowSet.getString("fieldid"));
					map.put("itemdesc",rowSet.getString("itemdesc")!=null?rowSet.getString("itemdesc"):"");
					map.put("itemtype",rowSet.getString("itemtype"));
					String codesetid=rowSet.getString("codesetid")!=null?rowSet.getString("codesetid"):"";
					if(codesetid.length()>0&&!"0".equals(codesetid)&& "A".equals(rowSet.getString("itemtype")))
						map.put("itemtype","AC"); 
					map.put("itemcode",codesetid); 
					list.add(map);
				 
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}
		return list; 
		
	}
	/**
	 * 获得多个薪资账套下的薪资项s
	 * @param salaryids 账套id 12,13,14
	 * @param itemTypes 指标类别，以逗号分隔可以是多个    举例 ： A,N,D,M,AC     （AC-->代码型指标）
	 * @return list<HashMap>  itemid,itemdesc,itemtype,itemcode
	 */
	public ArrayList getSalaryItemList(String salaryids,String itemTypes)  throws GeneralException
	{
		ArrayList list=new ArrayList();
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer("select * from salaryset where salaryid in ("+salaryids+")");
			ArrayList valueList=new ArrayList();
			if(itemTypes!=null&&itemTypes.trim().length()>0)
			{
				int n=0;
				String[] temps=itemTypes.split(",");
				String temp_str="";
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						if("AC".equals(temps[i]))
						{
							temp_str+=" or  ( itemtype=? and codesetid<>? ) ";
							valueList.add("A");
							valueList.add("0");
						}
						else if("A".equals(temps[i]))
						{
							temp_str+=" or  ( itemtype=? and codesetid=? ) ";
							valueList.add("A");
							valueList.add("0");
						}
						else
						{
							temp_str+=" or  itemtype=?";
							valueList.add(temps[i]);
						}
						n++;
					}
				}
				if(n>0)
				{
					buf.append(" and ( "+temp_str.substring(3)+" )");

				}
			}

			buf.append(" order by sortid");

			rowSet=dao.search(buf.toString(),valueList);
			HashMap map=null;
			HashMap itenidMap = new HashMap();
			while(rowSet.next())
			{
				String itemid = rowSet.getString("itemid");
			    if(!itenidMap.containsKey(itemid.toUpperCase())) {
			    	itenidMap.put(itemid.toUpperCase(), "1");
			    	map=new HashMap();
					map.put("itemid",rowSet.getString("itemid"));
					map.put("fieldid",rowSet.getString("fieldid"));
					map.put("itemdesc",rowSet.getString("itemdesc")!=null?rowSet.getString("itemdesc"):"");
					map.put("itemtype",rowSet.getString("itemtype"));
					String codesetid=rowSet.getString("codesetid")!=null?rowSet.getString("codesetid"):"";
					if(codesetid.length()>0&&!"0".equals(codesetid)&& "A".equals(rowSet.getString("itemtype")))
						map.put("itemtype","AC");
					map.put("itemcode",codesetid);
					list.add(map);
			    }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}
		return list;

	}
	/**
	 * 获得统计范围内薪资|保险数据涉及的年份，倒序排列
	 * @param salaryids 账套ID   12,234,333
	 * @param nbases   人员库   Usr,Ret
	 * @param scope   1：含过程中数据 
	 * @return
	 */
    public ArrayList getYearList(String salaryids,String nbases,int scope)  throws GeneralException
    {
        ArrayList list=new ArrayList();
        RowSet rowSet=null;
        try
        {
        	ContentDAO dao=new ContentDAO(this.conn);
            HashMap map = getWhereBuf(salaryids, nbases, scope);
        	String buf = (String) map.get("buf");
        	ArrayList paramList = (ArrayList) map.get("paramList");
            rowSet=dao.search("select distinct "+Sql_switcher.year("a00z0")+","+Sql_switcher.month("a00z0")+
            						" from ("+buf.toString()+") t order by "+Sql_switcher.year("a00z0")+" desc",paramList);
            while(rowSet.next())
            {
                list.add(rowSet.getString(1));
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            PubFunc.closeDbObj(rowSet);
        }
        return list;
    }
	
    /**
	 * 获得统计范围内薪资|保险数据涉及的年份，倒序排列
	 * @param salaryids 账套ID   12,234,333
	 * @param nbases   人员库   Usr,Ret
	 * @param scope   1：含过程中数据 
	 * @return
	 */
    public ArrayList getA00Z0List(String salaryids,String nbases,int scope)  throws GeneralException
    {
    	ArrayList list = new ArrayList();
        RowSet rowSet=null;
        try
        {
        	ContentDAO dao=new ContentDAO(this.conn);
        	HashMap map = getWhereBuf(salaryids, nbases, scope);
        	String buf = (String) map.get("buf");
        	ArrayList paramList = (ArrayList) map.get("paramList");
        	
            rowSet=dao.search("select distinct "+Sql_switcher.year("a00z0")+" year,"+Sql_switcher.month("a00z0")+
					" month from ("+buf.toString()+") t order by "+Sql_switcher.year("a00z0")+" desc",paramList);
            while(rowSet.next()) {
            	list.add(rowSet.getString("year") + "-" + rowSet.getString("month"));
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            PubFunc.closeDbObj(rowSet);
        }
        return list;
    }
    
    private HashMap getWhereBuf(String salaryids, String nbases, int scope) {
    	HashMap map = new HashMap();
    	try {
	    	StringBuffer buf = new StringBuffer("select distinct a00z0 from salaryhistory where salaryid in ( ");
	        String[] salaryArray=salaryids.split(",");
	        String[] nbaseArray=nbases.split(",");
	
	        ArrayList paramList=new ArrayList();
	        int n=0;
	        for(int i=0;i<salaryArray.length;i++)
	        {
	            if (!StringUtils.isBlank(salaryArray[i])) {
	                if(n!=0)
	                    buf.append(",");
	                buf.append("?");
	                paramList.add(new Integer(salaryArray[i].trim()));
	                n++;
	            }
	        }
	        buf.append(" ) and lower(nbase) in ( ");
	        n=0;
	        for(int i=0;i<nbaseArray.length;i++)
	        {
	            if (!StringUtils.isBlank(nbaseArray[i])) {
	                if(n!=0)
	                    buf.append(",");
	                buf.append("?");
	                paramList.add(nbaseArray[i].trim().toLowerCase());
	                n++;
	            }
	        }
	        buf.append(" ) ");
	        if(scope!=1) //不含过程中数据
	        {
	            buf.append(" and sp_flag='06' ");
	        }
	        String temp=buf.toString().replaceAll("salaryhistory","salaryarchive");
	        buf.append(" union "+temp);
	        paramList.addAll(paramList);
	        map.put("buf", buf.toString());
	        map.put("paramList", paramList);
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	return map;
    }
    /**
     * 权限限制sql语句
     *
     * @param salaryids 所选薪资账号号  13,23,34
     * @param b_units   机构ID
     * @param nbases    选中的人员库
     * @param tablename 表名
     * @param scope     .....
     * @return
     */
    public String getPrivSQL(String tablename, String nbases, String salaryids, String b_units, String scope) throws GeneralException {
        StringBuffer priv = new StringBuffer("");

        String[] salaryArray = salaryids.split(",");
        String[] nbaseArray = nbases.split(",");
        int n = 0;
        HashMap keyMap = new HashMap();
        for (int i = 0; i < salaryArray.length; i++) {
        	String salaryid = salaryArray[i];
            if (!StringUtils.isBlank(salaryArray[i])) {
            	//如果薪资账套的资源权限没有，就别看到这里的数据了
				if (!view.isHaveResource(IResourceConstant.GZ_SET, salaryid) && !view.isHaveResource(IResourceConstant.INS_SET, salaryid))
					continue;

                SalaryCtrlParamBo salaryCtrlParamBo = new SalaryCtrlParamBo(this.conn, new Integer(salaryArray[i].trim()));
                String orgid = salaryCtrlParamBo.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid"); //归属单位
                String deptid = salaryCtrlParamBo.getValue(SalaryCtrlParamBo.SUM_FIELD, "deptid"); //归属部门
                orgid = orgid != null ? orgid : "";
                deptid = deptid != null ? deptid : "";

                String key = "";
                if (StringUtils.isBlank(orgid) && StringUtils.isBlank(deptid)) {
                    key = "b0110,e0122";
                } else if (!StringUtils.isBlank(orgid) && !StringUtils.isBlank(deptid)) {
                    key = deptid;
                } else if (!StringUtils.isBlank(orgid) && StringUtils.isBlank(deptid)) {
                    key = orgid;
                } else if (StringUtils.isBlank(orgid) && !StringUtils.isBlank(deptid)) {
                    key = deptid;
                }
                if (keyMap.get(key) != null) {
                    String temp = (String) keyMap.get(key);
                    keyMap.put(key, temp + "," + salaryArray[i].trim());
                } else
                    keyMap.put(key, salaryArray[i].trim());
                n++;
            }
        }
        priv.append(" and lower(nbase) in ( ");
        n = 0;
        for (int i = 0; i < nbaseArray.length; i++) {
            if (!StringUtils.isBlank(nbaseArray[i])) {
                if (n != 0)
                    priv.append(",");
                priv.append("'" + nbaseArray[i].trim().toLowerCase() + "'");
                n++;
            }
        }
        priv.append(" )  and ( 1=2 ");


        String unitPriv = this.view.getUnitIdByBusi("1");
        unitPriv = unitPriv.replaceAll("`", ",");//每种权限分割符可能会不一样
        String[] unitPrivs = unitPriv.split(",");


        StringBuffer temp_str = new StringBuffer("");
        for (Iterator t = keyMap.keySet().iterator(); t.hasNext(); ) {
            String key = (String) t.next();
            String t_salaryids = (String) keyMap.get(key);

            if (!this.view.isSuper_admin() && unitPriv.indexOf("UN,") == -1) {

                temp_str.append(" or ( salaryid in ( " + t_salaryids + " )  and   (  ");

                StringBuffer temp_str2 = new StringBuffer("");
                for (int i = 0; i < unitPrivs.length; i++) {
                    String privCodeValue = unitPrivs[i].substring(2);

                    if (!key.contains("b0110") && !key.contains("e0122")) {
                        temp_str2.append(" or (  case when  nullif(" + tablename + "." + key + ",'') is not null  then " + tablename + "." + key + " ");
                        temp_str2.append("  when  nullif(" + tablename + "." + key + ",'') is  null    and nullif(" + tablename + ".e0122,'') is not null then " + tablename + ".e0122 ");
                        temp_str2.append("  else " + tablename + ".b0110 end ");
                        temp_str2.append(" like '" + privCodeValue + "%' ");
                        temp_str2.append(") ");
                    } else if (key.indexOf(",") == -1) //b0110 or e0122
                    {

                        temp_str2.append(" or  " + tablename + "." + key + " end ");
                        temp_str2.append(" like '" + privCodeValue + "%' ");
                    } else {
                        temp_str2.append(" or (  case when  nullif(" + tablename + ".e0122,'') is not null  then " + tablename + ".e0122 ");
                        temp_str2.append("  else " + tablename + ".b0110 end ");
                        temp_str2.append(" like '" + privCodeValue + "%' ");
                        temp_str2.append(") ");
                    }

                }
                temp_str.append(temp_str2.substring(3) + " ) ) ");
            } else {
                temp_str.append(" or  salaryid in ( " + t_salaryids + " )  ");
            }

        }
        if(temp_str.length() > 0)
        	priv.append(temp_str);
        
        priv.append(" )");
        return priv.toString();
    }


    private void upgradeReportdetailStruct() throws GeneralException {
        StringBuffer buf = new StringBuffer();
        RowSet rowSet = null;
        try {

            DbWizard dbw = new DbWizard(this.conn);

            if (!dbw.isExistField("reportdetail", "b0110", false)) {
                Table table = new Table("reportdetail");

                Field field = new Field("b0110", "b0110");
                field.setDatatype(DataType.STRING);
                field.setLength(50);
                table.addField(field);

                field = new Field("create_user", "create_user");
                field.setDatatype(DataType.STRING);
                field.setLength(50);
                table.addField(field);


                field = new Field("create_fullname", "create_fullname");
                field.setDatatype(DataType.STRING);
                field.setLength(50);
                table.addField(field);

                field = new Field("create_time", "create_time");
                field.setDatatype(DataType.DATE);
                table.addField(field);

                dbw.addColumns(table);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap getCtrlParam2Map(String rsid, String rsdtlid) {
        RowSet rs = null;
        Document doc = null;
        HashMap paramMap = new HashMap();
        try {
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search("select CtrlParam from reportdetail where RSID='" + rsid + "' and RSDTLID='" + rsdtlid + "'");
            String ctrlParam = "";
            String nbase = "";
            String salaryids = "";
            String verifying = "";
            if (rs.next())
                ctrlParam = Sql_switcher.readMemo(rs, "CtrlParam");
            if (StringUtils.isNotBlank(ctrlParam)) {
            	doc = PubFunc.generateDom(ctrlParam);
                String str_path = "/param/report";
                XPath xpath = XPath.newInstance(str_path);
                List childlist = xpath.selectNodes(doc);
                Element element = null;
                if (childlist != null && childlist.size() > 0) {
                    element = (Element) childlist.get(0);
                    //nbase 人员库   salaryids 薪资账套号   verifying:含审批数据
                    nbase = element.getAttributeValue("nbase");
                    salaryids = element.getAttributeValue("salaryids");
                    verifying = element.getAttributeValue("verifying");
                    if(StringUtils.isEmpty(nbase)){
                        nbase = "";
                    }
                    if(StringUtils.isEmpty(salaryids)){
                        salaryids = "";
                    }
                    if(StringUtils.isEmpty(verifying)){
                        verifying = "0";
                    }
                }
            }
            paramMap.put("rsid", rsid);
            paramMap.put("rsdtlid", rsdtlid);
            paramMap.put("nbase", nbase);
            paramMap.put("salaryids", salaryids);
            paramMap.put("verifying", verifying);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return paramMap;
    }

    /**
     * SQL语句人员库限制部分（高级授权）
     *
     * @param nbase
     * @param salaryid
     * @param tableName
     * @param verifying 
     * @return
     */
    public String getDbSQL(String nbase,String salaryid, String tableName, String verifying) {
        StringBuffer dbSql = new StringBuffer();
        try
        {
            if(nbase!=null&&nbase.length()>0){
                if (nbase.indexOf(",")==-1) {
                    dbSql.append(" and (upper("+tableName+".nbase)='");
                    dbSql.append(nbase.toUpperCase()+"')");
                }else{
                    String[] temp = nbase.split(",");
                    for (int i = 0; i < temp.length; i++) {
                        if (i == 0) {
                            dbSql.append(" and (");
                        }
                        dbSql.append("upper(nbase)='");
                        dbSql.append(temp[i].toUpperCase()+"'");
                        if (i != temp.length - 1) {
                            dbSql.append(" OR ");
                        } else
                            dbSql.append(")");
                    }
                }
            }
            if(verifying!=null&& "0".equals(verifying)){//不包含过程数据
                dbSql.append(" and "+tableName+".sp_flag = '06'");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbSql.toString();
    }

    public String getSalarysetSQL(String salaryid) {
        StringBuffer salarySQL = new StringBuffer();
        try {
            if (salaryid.indexOf(",") == -1) {
                salarySQL.append("( salaryid=");
                salarySQL.append(salaryid);
                salarySQL.append(")");
            } else {
                String[] temp = salaryid.split(",");
                for (int i = 0; i < temp.length; i++) {
                    if (i == 0) {
                        salarySQL.append("(");
                    }
                    salarySQL.append("salaryid=");
                    salarySQL.append(temp[i]);
                    if (i != temp.length - 1) {
                        salarySQL.append(" OR ");
                    } else
                        salarySQL.append(")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return salarySQL.toString();
    }

    /**
     * 判单薪资表账套中是否包含唯一性指标，如果包含返回唯一性指标，否者返回null
     *
     * @param rsdtlid 报表编号
     * @return
     */
    public Map<String, String> getOnlyFldFromRsdt(String rsdtlid) {
        Map<String, String> map = null;
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
            String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
            String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");
            if (!"0".equals(uniquenessvalid) && onlyname != null && !"".equals(onlyname)) {
                String sql = " select Itemid,Itemdesc from reportitem where Rsdtlid=? and lower(itemid)=?";
                List values = new ArrayList();
                values.add(rsdtlid);
                values.add(onlyname.toLowerCase());
                rs = dao.search(sql, values);
                if (rs.next()) {
                    map = new HashMap<String, String>();
                    map.put("itemid", rs.getString("itemid"));
                    map.put("itemdesc", rs.getString("itemdesc"));
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return map;
    }

    /**
     * 根据新参数对象，生成新参数串
     *
     * @param rpv
     * @return
     */
    public boolean saveXML(ReportParseVo rpv, String rsid, String rsdtlid) {
        String newXML = "";
        RowSet rs = null;
        boolean flag = false;
        ArrayList<String> list = new ArrayList<String>();
        try {
            String xml = "";
            ContentDAO dao = new ContentDAO(this.conn);
            String sql = "select ctrlparam from reportdetail where rsid=?";
            list.add(rsid);
            if (!"4".equals(rsid)
                    && !"8".equals(rsid)) {
                sql += " and rsdtlid=?";
                list.add(rsdtlid);
            }
            rs = dao.search(sql, list);
            while (rs.next()) {
                xml = Sql_switcher.readMemo(rs, "ctrlparam");
            }
            if (xml == null || "".equals(xml)) {
                xml = "<?xml version=\"1.0\" encoding=\"GB2312\"?>  <param> </param>  ";
            }
            Document doc = PubFunc.generateDom(xml);
            Element report = doc.getRootElement().getChild("report");
            if (report == null) {
                report = new Element("report");
                Element r_user = new Element("r_user");
                r_user.setAttribute("name", this.view.getUserName().toLowerCase());
                r_user.setAttribute("super", this.view.isSuper_admin() ? "1" : "0");

                Element page = new Element("page");
                page.setAttribute("type", rpv.getPagetype() == null ? "" : rpv.getPagetype());
                page.setAttribute("width", rpv.getWidth() == null ? "" : rpv.getWidth());
                page.setAttribute("height", rpv.getHeight() == null ? "" : rpv.getHeight());
                page.setAttribute("range", rpv.getOrientation() == null ? "" : rpv.getOrientation());
                page.setAttribute("margin_top", rpv.getTop() == null ? "" : rpv.getTop());
                page.setAttribute("margin_left", rpv.getLeft() == null ? "" : rpv.getLeft());
                page.setAttribute("margin_bottom", rpv.getBottom() == null ? "" : rpv.getBottom());
                page.setAttribute("margin_right", rpv.getRight() == null ? "" : rpv.getRight());

                Element title = new Element("title");
                title.setAttribute("content", rpv.getTitle_fw() == null ? "" : rpv.getTitle_fw());
                title.setAttribute("fontface", rpv.getTitle_fn() == null ? "" : rpv.getTitle_fn());
                title.setAttribute("fontsize", rpv.getTitle_fz() == null ? "" : rpv.getTitle_fz());
                title.setAttribute("fontblob", rpv.getTitle_fb() == null ? "" : rpv.getTitle_fb());
                title.setAttribute("underline", rpv.getTitle_fu() == null ? "" : rpv.getTitle_fu());
                title.setAttribute("fontitalic", rpv.getTitle_fi() == null ? "" : rpv.getTitle_fi());
                title.setAttribute("dline", rpv.getTitle_fs() == null ? "" : rpv.getTitle_fs());
                title.setAttribute("color", rpv.getTitle_fc() == null ? "" : rpv.getTitle_fc());

                Element page_head = new Element("page_head");
                page_head.setAttribute("left", rpv.getHead_flw() == null ? "" : rpv.getHead_flw());
                page_head.setAttribute("center", rpv.getHead_fmw() == null ? "" : rpv.getHead_fmw());
                page_head.setAttribute("right", rpv.getHead_frw() == null ? "" : rpv.getHead_frw());
                page_head.setAttribute("fontface", rpv.getHead_fn() == null ? "" : rpv.getHead_fn());
                page_head.setAttribute("fontsize", rpv.getHead_fz() == null ? "" : rpv.getHead_fz());
                page_head.setAttribute("fontblob", rpv.getHead_fb() == null ? "" : rpv.getHead_fb());
                page_head.setAttribute("underline", rpv.getHead_fu() == null ? "" : rpv.getHead_fu());
                page_head.setAttribute("fontitalic", rpv.getHead_fi() == null ? "" : rpv.getHead_fi());
                page_head.setAttribute("dline", rpv.getHead_fu() == null ? "" : rpv.getHead_fu());
                page_head.setAttribute("color", rpv.getHead_fc() == null ? "" : rpv.getHead_fc());
                page_head.setAttribute("lHeadHomeShow", rpv.getHead_flw_hs() == null ? "" : rpv.getHead_flw_hs());
                page_head.setAttribute("mHeadHomeShow", rpv.getHead_fmw_hs() == null ? "" : rpv.getHead_fmw_hs());
                page_head.setAttribute("rHeadHomeShow", rpv.getHead_frw_hs() == null ? "" : rpv.getHead_frw_hs());

                Element page_tail = new Element("page_tail");
                page_tail.setAttribute("left", rpv.getTile_flw() == null ? "" : rpv.getTile_flw());
                page_tail.setAttribute("center", rpv.getTile_fmw() == null ? "" : rpv.getTile_fmw());
                page_tail.setAttribute("right", rpv.getTile_frw() == null ? "" : rpv.getTile_frw());
                page_tail.setAttribute("fontface", rpv.getTile_fn() == null ? "" : rpv.getTile_fn());
                page_tail.setAttribute("fontsize", rpv.getTile_fz() == null ? "" : rpv.getTile_fz());
                page_tail.setAttribute("fontblob", rpv.getTile_fb() == null ? "" : rpv.getTile_fb());
                page_tail.setAttribute("underline", rpv.getTile_fu() == null ? "" : rpv.getTile_fu());
                page_tail.setAttribute("fontitalic", rpv.getTile_fi() == null ? "" : rpv.getTile_fi());
                page_tail.setAttribute("dline", rpv.getTile_fs() == null ? "" : rpv.getTile_fs());
                page_tail.setAttribute("color", rpv.getTile_fc() == null ? "" : rpv.getTile_fc());
                page_head.setAttribute("lFootHomeShow", rpv.getTile_flw_hs() == null ? "" : rpv.getTile_flw_hs());
                page_head.setAttribute("mFootHomeShow", rpv.getTile_fmw_hs() == null ? "" : rpv.getTile_fmw_hs());
                page_head.setAttribute("rFootHomeShow", rpv.getTile_frw_hs() == null ? "" : rpv.getTile_frw_hs());

                Element page_main = new Element("page_main");
                page_main.setAttribute("fontface", rpv.getBody_fn() == null ? "" : rpv.getBody_fn());
                page_main.setAttribute("fontsize", rpv.getBody_fz() == null ? "" : rpv.getBody_fz());
                page_main.setAttribute("fontblob", rpv.getBody_fb() == null ? "" : rpv.getBody_fb());
                page_main.setAttribute("underline", rpv.getBody_fu() == null ? "" : rpv.getBody_fu());
                page_main.setAttribute("fontitalic", rpv.getBody_fi() == null ? "" : rpv.getBody_fi());
                page_main.setAttribute("color", rpv.getBody_fc() == null ? "" : rpv.getBody_fc());

                Element page_thead = new Element("page_thead");
                page_thead.setAttribute("thead_fn", rpv.getThead_fn() == null ? "" : rpv.getThead_fn());
                page_thead.setAttribute("thead_fz", rpv.getThead_fz() == null ? "" : rpv.getThead_fz());
                page_thead.setAttribute("thead_fb", rpv.getThead_fb() == null ? "" : rpv.getThead_fb());
                page_thead.setAttribute("thead_fu", rpv.getThead_fu() == null ? "" : rpv.getThead_fu());
                page_thead.setAttribute("thead_fi", rpv.getThead_fi() == null ? "" : rpv.getThead_fi());
                page_thead.setAttribute("thead_fc", rpv.getThead_fc() == null ? "" : rpv.getThead_fc());
                r_user.addContent(page_thead);
                r_user.addContent(page);
                r_user.addContent(title);
                r_user.addContent(page_head);
                r_user.addContent(page_tail);
                r_user.addContent(page_main);
                report.addContent(r_user);
                doc.getRootElement().addContent(report);
            } else {
                String path = "/param/report/r_user[@name='" + this.view.getUserName().toLowerCase() + "']";
                XPath xpath = XPath.newInstance(path);
                Element element = (Element) xpath.selectSingleNode(doc);
                boolean isR_user = false;
                boolean isPage = false;
                boolean isTitle = false;
                boolean isPage_head = false;
                boolean isPage_tail = false;
                boolean isPage_main = false;
                boolean isPage_thead = false;
                if (element == null) {
                    element = new Element("r_user");
                    isR_user = true;
                }
                element.setAttribute("name", this.view.getUserName().toLowerCase());
                element.setAttribute("super", this.view.isSuper_admin() ? "1" : "0");

                Element page = element.getChild("page");
                if (page == null) {
                    page = new Element("page");
                    isPage = true;
                }
                page.setAttribute("type", rpv.getPagetype() == null ? "" : rpv.getPagetype());
                page.setAttribute("width", rpv.getWidth() == null ? "" : rpv.getWidth());
                page.setAttribute("height", rpv.getHeight() == null ? "" : rpv.getHeight());
                page.setAttribute("range", rpv.getOrientation() == null ? "" : rpv.getOrientation());
                page.setAttribute("margin_top", rpv.getTop() == null ? "" : rpv.getTop());
                page.setAttribute("margin_left", rpv.getLeft() == null ? "" : rpv.getLeft());
                page.setAttribute("margin_bottom", rpv.getBottom() == null ? "" : rpv.getBottom());
                page.setAttribute("margin_right", rpv.getRight() == null ? "" : rpv.getRight());
                Element title = element.getChild("title");
                if (title == null) {
                    title = new Element("title");
                    isTitle = true;
                }
                title.setAttribute("content", rpv.getTitle_fw() == null ? "" : rpv.getTitle_fw());
                title.setAttribute("fontface", rpv.getTitle_fn() == null ? "" : rpv.getTitle_fn());
                title.setAttribute("fontsize", rpv.getTitle_fz() == null ? "" : rpv.getTitle_fz());
                title.setAttribute("fontblob", rpv.getTitle_fb() == null ? "" : rpv.getTitle_fb());
                title.setAttribute("underline", rpv.getTitle_fu() == null ? "" : rpv.getTitle_fu());
                title.setAttribute("fontitalic", rpv.getTitle_fi() == null ? "" : rpv.getTitle_fi());
                title.setAttribute("dline", rpv.getTitle_fs() == null ? "" : rpv.getTitle_fs());
                title.setAttribute("color", rpv.getTitle_fc() == null ? "" : rpv.getTitle_fc());

                Element page_head = element.getChild("page_head");
                if (page_head == null) {
                    page_head = new Element("page_head");
                    isPage_head = true;
                }
                page_head.setAttribute("left", rpv.getHead_flw() == null ? "" : rpv.getHead_flw());
                page_head.setAttribute("center", rpv.getHead_fmw() == null ? "" : rpv.getHead_fmw());
                page_head.setAttribute("right", rpv.getHead_frw() == null ? "" : rpv.getHead_frw());
                page_head.setAttribute("fontface", rpv.getHead_fn() == null ? "" : rpv.getHead_fn());
                page_head.setAttribute("fontsize", rpv.getHead_fz() == null ? "" : rpv.getHead_fz());
                page_head.setAttribute("fontblob", rpv.getHead_fb() == null ? "" : rpv.getHead_fb());
                page_head.setAttribute("underline", rpv.getHead_fu() == null ? "" : rpv.getHead_fu());
                page_head.setAttribute("fontitalic", rpv.getHead_fi() == null ? "" : rpv.getHead_fi());
                page_head.setAttribute("dline", rpv.getHead_fu() == null ? "" : rpv.getHead_fu());
                page_head.setAttribute("color", rpv.getHead_fc() == null ? "" : rpv.getHead_fc());
                page_head.setAttribute("lHeadHomeShow", rpv.getHead_flw_hs() == null ? "" : rpv.getHead_flw_hs());
                page_head.setAttribute("mHeadHomeShow", rpv.getHead_fmw_hs() == null ? "" : rpv.getHead_fmw_hs());
                page_head.setAttribute("rHeadHomeShow", rpv.getHead_frw_hs() == null ? "" : rpv.getHead_frw_hs());

                Element page_tail = element.getChild("page_tail");
                if (page_tail == null) {
                    page_tail = new Element("page_tail");
                    isPage_tail = true;
                }
                page_tail.setAttribute("left", rpv.getTile_flw() == null ? "" : rpv.getTile_flw());
                page_tail.setAttribute("center", rpv.getTile_fmw() == null ? "" : rpv.getTile_fmw());
                page_tail.setAttribute("right", rpv.getTile_frw() == null ? "" : rpv.getTile_frw());
                page_tail.setAttribute("fontface", rpv.getTile_fn() == null ? "" : rpv.getTile_fn());
                page_tail.setAttribute("fontsize", rpv.getTile_fz() == null ? "" : rpv.getTile_fz());
                page_tail.setAttribute("fontblob", rpv.getTile_fb() == null ? "" : rpv.getTile_fb());
                page_tail.setAttribute("underline", rpv.getTile_fu() == null ? "" : rpv.getTile_fu());
                page_tail.setAttribute("fontitalic", rpv.getTile_fi() == null ? "" : rpv.getTile_fi());
                page_tail.setAttribute("dline", rpv.getTile_fs() == null ? "" : rpv.getTile_fs());
                page_tail.setAttribute("color", rpv.getTile_fc() == null ? "" : rpv.getTile_fc());
                page_tail.setAttribute("lFootHomeShow", rpv.getTile_flw_hs() == null ? "" : rpv.getTile_flw_hs());
                page_tail.setAttribute("mFootHomeShow", rpv.getTile_fmw_hs() == null ? "" : rpv.getTile_fmw_hs());
                page_tail.setAttribute("rFootHomeShow", rpv.getTile_frw_hs() == null ? "" : rpv.getTile_frw_hs());
                Element page_main = element.getChild("page_main");
                if (page_main == null) {
                    page_main = new Element("page_main");
                    isPage_main = true;
                }
                page_main.setAttribute("fontface", rpv.getBody_fn() == null ? "" : rpv.getBody_fn());
                page_main.setAttribute("fontsize", rpv.getBody_fz() == null ? "" : rpv.getBody_fz());
                page_main.setAttribute("fontblob", rpv.getBody_fb() == null ? "" : rpv.getBody_fb());
                page_main.setAttribute("underline", rpv.getBody_fu() == null ? "" : rpv.getBody_fu());
                page_main.setAttribute("fontitalic", rpv.getBody_fi() == null ? "" : rpv.getBody_fi());
                page_main.setAttribute("color", rpv.getBody_fc() == null ? "" : rpv.getBody_fc());
                Element page_thead = element.getChild("page_thead");
                if (page_thead == null) {
                    page_thead = new Element("page_thead");
                    isPage_thead = true;
                }
                page_thead.setAttribute("thead_fn", rpv.getThead_fn() == null ? "" : rpv.getThead_fn());
                page_thead.setAttribute("thead_fz", rpv.getThead_fz() == null ? "" : rpv.getThead_fz());
                page_thead.setAttribute("thead_fb", rpv.getThead_fb() == null ? "" : rpv.getThead_fb());
                page_thead.setAttribute("thead_fu", rpv.getThead_fu() == null ? "" : rpv.getThead_fu());
                page_thead.setAttribute("thead_fi", rpv.getThead_fi() == null ? "" : rpv.getThead_fi());
                page_thead.setAttribute("thead_fc", rpv.getThead_fc() == null ? "" : rpv.getThead_fc());
                if (isPage_main)
                    element.addContent(page_main);
                if (isPage_tail)
                    element.addContent(page_tail);
                if (isPage_head)
                    element.addContent(page_head);
                if (isTitle)
                    element.addContent(title);
                if (isPage)
                    element.addContent(page);
                if (isPage_thead)
                    element.addContent(page_thead);
                if (isR_user)
                    report.addContent(element);
            }
            XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
            newXML = outputter.outputString(doc);
            list.clear();
            sql = "update reportdetail set ctrlparam=? where rsid=?";
            list.add(newXML);
            list.add(rsid);
            if (!"4".equals(rsid)
                    && !"8".equals(rsid)) {
                sql += " and rsdtlid=?";
                list.add(rsdtlid);
            }
            dao.update(sql, list);

            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }

        return flag;
    }

    /**
     * 得到参数，如果有自己的，得到自己的，如果自己没有，在输出excel时找系统管理员的，在设置页面的时候不找管理员的，
     */
    public ReportParseVo analysePageSettingXml(String rsid, String rsdtlid) {
        ReportParseVo rpv = new ReportParseVo();
        RowSet rs = null;
        try {
            String xml = "";
            ContentDAO dao = new ContentDAO(this.conn);
            String sql = "select ctrlparam from reportdetail where rsid=?";
            ArrayList<String> params = new ArrayList<String>();
            params.add(rsid);
            if (!"4".equals(rsid)
                    && !"8".equals(rsid)) {
                sql += " and rsdtlid=?";
                params.add(rsdtlid);
            }
            rs = dao.search(sql, params);
            if (rs.next()) {
                xml = Sql_switcher.readMemo(rs, "ctrlparam");
            }
            if (xml == null || "".equals(xml)) {
                xml = "<?xml version=\"1.0\" encoding=\"GB2312\"?>  <param> </param>  ";
            }
            Document doc = PubFunc.generateDom(xml);

            String path = "/param/report/r_user[@name='" + this.view.getUserName().toLowerCase() + "']";
            XPath xpath = XPath.newInstance(path);
            Element element = (Element) xpath.selectSingleNode(doc);
            if (element == null)//当输出excel时，如果自己未设置过，找管理员的设置
            {
                path = "/param/report/r_user[@super='1']";
                xpath = XPath.newInstance(path);
                //element = (Element)xpath.selectSingleNode(doc);
                ArrayList list = (ArrayList) xpath.selectNodes(doc);
                if (list != null && list.size() > 0)
                    element = (Element) list.get(0);//多个设置取第一个
            }
            /**
             * page_type 纸张大小
             * page_width 纸张宽mm
             * page_height 纸张高mm
             * page_range 排列方式
             *
             * pagemargin_top 页面边距 上
             * pagemargin_left
             * pagemargin_bottom
             * pagemargin__right
             */
            String page_type = "";
            String page_width = "";
            String page_height = "";
            String page_range = "";
            String pagemargin_top = "";
            String pagemargin_left = "";
            String pagemargin_bottom = "";
            String pagemargin_right = "";
            /**
             * title_content 标题内容
             * title_fontface 字体
             * title_fontsize 字体大小
             * title_fontblob 粗体
             * title_underline 下划线
             * title_fontitalic 斜体
             * title_delline 删除线
             * title_color 颜色
             */
            String title_content = "";
            String title_fontface = "";
            String title_fontsize = "";
            String title_fontblob = "";
            String title_underline = "";
            String title_fontitalic = "";
            String title_delline = "";
            String title_color = "";

            /**
             * head_left 上左
             * head_center 上中
             * head_right 上右
             * head_fontface 字体
             * head_fontsize 字体大小
             * head_fontblod 粗体
             * head_underline 下划线
             * head_fontitalic 斜体
             * head_delline 删除线
             * head_color 颜色
             * head_flw_hs 上左内容仅首页显示
             *	head_fmw_hs 上中内容仅首页显示
             *	head_frw_hs 上右内容仅首页显示
             */
            String head_left = "";
            String head_center = "";
            String head_right = "";
            String head_fontface = "";
            String head_fontsize = "";
            String head_fontblod = "";
            String head_underline = "";
            String head_fontitalic = "";
            String head_delline = "";
            String head_color = "";
            String head_flw_hs = "";
            String head_fmw_hs = "";
            String head_frw_hs = "";
            /**
             * tail_left 下左
             * tail_center 下中
             * tail_right 下右
             * tail_fontface 字体
             * tail_fontsize 字体大小
             * tail_fontblod 粗体
             * tail_underline 下划线
             * tail_fontitalic 斜体
             * tail_delline 删除线
             * tail_color 颜色
             * tail_flw_hs 下左内容仅首页显示
             *	tail_fmw_hs 下中内容仅首页显示
             *	tail_frw_hs 下右内容仅首页显示
             */
            String tail_left = "";
            String tail_center = "";
            String tail_right = "";
            String tail_fontface = "";
            String tail_fontsize = "";
            String tail_fontblob = "";
            String tail_underline = "";
            String tail_fontitalic = "";
            String tail_delline = "";
            String tail_color = "";
            String tail_flw_hs = "";
            String tail_fmw_hs = "";
            String tail_frw_hs = "";
            /**
             * main_fontface 字体
             * main_fontsize 字体大小
             * main_fontblod 粗体
             * main_underline 下划线
             * main_fontitalic 斜体
             * main_color 颜色
             */
            String main_fontface = "";
            String main_fontsize = "";
            String main_fontblob = "";
            String main_underline = "";
            String main_fontitalic = "";
            String main_color = "";

            String thead_fn = "";
            String thead_fz = "";
            String thead_fb = "";
            String thead_fu = "";
            String thead_fi = "";
            String thead_fc = "";

            if (element != null) {
                Element page = element.getChild("page");
                if (page != null) {
                    page_type = page.getAttributeValue("type");
                    page_width = page.getAttributeValue("width");
                    page_height = page.getAttributeValue("height");
                    page_range = page.getAttributeValue("range");
                    pagemargin_top = page.getAttributeValue("margin_top");
                    pagemargin_left = page.getAttributeValue("margin_left");
                    pagemargin_bottom = page.getAttributeValue("margin_bottom");
                    pagemargin_right = page.getAttributeValue("margin_right");
                }
                Element title = element.getChild("title");
                if (title != null) {
                    title_content = title.getAttributeValue("content");
                    title_fontface = title.getAttributeValue("fontface");
                    title_fontsize = title.getAttributeValue("fontsize");
                    title_fontblob = title.getAttributeValue("fontblob");
                    title_underline = title.getAttributeValue("underline");
                    title_fontitalic = title.getAttributeValue("fontitalic");
                    title_delline = title.getAttributeValue("dline");
                    title_color = title.getAttributeValue("color");
                }

                Element page_head = element.getChild("page_head");
                if (page_head != null) {
                    head_left = page_head.getAttributeValue("left");
                    head_center = page_head.getAttributeValue("center");
                    head_right = page_head.getAttributeValue("right");
                    head_fontface = page_head.getAttributeValue("fontface");
                    head_fontsize = page_head.getAttributeValue("fontsize");
                    head_fontblod = page_head.getAttributeValue("fontblob");
                    head_underline = page_head.getAttributeValue("underline");
                    head_fontitalic = page_head.getAttributeValue("fontitalic");
                    head_delline = page_head.getAttributeValue("dline");
                    head_color = page_head.getAttributeValue("color");
                    head_flw_hs = page_head.getAttributeValue("lHeadHomeShow");
                    head_fmw_hs = page_head.getAttributeValue("mHeadHomeShow");
                    head_frw_hs = page_head.getAttributeValue("rHeadHomeShow");
                }
                Element page_tail = element.getChild("page_tail");
                if (page_tail != null) {
                    tail_left = page_tail.getAttributeValue("left");
                    tail_center = page_tail.getAttributeValue("center");
                    tail_right = page_tail.getAttributeValue("right");
                    tail_fontface = page_tail.getAttributeValue("fontface");
                    tail_fontsize = page_tail.getAttributeValue("fontsize");
                    tail_fontblob = page_tail.getAttributeValue("fontblob");
                    tail_underline = page_tail.getAttributeValue("underline");
                    tail_fontitalic = page_tail.getAttributeValue("fontitalic");
                    tail_delline = page_tail.getAttributeValue("dline");
                    tail_color = page_tail.getAttributeValue("color");
                    tail_flw_hs = page_tail.getAttributeValue("lFootHomeShow");
                    tail_fmw_hs = page_tail.getAttributeValue("mFootHomeShow");
                    tail_frw_hs = page_tail.getAttributeValue("rFootHomeShow");
                }
                Element page_main = element.getChild("page_main");
                if (page_main != null) {
                    main_fontface = page_main.getAttributeValue("fontface");
                    main_fontsize = page_main.getAttributeValue("fontsize");
                    main_fontblob = page_main.getAttributeValue("fontblob");
                    main_underline = page_main.getAttributeValue("underline");
                    main_fontitalic = page_main.getAttributeValue("fontitalic");
                    main_color = page_main.getAttributeValue("color");
                }
                Element page_thead = element.getChild("page_thead");
                if (page_thead != null) {
                    thead_fn = page_thead.getAttributeValue("thead_fn");
                    thead_fz = page_thead.getAttributeValue("thead_fz");
                    thead_fb = page_thead.getAttributeValue("thead_fb");
                    thead_fu = page_thead.getAttributeValue("thead_fu");
                    thead_fi = page_thead.getAttributeValue("thead_fi");
                    thead_fc = page_thead.getAttributeValue("thead_fc");
                }
            }
            rpv.setThead_fb(thead_fb);
            rpv.setThead_fc(thead_fc);
            rpv.setThead_fi(thead_fi);
            rpv.setThead_fn(thead_fn);
            rpv.setThead_fu(thead_fu);
            rpv.setThead_fz(thead_fz);
            rpv.setPagetype(page_type);
            rpv.setWidth(page_width);
            rpv.setHeight(page_height);
            rpv.setOrientation(page_range);
            rpv.setTop(pagemargin_top);
            rpv.setLeft(pagemargin_left);
            rpv.setBottom(pagemargin_bottom);
            rpv.setRight(pagemargin_right);
            rpv.setTitle_fw(title_content);
            rpv.setTitle_fn(title_fontface);
            rpv.setTitle_fz(title_fontsize);
            rpv.setTitle_fb(title_fontblob);
            rpv.setTitle_fu(title_underline);
            rpv.setTitle_fi(title_fontitalic);
            rpv.setTitle_fs(title_delline);
            rpv.setTitle_fc(title_color);
            rpv.setHead_flw(head_left);
            rpv.setHead_fmw(head_center);
            rpv.setHead_frw(head_right);
            rpv.setHead_fn(head_fontface);
            rpv.setHead_fz(head_fontsize);
            rpv.setHead_fb(head_fontblod);
            rpv.setHead_fu(head_underline);
            rpv.setHead_fi(head_fontitalic);
            rpv.setHead_fs(head_delline);
            rpv.setHead_fc(head_color);
            rpv.setHead_flw_hs(head_flw_hs);
            rpv.setHead_fmw_hs(head_fmw_hs);
            rpv.setHead_frw_hs(head_frw_hs);
            rpv.setTile_flw(tail_left);
            rpv.setTile_fmw(tail_center);
            rpv.setTile_frw(tail_right);
            rpv.setTile_fn(tail_fontface);
            rpv.setTile_fz(tail_fontsize);
            rpv.setTile_fb(tail_fontblob);
            rpv.setTile_fi(tail_fontitalic);
            rpv.setTile_fu(tail_underline);
            rpv.setTile_fs(tail_delline);
            rpv.setTile_fc(tail_color);
            rpv.setTile_flw_hs(tail_flw_hs);
            rpv.setTile_fmw_hs(tail_fmw_hs);
            rpv.setTile_frw_hs(tail_frw_hs);
            rpv.setBody_fn(main_fontface);
            rpv.setBody_fz(main_fontsize);
            rpv.setBody_fb(main_fontblob);
            rpv.setBody_fu(main_underline);
            rpv.setBody_fi(main_fontitalic);
            rpv.setBody_fc(main_color);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return rpv;
    }

    /**
     * 列头ColumnsInfo对象初始化
     *
     * @param columnId   id
     * @param columnDesc 名称
     * @param columnDesc 显示列宽
     * @return
     */
    public ColumnsInfo getColumnsInfo(String columnId, String columnDesc,
                                       int columnWidth, String codesetId, String columnType,
                                       int columnLength, int decimalWidth) {

        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId(columnId);
        columnsInfo.setColumnDesc(columnDesc);
        columnsInfo.setColumnWidth(columnWidth);// 显示列宽
        columnsInfo.setCodesetId(codesetId);// 指标集
        columnsInfo.setColumnType(columnType);// 类型N|M|A|D
        columnsInfo.setColumnLength(columnLength);// 显示长度
        columnsInfo.setDecimalWidth(decimalWidth);// 小数位
        columnsInfo.setAllowBlank(true);// 编辑时是否可以为空
        columnsInfo.setQueryable(true);
        columnsInfo.setFilterable(true);
        if("UM".equalsIgnoreCase(codesetId) || "UN".equalsIgnoreCase(codesetId) || "@K".equalsIgnoreCase(codesetId)) {
        	columnsInfo.setCtrltype("3");
        	columnsInfo.setNmodule("1");
		}
        return columnsInfo;
    }
    /**
     * 数字转月份
     * @param month
     * @return
     */
    public String getUpperMonth(int month) {
        String mon = "";
        switch (month) {
	        case 1: {
				mon = ResourceFactory.getProperty("date.month.january");
				break;
			}
			case 2: {
				mon = ResourceFactory.getProperty("date.month.february");
				break;
			}
			case 3: {
				mon = ResourceFactory.getProperty("date.month.march");
				break;
			}
			case 4: {
				mon = ResourceFactory.getProperty("date.month.april");
				break;
			}
			case 5: {
				mon = ResourceFactory.getProperty("date.month.may");
				break;
			}
			case 6: {
				mon = ResourceFactory.getProperty("date.month.june");
				break;
			}
			case 7: {
				mon = ResourceFactory.getProperty("date.month.july");
				break;
			}
			case 8: {
				mon = ResourceFactory.getProperty("date.month.auguest");
				break;
			}
			case 9: {
				mon = ResourceFactory.getProperty("date.month.september");
				break;
			}
			case 10: {
				mon = ResourceFactory.getProperty("date.month.october");
				break;
			}
			case 11: {
				mon = ResourceFactory.getProperty("date.month.november");
				break;
			}
			case 12: {
				mon = ResourceFactory.getProperty("date.month.december");
				break;
			}
        }
        return mon;
    }

    /**
     *根据页面设置导出Excel 支持多个sheet和单个sheet，
     *多个sheet dataList 格式 [map{sheetname,datalist},map{sheetname,datalist}]
     * @param tableTitle
     *          excel名称
     * @param pageSet
     *          页面设置对象
     * @param dataList
     *          导出数据
     * @param columnList
     *          导出列
     * @param chartType 
     * @param chartTextaera {左上列x，左上行y，右下列x，右下行y}
     * @param isOutChart 
     * @return
     * @throws GeneralException
     */
    public String exportExcel(String tableTitle, ReportParseVo pageSet,ArrayList dataList,ArrayList<ColumnsInfo> columnList,
                              boolean isMultipleSheet, boolean isOutChart, int[] chartTextaera, String chartType) throws GeneralException {
        String fileName = "";
        View view = new View();
        view.getLock();
        ZipOutputStream out = null;
        FileOutputStream fileOut = null;
        FileInputStream fis = null;
        try {
        	fileName = this.view.getUserName() + "_" + tableTitle + ".xls";
        	if(isMultipleSheet) {//多个sheet
        		boolean isPack = false;
        		String tmpFileName  = "";
        		if(dataList.size()>0) {
        			if(dataList.size()>200) {//需要拆分成多个文件输出，并且打成zip包
        				isPack = true;
        				tmpFileName = this.view.getUserName() + "_" + tableTitle + ".zip"; 
        			}
        			HashMap filenameMap = new HashMap();
        			ArrayList sheetNameList = new ArrayList();
        			ArrayList fileNameList = new ArrayList();
        			int j=1;
        			for(int i=0;i<dataList.size();i++) {
        				HashMap layMap = (HashMap) dataList.get(i);
        				String sheetname = "";
        				ArrayList datalist  = new ArrayList();
        				for(Object key : layMap.keySet()) {
        					sheetname = (String)key;
        					datalist = (ArrayList) layMap.get(key);
        				}
        				int k = i>199?i-200*(j-1):i;
        				if(k!=0) {
    		    			view.insertSheets(k,1);//插入sheet
    		    		}
        				exportExcel(tableTitle, pageSet,datalist,columnList,view);
        				if(isOutChart)
        					exportChartExcel(view,dataList,columnList,chartTextaera,chartType,sheetname,pageSet);
        				if(sheetNameList.contains(sheetname)){
    						int num = 0;
    						if(filenameMap.containsKey(sheetname)){
    							num = Integer.parseInt((String)filenameMap.get(sheetname));
    							filenameMap.put(sheetname,num+1);
    						}else{
    							num = 1;
    							filenameMap.put(sheetname,1);
    						}
    						sheetname = sheetname+num;
    					}
        				sheetNameList.add(sheetname);
		    			view.setSheetName(k, sheetname);
		    			if(isPack&&i==200*j-1){//将文件输出，然后拆分下一个文件
		    				fileName = this.view.getUserName() + "_" + tableTitle +j+ ".xls";
		    				view.write(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName);
		    				view.releaseLock();
		    				view = new View();
		    		        view.getLock();
		    		        sheetNameList.clear();
		    		        fileNameList.add(fileName);
							j++;
		    			}
        			}
        			if(isPack) {
        				if(sheetNameList.size()>0) {
        					fileName = this.view.getUserName() + "_" + tableTitle +j+ ".xls";
        					view.write(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName);
        					fileNameList.add(fileName);
        				}
						byte[] buffer = new byte[1024];
						String strZipPath = System.getProperty("java.io.tmpdir")+File.separator+tmpFileName;
						fileOut = new FileOutputStream(strZipPath);
						out = new ZipOutputStream(fileOut);
						// 设置压缩文件内的字符编码，不然会变成乱码 59678 60004
						out.setEncoding("GBK");
						// 下载的文件集合
						for (int i = 0; i < fileNameList.size(); i++) {
							fis = new FileInputStream(
									System.getProperty("java.io.tmpdir") + File.separator + fileNameList.get(i));
							out.putNextEntry(new ZipEntry((String) fileNameList.get(i)));
							int len;
							// 读入需要下载的文件的内容，打包到zip文件
							while ((len = fis.read(buffer)) > 0) {
								out.write(buffer, 0, len);
							}
							out.closeEntry();
							PubFunc.closeIoResource(fis);
						}
  		              	out.close();
  		                fileName = tmpFileName;
        			}else {
        				view.write(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName);
        			}
        		}else {
        			exportExcel(tableTitle, pageSet,dataList,columnList,view);
            		if(isOutChart)
            			exportChartExcel(view,dataList,columnList,chartTextaera,chartType,"sheet1",pageSet);
            		view.write(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName);
        		}
        	}else {//单个sheet
        		exportExcel(tableTitle, pageSet,dataList,columnList,view);
        		if(isOutChart)
        			exportChartExcel(view,dataList,columnList,chartTextaera,chartType,"sheet1",pageSet);
        		view.write(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName);
        	}
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            view.releaseLock();
            PubFunc.closeIoResource(fileOut);
            PubFunc.closeIoResource(fis);
            PubFunc.closeIoResource(out);
        }
        return fileName;
    }

    /**
     * 导出图表，包括饼图和柱状图
     * @param view
     * @param dataList
     * @param columnList
     * @param chartTextaera
     * @param chartType
     * @param sheetname 
     * @param pageSet 
     */
	private void exportChartExcel(View view, ArrayList dataList, ArrayList<ColumnsInfo> columnList, int[] chartTextaera, String chartType, String sheetname, ReportParseVo pageSet) {
		try {
			int addchartnum = 0;
			int valuenum = 0;
			boolean havetitle = false;
			boolean havetop = false;
			boolean havefoot = false;
			String title_fw = pageSet.getTitle_fw();//标题
			String topLeft = pageSet.getHead_flw();//页头上左内容
	        String topCenter = pageSet.getHead_fmw();//上中
	        String topRight = pageSet.getHead_frw();//上右
	        String footLeft = pageSet.getTile_flw();//页尾上左内容
	        String footCenter = pageSet.getTile_fmw();//上中
	        String footRight = pageSet.getTile_frw();//上右
	        if(StringUtils.isNotBlank(title_fw)) {
	        	havetitle = true;
	        }
	        if(StringUtils.isNotBlank(topLeft)||StringUtils.isNotBlank(topCenter)||StringUtils.isNotBlank(topRight)) {
	        	havetop = true;
	        }
	        if(StringUtils.isNotBlank(footLeft)||StringUtils.isNotBlank(footCenter)||StringUtils.isNotBlank(footRight)) {
	        	havefoot = true;
	        }
	        if(havetitle&&havetop&&havefoot) {
	        	addchartnum = 3;
	        }else if((!havetitle&&havetop&&havefoot)||(havetitle&&!havetop&&havefoot)||(havetitle&&havetop&&!havefoot)) {
	        	addchartnum = 2;
	        }else if((!havetitle&&!havetop&&havefoot)||(havetitle&&!havetop&&!havefoot)||(!havetitle&&havetop&&!havefoot)) {
	        	addchartnum = 1;
	        }
	        if(havetitle&&havetop) {
	        	valuenum = 2;
	        }else if((!havetitle&&havetop)||(havetitle&&!havetop)) {
	        	valuenum = 1;
	        }
			// 绘图区坐标addChart（左上列x，左上行y，右下列x，右下行y）
			ChartShape chartshape = view.addChart(0, dataList.size()+2+addchartnum, columnList.size(), dataList.size()+2+20+addchartnum);
			if("column".equals(chartType)) {		        
				StringBuffer textPlace=new StringBuffer();//图例
				StringBuffer valuePlaceBar=new StringBuffer();//数据
				textPlace.append(sheetname+"!$"+getNum2En(chartTextaera[0])+"$"+(1+valuenum)+":$"+getNum2En(chartTextaera[2])+"$"+(1+valuenum));
				valuePlaceBar.append(sheetname+"!$"+getNum2En(chartTextaera[0])+"$"+(chartTextaera[1]+valuenum)+":$"+getNum2En(chartTextaera[2])+"$"+(chartTextaera[3]+valuenum));
				chartshape.setChartType(ChartShape.TypeColumn);//设置为柱状图
				chartshape.addSeries();
				chartshape.setSeriesYValueFormula(0, valuePlaceBar.toString());//设置柱状图数据范围
				chartshape.setCategoryFormula(textPlace.toString());//设置柱状图图例范围
				//设置横坐标标题
				//chartshape.setAxisTitle(ChartShape.XAxis, 0, "");
	            //设置纵坐标标题
				//chartshape.setAxisTitle(ChartShape.YAxis, 0, "");
				//chartshape.initData(new RangeRef(chartTextaera[0],chartTextaera[1],chartTextaera[2],chartTextaera[3]), true);
				chartshape.setVaryColors(true);
			}else if("pie".equals(chartType)||"line".equals(chartType)) {
				StringBuffer textPlace=new StringBuffer();
				StringBuffer valuePlacePie=new StringBuffer();
				textPlace.append(sheetname+"!$"+getNum2En(chartTextaera[0])+"$"+(1+valuenum)+":$"+getNum2En(chartTextaera[2])+"$"+(1+valuenum));
				valuePlacePie.append(sheetname+"!$"+getNum2En(chartTextaera[0])+"$"+(chartTextaera[1]+valuenum)+":$"+getNum2En(chartTextaera[2])+"$"+(chartTextaera[3]+valuenum));
				if("line".equals(chartType)) {
					chartshape.setChartType(ChartShape.TypeLine);//设置为折线图
				}else
					chartshape.setChartType(ChartShape.TypePie);//设置为饼图
				chartshape.addSeries();			
				chartshape.setSeriesYValueFormula(0, valuePlacePie.toString());//设置饼图数据范围
				chartshape.setCategoryFormula(textPlace.toString());//设置饼图图例范围
				chartshape.setVaryColors(true);//设置为彩色
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将数字转成excel中对应列
	 * @param num
	 * @return
	 */
	private String getNum2En(int num) {
		String endesc = "";
		String pre = "";
		int num_ = num;
		int i = num/26;
		if(i>0) {
			pre = getNum2En(i);
			num_ = num%26;
		}
        switch (num_) {
            case 1: {
                endesc = pre+"A";
                break;
            }
            case 2: {
                endesc = pre+"B";
                break;
            }
            case 3: {
                endesc = pre+"C";
                break;
            }
            case 4: {
                endesc = pre+"D";
                break;
            }
            case 5: {
                endesc = pre+"E";
                break;
            }
            case 6: {
                endesc = pre+"F";
                break;
            }
            case 7: {
                endesc = pre+"G";
                break;
            }
            case 8: {
                endesc = pre+"H";
                break;
            }
            case 9: {
                endesc = pre+"I";
                break;
            }
            case 10: {
                endesc = pre+"J";
                break;
            }
            case 11: {
                endesc = pre+"K";
                break;
            }
            case 12: {
                endesc = pre+"L";
                break;
            }
            case 13: {
                endesc = pre+"M";
                break;
            }
            case 14: {
                endesc = pre+"N";
                break;
            }
            case 15: {
                endesc = pre+"O";
                break;
            }
            case 16: {
                endesc = pre+"P";
                break;
            }
            case 17: {
                endesc = pre+"Q";
                break;
            }
            case 18: {
                endesc = pre+"R";
                break;
            }
            case 19: {
                endesc = pre+"S";
                break;
            }
            case 20: {
                endesc = pre+"T";
                break;
            }
            case 21: {
                endesc = pre+"U";
                break;
            }
            case 22: {
                endesc = pre+"V";
                break;
            }
            case 23: {
                endesc = pre+"W";
                break;
            }
            case 24: {
                endesc = pre+"X";
                break;
            }
            case 25: {
                endesc = pre+"Y";
                break;
            }
            case 26: {
                endesc = pre+"Z";
                break;
            }
        }
        return endesc;
	}


	private void exportExcel(String tableTitle, ReportParseVo pageSet,ArrayList<LazyDynaBean> dataList,
            ArrayList<ColumnsInfo> columnList,View view) throws GeneralException {
    	ContentDAO dao = new ContentDAO(this.conn);
    	try {
	    	//标题
	        String formTitle = pageSet.getTitle_fw();//表格标题
	        String titleFontType = pageSet.getTitle_fn();//标题字体
	        String titleFontBlod = pageSet.getTitle_fb();//标题粗体
	        String titleColor = pageSet.getTitle_fc();//标题颜色
	        String titleItalic = pageSet.getTitle_fi();//标题斜体
	        String titleUnderLine = pageSet.getTitle_fu();//标题下划线
	        String titleDelLine = pageSet.getTitle_fs();//标题删除线
	        String titleSize = StringUtils.isBlank(pageSet.getTitle_fz()) ? "14" : pageSet.getTitle_fz();//标题字体大小
	
	        //页头
	        String topLeft = pageSet.getHead_flw();//页头上左内容
	        String topCenter = pageSet.getHead_fmw();//上中
	        String topRight = pageSet.getHead_frw();//上右
	        String topFontType = pageSet.getHead_fn();//字体类型
	        String topFontBlod = pageSet.getHead_fb();//粗体
	        String topFontColor = pageSet.getHead_fc();//颜色
	        String topItalic = pageSet.getHead_fi();//斜体
	        String topUnderLine = pageSet.getHead_fu();//下划线
	        String topDelLine = pageSet.getHead_fs();//删除线
	        String topSize = StringUtils.isBlank(pageSet.getHead_fz()) ? "10" : pageSet.getHead_fz();//字体大小
	
	        //页尾
	        String footLeft = pageSet.getTile_flw();//页尾上左内容
	        String footCenter = pageSet.getTile_fmw();//上中
	        String footRight = pageSet.getTile_frw();//上右
	        String footFontType = pageSet.getTile_fn();//字体类型
	        String footFontBlod = pageSet.getTile_fb();//粗体
	        String footFontColor = pageSet.getTile_fc();//颜色
	        String footItalic = pageSet.getTile_fi();//斜体
	        String footUnderLine = pageSet.getTile_fu();//下划线
	        String footDelLine = pageSet.getTile_fs();//删除线
	        String footSize = StringUtils.isBlank(pageSet.getTile_fz()) ? "10" : pageSet.getTile_fz();//字体大小
	
	        //正文内容
	        String bodyFontType = pageSet.getBody_fn();//字体类型
	        String bodyFontBlod = pageSet.getBody_fb();//粗体
	        //可能第一次什么都没设置，直接点
	        String bodyFontColor = StringUtils.isBlank(pageSet.getBody_fc()) ? "#000000" : pageSet.getBody_fc();//颜色默认黑色
	        String bodyItalic = pageSet.getBody_fi();//斜体
	        String bodyUnderLine = pageSet.getBody_fu();//下划线
	        String bodySize = StringUtils.isBlank(pageSet.getBody_fz()) ? "10" : pageSet.getBody_fz();//字体大小
	
	        //正文表头
	        String theadFontType = pageSet.getThead_fn();//字体类型
	        String theadFontBlod = pageSet.getThead_fb();//粗体
	        String theadFontColor = StringUtils.isBlank(pageSet.getThead_fc()) ? "#000000" : pageSet.getThead_fc();//颜色
	        String theadItalic = pageSet.getThead_fi();//下划线
	        String theadUnderLine = pageSet.getThead_fu();//斜体
	        String theadSize = StringUtils.isBlank(pageSet.getThead_fz()) ? "10" : pageSet.getThead_fz();//字体大小
	
	
	        // 设置颜色
	        view.setPaletteEntry(1, new Color(230, 230, 230));// 浅灰色
	        view.setPaletteEntry(2, new Color(217, 217, 217));// 深灰色
	        view.setPaletteEntry(3, new Color(250, 0, 0));// 浅灰色
	        //标题的样式
	        CellFormat tTitle = null;
	        if (StringUtils.isNotBlank(formTitle)) {
	            tTitle = view.getCellFormat();
	            tTitle.setFontSize(Double.parseDouble(titleSize));// 设置字体大小
	            tTitle.setFontBold(StringUtils.isNotBlank(titleFontBlod) ? true : false);//粗体
	            tTitle.setFontItalic(StringUtils.isNotBlank(titleItalic) ? true : false);//斜线
	            tTitle.setFontStrikeout(StringUtils.isNotBlank(titleDelLine) ? true : false);//删除线
	            if (StringUtils.isNotBlank(titleUnderLine))//下划线
	                tTitle.setFontUnderline((short) 1);
	            int[] tColor = new int[3];
	            tColor[0] = Integer.parseInt(titleColor.substring(1, 3), 16);
	            tColor[1] = Integer.parseInt(titleColor.substring(3, 5), 16);
	            tColor[2] = Integer.parseInt(titleColor.substring(5, 7), 16);
	            if ("FF0000".equalsIgnoreCase(titleColor)) {
	                tTitle.setFontColor(view.getPaletteEntry(3));//颜色
	            } else {
	                tTitle.setFontColor(new Color(tColor[0], tColor[1], tColor[2]));//颜色
	            }
	            tTitle.setFontName(titleFontType);//字体
	
	            tTitle.setMergeCells(true);// 合并单元格
	            tTitle.setWordWrap(false);
	            // 水平对齐方式
	            tTitle.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
	            // 垂直对齐方式
	            tTitle.setVerticalAlignment(CellFormat.VerticalAlignmentCenter);
	        }
	
	        CellFormat hTitle = null;
	        if (StringUtils.isNotBlank(topLeft) || StringUtils.isNotBlank(topCenter) || StringUtils.isNotBlank(topRight)) {
	            hTitle = view.getCellFormat();
	            //页头的样式
	            hTitle.setFontSize(Double.parseDouble(topSize));// 设置字体大小
	            hTitle.setFontBold(StringUtils.isNotBlank(topFontBlod) ? true : false);//粗体
	            hTitle.setFontItalic(StringUtils.isNotBlank(topItalic) ? true : false);//斜线
	            hTitle.setFontStrikeout(StringUtils.isNotBlank(topDelLine) ? true : false);//删除线
	            if (StringUtils.isNotBlank(topUnderLine))//下划线
	                hTitle.setFontUnderline((short) 1);
	            int[] hColor = new int[3];
	            hColor[0] = Integer.parseInt(topFontColor.substring(1, 3), 16);
	            hColor[1] = Integer.parseInt(topFontColor.substring(3, 5), 16);
	            hColor[2] = Integer.parseInt(topFontColor.substring(5, 7), 16);
	            if ("FF0000".equalsIgnoreCase(topFontColor)) {
	                hTitle.setFontColor(view.getPaletteEntry(3));//颜色
	            } else {
	                hTitle.setFontColor(new Color(hColor[0], hColor[1], hColor[2]));//颜色
	            }
	            hTitle.setFontName(topFontType);//字体
	
	            hTitle.setWordWrap(true);
	            // 垂直对齐方式
	            hTitle.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
	            hTitle.setVerticalAlignment(CellFormat.VerticalAlignmentTop);
	        }
	
	        CellFormat fTitle = null;
	        if (StringUtils.isNotBlank(footLeft) || StringUtils.isNotBlank(footCenter) || StringUtils.isNotBlank(footRight)) {
	            fTitle = view.getCellFormat();
	            //页尾的样式
	            fTitle.setFontSize(Double.parseDouble(footSize));// 设置字体大小
	            fTitle.setFontBold(StringUtils.isNotBlank(footFontBlod) ? true : false);//粗体
	            fTitle.setFontItalic(StringUtils.isNotBlank(footItalic) ? true : false);//斜线
	            fTitle.setFontStrikeout(StringUtils.isNotBlank(footDelLine) ? true : false);//删除线
	            if (StringUtils.isNotBlank(footUnderLine))//下划线
	                fTitle.setFontUnderline((short) 1);
	            int[] fColor = new int[3];
	            fColor[0] = Integer.parseInt(footFontColor.substring(1, 3), 16);
	            fColor[1] = Integer.parseInt(footFontColor.substring(3, 5), 16);
	            fColor[2] = Integer.parseInt(footFontColor.substring(5, 7), 16);
	            if ("FF0000".equalsIgnoreCase(footFontColor)) {
	                fTitle.setFontColor(view.getPaletteEntry(3));//颜色
	            } else {
	                fTitle.setFontColor(new Color(fColor[0], fColor[1], fColor[2]));//颜色
	            }
	            fTitle.setFontName(footFontType);//字体
	            fTitle.setWordWrap(true);
	            // 水平对齐方式
	            fTitle.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
	            // 垂直对齐方式
	            fTitle.setVerticalAlignment(CellFormat.VerticalAlignmentTop);
	        }
	
	        // 表头样式
	        view.setDefaultRowHeight(400);
	        CellFormat cfTitle = view.getCellFormat();
	        cfTitle.setFontSize(Double.parseDouble(theadSize));// 设置字体大小
	        cfTitle.setFontBold(StringUtils.isNotBlank(theadFontBlod) ? true : false);//粗体
	        cfTitle.setFontItalic(StringUtils.isNotBlank(theadItalic) ? true : false);//斜线
	        if (StringUtils.isNotBlank(theadUnderLine))//下划线
	            cfTitle.setFontUnderline((short) 1);
	        int[] cfColor = new int[3];
	        cfColor[0] = Integer.parseInt(theadFontColor.substring(1, 3), 16);
	        cfColor[1] = Integer.parseInt(theadFontColor.substring(3, 5), 16);
	        cfColor[2] = Integer.parseInt(theadFontColor.substring(5, 7), 16);
	        if ("FF0000".equalsIgnoreCase(theadFontColor)) {
	            cfTitle.setFontColor(view.getPaletteEntry(3));//颜色
	        } else {
	            cfTitle.setFontColor(new Color(cfColor[0], cfColor[1], cfColor[2]));//颜色
	        }
	        cfTitle.setFontName(theadFontType);//字体
	
	        cfTitle.setBottomBorder((short) 1);// 设置边框为细实线
	        cfTitle.setTopBorder(CellFormat.PatternSolid);
	        cfTitle.setLeftBorder((short) 1);
	        cfTitle.setRightBorder((short) 1);
	        cfTitle.setWordWrap(true);
	        // 水平对齐方式
	        cfTitle.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
	        // 垂直对齐方式
	        cfTitle.setVerticalAlignment(CellFormat.VerticalAlignmentCenter);
	
	        // 内容样式
	        CellFormat cfBody = view.getCellFormat();
	        cfBody.setFontSize(Double.parseDouble(bodySize));// 设置字体大小
	        cfBody.setFontBold(StringUtils.isNotBlank(bodyFontBlod) ? true : false);//粗体
	        cfBody.setFontItalic(StringUtils.isNotBlank(bodyItalic) ? true : false);//斜线
	        if (StringUtils.isNotBlank(bodyUnderLine))//下划线
	            cfBody.setFontUnderline((short) 1);
	        int[] bodyColor = new int[3];
	        bodyColor[0] = Integer.parseInt(bodyFontColor.substring(1, 3), 16);
	        bodyColor[1] = Integer.parseInt(bodyFontColor.substring(3, 5), 16);
	        bodyColor[2] = Integer.parseInt(bodyFontColor.substring(5, 7), 16);
	        if ("FF0000".equalsIgnoreCase(bodyFontColor)) {
	            cfBody.setFontColor(view.getPaletteEntry(3));//颜色
	        } else {
	            cfBody.setFontColor(new Color(bodyColor[0], bodyColor[1], bodyColor[2]));//颜色
	        }
	        cfBody.setFontName(bodyFontType);//字体
	
	        cfBody.setWordWrap(true);
	        cfBody.setBottomBorder(CellFormat.PatternSolid);// 设置边框为细实线
	        cfBody.setTopBorder(CellFormat.PatternSolid);
	        cfBody.setLeftBorder(CellFormat.PatternSolid);
	        cfBody.setRightBorder(CellFormat.PatternSolid);
	        //cfBody.setHorizontalAlignment(cfBody.HorizontalAlignmentLeft);// 水平居左
	        cfBody.setVerticalAlignment(CellFormat.VerticalAlignmentCenter);// 垂直居中
	
	        view.setDefaultColWidth(15 * 256);// 固定列宽
	        cfTitle.setPattern(CellFormat.PatternSolid);
	        cfTitle.setPatternFG(view.getPaletteEntry(1));// 设置添加背景色
	
	        //如果设置了标题和页头之类的则所有数据往后移动
	        int addColum = 0;
	        boolean hasTopHead = false;
	        //添加主标题
	        if (StringUtils.isNotBlank(formTitle)) {
	            formTitle = pageSet.getRealcontent(formTitle, this.view, dataList.size(), tableTitle, 1, dao);
	            view.setText(formTitle);
	            view.setRowHeight(0, 800);
	            view.setCellFormat(tTitle, 0, 0, 0, columnList.size() - 1);
	            addColum++;
	        }
	        //添加页头上左
	        if (StringUtils.isNotBlank(topLeft)) {
	            topLeft = pageSet.getRealcontent(topLeft, this.view, dataList.size(), tableTitle, 1, dao);
	            view.setText(addColum, 0, topLeft);
	            hTitle.setHorizontalAlignment(CellFormat.HorizontalAlignmentLeft);
	            view.setCellFormat(hTitle, addColum, 0, addColum, 0);
	            hasTopHead = true;
	        }
	        //添加页头上中
	        if (StringUtils.isNotBlank(topCenter)) {
	            topCenter = pageSet.getRealcontent(topCenter, this.view, dataList.size(), tableTitle, 1, dao);
	            hTitle.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
	            view.setText(addColum, (columnList.size() - 1) / 2, topCenter);
	            view.setCellFormat(hTitle, addColum, (columnList.size() - 1) / 2, addColum, (columnList.size() - 1) / 2);
	            hasTopHead = true;
	        }
	        //添加页头上右
	        if (StringUtils.isNotBlank(topRight)) {
	            topRight = pageSet.getRealcontent(topRight, this.view, dataList.size(), tableTitle, 1, dao);
	            view.setText(addColum, columnList.size() - 1, topRight);
	            hTitle.setHorizontalAlignment(CellFormat.HorizontalAlignmentRight);
	            view.setCellFormat(hTitle, addColum, columnList.size() - 1, addColum, columnList.size() - 1);
	            hasTopHead = true;
	        }
	
	        if (hasTopHead)//如果有上左，上中，上右其中的一个，直加一行
	            addColum++;
	
	        addColum = this.setHeadTitle(cfTitle, columnList, view, addColum);//插入标题
            int rowNums = addColum + 1;//数据所在的行
            
            for (int rowNum = 0; rowNum < dataList.size(); rowNum++) {
            	int double_column = 0;
                LazyDynaBean dataBean = dataList.get(rowNum);
                for (int colNum = 0; colNum < columnList.size(); colNum++) {
                    ColumnsInfo headBean = columnList.get(colNum);
                    ArrayList<ColumnsInfo> list_child = headBean.getChildColumns();
                    if(list_child.size() > 0) {
                    	for(int j = 0; j < list_child.size(); j++) {
                    		headBean = list_child.get(j);
                    		view = setData(headBean, dataBean, rowNums, double_column, cfBody, view, rowNum);
                    		double_column++;
                    	}
                    }else {
                    	view = setData(headBean, dataBean, rowNums, double_column, cfBody, view, rowNum);
                    	double_column++;
                    }
                    
                }
                rowNums++;
            }
            //添加页尾下左
            if (StringUtils.isNotBlank(footLeft)) {
                footLeft = pageSet.getRealcontent(footLeft, this.view, dataList.size(), tableTitle, 1, dao);
                view.setText(rowNums, 0, footLeft);
                view.setCellFormat(fTitle, rowNums, 0, rowNums, 0);
            }
            //添加页尾下中
            if (StringUtils.isNotBlank(footCenter)) {
                footCenter = pageSet.getRealcontent(footCenter, this.view, dataList.size(), tableTitle, 1, dao);
                view.setText(rowNums, (columnList.size() - 1) / 2, footCenter);
                view.setCellFormat(fTitle, rowNums, (columnList.size() - 1) / 2, rowNums, (columnList.size() - 1) / 2);

            }
            //添加页尾下右
            if (StringUtils.isNotBlank(footRight)) {
                footRight = pageSet.getRealcontent(footRight, this.view, dataList.size(), tableTitle, 1, dao);
                view.setText(rowNums, columnList.size() - 1, footRight);
                view.setCellFormat(fTitle, rowNums, columnList.size() - 1, rowNums, columnList.size() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            view.releaseLock();
        }
	}

	private View setData(ColumnsInfo headBean, LazyDynaBean dataBean, int rowNums, int colNum,CellFormat cfBody, View view, int rowNum) {
    	try {
	    	String itemid = headBean.getColumnId();
	        String align_ = headBean.getTextAlign();
	        String itemtype = headBean.getColumnType();
	        String codesetId = headBean.getCodesetId();
	        int nwidth = headBean.getColumnWidth();
	        int itemfmt = headBean.getDecimalWidth();
	        short align = 0;
	        if ("left".equalsIgnoreCase(align_)) {
	            align = CellFormat.HorizontalAlignmentLeft;
	        } else if ("center".equalsIgnoreCase(align_)) {
	            align = CellFormat.HorizontalAlignmentCenter;
	        } else if ("right".equalsIgnoreCase(align_)) {
	            align = CellFormat.HorizontalAlignmentRight;
	        }
	        String value = dataBean.get(itemid) == null ?"":String.valueOf(dataBean.get(itemid));
	        if ("A".equalsIgnoreCase(itemtype)) {
	            String content = "";
	            if (!StringUtils.isBlank(codesetId) && !"0".equals(codesetId)) {
                    String[] arr = value.split("`");
                    if(arr.length==2){
                        value=arr[1];
                    }else{
                        value = AdminCode.getCodeName(codesetId, value);
                    }
	            }
                view.setText(rowNums, colNum, value);
	            view.setCellFormat(cfBody, rowNums, colNum, rowNums, colNum);
	        }else if("N".equalsIgnoreCase(itemtype)){
	            String pattern = "#,##0";
	            String between = "";
	            if (itemfmt > 0)
	            	between += ".";
	            for (int ia = 0; ia < itemfmt; ia++)
	            	between += "0";
	            String format = pattern + between;
            	cfBody.setCustomFormat(format);
            	if(StringUtils.isNotEmpty(value)) {
            		view.setNumber(rowNums, colNum, Double.parseDouble(value));
            	}else
                	view.setText(rowNums, colNum, value);
            	view.setCellFormat(cfBody, rowNums, colNum, rowNums, colNum);
            } else {
	            view.setText(rowNums, colNum, value);
	            view.setCellFormat(cfBody, rowNums, colNum, rowNums, colNum);
	        }
	        cfBody.setHorizontalAlignment(align);
	        if (rowNum == 0) {
	            view.setColWidth(colNum, nwidth * 40);
	        }
	        view.setCellFormat(cfBody, rowNums, colNum, rowNums, colNum);
    	} catch (CellException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	return view;
    }
    /**
     * 设置Excel 表头
     * @param cfTitle
     * @param headList
     * @param view
     * @param row
     */
    private int setHeadTitle(CellFormat cfTitle, ArrayList<ColumnsInfo> headList, View view, int row) {
        try {
            view.setRowHeight(row, 600);
            boolean isDoubleTitle = false;
            for (int i = 0; i < headList.size(); i++) {
            	ColumnsInfo col = headList.get(i);
                String itemdesc = col.getColumnDesc();
                ArrayList childList = col.getChildColumns();
                if(childList.size() > 0) {
                	isDoubleTitle = true;
                	break;
                }
            }
            int duble_title = 0;
            for (int i = 0; i < headList.size(); i++) {
                ColumnsInfo col = headList.get(i);
                String itemdesc = col.getColumnDesc();
                ArrayList<ColumnsInfo> childList = col.getChildColumns();
                int width = col.getColumnWidth();
                if(isDoubleTitle && childList.size() > 0) {//双层表头，有子栏目
                	int temp_col = duble_title;
                	for(int j = 0; j < childList.size(); j++) {
                		ColumnsInfo col_child = childList.get(j);
                		width = col_child.getColumnWidth();
                        String itemdesc_child = col_child.getColumnDesc();
                        view.setText(row+1, duble_title, itemdesc_child);
                        view.setColWidth(duble_title, width*30);
                        view.setCellFormat(cfTitle, row+1, duble_title, row+1, duble_title); // 设置标题区域 和样式
                        duble_title++;
                	}
                	duble_title--;//因为近来的时候已经是1列了，如果循环一直加，则多了一列
                	view.setText(row, temp_col, itemdesc);
                	cfTitle.setMergeCells(true);
                    view.setCellFormat(cfTitle, row, temp_col, row, duble_title); // 设置标题区域 和样式
                    duble_title++;
                }else if(isDoubleTitle && childList.size() == 0) {
                	view.setColWidth(duble_title, width*30);
                	view.setText(row, duble_title, itemdesc);
                	view.setCellFormat(cfTitle, row, duble_title, row+1, duble_title); // 设置标题区域 和样式
                	duble_title++;
                }else {
                	view.setColWidth(i, width*30);
                	view.setText(row, i, itemdesc);
                	view.setCellFormat(cfTitle, row, i, row, i); // 设置标题区域 和样式
                }
            }
            if(isDoubleTitle) {
            	row = row + 1;
            }
        } catch (CellException e) {
            e.printStackTrace();
        }
        return row;
    }
    
    /**
	 * 提供相对精确的除法运算
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param scale
	 * @return
	 */
	public static String div(String v1, String v2, int scale) {
		if (scale < 0) {
			scale = 2;
		}
		if (v2 == null || "".equals(v2) ||Float.parseFloat(v2)==0) {
			v2 = "1";
		}
		if (v1 == null || "0".equals(v1)|| "".equals(v1)||v1.trim().length()==0) {
			return "0";
		}
		BigDecimal b1 = new BigDecimal(v1);

		BigDecimal b2 = new BigDecimal(v2);

		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toString();

	}

	public Object getCacheConfig(HashMap map, JSONObject jsonObj, String id) {
		Object obj = null;
		try {
			if(map != null) {
				obj = map.get(id);
			}else {
				obj = jsonObj.get(id);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

    /**
     * 根据已选人员库 获得人员库的翻译
     * @param nbases 分析表中勾选的人员库
     * @return
     */
	public Map<String,String> getDbNameMap(String nbases) throws GeneralException{
        Map<String,String> map = new HashMap<String,String>();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            if(StringUtils.isEmpty(nbases)){
                return map;
            }
            StringBuffer sql = new StringBuffer();
            sql.append("select pre,dbname from dbname where lower(pre) in (");
            String[] nbaseArr = nbases.toLowerCase().split(",");
            List values = new ArrayList();
            for(int i=0;i<nbaseArr.length;i++){
                if(i>0) {
                    sql.append(",");
                }
                sql.append("?");
                values.add(nbaseArr[i]);
            }
            sql.append(")");

            rs = dao.search(sql.toString(),values);
            while(rs.next()){
                String key = rs.getString("pre");
                String value = rs.getString("dbname");
                map.put(key,value);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }

        return map;
    }
	
	/**
     * 创建条件过滤
     * @return
     */
    public String createFilterSql(String filterParam){
    	if(StringUtils.isBlank(filterParam)) {
    		return "";
    	}
        JSONObject json = JSONObject.fromObject(SafeCode.keyWord_reback(filterParam));
        String itemid = json.getString("field");
        String itemtype = json.getString("itemtype");
        JSONArray factor = json.getJSONArray("factor");
        String expr = json.getString("expr");
        //如果为空或者没有数据，返回 原sql
        if(factor==null || factor.isEmpty()){
            return "";
        }
        StringBuffer filterWhere = new StringBuffer(" and (");
        String symbol;
        String value = "";
        filterIf:if("C".equals(itemtype)){// C代码型指标
            expr = "or";
            for(int i=0;i<factor.size();i++){
                String f = factor.getString(i);
                value = f.substring(f.indexOf("`")+1);
                filterWhere.append("UPPER("+itemid+") like '"+value.toUpperCase()+"%' or ");
            }
        }else if("D".equals(itemtype)){//时间类型
            String plan = json.getString("plan");

            if("custom".equals(plan)){
                for(int i=0;i<factor.size();i++){

                    String f = factor.getString(i);
                    symbol = f.substring(0,f.indexOf("`"));
                    value = f.substring(f.indexOf("`")+1);

                    String format = "YYYY-MM-DD HH24:mi:ss";
                    if(value.length()==4){
                        format = "YYYY";
                    }else if(value.length()==7)
                        format = "YYYY-MM";
                    else if(value.length()==10)
                        format = "YYYY-MM-DD";
                    else if(value.length()==16){
                        //当日期没有秒时，补位
                        value+=":00";
                    }

                    filterWhere.append(Sql_switcher.dateToChar(itemid, format)).append(symbol).append(" '").append(value).append("' ");
                    filterWhere.append(expr).append(" ");
                }

                break filterIf;
            }

            String f = factor.getString(0);
            symbol = f.substring(0,f.indexOf("`"));
            Calendar c = Calendar.getInstance();

            if("nextMonth".equals(symbol)){
                filterWhere.append(Sql_switcher.month(itemid)+"="+(c.get(Calendar.MONTH)+2)+" "+expr+" ");
            }else if("thisMonth".equals(symbol)){
                filterWhere.append(Sql_switcher.month(itemid)+"="+(c.get(Calendar.MONTH)+1)+" "+expr+" ");
            }else if("lastMonth".equals(symbol)){
                filterWhere.append(Sql_switcher.month(itemid)+"="+c.get(Calendar.MONTH)+" "+expr+" ");
            }else if("nextYear".equals(symbol)){
                filterWhere.append(Sql_switcher.year(itemid)+"="+(c.get(Calendar.YEAR)+1)+" "+expr+" ");
            }else if("thisYear".equals(symbol)){
                filterWhere.append(Sql_switcher.year(itemid)+"="+c.get(Calendar.YEAR)+" "+expr+" ");
            }else if("lastYear".equals(symbol)){
                filterWhere.append(Sql_switcher.year(itemid)+"="+(c.get(Calendar.YEAR)-1)+" "+expr+" ");
            }else{
                int nextYear = -1;
                int lastYear = -1;
                String nextSeason = "";
                String thisSeason = "";
                String lastSeason = "";
                if(c.get(Calendar.MONTH)<3){
                    thisSeason = "1,2,3";
                    lastYear = c.get(Calendar.YEAR)-1;
                }else if(c.get(Calendar.MONTH)<6){
                    nextSeason = "7,8,9";
                    thisSeason = "4,5,6";
                    lastSeason = "1,2,3";
                }else if(c.get(Calendar.MONTH)<9){
                    nextSeason = "10,11,12";
                    thisSeason = "7,8,9";
                    lastSeason = "4,5,6";
                }else{
                    thisSeason = "10,11,12";
                    nextYear = c.get(Calendar.YEAR)+1;
                }
                if("nextSeason".equals(symbol)){
                    if(nextYear>0)
                        filterWhere.append(Sql_switcher.month(itemid)+" in (1,2,3) and "+Sql_switcher.year(itemid)+"="+nextYear+" "+expr+" ");
                    else
                        filterWhere.append(Sql_switcher.month(itemid)+" in ("+nextSeason+") "+expr+" ");
                }else if("thisSeason".equals(symbol)){
                    filterWhere.append(Sql_switcher.month(itemid)+" in ("+thisSeason+") "+expr+" ");
                }else if("lastSeason".equals(symbol)){
                    if(lastYear>0)
                        filterWhere.append(Sql_switcher.month(itemid)+" in (10,11,12) and "+Sql_switcher.year(itemid)+"="+lastYear+" "+expr+" ");
                    else
                        filterWhere.append(Sql_switcher.month(itemid)+" in ("+lastSeason+") "+expr+" ");
                }
            }
        }else if("N".equals(itemtype)){//int型
            for(int i=0;i<factor.size();i++){
                String f = factor.getString(i);
                symbol = f.substring(0,f.indexOf("`"));
                value = f.substring(f.indexOf("`")+1);
                filterWhere.append(Sql_switcher.isnull(itemid, "0")+symbol+value+" "+expr+" ");
            }
        }else{//M(文本)型和A(字符)型
            for(int i=0;i<factor.size();i++){
                String f = factor.getString(i);
                symbol = f.substring(0,f.indexOf("`"));
                try {
                    value = URLDecoder.decode(f.substring(f.indexOf("`")+1), "UTF-8");
                    value = PubFunc.hireKeyWord_filter(value);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                };
                if("sta".equals(symbol)){//开头是
                    filterWhere.append(itemid+" like '"+value+"%' "+expr+" ");
                }else if("stano".equals(symbol)){//开头不是
                    filterWhere.append(itemid+" not like '"+value+"%' "+expr+" ");
                }else if("end".equals(symbol)){//结尾是
                    filterWhere.append(itemid+" like '%"+value+"' "+expr+" ");
                }else if("endno".equals(symbol)){//结尾不是
                    filterWhere.append(itemid+" not like '%"+value+"' "+expr+" ");
                }else if("cont".equals(symbol)){//包含
                    filterWhere.append(itemid+" like '%"+value+"%' "+expr+" ");
                }else if("contno".equals(symbol)){//不包含
                    filterWhere.append(itemid+" not like '%"+value+"%' "+expr+" ");
                }else{
                    if("=".equals(symbol) &&  value.indexOf("？")+value.indexOf("＊")>-2){
                        symbol = " like ";
                        value = value.replaceAll("？", "?");
                        value = value.replaceAll("＊", "%");
                    }
                    if(value.length()==0 && "=".equals(symbol) ){
                        filterWhere.append(" ("+ Sql_switcher.sqlToChar(itemid)+symbol+" '' or "+Sql_switcher.sqlToChar(itemid)+" is null ) "+expr+" ");
                    }else
                        filterWhere.append(Sql_switcher.sqlToChar(itemid)+symbol+" '"+value+"' "+expr+" ");
                }
            }
        }
        if("or".equals(expr))
            filterWhere.append(" 1=2 ");
        else
            filterWhere.append(" 1=1 ");
        filterWhere.append(" )");
        return filterWhere.toString();
    }
    
    /**
     * 创建条件过滤
     * @return
     */
    public String createOrderBySql(String sort){
    	String order_sql = "";
    	if(sort != null) {
	    	sort = PubFunc.hireKeyWord_filter_reback(sort);
			JSONArray sortArray = JSONArray.fromObject(sort);
			JSONObject sortObj = sortArray.getJSONObject(0);
	        String itemid = sortObj.getString("property");
	        String itemtype = sortObj.getString("direction");
	        order_sql = itemid + " " + itemtype;
    	}
    	return order_sql;
    }
    
    public String getCodeName(String id, String value) {
    	String name = "";
    	if(StringUtils.isNotBlank(id) && StringUtils.isNotBlank(value)) {
			FieldItem item = DataDictionary.getFieldItem(id);
			name = AdminCode.getCodeName(item.getCodesetid(), value);
		    //兼容处理，单位没找到，找部门
		    if("UN".equals(item.getCodesetid()) && name.length()<1){
		    		name = AdminCode.getCodeName("UM",value);
		    }
		    //兼容处理，部门没找到，找单位
		    if("UM".equals(item.getCodesetid()) && name.length()<1)
		    	   name = AdminCode.getCodeName("UN",value);
    	}
	    return name;
	}
    
    /**
     * 是否有私有方案
     * @param submoduleid
     * @return
     */
    private String hasPrivateScheme(String submoduleid) {
    	RowSet rset=null;
    	String result = "";
    	ArrayList<String> list = new ArrayList<String>();
    	try {
			ContentDAO dao = new ContentDAO(this.conn);
	    	// 是否存在私有记录
			String sqlForPrivate = "select scheme_id from t_sys_table_scheme where submoduleid = ? and is_share = 0 and username = ?";
			list.add(submoduleid);
			list.add(this.view.getUserName());
	    	rset=dao.search(sqlForPrivate, list);
	    	if(rset.next()){
	    		result = rset.getString("scheme_id");
	    	}
    	}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
    	return result;
    }
    
    /**
     * 是否有公有方案
     * @param submoduleid
     * @return
     */
    private String hasShareScheme(String submoduleid) {
    	RowSet rset=null;
    	String result = "";
    	ArrayList<String> list = new ArrayList<String>();
    	try {
			ContentDAO dao = new ContentDAO(this.conn);
	    	// 是否存在私有记录
			String sqlForShare= "select scheme_id from t_sys_table_scheme where submoduleid = ? and is_share = 1";
			list.add(submoduleid);
    		rset=dao.search(sqlForShare, list);
        	if(rset.next()){
        		result = rset.getString("scheme_id");
        	}
    	}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
    	return result;
    }
    /**
     * 将表格的修改保存到栏目设置
     * @param map
     */
	public void setChange2SchemeSet(HashMap map) {
		RowSet rset=null;
		try{
			String codeitemid=(String)map.get("codeitemid");
			String submoduleid=(String)map.get("submoduleid");
			String width=(String)map.get("width");
			String isshare=(String)map.get("isshare");
			String username = this.view.getUserName();
			boolean flagforshare = false;
			ContentDAO dao = new ContentDAO(this.conn);
			// 是否存在私有记录
			String pri_scheme = hasPrivateScheme(submoduleid);
        	if(StringUtils.isBlank(pri_scheme)) {//不存在私有
        		// 查询共有方案
        		String share_scheme = hasShareScheme(submoduleid);
        		
        		flagforshare = StringUtils.isNotBlank(share_scheme)?true:false;
        		insertScheme(flagforshare, submoduleid);
        	}
        	String sql = "update t_sys_table_scheme_item set displaywidth=? where itemid=? and scheme_id = (select scheme_id from t_sys_table_scheme where submoduleid=? and username=? and is_share=?)";
			ArrayList<String> list = new ArrayList<String>();
			list.add(width);
			list.add(codeitemid);
			list.add(submoduleid);
			list.add(username);
			list.add(isshare);
			dao.update(sql, list);
			setTableCache(submoduleid, width, codeitemid);
			
        }catch(Exception e){
        	e.printStackTrace();
        }finally {
        	PubFunc.closeDbObj(rset);
        }
	}
	
	private String insertScheme(boolean flagforshare, String submoduleid) {
		RowSet rset=null;
		String username = this.view.getUserName();
		ContentDAO dao = new ContentDAO(this.conn);
		String num = "";
		try {
			if(!flagforshare) {//没有共有方案
				TableDataConfigCache cache = (TableDataConfigCache) view.getHm().get(submoduleid);
				int pageSize = cache.getPageSize();
				String sql = "select "+Sql_switcher.isnull("MAX(scheme_id)","0")+"+1 as num from t_sys_table_scheme";
				rset = dao.search(sql);
				while(rset.next()){
					num = rset.getString("num");
				}
				sql = "INSERT INTO t_sys_table_scheme (scheme_id,submoduleid,username,is_share,rows_per_page) VALUES(?,?,?,?,?)";
				ArrayList<String> list = new ArrayList<String>(Arrays.asList(num, submoduleid, "su", "1", pageSize+""));//公有方案保存成su的名字
				dao.insert(sql, list);
				
				sql = "INSERT INTO t_sys_table_scheme_item (scheme_id,itemid,displayorder,displaydesc,is_display,displaywidth,align,is_order,is_sum,"
						+ "itemdesc,mergedesc,is_lock,is_fromdict,is_removable) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				ArrayList batchList = new ArrayList();
				//得到submoduleid 对应的表格的列
				ArrayList<ColumnsInfo> tablecolumns = cache.getTableColumns();
				int displayorder = 0;
				for(ColumnsInfo info:tablecolumns){
					ArrayList<ColumnsInfo> childColumn = info.getChildColumns();
					if(childColumn.size() > 0) {
						for(int i = 0; i < childColumn.size(); i++) {
							batchList.add(getColumnList(num, childColumn.get(i), displayorder, info.getColumnDesc()));
							displayorder++;
						}
					}else {
						batchList.add(getColumnList(num, info, displayorder, ""));
						displayorder++;
					}
				}
				dao.batchInsert(sql, batchList);
				flagforshare = true;
        	}
        	if(flagforshare){
				// 插入栏目设置表，把已有的公有方案拷贝成私有方案，最大id+1
				StringBuilder buf = new StringBuilder();
				buf.append("insert into t_sys_table_scheme (scheme_id,submoduleid,username,is_share,rows_per_page)");
				buf.append("(select (select MAX(scheme_id)+1 from t_sys_table_scheme),submoduleid,?,'0',rows_per_page");
				buf.append(" from t_sys_table_scheme");
				buf.append(" where submoduleid = ? and is_share = '1')");
				ArrayList<String> list = new ArrayList<String>();
				list.add(username);
				list.add(submoduleid);
				dao.insert(buf.toString(), list);
				// 获得拷贝的公有方案id
				String sql = "select scheme_id from t_sys_table_scheme where submoduleid=? and is_share=1";
				list.clear();
				list.add(submoduleid);
				rset = dao.search(sql, list);
				String copy_scheme_id = "";
				while(rset.next()){
					copy_scheme_id = rset.getString("scheme_id");
				}
				// 获得拷贝后的私有方案id
				sql = "select scheme_id from t_sys_table_scheme where submoduleid=? and username=? and is_share=0";
				list.clear();
				list.add(submoduleid);
				list.add(username);
				rset = dao.search(sql, list);
				while(rset.next()){
					num = rset.getString("scheme_id");
				}
				// 插入栏目设置item表，查询出拷贝的id，把id改成私有方案id，插回栏目设置表
				if(!StringUtils.isEmpty(copy_scheme_id) && !StringUtils.isEmpty(num)){
					buf.setLength(0);
					buf.append("insert into t_sys_table_scheme_item (scheme_id,itemid,displayorder,displaydesc,is_display,displaywidth,align,is_order,is_sum,itemdesc,mergedesc,is_lock,is_fromdict,is_removable)");
					buf.append("(select ?,itemid,displayorder,displaydesc,is_display,displaywidth,align,is_order,is_sum,itemdesc,mergedesc,is_lock,is_fromdict,is_removable");
					buf.append(" from t_sys_table_scheme_item");
					buf.append(" where scheme_id = ?)");
					list.clear();
					list.add(num);
					list.add(copy_scheme_id);
					dao.insert(buf.toString(), list);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
		return num;
	}
	/**
	 * 列宽设置到缓存中
	 * @param submoduleid
	 * @param width
	 * @param codeitemid
	 */
	private void setTableCache(String submoduleid, String width, String codeitemid) {
		boolean isIn = false;
		try {
			TableDataConfigCache cache = (TableDataConfigCache) view.getHm().get(submoduleid);
			ArrayList<ColumnsInfo> list = cache.getTableColumns();
			for(int i = 0; i < list.size(); i++) {
				ColumnsInfo col = list.get(i);
                String itemid = col.getColumnId();
                ArrayList<ColumnsInfo> childColumn = col.getChildColumns();
				if(!codeitemid.equalsIgnoreCase(itemid) && childColumn.size() > 0) {
					//双层表头的需要判断双层表头
					for(int j = 0; j < childColumn.size(); j++) {
						col = childColumn.get(j);
						itemid = col.getColumnId();
						if(codeitemid.equalsIgnoreCase(itemid)) {
		                	col.setColumnWidth(Integer.parseInt(width));
		                	isIn = true;
		                	break;
		                }
					}
				}else {
	                if(codeitemid.equalsIgnoreCase(itemid)) {
	                	col.setColumnWidth(Integer.parseInt(width));
	                	isIn = true;
	                }
				}
				if(isIn) {
					break;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList getColumnList(String num, ColumnsInfo info, int displayorder, String mergedesc){
    	ArrayList columnList = new ArrayList();
    	try {
			columnList.add(num);//scheme_id
			String columnid = info.getColumnId();
			columnList.add(columnid);//itemid
			columnList.add(displayorder);//displayorder
			columnList.add("");//displaydesc
			columnList.add(info.getLoadtype()==1?"1":"0");//is_display
			columnList.add(info.getColumnWidth());//displaywidth
			int align = 2;
			if("left".equals(info.getTextAlign())) {
				align = 1;
			}else if("right".equals(info.getTextAlign())) {
				align = 3;
			}
			columnList.add(align);//align
			columnList.add(info.getOrdertype());//is_order
			columnList.add("0");//is_sum
			columnList.add("");//itemdesc
			columnList.add(mergedesc);//mergedesc
			columnList.add(info.isLocked()?"1":"0");//is_lock
			if(columnid.indexOf("adde")!=-1||columnid.indexOf("addl")!=-1||columnid.indexOf("before")!=-1
					||columnid.indexOf("year")!=-1) {
				if(columnid.startsWith("avg")) {
					columnid = columnid.substring(3);
				}
				columnid = columnid.substring(0,5);
			}
			FieldItem fieldItem = DataDictionary.getFieldItem(columnid);
			if ("seq".equals(columnid) || "nbase".equals(columnid) || "b0110".equals(columnid)
					|| "e0122".equals(columnid) || "a0101".equals(columnid)) {
				fieldItem = null;
			}
			columnList.add(fieldItem!=null?"1":"0");//is_fromdict
			columnList.add("0");//is_removable
    	}catch (Exception e) {
			e.printStackTrace();
		}
		return columnList;
    }
    /**
	 * 
	 * @param xinz_id 薪资的权限id
	 * @param baox_id 保险的权限id
	 * @param module  0:薪资 1：保险
	 * @return
	 */
	public boolean hasTheFunction(String xinz_id, String baox_id, int module) {
		boolean hasTheFunction = false;
		if(module == 1) {
			hasTheFunction = this.view.hasTheFunction(baox_id);
		}else {
			hasTheFunction = this.view.hasTheFunction(xinz_id);
		}
		return hasTheFunction;
	}
	
	/**
     * 生成功能导航菜单的json串
     *
     * @param name 菜单名
     * @param id   菜单id
     * @param list 菜单功能集合
     * @return
     */
    public static String getMenuStr(String name, String id, ArrayList list) {
        StringBuffer str = new StringBuffer();
        try {
            if (name.length() > 0) {
                str.append("<jsfn>{xtype:'button',text:'" + name + "'");
            }
            if (StringUtils.isNotBlank(id)) {
                str.append(",id:'");
                str.append(id);
                str.append("'");
            }
            str.append(",menu:{items:[");
            for (int i = 0; i < list.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean) list.get(i);
                if (i != 0)
                    str.append(",");
                str.append("{");
                if (bean.get("xtype") != null && bean.get("xtype").toString().length() > 0)
                    str.append("xtype:'" + bean.get("xtype") + "'");
                if (bean.get("text") != null && bean.get("text").toString().length() > 0)
                    str.append("text:'" + bean.get("text") + "'");
                if (bean.get("handler") != null && bean.get("handler").toString().length() > 0) {
                    if (bean.get("xtype") != null && "datepicker".equalsIgnoreCase(bean.get("xtype").toString())) {//时间控件单独处理一下 方法GzGlobal.aaa(picker, date)这样写
                        str.append(",handler:function(picker, date){" + bean.get("handler") + ";}");
                    } else {
                        str.append(",handler:function(){" + bean.get("handler") + ";}");
                    }
                }
                String menuId = (String) bean.get("id");

                if (menuId != null && menuId.length() > 0)//人事异动-手工选择按钮需要id（gaohy）
                    str.append(",id:'" + menuId + "'");
                else
                    menuId = "";
                if (bean.get("icon") != null && bean.get("icon").toString().length() > 0)
                    str.append(",icon:'" + bean.get("icon") + "'");
                if (bean.get("value") != null && bean.get("value").toString().length() > 0)
                    str.append(",value:" + bean.get("value") + "");
                ArrayList menulist = (ArrayList) bean.get("menu");
                if (menulist != null && menulist.size() > 0) {
                    str.append(getMenuStr("", menuId, menulist));
                }
                str.append("}");
            }
            str.append("]}");
            if (name.length() > 0) {
                str.append("}</jsfn>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str.toString();
    }
    
    /**
     * 列拖拽更新栏目设置以及薪资分析中存的指标顺序
     * @param subModuleId
     * @param scheme_id
     * @param itemid
     * @param nextid
     * @param is_lock
     * @param rsdtlid
     * @param stid
     */
    public void saveCloumnMove(HashMap map) {
    	
    	String subModuleId = (String) map.get("subModuleId");
    	String itemid = (String) map.get("itemid"); 
    	String nextid = (String) map.get("nextid");
    	String is_lock = (String) map.get("is_lock");
    	String rsdtlid = (String) map.get("rsdtlid");
    	String rsid = (String) map.get("rsid");
		boolean flagforshare = false;
    	TableFactoryBO tfb = new TableFactoryBO(subModuleId, this.view, this.conn);
    	
    	try {
    		// 如果没有方案，先插入方案
	    	String scheme_id = hasPrivateScheme(subModuleId);
	    	if(StringUtils.isBlank(scheme_id)) {//不存在私有
	    		// 查询共有方案
	    		scheme_id = hasShareScheme(subModuleId);
        		flagforshare = StringUtils.isNotBlank(scheme_id)?true:false;
        		scheme_id = insertScheme(flagforshare, subModuleId);
	    	}
	    	// 调用表格控件的东西，只能更新完之后再查下
	    	// 更新栏目设置中的顺序
	    	tfb.updateColumnMove(Integer.parseInt(scheme_id), itemid, nextid, is_lock);
	    	// 获取排好顺序的指标
	    	HashMap fieldMap = updateColumnSort(subModuleId);
	    	HashMap<String, String> item_map = (HashMap<String, String>) fieldMap.get("item_map");
	    	ArrayList<String> list_column = (ArrayList<String>) fieldMap.get("list_column");
	    	// 循环薪资分析中选择的指标，分别把顺序对应写进去
	    	updateColumnGz(rsdtlid, rsid, item_map);
	    	setDisplayColumnCache(subModuleId, list_column);
    	}catch (Exception e) {
			e.printStackTrace();
		}finally {
			
		}
    }
    
    /**
     * 找到对应的已经排好顺序的指标集合
     * @param subModuleId
     * @return
     */
    private HashMap updateColumnSort(String subModuleId) {
    	boolean flagforprivate = false;
    	boolean flagforshare = false;
    	RowSet rs = null;
    	ContentDAO dao = new ContentDAO(this.conn);
    	ArrayList<String> list = new ArrayList<String>();
    	HashMap result = new HashMap();
    	ArrayList<String> list_column = new ArrayList<String>();
    	HashMap<String, String> map = new HashMap<String, String>();
    	try {
    		String pri_scheme = hasPrivateScheme(subModuleId);
    		// 私有
	    	if(StringUtils.isNotBlank(pri_scheme)) {
	    		String sql = "select itemid,displayorder from t_sys_table_scheme_item where scheme_id = ("
	    				+ Sql_switcher.sqlTop("select scheme_id from t_sys_table_scheme where submoduleid = ? and is_share = 0 and username = ?", 1)
	    				+ ")  order by displayorder";
				list.add(subModuleId);
				list.add(this.view.getUserName());
				rs=dao.search(sql, list);
				while(rs.next()){
		    		String itemid = rs.getString("itemid");
		    		map.put(itemid, rs.getString("displayorder"));
		    		list_column.add(itemid);
		    	}
	    	}else {
	    		String share_scheme = hasShareScheme(subModuleId);
	    		if(StringUtils.isNotBlank(share_scheme)) {
	    			String sql = "select itemid,displayorder from t_sys_table_scheme_item where scheme_id = (" 
	    					+ Sql_switcher.sqlTop("select 1 from t_sys_table_scheme where submoduleid = ? and is_share = 1", 1)
	    					+ ") order by displayorder";
	    			list.add(subModuleId);
					rs=dao.search(sql, list);
			    	while(rs.next()){
			    		String itemid = rs.getString("itemid");
			    		map.put(itemid, rs.getString("displayorder"));
			    		list_column.add(itemid);
			    	}
	    		}
	    	}
	    	result.put("list_column", list_column);
	    	result.put("item_map", map);
    	}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
    	return result;
    }
    
    /**
     * 找到reportitem指标，更新其顺序
     * @param rsdtlid
     * @param stid
     * @param fieldMap
     */
    private void updateColumnGz(String rsdtlid, String stid, HashMap<String, String> fieldMap) {
    	RowSet rs = null;
    	ContentDAO dao = new ContentDAO(this.conn);
    	ArrayList<String> list = new ArrayList<String>();
    	StringBuffer upd_sql = new StringBuffer();
    	StringBuffer sql_update_list = new StringBuffer();
    	ArrayList<ArrayList> listvalue = new ArrayList<ArrayList>();
    	try {
    		String sql = "select itemid from reportitem where RsDtlID = ? and stid = ?"; 
    		list.add(rsdtlid);
    		list.add(stid);
    		rs = dao.search(sql, list);
    		while(rs.next()) {
    			String itemid = rs.getString("itemid");
    			
    			ArrayList list_tem = new ArrayList();
    			list_tem.add((String)fieldMap.get(itemid));
    			list_tem.add(rsdtlid);
    			list_tem.add(stid);
    			list_tem.add(itemid);
				listvalue.add(list_tem);
    		}
    		sql_update_list.append("update reportitem set sortid=? where RsDtlID = ? and stid = ? and itemid = ?");
    		if(listvalue.size()>0)
				dao.batchUpdate(sql_update_list.toString(),listvalue);			
    	}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
    }
    
    /**
	 * 列宽设置到缓存中
	 * @param submoduleid
	 * @param width
	 * @param codeitemid
	 */
	private void setDisplayColumnCache(String subModuleId, ArrayList<String> list_column) {
		boolean isIn = false;
		HashMap map = new HashMap();
		ArrayList<ColumnsInfo> list_displaycolumn = new ArrayList<ColumnsInfo>(); 
		HashMap map_display = new HashMap();
		try {
			TableDataConfigCache cache = (TableDataConfigCache) view.getHm().get(subModuleId);
			ArrayList<ColumnsInfo> list = cache.getTableColumns();
			// 通过正确的顺序和缓存中的顺序比较，注意的是如果有子栏目的得比较
			for(int i = 0; i < list_column.size(); i++) {
				String itemid = list_column.get(i);
				for(int j = 0; j < list.size(); j++) {
					ColumnsInfo col = list.get(j);
					String columnId = col.getColumnId();
					ArrayList childColumn = col.getChildColumns();
					// 针对有子列的特殊处理
					if(childColumn.size() > 0) {
						boolean haveExists = false;
						//如果有子列，判断子列的顺序
						for(int k = 0; k < childColumn.size(); k++) {
							ColumnsInfo col_ = (ColumnsInfo) childColumn.get(k);
							String columnId_ = col_.getColumnId();
							if(itemid.equalsIgnoreCase(columnId_)) {
								Object obj = (map_display == null || map_display.size() == 0)?-1:map_display.get(col.getColumnId());
								int index = obj == null?-1:Integer.parseInt(obj.toString());
								// 如果存在，修改子栏目
								if(index > -1) {
									list_displaycolumn.get(index).addChildColumn(col_);
								}else {
									ColumnsInfo col_clone = col.clone();
									col_clone.setChildColumns(new ArrayList());
									col_clone.addChildColumn(col_);
									map_display.put(col_clone.getColumnId(), list_displaycolumn.size());
									list_displaycolumn.add(col_clone);
								}
								haveExists = true;
								break;
							}
						}
						if(haveExists)
							break;
					}else if(itemid.equalsIgnoreCase(columnId)) {
						list_displaycolumn.add(col);
						break;
					}
				}
			}
			cache.setDisplayColumns(list_displaycolumn);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据是否显示每月人数设置栏目设置中的这列的显示与否
	 * 暂时放在工具类里面，不属于业务，仅是控件之间的操作问题
	 * @param showNumberOfPeople
	 */
	public void setDisplayByShowNumberPerson(boolean showNumberOfPeople) {
		StringBuffer upd_sql = new StringBuffer();
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		int is_display = 0;
		try {
			// 查私有栏目设置
			String pri_scheme = hasPrivateScheme("gzStructure");
			// 如果没有私有，但是有共有，走共有栏目设置
			if(StringUtils.isBlank(pri_scheme)) {
				pri_scheme = hasShareScheme("gzStructure");
			}
			
			if(StringUtils.isNotBlank(pri_scheme)) {
				// 根据是否显示更新栏目设置的显示和隐藏，否则直接走栏目设置导致显示错误
				if(showNumberOfPeople) {
					is_display = 1;
				}
				list.add(is_display);
				list.add(pri_scheme);
				for(int i = 1; i < 13; i++) {
					upd_sql.append(" or itemid = ?");
					list.add("mc_" + i);
				}
				dao.update("update t_sys_table_scheme_item set is_display = ? where  scheme_id = ? and (" + upd_sql.substring(4) + ")", list);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
