package discord;
import com.cavariux.twitchirc.Chat.Channel;
import com.cavariux.twitchirc.Chat.User;
import com.cavariux.twitchirc.Core.TwitchBot;

public class LatchTwitchBot extends TwitchBot {

    public LatchTwitchBot () {
        this.setUsername("Latch93"); //this.setUsername("CavsBot");
        this.setOauth_Key("oauth:q26q7b1b28smxq7amnhwhl8r7x2mz5"); //this.setOauth_Key("oauth:9kvd020oj3wgcrsyafoaofrt3uv7bi");
        this.setClientID("okinq7ngael2xbugi3mm0yqssogu35");
    }

    @Override
    public void onMessage(User user, Channel channel, String message)
    {
        System.out.println("testing message from Twitch: " + message);
    }
}