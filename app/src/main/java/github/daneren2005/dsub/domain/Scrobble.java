package github.daneren2005.dsub.domain;

public class Scrobble {
    private final String serverKey;
    private final String serverId;
    private final long time;
    private final boolean isSubmission;

    public Scrobble(String serverKey, String serverId, long time, boolean isSubmission) {
        this.serverKey = serverKey;
        this.serverId = serverId;
        this.time = time;
        this.isSubmission = isSubmission;
    }


    public String getServerKey() {
        return serverKey;
    }

    public String getServerId() {
        return serverId;
    }

    public long getTime() {
        return time;
    }

    public boolean isSubmission() {
        return isSubmission;
    }

}
