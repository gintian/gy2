package com.hjsj.hrms.businessobject.competencymodal;

import com.hjsj.hrms.businessobject.competencymodal.personPostModal.PersonPostModalBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.MyObjectiveBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;



public class PostModalBo
{

	private int td_width = 100;  // 行宽
	private Connection conn = null;
	private UserView userView = null;
	private String plan_id = "";                   // 考核计划 ID
	private RecordVo plan_vo = null;               // 考核计划信息vo
	private RecordVo template_vo = null;           // 考核模板信息vo
	DecimalFormat myformat = new DecimalFormat("########.########"); //
	private String byModel="";

	public PostModalBo(Connection conn,UserView userView)
	{
		this.conn=conn;
		this.userView=userView;
	}

	public PostModalBo(Connection conn,UserView userView,String plan_id)
	{
		this.conn=conn;
		this.userView=userView;
		this.plan_id=plan_id;
		this.plan_vo=getPlanVo(plan_id);
		this.template_vo=getTemplateVo(this.plan_vo.getString("template_id"));
	}
	public  PostModalBo(Connection conn,UserView userView,String plan_id,String byModel)
	{
		this.conn=conn;
		this.userView=userView;
		this.plan_id=plan_id;
		this.plan_vo=getPlanVo(plan_id);
		this.template_vo=getTemplateVo(this.plan_vo.getString("template_id"));
		this.byModel=byModel;
	}
	public ArrayList getCompetencyModalList(String codeitemid,String object_type,String codesetid,String historyDate)
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{
			ContentDAO dao  = new ContentDAO(this.conn);
			String tableName="codeitem";
			if("3".equals(object_type))
			{
				codesetid="@K";
				tableName="organization";
			}
			this.tableName=tableName;
			StringBuffer buf = new StringBuffer("");
			buf.append(" select  pcm.object_type,pcm.object_id,pcm.point_id,pcm.score,pcm.point_type,pcm.gradecode,"+Sql_switcher.isnull("pcm.rank", "0")+"*100 as rank,");
			buf.append(" pp.pointname,codeitem.codeitemdesc,per_grade.gradedesc,codeitem2.codeitemdesc postname ");
			buf.append(" from per_competency_modal pcm left join per_point pp on pcm.point_id=pp.point_id ");
			buf.append(" left join (select codeitemid,codeitemdesc from codeitem where codesetid='70') codeitem  on pcm.point_type=codeitem.codeitemid left join per_grade on ");
			buf.append(" pcm.point_id=per_grade.point_id and pcm.gradecode=per_grade.gradecode ");
			buf.append(" left join (select codeitemid,codeitemdesc from "+tableName+" where UPPER(codesetid)='"+codesetid.toUpperCase()+"') codeitem2 on pcm.object_id=codeitem2.codeitemid ");
			buf.append(" where (pcm.object_id='"+codeitemid+"'");
			if(!"3".equals(object_type))
			{
				ArrayList parentLinkList=new ArrayList();
				this.getParentBean(codesetid, codeitemid, parentLinkList);
				for(int i=0;i<parentLinkList.size();i++)
				{
					if(i==0)
						buf.append(" or ((");
					LazyDynaBean bean=(LazyDynaBean)parentLinkList.get(i);
					String id=(String)bean.get("id");
					if(i!=0)
						buf.append(" or ");
					buf.append("  pcm.object_id='"+id+"'");
					if(i==parentLinkList.size()-1)
					{
						buf.append(")");
						buf.append(" and UPPER(pcm.point_id) not in(select UPPER(point_id) from per_competency_modal where object_type="+object_type+" and object_id='"+codeitemid+"'");
						buf.append(" and "+Sql_switcher.dateValue(historyDate)+" between start_date and end_date)");
						buf.append(")");
					}
				}
			}
			buf.append(")");
			buf.append(" and pcm.object_type="+object_type);
			buf.append(" and "+Sql_switcher.dateValue(historyDate)+" between start_date and end_date");
			buf.append(" order by object_id,pcm.point_id");
//			System.out.println(buf.toString());
			rs = dao.search(buf.toString());
			while(rs.next())
			{
				LazyDynaBean bean  = new LazyDynaBean();
				bean.set("postname", rs.getString("postname")==null?"":rs.getString("postname"));
				bean.set("object_id", rs.getString("object_id"));
				String point_id=rs.getString("point_id");
				if(!this.userView.isSuper_admin()&&!this.userView.isHaveResource(IResourceConstant.KH_FIELD,point_id))
					continue;
				bean.set("point_id", rs.getString("point_id"));
				bean.set("object_type",rs.getString("object_type"));
				bean.set("score",PubFunc.round(rs.getString("score")==null|| "".equals(rs.getString("score"))?"0":rs.getString("score"),2));
				bean.set("pointname", rs.getString("pointname")==null?"":rs.getString("pointname"));
				bean.set("codeitemdesc", rs.getString("codeitemdesc")==null?"":rs.getString("codeitemdesc"));
				String gradedesc=rs.getString("gradedesc")==null?"":rs.getString("gradedesc");
				if(gradedesc.indexOf(":")!=-1)
				{
					gradedesc=gradedesc.substring(0,gradedesc.indexOf(":"));
				}else if(gradedesc.indexOf("：")!=-1)
				{
					gradedesc=gradedesc.substring(0,gradedesc.indexOf("："));
				}
				//smk 2015.12.10
				if (gradedesc.length()>15)
					gradedesc = gradedesc.substring(0,15)+"...";
				bean.set("gradedesc",gradedesc);
				String desc=rs.getString("gradedesc")==null?"":rs.getString("gradedesc");
				bean.set("rank", PubFunc.round(rs.getString("rank")==null|| "".equals(rs.getString("rank"))?"0":rs.getString("rank"), 2)+"%");
				bean.set("idArray",rs.getString("point_id")+"`"+rs.getString("object_type")+"`"+rs.getString("object_id"));
				String isParent="0";
				if(!codeitemid.equalsIgnoreCase(rs.getString("object_id")))
					isParent="1";
				bean.set("isParent",isParent);
				bean.set("fulldesc",SafeCode.encode(desc));
				if(desc.length()>0)
					bean.set("ishave","1");
				else
					bean.set("ishave","0");
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
	/**
	 * 新建岗位素质模型指标
	 * @param object_id
	 * @param object_type
	 * @param points
	 */
	public void savePoints(String object_id,String object_type,String points)
	{
		try
		{
			String[] arr = points.split(",");
			ContentDAO dao = new ContentDAO(this.conn);
			String tmp="9999-12-31";
			ArrayList addList  = new ArrayList();
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss   看到岗位素质模型等地方的日期都是不带时分秒的   所以这里也不带   编码between  and   时候查的不够准确  zhaoxg 2014-4-10
			//calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-1);
			String nowDate=sdf.format(calendar.getTime());
			String sql="";
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i]))
					continue;

				sql="insert into per_competency_modal (object_type,object_id,point_id,start_date,end_date)" +
						" values("+object_type+",'"+object_id+"','"+arr[i]+"',"+Sql_switcher.dateValue(nowDate)+","+Sql_switcher.dateValue(tmp)+")  ";
				addList.add(sql);
			}
			dao.batchUpdate(addList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 删除岗位素质模型指标
	 * @param str
	 */
	public void delPostModal(String str)
	{
		try
		{
			ContentDAO dao  = new ContentDAO(this.conn);
			if(str==null|| "".equals(str))
				return;
			String[] tmp = str.split("~");
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String nowDate=sdf.format(calendar.getTime());
			calendar.add(Calendar.DATE, -1);    //取当前日期的前一天，这样置为无效后界面刷新就看不见了  zhaoxg add 2014-12-20
			String date=sdf.format(calendar.getTime());
			String sql="";
			ResultSet res=null;
			ArrayList sqlList=new ArrayList();
			for(int i=0;i<tmp.length;i++)
			{
				String s=tmp[i];
				if(s==null|| "".equals(s))
					continue;
				String[] inner_tmp = s.split("`");

				/**************先判断启动时间在岗位素质模型指标有效期内的素质计划是否引用了岗位素质模型指标打过分******************/
				StringBuffer sb=new StringBuffer();
				sb.append(" select pcm.point_id from per_plan pp,per_object po,per_competency_modal pcm ,per_ScoreDetail psd");
				sb.append(" where pcm.point_id='"+inner_tmp[0]+"' and pcm.object_type="+Integer.parseInt(inner_tmp[1])+" and pcm.object_id='"+inner_tmp[2]+"'  and "+Sql_switcher.dateValue(nowDate)+" between pcm.start_date and pcm.end_date ");
				sb.append(" and pp.execute_date between pcm.start_date and pcm.end_date");
				sb.append(" and pp.busitype=1");
				sb.append(" and pp.plan_id=po.plan_id");
				sb.append(" and po.E01A1=pcm.object_id");
				sb.append(" and pp.plan_id=psd.plan_id");
				sb.append(" and pcm.point_id=psd.point_id");
				res=dao.search(sb.toString());
				if(res.next()){//有素质计划引用了此指标,则将此指标置为过期
					sql=" update per_competency_modal set end_date="+Sql_switcher.dateValue(date)+" where object_type="+inner_tmp[1]+" and point_id = '"+inner_tmp[0]+"' and object_id = '"+inner_tmp[2]+"'  and "+Sql_switcher.dateValue(nowDate)+" between start_date and end_date ";
					sqlList.add(sql);
				}else{
					/**************再判断归档时间在岗位素质模型指标有效期内的素质计划是否引用了岗位素质模型指标*****************/
					sb.setLength(0);
					sb.append(" select pcm.point_id from per_plan pp,per_history_result phr,per_competency_modal pcm");
					sb.append(" where pcm.point_id='"+inner_tmp[0]+"' and pcm.object_type="+Integer.parseInt(inner_tmp[1])+" and pcm.object_id='"+inner_tmp[2]+"'  and "+Sql_switcher.dateValue(nowDate)+" between pcm.start_date and pcm.end_date ");
					sb.append(" and phr.archive_date between pcm.start_date and pcm.end_date");
					sb.append(" and pp.busitype=1");
					sb.append(" and pp.plan_id=phr.plan_id");
					sb.append(" and phr.E01A1=pcm.object_id");
					sb.append(" and phr.point_id=pcm.point_id");
					res=dao.search(sb.toString());
					if(res.next()){
						sql=" update per_competency_modal set end_date="+Sql_switcher.dateValue(date)+" where object_type="+inner_tmp[1]+" and point_id = '"+inner_tmp[0]+"' and object_id = '"+inner_tmp[2]+"'  and "+Sql_switcher.dateValue(nowDate)+" between start_date and end_date ";
						sqlList.add(sql);
					}else{//没有素质计划引用则直接删除
						sql=" delete from per_competency_modal where object_type="+inner_tmp[1]+" and object_id = '"+inner_tmp[2]+"' and point_id = '"+inner_tmp[0]+"'  and "+Sql_switcher.dateValue(nowDate)+" between start_date and end_date ";
						sqlList.add(sql);
					}
				}

			}
			if(sqlList.size()>0)
				dao.batchUpdate(sqlList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 *
	 * @param object_type 模型类别=1职务 =2岗位序列  =3岗位
	 * @param codeitemid
	 * @param point_id
	 * @return
	 */
	public ArrayList getPostModalInfo(String object_type,String codeitemid,String point_id,String codesetid)
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		String nowDate=sdf.format(calendar.getTime());
		try
		{
			ContentDAO dao  = new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer("");
			String tableName="codeitem";
			if("3".equals(object_type))
			{
				codesetid="@K";
				tableName="organization";
			}
			buf.append(" select  pcm.object_type,pcm.object_id,pcm.point_id,pcm.score,pcm.point_type,pcm.gradecode,"+Sql_switcher.isnull("pcm.rank", "0")+"*100 rank,");
			buf.append(" pp.pointname,codeitem.codeitemdesc,per_grade.gradedesc,codeitem2.codeitemdesc postname ");
			buf.append(" from per_competency_modal pcm left join per_point pp on pcm.point_id=pp.point_id ");
			buf.append(" left join (select codeitemid,codeitemdesc from codeitem where codesetid='70') codeitem  on pcm.point_type=codeitem.codeitemid left join per_grade on ");
			buf.append(" pcm.point_id=per_grade.point_id and pcm.gradecode=per_grade.gradecode ");
			buf.append(" left join (select codeitemid,codeitemdesc from "+tableName+" where UPPER(codesetid)='"+codesetid.toUpperCase()+"') codeitem2 on pcm.object_id=codeitem2.codeitemid ");
			buf.append(" where pcm.object_id='"+codeitemid+"'");
			buf.append(" and pcm.object_type="+object_type);
			buf.append(" and UPPER(pcm.point_id)='"+point_id.toUpperCase()+"'");
			buf.append(" and "+Sql_switcher.dateValue(nowDate)+" between pcm.start_date and pcm.end_date");
			rs = dao.search(buf.toString());
			while(rs.next())
			{
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("itemid","postname");
				abean.set("itemdesc",ResourceFactory.getProperty("competency.modal.postseq"));
				abean.set("editable","0");//=0不需要输入和保存的=1需要保存的
				abean.set("itemtype","A");
				abean.set("codesetid","0");
				abean.set("value",rs.getString("postname")==null?"":rs.getString("postname"));
				abean.set("viewvalue", "");
				list.add(abean);
				//
				abean = new LazyDynaBean();
				abean.set("itemid","point_type");
				abean.set("itemdesc","指标分类");
				abean.set("editable","1");//=0不需要输入和保存的=1需要保存的
				abean.set("itemtype","A");
				abean.set("codesetid","70");
				abean.set("value",rs.getString("point_type")==null?"":rs.getString("point_type"));
				abean.set("viewvalue", rs.getString("codeitemdesc")==null?"":rs.getString("codeitemdesc"));
				list.add(abean);
				//
				abean = new LazyDynaBean();
				abean.set("itemid","pointid");
				abean.set("itemdesc",ResourceFactory.getProperty("competency.modal.fieldcode"));
				abean.set("editable","0");//=0不需要输入和保存的=1需要保存的
				abean.set("itemtype","A");
				abean.set("codesetid","0");
				abean.set("value",rs.getString("point_id")==null?"":rs.getString("point_id"));
				abean.set("viewvalue", "");
				list.add(abean);
				//
				abean = new LazyDynaBean();
				abean.set("itemid","pointname");
				abean.set("itemdesc",ResourceFactory.getProperty("kh.field.field_n"));
				abean.set("editable","0");//=0不需要输入和保存的=1需要保存的
				abean.set("itemtype","A");
				abean.set("codesetid","0");
				abean.set("value",rs.getString("pointname")==null?"":rs.getString("pointname"));
				abean.set("viewvalue", "");
				list.add(abean);
				//
				abean = new LazyDynaBean();
				abean.set("itemid","score");
				abean.set("itemdesc",ResourceFactory.getProperty("competency.modal.score"));
				abean.set("editable","1");//=0不需要输入和保存的=1需要保存的
				abean.set("itemtype","N");
				abean.set("codesetid","0");
				abean.set("value",PubFunc.round(rs.getString("score")==null|| "".equals(rs.getString("score"))?"0":rs.getString("score"),2));
				abean.set("viewvalue", "");
				list.add(abean);
				//
				abean = new LazyDynaBean();
				abean.set("itemid","rank");
				abean.set("itemdesc",ResourceFactory.getProperty("label.kh.template.qz"));
				abean.set("editable","1");//=0不需要输入和保存的=1需要保存的
				abean.set("itemtype","N");
				abean.set("codesetid","0");
				abean.set("value",PubFunc.round(rs.getString("rank")==null|| "".equals(rs.getString("rank"))?"0":rs.getString("rank"),2));
				abean.set("viewvalue", "");
				list.add(abean);
				//
				abean = new LazyDynaBean();
				abean.set("itemid","gradecode");
				abean.set("itemdesc",ResourceFactory.getProperty("jx.param.degreepro"));
				abean.set("editable","1");//=0不需要输入和保存的=1需要保存的
				abean.set("itemtype","A");
				abean.set("codesetid","0");
				ArrayList gradeList = this.getGradeList(rs.getString("point_id"));
				String gradecode=rs.getString("gradecode");
				if((gradecode==null|| "".equals(gradecode))&&gradeList.size()>0)
				{
					gradecode=((CommonData)gradeList.get(0)).getDataValue();
				}
				abean.set("options",gradeList);
				abean.set("value",gradecode==null?"":gradecode);
				abean.set("viewvalue", "");

				list.add(abean);
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
	/**
	 * 编辑保存岗位素质模型指标
	 * @param list
	 * @param object_id
	 * @param object_type
	 * @param point_id
	 */
	public void savePostModal(ArrayList list,String object_id,String object_type,String point_id)
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar calendar = Calendar.getInstance();
			String nowDate=sdf.format(calendar.getTime());
			StringBuffer strBuf= new StringBuffer();
			strBuf.append(" update per_competency_modal set ");
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)list.get(i);
				String itemid=(String)abean.get("itemid");
				String editable=(String)abean.get("editable");
				String itemtype=(String)abean.get("itemtype");
				String value=(String)abean.get("value");
				String viewValue=(String)abean.get("viewvalue");

				if("1".equals(editable)&&value!=null&&value.trim().length()>0)
				{
					if("point_type".equalsIgnoreCase(itemid)&& "".equals(viewValue.trim()))
					{
						strBuf.append(" "+itemid+"='' ,");
					}
					else
					{
						if("A".equalsIgnoreCase(itemtype))
						{
							strBuf.append(" "+itemid+"='"+value+"' ,");
						}else if("N".equalsIgnoreCase(itemtype))
						{
							if("rank".equalsIgnoreCase(itemid))
							{
								strBuf.append(" "+itemid+"="+Double.parseDouble(value)/100+",");
							}
							else
							{
								strBuf.append(" "+itemid+"="+ Double.parseDouble(value)+",");
							}
						}else if("D".equalsIgnoreCase(itemtype))
						{

							Calendar d=Calendar.getInstance();
							String[] temp=value.split("-");
							d.set(Calendar.YEAR, Integer.parseInt(temp[0]));
							d.set(Calendar.MONTH, Integer.parseInt(temp[1])-1);
							d.set(Calendar.DATE, Integer.parseInt(temp[2]));
							strBuf.append(" "+itemid+"="+Sql_switcher.dateValue(sdf.format(d.getTime()))+",");
						}else{
							strBuf.append(" "+itemid+"='"+value+"' ,");
						}
					}
				}
				else if("1".equals(editable))
				{
					if("A".equalsIgnoreCase(itemtype))
					{
						strBuf.append(" "+itemid+"='' ,");
					}else if("N".equalsIgnoreCase(itemtype))
					{

						strBuf.append(" "+itemid+"=0 ,");

					}else if("D".equalsIgnoreCase(itemtype))
					{

						Calendar d=Calendar.getInstance();
						String[] temp=value.split("-");
						d.set(Calendar.YEAR, Integer.parseInt(temp[0]));
						d.set(Calendar.MONTH, Integer.parseInt(temp[1])-1);
						d.set(Calendar.DATE, Integer.parseInt(temp[2]));
						strBuf.append(" "+itemid+"="+Sql_switcher.dateValue(sdf.format(d.getTime()))+",");
					}else{
						strBuf.append(" "+itemid+"='"+value+"' ,");
					}
				}

			}
			String sql=strBuf.toString().substring(0, strBuf.toString().length()-1);
			sql+=" where object_type="+object_type+" and object_id = '"+object_id+"' and point_id = '"+point_id+"'   and "+Sql_switcher.dateValue(nowDate)+" between start_date and end_date  ";
			dao.update(sql.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public ArrayList getGradeList(String point_id)
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search("select gradecode,gradedesc from per_grade where UPPER(point_id)='"+point_id.toUpperCase()+"' order by gradevalue desc");
			while(rs.next())
			{
				String gradedesc=rs.getString("gradedesc")==null?"":rs.getString("gradedesc");
				if(gradedesc.indexOf(":")!=-1)
				{
					gradedesc=gradedesc.substring(0,gradedesc.indexOf(":"));
				}else if(gradedesc.indexOf("：")!=-1)
				{
					gradedesc=gradedesc.substring(0,gradedesc.indexOf("："));
				}
				list.add(new CommonData(rs.getString("gradecode"),gradedesc));
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
	public String isHistory(String codesetid)
	{
		String str="0";
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search("select validateflag from codeset where UPPER(codesetid)='"+codesetid.toUpperCase()+"'");
			while(rs.next())
			{
				str=rs.getString(1);
			}
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
		return str;
	}

	private int lay=0;
	private int rowNum = 0; // 行坐标
	private short colIndex = 0; // 纵坐标
	private HSSFWorkbook hwb;
	private HSSFSheet sheet;
	private HashMap itemToPointMap=new HashMap();
	private ArrayList templateItemList = new ArrayList();
	private HashMap leafItemLinkMap = new HashMap();
	private HashMap itemPointNum= new HashMap();
	private HashMap itemHaveFieldList=new HashMap();
	private HashMap childItemLinkMap= new HashMap();
	private HashMap nameMap=new HashMap();
	private ArrayList parentList= new ArrayList();
	private ArrayList leafItemList = new ArrayList();
	private HashMap layMap=new HashMap();
	private HashMap ifHasChildMap=new HashMap();
	private HSSFCellStyle centerstyle ;
	private HSSFCellStyle headstyle ;
	private ArrayList postModalList = new ArrayList();//p04List
	private HSSFRow row = null;
	private ArrayList headList = new ArrayList();
	private String tableName="";
	private HashMap existMap = new HashMap();
	private HashMap existItemMap = new HashMap();
	private String historyDate="";
	private HSSFCellStyle numberStyle=null;
	public void initData(String itemid,String object_type,String codesetid)
	{
		try
		{
			/**取得模板的所有项目并且初始化parentList列表*/
			this.templateItemList=this.getPointTypeList(itemid,object_type,codesetid);
			/**项目的itemid对应的是该项目的所有父亲，爷爷，太爷的列表*/
			this.leafItemLinkMap=getLeafItemLinkMap();
			/**每个项目对应的叶子节点个数*/
			this.itemPointNum=getItemPointNum();
			/**除叶子节点外的节点的指标数量*/
			this.childItemLinkMap=this.getChildItemLinkMap();
			this.doMethod2();
			this.headList();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 为导出excel做准备，初始化一些数据
	 * @param pitemid 选中节点的父节点id（要导出包括父节点在内的能力素质模型）
	 * @param itemid 选中节点的id
	 */
	public ArrayList getPointTypeList(String itemid,String object_type,String codesetid)
	{
		RowSet rs = null;
		ArrayList list = new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer();
			buf.append(" select codeitem.codeitemid,codeitem.codeitemdesc,parentid,childid,pcm.point_id,codeitem2.codeitemdesc AS postname  from codeitem");
			buf.append(" left join ");
			buf.append(" (select * from per_competency_modal pcm ");
			buf.append(" where (pcm.object_id='"+itemid+"'");
			ArrayList parentLinkList=new ArrayList();
			StringBuffer buf_str = new StringBuffer("");
			if(!"3".equals(object_type))
			{
				this.getParentBean(codesetid, itemid, parentLinkList);
				for(int i=0;i<parentLinkList.size();i++)
				{
					if(i==0)
						buf_str.append(" or ((");
					LazyDynaBean bean=(LazyDynaBean)parentLinkList.get(i);
					String id=(String)bean.get("id");
					if(i!=0)
						buf_str.append(" or ");
					buf_str.append(" pcm.object_id='"+id+"'");
					if(i==parentLinkList.size()-1)
					{
						buf_str.append(") and UPPER(pcm.point_id) not in(select UPPER(point_id) from per_competency_modal where object_type="+object_type+" and object_id='"+itemid+"'");
						buf_str.append(" and "+Sql_switcher.dateValue(historyDate)+" between start_date and end_date)");
						buf_str.append(")");
					}
				}
			}
			if(buf_str.length()>0)
				buf.append(buf_str.toString());
			buf.append(" ) and pcm.object_type="+object_type);
			buf.append(" and "+Sql_switcher.dateValue(this.historyDate)+" between start_date and end_date ");
			String relTableName = "codeitem";
			if("3".equals(object_type)){
				relTableName = "organization";
			}
			buf.append(" ) pcm on codeitem.codeitemid=pcm.point_type");
			buf.append(" LEFT JOIN ( SELECT codeitemid, codeitemdesc FROM "+relTableName+" WHERE UPPER(codesetid) = '"+codesetid+"' ) codeitem2 ON pcm.object_id = codeitem2.codeitemid ");
			buf.append(" where codeitem.codeitemid in (");
			buf.append(" select distinct point_type ");
			buf.append(" from per_competency_modal pcm ");
			buf.append(" where (pcm.object_id='"+itemid+"'");
			if(buf_str.length()>0)
				buf.append(buf_str.toString());
			buf.append(") and pcm.object_type="+object_type);
			buf.append(" and "+Sql_switcher.dateValue(this.historyDate)+" between start_date and end_date ");
			buf.append(") and codesetid='70' order by object_id, pcm.point_id");
			rs=dao.search(buf.toString());
			HashMap leafMap = new HashMap();
			while(rs.next())
			{
				if(!this.userView.isSuper_admin()&&!this.userView.isHaveResource(IResourceConstant.KH_FIELD, rs.getString("point_id")))
					continue;
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("codeitemid", rs.getString("codeitemid"));
				bean.set("codeitemdesc", rs.getString("codeitemdesc")==null?"":rs.getString("codeitemdesc"));
				bean.set("parentid",rs.getString("parentid"));
				bean.set("childid",rs.getString("childid"));
				String isleaf="0";
				if(existItemMap.get(rs.getString("codeitemid"))==null)
					existItemMap.put(rs.getString("codeitemid"), "1");
				else
					continue;
				if(rs.getString("childid")==null||rs.getString("childid").equalsIgnoreCase(rs.getString("codeitemid"))||this.isLeaf(object_type, itemid, buf_str.toString(), rs.getString("codeitemid")))
				{
					isleaf="1";
					this.leafItemList.add(bean);
				}
				bean.set("isleaf", isleaf);
				bean.set("postname", rs.getString("postname"));
				list.add(bean);
			}
			buf.setLength(0);
			buf.append(" select pcm.score,pcm.point_type,pcm.rank,pcm.object_id,");
			buf.append(" pp.pointname,per_grade.gradedesc,pcm.point_id,T.codeitemdesc");
			buf.append(" from per_competency_modal pcm left join per_point pp on pcm.point_id=pp.point_id ");
			buf.append(" left join per_grade on ");
			buf.append(" pcm.point_id=per_grade.point_id and pcm.gradecode=per_grade.gradecode ");
			buf.append(" left join (select * from codeitem where codesetid='70') T on pcm.point_type=T.codeitemid ");
			buf.append(" where (pcm.object_id='"+itemid+"' ");
			if(buf_str.length()>0)
				buf.append(buf_str.toString());
			buf.append(" ) and pcm.object_type="+object_type);
			buf.append(" and "+Sql_switcher.dateValue(this.historyDate)+" between pcm.start_date and pcm.end_date ");
			buf.append(" order by object_id,pcm.point_id");
			rs=dao.search(buf.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				if(!this.userView.isSuper_admin()&&!this.userView.isHaveResource(IResourceConstant.KH_FIELD, rs.getString("point_id")))
					continue;
				String codeitemdesc=rs.getString("codeitemdesc");
				String point_type=(rs.getString("point_type")==null|| "".equals(rs.getString("point_type")))?"-1":rs.getString("point_type");
				if(codeitemdesc==null|| "".equals(codeitemdesc))
					point_type="-1";
				String pointname=rs.getString("pointname")==null?"":rs.getString("pointname");
				String gradedesc=rs.getString("gradedesc")==null?"":rs.getString("gradedesc");
				String point_id=rs.getString("point_id")==null?"":rs.getString("point_id");
				if(gradedesc.indexOf(":")!=-1)
				{
					gradedesc=gradedesc.substring(0,gradedesc.indexOf(":"));
				}else if(gradedesc.indexOf("：")!=-1)
				{
					gradedesc=gradedesc.substring(0,gradedesc.indexOf("："));
				}
				String fullgradedesc=rs.getString("gradedesc")==null?"":rs.getString("gradedesc");
				String score=PubFunc.round((rs.getString("score")==null|| "".equals(rs.getString("score"))?"0.00":rs.getString("score")), 2);
				String rank=rs.getString("rank")==null|| "".equals(rs.getString("rank"))?"0.00":rs.getString("rank");
				String object_id=rs.getString("object_id");
				bean.set("point_type",point_type);
				bean.set("pointname",pointname);
				bean.set("point_id",point_id);
				bean.set("gradedesc",gradedesc);
				bean.set("score",score);
				rank=PubFunc.round((Double.parseDouble(rank)*100+""),2)+"%";
				bean.set("rank",rank);
				bean.set("object_id",object_id);
				bean.set("fulldesc",fullgradedesc);
				postModalList.add(bean);
				ArrayList aList = new ArrayList();
				if(itemToPointMap.get(point_type.toUpperCase())!=null)
					aList=(ArrayList)itemToPointMap.get(point_type.toUpperCase());
				aList.add(bean);
				itemToPointMap.put(point_type.toUpperCase(), aList);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public void doMethod2()
	{
		for(int i=0;i<parentList.size();i++)
		{
			LazyDynaBean bean = (LazyDynaBean)parentList.get(i);
			String itemid=(String)bean.get("codeitemid");
			layMap.put(itemid, "1");
			doM(bean,1);
		}
	}
	public void doM(LazyDynaBean bean,int lay)
	{
		lay++;
		for(int i=0;i<this.templateItemList.size();i++)
		{
			LazyDynaBean a_bean=(LazyDynaBean)this.templateItemList.get(i);
			String itemid=(String)bean.get("codeitemid");
			String a_itemid=(String)a_bean.get("codeitemid");
			String parentid=(String)a_bean.get("parentid");
			if(parentid.equals(itemid)&&!parentid.equalsIgnoreCase(a_itemid))
			{
				ifHasChildMap.put(itemid, "1");
				layMap.put(a_itemid,lay+"");
				doM(a_bean,lay);
			}
		}
	}
	/**
	 * 叶子项目对应的继承关系
	 * @return
	 */
	public  HashMap getLeafItemLinkMap()
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			LazyDynaBean abean=null;
			for(int i=0;i<this.leafItemList.size();i++)
			{
				abean=(LazyDynaBean)this.leafItemList.get(i);
				String item_id=(String)abean.get("codeitemid");
				String parent_id=(String)abean.get("parentid");
				ArrayList linkList=new ArrayList();
				linkList.add(abean);
				this.parentLink(linkList, item_id, dao);

				for(int j=0;j<linkList.size();j++)
				{
					LazyDynaBean xx=(LazyDynaBean)linkList.get(j);
					String codeitemid=(String)xx.get("codeitemid");
					if(existItemMap.get(codeitemid)==null)
					{
						this.templateItemList.add(xx);
						existItemMap.put(codeitemid, "1");
					}
					if(existMap.get(codeitemid)==null)
					{
						if(j==0&&linkList.size()>1)
						{

						}
						else
							this.parentList.add(xx);
						existMap.put(codeitemid, "1");
					}
				}
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
	/**
	 * 取得项目拥有的节点数
	 * @return
	 */
	public HashMap getItemPointNum()
	{
		HashMap map=new HashMap();
		LazyDynaBean a_bean=null;
		LazyDynaBean aa_bean=null;
		for(int i=0;i<templateItemList.size();i++)
		{
			a_bean=(LazyDynaBean)this.templateItemList.get(i);
			ArrayList list=new ArrayList();
			getLeafItemList(a_bean,list);
			int n=0;
			for(int j=0;j<list.size();j++)
			{
				aa_bean=(LazyDynaBean)list.get(j);
				String item_id=(String)aa_bean.get("codeitemid");
				if(itemToPointMap.get(item_id)!=null)
					n+=((ArrayList)itemToPointMap.get(item_id)).size();
				else
					n+=1;
			}
			map.put((String)a_bean.get("codeitemid"),new Integer(n));
		}
		return map;
	}


	public void getLeafItemList(LazyDynaBean abean,ArrayList list)
	{
		String item_id=(String)abean.get("codeitemid");
		String child_id=(String)abean.get("childid");
		String isleaf=(String)abean.get("isleaf");
		if("1".equals(isleaf))
		{
			list.add(abean);
			return;
		}
		LazyDynaBean a_bean=null;
		for(int j=0;j<this.templateItemList.size();j++)
		{
			a_bean=(LazyDynaBean)this.templateItemList.get(j);
			String parent_id=(String)a_bean.get("parentid");
			String a_itemid=(String)a_bean.get("codeitemid");
			if(parent_id.equals(item_id)&&!a_itemid.equalsIgnoreCase(item_id))
				getLeafItemList(a_bean,list);
		}

	}
	public HashMap getChildItemLinkMap()
	{
		HashMap map = new HashMap();
		for(int i=0;i<this.templateItemList.size();i++)
		{
			LazyDynaBean bean=(LazyDynaBean)this.templateItemList.get(i);
			ArrayList list=new ArrayList();
			doMethod(bean,list);
			LazyDynaBean aa_bean=null;
			int n=0;
			for(int j=0;j<list.size();j++)
			{
				aa_bean=(LazyDynaBean)list.get(j);
				String item_id=(String)aa_bean.get("codeitemid");
				if(itemToPointMap.get(item_id)!=null)
					n+=((ArrayList)itemToPointMap.get(item_id)).size();

			}
			map.put((String)bean.get("codeitemid"),new Integer(n));
		}
		return map;
	}
	public void doMethod(LazyDynaBean bean,ArrayList list)
	{
		String itemid=(String)bean.get("codeitemid");
		String childid=(String)bean.get("childid");
		String isleaf=(String)bean.get("isleaf");
		if("1".equals(isleaf))
		{
			//list.add(bean);
			return;
		}else
		{
			list.add(bean);
		}
		for(int j=0;j<this.templateItemList.size();j++)
		{
			LazyDynaBean a_bean=(LazyDynaBean)this.templateItemList.get(j);
			String parentid=(String)a_bean.get("parentid");
			String a_itemid=(String)a_bean.get("codeitemid");
			if(parentid.equals(itemid)&&!a_itemid.equalsIgnoreCase(itemid))
			{
				doMethod(a_bean,list);
			}
		}
	}
	/***
	 * 得到当前类别的父系类别列表
	 * @param parentList
	 * @param bean
	 * @param tableName
	 * @param dao
	 */
	public void parentLink(ArrayList parentList,String currentcodeitemid,ContentDAO dao)
	{
		RowSet rs = null;
		try
		{
			StringBuffer buf = new StringBuffer("");
			buf.append("select codeitemid,parentid,childid,codeitemdesc from ");
			buf.append(" codeitem ");
			buf.append(" where codeitemid=(select parentid from codeitem ");
			buf.append(" where UPPER(codeitemid)='"+currentcodeitemid+"' and codesetid='70') and codesetid='70'");
			rs=dao.search(buf.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				String codeitemid=rs.getString("codeitemid")==null?"":rs.getString("codeitemid");
				String childid=rs.getString("childid")==null?"":rs.getString("childid");
				String parentid=rs.getString("parentid")==null?"":rs.getString("parentid");
				if(codeitemid.equalsIgnoreCase(currentcodeitemid))
					return;
				bean.set("parentid",parentid);
				bean.set("childid",childid);
				bean.set("codeitemid",codeitemid);
				bean.set("codeitemdesc",rs.getString("codeitemdesc")==null?"":rs.getString("codeitemdesc"));
				bean.set("isleaf", "0");
				parentList.add(bean);
				if("".equals(parentid)||parentid.equalsIgnoreCase(codeitemid))
					return;
				this.parentLink(parentList, codeitemid, dao);
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
	}
	/***
	 * 判断是否是叶子类别，（是在素质模型表中判断是否还有子类别。如果无，就算为叶子节点）
	 * @param object_type
	 * @param itemid
	 * @param pitemid
	 * @param pointtype
	 * @return
	 */
	public boolean isLeaf(String object_type,String itemid,String pitemid,String pointtype)
	{
		boolean flag=true;
		RowSet rs = null;
		try
		{
			StringBuffer buf = new StringBuffer("");
			buf.append(" select * from per_competency_modal pcm ");
			buf.append(" where (pcm.object_id='"+itemid+"' ");
			if(pitemid.length()>0)
				buf.append(pitemid);
			buf.append(" ) and pcm.object_type="+object_type);
			buf.append(" and pcm.point_type<>'"+pointtype+"'");
			buf.append(" and pcm.point_type like '"+pointtype+"%'");
			buf.append(" and "+Sql_switcher.dateValue(this.historyDate)+" between pcm.start_date and pcm.end_date ");
			buf.append(" order by object_id,start_date");
			ContentDAO dao = new ContentDAO(this.conn);
			rs=dao.search(buf.toString());
			while(rs.next())
			{
				if(!this.userView.isSuper_admin()&&!this.userView.isHaveResource(IResourceConstant.KH_FIELD, rs.getString("point_id")))
					continue;
				flag=false;
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
		return flag;
	}
	public void getParentBean(String codesetid,String codeitemid,ArrayList parentLinkList)
	{
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rs=dao.search("select codeitemdesc,codeitemid from "+tableName+" where UPPER(codesetid)='"+codesetid.toUpperCase()+"' and UPPER(codeitemid)=(select" +
					" UPPER(parentid) from "+tableName+" where UPPER(codeitemid)='"+codeitemid.toUpperCase()+"' and UPPER(codesetid)='"+codesetid.toUpperCase()+"')");
			while(rs.next())
			{
				if(codeitemid.equalsIgnoreCase(rs.getString("codeitemid")))
					return;
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("desc",rs.getString("codeitemdesc"));
				abean.set("id",rs.getString("codeitemid"));
				parentLinkList.add(abean);
				this.getParentBean(codesetid, rs.getString("codeitemid"), parentLinkList);
			}
		}catch(Exception e)
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
	 * 导出excel
	 * @param object_type
	 * @param object_id
	 * @param codesetid
	 * @return
	 */
	public String exportData(String object_type,String codeitemid,String codesetid,String historyDate) throws GeneralException
	{
		String fileName=this.userView.getUserName()+"_PostModal_"+PubFunc.getStrg()+".xls";
		RowSet rs= null;
		try
		{
			hwb=new HSSFWorkbook();
			this.historyDate=historyDate;
			String tableName="codeitem";
			if("3".equals(object_type))
				tableName="organization";
			this.tableName=tableName;
			MyObjectiveBo mob=new MyObjectiveBo(this.conn,this.userView);
			mob.setHwb(hwb);
			headstyle=mob.style(hwb,6);
			centerstyle =mob.style(hwb, 1);
			numberStyle=mob.style(hwb, 1);
			HSSFDataFormat df = hwb.createDataFormat();
			numberStyle.setDataFormat(df.getFormat("0.00_ "));
			ArrayList childList=this.getChildPosList(codeitemid, object_type, codesetid);

			// 部门下没有指标导出的文件无法打开，故阻止其导出 lium
			if (childList == null || childList.size() == 0) {
				throw new Exception(AdminCode.getCodeName(codesetid, codeitemid) + "下没有能力素质指标，导出失败");
			}

			for(int index=0;index<childList.size();index++)
			{
				lay=0;
				rowNum = 0; // 行坐标
				colIndex = 0; // 纵坐标
				itemToPointMap=new HashMap();
				templateItemList = new ArrayList();
				leafItemLinkMap = new HashMap();
				itemPointNum= new HashMap();
				itemHaveFieldList=new HashMap();
				childItemLinkMap= new HashMap();
				parentList= new ArrayList();
				leafItemList = new ArrayList();
				layMap=new HashMap();
				ifHasChildMap=new HashMap();
				postModalList = new ArrayList();//p04List
				HSSFRow row = null;
				existMap = new HashMap();
				existItemMap = new HashMap();
				this.headList=new ArrayList();
				LazyDynaBean indexBean=(LazyDynaBean)childList.get(index);
				String object_id=(String)indexBean.get("codeitemid");
				String objectDesc=(String)indexBean.get("codeitemdesc");
				String sheetName="";
				if("3".equals(object_type))
				{
					if(this.nameMap.get(objectDesc.toUpperCase())!=null)
					{
						sheetName=objectDesc+(Integer.parseInt((String)this.nameMap.get(objectDesc.toUpperCase())));
						this.nameMap.put(objectDesc.toUpperCase(), ""+(Integer.parseInt((String)this.nameMap.get(objectDesc.toUpperCase()))+1));
					}
					else
					{
						sheetName=objectDesc;
						this.nameMap.put(objectDesc, "1");
					}
					if(sheetName==null|| "".equals(sheetName))
						sheetName="sheetName"+index;
					this.sheet=hwb.createSheet(sheetName.replaceAll("/", "-"));
				}else{
					this.sheet=hwb.createSheet();
				}
				ArrayList alist=this.headList;

				this.initData(object_id, object_type,codesetid);
				LazyDynaBean lazyBean  = new LazyDynaBean();
				lazyBean.set("itemid","fulldesc");
				lazyBean.set("itemdesc", "说明");
				lazyBean.set("itemtype", "A");
				lazyBean.set("deci", "0");
				alist.add(lazyBean);
				mob.setSheet(sheet);
				if(this.lay==0)
					this.lay=1;
				HashMap existWriteItem=new HashMap();
				LazyDynaBean abean=null;
				LazyDynaBean a_bean=null;
				int columnSize=0;
				//输出表头
				mob.executeCell(this.rowNum,this.colIndex,this.rowNum,Short.parseShort(String.valueOf(this.lay-1)),"岗位序列",headstyle);
				this.colIndex+=Short.parseShort(String.valueOf(this.lay));
				mob.executeCell(this.rowNum,this.colIndex,this.rowNum,Short.parseShort(String.valueOf(this.lay)),"指标分类",headstyle);
				this.colIndex+=Short.parseShort(String.valueOf(this.lay));
				for(int x=0;x<alist.size();x++)
				{
					LazyDynaBean labean=(LazyDynaBean)alist.get(x);
					String itemid=(String)labean.get("itemid");
					String itemdesc=(String)labean.get("itemdesc");
					mob.executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,itemdesc,headstyle);
					this.colIndex++;
				}
				this.rowNum++;
				columnSize=this.colIndex+lay;
				int rowNum=0;
				for(int i=0;i<this.leafItemList.size();i++)
				{
					abean=(LazyDynaBean)this.leafItemList.get(i);
					String item_id=(String)abean.get("codeitemid");
					this.colIndex=0;
					rowNum++;
					ArrayList linkParentList=(ArrayList)this.leafItemLinkMap.get(item_id);
					int current=linkParentList.size();
					if(current==1)
					{
						if(existWriteItem.get(item_id)!=null)
						{
							this.colIndex++;
							continue;
						}
						existWriteItem.put(item_id,"1");
						String itemdesc=(String)abean.get("codeitemdesc");
						String postname=(String)abean.get("postname");
						/**画出一个父项目*/
						int colspan=((itemPointNum.get(item_id)==null?0:((Integer)itemPointNum.get(item_id)).intValue())+(childItemLinkMap.get(item_id)==null?0:((Integer)childItemLinkMap.get(item_id)).intValue()));
						mob.executeCell(this.rowNum,this.colIndex,colspan==0?this.rowNum:(this.rowNum+colspan-1),this.colIndex,postname,centerstyle);
						this.colIndex++;
						/**父项目包含指标，画出指标*/
						/**该项目的层数*/
						int layer=Integer.parseInt((String)layMap.get(item_id));
						/**对应指标列表*/
						ArrayList fieldlistp = (ArrayList)this.itemToPointMap.get(item_id);
						/**该项目有指标*/
						if(fieldlistp!=null&&fieldlistp.size()>0)
						{
							for(int h=0;h<fieldlistp.size();h++)
							{
								LazyDynaBean pointbean=(LazyDynaBean)fieldlistp.get(h);
								int k=0;
								for(int f=0;f<this.lay-layer;f++)
								{
									mob.executeCell(this.rowNum,(short)(colIndex+f),this.rowNum,(short)((colIndex+f)),"",centerstyle);
									k++;
								}

								mob.executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),itemdesc,centerstyle);
								k++;

								for(int x=0;x<alist.size();x++)
								{
									LazyDynaBean labean=(LazyDynaBean)alist.get(x);
									String itemid=(String)labean.get("itemid");
									if("rank".equalsIgnoreCase(itemid)|| "score".equals(itemid))
										mob.executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),(String)pointbean.get(itemid),this.numberStyle);
									else
										mob.executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),(String)pointbean.get(itemid),centerstyle);
									k++;
								}
								this.rowNum++;
							}
						}
						/**没有指标**/
						else
						{
							/**画出空格*/
							int k=0;
							for(int f=0;f<this.lay-layer;f++)
							{
								mob.executeCell(this.rowNum,(short)(colIndex+f),this.rowNum,(short)((colIndex+f)),"",centerstyle);
								k++;
							}
							mob.executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k)," ",centerstyle);
							for(int x=0;x<alist.size();x++)
							{
								LazyDynaBean labean=(LazyDynaBean)alist.get(x);
								String itemid=(String)labean.get("itemid");
								String value=(String)abean.get(itemid);
								if("rank".equals(itemid)|| "score".equals(itemid))
									mob.executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),value,this.numberStyle);
								else
									mob.executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),value,centerstyle);
								k++;
							}
							this.rowNum++;
						}
					}
					else
					{
						/**叶子项目的所有父项目列表（爷爷，太爷）*/
						for(int e=linkParentList.size()-1;e>=0;e--)
						{
							a_bean=(LazyDynaBean)linkParentList.get(e);
							String itemid=(String)a_bean.get("codeitemid");
							if(existWriteItem.get(itemid)!=null)
							{
								this.colIndex++;
								continue;
							}
							existWriteItem.put(itemid,"1");
							String itemdesc=(String)a_bean.get("codeitemdesc");
							/**画出一个父项目*/
							int colspan=((itemPointNum.get(itemid)==null?0:((Integer)itemPointNum.get(itemid)).intValue())+(childItemLinkMap.get(itemid)==null?0:((Integer)childItemLinkMap.get(itemid)).intValue()));
							//this.rowNum++;
							mob.executeCell(this.rowNum,this.colIndex,colspan==0?this.rowNum:(this.rowNum+colspan-1),this.colIndex,itemdesc,centerstyle);
							this.colIndex++;
							/**父项目包含指标，画出指标*/
							/**该项目的层数*/
							int layer=Integer.parseInt((String)layMap.get(itemid));
							/**对应指标列表*/
							ArrayList fieldlistp = (ArrayList)this.itemToPointMap.get(itemid);
							/**该项目有指标*/
							if(fieldlistp!=null&&fieldlistp.size()>0)
							{
								for(int h=0;h<fieldlistp.size();h++)
								{
									LazyDynaBean pointbean=(LazyDynaBean)fieldlistp.get(h);
									int k=0;
									for(int f=0;f<this.lay-layer;f++)
									{
										mob.executeCell(this.rowNum,(short)(colIndex+f),this.rowNum,(short)((colIndex+f)),"",centerstyle);
										k++;
									}
									for(int x=0;x<alist.size();x++)
									{
										LazyDynaBean labean=(LazyDynaBean)alist.get(x);
										String itemId=(String)labean.get("itemid");
										if("rank".equals(itemId)|| "score".equals(itemId))
											mob.executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),(String)pointbean.get(itemId),this.numberStyle);
										else
											mob.executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),(String)pointbean.get(itemId),centerstyle);
										k++;
									}
									this.rowNum++;
								}
							}
							/**没有指标**/
							else
							{
								if(e==0)
								{
									int k=0;
									for(int f=0;f<this.lay-layer;f++)
									{
										mob.executeCell(this.rowNum,(short)(colIndex+f),this.rowNum,(short)((colIndex+f)),"",centerstyle);
										k++;
									}
									for(int x=0;x<alist.size();x++)
									{
										LazyDynaBean labean=(LazyDynaBean)alist.get(x);
										String itemId=(String)labean.get("itemid");
										String value=(String)a_bean.get(itemId);
										if("rank".equals(itemId)|| "score".equals(itemId))
											mob.executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),value,this.numberStyle);
										else
											mob.executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),value,centerstyle);
										k++;
									}
									this.rowNum++;
								}
							}
						}
					}
				}
				ArrayList nullList = (ArrayList)this.itemToPointMap.get("-1");
				if(nullList!=null&&nullList.size()>0)
				{
					this.colIndex=0;
					mob.executeCell(this.rowNum,this.colIndex,this.rowNum+nullList.size()-1,Short.parseShort(String.valueOf(this.lay-1)),objectDesc,centerstyle);
					this.colIndex+=Short.parseShort(String.valueOf(this.lay));

					mob.executeCell(this.rowNum,this.colIndex,this.rowNum+nullList.size()-1,Short.parseShort(String.valueOf(this.lay)),"",centerstyle);
					this.colIndex+=Short.parseShort(String.valueOf(this.lay));
					for(int h=0;h<nullList.size();h++)
					{
						LazyDynaBean pointbean=(LazyDynaBean)nullList.get(h);
						int k=0;
						for(int x=0;x<alist.size();x++)
						{
							LazyDynaBean labean=(LazyDynaBean)alist.get(x);
							String itemId=(String)labean.get("itemid");
							String str="";
							if(pointbean.get(itemId)!=null)
								str=(String)pointbean.get(itemId);
							if("rank".equals(itemId)|| "score".equals(itemId))
								mob.executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),str,this.numberStyle);
							else
								mob.executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),str,centerstyle);
							k++;
						}
						this.rowNum++;
					}
				}
				for (int i = 0; i <=columnSize; i++)
				{
					if(i==this.lay+1||i==this.lay+2)
						this.sheet.setColumnWidth(Short.parseShort(String.valueOf(i)),(short)4000);
					else
						this.sheet.setColumnWidth(Short.parseShort(String.valueOf(i)),(short)6000);
				}
				for (int i = 0; i <=this.rowNum; i++)
				{
					row = sheet.getRow(i);
					if(row==null)
						row = sheet.createRow(i);
					row.setHeight((short) 400);
				}
			}
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+ System.getProperty("file.separator") + fileName);
			hwb.write(fileOut);
			fileOut.close();
			sheet = null;
			hwb = null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
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
		return fileName;
	}
	public ArrayList getChildPosList(String codeitemid,String object_type,String codesetid)
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			if("3".equals(object_type)&&!"@K".equalsIgnoreCase(codesetid))
			{
				String sql = "select codeitemid,codeitemdesc from organization where codesetid='@K' and codeitemid like '"+codeitemid+"%' " +
						" and codeitemid in(select object_id from per_competency_modal where object_type="+object_type+" and object_id like '"+codeitemid+"%'"+
						" and "+Sql_switcher.dateValue(historyDate)+" between start_date and end_date)"+
						" and "+Sql_switcher.dateValue(historyDate)+" between start_date and end_date";

				rs=dao.search(sql);
				while(rs.next())
				{
					LazyDynaBean bean=new LazyDynaBean();
					bean.set("codeitemid", rs.getString("codeitemid"));
					bean.set("codeitemdesc", rs.getString("codeitemdesc"));
					list.add(bean);
				}
			}else{
				LazyDynaBean bean=new LazyDynaBean();
				bean.set("codeitemid", codeitemid);
				bean.set("codeitemdesc", AdminCode.getCodeName(codesetid,codeitemid));
				list.add(bean);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * excel文件列头
	 */
	public void headList()
	{
		try
		{
			LazyDynaBean lazyBean = null;
			/*lazyBean  = new LazyDynaBean();
			lazyBean.set("itemid","point_type");
			lazyBean.set("itemdesc", "类别");
			lazyBean.set("itemtype", "A");
			lazyBean.set("deci", "0");
			this.headList.add(lazyBean);*/
			lazyBean  = new LazyDynaBean();
			lazyBean.set("itemid","point_id");
			lazyBean.set("itemdesc", "指标编码");
			lazyBean.set("itemtype", "A");
			lazyBean.set("deci", "0");
			this.headList.add(lazyBean);
			lazyBean  = new LazyDynaBean();
			lazyBean.set("itemid","pointname");
			lazyBean.set("itemdesc", "指标名称");
			lazyBean.set("itemtype", "A");
			lazyBean.set("deci", "0");
			this.headList.add(lazyBean);
			lazyBean  = new LazyDynaBean();
			lazyBean.set("itemid","score");
			lazyBean.set("itemdesc","标准分值" );
			lazyBean.set("itemtype", "N");
			lazyBean.set("deci", "2");
			this.headList.add(lazyBean);
			lazyBean  = new LazyDynaBean();
			lazyBean.set("itemid","rank");
			lazyBean.set("itemdesc", "权重");
			lazyBean.set("itemtype", "N");
			lazyBean.set("deci", "2");
			this.headList.add(lazyBean);
			lazyBean  = new LazyDynaBean();
			lazyBean.set("itemid","gradedesc");
			lazyBean.set("itemdesc", "等级");
			lazyBean.set("itemtype", "A");
			lazyBean.set("deci", "0");
			this.headList.add(lazyBean);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 *
	 * @param object_id
	 * @param object_type
	 * @param import_type =1引入岗位体系模型，=2引入职务体系模型
	 */
	public String importPoint(String object_id,String object_type,int import_type,String codesetid,String historyDate)
	{
		StringBuffer buf = new StringBuffer("");
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			boolean isHave=false;
			String field="";
			if(import_type==1)
			{
				RecordVo ps_c_job_vo=ConstantParamter.getRealConstantVo("PS_C_JOB",this.conn);
				if(ps_c_job_vo!=null)
				{
					field=ps_c_job_vo.getString("str_value");
				}
			}
			else if(import_type==2)
			{
				RecordVo ps_job_vo=ConstantParamter.getRealConstantVo("PS_JOB",this.conn);
				if(ps_job_vo!=null)
				{
					field=ps_job_vo.getString("str_value");
				}
			}
			if(field==null|| "#".equals(field)|| "".equals(field))
			{
				if(import_type==1)
					buf.append("系统未设置【所属岗位序列指标】参数！");
				else
					buf.append("系统未设置【所属职务序列指标】参数！");
			}else
			{
				StringBuffer sql = new StringBuffer();
				String postModal="";
				sql.append(" select "+field+" from k01 where e01a1=");
				sql.append(" '"+object_id+"'");
				rs=dao.search(sql.toString());
				while(rs.next())
				{
					postModal=rs.getString(1);
				}
				if(postModal==null|| "".equals(postModal))
				{
					if(import_type==1)
						buf.append("该岗位未设置所属岗位序列！");
					else
						buf.append("该岗位未设置所属职务序列！");
				}
				else
				{
					sql.setLength(0);
					this.tableName="codeitem";
					FieldItem item = DataDictionary.getFieldItem(field.toLowerCase());
					String acodesetid=item.getCodesetid();
					ArrayList parentLinkList=new ArrayList();
					this.getParentBean(acodesetid, postModal, parentLinkList);
					StringBuffer buf_str = new StringBuffer("");
					boolean flag=false;
					for(int i=0;i<parentLinkList.size();i++)
					{
						if(i==0)
							buf_str.append(" or (");
						LazyDynaBean bean=(LazyDynaBean)parentLinkList.get(i);
						String id=(String)bean.get("id");
						if(i!=0)
							buf_str.append(" or ");
						buf_str.append("  object_id='"+id+"'");
						if(i==parentLinkList.size()-1)
						{
							buf_str.append(")");
						}
					}
					sql.setLength(0);
					sql.append(" select point_id,score,point_type,gradecode,start_date,end_date,rank ");
					sql.append(" from per_competency_modal where ");//import_type =1引入岗位体系模型，=2引入职务体系模型
					if(import_type==1)
						sql.append(" object_type=2 ");
					else
						sql.append(" object_type=1");
					sql.append(" and (object_id='"+postModal+"' ");
					if(buf_str.length()>0)
						sql.append(buf_str.toString());
					sql.append(") ");
					sql.append(" and UPPER(point_id) in (select UPPER(point_id) from per_competency_modal ");
					sql.append(" where object_id='"+object_id+"'");
					sql.append(" and object_type="+object_type+")");
					sql.append(" and "+Sql_switcher.dateValue(historyDate)+" between start_date and end_date ");
					rs=dao.search(sql.toString());
					ArrayList updateList = new ArrayList();
					Calendar calendar = Calendar.getInstance();
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String nowDate=sdf.format(calendar.getTime());
					ResultSet res=null;
					String aql="";
					String start_date="";
					while(rs.next())
					{

						String pointid=rs.getString(1);
						String gradecode = rs.getString("gradecode");
						gradecode = gradecode==null?"":gradecode;
						String point_type = rs.getString("point_type");
						point_type = point_type==null?"":point_type;
						String enddate = "9999-12-31";
						dao.update("update per_competency_modal set end_date="+Sql_switcher.charToDate("'"+enddate+"'")+" where object_id='"+object_id+"' and point_id='"+pointid+"' and object_type="+object_type+" ");

						aql=" update per_competency_modal set end_date="+Sql_switcher.dateValue(sdf.format(rs.getDate("end_date"))) +",start_date="+Sql_switcher.dateValue(sdf.format(rs.getDate("start_date")))+
								" ,score="+ rs.getDouble("score")+",rank="+rs.getDouble("rank")+",point_type='"+point_type+"',gradecode='"+gradecode+"'"+
								" where object_id='"+object_id+"' and point_id='"+pointid+"' and object_type="+object_type+"  and "+Sql_switcher.dateValue(nowDate)+" between start_date and end_date ";

						updateList.add(aql);
						flag=true;
					}
					if(updateList.size()>0)
						dao.batchUpdate(updateList);
					sql.setLength(0);
					sql.append(" select point_id,score,point_type,gradecode,start_date,end_date,rank,object_id");
					sql.append(" from per_competency_modal where ");//import_type =1引入岗位体系模型，=2引入职务体系模型
					if(import_type==1)
						sql.append(" object_type=2 ");
					else
						sql.append(" object_type=1");
					sql.append(" and (object_id='"+postModal+"' ");
					if(buf_str.length()>0)
						sql.append(buf_str.toString());
					sql.append(") ");
					sql.append(" and UPPER(point_id) not in (select UPPER(point_id) from per_competency_modal ");
					sql.append(" where object_id='"+object_id+"'");
					sql.append(" and object_type="+object_type+")");
					sql.append(" and "+Sql_switcher.dateValue(historyDate)+" between start_date and end_date order by object_id desc");
					rs=dao.search(sql.toString());
					HashMap map = new HashMap();
					ArrayList insertList = new ArrayList();
					while(rs.next())
					{
						RecordVo vo = new RecordVo("per_competency_modal");
						String pointid=rs.getString("point_id");
						if(map.get(pointid.toUpperCase())!=null)
							continue;
						map.put(pointid.toUpperCase(),"1");
						vo.setString("object_type",object_type);
						vo.setString("object_id", object_id);
						vo.setString("point_id",pointid);
						vo.setDate("end_date", rs.getDate("end_date"));
						vo.setDate("start_date", rs.getDate("start_date"));
						vo.setDouble("score", rs.getDouble("score"));
						vo.setDouble("rank",rs.getDouble("rank"));
						vo.setString("point_type",rs.getString("point_type"));
						vo.setString("gradecode", rs.getString("gradecode"));
						insertList.add(vo);
						flag=true;
					}
					if(insertList.size()>0)
						dao.addValueObject(insertList);
					if(!flag)
					{
						buf.append("没有可引入的数据！");
					}
				}
			}
			if(buf.length()<=0)
				buf.append("ok");
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
		return buf.toString();
	}




	/**
	 * 获得模板下指标信息
	 * @param templateID
	 * @return
	 */
	public ArrayList getPointList(String templateID)
	{
		ArrayList pointList=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			HashMap hasCoursePoint=new HashMap();
			StringBuffer buf=new StringBuffer("");
			buf.append("select distinct upper(point_id) from per_point_course where upper(point_id) in ( select upper(po.point_id) from per_template_item pi,per_template_point pp,per_point po where pi.item_id=pp.item_id and pp.point_id=po.point_id and template_id='"+templateID+"' ) ");
			RowSet rs=dao.search(buf.toString());
			while(rs.next())
				hasCoursePoint.put(rs.getString("point_id").toLowerCase(),"1");

			rs=dao.search("select po.point_id,po.pointname,po.pointkind,po.status,po.proposal from per_template_item pi,per_template_point pp,per_point po "
					+" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id='"+templateID+"'  order by pp.seq");
			LazyDynaBean bean=null;
			while(rs.next())
			{
				bean=new LazyDynaBean();
				bean.set("point_id",rs.getString("point_id").toLowerCase());
				bean.set("pointname",Sql_switcher.readMemo(rs,"pointname"));
				bean.set("pointkind",rs.getString("pointkind"));
				bean.set("status",rs.getString("status"));
				bean.set("proposal", Sql_switcher.readMemo(rs,"proposal")); //行为建议
				if(hasCoursePoint.get(rs.getString("point_id").toLowerCase())!=null)  //是否有关联课程
					bean.set("hasCourse","1");
				else
					bean.set("hasCourse","0");
				pointList.add(bean);
			}

			if(rs!=null)
				rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return pointList;
	}

	/**
	 * 获得计划信息
	 * @param planid
	 * @return
	 */
	public RecordVo getPlanVo(String planid)
	{
		RecordVo vo=new RecordVo("per_plan");
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			vo.setInt("plan_id",Integer.parseInt(planid));
			vo=dao.findByPrimaryKey(vo);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}

	/**
	 * template_id 模板编号
	 * 获得某编号的考核模板的所有信息
	 */
	public RecordVo getTemplateVo(String template_id)
	{
		RecordVo vo=new RecordVo("per_template");
		try
		{
			vo.setString("template_id",template_id);
			ContentDAO dao = new ContentDAO(this.conn);
			vo=dao.findByPrimaryKey(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}


	private String getWhl(Calendar sd)
	{
		StringBuffer whl=new StringBuffer("");
		whl.append(" and ( "+Sql_switcher.year("end_date")+">"+sd.get(Calendar.YEAR));
		whl.append(" or ( "+Sql_switcher.year("end_date")+"="+sd.get(Calendar.YEAR)+" and "+Sql_switcher.month("end_date")+">"+(sd.get(Calendar.MONTH)+1)+" ) ");
		whl.append(" or ( "+Sql_switcher.year("end_date")+"="+sd.get(Calendar.YEAR)+" and "+Sql_switcher.month("end_date")+"="+(sd.get(Calendar.MONTH)+1)+" and "+Sql_switcher.day("end_date")+">="+sd.get(Calendar.DATE)+" ) ) ");

		whl.append(" and ( "+Sql_switcher.year("start_date")+"<"+sd.get(Calendar.YEAR));
		whl.append(" or ( "+Sql_switcher.year("start_date")+"="+sd.get(Calendar.YEAR)+" and "+Sql_switcher.month("start_date")+"<"+(sd.get(Calendar.MONTH)+1)+" ) ");
		whl.append(" or ( "+Sql_switcher.year("start_date")+"="+sd.get(Calendar.YEAR)+" and "+Sql_switcher.month("start_date")+"="+(sd.get(Calendar.MONTH)+1)+" and "+Sql_switcher.day("start_date")+"<="+sd.get(Calendar.DATE)+" ) ) ");
		return whl.toString();
	}


	public HashMap getResultMap(RecordVo planVo,String nbase,String a0100,ArrayList pointList)
	{
		HashMap map=new HashMap();
		StringBuffer buf = new StringBuffer("");
		RowSet rs = null;
		LazyDynaBean abean=null;
		try
		{
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(String.valueOf(planVo.getInt("busitype"))!=null && String.valueOf(planVo.getInt("busitype")).trim().length()>0 && planVo.getInt("busitype")==1)
				per_comTable = "per_grade_competence"; // 能力素质标准标度
			HashMap pointDescMap=new HashMap();
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "select po.point_id,pg.gradedesc,pg.gradecode,pg.top_value,pg.bottom_value,pp.score,pg.gradevalue,pgt.gradedesc pt_gradedesc from per_template_item pi,per_template_point pp,per_point po,per_grade pg,"+per_comTable+" pgt "
					+ " where pi.item_id=pp.item_id and pp.point_id=po.point_id and po.point_id=pg.point_id and pg.gradecode=pgt.grade_template_id and template_id='" + planVo.getString("template_id") + "' ";
			sql += " order by pp.seq,pg.point_id,pg.grade_id";
			rs=dao.search(sql);
			String o_pointid="";
			ArrayList list=new ArrayList();
			while(rs.next())
			{
				String pointid=rs.getString("point_id");
				abean=new LazyDynaBean();
				abean.set("gradedesc", rs.getString("gradedesc"));
				abean.set("gradecode", rs.getString("gradecode"));
				abean.set("top_value", rs.getString("top_value")!=null?rs.getString("top_value"):"");
				abean.set("bottom_value", rs.getString("bottom_value")!=null?rs.getString("bottom_value"):"");
				abean.set("score", rs.getString("score")!=null?rs.getString("score") :"");
				abean.set("gradevalue", rs.getString("gradevalue")!=null?rs.getString("gradevalue"):"");
				abean.set("pt_gradedesc",rs.getString("pt_gradedesc"));

				if("".equals(o_pointid))
					o_pointid=pointid;
				if(o_pointid.equals(pointid))
				{
					list.add(abean);
				}
				else
				{
					pointDescMap.put(o_pointid.toLowerCase(),list);
					o_pointid=pointid;
					list=new ArrayList();
					list.add(abean);
				}
			}
			pointDescMap.put(o_pointid.toLowerCase(),list);

			String e01a1="";

			rs=dao.search("select e01a1 from "+nbase+"A01 where a0100='"+a0100+"'");
			if(rs.next())
			{
				if(rs.getString(1)!=null)
					e01a1=rs.getString(1);
			}
			HashMap  _map=new HashMap();
			rs=dao.search("select * from per_competency_modal where object_type=3 and object_id='"+a0100+"'  "+getWhl(Calendar.getInstance()));
			while(rs.next())
			{
				abean=new LazyDynaBean();
				String point_id=rs.getString("point_id").toLowerCase();
				String score=rs.getString("score")!=null?rs.getString("score"):"";
				String gradecode=rs.getString("gradecode")!=null?rs.getString("gradecode"):"";
				String rank=rs.getString("rank")!=null?rs.getString("rank"):"";
				abean.set("score",score);
				abean.set("gradecode",gradecode);
				abean.set("rank",rank);
				_map.put(point_id, abean);
			}

			rs=dao.search("select * from per_result_"+planVo.getInt("plan_id")+" where object_id='"+a0100+"'");
			if(rs.next())
			{
				for(int i=0;i<pointList.size();i++)
				{
					abean=(LazyDynaBean)pointList.get(i);
					String point_id=(String)abean.get("point_id");
					if(_map.get(point_id)!=null)
					{
						LazyDynaBean _bean=(LazyDynaBean)_map.get(point_id);
						abean.set("score",(String)_bean.get("score"));
						abean.set("gradecode",(String)_bean.get("gradecode"));
						abean.set("rank",(String)_bean.get("rank"));
					}
					else
					{
						abean.set("score","");
						abean.set("gradecode","");
						abean.set("rank","");
					}
					if(rs.getString("C_"+point_id)!=null)
					{
						ArrayList gradeList=new ArrayList();
						if(pointDescMap.get(point_id)!=null)
							gradeList=(ArrayList)pointDescMap.get(point_id);
						if(gradeList.size()==0)
							abean.set("gradecode_achieve","");
						else
						{
							double value=rs.getDouble("C_"+point_id);
							for(int j=0;j<gradeList.size();j++)
							{
								LazyDynaBean abean2=(LazyDynaBean)gradeList.get(j);
								String score=(String)abean2.get("score");
								String top_value=(String)abean2.get("top_value");
								String bottom_value=(String)abean2.get("bottom_value");
								String gradedesc=(String)abean2.get("gradedesc");
								if(score.length()>0||top_value.length()>0||bottom_value.length()>0)
								{
									double _score=Double.parseDouble(score);
									double _top_value=Double.parseDouble(top_value);
									double _bottom_value=Double.parseDouble(bottom_value);
									if(value>=_score*_bottom_value&&value<=_score*_top_value)
									{
										abean.set("gradecode_achieve",gradedesc);
										break;
									}
								}

							}

						}
					}
					else
						abean.set("gradecode_achieve","");

					map.put(point_id, abean);
				}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}



	HashMap leafitemPointMap = new HashMap();  //项目对应指标
	int _lay=0;
	/**
	 * @param a0100
	 * @param planid
	 * @return
	 */
	public String getGradeResultHtml(String a0100,ArrayList headList)
	{
		StringBuffer html = new StringBuffer("");
		StringBuffer titleHtml = new StringBuffer(""); // 表头html  加以区分方便维护
		StringBuffer bodyHtml = new StringBuffer("");  // 表体html  加以区分方便维护
		try
		{
			ArrayList pointList = getPointList();  // 获得模板下的指标（按顺序）
			ArrayList templateItemList = getTemplateItemList(); // 取得 模板项目记录
			HashMap leafItemLinkMap = getLeafItemLinkMap(leafitemPointMap,templateItemList); // 叶子项目对应的继承关系
			HashMap itemPointNum = getItemPointNum(templateItemList,leafitemPointMap); // 取得项目拥有的节点数
			// 获得能力素质各岗位定义的分值、权重和要求的等级
			HashMap postScoreMap = getE01a1RankScore();
			// 获得能力素质指标是否关联了课程
			HashMap perCourseMap = getPerpointCourse();
			// 获得考核结果表中某人的记录的主键id
			String perResultId = getPerResultId(a0100);
			RecordVo perResultVo = getPerResultVo(perResultId); // 获得考核结果表中某人的信息记录


			titleHtml.append("<tr><td style='border:0px' colspan='"+(headList.size()+this.lay-1)+"' ><table width='100%'  style='border-collapse:collapse;' class='ListTable'>");
			titleHtml.append("<tr>");
			titleHtml.append("<td class='TableRow_lrt common_border_color' width='20%' valign='middle' align='center' nowrap>单位名称</td>");
			titleHtml.append("<td class='RecordRow_lt common_border_color' width='30%' valign='middle' align='left' nowrap>&nbsp;"+AdminCode.getCodeName("UN", perResultVo.getString("b0110"))+"</td>");
			titleHtml.append("<td class='TableRow_lrt common_border_color' width='20%' valign='middle' align='center' nowrap>部门</td>");
			titleHtml.append("<td class='RecordRow_lrt common_border_color' width='30%' valign='middle' align='left' nowrap>&nbsp;"+AdminCode.getCodeName("UM", perResultVo.getString("e0122"))+"</td>");

			titleHtml.append("</tr><tr>");
			titleHtml.append("<td class='TableRow_lrt common_border_color' width='20%' valign='middle' align='center' nowrap>岗位名称</td>");
			titleHtml.append("<td class='RecordRow_lt common_border_color' width='30%' valign='middle' align='left' nowrap >&nbsp;"+AdminCode.getCodeName("@K", perResultVo.getString("e01a1"))+" ");
			//	titleHtml.append("<hrms:priv func_id='36040201'>");
			if(this.userView.hasTheFunction("36040201"))
				titleHtml.append("&nbsp;<img src=\"/images/station_per.jpg\" alt=\"岗人匹配\" BORDER='0' style='cursor:hand;' onclick=\"checkPerStion('2');\">&nbsp;&nbsp;");
			//	titleHtml.append("</hrms:priv>");
			titleHtml.append("</td>");
			titleHtml.append("<td class='TableRow_lrt common_border_color' width='20%' valign='middle' align='center' nowrap>姓名</td>");
			titleHtml.append("<td class='RecordRow_lrt common_border_color' width='30%' valign='middle' align='left' nowrap >&nbsp;"+perResultVo.getString("a0101")+" ");
			//	titleHtml.append("<hrms:priv func_id='36040202'>");
			if(this.userView.hasTheFunction("36040202"))
				titleHtml.append("&nbsp;<img src=\"/images/per_station.jpg\" alt=\"人岗匹配\" BORDER='0' style='cursor:hand;' onclick=\"checkPerStion('1');\">&nbsp;&nbsp;");
			//	titleHtml.append("</hrms:priv>");
			titleHtml.append("</td>");
			titleHtml.append("</tr><tr>");
			titleHtml.append("<td class='TableRow_lrt common_border_color' width='20%' valign='middle' align='center' nowrap>考核等级</td>");
			titleHtml.append("<td class='RecordRow_lt common_border_color' width='30%' valign='middle' align='left' nowrap>&nbsp;"+perResultVo.getString("resultdesc")+"</td>");
			titleHtml.append("<td class='TableRow_lrt common_border_color' width='20%' valign='middle' align='center' nowrap>匹配度</td>");
			String matesurmise = perResultVo.getString("matesurmise");
			titleHtml.append("<td class='RecordRow_lrt common_border_color' width='30%' valign='middle' align='left' nowrap>&nbsp;"+(PubFunc.multiple(matesurmise.length()==0?"0":matesurmise,"100", 2)+"%")+"</td>");//没有匹配度，认为匹配度是0 zhaoxg add 2014-11-19
			titleHtml.append("</tr></table>");
			titleHtml.append("</td></tr>");

			// 输出表头
			titleHtml.append("<tr class='trDeep' height='50' >");
			LazyDynaBean abean=null;
			for(int k=0;k<headList.size();k++)
			{
				abean=(LazyDynaBean)headList.get(k);
				String itemid=(String)abean.get("itemid");     // 表头字段ID
				String itemdesc=(String)abean.get("itemdesc"); // 表头描述信息
				int itemWidth=((Integer)abean.get("itemWidth")).intValue(); // 表头各列宽度

				if("item_id".equalsIgnoreCase(itemid))
					titleHtml.append("<td class='TableTitltRow common_background_color common_border_color' valign='middle' align='center' colspan='"+this.lay+"' nowrap >&nbsp;"+itemdesc+"&nbsp;</td>");
				else
					titleHtml.append("<td class='TableTitltRow common_background_color common_border_color' valign='middle' align='center' nowrap >&nbsp;"+itemdesc+"&nbsp;</td>");
			}
			titleHtml.append("</tr>");


			// 输出表体
			LazyDynaBean bean=null;
			HashMap existWriteItem=new HashMap();  // 放已画过的上级项目、上上级项目
			//  循环所有指标
			for(int k=0;k<pointList.size();k++)
			{
				LazyDynaBean lbean = (LazyDynaBean)pointList.get(k);
				String item_id = (String)lbean.get("item_id");  // 项目ID
				String point_id = (String)lbean.get("point_id");  // 指标ID
				String pointsetid = (String)lbean.get("pointsetid");  // 指标分类ID
				String pointname = (String)lbean.get("pointname"); // 指标名称
				String proposal = (String)lbean.get("proposal"); // 行为建议
				String score = (String)lbean.get("score");     // 指标得分
				String rank = (String)lbean.get("rank");     // 指标权重
				String scoreRankDegree = (String)postScoreMap.get(perResultVo.getString("e01a1")+":"+point_id.toLowerCase());
				String degreeDesc = "";

				String hjsoft = "true";
				if(scoreRankDegree!=null && scoreRankDegree.trim().length()>0)
				{
					double achveScore = perResultVo.getDouble("c_" + point_id.toLowerCase()); // 此指标的考核得分
					score = scoreRankDegree.substring(0,scoreRankDegree.indexOf("`"));
					rank = scoreRankDegree.substring(scoreRankDegree.indexOf("`")+1,scoreRankDegree.indexOf("*"));
					if(rank==null || rank.trim().length()<=0)
						rank = "0.0";
					String degreeCode = scoreRankDegree.substring(scoreRankDegree.indexOf("&")+1,scoreRankDegree.indexOf("@"));

					if(score!=null && score.trim().length()>0 && !"0.0".equals(score))
					{
						double minScore = achveScore/(Double.parseDouble(score)*Double.parseDouble(rank));
						if("1".equals(this.template_vo.getString("status")))
							minScore = achveScore/(Double.parseDouble(score));
						String bottom_value = getFieldGradeCodeList(point_id,degreeCode);
						if(bottom_value!=null && bottom_value.trim().length()>0 && minScore<Double.parseDouble(bottom_value)) // 此人此指标考核不合格：显示推送按钮，可以此指标关联的学习课程推送给此人
						{
							hjsoft = "false";
						}
					}
				}


				bodyHtml.append("<tr height='50' >");
				// 画表体列
				LazyDynaBean dbean=null;
				for(int x=0;x<headList.size();x++)
				{
					dbean=(LazyDynaBean)headList.get(x);
					String itemid=(String)dbean.get("itemid");     // 表头字段ID
					String itemdesc=(String)dbean.get("itemdesc"); // 表头描述信息
					int itemWidth=((Integer)dbean.get("itemWidth")).intValue(); // 表头各列宽度

					// 画指标的上级、上上级项目
					if("item_id".equalsIgnoreCase(itemid))
					{
						ArrayList linkParentList=(ArrayList)leafItemLinkMap.get(item_id);
						int e=linkParentList.size()-1;
						int y=linkParentList.size();
						for(int n=0;n<this.lay;n++)
						{
							if(e>=0)
							{
								bean=(LazyDynaBean)linkParentList.get(e);
								String itemId=(String)bean.get("item_id");
								if(existWriteItem.get(itemId)!=null)
								{
									e--;
									y--;
									continue;
								}
								existWriteItem.put(itemId,"1");
								String itemDesc=(String)bean.get("itemdesc");
								//画出一个父项目
								int rowspan=((itemPointNum.get(itemId)==null?0:((Integer)itemPointNum.get(itemId)).intValue()));
								bodyHtml.append(writeTd(itemId,itemDesc,"center",rowspan,80));
								e--;
							}
							y--;
							if(y<0)
							{
								//  画空项目
								bodyHtml.append(writeTd("","","center",0,80));
							}
						}

					}else if("point_id".equalsIgnoreCase(itemid))
					{
						if(point_id==null || point_id.trim().length()<=0)
						{
							//  画空指标
							bodyHtml.append(writeTd("",pointname,"left",0,100));
							//	bodyHtml.append("</tr>");
							//	continue;
						}else
						{
							//  画指标
							bodyHtml.append(writeTd("",pointname,"left",0,100));
						}
						// 显示分值  已隐藏
					}else if("p0413".equalsIgnoreCase(itemid))
					{
						if(scoreRankDegree!=null && scoreRankDegree.trim().length()>0)
							score = scoreRankDegree.substring(0,scoreRankDegree.indexOf("`"));

						bodyHtml.append(writeTd("",PubFunc.round(score,1),"right",0,0));

						// 显示权重  已隐藏
					}else if("p0415".equalsIgnoreCase(itemid))
					{
						if(scoreRankDegree!=null && scoreRankDegree.trim().length()>0)
							rank = scoreRankDegree.substring(scoreRankDegree.indexOf("`")+1,scoreRankDegree.indexOf("*"));

						bodyHtml.append(writeTd("",PubFunc.round(rank,1),"right",0,0));

						// 显示要求等级
					}else if("askDegree".equalsIgnoreCase(itemid))
					{
						if(scoreRankDegree!=null && scoreRankDegree.trim().length()>0)
						{
							degreeDesc = scoreRankDegree.substring(scoreRankDegree.indexOf("*")+1,scoreRankDegree.indexOf("&"));
							if(degreeDesc.indexOf(":")!=-1)
								degreeDesc = degreeDesc.substring(0,degreeDesc.indexOf(":"));
							else if(degreeDesc.indexOf("：")!=-1)
								degreeDesc = degreeDesc.substring(0,degreeDesc.indexOf("："));
						}

						bodyHtml.append(writeTd("",degreeDesc,"left",0,70));

						// 显示达到等级
					}else if("achveDegree".equalsIgnoreCase(itemid))
					{
						String bz = "";
						if(point_id!=null && point_id.trim().length()>0)
						{
							double achveScore = perResultVo.getDouble("c_" + point_id.toLowerCase()); // 此指标的考核得分

							if(scoreRankDegree!=null && scoreRankDegree.trim().length()>0)
							{
								score = scoreRankDegree.substring(0,scoreRankDegree.indexOf("`"));
								rank = scoreRankDegree.substring(scoreRankDegree.indexOf("`")+1,scoreRankDegree.indexOf("*"));
								if(rank==null || rank.trim().length()<=0)
									rank = "0.0";
								if(score!=null && score.trim().length()>0 && !"0.0".equals(score))
								{
									/**如果为权重模板的话不乘权重*/
									double minScore = achveScore/(Double.parseDouble(score)*Double.parseDouble(rank));
									if("1".equals(this.template_vo.getString("status")))
										minScore = achveScore/(Double.parseDouble(score));
									ArrayList gradeList = getFieldGradeList(point_id);
									for(int i=0;i<gradeList.size();i++)
									{
										LazyDynaBean ldbean = (LazyDynaBean) gradeList.get(i);
										bz = (String)ldbean.get("gradedesc");
										if(bz.indexOf(":")!=-1)
											bz = bz.substring(0,bz.indexOf(":"));
										else if(bz.indexOf("：")!=-1)
											bz = bz.substring(0,bz.indexOf("："));
										String top_value = (String)ldbean.get("top_value");
										String bottom_value = (String)ldbean.get("bottom_value");

										if(i==0 && minScore>Double.parseDouble(top_value))
											break;
										if(minScore<=Double.parseDouble(top_value) && minScore>=Double.parseDouble(bottom_value))
											break;
									}
								}
							}
						}
						bodyHtml.append(writeTd("",bz,"left",0,70));

						// 显示标准分值
					}else if("ruleScore".equalsIgnoreCase(itemid))
					{
						if(scoreRankDegree!=null && scoreRankDegree.trim().length()>0)
							score = scoreRankDegree.substring(scoreRankDegree.indexOf("@")+1);

						bodyHtml.append(writeTd("",score,"right",0,0));

						// 显示实际分值
					}else if("planScore".equalsIgnoreCase(itemid))
					{
						double planScore = perResultVo.getDouble("c_" + point_id.toLowerCase()); // 此指标的考核得分

						bodyHtml.append(writeTd("",PubFunc.round(String.valueOf(planScore),2),"right",0,0));

						// 显示分值*权重	liuy 2015-11-25
					}else if("scoreRank".equalsIgnoreCase(itemid)&& "1".equals(this.template_vo.getString("status")))
					{
						double planScore = perResultVo.getDouble("c_" + point_id.toLowerCase()); // 此指标的考核得分
						double scoreRank = planScore*Double.parseDouble(rank);
						bodyHtml.append(writeTd("",PubFunc.round(String.valueOf(scoreRank),2),"right",0,0));

						// 显示行为建议
					}else if("movePropose".equalsIgnoreCase(itemid))
					{
						if("false".equalsIgnoreCase(hjsoft))
						{
							if(perCourseMap.get(point_id)!=null)
								bodyHtml.append(writePointCaseTd("proposal",proposal,"left",point_id,pointsetid));
							else
								bodyHtml.append(writeTd("",proposal,"left",0,130));
						}else
							bodyHtml.append(writeTd("","","left",0,130));
					}
				}
				bodyHtml.append("</tr>");

			}
			//邓灿改  已将此按钮放到计划旁
			/*
			bodyHtml.append("<tr>");
			bodyHtml.append("<td align='left' style='height:35px'>");
			bodyHtml.append("<hrms:priv func_id='36040201'>");
			bodyHtml.append("<input type=\"button\" name=\"b_search\" value=\"岗人匹配\" onclick=\"checkPerStion('1')\" class=\"mybutton\">");
			bodyHtml.append("</hrms:priv>");
			bodyHtml.append("</td></tr>");
			*/
			html.append("<table class='ListTable' cellspacing='0' cellpadding='0' >");
			html.append(titleHtml.toString());
			html.append(bodyHtml.toString());
			html.append("</table>");

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return html.toString();
	}
	/**
	 * 按岗位素质模型测评 获得的表格内容
	 * @param a0100
	 * @param planid
	 * @return
	 */
	public String getGradeResultHtmlByModel(String a0100,ArrayList headList,String e01a1)
	{
		StringBuffer html = new StringBuffer("");
		StringBuffer titleHtml = new StringBuffer(""); // 表头html  加以区分方便维护
		StringBuffer bodyHtml = new StringBuffer("");  // 表体html  加以区分方便维护
		try
		{
			ArrayList pointList = getPointListByModel(a0100,e01a1);  // 获得模板下的指标（按顺序）
			ArrayList templateItemList = getTemplateItemListByModel(e01a1,a0100); // 取得 模板项目记录
			HashMap leafItemLinkMap = getLeafItemLinkMapByModel(leafitemPointMap,templateItemList); // 叶子项目对应的继承关系
			HashMap itemPointNum = getItemPointNumByModel(templateItemList,leafitemPointMap); // 取得项目拥有的节点数
			// 获得能力素质各岗位定义的分值、权重和要求的等级
			HashMap postScoreMap = getE01a1RankScore();
			// 获得能力素质指标是否关联了课程
			HashMap perCourseMap = getPerpointCourse();
			// 获得考核结果表中某人的记录的主键id
			String perResultId = getPerResultId(a0100);
			RecordVo perResultVo = getPerResultVo(perResultId); // 获得考核结果表中某人的信息记录
			HashMap pointScoreMap=new HashMap();
			pointScoreMap=getPointScoreMap(a0100);

			titleHtml.append("<tr><td style='border:0px' colspan='"+(headList.size()+this.lay-1)+"' ><table width='100%'  style='border-collapse:collapse;' class='ListTable'>");
			titleHtml.append("<tr>");
			titleHtml.append("<td class='TableRow_lrt common_border_color' width='20%' valign='middle' align='center' nowrap>单位名称</td>");
			titleHtml.append("<td class='RecordRow_lt common_border_color' width='30%' valign='middle' align='left' nowrap>&nbsp;"+AdminCode.getCodeName("UN", perResultVo.getString("b0110"))+"</td>");
			titleHtml.append("<td class='TableRow_lrt common_border_color' width='20%' valign='middle' align='center' nowrap>部门</td>");
			titleHtml.append("<td class='RecordRow_lrt common_border_color' width='30%' valign='middle' align='left' nowrap>&nbsp;"+AdminCode.getCodeName("UM", perResultVo.getString("e0122"))+"</td>");

			titleHtml.append("</tr><tr>");
			titleHtml.append("<td class='TableRow_lrt common_border_color' width='20%' valign='middle' align='center' nowrap>岗位名称</td>");
			titleHtml.append("<td class='RecordRow_lt common_border_color' width='30%' valign='middle' align='left' nowrap >&nbsp;"+AdminCode.getCodeName("@K", perResultVo.getString("e01a1"))+" ");
			//	titleHtml.append("<hrms:priv func_id='36040201'>");
			if(this.userView.hasTheFunction("36040201"))
				titleHtml.append("&nbsp;<img src=\"/images/station_per.jpg\" alt=\"岗人匹配\" BORDER='0' style='cursor:hand;' onclick=\"checkPerStion('2');\">&nbsp;&nbsp;");
			//	titleHtml.append("</hrms:priv>");
			titleHtml.append("</td>");
			titleHtml.append("<td class='TableRow_lrt common_border_color' width='20%' valign='middle' align='center' nowrap>姓名</td>");
			titleHtml.append("<td class='RecordRow_lrt common_border_color' width='30%' valign='middle' align='left' nowrap >&nbsp;"+perResultVo.getString("a0101")+" ");
			//	titleHtml.append("<hrms:priv func_id='36040202'>");
			if(this.userView.hasTheFunction("36040202"))
				titleHtml.append("&nbsp;<img src=\"/images/per_station.jpg\" alt=\"人岗匹配\" BORDER='0' style='cursor:hand;' onclick=\"checkPerStion('1');\">&nbsp;&nbsp;");
			//	titleHtml.append("</hrms:priv>");
			titleHtml.append("</td>");
			titleHtml.append("</tr><tr>");
			titleHtml.append("<td class='TableRow_lrt common_border_color' width='20%' valign='middle' align='center' nowrap>考核等级</td>");
			titleHtml.append("<td class='RecordRow_lt common_border_color' width='30%' valign='middle' align='left' nowrap>&nbsp;"+perResultVo.getString("resultdesc")+"</td>");
			titleHtml.append("<td class='TableRow_lrt common_border_color' width='20%' valign='middle' align='center' nowrap>匹配度</td>");
			String matesurmise = perResultVo.getString("matesurmise");
			matesurmise = matesurmise == null || "".equals(matesurmise) ? "0" : matesurmise;
			titleHtml.append("<td class='RecordRow_lrt common_border_color' width='30%' valign='middle' align='left' nowrap>&nbsp;"+(PubFunc.multiple(matesurmise,"100", 2)+"%")+"</td>");
			titleHtml.append("</tr></table>");
			titleHtml.append("</td></tr>");
			// 输出表头
			titleHtml.append("<tr class='trDeep' height='50' >");
			LazyDynaBean abean=null;
			for(int k=0;k<headList.size();k++)
			{
				abean=(LazyDynaBean)headList.get(k);
				String itemid=(String)abean.get("itemid");     // 表头字段ID
				String itemdesc=(String)abean.get("itemdesc"); // 表头描述信息
				int itemWidth=((Integer)abean.get("itemWidth")).intValue(); // 表头各列宽度

				if("item_id".equalsIgnoreCase(itemid))
					titleHtml.append("<td class='TableTitltRow common_background_color common_border_color' valign='middle' align='center' colspan='"+this.lay+"' nowrap >"+itemdesc+"</td>");
				else
					titleHtml.append("<td class='TableTitltRow common_background_color common_border_color' valign='middle' align='center' nowrap >"+itemdesc+"</td>");
			}
			titleHtml.append("</tr>");


			// 输出表体
			LazyDynaBean bean=null;
			HashMap existWriteItem=new HashMap();  // 放已画过的上级项目、上上级项目
			//  循环所有指标
			for(int k=0;k<pointList.size();k++)
			{
				LazyDynaBean lbean = (LazyDynaBean)pointList.get(k);
				String item_id = (String)lbean.get("item_id");  // 项目ID
				String point_id = (String)lbean.get("point_id");  // 指标ID
				String pointsetid = (String)lbean.get("pointsetid");  // 指标分类ID
				String pointname = (String)lbean.get("pointname"); // 指标名称
				String proposal = (String)lbean.get("proposal"); // 行为建议
				String score = (String)lbean.get("score");     // 指标得分
				String rank = (String)lbean.get("rank");     // 指标权重
				String scoreRankDegree = (String)postScoreMap.get(perResultVo.getString("e01a1")+":"+point_id.toLowerCase());
				String degreeDesc = "";

				String hjsoft = "true";
				if(scoreRankDegree!=null && scoreRankDegree.trim().length()>0)
				{
					String point_id_score =(String) pointScoreMap.get(point_id.toLowerCase())==null?"0":(String) pointScoreMap.get(point_id.toLowerCase());
					double achveScore = Double.parseDouble(point_id_score); // 此指标的考核得分
					score = scoreRankDegree.substring(0,scoreRankDegree.indexOf("`"));
					rank = scoreRankDegree.substring(scoreRankDegree.indexOf("`")+1,scoreRankDegree.indexOf("*"));
					if(rank==null || rank.trim().length()<=0)
						rank = "0.0";
					String degreeCode = scoreRankDegree.substring(scoreRankDegree.indexOf("&")+1,scoreRankDegree.indexOf("@"));

					if(score!=null && score.trim().length()>0 && !"0.0".equals(score))
					{
						double minScore = achveScore/(Double.parseDouble(score)*Double.parseDouble(rank));
						if("1".equals(this.template_vo.getString("status")))
							minScore = achveScore/(Double.parseDouble(score));
						String bottom_value = getFieldGradeCodeList(point_id,degreeCode);
						if(bottom_value!=null && bottom_value.trim().length()>0 && minScore<Double.parseDouble(bottom_value)) // 此人此指标考核不合格：显示推送按钮，可以此指标关联的学习课程推送给此人
						{
							hjsoft = "false";
						}
					}
				}


				bodyHtml.append("<tr height='50' >");
				// 画表体列
				LazyDynaBean dbean=null;
				for(int x=0;x<headList.size();x++)
				{
					dbean=(LazyDynaBean)headList.get(x);
					String itemid=(String)dbean.get("itemid");     // 表头字段ID
					String itemdesc=(String)dbean.get("itemdesc"); // 表头描述信息
					int itemWidth=((Integer)dbean.get("itemWidth")).intValue(); // 表头各列宽度

					// 画指标的上级、上上级项目
					if("item_id".equalsIgnoreCase(itemid))
					{
						ArrayList linkParentList=(ArrayList)leafItemLinkMap.get(item_id);
						int e=linkParentList.size()-1;
						int y=linkParentList.size();
						for(int n=0;n<this.lay;n++)
						{
							if(e>=0)
							{
								bean=(LazyDynaBean)linkParentList.get(e);
								String itemId=(String)bean.get("item_id");
								if(existWriteItem.get(itemId)!=null)
								{
									e--;
									y--;
									continue;
								}
								existWriteItem.put(itemId,"1");
								String itemDesc=(String)bean.get("itemdesc");
								//画出一个父项目
								int rowspan=((itemPointNum.get(itemId)==null?0:((Integer)itemPointNum.get(itemId)).intValue()));
								bodyHtml.append(writeTd(itemId,itemDesc,"center",rowspan,80));
								e--;
							}
							y--;
							if(y<0)
							{
								//  画空项目
								bodyHtml.append(writeTd("","","center",0,80));
							}
						}

					}else if("point_id".equalsIgnoreCase(itemid))
					{
						if(point_id==null || point_id.trim().length()<=0)
						{
							//  画空指标
							bodyHtml.append(writeTd("",pointname,"left",0,100));
							//	bodyHtml.append("</tr>");
							//	continue;
						}else
						{
							//  画指标
							bodyHtml.append(writeTd("",pointname,"left",0,100));
						}
						// 显示分值  已隐藏
					}else if("p0413".equalsIgnoreCase(itemid))
					{
						if(scoreRankDegree!=null && scoreRankDegree.trim().length()>0)
							score = scoreRankDegree.substring(0,scoreRankDegree.indexOf("`"));

						bodyHtml.append(writeTd("",PubFunc.round(score,1),"right",0,0));

						// 显示权重  已隐藏
					}else if("p0415".equalsIgnoreCase(itemid))
					{
						if(scoreRankDegree!=null && scoreRankDegree.trim().length()>0)
							rank = scoreRankDegree.substring(scoreRankDegree.indexOf("`")+1,scoreRankDegree.indexOf("*"));

						bodyHtml.append(writeTd("",PubFunc.round(rank,1),"right",0,0));

						// 显示要求等级
					}else if("askDegree".equalsIgnoreCase(itemid))
					{
						if(scoreRankDegree!=null && scoreRankDegree.trim().length()>0)
						{
							degreeDesc = scoreRankDegree.substring(scoreRankDegree.indexOf("*")+1,scoreRankDegree.indexOf("&"));
							if(degreeDesc.indexOf(":")!=-1)
								degreeDesc = degreeDesc.substring(0,degreeDesc.indexOf(":"));
							else if(degreeDesc.indexOf("：")!=-1)
								degreeDesc = degreeDesc.substring(0,degreeDesc.indexOf("："));
						}

						bodyHtml.append(writeTd("",degreeDesc,"left",0,70));

						// 显示达到等级
					}else if("achveDegree".equalsIgnoreCase(itemid))
					{
						String bz = "";
						if(point_id!=null && point_id.trim().length()>0)
						{
							String point_id_score =(String) pointScoreMap.get(point_id.toLowerCase())==null?"0":(String) pointScoreMap.get(point_id.toLowerCase());
							double achveScore = Double.parseDouble(point_id_score); // 此指标的考核得分

							if(scoreRankDegree!=null && scoreRankDegree.trim().length()>0)
							{
								score = scoreRankDegree.substring(0,scoreRankDegree.indexOf("`"));
								rank = scoreRankDegree.substring(scoreRankDegree.indexOf("`")+1,scoreRankDegree.indexOf("*"));
								if(rank==null || rank.trim().length()<=0)
									rank = "0.0";
								if(score!=null && score.trim().length()>0 && !"0.0".equals(score))
								{
									/**如果为权重模板的话不乘权重*/
									double minScore = achveScore/(Double.parseDouble(score)*Double.parseDouble(rank));
									if("1".equals(this.template_vo.getString("status")))
										minScore = achveScore/(Double.parseDouble(score));
									ArrayList gradeList = getFieldGradeList(point_id);
									int domainflag = getDomainFlag(plan_id);
									for(int i=0;i<gradeList.size();i++)
									{
										LazyDynaBean ldbean = (LazyDynaBean) gradeList.get(i);
										bz = (String)ldbean.get("gradedesc");
										if(bz.indexOf(":")!=-1)
											bz = bz.substring(0,bz.indexOf(":"));
										else if(bz.indexOf("：")!=-1)
											bz = bz.substring(0,bz.indexOf("："));
										String top_value = (String)ldbean.get("top_value");
										String bottom_value = (String)ldbean.get("bottom_value");
										if(domainflag == 0){
											if(i==0 && minScore>Double.parseDouble(top_value))
												break;
											if(minScore<=Double.parseDouble(top_value) && minScore>Double.parseDouble(bottom_value))
												break;
										}else if (domainflag == 1) {
											if(i==0 && minScore>Double.parseDouble(top_value))
												break;
											if(minScore<Double.parseDouble(top_value) && minScore>=Double.parseDouble(bottom_value))
												break;
										}
									}
								}
							}
						}
						bodyHtml.append(writeTd("",bz,"left",0,70));

						// 显示标准分值
					}else if("ruleScore".equalsIgnoreCase(itemid))
					{
						if(scoreRankDegree!=null && scoreRankDegree.trim().length()>0)
							score = scoreRankDegree.substring(scoreRankDegree.indexOf("@")+1);

						bodyHtml.append(writeTd("",score,"right",0,0));

						// 显示实际分值
					}else if("planScore".equalsIgnoreCase(itemid))
					{
						String point_id_score =(String) pointScoreMap.get(point_id.toLowerCase())==null?"0":(String) pointScoreMap.get(point_id.toLowerCase());
						double planScore = Double.parseDouble(point_id_score); // 此指标的考核得分

						bodyHtml.append(writeTd("",PubFunc.round(String.valueOf(planScore),2),"right",0,0));
						// 显示分值*权重 	liuy 2015-11-25
					}else if("scoreRank".equalsIgnoreCase(itemid))
					{
						String point_id_score =(String) pointScoreMap.get(point_id.toLowerCase())==null?"0":(String) pointScoreMap.get(point_id.toLowerCase());
						double planScore = Double.parseDouble(point_id_score); // 此指标的考核得分
						double scoreRank = planScore*Double.parseDouble(rank);
						bodyHtml.append(writeTd("",PubFunc.round(String.valueOf(scoreRank),2),"right",0,0));

						// 显示行为建议
					}else if("movePropose".equalsIgnoreCase(itemid))
					{
						if("false".equalsIgnoreCase(hjsoft))
						{
							if(perCourseMap.get(point_id)!=null)
								bodyHtml.append(writePointCaseTd("proposal",proposal,"left",point_id,pointsetid));
							else
								bodyHtml.append(writeTd("",proposal,"left",0,130));
						}else
							bodyHtml.append(writeTd("","","left",0,130));
					}
				}
				bodyHtml.append("</tr>");

			}
			//邓灿改  已将此按钮放到计划旁
			/*
			bodyHtml.append("<tr>");
			bodyHtml.append("<td align='left' style='height:35px'>");
			bodyHtml.append("<hrms:priv func_id='36040201'>");
			bodyHtml.append("<input type=\"button\" name=\"b_search\" value=\"岗人匹配\" onclick=\"checkPerStion('1')\" class=\"mybutton\">");
			bodyHtml.append("</hrms:priv>");
			bodyHtml.append("</td></tr>");
			*/
			html.append("<table class='ListTable' cellspacing='0' cellpadding='0' >");
			html.append(titleHtml.toString());
			html.append(bodyHtml.toString());
			html.append("</table>");

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return html.toString();
	}

	/**
	 * 得到等级分类的封闭标识
	 * @param plan_id
	 * @return
	 */
	private int getDomainFlag(String plan_id){
		int domainflag=0;
		ContentDAO dao = new ContentDAO(this.conn);
		String ssql="select domainflag from per_degree where degree_id='"+getGradeClass(plan_id)+"'";
		ResultSet res = null;
		try {
			res = dao.search(ssql);
			if(res.next()){//取得匹配度等级分类的封闭标识
				domainflag=res.getInt("domainflag");//0:上限封闭 	1:下限封闭
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(res!=null)
				PubFunc.closeResource(res);
		}
		return domainflag;
	}

	/**
	 * 得到考核计划等级分类参数
	 * @param plan_id
	 * @return
	 */
	public String getGradeClass(String plan_id){
		String gradeClass="";
		try{
			LoadXml loadxml=null;
			if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null){
				loadxml=new LoadXml(this.conn,plan_id);
				BatchGradeBo.getPlanLoadXmlMap().put(plan_id,loadxml);
			}else
				loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
			Hashtable planParam=loadxml.getDegreeWhole();
			gradeClass = (String)planParam.get("GradeClass");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return gradeClass;
	}

	/**
	 * 画单元格
	 * @return
	 */
	private String writeTd(String itemid,String context,String align,int rowspan,int width)
	{
		StringBuffer td=new StringBuffer("");
		td.append("\r\n<td class='RecordBodyRow common_border_color' valign='middle' align='"+align+"'");
		if(rowspan!=0)
			td.append(" rowspan='"+(rowspan)+"' ");
		else
			td.append(" height='50' ");
		if(width!=0)
			td.append(" width='"+width+"'");
		td.append(" nowrap>&nbsp;");
		td.append(context);
		td.append("&nbsp;</td>");
		return td.toString();
	}

	/**
	 * 画单元格
	 * @return
	 */
	private String writePointCaseTd(String itemid,String context,String align,String point_id,String pointsetid)
	{
		StringBuffer td=new StringBuffer("");
		td.append("\r\n<td class='RecordBodyRow common_border_color' valign='middle' align='"+align+"'");
		td.append(" >&nbsp;");
		td.append(context);
		//	td.append(" &nbsp;&nbsp;<img src=\"/images/arrow2.gif\" BORDER='0' style='cursor:hand;' onclick=\"selectAbilityClass('"+point_id+"','"+pointsetid+"','35');\">&nbsp;&nbsp;");
		if(this.userView.hasTheFunction("36030312")){
			td.append(" &nbsp;&nbsp;<img src=\"/images/send_class.jpg\" alt=\"推送课程\" BORDER='0' style='cursor:hand;' onclick=\"sendLessons('"+point_id+"');\">&nbsp;&nbsp;");
		}
		td.append("</td>");
		return td.toString();
	}

	/**
	 * 取得 模板项目记录
	 * @return
	 */
	public ArrayList getTemplateItemList()
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String sql="select * from per_template_item where template_id='"+this.plan_vo.getString("template_id")+"' ";
			sql+=" order by seq";
			RowSet rowSet=dao.search(sql);

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
	 * 按岗位素质测评 取得 模板项目记录  //无父子项目关系
	 * @return
	 */
	public ArrayList getTemplateItemListByModel(String e01a1 ,String a0100)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			buf.append(" select ph.point_id item_id,codeitem.codeitemdesc itemdesc from per_history_result ph ");
			buf.append("left join (select * from codeitem where codesetid='70') codeitem  ");
			buf.append(" on ph.point_id=codeitem.codeitemid  where plan_id='"+this.plan_id+"' and object_id='"+a0100+"' and status='1' ");
			RowSet rowSet=dao.search(buf.toString());

			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				abean.set("item_id",rowSet.getString("item_id"));
				if(rowSet.getString("item_id")!=null&& "-9999".equals(rowSet.getString("item_id"))){
					//abean.set("itemdesc","无指标分类");   岗位素质模型指标没有指标分类  2013.12.3 pjf
					abean.set("itemdesc","岗位素质模型指标");
				}else{
					abean.set("itemdesc",isNull(rowSet.getString("itemdesc")));
				}

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
	 * 取得项目拥有的节点数
	 * @return
	 */
	public HashMap getItemPointNum(ArrayList templateItemList,HashMap leafitemPointMap)
	{

		HashMap map=new HashMap();
		LazyDynaBean a_bean=null;
		LazyDynaBean aa_bean=null;
		for(int i=0;i<templateItemList.size();i++)
		{
			a_bean=(LazyDynaBean)templateItemList.get(i);
			ArrayList list=new ArrayList();
			getLeafItemList2(a_bean,list,templateItemList);
			int n=0;
			for(int j=0;j<list.size();j++)
			{
				aa_bean=(LazyDynaBean)list.get(j);
				String item_id=(String)aa_bean.get("item_id");
				if(leafitemPointMap.get(item_id)!=null)
					n+=((ArrayList)leafitemPointMap.get(item_id)).size();

			}
			map.put((String)a_bean.get("item_id"),new Integer(n));
		}
		return map;
	}
	public void getLeafItemList2(LazyDynaBean abean,ArrayList list,ArrayList templateItemList)
	{
		String item_id=(String)abean.get("item_id");
		String child_id=(String)abean.get("child_id");

		if(child_id.length()==0)
		{
			list.add(abean);
			return;
		}
		else if(leafitemPointMap.get(item_id)!=null)  //**
		{
			list.add(abean);
		}
		LazyDynaBean a_bean=null;
		for(int j=0;j<templateItemList.size();j++)
		{
			a_bean=(LazyDynaBean)templateItemList.get(j);
			String parent_id=(String)a_bean.get("parent_id");
			if(parent_id.equals(item_id))
				getLeafItemList2(a_bean,list,templateItemList);
		}
	}

	/**
	 * 按岗位素质模型测评 取得项目拥有的节点数
	 * @return
	 */
	public HashMap getItemPointNumByModel(ArrayList templateItemList,HashMap leafitemPointMap)
	{
		HashMap map=new HashMap();
		LazyDynaBean a_bean=null;
		for(int i=0;i<templateItemList.size();i++)
		{
			a_bean=(LazyDynaBean)templateItemList.get(i);
			ArrayList list=new ArrayList();
			String item_id=(String) a_bean.get("item_id");
			if(leafitemPointMap.get(item_id)!=null){
				int n=0;
				list=(ArrayList) leafitemPointMap.get(item_id);
				n=((ArrayList)leafitemPointMap.get(item_id)).size();
				map.put((String)a_bean.get("item_id"),new Integer(n));
			}

		}
		return map;
	}

	/**
	 * 叶子项目对应的继承关系
	 * @return
	 */
	public HashMap getLeafItemLinkMap(HashMap itemPointMap,ArrayList templateItemList)
	{
		HashMap map=new HashMap();
		try
		{
			LazyDynaBean abean=null;
			Set keySet=itemPointMap.keySet();
			for(Iterator t=keySet.iterator();t.hasNext();)
			{
				String item_id=(String)t.next();
				ArrayList linkList=new ArrayList();
				getParentItem(linkList,item_id,templateItemList);
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
	/**
	 * 按岗位素质指标测评  叶子项目对应的继承关系  仅一层
	 * @return
	 */
	public HashMap getLeafItemLinkMapByModel(HashMap itemPointMap,ArrayList templateItemList)
	{
		HashMap map=new HashMap();
		try
		{
			LazyDynaBean abean=null;
			Set keySet=itemPointMap.keySet();
			for(Iterator t=keySet.iterator();t.hasNext();)
			{
				String item_id=(String)t.next();
				ArrayList linkList=new ArrayList();
				for(int i=0;i<templateItemList.size();i++){
					abean=(LazyDynaBean) templateItemList.get(i);
					String _item_id=(String) abean.get("item_id");
					if(item_id.equals(_item_id)){
						linkList.add(abean);
						break;
					}
				}
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
	public void getParentItem(ArrayList list,String item_id,ArrayList templateItemList)
	{

		String parent_id="";
		LazyDynaBean abean=null;
		for(int i=0;i<templateItemList.size();i++)
		{
			abean=(LazyDynaBean)templateItemList.get(i);
			String _parent_id=(String)abean.get("parent_id");
			String _item_id=(String)abean.get("item_id");
			if(_item_id.equalsIgnoreCase(item_id))
			{
				parent_id=_parent_id;
				break;
			}
		}

		if(parent_id.length()==0)
		{
			list.add(abean);
			return;
		}

		LazyDynaBean a_bean=null;
		for(int i=0;i<templateItemList.size();i++)
		{
			a_bean=(LazyDynaBean)templateItemList.get(i);
			String _itemid=(String)a_bean.get("item_id");
			String _parentid=(String)a_bean.get("parent_id");
			if(_itemid.equals(parent_id))
			{
				list.add(abean);
				getParentItem(list,_itemid,templateItemList);
			}
		}
	}



	/**
	 * 获得模板下的指标（按顺序）
	 * @param planVo
	 * @return
	 */
	public ArrayList getPointList()
	{
		ArrayList pointList=new ArrayList();
		try
		{
			LazyDynaBean bean=null;
			LazyDynaBean _bean=null;
			RowSet rs=null;
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer("");

			ArrayList _pointList=new ArrayList();
			buf.append("select po.point_id,po.pointsetid,po.pointname,po.proposal,pp.item_id,pi.itemdesc,pp.score,pp.rank from per_template_item pi,per_template_point pp,per_point po ");
			buf.append(" where pi.item_id=pp.item_id and pp.point_id=po.point_id and template_id='"+this.plan_vo.getString("template_id")+"' ");
			buf.append(" order by pp.seq ");
			rs=dao.search(buf.toString());
			while(rs.next())
			{
				bean=new LazyDynaBean();
				bean.set("point_id",isNull(rs.getString("point_id")));
				bean.set("pointsetid",isNull(rs.getString("pointsetid")));
				bean.set("pointname",isNull(rs.getString("pointname")));
				bean.set("score",isNull(rs.getString("score")));
				bean.set("rank",isNull(rs.getString("rank")));
				bean.set("proposal",isNull(rs.getString("proposal")));
				bean.set("item_id",isNull(rs.getString("item_id")));
				bean.set("itemdesc",isNull(rs.getString("itemdesc")));
				_pointList.add(bean);
			}

			buf.setLength(0);
			buf.append("select * from per_template_item where item_id in ( ");
			buf.append(" select item_id from per_template_item where template_id='"+this.plan_vo.getString("template_id")+"' and child_id is null ");
			buf.append(" union all ");
			buf.append(" select distinct pti.item_id from per_template_item pti,per_template_point ptp ");
			buf.append(" where pti.item_id=ptp.item_id and pti.template_id='"+this.plan_vo.getString("template_id")+"' ) order by seq ");
			rs=dao.search(buf.toString());
			while(rs.next())
			{
				String item_id = isNull(rs.getString("item_id"));
				String itemdesc = isNull(rs.getString("itemdesc"));
				int num=0;
				ArrayList tempList=new ArrayList();
				for(int i=0;i<_pointList.size();i++)
				{
					bean=(LazyDynaBean)_pointList.get(i);
					String _point_id=(String)bean.get("point_id");
					String _pointsetid=(String)bean.get("pointsetid");
					String _pointname=(String)bean.get("pointname");
					String _score=(String)bean.get("score");
					String _rank=(String)bean.get("rank");
					String _proposal=(String)bean.get("proposal");
					String _item_id=(String)bean.get("item_id");
					String _itemdesc=(String)bean.get("itemdesc");
					if(_item_id.equalsIgnoreCase(item_id))
					{
						_bean=new LazyDynaBean();
						_bean.set("point_id",_point_id);
						_bean.set("pointsetid",_pointsetid);
						_bean.set("pointname",_pointname);
						_bean.set("score",_score);
						_bean.set("rank",_rank);
						_bean.set("proposal",_proposal);
						_bean.set("item_id",_item_id);
						_bean.set("itemdesc",_itemdesc);
						pointList.add(_bean);
						tempList.add(_bean);
						num++;
					}
				}

				if(num==0)
				{
					_bean=new LazyDynaBean();
					_bean.set("point_id","");
					_bean.set("pointsetid","");
					_bean.set("pointname","");
					_bean.set("score","");
					_bean.set("rank","");
					_bean.set("proposal","");
					_bean.set("item_id",item_id);
					_bean.set("itemdesc",itemdesc);
					pointList.add(_bean);
					tempList.add(_bean);
				}
				this.leafitemPointMap.put(item_id, tempList);
			}
			if(rs!=null)
				rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return pointList;
	}
	/**
	 * 按岗位素质测评获得岗位下的指标
	 * @param planVo
	 * @return
	 */
	public ArrayList getPointListByModel(String a0100,String e01a1)
	{
		ArrayList pointList=new ArrayList();
		try
		{
			LazyDynaBean bean=null;
			LazyDynaBean _bean=null;
			RowSet rs=null;
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer("");

			ArrayList _pointList=new ArrayList();
			buf.append("select T.*,I.itemdesc  from ( ");
			buf.append("select pp.point_id,pp.pointsetid,pp.pointname,pp.proposal,pm.point_type item_id ,pm.score,pm.rank  from per_competency_modal pm,per_point pp"
					+" where  pm.object_id='"+e01a1+"' and pm.object_type='3' and pm.point_id=pp.point_id and pm.point_id in" +
					"(select point_id from per_history_result where plan_id='"+this.plan_id+"' and object_id='"+a0100+"' and status='0' )");
			buf.append(" )T left join ( ");
			buf.append("select codeitemid item_id,codeitemdesc itemdesc from codeitem where CodeSetId='70' ");
			buf.append(") I on T.item_id=I.item_id");
			rs=dao.search(buf.toString());
			while(rs.next())
			{
				bean=new LazyDynaBean();
				bean.set("point_id",isNull(rs.getString("point_id")));
				bean.set("pointsetid",isNull(rs.getString("pointsetid")));
				bean.set("pointname",isNull(rs.getString("pointname")));
				bean.set("score",isNull(rs.getString("score")));
				bean.set("rank",isNull(rs.getString("rank")));
				bean.set("proposal",isNull(rs.getString("proposal")));
				if(rs.getString("item_id")==null|| "".equals(rs.getString("item_id"))){
					bean.set("item_id","-9999");
				}else{
					bean.set("item_id",isNull(rs.getString("item_id")));
				}

				bean.set("itemdesc",isNull(rs.getString("itemdesc")));
				_pointList.add(bean);
			}

			buf.setLength(0);
			buf.append(" select ph.point_id item_id,codeitem.codeitemdesc itemdesc from per_history_result ph ");
			buf.append("left join (select * from codeitem where codesetid='70') codeitem  ");
			buf.append(" on ph.point_id=codeitem.codeitemid  where plan_id='"+this.plan_id+"' and object_id='"+a0100+"' and status='1' ");
			rs=dao.search(buf.toString());
			while(rs.next())
			{
				String item_id ="";
				if(rs.getString("item_id")==null|| "".equals(rs.getString("item_id"))){
					item_id="-9999";
				}else{
					item_id=isNull(rs.getString("item_id"));
				}
				String itemdesc = isNull(rs.getString("itemdesc"));
				int num=0;
				ArrayList tempList=new ArrayList();
				for(int i=0;i<_pointList.size();i++)
				{
					bean=(LazyDynaBean)_pointList.get(i);
					String _point_id=(String)bean.get("point_id");
					String _pointsetid=(String)bean.get("pointsetid");
					String _pointname=(String)bean.get("pointname");
					String _score=(String)bean.get("score");
					String _rank=(String)bean.get("rank");
					String _proposal=(String)bean.get("proposal");
					String _item_id=(String)bean.get("item_id");
					String _itemdesc=(String)bean.get("itemdesc");
					if(_item_id.equalsIgnoreCase(item_id))
					{
						_bean=new LazyDynaBean();
						_bean.set("point_id",_point_id);
						_bean.set("pointsetid",_pointsetid);
						_bean.set("pointname",_pointname);
						_bean.set("score",_score);
						_bean.set("rank",_rank);
						_bean.set("proposal",_proposal);
						_bean.set("item_id",_item_id);
						_bean.set("itemdesc",_itemdesc);
						pointList.add(_bean);
						tempList.add(_bean);
						num++;
					}
				}

				if(num==0)
				{
					_bean=new LazyDynaBean();
					_bean.set("point_id","");
					_bean.set("pointsetid","");
					_bean.set("pointname","");
					_bean.set("score","");
					_bean.set("rank","");
					_bean.set("proposal","");
					_bean.set("item_id",item_id);
					_bean.set("itemdesc",itemdesc);
					pointList.add(_bean);
					tempList.add(_bean);
				}
				this.leafitemPointMap.put(item_id, tempList);
			}
			if(rs!=null)
				rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return pointList;
	}
	public String isNull(String str)
	{
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str))
			str = "";
		return str;
	}

	/**
	 * 考核指标的等级信息记录
	 * @param point_id
	 * @return
	 */
	public ArrayList getFieldGradeList(String point_id)
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{
			PersonPostModalBo ppo = new PersonPostModalBo(this.conn);
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(ppo.getComOrPer(point_id,"poi"))
				per_comTable = "per_grade_competence"; // 能力素质标准标度
			String sql = "select t.gradedesc as bz,g.* from per_grade g left join "+per_comTable+" t on g.gradecode=t.grade_template_id where g.point_id ='"+point_id+"' order by g.gradevalue desc";
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("bz",rs.getString("bz")==null?"":rs.getString("bz"));
				bean.set("gradedesc",rs.getString("gradedesc")==null?"":rs.getString("gradedesc").replaceAll("\r\n", "<br>"));
				bean.set("gradevalue",rs.getString("gradevalue")==null?"0":this.myformat.format(rs.getDouble("gradevalue")));
				bean.set("gradeid",rs.getString("gradecode")==null?"":rs.getString("gradecode"));
				bean.set("top_value",rs.getString("top_value")==null?"0":this.myformat.format(rs.getDouble("top_value")));
				bean.set("bottom_value",rs.getString("bottom_value")==null?"0":this.myformat.format(rs.getDouble("bottom_value")));
				bean.set("grade_id", rs.getString("grade_id"));
				list.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 考核指标的等级信息记录
	 * @param point_id,gradecode
	 * @return
	 */
	public String getFieldGradeCodeList(String point_id,String gradecode)
	{
		String bottom_value = "";
		RowSet rs = null;
		try
		{
			String sql = "select bottom_value from per_grade where point_id ='"+point_id+"' and gradecode ='"+gradecode+"' ";
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			if(rs.next())
			{
				bottom_value = rs.getString("bottom_value")==null?"0":this.myformat.format(rs.getDouble("bottom_value"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bottom_value;
	}

	/**获得能力素质指标是否关联了课程*/
	public HashMap getPerpointCourse()
	{
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			StringBuffer sql = new StringBuffer("");
			sql.append("select * from per_point_course");
			rowSet = dao.search(sql.toString());
			while(rowSet.next())
			{
				String point_id = isNull(rowSet.getString("point_id"));
				String r5000 = isNull(rowSet.getString("r5000"));

				map.put(point_id,r5000);
			}
			if(rowSet!=null)
				rowSet.close();

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/**获得能力素质指标关联的课程*/
	public ArrayList getPerpointCourseList(String point_id)
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{
			String sql = "select r5000 from per_point_course where point_id ='"+point_id+"' ";
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("r5000",rs.getString("r5000")==null?"":rs.getString("r5000"));
				list.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/**获得培训课程关联的课件*/
	public ArrayList getCourseWareList(String r5000)
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{
			String sql = "select r5100 from r51 where r5000=" + r5000;
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("r5100",rs.getString("r5100")==null?"":rs.getString("r5100"));
				list.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/**获得能力素质各岗位定义的分值、权重和要求的等级*/
	public HashMap getE01a1RankScore()
	{
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"); // 获取当前时间
		try
		{
			String startDate=PersonPostMatchingBo.getPlanStartDate(dao, plan_id);
			if(!"".equals(startDate)){
				creatDate=startDate;
			}
			StringBuffer sql=new StringBuffer();
			sql.append("select pc.object_id,pc.point_id,pc.score,pc.rank,pc.gradecode,pg.top_value,pg.bottom_value,pg.gradedesc ");
			sql.append(" from per_competency_modal pc,per_grade pg ");
			sql.append(" where pc.object_type = 3 ");
			sql.append(" and pc.point_id=pg.point_id and pc.gradecode=pg.gradecode ");
			sql.append(" and "+Sql_switcher.dateValue(creatDate)+" between start_date and end_date");

			rowSet = dao.search(sql.toString());
			while(rowSet.next())
			{
				String object_id = isNull(rowSet.getString("object_id"));
				String point_id = isNull(rowSet.getString("point_id"));
				String score = isNull(rowSet.getString("score"));
				String rank = isNull(rowSet.getString("rank"));
				String gradecode = isNull(rowSet.getString("gradecode"));
				String gradedesc = isNull(rowSet.getString("gradedesc"));
				String bottom_value = isNull(rowSet.getString("bottom_value"));
				if(bottom_value==null || bottom_value.trim().length()<=0)
					bottom_value = "0";
				String top_value = isNull(rowSet.getString("top_value"));
				if(top_value==null || top_value.trim().length()<=0)
					top_value = "0";

				/**如果为权重模板的话不乘权重*/
				double bottomScore = (Double.parseDouble(score)*Double.parseDouble(rank)*Double.parseDouble(bottom_value));
				double topScore = (Double.parseDouble(score)*Double.parseDouble(rank)*Double.parseDouble(top_value));
				if("1".equals(this.template_vo.getString("status")))
				{
					bottomScore = (Double.parseDouble(score)*Double.parseDouble(bottom_value));
					topScore = (Double.parseDouble(score)*Double.parseDouble(top_value));
				}
				if(this.byModel!=null&& "1".equals(this.byModel)){
					bottomScore = (Double.parseDouble(score)*Double.parseDouble(bottom_value));
					topScore = (Double.parseDouble(score)*Double.parseDouble(top_value));
				}
				String scoreFw = PubFunc.round(String.valueOf(bottomScore),1)+"~"+PubFunc.round(String.valueOf(topScore),1);


				map.put(object_id+":"+point_id.toLowerCase(),score+"`"+rank+"*"+gradedesc+"&"+gradecode+"@"+scoreFw);
			}
			if(rowSet!=null)
				rowSet.close();

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/**获得考核结果表中某人的记录的主键id*/
	public String getPerResultId(String object_id)
	{
		String id = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			StringBuffer sql = new StringBuffer("");
			sql.append("select id from per_result_" + this.plan_id + " where object_id='" + object_id + "' ");
			rowSet = dao.search(sql.toString());
			if (rowSet.next())
				id = isNull(rowSet.getString("id"));

			if(rowSet!=null)
				rowSet.close();

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return id;
	}
	/**
	 * 获得考核结果表中某人的记录的主键id*/
	public String getPerResultIdByModel(String object_id)
	{
		String id = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			StringBuffer sql = new StringBuffer("");
			sql.append("select id from per_history_result where object_id='" + object_id + "' and plan_id='"+ this.plan_id+"'");
			rowSet = dao.search(sql.toString());
			if (rowSet.next())
				id = isNull(rowSet.getString("id"));

			if(rowSet!=null)
				rowSet.close();

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return id;
	}
	/**
	 *
	 * @Title: getE01a1
	 * @Description:   得到考核对象的岗位
	 * @param @param object_id
	 * @param @return
	 * @return String
	 * @throws
	 */
	public String getE01a1(String object_id){
		String e01a1 = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			StringBuffer sql = new StringBuffer("");
			sql.append("select e01a1 from per_history_result where object_id='" + object_id + "' and plan_id='"+ this.plan_id+"'");
			rowSet = dao.search(sql.toString());
			if (rowSet.next())
				e01a1 = isNull(rowSet.getString("e01a1"));

			if(rowSet!=null)
				rowSet.close();

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return e01a1;
	}
	/**
	 *
	 * @Title: getPointScore
	 * @Description:    得到考核指标分数
	 * @param @param pointId
	 * @param @param objectId
	 * @param @return
	 * @return double
	 * @throws
	 */
	public double getPointScore(String pointId,String objectId){
		double pointScore = 0.0;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			StringBuffer sql = new StringBuffer("");
			sql.append("select score from per_history_result where point_id='"+pointId+"' and object_id='" + objectId + "' and plan_id='"+ this.plan_id+"'");
			rowSet = dao.search(sql.toString());
			if (rowSet.next())
				pointScore = rowSet.getDouble("score");

			if(rowSet!=null)
				rowSet.close();

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return pointScore;
	}
	/**
	 * 考核结果表中某人的信息记录
	 * @param planid
	 * @return
	 */
	public RecordVo getPerResultVo(String id)
	{
		RecordVo vo=new RecordVo("per_result_" + this.plan_id);
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			vo.setInt("id",Integer.parseInt(id));
			vo=dao.findByPrimaryKey(vo);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	/**
	 * 考核结果表中某人的信息记录
	 * @param planid
	 * @return
	 */
	public RecordVo getPerResultVoByModel(String id)
	{
		RecordVo vo=new RecordVo("per_history_result");
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			vo.setInt("id",Integer.parseInt(id));
			vo=dao.findByPrimaryKey(vo);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	/**
	 * 取得表头列表
	 * @return
	 */
	public ArrayList getHeadList()
	{
		ArrayList list = new ArrayList();
		FieldItem item = null;
		try
		{
			// 项目名称
			LazyDynaBean abean = null;
			abean = new LazyDynaBean();
			abean.set("itemid", "item_id");
			abean.set("itemdesc","项目名称");
			abean.set("itemType","A");
			abean.set("codesetid","0");
			abean.set("decimalwidth","0");
			abean.set("itemWidth",new Integer(td_width));
			list.add(abean);

			// 指标名称
			abean = new LazyDynaBean();
			abean.set("itemid", "point_id");
			abean.set("itemdesc","指标名称");
			abean.set("itemType","A");
			abean.set("codesetid","0");
			abean.set("decimalwidth","0");
			abean.set("itemWidth",new Integer(td_width));
			list.add(abean);
/*
			// 分值
			item=DataDictionary.getFieldItem("p0413");
			abean=new LazyDynaBean();
			abean.set("itemid", item.getItemid());
			abean.set("itemdesc",item.getItemdesc());
			abean.set("itemType",item.getItemtype());
			abean.set("codesetid",item.getCodesetid());
			abean.set("decimalwidth",String.valueOf(item.getDecimalwidth()));
			abean.set("itemWidth",new Integer(td_width));
			list.add(abean);

			// 如果为权重模板的话，加一列权重
//			if(this.template_vo.getString("status").equals("1"))
//	        {
				// 权重
				item=DataDictionary.getFieldItem("p0415");
				abean=new LazyDynaBean();
				abean.set("itemid", item.getItemid());
				abean.set("itemdesc",item.getItemdesc());
				abean.set("itemType",item.getItemtype());
				abean.set("codesetid",item.getCodesetid());
				abean.set("decimalwidth",String.valueOf(item.getDecimalwidth()));
				abean.set("itemWidth",new Integer(td_width));
				list.add(abean);
//	        }
*/
			// 要求等级
			abean = new LazyDynaBean();
			abean.set("itemid", "askDegree");
			abean.set("itemdesc","要求等级");
			abean.set("itemType","A");
			abean.set("codesetid","0");
			abean.set("decimalwidth","0");
			abean.set("itemWidth",new Integer(td_width));
			list.add(abean);

			// 达到等级
			abean = new LazyDynaBean();
			abean.set("itemid", "achveDegree");
			abean.set("itemdesc","达到等级");
			abean.set("itemType","A");
			abean.set("codesetid","0");
			abean.set("decimalwidth","0");
			abean.set("itemWidth",new Integer(td_width));
			list.add(abean);

			// 标准分值
			abean = new LazyDynaBean();
			abean.set("itemid", "ruleScore");
			abean.set("itemdesc","标准分值");
			abean.set("itemType","N");
			abean.set("codesetid","0");
			abean.set("decimalwidth","2");
			abean.set("itemWidth",new Integer(td_width));
			list.add(abean);

			// 考核得分
			abean = new LazyDynaBean();
			abean.set("itemid", "planScore");
			abean.set("itemdesc","实际分值");
			abean.set("itemType","N");
			abean.set("codesetid","0");
			abean.set("decimalwidth","2");
			abean.set("itemWidth",new Integer(td_width));
			list.add(abean);

			// 分值*权重  	liuy 2015-11-25
			if("1".equals(this.template_vo.getString("status"))){
				abean = new LazyDynaBean();
				abean.set("itemid", "scoreRank");
				abean.set("itemdesc","分值*权重");
				abean.set("itemType","N");
				abean.set("codesetid","0");
				abean.set("decimalwidth","2");
				abean.set("itemWidth",new Integer(td_width));
				list.add(abean);
			}

			// 行为建议
			abean = new LazyDynaBean();
			abean.set("itemid", "movePropose");
			abean.set("itemdesc","行为建议");
			abean.set("itemType","A");
			abean.set("codesetid","0");
			abean.set("decimalwidth","0");
			abean.set("itemWidth",new Integer(td_width));
			list.add(abean);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	public String compareDate(String historyDate)
	{
		String str="0";
		try
		{
			if(historyDate==null|| "".equals(historyDate))
				str="1";
			else{
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				String now = format.format(new Date());
				Date hdate=format.parse(historyDate);
				Date ndate = format.parse(now);
				if(hdate.compareTo(ndate)==1||hdate.compareTo(ndate)==0)
					str="1";
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	public HashMap getPointScoreMap(String a0100){
		HashMap map =new HashMap();
		ContentDAO dao  = new ContentDAO(this.conn);
		ResultSet rs=null;
		String point_id="";
		String score="";
		String sql ="select * from per_history_result where plan_id='"+this.plan_id+"' and object_id='"+a0100+"' and status='0' ";
		try {
			rs=dao.search(sql);
			while(rs.next()){
				point_id=isNull(rs.getString("point_id").toLowerCase());
				score=rs.getString("score")==null?"0":rs.getString("score");
				map.put(point_id, score);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return map;
	}
}