using System;
using System.Collections.Generic;
using System.Windows.Forms;

namespace HandwritingRecognition {
    static class HandwritingRecService {

        private static HWServer server;

        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        public static void Main() {

            // Fire up the HWRecognition Server
            server = new HWServer();

            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new HWRecForm());

        }
    }
}