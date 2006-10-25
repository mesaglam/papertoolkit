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

            // Fire up the HWRecognition Server
            if (true) {
                server = new HWServer();
            }
            if (false) {
                Application.EnableVisualStyles();
                Application.SetCompatibleTextRenderingDefault(false);
                Application.Run(new HWRecForm());
            }

            if (false) {
                String text = File.ReadAllText(@"C:\Documents and Settings\Ron Yeh\My Documents\Projects\PaperToolkit\penSynch\data\XML\2006_10_25__10_49_17.xml");
                Recognizer rec = new Recognizer();
                Strokes strokes = rec.getStrokesFromXML(text);
                RecognitionAlternates alternatives;
                String topResult = rec.recognize(strokes, out alternatives);
                Console.WriteLine("The Top Result is: " + topResult);
                Console.WriteLine("Here is the complete list of alternates: ");
                if (alternatives != null) {
                    for (int i = 0; i < alternatives.Count; i++) {
                        Console.WriteLine(alternatives[i].ToString() + "\t" + alternatives[i].Confidence);
                    }
                }
            }
        }
    }
}