using System;
using System.Collections.Generic;
using System.Text;
using System.Collections;
using System.Net;
using System.Net.Sockets;
using System.Diagnostics;
using System.IO;
using System.Threading;
using System.Text.RegularExpressions;

namespace HandwritingRecognition {

    class HWServer {
        /// <summary>
        /// How many clients have connected?
        /// </summary>
        private int clientCount = 0;

        /// <summary>
        /// Connect to localhost:9898 to use Handwriting Recognition.
        /// </summary>
        private int portNumber = 9898;

        /// <summary>
        /// 
        /// </summary>
        public HWServer() {
            ThreadStart threadDelegate = new ThreadStart(listenForConnections);
            Thread thread = new Thread(threadDelegate);
            thread.Start();
        }

        /// <summary>
        /// 
        /// </summary>
        private void listenForConnections() {
            Console.WriteLine("Handwriting Recognition Server Started...");

            try {
                TcpListener tcpListener = new TcpListener(IPAddress.Loopback, portNumber);
                tcpListener.Start();

                while (true) {

                    // Accept a new connection
                    Socket socketForClient = tcpListener.AcceptSocket();
                    Console.WriteLine("Socket Accepted...");

                    if (socketForClient.Connected) {
                        Console.WriteLine("Client Connected");

                        ClientWorkerThread worker = new ClientWorkerThread(socketForClient, clientCount++);
                        ThreadStart clientDelegate = new ThreadStart(worker.talkToClient);
                        Thread workerThread = new Thread(clientDelegate);
                        workerThread.Start();
                    }
                    else {
                        socketForClient.Close();
                        Console.WriteLine("Disconnecting from Client.");
                    }
                }
            }
            catch (SocketException se) {
                Console.WriteLine(se.Message);
            }
        }
    }

    /// <summary>
    /// 
    /// </summary>
    class ClientWorkerThread {
        private Socket client;
        private int id;

        public ClientWorkerThread(Socket clientSocket, int clientID) {
            client = clientSocket;
            id = clientID;
        }

        /// <summary>
        /// 
        /// 
        /// </summary>
        public void talkToClient() {
            while (true) {
                NetworkStream networkStream = new NetworkStream(client);
                StreamWriter streamWriter = new StreamWriter(networkStream);
                StreamReader streamReader = new StreamReader(networkStream);

                // blocking call to wait for input
                string line = streamReader.ReadLine();
                Console.WriteLine("Read from Client " + id + ": " + line);

                // get the command from the client
                Match match = Regex.Match(line, @"\[\[(.*?)\]\].*");
                if (match.Success) {
                    String command = match.Groups[1].ToString().ToLower();
                    Console.WriteLine("Matched: " + command);
                    switch (command) {
                        case "exit":
                            Console.WriteLine("Disconnecting from Client " + id + ".");
                            client.Close();
                            return;
                        case "quitserver":
                            Console.WriteLine("Disconnecting from Client " + id + ".");
                            client.Close();
                            Console.WriteLine("Exiting the Server.");
                            System.Environment.Exit(0);
                            return;
                        default:
                            break;
                    }
                }
                else {
                    // assume it's just some XML
                    Console.WriteLine("Some XML....");


                    // respond with some ASCII

                }


                int numChars = line.Length;
                string response = "There were " + numChars + " characters in your message [" + line + "]";
                streamWriter.WriteLine(response);
                streamWriter.Flush();
                Console.WriteLine("Wrote: " + response);
            }
        }

    }
}
