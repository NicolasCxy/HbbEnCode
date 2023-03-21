package com.hbb.common.utils;

import android.os.Environment;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 流工具类，方便把流保存下来
 */
public class StreamFile {
    private static final String TAG = "StreamFile";

    public static void writeBytes(byte[] array) {
        FileOutputStream writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileOutputStream(Environment.getExternalStorageDirectory() + "/x2642222_1280_720.h264", true);
            writer.write(array);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void writeBytes(byte[] array, boolean isSoftware) {
        FileOutputStream writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            if (isSoftware) {
                writer = new FileOutputStream(Environment.getExternalStorageDirectory() + "/softWare_" + GlobalVal.width + "_" + GlobalVal.height + "_" + GlobalVal.fps + "_" + GlobalVal.testSoftWareFlag + ".h264", true);
            } else {
                writer = new FileOutputStream(Environment.getExternalStorageDirectory() + "/HardWare_" + GlobalVal.width + "_" + GlobalVal.height + "_" + GlobalVal.fps + "_" + GlobalVal.testSoftWareFlag1 + " .h264", true);
            }

            writer.write(array);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void writeBytes(byte[] array, boolean isSoftware, String flag) {
        FileOutputStream writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            if (isSoftware) {
                writer = new FileOutputStream(Environment.getExternalStorageDirectory() + "/softWare_" + GlobalVal.width + "_" + GlobalVal.height + "_" + GlobalVal.fps + "_" + flag + ".h264", true);
            } else {
                writer = new FileOutputStream(Environment.getExternalStorageDirectory() + "/HardWare_" + GlobalVal.width + "_" + GlobalVal.height + "_" + GlobalVal.fps + "_" + flag + " .h264", true);
            }

            writer.write(array);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void writeLen(int len) {
        FileOutputStream writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileOutputStream(Environment.getExternalStorageDirectory() + "/h264.dat", true);
            writer.write(len);
            writer.write('\n');


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String writeContent(byte[] array) {
        char[] HEX_CHAR_TABLE = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(HEX_CHAR_TABLE[(b & 0xf0) >> 4]);
            sb.append(HEX_CHAR_TABLE[b & 0x0f]);
        }
//        Log.i(TAG, "writeContent: "+sb.toString());
        FileWriter writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(Environment.getExternalStorageDirectory() + "/CXY0314H264.txt", true);
            writer.write(sb.toString());
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
