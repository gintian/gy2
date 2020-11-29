package com.hjsj.hrms.businessobject.performance.batchGrade;

import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;



public class BatchGradeSinglePointBo 
{
	 private Connection conn = null;
	 private int columnWidth = 360;
	 private String planid="";
	 private UserView userView=null;
	 private Hashtable planParam=new Hashtable();
	 private RecordVo  per_templateVo=null;
	 private RecordVo  per_planVo=null;
	 
	 private String    gradeHtml="";
	 private String    _point_id="";
	 private ArrayList object_List=new ArrayList();
	 private HashMap   object_priv_map=new HashMap();   //考核主体对各对象指标的权限
	 private HashMap   perObjectResultMap=new HashMap();  //考核分数结果值
	 private ArrayList pointList=new ArrayList();
	 private HashMap pointParentItemMap=new HashMap();
	 private HashMap   pointGradeDescMap=new HashMap();            //指标标度列表
	 private ArrayList   standPointGradeDescList=new ArrayList();  //标准标度列表
	 private HashMap pointMaxValueMap=new HashMap();
	 private String  tableWidth="0";
	 private String  isAllSub="1";   //所有对象是否全部提交
	 
	 private String batchGradeOthField="";
	 
	 public BatchGradeSinglePointBo(Connection conn,String planid,UserView userView)
	 {
		 this.conn=conn;
		 this.planid=planid;
		 this.userView=userView;
		 LoadXml loadxml=null;
		 if(BatchGradeBo.planLoadXmlMap.get(this.planid)==null)
		 {
				loadxml = new LoadXml(this.conn, this.planid);
				BatchGradeBo.planLoadXmlMap.put(this.planid,loadxml);
		 }
		 else
				loadxml=(LoadXml)BatchGradeBo.planLoadXmlMap.get(this.planid);
		 planParam = loadxml.getDegreeWhole();
		 this.per_planVo=getRecordVo(planid,"per_plan","plan_id","N");
		 this.per_templateVo=getRecordVo(per_planVo.getString("template_id"),"per_template","template_id","A");
		  
		 if(SystemConfig.getPropertyValue("batchGradeOthField")!=null&&SystemConfig.getPropertyValue("batchGradeOthField").trim().length()>0)
		 {
			   batchGradeOthField=SystemConfig.getPropertyValue("batchGradeOthField").trim();		
			   FieldItem itemfield=DataDictionary.getFieldItem(batchGradeOthField);
			   if(!(itemfield!=null&& "A01".equalsIgnoreCase(itemfield.getFieldsetid())&&per_planVo.getInt("object_type")==2))
				   batchGradeOthField="";
		 }
	 }
	 
	 public BatchGradeSinglePointBo(Connection conn )
	 {
		 this.conn=conn;
	 }
	 
	 
	  public RecordVo getRecordVo(String  id,String tableName,String keyName,String type)
	  {
			RecordVo vo=new RecordVo(tableName);
			try
			{
				if("A".equalsIgnoreCase(type))
					vo.setString(keyName,id);
				else
					vo.setInt(keyName,Integer.parseInt(id));
				ContentDAO dao = new ContentDAO(this.conn);
				vo=dao.findByPrimaryKey(vo);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return vo;
	 }
	 
	  
	  
	  /**
	   * 保存 提交绩效分值
	   * @param opt
	   * @param obj_values
	   */
	 public void saveScore(String opt,String obj_values,String point_id)  throws GeneralException
	 {
		 RowSet rowSet = null;
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 String MustFillCause=(String)this.planParam.get("MustFillCause");                  //评分说明是否必填
			 DBMetaModel dbmodel = new DBMetaModel(this.conn);
			 if (!dbmodel.isHaveTheTable("per_table_" + planid))
				dbmodel.reloadTableModel("per_table_" + planid);
			 DbWizard dbWizard = new DbWizard(this.conn);
			 /*
			 RowSet rowSet = dao.search("select * from per_table_" + planid + " where 1=2");
			 ResultSetMetaData data = rowSet.getMetaData();
			 boolean isReasons = false;
			 for (int i = 0; i < data.getColumnCount(); i++)
			 {
				String name = data.getColumnName(i + 1).toLowerCase();
				if (name.equalsIgnoreCase("Reasons"))
				    isReasons = true;
			  }*/
			  if (!dbWizard.isExistField("per_table_" + planid,"reasons",false))
			  {
				Table table = new Table("per_table_" + planid);
				Field obj = new Field("Reasons", "Reasons");
				obj.setDatatype(DataType.CLOB);
				obj.setKeyable(false);
				obj.setVisible(false);
				obj.setAlign("left");
				table.addField(obj);
				
				dbWizard.addColumns(table);
			  }
			  String[] objvalues=obj_values.split("/");
			 
			  HashMap pointReasons =getPointReasonsMap(objvalues, this.userView.getA0100(),this.planid);
			  StringBuffer obj_str=new StringBuffer("");
			  
			  if(PerformanceConstantBo.pointGradeDesc_templateMap==null||PerformanceConstantBo.pointGradeDesc_templateMap.get(per_planVo.getString("template_id"))==null)
			  {
				  if(PerformanceConstantBo.pointGradeDesc_templateMap==null)
					  PerformanceConstantBo.pointGradeDesc_templateMap=new HashMap();
				  this.pointGradeDescMap=getPointGradeDescMap(per_planVo.getString("template_id"));
				  PerformanceConstantBo.pointGradeDesc_templateMap.put(per_planVo.getString("template_id"), this.pointGradeDescMap);
			  }
			  else
			  {
				  this.pointGradeDescMap=(HashMap)PerformanceConstantBo.pointGradeDesc_templateMap.get(per_planVo.getString("template_id"));
			  }
			 
			  if(PerformanceConstantBo.standPointGradeDescList==null)
			  {
				  if(String.valueOf(this.per_planVo.getInt("busitype"))!=null && String.valueOf(this.per_planVo.getInt("busitype")).trim().length()>0 && this.per_planVo.getInt("busitype")==1)
					  this.standPointGradeDescList=getCompetGradeDescList();
				  else
					  this.standPointGradeDescList=getStandPointGradeDescList();
				  PerformanceConstantBo.standPointGradeDescList= this.standPointGradeDescList;
			  }
			  else
				  this.standPointGradeDescList=PerformanceConstantBo.standPointGradeDescList;
			  
			  if(PerformanceConstantBo.pointList_templateMap==null||PerformanceConstantBo.pointList_templateMap.get(per_planVo.getString("template_id"))==null)
			  {
				  if(PerformanceConstantBo.pointList_templateMap==null)
					  PerformanceConstantBo.pointList_templateMap=new HashMap();
				  this.pointList=getPointList(per_planVo.getString("template_id"));
				  PerformanceConstantBo.pointList_templateMap.put(per_planVo.getString("template_id"), this.pointList);
			  }
			  else
			  {
				  this.pointList=(ArrayList)PerformanceConstantBo.pointList_templateMap.get(per_planVo.getString("template_id"));
			  }
			  LazyDynaBean abean=null;
			  LazyDynaBean pointBean=null;
			  for(int i=0;i<this.pointList.size();i++)
			  {
				  abean=(LazyDynaBean)this.pointList.get(i);
				  String _point_id=(String)abean.get("point_id");
				  if(_point_id.equalsIgnoreCase(point_id))
					  pointBean=abean;
			  }
			   
			 ArrayList pointGradeList=(ArrayList)this.pointGradeDescMap.get(point_id.toLowerCase());	
			 if(pointGradeList==null||pointGradeList.size()==0)
			 {
				 throw GeneralExceptionHandler.Handle(new GeneralException("指标没有定义标度!"));
			 } 
			 ArrayList recordList=new ArrayList();
			 
			 LoadXml loadxml = new LoadXml(this.conn,this.planid); 
			 ArrayList gradeScopeList = loadxml.getPerGradeScopeList("ScoreScope");	 // 得到主体评分范围所有设置				 
			 for(int i=0;i<objvalues.length;i++)
			 {
				 if(objvalues[i]!=null&&objvalues[i].trim().length()>0)
				 {
					 String[] temps=objvalues[i].split("~");
					 obj_str.append(",'"+temps[0]+"'");
					  
					 
					 ArrayList templist = new ArrayList();
					 IDGenerator idg = new IDGenerator(2, this.conn);
					 String id = idg.getId("per_table_xxx.id");
					 templist.add(new Integer(id));
					 templist.add(temps[0]);
					 templist.add(this.userView.getA0100());
					 templist.add(point_id);
					 String gradevalue="0";
					 for(int j=0;j<pointGradeList.size();j++)
					 {
						 abean=(LazyDynaBean)pointGradeList.get(j);
						 gradevalue=(String)abean.get("gradevalue");
						 String gradecode=(String)abean.get("gradecode");
						 if(temps[2].equalsIgnoreCase(gradecode))
						 {
							 break;
						 }
					 }
					 Double score = new Double(PubFunc.round(String.valueOf(Float.parseFloat(gradevalue) * Float.parseFloat((String)pointBean.get("score"))), 1));
					 					 
					 // 判断主体评分是否超出限制范围  JinChunhai 2011.11.16
					 if("2".equals(opt) && gradeScopeList!=null && gradeScopeList.size()>0)
					 {
							String str = "select body_id,a0101 from per_object where plan_id=" + this.planid + " and object_id = '" + temps[0] + "'";			        
							rowSet = dao.search(str);
					        String body_id = "";
					        String objectName = "";
						    if(rowSet.next())	
						    {
						    	body_id = rowSet.getString("body_id");
						    	objectName = rowSet.getString("a0101");
						    }
						    
						    if(body_id!=null && body_id.trim().length()>0)
						    {
								for (int k = 0; k < gradeScopeList.size(); k++)
								{
								    LazyDynaBean bean = (LazyDynaBean) gradeScopeList.get(k);
								    String bodyId = (String) bean.get("BodyId");
								    String upScope = (String) bean.get("UpScope");
								    String downScope = (String) bean.get("DownScope");						    
								    if(body_id.equalsIgnoreCase(bodyId))
								    {
								    	if((downScope!=null && downScope.trim().length()>0) && (Double.parseDouble(score.toString())<Double.parseDouble(downScope)))
								    	{
								    		dao.update("update per_mainbody set status=1 where plan_id="+this.planid+" and object_id='"+temps[0]+"' and mainbody_id='"+this.userView.getA0100()+"' ");
								    		throw (new GeneralException(ResourceFactory.getProperty("您对"+ objectName +"的评分低于评分下限值"+ downScope +"！")));
								    		//info="您对"+ objectName +"的评分低于评分下限值"+ downScope +"！";
								    	}
								    	else if((upScope!=null && upScope.trim().length()>0) && (Double.parseDouble(score.toString())>Double.parseDouble(upScope)))
								    	{
								    		dao.update("update per_mainbody set status=1 where plan_id="+this.planid+" and object_id='"+temps[0]+"' and mainbody_id='"+this.userView.getA0100()+"' ");
								    		throw (new GeneralException(ResourceFactory.getProperty("您对"+ objectName +"的评分高于评分上限值"+ upScope +"！")));	
								    		//info="您对"+ objectName +"的评分高于评分上限值"+ upScope +"！";
								    	}
								    	break;
								    }
								}
						    }
					}
					 
					 templist.add(score);
					 templist.add(new Double(0));
					 templist.add(temps[2]);
					 String reasons="";
					 if(pointReasons.get(temps[0]+"/"+point_id.toLowerCase())!=null)
						 reasons=(String)pointReasons.get(temps[0]+"/"+point_id.toLowerCase());
					 templist.add(reasons);
					 if("true".equalsIgnoreCase(MustFillCause)&&reasons.trim().length()==0)
						 throw GeneralExceptionHandler.Handle(new GeneralException("评分说明为必填!"));
					 recordList.add(templist);
				 }
			 }
			// if(obj_str.length()>0)
			 {
				 this.object_List=getObjectList(this.planid,this.userView.getA0100());
				 StringBuffer str=new StringBuffer("");
				 for(int i=0;i<this.object_List.size();i++)
				 {
					 LazyDynaBean _abean=(LazyDynaBean)this.object_List.get(i);
					 str.append(",'"+(String)_abean.get("object_id")+"'");
				 }
				 if(str.length()>0)
				 {
					 String sql = "delete from PER_TABLE_" + planid + " where object_id in ("+str.substring(1)+") and mainbody_id='"+this.userView.getA0100()+"' and lower(point_id)='"+point_id.toLowerCase()+"'";
					 dao.update(sql);
					 String sql0="";
					 if(recordList.size()>0)
					 {
					    sql0 = "insert into per_table_" + planid + " (id,object_id,mainbody_id,point_id,score,amount,degree_id,reasons)values(?,?,?,?,?,?,?,?)";
						dao.batchInsert(sql0, recordList);
					 }
					 sql0="update per_mainbody set status="+opt+" where plan_id="+this.planid+" and mainbody_id='"+this.userView.getA0100()+"' and status<>'4' and  object_id in ("+str.substring(1)+") ";
					 if("1".equals(opt))
						 sql0+=" and status<>'2'  ";
					 dao.update(sql0);
					 if("2".equals(opt))
					 {
						 sql0="update per_mainbody set status=7 where plan_id="+this.planid+" and mainbody_id='"+this.userView.getA0100()+"'  and  object_id in ("+str.substring(1)+") ";
						 sql0+=" and status='4'  ";
						 dao.update(sql0);
					 }
				 }
				 
				 // 提交时把待办置为已办  2011.06.28 JinChunhai
				 if("2".equals(opt))
				 {
					 PendingTask pt = new PendingTask();
					 String pendingCode = getPendingCode(this.planid,this.userView.getA0100());			
					 if(pendingCode!=null && pendingCode.trim().length()>0)
					 {				
						 pt.updatePending("P", pendingCode, 1, "评分已提交！",this.userView);
					 }
				 }								 
			 }
			 if(rowSet!=null)
					rowSet.close();
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
		 
	 }
   /**
	* 取得需置为已办的id
	* @param mainBodyId
	* @param nbase 
	* @return
	*/
	public String getPendingCode(String plan_id,String mainBodyId)
	{
		String id = "";
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
							
			String sql="select task_id from per_task_pt where plan_id="+ plan_id +" and mainbody_id='"+ mainBodyId +"' and flag=2";						
			rowSet = dao.search(sql);
			if(rowSet.next())
				id=rowSet.getString("task_id");
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return id;
	}
	 
	public HashMap getPointReasonsMap(String[] object_ids, String mainbody_id, String plan_id)
	{
		HashMap map = new HashMap();
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    StringBuffer objectIDs = new StringBuffer("");
		    for (int i = 0; i < object_ids.length; i++)
		    {
		    	if(object_ids[i]!=null&&object_ids[i].trim().length()>0)
				 {
					 String[] temps=object_ids[i].split("~");
					 objectIDs.append(",'" + temps[0] + "'");
				 }
		    }
		    if(objectIDs==null || objectIDs.toString().trim().length()<=0)
		    	return map;
			RowSet rowSet = dao.search("select * from per_table_" + plan_id + " where object_id in (" + objectIDs.substring(1) + ") and mainbody_id='" + mainbody_id + "' order by object_id");
		    while (rowSet.next())
		    {
				String scoreCause = Sql_switcher.readMemo(rowSet, "reasons");
				String point_id = rowSet.getString("point_id");
				String object_id = rowSet.getString("object_id");
				map.put(object_id+"/"+ point_id.toLowerCase(), scoreCause);
		    }

		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return map;
	    }
	 
	 
	  
	  
	  /**
	   * 取得打分界面html
	   * @return
	   */
	  public String getGradeHtml(int pointIndex)
	  {
		  try
		  {
			  if(SystemConfig.getPropertyValue("batchGradeSinglePointColumnWidth")!=null&&SystemConfig.getPropertyValue("batchGradeSinglePointColumnWidth").length()>0)
			 		columnWidth=Integer.parseInt(SystemConfig.getPropertyValue("batchGradeSinglePointColumnWidth").trim());
			  String showDeductionCause=(String)this.planParam.get("showDeductionCause");  //显示扣分原因(Ture, False(默认))
			  if(PerformanceConstantBo.pointGradeDesc_templateMap==null||PerformanceConstantBo.pointGradeDesc_templateMap.get(per_planVo.getString("template_id"))==null)
			  {
				  if(PerformanceConstantBo.pointGradeDesc_templateMap==null)
					  PerformanceConstantBo.pointGradeDesc_templateMap=new HashMap();
				  this.pointGradeDescMap=getPointGradeDescMap(per_planVo.getString("template_id"));
				  PerformanceConstantBo.pointGradeDesc_templateMap.put(per_planVo.getString("template_id"), this.pointGradeDescMap);
			  }
			  else
			  {
					  this.pointGradeDescMap=(HashMap)PerformanceConstantBo.pointGradeDesc_templateMap.get(per_planVo.getString("template_id"));
			  }
			  
			  if(PerformanceConstantBo.standPointGradeDescList==null)
			  {
				  if(String.valueOf(this.per_planVo.getInt("busitype"))!=null && String.valueOf(this.per_planVo.getInt("busitype")).trim().length()>0 && this.per_planVo.getInt("busitype")==1)
					  this.standPointGradeDescList=getCompetGradeDescList();
				  else
					  this.standPointGradeDescList=getStandPointGradeDescList();
				  PerformanceConstantBo.standPointGradeDescList= this.standPointGradeDescList;
			  }
			  else
				  this.standPointGradeDescList=PerformanceConstantBo.standPointGradeDescList;
			  
			  if(PerformanceConstantBo.pointList_templateMap==null||PerformanceConstantBo.pointList_templateMap.get(per_planVo.getString("template_id"))==null)
			  {
				  if(PerformanceConstantBo.pointList_templateMap==null)
					  PerformanceConstantBo.pointList_templateMap=new HashMap();
				  this.pointList=getPointList(per_planVo.getString("template_id"));
				  PerformanceConstantBo.pointList_templateMap.put(per_planVo.getString("template_id"), this.pointList);
			  }
			  else
			  {
				  this.pointList=(ArrayList)PerformanceConstantBo.pointList_templateMap.get(per_planVo.getString("template_id"));
			  }
			  
			  if(this.pointParentItemMap.size()==0)
				  this.pointParentItemMap=getPointParentItemMap(this.pointList,per_planVo.getString("template_id"));
			//  if(this.object_List==null||this.object_List.size()==0)
			  this.object_List=getObjectList(this.planid,this.userView.getA0100());
			  if(this.object_priv_map==null||this.object_priv_map.size()==0)
				  this.object_priv_map=getPointprivMap(this.planid,this.userView.getA0100(),this.pointList);
			  /* 得到某计划考核主体给对象的评分结果hashMap */
			  this.perObjectResultMap= getPerTableXXX(this.planid, this.userView.getA0100(), this.object_List);
			 
			  String BlankScoreOption = (String)this.planParam.get("BlankScoreOption");
			  if ("1".equals(BlankScoreOption))
					this.pointMaxValueMap = getMaxPointValue(per_planVo.getString("template_id")); // 指标未打分时，0
			                                                                        // 按未打分处理，1
			                                                                        // 计为最高分，默认值为按未打分处理
			  
			  this.gradeHtml=produceHtml(pointIndex);
			
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  return gradeHtml;
	  }
	  
	  /**
	   * 取得打分对象字符串
	   * @param objectList
	   * @return
	   */
	  public String getobjects_str(ArrayList objectList)
	  {
		  StringBuffer str=new StringBuffer("");
		  LazyDynaBean abean=null;
		  HashMap priv_map=null;
		  for(int i=0;i<objectList.size();i++)
		  {
			  abean=(LazyDynaBean)objectList.get(i);
			  String object_id=(String)abean.get("object_id");
			  priv_map = (HashMap) object_priv_map.get(object_id); // 得到具有某考核对象的指标权限map 
			  if(priv_map!=null&& "0".equals((String) priv_map.get(this._point_id))) //没有指标权限
				  continue;
			  str.append("/"+object_id+"~"+(String)abean.get("a0101"));
		  }
		  return str.toString();
	  }
	  
	  
	  /**
	   * 
	   * @param pointIndex
	   * @return
	   */
	  public String produceHtml(int pointIndex)
	  {
		  StringBuffer html=new StringBuffer("");
		  LazyDynaBean pointBean=(LazyDynaBean)this.pointList.get(pointIndex);
		  ArrayList parentList=(ArrayList)this.pointParentItemMap.get(((String)pointBean.get("point_id")).toLowerCase());
		  ArrayList pointGradeList=(ArrayList)this.pointGradeDescMap.get(((String)pointBean.get("point_id")).toLowerCase());
		  
		  
		  
		  int tableTotalWidth=this.columnWidth+this.columnWidth/3;
		  if(batchGradeOthField.length()>0)
			  tableTotalWidth+=100;
		  this.tableWidth=String.valueOf(tableTotalWidth+30);
		  html.append("<table  style='margin-top:-3' border='0' cellspacing='0'  align='center' cellpadding='0' class='' width='"+tableTotalWidth+"' >");
		  
		  html.append(getTableHeadHtml(parentList,pointGradeList,pointBean));
		  html.append(getTableBodyHtml(pointGradeList,pointBean));
		  
		  html.append("</table>");
		  return html.toString();
	  }
	  
	  
	  /**
	   * 取得表体html
	   * @param pointGradeList
	   * @param pointBean
	   * @return
	   */
	  public String getTableBodyHtml(ArrayList pointGradeList,LazyDynaBean pointBean)
	  {
		  StringBuffer html=new StringBuffer("");
		  String point_id=(String)pointBean.get("point_id");
		  this._point_id=point_id;
		  LazyDynaBean objectBean=null;
		  LazyDynaBean abean=null;
		  HashMap priv_map=null;
		  String BlankScoreOption = (String)this.planParam.get("BlankScoreOption");
		  String showDeductionCause=(String)this.planParam.get("showDeductionCause");  //显示扣分原因(Ture, False(默认))
		  String BlankScoreUseDegree = (String)this.planParam.get("BlankScoreUseDegree"); // 指标未打分，按用户定义的标度, 
		  String showNoMarking = (String)this.planParam.get("ShowNoMarking");  // 显示不打分
		  String performanceType = (String)this.planParam.get("performanceType"); // 考核形式  1：民主评测
			 
		  for(int i=0;i<object_List.size();i++)
		  {
			  objectBean=(LazyDynaBean)this.object_List.get(i);
			  String status=(String)objectBean.get("status");
			  String object_id=(String)objectBean.get("object_id");
			  String a0101=(String)objectBean.get("a0101");
			  String fillctrl=(String)objectBean.get("fillctrl");			  
			  
			  priv_map = (HashMap) object_priv_map.get(object_id); // 得到具有某考核对象的指标权限map
			  html.append("\r\n<tr    >");
			  
			  String otherInfo="";
			  if(objectBean.get("batchGradeOthInfo")!=null)
				  otherInfo=(String)objectBean.get("batchGradeOthInfo")+":";
			  
			  int bottomBorderWidth=0;
			  if(i==object_List.size()-1)
				  bottomBorderWidth = 1;
			  
			  html.append("<td align='center'  class='RecordRow' style=\"border-bottom-width:"+bottomBorderWidth+"px;\" nowrap >"+(i+1)+"</td>");
			  html.append("<td align='center'  class='RecordRow' style=\"border-width:1 1 "+bottomBorderWidth+" 0px;\" nowrap ><font class='fontStyle_self'  >"+otherInfo+((String)objectBean.get("a0101"))+"</font></td>");
			  
			  if (showNoMarking != null && "true".equalsIgnoreCase(showNoMarking))
			  {
				  html.append("<td class='RecordRow_right' style=\"border-bottom-width:"+bottomBorderWidth+"px;\"  align='center' ");
				  if ("2".equals(status) || "7".equals(status))
					  html.append(" disabled ");
				  html.append(" > ");
				  String checked="";
				  if ("4".equals(status) || "7".equals(status))
					  checked="checked";
				  html.append("<table><tr><td>");
				  html.append("<input type='checkbox'  "+checked+"   onclick='setStatus(this)'   name='noscore_"+object_id+"' " );
				  if(fillctrl!=null && fillctrl.trim().length()>0 && "1".equals(fillctrl)) // 必打分项
				  {
					  html.append(" style='display:none' ");
				  }
				  html.append(" > </td>");
				  
				  String display_desc = "none";
				  if ("4".equals(status) || "7".equals(status))
				  {
					  display_desc = "block";
				  }
				  if(fillctrl!=null && fillctrl.trim().length()>0 && "1".equals(fillctrl)) // 必打分项
					  display_desc = "none";
				  html.append("<td valign='bottom' >");
					  html.append("<div style='display:" + display_desc + "' id='b" + object_id + "' >");
					  html.append("<a  href=\"javascript:windowOpen('/performance/markStatus/markStatusList.do?b_edit2=edit&operater=3&type=0&status=" + status + "&planID=" + this.planid
							    + "&objectID=" + object_id + "&mainbodyID=" + this.userView.getA0100());
					  html.append("')\" >");					   
					  html.append(ResourceFactory.getProperty("performance.batchgrade.donotSubed"));
					  html.append("</a>");
					  html.append("</div>");
				  html.append("</td></tr></table>"); 
				  html.append("</td> ");
			  }
			  
			  HashMap objResultMap=null;
			  if(this.perObjectResultMap.get(object_id)!=null)
				  objResultMap=(HashMap)this.perObjectResultMap.get(object_id);
			  
			  
			 
			 
			 String defaultValue="";
			 if ("1".equals(BlankScoreOption))
			 {	
			  		LazyDynaBean _abean = (LazyDynaBean) this.pointMaxValueMap.get(point_id.toLowerCase());
				  	defaultValue = (String) _abean.get("gradecode");
			 }
			 if ("2".equals(BlankScoreOption))
			  		defaultValue=BlankScoreUseDegree;
			  
			  for(int j=0;j<pointGradeList.size();j++)
			  {
				  
				  
				  abean=(LazyDynaBean)pointGradeList.get(j);
				  html.append("\r\n<td   class='RecordRow_right' style=\"border-bottom-width:"+bottomBorderWidth+"px;\" nowrap align='center' >");	  
				  if(priv_map!=null&& "0".equals((String) priv_map.get(point_id))) //没有指标权限
				  {
					  html.append("<font color=\"#FF0000\">—</font>");
				  }
				  else
				  {
					  String checked_str="";
				      if ("1".equals(status) || "2".equals(status) || "3".equals(status)|| "8".equals(status))
					  {
				    	    LazyDynaBean values = (LazyDynaBean) objResultMap.get(point_id.toLowerCase());
							if (values != null)
							{
							    if (((String)values.get("degree_id")).equalsIgnoreCase((String)abean.get("gradecode")))
							    {
										checked_str="checked";
							    }
							}
							else if("1".equals(status))
							{
								if ("1".equals(BlankScoreOption) || "2".equals(BlankScoreOption))
								{
										if (defaultValue != null && defaultValue.length() > 0)
										{
										    if (defaultValue.equalsIgnoreCase((String)abean.get("gradecode")))
										    {
													checked_str="checked";
										    }
										}
								}
								
							}
					  } 
					  else if ("1".equals(BlankScoreOption) || "2".equals(BlankScoreOption))
					  {
							if (defaultValue != null && defaultValue.length() > 0)
							{
							    if (defaultValue.equalsIgnoreCase((String)abean.get("gradecode")))
							    {
										checked_str="checked";
							    }
							}
					  }
				      String disabled="";
				      if ("2".equals(status) || "4".equals(status) || "7".equals(status))
				    		disabled=" disabled ";
				    	
				      html.append("<input type='radio'  "+disabled+"    "+checked_str+" name='" +object_id + "~" +point_id+ "' value='"+(String)abean.get("gradecode")+"' /> ");
				    
				  }
				 
				  
				  html.append("</td>");
			  }
			  
			  if("true".equalsIgnoreCase(showDeductionCause))
			  {
				  html.append("<td  class='RecordRow_right' style=\"border-bottom-width:"+bottomBorderWidth+"px;\" nowrap  ");
				  String reasons="";
				  String  valign="top";
				  LazyDynaBean values =null;
				  if(objResultMap!=null)
					  values=(LazyDynaBean) objResultMap.get(point_id.toLowerCase());
				  if (values != null)
				  {
					  reasons=(String)values.get("reasons");
					  if(reasons.trim().length()==0)
							 valign="middle";
				  }
				  String opt="0";
				  if ("2".equals(status) || "4".equals(status) || "7".equals(status))
					  opt="1";
				  html.append("  valign='"+valign+"' align='left'  > ");
				  html.append("<table width='100%'  ><tr><td id='r_"+point_id+"_"+object_id+"' >"+reasons+"</td></tr><tr><td align='center'  >");
				  html.append("<img title='填写评分说明' onclick='scoreReason(\""+this.planid+"\",\""+object_id+"\",\""+this.userView.getA0100() +"\",\""+point_id+"\",\""+opt+"\")'  src='/images/readwrite_obj.gif' border=0></td></tr></table>");
				  html.append("</td>");
			  }
			  
		/*	  
			  if (showNoMarking != null && showNoMarking.equalsIgnoreCase("true"))
			  {
				  html.append("<td class='RecordRow' align='center' ");
		
				  html.append(" <table ><tr><td width='5' nowrap  valign='bottom' >    <input type='checkbox' tile='sdfasdf' onclick='setStatus(this)'   name='b" + object_id + "_" + this.planid + "_"
					    + this.userView.getA0100() + "' ");
				  String display_desc = "none";
				  if (status.equals("4") || status.equals("7"))
				  {
					  html.append(" checked ");
					  display_desc = "block";
				  }
				  if (fillctrl.equals("1")) // 必打分项
				  {
					  html.append(" style='display:none' ");
				  }
				  if (status.equals("2") || status.equals("7"))
					  html.append(" disabled ");
				  html.append(" >  </td><td width='40'  valign='bottom' nowrap >  ");
				  html.append("<div style='display:" + display_desc + "' id='b" + object_id + "' >");
				  html.append("<a  href=\"javascript:windowOpen('/performance/markStatus/markStatusList.do?b_edit2=edit&operater=3&type=0&status=" + status + "&planID=" + this.planid
					    + "&objectID=" + object_id + "&mainbodyID=" + this.userView.getA0100());
				  html.append("')\" >");
				  if (performanceType.equals("0"))
					  html.append(ResourceFactory.getProperty("performance.batchgrade.donotSubed"));
				  else if (performanceType.equals("1"))
					  html.append(ResourceFactory.getProperty("performance.batchgrade.forfeit"));
				  html.append("</a>");
				  html.append("</div></td></tr></table>");
		
				  html.append(" </td>");
				    
			  }			  
		*/  
			  html.append("</tr>");
		  }
		  return html.toString();
	  }
	  
	  /**
	   * 取得表头html
	   * @param parentList
	   * @param pointGradeList
	   * @param pointBean
	   * @return
	   */
	  public String getTableHeadHtml(ArrayList parentList,ArrayList pointGradeList,LazyDynaBean pointBean)
	  {
		  StringBuffer html=new StringBuffer("");
		  String showDeductionCause=(String)this.planParam.get("showDeductionCause");  //显示扣分原因(Ture, False(默认))
		  String showNoMarking = (String)this.planParam.get("ShowNoMarking");  // 显示不打分
		  String performanceType = (String)this.planParam.get("performanceType"); // 考核形式  1：民主评测
		  
		  double awidth=this.columnWidth/3;
		  if(batchGradeOthField.length()>0)
			  awidth+=100;
			  
		  html.append("\r\n<tr> <td   class='TableRow_lrt header_locked common_background_color common_border_color'  colspan='2'  valign='middle' align='center'  width='"+awidth+"' rowspan='"+(parentList.size()+2)+"' ><font class='fontStyle_self'  >");
		  if (this.per_planVo.getInt("object_type")==2) 	//人员	
			  html.append(ResourceFactory.getProperty("performance.batchgrade.title.name"));
		  else if (this.per_planVo.getInt("object_type")==1)//团队
			  html.append(ResourceFactory.getProperty("org.performance.unorum"));
		  else if (this.per_planVo.getInt("object_type")==3)//单位
			  html.append(ResourceFactory.getProperty("tree.unroot.undesc"));
		  else if (this.per_planVo.getInt("object_type")==4)//部门
			  html.append(ResourceFactory.getProperty("column.sys.dept"));
		  html.append("</font></td>");
		  
		  if (showNoMarking != null && "true".equalsIgnoreCase(showNoMarking))
		  {			  
			  html.append("<td class='TableRow_rt header_r_locked common_background_color common_border_color' id='b'  valign='middle' align='center' ");
			  html.append(" width='"+awidth+"' ");
			  html.append(" rowspan='"+(parentList.size()+2)+"' > ");
			  if ("0".equals(performanceType))
				  html.append("<font class='fontStyle_self' >"+ResourceFactory.getProperty("lable.performnace.noMarkCause")+"</font>");
			  else if ("1".equals(performanceType))
				  html.append("<font class='fontStyle_self' >"+ResourceFactory.getProperty("performance.batchgrade.forfeit")+"</font>");
			    
			  html.append("</td>");			  
		  }
		  
		  String visible=(String)pointBean.get("visible");
		  
		  String classHeadTyle = "TableRow_rt header_l_locked common_background_color common_border_color";
		  for(int i=parentList.size()-1;i>=0;i--)
		  {
			  if(i==0)
				  classHeadTyle = "TableRow_rt header_r_locked common_background_color common_border_color";
			  
			  LazyDynaBean itemBean=(LazyDynaBean)parentList.get(i);
			  if(i!=(parentList.size()-1))
				  html.append("\r\n<tr>");
			  html.append("<td  valign='middle' align='center' class='" + classHeadTyle + "' width='"+this.columnWidth+"'  colspan='"+pointGradeList.size()+"' ><font class='fontStyle_self'  >");
			  html.append((String)itemBean.get("itemdesc"));
			  html.append("</font></td>");
			  if("true".equalsIgnoreCase(showDeductionCause)&&(i==parentList.size()-1))
				  html.append("<td  valign='middle' align='center' class='" + classHeadTyle + "' width='150'  rowspan='"+(parentList.size()+2)+"' ><font class='fontStyle_self'  >评分说明</font></td>");
			  html.append("</tr>");
		  }
		  html.append("\r\n<tr>");
		  html.append("<td  valign='middle' align='center'  class='TableRow_rt header_r_locked common_background_color common_border_color' width='"+this.columnWidth+"' id='" +(String)pointBean.get("point_id")+ "'  ");
		  
		  if ("2".equals(visible))
			  html.append(" onclick='showDateSelectBox2(this);' style='cursor:hand'   onmouseout='Element.hide(\"date_panel\");' ");
		  else if("1".equals(visible))
			  html.append(" onclick='showDateSelectBox(this);' style='cursor:hand'   onmouseout='Element.hide(\"date_panel\");' ");
		  
		  html.append(" colspan='"+pointGradeList.size()+"'  ><font class='fontStyle_self2'  >");
		  html.append((String)pointBean.get("pointname"));
		  html.append("</font></td></tr>");
		  html.append("\r\n<tr>");
		  
		  
		  String DegreeShowType = (String)this.planParam.get("DegreeShowType");// 1-标准标度  // 2-指标标度
		  HashMap temp_map=new HashMap();
		  LazyDynaBean _Bean=null;
		  if("1".equals(DegreeShowType))
		  {
			//  pointGradeList=this.standPointGradeDescList;
			  for(int i=0;i<this.standPointGradeDescList.size();i++)
			  {
				  _Bean=(LazyDynaBean)this.standPointGradeDescList.get(i);
				  temp_map.put(((String)_Bean.get("gradecode")).toLowerCase(), (String)_Bean.get("gradedesc"));
			  }
		  }		  
		  
		  classHeadTyle = "TableRow_rt header_l_locked common_background_color common_border_color";
		  for(int i=0;i<pointGradeList.size();i++)
		  {
			  if(i==pointGradeList.size()-1)
				  classHeadTyle = "TableRow_rt header_r_locked common_background_color common_border_color";
			  
			  LazyDynaBean Bean=(LazyDynaBean)pointGradeList.get(i);
			  html.append("<td  valign='middle' align='center' class='" + classHeadTyle + "' ><font class='fontStyle_self'  >");
			  if("1".equals(DegreeShowType))
			  {
				  html.append((String)temp_map.get(((String)Bean.get("gradecode")).toLowerCase()));
			  }
			  else
				  html.append((String)Bean.get("gradedesc"));
			  html.append("</font></td>");
		  }
		  html.append("</tr>");
		  		  		  		  
		  return html.toString();
	  }
	  
	  
	  
	  
	  /**
       * 取得模版下指标的最高标度
       */
  public HashMap getMaxPointValue(String template_id)
  {

	HashMap map = new HashMap();
	try
	{
	    ContentDAO dao = new ContentDAO(this.conn);
	    String sql = "select ptp.point_id,ptp.score,ptp.rank,pp.pointkind,b.gradecode,b.gradevalue,b.top_value,b.bottom_value from per_template_item pti,per_template_point ptp,per_point pp,"
		    + " (select a.* from per_grade a where a.gradevalue=(select max(b.gradevalue) from per_grade b where a.point_id=b.point_id  ) )b"
		    + " where ptp.item_id=pti.item_id and pp.point_id=ptp.point_id and ptp.point_id=b.point_id  and pti.template_id='" + template_id + "' ";
	    RowSet rowSet = dao.search(sql);
	    LazyDynaBean abean = null;
	    while (rowSet.next())
	    {
		abean = new LazyDynaBean();
		String point_id = rowSet.getString("point_id");
		String score = rowSet.getString("score");
		String rank = rowSet.getString("rank");
		String pointkind = rowSet.getString("pointkind");
		String gradecode = rowSet.getString("gradecode");
		String gradevalue = rowSet.getString("gradevalue");
		String top_value = rowSet.getString("top_value");
		abean.set("score", score);
		abean.set("rank", rank);
		abean.set("pointkind", pointkind);
		abean.set("gradecode", gradecode);
		abean.set("gradevalue", gradevalue);
		abean.set("top_value", top_value);
		map.put(point_id.toLowerCase(), abean);
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return map;
  }
	  
	  
	  
	  
	  
	  
	  /**
	   * 取得绩效标准标度信息列表
	   * @return
	   */
	  public ArrayList getStandPointGradeDescList()
	  {
		 ArrayList gradeList=new ArrayList();
		 ContentDAO dao = new ContentDAO(this.conn);
		 try
		 {
			    LazyDynaBean abean=null;
				RowSet rowSet =dao.search("select * from per_grade_template");
				while(rowSet.next())
				{
					abean=new LazyDynaBean();
					abean.set("grade_template_id",rowSet.getString("grade_template_id"));
					abean.set("gradevalue",rowSet.getString("gradevalue"));
					abean.set("gradedesc",Sql_switcher.readMemo(rowSet,"gradedesc"));
					abean.set("top_value",rowSet.getString("top_value")!=null?rowSet.getString("top_value"):"0");
					abean.set("bottom_value",rowSet.getString("bottom_value")!=null?rowSet.getString("bottom_value"):"0");
					abean.set("gradecode", rowSet.getString("grade_template_id"));
					gradeList.add(abean);
				}
		  
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return gradeList;
	  }
	  /**
	   * 取得能力素质标准标度信息列表
	   * @return
	   */
	  public ArrayList getCompetGradeDescList()
	  {
		 ArrayList gradeList=new ArrayList();
		 ContentDAO dao = new ContentDAO(this.conn);
		 try
		 {
			    LazyDynaBean abean=null;
				RowSet rowSet =dao.search("select * from per_grade_competence");
				while(rowSet.next())
				{
					abean=new LazyDynaBean();
					abean.set("grade_template_id",rowSet.getString("grade_template_id"));
					abean.set("gradevalue",rowSet.getString("gradevalue"));
					abean.set("gradedesc",Sql_switcher.readMemo(rowSet,"gradedesc"));
					abean.set("top_value",rowSet.getString("top_value")!=null?rowSet.getString("top_value"):"0");
					abean.set("bottom_value",rowSet.getString("bottom_value")!=null?rowSet.getString("bottom_value"):"0");
					abean.set("gradecode", rowSet.getString("grade_template_id"));
					gradeList.add(abean);
				}
		  
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return gradeList;
	  }
	  
	  
	  
	  /**
       * 得到某计划考核主体给对象的评分结果hashMap
       * 
       * @param plan_id
       *                考核计划id
       * @param mainbodyID
       *                考核主体id
       * @param object_id
       *                考核对象列表
       * @return HashMap
       */
	  public HashMap getPerTableXXX(String plan_id, String mainbodyID, ArrayList objectList) throws GeneralException
	  {
	
		HashMap hashMap = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		
		try
		{
			RowSet rowSet = null;
		    DecimalFormat myformat1 = new DecimalFormat("##########.#####");//
		    DbWizard dbWizard = new DbWizard(this.conn);
		    String performanceType = (String) this.planParam.get("performanceType"); // 考核形式 0：绩效考核 1：民主评测
		    LazyDynaBean _abean=null;
		    
		    boolean isExistTable=false;
		    if (dbWizard.isExistTable("per_table_" + plan_id))
		    	isExistTable=true;
		    
		    for (Iterator t = objectList.iterator(); t.hasNext();)
		    {
				LazyDynaBean abean=(LazyDynaBean)t.next();
				String know_id=(String)abean.get("know_id");
				String whole_grade_id=(String)abean.get("whole_grade_id");
				String status=(String)abean.get("status");
				if ("1".equals(status) || "2".equals(status) || "3".equals(status)|| "8".equals(status) || ("1".equals(performanceType) && ("4".equals(status) || "7".equals(status))))
				{
				    String objectid = (String)abean.get("object_id");
				    HashMap map = new HashMap();
				    String sql = "";
				    if (isExistTable)
				    {
				    	if(!dbWizard.isExistField("per_table_" + plan_id,"reasons",false))
				    	{
				    		 Table table=new Table("per_table_"+plan_id);
							 Field obj=new Field("Reasons","Reasons");	
							 obj.setDatatype(DataType.CLOB);
							 obj.setKeyable(false);			
							 obj.setVisible(false);
							 obj.setAlign("left");		
							 table.addField(obj);
							 dbWizard.addColumns(table);
				    	}
				    	
						sql = "select * from per_table_" + plan_id + "  where mainbody_id='" + mainbodyID + "' and object_id='" + objectid + "' ";
						rowSet = dao.search(sql);
						 
						while (rowSet.next())
						{
							_abean=new LazyDynaBean();
							_abean.set("score",myformat1.format(rowSet.getDouble("score")));
							_abean.set("amount",PubFunc.round(rowSet.getString("amount")!=null?rowSet.getString("amount"):"0",1));
							_abean.set("point_id", rowSet.getString("point_id"));
							_abean.set("degree_id", rowSet.getString("degree_id")!=null?rowSet.getString("degree_id"):"");
							_abean.set("reasons",Sql_switcher.readMemo(rowSet,"reasons"));
						    map.put(rowSet.getString("point_id").toLowerCase(), _abean);
						}
		
				    }
				    
					map.put("know_id", know_id);
					map.put("whole_grade_id",whole_grade_id);
				  
				    hashMap.put(objectid, map);
				}
		    }
		    if(rowSet!=null)
		    	rowSet.close();
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return hashMap;
	  }
		  
		  
		  
	  
	  
	  
	  
	  
	  /**
       * 得到指标权限信息
       * 
       * @param plan_id
       *                考核计划id
       * @param per_mainbody
       *                考核主体
       * @return
       */
	  public HashMap getPointprivMap(String plan_id, String per_mainbody,ArrayList pointList) throws GeneralException
	  {
	
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
		    String sql = "select * from per_pointpriv_" + plan_id + " where mainbody_id='" + per_mainbody + "'";
		    rowSet = dao.search(sql);
		    int num = 0;
		    while (rowSet.next())
		    {
				num++;
				String object_id = rowSet.getString("object_id");
				HashMap pointMap = new HashMap();
				for (Iterator t = pointList.iterator(); t.hasNext();)
				{
				    LazyDynaBean pointBean=(LazyDynaBean)t.next();
				    String temp=(String)pointBean.get("point_id");
				    pointMap.put(temp, rowSet.getString("C_" + temp));
		
				}
				map.put(object_id, pointMap);
		    }
		    if (num == 0)
		    {
				rowSet = dao.search("select distinct object_id from per_mainbody where plan_id=" + plan_id + " and mainbody_id='" + per_mainbody + "' ");
				LazyDynaBean abean=null;
				while (rowSet.next())
				{
				    String object_id = rowSet.getString("object_id");
				    HashMap pointMap = new HashMap();
				    for (Iterator t = pointList.iterator(); t.hasNext();)
				    {
				    	abean=(LazyDynaBean)t.next();
						pointMap.put(((String)abean.get("point_id")).toUpperCase(), "1");
				    }
				    map.put(object_id, pointMap);
				}
		    }
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return map;
	  }
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  /**
	   * 取得当前计划该考核主体的对象列表
	   * @return
	   */
	  public ArrayList getObjectList(String plan_id,String mainbodyID)
	  {
		  ArrayList objList=new ArrayList();
		  try
		  {
			   ContentDAO dao = new ContentDAO(this.conn);
			   
			    
			   StringBuffer sql = new StringBuffer("select po.object_id,po.a0101,pm.status,pm.fillctrl,pm.know_id,pm.whole_grade_id");
			   if(batchGradeOthField.length()>0)
				   sql.append(",Usra01."+batchGradeOthField);
			   	sql.append(" from per_mainbody pm,per_object po ");
			   	if(batchGradeOthField.length()>0)
					sql.append(",Usra01");	
			   	sql.append(" where pm.object_id=po.object_id ");
			   	if(batchGradeOthField.length()>0)
			   		sql.append(" and po.object_id=Usra01.a0100");
			   	sql.append("  and  pm.plan_id=" + plan_id + " and po.plan_id=" + plan_id + " and pm.mainbody_id='" + mainbodyID + "' ");
			    
			    String a_mitiScoreMergeSelfEval = (String)this.planParam.get("mitiScoreMergeSelfEval");
			    if ("False".equalsIgnoreCase(a_mitiScoreMergeSelfEval))
			    	sql.append(" and pm.object_id<>'" + mainbodyID + "'");

			    if (this.per_planVo.getInt("object_type")==2) // 考核人员
			    	sql.append(" order by po.a0000,po.b0110,po.e0122,po.object_id ");
			    else // 考核部门
			    	sql.append(" order by po.a0000,po.b0110,po.object_id ");
			    RowSet rowSet = dao.search(sql.toString());
			    LazyDynaBean abean=null;
			    FieldItem itemfield=DataDictionary.getFieldItem(batchGradeOthField);
			    DecimalFormat myformat1 = new DecimalFormat("##########.#####");
			    SimpleDateFormat fm=new SimpleDateFormat("yyyy-MM-dd");
			    while (rowSet.next())
			    {
			    	abean=new LazyDynaBean();
			    	abean.set("object_id",rowSet.getString("object_id"));
			    	abean.set("a0101",rowSet.getString("a0101"));
			    	abean.set("status",rowSet.getString("status")!=null?rowSet.getString("status"):"0");
			    	if(!"2".equals((String)abean.get("status"))&&!"7".equals((String)abean.get("status")))
			    		isAllSub="0";
			    	abean.set("fillctrl",rowSet.getString("fillctrl")!=null?rowSet.getString("fillctrl"):"0");
			    	abean.set("know_id",rowSet.getString("know_id")!=null?rowSet.getString("know_id"):"");
			    	abean.set("whole_grade_id",rowSet.getString("whole_grade_id")!=null?rowSet.getString("whole_grade_id"):"");
			    	
			    	if(batchGradeOthField.length()>0)
			    	{ 
			    		if("A".equalsIgnoreCase(itemfield.getItemtype()))
			    		{
			    			if(rowSet.getString(batchGradeOthField)!=null&&rowSet.getString(batchGradeOthField).trim().length()>0)
			    			{
			    				if(!"0".equals(itemfield.getCodesetid()))
			    				{
			    					abean.set("batchGradeOthInfo",AdminCode.getCodeName(itemfield.getCodesetid(),rowSet.getString(batchGradeOthField).trim()));
			    				}
			    				else
			    				{
			    					abean.set("batchGradeOthInfo",rowSet.getString(batchGradeOthField).trim());
			    				}	
			    			}
			    		}
			    		else if("N".equalsIgnoreCase(itemfield.getItemtype()))
			    		{
			    			if(rowSet.getString(batchGradeOthField)!=null&&rowSet.getString(batchGradeOthField).trim().length()>0)
			    			{
			    				abean.set("batchGradeOthInfo",myformat1.format(rowSet.getDouble(batchGradeOthField)));
			    			}
			    		}
			    		else if("D".equalsIgnoreCase(itemfield.getItemtype()))
			    		{
			    			if(rowSet.getDate(batchGradeOthField)!=null)
			    			{
			    				abean.set("batchGradeOthInfo",fm.format(rowSet.getDate(batchGradeOthField)));
			    			}
			    		} 
			    	}	
			    	objList.add(abean);
			    }
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  
		  return objList;
	  }
	  
	  
	  
	  
	  /**
	   * 取得各指标的父项目
	   * @param pointList
	   * @param template_id
	   * @return
	   */
	  public HashMap getPointParentItemMap(ArrayList pointList,String template_id)
	  {
		  HashMap map=new HashMap();
		  try
		  {
			  ContentDAO dao = new ContentDAO(this.conn);
			  ArrayList itemList=getTemplateItemList(template_id);
			  LazyDynaBean abean=new LazyDynaBean();
			  LazyDynaBean _abean=null;
			  for(int i=0;i<pointList.size();i++)
			  {
				  abean=(LazyDynaBean)pointList.get(i);
				  String point_id=(String)abean.get("point_id");
				  String item_id=(String)abean.get("item_id");
				  ArrayList tempList=new ArrayList();
				  while(true)
				  {
					  boolean flag=true;
					  for(int j=0;j<itemList.size();j++)
					  {
						  _abean=(LazyDynaBean)itemList.get(j);
						  String _item_id=(String)_abean.get("item_id");
						  String _parent_id=(String)_abean.get("parent_id");
						  if(_item_id.equalsIgnoreCase(item_id))
						  {
							  tempList.add(_abean);
							  if(_parent_id==null||_parent_id.trim().length()==0)
								  flag=false;
							  else
								  item_id=_parent_id;
							  break;
						  }
					  }
					  if(!flag)
						  break;
				  }
				  map.put(point_id.toLowerCase(),tempList);
			  }
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  return map;
	  }
	  
	  
	  
	 
	  /**
	   * 取得模板下的指标
	   * @param template_id
	   * @return
	   */
	  public ArrayList getPointList(String template_id)
	  {
		  ArrayList list=new ArrayList();
		  try
		  {
			 ContentDAO dao = new ContentDAO(this.conn);
			 ArrayList tempPointList=new ArrayList();
			 HashMap map2=new HashMap();
			 ArrayList seqList=new ArrayList();
			 String sql = "select po.point_id,po.pointname,po.pointkind,pi.item_id,po.visible,po.fielditem,po.status,pp.score,po.Kh_content,po.Gd_principle from per_template_item pi,per_template_point pp,per_point po "
				    +" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id='" + template_id + "' "
			        +" order by pp.seq";
			 RowSet   rowSet = dao.search(sql);
			 LazyDynaBean abean=null; 
			 while(rowSet.next())
			 {
				  abean=new LazyDynaBean();
				  abean.set("point_id", rowSet.getString("point_id"));
				  abean.set("pointname",Sql_switcher.readMemo(rowSet, "pointname"));
				  abean.set("pointkind", rowSet.getString("pointkind"));
				  abean.set("item_id", rowSet.getString("item_id"));
				  abean.set("visible", rowSet.getString("visible")!=null?rowSet.getString("visible"):"0");
				  abean.set("fielditem", rowSet.getString("fielditem")!=null?rowSet.getString("fielditem"):"");
				  abean.set("status", rowSet.getString("status"));
				  abean.set("score", rowSet.getString("score"));
				  abean.set("kh_content", Sql_switcher.readMemo(rowSet, "kh_content"));
				  abean.set("gd_principle",Sql_switcher.readMemo(rowSet, "gd_principle"));
				  tempPointList.add(abean);
				  map2.put(rowSet.getString("point_id").toLowerCase(),abean);
			 }
			 get_LeafItemList(template_id,tempPointList ,seqList);
			 for (Iterator t = seqList.iterator(); t.hasNext();)
			 {
					String temp = (String) t.next();
					LazyDynaBean atemp = (LazyDynaBean) map2.get(temp);
					list.add(atemp);
			 }
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  return list;
	  }
	  
	  /**
		 * 叶子项目列表
		 *
		 */
		public void get_LeafItemList(String templateID,ArrayList pointList ,ArrayList seqList)
		{
			try
			{
				ArrayList itemList=getTemplateItemList(templateID);
				LazyDynaBean abean=null;
				for(int i=0;i<itemList.size();i++)
				{
					abean=(LazyDynaBean)itemList.get(i);
					String parent_id=(String)abean.get("parent_id");
					if(parent_id.length()==0)
					{
						setLeafItemFunc(abean,pointList,itemList,seqList);
					}
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
	   //	递归查找叶子项目
		public void setLeafItemFunc(LazyDynaBean abean,ArrayList pointList,ArrayList itemList,ArrayList seqList)
		{
			String item_id=(String)abean.get("item_id");
			String child_id=(String)abean.get("child_id");
			//判断项目下是否有指标
			
			
			
			if(child_id.length()==0)
			{
				String itemid=(String)abean.get("item_id");
				for(int i=0;i<pointList.size();i++)
				{
					LazyDynaBean tempBean=(LazyDynaBean)pointList.get(i);
					String a_itemid=(String)tempBean.get("item_id");
					if(itemid.equals(a_itemid))
					{
						seqList.add(((String)tempBean.get("point_id")).toLowerCase());
					}
				}
				return;
			}
			LazyDynaBean a_bean=null;
			for(int i=0;i<pointList.size();i++)
			{
				LazyDynaBean temp=(LazyDynaBean)pointList.get(i);
				String a_itemid=(String)temp.get("item_id");
				if(item_id.equals(a_itemid))
				{
					seqList.add(((String)temp.get("point_id")).toLowerCase());
				}
			}
			
			for(int j=0;j<itemList.size();j++)
			{
					a_bean=(LazyDynaBean)itemList.get(j);
					String parent_id=(String)a_bean.get("parent_id");
					if(parent_id.equals(item_id))
						setLeafItemFunc(a_bean,pointList,itemList,seqList);
			}
		}
	  
	  
		/**
		 * 取得 模板项目记录
		 * @return
		 */
		public ArrayList getTemplateItemList(String templateID)
		{
			ArrayList list=new ArrayList();
			try
			{
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rowSet=dao.search("select * from  per_template_item where template_id='"+templateID+"'  order by seq");
			    LazyDynaBean abean=null;
				while(rowSet.next())
			    {
					abean=new LazyDynaBean();
			    	abean.set("item_id",rowSet.getString("item_id"));
			    	abean.set("parent_id",rowSet.getString("parent_id")!=null?rowSet.getString("parent_id"):"");
			    	abean.set("child_id",rowSet.getString("child_id")!=null?rowSet.getString("child_id"):"");
			    	abean.set("template_id",rowSet.getString("template_id"));
			    	abean.set("itemdesc",rowSet.getString("itemdesc"));
			    	abean.set("seq",rowSet.getString("seq"));
			    	abean.set("kind",rowSet.getString("kind")!=null?rowSet.getString("kind"):"1");	
			    	list.add(abean);
			    }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return list;
		}
	  
	  
	  ////////////////////------------------------///////////////
	  
	  
	  
	  
	  /**
	   * 取得各指标的标度
	   * @param template_id
	   * @return
	   */
	  public HashMap getPointGradeDescMap(String template_id)
	  {
		  HashMap map=new HashMap();
		  try
		  {
			  ContentDAO dao = new ContentDAO(this.conn);
			  String sql="select pp.item_id,po.point_id,po.pointname,po.pointkind,pg.gradedesc,pg.gradecode,pg.top_value,pg.bottom_value,pp.score,pg.gradevalue "
				  +" from per_template_item pi,per_template_point pp,per_point po ,per_grade pg "
				  +" where pi.item_id=pp.item_id and pp.point_id=po.point_id and  po.point_id=pg.point_id and template_id='"+template_id+"' "
				  +" order by pp.seq,pg.grade_id";
			  RowSet rowSet=dao.search(sql);
			  LazyDynaBean abean=null;
			  
			  String pointid="";
			  ArrayList tempList=new ArrayList();
			  
			  while(rowSet.next())
			  {
				  abean=new LazyDynaBean();
				  abean.set("item_id", rowSet.getString("item_id"));
				  abean.set("point_id",rowSet.getString("point_id"));
				  abean.set("pointname",Sql_switcher.readMemo(rowSet, "pointname"));
				  abean.set("pointkind",rowSet.getString("pointkind"));
				  abean.set("gradedesc", Sql_switcher.readMemo(rowSet, "gradedesc"));
				  abean.set("gradecode",rowSet.getString("gradecode"));
				  abean.set("top_value",rowSet.getString("top_value")!=null?rowSet.getString("top_value"):"0");
				  abean.set("bottom_value", rowSet.getString("bottom_value")!=null?rowSet.getString("bottom_value"):"0");
				  abean.set("score", rowSet.getString("score")!=null?rowSet.getString("score"):"0");
				  abean.set("gradevalue", rowSet.getString("gradevalue")!=null?rowSet.getString("gradevalue"):"0");
				  
				  if(pointid.length()==0)
					  pointid=rowSet.getString("point_id");
				  
				  if(pointid.equalsIgnoreCase(rowSet.getString("point_id")))
						tempList.add(abean);
				  else
				  {
						map.put(pointid.toLowerCase(),tempList);
						tempList=new ArrayList();
						tempList.add(abean);
						pointid=rowSet.getString("point_id");
				 }
			  }
			  map.put(pointid.toLowerCase(),tempList);
			  if(rowSet!=null)
				  rowSet.close();
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  return map;
	  }

	public String getTableWidth() {
		return tableWidth;
	}

	public void setTableWidth(String tableWidth) {
		this.tableWidth = tableWidth;
	}

	public ArrayList getPointList() {
		return pointList;
	}

	public void setPointList(ArrayList pointList) {
		this.pointList = pointList;
	}

	public ArrayList getObject_List() {
		return object_List;
	}

	public void setObject_List(ArrayList object_List) {
		this.object_List = object_List;
	}

	public String get_point_id() {
		return _point_id;
	}

	public void set_point_id(String _point_id) {
		this._point_id = _point_id;
	}

	public String getIsAllSub(ArrayList objectList) {
		LazyDynaBean abean=null;
		for(int i=0;i<objectList.size();i++)
		{
			abean=(LazyDynaBean)objectList.get(i);
			if(!"2".equals((String)abean.get("status"))&&!"7".equals((String)abean.get("status")))
	    		isAllSub="0";
		}
		return isAllSub;
	}

	public void setIsAllSub(String isAllSub) {
		this.isAllSub = isAllSub;
	}

	public HashMap getPointParentItemMap() {
		return pointParentItemMap;
	}

	public void setPointParentItemMap(HashMap pointParentItemMap) {
		this.pointParentItemMap = pointParentItemMap;
	}

	public HashMap getObject_priv_map() {
		return object_priv_map;
	}

	public void setObject_priv_map(HashMap object_priv_map) {
		this.object_priv_map = object_priv_map;
	}
 
	 
	 
}
