package io.sot;

public class Session
{
    private Socket socket;

    private State state;

    public Session()
    {
    }

    public Socket getSocket()
    {
        return socket;
    }

    public void setSocket(Socket socket)
    {
        this.socket = socket;
    }

    public State getState()
    {
        return this.state;
    }

    public int getPacketSize()
    {
        // TODO
        return 8196;
    }

    public enum State
    {
        INITIAL,
        SSL_NEG,
        LOGIN_READY,
        SPNEGO_NEG,
        FED_AUTH_READY,
        LOGGED_IN,
        CLIENT_REQ_EXEC,
        ROUTING_COMPLETED,
        FINAL
    }
}
