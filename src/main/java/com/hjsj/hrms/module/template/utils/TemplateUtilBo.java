package com.hjsj.hrms.module.template.utils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.sql.RowSet;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.mortbay.util.ajax.JSON;

import com.hjsj.hrms.businessobject.general.template.TSubSetDomain;
import com.hjsj.hrms.module.template.utils.javabean.TemplateItem;
import com.hjsj.hrms.module.template.utils.javabean.TemplateOptionField;
import com.hjsj.hrms.module.template.utils.javabean.TemplatePage;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
/**
 * <p>Title:TemplateUtilBo.java</p>
 * <p>Description>:模板方法类，一般是公用且与模板业务相关的方法都写在这儿，
 * 方法一般都是按参数传递方式，不建议使用全局变量</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-08-23 上午10:36:32</p>
 * <p>@version: 7.0</p>
 */
public class TemplateUtilBo {
    private Connection conn=null;
    private UserView userView;
    private ContentDAO dao; 
	public TemplateUtilBo(Connection conn,UserView userview) {
		this.conn = conn;
		this.userView = userview;
		dao = new ContentDAO(this.conn);
	}
	
	/**
	 * 获得节点号
	 * @param task_id 多个taskid以逗号分隔
	 * @return
	 */
	public String getNodeIdByTask_ids(String task_ids,String tabid)
	{
		String node_id="-1";
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			if (task_ids.indexOf(",")>-1){//批量审批模式
			    String[] lists=StringUtils.split(task_ids,",");
			    String taskids="";
                for(int i=0;i<lists.length;i++)
                {
                    if(lists[i]==null||lists[i].trim().length()==0)
                         continue;
                    if (taskids.length()>0) 
                        taskids=taskids+",";
                    taskids=taskids+lists[i];
                }
                
                String sql="select * from t_wf_node where node_id in "
                    +"(select node_id from t_wf_task where task_id in ("+taskids+") )";
               
                            
                RowSet rowSet=dao.search(sql);
			    while (rowSet.next())
			    {
			    	node_id=rowSet.getString("node_id");
			    }
			}
			else {
				if(task_ids!=null)
				{
			        String sql="select * from t_wf_node where node_id=(select node_id from t_wf_task where task_id="+task_ids+" )";
			        if("0".equalsIgnoreCase(task_ids)){
	                	sql="select * from t_wf_node where nodetype=1 and tabid="+tabid;
	                }
	                RowSet rowSet=dao.search(sql);
	                if(rowSet.next())
	                {
	                	node_id=rowSet.getString("node_id");
	                }  
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return node_id;
	}
	/**
	 * 获得节点定义的指标权限
	 * @param task_id 多个taskid以逗号分隔
	 * @return
	 */
	public HashMap getFieldPrivByNode(String task_ids,int tabid)
	{
		HashMap _map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			if (task_ids.indexOf(",")>-1){//批量审批模式
			    String[] lists=StringUtils.split(task_ids,",");
			    String taskids="";
                for(int i=0;i<lists.length;i++)
                {
                    if(lists[i]==null||lists[i].trim().length()==0)
                         continue;
                    if (taskids.length()>0) 
                        taskids=taskids+",";
                    taskids=taskids+lists[i];
                }
                
                String sql="select * from t_wf_node where node_id in "
                    +"(select node_id from t_wf_task where task_id in ("+taskids+") )";
                RowSet rowSet=dao.search(sql);
			    while (rowSet.next())
			    {
			        String ext_param= Sql_switcher.readMemo(rowSet,"ext_param"); 
			        if (_map.size()<1){
			            _map= getFieldPrivFromXml(ext_param); 
			        }
			        else {
			            HashMap tmpMap = getFieldPrivFromXml(ext_param);
			            Iterator iter = tmpMap.entrySet().iterator(); 
			            while (iter.hasNext()) { //与上一节点的指标权限比较，取权限大的。
			                Entry entry = (Entry) iter.next(); 
			                String columnname = (String)entry.getKey(); 
			                String editable = (String)entry.getValue(); 
			                if (editable==null ||"".equals(editable)){
			                    editable="0";
                            }
			                if (_map.containsKey(columnname)) {
			                    String oldEditable=(String)_map.get(columnname);
			                    if (oldEditable==null ||"".equals(oldEditable)){
			                        oldEditable="0";
			                    }
			                    if (editable!=null){
			                        if (Integer.parseInt(editable)>Integer.parseInt(oldEditable)){//比原来的权限大
			                            _map.put(columnname, editable);  
			                        }
			                    }
			                }
			                else {
			                    _map.put(columnname, editable);  
			                }
			            } 
			        }
			    }
			}
			else {
				if(task_ids!=null)
				{
			        String sql="select * from t_wf_node where node_id=(select node_id from t_wf_task where task_id="+task_ids+" )";
			        if("0".equalsIgnoreCase(task_ids)){
	                	 sql="select * from t_wf_node where nodetype=1 and tabid="+tabid;
	                }
	                RowSet rowSet=dao.search(sql);
	                if(rowSet.next())
	                {
	                    String ext_param= Sql_switcher.readMemo(rowSet,"ext_param"); 
	                    _map = getFieldPrivFromXml(ext_param);
	                }  
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return _map;
	}
	/**
	 * 获得节点定义的指标权限
	 * @param task_id 多个taskid以逗号分隔
	 * @return
	 */
	public HashMap getFieldPrivByNode(String task_ids)
	{
		HashMap _map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			if (task_ids.indexOf(",")>-1){//批量审批模式
			    String[] lists=StringUtils.split(task_ids,",");
			    String taskids="";
                for(int i=0;i<lists.length;i++)
                {
                    if(lists[i]==null||lists[i].trim().length()==0)
                         continue;
                    if (taskids.length()>0) 
                        taskids=taskids+",";
                    taskids=taskids+lists[i];
                }
                
                String sql="select * from t_wf_node where node_id in "
                    +"(select node_id from t_wf_task where task_id in ("+taskids+") )";
                RowSet rowSet=dao.search(sql);
			    while (rowSet.next())
			    {
			        String ext_param= Sql_switcher.readMemo(rowSet,"ext_param"); 
			        if (_map.size()<1){
			            _map= getFieldPrivFromXml(ext_param); 
			        }
			        else {
			            HashMap tmpMap = getFieldPrivFromXml(ext_param);
			            Iterator iter = tmpMap.entrySet().iterator(); 
			            while (iter.hasNext()) { //与上一节点的指标权限比较，取权限大的。
			                Entry entry = (Entry) iter.next(); 
			                String columnname = (String)entry.getKey(); 
			                String editable = (String)entry.getValue(); 
			                if (editable==null ||"".equals(editable)){
			                    editable="0";
                            }
			                if (_map.containsKey(columnname)) {
			                    String oldEditable=(String)_map.get(columnname);
			                    if (oldEditable==null ||"".equals(oldEditable)){
			                        oldEditable="0";
			                    }
			                    if (editable!=null){
			                        if (Integer.parseInt(editable)>Integer.parseInt(oldEditable)){//比原来的权限大
			                            _map.put(columnname, editable);  
			                        }
			                    }
			                }
			                else {
			                    _map.put(columnname, editable);  
			                }
			            } 
			        }
			    }
			}
			else {
				if(task_ids!=null&&!"0".equals(task_ids.trim()))
				{
			        String sql="select * from t_wf_node where node_id=(select node_id from t_wf_task where task_id="+task_ids+" )";
	                RowSet rowSet=dao.search(sql);
	                if(rowSet.next())
	                {
	                    String ext_param= Sql_switcher.readMemo(rowSet,"ext_param"); 
	                    _map = getFieldPrivFromXml(ext_param);
	                }  
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return _map;
	}

    /**
     * 获得节点定义的指标权限
     * @param task_id
     * @return
     */
    private HashMap getFieldPrivFromXml(String ext_param)
    {
        HashMap _map=new HashMap();
        Document doc=null;
        Element element=null;
        try
        {	
            if(ext_param!=null&&ext_param.trim().length()>0)
            {
                doc=PubFunc.generateDom(ext_param);
                String xpath="/params/field_priv/field";
                XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                List childlist=findPath.selectNodes(doc);   
                if(childlist.size()==0){
                    xpath="/params/field_priv/field";
                     findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                     childlist=findPath.selectNodes(doc);
                }
                if(childlist!=null&&childlist.size()>0)
                {
                    for(int i=0;i<childlist.size();i++)
                    {
                        element=(Element)childlist.get(i);
                        String editable="";
                        //0|1|2(无|读|写)
                        if(element!=null&&element.getAttributeValue("editable")!=null)
                            editable=element.getAttributeValue("editable");
                        if(editable!=null&&editable.trim().length()>0)
                        {
                            String columnname=element.getAttributeValue("name").toLowerCase();
                            _map.put(columnname, editable);
                        }
                        
                    }
                }
            } 
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return _map;
    }
    
    
	/**
	 * 取得模板的所有临时变量
	 * @return
	 */
	public HashMap getAllVariableHm( int tabId)
	{
		HashMap hm=new HashMap();
		/*
		StringBuffer strsql=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.conn);
		
		try
		{
			strsql.append("select * from midvariable where nflag=0 and templetId <> 0 and (templetId = "
					+tabId+" or cstate = '1')"); //包含共享临时变量 2014-02-22
			strsql.append(" order by sorting");			
			RowSet rset=dao.search(strsql.toString());
			while(rset.next())
			{
				RecordVo vo=new RecordVo("midvariable");
				vo.setString("cname",rset.getString("cname"));
				vo.setString("chz",rset.getString("chz"));
				vo.setInt("ntype",rset.getInt("ntype"));
				vo.setString("cvalue",rset.getString("cValue"));
				String codesetid=rset.getString("codesetid");
				if(codesetid==null||codesetid.equalsIgnoreCase(""))
					codesetid="0";
				vo.setString("codesetid",codesetid);
				vo.setInt("fldlen",rset.getInt("fldlen"));
				vo.setInt("flddec",rset.getInt("flddec"));
				hm.put(rset.getString("cname"),vo);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		*/
		if(TemplateStaticDataBo.getAllVariableHm(tabId, conn,false)!=null)
			hm=(HashMap)TemplateStaticDataBo.getAllVariableHm(tabId, conn,false);
		
		return hm;
	}
	

	/** 
	* @Title: getAllCell 
	* @Description: 返回当前模板所有celllist 
	* @param @param tabId
	* @param @return
	* @return ArrayList
	*/ 
	public ArrayList getAllCell(int tabId) {
		   return getPageCell(tabId,-1);
		   
	   }

	
	/**
	 * 如果为null返回“”字符串
	 * @param value
	 * @return
	 */
	public String nullToSpace(String value)
	{
		if(value==null)
			return "";
		else 
			return value;
	}
	
	
	
	/**
	 * 获得TemplateSet对象
	 * @param rset
	 * @param var_hm  模版相关的变量集
	 * @return
	 */
	private TemplateSet getTemplateSet(RowSet rset,HashMap var_hm)
	{
		TemplateSet setBo = new TemplateSet(); 
		try
		{ 
			setBo.setTabId(rset.getInt("tabid"));
			setBo.setPageId(rset.getInt("pageid"));
			setBo.setHz(nullToSpace(rset.getString("hz")));// 设置表格的汉字描述
			setBo.setSetname(nullToSpace(rset.getString("setname")));// 设置子集的代码
			setBo.setCodeid(nullToSpace(rset.getString("codeid")));// 相关的代码类
			setBo.setField_hz(nullToSpace(rset.getString("Field_hz")));// 字段的汉字描述 取自业务字典
			setBo.setField_name(nullToSpace(rset.getString("Field_name")));// 指标的代码
			String flag = rset.getString("Flag") == null ? "" : rset.getString("Flag");// 数据源的标识（文本描述、照片......）
			setBo.setFlag(rset.getString("Flag"));// 设置数据源的标识
			String temp = rset.getString("subflag");// 子表控制符 0：字段 1：子集
			if (temp == null || "".equals(temp) || "0".equals(temp))
				setBo.setSubflag(false);
			else{
			    setBo.setSubflag(true);
			}
			setBo.setField_type(nullToSpace(rset.getString("Field_type")));
			setBo.setOld_fieldType(nullToSpace(rset.getString("Field_type")));
			setBo.setFormula(nullToSpace(Sql_switcher.readMemo(rset, "Formula")));// 设置字段的计算公式
			setBo.setAlign(rset.getInt("Align"));// 文字在单元格中的排列方式
			setBo.setDisformat(rset.getInt("DisFormat"));// 设置数据的格式

			if ("V".equalsIgnoreCase(flag)) {// 变量
				RecordVo vo = (RecordVo) var_hm.get(rset
						.getString("Field_name"));
				if (vo != null) {
					setBo.setDisformat(vo.getInt("flddec"));// 如果是临时变量
															// 么要根据临时变量表里面的小数位数来设置
					setBo.setVarVo(vo);
				}
			}
			setBo.setChgstate(rset.getInt("ChgState"));// 设置字段是变化前还是变化后
			setBo.setFonteffect(rset.getInt("Fonteffect"));// 设置字体效果
			setBo.setFontname(rset.getString("FontName"));// 设置字体名称
			setBo.setFontsize(rset.getInt("Fontsize"));// 设置字体大小
			setBo.setHismode(rset.getInt("HisMode"));// 设置历史定位方式
			if (Sql_switcher.searchDbServer() == 2)
				setBo.setMode(rset.getInt("Mode_o"));
			else
				setBo.setMode(rset.getInt("Mode"));// 多条记录的时候 那几种选择
			// (最近..最初..)
			setBo.setNsort(rset.getInt("nSort"));// 相同指示顺序号
			setBo.setGridno(rset.getInt("gridno"));// 单元格号
			setBo.setRcount(rset.getInt("Rcount"));// 记录数 和HisMode
			// 配合试用（标识最近（Rcount条））
			setBo.setRheight(rset.getInt("RHeight"));// 设置单元格高度
			setBo.setRleft(rset.getInt("RLeft"));// 单元格左边的坐标值
			setBo.setRwidth(rset.getInt("RWidth"));// 单元格的宽度
			setBo.setRtop(rset.getInt("RTop"));// 单元格上边坐标值
			setBo.setL(rset.getInt("L"));
			/** LBRT 代表着表格左下右上是否有线 **/
			setBo.setB(rset.getInt("B"));
			setBo.setR(rset.getInt("R"));
			setBo.setT(rset.getInt("T"));

			if (rset.getInt("yneed") == 0)
				setBo.setYneed(false);
			else
				setBo.setYneed(true);
			String sub_domain = Sql_switcher.readMemo(rset, "sub_domain");
			setBo.setXml_param(sub_domain);
	
			if (rset.getString("nhide") != null)
				setBo.setNhide(rset.getInt("nhide"));
			else
				setBo.setNhide(0);// 打印还是隐藏 0：打印 1：隐藏
			
			// 普通指标需检查是否构库 未构库的过滤掉
			if ( setBo.isABKItem() && !setBo.isSpecialItem()){
				if (!setBo.isSubflag()) {
				    FieldItem item = DataDictionary.getFieldItem(setBo.getField_name());
                    if (item == null) {// 数据字典里为空
                        //return null; 
                    }
                    else {
                    	//屏蔽 给出提示
                       // setBo.setCodeid(nullToSpace(item.getCodesetid()));//重新赋一遍，有时候不对template_set表中的codeid在指标发生变化后，没有更新。陈总事业演示库 wangrd 20160617
                    }
				}
				else {
				    FieldSet fieldset=DataDictionary.getFieldSetVo(setBo.getSetname());
                    if(fieldset==null)
                    {
                        return null; 
                    }
                    else {
                    	setBo.setField_hz(fieldset.getFieldsetdesc());
                    }
				}
			}else if(setBo.isSpecialItem()){//特殊字段 lis 20160706
				if("parentid".equals(setBo.getField_name()))//上级组织`单元名称`
					setBo.setCodeid(rset.getString("codeid"));
				else
					setBo.setCodeid("0");
			}
			
			if (setBo.isNeedChangeFieldType()) {
				setBo.setField_type("M");
			}
			if (setBo.isSubflag()){
			    setBo.setField_type("M");
			}
			if ("V".equalsIgnoreCase(flag)) {// 临时变量
				if(setBo.getVarVo()!=null){//如果模板中设置的临时变量在临时变量表中不存在  则不予计算了
					String codeid= setBo.getVarVo().getString("codesetid");
					setBo.setCodeid(codeid); 
				}
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return setBo;
	}
	
	
	/**
	 * 获得某个单元格的TemplateSet对象
	 * @param tabId  模版ID
	 * @param pageNum  页号
	 * @param gridno   单元格号
	 * @return
	 */
	public TemplateSet getCell(int tabId,int pageNum,int gridno)
	{
		TemplateSet  subsetBo=null;
		RowSet frowset=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			frowset=dao.search("select * from template_set where tabid=? and pageid=? and gridno=? ",Arrays.asList(new Object[] {Integer.valueOf(tabId),Integer.valueOf(pageNum),Integer.valueOf(gridno)}));
			if(frowset.next())
			{
				subsetBo= getTemplateSet(frowset,new HashMap());
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(frowset);
		}
		return subsetBo;
	}
	
	
    /** 
    * @Title: getPageCell 
    * @Description: 返回指定页的所有单元格celllist
    * @param @param tabId
    * @param @param pagenum
    * @param @return
    * @return ArrayList
    */ 
    public ArrayList getPageCell(int tabId,int pageNum) {
    	
    	ArrayList new_setbo=TemplateStaticDataBo.getPageCell(tabId,pageNum, conn); //20171112 邓灿，采用缓存解决并发下压力过大问题
    	/*
    	 * 
		HashMap var_hm = getAllVariableHm(tabId);
        ArrayList new_setbo=new ArrayList();
		String temp = "";
		try {
			RowSet rset = null;
			String sql = "select * from Template_Set where Tabid=? ";
			if (pageNum >-1) {
				sql = sql + "and Pageid=?";
			}
			sql = sql +" order by rtop,rleft";
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList sqlList = new ArrayList();
			sqlList.add(Integer.valueOf(tabId));
			if (pageNum > -1) {
				sqlList.add(Integer.valueOf(pageNum));
			}
			ArrayList setBoList = new ArrayList();
			rset = dao.search(sql, sqlList);
			while (rset.next()) {
				TemplateSet setBo=getTemplateSet(rset,var_hm);
				if(setBo!=null){
				    setBoList.add(setBo);
				}
			}
			
			//重新设置单元格四条边线
            int b=0;
            int l=0;
            int r=0;
            int t=0;
            for(int i=0;i<setBoList.size();i++)
            {
                TemplateSet cur_setbo =(TemplateSet)setBoList.get(i);  
                b=getRlineForList(setBoList,"b",cur_setbo.getB(),cur_setbo);
                l=getRlineForList(setBoList,"l",cur_setbo.getL(),cur_setbo);
                r=getRlineForList(setBoList,"r",cur_setbo.getR(),cur_setbo);
                t=getRlineForList(setBoList,"t",cur_setbo.getT(),cur_setbo);
                cur_setbo.setB(b);                  
                cur_setbo.setL(l);
                cur_setbo.setR(r);
                cur_setbo.setT(t);
                new_setbo.add(cur_setbo);
            }

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		*/
		return new_setbo;
    }
    /**
     * 重新取得线型，由于画线的原因
     * @param list
     * @param flag
     * @param line
     * @param cur_setbo//当前操作对象
     * @return
     */
    private int  getRlineForList(ArrayList list,String flag,int line,TemplateSet cur_setbo)
    {
        if(line==0)
            return line;
        else
        {
            float cur_rtop=cur_setbo.getRtop();//得到当前单元格的顶部
            float cur_rheight=cur_setbo.getRheight();//得到当前单元格的高度
            float cur_rleft=cur_setbo.getRleft();//得到当前单元格的左部
            float cur_rwidth=cur_setbo.getRwidth();////得到当前单元格的宽度
            TemplateSet setbo;  
            float rtop=0;
            float rheight=0;
            float rleft=0;
            float rwidth=0;
            int b=0;
            int t=0;
            int r=0;
            int l=0;
            int cur_gridno=cur_setbo.getGridno();
            int gridno=0;
            try
            {  
                for(int i=0;i<list.size();i++)
                {
                    setbo=(TemplateSet)list.get(i);  
                    rtop=setbo.getRtop();
                    rheight=setbo.getRheight();
                    rleft=setbo.getRleft();
                    rwidth=setbo.getRwidth();
                    gridno=setbo.getGridno();
                    if (setbo.getPageId()!=cur_setbo.getPageId()){
                        continue;
                    }
                    if(cur_gridno==gridno)
                        continue;
                    if("t".equals(flag))
                    {
                       b=setbo.getB();//得到每一个单元格的下部                    
                       if(b==0)
                       {
                         if((rtop+rheight)==cur_rtop&&((rleft>=cur_rleft&&rleft+rwidth<=cur_rleft+cur_rwidth)||(rleft<=cur_rleft&&rleft+rwidth>=cur_rleft+cur_rwidth)))
                          {
                             line=0;
                             break;
                          }
                       }
                    }else if("b".equals(flag))
                    {
                        t=setbo.getT();
                        if(t==0)
                        {
                            if(rtop==(cur_rtop+cur_rheight)&&
                                ((rleft>=cur_rleft&&rleft+rwidth<=cur_rleft+cur_rwidth)||
                                 (rleft<=cur_rleft&&rleft+rwidth>=cur_rleft+cur_rwidth)
                                )
                              )
                            {
                                line=0;
                                 break;
                            }
                        }                       
                    }else if("l".equals(flag))
                    {
                        r=setbo.getR();
                        if(r==0)
                        {
                            if((rleft+rwidth)==cur_rleft&&((rtop<=cur_rtop&&(rtop+rheight)>=(cur_rtop+cur_rheight))||(rtop>=cur_rtop&&(rtop+rheight)<=(cur_rtop+cur_rheight))))
                            {
                                line=0;
                                break;
                            }
                        }                       
                    }else if("r".equals(flag))
                    {
                        l=setbo.getL();
                        if(l==0)
                        {
                            if(rleft==(cur_rleft+cur_rwidth)&&((rtop<=cur_rtop&&rtop+rheight>=cur_rtop+cur_rheight)||(rtop>=cur_rtop&&rtop+rheight<=cur_rtop+cur_rheight)))
                            {
                                line=0;
                                break;
                            }
                        }
                    }
                }
                
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }       
        return line; 
    }
 
	public ArrayList getAllTemplateItem(int tabId) throws GeneralException
	{
		ArrayList outPriPageList = new ArrayList();
		return this.getAllTemplateItem(tabId,outPriPageList);
	}
	
	/** 
	* @Title: getAllTemplateItem 
	* @Description: 返回模板中所有的指标项(变量、子集区域)列表
	* @param @param tabId
	* @param @param outPriPageList 
	* @param @param pageNum -1 是获取所有模板页对象。
	* @param @return 请注意：列表中存放的是TemplateItem对象 不是FieldItem对象 
	       * 可通过TemplateItem.getFieldItem()获取FieldItem对象
	* @param @throws GeneralException
	* @return ArrayList
	*/
	public ArrayList getAllTemplateItem(int tabId, ArrayList outPriPageList) throws GeneralException {
		ArrayList templateItemList=new ArrayList();
		try
		{
			StringBuffer msgFieldTypeChange = new StringBuffer();
			StringBuffer msgFieldNull = new StringBuffer();
			StringBuffer msgCodeSetId = new StringBuffer();
			StringBuffer msgUsrFlag = new StringBuffer();
			StringBuffer msgVarItemFlag = new StringBuffer();
			ArrayList celllist=getAllCell(tabId);			
			String flag=null;//数据来源
			String temp=null;
			/**所有临时变量*/
			HashMap var_hm=getAllVariableHm(tabId);
			HashSet<String> tableFieldNameSet = new HashSet<String>();
			TemplateItem templateItem = null;
			for(int i=0;i<celllist.size();i++)
			{
			    TemplateSet setBo=(TemplateSet)celllist.get(i);			
	            flag=setBo.getFlag();
                if(flag==null||"".equals(flag) || "H".equals(flag))
                	 continue;
                if("F".equalsIgnoreCase(flag)){
                	setBo.setChgstate(2);//将附件设置成变化后
                }
                //过滤掉字段相同的 27062 linbz 增加校验条件，只过滤主集的相同字段
                if(tableFieldNameSet.contains(setBo.getTableFieldName()) &&setBo.isSubflag()){//bug 41952 两个设置相同的子集需要过滤掉一个。否则列表显示有问题
                	Boolean isHave=false;
	                	for(int k=0;k<templateItemList.size();k++){
	                		TemplateItem temItem=(TemplateItem) templateItemList.get(k);
	                		TemplateSet cellBo = temItem.getCellBo();
	                		if(cellBo.getTableFieldName().equalsIgnoreCase(setBo.getTableFieldName())){
	                			if(cellBo.getChgstate()==setBo.getChgstate()&&cellBo.getSub_domain_id().equalsIgnoreCase(setBo.getSub_domain_id())){
	                				isHave=true;
	                				break;
	                			}
	                		}
	                	}
                	if(isHave){
                		continue;
                	}
                }
                if(tableFieldNameSet.contains(setBo.getTableFieldName()) &&!setBo.isSubflag()&&!"F".equalsIgnoreCase(flag)){//附件不再过滤，可以显示多个。
                	if(StringUtils.isNotBlank(setBo.getDefaultValue())){
	                	for(int k=0;k<templateItemList.size();k++){
	                		TemplateItem temItem=(TemplateItem) templateItemList.get(k);
	                		TemplateSet cellBo = temItem.getCellBo();
	                		if(cellBo.getTableFieldName().equalsIgnoreCase(setBo.getTableFieldName())&&StringUtils.isBlank(cellBo.getDefaultValue())){
	                			cellBo.setDefaultValue(setBo.getDefaultValue());
	                		}
	                	}
                	}
                	continue;
                }else{
                	if(outPriPageList!=null&&outPriPageList.size()>0){
                		if(outPriPageList.contains(setBo.getPageId()))
                			tableFieldNameSet.add(setBo.getTableFieldName());
                		else
                			continue;
                	}else
                		if("F".equalsIgnoreCase(flag)){//因为附件setBo.getTableFieldName()值相同，为区分在后面追加"_k_"+setBo.getUniqueId()
                			tableFieldNameSet.add(setBo.getTableFieldName()+"_k_"+setBo.getUniqueId());
                        }else{
                        	tableFieldNameSet.add(setBo.getTableFieldName());
                        }
                }
                
				if ("V".equalsIgnoreCase(flag))
				{
					temp=setBo.getField_name()!=null?setBo.getField_name():"";
					RecordVo vo=(RecordVo)var_hm.get(temp);
					//如果vo为null说明临时变量表中无此临时变量，需要提示用户，否则会引起数据问题。
					if(vo!=null)
					{
						TemplateItem temItem=new TemplateItem();
						FieldItem fldItem=varVoToFieldItem(vo);
						fldItem.setDisplayid(setBo.getDisformat()); //将指标要求的格式写入displayid中
						templateItem=new TemplateItem();
					    templateItem.setFieldItem(fldItem);
					    templateItem.setFieldDesc(setBo.getHz());
					    templateItem.setFieldName(setBo.getTableFieldName());
					    templateItem.setbVarialbeItem(true);
					    //29815  linbz 在获取到临时变量对象后没有添加到集合里，故没有加载出来
					    templateItem.setCellBo(setBo);
					    templateItemList.add(templateItem);	
					}
					else
					{
						msgVarItemFlag.append("、［" + setBo.getField_hz()+"］");
					}
				}
				else if("P".equalsIgnoreCase(flag))//照片
				{
					templateItem=new TemplateItem();
					FieldItem fldItem=new FieldItem();
					fldItem.setItemid("photo");
					fldItem.setItemdesc("photo");
					fldItem.setFieldsetid("A00");
					fldItem.setItemtype("L");
					fldItem.setCodesetid("0");	
					
				    templateItem.setFieldItem(fldItem);
				    templateItem.setFieldDesc(setBo.getHz());
				    templateItem.setFieldName(setBo.getTableFieldName());
				    templateItem.setCellBo(setBo);
				    templateItemList.add(templateItem);	
				    
				    fldItem=new FieldItem();
				    fldItem.setItemid("ext");
				    fldItem.setItemdesc("ext");
				    fldItem.setFieldsetid("A00");
				    fldItem.setItemtype("A");
				    fldItem.setItemlength(10);
				    fldItem.setCodesetid("0");
				    
				    templateItem=new TemplateItem();
				    templateItem.setFieldItem(fldItem);
				    templateItem.setFieldDesc("扩展名");
				    templateItem.setFieldName("ext");
				    templateItem.setCellBo(setBo);
				    templateItemList.add(templateItem);	
				    
				    fldItem=new FieldItem();
				    fldItem.setItemid("fileid");
				    fldItem.setItemdesc("fileid");
				    fldItem.setFieldsetid("A00");
				    fldItem.setItemtype("A");
				    fldItem.setItemlength(200);
				    fldItem.setCodesetid("0");
				    
				    templateItem=new TemplateItem();
				    templateItem.setFieldItem(fldItem);
				    templateItem.setFieldDesc("文件ID");
				    templateItem.setFieldName("fileid");
				    templateItem.setCellBo(setBo);
				    templateItemList.add(templateItem);	
				    continue;
				}else{
					//校验指标  lis 20160809 start
					if(!setBo.isSubflag() && setBo.isABKItem() && !setBo.isSpecialItem())
					{
						if(DataDictionary.getFieldItem(setBo.getField_name().trim())!=null)
						{
							FieldItem item=(FieldItem)DataDictionary.getFieldItem(setBo.getField_name()!=null?setBo.getField_name():"").clone();
							if(!"0".equals(item.getUseflag())){//构库
								//数据字典与模板指标类型不一致  20140930 dengcan  xcs 特殊的数据  比如说条件定位等等，就更改了字段的类型 2014-10-9
								if((setBo.getOld_fieldType().length()>0) 
								        &&(!setBo.getOld_fieldType().equalsIgnoreCase(item.getItemtype()))) {
									msgFieldTypeChange.append("、［" + setBo.getField_hz()+"］");
								}
								String setCodeid =setBo.getCodeid().toUpperCase();
								if ("0".equals(setCodeid)){
									setCodeid="";
								}
								String itemCodeid =item.getCodesetid().toUpperCase();
								if ("0".equals(itemCodeid)){
									itemCodeid="";
								}
								if (!setCodeid.equals(itemCodeid)){//代码类发生变化
									//msgCodeSetId.append("、［" + setBo.getField_hz()+"］");
									//直接更改 不提示了
									String sql="update Template_Set set codeid='"+itemCodeid+"'"
											+" where tabid="+tabId
											+" and pageID="+setBo.getPageId()+""
											+" and gridno="+setBo.getGridno()+""								
											+" and field_name ='"+setBo.getField_name().toUpperCase()+"'"	;
									dao.update(sql);
								}
							}
							else{//未构库
								msgUsrFlag.append("、［" + setBo.getField_hz()+"］");
							}
						}else{
							msgFieldNull.append("、［" + setBo.getField_hz()+"］");
						}
					}
					//校验指标 end
					templateItem = this.convertTemplateSetToTemplateItem(setBo);
					if(templateItem!=null)
						templateItem.setCellBo(setBo);//templateItem为空报空指针异常
					if(templateItem != null)
						templateItemList.add(templateItem);	
				}
			}//for i loop end.
			
			//提示信息  lis 20160809
			StringBuffer msg = new StringBuffer();
			if(msgFieldNull.length() > 0){
				msg.append("下列指标在指标体系中不存在：");
				msg.append(msgFieldNull.substring(1));
			}
			if(msgUsrFlag.length()>0){
				if(msgFieldNull.length() > 0)
					msg.append("<br><br>");
				msg.append("下列指标未构库：");
				msg.append(msgUsrFlag.substring(1));
			}
			if(msgFieldTypeChange.length() > 0){
				if(msgFieldNull.length() > 0||msgUsrFlag.length()>0)
					msg.append("<br><br>");
				msg.append("下列指标的指标类型与指标体系不一致：");
				msg.append(msgFieldTypeChange.substring(1));
			}
			if(msgCodeSetId.length() > 0){
				if(msgFieldNull.length() > 0 || msgFieldTypeChange.length()>0||msgUsrFlag.length()>0)
					msg.append("<br><br>");
				msg.append("下列指标代码项发生变化：");
				msg.append(msgCodeSetId.substring(1));
			}
			if(msgVarItemFlag.length()>0)
			{
				if(msgFieldNull.length() > 0 || msgUsrFlag.length()>0|| msgFieldTypeChange.length()>0||msgCodeSetId.length()>0)
					msg.append("<br><br>");
				msg.append("下列临时变量在临时变量表中不存在：");
				msg.append(msgVarItemFlag.substring(1));
			}
			if(msg.length() > 0){
				msg.append("<br><br>请重新设置模板！");
				throw GeneralExceptionHandler.Handle(new Exception(msg.toString()));
			}
		}
		catch(Exception ex)
        {
        	ex.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ex);
        }  
		return templateItemList;
	}
	
	/**
	 * @author lis
	 * @Description: TemplateSet转换成TemplateItem
	 * @date Jul 5, 2016
	 * @param setBo
	 * @return
	 * @throws GeneralException 
	 */
	public TemplateItem convertTemplateSetToTemplateItem(TemplateSet setBo) throws GeneralException{
		TemplateItem templateItem=null;
		try {
			String flag=null;//数据来源
            flag=setBo.getFlag();
            
			if(setBo.isSubflag())
			{
				FieldSet fieldset=DataDictionary.getFieldSetVo(setBo.getSetname());
				if(fieldset!=null)
				{
					FieldItem fldItem=new FieldItem();
					fldItem.setFieldsetid(setBo.getSetname());						
					fldItem.setItemid(setBo.getTableFieldName());
					fldItem.setItemdesc(fieldset.getFieldsetdesc());
					fldItem.setItemtype("M");
				    /**插入子集*/
					fldItem.setFormula(setBo.getXml_param());
					fldItem.setNChgstate(setBo.getChgstate());
				    /**0指标，=1变量，=2子集区域*/
					fldItem.setVarible(2);
					fldItem.setDisplayid(setBo.getDisformat()); //将指标要求的格式写入displayid中
					templateItem=new TemplateItem();
				    templateItem.setFieldItem(fldItem);
				    templateItem.setFieldDesc(setBo.getHz());
				    templateItem.setbSubSetItem(true);
				    templateItem.setFieldName(setBo.getTableFieldName());
				}
			}
			else {
				if(setBo.isABKItem())
				{
					FieldItem item=null;
					String field_name=setBo.getField_name()!=null?setBo.getField_name():"";
					if(setBo.isSpecialItem())
					{
						item=new FieldItem();
						item.setItemid(field_name);
						item.setItemdesc(setBo.getField_hz());
						item.setFieldsetid(setBo.getSetname());
						item.setItemtype(setBo.getField_type());
						item.setCodesetid(setBo.getCodeid()==null?"0":setBo.getCodeid());
						item.setDisplayid(setBo.getDisformat()); //将指标要求的格式写入displayid中
						if(!"start_date".equalsIgnoreCase(field_name)) {
							if(field_name.toLowerCase().startsWith("codeitemdesc")) {
								item.setItemlength(76);
							}else {
								item.setItemlength(50);
							}
						}
						item.setUseflag("1");
					}
					else{
						if(DataDictionary.getFieldItem(setBo.getField_name().trim())!=null)
						{
							item=(FieldItem)DataDictionary.getFieldItem(setBo.getField_name()!=null?setBo.getField_name():"").clone();
							//数据字典与模板指标类型不一致  20140930 dengcan  xcs 特殊的数据  比如说条件定位等等，就更改了字段的类型 2014-10-9
							if((setBo.getOld_fieldType().length()>0) 
							        &&(!setBo.getOld_fieldType().equalsIgnoreCase(item.getItemtype()))) {
								//throw GeneralExceptionHandler.Handle(new Exception("［" + setBo.getField_hz()+"］在模板中定义的指标类型与指标体系不一致，请重新设置模板！"));
							}
						}else{
							//throw GeneralExceptionHandler.Handle(new Exception("［" + setBo.getField_hz()+"］在模板中定义的指标在指标体系中不存在，请重新设置模板！"));
						}
					}
					if(item!=null)
					{
						/**可以增加模板指标与字典表指标进行校验*/
						FieldItem fldItem=(FieldItem)item.cloneItem();
						fldItem.setNChgstate(setBo.getChgstate());
						fldItem.setFillable(setBo.isYneed());
						fldItem.setDisplayid(setBo.getDisformat()); //将指标要求的格式写入displayid中
						templateItem=new TemplateItem();
					    templateItem.setFieldItem(fldItem);
					    templateItem.setFieldDesc(setBo.getHz());
					    templateItem.setFieldName(setBo.getTableFieldName());	
					    templateItem.setbCommonFieldItem(true);
					}
				}
				else if("S".equalsIgnoreCase(flag))//电子签章
				{
					FieldItem fldItem=new FieldItem();
					fldItem.setItemid("signature");
					fldItem.setItemdesc("signature");
					fldItem.setFieldsetid("0");
					fldItem.setItemtype("M");
					fldItem.setCodesetid("0");
					
					templateItem=new TemplateItem();
				    templateItem.setFieldItem(fldItem);
				    templateItem.setFieldDesc(setBo.getHz());
				    templateItem.setFieldName(setBo.getTableFieldName());	
				}else if("F".equalsIgnoreCase(flag))//附件
				{
					TemplateItem temItem=new TemplateItem();
					FieldItem fldItem=new FieldItem();
					fldItem.setItemid("attachment"+setBo.getUniqueId());
					fldItem.setItemdesc("attachment"+setBo.getUniqueId());
					fldItem.setFieldsetid("0");
					fldItem.setItemtype("A");
					fldItem.setCodesetid("0");
					fldItem.setItemlength(20);
					
					templateItem=new TemplateItem();
				    templateItem.setFieldItem(fldItem);
				    templateItem.setFieldDesc(setBo.getHz());
				    templateItem.setFieldName(setBo.getTableFieldName()+setBo.getUniqueId());	
				}
			}
			if (templateItem!=null){
			    templateItem.setFieldType(setBo.getField_type());
			    templateItem.setCellBo(setBo);
			}
		} catch (Exception e) {
			e.printStackTrace();
        	throw GeneralExceptionHandler.Handle(e);
		}
		return templateItem;
	}
	
	/**
	 * 变量转换成FieldItem
	 * @param vo
	 * @return
	 */
	private FieldItem varVoToFieldItem(RecordVo vo)
	{
		FieldItem item=new FieldItem();
		item.setItemdesc(vo.getString("chz"));
		item.setItemid(vo.getString("cname"));
		item.setCodesetid(vo.getString("codesetid"));
		item.setVarible(1);
		item.setFormula(vo.getString("cvalue"));
		item.setItemlength(vo.getInt("fldlen"));
		item.setDecimalwidth(vo.getInt("flddec"));
		if(vo.getInt("ntype")==1)
			item.setItemtype("N");
		else if(vo.getInt("ntype")==2)
			item.setItemtype("A");
		else if(vo.getInt("ntype")==3)
			item.setItemtype("D");
		else if(vo.getInt("ntype")==4)
			item.setItemtype("A");
		
		return item;
	}
    
	/**
	 * 取得当前模板中所有页
	 * @return 列表存放的是TemplatePage对象
	 * @throws GeneralException
	 */
	public ArrayList getAllTemplatePage(int tabId) throws GeneralException {
		ArrayList list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rset = null;
		try {
			sql.append("select * from Template_Page where tabid=");
			sql.append(String.valueOf(tabId)+" order by pageid");
			rset = dao.search(sql.toString());
			String task_id = "0";
			boolean canGetIsMobile = false;
			DbWizard dbw = new DbWizard(this.conn);
			
			if(dbw.isExistField("template_Page", "IsMobile", false)){
			    canGetIsMobile = true;
			}
			boolean hasPaperOriFld = false;
			if(dbw.isExistField("template_Page", "paperOrientation", false)){
				hasPaperOriFld = true;
			}
			boolean hasIsShow = false;
			if(dbw.isExistField("template_Page", "isShow", false)){
				hasIsShow = true;
			}
			while (rset.next()) {
				TemplatePage pagebo = new TemplatePage();
				pagebo.setTabId(tabId);
				pagebo.setPageId(rset.getInt("pageid"));
				pagebo.setTitle(rset.getString("title"));
                if (rset.getInt("isprn") == 0)
                    pagebo.setPrint(false);
                else
                    pagebo.setPrint(true);        
                	
                pagebo.setMobile(false);
                if(canGetIsMobile){
                	String isMobile = rset.getString("IsMobile");//获得页签模版标识    0||null 非手机端模板  1：手机端模板 
                	if ("1".equals(isMobile)){
                		pagebo.setMobile(true);
                	}
                }
                if(hasPaperOriFld){
                	int paperOrientation = rset.getInt("paperOrientation");
                	pagebo.setPaperOrientation(paperOrientation);
                }
                pagebo.setShow(true);
                if(hasIsShow) {
                	String isShow = rset.getString("isShow");//页签是否显示 1||null 显示 0：不显示
                	if("0".equals(isShow)) {
                		pagebo.setShow(false);
                	}
                }
                list.add(pagebo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
			PubFunc.closeResource(rset);
		}
		return list;
	}
	
	
	
	  /**
     * 
     * @Title: readtableVo
     * @Description: 得到业务模板信息
     * @param tabid  模版号
     * @return RecordVo 存放业务模版的vo对象
     * @throws
     */
    public static RecordVo getTableVo(int tabid,Connection conn) {
    	RecordVo tab_vo=TemplateStaticDataBo.getTableVo(tabid, conn); //20171111 邓灿，采用缓存解决并发下压力过大问题
    	/*
    	RecordVo tab_vo=new RecordVo("Template_table");
    	ContentDAO dao=new ContentDAO(conn);
    	RowSet rowSet=null;
    	try
    	{
    		String sql="select template_table.tabid,template_table.name,template_table.noticeid,template_table.gzstandid,template_table.flag";
    		if(Sql_switcher.searchDbServer()==Constant.KUNLUN) //昆仑数据库static为系统关键字
    			sql+=",template_table.\"static\"";
    		else	
    			sql+=",template_table.static";
    		sql+=",template_table.paperori,template_table.paper,template_table.tmargin,template_table.bmargin,template_table.rmargin,template_table.lmargin,template_table.paperw,template_table.paperh";
    		sql+=",template_table.operationcode,template_table.operationname,template_table.factor,template_table.lexpr,template_table.llexpr,template_table.userfalg,template_table.username,template_table.userflag,template_table.sp_flag,template_table.dest_base,template_table.content,template_table.ctrl_para from template_table  WHERE template_table.tabid=?";
    		rowSet=dao.search(sql,Arrays.asList(new Object[] {Integer.valueOf(tabid)}));
    		if(rowSet.next())
    		{
    			tab_vo.setInt("tabid",rowSet.getInt("tabid"));
    			tab_vo.setString("name",rowSet.getString("name"));
    			tab_vo.setString("noticeid",rowSet.getString("noticeid"));
    			tab_vo.setString("gzstandid",rowSet.getString("gzstandid"));
    			tab_vo.setInt("flag",rowSet.getInt("flag"));
    			tab_vo.setInt("static",rowSet.getInt("static"));
    			tab_vo.setString("operationcode",rowSet.getString("operationcode"));
    			tab_vo.setString("operationname",rowSet.getString("operationname")); 
    			tab_vo.setString("factor",Sql_switcher.readMemo(rowSet,"factor"));
    			tab_vo.setString("lexpr",Sql_switcher.readMemo(rowSet,"lexpr"));
    			tab_vo.setString("llexpr",Sql_switcher.readMemo(rowSet,"llexpr")); 
    			tab_vo.setString("userfalg",rowSet.getString("userfalg"));
    			tab_vo.setString("username",rowSet.getString("username"));
    			tab_vo.setString("userflag",rowSet.getString("userflag"));
    			tab_vo.setString("sp_flag",rowSet.getString("sp_flag"));
    			tab_vo.setString("dest_base",rowSet.getString("dest_base"));
    			tab_vo.setString("content",Sql_switcher.readMemo(rowSet,"content"));
    			tab_vo.setString("ctrl_para",Sql_switcher.readMemo(rowSet,"ctrl_para")); 
    			tab_vo.setInt("paperori",rowSet.getInt("paperori"));
    			tab_vo.setInt("paper",rowSet.getInt("paper"));
    			tab_vo.setDouble("tmargin",rowSet.getFloat("tmargin"));
    			tab_vo.setDouble("bmargin",rowSet.getFloat("bmargin"));
    			tab_vo.setDouble("rmargin",rowSet.getFloat("rmargin"));
    			tab_vo.setDouble("lmargin",rowSet.getFloat("lmargin"));
    			tab_vo.setDouble("paperw",rowSet.getFloat("paperw"));
    			tab_vo.setDouble("paperh",rowSet.getFloat("paperh"));
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		PubFunc.closeDbObj(rowSet);
    	}
		*/
		return tab_vo;
    }

    

    /** 
    * @Title: getTableName 
    * @Description: 获取数据表名 自助起草g_templet_X,业务起草username+templet_X,流程中的templet_X
    * @param @param tabId
    * @param @param task_id
    * @param @param module_id
    * @param @return
    * @return String
    */ 
    public String getTableName(String moduleId,int tabId,String taskIds) {
    	String tabName = "";
        if (!"0".equals(taskIds)) {
        	tabName = "templet_" + tabId;
        } else {
        	if("9".equals(moduleId)){//业务申请
        		tabName = "g_templet_" + tabId;  
        	}else{
        		tabName =this.userView.getUserName() + "templet_" + tabId;
        	}
        }
        return tabName;
    }
    
    
    public String getInsId(String taskId) {
    	if ("0".equals(taskId)||"".equals(taskId)){
    		return "0";
    	}
    	String insId="";
        RecordVo vo = new RecordVo("t_wf_task");
        vo.setInt("task_id", Integer.parseInt(taskId));
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            vo = dao.findByPrimaryKey(vo);
            insId=vo.getString("ins_id");            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return insId;
    }
    /**
     * 通过taskIds获取全部ins_id
     * @param taskIds
     * @return
     */
    public ArrayList getTaskIdtoInsId(String taskIds){
    	ArrayList list = new ArrayList();
    	RowSet rs = null;
    	try{
	    	StringBuffer taskIdall = new StringBuffer("");
    		String [] taskIdlist = StringUtils.split(taskIds,",");
    		for(int i=0;i<taskIdlist.length;i++){
    			//校验taskId是否是数字
    			if(!PubFunc.validateNum(taskIdlist[i], 2))
    				continue;
    			if(i != 0)
    				taskIdall.append(",");
    			taskIdall.append(taskIdlist[i]);
			}
	    	if(StringUtils.isEmpty(taskIdall.toString())){
	    		return list;
	    	}
	    	StringBuffer sql = new StringBuffer("");
			sql.append("select task_id,ins_id from t_wf_task where task_id in (").append(taskIdall.toString()).append(")");
			rs = dao.search(sql.toString());
			HashMap map = new HashMap();
			while(rs.next()){
				String task_id = rs.getString("task_id");
				String ins_id = rs.getString("ins_id");
				if(StringUtils.isNotEmpty(task_id) && StringUtils.isNotEmpty(ins_id)){
					map = new HashMap();
		            map.put("task_id", task_id);
		            map.put("ins_id", ins_id);
		            list.add(map);
				}
			}
	    }catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return list;
    }
    
    public TemplateItem getTempItem(String hz,String fieldname,String fieldType,String codeid,String setname,String chgstate,String hismode,String subflag,String isvar)
    {
        FieldItem fldItem=new FieldItem();
        fldItem.setFieldsetid(setname);
        fldItem.setItemid(fieldname);
        fldItem.setItemdesc(hz);
        fldItem.setItemtype("A");
        fldItem.setCodesetid(codeid);
        fldItem.setNChgstate(Integer.parseInt(chgstate));
        
        TemplateItem temItem=new TemplateItem();
        temItem.setFieldItem(fldItem);
        temItem.setFieldName((fieldname+"_"+chgstate).toLowerCase());
        temItem.setFieldType("A");
        temItem.setFieldDesc(hz);
        return temItem;
    }
	
    
    /**
     * 
     * @Title: getFormatDate
     * @Description: TODO
     * @param:  value
     * @param:  disformat
     * @return: String
     * @throws
     */
    public String getFormatDate(String value, int disformat) {
        // TODO Auto-generated method stub
        StringBuffer buf = new StringBuffer();
        String format = null;
        if ("".equals(value)) {
            return "";//不能为" "
        }
        value=value.replace("年", "-").replace("月", "-").replace("日","");
        value=(value.lastIndexOf("-")==value.length()-1)?value.substring(0, value.length()-1):value;
        if(disformat==25){
        	format = "yyyy-MM-dd HH:mm";
        }else{
        	format = "yyyy-MM-dd";
        }
        Date date = (Date) DateUtils.getDate(value,format );
        int year = DateUtils.getYear(date);
        int month = DateUtils.getMonth(date);
        int day = DateUtils.getDay(date);
        int hour = DateUtils.getHour(date);
        int minute = DateUtils.getMinute(date);
        String[] strv = exchangNumToCn(year, month, day);
        value = value.replaceAll("-", ".");
        String temp = null;
        switch (disformat) {
        case 6: // 1991.12.3 按 1991.12.03处理 都统一改了
            buf.append(year);
            buf.append(".");
            buf=getMonthOrDayValue(buf, month);
            buf.append(".");
            buf=getMonthOrDayValue(buf,day);
            break;
        case 7: // 91.12.3
        	temp = String.valueOf(year);
            buf.append(temp.substring(2));
            buf.append(".");
            buf=getMonthOrDayValue(buf, month);
            buf.append(".");
            buf=getMonthOrDayValue(buf,day);
            break;
        case 8:// 1991.2
            buf.append(year);
            buf.append(".");
            buf.append(month);
            break;
        case 9:// 1992.02
            buf.append(value.substring(0, 7));
            break;
        case 10:// 92.2
        	temp = String.valueOf(year);
            buf.append(temp.substring(2));
            buf.append(".");
            buf.append(month);
            break;
        case 11:// 98.02
        	temp = String.valueOf(year);
            buf.append(temp.substring(2));
            buf.append(".");
            buf=getMonthOrDayValue(buf, month);
            break;
        case 12:// 一九九一年一月二日
            buf.append(strv[0]);
            buf.append("年");
            buf.append(strv[1]);
            buf.append("月");
            buf.append(strv[2]);
            buf.append("日");
            break;
        case 13:// 一九九一年一月
            buf.append(strv[0]);
            buf.append("年");
            buf.append(strv[1]);
            buf.append("月");
            break;
        case 14:// 1991年1月2日
            buf.append(year);
            buf.append("年");
            buf.append(month);
            buf.append("月");
            buf.append(day);
            buf.append("日");
            break;
        case 15:// 1991年1月
            buf.append(year);
            buf.append("年");
            buf.append(month);
            buf.append("月");
            break;
        case 16:// 91年1月2日
        	temp = String.valueOf(year);
            buf.append(temp.substring(2));
            buf.append("年");
            buf.append(month);
            buf.append("月");
            buf.append(day);
            buf.append("日");
            break;
        case 17:// 91年1月
        	temp = String.valueOf(year);
            buf.append(temp.substring(2));
            buf.append("年");
            buf.append(month);
            buf.append("月");
            break;
        case 18:// 年龄
            buf.append(getAge(year, month, day));
            break;
        case 19:// 1991（年）
            buf.append(year);
            break;
        case 20:// 1 （月）
            buf.append(month);
            break;
        case 21:// 23 （日）
            buf.append(day);
            break;
        case 22:// 1999年02月
            buf.append(year);
            buf.append("年");
            buf=getMonthOrDayValue(buf, month);
            buf.append("月");
            break;
        case 23:// 1999年02月03日
            buf.append(year);
            buf.append("年");
            buf=getMonthOrDayValue(buf, month);
            buf.append("月");
            buf=getMonthOrDayValue(buf,day);
            buf.append("日");
            break;
        case 24:// 1992.02.01
            buf.append(year);
            buf.append(".");
            buf=getMonthOrDayValue(buf, month);
            buf.append(".");
            buf=getMonthOrDayValue(buf,day);
            break;
        case 25:// 1992.02.01 10:30
            buf.append(year);
            buf.append(".");
            buf=getMonthOrDayValue(buf, month);
            buf.append(".");
            buf=getMonthOrDayValue(buf,day);
            buf.append(" ");
            buf = getMonthOrDayValue(buf,hour);
            buf.append(":");
            buf = getMonthOrDayValue(buf,minute);
            break;
        default:
            buf.append(year);
            buf.append(".");
            buf=getMonthOrDayValue(buf, month);
            buf.append(".");
            buf=getMonthOrDayValue(buf,day);
            break;
        }
        return buf.toString();
    }
    public StringBuffer getMonthOrDayValue(StringBuffer buf,int value){
        if(value>=10){
            buf.append(value);
        }else{
            buf.append("0"+value);
        }
        return buf;
        
    }
    /**
     * @Title: getAge
     * @Description: TODO
     * @param year
     * @param month
     * @param day
     * @return
     * @throws Object
     */
    private String getAge(int nyear, int nmonth, int nday) {
        // TODO Auto-generated method stub

        int ncyear, ncmonth, ncday;
        Date curdate = new Date();
        ncyear = DateUtils.getYear(curdate);
        ncmonth = DateUtils.getMonth(curdate);
        ncday = DateUtils.getDay(curdate);
        StringBuffer buf = new StringBuffer();
        int result = ncyear - nyear;
        if (nmonth > ncmonth) {
            result = result - 1;
        } else {
            if (nmonth == ncmonth) {
                if (nday > ncday) {
                    result = result - 1;
                }
            }
        }
        buf.append(result);
        return buf.toString();
    }

    /**
     * @Title: exchangNumToCn
     * @Description: TODO
     * @param year
     * @param month
     * @param day
     * @return
     * @throws String []
     */
    private String[] exchangNumToCn(int year, int month, int day) {
        // TODO Auto-generated method stub

        String[] strarr = new String[3];
        StringBuffer buf = new StringBuffer();
        String value = String.valueOf(year);
        for (int i = 0; i < value.length(); i++) {
            switch (value.charAt(i)) {
            case '1':
                buf.append("一");
                break;
            case '2':
                buf.append("二");
                break;
            case '3':
                buf.append("三");
                break;
            case '4':
                buf.append("四");
                break;
            case '5':
                buf.append("五");
                break;
            case '6':
                buf.append("六");
                break;
            case '7':
                buf.append("七");
                break;
            case '8':
                buf.append("八");
                break;
            case '9':
                buf.append("九");
                break;
            case '0':
                buf.append("零");
                break;
            }
        }
        strarr[0] = buf.toString();
        buf.setLength(0);
        switch (month) {
        case 1:
            buf.append("一");
            break;
        case 2:
            buf.append("二");
            break;
        case 3:
            buf.append("三");
            break;
        case 4:
            buf.append("四");
            break;
        case 5:
            buf.append("五");
            break;
        case 6:
            buf.append("六");
            break;
        case 7:
            buf.append("七");
            break;
        case 8:
            buf.append("八");
            break;
        case 9:
            buf.append("九");
            break;
        case 10:
            buf.append("十");
            break;
        case 11:
            buf.append("十一");
            break;
        case 12:
            buf.append("十二");
            break;
        }
        strarr[1] = buf.toString();
        buf.setLength(0);
        switch (day) {
        case 1:
            buf.append("一");
            break;
        case 2:
            buf.append("二");
            break;
        case 3:
            buf.append("三");
            break;
        case 4:
            buf.append("四");
            break;
        case 5:
            buf.append("五");
            break;
        case 6:
            buf.append("六");
            break;
        case 7:
            buf.append("七");
            break;
        case 8:
            buf.append("八");
            break;
        case 9:
            buf.append("九");
            break;
        case 10:
            buf.append("十");
            break;
        case 11:
            buf.append("十一");
            break;
        case 12:
            buf.append("十二");
            break;
        case 13:
            buf.append("十三");
            break;
        case 14:
            buf.append("十四");
            break;
        case 15:
            buf.append("十五");
            break;
        case 16:
            buf.append("十六");
            break;
        case 17:
            buf.append("十七");
            break;
        case 18:
            buf.append("十八");
            break;
        case 19:
            buf.append("十九");
            break;
        case 20:
            buf.append("二十");
            break;
        case 21:
            buf.append("二十一");
            break;
        case 22:
            buf.append("二十二");
            break;
        case 23:
            buf.append("二十三");
            break;
        case 24:
            buf.append("二十四");
            break;
        case 25:
            buf.append("二十五");
            break;
        case 26:
            buf.append("二十六");
            break;
        case 27:
            buf.append("二十七");
            break;
        case 28:
            buf.append("二十八");
            break;
        case 29:
            buf.append("二十九");
            break;
        case 30:
            buf.append("三十");
            break;
        case 31:
            buf.append("三十一");
            break;
        }
        strarr[2] = buf.toString();
        return strarr;
    }
    
	/*
	 * 判断是否是职称评审投票系统，此系统不使用ehr的用户，许多判断权限的地方需要特殊判断 
	 * */
	public static boolean isJobtitleVoteModule(UserView view) {
		boolean b=false;
		if (view.getHm().get("moduleFlag")!=null){
			if ("jobtitleVote".equals((String)view.getHm().get("moduleFlag"))){
				b=true;
			}
		}
		return b;
	}	
	/**
	 * 获得节点定义的指标必填项，变化后指标，无读值为0，写值为2，写并且必填值3
	 * @param task_id
	 * @return
	 */
	public HashMap getFieldPrivFillable(String task_id) {
		HashMap _map=new HashMap();
		Document doc=null;
		Element element=null;
		try
		{
			
			if(task_id!=null&&!"0".equals(task_id.trim()))
			{
				ContentDAO dao=new ContentDAO(this.conn);
				String sql="select * from t_wf_node where node_id=(select node_id from t_wf_task where task_id="+task_id+" )";
				RowSet rowSet=dao.search(sql);
				if(rowSet.next())
				{
					String ext_param= Sql_switcher.readMemo(rowSet,"ext_param"); 
					if(ext_param!=null&&ext_param.trim().length()>0)
					{
						doc=PubFunc.generateDom(ext_param); 
						String xpath="/params/field_priv/field";
						XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
						List childlist=findPath.selectNodes(doc);	
						if(childlist.size()==0){
							xpath="/params/field_priv/field";
							 findPath = XPath.newInstance(xpath);// 取得符合条件的节点
							 childlist=findPath.selectNodes(doc);
						}
						if(childlist!=null&&childlist.size()>0)
						{
							for(int i=0;i<childlist.size();i++)
							{
								element=(Element)childlist.get(i);
								String editable="";
								//0|1|2(无|读|写)
								if(element!=null&&element.getAttributeValue("editable")!=null)
									editable=element.getAttributeValue("editable");
								if(editable!=null&&editable.trim().length()>0)
								{
									String columnname=element.getAttributeValue("name").toLowerCase();
									if(columnname.endsWith("_2")|| columnname.startsWith("S_")||"photo".equals(columnname)||"attachment".equals(columnname) ){
										if("1".equals(editable))
											editable="0";
										String fillable = element.getAttributeValue("fillable");
										if("2".equals(editable)&&fillable!=null&& "true".equalsIgnoreCase(fillable))
											editable="3";
										_map.put(columnname.split("_")[0], editable);
									}
								}
							}
						}
					}
				}
				PubFunc.closeDbObj(rowSet);
			
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return _map;
	}
	public HashMap getFieldPrivFillable(String task_id,int tabid) {
		HashMap _map=new HashMap();
		Document doc=null;
		Element element=null;
		try
		{
			
			if(task_id!=null)
			{
				ContentDAO dao=new ContentDAO(this.conn);
				String sql="select ext_param from t_wf_node where node_id=(select node_id from t_wf_task where task_id="+task_id+" )";
				if("0".equals(task_id.trim())){
					sql="select ext_param from t_wf_node where nodetype=1 and tabid="+tabid;
				}
				RowSet rowSet=dao.search(sql);
				if(rowSet.next())
				{
					String ext_param= Sql_switcher.readMemo(rowSet,"ext_param"); 
					if(ext_param!=null&&ext_param.trim().length()>0)
					{
						doc=PubFunc.generateDom(ext_param);
						String xpath="/params/field_priv/field";
						XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
						List childlist=findPath.selectNodes(doc);	
						if(childlist.size()==0){
							xpath="/params/field_priv/field";
							 findPath = XPath.newInstance(xpath);// 取得符合条件的节点
							 childlist=findPath.selectNodes(doc);
						}
						if(childlist!=null&&childlist.size()>0)
						{
							for(int i=0;i<childlist.size();i++)
							{
								element=(Element)childlist.get(i);
								String editable="";
								//0|1|2(无|读|写)
								if(element!=null&&element.getAttributeValue("editable")!=null)
									editable=element.getAttributeValue("editable");
								if(editable!=null&&editable.trim().length()>0)
								{
									String columnname=element.getAttributeValue("name").toLowerCase();
									if(columnname.endsWith("_2")|| columnname.startsWith("s_")||"photo".equals(columnname)||"attachment".equals(columnname) ){
										if("1".equals(editable))
											editable="0";
										String fillable = element.getAttributeValue("fillable");
										if("2".equals(editable)&&fillable!=null&& "true".equalsIgnoreCase(fillable))
											editable="3";
										if(columnname.startsWith("s_")){
											_map.put(columnname.toUpperCase(), editable);
										}else{
											_map.put(columnname.split("_")[0], editable);
										}
									}
								}
							}
						}
					}
				}
				PubFunc.closeDbObj(rowSet);
			
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return _map;
	}
	//解析并且格式化审批意见。
	public static ArrayList formatOptionFiledValue(String optionFieldValue){
		ArrayList<String> valueList = new ArrayList<String>();
		if(StringUtils.isBlank(optionFieldValue)){
			return valueList;
		}
		optionFieldValue=optionFieldValue.replace("\\n", "\n").replace("\r\n", "\n").replace("\r", "\n").replace("\n\n","\n");
		String[] rowValue = optionFieldValue.split("\n");// 根据\n分隔审批意见
		int optionFormatType = 0;
		int rowIndex = 0;// 记录每个节点的行数
		int nodeIndex = 0;// 记录是第几个节点，区分是申请节点还是审批节点
		TemplateOptionField optionFile = null;// 定义审批意见对象
		for (int row = 0; row < rowValue.length; row++) {
			String rowInfo = rowValue[row];
			if(StringUtils.isBlank(rowInfo.trim())){
				rowIndex++;
				continue;
			}
			if ((rowInfo.contains(ResourceFactory.getProperty("format.optionfield.applicationtime"))
					&& rowInfo.contains(ResourceFactory.getProperty("format.optionfield.proposer")))||(
				rowInfo.contains(ResourceFactory.getProperty("format.optionfield.approver2"))
					&& rowInfo.contains(ResourceFactory.getProperty("format.optionfield.approvertime"))	)) {
				
				if(rowInfo.contains(ResourceFactory.getProperty("format.optionfield.applicationtime"))
					&& rowInfo.contains(ResourceFactory.getProperty("format.optionfield.proposer"))){
					nodeIndex=0;
				}
				if(rowInfo.contains(ResourceFactory.getProperty("format.optionfield.approver2"))
					&& rowInfo.contains(ResourceFactory.getProperty("format.optionfield.approvertime"))	){
					nodeIndex=1;
				}
				/**
				 * 申请人:xxx 申请时间:xxx 意见:同意 
				 * 批注: 
				 * 审批人:xxx 审批时间:xxx 意见:同意 
				 * 批注:
				 **/
				optionFormatType = 1;
				rowIndex=0;
			}
			else if (((!rowInfo.contains(ResourceFactory.getProperty("format.optionfield.applicationtime")))
					&& rowInfo.contains(ResourceFactory.getProperty("format.optionfield.proposer")))||(
				(!rowInfo.contains(ResourceFactory.getProperty("format.optionfield.approvertime")))
					&& (rowInfo.contains(ResourceFactory.getProperty("format.optionfield.opinionAgree"))||rowInfo.contains(ResourceFactory.getProperty("format.optionfield.opinionDisagree")))&&(!rowInfo.contains(ResourceFactory.getProperty("format.optionfield.annotation"))))) {
				if((!rowInfo.contains(ResourceFactory.getProperty("format.optionfield.applicationtime")))
					&& rowInfo.contains(ResourceFactory.getProperty("format.optionfield.proposer"))){
					nodeIndex=0;
				}
				if(
				(!rowInfo.contains(ResourceFactory.getProperty("format.optionfield.approvertime")))
					&& (rowInfo.contains(ResourceFactory.getProperty("format.optionfield.opinionAgree"))||rowInfo.contains(ResourceFactory.getProperty("format.optionfield.opinionDisagree")))&&(!rowInfo.contains(ResourceFactory.getProperty("format.optionfield.annotation")))){
					nodeIndex=1;
				}
				/**
				 * 申请人： xxx 2017-03-09 09:23 
				 * xxx意见：同意 xxx 2017-03-09 21:52
				 * 批注：不同意
				 */
				optionFormatType = 2;
				rowIndex=0;
			}
			else if(rowInfo.contains("(")&&rowInfo.contains(")：")){				
				/**
				 * 总部/研发中心/项目研发部(申请人)： 
				 * 王俊琪 2018-08-13 13:45
				 * 
				 * 总部/研发中心/项目研发部(审批人)： 
				 * 同意 王建华 2018-08-13 13:56 批注：通过
				 */
				if(row+1< rowValue.length){
					String nextRowInfo=rowValue[row+1];
					if(nextRowInfo.contains(ResourceFactory.getProperty("format.optionfield.fill"))||nextRowInfo.contains(ResourceFactory.getProperty("format.optionfield.fillopinion"))){
						/**
						 * 总部/研发中心/项目研发部(申请人)：
						 *  员工填写 
						 * 王俊琪   2018-08-13 13:45
						 * 
						 * 总部/研发中心/项目研发部(审批人)：
						 *  部门领导填写 
						 *  同意 王建华 2018-08-13 13:56 
						 *  批注：通过
						 */
						if(rowInfo.contains(ResourceFactory.getProperty("format.optionfield.proposer1"))){
							nodeIndex=0;
						}
						if(rowInfo.contains(ResourceFactory.getProperty("format.optionfield.approver1"))){
							nodeIndex=1;
						}
						optionFormatType = 4;
					}else{
						if(nextRowInfo.contains(ResourceFactory.getProperty("format.optionfield.agree"))||nextRowInfo.contains(ResourceFactory.getProperty("format.optionfield.disagree"))){
							nodeIndex=1;
						}else{
							nodeIndex=0;
						}
						optionFormatType = 3;
					}
				}else{
					optionFormatType = 3;
				}
				rowIndex=0;
			}
			if(rowIndex==0){
				if(row!=0){
					if(optionFile!=null){
						valueList.add(JSON.toString(optionFile.changeObjectToMap()));
						optionFile = new TemplateOptionField();
					}
				}
			}
			if(optionFile==null){
				optionFile = new TemplateOptionField();
			}
			switch (optionFormatType) {
				case 1: {
					String[] colValue = rowInfo.split("\\s+");// 用空格分割每行数据去得每行中每列数据
					for (int col = 0; col < colValue.length; col++) {// 循环一行没列的值
						String colInfo = colValue[col];
						String[] itemInfo = colInfo.split(":",2);
						if (rowIndex % 2 == 0) {// 奇数行第一列是审批人、审批时间、审批意见
							switch (col) {
								case 0: {
									optionFile.setApproverType(itemInfo[0]);// 申请人或审批人
									optionFile.setApproverName(itemInfo[1]);// 姓名
									break;
								}
								case 1: {
									optionFile.setApprovalTime(itemInfo[1]);// 审批时间
									break;
								}
								case 2: {
									optionFile.setApproverType(itemInfo[1]);// 同意、不同意
									break;
								}
							}
						} else {// 偶数行是批注
							optionFile.setApproverAnnotation(itemInfo[1]);// 批注
						}
					}
					if(nodeIndex==0){
						optionFile.setApproverAnnotation(ResourceFactory.getProperty("format.optionfield.startPro"));
					}
					rowIndex++;
					break;
				}
				case 2: {
					String[] colValue = rowInfo.split("\\s+");// 用空格分割每行数据
					for (int col = 0; col < colValue.length; col++) {
						String colInfo = colValue[col];
						String[] itemInfo = colInfo.split(":",2);
						if (nodeIndex == 0) {// 第一行固定是申请人信息
							switch (col) {
								case 0: {
									optionFile.setApproverType(itemInfo[0]);// 申请人
									break;
								}
								case 1: {
									optionFile.setApproverName(itemInfo[0]);// 申请人姓名
									break;
								}
								case 2: {
									optionFile.setApprovalTime(itemInfo[0]);// 申请时间
									break;
								}
							}
						} else {
							if (!rowInfo.contains(ResourceFactory.getProperty("format.optionfield.annotation"))) {// 如果不包含批注说明是审批人、审批意见、审批时间记录行。
								switch (col) {
									case 0: {
										optionFile.setApproverType(ResourceFactory.getProperty("format.optionfield.approver"));// 审批人
										optionFile.setApproverRole(itemInfo[0]
												.replace(ResourceFactory.getProperty("format.optionfield.opinion"), ""));// 节点名称
										optionFile.setApproverOpinion(itemInfo[1]);// 同意、不同意
										break;
									}
									case 1: {
										optionFile.setApproverName(itemInfo[0]);// 审批人姓名
										break;
									}
									case 2: {
										optionFile.setApprovalTime(itemInfo[0]);// 审批时间
										break;
									}
								}
							} else {// 批注信息
								if (itemInfo.length > 0) {
									optionFile.setApproverAnnotation(itemInfo[1]);// 批注
								}
							}
						}
					}
					if(nodeIndex==0){
						optionFile.setApproverAnnotation(ResourceFactory.getProperty("format.optionfield.startPro"));
					}
					rowIndex++;
					break;
				}
				case 3: {
					switch (rowIndex) {// 格式3数据比较规范，可以根据节点行数拆分每行的数据
						case 0: {// 审批人\申请人 单位部门信息 节点类型信息
							if (nodeIndex == 0) {// 申请节点
								optionFile.setApproverType(ResourceFactory.getProperty("format.optionfield.proposer2"));// 申请人
							} else {
								optionFile.setApproverType(ResourceFactory.getProperty("format.optionfield.approver"));// 审批人
							}
							if (rowInfo.trim().length() > 0) {// 如果替换后的还有内容，则进行分析单位、部门信息
								int num = rowInfo.indexOf("(");
								int lastNum = rowInfo.lastIndexOf(")");
								String roleInfo=rowInfo.substring(num+1,lastNum);
								if (nodeIndex == 0) {
									optionFile.setApproverRole(roleInfo+ResourceFactory.getProperty("format.optionfield.fill"));
								}else{
									optionFile.setApproverRole(roleInfo+ResourceFactory.getProperty("format.optionfield.fillopinion"));
								}
								rowInfo=rowInfo.substring(0,num);
								int firstIndex = rowInfo.indexOf("/");
								int lastIndex = rowInfo.lastIndexOf("/");
								if(firstIndex>-1&&lastIndex>-1){
									optionFile.setApproverUnit(rowInfo.substring(0, firstIndex));
									optionFile.setApproverDepartment(rowInfo.substring(lastIndex).replace("/", ""));
								}
							}
							break;
						}
						case 1: {// 审批人\申请人 姓名 时间
							String[] colInfo = rowInfo.split("\\s+",4);
							if (nodeIndex == 0) {
								optionFile.setApproverName(colInfo[0]);
								optionFile.setApprovalTime(colInfo[1] + " " + colInfo[2]);
							} else {
								optionFile.setApproverOpinion(colInfo[0]);
								optionFile.setApproverName(colInfo[1]);
								optionFile.setApprovalTime(colInfo[2] + " " + colInfo[3]);
							}
							break;
						}
						case 2: {// 审批人\申请人 批注
							if (nodeIndex != 0) {
								String[] colInfo = rowInfo.split("：",2);
								if (colInfo.length > 0) {
									optionFile.setApproverAnnotation(colInfo[1]);
								}
							}
							break;
						}
					}
					if(nodeIndex==0){
						optionFile.setApproverAnnotation(ResourceFactory.getProperty("format.optionfield.startPro"));
					}
					rowIndex++;
					break;
				}
				case 4: {
					switch (rowIndex) {
						case 0: {
							if (nodeIndex == 0) {
								optionFile.setApproverType(ResourceFactory.getProperty("format.optionfield.proposer2"));
							} else {
								optionFile.setApproverType(ResourceFactory.getProperty("format.optionfield.approver"));
							}
							if (rowInfo.trim().length() > 0) {
								int num = rowInfo.indexOf("(");
								int lastNum = rowInfo.lastIndexOf(")");
								String roleInfo=rowInfo.substring(num+1,lastNum);
								optionFile.setApproverRole(roleInfo);
								rowInfo=rowInfo.substring(0,num);
								int firstIndex = rowInfo.indexOf("/");
								int lastIndex = rowInfo.lastIndexOf("/");
								if(firstIndex>-1&&lastIndex>-1){
									optionFile.setApproverUnit(rowInfo.substring(0, firstIndex));
									optionFile.setApproverDepartment(rowInfo.substring(lastIndex).replace("/", ""));
								}
							}
							break;
						}
						case 1: {
							optionFile.setApproverRole(rowInfo);
							break;
						}
						case 2: {
							String[] colInfo = rowInfo.split("\\s+",4);
							if (nodeIndex == 0) {
								optionFile.setApproverName(colInfo[0]);
								optionFile.setApprovalTime(colInfo[1] + " " + colInfo[2]);
							} else {
								optionFile.setApproverOpinion(colInfo[0]);
								optionFile.setApproverName(colInfo[1]);
								optionFile.setApprovalTime(colInfo[2] + " " + colInfo[3]);
							}
							break;
						}
						case 3: {
							if (nodeIndex != 0) {							
								String[] colInfo = rowInfo.split("：",2);
								if (colInfo.length > 0) {
									optionFile.setApproverAnnotation(colInfo[1]);
								}
							}
							break;
						}
					}
					if(nodeIndex==0){
						optionFile.setApproverAnnotation(ResourceFactory.getProperty("format.optionfield.startPro"));
					}
					rowIndex++;
					break;
				}
				default: {
					break;
				}
			}
		}
		valueList.add(JSON.toString(optionFile.changeObjectToMap()));
		optionFile = new TemplateOptionField();
		return valueList;
	}
	/**
	 * 判断此流程中是否有驳回的任务。
	 * @param taskId
	 * @return
	 */
	public Boolean isHaveRejectTaskByTaskId(String taskId) {
		RowSet rowSet=null;
		ContentDAO dao=new ContentDAO(this.conn);
		Boolean isReject=false;
		try {
			String[] task=taskId.split(",");
			for(int num=0;num<task.length;num++){
				String sql="select state from t_wf_task where ins_id in(Select ins_id from t_wf_task where task_id=?) order by task_id asc";
				ArrayList paramlist=new ArrayList();
				paramlist.add(task[num]);
				rowSet=dao.search(sql,paramlist);
				while(rowSet.next()){
					String state=rowSet.getString("state");
					if("07".equalsIgnoreCase(state)){
						isReject=true;
						break;
					}
				}
				if(isReject){
					break;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
		return isReject;
	}
	public Boolean isHaveRejectTaskByInsId(String ins_id) {
		RowSet rowSet=null;
		ContentDAO dao=new ContentDAO(this.conn);
		Boolean isReject=false;
		try {
			String sql="select state from t_wf_task where ins_id =? order by task_id asc";
			ArrayList paramlist=new ArrayList();
			paramlist.add(ins_id);
			rowSet=dao.search(sql,paramlist);
			while(rowSet.next()){
				String state=rowSet.getString("state");
				if("07".equalsIgnoreCase(state)){
					isReject=true;
					break;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
		return isReject;
	}
	/**
	 * 当前这个任务是否是驳回的任务
	 * @param task_id
	 * @return
	 */
	public Boolean CurrIsRejectTaskByTaskId(String task_id) {
		RowSet rowSet=null;
		ContentDAO dao=new ContentDAO(this.conn);
		Boolean isReject=false;
		try {
			String sql="select state from t_wf_task where ins_id in(Select ins_id from t_wf_task where task_id=?) order by task_id asc";
			ArrayList paramlist=new ArrayList();
			paramlist.add(task_id);
			rowSet=dao.search(sql,paramlist);
			while(rowSet.next()){
				String state=rowSet.getString("state");
				if("07".equalsIgnoreCase(state)){
					isReject=true;
					break;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
		return isReject;
	}
	/**
	 * 查询流程是否结束
	 * @param ins_id
	 */
	public boolean isFinishedTask(String ins_id) {
		boolean isFinished = false;
        RecordVo vo = new RecordVo("t_wf_instance");
        vo.setInt("ins_id", Integer.parseInt(ins_id));
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            vo = dao.findByPrimaryKey(vo);
            String finished = vo.getString("finished");
            if("5".equals(finished)) {//结束的
            	isFinished = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isFinished;
	}
	
	/**
     * 查询出子集的对应，t_aXX_id_chgstate:XXX子集名称
     * @param tabid
     * @return
     */
    public HashMap<String, String> getFieldSetMap(int tabid) {
    	RowSet rs = null;
    	HashMap<String, String> map = new HashMap<String, String>();
    	ArrayList<Integer> list = new ArrayList<Integer>();
    	ContentDAO dao = new ContentDAO(conn);
    	try {
    		list.add(tabid);
    		rs = dao.search("select Hz,setName,sub_domain,ChgState from Template_Set where TabId=? and subflag = 1", list);
    		while(rs.next()) {
    			String sub_domain = rs.getString("sub_domain");
    			String hz = rs.getString("Hz");
    			String setName = rs.getString("setName");
    			String chgState = rs.getString("ChgState");
    			//解析xml，得到id，最后拼接
    			TSubSetDomain TSubSetDomain = new TSubSetDomain(sub_domain);
    			String id = TSubSetDomain.getId();
    			id = StringUtils.isNotBlank(id)?"_"+id:"";
    			map.put("t_" + setName.toLowerCase() + id + "_" + chgState, hz.replace("{", "").replace("}", "").replace("`", ""));
    		}
    	}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
    	return map;
    }
}
