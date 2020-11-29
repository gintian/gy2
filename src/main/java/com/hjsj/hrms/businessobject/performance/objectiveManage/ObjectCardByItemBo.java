package com.hjsj.hrms.businessobject.performance.objectiveManage;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class ObjectCardByItemBo {
	private Connection conn=null;
	private RecordVo plan_vo=null;
	private RecordVo perObject_vo=null;
	private RecordVo template_vo=null; 
	private UserView userView=null;
	private String model="";  // 1:团对  2:我的目标   3:目标制订  4.目标评估  5.目标结果  6:目标执行情况   7:目标卡代制定  8:评分调整
	private int opt=0;  //0:查看  1：操作  2.打分 
	private Hashtable planParam=null;
	private Hashtable publicParam=null;
	private ObjectCardBo objectCardBo=null;
	
	
	private ArrayList templateItemList=new ArrayList();
	private HashMap   templateItemMap=new HashMap();
	private ArrayList p04List=new ArrayList();
	
	/*一级项目 list {DynaBean abean1,DynaBean abean2,..... }
	 * abean1 { itemid:xxxx  ;itemdesc:xxxxx ; subItemLay:n(大项目下的小项目层级)  ;headList:xxxxx(ArrayList) ;leafTask {DynaBean aabean1,DynaBean aabean2,...}  }
	 *     DynaBean aabean1 {parentItemList:xxx ;  p0400:xxxx; }
	 */
	private ArrayList topItemList=new ArrayList();  
	private HashMap   itemToPointMap=new HashMap(); 
	private HashMap   itemToNumberMap=new HashMap();	//各项目拥有的节点数
	private HashMap   itemDataMap=new HashMap();   //各大项对应的 dataList;
	private HashMap groupToItemid = new HashMap();//组号对应的项目号，用来判断将相同组号的项目合并
	private HashMap itemidToData = new HashMap();//项目号对应bean（组号，显示的表头指标）
	private ArrayList mainbodyList =null;//其他考核主体列表
	/**
	 * 构造方法，ObjectCardBo必须是所以数据都被初始化后的
	 * @param cardBo
	 * @param _conn
	 */
	public ObjectCardByItemBo(ObjectCardBo cardBo, Connection _conn)
	{
		objectCardBo=cardBo;
		this.userView=objectCardBo.getUserView();
		this.plan_vo=objectCardBo.getPlan_vo();
		this.perObject_vo=objectCardBo.getPerObject_vo();
		this.template_vo=objectCardBo.getTemplate_vo();
		this.model=objectCardBo.getModel();
		this.opt=objectCardBo.getOpt();
		this.planParam=objectCardBo.getPlanParam();
		this.publicParam=objectCardBo.getPublicParam();
		this.conn=_conn;
		
	}
	
	public ObjectCardByItemBo()
	{
		
	}
	
	public String getObjectCardHtml()
	{
		StringBuffer html_str=new StringBuffer("");
		initData();
		
		
		
		return html_str.toString();
	}
	
	
	
	int lay=0;
	/***
	 * ---------------------
	 *  初始化数据,全部放在此处         
	 *  -------------------------------
	 * */
	private void initData()
	{
		this.templateItemList=objectCardBo.getTemplateItemList();
		objectCardBo.executeP04_commonnessData();
		this.p04List=objectCardBo.getP04List();
		LazyDynaBean a_bean=null;
		LazyDynaBean a_bean1=null;
		for(int i=0;i<this.templateItemList.size();i++)
		{
			a_bean=(LazyDynaBean)this.templateItemList.get(i);
			String parent_id=(String)a_bean.get("parent_id");
			String item_id=(String)a_bean.get("item_id");
			String itemdesc=(String)a_bean.get("itemdesc");
			if(parent_id.trim().length()==0) //一级项目
			{
			//	abean1 { itemid:xxxx  ;itemdesc:xxxxx ; subItemLay:n(大项目下的小项目层级)  ;headList:xxxxx(ArrayList) ;leafTask {DynaBean aabean1,DynaBean aabean2,...}  }
			//	     DynaBean aabean1 {parentItemList:xxx ;  p0400:xxxx; }
				a_bean1=new LazyDynaBean();
				a_bean1.set("itemid",item_id);
				a_bean1.set("itemdesc",itemdesc);
				lay=0;
				ArrayList leafTask=getLeafTask(a_bean);
				a_bean1.set("leafTask", leafTask);
				a_bean1.set("subItemLay", String.valueOf(lay));
				ArrayList headList=new ArrayList();
				headList=getHeadList(a_bean);
				a_bean1.set("headList", headList);
				
				topItemList.add(a_bean1);
			}
			templateItemMap.put(item_id, a_bean);
		} 
		this.itemToPointMap=getItemToPointMap();
		this.itemToNumberMap=getItemPointNum();
		this.itemidToData=this.analyseTargetGroupItems();
		
	}
	
	
	
	
	
	
	ArrayList leafNodeList=new ArrayList();
	/**
	 * 获得大项目下的所有任务列表
	 * @param a_bean
	 * @return
	 */
	private ArrayList getLeafTask(LazyDynaBean a_bean)
	{
		ArrayList list=new ArrayList();
		leafNodeList=new ArrayList();
		setLeafNodeFunc(a_bean);
		for(int i=0;i<this.leafNodeList.size();i++)
		{
			String _str=(String)this.leafNodeList.get(i);
			LazyDynaBean _bean=new LazyDynaBean();
			_bean.set("p0400", _str);
			ArrayList paretItemList=getParentItemList(_str);
			if(paretItemList.size()>this.lay)
				this.lay=paretItemList.size();
			_bean.set("parentItemList",paretItemList);
			list.add(_bean);
		} 
		return list;
	}
	
	/**
	 * 获得任务的父项目
	 * @param p0400
	 * @return
	 */
	private ArrayList parentItemList=new ArrayList();
	private ArrayList getParentItemList(String p0400)
	{
		parentItemList=new ArrayList();
		String _itemid="";
		if(p0400.indexOf("xxx")!=-1)
		{ 
			_itemid=p0400.split(":")[0].trim();
		}
		else
		{
			
			
		}
		
		
		return parentItemList;
	}
	
	
	
	
	 //	递归查找叶子任务
	public void setLeafNodeFunc(LazyDynaBean abean)
	{
		String item_id=(String)abean.get("item_id");
		String child_id=(String)abean.get("child_id");
		LazyDynaBean _bean=null;
		//判断项目下是否有指标
		if(child_id.length()==0)
		{
			if(itemToPointMap.get(item_id)!=null)
			{
				ArrayList tempList=(ArrayList)itemToPointMap.get(item_id);
				for(int i=0;i<tempList.size();i++)
				{
					_bean=(LazyDynaBean)tempList.get(i);
					String p0400=(String)_bean.get("p0400");
					leafNodeList.add(p0400);
				}
			}
			else
			{
				leafNodeList.add(item_id+":xxx");  //空任务
			}
			
			return;
		}
		else 
		{
			if(itemToPointMap.get(item_id)!=null)
			{
				ArrayList tempList=(ArrayList)itemToPointMap.get(item_id);
				for(int i=0;i<tempList.size();i++)
				{
					_bean=(LazyDynaBean)tempList.get(i);
					String p0400=(String)_bean.get("p0400");
					leafNodeList.add(p0400);
				}
			}
		}
		LazyDynaBean a_bean=null;
		for(int j=0;j<this.templateItemList.size();j++)
		{
				a_bean=(LazyDynaBean)this.templateItemList.get(j);
				String parent_id=(String)a_bean.get("parent_id");
				if(parent_id.equals(item_id))
					setLeafNodeFunc(a_bean);
		}
	}
	
	
	
	
	/**
	 * 获得一级项目下 列表的表头信息列表
	 * @param a_bean
	 * @return
	 */
	private ArrayList getHeadList(LazyDynaBean a_bean)
	{
		ArrayList list=new ArrayList();
		try{
	    	String item_id=(String)a_bean.get("item_id");//一级项目id，每个一级项目一个表，headlist不同
	    	String child_id=(String)a_bean.get("child_id");
	    	if(this.itemidToData!=null&&this.itemidToData.get(item_id)!=null)
	    	{
	    		LazyDynaBean bean = (LazyDynaBean)this.itemidToData.get(item_id);
	    		String fields = (String)bean.get("fields");
	    		ArrayList fieldList=DataDictionary.getFieldList("P04",Constant.USED_FIELD_SET);	
	    		FieldItem fielditem2=DataDictionary.getFieldItem("p0407");
	    		LazyDynaBean abean=null;
	    		if(child_id!=null&&child_id.trim().length()>0)//有孩子节点，才显示项目列
	    		{
	    			FieldItem fielditem=DataDictionary.getFieldItem("item_id");
	    			abean=new LazyDynaBean();
	    			abean.set("itemid", "itemid");
	    			if(fielditem==null|| "项目号".equalsIgnoreCase(fielditem.getItemdesc().trim()))
	    				abean.set("itemdesc","项目");
	    			else
	    				abean.set("itemdesc",fielditem.getItemdesc());
	    			abean.set("itemType","A");
	    			abean.set("codesetid","0");
	    			list.add(abean);
	    		}
	    		abean=new LazyDynaBean();
				abean.set("itemid", "p0401");
				if(fielditem2==null|| "任务内容".equalsIgnoreCase(fielditem2.getItemdesc().trim()))
				{
					if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))) //中国联通
						abean.set("itemdesc","工作目标");
					else
						abean.set("itemdesc",ResourceFactory.getProperty("jx.khplan.point"));
				}
				else
					abean.set("itemdesc",fielditem2.getItemdesc());
				abean.set("itemType","A");
				abean.set("codesetid","0");
				abean.set("decimalwidth","0");
				list.add(abean);
	    		if(this.model!=null&& "8".equals(this.model))//评分调整
	    		{
	    			
	    		}
	    	}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	/***
	 * 分析每个一级项目的组号和表头指标
	 * @return
	 */
	public HashMap analyseTargetGroupItems()
	{
		HashMap map = new HashMap();
		try
		{
			String TargetGroupItems="";
    		if(this.planParam!=null)
    		{
    			TargetGroupItems=(String)this.planParam.get("TargetGroupItems");
    			String[] arr = TargetGroupItems.split(";");
    			LazyDynaBean bean = null;
    			for(int i=0;i<arr.length;i++)
    			{
    				String[] arr_1 = arr[i].split(":");
    				String itemid = arr_1[0].split(",")[0];
    				String group=arr_1[0].split(",")[1];
    				String fields = arr_1[1];
    				bean = new LazyDynaBean();
    				this.groupToItemid.put(group, itemid);
    				bean.set("id", itemid);
    				bean.set("group", group);
    				bean.set("fields",fields);
    				map.put(itemid, bean);
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
	 * 取得项目对应任务map
	 * @return
	 */
	public HashMap getItemToPointMap()
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			LazyDynaBean abean=null;
			for(int i=0;i<this.p04List.size();i++)
			{
				abean=(LazyDynaBean)this.p04List.get(i);
				String p0400=(String)abean.get("p0400");
				String item_id=(String)abean.get("item_id");
				if(map.get(item_id)!=null)
				{
					ArrayList tempList=(ArrayList)map.get(item_id);
					tempList.add(abean);
					map.put(item_id,tempList);
				}
				else
				{
					ArrayList tempList=new ArrayList();
					tempList.add(abean);
					map.put(item_id,tempList);
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
	 * 取得项目拥有的节点数
	 * @return
	 */
	private HashMap getItemPointNum()
	{
		HashMap map=new HashMap();
		LazyDynaBean a_bean=null;
		LazyDynaBean aa_bean=null;
		LazyDynaBean aaa_bean=null;
		for(int i=0;i<templateItemList.size();i++)
		{
			a_bean=(LazyDynaBean)this.templateItemList.get(i);
			String self_itemid=(String)a_bean.get("item_id"); 
			ArrayList list=new ArrayList();
			getLeafItemList(a_bean,list);
			int n=0;
			for(int j=0;j<list.size();j++)
			{
				aa_bean=(LazyDynaBean)list.get(j);
				String item_id=(String)aa_bean.get("item_id");
				if(itemToPointMap.get(item_id)!=null)
					n+=((ArrayList)itemToPointMap.get(item_id)).size();
				else
					n+=1;
			} 
			map.put(self_itemid,String.valueOf(n));
		}
		return map;
	}
	
	
	private void getLeafItemList(LazyDynaBean abean,ArrayList list)
	{
		String item_id=(String)abean.get("item_id");
		String child_id=(String)abean.get("child_id");
		
		if(child_id.length()==0)
		{
			list.add(abean);
				return;
		}
		else if(itemToPointMap.get(item_id)!=null)  //**
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
	
	/**------------------         end  --------------------------*/
	
	
	/**
	 * 没有评分，按参数设置的默认值填入
	 */
	public ArrayList getP04ResultList(ObjectCardBo bo,String object_id,String plan_id,UserView userView)
	{
		ArrayList list=new ArrayList();
		
		Hashtable planParam=bo.getPlanParam();
		ArrayList p0400List=bo.getSelfP04List();
		HashMap perPointMap=bo.getPerPointMap();
		HashMap perPointScoreMap=bo.getSelfPerPointScoreMap();
		HashMap pointDescMap=bo.getTemplatePointDetail();			   //取得模板引入的绩效指标标度
		
		try
		{
		
			int KeepDecimal = Integer.parseInt((String)planParam.get("KeepDecimal")); // 保留小数位
			String  BlankScoreOption = (String) planParam.get("BlankScoreOption");// 指标未打分时，0 按未打分处理，1 计为最高分，默认值为按未打分处理 2用下面的参数
			String  BlankScoreUseDegree = (String) planParam.get("BlankScoreUseDegree");  //指标未打分，按用户定义的标度, 具体选自标准标度中, 如果指标中没有所定义标度，按未打分处理。A|B|C…
			String EvalOutLimitStdScore=(String)planParam.get("EvalOutLimitStdScore");  //评分时得分不受标准分限制True, False, 默认为 False;都加
			LazyDynaBean point_bean=null;
			String scoreflag=(String)planParam.get("scoreflag");  //=2混合，=1标度(默认值=混合)=4加扣分
		//	(id,plan_id,object_id,mainbody_id,p0400,score,amount,degree_id)
			
			if("4".equals(scoreflag))
				return list;
			
			ArrayList recordList=new ArrayList();
			
			for(int i=0;i<p0400List.size();i++)
			{
				
				recordList=new ArrayList();
				point_bean=(LazyDynaBean)p0400List.get(i);
				String fromflag=(String)point_bean.get("fromflag");
				String p0400=(String)point_bean.get("p0400");
				String p0401=(String)point_bean.get("p0401");
				String p0413=(String)point_bean.get("p0413");
				String p0415=(String)point_bean.get("p0415");
				String chg_type=(String)point_bean.get("chg_type");
				
				if(chg_type!=null&& "3".equals(chg_type))
					continue;
				
				
				IDGenerator idg = new IDGenerator(2, this.conn);
				String id = idg.getId("per_target_evaluation.id");
				recordList.add(new Integer(id));
				recordList.add(new Integer(plan_id));
				recordList.add(object_id);
				recordList.add(userView.getA0100());
				recordList.add(new Integer(p0400));
				
				
				
				boolean flag=true;
				if("2".equals(fromflag))  //来源KPI指标
				{
					LazyDynaBean pointBean=(LazyDynaBean)perPointMap.get(p0401.toLowerCase());
					String  point_kind="0";
					String  status="0";
					if(pointBean!=null)
					{
						point_kind=(String)pointBean.get("pointkind");
						status=(String)pointBean.get("status");
					}
					
					//统一打分指标
					if("1".equals(point_kind)&& "1".equals(status))
					{
						 continue;
					}
					else
					{
						
						ArrayList pointGradeList=(ArrayList)pointDescMap.get(p0401);
						if(pointGradeList==null)
							pointGradeList=new ArrayList();
						String pointkind="0";
						if(pointGradeList!=null&&pointGradeList.size()>0)
						{
							LazyDynaBean abean=(LazyDynaBean)pointGradeList.get(0);
							pointkind=(String)abean.get("pointkind");           // 0:定性  1:定量
						}
						if("0".equals(pointkind)) //定性
						{
							if("1".equals(scoreflag))  //标度
							{
								if(pointGradeList.size()>0)
								{
									if("1".equalsIgnoreCase(BlankScoreOption)) //计为最高
									{	
										LazyDynaBean a_bean=(LazyDynaBean)pointGradeList.get(0);
										 
										String a_info=bo.setPointValue(recordList,(String)a_bean.get("gradecode"),pointGradeList,Float.parseFloat(p0413),scoreflag,pointkind,new StringBuffer());
										if(a_info.length()>0)
											continue;
									}
									if("2".equalsIgnoreCase(BlankScoreOption)) //
									{  
										String a_info=bo.setPointValue(recordList,BlankScoreUseDegree,pointGradeList,Float.parseFloat(p0413),scoreflag,pointkind,new StringBuffer());
										if(a_info.length()>0)
											continue;
									}
								}
							}
							else if("2".equals(scoreflag)) //混合
							{
								
								if("1".equalsIgnoreCase(BlankScoreOption)) //计为最高
								{
									if(p0413==null|| "".equals(p0413))
										continue;
									if(p0413!=null&&!"".equals(p0413))
									{ 
										String a_info=bo.setPointValue(recordList,p0413,pointGradeList,Float.parseFloat(p0413),scoreflag,pointkind,new StringBuffer());
										if(a_info.length()>0)
											continue; 
									}
								}
								if("2".equalsIgnoreCase(BlankScoreOption)) //
								{
									LazyDynaBean a_bean=null; 
									String value="";
									for(int j=0;j<pointGradeList.size();j++)
									{
										a_bean=(LazyDynaBean)pointGradeList.get(j);
										String grade_template_id=(String)a_bean.get("gradecode");
										String gradevalue=(String)a_bean.get("gradevalue");
										if(grade_template_id.equalsIgnoreCase(BlankScoreUseDegree))
										{
											if(p0413==null|| "".equals(p0413))
												continue;
											value=PubFunc.multiple(gradevalue, p0413,1);
											break;
										}
									}
									String a_info=bo.setPointValue(recordList,value,pointGradeList,Float.parseFloat(p0413),scoreflag,pointkind,new StringBuffer());
									if(a_info.length()>0)
										continue;
									
									
								}
							}
							
						}
						if("1".equals(pointkind)) //定量
						{
							if(pointGradeList.size()>0)
							{
								if("1".equalsIgnoreCase(BlankScoreOption)) //计为最高
								{	
									LazyDynaBean a_bean=(LazyDynaBean)pointGradeList.get(0); 
									String a_info=bo.setPointValue(recordList,(String)a_bean.get("top_value"),pointGradeList,Float.parseFloat(p0413),scoreflag,pointkind,new StringBuffer());
									if(a_info.length()>0)
										continue;
									 
								}
								if("2".equalsIgnoreCase(BlankScoreOption)) //
								{
									LazyDynaBean a_bean=null;
									String value="";
									for(int j=0;j<pointGradeList.size();j++)
									{
										a_bean=(LazyDynaBean)pointGradeList.get(j);
										String grade_template_id=(String)a_bean.get("gradecode");
										String gradevalue=(String)a_bean.get("gradevalue");
										if(grade_template_id.equalsIgnoreCase(BlankScoreUseDegree))
										{  
											value=(String)a_bean.get("top_value"); 
											break;
										}
									}
									String a_info=bo.setPointValue(recordList,value,pointGradeList,Float.parseFloat(p0413),scoreflag,pointkind,new StringBuffer());
									if(a_info.length()>0)
										continue;
								}
							}
							
						}
					}
				
				}
				else
				{
					ArrayList pointGradeList=(ArrayList)pointDescMap.get("per_grade_desc");
					if(pointGradeList==null)
						pointGradeList=new ArrayList();
					if("1".equals(scoreflag))  //标度
					{
						String grade_id="";
						if(pointGradeList.size()>0)
						{
							if("1".equalsIgnoreCase(BlankScoreOption)) //计为最高
							{	
								LazyDynaBean a_bean=(LazyDynaBean)pointGradeList.get(0); 
								grade_id=(String)a_bean.get("grade_template_id");
							}
							if("2".equalsIgnoreCase(BlankScoreOption)) //
							{ 
								grade_id=BlankScoreUseDegree;
							}
						}
						
						LazyDynaBean _abean=null;
						String degree_id="";
						String gradevalue="";
						for(int j=0;j<pointGradeList.size();j++)
						{
								_abean=(LazyDynaBean)pointGradeList.get(j);
								String grade_template_id="";
								if("1".equals(fromflag))
									grade_template_id=(String)_abean.get("grade_template_id");
								else
									grade_template_id=(String)_abean.get("gradecode");
								
								String _gradevalue=(String)_abean.get("gradevalue");
								if(grade_id.equalsIgnoreCase(grade_template_id))
								{
									degree_id=grade_template_id;
									gradevalue=_gradevalue;
									break;
								}
						}
						 if(degree_id.length()==0)
						 {
							continue;
						 }
						 
						 if(Float.parseFloat(p0413)<0)
							 gradevalue=String.valueOf(1-Double.parseDouble(gradevalue));
						 recordList.add(new Float(PubFunc.multiple(gradevalue,p0413,KeepDecimal)));     //    new Float(temp[1]));
						 recordList.add(new Float(0));
						 recordList.add(degree_id);
						
						
					}
					else if("2".equals(scoreflag)) //混合
					{
						String _value="";
						if("1".equalsIgnoreCase(BlankScoreOption)) //计为最高
						{
							if(p0413!=null&&!"".equals(p0413))
							{
								_value=p0413;
							} 
						}
						if("2".equalsIgnoreCase(BlankScoreOption)) //
						{
							LazyDynaBean a_bean=null;
							for(int j=0;j<pointGradeList.size();j++)
							{
								a_bean=(LazyDynaBean)pointGradeList.get(j);
								String grade_template_id=(String)a_bean.get("grade_template_id");
								String gradevalue=(String)a_bean.get("gradevalue");
								if(grade_template_id.equalsIgnoreCase(BlankScoreUseDegree))
								{
									if(p0413==null|| "".equals(p0413))
										break; 
									_value=PubFunc.multiple(gradevalue, p0413,1);
									break;
								}
							}
						}
						
						recordList.add(new Float(PubFunc.round(_value,KeepDecimal)));
						recordList.add(new Float(0));
						LazyDynaBean _abean=null;
						String degree_id="";
						 
						 
						 double _maxScore=0;
						 String _maxGrade="";
						 double _minScore=-1;
						 String _minGrade="";
						 
						 int n=0;
						 for(int j=0;j<pointGradeList.size();j++)
						 {
								_abean=(LazyDynaBean)pointGradeList.get(j);
								String grade_template_id=(String)_abean.get("grade_template_id");
								String _gradevalue=(String)_abean.get("gradevalue");
								String top_value=(String)_abean.get("top_value");
								String bottom_value=(String)_abean.get("bottom_value");
								double value=Double.parseDouble(_value);
								double top=Double.parseDouble(PubFunc.multiple(top_value,p0413, 2));
								double bottom=Double.parseDouble(PubFunc.multiple(bottom_value,p0413, 2));
								
								if(Float.parseFloat(p0413)<0)
								{
									top=Double.parseDouble(PubFunc.multiple(String.valueOf(1-Double.parseDouble(top_value)),p0413, 2));
									bottom=Double.parseDouble(PubFunc.multiple(String.valueOf(1-Double.parseDouble(bottom_value)),p0413, 2));
								}
								
								
								if(value<=top&&value>=bottom)
								{
									degree_id=grade_template_id;
									break;
								}
								
								
								if("true".equalsIgnoreCase(EvalOutLimitStdScore))
								{
									if(top>_maxScore||n==0)
									{
										_maxScore=top;
										_maxGrade=grade_template_id;
									}
									if((bottom<_minScore)||n==0)
									{
										_minScore=bottom;
										_minGrade=grade_template_id;
									}
								}
								n++;
						 }
						 
						 if(degree_id.length()==0)
						 {
							double _score=Double.parseDouble(_value);
							if(_score>_maxScore)
								degree_id=_maxGrade;
							if(_score<_minScore)
								degree_id=_minGrade;
							
						 } 
						 recordList.add(degree_id); 
						
					}
				}
				list.add(recordList);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	
}
