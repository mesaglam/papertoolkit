using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace HandwritingRecognition {
    public partial class HWRecForm : Form {

        // This delegate enables asynchronous calls for setting
        // the text property on a TextBox control.
        delegate void AddTextCallback(string text);

        public HWRecForm() {
            InitializeComponent();
        }

        /// <summary>
        /// When it pops up, hide it!
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void HWRecForm_Load(object sender, EventArgs e) {
            Console.WriteLine("Loading...");
            WindowState = FormWindowState.Minimized;
            Hide();
        }

        /// <summary>
        /// Added through the property sheet.
        /// 
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void HWRecForm_Resize(object sender, EventArgs e) {
            if (WindowState == FormWindowState.Minimized) {
                Hide();
            }
        }
        /// <summary>
        /// 
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void trayIcon_DoubleClick(object sender, EventArgs e) {
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
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void restoreToolStripMenuItem_Click(object sender, EventArgs e) {
            restoreWindow();
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
        private void HWRecForm_FormClosing(object sender, FormClosingEventArgs e) {
            exit();
        }


        /// <summary>
        /// 
        /// </summary>
        private void exit() {
            Console.WriteLine("Exiting...");
            Application.Exit();
            System.Environment.Exit(0);
        }

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
                textBox.Text = text + "\r\n" + textBox.Text;
                Refresh();
            }
        }
    }
}