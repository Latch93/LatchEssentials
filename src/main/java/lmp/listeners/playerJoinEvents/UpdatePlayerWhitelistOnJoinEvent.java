package lmp.listeners.playerJoinEvents;

import lmp.Constants;
import lmp.LatchDiscord;
import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.util.Objects;

public class UpdatePlayerWhitelistOnJoinEvent implements Listener {

    public UpdatePlayerWhitelistOnJoinEvent(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    private void updatePlayerWhitelistInformation(PlayerJoinEvent e) throws IOException {
        Player player = e.getPlayer();
        FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
        if (whitelistCfg.isSet("players." + player.getUniqueId())) {
            whitelistCfg.set(lmp.Constants.YML_PLAYERS + player.getUniqueId() + ".minecraftName", player.getName());
            String discordId = whitelistCfg.getString(lmp.Constants.YML_PLAYERS + player.getUniqueId() + ".discordId");
            assert discordId != null;
            if (Objects.requireNonNull(LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID)).getMemberById(discordId) != null) {
                whitelistCfg.set(lmp.Constants.YML_PLAYERS + player.getUniqueId() + ".discordName", Objects.requireNonNull(Objects.requireNonNull(LatchDiscord.getJDA().getGuildById(lmp.Constants.GUILD_ID)).getMemberById(discordId)).getUser().getName());
                whitelistCfg.set(lmp.Constants.YML_PLAYERS + player.getUniqueId() + ".isPlayerInDiscord", true);
                if (!player.getName().equalsIgnoreCase(whitelistCfg.getString(lmp.Constants.YML_PLAYERS + player.getUniqueId() + ".minecraftName"))) {
                    whitelistCfg.set(lmp.Constants.YML_PLAYERS + player.getUniqueId() + ".minecraftName", player.getName());
                }
            } else {
                whitelistCfg.set(lmp.Constants.YML_PLAYERS + player.getUniqueId() + ".isPlayerInDiscord", false);
                player.kickPlayer("You not in Latch's Discord. To get the discord link, type !discord in Latch's twitch chat -> twitch.tv/latch93minecraft");
            }
        } else {
            Api.removePlayerFromPermissionGroup(player.getUniqueId().toString(), "member");
            Api.addPlayerToPermissionGroup(player.getUniqueId().toString(), "default");
            player.sendMessage(ChatColor.RED + "An Error has occurred, please link your account by typing !link in General Discord channel. Then pasting your command Latch's bot dm's you.");
        }
        whitelistCfg.save(Api.getConfigFile(YmlFileNames.YML_WHITELIST_FILE_NAME));
    }

}
