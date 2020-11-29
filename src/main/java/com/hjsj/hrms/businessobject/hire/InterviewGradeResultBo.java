package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.businessobject.competencymodal.personPostModal.PersonPostModalBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class InterviewGradeResultBo {

	private Connection conn=null;
	private int columnWidth=65;
	private StringBuffer span_ids=new StringBuffer("");
	
	public InterviewGradeResultBo(Connection conn)
	{
		this.conn=conn;
	}
	private String currentAndvance="0";//要查看分数的阶段是否设置了高级测评方式
	private String hireState="";//要查看的人员分数是处于那个测评阶段
	private String interviewSearch="0";//要查看那个阶段的打分状况 0：考评总分（没有高级测评的情况下） 31：初始阶段 32：复试
	public String getInterviewSearch() {
		return interviewSearch;
	}

	public void setInterviewSearch(String interviewSearch) {
		this.interviewSearch = interviewSearch;
	}

	public String getCurrentAndvance() {
		return currentAndvance;
	}

	public void setCurrentAndvance(String currentAndvance) {
		this.currentAndvance = currentAndvance;
	}


	public String getHireState() {
		return hireState;
	}

	public void setHireState(String hireState) {
		this.hireState = hireState;
	}

	/**
	 * 生成提交表单  && 是否有附加选项  &&  最大标度限制
	 * @param template_id	模版id
	 * @param plan_id		考核计划id
	 * @param mainBodyID	考核主体id
	 * @param status        权重分值标识 0：分值　　１：权重
	 * @return
	 */
	public ArrayList getBatchGradeHtml(String template_id,String object_id,String status,String titleName,String z0101,String scoreflag)  throws GeneralException
	{		
		
		ArrayList resultList=new ArrayList();
		try
		{
			StringBuffer html=new StringBuffer("");
			ArrayList list=getPerformanceStencilList(template_id);
			ArrayList pointList=getPerPointList(template_id);
		    ArrayList arrayList=getTableHeaderHtml(template_id,list,pointList,status,titleName);
		    String    nodeKnowDegree=(String)arrayList.get(1);       //了解程度
		    String    wholeEval=(String)arrayList.get(2);		     //总体评价
		    String    gradeClass=(String)arrayList.get(3);           //等级分类id
		    String    isKnowWhole=(String)arrayList.get(4);     
		    String    fineMax=(String)arrayList.get(5);
		    String    summaryFlag=(String)arrayList.get(6);		     //是否有总结报告
		    String    dataArea=(String)arrayList.get(7);             //定量指标的数值范围 
		    //String    scoreflag=(String)arrayList.get(8);		     //=2混合，=1标度
		    String    dataArea2=(String)arrayList.get(9);		     //当为混合打分时，表示各指标的数值范围
		    ArrayList a_perPointList=(ArrayList)arrayList.get(10);   //取得最底层的指标格（按顺序）
		    String     pointDeformity=(String)arrayList.get(11);     
		    String    showNoMarking=(String)arrayList.get(12);
		    
		    if(dataArea2.length()>2) {
                dataArea2=dataArea2.substring(1);
            }
		    
			/* 写表头 */
		    html.append((String)arrayList.get(0));
		    
		    ArrayList alist=(ArrayList)pointList.get(1);
		    String[] atemp=(String[])alist.get(0);
			ArrayList objectList=getPerPlanObjects(object_id,atemp[0]);//获得考核的对象
			
			/* 判断打分状态 */
			int gradeStatus=0;
			for(Iterator t=objectList.iterator();t.hasNext();)
			{
				String[] temp=(String[])t.next();
				if(Integer.parseInt(temp[2])>gradeStatus) {
                    gradeStatus=Integer.parseInt(temp[2]);
                }
			}	
			
			HashMap totalScoreMap=getPerMainbodyTotalScore(object_id,(ArrayList)pointList.get(1),z0101,template_id);
			
			/* 写表体内容 */
			String bodyContext=getTableBodyHtml(objectList,object_id,template_id,pointList,nodeKnowDegree,wholeEval,gradeClass,summaryFlag,status,scoreflag,a_perPointList,showNoMarking,totalScoreMap);
			html.append(bodyContext);			
			resultList.add(html.toString());
			
		}
		catch(Exception e)
		 {
			 e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		 }
		return resultList;
	}
	
	
	
	
	

	/**
	 * 根据考核模版得到各指标的权重
	 * @param templateID
	 * @return
	 */
	public HashMap getPointRank(String templateID)
	{
		HashMap rankMap=new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet=null;
		try
		{
			rowSet=dao.search("select point_id,per_template_point.rank  from per_template_item,per_template_point where per_template_item.item_id=per_template_point.item_id  and  template_id='"+templateID+"'");
			while(rowSet.next())
			{
				rankMap.put(rowSet.getString("point_id"),rowSet.getString("rank"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return rankMap;
	}
	
	
	
	
	public HashMap getPerMainbodyTotalScore(String objectID,ArrayList pointList,String z0101,String tempLateID)
	{
		HashMap map=new HashMap();
		HashMap pointRankMap=getPointRank(tempLateID);
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet     rowSet=null;
		try
		{
			StringBuffer sql_select=new StringBuffer("");
//			StringBuffer sql_from=new StringBuffer(" from (select a0100,a0101 from UsrA01 where a0100 in (select A0100_1 from zp_test_template where a0100='"+objectID+"' and point_id='"+((String[])pointList.get(0))[0]+"' )) permain");
			StringBuffer sql_from=new StringBuffer(" from (select a0100,a0101 from UsrA01 where a0100 in (select distinct A0100_1 from zp_test_template where a0100='"+objectID+"' and interview="+this.interviewSearch+" )) permain");
			for(int i=0;i<pointList.size();i++)
			{
				String[] temp=(String[])pointList.get(i);
				String point_id=temp[0];
				String arank=(String)pointRankMap.get(point_id);
				
				sql_select.append("+"
						+ Sql_switcher.isnull("t_" + point_id + ".score", "0")
						+ "*" + arank);
				sql_from.append(" left join (select * from zp_test_template"
						+ " where  z0101='" + z0101
						+ "' and point_id='" + point_id + "' and a0100='"+objectID+"' and interview="+this.interviewSearch);
				sql_from.append(" ) t_" + point_id
						+ " on permain.a0100=t_" + point_id + ".A0100_1 ");
				
			}
			String sql="select permain.a0100,"+sql_select.substring(1)+" score "+sql_from.toString();
			
			rowSet=dao.search(sql);
			while(rowSet.next())
			{
				String score=rowSet.getString("score");
				String aScore=PubFunc.round(score,1);
				map.put(rowSet.getString("a0100"),aScore);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	

	/**
	 * 生成表体html
	 * @param objectList         考核对象集合
	 * @param plan_id			 考核计划
	 * @param templateID		 模版id
	 * @param nodeKnowDegree     了解程度
	 * @param wholeEval			 总体评价
	 * @param GradeClass		 等级分类ID
	 * @param summaryFlag        总结报告
	 * @param status             权重分值标识 0：分值  1：权重
	 * @param scoreflag          2:混合  1。标度
	 * @author dengc
	 * @return
	 */
	public String getTableBodyHtml(ArrayList objectList,String object_id,String templateID,ArrayList pointList,String nodeKnowDegree,String wholeEval,String gradeClass,String summaryFlag,String status,String scoreflag,ArrayList a_perPointList,String showNoMarking,HashMap totalScoreMap)
		     throws GeneralException
	{				
		StringBuffer bodyHtml=new StringBuffer("");	
		try
		{
			
			ArrayList pointGradeList=(ArrayList)pointList.get(0);      //详细信息的绩效指标集和			
			/* 得到某计划考核主体给对象的评分结果hashMap */
			HashMap perTableMap=getPerTableXXX(object_id,objectList);
			
			
			for(Iterator t=objectList.iterator();t.hasNext();)
			{
				String[]  temp=(String[])t.next();                     //姓名							
				HashMap   objectResultMap=null;						   //考核对象的考核结果
				if(!"0".equals(temp[2])) {
                    objectResultMap=(HashMap)perTableMap.get(temp[0]);
                }
				bodyHtml.append("<tr><td id='a' class='RecordRow common_border_color' width='");
				bodyHtml.append(columnWidth);
				bodyHtml.append("' align='center'    >");
				bodyHtml.append(temp[1]);
				bodyHtml.append("</td>");					
				ArrayList  tempList=new ArrayList();				   //指标标度值
				String[]   a_temp=(String[])pointGradeList.get(0);
						
				tempList.add(a_temp);
				String point_id=a_temp[1];	
				String point_kind="";   	
				String    pointID="";
				int   num=0;
				
				for(int i=1;i<pointGradeList.size();i++)
				{
					
					String[]  temp1=(String[])pointGradeList.get(i);
					
					if(point_id.equals(temp1[1]))
					{
						point_kind=temp1[3];					     //要素类型 0:定性要点；1:定量要点					
						tempList.add(temp1);
						pointID=temp1[1];
					}
					else
					{		
						bodyHtml.append(getPointTD(tempList,point_kind,temp,objectResultMap,status,pointID,scoreflag,a_perPointList,num));
						tempList.clear();
						point_id=temp1[1];
						tempList.add(temp1);
						num++;
					}		
				}	
				bodyHtml.append(getPointTD(tempList,point_kind,temp,objectResultMap,status,pointID,scoreflag,a_perPointList,num));
				
				bodyHtml.append("<td  class='RecordRow common_border_color' style='z-index:2;'   align='center'  width='"+columnWidth+"'     nowrap ><font color='red' >");
				bodyHtml.append((String)totalScoreMap.get(temp[0]));
				bodyHtml.append("</font></td>");
				bodyHtml.append("</tr> \n ");
				
				
			}
		}
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }	
		return "<tbody>"+bodyHtml.toString()+"</tbody>";
	}
	
	

	/**
	 * 得到某计划考核主体给对象的评分结果hashMap
	 * @return	HashMap
	 */
	public HashMap getPerTableXXX(String object_id,ArrayList mainbodyList)   throws GeneralException
	{
		HashMap hashMap=new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet     rowSet=null;
		try
		{
			for(Iterator t=mainbodyList.iterator();t.hasNext();)
			{
				String[] temp0=(String[])t.next();
				
				
					String objectid=object_id;
					HashMap map=new HashMap();
					String  sql="select * from zp_test_template  where a0100_1='"+temp0[0]+"' and a0100='"+objectid+"' and interview="+this.interviewSearch;
					rowSet=dao.search(sql);
					while(rowSet.next())
					{
						String[] temp=new String[7];
						for(int i=0;i<7;i++)
						{
							temp[i]=rowSet.getString(i+1);
						}
						map.put(temp[5],temp);
					}
					hashMap.put(temp0[0],map);
			}
				
					
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	
		return hashMap;
	}
	
	
	/**
	 * 按条件生成 <td>中的内容 
	 * @param tempList			指标标度值
	 * @param point_kind		要素类型 0:定性要点；1:定量要点
	 * @param temp				考核对象信息  id\姓名\状态
	 * @param objectResultMap	对象的考核结果
	 * @param status            权重分值标识 0：分值  1：权重
	 * @param pointMap          具有某人的指标权限map
	 * @param scoreflag			1:标度 2:混合
	 * @return
	 */
    public	String getPointTD(ArrayList tempList,String point_kind,String[] temp,HashMap objectResultMap,String status,String pointID,String scoreflag,ArrayList a_pointList,int num)
    {
    	StringBuffer   td=new StringBuffer("");
    	
    	String[] pointGrade=(String[])tempList.get(0);
    	String a_fieldItem=pointGrade[10]; 
    	if("1".equals(scoreflag)&& "0".equals(point_kind))
    	{
    		{
    			td.append("<td  class='RecordRow common_border_color' style='z-index:2;'   align='center'  width='"+columnWidth+"'     nowrap >");
	    		for(Iterator t=tempList.iterator();t.hasNext();)
	    		{
	    			String[] a_temp=(String[])t.next();
	    			
	    			if("1".equals(temp[2])|| "2".equals(temp[2])|| "3".equals(temp[2]))
	    			{
	    				String[] values=(String[])objectResultMap.get(a_temp[1]);
	    				if(values!=null)
	    				{
		    				if(values[6]!=null&&values[6].equals(a_temp[5]))
		    				{
		    					
		    					td.append(a_temp[4]);
		    				}
	    				}
	    			}  				    				
	    		}	    	
    		}
    	}
    	else
    	{
    		String[]  a_temp=(String[])tempList.get(0);
    	
    		{
    			td.append("<td  class='RecordRow common_border_color' style='z-index:2;'   align='center'  width='"+columnWidth+"'   nowrap >");
	    		if("1".equals(temp[2])|| "2".equals(temp[2])|| "3".equals(temp[2]))
				{
	    			String[] values=(String[])objectResultMap.get(a_temp[1]);   			
	    			if(values!=null&&(values[4]!=null||values[3]!=null))
	    			{			
	    				
		    			if("1".equals(status))
		    			{
		    			
		    				td.append(values[3]);
		    			}
		    			else
		    			{
		    				if(values[4]!=null)
		    				{
		    				
		    					td.append(values[4]);
		    				}
		    				else
		    				{
		    					if(values[3].indexOf(".")>0){
		    						values[3] = values[3].replaceAll("0+?$", "");
		    						values[3] = values[3].replaceAll("[.]$", "");
		    					}
		    					td.append(values[3]);
		    				}
		    			}
		    		
	    			}
				}  	
    		}

    	}
    	td.append("</td>");
    	return td.toString();
    }
	
	
	
	
	
	
	
	
	

	/**
	 * @param mainBodyID		考核对象
	 * @author dengc
	 * @return
	 */
	public ArrayList getPerPlanObjects(String objectid,String pointid)  throws GeneralException
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet     rowSet=null;
//		String sql="select a0100,a0101 from UsrA01 where a0100 in (select A0100_1 from zp_test_template where a0100='"+objectid+"' and point_id='"+pointid+"' )";
		String sql="select a0100,a0101 from UsrA01 where a0100 in (select distinct A0100_1 from zp_test_template where a0100='"+objectid+"' and interview="+this.interviewSearch+")";
		try
		{
			rowSet=dao.search(sql);
			while(rowSet.next())
			{
				String[] temp=new String[3];
				temp[0]=rowSet.getString(1);
				temp[1]=rowSet.getString(2);
				temp[2]="1";
				list.add(temp);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	

	
	/**
	 * 生成表头html && 是否有了解程度 && 是否有总体评价 && 等级分类id
	 * @param templateID
	 * @param status   权重分值标识
	 * @param list
	 */
	public ArrayList getTableHeaderHtml(String templateID,ArrayList list,ArrayList pointList,String status,String titleName)   throws GeneralException
	{
		ArrayList arraylist=new ArrayList();		
		try
		{
			ArrayList items=(ArrayList)list.get(0);				  //模版项目列表		
			HashMap itemsCountMap=(HashMap)list.get(1);		      //最底层项目的指标个数集合
			int       lays=((Integer)list.get(2)).intValue();     //表头的总层数
			HashMap   map=(HashMap)list.get(3);					  //各项目的子项目或指标个数		
			ArrayList bottomItemList=(ArrayList)list.get(4);	  //模版最底层的项目
		
			StringBuffer tempColumn=new StringBuffer("");         //临时变量集合
			ArrayList    tempColumnList=new ArrayList();
			
			StringBuffer tableHtml=new StringBuffer("");

			boolean      isData=false;                            //控制变量 判断是否有定量的指标
			int    isKnowWhole=0;							      //1;有了结程度或总评选项  2：两者都有	
			float  fineMax=-1;
			int a=0;                                              //控制变量	
			String      pointDeformity="0";						  //指标是否没有设上下限
			String GradeClass="1";					//等级分类ID
			String SummaryFlag="false";				//个人总结评价作为评分标准
			String NodeKnowDegree="false";			//了解程度
			String WholeEval="false";			    	//总体评价			
			String limitation="-1";                  //=-1不转换,模板中最高标度的数目 (大于0小于1为百分比，大于1为绝对数)		
			String scoreflag="1";					//=2混合，=1标度
		
			/* 画第一层表头 */
			
			
			int a_cols=1;
			StringBuffer a_tableHtml=new StringBuffer("");
			a_tableHtml.append("<tr> <th id='a' class='TableRow'  valign='middle' align='center'  rowspan='");
			a_tableHtml.append(lays);
			a_tableHtml.append("'  width='50' nowrap > ");		
			a_tableHtml.append(ResourceFactory.getProperty("hire.parameterSet.interviewer"));
			a_tableHtml.append("</th>");				
			for(Iterator t=items.iterator();t.hasNext();)
			{
				String[] temp=(String[])t.next();		
				if(temp[1]==null)
				{				
					a_tableHtml.append("<th valign='middle' align='center' class='TableRow'  colspan='");
					a_tableHtml.append((String)map.get(temp[0]));
					a_tableHtml.append("' width='"+(columnWidth*Integer.parseInt((String)map.get(temp[0])))+"'  height='50'   > ");
					a_tableHtml.append(temp[3]);					
					a_tableHtml.append("</th>");					
					tempColumnList.add(temp);
					a_cols+=Integer.parseInt((String)map.get(temp[0]));
				}
				a++;
			}	
			
			a_tableHtml.append("<th id='a' class='TableRow'  valign='middle' align='center'  rowspan='");
			a_tableHtml.append(lays);
			a_tableHtml.append("'  width='50' nowrap > ");		
			a_tableHtml.append(ResourceFactory.getProperty("label.zp_exam.sum_score"));
			a_tableHtml.append("</th>");
			
			a_tableHtml.append("</tr> \n ");		
		
			//写标题
			
			tableHtml.append(a_tableHtml.toString());
			
			ArrayList perPointList=(ArrayList)pointList.get(1);
			SingleGradeBo singleGradeBo=new SingleGradeBo(this.conn);
			HashMap   pointItemMap=singleGradeBo.getPointItemList((ArrayList)pointList.get(1),items);	
			
			// 画表头的中间层
			tableHtml.append(getMidHeadHtml(lays,tempColumnList,items,map,perPointList,pointItemMap));						
			// 画指标列 
			
			StringBuffer sequence=new StringBuffer("");			
			tableHtml.append("<tr>");
			sequence.append("<tr><th id='a' class='TableRow'  valign='middle' align='center' width='50' nowrap  >序号</th>");
			HashMap   perPointScore=getPerPointScore(templateID);   //得到各指标的分值范围		
			ArrayList a_perPointList=new ArrayList();		        //指标项（按顺序显示 包括空指标项）	
			for(int i=0;i<bottomItemList.size();i++)
			{
				String[] a_term=(String[])bottomItemList.get(i);				
				int is_thing=0;
				for(Iterator t=perPointList.iterator();t.hasNext();)
				{
					String[] aa=(String[])t.next();
					if(aa[3].equals(a_term[0]))
					{
						aa[4]="1";
						a_perPointList.add(aa);
						is_thing=1;
					}
				}
				if(is_thing==0)
				{
						String[] aa=new String[5];
						aa[4]="0";
						a_perPointList.add(aa);
				}
			}
			
			int sequenceNum=0;
			for(Iterator t=perPointList.iterator();t.hasNext();)
			{
				sequenceNum++;
				String[] temp=(String[])t.next();
				
				//sequence.append("<th valign='top' align='center'  class='RecordRow_h' width='"+columnWidth+"' height='150'   ><font color='0066cc'>");
				tableHtml.append("<th valign='top' align='center' id='"+temp[0]+"' ");
				
				tableHtml.append(" class='TableRow' width='"+columnWidth+"' height='150'   >");
				tableHtml.append(temp[1]);
//				if(temp[5]==null||temp[5].equals("1")||temp[5].equals("2"))
//					tableHtml.append("<font color='red'>*</font>");
				tableHtml.append("<br>");
			
				String[] temp2=(String[])perPointScore.get(temp[0]);	
				if("0".equals(temp2[7])&& "0".equals(temp2[8])) {
                    pointDeformity="1";
                }
				
				if("0".equals(status)|| "2".equals(scoreflag))
				{
					if("1".equals(temp[2]))   //定量指标
					{						
						isData=true;
						tableHtml.append("<font face=幼圆  style='font-weight:normal;font-size:8pt'>"+PubFunc.round(temp2[3],0)+"~"+PubFunc.round(temp2[2],0)+"</font>");
					}
					else					  //定性指标
					{
						tableHtml.append("<font face=幼圆  style='font-weight:normal;font-size:8pt'>分值:"+PubFunc.round(temp2[1],0)+"</font>");
					}
				}
				else
				{					
					isData=true;
					tableHtml.append("<font face="+ResourceFactory.getProperty("font_family.song")+" style='font-weight:normal;font-size:8pt'>"+ResourceFactory.getProperty("lable.performance.singleGrade.value")+":"+PubFunc.round(temp2[1],0)+"</font>");	
				}
		//		tableHtml.append("</td></tr></table>");		
			
				tableHtml.append("</th>");
				//sequence.append(sequenceNum+"</font></th>");
				
			}
			tableHtml.append("</tr> \n");			
			//sequence.append("<th class='RecordRow_h' ><font color='0066cc'>"+(++sequenceNum)+"</font></th></tr> \n");
			//tableHtml.append(sequence.toString());
			String title=" <tr height='80'  > <th style='background-image:url(/images/mainbg.jpg)'  valign='middle' align='center' height='50'  colspan='"+a_cols+"'   > <font face="+ResourceFactory.getProperty("font_family.song")+" style='font-weight:bold;font-size:15pt'>   "+titleName+" </font> </th> </tr> ";
			arraylist.add("<thead>"+tableHtml.toString()+"<thead>");
			arraylist.add(NodeKnowDegree);
			arraylist.add(WholeEval);
			arraylist.add(GradeClass);
			arraylist.add(String.valueOf(isKnowWhole));
			arraylist.add(String.valueOf(fineMax));
			arraylist.add(SummaryFlag);			
			if(isData) {
                arraylist.add("");
            } else {
                arraylist.add("");
            }
			arraylist.add(scoreflag);
			arraylist.add("");
			
			arraylist.add(a_perPointList);
			arraylist.add(pointDeformity);
			arraylist.add("false");
		}
		catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
		return arraylist;
	}
	
	

	/**
	 * 得到模版下各指标的分值及最大上限值和最小下限值
	 * @param templateID	模版id
	 * @return
	 */
	public HashMap getPerPointScore(String templateID)   throws GeneralException
	{
		HashMap    hashMap=new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet     rowSet=null;
		try
		{
			StringBuffer sql1=new StringBuffer("select po.point_id,pp.score,max(pg.top_value) top_value,min(pg.bottom_value) bottom_value ,min(pg.gradecode) min_gradecode,max(pg.gradecode) max_gradecode,po.pointkind ,tt.t,tt.b ");
			sql1.append("from per_template_item pi,per_template_point pp,per_point po ,per_grade pg ,(select a.point_id,a.top_value t ,a.bottom_value b from per_grade a where a.top_value=(select max(top_value) from per_grade b  where a.point_id=b.point_id)) tt ");
			sql1.append(" where pi.item_id=pp.item_id and pp.point_id=po.point_id and  po.point_id=pg.point_id and po.point_id=tt.point_id  and template_id='");
			sql1.append(templateID);
			sql1.append("' ");
			sql1.append(" group by po.point_id,po.pointkind,pp.score,tt.t,tt.b ");
			rowSet=dao.search(sql1.toString());
			while(rowSet.next())
			{
				String[] temp=new String[9];
				for(int i=0;i<9;i++)
				{
					temp[i]=rowSet.getString(i+1);
				}
				hashMap.put(temp[0],temp);
			}
			
			StringBuffer sql2=new StringBuffer("select po.point_id,po.pointname,po.pointkind,pi.item_id ");
						 sql2.append(" from per_template_item pi,per_template_point pp,per_point po ");
						 sql2.append(" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id='"+templateID+"'  order by pp.seq");
			int i=0;
			rowSet=dao.search(sql2.toString());
			while(rowSet.next()) {
                i++;
            }
			if(i!=hashMap.size())
			{
				HashMap tempMap=new HashMap();
				StringBuffer sql=new StringBuffer("select po.point_id,pp.score,max(pg.top_value) top_value,min(pg.bottom_value) bottom_value ,min(pg.gradecode) min_gradecode,max(pg.gradecode) max_gradecode,po.pointkind,0 t,0 b ");
				sql.append("from per_template_item pi,per_template_point pp,per_point po ,per_grade pg  ");
				sql.append(" where pi.item_id=pp.item_id and pp.point_id=po.point_id and  po.point_id=pg.point_id   and template_id='");
				sql.append(templateID);
				sql.append("' ");
				sql.append(" group by po.point_id,po.pointkind,pp.score");							
				
				RowSet rowSet2=dao.search(sql.toString());
				while(rowSet2.next())
				{
						String[] temp2=new String[9];
				
						temp2[0]=rowSet2.getString("point_id");
						temp2[1]=rowSet2.getString("score");
						temp2[2]=rowSet2.getString("top_value");
						temp2[3]=rowSet2.getString("bottom_value");
						temp2[4]=rowSet2.getString("min_gradecode");
						temp2[5]=rowSet2.getString("max_gradecode");
						temp2[6]=rowSet2.getString("pointkind");
						temp2[7]=rowSet2.getString("t");
						temp2[8]=rowSet2.getString("b");
						
						tempMap.put(temp2[0],temp2);
				}	
				
				return tempMap;
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
	/*	finally
		{
			try
			{
				if(rowSet!=null)
				{
					rowSet.close();
				}
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}*/
		return hashMap;
	}
	
	

	//生成表头中间层html
	public String getMidHeadHtml(int lays,ArrayList tempColumnList,ArrayList items,HashMap map,ArrayList perPointList,HashMap pointItemMap)
	{
		StringBuffer tableHtml=new StringBuffer("");
		for(int b=2;b<lays;b++)
		{
			ArrayList tempList=new ArrayList();
			tableHtml.append("<tr>");
			int d=0;
			for(int i=0;i<tempColumnList.size();i++)
			{
				String[] temp1=(String[])tempColumnList.get(i);
				if(temp1[0]==null)
				{
					tableHtml.append("<th valign='middle' align='center' class='RecordRow_h' colspan='1' ");
					tableHtml.append(" width='"+columnWidth+"' height='50'   >");
					tableHtml.append("&nbsp;");
					tableHtml.append("</th>");
					
					tempList.add(temp1);
					d++;
				}
				else
				{
					int pointNum=Integer.parseInt((String)map.get(temp1[0]));
					int isNullItem=0;
					for(Iterator t1=items.iterator();t1.hasNext();)
					{
						String[] temp2=(String[])t1.next();					
						if(temp2[1]!=null&&temp2[1].equals(temp1[0]))
						{
							int pointNum2=Integer.parseInt((String)map.get(temp2[0]));
							int selfnum=0;
							isNullItem++;		
							while(d<perPointList.size())
							{
								String[] point=(String[])perPointList.get(d);
								ArrayList pointItemList=(ArrayList)pointItemMap.get(point[0]);
								int flag=0;
								for(Iterator t2=pointItemList.iterator();t2.hasNext();)
								{
									String[] tempItem=(String[])t2.next();
									if(tempItem[0].equals(temp2[0])) {
                                        flag++;
                                    }
								}
								
								if(flag==0)
								{
									tableHtml.append("<th valign='middle' align='center' class='RecordRow_h' colspan='1' ");
									tableHtml.append(" width='"+columnWidth+"' height='50'   >");
									tableHtml.append("&nbsp;");
									tableHtml.append("</th>");
									
									String[] ttt=new String[5];
									tempList.add(ttt);
									d++;
									selfnum++;
								}
								else
								{
									tableHtml.append("<th valign='middle' align='center' class='RecordRow_h' colspan='"+(String)map.get(temp2[0])+"' ");
									tableHtml.append(" width='"+(columnWidth*Integer.parseInt((String)map.get(temp2[0])))+"' height='50'   >");
									tableHtml.append(temp2[3]);
									tableHtml.append("</th>");	
									
									d+=pointNum2;
									selfnum+=pointNum2;
									tempList.add(temp2);
									break;
								}
							}

						}
					}
					if(isNullItem==0)
					{
						for(int a=0;a<pointNum;a++)
						{
							tableHtml.append("<th valign='middle' align='center' class='RecordRow_h' colspan='1' ");
							tableHtml.append(" width='"+columnWidth+"' height='50'   >");
							tableHtml.append("&nbsp;");
							tableHtml.append("</th>");
							
							String[] ttt=new String[5];
							tempList.add(ttt);
							d++;
						}
						
						
					}
					

				}
			}
			
			tableHtml.append("</tr>");
			tempColumnList=tempList;
		}
				
		return tableHtml.toString();
	}
	
	
	
	

	
	/**
	 * 模版项目列表 && 最底层项目的指标个数集合 && 表头的层数 && HashMap各项目包含的指标个数&&最底层的项目
	 * @param templateID
	 * @return
	 */
	public ArrayList getPerformanceStencilList(String templateID)  throws GeneralException
	{
		ArrayList  list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet     rowSet=null;
		int        lays=0;  //表头的层数
		HashMap        map=new HashMap();
		try
		{
		
			String item_id="0";
			String sql="";
			ArrayList bottomItemList=new ArrayList();
			/* 按循序得到模版项目列表 */
			ArrayList items=getItems(templateID);			
			/* 取得表头的层数 */
			getLays(items);
			lays=this.a_lays;
			lays++;	
			lays++;	
			list.add(items);			
			/* 得到各最底层项目的指标个数集合 */
			sql="select pp.item_id,count(pp.item_id) count  from  per_template_item pi,per_template_point pp where pi.item_id=pp.item_id and pi.template_id='"+templateID+"' group by pp.item_id ";
			rowSet=dao.search(sql);
			HashMap itemsCountMap=new HashMap();
			while(rowSet.next())
			{
				itemsCountMap.put(rowSet.getString("item_id"),rowSet.getString("count"));	
			}
			
			/* 求得map值 */
			for(Iterator t=items.iterator();t.hasNext();)
			{
				int count=0;
				String[] temp=(String[])t.next();
				this.leafNodes="";
				getleafCounts(temp,items,itemsCountMap);	
				if(this.leafNodes.substring(1).equals(temp[0])) {
                    bottomItemList.add(temp);
                }
				this.leafNodes+="/";
				
				
				String[] a=this.leafNodes.substring(1).split("/");

				for(int i=0;i<a.length;i++)
				{
					if(itemsCountMap.get(a[i])!=null) {
                        count+=Integer.parseInt((String)itemsCountMap.get(a[i]));
                    }
				//	else
				//		count++;
				}	
				if(!a[0].equals(temp[0])&&itemsCountMap.get(temp[0])!=null)
				{
					count+=Integer.parseInt((String)itemsCountMap.get(temp[0]));	
				}	
				map.put(temp[0],String.valueOf(count));
			}
			
			list.add(itemsCountMap);
			list.add(new Integer(lays));
			list.add(map);
			list.add(bottomItemList);
			
			for(int i=0;i<bottomItemList.size();i++)
			{
				String[] tt=(String[])bottomItemList.get(i);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	

	
	/**
	 * 求得某节点的所有叶子节点的id串
	 * @param node
	 * @param items
	 */
	String leafNodes="";	
	public void getleafCounts(String[] node,ArrayList items,HashMap itemsCountMap)
	{
		int i=0;
		for(Iterator t=items.iterator();t.hasNext();)
		{
			String[] temp=(String[])t.next();
			if(node[0].equals(temp[1]))
			{
				i++;
			}
		}		
		if(i==0) {
            leafNodes+="/"+node[0];
        } else
		{
			for(Iterator t=items.iterator();t.hasNext();)
			{
				String[] temp=(String[])t.next();
				if(node[0].equals(temp[1]))
				{
					if(itemsCountMap.get(temp[0])!=null&&leafNodes.indexOf("/"+node[0])==-1) {
                        leafNodes+="/"+node[0];
                    }
					getleafCounts(temp,items,itemsCountMap);	//递归
					
				}
			}
		}		
	}
	
	
	
	/**
	 * 按顺序显示表项
	 */
	public ArrayList getItems(String template_id)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet     rowSet=null;
		try
		{
			String sql="select * from per_template_item where template_id='"+template_id+"' order by seq";			
			rowSet=dao.search(sql);
			ArrayList items=new ArrayList();
			ArrayList bottomItemList=new ArrayList();
			ArrayList parentList=new ArrayList();
			String item_id="0";
			while(rowSet.next())
			{
				String[] temp=new String[5];
				temp[0]=rowSet.getString("item_id");
				temp[1]=rowSet.getString("parent_id");
				temp[2]=rowSet.getString("child_id");
				temp[3]=rowSet.getString("itemdesc");
				temp[4]=rowSet.getString("seq");
				items.add(temp);								
				if(temp[1]==null) {
                    parentList.add(temp);
                }
			}
			String node=null;
			for(int i=0;i<parentList.size();i++)
			{
				String[] temp=(String[])parentList.get(i);
				list.add(temp);
				searchIterms(items,list,temp[0]);
			
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 取得表头的层数
	 */
	int a_lays=0;
	public void getLays(ArrayList items)
	{
		
		for(Iterator t=items.iterator();t.hasNext();)
		{
			String[] item=(String[])t.next();
			if(item[1]==null)
			{	
				int lay=CountLevel(item,items) ;
				if( a_lays<lay)
				{
					a_lays=lay;
				}
			}			
		}
	}
	
	int CountLevel(String[] MyNode,ArrayList list) 
	 { 
	  if (MyNode == null) {
          return -1;
      }
	  int iLevel = 1; int iMaxLevel = 0; 
	  ArrayList subNodeList=new ArrayList();
	  for (int i=0; i<list.size(); i++) 
	  { 
	   String[] temp=(String[])list.get(i);
	   if(temp[1]!=null&&temp[1].equals(MyNode[0])) {
           subNodeList.add(temp);
       }
	  }
	  
	  for (int i=0; i<subNodeList.size(); i++) 
	  { 
	   iLevel = CountLevel((String[])subNodeList.get(i),list)+1; 
	   if (iMaxLevel < iLevel) {
           iMaxLevel = iLevel;
       }
	  } 
	  return iMaxLevel; 
	 } 

	
	


	/**
	 * 返回  某绩效模版的所有指标集
	 * @param templateID
	 * @return
	 */
	public ArrayList getPerPointList(String templateID)  throws GeneralException
	{
		ArrayList  list=new ArrayList();		
		ArrayList  pointGrageList=new ArrayList();
		ArrayList  a_pointGrageList=new ArrayList();
		ArrayList  pointList=new ArrayList();	
		ContentDAO dao=new ContentDAO(this.conn);
		String    isNull="0";                    //判断模版中指标标度上下限值是否设置
		RowSet     rowSet=null;
		
		PersonPostModalBo ppo = new PersonPostModalBo(this.conn);
		String per_comTable = "per_grade_template"; // 绩效标准标度
		if(ppo.getComOrPer(templateID,"temp")) {
            per_comTable = "per_grade_competence"; // 能力素质标准标度
        }
		HashMap   map2=new HashMap();
		String     sql="select pp.item_id,po.point_id,po.pointname,po.pointkind,pgt.gradedesc,pg.gradecode,pg.top_value,pg.bottom_value,pp.score,pg.gradevalue,po.fielditem  from per_template_item pi,per_template_point pp,per_point po ,per_grade pg,"+per_comTable+" pgt "
						+" where pi.item_id=pp.item_id and pp.point_id=po.point_id and  po.point_id=pg.point_id and pg.gradecode=pgt.grade_template_id and template_id='"+templateID+"'  order by pp.seq";  //pi.seq,
		try
		{
			rowSet=dao.search(sql);
			while(rowSet.next())
			{
				String[] temp=new String[11];
				for(int i=0;i<11;i++)
				{
					if(i==2) {
                        temp[i]=Sql_switcher.readMemo(rowSet,"pointname");
                    } else if(i==4) {
                        temp[i]=Sql_switcher.readMemo(rowSet,"gradedesc");
                    } else {
                        temp[i]=rowSet.getString(i+1);
                    }
					if(i==6||i==7)
					{
						if(temp[i]==null)
						{
							isNull="1";
						}
					}
					
				}
				a_pointGrageList.add(temp);
			}			
			rowSet=dao.search("select po.point_id,po.pointname,po.pointkind,pi.item_id,po.visible,po.fielditem from per_template_item pi,per_template_point pp,per_point po "
					+" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id='"+templateID+"'  order by pp.seq");	  //pi.seq,
			while(rowSet.next())
			{
				String[] temp=new String[7];
				temp[0]=rowSet.getString(1);
				temp[1]=Sql_switcher.readMemo(rowSet,"pointname");
				temp[2]=rowSet.getString(3);
				temp[3]=rowSet.getString(4);
				temp[4]="";
				temp[5]=rowSet.getString("visible");
				temp[6]=rowSet.getString("fielditem");
			//	pointList.add(temp);
				map2.put(temp[0].toLowerCase(),temp);
			}
			
			
			//解决排列顺序问题
			ArrayList seqList=new ArrayList();
			ParameterSetBo parameterSetBo=new ParameterSetBo(this.conn);
			ArrayList apointList=new ArrayList();
			ArrayList layItemList=new ArrayList();
			HashMap itemPoint=new HashMap();
			//分析绩效考核模版
			parameterSetBo.anaylseTemplateTable(apointList,layItemList,itemPoint,templateID,"");
			for(int i=0;i<apointList.size();i++)
			{
				seqList.add(((String)apointList.get(i)).toLowerCase());
			}
			
			for(Iterator t=seqList.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				pointList.add((String[])map2.get(temp));
			}
			
			for(Iterator t=seqList.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				for(Iterator t1=a_pointGrageList.iterator();t1.hasNext();)
				{
					String[] tt=(String[])t1.next();
					if(tt[1].toLowerCase().equals(temp)) {
                        pointGrageList.add(tt);
                    }
				}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	
		list.add(pointGrageList);
		list.add(pointList);
		list.add(isNull);
		
		return list;
	}
	
	
	
	

	public void searchIterms(ArrayList items,ArrayList list,String node)
	{
			for(Iterator t=items.iterator();t.hasNext();)
			{
				String[] temp1=(String[])t.next();		
				if(temp1[1]!=null&&temp1[1].equals(node))
				{					
					list.add(temp1);				

					searchIterms(items,list,temp1[0]);
				}
			}		
	}
	
}
