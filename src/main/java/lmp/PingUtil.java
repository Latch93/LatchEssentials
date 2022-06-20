package lmp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PingUtil {
    public static int getPing(Player p) {
        String v = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        if (!p.getClass().getName().equals("org.bukkit.craftbukkit." + v + ".entity.CraftPlayer")) {
            p = Bukkit.getPlayer(p.getUniqueId());
        }
        try {
            int ping = p.getPing();
            return ping;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
