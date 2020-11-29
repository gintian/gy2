package com.hjsj.hrms.businessobject.general.relation;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:RenderRelationBo.java
 * </p>
 * <p>
 * Description:考核关系/汇报关系
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-04-15 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 */
public class GenRenderRelationBo
{

    Connection conn = null;

    private String object_id = "";

    private String upperPost = "";// 直接上级职位

    private String upperUpperPost = "";// 上上级职位

    private String thirdUpperPost = "";// 第三级职位

    private String fourthUpperPost = "";// 第四级职位

    private String fieldItem = "";// 汇报关系指标
    private String dbpre="usr";//人员库标示
    private String relationid="";//审批关系id
    private UserView userView= null;
    private String actor_type="";

    /**
         * @param cn
         * @param object_id
         *                考核对象id
         * @param objPost
         *                考核对象的职位
         * @param fieldItem
         *                汇报关系指标
         */
    public GenRenderRelationBo(Connection cn, String object_id, String objPost, String fieldItem,String dbpre,String relationid,UserView userView,String actor_type) throws GeneralException
    {

	this.object_id = object_id;
	this.conn = cn;
	this.fieldItem = fieldItem;
	this.dbpre = dbpre;
	this.relationid = relationid;
	this.userView = userView;
	this.actor_type = actor_type;
	if(fieldItem.trim().length()==0) {
        throw new GeneralException("请先设置汇报关系！");
    }
	if (objPost.length() > 0) {
        this.upperPost = getUpperPos(objPost);
    }
	if (this.upperPost.length() > 0) {
        this.upperUpperPost = getUpperPos(this.upperPost);
    }
	if (this.upperUpperPost.length() > 0) {
        this.thirdUpperPost = getUpperPos(this.upperUpperPost);
    }
	if (this.thirdUpperPost.length() > 0) {
        this.fourthUpperPost = getUpperPos(this.thirdUpperPost);
    }
    }

    /**
         * 根据当前职务找到直接上级职务
         * 
         * @param posID
         * @return
     * @throws GeneralException 
         */
    public String getUpperPos(String posID) throws GeneralException
    {

	String upperPosID = "";
	ContentDAO dao = new ContentDAO(this.conn);
	RowSet rowSet = null;
	try
	{
	    rowSet = dao.search("select * from K01 where E01A1='" + posID + "'");
	    if (rowSet.next())
	    {
		if (rowSet.getString(fieldItem) != null && rowSet.getString(fieldItem).length() > 1)
		{
		    upperPosID = rowSet.getString(fieldItem);
		}
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw new GeneralException(e.getMessage());
	}
	return upperPosID;
    }

    /** 由主体分类等级取得主体分类id 
     * @throws GeneralException */
    public HashMap getMainBodyType() throws GeneralException
    {

	HashMap map = new HashMap();
	ContentDAO dao = new ContentDAO(this.conn);
	StringBuffer buf = new StringBuffer();
	if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
        buf.append("select level_o,body_id from per_mainbodyset where level_o is not null");
    } else {
        buf.append("select level,body_id from per_mainbodyset where level is not null");
    }
	buf.append(" and status=1  order by body_id");
	RowSet rowSet = null;
	try
	{
	    rowSet = dao.search(buf.toString());
	    while (rowSet.next()) {
            map.put(rowSet.getString(1), rowSet.getString(2));
        }

	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw new GeneralException(e.getMessage());
	}
	return map;
    }

    public void saveMainBody(String sp_grade) throws GeneralException
    {
	StringBuffer sql = new StringBuffer();
	StringBuffer buf = new StringBuffer();
	String operate = "";// 取sql得到连接符
//	ArrayList list = new ArrayList();
//	StringBuffer updatesql = new StringBuffer();
	switch (Sql_switcher.searchDbServer()) {
	case Constant.MSSQL: {
		operate = "+";
		break;
	}
	case Constant.DB2: {
		operate = "+";
		break;
	}
	case Constant.ORACEL: {
		operate = "||";
		break;
	}
	}
	if (this.upperPost.length() > 0)
	{
		String sp_grade2 =sp_grade;//默认直接领导
		if(sp_grade==null||sp_grade.trim().length()==0) {
            sp_grade2="9";
        }
	    if("9".equals(sp_grade2))
	    {
	    buf.append("select '"+dbpre+"'"+operate+"a0100 a0100,'");
	    buf.append(this.object_id+"' mainbody_id,'");
	    buf.append(this.relationid);
	    buf.append("' relation_id,b0110,e0122,e01a1,a0101,'"+sp_grade2+"' sp_grade, ");
	    if(actor_type!=null&& "1".equals(actor_type)) {
            buf.append("'"+this.actor_type+"' actor_type,"+Sql_switcher.dateValue(DateStyle.getSystemTime())+" create_time, ");
        } else {
            buf.append("'"+this.actor_type+"' actor_type,"+this.userView.getGroupId()+" groupid,"+Sql_switcher.dateValue(DateStyle.getSystemTime())+" create_time, ");
        }
	    buf.append("'"+this.userView.getUserName()+"' create_user,"+Sql_switcher.dateValue(DateStyle.getSystemTime())+" mod_time,'"+this.userView.getUserName()+"' mod_user ");
	    buf.append(" from "+dbpre+"a01 ");
	    buf.append("where '"+dbpre+"'"+operate+"a0100 not in (select mainbody_id from t_wf_mainbody where object_id='");
	    buf.append(this.object_id);
	    buf.append("' and relation_id='"+this.relationid+"')  and '"+dbpre+"'"+operate+"a0100<>'"+this.object_id+"' and e01a1='");
	    buf.append(this.upperPost);
	    buf.append("'");
//	    updatesql.setLength(0);
//	    updatesql.append(" update t_wf_mainbody set mod_time ="+Sql_switcher.dateValue(DateStyle.getSystemTime())+",mod_user='"+this.userView.getUserName()+"'");
//	    updatesql.append(" where object_id='"+this.object_id+"' and relation_id='"+this.relationid+"' and e01a1='"+this.upperPost+"' ");
//	    list.add(updatesql.toString());
	    }
	}
	if (this.upperUpperPost.length() > 0 && !this.upperUpperPost.equalsIgnoreCase(this.upperPost))
	{
		String sp_grade2 =sp_grade;
		if(sp_grade==null||sp_grade.trim().length()==0) {
            sp_grade2="10";
        }
	    if("10".equals(sp_grade2))
	    {
	    if (buf.length() > 0) {
            buf.append(" union all ");
        }
	    buf.append("select '"+dbpre+"'"+operate+"a0100 a0100,'");
	    buf.append(this.object_id+"' mainbody_id,'");
	    buf.append(this.relationid);
	    buf.append("' relation_id,b0110,e0122,e01a1,a0101,'"+sp_grade2+"' sp_grade, ");
	    if(actor_type!=null&& "1".equals(actor_type)) {
            buf.append("'"+this.actor_type+"' actor_type,"+Sql_switcher.dateValue(DateStyle.getSystemTime())+" create_time, ");
        } else {
            buf.append("'"+this.actor_type+"' actor_type,"+this.userView.getGroupId()+" groupid,"+Sql_switcher.dateValue(DateStyle.getSystemTime())+" create_time, ");
        }
	    buf.append("'"+this.userView.getUserName()+"' create_user,"+Sql_switcher.dateValue(DateStyle.getSystemTime())+" mod_time,'"+this.userView.getUserName()+"' mod_user ");
	    buf.append(" from "+dbpre+"a01 ");
	    buf.append("where '"+dbpre+"'"+operate+"a0100 not in (select mainbody_id from t_wf_mainbody where object_id='");
	    buf.append(this.object_id);
	    buf.append("' and relation_id='"+this.relationid+"') and '"+dbpre+"'"+operate+"a0100<>'"+this.object_id+"' and e01a1='");
	    buf.append(this.upperUpperPost);
	    buf.append("'");
//	    updatesql.setLength(0);
//	    updatesql.append(" update t_wf_mainbody set mod_time ="+Sql_switcher.dateValue(DateStyle.getSystemTime())+",mod_user='"+this.userView.getUserName()+"'");
//	    updatesql.append(" where object_id='"+this.object_id+"' and relation_id='"+this.relationid+"' and e01a1='"+this.upperUpperPost+"' ");
//	    list.add(updatesql.toString());
	    }
	}
	if (this.thirdUpperPost.length() > 0 && !this.thirdUpperPost.equalsIgnoreCase(this.upperUpperPost))
	{
		String sp_grade2 =sp_grade;
		if(sp_grade==null||sp_grade.trim().length()==0) {
            sp_grade2="11";
        }
	    if("11".equals(sp_grade2))
	    {
	    if (buf.length() > 0) {
            buf.append(" union all ");
        }
	    buf.append("select '"+dbpre+"'"+operate+"a0100 a0100,'");
	    buf.append(this.object_id+"' mainbody_id,'");
	    buf.append(this.relationid);
	    buf.append("' relation_id,b0110,e0122,e01a1,a0101,'"+sp_grade2+"' sp_grade, ");
	    if(actor_type!=null&& "1".equals(actor_type)) {
            buf.append("'"+this.actor_type+"' actor_type,"+Sql_switcher.dateValue(DateStyle.getSystemTime())+" create_time, ");
        } else {
            buf.append("'"+this.actor_type+"' actor_type,"+this.userView.getGroupId()+" groupid,"+Sql_switcher.dateValue(DateStyle.getSystemTime())+" create_time, ");
        }
	    buf.append("'"+this.userView.getUserName()+"' create_user,"+Sql_switcher.dateValue(DateStyle.getSystemTime())+" mod_time,'"+this.userView.getUserName()+"' mod_user ");
	    buf.append(" from "+dbpre+"a01 ");
	    buf.append("where '"+dbpre+"'"+operate+"a0100 not in (select mainbody_id from t_wf_mainbody where object_id='");
	    buf.append(this.object_id);
	    buf.append("' and relation_id='"+this.relationid+"')  and '"+dbpre+"'"+operate+"a0100<>'"+this.object_id+"' and e01a1='");
	    buf.append(this.thirdUpperPost);
	    buf.append("'");
//	    updatesql.setLength(0);
//	    updatesql.append(" update t_wf_mainbody set mod_time ="+Sql_switcher.dateValue(DateStyle.getSystemTime())+",mod_user='"+this.userView.getUserName()+"'");
//	    updatesql.append(" where object_id='"+this.object_id+"' and relation_id='"+this.relationid+"' and e01a1='"+this.thirdUpperPost+"' ");
//	    list.add(updatesql.toString());
	    }
	}
	if (this.fourthUpperPost.length() > 0 && !this.fourthUpperPost.equalsIgnoreCase(this.thirdUpperPost))
	{
		String sp_grade2 =sp_grade;
		if(sp_grade==null||sp_grade.trim().length()==0) {
            sp_grade2="12";
        }
	    if("12".equals(sp_grade2))
	    {
	    if (buf.length() > 0) {
            buf.append(" union all ");
        }
	    buf.append("select '"+dbpre+"'"+operate+"a0100 a0100,'");
	    buf.append(this.object_id+"' mainbody_id,'");
	    buf.append(this.relationid);
	    buf.append("' relation_id,b0110,e0122,e01a1,a0101,'"+sp_grade2+"' sp_grade, ");
	    if(actor_type!=null&& "1".equals(actor_type)) {
            buf.append("'"+this.actor_type+"' actor_type,"+Sql_switcher.dateValue(DateStyle.getSystemTime())+" create_time, ");
        } else {
            buf.append("'"+this.actor_type+"' actor_type,"+this.userView.getGroupId()+" groupid,"+Sql_switcher.dateValue(DateStyle.getSystemTime())+" create_time, ");
        }
	    buf.append("'"+this.userView.getUserName()+"' create_user,"+Sql_switcher.dateValue(DateStyle.getSystemTime())+" mod_time,'"+this.userView.getUserName()+"' mod_user ");
	    buf.append(" from "+dbpre+"a01 ");
	    buf.append("where '"+dbpre+"'"+operate+"a0100 not in (select mainbody_id from t_wf_mainbody where object_id='");
	    buf.append(this.object_id);
	    buf.append("' and relation_id='"+this.relationid+"') and '"+dbpre+"'"+operate+"a0100<>'"+this.object_id+"' and e01a1='");
	    buf.append(this.fourthUpperPost);
	    buf.append("'");
//	    updatesql.setLength(0);
//	    updatesql.append(" update t_wf_mainbody set mod_time ="+Sql_switcher.dateValue(DateStyle.getSystemTime())+",mod_user='"+this.userView.getUserName()+"'");
//	    updatesql.append(" where object_id='"+this.object_id+"' and relation_id='"+this.relationid+"' and e01a1='"+this.fourthUpperPost+"' ");
//	    list.add(updatesql.toString());
	    }
	}
	if (this.upperPost.length() + this.upperUpperPost.length() + this.thirdUpperPost.length() + this.fourthUpperPost.length() > 0 && buf.length() > 0)
	{
		if(actor_type!=null&& "1".equals(actor_type)) {
            sql.append("insert into t_wf_mainbody(mainbody_id,object_id,relation_id,b0110,e0122,e01a1,a0101,sp_grade,actor_type,create_time,create_user,mod_time,mod_user)");
        } else {
            sql.append("insert into t_wf_mainbody(mainbody_id,object_id,relation_id,b0110,e0122,e01a1,a0101,sp_grade,actor_type,groupid,create_time,create_user,mod_time,mod_user)");
        }
	    sql.append(buf.toString());
	    ContentDAO dao = new ContentDAO(this.conn);
	    try
	    {
		dao.insert(sql.toString(), new ArrayList());
		//更新相应的字段
//		if(list.size()>0){
//			for(int i=0;i<list.size();i++){
//				if(list.get(i).toString().length()>5)
//					dao.update(list.get(i).toString());
//			}
//		}
	    } catch (Exception e)
	    {
		e.printStackTrace();
		throw new GeneralException(e.getMessage());
	    }
	}

    }
}
