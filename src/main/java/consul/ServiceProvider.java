package consul;

import org.json.JSONObject;

import java.util.Arrays;

public class ServiceProvider {
    String id;
    String address;
    String node;
    String name;
    Integer port;
    String[] tags;
    boolean critical;

    public ServiceProvider() {

    }

    public ServiceProvider(String id, String name, Integer port, String[] tags) {
        this.id = id;
        this.name = name;
        this.port = port;
        this.tags = tags;
    }

    ServiceProvider(String name, String[] tags) {
        this.name = name;
        this.tags = tags;
    }

    ServiceProvider(JSONObject obj) {
        id = obj.has("ServiceID") ? obj.getString("ServiceID") : obj.optString("ID");
        address = obj.optString("Address");
        node = obj.optString("Node");
        name = obj.has("ServiceName") ? obj.getString("ServiceName") : obj.optString("Service");
        port = obj.has("ServicePort") ? obj.getInt("ServicePort") : obj.optInt("Port");
        // tags = obj.optJSONArray("ServiceTags");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public void setCritical(boolean critical) {
        this.critical = critical;
    }

    public boolean isCritical() {
        return critical;
    }

    @Override
    public String toString() {
        return "ServiceProvider [id=" + id + ", address=" + address + ", node=" + node + ", name=" + name + ", port=" + port +
                        ", tags=" + Arrays.toString(tags) + ", critical=" + critical + "]";
    }
}
