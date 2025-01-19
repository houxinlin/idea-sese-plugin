package dev.coolrequest.sese;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Random;

public class NextImageRunnable implements Runnable {
    private static final Random random = new Random();

    @NotNull
    private String calcNewValue(String path) {
        return path.trim() + "," + 6 + ",scale,center";
    }

    public static void deleteFilesWithPattern(Path directory, String pattern) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.getFileName().toString().contains(pattern)) {
                    Files.delete(file);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Override
    public void run() {
        try {
            JsonElement jsonElement = JsonParser.parseString(sendHttpRequest());
            JsonElement urlElement = jsonElement.getAsJsonObject().get("url");
            if (urlElement != null) {
                String userHome = System.getProperty("user.home");
                Path sese = Paths.get(userHome, ".config", "sese");
                if (!sese.toFile().exists()) {
                    Files.createDirectories(sese);
                }
                String path = downloadFile(urlElement.getAsString(), sese.toString());
                PropertiesComponent prop = PropertiesComponent.getInstance();
                String value = calcNewValue(path);
                prop.setValue(IdeBackgroundUtil.FRAME_PROP, value);
                prop.setValue(IdeBackgroundUtil.EDITOR_PROP, value);
                IdeBackgroundUtil.repaintAllWindows();
            }
        } catch (IOException ignored) {

        }
    }

    public static String downloadFile(String fileURL, String saveDir) {
        try {
            URL url = new URL(fileURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(5000);
            int responseCode = httpURLConnection.getResponseCode();
            int i = random.nextInt(999);
            String fileName = "dev.cool.sese.idea.image";
            deleteFilesWithPattern(Paths.get(saveDir), fileName);
            fileName = i + fileName;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpURLConnection.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(saveDir + File.separator + fileName);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                fileOutputStream.close();
            }
            httpURLConnection.disconnect();
            return saveDir + File.separator + fileName;
        } catch (IOException ignored) {
        }
        return null;
    }

    public static String sendHttpRequest() throws IOException {
        try {
            String urlString = "https://3650000.xyz/api/?type=json";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "IntelliJ IDEA Plugin");
            int responseCode = connection.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (Exception ignored) {

        }
        throw new IOException();
    }
}
