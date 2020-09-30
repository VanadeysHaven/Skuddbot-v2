package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Profiles.ServerMember;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies.Currency;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;
import me.Cooltimmetje.Skuddbot.Utilities.CooldownManager;
import me.Cooltimmetje.Skuddbot.Utilities.RNGManager;
import org.javacord.api.entity.channel.TextChannel;

import java.util.ArrayList;

/**
 * Manages games on a server level.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.0
 */
public class BlackjackGameManager {

    private static final int DECK_AMOUNT = 8;

    @Getter private long serverId;
    private CooldownManager cooldownManager;
    private ArrayList<BlackjackGame> games;
    @Getter private RNGManager rngManager;
    private CardStack cardStack;

    public BlackjackGameManager(long serverId){
        this.serverId = serverId;
        cooldownManager = new CooldownManager(60);
        games = new ArrayList<>();
        rngManager = new RNGManager();
        cardStack = new CardStack(DECK_AMOUNT, rngManager);
    }

    public boolean isOnCooldown(ServerMember member){
        return isOnCooldown(member.getId());
    }

    public boolean isOnCooldown(Identifier id){
        return isOnCooldown(id.getDiscordId());
    }

    public boolean isOnCooldown(long userId){
        return cooldownManager.isOnCooldown(userId);
    }

    public boolean hasGameActive(ServerMember member){
        return hasGameActive(member.getId());
    }

    public boolean hasGameActive(Identifier id){
        return hasGameActive(id.getDiscordId());
    }

    public boolean hasGameActive(long userId){
        for(BlackjackGame game : games)
            if(game.getPlayer().getId().getId() == userId)
                return true;

        return false;
    }

    public void startNewGame(ServerMember member, TextChannel channel, String handInstruction){
       BlackjackGame game = new BlackjackGame(member, 1, this, channel, handInstruction);
       games.add(game);
    }

    public void startNewGame(ServerMember member, int bet, TextChannel channel) {
        member.asSkuddUser().getCurrencies().incrementInt(Currency.SKUDDBUX, bet * -1);
        BlackjackGame game = new BlackjackGame(member, bet, this, channel);
        games.add(game);
    }

    public void wrapUp(ServerMember member){
        wrapUp(member.getId());
    }

    public void wrapUp(Identifier id){
        wrapUp(id.getDiscordId());
    }

    public void wrapUp(long userId){
        cooldownManager.startCooldown(userId);
        removeGame(userId);
    }

    private void removeGame(long userId){
        games.removeIf(game -> game.getPlayer().getId().getId() == userId);
    }


    public Card drawCard(){
        return cardStack.nextCard();
    }

}
