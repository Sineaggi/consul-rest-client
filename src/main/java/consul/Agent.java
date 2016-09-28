package consul;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Agent extends ConsulChain {
    Agent(Consul consul) {
        super(consul);
    }

    public Self self() throws ConsulException {
        final JSONObject member = checkResponse(Unirest.get(consul().getUrl() + EndpointCategory.Agent.getUri() + "self"))
                                    .getObject().getJSONObject("Member");
        return new Self(member.getString("Addr"), member.getInt("Port"), member.getString("Name"));
    }

    /**
     * Returns a list of all services offered.
     * @return
     * @throws ConsulException
     */
    public List<ServiceProvider> services() throws ConsulException {
        final Self self = self();
        final List<ServiceProvider> providers = new ArrayList<ServiceProvider>();

        final JSONObject obj = checkResponse(Unirest.get(consul().getUrl() + EndpointCategory.Agent.getUri() + "services")).getObject();
        for (Object key : obj.keySet()) {
            final JSONObject service = obj.getJSONObject(key.toString());

            final ServiceProvider provider = new ServiceProvider();
            provider.setId(service.getString("ID"));
            provider.setName(service.getString("Service"));
            provider.setPort(service.getInt("Port"));

            // Map tags
            String[] tags = null;
            if (!service.isNull("Tags")) {
                final JSONArray arr = service.getJSONArray("Tags");
                tags = new String[arr.length()];
                for (int i = 0; i < service.getJSONArray("Tags").length(); i++) {
                    tags[i] = arr.getString(i);
                }
            }
            provider.setTags(tags);
            provider.setAddress(self.getAddress());
            provider.setNode(self.getNode());

            providers.add(provider);
        }

        return providers;
    }

    public String register(ServiceProvider provider) throws ConsulException {
        final JSONArray tags = new JSONArray();
        if (provider.getTags() != null) {
            for (String tag : provider.getTags()) {
                tags.put(tag);
            }
        }

        final JSONObject service = new JSONObject();
        service.put("ID", provider.getId());
        service.put("Name", provider.getName());
        service.put("Port", provider.getPort());
        if (tags.length() > 0) {
            service.put("Tags", tags);
        }

        final HttpResponse<String> resp;
        try {
            resp = Unirest.put(consul().getUrl() + EndpointCategory.Agent.getUri() + "service/register")
                .body(service.toString()).asString();
        } catch (UnirestException e) {
            throw new ConsulException(e);
        }

        return resp.getBody().toString();
    }

    public void deregister(String serviceId) throws ConsulException {
        try {
            Unirest.get(consul().getUrl() + EndpointCategory.Agent.getUri() + "service/deregister/" + serviceId).asString();
        } catch (UnirestException e) {
            throw new ConsulException(e);
        }
    }

    public String getChecks() throws ConsulException {
        final HttpResponse<String> resp;
        try {
            resp = Unirest.get(consul().getUrl() + EndpointCategory.Agent.getUri() + "checks").asString();
        } catch (UnirestException e) {
            throw new ConsulException(e);
        }

        return resp.getBody().toString();
    }

    public String checkRegister(AgentCheck check) throws ConsulException {
        final JSONObject agentCheck = new JSONObject();
        agentCheck.put("ID", check.getId());
        agentCheck.put("Name", check.getName());
        agentCheck.put("Notes", check.getNotes());
        agentCheck.put("Script", check.getScript());
        agentCheck.put("Interval", check.getInterval());
        agentCheck.put("TTL", check.getTTL());

        HttpResponse<String> resp;
        try {
            resp = Unirest.put(consul().getUrl() + EndpointCategory.Agent.getUri() + "check/register")
                .body(agentCheck.toString()).asString();
        } catch (UnirestException e) {
            throw new ConsulException(e);
        }

        return resp.getBody().toString();
    }

    public void checkDeregister(String checkId) throws ConsulException {
        try {
            Unirest.get(consul().getUrl() + EndpointCategory.Agent.getUri() + "check/deregister/" + checkId).asString();
        } catch (UnirestException e) {
            throw new ConsulException(e);
        }
    }

    public void checkPass(String checkId) throws ConsulException {
        try {
            Unirest.get(consul().getUrl() + EndpointCategory.Agent.getUri() + "check/pass/" + checkId).asString();
        } catch (UnirestException e) {
            throw new ConsulException(e);
        }
    }

    public void checkWarn(String checkId) throws ConsulException {
        try {
            Unirest.get(consul().getUrl() + EndpointCategory.Agent.getUri() + "check/warn/" + checkId).asString();
        } catch (UnirestException e) {
            throw new ConsulException(e);
        }
    }

    public void checkFail(String checkId) throws ConsulException {
        try {
            Unirest.get(consul().getUrl() + EndpointCategory.Agent.getUri() + "check/fail/" + checkId).asString();
        } catch (UnirestException e) {
            throw new ConsulException(e);
        }
    }
}
