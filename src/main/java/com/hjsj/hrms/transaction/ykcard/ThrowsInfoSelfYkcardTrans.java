package com.hjsj.hrms.transaction.ykcard;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.interfaces.sys.IResourceConstant;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import com.ibm.icu.text.SimpleDateFormat;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 
 * <p>Title: ThrowsInfoSelfYkcardTrans </p>
 * <p>Description: 显示薪酬表</p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2005-7-11</p>
 * @author 
 * @version 1.0
 */
public class ThrowsInfoSelfYkcardTrans extends IBusiness {

	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String mobileFlag=(String)hm.get("mobileFlag");
		String flag=(String)hm.get("flag");
		if("true".equals(mobileFlag)) {
			this.getFormHM().put("yearlist", getLastSalaryDate(this.userView.getA0100(),this.userView.getDbname()));
		}else {
			
			String a0100=(String)hm.get("a0100");
			String pre=(String)hm.get("pre");	
			String userbase=(String)hm.get("userbase");
			String tabid=(String)this.getFormHM().get("tabid");
		    String fromModule=(String)hm.get("fromModule");//我的薪酬单个登记表菜单定制
		    String zp_flag=(String)hm.get("zp_noticetemplate_flag");//招聘调用登记表 权限全部放开
		   
			if(tabid==null||tabid.length()<=0)
				tabid="";
			
			String b0110=(String)hm.get("b0110");
			if(StringUtils.isNotBlank(zp_flag)){//招聘调用不需要权限
			    UserView userView=new UserView("su",this.frameconn);
			    try {
			    	userView.canLogin(true);
				} catch (Exception e) {
					e.printStackTrace();
				}

				tabid=(String)hm.get("tabid");
			}else{
				if("infoself".equals(flag)){//notself ios查看我的薪酬标记
					a0100=userView.getA0100();
					pre=userView.getDbname();
					userbase=userView.getDbname();
					b0110=userView.getUserOrgId();
					boolean isMobile = "1".equals(hm.get("isMobile"));
					if(!("statCount".equals(fromModule)||"myInfo".equals(fromModule)))//登记表统计方式链接和我的信息登记表不需要校验是否配置登记表，我的薪酬单独登记表配置参数
						if(!isSalaryCard(tabid, isMobile))
							throw new GeneralException("",ResourceFactory.getProperty("error.function.nopriv"),"","");
					    
				}else{
					CheckPrivSafeBo checkPrivSafeBo=new CheckPrivSafeBo(this.frameconn,this.userView);
					b0110=checkPrivSafeBo.checkOrg(b0110, "");
					pre=checkPrivSafeBo.checkDb(pre);
					userbase=checkPrivSafeBo.checkDb(userbase);
					a0100=checkPrivSafeBo.checkA0100(b0110, pre, a0100, "");
				}
			}
		    if(StringUtils.isNotBlank(fromModule)){//
		    	String inforkind=(String)hm.get("inforkind");
		    	a0100=this.userView.getA0100();
		    	tabid=(String)hm.get("tabid");
		    	if(!this.userView.isHaveResource(IResourceConstant.CARD,tabid))
		    		throw GeneralExceptionHandler.Handle(new Exception("无此登记表权限，请授权后再次查看！"));
		    	this.getFormHM().put("nbase", this.userView.getDbname());
		    	if("statCount".equals(fromModule)){//登记表显示统计方式 时间默认当前时间。导出pdf excel按钮
		    		this.getFormHM().put("cardtype", "statCount");
		    	}else if("myInfo".equals(fromModule)){//个人信息登记表显示，只显示导出按钮
		    		this.getFormHM().put("cardtype", "myInfo");
		    	}else if("salaryCard".equals(fromModule)){//我的薪酬登记表显示不显示 登记表下拉列表
		    		this.getFormHM().put("cardtype", "salaryCard");
		    	}else
		    		this.getFormHM().put("cardtype", "");
		    	hm.remove("fromModule");
		    	this.getFormHM().put("inforkind", inforkind);
		    }else{
		    	if(StringUtils.isNotBlank(zp_flag))
		    		this.getFormHM().put("cardtype", "zp_noticetemplate_flag");
		    	else	
		    		this.getFormHM().put("cardtype", "SS_SETCARD");
		    }
			this.getFormHM().put("firstFlag", "1");//我的薪酬员工薪酬第一次进入标记
			this.getFormHM().put("b0110",b0110);
			this.getFormHM().put("a0100",a0100);
	    	this.getFormHM().put("pre",pre);
	    	this.getFormHM().put("userbase",userbase);
	    	this.getFormHM().put("tabid",tabid);
		}
		
    	
	}
	/***
	 * 我的薪酬 微信 钉钉 查看年月按照设置过滤
	 * @param a0100
	 * @param nbase
	 * @return yearlist 倒序
	 */
	private ArrayList<String> getLastSalaryDate(String a0100,String nbase) {
		ArrayList<String> yearlist=new ArrayList<String>();
		ContentDAO dao=new ContentDAO(this.frameconn);
		RowSet rs=null;
		try {
			CardConstantSet cardConstantSet=new CardConstantSet(this.userView,this.frameconn);	
			String relating=cardConstantSet.getSearchRelating(dao);		
			String b0110=cardConstantSet.getRelatingValue(dao,this.userView.getA0100(),this.userView.getDbname(),relating,this.userView.getUserOrgId());				
			XmlParameter xml=new XmlParameter("UN",b0110,"00");
			xml.ReadOutParameterXml("SS_SETCARD",this.frameconn,"all");				
			String year_restrict=xml.getYear_restrict();//不显示XXXX年以后数据，不选择 为空
			
			StringBuffer sql=new StringBuffer();
			sql.append("select "+Sql_switcher.dateToChar("max(a00z0)")+" a00z0 from (");
          	sql.append("select max(a00z0) as a00z0 from SalaryHistory ");
          	if(a0100!=null&&a0100.length()>0)
          	{
          		sql.append("where a0100='"+a0100+"' and lower(nbase)='"+nbase.toLowerCase()+"' and sp_flag='06' ");
          	}
          	sql.append(" union select max(a00z0) as a00z0 from salaryarchive ");
          	if(a0100!=null&&a0100.length()>0)
          	{
          		sql.append("where a0100='"+a0100+"' and lower(nbase)='"+nbase.toLowerCase()+"' and sp_flag='06' ");
          	}
          	sql.append(")T ");
          	rs=dao.search(sql.toString());
          	String lastdate="";
          	String date="";
          	Calendar cal=Calendar.getInstance();
          	if(rs.next()) {
          		date=rs.getString("a00z0");
          	}
          	int year=2000;
      		if(StringUtils.isNotEmpty(date)) {
      			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
      			cal.setTime(sdf.parse(date));
      			lastdate=cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)>9?cal.get(Calendar.MONTH):"0"+cal.get(Calendar.MONTH));
      			year=cal.get(Calendar.YEAR);
      		}else {
      			lastdate=cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)>9?cal.get(Calendar.MONTH):"0"+cal.get(Calendar.MONTH));
      			year=cal.get(Calendar.YEAR);
      		}
      		yearlist.add(lastdate);
      		if(StringUtils.isNotEmpty(year_restrict)&&year>Integer.parseInt(year_restrict)) {
      			for(int i=year-1;i>=year-10;i--) {
      				if(Integer.parseInt(year_restrict)<=i) {
      						yearlist.add(i+"-01");
      				}
      			}
      		}
          	
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return yearlist;
	}

	private boolean isSalaryCard(String tabid, boolean isMobile) {
		boolean found = false;
		if (tabid == null || "".equals(tabid))
			return true;
		try {
			CardConstantSet cardConstantSet = new CardConstantSet(this.userView, this.frameconn);
			ContentDAO dao = new ContentDAO(this.frameconn);
			String relating = cardConstantSet.getSearchRelating(dao);
			String b0110 = cardConstantSet.getRelatingValue(dao, this.userView.getA0100(), 
					this.userView.getDbname(), relating, this.userView.getUserOrgId());
			XmlParameter xml = new XmlParameter("UN", b0110, "00");
			xml.ReadOutParameterXml("SS_SETCARD", this.frameconn, "all");

			String flag = xml.getFlag();
			ArrayList cardidlist = cardConstantSet.setCardidSelectSelfinfo( this.frameconn, this.userView, flag,
					this.userView.getDbname(), this.userView.getA0100(), b0110, xml, isMobile);
			if (cardidlist != null && cardidlist.size() > 0) {
				for (int i = 0; i < cardidlist.size(); i++) {
					CommonData dataobj = (CommonData) cardidlist.get(i);
					String cardid = dataobj.getDataValue();
					if (cardid.equals(tabid)) {
						found = true;
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return found;
	}
}
