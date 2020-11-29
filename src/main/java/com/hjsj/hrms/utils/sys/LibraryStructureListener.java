package com.hjsj.hrms.utils.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.FieldItem;

import java.sql.Connection;
import java.util.Arrays;

/**
 * 监听库结构变化，并对业务数据进行同步
 * 使用新线程后台执行
 */
public class LibraryStructureListener extends Thread{

    int action;
    Object item;

    private LibraryStructureListener(int action,Object item){
        this.action = action;
        this.item = item;
    }

    private static int ACTION_DELETE = 1;
    private static int ACTION_UPDATE = 2;

    public static void deleteCode(CodeItem item){
        LibraryStructureListener.doSync(LibraryStructureListener.ACTION_DELETE,item);
    }

    public static void updateCode(CodeItem item){
        LibraryStructureListener.doSync(LibraryStructureListener.ACTION_UPDATE,item);
    }

    public static void deleteField(FieldItem item){
        LibraryStructureListener.doSync(LibraryStructureListener.ACTION_DELETE,item);
    }

    public static void updateField(FieldItem item){
        LibraryStructureListener.doSync(LibraryStructureListener.ACTION_UPDATE,item);
    }

    private static void doSync(int action, Object item){
        new LibraryStructureListener(action,item).start();
    }

    @Override
    public void run(){
        if(item instanceof FieldItem) {
            this.fieldItemAction();
        }else{
            this.codeItemAction();
        }
    }

    private void codeItemAction(){

        CodeItem code = (CodeItem)item;
        if(LibraryStructureListener.ACTION_UPDATE == this.action){
            if("27".equalsIgnoreCase(code.getCodeid())){
                Connection conn = null;
                try {
                    conn = AdminDb.getConnection();
                    String sql = " update kq_item set item_name=? where item_id = ? ";
                    ContentDAO dao = new ContentDAO(conn);
                    dao.update(sql, Arrays.asList(code.getCodename(),code.getCodeitem()));
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    PubFunc.closeResource(conn);
                }
            }

        }else if(LibraryStructureListener.ACTION_DELETE == this.action){
            if("27".equalsIgnoreCase(code.getCodeid())){
                Connection conn = null;
                try {
                    conn = AdminDb.getConnection();
                    String sql = " delete from kq_item where item_id like ? ";
                    ContentDAO dao = new ContentDAO(conn);
                    dao.delete(sql, Arrays.asList(code.getCodeitem()+"%"));
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    PubFunc.closeResource(conn);
                }
            }

        }
    }

    private void fieldItemAction(){
        FieldItem code = (FieldItem)item;
        if(LibraryStructureListener.ACTION_UPDATE == this.action){

        }else if(LibraryStructureListener.ACTION_DELETE == this.action){

        }
    }

}
