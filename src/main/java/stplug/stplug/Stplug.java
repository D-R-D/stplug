package stplug.stplug;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public final class Stplug extends JavaPlugin implements Listener
{

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this,this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {

        if (command.getName().equalsIgnoreCase("container")) {
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
                    }
                } else if (args[0].equalsIgnoreCase("stop")) {
                    if (args[1] == null) {
                        sender.sendMessage("コンテナが指定されていません。");
                        return false;
                    } else {
                        String cmd = "container:stop:" + args[1];
                        sinssender(cmd);
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
}
