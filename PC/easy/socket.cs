
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Drawing;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Windows.Forms;

/*******************
* 作者 ：Ric Li
*
* 版本：V1.0
*
* 创建时间：2020/6/23 22:58:21
*
*******************/
namespace easy
{
    public class socket
    {
        public static init_socket init;
       public   Socket server_socket;
        public  Socket client_socket;
        public static String Path;
        public static String file_name;
        public  int is_connect = 0;
        public void  connect()//创建一个socket并一直监听接收消息
        {
            try
            {
                server_socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                IPEndPoint ipEndpoint = new IPEndPoint(IPAddress.Parse(Program.f.label4.Text.Trim()), 3721);
                server_socket.Bind(ipEndpoint);
                server_socket.Listen(10);

                try
                {
                    client_socket = server_socket.Accept();
                    is_connect = 1;
                }
                catch (System.Net.Sockets.SocketException e)
                {
                    //do nothing
                }
                try
                {
                    if (client_socket.Connected)
                    {
                        init_socket init_Socket = new init_socket(client_socket);
                        Thread thread = new Thread(new ParameterizedThreadStart(init_Socket.send));
                        thread.Start("is_there_u");
                        if (init_Socket.recive().Contains("yes_its_me"))
                        {

                            Program.f.label2.Text = "移动端连接成功";
                            Program.f.label2.ForeColor = Color.Green;
                        }
                        //Thread thread1 = new Thread(new ThreadStart(init_Socket.recive));
                        //thread1.Start();

                        //new Thread(new ThreadStart(new init_socket(client_socket).send)).Start();
                        init = new init_socket(client_socket);
                        while (true)
                        {
                            try
                            {
                                String str = rec();
                                if (!str.Contains("ok_rec_file")&&!str.Contains("file_name@"))
                                {
                                    Program.f.textBox1.Text = str;
                                }else if (str.Contains("file_name@"))
                                {
                                    String name = str.Substring(10,str.LastIndexOf("@")-str.IndexOf("@")-1);

                                    Console.WriteLine(name);
                                    String size = str.Substring(str.LastIndexOf("@") + 1);
                                    Console.WriteLine("size" + size);
                                   

                                    rec_file(client_socket,name,size);
                                }
                                else
                                {
                                    try
                                    {
                                        client_socket.SendFile(Path);
                                    }
                                    catch (System.Net.Sockets.SocketException e)
                                    {
                                        Application.ExitThread();
                                        //Restart();

                                    }
                                    Console.WriteLine("*******************your file already send");
                                    new Form2("文件 " +file_name +  " 已发送~").ShowDialog();
                                }
                            }
                            catch (ThreadInterruptedException e)
                            {
                                break;
                            }
                            catch (System.NullReferenceException)
                            {
                                //donothing
                            }
                        }

                    }
                }
                catch (System.NullReferenceException e)
                {
                    
                    //do nothing
                }
            }catch(Exception e)
            {
                return;
            }
            
        }

        private void Restart()
        {
            Thread thtmp = new Thread(new ParameterizedThreadStart(run));
            object appName = Application.ExecutablePath;
            Thread.Sleep(2000);
            thtmp.Start(appName);
        }

        private void run(Object obj)
        {
            Process ps = new Process();
            ps.StartInfo.FileName = obj.ToString();
            ps.Start();
        }
        public void send(String msg )
        {
            init.send(msg);
        }
        public String rec()
        {
          return   init.recive();
        }
        //public void send()
        //{
        //    new Thread(new ThreadStart(new init_socket(client_socket).send)).Start();
        //}

        public void send_file(String path)
        {
            try
            {
                init.send_file(path);
            }
            catch (System.NullReferenceException)
            {
                Program.f.label2.Text = "连接出错了";
                Program.f.label2.ForeColor = Color.Red;
            }
            }
        public void close()
        {
            if(server_socket!=null)
            server_socket.Close();
            if(client_socket!=null)
            client_socket.Close();
        }
        public void rec_file(Socket socket,String name,String length )
        {
            init.rec_file(socket, name,length);
        }

     
    }
}
