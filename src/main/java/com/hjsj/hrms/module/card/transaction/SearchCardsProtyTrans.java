/*
 * Created on 2006-5-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.module.card.transaction;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hjsj.hrms.module.card.businessobject.CardConstantSet;
import com.hjsj.hrms.module.card.businessobject.YkcardViewSubclass;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import com.ibm.icu.text.SimpleDateFormat;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.*;


@SuppressWarnings("serial")
public class SearchCardsProtyTrans extends IBusiness {

	/* 
	 * ============================================================
	 * userpriv 取消此参数 传递 
	 * userpriv之前用于菜单配置 区分模板显示是否关联控制权限：selfinfo 针对自助用户  直接显示指标内容，  noinfo针对业务用户   针对此情况再次区分fieldpurv 0 默认控制 1 不控制
	 * havepriv  取消此参数  之前是区分是否按照权限显示，0 需要权限 1 不需要权限 
	 * flag :infoself或nopriv 此参数控制  查看岗位说明书 校验是否越权 可根据inforkind=4 判断是岗位登记表使用时校验a0100传递参数校验是否越权
	 * cardtype：	     no,SS_SETCARD,leaber,mycard,jpcard,myposcard,ZP_POS_TEMPLATE,ZP_POS_TEMPLATE2,plan 取消此参数
	 * 使用inforkind替代  调用模块1：人员，2：单位，4：岗位；5：绩效；6：岗位说明书7：代表薪酬 8：领导桌面 9：招聘
	 * dbname 与a0100 整合 库前缀+`+人员id号(加上“`”用于区分是人员id还是组织机构代码)
	 * 保留参数
	 * fieldpurv 1 不控制权限 0 控制权限  与userpriv和havepriv权限设置冲突 取消此设置
	 * multi_cards 取消此参数 薪酬表配置 可根据inforkind=7 设置
	 * ==================================================================
		fieldpuriv	登记表指标权限控制    1 不控制    默认控制
		a0100	库前缀+`+人员id号(加上“`”用于区分是人员id还是组织机构代码)
		bizDate	业务日期；使用场景：模板指标条件定位 不为空时按照业务日期查询条件
		tabid	登记表号 可以多个以逗号分隔，2,3,4
		inforkind	调用模块1：人员，2：单位，4：岗位；5：绩效；6：岗位说明书7：代表薪酬 8：领导桌面 9：招聘,10 培训自助 我的积分
		plan_id	绩效（非必填，绩效使用时必填）/general/card/searchcard.do?b_query=link`home=2`inforkind=5`result=0`temp_id=Z011`plan_id=241
		"select template_id from per_plan where plan_id="+plan_id
		"select object_id,a0101 from per_result_"+plan_id
		"SELECT tabids from per_template where template_id='"+temp_id+"'"
	 * 
	 */
	
	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		String zp_flag=(String)this.getFormHM().get("zp_flag");
		String inforkind=(String)this.getFormHM().get("inforkind");
		if(StringUtils.isNotEmpty(zp_flag)&&"9".equals(inforkind)) {
			try {
				//招聘接口 不登录进行虚拟登录操作
				if(this.userView==null) {
					if("zp_noticetemplate_flag".equals(PubFunc.decrypt(zp_flag))) {
						this.userView=new UserView("su", this.frameconn);
						this.userView.canLogin(true);
					}else {
						return;
					}
					
				}else {
					//招聘外网登录后查看招聘公示 同样也用su伪登录，防止用户无模板或者指标权限无法查看
					if("zp_noticetemplate_flag".equals(PubFunc.decrypt(zp_flag))) {
						this.userView=new UserView("su", this.frameconn);
						this.userView.canLogin(true);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			if(this.userView==null) {
				return;
			}
		}
		
		String contentFlag=(String)this.getFormHM().get("flag");
		String fieldpurv=(String)this.getFormHM().get("fieldpurv");
		fieldpurv=PubFunc.decrypt(fieldpurv);//加密参数解密
		this.getFormHM().put("fieldpurv", fieldpurv);
		if("1".equals(inforkind)||"2".equals(inforkind)||"4".equals(inforkind)||"6".equals(inforkind))
			this.getFormHM().put("btnFunction", getBtnFunction(inforkind, userView,""));
		else if("7".equals(inforkind))//fieldpurv区分 我的薪酬与员工薪酬 
			this.getFormHM().put("btnFunction", getBtnFunction(inforkind, userView,fieldpurv));
		if("tree".equals(contentFlag)) {
			//加载登记表树  start 
			String tabid=(String)this.getFormHM().get("tabid");
			String a0100=(String)this.getFormHM().get("a0100");
			try {
				if(StringUtils.isNotEmpty(tabid)) {
					if(tabid.split(",").length>1) {//配置多个显示登记表树 一个不显示   String tabid,String inforkind,String a0100
						this.getFormHM().put("tableTree", getCardTreeList(tabid, inforkind,a0100));
					}
				}else
					this.getFormHM().put("tableTree", getCardTreeList("", inforkind,a0100));
				this.getFormHM().put("flagType", true);
			} catch (Exception e) {
				this.getFormHM().put("flagType", false);
				this.getFormHM().put("errMsg", e.getMessage());
				e.printStackTrace();
			}
			
		}else if("bookMark".equals(contentFlag)){//根据选择的tabid加载页签
			String tabid=(String)this.getFormHM().get("tabid");
			ContentDAO dao=new ContentDAO(this.frameconn);
			RowSet rs=null;
			ArrayList<String> tablist=new ArrayList<String>();
			tablist.add(tabid);
			ArrayList<HashMap<String,String>> books=new ArrayList<HashMap<String,String>>();
			HashMap<String,String> map=null;
			try {
				rs=dao.search("select * from rtitle where tabid=? order by pageid",tablist);
				while(rs.next()) {
					map=new HashMap<String,String>();
					map.put("pageid", rs.getString("pageid"));
					map.put("title", rs.getString("title"));
					books.add(map);
				}
				this.getFormHM().put("tabMark", books);//查询的页签集合
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				PubFunc.closeDbObj(rs);
			}
			
		}else if("checkdbname".equals(contentFlag)){//切换人员库 重新组装sql
			String dbname=(String)this.getFormHM().get("dbname");
			String plan_id=(String)this.getFormHM().get("plan_id");
			ArrayList list=getTableBuilderSql(inforkind,dbname,plan_id);
			this.getFormHM().put("tableConfig",list.get(0));
			if(list.size()>1) {
				this.getFormHM().put("onlyname", list.get(1));
			}
		}else if("salary".equals(contentFlag)){//我的薪酬
			String a0100=(String)this.getFormHM().get("a0100");
			try {
				String A0100="";
				String nbase="";
				if(StringUtils.isBlank(a0100)) {//我的薪酬 员工薪酬区分：
					A0100=this.userView.getA0100();
					nbase=this.userView.getDbname();
				}else {
					A0100=a0100.split("`")[1];
					nbase=a0100.split("`")[0];
				}
				HashMap<String,Object> map=null;
				map=getDistinctYear(this.frameconn, A0100, nbase);
				this.getFormHM().put("salaryMap", map);
				this.getFormHM().put("typeFlag", true);
			} catch (Exception e) {
				e.printStackTrace();
				this.getFormHM().put("typeFlag",false);
				this.getFormHM().put("msg",e.getMessage());
			}
		}else if("salaryCount".equals(contentFlag)){//我的薪酬切换月份 查询当前月份A00Z1 次数
			try {
				String a0100=(String)this.getFormHM().get("a0100");
				String A0100=a0100.split("`")[1];
				String nbase=a0100.split("`")[0];
				String year=(String)this.getFormHM().get("year");
				String month=(String)this.getFormHM().get("month");
				ArrayList<HashMap<String,String>> list=getSalaryCount(nbase, A0100, year, month);
				if(list!=null&&list.size()>0)
					this.getFormHM().put("count", JSONArray.fromObject(list).toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}else if("myCardScore".equals(contentFlag)){//我的积分登记表
			try {
				String a0100=(String)this.getFormHM().get("a0100");
				String A0100="";
				String nbase="";
				if(StringUtils.isBlank(a0100)) {
					A0100=this.userView.getA0100();
					nbase=this.userView.getDbname();
					this.getFormHM().put("a0100",nbase+"`"+A0100);
				}else {
					A0100=a0100.split("`")[1];
					nbase=a0100.split("`")[0];
				}
				this.getFormHM().put("salaryMap", getScoreProty(this.frameconn, a0100, nbase));
				this.getFormHM().put("typeFlag", true);
			} catch (Exception e) {
				e.printStackTrace();
				this.getFormHM().put("typeFlag",false);
				this.getFormHM().put("msg",e.getMessage());
			}
		}else if("photoUrl".equals(contentFlag)){
			String nbase=(String)this.getFormHM().get("nbase");
			String a0100=(String)this.getFormHM().get("a0100");
			this.getFormHM().put("photoUrl", getPhotoPath(nbase, a0100));
		}else {
			RowSet rs=null;
			ContentDAO dao=new ContentDAO(this.frameconn);
			try {
				String dbname=(String)this.getFormHM().get("dbname");
				//String userbase=(String)this.getFormHM().get("userbase");
				String a0100=(String)this.getFormHM().get("a0100");
				a0100=PubFunc.decrypt(a0100);
				if(StringUtils.isBlank(a0100)&&("7".equals(inforkind)||"10".equals(inforkind))) {
					a0100=this.userView.getDbname()+"`"+this.userView.getA0100();
					this.getFormHM().put("a0100", a0100);
				}
				String plan_id=(String)this.getFormHM().get("plan_id");
				//获取打印预演参数
				HashMap<String,String> printViewProty=printViewProtyMap(fieldpurv);//打印预演参数
				this.getFormHM().put("printViewProty",printViewProty);
				
				String dbType="1";
		   		switch(Sql_switcher.searchDbServer())
		   	    {
		   			  case Constant.MSSQL:
		   		      {
		   		    	  dbType="1";
		   				  break;
		   		      }
		   			  case Constant.ORACEL:
		   			  { 
		   				  dbType="2";
		   				  break;
		   			  }
		   			  case Constant.DB2:
		   			  {
		   				  dbType="3";
		   				  break;
		   			  }
		   	    }
		   		this.getFormHM().put("dbType", dbType);//数据库标识
				//招聘不校验人员范围权限
		   		if(!"1".equals(fieldpurv)&&StringUtils.isNotEmpty(a0100)&&!"9".equals(inforkind)){//取消flag 直接判断 用户是否对userbase a0100 校验越权
					CheckPrivSafeBo checkPrivSafeBo = new CheckPrivSafeBo(this.frameconn,userView);
					if("1".equals(inforkind)||"7".equals(inforkind)||"10".equals(inforkind)){
					    //我的积分 a0100为空的时候有可能是未关联自助人员  
					    if("10".equals(inforkind)&&"`".equals(a0100)) {
					        if(StringUtils.isEmpty(this.userView.getA0100())) {
					            throw new Exception("未关联自助用户，无法查看我的积分！");
					        }
					    }
						String A0100="";
						//当人员账号和人员库与登录用户相同时 不校验人员权限
						if(this.userView.getDbname().equalsIgnoreCase(a0100.split("`")[0])&&this.userView.getA0100().equalsIgnoreCase(a0100.split("`")[1])) {
							A0100="";
						}else {
							A0100=checkPrivSafeBo.checkA0100("", checkPrivSafeBo.checkDb(a0100.split("`")[0]), a0100.split("`")[1], "");
						}
						if(this.userView.getDbname().equalsIgnoreCase(a0100.split("`")[0])&&this.userView.getA0100().equalsIgnoreCase(a0100.split("`")[1])) {
						    a0100=this.userView.getDbname()+"`"+this.userView.getA0100();
						}
						if(StringUtils.isEmpty(A0100)) {
							a0100=this.userView.getDbname()+"`"+this.userView.getA0100();
						}else {
						    a0100=checkPrivSafeBo.checkDb(a0100.split("`")[0])+"`"+A0100;
						}
					}else if("2".equals(inforkind)){
						a0100=checkPrivSafeBo.checkOrg(a0100, "4");
					}else{
						a0100=checkPrivSafeBo.checkOrg(a0100, "4");
					}
				}/*else if(StringUtils.isNotEmpty(a0100)&&this.userView.getStatus()==4&&"1".equals(inforkind)) {
					a0100=this.userView.getDbname()+"`"+this.userView.getA0100();
					this.getFormHM().put("a0100", a0100);
				}*/
		   		YkcardViewSubclass yk=new YkcardViewSubclass();
				if("1".equals(inforkind)) {//dbname 为空时 查询人员库  
					ArrayList<HashMap<String, String>> dblist=new ArrayList<HashMap<String,String>>();
					if(StringUtils.isEmpty(a0100)) {
						dblist=yk.getdblist(userView, this.frameconn,"");
					}else {
						dblist=yk.getdblist(userView, this.frameconn,a0100.split("`")[0]);
					}
					if(dblist.size()<1) {
						throw new Exception("此用户未设置人员库权限，请联系管理员！");
					}
					this.getFormHM().put("dblist", dblist);
					dbname=dblist.get(0).get("id");
				}else if("5".equals(inforkind)) {//绩效查询设置模板
					ArrayList<String> list=new ArrayList<String>();
					String temp_id=(String)this.getFormHM().get("temp_id");
					list.add(temp_id);
					String sql="SELECT tabids from per_template where template_id=?";
					rs=dao.search(sql, list);
					String tabid="";
					while(rs.next()) {
						if(StringUtils.isEmpty(rs.getString("tabids"))) {
							throw new Exception("未配置对应模板，请检查配置！");
						}
						tabid+=","+rs.getString("tabids");
					}
					this.getFormHM().put("tabid", tabid.substring(1));
					ArrayList<HashMap<String, String>> dblist=new ArrayList<HashMap<String,String>>();
					dblist=yk.getdblist(userView, this.frameconn,"Usr");
					this.getFormHM().put("dblist", dblist);
				}
				if(StringUtils.isEmpty(a0100)&&!"7".equals(inforkind)&&!"10".equals(inforkind)) {//只显示单个信息无需此设置
					//表格控件 页面显示人员列表框 start  
					ArrayList list=getTableBuilderSql(inforkind,dbname,plan_id);
					this.getFormHM().put("tableConfig",list.get(0));
					if(list.size()>1) {
						this.getFormHM().put("onlyname", list.get(1));
					}
				}
				this.getFormHM().put("a0100",a0100);
				this.getFormHM().put("typeFlag", true);
			} catch (Exception e) {
				e.printStackTrace();
				this.getFormHM().put("typeFlag",false);
				this.getFormHM().put("msg",e.getMessage());
				
			}finally {
				PubFunc.closeDbObj(rs);
			}
		}
		
	}
	/***
	 * 我的薪酬切换月份 查询月份薪资发放次数
	 * @param nbase
	 * @param A0100
	 * @param year
	 * @param month
	 * @return
	 */
	private ArrayList<HashMap<String,String>> getSalaryCount(String nbase,String A0100,String year,String month) throws Exception{
		ArrayList<HashMap<String,String>> list=new ArrayList<HashMap<String,String>>();
		StringBuffer sbf=new StringBuffer();
		RowSet rs=null;
		ContentDAO dao=new ContentDAO(this.frameconn);
		String str=" and "+Sql_switcher.year("A00Z0")+"="+year+" and "+Sql_switcher.month("A00Z0")+"="+month;
		try {
			sbf.append("select max(A00Z1) A00Z1 from (");
			sbf.append(" select max(A00Z1) A00Z1 from SalaryHistory  ");
			sbf.append(" where a0100='"+A0100+"' ");
			sbf.append(str);
			sbf.append(" and sp_flag='06' and lower(nbase)='"+nbase.toLowerCase()+"' ");
			sbf.append(" union ");
			sbf.append(" select max(A00Z1) A00Z1 from Salaryarchive  ");
			sbf.append(" where a0100='"+A0100+"' ");
			sbf.append(str);
			sbf.append(" and sp_flag='06' and lower(nbase)='"+nbase.toLowerCase()+"' ");
			sbf.append(") A");
			
			rs=dao.search(sbf.toString());
			int maxCount=0;
			while(rs.next()) {
				if(StringUtils.isNotEmpty(rs.getString("A00Z1")))
					maxCount=Integer.parseInt(rs.getString("A00Z1"));
			}
			HashMap<String,String> map=null;
			if(maxCount>0) {
				for(int i=1;i<=maxCount;i++) {
					map=new HashMap<String, String>();
					map.put("countId", i+"");
					map.put("countName", i+"");
					list.add(map);
				}
				map=new HashMap<String, String>();
				map.put("countId", "all");
				map.put("countName", "全月");
				list.add(map);	
				return list;
			}
			
		}finally {
			PubFunc.closeDbObj(rs);
		}
		
		return null;
		
		
	}
	/***
	 * 查看我的积分
	 * @param conn
	 * @param a0100
	 * @param nbase
	 * @return
	 * @throws Exception
	 * String a0100,String nbase 参数暂时无用
	 */
	private HashMap<String,Object> getScoreProty(Connection conn,String a0100,String nbase) throws Exception{
		try {
			 HashMap<String,Object> map=new HashMap<String, Object>();
			 ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
		     String tabid = constantbo.getNodeAttributeValue("/param/em_point_tab", "id");
		     if(tabid == null || "".equals(tabid) || "#".equals(tabid))
		           throw new Exception(ResourceFactory.getProperty("train.setparam.integral.hint"));
		     ArrayList<HashMap<String,String>> yearlist=new ArrayList<HashMap<String,String>>();
		     Calendar ca=Calendar.getInstance();
		     int year=ca.get(Calendar.YEAR);
		     HashMap<String,String> yearmap=null;
		     for(int i=year;i>year-5;i--) {
		    	 yearmap=new HashMap<String, String>();
		    	 yearmap.put("yearId", i+"");
		    	 yearmap.put("yearName", i+"年");
		    	 yearlist.add(yearmap);//
		     }  
		     String bizDate=year+"-01-01";
		     map.put("tabid", tabid);
		     map.put("year",JSONArray.fromObject(yearlist).toString());
		     map.put("bizDate", bizDate);
		     map.put("queryflag", "1");
		     return map;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/***
	 * 我的薪酬查询日期
	 * @param conn
	 * @param a0100
	 * @return
	 */
    private HashMap<String,Object> getDistinctYear(Connection conn,String a0100,String nbase) throws Exception
    {
    	
    	ContentDAO dao=new ContentDAO(conn);
    	
    	String queryflag="0";
		String year_restrict="";
    	int count=0;
		CardConstantSet cardConstantSet=new CardConstantSet(this.userView,this.frameconn);	
		String relating=cardConstantSet.getSearchRelating(dao);		
		String b0110=cardConstantSet.getRelatingValue(dao,this.userView.getA0100(),this.userView.getDbname(),relating,this.userView.getUserOrgId());				
		XmlParameter xml=new XmlParameter("UN",b0110,"00");
		xml.ReadOutParameterXml("SS_SETCARD",this.frameconn,"all");	
		String type=xml.getType();//薪酬表查询类型： 0 条件查询 1 日期查询
		queryflag=type!=null&&type.length()>=0?type:"0";
		year_restrict=xml.getYear_restrict();//不显示XX年以后的数据
    	HashMap<String,Object> map=new HashMap<String, Object>();
    	//默认系统当前日期
    	String data="";
        StringBuffer sql=new StringBuffer();
        RowSet rs=null;//dao.search(sql.toString());
    	try
    	{
    		if(!(nbase!=null&&nbase.length()>0))//显示查询日期一般是关联我的薪酬，库前缀关联nbase 其他的不需要显示查询日期
    			return null;
    		sql.setLength(0);
    		sql.append(" select "+Sql_switcher.dateToChar("MAX(A00Z0)")+" as A00Z0,MAX(A00Z1) as A00Z1 from (");
	    	sql.append(" select MAX(A00Z0) as A00Z0,MAX(A00Z1) as A00Z1 from SalaryHistory ");
	    	sql.append(" where A00z0 =( ");
	    	sql.append(" select MAX(A00Z0) as A00z0 from SalaryHistory where a0100='"+a0100+"' ");
	    	sql.append(" and sp_flag='06' and lower(nbase)='"+nbase.toLowerCase()+"' ) ");
	    	sql.append(" and a0100='"+a0100+"' and sp_flag='06' and lower(nbase)='"+nbase.toLowerCase()+"' ");
	    	sql.append(" union all ");
	    	sql.append(" select MAX(A00Z0) as A00Z0,MAX(A00Z1) as A00Z1 from Salaryarchive ");
	    	sql.append(" where A00z0 =( ");
	    	sql.append(" select MAX(A00Z0) as A00z0 from SalaryHistory where a0100='"+a0100+"' ");
	    	sql.append(" and sp_flag='06' and lower(nbase)='"+nbase.toLowerCase()+"' ) ");
	    	sql.append(" and a0100='"+a0100+"' and sp_flag='06' and lower(nbase)='"+nbase.toLowerCase()+"' ");
	    	sql.append(" ) AA");
    		rs=dao.search(sql.toString());
    		while (rs.next()) {
				data=rs.getString("a00z0");
				count=rs.getInt("a00z1");
			}
    		
    		if(StringUtils.isEmpty(data)) {
    			data=Calendar.getInstance().get(Calendar.YEAR)+"-"+(Calendar.getInstance().get(Calendar.MONTH)+1)+"-"+Calendar.getInstance().get(Calendar.DATE);
    		}
    		
    		if(data!=null&&data.length()>0){
    			SimpleDateFormat sbf=null;
    			if(data.length()<11){
    				sbf=new SimpleDateFormat("yyyy-MM-dd");
    			}else{
    				sbf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    			}
    			Date date=sbf.parse(data);
    			Calendar cal=Calendar.getInstance();
    			cal.setTime(date);
    			int month=cal.get(Calendar.MONTH)+1;//月
    			//int day=cal.get(Calendar.DATE);//天
    			int season=1;//季度
    			
    			if(month>=4&&month<=6) {
    				season=2;
    			}else if(month>=7&&month<=9) {
    				season=3;
    			}else if(month>=10&&month<=12) {
    				season=4;
    			}
    			map.put("month", month);
    			map.put("season",season);
    			
    			ArrayList<HashMap<String,String>> countList=new ArrayList<HashMap<String,String>>();
    			HashMap<String,String> countMap=null;
    			//count=10;
    			if(count>0) {
    				for(int i=1;i<=count;i++) {//次数
    					countMap=new HashMap<String, String>();
    					countMap.put("countId", i+"");
    					countMap.put("countName", i+"");
    					countList.add(countMap);
    				}
    				countMap=new HashMap<String, String>();
    				countMap.put("countId", "all");
					countMap.put("countName", "全月");
    				countList.add(countMap);
    				
    				map.put("count",JSONArray.fromObject(countList).toString());
    			}
    			map.put("time",cal.get(Calendar.YEAR)+"-"+((cal.get(Calendar.MONTH)+1)<10?"0":"")+(cal.get(Calendar.MONTH)+1)+"-"+((Calendar.DATE)<10?"0":"")+cal.get(Calendar.DATE));
    			ArrayList<HashMap<String,String>> yearList=new  ArrayList<HashMap<String,String>>();
    			//默认显示最近一条至前十年的记录
    			HashMap<String,String> yearMap=null;
    			for (int i = 0; i < 10; i++) {
    				if(StringUtils.isNotEmpty(year_restrict)) {
    					if(cal.get(Calendar.YEAR)-i<Integer.parseInt(year_restrict)) {//设置XX年以后的数据不显示
    						continue;
    					}
    				}
    					
    				yearMap=new HashMap<String, String>();
    				yearMap.put("yearId", (cal.get(Calendar.YEAR)-i)+"");
    				yearMap.put("yearName", (cal.get(Calendar.YEAR)-i)+"年");
    				yearList.add(yearMap);
					//list.add((cal.get(Calendar.YEAR)-i)+"");
				}
    			map.put("queryflag", queryflag);
    			map.put("year", JSONArray.fromObject(yearList).toString());
    		}
    		/**关记录*/
    		if(rs!=null)
    			rs.close();
    	}finally {
			PubFunc.closeDbObj(rs);
		}    	
    	return map;
    }
	
	/***
	 * 查询登记表树 
	 * multi_cards 不为-1 时 查询设置我的薪酬登记表
	 * tabid只有一个表号时 隐藏树，多个时 显示树   tabid=1,2,3,4  配置登记表  根据登记表类型显示 关联inforkind 不属于当前类型登记表不显示
	 * tabid为空时 根据inforkind显示登记表树，
	 * 根据userview判断是否有表权限  
	 * */
	private ArrayList<HashMap<String,Object>> getCardTreeList(String tabid,String inforkind,String a0100) throws Exception{
		ArrayList<String> tabList=new ArrayList<String>();
		String[] tabArry=tabid.split(",");
		for (int i = 0; i < tabArry.length; i++) {
			if(StringUtils.isNotEmpty(tabArry[i]))
				tabList.add(tabArry[i]);
		}
		ArrayList<HashMap<String,Object>> dataMaplist=new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> dataMap=null;
		RowSet rs=null;
		ContentDAO dao=new ContentDAO(this.frameconn);
		try {
			
			//区分查询 普通登记表与薪酬登记表
			if(!"7".equals(inforkind)) {
				Map<String,String> stortMap=new LinkedHashMap<String, String>();
				//HashMap<String,String> rnameMap=new HashMap<String, String>();
				String flag="";
				/***
				 *          B 机构登记表   K 岗位说明书       P 绩效反馈表  H 基准岗位说明书   //A 人员登记表 
				 * 
				 * flag     ：Ｂ　              Ｋ　                       Ｐ　                  Ｈ    				  A
				  inforkind：2（单位）　４(岗位)　             ５(绩效评估)　６(基准岗位说明书)   1 
				 * */
				
				if("1".equals(inforkind)) {
					flag="A";
				}else if("2".equals(inforkind)) {
					flag="B";
				}else if("4".equals(inforkind)) {
					flag="K";
				}else if("5".equals(inforkind)) {
					flag="P";
				}else if("6".equals(inforkind)) {
					flag="H";
				}
				StringBuffer sbf=new StringBuffer();
				sbf.append("select * from rsort where flag is null or flag='"+flag+"' order by id ");
				

				rs=dao.search(sbf.toString());
				while(rs.next()) {
					stortMap.put(rs.getString("sortid"), rs.getString("sortname"));
				}
				if(stortMap!=null) {
					ArrayList<String> moduleflagList=new ArrayList<String>();
					for(String sortid:stortMap.keySet() ) {
						String sortname=stortMap.get(sortid);
						rs=null;
						sbf.setLength(0);
						moduleflagList.add(sortid);
						sbf.append("select * from rname where flagA='"+flag+"' and moduleflag='"+sortid+"' order by tabid");
						rs=dao.search(sbf.toString());
						//子节点集合
						ArrayList<HashMap<String,String>> childMapList=new ArrayList<HashMap<String,String>>();
						HashMap<String,String> childMap=null;
						
						while(rs.next()) {
							childMap=new HashMap<String, String>();
							String tableid=rs.getString("tabid");
							if(tabList.size()>0) {//配置多个table tablist不包含tabid 不放入树结构
								if(!tabList.contains(tableid)) {
									continue;
								}
							}
							if(!this.userView.isSuper_admin()&&!this.userView.isHaveResource(IResourceConstant.CARD, tableid)) {
								continue;
							}else {
								childMap.put("id", tableid);
								childMap.put("text",tableid+":"+rs.getString("name"));
								childMap.put("icon", "/images/overview_obj.gif");
								childMap.put("leaf", "true");
								childMapList.add(childMap);
							}
						}
						if(childMapList!=null&&childMapList.size()>0) {
							dataMap=new HashMap<String, Object>();
							dataMap.put("icon", "/images/open.png");
							dataMap.put("isCategory", "1");
							dataMap.put("text", sortname);
							dataMap.put("children", childMapList);
							dataMaplist.add(dataMap);
						}
					}
					//登记表不在RSORT 内的
					sbf.setLength(0);
					if(moduleflagList.size()>0) {
						rs=null;
						sbf.append("select * from rname where flagA='"+flag+"' ");//and moduleflag='"+sortid+"' order by tabid
						for(int i=0;i<moduleflagList.size();i++) {
							sbf.append(" and moduleflag<>'"+moduleflagList.get(i)+"' ");
						}
						sbf.append(" order by tabid");
						rs=dao.search(sbf.toString());
						HashMap<String,Object> othMap=null;
						while(rs.next()) {
							othMap=new HashMap<String, Object>();
							othMap.put("id", rs.getString("tabid"));
							othMap.put("text",rs.getString("tabid")+":"+rs.getString("name"));
							othMap.put("icon", "/images/overview_obj.gif");
							othMap.put("leaf", "true");
							dataMaplist.add(othMap);
						}
						
					}
					if(dataMaplist.size()<1) {
						throw new Exception("此用户无登记表资源权限，请联系管理员！");
					}
					
					return dataMaplist;
				}
				
			}else if("7".equals(inforkind)){//薪酬配置登记表 为1 时
				ArrayList salaryCards=new ArrayList();//=searchcardlist(inforkind);
				String isMobile=(String)this.getFormHM().get("isMobile");
				HashMap<String,Object> salaryCardMap=null;
				CardConstantSet cardConstantSet=new CardConstantSet(this.userView,this.frameconn);	
				String relating=cardConstantSet.getSearchRelating(dao);		
				String b0110=cardConstantSet.getRelatingValue(dao,this.userView.getA0100(),this.userView.getDbname(),relating,this.userView.getUserOrgId());				
				XmlParameter xml=new XmlParameter("UN",b0110,"00");
				xml.ReadOutParameterXml("SS_SETCARD",this.frameconn,"all");	
				String type=xml.getType();//薪酬表查询类型： 0 条件查询 1 日期查询
				String flag=xml.getFlag();
				boolean app = "1".equals(isMobile);
				String A0100=a0100.split("`")[1];
				String nbase=a0100.split("`")[0];
				if(this.userView.getA0100().equals(A0100))//配置薪酬表 则全部不校验指标权限  根据a0100 是否等于 当前操作人 区分是查看自己薪酬 还是查看员工薪酬
				{//只显示操作人自己的信息
					salaryCards=cardConstantSet.setCardidSelectSelfinfo(this.frameconn,this.userView,flag,nbase,this.userView.getA0100(),b0110,xml, app);
				}else
				{
					salaryCards=cardConstantSet.setCardidSelect(this.frameconn,this.userView,flag,nbase,A0100,b0110,xml);
				}
				if(salaryCards==null||salaryCards.size()<1) {
					throw new Exception("未配置薪酬表！");
				}
				for (int i = 0; i < salaryCards.size(); i++) {
					CommonData data=(CommonData)salaryCards.get(i);
					salaryCardMap=new HashMap<String, Object>();
					if(tabList.size()>0) {//配置多个table tablist不包含tabid 不放入树结构
						if(!tabList.contains(data.getDataValue())) {
							continue;
						}
					}
					if(!this.userView.isSuper_admin()&&!this.userView.isHaveResource(IResourceConstant.CARD, data.getDataValue())) {
						continue;
					}else {
						salaryCardMap.put("id",data.getDataValue() );
						salaryCardMap.put("text",data.getDataValue()+":"+data.getDataName().substring(("("+data.getDataValue()+")").length()));
						salaryCardMap.put("icon", "/images/overview_obj.gif");
						salaryCardMap.put("leaf", "true");
						dataMaplist.add(salaryCardMap);
					}
				}
				return dataMaplist;
			}
		} catch (Exception e) {
			throw e;
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return null;
	}
	
	//当a0100为空时 根据返回查询结果集sql
	private ArrayList getTableBuilderSql(String inforkind,String dbname,String plan_id) {
		//其他配置无下拉选项
		ArrayList<String> configList=new ArrayList<String>();
		String config="";
		StringBuffer sql=new StringBuffer();
		DbWizard db=new DbWizard(this.frameconn);
		//唯一标识
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一标识 唯一标识关联人员
		try{
			if(db.isExistTable("t_card_result", false)||"5".equals(inforkind)) {
				sql.setLength(0);
				if("1".equals(inforkind)) {
					ArrayList dbList=this.userView.getPrivDbList();//操作人人员库
					if(dbList==null)//自助用户未设置人员库权限时 直接获取当前用户人员库权限
						dbList.add(this.userView.getDbname());
					sql.append("select A.*,dbname.dbid from (");
					for(int i=0;i<dbList.size();i++) {
						String fieldSql="";
						String dbName=dbList.get(i).toString();
						if(!this.userView.isSuper_admin()){
							String pri_sql=this.updateResult(dbName);
							if(StringUtils.isNotEmpty(pri_sql)) {
								fieldSql+=" and objid in (select A0100 "+pri_sql+" ) ";
							}
						}
						sql.append("select "+(StringUtils.isNotEmpty(onlyname)?dbName+"A01."+onlyname+",":"")+" "+dbName+"A01.b0110 as b0110,"+dbName+"A01.E0122 as e0122,"
								+ " "+dbName+"A01.A0101 as objDesc,A.Objid,"+dbName+"A01.A0000,'"+dbName+"' as dbname,A.username,A.flag  from ( ");
						sql.append(" select * from t_card_result where username='"+this.userView.getUserName()+"' and nbase='"+dbName+"' "
								+ "and flag=1 and status='"+this.userView.getStatus()+"' "+fieldSql+") A left join "+dbName+"A01 on A.objid="+dbName+"A01.A0100");
						if(i<dbList.size()-1)
							sql.append(" union all ");
					}
					sql.append(") A left join dbname on A.dbname=dbname.pre") ;
				}else if("2".equals(inforkind)) {
					sql.append("select A.objid,B.codeitemdesc as objDesc,B.A0000 ");
					sql.append("from (");
					sql.append("select objid from t_card_result ");
					sql.append("where username='"+this.userView.getUserName()+"'");
					sql.append(" and flag=2 ");
					sql.append(" and status="+this.userView.getStatus());
					sql.append(")A,");
					sql.append("organization B where A.objid=B.codeitemid");
					
				}else if("4".equals(inforkind)) {
					sql.append("select A.objid,B.codeitemdesc as objDesc,B.codeitemid as A0000 ");
					sql.append("from (");
					sql.append("select objid from t_card_result ");
					sql.append("where username='"+this.userView.getUserName()+"'");
					sql.append(" and flag=4 ");
					sql.append(" and status="+this.userView.getStatus());
					sql.append(")A,");
					sql.append("organization B where B.codeitemid=A.objid");
				}else if("5".equals(inforkind)) {
					sql.append("select object_id as objid,a0101 as objDesc,object_id as A0000 from per_result_"+plan_id);
			    	PerformanceImplementBo bo=new PerformanceImplementBo(this.frameconn,this.userView,plan_id);
			    	String where=bo.getPrivWhere(this.userView);		    	
			    	if(where!=null&&where.length()>0)
			    		sql.append(" where 1=1 "+where);
				}else if("6".equals(inforkind)) {
					String codeset=new CardConstantSet().getStdPosCodeSetId();
		    		sql.append("select A.objid as objid,B.codeitemdesc as objDesc,B.A0000 as A0000 "
		    				+ "from t_card_result A ,CodeItem B "
		    				+" where B.codeitemid=A.objid "
		    				+ "and B.codesetid='"+codeset+"' "
		    				+" and A.flag=6 "// 基准岗位
		    				+  "and A.status="+this.userView.getStatus()
		    				+" and A.username='"+userView.getUserName()+"' ");
				}
			}
		  ArrayList<ColumnsInfo> columnsInfoList=new ArrayList<ColumnsInfo>();
			ColumnsInfo idInfo=new ColumnsInfo();
			ColumnsInfo idDescInfo=new ColumnsInfo();
			idInfo.setColumnType("A");
			idInfo.setCodesetId("0");
			idInfo.setColumnId("objid");
			idInfo.setColumnDesc("人员id");
			idInfo.setReadOnly(true);
			idInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			
			idDescInfo.setColumnWidth(220);
			idDescInfo.setColumnType("A");
			idDescInfo.setCodesetId("0");
			idDescInfo.setColumnId("objdesc");
			if("2".equals(inforkind)) {//单位
				idDescInfo.setColumnDesc("机构");
			}else if("6".equals(inforkind)) {
				idDescInfo.setColumnDesc("基准岗位名称");
			}else if("4".equals(inforkind)){
				idDescInfo.setColumnDesc("岗位名称");
			}else {
				idDescInfo.setColumnDesc("姓名");
			}
			idDescInfo.setRendererFunc("searchCard_me.personRendererFunc");

			columnsInfoList.add(idInfo);
			columnsInfoList.add(idDescInfo);
			if("1".equals(inforkind)) {
				ColumnsInfo col=new ColumnsInfo();
				//单位
				col.setColumnType("A");
				col.setCodesetId("UM");
				col.setColumnId("b0110");
				col.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
				columnsInfoList.add(col);
				//部门
				col=new ColumnsInfo();
				col.setColumnType("A");
				col.setCodesetId("UN");
				col.setColumnId("e0122");
				col.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
				columnsInfoList.add(col);
					//人员库
				col=new ColumnsInfo();
				col.setColumnType("A");
				col.setCodesetId("@@");
				col.setColumnId("dbName");
				col.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
				columnsInfoList.add(col);
				
				if(StringUtils.isNotEmpty(onlyname)) {
					col=new ColumnsInfo();
					col.setColumnType("A");
					col.setCodesetId("0");
					col.setColumnId(onlyname);
					col.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
					columnsInfoList.add(col);
				}
			}
			
			TableConfigBuilder builder = new TableConfigBuilder("ykcard",
					columnsInfoList, "ykcard", userView, this.getFrameconn());
			
			builder.setDataSql(sql.toString()/*"select * from ("+sql.toString()+") C"*/);
			if("1".equals(inforkind)) {
				builder.setOrderBy("order by dbid,A0000");
			}
			builder.setPageSize(20);
			builder.setTdMaxHeight(50);
			builder.setSelectable(true);
			builder.setSetScheme(false);
			builder.setModuleId("ykcard");
			builder.setColumnFilter(false);
			builder.setSortable(false);
			config = builder.createExtTableConfig();
			configList.add(config);
			if(StringUtils.isNotEmpty(onlyname)) {
				configList.add(onlyname+"`"+DataDictionary.getFieldItem(onlyname).getItemdesc());
			}
		  return configList;
		  
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	
	/** 获取人员图像所在路径 */
	private String getPhotoPath(String nbase, String a0100) {
		PhotoImgBo imgBo = new PhotoImgBo(this.frameconn);
		imgBo.setIdPhoto(true);
		return imgBo.getPhotoPath(nbase, a0100);
	}
	
	/**
	 * 打印预演参数配置
	 * */
	private HashMap<String,String> printViewProtyMap(String userpriv) {
		HashMap<String,String> map=new HashMap<String,String>();
		String tablePriv="";
		String menuPriv="";
		String isselfinfo="";
		if(!this.userView.isSuper_admin()) {
			if(userpriv!=null&& "1".equals(userpriv)) {
				menuPriv=this.userView.getEmp_fieldpriv().toString();
				tablePriv=this.userView.getEmp_tablepriv().toString();	
				isselfinfo="1";
			}else {
				menuPriv=this.userView.getFieldpriv().toString();
				tablePriv=this.userView.getTablepriv().toString();
			}
			if(menuPriv==null||menuPriv.length()<0)
				menuPriv=",";
			if(tablePriv==null||tablePriv.length()<0)
				tablePriv=",";
		}
		
		String superUser="0";
		if(this.userView.isSuper_admin())
		  superUser="1";
		
		
		
		map.put("menuPriv", menuPriv);   
		map.put("tablePriv",tablePriv);
		map.put("isselfinfo",isselfinfo);
		map.put("DBType","");
		map.put("UserName", this.userView.getUserName());
		map.put("userFullName",this.userView.getUserFullName());
		map.put("superUser",superUser);
		map.put("personlist","");
		map.put("version", this.userView.getVersion()+"");
		map.put("usedday", "");
		return map;
	}
	
	/**
	 * 取得卡片列表
	 * infortype A 人员，B单位,K职位 P绩效
	 * @throws GeneralException
	 */
	private ArrayList searchcardlist(String inforkind)throws Exception
	{
		String infortype="A";
         if(inforkind!=null)
         {
         	if("1".equals(inforkind))
         	{
         		infortype="A";            		
         	}
         	else if("2".equals(inforkind))
         	{
         		infortype="B";            		
         	}
         	else if("6".equals(inforkind))// 基准岗位
         	{
         		infortype="H";            		
         	}
         	else 
         	{
         		infortype="K";            		
         	}
         	
         }
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList<HashMap<String,String>> cardlist=new ArrayList<HashMap<String,String>>();
		StringBuffer buf=new StringBuffer();
		RowSet rset=null;
		try
		{
			ArrayList paralist=new ArrayList();		
			paralist.add(infortype);
			buf.append("select tabid,name,flaga from rname where flaga=?"  );
			HashMap<String,String> map=null;
			rset=dao.search(buf.toString(),paralist);
			while(rset.next())
			{
				map=new HashMap<String, String>();
				String tabid=rset.getString("tabid");
				if(this.getUserView()!=null&&this.getUserView().isHaveResource(IResourceConstant.CARD, tabid))
				{
					map.put("tabid", tabid);
					map.put("tabname",rset.getString("name"));
					cardlist.add(map);
				}
			}//while loop end.
			return cardlist;
		}finally {
			PubFunc.closeDbObj(rset);
		}
	}
	/**
	 * 自助用户 登录时查看登记表 如果查询结果表中无人员 更新结果表
	 * @throws Exception
	 */
	private String updateResult(String dbname) throws Exception{
		String sql="";
		ContentDAO dao=new ContentDAO(this.frameconn);
		RowSet rs=null;
		sql=this.userView.getPrivSQLExpression(dbname, false);
		if(this.userView.getStatus()==4) {
			try {
				//查询当前操作用户是否有人员范围权限
				boolean flag=false;
				rs=dao.search("select count(*) "+sql);
				if(rs.next()&&rs.getInt(1)==0) {
					flag=true;
				}
				rs=dao.search("select count(*) from t_card_result where username='"+this.userView.getUserName()+"' and flag=1 and status=4");
				if(rs.next()&&StringUtils.isNotEmpty(this.userView.getDbname())) {
					if(rs.getInt(1)==0) {
						ArrayList list=new ArrayList();
						list.add(this.userView.getUserName());
						list.add(dbname);
						list.add(this.userView.getA0100());
						list.add("1");
						list.add("4");
						dao.update("insert into t_card_result (username,nbase,objid,flag,status) values(?,?,?,?,?)", list);
					}
					//自助用户无人员范围权限时，更新查询结果表，过滤除操作人除自己外的人员数据
					if(flag&&rs.getInt(1)!=0) {
						dao.update("delete t_card_result where username='"+this.userView.getUserName()+"' and nbase='"+dbname+"' and objid<>'"+this.userView.getA0100()+"' and flag=1 and status=4");
					}
				}
				if(flag) {
					return "";
				}else {
					//自助用户权限范围授权本单位部门外的人员范围查询防止将本人过滤
					if(this.userView.getDbname().equalsIgnoreCase(dbname)) {
						return sql+" or A0100='"+this.userView.getA0100()+"'";
					}else {
						return sql;
					}
				}
			} finally {
				PubFunc.closeDbObj(rs);
			}
			
		}
		return sql;
	}
	
	//员工管理登记表员工自助进入时权限设置其他单位部门权限 无自己部门权限时，登记表内容默认显示权限范围内第一条记录 姓名下拉列表也应对应
	private String getA0101(String a0100,String dbname){
		ContentDAO dao=new ContentDAO(this.frameconn);
		RowSet rs=null;
		String name="";
		try {
			rs=dao.search("select A0101 from "+dbname+"A01 where a0100="+a0100);
			if(rs.next()){
				name=rs.getString("A0101");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}
	/**
	 * 根据模块用户判断当前用户按钮权限
	 * */
	private HashMap<String,String> getBtnFunction(String inforkind,UserView userView,String fieldPriv){
		HashMap<String,String> map=new HashMap<String, String>();
		String pdfBtn="false";
		String wordBtn="false";
		String printView="false";
		
		
		if("6".equals(inforkind)) {
			if(userView.hasTheFunction("250120701")) {
				pdfBtn="true";
			}
			if(userView.hasTheFunction("250120702")) {
				wordBtn="true";
			}
			if(userView.hasTheFunction("250120703")) {
				printView="true";
			}
			
		}else if("1".equals(inforkind)){
			if(userView.hasTheFunction("2604001")||userView.hasTheFunction("0315001")) {
				pdfBtn="true";
			}
			if(userView.hasTheFunction("2604002")||userView.hasTheFunction("0315002")) {
				wordBtn="true";
			}
			if(userView.hasTheFunction("2604003")||userView.hasTheFunction("0315003")) {
				printView="true";
			}
		}else if("2".equals(inforkind)) {
			if(userView.hasTheFunction("2304001")) {
				pdfBtn="true";
			}
			if(userView.hasTheFunction("2304002")) {
				wordBtn="true";
			}
			if(userView.hasTheFunction("2304003")) {
				printView="true";
			}
		}else if("7".equals(inforkind)) {
			if(!"1".equals(fieldPriv)) {
				if(userView.hasTheFunction("0102010301")) {
					pdfBtn="true";
				}
				if(userView.hasTheFunction("0102010302")) {
					wordBtn="true";
				}
				
			}else {
				if(userView.hasTheFunction("0302010301")) {
					pdfBtn="true";
				}
				if(userView.hasTheFunction("0302010302")) {
					wordBtn="true";
				}
				
			}
		}else if("4".equals(inforkind)) {
			if(userView.hasTheFunction("2504001")) {
				pdfBtn="true";
			}
			if(userView.hasTheFunction("2504002")) {
				wordBtn="true";
			}
			if(userView.hasTheFunction("2504003")) {
				printView="true";
			}
		} else {
			if(userView.hasTheFunction("0315001")||userView.hasTheFunction("2604001")) {
				pdfBtn="true";
			}
			if(userView.hasTheFunction("0315002")||userView.hasTheFunction("2604002")) {
				wordBtn="true";
			}
			if(userView.hasTheFunction("0315003")||userView.hasTheFunction("2604003")) {
				printView="true";
			}
		}
	
		map.put("pdfBtn", pdfBtn);
		map.put("wordBtn",wordBtn);
		map.put("printView",printView);
		return map;
	}
}
