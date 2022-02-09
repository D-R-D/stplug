package stplug.stplug;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class Stplug extends JavaPlugin implements Listener
{
    boolean bool = false;
    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this,this);
        Thread r_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    sinsreceiver();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        r_thread.start();

        try
        {
            eventsender("mcp_h:started");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent e)
    {
        if(bool) {
            Player p = e.getPlayer();
            p.sendMessage(ChatColor.RED + "sys_Message : サーバーはアップデート中です。10分後に再度ログインして下さい。");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {

        if (command.getName().equalsIgnoreCase("container") && !bool) {
            if (args.length == 0)
            {
                sender.sendMessage("サブコマンドを入力してください。");
                return false;
            } else {
                if (args[0].equalsIgnoreCase("start"))
                {
                    if (args[1] == null)
                    {
                        sender.sendMessage("コンテナが指定されていません。");
                        return false;
                    } else
                    {
                        String cmd = "container:start:" + args[1];

                        Thread r_thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try
                                {
                                    sinssender(cmd,args[1],"starting");
                                } catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        });
                        r_thread.start();


                    }
                } else if (args[0].equalsIgnoreCase("stop")) {
                    if (args[1] == null) {
                        sender.sendMessage("コンテナが指定されていません。");
                        return false;
                    } else {
                        String cmd = "container:stop:" + args[1];


                        Thread r_thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try
                                {
                                    sinssender(cmd,args[1],"stopping");
                                } catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        });
                        r_thread.start();


                    }
                } else {
                    sender.sendMessage("サブコマンドが違います。");
                    return false;
                }
            }
        }
        return true;
    }

    public void sinssender(String str,String sss ,String ssss) throws IOException {
        //strをbyte配列に変換
        byte[] data;
        data = str.getBytes(StandardCharsets.UTF_8);
        //接続用のソケットを作成
        DatagramSocket sock = null;
        sock = new DatagramSocket();
        //パケットを作成し、udpで送信する。
        DatagramPacket packet = new DatagramPacket(data,data.length,new InetSocketAddress("127.0.0.1",6011));
        sock.send(packet);
        //最後にお片付けして終了
        sock.close();

        getLogger().info("udp sent");

        eventsender( sss + ":" + ssss);
    }

    public void eventsender(String str) throws IOException {
        //strをbyte配列に変換
        byte[] data;
        data = str.getBytes(StandardCharsets.UTF_8);
        //接続用のソケットを作成
        DatagramSocket sock = null;
        sock = new DatagramSocket();
        //パケットを作成し、udpで送信する。
        DatagramPacket packet = new DatagramPacket(data,data.length,new InetSocketAddress("127.0.0.1",7011));
        sock.send(packet);
        //最後にお片付けして終了
        sock.close();

        getLogger().info("event sent");
    }

    public  void sinsreceiver() throws IOException {
        //データ受信
        byte[] data = new byte[1024];
        DatagramSocket sock = new DatagramSocket(7001);
        DatagramPacket packet = new DatagramPacket(data, data.length);

        while (true) {

            sock.receive(packet);

            String str = new String(Arrays.copyOf(packet.getData(), packet.getLength()), "UTF-8");
            getLogger().info(str);

            //データごとに処理
            if(str.equals("reboot"))
            {
                bool = true;
            }
            else
            {
                //saboru
            }
        }
    }

    public void Udssender(String str) throws IOException {
        UnixDomainSocketAddress socketAddress = UnixDomainSocketAddress.of("/uds/mcp_h/plg_mng");
        ServerSocketChannel uds = ServerSocketChannel.open(StandardProtocolFamily.UNIX);
        uds.bind(socketAddress);
    }
}
