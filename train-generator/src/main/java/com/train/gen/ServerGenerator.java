package com.train.gen;


import cn.hutool.core.date.DateTime;
import com.train.util.DbUtil;
import com.train.util.Field;
import com.train.util.TemplateUtil;
import com.train.util.XmlUtil;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ServerGenerator {

    // 文件生成的位置
    static String serverPath = "/src/main/java/com/train/";

    public static void main(String[] args) throws Exception {

        // 获取mybatis-generator
        String generatorPath = XmlUtil.getGeneratorPath();

        // 比如generator-config-train-system-management.xml，得到module = train-system-management
        String module = XmlUtil.getModule();
        System.out.println("module: " + module);
        serverPath = module + serverPath;
        System.out.println("serverPath: " + serverPath);

        // 读取table节点 train-generator/src/main/resources/generator-config-train-system-management.xml
        Document document = new SAXReader().read("train-generator/" + generatorPath);
        Node table = document.selectSingleNode("//table");
        System.out.println(table);
        // 读取table节点的属性 获取表名和实体类名
        Node tableName = table.selectSingleNode("@tableName");
        Node domainObjectName = table.selectSingleNode("@domainObjectName");
        System.out.println(tableName.getText() + "/" + domainObjectName.getText());

        // 为DbUtil设置数据源 读取jdbcConnection节点 获取数据库连接信息
        DbUtil.url = document.selectSingleNode("//@connectionURL").getText();
        DbUtil.user = document.selectSingleNode("//@userId").getText();
        DbUtil.password = document.selectSingleNode("//@password").getText();
        System.out.println("url: " + DbUtil.url);
        System.out.println("user: " + DbUtil.user);
        System.out.println("password: " + DbUtil.password);


        // 示例：表名 sms_record
        // Domain = SmsRecord 实体类名
        String Domain = domainObjectName.getText();
        // domain = smsRecord 实体类名首字母小写
        String domain = Domain.substring(0, 1).toLowerCase() + Domain.substring(1);
        // do_main = sms-record 做为vue的组件名
        String do_main = tableName.getText().replaceAll("_", "-");
        // 表中文名
        String tableNameCn = DbUtil.getTableComment(tableName.getText());
        List<Field> fieldList = DbUtil.getColumnByTableName(tableName.getText());
        Set<String> typeSet = TemplateUtil.getJavaTypes(fieldList);

        // 组装参数
        Map<String, Object> param = new HashMap<>();
        param.put("now", DateTime.now());
        param.put("module", module);
        param.put("Domain", Domain);
        param.put("domain", domain);
        param.put("do_main", do_main);
        param.put("tableNameCn", tableNameCn);
        param.put("fieldList", fieldList);
        param.put("typeSet", typeSet);
        param.put("readOnly", TemplateUtil.readOnly);
        System.out.println("组装参数：" + param);

        TemplateUtil.gen(Domain, param, "service", "service", serverPath);
        TemplateUtil.gen(Domain, param, "controller", "controller", serverPath);
        TemplateUtil.gen(Domain, param, "bean/request", "saveReq", serverPath);
        TemplateUtil.gen(Domain, param, "bean/request", "queryReq", serverPath);
        TemplateUtil.gen(Domain, param, "bean/response", "queryRes", serverPath);

        // 生成vue 前端代码暂不生成 由前端开发人员自己生成
        //TemplateUtil.genVue(do_main, param, module);
    }

}
