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

        private static String SERVER_STARTING_MSG = "Handwriting Recognition Server Starting...";

        /// <summary>
        /// How many clients have connected?
        /// </summary>
        private int clientCount = 0;

        /// <summary>
        /// Connect to localhost:9898 to use Handwriting Recognition.
        /// </summary>
        private int portNumber = 9898;

        private HWRecognitionForm gui;

        /// <summary>
        /// 
        /// </summary>
        public HWServer(HWRecognitionForm parentForm) {
            gui = parentForm;
            ThreadStart threadDelegate = new ThreadStart(listenForConnections);
            Thread thread = new Thread(threadDelegate);
            thread.Start();
        }

        /// <summary>
        /// Write both to the window and to the console...
        /// </summary>
        /// <param name="message"></param>
        private void log(String message) {
            gui.addTextSafely(message);
        }

        /// <summary>
        /// 
        /// </summary>
        private void listenForConnections() {
            try {
                TcpListener tcpListener = new TcpListener(IPAddress.Loopback, portNumber);
                log(SERVER_STARTING_MSG);
                tcpListener.Start();
                Console.WriteLine("[[serverstarted]] on port " + portNumber);

                while (true) {

                    // Accept a new connection
                    log("The server is waiting for a client to connect.");
                    Socket socketForClient = tcpListener.AcceptSocket(); // blocks here

                    if (socketForClient.Connected) {
                        log("A new client connected.");

                        ClientWorkerThread worker = new ClientWorkerThread(socketForClient, clientCount++, gui);
                        ThreadStart clientDelegate = new ThreadStart(worker.talkToClient);
                        Thread workerThread = new Thread(clientDelegate);
                        workerThread.Start();
                    }
                    else {
                        socketForClient.Close();
                        log("Disconnecting from client.");
                    }
                }
            }
            catch (SocketException se) {
                log(se.Message);
            }
        }
    }

    /// <summary>
    /// 
    /// </summary>
    class ClientWorkerThread {
        private Socket client;
        private int id;
        private HWRecognitionForm gui;

        /// <summary>
        /// The top ten alternatives from the most recent recognition request.
        /// </summary>
        private RecognitionAlternates alternatives;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="clientSocket"></param>
        /// <param name="clientID"></param>
        /// <param name="theGui"></param>
        public ClientWorkerThread(Socket clientSocket, int clientID, HWRecognitionForm theGui) {
            client = clientSocket;
            id = clientID;
            gui = theGui;
        }

        /// <summary>
        /// Write both to the window and to the console...
        /// </summary>
        /// <param name="message"></param>
        private void log(String message) {
            // Console.WriteLine(message);
            gui.addTextSafely(message);
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

                String shortMessage;
                if (line.Length > 20) {
                    shortMessage = line.Substring(0, 20) + "...";
                }
                else {
                    shortMessage = line;
                }
                log("Read from Client " + id + ": " + shortMessage);

                // get the command from the client
                Match match = Regex.Match(line, @"\[\[(.*?)\]\].*");
                if (match.Success) {
                    String command = match.Groups[1].ToString().ToLower();
                    log("Command: " + command);

                    switch (command) {
                        case "exit":
                            log("Disconnecting from Client " + id + ".");
                            client.Close();
                            return;
                        case "quitserver":
                            log("Disconnecting from Client " + id + ".");
                            client.Close();
                            log("Exiting the Server.");
                            gui.exit();
                            return;
                        case "topten":
                            // return nothing if there was no last call...
                            if (alternatives == null) {
                                // do nothing
                            }
                            else {
                                // return the top ten list of the last call...
                                log("Here is the complete list of alternates: ");
                                if (alternatives != null) {
                                    for (int i = 0; i < alternatives.Count; i++) {
                                        log(alternatives[i].ToString() + "  [" + alternatives[i].Confidence + "]");
                                        streamWriter.WriteLine(alternatives[i].ToString());
                                    }
                                    streamWriter.WriteLine("[[endofalternatives]]");
                                    streamWriter.Flush();
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
                else {
                    // assume it's just some XML
                    log("XML data received...");
                    Recognizer rec = new Recognizer();
                    Strokes strokes = rec.getStrokesFromXML(line);
                    alternatives = null;
                    String topResult = rec.recognize(strokes, out alternatives);
                    log("The top result is: " + topResult);

                    // respond with some ASCII
                    streamWriter.WriteLine(topResult);
                    streamWriter.Flush();
                }
            }
        }

    }
}
