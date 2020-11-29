package com.hjsj.hrms.transaction.report.report_analyse;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.reportCollect.IntegrateTableBo;
import com.hjsj.hrms.businessobject.report.reportCollect.IntegrateTableHtmlBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * <p>Title:</p>
 * <p>Description:根据条件生成综合表</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 26, 2006:5:55:54 PM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class ExecuteIntegrateTable2Trans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet recset=null;
			HashMap hm =(HashMap)this.getFormHM().get("requestPamaHM");
//			String[] right_fields=(String[])this.getFormHM().get("right_fields");    //生成综合表的条件 [:2010;1;6;1:2010;1;6;1:0, :2010;1;6;2:2010;1;6;2:0]合并[:jjjjj:2005;1;2;,2006;1;2;,:1:]
//			String   nums=(String)this.getFormHM().get("nums");   					 // aXX:列选择   bXX：行选择
//		//	nums="a2";
//			String   tabid=(String)this.getFormHM().get("tabid");
//			String   unitcode=(String)this.getFormHM().get("unitcode");
//			String   cols=(String)this.getFormHM().get("cols");
//			String   sortid="0";	  //表类别id
//			String   tname="";        //表名称
	//		String temp1=hm.get("right_fields")==null?"":(String)hm.get("right_fields");    //生成综合表的条件 //选择单位过多采用表单提交
			String temp1=(String)this.getFormHM().get("rightfields");
			temp1 = SafeCode.decode(temp1); 
			temp1=PubFunc.keyWord_reback(temp1);
			String[] right_fields=temp1.split("@@");
			this.getFormHM().put("right_fields", right_fields);
			String   nums=(String)hm.get("nums");   					 // aXX:列选择   bXX：行选择
		//	nums="a2";
			String   tabid=(String)hm.get("tabid");
			String   unitcode=(String)hm.get("unitcode");
			String   cols=(String)hm.get("cols");
			String   sortid="0";	  //表类别id
			String   tname="";        //表名称
			String totalnum =(String) this.getFormHM().get("totalnum");
			//年度reportYearid  对应yearid
			//reportTypes 报表类别  =1，一般 =2，年 =3，半年 =4，季报 =5，月报 =6,周报
			//reportCountInfo null 默认为1 对应countid
			//weekid 表类型=6，默认为null
			String reportTypes = (String)this.getFormHM().get("reportTypes");
			String countid = (String)hm.get("reportCount");
			String weekid = hm.get("weekid")==null?"":(String)hm.get("weekid");
			String yearid =(String) hm.get("yearid");
			this.getFormHM().put("countid", countid);
			this.getFormHM().put("weekid2", weekid);
			this.getFormHM().put("yearid", yearid);
			recset=dao.search("select tsortid,name from tname where tabid="+tabid);
			if(recset.next())
			{
				sortid=recset.getString("tsortid");
				tname=recset.getString("name");
			}
			IntegrateTableBo integrateTableBo=new IntegrateTableBo(this.getFrameconn(),totalnum);
			
			UserView _userview=null;
			if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
			{
				_userview=new UserView(userView.getS_userName(),userView.getS_pwd()!=null?userView.getS_pwd():"",this.getFrameconn());
				_userview.canLogin();
			}
			
			ArrayList resultList=null;
			if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
				resultList=integrateTableBo.getIntegrateTableAnalyseData(right_fields,tabid,sortid,nums,unitcode,Integer.parseInt(cols),_userview.getUserId(),_userview.getUserName(),reportTypes,yearid,countid,weekid);
			else
				resultList=integrateTableBo.getIntegrateTableAnalyseData(right_fields,tabid,sortid,nums,unitcode,Integer.parseInt(cols),this.getUserView().getUserId(),this.getUserView().getUserName(),reportTypes,yearid,countid,weekid);
			TnameBo tnameBo=null;
			if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
				tnameBo=new  TnameBo(this.getFrameconn(),tabid,_userview.getUserId(),_userview.getUserName()," ");
			else
				tnameBo=new  TnameBo(this.getFrameconn(),tabid,this.getUserView().getUserId(),this.getUserView().getUserName()," ");
			IntegrateTableHtmlBo htmlBo=new IntegrateTableHtmlBo(this.userView);//xiegh
			String html=htmlBo.creatHtmlView(tnameBo,30,resultList,nums,right_fields,tname);
			
			StringBuffer integrateValues=new StringBuffer("");
			for(Iterator t=resultList.iterator();t.hasNext();)
			{
				String[] temp=(String[])t.next();
				integrateValues.append("`");
				StringBuffer value=new StringBuffer("");
				for(int i=0;i<temp.length;i++)
					value.append(":"+temp[i]);
				integrateValues.append(value.substring(1));
			}
			if(integrateValues.length()>0)
				this.getFormHM().put("integrateValues",integrateValues.substring(1));
			else
				this.getFormHM().put("integrateValues","");
			this.getFormHM().put("html",html);		
			this.getFormHM().put("rowSerialNo",tnameBo.getRowSerialNo());
			this.getFormHM().put("colSerialNo", tnameBo.getColSerialNo());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}

}
