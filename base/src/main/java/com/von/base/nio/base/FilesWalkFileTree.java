package com.von.base.nio.base;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

public class FilesWalkFileTree {

    public static void main(String[] args) throws IOException {
        String filePath = "D:\\BaiduNetdiskDownload\\2025.5.20人和天骄大二班所有底片+每个子文件Outout是调色片";
//        statFileCount(filePath);
//        findFile(filePath, "jpg");
        delDir(filePath);
    }

    /**
     * 查找文件
     * @param filePath
     * @param fileSuffix
     * @throws IOException
     */
    public static void findFile(String filePath, String fileSuffix) throws IOException {
        AtomicInteger fileCount = new AtomicInteger();
        Files.walkFileTree(Paths.get(filePath), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//                System.out.println("visitFile: " + file);

                if (file.toFile().getName().toLowerCase().endsWith(fileSuffix)) {
                    fileCount.getAndIncrement();
                    System.out.println("findFile: " + file);
                }
                // 这行代码不要修改
                return super.visitFile(file, attrs);
            }
        });
        System.out.println("fileCount: " + fileCount);
    }

    /**
     * 统计目录下文件个数
     * @param filePath
     * @throws IOException
     */
    public static void statFileCount(String filePath) throws IOException {
        AtomicInteger fileCount = new AtomicInteger();
        AtomicInteger dirCount = new AtomicInteger();
        Files.walkFileTree(Paths.get(filePath), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("preVisitDirectory: " + dir);
                dirCount.getAndIncrement();
                // 这行代码不要修改
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("visitFile: " + file);

                fileCount.getAndIncrement();
                // 这行代码不要修改
                return super.visitFile(file, attrs);
            }
        });

        System.out.println("fileCount: " + fileCount);
        System.out.println("dirCount: " + dirCount);
    }

    /**
     * 删除目录
     * @param filePath
     * @throws IOException
     */
    public static void delDir(String filePath) throws IOException {

        Files.walkFileTree(Paths.get(filePath), new SimpleFileVisitor<Path>() {

            /**
             * 1、进入目录前
             * @param dir
             * @param attrs
             * @return
             * @throws IOException
             */
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("进入：" + dir);
                return super.preVisitDirectory(dir, attrs);
            }

            /**
             * 2、访问文件
             * @param file
             * @param attrs
             * @return
             * @throws IOException
             */
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("执行文件删除：" + file);
                return super.visitFile(file, attrs);
            }

            /**
             * 3、进入目录后
             * @param dir
             * @param exc
             * @return
             * @throws IOException
             */
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                System.out.println("退出目录，执行目录删除：" + dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

    /**
     * 复制目录
     * @param sourcePath
     * @param targetPath
     * @throws IOException
     */
    public static void copyDir(String sourcePath, String targetPath) throws IOException {
        Files.walk(Paths.get(sourcePath)).forEach(path-> {
            try {
                String targetName = path.toString().replace(sourcePath, targetPath);
                // 是目录
                if (Files.isDirectory(path)) {
                    Files.createDirectory(Paths.get(targetName));
                    // 是普通文件
                } else if (Files.isRegularFile(path)) {
                    Files.copy(path, Paths.get(targetName));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
