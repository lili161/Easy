using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace easy
{
    public partial class Form2 : Form
    {
        String msg;
        public Form2(String msg)
        {
            this.msg = msg;
            InitializeComponent();
        }

        private void Form2_Load(object sender, EventArgs e)
        {
            label2.Text = msg;
        }

        private void button1_Click(object sender, EventArgs e)
        {
            this.Close();
        }
    }
}
