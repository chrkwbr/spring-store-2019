package ninja.cero.store.accesslog;

public class AccessLog {

    private String date;

    private String method;

    private String path;

    private int status;

    private String host;

    private String address;

    private long elapsed;

    private String userAgent;

    private String referer;

    public AccessLog(String date, String method, String path, int status, String host,
                     String address, long elapsed, String userAgent,
                     String referer) {
        this.date = date;
        this.method = method;
        this.path = path;
        this.status = status;
        this.host = host;
        this.address = address;
        this.elapsed = elapsed;
        this.userAgent = userAgent;
        this.referer = referer;
    }

    public AccessLog() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getElapsed() {
        return elapsed;
    }

    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }


    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    @Override
    public String toString() {
        return "date:" + this.date + "\tmethod:" + this.method + "\tpath:" + this.path
            + "\tstatus:" + this.status + "\thost:" + this.host + "\taddress:"
            + this.address + "\telapsed:" + this.elapsed + "ms\tuser-agent:"
            + this.userAgent + "\treferer:" + this.referer;
    }
}