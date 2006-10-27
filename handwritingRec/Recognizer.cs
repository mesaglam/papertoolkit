using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using Microsoft.Ink;
using System.Xml;
using System.IO;
using System.Drawing;

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

            try {

                // object to parse the xml data
                StringReader stringReader = new StringReader(xmlString);
                XmlReader reader = XmlReader.Create(stringReader);

                // skip the junk nodes at the top of xml files
                reader.MoveToContent();

                while (reader.Read()) { // read a tag
                    String nodeNameLowerCase = reader.Name.ToLower();
                    switch (nodeNameLowerCase) { // name of the node
                        case "stroke":
                            // Console.WriteLine("<Stroke>");
                            Stroke stroke = handleStroke(reader, ink);
                            //if (stroke != null) {
                            //    Console.WriteLine(stroke.GetPoints().Length + " points in this stroke.");
                            //}
                            break;
                        default:
                            break;
                    }
                }
            }
            catch (XmlException xe) {
                Console.WriteLine("Exception in parsing XML. " + xe.Message);
            }
            Console.WriteLine(ink.Strokes.Count + " total strokes.");
            return ink.Strokes;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="reader"></param>
        /// <param name="ink"></param>
        private Stroke handleStroke(XmlReader reader, Ink ink) {
            // new empty list to store the points
            List<Point> stroke = new List<Point>();

            while (reader.Read()) {
                String nodeNameLowerCase = reader.Name.ToLower();
                switch (nodeNameLowerCase) {
                    case "p":
                        // C# Seems to use Bankers' Rounding
                        int x = (int)Math.Round(Double.Parse(reader.GetAttribute("x")));
                        int y = (int)Math.Round(Double.Parse(reader.GetAttribute("y")));
                        //Console.WriteLine("Sample: {0} {1} {2} {3}", x, y, reader.GetAttribute("f"), reader.GetAttribute("t"));
                        stroke.Add(new Point(x, y));
                        break;
                    case "stroke":
                        //Console.WriteLine("</Stroke>");
                        Point[] strokesArray = stroke.ToArray();
                        Stroke inkStroke = ink.CreateStroke(strokesArray);
                        return inkStroke;
                    default:
                        break;
                }
            }
            return null;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="strokes"></param>
        internal String recognize(Strokes strokes, out RecognitionAlternates alternates) {
            RecognizerContext rc = new RecognizerContext();
            rc.Strokes = strokes;

            try {
                RecognitionStatus status;
                RecognitionResult result;
                result = rc.Recognize(out status);
                if (status == RecognitionStatus.NoError) {
                    RecognitionAlternates alternatives = result.GetAlternatesFromSelection();
                    alternates = alternatives;
                    return result.TopString;
                }
                else {
                    Console.WriteLine("Error in recognition.");
                }
            }
            catch {
                Console.WriteLine("Exception in recognition.");
            }
            alternates = null;
            return null;
        }
    }
}
