package com.hjsj.hrms.module.template.signature.transaction;

import com.hjsj.hrms.module.template.signature.businessobject.SignatureBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class AddSignatureTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		RowSet rset = null; 
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			SignatureBo bo = new SignatureBo(this.frameconn,this.userView);
			String flag = (String) this.getFormHM().get("flag");
			String password = (String) this.getFormHM().get("password");
			String name = (String) this.getFormHM().get("name");//全称
			String userid = (String) this.getFormHM().get("userid");//用户名
			String b0110 = (String) this.getFormHM().get("b0110");
			String e01a1 = (String) this.getFormHM().get("e01a1");
			String e0122 = (String) this.getFormHM().get("e0122");
			String usertype = (String) this.getFormHM().get("usertype");
			ArrayList imgList = (ArrayList) this.getFormHM().get("imgList");
			String hardwareid = (String) this.getFormHM().get("hardwareid");

			if("1".equals(usertype)) {//业务用户
				if("0".equals(flag)) {
					userid = PubFunc.decrypt(userid);
					ArrayList params = new ArrayList();
					String sql ="select nbase,a0100,fullname from operuser where username=?";
					params.add(userid);
					rset= dao.search(sql,params);
					String nbase = "";
					String a0100 = "";
					String fullname = "";
					if(rset.next()) {
						nbase = rset.getString("nbase");
						a0100 = rset.getString("a0100");
						fullname = rset.getString("fullname");
					}
					if(StringUtils.isNotBlank(nbase)&&StringUtils.isNotBlank(a0100)) {
						params.clear();
						String tablename = nbase+"a01";
						sql = "select b0110,e0122,e01a1,a0101 from "+tablename+" where a0100=?";
						params.add(a0100);
						rset= dao.search(sql,params);
						if(rset.next()) {
							b0110 = rset.getString("b0110");
							e01a1 = rset.getString("e01a1");
							e0122 = rset.getString("e0122");
							String a0101 = rset.getString("a0101");
							if(StringUtils.isBlank(fullname)) {
								name = userid+"("+a0101+")";
							}else
								name = userid+"("+fullname+")";
						}
					}else {
						if(StringUtils.isBlank(fullname)) {
							name = userid;
						}else
							name = userid+"("+fullname+")";
					}
				}
			}
			RecordVo vo = new RecordVo("signature");
			if("0".equals(flag)) {//新增
				if("2".equals(usertype))
					userid = PubFunc.decrypt(userid);
				//判断此用户是否已经添加过印章
				String signatureid = "";
				boolean isUpdate = false;
				ArrayList params = new ArrayList();
				String sql = "select signatureid from signature where username=?";
				params.add(userid);
				rset = dao.search(sql,params);
				if(rset.next()) {
					isUpdate = true;
					signatureid = rset.getInt("signatureid")+"";
				}else {
					signatureid=bo.getMaxEitId();
				}
				ArrayList list  = new ArrayList();
				vo.setString("signatureid", signatureid);
				String ext_param = getExt_param(imgList,hardwareid);
				vo.setString("username", userid);
				vo.setString("password", password);
				vo.setString("usertype", usertype);
				vo.setString("fullname", name);
				vo.setString("b0110", "2".equals(usertype)?PubFunc.decrypt(b0110):b0110);
				vo.setString("e01a1", "2".equals(usertype)?PubFunc.decrypt(e01a1):e01a1);
				vo.setString("e0122", "2".equals(usertype)?PubFunc.decrypt(e0122):e0122);
				vo.setString("ext_param", ext_param);
				vo.setString("hardwareid", hardwareid);
				if(StringUtils.isNotBlank(hardwareid)) {//先将hardwareid对应数据清除hardwareid
					//更新之前绑定U盾的图片xml
					String search = "select ext_param from signature where hardwareid='"+hardwareid+"'";
					rset = dao.search(search);
					if(rset.next()) {
						String extparam_old = rset.getString("ext_param");
						//更新之前绑定U盾的图片xml
						updateOldXml(extparam_old,hardwareid,dao);
					}
					String sql_ = "update signature set hardwareid='' where hardwareid='"+hardwareid+"'";
					dao.update(sql_);
				}
				if(!isUpdate) {
					dao.addValueObject(vo);
				}else {
					dao.updateValueObject(vo);
				}
				this.getFormHM().put("savetodisklist", list);
			}else {//修改
				String file_modify = (String) this.getFormHM().get("file_modify");
				String password_modify = (String) this.getFormHM().get("password_modify");
				String filename_modify = (String) this.getFormHM().get("filename_modify");
				String hardware_modify = (String) this.getFormHM().get("hardware_modify");
				String signatureid = (String) this.getFormHM().get("signatureid");
				String delMarkId = (String) this.getFormHM().get("delMarkId");
				vo.setInt("signatureid", Integer.parseInt(signatureid));
				vo = dao.findByPrimaryKey(vo);
				boolean isSaveFile = false;
				if((StringUtils.isNotBlank(file_modify)&&"true".equals(file_modify))||(StringUtils.isNotBlank(filename_modify)&&"true".equals(filename_modify))) {
					String ext_param = getExt_param(imgList,hardwareid);
					vo.setString("ext_param", ext_param);
					isSaveFile = true;
					if(StringUtils.isNotEmpty(delMarkId)) {
						String[] delMarkids=delMarkId.split(",",-1);
						for(String id:delMarkids) {
							if(StringUtils.isNotEmpty(id)) {
								VfsService.deleteFile(this.userView.getUserName(), id);
							}
						}
					}
				}
				if(StringUtils.isNotBlank(password_modify)&&"true".equals(password_modify)) {
					vo.setString("password", password);
				}
				if(StringUtils.isNotBlank(hardware_modify)&&"true".equals(hardware_modify)) {
					vo.setString("hardwareid", hardwareid);
					//更新之前绑定U盾的图片xml
					String search = "select ext_param from signature where hardwareid='"+hardwareid+"'";
					rset = dao.search(search);
					if(rset.next()) {
						String extparam_old = rset.getString("ext_param");
						//更新之前绑定U盾的图片xml
						updateOldXml(extparam_old,hardwareid,dao);
					}
					String sql_ = "update signature set hardwareid='' where hardwareid='"+hardwareid+"'";
					dao.update(sql_);
					if(!isSaveFile) {
						String ext_param = getExt_param(imgList,hardwareid);
						vo.setString("ext_param", ext_param);
					}
				}
				dao.updateValueObject(vo);
			}
			//获得列表签章文件的图片高宽
			ArrayList imghwlist = bo.getImgHW();
			this.getFormHM().put("imghwlist", imghwlist);
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rset);
		}
	}

	private void updateOldXml(String ext_param, String hardwareid, ContentDAO dao) {
		RowSet rset = null;
		try {
			if (!"".equals(ext_param)){
				Document doc=PubFunc.generateDom(ext_param);
				Element element=null;
				XMLOutputter outputter = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				element = doc.getRootElement();
		        List childlist = element.getChildren("item");
		        for(int i=0;i<childlist.size();i++) {
		        	Element element2=(Element)childlist.get(i);
					String MarkData = element2.getAttributeValue("MarkData");
					if(StringUtils.isNotBlank(hardwareid)) {
						MarkData = MarkData.replace("`", "aA/bZDq");
						int index = MarkData.indexOf("aA/bZDq");
						if(index!=-1) {
							String hardwareid_ = MarkData.substring(0,index);
							hardwareid_ = PubFunc.decrypt(hardwareid_);
							//if(hardwareid_.equals(hardwareid)) {
								MarkData = MarkData.substring(index+7,MarkData.length());
							//}
						}
						element2.setAttribute("MarkData", MarkData);
					}
		        }
				ext_param = outputter.outputString(doc);
				String sql = "select signatureid from signature where hardwareid='"+hardwareid+"'";
				rset = dao.search(sql);
				String signatureid = "";
				if(rset.next()) {
					signatureid = rset.getString("signatureid");
				}
				if(StringUtils.isNotBlank(signatureid)) {
					RecordVo vo = new RecordVo("signature");
					vo.setString("signatureid", signatureid);
					vo.setString("ext_param", ext_param);
					dao.updateValueObject(vo);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
	}

	private String getExt_param(ArrayList imgList,  String hardwareid) {
		StringBuffer ext_param = new StringBuffer();
		InputStream in=null;
		try {
			if(imgList.size()>0) {
				ext_param.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
				ext_param.append("<params>");
				for(int i=0;i<imgList.size();i++)
				{
					MorphDynaBean mdb=(MorphDynaBean)imgList.get(i); 
					String filename = (String)mdb.get("filename");
					//String path = (String)mdb.get("path");
					String marksize = (String)mdb.get("marksize");
					String markid = (String)mdb.get("markid");
					String markname = (String)mdb.get("markname");
					String marktype = (String)mdb.get("marktype");
					//新增或者修改每次fileid都是最新的不需要判断
					String mark_flag = (String)mdb.get("flag");
					in = VfsService.getFile(markid);
					String datafile_en = "";
					if(in!=null) {
						ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				        byte[] buff = new byte[100];
				        int rc = 0;
				        while ((rc = in.read(buff, 0, 100)) > 0) {
				            byteArrayOutputStream.write(buff, 0, rc);
				        }
				        byte[] buf = byteArrayOutputStream.toByteArray();
						//将文件内容读取到内存中 
			            //base64转码
			            datafile_en = Base64.encodeBase64String(buf);
			            datafile_en=datafile_en.replace(" ", "");
			            if(StringUtils.isNotBlank(hardwareid)) {
			            	datafile_en=PubFunc.encrypt(hardwareid)+"aA/bZDq"+datafile_en;
			            }
			            //in.close();
					}
					//不再存储到本地，直接存储流
					/*if(filename.indexOf(".")==-1&&StringUtils.isNotBlank(filename))
						filename = PubFunc.decrypt(filename);
					String datafile_en = "";
					if("0".equals(mark_flag)) {
						path = path.replace("\\", File.separator).replace("/", File.separator);
						if(path.indexOf(File.separator)==-1&&StringUtils.isNotBlank(path)) {
							path = PubFunc.decrypt(path);
							path = path.replace("\\", File.separator).replace("/", File.separator);
						}
						if(path.endsWith(File.separator))
							path = path+filename;
						else
							path = path+File.separator+filename;
						File file = new File(path);
						if(file.exists()) {
							byte [] fileContent= new byte[(int) file.length()];
							//将文件内容读取到内存中 
				            FileInputStream fis=new FileInputStream(file);
				            fis.read(fileContent);
				            fis.close();
				            //base64转码
				            datafile_en = Base64.encodeBase64String(fileContent);
				            datafile_en=datafile_en.replace(" ", "");
				            if(StringUtils.isNotBlank(hardwareid)) {
				            	datafile_en=PubFunc.encrypt(hardwareid)+"aA/bZDq"+datafile_en;
				            }
						}
					}else {
						datafile_en = PubFunc.keyWord_reback(path);
						if(StringUtils.isNotBlank(hardwareid)) {
							datafile_en = datafile_en.replace("`", "aA/bZDq");
							if(datafile_en.indexOf("aA/bZDq")==-1) {
								datafile_en = PubFunc.encrypt(hardwareid)+"aA/bZDq"+datafile_en;
							}else {
								int index = datafile_en.indexOf("aA/bZDq");
								String MarkData_en = datafile_en.substring(index+7,datafile_en.length());
								datafile_en = PubFunc.encrypt(hardwareid)+"aA/bZDq"+MarkData_en;
							}
						}
					}*/
		            ext_param.append("<item MarkID=\""+markid+"\" MarkName=\""+markname+"\" MarkType=\""+marktype+"\" MarkSize=\""+marksize+"\" MarkData=\""+datafile_en+"\"  />");
				}
				ext_param.append("</params>");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeIoResource(in);
		}
		return ext_param.toString();
	}
}
