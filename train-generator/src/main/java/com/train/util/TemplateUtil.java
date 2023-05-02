package com.train.util;

import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023/5/2 14:09
 */
public class TemplateUtil {

    // 是否只读
    public static boolean readOnly = false;
    static String vuePath = "admin/src/views/main/";

    /**
     * 获取所有的Java类型，使用Set去重
     */
    public static Set<String> getJavaTypes(List<Field> fieldList) {
        Set<String> set = new HashSet<>();
        for (int i = 0; i < fieldList.size(); i++) {
            Field field = fieldList.get(i);
            set.add(field.getJavaType());
        }
        return set;
    }

    /**
     * 生成Vue文件
     */
    public static void genVue(String do_main, Map<String, Object> param,String module) throws IOException, TemplateException {
        FreemarkerUtil.initConfig("vue.ftl");
        new File(vuePath + module).mkdirs();
        String fileName = vuePath + module + "/" + do_main + ".vue";
        System.out.println("开始生成：" + fileName);
        FreemarkerUtil.generator(fileName, param);
    }

    /**
     * 生成Java文件
     */
    public static void gen(String Domain, Map<String, Object> param, String packageName, String target,String serverPath) throws IOException, TemplateException {
        FreemarkerUtil.initConfig(target + ".ftl");
        String toPath = serverPath + packageName + "/";
        new File(toPath).mkdirs();
        String Target = target.substring(0, 1).toUpperCase() + target.substring(1);
        String fileName = toPath + Domain + Target + ".java";
        System.out.println("开始生成：" + fileName);
        FreemarkerUtil.generator(fileName, param);
    }
}
