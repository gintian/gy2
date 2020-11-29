package com.hjsj.hrms.transaction.gz.voucher;

import com.hjsj.hrms.businessobject.gz.GzVoucherSendBo;
import com.hjsj.hrms.businessobject.gz.voucher.VoucherBo;
import com.hjsj.hrms.businessobject.gz.voucher.VoucherJounalBo;
import com.hjsj.hrms.interfaces.gz.Financial_voucherXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/**
 * 
* 
* 类名称：NewAndUpdateVoucherTrans   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 16, 2013 5:45:17 PM   
* 修改人：zhaoxg   
* 修改时间：Aug 16, 2013 5:45:17 PM   
* 修改备注：   新增修改凭证
* @version    
*
 */
public class NewAndUpdateVoucherTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao = new ContentDAO(this.frameconn);
			GzVoucherSendBo gzSendBo = new GzVoucherSendBo(this.frameconn, userView);//xiegh add 查询双币凭证状态
			DbWizard dbw = new DbWizard(this.frameconn);
			String flag = (String) this.getFormHM().get("flag");
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String flagtemp = "";
			VoucherJounalBo vjb = new VoucherJounalBo(this.frameconn);
			if(map!=null){
				flagtemp = (String) map.get("flagtemp");
			}
			String b0110 = (String) this.getFormHM().get("b0110");
			/*凭证表新增字段：账簿编号：c_code xiegh add 20170531*/
			String c_code = (String) this.getFormHM().get("c_code");//xiegh 20170527 财务账簿 
			String rate = (String) this.getFormHM().get("ratevalue");//汇率指标
			rate = rate==null?"":rate;
			String is_dual_money = (String)this.getFormHM().get("is_dual_money");
			c_code=c_code==null?"":c_code;
			ArrayList b0110List = this.getB0110List();
			ArrayList ratelist = new ArrayList();
			if("new".equals(flag)){
				
				String jiekou = (String) this.getFormHM().get("jiekou");
				String name = (String) this.getFormHM().get("name");
				String leibie = (String) this.getFormHM().get("leibie");
				String salarySetArray = (String) this.getFormHM().get("salarySetArray");
				String huizongItem = (String) this.getFormHM().get("huizongItem");
				huizongItem= huizongItem!=null ? huizongItem : "";
				String voucherItem = (String) this.getFormHM().get("voucherItem");
				 voucherItem= voucherItem!=null ? voucherItem : "";
				if("1".equals(jiekou)){
				    if("".equals(voucherItem)){
				        voucherItem="PZ_ID,VOUCHER_DATE,DBILL_DATE,DBILL_TIMES,C_TYPE,DEPTCODE,C_MARK,C_SUBJECT,FL_NAME,N_LOAN,MONEY,CHECK_ITEM,CHECK_ITEM_VALUE";
				    }
				}
				if("2".equals(jiekou)){
				    voucherItem="FL_ID,C_MARK,C_SUBJECT,FL_NAME,C_ITEMSQL,C_WHERE";//按月汇总 只添加 分录ID 摘要 科目 分录名称 计算公式  限制条件 
				}
				String dbid = (String) this.getFormHM().get("dbid");
				String web1 = (String) this.getFormHM().get("web1");
				String web2 = (String) this.getFormHM().get("web2");
				if(!dbw.isExistField("GZ_Warrant", "b0110", false)){
					Table table=new Table("GZ_Warrant");
					Field field=new Field("b0110","b0110");
					field.setDatatype(DataType.STRING);
					field.setLength(50);
					table.addField(field);	
					dbw.addColumns(table);
					DBMetaModel dbmodel=new DBMetaModel(this.frameconn);
					dbmodel.reloadTableModel("GZ_Warrant");	
				}
				int id = getMaxid(dao);
				String sql = "insert into GZ_Warrant (pn_id) values ("+id+")";
				dao.insert(sql, new ArrayList());
				VoucherBo xmlbo = new VoucherBo(this.frameconn,"","voucher",id+"");				
				xmlbo.setAttributeValue("/voucher/webservice", "url", web1);
				xmlbo.setAttributeValue("/voucher/webservice", "method", web2);
				xmlbo.setAttributeValue("/voucher/items", "fields", voucherItem);
				xmlbo.setTextValue("/voucher/exchg_rate_fld", rate);
				xmlbo.setTextValue("/voucher/is_dual_money", is_dual_money);
				String xml = xmlbo.saveStrValue();
				RecordVo recordvo=new RecordVo("GZ_Warrant");
				recordvo.setInt("pn_id",id);
				recordvo=dao.findByPrimaryKey(recordvo);
				recordvo.setString("c_type", leibie);
				recordvo.setString("c_name", name);
				recordvo.setString("c_dbase", dbid);
				if(!"".equals(c_code))
				recordvo.setString("c_code", c_code);
				recordvo.setString("c_scope", salarySetArray);
				recordvo.setString("interface_type", jiekou);
				recordvo.setString("collect_fields", huizongItem);
				recordvo.setString("content", xml);
				recordvo.setString("b0110", b0110);
				dao.updateValueObject(recordvo);
				this.getFormHM().put("pn_id", id+"");
				this.getFormHM().put("c_name", name);
				this.getFormHM().put("interface_type", jiekou);
				
			}else if("update".equals(flag)){
					
				String pnid = (String) this.getFormHM().get("pnid");
				VoucherBo xmlbo = new VoucherBo(this.frameconn,"","voucher",pnid);
				VoucherBo bo = new VoucherBo(this.frameconn,this.userView);
				String sql = "select *  from GZ_Warrant where pn_id="+pnid+"";
				RowSet rs = dao.search(sql);
				String pn_id = "";
				String c_type = "";
				String c_name = "";
				String c_dbase ="";
				String c_scope = "";//统计范围薪资类别
				String interface_type = "";
				String collect_fields = "";//汇总指标
				String voucherItem = "";
				String web1 = "";
				String web2 = "";
				String privflag = "2";
				String content ="";
				while(rs.next()){
					pn_id=rs.getString("pn_id");
					c_type=rs.getString("c_type");
					c_name=rs.getString("c_name");
					c_code=rs.getString("c_code");
					c_dbase=rs.getString("c_dbase");
					c_scope=rs.getString("c_scope");
					content=rs.getString("content");
					interface_type=rs.getString("interface_type");
					collect_fields=rs.getString("collect_fields");
					b0110 = rs.getString("b0110");
		          	privflag = Financial_voucherXml.IsHavePriv(this.userView,rs.getString("b0110"));//1：没关系 2：包含（上级） 3：下级
				}
				voucherItem = xmlbo.getXmlValue();
				web1 = xmlbo.getXmlValue1("url");
				web2 = xmlbo.getXmlValue1("method");
				String sql1 = "select C_GROUP from GZ_WARRANTLIST where PN_ID='"+pnid+"'";
				RowSet rs1 = dao.search(sql1);
				StringBuffer no = new StringBuffer();
				while(rs1.next()){
					if(rs1.getString("C_GROUP")!=null&&!"".equals(rs1.getString("C_GROUP"))){
						no.append(rs1.getString("C_GROUP"));
						no.append(",");
					}

				}
				StringBuffer zxgflag = new StringBuffer();
				if(voucherItem!=null&&!"".equals(voucherItem)&&!"null".equals(voucherItem)){
                    zxgflag.append(voucherItem);
                    zxgflag.append(",");
                }
				if(collect_fields!=null&&!"".equals(collect_fields)&&!"null".equals(collect_fields)){
					zxgflag.append(collect_fields);
					zxgflag.append(",");
				}
				if(no!=null&&!"".equals(no)&&!"null".equals(no)){
					zxgflag.append(no);
					zxgflag.append(",");
				}
				//HashSet set = new HashSet();//为什么要用set?导致顺序不对的原因所在
				ArrayList setList = new ArrayList();
				String[] _zxgflag = zxgflag.toString().split(",");
				for(int i=0;i<_zxgflag.length;i++){
				    if(setList.contains(_zxgflag[i].toUpperCase())){
				        continue;
				    }
					setList.add(_zxgflag[i]);
				}
				zxgflag = new StringBuffer();
				Iterator iterator=setList.iterator();
				while(iterator.hasNext()){
					zxgflag.append(iterator.next());
					zxgflag.append(",");
				}
				ArrayList voucherList = bo.getRightItemList(zxgflag.toString());
				String huizongList = "";
				if(collect_fields!=null&&!"".equals(collect_fields)&&!"null".equals(collect_fields)){
					huizongList = bo.getRightList1(collect_fields);
				}
				ArrayList salarySelectedList = vjb.getList(c_scope);
				ArrayList dbSelectedList = vjb.getList(c_dbase);
				ArrayList list = bo.getVoucherItem();
				ArrayList dbList =bo.getDbList();
				String[] array = {};
				if("1".equals(interface_type)){//如果是财务类型凭证
					 array = c_scope.split(",");
					 String whereSql ="itemtype ='N'";
					 ratelist= bo.getRateList(array,whereSql);
					 String isDualMoney = gzSendBo.getDomValue(content, "is_dual_money");
					 CommonData data = checkstatus(content,ratelist);
					 this.getFormHM().put("rateList",ratelist);
					 this.getFormHM().put("isDualMoney",isDualMoney);
					 this.getFormHM().put("rate",data);//回传历史汇率指标
				}
				this.getFormHM().put("dbList",dbList);
				this.getFormHM().put("pn_id", pn_id);
				this.getFormHM().put("c_type", c_type);
				this.getFormHM().put("c_name", c_name);
				this.getFormHM().put("c_code", c_code);
				this.getFormHM().put("c_dbase", c_dbase);
				this.getFormHM().put("c_scope", c_scope);
				this.getFormHM().put("interface_type", interface_type);
				this.getFormHM().put("web1", web1);
				this.getFormHM().put("web2", web2);
				this.getFormHM().put("voucherList", voucherList);
				this.getFormHM().put("huizongList", huizongList);
				this.getFormHM().put("voucherItem", zxgflag.toString());
				this.getFormHM().put("collect_fields", collect_fields);
				this.getFormHM().put("salarySelectedList", salarySelectedList);
				this.getFormHM().put("dbSelectedList", dbSelectedList);
				this.getFormHM().put("list",list);
				boolean isHave = false;
				if("2".equals(privflag)){
					for(int i=0;i<b0110List.size();i++){
						CommonData obj = (CommonData)b0110List.get(i);
						String b = obj.getDataValue();
						if(b.equalsIgnoreCase(b0110)){
							isHave = true;
						}
					}
				}
				if(!isHave&&b0110!=null){
					b0110List.add(new CommonData(b0110,AdminCode.getCodeName(b0110.substring(0, 2), b0110.substring(2))));
				}
				CommonData obj = (CommonData)b0110List.get(0);
				String _b0110 = obj.getDataValue();
				this.getFormHM().put("b0110", b0110==null||b0110.length()==0?_b0110:b0110);
				this.getFormHM().put("privflag",privflag);
			}else if("updated".equals(flag)){
				String jiekou = (String) this.getFormHM().get("jiekou");
				String name = (String) this.getFormHM().get("name");
				String leibie = (String) this.getFormHM().get("leibie");
				String salarySetArray = (String) this.getFormHM().get("salarySetArray");
				String huizongItem = (String) this.getFormHM().get("huizongItem");
				String voucherItem = (String) this.getFormHM().get("voucherItem");
				if(huizongItem==null||"null".equalsIgnoreCase(huizongItem)){
				    huizongItem=" ";
				}
				if(voucherItem!=null){
				    String []voucherItemArray = voucherItem.split(",");
				    voucherItem="";
				    for(int i=0;i<voucherItemArray.length;i++){
				        if(voucherItemArray[i]==null||"".equals(voucherItemArray[i])||"null".equals(voucherItemArray[i])){
				            continue;
				        }
				     voucherItem=voucherItem+voucherItemArray[i]+",";
				    }
				    if(voucherItem.trim().length()>0)
				        voucherItem=voucherItem.substring(0, voucherItem.length()-1);
				}
				if("2".equals(jiekou)){
                    voucherItem="FL_ID,C_MARK,C_SUBJECT,FL_NAME,C_ITEMSQL,C_WHERE";//按月汇总 只添加 分录ID 摘要 科目 分录名称 计算公式  限制条件 
                }
				if(!dbw.isExistField("GZ_Warrant", "b0110", false)){
					Table table=new Table("GZ_Warrant");
					Field field=new Field("b0110","b0110");
					field.setDatatype(DataType.STRING);
					field.setLength(50);
					table.addField(field);	
					dbw.addColumns(table);
					DBMetaModel dbmodel=new DBMetaModel(this.frameconn);
					dbmodel.reloadTableModel("GZ_Warrant");	
				}
				
				String dbid = (String) this.getFormHM().get("dbid");
				String web1 = (String) this.getFormHM().get("web1");
				String web2 = (String) this.getFormHM().get("web2");
				String id = (String) this.getFormHM().get("pnid");
				VoucherBo xmlbo = new VoucherBo(this.frameconn,"","voucher",id);				
				xmlbo.setAttributeValue("/voucher/webservice", "url", web1);
				xmlbo.setAttributeValue("/voucher/webservice", "method", web2);
				xmlbo.setAttributeValue("/voucher/items", "fields", voucherItem);
				xmlbo.setTextValue("/voucher/exchg_rate_fld", rate);
				xmlbo.setTextValue("/voucher/is_dual_money", is_dual_money);
				String xml = xmlbo.saveStrValue();
				RecordVo recordvo=new RecordVo("GZ_Warrant");
				recordvo.setInt("pn_id",Integer.parseInt(id));
				recordvo=dao.findByPrimaryKey(recordvo);
				recordvo.setString("c_type", leibie);
				recordvo.setString("c_name", name);
				recordvo.setString("c_dbase", dbid);
				if(!"".equals(c_code))
					recordvo.setString("c_code", c_code);
				recordvo.setString("c_scope", salarySetArray);
				recordvo.setString("interface_type", jiekou);
				recordvo.setString("collect_fields", huizongItem);
				recordvo.setString("content", xml);
				recordvo.setString("b0110", b0110);
				dao.updateValueObject(recordvo);
				this.getFormHM().put("pn_id", id);
				this.getFormHM().put("c_name", name);
				this.getFormHM().put("interface_type", jiekou);
			}else if("reflsh".equals(flag)){
			    String pn_id = (String) this.getFormHM().get("pn_id");
			    
			    String interface_type = (String) this.getFormHM().get("interface_type");//对应的财务凭证类别 也就是interface_type
                String c_name = (String) this.getFormHM().get("c_name");
                String c_type = (String) this.getFormHM().get("c_type");
                String salarySetArray = (String) this.getFormHM().get("resalarySetArray");//已经被选中的薪资类别
                
                String huizongItem = (String) this.getFormHM().get("huizongItem");//huizongitem-->collect_fields-->huizongList;
                huizongItem= huizongItem!=null ? huizongItem : "";
                
                String voucherItem = (String) this.getFormHM().get("voucherItem");
                voucherItem= voucherItem!=null ? voucherItem : "";
                
                String dbid = (String) this.getFormHM().get("dbid");
                
                String webURL = (String) this.getFormHM().get("webURL");
                webURL= webURL!=null ? webURL : "";
                
                String webFunction = (String) this.getFormHM().get("webFunction");
                webFunction= webFunction!=null ? webFunction : "";
                
                VoucherBo bo = new VoucherBo(this.frameconn,this.userView);
                ArrayList dbList =bo.getDbList();
                ArrayList salarySetList =bo.getSalarySetList();
                String huizongList = "";
                if(huizongItem!=null&&!"".equals(huizongItem)&&!"null".equals(huizongItem)){
                    huizongList = bo.getRightList1(huizongItem);
                }
                StringBuffer zxgflag=new StringBuffer();
                StringBuffer no = new StringBuffer();
                    if(!("".equals(pn_id)||pn_id==null)){
                        String sql1 = "select C_GROUP from GZ_WARRANTLIST where PN_ID='"+pn_id+"'";
                        RowSet rs1 = dao.search(sql1);
                        
                        while(rs1.next()){
                            if(rs1.getString("C_GROUP")!=null&&!"".equals(rs1.getString("C_GROUP"))){
                                no.append(rs1.getString("C_GROUP"));
                                no.append(",");
                            }

                        } 
                    }
                    
                    if(voucherItem!=null&&!"".equals(voucherItem)){
                        zxgflag.append(voucherItem);
                        zxgflag.append(",");
                    }
                    if(huizongItem!=null&&!"".equals(huizongItem)){
                        zxgflag.append(huizongItem);
                        zxgflag.append(",");
                    }
                    if(no!=null&&!"".equals(no)){
                        zxgflag.append(no);
                        zxgflag.append(",");
                    }
                    //HashSet set = new HashSet();导致顺序不对的原因
                    ArrayList setList = new ArrayList();
                    String[] _zxgflag = zxgflag.toString().split(",");
                    for(int i=0;i<_zxgflag.length;i++){
                        setList.add(_zxgflag[i]);
                    }
                    zxgflag = new StringBuffer();
                    Iterator iterator=setList.iterator();
                    while(iterator.hasNext()){
                        zxgflag.append(iterator.next());
                        zxgflag.append(",");
                    }
                ArrayList voucherList = bo.getRightItemList(zxgflag.toString());
                ArrayList xiangmuList =bo.getXiangmuList();
                if(voucherList.size()==0){
                    voucherList=xiangmuList;
                }
                ArrayList list = bo.getVoucherItem();
                this.getFormHM().put("huizongList", huizongList);
                this.getFormHM().put("voucherItem", zxgflag.toString());
                this.getFormHM().put("voucherList", voucherList);
                this.getFormHM().put("collect_fields", huizongItem);
                this.getFormHM().put("pn_id", pn_id);
                this.getFormHM().put("c_type", c_type);
                this.getFormHM().put("c_name", c_name);
                this.getFormHM().put("c_dbase", dbid);
                this.getFormHM().put("interface_type", interface_type);
                this.getFormHM().put("web1", webURL);
                this.getFormHM().put("web2", webFunction);
                this.getFormHM().put("webURL", webURL);
                this.getFormHM().put("webFunction", webFunction);
                this.getFormHM().put("dbList",dbList);
                this.getFormHM().put("c_scope", salarySetArray);
                this.getFormHM().put("salarysetList",salarySetList);
                this.getFormHM().put("list",list);
                
				String pnid = (String) this.getFormHM().get("pnid");
				String sql = "select *  from GZ_Warrant where pn_id="+pnid+"";
				RowSet rs = dao.search(sql);
				String content ="";
				String privflag = "2";
				while(rs.next()){
					b0110 = rs.getString("b0110");
		          	privflag = Financial_voucherXml.IsHavePriv(this.userView,rs.getString("b0110"));//1：没关系 2：包含（上级） 3：下级
		          	content = rs.getString("content");
				}
				boolean isHave = false;
				if("2".equals(privflag)){
					for(int i=0;i<b0110List.size();i++){
						CommonData obj = (CommonData)b0110List.get(i);
						String b = obj.getDataValue();
						if(b.equalsIgnoreCase(b0110)){
							isHave = true;
						}
					}
				}
				if(!isHave&&b0110!=null){
					b0110List.add(new CommonData(b0110,AdminCode.getCodeName(b0110.substring(0, 2), b0110.substring(2))));
				}
				CommonData obj = (CommonData)b0110List.get(0);
				String _b0110 = obj.getDataValue();
				String[] array = {};
				if("1".equals(interface_type)){//如果是财务类型凭证
					 array = salarySetArray.split(",");
					 String whereSql ="itemtype ='N'";
					 ratelist= bo.getRateList(array,whereSql);
					 String isDualMoney = gzSendBo.getDomValue(content, "is_dual_money");
					 this.getFormHM().put("isDualMoney",isDualMoney);
					 CommonData data = checkstatus(content,ratelist);
					 this.getFormHM().put("rate",data);
				}
				this.getFormHM().put("b0110", b0110==null||b0110.length()==0?_b0110:b0110);
				this.getFormHM().put("privflag",privflag);
			}else if("getRatelist".equals(flag)){//手动点击薪资类别时做数据联动
				 VoucherBo bo = new VoucherBo(this.frameconn,this.userView);
				 String salarySetArray = (String) this.getFormHM().get("salarySetArray");
				 String[] array = salarySetArray.split(",");
				 String whereSql ="itemtype ='N'";
				 ratelist = bo.getRateList(array,whereSql);
				 String content ="";
				 String pnid = (String) this.getFormHM().get("pnid");
				 String sql="";
				 if(pnid!=null){
					  sql= "select *  from GZ_Warrant where pn_id="+pnid+"";
					 RowSet rs = dao.search(sql);
					 while(rs.next()){
						 content = rs.getString("content");
					 }
				}
				 CommonData data=null;
				 if(!"".equals(content)){
					 data = checkstatus(content,ratelist);
					 this.getFormHM().put("rate",data);
				 }else{
					 this.getFormHM().put("rate",new CommonData("none","none"));
				 }
			}else{
				VoucherBo bo = new VoucherBo(this.frameconn,this.userView);
				ArrayList salarySetList =bo.getSalarySetList();
				ArrayList dbList =bo.getDbList();

				ArrayList xiangmuList =bo.getXiangmuList();
				ArrayList list = bo.getVoucherItem();
				this.getFormHM().put("salarysetList",salarySetList);
				this.getFormHM().put("dbList",dbList);
				this.getFormHM().put("xiangmuList",xiangmuList);
				this.getFormHM().put("flagtemp",flagtemp);
				this.getFormHM().put("list",list);
			}
			this.getFormHM().put("b0110List", b0110List);
			this.getFormHM().put("rateList",ratelist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	private CommonData checkstatus(String content, ArrayList ratelist) {
		for(int i =0;i<ratelist.size();i++){
			CommonData data  = (CommonData)ratelist.get(i);
			if(content.contains(data.getDataValue()))
				return new CommonData(data.getDataValue(),data.getDataName());
		}
		return new CommonData("none","none");
	}
	public int getMaxid(ContentDAO dao){
		int id = 0;
		try
		{
			String sql = "select max(pn_id) as max from GZ_Warrant";
			RowSet rs = dao.search(sql);
			if(rs.next()){
			    if(rs.getString("max")!=null){
			        id = Integer.parseInt(rs.getString("max")) + 1;
			    }
				
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return id;
	}
	/**
	 * 获取归属单位信息
	 * 根据归属单位判断该凭证的归属  哈药需求
	 * zhaoxg add 2015-9-21
	 * @return
	 */
	public ArrayList getB0110List()
	{
		ArrayList list=new ArrayList();
		try
		{
			String b_units = this.userView.getUnitIdByBusi("1");
			if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId())|| "UN`".equals(b_units)){
				String sql = "select * from organization   where codesetid<>'@K'  and parentid=codeitemid order by a0000";
				ContentDAO dao = new ContentDAO(this.frameconn);
				this.frowset = dao.search(sql);
				while(this.frowset.next()){
					list.add(new CommonData(this.frowset.getString("codesetid")+this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc")));
				}
			}else{
				String unitarr[] =b_units.split("`");	
				LazyDynaBean abean=null;
				if(unitarr.length>0){
					for(int i=0;i<unitarr.length;i++)
					{
	    				String codeid=unitarr[i];
	    				if(codeid==null|| "".equals(codeid))
	    					continue;
		    			if(codeid!=null&&codeid.trim().length()>2){
							String privCode = codeid.substring(0,2);
							String privCodeValue = codeid.substring(2);	
							list.add(new CommonData(codeid,AdminCode.getCodeName(privCode, privCodeValue)));
		    			}
					}
				}
			}
			
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
}
