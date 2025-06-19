package github.paroj.dsub2000.domain;

public class Scrobble {
    private final String serverKey;
    private final String songServerId;
    private final long time;
    private final boolean isSubmission;
    private int retries = 0;
    private long lastAttempt = 0;
    private String lastAttemptReason = null;

    public Scrobble(String serverKey, String songServerId, long time, boolean isSubmission) {
        this.serverKey = serverKey;
        this.songServerId = songServerId;
        this.time = time;
        this.isSubmission = isSubmission;
    }

    public String getServerKey() {
        return serverKey;
    }

    public String getSongServerId() {
        return songServerId;
    }

    public long getTime() {
        return time;
    }

    public boolean isSubmission() {
        return isSubmission;
    }

    public String getLastAttemptReason() {
        return lastAttemptReason;
    }

    public void setLastAttemptReason(String lastAttemptReason) {
        this.lastAttemptReason = lastAttemptReason;
    }

    public long getLastAttempt() {
        return lastAttempt;
    }

    public void setLastAttempt(long lastAttempt) {
        this.lastAttempt = lastAttempt;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }
}
