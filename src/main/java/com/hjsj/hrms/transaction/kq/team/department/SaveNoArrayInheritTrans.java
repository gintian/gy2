package com.hjsj.hrms.transaction.kq.team.department;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.BaseClassShift;
import com.hjsj.hrms.businessobject.kq.team.KqClassArrayConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 给没有排班的人员排班,继承部门班组排班
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *@author wangyao
 *@version 4.0
 */
public class SaveNoArrayInheritTrans extends IBusiness implements KqClassArrayConstant{

    public void execute() throws GeneralException {
        try {
            ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");  //人员的相关信息
            
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String start_date=(String)hm.get("startDate");  //开始时间
            String end_date=(String)hm.get("endDate");  //结束时间
            start_date = start_date.replaceAll("-", "\\.");
            end_date = end_date.replaceAll("-", "\\.");
            
            AnnualApply annualApply = new AnnualApply(userView, frameconn);
            boolean iscorrect = annualApply.getGroupDailyDataState(selectedinfolist, start_date, end_date);
            if (!iscorrect) 
                throw new GeneralException("请求的业务日期包含的日明细数据已经提交，不可再编辑，不能做申请操作，请与考勤管理员联系！");
            
            BaseClassShift baseClassShift=new BaseClassShift(this.userView,this.getFrameconn());
            ArrayList date_list=baseClassShift.getDatelist(start_date,end_date);  //时间list
            for(int i=0;i<selectedinfolist.size();i++)
            {
                LazyDynaBean bean=(LazyDynaBean)selectedinfolist.get(i);
                saveInheritCalss(bean,start_date,end_date,date_list);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    private void saveInheritCalss(LazyDynaBean bean,String start_date,String end_date,ArrayList date_list)
    {
        try
        {
            String a0100=(String)bean.get("a0100");
            String nbase=(String)bean.get("nbase");
            String e01a1=(String)bean.get("e01a1"); //职位
            String e0122=(String)bean.get("e0122"); //部门
            String b0110=(String)bean.get("b0110"); //单位
            String a0101=(String)bean.get("a0101");//姓名
            String date_Table=creat_KqTmp_Table(this.userView.getUserId()); //建立第一个临时表
            initializtion_date_Table(date_list,date_Table,nbase,e01a1,e0122,b0110,a0100,a0101);
            getbmtable(a0100,nbase,e01a1,e0122,b0110,a0101,date_Table);
            dropTable(date_Table);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * 建立时间临时表
     * @param userid
     * @return
     * @throws GeneralException
     */
    private String creat_KqTmp_Table(String userid)throws GeneralException
    {
        String tablename=getTmpTableName(this.userView.getUserName(),RegisterInitInfoData.getKqPrivCode(userView));
        DbWizard dbWizard =new DbWizard(this.getFrameconn());
        Table table=new Table(tablename);
        if(dbWizard.isExistTable(tablename,false))
        {
            dropTable(tablename);
        }
        Field temp = new Field("nbase","人员库");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);         
        temp.setVisible(false);
        table.addField(temp);
        Field temp1 = new Field("a0100","人员编号");
        temp1.setDatatype(DataType.STRING);
        temp1.setLength(50);
        temp1.setKeyable(false);            
        temp1.setVisible(false);
        table.addField(temp1);
        Field temp2=new Field("sDate","考勤日期");
        temp2.setDatatype(DataType.STRING);
        temp2.setLength(20);
        temp2.setKeyable(false);            
        temp2.setVisible(false);
        table.addField(temp2);
        Field temp3=new Field("b0110","单位");
        temp3.setDatatype(DataType.STRING);
        temp3.setLength(20);
        temp3.setKeyable(false);            
        temp3.setVisible(false);
        table.addField(temp3);
        Field temp4=new Field("e0122","部门");
        temp4.setDatatype(DataType.STRING);
        temp4.setLength(20);
        temp4.setKeyable(false);            
        temp4.setVisible(false);
        table.addField(temp4);
        Field temp5=new Field("e01a1","职位");
        temp5.setDatatype(DataType.STRING);
        temp5.setLength(20);
        temp5.setKeyable(false);            
        temp5.setVisible(false);
        table.addField(temp5);
        Field temp6=new Field("a0101","姓名");
        temp6.setDatatype(DataType.STRING);
        temp6.setLength(20);
        temp6.setKeyable(false);            
        temp6.setVisible(false);
        table.addField(temp6);
        try
        {
            dbWizard.createTable(table);
        }catch(Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }   
        
    
    return tablename;
    }
     /**
     * 新建临时表的名字
     * **/
    private String getTmpTableName(String UserId,String PrivCode) 
    {
        return "T#kq_shift_" + UserId;
    }
    /**
     * 删除临时表
     * @param tablename
     */
    private void dropTable(String tablename)
    {
        DbWizard dbWizard = new DbWizard(this.getFrameconn());
        dbWizard.dropTable(tablename);
    }
    /**
     * 生成初始时间表 初始数据
     * @param date_list
     * @param date_Table
     * @param nbase
     * @param e01a1
     * @param e0122
     * @param b0110
     * @param a0100
     * @return
     * @throws GeneralException
     */
    private void initializtion_date_Table(ArrayList date_list,String date_Table,String nbase,String e01a1,String e0122,String b0110,String a0100,String a0101) throws GeneralException
    {
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try
        {
            ArrayList deletelist= new ArrayList();
            String deleteSQL = "truncate table " + date_Table;      
            dao.delete(deleteSQL,deletelist);
            
            ArrayList insertList = new ArrayList();
            for(int i=0;i<date_list.size();i++)
            {
                String cur_date = date_list.get(i).toString();
                ArrayList  list = new ArrayList();
                list.add(nbase);
                list.add(a0100);
                list.add(cur_date);
                list.add(b0110);
                list.add(e0122);
                list.add(e01a1);
                list.add(a0101);
                insertList.add(list);
            }
            
            String insertSQL="insert into "+date_Table+" (nbase,a0100,sDate,b0110,e0122,e01a1,a0101) values (?,?,?,?,?,?,?)";
            dao.batchInsert(insertSQL,insertList);
        }catch(Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    /**
     * @author szk
     * 仅供b0100时 getbmtable调用
     * @param date_Table
     * @param b0110
     * @return
     */
    private void searchb0110(String date_Table,String b0110)
    {
        if (StringUtils.isEmpty(b0110)) 
            return;
        
        String codeitemid="";
        String parentid=b0110;
        ArrayList params = new ArrayList();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        RowSet rs = null;
        try
        {
            do
            {
                String parent="select codeitemid,parentid from organization where codesetid='UN' and codeitemid=?";
                params.clear();
                params.add(parentid);
                rs=dao.search(parent, params);
                if(rs.next())
                {
                    codeitemid=rs.getString("codeitemid");
                    parentid=rs.getString("parentid");
                    searchclass(date_Table, parentid, "UN");
                } else {
                    //没有找到上级单位，退出循环
                    break;
                }
            }
            while (!codeitemid.equals(parentid));
        }catch(Exception e)
        {
            e.printStackTrace();
        }finally
        {
            KqUtilsClass.closeDBResource(rs);
        }
    }
    /**
     * @author szk
     * 仅供getbmtable调用
     * @param date_Table
     * @param org_dept_id
     * @param codesetid
     * @return
     */
    private boolean searchclass(String date_Table,String org_dept_id,String codesetid)
    {
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        RowSet rs = null;
        boolean flag=true;
        try
        {
            String tt1="";
            String sqle01a1="select max(codesetid) as tt from kq_org_dept_shift td where td.q03z0 in (select sDate from "+date_Table+") and td.org_dept_id='"+org_dept_id+"' and td.codesetid='"+codesetid+"'";
            rs=dao.search(sqle01a1);
            while(rs.next())
            {
                tt1=rs.getString("tt");
            }
            if(tt1!=null)
            {
                ArrayList list=new ArrayList();
                //29828 linbz 在继承班组的班次之前先删除存在排班表里的null班次，若已有班次不被继承
                StringBuffer  deleteSQL = new StringBuffer();
                deleteSQL.append("delete from kq_employ_shift ");
                deleteSQL.append(" WHERE EXISTS ( ");
                deleteSQL.append(" select 1 from "+date_Table+" a ");
                deleteSQL.append(" where kq_employ_shift.nbase=a.nbase ");
                deleteSQL.append(" and kq_employ_shift.a0100=a.a0100 ");
                deleteSQL.append(" and kq_employ_shift.q03z0=a.sdate ");
                deleteSQL.append(" ) ");
                deleteSQL.append(" and kq_employ_shift.class_id is null ");
                dao.delete(deleteSQL.toString(), list);
                
                StringBuffer  insertSQL = new StringBuffer();
                insertSQL.append("INSERT INTO kq_employ_shift(nbase,A0100,A0101,B0110,E0122,E01A1,Q03Z0,class_id,status)");
                insertSQL.append(" SELECT nbase,A0100,A0101,B0110,E0122,E01A1,sDate,class_id,0 as status FROM (");
                insertSQL.append("SELECT a.nbase,a.A0100,a.a0101,a.b0110,a.e0122,a.e01a1,a.sDate,b.class_id,0 as status ");
                insertSQL.append(" FROM "+date_Table+" a,kq_org_dept_shift b");
                insertSQL.append(" WHERE b.q03z0=a.sDate and b.org_dept_id='"+org_dept_id+"' and b.codesetid='"+codesetid+"'");
                insertSQL.append(") c WHERE NOT EXISTS (");
                insertSQL.append("select 1 from kq_employ_shift where c.nbase = kq_employ_shift.nbase and c.a0100 = kq_employ_shift.a0100 and c.sdate = kq_employ_shift.q03z0");
                insertSQL.append(")");
                dao.insert(insertSQL.toString(),list);
                flag=false;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }finally
        {
            KqUtilsClass.closeDBResource(rs);
            
        }
        return flag;
        
    }
    /**
     * 判断 按照班组->职位->部门->单位的优先级顺序为人员生成排班
     * @param a0100
     * @param nbase
     * @param e01a1
     * @param e0122
     * @param b0110
     * @param a0101
     * @return
     * s
     */
    private void getbmtable(String a0100,String nbase,String e01a1,String e0122,String b0110,String a0101,String date_Table)
    {
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        String sql1="select group_id from kq_group_emp where a0100='"+a0100+"' and nbase='"+nbase+"'";
        RowSet rs = null;
        String group_id="";
        try
        {
            rs=dao.search(sql1);
            while(rs.next())
            {
                group_id=rs.getString("group_id");
            }
            if((!"".equals(group_id)||group_id.length()>0) && searchclass(date_Table, group_id, "@G"))
            {
                if(e01a1.length()>0 && searchclass(date_Table, e01a1, "@K"))
                {
                    if(e0122.length()>0 && searchclass(date_Table, e0122, "UM"))
                    {
                        if(b0110.length()>0 && searchclass(date_Table, b0110, "UN"))
                        {
                            searchb0110(date_Table, b0110);
                        }
                    }
                }else if(e0122.length()>0 && searchclass(date_Table, e0122, "UM"))
                {
                    if(b0110.length()>0 && searchclass(date_Table, b0110, "UN"))
                    {
                        searchb0110(date_Table, b0110);
                    }
                }else if(b0110.length()>0 && searchclass(date_Table, b0110, "UN"))
                {
                    searchb0110(date_Table, b0110);
                }
            }else
            {
                if(e01a1.length()>0 && searchclass(date_Table, e01a1, "@K"))
                {
                    if(e0122.length()>0 && searchclass(date_Table, e0122, "UM"))
                    {
                        if(b0110.length()>0 && searchclass(date_Table, b0110, "UN"))
                        {
                            searchb0110(date_Table, b0110);
                        }
                    }
                    
                }else if(e0122.length()>0 && searchclass(date_Table, e0122, "UM"))
                {
                    if(b0110.length()>0 && searchclass(date_Table, b0110, "UN"))
                    {
                        searchb0110(date_Table, b0110);
                    }
                }else if(b0110.length()>0 && searchclass(date_Table, b0110, "UN"))
                {
                    searchb0110(date_Table, b0110);
                }
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }finally
        {
            KqUtilsClass.closeDBResource(rs);
        }
    }
    
}
