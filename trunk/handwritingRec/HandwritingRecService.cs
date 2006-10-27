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
            // these calls have to happen before the form is constructed
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);

            // create the little popup (automatically minimize it after 3 seconds?)
            HWRecForm form = new HWRecForm();

            // Fire up the HWRecognition Server
            server = new HWServer(form);

            ApplicationContext context = new ApplicationContext();
            context.MainForm = form;

            Application.Run(context);
        }
    }
}