package com.hjsj.hrms.businessobject.performance.commend_table;

import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
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
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class CommendTableBo {
	/***
	 * 
	 */
	Connection conn;
	String tableType="0";
	UserView userView;
	public CommendTableBo(Connection conn,UserView userView) throws GeneralException 
	{
		this.conn=conn;
		this.userView=userView;
		this.tableType=this.getTableTypeValue();
	}
	public CommendTableBo(Connection conn,UserView userView,int type) throws GeneralException 
	{
		this.conn=conn;
		this.userView=userView;
	}
	public String getTableType()
	{
		return this.tableType;
	}
	public String getTableTypeValue()throws GeneralException
	{
		String tableType="0";
		try
		{
    		String base_unit=SystemConfig.getPropertyValue("base_unit");
    		if(base_unit==null|| "".equals(base_unit.trim()))
    		{
    			throw GeneralExceptionHandler.Handle(new Exception("系统未定义总部单位ID！"));
    		}
    		//ContentDAO dao = new ContentDAO(this.conn);
	    	//HashMap mp=getUserInfo(this.userView.getA0100(), this.userView.getDbname(), dao);
	    	String b0110="";
	    	String e0122="";
	    	//登录用户看见的表的类型，=0省级单位用表，=1总部用表
	    	base_unit=base_unit.trim();
	    	if(this.userView.getUserOrgId()!=null)
    			b0110=this.userView.getUserOrgId();
    		if(this.userView.getUserDeptId()!=null)
	    		e0122=this.userView.getUserDeptId();
    		if(!"".equals(b0110))
	    	{
	    		if(b0110.startsWith(base_unit))
	    			tableType="1";
	    	}
	    	else if(!"".equals(e0122))
    		{
    			if(e0122.startsWith(base_unit))
	    			tableType="1";
	    	}
    		else if(this.userView.getManagePrivCode()!=null)
    		{
	    		if("UN".equalsIgnoreCase(this.userView.getManagePrivCode()))
	    		{
	    			if(this.userView.getManagePrivCodeValue()==null|| "".equals(this.userView.getManagePrivCodeValue()))
	    				tableType="1";
	    			else if(this.userView.getManagePrivCodeValue().startsWith(base_unit))
	    				tableType="1";
	     		}else if("UM".equalsIgnoreCase(this.userView.getManagePrivCode()))
	    		{
	    			 if(this.userView.getManagePrivCodeValue().startsWith(base_unit))
	    				tableType="1";
	    		}
    		}
    		else
    		{
    			throw GeneralExceptionHandler.Handle(new Exception("您的单位编号，部门编号，管理范围都是空值，系统无法为您展现推荐表!"));
    		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return tableType;
	}
	/**
	 * 登录用户看见的表的类型，=0省级单位用表，=1总部用表
	 * @param tableType
	 * @param userView
	 * @return
	 */
	public HashMap getCommendList()throws GeneralException
	{
		HashMap map = new HashMap();
		try
		{
			ArrayList list = new ArrayList();
			String flag="0";
			StringBuffer cloumns=new StringBuffer("");
			ContentDAO dao = new ContentDAO(this.conn);
			String priv="";
			if("0".equals(this.tableType))
			{
				priv=SystemConfig.getPropertyValue("recommend_unit_item");
			}
			else
			{
				priv=SystemConfig.getPropertyValue("recommend_depart_item");
			}
			String msg="";
			if("0".equals(this.tableType))
				msg="推荐单位指标";
			else
				msg="推荐部门指标";
			if(priv==null|| "".equals(priv.trim()))
			{
				throw GeneralExceptionHandler.Handle(new Exception(msg+"未定义"));
				
			}
			else
			{
				FieldItem fielditem = DataDictionary.getFieldItem(priv.trim());
				if(fielditem==null||!"a01".equalsIgnoreCase(fielditem.getFieldsetid()))
					throw GeneralExceptionHandler.Handle(new Exception(msg+"定义错误,该指标必须已构库并且为人员主集中指标"));
			}
			String leader_flag=SystemConfig.getPropertyValue("leader_flag");
			if(leader_flag==null|| "".equals(leader_flag.trim()))
			{
				throw GeneralExceptionHandler.Handle(new Exception("领导标识指标未定义"));
			}
			else
			{
				FieldItem fielditem = DataDictionary.getFieldItem(leader_flag.trim());
				if(fielditem==null||!"a01".equalsIgnoreCase(fielditem.getFieldsetid()))
					throw GeneralExceptionHandler.Handle(new Exception("领导标识指标定义错误,该指标必须已构库并且为人员主集中指标"));
			}
			String recommend_type=SystemConfig.getPropertyValue("recommend_type");
			if(recommend_type==null|| "".equals(recommend_type.trim()))
			{
				throw GeneralExceptionHandler.Handle(new Exception("推荐人员类别指标未定义"));
			}
			else
			{
				FieldItem fielditem = DataDictionary.getFieldItem(recommend_type.trim());
				if(fielditem==null||!"a01".equalsIgnoreCase(fielditem.getFieldsetid()))
					throw GeneralExceptionHandler.Handle(new Exception("推荐人员类别指标定义错误,该指标必须已构库并且为人员主集中指标"));
			}
			StringBuffer astr=new StringBuffer("");
			String privValue="null";
			RowSet rowSet = dao.search("select "+priv+" from usra01 where a0100='"+userView.getA0100()+"'");
			while(rowSet.next())
			{
				privValue=((rowSet.getString(priv.trim())==null|| "".equals(rowSet.getString(priv.trim())))?"null":rowSet.getString(priv.trim()));
			}
			if("1".equals(tableType))
			{
				cloumns.append("a.C1,a.C2,a.C3,a.C4,a.C5,a.C6");
				astr.append("0 as C1,0 as C2,0 as C3,0 as C4,0 as C5,0 as C6");
			}
			else
			{
				cloumns.append("a.C3,a.C4,a.C5,a.C6,a.C7,a.C8,a.C9,a.C10");
				astr.append("0 as C7,0 as C8,0 as C3,0 as C4,0 as C5,0 as C6,0 as C9,0 as C10");
			}
			StringBuffer sql = new StringBuffer("select * from ((select a.a0100_1,a.a0101_1,a.flag,a.recommend_type,b.a0000,");
			sql.append(cloumns+" from per_recommend_result a,usra01 b where ");
			sql.append(" (a.c11=0 or a.c11 is null) and a.a0100='"+userView.getA0100()+"' and UPPER(a.nbase)='"+userView.getDbname().toUpperCase()+"'");
			sql.append(" and b.a0100=a.a0100_1)");
			sql.append(" union (");
			sql.append("select a0100 as a0100_1,a0101 as a0101_1,0 as flag,"+recommend_type+" as recommend_type,a0000,"+astr);
			sql.append(" from usra01 where "+leader_flag+"='1' and ");
			if("1".equals(this.tableType))
				sql.append(" e0122 like ");
			else
				sql.append("b0110 like ");
			sql.append("'"+privValue+"%'");
			sql.append(" and a0100 not in (select a0100_1 as a0100 from per_recommend_result where ");
			sql.append(" (c11=0 or c11 is null) and a0100='"+userView.getA0100()+"' and UPPER(nbase)='"+userView.getDbname().toUpperCase()+"'");
			sql.append(" and a0101_1<>'无合适人员')))");
			sql.append(" T order by a0000");
			RowSet rs =null;
			rs = dao.search(sql.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				if("2".equals(rs.getString("flag")))
					flag="2";
				if("无合适人员".equalsIgnoreCase(rs.getString("a0101_1")))
					continue;
				bean.set("a0100_1",rs.getString("a0100_1"));
				/*if(userView.getA0100().equals(rs.getString("a0100_1")))
				{
					bean.set("self","1");
				}
				else
				{*/
					bean.set("self","0");
				/*}*/
				bean.set("a0101_1", rs.getString("a0101_1"));
				bean.set("recommend_type",rs.getString("recommend_type")==null?"":rs.getString("recommend_type"));
				if("1".equals(tableType))
				{
					bean.set("C1",(rs.getString("C1")==null?"0":rs.getString("C1")));
					bean.set("C2",(rs.getString("C2")==null?"0":rs.getString("C2")));
					bean.set("C3",(rs.getString("C3")==null?"0":rs.getString("C3")));
					bean.set("C4",(rs.getString("C4")==null?"0":rs.getString("C4")));
					bean.set("C5",(rs.getString("C5")==null?"0":rs.getString("C5")));
					bean.set("C6",(rs.getString("C6")==null?"0":rs.getString("C6")));
				}
				else
				{
					bean.set("C3",(rs.getString("C3")==null?"0":rs.getString("C3")));
					bean.set("C4",(rs.getString("C4")==null?"0":rs.getString("C4")));
					bean.set("C5",(rs.getString("C5")==null?"0":rs.getString("C5")));
					bean.set("C6",(rs.getString("C6")==null?"0":rs.getString("C6")));
					bean.set("C7",(rs.getString("C7")==null?"0":rs.getString("C7")));
					bean.set("C8",(rs.getString("C8")==null?"0":rs.getString("C8")));
					bean.set("C9",(rs.getString("C9")==null?"0":rs.getString("C9")));
					bean.set("C10",(rs.getString("C10")==null?"0":rs.getString("C10")));
				}
			    list.add(bean);
			}
			rowSet.close();
			rs.close();
			map.put("list", list);
			map.put("flag",flag);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}
	public ArrayList getCommendList2(String commendUnit,String recommend_flag_item)
	{
		ArrayList list = new ArrayList();
		try
		{
			String recommend_type=SystemConfig.getPropertyValue("recommend_type");
			if(recommend_type==null|| "".equals(recommend_type.trim()))
			{
				throw GeneralExceptionHandler.Handle(new Exception("推荐人员类别指标未定义"));
			}
			else
			{
				FieldItem fielditem = DataDictionary.getFieldItem(recommend_type.trim());
				if(fielditem==null||!"a01".equalsIgnoreCase(fielditem.getFieldsetid()))
					throw GeneralExceptionHandler.Handle(new Exception("推荐人员类别指标定义错误,该指标必须已构库并且为人员主集中指标"));
			}
			StringBuffer sql = new StringBuffer("select * from ((select a.a0100_1,a.a0101_1,a.flag,a.recommend_type,b.a0000,");
			sql.append("c11 from per_recommend_result a,usra01 b where ");
			sql.append(" a.c11=1 and a.a0100='"+userView.getA0100()+"' and UPPER(a.nbase)='"+userView.getDbname().toUpperCase()+"'");
			sql.append(" and b.a0100=a.a0100_1)");
			sql.append(" union (");
			sql.append("select a0100 as a0100_1,a0101 as a0101_1,0 as flag,"+recommend_type+" as recommend_type,a0000,0 as c11");
			sql.append(" from usra01 where "+recommend_flag_item+"='1' and ");
			sql.append("b0110 like ");
			sql.append("'"+commendUnit+"%'");
			sql.append(" and a0100 not in (select a0100_1 as a0100 from per_recommend_result where ");
			sql.append(" c11=1 and a0100='"+userView.getA0100()+"' and UPPER(nbase)='"+userView.getDbname().toUpperCase()+"'");
			sql.append(" and a0101_1<>'无合适人员')))");
			sql.append(" T order by a0000");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				if("无合适人员".equalsIgnoreCase(rs.getString("a0101_1")))
					continue;
				bean.set("a0101_1", rs.getString("a0101_1"));
				bean.set("a0100_1", rs.getString("a0100_1"));
				bean.set("recommend_type",rs.getString("recommend_type"));
				bean.set("C11", rs.getString("c11"));
				bean.set("recommend_unit", commendUnit);
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
	 * 保存结果
	 * @param commendList 选择推荐人列表
	 * @param opttype 操作方式=0保存，=1完成（即flag值）
	 */
	public void saveCommendList(ArrayList commendList,String opttype)
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String priv="";
			if("0".equals(this.tableType))
			{
				priv=SystemConfig.getPropertyValue("recommend_unit_item").trim();
			}
			else
			{
				priv=SystemConfig.getPropertyValue("recommend_depart_item").trim();
			}
			String recommend_unit="";
			RowSet rowSet=null;
			rowSet=dao.search("select "+priv+" from "+this.userView.getDbname()+"A01 where a0100='"+this.userView.getA0100()+"'");
			if(rowSet.next())
				recommend_unit=rowSet.getString(1)!=null?rowSet.getString(1):"";
			//HashMap map = (this.getUserInfo(userView.getA0100(), userView.getDbname(), dao));
			String b0110=this.userView.getUserOrgId();
			String e0122=this.userView.getUserDeptId();
			String sql=" delete from per_recommend_result where a0100='"+userView.getA0100()+"' and (c11<>1 or c11 is null)";
			dao.delete(sql, new ArrayList());
			int j=0;
			ArrayList list = new ArrayList();
			for(int i=0;i<commendList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)commendList.get(i);
				/*String self=(String)bean.get("self");
				if(self.equals("1"))
				{
					j++;
					continue;
				}*/
				if("1".equals(this.tableType))
				{
					String C1=(String)bean.get("C1");
					String C2=(String)bean.get("C2");
					String C3=(String)bean.get("C3");
					String C4=(String)bean.get("C4");
					String C5=(String)bean.get("C5");
					String C6=(String)bean.get("C6");
					String recommend_type=(String)bean.get("recommend_type");
					if("0".equals(C1)&& "0".equals(C2)&& "0".equals(C3)&& "0".equals(C4)&& "0".equals(C5)&& "0".equals(C6))
					{
						j++;
						continue;
					}
					RecordVo vo = new RecordVo("per_recommend_result");
					IDGenerator idg = new IDGenerator(2,conn);
					String id=IDGenerator.getKeyId("per_recommend_result", "id", 1);
					vo.setInt("id",Integer.parseInt(id));
					vo = dao.findByPrimaryKey(vo);
					if(vo!=null)
					{
						vo.setString("c1", C1);
						vo.setString("c2", C2);
						vo.setString("c3", C3);
						vo.setString("c4", C4);
						vo.setString("c5", C5);
						vo.setString("c6", C6);
						vo.setString("flag",opttype);
						vo.setString("recommend_type", recommend_type);
						vo.setString("b0110",b0110);
						vo.setString("e0122",e0122);
						vo.setString("nbase", userView.getDbname());
						vo.setString("a0101_1", (String)bean.get("a0101_1"));
						vo.setString("a0100", userView.getA0100());
						vo.setString("a0100_1", (String)bean.get("a0100_1"));
						vo.setString("recommend_unit", recommend_unit);
						list.add(vo);
					}
				}
				else
				{
					String C3=(String)bean.get("C3");
					String C4=(String)bean.get("C4");
					String C5=(String)bean.get("C5");
					String C6=(String)bean.get("C6");
					String C7=(String)bean.get("C7");
					String C8=(String)bean.get("C8");
					String C9=(String)bean.get("C9");
					String C10=(String)bean.get("C10");
					String recommend_type=(String)bean.get("recommend_type");
					if("0".equals(C8)&& "0".equals(C9)&& "0".equals(C10)&& "0".equals(C7)&& "0".equals(C3)&& "0".equals(C4)&& "0".equals(C5)&& "0".equals(C6))
					{
						j++;
						continue;
					}
					RecordVo vo = new RecordVo("per_recommend_result");
					IDGenerator idg = new IDGenerator(2,conn);
					String id=IDGenerator.getKeyId("per_recommend_result", "id", 1);
					vo.setInt("id",Integer.parseInt(id));
					vo = dao.findByPrimaryKey(vo);
					if(vo!=null)
					{
						vo.setString("c7", C7);
						vo.setString("c8", C8);
						vo.setString("c3", C3);
						vo.setString("c4", C4);
						vo.setString("c5", C5);
						vo.setString("c6", C6);
						vo.setString("c9", C9);
						vo.setString("c10", C10);
						vo.setString("flag",opttype);
						vo.setString("recommend_type", recommend_type);
						vo.setString("b0110",b0110);
						vo.setString("e0122",e0122);
						vo.setString("nbase", userView.getDbname());
						vo.setString("a0101_1", (String)bean.get("a0101_1"));
						vo.setString("a0100", userView.getA0100());
						vo.setString("a0100_1", (String)bean.get("a0100_1"));
						vo.setString("recommend_unit", recommend_unit);
						list.add(vo);
					}
				}
			}
			if(j==commendList.size()&& "1".equals(opttype))
			{
				String asql=" delete from per_recommend_result where a0100='"+userView.getA0100()+"' and ( c11<>1 or c11 is null )";
				dao.delete(asql, new ArrayList());
				RecordVo vo = new RecordVo("per_recommend_result");
				IDGenerator idg = new IDGenerator(2,conn);
				String id=IDGenerator.getKeyId("per_recommend_result", "id", 1);
				vo.setInt("id",Integer.parseInt(id));
				vo = dao.findByPrimaryKey(vo);
				vo.setString("a0101_1", "无合适人员");
				vo.setString("a0100",userView.getA0100());
				vo.setString("a0100_1",userView.getA0100());
				vo.setString("nbase",userView.getDbname());
				vo.setString("recommend_unit", recommend_unit);
				vo.setString("flag", opttype);
				dao.updateValueObject(vo);
			}
			else
			{
	    		dao.updateValueObject(list);
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private String questionOne="";
	private String questionTwo="";
	private String questionFour="";
	private String questionFive="";
	private HashMap threemap = new HashMap();
	public void getBeforeResult()
	{
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer("");
			buf.append("select * from lt_leadership_result where ");
			buf.append(" a0100='"+this.userView.getA0100()+"' and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
			rs=dao.search(buf.toString());
			while(rs.next()){
				this.status=rs.getString("status");
				int question=rs.getInt("question");
				if(question==1)
				{
					questionOne=rs.getString("answer");
				}
				else if(question==2)
				{
					questionTwo = rs.getString("answer");
				}else if(question==3)
				{
					String a_nbase=rs.getString("a_nbase");
					String answer=rs.getString("answer");
					threemap.put((a_nbase+answer).toUpperCase(), "1");
				}
				else if(question==4)
				{
					questionFour=rs.getString("answer");
				}
				else if(question==5)
				{
					questionFive=rs.getString("answer");
				}
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
	}
	private String isLeader="2";
	private String status="0";
	public ArrayList getLeadershipMembers()
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{
			String recommend_flag_item=SystemConfig.getPropertyValue("recommend_flag_item");
			if(recommend_flag_item==null|| "".equals(recommend_flag_item))
				throw GeneralExceptionHandler.Handle(new Exception("人员班子标识指标未定义"));
			FieldItem fielditem = DataDictionary.getFieldItem(recommend_flag_item.trim());
			if(fielditem==null||!"A01".equalsIgnoreCase(fielditem.getFieldsetid())|| "0".equals(fielditem.getUseflag()))
		     	throw GeneralExceptionHandler.Handle(new Exception("人员班子标识指标定义错误,该指标必须已构库并且为人员主集中指标"));
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer("");
			buf.append("select a0100,");
			buf.append(recommend_flag_item+" as recommend");
			buf.append(",a0101,a0000 from usra01 where ("+recommend_flag_item+"='1' and ");
			buf.append(" b0110='"+this.userView.getUserOrgId()+"')");
			buf.append(" or a0100='"+this.userView.getA0100()+"' order by a0000");
			rs=dao.search(buf.toString());
			while(rs.next())
			{
				LazyDynaBean bean  = new LazyDynaBean();
				if(rs.getString("a0100").equalsIgnoreCase(this.userView.getA0100()))
				{
					this.isLeader=rs.getString("recommend")==null?"2":rs.getString("recommend");
					continue;
				}
				//bean.set("a0100", rs.getString("a0100"));
				//bean.set("nbase", "USR");
				String checked="0";
				if(threemap.get(("USR"+rs.getString("a0100")))!=null)
					checked="1";
				bean.set("checked", checked);
				bean.set("a0101", rs.getString("a0101"));
				bean.set("record","USR/"+rs.getString("a0100")+"/"+rs.getString("a0101")+"/"+(rs.getString("a0000")==null?"-1":rs.getString("a0000")));
				list.add(bean);
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
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	/**
	 * 
	 */
	public void createResultTable()
	{
		try
		{
			DbWizard dbWizard=new DbWizard(this.conn);
			String tableName="lt_leadership_result";
			Table table=new Table(tableName);
			if(!dbWizard.isExistTable(table.getName(),false))//如果表不存在则创建该表
			{
				Field temp = null;
				temp=new Field("nbase","人员库");
				temp.setDatatype(DataType.STRING);
				temp.setAlign("left");
				temp.setVisible(false);
				temp.setLength(10);
				table.addField(temp);
				
				temp=new Field("a_nbase","人员库");
				temp.setDatatype(DataType.STRING);
				temp.setAlign("left");
				temp.setVisible(false);
				temp.setLength(10);
				table.addField(temp); 
				
				temp= new Field("A0100","人员编号");
    			temp.setDatatype(DataType.STRING);
    			temp.setAlign("left");
    			temp.setVisible(false);
    			temp.setLength(10);
    			table.addField(temp);
    			
    			temp= new Field("a_A0101","人员姓名");
    			temp.setDatatype(DataType.STRING);
    			temp.setAlign("left");
    			temp.setVisible(false);
    			temp.setLength(12);
    			table.addField(temp);
    			
    			temp= new Field("a_A0000","序号");
    			temp.setDatatype(DataType.INT);
    			temp.setAlign("left");
    			temp.setVisible(false);
    			temp.setLength(50);
    			table.addField(temp);
    			
    			temp= new Field("b0110","单位");
    			temp.setDatatype(DataType.STRING);
    			temp.setAlign("left");
    			temp.setVisible(false);
    			temp.setLength(30);
    			table.addField(temp);
    			
    			temp= new Field("question","题号");
    			temp.setDatatype(DataType.INT);
    			temp.setAlign("left");
    			temp.setVisible(false);
    			temp.setLength(5);
    			table.addField(temp);
    			
    			temp= new Field("answer","答案");
    			temp.setDatatype(DataType.STRING);
    			temp.setAlign("left");
    			temp.setVisible(false);
    			temp.setLength(20);
    			table.addField(temp);
    			
    			temp= new Field("status","状态");//=0 保存，=1提交
    			temp.setDatatype(DataType.STRING);
    			temp.setAlign("left");
    			temp.setVisible(false);
    			temp.setLength(20);
    			table.addField(temp);
    			
    			temp= new Field("leader","班子成员标识");
    			temp.setDatatype(DataType.STRING);
    			temp.setAlign("left");
    			temp.setVisible(false);
    			temp.setLength(5);
    			table.addField(temp);
    			
    			dbWizard.createTable(table);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void saveResult(String one,String two,String three,String four,String five,int status,String leader)
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList list = new ArrayList();
			dao.delete("delete from lt_leadership_result where a0100='"+this.userView.getA0100()+"' and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'", new ArrayList());
			StringBuffer buf = new StringBuffer("");
			if(one!=null&&!"".equals(one.trim()))
			{
		    	buf.append(" insert into lt_leadership_result(nbase,a0100,b0110,question,answer,status,leader) values (");
		    	buf.append("'"+this.userView.getDbname()+"','"+this.userView.getA0100()+"','"+this.userView.getUserOrgId()+"',");
		    	buf.append("1,'"+one+"',"+status+",'"+leader+"')");
		    	list.add(buf.toString());
		    	buf.setLength(0);
			}
			if(two!=null&&!"".equals(two.trim()))
			{
		    	buf.append(" insert into lt_leadership_result(nbase,a0100,b0110,question,answer,status,leader) values (");
		    	buf.append("'"+this.userView.getDbname()+"','"+this.userView.getA0100()+"','"+this.userView.getUserOrgId()+"',");
		    	buf.append("2,'"+two+"',"+status+",'"+leader+"')");
		    	list.add(buf.toString());
		    	buf.setLength(0);
			}
			if(three!=null&&!"".equals(three.trim()))
			{
				String[] arr=three.split("`");
				for(int i=0;i<arr.length;i++)
				{
					String str=arr[i];
					if(str==null|| "".equals(str.trim()))
						continue;
					String[] a_arr=str.split("/");
		        	buf.append(" insert into lt_leadership_result(nbase,a0100,b0110,question,answer,status,leader,a_a0101,a_a0000,a_nbase) values (");
		        	buf.append("'"+this.userView.getDbname()+"','"+this.userView.getA0100()+"','"+this.userView.getUserOrgId()+"',");
		        	buf.append("3,'"+a_arr[1]+"',"+status+",'"+leader+"','"+a_arr[2]+"',"+a_arr[3]+",'"+a_arr[0]+"')");
		        	list.add(buf.toString());
		        	buf.setLength(0);
				}
			}
			if(four!=null&&!"".equals(four.trim()))
			{
		    	buf.append(" insert into lt_leadership_result(nbase,a0100,b0110,question,answer,status,leader) values (");
		    	buf.append("'"+this.userView.getDbname()+"','"+this.userView.getA0100()+"','"+this.userView.getUserOrgId()+"',");
		    	buf.append("4,'"+four+"',"+status+",'"+leader+"')");
		    	list.add(buf.toString());
		    	buf.setLength(0);
			}
			if(five!=null&&!"".equals(five.trim()))
			{
		    	buf.append(" insert into lt_leadership_result(nbase,a0100,b0110,question,answer,status,leader) values (");
		    	buf.append("'"+this.userView.getDbname()+"','"+this.userView.getA0100()+"','"+this.userView.getUserOrgId()+"',");
		    	buf.append("5,'"+five+"',"+status+",'"+leader+"')");
		    	list.add(buf.toString());
		    	buf.setLength(0);
			}
			if(list.size()>0)
	     		dao.batchUpdate(list);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private ArrayList oneList=new ArrayList();
	private ArrayList twoList=new ArrayList();
	private ArrayList threeList=new ArrayList();
	private ArrayList fourList=new ArrayList();
	private ArrayList fiveList=new ArrayList();
	public void searchResult(String b0110)
	{
		RowSet rs = null;
		try
		{
			ContentDAO dao  = new ContentDAO(this.conn);
			StringBuffer buf  = new StringBuffer();
			for(int i=1;i<6;i++)
			{
				ArrayList list = new ArrayList();
				buf.setLength(0);
				buf.append("select count(*) as total,answer,leader ");
				if(i==3)
					buf.append(",max(a_a0101) a0101,max(a_a0000) a_a0000 ");
				buf.append(" from lt_leadership_result  ");
				buf.append(" where UPPER(b0110)='"+b0110.toUpperCase()+"' and status='1'");//只统计提交的
				buf.append(" and question="+i);
				buf.append("  group by answer,leader ");
				if(i==3)
					buf.append(",a_a0000 order by "+Sql_switcher.isnull("a_a0000", "99999")+",answer,leader ");
				else
			    	buf.append("  order by answer,leader ");
				rs = dao.search(buf.toString());
				String answer="";
				int j=0;
				BigDecimal leader=new BigDecimal("0");
				BigDecimal noleader=new BigDecimal("0");
				HashMap map = new HashMap();
				String desc="";
				while(rs.next())
				{
					if(j==0)
					{
						answer=rs.getString("answer");
						if(i==3)
							desc=rs.getString("a0101");
					}
					if(answer.equalsIgnoreCase(rs.getString("answer")))
					{
						if("1".equals(rs.getString("leader")))
						{
							leader=leader.add(new BigDecimal(rs.getInt("total")+""));
						}
						else
						{
							noleader=noleader.add(new BigDecimal(rs.getInt("total")+""));
						}
						if(rs.isLast())
						{
							BigDecimal total = leader.add(noleader);
							LazyDynaBean bean = new LazyDynaBean();
							bean.set("total", total.toString());
							bean.set("tbl", "100.00%");
							bean.set("zc", leader.toString());
							bean.set("zcbl",total.compareTo(new BigDecimal("0"))==0?"0.00%":(leader.divide(total,2,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).toString()+"%"));
							bean.set("fzc",noleader.toString());
							bean.set("fzcbl", total.compareTo(new BigDecimal("0"))==0?"0.00%":(noleader.divide(total,2,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).toString()+"%"));
							if(i==3)
								bean.set("desc", desc);
							if(i==3)
								list.add(bean);
							else
						    	map.put(answer, bean);
						}
						
					}else
					{
						BigDecimal total = leader.add(noleader);
						LazyDynaBean bean = new LazyDynaBean();
						bean.set("total", total.toString());
						bean.set("tbl", "100.00%");
						bean.set("zc", leader.toString());
						bean.set("zcbl",total.compareTo(new BigDecimal("0"))==0?"0.00%":(leader.divide(total,2,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).toString()+"%"));
						bean.set("fzc",noleader.toString());
						bean.set("fzcbl", total.compareTo(new BigDecimal("0"))==0?"0.00%":(noleader.divide(total,2,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).toString()+"%"));
						if(i==3)
							bean.set("desc", desc);
						if(i==3)
							list.add(bean);
						else
					    	map.put(answer, bean);
						leader = new BigDecimal("0");
						noleader = new BigDecimal("0");
						answer=rs.getString("answer");
						if(i==3)
							desc=rs.getString("a0101");
						if("1".equals(rs.getString("leader")))
						{
							leader=leader.add(new BigDecimal(rs.getInt("total")+""));
						}
						else
						{
							noleader=noleader.add(new BigDecimal(rs.getInt("total")+""));
						}
						if(rs.isLast())
						{
							BigDecimal atotal = leader.add(noleader);
							LazyDynaBean abean = new LazyDynaBean();
							abean.set("total", atotal.toString());
							abean.set("tbl", "100.00%");
							abean.set("zc", leader.toString());
							abean.set("zcbl",atotal.compareTo(new BigDecimal("0"))==0?"0.00%":(leader.divide(atotal,2,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).toString()+"%"));
							abean.set("fzc",noleader.toString());
							abean.set("fzcbl", atotal.compareTo(new BigDecimal("0"))==0?"0.00%":(noleader.divide(atotal,2,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).toString()+"%"));
							if(i==3)
								abean.set("desc", desc);
							if(i==3)
								list.add(abean);
							else
					    		map.put(answer, abean);
						}
					}
					j++;
				}
				if(i!=3)
				{
		    		for(int x=3;x<9;x++)
			    	{
			    		if(i==4&&x>5)
				    		break;
				    	LazyDynaBean bean  = null;
				      	if(map.get(x+"")!=null)
				    	{
				    		bean = (LazyDynaBean)map.get(x+"");
			    		}
				    	else
			    		{
				    		bean = new LazyDynaBean();
					    	bean.set("total", "0");
					    	bean.set("tbl", "0.00%");
					    	bean.set("zc", "0");
					    	bean.set("zcbl","0.00%");
					    	bean.set("fzc","0");
					    	bean.set("fzcbl", "0.00%");
				    	}
				    	if(i==4)
				    	{
				    		if(x==3)
					    	{
					    		bean.set("desc", "市场经营和客服");
					    	}else if(x==4)
					    	{
					    		bean.set("desc", "建设运维IT支撑");
					    	}
					    	else if(x==5)
					    	{
					    		bean.set("desc", "综合财务");
				    		}
			    		}
			    		else
				    	{
				    		bean.set("desc", "["+x+"人]");
				    	}
				    	list.add(bean);
		    		}
				}
				if(i==1)
					this.setOneList(list);
				else if(i==2)
					this.setTwoList(list);
				else if(i==3)
					this.setThreeList(list);
				else if(i==4)
					this.setFourList(list);
				else if(i==5)
					this.setFiveList(list);
				
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
	}
	public String getNewLeaderExcel(String b0110)
	{
		String fileName=AdminCode.getCodeName("UN",b0110)+"新选拔任用领导人员民主评议表.xls";
		FileOutputStream fileOut = null;
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = null;
		try
		{
			ArrayList list = this.getNewLeaderResult(b0110);
			HSSFRow row = null;
			HSSFCell cell = null;
			sheet=workbook.createSheet();
			HSSFFont font = workbook.createFont();
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			HSSFCellStyle abStyle=workbook.createCellStyle();
			HSSFFont afont = workbook.createFont();
			afont.setColor(HSSFFont.COLOR_NORMAL);
			afont.setBold(false);
			abStyle.setFont(afont);
			abStyle.setAlignment(HorizontalAlignment.CENTER);
			abStyle.setBorderBottom(BorderStyle.THIN);
			abStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			abStyle.setBorderLeft(BorderStyle.THIN);
			abStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			abStyle.setBorderRight(BorderStyle.THIN);
			abStyle.setRightBorderColor(HSSFColor.BLACK.index);
			abStyle.setBorderTop(BorderStyle.THIN);
			abStyle.setTopBorderColor(HSSFColor.BLACK.index);
			abStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			short n=0;
			row=sheet.getRow(n);
			if(row==null)
				row=sheet.createRow(n);
			
			HSSFCellStyle bcellStyle = workbook.createCellStyle();
			bcellStyle.setFont(font);
			bcellStyle.setAlignment(HorizontalAlignment.LEFT);
			bcellStyle.setBorderBottom(BorderStyle.THIN);
			bcellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			bcellStyle.setBorderLeft(BorderStyle.THIN);
			bcellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			bcellStyle.setBorderRight(BorderStyle.THIN);
			bcellStyle.setRightBorderColor(HSSFColor.BLACK.index);
			bcellStyle.setBorderTop(BorderStyle.THIN);
			bcellStyle.setTopBorderColor(HSSFColor.BLACK.index);
			bcellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			cell=row.createCell(0);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("单位："+AdminCode.getCodeName("UN",b0110));
			cell.setCellStyle(cellStyle);
			ExportExcelUtil.mergeCell(sheet, n, (short)0, n, (short)(8));
			n++;
			for(int i=1;i<=9;i++)
			{
				cell=row.createCell(i);
				cell.setCellStyle(cellStyle);
			}
			row=sheet.getRow(n);
			if(row==null)
				row=sheet.createRow(n);
			cell=row.createCell(0);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("被测评人员情况");
			cell.setCellStyle(cellStyle);
			ExportExcelUtil.mergeCell(sheet, n, (short)0, n, (short)(1));
			
			cell=row.createCell(2);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("意见选项");
			cell.setCellStyle(cellStyle);
			ExportExcelUtil.mergeCell(sheet, n, (short)2, n+1, (short)(2));
			
			cell=row.createCell(3);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("意见统计");
			cell.setCellStyle(cellStyle);
			ExportExcelUtil.mergeCell(sheet, n, (short)3, n, (short)(8));
			n++;
			row=sheet.getRow(n);
			if(row==null)
				row=sheet.createRow(n);
			cell=row.createCell(0);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("姓名");
			cell.setCellStyle(cellStyle);
			cell=row.createCell(1);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("职务");
			cell.setCellStyle(cellStyle);
			/*cell=row.createCell(2);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("被测评人员情况");
			cell.setCellStyle(cellStyle);*/
			cell=row.createCell(3);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("总票数");
			cell.setCellStyle(cellStyle);
			cell=row.createCell(4);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("比例");
			cell.setCellStyle(cellStyle);
			cell=row.createCell(5);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("班子成员评价");
			cell.setCellStyle(cellStyle);
			cell=row.createCell(6);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("比例");
			cell.setCellStyle(cellStyle);
			cell=row.createCell(7);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("中层评价");
			cell.setCellStyle(cellStyle);
			cell=row.createCell(8);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("比例");
			cell.setCellStyle(cellStyle);
			n++;
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)list.get(i);
				String a0101=(String)bean.get("a0101");
				String e01a1=(String)bean.get("e01a1");
				ArrayList alist = (ArrayList)bean.get("subList");
				for(int j=0;j<alist.size();j++)
				{
					row=sheet.getRow(n);
					if(row==null)
						row=sheet.createRow(n);
					LazyDynaBean abean = (LazyDynaBean)alist.get(j);
					if(j==0)
					{
						cell=row.createCell(0);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellValue(a0101);
						cell.setCellStyle(abStyle);
						ExportExcelUtil.mergeCell(sheet, n, (short)0, n+3, (short)(0));
						
						cell=row.createCell(1);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellValue(e01a1);
						cell.setCellStyle(abStyle);
						ExportExcelUtil.mergeCell(sheet, n, (short)1, n+3, (short)(1));
					}
					else
					{
						cell=row.createCell(0);
						cell.setCellStyle(abStyle);
						
						cell=row.createCell(1);
						cell.setCellStyle(abStyle);
					}
					cell=row.createCell(2);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue((String)abean.get("desc"));
					cell.setCellStyle(abStyle);
					
					cell=row.createCell(3);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue((String)abean.get("total"));
					cell.setCellStyle(abStyle);
					
					cell=row.createCell(4);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue((String)abean.get("totalbl"));
					cell.setCellStyle(abStyle);
					
					cell=row.createCell(5);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue((String)abean.get("bzcy"));
					cell.setCellStyle(abStyle);
					
					cell=row.createCell(6);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue((String)abean.get("bzcybl"));
					cell.setCellStyle(abStyle);
					
					cell=row.createCell(7);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue((String)abean.get("zccy"));
					cell.setCellStyle(abStyle);
					
					cell=row.createCell(8);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue((String)abean.get("zccybl"));
					cell.setCellStyle(abStyle);
					n++;
				}
			}
			for(int i = 0; i <=8; i++)
			{
				sheet.setColumnWidth(Short.parseShort(String.valueOf(i)),(short)5500);
			}
			int cc=list.size()*4+2;
			for(int i = 0; i <=cc; i++)
			{
			    row = sheet.getRow(i);
				if(row==null)
					 row = sheet.createRow(i);
			    row.setHeight((short) 400);
			}
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+ System.getProperty("file.separator") + fileName);
			workbook.write(fileOut);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeIoResource(fileOut);
			PubFunc.closeResource(workbook);
		}
		return fileName;
	}
	public String getExcelFile(String b0110)
	{
		String fileName=AdminCode.getCodeName("UN",b0110)+"班子职数配备意见表.xls";
		FileOutputStream fileOut = null;
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = null;
		try
		{
			HSSFRow row = null;
			HSSFCell cell = null;
			sheet=workbook.createSheet();
			HSSFFont font = workbook.createFont();
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			HSSFCellStyle abStyle=workbook.createCellStyle();
			HSSFFont afont = workbook.createFont();
			afont.setColor(HSSFFont.COLOR_NORMAL);
			afont.setBold(false);
			abStyle.setFont(afont);
			abStyle.setAlignment(HorizontalAlignment.CENTER);
			abStyle.setBorderBottom(BorderStyle.THIN);
			abStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			abStyle.setBorderLeft(BorderStyle.THIN);
			abStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			abStyle.setBorderRight(BorderStyle.THIN);
			abStyle.setRightBorderColor(HSSFColor.BLACK.index);
			abStyle.setBorderTop(BorderStyle.THIN);
			abStyle.setTopBorderColor(HSSFColor.BLACK.index);
			abStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			short n=0;
			row=sheet.getRow(n);
			if(row==null)
				row=sheet.createRow(n);
			HSSFCellStyle bcellStyle = workbook.createCellStyle();
			bcellStyle.setFont(font);
			bcellStyle.setAlignment(HorizontalAlignment.LEFT);
			bcellStyle.setBorderBottom(BorderStyle.THIN);
			bcellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			bcellStyle.setBorderLeft(BorderStyle.THIN);
			bcellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			bcellStyle.setBorderRight(BorderStyle.THIN);
			bcellStyle.setRightBorderColor(HSSFColor.BLACK.index);
			bcellStyle.setBorderTop(BorderStyle.THIN);
			bcellStyle.setTopBorderColor(HSSFColor.BLACK.index);
			bcellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			cell=row.createCell(0);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("单位："+AdminCode.getCodeName("UN",b0110));
			cell.setCellStyle(bcellStyle);
			ExportExcelUtil.mergeCell(sheet, n, (short)0, n, (short)(7));
			for(int i=1;i<=7;i++)
			{
				cell=row.createCell(i);
				cell.setCellStyle(cellStyle);
			}
			n++;
			row=sheet.getRow(n);
			if(row==null)
				row=sheet.createRow(n);
			cell=row.createCell(0);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("项目");
			cell.setCellStyle(cellStyle);
			ExportExcelUtil.mergeCell(sheet, n, (short)0, n+1, (short)(0));
			cell=row.createCell(1);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("选项");
			cell.setCellStyle(cellStyle);
			ExportExcelUtil.mergeCell(sheet, n, (short)1, n+1, (short)(1));
			
			cell=row.createCell(2);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("意见统计");
			cell.setCellStyle(cellStyle);
			ExportExcelUtil.mergeCell(sheet, n, (short)2, n, (short)(7));
			for(int i=3;i<=7;i++)
			{
				cell=row.createCell(i);
				cell.setCellStyle(cellStyle);
			}
			n++;
			row=sheet.getRow(n);
			if(row==null)
				row=sheet.createRow(n);
			for(int i=0;i<=1;i++)
			{
				cell=row.createCell(i);
				cell.setCellStyle(cellStyle);
			}
			cell=row.createCell(2);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("总票数");
			cell.setCellStyle(cellStyle);
			cell=row.createCell(3);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("比例");
			cell.setCellStyle(cellStyle);
			cell=row.createCell(4);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("班子成员意见");
			cell.setCellStyle(cellStyle);
			cell=row.createCell(5);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("比例");
			cell.setCellStyle(cellStyle);
			cell=row.createCell(6);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("中层意见");
			cell.setCellStyle(cellStyle);
			cell=row.createCell(7);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("比例");
			cell.setCellStyle(cellStyle);
			n++;
			
			for(int i=0;i<this.oneList.size();i++)
			{
				row=sheet.getRow(n);
				if(row==null)
					row=sheet.createRow(n);
				LazyDynaBean bean = (LazyDynaBean)this.oneList.get(i);
				if(i==0)
				{
					cell=row.createCell(0);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue("职数建议");
					cell.setCellStyle(abStyle);
					ExportExcelUtil.mergeCell(sheet, n, (short)0, (n+5), (short)(0));
				}
				else
				{
					cell=row.createCell(0);
					cell.setCellStyle(abStyle);
				}
				cell=row.createCell(1);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("desc"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(2);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("total"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(3);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("tbl"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(4);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("zc"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(5);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("zcbl"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(6);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("fzc"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(7);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("fzcbl"));
				cell.setCellStyle(abStyle);
				n++;
			}
			for(int i=0;i<this.twoList.size();i++)
			{
				row=sheet.getRow(n);
				if(row==null)
					row=sheet.createRow(n);
				LazyDynaBean bean = (LazyDynaBean)this.twoList.get(i);
				if(i==0)
				{
					cell=row.createCell(0);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue("现班子选任人数");
					cell.setCellStyle(abStyle);
					ExportExcelUtil.mergeCell(sheet, n, (short)0, (n+5), (short)(0));
				}
				else
				{
					cell=row.createCell(0);
					cell.setCellStyle(abStyle);
				}
				cell=row.createCell(1);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("desc"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(2);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("total"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(3);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("tbl"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(4);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("zc"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(5);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("zcbl"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(6);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("fzc"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(7);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("fzcbl"));
				cell.setCellStyle(abStyle);
				n++;
			}
			
			for(int i=0;i<this.fiveList.size();i++)
			{
				row=sheet.getRow(n);
				if(row==null)
					row=sheet.createRow(n);
				LazyDynaBean bean = (LazyDynaBean)this.fiveList.get(i);
				if(i==0)
				{
					cell=row.createCell(0);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue("补充人数");
					cell.setCellStyle(abStyle);
					ExportExcelUtil.mergeCell(sheet, n, (short)0, (n+5), (short)(0));
				}
				else
				{
					cell=row.createCell(0);
					cell.setCellStyle(abStyle);
				}
				cell=row.createCell(1);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("desc"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(2);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("total"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(3);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("tbl"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(4);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("zc"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(5);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("zcbl"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(6);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("fzc"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(7);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("fzcbl"));
				cell.setCellStyle(abStyle);
				n++;
			}
			
			for(int i=0;i<this.threeList.size();i++)
			{
				row=sheet.getRow(n);
				if(row==null)
					row=sheet.createRow(n);
				LazyDynaBean bean = (LazyDynaBean)this.threeList.get(i);
				if(i==0)
				{
					cell=row.createCell(0);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue("现班子入选人员");
					cell.setCellStyle(abStyle);
					ExportExcelUtil.mergeCell(sheet, n, (short)0, (n+threeList.size()-1), (short)(0));
				}
				else
				{
					cell=row.createCell(0);
					cell.setCellStyle(abStyle);
				}
				cell=row.createCell(1);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("desc"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(2);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("total"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(3);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("tbl"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(4);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("zc"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(5);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("zcbl"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(6);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("fzc"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(7);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("fzcbl"));
				cell.setCellStyle(abStyle);
				n++;
			}
			for(int i=0;i<this.fourList.size();i++)
			{
				row=sheet.getRow(n);
				if(row==null)
					row=sheet.createRow(n);
				LazyDynaBean bean = (LazyDynaBean)this.fourList.get(i);
				if(i==0)
				{
					cell=row.createCell(0);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue("补充专业");
					cell.setCellStyle(abStyle);
					ExportExcelUtil.mergeCell(sheet, n, (short)0, (n+2), (short)(0));
				}
				else
				{
					cell=row.createCell(0);
					cell.setCellStyle(abStyle);
				}
				cell=row.createCell(1);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("desc"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(2);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("total"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(3);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("tbl"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(4);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("zc"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(5);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("zcbl"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(6);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("fzc"));
				cell.setCellStyle(abStyle);
				cell=row.createCell(7);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue((String)bean.get("fzcbl"));
				cell.setCellStyle(abStyle);
				n++;
			}
			
			
			int column=21+this.threeList.size();
			for(int i = 0; i <=column; i++)
			{
				sheet.setColumnWidth(Short.parseShort(String.valueOf(i)),(short)5500);
			}
			for(int i = 0; i <=n; i++)
			{
			    row = sheet.getRow(i);
				if(row==null)
					 row = sheet.createRow(i);
			    row.setHeight((short) 400);
			}
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+ System.getProperty("file.separator") + fileName);
			workbook.write(fileOut);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
		return fileName;
	}
	public void createNewLeaderResultTable()
	{
		try
		{
			DbWizard dbWizard=new DbWizard(this.conn);
			String tableName="newleader_result";
			Table table=new Table(tableName);
			if(!dbWizard.isExistTable(table.getName(),false))//如果表不存在则创建该表
			{
				Field temp = null;
				temp=new Field("nbase","人员库");
				temp.setDatatype(DataType.STRING);
				temp.setAlign("left");
				temp.setVisible(false);
				temp.setLength(10);
				table.addField(temp); 
				
				temp= new Field("A0100","人员编号");
    			temp.setDatatype(DataType.STRING);
    			temp.setAlign("left");
    			temp.setVisible(false);
    			temp.setLength(10);
    			table.addField(temp);
    			
    			temp= new Field("A0100_1","人员编号");
    			temp.setDatatype(DataType.STRING);
    			temp.setAlign("left");
    			temp.setVisible(false);
    			temp.setLength(10);
    			table.addField(temp);
    			
    			temp= new Field("b0110","单位");
    			temp.setDatatype(DataType.STRING);
    			temp.setAlign("left");
    			temp.setVisible(false);
    			temp.setLength(30);
    			table.addField(temp);
    			
    			temp= new Field("C1","C1");
    			temp.setDatatype(DataType.STRING);
    			temp.setAlign("left");
    			temp.setVisible(false);
    			temp.setLength(5);
    			table.addField(temp);
    			
    			temp= new Field("status","状态");//=0 保存，=1提交
    			temp.setDatatype(DataType.INT);
    			temp.setAlign("left");
    			temp.setVisible(false);
    			temp.setLength(2);
    			table.addField(temp);
    			dbWizard.createTable(table);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public ArrayList getNewLeaderResult(String code)
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String newLeaderField = SystemConfig.getPropertyValue("recommend_flag_item");//新领导标识
			String bz = SystemConfig.getPropertyValue("recommend_flag_item2");//班子成员标识
			StringBuffer buf = new StringBuffer();
			buf.append("select a0101,a0100,e01a1 from usra01 where ");
			buf.append(newLeaderField+"='1'");
			buf.append(" and b0110='"+code+"' order by a0000");
			HashMap map = this.getResult(code, bz, dao);
			ArrayList alist = new ArrayList();
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("desc", "满意");
			bean.set("total", "0");
			bean.set("totalbl", "0.00%");
			bean.set("bzcy", "0");
			bean.set("bzcybl","0.00%");
			bean.set("zccy", "0");
			bean.set("zccybl", "0.00%");
			alist.add(bean);
			bean = new LazyDynaBean();
			bean.set("desc", "基本满意");
			bean.set("total", "0");
			bean.set("totalbl", "0.00%");
			bean.set("bzcy", "0");
			bean.set("bzcybl","0.00%");
			bean.set("zccy", "0");
			bean.set("zccybl", "0.00%");
			alist.add(bean);
			bean = new LazyDynaBean();
			bean.set("desc", "不满意");
			bean.set("total", "0");
			bean.set("totalbl","0.00%");
			bean.set("bzcy", "0");
			bean.set("bzcybl","0.00%");
			bean.set("zccy", "0");
			bean.set("zccybl", "0.00%");
			alist.add(bean);
			bean = new LazyDynaBean();
			bean.set("desc", "不了解");
			bean.set("total","0");
			bean.set("totalbl", "0.00%");
			bean.set("bzcy", "0");
			bean.set("bzcybl","0.00%");
			bean.set("zccy", "0");
			bean.set("zccybl", "0.00%");
			alist.add(bean);
			rs = dao.search(buf.toString());
			while(rs.next())
			{
				String a0100=rs.getString("a0100");
				ArrayList subList=null;
				if(map.get(a0100)==null)
				{
					subList =alist;
				}
				else
				{
					subList = (ArrayList)map.get(a0100);
				}
				LazyDynaBean abean  = new LazyDynaBean();
				abean.set("a0101",rs.getString("a0101"));
				abean.set("e01a1", AdminCode.getCodeName("@K", rs.getString("e01a1")));
				abean.set("subList", subList);
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
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
		return list;
	}
	public HashMap getResult(String code,String bz,ContentDAO dao)
	{
		HashMap map = new HashMap();
		RowSet rs = null;
		try
		{
			rs = dao.search("select a0100,a0100_1,c1 from newleader_result where status='1' order by a0100");
			ArrayList list = null;
			int i=0;
			String a0100="";
			BigDecimal one= new BigDecimal("0");
			BigDecimal bzone= new BigDecimal("0");
			BigDecimal two= new BigDecimal("0");
			BigDecimal bztwo= new BigDecimal("0");
			BigDecimal three= new BigDecimal("0");
			BigDecimal bzthree= new BigDecimal("0");
			BigDecimal four= new BigDecimal("0");
			BigDecimal bzfour= new BigDecimal("0");
			HashMap bzmap = this.getBZCY(bz, code, dao);
			while(rs.next())
			{
				if(i==0)
				{
					a0100=rs.getString("a0100");
				}
				if(a0100.equalsIgnoreCase(rs.getString("a0100")))
				{
					String a0100_1=rs.getString("a0100_1");
					String c1=rs.getString("c1");
					if(bzmap.get(a0100_1)!=null)//班子成员
					{
						if("1".equals(c1))//满意
						{
							bzone=bzone.add(new BigDecimal("1"));
						}else if("2".equals(c1))//基本满意
						{
							bztwo= bztwo.add(new BigDecimal("1"));
						}else if("3".equals(c1))//不满意
						{
							bzthree=bzthree.add(new BigDecimal("1"));
						}else if("4".equals(c1))//不了解
						{
							bzfour=bzfour.add(new BigDecimal("1"));
						}
					}
					else
					{
						if("1".equals(c1))//满意
						{
							one=one.add(new BigDecimal("1"));
						}else if("2".equals(c1))//基本满意
						{
							two=two.add(new BigDecimal("1"));
						}else if("3".equals(c1))//不满意
						{
							three=three.add(new BigDecimal("1"));
						}else if("4".equals(c1))//不了解
						{
							four=four.add(new BigDecimal("1"));
						}
					}
				}
				else
				{
					list = new ArrayList();
					LazyDynaBean bean = new LazyDynaBean();
					BigDecimal total = bzone.add(one);
					BigDecimal total2 = bztwo.add(two);
					BigDecimal total3 = bzthree.add(three);
					BigDecimal total4 = bzfour.add(four);
					BigDecimal T1=total.add(total2).add(total3).add(total4);
					BigDecimal T2=bzone.add(bztwo).add(bzthree).add(bzfour);
					BigDecimal T3 = one.add(two).add(three).add(four);
					bean.set("desc", "满意");
					bean.set("total", total.toString());
					bean.set("totalbl", T1.compareTo(new BigDecimal("0"))==0?"0.00%":(total.divide(T1, 4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					bean.set("bzcy", bzone.toString());
					bean.set("bzcybl",T2.compareTo(new BigDecimal("0"))==0?"0.00%":(bzone.divide(T2,4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					bean.set("zccy", one.toString());
					bean.set("zccybl", T3.compareTo(new BigDecimal("0"))==0?"0.00%":(one.divide(T3,4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					list.add(bean);
					bean = new LazyDynaBean();
					bean.set("desc", "基本满意");
					bean.set("total", total2.toString());
					bean.set("totalbl",  T1.compareTo(new BigDecimal("0"))==0?"0.00%":(total2.divide(T1, 4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					bean.set("bzcy", bztwo.toString());
					bean.set("bzcybl",T2.compareTo(new BigDecimal("0"))==0?"0.00%":(bztwo.divide(T2,4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					bean.set("zccy", two.toString());
					bean.set("zccybl", T3.compareTo(new BigDecimal("0"))==0?"0.00%":(two.divide(T3,4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					list.add(bean);
					bean = new LazyDynaBean();
					bean.set("desc", "不满意");
					bean.set("total", total3.toString());
					bean.set("totalbl", T1.compareTo(new BigDecimal("0"))==0?"0.00%":(total3.divide(T1, 4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					bean.set("bzcy", bzthree.toString());
					bean.set("bzcybl",T2.compareTo(new BigDecimal("0"))==0?"0.00%":(bzthree.divide(T2,4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					bean.set("zccy", three.toString());
					bean.set("zccybl", T3.compareTo(new BigDecimal("0"))==0?"0.00%":(three.divide(T3,4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					list.add(bean);
					bean = new LazyDynaBean();
					bean.set("desc", "不了解");
					bean.set("total", total4.toString());
					bean.set("totalbl",  T1.compareTo(new BigDecimal("0"))==0?"0.00%":(total4.divide(T1, 4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					bean.set("bzcy", bzfour.toString());
					bean.set("bzcybl",T2.compareTo(new BigDecimal("0"))==0?"0.00%":(bzfour.divide(T2,4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					bean.set("zccy", four.toString());
					bean.set("zccybl", T3.compareTo(new BigDecimal("0"))==0?"0.00%":(four.divide(T3,4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					list.add(bean);
					map.put(a0100, list);
					one= new BigDecimal("0");
					bzone= new BigDecimal("0");
					two= new BigDecimal("0");
					bztwo= new BigDecimal("0");
					three= new BigDecimal("0");
					bzthree= new BigDecimal("0");
					four= new BigDecimal("0");
					bzfour= new BigDecimal("0");
					a0100=rs.getString("a0100");
					String a0100_1=rs.getString("a0100_1");
					String c1=rs.getString("c1");
					if(bzmap.get(a0100_1)!=null)//班子成员
					{
						if("1".equals(c1))//满意
						{
							bzone=bzone.add(new BigDecimal("1"));
						}else if("2".equals(c1))//基本满意
						{
							bztwo= bztwo.add(new BigDecimal("1"));
						}else if("3".equals(c1))//不满意
						{
							bzthree=bzthree.add(new BigDecimal("1"));
						}else if("4".equals(c1))//不了解
						{
							bzfour=bzfour.add(new BigDecimal("1"));
						}
					}
					else
					{
						if("1".equals(c1))//满意
						{
							one=one.add(new BigDecimal("1"));
						}else if("2".equals(c1))//基本满意
						{
							two=two.add(new BigDecimal("1"));
						}else if("3".equals(c1))//不满意
						{
							three=three.add(new BigDecimal("1"));
						}else if("4".equals(c1))//不了解
						{
							four=four.add(new BigDecimal("1"));
						}
					}
				}
				if(rs.isLast())
				{
					list = new ArrayList();
					LazyDynaBean bean = new LazyDynaBean();
					BigDecimal total = bzone.add(one);
					BigDecimal total2 = bztwo.add(two);
					BigDecimal total3 = bzthree.add(three);
					BigDecimal total4 = bzfour.add(four);
					BigDecimal T1=total.add(total2).add(total3).add(total4);
					BigDecimal T2=bzone.add(bztwo).add(bzthree).add(bzfour);
					BigDecimal T3 = one.add(two).add(three).add(four);
					bean.set("desc", "满意");
					bean.set("total", total.toString());
					bean.set("totalbl", T1.compareTo(new BigDecimal("0"))==0?"0.00%":(total.divide(T1, 4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					bean.set("bzcy", bzone.toString());
					bean.set("bzcybl",T2.compareTo(new BigDecimal("0"))==0?"0.00%":(bzone.divide(T2,4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					bean.set("zccy", one.toString());
					bean.set("zccybl", T3.compareTo(new BigDecimal("0"))==0?"0.00%":(one.divide(T3,4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					list.add(bean);
					bean = new LazyDynaBean();
					bean.set("desc", "基本满意");
					bean.set("total", total2.toString());
					bean.set("totalbl", T1.compareTo(new BigDecimal("0"))==0?"0.00%":(total2.divide(T1, 4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					bean.set("bzcy", bztwo.toString());
					bean.set("bzcybl",T2.compareTo(new BigDecimal("0"))==0?"0.00%":(bztwo.divide(T2,4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					bean.set("zccy", two.toString());
					bean.set("zccybl", T3.compareTo(new BigDecimal("0"))==0?"0.00%":(two.divide(T3,4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					list.add(bean);
					bean = new LazyDynaBean();
					bean.set("desc", "不满意");
					bean.set("total", total3.toString());
					bean.set("totalbl", T1.compareTo(new BigDecimal("0"))==0?"0.00%":(total3.divide(T1, 4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					bean.set("bzcy", bzthree.toString());
					bean.set("bzcybl",T2.compareTo(new BigDecimal("0"))==0?"0.00%":(bzthree.divide(T2,4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					bean.set("zccy", three.toString());
					bean.set("zccybl", T3.compareTo(new BigDecimal("0"))==0?"0.00%":(three.divide(T3,4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					list.add(bean);
					bean = new LazyDynaBean();
					bean.set("desc", "不了解");
					bean.set("total", total4.toString());
					bean.set("totalbl", T1.compareTo(new BigDecimal("0"))==0?"0.00%":(total4.divide(T1, 4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					bean.set("bzcy", bzfour.toString());
					bean.set("bzcybl",T2.compareTo(new BigDecimal("0"))==0?"0.00%":(bzfour.divide(T2,4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					bean.set("zccy", four.toString());
					bean.set("zccybl", T3.compareTo(new BigDecimal("0"))==0?"0.00%":(four.divide(T3,4,RoundingMode.HALF_DOWN).multiply(new BigDecimal("100")).divide(new BigDecimal("1"), 2, RoundingMode.HALF_DOWN).toString()+"%"));
					list.add(bean);
					map.put(a0100, list);
				}
				i++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
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
		return map;
	}
	/**
	 * 求本单位所以班子成员
	 * @param bz
	 * @param code
	 * @param dao
	 * @return
	 */
	public HashMap getBZCY(String bz,String code,ContentDAO dao)
	{
		HashMap map = new HashMap();
		RowSet rs = null;
		try
		{
			rs = dao.search("select a0100 from usra01 where b0110='"+code+"' and "+bz+"='1'");
			while(rs.next())
			{
				map.put(rs.getString("a0100"), "1");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
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
		return map;
	}
	private String newLeaderStatus="0";
	public ArrayList getLeaderList(ArrayList fieldlist,String newLeaderField)
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{
			StringBuffer buf  = new StringBuffer("select a0100,a0101");
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem item = (FieldItem)fieldlist.get(i);
				buf.append(","+item.getItemid());
			}
			buf.append(" from usra01 where ");
			buf.append(" b0110='"+this.userView.getUserOrgId()+"'");
			buf.append(" and "+newLeaderField+"='1' order by a0000");
			HashMap selectedMap = this.getSelected();
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(buf.toString());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				String a0100=rs.getString("a0100");
				bean.set("a0100", a0100);
				bean.set("a0101",rs.getString("a0101"));
				for(int i=0;i<fieldlist.size();i++)
				{
					FieldItem item = (FieldItem)fieldlist.get(i);
					if("A".equalsIgnoreCase(item.getItemtype()))
					{
						if(rs.getString(item.getItemid())==null)
							bean.set(item.getItemid(), "");
						else
						{
					    	if(item.isCode())
					    		bean.set(item.getItemid().toLowerCase(), AdminCode.getCodeName(item.getCodesetid(), rs.getString(item.getItemid())));
					    	else
						    	bean.set(item.getItemid().toLowerCase(),rs.getString(item.getItemid()));
						}
					} 
					else if("D".equalsIgnoreCase(item.getItemtype()))
					{
						if(rs.getDate(item.getItemid())==null)
							bean.set(item.getItemid(), "");
						else
						{
					    	if(rs.getDate(item.getItemid())!=null)
					        	bean.set(item.getItemid().toLowerCase(), format.format(rs.getDate(item.getItemid())));
					    	else
						    	bean.set(item.getItemid().toLowerCase(), "");
						}
					}
					else if("N".equalsIgnoreCase(item.getItemtype()))
					{
						int scale = item.getDecimalwidth();
						if(rs.getString(item.getItemid())!=null)
							bean.set(item.getItemid().toLowerCase(), PubFunc.round(rs.getString(item.getItemid()), scale));
						else
							bean.set(item.getItemid().toLowerCase(), "");
					}
					else
					{
						if(rs.getString(item.getItemid())!=null)
							bean.set(item.getItemid().toLowerCase(), rs.getString(item.getItemid()));
						else
							bean.set(item.getItemid().toLowerCase(), "");
					}
						
						
				}
				if(selectedMap.get(this.userView.getA0100()+a0100)!=null)
				{
					String status = (String)selectedMap.get(this.userView.getA0100()+a0100);
					bean.set("status", status);
				}
				else
				{
					bean.set("status", "0");
				}
				list.add(bean);
			}
				
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
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
		return list;
	}
	public boolean isHaveLeader()
	{
		boolean flag= false;
		RowSet rs = null;
		try
		{
			String newLeaderField = SystemConfig.getPropertyValue("recommend_flag_item");
			if(newLeaderField==null|| "".equals(newLeaderField))
				throw GeneralExceptionHandler.Handle(new Exception("系统未设置新选拔任用领导标识参数！"));
			StringBuffer buf  = new StringBuffer("select a0100 ");
			buf.append(" from usra01 where ");
			buf.append(" b0110='"+this.userView.getUserOrgId()+"'");
			buf.append(" and "+newLeaderField+"='1' ");
			ContentDAO dao  = new ContentDAO(this.conn);
			rs = dao.search(buf.toString());
			while(rs.next())
			{
				flag=true;
				break;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
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
		return flag;
	}
	public HashMap getSelected()
	{
		HashMap map = new HashMap();
		RowSet rs = null;
		try
		{
			ContentDAO dao  = new ContentDAO(this.conn);
			rs = dao.search("select a0100,a0100_1,C1,status from newleader_result where a0100_1='"+this.userView.getA0100()+"'");
			while(rs.next())
			{
				String a0100_1 = rs.getString("a0100_1");
				String a0100=rs.getString("a0100");
				String c1=rs.getString("c1");
				int status = rs.getInt("status");
				if(status==1)//已提交
					this.newLeaderStatus="1";
				map.put(a0100_1+a0100, c1);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
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
		return map;
	}
	public String  getNewStatus()
	{
		String status="0";
		RowSet rs = null;
		try
		{
			ContentDAO dao  = new ContentDAO(this.conn);
			rs = dao.search("select a0100,a0100_1,C1,status from newleader_result where a0100_1='"+this.userView.getA0100()+"'");
			while(rs.next())
			{
				int astatus = rs.getInt("status");
				if(astatus==1)//已提交
				{
					status="1";
					break;
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
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
		return status;
	}
	public boolean saveNewLeader(String record,String status)
	{
		boolean flag = true;
		try
		{
			if(record!=null&&record.trim().length()>0)
			{
				ContentDAO dao  = new ContentDAO(this.conn);
				ArrayList list = new ArrayList();
				dao.delete("delete from newleader_result where a0100_1='"+this.userView.getA0100()+"' and b0110='"+this.userView.getUserOrgId()+"'", new ArrayList());
				String [] arr = record.split("/");
				StringBuffer  buf =new StringBuffer();
				for(int i=0;i<arr.length;i++)
				{
					if(arr[i]==null|| "".equals(arr[i]))
						continue;
					buf.setLength(0);
					buf.append("insert into newleader_result(nbase,a0100,a0100_1,b0110,c1,status)");
					buf.append(" values ");
					buf.append("('USR','"+arr[i].split("`")[0]+"','"+this.userView.getA0100()+"','"+this.userView.getUserOrgId()+"'");
					buf.append(",'"+arr[i].split("`")[1]+"',"+Integer.parseInt(status)+")");
					list.add(buf.toString());
				}
				if(list.size()>0)
					dao.batchUpdate(list);
			}
		}
		catch(Exception e)
		{
			 flag=false;
			e.printStackTrace();
		}
		return flag;
	}
	public String getIsLeader() {
		return isLeader;
	}
	public void setIsLeader(String isLeader) {
		this.isLeader = isLeader;
	}
	public String getQuestionOne() {
		return questionOne;
	}
	public void setQuestionOne(String questionOne) {
		this.questionOne = questionOne;
	}
	public String getQuestionTwo() {
		return questionTwo;
	}
	public void setQuestionTwo(String questionTwo) {
		this.questionTwo = questionTwo;
	}
	public String getQuestionFour() {
		return questionFour;
	}
	public void setQuestionFour(String questionFour) {
		this.questionFour = questionFour;
	}
	public String getQuestionFive() {
		return questionFive;
	}
	public void setQuestionFive(String questionFive) {
		this.questionFive = questionFive;
	}
	public ArrayList getOneList() {
		return oneList;
	}
	public void setOneList(ArrayList oneList) {
		this.oneList = oneList;
	}
	public ArrayList getTwoList() {
		return twoList;
	}
	public void setTwoList(ArrayList twoList) {
		this.twoList = twoList;
	}
	public ArrayList getThreeList() {
		return threeList;
	}
	public void setThreeList(ArrayList threeList) {
		this.threeList = threeList;
	}
	public ArrayList getFourList() {
		return fourList;
	}
	public void setFourList(ArrayList fourList) {
		this.fourList = fourList;
	}
	public ArrayList getFiveList() {
		return fiveList;
	}
	public void setFiveList(ArrayList fiveList) {
		this.fiveList = fiveList;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNewLeaderStatus() {
		return newLeaderStatus;
	}
	public void setNewLeaderStatus(String newLeaderStatus) {
		this.newLeaderStatus = newLeaderStatus;
	}

}
