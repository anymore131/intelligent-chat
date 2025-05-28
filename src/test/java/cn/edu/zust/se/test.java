package cn.edu.zust.se;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class test {

    @Test
    public void stringTOMap(){
        String str = "{fileName=03-storage1,test.pdf, filePath=http://47.99.150.135:9000/ai-test/2025-05-13/8007b5f7cd054d4bbe0f7a34d8a02a2d.pdf, createTime=2025-05-13T17:50:48.790753100, fileUUID=2025-05-13/8007b5f7cd054d4bbe0f7a34d8a02a2d.pdf, id=1}";
        // 去掉大括号
        String content = str.substring(1, str.length() - 1);
        // 按 ", " 分割（注意空格）
        String[] pairs = content.split(", ");
        Map<String, String> map = new HashMap<>();
        for (String pair : pairs) {
            // 只按第一个 "=" 分割
            int equalIndex = pair.indexOf("=");
            if (equalIndex != -1) {
                String key = pair.substring(0, equalIndex).trim();
                String value = pair.substring(equalIndex + 1).trim();
                map.put(key, value);
            }
        }
        String fileName = map.get("fileName");
        System.out.println(fileName); // 输出: 03-storage1,test.pdf
    }
}
