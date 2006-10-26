using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.IO;
using Microsoft.Ink;

namespace HandwritingRecognition {
    static class HandwritingRecService {

        private static HWServer server;

        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        public static void Main() {

            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            HWRecForm form = new HWRecForm();

            // Fire up the HWRecognition Server
            server = new HWServer(form);

            Application.Run(form);
        }
    }
}