package me.Cooltimmetje.Skuddbot.Profiles;

import lombok.Getter;
import lombok.Setter;

public class SkuddId {

    @Getter private int id;
    @Getter private long serverId;
    @Getter @Setter private long userId;
    @Getter @Setter private String twitchUsername;

    public SkuddId(int id, long serverId){

    }
}
