package com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject;

import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.sys.SMSSender;
import com.hjsj.hrms.module.jobtitle.utils.JobtitleUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * 
 * <p>Title:ReviewMeetingBo </p>
 * <p>Description: 评审会议操作Bo类</p>
 * <p>Company: hjsj</p> 
 * <p>create time: 2015-12-31</p>
 * @author liuy
 * @version 1.0
 */
public class ReviewMeetingBo {
	Connection conn;
    ContentDAO dao;
    UserView userview;
    
    public ReviewMeetingBo() {

    }
    public ReviewMeetingBo(Connection conn) {
        this.conn = conn;
    }
    public ReviewMeetingBo(Connection conn, UserView userview) {
        this.conn = conn;
        this.userview = userview;
    }
    
    /**
     * 获取列头，表格渲染
     * @param collegeEval	是否添加"学院聘任组"列
     * @return
     * @throws GeneralException
     */
	@SuppressWarnings("unchecked")
	public ArrayList<ColumnsInfo> getColumnList(Boolean collegeEval) throws GeneralException{
    	ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
    	try{
	    	//取得数据字典中设置的w01的构库的所有字段
	    	ArrayList fieldList=DataDictionary.getFieldList("W03",Constant.USED_FIELD_SET);
	    	ArrayList<CommonData> operationData = getOperationData();
			for(int i=0;i<fieldList.size();i++){
				FieldItem item=(FieldItem)fieldList.get(i);	
				String itemid=item.getItemid();//字段id
				String itemtype=item.getItemtype();//字段类型
				String codesetid = item.getCodesetid();//关联的代码
				String columndesc = item.getItemdesc();//字段描述
				int itemlength=item.getItemlength();//字段长度
				int decimalWidth = item.getDecimalwidth();//小数位数
				String state=item.getState();//0隐藏  1显示
				ColumnsInfo examTime = getColumnsInfo(itemid, columndesc, 180, decimalWidth, itemtype);
				if("w0303".equalsIgnoreCase(itemid)){
					examTime.setLocked(true);
					examTime.setRendererFunc("meeting_me.toReviewfilePage");
				}
				if("w0307".equals(itemid)||"w0309".equals(itemid)||"w0311".equals(itemid)||"w0315".equals(itemid)||"w0321".equals(itemid)){
					examTime = getColumnsInfo(itemid, columndesc, 100, 0, itemtype);
				}
				
				if("w0323".equalsIgnoreCase(itemid)
						||"w0315".equals(itemid)){
					continue;
				}
				examTime.setColumnLength(itemlength);//设置此列编辑时长度限制
				
				if("A".equals(itemtype)){//A:字符型  D:日期型 N:数值型  M:备注型
					if("0".equals(codesetid)||codesetid==null){//非代码字符型
						examTime.setCodesetId("0");
						if("w0301".equalsIgnoreCase(itemid)){
							examTime.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//设置此列显示类型
							examTime.setEncrypted(true);
						}else if("sub_committee_id".equalsIgnoreCase(itemid)){
							//二级单位受参数配置和功能权限控制
							if(collegeEval && userview.hasTheFunction("380050511")){
								//复合列头
								ColumnsInfo sub_committee = getColumnsInfo("sub_committee", JobtitleUtil.ZC_REVIEWFILE_STEP4SHOWTEXT, 215, 0, "A");
								
								examTime.setColumnDesc(JobtitleUtil.ZC_REVIEWFILE_STEP4SHOWTEXT);
								examTime.setColumnWidth(150);
								if(operationData.size()>0)//为空的时候生成表格控件会报错
									examTime.setOperationData(operationData);//绑定此列操作数据
								examTime.setEditableValidFunc("meeting_me.checkCell");
								examTime.setRendererFunc("meeting_me.committeeRenderer");
								
								ColumnsInfo perNum = getColumnsInfo("w0323", "人数", 65, 0, "N");
								perNum.setRendererFunc("meeting_me.openChoosePage");
								perNum.setEditableValidFunc("false");
								perNum.setTextAlign("left");
								
								sub_committee.addChildColumn(examTime);
								sub_committee.addChildColumn(perNum);
								columnTmp.add(sub_committee);
								continue;
							}else
								continue;
						}else if("committee_id".equalsIgnoreCase(itemid)){
							//高评委 受功能权限控制
							if(userview.hasTheFunction("380050513")){
								ColumnsInfo gpw = getColumnsInfo("gpw",JobtitleUtil.ZC_REVIEWFILE_STEP1SHOWTEXT, 215, 0, "A");
								if(operationData.size()>0)//为空的时候生成表格控件会报错
									examTime.setOperationData(operationData);//绑定此列操作数据
								examTime.setColumnDesc(JobtitleUtil.ZC_REVIEWFILE_STEP1SHOWTEXT);
								examTime.setColumnWidth(150);
								examTime.setEditableValidFunc("meeting_me.checkCell");
								examTime.setRendererFunc("meeting_me.committeeRenderer");
								
								ColumnsInfo perNum = getColumnsInfo("w0315", "人数", 65, 0, "N");
								perNum.setRendererFunc("meeting_me.openChoosePage");
								perNum.setEditableValidFunc("false");
								perNum.setTextAlign("left");
								
								gpw.addChildColumn(examTime);
								gpw.addChildColumn(perNum);
								columnTmp.add(gpw);
								continue;
							}else
								continue;
						}else{
							if("0".equals(state))
								examTime.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
							examTime.setEditableValidFunc("meeting_me.checkCell");
						}
					}else{//代码型字符
						examTime.setCodesetId(codesetid);//设置代码类
						if("w0321".equals(itemid)){
							examTime.setEditableValidFunc("false");
						}else if("b0110".equalsIgnoreCase(itemid)){//所属机构 
							examTime.setColumnWidth(150);
							examTime.setCodesetId("UM");
							examTime.setCtrltype("3");
							examTime.setNmodule("9");
							examTime.setCodeSetValid(false);
							examTime.setEditableValidFunc("meeting_me.checkCell");
						}else if("w0325".equalsIgnoreCase(itemid)){
							//同行专家评议  受功能权限控制
							if(userview.hasTheFunction("380050512")){
								examTime.setColumnDesc(JobtitleUtil.ZC_REVIEWFILE_STEP3SHOWTEXT);
								examTime.setColumnWidth(90);
								examTime.setTextAlign("center");
								examTime.setEditableValidFunc("false");
								examTime.setRendererFunc("meeting_me.outsideAndSubjecetsRenderer");
							}else
								continue;
						}else {
							if("0".equals(state))
								examTime.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
							examTime.setEditableValidFunc("meeting_me.checkCell");
						}
					}
					columnTmp.add(examTime);
				}else if("D".equals(itemtype)||"N".equals(itemtype)||"M".equals(itemtype)){//日期型。数值。备注
					
					examTime.setCodesetId("0");
					if("0".equals(state))
						examTime.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
					
					examTime.setEditableValidFunc("meeting_me.checkCell");
					columnTmp.add(examTime);
				}
				
			}
			//因为学科组没有在业务字典中体现，所以需要自定义列
			if(userview.hasTheFunction("380050514")){//学科组评议 受功能权限控制
				//复合列头
				ColumnsInfo subjectgroups = getColumnsInfo("subjectgroups", JobtitleUtil.ZC_REVIEWFILE_STEP2SHOWTEXT, 165, 0, "A");
				
				//自定义列 评审成员
				ColumnsInfo w0101 = getColumnsInfo("w0101", "评审成员", 100, 0, "M");
				w0101.setCodesetId("0");
				w0101.setTextAlign("center");
	            w0101.setRendererFunc("meeting_me.openSubjects");
	            w0101.setEditableValidFunc("false");
				
				ColumnsInfo groupCount = getColumnsInfo("groupnum", "组数", 65, 0, "N");
				groupCount.setEditableValidFunc("false");
				groupCount.setTextAlign("left");
				
				subjectgroups.addChildColumn(w0101);
				subjectgroups.addChildColumn(groupCount);
				columnTmp.add(columnTmp.size()-1,subjectgroups);
			}
			//自定义列隐藏列 会议状态
			ColumnsInfo column = new ColumnsInfo();
			column.setColumnId("w0321_h");
			column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			column.setColumnType("A");
			columnTmp.add(column);
			
    	} catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    	return columnTmp;
    }
	
	/**
	 * 将权限范围内所有聘委会封装成ArrayList<CommonData>
	 * @return 操作数据operationData
	 * @throws GeneralException 
	 */
	private ArrayList<CommonData> getOperationData() throws GeneralException{
		ArrayList<CommonData> operationData = new ArrayList<CommonData>();
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select committee_id,committee_name from zc_committee ");
			sql.append(" where 1=1 ");
			//排除掉没有添加专家的评委会  haosl 20170620
			sql.append("and committee_id in (select distinct(committee_id) from zc_judgingpanel_experts) ");
			sql.append(new JobtitleUtil(this.conn, this.userview).getB0110Sql_down(this.userview.getUnitIdByBusi("9")));
			rs = dao.search(sql.toString());
			while (rs.next()) {
				String name = rs.getString("committee_name");
				String value = rs.getString("committee_id");
				CommonData cd = new CommonData();
				cd.setDataName(name);
				cd.setDataValue(value);
				operationData.add(cd);
			}
			if(operationData.size()>0){
				CommonData cd = new CommonData();
				cd.setDataName("　");
				cd.setDataValue(null);
				operationData.add(0, cd);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		return operationData;
	}
    
    /**
     * 列头ColumnsInfo对象初始化
     * @param columnId 编号
     * @param columnDesc 名称
     * @param columnWidth 列宽
     * @param decimalWidth 小数位长度
     * @param type 数据类型
     * @return
     */
    private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth, int decimalWidth, String type) {
        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId(columnId);
        columnsInfo.setColumnDesc(columnDesc);
        //columnsInfo.setCodesetId("");// 指标集
        columnsInfo.setColumnWidth(columnWidth);// 显示列宽
        if("w0307".equalsIgnoreCase(columnId)||"w0315".equalsIgnoreCase(columnId)){
        	type = "A";
        }
        columnsInfo.setColumnType(type);// 类型N|M|A|D
        if ("A".equals(type))
            columnsInfo.setCodesetId("0");
        columnsInfo.setDecimalWidth(decimalWidth);// 小数位
        if ("D".equals(type) || "N".equals(type))
            columnsInfo.setTextAlign("right");// 数值和日期默认居右
        return columnsInfo;
    }
    
    /**
     * 表格工具栏功能按钮
     * @return
     */
	@SuppressWarnings("unchecked")
	public ArrayList getButtonList() {
		ArrayList buttonList  = new ArrayList();
		if(userview.hasTheFunction("380050502"))
			buttonList.add(newButton("新增", "meeting_me.addMeeting", "true", null, null));
		if(userview.hasTheFunction("380050504"))
			buttonList.add(newButton("撤销", "meeting_me.deleteMeeting", "true", null, null));
		if(userview.hasTheFunction("380050503"))
			buttonList.add(newButton("保存", "meeting_me.saveMeeting", "true", null, null));

//		if(userview.hasTheFunction("380050505"))
//			buttonList.add(newButton("生成材料审核账号密码", "meeting_me.randomCreate", "true", null, null));
		if(userview.hasTheFunction("380050506"))
			buttonList.add(newButton("启动", "meeting_me.startTheMeeting", "true", null, null));
		if(userview.hasTheFunction("380050507"))	
			buttonList.add(newButton("暂停", "meeting_me.pauseMeeting", "true", null, null));
		//if(userview.hasTheFunction("380050508"))	
			//buttonList.add(newButton("结束", "meeting_me.endMeeting", "true", null, null));
		if(userview.hasTheFunction("380050510")){
			ButtonInfo buttonInfo = new ButtonInfo("发送通知", "meeting_me.sendMessage");
			buttonList.add(buttonInfo);
		}
		
		ButtonInfo queryBox = new ButtonInfo();
		queryBox.setType(ButtonInfo.TYPE_QUERYBOX);
		queryBox.setText("请输入会议名称");
		queryBox.setFunctionId("ZC00002301");
		buttonList.add(queryBox);
		return buttonList;
	}
    
    /**
     * 生成功能按钮
     * @param text 按钮文字
     * @param handler 按钮事件
     * @param getdata 事件触发时是否获取选中数据
     * @param icon 按钮图标
     * @param id 按钮编号
     * @return
     */
    private ButtonInfo newButton(String text, String handler, String getdata, String icon, String id) {
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
     * 得到页面加载sql
     * @return
     */
    @SuppressWarnings("unchecked")
	public String getSelectSql() {
    	ArrayList fieldList=DataDictionary.getFieldList("W03",Constant.USED_FIELD_SET);
    	StringBuffer datasql =  new StringBuffer();
		for(int i=0;i<fieldList.size();i++){
			FieldItem item=(FieldItem)fieldList.get(i);		
			String itemid=item.getItemid();//字段id
			if("w0325".equalsIgnoreCase(itemid)) {
				datasql.append(" "+Sql_switcher.isnull("w0325", "2")+" as w0325,");
			}else if("w0315".equalsIgnoreCase(itemid)
						||"w0323".equalsIgnoreCase(itemid)){
				datasql.append(" "+Sql_switcher.isnull(itemid, "0")+" as "+itemid+",");
			}else {
				datasql.append(" "+itemid+",");
			}
			if(i==fieldList.size()-1){
				datasql.deleteCharAt(datasql.length()-1);
			}
		}
		String sb = "";
		sb+=" (select "+datasql;
		sb+=",'评审成员' as w0101,w03.w0321 as w0321_h,(select COUNT(distinct(group_id)) from zc_expert_user z where z.w0301=W03.W0301) as groupnum,create_time "+" from W03 ";
		//得到登录人的职称管理范围
		String unit = this.userview.getUnitIdByBusi("9");
         if(!"UN`".equals(unit) && !userview.isSuper_admin()){
         	String [] unitarr = unit.split("`");
         	sb+=" where (";
         	for(int m=0;m<unitarr.length;m++){
         		String arr = unitarr[m];
         		arr = arr.substring(2,arr.length());
         		sb+=" b0110 like '"+arr+"%' or ";
         	}
         	// 权限小的用户，要能够看到所属机构为空的评审会议 chent 20171025 add
         	sb+=" nullif(b0110, '') is null" ;
         	sb+=")";
         }
         sb+=")";
         
		return sb;
	}
    
    /**
     * 得到要修改的评审会议字段列
     * @return
     */
    @SuppressWarnings("unchecked")
	public ArrayList getIdlist() {
		ArrayList idlist = new ArrayList();
		ArrayList fieldList=DataDictionary.getFieldList("W03",Constant.USED_FIELD_SET);
		for(int i=0;i<fieldList.size();i++){
			FieldItem item=(FieldItem)fieldList.get(i);		
			String itemid=item.getItemid();//字段id
			String state=item.getState();//0隐藏  1显示
			if("1".equals(state))
				idlist.add(itemid);
			else if("w0301".equals(itemid))
				idlist.add(itemid);
		}
		return idlist;
	}
    /**
     * 得到指标日期格式
     * @param fieldid 指标ID
     * @return
     */
    public String getDateType(String fieldid){
    	String dateType = "";
		FieldItem item = DataDictionary.getFieldItem(fieldid, 1);
		if(item!=null&&"D".equals(item.getItemtype())){
			dateType = "yyyy-MM-dd";
			int itemlength=item.getItemlength();
			if(itemlength==4)
				dateType = "yyyy";
			else if (itemlength==7)
				dateType = "yyyy-MM";
			else if (itemlength==10)
				dateType = "yyyy-MM-dd";
			else if (itemlength==16)
				dateType = "yyyy-MM-dd HH:mm";
			else if (itemlength==18)
				dateType = "yyyy-MM-dd HH:mm:ss";
		}
    	return dateType;
    }
    
    /**
     * 启动会议时，附加业务操作
     * @param message 提示信息
     * @param w0301   会议编号
     * @throws GeneralException
     */
    public void executeBusiness(StringBuffer message, String w0301) throws GeneralException{
    	RowSet rs = null;
    	try {
    		/*//获得评审阶段状态
    		HashMap<String, Boolean> steps = getEnableSteps(w0301);
    		boolean step1 = steps.get("step1");
    		boolean step2 = steps.get("step2");
    		boolean step3 = steps.get("step3");
    		boolean step4 = steps.get("step4");
    		
    		if(!this.userview.hasTheFunction("380050513")&&
    				!this.userview.hasTheFunction("380050511")&&
    				!this.userview.hasTheFunction("380050514")&&
    				!this.userview.hasTheFunction("380050512")){
    			//没有任何阶段的权限时 提示用户
				message.append("您没有评审阶段的权限，请联系管理员！"); 
    		}
    		
    		
    		if(!step1 && !step2 && !step3 && !step4){
    			String w0303 = "";
    			dao = new ContentDAO(this.conn);
    			StringBuffer sql = new StringBuffer();
    			sql.append("select w0303 from w03 where w0301 ='"+w0301+"'");
    			rs = dao.search(sql.toString());
    			if(rs.next())
        			w0303 = rs.getString("w0303");
    			message.append((w0303.length()>0?"评审会议【"+w0303+"】":"")+" 至少需要启用一个阶段!<br />");
    		}*/
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
    } 
    /**
     * 通知提醒-给申报人发送信息
     * @param contenMsg 信息内容
     * @param w0301   会议编号
     * @param sendlist   发送方式
     */
    public String getDeclarer(String w0301, String contenMsg, ArrayList sendlist) throws GeneralException{
        String msg = "";
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
            try {
                StringBuffer sql = new StringBuffer();
                //select W0503,W0505 from w05 where W0301='W0301'
                sql.append("select w0503,w0505");
                sql.append(" from w05");
                sql.append(" where w0301=?");
                ArrayList<String> list = new ArrayList<String>();
                list.add(w0301);
                
                rs = dao.search(sql.toString(), list);
                while(rs.next()){
                    String w0503 = rs.getString("w0503");//库前缀
                    String w0505 = rs.getString("w0505");//人员编号
                    String phoneId=ConstantParamter.getMobilePhoneField().toLowerCase();//电话指标
                    String emailId=ConstantParamter.getEmailField().toLowerCase();//邮件指标
                    
                    StringBuffer select_sql = new StringBuffer();
                    select_sql.append("select "+phoneId+","+emailId+",a0101");
                    select_sql.append(" from "+w0503+"A01");
                    select_sql.append(" where a0100='"+w0505+"'");
                    
                    RowSet rset = dao.search(select_sql.toString());
                    if(rset.next()){
                        String phone = rset.getString(phoneId);//邮件
                        String email = rset.getString(emailId);//电话
                        String weixin = getWeiXinNum(w0503, w0505);//微信
                        //发送方式
                        msg = sendMode(sendlist, contenMsg, email, phone, weixin);
                    }
                    PubFunc.closeDbObj(rset);
                }
            } catch (Exception e) {
                msg = e.getMessage();
                e.printStackTrace();
                throw GeneralExceptionHandler.Handle(e);
            }finally{
                PubFunc.closeDbObj(rs);
            }
           return msg;
    }
    /**
     * 通知提醒-给评委会人员发送信息
     * @param contenMsg 信息内容
     * @param w0301   会议编号
     * @param sendlist   发送方式
     */
    public String getJudges(String w0301, String contenMsg, ArrayList sendlist,int type,boolean isNewModule) throws GeneralException{
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        String msg = "";
            try {
                StringBuffer sql = new StringBuffer();
                ArrayList list = new ArrayList();
                list.add(w0301);
                if(isNewModule) {
            		sql.append("select w0101 from zc_judgingpanel_experts zcj,w03 where zcj.flag='1' and w03.w0301=? ");
            		if(type==1)
            			sql.append("and w03.committee_id");
            		else
            			sql.append("and w03.sub_committee_id");
                	sql.append("=zcj.committee_id");
                	
                }else {
                	sql.append("select w0101");
                	sql.append(" from zc_expert_user");
                	sql.append(" where w0301=? and type=? and W0501='xxxxxx'");
                	list.add(type);
                }
                
                
                rs = dao.search(sql.toString(), list);
                while(rs.next()){
                    String w0101 = rs.getString("w0101");//获取评委会 的专家编号
                    //发送方法
                    msg = numTosend(w0101, contenMsg, sendlist);
                }
            } catch (Exception e) {
                msg = e.getMessage();
                e.printStackTrace();
                throw GeneralExceptionHandler.Handle(e);
            }finally{
                PubFunc.closeDbObj(rs);
            }
            return msg;
    }
    /**
     * 通知提醒-给学科组人员发送信息
     * @param contenMsg 信息内容
     * @param w0301   会议编号
     * @param sendlist   发送方式
     */
    public String getSubject(String w0301, String contenMsg, ArrayList sendlist, ArrayList subjectlist) throws GeneralException{
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        String msg = "";
            try {
                StringBuffer sql = new StringBuffer();
                //select distinct w0101 from zc_expert_user where w0301='0000000040' and type=2 and group_id in ('', '', '')
                sql.append("select distinct w0101 from w01 where w0101 in (");
                sql.append("select distinct w0101");
                sql.append(" from zc_expert_user");
                sql.append(" where w0301='"+w0301+"' and type=2");
                sql.append(" and group_id in (");
                for(int i=0;i<subjectlist.size();i++){
                    String subjectid = (String) subjectlist.get(i);
                    sql.append("'"+subjectid+"',");
                }
                sql.append("''))");
                
                rs = dao.search(sql.toString());
                while(rs.next()){
                    String w0101 = rs.getString("w0101");//获取学科组 的专家编号
                    //发送方法
                    msg = numTosend(w0101, contenMsg, sendlist);
                }
            } catch (Exception e) {
                msg = e.getMessage();
                e.printStackTrace();
                throw GeneralExceptionHandler.Handle(e);
            }finally{
                PubFunc.closeDbObj(rs);
            }
            return msg;
    }
    /**
     * 通知提醒-通过专家编号获取信息并发送
     * @param w0101 专家编号
     * @param contenMsg   消息内容
     * @param sendlist   发送方式
     */
    public String numTosend(String w0101, String contenMsg, ArrayList sendlist) throws GeneralException{
        ContentDAO dao = new ContentDAO(this.conn);
        String msg = "";
        try {
            StringBuffer select_sql = new StringBuffer();
            select_sql.append("select w0113,w0115");
            select_sql.append(" from w01");
            select_sql.append(" where w0101=?");
            
            ArrayList<String> list = new ArrayList<String>();
            list.add(w0101);
            
            //通过专家编号 获取专家信息
            RowSet rset = dao.search(select_sql.toString(), list);
            if(rset.next()){
                String email = rset.getString("w0113");//邮件
                String phone = rset.getString("w0115");//电话
                
                ArrayList dbnameList = DataDictionary.getDbpreList();// DataDictionary.getDbpreList();  this.userview.getPrivDbList();
                String dbname = "";
                String a0100 = "";
                StringBuffer numsql = new StringBuffer();
                RowSet numset = null;
                for(int i=0;i<dbnameList.size();i++){
                    String dbnamei = String.valueOf(dbnameList.get(i));
                    numsql = new StringBuffer();
                    numsql.append("select a0100");
                    numsql.append(" from "+dbnamei+"A01");
                    numsql.append(" where GUIDKEY in (select guidKey From w01 where w0101='"+w0101+"')");
                    
                    numset = dao.search(numsql.toString());
                    if(numset.next()){
                        a0100 =  numset.getString("a0100");//人员编号
                        if(!"".equalsIgnoreCase(a0100) && a0100!=null){
                            dbname = dbnamei;
                            break;
                        }
                    }
                }
                PubFunc.closeDbObj(numset);
                String weixin = getWeiXinNum(dbname, a0100);
//                String weixin = rset.getString("w0117");//微信
                //发送方式
                msg = sendMode(sendlist, contenMsg, email, phone, weixin);
            }
            PubFunc.closeDbObj(rset);
        }catch (Exception e) {
            msg = e.getMessage();
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return msg;
    }
    /**
     * 通知提醒-邮件模板
     * @param content 信息内容
     * @param email_address   邮件地址
     */
    public LazyDynaBean sendEMail(String email_address, String content) throws GeneralException {
        LazyDynaBean emailbean = new LazyDynaBean();
        try {
            emailbean.set("toAddr", email_address);
            emailbean.set("subject", "评审会议通知");
            emailbean.set("bodyText", content);
            emailbean.set("href", "");
            emailbean.set("hrefDesc", "");                   

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return emailbean;
    }
    /**
     * 通知提醒-获取微信号方法
     * @param nbase 库前缀
     * @param a0100 人员编号
     */
    public String getWeiXinNum(String nbase, String a0100) throws GeneralException{
        String username = "";
        if(!"".equalsIgnoreCase(nbase) && !"".equalsIgnoreCase(a0100)){
            RecordVo login_vo = ConstantParamter
                    .getConstantVo("SS_LOGIN_USER_PWD");
            if (login_vo == null) {
                username = "username";
            } else {
                String login_name = login_vo.getString("str_value").toLowerCase();
                int idx = login_name.indexOf(",");
                if (idx == -1) {
                    username = "username";
                } else {
                    username = login_name.substring(0, idx);
                    if ("#".equals(username) || "".equals(username)) {
                        username = "username";
                    }
                }
            }
            String sql = "select "+username+" from "+nbase+"a01 where a0100 = '"+a0100+"'";
            ContentDAO dao = null;
            RowSet rs = null;
            try{
                dao = new ContentDAO(this.conn);
                rs = dao.search(sql);
                while(rs.next()){
                    username = rs.getString(username)==null?"":rs.getString(username);
                }
                
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                PubFunc.closeResource(rs);
            }
        }
        return username;
    }
    /**
     * 通知提醒-发送方式
     * @param sendlist 已选中的发送方式
     * @param contenMsg 消息内容
     * @param email 邮箱地址
     * @param phone 手机号
     * @param weixin 微信号
     */
    public String sendMode(ArrayList sendlist, String contenMsg, String email, String phone, String weixin) throws GeneralException{
        String msg = "";
        try{
            if(sendlist.contains("邮件")){
                /**邮件*/
                LazyDynaBean emailbean = sendEMail(email, contenMsg);
                AsyncEmailBo emailbo = new AsyncEmailBo(this.conn, this.userview);
                emailbo.send(emailbean);
            }else if(sendlist.contains("短信")){
                /**短信*/
//                NoteCheckSend note = new NoteCheckSend();
//                note.sendMessage(phone, contenMsg);
                
                HashMap<String, String> map = this.getSmsConfig();
                
                String comm_port = map.get("port");//端口
                String pin = map.get("pin");//PIN码
                
                if(StringUtils.isNotEmpty(comm_port) && StringUtils.isNotEmpty(pin)){
                	SMSSender sMSSender = SMSSender.getInstance(comm_port, pin);
                	contenMsg = contenMsg.replaceAll("&nbsp;", "");//去除html标签：空格符
                	contenMsg = contenMsg.replaceAll("&emsp;", "");//去除html标签：制表符
                	contenMsg = contenMsg.replaceAll("<br>", "");//去除html标签：换行符
                	sMSSender.send(phone, contenMsg);
                }
            }else if(sendlist.contains("微信")){
                /**微信*/
            	contenMsg = contenMsg.replaceAll("&nbsp;", "");//去除html标签：空格符
            	contenMsg = contenMsg.replaceAll("&emsp;", "");//去除html标签：制表符
            	contenMsg = contenMsg.replaceAll("<br>", "");//去除html标签：换行符
                WeiXinBo.sendMsgToPerson(weixin, "评审会议通知", contenMsg, "", "");
            }
            else if(sendlist.contains("钉钉")){
                /**钉钉*/
            	contenMsg = contenMsg.replaceAll("&nbsp;", "");//去除html标签：空格符
            	contenMsg = contenMsg.replaceAll("&emsp;", "");//去除html标签：制表符
            	contenMsg = contenMsg.replaceAll("<br>", "");//去除html标签：换行符
            	DTalkBo.sendMessage(weixin, "评审会议通知", contenMsg, "", "");
            }
        } catch (Exception e) {
            msg = e.getMessage();
            throw GeneralExceptionHandler.Handle(e);
        }
        return msg;
    }
    /**
     * 获取短信配置
     * @return HashMap<String, String>--port：端口  pin：PIN码
     * @throws GeneralException
     */
    private HashMap<String, String> getSmsConfig() throws GeneralException{
    	HashMap<String, String> map = new HashMap<String, String>();
    	
    	try{
	        RecordVo sms_vo=ConstantParamter.getConstantVo("SS_SMS_OPTIONS");
	        if(sms_vo == null) {
	        	return map;
	        }
	        String param = sms_vo.getString("str_value");
	        if(StringUtils.isEmpty(param)) {
	        	return map;
	        }
	        
	        Document doc = PubFunc.generateDom(param);
	        String xpath = "//port[@valid=\"true\"]";	        
	        XPath reportPath = XPath.newInstance(xpath);
	        List childlist = reportPath.selectNodes(doc);
	        if(childlist.size()!=0) {
	        	Element ele = null;
	        	ele = (Element)childlist.get(0);
	        	String port = ele.getAttributeValue("name");//端口
	        	String pin = ele.getAttributeValue("pin");//PIN码
	        	//int bit=Integer.parseInt(ele.getAttributeValue("bit"));//比特率
	        	map.put("port", port);
	        	map.put("pin", pin);
	        }
    	} catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        }    
    	
    	return map;
    }
    /**
     * 查询出所有评委会,用于为前台评委会列赋值 bug 27432 
     * 
     * @author haosl
     * @throws GeneralException 
     */
    public Map<String,String> getCommiteeInfo() throws GeneralException{
    	Map<String,String> map = new HashMap<String,String>();
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select committee_id,committee_name from zc_committee");
			rs = dao.search(sql.toString());
			while (rs.next()) {
				String value = rs.getString("committee_name");
				String name = rs.getString("committee_id");
				map.put(name, value);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		
		return map;
    }
    /**
     * 选择评委会报错后自动添加评委会的中的所有人员
     * @param w0301
     * @param committee_id
     * @param type
     * 		=1 评委会    =4 二级单位
     * @return 返回增加的专家人数
     * @throws GeneralException
     */
    public int addCommitteePersons(String w0301, String committee_id,int type) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			//获取评委会人员信息
    		StringBuilder sql = new StringBuilder();
    		sql.append("select w0101,role ");
    		sql.append("from zc_judgingpanel_experts ");
    		sql.append("where committee_id=? and flag='1' ");

    		ArrayList<String> list = new ArrayList<String>();
    		list.add(committee_id);
    		
			rs = dao.search(sql.toString(), list);
			
			rs.last();
			int rowcount = rs.getRow();//获得评委会下专家的条数
			if(rowcount == 0){//0条的话，只增加一条模板记录就可以了。
				RecordVo vo = new RecordVo("zc_expert_user");
				
				IDFactoryBean idf = new IDFactoryBean();
				vo.setString("user_id", idf.getId("zc_expert_user.user_id", "", conn));
				vo.setString("w0301", w0301);
				vo.setString("w0501", "xxxxxx");
				vo.setString("username", null);
				vo.setString("password", null);
				vo.setInt("state", 0);
				vo.setInt("type", type);
				vo.setString("w0101", null);
				vo.setString("description", null);
				vo.setString("role", "0");
				
				dao.addValueObject(vo);
			}else{//不为0条的话，就要增加相应的条数
				rs.beforeFirst();//将游标移到第一行前
				while (rs.next()) {
					String w0101 = rs.getString("w0101");
					String role = rs.getString("role");
					addPerson(w0301,w0101,role,type);
				}
			}
			
			return rowcount;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}  finally{
			PubFunc.closeDbObj(rs);
		}
	}
    /**
	 * 新增评委会
	 * @param w0301:会议编号
	 * @param group_id:学科组编号
	 * @param w0101:专家编号
	 * @param type =1 评委会  =4 二级单位
	 * @return msg
	 * @throws GeneralException
	 */
	private String addPerson(String w0301, String w0101,String role,int type) throws GeneralException{
		
		String msg = "";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RecordVo vo = new RecordVo("zc_expert_user");
			
			IDFactoryBean idf = new IDFactoryBean();
			vo.setString("user_id", idf.getId("zc_expert_user.user_id", "", conn));
			vo.setString("w0301", w0301);
			vo.setString("w0501", "xxxxxx");
			vo.setString("username", null);
			vo.setString("password", null);
			vo.setInt("state", 0);
			vo.setInt("type", type);
			vo.setString("w0101", w0101);
			vo.setString("description", null);
			vo.setString("role", role);
			
			dao.addValueObject(vo);
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return msg;
	}
	/**
	 * 删除指定会议下的关联的专家
	 * @param w0301
	 * @param type
	 * @throws GeneralException
	 */
	public void removeCommitteePersons(String w0301,int type) throws GeneralException{
		
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("delete from zc_expert_user ");
			sql.append("where w0301=? and type=?");
			List values = new ArrayList();
			values.add(w0301);
			values.add(type);
			dao.delete(sql.toString(), values);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
	public void saveCommitteePesons(String w0301,String committee_id,String sub_committee_id) throws GeneralException{
		ContentDAO dao  = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			if(StringUtils.isNotBlank(w0301)){
				w0301 = PubFunc.decrypt(w0301);
				
				String sql = "select committee_id,sub_committee_id from w03 where w0301=?";
				List<String> values = new ArrayList<String>();
				values.add(w0301);
				rs = dao.search(sql,values);
				String o_committee_id = "";//修改之前的评委会id
				String o_sub_committee_id = ""; //修改之前的二级单位id
				if(rs.next()){
					o_committee_id=rs.getString("committee_id");
					o_sub_committee_id=rs.getString("sub_committee_id");
				}
				//保存时，当选择了评委会时，需要自动将评委会下的专家关联到评审会议
				if(StringUtils.isBlank(committee_id) || !committee_id.equals(o_committee_id)){//评委会
					//增加专家前需要清掉之前的专家
					this.removeCommitteePersons(w0301, 1);
					int count = 0;
					if(StringUtils.isNotBlank(committee_id))
						count = this.addCommitteePersons(w0301, committee_id, 1);
					dao.update("update W03 set committee_id='"+committee_id+"',W0315 = "+count+" where W0301 = '"+ w0301 +"'");//更新评委会参会人数
				}
				if(StringUtils.isBlank(sub_committee_id) || !sub_committee_id.equals(o_sub_committee_id)){//二级单位
					//增加专家前需要清掉之前的专家
					this.removeCommitteePersons(w0301, 4);
					int count = 0;
					if(StringUtils.isNotBlank(sub_committee_id))
						count = this.addCommitteePersons(w0301, sub_committee_id, 4);
					dao.update("update W03 set sub_committee_id='"+sub_committee_id+"',W0323 = "+count+" where W0301 = '"+ w0301 +"'");//更新二级单位参会人数
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
	}
	
	/**
	 * 获得可用的发送方式（邮箱，短信，微信，钉钉）
	 * @throws GeneralException 
	 */
	public HashMap<String, Boolean> getEnableModes() throws GeneralException{
		// 短信
		HashMap<String, Boolean> enableModes = new HashMap<String, Boolean>();
		boolean smsflag = true;// 短信
		boolean emailflag = true;// 邮箱
		boolean weixinflag = true;// 微信
		boolean ddflag = true;// 钉钉
		HashMap<String, String> map = getSmsConfig();
		if (map.isEmpty())
			smsflag = false;

		RecordVo email_vo = ConstantParamter.getConstantVo("SS_STMP_SERVER");
		
		if (email_vo == null){
			emailflag = false;
		}else{
			try {
				String email_param = email_vo.getString("str_value");
				String ehost = "",eusername = "",epassword = "",efromAddr = "",emaxSend="",eport="",eauthy="";
				Document doc = PubFunc.generateDom(email_param);
				Element root = doc.getRootElement();
				Element child = (Element) root.getChildren().get(0);
				
				ehost = child.getAttributeValue("host");
				eusername = child.getAttributeValue("username");
				epassword = child.getAttributeValue("password");
				efromAddr = child.getAttributeValue("from_addr");
				emaxSend = child.getAttributeValue("max_send");
				eport = child.getAttributeValue("port");

				if(StringUtils.isBlank(ehost)
						&&StringUtils.isBlank(eusername)
						&&StringUtils.isBlank(epassword)
						&&StringUtils.isBlank(efromAddr)
						&&StringUtils.isBlank(emaxSend)
						&&StringUtils.isBlank(eport)) {
					emailflag = false;
				}
			} catch (Exception e) {
				throw GeneralExceptionHandler.Handle(e);
			}
		}
		//微信相关配置，全部为空则不显示微信通知选项   haosl 2017-9-19
		String wxcorpid = ConstantParamter.getAttribute("wx", "corpid");
		String wxcorpsecret = ConstantParamter.getAttribute("wx", "corpsecret");
		String wxurl = ConstantParamter.getAttribute("wx", "url");
		String wxtoken = ConstantParamter.getAttribute("wx", "token");
		String wxencodingaeskey = ConstantParamter.getAttribute("wx", "encodingaeskey");
		String wxagentid = ConstantParamter.getAttribute("wx", "agentid");
		if(StringUtils.isEmpty(wxcorpid) 
				&& StringUtils.isEmpty(wxcorpsecret)
				&& StringUtils.isEmpty(wxurl)
				&& StringUtils.isEmpty(wxtoken)
				&& StringUtils.isEmpty(wxencodingaeskey)
				&& StringUtils.isEmpty(wxagentid))
			weixinflag = false;
		//钉钉
		RecordVo dd_vo = ConstantParamter.getConstantVo("DINGTALK");
		if(dd_vo == null){
			ddflag = false;
		}else{
			if (dd_vo != null)
			{
				try {
					String ddcropid = "",ddcorpsecret = "",ddagentid = "",dduserid = "";
					Document doc = PubFunc.generateDom(dd_vo.getString("str_value"));
					Element root = doc.getRootElement();
					List list = root.getChildren();
					for (int i = 0; i < list.size(); ++i)
					{
						Element child = (Element) list.get(i);
						String key = child.getAttributeValue("key");
						if("corpid".equals(key))
							ddcropid = child.getAttributeValue("value");
						else if ("corpsecret".equals(key))
							ddcorpsecret = child.getAttributeValue("value");
						else if ("agentid".equals(key))
							ddagentid = child.getAttributeValue("value");
						else if ("userid".equals(key))
							dduserid = child.getAttributeValue("value");
					}
					if(StringUtils.isBlank(ddcropid)
							&&StringUtils.isBlank(ddcorpsecret)
							&&StringUtils.isBlank(ddagentid)
							&&StringUtils.isBlank(dduserid)) {
						ddflag = false;
					}
				} catch (Exception e) {
					throw GeneralExceptionHandler.Handle(e);
				}
			}
		}
		enableModes.put("smsflag", smsflag);
		enableModes.put("emailflag", emailflag);
		enableModes.put("weixinflag", weixinflag);
		enableModes.put("ddflag", ddflag);
		
		return enableModes;
		 
	}
	/**
	 * 获取会议关联的分类代码参数
	 * @param w0301
	 * @return
	 * chent
	 */
	public HashMap<String, String> getW03Ctrl_param(String w0301) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		try {
			RecordVo vo = new RecordVo("w03");
			vo.setString("w0301", w0301);
			vo = new ContentDAO(this.conn).findByPrimaryKey(vo);
			
			String ctrl_param = vo.getString("ctrl_param");
			
			if(StringUtils.isNotEmpty(ctrl_param)) {
		        Document doc = PubFunc.generateDom(ctrl_param);

		        Element root = doc.getRootElement();
		        Element position_levels = root.getChild("position_levels");
	            List position_level_list =  position_levels.getChildren("position_level");
	            
	            for(Object o : position_level_list) {
	            	Element el = (Element)o;
	            	String review_links_value = el.getAttribute("review_links").getValue();// 阶段
	            	
            		String value = el.getText();// 职级分类
            		map.put(review_links_value, value);
	            }
				
			} else {//没有配置，默认插一套默认
				String xmlValue = "<?xml version=\"1.0\" encoding=\"GB2312\"?><params><position_levels></position_levels></params>";
				
				String sql="update w03 set ctrl_param=? where w0301=?";
				new ContentDAO(this.conn).update(sql, Arrays.asList(new String[] {xmlValue, w0301}));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return map;
	}
	/**
	 * 保存会议关联的分类代码参数
	 * @param w0301
	 * @param configMap
	 * @return
	 * chent
	 */
	public int saveW03Ctrl_param(String w0301, String review_links, HashMap<String, String> configMap) {
		int errorcode = 1;
		
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RecordVo vo = new RecordVo("w03");
			vo.setString("w0301", w0301);
			vo = dao.findByPrimaryKey(vo);
			
			String ctrl_param = vo.getString("ctrl_param");
			
	        Document doc = PubFunc.generateDom(ctrl_param);
	        Element root = doc.getRootElement();
	        
	        // 删除原配置参数
	        Element position_levels = root.getChild("position_levels");
            List position_level_list =  position_levels.getChildren("position_level");
            
            ArrayList<Element> removeList = new ArrayList<Element>();
            String removeText = "";
	        for(Object o : position_level_list) {
            	Element el = (Element)o;
            	String review_links_value = el.getAttribute("review_links").getValue();// 阶段
            	if(review_links_value.equalsIgnoreCase(review_links)) {
            		removeList.add(el);
            		
            		removeText = el.getText();
            	}
            }
	        for(Element o : removeList) {
            	position_levels.removeContent(o);
            }
	        
	        // 插入新配置参数
	        Iterator iter = configMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry)iter.next();
				String key = (String)entry.getKey();
				String value = (String)entry.getValue();
				
				Element el = new Element("position_level");
				el.setAttribute("review_links", key);
				el.setText(value);
				position_levels.addContent(el);
				
				// 删除原配置参数关联的申报人数和申报人
				StringBuilder notInStr = new StringBuilder();
				String[] c_levelArr = value.split(",");
            	for(String r : c_levelArr) {
            		notInStr.append("'"+r+"',");
            		
            		if(StringUtils.isNotEmpty(removeText) && removeText.indexOf(r) < 0) {
            			// 清空人数,只清除被取消掉的职级的人数。
            			String upSql = "update zc_personnel_categories set c_"+r + "=0 where w0301=? and review_links=?";
            			dao.update(upSql, Arrays.asList(new String[] {w0301, review_links}));
            		}
            	}
            	notInStr.deleteCharAt(notInStr.length()-1);
            	String delSql = "delete from zc_categories_relations where c_level not in("+notInStr+") and categories_id in (select categories_id from zc_personnel_categories where w0301=? and review_links=?)";
            	dao.delete(delSql, Arrays.asList(new String[] {w0301, review_links}));
            	
            	// 删除栏目设置表，否则老的列和新的同步后的列会冲突。
            	String submoduleid = "jobtitle_reviewfile_diff_" + review_links;
            	dao.delete("delete from t_sys_table_scheme_item where scheme_id=(select scheme_id from t_sys_table_scheme where submoduleid=?)", Arrays.asList(new String[] {submoduleid}));
            	dao.delete("delete from t_sys_table_scheme where submoduleid=?", Arrays.asList(new String[] {submoduleid}));
			}
			
			// 更新表
            Format format = Format.getRawFormat();//设置xml字体编码，然后输出为字符串
        	format.setEncoding("UTF-8");
            XMLOutputter output=new XMLOutputter(format);
        	String xml = output.outputString(doc);//最终处理后xml
        	vo.setString("ctrl_param", xml);
        	
			int result = dao.updateValueObject(vo);
			if(result == 1) {
				errorcode = 0;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return errorcode;
	}
}
