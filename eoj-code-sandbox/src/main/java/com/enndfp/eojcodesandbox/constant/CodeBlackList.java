package com.enndfp.eojcodesandbox.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 代码黑名单
 * 黑名单检测通常用于辅助安全策略，而不是作为唯一的安全手段
 *
 * @author Enndfp
 */
public interface CodeBlackList {

    /**
     * Java 代码黑名单
     */
    List<String> JAVA_BLACK_LIST = Arrays.asList(
            // 文件操作相关
            "Files", "File", "FileInputStream", "FileOutputStream", "RandomAccessFile", "FileReader", "FileWriter",
            "FileChannel", "FileLock", "Path", "Paths", "File.createTempFile", "File.createTempDirectory",
            "ZipInputStream", "ZipOutputStream",

            // 网络相关
            "Socket", "ServerSocket", "DatagramSocket", "InetAddress", "URL", "URLConnection", "HttpURLConnection",
            "SocketChannel", "ServerSocketChannel", "DatagramChannel", "SocketPermission", "ServerSocketPermission",

            // 系统命令执行相关
            "exec", "Runtime.getRuntime().exec", "ProcessBuilder", "SecurityManager", "System.exit",
            "Runtime.getRuntime().halt", "SecurityManager.checkExec", "Runtime.getRuntime().exec",

            // 反射相关
            "Class.forName", "Method.invoke", "sun.reflect.", "java.lang.reflect.", "Unsafe", "sun.misc.Unsafe",
            "sun.reflect.Unsafe", "Proxy", "java.lang.reflect.Field", "java.lang.reflect.Method",

            // 数据库相关
            "Statement", "PreparedStatement", "CallableStatement", "DataSource", "Connection", "ResultSet", "Hibernate",
            "JPA", "createStatement", "prepareStatement", "prepareCall", "getConnection", "setAutoCommit", "setTransactionIsolation",

            // 加密解密相关
            "Cipher", "MessageDigest", "KeyGenerator", "KeyPairGenerator", "SecretKeyFactory", "KeyStore", "SecureRandom",
            "java.security.*", "javax.crypto.*", "javax.crypto.spec.*",

            // 序列化相关
            "ObjectInputStream", "ObjectOutputStream", "Serializable", "Externalizable", "readObject", "writeObject",
            "java.io.ObjectInputStream", "java.io.ObjectOutputStream",

            // 线程相关
            "Thread", "Runnable", "Executor", "ExecutorService", "ThreadPoolExecutor", "ThreadGroup", "ThreadLocal",
            "Thread.sleep", "Thread.yield", "Thread.stop", "Thread.suspend", "Thread.resume", "java.util.concurrent.*",

            // 安全管理器相关
            "SecurityManager",

            // 其他可能导致安全问题的操作
            "System.load", "System.loadLibrary", "JNI", "Java Native Interface", "Unsafe.allocateMemory", "Unsafe.freeMemory",
            "System.getProperties", "System.setProperty", "System.getenv", "System.console", "Runtime.addShutdownHook",
            "Runtime.load", "Runtime.loadLibrary",

            // 不安全的文件和环境变量操作
            "File.separator", "File.createTempFile", "File.createTempDirectory", "File.getCanonicalPath",
            "System.setProperty", "System.getenv", "System.console",

            // 本地库和外部命令调用
            "System.loadLibrary", "System.load", "Runtime.getRuntime().exec", "Runtime.exec", "ProcessBuilder", "execvp",
            "ProcessBuilder.start",

            // 不安全的Web操作（防止SQL注入和XSS）
            "java.net.HttpURLConnection", "java.net.URL", "java.net.Socket", "java.net.ServerSocket", "javax.servlet.*",
            "org.apache.catalina.*", "org.apache.coyote.*", "org.apache.tomcat.*",

            // 反序列化相关的安全隐患
            "java.io.ObjectInputStream", "java.io.ObjectOutputStream", "java.io.Serializable", "Externalizable",

            // 不安全的输入输出（防止命令注入）
            "BufferedReader", "FileReader", "FileWriter", "FileInputStream", "FileOutputStream", "PrintWriter",
            "InputStreamReader", "Scanner.nextLine", "Scanner.nextInt", "Scanner.nextDouble"
    );

    /**
     * C++代码黑名单
     */
    List<String> CPP_BLACK_LIST = Arrays.asList(
            "system", "popen", "fork", "exec", "ProcessBuilder",
            "Runtime", "shutdown", "fopen", "ifstream", "ofstream"
    );

    /**
     * Go代码黑名单
     */
    List<String> GO_BLACK_LIST = Arrays.asList(
            "os.Execute", "exec.Command", "syscall", "os.Remove",
            "ioutil.WriteFile", "os.Open", "os.Create"
    );
}
