package com.hjsj.hrms.businessobject.performance.singleGrade;

import com.hjsj.hrms.businessobject.competencymodal.personPostModal.PersonPostModalBo;
import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.*;

public class SingGradeTemplateBo {
	private int flag;         //培训=20 绩效=33 招聘=32
	private Connection conn=null;
	private UserView userView;
	private int td_width=100;
	private int td_height=60;
	private ArrayList pointList=new ArrayList();
	private TableOperateBo tableOperateBo=null;
	private String r3101="";  //活动编号 （针对培训模块）
	private  int    contentment=0; //满意度个数
	
	public SingGradeTemplateBo(Connection con,int flag)
	{
		this.conn=con;
		this.flag=flag;
		tableOperateBo=new TableOperateBo(this.conn);
	} 
	
	
	
	/**
	 * 得到考核页面(招聘管理考核页面)
	 * @param template_id
	 * @param status  //权重分值表识 0：分值 1：权重
	 * @param userID
	 * @return
	 */
	public ArrayList getSingleGradeHtml(String template_id,String status,String userID,String object_id,String titleName) throws GeneralException
	{	
		ArrayList list_temp=new ArrayList();
		try
		{
			
			ArrayList list=getPerformanceStencilList(template_id);
			ArrayList items=(ArrayList)list.get(0);				//模版项目列表(按顺序)
			int        lay=((Integer)list.get(1)).intValue();	//表头的层数
			HashMap    map=(HashMap)list.get(2);				//各项目包含的指标个数
			HashMap    subItemMap=(HashMap)list.get(3);			//各项目的子项目(hashmap)		
			pointList=getPerPointList(template_id);			
			HashMap   pointItemMap=getPointItemList((ArrayList)pointList.get(1),items);			
			list_temp=getGradeHtml(userID,status,template_id,lay,map,subItemMap,items,pointItemMap,object_id,titleName);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return list_temp;
	}
	
	
	
	
	

	public ArrayList getGradeHtml(String userID,String status,String template_id,int lay,HashMap map,HashMap subItemMap,ArrayList items,HashMap pointItemMap,String object_id,String titleName) throws GeneralException
	{
		
		ArrayList list=new ArrayList();
	     StringBuffer html=new StringBuffer("<table   class='ListTable_self' >");
	     StringBuffer personalComment=new StringBuffer(" ");
	     String       isNull="0";
	     String		  scoreflag="";
	     String       NodeKnowDegree="";
	     String 	  WholeEval="";
	     String       limitation="";
	     String 	  GradeClass="";
	     StringBuffer dataArea=new StringBuffer("");
	     RowSet rs = null;
		 try
		 {
			 Hashtable htxml=new Hashtable(); 
			 String ScaleToDegreeRule="3";   //分值转标度规则（1-就高 2-就低 3-就近就高（默认值）） 
			 NodeKnowDegree="false";	    //了解程度
			 WholeEval="false";			    //总体评价	
			 limitation="-1";                  //=-1不转换,模板中最高标度的数目 (大于0小于1为百分比，大于1为绝对数)		
			 scoreflag="1";					//=2混合，=1标度
			 ArrayList pointInfoList=(ArrayList)pointList.get(0);           //指标详细集
			 ArrayList a_pointList=(ArrayList)pointList.get(1);            //指标集 
			 isNull=(String)pointList.get(2);				                //指标范围是否为空 0：不为空  1：为空
			 HashMap itemsSignMap=getItemsSignMap(items);		
			
			 /* 得到某计划考核主体给对象的评分结果hashMap */
			 HashMap objectResultMap=new HashMap();
			 HashMap perTableMap=new HashMap();
			 String  a_status="0";  // 0:未提交 1已提交
			 if(flag==32)
			 {
				 perTableMap=getPerTableXXX(userID,object_id);
				 if(perTableMap.get(object_id)!=null)
						objectResultMap=(HashMap)perTableMap.get(object_id);
			 }
			 else if(flag==20)     //培训=20 绩效=33 招聘=32
			 {
				 //存在评估结果表
				 createEvaluatingResultTable(template_id);
				 
				// 根据object_id向TRA_EVAL_XXX中取分值 lium
				String sql = "SELECT * FROM TRA_EVAL_" + template_id + " WHERE r3101=? AND A0100=? AND NBASE=?";
				rs = new ContentDAO(conn).search(sql, Arrays.asList(new Object[] {
						r3101, object_id, userView.getDbname()
				}));

				// 每一个指标得分对应的标度(ABCDE)
				if (rs.next()) {
					a_status = "1"; // 0：未提交，1：已提交
					 
					for (Iterator i = a_pointList.iterator(); i.hasNext();) {
						String[] t = (String[]) i.next();
						String pid = t[0]; // point_id,指标id
						double score = rs.getDouble("C_" + pid);
						 
						for (Iterator _i = pointInfoList.iterator(); _i.hasNext();) {
							String[] _t = (String[]) _i.next(); // 指标，包括上下限等
							String _pid = _t[1]; // point_id,指标id
							if (!pid.equals(_pid)) {
								continue;
							}

							// tmp索引参照this.getPerTableXXX(String, String)方法
							String[] tmp = new String[7];
							if ("0".equals(t[2])) { // 定性指标
							// 将当前标度总分和比例相乘，与实际得分进行匹配
								double s = Double.parseDouble(_t[8]) * Double.parseDouble(_t[9]);
								if (score == s) {
									tmp[6] = _t[5];
									objectResultMap.put(pid, tmp);
									break;
								}
							} else { // 定量指标
								tmp[6] = score + "";
								objectResultMap.put(pid, tmp);
								break;
							}
						}
					}
				}
			 }
			 
			 			 

			 HashMap   a_pointmap=new HashMap();		   					  //得到具有某考核对象的指标权限map(目前设为全有)			 
			 for(int i=0;i<a_pointList.size();i++)
			 {
				 String[] temp=(String[])a_pointList.get(i);
				 a_pointmap.put(temp[0],"1");
			 }
			 
			 HashMap perPointScoreMap=getPerPointScore(template_id);			 //得到模版下各指标的分值及最大上限值和最小下限值
			 String[] temp={object_id," ",a_status};
			 
			 //加标题
			 html.append("<tr><td class=\"TableRow\"  valign='middle' align='center' height='50'   colspan='"+(lay+1)+"' ><font face=宋体 style='font-weight:bold;font-size:15pt'> "+titleName+" </font> </td></tr>");
			 
			 for(Iterator t=a_pointList.iterator();t.hasNext();)
			 {
				 String[] point=(String[])t.next();
				 String[] pointScore=(String[])perPointScoreMap.get(point[0]);
				 
				 ///////////////////各指标的数值范围///////////////////////
				 
			
				 if(point[6]!=null&&point[6].trim().length()>0)
				 {
					 	ContentDAO dao=new ContentDAO(this.conn);
						String a_value="";
						try
						{
							String fieldSetID="";
							RowSet rowSet=dao.search("select * from fielditem where itemid='"+point[6]+"'");
							if(rowSet.next())
							{
								fieldSetID=rowSet.getString("fieldsetid");
							}
							String a_sql="";
							if("A01".equalsIgnoreCase(fieldSetID))
							{
								a_sql="select "+point[6]+" from usr"+fieldSetID+"  where a0100='"+object_id+"'";
							}
							else
							{
								a_sql="select "+point[6]+" from usr"+fieldSetID+" a where a.i9999 =(select max(I9999) from usr"+fieldSetID+" b where a0100='"+object_id+"' and a.a0100=b.a0100)";
							}
							rowSet=dao.search(a_sql);
							if(rowSet.next())
							{
								a_value=rowSet.getString(1);
							}
							if(a_value!=null&&!"".equals(a_value))
							{
							
							java.util.regex.Pattern p=java.util.regex.Pattern.compile("^\\d+$|^\\d+\\.\\d+$");
						    java.util.regex.Matcher m=p.matcher(a_value);   	
							if(m.matches())
							{
								PersonPostModalBo ppo = new PersonPostModalBo(this.conn);
								String per_comTable = "per_grade_template"; // 绩效标准标度
								if(ppo.getComOrPer(template_id,"temp"))
									per_comTable = "per_grade_competence"; // 能力素质标准标度								
				    			rowSet=dao.search("select pp.item_id,po.point_id,po.pointname,pg.gradecode,pg.top_value,pg.bottom_value,pp.score,pg.gradevalue ,po.fielditem"
				    							+" from per_template_item pi,per_template_point pp,per_point po ,per_grade pg,"+per_comTable+" pgt "
				    							+" where pi.item_id=pp.item_id and pp.point_id=po.point_id and  po.point_id=pg.point_id and pg.gradecode=pgt.grade_template_id and template_id='"+template_id+"' "
				    							+" and pp.point_id='"+pointScore[0]+"'  order by pp.seq"	);	
				    			
				    			while(rowSet.next())
				    			{
				    				if("1".equals(pointScore[6])) //定量
				    				{
				    					if(Float.parseFloat(a_value)>=rowSet.getFloat("bottom_value"))
				    					{
				    						pointScore[2]=rowSet.getString("top_value");
				    						pointScore[4]=rowSet.getString("gradeCode");
				    						break;
				    					}
				    				}
				    				else						 //定性
				    				{
				    				
				    					if(Float.parseFloat(a_value)>=rowSet.getFloat("bottom_value")*rowSet.getFloat("score"))
				    					{
				    						pointScore[2]=rowSet.getString("top_value");
				    						pointScore[4]=rowSet.getString("gradeCode");
				    						break;
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
				 
				 
				 
				 
						if("1".equals(point[2]))   //定量指标
						{
											
							dataArea.append("/");
							dataArea.append(PubFunc.round(pointScore[3],0));
							dataArea.append("*");
							dataArea.append(PubFunc.round(pointScore[2],0));
							dataArea.append("#");
							dataArea.append(pointScore[4]);
							dataArea.append("*");
							dataArea.append(pointScore[5]);	
						}
						else					  //定性指标
						{
							
							dataArea.append("/");
							if(pointScore[3]!=null&&pointScore[2]!=null)
							{
								dataArea.append(String.valueOf(Float.parseFloat(pointScore[1])*Float.parseFloat(pointScore[3])));
								dataArea.append("*");
								dataArea.append(String.valueOf(Float.parseFloat(pointScore[1])*Float.parseFloat(pointScore[2])));
							}
							else
							{
								dataArea.append("0");
								dataArea.append("*");
								dataArea.append(pointScore[1]);
							}
							dataArea.append("#");
							dataArea.append(pointScore[4]);
							dataArea.append("*");
							dataArea.append(pointScore[5]);
						}
						
						dataArea.append("#");
						dataArea.append(String.valueOf(Float.parseFloat(pointScore[1])*Float.parseFloat(pointScore[7])));
						dataArea.append("*");
						dataArea.append(String.valueOf(Float.parseFloat(pointScore[1])*Float.parseFloat(pointScore[8])));
						

				 //////////////////////////////////////////////////
				 
				 ArrayList pointItemList=(ArrayList)pointItemMap.get(point[0]);
				 int a_lay=lay;
				 html.append("<tr>");
				 
				 int pointItemLength=pointItemList.size(); 
				 String[] currentItem=null;
				 for(int i=0;i<a_lay;i++)
				 {
					 if(i==0)
					 {
						 String[] item=(String[])pointItemList.get(--pointItemLength);
						 String sign=(String)itemsSignMap.get(item[0]);
						 if(!"1".equals(sign))
						 {				
						
							 html.append("<td class='RecordRow'  valign='middle' align='center'  rowspan='"+(String)map.get(item[0])+"'  width='"+td_width+"' height='"+td_height+"'  >");
							 html.append(item[3]);
							 html.append("</td>");
							 currentItem=item;
							 itemsSignMap.put(item[0],"1");
						 }
					 }
					 else if(i!=0&&i!=a_lay-1)
					 {
						 if(--pointItemLength>=0)
						 {
						 
							 String[] item=(String[])pointItemList.get(pointItemLength);
							 String sign=(String)itemsSignMap.get(item[0]);
							 if(!"1".equals(sign))
							 {							
								 html.append("<td class='RecordRow'  valign='middle' align='center'  rowspan='"+(String)map.get(item[0])+"'  width='"+td_width+"' height='"+td_height+"'  >");
								 html.append(item[3]);
								 html.append("</td>");								
								 currentItem=item;
								 itemsSignMap.put(item[0],"1");						
							 }
						 }
						 else
						 {
							 html.append("<td class='RecordRow'  valign='middle' align='center'  height='"+td_height+"'    width='"+td_width+"'  >&nbsp;</td>");
						 }					 
					 }
					 else
					 {
						 html.append("<td class='RecordRow' id='"+point[0]+"'  valign='middle' align='left'  height='"+td_height+"'  ");
					     if(point[5]==null|| "1".equals(point[5])|| "2".equals(point[5]))
					    	 html.append(" onmouseover='showDateSelectBox(this);'  onmouseout='Element.hide(\"date_panel\");'  ");
						 html.append(" >");	
						 html.append(point[1]);
						 if(point[5]==null|| "1".equals(point[5])|| "2".equals(point[5]))
								html.append("<font color='red'>*</font>");
						 html.append("&nbsp;&nbsp;&nbsp;"); 
						 html.append("</td>");
					 }
				 }
				 ArrayList a_list=getParticularPointList(pointInfoList,point);
				 html.append(getPointTD2((ArrayList)a_list.get(0),(String)a_list.get(1),temp,objectResultMap,status,a_pointmap,point[0],scoreflag,pointScore,point));
				 html.append("</tr>  \n ");
			 }
				/* 是否有了解程度 */
				String select_id=" ";			
			 html.append("</table>");
			 html.append("<br><span id=\"buttons\">");
			 
			 if ("0".equals(a_status)) { // 没有提交
				 html.append("<input type=\"button\" id=\"idSubmit\" name=\"b_save\" value=\""+ResourceFactory.getProperty("button.submit")+"\" onclick=\"check(1)\" class=\"mybutton\">");
			 }
			 html.append("&nbsp;<input type=\"button\" name=\"b_save\" value=\""+ResourceFactory.getProperty("train.evaluatingStencil.lookResult")+"\" onclick=\"lookResult('"+this.r3101+"','"+template_id+"')\" class=\"mybutton\"> ");
			 html.append("&nbsp;<input type=\"button\" name=\"b_refer\" value=\""+ResourceFactory.getProperty("button.return")+"\" onclick=\"goBack()\" class=\"mybutton\">");
			 html.append("</span>");
			
		 }
		 catch(Exception e)
		 {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
		 } finally {
			 PubFunc.closeDbObj(rs);
		 }
		
		 list.add(html.toString());
		 list.add(personalComment.toString());
		 list.add(isNull);
		 list.add(scoreflag);
		 list.add(dataArea.toString());
		 list.add(NodeKnowDegree);
		 list.add(WholeEval);
		 list.add(limitation);
		 list.add(GradeClass);
		 list.add(String.valueOf(lay));
		 return list;
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
    public	String getPointTD2(ArrayList tempList,String point_kind,String[] temp,HashMap objectResultMap,String status,HashMap pointMap,String pointID,String scoreflag,String[] pointScore,String[] point) throws GeneralException
    {
    	StringBuffer   td=new StringBuffer("");

    	td.append("<td class='RecordRow' align='left'  ");   	
    	td.append(" nowrap >");
    	td.append("<table border='0'><tr><td width='150' align='center' nowrap >");   
    	
    	String[] pointGrade=(String[])tempList.get(0);
    	String a_fieldItem=pointGrade[10];
    	String a_fieldItem_1=pointGrade[11];
    	
    	
    	if("1".equals(scoreflag)&& "0".equals(point_kind))
    	{
    		
	    		td.append("<select name='a");
	    		td.append(temp[0]);
	    		td.append("'");   		
	    		/*if(temp[2].equals("2"))
	    			td.append(" disabled='false'");*/
	    		td.append(" ><option value=''></option>");
	    		for(Iterator t=tempList.iterator();t.hasNext();)
	    		{
	    			String[] a_temp=(String[])t.next();
	    			td.append("<option value='");
	    			td.append(a_temp[5]);
	    			td.append("' ");   			
	    			if("1".equals(temp[2])|| "2".equals(temp[2])|| "3".equals(temp[2]))
	    			{
	    				String[] values=(String[])objectResultMap.get(a_temp[1]);
	    				if(values!=null)
	    				{
		    				if(values[6]!=null&&values[6].equals(a_temp[5]))
		    					td.append("selected");
	    				}
	    			}  			
	    			td.append(" >&nbsp;&nbsp;");
	    			td.append(a_temp[4]);
	    			td.append("&nbsp;&nbsp;</option>");   			
	    		}
	    		td.append("</select>");  	
    		
    	}
    	else
    	{
 
    		String[]  a_temp=(String[])tempList.get(0);
    		td.append(" <input  style='width: 65.0px' "); 
    		
    		{
    			
    				td.append(" type='text' ");
		    		if("1".equals(temp[2])|| "2".equals(temp[2])|| "3".equals(temp[2]))
					{
		    			String[] values=(String[])objectResultMap.get(a_temp[1]);
		    			
		    			if (flag == 20) {
		    				if (values != null) {
		    					// 定量指标在保存时没有考虑模版是权重还是分值模板，所以回显的时候也不作考虑 lium
		    					td.append(" value='").append(values[6]).append("'");
		    				}
		    			} else if(values!=null&&(values[4]!=null||values[3]!=null))
		    			{			
			    			td.append(" value='");
			    			if("1".equals(status))
			    				td.append(values[3]);
			    			else
			    			{
			    				if(values[4]!=null)
			    					td.append(values[4]);
			    				else
			    					td.append(values[3]);
			    			}
			    			
			    			td.append("'");
		    			}
					} 
    		}
    		td.append(" name='a"+temp[0]+"'    id='"+point[0]+"'      />");		
    		
    
    	}
    	td.append("</td><td width='40%' >");
    
			if("1".equals(point[2]))   //定量指标
			{
				
				td.append(" ( "+PubFunc.round(pointScore[3],0)+"~"+PubFunc.round(pointScore[2],0)+" ) ");
			}
			else					  //定性指标
			{
				if("2".equals(scoreflag))
					td.append(" ( "+ResourceFactory.getProperty("lable.performance.singleGrade.value")+":"+PubFunc.round(pointScore[1],0)+" ) ");			
			}
		
	
    	td.append("</td></tr></table>    </td>");
    	return td.toString();
    }
	
	
	
	
	
	
	public ArrayList getParticularPointList(ArrayList pointList,String[] point)
	{
		ArrayList list=new ArrayList();
		ArrayList tempPointList=new ArrayList();
		String    point_kind=""; 
		for(Iterator t=pointList.iterator();t.hasNext();)
		{
			String[] temp=(String[])t.next();
			if(temp[1].equals(point[0]))
			{
				tempPointList.add(temp);	
				point_kind=temp[3];
			}
		}
		list.add(tempPointList);
		list.add(point_kind);
		return list;
		
	}
	

	
	
	
	
	
	

	/**
	 * 得到某计划考核主体给对象的评分结果hashMap(针对招聘模块)
	 * @param plan_id       考核计划id
	 * @param mainbodyID	考核主体id
	 * @param object_id		考核对象列表
	 * @return	HashMap
	 */
	public HashMap getPerTableXXX(String mainbodyID,String object_id)   throws GeneralException
	{
		HashMap hashMap=new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet     rowSet=null;
		try
		{
			
					String objectid=object_id;
					HashMap map=new HashMap();
					String  sql="select * from zp_test_template  where a0100_1='"+mainbodyID+"' and a0100='"+objectid+"' ";
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
				
					hashMap.put(objectid,map);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	
		return hashMap;
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
			while(rowSet.next())
				i++;
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
	
		return hashMap;
	}
	
	
	
	
	
	
	
	
	
	
//	初始化 表项显示纪录 map
	public HashMap getItemsSignMap(ArrayList items)
	{
		HashMap itemSignMap=new HashMap();
		for(Iterator t=items.iterator();t.hasNext();)
		{
			String[] temp=(String[])t.next();
			itemSignMap.put(temp[0],"0");
		}
		return itemSignMap;
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
		if(ppo.getComOrPer(templateID,"temp"))
			per_comTable = "per_grade_competence"; // 能力素质标准标度
		HashMap   map2=new HashMap();
		String     sql="select pp.item_id,po.point_id,po.pointname,po.pointkind,pgt.gradedesc,pg.gradecode,pg.top_value,pg.bottom_value,pp.score,pg.gradevalue,po.fielditem,po.l_fielditem,po.status  from per_template_item pi,per_template_point pp,per_point po ,per_grade pg,"+per_comTable+" pgt "
						+" where pi.item_id=pp.item_id and pp.point_id=po.point_id and  po.point_id=pg.point_id and pg.gradecode=pgt.grade_template_id and template_id='"+templateID+"'  order by pp.seq,pg.grade_id";  //pi.seq,
		try
		{
			rowSet=dao.search(sql);
			while(rowSet.next())
			{
				String[] temp=new String[13];
				for(int i=0;i<13;i++)
				{
					if(i==2)
						temp[i]=Sql_switcher.readMemo(rowSet,"pointname");
					else if(i==4)
						temp[i]=Sql_switcher.readMemo(rowSet,"gradedesc");
					else
						temp[i]=rowSet.getString(i+1);
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
			rowSet=dao.search("select po.point_id,po.pointname,po.pointkind,pi.item_id,po.visible,po.fielditem,po.status from per_template_item pi,per_template_point pp,per_point po "
					+" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id='"+templateID+"'  order by pp.seq");	  //pi.seq,
			while(rowSet.next())
			{
				String[] temp=new String[8];
				temp[0]=rowSet.getString(1);
				temp[1]=Sql_switcher.readMemo(rowSet,"pointname");
				temp[2]=rowSet.getString(3);
				temp[3]=rowSet.getString(4);
				temp[4]="";
				temp[5]=rowSet.getString("visible");
				temp[6]=rowSet.getString("fielditem");
				temp[7]=rowSet.getString("status");
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
					if(tt[1].toLowerCase().equals(temp))
						pointGrageList.add(tt);
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

	
	/**
	 * 模版项目列表(按顺序) && 各项目的子项目(hashmap) && 表头的层数 && HashMap各项目包含的指标个数
	 * @param templateID
	 * @return
	 */
	public ArrayList getPerformanceStencilList(String templateID)  throws GeneralException
	{
		ArrayList  list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet     rowSet=null;
		int        lays=0;                  //表头的层数
		HashMap    map=new HashMap();       //各项目包含的指标个数
		HashMap    subItemMap=new HashMap();//各项目的子项目
		try
		{

			String sql="";
			/* 按顺序得到模版项目列表 */
			ArrayList items=getItems(templateID);			
			/* 取得表头的层数 */
			lays=getLays(items);
			lays++;	            //包含指标层
			
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
				this.leafNodes+="/";
			
				String[] a=this.leafNodes.substring(1).split("/");

				for(int i=0;i<a.length;i++)
				{
					if(itemsCountMap.get(a[i])!=null)
						count+=Integer.parseInt((String)itemsCountMap.get(a[i]));				
				}	
				if(!a[0].equals(temp[0])&&itemsCountMap.get(temp[0])!=null)
				{
					count+=Integer.parseInt((String)itemsCountMap.get(temp[0]));	
				}	
				map.put(temp[0],String.valueOf(count));
			}
			
			//各项目的子项目(hashmap)
			for(Iterator t=items.iterator();t.hasNext();)
			{
				String[] temp=(String[])t.next();
				StringBuffer subItem_str=new StringBuffer("");
				for(Iterator t1=items.iterator();t1.hasNext();)
				{
					
					String[] te=(String[])t1.next();
					if(te[1]!=null&&te[1].equals(temp[0]))
					{
						subItem_str.append(te[0]);
						subItem_str.append("/");
					}
				}
				if(subItem_str.length()>1)
				{
	
					subItemMap.put(temp[0],subItem_str.toString());
				}
				else
					subItemMap.put(temp[0],"");
			}

			list.add(items);
			list.add(new Integer(lays));
			list.add(map);
			list.add(subItemMap);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	
		return list;
	}
	
	

	/**
	 * 得到指标对应的表项列表((按层次))
	 * @param pointList
	 * @param items
	 * @return
	 */
	public HashMap getPointItemList(ArrayList pointList,ArrayList items)
	{
		HashMap pointItemMap=new HashMap();	
		for(Iterator t=pointList.iterator();t.hasNext();)
		{
			String[] temp=(String[])t.next();			
			
			String   item_str=temp[3];
			ArrayList pointItemList=new ArrayList();
			getPointItemList(item_str,pointItemList,items);
			pointItemMap.put(temp[0],pointItemList);
		}
		return pointItemMap;
	}
	
	
	
	public void getPointItemList(String item_str,ArrayList pointItemList,ArrayList items)
	{
		
		for(Iterator t1=items.iterator();t1.hasNext();)
		{
			String[] item=(String[])t1.next();
			if(item[0].equals(item_str))
			{
				pointItemList.add(item);
				if(item[1]!=null)
					getPointItemList(item[1],pointItemList,items);
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
				if(temp[1]==null)
					parentList.add(temp);
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
	
	/**
	 * 取得表头的层数
	 */
	
	public int  getLays(ArrayList items)
	{
		int a_lays=0;
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
		return a_lays+1;
	}
	
	int CountLevel(String[] MyNode,ArrayList list) 
	 { 
	  if (MyNode == null) return -1; 
	  int iLevel = 1; int iMaxLevel = 0; 
	  ArrayList subNodeList=new ArrayList();
	  for (int i=0; i<list.size(); i++) 
	  { 
	   String[] temp=(String[])list.get(i);
	   if(temp[1]!=null&&temp[1].equals(MyNode[0]))
	    subNodeList.add(temp);   
	  }
	  
	  for (int i=0; i<subNodeList.size(); i++) 
	  { 
	   iLevel = CountLevel((String[])subNodeList.get(i),list)+1; 
	   if (iMaxLevel < iLevel) 
	   iMaxLevel = iLevel; 
	  } 
	  return iMaxLevel; 
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
		if(i==0)
			leafNodes+="/"+node[0];
		else
		{
			for(Iterator t=items.iterator();t.hasNext();)
			{
				String[] temp=(String[])t.next();
				if(node[0].equals(temp[1]))
				{
					if(itemsCountMap.get(temp[0])!=null&&leafNodes.indexOf("/"+node[0])==-1)
						leafNodes+="/"+node[0];
					getleafCounts(temp,items,itemsCountMap);	//递归
					
				}
			}
		}		
	}
	
	
	
	
	/**
	 * 动态评估结果表（TRA_EVAL_+”模板号”）
	 * @param testTemplateID 考核模版id
	 * @return
	 */
	public void createEvaluatingResultTable(String testTemplateID)
	{
		
		DbWizard dbWizard=new DbWizard(this.conn);
		try
		{
			String tableName="TRA_EVAL_"+testTemplateID;
			ArrayList fieldList=getFieldList();
		    tableOperateBo.create_update_Table(tableName,fieldList,true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList getFieldList()
	{
		ArrayList list=new ArrayList();
		Field aField0=tableOperateBo.getField(false,"NBASE",ResourceFactory.getProperty("train.plan.applicationPre"),"A",3,0);
		list.add(aField0);
		Field aField1=tableOperateBo.getField(false,"R3101",ResourceFactory.getProperty("label.zp_job.zp_job_id"),"A",10,0);
		list.add(aField1);
		Field aField2=tableOperateBo.getField(false,"A0100",ResourceFactory.getProperty("label.zp_job.studentNo"),"A",10,0);
		list.add(aField2);
		Field aField3=tableOperateBo.getField(false,"R3702",ResourceFactory.getProperty("label.zp_job.trainResourceType"),"A",2,0);
		list.add(aField3);
		Field aField4=tableOperateBo.getField(false,"R3705",ResourceFactory.getProperty("label.zp_resource.resource_id"),"A",10,0);
		list.add(aField4);
		
		ArrayList dynamicFieldList=getDynamicFieldList();
		for(Iterator t=dynamicFieldList.iterator();t.hasNext();)
		{
			list.add((Field)t.next());
		}
		Field aField5=tableOperateBo.getField(false,"status","评估标志","N",10,0);
		list.add(aField5);
		return list;
	}
	
	
	
	public ArrayList getDynamicFieldList()
	{
		ArrayList list=new ArrayList();
		ArrayList a_pointList=(ArrayList)pointList.get(1);             //指标集 
		for(Iterator t=a_pointList.iterator();t.hasNext();)
		{
			String[] temp=(String[])t.next();
			Field aField=tableOperateBo.getField(false,"C_"+temp[0],"C_"+temp[0],"N",15,4);
			list.add(aField);
		}
		return list;
	}


	
	
	/**
	 * 将评分结果插入数据库(TRA_EVAL__XXXX  招聘考评打分)
	 * @param userid		考核对象		
	 * @param template_id	考核计划相应模版id
	 * @param usersValue	各对象的评分结果
	 * @param status        权重分值标识
	 * @param dbname 
	 * @param r3101         培训活动id
	 * @return
	 */
	 public String insertGradeResult(String userid,String template_id,String usersValue,String status,String dbname,String r3101)
	 			    throws GeneralException
	 {
		 String isSuccess="1";							//保存结果  1：保存成功  0：保存失败  2：指标范围为空不予保存
		 ContentDAO dao=new ContentDAO(this.conn);
		 /*查找参数表*/
		 try
		 {
			 HashMap rankMap=getPointRank(template_id);			
			
			 String ScaleToDegreeRule="3";      //分值转标度规则（1-就高 2-就低 3-就近就高（默认值）） 				
			 ArrayList pointList0=getPerPointList(template_id);
			 ArrayList pointInfoList=(ArrayList)pointList0.get(0);         //指标详细集
			 ArrayList pointList=(ArrayList)pointList0.get(1);             //指标集 
			 String    isNull=(String)pointList0.get(2);				   //指标范围是否为空 0：不为空  1：为空
			
			 if ("0".equals(isNull)) {

			String[] user_value = usersValue.split("/");		
			RecordVo vo=new RecordVo("TRA_EVAL_"+template_id);
			vo.setString("nbase",dbname);
			vo.setString("r3101",r3101);
			vo.setString("a0100",userid);
			vo.setInt("status",0);

			for (int t = 0; t < pointList.size(); t++) {
				if(!"null".equalsIgnoreCase(user_value[t]))
				{
					String[] temp = (String[]) pointList.get(t);
					double score=getPointScore(userid,
							temp, user_value, t, pointInfoList,
							ScaleToDegreeRule, status);
					vo.setDouble("c_"+temp[0].toLowerCase(),score*Float.parseFloat((String)rankMap.get(temp[0])));
				}
			}

			dao.addValueObject(vo);

		}
			 if("1".equals(isNull))
				 isSuccess="2";
		 }
		 catch(Exception e)
		 {
			 isSuccess="0";
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);			 
		 }		 		 
		 return isSuccess;
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
	 
	 
	 /**
	  * 按条件生成评分结果表的纪录
	  */
	 public double getPointScore(String user_id, String[] temp, String[] user_result, int t,
		ArrayList pointInfoList, String ScaleToDegreeRule, String status)
		throws GeneralException 
	{
	double score=0;
	try {
		
		if ("0".equals(temp[2])) // 定性
		{
			
			String[] temp2 = null;
			if (!"null".equals(user_result[t])) {
			
				for (Iterator tt = pointInfoList.iterator(); tt.hasNext();) {
					String[] temp1 = (String[]) tt.next();
					if (temp[0].equals(temp1[1])
							&& user_result[t].equals(temp1[5])) {
						temp2 = temp1;
						break;
					}
				}
				score=Double.parseDouble(String
						.valueOf(Float.parseFloat(temp2[9])
								* Float.parseFloat(temp2[8])));
			}
		} else // 定量
		{
			if (!"null".equals(user_result[t])) {
				String[] temp2 = null;
				for (Iterator tt = pointInfoList.iterator(); tt.hasNext();) {
					String[] temp1 = (String[]) tt.next();
					if (temp[0].equals(temp1[1])) {
						float topValue = Float.parseFloat(temp1[6]);
						float bottomValue = Float.parseFloat(temp1[7]);
						if (Float.parseFloat(user_result[t]) >= Float
								.parseFloat(temp1[7])
								&& Float.parseFloat(user_result[t]) <= Float
										.parseFloat(temp1[6])) {
							temp2 = temp1;
							break;
						}
					}
				}
				if ((user_result[t].length() >= 1 && "0123456789."
						.indexOf(user_result[t].charAt(0)) != -1)) {
					
					String ascore = String.valueOf(Float
							.parseFloat(temp2[9])
							* Float.parseFloat(temp2[8]));
					score= Double.parseDouble(ascore);
				}
			}
		}

	} catch (Exception e) {
		e.printStackTrace();
		throw GeneralExceptionHandler
				.Handle(new GeneralException(
						ResourceFactory.getProperty("kq.wizard.target")
								+ "："
								+ temp[1]
								+ "  "
								+ ResourceFactory
										.getProperty("label.performance.doNotGetGrade")
								+ "!"));
	}

	return score;
}
 
	
	
	
	
	
	/*****************************************************/
	/**
	 * 取得某活动调查评估的回收问卷数
	 */
	 public int getQuestionCount(String r3101,String templateid)throws GeneralException 
	 {
		int count=0;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet=null;
		try
		{
			rowSet=dao.search("select count(a0100) from tra_eval_"+templateid+" where r3101='"+r3101+"'");
			if(rowSet.next())
			{
				count=rowSet.getInt(1);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new GeneralException(ResourceFactory.getProperty(ResourceFactory.getProperty("label.zp_resource.noEvaluationRecord")+"!"));
		}
		
		return count;
	 }
	 
	 
	 //得到培训班的人数
	 public int getTrainClassMenCount(String r3101)throws GeneralException 
	 {
		 int count=0;
		 ContentDAO dao = new ContentDAO(this.conn);
		 RowSet rowSet=null;
		 try
		 {
			 rowSet=dao.search("select count(*) from r40 where r4005='"+r3101+"' and r4013 not in('01','02','07','08')");
			 if(rowSet.next())
			 {
					count=rowSet.getInt(1);
			 }
		 }
		 catch(Exception e)
		 {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
		 }
		return count;
	 }
	 
	 
	 
	 public String getAnalyseHtml(String templateid,String titleName)throws GeneralException 
	 {
		 StringBuffer html=new StringBuffer("");
		 try
		 {
			
			 html.append(getTitleHtml(this.r3101,titleName));
			 html.append(getBody(this.r3101,templateid));
			 html.append(getBottomTitle());
			 
			 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
		 return html.toString();
	 }
	 
	 
	 public String getBody(String r3101,String templateid)throws GeneralException
	 {
		 StringBuffer html=new StringBuffer("");
		 ArrayList gradeList=getPerGradeTemplateList(templateid);   //标准标度列表
		 try
		 {
			 int questionCount=getQuestionCount(this.r3101,templateid);
			 html.append("<table width=\"90%\"  cellspacing=\"0\" ><tr>"); 
			 html.append(getTableBodyHead(gradeList));        //产生表头
			 html.append(getTableBodyContent(gradeList,templateid,questionCount));
			 html.append(getTableBodyBasicInstance(templateid,questionCount,gradeList));
		 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
		 html.append("</table>");
		 return html.toString();
	 }
	 
	 
	 public String getBottomTitle()throws GeneralException
	 {
		 StringBuffer sub_html=new StringBuffer("");
		 Calendar d=Calendar.getInstance();
		 String date=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
		 sub_html.append("<table width=\"90%\" border=\"0\">");
		 sub_html.append("<tr><td width=\"30%\" height=\"46\"><strong>"+ResourceFactory.getProperty("train.evaluatingStencil.weave")+"：</strong></td><td width=\"40%\"><strong>"+ResourceFactory.getProperty("approve.personinfo.oks")+"：</strong></td><td width=\"30%\">&nbsp;</td></tr>");
		 sub_html.append("<tr><td width=\"30%\">&nbsp;</td><td width=\"40%\" >&nbsp;</td><td width=\"30%\" ><strong>"+ResourceFactory.getProperty("train.evaluatingStencil.statDate")+"：</strong>&nbsp;"+date+"</td></tr></table>");
		
		 return sub_html.toString();
	 }
	 
	 
	 
	 public String getTableBodyBasicInstance(String templateid,int questionCount,ArrayList gradeList)throws GeneralException
	 {
		 StringBuffer html=new StringBuffer("");
		 try
		 {
			int menCount=getTrainClassMenCount(this.r3101);  //培训班人数
			String percent="";//问卷回收比列
			String acontentment="";//满意度
			ArrayList a_pointList=(ArrayList)this.pointList.get(1);            //指标集 
		    if(questionCount!=0)
		    {
		    	int aa=a_pointList.size()*questionCount;
		    	
		    	acontentment=(new BigDecimal(this.contentment)).divide(new BigDecimal(aa),2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).toString();
		    	acontentment=PubFunc.round(acontentment,1);
		    	
		    }
			 if(menCount!=0)
			 {
				 percent=(new BigDecimal(questionCount)).divide(new BigDecimal(String.valueOf(menCount)),2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).toString();
				 percent=PubFunc.round(percent,1);
			 }
			
			 //调查基本情况
			html.append("<tr valign=\"top\">");
			html.append("<td height=\"48\" class='myUnToptd' colspan=\""+((gradeList.size()+1)*2+1)+"\" align=\"left\" ><p><strong>"+ResourceFactory.getProperty("train.evaluationStencil.investigateState")+"</strong>：</p>");
			html.append("<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+ResourceFactory.getProperty("train.evaluationStencil.trainMenCount")+"&nbsp;<input type='text' class='TEXT_NB' size='5' value='"+menCount+"' ");
			//查看培训模块的评估结果时，调查基本情况中的数据不允许更改   chenxg update 2015-03-03 
			if(flag==20)
                html.append(" readonly='readonly'");
			
			html.append("/> &nbsp;人，&nbsp;&nbsp;"+ResourceFactory.getProperty("train.evaluationStencil.returnQuestion")+"&nbsp;<input type='text' class='TEXT_NB'  size='5'  value='"+questionCount+"' ");
			
			if(flag==20)
                html.append(" readonly='readonly'");
			
			html.append("/>&nbsp;份，&nbsp;&nbsp;"+ResourceFactory.getProperty("train.evaluationStencil.returnQuestionPercent")+"&nbsp; <input type='text'  size='5'  class='TEXT_NB' value='"+percent+"' ");
			
			if(flag==20)
                html.append(" readonly='readonly'");
			
			html.append("/>%&nbsp;,&nbsp;&nbsp;"+ResourceFactory.getProperty("train.evaluationStencil.contentment")+"&nbsp;<input type='text' class='TEXT_NB'  size='5'  value='"+acontentment+"' ");
			
			if(flag==20)
                html.append(" readonly='readonly'");
			
			html.append("/> %&nbsp;&nbsp;");
			html.append("，&nbsp;&nbsp;总分:&nbsp;<input type='text' class='TEXT_NB' size='5' value='"+getTotalScore(templateid) + "'");
			
			if(flag==20)
			    html.append(" readonly='readonly'");
			
			html.append(" />分");
			html.append(" </p>");
			html.append("</td></tr>");
			//培训效果分析
			html.append("<tr valign=\"top\">"); 	
			html.append("<td height=\"90\"  class='myUnToptd' colspan=\""+((gradeList.size()+1)*2+1)+"\" align=\"left\"  ><strong>"+ResourceFactory.getProperty("train.evaluationStencil.trainEffectAnalyse")+":</strong>"); 
			html.append("</td></tr>");
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
		 return html.toString();
	 }
	 
	 
	 //取得总分
	 public String getTotalScore(String templateid)
	 {
		 String sum="0";
		 try
		 {
			 ArrayList a_pointList=(ArrayList)this.pointList.get(1);            //指标集 
			 StringBuffer sql=new StringBuffer("");
			 for(Iterator t=a_pointList.iterator();t.hasNext();)
			 {
				 String[] temp=(String[])t.next();
				 sql.append("+avg("+Sql_switcher.isnull("C_"+temp[0],"0")+")");
			 }
			 ContentDAO dao = new ContentDAO(this.conn);
			 RowSet rowSet=dao.search("select "+sql.substring(1)+" from TRA_EVAL_"+templateid+" where r3101='"+this.r3101+"'");
			 if(rowSet.next())
				 sum=PubFunc.round(rowSet.getString(1),1);
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return sum;
	 }
	 
	 
	 
	 public String getTableBodyContent(ArrayList gradeList,String templateid,int questionCount)throws GeneralException
	 {
		 StringBuffer html=new StringBuffer("");
		 try
		 {
			 HashMap gradeValueMap=getGradeValueMap(templateid);
			 ArrayList a_pointList=(ArrayList)this.pointList.get(1);            //指标集 
			 HashMap totalMap=new HashMap();
			 int a=0;
			 for(Iterator t=a_pointList.iterator();t.hasNext();)
			 {
				 a++;
				 String[] temp=(String[])t.next();
				 html.append("<tr><td height=\"55\" class='myUnToptd' >&nbsp;"+a+".&nbsp;"+temp[1]+"</td>");
				 HashMap pointGradeValueMap=new HashMap();
				 if(gradeValueMap!=null&&gradeValueMap.get("c_"+temp[0])!=null)
					 pointGradeValueMap=(HashMap)gradeValueMap.get("c_"+temp[0]);
				 for(int i=0;i<gradeList.size();i++)
				 {
					 LazyDynaBean abean=(LazyDynaBean)gradeList.get(i);
					 String gradeTemplateId=(String)abean.get("gradeTemplateId");
					 {
						 if(pointGradeValueMap.get(gradeTemplateId)!=null&&i==0)
						 {
							String count=(String)pointGradeValueMap.get(gradeTemplateId);
							if(count!=null&&!"".equals(count))
							{
								this.contentment+=Integer.parseInt(count);
							}
						 }
					 }
					 
					 
					 html.append(getTd(pointGradeValueMap,gradeTemplateId,questionCount,totalMap)); 
				 }
				 
				 //未选择
				 html.append(getTd(pointGradeValueMap,"unSelect",questionCount,totalMap));
				 html.append("</tr>");
				 
			 }
			 //总计
			 html.append("<tr><td height=\"55\" class='myUnToptd' >"+ResourceFactory.getProperty("train.evaluationStencil.total")+"</td>");
			 for(int i=0;i<gradeList.size();i++)
			 {
				 LazyDynaBean abean=(LazyDynaBean)gradeList.get(i);
				 String gradeTemplateId=(String)abean.get("gradeTemplateId");
				 String count="";
				 if(totalMap.get(gradeTemplateId)!=null)
					 count=(String)totalMap.get(gradeTemplateId);
				 html.append("<td  class='mylefttd' colspan='2' ><div align=\"center\">&nbsp;"+count+"</div></td>");
			 }
			 String gradeTemplateId="unSelect";
			 String count="";
			 if(totalMap.get(gradeTemplateId)!=null)
				 count=(String)totalMap.get(gradeTemplateId);
			 html.append("<td  class='mylefttd' colspan='2' ><div align=\"center\">&nbsp;"+count+"</div></td>");
			 html.append("</tr>");
			 
			 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
		 
		 return html.toString();
	 }
	 
	 
	 public String getTd(HashMap pointGradeValueMap,String gradeTemplateId,int questionCount,HashMap totalMap)
	 {
		 StringBuffer html=new StringBuffer("");
		 
		 String count="";
		 String percent="";
		 if(pointGradeValueMap.get(gradeTemplateId)!=null)
			 count=(String)pointGradeValueMap.get(gradeTemplateId);
		 else if("unSelect".equalsIgnoreCase(gradeTemplateId))
		 {
			 Set set=(Set)pointGradeValueMap.entrySet();
			 int num=0;
			 for(Iterator t=pointGradeValueMap.values().iterator();t.hasNext();)
			 {
				 
				num+=Integer.parseInt((String)t.next()); 
			 }
			 if(questionCount-num!=0)
    			 count=String.valueOf(questionCount-num);
			 
		 }
		 if(!"".equals(count)&&questionCount>0)
		 {
			 percent=(new BigDecimal(count)).divide(new BigDecimal(String.valueOf(questionCount)),2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).toString();
			 percent=PubFunc.round(percent,1)+"%";
		 }
		 
		 if(totalMap.get(gradeTemplateId)!=null)
		 {
			int totalCount=Integer.parseInt((String)totalMap.get(gradeTemplateId));
			if(!"".equals(count))
				totalCount+=Integer.parseInt(count);
			totalMap.put(gradeTemplateId,String.valueOf(totalCount));
		 }
		 else
		 {
			 if(!"".equals(count))
				 totalMap.put(gradeTemplateId,String.valueOf(count));
		 }
		 
		 html.append("<td  class='mylefttd'  ><div align=\"center\">&nbsp;"+count+"</div></td>");
		 html.append("<td  class='mylefttd'  ><div align=\"center\">&nbsp;"+percent+"</div></td>");
		 return html.toString();
	 }
	 
	 
	 
	 public String getTableBodyHead(ArrayList gradeList)throws GeneralException
	 {
		 StringBuffer html=new StringBuffer("");
		 try
		 {
			 html.append("<td width=\"18%\"  class='mytd'  rowspan=\"3\"><div align=\"center\"><strong>"+ResourceFactory.getProperty("train.evaluationStencil.evaluateItem")+"</strong></div></td>");
			 html.append("<td height=\"40\" class='myUnLefttd'  colspan=\""+((gradeList.size()+1)*2)+"\"><div align=\"center\"><strong>"+ResourceFactory.getProperty("train.evaluationStencil.evaluateResultStat")+"</strong></div></td></tr>");
			 html.append("<tr>");
			 
			 StringBuffer temp_str=new StringBuffer("<tr>");
			 for(Iterator t=gradeList.iterator();t.hasNext();)
			 {
				 LazyDynaBean abean=(LazyDynaBean)t.next();
				 html.append("<td class='mylefttd'  colspan=\"2\"><div align=\"center\"><strong>"+((String)abean.get("gradedesc"))+"</strong></div></td>");
				 temp_str.append("<td class='mylefttd'  width=\"6%\" height=\"33\"><div align=\"center\"><strong>"+ResourceFactory.getProperty("train.evaluationStencil.frequency")+"</strong></div></td>");
				 temp_str.append("<td  class='mylefttd' width=\"6%\"><div align=\"center\"><strong>"+ResourceFactory.getProperty("train.evaluationStencil.percent")+"</strong></div></td>");
			 }
			 html.append("<td  class='mylefttd' colspan=\"2\"><div align=\"center\"><strong>"+ResourceFactory.getProperty("train.evaluationStencil.unselect")+"</strong></div></td>");
			 temp_str.append("<td class='mylefttd'  width=\"6%\" height=\"33\"><div align=\"center\"><strong>"+ResourceFactory.getProperty("train.evaluationStencil.frequency")+"</strong></div></td>");
			 temp_str.append("<td  class='mylefttd' width=\"6%\"><div align=\"center\"><strong>"+ResourceFactory.getProperty("train.evaluationStencil.percent")+"</strong></div></td>");
			 temp_str.append("</tr>");
			 html.append("</tr>");
			 html.append(temp_str.toString());
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
		 return html.toString();
	 }
	 
	 
	 
	 
	 //取得标题 html
	 public String getTitleHtml(String r3101,String titleName)
	 {
		 StringBuffer sub_html=new StringBuffer("");
		 sub_html.append("<table width=\"90%\" border=\"0\">");
		 sub_html.append("<tr><td height=\"58\" align='center' colspan=\"2\"><font size='4'><strong>"+ResourceFactory.getProperty("train.evaluationStencil.trainEffectStat")+"</strong></font></td></tr>");
		 sub_html.append("<tr><td width=\"65%\" height=\"46\"><strong>"+ResourceFactory.getProperty("train.evaluationStencil.no")+"：</strong></td><td width=\"35%\">&nbsp;</td></tr>");
		 sub_html.append("<tr><td height=\"51\"><strong>"+ResourceFactory.getProperty("train.evaluationStencil.investigateName")+"：</strong>"+titleName+"</td><td><strong>"+ResourceFactory.getProperty("train.evaluationStencil.teacher")+"：</strong></td></tr></table>");
		
		 return sub_html.toString();
	 }
	 
	 
	 
	 
	 
	 public ArrayList getPerGradeTemplateList(String templateID)throws GeneralException
	 {
		 ArrayList list=new ArrayList();
		 ContentDAO dao = new ContentDAO(this.conn);
		 RowSet rowSet=null;
		 try
		 {
			 PersonPostModalBo ppo = new PersonPostModalBo(this.conn);
			 String per_comTable = "per_grade_template"; // 绩效标准标度
			 if(ppo.getComOrPer(templateID,"temp"))
				 per_comTable = "per_grade_competence"; // 能力素质标准标度
			 
			 ArrayList<String> paramList = new ArrayList<String>();
			 String sql = "select grade_template_id,gradedesc from "+per_comTable+" order by gradevalue desc";
			 //培训班查看考核结果，只看评估模块的指标设置的标度
			 if(StringUtils.isNotEmpty(this.r3101)) {
				 sql = "select grade_template_id,gradedesc from " + per_comTable + " where grade_template_id in ("
				 		+ "select distinct gradecode from per_grade where point_id in (select a.point_id from per_template_point a,per_template_item b "
				 		+ "where a.item_id=b.item_id and b.template_id=?))";
				 paramList.add(templateID);
			 }
				 
			 rowSet=dao.search(sql, paramList);
			 while(rowSet.next())
			 {
				 LazyDynaBean abean=new LazyDynaBean();
				 abean.set("gradeTemplateId",rowSet.getString(1));
				 abean.set("gradedesc",rowSet.getString(2));
				 list.add(abean);
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
		 return list;
	 }
	 
	 
	 
	 
	 
	 
	 public String[] getPerGradeTemplateArraye(String templateID)
	 {
		 StringBuffer gradeId=new StringBuffer("");
		 ContentDAO dao = new ContentDAO(this.conn);
		 RowSet rowSet=null;
		 try
		 {
			 PersonPostModalBo ppo = new PersonPostModalBo(this.conn);
			 String per_comTable = "per_grade_template"; // 绩效标准标度
			 if(ppo.getComOrPer(templateID,"temp"))
				 per_comTable = "per_grade_competence"; // 能力素质标准标度
			 rowSet=dao.search("select * from "+per_comTable+"");
			 while(rowSet.next())
			 {
				gradeId.append("/");
				gradeId.append(rowSet.getString("grade_template_id")); 
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 String str=gradeId.substring(1);
		 return str.split("/");
		 
	 }
	 
	 
	 /**
	  * 取得某活动下 各指标相应标度的选择数量
	  * @param templateID
	  * @return
	  */
	 public HashMap getGradeValueMap(String templateID)throws GeneralException
	 {
		 HashMap map=new HashMap();
		 ContentDAO dao = new ContentDAO(this.conn);
		 RowSet rowSet=null;
		 try
		 {
			 HashMap rankMap=getPointRank(templateID);	
			 
			 this.pointList=getPerPointList(templateID);
			 ArrayList pointInfoList=(ArrayList)pointList.get(0);         //指标详细集
			 ArrayList pointList0=(ArrayList)pointList.get(1);             //指标集 
			 String[]  perGradeTemplate=getPerGradeTemplateArraye(templateID);      //模版标度
			
			 rowSet=dao.search("select * from TRA_EVAL_"+templateID+" where r3101='"+this.r3101+"'");
			 while(rowSet.next())
			 {
				
				 for(Iterator t=pointList0.iterator();t.hasNext();)
				 {
					 String[] temp = (String[])t.next();
					 if(rowSet.getString("C_"+temp[0])!=null)
					 {
						 double score=rowSet.getDouble("C_"+temp[0]);
						 score=score/Double.parseDouble((String)rankMap.get(temp[0]));  // 分值除以权重
						 HashMap pointValueMap=new HashMap();
						 if(map.get("c_"+temp[0])!=null)
						 {
							 pointValueMap=(HashMap)map.get("c_"+temp[0]);
							 
						 }
						 map.put("c_"+temp[0],getPointValueMap(pointValueMap,perGradeTemplate,score,pointInfoList,temp));
					 }
					 else
					 {	
						 HashMap pointValueMap=new HashMap();
						 if(map.get("c_"+temp[0])!=null)
						 {
							 pointValueMap=(HashMap)map.get("c_"+temp[0]); 
						 }
						 if(pointValueMap.get("unSelect")!=null)
						 {
							 int count=Integer.parseInt((String)pointValueMap.get("unSelect"));
							 pointValueMap.put("unSelect",String.valueOf(++count));
						 }
						 else
						 {
							 pointValueMap.put("unSelect","1");
						 }
						 map.put("c_"+temp[0],pointValueMap);
					 }
				}
					 
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
		 return map;
	 }
	 
	 
	 
	 
	 
	 
	 public HashMap getPointValueMap(HashMap pointValueMap,String[] perGradeTemplate,double score,ArrayList pointInfoList,String[] temp)
	 {
		 String[] temp2=null;
		 for(int i=0;i<pointInfoList.size();i++)
		 {
			 String[] temp1 = (String[])pointInfoList.get(i);
			 if (temp[0].equals(temp1[1])) {				
				 	boolean isEnd=true;
				 	if((i+1)<pointInfoList.size())
				 	{
				 		String[] temp_bak = (String[])pointInfoList.get((i+1));
				 		if(temp_bak[1].equalsIgnoreCase(temp[0]))
				 			isEnd=false;
				 	}
					if (score<= Float
							.parseFloat(temp1[9])*Float
							.parseFloat(temp1[8])
							&&(score > Float
							.parseFloat(temp1[7])*Float
							.parseFloat(temp1[8])||(isEnd&&score >=Float
									.parseFloat(temp1[7])*Float
									.parseFloat(temp1[8]))))
					{
						temp2 = temp1;
						break;
					}
				}
		 }
		 if(temp2!=null)
		 {
			 for(int i=0;i<perGradeTemplate.length;i++)
			 {
				 String gradeId=perGradeTemplate[i];
				 if(gradeId.equalsIgnoreCase(temp2[5]))
				 {
					 if(pointValueMap.get(gradeId)!=null)
					 {
						 int count=Integer.parseInt((String)pointValueMap.get(gradeId));
						 pointValueMap.put(gradeId,String.valueOf(++count));
					 }
					 else
					 {
						 pointValueMap.put(gradeId,"1");
					 }
					
					 break;
				 }
			 }
		 }
		 return pointValueMap;
	 }  
	 
	 
	 
	 
	 
	 
	 
	 
	
	
	
	
	 
	 
	 
	 
	
	
	
	
	
	
	/*********************************************/
	
	

	public String getR3101() {
		return r3101;
	}

	public void setR3101(String r3101) {
		this.r3101 = r3101;
	}
	
	public UserView getUserView() {
		return userView;
	}
	
	public void setUserView(UserView userView) {
		this.userView = userView;
	}
}
