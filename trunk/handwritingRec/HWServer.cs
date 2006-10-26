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
using Microsoft.Ink;
using System.Windows.Forms;

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

        private HWRecForm gui;

        /// <summary>
        /// 
        /// </summary>
        public HWServer(HWRecForm parentForm) {
            gui = parentForm;
            ThreadStart threadDelegate = new ThreadStart(listenForConnections);
            Thread thread = new Thread(threadDelegate);
            thread.Start();
        }

        /// <summary>
        /// 
        /// </summary>
        private void listenForConnections() {
            try {
                TcpListener tcpListener = new TcpListener(IPAddress.Loopback, portNumber);
                tcpListener.Start();
                Console.WriteLine("Handwriting Recognition Server Started...");

                while (true) {

                    // Accept a new connection
                    Socket socketForClient = tcpListener.AcceptSocket();
                    Console.WriteLine("Socket Accepted...");

                    if (socketForClient.Connected) {
                        Console.WriteLine("Client Connected");

                        ClientWorkerThread worker = new ClientWorkerThread(socketForClient, clientCount++, gui);
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
        private HWRecForm gui;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="clientSocket"></param>
        /// <param name="clientID"></param>
        /// <param name="theGui"></param>
        public ClientWorkerThread(Socket clientSocket, int clientID, HWRecForm theGui) {
            client = clientSocket;
            id = clientID;
            gui = theGui;
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
                    gui.addTextSafely("Command: " + command);

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
                    Recognizer rec = new Recognizer();
                    Strokes strokes = rec.getStrokesFromXML(line);
                    RecognitionAlternates alternatives;
                    String topResult = rec.recognize(strokes, out alternatives);
                    Console.WriteLine("The Top Result is: " + topResult);
                    //Console.WriteLine("Here is the complete list of alternates: ");
                    //if (alternatives != null) {
                    //    for (int i = 0; i < alternatives.Count; i++) {
                    //        Console.WriteLine(alternatives[i].ToString() + "\t" + alternatives[i].Confidence);
                    //    }
                    //}

                    // respond with some ASCII
                    streamWriter.WriteLine(topResult);
                    streamWriter.Flush();
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
