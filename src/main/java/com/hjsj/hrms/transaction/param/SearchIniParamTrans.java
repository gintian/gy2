/*
 * Created on 2005-10-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.param;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hjsj.hrms.businessobject.sys.param.Sys_Infom_Parameter;
import com.hjsj.hrms.businessobject.sys.report.DyParameter;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdom.JDOMException;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchIniParamTrans extends IBusiness {
	/**
	 * 取得参数中已定义的卡片号
	 * @throws GeneralException
	 * @throws JDOMException 
	 */
	private void searchdefined()throws GeneralException, JDOMException
	{
		 try {
		 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		 String empcard=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"emp");
		 String orgcard=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"org");
		 String poscard=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"pos");
		 String rownums=sysbo.getValue(Sys_Oth_Parameter.EDIT_COLUMNS);
		 //日志热切换
		 String loglevel=sysbo.getValue(Sys_Oth_Parameter.LOGLEVEL);
		 Logger logger = LogManager.getRootLogger();
		 String log4jlevel = String.valueOf(logger.getLevel());
		 if(StringUtils.isEmpty(loglevel)||!loglevel.equalsIgnoreCase(log4jlevel)){
			 // 获取Logger对象，修改Logger对象的Level
			 loglevel = String.valueOf(logger.getLevel()).toUpperCase();
		 }
		 String num_per_page=sysbo.getValue(Sys_Oth_Parameter.NUM_PER_PAGE);
		 String approveflag=sysbo.getValue(Sys_Oth_Parameter.APPROVE_FLAG);
		 this.getFormHM().put("approveflag",approveflag);

		 String syn_bound=sysbo.getValue(Sys_Oth_Parameter.SYNTHETIZE_BOUND);
         String org_root_caption=sysbo.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
         String welcome_marquee=sysbo.getValue(Sys_Oth_Parameter.WELCOME_MARQUEE);
         String link_p_width=sysbo.getValue(Sys_Oth_Parameter.LIKN_P_WIDTH);
		 String link_p_height=sysbo.getValue(Sys_Oth_Parameter.LIKN_P_HEIGHT);
		 String lawrule_file_days=sysbo.getValue(Sys_Oth_Parameter.LAWRULE_FILE_DAYS);
		 String announce_days = sysbo.getValue(Sys_Oth_Parameter.ANNOUNCE_DAYS);
		 String stat_id=sysbo.getValue(Sys_Oth_Parameter.STAT_ID);
		 String ykcard_auto=sysbo.getValue(Sys_Oth_Parameter.YKCARD_AUTO);
		 String firstdays=sysbo.getValue(Sys_Oth_Parameter.DIARY,"firstdays");
		 firstdays=firstdays!=null&&firstdays.trim().length()>0?firstdays:"5";
		 String lastdays=sysbo.getValue(Sys_Oth_Parameter.DIARY,"lastdays");
		 lastdays=lastdays!=null&&lastdays.trim().length()>0?lastdays:"5";
		 
		 String subunitup=sysbo.getValue(Sys_Oth_Parameter.SUBUNITUP);
		 subunitup=subunitup!=null&&subunitup.trim().length()>0?subunitup:"true";
		 
		 String updisk=sysbo.getValue(Sys_Oth_Parameter.UPDISK);
		 updisk=updisk!=null&&updisk.trim().length()>0?updisk:"true";
		 String editupdisk=sysbo.getValue(Sys_Oth_Parameter.EDITUPDISK);
		 editupdisk=editupdisk!=null&&editupdisk.trim().length()>0?editupdisk:"true";
		 String inputchinfor=sysbo.getValue(Sys_Oth_Parameter.INPUTCHINFOR);
		 inputchinfor=inputchinfor!=null&&inputchinfor.trim().length()>0?inputchinfor:"1";
		 String condisk=sysbo.getValue(Sys_Oth_Parameter.CONDISK);
		 condisk=condisk!=null&&condisk.trim().length()>0?condisk:"0";

		 LazyDynaBean lazyDynaBean= (LazyDynaBean)DyParameter.getParameter(Sys_Oth_Parameter.PHOTO,"photo_w",this.frameconn);
		 String photo_w="";
		 if(lazyDynaBean!=null){
		  photo_w=(String)lazyDynaBean.get("photo_w");
		 photo_w=photo_w!=null&&photo_w.trim().length()>0?photo_w:"85";
		 }
		 
		 lazyDynaBean= (LazyDynaBean)DyParameter.getParameter(Sys_Oth_Parameter.PHOTO,"photo_h",this.frameconn);
		 String photo_h="";
		 if(lazyDynaBean!=null){
		 photo_h=(String)lazyDynaBean.get("photo_h");
		 photo_h=photo_h!=null&&photo_h.trim().length()>0?photo_h:"120";
		 }
		 String browse_photo=sysbo.getValue(Sys_Oth_Parameter.BROWSE_PHOTO);
		 browse_photo=browse_photo!=null&&browse_photo.length()>0?browse_photo:"0";//0默认为表格信息，1照片显示
		 String infosort_browse=sysbo.getValue(Sys_Oth_Parameter.INFOSORT_BROWSE);
		 String common_roster = sysbo.getValue(Sys_Oth_Parameter.COMMON_ROSTER);
		 String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		 String pinyin=sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
		 pinyin=pinyin!=null&&pinyin.length()>0?pinyin.toLowerCase():"";
		 String org_browse_format=sysbo.getValue(Sys_Oth_Parameter.ORG_BROWSE_FORMAT);
		 org_browse_format=(org_browse_format!=null&&org_browse_format.trim().length()>0)?org_browse_format:"0";
		 
		 String units=sysbo.getValue(Sys_Oth_Parameter.UNITS);
		 units=units!=null&&units.trim().length()>0?units:"0";
		 String place=sysbo.getValue(Sys_Oth_Parameter.PLACE);
		 place=place!=null&&place.trim().length()>0?place:"0";
		 DbWizard dbWizard =new DbWizard(this.getFrameconn());
		 String birthday_wid="";
		 if(!dbWizard.isExistTable("appoint_news",false))
		 {
			 birthday_wid="null";	
		 }else
		 {
			 birthday_wid=sysbo.getValue(Sys_Oth_Parameter.BIRTHDAY_WID);
			 if(birthday_wid==null||birthday_wid.length()<=0|| "null".equals(birthday_wid))
				 birthday_wid="";
		 }
		 String photo_other_itemid=sysbo.getValue(Sys_Oth_Parameter.PHOTO_OTHER_VIEW);//显示照片显示其他子集
		 photo_other_itemid=photo_other_itemid!=null&&photo_other_itemid.length()>0?photo_other_itemid:"";
		 String photo_other_view="";
		 if(photo_other_itemid!=null&&photo_other_itemid.length()>0)
		 {
			String[] pitems=photo_other_itemid.split(",");
			if(pitems==null||pitems.length==0)
			{
			    return;
			}
			FieldItem item=null;
			for(int i=0;i<pitems.length;i++)
			{
			    String fielditemid=pitems[i];
			    if(fielditemid==null|| "".equals(fielditemid))
			        continue;
			    item=DataDictionary.getFieldItem(fielditemid.toUpperCase());
			    if(item!=null)
			    {
			    	photo_other_view+=item.getItemdesc()+",";
			    }
			}
		 }
		 String blacklist_per=sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST,"base");//黑名单人员库
		 String blacklist_field=sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST,"field");//黑名单人员指标
		 String seprartor=sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
		 seprartor=seprartor!=null&&seprartor.length()>0?seprartor:"/";
		 
		 String browse_search_state=sysbo.getValue(Sys_Oth_Parameter.BROWSE_SEARCH_STATE);//人员信息浏览查询项状态 0，隐藏 1，显示
		 browse_search_state=browse_search_state!=null&&browse_search_state.length()>0?browse_search_state:"0";
		 //--------------报表上报是否支持审批  zhaoxg 2013-1-25 ------------------------
		 try{
		 String isApprove = sysbo.getValue(Sys_Oth_Parameter.ISAPPROVE);
		 String relation_id = sysbo.getValue(Sys_Oth_Parameter.APPROVEID);
		 if("".equals(isApprove)){
			 isApprove = "false";
		 }else if("false".equals(isApprove)){
			 relation_id = "00";
		 }
		 
		 ContentDAO dao = new ContentDAO(this.getFrameconn());
		 ArrayList approvelist = new ArrayList();
		 RowSet rs = null;
		 String sqll = "select * from t_wf_relation where actor_type = '4' and validflag = '1'";
		 rs = dao.search(sqll.toString());
		 CommonData datavo1 = new CommonData("00", "请选择...");
		 approvelist.add(datavo1);
			while (rs.next()) {
				CommonData datavo = new CommonData(rs.getString("relation_id"), rs.getString("cname"));
				approvelist.add(datavo);
			}
		 this.getFormHM().put("approvelist", approvelist);
		 this.getFormHM().put("isApprove", isApprove);
		 this.getFormHM().put("relation_id", relation_id);
		 } catch (Exception ex) {
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
		 }
			
		//-------------------------------------------------------------
		//明星员工
		ConstantXml constantXml = new ConstantXml(this.getFrameconn(),"SYS_OTH_PARAM","param");
		String complex_id=constantXml.getTextValue(Sys_Oth_Parameter.COMPLEX_ID);
		complex_id = complex_id == null?"":complex_id;
		if(complex_id.trim().length() <= 0){
			complex_id=sysbo.getValue(Sys_Oth_Parameter.COMPLEX_ID_NUM);
			complex_id = complex_id == null?"":complex_id;
		}
		this.getFormHM().put("complex_id", complex_id);
		ArrayList setList = new ArrayList();
		ArrayList itemList = new ArrayList();
		String fieldSetId = (String)this.getFormHM().get("setid");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		if(fieldSetId == null || "".equals(fieldSetId)){//首次进入
			String setsql = "select fieldsetid , fieldsetdesc,customdesc from fieldset where useflag <> 0 and (fieldsetid like 'A%') order by "+com.hrms.hjsj.utils.Sql_switcher.substr("fieldsetid", "1", "1")+",Displayorder";
			String firstSetId = "";
			boolean b = false;
			try {
				this.frowset = dao.search(setsql);
				while(this.frowset.next()){
					
					if("0".equals(this.userView.analyseTablePriv(this.frowset.getString("fieldsetid"))))
				        continue;
					if(!b){
						firstSetId = this.frowset.getString("fieldsetid");
						b=true;
					}
					CommonData dataobj = new CommonData();
					String setid = this.getFrowset().getString("fieldsetid");
					String setdesc ="";
					if(this.getFrowset().getString("customdesc")!=null&&this.getFrowset().getString("customdesc").length()>0)
						setdesc= this.getFrowset().getString("customdesc");
					else
						setdesc= this.getFrowset().getString("fieldsetdesc");
					dataobj = new CommonData(setid,/*"("+setid+")"+*/setdesc);
					setList.add(dataobj);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			ArrayList fielditemlist=DataDictionary.getFieldList(firstSetId,Constant.USED_FIELD_SET);
			CommonData top = new CommonData();
			top = new CommonData("","");
			itemList.add(top);
			if(fielditemlist!=null)
		    {
				for(int i=0;i<fielditemlist.size();i++)
			    {
			      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
			      if("M".equals(fielditem.getItemtype()))
				    	continue;
				  if("0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
				        continue;
				  CommonData dataobj = new CommonData();
				  dataobj = new CommonData(fielditem.getItemdesc(),fielditem.getItemdesc());
				  itemList.add(dataobj);
			   }
		   }		  
		}else{//改变指标集
			String itemsql = "select itemid,itemdesc  from fielditem where useflag <> 0 and fieldsetid ='"+fieldSetId+"'";
			CommonData top = new CommonData();
			top = new CommonData("","");
			itemList.add(top);
			try {
				this.frowset = dao.search(itemsql);
				while(this.frowset.next()){
					String itemdesc = this.frowset.getString("itemdesc");
					CommonData dataobj = new CommonData();
					dataobj = new CommonData(itemdesc,itemdesc);
					itemList.add(dataobj);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		this.getFormHM().put("setlist",setList);
		this.getFormHM().put("itemlist",itemList);
		 //--------------------------------------------------------------
		 //日明细审批限制
		 String dairyinfolimit=sysbo.getValue(Sys_Oth_Parameter.DAIRYINFOLIMIT);
		 String limit_HH=sysbo.getValue(Sys_Oth_Parameter.DAIRYINFOLIMIT,"limit_HH");
		 String limit_MM=sysbo.getValue(Sys_Oth_Parameter.DAIRYINFOLIMIT,"limit_MM");
		 this.getFormHM().put("seprartor", seprartor);
		 this.getFormHM().put("blacklist_per", blacklist_per);
		 this.getFormHM().put("blacklist_field", blacklist_field);
		 this.getFormHM().put("photo_other_itemid", photo_other_itemid);
		 this.getFormHM().put("photo_other_view", photo_other_view);
		 this.getFormHM().put("org_browse_format", org_browse_format);
		 this.getFormHM().put("rownums",rownums);
		 this.getFormHM().put("loglevel",loglevel);
		 this.getFormHM().put("empcard",empcard);
		 this.getFormHM().put("orgcard",orgcard);
		 this.getFormHM().put("poscard",poscard);
		 this.getFormHM().put("syn_bound",syn_bound);
		 this.getFormHM().put("org_root_caption",org_root_caption);
		 this.getFormHM().put("welcome_marquee",welcome_marquee);
		 this.getFormHM().put("num_per_page",num_per_page);
		 this.getFormHM().put("link_p_width",link_p_width);
		 this.getFormHM().put("link_p_height",link_p_height);
		 this.getFormHM().put("lawrule_file_days",lawrule_file_days);
		 this.getFormHM().put("announce_days", announce_days);
		 this.getFormHM().put("stat_id",stat_id);
		 this.getFormHM().put("ykcard_auto",ykcard_auto);
		 this.getFormHM().put("firstdays",firstdays);
		 this.getFormHM().put("lastdays",lastdays);
		 this.getFormHM().put("photo_w",photo_w);
		 this.getFormHM().put("photo_h",photo_h);
		 this.getFormHM().put("updisk",updisk);
		 this.getFormHM().put("subunitup",subunitup);
		 this.getFormHM().put("editupdisk",editupdisk);
		 this.getFormHM().put("inputchinfor",inputchinfor);
		 this.getFormHM().put("condisk",condisk);
		 this.getFormHM().put("browse_photo", browse_photo);
		 this.getFormHM().put("infosort_browse",infosort_browse);
		 this.getFormHM().put("common_roster",common_roster);
		 this.getFormHM().put("display_e0122",display_e0122);
		 this.getFormHM().put("pinyin_field", pinyin);
		 this.getFormHM().put("birthday_wid", birthday_wid);
		 this.getFormHM().put("units",units);
		 this.getFormHM().put("place",place);
		 this.getFormHM().put("browse_search_state", browse_search_state);
		 this.getFormHM().put("dairyinfolimit", dairyinfolimit);
	     this.getFormHM().put("limit_HH", limit_HH);
	     this.getFormHM().put("limit_MM", limit_MM);
		 getSynthetizeList(syn_bound);
		 getCondStatlist();
		 getCommonRoster();
		 getEmailtemplateList();
		 /***对Infom操作**/
		 Sys_Infom_Parameter sys_Infom_Parameter=new Sys_Infom_Parameter(this.getFrameconn(),"INFOM");
		 String photo_maxsize=sys_Infom_Parameter.getValue(Sys_Infom_Parameter.PHOTO,"MaxSize");
		 photo_maxsize=photo_maxsize!=null&&photo_maxsize.length()>=0?photo_maxsize:"";
		 this.getFormHM().put("photo_maxsize", photo_maxsize);
		 
		 String multimedia_maxsize=sys_Infom_Parameter.getValue(Sys_Infom_Parameter.MULTIMEDIA,"MultimediaMaxSize");
		 multimedia_maxsize=multimedia_maxsize!=null&&multimedia_maxsize.length()>=0?multimedia_maxsize:"";
		 this.getFormHM().put("multimedia_maxsize", multimedia_maxsize);
		 ///人员库
		 ArrayList dblist=DataDictionary.getDbpreList();
		 dblist=getNbaseList(dblist);
		 //人员列表
		 String setname="A01";
		 ArrayList fielditemlist = DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
		 ArrayList fieldlist=new ArrayList();
		 if(fielditemlist!=null)
		 {
				for(int i=0;i<fielditemlist.size();i++)
			    {
			      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
			      if("A".equals(fielditem.getItemtype())&& "0".equals(fielditem.getCodesetid()))
			      {
			    	  CommonData dataobj = new CommonData();
				      dataobj = new CommonData(fielditem.getItemid(), fielditem.getItemdesc());
				      fieldlist.add(dataobj); 
			      }
			    }
		 }
		 this.getFormHM().put("dblist", dblist);
		 this.getFormHM().put("fieldlist", fieldlist);
		 
		 String gquery_cond = sysbo.getValue(Sys_Oth_Parameter.GQUERY_COND,"value");
		 String g_cond=getGcond(gquery_cond);
		 this.getFormHM().put("gquery_cond", gquery_cond);
		 this.getFormHM().put("g_cond", g_cond);
		 
		 String themes = SysParamBo.getSysParamValue("THEMES", userView.getUserName());
		 this.getFormHM().put("themes", themes);
		 } catch (Exception ee) {
			 ee.printStackTrace();
				throw GeneralExceptionHandler.Handle(ee);
		 }
	}
	/**
	 * 获取人员分类条件
	 * @param gquery_cond
	 * @return
	 */
	private String getGcond(String gquery_cond) {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer g_cond=new StringBuffer();
		try {
			if(gquery_cond==null)
				gquery_cond="";
			String[] gquery_conds=gquery_cond.split(",");
			String sql="select id,name from lexpr";
			for (int i = 0; i < gquery_conds.length; i++) {
				if(gquery_conds[i]!=null&&gquery_conds[i].length()>0){
					sql="select name from lexpr where id="+gquery_conds[i];
					this.frowset=dao.search(sql);
					if(this.frowset.next())
					{	
						g_cond.append((i+1)+"."+this.frowset.getString("name")+"\r\n");
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return g_cond.toString();
	}
	/**
	 * 取得卡片列表
	 * @throws GeneralException
	 */
	private void searchcardlist()throws GeneralException
	{
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer buf=new StringBuffer();
		try
		{
			ArrayList emplist=new ArrayList();
			ArrayList orglist=new ArrayList();
			ArrayList poslist=new ArrayList();			
			buf.append("select tabid,name,flaga from rname");
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				String tabid=rset.getString("tabid");
				/**A 人员，B单位,C职位*/
				String flaga=rset.getString("flaga");
				if(this.userView.isHaveResource(IResourceConstant.CARD, tabid))
				{
					CommonData data=new CommonData();
					data.setDataValue(tabid);
					data.setDataName(rset.getString("name"));

					if("A".equalsIgnoreCase(flaga))
					{
						emplist.add(data);
					}
					else if("B".equalsIgnoreCase(flaga))
					{
						orglist.add(data);						
					}
					else if("K".equalsIgnoreCase(flaga))
					{
						poslist.add(data);							
					}					
				}
			}//while loop end.
			this.getFormHM().put("empcardlist", emplist);
			this.getFormHM().put("orgcardlist", orglist);
			this.getFormHM().put("poscardlist", poslist);			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 获取拼音简码指标
	 */
	private void searchpinyinfield()
	{
		try
		{
			ArrayList temp =new ArrayList();	
			ArrayList pinyin_fieldlist=new ArrayList();	
			temp = DataDictionary.getFieldList("A01",Constant.USED_FIELD_SET);
			for(int i=0;i<temp.size();i++)
			{
				FieldItem fi = (FieldItem)temp.get(i);
				if("A".equalsIgnoreCase(fi.getItemtype()) &&("0".equals(fi.getCodesetid())) &&(!"a0101".equalsIgnoreCase(fi.getItemid())))
				{
					CommonData data=new CommonData();
					data.setDataValue(fi.getItemid());
					data.setDataName(fi.getItemdesc());
					pinyin_fieldlist.add(data);
				}	
			}
			this.getFormHM().put("pinyin_fieldlist", pinyin_fieldlist);			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	
	}
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		
		try{
		   /* String fieldcond ="select itemid,itemdesc from fielditem where fieldsetid='A01' and useflag='1' and itemtype='A' and codesetid='0'";
		    String email="";
		    String phone="";
		    String sql="select str_value from constant where Constant='SS_EMAIL'";	
			//System.out.println("param");
		    List smaillist=ExecuteSQL.executeMyQuery(sql,this.getFrameconn());		 
		    if(!smaillist.isEmpty())
		    {
		 	LazyDynaBean rec=(LazyDynaBean)smaillist.get(0);
		 	email=rec.get("str_value")!=null?rec.get("str_value").toString():"";
		    }
		 
		    sql="select str_value from constant where Constant='SS_MOBILE_PHONE'";
		    List phonelist=ExecuteSQL.executeMyQuery(sql,this.getFrameconn());
		    if(!phonelist.isEmpty())
		    {
		     	LazyDynaBean rec=(LazyDynaBean)phonelist.get(0);
		     	phone=rec.get("str_value")!=null?rec.get("str_value").toString():"";
		    }		
		    this.getFormHM().put("email",email);
			   this.getFormHM().put("phone",phone);
			   this.getFormHM().put("fieldcond",fieldcond);*/
		     /**查询权限范围内的卡片*/
		    searchcardlist();
		    /**拼音简码指标查询*/
		    searchpinyinfield();
		    /**从参数定义中取得已定义的卡片*/
		    searchdefined();
		}catch(Exception e)
		{
			//e.printStackTrace();
		}
		   
		
	}
    public void getSynthetizeList(String syn_bound)
    {
    	ArrayList list=new ArrayList();
    	CommonData data=new CommonData();
    	data.setDataValue("all");
		data.setDataName("全部");
		list.add(data);
		data=new CommonData();
    	data.setDataValue("b0110");
		data.setDataName("单位");
		list.add(data);
		data=new CommonData();
    	data.setDataValue("e01a1");
		data.setDataName("岗位");
		list.add(data);
		data=new CommonData();
    	data.setDataValue("a0100");
		data.setDataName("人员");
		list.add(data);
		this.getFormHM().put("syn_list",list);
    }
    /**
     * 得到常用统计
     *
     */
    public void getCondStatlist()
    {
    	StringBuffer strsql=new StringBuffer();
        strsql.append("select id,name,type from lexpr where type='");//
        strsql.append("1");
        strsql.append("' order by id");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList list =new ArrayList();
        try
        {
           
           /**常用查询条件列表*/
            this.frowset=dao.search(strsql.toString());
            
            while(this.frowset.next())
            {
                if(!(this.userView.isHaveResource(IResourceConstant.LEXPR,this.frowset.getString("id"))))
                	continue;
                CommonData da=new CommonData();
                da.setDataValue(this.getFrowset().getString("id"));
                da.setDataName(this.getFrowset().getString("name"));
                list.add(da);
            }
        }
        catch(SQLException sqle)
        {
  	      sqle.printStackTrace();	                
        }
        finally
        {
            this.getFormHM().put("statlist",list);
        }
    }
    
    /**
     * 得到常用花名册列表
     *
     */
    public void getCommonRoster(){
    	ArrayList rosterlist = new ArrayList();
    	
    	String temp=this.userView.getResourceString(4);
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	try
        {
	    	if(temp==null||temp.length()==0){
	    		StringBuffer sqlstr = new StringBuffer();
			    sqlstr.append("SELECT tabid,Hzname from lname where Flag='1' order by  moduleFlag");
			    this.frowset=dao.search(sqlstr.toString());
			    while(this.frowset.next()){
			    	 CommonData data = new CommonData();
			    	 data.setDataName(this.frowset.getString("Hzname"));
			    	 data.setDataValue(this.frowset.getString("tabid"));
			    	 rosterlist.add(data);
			    }
	    	}
	    	else{
			    StringBuffer sqlstr = new StringBuffer();
			    sqlstr.append("SELECT tabid,Hzname from lname where Flag='1'");
			    if(!(this.userView.isAdmin()&& "1".equals(this.userView.getGroupId()))){
			    	sqlstr.append(" and tabid in (");   
					sqlstr.append(temp); 
					sqlstr.append(") order by  moduleFlag");
				}
			    this.frowset=dao.search(sqlstr.toString());
			    while(this.frowset.next()){
			    	 CommonData data = new CommonData();
			    	 data.setDataName(this.frowset.getString("Hzname"));
			    	 data.setDataValue(this.frowset.getString("tabid"));
			    	 rosterlist.add(data);
			    }
	    	}
        }catch(SQLException sqle){
    	      sqle.printStackTrace();	                
        }finally{
        	this.getFormHM().put("common_rosterlist",rosterlist);
        }
    }
    /**
     * 得到预警的list
     *
     */
    private void getEmailtemplateList()
    {
    	 ArrayList warnlist=new ArrayList();
    	 //加上order by norder，与预警设置界面数据显示顺序一致
    	 String sql="select wid,wName from hrpwarn  where valid<>0 order by norder";
    	 ContentDAO dao=new ContentDAO(this.getFrameconn());    	 
    	 try
    	 {
    		 CommonData da=new CommonData();
    		 /*da.setDataName("请选择");
    		 da.setDataValue("#");
    		 warnlist.add(da);*/
    		 this.frowset=dao.search(sql);
    		 while(this.frowset.next())    			 
    		 {
    			 da=new CommonData();
        		 da.setDataName(this.frowset.getString("wName"));
        		 da.setDataValue(this.frowset.getString("wid"));
        		 warnlist.add(da);
    		 }
    	 }catch(Exception e)
    	 {
    		 e.printStackTrace();
    	 }
         this.getFormHM().put("warnlist", warnlist);
    }
    
    public ArrayList getNbaseList(ArrayList list)
	 {
	     ArrayList kq_list=new ArrayList();
        if(list==null||list.size()<=0)
        	return kq_list;
        StringBuffer buf=new StringBuffer();
        buf.append("(");
        for(int i=0;i<list.size();i++)
        {
        	buf.append(" Upper(pre)='"+list.get(i).toString().toUpperCase()+"'");
        	if(i!=list.size()-1)
        		buf.append(" or ");
        }
        buf.append(")");
        StringBuffer sql=new StringBuffer();
        sql.append("select dbname,pre from dbname where 1=1 and ");
        if(buf!=null&&buf.toString().length()>0)
            sql.append(buf.toString());
        //黑名单人员库显示顺序不正确，添加排序  jingq add 2014.10.17
        sql.append(" order by dbid asc");
        ContentDAO dao=new ContentDAO(this.frameconn);
        RowSet rs=null;
        try
        {
       	 rs=dao.search(sql.toString());
       	 CommonData da=new CommonData();   		 
       	 while(rs.next())
       	 {
       		 da=new CommonData();
       		 da.setDataName(rs.getString("dbname"));
       		 da.setDataValue(rs.getString("pre"));
       		 kq_list.add(da);
       	 }
        }catch(Exception e)
        {
       	 e.printStackTrace();
        }finally
			{
				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
        return kq_list;
	 }
}
