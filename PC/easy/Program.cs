using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading;
using System.Windows.Forms;

namespace easy
{
    static class Program
    {
        public static Form1 f = new Form1();

        /// <summary>
        /// 应用程序的主入口点。
        /// </summary>
        [STAThread]
        static void Main()
        {
           
             if (!Directory.Exists("D://Easy/"))
            {
                try
                {
                    Directory.CreateDirectory("D://Easy/");
                }
                catch (System.IO.IOException)
                {
                    File.Delete("D://Easy");
                    Directory.CreateDirectory("D://Easy/");
                }
             }
            Application.EnableVisualStyles();
            //Application.SetCompatibleTextRenderingDefault(false);
          
            Application.Run(f);
           

      
        }
    }
}
