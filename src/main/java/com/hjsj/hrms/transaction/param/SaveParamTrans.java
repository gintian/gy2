/*
 * Created on 2005-10-24
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
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;




import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveParamTrans extends IBusiness {
	/**
	 * 保存登记表已定义的信息
	 * @throws GeneralException
	 */
	private void savecardinfo()throws GeneralException 
	{
		try
		{
			//为所有可能为#的字段做还原处理  jingq add 2014.09.20
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String emp_card_id=(String)this.getFormHM().get("emp_card_id");
			emp_card_id = PubFunc.keyWord_reback(emp_card_id);
			String org_card_id=(String)this.getFormHM().get("org_card_id");
			String pos_card_id=(String)this.getFormHM().get("pos_card_id");
			String rownums=(String) this.getFormHM().get("rownums");
			//日志级别设置
			String loglevel=(String) this.getFormHM().get("loglevel");

			String org_browse_format = (String)this.getFormHM().get("org_browse_format");
			String display_e0122 = (String)this.getFormHM().get("display_e0122")==null?"":(String)this.getFormHM().get("display_e0122").toString().trim();
			if(display_e0122.length()==0)
				display_e0122 = "0";
			String seprartor=(String)this.getFormHM().get("seprartor");
			String num_per_page=(String)this.getFormHM().get("num_per_page");
			String approveflag=(String)this.getFormHM().get("approveflag");
			String link_p_width=(String)this.getFormHM().get("link_p_width");
			String link_p_height=(String)this.getFormHM().get("link_p_height");
			String lawrule_file_days=(String)this.getFormHM().get("lawrule_file_days");
			String announce_days = (String)this.getFormHM().get("announce_days");
			String stat_id=(String)this.getFormHM().get("stat_id");
			stat_id = PubFunc.keyWord_reback(stat_id);
			String ykcard_auto=(String)this.getFormHM().get("ykcard_auto");
			String firstdays=(String)this.getFormHM().get("firstdays");
			firstdays=firstdays!=null&&firstdays.trim().length()>0?firstdays:"5";
			String lastdays=(String)this.getFormHM().get("lastdays");
			lastdays=lastdays!=null&&lastdays.trim().length()>0?lastdays:"5";
			String photo_h=(String)this.getFormHM().get("photo_h");
			photo_h=photo_h!=null&&photo_h.trim().length()>0?photo_h:"120";
			String photo_w=(String)this.getFormHM().get("photo_w");
			photo_w=photo_w!=null&&photo_w.trim().length()>0?photo_w:"85";
			//------------报表上报是否支持审批  zhaoxg 2013-1-25 ----------------------------
			String isApprove = (String) this.getFormHM().get("isApprove");
			String relation_id = (String) this.getFormHM().get("relation_id");
			relation_id = PubFunc.keyWord_reback(relation_id);
			String sql = "";
			if("true".equals(isApprove)&& "00".equals(relation_id)){
						sql = "update treport_ctrl set description=null,status=0,appuser = null,currappuser = null,username=null where appuser is not null";
						dao.update(sql);			
			}else if("false".equals(isApprove)){
						sql = "update treport_ctrl set description=null,status=0,appuser = null,currappuser = null,username=null where appuser is not null";
						dao.update(sql);
			}
			
			//----------------------------------------------------------------------------
			
			String subunitup=(String)this.getFormHM().get("subunitup");
			subunitup=subunitup!=null&&subunitup.trim().length()>0?subunitup:"true";
			String updisk=(String)this.getFormHM().get("updisk");
			updisk=updisk!=null&&updisk.trim().length()>0?updisk:"true";
			String editupdisk=(String)this.getFormHM().get("editupdisk");
			editupdisk=editupdisk!=null&&editupdisk.trim().length()>0?editupdisk:"true";
			String inputchinfor=(String)this.getFormHM().get("inputchinfor");//如果1：不直接入库；0为直接入库
			inputchinfor = "1";  //信息审核默认不直接入库 zgd 2014-2-13
			inputchinfor=inputchinfor!=null&&inputchinfor.trim().length()>0?inputchinfor:"1";
			String condisk=(String)this.getFormHM().get("condisk");
			condisk=condisk!=null&&condisk.trim().length()>0?condisk:"0";
			String units=(String)this.getFormHM().get("units");
			units=units!=null&&units.trim().length()>0?units:"0";
			String place=(String)this.getFormHM().get("place");
			place=place!=null&&place.trim().length()>0?place:"0";
			String photo_other_itemid=(String)this.getFormHM().get("photo_other_itemid");
			String photo_other_view=(String)this.getFormHM().get("photo_other_view");
			if(photo_other_view==null||photo_other_view.length()<=0)
				photo_other_itemid="";
            if(num_per_page==null||num_per_page.length()<=0)
            	num_per_page="10";
			Pattern pattern=Pattern.compile("\\d*");
			Matcher matcher=pattern.matcher(num_per_page);
			String browse_search_state=(String)this.getFormHM().get("browse_search_state");//员工信息浏览查询项 0：隐藏，1：显示
			browse_search_state=browse_search_state!=null&&browse_search_state.length()>0?browse_search_state:"0";
			boolean b=matcher.matches();
			if(!b){
				return;
//				throw GeneralExceptionHandler.Handle(new GeneralException("","<信息浏览每页显示纪录数>请填写数字！","",""));
			}
			String blacklist_field=(String)this.getFormHM().get("blacklist_field");//黑名单人员指标
			blacklist_field = PubFunc.keyWord_reback(blacklist_field);
			String dairyinfolimit=(String)this.getFormHM().get("dairyinfolimit");
			String limit_HH=(String)this.getFormHM().get("limit_HH");
			String limit_MM=(String)this.getFormHM().get("limit_MM");
			if(limit_HH!=null&&limit_HH.length()>0&&(limit_MM==null||limit_MM.length()<=0))
			{
				limit_MM="00";
			}else if(limit_HH==null||limit_HH.length()<=0||limit_MM==null||limit_MM.length()<=0)
				dairyinfolimit="";
			blacklist_field=blacklist_field!=null&&!"".equals(blacklist_field)&&!"#".equals(blacklist_field)?blacklist_field:"";
			String blacklist_per=(String)this.getFormHM().get("blacklist_per");//黑名单人员库
			blacklist_per = PubFunc.keyWord_reback(blacklist_per);
			blacklist_per=blacklist_per!=null&&!"".equals(blacklist_per)&&!"#".equals(blacklist_per)?blacklist_per:"";
            String syn_bound=(String)this.getFormHM().get("syn_bound");
            String org_root_caption=(String)this.getFormHM().get("org_root_caption");            
            String welcome_marquee=(String)this.getFormHM().get("welcome_marquee");
            String browse_photo=(String)this.getFormHM().get("browse_photo");
            String infosort_browse = (String)this.getFormHM().get("infosort_browse");
            String pinyin_field = (String)this.getFormHM().get("pinyin_field");
            pinyin_field = PubFunc.keyWord_reback(pinyin_field);
            String common_roster = (String)this.getFormHM().get("common_roster");
            common_roster = PubFunc.keyWord_reback(common_roster);
            String birthday_wid=(String)this.getFormHM().get("birthday_wid");
            birthday_wid = PubFunc.keyWord_reback(birthday_wid);
            String complex_id = (String)this.getFormHM().get("complex_id");
            complex_id = PubFunc.keyWord_reback(complex_id);
            birthday_wid=birthday_wid!=null&&!"#".equals(birthday_wid)?birthday_wid:"";
            ConstantXml constantXml = new ConstantXml(this.getFrameconn(),"SYS_OTH_PARAM","param");
            constantXml.setTextValue(Sys_Oth_Parameter.COMPLEX_ID,complex_id);
            constantXml.saveStrValue();
			Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
			sysoth.setValue(Sys_Oth_Parameter.BLACKLIST,"field",blacklist_field);
			sysoth.setValue(Sys_Oth_Parameter.BLACKLIST,"base",blacklist_per);
			sysoth.setValue(Sys_Oth_Parameter.BOROWSE_CARD,"emp",emp_card_id);
			sysoth.setValue(Sys_Oth_Parameter.BOROWSE_CARD,"org",org_card_id);
			sysoth.setValue(Sys_Oth_Parameter.BOROWSE_CARD,"pos",pos_card_id);
			sysoth.setValue(Sys_Oth_Parameter.EDIT_COLUMNS,rownums);
			sysoth.setValue(Sys_Oth_Parameter.ORG_BROWSE_FORMAT,org_browse_format);
			sysoth.setValue(Sys_Oth_Parameter.DISPLAY_E0122,display_e0122);
            sysoth.setValue(Sys_Oth_Parameter.NUM_PER_PAGE,num_per_page);
			sysoth.setValue(Sys_Oth_Parameter.APPROVE_FLAG,approveflag);
			sysoth.setValue(Sys_Oth_Parameter.WELCOME_MARQUEE,welcome_marquee);
			sysoth.setValue(Sys_Oth_Parameter.SYNTHETIZE_BOUND,syn_bound);
			sysoth.setValue(Sys_Oth_Parameter.LIKN_P_HEIGHT,link_p_height);
			sysoth.setValue(Sys_Oth_Parameter.LIKN_P_WIDTH,link_p_width);
			sysoth.setValue(Sys_Oth_Parameter.ORG_ROOT_DESC,org_root_caption!=null&&org_root_caption.length()>0?org_root_caption:"");
			sysoth.setValue(Sys_Oth_Parameter.LAWRULE_FILE_DAYS,lawrule_file_days!=null&&lawrule_file_days.length()>0?lawrule_file_days:"5");
			sysoth.setValue(Sys_Oth_Parameter.ANNOUNCE_DAYS, announce_days!=null&&announce_days.length()>0?announce_days:"3");
			sysoth.setValue(Sys_Oth_Parameter.DIARY,"firstdays",firstdays);
			sysoth.setValue(Sys_Oth_Parameter.DIARY,"lastdays",lastdays);
			sysoth.setValue(Sys_Oth_Parameter.PHOTO,"photo_w",photo_w);
			sysoth.setValue(Sys_Oth_Parameter.PHOTO,"photo_h",photo_h);
			sysoth.setValue(Sys_Oth_Parameter.SUBUNITUP,subunitup);
			sysoth.setValue(Sys_Oth_Parameter.UPDISK,updisk);
			sysoth.setValue(Sys_Oth_Parameter.EDITUPDISK,editupdisk);
			sysoth.setValue(Sys_Oth_Parameter.INPUTCHINFOR,inputchinfor);
			sysoth.setValue(Sys_Oth_Parameter.CONDISK,condisk);
			sysoth.setValue(Sys_Oth_Parameter.BROWSE_PHOTO, browse_photo!=null&&browse_photo.length()>0?browse_photo:"0");
			sysoth.setValue(Sys_Oth_Parameter.INFOSORT_BROWSE,infosort_browse);
			sysoth.setValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH,pinyin_field.toUpperCase());
			sysoth.setValue(Sys_Oth_Parameter.COMMON_ROSTER,common_roster);
			sysoth.setValue(Sys_Oth_Parameter.BIRTHDAY_WID,birthday_wid);
			sysoth.setValue(Sys_Oth_Parameter.UNITS,units);
			sysoth.setValue(Sys_Oth_Parameter.PLACE,place);
			setPrivFieldFillable(Sys_Oth_Parameter.UNITS,units);
			setPrivFieldFillable(Sys_Oth_Parameter.PLACE,place);
			sysoth.setValue(Sys_Oth_Parameter.PHOTO_OTHER_VIEW,photo_other_itemid);
			//设置日志级别
			updateLevel(loglevel);
			sysoth.setValue(Sys_Oth_Parameter.LOGLEVEL,loglevel);
			//系统管理其他参数，部门层级间隔符  jingq add 2014.09.20
			seprartor = PubFunc.keyWord_reback(seprartor);
			sysoth.setValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep", seprartor);
			sysoth.setValue(Sys_Oth_Parameter.BROWSE_SEARCH_STATE, browse_search_state);
			
			sysoth.setValue(Sys_Oth_Parameter.ISAPPROVE,isApprove);
			sysoth.setValue(Sys_Oth_Parameter.APPROVEID,relation_id);
			//			sysoth.setRownums(rownums);
			if(stat_id==null||stat_id.length()<=0|| "#".equals(stat_id))
				stat_id="";
			sysoth.setValue(Sys_Oth_Parameter.STAT_ID,stat_id);
			if(ykcard_auto==null||ykcard_auto.length()<=0)
				ykcard_auto="0";
			sysoth.setValue(Sys_Oth_Parameter.YKCARD_AUTO,ykcard_auto);
			String gquery_cond=(String)this.getFormHM().get("gquery_cond");//人员分类条件
			if(gquery_cond==null||gquery_cond.length()<=0)
				gquery_cond="";
			sysoth.setValue(Sys_Oth_Parameter.GQUERY_COND,"1");//类型
			sysoth.setValue(Sys_Oth_Parameter.GQUERY_COND,"value",gquery_cond);//值
			
			sysoth.setValue(Sys_Oth_Parameter.DAIRYINFOLIMIT,dairyinfolimit);//日志限制
			sysoth.setValue(Sys_Oth_Parameter.DAIRYINFOLIMIT,"limit_HH",limit_HH);//小时
			sysoth.setValue(Sys_Oth_Parameter.DAIRYINFOLIMIT,"limit_MM",limit_MM);//小时
			sysoth.saveParameter();
			AdminCode.dept_seq=seprartor;
			DyParameter.refresh(Sys_Oth_Parameter.PHOTO,"photo_w",this.frameconn);
			DyParameter.refresh(Sys_Oth_Parameter.PHOTO,"photo_h",this.frameconn);
			/***对Infom操作**/	
			Sys_Infom_Parameter sys_Infom_Parameter=new Sys_Infom_Parameter(this.getFrameconn(),"INFOM");
			String photo_maxsize=(String)this.getFormHM().get("photo_maxsize");
		   try{
		    if(Integer.parseInt(photo_maxsize)<=0){
		        photo_maxsize="-1";
		    }
		   }catch (Exception e) {
		       photo_maxsize="-1";
           }
			//photo_maxsize=photo_maxsize!=null&&photo_maxsize.length()>0?photo_maxsize:"-1";
			sys_Infom_Parameter.saveParamAttribute(Sys_Infom_Parameter.PHOTO,"MaxSize", photo_maxsize);	

			String multimedia_maxsize=(String)this.getFormHM().get("multimedia_maxsize");
		    try
		    {
                if(Integer.parseInt(multimedia_maxsize)<=0){
                    multimedia_maxsize="-1";
                }
            }catch (Exception e) {
                multimedia_maxsize="-1";
            }
			//multimedia_maxsize=multimedia_maxsize!=null&&multimedia_maxsize.length()>0?multimedia_maxsize:"-1";
			sys_Infom_Parameter.saveParamAttribute(Sys_Infom_Parameter.MULTIMEDIA,"MultimediaMaxSize", multimedia_maxsize);	
			sys_Infom_Parameter.saveParameter();
			
			//系统皮肤
			String themes = (String)this.getFormHM().get("themes");
			SysParamBo.setSysParamValue("THEMES", userView.getUserName(),themes);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	public void execute() throws GeneralException {
		
		try{
			/*String email=(String)this.getFormHM().get("email");
			String phone=(String)this.getFormHM().get("phone");
			ExecuteSQL exesql=new ExecuteSQL();
			String sql="delete from constant where constant='SS_EMAIL'";
			exesql.execUpdate(sql,this.getFrameconn());
			sql="insert into constant(constant,type,str_value,describe) values('SS_EMAIL','','" + email + "','Email指标')";
			exesql.execUpdate(sql,this.getFrameconn());
			RecordVo vo=new RecordVo("constant");
			vo.setString("constant","SS_EMAIL");
		    vo.setString("str_value",email);	    
		    ConstantParamter.putConstantVo(vo,"SS_EMAIL");
		    *//***电话**//*
			sql="delete from constant where constant='SS_MOBILE_PHONE'";
			exesql.execUpdate(sql,this.getFrameconn());
			sql="insert into constant(constant,type,str_value,describe) values('SS_MOBILE_PHONE','','" + phone + "','SS_MOBILE_PHONE指标')";
			exesql.execUpdate(sql,this.getFrameconn());
			vo=new RecordVo("constant");
			vo.setString("constant","SS_MOBILE_PHONE");
		    vo.setString("str_value",phone);
		    ConstantParamter.putConstantVo(vo,"SS_MOBILE_PHONE");*/
			
			  /**邮箱***/
		
		      /**保存卡片定义的信息*/
		      savecardinfo();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * 修改Logger的日志级别
	 * @param level
	 */
	public void updateLevel(String level){
		FileOutputStream out = null;
		try{
			if(StringUtils.isNotEmpty(level)){
				String path = Thread.currentThread().getContextClassLoader().getResource("/").getPath()+"log4j2.xml";
				//1.创建并读取一个Document对象
				Document doc=new SAXReader().read(new File(path));
				Element contatcElem = doc.getRootElement().element("Loggers").element("root");
				Attribute levelAttribute= contatcElem.attribute("level");
				levelAttribute.setValue(level);
				//1.创建输出流通道
				out=new FileOutputStream(path);
				OutputFormat format=OutputFormat.createPrettyPrint();//设置contact.xml文件格式（俗称：美观格式）
				format.setEncoding("UTF-8");//设置编码格式
				//2.创建写出的对象
				XMLWriter write=new XMLWriter(out,format);
				//3.写出对象
				write.write(doc);
				//4.关闭资源
				write.close();
/*				// 获取Logger对象，修改Logger对象的Level
				Logger logger = LogManager.getRootLogger();
				System.out.println("改变前："+logger.getLevel());
				logger.setLevel(Level.toLevel(level));
				System.out.println("改变后："+logger.getLevel());*/
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(out);
		}

	}


	/**
	 * 单位，职位是否必填对缓存的userView.getPrivFieldList("A01");进行操作
	 * @param param_type
	 * @param value
	 */
    private  void setPrivFieldFillable(int param_type,String value)
    {
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	if(param_type==Sys_Oth_Parameter.UNITS)
		{
			  ArrayList infoFieldList = userView.getPrivFieldList("A01"); 
			  if(infoFieldList==null||infoFieldList.size()<=0)
				  return;
			  for(int i=0;i<infoFieldList.size();i++)
			  {
				  FieldItem fieldItem = (FieldItem) infoFieldList.get(i);
				  if ("b0110".equalsIgnoreCase(fieldItem.getItemid())) {
				     if(value!=null&& "1".equals(value))
				     {
				    	 fieldItem.setFillable(true);
				     }else
				     {
				    	 fieldItem.setFillable(false);
				     }
				  }
			  }
			  privFieldFillableToDB("A01","b0100",value,dao);
			  
		}else if(param_type==Sys_Oth_Parameter.PLACE)
		{
			ArrayList infoFieldList = userView.getPrivFieldList("A01"); 
			  if(infoFieldList==null||infoFieldList.size()<=0)
				  return;
			  for(int i=0;i<infoFieldList.size();i++)
			  {
				  FieldItem fieldItem = (FieldItem) infoFieldList.get(i);
				  if ("e01a1".equalsIgnoreCase(fieldItem.getItemid())) {
				     if(value!=null&& "1".equals(value))
				     {
				    	 fieldItem.setFillable(true);
				     }else
				     {
				    	 fieldItem.setFillable(false);
				     }
				  }
			  }
			  privFieldFillableToDB("A01","e01a1",value,dao);
			 
		}    	
    }
    /**
     * 
     * @param setid
     * @param itemid
     * @param value
     * @param dao
     */
    public void privFieldFillableToDB(String setid,String itemid,String value,ContentDAO dao)
	{
		String sql="update fielditem set state='"+value+"' where Upper(fieldsetid)='"+setid.toUpperCase()+"'";
		sql=sql+" and Upper(itemid)='"+itemid+"'";
		try
		{
			dao.update(sql);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
