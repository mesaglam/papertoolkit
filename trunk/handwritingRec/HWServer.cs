using System;
using System.Collections.Generic;
using System.Text;
using System.Collections;
using System.Net;

namespace HandwritingRecognition {
    class HWServer {

        /// <summary>
        /// A thread-safe list of worker sockets (one per client).
        /// </summary>
        private ArrayList workerSocketList = ArrayList.Synchronized(new ArrayList());



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
