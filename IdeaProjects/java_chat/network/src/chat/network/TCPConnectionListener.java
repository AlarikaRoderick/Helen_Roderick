package chat.network;

public interface TCPConnectionListener {

    void OnConnectionReady(TCPConnection tcpconnection);    //соединение готово и с ним можно работать
    void OnReceiveString(TCPConnection tcpconnection, String value);    //принимается строка
    void OnDisconnect(TCPConnection tcpconnection);     //соединение разорвалось
    void OnException(TCPConnection tcpconnection, Exception e);   //все плохо
}
