package com.hjsj.hrms.businessobject.gz;

import com.hjsj.hrms.service.ladp.PareXmlUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * <p>
 * Title:GzVoucherSendBo
 * </p>
 * <p>
 * Description:发送凭证数据
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2017-06-08
 * </p>
 * 
 * @author xiegh
 * 
 * 
 */
public class GzVoucherSendBo {
	private Connection conn = null;
	private UserView userView = null;
	public static  final String ENDPOINT ="\r";
	public static final String LevelOneSpace="   ";
	public static final String LevelSecondSpace="      ";
	public static final String LevelThirdSpace="             ";
	public static final String LevelFourthSpace="                    ";
	public static final String LevelFithSpace="                        ";
	
	ArrayList<LazyDynaBean> flList = null;
	//业务字典表中对应的fieldsetid = 'GZ_WARRANTLIST'下的字段(系统自带的)
	public static String gz_warrantArray =   "PN_ID ,  FL_ID ,  PZ_ID ,  VOUCHER_DATE ,  DBILL_DATE ,  DBILL_TIMES ,"
			+ "C_TYPE ,  DEPTCODE ,  C_MARK ,  C_SUBJECT ,  FL_NAME ,  C_ITEMSQL ,  C_WHERE ,  N_LOAN ,  MONEY ,"
			+ "CHECK_ITEM ,  CHECK_ITEM_VALUE ,  FLSEQ ,  GPSEQ ,  MONEY_TYPE ,  EXT_MONEY ,  EXCH_RATE" ;

	public ArrayList<String> extractFields = new ArrayList<String>();//分录汇总指标+分组指标
	boolean  flseqFlag = false;
	boolean  gpseqFlag = false;
	public GzVoucherSendBo(Connection conn, UserView userView) {
		super();
		this.conn = conn;
		this.userView = userView;
	}
	
    /*
     *将凭证数据组装成XML格式，发送到web Services端
     *@parameter:pzIds:凭证id; timeIfno 计提时间 
     *
     *按月汇总：1：已生成 2：已通知，3：已接收
     *凭证：0：起草，1：成功，3：失败，4，已发送
     */
    public String sendMessages(String pzIds,String timeInfo)throws GeneralException{		
		String returnMsg = "";
		String info ="";
		String xml = "";
		String paramname="xmlData";
		GzVoucherSendBo bo =new GzVoucherSendBo(conn, userView);
		try{
			if (pzIds != null && pzIds.length() > 0) {
				String[] pzId = pzIds.split(",");
				
				for (int i = 0; i < pzId.length; i++){
					if("".equals(pzId[i]))
						continue;
					
				    	String interfacetype ="";//接口类型
				    	String url ="";//webservice的地址
				    	String method ="";//webservice的方法
				    	String[] configArray=null;//从配置文件中读取的webservice的信息 0:sendtype发送类型；1：url发送地址;2：method发送方法
				    	
				    	//读取数据库gz_warrant中凭证的webservice信息
					    String[] typeAndContent = getInterfaceType(pzId[i]);//数组typeAndContent 0：凭证接口；1：xml 2:凭证的名称
						interfacetype = typeAndContent[0];
						String webservice = typeAndContent[1];//从数据库中读取的content信息
						
						String[] options = getDomAttribute(webservice, "webservice", "url,method");//options[] 0:地址,1：方法；
						if(null!=options[0] && null!=options[1]){
							url = options[0].split("[?]")[0];
							method = options[1];
							
						}else{//读取配置文件voucher_param.xml的webservice信息
							configArray=getOptions();
							url =configArray[1].split("[?]")[0];
							if ("1".equals(interfacetype)) {
								method =configArray[2];//发送凭证的方法
							}
							else {
								method =configArray[3];//发送月汇总数据方法
							}
						}
						Call call  = getCall(url,method, paramname, "");//webService地址优先从凭证定义中走，次从配置文件voucher_param.xml读取
						if("sendVoucherData".equals(method)&&"1".equals(interfacetype)){//同步凭证数据

							ArrayList dataList = getVoucherInfoList(timeInfo,pzId[i]);
								
								if(dataList.size()>0){
										xml = returnBookVoucherXML(dataList,pzId[i]);//财务类型凭证xml格式
										try{
											//发送数据
											returnMsg = (String) call.invoke( new Object[] {xml} ); 
										
										}catch(Exception e){
											e.printStackTrace();
											throw GeneralExceptionHandler.Handle(new Exception("调用接口出现异常，由于网络问题或者其他问题！"));
										}
										//处理返回的XML 
										 info = handReturnXml(returnMsg);
								}else{
										 info = info +"凭证"+typeAndContent[2]+"没有可报送的数据";//只有财务凭证类型的凭证才能一次发发多个	
								}
									
						}else if("sendSalaryData".equals(method)&&"2".equals(interfacetype)){//同步月汇总数据
								//发送按月汇总类型凭证
								ArrayList<LazyDynaBean> dataList = getDataList(timeInfo,pzId[i]);
								
								if(dataList.size()>0){
									xml =returnMonthVoucherXML(dataList,pzId[i]);//月汇总Xml格式 
									
									try{
										
										returnMsg = (String) call.invoke( new Object[] {xml} );//发送xml数据 
										
									}catch(Exception e){
										e.printStackTrace();
										throw GeneralExceptionHandler.Handle(new Exception("调用接口出现异常，由于网络问题或者其他问题！"));
									}
									
									info = handleMonthVoucherReturnXml(returnMsg);//处理回传的xml
									
								}else{
									info ="1";//没有可报送的数据
								}
						}else if("sendMessage".equals(method)){//同步凭证与月汇总数据（不是传送数据，只传送参数，参数里面可区分是凭证还是月汇总数据）
							
						    	xml = bo.assembleVoucherParameterXML(pzId[i],interfacetype,timeInfo);//凭证与月汇总Xml格式
						    	try{
						    		
						    		returnMsg = (String) call.invoke( new Object[] {xml.toString()} );
						    		
								}catch(Exception e){
									e.printStackTrace();
									throw GeneralExceptionHandler.Handle(new Exception("调用接口出现异常，由于网络问题或者其他问题！"));
								}
						    	
					    		info=handReturnAllXml(returnMsg);
						}else{
								 info="调用接口出现异常，由于配置问题或者其他问题！";
						}
					}
			}
			
	   } catch(Exception e) {
		    e.printStackTrace(); 
		    throw GeneralExceptionHandler.Handle(e);	
	} 
    	return info;
	}

	private String handReturnAllXml(String returnMsg) {
		String pn_id="";//凭证种类主键
		String period="";//薪资计提日期 格式2017-01-01 11:43:50
		String upd_state="";//1：接口已更新状态 0:接口未更新状态 需产品处理
		String pndate="";//凭证日期 格式2015-04-08 11:43:50
		String sel_unitcode ="";// 用户管理范围权限
		String flag="";//1：成功 0:失败
		String info="";//接收失败返回信息
		String sucess_pz="";//1：发送成功的凭证,以逗号分割
		try{
			Document doc=PubFunc.generateDom(returnMsg);
			XPath xpath =null;
			Element ele = null;
			xpath = XPath.newInstance("/ehr/flag");
			ele= (Element)xpath.selectSingleNode(doc);			
			if(ele!=null){
				String str =ele.getText();
				flag = str;
			}
			xpath = XPath.newInstance("/ehr/info");
			ele= (Element)xpath.selectSingleNode(doc);			
			if(ele!=null){
				String str =ele.getText();
				info = str;
			}
			xpath = XPath.newInstance("/ehr/upd_state");
			ele= (Element)xpath.selectSingleNode(doc);			
			if(ele!=null){
				String str =ele.getText();
				upd_state = str;
			}
			xpath = XPath.newInstance("/ehr/pn_id");
			ele= (Element)xpath.selectSingleNode(doc);			
			if(ele!=null){
				String str =ele.getText();
				pn_id = str;
			}
			xpath = XPath.newInstance("/ehr/period");
			ele= (Element)xpath.selectSingleNode(doc);			
			if(ele!=null){
				String str =ele.getText();
				period = str;
			}
			xpath = XPath.newInstance("/ehr/pndate");
			ele= (Element)xpath.selectSingleNode(doc);			
			if(ele!=null){
				String str =ele.getText();
				pndate = str;
			}
			xpath = XPath.newInstance("/ehr/sel_unitcode");
			ele= (Element)xpath.selectSingleNode(doc);			
			if(ele!=null){
				String str =ele.getText();
				sel_unitcode = str;
			}
			
			xpath = XPath.newInstance("/ehr/sucess_pz");
			ele= (Element)xpath.selectSingleNode(doc);			
			if(ele!=null){
				String str =ele.getText();
				sucess_pz = str;
			}
			
			if ("1".equals(upd_state)){//接口已更新，产品不需要更新，只需刷新即可
				if ("0".equals(flag)){//失败 抛出失败信息
					if(null!=info&&info.trim().length()>0)
						throw  GeneralExceptionHandler.Handle(new Exception(info));
				}
			}
			else {
				String[] interface_id = getInterfaceType(pn_id);
				if("1".equals(interface_id)){//更新财务凭证类型的状态
					if("1".equals(flag)){
						info = updateWarrantRecordStatus(sucess_pz,"1");
						
					}else{
						info = updateWarrantRecordStatus(pn_id,period,"3");
					}
					
				}else if("2".equals(interface_id)){//更新月汇总凭证类型的状态
						info = updateWarrantDataStatus(pn_id,period,flag,sel_unitcode);
				}
				if(null!=info&&info.trim().length()>0)
					throw  GeneralExceptionHandler.Handle(new Exception(info));
			}
	
		}catch(Exception e){
			e.printStackTrace();
		}
		return info;
	}

	private String updateWarrantRecordStatus(String pn_id, String period, String state) {
		try {
    		ContentDAO dao = new ContentDAO(this.conn);
    		StringBuffer sql = new StringBuffer();
    		sql.append("update GZ_WarrantRecord set state='");
    		sql.append(state);
    		sql.append("' where pn_id ="+pn_id);
    		sql.append(" and "+ Sql_switcher.year(period)+"="+period.split("-")[0]);
    		sql.append(" and "+ Sql_switcher.month(period)+"="+period.split("-")[1]);
    		dao.update(sql.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		return  "发送失败！";
	}

	private String updateWarrantRecordStatus(String sucess_pz,String state) {
		String msg = "发送失败！";
		 String[] pz_array =sucess_pz.split(",");
			try {
	    		ContentDAO dao = new ContentDAO(this.conn);
	    		StringBuffer sql = new StringBuffer();
	    		sql.append("update GZ_WarrantRecord set state='");
	    		sql.append(state);
	    		sql.append("' where pz_id in (");
	    		int i =0;
	    		int j =0;
	    		for(String id :pz_array){
	    			if (i == 0) {
		    			sql.append("'");
		    			sql.append(id);
		    			sql.append("'");
	    			} else {
	    				sql.append(",");
	    				sql.append("'");
	    				sql.append(id);
		    			sql.append("'");
	    			}
	    			i++;
	    			if (j==900) {
	    				sql.append(")");
	    	    		dao.update(sql.toString());
	    	    		j = 0;
	    	    		
	    	    		sql.setLength(0);
	    	    		sql.append("update GZ_WarrantRecord set state='");
	    	    		sql.append(state);
	    	    		sql.append("' where pz_id in ('#'");    	    		
	    			}
	    		}
	    		sql.append(")");
	    		dao.update(sql.toString());
			}catch(Exception e){
				e.printStackTrace();
				msg = "发送失败！";
			}
			return msg;
	}

	private String updateWarrantDataStatus(String pn_id, String period,String flag,String sel_unitcode) {
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer();
			sql.append("update gz_warrantdata set status='");
			sql.append("1".equals(flag)?"1":"3");
			sql.append("' where gz_warrantdata.pzn_id ="+pn_id);
			sql.append(" and"+ Sql_switcher.year("gz_warrantdata.period")+"="+period.split("-")[0]);
			sql.append(" and"+ Sql_switcher.month("gz_warrantdata.period")+"="+period.split("-")[1]);
			sql.append(" and unitcode = "+sel_unitcode);
			dao.update(sql.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		return "1".equals(flag)?"2":"4"; //月汇总前台对应标识 2：成功，4，发送失败
	}

	private String assembleVoucherParameterXML(String pn_id,String interfacetype,String timeInfo) {
		StringBuffer XML = new StringBuffer();
		XML.append(" <?xml version=\"1.0\" encoding='GB2312'?> "+ENDPOINT);
		XML.append(" <ehr>"+ENDPOINT);
		
		StringBuffer bodyXML = new StringBuffer();
		if("1".equals(interfacetype)){
			bodyXML = getVoucherXmlOnThirdStyle(pn_id,timeInfo);
			
		}else if("2".equals(interfacetype)){
			bodyXML = getMonthVoucherXmlOnThirdStyle(pn_id,timeInfo);
			
		} 
		XML.append(bodyXML);
		XML.append(" </ehr>"+ENDPOINT);
		return XML.toString();
	}

	private StringBuffer getMonthVoucherXmlOnThirdStyle(String pn_id, String timeInfo) {
		RowSet rs = null;
		StringBuffer str= new StringBuffer();
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" select * from gz_warrantdata a,gz_warrant b ");
			sql.append(" where a.pn_id = b.pn_id ");
			sql.append(" and a.pn_id="+pn_id );
			sql.append(" and ");
			sql.append(Sql_switcher.year("a.period")+"="+timeInfo.split("-")[0]);
			sql.append(" and ");
			sql.append(Sql_switcher.month("a.period")+"="+timeInfo.split("-")[1]);
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql.toString());
			while(rs.next()){
				Date voucher_date = rs.getDate("pndate");
				String deptcode = rs.getString("unitcode");
				String interface_type=rs.getString("interface_type");
				
				str.append(LevelOneSpace+"<pn_id>"+pn_id+"</pn_id>"+ENDPOINT);
				str.append(LevelOneSpace+"<send_type>"+interface_type+"</send_type>"+ENDPOINT);
				str.append(LevelOneSpace+"<period>"+timeInfo+"</period>"+ENDPOINT);
				str.append(LevelOneSpace+"<voucher_date>"+voucher_date+"</voucher_date>"+ENDPOINT);
				str.append(LevelOneSpace+"<sel_unitcode>"+this.userView.getUnitIdByBusiOutofPriv("1")+"</sel_unitcode>"+ENDPOINT);
				str.append(ENDPOINT);
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return str;
	}

	private StringBuffer getVoucherXmlOnThirdStyle(String pn_id, String timeInfo) {
		RowSet rs = null;
		StringBuffer str= new StringBuffer();
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" select * from GZ_WarrantRecord a,gz_warrant b");
			sql.append(" where a.pn_id = b.pn_id ");
			sql.append(" and a.pn_id="+pn_id );
				sql.append(" and a.state='0' or a.state='3' ");
			sql.append(" and " + Sql_switcher.year("a.dbill_date")+"="+timeInfo.split("-")[0]);
			sql.append(" and ");
			sql.append(Sql_switcher.month("a.dbill_date")+"="+timeInfo.split("-")[1]);
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql.toString());
			while(rs.next()){
				Date voucher_date = rs.getDate("voucher_date");
				String deptcode = rs.getString("deptcode");
				String interface_type=rs.getString("interface_type");
				
				str.append(LevelOneSpace+"<pn_id>"+pn_id+"</pn_id>"+ENDPOINT);
				str.append(LevelOneSpace+"<send_type>"+interface_type+"</send_type>"+ENDPOINT);
				str.append(LevelOneSpace+"<period>"+timeInfo+"</period>"+ENDPOINT);
				str.append(LevelOneSpace+"<voucher_date>"+voucher_date+"</voucher_date>"+ENDPOINT);
				str.append(LevelOneSpace+"<sel_unitcode>"+this.userView.getUnitIdByBusiOutofPriv("1")+"</sel_unitcode>"+ENDPOINT);
				str.append(ENDPOINT);
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return str;
	}

	public String[] getOptions() {
		InputStream in = getClass().getClassLoader().getResourceAsStream("voucher_param.xml");
		if (in==null) return null;
		String[] array =null;
		try {
			Document doc =	PubFunc.generateDom(in);
			String type="";
			XPath  sendtype = XPath.newInstance("/param/sendtype");
			Element typeEle= (Element)sendtype.selectSingleNode(doc);
			if(null!=typeEle)
				type = typeEle.getText();
			
			String url ="";
			XPath  webserviceurlXpth = XPath.newInstance("/param/webserviceurl");
			Element urlEle= (Element)webserviceurlXpth.selectSingleNode(doc);
			if(urlEle!=null)
				url = urlEle.getText();
			
			//发送凭证方法
			String method="";
			XPath  webservicemethod = XPath.newInstance("/param/webservicemethod");
			Element methodEle= (Element)webservicemethod.selectSingleNode(doc);
			if(methodEle!=null)
				method = methodEle.getText();
			//发送月汇总的方法
			String salarymethod="";
			XPath  salaryMethodPath = XPath.newInstance("/param/salarymethod");
			Element salaryMethodEle= (Element)salaryMethodPath.selectSingleNode(doc);
			if(salaryMethodEle!=null)
				salarymethod = salaryMethodEle.getText();
			
			 array = new String[]{type,url,method,salarymethod};
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return array;
	}

	private Call getCall(String url,String method, String paramname,String namespace)
			throws ServiceException, MalformedURLException {
		
		//配置webservices信息	 
		Service service = new Service(); 
		Call call = (Call) service.createCall(); 			
		call.setTargetEndpointAddress(new java.net.URL(url)); 	
		call.setReturnType(XMLType.XSD_STRING);
		call.setUseSOAPAction(true);
		call.setOperationName(new QName(namespace, method));	       
		call.addParameter(new QName(namespace, paramname),XMLType.XSD_STRING,ParameterMode.IN);	
		call.setSOAPActionURI(namespace+"/"+method);//
		return call;
	}
    

	
	private String returnMonthVoucherXML(ArrayList<LazyDynaBean> dataList,String pn_id) {
		StringBuffer voucherXml = new StringBuffer(); // 总xml   
		String c_type =getC_type(pn_id);
		try 
    	{
			voucherXml.append("<?xml version=\"1.0\" encoding='GB2312'?>"+ENDPOINT);	
			voucherXml.append("<ehr>"+ENDPOINT);	
			StringBuffer secondBodyXml = processMonthBodyXml(pn_id);
			voucherXml.append(secondBodyXml);
			voucherXml.append(LevelOneSpace+"<pn_id>"+pn_id+"</pn_id>"+ENDPOINT);
			voucherXml.append(LevelOneSpace+"<c_type>"+c_type+"</c_type>"+ENDPOINT);
			StringBuffer bodyXml = getVoucherMonthBodyXml(pn_id,dataList);
			voucherXml.append(bodyXml);
			voucherXml.append("</ehr>");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		return voucherXml.toString();
	}
	private String handleMonthVoucherReturnXml(String resp) throws Exception {
		String info ="2";//2:发送成功 4：接受xml后修改凭证状态出错
		String flag = "";
		try {
			//"<?xml version=\"1.0\" encoding='GB2312'?><ehr><pn_id>01</pn_id><records><record period=\"2015-04-08 11:43:50\" pndate=\"2015-01-01 11:43:50\"  psncode=\"00001\"  flag=\"1\"><records><info></info></ehr>";
	
			Document  doc=PubFunc.generateDom(resp);
			XPath  xpath = XPath.newInstance("/ehr/pn_id");
			Element ele= (Element)xpath.selectSingleNode(doc);
			String pn_id = ele.getText();
			Element elementIfo= (Element)XPath.newInstance("/ehr/info").selectSingleNode(doc);
			String ifo = elementIfo.getText();
		
			
			Element elementDeatail= (Element)XPath.newInstance("/ehr/records").selectSingleNode(doc);
			String period = null;//计提时间
			List<String> psncodeSucessList= new ArrayList<String>();
			List<String> psncodeFailList= new ArrayList<String>();
			SimpleDateFormat sdf  =   new  SimpleDateFormat( "yyyy-MM" ); 
			if(elementDeatail!=null){
					List<Element>  fldEleList = (List<Element>)elementDeatail.getChildren();
					int i=0;
					for(Element obj : fldEleList){
						if(0==i){
							period = sdf.format((obj.getAttributeValue("period")));
						}
						String psncode=obj.getAttributeValue("psncode");
						flag = obj.getAttributeValue(flag);
						
						if("0".equals(flag)){//失败标识
							psncodeFailList.add(psncode);
						}else if("1".equals(flag)){//成功标识
							psncodeSucessList.add(psncode);
						}
						i++;
					}
				
					if(psncodeSucessList.size()>0)
						updateStatusOnMonthVoucher(psncodeSucessList,pn_id, period,"1");
		    		
					if(psncodeFailList.size()>0)
						updateStatusOnMonthVoucher(psncodeFailList,pn_id, period,"3");
			
					if(null!=ifo||!"".equals(ifo))
						 throw GeneralExceptionHandler.Handle(new Exception(ifo));
				
			}
		}catch(Exception e){
			info = "4";
			e.printStackTrace();
		}
		info = "1".equals(flag)?"2":"4";
		return info;
	}

	private void updateStatusOnMonthVoucher(List<String> psncodeSucessList,String pn_id, String period,String state){
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sqlSuccess = new StringBuffer();
			sqlSuccess.append("update gz_warrantdata set status='");
			sqlSuccess.append(state);
			sqlSuccess.append("' where ");
			sqlSuccess.append(" and "+Sql_switcher.year("gz_warrantdata.period") +" = "+period.split("-")[0]);
			sqlSuccess.append(" and "+Sql_switcher.year("gz_warrantdata.period") +" = "+period.split("-")[1]);
			sqlSuccess.append("  and psndcode  in (");
			int j=0;
			for(int i =0;i<psncodeSucessList.size();i++){
				String psncode =psncodeSucessList.get(i);
				if (i == 0) {
					sqlSuccess.append("'");
					sqlSuccess.append(psncode);
					sqlSuccess.append("'");
				} else {
					sqlSuccess.append(",");
					sqlSuccess.append("'");
					sqlSuccess.append(psncode);
					sqlSuccess.append("'");
				}
				
				j++;
				if (j==900) {
					sqlSuccess.append(")");
		    		dao.update(sqlSuccess.toString());
		    		j = 0;
		    		
		    		sqlSuccess.setLength(0);
		    		sqlSuccess.append("update gz_warrantdata set state='");
		    		sqlSuccess.append(state);
		    		sqlSuccess.append("' where ");
		    		sqlSuccess.append("' and "+Sql_switcher.year("gz_warrantdata.period") +" = "+period.split("-")[0]);
		    		sqlSuccess.append("' and "+Sql_switcher.year("gz_warrantdata.period") +" = "+period.split("-")[1]);
		    		sqlSuccess.append("  and psndcode  in (");  	    		
				}
			}
			sqlSuccess.append(")");
			dao.update(sqlSuccess.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private StringBuffer getVoucherMonthBodyXml(String pn_id, ArrayList dataList) {
		StringBuffer str = new StringBuffer();
		str.append(LevelOneSpace+"<records>"+ENDPOINT);
		for(int i =0;i<dataList.size();i++){
			LazyDynaBean bean = (LazyDynaBean)dataList.get(i);
			str.append(LevelSecondSpace+"<record period=\""+bean.get("period")+"\"  pndate=\""+bean.get("pndate")+"\"  deptcode=\""+bean.get("deptcode"));
			str.append("\"  unitcode_trans=\""+bean.get("unitcode_trans")+"\"  deptcode_trans=\""+bean.get("deptcode_trans")+"\"  name = \""+bean.get("name"));
			str.append("\"  ts=\""+bean.get("ts")+"\"  a0000=\""+bean.get("a0000")+"\"");
			for(LazyDynaBean flBean : flList){
				String c_subject = (String)flBean.get("c_subject");
				String c_subject_value = (String)bean.get(c_subject);
				str.append( c_subject+" = \""+c_subject_value+"\">");
			}
			str.append(">");
			str.append("</record>"+ENDPOINT);
		}
		str.append(LevelOneSpace+"</records>"+ENDPOINT);
		return str;
	}
	private String getC_type(String pn_id) {
		RowSet rs = null;
		String c_type = "";
		try{
			ContentDAO dao = new ContentDAO(conn);
			String sql = " select * from gz_warrant where pn_id="+pn_id;
			rs = dao.search(sql);
			while(rs.next()){
				c_type = rs.getString("c_type");
			}
		}catch(Exception  e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return c_type;
	}
	private StringBuffer processMonthBodyXml(String pn_id) {
		StringBuffer str = new StringBuffer();
	
		str.append(LevelOneSpace+"<fl_fld>"+ENDPOINT);
		for(LazyDynaBean bean :flList){
			String fl_name =(String)bean.get("fl_name");
			String c_mark =(String)bean.get("c_mark");
			String c_subject =(String)bean.get("c_subject");
			
			str.append(LevelSecondSpace+"<flditem fl_name=\""+fl_name+"\" c_subject=\""+c_subject+"\" c_mark=\""+c_mark+"\">");
			str.append("</flditem>"+ENDPOINT);
		}
		str.append(LevelOneSpace+"</fl_fld>"+ENDPOINT);
		return str;
	}
	private ArrayList<LazyDynaBean> getFlList(String pn_id) {
		RowSet rs =null;
		ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
		try{
			String sql = " select fl_name,c_subject,c_mark from GZ_WARRANTLIST where pn_id="+pn_id;
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			LazyDynaBean bean = null;
			String c_subject="";
			while(rs.next()){
				bean = new LazyDynaBean();
				bean.set("fl_name", rs.getString("fl_name"));
				c_subject=rs.getString("c_subject");
    			if(!Character.isLetter(c_subject.charAt(0)))//xiegh 20170621 如果是按月汇总且科目是以非字母开头，则在科目前面添加“HJ_” bug:28859
    				c_subject = "HJ_" + c_subject;
				bean.set("c_subject",c_subject );
				bean.set("c_mark", rs.getString("c_mark"));
				list.add(bean);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}

	private ArrayList<LazyDynaBean> getDataList(String timeInfo, String voucher_id) {
 		if(timeInfo==null || timeInfo.trim().length()==0)
			return new ArrayList<LazyDynaBean>();
		RowSet rs = null;
		flList = getFlList(voucher_id);
		ArrayList<LazyDynaBean>  list = new ArrayList<LazyDynaBean>();
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("select * from GZ_WARRANTDATA a ");
			sql.append(" and a.pn_id='"+voucher_id+"' ");
			if(timeInfo!=null&&!"all".equalsIgnoreCase(timeInfo))
			{
				String[] temps=timeInfo.split("-");
				sql.append(" and "+Sql_switcher.year("a.period")+"="+temps[0]);
				sql.append(" and "+Sql_switcher.month("a.period")+"="+temps[1]);
			}
			sql.append(" order by a.period desc ");
			
			rs = dao.search(sql.toString());
			LazyDynaBean bean = null;
			while(rs.next()){
				bean = new LazyDynaBean();
				Date period  = rs.getDate("period");//薪资计提日期 
				bean.set("period",period);
				
				Date pndate = rs.getDate("pndate");//凭证日期
				bean.set("pndate",pndate);
				
				String deptcode = rs.getString("deptcode");//部门编码 
				bean.set("deptcode", deptcode);
				
				String psncode = rs.getString("psncode");//人员唯一值
				bean.set("psncode", psncode);
				
				String unitcode_trans = rs.getString("unitcode_trans");
				bean.set("unitcode_trans", unitcode_trans);
				
				String deptcode_trans = rs.getString("deptcode_trans");
				bean.set("deptcode_trans", deptcode_trans==null?"":deptcode_trans);
				
				String name = rs.getString("name");
				bean.set("name", name);
				
				String a0000 = rs.getString("a0000");
				bean.set("a0000", a0000);
				
				String ts = rs.getString("ts");
				bean.set("ts", ts);
				
				for(LazyDynaBean flVO : flList){
					String c_subject = (String)flVO.get("c_subject");
					String c_subject_value = rs.getString(c_subject);
					c_subject_value=c_subject_value==null?"":c_subject_value;
					bean.set(c_subject, c_subject_value);
					
				}
				
				list.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return list;
	}
	private String[] getInterfaceType(String pn_id) {
		RowSet rs = null;
		String info[]=new String[3];
		try{
			String sql = "select interface_type,content,c_name from gz_warrant where pn_id ="+pn_id;
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			int i=0;
			while(rs.next()){
				String interface_type = rs.getString("interface_type");	
				info[i]=interface_type;
				i++;
				String content = rs.getString("content");
				info[i]=content;
				i++;
				String c_name = rs.getString("c_name");
				info[i]=c_name;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return info;
	}
	
	
	
	
	
	/**
	 * 获得凭证明细数据
	 * @param status
	 * @param timeInfo
	 * @param voucher_id
	 * @param headList
	 * @return
	 */
	public ArrayList getVoucherInfoList(String timeInfo,String voucher_id)throws GeneralException
	{
		ArrayList dataList = new ArrayList();
		RowSet rowSet = null;
		try
		{
			if(timeInfo==null || timeInfo.trim().length()==0)
				return new ArrayList();
			
			ContentDAO dao = new ContentDAO(this.conn);
			
			//String c_groupStr = getC_group(voucher_id);
			extractFields = getExtractFields(voucher_id);//凭证汇总指标+分录汇总指标
			
			StringBuffer sql=new StringBuffer("select * from GZ_WarrantRecord a,GZ_WARRANTLIST b  ");
			sql.append(" where a.pn_id=b.pn_id ");
			sql.append(" and a.fl_id=b.fl_id and a.pn_id='"+voucher_id+"' ");
			sql.append(" and (a.state='0' or a.state = '3')");
			if(timeInfo!=null&&!"all".equalsIgnoreCase(timeInfo))
			{
				String[] temps=timeInfo.split("-");
				sql.append(" and "+Sql_switcher.year("a.dbill_Date")+"="+temps[0]);
				sql.append(" and "+Sql_switcher.month("a.dbill_Date")+"="+temps[1]);
			}
			sql.append(" order by a.Pz_id ");
			  
			rowSet=dao.search(sql.toString());
			//String collect_fields = getCollects(voucher_id);//分录分组指标
			
			//collect_fields=collect_fields+(c_groupStr.length()>0?","+c_groupStr:"");
			
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			LazyDynaBean data_bean=null;
			while(rowSet.next()){
					data_bean = new LazyDynaBean();
					
					String pz_id = rowSet.getString("pz_id");
					Date dbill_date = rowSet.getDate("dbill_date");
					Date voucher_date = rowSet.getDate("voucher_date");
					String n_loan=rowSet.getString("n_loan");
						   n_loan=n_loan==null?"":n_loan;
					String fl_id=rowSet.getString("fl_id");
					String fl_name=rowSet.getString("fl_name");
						   fl_name=fl_name==null?"":fl_name;
					String c_mark=rowSet.getString("c_mark");
					String c_subject=rowSet.getString("c_subject");
					String exch_rate=rowSet.getString("exch_rate");
						   exch_rate=exch_rate==null?"":exch_rate;
					String money=rowSet.getString("money");
						   money=money==null?"":money;
					String money_type=rowSet.getString("money_type");
						   money_type=money_type==null?"":money_type;
					String ext_money=rowSet.getString("ext_money");
						   ext_money=ext_money==null?"":ext_money;
					String check_item=rowSet.getString("check_item");
						   check_item=check_item==null?"":check_item;
					String check_item_value=rowSet.getString("check_item_value");
						   check_item_value=check_item_value==null?"":check_item_value;
					String deptcode=rowSet.getString("deptcode");
						   deptcode=deptcode==null?"":deptcode;
				    String flseq = rowSet.getString("flseq");
				    	   flseq=flseq==null?"":flseq;
				   	String gpseq = rowSet.getString("gpseq");
				   			gpseq=gpseq==null?"":gpseq;
				   	if(extractFields.size()>0){//处理分组汇总指标
			   				for(String str : extractFields){
			   					String value = rowSet.getString(str);
			   					value =(value==null?"":value);
			   					data_bean.set(str, value);
			   				}
				   	}
				   	
					 data_bean.set("n_loan", n_loan);
					 data_bean.set("fl_id", fl_id);
					 data_bean.set("fl_name", fl_name);
					 data_bean.set("c_mark", c_mark);
					 data_bean.set("c_subject", c_subject);
					 data_bean.set("exchg_rate", exch_rate);
					 data_bean.set("money", money);
					 data_bean.set("money_type", money_type);
					 data_bean.set("ext_money", ext_money);
					 data_bean.set("check_item", check_item);
					 data_bean.set("check_item_value", check_item_value);
					 data_bean.set("deptcode", deptcode);
					 data_bean.set("dbill_date", sdf.format(dbill_date));
					 data_bean.set("voucher_date", sdf.format(voucher_date));
					 data_bean.set("pz_id", pz_id);
					 data_bean.set("flseq", flseq);
					 data_bean.set("gpseq", gpseq);
				
				dataList.add(data_bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}finally{
			PubFunc.closeDbObj(rowSet);
		}
		return dataList;
	}

	private ArrayList<String> getExtractFields(String voucher_id) throws SQLException {
		RowSet rs = null;
		ArrayList<String> extfields =new ArrayList<String>();
		try{
		//	VoucherBo xmlbo = new VoucherBo(conn,"","voucher",voucher_id);	
			String sql = "select * from gz_warrant where pn_id ="+voucher_id;
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			while(rs.next()){
				String content = rs.getString("content");
				String[] fields = getDomAttribute(content, "items", "fields");
				for(String itemid : fields[0].split(",")){
					if(!gz_warrantArray.contains(itemid.toUpperCase()))
						extfields.add(itemid);
				}
			}
			String collects = getCollects(voucher_id);
			if(collects.length()>0){
				for(String str : collects.split(",")){
					extfields.add(str);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return extfields;
	}

	
	   public String returnBookVoucherXML(ArrayList dataList,String pn_id){
		  	StringBuffer voucherXml = new StringBuffer(); // 总xml   	
	    	try{
	    		/************************组装财务类型凭证的XML***************************************/
				voucherXml.append("<?xml version=\"1.0\" encoding='GB2312'?>"+ENDPOINT);	
				voucherXml.append("<ehr>"+ENDPOINT);	
				StringBuffer secondBodyXml = processOthers();
				voucherXml.append(secondBodyXml);
				StringBuffer bodyXmll = getVoucherBodyXml(pn_id,dataList);
				voucherXml.append(bodyXmll+ENDPOINT);
				voucherXml.append("</ehr>");
	    	}catch (Exception e) {
	    		e.printStackTrace();
	    	} 
	    	return voucherXml.toString();
	    }
	
	private String handReturnXml(String response) throws JDOMException,Exception, GeneralException {
		String msg = "发送完成！";
		boolean failFlag = false; 
		Document doc=PubFunc.generateDom(response);
		XPath xpath = XPath.newInstance("/ehr");
		Element parentEle= (Element)xpath.selectSingleNode(doc);	
		ArrayList<String> sucessList = new ArrayList<String>();
		ArrayList<String> failList = new ArrayList<String>();
		if(parentEle!=null){
			List<Element>  childList = (List <Element>)parentEle.getChildren(); 
			String flag ="";
			String ifo ="";
			for(Element element : childList){
				String pz_id =element.getAttributeValue("pz_id");//凭证号
				ifo = ifo  + element.getChildText("info");
					
				flag =element.getChildText("flag");
				if("1".equals(flag)){
					sucessList.add(pz_id);
				}else if("0".equals(flag))
				{
					failFlag = true;
					failList.add(pz_id);
				}
						
			}
			if(sucessList.size()>0)
				updateVoucherStatus(sucessList,"1");//更新成功状态
			
			if(failList.size()>0)
				updateVoucherStatus(failList,"3");//更新失败转态
			
			if(null!=ifo&&ifo.trim().length()>0)
				 throw  GeneralExceptionHandler.Handle(new Exception(ifo));
		}
		if(failFlag)
			msg =msg+"部分发送失败！";
		return msg;
	}

	private void updateVoucherStatus(ArrayList<String> list, String state) {
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer();
			sql.append("update GZ_WarrantRecord set state='");
    		sql.append(state);
    		sql.append("' where pz_id in (");
    		int i =0;
    		int j =0;
    		for(String str :list){
    			if (i == 0) {
	    			sql.append("'");
	    			sql.append(str);
	    			sql.append("'");
    			} else {
    				sql.append(",");
    				sql.append("'");
    				sql.append(str);
	    			sql.append("'");
    			}
    			i++;
    			j++;
    			if (j==900) {
    				sql.append(")");
    	    		dao.update(sql.toString());
    	    		j = 0;
    	    		
    	    		sql.setLength(0);
    	    		sql.append("update GZ_WarrantRecord set state='");
    	    		sql.append(state);
    	    		sql.append("' where pz_id in ('#'");    	    		
    			}
    		}
    		sql.append(")");
    		dao.update(sql.toString());
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	   /**
		 * 获取解析的值
		 * 
		 * @param xml
		 * @param nodePath
		 * @return
		 */
		private ArrayList getMapList(String xml, String nodePath) 
		{
			PareXmlUtils xmlUtils = new PareXmlUtils(xml);
			ArrayList list = new ArrayList();
			List nodeList = xmlUtils.getNodes(nodePath);
			for (int i = 0; i < nodeList.size(); i++) 
			{
				Map map = new HashMap();
				Element el = (Element) nodeList.get(i);
				List li = el.getChildren();
				if (li != null) 
				{
					for (int j = 0; j < li.size(); j++) 
					{
						Element e = (Element) li.get(j);
						String value = e.getText();
						if (value == null) {
							value = "";
						}
						map.put(e.getName(), value);
					}
				}

				list.add(map);
			}

			return list;
		}
		/**
		 * 
		 * @param strXml
		 * @param pn_id:凭证ID
		 * @param type: 返回新增的<!--凭证分录自定义的指标，非系统指标 来自凭证分录表GZ_WARRANTLIST-->的格式不一样
		 *		0：<warrantlist_fld>	
		 *         <flditem itemid="gz_wa" itemdesc="辅助核算项目1" itemtype="char" length="30" dec="0"></flditem>
		 *         </warrantlist_fld>
		 *      
		 *      1: <gz_wa></gz_wa>
		 *		   <gz_wc></gz_wc>
		 */		  
		private StringBuffer processOthers() {
			DbWizard dbw = new DbWizard(this.conn);
    		flseqFlag = dbw.isExistField("gz_warrantrecord", "flseq",false);
    		gpseqFlag = dbw.isExistField("gz_warrantrecord", "gpseq",false);
			StringBuffer  strXml = new StringBuffer();
					strXml.append(LevelOneSpace+"<warrantlist_fld>"+ENDPOINT);
					if(extractFields.size()>0){
						for(String itemid : extractFields){
							 FieldItem item = DataDictionary.getFieldItem(itemid.toUpperCase());
							 if(null!=item){
								 String itemdesc =item.getItemdesc();
								 String itemtype = item.getItemtype();
								 int length = item.getItemlength();
								 int dec = item.getDecimalwidth();
								 strXml.append(LevelSecondSpace+"  <flditem itemid=\""+itemid+"\"  itemdesc=\""+itemdesc+"\"  itemtype=\""+itemtype+"\"  length=\""+length+"\"  dec=\""+dec+"\" ></flditem>"+ENDPOINT);
							 }
						}
							
					}
			strXml.append(LevelOneSpace+"</warrantlist_fld>"+ENDPOINT);
			return strXml;
		}
		
	    private StringBuffer getVoucherBodyXml(String pn_id,ArrayList dataList) throws GeneralException, SQLException {
	       	RowSet rs= null;
	    	StringBuffer bodyXml = new StringBuffer();
	    	ContentDAO dao = new ContentDAO(conn);
	    	try{
	    		String sql=" select b.* from  gz_warrant b where b.pn_id ="+pn_id;
		    	rs = dao.search(sql);
		    	while(rs.next()){
		    		String pnid = rs.getString("pn_id");
		    		String c_name = rs.getString("c_name");
		    		String c_type = rs.getString("c_type");
		    		String c_code =rs.getString("c_code")==null?"":rs.getString("c_code");
		    		String content =rs.getString("content");
		    		String is_dual_money = "".equals(getDomValue(content,"is_dual_money"))?"false":getDomValue(content,"is_dual_money");
		    		String exchg_rate_fld=getDomValue(content,"exchg_rate_fld");
		    		
		    		bodyXml.append(LevelOneSpace+"<voucher_attribute send_user=\""+this.userView.getUserName()+"\" pn_id=\""+pnid+"\" c_name=\""+c_name+"\" c_type=\""+c_type+"\"  c_code=\""+c_code+"\" is_dual_money=\""+is_dual_money+"\"  exchg_rate_fld =\""+exchg_rate_fld.toLowerCase()+"\">"+ENDPOINT);
		    		bodyXml.append(LevelOneSpace+"</voucher_attribute>"+ENDPOINT);
		    	}
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}finally{
	    		PubFunc.closeDbObj(rs);
	    	}
	    	processBodyXml(dataList, bodyXml,pn_id);//添加分录信息
	    	return bodyXml;
		}

		public String getDomValue(String content,String str) throws Exception, IOException {
			
			Document doc  =PubFunc.generateDom(content);
			
			XPath xpath = XPath.newInstance("/voucher/"+str);
			Element ele= (Element)xpath.selectSingleNode(doc);
			String value ="";
			if(null!=ele)
				value = ele.getText();
			return value;
		}
		
		public String[] getDomAttribute(String content,String item,String options){
			String[] optionArray = options.split(",");
			String[] arrayItem = new String[optionArray.length];
			try{
				Document doc=PubFunc.generateDom(content);
				XPath xpath = XPath.newInstance("/voucher/"+item);
				
				Element ele= (Element)xpath.selectSingleNode(doc);
				
				if(null!=ele)
					for(int i =0;i<optionArray.length;i++){
						String str =ele.getAttributeValue(optionArray[i]);
						if(!"".equals(str))
							arrayItem[i] =str;
					}
			}catch(Exception e){
				e.printStackTrace();
			}
			return arrayItem;
		}


		private void processBodyXml(ArrayList dataList, StringBuffer bodyXml,String pn_id) throws GeneralException, SQLException {
			bodyXml.append(LevelSecondSpace+"<voucher_data>"+ENDPOINT);
    			
			String str ="";
			for(int i =0;i<dataList.size();i++){
				LazyDynaBean bean = (LazyDynaBean) dataList.get(i);	
				String pz_id = (String)bean.get("pz_id");
				String dbill_date=(String)bean.get("dbill_date");
				String voucher_date=(String)bean.get("voucher_date");
				if(!pz_id.equalsIgnoreCase(str)){
					if(!"".equalsIgnoreCase(str)){
						bodyXml.append(LevelThirdSpace+"</voucher>"+ENDPOINT);
					}
					bodyXml.append(LevelThirdSpace+"<voucher pz_id=\""+pz_id+"\" dbill_date =\""+dbill_date+"\"  voucher_date =\""+voucher_date+"\">"+ENDPOINT);
					str= pz_id;
				}
				String n_loan = (String)bean.get("n_loan");//借贷标志
				String fl_id =  (String)bean.get("fl_id");//分录序号
				String fl_name =  (String)bean.get("fl_name");//分录名称
				String c_mark =  (String)bean.get("c_mark");//分录摘要
				String c_subject =  (String)bean.get("c_subject");//科目编号
				String exchg_rate =  (String)bean.get("exchg_rate");//汇率 未维护则为空
				String money =  (String)bean.get("money");//原币金额
				String money_type =  (String)bean.get("money_type");//币种 未维护则为空
				String ext_money =  (String)bean.get("ext_money");//本币金额
				String check_item =  (String)bean.get("check_item");//辅助核算项目
				String check_item_value =  (String)bean.get("check_item_value");//辅助核算项目值
				String deptcode =  (String)bean.get("deptcode");//部门编号
				String flseq ="";
				String gpseq ="";
				if(flseqFlag)
					flseq= (String)bean.get("flseq");//分录序号
				if(gpseqFlag)
					 gpseq = (String)bean.get("gpseq");//分录内序号
				
				
				//拼写分录信息
				bodyXml.append(LevelFourthSpace+"<detaiitem>\r");
				bodyXml.append(LevelFithSpace+"<n_loan>"+n_loan+"</n_loan>"+ENDPOINT);
				bodyXml.append(LevelFithSpace+"<fl_id>"+fl_id+"</fl_id>"+ENDPOINT);
				bodyXml.append(LevelFithSpace+"<fl_name>"+fl_name+"</fl_name>"+ENDPOINT);
				bodyXml.append(LevelFithSpace+"<c_mark>"+c_mark+"</c_mark>"+ENDPOINT);
				bodyXml.append(LevelFithSpace+"<c_subject>"+c_subject+"</c_subject>"+ENDPOINT);
				bodyXml.append(LevelFithSpace+"<exchg_rate>"+exchg_rate+"</exchg_rate>"+ENDPOINT);
				bodyXml.append(LevelFithSpace+"<money>"+money+"</money>"+ENDPOINT);
				bodyXml.append(LevelFithSpace+"<money_type>"+money_type+"</money_type>"+ENDPOINT);
				bodyXml.append(LevelFithSpace+"<ext_money>"+ext_money+"</ext_money>"+ENDPOINT);
				bodyXml.append(LevelFithSpace+"<check_item>"+check_item+"</check_item>"+ENDPOINT);
				bodyXml.append(LevelFithSpace+"<check_item_value>"+check_item_value+"</check_item_value>"+ENDPOINT);
				bodyXml.append(LevelFithSpace+"<deptcode>"+deptcode+"</deptcode>"+ENDPOINT);
				bodyXml.append(LevelFithSpace+"<flseq>"+flseq+"</flseq>"+ENDPOINT);//这两个序列号是后来添加的
				bodyXml.append(LevelFithSpace+"<gpseq>"+gpseq+"</gpseq>"+ENDPOINT);
				if(extractFields.size()>0){
					for(String obj :extractFields){
						String value = (String)bean.get(obj);
						bodyXml.append(LevelFithSpace+"<"+obj+">"+(value==null?"":value)+"</"+obj+">"+ENDPOINT);//分录汇总指标
					}
				}
				bodyXml.append(LevelFourthSpace+"</detaiitem>"+ENDPOINT);
				//处理分录表其他字段gz_warrantList自定义的其他字段 指标编号为节点名称，指标值为节点值
			}
			if (dataList.size()>0){
				bodyXml.append(LevelThirdSpace+"</voucher>"+ENDPOINT);
			}
			
			bodyXml.append(LevelSecondSpace+"</voucher_data>");
		}

		private String getCollects(String pn_id){
			String collect_fields ="";
			try{
				ContentDAO dao = new ContentDAO(conn);
				RecordVo voucherVo=new RecordVo("gz_warrant");
				voucherVo.setInt("pn_id",Integer.parseInt(pn_id));
				voucherVo=dao.findByPrimaryKey(voucherVo);
				 collect_fields=voucherVo.getString("collect_fields");  //汇总指标
			}catch(Exception e){
				e.printStackTrace();
			}
			return collect_fields;
		}
		
	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public UserView getUserView() {
		return userView;
	}

	public void setUserView(UserView userView) {
		this.userView = userView;
	}
}
