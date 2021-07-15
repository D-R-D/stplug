package stplug.stplug;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
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
                sinsreceiver();
            }
        });
        r_thread.start();
        eventsender("mcp_h:started");
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
            p.sendMessage(ChatColor.RED + "sys_Message : サーバーはアップデート中です。5分後に再度ログインして下さい。");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {

        if (command.getName().equalsIgnoreCase("container") && !bool) {
            if (args.length == 0) {
                sender.sendMessage("サブコマンドを入力してください。");
                return false;
            } else {
                if (args[0].equalsIgnoreCase("start")) {
                    if (args[1] == null) {
                        sender.sendMessage("コンテナが指定されていません。");
                        return false;
                    } else {
                        String cmd = "container:start:" + args[1];
                        sinssender(cmd);
                        eventsender(args[1] +":starting");
                    }
                } else if (args[0].equalsIgnoreCase("stop")) {
                    if (args[1] == null) {
                        sender.sendMessage("コンテナが指定されていません。");
                        return false;
                    } else {
                        String cmd = "container:stop:" + args[1];
                        sinssender(cmd);
                        eventsender(args[1] +":stopping");
                    }
                } else {
                    sender.sendMessage("サブコマンドが違います。");
                    return false;
                }
            }
        }
        return true;
    }

    public void sinssender(String str)
    {
        //strをbyte配列に変換
        byte[] data;
        data = str.getBytes(StandardCharsets.UTF_8);
        //接続用のソケットを作成
        DatagramSocket sock = null;
        try { sock = new DatagramSocket(); }
        catch (SocketException e) { e.printStackTrace();}
        //パケットを作成し、udpで送信する。
        DatagramPacket packet = new DatagramPacket(data,data.length,new InetSocketAddress("127.0.0.1",6011));
        try { sock.send(packet); }
        catch (IOException e) { e.printStackTrace(); }
        //最後にお片付けして終了
        sock.close();

        getLogger().info("udp sended");
    }

    public void eventsender(String str)
    {
        //strをbyte配列に変換
        byte[] data;
        data = str.getBytes(StandardCharsets.UTF_8);
        //接続用のソケットを作成
        DatagramSocket sock = null;
        try { sock = new DatagramSocket(); }
        catch (SocketException e) { e.printStackTrace();}
        //パケットを作成し、udpで送信する。
        DatagramPacket packet = new DatagramPacket(data,data.length,new InetSocketAddress("127.0.0.1",7011));
        try { sock.send(packet); }
        catch (IOException e) { e.printStackTrace(); }
        //最後にお片付けして終了
        sock.close();

        getLogger().info("event sended");
    }

    public  void sinsreceiver()
    {
        //データ受信
        byte[] data = new byte[1024];
        DatagramSocket sock = null;
        try {
            sock = new DatagramSocket(7001);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        DatagramPacket packet = new DatagramPacket(data, data.length);

        while (true) {
            try {
                sock.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String str = null;
            try {
                str = new String(Arrays.copyOf(packet.getData(), packet.getLength()), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            getLogger().info(str);

            //データごとに処理
            if(str.equals("reboot"))
            {
                bool = true;
            }
        }
    }
}
