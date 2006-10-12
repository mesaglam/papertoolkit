using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace HandwritingRecognition {
    public partial class HWRecForm : Form {
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
    }
}