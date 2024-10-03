package me.VanadeysHaven.Skuddbot.Utilities;

import lombok.Getter;
import me.VanadeysHaven.Skuddbot.Donator.DonatorManager;
import me.VanadeysHaven.Skuddbot.Donator.DonatorMessage;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Profiles.GlobalSettings.GlobalSetting;
import me.VanadeysHaven.Skuddbot.Profiles.GlobalSettings.GlobalSettingsContainer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Managing the playing status and avatar.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.26
 * @since 2.0
 */
public class AppearanceManager {

    private static final DonatorManager dm = new DonatorManager();
    private static RNGManager random = new RNGManager();

    @Getter
    public enum Avatar {
        DEFAULT   ("https://i.imgur.com/v1vlVru.png"),
        CHRISTMAS ("https://i.imgur.com/fc0ORQx.png"),
        WIP       ("https://i.imgur.com/HTZy6Ve.png"),
        BIRTHDAY  ("https://i.imgur.com/9VAQN5L.png"),
        MEME      ("https://i.imgur.com/N2nffCL.png");

        String url;

        Avatar(String url) {
            this.url = url;
        }

        public static boolean exists(String str){
            for(Avatar avatar : values())
                if(avatar.toString().equals(str)) return true;

            return false;
        }
    }

    @Getter
    private enum CalendarEvent {

        NEW_YEAR              (1,  1, DonatorMessage.Type.PLAYING_NEW_YEAR                                              ),
        CHRISTMAS_HAT         (1, 12, 23, 12,  DonatorMessage.Type.PLAYING,            Avatar.CHRISTMAS),
        CHRISTMAS             (24, 12, 26, 12, DonatorMessage.Type.PLAYING_CHRISTMAS,  Avatar.CHRISTMAS);

        private int startDate;
        private int startMonth;
        private int endDate;
        private int endMonth;
        private String displayMessage;
        private DonatorMessage.Type messageType;
        private Avatar avatar;

        CalendarEvent(int startDate, int startMonth, String displayMessage, Avatar avatar){
            this.startDate = startDate;
            this.startMonth = startMonth;
            endDate = startDate;
            endMonth = startMonth;
            this.displayMessage = displayMessage;
            messageType = null;
            this.avatar = avatar;
        }

        CalendarEvent(int startDate, int startMonth, String displayMessage){
            this.startDate = startDate;
            this.startMonth = startMonth;
            endDate = startDate;
            endMonth = startMonth;
            this.displayMessage = displayMessage;
            messageType = null;
            this.avatar = Avatar.DEFAULT;
        }

        CalendarEvent(int startDate, int startMonth, DonatorMessage.Type messageType, Avatar avatar){
            this.startDate = startDate;
            this.startMonth = startMonth;
            endDate = startDate;
            endMonth = startMonth;
            displayMessage = null;
            this.messageType = messageType;
            this.avatar = avatar;
        }

        CalendarEvent(int startDate, int startMonth, DonatorMessage.Type messageType){
            this.startDate = startDate;
            this.startMonth = startMonth;
            endDate = startDate;
            endMonth = startMonth;
            displayMessage = null;
            this.messageType = messageType;
            avatar = Avatar.DEFAULT;
        }

        CalendarEvent(int startDate, int startMonth, int endDate, int endMonth, DonatorMessage.Type messageType, Avatar avatar){
            this.startDate = startDate;
            this.startMonth = startMonth;
            this.endDate = endDate;
            this.endMonth = endMonth;
            displayMessage = null;
            this.messageType = messageType;
            this.avatar = avatar;
        }

        public static ArrayList<CalendarEvent> gatherEventsForDay(int day, int month){
            ArrayList<CalendarEvent> list = new ArrayList<>();
            for(CalendarEvent event : values()){
                if(day >= event.getStartDate() && day <= event.getEndDate()
                && month >= event.getStartMonth() && month <= event.getEndMonth()) list.add(event);
            }

            return list;
        }

        public static CalendarEvent getRandomEventForDay(int day, int month){
            ArrayList<CalendarEvent> list = gatherEventsForDay(day, month);
            if(list.isEmpty()) return null;
            return list.get(random.integer(0, list.size() - 1));
        }

    }

    public void tickAppearance(){
        Date current = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(current);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;

        CalendarEvent event = CalendarEvent.getRandomEventForDay(day, month);
        if(event != null) {
            if(event.getDisplayMessage() != null) {
                Main.getSkuddbot().getApi().updateActivity(event.getDisplayMessage());
            } else {
                Main.getSkuddbot().getApi().updateActivity(dm.getMessage(event.getMessageType()).getMessage());
            }
            setAvatar(event.getAvatar());
        } else {
            Main.getSkuddbot().getApi().updateActivity(dm.getMessage(DonatorMessage.Type.PLAYING).getMessage());
            setAvatar(Avatar.DEFAULT);
        }
    }

    public void setAvatar(Avatar avatar){
        GlobalSettingsContainer settings = Main.getSkuddbot().getGlobalSettings();
        if(settings.getCurrentAvatar() == avatar) return;

        settings.setCurrentAvatar(avatar);
        try {
            Main.getSkuddbot().getApi().updateAvatar(new URL(avatar.getUrl()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void appearanceStartup(){
        GlobalSettingsContainer gsc = Main.getSkuddbot().getGlobalSettings();
        Main.getSkuddbot().getApi().updateActivity(gsc.getString(GlobalSetting.VERSION) + " | " + gsc.getString(GlobalSetting.BRANCH) + " > " + gsc.getString(GlobalSetting.COMMIT));
    }

}

