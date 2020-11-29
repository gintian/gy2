package com.hjsj.hrms.businessobject.performance.objectiveManage;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.*;

public class ImportDeptFieldBo {
	
	Connection connection =null;
	UserView userView=null;
	private String DeptDutySubSet="";//部门职责子集
	private String DeptDutyTargetItem="";//部门职责子集中项目指标
	private String DeptDutyDataItem="";//部门职责子集中 时间指标
	private String DeptDutyTextValue="";//部门职责子集中与目标卡对应指标

	private RecordVo plan_vo;
	private Hashtable planParam=null;
	ContentDAO dao = null;
	public ImportDeptFieldBo(Connection connection,UserView userView,String planid){
		try{
			this.connection=connection;
			this.userView=userView;
			plan_vo=new RecordVo("per_plan");
			plan_vo.setInt("plan_id", Integer.parseInt(planid));
			dao=new ContentDAO(connection);
			plan_vo=dao.findByPrimaryKey(plan_vo);
			AnalysePlanParameterBo bo=new AnalysePlanParameterBo(this.connection);
			Hashtable ht_table=bo.analyseParameterXml();
			if(ht_table!=null)
			{
				if(ht_table.get("departDutySet")!=null)
					this.DeptDutySubSet=(String)ht_table.get("departDutySet");
			    if(ht_table.get("projectField")!=null)
			    	this.DeptDutyTargetItem=(String)ht_table.get("projectField");
			    if(ht_table.get("validDateField")!=null)
			    	this.DeptDutyDataItem=(String)ht_table.get("validDateField");
			    if(ht_table.get("departTextValue")!=null)
			    	this.DeptDutyTextValue=(String)ht_table.get("departTextValue");
			    if (this.DeptDutyTextValue!=null)
			    	this.DeptDutyTextValue=this.DeptDutyTextValue.replace("＝", "="); 
			}
			LoadXml loadxml=null;
			if(BatchGradeBo.getPlanLoadXmlMap().get(String.valueOf(this.plan_vo.getInt("plan_id")))==null)
			{
				loadxml=new LoadXml(this.connection,String.valueOf(this.plan_vo.getInt("plan_id")));
				BatchGradeBo.getPlanLoadXmlMap().put(String.valueOf(this.plan_vo.getInt("plan_id")),loadxml);
			}
			else
				loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(String.valueOf(this.plan_vo.getInt("plan_id")));		
			this.planParam=loadxml.getDegreeWhole(); 
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 查找考核对象的部门，还要找到部门的上级部门
	 * @param object_id
	 * @param nbase
	 * @return
	 */
	public String getDeptString(String object_id,String nbase)
	{
		String deptString="";
		RowSet rs=null;
		try
		{
			String oid = object_id;
			if(this.plan_vo.getInt("object_type")==2){//人员计划，要找考核对象的部门
				String sql = "select e0122 from "+nbase+"a01 where a0100='"+oid+"'";
				rs = dao.search(sql);
				while(rs.next())
				{
					oid=rs.getString("e0122")==null?"":rs.getString("e0122");;
				}
			}
			StringBuffer buf = new StringBuffer();
			buf.append(" select codeitemid from organization where codeitemid=(select parentid from organization where codeitemid='"+oid+"') and UPPER(codesetid)='UM'");
			rs= dao.search(buf.toString());
			if(rs.next())
			{
				deptString=rs.getString("codeitemid")+","+oid;
			}else{
				deptString=oid;
			}		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return deptString;
	}
	
	/**
	 * 取得已选择的部门任务
	 * @param itemid
	 * @param object_id
	 * @return
	 */
	public HashMap getSelectedDeptField(String itemid,String object_id,int type,String i9Str,String ids)
	{
		HashMap map = new HashMap();
		RowSet rs=null;
		try
		{
			if(type==1){
				String sql = "select p0401,chg_type,state  from p04 where item_id="+itemid+"  and fromflag=5 and plan_id="+this.plan_vo.getInt("plan_id");
				if(this.plan_vo.getInt("object_type")!=2)
					sql+=" and b0110='"+object_id+"'";
				else
					sql+=" and a0100='"+object_id+"'";
				 rs = dao.search(sql);
				while(rs.next())
				{
					String temp=rs.getString(1);
					if(temp==null|| "".equals(temp)||temp.indexOf("_")==-1)
						continue;
					String chg_type=rs.getString(2);
					String state = rs.getString(3);
					/**删除的任务还可以导入*/
					if(chg_type!=null&& "3".equals(chg_type)&&state!=null&& "-1".equals(state))
						continue;
					String strs = temp.split("_")[1]; // i9999号码        判断是否是数字 JinChunhai 2011.05.24
					String str2=temp.split("_")[0];//部门编码
					if(!isNumber(strs))
						continue;
					if(map.get(str2.toUpperCase())!=null){
						String valueString=(String)map.get(str2.toUpperCase());
						valueString+=","+strs;
						map.put(str2.toUpperCase(), valueString);
					}else{
						String valueString=strs;
						map.put(str2.toUpperCase(), valueString);
					}
				}
			}else{
				String[] arrStrings=i9Str.split("`");
				String[] arrStrings2=ids.split("`");
				for(int i=0;i<arrStrings2.length;i++){
					if(arrStrings2[i]==null|| "".equals(arrStrings2[i]))
						continue;
					if(map.get(arrStrings2[i].toUpperCase())!=null){
						String valueString=(String)map.get(arrStrings2[i].toUpperCase());
						valueString+=","+arrStrings[i];
						map.put(arrStrings2[i].toUpperCase(), valueString);
					}else{
						String valueString=arrStrings[i];
						map.put(arrStrings2[i].toUpperCase(), valueString);
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try {
				if(rs!=null)
					rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return map;
	}
	public boolean isNumber(String str) 
	{
		return str.matches("[\\d]+");
	}
	public ArrayList getDeptFieldList(String itemid,HashMap i9999,String deptStringid,int type)
	{
		ArrayList list = new ArrayList();
		int tableWidth=0;
		String isHaveRecord="0";
		String alertMessage="";
		RowSet rs =null;
		try
		{
			
			StringBuffer i9_buf=new StringBuffer("");
			Set keySet = i9999.keySet();
			Iterator tIterator=keySet.iterator();
			while(tIterator.hasNext()){
				String valueString=(String)tIterator.next();
				String valueString2=(String)i9999.get(valueString);
				if(type==1){//页面上选择后，找到页面上选择的记录
			    	i9_buf.append(" or (b0110='"+valueString.toUpperCase()+"' and i9999 in ("+valueString2+"))");
				}else{
					i9_buf.append(" or (b0110='"+valueString.toUpperCase()+"' and i9999 not in ("+valueString2+"))");
				}
			}
			HashMap fieldMap = new HashMap();
			if(DeptDutyTextValue!=null&&!"".equals(DeptDutyTextValue.trim())){
				String[] arrStrings=DeptDutyTextValue.split(",");
				for(int i=0;i<arrStrings.length;i++){
					String arrString=arrStrings[i];
					if(arrString==null|| "".equals(arrString))
						continue;
					fieldMap.put(arrString.split("=")[0].toUpperCase(), arrString.split("=")[1]);
				}
			}
			RecordVo vo = new RecordVo("per_template_item");
			vo.setInt("item_id", Integer.parseInt(itemid));
			vo=dao.findByPrimaryKey(vo);
			String itemdesc=vo.getString("itemdesc");
			ArrayList fieldList=DataDictionary.getFieldList("P04",Constant.USED_FIELD_SET);
			ArrayList alist = new ArrayList();
			StringBuffer column=new StringBuffer();
			for(int i=0;i<fieldList.size();i++)
			{
				FieldItem item=(FieldItem)fieldList.get(i);
				if(fieldMap.get(item.getItemid().toUpperCase())!=null)
				{
					String xx=(String)fieldMap.get(item.getItemid().toUpperCase());
					FieldItem a_item = DataDictionary.getFieldItem(xx.toLowerCase());
					if(a_item==null|| "0".equals(a_item.getUseflag())|| "0".equals(item.getUseflag()))
						continue;
					if(!a_item.getFieldsetid().equalsIgnoreCase(this.DeptDutySubSet))
					{
						list.add(0,new ArrayList());
		    			list.add(1,new ArrayList());
		    			list.add(2,tableWidth+"");
		    			isHaveRecord="1";
		    			alertMessage="目标卡部门职责参数设置错误，请到参数设置中重新设置参数并保存！";
		    			list.add(3,isHaveRecord);
		  		        list.add(4,alertMessage);
		  		        return list;	
					}
					LazyDynaBean bean = new LazyDynaBean();
					int tdwidth=a_item.getItemlength()+a_item.getDecimalwidth();
					if(item.getItemdesc().length()*2>tdwidth)
						tdwidth=item.getItemdesc().length();
					tableWidth+=tdwidth;
					bean.set("tdwidth", tdwidth+"px");
					bean.set("itemid", a_item.getItemid().toLowerCase());
					bean.set("itemid_1", item.getItemid().toLowerCase());
					if("p0407".equalsIgnoreCase(item.getItemid()))
					{
						if(this.planParam.get("TaskNameDesc")!=null&&!"".equals((String)this.planParam.get("TaskNameDesc")))
						{
							bean.set("itemdesc",(String)this.planParam.get("TaskNameDesc"));
						}
						else{
				    		if("任务内容".equalsIgnoreCase(item.getItemdesc().trim()))
				    		{
					    		if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))) //中国联通
					    			bean.set("itemdesc","工作目标");
					     		else
					     			bean.set("itemdesc",ResourceFactory.getProperty("jx.khplan.point"));
					    	}
					    	else
					    		bean.set("itemdesc",item.getItemdesc());
						}
					}else
					{
			    		bean.set("itemdesc", item.getItemdesc());//列头显示为目标卡指标名称
					}
					bean.set("itemtype_1",item.getItemtype().toUpperCase());
					bean.set("itemtype",a_item.getItemtype().toUpperCase());
					bean.set("codesetid",a_item.getCodesetid());
					bean.set("codesetid_1",item.getCodesetid());
					bean.set("decilength",a_item.getDecimalwidth()+"");
					bean.set("decilength_1",item.getDecimalwidth()+"");
					if("D".equalsIgnoreCase(a_item.getItemtype()))
					{
						column.append(","+Sql_switcher.dateToChar(a_item.getItemid(), "yyyy-mm-dd")+" as "+a_item.getItemid());// hh24:mi:ss
					}
					else
		    			column.append(","+a_item.getItemid());
					alist.add(bean);
				}
			}
			DBMetaModel dbmodel=new DBMetaModel(this.connection);
			dbmodel.reloadTableModel(this.DeptDutySubSet);
        	DbWizard dbWizard=new DbWizard(this.connection);  		
    		Table table=new Table(this.DeptDutySubSet);
    		if(!dbWizard.isExistTable(table.getName(),false))
    		{
    			list.add(0,new ArrayList());
    			list.add(1,new ArrayList());
    			list.add(2,tableWidth+"");
    			isHaveRecord="1";
    			alertMessage="目标卡部门职责参数中设置的部门职责子集未构库！";
    			list.add(3,isHaveRecord);
  		        list.add(4,alertMessage);
  		        return list;
    		}
    		if(alist.size()<=0)
    		{
    			list.add(0,new ArrayList());
    			list.add(1,new ArrayList());
    			list.add(2,tableWidth+"");
    			isHaveRecord="1";
    			alertMessage="目标卡部门职责参数中设置的部门职责子集指标未构库！";
    			list.add(3,isHaveRecord);
  		        list.add(4,alertMessage);
  		        return list;
    		}
			StringBuffer sql = new StringBuffer();
			sql.append("select b0110,i9999"+column.toString());
			sql.append(" from "+this.DeptDutySubSet);
			sql.append(" where 1=1 ");
			if(this.DeptDutyTargetItem!=null&&!"".equals(this.DeptDutyTargetItem)){
				sql.append(" and "+this.DeptDutyTargetItem+"='"+itemdesc+"'");
			}
			if(this.DeptDutyDataItem!=null&&!"".equals(this.DeptDutyDataItem)){
				sql.append(" and "+Sql_switcher.year(this.DeptDutyDataItem)+"="+this.plan_vo.getString("theyear"));
			}
			if(i9_buf.toString().length()>0)
			{
				if(type==1)
		    		sql.append(" and ("+i9_buf.toString().substring(3)+")");
				else
					sql.append(" and ("+i9_buf.toString().substring(3)+")");
			}
			sql.append(" and b0110 in ('"+deptStringid.replaceAll(",", "','")+"')");
			rs= dao.search(sql.toString());
			ArrayList dataList = new ArrayList();
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("e01a1", rs.getString("b0110"));
				bean.set("i9999",rs.getInt("i9999")+"");
				for(int i=0;i<alist.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)alist.get(i);
					String id=(String)abean.get("itemid");
					String itemtype=(String)abean.get("itemtype");
					String codesetid=(String)abean.get("codesetid");
					String decilength=(String)abean.get("decilength");
					if("A".equalsIgnoreCase(itemtype))
					{
						if("0".equals(codesetid))
						{
							bean.set(id, rs.getString(id)==null?"":rs.getString(id));
							bean.set(id+"_info", rs.getString(id)==null?"":rs.getString(id));
						}
						else
						{
							bean.set(id, rs.getString(id)==null?"":AdminCode.getCodeName(codesetid,rs.getString(id)));
							bean.set(id+"_1", rs.getString(id)==null?"":rs.getString(id));
							
						}
					}
					else if("D".equalsIgnoreCase(itemtype))
					{
						bean.set(id, rs.getString(id)==null?"":rs.getString(id));
					}
					else if("N".equalsIgnoreCase(itemtype))
					{
						if("0".equals(decilength))
						{
							bean.set(id, rs.getInt(id)+"");
						}
						else
						{
							bean.set(id, rs.getDouble(id)+"");
						}
					}
					else if("M".equalsIgnoreCase(itemtype))
					{
						String str=Sql_switcher.readMemo(rs, id);
						if(str.length()>30)
						{
							bean.set(id, str.substring(0,30)+"...");
							bean.set(id+"_info", str);
						}
						else
						{
							bean.set(id, str);
							bean.set(id+"_info", str);
						}
						
					}
					bean.set("itemtype", itemtype);
				}
				dataList.add(bean);
			}
			if(dataList.size()<=0)
			{
				list.add(0,new ArrayList());
    			list.add(1,new ArrayList());
    			list.add(2,tableWidth+"");
    			isHaveRecord="1";
    			alertMessage="目标卡部门职责子集中没有符合条件的数据！";
    			list.add(3,isHaveRecord);
  		        list.add(4,alertMessage);
  		        return list;
			}
			else
			{
	    		list.add(0,dataList);
		    	list.add(1,alist);
		    	list.add(2,tableWidth+"");
		        list.add(3,isHaveRecord);
		        list.add(4,alertMessage);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

}
