using System;
using System.IO;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using System.Diagnostics;

using ANOTOMAESTROREGISTRATION;

/// Author: Ron B. Yeh
/// May 2006 for BNet
/// Edited August 2006 for R3
///
/// Run this to hook up Anoto with the COM class (PenMonitor).
/// Make sure the COM class is registered correctly in the Windows registry
/// before doing this.
///
namespace PenMonitor {
    class Register {

        /// <summary>
        /// The binaries lives in bin, and the penSynch directory is one up (the parent).
        /// </summary>
        private static string rootDir = @"..\";

        private static string APP_NAME = "R3PenMonitor";

        /// <summary>
        /// Run this reg file to register the monitor DLL.
        /// </summary>
        private static string regFileName = "RegisterClassIDProgIDMappings.reg";

        private static string regFileTemplateName = "RegisterClassIDProgIDMappingsTemplate.reg";

        /// <summary>
        /// uses the Anoto SDK to register the Monitor.
        /// </summary>
        public static void Main() {
            // generate the correct reg file from the template
            createRegistryFile();

            // register the .REG file, blocking until the user finishes
            Process proc = Process.Start(rootDir + regFileName);
            proc.WaitForExit();
            Console.WriteLine("Done with the Windows Registry");

            // do all the hooking up between anoto and com object
            processAnotoRegistrations();
        }

        /// <summary>
        /// Automatically create the correct registry file for registering the COM object.
        /// </summary>
        private static void createRegistryFile() {
            // register the COM class
            // in the reg file, replace ____PATH____ with the correct file URL
            // e.g., "file:///C:/{...omitted...}/bin/Pen Monitor.dll"
            // == \bin\Pen Monitor.dll

            // find the reg file first
            // ../../../RegisterClassIDProgIDMappings.reg
            string regFileContents = File.ReadAllText(rootDir + regFileTemplateName);
            //Console.WriteLine(regFileContents);

            // find correct file URL
            string currentWorkingDirectory = Directory.GetCurrentDirectory();
            string penSynchMonitorDir = Directory.GetParent(".").FullName;
            string penMonitorDLL = penSynchMonitorDir + @"\bin\Pen Monitor.dll";

            // replace \ with /
            // add the file:/// in front
            string penMonitorURL = "file:///" + penMonitorDLL.Replace("\\", "/");


            // replace all instances of "____PATH____" with the URL
            string regFileContentsReplaced = regFileContents.Replace("____PATH____", penMonitorURL);
            regFileContentsReplaced = regFileContentsReplaced.Replace("____PENREQUESTSDIR____", penSynchMonitorDir.Replace(@"\", @"\\") + @"\\data\\");

            //Console.WriteLine(currentWorkingDirectory);
            //Console.WriteLine();
            //Console.WriteLine(penMonitorURL);

            Console.WriteLine(regFileContentsReplaced);
            File.WriteAllText(rootDir + regFileName, regFileContentsReplaced);
        }

        /// <summary>
        /// 
        /// 
        /// </summary>
        private static void processAnotoRegistrations() {
            // hook up Anoto with COM
            Console.WriteLine("Registering the Pen Monitor.");
            RegistrationManager2 reg = null;
            try {
                reg = new RegistrationManager2();
                reg.RegisterApplication(APP_NAME);
            }
            catch (COMException e) {
                Console.WriteLine("Handling COM Exception.");
                Console.WriteLine(e.ErrorCode);
            }
            Console.WriteLine("Done!");


            Console.WriteLine("Registering PAD files.");

            // find all *.pad files in "..\PADs\"
            string[] files = Directory.GetFiles(@"..\PADs\", "*.pad");

            foreach (string padFilePath in files) {
                Console.WriteLine(padFilePath);
                reg.RegisterPaper(padFilePath);
            }

            Console.WriteLine("Done Processing Anoto Registrations!");
        }
    }
}
