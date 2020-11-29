package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.achivement.PointCtrlXmlBo;
import com.hjsj.hrms.businessobject.performance.achivement.StandardItemBo;
import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>Title:SaveFieldTrans.java</p>
 * <p>Description:保存新建或编辑的指标</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2010-07-21</p> 
 * @author JinChunhai
 * @version 1.0
 */

public class SaveFieldTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		
		try
		{
			String subsys_id = (String)this.getFormHM().get("subsys_id");			
			String pointsetid = (String)this.getFormHM().get("pointsetid");
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String tabid=(String)map.get("tabid");
			String fieldnumber = (String)this.getFormHM().get("fieldnumber");
		    String fieldname = (String)this.getFormHM().get("fieldname");
		    fieldname=fieldname.replaceAll(",", "，");
		    fieldname=fieldname.replaceAll("'","‘");
		    String pointkind=(String)this.getFormHM().get("pointkind");
	        String fieldvlidflag=(String)this.getFormHM().get("fieldvlidflag");
			String description=(String)this.getFormHM().get("description");
			String proposal=(String)this.getFormHM().get("proposal");
			String visible=(String)this.getFormHM().get("visible");
		    String status=(String)map.get("status");
			String hiddennumber=(String)this.getFormHM().get("hiddennumber");
			String saveandcontinue=(String)map.get("saveandcontinue");
			String type=(String)map.get("type");
		    String gd_principle=(String)this.getFormHM().get("gd_principle");
			String kh_content=(String)this.getFormHM().get("kh_content");
			String computeFormula = (String)this.getFormHM().get("computeFormula"); // 定义的指标公式
			computeFormula = PubFunc.keyWord_reback(computeFormula);
			//pointkind=1 and status=1取规则的值
			//-----
			
			String aa=(String)this.getFormHM().get("aastr");
			String ff = (String)this.getFormHM().get("ffstr");
			String cc=(String)this.getFormHM().get("ccstr");
			String dd=(String)this.getFormHM().get("ddstr");
			String ee = (String)this.getFormHM().get("eestr");
			KhFieldBo bo = new KhFieldBo(this.getFrameconn());
			ContentDAO dao = new ContentDAO(this.getFrameconn());
//			pointkind=1 and status=1取规则的值
			String pointtype="0";
			String ltype="1";
			String add_type="0";
			String minus_type="1";
			String rule="0";
			String add_value="";
			String minus_value="";
			String add_score="";
			String minus_score="";
			String add_valid="0";
			String minus_valid="0";
			String convert="0";
			if("1".equals(pointkind)&& "1".equals(status))
			{
				pointtype=(String)this.getFormHM().get("pointtype");
				ltype=(String)this.getFormHM().get("ltype");
				add_type=(String)this.getFormHM().get("add_type");
				minus_type=(String)this.getFormHM().get("minus_type");
				add_value=(String)this.getFormHM().get("add_value");
				add_score=(String)this.getFormHM().get("add_score");
				add_valid=(String)this.getFormHM().get("add_valid");
				minus_value=(String)this.getFormHM().get("minus_value");
				minus_score=(String)this.getFormHM().get("minus_score");
				minus_valid=(String)this.getFormHM().get("minus_valid");
				rule=(String)this.getFormHM().get("rule");
				convert=(String)this.getFormHM().get("convert");			
			}
			/**新增*/
			//type=3为复制dumeilong
			if("1".equals(type)|| "3".equals(type))
			{
				int seq = bo.getNextAddOne("seq", "per_point");
				RecordVo vo = new RecordVo("per_point");
				vo.setString("point_id",fieldnumber);
				vo.setString("pointname",fieldname);
				vo.setString("pointsetid",pointsetid);
				vo.setInt("seq",seq);
				vo.setString("pointkind",pointkind);
				vo.setString("validflag", fieldvlidflag);
				vo.setString("description",description);
				vo.setString("proposal",proposal);
				vo.setString("visible", visible);
				vo.setString("status",status);
				vo.setInt("pointtype",Integer.parseInt(pointtype));
				vo.setString("kh_content", kh_content);
				vo.setString("formula", computeFormula);
				vo.setString("gd_principle",gd_principle);
				dao.addValueObject(vo);
				StringBuffer context = new StringBuffer();
				context.append("新增指标：【"+fieldnumber+":"+fieldname+"】<br>");
				context.append("指标类型："+pointkind+"(0:定性要点；1:定量要点)<br>");
				context.append("指标解释："+description+"<br>");
				context.append("指标标度和解释显示："+visible+"(1显示解释,2标度,其它不显示)<br>");
				context.append("有效标识："+fieldvlidflag+"(0:无效,1:有效)<br>");
				context.append("考核内容："+kh_content+"<br>");
				context.append("评分原则："+gd_principle+"<br>");
				context.append("计算公式："+computeFormula+"<br>");
				this.getFormHM().put("@eventlog", context.toString());
				if(!(this.userView.isSuper_admin())&&!"1".equals(this.userView.getGroupId()))
				{
					UserObjectBo user_bo=new UserObjectBo(this.getFrameconn());
					user_bo.saveResource(fieldnumber,this.userView,IResourceConstant.KH_FIELD);
				}

				StandardItemBo SIB= new StandardItemBo();
				/**在建规则的时候，指标还保存，point_id存入的是一个固定值，在指标保存后，将point_id改成新建指标的point_id*/
				SIB.updatePointid(fieldnumber, dao);
				if("3".equals(type))
				{//dml
					String sqlt="select * from per_standard_item where point_id='"+fieldnumber+"'";
					this.frowset=dao.search(sqlt);
					if(this.frowset.next())
					{
						dao.delete("delete from per_standard_item where point_id='"+fieldnumber+"'", new ArrayList());
					}
					ArrayList list=this.getScore(hiddennumber);
					if(list!=null&&list.size()>0)
					{
						String sqll="insert into per_standard_item(item_id,itemdesc,score,point_id,top_value,bottom_value,parent_id,child_id,seq)values(?,?,?,?,?,?,?,?,?)";
						LazyDynaBean bean=this.getMaxNumber(hiddennumber);
						int item_id;
						int seq2;
						if(bean!=null)
						{
							item_id=Integer.parseInt((String)bean.get("itemid"));
						
							seq2=Integer.parseInt((String)bean.get("seq"));
						}else
						{
							item_id=0;
							seq2=0;
						}
						ArrayList klist=new ArrayList();
						HashMap relationc=(HashMap)list.get(list.size()-1);
						list.remove(list.size()-1);
						HashMap relationp=(HashMap)list.get(list.size()-1);
						HashMap remember=new HashMap();
						list.remove(list.size()-1);
						HashMap relations=(HashMap)list.get(list.size()-1);
						list.remove(list.size()-1);
						HashMap hmm=new HashMap();
						for(Iterator t = list.iterator(); t.hasNext();)
						{
							LazyDynaBean dmlbean=(LazyDynaBean) t.next();
							ArrayList temp=new ArrayList();
							item_id++;
							if(dmlbean.get("itemid")!=null)
							{
								remember.put(dmlbean.get("itemid"), String.valueOf(item_id));
							}
							
							temp.add(String.valueOf(item_id));
							temp.add((String)dmlbean.get("itemdesc")==null?"":(String)dmlbean.get("itemdesc"));
							temp.add("NULL".equals(dmlbean.get("score"))?null:dmlbean.get("score"));
							temp.add((String)fieldnumber);
							temp.add("NULL".equals(dmlbean.get("topvalue"))?null:dmlbean.get("topvalue"));
							temp.add("NULL".equals(dmlbean.get("bottomvalue"))?null:dmlbean.get("bottomvalue"));
							hmm.put(dmlbean.get("itemid"), temp);
						}
						Iterator rr=hmm.entrySet().iterator();
						while(rr.hasNext())
						{
							String ktempc="";
							String ktempp="";
							Map.Entry entry = (Map.Entry) rr.next();
							String keys = (String) entry.getKey();
							ArrayList values = (ArrayList)entry.getValue();
							String tempp=(String)relationp.get(keys);
							if(tempp!=null)
							{
								ktempp=(String)remember.get(tempp);
								values.add(ktempp);
							}else
							{
								values.add(null);
							}
							String tempc=(String)relationc.get(keys);
							if(tempc!=null)
							{
								ktempc=(String)remember.get(tempc);
								if(ktempc==null)
								{
									values.add(String.valueOf(Integer.parseInt((String)remember.get(keys))+100));
								}else
									values.add(ktempc);
							}else
							{
								values.add(null);
							}
							if(relations!=null)
							{
								if(relations.get(keys)!=null)
								{
									values.add(String.valueOf(seq2+Integer.parseInt((String)relations.get(keys))+1));
								}
							}
							
							klist.add(values);
						}
						dao.batchInsert(sqll, klist);
					}
				}
			}
			/**修改*/
			if("2".equals(type))
			{
				/*RecordVo vo = new RecordVo("per_point");
				vo.setString("point_id",hiddennumber);
				vo=dao.findByPrimaryKey(vo);
				if(vo!=null)
				{
					vo.setString("point_id", fieldnumber);
					vo.setString("pointname", fieldname);
					vo.setString("pointkind",pointkind);
					vo.setString("validflag",fieldvlidflag);
					vo.setString("visible",visible);
					vo.setString("status",status);
					vo.setString("pointtype", pointtype);
					vo.setString("description", description);
					dao.updateValueObject(vo);
				}*/
				StringBuffer buf = new StringBuffer();
				buf.append("update per_point set point_id='");
				buf.append(fieldnumber+"',pointname=?");
				buf.append(",pointkind='");
				buf.append(pointkind+"',validflag='");
				buf.append(fieldvlidflag+"',visible='");
				buf.append(visible+"',status='");
				buf.append(status+"',pointtype="+pointtype+",description=? ");
				buf.append(",gd_principle=?,kh_content=?,formula=?,proposal=? ");
				buf.append(" where point_id='");
				buf.append(hiddennumber+"' and pointsetid='");
				buf.append(pointsetid+"'");
				ArrayList list = new ArrayList();
				list.add(fieldname);
				list.add(description);
				list.add(gd_principle);
				list.add(kh_content);
				list.add(computeFormula);
				list.add(proposal);
				dao.update(buf.toString(),list);
				StringBuffer context = new StringBuffer();
				context.append("修改指标：【"+fieldnumber+":"+fieldname+"】<br>");
				context.append("指标类型："+pointkind+"(0:定性要点；1:定量要点)<br>");
				context.append("指标解释："+description+"<br>");
				context.append("指标标度和解释显示："+visible+"(1显示解释,2标度,其它不显示)<br>");
				context.append("有效标识："+fieldvlidflag+"(0:无效,1:有效)<br>");
				context.append("考核内容："+kh_content+"<br>");
				context.append("评分原则："+gd_principle+"<br>");
				context.append("计算公式："+computeFormula+"<br>");
				this.getFormHM().put("@eventlog", context.toString());
			}
			if("1".equals(pointkind)&& "1".equals(status))
			{
				PointCtrlXmlBo PCXB = new PointCtrlXmlBo(fieldnumber,this.getFrameconn());
				PCXB.initXML();
				PCXB.setPropertyValue(PointCtrlXmlBo.param, "convert", convert, "", "", "", "", "", "");
				/**基本的指标才保存加减分规则*/
				if("0".equals(pointtype))
				{
	    			PCXB.setPropertyValue(PointCtrlXmlBo.l_method, "type", ltype, "rule", rule, "", "", "", "");
	    			if("1".equals(rule))
	    			{
	    				if("1".equals(ltype))
	    				{
	    					
	    				    BigDecimal b = new BigDecimal("100");
	    				    if(add_value!=null&&!"".equals(add_value))
	    				    {
	    				    	BigDecimal a = new BigDecimal(add_value);
	    				    	add_value=a.divide(b).doubleValue()+"";
	    				    }
	    				    if(minus_value!=null&&!"".equals(minus_value))
	    				    {
	    			    	    BigDecimal c = new BigDecimal(minus_value);
	    		    			minus_value=c.divide(b).doubleValue()+"";
	    				    }
	    				}
	    			}
	    			PCXB.setPropertyValue(PointCtrlXmlBo.add_score, "type", add_type,"value", add_value, "score",add_score, "valid",add_valid);
	    			PCXB.setPropertyValue(PointCtrlXmlBo.minus_score, "type", minus_type,"value", minus_value, "score",minus_score, "valid",minus_valid);
				}
				PCXB.updatePointCtrl();
			}
			bo.deleteFieldGrade(fieldnumber);
			
			String [] a_arr=aa.split("∑");//标度代码
			String [] f_arr=ff.split("∑");//标度内容
			String [] c_arr=cc.split("∑");//分值
			String [] d_arr=dd.split("∑");//上限值
			String [] e_arr=ee.split("∑");//下限值
			RecordVo vo = null;
			for(int i=0;i<a_arr.length;i++)
			{
				vo=new RecordVo("per_grade");
				int grade_id = new Integer(bo.getMaxNextId("per_grade","grade_id")).intValue();
				vo.setInt("grade_id", grade_id);
				vo.setString("point_id",fieldnumber);
				vo.setString("gradecode",a_arr[i]);
				vo.setString("gradedesc",f_arr[i]);
				vo.setString("gradevalue",c_arr[i]==null||"#".equals(c_arr[i])||"＃".equals(c_arr[i])?"":c_arr[i]);
				vo.setString("top_value",d_arr[i]==null||"#".equals(d_arr[i])||"＃".equals(d_arr[i])?"":d_arr[i]);
				vo.setString("bottom_value",e_arr[i]==null||"#".equals(e_arr[i])||"＃".equals(e_arr[i])?"":e_arr[i]);
				dao.addValueObject(vo);

			}

			String isClose="1";
			ArrayList newgradeList=null;
			if("1".equals(saveandcontinue))
			{
				newgradeList=new ArrayList();
				
			}
			else
			{
			    fieldnumber = bo.getNextSeq("point_id", "per_point");
			    fieldname="";
			    pointkind="0";
		        fieldvlidflag="1";
				description="";
				proposal="";
				gd_principle="";
				kh_content="";
				computeFormula="";
				visible="3";
			    status="";
				//pointtype="";
				hiddennumber="";
				isClose="2";
				newgradeList=bo.getGradeTemplateList(subsys_id);
				ltype="1";
				add_type="0";
			    minus_type="1";
				rule="0";
				add_value="";
				minus_value="";
				add_score="";
				minus_score="";
				add_valid="0";
				minus_valid="0";
				convert="0";
				pointtype="0";
			}
			this.getFormHM().put("fieldname", fieldname);
			this.getFormHM().put("pointkind", pointkind==null|| "".equals(pointkind)?"0":pointkind);
			this.getFormHM().put("fieldvlidflag", fieldvlidflag==null|| "".equals(fieldvlidflag)?"1":fieldvlidflag);
			this.getFormHM().put("description", description);
			this.getFormHM().put("proposal", proposal);
			this.getFormHM().put("visible", visible==null|| "".equals(visible)?"3":visible);
			this.getFormHM().put("status", status);
			this.getFormHM().put("pointtype", pointtype);
			this.getFormHM().put("fieldnumber", fieldnumber);
			this.getFormHM().put("hiddennumber", hiddennumber);
			this.getFormHM().put("pointsetid",pointsetid);
			this.getFormHM().put("display", "0".equals(pointkind)?"display:none;":"display:block;");
			this.getFormHM().put("type",type);
			this.getFormHM().put("newgradeList",newgradeList);
			this.getFormHM().put("isClose",isClose);
			this.getFormHM().put("isrefresh", "2");
			this.getFormHM().put("ltype",ltype);
			this.getFormHM().put("add_type",add_type);
			this.getFormHM().put("minus_type", minus_type);
			this.getFormHM().put("rule",rule);
			this.getFormHM().put("add_value",add_value);
			this.getFormHM().put("minus_value", minus_value);
			this.getFormHM().put("add_score",add_score);
			this.getFormHM().put("minus_score",minus_score);
			this.getFormHM().put("add_valid",add_valid);
			this.getFormHM().put("minus_valid",minus_valid);
			this.getFormHM().put("convert",convert);
			this.getFormHM().put("tabid", tabid);
			this.getFormHM().put("gd_principle", gd_principle);
			this.getFormHM().put("kh_content", kh_content);
			this.getFormHM().put("computeFormula", computeFormula);
			this.getFormHM().put("kpiTarget_id", "");
			this.getFormHM().put("kpiTargetType", "");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
	//dml
	public ArrayList getScore(String number)
	{
		ArrayList dmllist=new ArrayList();
		String sql="select * from per_standard_item where point_id='"+number+"' order by seq";
		ContentDAO dao=new ContentDAO(this.frameconn);
		String itemid="";
		String parentid="";
		String childid="";
		HashMap relationp=new HashMap();
		HashMap relationc=new HashMap();
		HashMap relations=new HashMap();
		int i=0;
		try 
		{
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				LazyDynaBean dmbean=new LazyDynaBean();
				dmbean.set("itemid", this.frowset.getString("item_id"));
				dmbean.set("itemdesc",this.frowset.getString("itemdesc")==null?"":this.frowset.getString("itemdesc"));
				dmbean.set("score", this.frowset.getString("score")==null?"NULL":PubFunc.round(this.frowset.getString("score"),2));
				dmbean.set("parnetid", this.frowset.getString("parent_id")==null?"NULL":this.frowset.getString("parent_id"));
				dmbean.set("childid", this.frowset.getString("child_id")==null?"NULL":this.frowset.getString("child_id"));
				dmbean.set("topvalue", this.frowset.getString("top_value")==null?"NULL":PubFunc.round(this.frowset.getString("top_value"),2));
				dmbean.set("bottomvalue",this.frowset.getString("bottom_value")==null?"NULL":PubFunc.round(this.frowset.getString("bottom_value"),2));
				dmllist.add(dmbean);
				itemid=this.frowset.getString("item_id")==null?"":(String)this.frowset.getString("item_id");
				if(this.frowset.getString("parent_id")!=null)
				{					
					 parentid=(String)this.frowset.getString("parent_id");
					 if(itemid!=null&&itemid.length()!=0)
					 {
						 relationp.put(itemid, parentid);
					 }
				}
				if(this.frowset.getString("child_id")!=null)
				{
					childid=this.frowset.getString("child_id");
					 if(itemid!=null&&itemid.length()!=0)
					 {
						 relationc.put(itemid, childid);
					 }
				}
				if(this.frowset.getString("seq")!=null)
				{
					 if(itemid!=null&&itemid.length()!=0)
					 {
						 relations.put(itemid, String.valueOf(i++));
					 }
				}
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		dmllist.add(relations);
		dmllist.add(relationp);
		dmllist.add(relationc);
		
		return dmllist;
		
	}
	
	//dml
	public LazyDynaBean getMaxNumber(String number)
	{
		LazyDynaBean bean=new LazyDynaBean();
		String sql="select max(item_id) as itemid,max(seq) as seq from per_standard_item ";
		ContentDAO dao=new ContentDAO(this.frameconn);
		try 
		{
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				bean.set("itemid", this.frowset.getString("itemid")!=null?this.frowset.getString("itemid"):"0");
				bean.set("seq", this.frowset.getString("seq")!=null?this.frowset.getString("seq"):"0");
			}
			
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return bean;
	}
	
}
