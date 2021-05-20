package me.VanadeysHaven.Skuddbot.Profiles.Users.Stats;

import lombok.Getter;
import lombok.Setter;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class for managing stat pages.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.2.1
 * @since 2.2.1
 */
public class StatPageManager {

    private static final Logger logger = LoggerFactory.getLogger(StatPageManager.class);
    private static StatPageManager instance;

    public static StatPageManager getInstance(){
        if(instance == null)
            instance = new StatPageManager();

        return instance;
    }

    private ArrayList<StatPage> pages;

    public void calculate(){
        pages = new PageCalculator().calculatePages();
        printPages();
    }

    public void printPages(){
        logger.info("Calculated Stat Pages: ");
        for(StatPage page : pages)
            logger.info(page.toString());
    }

    public StatPage getPage(int pageNumber){
        if(pageNumber <= 0 || pageNumber > getPageAmount()) throw new IllegalArgumentException("Page " + pageNumber + " does not exist.");

        return pages.get(pageNumber - 1);
    }

    public int getPageAmount(){
        return pages.size();
    }

    private class PageCalculator {

        private ArrayList<StatPage> tempPages;

        public PageCalculator() {
            tempPages = new ArrayList<>();
        }

        public ArrayList<StatPage> calculatePages() {
            int pageNumber = 1;
            ArrayList<StatPage> tempPages = new ArrayList<>();

            StatPage curPage = null;
            for (Stat.Category category : Stat.Category.values()) {
                if(!category.isShow()) continue;
                ArrayList<Stat> stats = category.getAll();
                stats.removeIf(s -> !s.isShow());
                StatPage page = new StatPage(-1, stats);
                if(curPage == null){
                    curPage = page;
                    continue;
                }

                while (curPage.exceedsMaxLength()) {
                    StatPage newPage = curPage.trimPage();
                    curPage.setPageNumber(pageNumber);
                    pageNumber++;
                    curPage = newPage;
                }

                if (curPage.canMerge(page)) {
                    curPage.mergeWith(page);
                } else {
                    curPage.setPageNumber(pageNumber);
                    pageNumber++;
                    tempPages.add(curPage);
                    curPage = page;
                }
            }
            curPage.setPageNumber(pageNumber);
            tempPages.add(curPage);

            return tempPages;
        }
    }

    public class StatPage {

        private static final int MAX_ELEMENTS = 21;

        @Getter @Setter private int pageNumber;
        @Getter private ArrayList<Stat> stats;

        public StatPage(int pageNumber, ArrayList<Stat> stats) {
            this.pageNumber = pageNumber;
            this.stats = stats;
        }

        public EmbedBuilder generateOverview(SkuddUser su){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setAuthor("Stats for: " + su.asMember().getDisplayName(), null, su.asMember().getUser().getAvatar());
            eb.setTitle("__Server:__ " + su.asMember().getServer().getName());

            Stat.Category category = null;
            for(Stat stat : stats){
                if(category != stat.getCategory() && stat.getCategory() != Stat.Category.NO_CATEGORY) {
                    category = stat.getCategory();
                    eb.addField("\u200B", category.getName() + ": ", false);
                }

                if(stat == Stat.EXPERIENCE){
                    eb.addField("__" + stat.getName() + ":__", su.getStats().formatLevel(), true);
                } else {
                    eb.addField("__" + stat.getName() + ":__", su.getStats().getString(stat) + " " + stat.getSuffix(), true);
                }
            }

            eb.addField("\u200B", "\u200B", false);
            eb.addField("__Page:__", pageNumber + "/" + getPageAmount(), true);
            eb.addField("__Navigate between pages:__", Emoji.ARROW_LEFT.getUnicode() + " and " + Emoji.ARROW_RIGHT.getUnicode(), true);
            eb.addField("__Refresh current page:__", Emoji.ARROWS_CC.getUnicode(), true);

//            eb.setFooter("Page " + pageNumber + "/" + getPageAmount() + "\nUse the " + Emoji.ARROW_LEFT.getUnicode() + " and " + Emoji.ARROW_RIGHT.getUnicode() + " reactions to navigate between pages. " +
//                    "\nUse the " + Emoji.ARROWS_CC.getUnicode() + " reaction to refresh the current page.");

            return eb;
        }

        private void mergeWith(StatPage page){
            ArrayList<Stat> list1 = new ArrayList<>(stats);
            ArrayList<Stat> list2 = new ArrayList<>(page.getStats());
            list1.addAll(list2);

            if(exceedsMaxLength(list1)) throw new IllegalStateException("Page will become too big.");

            stats = list1;
        }

        private StatPage trimPage(){
            ArrayList<Stat> currentPage = new ArrayList<>(stats);
            ArrayList<Stat> newPage = new ArrayList<>();

            if(!exceedsMaxLength(currentPage)) throw new IllegalStateException("Current page does not need trimming.");

            int currentIndex = currentPage.size() - 1;
            while(getElementsCount(currentPage) - newPage.size() > MAX_ELEMENTS){
                Stat lastStat = currentPage.get(currentIndex);
                newPage.add(lastStat);
                currentIndex--;
            }
            Collections.reverse(newPage);
            currentPage.removeAll(newPage);

            stats = currentPage;

            return new StatPage(-1, newPage);
        }

        private boolean canMerge(StatPage page){
            ArrayList<Stat> list1 = new ArrayList<>(stats);
            ArrayList<Stat> list2 = new ArrayList<>(page.getStats());
            list1.addAll(list2);

            return !exceedsMaxLength(list1);
        }

        private boolean exceedsMaxLength(){
            return exceedsMaxLength(stats);
        }

        private boolean exceedsMaxLength(ArrayList<Stat> stats){
            return getElementsCount(stats) > MAX_ELEMENTS;
        }

        private int getElementsCount(){
            return getElementsCount(stats);
        }

        private int getElementsCount(ArrayList<Stat> stats){
            return stats.size() + getCategoryCount(stats);
        }

        private int getCategoryCount(){
            return getCategoryCount(stats);
        }

        private int getCategoryCount(ArrayList<Stat> stats){
            ArrayList<Stat.Category> counted = new ArrayList<>();
            for(Stat stat : stats)
                if(!counted.contains(stat.getCategory()) && stat.getCategory() != Stat.Category.NO_CATEGORY)
                    counted.add(stat.getCategory());

            return counted.size();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            Stat.Category lastCategory = null;
            for(Stat stat : stats) {
                if(lastCategory != stat.getCategory()){
                    lastCategory = stat.getCategory();
                    sb.append(" | ").append(stat.getCategory().getName().toUpperCase());
                }

                sb.append(" | ").append(stat.getName());
            }

            return pageNumber + ": " + sb.substring(3) + " (elements: " + getElementsCount() + ")";
        }
    }

}
