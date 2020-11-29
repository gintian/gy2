package com.hjsj.hrms.businessobject.gz.templateset;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SalaryStandardBo {
	private Connection conn=null;
	private String hfactor="";
	private String s_hfactor="";
	private String vfactor="";
	private String s_vfactor="";
	private String item="";
	private String hcontent="";
	private String vcontent="";
	private String standID="";
	private String pkg_id="";
	/**薪资标准是否存在,chenmengqing added*/
	private boolean exist=false;
	/**横纵向指标*/
	private FieldItem hitem;
	private FieldItem s_hitem;
	private FieldItem vitem;
	private FieldItem s_vitem
	/**结果指标*/;
	private FieldItem r_item;
    private UserView userView;
    
    private ArrayList varList=new ArrayList();
    
	public SalaryStandardBo(Connection con)
	{
		this.conn=con;
	}
	
	
	
	public SalaryStandardBo(Connection conn,String standID,String pkg_id)
	{
		this.conn=conn;
		this.pkg_id=pkg_id;
		this.standID=standID.trim();
		getPropertyValue();
		
	}
	
	public SalaryStandardBo(Connection conn,String standID,String pkg_id,ArrayList var_list)
	{
		this.conn=conn;
		this.pkg_id=pkg_id;
		this.standID=standID.trim();
		this.varList=var_list;
		getPropertyValue();
		
	}
	
	/**
	 * 分析标准表是否为两维标准表
	 * @return
	 */
	public boolean isTwoDimension()
	{
		boolean hflag=false,vflag=false;
		if(hfactor.length()>0||s_hfactor.length()>0)
			hflag=true;
		if(vfactor.length()>0||s_vfactor.length()>0)
			vflag=true;
		return (hflag&&vflag);
	}
	/**
	 * 取得横向指标,如果一级指标存在，则按一级，否则按二级
	 * @return
	 */
	public FieldItem getHDimMenu()
	{
		FieldItem item=null;
		if(hfactor.length()>0||s_hfactor.length()>0)
		{
			if(hitem!=null||s_hitem!=null)
			{
				if(hitem!=null)
					item=hitem;
				else
					item=s_hitem;
			}
		}
		return item;
	}	
	/**
	 * 取得纵向指标
	 * @return
	 */
	public FieldItem getVDimMenu()
	{
		FieldItem item=null;
		if(vfactor.length()>0||s_vfactor.length()>0)
		{
			if(vitem!=null||s_vitem!=null)
			{
				if(vitem!=null)
					item=vitem;
				else
					item=s_vitem;
			}
		}
		return item;
	}		
	/**
	 * 公式转换
	 * @param fieldname     指标代号
	 * @param strexpr		表达式    
	 * @param buf			转换后的公式
	 * @return
	 */
	public boolean parseFormula(String fieldname,String strexpr,StringBuffer buf)
	{
		boolean bflag=true;
		try
		{
			FieldItem item=getFieldItem(fieldname);
			ArrayList list=new ArrayList();
			list.add(item);
			YksjParser yp=new YksjParser(new UserView("su",this.conn) ,list,
					YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
			yp.run(strexpr);
			/**单表计算*/
			buf.append(yp.getSQL());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
		}
		return bflag;
	}
	
	public boolean parseFormula(FieldItem item,String strexpr,StringBuffer buf)
	{
		boolean bflag=true;
		try
		{
			ArrayList list=new ArrayList();
			list.add(item);
			YksjParser yp=new YksjParser(new UserView("su",this.conn) ,list,
					YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
			yp.run(strexpr);
			/**单表计算*/
			buf.append(yp.getSQL());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
		}
		return bflag;
	}	
	/**
	 * 取得最大记录数
	 * @param fieldname
	 * @return
	 */
	private int getRows(String fieldname,String value)
	{
		int nmax=0;
		StringBuffer buf=new StringBuffer();
		ArrayList paralist=new ArrayList();
		buf.append("select count(*) as nmax from gz_stand_date where item=?");
		if(value.length()>0){
		buf.append(" and item_id in( ");
		buf.append(value);
		buf.append(")");
		}
		
		paralist.add(fieldname);
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RowSet rset=dao.search(buf.toString(),paralist);
			if(rset.next())
				nmax=rset.getInt("nmax");
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return nmax;
	}
	/**
	 * 拆分标准表项表内容
	 * @param content
	 * for exmple:
	 * 1.    11[];12[];13[];14[12,3,4];15[]
	 * 2.    11;23;45
	 * 3.    [1,2,3]
	 * @return  put("1","1,2,3")
	 * 			put("2","5,6,7");
	 */
	private HashMap  splitStdItemContent(String content)
	{
		HashMap map=new HashMap();
		StringBuffer first=new StringBuffer();
		StringBuffer second=new StringBuffer();
		if(!(content==null||content.length()==0))
		{
			if(content.charAt(0)=='[')
			{
				/**去掉前后[]括号*/
				second.append(content.substring(1, content.length()-1));
			}
			else
			{
				String[] conarr=StringUtils.split(content,";");
				for(int i=0;i<conarr.length;i++)
				{
					String temp=conarr[i];
					int idx=temp.indexOf("[");
					if(idx==-1)
						first.append(temp);
					else
					{
						first.append(temp.substring(0, idx));
						temp=temp.substring(idx);
						second.append(temp.substring(1, temp.length()-1));
					}
					first.append(",");
					second.append(",");
				}//for i loop end.
			}
		}
		String[] tmp=StringUtils.split(first.toString(),",");
		first.setLength(0);
		for(int i=0;i<tmp.length;i++)
		{
			if(first.indexOf(","+tmp[i]+",")==-1)
			{
				first.append(tmp[i]);
				first.append(",");
			}
		}
		tmp=StringUtils.split(second.toString(),",");
		second.setLength(0);
		for(int i=0;i<tmp.length;i++)
		{
			if(second.indexOf(","+tmp[i]+",")==-1)
			{
				second.append(tmp[i]);
				second.append(",");
			}
		}

		map.put("1", first.toString());
		map.put("2", second.toString());
		return map;
	}
	/**
	 * 组成查询串
	 * @param value
	 * @return
	 */
	private String addQuote(String value)
	{
		String[] temp=StringUtils.split(value,",");
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<temp.length;i++)
		{
			buf.append("'");
			buf.append(temp[i]);
			buf.append("'");
			buf.append(",");
		}// for i loop end.
		if(buf.length()>0)
			buf.setLength(buf.length()-1);
		return buf.toString();
	}
	/**
	 * 把计算临时表中指标，根据定义的区域规则，更新到临时字段中去S标准号+"_"+指标代号
	 * @param list
	 * @param tablename 计算表
	 * @throws GeneralException
	 */
	public void updateStdItem(ArrayList list,String tablename,ArrayList stdlist)throws GeneralException
	{
		try
		{
			int idx=0;
			StringBuffer buf=new StringBuffer();
			ArrayList paralist=new ArrayList();
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=null;
			for(int i=0;i<list.size();i++)
			{
				buf.setLength(0);
				paralist.clear();
				/**cmq changed at 20080712*/
				FieldItem fielditem=(FieldItem)list.get(i);
				String fieldname=fielditem.getItemid();
				//String fieldname=(String)list.get(i);
				idx=fieldname.indexOf("_");
				if(idx==-1)
					continue;
				String d_fieldname=fieldname.substring(idx+1);
				FieldItem item=getFieldItem(d_fieldname);
				if(item==null)
					continue;
				String value="";
				
				//20100318  dengcan
				boolean is_h=false;
				String firstItem="";
				String itemtype="";
				if(d_fieldname.equalsIgnoreCase(getS_hfactor())||(this.getS_hitem()!=null&&d_fieldname.equalsIgnoreCase(this.getS_hitem().getItemid())))
				{
					is_h=true;
					if(this.hitem!=null)
					{
						firstItem=this.hfactor;
						itemtype=this.hitem.getItemtype();
					}
				}
				else
				{
					if(this.vitem!=null)
					{
						firstItem=this.vfactor;
						itemtype=this.vitem.getItemtype();
					}
				}
				String[] temps=null;
				if(is_h)
					temps=this.hcontent.split(";");
				else
					temps=this.vcontent.split(";");
				for(int e=0;e<temps.length;e++)
				{
					if(temps[e].trim().length()==0)
						continue;
					temps[e]=temps[e].trim();
					idx=temps[e].indexOf("[");
					
					buf.setLength(0);
					if(d_fieldname.equalsIgnoreCase(getS_hfactor())||(this.getS_hitem()!=null&&d_fieldname.equalsIgnoreCase(this.getS_hitem().getItemid())))
					{
						if(firstItem.length()==0)
						{
							HashMap map=splitStdItemContent(this.hcontent);
							value=(String)map.get("2");
						}
						else
						{
							value=temps[e].substring(idx);
							value=(value.substring(1, value.length()-1));
						}
						value=addQuote(value);
						buf.append("select item_id,lexpr from gz_stand_date where lower(item)=?");
					}
					else
					{
						if(firstItem.length()==0)
						{
							HashMap map=splitStdItemContent(this.vcontent);
							value=(String)map.get("2");
						}
						else
						{
							value=temps[e].substring(idx);
							value=(value.substring(1, value.length()-1));
						}
						value=addQuote(value);
						buf.append("select item_id,lexpr from gz_stand_date where lower(item)=?");
					}
					if(value.length()>0){
					buf.append(" and item_id in (");
					buf.append(value);
					buf.append(")");
					}
					buf.append(" order by item_id");
					paralist.clear();
					paralist.add(d_fieldname.toLowerCase());
					
					rset=dao.search(buf.toString(), paralist); 
					String[][] cases=new String[getRows(d_fieldname.toUpperCase(),value)][2];
					int j=0;
					while(rset.next())
					{
						buf.setLength(0);
						String str_d=Sql_switcher.readMemo(rset, "lexpr").toLowerCase();
						if(d_fieldname.equalsIgnoreCase(getS_hfactor())||(this.getS_hitem()!=null&&d_fieldname.equalsIgnoreCase(this.getS_hitem().getItemid())))
						{
							if(getS_hfactor().equalsIgnoreCase(this.getS_hitem().getItemid()))
							{
								if(fielditem.isChangeAfter()||fielditem.isChangeBefore())
								{
									String rep=d_fieldname+"_"+fielditem.getNChgstate();
									str_d=str_d.replaceAll(d_fieldname, rep);
									fielditem.setItemid(rep);
								}
								else
									fielditem.setItemid(d_fieldname);
							}
							else
							{
								if(item.isFloat()||item.isInt())//数值型为空时默认按0处理 hej 20161220
									str_d=str_d.replaceAll(d_fieldname,Sql_switcher.isnull(getS_hfactor(),"0"));
								else
									str_d=str_d.replaceAll(d_fieldname,getS_hfactor());
								fielditem.setItemid(getS_hfactor());
							}
							
						}
						else
						{
							if(this.getS_vfactor().equalsIgnoreCase(this.getS_vitem().getItemid()))
							{
								if(fielditem.isChangeAfter()||fielditem.isChangeBefore())
								{
									String rep=d_fieldname+"_"+fielditem.getNChgstate();
									str_d=str_d.replaceAll(d_fieldname, rep);
									fielditem.setItemid(rep);
								}
								else
									fielditem.setItemid(d_fieldname);
							
							}
							else
							{
								if(item.isFloat()||item.isInt())//数值型为空时默认按0处理 hej 20161220
									str_d=str_d.replaceAll(d_fieldname,Sql_switcher.isnull(this.getS_vfactor(),"0"));
								else
									str_d=str_d.replaceAll(d_fieldname,this.getS_vfactor());
								fielditem.setItemid(this.getS_vfactor());
							}
							
						}
						
						
						
						String str_i=rset.getString("item_id");
						/**数值*/
						if(item.isFloat()||item.isInt())
						{
							cases[j][0]=str_d;
						}
						else//日期
						{
							if(parseFormula(fielditem,str_d,buf))
							{
								cases[j][0]=buf.toString();
							}
							else
							{
								cases[j][0]=str_d;
							}
						}
						cases[j][1]=str_i;
						++j;
					}//for while end.
					String strupdate=Sql_switcher.sql_Case("", "", cases);
					buf.setLength(0);
					buf.append("update ");
					buf.append(tablename);
					buf.append(" set ");
					buf.append(fieldname);
					buf.append("=");
					buf.append(strupdate);
					
					if(firstItem.length()>0)
					{
						value=temps[e].substring(0,idx);
						if("A".equalsIgnoreCase(itemtype))
							buf.append(" where "+firstItem+"='"+value+"'" );
						else if("N".equalsIgnoreCase(itemtype))
							buf.append(" where "+firstItem+"="+value );
					}
					
					
					stdlist.add(buf.toString());
				
				}
				//dao.update(buf.toString());
			}//for i loop end.
			if(rset!=null)
				rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	public void updateStdItem(ArrayList list,String tablename)throws GeneralException
	{
		try
		{
			int idx=0;
			StringBuffer buf=new StringBuffer();
			ArrayList paralist=new ArrayList();
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=null;
			for(int i=0;i<list.size();i++)
			{
				buf.setLength(0);
				paralist.clear();
				/**cmq changed at 20080712*/
				FieldItem fielditem=(FieldItem)list.get(i);
				String fieldname=fielditem.getItemid();
				//String fieldname=(String)list.get(i);
				idx=fieldname.indexOf("_");
				if(idx==-1)
					continue;
				String d_fieldname=fieldname.substring(idx+1);
				FieldItem item=getFieldItem(d_fieldname);
				if(item==null)
					continue;
				String value="";
				if(d_fieldname.equalsIgnoreCase(getS_hfactor()))
				{
					HashMap map=splitStdItemContent(this.hcontent);
					value=(String)map.get("2");
					value=addQuote(value);
					buf.append("select item_id,lexpr from gz_stand_date where lower(item)=?");
				}
				else
				{
					HashMap map=splitStdItemContent(this.vcontent);
					value=(String)map.get("2");
					value=addQuote(value);
					buf.append("select item_id,lexpr from gz_stand_date where lower(item)=?");
				}
				buf.append(" and item_id in (");
				buf.append(value);
				buf.append(")");
				buf.append(" order by item_id");
				paralist.add(d_fieldname.toLowerCase());
				
				rset=dao.search(buf.toString(), paralist);

				String[][] cases=new String[getRows(d_fieldname.toUpperCase(),value)][2];
				int j=0;
				while(rset.next())
				{
					buf.setLength(0);
					String str_d=Sql_switcher.readMemo(rset, "lexpr").toLowerCase();
					if(fielditem.isChangeAfter()||fielditem.isChangeBefore())
					{
						String rep=d_fieldname+"_"+fielditem.getNChgstate();
						str_d=str_d.replaceAll(d_fieldname, rep);
					}
					String str_i=rset.getString("item_id");
					/**数值*/
					if(item.isFloat()||item.isInt())
					{
						cases[j][0]=str_d;
					}
					else//日期
					{
						//因为“年份”，“月份”在算法分析器中不识别，换成“年”，“月”就行，因为是从gz_stand_date表中取得，是区间值，不能自己输入年份月份这种，所以这里直接替换就行
						str_d = str_d.replace("年份","年").replace("月份","月");
						
						if(parseFormula(d_fieldname,str_d,buf))
						{
							cases[j][0]=buf.toString();
						}
						else
						{
							cases[j][0]=str_d;
						}
					}
					cases[j][1]=str_i;
					++j;
				}//for while end.
				if(j>0)
				{
					String strupdate=Sql_switcher.sql_Case("", "", cases);
					buf.setLength(0);
					buf.append("update ");
					buf.append(tablename);
					buf.append(" set ");
					buf.append(fieldname);
					buf.append("=");
					buf.append(strupdate);
					dao.update(buf.toString());
				}
			}//for i loop end.
			if(rset!=null)
				rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}	
	/**
	 * 把横纵指标为日期型或数值型的指标，加到计算用的临时表中去。
	 * 字段名的命名规则，S+标准号+"_"+指标代号
	 * @param tablename
	 * @return 返回值为新建字段列表
	 */
	public ArrayList addStdItemIntoTable(String tablename)
	{
		boolean bflag=false;
		ArrayList fieldlist=new ArrayList();
		ArrayList list=new ArrayList();
		try
		{
			Table table=new Table(tablename);
			DbWizard dbw=new DbWizard(this.conn);
			RecordVo vo=new RecordVo(tablename);
			fieldlist.addAll(this.getGzStandFactorList(1));
			fieldlist.addAll(this.getGzStandFactorList(2));
			String fieldname="";
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem item=(FieldItem)fieldlist.get(i);
				fieldname=item.getItemid();
				if(!vo.hasAttribute(fieldname.toLowerCase()))
				{			
					bflag=true;						
					table.addField(item.cloneField());
				}				
				/**数值型和日期型*/
				if(item.isFloat()||item.isDate()||item.isInt())
				{
					fieldname="S"+this.standID+"_"+item.getItemid();
					if(!vo.hasAttribute(fieldname.toLowerCase()))
					{
						bflag=true;
						Field field=new Field(fieldname,fieldname);
						field.setDatatype(DataType.INT);
						field.setLength(10);
						table.addField(field);
					}	
					item.setItemid(fieldname);							
					//list.add(fieldname);
				}
				//else
				//{
//					fieldname=item.getItemid();
//					if(!vo.hasAttribute(fieldname.toLowerCase()))
//					{			
//						bflag=true;						
//						table.addField(item.cloneField());
//					}
				//}
		
				list.add(item);				
			}//for i loop end.
			if(bflag)
			{
				dbw.addColumns(table);
				DBMetaModel dbmodel=new DBMetaModel(this.conn);
				dbmodel.reloadTableModel(tablename);						
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
		}
		return list;
	}
	
	String nbase="";
	/*
	 * 
	 */
	public ArrayList addStdItemIntoTable(String tablename,ArrayList stdlist)
	{
		ArrayList fieldlist=new ArrayList();
		ArrayList list=new ArrayList();
		try
		{
			//RecordVo vo=new RecordVo(tablename);
			fieldlist.addAll(this.getGzStandFactorList(1));
			fieldlist.addAll(this.getGzStandFactorList(2));
			String fieldname="";
			StringBuffer strR=new StringBuffer();
			StringBuffer strSQL=new StringBuffer();
			HashMap map=new HashMap();
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem item=(FieldItem)fieldlist.get(i);
				/**数值型和日期型*/
				if(item.isFloat()||item.isDate()||item.isInt())
				{
					strSQL.setLength(0);
					strR.setLength(0);
					fieldname="S"+this.standID+"_"+item.getItemid();
					//if(!vo.hasAttribute(fieldname.toLowerCase()))
					//{
						strSQL.setLength(0);
		        		strSQL.append(" alter table ");
		    			strSQL.append(tablename);
		    			strSQL.append(" drop column ");			
		    			strSQL.append(fieldname);
		    			stdlist.add(strSQL.toString());
		    			
						strR.append(fieldname);
			        	strR.append(" ");
			        	strR.append(Sql_switcher.getFieldType('N',10,0));
		    			strSQL.setLength(0);    		
		    			strSQL.append("alter table ");
		    			strSQL.append(tablename);
		    			strSQL.append(" ADD ");
		    			strSQL.append(strR.toString());
		    			stdlist.add(strSQL.toString());
		    		/*
		    			if(tablename.toUpperCase().indexOf("TEMP_")!=-1&&map.get(item.getItemid())==null) //工资变动涉及到的 工资标准
		    			{
		    				strSQL.setLength(0);
			        		strSQL.append(" alter table ");
			    			strSQL.append(tablename);
			    			strSQL.append(" drop column ");			
			    			strSQL.append(item.getItemid());
			    			stdlist.add(strSQL.toString());
			    			
			    			String sAdd=item.getItemid()+" "+Sql_switcher.getFieldType(item.getItemtype().charAt(0),item.getItemlength(),item.getDecimalwidth());
							strSQL.setLength(0);    		
			    			strSQL.append("alter table ");
			    			strSQL.append(tablename);
			    			strSQL.append(" ADD ");
			    			strSQL.append(sAdd);
			    			stdlist.add(strSQL.toString());
			    		
			    			map.put(item.getItemid(),"1");
		    			}
		    		
		    			*/
		    			
		    			
					//}	
					item.setItemid(fieldname);							
				}
				list.add(item);				
			}//for i loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}

	public String getStandardJoinOn(String gzname)
	{
		return getStandardJoinOn(gzname,0);
	}
	/**
	 * 取得计算用表和薪资标准表的关联串
	 * @param gzname
	 * @param flag =0不带后涉也即变化后标识 =2|1变化后或前 ，在指标后面加 "_1|2",变化前或后
	 * @return
	 */
	public String getStandardJoinOn(String gzname,int flag)
	{
		StringBuffer buf=new StringBuffer();
		String fieldname=null;
		FieldItem item=null;
		buf.append("gz_item.id=");
		buf.append(this.standID);
		if(this.hfactor.length()>0&&this.s_hfactor.length()>0)
		{
			if(this.hfactor.length()>=5)
			{
				if("yk".equalsIgnoreCase(this.hfactor.substring(0,2)))  //20141125 dengcan
					item=getFieldItem(this.hfactor);
				else
					item=getFieldItem(this.hfactor.substring(0, 5));
			}
			else
				item=getFieldItem(this.hfactor);
			if(item==null)
				item=getVarItem(this.hfactor);
			if((item.isDate()||item.isFloat()||item.isInt())&&item.getVarible()!=1)
				fieldname="S"+this.standID+"_"+item.getItemid();
			else
			{
				//if(this.hfactor.length()>=5)
				//	fieldname=this.hfactor.substring(0, 5);
				//else
				fieldname=this.hfactor;
				//if(flag!=0&&item.getVarible()==0)//变量
				//	fieldname=fieldname+"_"+flag;
			}
			buf.append(" and ");
			buf.append(gzname);
			buf.append(".");
			buf.append(fieldname);
			buf.append("=gz_item.hvalue and (gz_item.s_hvalue is null or ");
			if(this.s_hfactor.length()>=5)			
			{
				if("yk".equalsIgnoreCase(this.s_hfactor.substring(0,2)))  //20141125 dengcan
					item=getFieldItem(this.s_hfactor);
				else
					item=getFieldItem(this.s_hfactor.substring(0, 5));
			}
			else
				item=getFieldItem(this.s_hfactor);
			if((item.isDate()||item.isFloat()||item.isInt())&&item.getVarible()!=1)
			{
				//fieldname="S"+this.standID+"_"+item.getItemid();
				if(item.getItemid().equalsIgnoreCase(this.s_hitem.getItemid()))
					fieldname="S"+this.standID+"_"+item.getItemid();
				else
					fieldname="S"+this.standID+"_"+this.s_hitem.getItemid();
			}
			else
			{
				//if(this.s_hfactor.length()>=5)	
				//	fieldname=this.s_hfactor.substring(0, 5);
				//else
				if(item.getVarible()==1)
				{
					if(item.isDate()||item.isFloat()||item.isInt())  //20141125 dengcan
						fieldname="S"+this.standID+"_"+this.s_hitem.getItemid();
					else
						fieldname=this.s_hfactor;
				}
				else
					fieldname=this.s_hfactor;
				//if(flag!=0&&item.getVarible()==0)
				//	fieldname=fieldname+"_"+flag;				
			}
			buf.append(gzname);
			buf.append(".");
			buf.append(fieldname);
			buf.append("=gz_item.s_hvalue)");
		}
		else
		{
			if(this.hfactor.length()>0)
			{
				if(this.hfactor.length()>=5)				
				{
					if("yk".equalsIgnoreCase(this.hfactor.substring(0,2)))  //20141125 dengcan
						item=getFieldItem(this.hfactor);
					else
						item=getFieldItem(this.hfactor.substring(0, 5));
				}
				else
					item=getFieldItem(this.hfactor);
				if(item==null)
					item=getVarItem(this.hfactor);
				if((item.isDate()||item.isFloat()||item.isInt())&&item.getVarible()!=1)
					fieldname="S"+this.standID+"_"+item.getItemid();
				else
				{
					//if(this.hfactor.length()>=5)
					//	fieldname=this.hfactor.substring(0, 5);
					//else
					fieldname=this.hfactor;
					//if(flag!=0&&item.getVarible()==0)
					//	fieldname=fieldname+"_"+flag;					
				}
				
				buf.append(" and ");
				buf.append(gzname);
				buf.append(".");
				buf.append(fieldname);
				buf.append("=gz_item.hvalue");
			}
			if(this.s_hfactor.length()>0)
			{
				if(this.s_hfactor.length()>=5)					
				{
					if("yk".equalsIgnoreCase(this.s_hfactor.substring(0,2)))   //20141125 dengcan
						item=getFieldItem(this.s_hfactor);
					else
						item=getFieldItem(this.s_hfactor.substring(0, 5));
				}
				else
					item=getFieldItem(this.s_hfactor);
				if(item==null)
					item=getVarItem(this.s_hfactor);
				if((item.isDate()||item.isFloat()||item.isInt())&&item.getVarible()!=1)
				{
					if(item.getItemid().equalsIgnoreCase(this.s_hitem.getItemid()))
						fieldname="S"+this.standID+"_"+item.getItemid();
					else
						fieldname="S"+this.standID+"_"+this.s_hitem.getItemid();
				}
				else
				{
					//if(this.s_hfactor.length()>=5)	
					//	fieldname=this.s_hfactor.substring(0, 5);
					//else
						if(item.getVarible()==1)
						{
							if(item.isDate()||item.isFloat()||item.isInt())
								fieldname="S"+this.standID+"_"+this.s_hitem.getItemid();
							else
								fieldname=this.s_hfactor;
						}
						else
							fieldname=this.s_hfactor;
					//if(flag!=0&&item.getVarible()==0)
					//	fieldname=fieldname+"_"+flag;					
				}
				buf.append(" and ");
				buf.append(gzname);
				buf.append(".");
				buf.append(fieldname);
				buf.append("=gz_item.s_hvalue");
			}
		}
		
		if(this.vfactor.length()>0&&this.s_vfactor.length()>0)
		{
			if(this.vfactor.length()>=5)
			{
				if("yk".equalsIgnoreCase(this.vfactor.substring(0,2)))   //20141125 dengcan
					item=getFieldItem(this.vfactor);
				else
					item=getFieldItem(this.vfactor.substring(0, 5));
			}
			else
				item=getFieldItem(this.vfactor);
			if(item==null)
				item=getVarItem(this.vfactor);
			if((item.isDate()||item.isFloat()||item.isInt())&&item.getVarible()!=1)
				fieldname="S"+this.standID+"_"+item.getItemid();
			else
			{
				//if(this.vfactor.length()>=5)				
				//	fieldname=this.vfactor.substring(0, 5);
				//else
				fieldname=this.vfactor;
				//if(flag!=0&&item.getVarible()==0)
				//	fieldname=fieldname+"_"+flag;				
			}
			buf.append(" and ");
			buf.append(gzname);
			buf.append(".");
			buf.append(fieldname);
			buf.append("=gz_item.vvalue and (gz_item.s_vvalue is null or ");
			if(this.s_vfactor.length()>=5)	
			{
				if("yk".equalsIgnoreCase(this.s_vfactor.substring(0,2)))   //20141125 dengcan
					item=getFieldItem(this.s_vfactor);
				else
					item=getFieldItem(this.s_vfactor.substring(0, 5));
			}
			else
				item=getFieldItem(this.s_vfactor);
			if(item==null)
				item=getVarItem(this.s_vfactor);
			if((item.isDate()||item.isFloat()||item.isInt())&&item.getVarible()!=1)
			{
				//fieldname="S"+this.standID+"_"+item.getItemid();
				if(item.getItemid().equalsIgnoreCase(this.s_vitem.getItemid()))
					fieldname="S"+this.standID+"_"+item.getItemid();
				else
					fieldname="S"+this.standID+"_"+this.s_vitem.getItemid();
			}
			else
			{
				//if(this.s_vfactor.length()>=5)	
				//	fieldname=this.s_vfactor.substring(0, 5);
				//else
				if(item.getVarible()==1)
				{
					if(item.isDate()||item.isFloat()||item.isInt())  //20141125 dengcan
						fieldname="S"+this.standID+"_"+this.s_vitem.getItemid();
					else
						fieldname=this.s_vfactor;
				}
				else
					fieldname=this.s_vfactor;
				//if(flag!=0&&item.getVarible()==0)
				//	fieldname=fieldname+"_"+flag;				
			}
			buf.append(gzname);
			buf.append(".");
			buf.append(fieldname);
			buf.append("=gz_item.s_vvalue)");
		}
		else
		{
			if(this.vfactor.length()>0)
			{
				if(this.vfactor.length()>=5)				
				{
					if("yk".equalsIgnoreCase(this.vfactor.substring(0,2)))   //20141125 dengcan
						item=getFieldItem(this.vfactor);
					else
						item=getFieldItem(this.vfactor.substring(0, 5));
				}
				else
					item=getFieldItem(this.vfactor);
				if(item==null)
					item=getVarItem(this.vfactor);
				if((item.isDate()||item.isFloat()||item.isInt())&&item.getVarible()!=1)
					fieldname="S"+this.standID+"_"+item.getItemid();
				else
				{
					//if(this.vfactor.length()>=5)		
					//	fieldname=this.vfactor.substring(0, 5);
					//else
					fieldname=this.vfactor;
					//if(flag!=0&&item.getVarible()==0)
					//	fieldname=fieldname+"_"+flag;					
				}
				buf.append(" and ");
				buf.append(gzname);
				buf.append(".");
				buf.append(fieldname);
				buf.append("=gz_item.vvalue");
			}
			if(this.s_vfactor.length()>0)
			{
				if(this.s_vfactor.length()>=5)
				{
					if("yk".equalsIgnoreCase(this.s_vfactor.substring(0,2)))   //20141125 dengcan
						item=getFieldItem(this.s_vfactor);
					else
						item=getFieldItem(this.s_vfactor.substring(0, 5));
				}
				else
					item=getFieldItem(this.s_vfactor);
				if(item==null)
					item=getVarItem(this.s_vfactor);
				if((item.isDate()||item.isFloat()||item.isInt())&&item.getVarible()!=1)
				{
					//fieldname="S"+this.standID+"_"+item.getItemid();
					if(item.getItemid().equalsIgnoreCase(this.s_vitem.getItemid()))
						fieldname="S"+this.standID+"_"+item.getItemid();
					else
						fieldname="S"+this.standID+"_"+this.s_vitem.getItemid();
				}
				else
				{
					
					if(item.getVarible()==1)
					{
						if(item.isDate()||item.isFloat()||item.isInt())  //20141125 dengcan
							fieldname="S"+this.standID+"_"+this.s_vitem.getItemid();
						else
							fieldname=this.s_vfactor;
					}
					else
						fieldname=this.s_vfactor;
								
				}
				buf.append(" and ");
				buf.append(gzname);
				buf.append(".");
				buf.append(fieldname);
				buf.append("=gz_item.s_vvalue");
			}
		}
		
		return buf.toString();
	}

	/**
	 * 检查横纵指标是否构库或是否其中之一是否定义了
	 * @param buf 返回的错误提示信息
	 * @return
	 */
	public boolean checkHVField(StringBuffer buf)
	{
		boolean bflag=true;
		FieldItem item=null;
		if(buf==null)
			buf=new StringBuffer();
		if("".equalsIgnoreCase(this.hfactor)&& "".equalsIgnoreCase(this.s_hfactor)&&
		   "".equalsIgnoreCase(this.vfactor)&& "".equalsIgnoreCase(this.s_vfactor))
		{
			buf.append(ResourceFactory.getProperty("error.gz.stand"));
			return false;
		}
		if(this.hfactor.length()>0)
		{
			item=getFieldItem(this.hfactor);
			if(item==null)
			{
				buf.append(ResourceFactory.getProperty("label.gz.hitem"));
				buf.append("[");
				buf.append(this.hfactor);
				buf.append("]");
				buf.append(ResourceFactory.getProperty("error.gz.notstanditem"));
			}
			else
			{
				if("0".equalsIgnoreCase(item.getUseflag()))
				{
					buf.append(ResourceFactory.getProperty("label.gz.hitem"));
					buf.append("[");
					buf.append(this.hfactor);
					buf.append("]");
					buf.append(ResourceFactory.getProperty("error.gz.item"));					
				}
				else
					bflag=true;					
			}
		}
		if(this.s_hfactor.length()>0)
		{
			item=getFieldItem(this.hfactor);
			if(item==null)
			{
				buf.append(ResourceFactory.getProperty("label.gz.hitem"));
				buf.append("[");
				buf.append(this.s_hfactor);
				buf.append("]");
				buf.append(ResourceFactory.getProperty("error.gz.notstanditem"));
			}
			else
			{
				if("0".equalsIgnoreCase(item.getUseflag()))
				{
					buf.append(ResourceFactory.getProperty("label.gz.chitem"));
					buf.append("[");
					buf.append(this.s_hfactor);
					buf.append("]");
					buf.append(ResourceFactory.getProperty("error.gz.item"));					
				}
				else
					bflag=true;						
			}
		}	
		if(this.vfactor.length()>0)
		{
			item=getFieldItem(this.hfactor);
			if(item==null)
			{
				buf.append(ResourceFactory.getProperty("label.gz.vitem"));
				buf.append("[");
				buf.append(this.vfactor);
				buf.append("]");
				buf.append(ResourceFactory.getProperty("error.gz.notstanditem"));
			}
			else
			{
				if("0".equalsIgnoreCase(item.getUseflag()))
				{
					buf.append(ResourceFactory.getProperty("label.gz.vitem"));
					buf.append("[");
					buf.append(this.vfactor);
					buf.append("]");
					buf.append(ResourceFactory.getProperty("error.gz.item"));					
				}
				else
					bflag=true;						
			}
		}
		if(this.s_vfactor.length()>0)
		{
			item=getFieldItem(this.hfactor);
			if(item==null)
			{
				buf.append(ResourceFactory.getProperty("label.gz.cvitem"));
				buf.append("[");
				buf.append(this.s_vfactor);
				buf.append("]");
				buf.append(ResourceFactory.getProperty("error.gz.notstanditem"));
			}
			else
			{
				if("0".equalsIgnoreCase(item.getUseflag()))
				{
					buf.append(ResourceFactory.getProperty("label.gz.cvitem"));
					buf.append("[");
					buf.append(this.s_vfactor);
					buf.append("]");
					buf.append(ResourceFactory.getProperty("error.gz.item"));					
				}
				else
					bflag=true;						
			}
		}			
		return bflag;
	}

	
	private FieldItem getFieldItem(String str)
	{
		FieldItem a_item=null; 
		a_item=DataDictionary.getFieldItem(str);
		if(a_item==null&&this.varList.size()>0)
		{
			FieldItem _item=null;
			for(int i=0;i<this.varList.size();i++)
			{
				_item=(FieldItem)this.varList.get(i);
				if(_item.getItemdesc().equalsIgnoreCase(str)||_item.getItemid().equalsIgnoreCase(str))
				{
					a_item=_item;
					break;
				}
				
			} 
		}
		return a_item;
	}
	
	public void getPropertyValue()
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String strsql ="";
			if(this.pkg_id!=null&&this.pkg_id.length()>0)   //取薪资标准表历史沿革 数据
				strsql="select * from gz_stand_history where pkg_id="+this.pkg_id+" and id="+this.standID;
			else											//取薪资标准表 数据
				strsql="select * from gz_stand where  id="+this.standID;
			RowSet rowSet=dao.search(strsql);
			if(rowSet.next())
			{
				this.hfactor=rowSet.getString("hfactor")!=null?rowSet.getString("hfactor"):"";
				this.s_hfactor=rowSet.getString("s_hfactor")!=null?rowSet.getString("s_hfactor"):"";
				this.hcontent=rowSet.getString("hcontent")!=null?rowSet.getString("hcontent"):"";
				this.vfactor=rowSet.getString("vfactor")!=null?rowSet.getString("vfactor"):"";
				this.s_vfactor=rowSet.getString("s_vfactor")!=null?rowSet.getString("s_vfactor"):"";
				this.vcontent=rowSet.getString("vcontent")!=null?rowSet.getString("vcontent"):"";
				this.item=rowSet.getString("item")!=null?rowSet.getString("item"):"";
				/**chenmengqing added*/
				this.exist=true;
				hitem=getFieldItem(this.hfactor);
				 /**用这个字段来标识横纵指标标识（横一｜二，纵一｜二）*/
				if(hitem!=null)
				{
					hitem=(FieldItem)hitem.cloneItem();
					hitem.setState("1"); 
				}
			 
				
				
				s_hitem=getFieldItem(this.s_hfactor);
				if(s_hitem!=null)
				{
					s_hitem=(FieldItem)s_hitem.cloneItem();
					s_hitem.setState("2");
				}	
				 
				
				vitem=getFieldItem(this.vfactor);
				if(vitem!=null)
				{
					vitem=(FieldItem)vitem.cloneItem();
					vitem.setState("3");
				}
				 
				
				
				s_vitem=getFieldItem(this.s_vfactor);
				if(s_vitem!=null)
				{
					s_vitem=(FieldItem)s_vitem.cloneItem();
					s_vitem.setState("4");
				}	
			 
				
				
				r_item=getFieldItem(this.item);
				if(r_item!=null)
				{
					r_item=(FieldItem)r_item.cloneItem();
					r_item.setState("5");
				}	
				 				
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 取得工资标准表（横纵向）指标列表
	 * @param flag 1:横向指标  2纵向指标
	 * @return ArrayList(FieldItem)
	 */
	public ArrayList getGzStandFactorList(int flag)throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer buf=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.conn);
		String standname = "";
		/**薪资标准表*/
		buf.append("select name from gz_stand where id ='");
		buf.append(this.standID);
		buf.append("'");
		try {
			RowSet rset=dao.search(buf.toString());
			if(rset.next()){
				standname = rset.getString("name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		if(flag==1)
		{
			if(this.hfactor!=null&&this.hfactor.length()>0)
			{
				if(this.hitem!=null)
				{
					if("0".equals(this.hitem.getUseflag())){
						throw new GeneralException(standname+"中横向指标  ［"+this.hitem.getItemdesc()+"］  未构库!");
					}
					FieldItem temp=(FieldItem)hitem.cloneItem();
					list.add(temp);						
				}
				
//				fieldItem=getFieldItem(this.hfactor.toLowerCase());
//				if(fieldItem!=null)
//				{
//					FieldItem temp=(FieldItem)fieldItem.cloneItem();
//					list.add(temp);				
//				}					
			}
			if(this.s_hfactor!=null&&this.s_hfactor.length()>0)
			{
				if(this.s_hitem!=null)
				{
					if("0".equals(this.s_hitem.getUseflag())){
						throw new GeneralException(standname+"中横向指标  ［"+this.s_hitem.getItemdesc()+"］  未构库!");
					}
					FieldItem temp=(FieldItem)s_hitem.cloneItem();
					list.add(temp);						
				}				
/*				fieldItem=getFieldItem(this.s_hfactor.toLowerCase());
				if(fieldItem!=null)
				{
					FieldItem temp=(FieldItem)fieldItem.cloneItem();
					list.add(temp);				
				}	*/				
			}

		}
		else if(flag==2)
		{
			if(this.vfactor!=null&&this.vfactor.length()>0)
			{
				if(this.vitem!=null)
				{
					if("0".equals(this.vitem.getUseflag())){
						throw new GeneralException(standname+"中纵向指标  ［"+this.vitem.getItemdesc()+"］  未构库!");
					}
					FieldItem temp=(FieldItem)vitem.cloneItem();
					list.add(temp);						
				}
//				fieldItem=getFieldItem(this.vfactor.toLowerCase());
//				if(fieldItem!=null)
//				{
//					FieldItem temp=(FieldItem)fieldItem.cloneItem();
//					list.add(temp);				
//				}					
			}
			if(this.s_vfactor!=null&&this.s_vfactor.length()>0)
			{
				if(this.s_vitem!=null)
				{
					if("0".equals(this.s_vitem.getUseflag())){
						throw new GeneralException(standname+"中纵向指标  ［"+this.hitem.getItemdesc()+"］  未构库!");
					}
					FieldItem temp=(FieldItem)s_vitem.cloneItem();
					list.add(temp);						
				}				
				
/*				fieldItem=getFieldItem(this.s_vfactor.toLowerCase());
				if(fieldItem!=null)
				{
					FieldItem temp=(FieldItem)fieldItem.cloneItem();
					list.add(temp);				
				}	*/				
			}
		}
	
		return list;
	}
	
	
	
	
	
	
	
	
	/**
	 * 取得工资标准列表
	 * @param pkg_id
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getSalaryStandardList(String pkg_id)throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			StringBuffer selectedID=new StringBuffer("");
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer("");
			buf.append("select ").append(Sql_switcher.isnull("b0110", "''")).append(" as b0110 ,id,name,hfactor,s_hfactor,vfactor,s_vfactor,item,createorg from gz_stand_history where pkg_id=").append(pkg_id);
			String unitid = "XXXX";
			StringBuffer tt = new StringBuffer();
			//zxj 工资标准不区分标准版、专业版，注释掉此部分代码：||this.userView.getVersion_flag()==0
			if(this.userView.isSuper_admin())
			{
				unitid="UN";
				tt.append(" or 1=1 ");
			}
			else
			{
				if(this.userView.getUnit_id()!=null&&this.userView.getUnit_id().trim().length()>2)
				{
					if(this.userView.getUnit_id().length()==3)
					{
						unitid="UN";
						tt.append(" or 1=1 ");
					}
					else
					{
				    	unitid=this.userView.getUnit_id();
				    	String[] unit_arr = unitid.split("`");
				    	for(int i=0;i<unit_arr.length;i++)
				    	{
				    		if(unit_arr[i]==null|| "".equals(unit_arr[i])||unit_arr[i].length()<2)
				    			continue;
				    		tt.append(" or b0110 like '%,"+unit_arr[i].substring(2)+"%' ");
				    	}
					}
				}
				else{
					if(this.userView.getManagePrivCode()!=null&&this.userView.getManagePrivCode().trim().length()>0)
					{
						if(this.userView.getManagePrivCodeValue()==null|| "".equals(this.userView.getManagePrivCodeValue().trim()))
						{
							unitid="UN";
							tt.append(" or 1=1 ");
						}
						else{
					    	unitid=this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
					    	tt.append(" or b0110 like '%,"+this.userView.getManagePrivCodeValue()+"%'");
						}
					}
					else//没有范围
					{
						
					}
				}
			}
			if(tt.toString().length()>0)
			{
				if(this.userView.isSuper_admin()|| "UN".equals(unitid))
				{
					
				}else
				{
					buf.append(" and (");
					buf.append("("+tt.toString().substring(3)+")");
					buf.append(" or "+Sql_switcher.isnull("b0110", "'E'")+"='E'");
					buf.append(")");
				}
			}
			if("XXXX".equals(unitid))
			{
				buf.append(" and "+Sql_switcher.isnull("b0110", "'E'")+"='E'");
			}
			buf.append(" order by id ");
			RowSet rowSet=dao.search(buf.toString());
			String[] unit_arr = unitid.split("`");
			while(rowSet.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				String b0110 = rowSet.getString("b0110")==null?"":rowSet.getString("b0110").toUpperCase();//归属单位：判断是否可以查看，为空可以
				String createorg = rowSet.getString("createorg")==null?"":rowSet.getString("createorg").toUpperCase();//创建单位：判断是否可以编辑和删除，为空可以
				String gsunit = "";//归属单位
				String allUnit="";
				StringBuffer b0110_Sp=new StringBuffer();
				if(b0110!=null&&!"".equals(b0110.trim()))
				{
					
			    	{
				    	String[] temp_arr=b0110.split(",");
			    		StringBuffer tem=new StringBuffer("");
			    		int j=0;
			    		for(int i=0;i<temp_arr.length;i++)
			    		{   					    		
			    			if(temp_arr[i]==null|| "".equals(temp_arr[i]))
			    				continue;
			    			String desc = "";
			    			if(AdminCode.getCodeName("UN",temp_arr[i])!=null&&!"".equals(AdminCode.getCodeName("UN",temp_arr[i])))
			    			{
			    				desc = AdminCode.getCodeName("UN",temp_arr[i]);
								b0110_Sp.append(",UN").append(temp_arr[i]);
			    			}
			    			else if(AdminCode.getCodeName("UM",temp_arr[i])!=null&&!"".equals(AdminCode.getCodeName("UM",temp_arr[i])))
			    			{
			    				desc = AdminCode.getCodeName("UM",temp_arr[i]);
								b0110_Sp.append(",UM").append(temp_arr[i]);
			    			}
			    			if(j<2)
				    		{
			    				gsunit+=desc+",";
				    		}
				    		tem.append(desc+",");
				    		j++;
			    		}
			    		if(gsunit.length()>0){
			    			gsunit = gsunit.substring(0,gsunit.length()-1);
			    			if(j>2)
			    				gsunit+="...";
			    		}
			    		if(tem.toString().length()>0)
			    		{
				    		tem.setLength(tem.length()-1);
				    		allUnit = tem.toString();
				    	}
			    	}
				}else{
					gsunit=ResourceFactory.getProperty("hire.jp.pos.all");
			    	allUnit=ResourceFactory.getProperty("hire.jp.pos.all");	
				}
				String iseditable="0";//等于1可编辑和删除，=0只能查看
				
				if(this.userView.isSuper_admin()||this.userView.getVersion_flag()==0)
				{
					iseditable="1";
				}
				else if("".equals(createorg))
				{
					if(this.userView.isSuper_admin())
				    	iseditable="1";
				}else if(!"XXXX".equalsIgnoreCase(unitid))
				{
			    	if("UN".equalsIgnoreCase(unitid)&& "UN".equalsIgnoreCase(createorg))
			    		iseditable="1";
			    	else 
			    	{
			    		boolean flag = false;
					    for(int i=0;i<unit_arr.length;i++)
				    	{
					    	if(unit_arr[i]==null|| "".equals(unit_arr[i])||unit_arr[i].length()<2)
					    		continue;
					    	if(createorg.equalsIgnoreCase(unit_arr[i].substring(2)))
					    	{
					    		flag=true; 
					    		break;
					    	}		
					    }
				    	if(flag)
					    	iseditable="1";
					    else
					    	iseditable="0";
			    	}
				}
				String id=rowSet.getString("id");
				abean.set("gsunit",gsunit);
				abean.set("allunit", allUnit);
				abean.set("iseditable", iseditable);
				abean.set("id",id);
				abean.set("name",rowSet.getString("name")==null?"":rowSet.getString("name"));
				abean.set("pkg_id", pkg_id);
				String hfactor=rowSet.getString("hfactor")!=null?rowSet.getString("hfactor"):"";
				String s_hfactor=rowSet.getString("s_hfactor")!=null?rowSet.getString("s_hfactor"):"";
				String vfactor=rowSet.getString("vfactor")!=null?rowSet.getString("vfactor"):"";
				String s_vfactor=rowSet.getString("s_vfactor")!=null?rowSet.getString("s_vfactor"):"";
				String item_result=rowSet.getString("item")!=null?rowSet.getString("item"):"";
				FieldItem item=null;
				if(hfactor.length()>1) 
				{
					item=getFieldItem(hfactor.toLowerCase()); 
					if(item!=null)
						hfactor=item.getItemdesc();
					else
					{
						hfactor="";
					}
				}
				if(s_hfactor.length()>1)
				{
					item=getFieldItem(s_hfactor); 
					if(item!=null)
						s_hfactor=item.getItemdesc();
					else
						s_hfactor="";
				}
				if(vfactor.length()>1)
				{
					item=getFieldItem(vfactor); 
					if(item!=null)
						vfactor=item.getItemdesc();
					else
						vfactor="";
				}
				if(s_vfactor.length()>1)
				{
					item=getFieldItem(s_vfactor); 
					if(item!=null)
						s_vfactor=item.getItemdesc();
					else 
						s_vfactor="";
				}
				if(item_result.length()>1)
				{
					item=getFieldItem(item_result); 
					if(item!=null)
						item_result=item.getItemdesc();
					else 
						item_result="";
				
				}
				
				abean.set("hfactor",hfactor);
				abean.set("s_hfactor",s_hfactor);
				abean.set("vfactor",vfactor);
				abean.set("s_vfactor",s_vfactor);
				abean.set("item_result",item_result);
				if(b0110_Sp.length()>0)
					b0110_Sp.append(",");
				abean.set("b0110",b0110_Sp.toString());
				list.add(abean);
			}
			rowSet.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	

	public ArrayList getLowerOperateList()
	{
		ArrayList list=new ArrayList();
		list.add(new CommonData("<","<"));
		list.add(new CommonData("<=","<="));
		list.add(new CommonData("无","无"));
		return list;
	}
	
	
	public ArrayList getHeightOperateList()
	{
		ArrayList list=new ArrayList();
		list.add(new CommonData("<","<"));
		list.add(new CommonData("<=","<="));
		list.add(new CommonData("=","="));
		list.add(new CommonData("无","无"));
		return list;
	}
	
	
	public ArrayList getMiddleValueList(String itemid,String itemdesc)
	{
		ArrayList list=new ArrayList();
		list.add(new CommonData("年龄("+itemid+")","年龄("+itemdesc+")"));
		list.add(new CommonData("工龄("+itemid+")","工龄("+itemdesc+")"));
		list.add(new CommonData("年份("+itemid+")","年份("+itemdesc+")"));
		list.add(new CommonData("月份("+itemid+")","月份("+itemdesc+")"));
		return list;
	}
	
	public String getMidValue(String num,String itemid)
	{
		String value="";
		if("0".equals(num))
			value="年龄("+itemid+")";
		else if("1".equals(num))
			value="工龄("+itemid+")";
		else if("2".equals(num))
			value="年份("+itemid+")";
		else if("3".equals(num))
			value="月份("+itemid+")";
		return value;
	}
	
	
	public RecordVo getStandardDataVo(String item,String item_id)
	{
		RecordVo vo=new RecordVo("gz_stand_date");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			vo.setString("item",item);
			vo.setString("item_id",item_id);
			vo=dao.findByPrimaryKey(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	
	
	/**
	 * 
	 * @param opt_type  0:lowerValue 1:heightValue 2:lowerOperate 3:heightOperate 4: middleValue 5:isAccuratelyDay
	 * @return
	 */
	public String getFactorValue(String opt_type,String factor,String type,String item)
	{
		String value="";
		String[] temp=factor.split("\\|");
		if("0".equals(opt_type))
		{
			value=temp[0];
		}
		else if("1".equals(opt_type))
		{
			if("D".equals(type))
			{
				value=temp[4];
			}
			else
			{
				value=temp[3];
			}
		}
		else if("2".equals(opt_type))
		{
			value=temp[1];
		}
		else if("3".equals(opt_type))
		{
			if("D".equals(type))
			{
				value=temp[3];
			}
			else
			{
				value=temp[2];
			}
		}
		else if("4".equals(opt_type))
		{
			if("D".equals(type))
			{
				String a_temp=temp[2];
				value=getMidValue(a_temp,item);
			}
		}
		else if("5".equals(opt_type))
		{

			String a_temp=temp[5];
			if("false".equalsIgnoreCase(a_temp))
				value="0";
			else
				value="1";
		}
		return value;
	}
	
	
	/**
	 * 保存薪资标准区间数据
	 * @param item
	 * @param item_id
	 * @param description
	 * @param type
	 * @param lowerValue
	 * @param lowerOperate
	 * @param heightValue
	 * @param heightOperate
	 * @param middleValue
	 * @param isAccuratelyDay
	 */
	public void saveStandardData(String item,String item_id,String description,String type,String lowerValue,String lowerOperate,String heightValue,String heightOperate,String middleValue,String isAccuratelyDay)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			if(item_id!=null&&item_id.trim().length()>0)
				dao.delete("delete from gz_stand_date where item='"+item+"' and item_id='"+item_id+"'",new ArrayList());
			else
				item_id=getItemId(item);
			RecordVo vo=new RecordVo("gz_stand_date");
			vo.setString("item",item);
			vo.setString("item_id",item_id);
			vo.setString("description",description);
			StringBuffer lexpr=new StringBuffer("");
			StringBuffer factor=new StringBuffer("");
			if("N".equals(type))
			{
				if("无".equals(lowerOperate))
				{
					factor.append("|无|"+heightOperate+"|"+heightValue+"|");
				}
				else
				{
					factor.append(lowerValue+"|"+lowerOperate+"|");
					lexpr.append(item.toUpperCase()+lowerOperate.replaceAll("<",">")+lowerValue);	
					
					if("无".equals(heightOperate))
					{
						factor.append("无||");						
					}
				}
				
				if(!"无".equals(heightOperate))
				{
					factor.append(heightOperate+"|"+heightValue+"|");
					
					if("无".equals(lowerOperate))
						lexpr.append(item.toUpperCase()+heightOperate+heightValue);
					else
						lexpr.append(" and "+item.toUpperCase()+heightOperate+heightValue);
				}
			}
			else
			{
				String accuratelyDay="False";
				if("1".equals(isAccuratelyDay))
					accuratelyDay="True";
				if("无".equals(lowerOperate))
				{
					factor.append("|无|"+getMiddleValue(middleValue)+"|"+heightOperate+"|"+heightValue+"|"+accuratelyDay+"|");
				}
				else
				{
					factor.append(lowerValue+"|"+lowerOperate+"|"+getMiddleValue(middleValue)+"|");
					lexpr.append(middleValue.toUpperCase()+lowerOperate.replaceAll("<",">")+lowerValue);	
					
					if("无".equals(heightOperate))
					{
						factor.append("无||"+accuratelyDay+"|");						
					}
				}
				
				if(!"无".equals(heightOperate))
				{
					factor.append(heightOperate+"|"+heightValue+"|"+accuratelyDay+"|");
					
					if("无".equals(lowerOperate))
						lexpr.append(middleValue.toUpperCase()+heightOperate+heightValue);
					else
						lexpr.append(" and "+middleValue.toUpperCase()+heightOperate+heightValue);
				}
				
			}
			vo.setString("lexpr",lexpr.toString());
			vo.setString("factor",factor.toString());
			dao.addValueObject(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	public String getMiddleValue(String middleName)
	{
		String middleValue="0";
		if("年龄".equals(middleName.substring(0,2)))
			middleValue="0";
		else if("工龄".equals(middleName.substring(0,2)))
			middleValue="1";
		else if("年份".equals(middleName.substring(0,2)))
			middleValue="2";
		else if("月份".equals(middleName.substring(0,2)))
			middleValue="3";
		return middleValue;
	}
	
	public String getItemId(String item)throws GeneralException
	{
		String item_id="";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select MAX("+Sql_switcher.sqlToInt("item_id")+") from gz_stand_date where item='"+item+"'");//item_id是字符型，直接取最大值不对，应该先转成int行  zhaoxg2014-12-26
			
			if(rowSet.next())
			{
				if(rowSet.getString(1)!=null)
				{
					int itemid=Integer.parseInt(rowSet.getString(1));
					item_id=String.valueOf(++itemid);
				}
				else
					item_id="1";
			}
			else 
				item_id="1";
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return item_id;
	}
	/**
	 * 如操作用户既有操作单位权限,又有管理范围权限,程序默认找最大的权限范围显示机构树;
	 * 如两者的权限范围属于平级,则优先按操作单位;
	 * 如没有设置操作单位,再按管理范围
	 * @return 0管理范围，1操作单位
	 */
 public String isOperOrManager()
 {
	 String oper="0";
	 if(this.userView.getUnit_id()!=null&&this.userView.getUnit_id().trim().length()>2)
	 {
		 if(this.userView.getManagePrivCode()==null|| "".equals(this.userView.getManagePrivCode()))//没有管理范围
			 oper="1";
		 else
		 {
			 if(this.userView.getUnit_id().length()==3)
			 {
				 oper="1";
			 }
			 else if(this.userView.getManagePrivCodeValue()==null|| "".equals(this.userView.getManagePrivCodeValue()))//管理范围为全部
				 oper="0";
			 else
			 {
	    		 String[] temp = this.userView.getUnit_id().split("`");
	    		 //找出操作单位中，范围最大的，长度越短，范围越大,现在只能以长度来判断了，如果出现特殊数据，就是实施的找事，和他PK！！！
	     		 String max = "";
	    		 for(int i=0;i<temp.length;i++)
	    		 {
		    		 if(temp[i]==null|| "".equals(temp[i]))
		    			 continue;
		    		 if(max.length()==0)
		    			 max = temp[i];
		    		 if(max.length()>temp[i].length())
	    				 max = temp[i];
	    		 }
	    		String manage = this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
	    		if(max.length()<manage.length()||max.length()==manage.length())
	    			oper="1";
			 }
		 }
	 }
	 return oper;
 }
 public boolean updateGsUnit(String id,String content,String pkg_id)
 {
	 boolean flag = true;
	 try
	 {
		 ContentDAO dao = new ContentDAO(this.conn);
		 if(content!=null&&content.trim().length()>0)
		 {
			 String[] tt= content.split(",");
			 StringBuffer tt_b = new StringBuffer("");
			 for(int i=0;i<tt.length;i++)
			 {
				 if(tt[i]==null|| "".equals(tt[i]))
					 continue;
				 tt_b.append(","+tt[i].substring(2));
			 }
			 content=tt_b.toString();
		 }
		 RecordVo vo = new RecordVo("gz_stand_history");
		 vo.setInt("id", Integer.parseInt(id));
		 vo.setInt("pkg_id", Integer.parseInt(pkg_id));
		 vo = dao.findByPrimaryKey(vo);
		 if("".equals(content))
		 {
			vo.setString("b0110", null);
		 }
	     else
	     {
		    vo.setString("b0110", content);
		 }
		 dao.updateValueObject(vo);
	 }
	 catch(Exception e)
	 {
		 flag = false;
		 e.printStackTrace();
	 }
	 return flag;
 }
 
 	/**
 	 * 查找出所对应的临时变量,不直接从数据字典里面去找，否则可能出现新增临时变量，不同步数据字典取不到
 	 * @author sunjian
 	 * @date 2017-11-24
 	 * @return FieldItem
 	 */
 	private FieldItem getVarItem(String cname) {
 		StringBuffer strsql = new StringBuffer();
 		FieldItem fielditem = new FieldItem();
 		ArrayList list = new ArrayList();
 		RowSet rset = null;
 		try {
 			ContentDAO dao=new ContentDAO(this.conn);
	 		strsql.append("select cname,chz,ntype,fldlen,codesetid,flddec from midvariable where cname=?");
	 		list.add(cname);
	 		rset=dao.search(strsql.toString(),list);
	 		if(rset.next()) {
		        String itemid = rset.getString("cname").toUpperCase();
		        fielditem.setItemid(itemid);
		        fielditem.setItemdesc(rset.getString("chz"));
		        fielditem.setDecimalwidth(rset.getInt("flddec"));
		        fielditem.setItemlength(rset.getInt("fldlen"));
		        fielditem.setCodesetid(rset.getString("codesetid"));
		        fielditem.setFieldsetid("");
		        int ntype = rset.getInt("ntype");
		        switch (ntype) {
		        case 1:
		          fielditem.setItemtype("N");
		          break;
		        case 2:
		          fielditem.setItemtype("A");
		          break;
		        case 3:
		          fielditem.setItemtype("D");
		          break;
		        case 4:
		          fielditem.setItemtype("A");
		          break;
		        default:
		          fielditem.setItemtype("A");
		        }
		
		        fielditem.setUseflag("1");
		        fielditem.setVarible(1);
	 		}
 		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rset);
		}
 		return fielditem;
 	}

	public String getHcontent() {
		return hcontent;
	}


	public void setHcontent(String hcontent) {
		this.hcontent = hcontent;
	}


	public String getHfactor() {
		return hfactor;
	}


	public void setHfactor(String hfactor) {
		this.hfactor = hfactor;
	}


	public String getItem() {
		return item;
	}


	public void setItem(String item) {
		this.item = item;
	}


	public String getS_hfactor() {
		return s_hfactor;
	}


	public void setS_hfactor(String s_hfactor) {
		this.s_hfactor = s_hfactor;
	}


	public String getS_vfactor() {
		return s_vfactor;
	}


	public void setS_vfactor(String s_vfactor) {
		this.s_vfactor = s_vfactor;
	}


	public String getStandID() {
		return standID;
	}


	public void setStandID(String standID) {
		this.standID = standID;
	}


	public String getVcontent() {
		return vcontent;
	}


	public void setVcontent(String vcontent) {
		this.vcontent = vcontent;
	}


	public String getVfactor() {
		return vfactor;
	}


	public void setVfactor(String vfactor) {
		this.vfactor = vfactor;
	}



	public String getPkg_id() {
		return pkg_id;
	}



	public void setPkg_id(String pkg_id) {
		this.pkg_id = pkg_id;
	}

	public boolean isExist() {
		return exist;
	}



	public FieldItem getHitem() {
		return hitem;
	}



	public FieldItem getR_item() {
		return r_item;
	}



	public FieldItem getS_hitem() {
		return s_hitem;
	}



	public FieldItem getS_vitem() {
		return s_vitem;
	}



	public FieldItem getVitem() {
		return vitem;
	}



	public String getNbase() {
		return nbase;
	}



	public void setNbase(String nbase) {
		this.nbase = nbase;
	}



	public void setS_hitem(FieldItem s_hitem) {
		this.s_hitem = s_hitem;
	}



	public void setS_vitem(FieldItem s_vitem) {
		this.s_vitem = s_vitem;
	}
	public void setUserView(UserView userView)
	{
		this.userView=userView;
	}



	public ArrayList getVarList() {
		return varList;
	}



	public void setVarList(ArrayList varList) {
		this.varList = varList;
	}
}
