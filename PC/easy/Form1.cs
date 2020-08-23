using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace easy
{
    public partial class Form1 : Form
    {
        public socket soc = new socket();
        public Thread thread1;
        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
         
            //string hostname = Dns.GetHostName();
            //IPHostEntry localhost = Dns.GetHostByName(hostname);
            //IPAddress localaddr = localhost.AddressList[3];
            System.Windows.Forms.Control.CheckForIllegalCrossThreadCalls = false;

            //label4.Text = localaddr.ToString();

            try
            {
                Process cmd = new Process();
                cmd.StartInfo.FileName = "ipconfig.exe";//设置程序名
                cmd.StartInfo.Arguments = "/all";  //参数
                                                   //重定向标准输出
                cmd.StartInfo.RedirectStandardOutput = true;
                cmd.StartInfo.RedirectStandardInput = true;
                cmd.StartInfo.UseShellExecute = false;
                cmd.StartInfo.CreateNoWindow = true;//不显示窗口（控制台程序是黑屏）
                                                    //cmd.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;          //暂时不明白什么意思
                /*
                收集一下 有备无患
                关于:ProcessWindowStyle.Hidden隐藏后如何再显示？
                hwndWin32Host = Win32Native.FindWindow(null, win32Exinfo.windowsName);
                Win32Native.ShowWindow(hwndWin32Host, 1);          //先FindWindow找到窗口后再ShowWindow
                */
                cmd.Start();
                string info = cmd.StandardOutput.ReadToEnd();
                cmd.WaitForExit();
                cmd.Close();
                if (info.Contains("WLAN"))
                {
                    info = info.Substring(info.IndexOf("WLAN"));
                    info = info.Substring(info.IndexOf("IPv4"));
                    info = info.Substring(info.IndexOf(":") + 1);
                    int end = info.IndexOf("(");
                    info = info.Substring(0, end);
                    label4.Text = info;
                }
                else
                {
                    info = info.Substring(info.IndexOf("无线"));
                    info = info.Substring(info.IndexOf("IPv4"));
                    info = info.Substring(info.IndexOf(":") + 1);
                    int end = info.IndexOf("(");
                    info = info.Substring(0, end);
                    label4.Text = info;
                }
            }
            catch (System.NullReferenceException)
            {
                //do nothing
            }
            label2.Text = "等待连接中···";
            Program.f.label2.ForeColor = Color.Black;
            soc = new socket();
            thread1 = new Thread(new ThreadStart(soc.connect));
            thread1.Start();
            Console.WriteLine(soc.ToString());

        }
        private void button2_Click(object sender, EventArgs e)
        {
            DialogResult rs = openFileDialog1.ShowDialog();
            if (rs == System.Windows.Forms.DialogResult.OK)
            {
                String file = openFileDialog1.FileName;
                if (File.Exists(file))
                {
                    textBox2.Text = file;
                }
               
            }

        }

        private void panel1_DragDrop(object sender, DragEventArgs e)
        {
            String file = ((System.Array)e.Data.GetData(DataFormats.FileDrop)).GetValue(0).ToString();
            if (File.Exists(file))
            {
                textBox2.Text = file;
            }
        }

        private void panel1_DragEnter(object sender, DragEventArgs e)
        {
            if (e.Data.GetDataPresent(DataFormats.FileDrop))
            {
                e.Effect = DragDropEffects.Link;
            }
            else
            {
                e.Effect = DragDropEffects.None;
            }
        }

        private void Form1_FormClosed(object sender, FormClosedEventArgs e)
        {
            if(soc.client_socket!=null)
            soc.client_socket.Close();
            if(soc.server_socket!=null)
            soc.server_socket.Close();
            soc.close();
            System.Environment.Exit(0);
        }

        private void button1_Click(object sender, EventArgs e)
        {
            if (!Program.f.textBox2.Text.Equals("")&&soc.is_connect!=0)//判断是否为空值
            {
                if (textBox2.Text.Length>2&&textBox2.Text.ElementAt(1) == ':' && textBox2.Text.ElementAt(0) >= 'A')//判断是否为文件
                {
                    Console.WriteLine("is a file");
                    soc.send_file(Program.f.textBox2.Text);
                    Program.f.textBox2.Text = "";
                }
                else
                {
                    soc.send(Program.f.textBox2.Text);
                    Program.f.textBox2.Text = "";
                }
            }
        }

        private void label5_Click(object sender, EventArgs e)
        {

        }

        private void button3_Click(object sender, EventArgs e)//刷新按钮点击事件
        {
            new Form2("已刷新，请移动端重新连接~").ShowDialog();
            Program.f.label2.Text = "等待连接中···";
            Program.f.label2.ForeColor = Color.Black;

            soc.close();
            
            thread1.Abort();
            Console.WriteLine(thread1.IsAlive);
            soc = new socket();
            thread1 = new Thread(new ThreadStart(soc.connect));
            thread1.Start();
        }
        //实现原理是  将原本的socket 关掉 让后将原来的线程指向一个新线程

        private void Form1_FormClosing(object sender, FormClosingEventArgs e)
        {
            soc.close();
           
        }

        private void textBox1_Click(object sender, EventArgs e)//点击接收栏自动复制
        {
            if (!textBox1.Text.Equals("")){
                Clipboard.SetDataObject(textBox1.Text);
                toolTip1.Show("已复制",textBox1,1000);
                textBox1.Text = "";
            }
        }

        private void toolTip1_Popup(object sender, PopupEventArgs e)
        {

        }

        private void button4_Click(object sender, EventArgs e)//打开文件夹
        {
            System.Diagnostics.Process.Start("D://Easy");
        }
    }
}
