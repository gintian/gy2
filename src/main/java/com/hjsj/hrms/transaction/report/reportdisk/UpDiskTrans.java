package com.hjsj.hrms.transaction.report.reportdisk;

import com.hjsj.hrms.interfaces.decryptor.Des;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

public class UpDiskTrans extends IBusiness {

	public void execute() throws GeneralException {
		FileOutputStream fileOut = null;
		try
		{
			String operateObject=(String)this.getFormHM().get("operateObject");
//			 用户输入的填报单位code
			String input_unitcode=(String)this.getFormHM().get("unitcode");
//			 用户输入的填报单位name 上报盘的文件名
			String input_unitname=(String)this.getFormHM().get("unitname");
//			生成上报盘的报表	
			String tabid_str=(String)this.getFormHM().get("tabid");
			tabid_str = PubFunc.keyWord_reback(tabid_str);//add by wangchaoqun on 2014-9-15 
			//add by wangchaoqun on 2014-9-29 begin 判断生成报盘时是否有该表权限
			if(tabid_str!=null&&tabid_str.trim().length()>0){
				String[] tabids = tabid_str.substring(1).split("/");
				for(int i=0; i<tabids.length; i++){
					boolean haveResource = false;
					if(this.userView!=null&&userView.isHaveResource(com.hrms.hjsj.sys.IResourceConstant.REPORT ,tabids[i])){
						haveResource = true;
					}
					if(!haveResource){
						throw new GeneralException(ResourceFactory.getProperty("report_collect.info5"));
					}
				}
				
			}
			//add by wangchaoqun on 2014-9-29 end
//			当前用户名
			String username=(String)this.getFormHM().get("username");
//			用户实际对应的unitcode
			String db_unitcode=(String)this.getFormHM().get("db_unitcode");
//			报表汇总中上报( 包含基层单位1 /只报汇总单位2 )
			String scope=(String)this.getFormHM().get("scope");
			input_unitcode=SafeCode.decode(input_unitcode);
			username=SafeCode.decode(username);
			input_unitname=SafeCode.decode(input_unitname);
			db_unitcode=SafeCode.decode(db_unitcode);


			// 1：编辑没上报表(单一表) 2：编辑上报后的表(可以有子单位)
			boolean contain_child = true;		
			if ("1".equals(operateObject)) {//编辑过程中的上报
					contain_child = false;
			}
			if ("2".equals(operateObject)) {
					contain_child = true;
			}

		  // response.setHeader("Content-Disposition","attachment;   filename=\"" + input_unitname + ".txt\"");
				//要上报的报表列表
			String[] tabid_array = tabid_str.split("/");
			if("2".equals(scope)){
					//与编辑报表过程中上报相同
					contain_child = false;
			}
			String outName="upDisk_"+PubFunc.getStrg()+".rpx";
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outName);
			UpDiskDownLoad.unitUpDisk(this.getFrameconn(), input_unitcode, input_unitname, username,
						tabid_array, contain_child,fileOut,
						db_unitcode);
			outName = PubFunc.encrypt(outName);  //add by wangchaoqun on 2014-9-15  加密下载文件
			this.getFormHM().put("outName",outName);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(fileOut);
		}

	}

	
	/**
	 * 
	 * @param con 数据库链接
	 * @param input_unitcode  用户输入的填表单位编码
	 * @param input_unitname  用户输入的填表单位名称
	 * @param username        当前用户名
	 * @param tabid_array     上报的报表集合
	 * @param contain_child   false 编辑报表中上报 true 报表汇总中报表上报
	 * @param outstream		  输出流
	 * @param dbunitcode	  当前用户对应的填表单位
	 */
	public static void unitUpDisk(Connection con, String input_unitcode,
			String input_unitname, String username, String[] tabid_array,
			boolean contain_child, OutputStream outstream, String dbunitcode) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			
			//当前用户对应的填表单位集合(包括子节点)
			List unitlist = UpDisk.getUserUnitCode(con, username, contain_child);
			
			// 当前报送的是哪个填报单位
			String db_unitcode = (String) unitlist.get(0);		
			db_unitcode = dbunitcode;
			
			
			
			boolean flg = false;//顶层节点(units)为false,设置单位编码与单位名称
			UpDisk updisk = null;			
			UpDisk.tabidArray = tabid_array; //要上报的单位集合
			
			Element element = doc.createElement("units");
			doc.appendChild(element);
			
			Iterator iterat = unitlist.iterator();
			
			while (iterat.hasNext()) {				
				// XML文档用来存储所有报表结点<reports>
				Document doc_reports = null;
				
				//规范单位编码
				String unitcode = (String) iterat.next();		
				
				String replace_unitcode = unitcode.replaceFirst(db_unitcode,input_unitcode);			
				String parentcode = UpDisk.getParentUnitcode(con, unitcode);							
				parentcode = parentcode.replaceFirst(db_unitcode,input_unitcode);
				
				for (int i = 0; i < tabid_array.length; i++) {
					if ("".equals(tabid_array[i])){
						continue;
					}
					String unitname = "";
					if (!flg) {//units节点的上报单位名称
						unitname = input_unitname;//用户输入的上报单位名称
					} else {
						unitname = UpDisk.getUnitName(con, unitcode);
					}
					updisk = new UpDisk(con, tabid_array[i], unitname , replace_unitcode, 
							parentcode, doc_reports, unitcode);				
					doc_reports = updisk.createUpDisk();
				}
				
				NodeList nodelist = doc_reports.getElementsByTagName("hrp_reports");
				Node hrp_reports = nodelist.item(0);

				// 将数据库中的填报单位ID转换后写入结点
				if (!flg) {
					//<units unitcode="11" unitname="tttt">
					element.setAttribute("unitcode", replace_unitcode);
					element.setAttribute("unitname", input_unitname);
					flg = true;
				}
				element.appendChild(doc.importNode(hrp_reports, true));
			}
			
			try {
				//CipherBox cipherBox = new CipherBox("Cipher_h_j_s_j");
				Des des = new Des();
				/*注掉原因：Transformer是抽象类，没有weblogic.jar 会出问题
				 * Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.ENCODING, "GB2312");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				StringWriter stringWriter = new StringWriter();
				transformer.transform(new DOMSource(doc), new StreamResult(stringWriter));
				//System.out.println(stringWriter.toString());
				outstream.write(stringWriter.toString().getBytes());
				*/
				
				XMLSerializer serializer = new XMLSerializer();
		    	ByteArrayOutputStream output=new ByteArrayOutputStream();
		    	serializer.setOutputByteStream(output);
		        //Insert your PipedOutputStream here instead of System.out!			
				OutputFormat out=new OutputFormat();
				out.setEncoding("UTF-8");
				serializer.setOutputFormat(out);
				serializer.serialize(doc);
			    String outputstr=new String(output.toByteArray(),"GB2312");		    
			    byte[] b= outputstr.getBytes();
			    outstream.write(b,0,b.length);
				// 输出加密文本
				// outstream.write(stringWriter.toString().getBytes());
				//outstream.write(des.EncryStr(stringWriter.toString(),"Cipher_h_j_s_j"));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
