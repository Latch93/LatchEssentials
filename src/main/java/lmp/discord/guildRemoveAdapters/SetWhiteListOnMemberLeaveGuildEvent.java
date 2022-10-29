package lmp.discord.guildRemoveAdapters;

import lmp.api.Api;
import lmp.constants.YmlFileNames;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class SetWhiteListOnMemberLeaveGuildEvent extends ListenerAdapter {

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        String minecraftId = Api.getMinecraftIdFromDCid(Objects.requireNonNull(event.getMember()).getUser().getId());
        FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
        if (!minecraftId.isEmpty() && whitelistCfg.isSet(lmp.Constants.YML_PLAYERS + minecraftId + ".isPlayerInDiscord")) {
            whitelistCfg.set(lmp.Constants.YML_PLAYERS + minecraftId + ".isPlayerInDiscord", false);
            try {
                whitelistCfg.save(Api.getConfigFile(YmlFileNames.YML_WHITELIST_FILE_NAME));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
