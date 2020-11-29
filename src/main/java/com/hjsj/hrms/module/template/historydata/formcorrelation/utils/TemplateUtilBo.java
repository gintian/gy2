package com.hjsj.hrms.module.template.historydata.formcorrelation.utils;

import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean.TemplatePage;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean.TemplateSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 模板方法类
* @Title: TemplateUtilBo
* @Description:
* @author: hej
* @date 2019年11月19日 下午5:14:40
* @version
 */
public class TemplateUtilBo {
    private Connection conn=null;
    private UserView userView;
	public TemplateUtilBo(Connection conn,UserView userview) {
		this.conn = conn;
		this.userView = userview;
	}
	
	/**
	 * 如果为null返回“”字符串
	 * @param value
	 * @return
	 */
	public String nullToSpace(String value)
	{
		if(value==null||"null".equalsIgnoreCase(value))
			return "";
		else 
			return value;
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
    
	/**
	 * 取得当前模板中所有页
	 * @param archive_id 
	 * @param pageid 
	 * @return 列表存放的是TemplatePage对象
	 * @throws GeneralException
	 */
	public ArrayList getAllArchiveTemplatePage(int tabId, String archive_id, int pageid) throws GeneralException {
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try {
			ArrayList pageList = getTemplateArchivePage(tabId,archive_id);
			for(int i=0;i<pageList.size();i++) {
				HashMap pageMap = (HashMap) pageList.get(i);
				TemplatePage pagebo = new TemplatePage();
				pagebo.setTabId(tabId);
				int page_id = Integer.parseInt((String)pageMap.get("pageid"));
            	if(pageid!=-1&&pageid!=page_id) {
            		continue;
            	}
				pagebo.setPageId(page_id);
				pagebo.setTitle((String)pageMap.get("title"));
                if (Integer.parseInt((String)pageMap.get("isprn")) == 0)
                    pagebo.setPrint(false);
                else
                    pagebo.setPrint(true);        
                pagebo.setMobile(false);
            	String isMobile = (String)pageMap.get("ismobile");//获得页签模版标识    0||null 非手机端模板  1：手机端模板 
            	if ("1".equals(isMobile)){
            		pagebo.setMobile(true);
            	}
            	String paperorientation = StringUtils.isBlank((String)pageMap.get("paperorientation"))?"0":(String)pageMap.get("paperorientation");
            	int paperOrientation = Integer.parseInt(paperorientation);
            	pagebo.setPaperOrientation(paperOrientation);
                pagebo.setShow(true);
            	String isShow = (String)pageMap.get("isshow");//页签是否显示 1||null 显示 0：不显示
            	if("0".equals(isShow)) {
            		pagebo.setShow(false);
            	}
                list.add(pagebo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rowSet);
		}
		return list;
	}
	/**
	 * 
	 * @param tabid
	 * @param archive_id
	 * @return
	 */
	public ArrayList getTemplateArchivePage(int tabid, String archive_id) {
		ArrayList list = new ArrayList();
		Document doc = null;
		RowSet rowSet = null;
		try
		{ 
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "select archived_data from t_cells_archive where tabid=? and id=?";
			ArrayList sqlList = new ArrayList();
			sqlList.add(tabid);
			sqlList.add(archive_id);
			rowSet = dao.search(sql, sqlList);
			String archived_data = "";
			if (rowSet.next()) {
				archived_data = rowSet.getString("archived_data");
			}
            doc=PubFunc.generateDom(archived_data); 
            String xpath="/data/template_page/record";
            XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
            List childlist=findPath.selectNodes(doc); 
            for(int i=0;i<childlist.size();i++) {
            	Element element = (Element) childlist.get(i);
            	HashMap map = new HashMap(); 
            	map.put("tabid",element.getChild("tabid").getValue());
            	map.put("pageid",element.getChild("pageid").getValue());
            	map.put("title",element.getChild("title").getValue());
            	map.put("flag",element.getChild("flag").getValue());
            	map.put("isprn",element.getChild("isprn").getValue());
            	map.put("ismobile",element.getChild("ismobile").getValue());
            	map.put("paperorientation","null".equals(element.getChild("paperorientation").getValue())?"":element.getChild("paperorientation").getValue());
            	map.put("isshow","null".equals(element.getChild("isshow").getValue())?"":element.getChild("isshow").getValue());
            	list.add(map);
            }
		}catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 获取模板页信息
	 * @param tabid
	 * @param archive_id
	 * @param pageid
	 * @return
	 */
	public ArrayList getTemplateArchiveTitle(int tabid, String archive_id, int pageid) {
		ArrayList list = new ArrayList();
		Document doc = null;
		RowSet rowSet = null;
		try
		{ 
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "select archived_data from t_cells_archive where tabid=? and id=?";
			ArrayList sqlList = new ArrayList();
			sqlList.add(tabid);
			sqlList.add(archive_id);
			rowSet = dao.search(sql, sqlList);
			String archived_data = "";
			if (rowSet.next()) {
				archived_data = rowSet.getString("archived_data");
			}
            doc=PubFunc.generateDom(archived_data);
            String xpath="/data/template_title/record";
            XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
            List childlist=findPath.selectNodes(doc); 
            for(int i=0;i<childlist.size();i++) {
            	Element element = (Element) childlist.get(i);
            	HashMap map = new HashMap(); 
            	int page_id = Integer.parseInt(element.getChild("pageid").getValue());
            	if(pageid!=-1&&pageid!=page_id) {
            		continue;
            	}
            	map.put("tabid",element.getChild("tabid").getValue());
            	map.put("pageid",element.getChild("pageid").getValue());
            	map.put("gridno",element.getChild("gridno").getValue());
            	map.put("hz",element.getChild("hz").getValue());
            	map.put("rleft",element.getChild("rleft").getValue());
            	map.put("rtop",element.getChild("rtop").getValue());
            	map.put("rwidth",element.getChild("rwidth").getValue());
            	map.put("rheight",element.getChild("rheight").getValue());
            	map.put("fontsize",element.getChild("fontsize").getValue());
            	map.put("fontname",element.getChild("fontname").getValue());
            	map.put("fonteffect",element.getChild("fonteffect").getValue());
            	map.put("flag",element.getChild("flag").getValue());
            	map.put("extendattr",element.getChild("extendattr").getValue());
            	//map.put("content",element.getChild("content").getValue());
            	list.add(map);
            }
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
		return list;
	}
	/**
	 * @param archive_id 
     * 
     * @Title: readtableVo
     * @Description: 得到业务模板信息
     * @param tabid  模版号
     * @return RecordVo 存放业务模版的vo对象
     * @throws
     */
    public RecordVo getArchiveTableVo(int tabid,String archive_id) {
    	RecordVo tab_vo=new RecordVo("Template_table");
		try {
			ArrayList list = getTemplateArchiveTable(tabid,archive_id);
			HashMap map = (HashMap) list.get(0);
			tab_vo.setInt("tabid",Integer.parseInt((String)map.get("tabid")));
			tab_vo.setString("name",(String) map.get("name"));
			tab_vo.setString("noticeid",(String) map.get("noticeid"));
			tab_vo.setString("gzstandid",(String) map.get("gzstandid"));
			tab_vo.setInt("flag",Integer.parseInt((String)map.get("flag")));
			//xus 20/5/14 【60416 】VFS+UTF-8+达梦：人事异动，归档以前的历史数据，再点击浏览打印或导出Excel时，提示：the attribute of Model is not exist!->static
			if(Sql_switcher.searchDbServerFlag() == Constant.DAMENG) {
				tab_vo.setInt("static_o",Integer.parseInt((String)map.get("static")));
			}else {
				tab_vo.setInt("static",Integer.parseInt((String)map.get("static")));
			}
			tab_vo.setString("operationcode",(String) map.get("operationcode"));
			tab_vo.setString("operationname",(String) map.get("operationname")); 
			tab_vo.setString("factor",(String) map.get("factor"));
			tab_vo.setString("lexpr",(String) map.get("lexpr"));
			tab_vo.setString("llexpr",(String) map.get("llexpr")); 
			tab_vo.setString("userfalg",(String) map.get("userfalg"));
			tab_vo.setString("username",(String) map.get("username"));
			tab_vo.setString("sp_flag",(String) map.get("sp_flag"));
			tab_vo.setString("dest_base",(String) map.get("dest_base"));
			tab_vo.setString("content",(String) map.get("content"));
			tab_vo.setString("ctrl_para",(String) map.get("ctrl_para")); 
			tab_vo.setInt("paperori",Integer.parseInt((String)map.get("paperori")));
			tab_vo.setInt("paper",Integer.parseInt((String)map.get("paperori")));
			tab_vo.setDouble("tmargin",Double.parseDouble((String)map.get("tmargin")));
			tab_vo.setDouble("bmargin",Double.parseDouble((String)map.get("bmargin")));
			tab_vo.setDouble("rmargin",Double.parseDouble((String)map.get("rmargin")));
			tab_vo.setDouble("lmargin",Double.parseDouble((String)map.get("lmargin")));
			tab_vo.setDouble("paperw",Double.parseDouble((String)map.get("paperw")));
			tab_vo.setDouble("paperh",Double.parseDouble((String)map.get("paperh")));
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
		return tab_vo;
    }

    public ArrayList getTemplateArchiveTable(int tabid, String archive_id) {
    	ArrayList list = new ArrayList();
		Document doc = null;
		RowSet rowSet = null;
		try
		{ 
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "select archived_data from t_cells_archive where tabid=? and id=?";
			ArrayList sqlList = new ArrayList();
			sqlList.add(tabid);
			sqlList.add(archive_id);
			rowSet = dao.search(sql, sqlList);
			String archived_data = "";
			if (rowSet.next()) {
				archived_data = rowSet.getString("archived_data");
			}
            doc=PubFunc.generateDom(archived_data); 
            String xpath="/data/template_table";
            XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
            List childlist=findPath.selectNodes(doc); 
            for(int i=0;i<childlist.size();i++) {
            	Element element = (Element) childlist.get(i);
            	HashMap map = new HashMap(); 
            	map.put("tabid",element.getChild("tabid").getValue());
            	map.put("name",element.getChild("name").getValue());
            	map.put("noticeid",element.getChild("noticeid").getValue());
            	map.put("gzstandid",element.getChild("gzstandid").getValue());
            	map.put("flag",element.getChild("flag").getValue());
            	map.put("static",element.getChild("static").getValue());
            	map.put("operationcode",element.getChild("operationcode").getValue());
            	map.put("operationname",element.getChild("operationname").getValue());
            	map.put("operationtype",element.getChild("operationtype").getValue());
            	map.put("factor",element.getChild("factor").getValue());
            	map.put("lexpr",element.getChild("lexpr").getValue());
            	map.put("llexpr",element.getChild("llexpr").getValue());
            	map.put("userfalg",element.getChild("userfalg").getValue());
            	map.put("username",element.getChild("username").getValue());
            	map.put("sp_flag",element.getChild("sp_flag").getValue());
            	map.put("dest_base",element.getChild("dest_base").getValue());
            	//map.put("content",element.getChild("content").getValue());
            	map.put("ctrl_para",element.getChild("ctrl_para").getValue());
            	map.put("paperori",element.getChild("paperori").getValue());
            	map.put("paper",element.getChild("paper").getValue());
            	map.put("tmargin",element.getChild("tmargin").getValue());
            	map.put("bmargin",element.getChild("bmargin").getValue());
            	map.put("rmargin",element.getChild("rmargin").getValue());
            	map.put("lmargin",element.getChild("lmargin").getValue());
            	map.put("paperw",element.getChild("paperw").getValue());
            	map.put("paperh",element.getChild("paperh").getValue());
            	list.add(map);
            }
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
		return list;
	}

    /**
     * 得到归档模板的相关属性
     * @param tabid
     * @param archiveid
     * @param pageid =-1全部页
     * @return
     */
	public ArrayList getArchiveCell(int tabid, String archiveid,int pageid) {
		ArrayList new_setbo=new ArrayList();
		RowSet rset = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "select archived_data from t_cells_archive where tabid=? and id=?";
			ArrayList sqlList = new ArrayList();
			sqlList.add(tabid);
			sqlList.add(archiveid);
			ArrayList setBoList = new ArrayList();
			rset = dao.search(sql, sqlList);
			if (rset.next()) {
				String archived_data = rset.getString("archived_data");
				setBoList = getTemplateArchiveSet(archived_data,pageid);
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
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new_setbo;
	}

	public ArrayList getTemplateArchiveSet(String archived_data, int pageid) {
		ArrayList setBoList = new ArrayList();
		Document doc = null;
		try
		{
            doc=PubFunc.generateDom(archived_data);
            String xpath="/data/template_set/record";
            XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
            List childlist=findPath.selectNodes(doc); 
            for(int i=0;i<childlist.size();i++) {
            	Element element = (Element) childlist.get(i);
            	TemplateSet setBo = new TemplateSet(conn); 
            	int page_id = Integer.parseInt(element.getChild("pageid").getValue());
            	if(pageid!=-1&&pageid!=page_id) {
            		continue;
            	}
            	setBo.setTabId(Integer.parseInt(element.getChild("tabid").getValue()));
            	setBo.setPageId(page_id);
    			setBo.setHz(nullToSpace(element.getChild("hz").getValue()));// 设置表格的汉字描述
    			setBo.setSetname(nullToSpace(element.getChild("setname").getValue()));// 设置子集的代码
    			setBo.setCodeid(nullToSpace(element.getChild("codeid").getValue()));// 相关的代码类
    			setBo.setField_hz(nullToSpace(element.getChild("field_hz").getValue()));// 字段的汉字描述 取自业务字典
    			setBo.setField_name(nullToSpace(element.getChild("field_name").getValue()));// 指标的代码
    			String flag = element.getChild("flag").getValue() == null ? "" : element.getChild("flag").getValue();// 数据源的标识（文本描述、照片......）
    			setBo.setFlag(flag);// 设置数据源的标识
    			String temp = element.getChild("subflag").getValue();// 子表控制符 0：字段 1：子集
    			if (temp == null || "".equals(temp) || "0".equals(temp))
    				setBo.setSubflag(false);
    			else{
    			    setBo.setSubflag(true);
    			}
    			setBo.setField_type(nullToSpace(element.getChild("field_type").getValue()));
    			setBo.setOld_fieldType(nullToSpace(element.getChild("field_type").getValue()));
    			setBo.setFormula(nullToSpace(element.getChild("formula").getValue()));// 设置字段的计算公式
    			setBo.setAlign(Integer.parseInt(element.getChild("align").getValue()));// 文字在单元格中的排列方式
    			setBo.setDisformat(Integer.parseInt(element.getChild("disformat").getValue()));// 设置数据的格式

    			if ("V".equalsIgnoreCase(flag)) {// 变量
    				/*RecordVo vo = (RecordVo) var_hm.get(rset
    						.getString("Field_name"));
    				if (vo != null) {
    					setBo.setDisformat(vo.getInt("flddec"));// 如果是临时变量
    															// 么要根据临时变量表里面的小数位数来设置
    					setBo.setVarVo(vo);
    				}*/
    			}
    			setBo.setChgstate(Integer.parseInt(element.getChild("chgstate").getValue()));// 设置字段是变化前还是变化后
    			setBo.setFonteffect(Integer.parseInt(element.getChild("fonteffect").getValue()));// 设置字体效果
    			setBo.setFontname(element.getChild("fontname").getValue());// 设置字体名称
    			setBo.setFontsize(Integer.parseInt(element.getChild("fontsize").getValue()));// 设置字体大小
    			setBo.setHismode(Integer.parseInt(element.getChild("hismode").getValue()));// 设置历史定位方式
    			//setBo.setMode(Integer.parseInt(element.getChild("mode").getValue()));// 多条记录的时候 那几种选择
    			// (最近..最初..)
    			setBo.setNsort(Integer.parseInt(element.getChild("nsort").getValue()));// 相同指示顺序号
    			setBo.setGridno(Integer.parseInt(element.getChild("gridno").getValue()));// 单元格号
    			setBo.setRcount(Integer.parseInt(element.getChild("rcount").getValue()));// 记录数 和HisMode
    			// 配合试用（标识最近（Rcount条））
    			setBo.setRheight(Integer.parseInt(element.getChild("rheight").getValue()));// 设置单元格高度
    			setBo.setRleft(Integer.parseInt(element.getChild("rleft").getValue()));// 单元格左边的坐标值
    			setBo.setRwidth(Integer.parseInt(element.getChild("rwidth").getValue()));// 单元格的宽度
    			setBo.setRtop(Integer.parseInt(element.getChild("rtop").getValue()));// 单元格上边坐标值
    			setBo.setL(Integer.parseInt(element.getChild("l").getValue()));
    			//** LBRT 代表着表格左下右上是否有线 **//
    			setBo.setB(Integer.parseInt(element.getChild("b").getValue()));
    			setBo.setR(Integer.parseInt(element.getChild("r").getValue()));
    			setBo.setT(Integer.parseInt(element.getChild("t").getValue()));

    			if (Integer.parseInt(element.getChild("yneed").getValue()) == 0)
    				setBo.setYneed(false);
    			else
    				setBo.setYneed(true);
    			String sub_domain = StringUtils.isEmpty(element.getChild("sub_domain").getValue())?null:element.getChild("sub_domain").getValue();
    			setBo.setXml_param(sub_domain);
    	
    			if (element.getChild("nhide").getValue() != null)
    				setBo.setNhide(Integer.parseInt(element.getChild("nhide").getValue()));
    			else
    				setBo.setNhide(0);// 打印还是隐藏 0：打印 1：隐藏
    			
    			if (setBo.isNeedChangeFieldType()) {
    				setBo.setField_type("M");
    			}
    			if (setBo.isSubflag()){
    			    setBo.setField_type("M");
    			    String hz = nullToSpace(element.getChild("hz").getValue());
    			    if(StringUtils.isNotEmpty(hz)) {
    			    	hz = hz.substring(1,hz.length()-2);
    			    }
    			    setBo.setField_hz(hz);
    			}
    			setBoList.add(setBo);
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return setBoList;
	}
	
}
