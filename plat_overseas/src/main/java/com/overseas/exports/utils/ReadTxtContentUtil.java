package com.overseas.exports.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.overseas.exports.SdkManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析txt 文本内容
 */

public class ReadTxtContentUtil {
    public static String FLAG_COMMENT = "#";
    public static String FLAG_FIELD = "$";
    public static String SPLIT_CHAR = "\\t";
    public static String SPLIT_TOKEN = ",";
    private List<String> itemTitleList = new ArrayList<String>();
    private List<String> itemContentList = new ArrayList<String>();
    private List<String[]> mListRecord = new ArrayList<String[]>();

    //读取文本文件中的内容
    public ReadTxtContentUtil(Context context, String filePath) {
        File file = new File(filePath);
        //判断是否是文件
        if (file.isDirectory()) {
            Toast.makeText(context, "The File doesn't not exist.", Toast.LENGTH_LONG).show();
        } else {
            try {
                InputStream inStream = context.getAssets().open(filePath);
                if (inStream != null) {
                    InputStreamReader inputReader = new InputStreamReader(inStream);
                    BufferedReader buffReader = new BufferedReader(inputReader);
                    String line;
                    boolean findFieldFlag = false;
                    //分行读取
                    while ((line = buffReader.readLine()) != null) {
                        if (line.startsWith(FLAG_COMMENT) || line.isEmpty()) {
                            continue;
                        }
                        if (line.startsWith(FLAG_FIELD)) {
                            findFieldFlag = true;
                            line = line.substring(1, line.length());
                            String[] itemTitle = line.split(SPLIT_CHAR);
                            for (int i = 0; i < itemTitle.length; ++i) {
                                String item = itemTitle[i];
                                itemTitleList.add(item);
                            }
                            continue;
                        }
                        if (!findFieldFlag) {
                            Toast.makeText(context, "文件格式错误", Toast.LENGTH_LONG).show();
                            break;
                        }
                        String[] itemContent = line.split(SPLIT_CHAR);
                        mListRecord.add(itemContent);
                        String item_content = null;
                        for (int i = 0; i < mListRecord.size(); ++i) {
                            item_content = mListRecord.get(i)[0];
                        }
                        itemContentList.add(item_content);
                    }
                    inStream.close();
                }
            } catch (java.io.FileNotFoundException e) {
                Toast.makeText(context, "The File doesn't not exist. " + e.toString(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(context, "msg = " + e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getItemContent(int code, String value) {
        int idPosition = itemContentList.indexOf(code + "");
        int contentPosition = itemTitleList.indexOf(value);
        return mListRecord.get(idPosition)[contentPosition];
    }
}
