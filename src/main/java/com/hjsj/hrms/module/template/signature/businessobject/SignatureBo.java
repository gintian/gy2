package com.hjsj.hrms.module.template.signature.businessobject;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.module.template.utils.javabean.TemplateModuleParam;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;

import javax.imageio.ImageIO;
import javax.sql.RowSet;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class SignatureBo {
	Connection conn;
    ContentDAO dao;
    UserView userview;
    
    public SignatureBo(){
    	
    }
    
    public SignatureBo(Connection conn){
    	this.conn = conn;
    }
    
    public SignatureBo(Connection conn, UserView userview){
    	this.conn = conn;
        this.userview = userview;
        this.dao = new ContentDAO(conn);
    }

	public ArrayList<ColumnsInfo> getColumnList() {
		ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
        try {
            // 印章编号
            ColumnsInfo idColumn = getColumnsInfo("signatureid", "编号", 60, "N");
            idColumn.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            idColumn.setTextAlign("center");
            columnTmp.add(idColumn);

            // 单位
            ColumnsInfo b0110 = getColumnsInfo("b0110", "单位名称", 180, "A"); 
            b0110.setCodesetId("UN");
            columnTmp.add(b0110);

            // 部门
            ColumnsInfo E0122 = getColumnsInfo("e0122", "部门", 180, "A");
            E0122.setCodesetId("UM");
            columnTmp.add(E0122);

            // 岗位
            ColumnsInfo E01a1 = getColumnsInfo("e01a1", "岗位名称", 180, "A");
            E01a1.setCodesetId("@K");
            columnTmp.add(E01a1);
            
            // 姓名
            ColumnsInfo fullname = getColumnsInfo("fullname", "帐号(姓名)", 150, "A");
            columnTmp.add(fullname);

            // 用户类型
            ColumnsInfo usertype = getColumnsInfo("usertype", "用户类型", 94, "A");
            ArrayList<CommonData> list = new ArrayList<CommonData>();
            list.add(new CommonData("1", "业务用户"));
            list.add(new CommonData("2", "自助用户"));
            usertype.setOperationData(list);
            columnTmp.add(usertype);
            
            // 印章图片
            ColumnsInfo signatureimg = new ColumnsInfo();
            signatureimg.setColumnDesc("印章图片");
            signatureimg.setRendererFunc("signature_me.showSignatureImgRender");
            signatureimg.setColumnWidth(90);
            signatureimg.setTextAlign("center");
			columnTmp.add(signatureimg);

            // 用户名
            ColumnsInfo username = getColumnsInfo("username", "用户名", 94, "A");
            username.setEncrypted(true);
            username.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            columnTmp.add(username);

            //密码
            ColumnsInfo password = getColumnsInfo("password", "密码", 94, "A");
            password.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            columnTmp.add(password);
            
            //印章xml
            ColumnsInfo ext_param = getColumnsInfo("ext_param", "印章图片xml", 94, "A");
            ext_param.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            columnTmp.add(ext_param);
            
            ColumnsInfo hardwareid = getColumnsInfo("hardwareid", "锁硬件id", 94, "A");
            hardwareid.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            columnTmp.add(hardwareid);
            
            ColumnsInfo isSaveToDisk = getColumnsInfo("issavetodisk", "图片是否存储到U盾", 94, "A");
            isSaveToDisk.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            columnTmp.add(isSaveToDisk);
            
            ColumnsInfo qnaction = new ColumnsInfo();
			qnaction.setColumnDesc("操作");
			qnaction.setRendererFunc("signature_me.actionRender");
			qnaction.setColumnWidth(150);
			qnaction.setTextAlign("center");
			columnTmp.add(qnaction);

        } catch (Exception e) {
            e.printStackTrace();
            GeneralExceptionHandler.Handle(e);
        }
        return columnTmp;
	}
	private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth, String type) {

        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId(columnId);
        columnsInfo.setColumnDesc(columnDesc);
        //columnsInfo.setCodesetId("");// 指标集
        columnsInfo.setColumnType(type);// 类型N|M|A|D
        columnsInfo.setColumnWidth(columnWidth);// 显示列宽
        if ("A".equals(type)) {
            columnsInfo.setCodesetId("0");
        }
        columnsInfo.setDecimalWidth(0);// 小数位

        columnsInfo.setTextAlign("left");
        return columnsInfo;
    }

	public ArrayList getButtonList() {
		ArrayList buttonList  = new ArrayList();
		//if (userview.isSuper_admin() /*|| userview.hasTheFunction("3001H01")*/){
			buttonList.add(newButton("添加印章", null, "signature_me.addSignature(0)", null, "true"));
		//}
		//if (userview.isSuper_admin() /*|| userview.hasTheFunction("3001H02")*/){
			buttonList.add(newButton("删除", null, "signature_me.delSignature(0)", null, "true"));
		//}
		return buttonList;
	}
	private ButtonInfo newButton(String text, String id, String handler, String icon, String getdata) {
        ButtonInfo button = new ButtonInfo(text, handler);
        if (getdata != null)
            button.setGetData(Boolean.valueOf(getdata).booleanValue());
        if (icon != null)
            button.setIcon(icon);
        if (id != null)
            button.setId(id);
        return button;
    }

	public ArrayList getSignatureUrl() {
		RowSet rset = null;
		ArrayList list = new ArrayList();
		try {
			String username = "";
			String na0100 = "";
			String sql = "select password,ext_param,username,hardwareid from signature where ";
			username = userview.getUserName();
			if(StringUtils.isNotBlank(username)&&userview.getStatus()==0) {
				sql+=" username='"+username+"'";
			}
			else if(userview.getStatus()==4) {
				na0100=userview.getDbname()+userview.getA0100();
				if(StringUtils.isNotBlank(na0100)) {
					sql+=" lower(username)='"+na0100.toLowerCase()+"'";
				}
			}
			rset = dao.search(sql);
			while(rset.next()) {
				String ext_param = rset.getString("ext_param");
				String password = rset.getString("password")==null?"":rset.getString("password");
				String username_ = rset.getString("username");
				String hardwareid = rset.getString("hardwareid");
				if (!"".equals(ext_param)){
					Document doc=PubFunc.generateDom(ext_param);
					Element element=null;
					element = doc.getRootElement();
			        List childlist = element.getChildren("item");
			        for(int i=0;i<childlist.size();i++) {
			        	Element element2=(Element)childlist.get(i);
						String MarkID = element2.getAttributeValue("MarkID");
						String MarkName = element2.getAttributeValue("MarkName").replace(",", "，").replace("^", "＾").replace("~", "～").replace("`", "｀");
						String MarkData = element2.getAttributeValue("MarkData");
						if(StringUtils.isNotBlank(hardwareid)) {
							MarkData = MarkData.replace("`", "aA/bZDq");
							int index = MarkData.indexOf("aA/bZDq");
							String hardwareid_ = MarkData.substring(0,index);
							hardwareid_ = PubFunc.decrypt(hardwareid_);
							if(hardwareid_.equals(hardwareid)) {
								MarkData = MarkData.substring(index+7,MarkData.length());
							}
						}
						MarkData=MarkData.replace(" ", "");
						byte [] markbytes = Base64.decodeBase64(MarkData);
						for (int kk = 0; kk < markbytes.length; ++kk) {
				            if (markbytes[kk] < 0) {// 调整异常数据
				            	markbytes[kk] += 256;
				            }
				        }
				        File file = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+MarkID+".jpg");
				        if (!file.exists()) {  
					    	// 生成jpeg图片
				        	FileOutputStream out = null;
				        	try {
				        		out = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+MarkID+".jpg");
						        out.write(markbytes);
						        out.flush();
							} finally {
								PubFunc.closeResource(out);
							}
					        
					    }
				        File tempFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+MarkID+".jpg");
						if(tempFile.exists()) {
						    BufferedImage sourceImg =ImageIO.read(new FileInputStream(tempFile));
						    int imgwidth = sourceImg.getWidth();
						    int imgheight = sourceImg.getHeight();
							list.add(MarkID+'`'+password+'`'+MarkData+"`"+MarkName+"`"+username_+"`"+imgwidth+"`"+imgheight);
						}
			        }
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
		return list;
	}
	/**
	 * 签章回显所用数据
	 * @param signxml
	 * @param tabid 
	 * @return
	 */
	public ArrayList getUserSignatureList(String signxml, String tabid) {
    	Document doc = null;
    	RowSet rset = null;
    	RowSet rset1 = null;
		ArrayList list = new ArrayList();
    	try {
			doc = PubFunc.generateDom(signxml);
			List<Element> elelist = doc.getRootElement().getChildren();
			for(int j = 0; j < elelist.size(); j++){
				Element ele = elelist.get(j);
				String documentid = tabid+"_"+ele.getAttributeValue("DocuemntID");
				List<Element> ele2list = ele.getChildren("item");
				if(ele2list!=null&&ele2list.size()>0){
					for(int k=0;k<ele2list.size();k++){
						Element ele2=(Element)ele2list.get(k);
						String UserName = ele2.getAttributeValue("UserName");
						String signaturehtmlid = ele2.getAttributeValue("SignatureHtmlID");
						String signatureid = ele2.getAttributeValue("SignatureID");
						boolean isExist = false;
						if(StringUtils.isNotBlank(UserName)) {
							String sql = "select password,ext_param,username from signature where ";
							sql+=" username='"+UserName+"'";
							rset = dao.search(sql);
							while(rset.next()) {
								String ext_param = rset.getString("ext_param");
								String password = rset.getString("password")==null?"":rset.getString("password");
								String username_ = rset.getString("username");
								if (!"".equals(ext_param)){
									Document doc1=PubFunc.generateDom(ext_param);
									Element element=null;
									element = doc1.getRootElement();
							        List childlist = element.getChildren("item");
							        for(int i=0;i<childlist.size();i++) {
							        	Element element2=(Element)childlist.get(i);
										String MarkID = element2.getAttributeValue("MarkID");
										String MarkName = element2.getAttributeValue("MarkName").replace(",", "，").replace("^", "＾").replace("~", "～").replace("`", "｀");
										if(signatureid.equals(MarkID)) {
											isExist = true;
											String sql1 = "select caimg from htmlsignature where documentid='"+documentid+"' and signatureid='"+signaturehtmlid+"'";
											rset1 = dao.search(sql1);
											InputStream in = null;
											int imgwidth;
											int imgheight;
											if(rset1.next()) {
												in = rset1.getBinaryStream("caimg");
												if(in!=null) {
													String path = PubFunc.getBlobBase64(rset1,1);
													if(!"".equals(path)) {
														BufferedImage sourceImg = ImageIO.read(in);
														imgwidth = sourceImg.getWidth();
													    imgheight = sourceImg.getHeight();
											            list.add(MarkID+'`'+password+'`'+path+"`"+MarkName+"`"+username_+"`"+imgwidth+"`"+imgheight);
													}
												}
											}
										}
							        }
								}
							}
						}
						if(!isExist) {
							String sql1 = "select caimg from htmlsignature where documentid='"+documentid+"' and signatureid='"+signaturehtmlid+"'";
							rset1 = dao.search(sql1);
							InputStream in = null;
							int imgwidth;
							int imgheight;
							if(rset1.next()) {
								in = rset1.getBinaryStream("caimg");
								if(in!=null) {
									String path = PubFunc.getBlobBase64(rset1,1);
									BufferedImage sourceImg = ImageIO.read(in);
									imgwidth = sourceImg.getWidth();
								    imgheight = sourceImg.getHeight();
						            list.add(signatureid+'`'+""+'`'+path+"`"+signaturehtmlid+"`"+UserName+"`"+imgwidth+"`"+imgheight);
								}
							}
						}
					}
				}
			}
    	}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
			PubFunc.closeDbObj(rset1);
		}
		return list;
	}
    /**
     * 得到主键值
     * @return
     */
    public String getMaxEitId()
    {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from id_factory where sequence_name='signature.signatureid'");
        RowSet rs=null;
        try
        {
            rs=dao.search(sql.toString());
            if(!rs.next())
            {
                StringBuffer insertSQL=new StringBuffer();
                insertSQL.append("insert into id_factory  (sequence_name, sequence_desc, minvalue, maxvalue, auto_increase, increase_order, prefix, suffix, currentid, id_length, increment_O)");
                insertSQL.append(" values ('signature.signatureid', '印章号', 1, 99999999, 1, 1, Null, Null, 0, 8, 1)");
                ArrayList list=new ArrayList();
                dao.insert(insertSQL.toString(),list);              
            }
            IDGenerator idg = new IDGenerator(2, this.conn);
            String file_id = idg.getId("signature.signatureid");
            return  file_id;
        }catch(Exception e){
            return null;
        }finally {
        	PubFunc.closeDbObj(rs);
        }
    }
    /**
     * 删除印章图片前先清除vfs对应的图片
     * @param signatureid
     */
    public void delSingatureMarkid(String signatureid)throws Exception {
    	RowSet rs=null;
    	try {
    		rs=dao.search("select ext_param from signature where signatureID=?", Arrays.asList(signatureid));
    		if(rs.next()) {
    			String ext_param = rs.getString(1);
    			if(StringUtils.isEmpty(ext_param)) {
    				return;
    			}
    			Document doc = PubFunc.generateDom(ext_param);
    			Element element=null;
				element = doc.getRootElement();
		        List<Element> childlist = element.getChildren("item");
		        for(Element el:childlist) {
		        	String markid=el.getAttributeValue("MarkID");
		        	VfsService.deleteFile(this.userview.getUserName(), markid);
		        }
    		}
		} finally {
			PubFunc.closeDbObj(rs);
		}
    }
    
	/**
	 * 获得列表签章文件的图片高宽
	 * @return
	 */
	public ArrayList getImgHW() {
	    RowSet rs=null;
	    ArrayList imghwlist = new ArrayList();
	    InputStream in=null;
	    try
	    {
	        String datasql = "select ext_param,signatureid,hardwareid from signature";
	        rs=dao.search(datasql);
	        while(rs.next())
	        {
	        	String ext_param = rs.getString("ext_param");
	        	String hardwareid = rs.getString("hardwareid");
	        	int signatureid = rs.getInt("signatureid");
				if (StringUtils.isNotEmpty(ext_param)){
					Document doc=PubFunc.generateDom(ext_param);
					Element element=null;
					element = doc.getRootElement();
			        List childlist = element.getChildren("item");
		        	Element element2=(Element)childlist.get(0);
					String MarkData = element2.getAttributeValue("MarkData");
					String MarkID = element2.getAttributeValue("MarkID");
					if(StringUtils.isNotBlank(hardwareid)) {
						MarkData = MarkData.replace("`", "aA/bZDq");
						int index = MarkData.indexOf("aA/bZDq");
						String hardwareid_ = MarkData.substring(0,index);
						hardwareid_ = PubFunc.decrypt(hardwareid_);
						if(hardwareid_.equals(hardwareid)) {
							MarkData = MarkData.substring(index+7,MarkData.length());
						}
					}
					MarkData=MarkData.replace(" ", "");
					byte [] markbytes = Base64.decodeBase64(MarkData);
					for (int kk = 0; kk < markbytes.length; ++kk) {
			            if (markbytes[kk] < 0) {// 调整异常数据
			            	markbytes[kk] += 256;
			            }
			        }
			        /*File file = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+MarkID+".jpg");
			        if (!file.exists()) {  
				    	// 生成jpeg图片
				        FileOutputStream out = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+MarkID+".jpg");
				        out.write(markbytes);
				        out.flush();
				        out.close();
				    }
			        File tempFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+MarkID+".jpg");*/
					in =VfsService.getFile(MarkID);
					if(in!=null) {
						ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				        byte[] buff = new byte[100];
				        int rc = 0;
				        while ((rc = in.read(buff, 0, 100)) > 0) {
				            byteArrayOutputStream.write(buff, 0, rc);
				        }
				        byte[] buf = byteArrayOutputStream.toByteArray();
				        ImageIcon icon=new ImageIcon(buf);
				        int imgwidth = icon.getIconWidth();
					    int imgheight = icon.getIconHeight();
					    imghwlist.add(signatureid+"`"+imgheight+"`"+imgwidth+"`"+MarkData);
					   /* BufferedImage sourceImg =ImageIO.read(new FileInputStream(tempFile));
					    int imgwidth = sourceImg.getWidth();
					    int imgheight = sourceImg.getHeight();
					    imghwlist.add(signatureid+"`"+imgheight+"`"+imgwidth+"`"+MarkData);*/
					}else {
						imghwlist.add(signatureid+"```");
					}
				}else {
					imghwlist.add(signatureid+"```");
				}
	        }
	    }catch(Exception e){
	    	e.printStackTrace();
	    }finally {
	    	PubFunc.closeDbObj(rs);
	    	PubFunc.closeIoResource(in);
	    }
	    return imghwlist;
	}
	/**
	 * 分析图片会被分成几份
	 * @param path
	 * @param eachSize 
	 */
	public String analysisImage(String path, int eachSize) {
		String text = "";
		FileInputStream fis = null;
		try {
			File srcFile = new File(path);
	        /*if(srcFile.length()==0){
	        }*/
	        byte[] fileContent= new byte[(int) srcFile.length()];
	        //将文件内容读取到内存中 
            fis = new FileInputStream(srcFile);
            fis.read(fileContent);
	        //base64转码
            String datafile_en = Base64.encodeBase64String(fileContent);
	        //计算要拆分为多少份 
	        int fileNumber;
	        if(datafile_en.length()%eachSize==0){
	            fileNumber = datafile_en.length()/eachSize;
	        } else{
	            fileNumber = datafile_en.length()/eachSize+1;
	        }
	        for (int i=0;i<fileNumber;i++){
	            String eachContent;
	            if(i!=fileNumber-1){
	                eachContent = datafile_en.substring(eachSize*i, eachSize*(i+1));
	            } else{
	                eachContent = datafile_en.substring(eachSize*i, datafile_en.length());
	            }
	            
	            if(i==0) {
	            	text+=eachContent;
	            }else {
	            	text+="`"+eachContent;
	            }
	        }
		}catch (Exception e) {
            e.printStackTrace();
        }finally {
        	PubFunc.closeResource(fis);
        }
		return text;
	}
	/**
	 * 校验用户与锁是否绑定
	 * @param baningID
	 * @return
	 */
	public boolean getBandingFlag(String baningID) {
		RowSet rset = null;
		boolean bandingflag = false;
		try {
			//解base64
			byte [] baningIDbytes = Base64.decodeBase64(baningID);
			baningID = new String(baningIDbytes);
			String username = this.userview.getUserName();
			if(this.userview.getStatus()==4) {
				username = this.userview.getDbname()+this.userview.getA0100();
			}
			String sql = "select hardwareid from signature where username='"+username+"'";
			rset = dao.search(sql);
			if(rset.next()) {
				String hardwareid = rset.getString("hardwareid");
				String banding_ = PubFunc.encrypt(username)+hardwareid;
				if(baningID.contains(banding_)) {
					bandingflag = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bandingflag;
	}
	/**
	 * 得到用户签章是否存储到U盾
	 * @return
	 */
	public String getSaveToDiskFlag() {
		String isSaveToDisk = "1";
		RowSet rset = null;
		String username = this.userview.getUserName();
		if(this.userview.getStatus()==4) {
			username = this.userview.getDbname()+this.userview.getA0100();
		}
		String sql = "select issavetodisk from signature where username='"+username+"'";
		try {
			rset = dao.search(sql);
			if(rset.next()) {
				isSaveToDisk = rset.getString("issavetodisk");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isSaveToDisk;
	}
	/**
	 * 将签章数据存储到htmlsignature表
	 * @param documentid
	 * @param signaturehtmlid
	 * @param markpath
	 * @param signatureid 
	 * @param isSaveToDisk 
	 */
	public void saveToHtmlSignature(String documentid, String signaturehtmlid, String markpath, String signatureid) {
		RowSet rset = null;
		InputStream photoStream = null;
		PreparedStatement prestmt = null;
		try {
			DbSecurityImpl dbS = new DbSecurityImpl();
			//先将已存在的记录删除
			ArrayList delparams = new ArrayList();
			String delsql = "delete from htmlsignature where signatureid=? and documentid=?";
			delparams.add(signaturehtmlid);
			delparams.add(documentid);
			dao.delete(delsql, delparams);
			//将数据存储
			String strSql = "";
			String username = this.userview.getUserName();
			if(this.userview.getStatus()==4) {
				username = this.userview.getDbname()+this.userview.getA0100();
			}
			if(Sql_switcher.searchDbServer()==2){
	            strSql="insert into HTMLSignature (DocumentID,SignatureID,SignatureSize,caimg,username) values ('"+documentid+"','"+signaturehtmlid+"','',EMPTY_BLOB(),'"+username+"') ";
	        }
	        if(Sql_switcher.searchDbServer()==1){
	        	strSql="insert into HTMLSignature (DocumentID,SignatureID,SignatureSize,caimg,username) values ('"+documentid+"','"+signaturehtmlid+"','',null,'"+username+"') ";
	        }
	        dao.insert(strSql,new ArrayList());
	        //将文件流存到表里
	        String markpath_ = "";
			String sql = "select ext_param,hardwareid from signature where ";
			sql+=" username='"+username+"'";
			rset = dao.search(sql);
			while(rset.next()) {
				String ext_param = rset.getString("ext_param");
				String hardwareid = rset.getString("hardwareid");
				if (!"".equals(ext_param)){
					Document doc1=PubFunc.generateDom(ext_param);
					Element element=null;
					element = doc1.getRootElement();
			        List childlist = element.getChildren("item");
			        for(int i=0;i<childlist.size();i++) {
			        	Element element2=(Element)childlist.get(i);
						String MarkID = element2.getAttributeValue("MarkID");
						String MarkData = element2.getAttributeValue("MarkData");
						if(signatureid.equals(MarkID)) {
							if(StringUtils.isNotBlank(hardwareid)) {
								MarkData = MarkData.replace("`", "aA/bZDq");
								int index = MarkData.indexOf("aA/bZDq");
								String hardwareid_ = MarkData.substring(0,index);
								hardwareid_ = PubFunc.decrypt(hardwareid_);
								if(hardwareid_.equals(hardwareid)) {
									MarkData = MarkData.substring(index+7,MarkData.length());
								}
							}
							MarkData=MarkData.replace(" ", "");
							markpath_ = MarkData;
						}
			        }
				}
			}
			byte [] markbytes =Base64.decodeBase64(markpath_);
			for (int kk = 0; kk < markbytes.length; ++kk) {
	            if (markbytes[kk] < 0) {// 调整异常数据
	            	markbytes[kk] += 256;
	            }
	        }
	        File file = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+signatureid+".jpg");
	        if (!file.exists()) {  
	        	FileOutputStream out = null;
		    	// 生成jpeg图片
	        	try {
	        		out = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+signatureid+".jpg");
			        out.write(markbytes);
			        out.flush();
				} finally {
					PubFunc.closeResource(out);
				}
		        
		    }
	        File tempFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+signatureid+".jpg");
		    if (tempFile.exists()) {  
		    	photoStream = new FileInputStream(tempFile);
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				 {
					Blob blob = getOracleBlob(tempFile,"HTMLSignature",signaturehtmlid,documentid);
					ArrayList list = new ArrayList();
					String updatesql = "update htmlsignature set caimg= ?  where  documentid= ? and signatureid= ?";
					list.add(blob);
					list.add(documentid);
					list.add(signaturehtmlid);
					dao.update(updatesql, list);
				 }
			     if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
			     {
			    	 String sql_="update HTMLSignature set caimg=? where signatureid=? and documentid=?";
			    	 prestmt = this.conn.prepareStatement(sql_);
		             prestmt.setBinaryStream(1,photoStream,(int)tempFile.length());
		             prestmt.setString(2,signaturehtmlid);
		             prestmt.setString(3,documentid);
		             dbS.open(this.conn, sql_); 
		             prestmt.executeUpdate();
		            
			     }
		    }	
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
			PubFunc.closeDbObj(prestmt);
			PubFunc.closeResource(photoStream);
			
		}
	}
	private Blob getOracleBlob(File file,String tablename,String id, String documentid) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select caimg from ");
		strSearch.append(tablename);
		strSearch.append(" where signatureid='");
		strSearch.append(id);
		strSearch.append("' and documentid='"+documentid+"'");
		strSearch.append("  FOR UPDATE");
		
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  ");
		strInsert.append(tablename);
		strInsert.append(" set caimg=EMPTY_BLOB() where signatureid='");
		strInsert.append(id);
		strInsert.append("' and documentid='"+documentid+"'");
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.conn);
	    InputStream in = null;
	    Blob blob = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			blob = blobutils.readBlob(strSearch.toString(),strInsert.toString(),in);
		}catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			PubFunc.closeIoResource(in);
		}
		return blob;
	}	
	
	public String getKeysn() {
		String keysn = "";
		RowSet rset = null;
		TemplateModuleParam templateModuleParam = new TemplateModuleParam(conn, userview);
		String keyfield = templateModuleParam.getKey_item();
		ArrayList params = new ArrayList();
		if(StringUtils.isBlank(this.userview.getDbname())) {
			return keysn;
		}
		String sql = "select "+keyfield+" from "+this.userview.getDbname()+"A01 where a0100=?";
		params.add(this.userview.getA0100());
		try {
			rset = dao.search(sql,params);
			if(rset.next()) {
				keysn = rset.getString(keyfield);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rset);
		}
		return keysn;
	}
}
