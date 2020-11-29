package com.hjsj.hrms.module.card.transaction;

import com.hjsj.hrms.module.card.businessobject.CreateCardHtmlBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import java.util.Calendar;
/***
 * 登记表页面html元素拼接
 * @author Administrator
 *
 */
public class CreateCardHtmlTrans extends IBusiness{
/***
 * istype 此参数原用于后台出错后界面显示错误信息标志不同模块 可用inforkind替代 0表示薪酬表。2表示机构1表示职位表3登记表
 * 
 * 
 * fieldpurv	登记表指标权限控制    1 不控制    默认控制
		a0100	库前缀+`+人员id号(加上“`”用于区分是人员id还是组织机构代码)
		bizDate	业务日期；使用场景：模板指标条件定位 不为空时按照业务日期查询条件
		tabid	登记表号 可以多个以逗号分隔，2,3,4
		inforkind	调用模块1：人员，2：单位，4：岗位；5：绩效；6：岗位说明书7：代表薪酬 8：领导桌面 9：招聘
		plan_id	绩效（非必填，绩效使用时必填）
 * */
	@Override
	public void execute() throws GeneralException {
		//String a0100=(String)this.getFormHM().get("a0100");//人员/单位/岗位/
		String inforkind=(String)this.getFormHM().get("inforkind");//模块类别
		String nid=(String)this.getFormHM().get("nid");//id
		String nbase=(String)this.getFormHM().get("nbase");//人员库
		String pageid=(String)this.getFormHM().get("pageid");
		String tabid=(String)this.getFormHM().get("tabid");
		String queryflag=(String)this.getFormHM().get("queryflag");          /*0代表安条件查询1代表安月时间查询2代表安时间段查询3.安时间季度查询*/
		queryflag=StringUtils.isEmpty(queryflag)?"0":queryflag;
		String fieldpurv=(String)this.getFormHM().get("fieldpurv");
		String bizDate=(String)this.getFormHM().get("bizDate");
		String plan_id=(String)this.getFormHM().get("plan_id");
		
		//---日期参数start
		String year=(String)this.getFormHM().get("year");//年
		year=StringUtils.isEmpty(year)?(Calendar.getInstance().get(Calendar.YEAR)+""):year;
		String month=(String)this.getFormHM().get("month");//月
		month=StringUtils.isEmpty(month)?(Calendar.getInstance().get(Calendar.MONTH)+1+""):month;
		String ctimes=(String)this.getFormHM().get("ctimes");//次数
		ctimes=StringUtils.isEmpty(ctimes)?("13"):ctimes;
		String season=(String)this.getFormHM().get("season");//季度
		season=StringUtils.isEmpty(season)?("1"):season;
		String startDate=(String)this.getFormHM().get("startDate");//开始日期
		startDate=StringUtils.isEmpty(startDate)?(Calendar.getInstance().get(Calendar.YEAR)+"-"+(Calendar.getInstance().get(Calendar.MONTH)+1)+"-"+Calendar.getInstance().get(Calendar.DATE)):startDate;
		String endDate=(String)this.getFormHM().get("endDate");//结束日期
		endDate=StringUtils.isEmpty(endDate)?(Calendar.getInstance().get(Calendar.YEAR)+"-"+(Calendar.getInstance().get(Calendar.MONTH)+1)+"-"+Calendar.getInstance().get(Calendar.DATE)):endDate;
		String browser=(String)this.getFormHM().get("browser");
		//---日期参数 end
		String isMobile=(String)this.getFormHM().get("isMobile");
		
		String cardtype=""; 
		/*if(StringUtils.isNotEmpty(a0100)) {
			if(a0100.indexOf("`")>-1) {//以Usr`A0100格式参数
				nbase=a0100.split("`")[0];
				nid=a0100.split("`")[1];
			}else {
				nid=a0100;
			}
		}*/
		String zp_flag=(String)this.getFormHM().get("zp_flag");
		
		try {
			if(StringUtils.isNotEmpty(zp_flag)) {
				if(this.userView==null) {
					if("zp_noticetemplate_flag".equals(PubFunc.decrypt(zp_flag))) {
						this.userView=new UserView("su", this.frameconn);
						this.userView.canLogin(true);
						cardtype="zp_noticetemplate_flag";
					}else {
						return;
					}
					
				}else {
					if("zp_noticetemplate_flag".equals(PubFunc.decrypt(zp_flag))) {
						this.userView=new UserView("su", this.frameconn);
						this.userView.canLogin(true);
						cardtype="zp_noticetemplate_flag";
					}
				}
			}else {
				if(this.userView==null) {
					return;
				}
			}
			
			CreateCardHtmlBo bo=new CreateCardHtmlBo(this.frameconn, this.userView);
			bo.setBizDate(bizDate);
			bo.setInforkind(inforkind);
			bo.setNid(nid);
			bo.setNbase(nbase);
			bo.setPageid(Integer.parseInt(pageid));
			bo.setTabid(Integer.parseInt(tabid));
			bo.setQueryflag(Integer.parseInt(queryflag));
			bo.setFieldpurv(fieldpurv);
			bo.setPlan_id(plan_id);
			bo.setYear(Integer.parseInt(year));
			bo.setMonth(Integer.parseInt(month));
			bo.setCtimes(Integer.parseInt(ctimes));
			bo.setSeason(Integer.parseInt(season));
			bo.setStartDate(startDate);
			bo.setEndDate(endDate);
			bo.setIsMobile(isMobile);
			bo.setCardtype(cardtype);
			if(StringUtils.isNotEmpty(browser))
				bo.setBrowser(browser);
			String html=bo.getCardHtml().toString();
			this.getFormHM().put("autoSize", "1".equals(bo.getYkcard_auto())?true:false);
			this.getFormHM().put("cardHtml", html);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
