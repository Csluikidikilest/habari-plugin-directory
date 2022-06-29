package org.qazima.habari.pluginsystem.directory;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpContext;
import org.apache.http.HttpStatus;
import org.qazima.habari.pluginsystem.interfaces.IReadOnlyConfiguration;
import org.qazima.habari.pluginsystem.interfaces.IPlugin;
import org.qazima.habari.pluginsystem.library.Content;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class Plugin implements IPlugin {
    private Configuration configuration = new Configuration();

    @Override
    public IReadOnlyConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public boolean configure(JsonNode node, int defaultPageSize, boolean isGetAllowed, boolean isDeleteAllowed, boolean isPostAllowed, boolean isPutAllowed) {
        getConfiguration().loadFromJson(node, defaultPageSize, isGetAllowed);
        return true;
    }

    @Override
    public int process(HttpContext httpContext, Content content) {
        int statusCode = HttpStatus.SC_OK;
        String localPath = ((Configuration)getConfiguration()).getPath();
        String remotePath = httpContext.getPath().substring(1).replace('/', File.separatorChar);
        String fileName = Path.of(localPath, remotePath).toString();
        if(fileName.endsWith(File.separator)) {
            String defaultPage = ((Configuration)getConfiguration()).getDefaultPages().stream().filter(dp -> new File(Path.of(localPath, remotePath, dp).toString()).exists()).findFirst().orElse("");
            fileName = Path.of(localPath, remotePath, defaultPage).toString();
        }

        File file = new File(fileName);
        if(file.exists()) {
            try {
                content.setType(URLConnection.guessContentTypeFromName(file.getName()));
                FileInputStream fileInputStream = new FileInputStream(file);
                content.setBody(fileInputStream.readAllBytes());
                fileInputStream.close();
            } catch (Exception e){
                statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
                content.setType("text/plain");
                content.setBody(e.toString().getBytes(StandardCharsets.UTF_8));
            }
        } else {
            statusCode = HttpStatus.SC_NOT_FOUND;
            content.setType("text/plain");
            content.setBody("".getBytes(StandardCharsets.UTF_8));
        }

        return statusCode;
    }

    @Override
    public int processConfigure(HttpContext httpContext, Content content) {
        int statusCode = HttpStatus.SC_OK;
        return statusCode;
    }

    @Override
    public int processMetadata(HttpContext httpContext, Content content) {
        int statusCode = HttpStatus.SC_OK;
        return statusCode;
    }
}
