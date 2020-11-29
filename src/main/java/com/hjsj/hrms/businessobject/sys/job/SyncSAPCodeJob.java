package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SyncSAPCodeJob implements Job {
	private Category log = Category.getInstance(SyncSAPCodeJob.class.getName());

	@Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Connection conn=null;
		try{
			conn=AdminDb.getConnection();

			/*ZX         机构代码    20
			ZV         成本中心    20
			ZU         利润中心    20
			ZT         订单号      20
*/
			// 公司代码codesetid
			String companySetId = SystemConfig.getPropertyValue("nxyp_maindata_companycodesetid");

			// 成本中心codesetid
			String costcenterSetId = SystemConfig.getPropertyValue("nxyp_maindata_costcentersetid");

			// 利润中心codesetid
			String profitcenterSetId = SystemConfig.getPropertyValue("nxyp_maindata_profitcentersetid");
			//订单号
			String orderSetId = SystemConfig.getPropertyValue("nxyp_maindata_ordersetid");
           if(companySetId==null|| "".equals(companySetId.trim())){
				log.error("公司代码 代码类号为空，请在system.properties中配置参数nxyp_maindata_companycodesetid");
			}
			if(costcenterSetId==null|| "".equals(costcenterSetId.trim())){
				log.error("成本中心代码类号为空 ，请在system.properties中配置参数nxyp_maindata_costcentersetid");
			}
			if(profitcenterSetId==null|| "".equals(profitcenterSetId.trim())){
				log.error("利润中心代码类号为空，请在system.properties中配置参数nxyp_maindata_profitcentersetid");
			}
			if(orderSetId==null|| "".equals(orderSetId)){
				log.error("订单号代码类号为空，请在system.properties中配置参数nxyp_maindata_ordersetid");
			}
			String xml = this.callSelfWebservice();
			String flag=this.analyseData(conn, companySetId, costcenterSetId, profitcenterSetId, orderSetId, xml);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(conn!=null) {
					conn.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	public String callSelfWebservice(){
		String xml="";
		try{
			/*String url="http://localhost:8088/ehr_service/services/dtgj";
			Service service = new Service(); 
		    org.apache.axis.client.Call _call =(Call)service.createCall();
		    _call.setTargetEndpointAddress(new java.net.URL(url)); 	
			_call.setReturnType(XMLType.XSD_STRING);
	        _call.setUseSOAPAction(true);
	        _call.setOperationStyle(org.apache.axis.constants.Style.RPC);
		    _call.setOperationUse(org.apache.axis.constants.Use.LITERAL);
	        _call.setOperationName("syncCodeData");
	        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});
	         xml = (String)_resp;*/


			SOAPConnection con = null;
			StringBuffer responseStr = new StringBuffer();
			try{
				String url ="http://192.168.11.180:8000/sap/bc/srt/rfc/sap/ZWEBS_INFO_HR/800/ZWEBS_INFO_HR/ZWEBS_INFO_HR?sap-client=800";
				MessageFactory reqMsgFactory = MessageFactory.newInstance();
				// 2.
				SOAPMessage request = reqMsgFactory.createMessage();
				
				// 3.
				SOAPPart soapPart = request.getSOAPPart();
				SOAPHeader header=request.getSOAPHeader();
				//header.setUserData(key, data, handler)
				// 3.1
				SOAPEnvelope envelope = soapPart.getEnvelope();

				envelope.setAttribute("xmlns:urn", "urn:sap-com:document:sap:soap:functions:mc-style");

				
				
				SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
				con = factory.createConnection();
				SOAPBody body = request.getSOAPBody();
				
				//request.setProperty(SOAPMessage., value)
				// 方法
			    SOAPElement getMessage = body.addChildElement("ZinfoToHr","urn","urn:sap-com:document:sap:soap:functions:mc-style");
				//SOAPElement getMessage =body.addChildElement("ZinfoToHr");
				getMessage.setEncodingStyle(SOAPConstants.URI_NS_SOAP_ENCODING);
				// 第一个参数
				SOAPElement in0 = getMessage.addChildElement("TAufnr");
			
				// 第二个参数
				SOAPElement in1 = getMessage.addChildElement("TBukrs");
			
				// 第三个参数
				SOAPElement in2 = getMessage.addChildElement("TKostl");
				
				SOAPElement in3 = getMessage.addChildElement("TPrctr");
	            
				//request.writeTo(System.out);
				//System.out.println(request.toString());
				SOAPMessage response = con.call(request, url);
				//response.writeTo(System.out);
				SOAPBody responseBody = response.getSOAPBody();
				NodeList it = responseBody.getFirstChild().getChildNodes();
				for (int j = 0; j < it.getLength(); j++) {
					Node node = it.item(j);
					if ("TAufnr".equalsIgnoreCase(node.getNodeName())||"TBukrs".equalsIgnoreCase(node.getNodeName())||"TKostl".equalsIgnoreCase(node.getNodeName())||"TPrctr".equalsIgnoreCase(node.getNodeName())) {
						responseStr.append("<"+node.getNodeName()+">");
						NodeList nodeList = node.getChildNodes();
						for (int i = 0; i < nodeList.getLength(); i++) {
							Node child = nodeList.item(i);
							if("item".equalsIgnoreCase(child.getNodeName())){
								responseStr.append("<"+child.getNodeName()+">");
								NodeList childList = child.getChildNodes();
								for(int k=0;k<childList.getLength();k++){
									Node cchild=childList.item(k);
									responseStr.append("<"+cchild.getNodeName().toUpperCase()+">"+cchild.getTextContent()+"</"+cchild.getNodeName().toUpperCase()+">");
								}
								responseStr.append("</"+child.getNodeName()+">");
							}
						}
						responseStr.append("</"+node.getNodeName()+">");
					}
				}
				if(responseStr.length()>0){
					responseStr.insert(0, "<?xml version='1.0' encoding='GBK'?><root>");
					responseStr.append("</root>");
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					if(con!=null) {
						con.close();
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			xml= responseStr.toString();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return xml;
	}
	/**
	 * 
	 * @param conn
	 * @param companySetId 公司代码
	 * @param costcenterSetId 成本中心
	 * @param profitcenterSetId 利润中心
	 * @param orderSetId 订单号
	 * @return
	 */
	public String analyseData(Connection conn,String companySetId,String costcenterSetId,String profitcenterSetId,String orderSetId,String xml){
		String flag="";
		try{
			if(xml==null|| "".equals(xml))
			{
				flag="无数据返回xml文件为空！";
				return flag;
			}
			Document doc = PubFunc.generateDom(xml);
			Element root  = doc.getRootElement();
			List list = root.getChildren();
			for(int i=0;i<list.size();i++){
				Element element = (Element)list.get(i);
				if("TBukrs".equalsIgnoreCase(element.getName())){//公司代码
					//codesetid, codeitemid, codeitemdesc,parentid,childid,invalid,layer,corcode,end_date,start_date
					ArrayList dataList = new ArrayList();
					List itemlist = element.getChildren("item");
					for(int j=0;j<itemlist.size();j++){
						String  selfId=(j+1)+"";
						if(j<9) {
							selfId="0"+selfId;
						}
						Element itemElement = (Element)itemlist.get(j);
						ArrayList subList = new ArrayList();
						subList.add(companySetId);
						// 编号
						subList.add(selfId+"");
						// 名称
						subList.add(itemElement.getChild("BUTXT").getText());
						// parentid
						subList.add(selfId+"");
						// childid
						subList.add(selfId+"");
						//layer
						subList.add("1");
						//corcode
						subList.add(itemElement.getChild("BUKRS").getText().toUpperCase());
						// 结束时间
						subList.add(DateUtils.getSqlDate("9999-12-31 00:00:00","yyyy-MM-dd HH:mm:ss"));
						// 开始时间
						subList.add(DateUtils.getSqlDate("1949-10-01 00:00:00","yyyy-MM-dd HH:mm:ss"));
						dataList.add(subList);
					}
					if(dataList.size()>0){
						this.deleteCode(conn, companySetId);
						this.insertData(dataList, conn,companySetId);
					}
				}else if("TAufnr".equalsIgnoreCase(element.getName())){//订单号
				
					//成本中心
					ArrayList dataList = new ArrayList();
					List itemlist = element.getChildren("item");
					HashMap onemap = new HashMap();
					HashMap twoMap = new HashMap();
					HashMap map = new HashMap();
					int t1=1;
					int t2=1;
					int t3=1;
					for(int j=0;j<itemlist.size();j++){
						Element itemElement = (Element)itemlist.get(j);
						String BUKRS=itemElement.getChildText("BUKRS");//公司代码
						String BUTXT=itemElement.getChildText("BUTXT");
						String PRCTR=itemElement.getChildText("PRCTR");//利润中心
						String LTEXT=itemElement.getChildText("LTEXT");
						String AUFNR=itemElement.getChildText("AUFNR");//订单
						String KTEXT=itemElement.getChildText("KTEXT");
						ArrayList subList = new ArrayList();
						ArrayList subList2=new ArrayList();
						ArrayList subList3=new ArrayList();
						int bId=1;
						int pId=1;
						int aId=1;
						if(onemap.get(BUKRS.toUpperCase())!=null){
							bId=Integer.parseInt((String)onemap.get(BUKRS.toUpperCase()));
						}else{
							bId=t1;
						}
						String bSelfId=bId+"";
						if(bId<=9) {
							bSelfId="0"+bId;
						}
						if(twoMap.get(BUKRS.toUpperCase()+PRCTR.toUpperCase())!=null){
							pId=Integer.parseInt((String)twoMap.get(BUKRS.toUpperCase()+PRCTR.toUpperCase()));
						}else{
							pId=t2;
						}
						String pSelfId=pId+"";
						if(pId<=9) {
							pSelfId="0"+pId;
						}
						pSelfId=bSelfId+pSelfId;
						if(map.get(BUKRS.toUpperCase()+PRCTR.toUpperCase()+AUFNR.toUpperCase())!=null){
							aId=Integer.parseInt((String)map.get(BUKRS.toUpperCase()+PRCTR.toUpperCase()+AUFNR.toUpperCase()));
						}else {
							aId=t3;
						}
						String aSelfId=aId+"";
						if(aId<=9) {
							aSelfId="0"+aId;
						}
						aSelfId=pSelfId+aSelfId;
						if(onemap.get(BUKRS.toUpperCase())==null){
							subList.add(orderSetId);
							// 编号
							subList.add(bSelfId);
							// 名称
							subList.add(BUTXT);
							// parentid
							subList.add(bSelfId);
							// childid
							subList.add(pSelfId);
							//layer
							subList.add("1");
							//corcode
							subList.add(BUKRS);
							// 结束时间
							subList.add(DateUtils.getSqlDate("9999-12-31 00:00:00","yyyy-MM-dd HH:mm:ss"));
							// 开始时间
							subList.add(DateUtils.getSqlDate("1949-10-01 00:00:00","yyyy-MM-dd HH:mm:ss"));
							dataList.add(subList);
							onemap.put(BUKRS.toUpperCase(), t1+"");
							t1++;
						}
						if(twoMap.get(BUKRS.toUpperCase()+PRCTR.toUpperCase())==null){
							subList2.add(orderSetId);
								// 编号
							subList2.add(pSelfId);
								// 名称
							subList2.add(LTEXT);
								// parentid
							subList2.add(bSelfId);
								// childid
							subList2.add(aSelfId);
								//layer
							subList2.add("2");
							//corcode
							subList2.add(PRCTR);
								// 结束时间
							subList2.add(DateUtils.getSqlDate("9999-12-31 00:00:00","yyyy-MM-dd HH:mm:ss"));
								// 开始时间
							subList2.add(DateUtils.getSqlDate("1949-10-01 00:00:00","yyyy-MM-dd HH:mm:ss"));
							dataList.add(subList2);
							twoMap.put(BUKRS.toUpperCase()+PRCTR.toUpperCase(), t2+"");
							t2++;
						}
						if(map.get(BUKRS.toUpperCase()+PRCTR.toUpperCase()+AUFNR.toUpperCase())==null)
						{
							subList3.add(orderSetId);
								// 编号
							subList3.add(aSelfId);
								// 名称
							subList3.add(KTEXT);
								// parentid
							subList3.add(pSelfId);
								// childid
							subList3.add(aSelfId);
								//layer
							subList3.add("3");
							//corcode
							subList3.add(AUFNR);
								// 结束时间
							subList3.add(DateUtils.getSqlDate("9999-12-31 00:00:00","yyyy-MM-dd HH:mm:ss"));
								// 开始时间
							subList3.add(DateUtils.getSqlDate("1949-10-01 00:00:00","yyyy-MM-dd HH:mm:ss"));
							dataList.add(subList3);
							map.put(BUKRS.toUpperCase()+PRCTR.toUpperCase()+AUFNR.toUpperCase(), t3+"");
							t3++;
						}
						
					}
					if(dataList.size()>0){
						this.deleteCode(conn, orderSetId);
						this.insertData(dataList, conn,orderSetId);
					}
				
					
				}else if("TKostl".equalsIgnoreCase(element.getName())){//成本中心
					ArrayList dataList = new ArrayList();
					List itemlist = element.getChildren("item");
					HashMap onemap = new HashMap();
					HashMap twoMap = new HashMap();
					HashMap map = new HashMap();
					int t1=1;
					int t2=1;
					int t3=1;
					for(int j=0;j<itemlist.size();j++){
						Element itemElement = (Element)itemlist.get(j);
						String BUKRS=itemElement.getChildText("BUKRS");
						String BUTXT=itemElement.getChildText("BUTXT");
						String PRCTR=itemElement.getChildText("PRCTR");
						String LTEXT=itemElement.getChildText("LTEXT");
						String KOSTL=itemElement.getChildText("KOSTL");
						String KLTEXT=itemElement.getChildText("KLTEXT");
						ArrayList subList = new ArrayList();
						ArrayList subList2=new ArrayList();
						ArrayList subList3=new ArrayList();
						int bId=1;
						int pId=1;
						int kId=1;
						if(onemap.get(BUKRS.toUpperCase())!=null){
							bId=Integer.parseInt((String)onemap.get(BUKRS.toUpperCase()));
						}else{
							bId=t1;
						}
						String bSelfId=bId+"";
						if(bId<=9) {
							bSelfId="0"+bId;
						}
						if(twoMap.get(BUKRS.toUpperCase()+PRCTR.toUpperCase())!=null){
							pId=Integer.parseInt((String)twoMap.get(BUKRS.toUpperCase()+PRCTR.toUpperCase()));
						}else{
							pId=t2;
						}
						String pSelfId=pId+"";
						if(pId<=9) {
							pSelfId="0"+pId;
						}
						pSelfId=bSelfId+pSelfId;
						if(map.get(BUKRS.toUpperCase()+PRCTR.toUpperCase()+KOSTL.toUpperCase())!=null){
							kId=Integer.parseInt((String)map.get(BUKRS.toUpperCase()+PRCTR.toUpperCase()+KOSTL.toUpperCase()));
						}else {
							kId=t3;
						}
						String kSelfId=kId+"";
						if(kId<=9) {
							kSelfId="0"+kId;
						}
						kSelfId=pSelfId+kSelfId;
						if(onemap.get(BUKRS.toUpperCase())==null){
							
							subList.add(costcenterSetId);
							// 编号
							subList.add(bSelfId);
							// 名称
							subList.add(BUTXT);
							// parentid
							subList.add(bSelfId);
							// childid
							subList.add(pSelfId);
							//layer
							subList.add("1");
							//corcode
							subList.add(BUKRS);
							// 结束时间
							subList.add(DateUtils.getSqlDate("9999-12-31 00:00:00","yyyy-MM-dd HH:mm:ss"));
							// 开始时间
							subList.add(DateUtils.getSqlDate("1949-10-01 00:00:00","yyyy-MM-dd HH:mm:ss"));
							dataList.add(subList);
							onemap.put(BUKRS.toUpperCase(), t1+"");
							t1++;
						}
						if(twoMap.get(BUKRS.toUpperCase()+PRCTR.toUpperCase())==null){
							subList2.add(costcenterSetId);
								// 编号
							subList2.add(pSelfId);
								// 名称
							subList2.add(LTEXT);
								// parentid
							subList2.add(bSelfId);
								// childid
							subList2.add(kSelfId);
								//layer
							subList2.add("2");
							//corcode
							subList2.add(PRCTR);
								// 结束时间
							subList2.add(DateUtils.getSqlDate("9999-12-31 00:00:00","yyyy-MM-dd HH:mm:ss"));
								// 开始时间
							subList2.add(DateUtils.getSqlDate("1949-10-01 00:00:00","yyyy-MM-dd HH:mm:ss"));
							dataList.add(subList2);
							twoMap.put(BUKRS.toUpperCase()+PRCTR.toUpperCase(), t2+"");
							t2++;
						}
						if(map.get(BUKRS.toUpperCase()+PRCTR.toUpperCase()+KOSTL.toUpperCase())==null){
							subList3.add(costcenterSetId);
								// 编号
							subList3.add(kSelfId);
								// 名称
							subList3.add(KLTEXT);
								// parentid
							subList3.add(pSelfId);
								// childid
							subList3.add(kSelfId);
								//layer
							subList3.add("3");
							//corcode
							subList3.add(KOSTL);
								// 结束时间
							subList3.add(DateUtils.getSqlDate("9999-12-31 00:00:00","yyyy-MM-dd HH:mm:ss"));
								// 开始时间
							subList3.add(DateUtils.getSqlDate("1949-10-01 00:00:00","yyyy-MM-dd HH:mm:ss"));
							dataList.add(subList3);
							map.put(BUKRS.toUpperCase()+PRCTR.toUpperCase()+KOSTL.toUpperCase(), t3+"");
							t3++;
						}
					}
					if(dataList.size()>0){
						this.deleteCode(conn, costcenterSetId);
						this.insertData(dataList, conn,costcenterSetId);
					}
				}else if("TPrctr".equalsIgnoreCase(element.getName())){//利润中心
					ArrayList dataList = new ArrayList();
					List itemlist = element.getChildren("item");
					HashMap map = new HashMap();
					ArrayList oneLayerList = new ArrayList();
					HashMap amap = new HashMap();
					int t1=1;
					int t2=1;
					for(int j=0;j<itemlist.size();j++){
						Element itemElement = (Element)itemlist.get(j);
						String BUKRS=itemElement.getChildText("BUKRS");
						String BUTXT=itemElement.getChildText("BUTXT");
						String PRCTR=itemElement.getChildText("PRCTR");
						String LTEXT=itemElement.getChildText("LTEXT");
						ArrayList subList = new ArrayList();
						ArrayList subList2=new ArrayList();
						int bId=1;
						int pId=1;
						if(map.get(BUKRS.toUpperCase())!=null){
							bId=Integer.parseInt((String)map.get(BUKRS.toUpperCase()));
						}else{
							bId=t1;
						}
						String bSelfId=bId+"";
						if(bId<=9) {
							bSelfId="0"+bId;
						}
						
						if(amap.get(BUKRS.toUpperCase()+PRCTR.toUpperCase())!=null){
							pId=Integer.parseInt((String)amap.get(BUKRS.toUpperCase()+PRCTR.toUpperCase()));
						}else{
							pId=t2;
						}
						String pSelfId=pId+"";
						if(pId<=9) {
							pSelfId="0"+pId;
						}
						pSelfId=bSelfId+pSelfId;
						if(map.get(BUKRS.toUpperCase())==null){
							
							subList.add(profitcenterSetId);
							// 编号
							subList.add(bSelfId);
							// 名称
							subList.add(BUTXT);
							// parentid
							subList.add(bSelfId);
							// childid
							subList.add(pSelfId);
							//layer
							subList.add("1");
							//corcode
							subList.add(BUKRS);
							// 结束时间
							subList.add(DateUtils.getSqlDate("9999-12-31 00:00:00","yyyy-MM-dd HH:mm:ss"));
							// 开始时间
							subList.add(DateUtils.getSqlDate("1949-10-01 00:00:00","yyyy-MM-dd HH:mm:ss"));
							dataList.add(subList);
							map.put(BUKRS.toUpperCase(), t1+"");
							t1++;
						}
						if(amap.get(BUKRS.toUpperCase()+PRCTR.toUpperCase())==null){
							subList2.add(profitcenterSetId);
								// 编号
							subList2.add(pSelfId);
							// 名称
							subList2.add(LTEXT);
								// parentid
							subList2.add(bSelfId);
								// childid
							subList2.add(pSelfId);
								//layer
							subList2.add("2");
							//corcode
							subList2.add(PRCTR);
								// 结束时间
							subList2.add(DateUtils.getSqlDate("9999-12-31 00:00:00","yyyy-MM-dd HH:mm:ss"));
								// 开始时间
							subList2.add(DateUtils.getSqlDate("1949-10-01 00:00:00","yyyy-MM-dd HH:mm:ss"));
							dataList.add(subList2);
							amap.put(BUKRS.toUpperCase()+PRCTR.toUpperCase(), t2+"");
							t2++;
						}
					}
					if(dataList.size()>0){
						this.deleteCode(conn, profitcenterSetId);
						this.insertData(dataList, conn,profitcenterSetId);
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			flag=e.getMessage();
		}
		return flag;
	}
	public void insertData(ArrayList dataList,Connection conn,String codesetid){
		try{
			String sql = "insert into codeitem(codesetid, codeitemid, codeitemdesc,parentid,childid,invalid,layer,corcode,end_date,start_date)"
				+ "values(?,?,?,?,?,1,?,?,?,?)";
			ContentDAO dao = new ContentDAO(conn);
			/*for(int i=0;i<dataList.size();i++){
				ArrayList list = (ArrayList)dataList.get(i);
				System.out.println(list.toString());
			}*/
			dao.batchInsert(sql, dataList);
		}catch(Exception e){
			e.printStackTrace();
			log.error("插入代码类"+codesetid+"数据失败! 错误原因："+e.getMessage());
		}
	}
	public void deleteCode(Connection conn,String codesetid){
		try{
			ContentDAO dao  = new ContentDAO(conn);
			dao.delete("delete from codeitem where UPPER(codesetid)='"+codesetid.toUpperCase()+"'",new ArrayList());
		}catch(Exception e){
			e.printStackTrace();
			log.error("删除代码类"+codesetid+"失败！错误原因："+e.getMessage());
		}
	}
}
