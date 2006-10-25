using System;
using System.Collections.Generic;
using System.Text;
using System.Collections;
using System.Net;
using System.Net.Sockets;
using System.Diagnostics;

namespace HandwritingRecognition {

    class HWServer {

        private Socket mainSocket;

        /// <summary>
        /// A thread-safe list of worker sockets (one per client).
        /// </summary>
        private ArrayList workerSocketList = ArrayList.Synchronized(new ArrayList());


        /// <summary>
        /// Connect to localhost:9898 to use Handwriting Recognition.
        /// </summary>
        private int portNumber = 9898;

        /// <summary>
        /// Enough for one character at a time!
        /// </summary>
        public class SocketPacket {
            public Socket currentSocket;
            public byte[] dataBuffer = new byte[256];
        }

        /// <summary>
        /// 
        /// </summary>
        public HWServer() {
            mainSocket = new Socket(AddressFamily.InterNetwork,
                                          SocketType.Stream,
                                          ProtocolType.Tcp);
            IPEndPoint ipLocal = new IPEndPoint(IPAddress.Any, portNumber);

            // Bind to local IP Address...
            mainSocket.Bind(ipLocal);

            // Start listening...
            mainSocket.Listen(4 /* pending connections backlog */);

            // Create the call back for any client connections...
            mainSocket.BeginAccept(new AsyncCallback(onClientConnect), null);

        }

        // This is the callback function, to be invoked when a client is connected
        public void onClientConnect(IAsyncResult asyn) {
            try {
                // Here we complete/end the BeginAccept() asynchronous call
                // by calling EndAccept() - which returns the reference to
                // a new Socket object
                Socket workerSocket = mainSocket.EndAccept(asyn);
                workerSocketList.Add(workerSocket);

                // Let the worker Socket do the further processing for the 
                // just connected client
                waitForData(workerSocket);

                // Display this client connection as a status message on the GUI	
                String str = String.Format("Client #{0} connected", workerSocketList.Count);
                Console.WriteLine(str);

                // Since the main Socket is now free, it can go back and wait for
                // other clients who are attempting to connect
                mainSocket.BeginAccept(new AsyncCallback(onClientConnect), null);
            }
            catch (ObjectDisposedException) {
                Console.WriteLine("onClientConnection: Socket has been closed.");
            }
            catch (SocketException se) {
                Console.WriteLine(se.Message);
            }

        }

        /// <summary>
        /// The function pointer for receiving data from the client.
        /// It will point to onDataReceived(...)
        /// </summary>
        public AsyncCallback workerCallBack;


        /// <summary>
        /// Start waiting for data from the client.
        /// </summary>
        /// <param name="socket"></param>
        public void waitForData(Socket socket) {
            try {
                if (workerCallBack == null) {
                    // Specify the callback function which is to be 
                    // invoked when there is any write activity by the 
                    // connected client
                    workerCallBack = new AsyncCallback(onDataReceived);
                }

                SocketPacket theSocPkt = new SocketPacket();
                theSocPkt.currentSocket = socket;

                // Start receiving any data written by the connected client
                // asynchronously
                socket.BeginReceive(theSocPkt.dataBuffer, 0,
                                   theSocPkt.dataBuffer.Length,
                                   SocketFlags.None,
                                   workerCallBack,
                                   theSocPkt);
            }
            catch (SocketException se) {
                Console.WriteLine(se.Message);
            }

        }

        /// <summary>
        /// This callback function will be invoked when the socket
        /// detects any client writing of data on the stream 
        /// </summary>
        /// <param name="asyn"></param>
        public void onDataReceived(IAsyncResult asyn) {
            try {
                SocketPacket socketData = (SocketPacket)asyn.AsyncState;

                int iRx = 0;

                // Complete the BeginReceive() asynchronous call by EndReceive() method
                // which will return the number of characters written to the stream 
                // by the client
                iRx = socketData.currentSocket.EndReceive(asyn);
                char[] chars = new char[iRx + 1];
                Decoder d = Encoding.UTF8.GetDecoder();
                int charLen = d.GetChars(socketData.dataBuffer, 0, iRx, chars, 0);
                String text = new System.String(chars);
                Console.WriteLine(text);


                // Continue the waiting for data on the Socket
                waitForData(socketData.currentSocket);
            }
            catch (ObjectDisposedException) {
                System.Diagnostics.Debugger.Log(0, "1", "\nOnDataReceived: Socket has been closed\n");
            }
            catch (SocketException se) {
                Console.WriteLine(se.Message);
            }
        }



        /// <summary>
        /// 
        /// </summary>
        /// <returns>The IP Address of this machine, in a String</returns>
        public String getIPAddress() {
            String hostName = Dns.GetHostName();
            IPHostEntry ipHostEntry = Dns.GetHostEntry(hostName);
            foreach (IPAddress ipAddr in ipHostEntry.AddressList) {
                Console.WriteLine(ipAddr.ToString());
            }
            return "LocalHost";
        }
    }
}
