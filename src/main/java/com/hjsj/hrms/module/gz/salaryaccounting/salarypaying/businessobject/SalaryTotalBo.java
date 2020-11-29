package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.util.*;


/**
 *<p>Title:薪资总额业务类</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2015-8-31</p> 
 *@author dengc
 *@version 7.x
 */
public class SalaryTotalBo {
	private Connection conn=null;
	private SalaryTemplateBo salaryTemplateBo=null; 
	private GzAmountXMLBo gzAmountXMLBo=null;
	private ArrayList controlList=new ArrayList();   //工资总额 参数控制项列表(有效的)
	private HashMap gzXmlMap=null;
	private UserView userView=null;
	private int salaryid=0;
	private String ctrl_type="";       //(0|1)是否进行部门总额控制
	private String contrlLevelId="";   //部门 控制层级
	private String belongUN="";        //归属单位
	private String belongUM="";        //归属部门
	private String ctrl_peroid="";     //控制种类 1按年| 0按月 |2季度
	private String ctrl_by_level="1";  //1;按层级控制  0：不按层级控制
	private String totalTable="";      //工资总额关联表
	/**薪资项目和临时变量列表*/
	private  ArrayList fldvarlist=new ArrayList();
	
	public SalaryTotalBo(Connection con,UserView a_userView,int salaryid)throws GeneralException
	{
		this.conn=con;
		this.salaryid=salaryid;
		this.userView=a_userView;
		this.salaryTemplateBo=new SalaryTemplateBo(conn,salaryid,this.userView);
		try
		{
			this.gzAmountXMLBo=new GzAmountXMLBo(this.conn,1);
			this.gzXmlMap=this.gzAmountXMLBo.getValuesMap();
			if(this.gzXmlMap!=null)
			{		
			//	fldvarlist.addAll(this.salaryTemplateBo.getMidVariableListByTable(salaryid+""));
			//	fldvarlist.addAll(this.salaryTemplateBo.getSalaryItemList("", salaryid+"",2)); 
				fldvarlist.addAll(this.getMidVariableList());
				fldvarlist.addAll(this.getGzFieldList());
				
				this.ctrl_type=(String)this.gzXmlMap.get("ctrl_type");
				this.ctrl_peroid=(String)this.gzXmlMap.get("ctrl_peroid");
				this.ctrl_by_level=(String)this.gzXmlMap.get("ctrl_by_level");
				HashMap um_un=(HashMap)this.gzXmlMap.get("hs");
				if(um_un!=null)
				{
					this.belongUN=(String)um_un.get("orgid");
					this.belongUM=(String)um_un.get("deptid");
					this.contrlLevelId=(String)um_un.get("contrlLevelId");  //部门控制层级
				}
				ArrayList ctrl_item_list=(ArrayList)this.gzXmlMap.get("ctrl_item");
				if(ctrl_item_list!=null)
				{
					for(int i=0;i<ctrl_item_list.size();i++)
					{
						LazyDynaBean abean=(LazyDynaBean)ctrl_item_list.get(i);
						String flag=(String)abean.get("flag");
						if("1".equals(flag))
							this.controlList.add(abean); ////工资总额 参数控制项列表(有效的)
					}
				} 
				this.totalTable=(String)this.gzXmlMap.get("setid");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);			
		}
	}
	
	
	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	private ArrayList getMidVariableList()throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec from ");
			buf.append(" midvariable where nflag=0 and templetid=0 ");
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname")); 
				item.setFieldsetid("A01");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
				case 4://代码型					
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return fieldlist;
	}
	
	/**
	 * 查询薪资类别中的指标列表
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList getGzFieldList()throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			//20141031 dengcan  客户薪资类别500多个，表中薪资项19000多个，严重影响效率
	//		buf.append("select fieldsetid,itemid,itemdesc,itemtype,itemlength,nwidth,decwidth,codesetid,formula  from salaryset ");
	//		buf.append(" order by sortid");
			buf.append("select distinct fieldsetid,itemid,itemdesc,itemtype,itemlength,nwidth,decwidth,codesetid   from salaryset ");
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			HashMap fieldItemMap=new HashMap();
			while(rset.next())
			{
				if(fieldItemMap.get(rset.getString("itemid"))==null)
				{
					FieldItem item=new FieldItem();
					item.setFieldsetid(rset.getString("fieldsetid"));
					item.setItemid(rset.getString("itemid"));
					item.setItemdesc(rset.getString("itemdesc"));
					item.setItemtype(rset.getString("itemtype"));
					item.setItemlength(rset.getInt("itemlength"));
					item.setDisplaywidth(rset.getInt("nwidth"));
					item.setDecimalwidth(rset.getInt("decwidth"));
					item.setCodesetid(rset.getString("codesetid"));
			//		item.setFormula(Sql_switcher.readMemo(rset,"formula"));
					item.setVarible(0);
					fieldlist.add(item);
					fieldItemMap.put(rset.getString("itemid"), "1");
				}
			}//while loop end.
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return fieldlist;
	}
	
	
	/**
	 * 删除薪资记录，判断记录所涉及的机构是否还有总额可计，如没有需将当月实发额置为0
	 * @param whereSql :  from xxxx where (a0100='' and a00z0='' and a00z1='' and  nbase=''  and salaryid=xxx ) or (......)
	 * @param flag : 1:薪资发放  2：薪资审批
	 */
	public void reCalculateTotal(String whereSql,int flag)
	{
		
		try
		{ 
			String isControl=this.salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag");   //该工资类别是否进行总额控制
			if("1".equals(isControl))
			{
				HashMap unMap= getUnByTime(whereSql,flag);
				ContentDAO dao=new ContentDAO(this.conn);
				String totalTable=(String)this.gzXmlMap.get("setid"); //薪资总额控制子集
				for(Iterator t=unMap.keySet().iterator();t.hasNext();)
				{
					String key=(String)t.next();
					String whl=(String)unMap.get(key);
					String ss=getEmptyRecordsUns(whl,key);
					String year=key.split("-")[0];
					String month=key.split("-")[1];
					if(isCalculateTotal3(year,month,whl))
					{
						StringBuffer whl2=new StringBuffer("");
						String setid=(String)this.gzXmlMap.get("setid");
						whl2.append(" and "+Sql_switcher.year(setid+"z0")+"="+year);
						whl2.append(" and "+Sql_switcher.month(setid+"z0")+"="+month);
						for(int i=0;i<controlList.size();i++)
						{
							LazyDynaBean abean=(LazyDynaBean)controlList.get(i);
							String Realitem=(String)abean.get("realitem");  //实发项目
							String balanceitem=(String)abean.get("balanceitem"); //剩余项目	
							String Planitem=(String)abean.get("planitem");  //计划项目
							dao.update("update "+totalTable+" set "+Realitem+"=0 where 1=1 "+whl2.toString()+" and b0110 in ("+ss+" ) ");
							dao.update("update "+totalTable+" set "+balanceitem+"=("+Planitem+"-"+Realitem+") where 1=1  "+whl2.toString()+" and b0110 in ("+ss+"  )");
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
	
	
	/**
	 * 判断是否需要计算总计(salaryhistory)
	 * @param belongTime 归属时间
	 * @return
	 */
	private boolean isCalculateTotal3(String year,String month,String whl)
	{
		boolean flag=true;
		if(this.controlList.size()==0)
			return false;
		String setid=(String)this.gzXmlMap.get("setid");
		if(setid==null||setid.length()==0)  //如果没有设置工资总额子集
			return false;
		String sp_flag=(String)this.gzXmlMap.get("sp_flag");
		if(sp_flag==null||sp_flag.length()==0)  //如果没有设置审批状态标识
			return false;
		
		String fc_flag=(String)this.gzXmlMap.get("fc_flag"); //封存状态指标 

		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);  
			StringBuffer sql=new StringBuffer("select count(b0110) from "+setid);
			sql.append(" where  b0110 in ("+whl+")");
			if("2".equals(ctrl_peroid))
			{
				sql.append(" and "+getQuarterWhl(year,Integer.parseInt(month),(String)this.gzXmlMap.get("setid")));
			}
			else
			{
				sql.append(" and "+Sql_switcher.year(setid+"z0")+"="+year);
				if("0".equals(ctrl_peroid))    //控制种类 1按年| 0按月 |2季度
				{
					sql.append(" and "+Sql_switcher.month(setid+"z0")+"="+month);
				}
			}
			sql.append(" and ("+sp_flag+"='04' or "+sp_flag+"='03' )");
			
			if(fc_flag!=null&&fc_flag.trim().length()>0) //设置了封存指标
			{
				sql.append(" and  "+Sql_switcher.isnull(fc_flag,"''")+"<>'1' ");
			}
			
		    RowSet rowSet=dao.search(sql.toString());
		    if(rowSet.next())
		    {
		    	if(rowSet.getInt(1)==0)
		    		return false;
		    }
		    
		    if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return flag;
	}
	
	
	
	//取得当前日期下 薪资历史表中无记录的组织机构
	private String getEmptyRecordsUns(String whl,String y_m)
	{
			StringBuffer s=new StringBuffer("");
			try
			{
				ContentDAO dao=new ContentDAO(this.conn);
				String year=y_m.split("-")[0];
				String month=y_m.split("-")[1];
				
				StringBuffer whl_=new StringBuffer("");
				whl_.append(" and "+Sql_switcher.year("a00z0")+"="+year);
				whl_.append(" and "+Sql_switcher.month("a00z0")+"="+month);	
				String field="b0110";
				if(this.belongUN!=null&&this.belongUN.length()>0)
					field=this.belongUN;
				if("0".equals(this.ctrl_type))
				{
					field="e0122";
					if(this.belongUM!=null&&this.belongUM.length()>0)
						field=this.belongUM;
				}
				RowSet rowSet=dao.search("select distinct "+field+" from salaryhistory where 1=1 "+whl_.toString()+" and "+field+" in ("+whl+")");
				StringBuffer str=new StringBuffer("");
				while(rowSet.next())
				{
					String value=rowSet.getString(1);
					str.append(",'"+value+"'");
				}
				String sql="select codeitemid from organization where codeitemid in ("+whl+")   ";
				if(str.length()>0)
					sql+=" and codeitemid not in ("+str.substring(1)+")";
				rowSet=dao.search(sql);
				while(rowSet.next())
					s.append(",'"+rowSet.getString(1)+"'");
				if(s.length()>0)
					return s.substring(1);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return s.toString();
	}
	
	
	
	/**
	 * 根据删除记录获得某年、某月需控制的总额单位
	 * @param whereSql :  from xxxx where (a0100='' and a00z0='' and a00z1='' and  nbase=''  and salaryid=xxx ) or (......)
	 * @param flag : 1:薪资发放  2：薪资审批
	 * @return
	 */
	private HashMap getUnByTime(String whereSql,int flag)
	{
		ContentDAO dao=new ContentDAO(this.conn);
		HashMap unMap=new HashMap();
		try
		{ 
			String field="b0110";
			if(this.belongUN!=null&&this.belongUN.length()>0)
				field=this.belongUN;
			if("0".equals(this.ctrl_type))
			{
				field="e0122";
				if(this.belongUM!=null&&this.belongUM.length()>0)
					field=this.belongUM;
			}
			String sql="select distinct "+field+","+Sql_switcher.year("a00z0")+","+Sql_switcher.month("a00z0")+" "+whereSql;
			if(flag==1)
				sql+=" and sp_flag='07'";
			RowSet rowSet=dao.search(sql); 
			while(rowSet.next())
			{
				String un=rowSet.getString(1);
				String key=rowSet.getString(2)+"-"+rowSet.getString(3); 
				if(unMap.get(key)==null)
				{
					unMap.put(key,"'"+un+"'");
				}
				else
				{
					String tmp=(String)unMap.get(key);
					unMap.put(key,tmp+",'"+un+"'");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return unMap;
	}
	
	
	
	/**
	 * 汇总薪资总额
	 * @param year 总额计算年份
	 * @throws GeneralException 
	 */
	public void collectData(String year) throws GeneralException
	{
		try
		{
			
			java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("[0-9]*");
	        java.util.regex.Matcher match=pattern.matcher(year);
	        if(match.matches()==true) //验证数值型，防止SQL注入
	        { 
				if("1".equals(ctrl_by_level))  //1;按层级控制
				{
				
					String isSetTotalCondition = isSetTotalCondition();
					if(StringUtils.isNotBlank(isSetTotalCondition)) {
						throw GeneralExceptionHandler.Handle(new Exception(isSetTotalCondition));
					}
					ContentDAO dao=new ContentDAO(this.conn);
					ArrayList layerOrgList=getLayerOrgList();
					
					HashMap   orgMap=getCollectOrg(year);
					for(int i=layerOrgList.size()-2;i>=0;i--)
					{
						ArrayList nodeList=(ArrayList)layerOrgList.get(i);
						for(int j=0;j<nodeList.size();j++)
						{
							LazyDynaBean abean=(LazyDynaBean)nodeList.get(j);
							String codeitemid=(String)abean.get("codeitemid");
							if(orgMap.get(codeitemid)!=null)
							{
								String sub_whl=getSubWhl(abean,(ArrayList)layerOrgList.get(i+1),orgMap);
								if(sub_whl.length()>0)
									collectGzTotal(codeitemid,sub_whl,year);
								
							}
							
						}
					}
				}
	        }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	/**
	 * 汇总工资总额
	 * @param codeitemid 汇总单位ID
	 * @param sub_whl   子单位ID
	 * @param year   总额控制年份 
	 */
	private void collectGzTotal(String codeitemid,String sub_whl,String year)
	{
		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("");
			String setid=(String)this.gzXmlMap.get("setid");

			String fcWhere=" and 1=1 "; 
	        String fc_flag=(String)this.gzXmlMap.get("fc_flag"); //封存状态指标   
	            if(fc_flag!=null&&fc_flag.trim().length()>0)
	                fcWhere=" and "+Sql_switcher.isnull(fc_flag,"''")+"<>'1'  ";//未封存
		
			for(int i=0;i<this.controlList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)controlList.get(i);
				String Realitem=(String)abean.get("realitem");  //实发项目
				String balanceitem=(String)abean.get("balanceitem"); //剩余项目	
				String Planitem=(String)abean.get("planitem");  //计划项目
			
				String itemid=Realitem;
				sql.setLength(0);
				sql.append("update "+setid+" set "+itemid+"=(select  "+itemid+" from ");
				sql.append(" (select sum("+itemid+") "+itemid+","+Sql_switcher.month(setid+"z0")+" amonth from "+setid+" where "+Sql_switcher.year(setid+"z0")+"=?  and b0110 in ("+sub_whl+") "
				        +fcWhere
				        +" group by "+Sql_switcher.month(setid+"z0")+" "); 
				sql.append(" )a where "+Sql_switcher.month(setid+"."+setid+"z0")+"=a.amonth ) where "+Sql_switcher.year(setid+"z0")+"=?  and b0110='"+codeitemid
				        +"'"+fcWhere+" and exists ( select null from ");
				sql.append(" (select sum("+itemid+") "+itemid+","+Sql_switcher.month(setid+"z0")+" amonth from "+setid+" where "+Sql_switcher.year(setid+"z0")+"=?  and b0110 in ("+sub_whl+") "
				        +fcWhere
				        +" group by "+Sql_switcher.month(setid+"z0")+" ");
				sql.append(" )a where "+Sql_switcher.month(setid+"."+setid+"z0")+"=a.amonth ) ");
				dao.update(sql.toString(),Arrays.asList(new Integer[]{new Integer(year),new Integer(year),new Integer(year)}));
				sql.setLength(0);
				sql.append("update "+setid+" set "+balanceitem+"=("+Planitem+"-"+Realitem+" ) where "+Sql_switcher.year(setid+"z0")+"=?  and b0110='"+codeitemid+"' and "+Realitem+" is not null");
				dao.update(sql.toString(),Arrays.asList(new Integer[]{new Integer(year)}));
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	
	
	
	/**
	 * 获得子单位ID
	 * @param abean
	 * @param subNodeList  低层级单位节点
	 * @param orgMap 工资总额表中存在的组织节点
	 * @return
	 */
	private String getSubWhl(LazyDynaBean abean,ArrayList subNodeList,HashMap orgMap)
	{
		String codeitemid=(String)abean.get("codeitemid");
		StringBuffer whl=new StringBuffer("");
		for(int i=0;i<subNodeList.size();i++)
		{
			LazyDynaBean aabean=(LazyDynaBean)subNodeList.get(i);
			String acodeitemid=(String)aabean.get("codeitemid");
			String aparentid=(String)aabean.get("parentid");
			if(aparentid.equals(codeitemid)&&orgMap.get(acodeitemid)!=null)
				whl.append(",'"+acodeitemid+"'");
		}
		if(whl.length()>0)
			return whl.substring(1);
		return whl.toString();
	}
	
	

	/**
	 * 取得每层的节点
	 * @return
	 */
	private ArrayList getLayerOrgList()
	{
		ArrayList layerList=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer(""); 
			sql.append("select * from organization where codesetid<>'@K'"); 
			if(sql.length()>0)
			{
				if("1".equals(this.ctrl_type))//1按单位总额控制
					sql.append(" and codesetid<>'UM' ");
				sql.append(" order by grade");
				RowSet rowSet=dao.search(sql.toString());
				ArrayList tempList=new ArrayList();
				String temp="";
				LazyDynaBean abean=new LazyDynaBean();
				while(rowSet.next())
				{
					String grade=rowSet.getString("grade");
					if(temp.length()==0)
						temp=grade;
					if(!temp.equals(grade))
					{
						layerList.add(tempList);
						tempList=new ArrayList();
						temp=grade;
					}
					abean=new LazyDynaBean();
					String codesetid=rowSet.getString("codesetid");
					String codeitemid=rowSet.getString("codeitemid");
					String parentid=rowSet.getString("parentid");
					String childid=rowSet.getString("childid");
					String codeitemdesc=rowSet.getString("codeitemdesc");
					abean.set("grade",grade);
					abean.set("codesetid",codesetid);
					abean.set("codeitemid",codeitemid);
					abean.set("parentid",parentid);
					abean.set("childid",childid);
					abean.set("codeitemdesc",codeitemdesc);
					
					tempList.add(abean);
				}
				if(tempList.size()>0)
					layerList.add(tempList);
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return layerList;
	}
	

	/**
	 * 取得工资总额表中存在的组织
	 * @param year
	 * @return
	 */
	private HashMap getCollectOrg(String year)
	{
		HashMap map=new HashMap();
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String setid=(String)this.gzXmlMap.get("setid");//=setid:单位工资总额子集
			if(setid.trim().length()>0)//非空串判断 xiegh 20170419
			{
				
				rowSet=dao.search("select distinct b0110 from "+setid+" where "+Sql_switcher.year(setid+"z0")+"=?",Arrays.asList(new Integer[]{new Integer(year)}));
				while(rowSet.next())
				{
					map.put(rowSet.getString("b0110"),"1");
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
		return map;
	}
	
	
	/**
	 * 工资总额控制
	 * @param whereSql:人员筛选条件
	 * @param flag:   1:薪资发放  2：薪资审批
	 * @param  otpDesc: 是否继续 | 不予提交工资 | 不予报批
	 * @return  if(info="success") 不超总额  else if（info indexOf (otpDesc)!=-1） 超总额，并返回错误信息  else xxxxx组织薪资总额没有批复
	 */
	public String  calculateTotal(String  whereSql,int flag,String optDesc)
	{
		String info="success";
		try
		{
			if(isControl(flag)) //是否进行薪资总额控制
			{
					if(StringUtils.isNotBlank(this.isSetTotalCondition()))
						throw GeneralExceptionHandler.Handle(new Exception(this.isSetTotalCondition()));

					SalaryCtrlParamBo ctrlparam=this.salaryTemplateBo.getCtrlparam();
					boolean isSp=false;
					String flow_flag=ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag"); //1:需要审批
					String ctrlType = ctrlparam.getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"ctrl_type"); //控制方式，=1强制控制，=0仅提示。
					if(ctrlType==null||ctrlType.trim().length()==0)
						ctrlType="1";
					if("1".equalsIgnoreCase(flow_flag))
						isSp=true;
					String tableName="salaryhistory";
					if(flag==1) //1:薪资发放
					{
						tableName=this.userView.getUserName()+"_salary_"+this.salaryid;
						DbNameBo.autoAddZ1_total(this.userView,tableName,salaryid+"",this.salaryTemplateBo.getManager(),this.salaryTemplateBo.getSalaryFieldStr(),"",isSp);
					} 
					 ArrayList dataList=getBelongTimeList(tableName,whereSql); //取得数据中的归属日期列表
					 for(int i=0;i<dataList.size();i++)
					 {
							LazyDynaBean dataBean=(LazyDynaBean)dataList.get(i);
							StringBuffer info_buffer=new StringBuffer(); //报错信息， 如：xxxxx薪资总额没有批复 
							if(isCalculateTotal(dataBean,whereSql,tableName,info_buffer,optDesc))
							{
								info=validateOverPlanValue(dataBean,whereSql,optDesc);
								if(info.length()==0||info.indexOf(optDesc)==-1|| "0".equals(ctrlType))
								{
									caculateTotal(dataBean,whereSql);
									if(info.length()==0)   //2011-06-21
										info="success";
								}
								else
									break;
							}
							else 
							{
								if(info_buffer.length()>0)
									info=info_buffer.toString();
								else
									info="success";
							}
					}
					
					
					 if(flag==1) //1:薪资发放
					 { 
						 ContentDAO dao=new ContentDAO(this.conn); 
						 String temp_name="t#"+this.userView.getUserName()+"_gz"; 
						 
						StringBuffer del=new StringBuffer("delete from salaryhistory where exists (select * from "+temp_name);
						del.append(" where a0100=salaryhistory.a0100 ");
						del.append(" and  upper(nbase)=upper(salaryhistory.nbase) and  a00z0=salaryhistory.a00z0 and  a00z1=salaryhistory.a00z1  ) ");
						del.append(" and salaryid="+salaryid);
						del.append(" and lower(userflag)='");   
						del.append(this.userView.getUserName().toLowerCase());
						del.append("'");
						dao.delete(del.toString(),new ArrayList()); 
						if(!isSp) //非审批工资套
						{
							StringBuffer s1=new StringBuffer("");
							StringBuffer s2=new StringBuffer("");
							RowSet rowSet=dao.search("select * from  "+temp_name+" where 1=2");
							ResultSetMetaData metaData=rowSet.getMetaData();
							for(int i=1;i<=metaData.getColumnCount();i++)
							{				 
									 
									s1.append(","+metaData.getColumnName(i));
									s2.append(","+metaData.getColumnName(i));
							}
							String sql0="insert into salaryhistory ("+s1.substring(1)+") select "+s2.substring(1)+" from "+temp_name+" where  ( sp_flag='06')";
							dao.update(sql0);
						}
						dao.update("delete from "+temp_name);
					 }
				 
			}
			else
				info="success";
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return info;
	}
	
	
	/**
	 * 是否进行薪资总额控制
	 * @param flag:   1:薪资发放  2：薪资审批
	 * @return
	 */
	private boolean  isControl(int flag )
	{
		boolean isControl=false;
		SalaryCtrlParamBo ctrlparam=this.salaryTemplateBo.getCtrlparam();
		String contrl_flag=ctrlparam.getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag");   //该工资类别是否进行总额控制
		if("1".equals(contrl_flag))
		{
			String amount_ctrl=ctrlparam.getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"amount_ctrl_ff");
			if(flag==2)
					  amount_ctrl=ctrlparam.getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"amount_ctrl_sp");
			if(amount_ctrl!=null&&amount_ctrl.trim().length()>0)
				contrl_flag=amount_ctrl; 
		}
		if(this.gzXmlMap!=null&& "1".equalsIgnoreCase(contrl_flag))
			isControl=true;
		
		if(this.controlList.size()==0)
			return false;
		String setid=(String)this.gzXmlMap.get("setid");
		if(setid==null||setid.length()==0)  //如果没有设置工资总额子集
			return false;
		String sp_flag=(String)this.gzXmlMap.get("sp_flag");
		if(sp_flag==null||sp_flag.length()==0)  //如果没有设置审批状态标识
			return false;
		
		return  isControl;
	}
	
	

	/**
	 * 计算工资总额
	 * @param belongTime
	 */
	private void  caculateTotal(LazyDynaBean belongTime,String whl)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn); 
			String tempTableName="t#"+userView.getUserName()+"_gz_2"; 
			String year=(String)belongTime.get("year");
			String month=(String)belongTime.get("month");
			StringBuffer whl2=new StringBuffer("");
			String setid=(String)this.gzXmlMap.get("setid");
			whl2.append(" and "+Sql_switcher.year(setid+"z0")+"="+year);
			whl2.append(" and "+Sql_switcher.month(setid+"z0")+"="+month);
			
			String fc_flag=(String)this.gzXmlMap.get("fc_flag"); //封存状态指标 	
			String totalTable=(String)this.gzXmlMap.get("setid"); //薪资总额控制子集
			if(fc_flag!=null&&fc_flag.trim().length()>0)
				whl2.append(" and "+Sql_switcher.isnull(fc_flag,"''")+"<>'1'  ");
			for(int i=0;i<controlList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)controlList.get(i);
				String Planitem=(String)abean.get("planitem");  //计划项目
				String Realitem=(String)abean.get("realitem");  //实发项目
				String formular=(String)abean.get("formular");  //计算公式
				String balanceitem=(String)abean.get("balanceitem");
				
				String _salaryid=","+((String)abean.get("salaryid")).toLowerCase()+","; 
				boolean flag=false;
				if(this.salaryid!=0)
				{
					if(_salaryid.indexOf(","+this.salaryid+",")!=-1)
					  flag=true;
					
				} 
				if(!flag)
					continue;   
				createTotalTempTable(belongTime,abean,whl,true); 
				dao.update("update "+totalTable+" set "+Realitem+"=(select realitem from "+tempTableName+" where "+totalTable+".b0110="+tempTableName+".b0110 ) where 1=1 "+whl2.toString()+" and b0110 in (select b0110 from "+tempTableName+" ) ");
				dao.update("update "+totalTable+" set "+balanceitem+"=("+Sql_switcher.isnull(Planitem,"0")+"-"+Sql_switcher.isnull(Realitem,"0")+") where 1=1  "+whl2.toString()+" and b0110 in (select b0110 from "+tempTableName+" )");
			 
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 *判断是否超过 工资总额计划值 
	 * @return
	 */
	private String validateOverPlanValue(LazyDynaBean belongTime,String whl,String optDesc)
	{
		String tempTableName="t#"+userView.getUserName()+"_gz_2"; 
		StringBuffer info=new StringBuffer("");
		try
		{
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0"; 
			ContentDAO dao=new ContentDAO(this.conn);
			HashMap umLayer=null;
			if(!"1".equals(this.ctrl_type))   //(0|1)是否进行部门总额控制
				umLayer=getUMLayer();
			boolean isOver=false;
			for(int i=0;i<controlList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)controlList.get(i);
				String Realitem=(String)abean.get("realitem");  //实发项目
				String formular=(String)abean.get("formular");  //计算公式
				String _salaryid=","+((String)abean.get("salaryid")).toLowerCase()+","; 
				boolean flag=false;
				if(this.salaryid!=0)
				{
					if(_salaryid.indexOf(","+this.salaryid+",")!=-1)
					  flag=true; 
				} 
				if(!flag)  //总额计算公式不含该薪资套
					continue; 			
				createTotalTempTable(belongTime,abean,whl,false);
				RowSet rowSet=dao.search("select b0110 from "+tempTableName+" where remainitem<0");
				int num=0;
				while(rowSet.next())
				{
					String org_name="";
					String b0110=rowSet.getString("b0110");
					if("1".equals(this.ctrl_type)) //按单位控制
					{
						org_name=AdminCode.getCodeName("UN",b0110);
						isOver=true;
					}
					else
					{	
						org_name=AdminCode.getCodeName("UM",b0110);
						if(org_name==null||org_name.length()==0)
						{
							org_name=AdminCode.getCodeName("UN",b0110);
							isOver=true;
						}
						else
						{
							if(this.contrlLevelId!=null&&this.contrlLevelId.length()>0)
							{
								int level=Integer.parseInt(this.contrlLevelId);
								int selfLevel=Integer.parseInt((String)umLayer.get(b0110.toLowerCase()));
								if(selfLevel<=level)
									isOver=true;
							}
							
							
							if(Integer.parseInt(display_e0122)>0)
							{
								CodeItem item=AdminCode.getCode("UM",b0110,Integer.parseInt(display_e0122));
								if(item!=null)
								{
									org_name=item.getCodename();
									
								}
							}
							String _org_name=this.salaryTemplateBo.getUnByUm(b0110);
							if(_org_name.length()>0)
								org_name=_org_name+" "+org_name;
						}
						
						
					} 
					info.append("\r\n"+org_name+" 薪资总额控制中实发项目大于计划项目");
					num++;
				}
				if(num>0)
				{
					if(this.contrlLevelId==null||this.contrlLevelId.length()==0)					
						info.append("\r\n "+optDesc);
					else if(isOver)
						info.append("\r\n "+optDesc);
				}
			
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			info.append("算法分析器运算错误！");
		}
		return info.toString();
	}
	
	
	
	
	
	/**
	 * 生成实发总额临时表 (已考虑 同月多次发放 和 汇总单位的实发总额)
	 *@param belongTime  总额计算月份
	 *@param abean  总额计算公式
	 *@param whl_str  人员筛选条件
 	 *@param isComputeRPValue 是否计算总额表中的剩余值和实际值
	 */
	private String createTotalTempTable(LazyDynaBean belongTime,LazyDynaBean abean,String whl,boolean isComputeRPValue)
	{
		String tempTableName="t#"+userView.getUserName()+"_gz_2"; //this.userView.getUserName()+"_tempTotlalSalary";
		try
		{
				DbWizard dbWizard =new DbWizard(this.conn);
				ContentDAO dao=new ContentDAO(this.conn);
				Table table=new Table(tempTableName,tempTableName);
				if(dbWizard.isExistTable(tempTableName,false))
				{
					dbWizard.dropTable("t#"+userView.getUserName()+"_gz_2");
				}
				
				
				Field obj=new Field("b0110","b0110");
				obj.setDatatype(DataType.STRING);
				obj.setKeyable(false);			
				obj.setVisible(false);
				obj.setLength(255);
				obj.setAlign("left");
				table.addField(obj);
				
				obj=new Field("realitem","realitem");
				obj.setDatatype(DataType.FLOAT);
				obj.setDecimalDigits(4);
				obj.setLength(15);							
				obj.setKeyable(false);			
				obj.setVisible(false);							
				obj.setAlign("left");
				table.addField(obj);
				
				obj=new Field("planitem","planitem");
				obj.setDatatype(DataType.FLOAT);
				obj.setDecimalDigits(4);
				obj.setLength(15);							
				obj.setKeyable(false);			
				obj.setVisible(false);							
				obj.setAlign("left");
				table.addField(obj);
				
				obj=new Field("remainitem","remainitem");
				obj.setDatatype(DataType.FLOAT);
				obj.setDecimalDigits(4);
				obj.setLength(15);							
				obj.setKeyable(false);			
				obj.setVisible(false);							
				obj.setAlign("left");
				table.addField(obj);
				
				obj=new Field("sp_flag","sp_flag");
				obj.setDatatype(DataType.STRING);
				obj.setKeyable(false);			
				obj.setVisible(false);
				obj.setLength(50);
				obj.setAlign("left");
				table.addField(obj);
				
				dbWizard.createTable(table);
				DBMetaModel dbmodel=new DBMetaModel(this.conn);
				dbmodel.reloadTableModel("t#"+userView.getUserName()+"_gz_2");
			
				importTotalTableData(belongTime,abean,whl,false,isComputeRPValue); //导入临时表实发总额数据
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return tempTableName;
	}
	
	
	/**
	 *导入临时表实发总额数据
	 *@param belongTime  总额计算月份
	 *@param abean  总额计算公式
	 *@param whl_str  人员筛选条件
	 *@param whereIsUnit : whl_str是否设置的是单位ID，如：'0101','0102'
	 *@param isComputeRPValue 是否计算总额表中的剩余值和实际值
	 */
	private  void importTotalTableData(LazyDynaBean belongTime,LazyDynaBean abean,String whl_str,boolean  whereIsUnit,boolean isComputeRPValue)
	{
		try
		{
			String tempTableName="t#"+userView.getUserName()+"_gz_2"; 
			String dateField="z0";
			ContentDAO dao=new ContentDAO(this.conn);
			String Planitem=Sql_switcher.isnull((String)abean.get("planitem"),"0");  //计划项目
			String Realitem=(String)abean.get("realitem");  //实发项目
			String formular=(String)abean.get("formular");  //计算公式
			String balanceitem=(String)abean.get("balanceitem"); //剩余项目
			
			String year=(String)belongTime.get("year");
			String month=(String)belongTime.get("month");
			StringBuffer whl=new StringBuffer("");
			String setid=(String)this.gzXmlMap.get("setid");
			String sp_flag=(String)this.gzXmlMap.get("sp_flag");
			
			whl.append(" and "+Sql_switcher.year("a00"+dateField)+"="+year);
			
			whl.append(" and "+Sql_switcher.month("a00"+dateField)+"="+month);	//20161203 dengcan
			/*
			if(ctrl_peroid .equals("0")) //按月控制   20141106 dengcan
					whl.append(" and "+Sql_switcher.month("a00"+dateField)+"="+month);		
			else if(ctrl_peroid .equals("2")) //按季度 
			{
					whl.setLength(0);
					whl.append(" and "+getQuarterWhl(year,Integer.parseInt(month),"a00"));
			} 
			*/
			
			String field="b0110";
			if(this.belongUN!=null&&this.belongUN.length()>0)
				field=this.belongUN;
			if("0".equals(this.ctrl_type))
			{
				field="e0122";
				if(this.belongUM!=null&&this.belongUM.length()>0)
					field=this.belongUM;
			}
			//导入各单位或部门当前月份实发值（已考虑 同月多次发放 和 汇总单位的实发总额）
			String sub_sql="";
			if(balanceitem!=null&&balanceitem.length()>0)
				sub_sql=calcFormula_str(formular,"",Realitem,"");
			else
				sub_sql=Sql_switcher.isnull(Realitem,"0");
			
			String sub_str="";
			if(whereIsUnit)
			{
				sub_str=" and "+field+" in ( "+whl_str+" )";
			}
			else
			{
				sub_str=" and "+field+" in (select distinct "+field+" from "+this.userView.getUserName()+"_salary_"+this.salaryid+") ";
				if(whl_str!=null&&whl_str.trim().length()>0)
				{
					StringBuffer a_sql=new StringBuffer("");
					a_sql.append(" and "+field+" in (select distinct "+field+" from salaryhistory where 1=1 "+whl_str+" )");	
					sub_str=a_sql.toString();
				}
			}
			if(sub_sql!=null&&sub_sql.trim().length()>0)
			{ 
				StringBuffer sql=new StringBuffer("");
				if(Sql_switcher.searchDbServer()!=2) //oracle
					sql.append("insert into "+tempTableName+" (realitem,b0110) ");
				sql.append(" select  (select sum("+sub_sql+") from salaryhistory a where a."+field+" like b.a_"+field+" "/*+sub_str*/+whl.toString()+"  ) acount,b."+field); //2011-06-21
				sql.append(" from (select ");
				if(Sql_switcher.searchDbServer()==2) //oracle
					sql.append(" distinct ");
				sql.append(field+","+field+Sql_switcher.concat()+"'%' a_"+field+" from salaryhistory where 1=1 "+sub_str+whl.toString());
				if(Sql_switcher.searchDbServer()!=2)
					sql.append("  group by "+field);
				sql.append(" ) b ");
				sql.append(" order by "+field);
				
				
				String clientName = SystemConfig.getPropertyValue("clientName");
				if(clientName!=null&& "weichai".equalsIgnoreCase(clientName))  //潍柴放开不按层级汇总   2014-7-3
				{
					if("0".equals(ctrl_by_level)) // 不按层级控制
					{
						sql.setLength(0);
						sql.append("insert into "+tempTableName+" (realitem,b0110) select  sum("+sub_sql+"),"+field+"  from salaryhistory where 1=1 "+sub_str+whl.toString()+"  group by "+field); 
						sql.append(" order by "+field);
					}
				}
				//导入实发项目数据
				if(Sql_switcher.searchDbServer()==2&&!"weichai".equals(clientName)) //oracle
				{
					RowSet rowSet=dao.search(sql.toString());
					ArrayList dataList=new ArrayList();
					while(rowSet.next())
					{
						double amount=rowSet.getDouble(1);
						String b0110=rowSet.getString(2);
						ArrayList valueList=new ArrayList(); 
						valueList.add(amount);
						valueList.add(b0110);
						dataList.add(valueList);
					}
					if(dataList.size()>0)
						dao.batchInsert("insert into "+tempTableName+" (realitem,b0110) values (?,?)", dataList);
				}
				else
					dao.update(sql.toString());  
				//导入计划项目数据
				sql.setLength(0);
				
				String fc_flag=(String)this.gzXmlMap.get("fc_flag"); //封存状态指标 	
				String subStr="";
				if(fc_flag!=null&&fc_flag.trim().length()>0)
					subStr=" and "+Sql_switcher.isnull(fc_flag,"''")+"<>'1'  ";
				
				if(!isComputeRPValue)
				{
					if("1".equals(ctrl_peroid))    //控制种类 1按年| 0按月 |2季度
					{
						sql.append("update "+tempTableName+" set planitem=(select plan_data from ");
						sql.append("(select sum("+Sql_switcher.isnull("planData","0")+") plan_data ,b0110"); 
						sql.append(" from ( select  "+Planitem+"   as planData ");
						
						sql.append(",b0110 from "+setid+"  where "+Sql_switcher.year(setid+"z0")+"="+year+" "+subStr+" ) a group by a.b0110) b");
						sql.append(" where "+tempTableName+".b0110=b.b0110  ) where exists (select null from ");
						sql.append("(select sum("+Sql_switcher.isnull("planData","0")+") plan_data ,b0110"); 
						sql.append(" from ( select  "+Planitem+"  as planData "); 
						sql.append(",b0110 from "+setid+" where "+Sql_switcher.year(setid+"z0")+"="+year+" "+subStr+" ) a group by a.b0110) b");
						sql.append(" where "+tempTableName+".b0110=b.b0110  )");
						dao.update(sql.toString());
						
					}
					else if("0".equals(ctrl_peroid))
					{
						sql.append("update "+tempTableName+" set planitem=(select "+Planitem+" from "+setid+" where "+Sql_switcher.year(setid+"z0")+"="+year);
						sql.append(" "+subStr+" and "+Sql_switcher.month(setid+"z0")+"="+month+" and  "+tempTableName+".b0110="+setid+".b0110 ) where exists ( select null from ");
						sql.append(setid+" where "+Sql_switcher.year(setid+"z0")+"="+year);
						sql.append(" "+subStr+" and "+Sql_switcher.month(setid+"z0")+"="+month+" and  "+tempTableName+".b0110="+setid+".b0110 ");
						sql.append(" ) ");
						dao.update(sql.toString());
					}
					else if("2".equals(ctrl_peroid))
					{
						String temp_whl=getQuarterWhl(year,Integer.parseInt(month),setid);
						sql.append("update "+tempTableName+" set planitem=(select plan_data from ");
						sql.append("(select sum("+Sql_switcher.isnull("planData","0")+") plan_data ,b0110"); 
						sql.append(" from ( select "+Planitem+"   as planData ");
						sql.append(",b0110 from "+setid+"  where "+temp_whl+" "+subStr+" ) a group by a.b0110) b");
						sql.append(" where "+tempTableName+".b0110=b.b0110  ) where exists (select null from ");
						sql.append("(select sum("+Sql_switcher.isnull("planData","0")+") plan_data ,b0110"); 
						sql.append(" from ( select  "+Planitem+"  as planData "); 
						sql.append(",b0110 from "+setid+" where "+temp_whl+" "+subStr+" ) a group by a.b0110) b");
						sql.append(" where "+tempTableName+".b0110=b.b0110  )");
						dao.update(sql.toString());
					}
				}
				
				sql.setLength(0);
				String str=" and "+Sql_switcher.year(setid+"Z0")+"="+year+" and "+Sql_switcher.month(setid+"Z0")+"="+month;
				sql.append("update "+tempTableName+" set sp_flag=(select "+sp_flag+" from "+setid+"  where "+setid+".b0110="+tempTableName+".b0110 "+str+" "+subStr+" )");
				sql.append(" where exists (select null from "+setid+"  where "+setid+".b0110="+tempTableName+".b0110 "+str+" "+subStr+" )");
				dao.update(sql.toString());
				dao.delete("delete from "+tempTableName+" where sp_flag is null or ( sp_flag<>'04' and sp_flag<>'03' )  ",new ArrayList());
				sql.setLength(0);
				
				
				
				subStr="";
				if(fc_flag!=null&&fc_flag.trim().length()>0) //设置了封存标识
				{		
					subStr=" and "+Sql_switcher.isnull(fc_flag,"''")+"='1'  "; 
					String temp_whl="";
					//实发额-已封存的实发额
					temp_whl=" "+Sql_switcher.year(setid+"z0")+"="+year+" and "+Sql_switcher.month(setid+"z0")+"="+month; 
					/*
					if(ctrl_peroid .equals("1"))    //控制种类 1按年| 0按月 |2季度
						temp_whl=Sql_switcher.year(setid+"z0")+"="+year;
					else if(ctrl_peroid.equals("0"))
						temp_whl=" "+Sql_switcher.year(setid+"z0")+"="+year+" and "+Sql_switcher.month(setid+"z0")+"="+month; 
					else if(ctrl_peroid.equals("2"))
						temp_whl=getQuarterWhl(year,Integer.parseInt(month),setid);
					*/
					sql.append("update "+tempTableName+" set realitem=(select "+tempTableName+".realitem-"+Sql_switcher.isnull("real_data","0")+" from ");
					sql.append("(select sum("+Sql_switcher.isnull(Realitem,"0")+") real_data ,b0110");
					sql.append(" from ( select "+Realitem+" ,b0110 from "+setid+"  where "+temp_whl+" "+subStr+" ) a group by a.b0110) b");
					sql.append(" where "+tempTableName+".b0110=b.b0110  ) where exists (select null from ");
					sql.append("(select sum("+Sql_switcher.isnull(Realitem,"0")+") real_data ,b0110");
					sql.append(" from ( select "+Realitem+"  ,b0110 from "+setid+" where "+temp_whl+" "+subStr+" ) a group by a.b0110) b");
					sql.append(" where "+tempTableName+".b0110=b.b0110  )");
					dao.update(sql.toString());
				}	 
				
				if(!isComputeRPValue) //校验，不计算总额表中的剩余值和实际值
				{
						sql.setLength(0);	
						String subStr2="";
						if(fc_flag!=null&&fc_flag.trim().length()>0)
							subStr2=" and "+Sql_switcher.isnull(fc_flag,"''")+"<>'1'  ";
						if("1".equals(ctrl_peroid))    //控制种类 1按年| 0按月 |2季度
						{
							sql.append("update "+tempTableName+" set realitem=(select "+tempTableName+".realitem+"+Sql_switcher.isnull("real_data","0")+" from ");
							sql.append("(select sum("+Sql_switcher.isnull(Realitem,"0")+") real_data ,b0110");
							sql.append(" from ( select "+Realitem+" ,b0110 from "+setid+"  where "+Sql_switcher.year(setid+"z0")+"="+year+" and "+Sql_switcher.month(setid+"z0")+"<>"+month+"    "+subStr2+" ) a group by a.b0110) b");
							sql.append(" where "+tempTableName+".b0110=b.b0110  ) where exists (select null from ");
							sql.append("(select sum("+Sql_switcher.isnull(Realitem,"0")+") real_data ,b0110");
							sql.append(" from ( select "+Realitem+"  ,b0110 from "+setid+" where "+Sql_switcher.year(setid+"z0")+"="+year+" and "+Sql_switcher.month(setid+"z0")+"<>"+month+"  "+subStr2+" ) a group by a.b0110) b");
							sql.append(" where "+tempTableName+".b0110=b.b0110  )");
							dao.update(sql.toString());
							
						}
						else if("2".equals(ctrl_peroid))
						{
							String temp_whl=getQuarterWhl(year,Integer.parseInt(month),setid);
							sql.append("update "+tempTableName+" set realitem=(select  "+tempTableName+".realitem+"+Sql_switcher.isnull("real_data","0")+" from ");
							sql.append("(select sum("+Sql_switcher.isnull(Realitem,"0")+") real_data ,b0110");
							sql.append(" from ( select "+Realitem+"  ,b0110 from "+setid+"  where "+temp_whl+" and "+Sql_switcher.month(setid+"z0")+"<>"+month+"   "+subStr2+" ) a group by a.b0110) b");
							sql.append(" where "+tempTableName+".b0110=b.b0110  ) where exists (select null from ");
							sql.append("(select sum("+Sql_switcher.isnull(Realitem,"0")+") real_data ,b0110");
							sql.append(" from ( select "+Realitem+"  ,b0110 from "+setid+" where "+temp_whl+" and "+Sql_switcher.month(setid+"z0")+"<>"+month+"  "+subStr2+" ) a group by a.b0110) b");
							sql.append(" where "+tempTableName+".b0110=b.b0110  )");
							dao.update(sql.toString());
						}	
				}
					
				
				if(fc_flag!=null&&fc_flag.trim().length()>0)
				{	
					String surplus_compute=(String)this.gzXmlMap.get("surplus_compute"); //封存结余参与计算
					sql.setLength(0);	
					if(surplus_compute!=null&& "1".equals(surplus_compute)&&!isComputeRPValue)
					{
						if("1".equals(ctrl_peroid))    //控制种类 1按年| 0按月 |2季度
						{
							sql.append("update "+tempTableName+" set planitem=(select "+Sql_switcher.isnull("plan_data","0")+"+"+tempTableName+".planitem from ");
							sql.append("(select sum("+Sql_switcher.isnull("planData","0")+") plan_data ,b0110");
							sql.append(" from ( select "+balanceitem+" as planData,b0110 from "+setid+"  where "+Sql_switcher.year(setid+"z0")+"="+year+" "+subStr+" ) a group by a.b0110) b");
							sql.append(" where "+tempTableName+".b0110=b.b0110  ) where exists (select null from ");
							sql.append("(select sum("+Sql_switcher.isnull("planData","0")+") plan_data ,b0110");
							sql.append(" from ( select "+balanceitem+" as planData,b0110 from "+setid+" where "+Sql_switcher.year(setid+"z0")+"="+year+" "+subStr+" ) a group by a.b0110) b");
							sql.append(" where "+tempTableName+".b0110=b.b0110  )");
							dao.update(sql.toString());
							
						}
						else if("0".equals(ctrl_peroid))
						{
							String temp_whl=" "+Sql_switcher.year(setid+"z0")+"="+year+" and "+Sql_switcher.month(setid+"z0")+"="+month;
							sql.append("update "+tempTableName+" set planitem=(select "+Sql_switcher.isnull("plan_data","0")+"+"+tempTableName+".planitem from ");
							sql.append("(select sum("+Sql_switcher.isnull("planData","0")+") plan_data ,b0110");
							sql.append(" from ( select "+balanceitem+" as planData,b0110 from "+setid+"  where "+temp_whl+" "+subStr+" ) a group by a.b0110) b");
							sql.append(" where "+tempTableName+".b0110=b.b0110  ) where exists (select null from ");
							sql.append("(select sum("+Sql_switcher.isnull("planData","0")+") plan_data ,b0110");
							sql.append(" from ( select "+balanceitem+" as planData,b0110 from "+setid+" where "+temp_whl+" "+subStr+" ) a group by a.b0110) b");
							sql.append(" where "+tempTableName+".b0110=b.b0110  )");
							dao.update(sql.toString());
						}
						else if("2".equals(ctrl_peroid))
						{
							String temp_whl=getQuarterWhl(year,Integer.parseInt(month),setid);
							sql.append("update "+tempTableName+" set planitem=(select "+Sql_switcher.isnull("plan_data","0")+"+"+tempTableName+".planitem from ");
							sql.append("(select sum("+Sql_switcher.isnull("planData","0")+") plan_data ,b0110");
							sql.append(" from ( select "+balanceitem+" as planData,b0110 from "+setid+"  where "+temp_whl+" "+subStr+" ) a group by a.b0110) b");
							sql.append(" where "+tempTableName+".b0110=b.b0110  ) where exists (select null from ");
							sql.append("(select sum("+Sql_switcher.isnull("planData","0")+") plan_data ,b0110");
							sql.append(" from ( select "+balanceitem+" as planData,b0110 from "+setid+" where "+temp_whl+" "+subStr+" ) a group by a.b0110) b");
							sql.append(" where "+tempTableName+".b0110=b.b0110  )");
							dao.update(sql.toString());
						}
					}
				}
				//计划剩余项目
				if(!isComputeRPValue)
					dao.update("update "+tempTableName+" set remainitem="+Sql_switcher.isnull("planitem","0")+"-"+Sql_switcher.isnull("realitem","0"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @param formula    计算公式
	 * @param cond       计算条件
	 * @param fieldname  计算项目
	 * @param strWhere   整个人员过滤条件
	 */
	private String calcFormula_str(String formula,String cond,String fieldname,String strWhere)throws GeneralException
	{
		String sql="";
		try
		{
			String table=this.userView.getUserName()+"_salary_"+this.salaryid;
			String strfilter="";
			YksjParser yp=null;
			ContentDAO dao=new ContentDAO(this.conn);
			/**先对计算公式的条件进行分析*/
			if(!(cond==null|| "".equalsIgnoreCase(cond)))
			{
				yp = new YksjParser( this.userView ,fldvarlist,
						YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
				yp.run_where(cond);
				strfilter=yp.getSQL();
			}
			StringBuffer strcond=new StringBuffer();
			if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
				strcond.append(strWhere);
			if(!("".equalsIgnoreCase(strfilter)))
			{
				if(strcond.length()>0)
					strcond.append(" and ");
				strcond.append(strfilter);
			}
			/**进行公式计算*/
			FieldItem item=DataDictionary.getFieldItem(fieldname);

			yp=new YksjParser( this.userView ,fldvarlist,
					YksjParser.forNormal, getDataType(item.getItemtype()),YksjParser.forPerson , "Ht", ""); 
			yp.run(formula,this.conn,strcond.toString(),table);
			/**单表计算*/
			sql=yp.getSQL(); 
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
      	    throw GeneralExceptionHandler.Handle(ex);
		}
		return sql;
	}
	
	
	/**
	 * 数值类型进行转换
	 * @param type
	 * @return
	 */
	private int getDataType(String type)
	{
		int datatype=0;
		switch(type.charAt(0))
		{
		case 'A':  
			datatype=YksjParser.STRVALUE;
			break;
		case 'D':
			datatype=YksjParser.DATEVALUE;
			break;
		case 'N':
			datatype=YksjParser.FLOAT;
			break;
		}
		return datatype;
	}
	
	
	//取得组织机构信息
	private ArrayList getOrgList()
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from organization  where codesetid<>'@K' order by codeitemid ");
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				abean.set("codesetid", rowSet.getString("codesetid"));
				abean.set("codeitemid",  rowSet.getString("codeitemid"));
				abean.set("codeitemdesc",  rowSet.getString("codeitemdesc"));
				abean.set("parentid",  rowSet.getString("parentid"));
				list.add(abean);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	  //取得组织机构的部门层级
	private HashMap getUMLayer()
	{
			HashMap map=new HashMap();
			try
			{
				ContentDAO dao=new ContentDAO(this.conn);
				ArrayList orgList=getOrgList();
				RowSet rowSet=dao.search("select * from organization where codeitemid=parentid");
				while(rowSet.next())
				{
					String codeitemid=rowSet.getString("codeitemid");
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("codesetid", rowSet.getString("codesetid"));
					abean.set("codeitemid",  rowSet.getString("codeitemid"));
					abean.set("codeitemdesc",  rowSet.getString("codeitemdesc"));
					abean.set("parentid",  rowSet.getString("parentid"));
					
					setUMlayer(abean,map,0,orgList);
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return map;
	}
		
		
	private void setUMlayer(LazyDynaBean _abean,HashMap map,int lay,ArrayList orgList)
	{
			LazyDynaBean abean=null;
			LazyDynaBean abean2=null;
			ArrayList tempList=new ArrayList();

			abean=_abean;
			String codesetid=(String)abean.get("codesetid");
			String codeitemid=(String)abean.get("codeitemid");
			boolean isUm=false;
			int _lay=0;
			if("UM".equalsIgnoreCase(codesetid))
			{
					isUm=true;
					_lay=lay+1;
					map.put(codeitemid.toLowerCase(),String.valueOf(lay+1));
			}
				
			for(int j=0;j<orgList.size();j++)
			{
					abean2=(LazyDynaBean)orgList.get(j);
					String a_parentid=(String)abean2.get("parentid");
					String a_codeitemid=(String)abean2.get("codeitemid");
					if(!a_parentid.equals(a_codeitemid)&&a_parentid.equalsIgnoreCase(codeitemid))
					{
						tempList.add(abean2);
					}
			}

			for(int i=0;i<tempList.size();i++)
			{
				abean2=(LazyDynaBean)tempList.get(i);
				if(isUm)
				{
					setUMlayer(abean2,map,_lay,orgList);
				}
				else
					setUMlayer(abean2,map,0,orgList);
			} 
	}
	
	/**
	 * 判断是否需要计算总计(salaryhistory)
	 * @param belongTime 归属时间
	 * @return
	 */
	private boolean isCalculateTotal(LazyDynaBean belongTime,String whl,String tabname,StringBuffer info_str,String optDesc)
	{ 
		boolean flag=true; 
		String setid=(String)this.gzXmlMap.get("setid");
		String sp_flag=(String)this.gzXmlMap.get("sp_flag");
		String fc_flag=(String)this.gzXmlMap.get("fc_flag"); //封存状态指标 
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String year=(String)belongTime.get("year");
			String month=(String)belongTime.get("month");
			StringBuffer sql=new StringBuffer("select count(b0110) from "+setid);
			String field="b0110";
			if(this.belongUN!=null&&this.belongUN.length()>0)
				field=this.belongUN;
			if("0".equals(this.ctrl_type)) //(0|1)是否进行部门总额控制
			{
				field="e0122";
				if(this.belongUM!=null&&this.belongUM.length()>0)
					field=this.belongUM;
			}
			sql.append(" where  b0110 in (select distinct "+field+" from "+tabname+" where 1=1 "+whl+" )");
			
			if("2".equals(ctrl_peroid)) //获得某季度下的记录条件
			{
				sql.append(" and "+getQuarterWhl(year,Integer.parseInt(month),(String)this.gzXmlMap.get("setid")));
			}
			else
			{
				sql.append(" and "+Sql_switcher.year(setid+"z0")+"="+year);
				if("0".equals(ctrl_peroid))    //控制种类 1按年| 0按月 |2季度
				{
					sql.append(" and "+Sql_switcher.month(setid+"z0")+"="+month);
				}
			} 
			if(fc_flag!=null&&fc_flag.trim().length()>0) //设置了封存指标
			{
							sql.append(" and  "+Sql_switcher.isnull(fc_flag,"''")+"<>'1' ");
			} 
			String sql_str=sql.toString().replaceAll("count\\(b0110\\)"," distinct b0110 ")+" and "+sp_flag+"<>'04' and "+sp_flag+"<>'03'  ";
			rowSet=dao.search(sql_str);
			StringBuffer desc=new StringBuffer("");
			while(rowSet.next())
			{
			   String b0110_value=rowSet.getString("b0110");
			   if(b0110_value!=null&&b0110_value.length()>0)
			   {
				   if("0".equals(this.ctrl_type)) //部门
				   {
					   desc.append(","+AdminCode.getCodeName("UM",b0110_value));
				   }
				   else
				   {
					   desc.append(","+AdminCode.getCodeName("UN",b0110_value));
				   }
			   }
			}
			if(desc.length()>0)
			{ 
				info_str.append(desc.substring(1)+" 薪资总额没有批复" + "\r\n " + optDesc);
				return false;
			} 
			sql.append(" and ( "+sp_flag+"='04' or "+sp_flag+"='03' )");
		    rowSet=dao.search(sql.toString());
		    if(rowSet.next())
		    {
		    	if(rowSet.getInt(1)==0)
		    		return false;
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
		return flag;
	}
	
	
	/**
	 * 获得某季度下的记录条件
	 * @param year  年
	 * @param month  月
	 * @param setid 子集
	 * @return
	 */
	private String getQuarterWhl(String year,int month,String setid)
	{
		
		StringBuffer sql=new StringBuffer("");
		sql.append(Sql_switcher.year(setid+"z0")+"="+year);
		int one=1;int two=2;int three=3;
		if(month>3&&month<=6)
		{
			one=4;two=5;three=6;
		}
		else if(month>=7&&month<=9)
		{
			one=7;two=8;three=9;
		}
		else if(month>=10&&month<=12)
		{
			one=10;two=11;three=12;
		} 
		sql.append(" and  ("+Sql_switcher.month(setid+"z0")+"="+one);
		sql.append(" or "+Sql_switcher.month(setid+"z0")+"="+two);
		sql.append(" or "+Sql_switcher.month(setid+"z0")+"="+three);
		sql.append(")"); 
		return sql.toString();
	}
	 
	
	/**
	 * 取得数据中的归属日期列表
	 * @param tabname 表名
	 * @param whl 条件
	 * @return
	 */
	private ArrayList getBelongTimeList(String tabname,String whl)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String  sql="select distinct a00z0 from "+tabname+" where 1=1 "+whl;
			RowSet rowSet=dao.search(sql);
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				Calendar c=Calendar.getInstance();
				Date d=rowSet.getDate(1);
				c.setTime(d);
				abean=new LazyDynaBean();
				abean.set("year",String.valueOf(c.get(Calendar.YEAR)));
				abean.set("month",String.valueOf(c.get(Calendar.MONTH)+1));
				abean.set("day",String.valueOf(c.get(Calendar.DATE)));
				list.add(abean);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 根据条件获取选中人的数据信息
	 * zhaoxg 2016-1-13
	 * @param whl
	 * @param set
	 * @param flag
	 * @return
	 */
	public ArrayList getDateList(String whl,HashSet set,boolean flag)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			
			String field="b0110";
			if(this.belongUN!=null&&this.belongUN.length()>0)
				field=this.belongUN;
			if("0".equals(this.ctrl_type))
			{
				field="e0122";
				if(this.belongUM!=null&&this.belongUM.length()>0)
					field=this.belongUM;
			}
			
			String tableName="salaryhistory";
			if(!flag)
				tableName=this.userView.getUserName()+"_salary_"+this.salaryid;
			for(Iterator t=set.iterator();t.hasNext();)
			{
				String str=(String)t.next();
				String[] strs=str.split("-");				
				StringBuffer subwhl=new StringBuffer("");
				
				subwhl.append(" and "+Sql_switcher.year("a00z0")+"="+strs[0]);
				subwhl.append(" and "+Sql_switcher.month("a00z0")+"="+strs[1]+" and a00z1="+strs[3]);	
				
				StringBuffer uns=new StringBuffer("");
				RowSet rowSet=dao.search("select distinct "+field+" from "+tableName+"  where 1=1 "+subwhl.toString()+whl);
				while(rowSet.next())
				{
					uns.append(",'"+rowSet.getString(1)+"'");
				}
				if(uns.length()>0)
				{
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("year",strs[0] );
					abean.set("month", strs[1]);
					abean.set("whl",uns.substring(1));
					list.add(abean);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 取得当前日期下 薪资历史表中无记录的组织机构
	 * @param whl
	 * @param belongTime
	 * @return
	 */
	public String getEmptyRecordsUns(String whl,LazyDynaBean belongTime)
	{
		StringBuffer s=new StringBuffer("");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			
			String year=(String)belongTime.get("year");
			String month=(String)belongTime.get("month");
			StringBuffer whl_=new StringBuffer("");
			whl_.append(" and "+Sql_switcher.year("a00z0")+"="+year);
			whl_.append(" and "+Sql_switcher.month("a00z0")+"="+month);	
			String field="b0110";
			if(this.belongUN!=null&&this.belongUN.length()>0)
				field=this.belongUN;
			if("0".equals(this.ctrl_type))
			{
				field="e0122";
				if(this.belongUM!=null&&this.belongUM.length()>0)
					field=this.belongUM;
			}
			RowSet rowSet=dao.search("select distinct "+field+" from salaryhistory where 1=1 "+whl_.toString()+" and "+field+" in ("+whl+")");
			StringBuffer str=new StringBuffer("");
			while(rowSet.next())
			{
				String value=rowSet.getString(1);
				str.append(",'"+value+"'");
			}
			String sql="select codeitemid from organization where codeitemid in ("+whl+")   ";
			if(str.length()>0)
				sql+=" and codeitemid not in ("+str.substring(1)+")";
			rowSet=dao.search(sql);
			while(rowSet.next())
				s.append(",'"+rowSet.getString(1)+"'");
			if(s.length()>0)
				return s.substring(1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return s.toString();
	}
	/**
	 * 判断是否需要计算总计(salaryhistory)
	 * @param belongTime 归属时间
	 * @return
	 */
	public boolean isCalculateTotal3(LazyDynaBean belongTime,String whl)
	{
		boolean flag=true;
		if(this.controlList.size()==0)
			return false;
		String setid=(String)this.gzXmlMap.get("setid");
		if(setid==null||setid.length()==0)  //如果没有设置工资总额子集
			return false;
		String sp_flag=(String)this.gzXmlMap.get("sp_flag");
		if(sp_flag==null||sp_flag.length()==0)  //如果没有设置审批状态标识
			return false;
		
		String fc_flag=(String)this.gzXmlMap.get("fc_flag"); //封存状态指标 
		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String year=(String)belongTime.get("year");
			String month=(String)belongTime.get("month");
			
			StringBuffer sql=new StringBuffer("select count(b0110) from "+setid);
			sql.append(" where  b0110 in ("+whl+")");
			if("2".equals(ctrl_peroid))
			{
				sql.append(" and "+getQuarterWhl(year,Integer.parseInt(month),(String)this.gzXmlMap.get("setid")));
			}
			else
			{
				sql.append(" and "+Sql_switcher.year(setid+"z0")+"="+year);
				if("0".equals(ctrl_peroid))    //控制种类 1按年| 0按月 |2季度
				{
					sql.append(" and "+Sql_switcher.month(setid+"z0")+"="+month);
				}
			}
			sql.append(" and ("+sp_flag+"='04' or "+sp_flag+"='03' )");
			
			if(fc_flag!=null&&fc_flag.trim().length()>0) //设置了封存指标
			{
				sql.append(" and  "+Sql_switcher.isnull(fc_flag,"''")+"<>'1' ");
			}
			
		    RowSet rowSet=dao.search(sql.toString());
		    if(rowSet.next())
		    {
		    	if(rowSet.getInt(1)==0)
		    		return false;
		    }
		    
		    if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 工资总额重新计算
	 * @param dateList   
	 */
	public void calculateTotalSum(ArrayList dateList)
	{
		try
		{
			for(int i=0;i<dateList.size();i++)
			{
				LazyDynaBean dataBean=(LazyDynaBean)dateList.get(i);
				String whl=(String)dataBean.get("whl");
				
				String ss=getEmptyRecordsUns(whl,dataBean);
				if(isCalculateTotal3(dataBean,whl))
				{
					importTotalTempTable(dataBean,whl,ss);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 *判断是否超过 工资总额计划值 
	 * @return
	 */
	public void importTotalTempTable(LazyDynaBean belongTime,String whl,String ss)
	{
		String tempTableName="t#"+userView.getUserName()+"_gz_2"; //this.userView.getUserName()+"_tempTotlalSalary";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			for(int i=0;i<controlList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)controlList.get(i);
				String Realitem=(String)abean.get("realitem");  //实发项目
				String formular=(String)abean.get("formular");  //计算公式
				String Planitem=(String)abean.get("planitem");  //计划项目
				String _salaryid=","+((String)abean.get("salaryid")).toLowerCase()+",";
				boolean flag=false;
				if(this.salaryid!=0)
				{
					if(_salaryid.indexOf(","+this.salaryid+",")!=-1)
					  flag=true;
				}
				if(!flag)
					continue;
				
				String year=(String)belongTime.get("year");
				String month=(String)belongTime.get("month");
				StringBuffer whl2=new StringBuffer("");
				String setid=(String)this.gzXmlMap.get("setid");
				whl2.append(" and "+Sql_switcher.year(setid+"z0")+"="+year);
				whl2.append(" and "+Sql_switcher.month(setid+"z0")+"="+month);
				
				String balanceitem=(String)abean.get("balanceitem");
				dao.update("update "+this.totalTable+" set "+Realitem+"=(select realitem from "+tempTableName+" where "+this.totalTable+".b0110="+tempTableName+".b0110 ) where 1=1 "+whl2.toString()+" and b0110 in (select b0110 from "+tempTableName+" ) ");
				dao.update("update "+this.totalTable+" set "+balanceitem+"=("+Planitem+"-"+Realitem+") where 1=1  "+whl2.toString()+" and b0110 in (select b0110 from "+tempTableName+" )");
				
				if(ss.length()>0)
				{
					dao.update("update "+this.totalTable+" set "+Realitem+"=0 where 1=1 "+whl2.toString()+" and b0110 in ("+ss+" ) ");
					dao.update("update "+this.totalTable+" set "+balanceitem+"=("+Planitem+"-"+Realitem+") where 1=1  "+whl2.toString()+" and b0110 in ("+ss+"  )");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
	}
	
	/**
	 * 判断是否设置了总额参数设置条件
	 * @return
	 */
	public String isSetTotalCondition() {
		String tipsData = "";
		try {
			if(this.gzXmlMap == null) {
				tipsData = "薪资总额参数未定义";
			}else {	
				if(StringUtils.isBlank(this.totalTable.trim())) {
					tipsData = "薪资总额参数未定义";
					return tipsData;
				}
				ArrayList dataList=(ArrayList) this.gzXmlMap.get("ctrl_item");
				if(dataList == null || dataList.size() == 0) {
					tipsData = "薪资总额参数未定义计划项目，实发项目和剩余项目参数!";
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return tipsData;
	}
	
	public SalaryTemplateBo getSalaryTemplateBo() {
		return salaryTemplateBo;
	}

	public void setSalaryTemplateBo(SalaryTemplateBo salaryTemplateBo) {
		this.salaryTemplateBo = salaryTemplateBo;
	}
	
	public HashMap getGzXmlMap() {
		return gzXmlMap;
	}


}
