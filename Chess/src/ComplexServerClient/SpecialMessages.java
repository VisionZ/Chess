package ComplexServerClient;

public final class SpecialMessages {
    
    private SpecialMessages() {
        
    }

    /**
     * Message sent to all clients every once in a while
     * to verify that they are still connected.
     */
    public static final String PING = "PING"; 
    
    /**
     * Message sent to disconnect a client.
     */
    public static final String EXIT = "EXIT";
    
    /**
     * Header message sent to indicate chess data is passing through.
     */
    public static final String DATA = "DATA";
    
    /**
     * Header message to indicate that client data from the
     * server is being sent to clients.
     */
    public static final String TABLE_LIST = "TABLE_LIST";
    
    /**
     * Header message to indicate that a client wants to
     * play with other particular client.
     */
    public static final String PLAYER = "Player";
}