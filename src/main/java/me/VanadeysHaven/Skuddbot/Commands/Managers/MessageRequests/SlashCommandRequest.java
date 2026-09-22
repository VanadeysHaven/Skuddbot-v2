package me.VanadeysHaven.Skuddbot.Commands.Managers.MessageRequests;

import lombok.Getter;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SlashCommandRequest implements CommandRequest {

    @Getter private final SlashCommandInteractionEvent interactionEvent;

    public SlashCommandRequest(SlashCommandInteractionEvent interactionEvent){
        this.interactionEvent = interactionEvent;
    }

    @Override
    public String getContent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getContent'");
    }

    @Override
    public User getSender() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSender'");
    }

    @Override
    public Member getMember() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMember'");
    }

    @Override
    public SkuddUser getProfile() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProfile'");
    }

    @Override
    public MessageChannel getChannel() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getChannel'");
    }

    @Override
    public Guild getGuild() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getGuild'");
    }

    @Override
    public User getUser() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }

    @Override
    public String[] getArgs() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getArgs'");
    }

    @Override
    public void replyError(String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'replyError'");
    }

    @Override
    public void reply(Emoji emoji, String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'reply'");
    }

    @Override
    public void reply(String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'reply'");
    }

    @Override
    public void addReaction(Emoji emoji, String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addReaction'");
    }

    @Override 
    public MessageCommandRequest asMessageRequest(){
        throw new IllegalStateException("MessageCommandRequest is not available on slash commands.");
    }

    @Override
    public SlashCommandRequest asSlashRequest(){
        return this;
    }

}
