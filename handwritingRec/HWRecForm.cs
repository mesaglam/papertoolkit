using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Threading;

namespace HandwritingRecognition {
    public partial class HWRecognitionForm : Form {

        // This delegate enables asynchronous calls for setting
        // the text property on a TextBox control.
        delegate void AddTextCallback(string text);

        public HWRecognitionForm() {
            InitializeComponent();
        }

        /// <summary>
        /// When it pops up, hide it!
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void formLoad(object sender, EventArgs e) {
            Console.WriteLine("Loading...");
            WindowState = FormWindowState.Minimized;
            ShowInTaskbar = false;
            trayIcon.ShowBalloonTip(0);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void formClosing(object sender, FormClosingEventArgs e) {
            exit();
        }

        /// <summary>
        /// Added through the property sheet.
        /// 
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void formResize(object sender, EventArgs e) {
            if (WindowState == FormWindowState.Minimized) {
                Hide();
            }
        }

        private int lineCount = 0;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="text"></param>
        internal void addTextSafely(String text) {
            if (textBox.InvokeRequired) {
                //Console.WriteLine("Invoke Required");
                AddTextCallback d = new AddTextCallback(addTextSafely);
                Invoke(d, new object[] { text });
            }
            else {
                //Console.WriteLine("Invoke NOT Required");
                textBox.Text = lineCount++ + "\t" + text + "\r\n" + textBox.Text;
                Refresh();
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void closeApplicationToolStripMenuItem_Click(object sender, EventArgs e) {
            exit();
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void minimizeButton_Click(object sender, EventArgs e) {
            WindowState = FormWindowState.Minimized;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void restoreToolStripMenuItem_Click(object sender, EventArgs e) {
            restoreWindow();
        }

        /// <summary>
        /// 
        /// </summary>
        private void restoreWindow() {
            Show();
            WindowState = FormWindowState.Normal;
        }

        /// <summary>
        /// 
        /// </summary>
        public void exit() {
            trayIcon.Visible = false;
            Console.WriteLine("Exiting...");
            Application.Exit();
            System.Environment.Exit(0);
        }


        /// <summary>
        /// Toggles Window State.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void trayIcon_MouseDoubleClick(object sender, MouseEventArgs e) {
            if (Visible) {
                WindowState = FormWindowState.Minimized;
                Hide();
            }
            else {
                restoreWindow();
            }
        }
    }
}