package com.hjsj.hrms.businessobject.performance.batchGrade;

import com.hjsj.hrms.businessobject.performance.options.PerDegreeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.*;

/**
 *<p>Title:TotalScoreControlBo.java</p> 
 *<p>Description:绩效打分总分强制分布控制</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 25, 2008</p> 
 *@author dengcan
 *@version 4.0
 */

public class TotalScoreControlBo 
{
	
	private Connection conn=null;
	private String planid="";          //考核计划
	private String degree_id="";       //考核等级id
	private ArrayList controlList=new ArrayList();  //控制列表
	private ArrayList perdegreedescList=new ArrayList();  //考核等级类荣
	private HashMap degreeDescMap=new HashMap();
	private ArrayList objectList = new ArrayList(); 
	
	public ArrayList getObjectList() {
		return objectList;
	}

	public void setObjectList(ArrayList objectList) {
		this.objectList = objectList;
	}

	public TotalScoreControlBo()
	{
		
	}
	
	public TotalScoreControlBo( Connection con,String a_planid,String degree_id)
	{
		this.conn=con;
		this.planid=a_planid;
		this.degree_id=degree_id;
	
	//	RecordVo vo=new RecordVo("per_degree");
	//	if(vo.hasAttribute("extpro"))
		{
			this.perdegreedescList=getPerdegreedescList();
			
			// 绩效打分总分强制分布控制获得高级设置 JinChunhai 2011.12.08 修改
		//	this.controlList = getControlList();						
			PerDegreeBo bo = new PerDegreeBo(this.conn,this.degree_id,this.planid);  
			this.controlList = bo.getDegreeHighSetList(true);//计算的强制分布不忽略启用	    				
		}
	}
	
	
	/**
	 * 取得本人考核计划的考核对象 或 考核对象所在的部门
	 * @param opt 0:考核对象,1:部门
	 * @param planid
	 * @param mainbody_id
	 * @return
	 */
	public HashMap getSelfUn_Man(String grouped,String planid,String mainbody_id)
	{
		HashMap map=new HashMap();
		try
		{
			
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("select ");
			if("0".equalsIgnoreCase(grouped)) {
                sql.append(" po.object_id ");
            } else {
                sql.append(" distinct po." + grouped + " ");
            }
			sql.append(" from per_mainbody pm,per_object po where pm.object_id=po.object_id and po.plan_id="+planid+"  ");
			sql.append(" and pm.plan_id="+planid+" and pm.mainbody_id='"+mainbody_id+"'" );
			RowSet rowSet=dao.search(sql.toString());
			while(rowSet.next()) {
                map.put(rowSet.getString(1)!=null?rowSet.getString(1):"-1","1");
            }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	/**
	 * 判断 是否不符合总分控制条件
	 * @param objectTotalScoreMap {objectid| 分数/排名 }
	 * @return
	 */
	public String isOverTotalScoreControl(HashMap objectTotalScoreMap,String planid,String mainbody_id,String CheckGradeRange)
	{		
		RecordVo planVo = getPlanVo(planid);
		String info="";
		HashMap objSelfMap=getSelfUn_Man("0",planid,mainbody_id);
		ArrayList objectTotalScoreList=resetTotalScoreList(objectTotalScoreMap,CheckGradeRange,mainbody_id,objSelfMap);
		
		if(this.controlList.size()<=0) {
            return info;
        }
		
		for(int i=0;i<this.controlList.size();i++)
		{
			LazyDynaBean controlBean = (LazyDynaBean)this.controlList.get(i);
		    String Mode=(String)controlBean.get("mode");   // 1=比例控制 2=人数控制 
		    String Oper=(String)controlBean.get("oper");   // Oper操作符：1=不少于 2=不多于
		    String Value=(String)controlBean.get("value"); // Value 值：比例时为百分比（保存时小于0），人数时为个数
		    String ori_value=Value;
		    String Grouped=(String)controlBean.get("grouped"); // 分组指标
		    String ActIds=(String)controlBean.get("actIds");   // ActIds 参与等级：等级ID号，以逗号分隔		      		      		      		   
  			String um_grade=(String)controlBean.get("UMGrade");// 部门考核等级
  			String toRoundOff=(String)controlBean.get("toRoundOff"); // 1表示四舍五入，为0表示取整 mode为百分比时
  			
  			if("-1".equalsIgnoreCase(Grouped)) // 空
            {
                Grouped = "false";
            } else if("UNIT".equalsIgnoreCase(Grouped)) // 单位
            {
                Grouped = "b0110";
            } else if("DEPART".equalsIgnoreCase(Grouped)) // 部门
            {
                Grouped = "e0122";
            } else if("CLASSIFY".equalsIgnoreCase(Grouped)) // 对象类别
            {
                Grouped = "body_id";
            }
		      
		    if(ActIds!=null&&ActIds.length()>0&&!",".equalsIgnoreCase(ActIds))
		    {
			      if("b0110".equalsIgnoreCase(Grouped) || "e0122".equalsIgnoreCase(Grouped) || "body_id".equalsIgnoreCase(Grouped))
			      {
			    	  HashMap e0122SelfMap = getSelfUn_Man(Grouped,planid,mainbody_id);
			    	  HashMap e0122Map = getE0122Map(Grouped,objectTotalScoreList);
			    	  Set keySet = e0122Map.keySet();
			    	  for(Iterator t=keySet.iterator();t.hasNext();)
			    	  {
			    		  String e0122=(String)t.next();
			    		  if(e0122SelfMap.get(e0122)==null) {
                              continue;
                          }
			    		  int e0122Num=Integer.parseInt((String)e0122Map.get(e0122));
			    		  
			    		  if("1".equals(Mode)) {
                              Value= PubFunc.round(String.valueOf(e0122Num*Float.parseFloat(ori_value)),3) ;
                          }
			    		  
			    		  int number = 0;
			    		  if("1".equals(Oper))//不少于
			    		  {
								if (toRoundOff != null && toRoundOff.trim().length() > 0 && "1".equalsIgnoreCase(toRoundOff)) {
                                    number=Integer.parseInt(PubFunc.round(String.valueOf(Double.parseDouble(Value)), 0)); // 四舍五入
                                } else {
                                    number=(int)Math.ceil(Double.parseDouble(Value));   // 向上取整 只要有小数则进1 3.0 3.1 3.8 返回 3.0 4.0 4.0
                                }
						  }			    			  
		    	          else if("2".equals(Oper))//不多于
		    	          {
								if (toRoundOff != null && toRoundOff.trim().length() > 0 && "1".equalsIgnoreCase(toRoundOff)) {
                                    number=Integer.parseInt(PubFunc.round(String.valueOf(Double.parseDouble(Value)), 0)); // 四舍五入
                                } else {
									number=(int)Math.floor(Double.parseDouble(Value)); // 向下取整 3.0 3.1 3.8 返回 3.0 3.0 3.0
									// 基数乘以比例小于一个时，默认按1个控制 chent 20180327 add
									if(number == 0) {
										number = 1;
									}
								}
						  }
			    		  
				    	  String[] temps=ActIds.split(",");
				    	  LazyDynaBean abean=null;
				    	  int temps_length=0;
				    	  for(int n=0;n<temps.length;n++)
				    	  {
				    		  int num=0;
				    		  if(temps[n].trim().length()==0) {
                                  continue;
                              }
					    	  for(int j=0;j<objectTotalScoreList.size();j++)
					    	  {
					    		  abean=(LazyDynaBean)objectTotalScoreList.get(j);
					    		  String object_id=(String)abean.get("object_id");
					    		  String status=(String)abean.get("status");
					    		  if(objSelfMap.get(object_id)==null) {
                                      continue;
                                  }
					    		  if(planVo.getInt("method")==2)
					    		  {
						    		  if(status!=null && status.trim().length()>0 && ("2".equals(status)|| "3".equals(status)|| "4".equals(status)|| "7".equals(status)))
						    		  {}
						    		  else if(status!=null && status.trim().length()>0 && "1".equals(status) && objectList.contains(object_id)){
						    			  
						    		  }
						    		  else {
                                          continue;
                                      }
					    		  }
					    		  
					    		  String ae0122=(String)abean.get(Grouped);
					    		  if(ae0122.length()==0) {
                                      ae0122="-1";
                                  }
					    		  if(e0122.equals(ae0122))
					    		  {
						    		  String id=(String)abean.get("id");
						    		  if(id.equals(temps[n]))
						    		  {
						    			  num++;
						    		  }
					    		  }
					    	  }					    	  
					    	  temps_length+=num;					    	
				    	  }
				    	  
				    	  String objectName = "";
				    	  if("body_id".equalsIgnoreCase(Grouped))
				    	  {
				    		  HashMap objectTypeMap = getObjectTypeMap();				    		  				    		  
				    		  objectName = (String)objectTypeMap.get(e0122);				    		  				    		  
				    	  }else if("b0110".equalsIgnoreCase(Grouped)) {
                              objectName = "-1".equals(e0122)?"":AdminCode.getCodeName("UN",e0122);
                          } else if("e0122".equalsIgnoreCase(Grouped)) {
                              objectName = "-1".equals(e0122)?"":AdminCode.getCodeName("UM",e0122);
                          }
				    	  
				    	  if("1".equals(Oper))  //不少于
				    	  {
				    		  if(temps_length<number)
				    		  {
				    			  info+=getInfo(temps,Mode,Oper,ori_value,Grouped,objectName);
				    		  }
				    	  }
				    	  else if("2".equals(Oper)) //不多于
				    	  {
				    		  if(temps_length>number)
				    		  {
				    			  info+=getInfo(temps,Mode,Oper,ori_value,Grouped,objectName);
				    		  }				    		  
				    	  }				    	  				    	  
			    	  }
			      }
			      else
			      {
			    	  if("1".equals(Mode)) {
                          Value= PubFunc.round(String.valueOf(objectTotalScoreList.size()*Float.parseFloat(Value)),3) ;
                      }
			    	  int number = 0;
		    		  if("1".equals(Oper))//不少于
		    		  {
							if (toRoundOff != null && toRoundOff.trim().length() > 0 && "1".equalsIgnoreCase(toRoundOff)) {
                                number=Integer.parseInt(PubFunc.round(String.valueOf(Double.parseDouble(Value)), 0)); // 四舍五入
                            } else {
                                number=(int)Math.ceil(Double.parseDouble(Value));   // 向上取整 只要有小数则进1 3.0 3.1 3.8 返回 3.0 4.0 4.0
                            }
					  }		    			  
	    	          else if("2".equals(Oper))//不多于
	    	          {
							if (toRoundOff != null && toRoundOff.trim().length() > 0 && "1".equalsIgnoreCase(toRoundOff)) {
                                number=Integer.parseInt(PubFunc.round(String.valueOf(Double.parseDouble(Value)), 0)); // 四舍五入
                            } else {
								number=(int)Math.floor(Double.parseDouble(Value)); // 向下取整 3.0 3.1 3.8 返回 3.0 3.0 3.0
								// 基数乘以比例小于一个时，默认按1个控制 chent 20180327 add
								if(number == 0) {
									number = 1;
								}
							}
					  }	    	        	  
			    	  
			    	  String[] temps=ActIds.split(",");
			    	  LazyDynaBean abean=null;
			    	  
			    	  int temps_length=0;
			    	  for(int n=0;n<temps.length;n++)
			    	  {
			    		  int num=0;
			    		  if(temps[n].trim().length()==0) {
                              continue;
                          }
				    	  for(int j=0;j<objectTotalScoreList.size();j++)
				    	  {
				    		  abean=(LazyDynaBean)objectTotalScoreList.get(j);
				    		  String object_id=(String)abean.get("object_id");
				    		  String status=(String)abean.get("status");
				    		  if(objSelfMap.get(object_id)==null) {
                                  continue;
                              }
				    		  if(planVo.getInt("method")==2)
				    		  {
					    		  if(status!=null && status.trim().length()>0 && ("2".equals(status)|| "3".equals(status)|| "4".equals(status)|| "7".equals(status)))
					    		  {}
					    		  else {
                                      continue;
                                  }
				    		  }
				    		  
				    		  String id=(String)abean.get("id");
				    		//for(int n=0;n<temps.length;n++)
				    		  {
				    			  if(id.equals(temps[n]))
				    			  {
				    				  num++;
				    				//  break;
				    			  }
				    		  }
				    	  }
				    	  temps_length+=num;
			    	  }
			    	  
			    	  if("1".equals(Oper))  //不少于
			    	  {
			    		  if(temps_length<number)
			    		  {
			    			  info+=getInfo(temps,Mode,Oper,ori_value,"","");
			    		  }
			    	  }
			    	  else if("2".equals(Oper)) //不多于
			    	  {
			    		  if(temps_length>number)
			    		  {
			    			  info+=getInfo(temps,Mode,Oper,ori_value,"","");
			    		  }
			    	  }			    	  
			      }
		      }		      
		}
		return info;
	}
	
	
	
	public String getInfo(String[] temps,String Mode,String Oper,String Value,String Grouped,String e0122)
	{
		StringBuffer info=new StringBuffer("\r\n");
		StringBuffer temp_info=new StringBuffer("");
		if(Grouped!=null && Grouped.trim().length()>0){
			if(e0122 != null && !"".equals(e0122)) {
                info.append(e0122+":");
            } else {
                info.append("考核对象类别为空：");
            }
		}
		for(int n=0;n<temps.length;n++)
		{
			 if(temps[n].trim().length()==0) {
                 continue;
             }
			 temp_info.append("\r\n"+(String)degreeDescMap.get(temps[n]));
			 if(n!=temps.length-1) {
                 temp_info.append(",");
             }
		}
		info.append(temp_info);
		if("1".equals(Mode)) {
            info.append(" "+ResourceFactory.getProperty("label.performance.gradePercentage")+" ");
        } else {
            info.append(" "+ResourceFactory.getProperty("label.performance.gradeNumber")+" ");
        }
		
		if("1".equals(Oper))  //不少于
        {
            info.append(" "+ResourceFactory.getProperty("label.performance.notLess")+" ");
        } else if("2".equals(Oper)) //不多于
        {
            info.append(" "+ResourceFactory.getProperty("label.performance.notMore")+" ");
        }
		
		if("1".equals(Mode))
		{
			info.append(PubFunc.round(String.valueOf(Float.parseFloat(Value)*100),0)+"%");			
		}
		else
		{
			info.append(Value+"！");
		}
		return info.toString();
	}
	
	
	
	public HashMap getE0122Map(String grouped,ArrayList objectTotalScoreList)
	{
		
		HashMap map=new HashMap();
		LazyDynaBean a_abean=null;
		for(int i=0;i<objectTotalScoreList.size();i++)
		{
			a_abean=(LazyDynaBean)objectTotalScoreList.get(i);
			String group=(String)a_abean.get(grouped);
			if(group.length()<=0) {
                group="-1";
            }
			if(map.get(group)==null) {
                map.put(group,"1");
            } else
			{
				int num=Integer.parseInt((String)map.get(group));
				map.put(group, String.valueOf(++num));				
			}
		}
		return map;
	}
	
	
	/**
	 * @param CheckGradeRange  多人打分等级控制是按所有主体还是单个主体。(0:所有，1: 单个)
	 * @param objectTotalScoreMap
	 * @return
	 */
	public ArrayList resetTotalScoreList(HashMap objectTotalScoreMap,String CheckGradeRange,String mainbody_id,HashMap e0122SelfMap)
	{
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			Set keySet=objectTotalScoreMap.keySet();
			StringBuffer objectid_str=new StringBuffer("");
			
			for(Iterator t=keySet.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				if(temp!=null&&!"objectList_order".equalsIgnoreCase(temp))
				{
					if("1".equals(CheckGradeRange)&&e0122SelfMap.get(temp)!=null) {
                        objectid_str.append(",'"+temp+"'");
                    } else if("0".equals(CheckGradeRange)) {
                        objectid_str.append(",'"+temp+"'");
                    }
				}
			}						
			String sql = "";
			if("0".equals(CheckGradeRange)) {
                sql = "select object_id,b0110,e0122,body_id from per_object where plan_id="+this.planid+" ";
            } else {
                sql = "select object_id,b0110,e0122,body_id from per_object where plan_id="+this.planid+" and object_id in ("+objectid_str.substring(1)+") ";
            }
						
			rowSet = dao.search(sql);
			LazyDynaBean aa_abean=new LazyDynaBean();
			String domainflag="1";
			if(this.perdegreedescList.size()>0)
			{
				aa_abean=(LazyDynaBean)this.perdegreedescList.get(0);
				domainflag=(String)aa_abean.get("domainflag");
			}
			HashMap objectStatusMap = getObjectStatusMap(mainbody_id);
			while(rowSet.next())
			{
				String b0110=rowSet.getString("b0110");
				String object_id=rowSet.getString("object_id");
				String e0122=rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"";
				String body_id=rowSet.getString("body_id")!=null?rowSet.getString("body_id"):"";
				String score="0";
				if((String)objectTotalScoreMap.get(object_id)!=null) {
                    score=((String)objectTotalScoreMap.get(object_id)).split("/")[0];
                }
				LazyDynaBean a_abean=null;
				String id="";
				
				if("1".equals(domainflag))  //下限封闭
				{	
					for(int i=0;i<this.perdegreedescList.size();i++)
					{
						a_abean=(LazyDynaBean)this.perdegreedescList.get(i);
						String aid=(String)a_abean.get("id");
						
						float topscore=Float.parseFloat((String)a_abean.get("topscore"));
						float bottomscore=Float.parseFloat((String)a_abean.get("bottomscore"));
						float ascore=0;
						if(score.length()>0) {
                            ascore=Float.parseFloat(score);
                        }
						
						if(ascore<=topscore&&ascore>=bottomscore)
						{
							id=aid;
							break;
						}						
					}
				}
				else if("0".equals(domainflag)) //上限封闭
				{
					for(int i=this.perdegreedescList.size()-1;i>=0;i--)
					{
						a_abean=(LazyDynaBean)this.perdegreedescList.get(i);
						String aid=(String)a_abean.get("id");
						
						float topscore=Float.parseFloat((String)a_abean.get("topscore"));
						float bottomscore=Float.parseFloat((String)a_abean.get("bottomscore"));
						float ascore=0;
						if(score.length()>0) {
                            ascore=Float.parseFloat(score);
                        }
						
						if(ascore<=topscore&&ascore>=bottomscore)
						{
							id=aid;
							break;
						}						
					}
				}
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("b0110", b0110);
				abean.set("object_id", object_id);
				abean.set("e0122", e0122);
				abean.set("body_id", body_id);
				if((String)objectStatusMap.get(object_id)!=null) {
                    abean.set("status", (String)objectStatusMap.get(object_id));
                } else {
                    abean.set("status", "0");
                }
				abean.set("score", score);
				abean.set("id",id);
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
     * @return
     */
	public HashMap getObjectStatusMap(String mainbody_id)
	{	
		HashMap map = new HashMap();		
		RowSet rowSet = null;
		try
		{		    
		    ContentDAO dao = new ContentDAO(this.conn);
		    StringBuffer sql = new StringBuffer("select object_id,status from per_mainbody ");
		    sql.append(" where plan_id = "+this.planid+" and mainbody_id='"+mainbody_id+"' ");
		    sql.append(" ");
		//  sql.append(" order by score desc ");		    
		    rowSet = dao.search(sql.toString());
			while (rowSet.next())
			{
			    String object_id = rowSet.getString("object_id");
			    String status = rowSet.getString("status");
			    
			    map.put(object_id, status);
			}
			
			if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}		
		return map;
	}
	
	/**
	 * 取得考核等级内容
	 * @return
	 */
	public ArrayList getPerdegreedescList()
	{
		ArrayList list=new ArrayList();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rowSet=dao.search("select per_degreedesc.id,per_degreedesc.itemname,per_degreedesc.itemdesc,"+Sql_switcher.isnull("per_degreedesc.topscore","0")+" topscore,"+Sql_switcher.isnull("per_degreedesc.bottomscore","0")+" bottomscore,per_degree.domainflag from per_degreedesc,per_degree where per_degreedesc.degree_id=per_degree.degree_id and  per_degreedesc.degree_id="+this.degree_id+" order by per_degreedesc.id");
			while(rowSet.next())
			{ 
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("id",rowSet.getString("id"));
				abean.set("itemname",rowSet.getString("itemname"));
				abean.set("topscore",rowSet.getString("topscore"));
				abean.set("bottomscore",rowSet.getString("bottomscore"));
				abean.set("domainflag",rowSet.getString("domainflag"));
				degreeDescMap.put(rowSet.getString("id"),rowSet.getString("itemname")+(rowSet.getString("itemdesc")!=null&&rowSet.getString("itemdesc").trim().length()>0?("("+rowSet.getString("itemdesc")+")"):""));
				list.add(abean);
			}
			
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	public static void main(String[] args)
	{
		TotalScoreControlBo bo=new TotalScoreControlBo();
		bo.getControlList();
		System.out.println("ddd");
	}
	
	
	
	/**
	<?xml version="1.0" encoding="GB2312"?>
	< !-- used：是否启用 -->
	<AdvancedDegrees used="TRUE|FALSE">
		< !-- Mode方式：1=比例控制 2=人数控制 -->
		< !-- Oper操作符：1=不少于 2=不多于 -->
		< !-- Value 值：比例时为百分比（保存时小于0），人数时为个数 -->
		< !-- Grouped 按部门分组：TRUE=分组 FALSE=不分组 -->
		< !-- ActIds 参与等级：等级ID号，以逗号分隔 -->
		<Degree Mode="1|2" Oper="1|2" Value="" Grouped="TRUE|FALSE" ActIds="12,14"/>
	</AdvancedDegrees>
   */
	//取得控制列表
	public ArrayList getControlList()
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from per_degree where degree_id="+this.degree_id);
			String extpro="";
			if(rowSet.next())
			{
				extpro=Sql_switcher.readMemo(rowSet,"extpro");
			}
	/*		String extpro="<?xml version=\"1.0\" encoding=\"GB2312\"?>";
			extpro+="<AdvancedDegrees used=\"TRUE\">";
			extpro+="<Degree Mode=\"1\" Oper=\"1\" Value=\"0.4\" Grouped=\"False\" ActIds=\"1\"/>";
		//	extpro+="<Degree Mode=\"2\" Oper=\"2\" Value=\"dfad\" Grouped=\"False\" ActIds=\"12,15\"/>";
			extpro+="</AdvancedDegrees>";*/
			
			if(!"".equals(extpro))
			{
				Element root;
				Document doc = PubFunc.generateDom(extpro);
				root = doc.getRootElement();
			//	if (root.getAttributeValue("used") != null&&root.getAttributeValue("used").equalsIgnoreCase("TRUE")) 
				{
					List alist=root.getChildren();
					for(int i=0;i<alist.size();i++)
					{
						Element element=(Element)alist.get(i);
						LazyDynaBean abean=new LazyDynaBean();
						String Mode="";String Oper="";String Value ="";String Grouped ="";String ActIds ="";
						abean.set("Mode", element.getAttributeValue("Mode"));
						abean.set("Oper", element.getAttributeValue("Oper"));
						abean.set("Value", element.getAttributeValue("Value"));
						abean.set("Grouped", element.getAttributeValue("Grouped"));
						abean.set("ActIds", element.getAttributeValue("ActIds"));
						list.add(abean);
				    }
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
     * 取得考核对象类别列表
     * @return
     */
	public HashMap getObjectTypeMap()
	{	
		HashMap map = new HashMap();
		RowSet rowSet = null;
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    String sql = "select body_id,name from per_mainbodyset where body_type=1 and status=1 order by seq";		   		    
		    rowSet = dao.search(sql);		    
		    while (rowSet.next())
		    {
				map.put(rowSet.getString("body_id"), rowSet.getString("name"));
		    }
		
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return map;
	}
	
	public RecordVo getPlanVo(String planid)
    {

		RecordVo vo = new RecordVo("per_plan");
		try
		{
		    vo.setInt("plan_id", Integer.parseInt(planid));
		    ContentDAO dao = new ContentDAO(this.conn);
		    vo = dao.findByPrimaryKey(vo);
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return vo;
    }
}
