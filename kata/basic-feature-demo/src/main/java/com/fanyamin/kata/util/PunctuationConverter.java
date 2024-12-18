package com.fanyamin.kata.util;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;


/**
 * it is a converter from chinese punctuation to english punctuation
 */
public class PunctuationConverter {

    // 使用final关键字确保引用不可变，这里存储的是不可变的BidiMap实例
    public static final BidiMap<String, String> CN_EN_MARK_MAP;

    static {
        // 先创建一个可变的DualHashBidiMap进行初始化
        CN_EN_MARK_MAP = new DualHashBidiMap<>();
        CN_EN_MARK_MAP.put("，", ", ");
        CN_EN_MARK_MAP.put("：", ": ");
        CN_EN_MARK_MAP.put("。", ". ");
        CN_EN_MARK_MAP.put("（", " (");
        CN_EN_MARK_MAP.put("）", ") ");

    }

    /**
     *
     * @param inputFilePath input file path
     * @param outputFilePath output file path
     * @param strMap a string map, key is old string, value is new string to replace the old string
     * @return  replaced characters count
     */
    public static int convertPunctuation(String inputFilePath, String outputFilePath, Map<String, String> strMap) {
        System.out.printf("# convert %s to %s ...", inputFilePath, outputFilePath);
        int count = 0;
        boolean isSameFile = inputFilePath.equals(outputFilePath);
        File tempFile = null;
        try {
            if (isSameFile) {
                // 如果是同一个文件，先创建临时文件用于暂存替换过程中的内容
                tempFile = File.createTempFile("temp", ".txt");
                outputFilePath = tempFile.getAbsolutePath();
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
                String line;
                while ((line = reader.readLine())!= null) {

                    for (Map.Entry<String, String> entry : strMap.entrySet()) {
                        if (line.contains(entry.getKey()) ) {
                            System.out.printf("convert %s -> %s in %s\n",entry.getKey(), entry.getValue(), line);
                            line = line.replace(entry.getKey(), entry.getValue());
                            System.out.printf("converted: %s\n", line);
                            count ++;
                        } else {
                            //System.out.printf("Does not contain %s -> %s\n", entry.getKey(), entry.getValue());
                        }
                    }
                    writer.write(line);
                    writer.newLine();
                }
            }
            if (isSameFile) {
                // 如果是同一个文件，将临时文件内容覆盖回原文件
                try (BufferedReader tempReader = new BufferedReader(new FileReader(tempFile.getAbsolutePath()));
                     BufferedWriter originalWriter = new BufferedWriter(new FileWriter(inputFilePath))) {
                    System.out.printf("same file of input and output: %s\n", inputFilePath);
                    String line;
                    while ((line = tempReader.readLine())!= null) {
                        originalWriter.write(line);
                        originalWriter.newLine();
                    }
                }
                // 删除临时文件
                tempFile.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    public static void main(String[] args) {
        String inputFilePath = null;
        String outputFilePath = null;

        for (int i = 0; i < args.length; i++) {
            if ("-i".equals(args[i])) {
                if (i + 1 < args.length) {
                    inputFilePath = args[i + 1];
                } else {
                    System.err.println("请在 -i 后指定输入文件路径");
                    return;
                }
            } else if ("-o".equals(args[i])) {
                if (i + 1 < args.length) {
                    outputFilePath = args[i + 1];
                } else {
                    System.err.println("请在 -o 后指定输出文件路径");
                    return;
                }
            }
        }

        if (inputFilePath == null) {
            System.err.println("请通过 -i 参数指定输入文件路径");
            inputFilePath = "/Users/walter/workspace/walter/wftech/source/language/java_interview.rst";
            //return;
        }

        if (outputFilePath == null) {
            //Path path = Paths.get(inputFilePath);
            //String basename = path.getFileName().toString();
            //outputFilePath = "/tmp/" + basename;
            outputFilePath = inputFilePath;
        }
        int replacedCount = convertPunctuation(inputFilePath, outputFilePath, CN_EN_MARK_MAP);
        System.out.println("替换的字符数量: " + replacedCount);
    }
}
