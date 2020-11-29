package com.hjsj.hrms.module.jobtitle.reviewfile.businessobject;

import com.hjsj.hrms.module.jobtitle.reviewfile.transaction.ReviewFileTrans;
import com.hjsj.hrms.module.jobtitle.utils.JobtitleUtil;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.swetake.util.Qrcode;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import javax.imageio.ImageIO;
import javax.sql.RowSet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ExportBo {
    private Connection conn = null;
    private UserView userview;

    /**
     * 构造函数
     * 
     * @param conn
     * @param userview
     */
    public ExportBo(Connection conn, UserView userview) {
        this.conn = conn;
        this.userview = userview;
    }
    
    
    /**
     * 20160624
     * @Title: getAllTypeList   
     * @Description: 查询出所有类型(去重)   
     * @param @return 
     * @param @throws GeneralException
     * @param @throws SQLException 
     * @return ArrayList<LazyDynaBean>
     * @author changxy    
     * @throws
     */
    @SuppressWarnings("unchecked")
    public ArrayList<LazyDynaBean> getAllTypeList() throws GeneralException, SQLException{
    	StringBuffer sbf=new StringBuffer();
    	sbf.append("select distinct type from  zc_expert_user order by type desc");
    	ArrayList list=new ArrayList();
    	ContentDAO dao=new ContentDAO(this.conn);
    	RowSet rowset=null;
    	try {
    		rowset=dao.search(sbf.toString());
    		LazyDynaBean bean=null;
    		while(rowset.next()){
    			bean=new LazyDynaBean();
    			bean.set("type", rowset.getString("type"));
    			list.add(bean);
    		}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			rowset.close();
		}
    	return list;
    }
    
    /**
     * 20160624
     * @Title: getProGroup   
     * @Description:查询学科组    
     * @param @param types
     * @param @param W0301
     * @param @return
     * @param @throws GeneralException
     * @param @throws SQLException 
     * @return ArrayList<LazyDynaBean>    
     * @throws
     */
    public ArrayList<LazyDynaBean> getProGroup(String W0301) throws GeneralException, SQLException{
    	StringBuffer sbf=new StringBuffer();
    	sbf.append(" select  group_id,group_name from");
    	sbf.append("  zc_subjectgroup where ");
    	sbf.append(" group_id in (");
    	sbf.append("select distinct(group_id) from w05 where w0301=? and group_id is not null)");
//    	sbf.append("select distinct group_id from zc_expert_user where ");
//    	sbf.append("type='"+types+"' ");
//    	sbf.append("and W0301='"+W0301+"')");
    	List values = new ArrayList();
    	values.add(W0301);
    	ContentDAO dao=new ContentDAO(this.conn);
    	LazyDynaBean lzbean=null;
    	ArrayList<LazyDynaBean> list=new ArrayList<LazyDynaBean>();
    	RowSet rowset=null;
    	try {
    		rowset=dao.search(sbf.toString(),values);
    		while(rowset.next()){
    			lzbean=new LazyDynaBean();
    			lzbean.set("group_id",rowset.getString("group_id"));
    			lzbean.set("group_name",rowset.getString("group_name"));
    			list.add(lzbean);
    		}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			rowset.close();
		}
    	return list;
    }
    
//    /**
//     *  20160624
//     * @Title: getdisPropose   
//     * @Description:去重查询申请人    
//     * @param @param type
//     * @param @param reviewPersonIds
//     * @param @param group_id
//     * @param @param w0301
//     * @param @return
//     * @param @throws GeneralException
//     * @param @throws SQLException
//     * @author changxy 
//     * @return String    
//     * @throws
//     */
//    @SuppressWarnings("unchecked")
//    @Deprecated
//    public String getdisProposes(String type,String reviewPersonIds,String group_id,String w0301) throws GeneralException, SQLException{
//    	
//    	StringBuffer sql = new StringBuffer();
//    	if("1".equals(type)||"3".equals(type)){//分类别统计
//    		sql.append("select distinct W0511,w0501 from ");
//    		sql.append("w05 where W0501 in ("+ reviewPersonIds +")");
//    	}else{
//    		sql.append("select ");
//        	sql.append(" distinct w05.w0511 w0511,w05.w0501");
//        	sql.append(" from zc_expert_user zc");
//        	sql.append(" left join (select W0501,W0507,W0509,W0511,W0513,W0515,W0301 from W05) W05 on zc.W0501=w05.W0501");
//        	sql.append(" left join (select W0301,W0303,W0321 from W03) W03 on zc.W0301=w03.W0301");
//        	sql.append(" where W03.W0301 = W05.W0301 ");
//        if(StringUtils.isNotEmpty(reviewPersonIds))
//        	sql.append(" and w05.w0501 in ("+ reviewPersonIds +")");
//        if("2".equals(type)){
//        	sql.append(" and zc.group_id='"+group_id+"' ");
//        	sql.append(" and zc.W0301='"+w0301+"' ");
//        	}
//        	sql.append(" and type='"+type+"' ");
//    	}
//    		sql.append(" order by w05.w0501");
//    	ContentDAO dao=new ContentDAO(this.conn);
//    	ArrayList<String> list=new ArrayList<String>();
//    	String str="";
//		RowSet row=null;
//    	try {
//    		row=dao.search(sql.toString());
//    		while(row.next()){
//    			if(row.getString("w0511")!=null)
//    				list.add(row.getString("w0511"));
//    		}
//    		if(list.size()>0){
//    			for (int i = 0; i < list.size(); i++) {
//    				if(i==list.size()-1)
//    					str+=list.get(i);
//    				else
//    					str+=list.get(i)+",";
//				}
//    		}
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw GeneralExceptionHandler.Handle(e);
//		}finally{
//			row.close();
//		}
//			return (String) str;
//    }
    /**
     * 
     * @param type
     * @param username
     * @param w0301
     * @param group_id
     * @return
     */
    public String getdisPropose(String type,String username,String w0301,String group_id)throws GeneralException{
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	StringBuffer proposeBuf = new StringBuffer();
    	try {
    		StringBuffer sql = new StringBuffer();
    		sql.append("select w0511 ");
    		sql.append("from W05 w05,zc_expert_user zc ");
    		sql.append("where w05.W0501=zc.w0501 and username=? and zc.W0301=? ");
    		sql.append("order by w05.w0501 DESC");//haosl 20161028
    		
    		List values = new ArrayList();
    		values.add(username);
    		values.add(w0301);
    		
        	rs = dao.search(sql.toString(),values);
        	while(rs.next()){
        		String proposer = rs.getString("w0511");
        		if(StringUtils.isNotEmpty(proposer)){
        			proposeBuf.append(proposer+"、");
        		}
        	}
        	if(proposeBuf.length()>0)
        		return proposeBuf.substring(0, proposeBuf.length()-1);
        	return "";
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
    }
    
    /**
     * 20160624
     * @Title: getAllSheetHeadList   
     * @Description:导出列头内容格式     
     * @param @return 
     * @return ArrayList<LazyDynaBean>
     * @author changxy    
     * @throws
     * 
     */
    /**
     * 
     * @param usetype
     * 			帐号类型    1. 审查帐号   2.投票帐号 
     * @param type
     * 			评审阶段
     * @return
     */
    @SuppressWarnings("unchecked")
    public ArrayList<LazyDynaBean> getAllSheetHeadList(String usetype,String type) {
    	ArrayList headList = new ArrayList();
        HashMap headStyleMap = new HashMap();
        headStyleMap.put("columnWidth",4000);//设置列宽
        int i = 0;    
        headList.add(getAbean("username", ResourceFactory.getProperty("zc_new.zc_reviewfile.expertUsernameType"), 
        		"0", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("pwd", ResourceFactory.getProperty("zc_new.zc_reviewfile.password"), 
        		"0", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("dimensional", "二维码", 
        		"0", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("proposer", "申请人", 
        		"0", "A", "0", 0, 0, i, i++, headStyleMap));
    	if(!"2".equals(usetype)){//usetype为2时是匿名投票，不应该显示专家姓名
	        headList.add(getAbean("w0107", "专家姓名", 
	    			"0", "A", "0", 0, 0, i, i++, headStyleMap));
    	}
        return headList;
    }
    
    /**
     * 2018/04/23
     * @Title: getAllSheetHeadList   
     * @Description:导出列头内容格式     
     * @param @return 
     * @return ArrayList<LazyDynaBean>
     * @author xus   
     * @throws
     * 
     */
    @SuppressWarnings("unchecked")
    public ArrayList<LazyDynaBean> getNewAllSheetHeadList() {
    	ArrayList headList = new ArrayList();
        HashMap headStyleMap = new HashMap();
        headStyleMap.put("columnWidth",4000);//设置列宽
        int i = 0;    
        headList.add(getAbean("groupname", ResourceFactory.getProperty("zc_new.zc_reviewfile.groupName"), 
        		"0", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("proposer", ResourceFactory.getProperty("zc_new.zc_reviewfile.proposer"), 
        		"0", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("username", ResourceFactory.getProperty("zc_new.zc_reviewfile.expertUsernameType"), 
        		"0", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("pwd", ResourceFactory.getProperty("zc_new.zc_reviewfile.password"), 
        		"0", "A", "0", 0, 0, i, i++, headStyleMap));
//        headList.add(getAbean("dimensional", ResourceFactory.getProperty("zc_new.zc_reviewfile.dimensional"), 
//        		"0", "A", "0", 0, 0, i, i++, headStyleMap));
        return headList;
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<LazyDynaBean> getSheetHeadList() {
        ArrayList headList = new ArrayList();
        HashMap headStyleMap = new HashMap();
        headStyleMap.put("columnWidth",4000);//设置列宽
        int i = 0;
        headList.add(getAbean("type", ResourceFactory.getProperty("zc_new.zc_reviewfile.expertType"), 
        		"0", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("username", ResourceFactory.getProperty("zc_new.zc_reviewfile.expertUsernameType"), 
        		"0", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("pwd", ResourceFactory.getProperty("zc_new.zc_reviewfile.password"), 
        		"0", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("state", ResourceFactory.getProperty("zc_new.zc_reviewfile.state"), 
        		"0", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("W0303", ResourceFactory.getProperty("zc_new.zc_reviewfile.meeting"), 
        		"0", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("W0507", ResourceFactory.getProperty("zc_new.zc_reviewfile.companyName"), 
        		"UN", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("W0509", ResourceFactory.getProperty("zc_new.zc_reviewfile.department"), 
        		"um", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("W0511", ResourceFactory.getProperty("zc_new.zc_reviewfile.nameOfApplicant"), 
        		"0", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("W0513", ResourceFactory.getProperty("zc_new.zc_reviewfile.currentProfessionalTechnicalPost"), 
        		"ai", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("W0515", ResourceFactory.getProperty("zc_new.zc_reviewfile.professionalTechnicalPost"), 
        		"ai", "A", "0", 0, 0, i, i++, headStyleMap));
        return headList;
    }
    
   /**
    * 20160624
    * @Title: getAllDataList   
    * @Description:types 类型  分类型导出,学科组分情况导出  groupid w0301 w0501 是针对学科组增加的参数，非学科组直接使用null  
    * @param @param firstHeadList
    * @param @param reviewPersonIds
    * @param @param types
    * @param @param group_id
    * @param @param w0301
    * @param @return
    * @param @throws GeneralException 
    * @return ArrayList
    * @author changxy     
    * @throws
    */
    @SuppressWarnings("unchecked")
	public ArrayList getAllDataList(ArrayList<LazyDynaBean> firstHeadList, String reviewPersonIds,String types,String group_id,String w0301,String usetype) 
    	throws GeneralException {
    	ArrayList firstDataList = new ArrayList();
    	try {
    		ExportExcelUtil excelUtil = new ExportExcelUtil(conn);
    		StringBuffer sql = new StringBuffer();
    		sql.append("select ");
    		//申请人和二维码查询先置空
    		sql.append(" distinct zc.username,zc.password pwd,w01.w0107 ,'' as dimensional,'' as proposer");
    		sql.append(" from zc_expert_user zc");
    		sql.append(" left join (select W0501,W0507,W0509,W0511,W0513,W0515,W0301 from W05) W05 on zc.W0501=w05.W0501");
    		sql.append(" left join (select W0301,W0303,W0321 from W03) W03 on zc.W0301=w03.W0301");
    		sql.append(" left join w01 on zc.w0101=w01.w0101");
    		sql.append(" where W03.W0301 = W05.W0301 and "+Sql_switcher.isnull("usetype", "1")+"="+usetype);
    		if(StringUtils.isNotEmpty(reviewPersonIds))
    			sql.append(" and w05.w0501 in ("+ reviewPersonIds +")");
    		if(group_id!=null&&w0301!=null){//学科组科目分组统计
    			sql.append(" and zc.group_id='"+group_id+"' ");
    			sql.append(" and zc.W0301='"+w0301+"' ");
    		}
			sql.append(" and type='"+types+"' ");
    		sql.append(" order by zc.username");
    		firstDataList = excelUtil.getExportData(firstHeadList, sql.toString());//根据sql得到数据集
    		LazyDynaBean ldbean =null;  
    		//设置最后一列：申请人列
    		//haosl 20161013 start 帐号相同合并申请人
    		for(int i=0;i<firstDataList.size();i++){
    			ldbean=(LazyDynaBean)firstDataList.get(i);
    			LazyDynaBean usernamebean=(LazyDynaBean)ldbean.get("username");
    			String username = (String)usernamebean.get("content");
    			String content=getdisPropose(types,username,w0301,group_id); //申请人
    			LazyDynaBean propeserBean=(LazyDynaBean)ldbean.get("proposer");
    			propeserBean.set("content", content);
    			
    		}
    		//haosl 20161013 end
    	} catch (Exception e) {
        	e.printStackTrace();
        	throw GeneralExceptionHandler.Handle(e);
        }
        return firstDataList;
    }
    /**
     * 封装第一页数据页的列头
     */
    @SuppressWarnings("unchecked")
	public ArrayList<LazyDynaBean> getFirstSheetHeadList() {
        ArrayList headList = new ArrayList();
        HashMap headStyleMap = new HashMap();
        headStyleMap.put("columnWidth",4000);//设置列宽
        int i = 0;
        headList.add(getAbean("type", ResourceFactory.getProperty("zc_new.zc_reviewfile.expertType"), 
        		"0", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("username", ResourceFactory.getProperty("zc_new.zc_reviewfile.expertUsernameType"), 
        		"0", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("pwd", ResourceFactory.getProperty("zc_new.zc_reviewfile.password"), 
        		"0", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("state", ResourceFactory.getProperty("zc_new.zc_reviewfile.state"), 
        		"0", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("W0303", ResourceFactory.getProperty("zc_new.zc_reviewfile.meeting"), 
        		"0", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("W0507", ResourceFactory.getProperty("zc_new.zc_reviewfile.companyName"), 
        		"UN", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("W0509", ResourceFactory.getProperty("zc_new.zc_reviewfile.department"), 
        		"um", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("W0511", ResourceFactory.getProperty("zc_new.zc_reviewfile.nameOfApplicant"), 
        		"0", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("W0513", ResourceFactory.getProperty("zc_new.zc_reviewfile.currentProfessionalTechnicalPost"), 
        		"ai", "A", "0", 0, 0, i, i++, headStyleMap));
        headList.add(getAbean("W0515", ResourceFactory.getProperty("zc_new.zc_reviewfile.professionalTechnicalPost"), 
        		"ai", "A", "0", 0, 0, i, i++, headStyleMap));
        return headList;
    }

    /**
     * 得到第一页表格数据集合
     * @param firstHeadList 第一页列头
     * @param meetingState 会议状态 
     * @param reviewPersonIds 申请编号字符串
     * @return 第一页表格数据集合
     * @throws GeneralException
     */
	@SuppressWarnings("unchecked")
	public ArrayList getFirstDataList(ArrayList<LazyDynaBean> firstHeadList, String reviewPersonIds) 
		throws GeneralException {
        ArrayList firstDataList = new ArrayList();
        try {
        	ExportExcelUtil excelUtil = new ExportExcelUtil(conn);
        	StringBuffer sql = new StringBuffer();
        	sql.append("select ");
        	sql.append(" (case when zc.type=1 then '内部评委'  when zc.type=2 then '学科组成员' ");
        	sql.append(" when zc.type=3 then '外部鉴定专家' end) type,");
        	sql.append(" zc.username,zc.password pwd,");
        	sql.append(" (case when zc.state=1 then '启用'   when zc.state=0 then '禁用' end) state,");
        	sql.append(" w03.W0303,w05.W0507,w05.W0509,w05.W0511,w05.W0513,w05.W0515");
        	sql.append(" from zc_expert_user zc");
        	sql.append(" left join (select W0501,W0507,W0509,W0511,W0513,W0515,W0301 from W05) W05 on zc.W0501=w05.W0501");
        	sql.append(" left join (select W0301,W0303,W0321 from W03) W03 on zc.W0301=w03.W0301");
        	sql.append(" where W03.W0301 = W05.W0301 ");
        	//sql.append(" and W03.W0321 = '"+ meetingState +"'");
        	if(StringUtils.isNotEmpty(reviewPersonIds))
        		sql.append(" and w05.w0501 in ("+ reviewPersonIds +")");
        	sql.append(" order by type,zc.username");
        	
        	firstDataList = excelUtil.getExportData(firstHeadList, sql.toString());//根据sql得到数据集
        	
        	LazyDynaBean ldbean = null;
        	int row1 = 1;//第一列开始行
        	String type = "";//当前行的type值
        	String temptype = "";//作对比的临时type值
        	
            int row2 = 1;//第二列开始行
            String username = "";
            String tempusername = "";
            String password = "";
            String temppassword = "";
            
            //合并单元格
            for (int i = 0; i < firstDataList.size(); i++) {
            	ldbean = (LazyDynaBean) firstDataList.get(i);
                LazyDynaBean tempLDBean = (LazyDynaBean) ldbean.get("type");
                type = (String) tempLDBean.get("content");
                tempLDBean = (LazyDynaBean) ldbean.get("username");
                username = (String) tempLDBean.get("content");
                tempLDBean = (LazyDynaBean) ldbean.get("pwd");
                password = (String) tempLDBean.get("content");
                if (i == 0){//取第一行数据作为对比数据
                	temptype = type;
                	tempusername = username;
                	temppassword = password;
                }else if (i > 0) {
                	//合并专家类型列
                    if (temptype.equals(type)) {//上一行和当前行数据相同
                    	ldbean = (LazyDynaBean) firstDataList.get(i - 1);//得到上一行的ldbean操作
                    	tempLDBean = (LazyDynaBean) ldbean.get("type");
                        tempLDBean.set("content", "");//将上一行相同内容置空
                        
                        if (i == firstDataList.size() - 1) {//最后一行特殊处理
                        	ldbean = (LazyDynaBean) firstDataList.get(i);//得到最后一行的ldbean操作
                        	tempLDBean = (LazyDynaBean) ldbean.get("type");
                            tempLDBean.set("content", "");//将最后一行内容置空
                            
                            ldbean = (LazyDynaBean) firstDataList.get(row1 - 1);
	                    	tempLDBean = (LazyDynaBean) ldbean.get("type");
	                    	tempLDBean.set("content", temptype);
	                    	this.setTempLDBean(tempLDBean, row1, i+1, 0, 0);//i+1是因为数据开始行是1
                        }
                    } else {//上一行和当前行数据不同
                    	ldbean = (LazyDynaBean) firstDataList.get(i - 1);//得到上一行的ldbean操作
                    	tempLDBean = (LazyDynaBean) ldbean.get("type");
                        tempLDBean.set("content", "");//将上一行相同内容置空
                    	
                    	ldbean = (LazyDynaBean) firstDataList.get(row1 - 1);//得到上一行的ldbean操作
                    	tempLDBean = (LazyDynaBean) ldbean.get("type");
                    	tempLDBean.set("content", temptype);
                    	this.setTempLDBean(tempLDBean, row1, i, 0, 0);//因为是处理上一行，所以i不加1
                        
                    	row1 = i+1;//上一行结束的位置加1就是下一行开始的位置
                        
                        temptype = type;//改变对比数据的值
                        if(i == firstDataList.size() - 1){//最后一行特殊处理
                        	ldbean = (LazyDynaBean) firstDataList.get(i);//得到上一行的ldbean操作
                        	tempLDBean = (LazyDynaBean) ldbean.get("type");
                        	this.setTempLDBean(tempLDBean, row1, i+1, 0, 0);//i+1是因为数据开始行是1
                        }
                    }
                    
                    //合并账号密码列
                    if (tempusername.equals(username)&&temppassword.equals(password)) {//上一行和当前行数据相同
                    	ldbean = (LazyDynaBean) firstDataList.get(i - 1);//得到上一行的ldbean操作
                    	tempLDBean = (LazyDynaBean) ldbean.get("username");
                        tempLDBean.set("content", "");//将上一行相同内容置空
                        tempLDBean = (LazyDynaBean) ldbean.get("pwd");
                        tempLDBean.set("content", "");//将上一行相同内容置空
                        
                        if (i == firstDataList.size() - 1) {//最后一行特殊处理
                        	ldbean = (LazyDynaBean) firstDataList.get(i);//得到最后一行的ldbean操作
                        	tempLDBean = (LazyDynaBean) ldbean.get("username");
                            tempLDBean.set("content", "");//将最后一行内容置空
                            tempLDBean = (LazyDynaBean) ldbean.get("pwd");
                            tempLDBean.set("content", "");//将最后一行内容置空
                            
                            ldbean = (LazyDynaBean) firstDataList.get(row2 - 1);
	                    	tempLDBean = (LazyDynaBean) ldbean.get("username");
	                    	tempLDBean.set("content", tempusername);
	                    	this.setTempLDBean(tempLDBean, row2, i+1, 1, 1);//i+1是因为数据开始行是1
                            
                            tempLDBean = (LazyDynaBean) ldbean.get("pwd");
	                    	tempLDBean.set("content", temppassword);
	                    	this.setTempLDBean(tempLDBean, row2, i+1, 2, 2);//i+1是因为数据开始行是1
                        }
                    } else {//上一行和当前行数据不同
                    	ldbean = (LazyDynaBean) firstDataList.get(i - 1);//得到上一行的ldbean操作
                    	tempLDBean = (LazyDynaBean) ldbean.get("username");
                        tempLDBean.set("content", "");//将上一行相同内容置空
                        tempLDBean = (LazyDynaBean) ldbean.get("pwd");
                        tempLDBean.set("content", "");//将上一行相同内容置空
                    	
                    	ldbean = (LazyDynaBean) firstDataList.get(row2 - 1);//得到上一行的ldbean操作
                    	tempLDBean = (LazyDynaBean) ldbean.get("username");
                    	tempLDBean.set("content", tempusername);
                    	this.setTempLDBean(tempLDBean, row2, i, 1, 1);//因为是处理上一行，所以i不加1
                        
                        tempLDBean = (LazyDynaBean) ldbean.get("pwd");
                    	tempLDBean.set("content", temppassword);
                    	this.setTempLDBean(tempLDBean, row2, i, 2, 2);//因为是处理上一行，所以i不加1
                    	
                        row2 = i+1;//上一行结束的位置加1就是下一行开始的位置
                        
                        tempusername = username;
                        temppassword = password;//改变对比数据的值
                        if(i == firstDataList.size() - 1){//最后一行特殊处理
                        	ldbean = (LazyDynaBean) firstDataList.get(i);//得到上一行的ldbean操作
                        	tempLDBean = (LazyDynaBean) ldbean.get("username");
                        	this.setTempLDBean(tempLDBean, row2, i+1, 1, 1);
                            
                            tempLDBean = (LazyDynaBean) ldbean.get("pwd");
                            this.setTempLDBean(tempLDBean, row2, i+1, 2, 2);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return firstDataList;
    }
    
    /**
     * 设置导出数据集输出位置
     * @param tempLDBean 临时数据bean
     * @param fromRowNum 开始行
     * @param toRowNum 结束行
     * @param fromColNum 开始列
     * @param toColNum 结束列
     */
    private void setTempLDBean(LazyDynaBean tempLDBean, int fromRowNum, int toRowNum, int fromColNum, int toColNum){
    	tempLDBean.set("fromRowNum", fromRowNum);
        tempLDBean.set("toRowNum", toRowNum);//加1是因为数据开始行是1
        tempLDBean.set("fromColNum", fromColNum);
        tempLDBean.set("toColNum", toColNum);
    }
    
    /**
     * 根据页面展示得到需要导出的列头
     * @param userView
     * @param isUpperCommitee
     * 			当前选中数据是否为上级会议
     * @return 
     */
	@SuppressWarnings("unchecked")
	public ArrayList getColumnsInfo(UserView userView) {
    	TableDataConfigCache catche = (TableDataConfigCache)userView.getHm().get("reviewFile");
		ArrayList<ColumnsInfo> tableColumns = catche.getTableColumns();
		//当前选中数据如果为上级会议，则只对学院聘任组的信息可导出   haosl  20160812  end。
		ArrayList columnDisplayConfig = catche.getColumnDisplayConfig();
		if (columnDisplayConfig==null)
			return tableColumns;
        for (int i = 0; i < columnDisplayConfig.size(); i++) {
        	MorphDynaBean mdbean = (MorphDynaBean)columnDisplayConfig.get(i);
        	if("0".equals(mdbean.get("is_display"))){
        		for (int j = 0; j < tableColumns.size() ; j++) {
        			ColumnsInfo tableColumn = (ColumnsInfo)tableColumns.get(j);
        			if(StringUtils.isNotEmpty(tableColumn.getColumnId())&&tableColumn.getColumnId().equals(mdbean.get("itemid"))){
        				tableColumns.remove(j);
        				break;
        			}
        		}
        	}
		}
    	return tableColumns;
    }
    
    /**
     * 得到第二页复合列头集合
     * @param columnsInfo 页面展示的列集合
     * @return 第二页复合列头集合
     */
    @SuppressWarnings("unchecked")
	public ArrayList<LazyDynaBean> getMergedCellList(ArrayList columnsInfo) {
    	ArrayList<LazyDynaBean> mergedCellList = new ArrayList<LazyDynaBean>();
		int colNum = 0;
		for (int i = 0; i < columnsInfo.size(); i++) {
			ColumnsInfo columnInfo = (ColumnsInfo)columnsInfo.get(i);
			ArrayList childColumnList = (ArrayList)columnInfo.getChildColumns();
			if(childColumnList.size() > 0){//子列头大于0，则是复合列头
				LazyDynaBean ldbean = new LazyDynaBean();
				ldbean.set("content", columnInfo.getColumnDesc());// 列头名称
                HashMap<String, Object> styleMap = new HashMap<String, Object>();// 样式
				styleMap.put("fontSize", 10);// 字号
				styleMap.put("fillForegroundColor", HSSFColor.GREY_25_PERCENT.index);// 背景色
				ldbean.set("mergedCellStyleMap", styleMap);
				ldbean.set("fromRowNum", 0);// 合并单元格从那行开始
				ldbean.set("toRowNum", 0);// 合并单元格到哪行结束
				ldbean.set("fromColNum", colNum);// 合并单元格从哪列开始
				ldbean.set("toColNum", colNum + childColumnList.size()-1);// 合并单元格从哪列结束
				mergedCellList.add(ldbean);
				colNum += childColumnList.size();//定位下次初始列
				continue;
			}else {
				String columnId = columnInfo.getColumnId();
				if("w0537_".equals(columnId)||"w0511".equals(columnId))
					continue;
				colNum += 1;//定位下次初始列
				continue;
			}
		}
		
		return mergedCellList;
    }
    
    
	 /**
     * 得到第二页（--数据导入-下载模板文件）的列头集合
     * @param columnsInfo 页面展示的列集合
     * @param isLocked 是否启用锁定，如锁定则不能修改
     * @param isUpperCommitee 所要导出的帐号是否属于上级会议
     * @return 第二页列头集合
     */
    @SuppressWarnings("unchecked")
    public ArrayList<LazyDynaBean> getSecondSheetHeadList(ArrayList columnsInfo, Boolean isForImport,String w0301) throws GeneralException{//isForImport
    	ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
        int colNum = 0;
        HashMap headStyleMap = null;//表头样式设置
        HashMap colheadStyleMap = null;//数据单元格样式
        
        //查询评审会议支持的评审环节  haosl  20170605 start
        HashMap<String, String> stepsMap = getEnableSteps(w0301);
        String exportHidden = ",";//不需要导出的列
        String noEditStr = ",";
        String step1 = stepsMap.get("step1");
        String step2 = stepsMap.get("step2");
        String step3 = stepsMap.get("step3");
        String step4 = stepsMap.get("step4");
        if("00".equals(step1)){//评委会   =11的时候不做任何特殊处理即可
        	exportHidden += "w0517,w0519,w0549,w0551,w0553,w0559,committee_hb,";
        }else if("10".equals(step1)){//导出但不可编辑
        	noEditStr+="w0517,w0549,w0551,w0553,w0559,";
        }else if("11".equals(step1)) {
        	noEditStr+="w0517,w0559,";
        }
        if("00".equals(step2)){//学科组 =11的时候不做任何特殊处理即可
        	exportHidden += "w0543,w0545,w0547,w0557,subject_hb,w0521,group_id,";
        }else if("10".equals(step2)){//导出但不可编辑
        	noEditStr+="w0543,w0545,w0547,w0557,group_id,w0521,";
        }else if("11".equals(step2)){//导出但不可编辑
        	noEditStr+="w0557,group_id,w0521,";
        }
        if("00".equals(step3)){//同行专家 =11的时候不做任何特殊处理即可
        	exportHidden += "checkproficient,w0527,w0529,w0531,w0533,checkproficient_hb,w0541,";
        }else if("10".equals(step3)){//导出但不可编辑
        	noEditStr+="w0527,w0529,w0531,w0533,checkproficient,";
        }else if("11".equals(step3)){//导出但不可编辑
        	noEditStr+="checkproficient,w0533,";
        }
        if("00".equals(step4)){//二级单位 =11的时候不做任何特殊处理即可
        	exportHidden += "committeename,w0563,w0565,w0567,w0569,w0571,college_hb,";
        }else if("10".equals(step4)){//导出但不可编辑
        	noEditStr+="w0563,w0565,w0567,w0569,w0571,";
        }else if("11".equals(step4)){//导出但不可编辑
        	noEditStr+="w0571,w0569,";
        }
        //查询评审会议支持的评审环节  haosl  20170605 end
        for (int i = 0; i < columnsInfo.size()-15; i++) {//后11列为隐藏列
        	ColumnsInfo columnInfo = (ColumnsInfo)columnsInfo.get(i);
            colheadStyleMap = new HashMap();
            if(StringUtils.isNotBlank(columnInfo.getTextAlign())) {
            	HorizontalAlignment align = HorizontalAlignment.CENTER;
            	if("left".equals(columnInfo.getTextAlign()))
            		align = HorizontalAlignment.LEFT;
            	else if("center".equals(columnInfo.getTextAlign()))
            		align = HorizontalAlignment.CENTER;
            	else if("right".equals(columnInfo.getTextAlign()))
            		align = HorizontalAlignment.RIGHT;
            	colheadStyleMap.put("align",align);//表头宽度设置
            } 
            ArrayList<ColumnsInfo> childColumnList = columnInfo.getChildColumns();
            if(childColumnList.size()>0){//判断childColumnList是否大于0，大于0为复合列
                for (int j = 0; j < childColumnList.size(); j++) {
                	ColumnsInfo columnInfo_j = (ColumnsInfo)childColumnList.get(j);
                	colheadStyleMap = new HashMap();//表头样式设置
                    if(StringUtils.isNotBlank(columnInfo_j.getTextAlign())) {
                    	HorizontalAlignment align = HorizontalAlignment.CENTER;
                    	if("left".equals(columnInfo_j.getTextAlign()))
                    		align = HorizontalAlignment.LEFT;
                    	else if("center".equals(columnInfo_j.getTextAlign()))
                    		align = HorizontalAlignment.CENTER;
                    	else if("right".equals(columnInfo_j.getTextAlign()))
                    		align = HorizontalAlignment.RIGHT;
                    	colheadStyleMap.put("align",align);//表头宽度设置
                    }
                    LazyDynaBean ldbean = new LazyDynaBean();
                    ldbean.set("itemid", columnInfo_j.getColumnId());//列头代码
                    if(isForImport){//导入数据--下载模板文件时，相关设置
                        ldbean.set("comment", columnInfo_j.getColumnId());//列头注释
                    }
                    ldbean.set("content",  columnInfo_j.getColumnDesc());//列头名称
                    ldbean.set("colType", columnInfo_j.getColumnType());//列数据类型
                    ldbean.set("codesetid", columnInfo_j.getCodesetId().toUpperCase());//列头代码
                    ldbean.set("decwidth",  columnInfo_j.getDecimalWidth()+"");//列小数位数
                    ldbean.set("fromRowNum", 1);//单元格开始行
                    ldbean.set("toRowNum", 1);//单元格结束行
                    ldbean.set("fromColNum", colNum);//单元格开始行列
                    ldbean.set("toColNum", colNum);//单元格结束行列
                    
                    headStyleMap = new HashMap();//表头样式设置
                    headStyleMap.put("columnWidth",columnInfo_j.getColumnWidth()*40);//表头宽度设置
                    
                    // 需要锁列
                    if ((isForImport && !StringUtils.isEmpty(ReviewFileTrans.exportIslock) && ReviewFileTrans.exportIslock.indexOf("," + columnInfo_j.getColumnId() + ",") != -1)
                    		||noEditStr.indexOf("," + columnInfo_j.getColumnId() + ",") != -1
                    		||!"W05".equalsIgnoreCase(columnInfo_j.getColumnId().substring(0, 3))){
                        ldbean.set("columnLocked", true);//设置为只读，锁列colStyleMap
                        headStyleMap.put("fillForegroundColor",HSSFColor.GREY_25_PERCENT.index);
                    }
                        
                    if (ReviewFileTrans.exportHidden.indexOf("," + columnInfo_j.getColumnId() + ",") != -1
                    		||exportHidden.indexOf("," + columnInfo_j.getColumnId() + ",")!=-1) {
                    	ldbean.set("columnHidden", true);//设置隐藏
                    }
                    ldbean.set("colStyleMap", colheadStyleMap);//列样式
                    ldbean.set("headStyleMap", headStyleMap);//表头样式
                    headList.add(ldbean);
                    colNum++;//下一列开始位置
                }
                continue;
            }
            String columnId = columnInfo.getColumnId();
            if("w0535_".equalsIgnoreCase(columnId)||"w0537_".equalsIgnoreCase(columnId))
                continue;
            LazyDynaBean ldbean = new LazyDynaBean();
            ldbean.set("itemid", columnId);//列头代码
            if(isForImport){//导入数据--下载模板文件时，相关设置
                ldbean.set("comment", columnId);//列头注释
            }
            ldbean.set("content", columnInfo.getColumnDesc());//列头名称
            //栏目设置指标排序做了特殊处理，为了导出模板时不报错，就得在这里还原 haosl 2017-08-03 add
            if("W0539".equalsIgnoreCase(columnId)||"W0541".equalsIgnoreCase(columnId)
            		||"W0555".equalsIgnoreCase(columnId)||"W0573".equalsIgnoreCase(columnId))
            	ldbean.set("colType","A");//列数据类型
            else
            	ldbean.set("colType", columnInfo.getColumnType());//列数据类型
            ldbean.set("codesetid", columnInfo.getCodesetId().toUpperCase());//列头代码
            ldbean.set("decwidth",  columnInfo.getDecimalWidth()+"");//列小数位数
            ldbean.set("fromRowNum", 0);//单元格开始行
            ldbean.set("toRowNum", 1);//单元格结束行
            ldbean.set("fromColNum", colNum);//单元格开始行列
            ldbean.set("toColNum", colNum);//单元格结束行列
            
            headStyleMap = new HashMap();//表头样式设置
            headStyleMap.put("columnWidth",columnInfo.getColumnWidth()*40);//表头宽度设置
            
            // 需要锁列
            if ((isForImport && !StringUtils.isEmpty(ReviewFileTrans.exportIslock) && ReviewFileTrans.exportIslock.indexOf("," + columnId + ",") != -1)
            		||!"W05".equalsIgnoreCase(columnId.substring(0, 3))) {
                ldbean.set("columnLocked", true);//设置为只读，锁列
                headStyleMap.put("fillForegroundColor",HSSFColor.GREY_25_PERCENT.index);
            }
            if (ReviewFileTrans.exportHidden.indexOf("," + columnId + ",") != -1
            		||exportHidden.indexOf("," + columnId + ",")!=-1) {
            	ldbean.set("columnHidden", true);//设置隐藏
            }
            ldbean.set("colStyleMap", colheadStyleMap);//列样式---？没起到作用
            ldbean.set("headStyleMap", headStyleMap);//表头样式
            headList.add(ldbean);
            colNum++;//下一列开始位置
        }
        headStyleMap = new HashMap();
        headStyleMap.put("columnWidth",8000);//表头宽度设置
	
        //学院聘任组专家账号明细
    	LazyDynaBean collegeAccount = getAbean("inCollegeList", ResourceFactory.getProperty("zc_new.zc_reviewfile.inCollegeList"),
				"0", "A", "0", 0, 1, colNum, colNum, headStyleMap);
		headList.add(collegeAccount);
    	//同行专家帐号明细
    	LazyDynaBean expertDetail = getAbean("exExpertList", ResourceFactory.getProperty("zc_new.zc_reviewfile.exExpertList"), 
    			"0", "A", "0", 0, 1, colNum+1, colNum+1, headStyleMap);
    	//评委账号明细
    	LazyDynaBean judgesAccount = getAbean("inReviewList", ResourceFactory.getProperty("zc_new.zc_reviewfile.inReviewList"), 
    			"0", "A", "0", 0, 1, colNum+2, colNum+2, headStyleMap);
    	//学科组专家账号明细
    	LazyDynaBean subjectAccount = getAbean("inExpertList", ResourceFactory.getProperty("zc_new.zc_reviewfile.inExpertList"), 
    			"0", "A", "0", 0, 1, colNum+3, colNum+3, headStyleMap);
    	//会议id
    	LazyDynaBean abeanw0301 = getAbean("w0301", "w0301", "0", "A", "0", 0, 1, colNum+4, colNum+4, headStyleMap);
    	//主键
    	LazyDynaBean abeanw0501 = getAbean("w0501", "材料序号", "0", "A", "0", 0, 1, colNum+5, colNum+5, headStyleMap);
        
    	if(isForImport){//导入数据--下载模板文件时，相关设置
    		 expertDetail.set("columnHidden", true);//设置为隐藏
             judgesAccount.set("columnHidden", true);
             subjectAccount.set("columnHidden", true);
        	 collegeAccount.set("columnHidden",true);//设置为隐藏 haosl 20160926 
        	
        	 abeanw0301.set("comment", "w0301");//列头注释
             abeanw0501.set("comment", "w0501");//列头注释
        	 abeanw0501.set("columnHidden", true);//设置为隐藏
             abeanw0301.set("columnHidden", true);//设置为隐藏
    	}
    	headList.add(expertDetail);
    	headList.add(judgesAccount);
    	headList.add(subjectAccount);
    	headList.add(abeanw0301);
    	headList.add(abeanw0501);
       
        return headList;
    }
	
	/**
     * 得到二页数据集合
     * @param secondHeadList 第二页列头集合
     * @param meetingState 会议状态
     * @param reviewPersonIds 评审编号字符串
     * @return 二页数据集合
     * @throws GeneralException
     */
    @SuppressWarnings("unchecked")
	public ArrayList getSecondDataList(ArrayList columnsInfo, ArrayList<LazyDynaBean> secondHeadList, String reviewPersonIds, Boolean isForImport) throws GeneralException {
        ArrayList secondDataList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        ReviewFileBo reviewFileBo = new ReviewFileBo(this.conn, this.userview);// 工具类
        try {
        	ExportExcelUtil excelUtil = new ExportExcelUtil(conn);
        	sql.append("select ");
        	String selectSql = getSelectSecondDataSql(columnsInfo);
        	StringBuilder w0555str = new StringBuilder();//评审环节
        	w0555str.append(" case");
        	w0555str.append(" when w0555 = 1 then '"+ JobtitleUtil.ZC_REVIEWFILE_STEP1SHOWTEXT +"'");
        	w0555str.append(" when w0555 = 2 then '"+ JobtitleUtil.ZC_REVIEWFILE_STEP2SHOWTEXT +"'");
        	w0555str.append(" when w0555 = 3 then '"+ JobtitleUtil.ZC_REVIEWFILE_STEP3SHOWTEXT +"'");
        	w0555str.append(" when w0555 = 4 then '"+ JobtitleUtil.ZC_REVIEWFILE_STEP4SHOWTEXT +"'");
        	w0555str.append(" end as w0555");
        	StringBuilder w0573str = new StringBuilder();//评审状态
        	w0573str.append(" case");
        	w0573str.append(" when w0573 = 1 then '"+ JobtitleUtil.ZC_REVIEWFILE_W0573_1 +"'");
        	w0573str.append(" when w0573 = 2 then '"+ JobtitleUtil.ZC_REVIEWFILE_W0573_2 +"'");
        	w0573str.append(" end as w0573");
        	selectSql = selectSql.replace("w0555", w0555str.toString());
        	selectSql = selectSql.replace("w0573", w0573str.toString());
        	sql.append(selectSql);
        	sql.append(" from W05");
        	sql.append(" left join W03 on w05.w0301=w03.w0301");
        	sql.append(" left join zc_committee on w05.w0561=zc_committee.committee_id");
        	//sql.append(" where w03.w0321='"+ meetingState +"'");
        	if(StringUtils.isNotEmpty(reviewPersonIds))
        		sql.append(" where w0501 in ("+ reviewPersonIds +")");
        	sql.append(" order by w0501 desc");
        	secondDataList = excelUtil.getExportData(secondHeadList, sql.toString());//得到数据列
        	LazyDynaBean ldbean = null;
        	for (int i = 0; i < secondDataList.size(); i++) {
        		ldbean = (LazyDynaBean) secondDataList.get(i);
                LazyDynaBean meetingBean = (LazyDynaBean) ldbean.get("w0301");
                String meetingid = (String) meetingBean.get("content");
                
                //内部评审问卷
                LazyDynaBean w0539Bean = (LazyDynaBean) ldbean.get("w0539");
                String w0539id = w0539Bean == null ? "" : (String) w0539Bean.get("content");
                //专家鉴定问卷
                LazyDynaBean w0541Bean = (LazyDynaBean) ldbean.get("w0541");
                String w0541id = w0541Bean==null? "" :(String) w0541Bean.get("content");
                ArrayList<HashMap> qnPlan = new ArrayList<HashMap>();
                qnPlan = reviewFileBo.getQnPlan();
                for(int j=0;j<qnPlan.size();j++){
                    HashMap qnmap = qnPlan.get(j);
                    String planId = (String) qnmap.get("dataValue");
                    String planName = (String) qnmap.get("dataName");
                    if(w0539id.equalsIgnoreCase(planId)){
                        w0539Bean.set("content", planId+":"+planName);
                    }
                    if(w0541id.equalsIgnoreCase(planId)){
                        w0541Bean.set("content", planId+":"+planName);
                    }
                }
                if(!isForImport){
                	LazyDynaBean reviewBean = (LazyDynaBean) ldbean.get("w0501");
                    String reviewid = (String) reviewBean.get("content");
                    // 获取每个申请中的 评审账号密码
                    Object inReviewList = getInfo(meetingid, reviewid, 1);
                    LazyDynaBean inReviewBean = (LazyDynaBean) ldbean.get("inReviewList");
                    if(inReviewBean!=null){
                    	inReviewBean.set("content", inReviewList);
                    }
                    
                    Object inExpertList = getInfo(meetingid, reviewid, 2);
                    LazyDynaBean inExpertBean = (LazyDynaBean) ldbean.get("inExpertList");
                    if(inExpertBean!=null){
                    	inExpertBean.set("content", inExpertList);
                    }
                    
                    Object exExpertList = getInfo(meetingid, reviewid, 3);
                    LazyDynaBean exExpertBean = (LazyDynaBean) ldbean.get("exExpertList");
                    if(exExpertBean!=null){
                    exExpertBean.set("content", exExpertList);
                    }
                    
                    Object inCollegeList = getInfo(meetingid, reviewid, 4);
                    LazyDynaBean inCollegeBean = (LazyDynaBean) ldbean.get("inCollegeList");
                    if(inCollegeBean!=null){
                    	inCollegeBean.set("content", inCollegeList);
                    }
                }
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return secondDataList;
    }
    
    /**
     * 得到第二页数据集合要查询的字段列
     * @param columnsInfo 页面展示的列集合
     * @return
     */
    
	@SuppressWarnings({ "unchecked", "unused" })
	private String getSelectSecondDataSql(ArrayList columnsInfo){
    	StringBuffer select_sql = new StringBuffer();
    	for (int i = 0; i < columnsInfo.size()-15; i++) {//后11列为隐藏列
			ColumnsInfo columnInfo = (ColumnsInfo)columnsInfo.get(i);
			String columnId = columnInfo.getColumnId();
			ArrayList<ColumnsInfo> childColumnList = columnInfo.getChildColumns();
			if(childColumnList.size()>0){//判断childColumnList是否大于0，大于0为复合列
				for (int j = 0; j < childColumnList.size(); j++) {
					ColumnsInfo columnInfo_j = (ColumnsInfo)childColumnList.get(j);
					columnId = columnInfo_j.getColumnId();
					if("w0571".equalsIgnoreCase(columnId)) {
						select_sql.append(Sql_switcher.sqlToChar("w0563+w0565+w0567")+Sql_switcher.concat()+"'/'"+Sql_switcher.concat()+Sql_switcher.sqlToChar(Sql_switcher.isnull("w0323", "0"))+" as w0571,");
						continue;
					}else if("checkproficient".equalsIgnoreCase(columnId)){
						select_sql.append(Sql_switcher.sqlToChar("w0527+w0529+w0531")+Sql_switcher.concat()+"'/'"+Sql_switcher.concat()+Sql_switcher.sqlToChar(Sql_switcher.isnull("w0523","0"))+" as checkproficient,");
						continue;
					}else if("w0521".equalsIgnoreCase(columnId)){
						select_sql.append(Sql_switcher.sqlToChar(Sql_switcher.isnull("w0543","0")+"+"+Sql_switcher.isnull("w0545","0")+"+"+Sql_switcher.isnull("w0547","0"))+Sql_switcher.concat()+"'/'"+Sql_switcher.concat()+Sql_switcher.sqlToChar(Sql_switcher.isnull("w0521","0"))+" as w0521,");
						continue;
					}else if("w0517".equalsIgnoreCase(columnId)){
						select_sql.append(Sql_switcher.sqlToChar("w0549+w0551+w0553")+Sql_switcher.concat()+"'/'"+Sql_switcher.concat()+Sql_switcher.sqlToChar(Sql_switcher.isnull("w0315","0"))+" as w0517,");
						continue;
					}
					if("meetingname".equals(columnId)||"checkproficient".equals(columnId)||"committeeagree".equals(columnId)
			        		||"subjectsagree".equals(columnId)||"proficientagree".equals(columnId)||"group_id".equals(columnId)||"collegeagree".equals(columnId))
			        	continue;
			        if("committeename".equalsIgnoreCase(columnInfo_j.getColumnId()))
                    	select_sql.append("zc_committee.committee_name as committeeName"+",");
                    else
                    	select_sql.append(columnId+",");
				}
				continue;
			}
			if("meetingname".equals(columnId)||"checkproficient".equals(columnId)||"committeeagree".equals(columnId)
	        		||"subjectsagree".equals(columnId)||"proficientagree".equals(columnId)||"group_id".equals(columnId)||"collegeagree".equals(columnId))
	        	continue;
			if("w0535_".equalsIgnoreCase(columnId)||"w0537_".equalsIgnoreCase(columnId))
				continue;
	        select_sql.append(columnId+",");
		}
    	select_sql.append("W03.W0301,W03.W0303 as meetingname,W0501,");
    	select_sql.append("(select group_name from zc_subjectgroup where group_id=w05.group_id) group_id,");
    	
    	//评委会赞成人数占比
    	select_sql.append("case when w0517=0 then '0' else cast(cast(cast((w0553)as float)/w0517*100 as int)as varchar(10)) end "+Sql_switcher.concat()+"'%'  as committeeagree");
    	//学科组赞成人数占比
    	select_sql.append(",case when w0521=0 then '0' else cast(cast(cast((w0547)as float)/w0521*100 as int)as varchar(10)) end "+Sql_switcher.concat()+"'%'  as subjectsagree");
    	//外部专家赞成人数占比
    	select_sql.append(",case when w0523=0 then '0' else cast(cast(cast((w0531)as float)/w0523*100 as int)as varchar(10)) end "+Sql_switcher.concat()+"'%'  as proficientagree");
    	//学院聘任组专家赞成人数占比
    	select_sql.append(",case when w0571=0 then '0' else cast(cast(cast((w0567)as float)/w0571*100 as int)as varchar(10)) end "+Sql_switcher.concat()+"'%'  as collegeagree");

    	select_sql.append(",'' as exExpertList,'' as inReviewList,'' as inExpertList,'' as inCollegeList");
    	return select_sql.toString();
    }
    
    /**
     * 
     * @Title:getInfo
     * @Description：获取每条数据对应的专家id等信息
     * @author liuyang
     * @param meetingid 会议id
     * @param reviewid  申请id
     * @param type -类别 1-内部评审 2-内部专家 3-外部专家
     * @return 返回信息字符串
     * @throws GeneralException
     */
    private String getInfo(String meetingid, String reviewid, int type) throws GeneralException {
        String result = "";
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            String add = Sql_switcher.concat();
            String str = " select ( '账号:'" + add + "username" + add + "'密码:'" + add + "password" + add + "'状态:'" + add
                    + "(case when state=0 then '禁用'else '启用' end)" + add + "'\n') as str ";
            str = str + " from  zc_expert_user ";
            str = str + " where 1=1 and W0301 = '" + meetingid + "' ";
            str = str + " and W0501 = '" + reviewid + "' ";
            str = str + " and type = " + type;
            str = str +" and " + Sql_switcher.isnull("usetype", "1")+"=1"; // 审查账号
            RowSet rs = dao.search(str);
            while (rs.next()) {
                result = result + rs.getString("str");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return result;
    }
    /**
     * 
     * @Title:getAbean
     * @Description：设置表头列表模板
     * @author liuyang
     * @param itemid 信息id
     * @param content 信息内容
     * @param codesetid 指标集id
     * @param colType 指标类别
     * @param decwidth 小数点保留位数
     * @param fromRowNum 从*行起始
     * @param toRowNum 至*行合并
     * @param fromColNum从*列起始
     * @param toColNum 至*列合并
     * @param headStyleMap 列头单元格宽度
     * @return
     */
    @SuppressWarnings("unchecked")
	private LazyDynaBean getAbean(String itemid, String content, String codesetid, String colType, String decwidth,
            int fromRowNum, int toRowNum, int fromColNum, int toColNum, HashMap headStyleMap) {
        LazyDynaBean abean = new LazyDynaBean();
        abean.set("itemid", itemid);
        abean.set("content", content);
        abean.set("codesetid", codesetid);
        abean.set("colType", colType);
        abean.set("decwidth", decwidth);
        abean.set("fromRowNum", fromRowNum);
        abean.set("toRowNum", toRowNum);
        abean.set("fromColNum", fromColNum);
        abean.set("toColNum", toColNum);
        abean.set("headStyleMap", headStyleMap);
        return abean;

    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public UserView getUserview() {
        return userview;
    }

    public void setUserview(UserView userview) {
        this.userview = userview;
    }
    /**
     * 
     * @Title: getdimensional   
     * @Description:根据专家账号和密码生成二维码
     * http://192.192.102.118:8081/module/jobtitle/hcmlogon.html?uid=XL6XeNkfyto@3HJD@&pwd=TkXHBKWlblQ@3HJD@
     * http://127.0.0.1:8888/module/jobtitle/hcmlogon.html?uid=eoN2KL5NLjs@3HJD@&pwd=eoN2KL5NLjs@3HJD@  
     * 			
    			this.setTempLDBean(temdimenbean, row4, i+1, 2, 2);  
     * @param @param username
     * @param @param userpwd 
     * @return void    
     * @throws
     */
    public byte[] getdimensional(String username,String userpwd) throws GeneralException{
    	ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    	StringBuffer sbf=new StringBuffer(); //生成url
    	sbf.append(SystemConfig.getServerURL());
    	sbf.append("/module/jobtitle/cardview/mobile/CardView.html");
    	sbf.append("?uid=");
    	sbf.append(PubFunc.encryption(username));
    	sbf.append("&pwd=");
    	sbf.append(PubFunc.encryption(userpwd));
    	sbf.append("&time=");
    	sbf.append(new Date().getTime());
    	//生成二维码
    	Qrcode qrcode=new Qrcode();
    	qrcode.setQrcodeErrorCorrect('M');//容错率
    	qrcode.setQrcodeEncodeMode('B');//设置编码格式
    	qrcode.setQrcodeVersion(0);//二维码版本 haosl 20170417 update 10==>0 版本为10时，字符串链接长度过长时，会报数组交表越界。
    	
    	int imgWidth=140;
    	int imgHeight = imgWidth;
    	BufferedImage bi=new BufferedImage(imgWidth,imgHeight,BufferedImage.TYPE_BYTE_BINARY);
    	try {
			
    		byte[] bytes=sbf.toString().getBytes("gbk");
    		//width, height,BufferedImage.TYPE_BYTE_BINARY
    		Graphics2D g=bi.createGraphics();
    		g.setBackground(Color.WHITE);
    		g.clearRect(0, 0, imgWidth, imgHeight);
    		g.setColor(Color.BLACK);
    		//限制字节数
    		if (bytes.length > 0 && bytes.length <= 1000) {  
				boolean[][] s=qrcode.calQrcode(bytes);//0或1  //calQrcode(d);
				for (int i = 0; i < s.length; i++) {
					for (int j = 0; j < s.length; j++) {
						if(s[j][i]){
							g.fillRect(j*2, i*2, 2, 2);//填充区域
						}
					}
				}
    		}
    		g.dispose();
    		bi.flush();
    		ImageIO.write(bi, "png",outStream );
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		byte[] outStreambyte=outStream.toByteArray();
    	return outStreambyte;
    }
    /**
	 * 获取需要导出的阶段
	 * @param w0301
	 * @return
	 * @throws GeneralException
	 */
	public HashMap<String, String> getEnableSteps(String w0301) throws GeneralException{
		HashMap<String, String> steps = new HashMap<String, String>();
		/**
		 * 00 代表不导出
		 * 10 代表导出但不能编辑
		 * 11 导出并可编辑
		 */
		steps.put("step1", "00");
		steps.put("step2", "00");
		steps.put("step3", "00");
		steps.put("step4", "00");
		
		if(StringUtils.isEmpty(w0301)){
			return steps;
		}
		ContentDAO dao = null;
		RowSet rs = null;
		RowSet oth_rs = null;
		try {
			RecordVo vo = new RecordVo("w03");
			vo.setString("w0301", w0301);
			
			dao = new ContentDAO(this.conn);
			vo = dao.findByPrimaryKey(vo);//本次上申报数据
			
			int w0315 = vo.getInt("w0315");
			String w0325 = vo.getString("w0325");
			int w0323 = vo.getInt("w0323");
			
			StringBuffer sql = new StringBuffer();
			//查询申报人历史申报数据，如果有，上会材料处的评审阶段显示时，则会加入历史数据中启用的评审阶段。
			sql.setLength(0);
			sql.append("select W05.w0301 from W05,W03 where ");
			sql.append(Sql_switcher.diffYears("w05.create_time", Sql_switcher.today())+"=0 ");//本年度
			sql.append("and W05.w0301=W03.w0301 and W05.w0301<>? and W03.W0321='06' ");
			sql.append("and W05.w0301=W03.w0301 ");
			sql.append("and  w0503"+Sql_switcher.concat()+"w0505 in (");
			sql.append("select w0503"+Sql_switcher.concat()+"w0505 from w05 where w0301='"+w0301+"'");
			sql.append(")order by W05.create_time desc");
			
			List<String> values = new ArrayList<String>();
			values.add(w0301);
			oth_rs = dao.search(sql.toString(), values);

			int w0315_oth = 0;
			String w0325_oth = "";
			int w0323_oth = 0;
			String w0301_oth="";
			if(oth_rs.next()){
				w0301_oth = oth_rs.getString("w0301");
				RecordVo vo_oth = new RecordVo("w03");
				vo_oth.setString("w0301", w0301_oth);
				vo_oth = dao.findByPrimaryKey(vo_oth);//本次上申报数据
				
				w0315_oth = vo_oth.getInt("w0315");
				w0325_oth = vo_oth.getString("w0325");
				w0323_oth = vo_oth.getInt("w0323");
			}
			
			//学科组
			sql.setLength(0);
			sql.append("select distinct(w0301) from zc_expert_user where w0301 in ('"+w0301+"','"+w0301_oth+"') and type=2");
			rs = dao.search(sql.toString());
			rs.last();
			int count = rs.getRow();
			if(count==0){
				steps.put("step2", "00");
			}else if(count==1){
				if(w0301.equals(rs.getString("w0301")))
					steps.put("step2", "11");
				else
					steps.put("step2", "10");
			}else if(count==2){
				steps.put("step2", "11");
			}
				
			if(w0315 > 0){//参会人数>0:启用高评委
				steps.put("step1","11");
			}else{
				if(w0315_oth>0)
					steps.put("step1","10");	
				else
					steps.put("step1","00");	
					
			}
			if("1".equals(w0325)){//启用同行评议组
				steps.put("step3","11");
			}else{
				if("1".equals(w0325_oth))
					steps.put("step3","10");	
				else
					steps.put("step3","00");	
					
			}
			if(w0323 > 0){//参会人数>0:启用二级单位
				steps.put("step4","11");
			}else{
				if(w0323_oth>0)
					steps.put("step4","10");	
				else
					steps.put("step4","00");	
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(oth_rs);
		}
		return steps;
	}
}
