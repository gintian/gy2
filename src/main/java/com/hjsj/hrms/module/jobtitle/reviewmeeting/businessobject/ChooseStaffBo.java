package com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject;

import com.hjsj.hrms.module.jobtitle.configfile.businessobject.JobtitleConfigBo;
import com.hjsj.hrms.module.jobtitle.utils.JobtitleUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title:ChooseStaffBo </p>
 * <p>Description: 操作参会人员Bo类</p>
 * <p>Company: hjsj</p> 
 * <p>create time: 2015-12-31</p>
 * @author liuy
 * @version 1.0
 */
public class ChooseStaffBo {
	Connection conn;
    ContentDAO dao;
    UserView userview;
    
    public ChooseStaffBo() {

    }
    public ChooseStaffBo(Connection conn) {
        this.conn = conn;
    }
    public ChooseStaffBo(Connection conn, UserView userview) {
        this.conn = conn;
        this.userview = userview;
    }
    
    /**
     * 获取列头，表格渲染
     * @return 渲染列头
     * @throws GeneralException 
     */
	public ArrayList<ColumnsInfo> getColumnList(String w0321) throws GeneralException{
    	ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
    	try{
    		ColumnsInfo user_id = getColumnsInfo("user_id", "账号表ID", 60, "A");
    		user_id.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            columnTmp.add(user_id);
    		
    		// 评审会议编号
            ColumnsInfo w0301 = getColumnsInfo("w0301", "会议ID", 60, "A");
            w0301.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            columnTmp.add(w0301);
            
            //学科组编号
            ColumnsInfo w0101 = getColumnsInfo("w0101", JobtitleUtil.ZC_MENU_SUBJECTSSHOWTEXT+"编号", 60, "A");
            w0101.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            columnTmp.add(w0101);
            
            //申报人主键序号ID 
            ColumnsInfo w0501 = getColumnsInfo("w0501", "申报人主键序号ID ", 60, "A");
            w0501.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            columnTmp.add(w0501);
            
            //专家编号
            ColumnsInfo group_id = getColumnsInfo("group_id", "专家编号", 60, "A");
            group_id.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            columnTmp.add(group_id);

            // 单位名称
            ColumnsInfo w0103 = getColumnsInfo("w0103", "单位名称", 135, "A");
            w0103.setEditableValidFunc("false");
            columnTmp.add(w0103);

            // 部门
            ColumnsInfo w0105 = getColumnsInfo("w0105", "部门", 135, "A");
            w0105.setEditableValidFunc("false");
            columnTmp.add(w0105);

            // 姓名
            ColumnsInfo w0107 = getColumnsInfo("w0107", "姓名", 100, "A");
            w0107.setEditableValidFunc("false");
            columnTmp.add(w0107);
            
            // 是否支持审核
			JobtitleConfigBo jobtitleConfigBo = new JobtitleConfigBo(this.conn,this.userview);
			boolean support_checking = jobtitleConfigBo.getParamConfig("support_checking");
			//支持审核时显示 审核账号列  haosl 20170620
			if(support_checking){
	            // 账号
	            ColumnsInfo username = getColumnsInfo("username", "材料审核账号", 145, "A");
	        	username.setEditableValidFunc("false");//禁止在此处维护账号密码 haosl 20170613
	            columnTmp.add(username);
	            // 密码
	            ColumnsInfo password = getColumnsInfo("password", "材料审核密码", 145, "A");
	        	password.setEditableValidFunc("false");//禁止在此处维护账号密码  20170613
	            columnTmp.add(password);
			}
            // 状态
            ColumnsInfo state = getColumnsInfo("state", "状态", 100, "A");
            //if(w0321.indexOf("05")!=-1||w0321.indexOf("06")!=-1)//暂停或起草状态的评审会议才能维护参会人员
        	state.setEditableValidFunc("false");
            ArrayList<CommonData> operationData = new ArrayList<CommonData>();
            CommonData cd = new CommonData();
			cd.setDataName("启用");
			cd.setDataValue("1");
			operationData.add(cd);
			cd = new CommonData();
			cd.setDataName("禁用");
			cd.setDataValue("0");
			operationData.add(cd);
			state.setOperationData(operationData);
            columnTmp.add(state);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    	return columnTmp;
    }
    
    /**
     * 列头ColumnsInfo对象初始化
     * @param columnId 编号
     * @param columnDesc 名称
     * @param columnWidth 列宽
     * @param type 数据类型
     * @return
     */
    private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth, String type) {
        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId(columnId);
        columnsInfo.setColumnDesc(columnDesc);
        //columnsInfo.setCodesetId("");// 指标集
        columnsInfo.setColumnWidth(columnWidth);// 显示列宽
        columnsInfo.setColumnType(type);// 类型N|M|A|D
        if ("A".equals(type))
            columnsInfo.setCodesetId("0");
        columnsInfo.setDecimalWidth(0);// 小数位
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
		buttonList.add(newButton("新增评委", "choose_me.openAddStaff", "true", null, null));
		buttonList.add(newButton("撤销评委", "choose_me.deleteStaff", "true", null, null));
		//buttonList.add(newButton("保存", "choose_me.saveStaff", "true", null, null));
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
     * 新增参会人员
     * @param w0301 评审会议编号
     * @param personidList 新增参会人员编号集合
     * @return
     * @throws GeneralException 
     */
    public String addChoosePerson(String w0301, ArrayList<String> personidList,int type) throws GeneralException {
		String msg = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			for(int i=0; i<personidList.size(); i++){
				String w0101 = personidList.get(i);
				w0101 = PubFunc.decrypt(w0101);
				StringBuilder sql = new StringBuilder();
				// 需要新增的专家是否已经存在
				sql.append("select * ");
				sql.append(" from zc_expert_user ");
				sql.append(" where W0301 = '"+ w0301 +"'");
				sql.append(" and W0101 = '"+ w0101 +"' and type="+type+" and "+Sql_switcher.isnull("usetype", "1")+"=1");
				rs = dao.search(sql.toString());
				if(rs.next())
					continue;
				else {
					sql.setLength(0);
					IDGenerator idg = new IDGenerator(2,this.conn);
					String user_id = idg.getId("zc_expert_user.user_id");
					sql.append("insert into zc_expert_user ");
					sql.append(" (user_id,W0301,W0501,state,W0101,type) ");
					sql.append(" values ");
					sql.append(" ('"+ user_id +"','"+ w0301 +"','xxxxxx',0,'"+ w0101 +"',"+type+")");
					dao.update(sql.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeResource(rs);
		}
		return msg;
	}
    
    /**
     * 删除参会人员
     * @param w0101 专家编号字符串数组
     * @return
     * @throws GeneralException 
     */
    public String deleteChoosePerson(String[] w0101, String w0301,int type) throws GeneralException {
		String msg = "";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			for(int i = 0;i < w0101.length;i++){
				StringBuilder sql = new StringBuilder();
				sql.append("delete ");
				sql.append("from zc_expert_user ");
				sql.append("where w0101=? and w0301=? and type=?");
				ArrayList list = new ArrayList();
				list.add(String.valueOf(w0101[i]));
				list.add(w0301);
				list.add(type);
				dao.delete(sql.toString(), list);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return msg;
	}
    
    /**
     * 随机生成查看账号密码（方法1）
     * @param list 账号密码集合
     * @param w0101 专家编号字符串数组
     * @param w0301 会议编号
     * @return
     * @throws GeneralException 
     */
    @SuppressWarnings("unchecked")
	public String randomCreate(ArrayList list, String[] w0101, String w0301,int type) throws GeneralException {
		String msg = "";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			for(int i = 0;i < w0101.length;i++){
				StringBuilder sql = new StringBuilder();
				sql.append("update zc_expert_user ");
				sql.append("set username=?,password=? ");
				sql.append("where w0301=? ");
				sql.append("and type=? ");
				sql.append("and w0101=? ");
				sql.append("and "+ Sql_switcher.isnull("usetype", "1") +"=1 ");
				
				ArrayList updatelist = new ArrayList();
				HashMap map = (HashMap)list.get(i);
				updatelist.add((String)map.get("content"));
				updatelist.add((String)map.get("pasword"));
				updatelist.add(w0301);
				updatelist.add(type);
				updatelist.add(String.valueOf(w0101[i]));
				
				dao.update(sql.toString(), updatelist);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return msg;
	}
    
    /**
     * 随机生成账号密码（方法2）
     * @param list 账号密码集合
     * @param userList 账号编号集合
     * @return
     * @throws GeneralException 
     */
    @SuppressWarnings("unchecked")
	public String randomCreate(ArrayList list, ArrayList userList) throws GeneralException {
		String msg = "";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			ArrayList<RecordVo> volist = new ArrayList<RecordVo>();
			for(int i = 0;i < userList.size();i++){
				RecordVo resultVo = new RecordVo("zc_expert_user");
				resultVo.setString("user_id", String.valueOf(userList.get(i)));
				HashMap map = (HashMap)list.get(i);
				resultVo.setString("username", (String)map.get("content"));
				resultVo.setString("password", (String)map.get("pasword"));
				resultVo.setInt("usetype",1);//查看帐号类型
				volist.add(resultVo);
			}
			dao.updateValueObject(volist);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return msg;
	}
    
    /**
     * 修改参会人员信息
     * @param updatelist 修改参会人员的列集合
     * @return
     * @throws GeneralException 
     */
    @SuppressWarnings("unchecked")
	public String updateChoosePerson(ArrayList updatelist) throws GeneralException {
		String msg = "";
		String temp = "";
		ArrayList updateFieldList=getUpdateFieldList();//要修改的字段
		boolean flag= true;	//是否更新
		//循环新增和修改的数据
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			if(updatelist.size()>0){//修改参会人员信息
				ArrayList<RecordVo> volist = new ArrayList<RecordVo>();
				for(int i=0;i<updatelist.size();i++){
					MorphDynaBean bean=(MorphDynaBean)updatelist.get(i);
					RecordVo resultVo = new RecordVo("zc_expert_user");
					for(int j=0;j<updateFieldList.size();j++){
						String fieldid = String.valueOf(updateFieldList.get(j));//字段名
						if(bean.get(fieldid) instanceof String){							
							String fieldvalue = (String)bean.get(fieldid);
							if("username".equalsIgnoreCase(fieldid)&&isUserExist(fieldvalue)){//haosl 20161014更新帐号时，判断帐号是否存在
								temp+="“"+fieldvalue+"”、";
								flag = false;//更新标记置为否
								break;
							}
							flag = true;
							resultVo.setString(fieldid, fieldvalue);
						}else if(bean.get(fieldid) instanceof Integer){
							int fieldvalue = (Integer)bean.get(fieldid);
							resultVo.setInt(fieldid, fieldvalue);
						}
					}
					if(flag)
						dao.updateValueObject(resultVo);//haosl 20161014 逐条更新，如果批量更新很有可能会有相同帐号被成功保存
					
				}
//				dao.updateValueObject(volist);
				if(temp.length()>0){
					temp = temp.substring(0,temp.length()-1);
					msg = "帐号 "+temp+" 已存在,未保存!";
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return msg;
	}
    
    /**
     * 循环可修改的字段列
     * @return
     */
    @SuppressWarnings("unchecked")
	private ArrayList getUpdateFieldList(){
    	ArrayList updateFieldList = new ArrayList();
    	updateFieldList.add("user_id");
    	updateFieldList.add("username");
    	updateFieldList.add("password");
    	updateFieldList.add("state");
    	return updateFieldList;
    }
    
    private boolean isUserExist(String username)throws GeneralException{
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	boolean isExist = false;
    	try {
    		String sql = "select count(username) as count from zc_expert_user where username=?";
    		List values = new ArrayList();
    		values.add(username);
			rs = dao.search(sql,values);
			if(rs.next()&&rs.getInt("count")>0){
				isExist = true;
			}
			return isExist;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    }
}
