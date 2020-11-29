package com.hjsj.hrms.businessobject.performance.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>Title:RenderRelationBo.java</p>
 * <p>Description:考核关系/汇报关系</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-04-15 11:11:11</p> 
 * @author JinChunhai
 * @version 1.0
 */

public class RenderRelationBo
{

    Connection conn = null;

    private String object_id = "";

    private String upperPost = "";// 直接上级职位

    private String upperUpperPost = "";// 上上级职位

    private String thirdUpperPost = "";// 第三级职位

    private String fourthUpperPost = "";// 第四级职位

    private String fieldItem = "";// 汇报关系指标
    
    private String peering="";//同时级别的指标

    /**
         * @param cn
         * @param object_id
         *                考核对象id
         * @param objPost
         *                考核对象的职位
         * @param fieldItem
         *                汇报关系指标
         */
    public RenderRelationBo(Connection cn, String object_id, String objPost, String fieldItem) throws GeneralException
    {

		this.object_id = object_id;
		this.conn = cn;
		this.fieldItem = fieldItem;
		if(fieldItem.trim().length()==0) {
            throw new GeneralException("请先设置汇报关系！");
        }
		
		// 本级与所有上级岗位中有重复的，应给出提示 lium
		int superiorCount = 1; // 包含几级上级(包含本级)
		Set superiorPosId = new HashSet(); // 所有上级(非重复)的岗位id
		superiorPosId.add(objPost);
		
		if (objPost.length() > 0) {
		    this.upperPost = getUpperPos(objPost);
		    superiorCount++;
		    superiorPosId.add(upperPost);
		}
		if (this.upperPost.length() > 0) {
		    this.upperUpperPost = getUpperPos(this.upperPost);
		    superiorCount++;
		    superiorPosId.add(upperUpperPost);
		}
		if (this.upperUpperPost.length() > 0) {
		    this.thirdUpperPost = getUpperPos(this.upperUpperPost);
		    superiorCount++;
		    superiorPosId.add(thirdUpperPost);
		}
		if (this.thirdUpperPost.length() > 0) {
			this.fourthUpperPost = getUpperPos(this.thirdUpperPost);
			superiorCount++;
			superiorPosId.add(fourthUpperPost);
		}
		
		if (superiorCount != superiorPosId.size()) {
			// 有重复的岗位编号，说明所有上级中有设置错误的岗位
			String currPost = AdminCode.getCodeName("@K", objPost);
			throw new GeneralException(currPost + "岗位汇报关系设置错误");
		}
		
		if(this.upperPost.length()>0){
		    this.peering=getPeeringPos(this.upperPost);
		}
    }

    /**
     * @throws GeneralException  
     * @Title: getPeeringPos 
     * @Description: 获得同事级别的岗位id
     * @param upperPost2
     * @return String   
     * @throws 
    */
    private String getPeeringPos(String upperPost) throws GeneralException {
        String searchSql ="select * from K01 where "+fieldItem+"='" +upperPost+"'";
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rowSet = null;
        try{
            rowSet=dao.search(searchSql);
            while(rowSet.next()){
                if(rowSet.getString("E01A1") != null && rowSet.getString("E01A1").length() > 1){
                    peering=peering+"'"+rowSet.getString("E01A1")+"'"+",";
                }
            }
            
        }catch(Exception e){
            e.printStackTrace();
            throw new GeneralException(e.getMessage());
        }
        if(peering.length()>1){
            peering=peering.substring(0, peering.length()-1);
        }
        return peering;
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

    public void saveMainBody() throws GeneralException
    {
		HashMap map = this.getMainBodyType();	
		StringBuffer sql = new StringBuffer();
		StringBuffer buf = new StringBuffer();
		if (this.upperPost.length() > 0)
		{
		    String body_id =map.get("1")==null?"":(String)map.get("1");
		    if(body_id.trim().length()>0)
		    {
			    buf.append("select a0100,'");
			    buf.append(this.object_id);
			    buf.append("' mainbody_id,b0110,e0122,e01a1,a0101,'"+body_id+"' body_id from usra01 ");
			    buf.append("where a0100 not in (select mainbody_id from per_mainbody_std where object_id='");
			    buf.append(this.object_id);
			    buf.append("') and e01a1='");
			    buf.append(this.upperPost);
			    buf.append("'");
		    }
		}
		if (this.upperUpperPost.length() > 0 && !this.upperUpperPost.equalsIgnoreCase(this.upperPost))
		{
		    String body_id =map.get("0")==null?"":(String)map.get("0");
		    if(body_id.trim().length()>0)
		    {
			    if (buf.length() > 0) {
                    buf.append(" union all ");
                }
			    buf.append("select a0100,'");
			    buf.append(this.object_id);
			    buf.append("' mainbody_id,b0110,e0122,e01a1,a0101,'"+body_id+"' body_id from usra01 ");
			    buf.append("where a0100 not in (select mainbody_id from per_mainbody_std where object_id='");
			    buf.append(this.object_id);
			    buf.append("') and e01a1='");
			    buf.append(this.upperUpperPost);
			    buf.append("'");
		    }
		}
		if (this.thirdUpperPost.length() > 0 && !this.thirdUpperPost.equalsIgnoreCase(this.upperUpperPost))
		{
		    String body_id =map.get("-1")==null?"":(String)map.get("-1");
		    if(body_id.trim().length()>0)
		    {
			    if (buf.length() > 0) {
                    buf.append(" union all ");
                }
			    buf.append("select a0100,'");
			    buf.append(this.object_id);
			    buf.append("' mainbody_id,b0110,e0122,e01a1,a0101,'"+body_id+"' body_id from usra01 ");
			    buf.append("where a0100 not in (select mainbody_id from per_mainbody_std where object_id='");
			    buf.append(this.object_id);
			    buf.append("') and e01a1='");
			    buf.append(this.thirdUpperPost);
			    buf.append("'");
		    }
		}
		if (this.fourthUpperPost.length() > 0 && !this.fourthUpperPost.equalsIgnoreCase(this.thirdUpperPost))
		{
		    String body_id =map.get("-2")==null?"":(String)map.get("-2");
		    if(body_id.trim().length()>0)
		    {
			    if (buf.length() > 0) {
                    buf.append(" union all ");
                }
			    buf.append("select a0100,'");
			    buf.append(this.object_id);
			    buf.append("' mainbody_id,b0110,e0122,e01a1,a0101,'"+body_id+"' body_id from usra01 ");
			    buf.append("where a0100 not in (select mainbody_id from per_mainbody_std where object_id='");
			    buf.append(this.object_id);
			    buf.append("') and e01a1='");
			    buf.append(this.fourthUpperPost);
			    buf.append("'");
		    }
		}
		if(this.peering.length()>0){
		    String body_id =map.get("2")==null?"":(String)map.get("2");//得到同级
		    if(body_id.trim().length()>0)
            {
                if (buf.length() > 0) {
                    buf.append(" union all ");
                }
                buf.append("select a0100,'");
                buf.append(this.object_id);
                buf.append("' mainbody_id,b0110,e0122,e01a1,a0101,'"+body_id+"' body_id from usra01 ");
                buf.append("where a0100 not in (select mainbody_id from per_mainbody_std where object_id='");
                buf.append(this.object_id );
                buf.append("') and a0100 <> '"+this.object_id+"'");
                buf.append(" and e01a1 in (");
                buf.append(peering+")");
                if (buf.length() > 0) {
                    buf.append(" union all ");
                }
                buf.append("select '");
                buf.append(this.object_id);
                buf.append("',a0100,aa.b0110,aa.e0122,aa.e01a1,aa.a0101");
                buf.append(",'"+body_id+"' body_id from UsrA01,(select b0110,e0122,e01a1,a0101 from UsrA01 where a0100='"+this.object_id+"') aa");
                buf.append(" where A0100<>'"+this.object_id+"'"); 
                buf.append("and a0100 not in (select object_id from per_mainbody_std where mainbody_id='");
                buf.append(this.object_id);
                buf.append("') AND UsrA01.E01A1 IN (select e01a1 from K01 where ");
                buf.append(fieldItem+"='"+this.upperPost+"')");
            }
		}
		if (this.upperPost.length() + this.upperUpperPost.length() + this.thirdUpperPost.length() + this.fourthUpperPost.length()+this.peering.length() > 0 && buf.length() > 0)
		{
		    sql.append("insert into per_mainbody_std(mainbody_id,object_id,b0110,e0122,e01a1,a0101,body_id)");
		    sql.append(buf.toString());
		    ContentDAO dao = new ContentDAO(this.conn);
		    try
		    {
		    	dao.insert(sql.toString(), new ArrayList());
		    } catch (Exception e)
		    {
				e.printStackTrace();
				throw new GeneralException(e.getMessage());
		    }
		}

    }
}
