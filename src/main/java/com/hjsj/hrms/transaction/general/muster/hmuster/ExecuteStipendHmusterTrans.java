package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hjsj.hrms.businessobject.general.muster.hmuster.HmusterBo;
import com.hjsj.hrms.businessobject.general.muster.hmuster.HmusterViewBo;
import com.hjsj.hrms.businessobject.general.muster.hmuster.StipendHmusterBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.interfaces.general.HmusterXML;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hjsj.hrms.utils.CodeTool;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.UsrResultTable;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ExecuteStipendHmusterTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String isInit=(String)hm.get("isInit");
			hm.remove("isInit");
			//String flag=(String)hm.get("prv_flag");
			String flag = (String)this.getFormHM().get("flag");
			String payment = (String)hm.get("payment");
			hm.remove("prv_flag");
			String musterID=(String)hm.get("musterID");
			String musterName="";
			String a0100=(String)hm.get("a0100");
			String dbpre=(String)hm.get("dbpre");
			String musterFlag=(String)this.getFormHM().get("musterFlag");//infoself 薪资花名册 默认走设置子集最后一条时间记录  statCount 按系统时间 
			if(StringUtils.isNotBlank(musterFlag)&&!"allInfo".equals(musterFlag)){
				if(!this.userView.isHaveResource(IResourceConstant.HIGHMUSTER,musterID))
					throw GeneralExceptionHandler.Handle(new Exception("无当前高级花名册权限，请授权后再次查看！"));
			}
			dbpre=dbpre!=null?dbpre:"";
			if("infoself".equals(flag)){
				a0100=this.userView.getA0100();
				dbpre=this.userView.getDbname();
			}else{
				if(!"payment".equalsIgnoreCase(payment)){
		            if(!this.userView.isSuper_admin()&&!this.userView.isHaveResource(IResourceConstant.HIGHMUSTER, musterID))
		                throw GeneralExceptionHandler.Handle(new Exception("没有操作该花名册的权限！"));
				}
				
				CheckPrivSafeBo checkPrivSafeBo=new CheckPrivSafeBo(this.frameconn,this.userView);
				dbpre=checkPrivSafeBo.checkDb(dbpre);
				a0100=checkPrivSafeBo.checkA0100("", dbpre, a0100, "");
			}
			UsrResultTable resulttable = new UsrResultTable();
			if(resulttable.isNumber(this.userView.getUserName())){
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.one.number.hroster")));
			}
			ArrayList dblist = this.userView.getPrivDbList();
			if(dbpre.trim().length()<1&&dblist.size()>0){
				dbpre = (String)dblist.get(0);
			}
			String groupCount="0";
			if(hm.get("groupCount")!=null)
			{
				groupCount=(String)hm.get("groupCount");
				hm.remove("groupCount");
			}
			else
			{
				groupCount=(String)this.getFormHM().get("groupCount");
			}
			groupCount=groupCount==null?"0":groupCount;
			String closeWindow=(String)this.getFormHM().get("closeWindow");
			this.getFormHM().put("closeWindow", closeWindow);
			String isTimeIdentifine="0";             // 0:无年月标识  1：有
			StringBuffer html=new StringBuffer("");
			ArrayList hmusterList=new ArrayList();
			ArrayList copyList=new ArrayList();
			CardConstantSet cardConstantSet=new CardConstantSet(this.getUserView(),this.getFrameconn());
			hmusterList=cardConstantSet.getMustCommDataList(dbpre,a0100,"1");	
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String relating=cardConstantSet.getSearchRelating(dao);	
			String b0110="";
			if(!this.userView.isSuper_admin())		
			   b0110=cardConstantSet.getRelatingValue(dao,this.userView.getA0100(),this.userView.getDbname(),relating,userView.getUserOrgId());
			XmlParameter xml=new XmlParameter("UN",b0110,"00");
			xml.ReadOutParameterXml("SS_SETCARD",this.getFrameconn(),"all");
			/**薪酬参数中的年限限制*/
			String year_restrict=xml.getYear_restrict();
			if(year_restrict==null|| "".equals(year_restrict)|| "#".equals(year_restrict))
				year_restrict="";
			if(musterID==null)
			{				
				// 取得花名册列表
				if(hmusterList.size()==0)
				{
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.not.pay.hroster")));
				}
				else
				{
					CommonData data=(CommonData)hmusterList.get(0);
					musterID=data.getDataValue();
					musterName=data.getDataName();
				}
			}
			else
			{
				for(int i=0;i<hmusterList.size();i++)
				{
					CommonData d=(CommonData)hmusterList.get(i);
					if(d.getDataValue().equalsIgnoreCase(musterID))
					{	
							musterName=d.getDataName();
							if("infoself".equals(musterFlag)||"statCount".equals(musterFlag))
								copyList.add(d);
							break;
					}
				}
				
				hm.remove("musterID");
			}
	    //	musterID="17";
		//	a0100="00000164";	
		//	dbpre="Usr";
			HmusterXML hmxml = new HmusterXML(this.getFrameconn(),musterID);
			//liuy 2015-4-21 8862：我的薪酬---列表方式---多层级表头取数显示一层级 begin
			String dataarea = hmxml.getValue(HmusterXML.DATAAREA);
			dataarea=dataarea!=null&&dataarea.trim().length()>0?dataarea:"0";
			String column=hmxml.getValue(HmusterXML.COLUMN);
			String pix=hmxml.getValue(HmusterXML.PIX);
			if("1".equals(dataarea)){
				column="1";
				pix="-1";
			}//liuy 2015-4-21 end
			String groupPoint=hmxml.getValue(HmusterXML.GROUPFIELD);
			String isGroupPoint="0";
			if(groupPoint!=null&&groupPoint.trim().length()>0){
				isGroupPoint="1";
			}
			String groupPoint2=hmxml.getValue(HmusterXML.GROUPFIELD2);
			String isGroupPoint2="0";
			if(groupPoint2!=null&&groupPoint2.trim().length()>0){
				isGroupPoint2="1";
			}
			String isGroupedSerials="0";//按组显示序号
			String groupedSerials = hmxml.getValue(HmusterXML.GROUPEDSERIALS);
			if("1".equals(groupedSerials))
				isGroupedSerials="1";
			String temptable = this.userView.getUserName().trim().replaceAll(" ", "")+"_muster_"+musterID;
			if(temptable.indexOf("（")!=-1||temptable.indexOf("）")!=-1){
				temptable = "\""+temptable+"\"";
			}
			
			StipendHmusterBo stipendHmusterBo=new StipendHmusterBo(this.getFrameconn());
			stipendHmusterBo.setMusterName(musterName);
			stipendHmusterBo.setGroupPoint(groupPoint);
			stipendHmusterBo.setIsGroupPoint(isGroupPoint);
			stipendHmusterBo.setGroupPoint2(groupPoint2);
			stipendHmusterBo.setIsGroupPoint2(isGroupPoint2);
			stipendHmusterBo.setTemptable(temptable);
			ArrayList yearList=new ArrayList();
			if(stipendHmusterBo.isTimeIdentifine(musterID,yearList,dbpre,a0100))
			{
				isTimeIdentifine="1";
			}
			String operate="0";         // 0:无条件  1：按年查询   2：按月查询  3 按季度查询  4 按时间段查询
			String year="";
			String month="";
			String startDate="";
			String endDate="";
			String quarter="";
			
			if("1".equals(isTimeIdentifine))
			{		// 默认第一次进入取查询出年集合的第一条数据
				if("statCount".equals(musterFlag)){
					operate="1";
					Calendar cal=Calendar.getInstance();
					year=cal.get(Calendar.YEAR)+"";
					yearList.clear();
					CommonData data1 = new CommonData("","");
					yearList.add(data1);
					for (int i = 0; i < 10; i++) {
						CommonData data = new CommonData((cal.get(Calendar.YEAR)-i)+"",(cal.get(Calendar.YEAR)-i)+"");
						yearList.add(data);
					}
                    
				}
				
				if((isInit!=null&& "init".equals(isInit))||this.getFormHM().get("operate")==null
						||((String)this.getFormHM().get("operate")).trim().length()==0
						|| "0".equals((String)this.getFormHM().get("operate")))
				{
					operate="1";
                    CommonData data = null;
                    if(yearList.size()>=2)
                        data = (CommonData)yearList.get(1);  // 第一项为空，取第二项(最大年份)
                    else if(yearList.size()>0)
                        data = (CommonData)yearList.get(yearList.size()-1);  // 最小年份
                    if(data != null)
                        year=data.getDataValue();
				}else
				{
					operate=(String)this.getFormHM().get("operate");
					year=(String)this.getFormHM().get("year");
					month=(String)this.getFormHM().get("month");
					startDate=(String)this.getFormHM().get("startDate");
					endDate=(String)this.getFormHM().get("endDate");
					quarter=(String)this.getFormHM().get("quarter");
				}
			}
			else 
				operate="0";
			stipendHmusterBo.getMidvariable(musterID);
			StipendHmusterBo.year_restrict=year_restrict;
			stipendHmusterBo.setGroupCount(groupCount);
			stipendHmusterBo.importData(a0100,musterID,dbpre,operate,year,month,startDate,endDate,quarter,userView);
			HmusterViewBo hmusterViewBo=new HmusterViewBo(this.getFrameconn(),musterID);
			HmusterBo hmusterBo=new HmusterBo(this.getFrameconn(),this.getUserView());
			HashMap cFactorMap=hmusterBo.getCfactor(musterID);
			if(cFactorMap.get("groupN")!=null){
				if(cFactorMap.get("multipleGroupN")!=null)
					hmusterViewBo.setIsGroupNoPage((String)cFactorMap.get("multipleGroupN"));
			}
			String zeroPrint=(String)this.getFormHM().get("zeroPrint");	
			if("TRUE".equalsIgnoreCase(hmxml.getValue(HmusterXML.SHOWZERO))){
				zeroPrint="1";//人事异动模块调用时无取数按钮 需从cs后台取设置参数
			}
			zeroPrint=zeroPrint!=null&&zeroPrint.trim().length()>0?zeroPrint:"0";

			hmusterViewBo.setA0100(a0100);
			hmusterViewBo.setIsGroupPoint2(isGroupPoint2);
			hmusterViewBo.setGroupPoint2(groupPoint2);
			hmusterViewBo.setIsGroupedSerials(isGroupedSerials);
			hmusterViewBo.setGroupCount(groupCount);
			hmusterViewBo.getResourceCloumn(musterID, "stipend");
			ArrayList list=hmusterViewBo.getHumster("stipend",musterID,isGroupPoint,groupPoint,temptable,"10000","1"
					,"0",zeroPrint,"0",column,pix,"",this.userView.getUserName(),dbpre,"0","","","",userView,"next","1","stipend");
			this.getFormHM().put("paperRows", String.valueOf(hmusterViewBo.getPageRows()));
	    	hmusterViewBo=null;
			String tableTitleTop=(String)list.get(0);
			String tableHeader=(String)list.get(1);
			String tableBody=(String)list.get(2);
			String tableTitleBottom=(String)list.get(3);
			String turnPage=(String)list.get(4);
			ArrayList photoList0=(ArrayList)list.get(5);
			
			html.append(CodeTool.toGB2312(tableTitleTop));
			html.append(tableHeader);
			html.append(tableBody);
			html.append(CodeTool.toGB2312(tableTitleBottom));
			
			this.getFormHM().put("isTimeIdentifine",isTimeIdentifine);
			this.getFormHM().put("operate",operate);
			if(yearList!=null&&yearList.size()>0){
				CommonData data=new CommonData("","");
				yearList.remove(0);
				yearList.add(0, data);
			}
			this.getFormHM().put("yearList",yearList);
			this.getFormHM().put("year",year);
			this.getFormHM().put("html",html.toString());
			this.getFormHM().put("dbpre",dbpre);
			this.getFormHM().put("a0100",a0100);
			this.getFormHM().put("musterID",musterID);
			this.getFormHM().put("musterName",musterName);
			this.getFormHM().put("groupCount", groupCount);
			if("infoself".equals(musterFlag)||"statCount".equals(musterFlag)){
				this.getFormHM().put("musterFlag",( "infoself".equals(musterFlag)?"infoself":"statCount"));
				this.getFormHM().put("hmusterList",copyList);
				
			}else
				this.getFormHM().put("hmusterList",hmusterList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
			
		}

	}

}
