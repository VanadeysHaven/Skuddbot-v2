package me.VanadeysHaven.Skuddbot.Profiles.Pages;

import lombok.Getter;
import lombok.Setter;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a page for with pageable objects.
 *
 * @author Vanadey's Haven
 * @version 2.3.24
 * @since 2.3.24
 */
public abstract class Page<T extends Pageable<C>, C extends PageableCategory> {

    private static final Logger logger = LoggerFactory.getLogger(Page.class);

    private static final int MAX_SIZE = 21;

    @Getter @Setter private int pageNumber;
    @Getter private List<T> pageItems;
    private PageManager pageManager;

    public Page(int pageNumber, List<T> pageItems) {
        this.pageNumber = pageNumber;
        this.pageItems = pageItems;
    }

    public EmbedBuilder generatePage(){
        EmbedBuilder eb = new EmbedBuilder();

        String[] title = getPageTitle().split("\n");
        eb.setAuthor(title[0]);
        eb.setTitle(title[1]);

        C category = null;
        for(T item : pageItems) {
            if(item.getCategory() != category){
                category = item.getCategory();
                eb.addField("\u200B", category.getName() + ": ", false);
            }

            String data = getData(item);
            if(data == null)
                continue;

            eb.addField("__" + item.getName() + ":__", data, true);
        }

        eb.addField("\u200B", "\u200b", false);
        addFooter(eb);

        return eb;
    }

    private void addFooter(EmbedBuilder eb){
        eb.addField("\u200B", "\u200B", false);
        eb.addField("__Page:__", pageNumber + "/" + pageManager.getPageAmount(), true);
        eb.addField("__Navigate between pages:__", Emoji.ARROW_LEFT.getUnicode() + " and " + Emoji.ARROW_RIGHT.getUnicode(), true);
        eb.addField("__Refresh current page:__", Emoji.ARROWS_CC.getUnicode(), true);
    }

    public void mergeWith(Page<T, C> page){
        if(!canMerge(page)) throw new IllegalStateException("These pages can't be merged.");

        List<T> list = new ArrayList<>(pageItems);
        list = mergeLists(list, page.getPageItems());

        pageItems = list;
    }

    public Page trimPage(){
        if(!exceedsMaxLength()) throw new IllegalStateException("This page doesn't exceed the max length.");

        List<T> currentPage = new ArrayList<>(pageItems);
        List<T> newPage = currentPage.subList(MAX_SIZE, currentPage.size());
        currentPage = currentPage.subList(0, MAX_SIZE);

        pageItems = currentPage;

        return new Page(-1, newPage); //TODO: FIND SOLUTION
    }

    public boolean canMerge(Page<T, C> page){
        return exceedsMaxLength(mergeLists(getPageItems(), page.getPageItems()));
    }

    private List<T> mergeLists(List<T> list1, List<T> list2){
        List<T> list = new ArrayList<>(list1);
        list.addAll(list2);

        return list;
    }

    private boolean exceedsMaxLength(){
        return exceedsMaxLength(pageItems);
    }

    private boolean exceedsMaxLength(List<T> list) {
        return getElementCount(list) > MAX_SIZE;
    }

    private int getElementCount(){
        return getElementCount(pageItems);
    }

    private int getElementCount(List<T> list){
        return getCategoryCount(list) + list.size();
    }

    private int getCategoryCount(){
        return getCategoryCount(pageItems);
    }

    private int getCategoryCount(List<T> list){
        List<C> counted = new ArrayList<>();
        for(T item : list)
            if(!counted.contains(item.getCategory()))
                counted.add(item.getCategory());

        return counted.size();
    }

    public abstract String getPageTitle();

    public abstract String getData(T item);

}
