package lmp.listeners.playerMoveEvents;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DenyMoveForUnlinkedPlayerEvent implements Listener {
    public DenyMoveForUnlinkedPlayerEvent(Main plugin) {plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent e) {
        FileConfiguration enabledEventsCfg = Api.getFileConfiguration(YmlFileNames.YML_ENABLED_EVENTS_FILE_NAME);
        if (enabledEventsCfg.getBoolean("disableMovementAndSendMessageToUnlinkedPlayer")) {
            Set<String> groups = Objects.requireNonNull(Main.getLuckPerms().getUserManager().getUser(e.getPlayer().getUniqueId())).getNodes().stream()
                    .filter(NodeType.INHERITANCE::matches)
                    .map(NodeType.INHERITANCE::cast)
                    .map(InheritanceNode::getGroupName)
                    .collect(Collectors.toSet());
            if (groups.contains("default")){
                e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Go to Discord and type " + ChatColor.AQUA + "!link" + ChatColor.RED + " in the General channel."));
                e.setCancelled(true);
            }
        }
    }}
