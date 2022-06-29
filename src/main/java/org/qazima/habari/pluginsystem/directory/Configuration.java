package org.qazima.habari.pluginsystem.directory;

import com.fasterxml.jackson.databind.JsonNode;
import org.qazima.habari.pluginsystem.extension.NodeExtension;
import org.qazima.habari.pluginsystem.interfaces.IReadOnlyConfiguration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Configuration implements IReadOnlyConfiguration {
    private String configurationUri = "";
    private final List<String> defaultPages = new ArrayList<>();
    private String metadataUri = "";
    private String path = "";
    private String uri = "";

    public String getConfigurationUri() {
        return configurationUri;
    }

    public void setConfigurationUri(String configurationUri) {
        this.configurationUri = configurationUri;
    }

    public List<String> getDefaultPages() {
        return defaultPages;
    }

    @Override
    public int getDefaultPageSize() {
        return 0;
    }

    @Override
    public boolean isGetAllowed() {
        return true;
    }

    @Override
    public String getMetadataUri() {
        return metadataUri;
    }

    private void setMetadataUri(String metadataUri) {
        this.metadataUri = metadataUri;
    }

    public String getPath() {
        return path;
    }

    private void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getUri() {
        return uri;
    }

    private void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String getType() {
        return "Directory";
    }

    @Override
    public void loadFromJson(JsonNode node, int defaultPageSize, boolean isGetAllowed) {
        setPath(NodeExtension.get(node, "path", ""));
        setConfigurationUri(NodeExtension.get(node, "configurationUri", "/configuration"));
        setMetadataUri(NodeExtension.get(node, "metadataUri", "/metadata"));
        setUri(NodeExtension.get(node, "uri", "/"));
        Iterator<JsonNode> defaultPagesIterator = NodeExtension.getElements(node,"defaultPages");
        while (defaultPagesIterator.hasNext()) {
            JsonNode defaultPageNode = defaultPagesIterator.next();
            getDefaultPages().add(defaultPageNode.asText());
        }
    }
}
