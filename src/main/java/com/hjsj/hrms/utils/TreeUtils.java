package com.hjsj.hrms.utils;


import org.apache.commons.lang.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 递归创建树结构公共类
 *
 * zhanghua 2018-06-20
 *
 * 此方法基于反射实现，可自行创建将树节点类 至少需要包含 id,父节点id ,子节点数组 三个属性
 * 例如
    class TestTree{
         private String id;
         private String name;
         private String pid;
     ｝
    TestTree testTree4 = new TestTree();
    testTree4.setId("1000");
    testTree4.setName("山东省");
    testTree4.setPid("0");

     TestTree testTree3 = new TestTree();
     testTree3.setId("1002001");
     testTree3.setName("青岛市");
     testTree3.setPid("1002");
     TreeUtils.createTree(testTreeList,testTree4,"id","pid","testTrees");
 *
 * 调用createTree 方法 createTree方法，根节点，即testTree4就是最全的最后的树结构。
 */
public class TreeUtils {

    /**
     *
     * 创建树
     * @param list               树数据
     * @param root               根节点
     * @param keyFieldName       关联属性
     * @param parentKeyFieldName 关联父属性
     * @param subFieldName       子节点数据
     * @param <T>                根节点
     */
    public static <T> void createTree(List<T> list, T root, String keyFieldName, String parentKeyFieldName, String subFieldName) {
        Field keyField = getField(keyFieldName, root);
        Field parentKeyField = getField(parentKeyFieldName, root);
        Field subField = getField(subFieldName, root);
        find(list, root, keyField, parentKeyField, subField);
    }
    /**
     * 创建带层级的树
     * @param list               树数据
     * @param root               根节点
     * @param keyFieldName       关联属性
     * @param parentKeyFieldName 关联父属性
     * @param subFieldName       子节点数据
     * @param <T>                根节点
     * @param levelFieldName     层级属性
     */
    public static <T> void createTree(List<T> list, T root, String keyFieldName, String parentKeyFieldName, String subFieldName,String levelFieldName) {
        Field keyField = getField(keyFieldName, root);
        Field parentKeyField = getField(parentKeyFieldName, root);
        Field subField = getField(subFieldName, root);
        Field levelField = getField(levelFieldName, root);
        find(list, root, keyField, parentKeyField, subField,levelField,0);
    }

    /**
     * 根据父节点的关联值 查找
     */
    public static <T, E> List<E> getKeys(List<T> list, T root, String keyFieldName, String parentKeyFieldName) {
        Field keyField = getField(keyFieldName, root);
        Field parentKeyField = getField(parentKeyFieldName, root);
        List<E> keys = new ArrayList<E>();
        E value = getValueByGetMethod(keyField, root);
        keys.add(value);
        findKeys(list, keys, root, keyField, parentKeyField);
        return keys;
    }

    private static <T> void find(List<T> list, T parent, Field keyField, Field parentKeyField, Field subField) {
        List<T> subs = getSubs(list, parent, keyField, parentKeyField);

        if (subs != null) {
            setValueByField(subField, parent, subs);
            for (T sub : subs) {
                //递归找子节点
                find(list, sub, keyField, parentKeyField, subField);
            }
        }
    }
    private static <T> void find(List<T> list, T parent, Field keyField, Field parentKeyField, Field subField,Field levelField,int levelNum) {
        List<T> subs = getSubs(list, parent, keyField, parentKeyField,levelField,levelNum+1);

        if (subs != null) {
            setValueByField(subField, parent, subs);
            setValueByField(levelField, parent, levelNum);
            for (T sub : subs) {
                //递归找子节点
                find(list, sub, keyField, parentKeyField, subField,levelField,levelNum+1);
            }
        }
    }

    private static <T, E> List<E> findKeys(List<T> list, List<E> keys, T parent, Field keyField, Field parentKeyField) {
        List<T> subs = getSubs(list, parent, keyField, parentKeyField);

        List<E> subKeys = getSubKeys(list, parent, keyField, parentKeyField);
        if (subs != null) {
            keys.addAll(subKeys);
            for (T sub : subs) {
                //递归找子节点
                findKeys(list, keys, sub, keyField, parentKeyField);
            }
        }
        return keys;
    }


    private static <T> List<T> getSubs(List<T> list, T parent, Field keyField, Field parentKeyField) {
        List<T> subs = null;
        for (T t : list) {
            Object keyFieldValue = getValueByField(keyField, parent);
            Object parentFieldValue = getValueByField(parentKeyField, t);
            if (keyFieldValue.equals(parentFieldValue)) {
                if (subs == null) {
                    subs = new ArrayList<T>();
                }
                subs.add(t);
            }
        }
        return subs;
    }
    private static <T> List<T> getSubs(List<T> list, T parent, Field keyField, Field parentKeyField,Field levelField,int levelNum) {
        List<T> subs = null;
        for (T t : list) {
            Object keyFieldValue = getValueByField(keyField, parent);
            Object parentFieldValue = getValueByField(parentKeyField, t);
            if (keyFieldValue.equals(parentFieldValue)) {
                if (subs == null) {
                    subs = new ArrayList<T>();
                }
                setValueByField(levelField, t, levelNum);
                subs.add(t);
            }
        }
        return subs;
    }


    private static <T, E> List<E> getSubKeys(List<T> list, T parent, Field keyField, Field parentKeyField) {
        List<E> subs = null;
        for (T t : list) {
            Object keyFieldValue = getValueByField(keyField, parent); //父节点key
            Object parentFieldValue = getValueByField(parentKeyField, t); //根结点关联的key
            if (keyFieldValue.equals(parentFieldValue)) { //关联字段相等
                if (subs == null) {
                    subs = new ArrayList<E>();
                }
                //取子节点key
                Object key = getValueByField(keyField, t);
                subs.add((E) key);
            }
        }
        return subs;
    }

    /**
     * 根据属性名获取属性
     */
    public static Field getField(String fieldName, Class<?> clazz) {
        Class<?> old = clazz;
        Field field = null;
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                if (field != null) {
                    break;
                }
            } catch (Exception e) {
            }
        }
        if (field == null) {
            throw new NullPointerException(old + "没有" + fieldName + "属性");
        }
        return field;
    }

    /**
     * 获取目标类的属性
     */
    public static Field getField(String fieldName, String className) {
        try {
            return getField(fieldName, Class.forName(className));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取目标对象的属性
     */
    public static Field getField(String fieldName, Object object) {
        return getField(fieldName, object.getClass());
    }


    /**
     * 获取当前类的属性 包括父类
     */
    public static List<Field> getFields(Class<?> clazz, Class<?> stopClass) {
        try {
            List<Field> fieldList = new ArrayList<Field>();
            while (clazz != null && clazz != stopClass) {//当父类为null的时候说明到达了最上层的父类(Object类).
                fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
                clazz = clazz.getSuperclass(); //得到父类,然后赋给自己
            }
            return fieldList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }




    /**
     * 获取当前类的属性 包括父类
     */
    @Deprecated
    public static List<Field> getFields(Class<?> clazz) {
        return getFields(clazz, Object.class);
    }

    private static List<Class<?>> getSuperClasses(Class<?> clazz, Class<?> stopClass) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        while (clazz != null && clazz != stopClass) {//当父类为null的时候说明到达了最上层的父类(Object类).
            classes.add(clazz);
            clazz = clazz.getSuperclass(); //得到父类,然后赋给自己
        }
        return classes;
    }


    /**
     * 通过属性赋值
     */
    public static void setValueByField(String fieldName, Object object, Object value) {
        Field field = getField(fieldName, object.getClass());
        setValueByField(field, object, value);
    }

    /**
     * 通过属性赋值
     */
    public static void setValueByField(Field field, Object object, Object value) {
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
                field.set(object, value);
                field.setAccessible(false);
            } else {
                field.set(object, value);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    /**
     * 获取属性的值
     */
    public static <T> T getValueByField(String fieldName, Object object) {
        Field field = getField(fieldName, object.getClass());
        return getValueByField(field, object);
    }

    /**
     * 获取属性的值
     */
    public static <T> T getValueByField(Field field, Object object) {
        try {
            Object value;
            if (!field.isAccessible()) {
                field.setAccessible(true);
                value = field.get(object);
                field.setAccessible(false);
            } else {
                value = field.get(object);
            }
            return (T) value;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 通过set方法赋值
     */
    public static void setValueBySetMethod(String fieldName, Object object, Object value) {
        if (object == null) {
            throw new RuntimeException("实例对象不能为空");
        }
        if (value == null) {
            return;
        }
        try {
            String setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method setMethod = getMethod(setMethodName, object.getClass(), value.getClass());
            setMethod.invoke(object, value);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 通过set方法赋值
     */
    public static void setValueBySetMethod(Field field, Object object, Object value) {
        if (object == null) {
            throw new RuntimeException("实例对象不能为空");
        }
        if (value == null) {
            return;
        }
        setValueBySetMethod(field.getName(), object, value);
    }

    /**
     * 通过get方法取值
     */
    public static <T> T getValueByGetMethod(String fieldName, Object object) {
        try {
            if (StringUtils.isNotBlank(fieldName)) {
                String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Method getMethod = getMethod(getMethodName, object.getClass());
                return (T) getMethod.invoke(object);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 通过get方法取值
     */
    public static <T> T getValueByGetMethod(Field field, Object object) {
        return getValueByGetMethod(field.getName(), object);
    }


    /**
     * 获取某个类的某个方法(当前类和父类)
     */
    public static Method getMethod(String methodName, Class<?> clazz) {
        Method method = null;
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                method = clazz.getDeclaredMethod(methodName);
                break;
            } catch (Exception e) {
            }
        }
        if (method == null) {
            throw new NullPointerException("没有" + methodName + "方法");
        }
        return method;
    }

    /**
     * 获取get方法
     *
     * @param fieldName 属性名
     * @return
     */
    public static String getMethodName(String fieldName) {
        String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return methodName;
    }

    /**
     * 获取某个类的某个方法(当前类和父类) 带一个参数
     */
    public static Method getMethod(String methodName, Class<?> clazz, Class<?> paramType) {
        Method method = null;
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                method = clazz.getDeclaredMethod(methodName, paramType);
                if (method != null) {
                    return method;
                }
            } catch (Exception e) {
            }
        }
        if (method == null) {
            throw new NullPointerException(clazz + "没有" + methodName + "方法");
        }
        return method;
    }

    /**
     * 获取某个类的某个方法(当前类和父类)
     */
    public static Method getMethod(String methodName, Object obj) {
        return getMethod(methodName, obj.getClass());
    }

    /**
     * 获取某个类的某个方法(当前类和父类) 一个参数
     */
    public static Method getMethod(String methodName, Object obj, Class<?> paramType) {
        return getMethod(methodName, obj.getClass(), paramType);
    }

    /**
     * 获取某个类的某个方法(当前类和父类)
     */
    public static Method getMethod(String methodName, String clazz) {
        try {
            return getMethod(methodName, Class.forName(clazz));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    /**
     * 获取某个类的某个方法(当前类和父类) 一个参数
     */
    public static Method getMethod(String methodName, String clazz, Class<?> paramType) {
        try {
            return getMethod(methodName, Class.forName(clazz), paramType);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    /**
     * 获取方法上的注解
     */
    public static Annotation getMethodAnnotation(Method method, Class targetAnnotationClass) {
        Annotation methodAnnotation = method.getAnnotation(targetAnnotationClass);
        return methodAnnotation;
    }

    /**
     * 获取属性上的注解
     */
    public static Annotation getFieldAnnotation(Field field, Class targetAnnotationClass) {
        Annotation methodAnnotation = field.getAnnotation(targetAnnotationClass);
        return methodAnnotation;
    }

    /**
     * 获取类上的注解
     *
     * @param targetAnnotationClass 目标注解
     * @param targetObjcetClass     目标类
     * @return 目标注解实例
     */
    public static Annotation getClassAnnotation(Class targetAnnotationClass, Class<?> targetObjcetClass) {
        Annotation methodAnnotation = targetObjcetClass.getAnnotation(targetAnnotationClass);
        return methodAnnotation;
    }

    /**
     * 获取类上的注解
     *
     * @return 目标注解实例
     */
    public static Annotation getClassAnnotation(Class targetAnnotationClass, Object obj) {
        return getClassAnnotation(targetAnnotationClass, obj.getClass());
    }

    /**
     * 获取类上的注解
     *
     * @return 目标注解实例
     */
    public static Annotation getClassAnnotation(Class targetAnnotationClass, String clazz) {
        try {
            return getClassAnnotation(targetAnnotationClass, Class.forName(clazz));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    /**
     * 获取注解某个属性的值
     *
     * @param methodName 属性名
     * @param annotation 目标注解
     * @param <T>        返回类型
     * @throws Exception
     */
    public static <T> T getAnnotationValue(String methodName, Annotation annotation) {
        try {
            Method method = annotation.annotationType().getMethod(methodName);
            Object object = method.invoke(annotation);
            return (T) object;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    /**
     * 获取某个类的某个方法上的某个注解的属性
     *
     * @param methodName            注解属性的名字
     * @param targetAnnotationClass 目标注解
     * @param targetObjecMethodName 目标类的方法
     * @param targetObjectClass     目标类
     * @param <T>                   返回值类型
     */
    public static <T> T getMethodAnnotationValue(String methodName, Class targetAnnotationClass, String targetObjecMethodName, Class targetObjectClass) {
        Method method = getMethod(targetObjecMethodName, targetObjectClass);
        Annotation annotation = getMethodAnnotation(method, targetAnnotationClass);
        return getAnnotationValue(methodName, annotation);
    }

    /**
     * @param methodName            注解属性名
     * @param targetAnnotationClass 目标注解
     * @param targetObjecFieldName  目标属性名字
     * @param targetObjectClass     目标类
     * @param <T>                   返回值类型
     */
    public static <T> T getFieldAnnotationValue(String methodName, Class targetAnnotationClass, String targetObjecFieldName, Class targetObjectClass) {
        Field field = getField(targetObjecFieldName, targetObjectClass);
        Annotation annotation = getFieldAnnotation(field, targetAnnotationClass);
        return getAnnotationValue(methodName, annotation);
    }

    /**
     * 判断 clazz是否是target的子类型或者相等
     */
    public static boolean isSubClassOrEquesClass(Class<?> clazz, Class<?> target) {
        if (clazz == target) {
            return true;
        }
        while (clazz != Object.class) {
            if (clazz == target) {
                return true;
            }
            clazz = clazz.getSuperclass();
        }
        return false;
    }

}