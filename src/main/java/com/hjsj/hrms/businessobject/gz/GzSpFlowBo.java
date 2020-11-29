	package com.hjsj.hrms.businessobject.gz;

	import com.hjsj.hrms.businessobject.performance.singleGrade.TableOperateBo;
	import com.hjsj.hrms.businessobject.train.CtrlParamXmlBo;
	import com.hjsj.hrms.utils.PubFunc;
	import com.hrms.frame.codec.SafeCode;
	import com.hrms.frame.dao.ContentDAO;
	import com.hrms.frame.dao.RecordVo;
	import com.hrms.frame.dao.db.DBMetaModel;
	import com.hrms.frame.dbstruct.DbWizard;
	import com.hrms.frame.dbstruct.Field;
	import com.hrms.frame.dbstruct.Table;
	import com.hrms.frame.utility.AdminCode;
	import com.hrms.frame.utility.AdminDb;
	import com.hrms.hjsj.sys.IResourceConstant;
	import com.hrms.hjsj.utils.Sql_switcher;
	import com.hrms.struts.taglib.CommonData;
	import com.hrms.struts.valueobject.UserView;
	import org.apache.commons.beanutils.LazyDynaBean;

	import javax.sql.RowSet;
	import java.sql.Connection;
	import java.sql.Date;
	import java.text.Collator;
	import java.text.SimpleDateFormat;
	import java.util.*;

public class GzSpFlowBo {
	private Connection conn=null;
	private UserView userView=null;
	private int gz_module=0;
	public GzSpFlowBo(Connection conn,UserView userView,int gz_module)
	{
		this.conn=conn;
		this.userView=userView;
		this.gz_module=gz_module;
	}
	public GzSpFlowBo(Connection conn,UserView view)
	{
		this.conn=conn;
		this.userView=view;
	}
	public GzSpFlowBo()
	{
	}
	/**
	 * 取得权限范围内的所有需要审批的工资套
	 * @return
	 */
	public ArrayList getSpSalarySetList()
	{
		ArrayList list = new ArrayList();
		RowSet rs=null;
		try
		{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select salaryid,cname,ctrl_param from salarytemplate ");
			if(this.gz_module==0)
				sql.append(" where (cstate is null or cstate='')");// 薪资类别
			else
				sql.append(" where cstate='1'");// 险种类别
			ContentDAO dao = new ContentDAO(this.conn);
			SalaryCtrlParamBo ctrlparam=null;
			 rs= dao.search(sql.toString()+" order by seq");
			while(rs.next())
			{
				if(this.gz_module==0)
				{
					if(!this.userView.isHaveResource(IResourceConstant.GZ_SET, rs.getString("salaryid")))
						continue;
				}
				else
				{
					if(!this.userView.isHaveResource(IResourceConstant.INS_SET, rs.getString("salaryid")))
						continue;
				}
				String ctrl_param=Sql_switcher.readMemo(rs, "ctrl_param");
				ctrlparam=new SalaryCtrlParamBo(this.conn,rs.getInt("salaryid"),ctrl_param); 
				String flow_flag=ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag");
				if(!"1".equalsIgnoreCase(flow_flag))
					continue;
				CommonData cd = new CommonData(rs.getInt("salaryid")+"",rs.getInt("salaryid")+"."+rs.getString("cname"));
				list.add(cd);
			}
			if(list.size()>0)
			{
				list.add(new CommonData("-2","全部"));
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
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	/**
	 * 取工资类别里业务日期
	 * @param salaryid
	 * @return
	 */
	public ArrayList getBusiDateList(String salaryid)
	{
		ArrayList list = new ArrayList();
		RowSet rs=null;
		try
		{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select gel.salaryid, gel.a00z2");
			if("-2".equals(salaryid)) //2014-5-23  dengcan 优化效率
				sql.append(",st.ctrl_param ");
			sql.append(" from gz_extend_log gel left join salarytemplate st on gel.salaryid=st.salaryid ");
			if(!"-2".equals(salaryid))
				sql.append(" where gel.salaryid="+salaryid);
			else{
				if(this.gz_module==0)
					sql.append(" where gel.salaryid in (select salaryid from salarytemplate where (cstate is null or cstate=''))");// 薪资类别
				else
					sql.append(" where gel.salaryid in (select salaryid from salarytemplate where cstate='1')");// 险种类别
			}
			sql.append(" order by gel.a00z2 desc");
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString());
			CommonData temp=null;
			HashMap amap = new HashMap();
			HashMap salaryMap=new HashMap();  //2014-5-23  dengcan 优化效率
			while(rs.next())
			{
				String _salaryid=rs.getString("salaryid");
				if("-2".equals(salaryid))
				{
					if(this.gz_module==0)
					{
						if(!this.userView.isHaveResource(IResourceConstant.GZ_SET, rs.getString("salaryid")))
							continue;
					}
					else
					{
						if(!this.userView.isHaveResource(IResourceConstant.INS_SET, rs.getString("salaryid")))
							continue;
					}
					String flow_flag="";
					if(salaryMap.get(_salaryid)==null)
					{
						String ctrl_param=Sql_switcher.readMemo(rs, "ctrl_param");
						SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.conn,rs.getInt("salaryid"),ctrl_param); 
						flow_flag=ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag");
						salaryMap.put(_salaryid,flow_flag);
					}
					else
						flow_flag=(String)salaryMap.get(_salaryid);
					
					if(!"1".equalsIgnoreCase(flow_flag))
						continue;
				}
				String strdate=PubFunc.FormatDate(rs.getDate("A00Z2"), "yyyy-MM-dd");
				if(amap.get(strdate)!=null)
					continue;
				amap.put(strdate, strdate);
				temp=new CommonData(strdate,strdate);
				list.add(temp);
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
	/**
	 * 取工资类别里的薪资员
	 * @param salaryid
	 * @return
	 */
	public ArrayList getUsrNameList(HashSet name)
	{
		ArrayList list = new ArrayList();
		try
		{
			Comparator co = Collator.getInstance(java.util.Locale.CHINA);
			String[] arr=new String[name.size()];
			
			CommonData temp=null;
			temp=new CommonData("-1","全部");
			list.add(temp);
			Iterator it = name.iterator();		
			int i=0;
			while(it.hasNext()){
				String tempName=it.next().toString();
				arr[i]=tempName;
				i++;
//				temp=new CommonData(tempName,tempName);
//				list.add(temp);
			}
			Arrays.sort(arr,co);
			for(int j=0;j<arr.length;j++){
				temp=new CommonData(arr[j],arr[j]);
				list.add(temp);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 取工资类别里审批状态
	 * @param salaryid
	 * @return
	 */
	public ArrayList getSpFlagList(HashSet name)
	{		
		ArrayList list = new ArrayList();
	try
	{
		Comparator co = Collator.getInstance(java.util.Locale.CHINA);
		String[] arr=new String[name.size()];
		
		CommonData temp=null;
		temp=new CommonData("-1","全部");
		list.add(temp);
		Iterator it = name.iterator();		
		int i=0;
		while(it.hasNext()){
			String tempName=it.next().toString();
			arr[i]=tempName;
			i++;
//			temp=new CommonData(tempName,tempName);
//			list.add(temp);
		}
		Arrays.sort(arr,co);
		for(int j=0;j<arr.length;j++){
			temp=new CommonData(arr[j],arr[j]);
			list.add(temp);
		}
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return list;
	}
	/**
	 * 取工资类别里当前操作人
	 * @param salaryid
	 * @return
	 */
	public ArrayList getcurrList(HashSet name)
	{		ArrayList list = new ArrayList();
	try
	{
		CommonData temp=null;
		temp=new CommonData("-1","全部");
		list.add(temp);
		Iterator it = name.iterator();		
		while(it.hasNext()){
			String tempName=it.next().toString();
			if(tempName!=null&&!"".equals(tempName)){
				temp=new CommonData(tempName,tempName);
				list.add(temp);
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
	 * 取工资套下，业务日期内的发放次数
	 * @param salaryid
	 * @param busiDate
	 * @return
	 */
	public ArrayList getCountList(String salaryid,String busiDate)
	{
		ArrayList list = new ArrayList();
		RowSet rs=null;
		try
		{
			if(busiDate==null|| "-1".equals(busiDate))
				return list;
			StringBuffer sql = new StringBuffer("");
			sql.append(" select distinct a00z3 from gz_extend_log where ");
			sql.append(" salaryid="+salaryid);
			sql.append(" and ");
			sql.append(Sql_switcher.dateToChar("a00z2", "yyyy-MM-dd")+"='"+busiDate+"'");
			ContentDAO dao = new ContentDAO(this.conn);
		    rs = dao.search(sql.toString());
			CommonData temp=null;
			while(rs.next())
			{
				String count=rs.getString("a00z3");
				temp = new CommonData(count,count);
				list.add(temp);
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
		return list;
	}
	/**
	 * 取审批过程数据列表
	 * @param salaryid
	 * @return
	 */
	public ArrayList getSpDataList(String salaryid,String busiDate)//,String count
	{
		ArrayList list = new ArrayList();
		try
		{
			if(busiDate==null|| "-1".equals(busiDate)|| "-1".equals(salaryid))//||count==null||count.equals("-1")
				return list;
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer("");
			sql.append(" select "+Sql_switcher.dateToChar("gel.a00z2", "yyyy-MM-dd")+" as a00z2,gel.A00Z3,gel.SP_Flag,gel.UserName,st.cname,gel.salaryid,st.ctrl_param ");
			sql.append(" ,case when nullif(operuser.fullname,'')  is not null  then operuser.fullname else gel.username end as fullname "); //20150603 dengcan
			sql.append(" from gz_extend_log gel left join salarytemplate st on gel.salaryid=st.salaryid ");
			sql.append(" left join operuser on gel.username=operuser.username "); //20150603 dengcan
			sql.append(" where "+Sql_switcher.dateToChar("gel.a00z2", "yyyy-MM-dd")+"='"+busiDate+"'");
			if(!"-2".equals(salaryid)){
		    	sql.append(" and gel.salaryid="+salaryid);
			}
			else{
				if(this.gz_module==0)
					sql.append(" and gel.salaryid in (select salaryid from salarytemplate where (cstate is null or cstate=''))");// 薪资类别
				else
					sql.append(" and gel.salaryid in (select salaryid from salarytemplate where cstate='1')");// 险种类别
			}
			sql.append("  order by st.seq,gel.a00z3,gel.sp_flag");//and a00z3="+count+"
			RowSet rs = dao.search(sql.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				String sp_flag=rs.getString("SP_Flag");
				String admin=rs.getString("UserName");
				String count=rs.getString("a00z3");
				String cname=rs.getString("cname");
				String fullName=rs.getString("fullname"); //20150603 dengcan
				if("-2".equals(salaryid))
				{
			    	if(this.gz_module==0)
		    		{
			    		if(!this.userView.isHaveResource(IResourceConstant.GZ_SET, rs.getString("salaryid")))
			    			continue;
			    	}
		    		else
		    		{
			    		if(!this.userView.isHaveResource(IResourceConstant.INS_SET, rs.getString("salaryid")))
				    		continue;
		    		}
			    	String ctrl_param=Sql_switcher.readMemo(rs, "ctrl_param");
					SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.conn,rs.getInt("salaryid"),ctrl_param); 
					String flow_flag=ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag");
					if(!"1".equalsIgnoreCase(flow_flag))
						continue;
				}
				/*
				RecordVo userVo = new RecordVo("operuser");
				userVo.setString("username", admin);
				String fullName="";
				if(dao.isExistRecordVo(userVo))
				{
			    	userVo = dao.findByPrimaryKey(userVo);
			    	fullName = userVo.getString("fullname");
			    	if(fullName==null||fullName.equals(""))
				    	fullName=admin;
				}
				*/
				if("01".equals(sp_flag)|| "06".equals(sp_flag))
				{
					bean.set("cname",cname);
					bean.set("busidate", busiDate);
					bean.set("count", count);
					bean.set("sp_flag", AdminCode.getCodeName("23",sp_flag));
					bean.set("admin",fullName);
					bean.set("curr_oper", "06".equals(sp_flag)?"":fullName);
					list.add(bean);
				}
				else if("05".equals(sp_flag)|| "02".equals(sp_flag))
				{
					ArrayList temp=this.getInOperSalarySetSpFlowInfo(rs.getString("salaryid"), busiDate, count, admin, cname, dao,fullName);
					list.addAll(temp);
					
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	private ArrayList getInOperSalarySetSpFlowInfo(String salaryid,String busiDate,String count,String admin,String cname,ContentDAO dao,String fullName)
	{
		ArrayList list = new ArrayList();
		try
		{
			String withNoLock="";
			if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
				withNoLock=" WITH(NOLOCK) ";
			
			StringBuffer buf = new StringBuffer(""); 
			buf.append(" select distinct sp_flag from "+admin+"_salary_"+salaryid+withNoLock);
			buf.append(" where a00z3="+count);
			buf.append(" and "+Sql_switcher.dateToChar("a00z2", "yyyy-MM-dd")+"='"+busiDate+"' order by sp_flag");
			RowSet rs = dao.search(buf.toString());
			boolean isTwo=false;
			HashMap existSpMap = new HashMap();
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				String sp_flag=rs.getString("SP_Flag");
				if("03".equals(sp_flag)|| "01".equals(sp_flag)|| "07".equals(sp_flag)|| "06".equals(sp_flag))
				{
					existSpMap.put(sp_flag.toUpperCase(), sp_flag);
		    		bean.set("cname",cname);
		    		bean.set("busidate", busiDate);
		    		bean.set("count", count);
			    	bean.set("sp_flag", AdminCode.getCodeName("23",sp_flag));
		    		bean.set("admin",fullName);
		    		if("06".equals(sp_flag))
		    			bean.set("curr_oper", "");
		    		else
			        	bean.set("curr_oper", fullName);
		    		list.add(bean);
				}
				else if("02".equals(sp_flag))
				{
					isTwo=true;
				}
			}
			if(isTwo)
			{
				ArrayList history=this.getInOperSalarySetSpFlowInfoFromSalaryHistory(salaryid, busiDate, count, admin, cname, dao,fullName,existSpMap);
				list.addAll(history);
			}
			
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	private ArrayList getInOperSalarySetSpFlowInfoFromSalaryHistory(String salaryid,String busiDate,String count,String admin,String cname,ContentDAO dao,String fullName,HashMap existSpMap)
	{
		ArrayList list = new ArrayList();
		RowSet rs=null;
		try
		{
			String withNoLock="";
			if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
				withNoLock=" WITH(NOLOCK) ";
			
			StringBuffer buf = new StringBuffer("");
			buf.append(" select distinct sp_flag "); //,curr_user "); 
			buf.append(" ,case when nullif(operuser.fullname,'') is not null then operuser.fullname else SalaryHistory.curr_user end as curr_user "); //20150603 dengcan
			
			buf.append(" from SalaryHistory "+withNoLock); 
			buf.append(" left join operuser "+withNoLock+" on SalaryHistory.curr_user=operuser.username "); //20150603 dengcan
				
			
			buf.append(" where a00z3="+count+" and salaryid="+salaryid);
			buf.append(" and "+Sql_switcher.dateToChar("a00z2", "yyyy-MM-dd")+"='"+busiDate+"' and sp_flag<>'06'");
			buf.append(" and UPPER(SalaryHistory.userflag)='"+admin.toUpperCase()+"'");
			rs = dao.search(buf.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				String sp_flag=rs.getString("SP_Flag");
				if(existSpMap.get(sp_flag.toUpperCase())!=null)
					continue;
				if("03".equals(sp_flag)|| "01".equals(sp_flag))
				{
		    		bean.set("cname",cname);
		    		bean.set("busidate", busiDate);
		    		bean.set("count", count);
			    	bean.set("sp_flag", AdminCode.getCodeName("23",sp_flag));
		    		bean.set("admin",fullName);
			    	bean.set("curr_oper", fullName);
		    		list.add(bean);
				}
				else if("02".equals(sp_flag)|| "07".equals(sp_flag))
				{
					bean.set("cname",cname);
		    		bean.set("busidate", busiDate);
		    		bean.set("count", count);
			    	bean.set("sp_flag", AdminCode.getCodeName("23",sp_flag));
		    		bean.set("admin",fullName);
		    		String curr_user=rs.getString("curr_user")==null?"":rs.getString("curr_user");
		    	/*
		    		if(!curr_user.equals(""))
		    		{
		        		RecordVo userVo = new RecordVo("operuser");
			    		userVo.setString("username", curr_user);
			    		if(dao.isExistRecordVo(userVo))
			    		{
			    	    	userVo = dao.findByPrimaryKey(userVo);
				        	String afullName = userVo.getString("fullname");
				    	    if(afullName!=null&&!afullName.equals(""))
				    		   curr_user=afullName;
			    		}
		    		}
			    	*/
		    		bean.set("curr_oper", curr_user);
			    	list.add(bean);
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
				try{
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
	/**判断是从更多进入，还是直接从连接进入，返回页面不一样=0是从更多进入*/
	private String enteryType="1";
	public void setEnteryType(String enteryType)
	{
		this.enteryType=enteryType;
	}
	public ArrayList getHotInvestigateList(String discriminateFlag)
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			/**调查问卷*/
			if("rese".equalsIgnoreCase(discriminateFlag))
			{
		    	String topicsql = "select releasedate,days,id,content,description from investigate where flag=1 and status=0";// status=0是热点调查
		    	Calendar today=Calendar.getInstance();
		    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		    	rs = dao.search(topicsql);
		     	StringBuffer id_buf = new StringBuffer("");
		    	while(rs.next())
		    	{
		    		Date releasedate=rs.getDate("releasedate");
		    		String date_str=dateFormat.format(releasedate);
			    	Calendar startDate=Calendar.getInstance();
		    		startDate.set(Calendar.YEAR,Integer.parseInt(date_str.substring(0,4)));
			    	startDate.set(Calendar.MONTH,Integer.parseInt(date_str.substring(5,7))-1);
			    	startDate.set(Calendar.DATE,Integer.parseInt(date_str.substring(8)));
			    	Calendar endDate=(Calendar)startDate.clone();
			    	int days=rs.getInt("days");
			    	endDate.add(Calendar.DATE,days);
			    	if((today.get(Calendar.YEAR)+":"+today.get(Calendar.MONTH)+":"+today.get(Calendar.DATE)).equals(startDate.get(Calendar.YEAR)+":"+startDate.get(Calendar.MONTH)+":"+startDate.get(Calendar.DATE))||
			    			(today.get(Calendar.YEAR)+":"+today.get(Calendar.MONTH)+":"+today.get(Calendar.DATE)).equals((endDate.get(Calendar.YEAR)+":"+endDate.get(Calendar.MONTH)+":"+endDate.get(Calendar.DATE)))||(today.after(startDate)&&today.before(endDate)))
			    	{
			      		id_buf.append(" or id='"+rs.getString("id")+"' ");
			    	}
		    	}
		    	StringBuffer sql_buf = new StringBuffer("");
		    	sql_buf.append(" select itemid,id,name from investigate_item where ");
		    	if(id_buf.length()>1)
		      		sql_buf.append(id_buf.toString().substring(3));
		    	else
		    		sql_buf.append(" 1=2 ");
	    		rs=null;
		    	rs=dao.search(sql_buf.toString());
		    	HashMap map = new HashMap();
		    	while(rs.next())
	    	   	{
		    		if(map.get(rs.getString("id"))==null)
			    	{
			    		map.put(rs.getString("id"), rs.getString("id"));
			    	   LazyDynaBean bean = new LazyDynaBean();
			    	   bean.set("name", rs.getString("name")==null?"":rs.getString("name"));
			     	   bean.set("url", "/selfservice/welcome/hot_topic.do?b_query=query&homePageHotId="+PubFunc.encryption(rs.getString("itemid"))+"&enteryType="+this.enteryType+"&discriminateFlag="+discriminateFlag);
				       bean.set("id",rs.getString("itemid"));
				       bean.set("status", "0");
				       bean.set("discriminateFlag", discriminateFlag);
			    	   list.add(bean);
		    		}
    			}
	    		topicsql = "select releasedate,days,id,content,description from investigate where flag=1 and status=1";//问卷调查，
	    		rs = dao.search(topicsql);
		    	while(rs.next())
	      		{
		     		Date releasedate = rs.getDate("releasedate");
		     		if (null==releasedate)
                    {
                        continue;
                    }
	    			String date_str=dateFormat.format(releasedate);
		    		Calendar startDate=Calendar.getInstance();
	    			startDate.set(Calendar.YEAR,Integer.parseInt(date_str.substring(0,4)));
		    		startDate.set(Calendar.MONTH,Integer.parseInt(date_str.substring(5,7))-1);
		    		startDate.set(Calendar.DATE,Integer.parseInt(date_str.substring(8)));
		     		Calendar endDate=(Calendar)startDate.clone();
		    		int days=rs.getInt("days");
		     		endDate.add(Calendar.DATE,days);
		    		if((today.get(Calendar.YEAR)+":"+today.get(Calendar.MONTH)+":"+today.get(Calendar.DATE)).equals(startDate.get(Calendar.YEAR)+":"+startDate.get(Calendar.MONTH)+":"+startDate.get(Calendar.DATE))||
			    			(today.get(Calendar.YEAR)+":"+today.get(Calendar.MONTH)+":"+today.get(Calendar.DATE)).equals((endDate.get(Calendar.YEAR)+":"+endDate.get(Calendar.MONTH)+":"+endDate.get(Calendar.DATE)))||(today.after(startDate)&&today.before(endDate)))
		    		{
			    		 LazyDynaBean bean = new LazyDynaBean();
			    		 if(this.userView.isHaveResource(IResourceConstant.INVEST,rs.getString("id"))||this.userView.isSuper_admin())
			     		 {
				        	 bean.set("name", rs.getString("content")==null?"":rs.getString("content"));
				        	 bean.set("url", "/selfservice/infomanager/askinv/questionnaire.do?b_query=link&id="+PubFunc.encryption(rs.getString("id"))+"&flag=1&enteryType="+this.enteryType+"&isClose=1&discriminateFlag="+discriminateFlag);
				        	 bean.set("id",rs.getString("id"));
			    			 bean.set("status", "1");
			    			 bean.set("discriminateFlag", discriminateFlag);
				         	 list.add(bean);
			    		 }
			    	}
	    		}
		    	/*我的问卷*/
		    	String receiver = "Usr"+this.userView.getA0100();
				
				StringBuffer sql = new StringBuffer();
				sql.append("select Pending_title,Pending_url,Pending_status,bread");
				sql.append(" from t_hr_pendingtask");
				sql.append(" where Pending_type='80'");
				sql.append(" and (pending_status='0' or pending_status='3')");			
				sql.append(" and Receiver='" + receiver + "'");
				//zhangh 2020-1-15 【56374】V77问卷调查：收集配置时，将问卷分享给内部员工，员工在系统首页的“热点调查”中查看，
				// 新发的问卷排序在最后面，只有点开more才能看见新发的问卷，顺序不对
				sql.append(" order by pending_id desc ");
				LazyDynaBean abean=null;
				rs = dao.search(sql.toString());
				while(rs.next()){
					abean=new LazyDynaBean();
					abean.set("name",rs.getString("Pending_title"));
					abean.set("url",rs.getString("Pending_url"));
					abean.set("id",this.userView.getA0100());
	    			abean.set("status", "1");
	    			abean.set("discriminateFlag", discriminateFlag);
					abean.set("target","_blank");
	                list.add(abean);
				}
			}
			/**培训问卷，只有自助用户才可以*/
			else if("train".equalsIgnoreCase(discriminateFlag))
			{
				if(this.userView.getA0100()==null|| "".equals(this.userView.getA0100()))
					return list;
				RecordVo vo=new RecordVo("r31");
				if(!vo.hasAttribute("ctrl_param"))
				{
					DbWizard dbWizard=new DbWizard(this.conn);
					DBMetaModel dbmodel=new DBMetaModel(this.conn);
					
					Table table=new Table("r31");
					TableOperateBo tableOperateBo=new TableOperateBo(this.conn);
					Field aField0=tableOperateBo.getField(false,"ctrl_param","控制参数","M",3,0);
					table.addField(aField0);
					dbWizard.addColumns(table);
					dbmodel.reloadTableModel("r31");
				}
				//学习评估只显示发布和已结束状态的调查问卷及评估 chenxg update 2015-02-05
				rs=dao.search("select r3130,ctrl_param,r31.r3101 from r40,r31 where  r40.r4005=r31.r3101 and  r40.r4001='"+this.userView.getA0100()+"' and r40.NBase='"+this.userView.getDbname()+"' and (r4013='03' or r4013='04' or r4013='05' ) and (R3127='04' or R3127='06')");
				CtrlParamXmlBo xmlBo=new CtrlParamXmlBo(this.conn);
				while(rs.next())
				{
					String ctrl_param=Sql_switcher.readMemo(rs,"ctrl_param");
					String a_name="";
					if(rs.getString("r3130")!=null)
						a_name=rs.getString("r3130");
					if(ctrl_param!=null&&ctrl_param.trim().length()>0)
					{
						xmlBo.setXml(ctrl_param);
						ArrayList lists=xmlBo.getEvaluateModelList();
						for(Iterator a=lists.iterator();a.hasNext();)
						{
							LazyDynaBean abean=(LazyDynaBean)a.next();
							String name=(String)abean.get("name");   //  1:评估模版  0：调查问卷
							String type=(String)abean.get("type");
							String run=(String)abean.get("run");
							String end_date=(String)abean.get("end_date");
							String value=(String)abean.get("value");
							
							if("true".equals(run))
							{
								Calendar today=Calendar.getInstance();
								Calendar endDate=Calendar.getInstance();
								endDate.set(Calendar.YEAR,Integer.parseInt(end_date.substring(0,4)));
								endDate.set(Calendar.MONTH,Integer.parseInt(end_date.substring(5,7))-1);
								endDate.set(Calendar.DATE,Integer.parseInt(end_date.substring(8)));
								if(today.before(endDate)||today.equals(endDate))
								{
									LazyDynaBean bean=new LazyDynaBean();//abean;
									if("job".equals(type))
									{
										if("1".equals(name))
											bean.set("name",a_name+"培训班评估");
										else
											bean.set("name",a_name+"培训班效果调查问卷");
									}
									else
									{
										if("1".equals(name))
											bean.set("name",a_name+"教师评估");
										else
											bean.set("name",a_name+"教师教学效果调查问卷");
									}
									/* bean.set("name", rs.getString("content")==null?"":rs.getString("content"));
						        	 bean.set("url", "/selfservice/infomanager/askinv/questionnaire.do?b_query=link&id="+rs.getString("id")+"&flag=1&enteryType="+this.enteryType+"&home=5&isClose=1");
						        	 bean.set("id",rs.getString("id"));
					    			 bean.set("status", "1");*/
									String[] ids = value.split(":");
									String planId = ids[0];
									if("1".equals(name))//学习评估中点击进入评估页面链接加密  chenxg  2015-01-29
										bean.set("url", "/train/evaluatingStencil.do?b_query=link&r3101="+SafeCode.encode(PubFunc.encrypt(rs.getString("r3101")))+"&id="+value+"&flag=2&type="+type+"&enteryType="+this.enteryType+"&isClose=1&discriminateFlag="+discriminateFlag);
									else
										bean.set("url","/module/system/questionnaire/template/AnswerQn.jsp?suerveyid="+PubFunc.encryption(planId)+"&flag=2&enteryType="+this.enteryType+"&isClose=1&discriminateFlag="+discriminateFlag);
									bean.set("discriminateFlag", discriminateFlag);
									list.add(bean);
								}
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
		finally
		{
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
	
/*	private ArrayList<HashMap<String,String>> getqnlist(int qnid,ArrayList<HashMap<String,String>> qnlist,int planid,ContentDAO dao,String planname){
		RowSet rs = null;
		String qn_qnid_data = "qn_"+qnid+"_data";
		HashMap maps = new HashMap();
        String qn_matrix_qnid_data = "qn_matrix_"+qnid+"_data";
        DbWizard w = new DbWizard(this.conn);
        String planId = "";
    	try {
	        if(w.isExistTable(qn_qnid_data,false)){//如果存在
	        	String qnsql = "select * from "+qn_qnid_data+" where mainObject='"+this.userView.getA0100()+"' and planid='"+planid+"' and status in('0','1')";
				rs = dao.search(qnsql);
	        	while(rs.next()){
	        		planId = String.valueOf(rs.getInt("planid"));
	        	}
	        }
	        else if(w.isExistTable(qn_matrix_qnid_data,false)){//如果存在
	        	String qnsql = "select planid from "+qn_matrix_qnid_data+" where mainObject='"+this.userView.getA0100()+"' and planid='"+planid+"' and status in('0','1') group by planid";
	        	rs = dao.search(qnsql);
	        	while(rs.next()){
	        		planId = String.valueOf(rs.getInt("planid"));
	        	}
	        }
	        if(planId!=""){
	        	 maps.put("planid", planId);
		         maps.put("planname", planname);
			     maps.put("a0100", this.userView.getA0100());
			     qnlist.add(maps);
	        }
    	} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(rs!=null)
			{
				try{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
        return qnlist;
	}*/
	public ArrayList getSpDataList(String onlyNumber)
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		Connection conn=null;
		try
		{
			String userName="T"+onlyNumber;
			conn=(Connection)AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer buf = new StringBuffer("");
			ArrayList alist = new ArrayList();
			buf.append(" select salaryid,cname,ctrl_param from salarytemplate ");
			buf.append(" where (cstate is null or cstate='')");// 薪资类别
			SalaryCtrlParamBo ctrlparam=null;
			rs= dao.search(buf.toString());
			StringBuffer temp=new StringBuffer("");
			while(rs.next())
			{
				String ctrl_param=Sql_switcher.readMemo(rs, "ctrl_param");
				ctrlparam=new SalaryCtrlParamBo(this.conn,rs.getInt("salaryid"),ctrl_param); 
				String flow_flag=ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag");
				if(!"1".equalsIgnoreCase(flow_flag))
				{
					continue;
				}
				String manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
				LazyDynaBean bean  = new LazyDynaBean();
				if((manager!=null&&manager.length()>0&&userName.equalsIgnoreCase(manager))||(manager==null||manager.trim().length()==0))
				{
					bean.set("salaryid", rs.getString("salaryid"));
					bean.set("manager", userName);
					alist.add(bean);
				}
				temp.append(" or salaryid="+rs.getString("salaryid"));
			}
			int acount=0;
			if(alist.size()>0)
			{
		    	DbWizard dbWizard=new DbWizard(conn);	    	
	    		RecordVo vo  = new RecordVo("operuser");
	    		vo.setString("username", userName.toUpperCase());
		    	if(dao.isExistRecordVo(vo))
		    	{
		    		vo = dao.findByPrimaryKey(vo);
			    	String password = vo.getString("password");
	             	UserView view  =new UserView(userName,password,conn);
	             	if(view!=null)
	             	{
	         	    	for(int i=0;i<alist.size();i++)
	            		{
	         	    		LazyDynaBean bean = (LazyDynaBean)alist.get(i);
	         	    		String salaryid=(String)bean.get("salaryid");
	         		    	String manager = (String)bean.get("manager");
	         	    		if(view.isHaveResource(IResourceConstant.GZ_SET, salaryid)||manager.equalsIgnoreCase(userName))
	         		    	{
	         		    		String tableName = userName+"_salary_"+salaryid;
	         		    		Table table=new Table(tableName);
	         		    		if(dbWizard.isExistTable(table.getName(),false))
	         		    		{
	         			    		rs = dao.search("select sp_flag from "+table.getName()+" where sp_flag='07'");
	         			    		if(rs.next())
	         			    			acount++;
	         		    		}
	             			}
	             		}
	             	}
	    		}
			}
			if(temp.toString().length()>0)
			{
				StringBuffer sql = new StringBuffer("");
				sql.append("select count(*),salaryid from (select salaryid,curr_user,a00z2,a00z3 from salaryhistory where ");
				sql.append("("+temp.toString().substring(3)+")");
				sql.append(" and UPPER(curr_user)='"+userName.toUpperCase()+"'");
				sql.append(" and ( sp_flag='02' or sp_flag='07' )  group by salaryid,a00z2,a00z3,curr_user) T group by salaryid,a00z2,a00z3");
				rs=dao.search(sql.toString());
				int count=0;
				while(rs.next())
				{
					count+=rs.getInt(1);
				}
				if(count>0)
				{
					count+=acount;
					list.add("/gz/auto_logon/header.do?br_query=link");
					list.add(count+"");
				}
			}
			if(list.size()==0&&acount>0)//去工资发放里找
			{
				
         		list.add("/gz/auto_logon/header.do?br_init=link");
				list.add(acount+"");
         		
			}
			if(list.size()==0)
			{
				list.add("/gz/auto_logon/header.do?br_first=link");
				list.add("0");
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
			if(conn!=null)
			{
				try
				{
					conn.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return list;
	}
}
