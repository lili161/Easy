/*******************
* 作者 ：Ric Li
*
* 版本：V1.0
*
* 创建时间：2020/6/23 23:04:42
*
*******************/


using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net.Sockets;
using System.Text;

namespace easy
{
   public  class init_socket
    {
        public String Path;
        public int is_duplicate = 0;//是否重名 是=1 否=0
        const int buffer_size = 1024;
        Socket client_socket;
        public init_socket(Socket socket)
        {
            this.client_socket = socket;
            Console.WriteLine("a client line in");
            Console.WriteLine("client ip addr is " + client_socket.RemoteEndPoint.ToString());
        }
        public String recive()
        {
            int byte_s = 1024;
            Byte[] receive=new Byte[byte_s];
            int num=-1;
            try
            {
                num = client_socket.Receive(receive);
            }catch(System.Net.Sockets.SocketException)
            {
                //donothing
            }
            //int times,left;
            //if (num % byte_s == 0)
            //{
            //    times = num / byte_s;
            //    left = 0;
            //}
            //else{
            //    times = num / byte_s;
            //    times++;
            //    left = num % byte_s;
            //}
            //Console.WriteLine(num);
            String s_receive = "";
            try { 
            s_receive = Encoding.UTF8.GetString(receive, 0, num);
            }
            catch (System.ArgumentOutOfRangeException)
            {
                Program.f.label2.Text = "连接出错了！";
                Program.f.label2.ForeColor = Color.Red;
            }
            //for(int i = 0; i < times - 1; i++)
            //{
            //    if (i < times - 2)
            //    {
            //        client_socket.Receive(receive);
            //        s_receive += Encoding.UTF8.GetString(receive, 0, byte_s);
            //    }
            //    else
            //    {
            //        client_socket.Receive(receive);
            //        s_receive += Encoding.UTF8.GetString(receive, 0, left);
            //    }

            //}
      
           
        
          
            return s_receive;

        }
        public void send(object msg)
        {
            byte[] buffer = new byte[buffer_size];
            String file_path = Program.f.textBox2.Text;
            String file_name = file_path.Substring(file_path.LastIndexOf("\\")+1, file_path.Length - file_path.LastIndexOf('\\')-1 );
            byte[] b_msg = Encoding.UTF8.GetBytes(1+"\n"+msg.ToString()+"\n");
            int i = client_socket.Send(b_msg);
            Console.WriteLine("Sent {0} bytes.", i);
            //int a=  client_socket.Send(Encoding.UTF8.GetBytes("can_recive" + file_path + "," + file_name));
            //  Console.WriteLine("allready send!" + "can_recive" + file_path + "," + file_name+" "+a);
        }
        public void send_file(string path)
        {
            FileInfo fileInfo = new FileInfo(path.ToString());
            String file_name = path.Substring(path.LastIndexOf("\\") + 1, path.Length - path.LastIndexOf('\\') - 1);
            byte[] msg = Encoding.UTF8.GetBytes(1+"\n"+"file_name@"+file_name+"@"+fileInfo.Length+"\n");
            Console.WriteLine(path);
            client_socket.Send(msg);
            socket.Path = path;
            socket.file_name = file_name;
            //if (recive().Contains("ok_rec_file"))
            //{
            //     client_socket.SendFile(path);
            //    Console.WriteLine("your file already send");
            //}
        }
        public void close()
        {
            this.client_socket.Close();
        }
        public void rec_file(Socket socket,String name,String length)
        {
            Console.WriteLine(name);
            //Console.WriteLine(size);
            //写入接受文件的部分
            int len = 0;
            int increase = 0;
            byte[] save = new byte[int.Parse(length)];
            if (File.Exists("D://Easy/" + name))
            {
                is_duplicate ++;
                for(int i = 0; i < 20; i++)
                {
                    if(File.Exists("D://Easy/" + name.Substring(0, name.IndexOf(".")) + "("+is_duplicate+")" + name.Substring(name.IndexOf("."))))
                    {
                        is_duplicate++;
                    }
                    else
                    {
                        break;
                    }
                }
                //File.Create("D://Easy/" + name);
                //Console.WriteLine("3");
            }

            if (is_duplicate == 0)
            {
                FileStream stream = new FileStream("D://Easy/" + name, FileMode.Append);
                while ((len += (increase = socket.Receive(save))) <= save.Length)
                {

                    stream.Write(save, 0, increase);
                    Console.WriteLine("once");
                    if (len >= save.Length)
                        break;
                }
                //socket.Receive(save);

                //stream.Write(save, 0, save.Length);
                Console.WriteLine("write done");
                stream.Close();
                new Form2("文件 " + name + " 已接收~").ShowDialog();
            }
            else
            {
                FileStream stream = new FileStream("D://Easy/" + name.Substring(0,name.IndexOf("."))+"("+is_duplicate+")"+name.Substring(name.IndexOf(".")), FileMode.Append);
                while ((len += (increase = socket.Receive(save))) <= save.Length)
                {

                    stream.Write(save, 0, increase);
                    Console.WriteLine("once");
                    if (len >= save.Length)
                        break;
                }
                //socket.Receive(save);

                //stream.Write(save, 0, save.Length);
                Console.WriteLine("write done");
                stream.Close();
                new Form2("文件 " + name + " 已接收~").ShowDialog();
                new Form2("文件更名为  "+ name.Substring(0, name.IndexOf(".")) + "("+is_duplicate+")" + name.Substring(name.IndexOf("."))).ShowDialog();
                is_duplicate = 0;
            }

            Console.WriteLine("4");


         
        }
    }
}
