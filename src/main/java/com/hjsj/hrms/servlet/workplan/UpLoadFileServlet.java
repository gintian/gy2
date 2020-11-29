package com.hjsj.hrms.servlet.workplan;

import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanCommunicationBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanOperationLogBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 简要说明类的作用
 * 其它补充说明
 * <p>Title: UpLoadFileServlet </p>
 * <p>Company: hjsj</p>
 * <p>create time  2014-7-26 上午11:57:53</p>
 * @author guoby
 * @version 1.0
 */
public class UpLoadFileServlet extends HttpServlet {

	//上传文件大小限制 20M
	private final int MAX_SIZE = 1024 * 1024 * 20;

	private Connection conn = null;
	WorkPlanCommunicationBo communicationBo = null;


	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
						  HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String msgContentId = request.getParameter("id");//文字信息输入框id
		String to_name = request.getParameter("to_name");
		String fileId = request.getParameter("fileId"); // 附件来源input编号
		String type = request.getParameter("type"); // 消息来源编号，3 工作总结，2 工作计划；
		String month = request.getParameter("month");//为了决节发布总结沟通消息的邮件通知，月末月份不对的问题。
		String year = request.getParameter("year");//为了决节发布总结沟通消息的邮件通知，月末月份不对的问题。
		String objectid = WorkPlanUtil.decryption(request.getParameter("objectid"));
		// 工作总结 或者 工作计划编号
		String workId=objectid;
		ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory());
		// 解决乱码问题
		uploadHandler.setHeaderEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession();
		UserView userView = (UserView) session.getAttribute(WebConstant.userView);
		/*登陆校验 guodd 2018-11-01*/
		if(userView==null || userView.getHm().containsKey("isEmployee")) {
			out.println("<script>alert('请登录系统！');</script>");
			return;
		}

		FileItem item = null;
		InputStream input = null;
		long fileSize = 0;// 文件大小

		try {
			conn = AdminDb.getConnection();
			communicationBo = new WorkPlanCommunicationBo(this.conn, userView);
			DbWizard dbw=new DbWizard(conn);
			String filePath = "";
			String savePath = "";
			String savePaths = "";
			String msg="";
			String msgId="";
			String submitDate = DateUtils.FormatDate(new Date(),"yyyy-MM-dd HH:mm");
			String responseFileName = "error";
			String responseFileNames = "";
			List items = uploadHandler.parseRequest(request);
			//保存沟通记录前校验附件的合法性，不符合的话，沟通信息也不保存
			for (int i = items.size()-1; i >=0 ; i--) {// 附件
				item = (FileItem) items.get(i);
				if (fileId.equalsIgnoreCase(item.getFieldName()) ){
					filePath = item.getName();
					fileSize = item.getSize();
					// 上传附件到服务器 and 保存文件信息
					String fileName="";
					if (!"".equals(filePath)) {
						// 实现附件上传
						if (fileSize > MAX_SIZE) {
							out.println("{\"result\":\"fileSizeOver\"}");
							return;
						}
						// 截取文件名 fileName= "数据库设计说明—工作计划.doc"
						filePath = filePath.replace("\\", "/");
						fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

						//判断文件后缀是否跟文件类型相符合、文件类型是否在白名单内
						String ext = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
						input = item.getInputStream();
						boolean isOk = FileTypeUtil.isFileTypeEqual(input,ext);
						if(!isOk || !SystemConfig.getAllowExt().contains(","+ext+",") || fileName.split("\\.").length>2) {
							out.println("<script>alert('上传文件为非法文件！');</script>");
							return;
						}
					}
				}
			}
			for (int i = 0; i < items.size(); i++) {// 沟通信息
				item = (FileItem) items.get(i);
				if ("msgContent".equalsIgnoreCase(item.getFieldName())&&"msgContent".equals(msgContentId)){// 沟通信息内容（name='msgContent' 这个文本域）
					byte[] array = item.get();//使用字节数组获取文本域的沟通信息
					msg = new String(array, "UTF-8");
					if("填写对工作的意见和要求，或者汇报任务的执行情况。".equals(msg)){
						msg="";
					}
					if(StringUtils.isNotBlank(msg)){
						msg = PubFunc.hireKeyWord_filter(msg);
					}
					// 保存沟通信息
					msgId= this.saveMessage(type,workId, msg, to_name,submitDate,userView,month,year);
					if (null == msgId || "".equals(msgId)) {
						out.println("{\"result\":\"saveMsgError\"}");
						return;
					}
				}else if ("msgContent1".equalsIgnoreCase(item.getFieldName())&&"write2".equals(msgContentId)){// 沟通信息内容（name='msgContent1' 这个文本域）
					byte[] array = item.get();//使用字节数组获取文本域的沟通信息
					msg = new String(array, "UTF-8");
					if(StringUtils.isNotBlank(msg)){
						msg = PubFunc.hireKeyWord_filter(msg);
					}
					// 保存沟通信息
					msgId= this.saveMessage(type,workId, msg, to_name,submitDate,userView,month,year);
					if (null == msgId || "".equals(msgId)) {
						out.println("{\"result\":\"saveMsgError\"}");
						return;
					}
				}
			}
			//上面已经校验过文件的合法性，此处就不再校验了
			for (int i = items.size()-1; i >=0 ; i--) {// 附件
				item = (FileItem) items.get(i);
				if (fileId.equalsIgnoreCase(item.getFieldName()) ){
					filePath = item.getName();
					// 上传附件到服务器 and 保存文件信息
					String fileName="";
					if (!"".equals(filePath)) {
						// 实现附件上传
						// 截取文件名 fileName= "数据库设计说明—工作计划.doc"
						filePath = filePath.replace("\\", "/");
						fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
						// Line 166 的代码会关掉流 所以需要重新打开流  haosl
						input = item.getInputStream();
						// 上传到VFS
						String username = userView.getUserName() ;
						VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.doc;
						VfsModulesEnum vfsModulesEnum = VfsModulesEnum.MB;
						VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.other;
						String CategoryGuidKey = "";
						String filetag = "";
						//使用VFS实现文件上传，保存文件id
						String fileid  = VfsService.addFile(username,vfsFiletypeEnum,vfsModulesEnum,vfsCategoryEnum,CategoryGuidKey,input,fileName,filetag,false);
						//相对路径,下载的时候获取多媒体路径加上相对路径wusy
						this.saveUpLoadFile(msgId, fileName, userView, fileid);
						//一次可上传多个文件，可能会有多个路径以及多个文件名，中间用逗号分隔
						savePaths = fileid +","+ savePaths;
						responseFileNames = fileName+","+responseFileNames;
					}

				}
			}
			WorkPlanBo pb = new WorkPlanBo(conn, userView);
			PlanTaskBo pbo = new PlanTaskBo(conn, userView);
			String photoUrl = pb.getPhotoPath(userView.getDbname(), userView.getA0100());

			out.print("{\"date\":\"" + submitDate
					+ "\",\"result\":\"success"
					+ "\",\"name\":\"" + userView.getUserFullName()
					+ "\",\"fName\":\"" + responseFileNames
					+ "\",\"photoUrl\":\"" + photoUrl
					+ "\",\"msgId\":\"" + WorkPlanUtil.encryption(msgId)
					+ "\",\"path\":\"" + savePaths+ "\"}");



			// 任务编号和任务进度 add by 刘蒙
			String p0800 = request.getParameter("p0800"); // 任务id
			String p0835 = request.getParameter("p0835"); // 任务进度

			//如果当前任务进度为100%
			if("100".equals(p0835)){
				WorkPlanBo planBo = new WorkPlanBo(this.conn, userView);
				String P0800 = WorkPlanUtil.decryption(p0800);
				//记录完成时间，取系统时间（finishDate）
				//String submitDate = DateUtils.FormatDate(new Date(),"yyyy-MM-dd HH:mm");
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//设置日期格式
				String taskids=planBo.getSubTasks("0",P0800);
				String ids[]=taskids.split(",");
				String sql="";
				//且有子任务，则将所有子任务进度置为100%
				for(int i=0;i<ids.length-1;i++){
					if(ids[i].equals(P0800)){
						continue;
					}
					Date finishDate = null;
					boolean b = false;
					finishDate=df.parse(df.format(new Date()));
					RecordVo subTaskVo = new RecordVo("p08");
					subTaskVo.setInt("p0800", Integer.parseInt(ids[i]));
					subTaskVo = new ContentDAO(conn).findByPrimaryKey(subTaskVo);
					subTaskVo.setString("p0809", "3");
					subTaskVo.setInt("p0835", 100);
					subTaskVo.setDate("finishdate", finishDate);
					if(subTaskVo.getInt("p0833") != 2){
						new ContentDAO(conn).updateValueObject(subTaskVo);
					}
				}
			}
			if (p0800 != null && p0835 != null && p0800.length()>0 && p0835.length()>0
					&&!"0".equals(p0800)) {
				int iP0800 = "".equals(p0800) ? 0 : Integer.parseInt(WorkPlanUtil.decryption(p0800));
				int iP0835 = "".equals(p0835) ? 0 : Integer.parseInt(p0835);
				RecordVo task = pbo.getTask(iP0800);
				int DBiP0835=task.getInt("p0835");
				String uSql ="";
				String p0809 = null;
				switch(iP0835) {
					case 0: p0809 = "1"; break;
					case 100: p0809 = "3"; break;
					default: p0809 = "2";
				}
				// 更新任务进度和任务状态
				if(DBiP0835!=iP0835){
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//设置日期格式
					Date finishDate1 = null;
					//java.sql.Date finish = null;
					boolean b = false;
					if(iP0835==100){
						finishDate1=df.parse(df.format(new Date()));
//						 if(Sql_switcher.searchDbServer() == 2){
//							 finish = new java.sql.Date(finishDate.getTime());
//							 b = true;
//						 }
					}else{
						finishDate1=null;
					}

//					 uSql = "UPDATE P08 SET p0835=?,p0809=?,finishDate=? WHERE p0800=?";
//				     new ContentDAO(conn).update(uSql, Arrays.asList(new Object[] {
//								new Integer(iP0835),
//								p0809,
////								b == true ? finish : finishDate,
//								finishDate1,
//								new Integer(iP0800)
//						}));
					task.setInt("p0835", iP0835);
					task.setString("p0809", p0809);
					task.setDate("finishdate", finishDate1);
					new ContentDAO(conn).updateValueObject(task);
					//记录日志  更新任务进度wusy
					RecordVo p08Vo = new RecordVo("p08");
					p08Vo.setInt("p0800", iP0800);
					try {
						p08Vo = new ContentDAO(conn).findByPrimaryKey(p08Vo);
						WorkPlanBo planBo = new WorkPlanBo(conn, userView);
						planBo.syncP04(WorkPlanUtil.decryption(p0800), "p0419", String.valueOf(iP0835) , "N");//同步目标卡的值 chent 20160414
					} catch (Exception e) {
						e.printStackTrace();
					}

					if("02".equals(p08Vo.getString("p0811")) || "03".equals(p08Vo.getString("p0811"))){
						String logcontent = "将任务完成进度更新为" + iP0835 + "%";
						new WorkPlanOperationLogBo(conn, userView).addLog(iP0800, logcontent);
					}

				}

			}
			pbo.setWorkPlanChangeFlg("true");//工作计划页面变更flg设定为true
		}catch (GeneralException e) {
			e.printStackTrace();
			out.println("<script>alert('信息保存失败，请联系管理员检查多媒体路径配置是否正确！');</script>");
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			WorkPlanUtil.closeDBResource(conn);
			out.close();
			fileSize = 0;
		}

	}

	// 保存文件到服务器
	private String upLoad(String filePath, InputStream input, String savePath) {
		String result = "error";
		OutputStream out = null;
		if ("".equals(input))
			return result;

		// 保存目录
		String sPath = savePath;
		try {
			if (!this.createDir(sPath))
				return result;
			//savePath += "\\" + filePath;
			savePath += File.separator + filePath;//兼容不同OS chent 20160325
			// 存在文件就删除，重新添加
			File file = new File(savePath);
			if (file.isFile() && file.exists())
				file.delete();

			byte[] bt = new byte[1024];
			int read = 0;
			out = new FileOutputStream(savePath);
			while ((read = input.read(bt)) != -1) {
				out.write(bt, 0, read);
			}
			result = "success";
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(out);
			PubFunc.closeResource(input);
		}
		return result;
	}

	// 保存发布的信息
	private String saveMessage(String type, String object_id, String content,
							   String to_name, String submitDate, UserView userView,String month,String year) {

		String nbase = userView.getDbname();
		String a0100 = userView.getA0100();
		String loginName = userView.getUserName();// 创建者用户名
		String userName = userView.getUserFullName();// 创建者姓名

		WorkPlanBo workPlanBo = new WorkPlanBo(conn, userView);
		if("3".equals(type)) {
			workPlanBo.setPeriodMonth(month);
			workPlanBo.setPeriodYear(year);
		}
		workPlanBo.sendDiscussionMsg(type, object_id, content, submitDate, userView);
		return communicationBo.publishMessage(type, object_id, content, submitDate, to_name);
	}

	// 保存上传附件相应信息到数据库
	private void saveUpLoadFile(String object_id, String fileName,UserView userView, String savePath) {

		object_id = communicationBo.getMessageContentId() + "";
		String create_user = userView.getUserName();
		String ext = fileName.substring(fileName.lastIndexOf("."));
		String file_name_old = fileName;

		// 保存文件信息
		communicationBo.saveUpLoadFile("4", object_id, savePath, fileName,file_name_old, ext, create_user);
	}

	/**
	 * 判断改目录是否存在
	 *
	 * @param destDirName 目标目录
	 * @return
	 */
	public boolean createDir(String destDirName) {
		if (!destDirName.endsWith(File.separator))
			destDirName = destDirName + File.separator;

		File dir = new File(destDirName);
		if (dir.exists()) {
			return true;
		}

		// 创建单个目录
		return dir.mkdirs();
	}

}
