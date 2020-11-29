package com.hjsj.hrms.transaction.report.edit_report.receive_report;

import com.hjsj.hrms.interfaces.decryptor.Des;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.sql.RowSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ReceiveReportTrans extends IBusiness {

	 private String clew = "";
	 private String editture = "";

	public void execute() throws GeneralException {
		//System.out.println("接收上报盘开始。。。。。。。。。。。。。");
		// 提示信息
		FormFile form_file = (FormFile) getFormHM().get("file");
		String editflag = (String) getFormHM().get("editflag");
		String b_query = (String) getFormHM().get("b_query");//进入
		String b_save = (String) getFormHM().get("b_save");//处理
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String b_query2 = (String) hm.get("b_query2");//进入
		if(b_query2==null){
			b_query2="2";	
		}
		if(b_query2!=null)
		hm.remove("b_query2");
		ParseXml parseXml = new ParseXml();
		try {
			
			clew="";
			editture="";						
			
			//处理时所需参数
			String input_unitcode = (String) getFormHM().get("unitcode");
			String input_unitname = (String) getFormHM().get("unitname");
			//权限范围下的报表
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			byte[] data=form_file.getFileData();
			String source =new String(data); 
			if(source!=null&&!"".equals(source)){
				this.getFormHM().put("file2", form_file);
			}else{
				form_file=(FormFile) getFormHM().get("file2");
			}
			
			if(editflag!=null&& "1".equals(editflag)){
				String reportTypes = "";   //当前用户负责的报表类别
				String report="";
				String reports="";
				String unitcode="";
				String flag ="";
				StringBuffer sql = new StringBuffer();
				sql.append("select reporttypes,report,unitcode from tt_organization where unitcode = (select unitcode from operuser where username = '");
				sql.append(userView.getUserName());
				sql.append("')");
					this.frowset = dao.search(sql.toString());
					if (this.frowset.next()) {
						flag="1";
						reportTypes = (String) this.frowset.getString("reporttypes");
						reports=Sql_switcher.readMemo(this.frowset,"report");
						unitcode=this.frowset.getString("unitcode");
						if (reportTypes == null || "".equals(reportTypes)) {
							// 用户没有权限操作任何报表
							Exception e = new Exception(ResourceFactory.getProperty("report.usernotreport"));
							clew=ResourceFactory.getProperty("report.usernotreport");
							throw GeneralExceptionHandler.Handle(e);
						} else {
							if (reportTypes.charAt(reportTypes.length() - 1) == ',') {
								reportTypes = reportTypes.substring(0, reportTypes.length() - 1);
							}

						}

					} 
						sql.delete(0, sql.length());
						if("1".equals(flag)){
							if(reports!=null&&reports.endsWith(",")){
								reports = reports.substring(0,reports.length()-1);
							}
							if(reports!=null&&reports.startsWith(",")){
								reports = reports.substring(1,reports.length());
							}
							if(reports!=null&&reports.length()>0&&!reports.startsWith(",")&&!reports.endsWith(","))
								sql.append("select tabid  from tname where tsortid in ("+reportTypes+") and tabid not in ("+reports+")");
							else
								sql.append("select tabid  from tname where tsortid in ("+reportTypes+")");	
						}
						else
							sql.append("select tabid  from tname ");
						this.frowset = dao.search(sql.toString());
						while(this.frowset.next())
			    		{
						
							if(userView.isHaveResource(IResourceConstant.REPORT,this.frowset.getString("tabid")))
							{
								report+=	this.frowset.getString("tabid")+",";
							}
			    		}
						if("".equals(report)&&!userView.isSuper_admin()){
							// 用户没有权限操作任何报表
							Exception e = new Exception(ResourceFactory.getProperty("report.usernotreport"));
							clew=ResourceFactory.getProperty("report.usernotreport");
							throw GeneralExceptionHandler.Handle(e);
						}else{
							report = ","+report;
						}
						
					if(report.length()>0)
						parseXml.setReport(report);
					sql.delete(0, sql.length());
					sql.append("select tabid,name  from tname ");
					this.frowset = dao.search(sql.toString());
					HashMap map = new HashMap();
					while(this.frowset.next())
		    		{
						map.put(this.frowset.getString("tabid"), this.frowset.getString("name"));
		    		}
					parseXml.setOperateObject(editflag);
					parseXml.setMap(map);
					parseXml.setUsername(this.getUserView().getUserName()); 
					String scope = (String) getFormHM().get("scope");
					
					this.receiveXmlSecond(form_file,input_unitcode, input_unitname ,scope,unitcode,b_query2,parseXml);
			}
			else{
				StringBuffer sql = new StringBuffer();
				Calendar d=Calendar.getInstance();
				int yy=d.get(Calendar.YEAR);
				int mm=d.get(Calendar.MONTH)+1;
				int dd=d.get(Calendar.DATE);
				StringBuffer ext_sql = new StringBuffer();
				ext_sql.append(" and ( "+Sql_switcher.year("tt_organization.end_date")+">"+yy);
				ext_sql.append(" or ( "+Sql_switcher.year("tt_organization.end_date")+"="+yy+" and "+Sql_switcher.month("tt_organization.end_date")+">"+mm+" ) ");
				ext_sql.append(" or ( "+Sql_switcher.year("tt_organization.end_date")+"="+yy+" and "+Sql_switcher.month("tt_organization.end_date")+"="+mm+" and "+Sql_switcher.day("tt_organization.end_date")+">="+dd+" ) ) ");
				ext_sql.append(" and ( "+Sql_switcher.year("tt_organization.start_date")+"<"+yy);
				ext_sql.append(" or ( "+Sql_switcher.year("tt_organization.start_date")+"="+yy+" and "+Sql_switcher.month("tt_organization.start_date")+"<"+mm+" ) ");
				ext_sql.append(" or ( "+Sql_switcher.year("tt_organization.start_date")+"="+yy+" and "+Sql_switcher.month("tt_organization.start_date")+"="+mm+" and "+Sql_switcher.day("tt_organization.start_date")+"<="+dd+" ) ) ");	 			
				
				sql.append("select reporttypes,report,unitcode from tt_organization where unitcode = (select unitcode from operuser where username = '");
				sql.append(userView.getUserName());
				sql.append("') "+ext_sql+"");
					this.frowset = dao.search(sql.toString());
					if (this.frowset.next()) {
						
					} else{
						Exception e = new Exception(ResourceFactory.getProperty("report.usernotreport"));
						clew=ResourceFactory.getProperty("report.usernotreport");
						throw GeneralExceptionHandler.Handle(e);	
					}
				parseXml.setOperateObject(""); 
				parseXml.setUsername(this.getUserView().getUserName()); 
				if (!"".equals(b_query)) {//上传
					Map map = this.receiveXmlFirst(form_file);
					//上报盘默认值
					this.getFormHM().put("unitcode", map.get("root_unitcode"));
					this.getFormHM().put("unitname", map.get("root_unitname"));				
				} else if (!"".equals(b_save)) {//保存
					String scope = (String) getFormHM().get("scope");
					
					this.receiveXmlSecond(form_file,input_unitcode, input_unitname ,scope,"","",parseXml);
								
				}
			}
		
		} catch (Exception e) {
			if ("".equals(clew)){
				clew = parseXml.getClew();
				if("".equals(clew)){
				clew = ResourceFactory
						.getProperty("receive_report.receive_error");
			e.printStackTrace();
				}
			}
			if((editflag!=null&& "1".equals(editflag)) || (!"".equals(b_save))){
				
			}else{
				throw GeneralExceptionHandler.Handle(e);	
			}
		} finally {
			if ("".equals(clew)){
				clew = parseXml.getClew();
				if ("".equals(clew)){
			clew = ResourceFactory.getProperty("label.common.success");	
				}
			}
			this.getFormHM().put("clew", clew);
			String dxt = (String)hm.get("returnvalue");
			if(dxt!=null&&!"dxt".equals(dxt))
				hm.remove("returnvalue");
			if(dxt==null)
				dxt="";
			this.getFormHM().put("returnflag", dxt);
			if(editflag!=null&& "1".equals(editflag)){
				if("1".equals(editture)){
					this.getFormHM().put("clew", clew);
				//throw new GeneralException(clew);
				}else{
					if(!clew.equals(ResourceFactory.getProperty("label.common.success")))
						this.getFormHM().put("clew", clew);
						//throw new GeneralException(clew);
				}
			}
		}
	}
	/**
	 * 接收上报盘第一步
	 * @param form_file 上报盘文件
	 * @return 
	 * @throws Exception
	 */
	public Map receiveXmlFirst(FormFile form_file) throws Exception {
		//System.out.println("接收第一步。。。。");
		HashMap map = new HashMap();

		//Des des = new Des();
		//String source = des.DecryStr(form_file.getFileData(), "Cipher_h_j_s_j");//解密
		String source=new String(form_file.getFileData());
		StringReader sr = new StringReader(source);
		InputSource isource = new InputSource(sr);		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(isource);		
		
		NodeList nodelist = doc.getElementsByTagName("units");
		
		//上报盘xml中的根节点上报单位的unitcode
		String root_unitcode = "";
		String root_unitname = "";
		if (nodelist.getLength() > 0) {
			Element e = (Element) nodelist.item(0);
			root_unitcode = e.getAttribute("unitcode");
			root_unitname = e.getAttribute("unitname");
		} else {
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					ResourceFactory.getProperty("receive_report.xmlvalid"), "",
					""));
		}
		
		map.put("root_unitcode", root_unitcode);
		map.put("root_unitname", root_unitname);
		
		return map;
	}
	
	/**
	 * 
	 * @param form_file 上报盘文件
	 * @param input_unitcode 用户输入的填报单位编码
	 * @param input_unitname 用户输入的填报单位名称
	 * @param scope 接收范围 (1接收直属和基层数据,2只接收直属单位数据) 
	 * @return 
	 * @throws Exception
	 */
	public void receiveXmlSecond(FormFile form_file,String input_unitcode, 
			String input_unitname ,String scope,String unitcode,String b_query2,ParseXml parseXml) throws Exception {
		//System.out.println("接收第二步。。。。。。");
		Des des = new Des();
		
		byte[] data=form_file.getFileData();
		String source =new String(data);   //des.DecryStr(form_file.getFileData(), "Cipher_h_j_s_j");//解密	
		//System.out.println(source);
		StringReader sr = new StringReader(source);
		InputSource isource = new InputSource(sr);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(isource);
		
		NodeList nodelist = doc.getElementsByTagName("units");
		//上报盘xml中的根节点上报单位的unitcode
		String root_unitcode = "";
		//String root_unitname = "";
		if (nodelist.getLength() > 0) {
			Element e = (Element) nodelist.item(0);
			root_unitcode = e.getAttribute("unitcode");
			//root_unitname = e.getAttribute("unitname");
		} else {
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("receive_report.xmlvalid"), "",""));
		}
		parseXml.setUserCode(input_unitcode);		
		if(parseXml.getOperateObject()!=null&& "1".equals(parseXml.getOperateObject())){
			//只接收直属单位数据				
			nodelist = doc.getElementsByTagName("hrp_reports");//hrp_reports 节点集合
			for (int i = 0; i < nodelist.getLength(); i++) {
				Element node = (Element) nodelist.item(i);
				String node_unitcode = node.getAttribute("unitcode");  //xml填报单位编码
				if(b_query2!=null&& "2".equals(b_query2)){
					if(unitcode!=null&&unitcode.length()>0){
					if(!node_unitcode.equals(unitcode)){
						this.getFormHM().put("editvalide", "1");	
						break;
					}
					}
				}
				this.getFormHM().put("editvalide", "");
				editture="1";
				if(node_unitcode.trim().equals(root_unitcode.trim())){//节点单位编码与根节点单位编码相同(直属单位)
					String node_parentcode = node.getAttribute("parented"); //xml父填表单位编码
					parseXml.setCon(this.getFrameconn());
					NodeList hrp_reports_list = doc.getElementsByTagName("hrp_reports");
					parseXml.setHrp_reports((Element) hrp_reports_list.item(0));
					parseXml.setUnitcode(node_unitcode);
					//ParseXml parsexml = new ParseXml(this.getFrameconn(),doc, node_unitcode, node_parentcode);
					parseXml.parseXml();
					break;
				}
				this.getFormHM().put("file2", null);
			}
		
		}else{
			boolean b = this.isUnitcodeExists(this.getFrameconn(),input_unitcode,input_unitname);
			if(!b){
				clew = ResourceFactory.getProperty("edit_report.info2")+"！";
				throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("edit_report.info2")+"！","", ""));
			}else{
				String currentUserUnitCode = ReceiveReportTrans.getUserUnitcode(this.getFrameconn(), userView.getUserName()); 
				boolean bb = this.checkUnitCodeSpace(this.getFrameconn(),currentUserUnitCode,input_unitcode);
				if(!bb){
					clew = ResourceFactory.getProperty("edit_report.info3")+"！";
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("edit_report.info3")+"！","", ""));			
				}else{				
					if("2".equals(scope)){//只接收直属单位数据
						nodelist = doc.getElementsByTagName("hrp_reports");//hrp_reports 节点集合
						for (int i = 0; i < nodelist.getLength(); i++) {
							Element node = (Element) nodelist.item(i);
							String node_unitcode = node.getAttribute("unitcode");  //xml填报单位编码
							if(node_unitcode.trim().equals(root_unitcode.trim())){//节点单位编码与根节点单位编码相同(直属单位)
								String node_parentcode = node.getAttribute("parented"); //xml父填表单位编码	
						
								String replace_unitcode = node_unitcode.replaceFirst(root_unitcode,input_unitcode);
								
								String parentid = this.getUnitcodeParentId(this.getFrameconn(),input_unitcode);
								
								//首先删除原有填报单位
								//ParseXml.deleteUnitcode2(getFrameconn(), replace_unitcode);
								//其次添加新的填报单位
								if(parentid.trim().length()==0)
									parseXml.insertUnitcode(getFrameconn(), replace_unitcode,parentid, input_unitname);
								parseXml.setCon(this.getFrameconn());
								NodeList hrp_reports_list = doc.getElementsByTagName("hrp_reports");
								parseXml.setHrp_reports((Element) hrp_reports_list.item(0));
								parseXml.setUnitcode(node_unitcode);
								//ParseXml parsexml = new ParseXml(this.getFrameconn(),doc, node_unitcode, node_parentcode);
								parseXml.parseXml();
								break;
							}
						}
					}else{
					//////////////////////////////刘/////////////////////////////////////////////////////////
					
						nodelist = doc.getElementsByTagName("hrp_reports");
						for (int i = 0; i < nodelist.getLength(); i++) {
							Element node = (Element) nodelist.item(i);
							
							String node_unitcode = node.getAttribute("unitcode");
							String node_parentcode = node.getAttribute("parented");						
							String node_unitname = node.getAttribute("unitname");
							
							String repalceParentCode = "";
							//格式替换
							String replace_unitcode = node_unitcode.replaceFirst(root_unitcode,input_unitcode);			
							parseXml.setUserCode(input_unitcode);
							
							//将节点 node 添加到此节点的子节点列表的末尾。如果 node 已经存在于树中，则首先移除它。 
							Document report_doc = builder.newDocument();						
							report_doc.appendChild(report_doc.importNode(node, true));
	
							if (node_unitcode.trim().equals(root_unitcode.trim())) {//根节点
								String parentid = this.getUnitcodeParentId(this.getFrameconn(),input_unitcode);					
								//首先删除原有填报单位
								//ParseXml.deleteUnitcode(getFrameconn(), replace_unitcode);
								//其次添加新的填报单位
								if(!parseXml.isUnitCode(getFrameconn(),replace_unitcode))
									parseXml.insertUnitcode(getFrameconn(), replace_unitcode,parentid, input_unitname);
								parseXml.setCon(this.getFrameconn());
								NodeList hrp_reports_list = doc.getElementsByTagName("hrp_reports");
								parseXml.setHrp_reports((Element) hrp_reports_list.item(0));
								parseXml.setUnitcode(node_unitcode);
								//ParseXml parsexml = new ParseXml(getFrameconn(),report_doc, replace_unitcode, node_parentcode);
								parseXml.parseXml();
							} else {
	
								replace_unitcode = node_unitcode.replaceFirst(root_unitcode,input_unitcode);
								repalceParentCode = node_parentcode.replaceFirst(root_unitcode,input_unitcode);
								
								//首先删除原有填报单位
								//ParseXml.deleteUnitcode(getFrameconn(), replace_unitcode);
								//其次添加新的填报单位
								if(!parseXml.isUnitCode(getFrameconn(),replace_unitcode))
									parseXml.insertUnitcode(getFrameconn(), replace_unitcode,repalceParentCode, node_unitname);
								//最后填充数据
								parseXml.setCon(this.getFrameconn());
								NodeList hrp_reports_list = doc.getElementsByTagName("hrp_reports");
								parseXml.setHrp_reports((Element) hrp_reports_list.item(0));
								parseXml.setUnitcode(node_unitcode);
								//ParseXml parsexml = new ParseXml(getFrameconn(), report_doc,replace_unitcode, repalceParentCode);
								parseXml.parseXml();
							}
						}
	
						//////////////////////////////////////////////////////////////////////////////////////
						
					}
				}
			}
		}
	}

	/**
	 * 根据用户名返回填报单位
	 * 
	 * @param con
	 * @param username
	 *            用户名
	 * @return 用户所在填报单位
	 */
	public static String getUserUnitcode(Connection con, String username)
			throws SQLException {
		String unitcode = "";
		ContentDAO dao = new ContentDAO(con);
		RowSet rs = dao
				.search("select unitcode from operuser where UserName = '"
						+ username + "'");
		if (rs != null && rs.next()) {
			unitcode = rs.getString(1);
		}

		return unitcode;
	}

	/**
	 * 判断用户输入的上报单位是否存在
	 * 是否连同用户输入用户名待定?
	 * @param conn
	 * @param inputUnitCode
	 * @param inputUnitName
	 * @return
	 */
	public boolean isUnitcodeExists(Connection conn , String inputUnitCode,String inputUnitName){
		boolean b = false;
		//String sql =" select * from tt_organization where unitcode='" + inputUnitCode +"'";	
		String sql =" select * from tt_organization where unitcode='" 
			+ inputUnitCode +"' and unitname='"+inputUnitName+"'";
		ContentDAO dao = new ContentDAO(conn);
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				b = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return b; 
	}
	
	/**
	 * 判断接收数据的填报单位是否是当前用户对应填报单位管辖范围内的数据
	 * 接收报盘，不应该将数据接收到非我管辖范围内的单位中
	 * ?是不是只要是管辖范围内的填报单位都可以接收?越级接收问题
	 * @param conn
	 * @param parentidFlag 当前用户对应的填报单位编码
	 * @param unitCode 用户输入的填报单位编码
	 * @return
	 */
	public boolean checkUnitCodeSpace(Connection conn ,String parentidFlag, String unitCode){
		boolean b = false;
		String sql="select * from tt_organization where unitcode='" + unitCode +"' and unitcode like '"+ parentidFlag + "%'";
		//String sql="select * from tt_organization where unitcode='" + unitCode +"' and parentid ='"+ parentidFlag + "'";
		ContentDAO dao = new ContentDAO(conn);
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				b = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return b;
		
		
	}
	
	/**
	 * 判断上报盘中的填报单位在用户输入的填报单位下是否存在
	 * @param userCode
	 * @param con
	 * @param unitCode
	 * @return
	 */
	public boolean findChildUnitCode( Connection con , String input_unitcode, String node_unitcode) {
		ContentDAO dao = new ContentDAO(con);
		String sql="select unitcode from tt_organization where unitcode like '"
			+ input_unitcode + "%' and unitcode = '" + node_unitcode + "'";
		try {
			this.frowset = dao.search(sql);

			if(this.frowset.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}
	
	/**
	 * 判断填表单位是否存在子单位
	 * @param conn
	 * @param unitCode
	 * @return
	 */
	public boolean isChildExists(Connection conn , String unitCode){
		boolean b = false;
		String sql="select unitcode from tt_organization where parentid = '"+ unitCode + "' and parentid <> unitcode";
		ContentDAO dao = new ContentDAO(conn);
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				b = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return b;
	}
	
	/**
	 * 获得子单位编码长度
	 * @param conn
	 * @param unitCode
	 * @return
	 */
	public int getChildUnitCodeLength(Connection conn , String unitCode){
		int len = 0;
		String sql="select unitcode from tt_organization where parentid = '"+ unitCode + "' and parentid <> unitcode";
		ContentDAO dao = new ContentDAO(conn);
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				len = this.frowset.getString("unitcode").length();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return len;
	}
	
	/**
	 * 获得上报盘中根节点的首个子节点的长度
	 * @param doc
	 * @param input_unitcode
	 * @param root_unitcode
	 * @return
	 */
	public int getXMLrootChildUnitCodeLength(Document doc, String input_unitcode,String root_unitcode){
		int n = 0;
		int iu = Integer.parseInt(input_unitcode);
		int rn = Integer.parseInt(root_unitcode);
		NodeList nodelist = doc.getElementsByTagName("hrp_reports");
		for (int i = 0; i < nodelist.getLength(); i++) {
			Element node = (Element) nodelist.item(i);			
			String node_unitcode = node.getAttribute("unitcode");  //xml填报单位编码
			String node_parentcode = node.getAttribute("parented"); //xml父填表单位编码	
			int nun = Integer.parseInt(node_unitcode);
			if(node_parentcode.equalsIgnoreCase(root_unitcode)&& !node_unitcode.equalsIgnoreCase(root_unitcode)){
				n = nun - rn +iu;
			}
		}
		return n;
	}
	
	/**
	 * 判断当前填报单位是否有子单位
	 * 
	 * @param con
	 * @param unitCode
	 *            填报单位编码
	 * @return
	 * @throws SQLException
	 */
	public static String[] childExists(Connection con, String unitCode)
			throws SQLException {
		String[] s = new String[2];
		ContentDAO dao = new ContentDAO(con);
		RowSet rs = dao
				.search("select unitcode from tt_organization where parentid = '"
						+ unitCode + "' and parentid <> unitcode");
		if (rs != null && rs.next()) {
			s[0] = new String("true");
			s[1] = String.valueOf(rs.getString("unitcode").length());
			// System.out.println(rs.getString("unitcode").length());
		} else {
			s[0] = new String("false");
		}
		return s;
	}

	/**
	 * 获得用户输入填报单位的父单位
	 * @param conn
	 * @param input_unitcode
	 * @return
	 */
	public String getUnitcodeParentId(Connection conn, String input_unitcode){
		String parentid = "";
		String sql="select parentid from tt_organization where unitcode = '"+ input_unitcode + "'";
	//	System.out.println(sql);
		ContentDAO dao = new ContentDAO(conn);
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				parentid = this.frowset.getString("parentid");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return parentid;
	}
	public String getClew() {
		return clew;
	}
	public void setClew(String clew) {
		this.clew = clew;
	}
	public String getEditture() {
		return editture;
	}
	public void setEditture(String editture) {
		this.editture = editture;
	}
	
}
