using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.IO;

namespace HandwritingRecognition {
    static class HandwritingRecService {

        private static HWServer server;

        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        public static void Main() {

            // Fire up the HWRecognition Server
            //server = new HWServer();

            //Application.EnableVisualStyles();
            //Application.SetCompatibleTextRenderingDefault(false);
            //Application.Run(new HWRecForm());

            String text = File.ReadAllText(@"C:\Documents and Settings\Ron Yeh\My Documents\Projects\PaperToolkit\penSynch\data\XML\2006_10_25__10_49_17.xml");
            new Recognizer().getStrokesFromXML(text);
        }
    }
}