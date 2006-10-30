using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace PenMonitor {

    /// <summary>
    /// 
    /// </summary>
    public partial class MessageForm : Form {
        public MessageForm() {
            InitializeComponent();
        }

        private void closeButton_Click(object sender, EventArgs e) {
            Close();
        }

        private void MessageForm_Load(object sender, EventArgs e) {

        }

        /// <summary>
        /// Replace the text in the textbox with the parameter "text"
        /// </summary>
        /// <param name="text"></param>
        public void setText(string text) {
            outputTextBox.Text = text;
        }

        /// <summary>
        /// Adds text on a new line.
        /// </summary>
        /// <param name="text"></param>
        public void appendLine(string text) {
            outputTextBox.Text += Environment.NewLine + text;
            // repaint this control
            this.Refresh();
        }


        public void appendText(string text) {
            outputTextBox.Text += text;
        }
    }
}