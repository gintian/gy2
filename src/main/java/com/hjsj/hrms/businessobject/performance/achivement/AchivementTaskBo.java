package com.hjsj.hrms.businessobject.performance.achivement;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title:AchivementTaskBo.java</p>
 * <p>Description:业绩任务书.</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-11-10 10:34:27</p>
 * @author JinChunhai
 * @version 5.0
 */

public class AchivementTaskBo 
{
	private Connection con=null;
	private UserView userview=null;
	
	public AchivementTaskBo(Connection a_con,UserView userView)
	{
		this.con=a_con;
		this.userview=userView;
	}
	
	
	/**
	 * 保存指标排序
	 * @param pointList
	 */
	public void savePointSort(String[] pointList,String target_id)
	{
		DbSecurityImpl dbS = new DbSecurityImpl();
		PreparedStatement ps= null;
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			String sql = "update per_target_point set seq=? where target_id="+target_id+" and lower(point_id)=?";
			ps=this.con.prepareStatement(sql);
			for(int i=0;i<pointList.length;i++)
			{
				ps.setInt(1,i+1);
				ps.setString(2,pointList[i].toLowerCase());
				ps.addBatch();
			}
			// 打开Wallet
			dbS.open(this.con, sql);
			ps.executeBatch();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(ps);
			PubFunc.closeDbObj(this.con);
			try {
				// 关闭Wallet
				dbS.close(this.con);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	
	/**
	 * 撤销绩效任务
	 * @param selectids
	 * @param target_id
	 */
	public void delTargetTask(String selectids,String target_id)
	{
		DbSecurityImpl dbS = new DbSecurityImpl();
		PreparedStatement ps= null;
		try
		{
			String[] temps=selectids.split("`");
			String sql = "delete from per_target_mx where target_id="+target_id+" and kh_cyle=? and object_id=?";
			ps=this.con.prepareStatement(sql);
			for(int i=0;i<temps.length;i++)
			{
				if(temps[i].trim().length()>0)
				{
					String[] temps2=PubFunc.keyWord_reback(temps[i]).split("/");
					ps.setString(1, temps2[1]);
					ps.setString(2, temps2[0]);
					ps.addBatch();                 
				}
			}
			// 打开Wallet
			dbS.open(this.con, sql);
			ps.executeBatch();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(ps);
			PubFunc.closeDbObj(this.con);
			// 关闭Wallet
			dbS.close(this.con);

		}
	}
	
	
	
	//取得绩效目标任务书
	public RecordVo  getPerTargetVo(String target_id)
	{
		RecordVo vo=new RecordVo("per_target_list");
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			vo.setInt("target_id",Integer.parseInt(target_id));
			vo=dao.findByPrimaryKey(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	
	
	
	/**
	 * 提交 任务分配
	 * @param target_id
	 * @param right_fields
	 * @param cycle
	 * @return
	 */
	public boolean subAchivementTask(String target_id,String[] right_fields,String cycle)
	{
		boolean flag=true;
		try
		{
			ArrayList list=new ArrayList();
			ContentDAO dao=new ContentDAO(this.con);
			RecordVo targetVo=getPerTargetVo(target_id);
			int a_cycle=targetVo.getInt("cycle");  //(0|1|2|3)=(年度|半年|季度|月度)
			HashMap existRecordMap=getPerTargetMxRecords(target_id);
			HashMap objectInfoMap=getObjectInfoMap(right_fields,targetVo.getString("object_type"));
			
			for(int i=0;i<right_fields.length;i++)
			{
				if(right_fields[i].length()>0)
				{
					String objectid= "1".equals(targetVo.getString("object_type"))?right_fields[i].substring(2):right_fields[i].substring(3);
					if("-1".equals(cycle))
					{
						if(a_cycle==0)  //年度
						{
							if(existRecordMap.get(objectid)!=null)
							{
								String str=(String)existRecordMap.get(objectid);
								if(str.indexOf(",01,")==-1) {
                                    list.add(getPerTargetMxVo("01",target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
                                }
							}
							else
							{
								list.add(getPerTargetMxVo("01",target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
							}
						}
						else
						{
							if(existRecordMap.get(objectid)!=null)
							{
								String str=(String)existRecordMap.get(objectid);
								if(a_cycle==1)
								{
									if(str.indexOf(",1,")==-1) {
                                        list.add(getPerTargetMxVo("1",target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
                                    }
									if(str.indexOf(",2,")==-1) {
                                        list.add(getPerTargetMxVo("2",target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
                                    }
								}
								else if(a_cycle==2)
								{
									if(str.indexOf(",01,")==-1) {
                                        list.add(getPerTargetMxVo("01",target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
                                    }
									
									if(str.indexOf(",02,")==-1) {
                                        list.add(getPerTargetMxVo("02",target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
                                    }
									if(str.indexOf(",03,")==-1) {
                                        list.add(getPerTargetMxVo("03",target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
                                    }
									if(str.indexOf(",04,")==-1) {
                                        list.add(getPerTargetMxVo("04",target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
                                    }
								}
								else if(a_cycle==3)
								{
									for(int j=1;j<10;j++)
									{
										if(str.indexOf(",0"+j+",")==-1) {
                                            list.add(getPerTargetMxVo("0"+j,target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
                                        }
									}
									if(str.indexOf(",10,")==-1) {
                                        list.add(getPerTargetMxVo("10",target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
                                    }
									if(str.indexOf(",11,")==-1) {
                                        list.add(getPerTargetMxVo("11",target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
                                    }
									if(str.indexOf(",12,")==-1) {
                                        list.add(getPerTargetMxVo("12",target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
                                    }
								}
								
								
							}
							else
							{
								if(a_cycle==1)
								{
									list.add(getPerTargetMxVo("1",target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
									list.add(getPerTargetMxVo("2",target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
								}
								else if(a_cycle==2)
								{
									list.add(getPerTargetMxVo("01",target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
									list.add(getPerTargetMxVo("02",target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
									list.add(getPerTargetMxVo("03",target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
									list.add(getPerTargetMxVo("04",target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
								}
								else if(a_cycle==3)
								{
									for(int j=1;j<10;j++) {
                                        list.add(getPerTargetMxVo("0"+j,target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
                                    }
									list.add(getPerTargetMxVo("10",target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
									list.add(getPerTargetMxVo("11",target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
									list.add(getPerTargetMxVo("12",target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
								}
							}
							
						}
					}
					else
					{
						if(existRecordMap.get(objectid)!=null)
						{
							String str=(String)existRecordMap.get(objectid);
							if(str.indexOf(","+cycle+",")==-1) {
                                list.add(getPerTargetMxVo(cycle,target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
                            }
						}
						else
						{
							list.add(getPerTargetMxVo(cycle,target_id,(LazyDynaBean)objectInfoMap.get(objectid)));
						}
						
					}
				}
			}
			dao.addValueObject(list);
		}
		catch(Exception e)
		{
			flag=false;
			e.printStackTrace();
		}
		
		return flag;
	}
	
	/**
	 * 取得 目标任务明细纪录
	 * @param kh_cyle
	 * @param target_id
	 * @param objectBean
	 * @return
	 */
	public RecordVo getPerTargetMxVo(String kh_cyle,String target_id,LazyDynaBean objectBean)
	{
		RecordVo vo=new RecordVo("per_target_mx");
		vo.setString("kh_cyle",kh_cyle);
		vo.setInt("target_id",Integer.parseInt(target_id));
		vo.setString("object_id",(String)objectBean.get("object_id"));
		vo.setString("nbase",objectBean.get("nbase")==null||((String)objectBean.get("nbase")).trim().length()==0?"Usr":((String)objectBean.get("nbase")));
		vo.setString("b0110",(String)objectBean.get("b0110"));
		vo.setString("e0122",(String)objectBean.get("e0122"));
		vo.setString("a0100",(String)objectBean.get("a0100"));
		vo.setString("a0101",(String)objectBean.get("a0101"));
		if(((String)objectBean.get("a0000")).length()>0) {
            vo.setInt("a0000", Integer.parseInt((String)objectBean.get("a0000")));
        }
		return vo;
	}
	
	
	/**
	 * 取得目标任务 已有的纪录
	 * @param target_id
	 * @return
	 */
	public HashMap getPerTargetMxRecords(String target_id)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			RowSet rowSet=dao.search("select kh_cyle,object_id,nbase  from per_target_mx where target_id="+target_id+" order by object_id,kh_cyle");
			String o="";
			StringBuffer str=new StringBuffer(",");
			while(rowSet.next())
			{
				String object_id=rowSet.getString("object_id");
				if(o.length()==0) {
                    o=object_id;
                }
				if(o.equals(object_id))
				{
					str.append(rowSet.getString("kh_cyle")+",");
				}
				else
				{
					map.put(o,str.toString());
					str.setLength(0);
					str.append(","+rowSet.getString("kh_cyle")+",");
					o=object_id;
				}
			}
			map.put(o, str.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	/**
	 * 取得考核对象信息
	 * @param right_fields
	 * @param cycle
	 * @return
	 */
	public HashMap getObjectInfoMap(String[] right_fields,String object_type)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			StringBuffer sub_str=new StringBuffer("");
			for(int i=0;i<right_fields.length;i++)
			{
				if("1".equals(object_type))   //团体
				{
					sub_str.append(",'"+right_fields[i].substring(2)+"'");
				}
				else if("2".equals(object_type))  //人员
				{
					sub_str.append(",'"+right_fields[i].substring(3)+"'");
				}
			}
			String sql="select *  from ";
			if("2".equals(object_type))   //人员
			{
				sql+=" UsrA01 where a0100 in ("+sub_str.substring(1)+")";
			}
			else if("1".equals(object_type))  //团体
			{
				sql+=" organization where codeitemid in ("+sub_str.substring(1)+")";
			}
			RowSet rowSet=dao.search(sql);
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				String object_id="";
				if("1".equals(object_type))   //团体
				{
					object_id= rowSet.getString("codeitemid");
					abean.set("object_id", rowSet.getString("codeitemid"));
					abean.set("nbase","");
					abean.set("b0110","");
					abean.set("e0122","");
					abean.set("a0100","");
					abean.set("a0101",rowSet.getString("codeitemdesc")!=null?rowSet.getString("codeitemdesc"):"");
					abean.set("a0000",rowSet.getString("a0000")!=null?rowSet.getString("a0000"):"");
				
				}
				else if("2".equals(object_type))   //人员
				{
					object_id=rowSet.getString("a0100");
					abean.set("object_id", rowSet.getString("a0100"));
					abean.set("nbase","Usr");
					abean.set("b0110",rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"");
					abean.set("e0122",rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"");
					abean.set("a0100",rowSet.getString("a0100"));
					abean.set("a0101",rowSet.getString("a0101")!=null?rowSet.getString("a0101"):"");
					abean.set("a0000",rowSet.getString("a0000")!=null?rowSet.getString("a0000"):"");
				}
				map.put(object_id,abean);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	
	/**      **********取得指标类别层次************/
	
	
	
	/**
	 *取得指标分类（定量/统一打分/基本型指标 所属的指标类别） 
	 * @return
	 */
	public ArrayList getPointClassList2()
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			ArrayList setList=new ArrayList();
			RowSet rowSet=dao.search("select pp.pointsetid,pps.pointsetname,pps.seq,pp.point_id from per_point pp,per_pointset pps where pp.pointsetid=pps.pointsetid and pp.pointkind=1 and pp.status=1 and ( pp.pointtype=0 or pp.pointtype is null ) order by pps.seq");
			HashMap map=new HashMap();
			while(rowSet.next())
			{
				String  point_id=rowSet.getString("point_id");
				String  pointsetid=rowSet.getString("pointsetid");
				if(!(this.userview.isSuper_admin()))
				{
					if(!this.userview.isHaveResource(IResourceConstant.KH_FIELD,point_id))
					{
						continue;
					}
				}
				map.put(pointsetid,new CommonData(rowSet.getString("pointsetid"),rowSet.getString("pointsetname")));
			}
			Set set=map.keySet();
			for(Iterator t=set.iterator();t.hasNext();)
			{
				CommonData data=(CommonData)map.get((String)t.next());
				String pointsetid=data.getDataValue();
				rowSet=dao.search("select * from per_point where pointkind=1 and  status=1 and (  pointtype=0 or pointtype is null ) and pointsetid="+pointsetid);
				ArrayList alist=new ArrayList();
				while(rowSet.next())
				{
					String Pointctrl=Sql_switcher.readMemo(rowSet,"Pointctrl");
					HashMap amap=PointCtrlXmlBo.getAttributeValues(Pointctrl);
					String point_id=rowSet.getString("point_id");
					String computeRule=(String)amap.get("computeRule");
					if(computeRule==null|| "0".equals(computeRule)) {
                        continue;
                    }
					if(!(this.userview.isSuper_admin()))
					{
						if(!this.userview.isHaveResource(IResourceConstant.KH_FIELD,point_id))
						{
							continue;
						}
					}
					alist.add(new CommonData(point_id,rowSet.getString("pointname")));
				}
				if(alist.size()>0) {
                    list.add(data);
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
	 *取得指标分类（定量/统一打分/基本型指标 所属的指标类别） 
	 * @return
	 */
	public ArrayList getPointClassList()
	{
		ArrayList list=new ArrayList();
		try
		{
			ArrayList pointSetList=getPerPointSetList();
			ContentDAO dao=new ContentDAO(this.con);
			ArrayList setList=new ArrayList();
			RowSet rowSet=dao.search("select pp.pointsetid from per_point pp,per_pointset pps where pp.pointsetid=pps.pointsetid and pp.pointkind=1 and pp.status=1 and ( pp.pointtype=0 or pp.pointtype is null ) order by pps.seq");
			while(rowSet.next()) {
                setList.add(rowSet.getString("pointsetid"));
            }
			
			for(int i=0;i<pointSetList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)pointSetList.get(i);
				String a_pointsetid=(String)abean.get("pointsetid");
				String a_parent_id=(String)abean.get("parent_id");
				String pointsetname=(String)abean.get("pointsetname");
				
				HashMap map=new HashMap();
				map.put("flag","0");
				if(a_parent_id.length()==0)
				{
						recursionTree(pointSetList,setList,a_pointsetid,map);
						if("1".equals((String)map.get("flag")))
						{
							list.add(new CommonData(a_pointsetid,pointsetname));
							executeSetList(pointSetList,setList,a_pointsetid,list,2);
							
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
	
	
	public void executeSetList(ArrayList pointSetList,ArrayList setList,String pointsetid,ArrayList list,int n)
	{
		LazyDynaBean abean1=null;
		
		for(int i=0;i<pointSetList.size();i++)
		{
			abean1=(LazyDynaBean)pointSetList.get(i);
			String a_pointsetid=(String)abean1.get("pointsetid");
			String a_parent_id=(String)abean1.get("parent_id");
			String pointsetname=(String)abean1.get("pointsetname");
			if(a_parent_id.equals(pointsetid))
			{
				HashMap map=new HashMap();
				map.put("flag","0");
				recursionTree(pointSetList,setList,a_pointsetid,map);
				if("1".equals((String)map.get("flag")))
				{
					StringBuffer str=new StringBuffer("");
					for(int j=0;j<n;j++) {
                        str.append("&nbsp;");
                    }
					list.add(new CommonData(a_pointsetid,str.toString()+pointsetname));
					executeSetList(pointSetList,setList,a_pointsetid,list,n+2);
					
				}
			}
		}
	}
	
	
	
	/**
	 * 判断节点下是否有已知的薪资类别
	 * @param pointSetList
	 * @param setList
	 * @param pointsetid
	 * @param layList
	 * @return
	 */
	public void recursionTree(ArrayList pointSetList,ArrayList setList,String pointsetid,HashMap map)
	{
	
		LazyDynaBean abean1=null;
		for(int j=0;j<setList.size();j++)
		{
			if(((String)setList.get(j)).equals(pointsetid))
			{
				map.put("flag","1");
				return;
			}
		}
		
		for(int i=0;i<pointSetList.size();i++)
		{
			abean1=(LazyDynaBean)pointSetList.get(i);
			String a_pointsetid=(String)abean1.get("pointsetid");
			String a_parent_id=(String)abean1.get("parent_id");
			if(a_parent_id.equals(pointsetid))
			{
				boolean isFlag=false;
				for(int j=0;j<setList.size();j++)
				{
					if(a_pointsetid.equals((String)setList.get(j)))
					{
						isFlag=true;
						break;
					}
				}
				if(isFlag)
				{
					map.put("flag","1");
					return;
				}
				else
				{
					recursionTree(pointSetList,setList,a_pointsetid,map);
				}
				
			}
		}
		
		
		
	}
	
	
	/**
	 * 取得绩效指标分类列表
	 * @return
	 */
	public ArrayList getPerPointSetList()
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			RowSet rowSet=dao.search("select *  from per_pointset where   Validflag=1 order by seq");
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				abean.set("pointsetid", rowSet.getString("pointsetid"));
				abean.set("pointsetname", rowSet.getString("pointsetname"));
				abean.set("parent_id", rowSet.getString("parent_id")!=null?rowSet.getString("parent_id"):"");
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**   ------------------------end---------------------------------   */
	
	
	
	
	
	
	
	
	public LazyDynaBean getDynaBean(String flag,String itemid,String type,String value,String desc)
	{
		LazyDynaBean abean=new LazyDynaBean();
		abean.set("flag",flag);
		abean.set("itemid",itemid);
		abean.set("type",type);
		abean.set("value",value);
		abean.set("desc",desc);
		return abean;
	}
	
	
	/**
	 * 取得目标任务书的字段列表
	 * @param targetid
	 * @param opt
	 * @return
	 */
	public ArrayList getTargetColumnList(String targetid,String opt)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			RowSet rowSet=null;
			
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			if("new".equals(opt))
			{
				list.add(getDynaBean("0","target_id","I",String.valueOf(DbNameBo.getPrimaryKey("per_target_list", "target_id", this.con)),""));
				list.add(getDynaBean("1","name","A","",ResourceFactory.getProperty("per.achivement.taskbook")));
				list.add(getDynaBean("1","cycle","I","0",ResourceFactory.getProperty("jx.khplan.cycle")));
				list.add(getDynaBean("1","theyear","A",String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),ResourceFactory.getProperty("jx.khplan.khyeardu")));
			//	list.add(getDynaBean("1","themonth","A","","考核月度"));
			//	list.add(getDynaBean("1","thequarter","A","","考核季度"));
				list.add(getDynaBean("0","create_user","A",this.userview.getA0100(),ResourceFactory.getProperty("jx.khplan.creator")));
				list.add(getDynaBean("0","create_date","D",df.format(new Date()),ResourceFactory.getProperty("jx.khplan.createTime")));
				list.add(getDynaBean("0","sp_flag","A","03",ResourceFactory.getProperty("jx.khplan.spstatus")));
				list.add(getDynaBean("1","object_type","A","2",ResourceFactory.getProperty("jx.khplan.objectype")));
				list.add(getDynaBean("0","b0110","A",this.userview.getUserDeptId(),ResourceFactory.getProperty("jx.khplan.createUserUn")));
			}
			else if("edit".equals(opt))
			{
				rowSet=dao.search("select *  from per_target_list where target_id="+targetid);
				if(rowSet.next())
				{
					list.add(getDynaBean("0","target_id","I",rowSet.getString("target_id"),""));
					list.add(getDynaBean("1","name","A",rowSet.getString("name"),ResourceFactory.getProperty("per.achivement.taskbook")));
					list.add(getDynaBean("1","cycle","I",rowSet.getString("cycle"),ResourceFactory.getProperty("jx.khplan.cycle")));
					list.add(getDynaBean("1","theyear","A",rowSet.getString("theyear"),ResourceFactory.getProperty("jx.khplan.khyeardu")));
				//	list.add(getDynaBean("1","themonth","A",rowSet.getString("themonth")!=null?rowSet.getString("themonth"):"","考核月度"));
				//	list.add(getDynaBean("1","thequarter","A",rowSet.getString("thequarter")!=null?rowSet.getString("thequarter"):"","考核季度"));
					list.add(getDynaBean("0","create_user","A",rowSet.getString("create_user")!=null?rowSet.getString("create_user"):"",ResourceFactory.getProperty("jx.khplan.creator")));
					list.add(getDynaBean("0","create_date","D",df.format(rowSet.getDate("create_date")),ResourceFactory.getProperty("jx.khplan.createTime")));
					list.add(getDynaBean("0","sp_flag","A",rowSet.getString("sp_flag")!=null?rowSet.getString("sp_flag"):"03",ResourceFactory.getProperty("jx.khplan.spstatus")));
					list.add(getDynaBean("1","object_type","A",rowSet.getString("object_type"),ResourceFactory.getProperty("jx.khplan.objectype")));
					list.add(getDynaBean("0","b0110","A",rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"",ResourceFactory.getProperty("jx.khplan.createUserUn")));
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
	 * 取得 指标与名称对应map
	 * @param right_fields
	 */
	public HashMap getPointToNameMap(String[] right_fields)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			StringBuffer subsql=new StringBuffer("");
			for(int i=0;i<right_fields.length;i++)
			{
				if(right_fields[i].length()>0) {
                    subsql.append(" or lower(point_id)='"+right_fields[i].toLowerCase()+"'");
                }
			}
			RowSet rowSet=dao.search("select * from per_point where pointkind=1 and status=1 and ("+subsql.substring(3)+")");
			while(rowSet.next()) {
                map.put(rowSet.getString("point_id").toLowerCase(),rowSet.getString("pointname"));
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 保存目标任务书标准值
	 * @param dataList
	 * @param pointList
	 * @param target_id
	 */
	public void saveTargetData(ArrayList dataList,ArrayList pointList,String target_id)
	{
		DbSecurityImpl dbS = new DbSecurityImpl();
		PreparedStatement pt= null;
		try
		{
			StringBuffer sql_sub=new StringBuffer("");
			for(int i=0;i<pointList.size();i++)
			{
				CommonData d=(CommonData)pointList.get(i);
				sql_sub.append(",T_"+d.getDataValue()+"=?");
			}
			String sql = "update per_target_mx set "+sql_sub.substring(1)+" where target_id=? and object_id=? and kh_cyle=?";
			pt=this.con.prepareStatement(sql);
			for(int i=0;i<dataList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)dataList.get(i);
				for(int j=0;j<pointList.size();j++)
				{
					CommonData d=(CommonData)pointList.get(j);
					String value=(String)abean.get(d.getDataValue());
					if(value!=null&&value.trim().length()>0){
						if(!"no".equalsIgnoreCase(value)) {
                            pt.setFloat(j+1,Float.parseFloat(value));
                        } else {
                            pt.setFloat(j+1,0);
                        }
					}	
					else {
                        pt.setFloat(j+1,0);
                    }
				}
				pt.setInt(pointList.size()+1,Integer.parseInt(target_id));
				pt.setString(pointList.size()+2, (String)abean.get("object_id"));
				pt.setString(pointList.size()+3,(String)abean.get("kh_cyle"));
				pt.addBatch();
			}
			// 打开Wallet
			dbS.open(this.con, sql);
			pt.executeBatch();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(pt);
			try {
				// 关闭Wallet
				dbS.close(this.con);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public ArrayList getCycleList(int cycle)
	{
		ArrayList list=new ArrayList();
		list.add(new CommonData("-1",ResourceFactory.getProperty("hire.jp.pos.all")));
		if(cycle==1)
		{
			list.add(new CommonData("1",ResourceFactory.getProperty("report.pigeonhole.uphalfyear")));
			list.add(new CommonData("2",ResourceFactory.getProperty("report.pigeonhole.downhalfyear")));
		}
		if(cycle==2)
		{
			list.add(new CommonData("01",ResourceFactory.getProperty("report.pigionhole.oneQuarter")));
			list.add(new CommonData("02",ResourceFactory.getProperty("report.pigionhole.twoQuarter")));
			list.add(new CommonData("03",ResourceFactory.getProperty("report.pigionhole.threeQuarter")));
			list.add(new CommonData("04",ResourceFactory.getProperty("report.pigionhole.fourQuarter")));
		}
		if(cycle==3)
		{
			for(int i=1;i<10;i++) {
                list.add(new CommonData("0"+i,i+ResourceFactory.getProperty("columns.archive.month")));
            }
			list.add(new CommonData("10","10"+ResourceFactory.getProperty("columns.archive.month")));
			list.add(new CommonData("11","11"+ResourceFactory.getProperty("columns.archive.month")));
			list.add(new CommonData("12","12"+ResourceFactory.getProperty("columns.archive.month")));
		}
		return list;
	}
	
	
	
	
	
	
	
	/**
	 * 取得目标任务书数据
	 * @param pointList
	 * @param target_id
	 * @return
	 */
	public ArrayList getTargetDataList(ArrayList pointList,String target_id,String sql_whl,String acycle)
	{
		sql_whl = PubFunc.keyWord_reback(sql_whl);
		ArrayList dataList=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			int cycle=0;
			String sql="select *  from per_target_list where target_id="+target_id;			
			RowSet  rowSet=dao.search(sql);
			String cycle_str="";
			String theyear="";
			String object_type="";
			if(rowSet.next())
			{
				cycle=rowSet.getInt("cycle");
				theyear=rowSet.getString("theyear");
				object_type=rowSet.getString("object_type");
			}
			StringBuffer sql0=new StringBuffer("select per_target_mx.* from per_target_mx ");
			if("2".equals(object_type)) {
                sql0.append(",UsrA01 where per_target_mx.object_Id=UsrA01.a0100   ");
            } else {
                sql0.append(",organization where per_target_mx.object_Id=organization.codeitemid   ");
            }
			
			sql0.append(" and  per_target_mx.target_id="+target_id);
			if(acycle!=null&&!"-1".equals(acycle)) {
                sql0.append(" and per_target_mx.kh_cyle='"+acycle+"'");
            }
			if(sql_whl!=null&&sql_whl.length()>0) {
                sql0.append(" and ( "+sql_whl+" )");
            }
			
			// 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
			String operOrg = this.userview.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if("2".equals(object_type))
			{
				StringBuffer buf = new StringBuffer();				
				if (operOrg!=null && operOrg.length() > 3)
				{					 
					StringBuffer tempSql = new StringBuffer("");
					String[] temp = operOrg.split("`");
					for (int i = 0; i < temp.length; i++)
					{
						if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
                            tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
                        } else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2))) {
                            tempSql.append(" or  e0122 like '" + temp[i].substring(2) + "%'");
                        }
					}
					buf.append(" select usra01.A0100 from usra01 where  ( " + tempSql.substring(3) + " ) ");
					 
				}
				else if((!this.userview.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
				{
					String priStrSql = InfoUtils.getWhereINSql(this.userview,"Usr");
					if(priStrSql.length()>0)
					{
						buf.append("select usra01.A0100 ");
						buf.append(priStrSql);
					}
				}
				if(buf.length()>0)
				{
					sql0.append(" and per_target_mx.object_Id in ("+buf.toString()+") ");
				}

			}
			else
			{
				 if (operOrg!=null && operOrg.length() > 3)
				 {
					StringBuffer tempSql = new StringBuffer("");
					String[] temp = operOrg.split("`");
					for (int i = 0; i < temp.length; i++)
					{
					    tempSql.append(" or per_target_mx.object_Id like '" + temp[i].substring(2) + "%'");
					}
					sql0.append(" and ( " + tempSql.substring(3) + " ) ");
				 }
				 else if((!this.userview.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
				 {
					String codeid=userview.getManagePrivCode();
					String codevalue=userview.getManagePrivCodeValue();
					String a_code=codeid+codevalue;
					
					if(a_code.trim().length()==0)
					{
						sql0.append(" and 1=2 ");
					}
					else if(!("UN".equals(a_code)))
					{
							sql0.append(" and per_target_mx.object_Id like '"+codevalue+"%' "); 
							
					}
				 }				
			}			
			
			sql0.append(" order by per_target_mx.kh_cyle");
			if("2".equals(object_type)) {
                sql0.append(",Usra01.a0000");
            } else {
                sql0.append(",organization.a0000");
            }
			rowSet=dao.search(sql0.toString());
			
			LazyDynaBean abean=null;
			DecimalFormat myformat1 = new DecimalFormat("########.###");//
			int n=0;
			Permission p=new Permission(this.con,this.userview);  //Nov 03 JinChunhai修改
			while(rowSet.next())
			{
				String object_id=rowSet.getString("object_id");
				abean=new LazyDynaBean();
				abean.set("a0101",rowSet.getString("a0101"));
				abean.set("target_id",rowSet.getString("target_id"));
				abean.set("nbase",rowSet.getString("NBASE"));
				abean.set("object_id",object_id);
				abean.set("object_type",object_type);
				abean.set("kh_cyle",rowSet.getString("kh_cyle"));
				abean.set("cycle_str",getCycle_str(rowSet.getString("kh_cyle"),cycle,theyear));
				String b0110=rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"";
				String e0122=rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"";
				
				for(int i=0;i<pointList.size();i++)
				{
					CommonData d=(CommonData)pointList.get(i);
					boolean right = true;
					if(!"2".equals(object_type))  // 非2 团队
                    {
                        right = p.getPrivPoint("", object_id, d.getDataValue());
                    } else if("2".equals(object_type))  // 2 人员
                    {
                        right = p.getPrivPoint(b0110, e0122, d.getDataValue());
                    }
					if(right==true)
					{
						if(rowSet.getString("T_"+d.getDataValue())!=null&&rowSet.getFloat("T_"+d.getDataValue())==0) {
                            abean.set(d.getDataValue(),"0");
                        } else {
                            abean.set(d.getDataValue(),rowSet.getString("T_"+d.getDataValue())!=null&&rowSet.getFloat("T_"+d.getDataValue())!=0?myformat1.format(rowSet.getDouble("T_"+d.getDataValue())):"");
                        }
					
					}else{
						abean.set(d.getDataValue(),"no");
					}
															
//					if(rowSet.getString("T_"+d.getDataValue())!=null&&rowSet.getFloat("T_"+d.getDataValue())==0)
//						abean.set(d.getDataValue(),"0");
//					else
//						abean.set(d.getDataValue(),rowSet.getString("T_"+d.getDataValue())!=null&&rowSet.getFloat("T_"+d.getDataValue())!=0?myformat1.format(rowSet.getDouble("T_"+d.getDataValue())):"");
				
				}
				if("2".equals(object_type))
				{
					b0110=AdminCode.getCodeName("UN", b0110);
					e0122=AdminCode.getCodeName("UM", e0122);
				}
				abean.set("b0110",b0110);
				abean.set("e0122",e0122);
				abean.set("index",String.valueOf(n));
				dataList.add(abean);
				n++;
			}
			
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return dataList;
	}
	
	//取得目标任务明细记录的 考核期间
	public String getCycle_str(String kh_cycle,int cycle,String year)
	{
		String str=
			str=year+ResourceFactory.getProperty("columns.archive.year");
		if(cycle==1)
		{	
			if("1".equals(kh_cycle)) {
                str+=ResourceFactory.getProperty("report.pigeonhole.uphalfyear");
            } else if("2".equals(kh_cycle)) {
                str+=ResourceFactory.getProperty("report.pigeonhole.downhalfyear");
            }
		}
		else if(cycle==2)
		{	
			if("01".equals(kh_cycle)) {
                str+=ResourceFactory.getProperty("report.pigionhole.oneQuarter");
            } else if("02".equals(kh_cycle)) {
                str+=ResourceFactory.getProperty("report.pigionhole.twoQuarter");
            } else if("03".equals(kh_cycle)) {
                str+=ResourceFactory.getProperty("report.pigionhole.threeQuarter");
            } else if("04".equals(kh_cycle)) {
                str+=ResourceFactory.getProperty("report.pigionhole.fourQuarter");
            }
		}
		else if(cycle==3)
		{	
			if("10".equals(kh_cycle)|| "11".equals(kh_cycle)|| "12".equals(kh_cycle)) {
                str+=kh_cycle+ResourceFactory.getProperty("columns.archive.month");
            } else {
                str+=kh_cycle.substring(1)+ResourceFactory.getProperty("columns.archive.month");
            }
		}
		return str;
	}
	
	
	
	/**
	 * 取得目标任务书对应的要素 列表
	 * @param target_id
	 * @return
	 */
	public ArrayList getTargetPointList(String target_id)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			RowSet rowSet=dao.search("select * from per_target_point where target_id="+target_id+" order by seq");
			while(rowSet.next())
			{
				if(!(this.userview.isSuper_admin()))
				{
					if(!this.userview.isHaveResource(IResourceConstant.KH_FIELD,rowSet.getString("point_id")))
					{
						continue;
					}
				}
				list.add(new CommonData(rowSet.getString("point_id"),rowSet.getString("pointname")));
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
	
	/**
	 * 取得目标任务书对应的有权限的指标
	 * @param target_id
	 * Auther JinChunhai
	 * @return
	 */
	public ArrayList getBigTargetPointList(String target_id,String orgCode,String object_type)
	{
		ArrayList list=new ArrayList();
		RowSet rowSet;
		RowSet rs;
		try
		{
			ContentDAO dao=new ContentDAO(this.con);			
			StringBuffer strSql=new StringBuffer("select DISTINCT object_id,b0110,e0122 from per_target_mx where 1=1 ");						
			if(orgCode!=null && orgCode.length()>0) {
                strSql.append(" and object_id='"+orgCode+"' and target_id="+target_id);
            } else if("2".equals(object_type))  // 2 人员
            {
                strSql.append(" and A0000=(select min(A0000) A0000 from per_target_mx where target_id="+target_id+") and target_id="+target_id);
            } else {
                strSql.append(" and A0000=(select min(A0000) A0000 from per_target_mx where target_id="+target_id+") and target_id="+target_id);
            }
			rs=dao.search(strSql.toString());												
			rowSet=dao.search("select * from per_target_point where target_id="+target_id+" order by seq");
			
			Permission p=new Permission(this.con,this.userview);
			while(rs.next())
			{				
				String object_id=rs.getString("object_id");
				String b0110=rs.getString("b0110")!=null?rs.getString("b0110"):"";
				String e0122=rs.getString("e0122")!=null?rs.getString("e0122"):"";												
				while(rowSet.next())
				{
					if(!(this.userview.isSuper_admin()))
					{
						if(!this.userview.isHaveResource(IResourceConstant.KH_FIELD,rowSet.getString("point_id")))
						{
							continue;
						}
					}					
					boolean right = true;
					if(!"2".equals(object_type))  // 非2 团队
                    {
                        right = p.getPrivPoint("", object_id, rowSet.getString("point_id"));
                    } else if("2".equals(object_type))  // 2 人员
                    {
                        right = p.getPrivPoint(b0110, e0122, rowSet.getString("point_id"));
                    }
					if(right==true)
					{
						list.add(new CommonData(rowSet.getString("point_id"),rowSet.getString("pointname")));
					}else{
						
					}					
				}				
			}
			if(rowSet!=null) {
                rowSet.close();
            }
			if(rs!=null) {
                rs.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 新建 或 修改 绩效目标表
	 * @param opt new/edit
	 * @param right_fields
	 * @param targetColumnList
	 */
	public String SaveAchivementTask(String opt,String[] right_fields,ArrayList targetColumnList)
	{
		String root_url=ResourceFactory.getProperty("per.achivement.achivementbook");
		
		try
		{
			String object_type="";
			String theyear="";
			String name="";
			ContentDAO dao=new ContentDAO(this.con);
			LazyDynaBean abean=(LazyDynaBean)targetColumnList.get(0);
			String target_id=(String)abean.get("value");
			dao.delete("delete from per_target_list where target_id="+(String)abean.get("value"),new ArrayList());
			dao.delete("delete from per_target_point where target_id="+(String)abean.get("value"),new ArrayList());
		
			synchroTargetMxTable(right_fields);
			// add (per_target_list) record
			RecordVo vo=new RecordVo("per_target_list");
			for(int i=0;i<targetColumnList.size();i++)
			{
				abean=(LazyDynaBean)targetColumnList.get(i);
				String itemid=(String)abean.get("itemid");
				String type=(String)abean.get("type");
				String value=(String)abean.get("value");
				if("I".equals(type)&&value.trim().length()>0) {
                    vo.setInt(itemid,Integer.parseInt(value));
                }
				if("A".equals(type)&&value.trim().length()>0) {
                    vo.setString(itemid,value);
                }
				if("D".equals(type)&&value.trim().length()>0)
				{
					String[] temps=value.split("-");
					Calendar c= Calendar.getInstance();
					c.set(Calendar.YEAR,Integer.parseInt(temps[0]));
					c.set(Calendar.MONTH,Integer.parseInt(temps[1])+1);
					c.set(Calendar.DATE,Integer.parseInt(temps[2]));
					vo.setDate(itemid,c.getTime());
				}
				if("object_type".equalsIgnoreCase(itemid)) {
                    object_type=value;
                }
				if("theyear".equalsIgnoreCase(itemid)) {
                    theyear=value;
                }
				if("name".equalsIgnoreCase(itemid)) {
                    name=value;
                }
			}
			dao.addValueObject(vo);
			//  add (per_target_point) record
			HashMap point_nameMap=getPointToNameMap(right_fields);
			int n=0;
			ArrayList list=new ArrayList();
			for(int i=0;i<right_fields.length;i++)
			{
				if(right_fields[i].length()>0)
				{
					n++;
					RecordVo vo2=new RecordVo("per_target_point");
					vo2.setString("point_id",right_fields[i]);
					vo2.setInt("target_id",Integer.parseInt(target_id));
					vo2.setString("pointname",(String)point_nameMap.get(right_fields[i].toLowerCase()));
					vo2.setInt("seq",n);
					list.add(vo2);
				}
			}
			dao.addValueObject(list);
			if("1".equals(object_type))// 1\2\4\3  团队\人员\部门\单位
            {
                root_url+="/"+ResourceFactory.getProperty("jx.jifen.group");
            } else if("2".equals(object_type)) {
                root_url+="/"+ResourceFactory.getProperty("jx.jifen.person");
            } else if("4".equals(object_type)) {
                root_url+="/"+ResourceFactory.getProperty("tree.umroot.umdesc");
            } else if("3".equals(object_type)) {
                root_url+="/"+ResourceFactory.getProperty("tree.unroot.undesc");
            }
			
			
			
			root_url+="/"+theyear;
			root_url+="/"+target_id+"`"+name;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return root_url;
	}
	
	/**
	 * 同步目标任务明细表
	 *
	 */
	public void synchroTargetMxTable(String[] right_fields)
	{
		try
		{
			DbWizard dbWizard=new DbWizard(this.con); 
			ContentDAO dao = new ContentDAO(this.con);
			ResultSet resultSet=dao.search("select * from per_target_mx where 1=2");
			ResultSetMetaData mt=resultSet.getMetaData();
			HashMap existColumnMap=new HashMap();
			for(int i=0;i<mt.getColumnCount();i++)
			{
				String columnName=mt.getColumnName(i+1);
				existColumnMap.put(columnName.toLowerCase(),"1");
			}
			
			Table table=new Table("per_target_mx");
			int n=0;
			for(int i=0;i<right_fields.length;i++)
			{
				if(right_fields[i]!=null&&right_fields[i].length()>0&&existColumnMap.get("t_"+right_fields[i].toLowerCase())==null)
				{
					n++;
					table.addField(getField("T_"+right_fields[i],"T_"+right_fields[i]));
					existColumnMap.put("t_"+right_fields[i].toLowerCase(),"1");
				}
			}
			if(n>0) {
                dbWizard.addColumns(table);
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	public Field getField(String fieldname,String desc)
	{
		Field obj=new Field(fieldname,desc);	
		obj.setDatatype(DataType.FLOAT);
		obj.setDecimalDigits(6);
		obj.setLength(15);							
		obj.setKeyable(false);			
		obj.setVisible(false);							
		obj.setAlign("left");
		return obj;
	}
	
	/**
	 * 取得查询目标任务书数据的Sql语句
	 * @return
	 */
	public String getTargetDataListSql(ArrayList pointList,String target_id,String sql_whl,String acycle)
	{
		sql_whl = PubFunc.keyWord_reback(sql_whl);
		String targetDataListSql = "";
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			int cycle=0;
			String sql="select *  from per_target_list where target_id="+target_id;			
			RowSet  rowSet=dao.search(sql);
			String cycle_str="";
			String theyear="";
			String object_type="";
			if(rowSet.next())
			{
				cycle=rowSet.getInt("cycle");
				theyear=rowSet.getString("theyear");
				object_type=rowSet.getString("object_type");
			}
			StringBuffer sql0=new StringBuffer("select per_target_mx.* from per_target_mx ");
			if("2".equals(object_type)) {
                sql0.append(",UsrA01 where per_target_mx.object_Id=UsrA01.a0100   ");
            } else {
                sql0.append(",organization where per_target_mx.object_Id=organization.codeitemid   ");
            }
			
			sql0.append(" and  per_target_mx.target_id="+target_id);
			if(acycle!=null&&!"-1".equals(acycle)) {
                sql0.append(" and per_target_mx.kh_cyle='"+acycle+"'");
            }
			if(sql_whl!=null&&sql_whl.length()>0) {
                sql0.append(" and ( "+sql_whl+" )");
            }
			
			// 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
			String operOrg = this.userview.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if("2".equals(object_type))
			{
				StringBuffer buf = new StringBuffer();				
				if (operOrg!=null && operOrg.length() > 3)
				{					 
					StringBuffer tempSql = new StringBuffer("");
					String[] temp = operOrg.split("`");
					for (int i = 0; i < temp.length; i++)
					{
						if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
                            tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
                        } else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2))) {
                            tempSql.append(" or  e0122 like '" + temp[i].substring(2) + "%'");
                        }
					}
					buf.append(" select usra01.A0100 from usra01 where  ( " + tempSql.substring(3) + " ) ");					 
				}
				else if((!this.userview.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
				{
					String priStrSql = InfoUtils.getWhereINSql(this.userview,"Usr");
					if(priStrSql.length()>0)
					{
						buf.append("select usra01.A0100 ");
						buf.append(priStrSql);
					}
				}
				if(buf.length()>0)
				{
					sql0.append(" and per_target_mx.object_Id in ("+buf.toString()+") ");
				}

			}
			else
			{
				 if (operOrg!=null && operOrg.length() > 3)
				 {
					 StringBuffer tempSql = new StringBuffer("");
					 String[] temp = operOrg.split("`");
					 for (int i = 0; i < temp.length; i++)
					 {
						 tempSql.append(" or per_target_mx.object_Id like '" + temp[i].substring(2) + "%'");
					 }
					 sql0.append(" and ( " + tempSql.substring(3) + " ) ");
				 }
				 else if((!this.userview.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
				 {
					 String codeid=userview.getManagePrivCode();
					 String codevalue=userview.getManagePrivCodeValue();
					 String a_code=codeid+codevalue;
					
					 if(a_code.trim().length()==0)
					 {
						 sql0.append(" and 1=2 ");
					 }
					 else if(!("UN".equals(a_code)))
					 {
						 sql0.append(" and per_target_mx.object_Id like '"+codevalue+"%' "); 
							
					 }
				 }				
			}			
			
			sql0.append(" order by per_target_mx.kh_cyle");
			if("2".equals(object_type)) {
                sql0.append(",Usra01.a0000");
            } else {
                sql0.append(",organization.a0000");
            }
			
			targetDataListSql = sql0.toString();					
		
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return targetDataListSql;
	}
}
