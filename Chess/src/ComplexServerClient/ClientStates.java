package ComplexServerClient;

public final class ClientStates {

    /**
     * State of client if client is playing with another client.
     */
    public static final int PLAYING = 0;

    /**
     * State of client if client has not decided to play yet.
     */
    public static final int WAITING = 1;

    /**
     * State of client when disconnecting from the server.
     */
    @Deprecated
    public static final int EXITED = 2;
}