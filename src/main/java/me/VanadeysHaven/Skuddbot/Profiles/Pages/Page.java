package me.VanadeysHaven.Skuddbot.Profiles.Pages;

import lombok.Getter;
import lombok.Setter;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Profiles.Server.SkuddServer;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
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
public abstract class Page<T extends Pageable<C>, C extends PageableCategory<T>> {

    private static final Logger logger = LoggerFactory.getLogger(Page.class);

    /**
     * The maximum amount of items per page.
     */
    private static final int MAX_SIZE = PageManager.MAX_SIZE;

    /**
     * The page number.
     */
    @Getter @Setter private int pageNumber;
    /**
     * The items in the page.
     */
    @Getter private List<T> pageItems;
    /**
     * The page manager that manages this page.
     */
    private final PageManager<T,C> pageManager;

    /**
     * Constructor for Page.
     * @param pageNumber The page number.
     * @param pageItems The items in the page.
     * @param pageManager The page manager.
     */
    public Page(int pageNumber, List<T> pageItems, PageManager<T,C> pageManager) {
        this.pageNumber = pageNumber;
        this.pageItems = pageItems;
        this.pageManager = pageManager;
    }

    /**
     * Generates the embed for the page.
     *
     * @param user The user to generate the embed for. Can be null if the page is not user-specific.
     * @param server The server to generate the embed for.
     * @return The embed for the page.
     */
    public EmbedBuilder generatePage(SkuddUser user, SkuddServer server){
        EmbedBuilder eb = new EmbedBuilder(); //Create a new embed builder.

        String[] title = getPageTitle() //Get the title of the page.
                .replace("$user", getUserName(user)) //Replace $user with the user's display name.
                .replace("$server", getServerName(server)) //Replace $server with the server's name.
                .split("\n"); //Split the title into multiple lines.
        eb.setAuthor(title[0], null, getPageAuthorImage(user, server)); //Set the author of the embed with the first line of the title and the author image.
        if(title.length > 1) //If there are more lines in the title...
            eb.setTitle(title[1]); //Set the title of the embed to the second line of the title.

        C category = null; //Initialize category variable.
        for(T item : pageItems) { //Iterate over all items in the page.
            if(item.getCategory() != category){ //If the category of the item is different from the category of the last item.
                category = item.getCategory(); //Set the category to the category of the item.
                eb.addField("\u200B", category.getName() + ": ", false); //Create new category header
            }

            String data = getData(item, user, server); //Get the data for the item.
            if(data == null)
                continue; //If the data is null, skip current item.

            eb.addField("__" + item.getName() + ":__", data, true); //Add the item to the embed.
        }

        addFooter(eb); //Add the footer to the embed.

        return eb; //Return the embed.
    }

    /**
     * Adds the footer to the embed.
     *
     * @param eb The embed builder that  the footer will be added to.
     */
    private void addFooter(EmbedBuilder eb){
        eb.addField("\u200B", "\u200B", false); //Add a blank field
        eb.addField("__Page:__", pageNumber + "/" + pageManager.getPageAmount(), true); //add the page indicator
        eb.addField("__Navigate between pages:__", Emoji.ARROW_LEFT.getUnicode() + " and " + Emoji.ARROW_RIGHT.getUnicode(), true); //add the navigation instructions
        eb.addField("__Refresh current page:__", Emoji.ARROWS_CC.getUnicode(), true); //add the refresh instructions
    }

    /**
     * Merges the page with the given page.
     *
     * @param page The page to merge with.
     */
    public void mergeWith(Page<T, C> page){
        if(!canMerge(page)) //If the pages can't be merged, throw an exception.
            throw new IllegalStateException("These pages can't be merged.");

        List<T> list = new ArrayList<>(pageItems); //Create a new list of items.
        list = mergeLists(list, page.getPageItems()); //Merge the lists.

        pageItems = list; //Set the page items to the merged list.
    }


    /**
     * Trims the current page to the max size, and returns a new page with the remainder.
     *
     * @return A new page with the remainder.
     * @throws IllegalStateException If the current page doesn't need to be trimmed.
     */
    public Page<T,C> trimPage(){
        if(!exceedsMaxLength()) //check if the current page needs to be trimmed and throw an exception if it doesn't
            throw new IllegalStateException("This page doesn't exceed the max length.");

        List<T> currentPage = new ArrayList<>(pageItems); //create a new list of the current page
        List<T> newPage = currentPage.subList(MAX_SIZE, currentPage.size()); //create a new list of the page after the max length
        currentPage = currentPage.subList(0, MAX_SIZE); //trim the current page to the max length

        pageItems = currentPage; //set the current page to the trimmed page

        return pageManager.constructNewPage(pageNumber + 1, newPage, pageManager); //construct and return the child page
    }

    /**
     * Checks if a given page can merge with the current page.
     *
     * @param page The page to check.
     * @return True if the pages can be merged, false otherwise.
     */
    public boolean canMerge(Page<T, C> page){
        return !exceedsMaxLength(mergeLists(getPageItems(), page.getPageItems())); //return whether the merged list exceeds the max length
    }

    /**
     * Merges two lists together.
     *
     * @param list1 The first list.
     * @param list2 The second list.
     * @return The merged list.
     */
    private List<T> mergeLists(List<T> list1, List<T> list2){
        List<T> list = new ArrayList<>(list1); //create a new list with items from the first list
        list.addAll(list2); //add all items from the second list

        return list; //return the merged list
    }

    /**
     * Checks if the current page exceeds the max length.
     *
     * @return True if the current page exceeds the max length, false otherwise.
     */
    public boolean exceedsMaxLength(){
        return exceedsMaxLength(pageItems); //return whether the current page exceeds the max length
    }

    /**
     * Checks if a given list exceeds the max length.
     *
     * @param list The list to check.
     * @return True if the list exceeds the max length, false otherwise.
     */
    private boolean exceedsMaxLength(List<T> list) {
        return getElementCount(list) > MAX_SIZE; //return whether the list exceeds the max length
    }

    /**
     * Gets the amount of elements in the current page.
     *
     * @return The amount of elements in the current page.
     */
    private int getElementCount(){
        return getElementCount(pageItems); //return the amount of elements in the current page
    }

    /**
     * Gets the amount of elements in a given list.
     *
     * @param list The list to count.
     * @return The amount of elements in the list.
     */
    private int getElementCount(List<T> list){
        return getCategoryCount(list) + list.size();
    }

    /**
     * Gets the amount of categories in the current page.
     * @return The amount of categories in the current page.
     */
    private int getCategoryCount(){
        return getCategoryCount(pageItems); //return the amount of categories in the current page
    }

    /**
     * Gets the amount of categories in a given list.
     *
     * @param list The list to count.
     * @return The amount of categories in the list.
     */
    private int getCategoryCount(List<T> list){
        List<C> counted = new ArrayList<>(); //create a new list of counted categories
        for(T item : list) //iterate over all items in the list
            if(!counted.contains(item.getCategory()))
                counted.add(item.getCategory()); //add the category to the list if it isn't already in the list

        return counted.size(); //return the amount of categories in the list
    }

    /**
     * Method for getting the page title.
     *
     * @return The page title.
     */
    public abstract String getPageTitle();

    /**
     * Method for getting the author image url for the embed.
     *
     * @param user The user to get the author image url for. Can be null if the page isn't user specific.
     * @param server The server to get the author image url for.
     * @return The author image url for the embed.
     */
    public abstract String getPageAuthorImage(SkuddUser user, SkuddServer server);

    /**
     * Method for getting the data associated with an item.
     *
     * @param item The item to get the data for.
     * @param user The user to get the data for. Can be null if the page isn't user specific.
     * @param server The server that the page is for.
     * @return The data associated with the item.
     */
    public abstract String getData(T item, SkuddUser user, SkuddServer server);

    /**
     * Method for getting the server name for a specified server.
     *
     * @param server The server to get the name for.
     * @return The server for the specified server.
     */
    public String getServerName(SkuddServer server){
        return server.getName(); //return the server name
    }

    /**
     * Method for getting the username for a specified user.
     *
     * @param user The user to get the name for. Can be null if the page isn't user specific.
     * @return The username for the specified user.
     */
    public String getUserName(SkuddUser user) {
        if(user == null)
            return "Unknown"; //return "Unknown" if the user is null

        return user.asMember().getDisplayName(); //return the username
    }

}
