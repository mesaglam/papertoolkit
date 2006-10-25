using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Ink;
using System.Xml;
using System.IO;

namespace HandwritingRecognition {

    /// <summary>
    /// 
    /// </summary>
    class Recognizer {

        /// <summary>
        /// Create a Strokes object out of R3 Toolkit XML (superset of BNet XML)
        /// </summary>
        /// <param name="stream"></param>
        /// <returns></returns>
        public Strokes getStrokesFromXML(String xmlString) {

            // ink object for creating pen strokes
            InkCollector inkCollector = new InkCollector();
            Ink ink = new Ink();

            StringReader stringReader = new StringReader(xmlString);
            XmlReader reader = XmlReader.Create(stringReader);


            while (reader.Read()) {
                if (reader.IsStartElement()) {
                    if (reader.IsEmptyElement) {
                        Console.WriteLine("<{0}/>", reader.Name);
                    }
                    else {
                        Console.WriteLine("<{0}> ", reader.Name);
                    }
                }
                else {
                    Console.WriteLine("</{0}>", reader.Name);
                }
            }


            return ink.Strokes;
        }


    }
}
