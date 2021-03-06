package org.dreamexposure.tap.core.objects.file;

import org.json.JSONObject;

import java.util.UUID;

/**
 * @author NovaFox161
 * Date Created: 12/15/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class UploadedFile {
    private String hash;
    private String path;
    private String type;
    private String url;
    private UUID uploader;
    private long timestamp;
    private String name;
    
    //Getters
    public String getHash() {
        return hash;
    }
    
    public String getPath() {
        return path;
    }
    
    public String getType() {
        return type;
    }
    
    public String getUrl() {
        return url;
    }
    
    public UUID getUploader() {
        return uploader;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getName() {
        return name;
    }
    
    //Setters
    public void setHash(String _hash) {
        hash = _hash;
    }
    
    public void setPath(String _path) {
        path = _path;
    }
    
    public void setType(String _type) {
        type = _type;
    }
    
    public void setUrl(String _url) {
        url = _url;
    }
    
    public void setUploader(UUID _uploader) {
        uploader = _uploader;
    }

    public void setTimestamp(long _timestamp) {
        timestamp = _timestamp;
    }

    public void setName(String _name) {
        name = _name;
    }
    
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        
        json.put("hash", hash);
        json.put("path", path);
        json.put("type", type);
        json.put("url", url);
        json.put("uploader", uploader.toString());
        json.put("timestamp", timestamp);
        json.put("name", name);
        
        return json;
    }
    
    public UploadedFile fromJson(JSONObject json) {
        hash = json.getString("hash");
        path = json.getString("path");
        type = json.getString("type");
        url = json.getString("url");
        uploader = UUID.fromString(json.getString("uploader"));
        timestamp = json.getLong("timestamp");
        name = json.getString("name");
        
        return this;
    }
}
