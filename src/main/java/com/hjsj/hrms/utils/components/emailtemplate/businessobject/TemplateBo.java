package com.hjsj.hrms.utils.components.emailtemplate.businessobject;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.recruitment.util.RecruitPrivBo;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.VfsFileEntity;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:TemplateBo
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 3, 2015 2:06:58 PM
 * </p>
 * 
 * @author sunming
 * @version 1.0
 */
/**
 * <p>Title:TemplateBo</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 4, 2015 4:37:37 PM</p>
 * @author sunming
 * @version 1.0
 */
public class TemplateBo {
	Connection conn;
	ContentDAO dao;
	UserView userview;
	
	/** 加载公共+上级+本级+下级 */
    public final static int LEVEL_GLOBAL_PARENT_SELF_CHILD = 0;
    /** 加载公共+上级+本级 */
    public final static int LEVEL_GLOBAL_PARENT_SELF = 1;
    /** 加载本级+下级 */
    public final static int LEVEL_SELF_CHILD = 2;
    /** 加载上级 */
    public final static int LEVEL_PARENT = 3;

	public TemplateBo(Connection conn,ContentDAO dao, UserView userview) {
		this.conn = conn;
		this.dao=dao;
		this.userview = userview;

	}


	private ButtonInfo newButton(String text, String id, String handler,
			String icon, String getdata) {
		ButtonInfo button = new ButtonInfo(text, handler);
		if (getdata != null)
			button.setGetData(Boolean.valueOf(getdata).booleanValue());
		if (icon != null)
			button.setIcon(icon);
		if (id != null)
			button.setId(id);
		return button;
	}

	/**
	 * @Title:getButtonList
	 * @Description:查询功能按钮
	 * @param isModule
	 * @return
	 */
	public ArrayList getButtonList(boolean isModule) {
		ArrayList buttonList = new ArrayList();
		if (!isModule) {
//			buttonList.add("-");
//			buttonList.add(newButton("新建", null, "Global.insertEmailTemplate",
//					null, "true"));
			if(userview.hasTheFunction("311060301") || userview.hasTheFunction("326060901"))
				buttonList.add(newButton("新增", null, "Global.insertEmailTemplate",
						null, "true"));
			if(userview.hasTheFunction("311060302") || userview.hasTheFunction("326060902"))
			buttonList.add(newButton("删除", null, "Global.deleteEmailTemplate",
					null, "true"));
		}
        ButtonInfo querybox = new ButtonInfo();
        querybox.setFunctionId("ZP0000002341");
        querybox.setType(ButtonInfo.TYPE_QUERYBOX);
        querybox.setText("请输入模板类型、名称或邮件标题...");
        querybox.setShowPlanBox(false);
        buttonList.add(querybox);
		return buttonList;
	}

	/**
	 * @param isModule
	 * @Title: getColumnList
	 * @Description: 查询列表表头信息
	 * @param
	 * @return
	 * @return ArrayList
	 * @throws GeneralException
	 */
	public ArrayList getColumnList(boolean isModule, String isShowModuleType) throws GeneralException {

		ArrayList list = new ArrayList();
		ArrayList columnList = new ArrayList();
		if(!"1".equals(isShowModuleType))//显示模板类型根据参数控制
			list.add("sub_module"); // 子模块编码
		
		list.add("name"); // 模板名称
		list.add("subject"); // 邮件标题
		list.add("return_address"); // 回复邮件地址
		list.add("content"); // 邮件内容
		list.add("id");
		list.add("b0110"); // 所属机构
		list.add("ownflag");
		list.add("valid");//是否启用

		try {

			for (int i = 0; i < list.size(); i++) {
				FieldItem item = DataDictionary.getFieldItem((String) list
						.get(i));
				ColumnsInfo info = new ColumnsInfo();
				if (item == null && !"1".equals(isShowModuleType)
						&& "sub_module".equalsIgnoreCase((String) list.get(i))) {
					info.setColumnId("sub_module");
					info.setColumnType("A");
					info.setColumnDesc("通知模板类别 ");
					info.setColumnWidth(150);
					info.setRendererFunc("Global.getSubModuleName");
				} else if ("name".equalsIgnoreCase((String) list.get(i))) {
					info.setColumnId("name");
					info.setColumnType("A");
					info.setColumnDesc("模板名称 ");
					info.setColumnWidth(250);
					info.setRendererFunc("Global.editEmailTemplate");
					info.setLocked(true);
				} else if ("subject".equalsIgnoreCase((String) list.get(i))) {
					info.setColumnId("subject");
					info.setColumnType("A");
					info.setColumnDesc("邮件标题 ");
					info.setColumnWidth(300);
					info.setRendererFunc("Global.editEmailTemplate");
				} else if ("return_address".equalsIgnoreCase((String) list
						.get(i))) {
					info.setColumnId("return_address");
					info.setColumnType("A");
					info.setColumnDesc("回复邮件地址");
					info.setColumnWidth(200);
					// info.setRendererFunc("Global.toPositionDetail");
				} else if ("content".equalsIgnoreCase((String) list.get(i))) {
					info.setColumnId("content");
					info.setColumnType("A");
					info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
					info.setColumnDesc("邮件内容 ");
					info.setColumnWidth(300);
				} else if ("b0110"
						.equalsIgnoreCase((String) list.get(i))) {
					info.setColumnId("b0110");
					info.setColumnType("A");
					info.setColumnWidth(150);
					info.setRendererFunc("Global.showCodeItemDesc");
					info.setColumnDesc("所属机构 ");
				} else if ("id".equalsIgnoreCase((String) list.get(i))) {
					info.setColumnId("id");
					info.setColumnType("A");
					info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
					// info.setEncrypted(true);
					info.setColumnDesc("id");
					info.setColumnLength(0);
				} else if ("ownflag".equalsIgnoreCase((String) list.get(i))) {
					info.setColumnId("ownflag");
					info.setColumnType("A");
					//info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
					// info.setEncrypted(true);
					info.setColumnDesc("模板来源");
					info.setRendererFunc("Global.showOwnflag");
					info.setColumnWidth(100);
				}else if ("valid".equalsIgnoreCase((String) list.get(i))) {
					info.setColumnId("valid");
					info.setColumnType("A");
					info.setColumnDesc("启用");
					info.setTextAlign("center");
					info.setRendererFunc("Global.addvalid");
					info.setColumnWidth(50);
				} else {
					throw new GeneralException("列表头定义错误");
				}

				columnList.add(info);
			}// for end

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		return columnList;
	}
	/**
	 * @Title:getQueryTemplateSql
	 * @Description:列表页面的sql
	 * @param tempList
	 * @param text
	 * @param unitB0110
	 * @param b0110Email
	 * @return
	 * @throws GeneralException
	 */
	public String getQueryTemplateSql( ArrayList text,
			String unitB0110, String b0110Email, String opt) throws GeneralException {
		StringBuffer str_sql = new StringBuffer();
		try {
			str_sql.append("select sub_module,Name,nModule,Subject,Content,Address,return_address,id,b0110,ownflag, ");
			str_sql.append(Sql_switcher.isnull("valid", "1"));
			str_sql.append(" valid from email_name");
			//招聘模板
			str_sql.append(" where nModule=");
			str_sql.append(opt);
			
			//管理范围权限
			str_sql.append(" and ").append(this.getPrivWhr(this.userview, "B0110", this.LEVEL_GLOBAL_PARENT_SELF_CHILD,opt));
			//过滤条件
			if (text != null && text.size() > 0) {
			    str_sql.append(" and (");
			    ArrayList tempList  = new ArrayList();
			    tempList.add("1");
			    for (int i = 0; i < text.size(); i++) {
                    String aStr = (String) text.get(i);
                    if(i==0)
                        str_sql.append("Name like '%" + aStr + "%' or Subject like '%" + aStr + "%' ");
                    else 
                        str_sql.append(" or Name like '%" + aStr + "%' or Subject like '%" + aStr + "%' ");
                    
                    aStr = aStr.trim().toLowerCase();
                    if("接受职位申请通知".indexOf(aStr)>=0){
                        tempList.add("10");
                    }
                    if("拒绝职位申请通知".indexOf(aStr)>=0){
                        tempList.add("11");
                    }
                    if("面试安排通知（申请人）".indexOf(aStr)>=0){
                        tempList.add("20");
                    }
                    if("面试安排通知（面试官）".indexOf(aStr)>=0){
                        tempList.add("30");
                    }
                    if("面试通知（通过）".indexOf(aStr)>=0){
                        tempList.add("40");
                    }
                    if("面试通知（淘汰）".indexOf(aStr)>=0){
                        tempList.add("50");
                    }
                    if("Offer".toLowerCase().indexOf(aStr)>=0){
                        tempList.add("60");
                    }
                    if("入职通知（管理人员）".indexOf(aStr)>=0){
                        tempList.add("70");
                    }
                    if("简历评价通知（评价人）".indexOf(aStr)>=0){
                        tempList.add("80");
                    }
                }
				for (int i = 0; i < tempList.size(); i++) {
					String sub = (String) tempList.get(i);
					str_sql.append(" or sub_module = "
							+ Integer.parseInt(sub.toString()) + "");
				}

				str_sql.append(")");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return str_sql.toString();

	}


	/**
	 * @Title:addEmailField
	 * @Description:保存邮件模板选择的项目
	 * @param templateId
	 * @param fieldList
	 * @param flag
	 */
	public void addEmailField(int templateId, ArrayList fieldList, int flag) {
		try {
			HashMap map = isHaveThisRecord(templateId);
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer();
			ArrayList list = new ArrayList();
			ArrayList alist = new ArrayList();
			if (fieldList == null || fieldList.size() == 0)
				return;
			for (int i = 0; i < fieldList.size(); i++) {
				String s = SafeCode
						.decode(((String) fieldList.get(i) == null || "".equals((String) fieldList
								.get(i))) ? "" : (String) fieldList
								.get(i));
				if (s == null || s.trim().length() == 0) {
					continue;
				}
				String[] arr = s.split("`");
				RecordVo vo = new RecordVo("email_field");
				vo.setInt("id", templateId);// arr[0]
				vo.setInt("fieldid", Integer.parseInt(arr[1]));
				vo.setString("fieldtitle", arr[2]);
				vo.setString("fieldtype", arr[3].toUpperCase());
				String fd = (arr[4] == null || "".equals(arr[4]) ? "" : arr[4]
						.replaceAll("#", "\""));
				if (Integer.parseInt(arr[10]) == 1)// formula
				{
					fd = PubFunc.keyWord_reback(arr[4]).replaceAll("#", "\"");
				}
				vo.setString("fieldcontent", fd);
				vo.setInt("dateformat", Integer.parseInt(arr[5]));
				vo.setInt("fieldlen", Integer.parseInt(arr[6]));
				vo.setInt("ndec", Integer.parseInt(arr[7]));
				vo.setString("codeset", arr[8].toUpperCase());
				vo.setString("fieldset", arr[9]);
				vo.setString("nflag", arr[10]);
				if (flag != 1 && map != null) {
					if (map.get(arr[1]) != null) {
						sql
								.append("update email_field set fieldtitle=?,fieldtype='"
										+ arr[3].toUpperCase() + "',");
						sql.append("fieldcontent=?,dateformat=" + arr[5]
								+ ",fieldlen=" + arr[6] + ",ndec=" + arr[7]);
						sql.append(",codeset='" + arr[8].toUpperCase() + "',");
						sql.append("fieldset='" + arr[9].toUpperCase() + "',");
						sql.append("nflag=" + arr[10]);
						sql.append(" where id=" + templateId + " and fieldid="
								+ arr[1]);
						ArrayList updateList = new ArrayList();
						updateList.add(arr[2]);
						updateList.add(arr[4] == null || "".equals(arr[4]) ? ""
								: arr[4]);
						dao.update(sql.toString(), updateList);
						sql.setLength(0);
					} else {
						alist.add(vo);
					}
				} else {
					list.add(vo);
				}
			}
			if (flag == 1) {
				dao.addValueObject(list);
			} else {
				if (alist != null && alist.size() > 0) {
					dao.addValueObject(alist);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Title:isHaveThisRecord
	 * @Description:用于判断数据库中是否已经存在前台传来的邮件模板项目,存在更新,不存在插
	 * @param id
	 * @return
	 */
	public HashMap isHaveThisRecord(int id/* ,int fieldid */) {
		HashMap map = new HashMap();
		try {
			String sql = "select fieldid from email_field where id=" + id/*
																			 * +"
																			 * and
																			 * fieldid="+fieldid
																			 */
					+ " order by fieldid";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while (rs.next()) {
				map.put(rs.getString("fieldid"), rs.getString("fieldid"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;

	}

	/**
	 * @Title:getTemplateId
	 * @Description:取得模板id,最大+1
	 * @return
	 */
	public int getTemplateId() {
		int n = 0;
		try {
			String sql = "select max(id) from email_name";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while (rs.next()) {
				n = rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return n + 1;
	}

	/**
	 * @Title:getTemplateFieldId
	 * @Description:取得邮件模板项目的id 规则 最大+1
	 * @param template_id
	 * @return
	 */
	public int getTemplateFieldId(int template_id) {
		int n = 0;
		try {
			String sql = "select max(fieldid) from email_field where id="
					+ template_id;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while (rs.next()) {
				n = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return n + 1;

	}

	/**
	 * @Title:getAttachList
	 * @Description:取得模板的附件列表
	 * @param templateId
	 * @return
	 */
	public ArrayList getAttachList(String templateId) {
		ArrayList list = new ArrayList();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from email_attach where id=");
			sql.append(templateId);
			sql.append(" order by attach_id");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql.toString());
			VfsFileEntity fileEntity = null;
			while (rs.next()) {
				fileEntity = VfsService.getFileEntity(rs.getString("fileid"));
				String filename = rs.getString("server_filename");
				HashMap bean = new HashMap();
				bean.put("id", PubFunc.encrypt(rs.getString("attach_id")));
				bean.put("filename", rs.getString("fileid"));
				bean.put("localname", filename);
				bean.put("size", (fileEntity==null? 0 : fileEntity.getFilesize()/1024)+"KB");
				bean.put("path",rs.getString("fileid"));
				list.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	public double div(double v1, double v2, int scale) throws GeneralException {
		if (scale < 0) {
			throw GeneralExceptionHandler.Handle(new Exception(
					" The scale must be a positive integer or zero"));
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public int compare(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.compareTo(b2);
	}
	/**
	 * 获取配置的文件存放根目录
	 * @return
	 * @throws GeneralException
	 */
	public String  getRootDir() throws GeneralException{
  	  ConstantXml constantXml = new ConstantXml(this.conn,"FILEPATH_PARAM");
        String RootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
        if ((RootDir==null) || ("".equals(RootDir))){
      	  throw new GeneralException("没有配置多媒体存储路径！");
        }   
        
        RootDir=RootDir.replace("\\",File.separator);          
        if (!RootDir.endsWith(File.separator)) 
      	  RootDir =RootDir+File.separator;          
        
        RootDir=RootDir+"multimedia"+File.separator;
        return RootDir;
  }

	/**
	 * @Title:getAttachId
	 * @Description:取附件id，最大加一
	 * @return
	 */
	private int getAttachId() {
		int n = 1;
		try {
			String sql = "select max(attach_id) from email_attach ";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while (rs.next()) {
				n = rs.getInt(1);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return n;
	}
	/**
	 * 查询数据库中指定模板所有已存在文件 
	 * @param templateId
	 * @param server_filename
	 * @return
	 */
	public HashMap getAllBaseFile(String templateId){
		HashMap map = new HashMap();
		try{
			String sql="select * from email_attach where id='"+templateId+"' order by attach_id desc";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null; 
			rs = dao.search(sql);
			int key = 0;
			while(rs.next()){
				map.put(""+key+"", rs.getString("fileid"));
				key++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 删除email_attach中的上传文件
	 * @param templateId
	 * @param server_filename
	 */
	public void deleteEmailAttach(String templateId,String fileId){
		String sql="";
		ResultSet rs=null;
		ContentDAO dao = new ContentDAO(this.conn);
		try{
			/**
			 * 删除文件
			 */
			sql="select * from email_attach where id=? and fileid = ?";
			ArrayList<String> list = new ArrayList<String>();
			list.add(templateId);
			list.add(fileId);
			rs = dao.search(sql, list);
			if(rs.next()){
				VfsService.deleteFile(userview.getUserName(), fileId);
			}
			sql="delete from email_attach where id=? and fileid = ?";
			dao.update(sql, list);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 删除所有记录
	 * @param templateId
	 */
	public void deleteAllEmailAttach(String templateId){
		String sql="";
		ContentDAO dao = new ContentDAO(this.conn);
		try{
			/**
			 * 删除文件
			 */
			deleteDictory(templateId);
			/**
			 * 删除记录
			 */
			sql="delete from email_attach where id='"+templateId+"'";
			dao.update(sql);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 删除附件
	 * @param templateId模板id
	 */
	private void deleteDictory(String templateId) {
		String sql="select fileid from email_attach where id=?";
		ArrayList<String> value = new ArrayList<String>();
		value.add(templateId);
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search(sql, value);
			while(rs.next()) {
					VfsService.deleteFile(userview.getUserName(), rs.getString("fileid"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
	}
	/**
	 * @Title:insertEmail_attach
	 * @Description:把附件基本信息保存到数据库
	 * @param templateId
	 * @param fileid
	 * @param fileName
	 */
	public void insertEmail_attach(String templateId, String fileid,
			String fileName,String server_filename) {
		try {
			int index = fileName.lastIndexOf(".");
			String ext = fileName.substring(index + 1, fileName.length());
			StringBuffer sql = new StringBuffer();
			sql.append("insert into email_attach(id,attach_id,filename,extname,server_filename,fileid) values(?,?,?,?,?,?)");
			ArrayList<Object> list = new ArrayList<Object>();
			list.add(templateId);
			list.add(this.getAttachId()+1);
			list.add(fileName);
			list.add(ext);
			list.add(server_filename);
			list.add(fileid);
			ContentDAO dao = new ContentDAO(this.conn);
			dao.insert(sql.toString(), list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Title:updateEmail_attach
	 * @Description:文件存到数据库
	 * @param file
	 * @param attach_id
	 */
	public void updateEmail_attach(File file, String attach_id) {
		byte[] data = null;
		Blob blob = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RecordVo vo = new RecordVo("email_attach");
			vo.setInt("attach_id", Integer.parseInt(attach_id));
			RecordVo a_vo = dao.findByPrimaryKey(vo);

			switch (Sql_switcher.searchDbServer()) {
			case Constant.ORACEL:
				break;
			default:
				data = getFileData(file);
				a_vo.setObject("attach", data);
				break;
			}
			// dao.updateValueObject(a_vo);
			// dao.updateValueObject(a_vo);
			if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
				blob = getOracleBlob(file, attach_id);
				a_vo.setObject("attach", blob);
			}
			dao.updateValueObject(a_vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			data = null;
			blob = null;
		}

	}
	/**
	 * 读取文件二进制数据 
	 * @param file
	 * @return
	 */
	public byte[] getFileData(File file){
		InputStream in = null;
		StringBuffer sb=new StringBuffer();
        try {
            // 一次读一个字节
            in = new FileInputStream(file);
            byte[] buf = new byte[1024];
            while((in.read(buf))!=-1){
                sb.append(new String(buf));    
                buf=new byte[1024];//重新生成，避免和上次读取的数据重复
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
			PubFunc.closeResource(in);
		}
        return sb.toString().getBytes(); 
	}

	/**
	 * @Title:getOracleBlob
	 * @Description:oracle得到blob字段
	 * @param file
	 * @param attach_id
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Blob getOracleBlob(File file, String attach_id)
			throws FileNotFoundException, IOException {
		StringBuffer strSearch = new StringBuffer();
		strSearch.append("select attach from ");
		strSearch.append("email_attach ");
		strSearch.append(" where attach_id=");
		strSearch.append(attach_id);
		strSearch.append(" FOR UPDATE");

		StringBuffer strInsert = new StringBuffer();
		strInsert.append("update  ");
		strInsert.append("email_attach");
		strInsert.append(" set attach=EMPTY_BLOB() where attach_id=");
		strInsert.append(attach_id);
		OracleBlobUtils blobutils = new OracleBlobUtils(this.conn);
		Blob blob = null;
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			blob = blobutils.readBlob(strSearch.toString(), strInsert
					.toString(), is);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeIoResource(is);
		}
		return blob;
	}
	
	/**
	 * @Title:deleteAttach
	 * @Description:删除附件
	 * @param attach_id
	 */
	public void deleteAttach(String attach_id) {
		try {
			String sql = "delete from email_attach where attach_id="
					+ attach_id;
			ContentDAO dao = new ContentDAO(this.conn);
			dao.delete(sql, new ArrayList());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 使用VFS管理文件不再需要移动文件
	 * 复制文件
	 * @param path  源文件目录（不带文件名）这里指的是上传文件的根路径+每个模块指定目录
	 * @param destSrc   需要增加的目录
	 * @param flag	0:复制        1：剪切 
	 * @param srcname   源文件名称,不为空的时候只复制名称一样的文件 
	 */
	@Deprecated
	public void copyFile(String path,String[] destSrc,String flag,String srcname,String destname){
		try{
			File srcFile = new File(path);
			if(srcFile.exists()){//源文件目录存在
				String filename = srcFile.getName();
				String filePath = srcFile.getPath();
				StringBuffer destPath = new StringBuffer(filePath);
				for (int i = 0; i < destSrc.length; i++) {
					destPath.append(srcFile.separator+destSrc[i]);
				}
				destPath.append(srcFile.separator);
				File destFile = new File(destPath.toString());
				if(!destFile.exists()){//目标文件不存在,创建目录
					destFile.mkdirs();
				}
				File[] files = srcFile.listFiles();  //源目录下所有文件
				for (File file : files)  
				{  
					File tem = new File(destFile.getPath()+File.separator+srcname);
					if(!StringUtils.isEmpty(srcname)){
						if(!file.isDirectory()&&file.getName().equals(srcname)){//只复制当前文件
							FileUtils.copyFileToDirectory(file, destFile);//将文件拷贝到目标目录下
							if("1".equals(flag))
								file.delete();//删除文件
							if(StringUtils.isNotEmpty(destname))//将文件重命名
								tem.renameTo(new File(destFile.getPath()+File.separator+destname));
						}
					}else{
						if(!file.isDirectory()){//复制当前文件夹下所有文件和文件夹
							FileUtils.copyFileToDirectory(file, destFile);//将文件拷贝到目标目录下
							if("1".equals(flag))
								file.delete();//删除文件
							if(StringUtils.isNotEmpty(destname))
								tem.renameTo(new File(destFile.getPath()+File.separator+destname));
						}
					}
				}  
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 查询当前用户权限范围的所有上级
	 * @return
	 */
	public ArrayList getParentTemplates(String opt){
		StringBuffer sql = null;
		ArrayList res = new ArrayList();
		
		RowSet rs = null;
		String where = null;
		//如果是超级管理员则不用考虑权限
		if(this.userview.isSuper_admin() || this.userview.isAdmin())
			return res;
		
		try{
			//获取权限范围
			where = getPrivWhr(this.userview, "B0110", RecruitPrivBo.LEVEL_PARENT, opt);
			
			sql = new StringBuffer("select * from email_name");
			sql.append(" where ").append(where);
			
			rs = dao.search(sql.toString());
			while(rs.next()){
				if(!res.contains(rs.getString("id")))
					res.add(rs.getString("id"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return res;
	}
	
	private String getPrivWhr(UserView userView, String b0110Fld, Integer levelFlag, String opt) {
		String privB0110Str = "";
		StringBuffer sqlWhr = new StringBuffer();
        try {
        	if (userView == null)
        		return "1=2";
        	
        	if (userView.isSuper_admin())
        		return "1=1";
	        b0110Fld = (b0110Fld == null || "".equals(b0110Fld)) ? "b0110" : b0110Fld;
	        levelFlag = (levelFlag == null) ? 0 : levelFlag;
			//取管理范围权限
	        privB0110Str = getB0110(userView, opt);
				
	        String[] privB0110s = privB0110Str.split("`");
	        
	        sqlWhr.append("(");
	        
	        //UN`是全权
	        if ("UN`".equalsIgnoreCase(privB0110Str))
	            return "1=1";
	        
	        for (int i = 0; i < privB0110s.length; i++) {
	            String privB0110 = privB0110s[i].trim();
	            if ("".equals(privB0110))
	                continue;
	            
	            if (privB0110.startsWith("UN") || privB0110.startsWith("UM") || privB0110.startsWith("@K"))
	                privB0110 = privB0110.substring(2);
	            
	            if ("HJSJ".equals(privB0110))
	                privB0110 = "";            
	            
	            if (sqlWhr.length() > 1)
	                sqlWhr.append(" OR ");
	            
	            sqlWhr.append(" 1=2 ");
	            
	          //公共流程
	            if (levelFlag != LEVEL_SELF_CHILD && levelFlag != LEVEL_PARENT ) {
	                sqlWhr.append(" OR ");
	                sqlWhr.append(b0110Fld).append("=''");
	                sqlWhr.append(" or ").append(b0110Fld).append(" is null");
	                sqlWhr.append(" or ").append(b0110Fld).append("='UN`'");
	                sqlWhr.append(" or ").append(b0110Fld).append("='HJSJ'");
	            }
	            
	            if (levelFlag != LEVEL_SELF_CHILD) {
	                //上级
	                sqlWhr.append(" OR ");
	                sqlWhr.append("(");
	                sqlWhr.append(b0110Fld).append("=").append(Sql_switcher.left("'" + privB0110 + "'", Sql_switcher.length(b0110Fld)));
	                sqlWhr.append(" and ").append(b0110Fld).append("<>'").append(privB0110).append("'");
	                sqlWhr.append(")");
	            }
	            
	            //下级
	            if (levelFlag != LEVEL_GLOBAL_PARENT_SELF && levelFlag != LEVEL_PARENT) {
	                sqlWhr.append(" OR ");
	                sqlWhr.append("(");
	                sqlWhr.append(b0110Fld).append(" LIKE '").append(privB0110).append("%'");
	                sqlWhr.append(" and ").append(b0110Fld).append("<>'").append(privB0110).append("'");
	                sqlWhr.append(")");
	            }
	            
	            //本级
	            if (levelFlag != LEVEL_PARENT) {
	                sqlWhr.append(" OR ");
	                sqlWhr.append(b0110Fld).append("='").append(privB0110).append("'");
	            }
	            
	            
	        }
	        sqlWhr.append(")");
		} catch (GeneralException e) {
			e.printStackTrace();
		}
        return sqlWhr.toString();
	}
	
	/**
	 * 获取个模块的管理范围权限
	 * @param userView
	 * @param opt
	 * @return
	 * @throws GeneralException
	 */
	public String getB0110(UserView userView, String opt) throws GeneralException {
        String b0110 = "";
        try {
            String codeid = "";
            if (userView.isSuper_admin() || "1".equals(userView.getGroupId()))
                return "HJSJ";
            
            if("7".equals(opt))//招聘
            	codeid = userView.getUnitIdByBusi("7");
            else if("9".equals(opt))//绩效
            	codeid = userView.getUnitIdByBusi("5");
            if (codeid == null || "".equals(codeid) || "UN".equalsIgnoreCase(codeid)
                    || "UM`".equalsIgnoreCase(codeid) || "@K`".equalsIgnoreCase(codeid)) {
            	if("7".equals(opt))//招聘
            		throw new Exception("您没有招聘模块的管理范围权限！请联系管理员。");
            	else if("9".equals(opt))
            		throw new Exception("您没有绩效模块的管理范围权限！请联系管理员。");
            }

            if (codeid.trim().length() < 3)
                return "HJSJ";
            
            if (codeid.indexOf("`") == -1) {
                if (codeid.startsWith("UN") || codeid.startsWith("UM")) {
                    b0110 = codeid.substring(2);
                } else {
                    b0110 = codeid;
                }
                
                return b0110;
            } 
            
            String[] temps = codeid.split("`");
            codeid = "";
            for (int i = 0; i < temps.length; i++) {
                if (codeid.startsWith("UN") || codeid.startsWith("UM")) {
                    b0110 += temps[i].substring(2) + "`";
                } else {
                    b0110 += temps[i] + "`";
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new Exception("您没有该模块的管理范围权限！请联系管理员。"));
        }
        return b0110;
    }
}
