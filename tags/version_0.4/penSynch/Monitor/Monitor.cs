using System;
using System.Diagnostics;
using System.Runtime.InteropServices;
using System.IO;
using System.Collections;
using System.Xml;

using Microsoft.Win32;

using Anoto.Service;
using Anoto.Common;

///
/// This Pen Monitor is called by Anoto Pen Drivers. It will invoke ButterflyNet/R3, passing the request file along.
/// BNet performs the actual pen synch import.
///
namespace PenMonitor {

    // used for COM
    [Guid("A5304E70-BE2B-4070-94EA-1B2BFEBB1165")]
    [ClassInterface(ClassInterfaceType.None)]
    [ProgId("R3PenMonitor")]

    /// The Anoto pen drivers will talk to this
    public class Monitor : Anoto.Notification.IDataReceiver {

        /// <summary>
        /// The penRequests directory.
        /// </summary>
        private static string penRequestDir;

        private static string universalTimeString = "UniversalTime";

        private static string nowString = "NowTime";

        private static MessageForm form;

        /// <summary>
        /// Where the latest request was saved to.
        /// Pass this into ButterflyNet so it can process it.
        /// </summary>
        private static string requestXMLFilePath;


        //
        // This is the Notify method that Anoto drivers expect to find.
        //
        void Anoto.Notification.IDataReceiver.Notify(string category, object data) {
            // display it in a message box
            form = new MessageForm();
            form.Show();
            form.WindowState = System.Windows.Forms.FormWindowState.Minimized;

            // find the requests path from the windows registry
            // [HKEY_LOCAL_MACHINE\SOFTWARE\Classes\BNetPenMonitor]:"PenRequestsDir"
            RegistryKey key = Registry.LocalMachine.OpenSubKey(@"SOFTWARE\Classes\R3PenMonitor");
            penRequestDir = (string)key.GetValue("PenRequestsDir", "DefaultValue");
            form.setText("Saving Pen Information to: " + penRequestDir);

            // time stamp information
            // universal time of this request
            universalTimeString = DateTime.Now.ToUniversalTime().ToString();
            // get the current time stamp in a sortable fashion
            nowString = DateTime.Now.ToString("s").Replace("T", "__").Replace(":", "_").Replace("-", "_");


            // Register that a Request happened in the Log file
            StreamWriter log = createFileStreamAppend(penRequestDir + @"Requests.log");
            // log.WriteLine("Request at: " + nowString);


            // save the request file
            saveRequestFile(category, data, log);


            // flush & close the log file
            log.Flush();
            log.Close();

            form.appendLine("Pen Synchronization Complete! Closing this window...");


            // Run R3 to import the new strokes...
            string R3ImportAppPath = Directory.GetParent(penRequestDir).Parent.FullName + @"\bin\BatchImporter.exe";
            form.appendLine("Starting " + R3ImportAppPath + " " + requestXMLFilePath);
            // pass along the request file, and tell BNet to redirect the console to console.log
            Process.Start(R3ImportAppPath, '"' + requestXMLFilePath + '"');
        }

        /// <summary>
        /// Old Paper Todos Code. 
        /// </summary>
        /// <param name="category"></param>
        /// <param name="data"></param>
        void saveRequestFile(string category, object data, StreamWriter log) {

            // make the pen request from the PGC data
            PenRequest req = new PenRequest();
            req.Initialize(data);


            // put request information in an xml file (one per request)
            requestXMLFilePath = penRequestDir + @"XML\" + nowString + "_" + req.PenId + ".xml";

            // xml writer
            XmlTextWriter xml = new XmlTextWriter(requestXMLFilePath, null);


            // start the xml document
            xml.WriteStartDocument();
            xml.WriteStartElement("penRequest");
            xml.WriteStartElement("requestInformation");



            xml.WriteStartElement("universalTime");
            xml.WriteStartAttribute("time");
            xml.WriteValue(universalTimeString);
            xml.WriteEndElement();


            xml.WriteStartElement("localTime");
            xml.WriteStartAttribute("time");
            xml.WriteValue(DateTime.Now.ToString());
            xml.WriteEndElement();



            log.WriteLine();
            log.WriteLine("////////// Request at " + nowString);
            log.WriteLine("UniversalTime: " + universalTimeString);

            // more user feedback
            form.appendLine("Pen Synched on: " + DateTime.Now.ToString());


            // Save the data to a pgc file by using Anoto.Util
            // writePGCFile(data, todosPath, nowString);


            log.WriteLine("PenID: " + req.PenId);
            log.WriteLine("NumPages: " + req.Pages.Count);

            // more user feedback
            form.appendLine("Number of Pages in this Session: " + req.Pages.Count);

            xml.WriteStartElement("penID");
            xml.WriteStartAttribute("id");
            xml.WriteValue(req.PenId);
            xml.WriteEndElement(); // end penID

            xml.WriteStartElement("numPages");
            xml.WriteStartAttribute("num");
            xml.WriteValue(req.Pages.Count);
            xml.WriteEndElement(); // end penID

            xml.WriteEndElement(); // end requestInformation

            // get the pages
            RequestPages pages = req.Pages;

            // process page addresses
            Anoto.Util.PageAddress pageAddressUtil = new Anoto.Util.PageAddress();

            // go through all the pages
            IEnumerator pagesEnum = pages.GetEnumerator();
            int pageCount = 0;
            while (pagesEnum.MoveNext()) {
                xml.WriteStartElement("page");
                log.WriteLine("Page Object " + pageCount + ": ");
                Page page = (Page)pagesEnum.Current;
                log.WriteLine("\t" + page.PageAddress);

                // more user feedback
                form.appendLine("Processing Page with Address: " + page.PageAddress);

                // break down the page address
                int segment = pageAddressUtil.GetSegment(page.PageAddress);
                int shelf = pageAddressUtil.GetShelf(page.PageAddress);
                int book = pageAddressUtil.GetBook(page.PageAddress);
                int pageNum = pageAddressUtil.GetPage(page.PageAddress);
                int instance = pageAddressUtil.GetInstance(page.PageAddress);

                log.WriteLine("\t  Segment: " + segment);
                log.WriteLine("\t  Shelf: " + shelf);
                log.WriteLine("\t  Book: " + book);
                log.WriteLine("\t  Page: " + pageNum);

                xml.WriteStartAttribute("address");
                xml.WriteValue(segment + "." + shelf + "." + book + "." + pageNum);
                xml.WriteEndAttribute();
                xml.WriteStartAttribute("addressWithInstance");
                xml.WriteValue(page.PageAddress);
                xml.WriteEndAttribute();
                xml.WriteStartAttribute("segment");
                xml.WriteValue(segment + "");
                xml.WriteEndAttribute();
                xml.WriteStartAttribute("shelf");
                xml.WriteValue(shelf + "");
                xml.WriteEndAttribute();
                xml.WriteStartAttribute("book");
                xml.WriteValue(book + "");
                xml.WriteEndAttribute();
                xml.WriteStartAttribute("page");
                xml.WriteValue(pageNum);
                xml.WriteEndAttribute();

                // the instance N of the page in the request formatted as X.X.X.X#N 
                if (instance < 0) {
                    log.WriteLine("\t  Instance: NONE");
                }
                else {
                    log.WriteLine("\t  Instance: " + instance);
                }

                PenStrokes strokes = page.PenStrokes;
                log.WriteLine("\tPen Strokes Count: " + strokes.Count);
                Bounds b = strokes.Bounds;
                log.WriteLine("\t  Bounds Size: " + b.Width + " x " + b.Height);
                log.WriteLine("\t  Bounds[ltrb]: " + b.Left + " " + b.Top + " " + b.Right + " " + b.Bottom);

                xml.WriteStartAttribute("minX");
                xml.WriteValue(b.Left);
                xml.WriteStartAttribute("minY");
                xml.WriteValue(b.Top);
                xml.WriteStartAttribute("maxX");
                xml.WriteValue(b.Right);
                xml.WriteStartAttribute("maxY");
                xml.WriteValue(b.Bottom);

                // deal with all of the strokes
                processStrokes(strokes, log, xml);

                // next page
                pageCount++;

                xml.WriteEndElement();
            }


            // finish the xml document, Flush & Close
            xml.WriteEndDocument();
            xml.Flush();
            xml.Close();
        }


        /// <summary>
        /// Puts the stroke data into the XML file.
        /// </summary>
        private static void processStrokes(PenStrokes strokes, StreamWriter log, XmlTextWriter xml) {
            IEnumerator strokesEnum = strokes.GetEnumerator();

            // more user feedback
            form.appendLine("");

            while (strokesEnum.MoveNext()) {
                PenStroke stroke = (PenStroke)strokesEnum.Current;

                // more user feedback
                form.appendText(".");

                //log.WriteLine("\tPen Stroke");
                //log.WriteLine("\t  Duration: " + stroke.Duration);

                long strokeTimeMillis = ((long)stroke.StartSecond * 1000) + (long)stroke.StartMillisecond;
                long strokeTimeSystemMillis = ((long)stroke.SystemSecond * 1000) + (long)stroke.SystemMillisecond;

                xml.WriteStartElement("stroke");
                xml.WriteStartAttribute("begin");
                xml.WriteValue(strokeTimeMillis);
                xml.WriteStartAttribute("beginSystemTime");
                xml.WriteValue(strokeTimeSystemMillis);
                xml.WriteStartAttribute("duration");
                xml.WriteValue(stroke.Duration);
                xml.WriteStartAttribute("end");
                xml.WriteValue(strokeTimeMillis + stroke.Duration);
                xml.WriteStartAttribute("colorRGB");
                xml.WriteValue(getRGBString(stroke.Color));


                xml.WriteStartAttribute("minStrokeX");
                xml.WriteValue(stroke.Bounds.Left);
                xml.WriteStartAttribute("minStrokeY");
                xml.WriteValue(stroke.Bounds.Top);
                xml.WriteStartAttribute("maxStrokeX");
                xml.WriteValue(stroke.Bounds.Right);
                xml.WriteStartAttribute("maxStrokeY");
                xml.WriteValue(stroke.Bounds.Bottom);


                int numSamples = stroke.X.Length;
                float[] x = stroke.X;
                float[] y = stroke.Y;
                byte[] f = stroke.Force;
                int[] deltaMillis = stroke.DeltaTime;

                long startTimeMillis = strokeTimeMillis;

                for (int j = 0; j < numSamples; j++) {
                    xml.WriteStartElement("p");
                    // x
                    xml.WriteStartAttribute("x");
                    xml.WriteValue(x[j]);
                    // y
                    xml.WriteStartAttribute("y");
                    xml.WriteValue(y[j]);
                    // f
                    xml.WriteStartAttribute("f");
                    xml.WriteValue(f[j]);
                    // t
                    // move the time counter by delta
                    startTimeMillis += deltaMillis[j];
                    xml.WriteStartAttribute("t");
                    xml.WriteValue(startTimeMillis);

                    xml.WriteEndElement();
                }
                xml.WriteEndElement();
            }
        }

        //
        // Turns the Anoto RGB code into a string that we can parse later.
        //
        private static string getRGBString(int colorString) {
            int r = colorString & 0xFF;
            int g = (colorString >> 8) & 0xFF;
            int b = (colorString >> 16) & 0xFF;

            return r + "_" + g + "_" + b;
        }

        //
        // Makes a streamwriter so that we can output to a file (with specified path)
        //
        private static StreamWriter createFileStreamAppend(string filePath) {
            if (!File.Exists(filePath)) {
                FileStream fstream = File.Create(filePath);
                fstream.Close();
            }
            StreamWriter file = null;
            try {
                file = File.AppendText(filePath);
            }
            catch (Exception e) {
                Console.WriteLine(e.Message.ToString());
            }
            return file;
        }

        //
        // Makes a streamwriter so that we can output to a file (with specified path)
        //
        private static StreamWriter createFileStreamReplace(string filePath) {
            if (!File.Exists(filePath)) {
                FileStream fstream = File.Create(filePath);
                fstream.Close();
            }
            StreamWriter file = null;
            try {
                file = File.CreateText(filePath);
            }
            catch (Exception e) {
                Console.WriteLine(e.Message.ToString());
            }
            return file;
        }


        //
        // Output the PGC file for the request...
        //
        private static void writePGCFile(object data, string requestPath, string fileName) {
            Anoto.Util.File file = new Anoto.Util.FileClass();

            file.Write(requestPath + fileName + ".pgc", data);
        }

        // 
        // Specified in the PAD file. This will determine what type of PAD will invoke this handler.
        // 
        string Anoto.Notification.IDataReceiver.Category {
            get {
                return "fieldtools::R3";
            }
        }
    }
}
