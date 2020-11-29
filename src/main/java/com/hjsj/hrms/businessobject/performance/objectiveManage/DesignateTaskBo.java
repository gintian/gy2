package com.hjsj.hrms.businessobject.performance.objectiveManage;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 目标卡任务下达操作类
 * <p>Title:DesignateTaskBo.java</p>
 * <p>Description>:DesignateTaskBo.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jul 27, 2011  3:14:07 PM </p>
 * <p>@version: 5.0</p>
 * <p>@author: LiZhenWei
 */

public class DesignateTaskBo 
{
	private Connection conn;
	private UserView userView;
	private RecordVo plan_vo = null;
	private RecordVo template_vo = null;
	private String object_id = "";
	private String plan_id = "";
	private ArrayList p04List = new ArrayList();
	private ArrayList templateItemList = new ArrayList();  // 模板项目记录表
	private ArrayList leafItemList = new ArrayList();      // 叶子项目列表
	private HashMap itemToPointMap = new HashMap();      // 项目对应任务map
//	private HashMap leafItemLinkMap = new HashMap();     // 叶子项目对应的继承关系
	private int lay = 0;   							// 项目层级
//	private HashMap itemPointNum = new HashMap(); // 取得项目拥有的节点数
	
	public DesignateTaskBo(Connection conn,UserView userView)
	{
		this.conn = conn;
		this.userView = userView;
	}
	public DesignateTaskBo(Connection conn,UserView userView,String plan_id,String object_id)
	{
		this.conn = conn;
		this.userView = userView;
		this.plan_id = plan_id;
		this.object_id = object_id;		
		this.plan_vo = getPlanVo();
		this.template_vo = get_TemplateVo();		
		
		this.p04List = getKPIList(this.plan_id,this.object_id);
		this.templateItemList = getTemplateItemList(); // 取得 模板项目记录		
		get_LeafItemList(); // 递归查找叶子项目列表
		this.itemToPointMap = getItemToPointMap(); // 项目对应任务map
	//	this.leafItemLinkMap = getLeafItemLinkMap(); // 叶子项目对应的继承关系
	//	this.itemPointNum = getItemPointNum(); // 取得项目拥有的节点数
		
	}
	public RecordVo getPlanVo()
	{
		RecordVo vo=new RecordVo("per_plan");
		try
		{
			vo.setInt("plan_id",Integer.parseInt(this.plan_id));
			ContentDAO dao = new ContentDAO(this.conn);
			vo=dao.findByPrimaryKey(vo);
			if(vo.getInt("method")==0)
				vo.setInt("method",1);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	public RecordVo get_TemplateVo()
	{
		RecordVo vo=new RecordVo("per_template");
		try
		{
			vo.setString("template_id",this.plan_vo.getString("template_id"));
			ContentDAO dao = new ContentDAO(this.conn);
			vo=dao.findByPrimaryKey(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	
	public ArrayList getKPIList(String plan_id,String object_id)
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{
		   ContentDAO dao = new ContentDAO(this.conn);
		   RecordVo vo = new RecordVo("per_plan");
		   vo.setInt("plan_id", Integer.parseInt(plan_id));
		   vo = dao.findByPrimaryKey(vo);
		   String object_type=vo.getString("object_type");
		   StringBuffer sql = new StringBuffer("");
		   sql.append("select p04.item_id,pti.itemdesc,p04.p0400,p04.p0407,p04.fromflag,p04.p0401,pti.seq from p04 left join per_template_item pti on ");//要区分指标和任务，指标不能改名fromflag=1是任务，可以改名只能找这2种做任务下达1,自建（不进KPI库） 2,来源KPI指标
		   sql.append(" p04.item_id=pti.item_id ");
		   sql.append(" where plan_id="+plan_id);
		   sql.append(" and ");
		   if(!"2".equals(object_type))//不是人员
			   sql.append(" b0110='"+object_id+"'");
		   else
			   sql.append(" a0100='"+object_id+"'");
		   sql.append(" and ( ( state=-1 and (chg_type!=3 or chg_type is null) ) or state is null or state<>-1 )");
		   sql.append(" and (fromflag=1 or fromflag=2) order by pti.seq,p04.seq ");//只有自建和KPI的能下达，别的暂时不能
		   rs = dao.search(sql.toString());
		   HashMap listmap = this.getAllTask(plan_id, object_type, object_id);
		   while(rs.next())
		   {
			   String item_id = rs.getString("item_id");
			   String itemdesc = rs.getString("itemdesc");
			   String p0400 = rs.getString("p0400");
			   String p0407=Sql_switcher.readMemo(rs, "p0407");
			   LazyDynaBean bean  = new LazyDynaBean();
			   bean.set("xa0101", "");
			   bean.set("za0101", "");
			   bean.set("p0407",p0407);
			   bean.set("p0400", p0400);
			   bean.set("item_id", item_id);
			   bean.set("itemdesc", itemdesc);
			   bean.set("fromflag",rs.getString("fromflag"));
			   bean.set("p0401", rs.getString("p0401"));
			   if(listmap.get(p0400)!=null)
			   {
				   ArrayList sublist = (ArrayList)listmap.get(p0400);
				   bean.set("sublist", sublist);
			   }else{
				   bean.set("sublist", new ArrayList());
			   }
			   bean.set("encodep0407", SafeCode.encode(p0407));
			   list.add(bean);
		   }
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }finally{
		   try
		   {
			   if(rs!=null)
				   rs.close();
		   }
		   catch(Exception e)
		   {
			   e.printStackTrace();
		   }
	   }
	   return list;
   }
   public HashMap getAllTask(String plan_id,String object_type,String object_id)
   {
	   HashMap map = new HashMap();
	   RowSet rs = null;
	   try
	   {
		   StringBuffer sql = new StringBuffer("");
		   sql.append("select p0400 from p04 where ");
		   sql.append(" plan_id="+plan_id);
		   sql.append(" and ");
		   if(!"2".equals(object_type))//不是人员
			   sql.append(" b0110='"+object_id+"'");
		   else
			   sql.append(" a0100='"+object_id+"'");
		   sql.append(" and ( ( state=-1 and (chg_type!=3 or chg_type is null) ) or state is null or state<>-1 ) and (fromflag=1 or fromflag=2)");
		   StringBuffer buf = new StringBuffer("");
		   buf.append("select task_id,p0400,to_a0101,task_type,group_id,to_p0407,to_a0100,to_p0400 from per_designate_task ");
		   buf.append(" where p0400 in (");
		   buf.append(sql.toString());
		   buf.append(")");
		   buf.append(" order by task_id,group_id");
		   ContentDAO dao = new ContentDAO(this.conn);
		   rs = dao.search(buf.toString());
		   ArrayList zlist = new ArrayList();
		   ArrayList list = new ArrayList();
		   LazyDynaBean lazyBean =null;
		   HashMap amap = new HashMap();
		   HashMap noCanDelMap = this.getNoCanDeletePeople(plan_id, object_type, object_id);
		   while(rs.next())
		   {
			   int task_id = rs.getInt("task_id");
			   int p0400 = rs.getInt("p0400");
			   int group_id = rs.getInt("group_id");
			   int task_type =rs.getInt("task_type");//=0时，为还未正式下达的任务，
			   String a0101 = rs.getString("to_a0101")==null|| "no".equalsIgnoreCase(rs.getString("to_a0101"))?"":rs.getString("to_a0101");
			   String p0407 = Sql_switcher.readMemo(rs,"to_p0407");
			   String key = p0400+"`"+group_id;
			   lazyBean = new LazyDynaBean();
			   lazyBean.set("task_id", task_id+"");
			   lazyBean.set("p0400", p0400+"");
			   lazyBean.set("group_id", group_id+"");
			   lazyBean.set("task_type", task_type+"");
			   lazyBean.set("a0101", a0101);
			   if(task_type==0)
			   {
				   lazyBean.set("canDel", "0");
			   }
			   else if(task_type==1)
			   {
				   String to_p0400=rs.getString("to_p0400");
				   String to_a0100=rs.getString("to_a0100");
				   if(noCanDelMap.get(to_a0100+to_p0400)==null)
				   {
					   lazyBean.set("canDel", "1");
				   }else
				   {
					   lazyBean.set("canDel", "0");
				   }
			   }
			   else if(task_type==2)
			   {
				   lazyBean.set("canDel", "1");
			   }
			   lazyBean.set("p0407", p0407);
			   lazyBean.set("encodep0407", SafeCode.encode(p0407));
			   lazyBean.set("key", key);
			   zlist.add(lazyBean);
			   if(amap.get(key)==null)
			   {
				   amap.put(key, "1");
				   list.add(lazyBean);
			   }
		   }
		   for(Iterator i=list.iterator();i.hasNext();)
		   {
			   LazyDynaBean abean = (LazyDynaBean)i.next();
			   String key=(String)abean.get("key");
			   ArrayList zbuf = new ArrayList();
			   ArrayList xbuf = new ArrayList();
			   for(int j=0;j<zlist.size();j++)
			   {
				   LazyDynaBean bean = (LazyDynaBean)zlist.get(j);
				   String p0400=(String)bean.get("p0400");
				   String group_id=(String)bean.get("group_id");
				   String akey = p0400+"`"+group_id;
				   String a0101=(String)bean.get("a0101");
				   if(akey.equals(key)&&!"".equals(a0101))
				   {
					   String task_type=(String)bean.get("task_type");
					   if("0".equals(task_type))
						   break;
					   if("1".equals(task_type))
						   zbuf.add(bean);
					   else
						   xbuf.add(bean);
				   }
			   }
			   abean.set("za0101", zbuf);
			   abean.set("xa0101", xbuf);
			   String p0400=(String)abean.get("p0400");
			   if(map.get(p0400)==null)
			   {
				   ArrayList alist = new ArrayList();
				   alist.add(abean);
				   map.put(p0400, alist);
			   }else{
				   ArrayList alist = (ArrayList)map.get(p0400);
				   alist.add(abean);
			   }
		   }
		   
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }finally{
		   if(rs!=null)
		   {
			   try
			   {
				   rs.close();
			   }catch(Exception e)
			   {
				   e.printStackTrace();
			   }
		   }
	   }
	   return map;
   }
   public HashMap getNoCanDeletePeople(String plan_id,String object_type,String object_id)
   {
	   HashMap map = new HashMap();
	   RowSet rs = null;
	   try
	   {
		   ContentDAO dao = new ContentDAO(this.conn);
		   StringBuffer buf = new StringBuffer("");
		   buf.append("select p04.b0110,p04.a0100,p04.p0400,p04.f_p0400,p04.plan_id,po.sp_flag,pp.status from p04 left join per_object po on p04.plan_id=po.plan_id ");
		   buf.append(" and p04.a0100=po.object_id left join per_plan pp on p04.plan_id=pp.plan_id ");
    	   buf.append(" where p04.p0400 in (");
    	   buf.append("select to_p0400 from per_designate_task where p0400");
    	   StringBuffer sql = new StringBuffer("");
		   sql.append("select p0400 from p04 where ");
		   sql.append(" plan_id="+plan_id);
		   sql.append(" and ");
		   if(!"2".equals(object_type))//不是人员
			   sql.append(" b0110='"+object_id+"'");
		   else
			   sql.append(" a0100='"+object_id+"'");
		   sql.append(" and ( ( state=-1 and (chg_type!=3 or chg_type is null) ) or state is null or state<>-1 ) and (fromflag=1 or fromflag=2)");
		   buf.append(" in ("+sql.toString()+"))");
    	   rs=dao.search(buf.toString());
    	   while(rs.next())
    	   {
    		   String sp_flag=rs.getString("sp_flag")==null?"01":rs.getString("sp_flag");
	    	   String status=rs.getString("status");
    		   if("8".equals(status)&&("01".equals(sp_flag)|| "07".equals(sp_flag)))//计划为分发，审批状态为起草或驳回的才可以删除
    		   {
    	    	   continue;
    		   }  
    		   String aobject_id=rs.getString("a0100");
	    	   String p0400=rs.getString("p0400");
	    	   map.put(aobject_id+p0400, "1");
    	   }
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   finally{
		   try
		   {
			   if(rs!=null)
				   rs.close();
		   }
		   catch(Exception e)
		   {
			   e.printStackTrace();
		   }
	   }
	   return map;
   }
   /**
    * 获取做大任务号或组号
    * @param column
    * @return
    */
   public int getMax(String column,String table)//per_designate_task
   {
	   int id=0;
	   RowSet rs = null;
	   try
	   {
		   ContentDAO dao = new ContentDAO(this.conn);
		   rs = dao.search("select max("+column+") from "+table);
		   while(rs.next())
		   {
			   id=rs.getInt(1);
		   }
		   id=id==0?1:id+1;
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   finally{
		   if(rs!=null)
		   {
			   try
			   {
				   rs.close();
			   }
			   catch(Exception e)
			   {
				   e.printStackTrace();
			   }
		   }
	   }
	   return id;
   }
   public void addRecord(String p0400,String p0407)
   {
	   try
	   {
		   int a_task_id= this.getMax("task_id", "per_designate_task");
		   int a_group_id=this.getMax("group_id", "per_designate_task");
		   RecordVo vo = new RecordVo("per_designate_task");
		   vo.setInt("task_id", a_task_id);
		   vo.setInt("group_id",a_group_id);
		   vo.setString("to_p0407", p0407);
		   vo.setInt("p0400", Integer.parseInt(p0400));
		   vo.setInt("task_type", 0);
		   ContentDAO dao =new ContentDAO(this.conn);
		   dao.addValueObject(vo);
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
   }
   public String createTempTable()
   {
	   String tableName="T#su_per_dtask";
	   try
	   {
		   Table table = new Table(tableName);
		   DbWizard dw = new DbWizard(this.conn);
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
		   if(dw.isExistTable(table.getName(), false))
		   {
			   dbmodel.reloadTableModel(tableName);
		   }else
		   {
			   Field field = new Field("task_id","task_id");
			   field.setDatatype(DataType.INT);
			   field.setLength(10);
			   field.setKeyable(true);
			   field.setNullable(false);
			   table.addField(field);
			   field = new Field("p0400","p0400");
			   field.setDatatype(DataType.INT);
			   field.setLength(10);
			   table.addField(field);
			   field = new Field("group_id","group_id");
			   field.setDatatype(DataType.INT);
			   field.setLength(10);
			   table.addField(field);
			   field = new Field("to_p0407","to_p0407");
			   field.setDatatype(DataType.CLOB);
			   field.setLength(10);
			   table.addField(field);
			   field = new Field("userName","userName");
			   field.setDatatype(DataType.STRING);
			   field.setLength(70);
			   table.addField(field);
			   /*field = new Field("atask_id","atask_id");
			   field.setDatatype(DataType.INT);
			   field.setLength(10);
			   table.addField(field);
			   field = new Field("fromflag","fromflag");
			   field.setDatatype(DataType.INT);
			   field.setLength(10);
			   table.addField(field);
			   field = new Field("p0401","p0401");
			   field.setDatatype(DataType.STRING);
			   field.setLength(50);
			   table.addField(field);*/
			   dw.createTable(table);
			   dbmodel.reloadTableModel(tableName);
		   } 
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   return tableName;
   }
   /**
    * 
    * @param p0400
    * @param to_plan_id
    * @param to_item_id
    * @param a0100s
    * @param p0407
    * @param fromflag
    * @param p0401
    * @param qzfp
    * @param type
    * @param taskid
    * @param group_id
    * @param task_type=1主办任务，=2协办任务
    */
   public void addDesinateTask(String p0400,String plan_id,String to_plan_id,String to_item_id,String a0100s,String p0407,String fromflag,String p0401,String qzfp,String type,String taskid,String group_id,String task_type){
	   RowSet rs = null;
	   DbSecurityImpl dbS = new DbSecurityImpl();
	   PreparedStatement pt =null;
	   try
	   {
		   //HashMap baseInfoMap = this.getBaseInfo(a0100s);
		   String[] array = a0100s.split("/");
		   ContentDAO dao  = new ContentDAO(this.conn);
		   ArrayList insertP04 = new ArrayList();
		   ArrayList insertTask = new ArrayList();
		   RecordVo plan_vo = new RecordVo("per_plan");
		   plan_vo.setInt("plan_id",Integer.parseInt(to_plan_id));
		   plan_vo=dao.findByPrimaryKey(plan_vo);
		   String template_id = plan_vo.getString("template_id");
		   RecordVo templateVo = new RecordVo("per_template");
		   templateVo.setString("template_id", template_id);
		   templateVo=dao.findByPrimaryKey(templateVo);
		  String status=templateVo.getString("status");
		   /**先增加目标卡中的记录，在增加下达任务表*/
		   int i=0;
		   int tid= this.getMax("task_id", "per_designate_task");
		   int ii=0;
		   RecordVo yp04vo=new RecordVo("p04");
		   yp04vo.setInt("p0400", Integer.parseInt(p0400));
		   yp04vo=dao.findByPrimaryKey(yp04vo);		   
		   
		    AnalysePlanParameterBo appb=new AnalysePlanParameterBo(this.conn);
			Hashtable ht_table=appb.analyseParameterXml();
			LoadXml parameter_content = null;
		    if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"")==null)
			{						
		        parameter_content = new LoadXml(this.conn,plan_id+"");
				BatchGradeBo.getPlanLoadXmlMap().put(plan_id+"",parameter_content);
			}
			else
			{
				parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"");
			}
			Hashtable params = parameter_content.getDegreeWhole();
			String targetDefineItem_p="";
		    if(ht_table!=null)
		    {
		        if(ht_table.get("TargetDefineItem")!=null&&((String)ht_table.get("TargetDefineItem")).trim().length()>0)
		        	targetDefineItem_p=(","+(String)ht_table.get("TargetDefineItem")+",").toUpperCase();
		    }
		    if(params.get("TargetTraceEnabled")!=null&& "True".equalsIgnoreCase((String)params.get("TargetTraceEnabled")))
			{
				if(params.get("TargetDefineItem")!=null&&((String)params.get("TargetDefineItem")).trim().length()>0)
					targetDefineItem_p=(","+((String)params.get("TargetDefineItem")).trim()+",").toUpperCase();   //目标卡指标
			}
		   		  		 		   
		   LoadXml loadXml = null;
         	if(BatchGradeBo.getPlanLoadXmlMap().get(to_plan_id+"")==null)
			{
					
         		loadXml = new LoadXml(this.conn,to_plan_id+"");
				BatchGradeBo.getPlanLoadXmlMap().put(to_plan_id+"",loadXml);
			}
			else
			{
				loadXml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(to_plan_id+"");
			}
			Hashtable hparams = loadXml.getDegreeWhole();
			String targetDefineItem_t="";
		    if(ht_table!=null)
		    {
		        if(ht_table.get("TargetDefineItem")!=null&&((String)ht_table.get("TargetDefineItem")).trim().length()>0)
		        	targetDefineItem_t=(","+(String)ht_table.get("TargetDefineItem")+",").toUpperCase();
		    }
		    if(hparams.get("TargetTraceEnabled")!=null&& "True".equalsIgnoreCase((String)hparams.get("TargetTraceEnabled")))
			{
				if(hparams.get("TargetDefineItem")!=null&&((String)hparams.get("TargetDefineItem")).trim().length()>0)
					targetDefineItem_t=(","+((String)hparams.get("TargetDefineItem")).trim()+",").toUpperCase();   //目标卡指标
			}
			
		    String targetDefineItem = "";
		    if(targetDefineItem_t!=null && targetDefineItem_t.trim().length()>0)
		    {
		    	String[] matters = targetDefineItem_t.split(",");
		    	for (int k = 0; k < matters.length; k++)
				{
		    		if(matters[k]==null|| "".equals(matters[k]))
		    			continue;
				    String itemid = matters[k];				    				
					if(targetDefineItem_p!=null && targetDefineItem_p.trim().length()>0 && targetDefineItem_p.indexOf(","+itemid.toUpperCase()+",")!=-1)
					{
						targetDefineItem += ","+itemid.toUpperCase();						
					}
				}
		    }		    
		//   System.out.println(targetDefineItem+"&&&&&&&&&&&&&&&&&&&");
		//   System.out.println(targetDefineItem.substring(1)+"#######################");
		    
		   for(int index=0;index<array.length;index++)
		   {
			   if(array[index]==null|| "".equals(array[index]))
				   continue;
			   String[] temp=array[index].split("`");//isNew+"`"+rs.getString("object_id")+"`"+rs.getString("p0400")+"`"+rs.getString("task_id")+"sp_flag";
			   String isNew=temp[0];//isNew="1";//原来有记录
			   String a0100=temp[1];
			   String isChecked=temp[5];//=1被选中
			   String sp_flag=temp[4];//=1被选中
			  
			   if("0".equals(isNew)&& "1".equals(isChecked))
			   {
				   String b0110="";
        		   String e0122="";
    	    	   String e01a1="";
		    	   String a0101="";
    	    	   String nbase="";
        		   RecordVo baseInfoVo = new RecordVo("USRA01");
        		   baseInfoVo.setString("a0100", a0100);
    	    	   if(dao.isExistRecordVo(baseInfoVo))
		    	   {
    	    		   baseInfoVo=dao.findByPrimaryKey(baseInfoVo);
    	    		   b0110 = baseInfoVo.getString("b0110");
	         		   e0122 = baseInfoVo.getString("e0122");
	        		   e01a1 = baseInfoVo.getString("e01a1");
	        		   a0101 = baseInfoVo.getString("a0101");
 	    	    	   nbase = "USR";
    	    	   }
    	    	   String to_p0400="";
				   if("1".equals(task_type))
				   {
		        	   IDGenerator idg=new IDGenerator(2,conn);
		        	   to_p0400=idg.getId("P04.P0400");
		        	   RecordVo p04vo = new RecordVo("p04");
		        	   p04vo.setInt("p0400", Integer.parseInt(to_p0400));
		    	       int seq = this.getP04Seq(to_plan_id, a0100, dao);
		        	   p04vo.setInt("seq",seq);
		        	  
 	    	    	   p04vo.setString("b0110", b0110);
	    	    	   p04vo.setString("e0122", e0122);
	    	    	   p04vo.setString("e01a1", e01a1);
	    	    	   p04vo.setString("nbase", nbase);
     		    	   p04vo.setString("a0101", a0101);
	    	    	   p04vo.setString("p0407", p0407);
	    	    	   p04vo.setString("a0100", a0100);
	    	    	   p04vo.setInt("item_id",Integer.parseInt(to_item_id));
		        	   if("1".equals(fromflag))
		         	       p04vo.setString("p0401",to_p0400);
	        		   else
		        		   p04vo.setString("p0401",p0401);
		        	   p04vo.setInt("plan_id",Integer.parseInt(to_plan_id));
		        	   if("07".equals(sp_flag))
		        	   {
		        		   p04vo.setInt("state", -1);
		        		   p04vo.setInt("chg_type", 2);
		        	   }else
		        	   {
	        	    	   p04vo.setInt("state", 0);
		        	   }
	    	    	   p04vo.setInt("fromflag", Integer.parseInt(fromflag));//1,自建（不进KPI库）即任务 2,来源KPI指标即指标
		        	   if("1".equals(qzfp))//强制分配
	    	    	   {
	    	    		   p04vo.setInt("itemtype",3);
	     	    	   }
	        		   else
	    	    	   {
	    	    		   p04vo.setInt("itemtype", 0);
	        		   }
		        	   if("0".equals(status))//分值
		            	   p04vo.setDouble("p0415", 1);
		        	   else 
		        		   p04vo.setDouble("p0413", 100);
		        	   
		        	   if(targetDefineItem!=null && targetDefineItem.trim().length()>0)
		        	   {
		        		    //targetDefineItem = targetDefineItem.substring(1);
		        		    String[] matters = targetDefineItem.split(",");
			   		    	for (int k = 0; k < matters.length; k++)
			   				{
			   		    		if(matters[k]==null|| "".equals(matters[k])|| "ATTACHMENT".equalsIgnoreCase(matters[k])|| "p0413".equalsIgnoreCase(matters[k])|| "p0421".equalsIgnoreCase(matters[k])|| "p0415".equalsIgnoreCase(matters[k])|| "p0423".equalsIgnoreCase(matters[k]))
			   		    			continue;
			   				    String itemid = matters[k];
			   				    FieldItem fielditem = DataDictionary.getFieldItem(itemid);
								String itemtype = fielditem.getItemtype();
								int decimalwidth = fielditem.getDecimalwidth();
								if ("N".equalsIgnoreCase(itemtype))
								{
									if (decimalwidth == 0)
										p04vo.setInt(itemid.toLowerCase(), yp04vo.getInt(itemid.toLowerCase()));
									else
										p04vo.setDouble(itemid.toLowerCase(), yp04vo.getDouble(itemid.toLowerCase()));
								} 							
								else if ("M".equalsIgnoreCase(itemtype))
								{
									p04vo.setString(itemid.toLowerCase(), yp04vo.getString(itemid.toLowerCase()));
								}
								else if ("D".equalsIgnoreCase(itemtype))
								{
									p04vo.setDate(itemid.toLowerCase(), yp04vo.getDate(itemid.toLowerCase()));
								}
								else
								{
									p04vo.setString(itemid.toLowerCase(), yp04vo.getString(itemid.toLowerCase()));
								}
			   				}
		        	   }		        	   
		        	   
		        	   insertP04.add(p04vo);
		        	   if(params.get("TaskSupportAttach")!=null&& "True".equalsIgnoreCase((String)params.get("TaskSupportAttach")))
		        	   {
		            	   rs = dao.search("select article_id,task_id,ext,affix,article_name from per_article where Article_type=3 and task_id="+p0400);
		            	   while(rs.next())
		            	   {
		        	    	   int article_id = insertPerArticleRecord(to_plan_id, 3, 2,a0100,to_p0400);
		        	    	   String sql = "update per_article set ext=?,affix=?,Article_name=? where article_id=?";
					    		try {
									pt = this.conn.prepareStatement(sql);
									pt.setString(1, rs.getString("ext"));
									// blob字段保存,数据库中差异
									switch (Sql_switcher.searchDbServer()) {
									case Constant.ORACEL:
										Blob blob = getOracleBlob(rs.getBinaryStream("affix"), "per_article",article_id);
										pt.setBlob(2, blob);
										pt.setString(3, rs.getString("Article_name"));
										pt.setInt(4, article_id);
										break;
									default:
										byte[] data = rs.getBytes("affix");
										// a_vo.setObject("affix",data);
										pt.setBytes(2, data);
										pt.setString(3, rs.getString("Article_name"));
										pt.setInt(4, article_id);
										break;
									}
									// 打开Wallet
									dbS.open(this.conn, sql);
									pt.execute();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}finally {
									PubFunc.closeDbObj(pt);
								}
		            	   }
		        	   }
				   }
		    	   RecordVo pdtvo = new RecordVo("per_designate_task");
		    	   int id=0;
		    	   if("0".equalsIgnoreCase(type)&&i==0)//还没具体下达过业务的，
		    	   {
		    		   id=Integer.parseInt(taskid);
		    		   dao.delete("delete from per_designate_task where task_id="+taskid, new ArrayList());
		    	   }
		    	   else{
		    		   id=tid+ii;
		    		   ii++;
		    	   }
		    	   pdtvo.setInt("task_id", id);
		    	   pdtvo.setInt("group_id",Integer.parseInt(group_id));
		    	   pdtvo.setInt("p0400", Integer.parseInt(p0400));
		    	   if("1".equals(task_type))
		        	   pdtvo.setInt("to_p0400", Integer.parseInt(to_p0400));
		    	   pdtvo.setString("to_p0407", p0407);
		    	   pdtvo.setString("to_a0100", a0100);
		    	   pdtvo.setString("to_nbase", nbase.toUpperCase());
		    	   pdtvo.setString("to_a0101", a0101);
		    	   pdtvo.setString("nbase",this.userView.getDbname().toUpperCase());
		    	   pdtvo.setString("a0100", this.userView.getA0100());
		    	   pdtvo.setString("a0101",this.userView.getUserFullName());
		    	   pdtvo.setInt("task_type",Integer.parseInt(task_type));
		    	   pdtvo.setDate("create_date", new Date());
		    	   insertTask.add(pdtvo);
		    	   i++;
			   }
		   }
		   if(insertP04.size()>0)
			   dao.addValueObject(insertP04);
		   if(insertTask.size()>0)
	     	   dao.addValueObject(insertTask);
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }finally{
		   PubFunc.closeDbObj(rs);
		   PubFunc.closeDbObj(pt);
		   PubFunc.closeDbObj(this.conn);
		   
		   try
		   {
			   // 关闭Wallet
			   dbS.close(this.conn);
		   }
		   catch(Exception e)
		   {
			   e.printStackTrace();
		   }
	   }
   }
   private Blob getOracleBlob(InputStream file,String tablename,int article_id) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select affix from ");
		strSearch.append(tablename);
		strSearch.append(" where article_id=");
		strSearch.append(article_id);		
		strSearch.append(" FOR UPDATE");
		
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  ");
		strInsert.append(tablename);
		strInsert.append(" set affix=EMPTY_BLOB() where article_id=");
		strInsert.append(article_id);
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.conn);
		Blob blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),file); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		return blob;
	}
   private int insertPerArticleRecord(String planid,int article_type,int fileflag,String a_objectid,String p0400)
	{
		int article_id=0;
		RowSet rs = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel("per_article");
			RecordVo avo=new RecordVo("per_article");
			article_id= DbNameBo.getPrimaryKey("per_article","article_id",this.conn);
			avo.setInt("article_id", article_id);
			avo.setInt("plan_id",Integer.parseInt(planid));
			String b0110="";String e0122="";String e01a1="";String a0101="";
			rs=dao.search("select b0110,e0122,e01a1,a0101 from "+this.userView.getDbname()+"A01 where a0100='"+a_objectid+"'");
			if(rs.next())
			{
				b0110=rs.getString("b0110")!=null?rs.getString("b0110"):"";
				e0122=rs.getString("e0122")!=null?rs.getString("e0122"):"";
				e01a1=rs.getString("e01a1")!=null?rs.getString("e01a1"):"";
				a0101=rs.getString("a0101")!=null?rs.getString("a0101"):"";
			}
			avo.setString("b0110",b0110);
			avo.setString("e0122", e0122);
			avo.setString("e01a1", e01a1);
			avo.setString("nbase","Usr");
			avo.setString("a0100",a_objectid);
			avo.setString("a0101",a0101);
			avo.setInt("article_type", article_type);
			avo.setInt("task_id", Integer.parseInt(p0400));
			avo.setInt("fileflag",fileflag);
			avo.setInt("state",0);
			dao.addValueObject(avo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return article_id;
	}
	
   public void addDesinateTask2(String p0400,String a0100s,String p0407,String type,String taskid,String group_id,String task_type){
	   try
	   {
		   HashMap baseInfoMap = this.getBaseInfo(p0400,group_id);
		   String[] array = a0100s.split("/");
		   ContentDAO dao  = new ContentDAO(this.conn);
		   /**先增加目标卡中的记录，在增加下达任务表*/
		   int i=0;
		   int tid= this.getMax("task_id", "per_designate_task");
		   int ii=0;
		   ArrayList insertTask=new ArrayList();
		   String[] arr1=array[0].split(",");
		   String[] arr2=array[1].split(",");
		   for(int index=0;index<arr2.length;index++)
		   {
			   
			    if(arr2[index]==null|| "".equals(arr2[index]))
				   continue;
			    String temp=arr2[index];//isNew+"`"+rs.getString("object_id")+"`"+rs.getString("p0400")+"`"+rs.getString("task_id");
			    String a0100=temp.substring(3);
			    String nbase=temp.substring(0,3);
			    String a0101=arr1[index];
			    if(baseInfoMap.get(nbase.toUpperCase()+a0100)!=null)
			    	continue;
	   	        RecordVo pdtvo = new RecordVo("per_designate_task");
    		    int id=0;
    		    if("0".equalsIgnoreCase(type)&&i==0)//还没具体下达过业务的，
    		    {
	        	     id=Integer.parseInt(taskid);
	        	    dao.delete("delete from per_designate_task where task_id="+taskid, new ArrayList());
    		    }
    		    else{
    		        id=tid+ii;
    		        ii++;
    		    }
    		    pdtvo.setInt("task_id", id);
    		    pdtvo.setInt("group_id",Integer.parseInt(group_id));
	    	    pdtvo.setInt("p0400", Integer.parseInt(p0400));
	    	    pdtvo.setString("to_p0407", p0407);
    		    pdtvo.setString("to_a0100", a0100);
	    	    pdtvo.setString("to_nbase", nbase.toUpperCase());
	    	    pdtvo.setString("to_a0101", a0101);
	    	    pdtvo.setString("nbase",this.userView.getDbname().toUpperCase());
	    	    pdtvo.setString("a0100", this.userView.getA0100());
	    	    pdtvo.setString("a0101",this.userView.getUserFullName());
	    	    pdtvo.setInt("task_type",Integer.parseInt(task_type));
	    	    pdtvo.setDate("create_date", new Date());
	    	    insertTask.add(pdtvo);
    		    i++;
    	   }
   		   if(insertTask.size()>0)
	     	   dao.addValueObject(insertTask);	   
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
   }
   public int getP04Seq(String plan_id,String object_id,ContentDAO dao)
   {
	   int seq=1;
	   RowSet rs = null;
	   try
	   {
		   String sql="select "+Sql_switcher.isnull("max(seq)","0")+"+1 from p04 where a0100='"+object_id+"' and plan_id="+plan_id;
		   rs = dao.search(sql);
		   while(rs.next())
		   {
			   seq = rs.getInt(1);
		   }
		   
		   
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }finally{
		   try
		   {
			   if(rs!=null)
				   rs.close();
		   }
		   catch(Exception e)
		   {
			   e.printStackTrace();
		   }
	   }
	   return seq;
   }
   public HashMap getBaseInfo(String p0400,String group_id)
   {
	   HashMap map = new HashMap();
	   RowSet rs = null;
	   try
	   {
		  ContentDAO dao  = new ContentDAO(this.conn);
		  rs = dao.search("select to_a0100,to_nbase from per_designate_task where p0400="+p0400+" and group_id="+group_id);
		  while(rs.next())
		  {
			  if(rs.getString("to_a0100")==null)
				  continue;
			  map.put(rs.getString("to_nbase").toUpperCase()+rs.getString("to_a0100"),"1");
		  }
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }finally{
		   try
		   {
			   if(rs!=null)
				   rs.close();
		   }
		   catch(Exception e)
		   {
			   e.printStackTrace();
		   }
	   }
	   return map;
   }
   /**
    * 取得计划列表，必须包含在原计划内的才可以
    * @param plan_id
    * @return
    */
   public ArrayList getPlanList(String plan_id)
   {
	   ArrayList list = new ArrayList();
	   RowSet rs = null;
	   try
	   {
		   ContentDAO dao  = new ContentDAO(this.conn);
		   StringBuffer sql = new StringBuffer("");
		   sql.append("select plan_id,name,theyear,themonth,thequarter,start_date,end_date,cycle,");
		   sql.append(Sql_switcher.year("start_date")+" as sy,"+Sql_switcher.month("start_date")+" as sm, ");
		   sql.append(Sql_switcher.year("end_date")+" as ey,"+Sql_switcher.month("end_date")+" as em,");
		   sql.append(Sql_switcher.isnull("a0000", "99999")+" as norder,status from per_plan ");
		   sql.append("where method=2 and object_type=2 and status=8 ");
		   sql.append(" and plan_id in ");
		   sql.append("( select distinct plan_id from per_object where ");
		   sql.append(" (sp_flag is null or sp_flag='01' or sp_flag='07'))");
		   RecordVo vo = new RecordVo("per_plan");
		   vo.setInt("plan_id", Integer.parseInt(plan_id));
		   vo = dao.findByPrimaryKey(vo);//考核周期:(0|1|2|3|7)=(年度|半年|季度|月度|不定期)
		   int cycle = vo.getInt("cycle");
		   sql.append(" order by norder asc,plan_id desc ");
		   rs = dao.search(sql.toString());
		   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		   /**thequarter=1,2上半年，下半年，thequarter=01，02，03，04第一，二，三，四季度*/
		   while(rs.next())
		   {
			//   if(plan_id.equalsIgnoreCase(rs.getString("plan_id")))
			//	   continue;
			   int rscycle = rs.getInt("cycle");
			   if(cycle==0)//可以包含同年的所有，但是不定期的要判断一下
			   {
				   String theyear = vo.getString("theyear");
				   if(rscycle==7)
				   {
					  String sy = rs.getString("sy");
					  String ey = rs.getString("ey");
					  if(Integer.parseInt(theyear)<Integer.parseInt(sy)||Integer.parseInt(theyear)>Integer.parseInt(ey))
						  continue;
				   }
				   else
				   {
					   if(!rs.getString("theyear").equals(theyear))
						   continue;
				   }
			   }
			   else if(cycle==1)//半年度，可以包含半年度，季度，月度，不定期
			   {
				   String theyear = vo.getString("theyear");
				   String temp =  vo.getString("thequarter");
				   if(rscycle==0||(rscycle!=7&&!theyear.equals(rs.getString("theyear"))))//年度
				   {
					   continue;
				   }
				   else if(rscycle==1)//半年
				   {
					  if(!temp.equals(rs.getString("thequarter")))
						  continue;
					   
				   }else if(rscycle==2){
					   if("1".equals(temp)&&("03".equals(rs.getString("thequarter"))|| "04".equals(rs.getString("thequarter"))))//上半年
					   {
						   continue;
					   }
					   else if("2".equals(temp)&&("01".equals(rs.getString("thequarter"))|| "02".equals(rs.getString("thequarter"))))//下半年
					   {
						   continue;
					   }
				   }else if(rscycle==3){
					   if("1".equals(temp)&&("07".equals(rs.getString("themonth"))|| "08".equals(rs.getString("themonth"))
							   || "09".equals(rs.getString("themonth"))|| "10".equals(rs.getString("themonth"))|| "11".equals(rs.getString("themonth"))
							   || "12".equals(rs.getString("themonth"))))//上半年
					   {
						   continue;
					   }
					   else if("2".equals(temp)&&("01".equals(rs.getString("themonth"))|| "02".equals(rs.getString("themonth"))
							   || "03".equals(rs.getString("themonth"))|| "04".equals(rs.getString("themonth"))|| "05".equals(rs.getString("themonth"))
							   || "06".equals(rs.getString("themonth"))))//下半年
					   {
						   continue;
					   }
				   }
				   else if(rscycle==7)
				   {
					   String sy=rs.getString("sy");
					   String sm=rs.getString("sm");
					   String ey=rs.getString("ey");
					   String em=rs.getString("em");
					   if(Integer.parseInt(theyear)<Integer.parseInt(sy)||Integer.parseInt(theyear)>Integer.parseInt(ey))//在考核周期之外
					   {
						   continue;
					   }
					   else
					   {
						   if(Integer.parseInt(theyear)==Integer.parseInt(sy))
						   {
							   if("1".equals(temp)&&DesignateTaskBo.getIntMonth(sm)>6)
								   continue;
						   }else if(Integer.parseInt(theyear)==Integer.parseInt(ey))
						   {
							   if("2".equals(temp)&&DesignateTaskBo.getIntMonth(em)<6)
								   continue;
						   }
					   }
				   }
			   }
			   else if(cycle==2)//季度可以包含：季度，月度，不定期
			   {
				   String theyear = vo.getString("theyear");
				   String temp =  vo.getString("thequarter");
				   if(rscycle==0||rscycle==1||(rscycle!=7&&!theyear.equals(rs.getString("theyear"))))//年度
				   {
					   continue;
				   }
				   else if(rscycle==2)//季度
				   {
					   if(!temp.equals(rs.getString("thequarter")))
						   continue;
				   }
				   else if(rscycle==3)//月度
				   {
					   if("01".equals(temp)&&!"01".equals(rs.getString("themonth"))&&!"02".equals(rs.getString("themonth"))&&!"03".equals(rs.getString("themonth")))
					   {
						   continue;
					   }
					   else if("02".equals(temp)&&!"04".equals(rs.getString("themonth"))&&!"05".equals(rs.getString("themonth"))&&!"06".equals(rs.getString("themonth")))
					   {
						   continue;
					   }else if("03".equals(temp)&&!"07".equals(rs.getString("themonth"))&&!"08".equals(rs.getString("themonth"))&&!"09".equals(rs.getString("themonth")))
					   {
						   continue;
					   }else if("04".equals(temp)&&!"10".equals(rs.getString("themonth"))&&!"11".equals(rs.getString("themonth"))&&!"12".equals(rs.getString("themonth")))
					   {
						   continue;
					   }
				   }
				   else if(rscycle==7)//不定期
				   {
					   String sy=rs.getString("sy");
					   String sm=rs.getString("sm");
					   String ey=rs.getString("ey");
					   String em=rs.getString("em");
					   if(Integer.parseInt(theyear)<Integer.parseInt(sy)||Integer.parseInt(theyear)>Integer.parseInt(ey))
						   continue;
					   else{
						   if(theyear.equals(sy))
						   {
							   if("01".equals(temp)&&DesignateTaskBo.getIntMonth(sm)>1)
							   {
								   continue;
							   }else if("02".equals(temp)&&DesignateTaskBo.getIntMonth(sm)>4)
							   {
								   continue;
							   }else if("03".equals(temp)&&DesignateTaskBo.getIntMonth(sm)>7)
							   {
								   continue;
							   }else if("04".equals(temp)&&DesignateTaskBo.getIntMonth(sm)>10)
							   {
								   continue;
							   }
								   
						   }
						   if(theyear.equals(ey))
						   {
							   if("01".equals(temp)&&DesignateTaskBo.getIntMonth(em)<3)
							   {
								   continue;
							   }else if("02".equals(temp)&&DesignateTaskBo.getIntMonth(em)<6)
							   {
								   continue;
							   }else if("03".equals(temp)&&DesignateTaskBo.getIntMonth(em)<9)
							   {
								   continue;
							   }else if("04".equals(temp)&&DesignateTaskBo.getIntMonth(em)<12)
							   {
								   continue;
							   }
								   
						   }
					   }
				   }
			   }
			   else if(cycle==3)//月度可以包含：月度，不定期，考核周期:(0|1|2|3|7)=(年度|半年|季度|月度|不定期)
			   {
				   String theyear = vo.getString("theyear");
				   String temp =  vo.getString("themonth");
				   if(rscycle==0||rscycle==1||rscycle==2||(rscycle!=7&&!theyear.equals(rs.getString("theyear"))))
					   continue;
				   else{
					   if(rscycle==3)
					   {
						   if(!temp.equals(rs.getString("themonth")))
							   continue;
					   }else if(rscycle==7)
					   {
						   String sy=rs.getString("sy");
						   String sm=rs.getString("sm");
						   String ey=rs.getString("ey");
						   String em=rs.getString("em");
						   if(Integer.parseInt(theyear)<Integer.parseInt(sy)||Integer.parseInt(theyear)>Integer.parseInt(ey))
							   continue;
						   else if(DesignateTaskBo.getIntMonth(temp)!=Integer.parseInt(sm)&&DesignateTaskBo.getIntMonth(temp)!=Integer.parseInt(em))
							   continue;
					   }
				   }
			   }
			   else if(cycle==7)//不定期的，？？？
			   {
				   String sy=rs.getString("sy");
				   String sm=rs.getString("sm");
				   String ey=rs.getString("ey");
				   String em=rs.getString("em");
				   String std=format.format(vo.getDate("start_date"));
				   String etd=format.format(vo.getDate("end_date"));
				   String std_y=std.substring(0,4);
				   String std_m=std.substring(5,7);
				   String etd_y=etd.substring(0,4);
				   String etd_m=etd.substring(5,7);
				   String theyear = rs.getString("theyear");
				   String temp=rs.getString("thequarter");
				   if(rscycle==0)
				   {
					   if(Integer.parseInt(theyear)<Integer.parseInt(std_y)||Integer.parseInt(theyear)>Integer.parseInt(etd_y))
							  continue;
				   }else if(rscycle==1){//harf year
					   if(Integer.parseInt(theyear)<Integer.parseInt(std_y)||Integer.parseInt(theyear)>Integer.parseInt(etd_y))//在考核周期之外
					   {
						   continue;
					   }
					   else
					   {
						   if(Integer.parseInt(theyear)==Integer.parseInt(std_y))
						   {
							   if("1".equals(temp)&&DesignateTaskBo.getIntMonth(std_m)>6)
								   continue;
						   }else if(Integer.parseInt(theyear)==Integer.parseInt(ey))
						   {
							   if("2".equals(temp)&&DesignateTaskBo.getIntMonth(etd_m)<6)
								   continue;
						   }
					   }
				   }else if(rscycle==2)
				   {
					   if(Integer.parseInt(theyear)<Integer.parseInt(std_y)||Integer.parseInt(theyear)>Integer.parseInt(etd_y))//在考核周期之外
					   {
						   continue;
					   }else
					   {
						   if(theyear.equals(std_y))
						   {
							   if("01".equals(temp)&&DesignateTaskBo.getIntMonth(std_m)>1)
							   {
								   continue;
							   }else if("02".equals(temp)&&DesignateTaskBo.getIntMonth(std_m)>4)
							   {
								   continue;
							   }else if("03".equals(temp)&&DesignateTaskBo.getIntMonth(std_m)>7)
							   {
								   continue;
							   }else if("04".equals(temp)&&DesignateTaskBo.getIntMonth(std_m)>10)
							   {
								   continue;
							   }  
						   }
						   if(theyear.equals(etd_y))
						   {
							   if("01".equals(temp)&&DesignateTaskBo.getIntMonth(etd_m)<3)
							   {
								   continue;
							   }else if("02".equals(temp)&&DesignateTaskBo.getIntMonth(etd_m)<6)
							   {
								   continue;
							   }else if("03".equals(temp)&&DesignateTaskBo.getIntMonth(etd_m)<9)
							   {
								   continue;
							   }else if("04".equals(temp)&&DesignateTaskBo.getIntMonth(etd_m)<12)
							   {
								   continue;
							   } 
						   }
					   }
				   }else if(rscycle==3)
				   {
					   if(Integer.parseInt(theyear)<Integer.parseInt(std_y)||Integer.parseInt(theyear)>Integer.parseInt(etd_y))
						   continue;
					   else if(DesignateTaskBo.getIntMonth(temp)!=Integer.parseInt(std_m)&&DesignateTaskBo.getIntMonth(temp)!=Integer.parseInt(etd_m))
						   continue;
				   }else
				   {
					   if(Integer.parseInt(std_y)>Integer.parseInt(ey)||Integer.parseInt(etd_y)<Integer.parseInt(sy))
						   continue;
				   }
			   }
			   CommonData cd = new CommonData(rs.getString("plan_id"),rs.getString("plan_id")+"."+rs.getString("name")+"(已分发)");
			   list.add(cd);
		   }
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }finally{
		   try
		   {
			   if(rs!=null)
				   rs.close();
		   }catch(Exception e)
		   {
			   e.printStackTrace();
		   }
	   }
	   return list;
   }
   public static int getIntMonth(String month)
   {
	   int intmonth=1;
	   if("01".equals(month))
		   intmonth=1;
	   else if("02".equals(month))
		   intmonth=2;
	   else if("03".equals(month))
		   intmonth=3;
	   else if("04".equals(month))
		   intmonth=4;
	   else if("05".equals(month))
		   intmonth=5;
	   else if("06".equals(month))
		   intmonth=6;
	   else if("07".equals(month))
		   intmonth=7;
	   else if("08".equals(month))
		   intmonth=8;
	   else if("09".equals(month))
		   intmonth=9;
	   else if("10".equals(month))
		   intmonth=10;
	   else if("11".equals(month))
		   intmonth=11;
	   else if("12".equals(month))
		   intmonth=12;
	   return intmonth;
   }
   public ArrayList getPlanItem(String plan_id)
   {
	   ArrayList list = new ArrayList();
	   RowSet rs = null;
	   try
	   {
		   if(plan_id==null|| "".equals(plan_id))
			   return list;
		   RecordVo vo = new RecordVo("per_plan");
		   ContentDAO dao  = new ContentDAO(this.conn);
		   vo.setInt("plan_id", Integer.parseInt(plan_id));
		   vo = dao.findByPrimaryKey(vo);
		   rs = dao.search("select item_id,itemdesc,score,rank,child_id from per_template_item where UPPER(template_id)='"+vo.getString("template_id").toUpperCase()+"' " +
		   		" and item_id not in (select item_id from per_template_point) order by seq");
		   while(rs.next())
		   {
			   String child_id=rs.getString("child_id");
			   if(child_id==null|| "".equals(child_id))//只选叶子节点
			   {
		    	   CommonData cd = new CommonData(rs.getString("item_id"),rs.getString("itemdesc"));
		    	   list.add(cd);
			   }
		   }
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }finally{
		   if(rs!=null)
		   {
			   try
			   {
				  rs.close(); 
			   }catch(Exception e)
			   {
				   e.printStackTrace();
			   }
		   }
	   }
	   return list;
   }
  /* public String getPrivStr(){
	   StringBuffer sb = new StringBuffer("");
	   if(!this.userView.isSuper_admin()){
		   boolean flag=false;
		   int status = this.userView.getStatus();//4自助用户,0业务用户
		   String unit_id="";
		   if(status==0){//业务用户
			   unit_id = this.userView.getUnitIdByBusi("5");//取业务操作单位
			   if(unit_id.length()==0){//如果没有业务操作单位，则取操作单位
				   unit_id = this.userView.getUnit_id();//取操作单位
				   if(unit_id.length()==0){//如果没有操作单位，则取管理范围
					   unit_id = this.userView.getManagePrivCode();//管理范围
				   }
				   flag = true;
			   }else{
				   flag = true;
			   }
		   }else{//自助用户
			   
		   }
		   if(flag){
			   sb.append(" and (b0110 like '"+unit_id+"%' or e0122 like '"+unit_id+"%')");
		   }
	   }
	   return sb.toString();
   }*/
   
   /**
	 * 获取用户权限
	 * 1、业务用户：先取业务操作单位->操作单位->管理范围
   * 2、自助用户：业务操作单位（若关联了业务用户，取业务用户的业务操作单位，否则取自己的业务操作单位）->管理范围->所在单位
	 * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围
	 */
  public String getUserViewPersonWhere()
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
						tempSql.append(" or b0110 like '" + temp[i].substring(2) + "%'");
					else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or e0122 like '" + temp[i].substring(2) + "%'");
				}
				buf.append(" and ( " + tempSql.substring(3) + " ) ");
			} 
			else if((!userView.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{
				String codeid = userView.getManagePrivCode();
				String codevalue = userView.getManagePrivCodeValue();
				String a_code = codeid + codevalue;

				if (a_code.trim().length() > 0)// 说明授权了
				{
					if ("UN".equalsIgnoreCase(a_code))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
						buf.append(" and 1=1 ");
					else if("UN".equalsIgnoreCase(codeid))
						buf.append(" and b0110 like '" + codevalue + "%'");
					else if("UM".equalsIgnoreCase(codeid))
						buf.append(" and e0122 like '" + codevalue + "%'");
					else if("@K".equalsIgnoreCase(codeid))
						buf.append(" and e01a1 like '" + codevalue + "%'");
					else
						buf.append(" and b0110 like '" + codevalue + "%'");
						
				} else
					buf.append(" and 1=2 ");
			}
			str = buf.toString();
		}

		return str;		
	}
	
	
   public ArrayList getObjectList(String plan_id,String p0400,String group_id,String task_type,String item_id)
   {
	   ArrayList list = new ArrayList();
	   RowSet rs = null;
	   try
	   {
		   if(plan_id==null|| "".equals(plan_id))
			   return list;
		   StringBuffer buf = new StringBuffer();
		   buf.append(" select p.a0100,p.plan_id,p.p0400,t.task_id from p04 p,per_designate_task t where ");
		   buf.append(" p.plan_id="+plan_id+" and ");
		   buf.append(" p.p0400=");
		   buf.append(" t.to_p0400 ");
		   buf.append(" and t.p0400="+p0400+" and p.a0100=t.to_a0100");
		   buf.append(" and t.group_id="+group_id);
		   buf.append(" and task_type="+task_type);
	  //   buf.append(" and p.item_id="+item_id);
		   ContentDAO dao = new ContentDAO(this.conn);
		   rs=dao.search(buf.toString());
		   HashMap existMap = new HashMap();
		   while(rs.next())
		   {
			   String str=rs.getString("p0400")+"`"+rs.getString("task_id");
			   existMap.put(rs.getString("a0100"), str);
		   }
		   if(rs!=null)
			   rs.close();

		   
		   LoadXml loadxml = new LoadXml(this.conn, plan_id);
		   Hashtable htxml = new Hashtable();
		   htxml = loadxml.getDegreeWhole();
		   String targetAllowAdjustAfterApprove = (String) htxml.get("TargetAllowAdjustAfterApprove");//目标卡批准后允许再调整, (True, False, 默认为True)
		   
		 //不能同时即为主办人，又为协办人
		   rs = dao.search("select p0400,task_id,to_a0100 from per_designate_task where p0400="+p0400+" and group_id="+group_id+" and task_type=2");
		   while(rs.next())
		   {
			   String str=rs.getString("p0400")+"`"+rs.getString("task_id");
			   existMap.put(rs.getString("to_a0100"), str);
		   }
		   buf.setLength(0);
		   buf.append("select object_id,sp_flag,a0101,e0122 from per_object where plan_id="+plan_id);
		   buf.append(this.getUserViewPersonWhere());
		   buf.append(" order by a0000 ");
		   rs = dao.search(buf.toString());
		   while(rs.next())
		   {
			   LazyDynaBean bean = new LazyDynaBean();
			   String sp_flag=rs.getString("sp_flag");
			   if(sp_flag==null|| "".equals(sp_flag))
				   sp_flag="01";
			   String visibleCheckBox = "0";//不显示选择狂框
			   if("01".equals(sp_flag)|| "07".equals(sp_flag))
				   visibleCheckBox="1";//显示选择狂框
			   
			   if("true".equalsIgnoreCase(targetAllowAdjustAfterApprove)){//目标卡批准后允许再调整 可以下达任务chent 20161009
				   visibleCheckBox="1";//显示选择狂框
			   }
			   String isChecked="0";//默认不被选中
			   String isNew="0";//选中后，是否新增加一条记录，如果是原来选过的，就不增加新记录了，但是如果原来选过，但是这回没选，要删除记录；
			   String astr="-1`-1";
			   if(existMap.get(rs.getString("object_id"))!=null)//已经存在，再选不会在新增加一条记录，
			   {
				   continue;
			   }
			   String hiddenStr=isNew+"`"+rs.getString("object_id")+"`"+astr+"`"+sp_flag;
			   bean.set("hiddenStr", hiddenStr);
			   bean.set("e0122",AdminCode.getCodeName("UM",rs.getString("e0122")));
			   bean.set("a0101", rs.getString("a0101"));
			   bean.set("visibleCheckBox", visibleCheckBox);
			   bean.set("isChecked", isChecked);
			   list.add(bean);   
		   } 
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }finally{
		   if(rs!=null)
		   {
			   try
			   {
				  rs.close(); 
			   }catch(Exception e)
			   {
				   e.printStackTrace();
			   }
		   }
	   }
	   return list;
   }
   public void  deleteTask(String type,String taskid,String p0400,String group_id)
   {
	   RowSet rs = null;
	   try
	   {
		   ContentDAO dao = new ContentDAO(this.conn);
		   if("0".equalsIgnoreCase(type))//还没有实际的下发任务
		   {
			 dao.delete("delete from per_designate_task where task_id="+taskid, new ArrayList());  
		   }
		   else{
			   /**删除下达任务，直接删除目标卡状态为起草和驳回的的考核对象的任务(计划为分发的才可以删除)，并且级联删除调整过的任务*/
			   StringBuffer buf = new StringBuffer("");
			   buf.append("select p04.b0110,p04.a0100,p04.p0400,p04.f_p0400,p04.plan_id,po.sp_flag,pp.status from p04 left join per_object po on p04.plan_id=po.plan_id ");
			   buf.append(" and p04.a0100=po.object_id left join per_plan pp on p04.plan_id=pp.plan_id ");
	    	   buf.append(" where p04.p0400 in (");
	    	   buf.append("select to_p0400 from per_designate_task where p0400=");
	    	   buf.append(p0400+" and group_id="+group_id+")");
	    	   rs=dao.search(buf.toString());
	    	   StringBuffer ids = new StringBuffer("");
	    	   while(rs.next())
	    	   {
	    		   String sp_flag=rs.getString("sp_flag")==null?"01":rs.getString("sp_flag");
	    		   String plan_id=rs.getString("plan_id");
    	    	   String object_id=rs.getString("a0100");
    	    	   String status=rs.getString("status");
	    		   if("8".equals(status)&&("01".equals(sp_flag)|| "07".equals(sp_flag)))//计划为分发，审批状态为起草或驳回的才可以删除
	    		   {
	    	    	   String ap0400=rs.getString("p0400");
	    	    	   ids.append(","+ap0400);
	    	    	   this.deleteP04(ap0400);
	    		   }  
	    	   }
	    	   buf.setLength(0);
	    	   int minTaskid=0;
	    	   RecordVo minVo = new RecordVo("per_designate_task");
	    	   buf.append(" select min(task_id) from per_designate_task where p0400="+p0400);
	    	   buf.append(" and group_id="+group_id);
	    	   if(rs!=null)
	    		   rs.close();
	    	   rs = dao.search(buf.toString());
	    	   while(rs.next())
	    		   minTaskid=rs.getInt(1);
	    	   if(minTaskid!=0)
	    	   {
	        	   minVo.setInt("task_id", minTaskid);
	        	   minVo=dao.findByPrimaryKey(minVo);
	    	   }
	    	   
	    	   buf.setLength(0);
	    	   if(ids.toString().length()>0)//有问题
	    	   {
	        	   buf.append("delete from per_designate_task where p0400=");
	        	   buf.append(p0400+" and group_id="+group_id);
	        	   buf.append(" and to_p0400 in ("+ids.toString().substring(1)+")");
	        	   dao.delete(buf.toString(), new ArrayList());
	    	   }
	    	   //如果主办人自己删除了目标卡任务，这样目标卡里就没有这个任务了，导致下达人在来删除的时候，删除不掉这个任务
	    	   buf.setLength(0);
	    	   buf.append("delete from per_designate_task where p0400=");
        	   buf.append(p0400+" and group_id="+group_id);
        	   buf.append(" and to_p0400 not in (select p0400 from p04)  and task_type=1");//会不会效率问题？
        	   dao.delete(buf.toString(), new ArrayList());
	    	   //删除协办人
	    	   buf.setLength(0);
	    	   buf.append("delete from per_designate_task where p0400=");
        	   buf.append(p0400+" and group_id="+group_id);
        	   buf.append(" and task_type=2");
        	   dao.delete(buf.toString(), new ArrayList());
        	   if(rs!=null)
        		   rs.close();
        	   int currMinTaskId=0;
        	   buf.setLength(0);
        	   buf.append(" select min(task_id) from per_designate_task where p0400="+p0400);
	    	   buf.append(" and group_id="+group_id);
	    	   while(rs.next())
	    	   {
	    		   currMinTaskId=rs.getInt(1);
	    	   }
	    	   if(currMinTaskId!=0&&currMinTaskId!=minTaskid)//这个地方，主要是怕删除下达任务后，打乱下达任务的显示顺序，
	    	   {
	    		   dao.update("update per_designate_task set task_id="+minTaskid+" where task_id="+currMinTaskId);
	    	   }else if(currMinTaskId==0)
	    	   {
	    		   /*RecordVo avo = new RecordVo("per_designate_task");
	    		   avo.setInt("task_id", minTaskid);
	    		   avo.setInt("p0400",Integer.parseInt(p0400));
	    		   avo.setInt("group_id",Integer.parseInt(group_id));
	    		   avo.setString("to_p0407", minVo.getString("to_p0407")==null?"":minVo.getString("to_p0407"));
	    		   avo.setInt("task_type",0);
	    		   dao.addValueObject(avo);*/
	    	   } 
		   }
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }finally{
		   try
		   {
			   if(rs!=null)
				   rs.close();
		   }
		   catch(Exception e)
		   {
			   e.printStackTrace();
		   }
	   }
   }
   /**
    * 删除单个人
    */
   public void deleteSinglePeople(String task_id,String p0400,String group_id)
   {
	   RowSet rs = null;
	   try
	   {
		   ContentDAO dao = new ContentDAO(this.conn);
		   int minTaskid=0;
    	   RecordVo minVo = new RecordVo("per_designate_task");
    	   StringBuffer buf = new StringBuffer();
    	   buf.append(" select min(task_id) from per_designate_task where p0400="+p0400);
    	   buf.append(" and group_id="+group_id);
    	   rs=dao.search(buf.toString());
    	   while(rs.next())
    	   {
    		   minTaskid=rs.getInt(1);
    	   }
    	   if(minTaskid!=0)
    	   {
        	   minVo.setInt("task_id", minTaskid);
        	   minVo=dao.findByPrimaryKey(minVo);
    	   }
    	   buf.setLength(0);
    	   RecordVo delVo = new RecordVo("per_designate_task");
    	   delVo.setInt("task_id", Integer.parseInt(task_id));
    	   delVo = dao.findByPrimaryKey(delVo);
    	   int to_p0400=delVo.getInt("to_p0400");
    	   int task_type=delVo.getInt("task_type");
    	   if(task_type==1)
    	      this.deleteP04(to_p0400+"");//删除目标卡，附件
    	   dao.delete("delete from per_designate_task where task_id="+task_id, new ArrayList());
    	   int currMinTaskId=0;
    	   buf.setLength(0);
    	   buf.append(" select min(task_id) from per_designate_task where p0400="+p0400);
    	   buf.append(" and group_id="+group_id);
    	   rs=dao.search(buf.toString());
    	   while(rs.next())
    	   {
    		   currMinTaskId=rs.getInt(1);
    	   }
    	   if(currMinTaskId!=0&&currMinTaskId!=minTaskid)//这个地方，主要是怕删除下达任务后，打乱下达任务的显示顺序，
    	   {
    		   dao.update("update per_designate_task set task_id="+minTaskid+" where task_id="+currMinTaskId);
    	   }else if(currMinTaskId==0)
    	   {
    		   RecordVo avo = new RecordVo("per_designate_task");
    		   avo.setInt("task_id", minTaskid);
    		   avo.setInt("p0400",Integer.parseInt(p0400));
    		   avo.setInt("group_id",Integer.parseInt(group_id));
    		   avo.setString("to_p0407", minVo.getString("to_p0407")==null?"":minVo.getString("to_p0407"));
    		   avo.setInt("task_type",0);
    		   dao.addValueObject(avo);
    	   }
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }finally{
		   if(rs!=null)
		   {
			   try
			   {
				   rs.close();
			   }
			   catch(Exception e)
			   {
				   e.printStackTrace();
			   }
		   }
	   }
   }
   public void deleteP04(String p0400)
   {
	   RowSet rs = null;
	   try
	   {
		   ContentDAO dao = new ContentDAO(this.conn);
		   dao.delete("delete from p04 where p0400="+p0400, new ArrayList());
		   //删除附件
		   dao.delete("delete from per_article where article_type=3 and task_id="+p0400, new ArrayList());
		   rs = dao.search("select p0400 from p04 where f_p0400="+p0400);//删除调整的任务
		   if(rs.next())
		   {
			   this.deleteP04(rs.getString("p0400"));
		   }
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }finally{
		   try
		   {
			   if(rs!=null)
				   rs.close();
		   }catch(Exception e)
		   {
			   e.printStackTrace();
		   }
	   }
   }
   
   /**
	 * 取得 模板项目记录
	 * @return
	 */
	public ArrayList getTemplateItemList()
	{
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "select * from per_template_item where template_id='"+this.plan_vo.getString("template_id")+"' ";
			sql+=" order by seq";
			rowSet = dao.search(sql);		
		    LazyDynaBean abean = null;
			while(rowSet.next())
		    {
				abean = new LazyDynaBean();
		    	abean.set("item_id",rowSet.getString("item_id"));
		    	abean.set("parent_id",rowSet.getString("parent_id")!=null?rowSet.getString("parent_id"):"");
		    	abean.set("child_id",rowSet.getString("child_id")!=null?rowSet.getString("child_id"):"");
		    	abean.set("template_id",rowSet.getString("template_id"));
		    	abean.set("itemdesc",rowSet.getString("itemdesc"));
		    	abean.set("seq",rowSet.getString("seq"));
		    	
		    	list.add(abean);
		    }
		
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 递归查找叶子项目列表
	 */
	public void get_LeafItemList()
	{
		try
		{			
			LazyDynaBean abean=null;
			for(int i=0;i<this.templateItemList.size();i++)
			{
				abean=(LazyDynaBean)this.templateItemList.get(i);
				String parent_id=(String)abean.get("parent_id");
				if(parent_id.length()==0)
				{
				//	ArrayList tempList=new ArrayList();
				//	tempList.add(abean);
					setLeafItemFunc(abean);
				}
			}			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}	
    //	递归查找叶子项目
	public void setLeafItemFunc(LazyDynaBean abean)
	{
		String item_id=(String)abean.get("item_id");
		String child_id=(String)abean.get("child_id");
		
		//判断项目下是否有指标		
		if(child_id.length()==0)
		{
			this.leafItemList.add(abean);
			return;
		}
		else 
		{
			LazyDynaBean bean=null;
			for(int i=0;i<this.p04List.size();i++)
			{
				bean=(LazyDynaBean)this.p04List.get(i);
				String _item_id=(String)bean.get("item_id");
				if(_item_id.equalsIgnoreCase(item_id))
				{
					this.leafItemList.add(abean);
					break;
				}
			}
		}
		LazyDynaBean a_bean=null;
		for(int j=0;j<this.templateItemList.size();j++)
		{
			a_bean=(LazyDynaBean)this.templateItemList.get(j);
			String parent_id=(String)a_bean.get("parent_id");
			if(parent_id.equals(item_id))
				setLeafItemFunc(a_bean);
		}
	}
	
	/**
	 * 取得项目对应任务map
	 * @return
	 */
	public HashMap getItemToPointMap()
	{
		HashMap map = new HashMap();
		try
		{
			LazyDynaBean abean=null;
			for(int i=0;i<this.p04List.size();i++)
			{
				abean=(LazyDynaBean)this.p04List.get(i);
				String p0400=(String)abean.get("p0400");
				String item_id=(String)abean.get("item_id");
				ArrayList sublist = (ArrayList)abean.get("sublist");
				if(map.get(item_id)!=null)
				{
					String lay = (String)map.get(item_id);
					int layer = Integer.parseInt(lay)+sublist.size()+(sublist.size()==0?1:0);//如果本身新增了指标的时候，多加1就会多一个空白行
					map.put(item_id,String.valueOf(layer));
				}
				else
				{
				//	ArrayList tempList=new ArrayList();
				//	tempList.add(abean);
					map.put(item_id,String.valueOf(sublist.size()+(sublist.size()==0?1:0)));
				}
			}			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 叶子项目对应的继承关系
	 * @return
	 */
	public HashMap getLeafItemLinkMap()
	{
		HashMap map = new HashMap();
		try
		{
			LazyDynaBean abean = null;
			for(int i=0;i<this.leafItemList.size();i++)
			{
				abean=(LazyDynaBean)this.leafItemList.get(i);
				String item_id=(String)abean.get("item_id");
				String parent_id=(String)abean.get("parent_id");
				ArrayList linkList=new ArrayList();
				getParentItem(linkList,abean);
				if(linkList.size()>lay)
					lay=linkList.size();
				map.put(item_id,linkList);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}	
	//寻找继承关系
	public void getParentItem(ArrayList list,LazyDynaBean abean)
	{
		String item_id=(String)abean.get("item_id");
		String parent_id=(String)abean.get("parent_id");
		if(parent_id.length()==0)
		{
			list.add(abean);
			return;
		}
		LazyDynaBean a_bean=null;
		for(int i=0;i<this.templateItemList.size();i++)
		{
			a_bean=(LazyDynaBean)this.templateItemList.get(i);
			String itemid=(String)a_bean.get("item_id");
			String parentid=(String)a_bean.get("parent_id");
			if(itemid.equals(parent_id))
			{
				list.add(abean);
				getParentItem(list,a_bean);
			}			
		}				
	}
	
	/**
	 * 取得项目拥有的节点数
	 * @return
	 */
	public HashMap getItemPointNum()
	{
		HashMap map=new HashMap();
		LazyDynaBean a_bean=null;
		LazyDynaBean aa_bean=null;
		for(int i=0;i<this.templateItemList.size();i++)
		{
			a_bean=(LazyDynaBean)this.templateItemList.get(i);
			ArrayList list=new ArrayList();
			getLeafItemList(a_bean,list);
			int n=0;
			for(int j=0;j<list.size();j++)
			{
				aa_bean=(LazyDynaBean)list.get(j);
				String item_id=(String)aa_bean.get("item_id");
				if(this.itemToPointMap.get(item_id)!=null)				
					n+=Integer.parseInt((String)this.itemToPointMap.get(item_id));
				//else
					//n+=1;
			}
			map.put((String)a_bean.get("item_id"),new Integer(n));
		}
		return map;
	}		
	public void getLeafItemList(LazyDynaBean abean,ArrayList list)
	{
		String item_id=(String)abean.get("item_id");
		String child_id=(String)abean.get("child_id");
		
		if(child_id.length()==0)
		{
			list.add(abean);
				return;
		}
		else if(this.itemToPointMap.get(item_id)!=null)  //**
		{
			list.add(abean);
		}
				
		LazyDynaBean a_bean=null;
		for(int j=0;j<this.templateItemList.size();j++)
		{
			a_bean=(LazyDynaBean)this.templateItemList.get(j);
			String parent_id=(String)a_bean.get("parent_id");
			if(parent_id.equals(item_id))
				getLeafItemList(a_bean,list);
		}
		
	}
	public int getLay() {
		return lay;
	}
	public void setLay(int lay) {
		this.lay = lay;
	}

	
/*******************************************任务分解*******************************************************/	
	
	/**
	 * 取得需要展现的json格式的数据的表头
	 * @return
	 */
	public String getColumnsHead(String cardTaskNameDesc,String targetTraceItem)
	{
		String jsonHead = "";
		try
		{			
			FieldItem fielditem = DataDictionary.getFieldItem("p0407");			

			if(cardTaskNameDesc==null || cardTaskNameDesc.trim().length()<=0)
			{				
	    		if(fielditem==null || "任务内容".equalsIgnoreCase(fielditem.getItemdesc().trim()))
	    		{
		    		if(SystemConfig.getPropertyValue("clientName")!=null && "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))) //中国联通
		    			cardTaskNameDesc = "工作目标";
		     		else
		     			cardTaskNameDesc = ResourceFactory.getProperty("jx.khplan.point");
		    	}
		    	else
		    		cardTaskNameDesc = fielditem.getItemdesc();
			}
			
			StringBuffer columns = new StringBuffer("");			
			columns.append("[{ ");			
			columns.append("header:'" + cardTaskNameDesc + "', ");
			columns.append("width:200, ");
		//	columns.append("locked: true, ");
			columns.append("dataIndex:'p0407' ");
			columns.append("},{ ");
			columns.append("header:'主办人', ");
			columns.append("width:70, ");
		//	columns.append("locked: true, ");
			columns.append("dataIndex:'user' ");
			columns.append("}");
			
			if(targetTraceItem!=null && targetTraceItem.trim().length()>0)
			{
				String[] items = targetTraceItem.split(",");
				for (int i = 0; i < items.length; i++)
				{
					if("attachment".equalsIgnoreCase(items[i].toLowerCase()))
						continue;
					fielditem = DataDictionary.getFieldItem(items[i].toLowerCase());					
					columns.append(",{ ");
					columns.append("header:'" + fielditem.getItemdesc() + "', ");
					if("M".equalsIgnoreCase(fielditem.getItemtype()) || "A".equalsIgnoreCase(fielditem.getItemtype()))
						columns.append("width:150, ");
					else if("N".equalsIgnoreCase(fielditem.getItemtype()))
						columns.append("width:70, ");
					else
						columns.append("width:100, ");
					columns.append("dataIndex:'" + items[i].toLowerCase() + "' ");
					columns.append("}");										
				}
			}
			columns.append("] ");			
			jsonHead = columns.toString();						
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return jsonHead;
	}
		
	/**
	 * 取得需要展现的json格式的数据的表体
	 * @return
	 */
	public String getDataJson(String plan_id,String p0400,String targetTraceItem)
	{
		StringBuffer datajson = new StringBuffer("");
		RowSet rowSet = null;
		RowSet rs = null;
		FieldItem fielditem = null;	
		try
		{	
			DecimalFormat myformat = new DecimalFormat("########.###");
			SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
			ContentDAO dao = new ContentDAO(this.conn);			
			datajson.append("[{ ");
			String sql = "select * from p04 where p0400='" + p0400 + "' ";
			rowSet = dao.search(sql);				    
			while(rowSet.next())
		    {				
				String p0407 = rowSet.getString("p0407")!=null?rowSet.getString("p0407"):"";			
				datajson.append("p0407:'" + p0407 + "', ");
				datajson.append("user:'" + this.userView.getUserFullName() + "', ");				
				if(targetTraceItem!=null && targetTraceItem.trim().length()>0)
				{
					String[] items = targetTraceItem.split(",");
					for (int i = 0; i < items.length; i++)
					{
						if("attachment".equalsIgnoreCase(items[i].toLowerCase()))
							continue;
						fielditem = DataDictionary.getFieldItem(items[i].toLowerCase());	
						String itemid = "";						
						if("score_org".equalsIgnoreCase(items[i].toLowerCase()))
						{
							itemid = rowSet.getString(items[i])!=null?rowSet.getString(items[i]):"";
							if(itemid!=null && itemid.trim().length()>0)
							{
								if(AdminCode.getCodeName("UN", itemid)!=null && AdminCode.getCodeName("UN", itemid).length()>0)								
									itemid = AdminCode.getCodeName("UN", itemid);								
								else								
									itemid = AdminCode.getCodeName("UM", itemid);																	
							}
						}
						else if("D".equalsIgnoreCase(fielditem.getItemtype()))
						{
							if(rowSet.getDate(items[i])!=null)
								itemid = fm.format(rowSet.getDate(items[i]));							
						}
						else if("M".equalsIgnoreCase(fielditem.getItemtype()))
							itemid = rowSet.getString(items[i])!=null?rowSet.getString(items[i]):"";
						else
						{
							if(fielditem.getCodesetid()==null || "0".equals(fielditem.getCodesetid()) || fielditem.getCodesetid().trim().length()==0)
							{
								if("N".equalsIgnoreCase(fielditem.getItemtype()) && fielditem.getDecimalwidth()>0)
									itemid = rowSet.getString(items[i])==null?"":myformat.format(rowSet.getDouble(items[i]));
								else
								{
									itemid = rowSet.getString(items[i])!=null?rowSet.getString(items[i]):"";
									if("p0419".equalsIgnoreCase(items[i]) && itemid!=null && itemid.trim().length()>0)
										itemid = itemid + "%";	
								}
							}
							else
							{
								if(rowSet.getString(items[i])!=null)																
									itemid = AdminCode.getCodeName(fielditem.getCodesetid(), rowSet.getString(items[i]));																	
							}
						}
												
/*						
						if(fielditem.getItemtype().equalsIgnoreCase("N") && fielditem.getDecimalwidth()!=0)
							itemid = rowSet.getString(items[i])==null?"":myformat.format(rowSet.getDouble(items[i]));						
						else
						{
							itemid = rowSet.getString(items[i])!=null?rowSet.getString(items[i]):"";
							if(items[i].equalsIgnoreCase("p0419") && itemid!=null && itemid.trim().length()>0)
								itemid = itemid + "%";
						}
*/						
						datajson.append("" + items[i].toLowerCase() + ":'" + itemid + "', ");										
					}
				}
		    }
			datajson.append("uiProvider:'col', ");			
			if(getDataJson(p0400)) // 判断是否又把此任务向下分解了
			{							
				datajson.append("cls:'master-task', ");
				datajson.append("iconCls:'task-folder', ");
				datajson.append("children:[ { ");
				
				// 递归查找分解的任务
				String extjson = getDesignateTask(p0400,targetTraceItem);
				datajson.append(extjson);
			}
			else
			{							
				datajson.append("leaf:true, ");
				datajson.append("iconCls:'task' ");				
			}
			
			datajson.append("}] ");			
			
			if(rowSet!=null)
				rowSet.close();
			if(rs!=null)
				rs.close();	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return datajson.toString();
	}
	
	/**
	 * 递归查找分解的任务
	 */
	public String getDesignateTask(String p0400,String targetTraceItem)
	{
		StringBuffer datajson = new StringBuffer("");
		RowSet rowSet = null;
		FieldItem fielditem = null;	
		try
		{	
			DecimalFormat myformat = new DecimalFormat("########.###");
			SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
			ContentDAO dao = new ContentDAO(this.conn);			
			StringBuffer jsonDesc = new StringBuffer("");
			String sql = "select * from p04 where p0400 in (select to_p0400 from per_designate_task where p0400='" + p0400 + "') ";
			rowSet = dao.search(sql);	
			while(rowSet.next())
		    {	
				String to_p0400 = rowSet.getString("p0400");									
				String to_p0407 = rowSet.getString("p0407")!=null?rowSet.getString("p0407"):"";
				String to_a0101 = rowSet.getString("a0101")!=null?rowSet.getString("a0101"):"";												
				jsonDesc.append("p0407:'" + to_p0407 + "', ");
				jsonDesc.append("user:'" + to_a0101 + "', ");						
				if(targetTraceItem!=null && targetTraceItem.trim().length()>0)
				{
					String[] items = targetTraceItem.split(",");
					for (int i = 0; i < items.length; i++)
					{
						if("attachment".equalsIgnoreCase(items[i].toLowerCase()))
							continue;
						fielditem = DataDictionary.getFieldItem(items[i].toLowerCase());	
						String itemid = "";
						if("score_org".equalsIgnoreCase(items[i].toLowerCase()))
						{
							itemid = rowSet.getString(items[i])!=null?rowSet.getString(items[i]):"";
							if(itemid!=null && itemid.trim().length()>0)
							{
								if(AdminCode.getCodeName("UN", itemid)!=null && AdminCode.getCodeName("UN", itemid).length()>0)								
									itemid = AdminCode.getCodeName("UN", itemid);								
								else								
									itemid = AdminCode.getCodeName("UM", itemid);																	
							}
						}
						else if("D".equalsIgnoreCase(fielditem.getItemtype()))
						{
							if(rowSet.getDate(items[i])!=null)
								itemid = fm.format(rowSet.getDate(items[i]));							
						}
						else if("M".equalsIgnoreCase(fielditem.getItemtype()))
							itemid = rowSet.getString(items[i])!=null?rowSet.getString(items[i]):"";
						else
						{
							if(fielditem.getCodesetid()==null || "0".equals(fielditem.getCodesetid()) || fielditem.getCodesetid().trim().length()==0)
							{
								if("N".equalsIgnoreCase(fielditem.getItemtype()) && fielditem.getDecimalwidth()>0)
									itemid = rowSet.getString(items[i])==null?"":myformat.format(rowSet.getDouble(items[i]));
								else
								{
									itemid = rowSet.getString(items[i])!=null?rowSet.getString(items[i]):"";
									if("p0419".equalsIgnoreCase(items[i]) && itemid!=null && itemid.trim().length()>0)
										itemid = itemid + "%";	
								}
							}
							else
							{
								if(rowSet.getString(items[i])!=null)																
									itemid = AdminCode.getCodeName(fielditem.getCodesetid(), rowSet.getString(items[i]));																	
							}
						}
/*						
						if(fielditem.getItemtype().equalsIgnoreCase("N") && fielditem.getDecimalwidth()!=0)
							itemid = rowSet.getString(items[i])==null?"":myformat.format(rowSet.getDouble(items[i]));						
						else
						{
							itemid = rowSet.getString(items[i])!=null?rowSet.getString(items[i]):"";
							if(items[i].equalsIgnoreCase("p0419") && itemid!=null && itemid.trim().length()>0)
								itemid = itemid + "%";
						}
*/						
						jsonDesc.append("" + items[i].toLowerCase() + ":'" + itemid + "', ");																
					}
				}						
				jsonDesc.append("uiProvider:'col', ");
				if(getDataJson(to_p0400)) // 判断是否又把此任务向下分解了
				{							
					jsonDesc.append("cls:'master-task', ");
					jsonDesc.append("iconCls:'task-folder', ");
					jsonDesc.append("children:[ { ");
							
					// 递归查找分解的任务
					String json = getDesignateTask(to_p0400,targetTraceItem);
					jsonDesc.append(json);
				}
				else
				{							
					jsonDesc.append("leaf:true, ");
					jsonDesc.append("iconCls:'task' ");							
				}
				jsonDesc.append("},{");
		    }
			if(jsonDesc.toString().trim().length()>0)
			{
				datajson.append(jsonDesc.toString().substring(0,jsonDesc.toString().length()-2));	
				datajson.append("] ");	
			}			
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return datajson.toString();
	}		
	
	// 判断是否又把此任务向下分解了
	public boolean getDataJson(String p0400)
	{
		boolean json = false;
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);															
		//	String sql = "select to_a0100 from per_designate_task where p0400='" + p0400 + "' order by group_id ";
			String sql = "select count(p0400) num from p04 where p0400 in (select to_p0400 from per_designate_task where p0400='" + p0400 + "') ";
			rowSet = dao.search(sql);
			int num = 0;
			while(rowSet.next())
		    {
				num = rowSet.getInt("num");
		    	if(num>0)
		    		json = true;
		    }		
			if(rowSet!=null)
				rowSet.close();			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return json;
	}
	
}