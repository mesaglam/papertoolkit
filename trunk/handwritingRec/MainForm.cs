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
        delegate void SetTextCallback(string text);

        public HWRecForm() {
            InitializeComponent();
        }

        private void HWRecForm_Load(object sender, EventArgs e) {

        }

        /// <summary>
        /// Added through the property sheet.
        /// 
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void HWRecForm_Resize(object sender, EventArgs e) {
            if (FormWindowState.Minimized == WindowState) {
                Hide();
            }   
        }

        private void trayIcon_DoubleClick(object sender, EventArgs e) {
            restoreWindow();
        }

        private void restoreWindow() {
            Show();
            WindowState = FormWindowState.Normal;
        }

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
        internal void setTextSafely(string text) {
            if (textBox.InvokeRequired) {
                Console.WriteLine("Invoke Required");
                SetTextCallback d = new SetTextCallback(setTextSafely);
                Invoke(d, new object[] { text });
            }
            else {
                Console.WriteLine("Invoke NOT Required");
                textBox.Text = text + "\r\n" + textBox.Text;
                Refresh();
            }
        }
    }
}