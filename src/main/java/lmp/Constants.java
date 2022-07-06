package lmp;

public class Constants {

    public static final String DISCORD_BOT_TOKEN = Api.loadConfig(Constants.YML_CONFIG_FILE_NAME).getString("botToken");
    public static final String PLUGIN_NAME = "DiscordText";
    public static final String SERVER_OWNER_NAME = "latch93";

    // Whitelist channel messages
    public static final String USERNAME_DOES_NOT_EXIST_MESSAGE = "Username does not exist. Please try again. If you are on Bedrock, message Latch in the general channel.";
    public static final String ADDED_TO_WHITELIST_MESSAGE = "You were added to the whitelist. Happy Mining!!!";
    public static final String USER_EXISTS_ON_WHITELIST_MESSAGE = "You are already added to the whitelist. :smile:";

    // On Join and Leave messages
    public static final String DISCORD_USERNAME_LABEL = "Discord Username: ";
    public static final String MINECRAFT_USERNAME_LABEL = "\nMinecraft Username: ";

    // Discord Commands
    public static final String CLEAR_COMMAND = "!clear";
    public static final String STAFF_APPLY_COMMAND = "!apply";
    public static final String CLEAR_ALL_COMMAND = "!pineapple";
    public static final String CLEAR_ALL_USER_MESSAGES_COMMAND = "!boof";
    public static final String SEARCH_PLAYER_SHOP_COMMAND = "!search";
    public static final String SET_WHITELIST_COMMAND = "!setwhitelist";
    public static final String SEARCH_USER_COMMAND = "!searchplayer";
    public static final String UNBAN_REQUEST = "!unbanRequest";
    public static final String TOGGLE_WHITELIST_COMMAND = "!whitelist";
    public static final String LTS_NOMINATION_COMMAND = "!nominate";
    public static final String ONLINE_COMMAND = "!online";
    public static final String STAT_COMMAND = "!stat";

    // Channel and User IDs
    public static final String SERVER_OWNER_ID = "460463941542215691";
    public static final String ANNOUNCEMENT_CHANNEL_ID = "963616792562008076";
    public static final String STAFF_APP_SUBMITTED_CHANNEL_ID = "951562494344847390";
    public static final String RULES_CHANNEL_MESSAGE_ID = "950601046575706143";
    public static final String RULES_CHANNEL_ID = "625996424554872842";
    public static final String TEST_CHANNEL_ID = "950920437976694795";
    public static final String MEMBER_ROLE_ID = "628708160479166485";
    public static final String STAFF_APPLICATION_CHANNEL_ID = "635277380511858699";
    public static final String MINECRAFT_CHAT_CHANNEL_ID = "627209350888554542";
    public static final String LATCH93BOT_USER_ID = "950478875488579655";
    public static final String SEARCH_CHANNEL_ID = "976590390788493363";
    public static final String BAN_LOG_CHANNEL_ID = "969436426234114119";
    public static final String DISCORD_CONSOLE_CHANNEL_ID = "627210135877713970";
    public static final String RANDOM_ITEM_LOG_CHANNEL_ID = "955870340670689340";
    public static final String GUILD_ID = "625983914049142786";
    public static final String UNBAN_REQUEST_CHANNEL_ID = "963008161055391814";
    public static final String UNBAN_REQUEST_COMPLETE_CHANNEL_ID = "966062192157917284";
    public static final String DISCORD_STAFF_CHAT_CHANNEL_ID = "636400726070919180";
    public static final String LTS_NOMINEE_CHANNEL_ID = "632449054709776384";
    public static final String GET_ROLE_CHANNEL_ID = "976307022557483058";
    public static final String MOBILE_ROLE_ID = "976308545991630968";
    public static final String PLAYSTATION_ROLE_ID = "976308441528270888";
    public static final String XBOX_ROLE_ID = "976308156189794375";
    public static final String JAVA_ROLE_ID = "976309331177902161";
    public static final String MOBILE_MESSAGE_ID = "976310931481034772";
    public static final String PLAYSTATION_MESSAGE_ID = "976310469633638420";
    public static final String XBOX_MESSAGE_ID = "976310645198827532";
    public static final String JAVA_MESSAGE_ID = "976311072334155797";
    public static final String GENERAL_CHANNEL_ID = "625983914049142790";

    // Backpack commands
    public static final String BACKPACK_BUY_COMMAND = "buy";
    public static final String BACKPACK_UPGRADE_COMMAND = "upgrade";

    // Generic commands
    public static final String OPEN_COMMAND = "open";

    // Player Shop commands
    public static final String SET_WORTH_COMMAND = "setworth";
    public static final String MY_SHOP_COMMAND = "myshop";

    // Twitch Bot commands
    public static final String START_BOT_COMMAND = "start";
    public static final String STOP_BOT_COMMAND = "stop";
    public static final String ADD_BOT_COMMAND = "addBot";
    public static final String HELP_COMMAND = "help";
    public static final String SEND_TWITCH_MESSAGE_COMMAND = "sendMessage";
    public static final String ADD_CHANNEL_ID = "addChannelId";

    // DiscordStaffChat commands
    public static final String DISCORD_STAFF_CHAT_COMMAND = "dtsc";


    // Backpack message strings
    public static final String YML_YOU_NEED_AT_LEAST = "You need at least ";
    public static final String YML_BACKPACK_LEVEL_IS_NOW = "Backpack level is now ";
    public static final String YML_OUT_OF = " out of";
    public static final String YML_BACKPACK_NOW_HOLDS = ". Backpack now holds ";
    public static final String YML_BACKPACK_LEVEL_COST_LEVEL_1 = "Backpack-Level-Cost.level-1";
    public static final String YML_BACKPACK_LEVEL_COST_LEVEL_2 = "Backpack-Level-Cost.level-2";
    public static final String YML_BACKPACK_LEVEL_COST_LEVEL_3 = "Backpack-Level-Cost.level-3";
    public static final String YML_BACKPACK_LEVEL_COST_LEVEL_4 = "Backpack-Level-Cost.level-4";
    public static final String YML_BACKPACK_LEVEL_COST_LEVEL_5 = "Backpack-Level-Cost.level-5";
    public static final String YML_BACKPACK_LEVEL_COST_LEVEL_6 = "Backpack-Level-Cost.level-6";
    public static final String YML_TO_UPGRADE_YOUR_BACKPACK = " to upgrade your backpack.";
    public static final String YML_YOUR_NEW_BALANCE_IS = "Your new balance is: ";
    public static final String YML_POSSESSIVE_BACKPACK = "'s Backpack";

    // Player Shops message strings
    public static final String YML_POSSESSIVE_PLAYER_SHOP ="'s Shop";

    // yml strings
    public static final String YML_PLAYERS = "players.";
    public static final String YML_ENCHANTS =".enchants.";
    public static final String YML_SIZE = ".size";
    public static final String YML_ITEMS = "items.";
    public static final String YML_SLOTS = ".slots.";
    public static final String YML_ROLES = "roles.";
    public static final String YML_DONATIONS = "donations.";
    public static final String YML_MEMBERS = "members.";
    // Config File Names
    public static final String YML_BANK_FILE_NAME = "bank";
    public static final String YML_AUTO_MINER_FILE_NAME = "autoMiner";
    public static final String YML_WHITELIST_FILE_NAME = "whitelist";
    public static final String YML_PLAYER_SHOP_FILE_NAME = "playerShops";
    public static final String YML_BACK_PACK_FILE_NAME = "playerBackpack";
    public static final String YML_CONFIG_FILE_NAME = "config";
    public static final String YML_ADVANCEMENT_FILE_NAME = "advancement";
    public static final String YML_LOTTERY_FILE_NAME = "lottery";
    public static final String YML_TWITCH_FILE_NAME = "twitch";
    public static final String YML_AUTO_SORTER_FILE_NAME = "autoSorter";
    public static final String YML_BOSS_FILE_NAME = "boss";
    public static final String YML_DISCORD_ROLES_FILE_NAME = "discordRoles";
    public static final String YML_BLOCK_BREAK_LOG_FILE_NAME = "blockBreakLog";
    public static final String YML_BLOCK_PLACE_LOG_FILE_NAME = "blockPlaceLog";
    public static final String YML_VILLAGER_HURT_LOG_FILE_NAME = "villagerHurtLog";
    public static final String YML_MONEY_ORDER_LOG_FILE_NAME = "moneyOrderLog";
    public static final String YML_DONATION_FILE_NAME = "donations";

    public static final String INVALID_PARAMETERS = "Incorrect parameters. Please try again";

    public static final String BUILDER_ROLE_ID = "955299752432533505";
    public static final String HELPER_ROLE_ID = "974694500800794624";
    public static final String JR_MOD_ROLE_ID = "971160639932362783";
    public static final String MOD_ROLE_ID = "636400791753850901";
    public static final String ADMIN_ROLE_ID = "627227187166314496";
    public static final String VIP_ROLE_ID = "630549454776303627";

    // Donation and Feature Purchase ID's
    public static final String DONOR_PRODUCT_ID = "SyiG6YMAa1";

}
