package com.hjsj.hrms.module.gz.gzspcollect.transaction;

import com.hjsj.hrms.module.gz.gzspcollect.businessobject.GzSpCollectBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * 
 * 项目名称：hcm7.x
 * 类名称：GzSpCollectAppralTrans 
 * 类描述： 薪资审批汇总报批、驳回、批准
 * 创建人：zhaoxg
 * 创建时间：Dec 23, 2015 4:54:56 PM
 * 修改人：zhaoxg
 * 修改时间：Dec 23, 2015 4:54:56 PM
 * 修改备注： 
 * @version
 */
public class GzSpCollectAppralTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		RowSet rs = null;
		try{
			String appealObject=(String)this.getFormHM().get("appealObject");//报批给appealObject
			appealObject = PubFunc.decrypt(SafeCode.decode(appealObject));
			String salaryid=(String)this.getFormHM().get("salaryid");//薪资类别号
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		    String accountingdate = (String)this.getFormHM().get("appdate"); //业务日期
		    accountingdate = PubFunc.decrypt(SafeCode.decode(accountingdate));
		    String accountingcount = (String)this.getFormHM().get("count"); //次数
		    accountingcount = PubFunc.decrypt(SafeCode.decode(accountingcount));
		    String opt = (String) this.getFormHM().get("opt");//appeal:报批  reject:驳回  confirm:批准   confirmAll:批准
		    String rejectCause = (String) this.getFormHM().get("rejectCause");//驳回原因
		    String sendMen = (String) this.getFormHM().get("sendMen");//批准后抄送人 设置了通知才生效
			//是否来自待办 0 否 1是。
		    String fromPending = this.getFormHM().containsKey("fromPending")?(String) this.getFormHM().get("fromPending"):"0";
		    if(sendMen==null)
		    	sendMen="";
		    if(sendMen.indexOf(",")!=-1){
		    	String[] _sendMen = sendMen.split(",");
		    	StringBuffer str = new StringBuffer();
		    	for(int i=0;i<_sendMen.length;i++){
		    		str.append(PubFunc.decrypt(SafeCode.decode(_sendMen[i])));
		    		str.append(",");
		    	}
		    	sendMen = str.toString();
		    }else{
		    	sendMen = PubFunc.decrypt(SafeCode.decode(sendMen));
		    }
			SalaryAccountBo bo = new SalaryAccountBo(this.frameconn,this.userView,Integer.parseInt(salaryid));
			SalaryTemplateBo gzbo=bo.getSalaryTemplateBo();
			
		    StringBuffer filtersql = new StringBuffer();//前台过滤条件\
	    	String collectPoint = (String) this.getFormHM().get("collectPoint");//汇总指标
	    	String selectID = (String) this.getFormHM().get("selectID");//选择的记录
	    	String cound = (String) this.getFormHM().get("cound");//人员筛选
	    	GzSpCollectBo spbo = new GzSpCollectBo(this.userView,this.frameconn);
	    	filtersql.append(spbo.getCollectSPPriv(gzbo, selectID,cound, collectPoint,salaryid,accountingdate,accountingcount));
	    	
	    	ContentDAO dao = new ContentDAO(this.frameconn);
	    	rs = dao.search("select count(salaryid) as num from salaryhistory where 1=1 "+filtersql);
	    	if(rs.next()){
	    		int t = rs.getInt("num");
	    		if(t==0){
	    			throw GeneralExceptionHandler.Handle(new Exception("请选择可操作的数据！"));
	    		}
	    	}
	    	
		    accountingdate = accountingdate.replaceAll("\\.", "-");
		    String[] temps=accountingdate.split("-");
		    LazyDynaBean busiDate = new LazyDynaBean();
		    busiDate.set("year", temps[0]);
		    busiDate.set("month", temps[1]);
		    busiDate.set("day", temps[2]);
		    busiDate.set("count", accountingcount);
		    bo.gzSp(appealObject, opt, rejectCause, sendMen, filtersql.toString(), busiDate);
		    //只有驳回的时候查找出所有的数量，前台根据数量进行判断，是否清空业务日期和发起人
		    if("reject".equals(opt)) {
		    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		    	Date date = new Date(df.parse(accountingdate).getTime());
			    String sql = "select count(1) as count from SalaryHistory where salaryid =? and A00Z0 =? and ((((AppUser is null  "+gzbo.getWhlByUnits("salaryhistory", true)+" ) or AppUser Like '%;" + this.userView.getUserName() + ";%' )) or curr_user='" + this.userView.getUserName() + "')";
			    ArrayList list = new ArrayList();
			    list.add(salaryid);
			    list.add(date);
			    rs = dao.search(sql,list);
			    int count = 0;
			    while(rs.next()) {
			    	count = rs.getInt("count");
			    }
			    this.getFormHM().put("count", count);
		    }
		    //如果从待办进入，则查询剩余可审批的数据条数
		    if("1".equals(fromPending)){
		    	int pengdingNum=spbo.getRemainderNumber("salaryhistory",salaryid,accountingdate,accountingcount);
		    	this.getFormHM().put("lastNumber",pengdingNum);
			}


		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeResource(rs);
		}
	}
}
