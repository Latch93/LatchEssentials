package lmp.constants;

import lmp.api.Api;

public class Constants {

    public static final String DISCORD_BOT_TOKEN = Api.loadConfig(YmlFileNames.YML_CONFIG_FILE_NAME).getString("botToken");
    public static final String PLUGIN_NAME = "LatchEssentials";
    public static final String SERVER_OWNER_MINECRAFT_NAME = "latch93";
    public static final String SERVER_OWNER_MINECRAFT_ID = "f4c77e52-de47-4174-8282-0d962d089301";

}
