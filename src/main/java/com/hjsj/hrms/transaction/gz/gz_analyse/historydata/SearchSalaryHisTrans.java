package com.hjsj.hrms.transaction.gz.gz_analyse.historydata;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.ResultSetMetaData;
import java.util.*;

/**
 * <p>
 * Title:SearchSalaryHisTrans
 * </p>
 * <p>
 * Description:查询归档数据
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:2009-6-18:下午02:03:54
 * </p>
 * 
 * @author fanzhiguo
 * @version 1.0
 */
public class SearchSalaryHisTrans extends IBusiness
{
    public void execute() throws GeneralException
	{
    	try{
    		this.userView.getHm().put("gz_filterWhl","");
    		String[] salaryids = (String[]) this.getFormHM().get("salaryids");

    		boolean isNewVersion=this.userView.getVersion()>=70?true:false;

			HistoryDataBo hbo = new HistoryDataBo(this.frameconn, this.userView);


			/** 项目过滤条件号，全部为all */
    		String itemid = (String) this.getFormHM().get("itemid");

    		if(StringUtils.isBlank(itemid))
				itemid="all";
    		if (itemid == null || "".equalsIgnoreCase(itemid))
    		    itemid = "all";
			String condid=(String)this.getFormHM().get("cond_id_str");
			if(condid==null|| "".equalsIgnoreCase(condid))
				condid=(String)this.getFormHM().get("condid");
			if(condid==null|| "".equalsIgnoreCase(condid))
				condid="all";
    	
    		ArrayList itemfilterlist2 = new ArrayList();// 获得项目过滤
    		ArrayList manfilterlist2 = new ArrayList();// 获得人员筛选条件
    		HashMap allFieldsMap = new HashMap();// 项目过滤为所有项目时候的字段
    		CommonData allItem = new CommonData("all", ResourceFactory.getProperty("label.gz.allman"));
    		manfilterlist2.add(allItem);
    		allItem = new CommonData("all", ResourceFactory.getProperty("label.gz.allitem"));
    		itemfilterlist2.add(allItem);
    		/** 薪资类别 */
    		String reportSalaryid = "",salary_Id="";
    		HashMap<String,Field> fieldlistAll=new HashMap<String,Field> ();


    		for (int i = 0; i < salaryids.length; i++)
    		{
    		    String salaryid = salaryids[i];
    		    reportSalaryid = salaryids[i];
				salary_Id+=salaryids[i]+",";
    		    SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(), Integer.parseInt(salaryid), this.userView);
    		    if(!isNewVersion) {
					String filterid = gzbo.getFiltersIds(salaryid);
					ArrayList itemfilterlist = gzbo.getItemFilterList(filterid);
					for (int k = 0; k < itemfilterlist.size(); k++) {
						CommonData temp = (CommonData) itemfilterlist.get(k);
						if ("new".equals(temp.getDataValue()) || "all".equals(temp.getDataValue()))
							continue;
						else
							itemfilterlist2.add(new CommonData(salaryid + ":" + temp.getDataValue(), temp.getDataName()));
					}
					ArrayList manfilterlist = gzbo.getManFilterList();
					for (int j = 0; j < manfilterlist.size(); j++) {
						CommonData temp = (CommonData) manfilterlist.get(j);
						if ("new".equals(temp.getDataValue()) || "all".equals(temp.getDataValue()))
							continue;
						else
							manfilterlist2.add(new CommonData(salaryid + ":" + temp.getDataValue(), temp.getDataName()));
					}
				}
				ArrayList fieldlist = gzbo.getFieldlist();
				for (int i1 = 0; i1 < fieldlist.size(); i1++) {
					Field field=(Field)fieldlist.get(i1);
					fieldlistAll.put(field.getName(),field);
				}

				if ("all".equalsIgnoreCase(itemid))
    		    {
    			for (int m = 0; m < fieldlist.size(); m++)
    			{
    			    Field field = (Field) fieldlist.get(m);
    			    allFieldsMap.put(field.getName(), field);
    			}
    		    }
    		}

			SalaryTemplateBo gzbo1 = new SalaryTemplateBo(this.getFrameconn());
    		if(isNewVersion){
				String filterId=hbo.getFilterIdFromHistory();
				if(filterId.endsWith(",")) {
					filterId = filterId.substring(0, filterId.length() - 1);
				}
				gzbo1.setUserview(this.getUserView());
				itemfilterlist2 = gzbo1.getItemFilterList(filterId);

				manfilterlist2.clear();
				manfilterlist2.addAll(hbo.searchManFilterFromHistory());
			}

    	
    		// 项目过滤
    		this.getFormHM().put("itemlist", itemfilterlist2);
    		this.getFormHM().put("itemid", itemid);
    	
    		// 人员筛选
    		this.getFormHM().put("condid", condid);
    		this.getFormHM().put("condlist", manfilterlist2);
    		/** 选择一个或多个工资类别，当选择一个时才出现报表按钮 */
    		this.getFormHM().put("isOnlySet", salaryids.length > 1 ? "1" : "0");
    		this.getFormHM().put("salaryid", salaryids.length > 1 ?salary_Id:reportSalaryid);
    		/** 业务日期和业务次数 */
    		String bosdate = (String) this.getFormHM().get("bosdate");
    		String count = (String) this.getFormHM().get("count");
    		String b_units=this.userView.getUnitIdByBusiOutofPriv("1");
    		if(b_units.length()==0|| "UN".equalsIgnoreCase(b_units)){
    			
    			if(!(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId())))   //20160229 dengcan
    				throw GeneralExceptionHandler.Handle(new Exception("管理员没有给您授权业务范围，您无权查看数据！"));
    		}
    		ArrayList datelist = hbo.getOperationDateList(salaryids);
    		if ((bosdate == null || "".equalsIgnoreCase(bosdate)) && datelist.size() > 0)
    		{
    		    bosdate = ((CommonData) datelist.get(0)).getDataValue();
    		} else if (bosdate == null || "".equalsIgnoreCase(bosdate))
    		    bosdate = PubFunc.FormatDate(new Date(), "yyyy.MM.dd");
    		else
    		{
    		    boolean isExist = false;
    		    for (int i = 0; i < datelist.size(); i++)
    		    {
    				CommonData data = (CommonData) datelist.get(i);
    				if (data.getDataValue().equalsIgnoreCase(bosdate))
    				    isExist = true;
    		    }
    		    if (!isExist && datelist.size() > 0)
    		    	bosdate = ((CommonData) datelist.get(0)).getDataValue();
    		    if (!isExist && datelist.size() == 0)
    		    	bosdate = PubFunc.FormatDate(new Date(), "yyyy.MM.dd");
    		}
    		this.getFormHM().put("bosdate", bosdate);
    		this.getFormHM().put("datelist", datelist);
    	
    		ArrayList countlist = hbo.getOperationCountList(salaryids, bosdate);
    		if ((count == null || "".equalsIgnoreCase(count)) && countlist.size() > 0)
    		    count = ((CommonData) countlist.get(countlist.size() - 1)).getDataValue();
    		else if (count == null || "".equalsIgnoreCase(count))
    		    count = "1";
    		else
    		{
    		    boolean isExist = false;
    		    for (int i = 0; i < countlist.size(); i++)
    		    {
    			CommonData data = (CommonData) countlist.get(i);
    			if (data.getDataValue().equalsIgnoreCase(count))
    			    isExist = true;
    		    }
    		    if (!isExist && countlist.size() > 0)
    			count = ((CommonData) countlist.get(countlist.size() - 1)).getDataValue();
    		    else if (!isExist && countlist.size() == 0)
    			count = "1";
    	
    		}
    		this.getFormHM().put("countlist", countlist);
    		this.getFormHM().put("count", count);
    		String salaryid = "";
    		ArrayList fieldlist = new ArrayList();
    		// 确定要展示的字段
    		if (!"all".equalsIgnoreCase(itemid))
    		{
    			if(isNewVersion){
					ArrayList list=new ArrayList();
					Iterator i = fieldlistAll.entrySet().iterator();
					while(i.hasNext()){
						Map.Entry entry=(Map.Entry)i.next();
						list.add(entry.getValue());
					}
					fieldlist = gzbo1.filterItemList(list, itemid);
				}else {
					String[] temp = itemid.split(":");
					salaryid = temp[0];
					itemid = temp[1];
					SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(), Integer.parseInt(salaryid), this.userView);
					fieldlist = gzbo.getFieldlist();
					fieldlist = gzbo.filterItemList(fieldlist, itemid);
				}
    		} else
    		{
    		    ArrayList allSalaryItems = hbo.getSalaryItems(salaryids);
    		    for (int i = 0; i < allSalaryItems.size(); i++)
    		    {
    			String item = (String) allSalaryItems.get(i);
    			if (allFieldsMap.get(item) != null)		
    			    fieldlist.add(allFieldsMap.get(item));		
    		    }
    		}
    		String fieldStr = "";
    		// 归档表中有的字段才能显示
    		ArrayList fieldlist2 = new ArrayList();
    		HashMap map = hbo.getAllFlds();
    		for (int i = 0; i < fieldlist.size(); i++)
    		{
    		    Field field = (Field) fieldlist.get(i);
    		    String fieldname = field.getName();
    		    if (map.get(fieldname.toUpperCase()) != null)
    		    {
    			field.setReadonly(true);
    			fieldlist2.add(field);
    			if (field.isVisible())
    			    fieldStr += "," + fieldname + ":" + field.getLabel();
    		    }
    		}
    		this.getFormHM().put("fieldStr", fieldStr.length() > 0 ? fieldStr.substring(1) : "");
    	
    		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
    		String a_code = (String) hm.get("a_code");
    		this.getFormHM().put("a_code", a_code);
    	
    		/** 薪资归档数据过滤 */
    		StringBuffer buf = new StringBuffer();
    		buf.append("select * ");
    		buf.append(" from salaryarchive where 1=1 ");
    	
    		buf.append(" and A00Z3=");
    		buf.append(count);
    		buf.append(" and A00Z2=");
    		buf.append(Sql_switcher.dateValue(bosdate));
    		if (!(a_code == null || "".equalsIgnoreCase(a_code)))
    		{
    			String b0110_item="b0110";
    			String e0122_item="e0122";
    			buf.append(" and (");
    			for (int i = 0; i < salaryids.length; i++){//选择多个薪资类别时，要根据每个类别的归属单位及归属部门来进一步过滤  zhaoxg 2013-9-5 add
    				b0110_item="b0110";
    				e0122_item="e0122";
    				if(i==0){
    					buf.append("  (salaryid = ");
    				}else{
    					buf.append(" or (salaryid = ");
    				}
    				
    			    buf.append(salaryids[i]);
    	
    				SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryids[i]));
    				String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid"); //归属单位
    				String deptid =ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");//归属部门
    				
    				if(orgid!=null&&orgid.trim().length()>0&&isHave(orgid))
    				{ 
    					 b0110_item=orgid;
    					if(deptid!=null&&deptid.trim().length()>0&&isHave(deptid))
    						e0122_item=deptid;
    					else
    						e0122_item="";  
    				}
    				else if(deptid!=null&&deptid.trim().length()>0&&isHave(deptid))
    				{ 
    					e0122_item=deptid;
    					b0110_item="";
    				}
    			
    			
    			
    		    String codesetid = a_code.substring(0, 2);
    		    String value = a_code.substring(2);
    		    if ("UN".equalsIgnoreCase(codesetid))
    		    {
    		    	
    		    	if(b0110_item.length()>0)	
    		    	{
    					buf.append(" and ("+b0110_item+" like '");
    					buf.append(value);
    					buf.append("%'");
    					if ("".equalsIgnoreCase(value))
    					{
    					    buf.append(" or "+b0110_item+" is null");
    					}
    					buf.append(")");
    		    	}
    		    	else 
    		    	{
    		    		buf.append(" and ("+e0122_item+" like '");
    					buf.append(value);
    					buf.append("%'");
    					if ("".equalsIgnoreCase(value))
    					{
    					    buf.append(" or "+e0122_item+" is null");
    					}
    					buf.append(")");
    		    	}
    		    }
    		    if ("UM".equalsIgnoreCase(codesetid))
    		    {
    		    	if(e0122_item.length()>0)	
    		    	{
    					buf.append(" and "+e0122_item+" like '");
    					buf.append(value);
    					buf.append("%'");
    		    	}
    		    }
    		    buf.append(")");
    		}
    			buf.append(")");
    		}else{
    			buf.append(" and (");
    			for (int i = 0; i < salaryids.length; i++){
    				if(i==0){
    					buf.append("  (salaryid = ");
    				}else{
    					buf.append(" or (salaryid = ");
    				}
    				
    			    buf.append(salaryids[i]);
    			    buf.append(")");
    			}
    			buf.append(")");
    		}
    		 
    		String priv_str=hbo.getPrivPre_His(salaryids);// hbo.getPrivPre("1");
    		
    		// 加上人员过滤条件
    		String swhere = " 1=1 ";
    		if (!"all".equalsIgnoreCase(condid)&&!"new".equalsIgnoreCase(condid))
    		{
    			if(isNewVersion) {
    				swhere=this.getEmpFilterSql(condid);
				}else{
					String[] temp = condid.split(":");
					salaryid = temp[0];
					condid = temp[1];
					SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(), Integer.parseInt(salaryid), this.userView);
					swhere = gzbo.getFilterWhere(condid, "salaryarchive");

				}
    		}

			String empfiltersql = SafeCode.decode((String)this.getFormHM().get("empfiltersql"));
			empfiltersql= PubFunc.decrypt(empfiltersql);
			if(!(empfiltersql==null || "".equals(empfiltersql)))
			{
				swhere+=" and ("+empfiltersql+")";
			}
			else
			{
				if("new".equalsIgnoreCase(condid))
				{
					condid="all";
				}
			}
    		
    		
    		
    		if (swhere.length() > 0)
    		{
    		    buf.append(" and ");
    		    buf.append("(" + swhere + ")");
    		}
    		
    		if (priv_str.length() > 0)
    		{
    		    buf.append(" and ");
    		    buf.append("(" + priv_str + ")");
    		}
    		
    		
    		buf.append(" order by nbase,a0000, A00Z0, A00Z1"); 
    		this.getFormHM().put("sql", PubFunc.encrypt(buf.toString()));
    		this.getFormHM().put("condid", condid);
			this.getFormHM().put("empfiltersql", "");
    		//this.getFormHM().put("cond_id_str", condid);
    		this.getFormHM().put("fieldlist", fieldlist2);
    	}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
    /**
     * 判断库中是否含有指定的字段
     * @param str
     * @return
     */
    public boolean isHave(String str){
    	boolean flag=false;
    	try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			RowSet rowSet =dao.search("select * from salaryarchive where 1=2");
			ResultSetMetaData data=rowSet.getMetaData();
			for(int j=1;j<=data.getColumnCount();j++)
			{
				String columnName=data.getColumnName(j).toLowerCase();
				if(str.equalsIgnoreCase(columnName)){
					flag=true;
				}
			}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return flag;
    }

    private String getEmpFilterSql(String condid) throws GeneralException {
    	String strSql="";
    	try{
    		String expr="",factor="";
    		HistoryDataBo hbo=new HistoryDataBo(this.getFrameconn(),this.getUserView());
    		ArrayList<Field> allFields =hbo.searchAllGzItem();
    		HashMap<String,String> filterMap=hbo.getServiceItemMapFromHistory();
    		if(filterMap.containsKey(condid)){
				expr=filterMap.get(condid).split("\\|")[0];
				factor=filterMap.get(condid).split("\\|")[1];
			}else{
    			return "";
			}
    		HashMap fieldItemMap=new HashMap();

			for (Field field : allFields) {
				FieldItem item = new FieldItem();
				item.setCodesetid(field.getCodesetid());
				item.setUseflag("1");
				if(field.getDatatype()== DataType.DATE)
				{
					item.setItemtype("D");
				}
				else if(field.getDatatype()==DataType.STRING)
				{
					item.setItemtype("A");
				}
				else if(field.getDatatype()==DataType.INT||field.getDatatype()==DataType.FLOAT)
				{
					item.setItemtype("N");
				}
				else if(field.getDatatype()==DataType.CLOB)
				{
					item.setItemtype("M");
				}
				else
					item.setItemtype("A");
				item.setItemid(field.getName().toUpperCase());
				item.setAlign(field.getAlign());
				item.setItemdesc(field.getLabel());

				fieldItemMap.put(field.getName().toUpperCase(),item);
			}

			FactorList factor_bo=new FactorList(expr,factor,this.getUserView().getUserId(),fieldItemMap);
			strSql=factor_bo.getSingleTableSqlExpression("salaryarchive");


    	}catch (Exception e){
    	    e.printStackTrace();
    	}
    	return  strSql;
	}
}
